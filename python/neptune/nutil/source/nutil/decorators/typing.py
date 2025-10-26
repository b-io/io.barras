#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# TYPING DECORATORS ####################################################################################################
# Goal
#   Provide a decorator that enforces a function’s type annotations at runtime, with optional depth control for
#   nested containers and bounded sampling for iterables.
#
# Terminology
#   • an `annotation` is a type hint attached to a function parameter or to the special `return` slot.
#   • a `container` is a parametrized collection like `list[T]`, `dict[K, V]`, `tuple[...]`, `Sequence[T]`, etc.
#
# Behavior
#   • the decorator validates only annotated parameters; unannotated parameters are ignored.
#   • the validation supports `Annotated[T, ...]`, `Union[...]` / `X | Y`, `Literal[...]`, `Type[T]`, and containers.
#   • the iterable validation samples up to `sample_limit` elements via `peek(...)` to avoid exhausting one-shot iterators.
#   • the recursion depth is controlled by `max_depth` (0 → unlimited). When the limit is reached, the validator checks
#     only the outer container type at that level.
########################################################################################################################

from __future__ import annotations

import inspect
import logging
from functools import wraps
from typing import (
    Annotated,
    Any,
    Callable,
    Dict,
    get_args,
    get_origin,
    Literal,
    Tuple,
    Type,
    TypeVar,
    Union,
)

from nutil.common import (
    is_iterable,
    is_list,
    is_mapping,
    is_sequence,
    is_set,
    is_tuple,
    is_type,
    peek,
)
from nutil.exceptions import create_type_error, ErrorList, ExpectedTypeList, get_function_name

## PUBLIC DECORATORS ###################################################################################################

__TYPING_DECORATORS___________________________________________ = ""

F = TypeVar("F", bound=Callable[..., Any])


def resolve_type_hints(obj: Any) -> Dict[str, Any]:
    """
    Returns the resolved type hints for `obj`, handling forward references robustly.

    Prefers `inspect.get_annotations(obj, eval_str=True)` on Python 3.10+, and falls back to `typing.get_type_hints`
    with the function globals.

    Args:
        obj: The object whose annotations to resolve.

    Returns:
        A `dict` of parameter names (and optionally `'return'`) to resolved annotations.
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
    if origin is Annotated:
        return matches_type_hints(
            value, args[0], sample_limit=sample_limit, max_depth=max_depth, _depth=_depth
        )
    if origin is Literal:
        return any(value == a for a in args)
    if origin is Union:
        return any(
            matches_type_hints(
                value, a, sample_limit=sample_limit, max_depth=max_depth, _depth=_depth
            )
            for a in args
        )

    # Validate a `Type[T]`
    if is_type(origin):
        return isinstance(value, type) and (not args or issubclass(value, args[0]))

    # Validate the tuples
    if is_tuple(origin):
        if not isinstance(value, tuple):
            return False
        if not should_recurse:
            return True  # accepts the outer tuple only
        if len(args) == 2 and args[1] is Ellipsis:
            (elem_type, _) = args
            # validates a variable-length `tuple[T, ...]`
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
    if is_list(origin):
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

    if is_sequence(origin):
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

    # Validate the mappings
    if is_mapping(origin):
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

    # Validate the sets
    if is_set(origin):
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
    if is_iterable(origin):
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


def flatten_expected_types(annotation: Any) -> Tuple[Any, ...]:
    """
    Normalizes the `annotation` into a `tuple` of acceptable alternatives for display.

    Dispatch:
        • `Union[int, str]`      → `(int, str)`
        • `Optional[int]`        → `(int, NoneType)`
        • `Annotated[T, ...]`    → same as `flatten_expected_types(T)`
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
    if origin is Literal:
        return tuple(type(v) for v in args) if args else (annotation,)
    if origin is Union:
        return tuple(args)
    return (annotation,)


def _expected_for_display(annotation: Any) -> Union[Type[Any], Tuple[Any, ...]]:
    """
    Adapts the flattened alternatives to a shape accepted by `create_type_error`.
    """
    flat = flatten_expected_types(annotation)
    return flat[0] if len(flat) == 1 else flat


def typesafe(
    *,
    mode: str = "raise",
    logger: logging.Logger | None = None,
    sample_limit: int = 1,
    max_depth: int = 0,
) -> Callable[[F], F]:
    """
    Decorates a function to enforce its type annotations at runtime.

    Summary:
        Validates that each annotated input matches the declared type in the function signature,
        including unions, optionals, and parametrized containers (the `list`, the `dict`, the `tuple`,
        the `set`, the `Mapping`, the `Sequence`, and the `Iterable`). Collects all mismatches and emits
        a single error via `create_type_error`.

        Depth control:
            Treats `max_depth == 0` as unlimited recursion. Otherwise, once the limit is reached, the validator
            checks only the outer container type for deeper levels.

    Args:
        mode: The validation mode, either `'raise'` to raise a `TypeError`, or `'suggest'` to log a warning.
        logger: The logger for `'suggest'` mode (defaults to `logging.getLogger(__name__)`).
        sample_limit: The maximum number of iterable elements to check per argument.
        max_depth: The maximum container-nesting depth to validate (0 = unlimited).

    Raises:
        TypeError: If `mode == 'raise'` and any argument does not conform to its annotation.

    Complexity:
        Validating `n` parameters is O(n). Container checks are linear in size but bounded by `sample_limit` for
        iterables; unions are O(k) in the number of branches.
    """
    if mode not in ("raise", "suggest"):
        raise ValueError("'mode' must be 'raise' or 'suggest'")

    if logger is None:
        logger = logging.getLogger(__name__)

    def _typesafe(func: F) -> F:
        sig = inspect.signature(func)
        type_hints: Dict[str, Any] = resolve_type_hints(func)

        @wraps(func)
        def _wrapper(*args: Any, **kwargs: Any) -> Any:
            bound = sig.bind_partial(*args, **kwargs)
            bound.apply_defaults()

            # Collect all mismatches
            bad_names: ErrorList[str] = ErrorList()
            bad_values: ErrorList[Any] = ErrorList()
            bad_expected: ErrorList[Union[Type[Any], ExpectedTypeList[Any]]] = ErrorList()

            for name, value in bound.arguments.items():
                ann = type_hints.get(name)
                if ann is None:
                    continue
                if not matches_type_hints(
                    value, ann, sample_limit=sample_limit, max_depth=max_depth
                ):
                    bad_names.append(name)
                    bad_values.append(value)
                    bad_expected.append(_expected_for_display(ann))

            if bad_names:
                err = create_type_error(
                    input_names=bad_names if len(bad_names) > 1 else bad_names[0],
                    input_values=bad_values if len(bad_values) > 1 else bad_values[0],
                    expected_types=bad_expected if len(bad_expected) > 1 else bad_expected[0],
                    function_name=get_function_name(level=1),
                )
                if mode == "raise":
                    raise err
                logger.warning("%s", err)  # warns and continues in 'suggest' mode

            return func(*args, **kwargs)

        return _wrapper

    return _typesafe
