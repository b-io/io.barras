#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# NAME
#   <NAME> - contains utility constants
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

import multiprocessing as mp
import struct

import numpy as np

## CONSTANTS #############################################################################

__CONSTANTS_________________________________________________ = ""

### DEFAULTS ###############################################

# The default encoding
DEFAULT_ENCODING = "utf-8"

# The default environment
DEFAULT_ENV = "local"

# The default assert
DEFAULT_ASSERT = True

##############################

# The default root
DEFAULT_ROOT = None

# The default resources directory
DEFAULT_RES_DIR = "resources"

##############################

# The default severity level (0: FAIL, 1: ERROR, 2: WARN, 3: RESULT, 4: INFO, 5: TEST, 6: DEBUG, 7: TRACE)
DEFAULT_SEVERITY_LEVEL = 4

# The default flag specifying whether to enable the verbose mode
DEFAULT_VERBOSE = True

### GLOBALS ################################################

BIT_COUNT = 8 * struct.calcsize("P")

CORE_COUNT = mp.cpu_count() or 1

##############################

EMPTY = ()

NA_NAME = "N/A"

INF = np.inf
NAN = np.nan

### TYPES ##################################################

OBJECT_TYPE = object
OBJECT_ELEMENT_TYPE = np.object_
