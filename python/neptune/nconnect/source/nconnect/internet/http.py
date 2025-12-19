#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide connectivity utilities for HTTP clients.
########################################################################################################################

import json
import logging
import random
import time
from dataclasses import dataclass

import requests
from requests import sessions
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from nutil.common import *
from nutil.enums import HttpMethod, HttpStatusCode, StrEnum
from nutil.io.file import write_bytes
from nutil.scalar.number import to_int
from nutil.scalar.string import to_string
from nutil.struct.util import create_empty


__HTTP_CONSTANTS__________________________________________________________________________ = ""


### DEFAULTS ###############################################

# The default user agent per the common API policy (e.g., Wikimedia)
DEFAULT_USER_AGENT: str = (
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119 Safari/537.36"
)
# The default `Accept` header used across the HTTP helpers (the output format negotiation)
DEFAULT_ACCEPT: str = "*/*"
# The default OK HTTP statuses
DEFAULT_OK_STATUSES: Tuple[HttpStatusCode, ...] = (HttpStatusCode.OK,)
# The default empty HTTP statuses
DEFAULT_EMPTY_STATUSES: Tuple[HttpStatusCode, ...] = (
    HttpStatusCode.NO_CONTENT,
    HttpStatusCode.NOT_FOUND,
    HttpStatusCode.GONE,
)
# The default rate limit HTTP statuses
DEFAULT_RATE_LIMIT_STATUSES: Tuple[HttpStatusCode, ...] = (
    HttpStatusCode.TOO_MANY_REQUESTS,
    HttpStatusCode.SERVICE_UNAVAILABLE,
)

# The polite delay after successful calls
DEFAULT_THROTTLE: float = 0.25  # [s]
# The default per-request timeout
DEFAULT_TIMEOUT: float = 25  # [s]


__HTTP_CLASSES____________________________________________________________________________ = ""


class Outcome(StrEnum):
    """A normalized response classification independent of the raw HTTP codes."""

    OK = "ok"  # 200 + non-empty payload
    EMPTY_OK = "empty_ok"  # 200 + empty payload (accepted)
    EMPTY_MISS = "empty_miss"  # 200 + empty payload (treated like a miss)
    NO_CONTENT = "no_content"  # e.g., 204/404/410 (accepted-empty statuses)
    UNEXPECTED_STATUS = "unexpected_status"  # other non-2xx


@dataclass(frozen=True)
class ProviderResult:
    """A container for a provider's raw `payload`, the HTTP `status`, and the normalized `outcome`."""

    payload: Any
    status: int = 200
    outcome: Outcome = Outcome.OK


class RateLimitError(RuntimeError):
    """
    An API rate-limit error.

    It builds the default message `"Rate limit reached"` and optionally appends the API
    name in parentheses.

    Args:
        api: The optional provider name to include in the message.
        message: The optional explicit message (overrides the default formatting).

    Examples:
        • `RateLimitError()` → `"Rate limit reached"`.
        • `RateLimitError(api="PONS")` → `"Rate limit reached (PONS)"`.
    """

    def __init__(self, api: Optional[str] = None, message: Optional[str] = None) -> None:
        msg = message or ("Rate limit reached" + (f" ({api})" if api else ""))
        super().__init__(msg)


__HTTP_GENERATORS_________________________________________________________________________ = ""


def build_session_with_retries(
    total_retries: int = 4,
    backoff_factor: float = 0.5,
    *,
    allowed_methods: Optional[Iterable[str]] = None,
    status_forcelist: Optional[Iterable[int]] = None,
    headers: Optional[Mapping[str, str]] = None,
    accept: Optional[str] = None,
    user_agent: Optional[str] = None,
) -> requests.Session:
    """
    Builds a `requests.Session` with retry on 429/5xx HTTP statuses and sensible defaults.

    Args:
        total_retries: The maximum number of retries on the transient errors.
        backoff_factor: The backoff factor (exponential).
        allowed_methods: The retriable HTTP methods; defaults to `{"GET"}`.
        status_forcelist: The HTTP status codes that trigger a retry; defaults to `DEFAULT_RATE_LIMIT_STATUSES + (500, 502, 504)`.
        headers: The additional request headers to set on the session (e.g., `{"Authorization": "Bearer …"}`).
                 If a key here duplicates another header, this value takes precedence.
        accept: The default `"Accept"` header (e.g., `"application/json"`, `"text/html"`).
                Defaults to `DEFAULT_ACCEPT` or `HTTP_ACCEPT`.
        user_agent: The `User-Agent` header value; defaults to `DEFAULT_USER_AGENT` or `HTTP_USER_AGENT`.

    Returns:
        A configured `requests.Session`.
    """
    ua_value: str = user_agent or os.environ.get("HTTP_USER_AGENT") or DEFAULT_USER_AGENT
    accept_value: str = accept or os.environ.get("HTTP_ACCEPT") or DEFAULT_ACCEPT
    allowed_methods = frozenset(m.upper() for m in (allowed_methods or {"GET"}))
    status_forcelist = frozenset(
        status_forcelist
        or (
            DEFAULT_RATE_LIMIT_STATUSES
            + (
                HttpStatusCode.INTERNAL_SERVER_ERROR,
                HttpStatusCode.BAD_GATEWAY,
                HttpStatusCode.GATEWAY_TIMEOUT,
            )
        )
    )

    retry = Retry(
        total=total_retries,
        connect=total_retries,
        read=total_retries,
        status=total_retries,
        allowed_methods=allowed_methods,
        status_forcelist=status_forcelist,
        backoff_factor=backoff_factor,  # 0.5, 1, 2, …
        raise_on_status=False,
        respect_retry_after_header=True,
    )

    adapter = HTTPAdapter(max_retries=retry)

    session = requests.Session()
    session.mount("https://", adapter)
    session.mount("http://", adapter)
    session.headers.update({"User-Agent": ua_value, "Accept": accept_value})
    if not is_null(headers):
        session.headers.update(headers)
    return session


