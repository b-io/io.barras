#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the machine learning utilities.
########################################################################################################################

from __future__ import annotations

import unittest

import matplotlib
from sklearn.datasets import make_blobs

from nlearn.clustering import *
from nlearn.nlp import *
from nutil.io.logging import configure_logging
from nutil.test.unittest import Test

__LEARN_TEST_SETTINGS_____________________________________________________________________ = ""

matplotlib.use("Agg")


__LEARN_TEST_CONSTANTS____________________________________________________________________ = ""

SIZE = 1000


__LEARN_TEST_CASES________________________________________________________________________ = ""


class TestLearn(Test):

    def __init__(self, methodName="runTest"):
        super().__init__(methodName=methodName)

        charts.disable_default_rendering()

    def test_clustering(self):
        logging.info("Cluster")
        data, classes, centers = make_blobs(
            n_samples=500,
            n_features=2,
            centers=4,
            cluster_std=1,
            center_box=(-10, 10),
            shuffle=True,
            random_state=0,
            return_centers=True,
        )
        fig = plot_clusters(data, classes, means=centers)
        fig.show()
        # - K-means
        model = create_clustering(data, n=4)
        fig = plot_clusters(
            data,
            model.predict(data),
            means=model.cluster_centers_,
            title="K-Means With Four Components",
        )
        fig.show()
        fig = plot_silhouettes(data, model.predict(data))
        fig.show()
        model = create_clustering(data, n=5)
        fig = plot_clusters(
            data,
            model.predict(data),
            means=model.cluster_centers_,
            title="K-Means With Five Components",
        )
        fig.show()
        fig = plot_silhouettes(data, model.predict(data))
        fig.show()
        # - Gaussian mixture
        model = create_gaussian_mixture(data, n=5)
        fig = plot_mixture(data, model, title="Gaussian Mixture With Five Components")
        fig.show()
        # - Dirichlet process Gaussian mixture
        model = create_bayesian_gaussian_mixture(data, n=5)
        fig = plot_mixture(data, model, title="Dirichlet Process Gaussian Mixture With Five Components")
        fig.show()

        logging.info("Fit a Gaussian mixture with one component")
        data = np.random.randn(SIZE, 2)
        data[:, 1] = exp(1 + data[:, 1])
        data[:, 0], data[:, 1] = rotate_point(data[:, 0], data[:, 1], angle=1)
        model = create_gaussian_mixture(data, n=1)
        fig = plot_mixture(data, model, title="Gaussian Mixture With One Component")
        fig.show()

        logging.info("Fit a Gaussian mixture with five components")
        data = np.array([[0, -0.1], [1.7, 0.4]])
        data = np.r_[
            np.dot(np.random.randn(SIZE, 2), data),
            0.7 * np.random.randn(SIZE, 2) + np.array([-6, 3]),
        ]
        model = create_gaussian_mixture(data, n=5)
        fig = plot_mixture(data, model, title="Gaussian Mixture With Five Components")
        fig.show()

        logging.info("Fit a Dirichlet process Gaussian mixture with five components")
        data = np.array([[0, -0.1], [1.7, 0.4]])
        data = np.r_[
            np.dot(np.random.randn(SIZE, 2), data),
            0.7 * np.random.randn(SIZE, 2) + np.array([-6, 3]),
        ]
        model = create_bayesian_gaussian_mixture(data, n=5)
        fig = plot_mixture(data, model, title="Dirichlet Process Gaussian Mixture With Five Components")
        fig.show()

    def test_common(self):
        # Create a minimal, deterministic sample
        y_true = np.array([0, 0, 1, 1, 2, 2])
        y_pred = np.array([0, 1, 1, 1, 2, 0])

        # Build the confusion matrix plot
        fig = plot_confusion_matrix(
            y_true,
            y_pred,
            normalize=False,
            title="Confusion Matrix",
        )

        # Check if the heatmap trace exists
        assert any(isinstance(trace, go.Heatmap) for trace in fig.data)

        # Check if labels on the heatmap match the number of classes seen
        labels = sorted(set(y_true) | set(y_pred))
        heat = next(tr for tr in fig.data if isinstance(tr, go.Heatmap))
        assert len(list(dict.fromkeys(list(heat.x)))) == len(labels)
        assert len(list(dict.fromkeys(list(heat.y)))) == len(labels)

        # Show the figure
        fig.show()

    def test_nlp(self):
        logging.info("Create a handler for word embeddings")
        _ = WordEmbeddings()


__LEARN_TEST_RUNNERS______________________________________________________________________ = ""


### MAIN ###################################################


def main() -> None:
    """Tests the machine learning utilities."""
    configure_logging(level=logging.DEBUG)
    unittest.main()


if __name__ == "__main__":
    main()
