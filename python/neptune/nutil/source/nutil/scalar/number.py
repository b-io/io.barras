#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains common utility functions
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

from nutil.common import *

## NUMBER CONSTANTS ######################################################################

__NUMBER_CONSTANTS__________________________________________ = ""

# The default maximum number of decimals
DEFAULT_MAX_DECIMALS = 8

############################################################

EPS = np.finfo(FLOAT_TYPE).eps


## NUMBER CONVERTERS #####################################################################

__NUMBER_CONVERTERS_________________________________________ = ""


def to_int(x: Any):
    if is_null(x):
        return NAN
    elif is_collection(x):
        if hasattr(x, "astype"):
            return x.astype(INT_ELEMENT_TYPE)
        from nutil.struct.util import apply
        return apply(x, to_int)
    return int(x)


def to_float(x: Any):
    if is_null(x):
        return NAN
    elif is_collection(x):
        if hasattr(x, "astype"):
            return x.astype(FLOAT_ELEMENT_TYPE)
        from nutil.struct.util import apply
        return apply(x, to_float)
    return float(x)


## NUMBER FORMATTERS #####################################################################

__NUMBER_FORMATTERS_________________________________________ = ""


def format_number(x, decimals=DEFAULT_MAX_DECIMALS):
    return str(round(x, decimals=decimals))


##############################


def format_nth(x: Any):
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


## NUMBER GENERATORS #####################################################################

__NUMBER_GENERATORS_________________________________________ = ""


def create_sequence(start=0, stop=0, step=1, include=False, size=None):
    if start == stop:
        if include:
            return start
        return to_array(element_type=INT_ELEMENT_TYPE)
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


## NUMBER PROCESSORS #####################################################################

__NUMBER_PROCESSORS_________________________________________ = ""


def ceil(x: Any):
    return to_int(np.ceil(x))


def floor(x: Any):
    return to_int(np.floor(x))


def round(x, decimals=DEFAULT_MAX_DECIMALS):
    if decimals == 0:
        return round_to_int(x)
    return np.round(x, decimals=decimals)


def round_to_int(x: Any):
    return to_int(np.round(x))


##############################


def mod(x, y):
    m = x % y
    return y if m == 0 else m


##############################


def nearest(c, value):
    if is_empty(c):
        return None
    elif is_series(c) or is_array(c):
        from nutil.struct.util import get
        return get(c, abs(c - value).argmin())
    return min(to_list(c), key=lambda x: abs(x - value))


def farthest(c, value):
    if is_empty(c):
        return None
    elif is_series(c) or is_array(c):
        from nutil.struct.util import get
        return get(c, abs(c - value).argmax())
    return max(to_list(c), key=lambda x: abs(x - value))


## NUMBER VERIFIERS ######################################################################

__NUMBER_VERIFIERS__________________________________________ = ""

def equals(x, y):
    return is_null(x) and is_null(y) or x == y
