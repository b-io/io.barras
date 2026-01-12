#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common tooling utilities.
########################################################################################################################

from __future__ import annotations

from nutil.struct.util import *

__COMMON_TOOL_CONSTANTS___________________________________________________________________ = ""


### DEFAULTS ###############################################

DEFAULT_EXCLUDES: List[str] = [
    "**/__pycache__/**",
    "**/.git/**",
    "**/.venv/**",
    "**/build/**",
    "**/dist/**",
    "**/node_modules/**",
    "**/venv/**",
]
