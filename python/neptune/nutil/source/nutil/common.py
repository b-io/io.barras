#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common utilities.
########################################################################################################################

from __future__ import annotations

import builtins
import os
import sys
import types
import warnings

from nutil.struct.common import *

__COMMON_SETTINGS_________________________________________________________________________ = ""


warnings.simplefilter(action="ignore", category=FutureWarning)


__COMMON_CONSTANTS________________________________________________________________________ = ""


### DEFAULTS ###############################################

DEFAULT_UNKNOWN = "unknown"


__COMMON_CLASSES__________________________________________________________________________ = ""


class Object:
    """An Object base class."""


__COMMON_ACCESSORS________________________________________________________________________ = ""


def get_exec_info() -> Optional[Type[BaseException]]:
    """Returns the exception type from the current exception info, or `None`."""
    return sys.exc_info()[0]


##############################


def get_frame(level: int = 0) -> types.FrameType:
    """Returns the caller `FrameType` at the specified `level` above this function."""
    if level < 0:
        raise ValueError("'level' must be non-negative")
    try:
        return sys._getframe(1 + level)  # adds 1 to skip this frame
    except ValueError as e:
        raise IndexError(f"'level'={level} exceeds call stack depth") from e


def get_function_name(
    level: int = 0,
    *,
    default: str = DEFAULT_UNKNOWN,
    module: bool = False,
    use_qualified_name: bool = False,
) -> str:
    """Returns the function name at the specified `level` (0 = immediate caller of this helper)."""
    try:
        f = get_frame(level + 1)  # adds 1 to skip this frame
    except IndexError:
        return default

    name = f.f_code.co_name  # bare name

    if use_qualified_name:
        # Best-effort class qualification via locals
        cls = None
        loc = f.f_locals
        if "self" in loc:
            cls = loc["self"].__class__
        elif "cls" in loc and isinstance(loc["cls"], type):
            cls = loc["cls"]
        if not is_null(cls):
            name = f"{cls.__name__}.{name}"

    if module:
        mod = f.f_globals.get("__name__")
        if mod:
            name = f"{mod}.{name}"
    return name


def get_script_name(level: int = 0, default: str = DEFAULT_UNKNOWN) -> str:
    """Returns the script file name at the specified call `level`."""
    try:
        f = get_frame(level + 1)  # adds 1 to skip this frame
        return os.path.basename(f.f_code.co_filename)
    except IndexError:
        return default


def get_line_number(level: int = 0) -> int:
    """Returns the line number at the specified call `level`."""
    try:
        return get_frame(level + 1).f_lineno  # adds 1 to skip this frame
    except IndexError:
        return -1


##############################


def get_module_name(x: Any) -> str:
    """Returns the module name of the class of `x`."""
    return x.__class__.__module__


def get_class_name(x: Any) -> str:
    """Returns the class name of `x`."""
    return x.__class__.__name__


def get_full_class_name(x: Any) -> str:
    """Returns the fully qualified class name of `x` (module + class)."""
    module_name = get_module_name(x)
    if is_null(module_name) or module_name == get_module_name(str):
        return get_class_name(x)
    return collapse(module_name, ".", get_class_name(x))


def get_attributes(x: Any) -> List[str]:
    """Returns the public attribute names of `x` that are stored in the instance dictionary."""
    try:
        return [a for a in vars(x) if not a.startswith("_")]
    except TypeError:
        return [a for a in dir(x) if not a.startswith("_")]  # falls back to `dir`


def get_all_attributes(x: Any) -> List[str]:
    """Returns all public attribute names of `x` (via `dir(…)`)."""
    return [a for a in dir(x) if not a.startswith("_")]


__COMMON_IO_ACCESSORS_______________________________________ = ""


def get_path(path: str = ".") -> str:
    """Returns the absolute path for `path`."""
    return os.path.abspath(path)


def get_dir(path: str = ".", parent: Optional[bool] = None) -> str:
    """
    Returns the directory for `path`.

    Dispatch:
        • If `path` is a file, returns the directory that contains the file.
        • If `path` is a directory and `parent` is `False`/`None`, returns the directory itself.
        • If `parent` is `True`, returns the parent directory of `path`.
    """
    abs_path = get_path(path)
    if not is_null(parent):
        return os.path.dirname(abs_path)
    if os.path.isdir(abs_path):
        return abs_path  # the directory itself
    return os.path.dirname(abs_path)  # the directory containing the file


