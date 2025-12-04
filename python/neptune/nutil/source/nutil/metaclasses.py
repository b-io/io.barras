#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide metaclasses.
########################################################################################################################

from __future__ import annotations

import logging
from threading import RLock
from types import MappingProxyType
from typing import Any, cast, Dict, List, NoReturn, Optional, Tuple, Type, TypeVar

## METACLASSES ###########################################################################

__METACLASSES_______________________________________________ = ""

T = TypeVar("T")


def combine_metaclasses(*metas: Type[type]) -> type:
    """
    Returns a new metaclass that inherits from the specified metaclasses in left-to-right order.
    If no metaclasses are specified, returns the built-in `type`.
    """
    if not metas:
        return type
    name = "".join(m.__name__ for m in metas) or "CombinedMeta"
    return type(name, metas, {})


### NO PUBLIC CONSTRUCTOR ##################################


class NoPublicConstructorMeta(type):
    """A metaclass that forbids direct instantiation (no public constructor)."""

    def __call__(cls: Type[T], *args: Any, **kwargs: Any) -> NoReturn:
        raise TypeError(f"'{cls.__module__}.{cls.__qualname__}' has no public constructor")


### FINAL SINGLETON ########################################


class FinalSingletonMeta(type):
    """
    A metaclass that enforces a final, per-subclass singleton instance with thread-safe
    initialization.
    """

    def __init__(cls: Type[T], name: str, bases: Tuple[Type[Any], ...], ns: Dict[str, Any]) -> None:
        super().__init__(name, bases, ns)
        cls._lock: RLock = RLock()
        cls._instance: Optional[T] = None

    def __call__(cls: Type[T], *args: Any, **kwargs: Any) -> T:
        """
        Returns the final singleton instance.

        If no instance exists, creates it using the specified constructor arguments.
        If an instance already exists, returns it and ignores any specified constructor arguments.
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

        Special case: `__dict__` returns a read-only merged view of class and instance dictionaries
        (class keys take precedence on conflicts).
        """
        # Special-case `__dict__` to provide a merged, read-only view
        if name == "__dict__":
            class_dict = super().__getattribute__("__dict__")
            try:
                lock = super().__getattribute__("_lock")
            except AttributeError:
                # Fall back to the class `dict` during very early initialization
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

        # 2) Forward to the instance
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
            # Do not catch `AttributeError` here; if the instance lacks `name`, let it propagate
            return getattr(instance, name)

    def __dir__(cls: Type[T]) -> List[str]:
        """Returns the union of class and instance attribute names (once initialized)."""
        names = set(super().__dir__())
        with cls._lock:
            instance = cls._instance
            if instance is not None:
                # Include the slots, properties, and dynamic attributes visible via `dir(...)`
                names.update(dir(instance))
        return sorted(names)

    ########################################################

    def exists(cls: Type[T]) -> bool:
        """Returns whether the final singleton instance exists."""
        with cls._lock:
            return cls._instance is not None

    def get(cls: Type[T]) -> T:
        """Returns the final singleton instance, or raises if it is not initialized."""
        with cls._lock:
            if cls._instance is None:
                raise RuntimeError(
                    f"Final singleton '{cls.__module__}.{cls.__qualname__}' is not initialized; "
                    f"call '{cls.__qualname__}(...)' first"
                )
            return cast(T, cls._instance)


### OVERWRITABLE SINGLETON #################################


