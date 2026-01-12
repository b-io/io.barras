#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the Web-serving utilities.
#
# Notes
#   • The tests use a minimal ASGI harness (no live network) to validate middleware, handlers, and routers.
#   • The ASGI spec requires lowercase header keys as bytes for correct Starlette behavior (e.g., CORS preflight).
########################################################################################################################

from __future__ import annotations

import json
import logging
from typing import Any, Dict, Iterable, List, Mapping, Optional, Tuple

import pytest
from fastapi import FastAPI
from starlette.exceptions import HTTPException as StarletteHTTPException

from nserve.app import create_app, ServeAppOptions
from nserve.context import get_request_id, reset_request_id, set_request_id
from nserve.errors import ErrorCodes, install_exception_handlers
from nserve.http import get_client_host, get_header, headers_to_dict
from nserve.middleware import AccessLogMiddleware, RequestIdMiddleware
from nserve.uvicorn import run_uvicorn, UvicornOptions

__SERVE_TEST_CASES________________________________________________________________________ = ""


def test_http_headers_to_dict_lowercases_and_decodes() -> None:
    scope: Dict[str, Any] = {
        "headers": [
            (b"x-test", b"abc"),
            (b"user-agent", b"my-agent"),
        ]
    }
    out = headers_to_dict(scope)  # type: ignore[arg-type]
    assert out["x-test"] == "abc"
    assert out["user-agent"] == "my-agent"


def test_http_get_header_is_case_insensitive() -> None:
    scope: Dict[str, Any] = {"headers": [(b"x-request-id", b"r1")]}
    assert get_header(scope, "x-request-id") == "r1"  # type: ignore[arg-type]
    assert get_header(scope, "X-REQUEST-ID") == "r1"  # type: ignore[arg-type]
    assert get_header(scope, "missing", default="d") == "d"  # type: ignore[arg-type]


def test_http_get_client_host_defaults_and_reads_client() -> None:
    assert get_client_host({"type": "http"}) == "-"  # type: ignore[arg-type]
    assert get_client_host({"type": "http", "client": ("1.2.3.4", 1)}) == "1.2.3.4"  # type: ignore[arg-type]


def test_context_request_id_default_set_reset() -> None:
    assert get_request_id() == "-"
    token = set_request_id("abc")
    assert get_request_id() == "abc"
    reset_request_id(token)
    assert get_request_id() == "-"


@pytest.mark.anyio
async def test_request_id_middleware_generates_and_injects_and_resets() -> None:
    seen: Dict[str, str] = {}

    async def app(scope: Any, receive: Any, send: Any) -> None:
        _ = scope, receive
        seen["request_id"] = get_request_id()
        await send({"type": "http.response.start", "status": 200, "headers": []})
        await send({"type": "http.response.body", "body": b"ok"})

    middleware = RequestIdMiddleware(app, generator=lambda: "gen-1")
    status, headers, body = await _asgi_request(middleware, path="/x")
    assert status == 200
    assert body == b"ok"
    assert headers.get("x-request-id") == "gen-1"
    assert seen["request_id"] == "gen-1"
    assert get_request_id() == "-"


@pytest.mark.anyio
async def test_request_id_middleware_propagates_existing_header() -> None:
    calls: Dict[str, int] = {"n": 0}

    def _gen() -> str:
        calls["n"] += 1
        return "should-not-be-used"

    async def app(scope: Any, receive: Any, send: Any) -> None:
        _ = scope, receive
        assert get_request_id() == "req-9"
        await send({"type": "http.response.start", "status": 204, "headers": []})
        await send({"type": "http.response.body", "body": b""})

    middleware = RequestIdMiddleware(app, generator=_gen)
    status, headers, _body = await _asgi_request(middleware, headers={"X-Request-Id": "req-9"})
    assert status == 204
    assert headers.get("x-request-id") == "req-9"
    assert calls["n"] == 0


