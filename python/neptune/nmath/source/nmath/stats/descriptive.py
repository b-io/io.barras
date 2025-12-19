#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide statistical utilities for descriptive statistics.
########################################################################################################################

from __future__ import annotations

import plotly.graph_objs as go
from scipy import stats

from nformat.common import *
from ngui import charts
from nutil.scalar.number import EPS, format_number
from nutil.struct.util import *


__DESCRIPTIVE_CONSTANTS___________________________________________________________________ = ""


### DEFAULTS ###############################################

# The default number of points
DEFAULT_POINT_COUNT = 100

# The default maximum fraction of unique values (distinct points) for treating a series as discrete
DEFAULT_DISCRETE_RELATIVE_THRESHOLD = 0.2


__DESCRIPTIVE_FIGURES_____________________________________________________________________ = ""


def draw_histogram(
    series,
    bins=None,
    norm="probability",
    # Chart
    color=None,
    index=None,
    name=None,
    opacity=1,
    yaxis=0,
    # Flags
    show_date=False,
    show_legend=True,
    show_name=True,
):
    if is_null(name):
        name = get_name(series)
        name = (name + " " if not is_empty(name) else "") + "Histogram"
    name = charts.get_label(name, yaxis=yaxis, show_date=show_date, show_name=show_name)
    hover_template = charts.get_hover_template(index=index)
    marker = dict(color=color)
    return go.Histogram(
        x=series,
        histnorm=norm,
        xbins=bins,
        # Chart
        customdata=index,
        hovertemplate=hover_template,
        marker=marker,
        name=name,
        opacity=opacity,
        yaxis="y" + str(1 if yaxis == 0 else yaxis),
        # Flags
        showlegend=show_legend,
    )


def plot_histogram(
    df,
    bins=None,
    norm="probability",
    method=None,
    point_count=DEFAULT_POINT_COUNT,
    weights=None,
    # Figure
    fig=None,
    title=None,
    title_x=None,
    title_y=None,
    title_y2=None,
    width=DEFAULT_WIDTH,
    height=DEFAULT_HEIGHT,
    margin=None,
    # Chart
    colors=DEFAULT_COLORS,
    index=None,
    name=None,
    opacity=1,
    yaxis=0,
    # Flags
    show_date=False,
    show_density=True,
    show_legend=True,
    show_name=True,
):
    if is_null(fig):
        fig = charts.create_figure(
            title=title,
            title_x=title_x,
            title_y=title_y,
            title_y2=title_y2,
            width=width,
            height=height,
            margin=margin,
        )
    colors = get_iterator(to_list(colors), cycle=True)

    for s in to_series(df) if is_frame(df) else [df]:
        color = next(colors)
        fig.add_trace(
            draw_histogram(
                s,
                bins=bins,
                norm=norm,
                # Chart
                color=color,
                index=index,
                name=name,
                opacity=opacity / 2 if show_density else opacity,
                yaxis=yaxis,
                # Flags
                show_date=show_date,
                show_legend=show_legend,
                show_name=show_name,
            )
        )
        if show_density:
            fig.add_trace(
                draw_density(
                    s,
                    method=method,
                    point_count=point_count,
                    weights=weights,
                    # Chart
                    color=color,
                    index=index,
                    name=name,
                    opacity=opacity,
                    yaxis=yaxis,
                    # Flags
                    show_date=show_date,
                    show_legend=show_legend,
                    show_name=show_name,
                )
            )
    return fig


def plot_multi_histogram(
    df,
    bins=None,
    norm="probability",
    # Figure
    fig=None,
    row_count=None,
    col_count=None,
    share_x=True,
    share_y=True,
    title=None,
    subtitles=None,
    title_x=None,
    title_y=None,
    width=DEFAULT_WIDTH,
    height=DEFAULT_HEIGHT,
    margin=None,
    # Chart
    colors=DEFAULT_COLORS,
    index=None,
    opacity=1,
    # Flags
    show_date=False,
    show_legend=False,
    show_name=True,
):
    return charts.plot_multi(
        df,
        draw_histogram,
        bins=bins,
        norm=norm,
        # Figure
        fig=fig,
        row_count=row_count,
        col_count=col_count,
        share_x=share_x,
        share_y=share_y,
        title=title,
        subtitles=subtitles,
        title_x=title_x,
        title_y=title_y,
        width=width,
        height=height,
        margin=margin,
        # Chart
        colors=colors,
        index=index,
        opacity=opacity,
        # Flags
        show_date=show_date,
        show_legend=show_legend,
        show_name=show_name,
    )


##############################


