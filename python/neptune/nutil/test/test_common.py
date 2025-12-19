#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the common utilities.
########################################################################################################################

from __future__ import annotations

import logging
import timeit
import unittest

from nutil.io.logging import configure_logging
from nutil.math import *
from nutil.scalar.string import *
from nutil.test.unittest import Test

__COMMON_TEST_CONSTANTS___________________________________________________________________ = ""

PRECISION = 14  # decimals
SIZE = 100
ROW_SIZE = 10000

TEST_COUNT = 10


__COMMON_TEST_CASES_______________________________________________________________________ = ""


class TestCommon(Test):

    def test(self):
        # Initialize
        hello = "Hello, world!"
        token = generate_string(SIZE, include_digits=False)

        l1 = to_list(reverse(range(SIZE)))
        l2 = to_list(np.random.randint(0, SIZE, size=SIZE))

        d1 = to_dict(l1)
        d2 = to_dict(l2)

        s1 = to_series(d1, name="A")
        s2 = to_series(d2, name="B")

        df = concat_cols(s1, s2)
        df1 = to_frame(s1)
        df2 = to_frame(s2)
        df3 = concat_cols(df, to_series(list(token), name="C"))
        df4 = unpivot(df3, "C", names=["group", "index"])

        g0 = df.groupby(by=get_index(df))
        g1 = df.T.groupby({k: "group" for k in get_keys(df)})

        a = to_array(df)

        f = np.sum

        logging.info("Test the string functions")
        replace(token, "A", "B")
        self.assert_equals(replace_word("Bonjour, world!", "Bonjour", "Hello"), hello)
        self.assert_equals(count(split(hello, ",")), 2)

        self.apply(hello.split(), replace, "H", "I")

        logging.info("Test the list functions")
        self.get_items(l1)
        self.get_rows(l1)
        self.get_cols(l1)

        self.apply(l1, f)
        self.apply(l1, f, axis=0)
        self.apply(l1, f, axis=1)
        self.apply(l1.copy(), f, inplace=True)

        self.assert_equals(set_element_types(l1.copy(), get_element_types(l1)), l1)
        self.assert_equals(set_element_types(l1.copy(), FLOAT_TYPE), to_float(l1))

        self.tally(l1, [SIZE / 3, 2 * SIZE / 3])
        self.tally(l2, [SIZE / 3, 2 * SIZE / 3])

        logging.info("Test the array functions")
        self.get_items(a)
        self.get_rows(a)
        self.get_cols(a)

        self.apply(a, f)
        self.apply(a, f, axis=0)
        self.apply(a, f, axis=1)
        self.apply(a.copy(), f, inplace=True)

        self.assert_equals(set_element_types(a.copy(), get_element_types(a)), a)
        self.assert_equals(set_element_types(a.copy(), FLOAT_TYPE), to_float(a))

        self.assert_equals(a, df, assert_order=True)

        self.tally(a, [SIZE / 3, 2 * SIZE / 3])

        logging.info("Test the dictionary functions")
        self.get_items(d1)

        self.apply(d1, f)
        self.apply(d1.copy(), f, inplace=True)

        self.assert_equals(set_element_types(d1.copy(), get_element_types(d1)), d1)
        self.assert_equals(set_element_types(d1.copy(), FLOAT_TYPE), to_float(d1))

        self.assert_equals(update(d1.copy(), d2), take(d2, d1))
        self.assert_equals(upsert(d1.copy(), d2), d2)

        logging.info("Test the series functions")
        self.get_items(s1)
        self.get_rows(s1)
        self.get_cols(s1)

        self.apply(s1, f)
        self.apply(s1, f, axis=0)
        self.apply(s1.copy(), f, inplace=True)

        self.assert_equals(set_element_types(s1.copy(), get_element_types(s1)), s1)
        self.assert_equals(set_element_types(s1.copy(), FLOAT_TYPE), to_float(s1))

        self.assert_equals(update(s1.copy(), s2), take(s2, s1))
        self.assert_equals(upsert(s1.copy(), s2), s2)

        self.tally(s1, [SIZE / 3, 2 * SIZE / 3])
        self.tally(s2, [SIZE / 3, 2 * SIZE / 3])

        logging.info("Test the frame functions")
        self.get_items(df)
        self.get_rows(df)
        self.get_cols(df)

        self.apply(df, f)
        self.apply(df, f, axis=0)
        self.apply(df, f, axis=1)
        self.apply(df.copy(), f, inplace=True)

        self.assert_equals(set_element_types(df.copy(), get_element_types(df)), df)
        self.assert_equals(set_element_types(df.copy(), FLOAT_TYPE), to_float(df))

        self.assert_equals(update(df1.copy(), df2), df1)
        self.assert_equals(upsert(df1.copy(), df2), df)

        self.assert_equals(pivot(df4, "group", "index", "C"), df3)

        self.tally(df1, [SIZE / 3, 2 * SIZE / 3])
        self.tally(df2, [SIZE / 3, 2 * SIZE / 3])

        logging.info("Test the group functions")
        self.get_items(g0)
        self.get_items(g1)

        self.apply(g0, f)
        self.apply(g1, f)

        self.assert_equals(set_element_types(g0, get_element_types(g0)), df)
        self.assert_equals(set_element_types(g0, FLOAT_TYPE), to_float(df))

        self.tally(g0, [SIZE / 3, 2 * SIZE / 3])

    ########################################################

    def get_items(self, s):
        t = timeit.timeit(
            stmt=lambda: get_items(s, inclusion=range(len(s)), exclusion=range(int(len(s) / 2))),
            number=TEST_COUNT,
        )
        logging.info(len(s), "items retrieved", TEST_COUNT, "times in", round(t), "[s]")

    def get_rows(self, s):
        t = timeit.timeit(stmt=lambda: get_rows(s), number=TEST_COUNT)
        logging.info(count_rows(s), "rows retrieved", TEST_COUNT, "times in", round(t), "[s]")

    def get_cols(self, s):
        t = timeit.timeit(stmt=lambda: get_cols(s), number=TEST_COUNT)
        logging.info(count_cols(s), "cols retrieved", TEST_COUNT, "times in", round(t), "[s]")

    ##########################

    def apply(self, s, f, *args, axis: Optional[Axis] = None, inplace=False, **kwargs):
        t = timeit.timeit(stmt=lambda: apply(s, f, *args, axis=axis, inplace=inplace, **kwargs), number=TEST_COUNT)
        logging.info(
            "Applied",
            f.__name__,
            "on",
            count(s, axis=None),
            "items",
            TEST_COUNT,
            "times in",
            round(t),
            "[s]",
        )

    def tally(self, s, boundaries):
        t = timeit.timeit(stmt=lambda: tally(s, boundaries), number=TEST_COUNT)
        logging.info(count(s, axis=None), "elements tallied", TEST_COUNT, "times in", round(t), "[s]")


