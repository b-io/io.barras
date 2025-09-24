#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains common utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2025 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

####################################################################################################
# TUPLE CONSTANTS
####################################################################################################

__TUPLE_CONSTANTS_________________________________ = ""

TUPLE_TYPE = tuple


####################################################################################################
# TUPLE VERIFIERS
####################################################################################################

__TUPLE_VERIFIERS_________________________________ = ""


def is_tuple(x):
    """Returns whether `x` is a `tuple`."""
    return isinstance(x, TUPLE_TYPE)