def draw_density(
    series,
    method=None,
    point_count=DEFAULT_POINT_COUNT,
    weights=None,
    # Chart
    color=None,
    dash=None,
    fill="none",
    index=None,
    line_width=DEFAULT_LINE_WIDTH,
    marker_size=DEFAULT_MARKER_SIZE,
    mode="lines",
    name=None,
    opacity=1,
    stackgroup=None,
    yaxis=0,
    # Flags
    show_date=False,
    show_legend=True,
    show_name=True,
):
    return charts.draw_series(
        series,
        f=get_density,
        method=method,
        point_count=point_count,
        weights=weights,
        # Chart
        color=color,
        dash=dash,
        fill=fill,
        index=index,
        line_width=line_width,
        marker_size=marker_size,
        mode=mode,
        name=name,
        opacity=opacity,
        stackgroup=stackgroup,
        yaxis=yaxis,
        # Flags
        show_date=show_date,
        show_legend=show_legend,
        show_name=show_name,
    )


def plot_density(
    df,
    method=None,
    point_count=DEFAULT_POINT_COUNT,
    weights=None,
    # Figure
    fig=None,
    title=None,
    title_x=None,
    title_y=None,
    title_y2=None,
    width=DEFAULT_WIDTH,
    height=DEFAULT_HEIGHT,
    margin=None,
    # Chart
    colors=DEFAULT_COLORS,
    dash=None,
    fill="none",
    index=None,
    line_width=DEFAULT_LINE_WIDTH,
    marker_size=DEFAULT_MARKER_SIZE,
    mode="lines",
    name=None,
    opacity=1,
    stackgroup=None,
    yaxis=0,
    # Flags
    show_date=False,
    show_legend=True,
    show_name=True,
):
    return charts.plot_series(
        df,
        f=get_density,
        method=method,
        point_count=point_count,
        weights=weights,
        # Figure
        fig=fig,
        title=title,
        title_x=title_x,
        title_y=title_y,
        title_y2=title_y2,
        width=width,
        height=height,
        margin=margin,
        # Chart
        colors=colors,
        dash=dash,
        fill=fill,
        index=index,
        line_width=line_width,
        marker_size=marker_size,
        mode=mode,
        name=name,
        opacity=opacity,
        stackgroup=stackgroup,
        yaxis=yaxis,
        # Flags
        show_date=show_date,
        show_legend=show_legend,
        show_name=show_name,
    )


def plot_multi_density(
    df,
    method=None,
    point_count=DEFAULT_POINT_COUNT,
    weights=None,
    # Figure
    fig=None,
    row_count=None,
    col_count=None,
    share_x=True,
    share_y=True,
    title=None,
    subtitles=None,
    title_x=None,
    title_y=None,
    width=DEFAULT_WIDTH,
    height=DEFAULT_HEIGHT,
    margin=None,
    # Chart
    colors=DEFAULT_COLORS,
    dash=None,
    fill="none",
    index=None,
    line_width=DEFAULT_LINE_WIDTH,
    marker_size=DEFAULT_MARKER_SIZE,
    mode="lines",
    opacity=1,
    stackgroup=None,
    # Flags
    show_date=False,
    show_legend=False,
    show_name=True,
):
    return charts.plot_multi_series(
        df,
        f=get_density,
        method=method,
        point_count=point_count,
        weights=weights,
        # Figure
        fig=fig,
        row_count=row_count,
        col_count=col_count,
        share_x=share_x,
        share_y=share_y,
        title=title,
        subtitles=subtitles,
        title_x=title_x,
        title_y=title_y,
        width=width,
        height=height,
        margin=margin,
        # Chart
        colors=colors,
        dash=dash,
        fill=fill,
        index=index,
        line_width=line_width,
        marker_size=marker_size,
        mode=mode,
        opacity=opacity,
        stackgroup=stackgroup,
        # Flags
        show_date=show_date,
        show_legend=show_legend,
        show_name=show_name,
    )


##############################


