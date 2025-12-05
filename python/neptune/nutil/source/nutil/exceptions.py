#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide utility exceptions.
########################################################################################################################

from typing import Callable

from nutil.common import *

## EXCEPTION CLASSES #####################################################################

__EXCEPTION_CLASSES_________________________________________ = ""

from nutil.typing import get_type_name


class ErrorList(List[Any]):
    """A specialized `list` for error values."""


### TYPE ERROR #############################################

__TYPE_ERROR_CLASSES________________________________________ = ""


class ExpectedTypeList(List[Type[Any]]):
    """A specialized `list` for expected types."""


## EXCEPTION PROCESSORS ##################################################################

__EXCEPTION_PROCESSORS______________________________________ = ""

### TYPE ERROR #############################################

__TYPE_ERROR_PROCESSORS_____________________________________ = ""


def create_type_error(
    input_names: Union[str, ErrorList[str]],
    input_values: Union[Any, ErrorList[Any]],
    expected_types: Union[
        Type[Any],
        ExpectedTypeList[Type[Any]],
        ErrorList[Union[Type[Any], ExpectedTypeList[Type[Any]]]],
    ],
    function_name: Optional[str] = None,
    *,
    group_separator: str = ", ",
    set_separator: str = "/",
) -> TypeError:
    """Constructs a detailed `TypeError` describing input type mismatches."""
    if function_name is None:
        function_name = get_function_name(level=1)

    has_multiple_inputs = isinstance(input_names, ErrorList) and len(input_names) > 1
    has_parallel_expected = isinstance(expected_types, ErrorList) and len(expected_types) > 1

    def _format(
        items: Union[Any, ErrorList[Any], ExpectedTypeList[Type[Any]]],
        *,
        scalar: Callable[[Any], str],
    ) -> str:
        if isinstance(items, ErrorList):
            return group_separator.join(_format(item, scalar=scalar) for item in items)
        elif isinstance(items, ExpectedTypeList):
            return set_separator.join(f"'{scalar(item)}'" for item in items)
        return f"'{scalar(items)}'"

    input_names_str = _format(input_names, scalar=str)
    input_types_str = _format(input_values, scalar=get_type_name)
    expected_types_str = _format(expected_types, scalar=get_type_name)

    return TypeError(
        f"The input{'s' if has_multiple_inputs else ''} {input_names_str} of the function "
        f"'{function_name}' must be of type{'s' if has_parallel_expected else ''} "
        f"{expected_types_str}{', respectively' if has_parallel_expected else ''}; "
        f"got type{'s' if has_multiple_inputs else ''} "
        f"{input_types_str}{', respectively' if has_multiple_inputs else ''}"
    )
