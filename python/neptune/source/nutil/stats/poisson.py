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
# POISSON CONSTANTS
####################################################################################################

__POISSON_CONSTANTS_______________________________ = ''

POISSON_NAME = 'Poisson'

####################################################################################################
# POISSON CLASSES
####################################################################################################

__POISSON_CLASSES_________________________________ = ''


class Poisson(Distribution):

	def __init__(self, lam=1, series=None, dof=1):
		super().__init__(POISSON_NAME, series=series, dof=dof)

		if is_null(series):
			self.lam = lam
		else:
			# Estimate the parameter λ of the distribution
			self.lam = mean(series)

	##############################################
	# OPERATORS
	##############################################

	def __str__(self):
		return self.name + par(collist(self.lam))

	##############################################
	# PROCESSORS
	##############################################

	def mean(self):
		return self.lam

	def median(self):
		return floor(self.lam + 1 / 3 - 0.02 / self.lam)

	def mode(self):
		return floor(self.lam)

	def std(self):
		return sqrt(self.var())

	def var(self):
		return self.lam

	def skew(self):
		return 1 / self.std()

	def kurtosis(self):
		return 1 / self.var()

	def entropy(self):
		correction = - 1 / (12 * self.lam) - 1 / (24 * self.lam ** 2) - 19 / (360 * self.lam ** 3)
		return log(2 * PI * E * self.lam) / 2 + correction

	def pdf(self, x):
		return pmf(x, lam=self.lam)

	def cdf(self, x):
		return cdf(x, lam=self.lam)

	def inv_cdf(self, p):
		return inv_cdf(p, lam=self.lam)

	def margin(self, p=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		# Confidence interval
		if mean or std:
			q = normal.quantile(p=p, tail=tail, dof=self.size - 1, std=std)
			if mean:
				return multiply(q / sqrt(self.size), self.std())
			elif std:
				return multiply(sqrt((self.size - 1) / q), self.std())
		# Prediction interval
		return subtract(interval(p=p, tail=tail, lam=self.lam), self.mean())

	def interval(self, p=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		margin = self.margin(p=p, tail=tail, mean=mean, std=std)
		if std:
			return add(self.std(), margin)
		return add(self.mean(), margin)


####################################################################################################
# POISSON FUNCTIONS
####################################################################################################

__POISSON_________________________________________ = ''


def generate(size=1, lam=1):
	return np.random.poisson(lam=lam, size=size)


##################################################

def pmf(x, lam=1):
	return stats.poisson.pmf(x, mu=lam)


def cdf(x, lam=1):
	return stats.poisson.cdf(x, mu=lam)


def inv_cdf(p, lam=1):
	return stats.poisson.ppf(p, mu=lam)


#########################

def quantile(p=DEFAULT_CONFIDENCE_LEVEL, tail=2, dof=None, lam=1, std=False):
	if std:
		return normal.chi2(dof, p=p, tail=tail, sigma=sqrt(lam))
	return apply(inv_cdf, interval_probability(p=p, tail=tail), lam=lam)


def interval(p=DEFAULT_CONFIDENCE_LEVEL, tail=2, lam=1):
	return apply(inv_cdf, interval_probability(p=p, tail=tail), lam=lam)


#########################

def event_interval(k, p=DEFAULT_CONFIDENCE_LEVEL):
	p = 0.5 + p / 2
	return to_array(chi2(2 * k, p=p, tail=-1) / 2,
	                chi2(2 * k + 2, p=p, tail=1) / 2)