def get_filename(path: str = ".") -> str:
    """Returns the base file name of `path`."""
    return os.path.basename(get_path(path))


def get_extension(path: str = ".") -> str:
    """Returns the file extension of `path` without the leading dot; returns `""` if none."""
    return os.path.splitext(get_path(path))[1][1:]


def format_dir(dir: Optional[str]) -> str:
    """Returns `dir` normalized with exactly one trailing slash; returns `""` if falsy."""
    if is_empty(dir):
        return ""
    if dir[-1:] in ("/", "\\"):
        dir = dir[:-1]
    return dir + "/"


__COMMON_IO_FINDERS_________________________________________ = ""


def find_path(filename: str, dir: Optional[str] = None, subdir: Optional[str] = None) -> str:
    """
    Returns a candidate absolute path for `filename`, optionally within `dir` and `subdir`.

    If `dir` is null, searches upward from the current directory until root, stopping when
    `filename` exists inside `subdir`. If not found, returns the last candidate path at root.
    """
    if is_null(dir):
        dir = get_dir(get_path())
        while (not is_file(format_dir(dir) + format_dir(subdir) + filename)) and (not is_root(dir)):
            dir = get_dir(dir, parent=True)
    elif is_file(dir):
        dir = get_dir(dir)
    return format_dir(dir) + format_dir(subdir) + filename


__COMMON_PROCESSORS_______________________________________________________________________ = ""


def deep_hash(x: Any, default: int = 0) -> int:
    """
    Returns the structural hash value of `x`.

    Uses a recursive strategy for containers so that unhashable but structurally
    equal objects (like lists, dicts, nested structures) produce the same hash.
    """
    # Explicit `None` handling
    if is_null(x):
        return default

    # Use a callable `hash` method on `x` if present
    elif has_callable(x, "hash"):
        return x.hash()

    # Handle scalars: delegate directly to the built-in hash
    elif is_scalar(x):
        return builtins.hash(x)

    # Handle mappings: order-independent, recurse on keys and values
    elif is_mapping(x):
        # (key_hash, value_hash) pairs, sorted by key_hash for stability
        items = tuple(sorted((deep_hash(k, default), deep_hash(v, default)) for k, v in x.items()))
        # Include the type to avoid collisions between different mapping types
        return builtins.hash((type(x), items))

    # Handle tuples: order-dependent, recurse on elements
    elif is_tuple(x):
        return builtins.hash(tuple(deep_hash(v, default) for v in x))

    # Handle other iterables (lists, sets, etc.): order-dependent but type-tagged
    elif is_iterable(x):
        return builtins.hash((type(x), tuple(deep_hash(v, default) for v in x)))

    # Fall back to the built-in hash or to a type-tagged representation
    try:
        return builtins.hash(x)
    except TypeError:
        return builtins.hash((type(x).__qualname__, repr(x)))


def forward(*args: Any) -> Any:
    """
    Returns the single argument if one is specified; otherwise returns the `list` of arguments.
    """
    if len(args) == 1:
        return args[0]
    return list(args)


def forward_element(*args: Any) -> Any:
    """
    Returns the single argument if one is specified; otherwise returns the `tuple` of arguments.
    """
    if len(args) == 1:
        return args[0]
    return tuple(args)


def invert(x: Any) -> Any:
    """Returns the logical negation of `x` using `numpy.logical_not` (vectorized for arrays)."""
    return np.logical_not(x)


__COMMON_SCALAR_PROCESSORS__________________________________ = ""


def collapse(
    *args: Any,
    default: str = "",
    delimiter: str = "",
    strip: Optional[str] = None,
) -> str:
    """
    Returns the string computed by joining the specified arguments with the specified delimiter.

    Args:
        *args: The values to be converted to strings and joined.
        default: The string used when a value is null.
        delimiter: The delimiter inserted between the collapsed values.
        strip: The characters to strip from both ends of each value before joining.
            If `None`, no stripping is performed.

    Returns:
        The collapsed string.
    """
    return delimiter.join(map(lambda x: stringify(x, default=default, strip=strip), to_list(*args)))


def collist(*args: Any, default: str = "", strip: Optional[str] = None) -> str:
    """Returns the string computed by joining the specified arguments with a comma."""
    return collapse(*args, default=default, delimiter=",", strip=strip)


