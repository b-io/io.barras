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
from sklearn.metrics import silhouette_samples
from sklego.mixture import BayesianGMMOutlierDetector, GMMOutlierDetector

from nmath.descriptive import *

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


def create_clustering(points, n=50, batch_size=1024,
                      max_iteration_count=DEFAULT_MAX_ITERATION_COUNT, random_state=None,
                      use_mini_batch=False, verbose=VERBOSE):
	if use_mini_batch:
		model = MiniBatchKMeans(n_clusters=n, batch_size=batch_size, max_iter=max_iteration_count,
		                        random_state=random_state, verbose=verbose)
	else:
		model = KMeans(n_clusters=n, max_iter=max_iteration_count, random_state=random_state,
		               verbose=verbose)
	return model.fit(points)


#########################

def create_gaussian_mixture(points, n=1, covariance_type='full',
                            max_iteration_count=DEFAULT_MAX_ITERATION_COUNT):
	'''Creates a Gaussian mixture with the specified number of components and fits the specified
	points with the expectation-maximization (EM) algorithm. Note that the variational inference
	model is using all the components.'''
	model = mixture.GaussianMixture(n_components=n, covariance_type=covariance_type,
	                                max_iter=max_iteration_count)
	return model.fit(points)


def create_bayesian_gaussian_mixture(points, n=1, covariance_type='full',
                                     max_iteration_count=DEFAULT_MAX_ITERATION_COUNT):
	'''Creates a Dirichlet process Gaussian mixture with the specified number of components and fits
	the specified points with the expectation-maximization (EM) algorithm. Note that the Dirichlet
	process model adapts the number of components automatically.'''
	model = mixture.BayesianGaussianMixture(n_components=n,
	                                        covariance_type=covariance_type,
	                                        max_iter=max_iteration_count)
	return model.fit(points)


#########################

def create_outlier_detector(points, n=1, covariance_type='full', init_params='kmeans',
                            max_iteration_count=DEFAULT_MAX_ITERATION_COUNT, method='quantile',
                            threshold=DEFAULT_CONFIDENCE_LEVEL):
	'''Creates a detector based on a Gaussian mixture with the specified number of components and
	fits the specified points with the expectation-maximization (EM) algorithm. Note that the
	variational inference model is using all the components.'''
	model = GMMOutlierDetector(n_components=n, covariance_type=covariance_type,
	                           init_params=init_params, max_iter=max_iteration_count, method=method,
	                           threshold=threshold)
	return model.fit(points)


def create_bayesian_outlier_detector(points, n=1, covariance_type='full', init_params='kmeans',
                                     max_iteration_count=DEFAULT_MAX_ITERATION_COUNT,
                                     method='quantile', threshold=DEFAULT_CONFIDENCE_LEVEL):
	'''Creates a detector based on a Dirichlet process Gaussian mixture with the specified number of
	components and fits the specified points with the expectation-maximization (EM) algorithm. Note
	that the Dirichlet process model adapts the number of components automatically.'''
	model = BayesianGMMOutlierDetector(n_components=n, covariance_type=covariance_type,
	                                   init_params=init_params, max_iter=max_iteration_count,
	                                   method=method, threshold=threshold)
	return model.fit(points)


##################################################

def cluster(points, n=50, batch_size=1024, max_iteration_count=DEFAULT_MAX_ITERATION_COUNT,
            random_state=None, use_mini_batch=False, verbose=VERBOSE):
	return create_clustering(points, n=n, batch_size=batch_size,
	                         max_iteration_count=max_iteration_count, random_state=random_state,
	                         use_mini_batch=use_mini_batch, verbose=verbose).predict(points)


# • CLUSTERING FIGURE ##############################################################################

__CLUSTERING_FIGURE_______________________________ = ''


