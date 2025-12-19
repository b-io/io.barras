#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the ordered set implementation with its adapters and utilities.
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

from nutil.struct.collection.registry.ordered_set import *


__ORDERED_SET_TEST_FIXTURES_______________________________________________________________ = ""


@pytest.fixture
def s_abc() -> OrderedSet[str]:
    """Returns an `OrderedSet` with three elements in insertion order."""
    return OrderedSet(["a", "b", "c"])


@pytest.fixture
def s_bcd() -> OrderedSet[str]:
    """Returns an `OrderedSet` with overlapping tail, used for set algebra tests."""
    return OrderedSet(["b", "c", "d"])


__ORDERED_SET_TEST_CASES__________________________________________________________________ = ""


def test_construct_deduplicates_and_preserves_order() -> None:
    """Verifies that construction removes the duplicates and preserves the insertion order."""
    s = OrderedSet(["a", "b", "a", "c", "b"])
    assert list(s) == ["a", "b", "c"]
    assert len(s) == 3
    assert "a" in s and "z" not in s


def test_to_iterable_zero_copy_reflects_updates(s_abc: OrderedSet[str]) -> None:
    """Verifies that `to_iterable` returns a dynamic view that reflects subsequent updates."""
    view = s_abc.to_iterable()
    assert list(view) == ["a", "b", "c"]
    s_abc.add("d")
    assert list(view) == ["a", "b", "c", "d"]


### ITERATION, INDEXING, SLICING, REVERSAL #################


def test_iter_and_reversed(s_abc: OrderedSet[str]) -> None:
    """Verifies forward and reversed iteration order."""
    assert list(create_iterator(s_abc)) == ["a", "b", "c"]
    assert list(reversed(s_abc)) == ["c", "b", "a"]


def test_getitem_index_positive_and_negative(s_abc: OrderedSet[str]) -> None:
    """Verifies positive and negative indexing."""
    assert s_abc[0] == "a"
    assert s_abc[1] == "b"
    assert s_abc[-1] == "c"
    assert s_abc[-2] == "b"


def test_getitem_index_out_of_range_raises(s_abc: OrderedSet[str]) -> None:
    """Verifies that out-of-range indexing raises the documented `IndexError`."""
    with pytest.raises(IndexError) as e:
        _ = s_abc[3]
    assert "OrderedSet index out of range" in str(e.value)


def test_getitem_index_type_error_message(s_abc: OrderedSet[str]) -> None:
    """Verifies that non-integer index types raise a consistent `TypeError` message."""
    with pytest.raises(TypeError) as e:
        _ = s_abc[1.2]  # float not allowed
    assert "Collection indices must be integers or slices" in str(e.value)


def test_getitem_slice_unit_step_fast_path(s_abc: OrderedSet[str]) -> None:
    """Verifies slicing with a unit step returns a `list` and uses the fast path."""
    assert s_abc[0:0] == []
    assert s_abc[0:1] == ["a"]
    assert s_abc[0:2] == ["a", "b"]
    assert s_abc[1:3] == ["b", "c"]
    assert s_abc[:2] == ["a", "b"]
    assert s_abc[1:] == ["b", "c"]


def test_getitem_slice_general_step_materializes(s_abc: OrderedSet[str]) -> None:
    """Verifies negative or non-unit steps by comparing against `list(s)[slice]`."""
    as_list = list(s_abc)
    assert s_abc[::-1] == as_list[::-1]
    assert s_abc[::2] == as_list[::2]
    assert s_abc[2:0:-1] == as_list[2:0:-1]


############################################################


def test_numpy_fancy_indexing_with_ordered_set_1d() -> None:
    """Verifies that an `OrderedSet` of integer positions can index into a NumPy array."""
    indices = OrderedSet([1, 2])
    a = np.array([1, 2, 3, 4])
    assert np.array_equal(a[indices], [2, 3])


def test_numpy_fancy_indexing_with_ordered_set_2d() -> None:
    """Verifies `OrderedSet` works for row/column fancy indexing on 2-D arrays."""
    A = np.array([[10, 11, 12], [20, 21, 22], [30, 31, 32], [40, 41, 42]])
    rows = OrderedSet([1, 3])
    cols = OrderedSet([0, 2])
    assert np.array_equal(A[list(rows), :], [[20, 21, 22], [40, 41, 42]])
    assert np.array_equal(A[:, list(cols)], [[10, 12], [20, 22], [30, 32], [40, 42]])


### FIRST / LAST ###########################################


def test_first_last_non_empty(s_abc: OrderedSet[str]) -> None:
    """Verifies `first`/`last` on non-empty sets."""
    assert s_abc.first() == "a"
    assert s_abc.last() == "c"


def test_first_last_empty_raise() -> None:
    """Verifies `first`/`last` raise `IndexError` on an empty set with the documented message."""
    s = OrderedSet()
    with pytest.raises(IndexError) as e1:
        _ = s.first()
    assert "OrderedSet is empty" in str(e1.value)
    with pytest.raises(IndexError) as e2:
        _ = s.last()
    assert "OrderedSet is empty" in str(e2.value)


### MUTATORS ###############################################


