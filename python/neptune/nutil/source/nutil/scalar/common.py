#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains common scalar utility functions
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

import numbers
from datetime import *

import pandas as pd

from nutil.constants import *

####################################################################################################
# COMMON SCALAR CONSTANTS
####################################################################################################

# • BOOLEAN ########################################################################################

__COMMON_BOOLEAN_CONSTANTS________________________ = ""

BOOLEAN_TYPE = bool
BOOLEAN_ELEMENT_TYPE = np.bool_

# • BYTES ##########################################################################################

__COMMON_BYTES_CONSTANTS__________________________ = ""

BYTES_TYPE = bytes
BYTES_ELEMENT_TYPE = np.bytes_


# • DATE ###########################################################################################

__COMMON_DATE_CONSTANTS___________________________ = ""

DATE_TYPE = date

DATETIME_TYPE = datetime

TIMESTAMP_TYPE = pd.Timestamp


# • NUMBER #########################################################################################

__COMMON_NUMBER_CONSTANTS_________________________ = ""

NUMBER_TYPE = numbers.Number

#########################

FLOAT_TYPE = float
FLOAT_ELEMENT_TYPE = np.float32 if BIT_COUNT == 32 else np.float64 if BIT_COUNT == 64 else None

INT_TYPE = int
INT_ELEMENT_TYPE = np.int32 if BIT_COUNT == 32 else np.int64 if BIT_COUNT == 64 else None

LONG_TYPE = int
LONG_ELEMENT_TYPE = np.uint32 if BIT_COUNT == 32 else np.uint64 if BIT_COUNT == 64 else None

SHORT_TYPE = int
SHORT_ELEMENT_TYPE = np.uint8

# • STRING #########################################################################################

__COMMON_STRING_CONSTANTS_________________________ = ""

STRING_TYPE = str
STRING_ELEMENT_TYPE = np.str_  # np.string_


####################################################################################################
# COMMON SCALAR VERIFIERS
####################################################################################################

__COMMON_SCALAR_VERIFIERS_________________________ = ""

def is_scalar(x):
    return (is_boolean(x) or
            is_bytes(x) or
            is_date(x) or is_datetime(x) or is_timestamp(x) or
            is_number(x) or
            is_string(x))


# • BOOLEAN ########################################################################################

__COMMON_BOOLEAN_VERIFIERS________________________ = ""

def is_boolean(x):
    return isinstance(x, BOOLEAN_TYPE) or isinstance(x, BOOLEAN_ELEMENT_TYPE)


# • BYTES ##########################################################################################

__COMMON_BYTES_VERIFIERS__________________________ = ""


def is_bytes(x):
    return isinstance(x, BYTES_TYPE) or isinstance(x, BYTES_ELEMENT_TYPE)


# • DATE ###########################################################################################

__COMMON_DATE_VERIFIERS___________________________ = ""

def is_date(x):
    return isinstance(x, DATE_TYPE)


def is_datetime(x):
    return isinstance(x, DATETIME_TYPE)


def is_timestamp(x):
    return isinstance(x, TIMESTAMP_TYPE)


def is_stamp(x):
    return is_number(x)


# • NUMBER #########################################################################################

__COMMON_NUMBER_VERIFIERS_________________________ = ""

def is_number(x):
    return isinstance(x, NUMBER_TYPE)


def is_float(x):
    return isinstance(x, FLOAT_TYPE) or isinstance(x, FLOAT_ELEMENT_TYPE)


def is_int(x):
    return isinstance(x, INT_TYPE) or isinstance(x, INT_ELEMENT_TYPE)


def is_long(x):
    return isinstance(x, LONG_TYPE) or isinstance(x, LONG_ELEMENT_TYPE)


def is_short(x):
    return isinstance(x, SHORT_TYPE) or isinstance(x, SHORT_ELEMENT_TYPE)

##################################################

def is_nan(x):
    return x is pd.NA or x is pd.NaT or (is_number(x) and str(x) == "nan")


# • STRING #########################################################################################

__COMMON_STRING_VERIFIERS_________________________ = ""

def is_string(x):
    return isinstance(x, STRING_TYPE) or isinstance(x, STRING_ELEMENT_TYPE)
