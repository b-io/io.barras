#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - test utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2021 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

import unittest

from nutil.ts import *

####################################################################################################
# TEST CONSTANTS
####################################################################################################

_TEST_CONSTANTS___________________________________ = ''

PRECISION = 14  # decimals

####################################################################################################
# TEST CLASSES
####################################################################################################

__TEST_CLASSES____________________________________ = ''


class Test(unittest.TestCase):

	def assert_equals(self, first, second, precision=PRECISION):
		if is_collection(first):
			for i in range(len(first)):
				self.assert_equals(first[i], second[i], precision=precision)
		else:
			self.assertAlmostEqual(first, second, places=precision)


class TestTimeSeries(Test):

	def test(self):
		date_to = get_datetime()
		date_from = date_to - 2 * YEAR
		index = create_datetime_sequence(date_from, date_to)
		s = to_series(range(len(index)), index=index)
		s = transform_series(s, freq=Frequency.MONTHS, transformation=Transformation.LOG_RETURNS)
		test(get_first(get_index(s)), '=', get_next_month_end(date_from))
		self.assert_equals(to_stamp(get_first(get_index(s))),
		                   to_stamp(get_next_month_end(date_from)))


####################################################################################################
# TEST MAIN
####################################################################################################

__TEST_MAIN_______________________________________ = ''


def main():
	"""Tests the library."""
	unittest.main()


if __name__ == '__main__':
	main()
