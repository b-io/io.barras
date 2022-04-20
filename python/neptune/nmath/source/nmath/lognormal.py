#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain mathematical utility functions for log-normal distributions
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

from nmath import normal
from nmath.common import *

####################################################################################################
# LOG-NORMAL CONSTANTS
####################################################################################################

__LOG_NORMAL_CONSTANTS____________________________ = ''

LOG_NORMAL_NAME = 'Log-Normal'

####################################################################################################
# LOG-NORMAL CLASSES
####################################################################################################

__LOG_NORMAL_CLASSES______________________________ = ''


class LogNormal(Distribution):

	def __init__(self, mu=0, sigma=1, series=None, dof=1):
		super().__init__(LOG_NORMAL_NAME, series=series, dof=dof)

		if is_null(series):
			self.mu = mu
			self.sigma = sigma
		else:
			# Estimate the parameters μ and σ of the distribution
			self.mu = normal.mean(log(series))
			self.sigma = normal.std(log(series), dof=dof)

	##############################################
	# OPERATORS
	##############################################

	def __str__(self):
		return self.name + par(collist(self.mu, self.sigma))

	##############################################
	# PROCESSORS
	##############################################

	def mean(self):
		sigma2 = self.sigma ** 2
		return exp(self.mu + sigma2 / 2)

	def median(self):
		return exp(self.mu)

	def mode(self):
		sigma2 = self.sigma ** 2
		return exp(self.mu - sigma2)

	def std(self):
		return sqrt(self.var())

	def var(self):
		sigma2 = self.sigma ** 2
		return exp(2 * self.mu + sigma2) * (exp(sigma2) - 1)

	def skew(self):
		sigma2 = self.sigma ** 2
		return (exp(sigma2) + 2) * sqrt(exp(sigma2) - 1)

	def kurtosis(self):
		sigma2 = self.sigma ** 2
		return exp(4 * sigma2) + 2 * exp(3 * sigma2) + 3 * exp(2 * sigma2) - 6

	def entropy(self):
		sigma2 = self.sigma ** 2
		return self.mu + log(2 * PI * E * sigma2) / 2

	def pdf(self, x):
		return pdf(x, mu=self.mu, sigma=self.sigma)

	def cdf(self, x):
		return cdf(x, mu=self.mu, sigma=self.sigma)

	def inv_cdf(self, q):
		return inv_cdf(q, mu=self.mu, sigma=self.sigma)

	def margin(self, cl=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		if mean or std:
			# Confidence interval
			q = normal.quantile(cl=cl, tail=tail, dof=self.size - 1, std=std)
			if mean:
				if self.dof == 0:
					# Cox's method
					sigma2 = self.sigma ** 2
					s = sqrt(sigma2 / self.size + sigma2 ** 2 / (2 * (self.size - 1)))
				else:
					# Angus's conservative method
					if tail == -1:
						q = t(cl=cl, tail=-1, dof=self.size - 1)
					elif tail == 1:
						q = sqrt(self.size / 2 * ((self.size - 1) / chi2(self.size - 1, cl=cl, tail=-1) - 1))
					elif tail == 2:
						cl = 0.5 + cl / 2
						q = to_array(t(cl=cl, tail=-1, dof=self.size - 1),
						             sqrt(self.size / 2 * ((self.size - 1) / chi2(self.size - 1, cl=cl, tail=-1) - 1)),
						             type=FLOAT_ELEMENT_TYPE)
					sigma2 = self.sigma ** 2
					s = sqrt((sigma2 + sigma2 ** 2 / 2) / self.size)
				return exp(multiply(q, s))
			elif std:
				return exp(multiply(sqrt((self.size - 1) / q), self.sigma))
		# Prediction interval
		return divide(interval(cl=cl, tail=tail, mu=self.mu, sigma=self.sigma), self.mean())

	def interval(self, cl=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		margin = self.margin(cl=cl, tail=tail, mean=mean, std=std)
		if std:
			return multiply(self.std(), margin)
		return multiply(self.mean(), margin)


####################################################################################################
# LOG-NORMAL FUNCTIONS
####################################################################################################

__LOG_NORMAL______________________________________ = ''


def generate(size=1, mu=0, sigma=1):
	return np.random.lognormal(mean=mu, sigma=sigma, size=size)


##################################################

def pdf(x, mu=0, sigma=1):
	return stats.lognorm.pdf(x, s=sigma, scale=exp(mu))


def cdf(x, mu=0, sigma=1):
	return stats.lognorm.cdf(x, s=sigma, scale=exp(mu))


def inv_cdf(q, mu=0, sigma=1):
	return stats.lognorm.ppf(q, s=sigma, scale=exp(mu))


#########################

def quantile(cl=DEFAULT_CONFIDENCE_LEVEL, tail=2, dof=None, mu=0, sigma=1, std=False):
	if std:
		return normal.chi2(dof, cl=cl, tail=tail, sigma=sigma)
	return interval(cl=cl, tail=tail, mu=mu, sigma=sigma)


def interval(cl=DEFAULT_CONFIDENCE_LEVEL, tail=2, mu=0, sigma=1):
	return apply(interval_probability(cl=cl, tail=tail), inv_cdf, mu=mu, sigma=sigma)
