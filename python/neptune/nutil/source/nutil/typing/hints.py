#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide typing hints utilities.
########################################################################################################################

from __future__ import annotations

import inspect
from typing import Annotated, get_args, get_origin

from nutil.common import *

## TYPING HINTS ##########################################################################

__TYPING_HINTS______________________________________________ = ""


def expected_for_display(annotation: Any) -> Union[Type[Any], Tuple[Any, ...]]:
    """
    Adapts the flattened alternatives to a shape accepted by `create_type_error`.
    """
    flat = flatten_expected_types(annotation)
    return flat[0] if len(flat) == 1 else flat


def get_type_hints(x: Any) -> Dict[str, Any]:
    """
    Returns the resolved type hints for `x`, handling forward references and Python version
    differences robustly.

    Tries `inspect.get_annotations(x, eval_str=True)` on Python 3.10+ and falls back to
    `typing.get_type_hints` when unavailable.

    Complexity:
        O(n) in the number of annotations, with constant-time dictionary operations.
    """
    try:
        return inspect.get_annotations(x, eval_str=True)
    except (AttributeError, TypeError, NameError):
        try:
            from typing import get_type_hints

            glb = getattr(x, "__globals__", None)
            return get_type_hints(x, globalns=glb)
        except Exception:
            return {}


def flatten_expected_types(annotation: Any) -> Tuple[Any, ...]:
    """
    Normalizes the `annotation` into a `tuple` of acceptable alternatives for display.

    Dispatch:
        • `Union[int, str]`      → `(int, str)`
        • `Optional[int]`        → `(int, NoneType)`
        • `Annotated[T, ...]`    → same as `_flatten_expected_types(T)`
        • `Literal[1, 2, "x"]`   → `(int, int, str)`
        • `list[int]`            → `(list,)`
        • `int`                  → `(int,)`

    Returns:
        A `tuple` of acceptable types.
    """
    origin = get_origin(annotation)
    args = get_args(annotation)

    if origin is Annotated:
        return flatten_expected_types(args[0])
    elif origin is Literal:
        return tuple(type(v) for v in args) if args else (annotation,)
    elif origin is Union:
        return tuple(args)
    return (annotation,)


def matches_type_hints(
    value: Any,
    annotation: Any,
    *,
    sample_limit: int = 1,
    max_depth: int = 0,
    _depth: int = 0,
) -> bool:
    """
    Determines whether the `value` conforms to the `annotation` (PEP 484/585/604), with depth control.

    Notes:
        • Supports `Any`, `Union[...]` / `X | Y`, `Annotated[T, ...]`, `Literal[...]`, `Type[T]`,
          variable-length `tuple[T, ...]`, `Sequence[T]`, `Mapping[K, V]`, and generic `Iterable[T]`.
        • Samples up to `sample_limit` items for iterables to avoid exhausting one-shot producers.
        • Treats `max_depth == 0` as unlimited recursion; otherwise, once `_depth >= max_depth`, the validator
          checks only the outer container type at that level.

    Args:
        value: The value to validate.
        annotation: The type annotation to validate against.
        sample_limit: The maximum number of iterable elements to check.
        max_depth: The maximum container-nesting depth to validate (0 = unlimited).
        _depth: The internal recursion counter.

    Returns:
        `True` if the `value` matches the `annotation`, otherwise `False`.

    Complexity:
        Linear in container sizes (bounded by `sample_limit` for iterables) and O(k) in the number of union branches.
    """
    if annotation is Any:
        return True

    origin = get_origin(annotation)
    args = get_args(annotation)
    should_recurse = (max_depth == 0) or (_depth < max_depth)

    # Handle a bare type or a PEP 604 union surfaced as `types.UnionType`
    if origin is None:
        if (
            getattr(annotation, "__module__", "") == "types"
            and getattr(annotation, "__qualname__", "") == "UnionType"
        ):
            return any(
                matches_type_hints(
                    value, a, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth
                )
                for a in args
            )
        try:
            return isinstance(value, annotation)
        except TypeError:
            return False

    # Handle the wrappers that do not increase the container depth
    elif origin is Annotated:
        return matches_type_hints(
            value, args[0], sample_limit=sample_limit, max_depth=max_depth, _depth=_depth
        )
    elif origin is Literal:
        return any(value == a for a in args)
    elif origin is Union:
        return any(
            matches_type_hints(
                value, a, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth
            )
            for a in args
        )

    # Validate a `Type[T]`
    elif is_type(origin):
        return isinstance(value, type) and (not args or issubclass(value, args[0]))

    # Validate the tuples
    elif is_tuple(origin):
        if not isinstance(value, tuple):
            return False
        if not should_recurse:
            return True  # accepts the outer tuple only
        if len(args) == 2 and args[1] is Ellipsis:
            (elem_type, _) = args
            # Validate a variable-length `tuple[T, ...]`
            return all(
                matches_type_hints(
                    v, elem_type, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth + 1
                )
                for v in value
            )
        if len(args) != len(value):
            return False
        return all(
            matches_type_hints(
                v, t, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth + 1
            )
            for v, t in zip(value, args)
        )

    # Validate the lists and the generic sequences (excluding the tuples)
    elif is_list(origin):
        if not is_list(value):
            return False
        if not args or not should_recurse:
            return True
        (elem_type,) = args
        return all(
            matches_type_hints(
                v, elem_type, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth + 1
            )
            for v in value
        )

    # Validate the mappings
    elif is_mapping(origin):
        if not is_mapping(value):
            return False
        if not args or not should_recurse:
            return True
        key_type, val_type = args
        return all(
            matches_type_hints(
                k, key_type, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth + 1
            )
            and matches_type_hints(
                v, val_type, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth + 1
            )
            for k, v in value.items()
        )

    # Validate the sequences
    elif is_sequence(origin):
        if not is_sequence(value):
            return False
        if not args or not should_recurse:
            return True
        (elem_type,) = args
        return all(
            matches_type_hints(
                v, elem_type, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth + 1
            )
            for v in value
        )

    # Validate the sets
    elif is_set(origin):
        if not is_set(value):
            return False
        if not args or not should_recurse:
            return True
        (elem_type,) = args
        return all(
            matches_type_hints(
                v, elem_type, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth + 1
            )
            for v in value
        )

    # Validate the generic iterables with sampling
    elif is_iterable(origin):
        if not is_iterable(value):
            return False
        if not args or not should_recurse:
            return True
        (elem_type,) = args
        has_item, first_item, it = peek(value)
        if not has_item:
            return True
        if not matches_type_hints(
            first_item, elem_type, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth + 1
        ):
            return False
        checked = 1
        for x in it:
            if not matches_type_hints(
                x, elem_type, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth + 1
            ):
                return False
            checked += 1
            if checked >= sample_limit:
                break
        return True

    # Handle the fallback for the protocols or the concrete origins
    try:
        return isinstance(value, origin)
    except TypeError:
        return False


def resolve_type_hints(obj: Any) -> Dict[str, Any]:
    """
    Returns the resolved type hints for `obj`, handling forward references robustly.

    Prefers `inspect.get_annotations(obj, eval_str=True)` on Python 3.10+, and falls back to `typing.get_type_hints`
    with the function globals.

    Args:
        obj: The object whose annotations to resolve.

    Returns:
        A `dict` of parameter names (and optionally `"return"`) to resolved annotations.
    """
    try:
        return inspect.get_annotations(obj, eval_str=True)
    except (AttributeError, TypeError, NameError):
        from typing import get_type_hints

        glb = getattr(obj, "__globals__", None)
        try:
            return get_type_hints(obj, globalns=glb)
        except Exception:
            return {}
