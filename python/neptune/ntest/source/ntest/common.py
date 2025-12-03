#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# NAME
#   test_nutil - test the utility library with pytest
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

import logging
import timeit

import pytest

from nutil.struct.util import *

## COMMON TEST CONSTANTS #################################################################

__COMMON_TEST_CONSTANTS_____________________________________ = ""

PRECISION = 14  # decimals
SIZE = 100
ROW_SIZE = 10000

TEST_COUNT = 10

## COMMON TEST PROCESSORS ################################################################

__COMMON_TEST_PROCESSORS____________________________________ = ""


def assert_equals(first, second, precision=PRECISION, assert_order=False):
    """Asserts that two structures are equal with custom collection/number logic."""
    if is_collection(first):
        if len(np.shape(first)) > 1:
            if assert_order:
                row_count = count_rows(first)
                col_count = count_cols(first)
                for i in range(row_count):
                    for j in range(col_count):
                        assert_equals(
                            get(get(first, j, axis=1), i, axis=0),
                            get(get(second, j, axis=1), i, axis=0),
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


def apply_timed(c, f, test_count, *args, axis=None, inplace=False, **kwargs):
    """Applies a function and logs timing via nutil.test."""
    t = timed(lambda: apply(c, f, *args, axis=axis, inplace=inplace, **kwargs), test_count)
    logging.info(
        "Applied",
        f.__name__,
        "on",
        count(c, axis=None),
        "items",
        test_count,
        "times in",
        round(t),
        "[s]",
    )  # prints/logs


##############################


def get_items_timed(c, test_count):
    """Gets items with inclusion/exclusion and logs timing via nutil.test."""
    t = timed(
        lambda: get_items(c, inclusion=range(len(c)), exclusion=range(int(len(c) / 2))), test_count
    )
    logging.info(len(c), "items retrieved", test_count, "times in", round(t), "[s]")  # prints/logs


def get_rows_timed(c, test_count):
    """Gets rows and logs timing via nutil.test."""
    t = timed(lambda: get_rows(c), test_count)
    logging.info(
        count_rows(c), "rows retrieved", test_count, "times in", round(t), "[s]"
    )  # prints/logs


def get_cols_timed(c, test_count):
    """Gets columns and logs timing via nutil.test."""
    t = timed(lambda: get_cols(c), test_count)
    logging.info(
        count_cols(c), "cols retrieved", test_count, "times in", round(t), "[s]"
    )  # prints/logs


##############################


def timed(stmt, number):
    """Computes time for a callable executed a specified number of times."""
    return timeit.timeit(stmt=stmt, number=number)
