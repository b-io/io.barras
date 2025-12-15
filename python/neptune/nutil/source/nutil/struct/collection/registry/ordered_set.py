#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide an ordered set implementation and its adapter.
########################################################################################################################

from __future__ import annotations

from collections import OrderedDict
from typing import MutableSet

from nutil.common import *

## ORDERED SET CLASSES ###################################################################

__ORDERED_SET_CLASSES_______________________________________ = ""

T = TypeVar("T")


class OrderedSet(AbstractSequentialCollection[T], MutableSet[T], Generic[T]):
    """
    An ordered set implementation that preserves the insertion order.

    Notes:
        • Elements must be hashable (stored as `OrderedDict` keys).
        • Iteration, indexing, and slicing follow the insertion order.
        • Membership, equality, and set algebra match `set`.
        • Operators return an `OrderedSet` and preserve a deterministic order (`self`'s order first).
        • Average complexity: add/discard/membership O(1) amortized; iteration O(n); indexing O(1+index).
    """

    def __init__(self, iterable: Iterable[T] = ()) -> None:
        """
        Initializes this ordered set with the specified `Iterable`.

        Complexity:
            Let `n = len(iterable)`.
            Overall: O(n).
        """
        super().__init__()
        self.elements: OrderedDict[T, None] = OrderedDict.fromkeys(iterable)

    ### COLLECTION #########################################

    def __len__(self) -> int:
        """
        Returns the number of elements contained in this ordered set.

        Complexity:
            O(1).
        """
        return len(self.elements)

    ### CONTAINER ##########################################

    def __contains__(self, element: object) -> bool:
        """
        Returns whether the specified element is contained in this ordered set.

        Complexity:
            Average-case O(1) (hash table lookup).
        """
        return element in self.elements

    ### ITERABLE ###########################################

    def __iter__(self) -> Iterator[T]:
        """
        Returns an `Iterator` over the elements of this ordered set.

        Complexity:
            Creation: O(1).
            Traversal: O(n).
        """
        return create_iterator(self.elements.keys())

    ### SEQUENCE ###########################################

    @overload
    def __getitem__(self, index: int) -> T: ...

    @overload
    def __getitem__(self, index: slice) -> List[T]: ...

    def __getitem__(self, index):
        """
        Returns the element(s) at the specified position or slice (in the insertion order).

        Complexity:
            • Integer index `i`: O(i + 1) via iterator skipping (worst-case O(n)).
            • Slice with unit step (`step is None` or `step == 1`) and length `k = stop - start`: O(k).
            • General slice (negative or non-unit step): O(n) (materialize keys) + O(k) slicing.
        """
        keys = self.elements.keys()
        if isinstance(index, slice):
            # Fast path for unit-step slices without full materialization
            if index.step is None or index.step == 1:
                start, stop, _ = index.indices(len(self))
                if stop <= start:
                    return []
                # Extract exactly the required window
                return list(itertools.islice(keys, start, stop))
            # General slice path (handles negative and non-unit steps)
            return list(keys)[index]

        try:
            index = operator.index(index)  # accepts int-like types (e.g., numpy.int64)
        except TypeError as e:
            raise TypeError(f"Collection indices must be integers or slices, not '{type(index).__name__}'") from e

        n = len(self.elements)
        if index < 0:
            index += n
        if index < 0 or index >= n:
            raise IndexError("OrderedSet index out of range")
        return next(itertools.islice(keys, index, index + 1))

    def __reversed__(self) -> Iterator[T]:
        """
        Returns a reversed `Iterator` over the elements of this ordered set.

        Complexity:
            Creation: O(1).
            Traversal: O(n).
        """
        # In CPython 3.7+, `reversed(OrderedDict)` yields keys in the reverse of the insertion order
        return reversed(self.elements)  # reversed keys view

    def first(self) -> T:
        """
        Returns the first element of this ordered set.

        Complexity:
            Amortized O(1).
        """
        try:
            return next(create_iterator(self.elements))
        except StopIteration:
            raise IndexError("OrderedSet is empty") from None

    def last(self) -> T:
        """
        Returns the last element of this ordered set.

        Complexity:
            Amortized O(1).
        """
        try:
            return next(reversed(self.elements))
        except StopIteration:
            raise IndexError("OrderedSet is empty") from None

    ### CONVERTERS #########################################

    def to_iterable(self) -> Iterable[T]:
        """
        Returns an `Iterable` view of this ordered set (zero-copy).

        Complexity:
            O(1) to get the view; iteration over it is O(n).
        """
        return self.elements.keys()

    ### MUTATORS ###########################################

    def add(self, element: T) -> None:
        """
        Adds the specified element to this ordered set.

        Complexity:
            Amortized O(1) (hash table insert; occasional resize).
        """
        self.elements[element] = None

    def discard(self, element: T) -> None:
        """
        Discards the specified element from this ordered set if present.

        Complexity:
            Amortized O(1) (hash table delete; occasional resize).
        """
        self.elements.pop(element, None)

    def update(self, *iterables: Iterable[T]) -> None:
        """
        Updates this ordered set with one or more specified `Iterable`,
        preserving the insertion order.

        Complexity:
            Let `k_i = len(iterables[i])`.
            Overall: amortized O(∑ k_i).
            Per iterable: amortized O(k_i) (dict/`OrderedDict` updates; occasional resizes).
        """
        for iterable in iterables:
            if is_ordered_set(iterable):
                self.elements.update(iterable.elements)
            else:
                self.elements.update(OrderedDict.fromkeys(iterable))

    def clear(self) -> None:
        """
        Removes all elements from this ordered set.

        Complexity:
            O(n).
        """
        self.elements.clear()

    ### SET ALGEBRA (ORDER-PRESERVING) #####################

    def __and__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        Ordered intersection (in `self`'s order).

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            If the elements of `other` are hashable: O(m + n).
            Otherwise: O(m · n).
        """
        try:
            other_set = set(other)
        except TypeError:
            other_list = list(other)
            return OrderedSet(x for x in self if any(x == y for y in other_list))
        return OrderedSet(x for x in self if x in other_set)

    def __iand__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        In-place ordered intersection (keep elements also in `other`).

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            If the elements of `other` are hashable: O(m + n).
            Otherwise: O(m · n).
        """
        try:
            other_set = set(other)
        except TypeError:
            other_list = list(other)
            self.elements = OrderedDict((k, None) for k in self.elements if any(k == y for y in other_list))
        else:
            self.elements = OrderedDict((k, None) for k in self.elements if k in other_set)
        return self

    def __rand__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        Ordered intersection (in `other`'s order).
        Requires hashable elements in `other`.

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            Conversion `OrderedSet(other)`: O(m).
            Then intersection as above: O(m + n) if hashable, else O(m · n).
            Overall: O(m + n) if hashable; else O(m · n).
        """
        return OrderedSet(other).__and__(self)

    ##########################

    def __or__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        Ordered union (this ordered set first, then new elements from `other`).
        Requires hashable elements in `other`.

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            O(m + n).
        """
        union_set = OrderedSet(self)
        union_set.update(other)
        return union_set

    def __ior__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        In-place ordered union (this ordered set first, then new elements from `other`).
        Requires hashable elements in `other`.

        Complexity:
            Let `m = len(other)`.
            O(m).
        """
        if isinstance(other, OrderedSet):
            self.elements.update(other.elements)
        else:
            self.elements.update(OrderedDict.fromkeys(other))
        return self

    def __ror__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        Ordered union (`other` first, then new elements from `self`).
        Requires hashable elements in `other`.

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            Conversion `OrderedSet(other)`: O(m).
            Then union: O(m + n).
            Overall: O(m + n).
        """
        return OrderedSet(other).__or__(self)

    ##########################

    def __sub__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        Ordered difference.

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            If the elements of `other` are hashable: O(m + n).
            Otherwise: O(m · n).
        """
        try:
            other_set = set(other)
        except TypeError:
            other_list = list(other)
            return OrderedSet(x for x in self if not any(x == y for y in other_list))
        return OrderedSet(x for x in self if x not in other_set)

    def __isub__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        In-place ordered difference (remove elements found in `other`).

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            If the elements of `other` are hashable: O(m + n).
            Otherwise: O(m · n).
        """
        try:
            other_set = set(other)
        except TypeError:
            other_list = list(other)
            self.elements = OrderedDict((k, None) for k in self.elements if not any(k == y for y in other_list))
        else:
            self.elements = OrderedDict((k, None) for k in self.elements if k not in other_set)
        return self

    def __rsub__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        Ordered difference (`other` minus `self`, in `other`'s order).
        Requires hashable elements in `other`.

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            Conversion `OrderedSet(other)`: O(m).
            Then difference: O(m + n) if hashable, else O(m · n).
        """
        return OrderedSet(other).__sub__(self)

    ##########################

    def __xor__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        Ordered symmetric difference.
        Requires hashable elements in `other`.

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            If the elements of `other` are hashable: O(m + n).
            Otherwise: TypeError (elements must be hashable).
        """
        other_list = list(other)
        try:
            other_set = set(other_list)
        except TypeError:
            raise TypeError("Symmetric difference requires hashable elements in 'other'")
        else:
            left_only = (x for x in self if x not in other_set)
            right_only = (y for y in other_list if y not in self.elements)
            return OrderedSet(itertools.chain(left_only, right_only))

    def __ixor__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        In-place ordered symmetric difference.
        Requires hashable elements in `other`.

        Keeps elements unique to this ordered set (in `self`'s order),
        then elements unique to `other` (in `other`'s order).

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            If the elements of `other` are hashable: O(m + n).
            Otherwise: TypeError (elements must be hashable).
        """
        other_list = list(other)
        try:
            other_set = set(other_list)
        except TypeError:
            raise TypeError("In-place symmetric difference requires hashable elements in 'other'")
        else:
            left_only = (x for x in self if x not in other_set)
            right_only = (y for y in other_list if y not in self.elements)
            new_iter = itertools.chain(left_only, right_only)
        self.elements = OrderedDict.fromkeys(new_iter)
        return self

    def __rxor__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """
        Ordered symmetric difference (uniques from `other` in `other`'s order, then uniques from `self`).
        Requires hashable elements in `other`.

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            Conversion `OrderedSet(other)`: O(m).
            Then symmetric difference: O(m + n) if hashable; else TypeError.
        """
        return OrderedSet(other).__xor__(self)

    ### SUBSET / SUPERSET ##################################

    def __le__(self, other: Iterable[T]) -> bool:
        """
        Return whether this ordered set is a subset of `other`.

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            • If `other` is an `OrderedSet`: O(n).
            • Else if the elements of `other` are hashable: O(m + n).
            • Else: O(m · n).
        """
        if isinstance(other, OrderedSet):
            other_keys = other.elements  # dict-like membership is O(1)
            return all(e in other_keys for e in self)
        try:
            other_keys = set(other)
            return all(e in other_keys for e in self)
        except TypeError:
            other_list = list(other)
            return all(any(e == y for y in other_list) for e in self)

    def __lt__(self, other: Iterable[T]) -> bool:
        """
        Return whether this ordered set is a proper subset of `other`.

        Complexity:
            Same as `__le__`, plus an equality check: up to O(n).
        """
        return self <= other and self != other

    def __ge__(self, other: Iterable[T]) -> bool:
        """
        Return whether this ordered set is a superset of `other`.

        Complexity:
            Let `m = len(other)`, `n = len(self)`.
            • If `other` is an `OrderedSet`: O(m).
            • Else if the elements of `other` are hashable: O(m) membership checks.
            • Else: O(m · n).
        """
        if isinstance(other, OrderedSet):
            return all(e in self.elements for e in other)
        try:
            return all(e in self for e in other)
        except TypeError:
            other_list = list(other)
            return all(any(y == e for y in self) for e in other_list)

    def __gt__(self, other: Iterable[T]) -> bool:
        """
        Returns whether this ordered set is a proper superset of `other`.

        Complexity:
            Same as `__ge__`, plus an equality check: up to O(n).
        """
        return self >= other and self != other

    ### HASH ###############################################

    def __hash__(self) -> int:
        """
        Returns a hash value consistent with the set-like equality.

        Notes:
            • Uses a `frozenset` of the elements, so the hash is independent of order,
              matching the set-like equality semantics.
            • This ordered set is mutable. Do not mutate it while it is being used
              as a key in a dictionary or as an element in another set; treat such
              instances as effectively immutable.

        Complexity:
            Let `n = len(self)`. Building the `frozenset` and hashing it is O(n).
        """
        return hash(frozenset(self.elements.keys()))

    ### REPRESENTATION #####################################

    REPR_OPEN = "{"
    REPR_CLOSE = "}"
    STR_OPEN = "{"
    STR_CLOSE = "}"


@adapts(OrderedSet, priority=3)
class OrderedSetAdapter(AbstractSequentialCollectionAdapter[T]):
    """
    An `OrderedSet` adapter with ordered-set-aware helpers.

    Inherits sequential helpers from `AbstractSequentialCollectionAdapter` and reuses the
    `AbstractCollectionAdapter` mutators to keep semantics consistent.
    """

    ### ITERABLE ###########################################

    def to_iterable(self, x: OrderedSet[T]) -> Iterable[T]:
        """
        Returns an `Iterable` view of this ordered set (zero-copy).

        Complexity:
            O(1) to get the view; iteration over it is O(n).
        """
        return x.to_iterable()

    ### OPTIONAL MUTATORS (MUTABLE) ########################

    def add(self, x: OrderedSet[T], v: T) -> None:
        """
        Adds the specified element to this ordered set.

        Complexity:
            Amortized O(1).
        """
        x.add(v)

    def discard(self, x: OrderedSet[T], v: T) -> None:
        """
        Discards the specified element from this ordered set if present.

        Complexity:
            Amortized O(1).
        """
        x.discard(v)

    def update(self, x: OrderedSet[T], *iterables: Iterable[T]) -> None:
        """
        Updates this ordered set with one or more specified `Iterable`,
        preserving the insertion order.

        Complexity:
            Let `k_i = len(iterables[i])`.
            Overall: amortized O(∑ k_i).
            Per iterable: amortized O(k_i).
        """
        x.update(*iterables)


## ORDERED SET CONSTANTS #################################################################

__ORDERED_SET_CONSTANTS_____________________________________ = ""

ORDERED_SET_TYPE = OrderedSet


## ORDERED SET CONVERTERS ################################################################

__ORDERED_SET_CONVERTERS____________________________________ = ""


def to_ordered_set(*args: Any) -> OrderedSet[Any]:
    """
    Returns an `OrderedSet` from the specified arguments.

    Complexity:
        • One `OrderedSet` arg: O(1) (return as-is).
        • One non-`OrderedSet` arg of length `k`: O(k).
        • Multiple args (`p` positional items): O(p).

    Dispatch:
        • If exactly one `OrderedSet` is specified, returns it unchanged.
        • If exactly one non-`OrderedSet` argument is specified, constructs `OrderedSet(arg)`.
        • If multiple arguments are specified, constructs `OrderedSet(args)`.
    """
    if len(args) == 1:
        arg = args[0]
        if is_ordered_set(arg):
            return arg
        return OrderedSet(arg)
    return OrderedSet(args)


## ORDERED SET PROCESSORS ################################################################

__ORDERED_SET_PROCESSORS____________________________________ = ""


def filter_ordered_set(
    s: Optional[Iterable[T]],
    inclusion: Optional[Iterable[T]] = None,
    exclusion: Optional[Iterable[T]] = None,
) -> OrderedSet[T]:
    """
    Returns the values of `s` that are in `inclusion` and not in `exclusion`.

    Complexity:
        Let `n = len(s)`, `a = len(inclusion or [])`, `b = len(exclusion or [])`.
        Conversions to `OrderedSet`: O(n) + O(a) + O(b).
        Set algebra: typically O(n + a + b) when hashable.
        Overall: O(n + a + b).
    """
    if is_empty(s):
        return OrderedSet()
    s = to_ordered_set(s)
    if not has_filter(inclusion=inclusion, exclusion=exclusion):
        return s
    inclusion = to_ordered_set(inclusion) if inclusion is not None else None
    exclusion = to_ordered_set(exclusion) if exclusion is not None else None
    if inclusion is None:
        return s - (exclusion or OrderedSet())
    elif exclusion is None or len(exclusion) == 0:
        return s & inclusion
    return (s & inclusion) - exclusion


def include_ordered_set(s: Iterable[T], inclusion: Iterable[T]) -> OrderedSet[T]:
    """
    Returns the values of `s` that are in `inclusion`.

    Complexity:
        See `filter_ordered_set`; typically O(|s| + |inclusion|).
    """
    return filter_ordered_set(s, inclusion=inclusion)


def exclude_ordered_set(s: Iterable[T], exclusion: Iterable[T]) -> OrderedSet[T]:
    """
    Returns the values of `s` that are not in `exclusion`.

    Complexity:
        See `filter_ordered_set`; typically O(|s| + |exclusion|).
    """
    return filter_ordered_set(s, exclusion=exclusion)


## ORDERED SET VERIFIERS #################################################################

__ORDERED_SET_VERIFIERS_____________________________________ = ""


def is_ordered_set(x: Any) -> bool:
    """Returns whether `x` is an `OrderedSet`."""
    return isinstance(x, ORDERED_SET_TYPE)


def is_ordered_set_type(t: Type[Any]) -> bool:
    """Returns whether `t` is an `OrderedSet` type."""
    return issubclass(t, ORDERED_SET_TYPE)
