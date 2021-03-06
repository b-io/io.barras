#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain machine learning utility functions
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

from scipy import linalg
from sklearn import mixture
from sklego.mixture import BayesianGMMOutlierDetector, GMMOutlierDetector

from nutil.gui import *
from nutil.stats.common import *

####################################################################################################
# MIXTURE FUNCTIONS
####################################################################################################

__MIXTURE_________________________________________ = ''


def create_gaussian_mixture(data, n=1, covariance_type='full'):
	"""Creates a Gaussian mixture with the specified number of components and fits the specified
	data with the expectation-maximization (EM) algorithm. Note that the variational inference model
	is using all the components."""
	model = mixture.GaussianMixture(n_components=n, covariance_type=covariance_type)
	return model.fit(data)


def create_bayesian_gaussian_mixture(data, n=1, covariance_type='full'):
	"""Creates a Dirichlet process Gaussian mixture with the specified number of components and fits
	the specified data with the expectation-maximization (EM) algorithm. Note that the Dirichlet
	process model adapts the number of components automatically."""
	model = mixture.BayesianGaussianMixture(n_components=n, covariance_type=covariance_type)
	return model.fit(data)


#########################

def create_outlier_detector(data, n=1, covariance_type='full', init_params='kmeans',
                            method='quantile', threshold=DEFAULT_CONFIDENCE_LEVEL):
	"""Creates a detector based on a Gaussian mixture with the specified number of components and
	fits the specified data with the expectation-maximization (EM) algorithm. Note that the
	variational inference model is using all the components."""
	model = GMMOutlierDetector(n_components=n, covariance_type=covariance_type,
	                           init_params=init_params, method=method, threshold=threshold)
	return model.fit(data)


def create_bayesian_outlier_detector(data, n=1, covariance_type='full', init_params='kmeans',
                                     method='quantile', threshold=DEFAULT_CONFIDENCE_LEVEL):
	"""Creates a detector based on a Dirichlet process Gaussian mixture with the specified number of
	components and fits the specified data with the expectation-maximization (EM) algorithm. Note
	that the Dirichlet process model adapts the number of components automatically."""
	model = BayesianGMMOutlierDetector(n_components=n, covariance_type=covariance_type,
	                                   init_params=init_params, method=method, threshold=threshold)
	return model.fit(data)


# • MIXTURE PLOTS ##################################################################################

__MIXTURE_PLOTS___________________________________ = ''


def plot_clusters(data, labels, means, covariances, fig=None, colors=DEFAULT_COLORS_ITERATOR,
                  dash='dot', index=None, opacity=0.5, precision=100, show_ellipses=True,
                  show_legend=True, show_points=True, size=4, title=None, width=2):
	"""Plots the clusters of the specified data identified by the specified labels and encircles
	them with ellipses using their specified means and covariances."""
	if is_null(fig):
		if is_frame(data):
			names = get_names(data)
			fig = create_figure(title=title, title_x=names[0], title_y=names[1])
		else:
			fig = create_figure(title=title)
	if is_null(index) and is_frame(data):
		index = get_index(data)

	for i, (mean, covariance, color) in enumerate(zip(means, covariances, colors)):
		# Skip the labels that are not present
		if not np.any(labels == i):
			continue

		# Create the trace of the points
		if show_points:
			name = collapse('Cluster ', i + 1)
			fig.add_trace(draw(x=data[labels == i, 0], y=data[labels == i, 1], color=color,
			                   index=index, mode='markers', name=name, show_legend=False,
			                   size=size))

		# Create the trace of an ellipse around the cluster
		if show_ellipses:
			v, w = linalg.eigh(covariance)
			v = 2 * sqrt(2 * v)
			u = w[0] / linalg.norm(w[0])
			angle = atan2(u[1], u[0])
			a, b = v
			color = get_complementary_color(color)
			name = collapse('Cluster ', i + 1, ' Tilted At ', round(angle * RAD_TO_DEG, 2), '°')
			fig.add_trace(draw_ellipse(mean, a, b, angle=angle, color=color, dash=dash,
			                           name=name, opacity=opacity, precision=precision,
			                           show_legend=show_legend, width=width))
			fig.add_trace(draw([mean[0] - a * cos(angle), mean[0] + a * cos(angle)],
			                   [mean[1] - a * sin(angle), mean[1] + a * sin(angle)],
			                   color=color, dash=dash, name=name, opacity=opacity,
			                   show_legend=False, width=width))
			fig.add_trace(draw([mean[0] - b * sin(angle), mean[0] + b * sin(angle)],
			                   [mean[1] + b * cos(angle), mean[1] - b * cos(angle)],
			                   color=color, dash=dash, name=name, opacity=opacity,
			                   show_legend=False, width=width))
	return fig


def plot_mixture(data, model, fig=None, colors=DEFAULT_COLORS_ITERATOR, index=None, opacity=0.5,
                 precision=100, show_ellipses=True, show_legend=True, show_points=True, size=4,
                 title=None, width=2):
	"""Plots the clusters of the specified data identified by the specified model and encircles them
	with ellipses."""
	return plot_clusters(data, model.predict(data), model.means_, model.covariances_, fig=fig,
	                     colors=colors, index=index, opacity=opacity, precision=precision,
	                     show_ellipses=show_ellipses, show_legend=show_legend,
	                     show_points=show_points, size=size, title=title, width=width)


def plot_detector(data, detector, fig=None, colors=DEFAULT_COLORS_ITERATOR, index=None, opacity=0.5,
                  precision=100, show_ellipses=True, show_legend=True, show_points=True, size=4,
                  title=None, width=2):
	"""Plots the clusters of the specified data identified by the specified detector and encircles
	them with ellipses."""
	if is_null(index) and is_frame(data):
		index = get_index(data)

	# Create the trace of the points
	if show_points:
		color = detector.score_samples(data)
		if is_null(fig):
			fig = create_figure(title=title)
		fig.add_trace(draw(x=get_col(data), y=get_col(data, 1), color=color, index=index,
		                   mode='markers', show_legend=False, size=size))

	# Create the trace of an ellipse around each cluster
	if show_ellipses:
		fig = plot_mixture(data, detector.gmm_, fig=fig, colors=colors, index=index,
		                   opacity=opacity, precision=precision, show_legend=show_legend,
		                   show_points=False, size=size, title=title, width=width)
	return fig
