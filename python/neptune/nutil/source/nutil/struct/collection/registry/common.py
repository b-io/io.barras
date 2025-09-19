from __future__ import annotations

import itertools
import operator
from abc import ABC, abstractmethod
from collections.abc import (
    Collection,
    Iterable,
    Iterator,
)
from threading import RLock
from typing import (
    Any,
    cast,
    ClassVar,
    Dict,
    Generic,
    ItemsView,
    KeysView,
    List,
    Mapping,
    Optional,
    overload,
    Sequence,
    Set,
    Tuple,
    Type,
    TypeVar,
    Union,
    ValuesView,
)

from nutil.annotations import classproperty
from nutil.metaclasses import FinalSingletonMeta

####################################################################################################
# COMMON COLLECTION REGISTRY ANNOTATIONS
####################################################################################################

__COMMON_COLLECTION_REGISTRY_ANNOTATIONS__________ = ""


def adapts(*target_types: Type[Any], priority: int = 0, override: bool = False):
    """
    Class decorator that:
    • Sets class-level metadata:
      - `__adapts__`: a tuple of all supported target types.
      - `__adapter_priority__`: the adapter’s priority value.
    • Sets instance-level metadata for each registered adapter:
      - `__adapts__`: the single bound target type for that instance.
      - `__adapter_priority__`: the same priority value.
    • Creates and registers one adapter instance per target type with the `CollectionRegistry`.
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


####################################################################################################
# COMMON COLLECTION REGISTRY CLASSES
####################################################################################################

__COMMON_COLLECTION_REGISTRY_CLASSES______________ = ""

T = TypeVar("T")


class CollectionAdapter(Generic[T], ABC):
    """
    Defines the adapter contract for external collection types.

    Minimal hooks mirror AbstractCollection semantics. Override for O(1) where possible.
    The `@adapts(...)` decorator sets class-level `__adapts__` (tuple of supported targets) and
    binds instance-level `__adapts__` (the single bound target), plus `__adapter_priority__`.
    """

    # Instance attributes only; class attributes are set on the class object by `@adapts(...)`
    __slots__ = ("__adapts__", "__adapter_priority__")

    # Injected by `@adapts(...)` at runtime
    __adapts__: Type[Any]  # bound target type (instance-level)
    __adapter_priority__: int  # instance priority metadata

    ##############################################
    # COLLECTION
    ##############################################

    def size(self, x: Any) -> int:
        """
        Returns the number of elements contained in the specified collection.
        Uses `len(x)` if available (O(1)), otherwise falls back to iteration (O(n)).
        """
        try:
            return len(x)
        except (AttributeError, NotImplementedError, TypeError):
            n = 0
            for _ in self.to_iterable(x):
                n += 1
            return n

    ##############################################
    # CONTAINER
    ##############################################

    def contains(self, x: Any, v: T) -> bool:
        """Returns True if the specified element is contained in the specified collection (O(n) by default)."""
        try:
            # Fast path if the underlying object implements `__contains__`
            return v in x
        except (AttributeError, NotImplementedError, TypeError):
            return any(e == v for e in self.to_iterable(x))

    ##############################################
    # ITERABLE
    ##############################################

    @abstractmethod
    def to_iterable(self, x: Any) -> Iterable[T]:
        """Returns an iterable view of the specified collection.

        Raises:
            NotImplementedError: If the adapter does not implement this method.
        """

    ##############################################
    # VERIFIERS
    ##############################################

    def is_instance(self, x: Any) -> bool:
        """Returns True if the specified value is an instance of this adapter's target type."""
        return isinstance(x, self.target_type)

    ##############################################
    # ACCESSORS
    ##############################################

    @classproperty
    def target_types(cls) -> Tuple[Type[Any], ...]:
        """Returns the tuple of target types this adapter class supports."""
        return getattr(cls, "__adapts__", ())

    @property
    def target_type(self) -> Type[Any]:
        """Returns the bound target type for this adapter instance."""
        target_type = getattr(self, "__adapts__", None)

        # Instance must hold a single concrete type; otherwise it is unbound or class-level metadata
        if not isinstance(target_type, type):
            raise TypeError(
                "Adapter instance is not bound to a single target; "
                "ensure it was constructed via '@adapts(...)'"
            )
        return target_type

    ##############################################
    # CONVERTERS
    ##############################################

    def from_iterable(self, iterable: Iterable[T]) -> Any:
        """
        Returns a new instance of the adapter's target type constructed from the specified iterable.

        Resolution order:
        1) Uses `target_type.from_iterable(iterable)` if present (e.g., `AbstractCollection`).
        2) Tries `target_type(iterable)`.
        3) Tries `target_type(list(iterable))` as the final fallback.

        Only constructor-shape errors (`TypeError`) trigger fallbacks; other exceptions propagate.
        Single-pass iterables are duplicated to avoid partial consumption across attempts.
        """
        target_type = self.target_type

        # 1) Canonical hook for the target types implementing `from_iterable` (e.g., `AbstractCollection`)
        factory = getattr(target_type, "from_iterable", None)
        if callable(factory):
            return factory(iterable)

        # Validate iterability and detect single-pass iterator
        try:
            iterator = iter(iterable)
        except TypeError as e:
            raise TypeError(
                f"'{getattr(target_type, '__name__', str(target_type))}.from_iterable' expects an iterable, "
                f"not '{type(iterable).__name__}'"
            ) from e
        single_pass = iterator is iterable

        # Duplicate single-pass iterables so the fallback sees the full stream
        iterable_1 = iterable_2 = iterable
        if single_pass:
            iterable_1, iterable_2 = itertools.tee(iterator, 2)

        # 2) Try constructor accepting the iterable
        try:
            return target_type(iterable_1)
        except TypeError:
            # Likely signature/type mismatch; try the final fallback
            pass

        # 3) Fallback: materialize once and pass a list
        try:
            return target_type(list(iterable_2))
        except TypeError as e:
            raise TypeError(
                f"Cannot construct '{getattr(target_type, '__name__', str(target_type))}' from iterable; "
                f"provide 'from_iterable' or a constructor accepting an iterable/list"
            ) from e

    #####################

    def to_list(self, x: Any) -> List[T]:
        """Returns a list built from the specified collection."""
        return list(self.to_iterable(x))

    def to_set(self, x: Any) -> Set[T]:
        """Returns a set built from the specified collection."""
        return set(self.to_iterable(x))

    def to_tuple(self, x: Any) -> Tuple[T, ...]:
        """Returns a tuple built from the specified collection."""
        return tuple(self.to_iterable(x))

    ##############################################
    # OPTIONAL MUTATORS (IMMUTABLE BY DEFAULT)
    ##############################################

    def add(self, x: Any, v: T) -> None:
        """Adds the specified element to the specified collection."""
        raise TypeError("Collection is immutable")

    def discard(self, x: Any, v: T) -> None:
        """Discards the specified element from the specified collection."""
        raise TypeError("Collection is immutable")

    def update(self, x: Any, it: Iterable[T]) -> None:
        """Updates the specified collection with the specified iterable."""
        raise TypeError("Collection is immutable")


