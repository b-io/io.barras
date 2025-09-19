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

from __future__ import annotations

from collections.abc import MutableSet

from nutil.scalar.common import *
from nutil.struct.collection.registry.common import *

####################################################################################################
# COMMON COLLECTION CONSTANTS
####################################################################################################

__COMMON_COLLECTION_CONSTANTS_____________________ = ""

ITERABLE_TYPE = Iterable

SEQUENCE_TYPE = Sequence


# • ARRAY ##########################################################################################

__COMMON_ARRAY_CONSTANTS__________________________ = ""

ARRAY_TYPE = np.ndarray


# • DICT ###########################################################################################

__COMMON_DICT_CONSTANTS___________________________ = ""

DICT_TYPE = dict


# • LIST ###########################################################################################

__COMMON_LIST_CONSTANTS___________________________ = ""

LIST_TYPE = list


# • SET ############################################################################################

__COMMON_SET_CONSTANTS____________________________ = ""

SET_TYPE = set

MUTABLE_SET_TYPE = MutableSet

####################################################################################################
# COMMON COLLECTION VERIFIERS
####################################################################################################

__COMMON_COLLECTION_VERIFIERS_____________________ = ""


def is_iterable(x):
    return isinstance(x, ITERABLE_TYPE)


def is_sequence(x):
    return isinstance(x, SEQUENCE_TYPE)


#########################


def is_collection(x: Any) -> bool:
    return is_abstract_collection(x) or is_array(x) or is_dict(x) or is_list(x) or is_set(x)


# • ARRAY ##########################################################################################

__COMMON_ARRAY_VERIFIERS__________________________ = ""


def is_array(x):
    return isinstance(x, ARRAY_TYPE)


# • DICT ###########################################################################################

__COMMON_DICT_VERIFIERS___________________________ = ""


def is_dict(x):
    return isinstance(x, DICT_TYPE)


# • LIST ###########################################################################################

__COMMON_LIST_VERIFIERS___________________________ = ""


def is_list(x):
    return isinstance(x, LIST_TYPE)


# • SET ############################################################################################

__COMMON_SET_VERIFIERS____________________________ = ""


def is_set(x):
    return isinstance(x, SET_TYPE)


def is_mutable_set(x):
    return isinstance(x, MUTABLE_SET_TYPE)
