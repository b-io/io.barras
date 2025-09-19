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

from nutil.struct.collection.common import *

####################################################################################################
# ARRAY GENERATORS
####################################################################################################

__ARRAY_GENERATORS________________________________ = ""


def create_random_array(*shape):
    return np.random.rand(*to_tuple(*shape))


def create_random_int_array(low, *shape, high=None):
    return np.random.randint(low, high=high, size=to_tuple(*shape), dtype=INT_ELEMENT_TYPE)


def create_random_long_array(low, *shape, high=None):
    return np.random.randint(low, high=high, size=to_tuple(*shape), dtype=LONG_ELEMENT_TYPE)


def create_random_short_array(low, *shape, high=None):
    return np.random.randint(low, high=high, size=to_tuple(*shape), dtype=SHORT_ELEMENT_TYPE)
