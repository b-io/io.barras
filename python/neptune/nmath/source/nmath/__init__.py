#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains mathematical utility functions
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

from nutil.common import *


## MATH CONSTANTS ########################################################################

__MATH_CONSTANTS____________________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
__version__ = "1.0.0.post137"

############################################################

NAME = "nmath"
VERSION = __version__
DESCRIPTION = "Mathematical utility library"


## MATH MAIN #############################################################################

__MATH_MAIN_________________________________________________ = ""


def main():
    """Starts the application."""
    info("Start %s %s (%s)" % (NAME, VERSION, ENV))


if __name__ == "__main__":
    main()
