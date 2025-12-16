#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common machine learning utilities.
########################################################################################################################

import plotly.graph_objs as go

from nformat.common import DEFAULT_HEIGHT, DEFAULT_WIDTH
from ngui.charts import create_figure, get_hover_template, update_layout_size
from nutil.math import *

## COMMON LEARN CONFIG PROPERTIES ########################################################

__COMMON_LEARN_CONFIG_PROPERTIES____________________________ = ""


### DEFAULTS ###############################################

# The default LEARN configuration
DEFAULT_LEARN_CONFIG = {
    "nlp": {
        # Pre-trained word vector path
        "wordVectorPath": ""
    }
}


### GLOBALS ################################################

CONFIG.read_dict(DEFAULT_LEARN_CONFIG)
load_config("learn")

WORD_VECTOR_PATH = CONFIG.get("nlp", "wordVectorPath")


## COMMON LEARN FIGURES ##################################################################

__COMMON_LEARN_FIGURES______________________________________ = ""


def plot_confusion_matrix(
    target: Any,
    prediction: Any,
    normalize: bool = False,
    title: str = "Confusion Matrix",
    labels: Optional[Sequence[Any]] = None,  # optional ordering
    color_scale: Union[str, List[Any]] = "Greys",
    reverse_scale: bool = False,
    show_values: bool = True,
    width: int = DEFAULT_WIDTH,
    height: int = DEFAULT_HEIGHT,
    margin: Optional[dict] = None,
) -> go.Figure:
    """
    Builds an interactive confusion-matrix heatmap using Plotly and the GUI helpers.

    Args:
        target: The true labels (array-like / Pandas Series).
        prediction: The predicted labels (array-like / Pandas Series).
        normalize: Whether to normalize the confusion matrix (delegated to `get_confusion_matrix`).
        title: The chart title.
        labels: The class order to enforce on both axes.
        color_scale: The Plotly colorscale to use (e.g., `"Greys"`, `"Viridis"`, or a custom list).
        show_values: Whether to annotate cells with values.
        width: The figure width (delegated to `update_layout_size`).
        height: The figure height (delegated to `update_layout_size`).
        margin: The figure margins (delegated to `update_layout_size`).

    Returns:
        The created Plotly `go.Figure`.
    """
    # Compute the confusion matrix
    cm = get_confusion_matrix(target, prediction, normalize=normalize)

    # Ensure the square union of labels and the optional order
    if is_null(labels):
        labels = list(pd.Index(cm.index).union(pd.Index(cm.columns)))
    cm = cm.reindex(index=labels, columns=labels, fill_value=0)

    # Set the axis titles
    x_title = cm.columns.name or "Predicted Label"
    y_title = cm.index.name or "True Label"

    # Create a figure
    fig = create_figure(title=title, title_x=x_title, title_y=y_title, width=width, height=height, margin=margin)

    # Optionally annotate the cells
    if show_values:
        text = [[f"{v:.2f}" if normalize else f"{int(round(v))}" for v in row] for row in cm.values]
        text_template = "%{text}"
    else:
        text = None
        text_template = None

    # Add the heatmap trace
    fig.add_trace(
        go.Heatmap(
            z=cm.values,
            x=list(cm.columns),
            y=list(cm.index),
            colorscale=color_scale,
            reversescale=reverse_scale,
            colorbar=dict(title="Proportion" if normalize else "Count"),
            text=text,
            texttemplate=text_template,
            hovertemplate=get_hover_template(extra_template="Proportion: %{z:.2f}" if normalize else "Count: %{z}"),
        )
    )

    fig.update_yaxes(autorange="reversed")

    update_layout_size(fig, width=width, height=height, margin=margin)
    return fig


## COMMON LEARN PROCESSORS ###############################################################

__COMMON_LEARN_PROCESSORS___________________________________ = ""


def get_confusion_matrix(target, prediction, normalize=False):
    target = np.asarray(target).reshape(-1)
    prediction = np.asarray(prediction).reshape(-1)
    cm = pd.crosstab(
        target,
        prediction,
        rownames=["Target"],
        colnames=["Prediction"],
        margins=False,
    )
    if normalize:
        cm /= cm.sum(axis=1)
    return cm


############################################################


def to_one_hot(Y, size):
    """
    Converts the specified array of vectors Y to an array of one-hot vectors of the specified size.

    :param Y:    an array of vectors
    :param size: the size of the one-hot vectors

    :return: an array of one-hot vectors of the specified size
    """
    return np.eye(size)[Y.reshape(-1)]


############################################################


def softmax(x: Any):
    """Returns the softmax values for every set of scores in `x`."""
    e_x = np.exp(x - np.max(x))
    return e_x / e_x.sum()
