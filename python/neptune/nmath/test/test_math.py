#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the mathematical utilities.
########################################################################################################################

from __future__ import annotations

import logging
import unittest

from nmath.common import *
from nmath.stats import binomial, descriptive, lognormal, normal, poisson
from nutil.io.logging import configure_logging
from nutil.math import *
from nutil.scalar.string import par
from nutil.struct.util import concat_cols, get_col, to_series
from nutil.test.unittest import Test


__MATH_TEST_CONSTANTS_____________________________________________________________________ = ""

PRECISION = 14  # decimals
SIZE = 1000


__MATH_TEST_CASES_________________________________________________________________________ = ""


### STATISTICS #############################################


class TestStats(Test):

    def test_descriptive(self):
        logging.info("Test the descriptive statistics")
        series = [
            to_series(binomial.generate(SIZE, n=10, p=0.2), name="Binomial"),
            to_series(normal.generate(SIZE, mu=10, sigma=1), name="Normal"),
            to_series(lognormal.generate(SIZE, mu=0, sigma=1), name="Log-Normal"),
            to_series(poisson.generate(SIZE, lam=10), name="Poisson"),
        ]
        df = concat_cols(series)
        fig = descriptive.plot_multi_histogram(df, share_x=False, share_y=False, opacity=0.5)
        fig = descriptive.plot_multi_density(df, fig=fig, share_x=False, share_y=False)
        fig.show()
        fig = descriptive.plot_histogram(get_col(df))
        fig.show()

    ##########################

    def test_binomial(self):
        logging.info("Test the", binomial.BINOMIAL_NAME, "distribution")
        n = 10
        p = 0.2
        logging.info(par(collist(n, p)))
        a = binomial.generate(SIZE, n=n, p=p)
        s = to_series(a)
        dist_a, dist_s = binomial.Binomial(series=a), binomial.Binomial(series=s)
        logging.info(dist_a, dist_s)
        self.assert_dist(dist_a, dist_s)
        interval = stats.binom.interval(DEFAULT_CONFIDENCE_LEVEL, n=n, p=p)
        logging.info("- Real confidence interval:", interval)

    def test_normal(self):
        logging.info("Test the", normal.NORMAL_NAME, "distribution")
        mu = 100
        sigma = 10
        logging.info(par(collist(mu, sigma)))
        a = normal.generate(SIZE, mu=mu, sigma=sigma)
        s = to_series(a)
        dist_a, dist_s = normal.Normal(series=a), normal.Normal(series=s)
        logging.info(dist_a, dist_s)
        self.assert_dist(dist_a, dist_s)
        interval = stats.norm.interval(DEFAULT_CONFIDENCE_LEVEL, loc=mu, scale=sigma)
        logging.info("- Real confidence interval:", interval)

    def test_normal_kde(self):
        logging.info("Test the", normal.NORMAL_KDE_NAME, "distribution")
        mu = 100
        sigma = 10
        logging.info(par(collist(mu, sigma)))
        a = normal.generate(SIZE, mu=mu, sigma=sigma)
        s = to_series(a)
        dist_a, dist_s = normal.NormalKDE(series=a), normal.NormalKDE(series=s)
        logging.info(dist_a, dist_s)
        self.assert_dist(dist_a, dist_s)
        interval = stats.norm.interval(DEFAULT_CONFIDENCE_LEVEL, loc=mu, scale=sigma)
        logging.info("- Real confidence interval:", interval)

    def test_log_normal(self):
        logging.info("Test the", lognormal.LOG_NORMAL_NAME, "distribution")
        mu = 10
        sigma = 1
        logging.info(par(collist(mu, sigma)))
        a = lognormal.generate(SIZE, mu=mu, sigma=sigma)
        s = to_series(a)
        dist_a, dist_s = lognormal.LogNormal(series=a), lognormal.LogNormal(series=s)
        logging.info(dist_a, dist_s)
        self.assert_dist(dist_a, dist_s)
        interval = stats.lognorm.interval(DEFAULT_CONFIDENCE_LEVEL, s=sigma, scale=exp(mu))
        logging.info("- Real confidence interval:", interval)

    def test_poisson(self):
        logging.info("Test the", poisson.POISSON_NAME, "distribution")
        lam = 10
        logging.info(par(collist(lam)))
        a = poisson.generate(SIZE, lam=lam)
        s = to_series(a)
        dist_a, dist_s = poisson.Poisson(series=a), poisson.Poisson(series=s)
        logging.info(dist_a, dist_s)
        self.assert_dist(dist_a, dist_s)
        interval = stats.poisson.interval(DEFAULT_CONFIDENCE_LEVEL, mu=lam)
        logging.info("- Real confidence interval:", interval)

    ########################################################

    def assert_dist(self, dist_a, dist_s):
        mean_a, mean_s = dist_a.mean(), dist_s.mean()
        logging.info("- Mean (with arrays):", mean_a)
        logging.info("- Mean (with series):", mean_s)
        self.assert_equals(mean_a, mean_s)

        std_a, std_s = dist_a.std(), dist_s.std()
        logging.info("- Standard deviation (with arrays):", std_a)
        logging.info("- Standard deviation (with series):", std_s)
        self.assert_equals(std_a, std_s)

        entropy_a, entropy_s = dist_a.entropy(), dist_s.entropy()
        logging.info("- Entropy (with arrays):", entropy_a)
        logging.info("- Entropy (with series):", entropy_s)
        self.assert_equals(entropy_a, entropy_s)

        pdf_a, pdf_s = dist_a.pdf(1), dist_s.pdf(1)
        logging.info("- PDF (with arrays):", pdf_a)
        logging.info("- PDF (with series):", pdf_s)
        self.assert_equals(pdf_a, pdf_s)

        cdf_a, cdf_s = dist_a.cdf(0), dist_s.cdf(0)
        logging.info("- CDF (with arrays):", cdf_a)
        logging.info("- CDF (with series):", cdf_s)
        self.assert_equals(cdf_a, cdf_s)

        inv_cdf_a, inv_cdf_s = dist_a.inv_cdf(0.5), dist_s.inv_cdf(0.5)
        logging.info("- Inverse CDF (with arrays):", inv_cdf_a)
        logging.info("- Inverse CDF (with series):", inv_cdf_s)
        self.assert_equals(inv_cdf_a, inv_cdf_s)

        margin_a, margin_s = dist_a.margin(), dist_s.margin()
        logging.info("- Margin (with arrays):", margin_a)
        logging.info("- Margin (with series):", margin_s)
        self.assert_equals(margin_a, margin_s)

        interval_a, interval_s = dist_a.interval(), dist_s.interval()
        logging.info("- Prediction interval (with arrays):", interval_a)
        logging.info("- Prediction interval (with series):", interval_s)
        self.assert_equals(interval_a, interval_s)

        interval_a, interval_s = dist_a.interval(is_mean=True), dist_s.interval(is_mean=True)
        logging.info("- Mean confidence interval (with arrays):", interval_a)
        logging.info("- Mean confidence interval (with series):", interval_s)
        self.assert_equals(interval_a, interval_s)

        interval_a, interval_s = dist_a.interval(is_std=True), dist_s.interval(is_std=True)
        logging.info("- Standard deviation confidence interval (with arrays):", interval_a)
        logging.info("- Standard deviation confidence interval (with series):", interval_s)
        self.assert_equals(interval_a, interval_s)


__MATH_TEST_RUNNERS_______________________________________________________________________ = ""


### MAIN ###################################################


def main() -> None:
    """Tests the mathematical utilities."""
    configure_logging(level=logging.DEBUG)
    unittest.main()


if __name__ == "__main__":
    main()
