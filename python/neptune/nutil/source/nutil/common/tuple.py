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

####################################################################################################
# COMMON TUPLE CONSTANTS
####################################################################################################

__COMMON_TUPLE_CONSTANTS__________________________ = ""

TUPLE_TYPE = tuple

####################################################################################################
# COMMON TUPLE VERIFIERS
####################################################################################################

__COMMON_TUPLE_VERIFIERS__________________________ = ""


def is_tuple(x):
    return isinstance(x, TUPLE_TYPE)


####################################################################################################
# COMMON TUPLE CONVERTERS
####################################################################################################

__COMMON_TUPLE_CONVERTERS_________________________ = ""


def to_tuple(*args):
    if len(args) == 1:
        arg = args[0]
        if is_tuple(arg):
            return arg
        elif is_collection(arg):
            return tuple(arg if not is_dict(arg) else arg.values())
        return (arg,)
    return tuple(args)
