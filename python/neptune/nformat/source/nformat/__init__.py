#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain financial utility functions
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

from nutil.common import *

####################################################################################################
# FIN CONSTANTS
####################################################################################################

__FIN_CONSTANTS___________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
__version__ = "1.0.0.post138"

##################################################

NAME = "nformat"
VERSION = __version__
DESCRIPTION = "Formatting utility library"

####################################################################################################
# FIN MAIN
####################################################################################################

__FIN_MAIN________________________________________ = ""


def main():
    """Starts the application."""
    info("Start %s %s (%s)" % (NAME, VERSION, ENV))


if __name__ == "__main__":
    main()
