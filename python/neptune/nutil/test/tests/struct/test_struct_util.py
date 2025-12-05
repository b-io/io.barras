#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the structure utilities.
########################################################################################################################

from __future__ import annotations

import math
import unittest
from collections import OrderedDict
from typing import Any, Mapping

import numpy as np
import pandas as pd

from nutil.config import BOOLEAN_ELEMENT_TYPE, FLOAT_ELEMENT_TYPE
from nutil.enums import Aggregation, Position
from nutil.struct import util
from nutil.struct.collection.registry.ordered_set import OrderedSet

## STRUCT UTIL TEST CASES ###############################################################

__STRUCT_UTIL_TEST_CASES____________________________________ = ""


### BASIC ACCESSORS ######################################################################


def test_get_on_list_and_array_and_dict() -> None:
    data_list = [10, 20, 30]
    data_array = np.array([[1, 2], [3, 4]])
    data_dict = OrderedDict([("a", 1), ("b", 2), ("c", 3)])

    assert util.get(data_list, 1) == 20
    np.testing.assert_array_equal(util.get(data_array, 1), np.array([3, 4]))
    assert util.get(data_array, 0, axis=1)[0] == 1  # first column
    assert util.get(data_dict, 0) == 1
    assert util.get_first(data_list) == 10
    assert util.get_last(data_list) == 30


def test_get_middle_for_list_and_dataframe() -> None:
    data = [1, 2, 3, 4, 5]
    assert util.get_middle(data) == 3  # (5-1)//2 == 2

    df = pd.DataFrame({"a": [1, 2, 3], "b": [4, 5, 6]})
    # 3 rows → middle row index 1
    middle_row = util.get_middle(df, axis=0)
    assert isinstance(middle_row, pd.DataFrame)
    assert middle_row.iloc[0]["a"] == 2
    assert middle_row.iloc[0]["b"] == 5


def test_get_shape_for_list_tuple_and_dataframe() -> None:
    data_list = [1, 2, 3]
    assert util.get_shape(data_list) == (3,)

    shape_tuple = (2, 3)
    assert util.get_shape(shape_tuple) == shape_tuple

    df = pd.DataFrame({"a": [1, 2], "b": [3, 4], "c": [5, 6]})
    assert util.get_shape(df) == (2, 3)


def test_get_iterator_and_get_next() -> None:
    it = util.get_iterator([1, 2, 3])
    assert next(it) == 1
    assert next(it) == 2

    # Element stays as-is
    assert util.get_next(42) == 42

    # Cycling iterator
    cycle_it = util.get_iterator([1, 2], cycle=True)
    assert next(cycle_it) == 1
    assert next(cycle_it) == 2
    assert next(cycle_it) == 1  # wrapped


### NAMES / KEYS / INDEX ################################################################


def test_get_names_and_keys_for_dataframe_and_series() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [3, 4]})
    s = pd.Series([10, 20], name="value", index=["x", "y"])

    names = util.get_names(df)
    assert list(names) == ["a", "b"]

    series_name = util.get_name(s)
    assert series_name == "value"

    keys_df = util.get_keys(df)
    assert isinstance(keys_df, OrderedSet)
    assert list(keys_df) == ["a", "b"]

    keys_series = util.get_keys(s)
    assert list(keys_series) == ["x", "y"]


def test_get_index_and_index_name_for_dataframe() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [3, 4]}, index=pd.Index(["i1", "i2"], name="idx"))
    index = util.get_index(df)
    assert index == ["i1", "i2"]

    index_name = util.get_index_name(df)
    assert index_name == "idx"


def test_common_and_uncommon_names_and_keys() -> None:
    df1 = pd.DataFrame({"a": [1], "b": [2], "c": [3]})
    df2 = pd.DataFrame({"b": [4], "c": [5], "d": [6]})

    common_names = util.get_common_names(df1, df2)
    assert list(common_names) == ["b", "c"]

    uncommon_names = util.get_uncommon_names(df1, df2)
    assert list(uncommon_names) == ["a"]

    common_keys = util.get_common_keys(df1, df2)
    assert list(common_keys) == ["b", "c"]

    uncommon_keys = util.get_uncommon_keys(df1, df2)
    assert list(uncommon_keys) == ["a"]


