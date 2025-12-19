#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common collection utilities.
########################################################################################################################

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

__COMMON_COLLECTION_CONSTANTS_____________________________________________________________ = ""

COLLECTION_TYPE = ABCCollection

ITERABLE_TYPE = ABCIterable

SEQUENCE_TYPE = ABCSequence
MUTABLE_SEQUENCE_TYPE = ABCMutableSequence


__COMMON_ARRAY_CONSTANTS____________________________________ = ""

ARRAY_TYPE = np.ndarray


__COMMON_DICT_CONSTANTS_____________________________________ = ""

DICT_TYPE = dict


__COMMON_LIST_CONSTANTS_____________________________________ = ""

LIST_TYPE = list


__COMMON_SET_CONSTANTS______________________________________ = ""

SET_TYPE = ABCSet
FROZENSET_TYPE = frozenset
MUTABLE_SET_TYPE = ABCMutableSet


__COMMON_COLLECTION_ACCESSORS_____________________________________________________________ = ""


def peek(iterable: Iterable[Any]) -> Tuple[bool, Optional[Any], Iterator[Any]]:
    """
    Returns a `tuple` `(has_item, first_or_none, iterator)` without consuming the first element.
    """
    it1, it2 = itertools.tee(create_iterator(iterable), 2)
    try:
        return True, next(it1), it2
    except StopIteration:
        return False, None, it2


__COMMON_COLLECTION_VALIDATORS____________________________________________________________ = ""


def is_element(x: Any) -> bool:
    """Returns whether `x` is an element."""
    return not isinstance(x, ITERABLE_TYPE) or is_byte_like(x) or is_string(x)


def is_element_type(t: Type[Any]) -> bool:
    """Returns whether `t` is an element type."""
    return not issubclass(t, ITERABLE_TYPE) or is_byte_like_type(t) or is_string_type(t)


##############################


def is_collection(x: Any) -> bool:
    """
    Returns whether `x` is a generic collection (1-D container) excluding:
        • byte-like types (`bytes`, `bytearray`, `memoryview`)
        • `str` (treated as scalar text)
        • `tuple` (treated as an atomic element)
    """
    return isinstance(x, COLLECTION_TYPE) and not is_element(x)


def is_collection_type(t: Type[Any]) -> bool:
    """
    Returns whether `t` is a generic collection (1-D container) type excluding:
        • byte-like types (`bytes`, `bytearray`, `memoryview`)
        • `str` (treated as scalar text)
        • `tuple` (treated as an atomic element)
    """
    return issubclass(t, COLLECTION_TYPE) and not is_element_type(t)


##############################


def is_iterable(x: Any) -> bool:
    """
    Returns whether `x` is an `Iterable` excluding:
        • byte-like types (`bytes`, `bytearray`, `memoryview`)
        • `str` (treated as scalar text)
    """
    return isinstance(x, ITERABLE_TYPE) and not is_element(x)


def is_iterable_type(t: Type[Any]) -> bool:
    """
    Returns whether `t` is an `Iterable` type excluding:
        • byte-like types (`bytes`, `bytearray`, `memoryview`)
        • `str` (treated as scalar text)
    """
    return issubclass(t, ITERABLE_TYPE) and not is_element_type(t)


def is_iterable_of_tuples(x: Any, size: Optional[int] = None, check_all: bool = False) -> bool:
    """
    Returns whether `x` is an `Iterable` of `tuple` (optionally of a fixed `size`).

    Notes:
        • Empty iterables are considered `True`.
        • If `check_all` is `False` (default), only the first element is checked (O(1)).
        • If `check_all` is `True`, all elements are checked (O(n)).
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


##############################


def is_sequence(x: Any) -> bool:
    """Returns whether `x` is a `Sequence`."""
    return isinstance(x, SEQUENCE_TYPE) and not is_element(x)


def is_sequence_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `Sequence` type."""
    return issubclass(t, SEQUENCE_TYPE) and not is_element_type(t)


def is_mutable_sequence(x: Any) -> bool:
    """Returns whether `x` is a `MutableSequence`."""
    return isinstance(x, MUTABLE_SEQUENCE_TYPE) and not is_element(x)


def is_mutable_sequence_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `MutableSequence` type."""
    return issubclass(t, MUTABLE_SEQUENCE_TYPE) and not is_element_type(t)


__COMMON_ARRAY_VALIDATORS___________________________________ = ""


def is_array(x: Any) -> bool:
    """Returns whether `x` is a NumPy `ndarray`."""
    return isinstance(x, ARRAY_TYPE)


def is_array_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a NumPy `ndarray` type."""
    return issubclass(t, ARRAY_TYPE)


__COMMON_DICT_VALIDATORS____________________________________ = ""


def is_dict(x: Any) -> bool:
    """Returns whether `x` is a `dict`."""
    return isinstance(x, DICT_TYPE)


def is_dict_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `dict` type."""
    return issubclass(t, DICT_TYPE)


__COMMON_LIST_VALIDATORS____________________________________ = ""


def is_list(x: Any) -> bool:
    """Returns whether `x` is a `list`."""
    return isinstance(x, LIST_TYPE)


def is_list_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `list` type."""
    return issubclass(t, LIST_TYPE)


__COMMON_SET_VALIDATORS_____________________________________ = ""


def is_set(x: Any) -> bool:
    """Returns whether `x` is a `Set` (including `set` and `frozenset`)."""
    return isinstance(x, SET_TYPE)


def is_set_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `Set` type (including `set` and `frozenset`)."""
    return issubclass(t, SET_TYPE)


def is_frozen_set(x: Any) -> bool:
    """Returns whether `x` is a `frozenset`."""
    return isinstance(x, FROZENSET_TYPE)


def is_frozen_set_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `frozenset` type."""
    return issubclass(t, FROZENSET_TYPE)


def is_mutable_set(x: Any) -> bool:
    """Returns whether `x` is a `MutableSet` (including `set`)."""
    return isinstance(x, MUTABLE_SET_TYPE)


def is_mutable_set_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `MutableSet` type (including `set`)."""
    return issubclass(t, MUTABLE_SET_TYPE)
