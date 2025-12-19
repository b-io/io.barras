#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the common structure utilities.
#
# Description
#   Validates construction, iteration, indexing/slicing, mutators, order-preserving set algebra,
#   subset/superset relations, converters, processors, and verifiers for `OrderedSet`.
#
# Usage
#   Run with: pytest -q
########################################################################################################################

from __future__ import annotations

import pytest

from nutil.struct.common import *


__COMMON_STRUCT_TEST_CASES________________________________________________________________ = ""


class _DelegateAll:
    """A delegate that provides `to_array`, `to_dict`, `to_list`, `to_set`, `to_tuple`."""

    def __init__(self, data):
        self.data = data

    def to_array(self):
        return np.array(self.data)

    def to_dict(self):
        return dict(enumerate(self.data))

    def to_list(self):
        return list(self.data)

    def to_set(self):
        return set(self.data)

    def to_tuple(self):
        return tuple(self.data)


class _CallableProbe:
    """A probe with a callable attribute `ping` to validate `has_callable`."""

    def ping(self) -> str:
        return "pong"


### 1. CONVERTERS & UNWRAPPERS #############################


@pytest.mark.parametrize(
    "arg, dtype, expected, expected_dtype",
    [
        (1, None, np.array([1]), None),
        (1.5, float, np.array([1.5]), np.float64),
        ({1: "a", 2: "b"}, object, np.array(["a", "b"], dtype=object), object),
        ([1, 2, 3], None, np.array([1, 2, 3]), None),
    ],
)
def test_to_array_single_argument(arg, dtype, expected, expected_dtype):
    """Verifies `to_array` with a single argument across scalar, `Mapping`, and `Iterable`."""
    a = to_array(arg, element_type=dtype)
    np.testing.assert_array_equal(a, expected)
    if expected_dtype is not None:
        assert a.dtype == expected_dtype


def test_to_array_existing_array_enforces_dtype_without_copy_when_possible():
    """Verifies `to_array` preserves `ndarray` when `dtype` matches (no copy)."""
    base = np.array([1, 2, 3], dtype=np.int64)
    out = to_array(base, element_type=np.int64)
    assert out is base
    np.testing.assert_array_equal(out, base)


def test_to_array_iterable_and_variadic():
    """Verifies `to_array` converts an `Iterable` or multiple arguments into an `ndarray`."""
    np.testing.assert_array_equal(to_array(range(3)), np.array([0, 1, 2]))
    np.testing.assert_array_equal(to_array(1, 2, 3), np.array([1, 2, 3]))


def test_to_array_delegation():
    """Verifies `to_array` delegates to an object's `to_array` method."""
    src = _DelegateAll([1, 2, 3])
    np.testing.assert_array_equal(to_array(src), np.array([1, 2, 3]))


@pytest.mark.parametrize(
    "inp, expected",
    [
        (np.array(7), 7),
        (np.array([9]), 9),
        (np.array([1, 2]), (1, 2)),
        ("x", "x"),
    ],
)
def test_unarray(inp, expected):
    """Verifies `unarray` unwraps `ndarray` scalars, singletons, and multi-elements."""
    assert unarray(inp) == expected


def test_to_dict_single_and_variadic():
    """Verifies `to_dict` with scalar, `Mapping`, iterable pairs, generic iterable, and variadic args."""
    assert to_dict(7) == {0: 7}
    assert to_dict({"a": 1, "b": 2}) == {"a": 1, "b": 2}
    assert to_dict([("x", 1), ("y", 2)]) == {"x": 1, "y": 2}
    assert to_dict(["a", "b"]) == {0: "a", 1: "b"}
    assert to_dict(("k", 1), ("v", 2)) == {"k": 1, "v": 2}


def test_to_dict_delegation():
    """Verifies `to_dict` delegates to an object's `to_dict` method."""
    src = _DelegateAll([10, 20])
    assert to_dict(src) == {0: 10, 1: 20}


@pytest.mark.parametrize(
    "inp, expected",
    [
        ({1: "a"}, "a"),
        ({1: "a", 2: "b"}, ("a", "b")),
        ("x", "x"),
    ],
)
def test_undict(inp: Any, expected: Any):
    """Verifies `undict` unwraps `dict` into a value, tuple of values, or passthrough."""
    out = undict(inp)
    if isinstance(inp, dict) and len(inp) > 1:
        assert tuple(out) == expected
    else:
        assert out == expected


def test_to_list_single_and_variadic():
    """Verifies `to_list` with scalar, `Mapping`, iterable, and variadic args."""
    assert to_list(5) == [5]
    assert to_list({"a": 1, "b": 2}) == [1, 2]
    assert to_list([1, 2]) == [1, 2]
    assert to_list(range(3)) == [0, 1, 2]
    assert to_list(1, 2, 3) == [1, 2, 3]


def test_to_list_delegation():
    """Verifies `to_list` delegates to an object's `to_list` method."""
    src = _DelegateAll([10, 20])
    assert to_list(src) == [10, 20]


@pytest.mark.parametrize(
    "inp, expected",
    [
        ([9], 9),
        ([1, 2], (1, 2)),
        ("x", "x"),
    ],
)
def test_unlist(inp, expected):
    """Verifies `unlist` unwraps a `list` into element, tuple, or passthrough."""
    assert unlist(inp) == expected


