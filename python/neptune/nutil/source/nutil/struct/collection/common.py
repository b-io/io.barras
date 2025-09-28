#!/usr/bin/env python
####################################################################################################
# NAME
#   <NAME> - contains common utility functions
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from __future__ import annotations

from collections.abc import (
    Collection as ABCCollection,
    Iterable as ABCIterable,
    MutableSequence as ABCMutableSequence,
    MutableSet as ABCMutableSet,
    Sequence as ABCSequence,
    Set as ABCSet,
)

from nutil.scalar.common import *
from nutil.struct.collection.registry.common import *
from nutil.struct.tuple.common import *

####################################################################################################
# COMMON COLLECTION CONSTANTS
####################################################################################################

__COMMON_COLLECTION_CONSTANTS_____________________ = ""

COLLECTION_TYPE = ABCCollection

ITERABLE_TYPE = ABCIterable

SEQUENCE_TYPE = ABCSequence
MUTABLE_SEQUENCE_TYPE = ABCMutableSequence

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

SET_TYPE = ABCSet
FROZENSET_TYPE = frozenset
MUTABLE_SET_TYPE = ABCMutableSet

####################################################################################################
# COMMON COLLECTION ACCESSORS
####################################################################################################

__COMMON_COLLECTION_ACCESSORS_____________________ = ""


def peek(iterable: Iterable[Any]) -> Tuple[bool, Optional[Any], Iterator[Any]]:
    """
    Returns a `tuple` `(has_item, first_or_none, iterator)` without consuming the first element.
    """
    it1, it2 = itertools.tee(iter(iterable), 2)
    try:
        return True, next(it1), it2
    except StopIteration:
        return False, None, it2


####################################################################################################
# COMMON COLLECTION VERIFIERS
####################################################################################################

__COMMON_COLLECTION_VERIFIERS_____________________ = ""


def is_collection(x: Any) -> bool:
    """
    Returns whether `x` is a generic collection (1-D container) excluding:
        • byte-like types (`bytes`, `bytearray`, `memoryview`)
        • `str` (treated as scalar text)
        • `tuple` (treated as an atomic element)
    """
    return (
        isinstance(x, COLLECTION_TYPE)
        and not is_byte_like(x)
        and not is_string(x)
        and not is_tuple(x)
    )


#########################


def is_iterable(x: Any) -> bool:
    """
    Returns whether `x` is an `Iterable` excluding:
        • byte-like types (`bytes`, `bytearray`, `memoryview`)
        • `str` (treated as scalar text)
    """
    return isinstance(x, ITERABLE_TYPE) and not is_byte_like(x) and not is_string(x)


def is_iterable_of_tuples(x: Any, size: Optional[int] = None, check_all: bool = False) -> bool:
    """
    Returns whether `x` is an `Iterable` of `tuple` (optionally of a fixed `size`).

    Notes:
        • Empty iterables are considered `True`.
        • If `check_all` is False (default), only the first element is checked (O(1)).
        • If `check_all` is True, all elements are checked (O(n)).
        • Relies on `is_iterable` (which excludes `str`/`bytes`/etc.).
    """
    if not is_iterable(x):
        return False

    # Check if the `Iterable` is empty
    has_item, first_item, it = peek(x)
    if not has_item:
        return True

    # Check if the first `tuple` matches the expected size
    if not is_valid_tuple(first_item, size=size):
        return False

    # Check if every other `tuple` matches the expected size (if requested)
    if check_all:
        return all(is_valid_tuple(item, size=size) for item in it)

    return True


#########################


def is_sequence(x: Any) -> bool:
    """Returns whether `x` is a `Sequence`."""
    return isinstance(x, SEQUENCE_TYPE)


def is_mutable_sequence(x: Any) -> bool:
    """Returns whether `x` is a `MutableSequence`."""
    return isinstance(x, MUTABLE_SEQUENCE_TYPE)


# • ARRAY ##########################################################################################

__COMMON_ARRAY_VERIFIERS__________________________ = ""


def is_array(x: Any) -> bool:
    """Returns whether `x` is a NumPy `ndarray`."""
    return isinstance(x, ARRAY_TYPE)


# • DICT ###########################################################################################

__COMMON_DICT_VERIFIERS___________________________ = ""


def is_dict(x: Any) -> bool:
    """Returns whether `x` is a `dict`."""
    return isinstance(x, DICT_TYPE)


# • LIST ###########################################################################################

__COMMON_LIST_VERIFIERS___________________________ = ""


def is_list(x: Any) -> bool:
    """Returns whether `x` is a `list`."""
    return isinstance(x, LIST_TYPE)


# • SET ############################################################################################

__COMMON_SET_VERIFIERS____________________________ = ""


def is_set(x: Any) -> bool:
    """Returns whether `x` is a `Set` (including `set` and `frozenset`)."""
    return isinstance(x, SET_TYPE)


def is_frozen_set(x: Any) -> bool:
    """Returns whether `x` is a `frozenset`."""
    return isinstance(x, FROZENSET_TYPE)


def is_mutable_set(x: Any) -> bool:
    """Returns whether `x` is a `MutableSet` (including `set`)."""
    return isinstance(x, MUTABLE_SET_TYPE)
