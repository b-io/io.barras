#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains collection registry utilities
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

## COLLECTION REGISTRY INIT ##############################################################

__COLLECTION_REGISTRY_INIT__________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