def test_get_item_items_and_values_for_dict_and_dataframe() -> None:
    d = OrderedDict([("x", 1), ("y", 2)])
    assert util.get_item(d, keys=["x"]) == ("x", 1)

    items = util.get_items(d)
    assert items == [("x", 1), ("y", 2)]

    df = pd.DataFrame({"a": [1, 2], "b": [3, 4]})
    values = util.get_values(df)
    assert values.shape == (2, 2)
    np.testing.assert_array_equal(values, df.values)

    single_value = util.get_value(df)
    # Simplify returns a scalar only when there is one element; here it remains the array
    assert isinstance(single_value, np.ndarray)


### ELEMENT TYPES ########################################################################


def test_get_element_types_on_dataframe_series_array_and_scalar() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [1.0, 2.0]})
    types_df = util.get_element_types(df)
    assert set(types_df.keys()) == {"a", "b"}
    assert np.issubdtype(types_df["a"], np.integer)
    assert np.issubdtype(types_df["b"], np.floating)

    s = pd.Series([1.0, 2.0])
    t_series = util.get_element_types(s)
    assert np.issubdtype(t_series, np.floating)

    arr = np.array([1, 2, 3], dtype=np.int64)
    t_arr = util.get_element_types(arr)
    assert np.issubdtype(t_arr, np.integer)

    # Non-subscriptable scalar
    types_scalar = util.get_element_types(42)
    assert types_scalar[0] is int


def test_get_min_element_type_promotes_safely() -> None:
    t = util.get_min_element_type(
        np.array([1, 2], dtype=np.int32), np.array([1.5], dtype=np.float64)
    )
    assert np.issubdtype(t, np.floating)
    assert t == np.dtype(FLOAT_ELEMENT_TYPE) or np.issubdtype(t, np.float64)


### SET NAMES / KEYS / INDEX / VALUES / TYPES ###########################################


def test_set_names_for_dataframe_and_series_and_generic() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [3, 4]})
    util.set_names(df, ["x", "y"])
    assert list(df.columns) == ["x", "y"]

    s = pd.Series([1, 2], name="old")
    util.set_names(s, "new")
    assert s.name == "new"

    d = OrderedDict([("a", 1), ("b", 2)])
    util.set_keys(d, ["x", "y"])
    assert d == OrderedDict([("x", 1), ("y", 2)])


def test_set_index_and_multiindex_and_index_name() -> None:
    df = pd.DataFrame({"a": [1, 2]}, index=[10, 20])
    util.set_index(df, ["i1", "i2"], index_name="idx")
    assert list(df.index) == ["i1", "i2"]
    assert df.index.name == "idx"

    df2 = pd.DataFrame({"a": [1, 2]})
    util.set_index(df2, [("A", 1), ("B", 2)], index_name="mi")
    assert isinstance(df2.index, pd.MultiIndex)
    assert list(df2.index) == [("A", 1), ("B", 2)]
    # For MultiIndex, index_name is expanded as mi1, mi2
    assert df2.index.names == ["mi1", "mi2"]


def test_set_values_for_dataframe_series_array_and_dict() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [3, 4]})
    util.set_values(df, 9)
    np.testing.assert_array_equal(df.values, np.full((2, 2), 9))

    s = pd.Series([1, 2], index=["x", "y"])
    util.set_values(s, [10, 20])
    assert list(s.values) == [10, 20]

    arr = np.array([1, 2, 3])
    util.set_values(arr, [4, 5, 6])
    np.testing.assert_array_equal(arr, np.array([4, 5, 6]))

    d = {"a": 1, "b": 2}
    util.set_values(d, [10, 20])
    assert d == {"a": 10, "b": 20}


def test_set_element_types_for_dataframe_series_array_and_dict() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [3.0, 4.0]})
    util.set_element_types(df, {"a": np.float64, "b": np.float64})
    assert np.issubdtype(df["a"].dtype, np.floating)
    assert np.issubdtype(df["b"].dtype, np.floating)

    s = pd.Series([1, 2, 3])
    util.set_element_types(s, np.float64)
    assert np.issubdtype(s.dtype, np.floating)

    arr = np.array([1, 2, 3])
    util.set_element_types(arr, np.float64)
    assert np.issubdtype(arr.dtype, np.floating)

    d = {"a": "1", "b": "2"}
    util.set_element_types(d, {"a": int, "b": int})
    assert d == {"a": 1, "b": 2}


### STRUCT / COLLECTION CONVERTERS ######################################################


