#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide a small CLI to run a FastAPI application created by `nserve.create_app()` via Uvicorn.
########################################################################################################################

from __future__ import annotations

import argparse
import logging

from nserve.app import create_app, ServeAppOptions
from nserve.uvicorn import run_uvicorn, UvicornOptions
from nutil.io.logging import configure_logging

__CLI_RUNNERS_____________________________________________________________________________ = ""


def run_with_args(args: argparse.Namespace) -> None:
    """Runs the CLI using the specified parsed arguments."""
    app_options = ServeAppOptions(
        title=args.title,
        version=args.version,
        enable_cors=bool(args.cors),
        enable_access_log=not bool(args.no_access_log),
        include_exception_details=bool(args.details),
    )
    app = create_app(app_options)

    uvicorn_options = UvicornOptions(
        host=str(args.host),
        port=int(args.port),
        reload=bool(args.reload),
        log_level=str(args.log_level),
    )

    logging.info(
        "Run the Uvicorn server on %s:%d (reload=%s)",
        uvicorn_options.host,
        uvicorn_options.port,
        uvicorn_options.reload,
    )
    run_uvicorn(app, uvicorn_options)


### ARGUMENTS ##############################################


def parse_args() -> argparse.Namespace:
    """Parses the CLI arguments."""
    return _build_arg_parser().parse_args()


def _build_arg_parser() -> argparse.ArgumentParser:
    """Builds the CLI argument parser."""
    ap = argparse.ArgumentParser(description="Run a basic FastAPI service with nserve utilities.")
    ap.add_argument("--host", help="Bind host.", default="0.0.0.0")
    ap.add_argument("--port", help="Bind port.", type=int, default=8000)
    ap.add_argument("--reload", help="Enable auto-reload.", action="store_true")
    ap.add_argument("--log-level", help="Uvicorn log level.", default="info")

    ap.add_argument("--title", help="OpenAPI title.", default="Service")
    ap.add_argument("--version", help="Service version.", default="0.0.0")

    ap.add_argument("--cors", help="Enable permissive CORS.", action="store_true")
    ap.add_argument("--no-access-log", help="Disable access logging middleware.", action="store_true")
    ap.add_argument("--details", help="Include exception details in error responses.", action="store_true")
    return ap


### MAIN ###################################################


def main() -> None:
    """Runs the CLI."""
    configure_logging(level=logging.INFO)
    args = parse_args()
    logging.info("Run '%s' with args: %s", "nserve", args)
    run_with_args(args)


if __name__ == "__main__":
    main()
