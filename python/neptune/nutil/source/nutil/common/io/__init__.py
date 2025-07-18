#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2022 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from . import console, file
from .console import *
from .file import *

####################################################################################################
# CONSTANTS
####################################################################################################

__CONSTANTS_______________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
