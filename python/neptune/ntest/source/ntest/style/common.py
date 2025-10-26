#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# COMMON UTILITIES #####################################################################################################
# Goal
#   Provide small, dependency-light helpers for path handling, glob matching, robust HTTP GET with retries,
#   atomic writes, Unicode/text sanitization (including BiDi/zero-width cleanup and dehyphenation), HTML minification,
#   and compact diff previews you can log during regex-driven edits.
#
# What This Module Offers
#   • Globs & Matching
#       - `dirnames_from_globs(globs, suffix="/**")`        → directory base names implied by directory globs
#       - `match_any_globs(rel_path, globs)`                → POSIX path vs. glob list (treats leading `"**/"` as optional)
#       - `merge_globs(primary, extra)`                     → stable merge (preserve order, drop dups)
#       - `should_exclude_dir(rel_dir, exclude)`            → prune directories by exclude patterns
#       - `should_exclude_file(rel_path, exclude, include)` → inclusion/exclusion gate for files
#
#   • Iterables
#       - `deduplicate(items)`  → remove duplicates while preserving order
#
#   • Networking (GET + retries with backoff)
#       - `build_session_with_retries(...)`         → `requests.Session` with Retry(429/5xx, backoff, headers)
#       - `request(session, url, ...)`              → polite GET with throttle; returns `Response | None`
#       - `request_json(session, url, ...)`         → `(status, obj|None)`; raises `RateLimitError` on 429/503
#       - `RateLimitError(api=None, message=None)`  → uniform, API-labelled rate-limit error
#       Defaults:
#         `DEFAULT_USER_AGENT`, `DEFAULT_ACCEPT`, `DEFAULT_TIMEOUT`, `DEFAULT_THROTTLE`
#
#   • IO
#       - `to_json(obj)`            → JSON-friendly projection (e.g., set → sorted list)
#       - `write_text(path, text)`  → atomic write with fsync best-effort
#
#   • Paths
#       - `join_posix_paths(a, b)`              → clean POSIX join
#       - `resolve_path(path, must_exist=True)` → search CWD & parents when `must_exist=True`
#       - `to_relative_posix_path(path, root)`  → POSIX-style relative ("" for root)
#
#   • Pattern Computation (German declensions)
#       - `get_declension(base, variant, allow_suffixes=...)`   → compact token: "-", "-e", "¨e", etc.
#
#   • Regex Helpers
#       - `build_regex_alternation(alternatives)`                       → longest-first non-capturing alternation
#       - `compile_regex_alternation_pattern(alternatives, flags=...)`  → whole-word compiled pattern
#
#   • String Sets (Latin + DE/FR letters)
#       - `LOWERCASE`, `UPPERCASE`, `LETTERS`   → handy for character-class ranges
#
#   • Text & HTML Sanitizing
#       - `DehyphenationMode` (`off` | `conservative` | `aggressive`)
#       - `SanitizeConfig(dehyphenation=..., collapse_blank_lines=True, normalize_quotes_and_dashes=True)`
#       - `clean_text(text, sanitize_config=None)`  → fix mojibake, remove zero-width/BiDi marks, normalize
#                                                          quotes/dashes/ellipsis, dehyphenate across line breaks,
#                                                          compact whitespace while preserving newlines
#       - `fold_to_ascii(text)`                     → best-effort diacritic folding (NFKD + strip combining)
#       - `minify_html(html_text, preserve_tags=("pre","code","textarea"))`
#                                                   → collapse inter-tag whitespace, keep preserved blocks intact,
#                                                     fix `<p>…<table>` nesting, micro-spacing around tags vs. letters
#       - `minify_text(s)`                          → single-line compaction for CSV cells
#       - `parse_json(s)`                           → safe `dict`-only parse (else `None`)
#       - `strip_html_tags(html_text)`              → plaintext with reasonable newlines for block elements
#       Regex constants clarify terminology:
#         • “BiDi marks” = bidirectional control chars; “NBSP-like” = `\u00A0`, `\u202F`, `\u2007`, `\u2009`, `\u200A`.
#
#   • Differences & Regex-Aware Previews
#       - `get_text_window(text, from_index=0, to_index=None, max_length=200)`  → compact printable slice (↵, ␍, ⇥)
#       - `get_diffs(old, new, context_length=20, max_diffs=100)`               → human-friendly change snippets
#       - `get_diffs_with_pattern(text, pattern, replacement, ...)`             → previews of regex replacements
#       - `sub(pattern, replacement, old, flags=0, label="")`                   → `re.sub` + logged previews
#
# Cache Helpers
#   • `CachePolicy` (Enum with string values) is provided for callers that persist caches.
#
# Key Behaviors & Guarantees
#   • Text cleaning never invents characters; it normalizes or removes control/formatting marks.
#   • Dehyphenation:
#       - `conservative`: join `lowercase-⏎lowercase` only (safer for headings/proper nouns).
#       - `aggressive`: join any `\w-⏎\w` pair.
#   • HTML minification never touches content within `preserve_tags`.
#   • Networking honors `Retry-After` and uses exponential backoff; callers can opt-in to raising on rate limits.
#
# Dependencies
#   • `ftfy`, `requests`, `urllib3` (via `Retry`), and Python stdlib.
#
# Importing
#   from text import (
#       clean_text, minify_html, fold_to_ascii,
#       build_session_with_retries, request_json,
#       join_posix_paths, resolve_path, sub,
#       SanitizeConfig, DehyphenationMode,
#   )
#
# Quick Examples
#   • Clean a blob (keep Unicode, conservative dehyphenation):
#       s = clean_text(raw_html_or_text)
#
#   • Clean with ASCII folding:
#       s = fold_to_ascii(clean_text(text, sanitize_config=SanitizeConfig(normalize_quotes_and_dashes=True)))
#
#   • Robust JSON GET:
#       sess = build_session_with_retries(headers={"Authorization": f"Bearer {token}"})
#       status, data = request_json(sess, "https://api.example.com/v1/thing", api_name="Example")
#
#   • Regex substitution with previews in logs (the `pattern` label appears in log lines):
#       new = sub(r"\s+\n", "\n", old, flags=0, label="trim_trailing_ws")
#
# Notes on Wording
#   • Docstrings sometimes use articles with identifiers for readability (e.g., “the `pattern`”), which is intentional.
########################################################################################################################


