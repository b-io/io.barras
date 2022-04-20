#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain common mathematical utility functions
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

from abc import ABC, abstractmethod

from scipy import stats

from nutil.math import *

####################################################################################################
# MATH COMMON CONSTANTS
####################################################################################################

__MATH_COMMON_CONSTANTS___________________________ = ''

# The default confidence level
if not exists('DEFAULT_CONFIDENCE_LEVEL'):
	DEFAULT_CONFIDENCE_LEVEL = 0.95

####################################################################################################
# MATH COMMON CLASSES
####################################################################################################

__MATH_COMMON_CLASSES_____________________________ = ''


class Distribution(ABC):

	def __init__(self, name, series=None, dof=1):
		self.name = name
		self.series = series
		self.dof = dof
		self.size = len(series) if not is_null(series) else NAN

	##############################################
	# OPERATORS
	##############################################

	def __str__(self):
		return self.name + ' Distribution'

	##############################################
	# PROCESSORS
	##############################################

	@abstractmethod
	def mean(self):
		pass

	@abstractmethod
	def median(self):
		pass

	@abstractmethod
	def mode(self):
		pass

	@abstractmethod
	def std(self):
		pass

	@abstractmethod
	def var(self):
		pass

	@abstractmethod
	def skew(self):
		pass

	@abstractmethod
	def kurtosis(self):
		pass

	@abstractmethod
	def entropy(self):
		pass

	@abstractmethod
	def pdf(self, x):
		pass

	@abstractmethod
	def cdf(self, x):
		pass

	@abstractmethod
	def inv_cdf(self, q):
		pass

	@abstractmethod
	def margin(self, cl=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		pass

	@abstractmethod
	def interval(self, cl=DEFAULT_CONFIDENCE_LEVEL, tail=2, mean=False, std=False):
		pass


####################################################################################################
# MATH COMMON FUNCTIONS
####################################################################################################

__MATH_COMMON_____________________________________ = ''


def mode(*args, axis=0):
	c = forward(*args)
	return calculate(c, f=stats.mode, axis=axis)


def cor(c1, c2):
	if is_table(c1) or is_table(c2) or is_dict(c1) or is_dict(c2):
		return to_series(c1).corr(to_series(c2))
	return np.corrcoef(c1, c2)[0, 1]


def cov(c1, c2, dof=1):
	if is_table(c1) or is_table(c2) or is_dict(c1) or is_dict(c2):
		return to_series(c1).cov(to_series(c2), ddof=dof)
	return np.cov(c1, c2, ddof=dof)[0, 1]


def skew(*args, axis=0):
	c = forward(*args)
	if is_group(c):
		return c.skew()
	elif is_frame(c):
		return c.skew(axis=axis)
	return stats.skew(get_values(c), axis=axis)


def kurtosis(*args, axis=0):
	c = forward(*args)
	return calculate(c, f=stats.kurtosis, axis=axis)


def entropy(*args, axis=0):
	c = forward(*args)
	return calculate(c, f=stats.entropy, axis=axis)


def margin(cl=DEFAULT_CONFIDENCE_LEVEL, tail=2):
	if tail == -1 or tail == 1:
		# Cantelli's inequality
		k = sqrt(1 / (1 - cl) - 1)
		if tail == -1:
			return -k
		elif tail == 1:
			return k
	elif tail == 2:
		# Chebyshev's inequality
		k = sqrt(1 / (1 - cl))
		return to_array(-k, k, type=FLOAT_ELEMENT_TYPE)


#########################

def interval_probability(cl=DEFAULT_CONFIDENCE_LEVEL, tail=2):
	if tail == -1:
		return 1 - cl
	elif tail == 1:
		return cl
	elif tail == 2:
		return to_array(0.5 - cl / 2, 0.5 + cl / 2, type=FLOAT_ELEMENT_TYPE)
	return NAN


def z(cl=DEFAULT_CONFIDENCE_LEVEL, tail=2, mu=0, sigma=1):
	return apply(interval_probability(cl=cl, tail=tail), stats.norm.ppf, mu, sigma)


def t(dof, cl=DEFAULT_CONFIDENCE_LEVEL, tail=2, mu=0, sigma=1):
	return apply(interval_probability(cl=cl, tail=tail), stats.t.ppf, dof, mu, sigma)


def chi2(*dof, cl=DEFAULT_CONFIDENCE_LEVEL, tail=2, sigma=1):
	sigma2 = sigma ** 2
	return apply(interval_probability(cl=cl, tail=tail), stats.chi2.ppf, forward(*dof), 0, sigma2)


def f(dofn, dofd, cl=DEFAULT_CONFIDENCE_LEVEL, tail=2, sigma=1):
	sigma2 = sigma ** 2
	return apply(interval_probability(cl=cl, tail=tail), stats.f.ppf, dofn, dofd, 0, sigma2)


#########################

def standardize(value, mean=0, std=1):
	return (value - mean) / std
