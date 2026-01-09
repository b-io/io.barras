#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide request-scoped context values for ASGI applications (e.g., the request ID) using `contextvars`.
########################################################################################################################

from __future__ import annotations

from contextvars import ContextVar, Token
from typing import Optional

__CONTEXT_CONSTANTS_______________________________________________________________________ = ""


### REQUEST ID #############################################

_REQUEST_ID_CONTEXT: ContextVar[str] = ContextVar("nserve_request_id", default="-")


__CONTEXT_ACCESSORS_______________________________________________________________________ = ""


### GETTERS ################################################


def get_request_id(default: Optional[str] = None) -> str:
    """Returns the request ID from the current context.

    Args:
        default: The fallback value when the context is unset.

    Returns:
        The request ID
    """
    request_id = _REQUEST_ID_CONTEXT.get()
    if request_id != "-":
        return request_id
    return default if default is not None else "-"


### SETTERS ################################################


def set_request_id(request_id: str) -> Token:
    """Sets the request ID in the current context.

    Args:
        request_id: The request ID to set.

    Returns:
        The context token usable for resetting the value
    """
    return _REQUEST_ID_CONTEXT.set(request_id)


def reset_request_id(token: Token) -> None:
    """Resets the request ID to the value captured by the specified token.

    Args:
        token: The context token returned by `set_request_id`.
    """
    _REQUEST_ID_CONTEXT.reset(token)
