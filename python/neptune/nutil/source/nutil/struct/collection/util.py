#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains collection converters
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

from __future__ import annotations

from nutil.struct.collection.registry.ordered_set import *

####################################################################################################
# COLLECTION CONVERTERS
####################################################################################################

__COLLECTION_CONVERTERS___________________________ = ""


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