class CollectionRegistry(metaclass=FinalSingletonMeta):
    """
    Maintains a global, thread-safe registry of collection types mapped to their adapters,
    with deterministic nearest-match resolution.

    Resolution strategy:
    1) If an exact adapter is registered for the type, select the highest-priority one.
       • When multiple adapters share the same priority, the earliest registered is chosen.
    2) Otherwise, inspect the method resolution order (MRO) of the type:
       • Among all registered base types that appear in the MRO, pick the base with the
         smallest index (i.e., the nearest ancestor).
       • Within that base’s adapters, prefer the highest-priority adapter.
       • If multiple adapters share the same priority, the earliest registered is chosen.
    """

    _REGISTRY: ClassVar[Dict[Type[Any], List[CollectionAdapter[Any]]]] = {}
    _LOCK: ClassVar[RLock] = RLock()

    def exists(self, x: Any) -> bool:
        """Returns True if there is a registered adapter for the specified object or its bases."""
        return self.get(x) is not None

    def get(self, x_or_t: Any) -> Optional[CollectionAdapter[Any]]:
        """Returns the best adapter for the instance or type, or None if unregistered."""
        t = x_or_t if isinstance(x_or_t, type) else type(x_or_t)
        with self._LOCK:

            def _get_best_adapter(adapters: List[CollectionAdapter[Any]]) -> CollectionAdapter[Any]:
                """Returns the adapter with the highest priority; if priorities tie, the earliest registered is chosen."""
                return max(adapters, key=lambda a: getattr(a, "__adapter_priority__", 0))

            # 1) Exact adapters
            exact_adapters = self._REGISTRY.get(t)
            if exact_adapters:
                return _get_best_adapter(exact_adapters)

            # 2) MRO-nearest adapters among registered bases
            best_adapter: Optional[CollectionAdapter[Any]] = None
            best_adapter_distance = float("inf")
            best_adapter_priority = float("-inf")
            mro = t.__mro__

            for base, adapters in self._REGISTRY.items():
                if not issubclass(t, base):
                    continue
                # Find the MRO distance; if the base is not in MRO (ABC unrelated), skip
                try:
                    adapter_distance = mro.index(base)
                except ValueError:
                    continue

                if adapter_distance < best_adapter_distance:
                    best_adapter_distance = adapter_distance
                    best_adapter = _get_best_adapter(adapters)
                    best_adapter_priority = getattr(best_adapter, "__adapter_priority__", 0)
                elif adapter_distance == best_adapter_distance:
                    adapter = _get_best_adapter(adapters)
                    adapter_priority = getattr(adapter, "__adapter_priority__", 0)
                    if adapter_priority > best_adapter_priority:
                        best_adapter, best_adapter_priority = adapter, adapter_priority
                    elif adapter_priority == best_adapter_priority:
                        # Same distance and same priority: keep earliest (do nothing)
                        pass

            return best_adapter

    def register(
        self, t: Type[Any], adapter: CollectionAdapter[Any], *, override: bool = False
    ) -> None:
        """Registers the specified collection type with the specified adapter."""
        if not isinstance(adapter, CollectionAdapter):
            raise TypeError("Adapter must be an instance of 'CollectionAdapter' (not the class)")
        with self._LOCK:
            adapters = self._REGISTRY.setdefault(t, [])
            if override:
                # Replace all adapters for this exact type
                self._REGISTRY[t] = [adapter]
            else:
                adapters.append(adapter)


