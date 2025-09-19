import logging
from threading import RLock
from types import MappingProxyType
from typing import Any, cast, Dict, List, NoReturn, Optional, Tuple, Type, TypeVar

####################################################################################################
# METACLASSES
####################################################################################################

__METACLASSES_____________________________________ = ""

T = TypeVar("T")


def combine_metaclasses(*metas: Type[type]) -> type:
    """
    Returns a new metaclass that inherits from the specified metaclasses in the specified
    left-to-right order. If no metaclasses are provided, returns the built-in `type`.
    """
    if not metas:
        return type
    name = "".join(m.__name__ for m in metas) or "CombinedMeta"
    return type(name, metas, {})


# • NO PUBLIC CONSTRUCTOR ##########################################################################


class NoPublicConstructorMeta(type):
    """Ensures that the class cannot be instantiated directly (no public constructor)."""

    def __call__(cls: Type[T], *args: Any, **kwargs: Any) -> NoReturn:
        raise TypeError(f"'{cls.__module__}.{cls.__qualname__}' has no public constructor")


# • FINAL SINGLETON ################################################################################


class FinalSingletonMeta(type):
    """
    Ensures a final, per-subclass singleton instance with thread-safe initialization.

    Behavior
    --------
    • The first call to the subclass constructs the singleton instance using the specified
      constructor arguments.
    • Subsequent calls (with or without arguments) return the existing instance; any new
      constructor arguments are ignored (a debug message is logged).
    • Accessing instance attributes/methods on the class before initialization raises a
      `RuntimeError` with guidance.

    Attribute precedence (class access)
    -----------------------------------
    • Class attributes take precedence (Python’s normal rule).
    • If the attribute is not defined on the class, the lookup is forwarded to the singleton
      instance (once initialized).

    `__dict__` (merged, read-only)
    ----------------------------
    • Returns a read-only mapping that combines class and instance attributes once initialized:
      instance keys are included, but class keys take precedence on conflicts.
    • Note: this deviates from Python’s usual class `__dict__` (class-only).

    `__dir__` (discoverability)
    -------------------------
    • Returns the union of class and instance attribute names (once initialized) to improve
      interactive help and IDE autocompletion. Names only; no values or precedence.

    Writes (no forwarding)
    ----------------------
    • Setting attributes on the class writes to the class namespace; writes are not forwarded
      to the singleton instance.

    Error semantics
    ---------------
    • Uninitialized access to an instance-only attribute via the class raises RuntimeError.
    • Missing attributes on both class and instance raise `AttributeError` (standard Python behavior).
    """

    def __init__(cls: Type[T], name: str, bases: Tuple[type, ...], ns: Dict[str, Any]) -> None:
        super().__init__(name, bases, ns)
        cls._lock: RLock = RLock()
        cls._instance: Optional[T] = None

    def __call__(cls: Type[T], *args: Any, **kwargs: Any) -> T:
        """
        Returns the final singleton instance for the specified class.

        If no instance exists, creates it using the specified constructor arguments.
        If an instance already exists, returns it and ignores any provided arguments.
        """
        with cls._lock:
            if cls._instance is None:
                logging.debug(
                    f"Create the final singleton instance of '{cls.__module__}.{cls.__qualname__}'."
                )
                cls._instance = super(FinalSingletonMeta, cls).__call__(*args, **kwargs)
            elif args or kwargs:
                logging.debug(
                    f"Ignored the constructor arguments for the already-initialized final "
                    f"singleton '{cls.__module__}.{cls.__qualname__}'."
                )
            return cast(T, cls._instance)

    def __getattribute__(cls: Type[T], name: str) -> Any:
        """
        Returns a class attribute (preferred) or forwards to the instance after initialization.

        Special case:
        • `__dict__` returns a read-only merged view of class and instance dictionaries
          (if initialized), with class keys taking precedence on conflicts.
        """
        # Special-case `__dict__` to provide a merged, read-only view
        if name == "__dict__":
            class_dict = super().__getattribute__("__dict__")
            try:
                lock = super().__getattribute__("_lock")
            except AttributeError:
                # Fall back to the class dict during very early initialization
                return class_dict
            with lock:
                instance = super().__getattribute__("_instance")
                if instance is None:
                    return class_dict
                merged_dict: Dict[str, Any] = dict(getattr(instance, "__dict__", {}))
                # Class keys take precedence on conflicts (overlay class entries last), then freeze
                merged_dict.update(dict(class_dict))
                return MappingProxyType(merged_dict)

        # Keep dunders and management attributes as class attributes
        if name.startswith("__") and name.endswith("__"):
            return super().__getattribute__(name)
        if name in {"_lock", "_instance", "exists", "get", "mro"}:
            return super().__getattribute__(name)

        # 1) Class-first lookup
        try:
            return super().__getattribute__(name)
        except AttributeError:
            pass

        # 2) Forward to instance (if initialized)
        try:
            lock = super().__getattribute__("_lock")
        except AttributeError as e:
            # Early init: behave as not initialized yet
            raise RuntimeError(
                f"Final singleton '{cls.__module__}.{cls.__qualname__}' is not initialized; "
                f"call '{cls.__qualname__}(...)' first"
            ) from e
        with lock:
            instance = super().__getattribute__("_instance")
            if instance is None:
                raise RuntimeError(
                    f"Final singleton '{cls.__module__}.{cls.__qualname__}' is not initialized; "
                    f"call '{cls.__qualname__}(...)' first"
                )
            # Do not catch `AttributeError` here; missing attributes must surface normally
            return getattr(instance, name)

    def __dir__(cls: Type[T]) -> List[str]:
        """
        Returns the merged set of attribute names from the class and, if initialized, the final
        singleton instance. Includes names exposed by slots, properties, and dynamic attributes
        visible via dir(instance).
        """
        names = set(super().__dir__())
        with cls._lock:
            inst = cls._instance
            if inst is not None:
                # Include slots, properties, and dynamic attributes visible via dir()
                names.update(dir(inst))
        return sorted(names)

    ##############################################

    def exists(cls: Type[T]) -> bool:
        """Returns True if the final singleton instance exists."""
        with cls._lock:
            return cls._instance is not None

    def get(cls: Type[T]) -> T:
        """Returns the final singleton instance, or raises if not initialized."""
        with cls._lock:
            if cls._instance is None:
                raise RuntimeError(
                    f"Final singleton '{cls.__module__}.{cls.__qualname__}' is not initialized; "
                    f"call '{cls.__qualname__}(...)' first"
                )
            return cast(T, cls._instance)


