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

import configparser
import functools
import inspect
import multiprocessing as mp
import os
import pdb
import struct
import sys
import warnings
from concurrent.futures import ThreadPoolExecutor

import numpy as np
from multiprocess.pool import Pool
from tabulate import tabulate

####################################################################################################
# COMMON SETTINGS
####################################################################################################

warnings.simplefilter(action="ignore", category=FutureWarning)

####################################################################################################
# COMMON CLASSES
####################################################################################################

__COMMON_CLASSES__________________________________ = ""


class Object(object):
    pass


##################################################


class EnvInterpolation(configparser.BasicInterpolation):
    """Extends the basic property parser to handle environment variables."""

    def before_get(self, parser, section, option, value, defaults):
        value = super().before_get(parser, section, option, value, defaults)
        return os.path.expandvars(value)


####################################################################################################
# COMMON CONSTANTS
####################################################################################################

__COMMON_CONSTANTS________________________________ = ""

# The default environment
DEFAULT_ENV = "local"

# The default assert
DEFAULT_ASSERT = True

#########################

# The default severity level (0: FAIL, 1: ERROR, 2: WARN, 3: RESULT, 4: INFO, 5: TEST, 6: DEBUG, 7: TRACE)
DEFAULT_SEVERITY_LEVEL = 4

# The default flag specifying whether to enable the verbose mode
DEFAULT_VERBOSE = True

##################################################

CONFIG = configparser.ConfigParser(interpolation=EnvInterpolation())

##################################################

OBJECT_TYPE = object
OBJECT_ELEMENT_TYPE = np.object_

##################################################

BIT_COUNT = 8 * struct.calcsize("P")

CORE_COUNT = mp.cpu_count() or 1

EMPTY = ()

NA_NAME = "N/A"

####################################################################################################
# COMMON VERIFIERS
####################################################################################################

__COMMON_VERIFIERS________________________________ = ""


def is_null(x):
    return x is None or is_nan(x)


def is_all_null(*args):
    return all([is_null(arg) for arg in to_collection(*args)])


def is_all_not_null(*args):
    return not is_any_null(*args)


def is_any_null(*args):
    return any([is_null(arg) for arg in to_collection(*args)])


def is_any_not_null(*args):
    return not is_all_null(*args)


#########################


def is_empty(x):
    return is_null(x) or (
        hasattr(x, "__len__") and len(x) == 0 or is_frame(x) and count_cols(x) == 0
    )


def is_all_empty(*args):
    return all([is_empty(arg) for arg in to_collection(*args)])


def is_all_not_empty(*args):
    return not is_any_empty(*args)


def is_any_empty(*args):
    return any([is_empty(arg) for arg in to_collection(*args)])


def is_any_not_empty(*args):
    return not is_all_empty(*args)


#########################


def is_all_value(value, *args):
    return all([value == arg for arg in to_collection(*args)])


def is_all_not_value(value, *args):
    return not is_any_value(value, *args)


def is_any_value(value, *args):
    return any([value == arg for arg in to_collection(*args)])


def is_any_not_value(value, *args):
    return not is_all_value(value, *args)


##################################################


def exists(x):
    return x in globals() or x in locals() or x in dir(__builtins__)


####################################################################################################
# COMMON PROPERTIES
####################################################################################################

__COMMON_PROPERTIES_______________________________ = ""


def get_config_path(filename, dir=DEFAULT_ROOT, subdir=DEFAULT_RES_DIR):
    """Returns the path to the properties with the specified filename in the specified directory."""
    return find_path(filename + ".properties", dir=dir, subdir=subdir)


def load_config(filename, dir=DEFAULT_ROOT, subdir=DEFAULT_RES_DIR):
    """Loads the properties with the specified filename in the specified directory."""
    return CONFIG.read(get_config_path(filename, dir=dir, subdir=subdir))


def escape_property(property):
    return property.replace("%", "%%") if not is_null(property) else None


#########################