##################################################

C = TypeVar("C", bound="AbstractCollection")


class AbstractCollection(Collection[T], Generic[T], ABC):
    """
    Serves as the abstract base class for all collections (Java-like).

    Provides default conversions and helpers. Concrete collections implement `__iter__` and `__len__`.
    Mutating operations are optional (raise `TypeError` by default).
    """

    ##############################################
    # COLLECTION
    ##############################################

    @abstractmethod
    def __len__(self) -> int:
        """Returns the number of elements contained in the specified collection."""
        raise NotImplementedError

    def is_empty(self) -> bool:
        """Returns True if the specified collection contains no elements."""
        return len(self) == 0

    def __bool__(self) -> bool:
        """Returns True if the specified collection contains at least one element."""
        return not self.is_empty()

    ##############################################
    # CONTAINER
    ##############################################

    def __contains__(self, x: object) -> bool:
        """Returns True if the specified element is contained in the collection."""
        # Short-circuit linear membership by default
        return any(e == x for e in self)

    ##############################################
    # ITERABLE
    ##############################################

    @abstractmethod
    def __iter__(self) -> Iterator[T]:
        """Returns an iterator over the specified collection elements."""
        raise NotImplementedError

    ##############################################
    # OPTIONAL MUTATORS (IMMUTABLE BY DEFAULT)
    ##############################################

    def add(self, v: T) -> None:
        """Adds the specified element to the collection."""
        raise TypeError("Collection is immutable")

    def discard(self, v: T) -> None:
        """Discards the specified element from the collection if present."""
        raise TypeError("Collection is immutable")

    def update(self, it: Iterable[T]) -> None:
        """Updates the specified collection with the specified iterable of elements."""
        raise TypeError("Collection is immutable")

    ##############################################
    # CONVERTERS
    ##############################################

    @classmethod
    def from_iterable(cls: Type[C], iterable: Iterable[T]) -> C:
        """
        Returns an instance of the specified collection type built from the specified iterable.

        Resolution order (base implementation):
        1) If the iterable is already an instance of cls, returns it (idempotent fast-path).
        2) Try `cls(iterable)` if the constructor accepts an `Iterable`.
        3) Try `cls(list(iterable))` as a final fallback.

        Notes:
        • Concrete subclasses should override to construct efficiently and preserve invariants.
        • Only constructor-shape errors (`TypeError`) trigger fallbacks; other exceptions propagate.
        • Single-pass iterables are duplicated to avoid partial consumption across attempts.
        """
        # 1) Idempotent fast-path: if already an instance of this class, return as-is
        if isinstance(iterable, cls):
            return cast(C, iterable)

        # Validate iterability and detect single-pass (vs. re-iterable)
        try:
            iterator = iter(iterable)
        except TypeError as e:
            raise TypeError(
                f"'{cls.__name__}.from_iterable' expects an iterable, not '{type(iterable).__name__}'"
            ) from e
        single_pass = iterator is iterable

        # Duplicate single-pass iterables so the fallback sees the full stream
        iterable_1 = iterable_2 = iterable
        if single_pass:
            iterable_1, iterable_2 = itertools.tee(iterator, 2)

        # 2) Try the constructor directly with the iterable
        try:
            return cls(iterable_1)
        except TypeError:
            # Likely signature/type mismatch; try the final fallback
            pass

        # 3) Fallback: materialize once and pass a list
        try:
            return cls(list(iterable_2))
        except TypeError as e:
            raise TypeError(
                f"'{cls.__name__}' must implement 'from_iterable' or accept an iterable/list in its constructor; "
                f"got '{type(iterable).__name__}'"
            ) from e

    #####################

    def to_iterable(self) -> Iterable[T]:
        """Returns an iterable view of the specified collection."""
        return self  # instances are iterable by contract

    def to_list(self) -> List[T]:
        """Returns a list built from the specified collection."""
        return list(self)

    def to_set(self) -> Set[T]:
        """Returns a set built from the specified collection."""
        return set(self)

    def to_tuple(self) -> Tuple[T, ...]:
        """Returns a tuple built from the specified collection."""
        return tuple(self)

    ##############################################
    # REPRESENTATION
    ##############################################

    REPR_OPEN: str = "["  # left enclosure used by `__repr__`
    REPR_CLOSE: str = "]"  # right enclosure used by `__repr__`
    STR_OPEN: str = "{"  # left enclosure used by `__str__`
    STR_CLOSE: str = "}"  # right enclosure used by `__str__`
    DELIM: str = ", "  # delimiter between elements
    REPR_MAX: int = 100  # maximum elements shown before adding an ellipsis
    ELLIPSIS: str = "…"  # ellipsis marker used by `__repr__`/`__str__`

    def __repr__(self) -> str:
        """Returns the canonical string representation of the specified collection."""
        cls = type(self).__name__
        iterator = iter(self)
        parts = [repr(e) for e in itertools.islice(iterator, self.REPR_MAX)]
        # Consume one extra element to decide whether to append an ellipsis
        if next(iterator, None) is not None:
            parts.append(self.ELLIPSIS)
        body = self.DELIM.join(parts)
        return f"{cls}({self.REPR_OPEN}{body}{self.REPR_CLOSE})"

    def __str__(self) -> str:
        """Returns the user-friendly string representation of the specified collection."""
        iterator = iter(self)
        parts = [repr(e) for e in itertools.islice(iterator, self.REPR_MAX)]
        if next(iterator, None) is not None:
            parts.append(self.ELLIPSIS)
        body = self.DELIM.join(parts)
        return f"{self.STR_OPEN}{body}{self.STR_CLOSE}"


