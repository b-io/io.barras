#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide profile utilities.
########################################################################################################################

from __future__ import annotations

import cProfile
import pstats
from io import StringIO
from pstats import SortKey

__COMMON_PROFILE_PROCESSORS_______________________________________________________________ = ""


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
