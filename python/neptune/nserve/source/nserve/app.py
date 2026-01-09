#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide an opinionated FastAPI application factory with standard middleware and endpoints.
#
# Behavior
#   • Installs request ID propagation and an optional access logger.
#   • Installs standardized exception handlers.
#   • Optionally installs standard health endpoints.
########################################################################################################################

from __future__ import annotations

from dataclasses import dataclass
from typing import Optional, Tuple

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from nserve.errors import install_exception_handlers
from nserve.health import create_health_router
from nserve.http import REQUEST_ID_HEADER
from nserve.middleware import AccessLogMiddleware, RequestIdMiddleware

__APP_CLASSES_____________________________________________________________________________ = ""


@dataclass(frozen=True)
class ServeAppOptions:
    """A configuration object for the `create_app()` factory."""

    title: str = "Service"
    version: str = "0.0.0"
    description: str = "ASGI service"

    root_path: str = ""
    docs_url: Optional[str] = "/docs"
    redoc_url: Optional[str] = "/redoc"
    openapi_url: Optional[str] = "/openapi.json"

    enable_request_id: bool = True
    request_id_header: str = REQUEST_ID_HEADER

    enable_access_log: bool = True

    include_exception_details: bool = False

    enable_cors: bool = False
    cors_allow_origins: Tuple[str, ...] = ("*",)
    cors_allow_methods: Tuple[str, ...] = ("*",)
    cors_allow_headers: Tuple[str, ...] = ("*",)
    cors_allow_credentials: bool = True

    install_health_endpoints: bool = True


__APP_FACTORIES___________________________________________________________________________ = ""


def create_app(options: ServeAppOptions) -> FastAPI:
    """Creates a FastAPI application using the specified options.

    Args:
        options: The options controlling the application configuration.

    Returns:
        The FastAPI application
    """
    app = FastAPI(
        title=options.title,
        version=options.version,
        description=options.description,
        root_path=options.root_path,
        docs_url=options.docs_url,
        redoc_url=options.redoc_url,
        openapi_url=options.openapi_url,
    )

    if options.enable_cors:
        app.add_middleware(
            CORSMiddleware,
            allow_origins=list(options.cors_allow_origins),
            allow_methods=list(options.cors_allow_methods),
            allow_headers=list(options.cors_allow_headers),
            allow_credentials=options.cors_allow_credentials,
        )

    if options.enable_request_id:
        app.add_middleware(RequestIdMiddleware, header_name=options.request_id_header)

    if options.enable_access_log:
        app.add_middleware(AccessLogMiddleware, request_id_header=options.request_id_header)

    install_exception_handlers(app, include_details=options.include_exception_details)

    if options.install_health_endpoints:
        app.include_router(create_health_router(version=options.version))

    return app
