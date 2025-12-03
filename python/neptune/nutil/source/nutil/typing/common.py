#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# NAME
#   <NAME> - contains common typing utilities
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

from __future__ import annotations

from typing import Callable

from nutil.struct.collection.common import *

## COMMON TYPING ACCESSORS ###############################################################

__COMMON_TYPING_ACCESSORS___________________________________ = ""


def get_type_name(t: Type[Any]) -> str:
    """Returns the simple type name for a `type`, or the instance type name for values."""
    return t.__name__ if isinstance(t, type) else type(t).__name__


def get_type_names(ts: Iterable[Any], separator: str = ", ") -> str:
    """Returns the simple type names of the specified types joined by the specified `separator`."""
    return separator.join(get_type_name(t) for t in ts)


## COMMON TYPING VERIFIERS ###############################################################

__COMMON_TYPING_VERIFIERS___________________________________ = ""


def assert_element_types(
    name: str,
    value: Any,
    allowed_types: Tuple[Type[Any], ...],
    *,
    collection_predicate: Optional[Callable[[Any], bool]] = None,
) -> None:
    """
    Verifies that the `value` is a scalar instance of one of `allowed_types`. Collections are rejected.

    Dispatch:
        • If `value` is a generic collection (default excludes str/bytes/bytearray/memoryview),
          raises `TypeError`.
        • Otherwise, if `allowed_types` is non-empty, `value` must be an instance of one of them.

    Complexity:
        O(len(allowed_types)) `isinstance` checks.
    """
    is_allowed_collection = collection_predicate or is_collection
    if is_allowed_collection(value):
        scalars = get_type_names(allowed_types)
        raise TypeError(
            f"'{name}' must be a scalar instance of {{{scalars}}}; got {type(value).__name__}"
            if scalars
            else f"'{name}' must be a scalar; got {type(value).__name__}"
        )
    if allowed_types and not isinstance(value, allowed_types):
        scalars = get_type_names(allowed_types)
        raise TypeError(
            f"'{name}' must be an instance of {{{scalars}}}; got {type(value).__name__}"
        )


def assert_types(
    name: str,
    value: Any,
    allowed_types: Tuple[Type[Any], ...],
    *,
    allowed_collection_types: Tuple[Type[Any], ...] = (),
    collection_predicate: Optional[Callable[[Any], bool]] = None,
) -> None:
    """
    Verifies that the `value` conforms to the allowed scalar/container types.

    Dispatch:
        • Returns if `value` is an instance of any type in `allowed_types` or
          `allowed_collection_types`.
        • If `value` is a generic collection (default excludes str/bytes/bytearray/memoryview),
          it MUST be an instance of one of `allowed_collection_types`, otherwise raises `TypeError`.
        • If `value` is a scalar and `allowed_types` is non-empty, it MUST be an instance of one of
          `allowed_types`, otherwise raises `TypeError`.

    Complexity:
        O(len(allowed_types) + len(allowed_collection_types)) `isinstance` checks.
    """
    for t in allowed_types + allowed_collection_types:
        if isinstance(value, t):
            return

    is_allowed_collection = collection_predicate or is_collection
    if is_allowed_collection(value):
        if allowed_collection_types:
            raise TypeError(
                f"'{name}' must be one of {{{get_type_names(allowed_collection_types)}}}; "
                f"got {type(value).__name__}"
            )
        raise TypeError(f"'{name}' must be a scalar; got {type(value).__name__}")

    if allowed_types and not isinstance(value, allowed_types):
        raise TypeError(
            f"'{name}' must be an instance of {{{get_type_names(allowed_types)}}}; "
            f"got {type(value).__name__}"
        )
