#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common table utilities.
########################################################################################################################

from __future__ import annotations

from pandas.core.groupby.generic import DataFrameGroupBy, SeriesGroupBy

from nutil.scalar.common import *

__COMMON_TABLE_TYPES______________________________________________________________________ = ""


# The typing alias for any supported tables
TableType = Union["pd.Series", "pd.DataFrame"]


__COMMON_DATAFRAME_TYPES____________________________________ = ""


SERIES_TYPE = pd.Series
SERIES_GROUP_BY_TYPE = SeriesGroupBy

FRAME_TYPE = pd.DataFrame
FRAME_GROUP_BY_TYPE = DataFrameGroupBy

##############################

INDEX_TYPE = pd.Index
TIME_INDEX_TYPE = pd.DatetimeIndex


__COMMON_TABLE_ACCESSORS__________________________________________________________________ = ""


__COMMON_DATAFRAME_ACCESSORS________________________________ = ""


### COUNTERS ###############################################


def count_rows(df):
    """Counts the rows of the specified dataframe."""
    if is_group_by(df):
        if df.axis == 0:
            return len(df.groups)
        df = df.obj
    if is_series(df):
        return len(df)
    shape = np.shape(df)
    return shape[0] if len(shape) >= 1 else 0


def count_cols(df):
    """Counts the columns of the specified dataframe."""
    if is_group_by(df):
        if df.axis == 1:
            return len(df.groups)
        df = df.obj
    if is_series(df):
        return 1
    shape = np.shape(df)
    return shape[1] if len(shape) >= 2 else 0


__COMMON_TABLE_VALIDATORS_________________________________________________________________ = ""


def is_table(x: Any) -> bool:
    """Returns whether `x` is a Pandas `Series` or `DataFrame`."""
    return is_series(x) or is_frame(x)


def is_table_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a Pandas `Series` or `DataFrame` type."""
    return is_series_type(t) or is_frame_type(t)


__COMMON_DATAFRAME_VALIDATORS_______________________________ = ""


def is_series(x: Any) -> bool:
    """Returns whether `x` is a Pandas `Series`."""
    return isinstance(x, (SERIES_TYPE, SERIES_GROUP_BY_TYPE))


def is_series_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a Pandas `Series` type."""
    return issubclass(t, (SERIES_TYPE, SERIES_GROUP_BY_TYPE))


def is_frame(x: Any) -> bool:
    """Returns whether `x` is a Pandas `DataFrame`."""
    return isinstance(x, (FRAME_TYPE, FRAME_GROUP_BY_TYPE))


def is_frame_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a Pandas `DataFrame` type."""
    return issubclass(t, (FRAME_TYPE, FRAME_GROUP_BY_TYPE))


def is_group_by(x: Any) -> bool:
    """Returns whether `x` is a Pandas `DataSeriesGroupBy` or `DataFrameGroupBy`."""
    return isinstance(x, (SERIES_GROUP_BY_TYPE, FRAME_GROUP_BY_TYPE))


def is_group_by_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a Pandas `DataSeriesGroupBy` or `DataFrameGroupBy` type."""
    return issubclass(t, (SERIES_GROUP_BY_TYPE, FRAME_GROUP_BY_TYPE))


##############################


def is_time_series(x: Any) -> bool:
    """Returns whether `x` is a Pandas `Series` or `DataFrame` with a Pandas `DatetimeIndex`."""
    if is_group_by(x):
        x = x.obj
    return is_table(x) and is_time_index(x.index)


##############################


def is_index(x: Any) -> bool:
    """Returns whether `x` is a Pandas `Index`."""
    return isinstance(x, INDEX_TYPE)


def is_index_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a Pandas `Index` type."""
    return issubclass(t, INDEX_TYPE)


def is_time_index(x: Any) -> bool:
    """Returns whether `x` is a Pandas `DatetimeIndex`."""
    return isinstance(x, TIME_INDEX_TYPE)


def is_time_index_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a Pandas `DatetimeIndex` type."""
    return issubclass(t, TIME_INDEX_TYPE)
