#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide a common collection registry with its adapters and utilities.
########################################################################################################################

from __future__ import annotations

import itertools
import operator
from abc import ABC, abstractmethod
from collections.abc import (
    Iterator as ABCIterator,
    Mapping as ABCMapping,
    MutableMapping as ABCMutableMapping,
)
from functools import lru_cache
from threading import RLock
from typing import (
    cast,
    ClassVar,
    Collection,
    Dict,
    Generic,
    ItemsView,
    Iterable,
    Iterator,
    KeysView,
    List,
    Mapping,
    Optional,
    overload,
    Sequence,
    Set,
    Tuple,
    Type,
    Union,
    ValuesView,
)

import numpy as np

from nutil.decorators.common import *
from nutil.metaclasses import FinalSingletonMeta


__COMMON_COLLECTION_REGISTRY_DECORATORS___________________________________________________ = ""


def adapts(*target_types: Type[Any], priority: int = 0, override: bool = False):
    """
    Class decorator that:
        • Sets class-level metadata:
            – `__adapts__`: a `tuple` of all supported target types.
            – `__adapter_priority__`: the adapter priority value.
        • Sets instance-level metadata for each registered adapter:
            – `__adapts__`: the single bound target type for that instance.
            – `__adapter_priority__`: the same priority value.
        • Creates and registers one adapter instance per target type with the `CollectionRegistry`.

    Complexity:
        Let `t = len(target_types)`.
        Deduplication: O(t).
        Registration: O(t).
        Overall: O(t).
    """
    if not target_types:
        raise ValueError("At least one target type must be specified for '@adapts(...)'")

    # Deduplicate while preserving order
    unique_target_types = tuple(dict.fromkeys(target_types))

    def _adapts(cls: Type["CollectionAdapter[Any]"]):
        # Class-level metadata
        setattr(cls, "__adapts__", unique_target_types)
        setattr(cls, "__adapter_priority__", priority)

        registry = CollectionRegistry()
        for target_type in unique_target_types:
            adapter = cls()
            # Instance-level metadata
            setattr(adapter, "__adapts__", target_type)
            setattr(adapter, "__adapter_priority__", priority)
            registry.register(target_type, adapter, override=override)
        return cls

    return _adapts


__COMMON_COLLECTION_REGISTRY_CLASSES______________________________________________________ = ""

# Typing alias for any supported element types across `Struct`
ElementType = Union[Type[Any], np.dtype[Any]]

T = TypeVar("T")