@pytest.mark.anyio
async def test_access_log_middleware_logs_one_line_and_uses_response_request_id(caplog: Any) -> None:
    caplog.set_level(logging.INFO, logger="nserve.access")

    async def app(scope: Any, receive: Any, send: Any) -> None:
        _ = scope, receive
        await send(
            {
                "type": "http.response.start",
                "status": 201,
                "headers": [(b"x-request-id", b"resp-1")],
            }
        )
        await send({"type": "http.response.body", "body": b""})

    middleware = AccessLogMiddleware(app)
    status, _headers, _body = await _asgi_request(
        middleware,
        method="POST",
        path="/items",
        headers={"X-Request-Id": "req-1"},
    )
    assert status == 201

    records = [r for r in caplog.records if r.name == "nserve.access"]
    assert len(records) == 1
    msg = records[0].getMessage()
    assert "HTTP POST /items -> 201" in msg
    assert "client=127.0.0.1" in msg
    assert "request_id=resp-1" in msg


@pytest.mark.anyio
async def test_exception_handlers_http_exception_without_details() -> None:
    app = FastAPI()
    install_exception_handlers(app, include_details=False)

    @app.get("/boom")
    async def boom() -> None:
        raise StarletteHTTPException(status_code=404, detail="missing")

    status, _headers, body = await _asgi_request(app, path="/boom")
    payload = _json(body)

    assert status == 404
    assert payload["error"] == ErrorCodes.HTTP_ERROR
    assert payload["message"] == "missing"
    assert payload["status"] == 404
    assert payload["request_id"] == "-"
    assert "details" not in payload


@pytest.mark.anyio
async def test_exception_handlers_http_exception_with_details() -> None:
    app = FastAPI()
    install_exception_handlers(app, include_details=True)

    @app.get("/boom")
    async def boom() -> None:
        raise StarletteHTTPException(status_code=403, detail="forbidden")

    status, _headers, body = await _asgi_request(app, path="/boom")
    payload = _json(body)

    assert status == 403
    assert payload["error"] == ErrorCodes.HTTP_ERROR
    assert payload["details"] == "forbidden"


@pytest.mark.anyio
async def test_exception_handlers_validation_error() -> None:
    app = FastAPI()
    install_exception_handlers(app, include_details=False)

    @app.get("/add")
    async def add(x: int) -> Dict[str, int]:
        return {"x": x}

    status, _headers, body = await _asgi_request(app, path="/add", query_string=b"x=not-an-int")
    payload = _json(body)

    assert status == 422
    assert payload["error"] == ErrorCodes.VALIDATION_ERROR
    assert payload["message"] == "Validation error"
    assert payload["status"] == 422
    assert "details" not in payload


@pytest.mark.anyio
async def test_exception_handlers_internal_error() -> None:
    app = FastAPI()
    install_exception_handlers(app, include_details=False)

    @app.get("/oops")
    async def oops() -> None:
        raise ValueError("bad")

    status, _headers, body = await _asgi_request(app, path="/oops")
    payload = _json(body)

    assert status == 500
    assert payload["error"] == ErrorCodes.INTERNAL_ERROR
    assert payload["message"] == "Internal server error"
    assert payload["status"] == 500
    assert "details" not in payload


@pytest.mark.anyio
async def test_app_factory_health_endpoints() -> None:
    app = create_app(ServeAppOptions(version="1.2.3", enable_access_log=False))
    status, _headers, body = await _asgi_request(app, path="/health")
    assert status == 200
    assert _json(body) == {"status": "ok", "version": "1.2.3"}


@pytest.mark.anyio
async def test_app_factory_disable_health_endpoints() -> None:
    app = create_app(ServeAppOptions(install_health_endpoints=False, enable_access_log=False))
    status, _headers, _body = await _asgi_request(app, path="/health")
    assert status == 404


@pytest.mark.anyio
async def test_app_factory_cors_preflight_includes_headers() -> None:
    app = create_app(
        ServeAppOptions(
            enable_cors=True,
            enable_access_log=False,
            cors_allow_origins=("*",),
            cors_allow_methods=("GET", "POST", "OPTIONS"),
            cors_allow_headers=("*",),
            cors_allow_credentials=True,
        )
    )
    status, headers, _body = await _asgi_request(
        app,
        method="OPTIONS",
        path="/health",
        headers={
            "Origin": "https://example.com",
            "Access-Control-Request-Method": "GET",
        },
    )
    assert status in {200, 204}
    assert "access-control-allow-origin" in headers
    assert headers["access-control-allow-origin"] in {"*", "https://example.com"}