def test_add_discard_clear(s_abc: OrderedSet[str]) -> None:
    """Verifies `add`/`discard`/`clear` semantics and idempotency."""
    s_abc.add("b")  # duplicate add is a no-op
    assert list(s_abc) == ["a", "b", "c"]
    s_abc.add("d")
    assert list(s_abc) == ["a", "b", "c", "d"]
    s_abc.discard("x")  # missing is a no-op
    s_abc.discard("b")
    assert list(s_abc) == ["a", "c", "d"]
    s_abc.clear()
    assert len(s_abc) == 0
    assert list(s_abc) == []


def test_update_with_iterables_and_orderedset() -> None:
    """Verifies `update` with both plain iterables and an `OrderedSet` preserves order."""
    s = OrderedSet(["a"])
    s.update(["b", "c"])
    assert list(s) == ["a", "b", "c"]
    s_other = OrderedSet(["b", "d", "e"])
    s.update(s_other)
    assert list(s) == ["a", "b", "c", "d", "e"]


### ORDER-PRESERVING SET ALGEBRA ###########################


def test_intersection_ordered(s_abc: OrderedSet[str], s_bcd: OrderedSet[str]) -> None:
    """Verifies `&` keeps `self`'s order; `other & self` keeps `other`'s order."""
    assert list(s_abc & s_bcd) == ["b", "c"]
    assert list(s_bcd & s_abc) == ["b", "c"]
    # `__rand__`
    assert list(["x", "b", "c", "y"] & s_abc) == ["b", "c"]


def test_union_ordered(s_abc: OrderedSet[str], s_bcd: OrderedSet[str]) -> None:
    """Verifies `|` yields `self` first then new from `other`; `other | self` flips that."""
    assert list(s_abc | s_bcd) == ["a", "b", "c", "d"]
    assert list(s_bcd | s_abc) == ["b", "c", "d", "a"]
    # `__ror__`
    assert list(["z", "a"] | s_abc) == ["z", "a", "b", "c"]


def test_difference_ordered(s_abc: OrderedSet[str], s_bcd: OrderedSet[str]) -> None:
    """Verifies ordered difference."""
    assert list(s_abc - s_bcd) == ["a"]
    assert list(s_bcd - s_abc) == ["d"]
    # `__rsub__`
    assert list(["a", "b", "x"] - s_abc) == ["x"]


def test_symmetric_difference_ordered_and_requires_hashable() -> None:
    """Verifies ordered symmetric difference and hashability requirement."""
    s1 = OrderedSet(["a", "b", "c"])
    s2 = OrderedSet(["b", "c", "d"])
    assert list(s1 ^ s2) == ["a", "d"]
    # `__rxor__`
    assert list(["x", "a"] ^ s1) == ["x", "b", "c"]
    with pytest.raises(TypeError):
        _ = s1 ^ [["b"], ["c"]]  # lists are unhashable


def test_inplace_set_ops_preserve_documented_order() -> None:
    """Verifies in-place `&`, `|`, `-`, `^` semantics and order guarantees."""
    s = OrderedSet(["a", "b", "c"])
    s &= ["b", "c", "d"]
    assert list(s) == ["b", "c"]
    s |= ["c", "d", "e"]
    assert list(s) == ["b", "c", "d", "e"]
    s -= ["b", "x"]
    assert list(s) == ["c", "d", "e"]
    s ^= ["d", "y"]
    assert list(s) == ["c", "e", "y"]


### SUBSET / SUPERSET ######################################


def test_subset_superset_relations() -> None:
    """Verifies `<=`, `<`, `>=`, `>` against iterables and `OrderedSet`."""
    s = OrderedSet([1, 2, 3])
    assert s <= [1, 2, 3, 4]
    assert s < [1, 2, 3, 4]
    assert s >= [1, 2]
    assert s > [1, 2]
    assert s <= OrderedSet([1, 2, 3])
    assert not (s < OrderedSet([1, 2, 3]))


### CONVERTERS / PROCESSORS / VERIFIERS ####################


def test_to_ordered_set_variants() -> None:
    """Verifies dispatch variants of `to_ordered_set`."""
    s1 = to_ordered_set(OrderedSet([1, 2]))
    assert isinstance(s1, OrderedSet)
    assert list(s1) == [1, 2]
    s2 = to_ordered_set([2, 2, 1])
    assert list(s2) == [2, 1]
    s3 = to_ordered_set(1, 2, 1)
    assert list(s3) == [1, 2]


def test_filter_include_exclude_helpers() -> None:
    """Verifies `filter_ordered_set`, `include_ordered_set`, `exclude_ordered_set`."""
    data = ["a", "b", "c", "d"]
    inc = ["b", "d", "e"]
    exc = ["a", "x"]
    assert list(filter_ordered_set(data, inclusion=inc)) == ["b", "d"]
    assert list(filter_ordered_set(data, exclusion=exc)) == ["b", "c", "d"]
    assert list(filter_ordered_set(data, inclusion=inc, exclusion=["d"])) == ["b"]
    assert list(include_ordered_set(data, inc)) == ["b", "d"]
    assert list(exclude_ordered_set(data, exc)) == ["b", "c", "d"]


def test_is_ordered_set_verifier(s_abc: OrderedSet[str]) -> None:
    """Verifies the type guard `is_ordered_set`."""
    assert is_ordered_set(s_abc)
    assert not is_ordered_set(["a", "b"])