class CollectionAdapter(Generic[T], ABC):
    """
    An adapter contract for external collection types.

    Minimal hooks mirror `AbstractCollection` semantics. Override for O(1) where possible.
    The `@adapts(...)` decorator sets class-level `__adapts__` (tuple of supported targets) and
    binds instance-level `__adapts__` (the single bound target), plus `__adapter_priority__`.
    """

    # Instance attributes only; class attributes are set on the class object by `@adapts(...)`
    __slots__ = ("__adapts__", "__adapter_priority__")

    # Injected by `@adapts(...)` at runtime
    __adapts__: Type[Any]  # bound target type (instance-level)
    __adapter_priority__: int  # instance priority metadata

    ### COLLECTION #########################################

    def size(self, x: Any) -> int:
        """
        Returns the number of elements contained in the specified collection.

        Complexity:
            • If `len(x)` is supported: O(1).
            • Else if `length_hint(x)` available: O(1) (hint only).
            • Else: O(n) by iteration.
        """
        try:
            return len(x)  # enforces int >= 0
        except TypeError:
            pass
        hint = operator.length_hint(x, -1)
        if hint >= 0:
            return hint
        n = 0
        for _ in self.to_iterable(x):
            n += 1
        return n

    ### CONTAINER ##########################################

    def contains(self, x: Any, v: T) -> bool:
        """
        Returns whether the specified element is contained in the specified collection.

        Complexity:
            • If `x.__contains__` exists:
                – hash-based containers: average O(1).
                – sequence/linear containers: O(n).
            • Else (iterate): O(n).
        """
        __contains__ = getattr(x, "__contains__", None)
        if __contains__ is None:
            return any(e == v for e in self.to_iterable(x))
        return __contains__(v)

    ### ITERABLE ###########################################

    @abstractmethod
    def to_iterable(self, x: Any) -> Iterable[T]:
        """
        Returns an `Iterable` view of the specified collection.

        Complexity:
            Typically O(1) to obtain the view; iterating the view is O(n).

        Raises:
            NotImplementedError: If the adapter does not implement this method.
        """

    ### ACCESSORS ##########################################

    @classproperty
    def target_types(cls) -> Tuple[Type[Any], ...]:
        """
        Returns the `tuple` of target types this adapter class supports.

        Complexity:
            O(1).
        """
        return getattr(cls, "__adapts__", ())

    @property
    def target_type(self) -> Type[Any]:
        """
        Returns the bound target type for this adapter instance.

        Complexity:
            O(1).
        """
        target_type = getattr(self, "__adapts__", None)
        # Instance must hold a single concrete type; otherwise it is unbound or class-level metadata
        if not isinstance(target_type, type):
            raise TypeError(
                "Adapter instance is not bound to a single target; ensure it was constructed via '@adapts(...)'"
            )
        return target_type

    ### CONVERTERS #########################################

    def from_iterable(self, iterable: Iterable[T]) -> Any:
        """
        Returns a new instance of this adapter target type constructed from the specified `Iterable`.

        Resolution order:
            1) Uses `target_type.from_iterable(iterable)` if present (e.g., `AbstractCollection`).
            2) Tries `target_type(iterable)`.
            3) Tries `target_type(list(iterable))` as the final fallback.

        Complexity:
            Let `n = len(iterable)` if known; else count of elements consumed.
            • Path (1): depends on implementation (typically O(n)).
            • Path (2): typically O(n).
            • Path (3): materialize a `list` O(n), then construction O(n).
            Overall (typical): O(n).

        Notes:
            • Only constructor-shape errors (`TypeError`) trigger fallbacks; other exceptions propagate.
            • Single-pass iterables are duplicated to avoid partial consumption across attempts.
        """
        target_type = self.target_type

        # 1) Canonical hook for the target types implementing `from_iterable`
        if has_callable(target_type, "from_iterable"):
            return target_type.from_iterable(iterable)

        # Duplicate the single-pass `Iterable` so the fallback sees the full stream
        it1, it2 = create_safe_iterables(iterable, 2)

        # 2) Try the constructor of the target type directly with the `Iterable`
        try:
            return target_type(it1)
        except TypeError:
            pass  # likely a signature/type mismatch; try the final fallback

        # 3) Fallback: materialize once and pass a `list`
        try:
            return target_type(list(it2))
        except TypeError as e:
            raise TypeError(
                f"Cannot construct '{getattr(target_type, '__name__', str(target_type))}' from an 'Iterable'; "
                f"provide 'from_iterable' or a constructor accepting an 'Iterable'/'list'"
            ) from e

    ##########################

    def to_array(self, x, element_type: Optional[ElementType] = None) -> np.ndarray:
        """
        Returns an `array` built from the specified collection.

        Complexity:
            O(n) to materialize.
        """
        return np.array(self.to_iterable(x), dtype=element_type)

    def to_list(self, x: Any) -> List[T]:
        """
        Returns a `list` built from the specified collection.

        Complexity:
            O(n).
        """
        return list(self.to_iterable(x))

    def to_set(self, x: Any) -> Set[T]:
        """
        Returns a `set` built from the specified collection.

        Complexity:
            O(n) average.
        """
        return set(self.to_iterable(x))

    def to_tuple(self, x: Any) -> Tuple[T, ...]:
        """
        Returns a `tuple` built from the specified collection.

        Complexity:
            O(n).
        """
        return tuple(self.to_iterable(x))

    ### OPTIONAL MUTATORS (IMMUTABLE BY DEFAULT) ###########

    def add(self, x: Any, v: T) -> None:
        """Adds the specified element to the specified collection."""
        raise TypeError("Collection is immutable")

    def discard(self, x: Any, v: T) -> None:
        """Discards the specified element from the specified collection if present."""
        raise TypeError("Collection is immutable")

    def update(self, x: Any, *iterables: Iterable[T]) -> None:
        """Updates the specified collection with one or more specified `Iterable`."""
        raise TypeError("Collection is immutable")

    ### VERIFIERS ##########################################

    def is_instance(self, x: Any) -> bool:
        """Returns whether `x` is an instance of this adapter target type."""
        return isinstance(x, self.target_type)


