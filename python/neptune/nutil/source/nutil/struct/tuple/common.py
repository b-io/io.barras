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
    return isinstance(x, TUPLE_TYPE)


####################################################################################################
# TUPLE CONVERTERS
####################################################################################################

__TUPLE_CONVERTERS________________________________ = ""


def to_tuple(*args):
    """
    Returns a `tuple` from the specified arguments.

    • Single argument:
      – If already a `tuple`, returns it unchanged.
      – If a `dict`, returns its values as a `tuple`.
      – If another iterable (excluding `bytes` or `str`), converts it to a `tuple`.
      – Otherwise, wraps it in a single-element `tuple`.
    • Multiple arguments: packs them into a `tuple`.
    """
    if len(args) == 1:
        arg = args[0]
        if isinstance(arg, tuple):
            return arg
        if isinstance(arg, dict):
            return tuple(arg.values())
        if isinstance(arg, (bytes, str)):
            return (arg,)
        try:
            return tuple(arg)
        except TypeError:
            return (arg,)
    return tuple(args)
