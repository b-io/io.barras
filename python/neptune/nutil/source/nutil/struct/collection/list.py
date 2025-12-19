#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide collection utilities for lists.
########################################################################################################################

from __future__ import annotations

from nutil.common import *

__LIST_PROCESSORS_________________________________________________________________________ = ""


def deduplicate(items: List[Any]) -> List[Any]:
    """
    Removes the duplicate `items` while preserving their original order.

    Args:
        items: The list of items to deduplicate.

    Returns:
        The deduplicated list with preserved order.
    """
    out: List[Any] = []
    seen: Set[Any] = set()

    for item in items:
        try:
            is_already_seen = item in seen
        except TypeError as e:
            raise TypeError(f"Item {item!r} is not hashable") from e
        if not is_already_seen:
            out.append(item)
            seen.add(item)
    return out


##############################


def filter_list(l, inclusion=None, exclusion=None):
    """
    Returns the values of the specified `list` that are in the specified inclusive `list` and are
    not in the specified exclusive `list`.
    """
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
    """Returns the values of the specified `list` that are in the specified inclusive `list`."""
    return filter_list(l, inclusion=inclusion)


def exclude_list(l, exclusion):
    """Returns the values of the specified `list` that are not in the specified exclusive `list`."""
    return filter_list(l, exclusion=exclusion)


##############################


def flatten_list(l, depth=-1):
    if is_empty(l):
        return []
    if is_element(l) or depth == 0:
        return to_list(l)
    elif depth == 1:
        return [v for sl in l for v in sl]
    fl = []
    for sl in l:
        fl += flatten_list(sl, depth=depth - 1)
    return fl


##############################


def mask_list(l, mask):
    """Returns the values of the specified `list` that are `True` in the specified mask."""
    return [v for i, v in enumerate(l) if mask[i]]


##############################


def resize_list(l, size, left=False, value=None):
    if len(l) < size:
        values = repeat(value, size - len(l))
        if left:
            return values + l
        return l + values
    return l[0:size]


##############################


def repeat(value, n):
    return n * [value]


##############################


def rotate_list(l, n=1):
    return l[-n:] + l[:-n]
