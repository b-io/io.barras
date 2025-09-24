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

import builtins
import inspect
import os
import sys
import types
import warnings
from typing import Annotated, Callable, get_args, get_origin, Literal

from nutil.struct.common import *

####################################################################################################
# COMMON SETTINGS
####################################################################################################

warnings.simplefilter(action="ignore", category=FutureWarning)

####################################################################################################
# COMMON CONSTANTS
####################################################################################################

__COMMON_CONSTANTS________________________________ = ""

DEFAULT_UNKNOWN = "unknown"


####################################################################################################
# COMMON CLASSES
####################################################################################################

__COMMON_CLASSES__________________________________ = ""


class Object:
    """An Object base class."""


####################################################################################################
# COMMON ACCESSORS
####################################################################################################

__COMMON_ACCESSORS________________________________ = ""


def get_exec_info() -> Optional[Type[BaseException]]:
    """Returns the exception type from the current exception info, or `None`."""
    return sys.exc_info()[0]


#########################

def get_frame(level: int = 0) -> types.FrameType:
    """Returns the caller’s `FrameType` at the specified `level` above this function."""
    if level < 0:
        raise ValueError("'level' must be non-negative")
    try:
        # +1 skips this helper’s own frame
        return sys._getframe(1 + level)
    except ValueError as e:
        raise IndexError(f"'level'={level} exceeds call stack depth") from e

def get_function_name(level: int = 0, *, qualified: bool = False, module: bool = False,
                      default: str = DEFAULT_UNKNOWN) -> str:
    """Returns the function name at the specified `level` (0 = immediate caller of this helper)."""
    try:
        f = get_frame(level + 1)
    except IndexError:
        return default

    name = f.f_code.co_name  # bare name

    if qualified:
        # Best-effort class qualification via locals
        cls = None
        loc = f.f_locals
        if "self" in loc:
            cls = loc["self"].__class__
        elif "cls" in loc and isinstance(loc["cls"], type):
            cls = loc["cls"]
        if cls is not None:
            name = f"{cls.__name__}.{name}"

    if module:
        mod = f.f_globals.get("__name__")
        if mod:
            name = f"{mod}.{name}"
    return name

def get_script_name(level: int = 0, default:str = DEFAULT_UNKNOWN) -> str:
    """Returns the script file name at the specified call `level`."""
    try:
        f = get_frame(level + 1)
        return os.path.basename(f.f_code.co_filename)
    except IndexError:
        return default

def get_line_number(level: int = 0) -> int:
    """Returns the line number at the specified call `level`."""
    try:
        return get_frame(level + 1).f_lineno
    except IndexError:
        return -1


#########################

def get_module_name(x: Any) -> str:
    """Returns the module name of the class of `x`."""
    return x.__class__.__module__


def get_class_name(x: Any) -> str:
    """Returns the class name of `x`."""
    return x.__class__.__name__


def get_full_class_name(x: Any) -> str:
    """Returns the fully-qualified class name of `x` (module + class)."""
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
    """Returns all public attribute names of `x` (via `dir(...)`)."""
    return [a for a in dir(x) if not a.startswith("_")]


#########################

T = TypeVar("T")

def get_type_name(t: Any) -> str:
    """Returns the simple type name for a `type`, or the instance type name for values."""
    return t.__name__ if isinstance(t, type) else type(t).__name__

def get_type_names(ts: Iterable[Any], separator: str = ", ") -> str:
    """Returns the simple type names of the specified types joined by the specified `separator`."""
    return separator.join(get_type_name(t) for t in ts)

#########################

def get_type_hints(x: Any) -> Dict[str, Any]:
    """
    Returns the resolved type hints for `x`, handling forward references and Python version
    differences robustly.

    Tries `inspect.get_annotations(obj, eval_str=True)` on Python 3.10+ and falls back to
    `typing.get_type_hints` when unavailable.

    Complexity:
        O(n) in the number of annotations, with constant-time dictionary operations.
    """
    try:
        return inspect.get_annotations(obj, eval_str=True)  # type: ignore[attr-defined]
    except (AttributeError, TypeError, NameError):
        try:
            from typing import get_type_hints
            glb = getattr(obj, "__globals__", None)
            return get_type_hints(obj, globalns=glb)
        except Exception:
            return {}

