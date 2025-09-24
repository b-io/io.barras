#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains common utility functions
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

from nutil.struct.collection.common import *
from nutil.struct.table.common import *
from nutil.struct.tuple.common import *

####################################################################################################
# COMMON STRUCT CONVERTERS
####################################################################################################

__COMMON_STRUCT_CONVERTERS________________________ = ""


# • COLLECTION ###################################


def to_array(*args: Any, element_type: Optional[Union[np.dtype[Any], type]] = None) -> np.ndarray:
    """
    Returns an `array` from the specified arguments.

    • Single argument:
      – If GroupBy: unwraps to object or groups.
      – If the object has a `to_array` method, delegates to it.
      – If a scalar, wraps it as a single-element `array`.
      – If already an `array`, returns it unchanged (dtype may be enforced if specified).
      – If a `Mapping`, returns its values as an `array`.
      – If an `Iterable`, converts it to an `array`.
      – Otherwise, wraps it in a single-element `array`.
    • Multiple arguments: packs them into an `array`.
    """
    if len(args) == 1:
        arg = args[0]
        if is_group_by(arg):
            arg = arg.obj if getattr(arg, "axis", 0) == 0 else arg.groups
        if is_callable(arg, "to_array"):
            return arg.to_array()
        elif is_scalar(arg):
            return np.array([arg], dtype=element_type)
        elif is_array(arg):
            return arg.astype(element_type, copy=False) if element_type is not None else arg
        elif is_mapping(arg):
            return np.array(list(arg.values()), dtype=element_type)
        elif is_iterable(arg):
            return np.array(arg, dtype=element_type)
        return np.array([arg], dtype=element_type)
    return np.array(args, dtype=element_type)


def unarray(a: Any) -> Union[Any, tuple]:
    """
    Unwraps a NumPy `array`:

    – If `a` is a 0-dim array, returns its scalar value.
    – If `a` is a 1-element array, returns the element.
    – If `a` is a multi-element array, returns a `tuple` of its values.
    – Otherwise, returns `a` unchanged.
    """
    if is_array(a):
        if a.ndim == 0:  # NumPy scalar container
            return a.item()
        if len(a) == 1:
            return a[0]
        return tuple(a)
    return a


#########################


def to_dict(*args: Any) -> Dict[Any, Any]:
    """
    Returns a `dict` from the specified arguments.

    • Single argument:
      – If GroupBy: unwraps to object or groups.
      – If the object has `to_dict`, delegates to it.
      – If a scalar, enumerates characters/bytes starting at 0.
      – If a `Mapping`, materializes a real dict (shallow).
      – If an `Iterable` of pairs, converts to a dict of key–value pairs.
      – If a generic `Iterable`, enumerates values starting at 0.
      – Otherwise, returns `{0: arg}`.
    • Multiple arguments:
      – If all arguments are `(key, value)` pairs, builds a dict from them.
      – Otherwise, enumerates the arguments starting at 0.
    """
    if len(args) == 1:
        arg = args[0]
        if is_group_by(arg):
            arg = arg.obj if getattr(arg, "axis", 0) == 0 else arg.groups
        elif is_callable(arg, "to_dict"):
            return arg.to_dict()
        elif is_scalar(arg):
            return {i: ch for i, ch in enumerate(arg)}
        elif is_mapping(arg):
            return arg if isinstance(arg, dict) else dict(arg)
        elif is_iterable(arg):
            if is_iterable_of_pairs(arg):
                return dict(arg)
            return {i: v for i, v in enumerate(arg)}
        return {0: arg}
    if is_iterable_of_pairs(args):
        return dict(args)
    return {i: v for i, v in enumerate(args)}


def undict(d):
    if is_dict(d):
        if len(d) == 1:
            return d[0]
        return tuple(d)
    return d


