#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide small helpers to run an ASGI application with Uvicorn.
########################################################################################################################

from __future__ import annotations

from dataclasses import dataclass
from typing import Optional, Union

import uvicorn
from fastapi import FastAPI

__UVICORN_CLASSES_________________________________________________________________________ = ""


@dataclass(frozen=True)
class UvicornOptions:
    """A configuration object for `run_uvicorn()`."""

    host: str = "0.0.0.0"
    port: int = 8000
    reload: bool = False
    log_level: str = "info"
    workers: int = 1

    proxy_headers: bool = True
    forwarded_allow_ips: Optional[str] = None


__UVICORN_RUNNERS_________________________________________________________________________ = ""


def run_uvicorn(app: Union[FastAPI, str], options: UvicornOptions) -> None:
    """Runs Uvicorn for the specified FastAPI application.

    Args:
        app: The FastAPI application or an import string (e.g., `"package.module:app"`).
        options: The Uvicorn options.

    Raises:
        ValueError: If `options.port` is outside the range [1, 65535]
    """
    if options.port < 1 or options.port > 65535:
        raise ValueError("The port must be in [1, 65535]")

    uvicorn.run(
        app,
        host=options.host,
        port=options.port,
        reload=options.reload,
        log_level=options.log_level,
        workers=options.workers,
        proxy_headers=options.proxy_headers,
        forwarded_allow_ips=options.forwarded_allow_ips,
    )