def test_run_uvicorn_validates_port_and_calls_uvicorn(monkeypatch: Any) -> None:
    calls: Dict[str, Any] = {}

    def _run(app: Any, **kwargs: Any) -> None:
        calls["app"] = app
        calls["kwargs"] = kwargs

    monkeypatch.setattr(__import__("uvicorn"), "run", _run)

    with pytest.raises(ValueError):
        run_uvicorn("x:y", UvicornOptions(port=0))

    run_uvicorn("x:y", UvicornOptions(host="127.0.0.1", port=8001, log_level="debug", workers=2, reload=True))
    assert calls["app"] == "x:y"
    assert calls["kwargs"]["host"] == "127.0.0.1"
    assert calls["kwargs"]["port"] == 8001
    assert calls["kwargs"]["log_level"] == "debug"
    assert calls["kwargs"]["workers"] == 2
    assert calls["kwargs"]["reload"] is True


### HELPERS ################################################


def _encode_headers(headers: Optional[Mapping[str, str]]) -> List[Tuple[bytes, bytes]]:
    """
    Encodes headers for an ASGI scope.

    Important:
        ASGI header keys must be lowercase bytes for Starlette's `Headers(scope=...)` to match keys correctly.
    """
    out: List[Tuple[bytes, bytes]] = []
    for k, v in (headers or {}).items():
        out.append((k.lower().encode("latin-1"), v.encode("utf-8")))
    return out


def _decode_headers(headers: Iterable[Tuple[bytes, bytes]]) -> Dict[str, str]:
    out: Dict[str, str] = {}
    for k_b, v_b in headers:
        out[k_b.decode("latin-1").lower()] = v_b.decode("utf-8", errors="replace")
    return out


async def _asgi_request(
    app: Any,
    *,
    method: str = "GET",
    path: str = "/",
    query_string: bytes = b"",
    headers: Optional[Mapping[str, str]] = None,
    body: bytes = b"",
    client: Optional[Tuple[str, int]] = ("127.0.0.1", 12345),
) -> Tuple[int, Dict[str, str], bytes]:
    """
    Runs a single ASGI HTTP request and returns `(status_code, headers, body)`.

    Note:
        Starlette's `ServerErrorMiddleware` re-raises exceptions after sending a response. This harness swallows such
        exceptions only when a response has already started.
    """
    scope: Dict[str, Any] = {
        "type": "http",
        "asgi": {"version": "3.0"},
        "http_version": "1.1",
        "scheme": "http",
        "method": method,
        "path": path,
        "raw_path": path.encode("utf-8"),
        "query_string": query_string,
        "headers": _encode_headers(headers),
        "client": client,
        "server": ("testserver", 80),
    }

    received_once = False

    async def receive() -> Dict[str, Any]:
        nonlocal received_once
        if not received_once:
            received_once = True
            return {"type": "http.request", "body": body, "more_body": False}
        return {"type": "http.disconnect"}

    status_code: int = 0
    response_headers: List[Tuple[bytes, bytes]] = []
    response_body_parts: List[bytes] = []
    response_started = False

    async def send(message: Dict[str, Any]) -> None:
        nonlocal status_code, response_headers, response_started
        if message.get("type") == "http.response.start":
            response_started = True
            status_code = int(message.get("status", 0))
            response_headers = list(message.get("headers", []))
        elif message.get("type") == "http.response.body":
            response_body_parts.append(message.get("body", b""))

    try:
        await app(scope, receive, send)
    except Exception:
        if not response_started:
            raise

    return status_code, _decode_headers(response_headers), b"".join(response_body_parts)


def _json(body: bytes) -> Dict[str, Any]:
    return json.loads(body.decode("utf-8"))
