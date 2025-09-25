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

from typing import Any, Optional

####################################################################################################
# TUPLE CONSTANTS
####################################################################################################

__TUPLE_CONSTANTS_________________________________ = ""

TUPLE_TYPE = tuple

####################################################################################################
# TUPLE VERIFIERS
####################################################################################################

__TUPLE_VERIFIERS_________________________________ = ""


def is_tuple(x: Any):
    """Returns whether `x` is a `tuple`."""
    return isinstance(x, TUPLE_TYPE)


def is_valid_tuple(x: Any, size: Optional[int] = None) -> bool:
    """Returns whether `x` is a `tuple` and optionally whether it has the specified `size`."""
    if not is_tuple(x):
        return False
    elif size is not None and len(x) != size:
        return False
    return True