def plot_cumulative_distribution(
    x,
    classes=None,
    labels=None,
    # Figure
    fig=None,
    title="Cumulative Distribution",
    title_x=None,
    title_y="%",
    width=DEFAULT_WIDTH,
    height=DEFAULT_HEIGHT,
    margin=None,
    # Chart
    colors=DEFAULT_COLORS,
    line_width=DEFAULT_LINE_WIDTH,
    marker_size=DEFAULT_MARKER_SIZE,
    # Flags
    show_legend=True,
):
    # Get the sorted unique classes
    sorted_unique_classes = sort(to_set(classes)) if not is_empty(classes) else [0]
    if is_null(fig):
        if is_frame(x):
            names = get_names(x)
            fig = charts.create_figures(
                len(sorted_unique_classes),
                1,
                title=title,
                title_x=names[0],
                title_y=names[1],
                share_x=True,
                width=width,
                height=height,
                margin=margin,
            )
        else:
            fig = charts.create_figures(
                len(sorted_unique_classes),
                1,
                title=title,
                title_x=title_x,
                title_y=title_y,
                share_x=True,
                width=width,
                height=height,
                margin=margin,
            )
    colors = get_iterator(to_list(colors), cycle=True)

    # Convert `x` and classes to arrays
    x = to_array(x)
    classes = to_array(classes)

    # Get the mean value for all the classes
    mean_value = mean(x)

    for i, c in enumerate(sorted_unique_classes):
        # Skip the classes that are not present and get the class values
        if not is_empty(classes):
            class_filter = classes == c
            if not any_values(class_filter):
                continue
            class_values = to_array(sort(x[class_filter]))
        else:
            class_values = to_array(sort(x))
        class_value_count = len(class_values)

        # Draw the cumulative distribution of the class values
        class_value_range = to_array(range(class_value_count)) / (class_value_count - 1) * 100
        class_color = next(colors)
        class_name = str(labels[c] if not is_null(labels) else c)
        fig.add_trace(
            charts.draw(
                x=class_values,
                y=class_value_range,
                # Chart
                color=class_color,
                fill="tozeroy",
                line_width=line_width,
                marker_size=marker_size,
                name=class_name,
                stackgroup=class_name,
                # Flags
                show_legend=show_legend,
            ),
            row=i + 1,
            col=1,
        )
        class_mean_value = mean(class_values)
        fig.add_vline(
            class_mean_value,
            annotation=dict(
                yanchor="top",
                borderpad=4,
                borderwidth=line_width,
                bgcolor="white",
                bordercolor=class_color,
                text=format_number(class_mean_value),
            ),
            line=dict(color=class_color, dash="dash", width=line_width),
            row=i + 1,
            col=1,
        )
        if not is_empty(classes):
            fig.add_vline(
                mean_value,
                annotation=(
                    dict(
                        yanchor="bottom",
                        borderpad=4,
                        borderwidth=line_width,
                        bgcolor="white",
                        bordercolor="black",
                        text=format_number(mean_value),
                    )
                    if i == 0
                    else None
                ),
                line=dict(color="black", dash="dash", width=line_width),
                row=i + 1,
                col=1,
            )
    return fig


__DESCRIPTIVE_PROCESSORS__________________________________________________________________ = ""


def get_density(
    series,
    method: Optional[Any] = None,
    point_count: int = DEFAULT_POINT_COUNT,
    weights: Optional[np.ndarray] = None,
    relative_threshold: float = DEFAULT_DISCRETE_RELATIVE_THRESHOLD,
    absolute_threshold: int = DEFAULT_POINT_COUNT // 2,
) -> pd.Series:
    """Estimates a density for continuous data or a PMF for discrete data."""
    name = get_name(series)
    if not is_empty(name):
        name += " "
    values = get_values(series)
    if weights is not None and len(weights) != len(values):
        raise ValueError("'weights' must have the same length as 'values'")

    # Treat integer-like data with few unique values as discrete
    unique_values = np.unique(values)
    if is_discrete_like(values, relative_threshold=relative_threshold, absolute_threshold=absolute_threshold):
        # Empirical PMF
        sorted_unique_values = np.sort(unique_values)
        if is_null(weights):
            counts = np.array([(values == v).sum() for v in sorted_unique_values], dtype=float)
            pmf = counts / counts.sum()
        else:
            pmf = np.array([weights[values == v].sum() for v in sorted_unique_values], dtype=float)
            pmf = pmf / pmf.sum()

        return pd.Series(pmf, index=sorted_unique_values, name=name + "PMF")

    # Continuous KDE
    if unique_values.size < 2:
        # `gaussian_kde` fails on a constant series
        return pd.Series([0.0], index=[float(unique_values[0])], name=name + "Density")

    x = np.linspace(float(values.min()), float(values.max()), num=point_count)
    kde = stats.gaussian_kde(values, bw_method=method, weights=weights)
    return pd.Series(kde(x), index=x, name=name + "Density")


__DESCRIPTIVE_VALIDATORS__________________________________________________________________ = ""


def is_discrete_like(
    values: np.ndarray,
    *,
    tolerance: float = EPS,
    relative_threshold: float = DEFAULT_DISCRETE_RELATIVE_THRESHOLD,
    absolute_threshold: int = DEFAULT_POINT_COUNT // 2,
) -> bool:
    """Returns whether `values` look discrete enough to prefer a PMF over a KDE."""
    if is_empty(values) or not is_integer_like(values, tolerance=tolerance):
        return False

    # Use both a relative cap (`relative_threshold_ratio * n`) and an absolute cap (`absolute_threshold`)
    unique_values = np.unique(values)
    relative_cap = max(1, int(round(relative_threshold * values.size)))
    absolute_cap = max(1, int(absolute_threshold))
    return bool(unique_values.size <= min(relative_cap, absolute_cap))


def is_integer_like(values: np.ndarray, *, tolerance: float = EPS) -> bool:
    """Returns whether all values are numerically close to integers."""
    if is_empty(values):
        return False

    return bool(np.all(np.isclose(values, np.round(values), atol=tolerance)))
