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
    Callable,
    get_args,
    get_origin,
)

from nutil.common import *
from nutil.exceptions import create_type_error, ErrorList, ExpectedTypeList, get_function_name

## TYPING DECORATORS #####################################################################

__TYPING_DECORATORS___________________________________________ = ""

from nutil.typing.hints import expected_for_display, matches_type_hints, resolve_type_hints

F = TypeVar("F", bound=Callable[..., Any])


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
        mode: The validation mode, either `"raise"` to raise a `TypeError`, or `"suggest"` to log a warning.
        logger: The logger for `"suggest"` mode (defaults to `logging.getLogger(__name__)`).
        sample_limit: The maximum number of iterable elements to check per argument.
        max_depth: The maximum container-nesting depth to validate (0 = unlimited).

    Raises:
        TypeError: If `mode == "raise"` and any argument does not conform to its annotation.
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
                    bad_expected.append(expected_for_display(ann))

            if bad_names:
                err = create_type_error(
                    input_names=bad_names if len(bad_names) > 1 else bad_names[0],
                    input_values=bad_values if len(bad_values) > 1 else bad_values[0],
                    expected_types=bad_expected if len(bad_expected) > 1 else bad_expected[0],
                    function_name=get_function_name(level=1),
                )
                if mode == "raise":
                    raise err
                logger.warning("%s", err)  # warns and continues in `"suggest"` mode

            return func(*args, **kwargs)

        return _wrapper

    return _typesafe
