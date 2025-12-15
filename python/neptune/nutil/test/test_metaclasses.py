#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the metaclasses.
########################################################################################################################

import unittest

import pytest

from nutil.io.logging import configure_logging
from nutil.metaclasses import *

## METACLASSES TEST CASES ################################################################

__METACLASSES_TEST_CASES____________________________________ = ""


### COMBINE METACLASSES ####################################


def test_combine_metaclasses_returns_type_when_no_metas() -> None:
    assert combine_metaclasses() is type


def test_combine_metaclasses_creates_metaclass_inheriting_all() -> None:
    class MetaA(type):
        marker_a = "A"

    class MetaB(type):
        marker_b = "B"

    Combined = combine_metaclasses(MetaA, MetaB)

    # Combined should inherit from both
    assert issubclass(Combined, MetaA)
    assert issubclass(Combined, MetaB)

    class MyClass(metaclass=Combined):  # type: ignore[misc]
        pass

    assert type(MyClass) is Combined
    assert MyClass.__class__.marker_a == "A"
    assert MyClass.__class__.marker_b == "B"


### NO PUBLIC CONSTRUCTOR ##################################


def test_no_public_constructor_meta_forbids_instantiation() -> None:
    class Hidden(metaclass=NoPublicConstructorMeta):
        pass

    with pytest.raises(TypeError, match="has no public constructor"):
        Hidden()

    # `Hidden` is still a normal type object otherwise
    assert isinstance(Hidden, type)


def test_no_public_constructor_meta_forbids_subclass_instantiation() -> None:
    class BaseHidden(metaclass=NoPublicConstructorMeta):
        pass

    class DerivedHidden(BaseHidden):
        pass

    with pytest.raises(TypeError, match="has no public constructor"):
        DerivedHidden()


### FINAL SINGLETON META ###################################


def test_final_singleton_meta_creates_single_instance_and_ignores_extra_args() -> None:
    class FinalConfig(metaclass=FinalSingletonMeta):
        def __init__(self, value: int) -> None:
            self.value = value

    assert not FinalConfig.exists()

    first = FinalConfig(1)
    second = FinalConfig(2)

    assert first is second
    assert FinalConfig.exists()
    # Constructor arguments after the first call are ignored
    assert first.value == 1
    assert second.value == 1

    # `get()` returns the same instance
    assert FinalConfig.get() is first


def test_final_singleton_meta_attribute_forwarding_and_not_initialized_error() -> None:
    class FinalConfig(metaclass=FinalSingletonMeta):
        CONST = 42

        def __init__(self, value: int) -> None:
            self.value = value

    # Class attribute is available without initialization
    assert FinalConfig.CONST == 42

    # Accessing an instance attribute before initialization should fail
    with pytest.raises(RuntimeError, match="is not initialized"):
        _ = FinalConfig.value  # type: ignore[attr-defined]

    # After initialization, instance attributes are visible on the class
    instance = FinalConfig(10)
    assert FinalConfig.value == 10  # forwarded to instance
    assert instance is FinalConfig.get()


def test_final_singleton_meta_dict_is_merged_and_readonly() -> None:
    class FinalConfig(metaclass=FinalSingletonMeta):
        CONST = 100
        shadowed = "class"

        def __init__(self) -> None:
            self.value = 1
            self.shadowed = "instance"

    # Before initialization, `__dict__` is just the class dict
    class_dict = FinalConfig.__dict__
    assert isinstance(class_dict, MappingProxyType)
    assert "CONST" in class_dict
    assert "value" not in class_dict

    # After initialization, `__dict__` is a merged, read-only view
    _ = FinalConfig()
    merged_dict = FinalConfig.__dict__
    assert isinstance(merged_dict, MappingProxyType)
    assert merged_dict["CONST"] == 100
    assert merged_dict["value"] == 1
    # Class keys take precedence
    assert merged_dict["shadowed"] == "class"

    with pytest.raises(TypeError):
        merged_dict["x"] = 1  # type: ignore[index]


### SINGLETON META #########################################


def test_singleton_meta_creates_and_recreates_instance_via_call() -> None:
    class Service(metaclass=SingletonMeta):
        def __init__(self, value: int = 0) -> None:
            self.value = value

    assert not Service.exists()

    # First call creates instance
    first = Service(10)
    assert Service.exists()
    assert first.value == 10

    # Second call with new args recreates instance
    second = Service(20)
    assert second is not first
    assert second.value == 20

    # Call without args reuses the last stored constructor args (20)
    third = Service()
    assert third is not second
    assert third.value == 20

    # `get()` returns the current singleton instance
    assert Service.get() is third


def test_singleton_meta_attribute_forwarding_and_implicit_creation() -> None:
    class Service(metaclass=SingletonMeta):
        CONST = 7

        def __init__(self, value: int = 0) -> None:
            self.value = value

    # Class attribute is available without initialization
    assert Service.CONST == 7
    assert not Service.exists()

    # Accessing an instance attribute triggers implicit creation via `get()`
    assert Service.value == 0  # type: ignore[attr-defined]
    assert Service.exists()
    instance = Service.get()
    assert instance.value == 0

    # After explicit recreation, attributes still forward correctly
    Service(5)
    assert Service.value == 5  # type: ignore[attr-defined]


