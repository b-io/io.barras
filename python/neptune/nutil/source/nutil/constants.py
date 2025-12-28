#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide utility constants.
########################################################################################################################

from __future__ import annotations

import multiprocessing as mp
import struct
from typing import Optional

import numpy as np

__CONSTANTS_______________________________________________________________________________ = ""


### DEFAULTS ###############################################

# The default assert
DEFAULT_ASSERT: bool = True

# The default encoding
DEFAULT_ENCODING = "utf-8"

# The default environment
DEFAULT_ENV = "local"

##############################

# The default root
DEFAULT_ROOT: Optional[str] = None

# The default resources directory
DEFAULT_RES_DIR = "resources"

##############################

# The default severity level (0: FAIL, 1: ERROR, 2: WARN, 3: RESULT, 4: INFO, 5: TEST, 6: DEBUG, 7: TRACE)
DEFAULT_SEVERITY_LEVEL: int = 4

# The default flag specifying whether to enable the verbose mode
DEFAULT_VERBOSE: bool = True


### GLOBALS ################################################

BIT_COUNT: int = 8 * struct.calcsize("P")

CORE_COUNT: int = mp.cpu_count() or 1

##############################

EMPTY = ()

##############################

NA_NAME = "N/A"

BULLET = "•"
COLON = ":"
ELLIPSIS = "…"
SEMICOLON = ";"

BACKSPACE = "\b"
CARRIAGE_RETURN = "\r"
FORM_FEED = "\f"
NEWLINE = "\n"
TABULATION = "\t"

##############################

INF = np.inf
NAN = np.nan


### TYPES ##################################################

OBJECT_TYPE = object
OBJECT_ELEMENT_TYPE = np.object_