def test_to_struct_and_unstruct() -> None:
    # Single argument that is already a struct
    s = [1, 2, 3]
    assert util.to_struct(s) is s

    # Single non-struct argument
    assert util.to_struct(42) == [42]

    # Multiple arguments
    assert util.to_struct(1, 2, 3) == [1, 2, 3]

    # Unstruct on struct
    assert util.unstruct([42]) == 42
    assert util.unstruct([1, 2]) == (1, 2)
    assert util.unstruct(5) == 5


def test_to_collection_indexed_and_subscriptable_and_uncollect() -> None:
    c = util.to_collection(1)
    assert isinstance(c, list) and c == [1]

    c2 = util.to_collection([1, 2, 3])
    assert c2 == [1, 2, 3]

    ic = util.to_indexed_collection({"a": 1})
    assert isinstance(ic, list)

    sc = util.to_subscriptable_collection(1)
    assert sc == [1]

    # Uncollect mirrors unstruct for collections
    assert util.uncollect([42]) == 42
    assert util.uncollect([1, 2]) == (1, 2)
    assert util.uncollect(5) == 5


def test_struct_to_type_and_struct_to_common_type() -> None:
    data = [1, 2, 3]
    template_df = pd.DataFrame({"a": [0, 0, 0]}, index=[10, 11, 12])

    as_type = util.struct_to_type(data, template_df)
    assert isinstance(as_type, pd.DataFrame)
    assert list(as_type.columns) == list(template_df.columns)

    as_common = util.struct_to_common_type(data, template_df)
    assert isinstance(as_common, pd.DataFrame)

    template_dict = OrderedDict([("x", None), ("y", None)])
    as_type_dict = util.struct_to_type(data, template_dict)
    assert isinstance(as_type_dict, dict)
    assert list(as_type_dict.keys()) == ["x", "y"]


def test_to_series_and_to_time_series() -> None:
    data = [1, 2, 3]
    idx = ["t1", "t2", "t3"]
    s = util.to_series(data, name="value", index=idx, element_type=FLOAT_ELEMENT_TYPE)
    assert isinstance(s, pd.Series)
    assert list(s.index) == idx
    assert s.name == "value"
    assert np.issubdtype(s.dtype, np.floating)

    ts = util.to_time_series(data, name="ts", index=idx)
    assert isinstance(ts.index, pd.DatetimeIndex)
    assert ts.name == "ts"


def test_to_frame_and_to_time_frame() -> None:
    data = [[1, 2], [3, 4]]
    names = ["a", "b"]
    idx = ["i1", "i2"]

    df = util.to_frame(data, names=names, index=idx, index_name="idx")
    assert isinstance(df, pd.DataFrame)
    assert list(df.columns) == names
    assert list(df.index) == idx
    assert df.index.name == "idx"

    tf = util.to_time_frame(data, names=names, index=idx, index_name="tidx")
    assert isinstance(tf.index, pd.DatetimeIndex)
    assert tf.index.name == "tidx"


### STRUCT GENERATORS ###################################################################


def test_create_empty_for_builtin_types_and_pandas_and_numpy() -> None:
    empty_list = util.create_empty(list)
    assert empty_list == []

    empty_dict = util.create_empty(dict)
    assert empty_dict == {}

    empty_arr = util.create_empty(np.ndarray, element_type=np.float64)
    assert isinstance(empty_arr, np.ndarray)
    assert empty_arr.size == 0
    assert np.issubdtype(empty_arr.dtype, np.floating)

    empty_df = util.create_empty(pd.DataFrame)
    assert isinstance(empty_df, pd.DataFrame)
    assert empty_df.shape == (0, 0)

    empty_series = util.create_empty(pd.Series, element_type=np.float64)
    assert isinstance(empty_series, pd.Series)
    assert empty_series.size == 0
    assert np.issubdtype(empty_series.dtype, np.floating)


def test_create_mask_vectorized_and_fallback() -> None:
    arr = np.array([1, 2, 3, 4])
    mask = util.create_mask(arr, condition=lambda v: v % 2 == 0)
    np.testing.assert_array_equal(mask, np.array([False, True, False, True]))

    # Fallback path: use a condition that raises for arrays and is caught
    def non_vectorizable(x: Any) -> bool:
        if isinstance(x, np.ndarray):
            raise TypeError("force fallback")
        return bool(x % 2)

    mask_fb = util.create_mask(arr, condition=non_vectorizable)
    np.testing.assert_array_equal(mask_fb, np.array([True, False, True, False]))


### STRUCT PROCESSORS: ALL/ANY, APPLY, CALCULATE ########################################