class SingletonMeta(type):
    """
    A metaclass that provides a per-subclass singleton instance with controlled overwrite and thread
    safety.
    """

    def __init__(cls: Type[T], name: str, bases: Tuple[Type[Any], ...], ns: Dict[str, Any]) -> None:
        super().__init__(name, bases, ns)
        cls._lock: RLock = RLock()
        cls._instance: Optional[T] = None
        cls._args: Tuple[Any, ...] = ()
        cls._kwargs: Dict[str, Any] = {}

    def __call__(cls: Type[T], *args: Any, **kwargs: Any) -> T:
        """
        Returns the singleton instance for the specified class.

        If no instance exists, creates it using the specified constructor arguments.
        If an instance already exists, optionally updates stored constructor arguments and recreates
        the instance.
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
                # Otherwise reuse the last stored ones
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
                        f"with the last stored constructor arguments."
                    )
            cls._instance = super(SingletonMeta, cls).__call__(*cls._args, **cls._kwargs)
            return cast(T, cls._instance)

    def __getattribute__(cls: Type[T], name: str) -> Any:
        """
        Returns a class attribute (preferred) or forwards to the singleton instance after
        initialization.

        Special case: `__dict__` returns a read-only merged view of class and instance dictionaries
        (class keys take precedence on conflicts).
        """
        # Special-case `__dict__` to provide a merged, read-only view
        if name == "__dict__":
            class_dict = super().__getattribute__("__dict__")
            try:
                lock = super().__getattribute__("_lock")
            except AttributeError:
                # Fall back to the class `dict` during very early initialization
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

        # 2) Forward to the instance
        try:
            lock = super().__getattribute__("_lock")
        except AttributeError as e:
            # Early init: behave as not initialized yet
            raise RuntimeError(
                f"Singleton '{cls.__module__}.{cls.__qualname__}' is not initialized; "
                f"call '{cls.__qualname__}(...)' first"
            ) from e
        with lock:
            # Get or create the instance via `get()` using the stored constructor arguments
            instance_get = super().__getattribute__("get")
            instance = instance_get()  # creates or refreshes it if it is expired
            # Do not catch `AttributeError` here; if the instance lacks `name`, let it propagate
            return getattr(instance, name)

    def __dir__(cls: Type[T]) -> List[str]:
        """Returns the union of class and instance attribute names (once initialized)."""
        names = set(super().__dir__())
        with cls._lock:
            instance = cls._instance
            if instance is not None:
                # Include the slots, properties, and dynamic attributes visible via `dir(...)`
                names.update(dir(instance))
        return sorted(names)

    ########################################################

    def exists(cls: Type[T]) -> bool:
        """Returns whether the singleton instance exists."""
        with cls._lock:
            return cls._instance is not None

    def get(cls: Type[T]) -> T:
        """Returns the singleton instance; creates it via `cls.set(...)` if it is missing."""
        with cls._lock:
            if cls._instance is None:
                # Create the singleton instance using the last stored constructor arguments
                cls.set(*cls._args, **cls._kwargs)
            return cast(T, cls._instance)

    def set(cls: Type[T], *args: Any, **kwargs: Any) -> T:
        """
        Creates the singleton instance if it does not exist yet using the specified constructor arguments and stores
        them for future resets.
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
        """Recreates the singleton instance using the last stored constructor arguments."""
        with cls._lock:
            if cls._instance is None:
                raise UnboundLocalError(
                    f"The singleton instance of '{cls.__module__}.{cls.__qualname__}' is not created"
                )
            logging.debug(
                f"Recreate the singleton instance of '{cls.__module__}.{cls.__qualname__}' "
                f"with the last stored constructor arguments."
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


# • TEMPORARY SINGLETON (WITH EXPIRATION) ##############################################################################


class TempSingletonMeta(type):
    """
    A metaclass that provides a per-subclass *temporary* singleton instance with an optional
    expiration.
    """

    def __init__(cls: Type[T], name: str, bases: Tuple[Type[Any], ...], ns: Dict[str, Any]) -> None:
        super().__init__(name, bases, ns)
        cls._lock: RLock = RLock()
        cls._instance: Optional[T] = None
        cls._args: Tuple[Any, ...] = ()
        cls._kwargs: Dict[str, Any] = {}
        cls._lifespan: int = 0  # seconds; 0 = no expiration
        cls._created_at: int = 0  # epoch seconds

    def __call__(cls: Type[T], *args: Any, **kwargs: Any) -> T:
        """Returns the temp singleton; (re)creates it and refreshes its timestamp."""
        with cls._lock:
            if cls._instance is None:
                logging.debug(
                    f"Create the temp singleton instance of '{cls.__module__}.{cls.__qualname__}'."
                )
                cls._args = args
                cls._kwargs = kwargs
            else:
                # Update the stored constructor arguments if new ones are specified;
                # Otherwise reuse the last stored ones
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
                        f"with the last stored constructor arguments."
                    )
            cls._instance = super(TempSingletonMeta, cls).__call__(*cls._args, **cls._kwargs)
            cls._created_at = cls._now()
            return cast(T, cls._instance)

    def __getattribute__(cls: Type[T], name: str) -> Any:
        """
        Returns a class attribute (preferred) or forwards to the temp singleton instance
        (auto-refresh on expiry).

        Special case: `__dict__` returns a read-only merged view of class and instance dictionaries
        (class keys take precedence on conflicts).
        """
        # Special-case `__dict__` to provide a merged, read-only view
        if name == "__dict__":
            class_dict = super().__getattribute__("__dict__")
            try:
                lock = super().__getattribute__("_lock")
            except AttributeError:
                # Fall back to the class `dict` during very early initialization
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

        # 2) Forward to the instance
        try:
            lock = super().__getattribute__("_lock")
        except AttributeError as e:
            # Early init: behave as not initialized yet
            raise RuntimeError(
                f"Temp singleton '{cls.__module__}.{cls.__qualname__}' is not initialized; "
                f"call '{cls.__qualname__}(...)' first"
            ) from e
        with lock:
            # Get or create the instance via `get()` using the stored constructor arguments
            instance_get = super().__getattribute__("get")
            instance = instance_get()  # creates or refreshes it if it is expired
            # Do not catch `AttributeError` here; if the instance lacks `name`, let it propagate
            return getattr(instance, name)

    def __dir__(cls: Type[T]) -> List[str]:
        """Returns the union of class and instance attribute names (once initialized)."""
        names = set(super().__dir__())
        with cls._lock:
            instance = cls._instance
            if instance is not None:
                # Include the slots, properties, and dynamic attributes visible via `dir(...)`
                names.update(dir(instance))
        return sorted(names)

    ########################################################

    def exists(cls: Type[T]) -> bool:
        """Returns whether the temp singleton instance exists and is not expired."""
        with cls._lock:
            return cls._instance is not None and cls._is_valid()

    def get(cls: Type[T]) -> T:
        """
        Returns the temp singleton; creates or refreshes it via `cls.set(...)` if it is missing or
        expired.
        """
        with cls._lock:
            if cls._instance is None:
                return cls.set(*cls._args, **cls._kwargs)
            if not cls._is_valid():
                return cls.reset()
            return cast(T, cls._instance)

    def set(cls: Type[T], *args: Any, _lifespan: Optional[int] = None, **kwargs: Any) -> T:
        """Creates or replaces the temp singleton instance and (optionally) sets the lifespan."""
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
        Recreates the temp singleton instance with the last stored constructor arguments and
        refreshes the creation timestamp.
        """
        with cls._lock:
            if cls._instance is None:
                raise UnboundLocalError(
                    f"The temp singleton instance of '{cls.__module__}.{cls.__qualname__}' is not created"
                )
            logging.debug(
                f"Recreate the temp singleton instance of '{cls.__module__}.{cls.__qualname__}' "
                f"with the last stored constructor arguments."
            )
            cls._instance = super(TempSingletonMeta, cls).__call__(*cls._args, **cls._kwargs)
            cls._created_at = cls._now()
            return cast(T, cls._instance)

    def delete(cls: Type[T]) -> None:
        """Deletes the temp singleton instance if it exists and clears its creation timestamp."""
        with cls._lock:
            if cls._instance is not None:
                logging.debug(
                    f"Delete the temp singleton instance of '{cls.__module__}.{cls.__qualname__}'."
                )
                cls._instance = None
                cls._created_at = 0

    def _is_valid(cls: Type[T]) -> bool:
        """Returns whether the temp singleton instance is not expired."""
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
