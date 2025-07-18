#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain common utility functions
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

from nutil.common.collections import *

####################################################################################################
# COMMON DATAFRAME CONSTANTS
####################################################################################################

__COMMON_DATAFRAME_CONSTANTS______________________ = ""

SERIES_TYPE = pd.Series
SERIES_GROUP_TYPE = pd.core.groupby.generic.SeriesGroupBy

FRAME_TYPE = pd.DataFrame
FRAME_GROUP_TYPE = pd.core.groupby.generic.DataFrameGroupBy

#########################

INDEX_TYPE = pd.Index
TIME_INDEX_TYPE = pd.DatetimeIndex

####################################################################################################
# COMMON DATAFRAME VERIFIERS
####################################################################################################

__COMMON_DATAFRAME_VERIFIERS________________________________ = ""


def is_table(x):
    return is_series(x) or is_frame(x)


def is_series(x):
    return isinstance(x, SERIES_TYPE) or isinstance(x, SERIES_GROUP_TYPE)


def is_frame(x):
    return isinstance(x, FRAME_TYPE) or isinstance(x, FRAME_GROUP_TYPE)


def is_group(x):
    return isinstance(x, SERIES_GROUP_TYPE) or isinstance(x, FRAME_GROUP_TYPE)


#########################


def is_time_series(x):
    return is_table(x) and not is_group(x) and is_time_index(x.index)


#########################


def is_index(x):
    return isinstance(x, INDEX_TYPE)


def is_time_index(x):
    return isinstance(x, TIME_INDEX_TYPE)


####################################################################################################
# COMMON DATAFRAME PROPERTIES
####################################################################################################

__COMMON_DATAFRAME_ACCESSORS______________________ = ""


def get_row(df, i=0):
    """Returns the row of the specified dataframe at the specified index."""
    if is_group(df):
        df = get_values(df)
    elif is_table(df):
        return df.iloc[i:] if i == -1 else df.iloc[i : i + 1]
    return df[i]


def get_first_row(df):
    """Returns the first row of the specified dataframe."""
    return get_row(df, 0)


def get_last_row(df):
    """Returns the last row of the specified dataframe."""
    return get_row(df, -1)


def get_rows(df):
    """Returns the rows of the specified dataframe."""
    if is_frame(df):
        return [row for _, row in df.iterrows()]
    elif is_series(df):
        return [row for _, row in df.items()]
    return [get_row(df, i) for i in range(count_rows(df))]


#########################


def get_col(df, j=0):
    """Returns the column of the specified dataframe at the specified index."""
    if is_group(df):
        df = get_values(df)
    elif is_frame(df):
        return df.iloc[:, j]
    elif is_series(df):
        return df.iloc[:]
    return df[:, j]


def get_first_col(df):
    """Returns the first column of the specified dataframe."""
    return get_col(df, 0)


def get_last_col(df):
    """Returns the last column of the specified dataframe."""
    return get_col(df, -1)


def get_cols(df):
    """Returns the columns of the specified dataframe."""
    if is_frame(df):
        return [col for _, col in df.items()]
    elif is_series(df):
        return [df]
    return [get_col(df, j) for j in range(count_cols(df))]


####################################################################################################
# COMMON DATAFRAME CONVERTERS
####################################################################################################

__COMMON_DATAFRAME_CONVERTERS_____________________ = ""


def to_series(data, name=None, index=None, type=None):
    """Converts the specified collection to a series."""
    if is_empty(data) and not is_table(data):
        data = []
        type = OBJECT_TYPE
    elif is_group(data):
        data = data.obj
    elif not is_collection(data):
        data = create_array(len(get_index(index)), fill=data, type=type)
    if is_frame(data):
        if count_cols(data) > 1:
            return get_cols(data)
        series = get_col(data) if not is_empty(data) else pd.Series(data=data, dtype=type)
    elif is_series(data):
        series = data.copy()
    else:
        series = pd.Series(data=data, dtype=type)
    if not is_null(name):
        set_names(series, name)
    if not is_null(index):
        set_index(series, index)
    return series