from __future__ import annotations

import difflib
import fnmatch
import html
import json
import logging
import os
import re
import time
from dataclasses import dataclass
from enum import Enum
from pathlib import Path
from typing import (
    Any,
    Callable,
    Dict,
    Iterable,
    List,
    Mapping,
    Match,
    Optional,
    Pattern,
    Set,
    Tuple,
    Union,
)

import ftfy
import requests
import unicodedata
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry


## CACHING ###############################################################################


class CachePolicy(str, Enum):
    """
    An enum that selects the cache write behavior (JSON-serializable as strings).

    Values:
        `"read_only"`: Never mutates the cache; no network writes.
        `"write_miss_only"`: Adds new keys on cache misses only (default-safe).
        `"overwrite"`: May replace existing keys.
    """

    READ_ONLY = "read_only"
    WRITE_MISS_ONLY = "write_miss_only"
    OVERWRITE = "overwrite"

    @classmethod
    def from_value(cls, value: str) -> "CachePolicy":
        """Parses a member by value, raising a uniform error on failure."""
        try:
            return cls(value)
        except ValueError as e:
            raise ValueError(f"'{value}' is not a valid value for '{cls.__name__}'") from e

    @classmethod
    def from_name(cls, name: str) -> "CachePolicy":
        """Parses a member by name, raising a uniform error on failure."""
        try:
            return cls[name]
        except KeyError as e:
            raise ValueError(f"'{name}' is not a valid name for '{cls.__name__}'") from e

    @classmethod
    def names(cls) -> List[str]:
        """Returns the list of member names."""
        return list(cls.__members__.keys())

    @classmethod
    def values(cls) -> List[str]:
        """Returns the list of member values."""
        return [m.value for m in cls]


## GLOBS & MATCHING ######################################################################


def dirnames_from_globs(globs: List[str], *, suffix: str = "/**") -> Set[str]:
    """
    Derives the directory basenames from the glob patterns that end with the `suffix`.

    Strategy:
        - Select the last non-wildcard path segment from patterns ending with the `suffix`.

    Args:
        globs: The list of repository-level glob patterns.
        suffix: The marker suffix that denotes directory patterns (defaults to `"/**"`).

    Returns:
        The set of directory basenames discovered.
    """
    names: Set[str] = set()
    for glob in globs:
        if glob.endswith(suffix):
            core = glob[: -len(suffix)]
            segment = core.split("/")[-1]
            if segment and not any(ch in segment for ch in "*?[]"):  # a plain segment
                names.add(segment)
    return names


def match_any_globs(rel_path: str, globs: Iterable[str]) -> bool:
    """
    Matches a POSIX relative path against any glob pattern.

    Args:
        rel_path: The relative path (POSIX separators).
        globs: The iterable of glob patterns.

    Returns:
        `True` if at least one pattern matches; otherwise `False`.
    """
    for glob in globs:
        if fnmatch.fnmatch(rel_path, glob):
            return True
        # Treat `"**/"` as optional so the root-level files match `"**/*.ext"`
        if glob.startswith("**/") and fnmatch.fnmatch(rel_path, glob[3:]):
            return True
    return False