# TODO: use is_dict, is_list, is_set, etc. and make sure they check against collection ABC...
def matches_type_hints(value: Any, annotation: Any, sample_limit: int = 1) -> bool:
    """
    Determines whether the `value` conforms to the `annotation` (PEP 484/585/604), including
    parametrized containers and unions. Validates element types recursively.

    Notes:
        - Accepts `Any`, `Union[...]` (incl. `X | Y`), `Annotated[T, ...]`, `Literal[...]`,
          `Type[T]`, `tuple[int, ...]`, `Sequence[T]`, `Mapping[K, V]`, etc.
        - For iterables, it samples up to `_SAMPLE_LIMIT` elements (may consume from one-shot
          iterators).

    Complexity:
        Linear in container sizes; union checks are O(k) in the number of union branches.
    """
    if annotation is Any:
        return True

    origin = get_origin(annotation)
    args = get_args(annotation)

    if is_null(origin):
        # PEP 604 `X | Y` may surface as `types.UnionType` on some versions
        if getattr(annotation, "__module__", "") == "types" and getattr(annotation, "__qualname__", "") == "UnionType":
            return any(matches_type_hints(value, a) for a in args)
        try:
            return isinstance(value, annotation)
        except TypeError:
            return False
    elif origin is Annotated:
        return matches_type_hints(value, args[0])
    elif origin is Literal:
        return any(value == a for a in args)
    elif origin is Union:
        return any(matches_type_hints(value, a) for a in args)
    elif is_type(origin):
        return isinstance(value, type) and (not args or issubclass(value, args[0]))
    elif is_tuple(origin):
        if not isinstance(value, tuple):
            return False
        if len(args) == 2 and args[1] is Ellipsis:  # variable-length homogeneous tuple: `tuple[T, ...]`
            (elem_type, _) = args
            return all(matches_type_hints(v, elem_type) for v in value)
        if len(args) != len(value):
            return False
        return all(matches_type_hints(v, t) for v, t in zip(value, args))
    elif is_list(origin):
        if not is_list(value):
            return False
        if not args:
            return True
        (elem_type,) = args
        return all(matches_type_hints(v, elem_type) for v in value)
    elif is_sequence(origin):
        # excludes `tuple` (handled above)
        if not is_sequence(value):
            return False
        if not args:
            return True
        (elem_type,) = args
        return all(matches_type_hints(v, elem_type) for v in value)
    elif is_mapping(origin):
        if not is_mapping(value):
            return False
        if not args:
            return True
        key_type, val_type = args
        return all(
            matches_type_hints(k, key_type) and matches_type_hints(v, val_type)
            for k, v in value.items()
        )
    elif is_set(origin):
        if not is_set(value):
            return False
        if not args:
            return True
        (elem_type,) = args
        return all(matches_type_hints(v, elem_type) for v in value)
    elif is_iterable(origin):
        if not is_iterable(value):
            return False
        if not args:
            return True
        (elem_type,) = args
        # Check up to `sample_limit` items
        has_item, first_item, it = peek(value)
        if not has_item:
            return True
        if not matches_type_hints(first_item, elem_type):
            return False
        checked = 1
        for x in it:
            if not matches_type_hints(x, elem_type):
                return False
            checked += 1
            if checked >= sample_limit:
                break
        return True
    try:
        return isinstance(value, origin)
    except TypeError:
        return False


def flatten_expected_types(annotation: Any) -> Tuple[Any, ...]:
    """
    Normalizes the `annotation` into a `tuple` of acceptable alternatives for display or downstream
    formatting (for example, wrapping into `ExpectedTypeList` elsewhere).

    Examples:
        - `Union[int, str]`      → `(int, str)`
        - `Optional[int]`        → `(int, NoneType)`
        - `Annotated[T, ...]`    → same as `flatten_expected_types(T)`
        - `Literal[1, 2, "x"]`   → `(int, int, str)` (the raw literal values are not returned here)
        - `list[int]`            → `(list,)`  (container element typing is not expanded)
        - `int`                  → `(int,)`

    Complexity:
        O(k) for `Union`/`Annotated`/`Literal` unwrapping; otherwise O(1).
    """
    origin = get_origin(annotation)
    args = get_args(annotation)

    if origin is Annotated:
        return flatten_expected_types(args[0])
    elif origin is Literal:
        return tuple(type(v) for v in args) if args else (annotation,)
    elif origin is Union:
        return tuple(args)  # includes `NoneType` when `Optional[T]` is `Union[T, NoneType]`
    return (annotation,)

# • IO #############################################################################################

