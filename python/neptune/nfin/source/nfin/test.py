#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - test the financial utility library
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

from nfin.ts import *
from nutil.test import *

####################################################################################################
# FIN TEST CONSTANTS
####################################################################################################

_FIN_TEST_CONSTANTS_______________________________ = ''

PRECISION = 14  # decimals

TEST_COUNT = 10

####################################################################################################
# FIN TEST CLASSES
####################################################################################################

__FIN_TEST_CLASSES________________________________ = ''


class TestFin(Test):

	def test_time_series(self):
		date_to = get_date()
		date_from = date_to - 2 * RELATIVE_YEAR
		index = create_date_sequence(date_from, date_to)
		series = rename(to_series(cum_diff(create_random_int_array(-5, len(index), high=6), 0)[:-1],
		                          index=index), 'Random walk')

		test('Test the time series transformations')
		for freq in (Frequency.DAYS, Frequency.WEEKS, Frequency.MONTHS, Frequency.QUARTERS,
		             Frequency.SEMESTERS, Frequency.YEARS):
			for group in (Group.FIRST, Group.LAST):
				s = transform_series(series, freq=freq, group=group,
				                     transformation=Transformation.DIFF)
				if freq is Frequency.MONTHS:
					if group is Group.FIRST:
						test(get_first(get_index(s)), '=', get_next_month_start(date_from))
						self.assert_equals(to_stamp(get_first(get_index(s))),
						                   to_stamp(get_next_month_start(date_from)))
					else:
						test(get_first(get_index(s)), '=', get_next_month_end(date_from))
						self.assert_equals(to_stamp(get_first(get_index(s))),
						                   to_stamp(get_next_month_end(date_from)))
				test(find_nearest_freq(s), '=', freq)
				self.assert_equals(find_nearest_freq(s).value, freq.value)
				if freq is not Frequency.DAYS:
					test(find_nearest_group(s, freq=freq), '=', group)
					self.assert_equals(find_nearest_group(s, freq=freq).value, group.value)

		test('Test the time series forecast')
		forecasted_series = rename(forecast_series(series, horizon=2), 'Forecast')
		forecasted_series = forecasted_series[forecasted_series.index >= date_to]
		self.assert_equals(forecasted_series[-1], -153.4974678080443)
		fig = plot_series(concat_cols(series, forecasted_series),
		                  title='Forecasting')
		fig.show()

		test('Plot the seasonal-trend decomposition')
		decomposition = decompose_series(series)
		fig = plot_decomposition(decomposition.trend, decomposition.seasonal, decomposition.resid,
		                         name='Random walk', color='blue')
		decomposition = decompose_series(forecasted_series)
		fig = plot_decomposition(decomposition.trend, decomposition.seasonal, decomposition.resid,
		                         fig=fig, name='Forecast', color='orange')
		fig.show()


####################################################################################################
# TEST MAIN
####################################################################################################

__TEST_MAIN_______________________________________ = ''


def main():
	'''Tests the financial utility library.'''
	unittest.main()


if __name__ == '__main__':
	main()
