#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain machine learning utility functions for multivariate regressions
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

import statsmodels.api as sm

####################################################################################################
# REGRESSION FUNCTIONS
####################################################################################################

__REGRESSION______________________________________ = ''


def fit(y, X):
	'''Fits the model of the OLS regression of the specified endogenous variable y on the specified
	exogenous variables X.'''
	X = sm.add_constant(X)
	return sm.OLS(y, X).fit()


def predict(model, X):
	'''Predicts the endogenous variable y of the specified model on the specified exogenous
	variables X.'''
	return model.predict(X).ravel()


def summarize(y, X):
	'''Summarizes the results of the OLS regression of the specified endogenous variable y on the
	specified exogenous variables X.'''
	return fit(y, X).summary()
