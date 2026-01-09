#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide standardized error responses and exception handlers for FastAPI applications.
########################################################################################################################

from __future__ import annotations

import logging
from typing import Any, Dict, Optional

from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from starlette.exceptions import HTTPException as StarletteHTTPException

from nserve.context import get_request_id

__ERRORS_CONSTANTS________________________________________________________________________ = ""


### DEFAULTS ###############################################

# The default logger name used by the exception handlers
DEFAULT_EXCEPTION_LOGGER_NAME: str = "nserve.exceptions"


__ERRORS_CLASSES__________________________________________________________________________ = ""


class ErrorCodes:
    """A namespace for standardized error codes."""

    HTTP_ERROR: str = "http_error"
    VALIDATION_ERROR: str = "validation_error"
    INTERNAL_ERROR: str = "internal_error"


__ERRORS_FACTORIES________________________________________________________________________ = ""


def _build_error_payload(
    code: str,
    message: str,
    status_code: int,
    details: Optional[Any] = None,
    request_id: Optional[str] = None,
) -> Dict[str, Any]:
    request_id_v = request_id if request_id is not None else get_request_id()
    payload: Dict[str, Any] = {
        "error": code,
        "message": message,
        "status": status_code,
        "request_id": request_id_v,
    }
    if details is not None:
        payload["details"] = details
    return payload


def install_exception_handlers(
    app: FastAPI,
    include_details: bool = False,
    logger_name: str = DEFAULT_EXCEPTION_LOGGER_NAME,
) -> None:
    """Installs standardized exception handlers on the specified FastAPI application.

    Args:
        app: The FastAPI application.
        include_details: Whether to include detailed error information in responses.
        logger_name: The logger name used for server-side error logging.
    """
    logger = logging.getLogger(logger_name)

    @app.exception_handler(StarletteHTTPException)
    async def _handle_http_exception(request: Request, exc: StarletteHTTPException) -> JSONResponse:
        _ = request  # unused
        status_code = int(getattr(exc, "status_code", 500))
        message = str(getattr(exc, "detail", "HTTP error"))
        payload = _build_error_payload(
            code=ErrorCodes.HTTP_ERROR,
            message=message,
            status_code=status_code,
            details=message if include_details else None,
        )
        return JSONResponse(status_code=status_code, content=payload)

    @app.exception_handler(RequestValidationError)
    async def _handle_validation_error(request: Request, exc: RequestValidationError) -> JSONResponse:
        _ = request  # unused
        status_code = 422
        details = exc.errors() if include_details else None
        payload = _build_error_payload(
            code=ErrorCodes.VALIDATION_ERROR,
            message="Validation error",
            status_code=status_code,
            details=details,
        )
        return JSONResponse(status_code=status_code, content=payload)

    @app.exception_handler(Exception)
    async def _handle_unhandled_exception(request: Request, exc: Exception) -> JSONResponse:
        _ = request  # unused
        logger.exception("Unhandled exception (request_id=%s)", get_request_id())
        status_code = 500
        details = str(exc) if include_details else None
        payload = _build_error_payload(
            code=ErrorCodes.INTERNAL_ERROR,
            message="Internal server error",
            status_code=status_code,
            details=details,
        )
        return JSONResponse(status_code=status_code, content=payload)