def test_all_any_values_helpers() -> None:
    arr = np.array([True, True, False])
    df = pd.DataFrame({"a": [1, 0, 1]})

    assert util.all_values(arr)
    assert not util.all_values(df["a"] > 0)

    assert util.any_values(df["a"] == 0)
    assert util.any_not_values(np.array([True, True, False]))
    assert not util.all_not_values(np.array([True, False]))


def test_apply_on_dataframe_series_array_and_dict() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [3, 4]})

    doubled = util.apply(df, lambda x: x * 2, axis=None)
    pd.testing.assert_frame_equal(doubled, df * 2)

    col_sums = util.apply(df, np.sum, axis=0)
    pd.testing.assert_series_equal(col_sums, df.apply(np.sum, axis=0))

    s = pd.Series([1, 2, 3])
    squared = util.apply(s, lambda x: x * x)
    pd.testing.assert_series_equal(squared, s * s)

    arr = np.array([[1, 2], [3, 4]])
    arr2 = util.apply(arr, lambda x: x + 1)
    np.testing.assert_array_equal(arr2, arr + 1)

    d = {"x": 1, "y": 2}
    d2 = util.apply(d, lambda x: x * 10)
    assert d2 == {"x": 10, "y": 20}


def test_fill_with_and_fill_null_with() -> None:
    s = pd.Series([1, None, 3])
    filled = util.fill_with(s, -1, condition=lambda x: x is None)
    assert list(filled) == [1, -1, 3]

    s2 = pd.Series([None, 2])
    filled2 = util.fill_null_with(s2, 0)
    assert list(filled2) == [0, 2]


def test_calculate_preserves_labels_for_dataframe() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [3, 4]})

    result_axis0 = util.calculate(df, np.sum, axis=0)
    expected0 = pd.Series([3, 7], index=["a", "b"])
    pd.testing.assert_series_equal(result_axis0, expected0)

    result_axis1 = util.calculate(df, np.sum, axis=1)
    expected1 = pd.Series([4, 6], index=df.index)
    pd.testing.assert_series_equal(result_axis1, expected1)


### FLATTEN / GROUPBY / REDUCE / LOGICAL REDUCE #########################################


def test_flatten_array_respects_axis_order() -> None:
    arr = np.array([[1, 2], [3, 4]])

    flat_c = util.flatten(arr, axis=0)
    np.testing.assert_array_equal(flat_c, np.array([1, 2, 3, 4]))

    flat_f = util.flatten(arr, axis=1)
    np.testing.assert_array_equal(flat_f, np.array([1, 3, 2, 4]))


def test_groupby_position_and_aggregation() -> None:
    arr = np.array([1, 2, 3, 4])

    assert util.groupby(arr, pos=Position.START) == 1
    assert util.groupby(arr, pos=Position.END) == 4
    assert util.groupby(arr, agg=Aggregation.SUM) == 10
    assert math.isclose(util.groupby(arr, agg=Aggregation.MEAN), 2.5)


def test_reduce_and_reduce_or_with_empty_axes() -> None:
    x = np.array([[True, True], [False, True]])
    and_rows = util.reduce_and(x, axis=0)
    or_rows = util.reduce_or(x, axis=0)
    np.testing.assert_array_equal(and_rows, np.array([False, True]))
    np.testing.assert_array_equal(or_rows, np.array([True, True]))

    empty_rows = np.zeros((0, 3), dtype=bool)
    and_empty_rows = util.reduce_and(empty_rows, axis=0)
    or_empty_rows = util.reduce_or(empty_rows, axis=0)
    np.testing.assert_array_equal(and_empty_rows, np.ones(3, dtype=BOOLEAN_ELEMENT_TYPE))
    np.testing.assert_array_equal(or_empty_rows, np.zeros(3, dtype=BOOLEAN_ELEMENT_TYPE))

    empty_cols = np.zeros((3, 0), dtype=bool)
    and_empty_cols = util.reduce_and(empty_cols, axis=1)
    or_empty_cols = util.reduce_or(empty_cols, axis=1)
    np.testing.assert_array_equal(and_empty_cols, np.ones(3, dtype=BOOLEAN_ELEMENT_TYPE))
    np.testing.assert_array_equal(or_empty_cols, np.zeros(3, dtype=BOOLEAN_ELEMENT_TYPE))


def test_reduce_with_initializer_and_without() -> None:
    data = [1, 2, 3]
    result = util.reduce(data, lambda x, y: x + y)
    assert result == 6

    result_init = util.reduce(data, lambda x, y: x + y, initializer=10)
    assert result_init == 16

    assert util.reduce([], lambda x, y: x + y, initializer=None) is None


