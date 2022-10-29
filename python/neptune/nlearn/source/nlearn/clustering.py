#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain machine learning utility functions for clustering
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

from sklearn import mixture
from sklearn.cluster import KMeans, MiniBatchKMeans
from sklego.mixture import BayesianGMMOutlierDetector, GMMOutlierDetector

from ngui.chart import *
from ngui.common import *
from nmath.common import *

####################################################################################################
# CLUSTERING CONSTANTS
####################################################################################################

__CLUSTERING_CONSTANTS____________________________ = ''

# The default maximum number of iterations
DEFAULT_MAX_ITERATION_COUNT = 1000

####################################################################################################
# CLUSTERING FUNCTIONS
####################################################################################################

__CLUSTERING______________________________________ = ''


def create_clustering(vectors, n=50, batch_size=1024,
                      max_iteration_count=DEFAULT_MAX_ITERATION_COUNT, random_state=None,
                      use_mini_batch=False, verbose=VERBOSE):
	if use_mini_batch:
		model = MiniBatchKMeans(n_clusters=n, batch_size=batch_size, max_iter=max_iteration_count,
		                        random_state=random_state, verbose=verbose)
	else:
		model = KMeans(n_clusters=n, max_iter=max_iteration_count, random_state=random_state,
		               verbose=verbose)
	return model.fit(vectors)


#########################

def create_gaussian_mixture(data, n=1, covariance_type='full',
                            max_iteration_count=DEFAULT_MAX_ITERATION_COUNT):
	'''Creates a Gaussian mixture with the specified number of components and fits the specified
	data with the expectation-maximization (EM) algorithm. Note that the variational inference model
	is using all the components.'''
	model = mixture.GaussianMixture(n_components=n, covariance_type=covariance_type,
	                                max_iter=max_iteration_count)
	return model.fit(data)


def create_bayesian_gaussian_mixture(data, n=1, covariance_type='full',
                                     max_iteration_count=DEFAULT_MAX_ITERATION_COUNT):
	'''Creates a Dirichlet process Gaussian mixture with the specified number of components and fits
	the specified data with the expectation-maximization (EM) algorithm. Note that the Dirichlet
	process model adapts the number of components automatically.'''
	model = mixture.BayesianGaussianMixture(n_components=n,
	                                        covariance_type=covariance_type,
	                                        max_iter=max_iteration_count)
	return model.fit(data)


#########################

def create_outlier_detector(data, n=1, covariance_type='full', init_params='kmeans',
                            max_iteration_count=DEFAULT_MAX_ITERATION_COUNT, method='quantile',
                            threshold=DEFAULT_CONFIDENCE_LEVEL):
	'''Creates a detector based on a Gaussian mixture with the specified number of components and
	fits the specified data with the expectation-maximization (EM) algorithm. Note that the
	variational inference model is using all the components.'''
	model = GMMOutlierDetector(n_components=n, covariance_type=covariance_type,
	                           init_params=init_params, max_iter=max_iteration_count, method=method,
	                           threshold=threshold)
	return model.fit(data)


def create_bayesian_outlier_detector(data, n=1, covariance_type='full', init_params='kmeans',
                                     max_iteration_count=DEFAULT_MAX_ITERATION_COUNT,
                                     method='quantile', threshold=DEFAULT_CONFIDENCE_LEVEL):
	'''Creates a detector based on a Dirichlet process Gaussian mixture with the specified number of
	components and fits the specified data with the expectation-maximization (EM) algorithm. Note
	that the Dirichlet process model adapts the number of components automatically.'''
	model = BayesianGMMOutlierDetector(n_components=n, covariance_type=covariance_type,
	                                   init_params=init_params, max_iter=max_iteration_count,
	                                   method=method, threshold=threshold)
	return model.fit(data)


##################################################

def cluster(vectors, n=50, batch_size=1024, max_iteration_count=DEFAULT_MAX_ITERATION_COUNT,
            random_state=None, use_mini_batch=False, verbose=VERBOSE):
	return create_clustering(vectors, n=n, batch_size=batch_size,
	                         max_iteration_count=max_iteration_count, random_state=random_state,
	                         use_mini_batch=use_mini_batch, verbose=verbose).predict(vectors)


