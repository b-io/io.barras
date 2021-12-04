#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain statistical utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2021 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from nutil.common import *

####################################################################################################
# STATS CONSTANTS
####################################################################################################

__STATS_CONSTANTS_________________________________ = ''

__all__ = [s for s in dir() if not s.startswith('_')]
__version__ = '1.0.0.post110'

##################################################

NAME = 'nutil.stats'
VERSION = __version__
DESCRIPTION = 'Statistical functions'

####################################################################################################
# STATS MAIN
####################################################################################################

__STATS_MAIN______________________________________ = ''


def main():
	"""Starts the application."""
	info('Start %s %s (%s)' % (NAME, VERSION, ENV))


if __name__ == '__main__':
	main()