class CollectionRegistry(metaclass=FinalSingletonMeta):
    """
    A global, thread-safe registry of collection types mapped to their adapters,
    with deterministic nearest-match resolution.

    Resolution strategy:
    1) If an exact adapter is registered for the type, select the highest-priority one.
       • When multiple adapters share the same priority, the earliest registered is chosen.
    2) Otherwise, inspect the method resolution order (MRO) of the type:
       • Among all registered base types that appear in the MRO, pick the base with the
         smallest index (i.e., the nearest ancestor).
       • Within these base adapters, prefer the highest-priority adapter.
       • If multiple adapters share the same priority, the earliest registered is chosen.
    """

    _REGISTRY: ClassVar[Dict[Type[Any], List[CollectionAdapter[Any]]]] = {}
    _LOCK: ClassVar[RLock] = RLock()

    def exists(self, x: Any) -> bool:
        """Returns whether there is a registered adapter for `x` or its bases."""
        return self.get(x) is not None

    def get(self, x_or_t: Any) -> Optional[CollectionAdapter[Any]]:
        """Returns the best adapter for the instance or type, or None if unregistered."""
        t = x_or_t if isinstance(x_or_t, type) else type(x_or_t)
        with self._LOCK:
            return self._resolve_adapter_for_type(t)

    def register(self, t: Type[Any], adapter: CollectionAdapter[Any], *, override: bool = False) -> None:
        """Registers the specified collection type with the specified adapter."""
        if not isinstance(adapter, CollectionAdapter):
            raise TypeError("Adapter must be an instance of 'CollectionAdapter' (not the class)")
        with self._LOCK:
            if override:
                self._REGISTRY[t] = [adapter]
            else:
                self._REGISTRY.setdefault(t, []).append(adapter)
            # Invalidate the cache after any change
            self._resolve_adapter_for_type.cache_clear()

    @staticmethod
    @lru_cache(maxsize=4096)
    def _resolve_adapter_for_type(t: Type[Any]) -> Optional[CollectionAdapter[Any]]:
        """Returns the best adapter for the specified type. Caches via LRU (O(1) after warm-up)."""

        def _best(adapters: List[CollectionAdapter[Any]]) -> CollectionAdapter[Any]:
            """Resolves by the highest priority; stable for equal priority (list order)."""
            return max(adapters, key=lambda a: getattr(a, "__adapter_priority__", 0))

        # 1) Exact adapters
        exact = CollectionRegistry._REGISTRY.get(t)
        if exact:
            return _best(exact)

        # 2) MRO-nearest adapters among registered bases
        best_adapter: Optional[CollectionAdapter[Any]] = None
        best_distance = float("inf")
        best_priority = float("-inf")
        mro = t.__mro__
        for base, adapters in CollectionRegistry._REGISTRY.items():
            if not issubclass(t, base):
                continue
            try:
                distance = mro.index(base)
            except ValueError:
                continue
            if distance < best_distance:
                best_distance = distance
                best_adapter = _best(adapters)
                best_priority = getattr(best_adapter, "__adapter_priority__", 0)
            elif distance == best_distance:
                adapter = _best(adapters)
                priority = getattr(adapter, "__adapter_priority__", 0)
                if priority > best_priority:
                    best_adapter, best_priority = adapter, priority
        return best_adapter


### ABSTRACT COLLECTION ####################################

C = TypeVar("C", bound="AbstractCollection")


