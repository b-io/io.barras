#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# NAME
#   <NAME> - contains common utilities
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

from typing import Any, Optional

## COMMON TUPLE CONSTANTS ################################################################

__COMMON_TUPLE_CONSTANTS____________________________________ = ""

TUPLE_TYPE = tuple


## COMMON TUPLE VERIFIERS ################################################################

__COMMON_TUPLE_VERIFIERS____________________________________ = ""


def is_tuple(x: Any):
    """Returns whether `x` is a `tuple`."""
    return isinstance(x, TUPLE_TYPE)


def is_valid_tuple(x: Any, size: Optional[int] = None) -> bool:
    """Returns whether `x` is a `tuple` and optionally of the specified `size`."""
    if not is_tuple(x):
        return False
    elif size is not None and len(x) != size:
        return False
    return True