def plot_clusters(points, classes, means=None, covariances=None, labels=None, std_count=1,
                  fig=None, title='Clusters', title_x=None, title_y=None,
                  colors=DEFAULT_COLORS, dash='dot', index=None, opacity=0.75, precision=100,
                  show_centers=True, show_ellipses=True, show_legend=True, show_points=True,
                  size=DEFAULT_MARKER_SIZE, width=DEFAULT_LINE_WIDTH):
	'''Plots the clusters of the specified points identified by the specified classes and encircles
	them with ellipses using their specified means and covariances.'''
	if is_null(fig):
		if is_frame(points):
			names = get_names(points)
			fig = create_figure(title=title, title_x=names[0], title_y=names[1])
		else:
			fig = create_figure(title=title, title_x=title_x, title_y=title_y)
	if is_null(index) and is_frame(points):
		index = get_index(points)
	colors = get_iterator(to_list(colors), cycle=True)

	# Convert the points, classes and index to arrays
	points = to_array(points)
	classes = to_array(classes)
	index = to_array(index) if not is_null(index) else None

	for i, c in enumerate(sort(to_set(classes))):
		# Skip the classes that are not present
		class_filter = classes == c
		if not any_values(class_filter):
			continue
		cluster_color = next(colors)
		cluster_name = str(labels[c] if not is_null(labels) else c)

		# Draw the cluster points
		if show_points:
			cluster_points = points[class_filter]
			cluster_index = index[class_filter] if not is_null(index) else None
			fig.add_trace(draw(x=get_col(cluster_points), y=get_col(cluster_points, 1),
			                   color=cluster_color, index=cluster_index, mode='markers',
			                   name=cluster_name,
			                   show_legend=False,
			                   size=size))

		# Draw the cluster center
		if show_centers and not is_null(means):
			cluster_center = means[c]
			fig.add_annotation(x=cluster_center[0], y=cluster_center[1], text=web.b(cluster_name),
			                   opacity=opacity, bordercolor='black', bgcolor='white',
			                   borderwidth=width, borderpad=4, showarrow=True, arrowhead=6)

		# Draw an ellipse around the cluster
		if show_ellipses and not is_null(means) and not is_null(covariances):
			cluster_mean = means[c]
			cluster_covariance = covariances[c]
			eigenvalues, eigenvectors = eigh(cluster_covariance)
			a, b = 2 * std_count * sqrt(eigenvalues)
			eigenvector = normalize(eigenvectors[0])
			angle = atan2(eigenvector[1], eigenvector[0])
			color = format_rgb_color(get_complementary_color(cluster_color))
			name = collapse(cluster_name, ' Tilted At ', round(angle * RAD_TO_DEG, 2), '°')
			plot_ellipse(cluster_mean, a, b, angle=angle, precision=precision,
			             fig=fig, color=color, dash=dash, name=name, opacity=opacity,
			             show_legend=show_legend,
			             width=width)
	return fig


def plot_silhouettes(points, classes, labels=None,
                     fig=None, title='Silhouette Coefficients', title_x=None, title_y='%',
                     colors=DEFAULT_COLORS,
                     show_legend=True,
                     width=DEFAULT_LINE_WIDTH):
	return plot_cumulative_distribution(silhouette_samples(points, classes), classes, labels=labels,
	                                    fig=fig, title=title, title_x=title_x, title_y=title_y,
	                                    colors=colors,
	                                    show_legend=show_legend,
	                                    width=width)


##################################################

def plot_mixture(points, model,
                 fig=None, title=None, title_x=None, title_y=None,
                 colors=DEFAULT_COLORS, dash='dot', index=None, opacity=0.75, precision=100,
                 show_centers=True, show_ellipses=True, show_legend=True, show_points=True,
                 size=DEFAULT_MARKER_SIZE, width=DEFAULT_LINE_WIDTH):
	'''Plots the clusters of the specified points identified by the specified model and encircles
	them with ellipses.'''
	return plot_clusters(points, model.predict(points), means=model.means_,
	                     covariances=model.covariances_,
	                     fig=fig, title=title, title_x=title_x, title_y=title_y,
	                     colors=colors, dash=dash, index=index, opacity=opacity,
	                     precision=precision,
	                     show_centers=show_centers, show_ellipses=show_ellipses,
	                     show_legend=show_legend, show_points=show_points,
	                     size=size, width=width)


def plot_detector(points, detector,
                  fig=None, title=None, title_x=None, title_y=None,
                  colors=DEFAULT_COLORS, dash='dot', index=None, opacity=0.75, precision=100,
                  show_ellipses=True, show_legend=True, show_points=True,
                  size=DEFAULT_MARKER_SIZE, width=DEFAULT_LINE_WIDTH):
	'''Plots the clusters of the specified points identified by the specified detector and encircles
	them with ellipses.'''
	if is_null(fig):
		fig = create_figure(title=title, title_x=title_x, title_y=title_y)
	if is_null(index) and is_frame(points):
		index = get_index(points)

	# Draw the cluster points
	if show_points:
		color = detector.score_samples(points)
		fig.add_trace(draw(x=get_col(points), y=get_col(points, 1),
		                   color=color, index=index, mode='markers',
		                   show_legend=False,
		                   size=size))

	# Draw an ellipse around every cluster
	if show_ellipses:
		fig = plot_mixture(points, detector.gmm_,
		                   fig=fig, title=title,
		                   colors=colors, dash=dash, index=index, opacity=opacity,
		                   precision=precision,
		                   show_legend=show_legend, show_points=False,
		                   size=size, width=width)
	return fig