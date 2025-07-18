#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain common utility functions
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

from nutil.common.collections import (
    INT_ELEMENT_TYPE,
    LONG_ELEMENT_TYPE,
    SHORT_ELEMENT_TYPE,
    is_subscriptable_collection,
    np,
    to_list,
    to_tuple,
)

####################################################################################################
# COMMON ARRAY CONSTANTS
####################################################################################################

__COMMON_ARRAY_CONSTANTS__________________________ = ""

ARRAY_TYPE = np.ndarray

####################################################################################################
# COMMON ARRAY VERIFIERS
####################################################################################################

__ARRAY_VERIFIERS_________________________________ = ""


def is_array(x):
    return isinstance(x, ARRAY_TYPE)


####################################################################################################
# COMMON ARRAY CONVERTERS
####################################################################################################

__COMMON_ARRAY_CONVERTERS_________________________ = ""


def to_array(*args, type=None):
    if len(args) == 1:
        arg = args[0]
        if is_array(arg):
            return arg
        elif is_subscriptable_collection(arg):
            return np.array(arg, dtype=type)
    return np.array(to_list(*args), dtype=type)


def unarray(a):
    if is_array(a):
        if len(a) == 1:
            return a[0]
        return tuple(a)
    return a


####################################################################################################
# COMMON ARRAY GENERATORS
####################################################################################################

__COMMON_ARRAY_GENERATORS_________________________ = ""


def create_array(*shape, fill=0, order="C", type=None):
    return np.full(to_tuple(*shape), fill, dtype=type, order=order)


def create_random_array(*shape):
    return np.random.rand(*to_tuple(*shape))


def create_random_int_array(low, *shape, high=None):
    return np.random.randint(low, high=high, size=to_tuple(*shape), dtype=INT_ELEMENT_TYPE)


def create_random_long_array(low, *shape, high=None):
    return np.random.randint(low, high=high, size=to_tuple(*shape), dtype=LONG_ELEMENT_TYPE)


def create_random_short_array(low, *shape, high=None):
    return np.random.randint(low, high=high, size=to_tuple(*shape), dtype=SHORT_ELEMENT_TYPE)
