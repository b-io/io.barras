#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain common machine learning utility functions
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

from nutil.math import *

####################################################################################################
# ML COMMON FUNCTIONS
####################################################################################################

__ML_COMMON_______________________________________ = ''


def softmax(x):
	"""Returns the softmax values for every set of scores in x."""
	e_x = np.exp(x - np.max(x))
	return e_x / e_x.sum()