def merge_globs(primary: List[str], extra: List[str]) -> List[str]:
    """
    Merges two glob lists, preserving order and removing duplicates.

    Args:
        primary: The primary list of patterns.
        extra: The additional list of patterns.

    Returns:
        The merged list of unique patterns in first-seen order.
    """
    seen: Set[str] = set()
    merged: List[str] = []
    for g in [*primary, *extra]:
        if g not in seen:
            merged.append(g)
            seen.add(g)
    return merged


def should_exclude_dir(rel_dir: str, exclude: List[str]) -> bool:
    """
    Decides whether a directory should be pruned based on the exclude globs.

    Notes:
        A trailing slash is appended to make `"**/dir/**"` style patterns work reliably.
    """
    rel_path = rel_dir.rstrip("/") + "/"
    return match_any_globs(rel_path, exclude)


def should_exclude_file(rel_path: str, exclude: List[str], include: List[str]) -> bool:
    """
    Decides whether a file should be excluded based on the include/exclude globs.

    Args:
        rel_path: The POSIX-style relative path (e.g., `"src/pkg/mod.py"`).
        exclude: The list of exclude patterns.
        include: The list of include patterns.

    Returns:
        `True` if the file should be skipped; otherwise `False`.
    """
    if include and not match_any_globs(rel_path, include):
        return True
    if exclude and match_any_globs(rel_path, exclude):
        return True
    return False


## ITERABLE ##############################################################################


def deduplicate(items: List[str]) -> List[str]:
    """
    Strips duplicates from a list while preserving the original order.

    Args:
        items: The sequence of strings to deduplicate.

    Returns:
        The deduplicated list with preserved order.
    """
    out: List[str] = []
    seen: Set[str] = set()
    for x in items:
        if x and x not in seen:
            out.append(x)
            seen.add(x)
    return out


## NETWORKING ############################################################################

# The default user agent per the common API policy (for example, WikiMedia); avoid generic defaults
DEFAULT_USER_AGENT = "HttpClient/1.0 (+https://parisjetaime.com/)"
# The default `Accept` header used across the HTTP helpers (the output format negotiation)
DEFAULT_ACCEPT = "application/json"

# The polite delay after successful calls
DEFAULT_THROTTLE: float = 0.25  # [s]
# The default per-request timeout
DEFAULT_TIMEOUT: float = 25.0  # [s]


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


## I/O ###################################################################################


def to_json(obj: Any) -> Any:
    """Serializes arbitrary containers into JSON-friendly structures (e.g., a `set` → a sorted `list`)."""

    if isinstance(obj, dict):
        return {k: to_json(v) for k, v in obj.items()}
    if isinstance(obj, (list, tuple)):
        return [to_json(x) for x in obj]
    if isinstance(obj, set):
        return sorted(to_json(x) for x in obj)
    return obj


def write_text(path: Path, text: str) -> None:
    """
    Writes the `text` to the `path` atomically.

    Args:
        path: The destination file path.
        text: The full file text to write.

    Raises:
        OSError: Propagated only from `Path.replace` if it fails.
    """
    temp_path = path.with_suffix(path.suffix + ".tmp")
    temp_path.parent.mkdir(parents=True, exist_ok=True)
    with temp_path.open("w", encoding="utf-8", newline="") as fh:
        fh.write(text)
        try:
            fh.flush()
            os.fsync(fh.fileno())
        except Exception:
            # Treat the `fsync` failures as non-fatal
            pass
    temp_path.replace(path)


## PATHS #################################################################################


def join_posix_paths(a: str, b: str) -> str:
    """Joins two POSIX path fragments into a clean relative path."""
    return "/".join(p for p in (a, b) if p)


def resolve_path(path: Union[str, Path], *, must_exist: bool = True) -> Path:
    """
    Resolves a `path` relative to the current working directory (and its parents), then returns an absolute `Path`.

    Behavior:
        - When `must_exist` is `True` (default), searches the CWD and all parent directories for an existing match;
          raises `FileNotFoundError` if not found.
        - When `must_exist` is `False`, does not search and does not require existence; returns an absolute path
          composed against the CWD if the input was relative.

    Args:
        path: The input path-like to resolve.
        must_exist: Whether the path must exist to be considered valid.

    Returns:
        The absolute `Path`. If `must_exist` is `False` and the target does not exist, returns an absolute path without
        verifying existence.

    Raises:
        FileNotFoundError: When `must_exist` is `True` and the path cannot be located.
    """
    p = Path(path).expanduser()

    if must_exist:
        if p.exists():
            return p.resolve()
        if not p.is_absolute() and p.parts:
            cwd = Path.cwd().resolve()
            for base in (cwd, *cwd.parents):
                candidate = base / p
                if candidate.exists():
                    return candidate.resolve()
        raise FileNotFoundError(
            f"Could not find '{p}'. Tried the CWD and all the CWD parents with the specified subpath"
        )

    p = p if p.is_absolute() else (Path.cwd() / p)
    return p.resolve() if p.exists() else p


