#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains common utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2025 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from __future__ import annotations

from pandas.core.groupby.generic import DataFrameGroupBy, SeriesGroupBy

from nutil.scalar.common import *

####################################################################################################
# COMMON TABLE CONSTANTS
####################################################################################################

__COMMON_TABLE_CONSTANTS__________________________ = ""

# • DATAFRAME ######################################################################################

__COMMON_DATAFRAME_CONSTANTS______________________ = ""

SERIES_TYPE = pd.Series
SERIES_GROUP_BY_TYPE = SeriesGroupBy

FRAME_TYPE = pd.DataFrame
FRAME_GROUP_BY_TYPE = DataFrameGroupBy

#########################

INDEX_TYPE = pd.Index
TIME_INDEX_TYPE = pd.DatetimeIndex


####################################################################################################
# COMMON TABLE VERIFIERS
####################################################################################################

__COMMON_TABLE_VERIFIERS__________________________ = ""


def is_table(x):
    return is_frame(x) or is_series(x)


# • DATAFRAME ######################################################################################

__COMMON_DATAFRAME_VERIFIERS______________________ = ""


def is_series(x):
    return isinstance(x, SERIES_TYPE) or isinstance(x, SERIES_GROUP_BY_TYPE)


def is_frame(x):
    return isinstance(x, FRAME_TYPE) or isinstance(x, FRAME_GROUP_BY_TYPE)


def is_group_by(x):
    return isinstance(x, SERIES_GROUP_BY_TYPE) or isinstance(x, FRAME_GROUP_BY_TYPE)


#########################


def is_time_series(x):
    return is_table(x) and not is_group_by(x) and is_time_index(x.index)


#########################


def is_index(x):
    return isinstance(x, INDEX_TYPE)


def is_time_index(x):
    return isinstance(x, TIME_INDEX_TYPE)


####################################################################################################
# COMMON TABLE ACCESSORS
####################################################################################################

__COMMON_TABLE_ACCESSORS_____________________ = ""

# • DATAFRAME ######################################################################################

__COMMON_DATAFRAME_ACCESSORS______________________ = ""


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
