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
from sklearn.preprocessing import OrdinalEncoder

from ngui.chart import *
from nutil.common import *

####################################################################################################
# REGRESSION FUNCTIONS
####################################################################################################

__REGRESSION______________________________________ = ''


def get_categorical_variables(X):
	return [k for k, v in get_element_types(X).items()
	        if v in (OBJECT_ELEMENT_TYPE, STRING_ELEMENT_TYPE)]


##################################################

def encode_categorical_variables(X, type=INT_ELEMENT_TYPE):
	categorical_variables = get_categorical_variables(X)
	X[categorical_variables] = OrdinalEncoder(dtype=type).fit_transform(X[categorical_variables])
	return X


def fit(X, y):
	'''Fits the model of the OLS regression of the specified endogenous variable y on the specified
	exogenous variables X.'''
	X = sm.add_constant(X)
	return sm.OLS(y, X).fit()


def predict(model, X):
	'''Predicts the endogenous variable y of the specified model on the specified exogenous
	variables X.'''
	return model.predict(X).ravel()


def summarize(X, y):
	'''Summarizes the results of the OLS regression of the specified endogenous variable y on the
	specified exogenous variables X.'''
	return fit(X, y).summary()


# • REGRESSION FIGURE ##############################################################################

__REGRESSION_FIGURE_______________________________ = ''


def plot_variables(X, y,
                   # Figure
                   fig=None, row_count=None, col_count=None,
                   share_x=False, share_y=True,
                   title=None, subtitles=None, title_x=None, title_y=None,
                   width=DEFAULT_WIDTH, height=DEFAULT_HEIGHT, margin=None,
                   # Chart
                   colors=DEFAULT_COLORS, mode='markers',
                   # Flags
                   show_legend=False, **kwargs):
	def draw_variables(x, **kwargs):
		return draw(x, y=y, mode=mode, show_legend=show_legend, **kwargs)

	return plot_multi(X, draw_variables,
	                  # Figure
	                  fig=fig, row_count=row_count, col_count=col_count,
	                  share_x=share_x, share_y=share_y,
	                  title=title, subtitles=subtitles, title_x=title_x, title_y=title_y,
	                  width=width, height=height, margin=margin,
	                  # Chart
	                  colors=colors, **kwargs)
