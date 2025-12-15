#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide unittest utilities.
########################################################################################################################

import random
import unittest

from nutil.math import *
from nutil.test.util import PRECISION

## UNIT TEST CLASSES #####################################################################

__UNIT_TEST_CLASSES_________________________________________ = ""


class Test(unittest.TestCase):

    def __init__(self, methodName="runTest"):
        super().__init__(methodName=methodName)

        random.seed(0)
        np.random.seed(0)

    def assert_equals(self, first, second, precision=PRECISION, assert_order=False):
        if is_struct(first):
            if len(np.shape(first)) > 1:
                if assert_order:
                    row_count = count_rows(first)
                    col_count = count_cols(first)
                    for j in range(col_count):
                        first_col = get(first, j, axis=1)
                        second_col = get(second, j, axis=1)
                        for i in range(row_count):
                            self.assert_equals(
                                simplify(get(first_col, i, axis=0)),
                                simplify(get(second_col, i, axis=0)),
                                precision=precision,
                            )
                else:
                    keys = get_keys(first)
                    self.assertEqual(keys, get_keys(second), msg="The keys of the collections are different")
                    index = get_index(first)
                    self.assertEqual(index, get_index(second), msg="The indexes of the collections are different")
                    for k in keys:
                        for i in index:
                            self.assert_equals(first[k][i], second[k][i], precision=precision)
            else:
                if assert_order:
                    row_count = count_rows(first)
                    for i in range(row_count):
                        self.assert_equals(get(first, i), get(second, i), precision=precision)
                else:
                    keys = get_keys(first)
                    self.assertEqual(keys, get_keys(second), msg="The keys of the collections are different")
                    for k in keys:
                        self.assert_equals(first[k], second[k], precision=precision)
        else:
            if is_number(first) and is_number(second):
                if not is_null(first) and not is_null(second):
                    self.assertAlmostEqual(first, second, places=precision)
            else:
                self.assertEqual(first, second)
