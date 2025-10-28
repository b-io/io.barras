#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains utility functions for HTTP clients
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

import logging
import time

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from nutil.common import *

## HTTP CONSTANTS ########################################################################

__HTTP_CONSTANTS____________________________________________ = ""

# The default user agent per the common API policy (for example, Wikimedia); avoid generic defaults
DEFAULT_USER_AGENT = "Neptune/1.0.0 (+https://barras.io; repo:https://github.com/b-io/io.barras)"
# The default `Accept` header used across the HTTP helpers (the output format negotiation)
DEFAULT_ACCEPT = "application/json"

# The polite delay after successful calls
DEFAULT_THROTTLE: float = 0.25  # [s]
# The default per-request timeout
DEFAULT_TIMEOUT: float = 25.0  # [s]


## HTTP CLASSES ##########################################################################

__HTTP_CLASSES______________________________________________ = ""


class RateLimitError(RuntimeError):
    """
    A rate-limit error for provider APIs.

    It builds the default message `"Rate limit reached"` and optionally appends the API
    name in parentheses.

    Args:
        api: The optional provider name to include in the message.
        message: The optional explicit message (overrides the default formatting).

    Examples:
        - `RateLimitError()` → `"Rate limit reached"`
        - `RateLimitError(api="PONS")` → `"Rate limit reached (PONS)"`
    """

    def __init__(self, api: Optional[str] = None, message: Optional[str] = None) -> None:
        msg = message or ("Rate limit reached" + (f" ({api})" if api else ""))
        super().__init__(msg)


## HTTP PROCESSORS #######################################################################