def to_relative_posix_path(path: Path, root: Path) -> str:
    """
    Converts a path to a POSIX-style relative string.

    Args:
        path: The absolute or relative path to convert.
        root: The root directory used as the base.

    Returns:
        The POSIX-style relative path (`""` for the root itself).
    """
    rel_path = os.path.relpath(str(path), str(root))
    return "" if rel_path == "." else rel_path.replace("\\", "/")


## PATTERN COMPUTATION ###################################################################


def get_declension(base: str, variant: str, *, allow_suffixes: Tuple[str, ...]) -> str:
    """
    Computes a compact declension pattern mapping `base` → `variant`.

    Rules:
        - `"-"` for the identical form
        - A pure suffix from `allow_suffixes`: `"-s"`, `"-e"`, …
        - An umlaut (±the suffix): `"¨"`, `"¨e"`, `"¨en"`, `"¨er"`
        - A fallback to the literal tail difference as a suffix

    Args:
        base: The base form (the singular nominative).
        variant: The target form to compare.
        allow_suffixes: The tuple of the recognized suffix strings.

    Returns:
        The compact pattern token (e.g., `"-"`, `"¨e"`, `"-en"`) or an empty string when the inputs are empty.
    """
    # Validate the inputs
    if not base or not variant:
        return ""

    # Handle the identical case
    if variant == base:
        return "-"

    # Recognize a pure suffix
    for suf in allow_suffixes:
        if variant == base + suf:
            return "-" if not suf else f"-{suf}"

    # Synthesize the umlauted stems (the rightmost eligible vowel)
    stems: List[str] = []

    # Add the `"au"` → `"äu"` transformation (respect case; use the last occurrence)
    i = base.rfind("au")
    j = base.rfind("Au")
    k = max(i, j)
    if k != -1:
        stems.append(base[:k] + ("äu" if k == i else "Äu") + base[k + 2 :])

    # Add the single rightmost `"a"`/`"A"`/… → `"ä"`/`"Ä"`/… transformation (respect case)
    uml = {
        "a": "ä",
        "o": "ö",
        "u": "ü",
        "A": "Ä",
        "O": "Ö",
        "U": "Ü",
    }
    for idx in range(len(base) - 1, -1, -1):
        ch = base[idx]
        if ch in uml:
            stems.append(base[:idx] + uml[ch] + base[idx + 1 :])
            break

    # Detect the umlaut change (+ an optional suffix)
    for stem in stems:
        if variant.startswith(stem):
            return "¨" + variant[len(stem) :]

    # Compute the common prefix length
    prefix_len = 0
    for a, b in zip(base, variant):
        if a == b:
            prefix_len += 1
        else:
            break

    # Expose the added tail
    tail = variant[prefix_len:]
    return (
        variant
        if prefix_len < len(base)
        else "-" if not tail else ("-" if prefix_len != 0 else "") + tail
    )


## REGEX #################################################################################


def build_regex_alternation(alternatives: Iterable[str]) -> str:
    """Builds a non-capturing alternation sorted longest-first to prevent partial matches."""
    return "(?:" + "|".join(map(re.escape, sorted(alternatives, key=len, reverse=True))) + ")"


def compile_regex_alternation_pattern(
    alternatives: Iterable[str], *, flags: int = re.IGNORECASE
) -> Pattern[str]:
    """Compiles a regex that matches any alternative as a whole word (with word boundaries)."""
    return re.compile(rf"\b({build_regex_alternation(alternatives)})\b", flags=flags)


## STRING SETS ###########################################################################

# The basic Latin ranges
LOWERCASE_LATIN: str = "a-z"
UPPERCASE_LATIN: str = "A-Z"

# The language-specific letters
LOWERCASE_DE: str = "äöüß"
UPPERCASE_DE: str = "ÄÖÜẞ"

LOWERCASE_FR: str = "àâæçéèêëîïôœùûüÿ"
UPPERCASE_FR: str = "ÀÂÆÇÉÈÊËÎÏÔŒÙÛÜŸ"

# The combined sets
LOWERCASE: str = f"{LOWERCASE_LATIN}{LOWERCASE_DE}{LOWERCASE_FR}"
UPPERCASE: str = f"{UPPERCASE_LATIN}{UPPERCASE_DE}{UPPERCASE_FR}"
LETTERS: str = f"{LOWERCASE}{UPPERCASE}"