class AbstractCollection(Collection[T], Generic[T], ABC):
    """
    An abstract base class for collections (Java-like).

    Provides default conversions and helpers. Concrete collections implement `__iter__` and `__len__`.
    Mutating operations are optional (raise `TypeError` by default).
    """

    ### COLLECTION #########################################

    @abstractmethod
    def __len__(self) -> int:
        """Returns the number of elements contained in this collection."""
        raise NotImplementedError

    def is_empty(self) -> bool:
        """
        Returns whether this collection contains no elements.

        Complexity:
            O(1).
        """
        return len(self) == 0

    def __bool__(self) -> bool:
        """
        Returns whether this collection contains at least one element.

        Complexity:
            O(1).
        """
        return not self.is_empty()

    ### CONTAINER ##########################################

    def __contains__(self, key: object) -> bool:
        """
        Returns whether the specified element is contained in this collection.

        Complexity:
            O(n) by linear scan (base default). Subclasses may provide faster membership.
        """
        # Short-circuit linear membership by default
        return any(e == key for e in self)

    ### ITERABLE ###########################################

    @abstractmethod
    def __iter__(self) -> Iterator[T]:
        """Returns an `Iterator` over the elements of this collection."""
        raise NotImplementedError

    ### OPTIONAL MUTATORS (IMMUTABLE BY DEFAULT) ###########

    def add(self, v: T) -> None:
        """Adds the specified element to this collection."""
        raise TypeError("Collection is immutable")

    def discard(self, v: T) -> None:
        """Discards the specified element from this collection if present."""
        raise TypeError("Collection is immutable")

    def update(self, *iterables: Iterable[T]) -> None:
        """Updates this collection with one or more specified `Iterable`."""
        raise TypeError("Collection is immutable")

    ### CONVERTERS #########################################

    @classmethod
    def from_iterable(cls: Type[C], iterable: Iterable[T]) -> C:
        """
        Returns an instance of this collection type built from the specified `Iterable`.

        Resolution order (base implementation):
            1) If the `Iterable` is already an instance of cls, returns it (idempotent fast-path).
            2) Try `cls(iterable)` if the constructor accepts an `Iterable`.
            3) Try `cls(list(iterable))` as a final fallback.

        Complexity:
            Let `n = len(iterable)` if known; else number of items consumed.
            • Path (1): O(1).
            • Path (2): typically O(n).
            • Path (3): O(n) to build a `list` + O(n) to construct.
            Overall (typical): O(n).

        Notes:
            • Concrete subclasses should override to construct efficiently and preserve invariants.
            • Only constructor-shape errors (`TypeError`) trigger fallbacks; other exceptions propagate.
            • Single-pass iterables are duplicated to avoid partial consumption across attempts.
        """
        # 1) Idempotent fast-path: if already an instance of this class, return as-is
        if isinstance(iterable, cls):
            return cast(C, iterable)

        # Duplicate the single-pass `Iterable` so the fallback sees the full stream
        it1, it2 = create_safe_iterables(iterable, 2)

        # 2) Try the constructor directly with the `Iterable`
        try:
            return cls(it1)
        except TypeError:
            pass  # likely a signature/type mismatch; try the final fallback

        # 3) Fallback: materialize once and pass a `list`
        try:
            return cls(list(it2))
        except TypeError as e:
            raise TypeError(
                f"'{cls.__name__}' must implement 'from_iterable' or accept an 'Iterable'/'list' in its constructor; "
                f"got '{type(iterable).__name__}'"
            ) from e

    ##########################

    def to_iterable(self) -> Iterable[T]:
        """
        Returns an `Iterable` view of this collection.

        Complexity:
            O(1) to obtain; iteration O(n).
        """
        return self  # instances are iterable by contract

    def to_array(self, element_type: Optional[ElementType] = None) -> np.ndarray:
        """
        Returns an `array` built from this collection.

        Complexity:
            O(n).
        """
        return np.array(self, dtype=element_type)

    def to_list(self) -> List[T]:
        """
        Returns a `list` built from this collection.

        Complexity:
            O(n).
        """
        return list(self)

    def to_set(self) -> Set[T]:
        """
        Returns a `set` built from this collection.

        Complexity:
            O(n) average.
        """
        return set(self)

    def to_tuple(self) -> Tuple[T, ...]:
        """
        Returns a `tuple` built from this collection.

        Complexity:
            O(n).
        """
        return tuple(self)

    ### REPRESENTATION #####################################

    REPR_OPEN: str = "["  # left enclosure used by `__repr__`
    REPR_CLOSE: str = "]"  # right enclosure used by `__repr__`
    STR_OPEN: str = "{"  # left enclosure used by `__str__`
    STR_CLOSE: str = "}"  # right enclosure used by `__str__`
    DELIM: str = ", "  # delimiter between elements
    REPR_MAX: int = 100  # maximum elements shown before adding an ellipsis
    ELLIPSIS: str = "…"  # ellipsis marker used by `__repr__`/`__str__`

    def __repr__(self) -> str:
        """Returns the canonical string representation of this collection."""
        cls = type(self).__name__
        it = create_iterator(self)
        parts = [repr(e) for e in itertools.islice(it, self.REPR_MAX)]
        # Consume one extra element to decide whether to append an ellipsis
        if next(it, None) is not None:
            parts.append(self.ELLIPSIS)
        body = self.DELIM.join(parts)
        return f"{cls}({self.REPR_OPEN}{body}{self.REPR_CLOSE})"

    def __str__(self) -> str:
        """Returns the user-friendly string representation of this collection."""
        it = create_iterator(self)
        parts = [repr(e) for e in itertools.islice(it, self.REPR_MAX)]
        if next(it, None) is not None:
            parts.append(self.ELLIPSIS)
        body = self.DELIM.join(parts)
        return f"{self.STR_OPEN}{body}{self.STR_CLOSE}"


