#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains struct converters
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

from nutil.scalar.converters import *

from nutil.common import *

####################################################################################################
# COLLECTION CONVERTERS
####################################################################################################

__COLLECTION_CONVERTERS___________________________ = ""


def to_collection(*args):
    if len(args) == 1:
        arg = args[0]
        if is_collection(arg):
            return arg
        return [arg]
    return to_tuple(*args)


def to_indexed_collection(*args):
    if len(args) == 1:
        arg = args[0]
        if is_collection(arg) and has_index(arg):
            return arg
        return [arg]
    return to_tuple(*args)


def to_subscriptable_collection(*args):
    if len(args) == 1:
        arg = args[0]
        if is_subscriptable(arg):
            return arg
        return [arg]
    return to_tuple(*args)


def uncollect(c):
    if is_collection(c):
        if len(c) == 1:
            return get_next(c)
        return tuple(c)
    return c


#########################


def collection_to_type(c, template):
    if is_frame(template):
        return to_frame(c, names=template, index=template)
    elif is_series(template):
        return to_series(c, name=template, index=template)
    elif is_dict(template):
        return dict(zip(get_keys(template), c))
    elif is_ordered_set(template):
        return to_ordered_set(c)
    elif is_set(template):
        return to_set(c)
    elif is_array(template):
        return to_array(c)
    elif is_list(template):
        return to_list(c)
    return c


def collection_to_common_type(c, template):
    if is_frame(template):
        return to_frame(c)
    elif is_series(template):
        return to_series(c)
    elif is_dict(template):
        return to_dict(c)
    elif is_ordered_set(template):
        return to_ordered_set(c)
    elif is_set(template):
        return to_set(c)
    elif is_array(template):
        return to_array(c)
    elif is_list(template):
        return to_list(c)
    return c


# • ARRAY ##########################################################################################

__COMMON_ARRAY_CONVERTERS_________________________ = ""


def unarray(a):
    if is_array(a):
        if len(a) == 1:
            return a[0]
        return tuple(a)
    return a


# • LIST ###########################################################################################

__COMMON_LIST_CONVERTERS__________________________ = ""


def unlist(l):
    if is_list(l):
        if len(l) == 1:
            return l[0]
        return tuple(l)
    return l


# • SET ############################################################################################

__COMMON_SET_CONVERTERS___________________________ = ""


def unset(s):
    if is_set(s):
        if len(s) == 1:
            return get_next(s)
        return tuple(s)
    return s
