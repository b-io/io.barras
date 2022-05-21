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
#    Copyright © 2013-2022 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from nutil.common import *

####################################################################################################
# ML CONSTANTS
####################################################################################################

__ML_CONSTANTS____________________________________ = ''

__all__ = [s for s in dir() if not s.startswith('_')]
__version__ = '1.0.0.post132'

##################################################

NAME = 'nlearn'
VERSION = __version__
DESCRIPTION = 'Machine learning functions'

####################################################################################################
# ML MAIN
####################################################################################################

__ML_MAIN_________________________________________ = ''


def main():
	'''Starts the application.'''
	info('Start %s %s (%s)' % (NAME, VERSION, ENV))


if __name__ == '__main__':
	main()