__HTTP_PROCESSORS_________________________________________________________________________ = ""


### HTTP REQUESTS ##########################################


def request(
    url: str,
    *,
    session: Optional[requests.Session] = None,
    method: HttpMethod = HttpMethod.GET,
    params: Optional[Dict[str, Any]] = None,
    headers: Optional[Dict[str, str]] = None,
    data: Optional[Union[Dict[str, Any], List[Tuple[str, Any]], bytes, str]] = None,
    files: Optional[Dict[str, Any]] = None,
    json: Optional[Union[Dict[str, Any], List[Any]]] = None,
    timeout: float = DEFAULT_TIMEOUT,
    throttle: float = DEFAULT_THROTTLE,
    api_name: Optional[str] = None,
    accept_empty_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_EMPTY_STATUSES,
    rate_limit_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_RATE_LIMIT_STATUSES,
    raise_on_rate_limit: bool = True,
    raise_on_http_error: bool = False,
) -> Tuple[int, Optional[requests.Response]]:
    """
    Performs an HTTP request using a session with adapter-managed retries and returns the raw response.

    Behavior:
        • Uses `session.request(method.value, …)` (defaults to `HttpMethod.GET`).
        • Sleeps the `throttle` seconds after each attempt (success or failure).
        • On transport failure (no response), logs a warning and returns `(0, None)`.
        • If `status ∈ accept_empty_statuses`, returns `(status, response)` without error.
        • If `status ∈ rate_limit_statuses` and `raise_on_rate_limit` is `True`, raises `RateLimitError(api=api_name)`.
        • If `raise_on_http_error=True`, non-2xx statuses raise `requests.HTTPError` via `response.raise_for_status()`.

    Args:
        url: The full URL.
        session: The `requests.Session` (with retries configured via adapters).
        method: The HTTP method (e.g., `HttpMethod.GET`, `HttpMethod.POST`, …).
        params: The querystring parameters.
        headers: The extra request headers.
        data: The form data / bytes payload.
        files: The multipart files payload.
        json: The JSON body (for JSON requests).
        timeout: The per-request timeout in seconds.
        throttle: The politeness sleep after the request attempt.
        api_name: The provider identifier used in `RateLimitError`.
        accept_empty_statuses: The statuses that are expected to have empty bodies (e.g., `204`, `404`, `410`).
        rate_limit_statuses: The statuses considered rate limiting (e.g., `429`, `503`).
        raise_on_rate_limit: When `True`, raises the `RateLimitError` for `rate_limit_statuses`.
        raise_on_http_error: When `True`, raises `HTTPError` on non-2xx (except accepted empty statuses).

    Returns:
        `(status_code, response_or_None)`. The response is `None` only on transport failure.

    Raises:
        RateLimitError: When rate-limited and `raise_on_rate_limit=True`.
        requests.HTTPError: When `raise_on_http_error=True` and status is non-2xx (not in `accept_empty_statuses`).
    """
    try:
        if is_null(session):
            with sessions.Session() as session:
                response = session.request(
                    method=method.value,
                    url=url,
                    params=params,
                    headers=headers,
                    data=data,
                    files=files,
                    json=json,
                    timeout=timeout,
                )
        else:
            response = session.request(
                method=method.value,
                url=url,
                params=params,
                headers=headers,
                data=data,
                files=files,
                json=json,
                timeout=timeout,
            )
    except requests.RequestException as e:
        logging.warning("%s '%s' raised '%s'", method.value, url, e)
        return 0, None
    finally:
        # Always throttle, even on exceptions
        if throttle and throttle > 0:
            time.sleep(throttle + random.uniform(0, 0.1))

    status = response.status_code

    # Handle accepted empty payloads (no error)
    if status in to_int(accept_empty_statuses):
        return status, response

    # Handle rate limitations
    if status in to_int(rate_limit_statuses):
        if raise_on_rate_limit:
            raise RateLimitError(api=api_name)
        return status, response

    # Handle non-2xx statuses
    if not (200 <= status < 300):
        if raise_on_http_error:
            response.raise_for_status()
        return status, response

    # Handle 2xx statuses
    return status, response