# • OVERWRITEABLE SINGLETON ########################################################################


class SingletonMeta(type):
    """
    Ensures a singleton instance per subclass with thread-safe initialization and controlled
    overwrite via setters.

    Behavior
    --------
    • The first call to the subclass constructs the singleton instance using the specified
      constructor arguments.
    • Subsequent calls:
        – If new constructor arguments are specified, they are stored and the singleton is
          recreated with those arguments.
        – If no arguments are specified, the singleton is recreated using the last stored
          constructor arguments.
    • Accessing attributes via the class:
        – Class attributes take precedence (Python’s normal rule).
        – If the attribute is not found on the class, the lookup is forwarded to the singleton
          instance. If the instance does not exist yet, it is created by calling `get()`; this
          may call `set()` with the last stored arguments (possibly empty).

    `__dict__` (merged, read-only)
    ----------------------------
    • Returns a read-only mapping combining class and instance attributes (once initialized):
      instance keys are included, but class keys take precedence on conflicts.
    • Note: this deviates from Python’s usual class `__dict__` (class-only).

    `__dir__` (discoverability)
    -------------------------
    • Returns the union of class and instance attribute names (once initialized) to improve
      interactive help and IDE autocompletion. Names only; no values or precedence.

    Writes (no forwarding)
    ----------------------
    • Setting attributes on the class writes to the class namespace; writes are not forwarded
      to the singleton instance.

    Error semantics
    ---------------
    • If `get()` is called when no instance exists and no prior constructor arguments are stored,
      `set()` will attempt to construct with empty arguments; this may raise a `TypeError` if the
      subclass constructor requires parameters.
    • Missing attributes on both the class and the instance raise `AttributeError` (standard Python).
    """

    def __init__(cls: Type[T], name: str, bases: Tuple[type, ...], ns: Dict[str, Any]) -> None:
        super().__init__(name, bases, ns)
        cls._lock: RLock = RLock()
        cls._instance: Optional[T] = None
        cls._args: Tuple[Any, ...] = ()
        cls._kwargs: Dict[str, Any] = {}

    def __call__(cls: Type[T], *args: Any, **kwargs: Any) -> T:
        """
        Returns the singleton instance for the specified class.

        If no instance exists, creates it using the specified constructor arguments.
        If an instance already exists, optionally updates stored constructor arguments and
        recreates the instance.
        """
        with cls._lock:
            if cls._instance is None:
                logging.debug(
                    f"Create the singleton instance of '{cls.__module__}.{cls.__qualname__}'."
                )
                cls._args = args
                cls._kwargs = kwargs
            else:
                # Update the stored constructor arguments if new ones are specified;
                # otherwise reuse the last ones
                if args or kwargs:
                    cls._args = args
                    cls._kwargs = kwargs
                    logging.debug(
                        f"Recreate the singleton instance of '{cls.__module__}.{cls.__qualname__}' "
                        f"with the new constructor arguments."
                    )
                else:
                    logging.debug(
                        f"Recreate the singleton instance of '{cls.__module__}.{cls.__qualname__}' "
                        f"with the last constructor arguments."
                    )
            cls._instance = super(SingletonMeta, cls).__call__(*cls._args, **cls._kwargs)
            return cast(T, cls._instance)

    def __getattribute__(cls: Type[T], name: str) -> Any:
        """
        Returns a class attribute (preferred) or forwards to the singleton instance after
        initialization.

        Special case:
        • `__dict__` returns a read-only merged view of class and instance dictionaries
          (if initialized) with class keys taking precedence on conflicts.
        """
        # Special-case `__dict__` to provide a merged, read-only view
        if name == "__dict__":
            class_dict = super().__getattribute__("__dict__")
            try:
                lock = super().__getattribute__("_lock")
            except AttributeError:
                # Fall back to the class dict during very early initialization
                return class_dict
            with lock:
                instance = super().__getattribute__("_instance")
                if instance is None:
                    return class_dict
                merged_dict: Dict[str, Any] = dict(getattr(instance, "__dict__", {}))
                # Class keys take precedence on conflicts (overlay class entries last), then freeze
                merged_dict.update(dict(class_dict))
                return MappingProxyType(merged_dict)

        # Keep dunders and management attributes as class attributes
        if name.startswith("__") and name.endswith("__"):
            return super().__getattribute__(name)
        if name in {
            "_lock",
            "_instance",
            "_args",
            "_kwargs",
            "exists",
            "get",
            "set",
            "reset",
            "delete",
            "mro",
        }:
            return super().__getattribute__(name)

        # 1) Class-first lookup
        try:
            return super().__getattribute__(name)
        except AttributeError:
            pass

        # 2) Forward to the instance (creating it if necessary via get(), which may call set())
        try:
            lock = super().__getattribute__("_lock")
        except AttributeError as e:
            # Very early init: behave as not initialized yet (cannot safely create)
            raise RuntimeError(
                f"Singleton '{cls.__module__}.{cls.__qualname__}' is not initialized; "
                f"call '{cls.__qualname__}(...)' first"
            ) from e
        with lock:
            # Obtain or create the instance via get() using stored args (possibly empty)
            instance_get = super().__getattribute__("get")
            instance = instance_get()
            # Do not catch `AttributeError` here; if the instance lacks `name`, let it propagate
            return getattr(instance, name)

    def __dir__(cls: Type[T]) -> List[str]:
        """
        Returns the merged set of attribute names from the class and, if initialized, the singleton
        instance. Includes names exposed by slots, properties, and dynamic attributes visible via
        dir(instance).
        """
        names = set(super().__dir__())
        with cls._lock:
            inst = cls._instance
            if inst is not None:
                # Include slots, properties, and dynamic attributes visible via dir()
                names.update(dir(inst))
        return sorted(names)

    ##############################################

    def exists(cls: Type[T]) -> bool:
        """Returns True if the singleton instance exists."""
        with cls._lock:
            return cls._instance is not None

    def get(cls: Type[T]) -> T:
        """
        Returns the singleton instance. If it does not exist yet, creates it by calling set()
        with the last stored constructor arguments (possibly empty).
        """
        with cls._lock:
            if cls._instance is None:
                # Create the singleton instance using the last stored arguments (may be empty)
                cls.set(*cls._args, **cls._kwargs)
            return cast(T, cls._instance)

    def set(cls: Type[T], *args: Any, **kwargs: Any) -> T:
        """
        Creates the singleton instance if it does not exist yet using the specified constructor
        arguments, and stores those arguments for future resets or recreations.
        """
        with cls._lock:
            if cls._instance is None:
                logging.debug(
                    f"Create the singleton instance of '{cls.__module__}.{cls.__qualname__}'."
                )
                cls._args = args
                cls._kwargs = kwargs
                cls._instance = super(SingletonMeta, cls).__call__(*args, **kwargs)
            return cast(T, cls._instance)

    def reset(cls: Type[T]) -> T:
        """
        Recreates the singleton instance using the last stored constructor arguments.
        Raises `UnboundLocalError` if no instance has been created yet.
        """
        with cls._lock:
            if cls._instance is None:
                raise UnboundLocalError(
                    f"The singleton instance of '{cls.__module__}.{cls.__qualname__}' is not created"
                )
            logging.debug(
                f"Recreate the singleton instance of '{cls.__module__}.{cls.__qualname__}' "
                f"with the last constructor arguments."
            )
            cls._instance = super(SingletonMeta, cls).__call__(*cls._args, **cls._kwargs)
            return cast(T, cls._instance)

    def delete(cls: Type[T]) -> None:
        """Deletes the singleton instance if it exists (does nothing otherwise)."""
        with cls._lock:
            if cls._instance is not None:
                logging.debug(
                    f"Delete the singleton instance of '{cls.__module__}.{cls.__qualname__}'."
                )
                cls._instance = None