## TEXT & HTML SANITIZING ################################################################


class DehyphenationMode(Enum):
    """An enum for the dehyphenation strategies."""

    OFF = "off"
    CONSERVATIVE = "conservative"  # only join lowercase-to-lowercase across a line break
    AGGRESSIVE = "aggressive"  # join any letter-to-letter across a line break

    @classmethod
    def from_value(cls, value: str) -> "DehyphenationMode":
        """Parses a member by value, raising a uniform error on failure."""
        try:
            return cls(value)
        except ValueError as e:
            raise ValueError(f"'{value}' is not a valid value for '{cls.__name__}'") from e

    @classmethod
    def from_name(cls, name: str) -> "DehyphenationMode":
        """Parses a member by name, raising a uniform error on failure."""
        try:
            return cls[name]
        except KeyError as e:
            raise ValueError(f"'{name}' is not a valid name for '{cls.__name__}'") from e

    @classmethod
    def names(cls) -> List[str]:
        """Returns the list of member names."""
        return list(cls.__members__.keys())

    @classmethod
    def values(cls) -> List[str]:
        """Returns the list of member values."""
        return [m.value for m in cls]


@dataclass(frozen=True)
class SanitizeConfig:
    """
    A configuration container that holds the options for `clean_text`.

    Args:
        dehyphenation: The dehyphenation mode (`"off"`, `"conservative"`, `"aggressive"`).
        collapse_blank_lines: If `True`, collapses `"\n\n+"` to a single `"\n"`.
        normalize_quotes_and_dashes: If `True`, maps curly quotes/dashes/minus via a small table.
    """

    dehyphenation: DehyphenationMode = DehyphenationMode.CONSERVATIVE
    collapse_blank_lines: bool = True
    normalize_quotes_and_dashes: bool = True


### PUNCTUATION TRANSLATION TABLE ##########################

PUNCTUATION_TRANSLATION_TABLE: Dict[int, Union[str, int]] = str.maketrans(
    {
        # The dashes
        "–": "-",  # en dash
        "—": "-",  # em dash
        "\u2011": "-",  # non-breaking hyphen
        "\u2010": "-",  # hyphen
        "\u2212": "-",  # minus sign
        "\u25B6": "-",  # ▶ black right-pointing triangle
        "\u25BA": "-",  # ► black right-pointing pointer
        "\u25B8": "-",  # ▸ small right-pointing triangle
        # The quotes
        "“": '"',
        "”": '"',
        "„": '"',
        "’": "'",
        "‘": "'",
        "‚": "'",
        # The ellipsis
        "\u2026": "…",
    }
)

### REGEX CONSTANTS ########################################

# The zero-width and bidirectional (BiDi) control characters (to be removed)
ZERO_WIDTH_AND_BIDI_MARKS_PATTERN: Pattern[str] = re.compile(
    r"[\u200B\u200C\u200D\u2060\uFEFF\u200E\u200F\u202A-\u202E\u2066-\u2069]"
)

# The non-breaking and thin-ish spaces (to be mapped to a regular space)
NO_BREAK_OR_THIN_SPACE_PATTERN: Pattern[str] = re.compile(
    r"[\u00A0\u202F\u2007\u2009\u200A]"
)  # nbsp, narrow no-break, figure, thin/hair

# The discretionary soft hyphen (to be removed)
SOFT_HYPHEN_PATTERN: Pattern[str] = re.compile("\u00AD")

# The form-feed controls (to be normalized to a newline)
FORM_FEED_CHARS_PATTERN: Pattern[str] = re.compile(r"[\f]+")

# The horizontal whitespace except newline (to be collapsed to a single space)
HORIZONTAL_WHITESPACE_EXCEPT_NEWLINE_PATTERN: Pattern[str] = re.compile(r"[^\S\n]+")

# The multiple consecutive newlines (to be collapsed to a single newline)
MULTIPLE_NEWLINES_PATTERN: Pattern[str] = re.compile(r"\n{2,}")

# The dehyphenation across line breaks
ANY_LETTER_HYPHEN_LINEBREAK_ANY_LETTER_PATTERN: Pattern[str] = re.compile(r"(?<=\w)-\n(?=\w)")
LOWERCASE_HYPHEN_LINEBREAK_LOWERCASE_PATTERN: Pattern[str] = re.compile(
    rf"(?<=[{LOWERCASE}])-\n(?=[{LOWERCASE}])"
)


