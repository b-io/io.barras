#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain common machine learning utility functions
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

from ngui.chart import *
from nutil.math import *

####################################################################################################
# ML COMMON FUNCTIONS
####################################################################################################

__ML_COMMON_______________________________________ = ''


def get_confusion_matrix(target, prediction, normalize=False):
	cm = pd.crosstab(target, prediction.reshape(prediction.shape[0]),
	                 rownames=['Target'], colnames=['Prediction'], margins=True)
	if normalize:
		cm /= cm.sum(axis=1)
	return cm


##################################################

def to_one_hot(Y, size):
	'''
	Converts the specified array of vectors Y to an array of one-hot vectors of the specified size.

	:param Y:    an array of vectors
	:param size: the size of the one-hot vectors

	:return: an array of one-hot vectors of the specified size
	'''
	return np.eye(size)[Y.reshape(-1)]


##################################################

def softmax(x):
	'''Returns the softmax values for every set of scores in x.'''
	e_x = np.exp(x - np.max(x))
	return e_x / e_x.sum()


# • ML FIGURE ######################################################################################

__ML_FIGURE_______________________________________ = ''


def plot_confusion_matrix(target, prediction, color_map=mplot.cm.gray_r, normalize=False,
                          title='Confusion Matrix'):
	'''Plots the confusion matrix between the specified target and prediction.'''
	cm = get_confusion_matrix(target, prediction, normalize=normalize)
	mplot.matshow(cm, cmap=color_map)
	mplot.colorbar()
	mplot.title(title)
	mplot.xlabel(cm.columns.name)
	mplot.ylabel(cm.index.name)
	ticks = np.arange(len(cm.columns))
	mplot.xticks(ticks, cm.columns, rotation=45)
	mplot.yticks(ticks, cm.index)
	return mplot