#### HTTP CONTENT ############


def request_content(
    url: str,
    *,
    session: Optional[requests.Session] = None,
    method: HttpMethod = HttpMethod.GET,
    params: Optional[Dict[str, Any]] = None,
    headers: Optional[Dict[str, str]] = None,
    data: Optional[Union[Dict[str, Any], List[Tuple[str, Any]], bytes, str]] = None,
    files: Optional[Dict[str, Any]] = None,
    json: Optional[Union[Dict[str, Any], List[Any]]] = None,
    timeout: float = DEFAULT_TIMEOUT,
    throttle: float = DEFAULT_THROTTLE,
    api_name: Optional[str] = None,
    accept_empty_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_EMPTY_STATUSES,
    rate_limit_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_RATE_LIMIT_STATUSES,
    raise_on_rate_limit: bool = True,
    raise_on_http_error: bool = True,
) -> Tuple[int, Optional[bytes]]:
    """
    Parses the binary content from an HTTP request via `request` and returns `(status, content_or_None)`.

    Behavior:
        • Sleeps the `throttle` seconds after each attempt (success or failure).
        • On transport failure (no response), raises `requests.RequestException`.
        • If `status ∈ accept_empty_statuses`, returns `(status, None)` without error.
        • If `status ∈ rate_limit_statuses` and `raise_on_rate_limit` is `True`, raises `RateLimitError(api=api_name)`.
        • If `raise_on_http_error=True`, non-2xx statuses raise `requests.HTTPError` via `response.raise_for_status()`.
        • On success, returns `Response.content` as bytes.

    Args:
        url: The full URL of the resource to request.
        session: The `requests.Session` (with retries configured via adapters).
        method: The HTTP method (e.g., `HttpMethod.GET`, `HttpMethod.POST`, …).
        params: The querystring parameters.
        headers: The extra request headers.
        data: The form data / bytes payload.
        files: The multipart files payload.
        json: The JSON body (for JSON requests).
        timeout: The per-request timeout in seconds.
        throttle: The politeness sleep after the request attempt.
        api_name: The provider identifier used in `RateLimitError`.
        accept_empty_statuses: The statuses that are expected to have empty bodies (e.g., `204`, `404`, `410`).
        rate_limit_statuses: The statuses considered rate limiting (e.g., `429`, `503`).
        raise_on_rate_limit: When `True`, raises the `RateLimitError` for `rate_limit_statuses`.
        raise_on_http_error: When `True`, raises `HTTPError` on non-2xx (except accepted empty statuses).

    Returns:
        `(status_code, content_or_None)`. The content is `None` only for accepted empty statuses.

    Raises:
        RateLimitError: When rate-limited and `raise_on_rate_limit=True`.
        requests.HTTPError: When `raise_on_http_error=True` and status is non-2xx (not in `accept_empty_statuses`).
        requests.RequestException: When the underlying request fails and no response is available.
    """
    status, response = request(
        url,
        session=session,
        method=method,
        params=params,
        headers=headers,
        data=data,
        files=files,
        json=json,
        timeout=timeout,
        throttle=throttle,
        api_name=api_name,
        accept_empty_statuses=accept_empty_statuses,
        rate_limit_statuses=rate_limit_statuses,
        raise_on_rate_limit=raise_on_rate_limit,
        raise_on_http_error=raise_on_http_error,
    )
    if is_null(response):
        raise requests.RequestException(f"{method.value} '{url}' failed after retries")

    if status in to_int(accept_empty_statuses):
        return status, None

    return status, response.content


#### HTTP JSON ###############


