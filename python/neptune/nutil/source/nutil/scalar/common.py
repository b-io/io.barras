#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common scalar utilities.
########################################################################################################################

from __future__ import annotations

import numbers
from datetime import date, datetime
from typing import Any, Type

import pandas as pd

from nutil.constants import *

## COMMON SCALAR CONSTANTS ###############################################################

### BOOLEAN ################################################

__COMMON_BOOLEAN_CONSTANTS__________________________________ = ""

BOOLEAN_TYPE = bool
BOOLEAN_ELEMENT_TYPE = np.bool_

### BYTES ##################################################

__COMMON_BYTES_CONSTANTS____________________________________ = ""

BYTES_TYPE = bytes
BYTES_ELEMENT_TYPE = np.bytes_

BYTEARRAY_TYPE = bytearray
MEMORYVIEW_TYPE = memoryview

### DATE ###################################################

__COMMON_DATE_CONSTANTS_____________________________________ = ""

DATE_TYPE = date
DATETIME_TYPE = datetime
TIMESTAMP_TYPE = pd.Timestamp

### NUMBER #################################################

__COMMON_NUMBER_CONSTANTS___________________________________ = ""

NUMBER_TYPE = numbers.Number

##############################

FLOAT_TYPE = float
FLOAT_ELEMENT_TYPE = np.float32 if BIT_COUNT == 32 else np.float64 if BIT_COUNT == 64 else np.float_

INT_TYPE = int
INT_ELEMENT_TYPE = np.int32 if BIT_COUNT == 32 else np.int64 if BIT_COUNT == 64 else np.int_

LONG_TYPE = int
LONG_ELEMENT_TYPE = np.uint32 if BIT_COUNT == 32 else np.uint64 if BIT_COUNT == 64 else np.long

SHORT_TYPE = int
SHORT_ELEMENT_TYPE = np.uint8

### STRING #################################################

__COMMON_STRING_CONSTANTS___________________________________ = ""

STRING_TYPE = str
STRING_ELEMENT_TYPE = np.str_  # np.string_


## COMMON SCALAR VERIFIERS ###############################################################

__COMMON_SCALAR_VERIFIERS___________________________________ = ""


def is_scalar(x: Any) -> bool:
    """Returns whether `x` is one of the supported scalar categories."""
    return (
        is_boolean(x)
        or is_byte_like(x)
        or is_date(x)
        or is_datetime(x)
        or is_timestamp(x)
        or is_number(x)
        or is_string(x)
    )


def is_scalar_type(t: Type[Any]) -> bool:
    """Returns whether `t` is one of the supported scalar types."""
    return (
        is_boolean_type(t)
        or is_bytes_type(t)
        or is_date_type(t)
        or is_datetime_type(t)
        or is_timestamp_type(t)
        or is_number_type(t)
        or is_string_type(t)
    )


### BOOLEAN ################################################

__COMMON_BOOLEAN_VERIFIERS__________________________________ = ""


def is_boolean(x: Any) -> bool:
    """Returns whether `x` is a boolean scalar (Python or NumPy)."""
    return isinstance(x, (BOOLEAN_TYPE, BOOLEAN_ELEMENT_TYPE))


def is_boolean_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a boolean type (Python or NumPy)."""
    return issubclass(t, (BOOLEAN_TYPE, BOOLEAN_ELEMENT_TYPE))


### BYTES ##################################################

__COMMON_BYTES_VERIFIERS____________________________________ = ""


def is_bytes(x: Any) -> bool:
    """Returns whether `x` is a bytes scalar (Python or NumPy)."""
    return isinstance(x, (BYTES_TYPE, BYTES_ELEMENT_TYPE))


def is_bytes_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a bytes type (Python or NumPy)."""
    return issubclass(t, (BYTES_TYPE, BYTES_ELEMENT_TYPE))


def is_byte_like(x: Any) -> bool:
    """Returns whether `x` is a byte-like object (Python or NumPy)."""
    return isinstance(x, (BYTES_TYPE, BYTEARRAY_TYPE, MEMORYVIEW_TYPE))