### CONCAT / INSERT / UPDATE / UPSERT ###################################################


def test_concat_for_lists_dicts_sets_arrays_and_tables() -> None:
    l1 = [1, 2]
    l2 = [3]
    assert util.concat(l1, l2) == [1, 2, 3]

    d1 = OrderedDict([("a", 1)])
    d2 = OrderedDict([("b", 2)])
    assert util.concat(d1, d2) == {"a": 1, "b": 2}

    s1 = {1, 2}
    s2 = {2, 3}
    assert util.concat(s1, s2) == {1, 2, 3}

    a1 = np.array([1, 2])
    a2 = np.array([3])
    np.testing.assert_array_equal(util.concat(a1, a2), np.array([1, 2, 3]))

    df1 = pd.DataFrame({"a": [1]})
    df2 = pd.DataFrame({"a": [2]})
    concatenated = util.concat(df1, df2)
    # Single column → concat_rows returns Series
    assert isinstance(concatenated, pd.Series)
    assert list(concatenated.values) == [1, 2]


def test_insert_rows_and_insert_cols_for_dataframe() -> None:
    df1 = pd.DataFrame({"a": [1, 2]}, index=[0, 1])
    df2 = pd.DataFrame({"a": [3, 4]}, index=[1, 2])

    # Insert rows: rows from df2 with index 2 should be appended
    inserted_rows = util.insert_rows(df1.copy(), df2)
    assert list(inserted_rows.index) == [0, 1, 2]
    assert list(inserted_rows["a"]) == [1, 2, 4]

    df3 = pd.DataFrame({"b": [10, 20]}, index=[0, 1])
    inserted_cols = util.insert_cols(df1.copy(), df3)
    assert list(inserted_cols.columns) == ["a", "b"]
    assert list(inserted_cols["b"]) == [10, 20]


def test_update_and_upsert_for_dict() -> None:
    d1 = {"a": 1}
    d2 = {"b": 2}

    updated = util.update(d1.copy(), d2)
    # No common keys → unchanged
    assert updated == {"a": 1}

    upserted = util.upsert(d1.copy(), d2)
    assert upserted == {"a": 1, "b": 2}


def test_update_for_dataframe_common_keys_only() -> None:
    df1 = pd.DataFrame({"a": [1, 2]}, index=[0, 1])
    df2 = pd.DataFrame({"a": [10, 20], "b": [100, 200]}, index=[1, 2])

    updated = util.update(df1.copy(), df2)
    assert list(updated.columns) == ["a"]
    assert list(updated["a"]) == [1, 20]


### FILTERING AND WHERE #################################################################


def test_filter_and_filter_index_for_dataframe_and_dict() -> None:
    df = pd.DataFrame(
        {"a": [1, 2, 3], "b": [4, 5, 6], "c": [7, 8, 9]},
        index=["i1", "i2", "i3"],
    )

    filtered_cols = util.filter(df, inclusion=["a", "c"])
    assert list(filtered_cols.columns) == ["a", "c"]

    filtered_excl = util.filter(df, exclusion=["b"])
    assert list(filtered_excl.columns) == ["a", "c"]

    filtered_idx = util.filter_index(df, inclusion=["i1", "i3"])
    assert list(filtered_idx.index) == ["i1", "i3"]

    d = {"x": 1, "y": 2, "z": 3}
    filtered_dict = util.filter(d, inclusion=["x", "z"])
    assert filtered_dict == {"x": 1, "z": 3}


def test_filter_with_variants_and_filter_null_and_empty() -> None:
    df = pd.DataFrame({"a": [1, None, 3], "b": ["x", "", None]})

    null_all = util.filter_null(df)
    assert list(null_all.index) == []

    any_null = util.filter_any_null(df)
    assert list(any_null.index) == [0, 1, 2]  # at least one null/None/empty

    empty_all = util.filter_empty(df)
    assert list(empty_all.index) == []

    any_empty = util.filter_any_empty(df)
    assert list(any_empty.index) == [1, 2]

    value_rows = util.filter_value(df, 1, keys=["a"])
    assert list(value_rows.index) == [0]

    between_rows = util.filter_between(df["a"], 1, 3)
    assert list(between_rows.index) == [0, 2]


def test_where_returns_matching_keys() -> None:
    values = [1, 2, 3, 4]
    keys = util.where(values, condition=lambda v: v % 2 == 0)
    assert keys == [1, 3]  # positions of 2 and 4


