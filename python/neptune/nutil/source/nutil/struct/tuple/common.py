#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common tuple utilities.
########################################################################################################################

from __future__ import annotations

from typing import Any, Optional, Type


__COMMON_TUPLE_CONSTANTS__________________________________________________________________ = ""

TUPLE_TYPE = tuple


__COMMON_TUPLE_VALIDATORS_________________________________________________________________ = ""


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
