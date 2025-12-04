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

    @classmethod
    def from_value_or_name(cls, value: str) -> "CachePolicy":
        """Parses a member by value, then by name, and raises a uniform error on failure."""
        try:
            return cls.from_value(value)
        except ValueError:
            return cls.from_name(value.upper())

    @classmethod
    def from_value(cls, value: str) -> "CachePolicy":
        """Parses a member by value, raising a uniform error on failure."""
        try:
            return cls(value)
        except ValueError as e:
            raise ValueError(f"'{value}' is not a valid value for '{cls.__name__}'") from e

    @classmethod
    def from_name(cls, name: str) -> "CachePolicy":
        """Parses a member by name, raising a uniform error on failure."""
        try:
            return cls[name]
        except KeyError as e:
            raise ValueError(f"'{name}' is not a valid name for '{cls.__name__}'") from e

    @classmethod
    def names(cls) -> List[str]:
        """Returns the list of member names."""
        return list(cls.__members__.keys())

    @classmethod
    def values(cls) -> List[str]:
        """Returns the list of member values."""
        return [m.value for m in cls]