# The default configuration
DEFAULT_CONFIG = {
    "common": {
        # Assert
        "assert": DEFAULT_ASSERT,
        # Environment (local, dev, test, model, prod)
        "env": DEFAULT_ENV,
    },
    "console": {
        # Severity level (0: FAIL, 1: ERROR, 2: WARN, 3: RESULT, 4: INFO, 5: TEST, 6: DEBUG, 7: TRACE)
        "severityLevel": DEFAULT_SEVERITY_LEVEL,
        # Verbose
        "verbose": DEFAULT_VERBOSE,
    },
    "date": {
        # Date format
        "dateFormat": escape_property(DEFAULT_DATE_FORMAT),
        # Time format
        "timeFormat": escape_property(DEFAULT_TIME_FORMAT),
        # Aggregation (count, min, max, mean, median, std, var, sum)
        "aggregation": DEFAULT_AGGREGATION,
        # Frequency (D, W, M, Q, S, Y)
        "frequency": DEFAULT_FREQUENCY,
        # Period
        "period": DEFAULT_PERIOD,
        # Position (first, middle, last)
        "position": DEFAULT_POSITION,
    },
}
CONFIG.read_dict(DEFAULT_CONFIG)
load_config("common")

##################################################

# The flag specifying whether to assert
ASSERT = CONFIG.getboolean("common", "assert")

# The environment
ENV = Environment(CONFIG.get("common", "env"))

# • CONSOLE ########################################################################################

__CONSOLE_PROPERTIES______________________________ = ""

# The severity level
SEVERITY_LEVEL = SeverityLevel(CONFIG.getint("console", "severityLevel"))

# The flag specifying whether to enable the verbose mode
VERBOSE = CONFIG.getboolean("console", "verbose")

####################################################################################################
# COMMON ACCESSORS
####################################################################################################

__COMMON_ACCESSORS________________________________ = ""


def get_exec_info():
    return sys.exc_info()[0]


#########################


def get_stack(level=0):
    return inspect.stack()[level + 1]


def get_script_name(level=0):
    return get_filename(get_stack(level + 1)[1])


def get_function_name(level=0):
    return get_stack(level + 1)[3]


def get_line_number(level=0):
    return get_stack(level + 1)[2]


#########################


def get_module_name(obj):
    return obj.__class__.__module__


def get_class_name(obj):
    return obj.__class__.__name__


def get_full_class_name(obj):
    module_name = get_module_name(obj)
    if is_null(module_name) or module_name == get_module_name(str):
        return get_class_name(obj)
    return collapse(module_name, ".", get_class_name(obj))


def get_attributes(obj):
    return [a for a in vars(obj) if not a.startswith("_")]


def get_all_attributes(obj):
    return [a for a in dir(obj) if not a.startswith("_")]


####################################################################################################
# COMMON CONVERTERS
####################################################################################################

__COMMON_CONVERTERS_______________________________ = ""


def to_element_type(x, t):
    if type(x) is t:
        return x
    if t is TUPLE_TYPE:
        return to_tuple(x)
    elif t is TIMESTAMP_TYPE:
        return to_timestamp(x)
    elif t is DATETIME_TYPE:
        return to_datetime(x)
    elif t is DATE_TYPE:
        return to_date(x)
    elif t is BOOL_TYPE:
        return to_bool(x)
    elif t is BYTE_TYPE:
        return to_byte(x)
    elif t is FLOAT_TYPE:
        return to_float(x)
    elif t is INT_TYPE:
        return to_int(x)
    elif t is STRING_TYPE:
        return to_string(x)
    return x


# • TUPLE ##########################################################################################

__TUPLE_CONVERTERS________________________________ = ""


def to_tuple(*args):
    if len(args) == 1:
        arg = args[0]
        if is_tuple(arg):
            return arg
        elif is_collection(arg):
            return tuple(arg if not is_dict(arg) else arg.values())
        return (arg,)
    return tuple(args)


####################################################################################################
# COMMON FORMATTERS
####################################################################################################

__COMMON_FORMATTERS_______________________________ = ""


def format_bulleted_value(value):
    return collapse(NEWLINE, BULLET, " ", round(value) if is_number(value) else value)


def format_bulleted_list(l, f=format_bulleted_value):
    return collapse([f(v) for v in l])


#########################


def format_bulleted_item(key, value):
    return format_bulleted_value(
        collapse(key, COLON, " ", round(value) if is_number(value) else value)
    )


def format_bulleted_dict(d, f=format_bulleted_item):
    return collapse([f(k, v) for k, v in d.items()])


