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

from scipy.special import ndtr

from nutil.stats.common import *

####################################################################################################
# NORMAL CONSTANTS
####################################################################################################

__NORMAL_CONSTANTS________________________________ = ''

NORMAL_NAME = 'Normal'
NORMAL_KDE_NAME = 'Normal KDE'

####################################################################################################
# NORMAL CLASSES
####################################################################################################

__NORMAL_CLASSES__________________________________ = ''


class Normal(Distribution):

	def __init__(self, mu=0, sigma=1, series=None, dof=1):
		super().__init__(NORMAL_NAME, series=series, dof=dof)

		if is_null(series):
			self.mu = mu
			self.sigma = sigma
		else:
			# Estimate the parameters μ and σ of the distribution
			self.mu = mean(series)
			self.sigma = std(series, dof=dof)

	def __str__(self):
		return self.name + par(collist(self.mu, self.sigma))

	def mean(self):
		return self.mu

	def median(self):
		return self.mu

	def mode(self):
		return self.mu

	def std(self):
		return self.sigma

	def var(self):
		return self.sigma ** 2

	def skew(self):
		return 0

	def kurtosis(self):
		return 0

	def entropy(self):
		return log(2 * PI * E * self.sigma ** 2) / 2

	def pdf(self, x):
		return pdf(x, dof=self.size - 1, mu=self.mu, sigma=self.sigma)

	def cdf(self, x):
		return cdf(x, dof=self.size - 1, mu=self.mu, sigma=self.sigma)

	def inv_cdf(self, p):
		return inv_cdf(p, dof=self.size - 1, mu=self.mu, sigma=self.sigma)

	def margin(self, p=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		if mean or std:
			# Confidence interval
			q = quantile(p=p, tail=tail, dof=self.size - 1, std=std)
			if mean:
				return multiply(q / sqrt(self.size), self.std())
			elif std:
				return multiply(sqrt((self.size - 1) / q), self.std())
		if not is_null(self.size):
			# Prediction interval for unknown mean and variance
			q = quantile(p=p, tail=tail, dof=self.size - 1, std=std)
			return multiply(sqrt(1 + 1 / self.size) * q, self.std())
		# Prediction interval
		return subtract(interval(p=p, tail=tail, mu=self.mu, sigma=self.sigma), self.mean())

	def interval(self, p=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		margin = self.margin(p=p, tail=tail, mean=mean, std=std)
		if std:
			return add(self.std(), margin)
		return add(self.mean(), margin)


class NormalKDE(Distribution):

	def __init__(self, series=None, dof=1):
		super().__init__(NORMAL_KDE_NAME, series=series, dof=dof)

		# Estimate the parameters μ and σ of the distribution
		self.mu = mean(series)
		self.sigma = std(series, dof=dof)

		# Estimate the density of the distribution
		self.kernel = stats.gaussian_kde(series)

	def __str__(self):
		return self.name + par(collist(self.mu, self.sigma))

	def mean(self):
		return self.mu

	def median(self):
		return self.mu

	def mode(self):
		return self.mu

	def std(self):
		return self.sigma

	def var(self):
		return self.sigma ** 2

	def skew(self):
		return 0

	def kurtosis(self):
		return 0

	def entropy(self):
		return log(2 * PI * E * self.sigma ** 2) / 2

	def pdf(self, x):
		return simplify(self.kernel.pdf(x))

	def cdf(self, x):
		return ndtr(np.ravel(x - self.kernel.dataset) / self.kernel.factor).mean()

	def inv_cdf(self, p):
		return np.quantile(self.series, p)

	def margin(self, p=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		if mean or std:
			# Confidence interval
			q = quantile(p=p, tail=tail, dof=self.size - 1, std=std)
			if mean:
				return multiply(q / sqrt(self.size), self.std())
			elif std:
				return multiply(sqrt((self.size - 1) / q), self.std())
		if not is_null(self.size):
			# Prediction interval for unknown mean and variance
			q = quantile(p=p, tail=tail, dof=self.size - 1, std=std)
			return multiply(sqrt(1 + 1 / self.size) * q, self.std())
		# Prediction interval
		return subtract(interval(p=p, tail=tail, mu=self.mu, sigma=self.sigma), self.mean())

	def interval(self, p=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		margin = self.margin(p=p, tail=tail, mean=mean, std=std)
		if std:
			return add(self.std(), margin)
		return add(self.mean(), margin)


####################################################################################################
# NORMAL FUNCTIONS
####################################################################################################

__NORMAL__________________________________________ = ''


def generate(size=1, mu=0, sigma=1):
	return np.random.normal(loc=mu, scale=sigma, size=size)


##################################################

def pdf(x, dof=None, mu=0, sigma=1):
	if is_null(dof):
		return stats.norm.pdf(x, loc=mu, scale=sigma)
	return stats.t.pdf(x, df=dof, loc=mu, scale=sigma)


def cdf(x, dof=None, mu=0, sigma=1):
	if is_null(dof):
		return stats.norm.cdf(x, loc=mu, scale=sigma)
	return stats.t.cdf(x, df=dof, loc=mu, scale=sigma)


def inv_cdf(p, dof=None, mu=0, sigma=1):
	if is_null(dof):
		return stats.norm.ppf(p, loc=mu, scale=sigma)
	return stats.t.ppf(p, df=dof, loc=mu, scale=sigma)


#########################

def quantile(p=DEFAULT_CONFIDENCE_LEVEL, tail=2, dof=None, mu=0, sigma=1, std=False):
	if std:
		return chi2(dof, p=p, tail=tail, sigma=sigma)
	if is_null(dof):
		return z(p=p, tail=tail, mu=mu, sigma=sigma)
	return t(dof, p=p, tail=tail, mu=mu, sigma=sigma)


def interval(p=DEFAULT_CONFIDENCE_LEVEL, tail=2, dof=None, mu=0, sigma=1):
	return apply(inv_cdf, interval_probability(p=p, tail=tail), dof=dof, mu=mu, sigma=sigma)
