#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide formatting utilities for structures.
########################################################################################################################

from __future__ import annotations

from nutil.common import *
from nutil.scalar.string import BULLET, COLON, NEWLINE


__STRUCT_FORMATTER_PROCESSORS_____________________________________________________________ = ""


def format_bulleted_value(value):
    return collapse(NEWLINE, BULLET, " ", round(value) if is_number(value) else value)


def format_bulleted_list(l, f=format_bulleted_value):
    return collapse([f(v) for v in l])


##############################


def format_bulleted_item(key, value):
    return format_bulleted_value(collapse(key, COLON, " ", round(value) if is_number(value) else value))


def format_bulleted_dict(d, f=format_bulleted_item):
    return collapse([f(k, v) for k, v in d.items()])