@adapts(AbstractCollection, priority=1)
class AbstractCollectionAdapter(CollectionAdapter[T]):
    """An adapter that delegates to an `AbstractCollection` implementation."""

    ### ITERABLE ###########################################

    def to_iterable(self, x: AbstractCollection[T]) -> Iterable[T]:
        """
        Returns an `Iterable` view of the specified collection.

        Complexity:
            O(1) to obtain; iteration O(n).
        """
        return x.to_iterable()

    ### OPTIONAL MUTATORS (IMMUTABLE BY DEFAULT) ###########

    def add(self, x: AbstractCollection[T], v: T) -> None:
        """Adds the specified element to the specified collection."""
        x.add(v)

    def discard(self, x: AbstractCollection[T], v: T) -> None:
        """Discards the specified element from the specified collection if present."""
        x.discard(v)

    def update(self, x: AbstractCollection[T], *iterables: Iterable[T]) -> None:
        """Updates the specified collection with one or more specified `Iterable`."""
        x.update(*iterables)


##############################


class AbstractSequentialCollection(AbstractCollection[T], Sequence[T], ABC):
    """
    An abstract base class for sequential collections.

    Adds efficient defaults for indexed access and reversed iteration while keeping Sequence[T]
    semantics. Concrete subclasses still implement `__iter__` and `__len__`.
    """

    ### SEQUENCE ###########################################

    @overload
    def __getitem__(self, index: int) -> T: ...

    @overload
    def __getitem__(self, index: slice) -> List[T]: ...

    def __getitem__(self, index):
        """
        Returns the element(s) at the specified position or slice.

        • If `index` is an integer, returns the element at that position.
        • If `index` is a slice, returns a new `list` containing the elements in the specified range.

        Complexity:
            • Integer index `i`: O(i + 1) via iterator skipping (base default).
            • Slice unit step (`step == 1`), length `k`: O(k).
            • General slice: O(k * T_get) where `T_get` is cost of `self[i]` (base `self[i]` is O(i)).
              ⇒ In this base class, non-unit-step slicing can degrade toward O(k^2).
            Subclasses with O(1) indexing should override for O(k).
        """
        if isinstance(index, slice):
            # Fast path for unit-step slices without full materialization
            start, stop, step = index.indices(len(self))
            if step == 1:
                if stop <= start:
                    return []
                return list(itertools.islice(self, start, stop))
            return [self[i] for i in range(start, stop, step)]

        try:
            index = operator.index(index)  # accepts int-like types (e.g., numpy.int64)
        except TypeError as e:
            raise TypeError(f"Collection indices must be integers or slices, not '{type(index).__name__}'") from e

        n = len(self)
        if index < 0:
            index += n
        if index < 0 or index >= n:
            raise IndexError("Collection index out of range")
        return next(itertools.islice(self, index, index + 1))

    def __reversed__(self) -> Iterator[T]:
        """
        Returns a reversed `Iterator` over the elements of this collection.

        Complexity:
            O(n) time, O(n) memory (materializes a `list` for predictable reverse).
        """
        return reversed(self.to_list())  # predictable


@adapts(AbstractSequentialCollection, priority=2)
class AbstractSequentialCollectionAdapter(AbstractCollectionAdapter[T]):
    """
    An adapter that delegates to an `AbstractSequentialCollection` implementation with
    sequential-aware helpers.

    Preserves the `AbstractCollectionAdapter` contract and adds efficient defaults that leverage
    `__getitem__` (index/slice) and `__reversed__` provided by the specified collection.
    """

    ### SEQUENCE ###########################################

    def get_at(self, x: AbstractSequentialCollection[T], index: int) -> T:
        """
        Returns the element at the specified index (supports negative indices).

        Complexity:
            Follows `x.__getitem__`; base default O(index + 1).
        """
        return x[index]

    def get_slice(self, x: AbstractSequentialCollection[T], s: slice) -> List[T]:
        """
        Returns a `list` containing the elements in the specified `slice`.

        Complexity:
            Follows `x.__getitem__` slice rules; base default O(k) for unit-step, else up to O(k^2).
        """
        return x[s]

    def to_reversed_iterable(self, x: AbstractSequentialCollection[T]) -> Iterable[T]:
        """
        Returns a reversed `Iterable` view of the specified collection.

        Complexity:
            O(n) time, O(n) memory (base default).
        """
        return reversed(x)


