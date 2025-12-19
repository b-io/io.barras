#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide collection utilities for arrays.
########################################################################################################################

from __future__ import annotations

from nutil.common import *


__ARRAY_GENERATORS________________________________________________________________________ = ""


def create_array(*shape, fill=0, order="C", element_type: Optional[ElementType] = None):
    return np.full(to_tuple(*shape), fill, dtype=element_type, order=order)


############################################################


def create_random_array(*shape):
    return np.random.rand(*to_tuple(*shape))


def create_random_int_array(low, *shape, high=None):
    return np.random.randint(low, high=high, size=to_tuple(*shape), dtype=INT_ELEMENT_TYPE)


def create_random_long_array(low, *shape, high=None):
    return np.random.randint(low, high=high, size=to_tuple(*shape), dtype=LONG_ELEMENT_TYPE)


def create_random_short_array(low, *shape, high=None):
    return np.random.randint(low, high=high, size=to_tuple(*shape), dtype=SHORT_ELEMENT_TYPE)
