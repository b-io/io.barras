#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain common utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2022 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from distutils.util import strtobool

from nutil.common.arrays import *
from nutil.common.dataframes import *
from nutil.common.strings import *

####################################################################################################
# COMMON NUMBER CONSTANTS
####################################################################################################

__COMMON_NUMBER_CONSTANTS_________________________ = ""

# The default maximum number of decimals
DEFAULT_MAX_DECIMALS = 8

##################################################

NUMBER_TYPE = numbers.Number

#########################

BOOL_TYPE = bool
BOOL_ELEMENT_TYPE = np.bool8

FLOAT_TYPE = float
FLOAT_ELEMENT_TYPE = np.float32 if BIT_COUNT == 32 else np.float64 if BIT_COUNT == 64 else None

INT_TYPE = int
INT_ELEMENT_TYPE = np.int32 if BIT_COUNT == 32 else np.int64 if BIT_COUNT == 64 else None

LONG_TYPE = int
LONG_ELEMENT_TYPE = np.uint32 if BIT_COUNT == 32 else np.uint64 if BIT_COUNT == 64 else None

SHORT_TYPE = int
SHORT_ELEMENT_TYPE = np.uint8

##################################################

EPS = np.finfo(FLOAT_TYPE).eps
INF = np.inf
NAN = np.nan


####################################################################################################
# COMMON NUMBER VERIFIERS
####################################################################################################

__COMMON_NUMBER_VERIFIERS_________________________ = ""


def is_nan(x):
    return x is pd.NA or x is pd.NaT or (is_number(x) and str(x) == "nan")


#########################


def is_number(x):
    return isinstance(x, NUMBER_TYPE)


def is_bool(x):
    return isinstance(x, BOOL_TYPE) or isinstance(x, BOOL_ELEMENT_TYPE)


def is_float(x):
    return isinstance(x, FLOAT_TYPE) or isinstance(x, FLOAT_ELEMENT_TYPE)


def is_int(x):
    return isinstance(x, INT_TYPE) or isinstance(x, INT_ELEMENT_TYPE)


def is_long(x):
    return isinstance(x, LONG_TYPE) or isinstance(x, LONG_ELEMENT_TYPE)


def is_short(x):
    return isinstance(x, SHORT_TYPE) or isinstance(x, SHORT_ELEMENT_TYPE)


##################################################


def equals(x, y):
    return is_null(x) and is_null(y) or x == y


####################################################################################################
# COMMON NUMBER CONVERTERS
####################################################################################################

__COMMON_NUMBER_CONVERTERS________________________ = ""


def to_bool(x):
    if is_null(x):
        return NAN
    elif is_collection(x):
        if hasattr(x, "astype"):
            return x.astype(BOOL_ELEMENT_TYPE)
        return apply(x, to_bool)
    elif is_string(x):
        return bool(strtobool(x))
    return bool(x)


def to_int(x):
    if is_null(x):
        return NAN
    elif is_collection(x):
        if hasattr(x, "astype"):
            return x.astype(INT_ELEMENT_TYPE)
        return apply(x, to_int)
    return int(x)


def to_float(x):
    if is_null(x):
        return NAN
    elif is_collection(x):
        if hasattr(x, "astype"):
            return x.astype(FLOAT_ELEMENT_TYPE)
        return apply(x, to_float)
    return float(x)


####################################################################################################
# COMMON NUMBER FORMATTERS
####################################################################################################

__COMMON_NUMBER_FORMATTERS________________________ = ""


def format_number(x, decimals=DEFAULT_MAX_DECIMALS):
    return str(round(x, decimals=decimals))


#########################


def format_nth(x):
    s = str(x)
    if s[-1] == "1":
        return s + "st"
    elif s[-1] == "2":
        return s + "nd"
    elif s[-1] == "3":
        return s + "rd"
    return s + "th"


def format_percent(x, decimals=DEFAULT_MAX_DECIMALS):
    return format_number(x * 100, decimals=decimals) + "%"


####################################################################################################
# COMMON NUMBER GENERATORS
####################################################################################################

__COMMON_NUMBER_GENERATORS________________________ = ""


def create_sequence(start=0, stop=0, step=1, include=False, size=None):
    if start == stop:
        if include:
            return start
        return to_array(type=INT_ELEMENT_TYPE)
    elif start > stop:
        start, stop = stop, start
    if not is_null(size):
        if size <= 1:
            return start
        step = (stop - start) / (size if not include else size - 1)
    sequence = np.arange(start, stop, step)
    if include:
        sequence = np.append(sequence, stop)
    return sequence


####################################################################################################
# COMMON NUMBER PROCESSORS
####################################################################################################

__COMMON_NUMBER_PROCESSORS________________________ = ""


def ceil(x):
    return to_int(np.ceil(x))


def floor(x):
    return to_int(np.floor(x))


def round(x, decimals=DEFAULT_MAX_DECIMALS):
    if decimals == 0:
        return round_to_int(x)
    return np.round(x, decimals=decimals)


def round_to_int(x):
    return to_int(np.round(x))


#########################


def mod(x, y):
    m = x % y
    return y if m == 0 else m


#########################


def nearest(c, value):
    if is_empty(c):
        return None
    elif is_series(c) or is_array(c):
        return get(c, abs(c - value).argmin())
    return min(to_list(c), key=lambda x: abs(x - value))


def farthest(c, value):
    if is_empty(c):
        return None
    elif is_series(c) or is_array(c):
        return get(c, abs(c - value).argmax())
    return max(to_list(c), key=lambda x: abs(x - value))