class TestMath(Test):

    def test(self):
        vectors = [[1, 2], [3, 4], [5, 6]]
        self.assert_equals(distance(vectors[1], vectors[1]), 0)
        self.assert_equals(distances(vectors[1], vectors)[1], 0)
        self.assert_equals(distances(vectors[1], vectors)[0], distances(vectors[1], vectors)[2])
        self.assert_equals(distances(vectors[1], vectors), distances(vectors, vectors[1]))
        self.assert_equals(min_distance(vectors[1], vectors), 0)
        self.assert_equals(min_distance_index(vectors[1], vectors), 1)

        vector = create_random_array(ROW_SIZE)
        self.normalize(vector)

        vectors = create_random_array(SIZE, ROW_SIZE)
        self.min_distance(vector, vectors)

    ########################################################

    def normalize(self, vector):
        t = timeit.timeit(stmt=lambda: normalize(vector), number=TEST_COUNT)
        logging.info(
            "Normalization of a",
            str(ROW_SIZE) + "-dimensional vector computed",
            TEST_COUNT,
            "times in",
            round(t),
            "[s]",
        )

    def min_distance(self, vector, vectors):
        t = timeit.timeit(stmt=lambda: min_distance(vector, vectors), number=TEST_COUNT)
        logging.info(
            "Minimum distance of a",
            str(ROW_SIZE) + "-dimensional vector to",
            SIZE,
            "",
            str(ROW_SIZE) + "-dimensional vectors computed",
            TEST_COUNT,
            "times in",
            round(t),
            "[s]",
        )


__COMMON_TEST_RUNNERS_____________________________________________________________________ = ""


### MAIN ###################################################


def main() -> None:
    """Tests the common utilities."""
    configure_logging(level=logging.DEBUG)
    unittest.main()


if __name__ == "__main__":
    main()
