#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains utility functions
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

## CONSTANTS #############################################################################

__CONSTANTS_________________________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
__version__ = "1.0.0.post137"

############################################################

NAME = "nutil"
VERSION = __version__
DESCRIPTION = "Utility library"


## MAIN ##################################################################################

__MAIN______________________________________________________ = ""


def main():
    """Starts the application."""
    info("Start %s %s (%s)" % (NAME, VERSION, ENV))


if __name__ == "__main__":
    main()