# • TEMPORARY SINGLETON (WITH EXPIRATION) ##########################################################


class TempSingletonMeta(type):
    """
    Ensures a per-subclass *temporary* singleton instance with thread-safe initialization and a
    configurable lifespan in seconds. When the instance expires, the next access (via the class)
    recreates it using the last stored constructor arguments.

    Behavior
    --------
    • The first call to the subclass constructs the temp singleton using the specified
      constructor arguments and timestamps its creation.
    • Subsequent calls:
        – If new constructor arguments are specified, they are stored and the temp singleton is
          recreated with those arguments and a fresh timestamp.
        – If no arguments are specified, the temp singleton is recreated using the last stored
          constructor arguments and a fresh timestamp.
    • Accessing attributes via the class:
        – Class attributes take precedence (Python’s normal rule).
        – If the attribute is not defined on the class, the lookup is forwarded to the temp
          singleton instance obtained via `get()`. If the instance does not exist or is expired,
          `get()` will (re)create it (using `set()` or `reset()`), then the attribute is read.

    Lifespan (seconds)
    ------------------
    • `_lifespan == 0` (default) means no expiration: the instance never expires once created.
    • `_lifespan > 0` means the instance expires `_lifespan` seconds after `_created_at`.
      Expiration is evaluated lazily on access (`get()` / forwarded reads).

    `__dict__` (merged, read-only)
    ----------------------------
    • Returns a read-only mapping that combines class and instance attributes once initialized:
      instance keys are included, but class keys take precedence on conflicts.
    • Note: this deviates from Python’s usual class `__dict__` (class-only).

    `__dir__` (discoverability)
    -------------------------
    • Returns the union of class and instance attribute names (once initialized) to improve
      interactive help and IDE autocompletion. Names only; no values or precedence.

    Writes (no forwarding)
    ----------------------
    • Setting attributes on the class writes to the class namespace; writes are not forwarded
      to the temp singleton instance.

    Error semantics
    ---------------
    • If `get()` is called when no instance exists and no prior constructor arguments are stored,
      `set()` will attempt to construct with empty arguments; this may raise a `TypeError` if the
      subclass constructor requires parameters.
    • Missing attributes on both the class and the instance raise `AttributeError` (standard Python).
    """

    def __init__(cls: Type[T], name: str, bases: Tuple[type, ...], ns: Dict[str, Any]) -> None:
        super().__init__(name, bases, ns)
        cls._lock: RLock = RLock()
        cls._instance: Optional[T] = None
        cls._args: Tuple[Any, ...] = ()
        cls._kwargs: Dict[str, Any] = {}
        cls._lifespan: int = 0  # seconds; 0 means no expiration
        cls._created_at: int = 0  # epoch seconds

    def __call__(cls: Type[T], *args: Any, **kwargs: Any) -> T:
        """
        Returns the temp singleton instance for the specified class.

        If no instance exists, creates it using the specified constructor arguments.
        If an instance already exists, optionally updates stored constructor arguments and
        recreates the instance (refreshes timestamp).
        """
        with cls._lock:
            if cls._instance is None:
                logging.debug(
                    f"Create the temp singleton instance of '{cls.__module__}.{cls.__qualname__}'."
                )
                cls._args = args
                cls._kwargs = kwargs
            else:
                # Update the stored constructor arguments if new ones are specified;
                # otherwise reuse the last ones
                if args or kwargs:
                    cls._args = args
                    cls._kwargs = kwargs
                    logging.debug(
                        f"Recreate the temp singleton instance of '{cls.__module__}.{cls.__qualname__}' "
                        f"with the new constructor arguments."
                    )
                else:
                    logging.debug(
                        f"Recreate the temp singleton instance of '{cls.__module__}.{cls.__qualname__}' "
                        f"with the last constructor arguments."
                    )
            cls._instance = super(TempSingletonMeta, cls).__call__(*cls._args, **cls._kwargs)
            cls._created_at = cls._now()
            return cast(T, cls._instance)

    def __getattribute__(cls: Type[T], name: str) -> Any:
        """
        Returns a class attribute (preferred) or forwards to the temp singleton instance after
        initialization and on-demand refresh if expired.

        Special case:
        • `__dict__` returns a read-only merged view of class and instance dictionaries
          (if initialized) with class keys taking precedence on conflicts.
        """
        # Special-case `__dict__` to provide a merged, read-only view
        if name == "__dict__":
            class_dict = super().__getattribute__("__dict__")
            try:
                lock = super().__getattribute__("_lock")
            except AttributeError:
                # Fall back to the class dict during very early initialization
                return class_dict
            with lock:
                instance = super().__getattribute__("_instance")
                if instance is None:
                    return class_dict
                merged_dict: Dict[str, Any] = dict(getattr(instance, "__dict__", {}))
                # Class keys take precedence on conflicts (overlay class entries last), then freeze
                merged_dict.update(dict(class_dict))
                return MappingProxyType(merged_dict)

        # Keep dunders and management attributes as class attributes
        if name.startswith("__") and name.endswith("__"):
            return super().__getattribute__(name)
        if name in {
            "_lock",
            "_instance",
            "_args",
            "_kwargs",
            "_lifespan",
            "_created_at",
            "exists",
            "get",
            "set",
            "reset",
            "delete",
            "_is_valid",
            "_now",
            "mro",
        }:
            return super().__getattribute__(name)

        # 1) Class-first lookup
        try:
            return super().__getattribute__(name)
        except AttributeError:
            pass

        # 2) Forward to the instance obtained via get() (auto-refresh on expiry)
        try:
            lock = super().__getattribute__("_lock")
        except AttributeError as e:
            # Very early init: behave as not initialized yet (cannot safely create)
            raise RuntimeError(
                f"Temp singleton '{cls.__module__}.{cls.__qualname__}' is not initialized; "
                f"call '{cls.__qualname__}(...)' first"
            ) from e
        with lock:
            instance_get = super().__getattribute__("get")
            instance = instance_get()  # May create or refresh if expired
            # Do not catch `AttributeError` here; let genuine missing attributes propagate
            return getattr(instance, name)

    def __dir__(cls: Type[T]) -> List[str]:
        """
        Returns the merged set of attribute names from the class and, if initialized, the temp
        singleton instance. Includes names exposed by slots, properties, and dynamic attributes
        visible via dir(instance).
        """
        names = set(super().__dir__())
        with cls._lock:
            inst = cls._instance
            if inst is not None:
                # Include slots, properties, and dynamic attributes visible via dir()
                names.update(dir(inst))
        return sorted(names)

    ##############################################

    def exists(cls: Type[T]) -> bool:
        """Returns True if the temp singleton instance exists and is not expired."""
        with cls._lock:
            return cls._instance is not None and cls._is_valid()

    def get(cls: Type[T]) -> T:
        """
        Returns the temp singleton instance. If it does not exist, creates it by calling set()
        with the last stored constructor arguments (possibly empty). If it exists but is expired,
        recreates it by calling reset().
        """
        with cls._lock:
            if cls._instance is None:
                # Create the temp singleton instance using the last stored arguments (may be empty)
                return cls.set(*cls._args, **cls._kwargs)
            if not cls._is_valid():
                # Recreate the temp singleton instance if expired
                return cls.reset()
            return cast(T, cls._instance)

    def set(cls: Type[T], *args: Any, _lifespan: Optional[int] = None, **kwargs: Any) -> T:
        """
        Creates or replaces the temp singleton instance using the specified constructor arguments.
        Also stores the arguments for future recreations and sets (or leaves) the lifespan.
        """
        with cls._lock:
            logging.debug(
                f"Create the temp singleton instance of '{cls.__module__}.{cls.__qualname__}'."
            )
            cls._args = args
            cls._kwargs = kwargs
            if _lifespan is not None:
                if _lifespan < 0:
                    raise ValueError("Lifespan must be non-negative")
                cls._lifespan = int(_lifespan)
            cls._instance = super(TempSingletonMeta, cls).__call__(*args, **kwargs)
            cls._created_at = cls._now()
            return cast(T, cls._instance)

    def reset(cls: Type[T]) -> T:
        """
        Recreates the temp singleton instance using the last stored constructor arguments and
        updates the creation timestamp. Raises `UnboundLocalError` if no instance has been created.
        """
        with cls._lock:
            if cls._instance is None:
                raise UnboundLocalError(
                    f"The temp singleton instance of '{cls.__module__}.{cls.__qualname__}' is not created"
                )
            logging.debug(
                f"Recreate the temp singleton instance of '{cls.__module__}.{cls.__qualname__}' "
                f"with the last constructor arguments."
            )
            cls._instance = super(TempSingletonMeta, cls).__call__(*cls._args, **cls._kwargs)
            cls._created_at = cls._now()
            return cast(T, cls._instance)

    def delete(cls: Type[T]) -> None:
        """Deletes the temp singleton instance if it exists and clears the creation timestamp."""
        with cls._lock:
            if cls._instance is not None:
                logging.debug(
                    f"Delete the temp singleton instance of '{cls.__module__}.{cls.__qualname__}'."
                )
                cls._instance = None
                cls._created_at = 0

    def _is_valid(cls: Type[T]) -> bool:
        """Returns True if the temp singleton has not expired; False otherwise."""
        if cls._instance is None:
            return False
        if cls._lifespan <= 0:
            return True
        age = cls._now() - cls._created_at
        logging.debug(
            f"Current lifespan of '{cls.__module__}.{cls.__qualname__}': {age}s (max {cls._lifespan}s)"
        )
        return age < cls._lifespan

    @staticmethod
    def _now() -> int:
        """Returns the current Unix timestamp in seconds."""
        from nutil.scalar.date import get_stamp

        return int(get_stamp())