##############################

D = TypeVar("D")
K = TypeVar("K")
V = TypeVar("V", covariant=True)
CMap = TypeVar("CMap", bound="AbstractMappingCollection")


class AbstractMappingCollection(AbstractCollection[K], Mapping[K, V], Generic[K, V], ABC):
    """
    An abstract base class for mapping collections.

    • Iteration yields keys (Mapping contract).
    • Membership tests are key-based.
    • Converters (`to_list`, `to_set`, `to_tuple`) return values by default.
    • Mutators are optional and raise `TypeError` by default (immutable-by-default policy).
    """

    ### CONTAINER ##########################################

    def __contains__(self, key: object) -> bool:
        """
        Returns whether the specified key is contained in this mapping collection.

        Complexity:
            Average O(1) for hash-based mappings.
        """
        # Mapping membership is defined on keys
        return MAPPING_TYPE.__contains__(self, key)

    ### MAPPING ############################################

    @abstractmethod
    def __getitem__(self, key: K) -> V:
        """Returns the value associated with the specified key."""
        raise NotImplementedError

    @overload
    def get(self, key: K) -> Optional[V]: ...

    @overload
    def get(self, key: K, default: D) -> Union[D, V]: ...

    def get(self, key: K, default: Optional[D] = None) -> Optional[Union[D, V]]:
        """
        Returns the value associated with the specified key, or the specified default.

        Complexity:
            Average O(1) on hash-based mappings.
        """
        return MAPPING_TYPE.get(self, key, default)  # uses Mapping implementation

    ### OPTIONAL MUTATORS (IMMUTABLE BY DEFAULT) ###########

    def put(self, key: K, value: V) -> None:
        """
        Associates the specified value with the specified key in this mapping collection,
        replacing any existing entry.
        """
        raise TypeError("Mapping collection is immutable")

    def discard_key(self, key: K) -> None:
        """
        Discards the specified key and its associated value from this mapping collection if present.
        """
        raise TypeError("Mapping collection is immutable")

    def update_pairs(self, pairs: Iterable[Tuple[K, V]]) -> None:
        """Updates this mapping collection with the specified key-value pairs."""
        raise TypeError("Mapping collection is immutable")

    ### CONVERTERS (VALUES BY DEFAULT) #####################

    @classmethod
    def from_iterable(cls: Type[CMap], iterable: Union[Iterable[Tuple[K, V]], Mapping[K, V]]) -> CMap:
        """
        Returns an instance of this mapping collection type built from the specified `Iterable` of
        pairs or from the specified mapping. Class-level converter.

        Resolution order (base implementation):
            1) If the `Iterable` is already an instance of cls, returns it (idempotent fast-path).
            2) Try `cls(iterable)` directly.
            3) If that fails, try `cls(dict(iterable))` (accepts (key, value) pairs).
            4) Otherwise, raises a `TypeError`.

        Complexity:
            Let `n = number of pairs / mapping size`.
            • Path (1): O(1).
            • Path (2): typically O(n).
            • Path (3): O(n) to build a `dict` + O(n) to construct.
            Overall (typical): O(n).

        Notes:
            • Concrete subclasses should override to construct efficiently and preserve invariants.
            • Only constructor-shape errors (TypeError) trigger fallbacks; other exceptions propagate.
        """
        # 1) Idempotent fast-path: if already an instance of this class, return as-is
        if isinstance(iterable, cls):
            return cast(CMap, iterable)

        # 2) Try the constructor directly with the `Iterable`
        try:
            return cls(iterable)
        except TypeError:
            pass  # likely a signature/type mismatch; try the final fallback

        # 3) Fallback: materialize once and pass a `dict`
        try:
            return cls(dict(iterable))
        except (TypeError, ValueError) as e:
            raise TypeError(
                f"'{cls.__name__}' must implement 'from_iterable' or accept Mapping/pairs in its constructor; "
                f"got '{type(iterable).__name__}'"
            ) from e

    ##########################

    def to_array(self, element_type: Optional[ElementType] = None) -> np.ndarray:
        """
        Returns an `array` built from the values of this mapping collection.

        Complexity:
            O(n).
        """
        return np.array(self.values(), dtype=element_type)

    def to_list(self) -> List[V]:
        """
        Returns a `list` built from the values of this mapping collection.

        Complexity:
            O(n).
        """
        return list(self.values())

    def to_set(self) -> Set[V]:
        """
        Returns a `set` built from the values of this mapping collection.

        Complexity:
            O(n) average.
        """
        return set(self.values())

    def to_tuple(self) -> Tuple[V, ...]:
        """
        Returns a `tuple` built from the values of this mapping collection.

        Complexity:
            O(n).
        """
        return tuple(self.values())

    ### REPRESENTATION #####################################

    REPR_OPEN: str = "{"  # left enclosure used by `__repr__`
    REPR_CLOSE: str = "}"  # right enclosure used by `__repr__`

    def __repr__(self) -> str:
        """Returns the canonical string representation of this mapping collection."""
        cls = type(self).__name__
        it = create_iterator(self.items())
        parts = [f"{repr(k)}: {repr(v)}" for k, v in itertools.islice(it, self.REPR_MAX)]
        # Consume one extra item to decide whether to append an ellipsis
        if next(it, None) is not None:
            parts.append(self.ELLIPSIS)
        body = self.DELIM.join(parts)
        return f"{cls}({self.REPR_OPEN}{body}{self.REPR_CLOSE})"

    def __str__(self) -> str:
        """Returns the user-friendly string representation of this mapping collection."""
        it = create_iterator(self.items())
        parts = [f"{repr(k)}: {repr(v)}" for k, v in itertools.islice(it, self.REPR_MAX)]
        # Consume one extra item to decide whether to append an ellipsis
        if next(it, None) is not None:
            parts.append(self.ELLIPSIS)
        body = self.DELIM.join(parts)
        return f"{self.STR_OPEN}{body}{self.STR_CLOSE}"