def request_json(
    url: str,
    *,
    session: Optional[requests.Session] = None,
    method: HttpMethod = HttpMethod.GET,
    params: Optional[Dict[str, Any]] = None,
    headers: Optional[Dict[str, str]] = None,
    data: Optional[Union[Dict[str, Any], List[Tuple[str, Any]], bytes, str]] = None,
    files: Optional[Dict[str, Any]] = None,
    json: Optional[Union[Dict[str, Any], List[Any]]] = None,
    timeout: float = DEFAULT_TIMEOUT,
    throttle: float = DEFAULT_THROTTLE,
    api_name: Optional[str] = None,
    accept_empty_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_EMPTY_STATUSES,
    rate_limit_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_RATE_LIMIT_STATUSES,
    raise_on_rate_limit: bool = True,
    raise_on_http_error: bool = True,
) -> Tuple[int, Optional[Union[Dict[str, Any], List[Any]]]]:
    """
    Parses the JSON from an HTTP request via `request` and returns `(status, json_or_None)`.

    Behavior:
        • Sleeps the `throttle` seconds after each attempt (success or failure).
        • On transport failure (no response), raises `requests.RequestException`.
        • If `status ∈ accept_empty_statuses`, returns `(status, None)` without error.
        • If `status ∈ rate_limit_statuses` and `raise_on_rate_limit` is `True`, raises `RateLimitError(api=api_name)`.
        • If `raise_on_http_error=True`, non-2xx statuses raise `requests.HTTPError` via `response.raise_for_status()`.
        • On success, validates that the top-level JSON is a `dict` or a `list`.

    Args:
        url: The full URL of the resource to request.
        session: The `requests.Session` (with retries configured via adapters).
        method: The HTTP method (e.g., `HttpMethod.GET`, `HttpMethod.POST`, …).
        params: The querystring parameters.
        headers: The extra request headers.
        data: The form data / bytes payload.
        files: The multipart files payload.
        json: The JSON body (for JSON requests).
        timeout: The per-request timeout in seconds.
        throttle: The politeness sleep after the request attempt.
        api_name: The provider identifier used in `RateLimitError`.
        accept_empty_statuses: The statuses that are expected to have empty bodies (e.g., `204`, `404`, `410`).
        rate_limit_statuses: The statuses considered rate limiting (e.g., `429`, `503`).
        raise_on_rate_limit: When `True`, raises the `RateLimitError` for `rate_limit_statuses`.
        raise_on_http_error: When `True`, raises `HTTPError` on non-2xx (except accepted empty statuses).

    Returns:
      `(status_code, json_or_None)`.
    """
    headers = {"Accept": "application/json", **(headers or {})}
    status, response = request(
        url,
        session=session,
        method=method,
        params=params,
        headers=headers,
        data=data,
        files=files,
        json=json,
        timeout=timeout,
        throttle=throttle,
        api_name=api_name,
        accept_empty_statuses=accept_empty_statuses,
        rate_limit_statuses=rate_limit_statuses,
        raise_on_rate_limit=raise_on_rate_limit,
        raise_on_http_error=raise_on_http_error,
    )
    if is_null(response):
        raise requests.RequestException(f"{method.value} '{url}' failed after retries")

    if status in to_int(accept_empty_statuses):
        return status, None

    json = response.json()
    if not isinstance(json, (dict, list)):
        raise ValueError("Unexpected JSON shape (expected a top-level object or list)")
    return status, json


#### HTTP TEXT ###############


def request_text(
    url: str,
    *,
    session: Optional[requests.Session] = None,
    method: HttpMethod = HttpMethod.GET,
    params: Optional[Dict[str, Any]] = None,
    headers: Optional[Dict[str, str]] = None,
    data: Optional[Union[Dict[str, Any], List[Tuple[str, Any]], bytes, str]] = None,
    files: Optional[Dict[str, Any]] = None,
    json: Optional[Union[Dict[str, Any], List[Any]]] = None,
    timeout: float = DEFAULT_TIMEOUT,
    throttle: float = DEFAULT_THROTTLE,
    api_name: Optional[str] = None,
    accept_empty_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_EMPTY_STATUSES,
    rate_limit_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_RATE_LIMIT_STATUSES,
    raise_on_rate_limit: bool = True,
    raise_on_http_error: bool = True,
) -> Tuple[int, Optional[str]]:
    """
    Parses the text from an HTTP request via `request` and returns `(status, text_or_None)`.

    Behavior:
        • Sleeps the `throttle` seconds after each attempt (success or failure).
        • On transport failure (no response), raises `requests.RequestException`.
        • If `status ∈ accept_empty_statuses`, returns `(status, None)` without error.
        • If `status ∈ rate_limit_statuses` and `raise_on_rate_limit` is `True`, raises `RateLimitError(api=api_name)`.
        • If `raise_on_http_error=True`, non-2xx statuses raise `requests.HTTPError` via `response.raise_for_status()`.
        • On success, returns `Response.text`.

    Args:
        url: The full URL of the resource to request.
        session: The `requests.Session` (with retries configured via adapters).
        method: The HTTP method (e.g., `HttpMethod.GET`, `HttpMethod.POST`, …).
        params: The querystring parameters.
        headers: The extra request headers.
        data: The form data / bytes payload.
        files: The multipart files payload.
        json: The JSON body (for JSON requests).
        timeout: The per-request timeout in seconds.
        throttle: The politeness sleep after the request attempt.
        api_name: The provider identifier used in `RateLimitError`.
        accept_empty_statuses: The statuses that are expected to have empty bodies (e.g., `204`, `404`, `410`).
        rate_limit_statuses: The statuses considered rate limiting (e.g., `429`, `503`).
        raise_on_rate_limit: When `True`, raises the `RateLimitError` for `rate_limit_statuses`.
        raise_on_http_error: When `True`, raises `HTTPError` on non-2xx (except accepted empty statuses).

    Returns:
      `(status_code, text_or_None)`.
    """
    status, response = request(
        url,
        session=session,
        method=method,
        params=params,
        headers=headers,
        data=data,
        files=files,
        json=json,
        timeout=timeout,
        throttle=throttle,
        api_name=api_name,
        accept_empty_statuses=accept_empty_statuses,
        rate_limit_statuses=rate_limit_statuses,
        raise_on_rate_limit=raise_on_rate_limit,
        raise_on_http_error=raise_on_http_error,
    )
    if is_null(response):
        raise requests.RequestException(f"{method.value} '{url}' failed after retries")

    if status in to_int(accept_empty_statuses):
        return status, None

    return status, response.text


