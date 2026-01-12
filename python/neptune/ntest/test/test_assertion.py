#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the test utilities.
########################################################################################################################

from __future__ import annotations

import numpy as np
import pandas as pd
import pytest

from ntest.common import assert_equals

__ASSERTION_TEST_CASES____________________________________________________________________ = ""


def test_assert_equals_scalar_equal() -> None:
    assert_equals("a", "a")
    assert_equals(42, 42)
    assert_equals(True, True)


def test_assert_equals_float_within_precision() -> None:
    assert_equals(1.234567, 1.2345674, precision=6)


def test_assert_equals_float_outside_precision_raises() -> None:
    with pytest.raises(AssertionError):
        assert_equals(1.234567, 1.2345689, precision=6)


def test_assert_equals_dict_key_order_is_ignored() -> None:
    first = {"a": 1, "b": 2}
    second = {"b": 2, "a": 1}
    assert_equals(first, second)


def test_assert_equals_dict_keys_mismatch_raises() -> None:
    first = {"a": 1, "b": 2}
    second = {"a": 1, "c": 2}
    with pytest.raises(AssertionError, match=r"keys.*different"):
        assert_equals(first, second)


def test_assert_equals_numpy_2d_array() -> None:
    first = np.array([[1.0, 2.0], [3.0, 4.0]])
    second = first.copy()
    second[0, 0] += 4e-7
    assert_equals(first, second, precision=6)


def test_assert_equals_dataframe() -> None:
    first = pd.DataFrame({"a": [1.0, 2.0], "b": [3.0, 4.0]}, index=["x", "y"])
    second = first.copy()
    second.loc["x", "a"] += 4e-7
    assert_equals(first, second, precision=6)


def test_assert_equals_dataframe_index_mismatch_raises() -> None:
    first = pd.DataFrame({"a": [1, 2], "b": [3, 4]}, index=["x", "y"])
    second = pd.DataFrame({"a": [1, 2], "b": [3, 4]}, index=["x", "z"])
    with pytest.raises(AssertionError, match=r"indexes.*different"):
        assert_equals(first, second)
