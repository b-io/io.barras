#!/usr/bin/env python
####################################################################################################
# NAME
#   <NAME> - contains financial utility functions
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from nutil.common import *


####################################################################################################
# FIN CONSTANTS
####################################################################################################

__FIN_CONSTANTS___________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
__version__ = "1.0.0.post137"

##################################################

NAME = "nfin"
VERSION = __version__
DESCRIPTION = "Financial utility library"


####################################################################################################
# FIN MAIN
####################################################################################################

__FIN_MAIN________________________________________ = ""


def main():
    """Starts the application."""
    info("Start %s %s (%s)" % (NAME, VERSION, ENV))


if __name__ == "__main__":
    main()