### HTTP LOOKUPS ###########################################


#### HTTP CONTENT ############


def lookup_content(
    url: str,
    *,
    accept_empty_response: bool = True,
    accept_ok_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_OK_STATUSES,
    accept_empty_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_EMPTY_STATUSES,
    coerce: Optional[Callable[[bytes], Any]] = None,
    name: Optional[str] = None,
    context: Optional[str] = None,
    preview_payload: bool = False,
    **kwargs,
) -> ProviderResult:
    """
    Performs a binary-content request and normalizes the response.

    Behavior:
        • Calls `request_content` with `raise_on_http_error=False` unless overridden.
        • Treats any `status ∈ accept_ok_statuses` as OK:
            – Returns `Outcome.OK` for a non-empty payload.
            – Returns `Outcome.EMPTY_OK` for an empty payload (or `Outcome.EMPTY_MISS` when `accept_empty_response=False`).
        • Classifies any `status ∈ accept_empty_statuses` as `Outcome.NO_CONTENT` and returns an empty payload.
        • Classifies any other status as `Outcome.UNEXPECTED_STATUS` and returns an empty payload.
        • Optionally applies `coerce(payload)` to transform bytes into a richer type (e.g., parsed document).

    Args:
        url: The target endpoint.
        accept_empty_response: Indicates whether an empty payload for OK statuses is considered valid.
        accept_ok_statuses: The HTTP statuses to treat as OK (e.g., `(200, 201, 202)`).
        accept_empty_statuses: The HTTP statuses considered valid empty responses (e.g., `204`, `404`, `410`).
        coerce: The optional transformer applied to the raw `bytes` before returning.
        name: The identifier for the logs (e.g., filename, resource key).
        context: The short log context (e.g., `"pdf"`, route).
        preview_payload: Appends a compact payload preview to the logs when `True`.
        **kwargs: Forwards the extra parameters to `request_content` (session, method, params, headers, …).

    Returns:
        `ProviderResult(payload=<Any>, status=<int>, outcome=<Outcome>)`.

    Raises:
        ValueError: Never raised here (kept for symmetry with `lookup_json`).
    """
    # Initialize the empty payload container
    empty_payload: bytes = b""

    # Set the defaults for the underlying request
    kwargs.setdefault("raise_on_http_error", False)
    effective_empty_statuses = kwargs.setdefault("accept_empty_statuses", to_int(accept_empty_statuses))

    # Build the optional log suffix
    suffix_parts: List[str] = []
    if not is_null(name):
        suffix_parts.append(f"'{name}'")
    if not is_null(context):
        suffix_parts.append(f"[{context}]")
    log_suffix = (" for " + " ".join(suffix_parts)) if suffix_parts else ""

    def _preview_payload(x: Any, *, max_bytes: int = 200) -> str:
        if not preview_payload:
            return ""
        try:
            if isinstance(x, (bytes, bytearray)):
                raw = bytes(x)
                snippet = raw[:max_bytes]
                try:
                    s = snippet.decode("utf-8", errors="replace")
                except Exception:
                    s = repr(snippet)
                if len(raw) > max_bytes:
                    s = s[: max_bytes - 1] + "…"
            else:
                s = repr(x)
        except Exception:
            s = "<unprintable>"
        return f", payload={s}"

    # Perform the request
    status: int = 0
    try:
        status, data = request_content(url, **kwargs)
    except Exception as e:
        logging.warning(
            "Transport failure%s → status=%s, outcome=%s, error=%s",
            log_suffix,
            status,
            Outcome.UNEXPECTED_STATUS.value,
            e,
        )
        return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.UNEXPECTED_STATUS)

    # Treat any accepted OK status like 200
    if status in to_int(accept_ok_statuses):
        # Handle the OK status with no body
        if is_null(data):
            if accept_empty_response:
                logging.debug(
                    "Empty response (HTTP %s)%s → status=%s, outcome=%s%s",
                    status,
                    log_suffix,
                    status,
                    Outcome.EMPTY_OK.value,
                    _preview_payload(empty_payload),
                )
                return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_OK)
            logging.warning(
                "Empty response (HTTP %s)%s → status=%s, outcome=%s%s",
                status,
                log_suffix,
                status,
                Outcome.EMPTY_MISS.value,
                _preview_payload(empty_payload),
            )
            return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_MISS)

        # Apply the optional coercion
        payload: Any = data
        if not is_null(coerce):
            try:
                payload = coerce(data)
            except Exception as e:
                logging.warning("Coercion fails%s: %s", log_suffix, e)
                payload = empty_payload

        # Handle the empty payload after coercion or as-is
        if not payload:
            if accept_empty_response:
                logging.debug(
                    "Empty payload (HTTP %s)%s → status=%s, outcome=%s%s",
                    status,
                    log_suffix,
                    status,
                    Outcome.EMPTY_OK.value,
                    _preview_payload(empty_payload),
                )
                return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_OK)
            logging.warning(
                "Empty payload (HTTP %s)%s → status=%s, outcome=%s%s",
                status,
                log_suffix,
                status,
                Outcome.EMPTY_MISS.value,
                _preview_payload(empty_payload),
            )
            return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_MISS)

        # Handle the non-empty success
        logging.debug(
            "Non-empty payload (HTTP %s)%s → status=%s, outcome=%s",
            status,
            log_suffix,
            status,
            Outcome.OK.value,
        )
        return ProviderResult(payload=payload, status=status, outcome=Outcome.OK)

    # Handle the definitive empty statuses (e.g., 204/404/410)
    if status in effective_empty_statuses:
        logging.debug(
            "No content (HTTP %s)%s → status=%s, outcome=%s%s",
            status,
            log_suffix,
            status,
            Outcome.NO_CONTENT.value,
            _preview_payload(empty_payload),
        )
        return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.NO_CONTENT)

    # Handle the unexpected status
    logging.warning(
        "Unexpected HTTP %s%s → status=%s, outcome=%s%s",
        status,
        log_suffix,
        status,
        Outcome.UNEXPECTED_STATUS.value,
        _preview_payload(empty_payload),
    )
    return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.UNEXPECTED_STATUS)