def to_time_series(data, name=None, index=None, type=FLOAT_ELEMENT_TYPE):
    """Converts the specified collection to a time series."""
    if not is_null(index):
        index = to_timestamp(to_array(index))
    return to_series(data, name=name, index=index, type=type)


#########################


def to_frame(data, names=None, index=None, index_name="index", type=None):
    """Converts the specified collection to a dataframe."""
    if is_empty(data) and not is_table(data):
        data = []
        type = OBJECT_TYPE
    elif is_group(data):
        data = data.obj
    elif not is_collection(data):
        data = create_array(len(get_index(index)), len(get_names(names)), fill=data, type=type)
    if is_frame(data):
        frame = data.copy()
    elif is_series(data):
        frame = data.to_frame()
    elif is_dict(data):
        frame = pd.DataFrame.from_dict(data, dtype=type, orient="index")
    else:
        frame = pd.DataFrame(data=data, dtype=type)
    if not is_null(names):
        set_names(frame, names)
    if not is_null(index):
        set_index(frame, index)
    set_index_name(frame, index_name)
    return frame


def to_time_frame(data, names=None, index=None, index_name="index", type=FLOAT_ELEMENT_TYPE):
    """Converts the specified collection to a time frame."""
    if not is_null(index):
        index = to_timestamp(to_array(index))
    return to_frame(data, names=names, index=index, index_name=index_name, type=type)


####################################################################################################
# COMMON DATAFRAME PROCESSORS
####################################################################################################

__COMMON_DATAFRAME_PROCESSORS_____________________ = ""


def combine_all(*args, f):
    return reduce(lambda left, right: combine(left, right, f), *args)


def combine(left, right, f):
    """Combines the specified left dataframe with the specified right dataframe with the specified
    function on the common columns (or on the specified columns if they are not null)."""
    return to_frame(left).combine(to_frame(right), f)


#########################


def concat_rows(*rows, copy=False, ignore_index=False, sort=False, verify_integrity=False):
    """Concatenates the specified rows to a dataframe."""
    df = pd.concat(
        [to_frame(row) for row in to_collection(*rows)],
        axis=0,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
    )
    if count_cols(df) == 1:
        return to_series(df)
    return df


def concat_cols(*cols, copy=False, ignore_index=False, sort=False, verify_integrity=False):
    """Concatenates the specified columns to a dataframe."""
    df = pd.concat(
        to_collection(*cols),
        axis=1,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
    )
    if count_cols(df) == 1:
        return to_series(df)
    return df


#########################


def count_rows(df):
    """Counts the rows of the specified dataframe."""
    if is_group(df):
        if df.axis == 0:
            return len(df.groups)
        df = df.obj
    if is_series(df):
        return len(df)
    shape = np.shape(df)
    return shape[0] if len(shape) >= 1 else 0


def count_cols(df):
    """Counts the columns of the specified dataframe."""
    if is_group(df):
        if df.axis == 1:
            return len(df.groups)
        df = df.obj
    if is_series(df):
        return 1
    shape = np.shape(df)
    return shape[1] if len(shape) >= 2 else 0


#########################


def fill_null_all(df, model, numeric_default=None, object_default=None):
    if is_series(df):
        return fill_null_rows(
            df, get_index(model), numeric_default=numeric_default, object_default=object_default
        )
    return fill_null(
        sort_index(
            df.reindex(
                columns=unique(get_names(df) + get_names(model)),
                index=unique(get_index(df) + get_index(model)),
            )
        ),
        numeric_default=numeric_default,
        object_default=object_default,
    )


def fill_null_rows(df, index, numeric_default=None, object_default=None):
    if is_table(index):
        index = get_index(index)
    return fill_null(
        sort_index(df.reindex(index=unique(get_index(df) + to_list(index)))),
        numeric_default=numeric_default,
        object_default=object_default,
    )


def fill_null_cols(df, names, numeric_default=None, object_default=None):
    if is_table(names):
        names = get_names(names)
    return fill_null(
        sort_index(df.reindex(columns=unique(get_names(df) + to_list(names)))),
        numeric_default=numeric_default,
        object_default=object_default,
    )