def is_byte_like_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a byte-like type (Python or NumPy)."""
    return issubclass(t, (BYTES_TYPE, BYTEARRAY_TYPE, MEMORYVIEW_TYPE))


### DATE ###################################################

__COMMON_DATE_VERIFIERS_____________________________________ = ""


def is_date(x: Any) -> bool:
    """Returns whether `x` is a `date` scalar (without time)."""
    return isinstance(x, DATE_TYPE)


def is_date_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `date` type (without time)."""
    return issubclass(t, DATE_TYPE)


def is_datetime(x: Any) -> bool:
    """Returns whether `x` is a `datetime` scalar."""
    return isinstance(x, DATETIME_TYPE)


def is_datetime_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a `datetime` type."""
    return issubclass(t, DATETIME_TYPE)


def is_timestamp(x: Any) -> bool:
    """Returns whether `x` is a Pandas `Timestamp` scalar."""
    return isinstance(x, TIMESTAMP_TYPE)


def is_timestamp_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a Pandas `Timestamp` type."""
    return issubclass(t, TIMESTAMP_TYPE)


def is_stamp(x: Any) -> bool:
    """Returns whether `x` is a numeric timestamp (epoch seconds/millis, etc.)."""
    return is_number(x) and not is_boolean(x) and np.isfinite(x) and x >= 0


### NUMBER #################################################

__COMMON_NUMBER_VERIFIERS___________________________________ = ""


def is_number(x: Any) -> bool:
    """Returns whether `x` is a numeric scalar (Python, NumPy, or compatible)."""
    return isinstance(x, NUMBER_TYPE)


def is_number_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a numeric type (Python, NumPy, or compatible)."""
    return issubclass(t, NUMBER_TYPE)


def is_float(x: Any) -> bool:
    """Returns whether `x` is a floating-point scalar (Python or NumPy)."""
    return isinstance(x, (FLOAT_TYPE, FLOAT_ELEMENT_TYPE))


def is_float_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a floating-point type (Python or NumPy)."""
    return issubclass(t, (FLOAT_TYPE, FLOAT_ELEMENT_TYPE))


def is_int(x: Any) -> bool:
    """Returns whether `x` is an integer scalar (Python or NumPy)."""
    return isinstance(x, (INT_TYPE, INT_ELEMENT_TYPE))


def is_int_type(t: Type[Any]) -> bool:
    """Returns whether `t` is an integer type (Python or NumPy)."""
    return issubclass(t, (INT_TYPE, INT_ELEMENT_TYPE))


def is_long(x: Any) -> bool:
    """Returns whether `x` is a long-integer scalar (alias of `int`, NumPy unsigned width-mapped)."""
    return isinstance(x, (LONG_TYPE, LONG_ELEMENT_TYPE))


def is_long_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a long-integer type (alias of `int`, NumPy unsigned width-mapped)."""
    return issubclass(t, (LONG_TYPE, LONG_ELEMENT_TYPE))


def is_short(x: Any) -> bool:
    """Returns whether `x` is a short-integer scalar (NumPy `uint8`)."""
    return isinstance(x, (SHORT_TYPE, SHORT_ELEMENT_TYPE))


def is_short_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a short-integer type (NumPy `uint8`)."""
    return issubclass(t, (SHORT_TYPE, SHORT_ELEMENT_TYPE))


############################################################


def is_nan(x: Any) -> bool:
    """
    Returns whether `x` is a NaN-like sentinel (IEEE NaN or Pandas NA/NaT).

    Notes:
        • Numeric NaN is detected via the `x != x` property (covers Python/NumPy/Decimal NaNs).
        • Keeps `None` distinct from NaN; use `is_null` elsewhere to treat both as null.
        • Accepts the string `"nan"` (case-insensitive) for backwards compatibility.
    """
    if x is pd.NA or x is pd.NaT:
        return True
    elif is_number(x):
        # NaN is not equal to itself (covers float/np.floating/Decimal NaN)
        return x != x  # noqa: PLR0124 (intentional self-comparison)
    elif isinstance(x, str):
        return x.lower() == "nan"
    return False


### STRING #################################################

__COMMON_STRING_VERIFIERS___________________________________ = ""


def is_string(x: Any) -> bool:
    """Returns whether `x` is a string scalar (Python or NumPy)."""
    return isinstance(x, (STRING_TYPE, STRING_ELEMENT_TYPE))


def is_string_type(t: Type[Any]) -> bool:
    """Returns whether `t` is a string type (Python or NumPy)."""
    return issubclass(t, (STRING_TYPE, STRING_ELEMENT_TYPE))
