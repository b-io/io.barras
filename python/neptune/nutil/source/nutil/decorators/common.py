#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common decorators.
########################################################################################################################

from __future__ import annotations

from typing import Any, Callable, TypeVar

## COMMON DECORATORS #####################################################################

__COMMON_DECORATORS_________________________________________ = ""

F = TypeVar("F", bound=Callable[..., Any])


class classproperty:
    """Implements a read-only property evaluated on the class (not the instance)."""

    def __init__(self, fget):
        if not callable(fget):
            raise TypeError("'classproperty' expects a callable 'fget'")
        self.fget = fget

    def __get__(self, x: Any, owner=None):
        owner = owner if owner is not None else type(x)
        return self.fget(owner)
