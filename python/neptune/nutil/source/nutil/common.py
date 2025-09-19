#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains common utility functions
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
import os
import sys
import warnings

from nutil.struct.common import *

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



####################################################################################################
# COMMON VERIFIERS
####################################################################################################

__COMMON_VERIFIERS________________________________ = ""


def is_null(x):
    return x is None or is_nan(x)


def is_all_null(*args):
    return all(is_null(arg) for arg in to_list(*args))


def is_all_not_null(*args):
    return not is_any_null(*args)


def is_any_null(*args):
    return any([is_null(arg) for arg in to_list(*args)])


def is_any_not_null(*args):
    return not is_all_null(*args)


#########################


def is_empty(x):
    return is_null(x) or (
        hasattr(x, "__len__") and len(x) == 0 or is_frame(x) and count_cols(x) == 0
    )

def is_all_empty(*args):
    return all([is_empty(arg) for arg in to_list(*args)])


def is_all_not_empty(*args):
    return not is_any_empty(*args)


def is_any_empty(*args):
    return any([is_empty(arg) for arg in to_list(*args)])


def is_any_not_empty(*args):
    return not is_all_empty(*args)


#########################


def is_all_value(value, *args):
    return all([value == arg for arg in to_list(*args)])


def is_all_not_value(value, *args):
    return not is_any_value(value, *args)


def is_any_value(value, *args):
    return any([value == arg for arg in to_list(*args)])


def is_any_not_value(value, *args):
    return not is_all_value(value, *args)

##################################################

def exists(x):
    return x in globals() or x in locals() or x in dir(__builtins__)


# • IO #############################################################################################

__COMMON_IO_VERIFIERS_____________________________ = ""


def is_dir(path):
    return os.path.isdir(path)


def is_file(path):
    return os.path.isfile(path)


#########################


def is_root(path):
    return os.path.dirname(path) == path


# • STRUCT #########################################################################################

__COMMON_STRUCT_VERIFIERS_________________________ = ""

def has_filter(keys=None, inclusion=None, exclusion=None):
    return not is_null(keys) or not is_null(inclusion) or not is_empty(exclusion)


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

# • IO #############################################################################################

__COMMON_IO_ACCESSORS_____________________________ = ""

def get_path(path="."):
    return os.path.abspath(path)

#########################

def get_dir(path=".", parent=None):
    path = get_path(path)
    if not parent:
        parent = not is_dir(path)
    return os.path.dirname(get_path(path) + ("/" if not parent else ""))


def get_filename(path="."):
    path = get_path(path)
    return os.path.basename(path)


def get_extension(path="."):
    path = get_path(path)
    return os.path.splitext(path)[1][1:]

#########################

def find_path(filename, dir=None, subdir=None):
    if is_null(dir):
        dir = get_dir(get_path())
        while not is_file(format_dir(dir) + format_dir(subdir) + filename) and not is_root(dir):
            dir = get_dir(dir, parent=True)
    elif is_file(dir):
        dir = get_dir(dir)
    return format_dir(dir) + format_dir(subdir) + filename


####################################################################################################
# COMMON CONVERTERS
####################################################################################################

__COMMON_CONVERTERS_______________________________ = ""

# • STRUCT #########################################################################################

__COMMON_STRUCT_CONVERTERS________________________ = ""

# • COLLECTION ###################################

def to_list(*args):
    """
    Returns a `list` from the specified arguments.

    • Single argument:
      – If already a `list`, returns it unchanged.
      – If a `dict`, returns its values as a `list`.
      – If a `bytes` or a `str`, wraps it as a single-element `list`.
      – If another iterable, converts it to a `list`.
      – Otherwise, wraps it in a single-element `list`.
    • Multiple arguments: packs them into a `list`.
    """
    if len(args) == 1:
        arg = args[0]
        if isinstance(arg, list):
            return arg
        if isinstance(arg, dict):
            return list(arg.values())
        if isinstance(arg, (bytes, str)):
            return [arg]
        try:
            return list(arg)
        except TypeError:
            return [arg]
    return list(args)

def to_set(*args):
    """
    Returns a `set` from the specified arguments.

    • Single argument:
      – If already a `set`, returns it unchanged.
      – If a `dict`, returns its values as a `set`.
      – If a `bytes` or a `str`, wraps it as a single-element `set`.
      – If another iterable, converts it to a `set`.
      – Otherwise, wraps it in a single-element `set`.
    • Multiple arguments: packs them into a `set`.
    """
    if len(args) == 1:
        arg = args[0]
        if isinstance(arg, set):
            return arg
        if isinstance(arg, dict):
            return set(arg.values())
        if isinstance(arg, (str, bytes)):
            return {arg}
        try:
            return set(arg)
        except TypeError:
            return {arg}
    return set(args)

# • TUPLE ########################################

def to_tuple(*args):
    """
    Returns a `tuple` from the specified arguments.

    • Single argument:
      – If already a `tuple`, returns it unchanged.
      – If a `dict`, returns its values as a `tuple`.
      – If a `bytes` or a `str`, wraps it as a single-element `tuple`.
      – If another iterable, converts it to a `tuple`.
      – Otherwise, wraps it in a single-element `tuple`.
    • Multiple arguments: packs them into a `tuple`.
    """
    if len(args) == 1:
        arg = args[0]
        if isinstance(arg, tuple):
            return arg
        if isinstance(arg, dict):
            return tuple(arg.values())
        if isinstance(arg, (bytes, str)):
            return (arg,)
        try:
            return tuple(arg)
        except TypeError:
            return (arg,)
    return tuple(args)

####################################################################################################
# COMMON PROCESSORS
####################################################################################################

__COMMON_PROCESSORS_______________________________ = ""

def invert(x):
    return np.logical_not(x)


# • IO #############################################################################################

__COMMON_IO_PROCESSORS____________________________ = ""

def format_dir(dir):
    if not dir:
        return ""
    if dir[-1] == "/" or dir[-1] == "\\":
        dir = dir[:-1]
    return dir + "/"

# • SCALAR #########################################################################################

__COMMON_SCALAR_PROCESSORS________________________ = ""

def collapse(*args, delimiter="", append=False):
    """Returns the string computed by joining the specified arguments with the specified
    delimiter."""
    return delimiter.join(map(str, to_list(*args))) + (delimiter if append else "")


def collist(*args):
    """Returns the string computed by joining the specified arguments with a comma."""
    return collapse(*args, delimiter=",")


def paste(*args):
    """Returns the string computed by joining the specified arguments with a space."""
    return collapse([s for s in map(str, to_list(*args)) if s != ""], delimiter=" ")


# • STRUCT #########################################################################################

__COMMON_STRUCT_PROCESSORS________________________ = ""