### FILL NULL HELPERS ###################################################################


def test_fill_null_for_dataframe_numeric_vs_object_defaults() -> None:
    df = pd.DataFrame({"a": [1.0, np.nan], "b": [None, "x"]})
    util.fill_null(df, numeric_default=0.0, object_default="missing")

    assert list(df["a"]) == [1.0, 0.0]
    assert list(df["b"]) == ["missing", "x"]


def test_fill_null_rows_and_cols_align_to_model() -> None:
    df = pd.DataFrame({"a": [1.0]}, index=[0])
    filled_rows = util.fill_null_rows(df, [0, 1], numeric_default=0.0)
    assert list(filled_rows.index) == [0, 1]
    assert list(filled_rows["a"]) == [1.0, 0.0]

    df2 = pd.DataFrame({"a": [1.0], "b": [np.nan]}, index=[0])
    filled_cols = util.fill_null_cols(df2[["a"]], ["a", "b"], numeric_default=0.0)
    assert list(filled_cols.columns) == ["a", "b"]
    assert list(filled_cols["a"]) == [1.0]
    assert list(filled_cols["b"]) == [0.0]


### TAKE / SLICE / REVERSE / UNIQUE #####################################################


def test_take_take_not_take_at_and_take_not_at_for_dataframe_and_list() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [3, 4]}, index=[10, 20])

    taken_rows = util.take(df, [10], axis=0)
    assert list(taken_rows.index) == [10]

    taken_cols = util.take(df, ["a"], axis=1)
    assert list(taken_cols.columns) == ["a"]

    taken_not_rows = util.take_not(df, [10], axis=0)
    assert list(taken_not_rows.index) == [20]

    taken_at = util.take_at(df, [0], axis=0)
    assert list(taken_at.index) == [10]

    taken_not_at = util.take_not_at(df, [0], axis=0)
    assert list(taken_not_at.index) == [20]

    l = [1, 2, 3, 4]
    assert util.slice(l, 1, 3) == [2, 3]


def test_reverse_for_dataframe_dict_and_list() -> None:
    df = pd.DataFrame({"a": [1, 2, 3]})
    reversed_rows = util.reverse(df, axis=0)
    assert list(reversed_rows["a"]) == [3, 2, 1]

    d = OrderedDict([("x", 1), ("y", 2)])
    rev_d = util.reverse(d)
    assert list(rev_d.items()) == [("y", 2), ("x", 1)]

    l = [1, 2, 3]
    assert util.reverse(l) == [3, 2, 1]


def test_unique_for_list_and_dataframe_with_position_bias() -> None:
    l = [1, 2, 1, 2, 3]
    # For lists, positional bias does not change values, only which occurrence is kept
    assert util.unique(l, pos=Position.START) == [1, 2, 3]
    assert util.unique(l, pos=Position.END) == [1, 2, 3]
    assert util.unique(l, pos=Position.MIDDLE) == [1, 2, 3]

    df = pd.DataFrame({"v": [10, 20, 30]}, index=["a", "a", "b"])

    start = util.unique(df, pos=Position.START)
    end = util.unique(df, pos=Position.END)
    middle = util.unique(df, pos=Position.MIDDLE)

    assert list(start.index) == ["a", "b"]
    assert list(end.index) == ["a", "b"]
    assert list(middle.index) == ["a", "b"]

    assert start.loc["a", "v"] == 10
    assert end.loc["a", "v"] == 20
    assert middle.loc["a", "v"] == 20


### REMOVE NULL / EMPTY / VALUE #########################################################


def test_remove_null_empty_and_value_for_dataframe_and_list() -> None:
    df = pd.DataFrame({"a": [1, None, 3], "b": [0, 0, 0]})
    removed_null_rows = util.remove_null(df.copy(), conservative=True, axis=0)
    assert list(removed_null_rows.index) == [0, 2]

    removed_value_rows = util.remove_value(df.copy(), 0, conservative=False, axis=0)
    # Remove rows where any value equals 0
    assert list(removed_value_rows.index) == [1]

    l = [None, 1, None]
    removed_null_cols = util.remove_null(l.copy(), conservative=False, axis=0)
    assert removed_null_cols == [1]


### TALLY ###############################################################################


def test_tally_into_intervals() -> None:
    s = [1, 2, 5, 7]
    boundaries = [3, 6]
    tallied = util.tally(s, boundaries)
    # [1, 2) -> bin 0; [3, 6) -> bin 1; >=6 -> bin 2
    assert list(tallied) == [0, 0, 1, 2]