def clean_text(
    text: str,
    *,
    sanitize_config: Optional[SanitizeConfig] = None,
) -> str:
    """
    Cleans the raw text while preserving its meaningful structure.

    What this does (in order):
      1) Fixes mojibake via `ftfy` and normalizes Unicode (`"NFC"`).
      2) Removes the zero-width and directional marks; maps the NBSP-like spaces to a regular `" "`.
      3) Normalizes the dashes, hyphens, and minus; removes the discretionary soft hyphen.
      4) Dehyphenates across line breaks as per the configured mode.
      5) Normalizes the whitespace (keeps newlines) and collapses control chars (form feed → `"\n"`).
      6) Normalizes a few punctuation marks (the quotes and the ellipsis).

    Args:
        text: The raw input string to clean. If `text` is falsy, returns an empty string.
        sanitize_config: The `SanitizeConfig` controlling dehyphenation and punctuation normalization.

    Returns:
        The cleaned text with normalized Unicode, spacing, and punctuation.
    """
    if not text:
        return ""

    # Derive the config controlling the dehyphenation and the punctuation normalization
    sanitize_config = sanitize_config or SanitizeConfig()

    # 1) Unicode repair + normalization
    s = ftfy.fix_text(text)
    s = unicodedata.normalize("NFC", s)

    # 2) Remove the zero-width & BiDi markers; normalize the NBSP-like spaces
    s = ZERO_WIDTH_AND_BIDI_MARKS_PATTERN.sub("", s)
    s = s.replace("&nbsp;", "\u00A0")
    s = NO_BREAK_OR_THIN_SPACE_PATTERN.sub(" ", s)

    # 3) Remove the soft hyphen; normalize the newlines
    s = SOFT_HYPHEN_PATTERN.sub("", s)
    s = s.replace("\r\n", "\n").replace("\r", "\n")

    # 4) Normalize the quotes/dashes if enabled
    if sanitize_config.normalize_quotes_and_dashes:
        s = s.translate(PUNCTUATION_TRANSLATION_TABLE)
        # Optionally collapse exactly three ASCII dots into a single ellipsis (but not 4+ dots)
        s = re.sub(r"(?<!\.)\.\.\.(?!\.)", "…", s)

    # 5) Dehyphenate across line breaks
    if sanitize_config.dehyphenation is DehyphenationMode.AGGRESSIVE:
        s = ANY_LETTER_HYPHEN_LINEBREAK_ANY_LETTER_PATTERN.sub("", s)
    elif sanitize_config.dehyphenation is DehyphenationMode.CONSERVATIVE:
        s = LOWERCASE_HYPHEN_LINEBREAK_LOWERCASE_PATTERN.sub("", s)

    # 6) Whitespace normalization while preserving the line structure
    s = FORM_FEED_CHARS_PATTERN.sub("\n", s)  # form feed → newline
    s = HORIZONTAL_WHITESPACE_EXCEPT_NEWLINE_PATTERN.sub(
        " ", s
    )  # collapse horizontal whitespace runs
    if sanitize_config.collapse_blank_lines:
        s = MULTIPLE_NEWLINES_PATTERN.sub("\n", s)  # normalize multiple newlines

    return s.strip()


def fold_to_ascii(text: str) -> str:
    """
    Folds diacritics to ASCII by removing combining marks (best-effort).

    Example:
        `"Straße"` → `"Strasse"`, `"Curaçao"` → `"Curacao"`.

    Args:
        text: The input string.

    Returns:
        An ASCII-ish representation useful for search keys or filenames.
    """
    if not text:
        return ""
    s = unicodedata.normalize("NFKD", text)
    s = "".join(ch for ch in s if not unicodedata.combining(ch))
    return s.encode("ascii", "ignore").decode("ascii")