@adapts(AbstractMappingCollection, priority=3)
class AbstractMappingCollectionAdapter(AbstractCollectionAdapter[K], Generic[K, V]):
    """
    An adapter that delegates to an `AbstractMappingCollection` implementation with mapping-aware
    helpers.

    Preserves the `AbstractCollectionAdapter` contract and provides convenient access to keys,
    values, and items.
    """

    ### MAPPING ############################################

    @overload
    def get(self, x: AbstractMappingCollection[K, V], key: K) -> Optional[V]: ...

    @overload
    def get(self, x: AbstractMappingCollection[K, V], key: K, default: D) -> Union[D, V]: ...

    def get(self, x: AbstractMappingCollection[K, V], key: K, default: Optional[D] = None) -> Optional[Union[D, V]]:
        """
        Returns the value associated with the specified key, or the specified default.

        Complexity:
            Average O(1) on hash-based mappings.
        """
        return x.get(key, default)

    ##########################

    def keys(self, x: AbstractMappingCollection[K, V]) -> KeysView[K]:
        """
        Returns a keys view of the specified mapping collection.

        Complexity:
            O(1) to obtain; iteration O(n).
        """
        return x.keys()

    def values(self, x: AbstractMappingCollection[K, V]) -> ValuesView[V]:
        """
        Returns a values view of the specified mapping collection.

        Complexity:
            O(1) to obtain; iteration O(n).
        """
        return x.values()

    def items(self, x: AbstractMappingCollection[K, V]) -> ItemsView[K, V]:
        """
        Returns an items view of the specified mapping collection.

        Complexity:
            O(1) to obtain; iteration O(n).
        """
        return x.items()

    ### OPTIONAL MUTATORS (IMMUTABLE BY DEFAULT) ###########

    def put(self, x: AbstractMappingCollection[K, V], key: K, value: V) -> None:
        """
        Associates the specified value with the specified key in the specified mapping collection,
        replacing any existing entry.
        """
        x.put(key, value)

    def discard_key(self, x: AbstractMappingCollection[K, V], key: K) -> None:
        """
        Discards the specified key and its associated value from the specified mapping collection if
        present.
        """
        x.discard_key(key)

    def update_pairs(self, x: AbstractMappingCollection[K, V], pairs: Iterable[Tuple[K, V]]) -> None:
        """Updates the specified mapping collection with the specified key-value pairs."""
        x.update_pairs(pairs)


__COMMON_COLLECTION_REGISTRY_CONSTANTS____________________________________________________ = ""

ITERATOR_TYPE = ABCIterator

MAPPING_TYPE = ABCMapping
MUTABLE_MAPPING_TYPE = ABCMutableMapping