def paste(*args: Any, default: str = "", strip: Optional[str] = None) -> str:
    """
    Returns the string computed by joining the specified arguments with a space.

    If `default` is empty, falsy arguments are removed before collapsing.
    """
    return " ".join(s for s in (stringify(x, default=default, strip=strip) for x in to_list(*args)) if s)


def stringify(x: Any, *, default: str = "", strip: Optional[str] = None) -> str:
    """Returns the string representation of `x`."""
    s = str(x) if not is_null(x) else default
    if not is_null(strip):
        s = s.strip(strip)
    return s


__COMMON_VALIDATORS_______________________________________________________________________ = ""


def is_null(x: Any) -> bool:
    """Returns whether `x` is `None` or `NaN`."""
    return x is None or is_nan(x)


def is_all_null(*args: Any) -> bool:
    """Returns whether all specified arguments are null."""
    return all(is_null(arg) for arg in to_list(*args))


def is_all_not_null(*args: Any) -> bool:
    """Returns whether none of the specified arguments is null."""
    return not is_any_null(*args)


def is_any_null(*args: Any) -> bool:
    """Returns whether any of the specified arguments is null."""
    return any(is_null(arg) for arg in to_list(*args))


def is_any_not_null(*args: Any) -> bool:
    """Returns whether at least one of the specified arguments is not null."""
    return not is_all_null(*args)


##############################


def is_type(x: Any) -> bool:
    """Returns whether `x` is a `type`."""
    return isinstance(x, type)


##############################


def is_empty(x: Any) -> bool:
    """Returns whether `x` is semantically empty (null, zero-length, or empty frame)."""
    return is_null(x) or ((hasattr(x, "__len__") and len(x) == 0) or (is_frame(x) and count_cols(x) == 0))


def is_all_empty(*args: Any) -> bool:
    """Returns whether all specified arguments are empty."""
    return all(is_empty(arg) for arg in to_list(*args))


def is_all_not_empty(*args: Any) -> bool:
    """Returns whether none of the specified arguments is empty."""
    return not is_any_empty(*args)


def is_any_empty(*args: Any) -> bool:
    """Returns whether any of the specified arguments is empty."""
    return any(is_empty(arg) for arg in to_list(*args))


def is_any_not_empty(*args: Any) -> bool:
    """Returns whether at least one of the specified arguments is not empty."""
    return not is_all_empty(*args)


##############################


def is_all_value(value: Any, *args: Any) -> bool:
    """Returns whether all specified arguments equal `value`."""
    return all(value == arg for arg in to_list(*args))


def is_all_not_value(value: Any, *args: Any) -> bool:
    """Returns whether none of the specified arguments equals `value`."""
    return not is_any_value(value, *args)


def is_any_value(value: Any, *args: Any) -> bool:
    """Returns whether any of the specified arguments equals `value`."""
    return any(value == arg for arg in to_list(*args))


def is_any_not_value(value: Any, *args: Any) -> bool:
    """Returns whether at least one of the specified arguments does not equal `value`."""
    return not is_all_value(value, *args)


############################################################


def exists(name: str, *, level: int = 0) -> bool:
    """
    Returns whether an identifier `name` exists in the caller scope or builtins.
    Walks up `level` frames (`0` for the immediate caller).
    """
    if level < 0:
        raise ValueError("'level' must be non-negative")
    try:
        f = get_frame(level + 1)  # adds 1 to skip this frame
    except IndexError:
        return hasattr(builtins, name)

    return (name in f.f_locals) or (name in f.f_globals) or hasattr(builtins, name)


__COMMON_IO_VALIDATORS______________________________________ = ""


def is_dir(path: str) -> bool:
    """Returns whether `path` is a directory."""
    return os.path.isdir(path)


def is_file(path: str) -> bool:
    """Returns whether `path` is a file."""
    return os.path.isfile(path)


def is_root(path: str) -> bool:
    """Returns whether `path` is a filesystem root."""
    return os.path.dirname(path) == path


__COMMON_STRUCT_VALIDATORS__________________________________ = ""


def has_filter(keys: Any = None, inclusion: Any = None, exclusion: Any = None) -> bool:
    """Returns whether at least one of `keys`, `inclusion`, or `exclusion` is non-empty."""
    return not is_all_empty(keys, inclusion, exclusion)