def minify_html(
    html_text: Optional[str], *, preserve_tags: Iterable[str] = ("pre", "code", "textarea")
) -> Optional[str]:
    """
    Returns a compact HTML string by collapsing whitespace and trimming spaces *between* tags.

    Guarantees:
      - Preserve the content inside the `preserve_tags` (no whitespace/spacing fixes there).
      - Collapse runs of whitespace to a single space elsewhere.
      - Remove whitespace between closing and opening tags (`"> <"` → `"><"`).
      - Leave the attribute/element order intact.

    Args:
        html_text: The HTML string to minify. If falsy, returns it unchanged.
        preserve_tags: The tag names whose inner HTML must be preserved verbatim.

    Returns:
        The minified HTML, or `None` if the result is empty after trimming.
    """
    if not html_text:
        return html_text

    s = clean_text(html_text)
    placeholders: Dict[str, str] = {}

    # 1) Stash the preserved blocks so nothing touches their contents
    for tag in preserve_tags:
        pattern = re.compile(rf"<{tag}\b[^>]*>.*?</{tag}>", re.IGNORECASE | re.DOTALL)

        def _stash(m: re.Match[str]) -> str:
            key = f"__PRESERVE_BLOCK_{len(placeholders)}__"
            placeholders[key] = m.group(0)
            return key

        s = pattern.sub(_stash, s)

    # 2) Normalize the entities and the whitespace (outside the preserved blocks)
    s = s.replace("&nbsp;", " ")
    s = re.sub(r">\s+<", "><", s)  # tighten inter-tag gaps

    # 3) Fix the invalid `<p>…<table>` nesting: close <p> before a table
    #    (HTML5 implicitly closes <p> before block-level elements; no reopen needed.)
    s = re.sub(r"(?is)<p>(.*?)\s*(?=<table\b)", r"<p>\1</p>", s)

    # 4) Adjust the micro-spacing around the tags vs. the letters (outside the preserved blocks)
    #    - Ensure a space after a closing tag when a letter follows
    #    - Ensure a space before an opening tag when preceded by a letter
    s = re.sub(rf"(</[^>]+>)(?=[{LETTERS}])", r"\1 ", s)
    s = re.sub(rf"(?<=[{LETTERS}])(<[^/!][^>]*>)", r" \1", s)

    # 5) Tighten the spaces around the quotes and the inline tags
    s = re.sub(r"\"\s+(<)", r'"\1', s)  # `" <span>"` → `"<span>"`
    s = re.sub(r"(>)\s+\"", r'\1"', s)  # `"</span> "` → `"</span>"`

    # 6) Restore the preserved blocks verbatim
    for key, block in placeholders.items():
        s = s.replace(key, block)

    return s.strip()


def minify_text(s: Optional[str]) -> Optional[str]:
    """
    Returns a single-line, whitespace-compacted string suitable for CSV cells.

    It uses the project cleaner (no dehyphenation surprises here), then collapses all
    whitespace (including newlines) to single spaces.
    """
    if not s:
        return None
    s = clean_text(s)  # keep spelling as-is; normalize punctuation/spacing
    s = re.sub(r"\s+", " ", s)
    return s.strip()


def parse_json(s: str) -> Optional[Dict[str, Any]]:
    """Attempts to parse the response text as JSON and returns the object on success, otherwise `None`."""
    try:
        obj = json.loads(s)
        return obj if isinstance(obj, dict) else None
    except Exception:
        return None


def strip_html_tags(html_text: str) -> str:
    """
    Strips HTML tags, preserving reasonable line breaks for block-level elements.

    It converts common block boundaries (`<p>`, `<div>`, headings, `<br>`, `<li>`, `<tr>`) to `"\n"`,
    unescapes HTML entities, and collapses leftover whitespace while keeping newlines.

    Args:
        html_text: The HTML snippet to strip.

    Returns:
        The plaintext with approximate structure preserved.
    """
    if not html_text:
        return ""

    s = html_text
    # Normalize the common block boundaries to newlines before stripping the tags
    s = re.sub(r"(?i)<\s*br\s*/?\s*>", "\n", s)
    s = re.sub(r"(?i)</\s*(p|div|h[1-6]|li|tr|table|ul|ol)\s*>", "\n", s)

    # Drop all remaining tags
    s = re.sub(r"(?s)<[^>]+>", "", s)

    # Unescape the entities after dropping the tags
    s = html.unescape(s)

    # Reuse the whitespace compactor semantics
    s = HORIZONTAL_WHITESPACE_EXCEPT_NEWLINE_PATTERN.sub(" ", s)
    s = MULTIPLE_NEWLINES_PATTERN.sub("\n", s)
    return s.strip()


## DIFFERENCES ###########################################################################


def get_text_window(
    text: str,
    from_index: int = 0,  # inclusive index into the `text`
    to_index: Optional[int] = None,  # exclusive index into the `text`
    max_length: Optional[int] = 200,
) -> str:
    """
    Slices a small, printable window from the `text`, adding ellipses and visualizing control characters.

    Behavior:
        - Converts `\n`, `\r`, `\t` to `⏎`, `␍`, `⇥` for display.
        - Prepends/appends `…` when the window is clipped.
        - Enforces `max_length` if provided.

    Args:
        text: The source string.
        from_index: The inclusive start index within the `text` (defaults to `0`).
        to_index: The exclusive end index within the `text` (defaults to `len(text)`).
        max_length: The maximum window length (if set, limits `to_index`).

    Returns:
        The printable snippet string.
    """
    if to_index is None:
        to_index = len(text)
    start = max(0, from_index)
    end = min(to_index, len(text))
    if max_length is not None:
        end = min(end, start + max_length)

    s = text[start:end].replace("\n", "⏎").replace("\r", "␍").replace("\t", "⇥")
    if start > 0:
        s = "…" + s
    if end < len(text):
        s += "…"
    return s