@adapts(AbstractCollection, priority=1)
class AbstractCollectionAdapter(CollectionAdapter[T]):
    """Delegates to an `AbstractCollection` implementation."""

    ##############################################
    # COLLECTION
    ##############################################

    def size(self, x: AbstractCollection[T]) -> int:
        """Returns the number of elements contained in the specified collection (O(1) if `__len__` else O(n))."""
        return len(x)

    ##############################################
    # CONTAINER
    ##############################################

    def contains(self, x: AbstractCollection[T], v: T) -> bool:
        """Returns True if the specified element is contained in the specified collection (O(n) by default)."""
        return v in x

    ##############################################
    # ITERABLE
    ##############################################

    def to_iterable(self, x: AbstractCollection[T]) -> Iterable[T]:
        """Returns an iterable view of the specified collection."""
        return x.to_iterable()

    ##############################################
    # OPTIONAL MUTATORS (IMMUTABLE BY DEFAULT)
    ##############################################

    def add(self, x: AbstractCollection[T], v: T) -> None:
        """Adds the specified element to the specified collection."""
        x.add(v)

    def discard(self, x: AbstractCollection[T], v: T) -> None:
        """Discards the specified element from the specified collection."""
        x.discard(v)

    def update(self, x: AbstractCollection[T], it: Iterable[T]) -> None:
        """Updates the specified collection with the specified iterable."""
        x.update(it)


