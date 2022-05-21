#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain graphical utility functions
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
# CONSTANTS
####################################################################################################

__CONSTANTS_______________________________________ = ''

__all__ = [s for s in dir() if not s.startswith('_')]
__version__ = '1.0.0.post132'

##################################################

NAME = 'ngui'
VERSION = __version__
DESCRIPTION = 'Graphical utility library'

####################################################################################################
# MAIN
####################################################################################################

__MAIN____________________________________________ = ''


def main():
	'''Starts the application.'''
	info('Start %s %s (%s)' % (NAME, VERSION, ENV))


if __name__ == '__main__':
	main()