__COMMON_IO_ACCESSORS_____________________________ = ""


def get_path(path: str = ".") -> str:
    """Returns the absolute path for `path`."""
    return os.path.abspath(path)


def get_dir(path: str = ".", parent: Optional[bool] = None) -> str:
    """
    Returns the directory for `path`.

    Behavior:
    • If `path` is a file, returns the directory that contains the file.
    • If `path` is a directory and `parent` is `False`/`None`, returns the directory itself.
    • If `parent` is `True`, returns the parent directory of `path`.
    """
    abs_path = get_path(path)
    if parent is True:
        return os.path.dirname(abs_path)
    if os.path.isdir(abs_path):
        return abs_path  # directory itself
    return os.path.dirname(abs_path)  # directory containing the file


def get_filename(path: str = ".") -> str:
    """Returns the base file name of `path`."""
    return os.path.basename(get_path(path))


def get_extension(path: str = ".") -> str:
    """Returns the file extension of `path` without the leading dot; returns `""` if none."""
    return os.path.splitext(get_path(path))[1][1:]


def format_dir(directory: Optional[str]) -> str:
    """Returns `directory` normalized with exactly one trailing slash; returns `""` if falsy."""
    if not directory:
        return ""
    d = directory[:-1] if directory[-1:] in ("/", "\\") else directory
    return d + "/"


def find_path(filename: str, directory: Optional[str] = None, subdir: Optional[str] = None) -> str:
    """
    Returns a candidate absolute path for `filename`, optionally within `directory` and `subdir`.

    If `directory` is `None`, searches upward from the current directory until root, stopping when
    `filename` exists inside `subdir`. If not found, returns the last candidate path at root.
    """
    if is_null(directory):
        directory = get_dir(get_path())
        while (not is_file(format_dir(directory) + format_dir(subdir) + filename)) and (not is_root(directory)):
            directory = get_dir(directory, parent=True)
    elif is_file(directory):
        directory = get_dir(directory)
    return format_dir(directory) + format_dir(subdir) + filename


####################################################################################################
# COMMON CONVERTERS
####################################################################################################

__COMMON_CONVERTERS_______________________________ = ""


####################################################################################################
# COMMON PROCESSORS
####################################################################################################

__COMMON_PROCESSORS_______________________________ = ""

def forward(*args: Any) -> Any:
    """Returns the single argument if one is specified; otherwise returns the list of arguments."""
    if len(args) == 1:
        return args[0]
    return list(args)


def forward_element(*args: Any) -> Any:
    """Returns the single argument if one is specified; otherwise returns the tuple of arguments."""
    if len(args) == 1:
        return args[0]
    return tuple(args)


def invert(x: Any) -> Any:
    """Returns the logical negation of `x` using `numpy.logical_not` (vectorized for arrays)."""
    return np.logical_not(x)


# • SCALAR #########################################################################################

__COMMON_SCALAR_PROCESSORS________________________ = ""


def collapse(*args: Any, delimiter: str = "", append: bool = False) -> str:
    """Returns the string computed by joining the specified arguments with the specified delimiter."""
    return delimiter.join(map(str, to_list(*args))) + (delimiter if append else "")


def collist(*args: Any) -> str:
    """Returns the string computed by joining the specified arguments with a comma."""
    return collapse(*args, delimiter=",")


def paste(*args: Any) -> str:
    """Returns the string computed by joining the specified arguments with a space."""
    return collapse([s for s in map(str, to_list(*args)) if s != ""], delimiter=" ")


####################################################################################################
# COMMON VERIFIERS
####################################################################################################

__COMMON_VERIFIERS________________________________ = ""


def is_null(x: Any) -> bool:
    """Returns whether `x` is `None` or `NaN`."""
    return x is None or is_nan(x)


def is_all_null(*args: Any) -> bool:
    """Returns whether all specified arguments are `null`."""
    return all(is_null(arg) for arg in to_list(*args))


def is_all_not_null(*args: Any) -> bool:
    """Returns whether none of the specified arguments is `null`."""
    return not is_any_null(*args)


def is_any_null(*args: Any) -> bool:
    """Returns whether any of the specified arguments is `null`."""
    return any(is_null(arg) for arg in to_list(*args))


def is_any_not_null(*args: Any) -> bool:
    """Returns whether at least one of the specified arguments is not `null`."""
    return not is_all_null(*args)

#########################