### LIST FIND HELPERS ###################################################################


def test_find_all_and_find_not_and_with_variants() -> None:
    l = [1, 2, 3, 2]

    assert util.find_all(l, 2) == [1, 3]
    assert util.find_all_not(l, 2) == [0, 2]

    assert util.find(l, 2) == 1
    assert util.find_not(l, 2) == 0

    assert util.find_last(l, 2) == 3
    assert util.find_last_not(l, 2) == 2

    assert util.find_in(l, {2, 3}) == 1
    assert util.find_not_in(l, {2, 3}) == 0

    assert util.find_last_in(l, {2, 3}) == 3
    assert util.find_last_not_in(l, {2, 3}) == 0


### DATAFRAME COMBINE / CONCAT ROWS / CONCAT COLS ######################################


def test_combine_and_combine_all() -> None:
    left = pd.DataFrame({"a": [1, 2]})
    right = pd.DataFrame({"a": [10, 20]})

    def plus(s1: pd.Series, s2: pd.Series) -> pd.Series:
        return s1 + s2

    combined = util.combine(left, right, plus)
    pd.testing.assert_frame_equal(combined, pd.DataFrame({"a": [11, 22]}))

    combined_all = util.combine_all(left, right, right, f=plus)
    pd.testing.assert_frame_equal(combined_all, pd.DataFrame({"a": [21, 42]}))


def test_concat_rows_and_concat_cols() -> None:
    df1 = pd.DataFrame({"a": [1]})
    df2 = pd.DataFrame({"a": [2]})
    rows = util.concat_rows(df1, df2)
    assert isinstance(rows, pd.Series)
    assert list(rows.values) == [1, 2]

    s1 = pd.Series([1, 2], name="a")
    s2 = pd.Series([3, 4], name="b")
    cols = util.concat_cols(s1, s2)
    assert isinstance(cols, pd.DataFrame)
    assert list(cols.columns) == ["a", "b"]


### ROW-LEVEL FILTERS ###################################################################


def _row_mapping(df: pd.DataFrame, **vals: Any) -> Mapping[Any, Any]:
    return vals


def test_filter_rows_variants() -> None:
    df = pd.DataFrame({"a": [1, 2, 2], "b": [3, 4, 4]})

    row = _row_mapping(df, a=2, b=4)

    rows = util.filter_rows(df, row)
    assert list(rows.index) == [1, 2]

    rows_not = util.filter_rows_not(df, row)
    assert list(rows_not.index) == [0]

    rows_any = util.filter_any_rows(df, row)
    assert list(rows_any.index) == [1, 2]

    rows_any_not = util.filter_any_rows_not(df, row)
    assert list(rows_any_not.index) == [0]


def test_filter_rows_with_in_not_in_any_variants() -> None:
    df = pd.DataFrame({"a": [1, 2, 3], "b": [4, 5, 6]})

    rows_in = util.filter_rows_in(df, {"a": [2, 3]})
    assert list(rows_in.index) == [1, 2]

    rows_not_in = util.filter_rows_not_in(df, {"a": [2, 3]})
    assert list(rows_not_in.index) == [0]

    rows_any_in = util.filter_any_rows_in(df, {"a": [2], "b": [4]})
    assert list(rows_any_in.index) == [0, 1]

    rows_any_not_in = util.filter_any_rows_not_in(df, {"a": [1], "b": [4]})
    assert list(rows_any_not_in.index) == [1, 2]


### JOIN / MERGE / PIVOT / UNPIVOT ######################################################


def test_join_and_join_all() -> None:
    left = pd.DataFrame({"key": [1, 2], "a": [10, 20]})
    right = pd.DataFrame({"key": [1, 3], "b": [100, 300]})

    result = util.join(left, right, how="inner", on="key", index_name="idx")
    expected = left.join(right, how="inner", on="key", rsuffix="2", validate="m:m")
    expected.index.name = "idx"
    pd.testing.assert_frame_equal(result, expected)

    all_joined = util.join_all(left, right, how="left", on="key", index_name="idx")
    expected_all = left.join(right, how="left", on="key", rsuffix="2", validate="m:m")
    expected_all.index.name = "idx"
    pd.testing.assert_frame_equal(all_joined, expected_all)


