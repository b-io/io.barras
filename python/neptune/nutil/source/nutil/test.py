#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - test the utility library
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2022 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

import timeit
import unittest

from nutil.common import *

####################################################################################################
# TEST CONSTANTS
####################################################################################################

_TEST_CONSTANTS___________________________________ = ''

PRECISION = 14  # decimals

TEST_COUNT = 10

####################################################################################################
# TEST CLASSES
####################################################################################################

__TEST_CLASSES____________________________________ = ''


class Test(unittest.TestCase):

	def assert_equals(self, first, second, precision=PRECISION, assert_order=False):
		if is_collection(first):
			if len(np.shape(first)) > 1:
				if assert_order:
					row_count = count_rows(first)
					col_count = count_cols(first)
					for i in range(row_count):
						for j in range(col_count):
							self.assert_equals(get(get(first, j, axis=1), i, axis=0),
							                   get(get(second, j, axis=1), i, axis=0),
							                   precision=precision)
				else:
					keys = get_keys(first)
					index = get_index(first)
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
					for k in keys:
						self.assert_equals(first[k], second[k], precision=precision)
		else:
			if is_number(first) and is_number(second):
				if not is_null(first) and not is_null(second):
					self.assertAlmostEqual(first, second, places=precision)
			else:
				self.assertEqual(first, second)


class TestCommon(Test):

	def test(self):
		# Initialize the collections
		l1 = to_list(reverse(range(100)))
		l2 = to_list(np.random.randint(0, 100, size=100))

		d1 = to_dict(l1)
		d2 = to_dict(l2)

		s1 = to_series(d1, name='a')
		s2 = to_series(d2, name='b')

		df = concat_cols(s1, s2)
		df1 = to_frame(s1)
		df2 = to_frame(s2)

		g0 = df.groupby(by=get_index(df), axis=0)
		g1 = df.groupby(by={k: 'group' for k in get_keys(df)}, axis=1)

		a = to_array(df)

		f = np.sum

		test('Test the list functions')
		self.get_items(l1)
		self.get_rows(l1)
		self.get_cols(l1)

		self.apply(l1, f)
		self.apply(l1, f, axis=0)
		self.apply(l1, f, axis=1)
		self.apply(l1.copy(), f, inplace=True)

		self.tally(l1, [33, 66])
		self.tally(l2, [33, 66])

		test('Test the array functions')
		self.get_items(a)
		self.get_rows(a)
		self.get_cols(a)

		self.apply(a, f)
		self.apply(a, f, axis=0)
		self.apply(a, f, axis=1)
		self.apply(a.copy(), f, inplace=True)

		self.assert_equals(a, df, assert_order=True)

		self.tally(a, [33, 66])

		test('Test the dictionary functions')
		self.get_items(d1)

		self.apply(d1, f)
		self.apply(d1.copy(), f, inplace=True)

		self.assert_equals(update(d1.copy(), d2), take(d2, d1))
		self.assert_equals(upsert(d1.copy(), d2), d2)

		test('Test the series functions')
		self.get_items(s1)
		self.get_rows(s1)
		self.get_cols(s1)

		self.apply(s1, f)
		self.apply(s1, f, axis=0)
		self.apply(s1.copy(), f, inplace=True)

		self.assert_equals(update(s1.copy(), s2), take(s2, s1))
		self.assert_equals(upsert(s1.copy(), s2), s2)

		self.tally(s1, [33, 66])
		self.tally(s2, [33, 66])

		test('Test the frame functions')
		self.get_items(df)
		self.get_rows(df)
		self.get_cols(df)

		self.apply(df, f)
		self.apply(df, f, axis=0)
		self.apply(df, f, axis=1)
		self.apply(df.copy(), f, inplace=True)

		self.assert_equals(update(df1.copy(), df2), df1)
		self.assert_equals(upsert(df1.copy(), df2), df)

		self.tally(df1, [33, 66])
		self.tally(df2, [33, 66])

		test('Test the group functions')
		self.get_items(g0)
		self.get_items(g1)

		self.apply(g0, f)
		self.apply(g1, f)

		self.tally(g0, [33, 66])

	##############################################

	def get_items(self, c):
		t = timeit.timeit(stmt=lambda: get_items(c, inclusion=range(len(c)),
		                                         exclusion=range(int(len(c) / 2))),
		                  number=TEST_COUNT)
		test(len(c), 'items retrieved', TEST_COUNT, 'times in', round(t), '[s]')

	def get_rows(self, c):
		t = timeit.timeit(stmt=lambda: get_rows(c), number=TEST_COUNT)
		test(count_rows(c), 'rows retrieved', TEST_COUNT, 'times in', round(t), '[s]')

	def get_cols(self, c):
		t = timeit.timeit(stmt=lambda: get_cols(c), number=TEST_COUNT)
		test(count_cols(c), 'cols retrieved', TEST_COUNT, 'times in', round(t), '[s]')

	##############################################

	def apply(self, c, f, axis=None, inplace=False):
		t = timeit.timeit(stmt=lambda: apply(c, f, axis=axis, inplace=inplace), number=TEST_COUNT)
		test('Applied', f.__name__, 'on', count(c, axis=None), 'items', TEST_COUNT, 'times in',
		     round(t), '[s]')

	def tally(self, c, boundaries):
		t = timeit.timeit(stmt=lambda: tally(c, boundaries), number=TEST_COUNT)
		test(count(c, axis=None), 'elements tallied', TEST_COUNT, 'times in', round(t), '[s]')


####################################################################################################
# TEST MAIN
####################################################################################################

__TEST_MAIN_______________________________________ = ''


def main():
	'''Tests the utility library.'''
	unittest.main()


if __name__ == '__main__':
	main()
