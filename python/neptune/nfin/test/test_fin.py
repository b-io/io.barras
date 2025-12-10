#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the financial utilities.
########################################################################################################################

import logging
import unittest

from nfin.time_series import *
from ntest.unit.unittest import Test
from nutil.io.logging import configure_logging
from nutil.scalar.date import *

## FIN TEST CONSTANTS ####################################################################

__FIN_TEST_CONSTANTS________________________________________ = ""

PRECISION = 14  # decimals

TEST_COUNT = 10


## FIN TEST CASES ########################################################################

__FIN_TEST_CASES____________________________________________ = ""


class TestFin(Test):

    def test_time_series(self):
        date_to = get_date()
        date_from = date_to - 2 * RELATIVE_YEAR
        index = create_date_sequence(date_from, date_to)
        series = rename(
            to_series(
                cum_diff(create_random_int_array(-5, len(index), high=6), 0)[:-1], index=index
            ),
            "Random walk",
        )

        logging.info("Test the time series transformations")
        for agg in (Aggregation.IDENTITY, Aggregation.MEAN):
            for freq in (
                # Frequency.DAYS,
                Frequency.WEEKS,
                Frequency.MONTHS,
                Frequency.QUARTERS,
                Frequency.SEMESTERS,
                Frequency.YEARS,
            ):
                for pos in (Position.START, Position.MIDDLE, Position.END):
                    s = remove_null(
                        transform_series(
                            series,
                            agg=agg,
                            freq=freq,
                            pos=pos,
                            transformation=Transformation.DIFF,
                        )
                    )
                    if freq is Frequency.MONTHS:
                        if pos is Position.START:
                            logging.info(
                                get_first(get_index(s)), "=", get_next_month_start(date_from)
                            )
                            self.assert_equals(
                                to_stamp(get_first(get_index(s))),
                                to_stamp(get_next_month_start(date_from)),
                            )
                        elif pos is Position.END:
                            logging.info(
                                get_first(get_index(s)), "=", get_next_month_end(date_from)
                            )
                            self.assert_equals(
                                to_stamp(get_first(get_index(s))),
                                to_stamp(get_next_month_end(date_from)),
                            )
                    logging.info(find_nearest_freq(s), "=", freq)
                    self.assert_equals(find_nearest_freq(s).value, freq.value)
                    if freq is not Frequency.DAYS:
                        logging.info(find_nearest_position(s, freq=freq), "=", pos)
                        self.assert_equals(find_nearest_position(s, freq=freq).value, pos.value)

        logging.info("Test the time series forecast")
        forecasted_series = rename(
            forecast_series(series, horizon=2, freq=Frequency.MONTHS), "Forecast"
        )
        forecasted_series = forecasted_series[forecasted_series.index >= pd.Timestamp(date_to)]
        if agg is Aggregation.IDENTITY:
            self.assert_equals(forecasted_series.iloc[-1], -55.73330798470403)
        fig = plot_series(concat_cols(series, forecasted_series), title="Forecasting")
        fig.show()

        logging.info("Plot the seasonal-trend decomposition")
        decomposition = decompose_series(series)
        fig = plot_decomposition(
            decomposition.trend,
            decomposition.seasonal,
            decomposition.resid,
            name="Random walk",
            color="blue",
        )
        decomposition = decompose_series(forecasted_series)
        fig = plot_decomposition(
            decomposition.trend,
            decomposition.seasonal,
            decomposition.resid,
            fig=fig,
            name="Forecast",
            color="orange",
        )
        fig.show()


## FIN TEST MAIN #########################################################################

__FIN_TEST_MAIN_____________________________________________ = ""


def main() -> None:
    """Tests the financial utilities."""
    configure_logging(level=logging.DEBUG)
    unittest.main()


if __name__ == "__main__":
    main()
