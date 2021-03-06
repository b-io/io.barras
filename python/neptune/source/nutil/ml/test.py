#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - test machine learning utility functions
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

from nutil.ml.mixture import *
from nutil.test import *

####################################################################################################
# ML TEST CONSTANTS
####################################################################################################

__ML_TEST_CONSTANTS_______________________________ = ''

SIZE = 1000

####################################################################################################
# ML TEST CLASSES
####################################################################################################

__ML_TEST_CLASSES_________________________________ = ''


class TestML(Test):

	def test_mixture(self):
		test('Fit a Gaussian mixture with one component')
		data = np.random.randn(SIZE, 2)
		data[:, 1] = exp(1 + data[:, 1])
		data[:, 0], data[:, 1] = rotate(data[:, 0], data[:, 1], 1)
		model = create_gaussian_mixture(data, n=1)
		fig = plot_mixture(data, model, title='Gaussian Mixture With One Component')
		fig.show()

		test('Fit a Gaussian mixture with five components')
		data = np.array([[0, -0.1], [1.7, 0.4]])
		data = np.r_[np.dot(np.random.randn(SIZE, 2), data),
		             0.7 * np.random.randn(SIZE, 2) + np.array([-6, 3])]
		model = create_gaussian_mixture(data, n=5)
		fig = plot_mixture(data, model, title='Gaussian Mixture With Five Components')
		fig.show()

		test('Fit a Dirichlet process Gaussian mixture with five components')
		data = np.array([[0, -0.1], [1.7, 0.4]])
		data = np.r_[np.dot(np.random.randn(SIZE, 2), data),
		             0.7 * np.random.randn(SIZE, 2) + np.array([-6, 3])]
		model = create_bayesian_gaussian_mixture(data, n=5)
		fig = plot_mixture(data, model,
		                   title='Dirichlet Process Gaussian Mixture With Five Components')
		fig.show()


####################################################################################################
# ML TEST MAIN
####################################################################################################

__ML_TEST_MAIN_________________________________ = ''


def main():
	"""Tests the machine learning library."""
	unittest.main()


if __name__ == '__main__':
	main()