#########################


def filter_rows_with(df, row, f, *args, **kwargs):
    """Returns the rows of the specified dataframe whose values return True with the specified
    row and function for all the specified keys."""
    if is_empty(df):
        return df
    return df.loc[
        reduce_and([apply(df[k], f, v, *args, **kwargs) for k, v in row.items() if k in df])
    ]


def filter_rows_not_with(df, row, f, *args, **kwargs):
    """Returns the rows of the specified dataframe whose values return False with the specified
    row and function for all the specified keys."""
    if is_empty(df):
        return df
    return df.loc[
        reduce_and([invert(apply(df[k], f, v, *args, **kwargs)) for k, v in row.items() if k in df])
    ]


def filter_any_rows_with(df, row, f, *args, **kwargs):
    """Returns the rows of the specified dataframe whose values return True with the specified
    row and function for at least one specified key."""
    if is_empty(df):
        return df
    return df.loc[
        reduce_or([apply(df[k], f, v, *args, **kwargs) for k, v in row.items() if k in df])
    ]


def filter_any_rows_not_with(df, row, f, *args, **kwargs):
    """Returns the rows of the specified dataframe whose values return False with the specified
    row and function for at least one specified key."""
    if is_empty(df):
        return df
    return df.loc[
        reduce_or([invert(apply(df[k], f, v, *args, **kwargs)) for k, v in row.items() if k in df])
    ]


#########################


def filter_rows(df, row):
    """Returns the rows of the specified dataframe that match the specified row for all the common
    columns."""
    if is_empty(df) or is_null(row):
        return df
    return df.loc[reduce_and([df[k] == v for k, v in row.items() if k in df])]


def filter_rows_not(df, row):
    """Returns the rows of the specified dataframe that do not match the specified row for all the
    common columns."""
    if is_empty(df) or is_null(row):
        return df
    return df.loc[reduce_and([df[k] != v for k, v in row.items() if k in df])]


def filter_any_rows(df, row):
    """Returns the rows of the specified dataframe that match the specified row for at least one
    common column."""
    if is_empty(df) or is_null(row):
        return df
    return df.loc[reduce_or([df[k] == v for k, v in row.items() if k in df])]


def filter_any_rows_not(df, row):
    """Returns the rows of the specified dataframe that do not match the specified row for at least
    one common column."""
    if is_empty(df) or is_null(row):
        return df
    return df.loc[reduce_or([df[k] != v for k, v in row.items() if k in df])]


#########################


def filter_rows_in(df, rows):
    """Returns the rows of the specified dataframe that match the specified rows for all the common
    columns."""
    if is_empty(df) or is_null(rows):
        return df
    return df.loc[reduce_and([df[k].isin(to_set(values)) for k, values in rows.items() if k in df])]


def filter_rows_not_in(df, rows):
    """Returns the rows of the specified dataframe that do not match the specified rows for all the
    common columns."""
    if is_empty(df) or is_null(rows):
        return df
    return df.loc[
        reduce_and([invert(df[k].isin(to_set(values))) for k, values in rows.items() if k in df])
    ]


def filter_any_rows_in(df, rows):
    """Returns the rows of the specified dataframe that match the specified rows for at least one
    common column."""
    if is_empty(df) or is_null(rows):
        return df
    return df.loc[reduce_or([df[k].isin(to_set(values)) for k, values in rows.items() if k in df])]


def filter_any_rows_not_in(df, rows):
    """Returns the rows of the specified dataframe that do not match the specified rows for at least
    one common column."""
    if is_empty(df) or is_null(rows):
        return df
    return df.loc[
        reduce_or([invert(df[k].isin(to_set(values))) for k, values in rows.items() if k in df])
    ]


#########################


def join_all(*args, how="inner", on=None, index_name="index", suffix="2", validate="m:m"):
    return reduce(
        lambda left, right: join(
            left, right, how=how, on=on, index_name=index_name, suffix=suffix, validate=validate
        ),
        *args,
    )


