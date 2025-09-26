#!/usr/bin/env python
####################################################################################################
# NAME
#   <NAME> - contains utility annotations
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from __future__ import annotations

import inspect
import logging
from functools import wraps
from typing import (Any, Callable, Dict, Optional,
                    Type, TypeVar, Union)

from nutil.common import get_function_name
from nutil.exceptions import (create_type_error, ErrorList, ExpectedTypeList)

####################################################################################################
# ANNOTATIONS
####################################################################################################

__ANNOTATIONS_____________________________________ = ""

class classproperty:
    """Implements a read-only property evaluated on the class (not the instance)."""

    def __init__(self, fget):
        if not callable(fget):
            raise TypeError("'classproperty' expects a callable 'fget'")
        self.fget = fget

    def __get__(self, x: Any, owner=None):
        owner = owner if owner is not None else type(x)
        return self.fget(owner)


# • TYPE CHECKING ANNOTATIONS ######################################################################

__TYPE_CHECKING_ANNOTATIONS_______________________ = ""

F = TypeVar("F", bound=Callable[..., Any])


def typesafe(*, mode: str = "raise", logger: Optional[logging.Logger] = None) -> Callable[[F], F]:
    """
    Decorates a function to enforce its type annotations at runtime.

    Summary:
        Validates that each input matches the annotated type in the function signature,
        including unions, optionals, and parametrized containers (the `list`, the `dict`,
        the `tuple`, the `set`, the `Mapping`, the `Sequence`, and the `Iterable`).
        Collects all mismatches and emits a single error via `create_type_error`.

    Args:
        mode: the validation mode, either `"raise"` (default) to raise a `TypeError`,
              or `"suggest"` to log a warning and proceed.
        logger: the logger for `"suggest"` mode (defaults to `logging.getLogger(__name__)`).

    Raises:
        TypeError: if `mode == "raise"` and any argument does not conform to the annotated type.

    Complexity:
        Validating `n` parameters is O(n), with recursive checks proportional to the sizes of
        the annotated containers. Union checks are O(n) in the number of union branches.
    """
    if mode not in ("raise", "suggest"):
        raise ValueError("'mode' must be 'raise' or 'suggest'")

    if logger is None:
        logger = logging.getLogger(__name__)

    def _typesafe(func: F) -> F:
        sig = inspect.signature(func)
        try:
            type_hints: Dict[str, Any] = inspect.get_annotations(func, eval_str=True)  # Python 3.10+
        except (AttributeError, TypeError, NameError):
            from typing import get_type_hints
            type_hints = get_type_hints(func, globalns=func.__globals__)

        @wraps(func)
        def _wrapper(*args: Any, **kwargs: Any) -> Any:
            bound = sig.bind_partial(*args, **kwargs)
            bound.apply_defaults()

            # collect all mismatches
            bad_names: ErrorList[str] = ErrorList()
            bad_values: ErrorList[Any] = ErrorList()
            bad_expected: ErrorList[Union[Type[Any], ExpectedTypeList[Any]]] = ErrorList()

            for name, value in bound.arguments.items():
                if name not in type_hints:
                    continue
                expected = type_hints[name]
                if not _isinstance_safe(value, expected):
                    bad_names.append(name)
                    bad_values.append(value)
                    bad_expected.append(_expected_for_display(expected))

            if bad_names:
                err = create_type_error(
                    input_names=bad_names if len(bad_names) > 1 else bad_names[0],
                    input_values=bad_values if len(bad_values) > 1 else bad_values[0],
                    expected_types=bad_expected if len(bad_expected) > 1 else bad_expected[0],
                    function_name=get_function_name(level=1),
                )
                if mode == "raise":
                    raise err
                # suggest mode: warn and continue
                logger.warning("%s", err)

            return func(*args, **kwargs)

        return _wrapper

    return _typesafe

