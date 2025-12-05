#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# COMMON CACHING #######################################################################################################
# Goal
#   Provide common caching utilities.
########################################################################################################################

from __future__ import annotations

from typing import List

from nutil.enums import StrEnum

## COMMON CACHING CLASSES ################################################################

__COMMON_CACHING_CLASSES____________________________________ = ""


class CachePolicy(StrEnum):
    """
    An enum that selects the cache write behavior (JSON-serializable as strings).

    Values:
        `"read_only"`: Never mutates the cache; no network writes.
        `"write_miss_only"`: Adds the new keys on cache misses only (default-safe).
        `"overwrite"`: May replace the existing keys.
    """

    READ_ONLY: str = "read_only"
    WRITE_MISS_ONLY: str = "write_miss_only"
    OVERWRITE: str = "overwrite"
