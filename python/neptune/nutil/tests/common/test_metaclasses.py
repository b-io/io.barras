from types import MappingProxyType

import pytest

from nutil.metaclasses import FinalSingletonMeta, SingletonMeta, TempSingletonMeta

####################################################################################################
# METACLASS TEST CLASSES
####################################################################################################

__METACLASS_TEST_CLASSES__________________________ = ""


class FinalThing(metaclass=FinalSingletonMeta):
    """Represents a final singleton target used in tests."""

    CLASS_ATTR = "C"

    def __init__(self, x: int) -> None:
        # Store the specified value
        self.x = x
        self.counter = 1  # Count instance creations

    def foo(self) -> int:
        """Returns the stored value."""
        return self.x


class ReconfigurableThing(metaclass=SingletonMeta):
    """Represents a reconfigurable singleton target used in tests."""

    CLASS_ATTR = "C"

    def __init__(self, x: int = 0, y: int = 0) -> None:
        # Store the specified values
        self.x = x
        self.y = y
        self.counter = getattr(self, "counter", 0) + 1  # Count recreations


class TempThing(metaclass=TempSingletonMeta):
    """Represents a temporary singleton target used in tests."""

    CLASS_ATTR = "C"

    def __init__(self, x: int = 0) -> None:
        # Store the specified value
        self.x = x
        self.counter = getattr(self, "counter", 0) + 1  # Count recreations


####################################################################################################
# METACLASS TESTS
####################################################################################################

__METACLASS_TESTS_________________________________ = ""

# • FINAL SINGLETON ################################################################################


def test_final_singleton_uninitialized_access_raises():
    """Ensures accessing an instance attribute via the class before init raises a `RuntimeError`."""
    # Guarantee clean state in case of re-run
    if FinalThing.exists():
        # There is no delete for FinalSingletonMeta; emulate a fresh class by redefining
        class FreshFinal(metaclass=FinalSingletonMeta):
            CLASS_ATTR = "C"

            def __init__(self, x: int) -> None:
                self.x = x

        with pytest.raises(RuntimeError):
            _ = FreshFinal.x  # Forwarding before init -> RuntimeError
        # Initialize and ensure success
        FreshFinal(42)
        assert FreshFinal.x == 42
        return

    # Fresh path
    with pytest.raises(RuntimeError):
        _ = FinalThing.x  # Forwarding before init -> RuntimeError

    # After initialization, forwarding works
    FinalThing(7)
    assert FinalThing.x == 7
    assert FinalThing.foo() == 7  # Method forwarding via class


def test_final_singleton_class_first_and_merged_dict_dir():
    """Ensures class-first precedence, merged __dict__, read-only mapping, and dir() discoverability."""
    # Ensure initialized
    FinalThing(11)
    # Class-first precedence (class wins over instance on conflicts)
    FinalThing.CLASS_ATTR = "C_CLASS"  # Set on the class
    inst = FinalThing.get()
    inst.CLASS_ATTR = "C_INSTANCE"  # Shadow attribute on the instance

    # __dict__: merged and read-only; class keys win on conflicts
    d = FinalThing.__dict__
    assert isinstance(d, MappingProxyType)
    assert d["CLASS_ATTR"] == "C_CLASS"  # Class value wins
    assert "x" in d  # Instance attribute appears in merged mapping

    with pytest.raises(TypeError):
        d["new"] = 1  # MappingProxyType is read-only

    # __dir__: includes instance attribute names
    names = dir(FinalThing)
    assert "x" in names
    assert "foo" in names
    assert "CLASS_ATTR" in names


# • OVERWRITEABLE SINGLETON ########################################################################


def test_singletonmeta_recreate_on_call_with_new_args():
    """Ensures `__call__` recreates the singleton when new constructor arguments are specified."""
    # First call creates instance
    a = ReconfigurableThing(1, 2)
    assert (a.x, a.y) == (1, 2)
    first_counter = a.counter

    # Second call with new args recreates instance (counter increments)
    b = ReconfigurableThing(10, 20)
    assert (b.x, b.y) == (10, 20)
    assert b.counter == first_counter + 1
    assert b is ReconfigurableThing.get()


def test_singletonmeta_forwarding_and_get_set_reset_delete_exists():
    """Covers forwarding, `get()`, `set()`, `reset()`, `delete()`, and `exists()`."""
    # Ensure clean instance created with set()
    inst1 = ReconfigurableThing.set(3, 4)
    assert ReconfigurableThing.exists() is True
    assert ReconfigurableThing.x == 3  # Forwarding via class
    assert ReconfigurableThing.y == 4

    # Reset recreates with last stored args
    count_before = inst1.counter
    inst2 = ReconfigurableThing.reset()
    assert inst2.counter == count_before + 1
    assert ReconfigurableThing.x == 3
    assert ReconfigurableThing.y == 4

    # Calling with no args recreates with stored args
    inst3 = ReconfigurableThing()
    assert inst3.counter == inst2.counter + 1
    assert ReconfigurableThing.x == 3

    # Calling with new args updates stored args and recreates
    inst4 = ReconfigurableThing(9, 8)
    assert (inst4.x, inst4.y) == (9, 8)

    # `__dict__` merged and class-first
    d = ReconfigurableThing.__dict__
    assert isinstance(d, MappingProxyType)
    ReconfigurableThing.CLASS_ATTR = "C_CLASS"
    inst4.CLASS_ATTR = "C_INSTANCE"
    assert d["CLASS_ATTR"] == "C_CLASS"
    assert "x" in d

    # `__dir__` includes instance names
    names = dir(ReconfigurableThing)
    assert "x" in names and "y" in names

    # Delete drops instance
    ReconfigurableThing.delete()
    assert ReconfigurableThing.exists() is False

    # `get()` with no stored args will attempt to set() with empty args (works here: defaults)
    got = ReconfigurableThing.get()
    assert isinstance(got, ReconfigurableThing)
    assert (got.x, got.y) == (0, 0)


# • TEMPORARY SINGLETON (WITH EXPIRATION) ##########################################################


def test_tempsingleton_lifespan_and_auto_refresh(monkeypatch):
    """Ensures expiration is honored and auto-refresh occurs on access."""
    # Control time by monkeypatching _now()
    t = 1

    def fake_now() -> int:
        return t

    monkeypatch.setattr(TempThing, "_now", staticmethod(fake_now))

    # Create with lifespan 5 seconds
    a = TempThing.set(42, _lifespan=5)
    assert a.x == 42
    first_counter = a.counter
    assert TempThing.exists() is True

    # Before expiry
    t = 3
    assert TempThing.exists() is True
    assert TempThing.x == 42  # Forwarding via class does not refresh if not expired

    # After expiry
    t = 10  # age = 9 > 5
    assert TempThing.exists() is False  # exists() reports not expired instance
    # Access via class triggers get() → reset() → recreate
    x_val = TempThing.x
    assert x_val == 42
    assert TempThing.get().counter == first_counter + 1

    # `__dict__` merged and class-first
    d = TempThing.__dict__
    assert isinstance(d, MappingProxyType)
    TempThing.CLASS_ATTR = "C_CLASS"
    TempThing.get().CLASS_ATTR = "C_INSTANCE"
    assert d["CLASS_ATTR"] == "C_CLASS"
    assert "x" in d

    # `__dir__` includes instance names
    names = dir(TempThing)
    assert "x" in names


def test_tempsingleton_set_negative_lifespan_raises():
    """Ensures a negative lifespan is rejected."""
    with pytest.raises(ValueError):
        TempThing.set(1, _lifespan=-1)