#### HTTP JSON ###############


def lookup_json(
    url: str,
    *,
    accept_empty_response: bool = True,
    accept_ok_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_OK_STATUSES,
    accept_empty_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_EMPTY_STATUSES,
    coerce: Optional[Callable[[Any], Any]] = None,
    response_type: Union[Type[dict], Type[list]] = dict,
    name: Optional[str] = None,
    context: Optional[str] = None,
    preview_payload: bool = False,
    **kwargs,
) -> "ProviderResult":
    """
    Performs a JSON request and normalizes the response.

    Behavior:
        • Calls `request_json` with `raise_on_http_error=False` unless overridden.
        • Validates the top-level JSON shape (`response_type` is `dict` or `list`).
        • Applies `coerce(payload)` when provided and logs on failure.
        • Treats any `status ∈ accept_ok_statuses` as OK:
            – Returns `Outcome.OK` for a non-empty payload.
            – Returns `Outcome.EMPTY_OK` for an empty payload (or `Outcome.EMPTY_MISS` when `accept_empty_response=False`).
        • Classifies any `status ∈ accept_empty_statuses` as `Outcome.NO_CONTENT` and returns an empty payload.
        • Classifies any other status as `Outcome.UNEXPECTED_STATUS` and returns an empty payload.

    Args:
        url: The target endpoint.
        accept_empty_response: Indicates whether an empty JSON payload for OK statuses is considered valid.
        accept_ok_statuses: The HTTP statuses to treat as OK (e.g., `(200, 201, 202)`).
        accept_empty_statuses: The HTTP statuses considered valid empty responses (e.g., `204`, `404`, `410`).
        coerce: The optional transformer applied to the parsed JSON before returning.
        name: The identifier for the logs (e.g., lemma, title).
        context: The short log context (e.g., `"de → en"`, route).
        response_type: The expected top-level JSON type (`dict` or `list`).
        preview_payload: Appends a compact payload preview to the logs when `True`.
        **kwargs: Forwards the extra parameters to `request_json` (session, method, params, headers, …).

    Returns:
        `ProviderResult(payload=<Any>, status=<int>, outcome=<Outcome>)`.

    Raises:
        ValueError: When the `response_type` is not `dict` or `list`.
        RuntimeError: When an OK status returns an unexpected top-level JSON type.
    """
    # Validate the `response_type`
    if response_type not in (dict, list):
        raise ValueError("`response_type` must be `dict` or `list`")

    # Initialize the empty payload container
    empty_payload = create_empty(response_type)

    # Set the defaults for the underlying request
    kwargs.setdefault("raise_on_http_error", False)
    effective_empty_statuses = kwargs.setdefault("accept_empty_statuses", to_int(accept_empty_statuses))

    # Build the optional log suffix
    suffix_parts: List[str] = []
    if not is_null(name):
        suffix_parts.append(f"'{name}'")
    if not is_null(context):
        suffix_parts.append(f"[{context}]")
    log_suffix = (" for " + " ".join(suffix_parts)) if suffix_parts else ""

    def _preview_payload(x: Any, *, max_chars: int = 200) -> str:
        if not preview_payload:
            return ""
        try:
            s = json.dumps(x, ensure_ascii=False, separators=(",", ":"), default=str)
        except Exception:
            s = repr(x)
        if len(s) > max_chars:
            s = s[: max_chars - 1] + "…"
        return f", payload={s}"

    # Perform the request
    status: int = 0
    try:
        status, data = request_json(url, **kwargs)
    except Exception as e:
        logging.warning(
            "Transport failure%s → status=%s, outcome=%s, error=%s",
            log_suffix,
            status,
            Outcome.UNEXPECTED_STATUS.value,
            e,
        )
        return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.UNEXPECTED_STATUS)

    # Treat any accepted OK status like 200
    if status in to_int(accept_ok_statuses):
        # Handle the OK status with no body
        if is_null(data):
            if accept_empty_response:
                logging.debug(
                    "Empty response (HTTP %s)%s → status=%s, outcome=%s%s",
                    status,
                    log_suffix,
                    status,
                    Outcome.EMPTY_OK.value,
                    _preview_payload(empty_payload),
                )
                return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_OK)
            logging.warning(
                "Empty response (HTTP %s)%s → status=%s, outcome=%s%s",
                status,
                log_suffix,
                status,
                Outcome.EMPTY_MISS.value,
                _preview_payload(empty_payload),
            )
            return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_MISS)

        # Validate the OK response body type
        if not isinstance(data, response_type):
            actual = type(data).__name__
            expected = response_type.__name__
            raise RuntimeError(f"Unexpected payload type (expected top-level {expected}, got {actual})")

        # Apply the optional coercion
        payload: Any = data
        if not is_null(coerce):
            try:
                payload = coerce(data)
            except Exception as e:
                logging.warning("Coercion fails%s: %s", log_suffix, e)
                payload = empty_payload

        # Handle the empty payload after coercion or as-is
        if not payload:
            if accept_empty_response:
                logging.debug(
                    "Empty payload (HTTP %s)%s → status=%s, outcome=%s%s",
                    status,
                    log_suffix,
                    status,
                    Outcome.EMPTY_OK.value,
                    _preview_payload(empty_payload),
                )
                return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_OK)
            logging.warning(
                "Empty payload (HTTP %s)%s → status=%s, outcome=%s%s",
                status,
                log_suffix,
                status,
                Outcome.EMPTY_MISS.value,
                _preview_payload(empty_payload),
            )
            return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_MISS)

        # Handle the non-empty success
        logging.debug(
            "Non-empty payload (HTTP %s)%s → status=%s, outcome=%s",
            status,
            log_suffix,
            status,
            Outcome.OK.value,
        )
        return ProviderResult(payload=payload, status=status, outcome=Outcome.OK)

    # Handle the definitive empty statuses (e.g., 204/404/410)
    if status in effective_empty_statuses:
        logging.debug(
            "No content (HTTP %s)%s → status=%s, outcome=%s%s",
            status,
            log_suffix,
            status,
            Outcome.NO_CONTENT.value,
            _preview_payload(empty_payload),
        )
        return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.NO_CONTENT)

    # Handle the unexpected status
    logging.warning(
        "Unexpected HTTP %s%s → status=%s, outcome=%s%s",
        status,
        log_suffix,
        status,
        Outcome.UNEXPECTED_STATUS.value,
        _preview_payload(empty_payload),
    )
    return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.UNEXPECTED_STATUS)


