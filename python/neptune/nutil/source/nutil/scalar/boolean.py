#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide boolean utilities.
########################################################################################################################

from __future__ import annotations

from distutils.util import strtobool

from nutil.common import *

__BOOLEAN_CONVERTERS______________________________________________________________________ = ""


def to_boolean(x: Any):
    if is_null(x):
        return NAN
    elif is_struct(x):
        if has_callable(x, "astype"):
            return x.astype(BOOLEAN_ELEMENT_TYPE)
        from nutil.struct.util import apply

        return apply(x, to_boolean)
    elif is_string(x):
        return bool(strtobool(x))
    return bool(x)