#########################


class AbstractSequentialCollection(AbstractCollection[T], Sequence[T], ABC):
    """
    Serves as the abstract base class for sequential collections.

    Adds efficient defaults for indexed access and reversed iteration while keeping Sequence[T]
    semantics. Concrete subclasses still implement `__iter__` and `__len__`.
    """

    ##############################################
    # SEQUENCE
    ##############################################

    @overload
    def __getitem__(self, index: int) -> T: ...

    @overload
    def __getitem__(self, index: slice) -> List[T]: ...

    def __getitem__(self, index):
        """
        Returns the element(s) at the specified position or slice.

        • If `index` is an integer, returns the element at that position.
        • If `index` is a slice, returns a new list containing the elements in the specified range.
        """
        if isinstance(index, slice):
            # Fast path for unit-step slices without full materialization
            if index.step is None or index.step == 1:
                start, stop, _ = index.indices(len(self))
                if stop <= start:
                    return []
                # Extract exactly the required window
                return list(itertools.islice(self, start, stop))
            # General slice path (handles negative and non-unit steps)
            return self.to_list()[index]

        try:
            index = operator.index(index)  # accept int-like types (e.g., numpy.int64)
        except TypeError as e:
            raise TypeError(
                f"Collection indices must be integers or slices, not '{type(index).__name__}'"
            ) from e

        n = len(self)
        if index < 0:
            index += n
        if index < 0 or index >= n:
            raise IndexError("Collection index out of range")
        return next(itertools.islice(self, index, index + 1))

    def __reversed__(self) -> Iterator[T]:
        """Returns a reversed iterator over the specified collection elements."""
        return reversed(self.to_list())


@adapts(AbstractSequentialCollection, priority=2)
class AbstractSequentialCollectionAdapter(AbstractCollectionAdapter[T]):
    """
    Delegates to an `AbstractSequentialCollection` implementation with sequential-aware helpers.

    Preserves the `AbstractCollectionAdapter` contract and adds efficient defaults that leverage
    `__getitem__` (index/slice) and `__reversed__` provided by the specified collection.
    """

    ##############################################
    # SEQUENCE
    ##############################################

    def get_at(self, x: AbstractSequentialCollection[T], index: int) -> T:
        """Returns the element at the specified index (supports negative indices)."""
        return x[index]

    def get_slice(self, x: AbstractSequentialCollection[T], s: slice) -> List[T]:
        """
        Returns a list containing the elements in the specified slice.
        """
        return x[s]

    def to_reversed_iterable(self, x: AbstractSequentialCollection[T]) -> Iterable[T]:
        """Returns a reversed iterable view of the specified collection."""
        return reversed(x)


