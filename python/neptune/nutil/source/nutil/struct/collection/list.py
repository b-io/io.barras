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

from nutil.common import *


####################################################################################################
# LIST PROCESSORS
####################################################################################################

__LIST_PROCESSORS_________________________________ = ""


def filter_list(l, inclusion=None, exclusion=None):
    """Returns the values of the specified list that are in the specified inclusive list and are not
    in the specified exclusive list."""
    if is_empty(l):
        return []
    if not has_filter(inclusion=inclusion, exclusion=exclusion):
        return to_list(l)
    elif is_null(inclusion):
        return [v for v in l if v not in to_list(exclusion)]
    elif is_empty(exclusion):
        return [v for v in l if v in to_list(inclusion)]
    return [v for v in l if v in to_list(inclusion) and v not in to_list(exclusion)]


def include_list(l, inclusion):
    """Returns the values of the specified list that are in the specified inclusive list."""
    return filter_list(l, inclusion=inclusion)


def exclude_list(l, exclusion):
    """Returns the values of the specified list that are not in the specified exclusive list."""
    return filter_list(l, exclusion=exclusion)


#########################


def flatten_list(l, depth=-1):
    if is_empty(l):
        return []
    if not is_collection(l) or depth == 0:
        return to_list(l)
    elif depth == 1:
        return [v for sl in l for v in sl]
    fl = []
    for sl in l:
        fl += flatten_list(sl, depth=depth - 1)
    return fl


#########################


def mask_list(l, mask):
    """Returns the values of the specified list that are True in the specified mask."""
    return [v for i, v in enumerate(l) if mask[i]]


#########################


def resize_list(l, size, left=False, value=None):
    if len(l) < size:
        values = repeat(value, size - len(l))
        if left:
            return values + l
        return l + values
    return l[0:size]


#########################


def repeat(value, n):
    return n * [value]


#########################


def rotate_list(l, n=1):
    return l[-n:] + l[:-n]
