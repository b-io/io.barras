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

import cProfile
import pstats
from io import StringIO
from pstats import SortKey


## COMMON PROFILE PROCESSORS #############################################################

__COMMON_PROFILE_PROCESSORS_________________________________ = ""


def start_profile():
    """Starts profiling."""
    profile = cProfile.Profile()
    profile.enable()
    return profile


def end_profile(profile, stream=StringIO()):
    """Ends profiling and returns the stream."""
    profile.disable()
    pstats.Stats(profile, stream=stream).sort_stats(SortKey.CUMULATIVE).print_stats()
    return stream