def join(left, right, how="inner", on=None, index_name="index", suffix="2", validate="m:m"):
    """Joins the specified left dataframe with the specified right dataframe on the common index (or
    on the specified index if it is not null)."""
    return set_index_name(
        to_frame(left).join(to_frame(right), how=how, on=on, rsuffix=suffix, validate=validate),
        index_name,
    )


#########################


def merge_all(
    *args,
    how="inner",
    on=None,
    index_name="index",
    suffixes=[None, "2"],
    indicator=None,
    validate="m:m",
):
    return reduce(
        lambda left, right: merge(
            left,
            right,
            how=how,
            on=on,
            index_name=index_name,
            suffixes=suffixes,
            indicator=indicator,
            validate=validate,
        ),
        *args,
    )


def merge(
    left,
    right,
    how="inner",
    on=None,
    index_name="index",
    suffixes=[None, "2"],
    indicator=None,
    validate="m:m",
):
    """Merges the specified left dataframe with the specified right dataframe on the common index
    (or on the specified index and/or columns if they are not null)."""
    return set_index_name(
        to_frame(left).merge(
            to_frame(right),
            copy=False,
            how=how,
            on=on if not is_null(on) else get_names(left.index),
            suffixes=suffixes,
            indicator=indicator,
            validate=validate,
        ),
        index_name,
    )


#########################


def pivot(df, names, index, values):
    return df.pivot(columns=names, index=index, values=values)


def unpivot(df, value, names=None):
    df = filter_not_null(df.unstack().reset_index(name=value), keys=value)
    if not is_null(names):
        df.rename(
            columns={"level_" + str(i): name for i, name in enumerate(to_list(names))}, inplace=True
        )
    return df


#########################


def remove_row(df, index=None, level=None, inplace=False):
    return df.drop(index=index, level=level, inplace=inplace)


def remove_row_at(df, i):
    if i < 0:
        i = count_rows(df) + i
    return df.iloc[to_list(range(0, i)) + to_list(range(i + 1, count_rows(df))), :]


def remove_col(df, names=None, level=None, inplace=False):
    return df.drop(columns=names, level=level, inplace=inplace)


def remove_col_at(df, j):
    if j < 0:
        j = count_cols(df) + j
    return df.iloc[:, to_list(range(0, j)) + to_list(range(j + 1, count_cols(df)))]


#########################


def rename(df, names=None, index=None, level=None):
    if is_all_empty(names, index):
        set_names(df, range(count_cols(df)))
    else:
        if not is_null(names):
            set_names(df, names)
        if not is_null(index):
            df.rename(index=index, level=level, copy=False, inplace=True)
    return df


def rename_all(*args, names=None, index=None, level=None):
    for arg in args:
        rename(arg, names=names, index=index, level=level)


#########################


def rotate_rows(df, drop=True, prepend=False):
    """Rotates the rows of the specified dataframe."""
    if is_empty(df):
        return df
    if prepend:
        df = concat_rows(get_last_row(df), df)
        if drop:
            df = remove_row_at(df, -1)
    else:
        df = concat_rows(df, get_first_row(df))
        if drop:
            df = remove_row_at(df, 0)
    return df


def rotate_cols(df, drop=True, prepend=False):
    """Rotates the columns of the specified dataframe."""
    if is_empty(df):
        return df
    if prepend:
        df = concat_cols(get_last_col(df), df)
        if drop:
            df = remove_col_at(df, -1)
    else:
        df = concat_cols(df, get_first_col(df))
        if drop:
            df = remove_col_at(df, 0)
    return df


#########################


def sum_rows(df):
    """Returns the sum of the rows of the specified dataframe."""
    return df.sum(axis=0)


def sum_cols(df):
    """Returns the sum of the columns of the specified dataframe."""
    return df.sum(axis=1)


def product_rows(df):
    """Returns the product of the rows of the specified dataframe."""
    return df.product(axis=0)


def product_cols(df):
    """Returns the product of the columns of the specified dataframe."""
    return df.product(axis=1)
