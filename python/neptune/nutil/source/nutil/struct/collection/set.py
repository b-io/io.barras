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

from nutil.common import *

## SET PROCESSORS ########################################################################

__SET_PROCESSORS____________________________________________ = ""


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