def test_to_set_single_and_variadic():
    """Verifies `to_set` with scalar, `Mapping`, `set`, iterable, and variadic args."""
    assert to_set(5) == {5}
    assert to_set({"a": 1, "b": 2}) == {1, 2}
    assert to_set({1, 2}) == {1, 2}
    assert to_set([1, 2, 2]) == {1, 2}
    assert to_set(1, 2, 2) == {1, 2}


def test_to_set_delegation():
    """Verifies `to_set` delegates to an object's `to_set` method."""
    src = _DelegateAll([10, 20])
    assert to_set(src) == {10, 20}


@pytest.mark.parametrize(
    "inp, expected_checker",
    [
        ({1}, lambda x: x == 1),
        ({1, 2}, lambda x: set(x) == {1, 2}),
        ("x", lambda x: x == "x"),
    ],
)
def test_unset(inp, expected_checker):
    """Verifies `unset` unwraps a `set` into element, tuple of elements, or passthrough."""
    assert expected_checker(unset(inp))


def test_to_tuple_single_and_variadic():
    """Verifies `to_tuple` with scalar, `Mapping`, `tuple`, iterable, and variadic args."""
    assert to_tuple(5) == (5,)
    assert to_tuple({"a": 1, "b": 2}) == (1, 2)
    assert to_tuple((1, 2)) == (1, 2)
    assert to_tuple([1, 2]) == (1, 2)
    assert to_tuple(1, 2, 3) == (1, 2, 3)


def test_to_tuple_delegation():
    """Verifies `to_tuple` delegates to an object's `to_tuple` method."""
    src = _DelegateAll([10, 20])
    assert to_tuple(src) == (10, 20)


### 2. UNGROUP (PANDAS GroupBy) ############################


def test_ungroup_modes_obj_groups_auto_rows_and_columns():
    """Verifies `ungroup` returns `.obj`, `.groups`, or passthrough depending on `mode` and axis."""
    df = pd.DataFrame({"k": ["a", "a", "b"], "v": [1, 2, 3]})

    g_rows = df.groupby("k")
    assert ungroup(g_rows, mode="obj").equals(df)

    groups = ungroup(g_rows, mode="groups")  # prettyDict wrapper
    groups = dict(groups)  # unwrap
    groups = {k: (v.tolist() if has_callable(v, "tolist") else list(v)) for k, v in groups.items()}
    assert groups == {"a": [0, 1], "b": [2]}
    assert ungroup(g_rows, mode="auto").equals(df)

    g_cols = df.T.groupby({"k": "grp1", "v": "grp1"})
    assert set(ungroup(g_cols, mode="groups").keys()) == {"grp1"}
    assert isinstance(ungroup(g_cols, mode="auto"), pd.DataFrame)

    assert ungroup(123) == 123


### 3. VERIFIERS & UTILITIES ###############################


def test_is_struct_and_is_element():
    """Verifies `is_struct` for collection, table, tuple; `is_element` for scalars and tuples."""
    assert is_struct([1, 2, 3]) is True
    assert is_struct({"a": 1}) is True
    assert is_struct((1, 2)) is True
    assert is_struct(pd.Series([1, 2])) is True
    assert is_struct(pd.DataFrame({"a": [1]})) is True
    assert is_struct(42) is False

    assert is_element(42) is True
    assert is_element("42") is True
    assert is_element((1, 2)) is False
    assert is_element([1, 2]) is False


def test_is_multidimensional_and_is_subscriptable():
    """Verifies `is_multidimensional` and `is_subscriptable` on arrays, tables, and scalars."""
    assert is_multidimensional(np.array([[1, 2], [3, 4]])) is True
    assert is_multidimensional(pd.DataFrame({"a": [1]})) is True
    assert is_multidimensional([1, 2, 3]) is False

    assert is_subscriptable("abc") is True
    assert is_subscriptable(123) is False


def test_has_callable_probe():
    """Verifies `has_callable` detects callable attribute `ping` and not missing ones."""
    probe = _CallableProbe()
    assert has_callable(probe, "ping") is True
    assert has_callable(probe, "missing") is False


def test_has_index():
    """Verifies `has_index` for `ndarray` and `Sequence`, but not for scalars."""
    assert has_index(np.array([1, 2])) is True
    assert has_index([1, 2]) is True
    assert has_index(1) is False


def test_compare_length_helpers():
    """Verifies `compare_length` and helpers `has_length_*` with valid and invalid inputs."""
    xs = [1, 2, 3]
    assert compare_length(xs, 3, operator.eq) is True
    assert compare_length(xs, 2, operator.gt) is True
    assert compare_length(xs, 4, operator.lt) is True

    assert has_length_ge(xs, 3) is True
    assert has_length_gt(xs, 2) is True
    assert has_length_le(xs, 3) is True
    assert has_length_lt(xs, 4) is True

    assert compare_length(123, 1, operator.eq) is False
    assert has_length_ge(123, 1) is False
    assert has_length_gt(123, 0) is False
    assert has_length_le(123, 0) is False
    assert has_length_lt(123, 2) is False
