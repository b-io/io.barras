#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains utility exceptions
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

from typing import Any, Callable, List, Optional, Type, Union

from nutil.common import get_function_name, get_type_name

####################################################################################################
# EXCEPTION CLASSES
####################################################################################################

__EXCEPTION_CLASSES_______________________________ = ""


class ErrorList(List[Any]):
    """A specialized list for error values."""


# • TYPE ERROR #####################################################################################

__TYPE_ERROR_CLASSES______________________________ = ""

class ExpectedTypeList(List[Type[Any]]):
    """A specialized list for expected types."""


####################################################################################################
# EXCEPTION PROCESSORS
####################################################################################################

__EXCEPTION_PROCESSORS____________________________ = ""

# • TYPE ERROR #####################################################################################

__TYPE_ERROR_PROCESSORS___________________________ = ""


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

    def _format(items: Union[Any, ErrorList[Any], ExpectedTypeList[Type[Any]]],
                *, scalar: Callable[[Any], str]) -> str:
        if isinstance(items, ErrorList):
            return group_separator.join(_format(x, scalar=scalar) for x in items)
        if isinstance(items, ExpectedTypeList):
            return set_separator.join(f"'{scalar(t)}'" for t in items)
        return f"'{scalar(items)}'"

    input_names_str    = _format(input_names,    scalar=str)
    input_types_str    = _format(input_values,   scalar=get_type_name)
    expected_types_str = _format(expected_types, scalar=get_type_name)

    return TypeError(
        f"The input{'s' if has_multiple_inputs else ''} {input_names_str} of the function "
        f"'{function_name}' must be of type{'s' if has_parallel_expected else ''} "
        f"{expected_types_str}{', respectively' if has_parallel_expected else ''}; "
        f"got type{'s' if has_multiple_inputs else ''} "
        f"{input_types_str}{', respectively' if has_multiple_inputs else ''}"
    )