def get_diffs(old: str, new: str, context_length: int = 20, max_diffs: int = 100) -> List[str]:
    """
    Summarizes the differences between `old` and `new` as compact, context-rich snippets.

    Format:
        Each snippet shows a small window around the change and an inline mark:
        - `[-"old"]` for deletions
        - `[+"new"]` for insertions
        - `[-"old"][+"new"]` for replacements

    Args:
        old: The original string.
        new: The revised string.
        context_length: The number of characters to include on each side of a change.
        max_diffs: The maximum number of snippets to emit before truncating.

    Returns:
        The list of snippet strings in change order.
    """
    out: List[str] = []
    sm = difflib.SequenceMatcher(a=old, b=new, autojunk=False)
    for tag, i1, i2, j1, j2 in sm.get_opcodes():
        if tag == "equal":
            continue

        # Choose a window around the `new`-string position for user-facing context
        before = get_text_window(new[:j1], from_index=j1 - context_length)
        after = get_text_window(new[j2:], to_index=context_length)
        old_segment = get_text_window(old[i1:i2])
        new_segment = get_text_window(new[j1:j2])

        if tag == "replace":
            body = f'[-"{old_segment}"][+"{new_segment}"]'
        elif tag == "delete":
            body = f'[-"{old_segment}"]'
        elif tag == "insert":
            body = f'[+"{new_segment}"]'
        else:
            continue

        # Build the change snippet (show the character index in the `new` string)
        index = j1
        snippet = f"@char {index}  …{before}{body}{after}"
        out.append(snippet)

        if len(out) >= max_diffs:
            out.append("…(diff truncated)…")
            break
    return out


def get_diffs_with_pattern(
    text: str,
    pattern: Pattern[str],
    replacement: Union[str, Callable[[Match[str]], str]],
    *,
    context_length: int = 25,
    max_diffs: int = 50,
) -> List[str]:
    """
    Shows compact, context-rich previews for replacements matched by the `pattern`.

    Behavior:
        - Finds non-overlapping regex matches with `pattern.finditer(text)`.
        - Builds a window around each match using `get_text_window`.
        - Previews the replacement by calling `replacement(match)` if callable, else `match.expand(replacement)`.

    Args:
        text: The input text to scan.
        pattern: The compiled regular expression to search with.
        replacement: The replacement string (may use backreferences) or a callable.
        context_length: The number of characters to include on each side of the match.
        max_diffs: The maximum number of preview snippets to emit.

    Returns:
        The list of preview snippet strings in match order.
    """
    out: List[str] = []
    for m in pattern.finditer(text):
        # Choose a window around the match position for user-facing context
        start, end = m.span()
        before = get_text_window(text[:start], from_index=start - context_length)
        after = get_text_window(text[end:], to_index=context_length)
        old_segment = get_text_window(text[start:end])

        # Preview the replacement without modifying the string
        try:
            new_segment_raw = replacement(m) if callable(replacement) else m.expand(replacement)
        except Exception:
            new_segment_raw = "<callable>"
        new_segment = get_text_window(new_segment_raw)

        # Build the change snippet
        snippet = f'…{before}[-"{old_segment}"][+"{new_segment}"]{after}'
        out.append(snippet)

        if len(out) >= max_diffs:
            logging.warning("…(diff truncated)…")
            break
    return out


def sub(
    pattern: str,
    replacement: Union[str, Callable[[Match[str]], str]],
    old: str,
    flags: int = 0,
    label: str = "",
) -> str:
    """
    Substitutes the regex matches with the replacement while logging compact previews of the changes.

    Strategy:
        1) Compile the pattern with the flags.
        2) If there is at least one match, emit preview snippets via `get_diffs_with_pattern`.
        3) Perform the actual substitution once with `re.sub`.

    Args:
        pattern: The regular-expression pattern.
        replacement: The replacement string (supports backreferences) or a callable.
        old: The original text to transform.
        flags: The regex flags to pass to `re.compile`.
        label: The optional label to include in log messages.

    Returns:
        The transformed string with all substitutions applied.
    """
    pattern_re: Pattern[str] = re.compile(pattern, flags)

    # Peek first to decide whether to log
    if not pattern_re.search(old):
        return old

    # Show the change snippets
    for snippet in get_diffs_with_pattern(old, pattern_re, replacement):
        logging.warning("[clean:%s] %s", label or pattern, snippet)

    # Perform the actual substitution once
    new, n = pattern_re.subn(replacement, old)
    logging.debug("[clean:%s] number of replacements: %d", label or pattern, n)
    return new
