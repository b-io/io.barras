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
#    Copyright © 2013-2025 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

import csv
import json
import os
from urllib.request import urlopen

import pandas as pd
import validators

####################################################################################################
# COMMON FILE CONSTANTS
####################################################################################################

__COMMON_FILE_CONSTANTS___________________________ = ""

# The default root
DEFAULT_ROOT = None

# The default resources directory
DEFAULT_RES_DIR = "resources"

#########################

# The default encoding
DEFAULT_ENCODING = "utf-8"

####################################################################################################
# COMMON FILE VERIFIERS
####################################################################################################

__COMMON_FILE_VERIFIERS___________________________ = ""


def is_dir(path):
    return os.path.isdir(path)


def is_file(path):
    return os.path.isfile(path)


#########################


def is_root(path):
    return os.path.dirname(path) == path


####################################################################################################
# COMMON FILE FUNCTIONS
####################################################################################################

__COMMON_FILE_____________________________________ = ""


def get_path(path="."):
    return os.path.abspath(path)


#########################


def get_dir(path=".", parent=None):
    path = get_path(path)
    if is_null(parent):
        parent = not is_dir(path)
    return os.path.dirname(get_path(path) + ("/" if not parent else ""))


def get_filename(path="."):
    path = get_path(path)
    return os.path.basename(path)


def get_extension(path="."):
    path = get_path(path)
    return os.path.splitext(path)[1][1:]


##################################################


def create_dir(path):
    if not os.path.exists(path):
        os.makedirs(path)


##################################################


def format_dir(dir):
    if is_null(dir):
        return ""
    if dir[-1] == "/" or dir[-1] == "\\":
        dir = dir[:-1]
    return dir + "/"


##################################################


def find_path(filename, dir=None, subdir=None):
    if is_null(dir):
        dir = get_dir(get_path())
        while not is_file(format_dir(dir) + format_dir(subdir) + filename) and not is_root(dir):
            dir = get_dir(dir, parent=True)
    elif is_file(dir):
        dir = get_dir(dir)
    return format_dir(dir) + format_dir(subdir) + filename


#########################


def read(path, encoding=DEFAULT_ENCODING, ignore=False, newline=None):
    if validators.url(path):
        with urlopen(path) as f:
            encoding = encoding if not is_null(encoding) else f.headers.get_content_charset()
            return f.read().decode(encoding=encoding)
    with open(
        path, mode="r", encoding=encoding, errors="ignore" if ignore else None, newline=newline
    ) as f:
        return f.read()


def read_iterator(path, encoding=DEFAULT_ENCODING, ignore=False, newline=None):
    if validators.url(path):
        with urlopen(path) as f:
            encoding = encoding if not is_null(encoding) else f.headers.get_content_charset()
            for line in f:
                yield line.decode(encoding=encoding)
    else:
        with open(
            path, mode="r", encoding=encoding, errors="ignore" if ignore else None, newline=newline
        ) as f:
            for line in f:
                yield line


def read_enumerator(path, encoding=DEFAULT_ENCODING, ignore=False, newline=None):
    if validators.url(path):
        with urlopen(path) as f:
            encoding = encoding if not is_null(encoding) else f.headers.get_content_charset()
            for i, line in enumerate(f):
                yield i, line.decode(encoding=encoding)
    else:
        with open(
            path, mode="r", encoding=encoding, errors="ignore" if ignore else None, newline=newline
        ) as f:
            for i, line in enumerate(f):
                yield i, line


def read_bytes(path):
    if validators.url(path):
        with urlopen(path) as f:
            return f.read()
    with open(path, mode="rb") as f:
        return f.read()


def read_csv(
    path,
    encoding=DEFAULT_ENCODING,
    delimiter=",",
    ignore=False,
    index_cols=None,
    index_name="index",
    na_values=[""],
    newline=None,
    type=None,
    **kwargs,
):
    df = pd.read_csv(
        path,
        encoding=encoding,
        delimiter=delimiter,
        dtype=type,
        error_bad_lines=not ignore,
        index_col=index_cols,
        lineterminator=newline,
        na_values=na_values,
        **kwargs,
    )
    if is_null(index_cols):
        set_index_name(df, index_name)
    return df


def read_json(path, encoding=DEFAULT_ENCODING, ignore=None, newline=None, **kwargs):
    if validators.url(path):
        with urlopen(path) as f:
            return json.load(f, **kwargs)
    with open(
        path, mode="r", encoding=encoding, errors="ignore" if ignore else None, newline=newline
    ) as f:
        return json.load(f, **kwargs)


#########################


def write(path, content, append=False, encoding=DEFAULT_ENCODING, ignore=False, newline=None):
    with open(
        path,
        mode="a" if append else "w",
        encoding=encoding,
        errors="ignore" if ignore else None,
        newline=newline,
    ) as f:
        return f.write(content)


def write_bytes(path, content, append=False, ignore=False):
    with open(path, mode="ab" if append else "wb", errors="ignore" if ignore else None) as f:
        return f.write(content)


def write_csv(
    path,
    content,
    append=False,
    dialect="excel",
    encoding=DEFAULT_ENCODING,
    ignore=False,
    newline=None,
    **kwargs,
):
    with open(
        path,
        mode="a" if append else "w",
        encoding=encoding,
        errors="ignore" if ignore else None,
        newline=newline,
    ) as f:
        if is_dict(content):
            return csv.writer(f, dialect=dialect, **kwargs).writerow(content)
        else:
            return csv.writer(f, dialect=dialect, **kwargs).writerows(content)


def write_json(
    path,
    content,
    append=False,
    encoding=DEFAULT_ENCODING,
    ignore=False,
    indent=None,
    newline=None,
    **kwargs,
):
    with open(
        path,
        mode="a" if append else "w",
        encoding=encoding,
        errors="ignore" if ignore else None,
        newline=newline,
    ) as f:
        return json.dump(content, f, indent=indent, **kwargs)