#########################

D = TypeVar("D")
K = TypeVar("K")
V = TypeVar("V", covariant=True)
CMap = TypeVar("CMap", bound="AbstractMappingCollection")


class AbstractMappingCollection(AbstractCollection[K], Mapping[K, V], Generic[K, V], ABC):
    """
    Serves as the abstract base class for mapping collections.

    • Iteration yields keys (Mapping contract).
    • Membership tests are key-based.
    • Converters (`to_list`, `to_set`, `to_tuple`) return values by default.
    • Mutators are optional and raise `TypeError` by default (immutable-by-default policy).
    """

    ##############################################
    # CONTAINER
    ##############################################

    def __contains__(self, x: object) -> bool:
        """Returns True if the specified key is contained in the specified mapping collection."""
        # Mapping membership is defined on keys
        return Mapping.__contains__(self, x)

    ##############################################
    # MAPPING
    ##############################################

    @abstractmethod
    def __getitem__(self, key: K) -> V:
        """Returns the value associated with the specified key."""
        raise NotImplementedError

    @overload
    def get(self, key: K) -> Optional[V]: ...

    @overload
    def get(self, key: K, default: D) -> Union[D, V]: ...

    def get(self, key: K, default: Optional[D] = None) -> Optional[Union[D, V]]:
        """Returns the value associated with the specified key, or the specified default."""
        return Mapping.get(self, key, default)  # uses Mapping implementation

    ##############################################
    # OPTIONAL MUTATORS (IMMUTABLE BY DEFAULT)
    ##############################################

    def put(self, key: K, value: V) -> None:
        """Associates the specified value with the specified key."""
        raise TypeError("Mapping collection is immutable")

    def discard_key(self, key: K) -> None:
        """Discards the specified key and its associated value if present."""
        raise TypeError("Mapping collection is immutable")

    def update_pairs(self, pairs: Iterable[Tuple[K, V]]) -> None:
        """Updates the specified mapping collection with the specified key-value pairs."""
        raise TypeError("Mapping collection is immutable")

    ##############################################
    # CONVERTERS (VALUES BY DEFAULT)
    ##############################################

    @classmethod
    def from_iterable(
        cls: Type[CMap], iterable: Union[Iterable[Tuple[K, V]], Mapping[K, V]]
    ) -> CMap:
        """
        Returns an instance of the specified mapping collection built from the specified iterable of pairs
        or from the specified mapping. Class-level converter.

        Resolution order (base implementation):
        1) If the iterable is already an instance of cls, returns it (idempotent fast-path).
        2) Try cls(iterable) directly.
        3) If that fails, try cls(dict(iterable)) (accepts (key, value) pairs).
        4) Otherwise, raises a `TypeError`.

        Notes:
        • Concrete subclasses should override to construct efficiently and preserve invariants.
        • Only constructor-shape errors (TypeError) trigger fallbacks; other exceptions propagate.
        """
        # 1) Idempotent fast-path
        if isinstance(iterable, cls):
            return cast(CMap, iterable)

        # 2) Direct constructor path
        try:
            return cls(iterable)
        except TypeError:
            pass

        # 3) Pairs → dict materialization
        try:
            return cls(dict(iterable))
        except (TypeError, ValueError) as e:
            raise TypeError(
                f"'{cls.__name__}' must implement 'from_iterable' or accept Mapping/pairs in its constructor; "
                f"got '{type(iterable).__name__}'"
            ) from e

    def to_list(self) -> List[V]:
        """Returns a list built from the specified mapping values."""
        return list(self.values())

    def to_set(self) -> Set[V]:
        """Returns a set built from the specified mapping values."""
        return set(self.values())

    def to_tuple(self) -> Tuple[V, ...]:
        """Returns a tuple built from the specified mapping values."""
        return tuple(self.values())

    ##############################################
    # REPRESENTATION
    ##############################################

    REPR_OPEN: str = "{"  # left enclosure used by `__repr__`
    REPR_CLOSE: str = "}"  # right enclosure used by `__repr__`

    def __repr__(self) -> str:
        """Returns the canonical string representation of the specified mapping collection."""
        cls = type(self).__name__
        iterator = iter(self.items())
        parts = [f"{repr(k)}: {repr(v)}" for k, v in itertools.islice(iterator, self.REPR_MAX)]
        # Consume one extra item to decide whether to append an ellipsis
        if next(iterator, None) is not None:
            parts.append(self.ELLIPSIS)
        body = self.DELIM.join(parts)
        return f"{cls}({self.REPR_OPEN}{body}{self.REPR_CLOSE})"

    def __str__(self) -> str:
        """Returns the user-friendly string representation of the specified mapping collection."""
        iterator = iter(self.items())
        parts = [f"{repr(k)}: {repr(v)}" for k, v in itertools.islice(iterator, self.REPR_MAX)]
        # Consume one extra item to decide whether to append an ellipsis
        if next(iterator, None) is not None:
            parts.append(self.ELLIPSIS)
        body = self.DELIM.join(parts)
        return f"{self.STR_OPEN}{body}{self.STR_CLOSE}"


