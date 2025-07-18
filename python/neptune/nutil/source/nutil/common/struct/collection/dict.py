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
#    Copyright © 2013-2025 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

####################################################################################################
# COMMON DICT CONSTANTS
####################################################################################################

__COMMON_DICT_CONSTANTS___________________________ = ""

DICT_TYPE = dict

####################################################################################################
# COMMON DICT VERIFIERS
####################################################################################################

__COMMON_DICT_VERIFIERS___________________________ = ""


def is_dict(x):
    return isinstance(x, DICT_TYPE)


####################################################################################################
# COMMON DICT CONVERTERS
####################################################################################################

__COMMON_DICT_CONVERTERS__________________________ = ""


def to_dict(c):
    """Converts the specified collection to a dictionary."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c):
        return {}
    elif is_table(c):
        return c.to_dict()
    elif is_dict(c):
        return c
    return {i: v for i, v in enumerate(c)}
