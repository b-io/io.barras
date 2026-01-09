#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide low-level HTTP utilities for ASGI/FastAPI servers (headers, client metadata, and small helpers).
########################################################################################################################

from __future__ import annotations

from typing import Dict, Optional

from starlette.types import Scope

__HTTP_CONSTANTS__________________________________________________________________________ = ""


### HEADERS ################################################

# The header carrying the request ID
REQUEST_ID_HEADER: str = "X-Request-Id"

# The header carrying the correlation ID (commonly propagated across services)
CORRELATION_ID_HEADER: str = "X-Correlation-Id"

# The header carrying the user-agent
USER_AGENT_HEADER: str = "User-Agent"


__HTTP_ACCESSORS__________________________________________________________________________ = ""


### GETTERS ################################################


def get_header(scope: Scope, name: str, default: Optional[str] = None) -> Optional[str]:
    """Gets the specified HTTP header from the specified ASGI scope.

    Args:
        scope: The ASGI connection scope.
        name: The header name (case-insensitive).
        default: The default when the header is missing.

    Returns:
        The header value if present, otherwise the default
    """
    name_l = name.lower()
    for key_b, val_b in scope.get("headers", []):
        if key_b.decode("latin-1").lower() == name_l:
            return val_b.decode("utf-8", errors="replace")
    return default


def get_client_host(scope: Scope, default: str = "-") -> str:
    """Gets the client host from the specified ASGI scope.

    Args:
        scope: The ASGI connection scope.
        default: The fallback value when the host is unknown.

    Returns:
        The client host
    """
    client = scope.get("client")
    if not client:
        return default
    host, _port = client
    return host if host else default


__HTTP_CONVERTERS_________________________________________________________________________ = ""


def headers_to_dict(scope: Scope) -> Dict[str, str]:
    """Converts the HTTP headers of the specified ASGI scope to a lowercase-key dictionary.

    Notes:
        The values are decoded as UTF-8 with replacement for invalid bytes.

    Args:
        scope: The ASGI connection scope.

    Returns:
        A dictionary of headers
    """
    headers: Dict[str, str] = {}
    for key_b, val_b in scope.get("headers", []):
        key = key_b.decode("latin-1").lower()
        val = val_b.decode("utf-8", errors="replace")
        headers[key] = val
    return headers