@adapts(AbstractMappingCollection, priority=3)
class AbstractMappingCollectionAdapter(AbstractCollectionAdapter[K], Generic[K, V]):
    """
    Delegates to an `AbstractMappingCollection` implementation with mapping-aware helpers.

    Preserves the `AbstractCollectionAdapter` contract and provides convenient access to keys, values, and items.
    """

    ##############################################
    # MAPPING
    ##############################################

    @overload
    def get(self, x: AbstractMappingCollection[K, V], key: K) -> Optional[V]: ...

    @overload
    def get(self, x: AbstractMappingCollection[K, V], key: K, default: D) -> Union[D, V]: ...

    def get(
        self, x: AbstractMappingCollection[K, V], key: K, default: Optional[D] = None
    ) -> Optional[Union[D, V]]:
        """Returns the value associated with the specified key, or the specified default."""
        return x.get(key, default)

    #####################

    def keys(self, x: AbstractMappingCollection[K, V]) -> KeysView[K]:
        """Returns a keys view of the specified mapping."""
        return x.keys()

    def values(self, x: AbstractMappingCollection[K, V]) -> ValuesView[V]:
        """Returns a values view of the specified mapping."""
        return x.values()

    def items(self, x: AbstractMappingCollection[K, V]) -> ItemsView[K, V]:
        """Returns an items view of the specified mapping."""
        return x.items()

    ##############################################
    # OPTIONAL MUTATORS (IMMUTABLE BY DEFAULT)
    ##############################################

    def put(self, x: AbstractMappingCollection[K, V], key: K, value: V) -> None:
        """Associates the specified value with the specified key."""
        x.put(key, value)

    def discard_key(self, x: AbstractMappingCollection[K, V], key: K) -> None:
        """Discards the specified key and its associated value if present."""
        x.discard_key(key)

    def update_pairs(
        self, x: AbstractMappingCollection[K, V], pairs: Iterable[Tuple[K, V]]
    ) -> None:
        """Updates the specified mapping collection with the specified key-value pairs."""
        x.update_pairs(pairs)


####################################################################################################
# COMMON COLLECTION REGISTRY VERIFIERS
####################################################################################################

__COMMON_COLLECTION_REGISTRY_VERIFIERS____________ = ""


def is_abstract_collection(x: Any) -> bool:
    """Returns True if the specified object is an `AbstractCollection`."""
    return isinstance(x, AbstractCollection)


def is_abstract_sequential_collection(x: Any) -> bool:
    """Returns True if the specified object is an `AbstractSequentialCollection`."""
    return isinstance(x, AbstractSequentialCollection)


def is_abstract_mapping_collection(x: Any) -> bool:
    """Returns True if the specified object is an `AbstractMappingCollection`."""
    return isinstance(x, AbstractMappingCollection)