def is_type(x: Any) -> bool:
    """Returns whether `x` is a `type`."""
    return isinstance(x, type)

#########################

def is_empty(x: Any) -> bool:
    """Returns whether `x` is semantically empty (null, zero-length, or empty frame)."""
    return is_null(x) or (
        (hasattr(x, "__len__") and len(x) == 0) or (is_frame(x) and count_cols(x) == 0)
    )


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


#########################

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

##################################################

def exists(name: str, *, level: int = 0) -> bool:
    """
    Returns whether an identifier `name` exists in the caller’s scope or builtins.
    0 = immediate caller of this helper; walks up `level` frames.
    """
    if level < 0:
        raise ValueError("'level' must be non-negative")
    try:
        f = get_frame(level + 1)
    except IndexError:
        return hasattr(builtins, name)

    return (name in f.f_locals) or (name in f.f_globals) or hasattr(builtins, name)

##################################################

def assert_element_types(
    name: str,
    value: Any,
    allowed_types: Tuple[Type[Any], ...],
    *,
    collection_predicate: Optional[Callable[[Any], bool]] = None,
) -> None:
    """
    Verifies that the `value` is a scalar instance of one of `allowed_types`. Collections are rejected.

    Behavior:
    • If `value` is a generic collection (default excludes str/bytes/bytearray/memoryview), raises `TypeError`.
    • Otherwise, if `allowed_types` is non-empty, `value` must be an instance of one of them.

    Complexity:
        O(len(allowed_types)) `isinstance` checks.
    """
    is_allowed_collection = collection_predicate or is_collection
    if is_allowed_collection(value):
        scalars = get_type_names(allowed_types)
        raise TypeError(
            f"'{name}' must be a scalar instance of {{{scalars}}}; got {type(value).__name__}"
            if scalars else
            f"'{name}' must be a scalar; got {type(value).__name__}"
        )
    if allowed_types and not isinstance(value, allowed_types):
        scalars = get_type_names(allowed_types)
        raise TypeError(f"'{name}' must be an instance of {{{scalars}}}; got {type(value).__name__}")


def assert_types(
    name: str,
    value: Any,
    allowed_types: Tuple[Type[Any], ...],
    *,
    allowed_collection_types: Tuple[Type[Any], ...] = (),
    collection_predicate: Optional[Callable[[Any], bool]] = None,
) -> None:
    """
    Verifies that the `value` conforms to the allowed scalar/container types.

    Behavior:
    • Returns if `value` is an instance of any type in `allowed_types` or `allowed_collection_types`.
    • If `value` is a generic collection (default excludes str/bytes/bytearray/memoryview), it MUST
      be an instance of one of `allowed_collection_types`, otherwise raises `TypeError`.
    • If `value` is a scalar and `allowed_types` is non-empty, it MUST be an instance of one of
      `allowed_types`, otherwise raises `TypeError`.

    Complexity:
        O(len(allowed_types) + len(allowed_collection_types)) `isinstance` checks.
    """
    for t in allowed_types + allowed_collection_types:
        if isinstance(value, t):
            return

    is_allowed_collection = collection_predicate or is_collection
    if is_allowed_collection(value):
        if allowed_collection_types:
            raise TypeError(
                f"'{name}' must be one of {{{get_type_names(allowed_collection_types)}}}; "
                f"got {type(value).__name__}"
            )
        raise TypeError(f"'{name}' must be a scalar; got {type(value).__name__}")

    if allowed_types and not isinstance(value, allowed_types):
        raise TypeError(
            f"'{name}' must be an instance of {{{get_type_names(allowed_types)}}}; "
            f"got {type(value).__name__}"
        )


# • IO #############################################################################################

__COMMON_IO_VERIFIERS_____________________________ = ""


def is_dir(path: str) -> bool:
    """Returns whether `path` is a directory."""
    return os.path.isdir(path)


def is_file(path: str) -> bool:
    """Returns whether `path` is a file."""
    return os.path.isfile(path)


def is_root(path: str) -> bool:
    """Returns whether `path` is a filesystem root."""
    return os.path.dirname(path) == path


# • STRUCT #########################################################################################

__COMMON_STRUCT_VERIFIERS_________________________ = ""


def has_filter(keys: Any = None, inclusion: Any = None, exclusion: Any = None) -> bool:
    """Returns whether at least one of `keys`, `inclusion`, or `exclusion` is non-empty."""
    return not is_all_empty(keys, inclusion, exclusion)
