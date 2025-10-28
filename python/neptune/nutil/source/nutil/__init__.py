#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains utilities
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

from . import common
from .common import *

## UTIL INIT #############################################################################

__UTIL_INIT_________________________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
__version__ = "1.0.0.post137"

############################################################

NAME = "nutil"
VERSION = __version__
DESCRIPTION = "Utility library"
