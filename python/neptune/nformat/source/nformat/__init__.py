#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide formatting utilities.
########################################################################################################################

from .common import *

## FORMATTING INIT #######################################################################

__FORMATTING_INIT___________________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
__version__ = "1.0.1a1"


### GLOBALS ################################################

NAME = "nformat"
VERSION = __version__
DESCRIPTION = "Formatting utility library"
