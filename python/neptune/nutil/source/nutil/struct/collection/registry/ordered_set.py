#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains an ordered set implementation and its adapter
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

from collections import OrderedDict

from nutil.common import *

####################################################################################################
# ORDERED SET CLASSES
####################################################################################################

__ORDERED_SET_CLASSES_____________________________ = ""

T = TypeVar("T")


class OrderedSet(AbstractSequentialCollection[T], MutableSet[T], Generic[T]):
    """
    Implements a set that preserves the insertion order.

    Iteration and indexing follow the insertion order. Equality and set algebra follow the standard
    set semantics. Operators return OrderedSet and preserve a deterministic order.
    """

    ##############################################
    # INITIALIZATION
    ##############################################

    def __init__(self, iterable: Iterable[T] = ()) -> None:
        """Initializes the specified ordered set with the specified iterable (O(n) in C)."""
        super().__init__()
        self.elements: OrderedDict[T, None] = OrderedDict.fromkeys(iterable)  # O(n) in C

    ##############################################
    # COLLECTION
    ##############################################

    def __len__(self) -> int:
        """Returns the number of elements contained in the specified ordered set (O(1))."""
        return len(self.elements)

    ##############################################
    # CONTAINER
    ##############################################

    def __contains__(self, x: object) -> bool:
        """Returns True if the specified element is contained in the ordered set (O(1) avg)."""
        return x in self.elements

    ##############################################
    # ITERABLE
    ##############################################

    def __iter__(self) -> Iterator[T]:
        """Returns an iterator over the specified ordered set elements (O(1))."""
        return iter(self.elements.keys())

    ##############################################
    # SEQUENCE
    ##############################################

    @overload
    def __getitem__(self, index: int) -> T: ...

    @overload
    def __getitem__(self, index: slice) -> List[T]: ...

    def __getitem__(self, index):
        """Returns the element(s) at the specified position or slice (in the insertion order)."""
        keys = self.elements.keys()
        if isinstance(index, slice):
            # Fast path for unit-step slices without full materialization
            if index.step is None or index.step == 1:
                start, stop, _ = index.indices(len(self))
                if stop <= start:
                    return []
                # Extract exactly the required window
                return list(itertools.islice(keys, start, stop))  # O(k)
            # General slice path (handles negative and non-unit steps)
            return list(keys)[index]  # O(n) materialization

        try:
            index = operator.index(index)  # accept int-like types (e.g., numpy.int64)
        except TypeError as e:
            raise TypeError(
                f"Collection indices must be integers or slices, not '{type(index).__name__}'"
            ) from e

        n = len(self.elements)
        if index < 0:
            index += n
        if index < 0 or index >= n:
            raise IndexError("OrderedSet index out of range")
        return next(itertools.islice(keys, index, index + 1))  # O(index)

    def __reversed__(self) -> Iterator[T]:
        """Returns a reversed iterator over the specified ordered set elements (O(1))."""
        # In CPython 3.7+, reversed(OrderedDict) yields keys in reverse insertion order.
        return reversed(self.elements)  # reversed keys view

    def first(self) -> T:
        """Returns the first element of the specified ordered set (O(1) amortized)."""
        return next(iter(self.elements))  # raises StopIteration if empty

    def last(self) -> T:
        """Returns the last element of the specified ordered set (O(1) amortized)."""
        return next(reversed(self.elements))  # raises StopIteration if empty

    ##############################################
    # CONVERTERS
    ##############################################

    def to_iterable(self) -> Iterable[T]:
        """Returns an iterable view of the specified ordered set (zero-copy) (O(1))."""
        return self.elements.keys()

    ##############################################
    # MUTATORS
    ##############################################

    def add(self, element: T) -> None:
        """Adds the specified element to the specified ordered set (O(1) avg)."""
        self.elements[element] = None

    def discard(self, element: T) -> None:
        """Discards the specified element from the specified ordered set if present (O(1) avg)."""
        self.elements.pop(element, None)

    def update(self, iterable: Iterable[T]) -> None:
        """Updates the specified ordered set with the specified iterable of elements (O(n) in C)."""
        if isinstance(iterable, OrderedSet):
            self.elements.update(iterable.elements)  # O(n) in C
        else:
            self.elements.update(OrderedDict.fromkeys(iterable))  # O(n) in C

    def clear(self) -> None:
        """Removes all elements from the specified ordered set (O(n) in C)."""
        self.elements.clear()

    ##############################################
    # SET ALGEBRA (ORDER-PRESERVING)
    ##############################################

    def __and__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """Returns an ordered intersection (O(m + n) avg)."""
        other_set = set(other)  # O(m)
        return OrderedSet(x for x in self if x in other_set)  # O(n)

    def __or__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """Returns an ordered union (self first, then new elements from other) (O(m + n) avg)."""
        out = OrderedSet(self)  # O(n)
        for x in other:  # O(m)
            if x not in out:  # O(1) avg
                out.add(x)  # O(1) avg
        return out

    def __sub__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """Returns an ordered difference (O(m + n) avg)."""
        other_set = set(other)  # O(m)
        return OrderedSet(x for x in self if x not in other_set)  # O(n)

    def __xor__(self, other: Iterable[T]) -> "OrderedSet[T]":
        """Returns an ordered symmetric difference (O(m + n) avg)."""
        other_list = list(other)  # preserve order of `other`
        other_set = set(other_list)
        self_set = set(self.elements.keys())
        left_only = (x for x in self if x not in other_set)
        right_only = (x for x in other_list if x not in self_set)
        return OrderedSet(itertools.chain(left_only, right_only))

    ##############################################
    # SUBSET / SUPERSET
    ##############################################

    def __le__(self, other: Iterable[T]) -> bool:
        """Returns True if self is a subset of other (O(m + n) avg)."""
        other_keys = other.elements.keys() if isinstance(other, OrderedSet) else set(other)  # O(m)
        return all(e in other_keys for e in self)  # O(n)

    def __lt__(self, other: Iterable[T]) -> bool:
        """Returns True if self is a proper subset of other (O(m + n) avg)."""
        return self <= other and self != other

    def __ge__(self, other: Iterable[T]) -> bool:
        """Returns True if self is a superset of other (O(m + n) avg)."""
        return all(e in self for e in other)

    def __gt__(self, other: Iterable[T]) -> bool:
        """Returns True if self is a proper superset of other (O(m + n) avg)."""
        return self >= other and self != other


