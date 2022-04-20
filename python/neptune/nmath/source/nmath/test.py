#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - test the mathematical utility library
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

from nmath import binomial, descriptive, lognormal, normal, poisson
from nmath.common import *
from nutil.test import *

####################################################################################################
# MATH TEST CONSTANTS
####################################################################################################

__MATH_TEST_CONSTANTS_____________________________ = ''

PRECISION = 14  # decimals
SIZE = 1000

####################################################################################################
# MATH TEST CLASSES
####################################################################################################

__MATH_TEST_CLASSES_______________________________ = ''


class TestStats(Test):

	def test_descriptive(self):
		test('Descriptive Statistics')
		series = [
			to_series(binomial.generate(SIZE, n=10, p=0.2), name='Binomial'),
			to_series(normal.generate(SIZE, mu=10, sigma=1), name='Normal'),
			to_series(lognormal.generate(SIZE, mu=0, sigma=1), name='Log-Normal'),
			to_series(poisson.generate(SIZE, lam=10), name='Poisson')
		]
		df = concat_cols(series)
		fig = descriptive.plot_multi_histogram(df, share_x=False, share_y=False)
		fig.show()
		fig = descriptive.plot_multi_density(df, share_x=False, share_y=False)
		fig.show()
		fig = descriptive.plot_histogram(get_col(df))
		fig.show()

	#####################

	def test_binomial(self):
		test(binomial.BINOMIAL_NAME.title(), 'Distribution')
		n = 10
		p = 0.2
		test(par(collist(n, p)))
		a = binomial.generate(SIZE, n=n, p=p)
		s = to_series(a)
		dist_a, dist_s = binomial.Binomial(series=a), binomial.Binomial(series=s)
		test(dist_a, dist_s)
		self.assert_dist(dist_a, dist_s)
		interval = stats.binom.interval(DEFAULT_CONFIDENCE_LEVEL, n=n, p=p)
		test('- Real confidence interval:', interval)

	def test_normal(self):
		test(normal.NORMAL_NAME.title(), 'Distribution')
		mu = 100
		sigma = 10
		test(par(collist(mu, sigma)))
		a = normal.generate(SIZE, mu=mu, sigma=sigma)
		s = to_series(a)
		dist_a, dist_s = normal.Normal(series=a), normal.Normal(series=s)
		test(dist_a, dist_s)
		self.assert_dist(dist_a, dist_s)
		interval = stats.norm.interval(DEFAULT_CONFIDENCE_LEVEL, loc=mu, scale=sigma)
		test('- Real confidence interval:', interval)

	def test_normal_kde(self):
		test(normal.NORMAL_KDE_NAME.title(), 'Distribution')
		mu = 100
		sigma = 10
		test(par(collist(mu, sigma)))
		a = normal.generate(SIZE, mu=mu, sigma=sigma)
		s = to_series(a)
		dist_a, dist_s = normal.NormalKDE(series=a), normal.NormalKDE(series=s)
		test(dist_a, dist_s)
		self.assert_dist(dist_a, dist_s)
		interval = stats.norm.interval(DEFAULT_CONFIDENCE_LEVEL, loc=mu, scale=sigma)
		test('- Real confidence interval:', interval)

	def test_log_normal(self):
		test(lognormal.LOG_NORMAL_NAME.title(), 'Distribution')
		mu = 10
		sigma = 1
		test(par(collist(mu, sigma)))
		a = lognormal.generate(SIZE, mu=mu, sigma=sigma)
		s = to_series(a)
		dist_a, dist_s = lognormal.LogNormal(series=a), lognormal.LogNormal(series=s)
		test(dist_a, dist_s)
		self.assert_dist(dist_a, dist_s)
		interval = stats.lognorm.interval(DEFAULT_CONFIDENCE_LEVEL, s=sigma, scale=exp(mu))
		test('- Real confidence interval:', interval)

	def test_poisson(self):
		test(poisson.POISSON_NAME.title(), 'Distribution')
		lam = 10
		test(par(collist(lam)))
		a = poisson.generate(SIZE, lam=lam)
		s = to_series(a)
		dist_a, dist_s = poisson.Poisson(series=a), poisson.Poisson(series=s)
		test(dist_a, dist_s)
		self.assert_dist(dist_a, dist_s)
		interval = stats.poisson.interval(DEFAULT_CONFIDENCE_LEVEL, mu=lam)
		test('- Real confidence interval:', interval)

	##############################################

	def assert_dist(self, dist_a, dist_s):
		mean_a, mean_s = dist_a.mean(), dist_s.mean()
		test('- Mean (with arrays):', mean_a)
		test('- Mean (with series):', mean_s)
		self.assert_equals(mean_a, mean_s)

		std_a, std_s = dist_a.pdf(0), dist_s.pdf(0)
		test('- Standard deviation (with arrays):', std_a)
		test('- Standard deviation (with series):', std_s)
		self.assert_equals(std_a, std_s)

		entropy_a, entropy_s = dist_a.entropy(), dist_s.entropy()
		test('- Entropy (with arrays):', entropy_a)
		test('- Entropy (with series):', entropy_s)
		self.assert_equals(entropy_a, entropy_s)

		pdf_a, pdf_s = dist_a.pdf(1), dist_s.pdf(1)
		test('- PDF (with arrays):', pdf_a)
		test('- PDF (with series):', pdf_s)
		self.assert_equals(pdf_a, pdf_s)

		cdf_a, cdf_s = dist_a.cdf(0), dist_s.cdf(0)
		test('- CDF (with arrays):', cdf_a)
		test('- CDF (with series):', cdf_s)
		self.assert_equals(cdf_a, cdf_s)

		inv_cdf_a, inv_cdf_s = dist_a.inv_cdf(0.5), dist_s.inv_cdf(0.5)
		test('- Inverse CDF (with arrays):', inv_cdf_a)
		test('- Inverse CDF (with series):', inv_cdf_s)
		self.assert_equals(inv_cdf_a, inv_cdf_s)

		margin_a, margin_s = dist_a.margin(), dist_s.margin()
		test('- Margin (with arrays):', margin_a)
		test('- Margin (with series):', margin_s)
		self.assert_equals(margin_a, margin_s)

		interval_a, interval_s = dist_a.interval(mean=True), dist_s.interval(mean=True)
		test('- Mean confidence interval (with arrays):', interval_a)
		test('- Mean confidence interval (with series):', interval_s)
		self.assert_equals(interval_a, interval_s)

		interval_a, interval_s = dist_a.interval(), dist_s.interval()
		test('- Confidence interval (with arrays):', interval_a)
		test('- Confidence interval (with series):', interval_s)
		self.assert_equals(interval_a, interval_s)


####################################################################################################
# MATH TEST MAIN
####################################################################################################

__MATH_TEST_MAIN__________________________________ = ''


def main():
	'''Tests the mathematical utility library.'''
	unittest.main()


if __name__ == '__main__':
	main()