def to_list(*args) -> List[Any]:
    """
    Returns a `list` from the specified arguments.

    • Single argument:
      – If the object has a `to_list` method, delegates to it.
      – If a `bytes` or a `str`, wraps it as a single-element `list`.
      – If a `dict`, returns its values as a `list`.
      – If already a `list`, returns it unchanged.
      – If an `Iterable`, converts it to a `list`.
      – Otherwise, wraps it in a single-element `list`.
    • Multiple arguments: packs them into a `list`.
    """
    if len(args) == 1:
        arg = args[0]
        if is_callable(arg, "to_list"):
            return arg.to_list()
        elif isinstance(arg, (bytes, str)):
            return [arg]
        elif isinstance(arg, dict):
            return list(arg.values())
        elif isinstance(arg, list):
            return arg
        elif is_iterable(arg):
            return list(arg)
        return [arg]
    return list(args)


def to_set(*args) -> Set[Any]:
    """
    Returns a `set` from the specified arguments.

    • Single argument:
      – If the object has a `to_set` method, delegates to it.
      – If a `bytes` or a `str`, wraps it as a single-element `set`.
      – If a `dict`, returns its values as a `set`.
      – If already a `set`, returns it unchanged.
      – If an `Iterable`, converts it to a `set`.
      – Otherwise, wraps it in a single-element `set`.
    • Multiple arguments: packs them into a `set`.
    """
    if len(args) == 1:
        arg = args[0]
        if is_callable(arg, "to_set"):
            return arg.to_set()
        elif isinstance(arg, (bytes, str)):
            return {arg}
        elif isinstance(arg, dict):
            return set(arg.values())
        elif isinstance(arg, set):
            return arg
        elif is_iterable(arg):
            return set(arg)
        return {arg}
    return set(args)


# • TUPLE ########################################


def to_tuple(*args) -> Tuple[Any, ...]:
    """
    Returns a `tuple` from the specified arguments.

    • Single argument:
      – If the object has a `to_tuple` method, delegates to it.
      – If a `bytes` or a `str`, wraps it as a single-element `tuple`.
      – If a `dict`, returns its values as a `tuple`.
      – If already a `tuple`, returns it unchanged.
      – If an `Iterable`, converts it to a `tuple`.
      – Otherwise, wraps it in a single-element `tuple`.
    • Multiple arguments: packs them into a `tuple`.
    """
    if len(args) == 1:
        arg = args[0]
        if is_callable(arg, "to_tuple"):
            return arg.to_tuple()
        elif isinstance(arg, (bytes, str)):
            return (arg,)
        elif isinstance(arg, dict):
            return tuple(arg.values())
        elif isinstance(arg, tuple):
            return arg
        elif is_iterable(arg):
            return tuple(arg)
        return (arg,)
    return tuple(args)


####################################################################################################
# COMMON STRUCT VERIFIERS
####################################################################################################

__COMMON_STRUCT_VERIFIERS_____________________ = ""


def is_struct(x):
    return is_collection(x) or is_table(x) or is_tuple(x)


#########################


def is_multidimensional(x):
    return is_table(x) or is_array(x)


def is_subscriptable(x):
    return hasattr(x, "__getitem__")


#########################


def is_callable(x: Any, name: str) -> bool:
    """
    Returns whether `x` has an attribute with the specified name and
    whether that attribute is callable.

    Example:
        if is_callable(df, "to_dict"):
            return df.to_dict()
    """
    return callable(getattr(x, name, None))


##################################################


def has_index(c):
    return is_array(c) or is_index(c) or is_sequence(c)


#########################


def compare_length(x: Any, n: int, op) -> bool:
    """
    Compares the length of a collection to a specified number using the given operator.
    Returns False if x is not a valid collection or has no length.
    """
    if not is_struct(x):
        return False
    try:
        return op(len(x), n)
    except TypeError:
        return False


def has_length_ge(x: Any, n: int = 1) -> bool:
    """Returns whether len(x) >= n and x is a collection."""
    return compare_length(x, n, operator.ge)


def has_length_gt(x: Any, n: int = 1) -> bool:
    """Returns whether len(x) > n and x is a collection."""
    return compare_length(x, n, operator.gt)


def has_length_le(x: Any, n: int = 1) -> bool:
    """Returns whether len(x) <= n and x is a collection."""
    return compare_length(x, n, operator.le)


def has_length_lt(x: Any, n: int = 1) -> bool:
    """Returns whether len(x) < n and x is a collection."""
    return compare_length(x, n, operator.lt)
