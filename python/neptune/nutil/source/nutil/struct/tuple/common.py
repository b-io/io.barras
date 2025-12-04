#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# Goal
#   Provide common utilities.
##########################################################################################

from typing import Any, Optional, Type

## COMMON TUPLE CONSTANTS ################################################################

__COMMON_TUPLE_CONSTANTS____________________________________ = ""

TUPLE_TYPE = tuple


## COMMON TUPLE VERIFIERS ################################################################

__COMMON_TUPLE_VERIFIERS____________________________________ = ""


def is_tuple(x: Any) -> bool:
    """Returns whether `x` is a `tuple`."""
    return isinstance(x, TUPLE_TYPE)


def is_tuple_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `tuple` type."""
    return issubclass(t, TUPLE_TYPE)


def is_valid_tuple(x: Any, size: Optional[int] = None) -> bool:
    """Returns whether `x` is a `tuple` and optionally of the specified `size`."""
    if not is_tuple(x):
        return False
    elif size is not None and len(x) != size:
        return False
    return True