@adapts(OrderedSet, priority=3)
class OrderedSetAdapter(AbstractSequentialCollectionAdapter[T]):
    """
    Delegates to an `OrderedSet` implementation with ordered-set-aware helpers.

    Inherits sequential helpers from `AbstractSequentialCollectionAdapter` and reuses the
    `AbstractCollectionAdapter` mutators to keep semantics consistent.
    """

    ##############################################
    # ITERABLE
    ##############################################

    def to_iterable(self, x: OrderedSet[T]) -> Iterable[T]:
        """Returns an iterable view of the specified ordered set (zero-copy)."""
        return x.to_iterable()

    ##############################################
    # OPTIONAL MUTATORS (MUTABLE)
    ##############################################

    def add(self, x: OrderedSet[T], v: T) -> None:
        """Adds the specified element to the specified ordered set."""
        x.add(v)

    def discard(self, x: OrderedSet[T], v: T) -> None:
        """Discards the specified element from the specified ordered set."""
        x.discard(v)

    def update(self, x: OrderedSet[T], it: Iterable[T]) -> None:
        """Updates the specified ordered set with the specified iterable."""
        x.update(it)


####################################################################################################
# ORDERED SET CONSTANTS
####################################################################################################

__ORDERED_SET_CONSTANTS___________________________ = ""

ORDERED_SET_TYPE = OrderedSet


####################################################################################################
# ORDERED SET VERIFIERS
####################################################################################################

__ORDERED_SET_VERIFIERS___________________________ = ""


def is_ordered_set(x: Any) -> bool:
    """Returns True if the specified object is an `OrderedSet`."""
    return isinstance(x, ORDERED_SET_TYPE)


####################################################################################################
# ORDERED SET CONVERTERS
####################################################################################################

__ORDERED_SET_CONVERTERS__________________________ = ""


def to_ordered_set(*args: Any) -> OrderedSet[Any]:
    """
    Returns an `OrderedSet` from the specified arguments.

    • If exactly one OrderedSet is specified, returns it unchanged.
    • If exactly one non-OrderedSet argument is specified, constructs OrderedSet(arg).
    • If multiple arguments are specified, constructs OrderedSet(args).
    """
    if len(args) == 1:
        arg = args[0]
        if is_ordered_set(arg):
            return arg
        return OrderedSet(arg)
    return OrderedSet(args)


####################################################################################################
# ORDERED SET PROCESSORS
####################################################################################################

__ORDERED_SET_PROCESSORS__________________________ = ""


def filter_ordered_set(
    s: Optional[Iterable[T]],
    inclusion: Optional[Iterable[T]] = None,
    exclusion: Optional[Iterable[T]] = None,
) -> OrderedSet[T]:
    """Returns the values of the specified ordered set that are in the specified inclusive set and
    are not in the specified exclusive set."""
    if is_empty(s):
        return OrderedSet()
    if not has_filter(inclusion=inclusion, exclusion=exclusion):
        return to_ordered_set(s)
    if is_null(inclusion):
        return to_ordered_set(s) - set(exclusion)
    if is_empty(exclusion):
        return to_ordered_set(s) & set(inclusion)
    return to_ordered_set(s) & set(inclusion) - set(exclusion)


def include_ordered_set(s: Iterable[T], inclusion: Iterable[T]) -> OrderedSet[T]:
    """Returns the values of the specified ordered set that are in the specified inclusive set."""
    return filter_ordered_set(s, inclusion=inclusion)


def exclude_ordered_set(s: Iterable[T], exclusion: Iterable[T]) -> OrderedSet[T]:
    """Returns the values of the specified ordered set that are not in the specified exclusive set."""
    return filter_ordered_set(s, exclusion=exclusion)
