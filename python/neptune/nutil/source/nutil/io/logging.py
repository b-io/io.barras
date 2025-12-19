#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

import logging

########################################################################################################################
# Goal
#   Provide logging utilities.
########################################################################################################################


__LOGGING_CONSTANTS_______________________________________________________________________ = ""


### DEFAULTS ###############################################

DEFAULT_LOG_LEVEL = logging.INFO
DEFAULT_LOG_FORMAT = "%(asctime)s [%(module)s] [%(levelname)s] %(message)s"
DEFAULT_LOG_DATE_FORMAT = "%H:%M:%S"


__LOGGING_PROCESSORS______________________________________________________________________ = ""


def configure_logging(
    level: int = DEFAULT_LOG_LEVEL,
    format: str = DEFAULT_LOG_FORMAT,
    date_format: str = DEFAULT_LOG_DATE_FORMAT,
    force: bool = True,
):
    logging.basicConfig(
        level=level,
        format=format,
        datefmt=date_format,
        force=force,
    )
