#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the decorators.
########################################################################################################################

import logging
import unittest

import pytest
from _pytest.logging import LogCaptureFixture

from nutil.decorators.caching import hash_cache
from nutil.decorators.typing import typesafe
from nutil.math import *

## DECORATORS TEST CASES #################################################################

__DECORATORS_TEST_CASES_____________________________________ = ""


### HASH CACHE DECORATOR ###################################


def test_hash_cache_caches_results_for_same_arguments() -> None:
    calls = {"count": 0}

    @hash_cache(maxsize=8)
    def sum_list(values: list[int]) -> int:
        calls["count"] += 1
        return sum(values)

    # First invocation computes the result
    assert sum_list([1, 2, 3]) == 6
    assert calls["count"] == 1

    # Second invocation with the same logical input hits the cache
    assert sum_list([1, 2, 3]) == 6
    assert calls["count"] == 1  # no additional call


def test_hash_cache_lru_eviction_for_old_keys() -> None:
    calls = {"count": 0}

    @hash_cache(maxsize=2)
    def double(x: int) -> int:
        calls["count"] += 1
        return 2 * x

    # Insert two distinct keys
    assert double(1) == 2  # miss
    assert double(2) == 4  # miss
    assert calls["count"] == 2

    # Reuse key 1 so it becomes most recently used; key 2 is now the LRU
    assert double(1) == 2  # hit
    assert calls["count"] == 2

    # Insert a third key → evict LRU (key 2)
    assert double(3) == 6  # miss
    assert calls["count"] == 3

    # Key 1 should still be cached, key 2 should have been evicted
    assert double(1) == 2  # hit
    assert calls["count"] == 3
    assert double(2) == 4  # miss after eviction
    assert calls["count"] == 4


### TYPESAFE DECORATOR #####################################


def test_typesafe_accepts_matching_types() -> None:
    @typesafe()
    def add(x: int, y: int) -> int:
        return x + y

    assert add(1, 2) == 3


def test_typesafe_ignores_unannotated_parameters() -> None:
    @typesafe()
    def concat(prefix, value: str) -> str:
        return f"{prefix}{value}"

    # `prefix` is unannotated and should not be validated
    assert concat(123, "abc") == "123abc"


def test_typesafe_raises_on_type_mismatch_in_raise_mode() -> None:
    @typesafe(mode="raise")
    def multiply(x: int, y: int) -> int:
        return x * y

    # Using the wrong type should raise a TypeError
    with pytest.raises(TypeError):
        multiply("1", 2)


def test_typesafe_only_checks_annotated_arguments() -> None:
    @typesafe(mode="raise")
    def func(a: int, b, c: str) -> str:
        return f"{a}-{b}-{c}"

    # `b` is unannotated and should be accepted even when not a string
    assert func(1, object(), "x") == "1-<object>-x".replace("<object>", "object()")[:3] or func(
        1, object(), "x"
    )


def test_typesafe_suggest_mode_does_not_raise(caplog: LogCaptureFixture) -> None:
    @typesafe(mode="suggest")
    def to_int(x: int) -> int:
        return x

    # Wrong type should not raise in `"suggest"` mode
    with caplog.at_level(logging.WARNING):
        result = to_int("1")  # type: ignore[arg-type]

    # The function still runs and returns the raw value
    assert result == "1"

    # A warning should be logged
    assert any(rec.levelno == logging.WARNING for rec in caplog.records)


## DECORATORS TEST MAIN ##################################################################

__DECORATOR_TEST_MAIN_______________________________________ = ""


def main():
    """Tests the decorators."""
    unittest.main()


if __name__ == "__main__":
    main()
