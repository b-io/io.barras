#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide test utilities.
########################################################################################################################

from __future__ import annotations

import logging
import timeit

import pytest

from nutil.struct.util import *


__COMMON_TEST_CONSTANTS___________________________________________________________________ = ""

PRECISION = 14  # decimals
SIZE = 100
ROW_SIZE = 10000

TEST_COUNT = 10


__COMMON_TEST_PROCESSORS__________________________________________________________________ = ""


def assert_equals(first, second, precision=PRECISION, assert_order=False):
    """Asserts that two structures are equal with custom collection/number logic."""
    if is_struct(first):
        if len(np.shape(first)) > 1:
            if assert_order:
                row_count = count_rows(first)
                col_count = count_cols(first)
                for j in range(col_count):
                    first_col = get(first, j, axis=1)
                    second_col = get(second, j, axis=1)
                    for i in range(row_count):
                        assert_equals(
                            simplify(get(first_col, i, axis=0)),
                            simplify(get(second_col, i, axis=0)),
                            precision=precision,
                        )
            else:
                keys = get_keys(first)
                assert keys == get_keys(second), "The keys of the collections are different"
                index = get_index(first)
                assert index == get_index(second), "The indexes of the collections are different"
                for k in keys:
                    for i in index:
                        assert_equals(first[k][i], second[k][i], precision=precision)
        else:
            if assert_order:
                row_count = count_rows(first)
                for i in range(row_count):
                    assert_equals(get(first, i), get(second, i), precision=precision)
            else:
                keys = get_keys(first)
                assert keys == get_keys(second), "The keys of the collections are different"
                for k in keys:
                    assert_equals(first[k], second[k], precision=precision)
    else:
        if is_number(first) and is_number(second):
            if not is_null(first) and not is_null(second):
                # Use pytest's approx for clear diffs
                assert first == pytest.approx(second, abs=0, rel=0, ndigits=precision)
        else:
            assert first == second


##############################


def apply_timed(s, f, test_count, *args, axis=None, inplace=False, **kwargs):
    """Applies a function and logs timing via nutil.test."""
    t = timed(lambda: apply(s, f, *args, axis=axis, inplace=inplace, **kwargs), test_count)
    logging.info(
        "Applied",
        f.__name__,
        "on",
        count(s, axis=None),
        "items",
        test_count,
        "times in",
        round(t),
        "[s]",
    )  # prints/logs


##############################


def get_items_timed(s, test_count):
    """Gets items with inclusion/exclusion and logs timing via nutil.test."""
    t = timed(lambda: get_items(s, inclusion=range(len(s)), exclusion=range(int(len(s) / 2))), test_count)
    logging.info(len(s), "items retrieved", test_count, "times in", round(t), "[s]")  # prints/logs


def get_rows_timed(s, test_count):
    """Gets rows and logs timing via nutil.test."""
    t = timed(lambda: get_rows(s), test_count)
    logging.info(count_rows(s), "rows retrieved", test_count, "times in", round(t), "[s]")  # prints/logs


def get_cols_timed(s, test_count):
    """Gets columns and logs timing via nutil.test."""
    t = timed(lambda: get_cols(s), test_count)
    logging.info(count_cols(s), "cols retrieved", test_count, "times in", round(t), "[s]")  # prints/logs


##############################


def timed(stmt, number):
    """Computes time for a callable executed a specified number of times."""
    return timeit.timeit(stmt=stmt, number=number)
