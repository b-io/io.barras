#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# Goal
#   Provide scalar converters.
##########################################################################################

from nutil.scalar.boolean import to_boolean
from nutil.scalar.bytes import to_bytes
from nutil.scalar.date import to_date, to_datetime, to_timestamp
from nutil.scalar.number import to_float, to_int
from nutil.scalar.string import *

## SCALAR CONVERTERS #####################################################################

__SCALAR_CONVERTERS_________________________________________ = ""


def to_scalar(x, t):
    if type(x) is t:
        return x
    if t is TIMESTAMP_TYPE:
        return to_timestamp(x)
    elif t is DATETIME_TYPE:
        return to_datetime(x)
    elif t is DATE_TYPE:
        return to_date(x)
    elif t is BOOLEAN_TYPE:
        return to_boolean(x)
    elif t is BYTES_TYPE:
        return to_bytes(x)
    elif t is FLOAT_TYPE:
        return to_float(x)
    elif t is INT_TYPE:
        return to_int(x)
    elif t is STRING_TYPE:
        return to_string(x)
    return x