__COMMON_COLLECTION_REGISTRY_GENERATORS___________________________________________________ = ""


def create_iterator(x: Any, *, target_type: Union[Type[Any], str] = "object") -> Iterator[Any]:
    """
    Returns an `Iterator` over `x`.

    Complexity:
        Typically O(1) to obtain the iterator; traversal O(n).

    Raises:
        TypeError: If `x` is not iterable.
    """
    try:
        return iter(x)
    except TypeError as e:
        name = target_type if isinstance(target_type, str) else getattr(target_type, "__name__", str(target_type))
        raise TypeError(f"'{name}' expects an 'Iterable', not '{type(x).__name__}'") from e


def create_safe_iterables(iterable: Iterable[T], n: int = 2) -> Tuple[Iterable[T], ...]:
    """
    Returns `n` safe iterables; replicates only if the specified `Iterable` is single-pass.

    Notes:
        • Single-pass (`Iterator` is its own `Iterable`): returns `n` independent tees.
        • Re-iterable: returns the same iterable reference repeated `n` times.

    Complexity:
        • Creation: O(1).
        • Consumption:
            – Re-iterable: inherent O(n) as consumed.
            – `tee`: amortized O(n) total across tees (lazy buffering).

    Raises:
        ValueError: If `n` is less than 1.
    """
    if n < 1:
        raise ValueError("'n' must be >= 1")

    it = create_iterator(iterable)
    # Single-pass check by identity (if `create_iterator(x) is x`, then `x` is its own `Iterator`)
    if it is iterable:
        return itertools.tee(it, n)  # produces `n` independent `Iterator` (to avoid consuming `it`)
    return (iterable,) * n  # reuses the same re-`Iterable` reference `n` times


__COMMON_COLLECTION_REGISTRY_VALIDATORS___________________________________________________ = ""


def is_iterator(x: Any) -> bool:
    """Returns whether `x` is an `Iterator`."""
    return isinstance(x, ITERATOR_TYPE)


def is_iterator_type(t: Type[Any]) -> bool:
    """Returns whether `t` is an `Iterator` type."""
    return issubclass(t, ITERATOR_TYPE)


def is_single_pass_iterator(x: Any) -> bool:
    """Returns whether `x` is a single-pass `Iterator`."""
    try:
        return create_iterator(x) is x
    except TypeError:
        return False


def is_mapping(x: Any) -> bool:
    """Returns whether `x` is a `Mapping` (including `dict`)."""
    return isinstance(x, MAPPING_TYPE)


def is_mapping_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `Mapping` type (including `dict`)."""
    return issubclass(t, MAPPING_TYPE)


def is_mutable_mapping(x: Any) -> bool:
    """Returns whether `x` is a `MutableMapping` (including `dict`)."""
    return isinstance(x, MUTABLE_MAPPING_TYPE)


def is_mutable_mapping_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `MutableMapping` type (including `dict`)."""
    return issubclass(t, MUTABLE_MAPPING_TYPE)


##############################


def is_abstract_collection(x: Any) -> bool:
    """Returns whether `x` is an `AbstractCollection`."""
    return isinstance(x, AbstractCollection)


def is_abstract_collection_type(t: Type[Any]) -> bool:
    """Returns whether `t` is an `AbstractCollection` type."""
    return issubclass(t, AbstractCollection)


def is_abstract_sequential_collection(x: Any) -> bool:
    """Returns whether `x` is an `AbstractSequentialCollection`."""
    return isinstance(x, AbstractSequentialCollection)


def is_abstract_sequential_collection_type(t: Type[Any]) -> bool:
    """Returns whether `t` is an `AbstractSequentialCollection` type."""
    return issubclass(t, AbstractSequentialCollection)


def is_abstract_mapping_collection(x: Any) -> bool:
    """Returns whether `x` is an `AbstractMappingCollection`."""
    return isinstance(x, AbstractMappingCollection)


def is_abstract_mapping_collection_type(t: Type[Any]) -> bool:
    """Returns whether `t` is an `AbstractMappingCollection` type."""
    return issubclass(t, AbstractMappingCollection)


############################################################


def has_callable(x: Any, attribute: str) -> bool:
    """
    Returns whether `x` has a callable `attribute` (function or method).

    Example:
        if has_callable(df, "to_dict"):
            return df.to_dict()
    """
    return callable(getattr(x, attribute, None))
