import logging
import threading
from collections import defaultdict
from typing import Any, Dict, List, Type, TypeVar

from nutil.common import get_stamp

T = TypeVar("T")


def combine_metaclasses(*args):
    name = "".join(a.__name__ for a in args)
    return type(name, args, {})


class NoPublicConstructorMeta(type):
    """
    Metaclass ensuring no public constructor.

    Raises:
        TypeError: If a class using this metaclass is instantiated.
    """

    def __call__(cls, *args: Any, **kwargs: Any):
        raise TypeError(f"'{cls .__module__ }.{cls.__qualname__ }' has no public constructor")


class FinalSingletonMeta(type):
    """
    Metaclass ensuring final single instance.
    """

    _instances: Dict[Type[T], T] = {}

    def __call__(cls, *args: Any, **kwargs: Any):
        if cls not in cls._instances:
            # Create the instance
            logging.debug(
                f"Create the final single instance of '{cls.__module__}.{cls.__qualname__}.'"
            )
            cls._instances[cls] = super().__call__(*args, **kwargs)
        return cls._instances[cls]


class SingletonMeta(type):
    """
    Metaclass ensuring single instance and allowing instance overwrite.

    Raises:
        TypeError: If a class using this metaclass is instantiated.
    """

    _args: Dict[Type[T], List] = {}
    _kwargs: Dict[Type[T], Dict] = {}
    _instances: Dict[Type[T], T] = 0
    _locks: Dict[Type[T], threading.RLock] = defaultdict(threading.RLock)

    def __call__(cls, *args: Any, **kwargs: Any):
        raise TypeError(
            f"'{cls.__module__}.{cls.__qualname__}' has no public constructor; "
            "use the getters and setters"
        )

    def lock(cls: Type[T]) -> threading.RLock:
        return cls.__locks[cls]

    def exists(cls: Type[T]) -> bool:
        with cls.lock():
            return cls in cls._instances

    def get(cls: Type[T]) -> T:
        with cls.lock():
            if not cls.exists():
                cls.set()
            return cls._instances[cls]

    def set(cls: Type[T], *args: Any, **kwargs: Any) -> T:
        with cls.lock():
            if not cls.exists():
                # Store the arguments and keyword arguments
                cls._args[cls] = args
                cls._kwargs[cls] = kwargs
                # Create the instance
                logging.debug(
                    f"Create the single instance of '{cls.__module__}.{cls.__qualname__}.'"
                )
                cls._instances[cls] = super().__call__(*args, **kwargs)
            return cls._instances[cls]

    def reset(cls: Type[T]):
        with cls.lock():
            if not cls.exists():
                raise UnboundLocalError(
                    f"The single instance of '{cls.__module__}.{cls.__qualname__}' is not created"
                )
            # Recreate the instance
            logging.debug(f"Recreate the single instance of '{cls.__module__}.{cls.__qualname__}'.")
            cls._instances[cls] = super().__call__(*cls._args[cls], **cls._kwargs[cls])

    def delete(cls: Type[T]):
        with cls.lock():
            if cls.exists():
                # Delete the instance
                logging.debug(f"Delete the instance of '{cls.__module__}. {cls.__qualname__}'.")
                del cls._instances[cls]


class TempSingletonMeta(type):
    """
    Metaclass ensuring single instance and allowing instance overwrite.

    Raises:
        TypeError: If a class using this metaclass is instantiated.
    """

    _args: Dict[Type[T], List] = {}
    _kwargs: Dict[Type[T], Dict] = {}
    _instances: Dict[Type[T], T] = {}
    _locks: Dict[Type[T], threading.RLock] = defaultdict(threading.RLock)

    _lifespan: Dict[Type[T], int] = defaultdict(int)
    _creation_time: Dict[Type[T], int] = defaultdict(int)

    def __call__(cls, *args: Any, **kwargs: Any):
        raise TypeError(
            f"{cls.__module__}. {cls.__qualname__} has no public constructor; "
            f"use the getters and setters"
        )

    def lock(cls: Type[T]) -> threading.RLock:
        return cls._locks[cls]

    def exists(cls: Type[T]) -> bool:
        with cls.lock():
            return cls in cls._instances

    def valid(cls: Type[T]) -> bool:
        with cls.lock():
            if not cls.exists():
                return False
            get_stamp()
            current