def test_singleton_meta_dict_is_merged_and_readonly() -> None:
    class Service(metaclass=SingletonMeta):
        CONST = 1
        shadowed = "class"

        def __init__(self) -> None:
            self.value = 2
            self.shadowed = "instance"

    _ = Service()
    merged_dict = Service.__dict__

    assert isinstance(merged_dict, MappingProxyType)
    assert merged_dict["CONST"] == 1
    assert merged_dict["value"] == 2
    # Class keys take precedence
    assert merged_dict["shadowed"] == "class"

    with pytest.raises(TypeError):
        merged_dict["x"] = 1  # type: ignore[index]


def test_singleton_meta_delete_and_reset_and_get() -> None:
    class Service(metaclass=SingletonMeta):
        def __init__(self, value: int = 0) -> None:
            self.value = value

    # Create via `set`
    instance1 = Service.set(3)
    assert Service.exists()
    assert instance1.value == 3

    # Reset recreates with stored args
    instance2 = Service.reset()
    assert instance2 is not instance1
    assert instance2.value == 3

    # Delete removes the instance but keeps stored args
    Service.delete()
    assert not Service.exists()

    # `get()` recreates using last stored args
    instance3 = Service.get()
    assert instance3.value == 3


### TEMPORARY SINGLETON META ###############################


def test_temp_singleton_meta_lifespan_and_expiration() -> None:
    current_time = 0

    def fake_now() -> int:
        return current_time

    class TempService(metaclass=TempSingletonMeta):
        def __init__(self, value: int) -> None:
            self.value = value

    # Override `_now` to make time deterministic
    TempService._now = staticmethod(fake_now)  # type: ignore[assignment]

    assert not TempService.exists()

    # Create with lifespan 10s
    instance1 = TempService.set(1, _lifespan=10)
    assert TempService.exists()
    assert instance1.value == 1

    # Still valid at t=5
    current_time = 5
    assert TempService.exists()
    instance2 = TempService.get()
    assert instance2 is instance1

    # Expired at t=11
    current_time = 11
    assert not TempService.exists()

    # `get()` should recreate (via reset) with stored args
    instance3 = TempService.get()
    assert instance3 is not instance1
    assert instance3.value == 1
    assert TempService.exists()


def test_temp_singleton_meta_attribute_forwarding_uses_auto_refresh() -> None:
    current_time = 0

    def fake_now() -> int:
        return current_time

    class TempService(metaclass=TempSingletonMeta):
        CONST = 9

        def __init__(self, value: int) -> None:
            self.value = value

    TempService._now = staticmethod(fake_now)  # type: ignore[assignment]

    # Class attribute is available without initialization
    assert TempService.CONST == 9

    # Create with lifespan 2s
    _ = TempService.set(5, _lifespan=2)
    assert TempService.exists()
    assert TempService.value == 5  # type: ignore[attr-defined]

    # After expiry, attribute access should auto-refresh the instance
    current_time = 3
    assert not TempService.exists()
    value_before = TempService.value  # type: ignore[attr-defined]
    assert value_before == 5
    assert TempService.exists()


def test_temp_singleton_meta_dict_is_merged_and_readonly() -> None:
    current_time = 0

    def fake_now() -> int:
        return current_time

    class TempService(metaclass=TempSingletonMeta):
        CONST = 10
        shadowed = "class"

        def __init__(self) -> None:
            self.value = 2
            self.shadowed = "instance"

    TempService._now = staticmethod(fake_now)  # type: ignore[assignment]

    _ = TempService.set(_lifespan=0)
    merged_dict = TempService.__dict__

    assert isinstance(merged_dict, MappingProxyType)
    assert merged_dict["CONST"] == 10
    assert merged_dict["value"] == 2
    assert merged_dict["shadowed"] == "class"

    with pytest.raises(TypeError):
        merged_dict["x"] = 1  # type: ignore[index]


def test_temp_singleton_meta_delete_clears_instance_and_timestamp() -> None:
    current_time = 0

    def fake_now() -> int:
        return current_time

    class TempService(metaclass=TempSingletonMeta):
        def __init__(self, value: int) -> None:
            self.value = value

    TempService._now = staticmethod(fake_now)  # type: ignore[assignment]

    _ = TempService.set(1, _lifespan=10)
    assert TempService.exists()

    TempService.delete()
    assert not TempService.exists()

    # After delete, `get()` recreates the instance
    instance = TempService.get()
    assert instance.value == 1
    assert TempService.exists()


def test_temp_singleton_meta_rejects_negative_lifespan() -> None:
    class TempService(metaclass=TempSingletonMeta):
        def __init__(self, value: int) -> None:
            self.value = value

    with pytest.raises(ValueError, match="Lifespan must be non-negative"):
        TempService.set(1, _lifespan=-1)


## METACLASSES TEST MAIN #################################################################

__METACLASSES_TEST_MAIN_____________________________________ = ""


def main() -> None:
    """Tests the metaclasses."""
    configure_logging(level=logging.DEBUG)
    unittest.main()


if __name__ == "__main__":
    main()
