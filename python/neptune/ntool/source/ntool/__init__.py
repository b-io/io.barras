#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide tooling utilities.
########################################################################################################################

from __future__ import annotations

__TOOL_CONSTANTS__________________________________________________________________________ = ""


__all__ = [s for s in dir() if not s.startswith("_")]
__version__ = "1.0.1a1"


### GLOBALS ################################################

NAME = "ntool"
VERSION = __version__
DESCRIPTION = "Tooling utility library"
