#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain mathematical utility functions
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
# MATH CONSTANTS
####################################################################################################

__MATH_CONSTANTS__________________________________ = ''

__all__ = [s for s in dir() if not s.startswith('_')]
__version__ = '1.0.0.post132'

##################################################

NAME = 'nmath'
VERSION = __version__
DESCRIPTION = 'Mathematical functions'

####################################################################################################
# MATH MAIN
####################################################################################################

__MATH_MAIN_______________________________________ = ''


def main():
	'''Starts the application.'''
	info('Start %s %s (%s)' % (NAME, VERSION, ENV))


if __name__ == '__main__':
	main()
