#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains common decorators
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

from __future__ import annotations

from typing import Any

## COMMON DECORATORS #####################################################################

__COMMON_DECORATORS_________________________________________ = ""


class classproperty:
    """Implements a read-only property evaluated on the class (not the instance)."""

    def __init__(self, fget):
        if not callable(fget):
            raise TypeError("'classproperty' expects a callable 'fget'")
        self.fget = fget

    def __get__(self, x: Any, owner=None):
        owner = owner if owner is not None else type(x)
        return self.fget(owner)