####################################################################################################
# COMMON GENERATORS
####################################################################################################

__COMMON_GENERATORS_______________________________ = ""

####################################################################################################
# COMMON PROCESSORS
####################################################################################################

__COMMON_PROCESSORS_______________________________ = ""


def apply(
    x, f, *args, inplace=False, axis=None, keys=None, inclusion=None, exclusion=None, **kwargs
):
    """Applies the specified function iteratively over the specified value along the specified axis
    (over the rows, columns or elements if the specified axis is respectively zero, one or null)
    with the specified arguments."""
    if not is_subscriptable_collection(x):
        return f(x, *args, **kwargs)
    elif is_empty(x):
        return x
    if is_null(keys):
        keys = get_keys(x, inclusion=inclusion, exclusion=exclusion)
    if inplace:
        return set_values(x, apply(x, f, *args, axis=axis, keys=keys, **kwargs), keys=keys)
    if is_group(x):
        axis = x.axis
        if axis == 0:
            return concat_rows(
                [
                    to_frame(
                        [to_array(f(get_values(v, keys=keys)), *args, **kwargs)], index=to_list(i)
                    )
                    for i, v in x
                ]
            )
        return concat_cols(
            [
                to_series(to_array(f(to_array(v), *args, **kwargs)), name=k)
                for k, v in x
                if k in keys
            ]
        )
    elif is_frame(x):
        if is_null(axis):
            return concat_cols([x.loc[:, k].apply(f, args=args, **kwargs) for k in keys])
        return x.loc[:, keys].apply(f, args=args, axis=axis, **kwargs)
    elif is_series(x):
        return x.loc[keys].apply(f, args=args, **kwargs)
    elif is_dict(x):
        return {k: f(x[k], *args, **kwargs) for k in keys}
    elif is_array(x):
        if is_null(axis):
            return np.vectorize(lambda x: f(x, *args, **kwargs))(x[keys])
        return np.apply_along_axis(f, axis, x[keys], *args, **kwargs)
    return collection_to_type([f(x[k], *args, **kwargs) for k in keys], x)


def fill_with(x, value, *args, condition=lambda x, *args, **kwargs: True, inplace=False, **kwargs):
    return apply(x, lambda x: value if condition(x, *args, **kwargs) else x, inplace=inplace)


def fill_null_with(x, value, inplace=False):
    return fill_with(x, value, condition=is_null, inplace=inplace)


#########################


def browser():
    pdb.set_trace()


#########################


def clear():
    sys.modules[__name__].__dict__.clear()


#########################


def forward(*args):
    if len(args) == 1:
        return args[0]
    return list(args)


def forward_element(*args):
    if len(args) == 1:
        return args[0]
    return tuple(args)


#########################


def invert(x):
    return np.logical_not(x)


def reduce_and(x, axis=0):
    """Reduces the dimension of the specified arguments by applying the logical AND function
    cumulatively along the specified axis (over the rows or columns if the specified axis is
    respectively zero or one)."""
    if axis == 1 and count_cols(x) == 0:
        return to_array(x, type=BOOL_ELEMENT_TYPE)
    return np.logical_and.reduce(x, axis=axis)


def reduce_or(x, axis=0):
    """Reduces the dimension of the specified arguments by applying the logical OR function
    cumulatively along the specified axis (over the rows or columns if the specified axis is
    respectively zero or one)."""
    if axis == 1 and count_cols(x) == 0:
        return to_array(x, type=BOOL_ELEMENT_TYPE)
    return np.logical_or.reduce(x, axis=axis)


#########################


def reduce(c, f, *args, initializer=None, **kwargs):
    """Reduces the specified arguments to a single one by applying the specified function
    cumulatively (from left to right)."""
    if is_empty(c):
        return initializer
    if not is_null(initializer):
        return functools.reduce(lambda x, y: f(x, y, *args, **kwargs), c, initializer)
    return functools.reduce(lambda x, y: f(x, y, *args, **kwargs), c)


# • CONSOLE ########################################################################################

__CONSOLE_PROCESSORS______________________________ = ""


