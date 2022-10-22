#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - test the machine learning utility library
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

from nlearn.mixture import *
from nlearn.nlp import WordEmbeddings
from nutil.test import *

####################################################################################################
# LEARN TEST CONSTANTS
####################################################################################################

__LEARN_TEST_CONSTANTS____________________________ = ''

SIZE = 1000

####################################################################################################
# LEARN TEST CLASSES
####################################################################################################

__LEARN_TEST_CLASSES______________________________ = ''


class TestLearn(Test):

	def test_mixture(self):
		test('Fit a Gaussian mixture with one component')
		data = np.random.randn(SIZE, 2)
		data[:, 1] = exp(1 + data[:, 1])
		data[:, 0], data[:, 1] = rotate_point(data[:, 0], data[:, 1], angle=1)
		model = create_gaussian_mixture(data, component_count=1)
		fig = plot_mixture(data, model, title='Gaussian Mixture With One Component')
		fig.show()

		test('Fit a Gaussian mixture with five components')
		data = np.array([[0, -0.1], [1.7, 0.4]])
		data = np.r_[np.dot(np.random.randn(SIZE, 2), data),
		             0.7 * np.random.randn(SIZE, 2) + np.array([-6, 3])]
		model = create_gaussian_mixture(data, component_count=5)
		fig = plot_mixture(data, model, title='Gaussian Mixture With Five Components')
		fig.show()

		test('Fit a Dirichlet process Gaussian mixture with five components')
		data = np.array([[0, -0.1], [1.7, 0.4]])
		data = np.r_[np.dot(np.random.randn(SIZE, 2), data),
		             0.7 * np.random.randn(SIZE, 2) + np.array([-6, 3])]
		model = create_bayesian_gaussian_mixture(data, component_count=5)
		fig = plot_mixture(data, model,
		                   title='Dirichlet Process Gaussian Mixture With Five Components')
		fig.show()

	def test_nlp(self):
		test('Create a handler for word embeddings')
		_ = WordEmbeddings()


####################################################################################################
# LEARN TEST MAIN
####################################################################################################

__LEARN_TEST_MAIN_________________________________ = ''


def main():
	'''Tests the machine learning utility library.'''
	unittest.main()


if __name__ == '__main__':
	main()