#### HTTP TEXT ###############


def lookup_text(
    url: str,
    *,
    accept_empty_response: bool = True,
    accept_ok_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_OK_STATUSES,
    accept_empty_statuses: Tuple[Union[int, HttpStatusCode], ...] = DEFAULT_EMPTY_STATUSES,
    coerce: Optional[Callable[[str], Any]] = None,
    name: Optional[str] = None,
    context: Optional[str] = None,
    preview_payload: bool = False,
    **kwargs,
) -> ProviderResult:
    """
    Performs a text request and normalizes the response.

    Behavior:
        • Calls `request_text` with `raise_on_http_error=False` unless overridden.
        • Applies `coerce(payload)` when provided and logs on failure.
        • Treats any `status ∈ accept_ok_statuses` as OK:
            – Returns `Outcome.OK` for a non-empty payload.
            – Returns `Outcome.EMPTY_OK` for an empty payload (or `Outcome.EMPTY_MISS` when `accept_empty_response=False`).
        • Classifies any `status ∈ accept_empty_statuses` as `Outcome.NO_CONTENT` and returns an empty payload.
        • Classifies any other status as `Outcome.UNEXPECTED_STATUS` and returns an empty payload.

    Args:
        url: The target endpoint.
        accept_empty_response: Indicates whether an empty text payload for OK statuses is considered valid.
        accept_ok_statuses: The HTTP statuses to treat as OK (e.g., `(200, 201, 202)`).
        accept_empty_statuses: The HTTP statuses considered valid empty responses (e.g., `204`, `404`, `410`).
        coerce: The optional transformer applied to the text before returning (e.g., HTML parsing).
        name: The identifier for the logs (e.g., page title, route).
        context: The short log context (e.g., `"en → de"`, endpoint).
        preview_payload: Appends a compact payload preview to the logs when `True`.
        **kwargs: Forwards the extra parameters to `request_text` (session, method, params, headers, …).

    Returns:
        `ProviderResult(payload=<Any>, status=<int>, outcome=<Outcome>)`.
    """
    # Initialize the empty payload container
    empty_payload: str = ""

    # Set the defaults for the underlying request
    kwargs.setdefault("raise_on_http_error", False)
    effective_empty_statuses = kwargs.setdefault("accept_empty_statuses", to_int(accept_empty_statuses))

    # Build the optional log suffix
    suffix_parts: List[str] = []
    if not is_null(name):
        suffix_parts.append(f"'{name}'")
    if not is_null(context):
        suffix_parts.append(f"[{context}]")
    log_suffix = (" for " + " ".join(suffix_parts)) if suffix_parts else ""

    def _preview_payload(x: Any, *, max_chars: int = 200) -> str:
        if not preview_payload:
            return ""
        try:
            s = to_string(x)
        except Exception:
            s = repr(x)
        if len(s) > max_chars:
            s = s[: max_chars - 1] + "…"
        return f", payload={s}"

    # Perform the request
    status: int = 0
    try:
        status, data = request_text(url, **kwargs)
    except Exception as e:
        logging.warning(
            "Transport failure%s → status=%s, outcome=%s, error=%s",
            log_suffix,
            status,
            Outcome.UNEXPECTED_STATUS.value,
            e,
        )
        return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.UNEXPECTED_STATUS)

    # Treat any accepted OK status like 200
    if status in to_int(accept_ok_statuses):
        # Handle the OK status with no body
        if is_null(data):
            if accept_empty_response:
                logging.debug(
                    "Empty response (HTTP %s)%s → status=%s, outcome=%s%s",
                    status,
                    log_suffix,
                    status,
                    Outcome.EMPTY_OK.value,
                    _preview_payload(empty_payload),
                )
                return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_OK)
            logging.warning(
                "Empty response (HTTP %s)%s → status=%s, outcome=%s%s",
                status,
                log_suffix,
                status,
                Outcome.EMPTY_MISS.value,
                _preview_payload(empty_payload),
            )
            return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_MISS)

        # Apply the optional coercion
        payload: Any = data
        if not is_null(coerce):
            try:
                payload = coerce(data)
            except Exception as e:
                logging.warning("Coercion fails%s: %s", log_suffix, e)
                payload = empty_payload

        # Handle the empty payload after coercion or as-is
        if not payload:
            if accept_empty_response:
                logging.debug(
                    "Empty payload (HTTP %s)%s → status=%s, outcome=%s%s",
                    status,
                    log_suffix,
                    status,
                    Outcome.EMPTY_OK.value,
                    _preview_payload(empty_payload),
                )
                return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_OK)
            logging.warning(
                "Empty payload (HTTP %s)%s → status=%s, outcome=%s%s",
                status,
                log_suffix,
                status,
                Outcome.EMPTY_MISS.value,
                _preview_payload(empty_payload),
            )
            return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.EMPTY_MISS)

        # Handle the non-empty success
        logging.debug(
            "Non-empty payload (HTTP %s)%s → status=%s, outcome=%s",
            status,
            log_suffix,
            status,
            Outcome.OK.value,
        )
        return ProviderResult(payload=payload, status=status, outcome=Outcome.OK)

    # Handle the definitive empty statuses (e.g., 204/404/410)
    if status in effective_empty_statuses:
        logging.debug(
            "No content (HTTP %s)%s → status=%s, outcome=%s%s",
            status,
            log_suffix,
            status,
            Outcome.NO_CONTENT.value,
            _preview_payload(empty_payload),
        )
        return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.NO_CONTENT)

    # Handle the unexpected status
    logging.warning(
        "Unexpected HTTP %s%s → status=%s, outcome=%s%s",
        status,
        log_suffix,
        status,
        Outcome.UNEXPECTED_STATUS.value,
        _preview_payload(empty_payload),
    )
    return ProviderResult(payload=empty_payload, status=status, outcome=Outcome.UNEXPECTED_STATUS)


### HTTP DOWNLOADS #########################################


def download(
    url: str,
    *,
    dir: Optional[str] = None,
    filename: Optional[str] = None,
    name: Optional[str] = None,
    context: Optional[str] = None,
    **kwargs,
) -> str:
    """
    Downloads the file from `url` and writes it to the specified directory, using `lookup_content`.

    Returns:
        The path to the written file as a string.

    Raises:
        requests.RequestException: When no content is available for download.
    """
    if is_null(dir):
        dir = get_dir(".")
    assert dir is not None

    if is_null(filename):
        filename = get_filename(url).split("?", 1)[0]
    assert filename is not None

    target_path = collapse(dir, "/", filename)

    result = lookup_content(
        url,
        accept_empty_response=False,
        name=name or filename,
        context=context,
        **kwargs,
    )

    if not result.payload:
        raise requests.RequestException("No content received for '%s' (HTTP %s)" % (url, result.status))

    try:
        return write_bytes(target_path, result.payload)  # type: ignore[arg-type]
    except Exception as ex:
        logging.error(
            "Failed to write downloaded content to '%s': %s",
            target_path,
            ex,
        )
        raise
