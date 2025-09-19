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

from nutil.struct.collection.common import *
from nutil.struct.table.common import *
from nutil.struct.tuple.common import *

####################################################################################################
# COMMON STRUCT CONSTANTS
####################################################################################################

__COMMON_STRUCT_CONSTANTS_____________________ = ""

ITERABLE_TYPE = Iterable

SEQUENCE_TYPE = Sequence


# • ARRAY ##########################################################################################

__COMMON_ARRAY_CONSTANTS__________________________ = ""

ARRAY_TYPE = np.ndarray


# • DATAFRAME ######################################################################################

__COMMON_DATAFRAME_CONSTANTS______________________ = ""

SERIES_TYPE = pd.Series
SERIES_GROUP_BY_TYPE = SeriesGroupBy

FRAME_TYPE = pd.DataFrame
FRAME_GROUP_BY_TYPE = DataFrameGroupBy

#########################

INDEX_TYPE = pd.Index
TIME_INDEX_TYPE = pd.DatetimeIndex


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
# COMMON STRUCT VERIFIERS
####################################################################################################

__COMMON_STRUCT_VERIFIERS_____________________ = ""


def is_struct(x):
    return is_collection(x) or is_table(x) or is_tuple(x)


#########################


def is_multidimensional(x):
    return is_table(x) or is_array(x)


def is_subscriptable(x):
    return hasattr(x, "__getitem__")


##################################################


def has_index(c):
    return is_array(c) or is_index(c) or is_sequence(c)


#########################


def compare_length(x: Any, n: int, op) -> bool:
    """
    Compares the length of a collection to a specified number using the given operator.
    Returns False if x is not a valid collection or has no length.
    """
    if not is_struct(x):
        return False
    try:
        return op(len(x), n)
    except TypeError:
        return False


def has_length_ge(x: Any, n: int = 1) -> bool:
    """Returns True if len(x) >= n and x is a collection."""
    return compare_length(x, n, operator.ge)


def has_length_gt(x: Any, n: int = 1) -> bool:
    """Returns True if len(x) > n and x is a collection."""
    return compare_length(x, n, operator.gt)


def has_length_le(x: Any, n: int = 1) -> bool:
    """Returns True if len(x) <= n and x is a collection."""
    return compare_length(x, n, operator.le)


def has_length_lt(x: Any, n: int = 1) -> bool:
    """Returns True if len(x) < n and x is a collection."""
    return compare_length(x, n, operator.lt)


####################################################################################################
# COMMON STRUCT ACCESSORS
####################################################################################################

__COMMON_STRUCT_ACCESSORS_________________________ = ""
