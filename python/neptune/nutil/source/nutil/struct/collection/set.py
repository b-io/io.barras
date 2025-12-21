#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide collection utilities for sets.
########################################################################################################################

from __future__ import annotations

from nutil.common import *

__SET_FILTERS_____________________________________________________________________________ = ""


def filter_set(s, inclusion=None, exclusion=None):
    """
    Returns the values of the specified set that are in the specified inclusive set and are not in
    the specified exclusive set.
    """
    if is_empty(s):
        return set()
    if not has_filter(inclusion=inclusion, exclusion=exclusion):
        return to_set(s)
    elif is_null(inclusion):
        return to_set(s) - to_set(exclusion)
    elif is_empty(exclusion):
        return to_set(s) & to_set(inclusion)
    return to_set(s) & to_set(inclusion) - to_set(exclusion)


def include_set(s, inclusion):
    """Returns the values of the specified set that are in the specified inclusive set."""
    return filter_set(s, inclusion=inclusion)


def exclude_set(s, exclusion):
    """Returns the values of the specified set that are not in the specified exclusive set."""
    return filter_set(s, exclusion=exclusion)
