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

from nutil.common.collections import has_filter, is_collection, is_dict, is_empty, is_null, to_set

####################################################################################################
# COMMON LIST CONSTANTS
####################################################################################################

__COMMON_LIST_CONSTANTS___________________________ = ""

LIST_TYPE = list

####################################################################################################
# COMMON LIST VERIFIERS
####################################################################################################

__COMMON_LIST_VERIFIERS___________________________ = ""


def is_list(x):
    return isinstance(x, LIST_TYPE)


####################################################################################################
# COMMON LIST CONVERTERS
####################################################################################################

__COMMON_LIST_CONVERTERS__________________________ = ""


def to_list(*args):
    if len(args) == 1:
        arg = args[0]
        if is_list(arg):
            return arg
        elif is_collection(arg):
            return list(arg if not is_dict(arg) else arg.values())
        return [arg]
    return list(args)


def unlist(l):
    if is_list(l):
        if len(l) == 1:
            return l[0]
        return tuple(l)
    return l


####################################################################################################
# COMMON LIST PROCESSORS
####################################################################################################

__COMMON_LIST_PROCESSORS__________________________ = ""


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


def find_all(l, value):
    return find_all_with(l, lambda v: v == value)


def find_all_not(l, value):
    return find_all_not_with(l, lambda v: v == value)


def find_all_in(l, values):
    values = to_set(values)
    return find_all_with(l, lambda v: v in values)


def find_all_not_in(l, values):
    values = to_set(values)
    return find_all_not_with(l, lambda v: v in values)


def find_all_with(l, f, *args, **kwargs):
    return [i for i in range(len(l)) if f(l[i], *args, **kwargs)]


def find_all_not_with(l, f, *args, **kwargs):
    return [i for i in range(len(l)) if not f(l[i], *args, **kwargs)]


#########################


def find(l, value):
    return find_with(l, lambda v: v == value)


def find_not(l, value):
    return find_not_with(l, lambda v: v == value)


def find_in(l, values):
    values = to_set(values)
    return find_with(l, lambda v: v in values)


def find_not_in(l, values):
    values = to_set(values)
    return find_not_with(l, lambda v: v in values)


def find_with(l, f, *args, **kwargs):
    return next((i for i in range(len(l)) if f(l[i], *args, **kwargs)), None)


def find_not_with(l, f, *args, **kwargs):
    return next((i for i in range(len(l)) if not f(l[i], *args, **kwargs)), None)


#########################


def find_last(l, value):
    return find_last_with(l, lambda v: v == value)


def find_last_not(l, value):
    return find_last_not_with(l, lambda v: v == value)


def find_last_in(l, values):
    values = to_set(values)
    return find_last_with(l, lambda v: v in values)


def find_last_not_in(l, values):
    values = to_set(values)
    return find_last_not_with(l, lambda v: v in values)


def find_last_with(l, f, *args, **kwargs):
    return len(l) - find_with(l[::-1], f, *args, **kwargs) - 1


def find_last_not_with(l, f, *args, **kwargs):
    return len(l) - find_not_with(l[::-1], f, *args, **kwargs) - 1


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