def trace(*args, level=0):
    if SEVERITY_LEVEL.value >= 7:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][TRAC]",
                "[",
                get_script_name(level + 1),
                "]",
                "[",
                get_function_name(level + 1),
                "]",
                "[",
                get_line_number(level + 1),
                "] ",
                paste(*args),
            )
        )


def debug(*args, level=0):
    if SEVERITY_LEVEL.value >= 6:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][DEBU]",
                "[",
                get_script_name(level + 1),
                "]",
                "[",
                get_function_name(level + 1),
                "] ",
                paste(*args),
            )
        )


def test(*args, level=0):
    if SEVERITY_LEVEL.value >= 5:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][TEST]",
                "[",
                get_script_name(level + 1),
                "] ",
                paste(*args),
            )
        )


def info(*args):
    if SEVERITY_LEVEL.value >= 4:
        print(collapse("[", get_datetime_string(), "][INFO] ", paste(*args)))


def result(*args):
    if SEVERITY_LEVEL.value >= 3:
        print(paste(*args))


def warn(*args, level=0):
    if SEVERITY_LEVEL.value >= 2:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][WARN]",
                "[",
                get_script_name(level + 1),
                "] ",
                paste(*args),
            ),
            file=sys.stderr,
        )


def error(*args, level=0):
    if SEVERITY_LEVEL.value >= 1:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][ERRO]",
                "[",
                get_script_name(level + 1),
                "]",
                "[",
                get_function_name(level + 1),
                "] ",
                paste(*args),
            ),
            file=sys.stderr,
        )


def fail(*args, level=0):
    if SEVERITY_LEVEL.value >= 0:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][FAIL]",
                "[",
                get_script_name(level + 1),
                "]",
                "[",
                get_function_name(level + 1),
                "]",
                "[",
                get_line_number(level + 1),
                "] ",
                paste(*args),
            ),
            file=sys.stderr,
        )


##################################################


def print_table(table, format="grid", headers=None, show_index="default"):
    print(
        tabulate(
            table,
            tablefmt=format,
            headers=(
                headers if not is_null(headers) else get_names(table) if is_table(table) else EMPTY
            ),
            showindex=show_index,
        )
    )


# • THREAD #########################################################################################

__THREAD_PROCESSORS_______________________________ = ""


def multithread(c, f, *args, asynchronous=False, max_workers=CORE_COUNT, timeout=None, **kwargs):
    return multithread_map(
        c,
        lambda x: apply(x, f, *args, **kwargs),
        asynchronous=asynchronous,
        max_workers=max_workers,
        timeout=timeout,
    )


def multithread_map(c, f, asynchronous=False, max_workers=CORE_COUNT, timeout=None):
    if is_empty(c) or not is_collection(c):
        return []
    max_workers = min(max_workers, len(c))
    trace(
        "Apply the function",
        quote(f.__name__),
        "to the collection of size",
        len(c),
        "(multithreading)",
    )
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        # Submit the tasks and collect the results
        results = executor.map(f, c, timeout=timeout)
        if asynchronous:
            return results
        return to_list(results)


def multiprocess(
    c,
    f,
    *args,
    asynchronous=False,
    chunk_size=None,
    max_workers=CORE_COUNT,
    timeout=None,
    callback=None,
    error_callback=None,
    **kwargs,
):
    return multiprocess_map(
        c,
        lambda x: apply(x, f, *args, **kwargs),
        asynchronous=asynchronous,
        chunk_size=chunk_size,
        max_workers=max_workers,
        timeout=timeout,
        callback=callback,
        error_callback=error_callback,
    )


def multiprocess_map(
    c,
    f,
    asynchronous=False,
    chunk_size=None,
    max_workers=CORE_COUNT,
    timeout=None,
    callback=None,
    error_callback=None,
):
    if is_empty(c) or not is_collection(c):
        return []
    max_workers = min(max_workers, len(c))
    if is_null(chunk_size):
        chunk_size = ceil(len(c) / max_workers)
    trace(
        "Apply the function",
        quote(f.__name__),
        "to the collection of size",
        len(c),
        "(multiprocessing)",
    )
    with Pool(processes=max_workers) as executor:
        # Submit the tasks and collect the results
        results = executor.map_async(
            f, c, chunksize=chunk_size, callback=callback, error_callback=error_callback
        )
        if asynchronous:
            return results
        return results.get(timeout=timeout)