__HTTP_PROCESSORS___________________________________________ = ""


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
    Builds a `requests.Session` with retry-on-5xx/`"429"` and sensible defaults.

    Args:
        total_retries: The maximum number of retries on transient errors.
        backoff_factor: The backoff factor (exponential).
        allowed_methods: The retriable HTTP methods; defaults to `{"GET"}`.
        status_forcelist: The HTTP status codes that trigger a retry; defaults to `{429, 500, 502, 503, 504}`.
        headers: Additional request headers to set on the session (e.g., `{"Authorization": "Bearer …"}`).
                 If a key here duplicates another header, this value takes precedence.
        accept: The default `"Accept"` header (e.g., `"application/json"`, `"text/html"`).
                Defaults to `DEFAULT_ACCEPT` or `HTTP_ACCEPT`.
        user_agent: The `User-Agent` header value; defaults to `DEFAULT_USER_AGENT` or `HTTP_USER_AGENT`.

    Returns:
        A configured `requests.Session`.
    """
    ua_value: str = user_agent or os.environ.get("HTTP_USER_AGENT") or DEFAULT_USER_AGENT
    accept_value: str = accept or os.environ.get("HTTP_ACCEPT") or DEFAULT_ACCEPT
    allowed_methods = frozenset(allowed_methods or {"GET"})
    status_forcelist = frozenset(status_forcelist or {429, 500, 502, 503, 504})

    retry = Retry(
        total=total_retries,
        connect=total_retries,
        read=total_retries,
        status=total_retries,
        allowed_methods=allowed_methods,
        status_forcelist=status_forcelist,
        backoff_factor=backoff_factor,  # 0.5, 1.0, 2.0, …
        raise_on_status=False,
        respect_retry_after_header=True,
    )

    adapter = HTTPAdapter(max_retries=retry)

    session = requests.Session()
    session.mount("https://", adapter)
    session.mount("http://", adapter)
    session.headers.update({"User-Agent": ua_value, "Accept": accept_value})
    if headers:
        session.headers.update(headers)
    return session


def request(
    session: requests.Session,
    url: str,
    *,
    method: str = "GET",
    params: Optional[Dict[str, Any]] = None,
    headers: Optional[Dict[str, str]] = None,
    data: Optional[Union[Dict[str, Any], List[Tuple[str, Any]], bytes, str]] = None,
    files: Optional[Dict[str, Any]] = None,
    json: Optional[Union[Dict[str, Any], List[Any]]] = None,
    timeout: float = DEFAULT_TIMEOUT,
    throttle: float = DEFAULT_THROTTLE,
    api_name: Optional[str] = None,
    raise_on_rate_limit: bool = True,
    raise_on_http_error: bool = False,
    rate_limit_statuses: Tuple[int, ...] = (429, 503),
    accept_empty_statuses: Tuple[int, ...] = (204, 404),
) -> Tuple[int, Optional[requests.Response]]:
    """
    Performs an HTTP request using a session with adapter-managed retries and returns the raw response.

    Behavior:
        - Uses `session.request(method, ...)` so any HTTP verb is supported (defaults to `"GET"`).
        - Sleeps `throttle` seconds after each attempt (success or failure).
        - If `status ∈ rate_limit_statuses` and `raise_on_rate_limit` is `True`, raises `RateLimitError(api=api_name)`.
        - If `status ∈ accept_empty_statuses`, returns `(status, response)` without error.
        - If `raise_on_http_error=True`, non-2xx statuses raise `requests.HTTPError` via `response.raise_for_status()`.

    Args:
        session: The `requests.Session` (with retries configured via adapters).
        url: The full URL.
        method: The HTTP method (e.g., `"GET"`, `"POST"`, `"HEAD"`, ...). Case-insensitive.
        params: The querystring parameters.
        headers: Extra request headers.
        data: The form data / bytes payload.
        files: The multipart files payload.
        json: The JSON body (for JSON requests).
        timeout: The per-request timeout in seconds.
        throttle: Politeness sleep after the request attempt.
        api_name: Provider identifier used in `RateLimitError`.
        raise_on_rate_limit: When `True`, raises `RateLimitError` for `rate_limit_statuses`.
        raise_on_http_error: When `True`, raises `HTTPError` on non-2xx (except accepted empty statuses).
        rate_limit_statuses: Statuses considered rate limiting (e.g., `429`, `503`).
        accept_empty_statuses: Statuses that are expected to have empty bodies (e.g., `204`, `404`).

    Returns:
        `(status_code, response_or_None)`. The response is `None` only on transport failure.

    Raises:
        RateLimitError: When rate-limited and `raise_on_rate_limit=True`.
        requests.HTTPError: When `raise_on_http_error=True` and status is non-2xx (not in `accept_empty_statuses`).
    """
    try:
        resp = session.request(
            url=url,
            method=method.upper(),
            params=params,
            headers=headers,
            data=data,
            files=files,
            json=json,
            timeout=timeout,
        )
    except requests.RequestException as e:
        logging.warning("%s '%s' raised '%s'", method.upper(), url, e)
        time.sleep(throttle)
        return 0, None
    finally:
        # Ensure we always throttle, even on exceptions
        if throttle and throttle > 0:
            if "resp" in locals():
                time.sleep(throttle)

    status = resp.status_code

    # Rate limiting
    if status in rate_limit_statuses:
        if raise_on_rate_limit:
            raise RateLimitError(api=api_name)
        return status, resp

    # Accepted empty payloads (no error)
    if status in accept_empty_statuses:
        return status, resp

    # Non-2xx
    if not (200 <= status < 300):
        if raise_on_http_error:
            resp.raise_for_status()
        return status, resp

    # 2xx
    return status, resp


def request_json(
    session: requests.Session,
    url: str,
    *,
    method: str = "GET",
    params: Optional[Dict[str, Any]] = None,
    headers: Optional[Dict[str, str]] = None,
    data: Optional[Union[Dict[str, Any], List[Tuple[str, Any]], bytes, str]] = None,
    files: Optional[Dict[str, Any]] = None,
    json: Optional[Union[Dict[str, Any], List[Any]]] = None,
    timeout: float = DEFAULT_TIMEOUT,
    throttle: float = DEFAULT_THROTTLE,
    api_name: Optional[str] = None,
    raise_on_rate_limit: bool = True,
    raise_on_http_error: bool = True,
    rate_limit_statuses: Tuple[int, ...] = (429, 503),
    accept_empty_statuses: Tuple[int, ...] = (204, 404),
) -> Tuple[int, Optional[Union[Dict[str, Any], List[Any]]]]:
    """
    Parses JSON from an HTTP request via `request` and returns `(status, json_or_None)`.

    Behavior:
      • Raises `RateLimitError` when rate-limited and `raise_on_rate_limit=True`.
      • Raises `HTTPError` on non-2xx when `raise_on_http_error=True` (except `accept_empty_statuses`).
      • On transport failure (no response), raises `requests.RequestException`.
      • On `accept_empty_statuses` (e.g., 204/404), returns `(status, None)`.
      • On success, validates that the top-level JSON is a `dict` or `list`.

    Returns:
      `(status_code, json_obj_or_None)`.
    """
    status, resp = request(
        session,
        url,
        method=method,
        params=params,
        headers=headers,
        data=data,
        files=files,
        json=json,
        timeout=timeout,
        throttle=throttle,
        api_name=api_name,
        raise_on_rate_limit=raise_on_rate_limit,
        raise_on_http_error=raise_on_http_error,
        rate_limit_statuses=rate_limit_statuses,
        accept_empty_statuses=accept_empty_statuses,
    )
    if resp is None:
        raise requests.RequestException(f"{method.upper()} '{url}' failed after retries")

    if status in accept_empty_statuses:
        return status, None

    json_obj = resp.json()
    if not isinstance(json_obj, (dict, list)):
        raise ValueError("Unexpected JSON shape (expected a top-level object or list)")
    return status, json_obj


def request_text(
    session: requests.Session,
    url: str,
    *,
    method: str = "GET",
    params: Optional[Dict[str, Any]] = None,
    headers: Optional[Dict[str, str]] = None,
    data: Optional[Union[Dict[str, Any], List[Tuple[str, Any]], bytes, str]] = None,
    files: Optional[Dict[str, Any]] = None,
    json: Optional[Union[Dict[str, Any], List[Any]]] = None,
    timeout: float = DEFAULT_TIMEOUT,
    throttle: float = DEFAULT_THROTTLE,
    api_name: Optional[str] = None,
    raise_on_rate_limit: bool = True,
    raise_on_http_error: bool = True,
    rate_limit_statuses: Tuple[int, ...] = (429, 503),
    accept_empty_statuses: Tuple[int, ...] = (204, 404),
) -> Tuple[int, Optional[str]]:
    """
    Parses text from an HTTP request via `request` and returns `(status, text_or_None)`.

    Behavior:
      • Raises `RateLimitError` when rate-limited and `raise_on_rate_limit=True`.
      • Raises `HTTPError` on non-2xx when `raise_on_http_error=True` (except `accept_empty_statuses`).
      • On transport failure (no response), raises `requests.RequestException`.
      • On `accept_empty_statuses` (e.g., 204/404), returns `(status, None)`.
      • On success, returns `Response.text` (decoded per `requests` encoding detection).

    Returns:
      `(status_code, text_or_None)`.
    """
    status, resp = request(
        session,
        url,
        method=method,
        params=params,
        headers=headers,
        data=data,
        files=files,
        json=json,
        timeout=timeout,
        throttle=throttle,
        api_name=api_name,
        raise_on_rate_limit=raise_on_rate_limit,
        raise_on_http_error=raise_on_http_error,
        rate_limit_statuses=rate_limit_statuses,
        accept_empty_statuses=accept_empty_statuses,
    )
    if resp is None:
        raise requests.RequestException(f"{method.upper()} '{url}' failed after retries")

    if status in accept_empty_statuses:
        return status, None

    return status, resp.text
