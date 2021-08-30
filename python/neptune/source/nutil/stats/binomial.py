#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain statistical utility functions
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

from nutil.stats import normal
from nutil.stats.common import *

####################################################################################################
# BINOMIAL CONSTANTS
####################################################################################################

__BINOMIAL_CONSTANTS______________________________ = ''

BINOMIAL_NAME = 'Binomial'

####################################################################################################
# BINOMIAL CLASSES
####################################################################################################

__BINOMIAL_CLASSES________________________________ = ''


class Binomial(Distribution):

	def __init__(self, n=1, p=0.5, series=None, dof=1):
		super().__init__(BINOMIAL_NAME, series=series, dof=dof)

		if is_null(series):
			self.n = n
			self.p = p
		else:
			# Estimate the parameters n and p of the distribution
			x = max(series)
			m = mean(series)
			v = var(series, dof=dof)
			self.n = round(x ** 2 * v / (m * (x - m)))
			self.p = m / self.n

	##############################################
	# OPERATORS
	##############################################

	def __str__(self):
		return self.name + par(collist(self.n, self.p))

	##############################################
	# PROCESSORS
	##############################################

	def mean(self):
		return self.n * self.p

	def median(self):
		return round(self.n * self.p)

	def mode(self):
		return floor((self.n + 1) * self.p)

	def std(self):
		return sqrt(self.var())

	def var(self):
		return self.n * self.p * (1 - self.p)

	def skew(self):
		return (1 - 2 * self.p) / self.std()

	def kurtosis(self):
		return (1 - 6 * self.p * (1 - self.p)) / self.var()

	def entropy(self):
		return log(2 * PI * E * self.n * self.p * (1 - self.p)) / 2

	def pdf(self, x):
		return pmf(x, n=self.n, p=self.p)

	def cdf(self, x):
		return cdf(x, n=self.n, p=self.p)

	def inv_cdf(self, p):
		return inv_cdf(p, n=self.n, p=self.p)

	def margin(self, p=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		# Confidence interval
		if mean or std:
			q = normal.quantile(p=p, tail=tail, dof=self.size - 1, std=std)
			if mean:
				return multiply(q / sqrt(self.size), self.std())
			elif std:
				return multiply(sqrt((self.size - 1) / q), self.std())
		# Prediction interval
		return subtract(interval(probability=p, tail=tail, n=self.n, p=self.p), self.mean())

	def interval(self, p=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		margin = self.margin(p=p, tail=tail, mean=mean, std=std)
		if std:
			return add(self.std(), margin)
		return add(self.mean(), margin)


####################################################################################################
# BINOMIAL FUNCTIONS
####################################################################################################

__BINOMIAL________________________________________ = ''


def generate(size=1, n=1, p=0.5):
	return np.random.binomial(n, p, size=size)


##################################################

def pmf(x, n=1, p=0.5):
	return stats.binom.pmf(x, n=n, p=p)


def cdf(x, n=1, p=0.5):
	return stats.binom.cdf(x, n=n, p=p)


def inv_cdf(probability, n=1, p=0.5):
	return stats.binom.ppf(probability, n=n, p=p)


#########################

def quantile(probability=DEFAULT_CONFIDENCE_LEVEL, tail=2, dof=None, n=1, p=0.5, std=False):
	if std:
		return normal.chi2(dof, p=probability, tail=tail, sigma=sqrt(n * p * (1 - p)))
	return apply(inv_cdf, interval_probability(p=probability, tail=tail), n=n, p=p)


def interval(probability=DEFAULT_CONFIDENCE_LEVEL, tail=2, n=1, p=0.5):
	return apply(inv_cdf, interval_probability(p=probability, tail=tail), n=n, p=p)


#########################

def event_interval(k, probability=DEFAULT_CONFIDENCE_LEVEL, n=1):
	p = 0.5 + probability / 2
	return to_array(1 / (1 + (n - k + 1) / (k * f(2 * k, 2 * (n - k + 1), p=p, tail=-1))),
	                1 / (1 + (n - k) / ((k + 1) * f(2 * (k + 1), 2 * (n - k), p=p, tail=1))))