# • CLUSTERING FIGURE ##############################################################################

__CLUSTERING_FIGURE_______________________________ = ''


def plot_clusters(data, classes, means=None, covariances=None, labels=None, std_count=1,
                  fig=None, title=None, colors=DEFAULT_COLORS, dash='dot', index=None, opacity=0.75,
                  precision=100, show_centers=True, show_ellipses=True, show_legend=True,
                  show_points=True, size=4, width=2):
	'''Plots the clusters of the specified data identified by the specified classes and encircles
	them with ellipses using their specified means and covariances.'''
	if is_null(fig):
		if is_frame(data):
			names = get_names(data)
			fig = create_figure(title=title, title_x=names[0], title_y=names[1])
		else:
			fig = create_figure(title=title)
	if is_null(index) and is_frame(data):
		index = get_index(data)
	colors = get_iterator(to_list(colors), cycle=True)
	for i, c in enumerate(sort(to_set(classes))):
		# Skip the classes that are not present
		class_filter = classes == c
		if not any_values(class_filter):
			continue
		cluster_name = paste('Cluster', labels[c] if not is_null(labels) else i + 1)
		cluster_color = next(colors)

		# Create the trace of the cluster points
		if show_points:
			cluster_data = data[class_filter]
			cluster_index = index[class_filter] if not is_null(index) else None
			fig.add_trace(draw(x=get_col(cluster_data, 0), y=get_col(cluster_data, 1),
			                   color=cluster_color, index=cluster_index, mode='markers',
			                   name=cluster_name, show_legend=False, size=size))

		# Create the annotation of the cluster center
		if show_centers and not is_null(means):
			cluster_center = means[c]
			fig.add_annotation(x=cluster_center[0], y=cluster_center[1], text=web.b(cluster_name),
			                   opacity=opacity, bordercolor='black', bgcolor='white', borderwidth=2,
			                   borderpad=4, showarrow=True, arrowhead=6)

		# Create the trace of an ellipse around the cluster
		if show_ellipses and not is_null(means) and not is_null(covariances):
			cluster_mean = means[c]
			cluster_covariance = covariances[c]
			eigenvalues, eigenvectors = eigh(cluster_covariance)
			a, b = 2 * std_count * sqrt(eigenvalues)
			eigenvector = normalize(eigenvectors[0])
			angle = atan2(eigenvector[1], eigenvector[0])
			name = collapse(cluster_name, ' Tilted At ', round(angle * RAD_TO_DEG, 2), '°')
			color = format_rgb_color(get_complementary_color(cluster_color))
			plot_ellipse(cluster_mean, a, b, angle=angle, color=color, fig=fig, dash=dash,
			             name=name, opacity=opacity, precision=precision, show_legend=show_legend,
			             width=width)
	return fig


##################################################

def plot_mixture(data, model, fig=None, title=None, colors=DEFAULT_COLORS, dash='dot', index=None,
                 opacity=0.75, precision=100, show_centers=True, show_ellipses=True,
                 show_legend=True, show_points=True, size=4, width=2):
	'''Plots the clusters of the specified data identified by the specified model and encircles them
	with ellipses.'''
	return plot_clusters(data, model.predict(data), means=model.means_,
	                     covariances=model.covariances_, fig=fig, title=title, colors=colors,
	                     dash=dash, index=index, opacity=opacity, precision=precision,
	                     show_centers=show_centers, show_ellipses=show_ellipses,
	                     show_legend=show_legend, show_points=show_points, size=size, width=width)


def plot_detector(data, detector, fig=None, title=None, colors=DEFAULT_COLORS, dash='dot',
                  index=None, opacity=0.75, precision=100, show_ellipses=True, show_legend=True,
                  show_points=True, size=4, width=2):
	'''Plots the clusters of the specified data identified by the specified detector and encircles
	them with ellipses.'''
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
		fig = plot_mixture(data, detector.gmm_, fig=fig, title=title, colors=colors, dash=dash,
		                   index=index, opacity=opacity, precision=precision,
		                   show_legend=show_legend, show_points=False, size=size, width=width)
	return fig
