#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide reusable ASGI middleware for FastAPI applications.
#
# Behavior
#   • `RequestIdMiddleware` propagates (or generates) a request ID and always echoes it on the response.
#   • `AccessLogMiddleware` logs one structured line per request with the status code and duration.
########################################################################################################################

from __future__ import annotations

import logging
import time
import uuid
from typing import Callable, Optional

from starlette.types import ASGIApp, Message, Receive, Scope, Send

from nserve.context import reset_request_id, set_request_id
from nserve.http import get_client_host, get_header, REQUEST_ID_HEADER

__MIDDLEWARE_CONSTANTS____________________________________________________________________ = ""


### DEFAULTS ###############################################

# The default logger name for access logs
DEFAULT_ACCESS_LOGGER_NAME: str = "nserve.access"


__MIDDLEWARE_CLASSES______________________________________________________________________ = ""


class RequestIdMiddleware:
    """An ASGI middleware that propagates and injects a request ID."""

    def __init__(
        self,
        app: ASGIApp,
        header_name: str = REQUEST_ID_HEADER,
        generator: Optional[Callable[[], str]] = None,
    ) -> None:
        self.app = app
        self.header_name = header_name
        self._header_name_l = header_name.lower()
        self._header_name_b = header_name.encode("latin-1")
        self.generator = generator if generator is not None else (lambda: uuid.uuid4().hex)

    async def __call__(self, scope: Scope, receive: Receive, send: Send) -> None:
        if scope.get("type") != "http":
            await self.app(scope, receive, send)
            return

        request_id = get_header(scope, self._header_name_l)
        if not request_id:
            request_id = self.generator()

        token = set_request_id(request_id)

        async def send_wrapper(message: Message) -> None:
            if message.get("type") == "http.response.start":
                headers = list(message.get("headers", []))
                headers.append((self._header_name_b, request_id.encode("utf-8")))
                message["headers"] = headers
            await send(message)

        try:
            await self.app(scope, receive, send_wrapper)
        finally:
            reset_request_id(token)


class AccessLogMiddleware:
    """An ASGI middleware that logs one access record per HTTP request."""

    def __init__(
        self,
        app: ASGIApp,
        logger_name: str = DEFAULT_ACCESS_LOGGER_NAME,
        request_id_header: str = REQUEST_ID_HEADER,
    ) -> None:
        self.app = app
        self.logger = logging.getLogger(logger_name)
        self.request_id_header = request_id_header
        self._request_id_header_l = request_id_header.lower()

    async def __call__(self, scope: Scope, receive: Receive, send: Send) -> None:
        if scope.get("type") != "http":
            await self.app(scope, receive, send)
            return

        method = scope.get("method", "-")
        path = scope.get("path", "-")
        client_host = get_client_host(scope)

        request_id = get_header(scope, self._request_id_header_l, default="-") or "-"
        response_request_id = request_id

        status_code: int = 0
        start = time.perf_counter()

        async def send_wrapper(message: Message) -> None:
            nonlocal status_code, response_request_id
            if message.get("type") == "http.response.start":
                status_code = int(message.get("status", 0))
                for key_b, val_b in message.get("headers", []):
                    if key_b.decode("latin-1").lower() == self._request_id_header_l:
                        response_request_id = val_b.decode("utf-8", errors="replace")
                        break
            await send(message)

        try:
            await self.app(scope, receive, send_wrapper)
        finally:
            duration_s = time.perf_counter() - start
            self.logger.info(
                "HTTP %s %s -> %d in %.3fs (client=%s, request_id=%s)",
                method,
                path,
                status_code,
                duration_s,
                client_host,
                response_request_id,
            )