def test_merge_and_merge_all() -> None:
    left = pd.DataFrame({"key": [1, 2], "a": [10, 20]})
    right = pd.DataFrame({"key": [1, 3], "b": [100, 300]})

    result = util.merge(left, right, how="inner", on="key", index_name="idx")
    expected = left.merge(
        right,
        copy=False,
        how="inner",
        on="key",
        suffixes=(None, "2"),
        indicator=None,
        validate="m:m",
    )
    expected.index.name = "idx"
    pd.testing.assert_frame_equal(result, expected)

    all_merged = util.merge_all(left, right, how="left", on="key", index_name="idx")
    expected_all = left.merge(
        right,
        copy=False,
        how="left",
        on="key",
        suffixes=(None, "2"),
        indicator=None,
        validate="m:m",
    )
    expected_all.index.name = "idx"
    pd.testing.assert_frame_equal(all_merged, expected_all)


def test_pivot_and_unpivot() -> None:
    df = pd.DataFrame(
        {
            "name": ["n1", "n1", "n2"],
            "idx": [1, 2, 1],
            "val": [10, 20, 30],
        }
    )

    wide = util.pivot(df, names="name", index="idx", values="val")
    expected_wide = df.pivot(columns="name", index="idx", values="val")
    pd.testing.assert_frame_equal(wide, expected_wide)

    long = util.unpivot(wide, value="val", names=["idx", "name"])
    # Column names after renaming
    assert set(long.columns) == {"idx", "name", "val"}
    # No null in value column
    assert not long["val"].isna().any()


### ROW / COLUMN REMOVAL / RENAMING / ROTATION ##########################################


def test_remove_row_and_col_and_at_variants() -> None:
    df = pd.DataFrame({"a": [1, 2, 3], "b": [4, 5, 6]}, index=[0, 1, 2])

    df_no_row1 = util.remove_row_at(df, 1)
    assert list(df_no_row1.index) == [0, 2]

    df_no_last = util.remove_row_at(df, -1)
    assert list(df_no_last.index) == [0, 1]

    df_no_col0 = util.remove_col_at(df, 0)
    assert list(df_no_col0.columns) == ["b"]

    df_no_last_col = util.remove_col_at(df, -1)
    assert list(df_no_last_col.columns) == ["a"]


def test_rename_and_rename_all() -> None:
    df = pd.DataFrame({"a": [1, 2]}, index=[10, 20])

    renamed = util.rename(df.copy(), names=["x"], index={10: "i1", 20: "i2"})
    assert list(renamed.columns) == ["x"]
    assert list(renamed.index) == ["i1", "i2"]

    df1 = pd.DataFrame({"a": [1]})
    df2 = pd.DataFrame({"a": [2]})
    util.rename_all(df1, df2, names=["x"])
    assert list(df1.columns) == ["x"]
    assert list(df2.columns) == ["x"]


def test_rotate_rows_and_cols_for_dataframe_with_multiple_columns() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [3, 4]})

    rotated_rows = util.rotate_rows(df.copy(), drop=True, prepend=False)
    assert list(rotated_rows["a"]) == [2, 1]
    assert list(rotated_rows["b"]) == [4, 3]

    rotated_rows_prepend = util.rotate_rows(df.copy(), drop=True, prepend=True)
    assert list(rotated_rows_prepend["a"]) == [2, 1]
    assert list(rotated_rows_prepend["b"]) == [4, 3]

    rotated_cols = util.rotate_cols(df.copy(), drop=True, prepend=False)
    assert list(rotated_cols.columns) == ["b", "a"]
    rotated_cols_prepend = util.rotate_cols(df.copy(), drop=True, prepend=True)
    assert list(rotated_cols_prepend.columns) == ["b", "a"]


### SUM / PRODUCT HELPERS ################################################################


def test_sum_rows_and_sum_cols_and_product_rows_and_cols() -> None:
    df = pd.DataFrame({"a": [1, 2], "b": [3, 4]})

    sum_r = util.sum_rows(df)
    sum_c = util.sum_cols(df)
    pd.testing.assert_series_equal(sum_r, df.sum(axis=0))
    pd.testing.assert_series_equal(sum_c, df.sum(axis=1))

    prod_r = util.product_rows(df)
    prod_c = util.product_cols(df)
    pd.testing.assert_series_equal(prod_r, df.product(axis=0))
    pd.testing.assert_series_equal(prod_c, df.product(axis=1))


## STRUCT UTIL TEST MAIN ################################################################

__STRUCT_UTIL_TEST_MAIN_____________________________________ = ""


def main() -> None:
    """Tests the structure utilities."""
    unittest.main()


if __name__ == "__main__":
    main()
