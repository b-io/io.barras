#!/usr/bin/env python
####################################################################################################
# NAME
#   <NAME> - contains common utility functions
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

import csv
import json
import validators
from urllib.request import urlopen

from nutil.common import *

####################################################################################################
# FILE CONSTANTS
####################################################################################################

__FILE_CONSTANTS__________________________________ = ""

# The default encoding
DEFAULT_ENCODING = "utf-8"


####################################################################################################
# FILE GENERATORS
####################################################################################################

__FILE_GENERATORS_________________________________ = ""


def create_dir(path):
    if not os.path.exists(path):
        os.makedirs(path)


####################################################################################################
# FILE PROCESSORS
####################################################################################################

__FILE_PROCESSORS_________________________________ = ""


def format_dir(dir):
    if not dir:
        return ""
    if dir[-1] == "/" or dir[-1] == "\\":
        dir = dir[:-1]
    return dir + "/"


##################################################


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
    element_type: Optional[Union[np.dtype[Any], Type[Any]]] = None,
    **kwargs,
):
    df = pd.read_csv(
        path,
        encoding=encoding,
        delimiter=delimiter,
        dtype=element_type,
        error_bad_lines=not ignore,
        index_col=index_cols,
        lineterminator=newline,
        na_values=na_values,
        **kwargs,
    )
    if not index_cols:
        from nutil.struct.table.dataframe import set_index_name

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
