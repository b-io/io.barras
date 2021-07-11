#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain machine learning utility functions
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
# MACHINE LEARNING CONSTANTS
####################################################################################################

__MACHINE_LEARNING_CONSTANTS______________________ = ''

__all__ = [s for s in dir() if not s.startswith('_')]
__version__ = '1.0.0.post88'

##################################################

NAME = 'nutil.ml'
VERSION = __version__
DESCRIPTION = 'Machine learning functions'

####################################################################################################
# MACHINE LEARNING MAIN
####################################################################################################

__MACHINE_LEARNING_MAIN___________________________ = ''


def main():
	"""Starts the application."""
	info('Start %s %s (%s)' % (NAME, VERSION, ENV))


if __name__ == '__main__':
	main()
