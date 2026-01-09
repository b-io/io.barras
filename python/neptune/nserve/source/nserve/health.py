#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide standard health endpoints (`/health`, `/live`, and `/ready`) for FastAPI applications.
########################################################################################################################

from __future__ import annotations

from typing import Any, Dict, Optional

from fastapi import APIRouter

__HEALTH_CONSTANTS________________________________________________________________________ = ""


### DEFAULTS ###############################################

DEFAULT_HEALTH_PATH: str = "/health"
DEFAULT_LIVE_PATH: str = "/live"
DEFAULT_READY_PATH: str = "/ready"


__HEALTH_FACTORIES________________________________________________________________________ = ""


def create_health_router(
    health_path: str = DEFAULT_HEALTH_PATH,
    live_path: str = DEFAULT_LIVE_PATH,
    ready_path: str = DEFAULT_READY_PATH,
    version: Optional[str] = None,
) -> APIRouter:
    """Creates an `APIRouter` exposing health endpoints.

    Args:
        health_path: The path for the health endpoint.
        live_path: The path for the liveness endpoint.
        ready_path: The path for the readiness endpoint.
        version: The optional application version returned in the payload.

    Returns:
        The router
    """
    router = APIRouter()

    def _payload(status: str) -> Dict[str, Any]:
        payload: Dict[str, Any] = {"status": status}
        if version is not None:
            payload["version"] = version
        return payload

    @router.get(health_path, tags=["health"])
    async def health() -> Dict[str, Any]:
        """Serves a basic health response."""
        return _payload("ok")

    @router.get(live_path, tags=["health"])
    async def live() -> Dict[str, Any]:
        """Serves a liveness response."""
        return _payload("live")

    @router.get(ready_path, tags=["health"])
    async def ready() -> Dict[str, Any]:
        """Serves a readiness response."""
        return _payload("ready")

    return router
