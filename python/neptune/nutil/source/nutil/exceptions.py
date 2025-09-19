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

import inspect
from typing import Any, Callable, List, Optional, Union

from nutil import is_collection, quote

####################################################################################################
# EXCEPTION CLASSES
####################################################################################################

__EXCEPTION_CLASSES_______________________________ = ""

# • TYPE ERROR #####################################################################################

__TYPE_ERROR_CLASSES______________________________ = ""

class ErrorList(List[Any]):
    """A specialized list for grouping multiple error values."""

class ExpectedTypeList(List[type]):
    """A specialized list for grouping multiple expected types."""

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
        type, ExpectedTypeList[type], ErrorList[Union[type, ExpectedTypeList[type]]]
    ],
    function_name: Optional[str] = None,
) -> TypeError:
    """
    Constructs a detailed `TypeError` describing input type mismatches.

    Args:
        input_names: A string or list of strings identifying the input variables.
        input_values: A single value or list of values provided as input.
        expected_types: A type or nested list structure of expected types.
        function_name: The name of the function where the error occurred. If not specified,
                       it is inferred from the call stack.

    Returns:
        A formatted `TypeError` with details on mismatched types.
    """
    if any(isinstance(x, ErrorList) for x in (input_names, input_values, expected_types)):
        assert isinstance(input_names, ErrorList)
        assert isinstance(input_values, ErrorList)
        assert isinstance(expected_types, ErrorList)

    if function_name is None:
        function_name = inspect.stack()[1].function

    has_multiple_inputs = is_collection(input_names) and len(input_names) > 1
    has_multiple_expected_types = is_collection(expected_types) and len(expected_types) > 1

    input_names_str = format_type_error_items(input_names, str, ", ")
    input_types_str = format_type_error_items(input_values, lambda v: type(v).__name__, ", ")
    expected_types_str = format_type_error_items(expected_types, lambda t: t.__name__, "/")

    return TypeError(
        f"The input{'s' if has_multiple_inputs else ''} {input_names_str} of the function "
        f"'{function_name}' must be of type{'s' if has_multiple_expected_types else ''} "
        f"{expected_types_str}{', respectively' if has_multiple_expected_types else ''}; "
        f"got type{'s' if has_multiple_inputs else ''} "
        f"{input_types_str}{', respectively' if has_multiple_inputs else ''}"
    )

def format_type_error_items(
    items: Union[Any, ErrorList[Any], ExpectedTypeList[Any]],
    formatter: Callable[[Any], str],
    separator: str,
) -> str:
    """
    Formats error items into a string using the specified formatter and separator.

    Args:
        items: A single item or a list of items (possibly nested ErrorList or ExpectedTypeList).
        formatter: A function that converts each item into a string.
        separator: A string used to join formatted items.

    Returns:
        A formatted string representation of the items.
    """
    if isinstance(items, (ErrorList, ExpectedTypeList)):
        return separator.join(format_type_error_items(item, formatter, separator) for item in items)
    return quote(formatter(items))
