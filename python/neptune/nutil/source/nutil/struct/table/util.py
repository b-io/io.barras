#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide table utilities.
########################################################################################################################

from __future__ import annotations

from dataclasses import fields, is_dataclass
from typing import Container, Protocol

from nutil.common import *
from nutil.scalar.string import to_string

__TABLE_CLASSES___________________________________________________________________________ = ""


### ROWS ###################################################


class Dataclass(Protocol):
    """The structural type for dataclass instances."""

    __dataclass_fields__: Mapping[str, Any]


# The typing alias for any supported rows
Row = Union[Dataclass, Iterable[Any], Mapping[str, Any], type]


__TABLE_ACCESSORS_________________________________________________________________________ = ""


### ROWS ###################################################


#### ROW KEYS ################


def get_row_keys(row: Row, *, keys: Optional[Container[str]] = None) -> Tuple[Any, ...]:
    """
    Returns a header-like sequence for `row`.

    Behavior:
        • annotated class → `__annotations__.keys()`
        • dataclass       → field names in definition order
        • mapping         → mapping keys
        • other iterables → elements returned as-is

    For plain iterables (lists/tuples/…), `keys` is ignored because there are no column names to filter by.
    """
    if isinstance(row, type):
        return get_annotations_keys(row, keys)
    elif is_dataclass(row):
        if not is_null(keys):
            return tuple(f.name for f in fields(row) if f.name in keys)
        return tuple(f.name for f in fields(row))
    elif isinstance(row, Mapping):
        if not is_null(keys):
            return tuple(k for k in row.keys() if k in keys)
        return tuple(row.keys())
    elif isinstance(row, Iterable):
        return tuple(row)
    raise TypeError(f"Unsupported row type '{type(row)!r}'")


def get_annotations_keys(x: Any, *, keys: Optional[Container[str]] = None) -> Tuple[str, ...]:
    annotations = getattr(x, "__annotations__", None)
    if is_null(annotations):
        raise TypeError(f"Unsupported type '{type(x)!r}'")
    if not is_null(keys):
        return tuple(k for k in annotations.keys() if k in keys)
    return tuple(annotations.keys())


#### ROW VALUES ##############


def get_row_value(row: Row, key: str) -> Optional[Any]:
    if isinstance(row, type):
        return get_annotations_value(row, key)
    elif is_dataclass(row):
        for f in fields(row):
            if f.name == key:
                return getattr(row, f.name)
        return None
    elif isinstance(row, Mapping):
        return row.get(key)
    elif isinstance(row, Iterable):
        return row
    raise TypeError(f"Unsupported row type '{type(row)!r}'")


def get_row_string(row: Row, key: str) -> str:
    return to_string(get_row_value(row, key))


def get_annotations_value(x: Any, key: str) -> Optional[Any]:
    annotations = getattr(x, "__annotations__", None)
    if is_null(annotations):
        raise TypeError(f"Unsupported type '{type(x)!r}'")
    return annotations.get(key)


def get_row_values(row: Row, *, keys: Optional[Container[str]] = None) -> Tuple[Any, ...]:
    """
    Returns a value sequence for `row`.

    Behavior:
        • annotated class → `__annotations__.values()`
        • dataclass       → field values in definition order
        • mapping         → mapping values
        • other iterables → elements returned as-is

    For plain iterables (lists/tuples/…), `keys` is ignored because there are no column names to filter by.
    """
    if isinstance(row, type):
        return get_annotations_values(row, keys)
    elif is_dataclass(row):
        if not is_null(keys):
            return tuple(getattr(row, f.name) for f in fields(row) if f.name in keys)
        return tuple(getattr(row, f.name) for f in fields(row))
    elif isinstance(row, Mapping):
        if not is_null(keys):
            return tuple(row.get(k) for k in row.keys() if k in keys)
        return tuple(row.values())
    elif isinstance(row, Iterable):
        return tuple(row)
    raise TypeError(f"Unsupported row type '{type(row)!r}'")


def get_annotations_values(x: Any, *, keys: Optional[Container[str]] = None) -> Tuple[Any, ...]:
    annotations = getattr(x, "__annotations__", None)
    if is_null(annotations):
        raise TypeError(f"Unsupported type '{type(x)!r}'")
    if not is_null(keys):
        return tuple(annotations.get(k) for k in annotations.keys() if k in keys)
    return tuple(annotations.values())


__TABLE_PROCESSORS________________________________________________________________________ = ""


### ROWS ###################################################


def deduplicate_rows(rows: Iterable[Row], *, keys: Optional[Container[str]] = None) -> List[Row]:
    """
    Strips duplicate rows while preserving the original order.

    Args:
        rows:
            The rows to deduplicate. Each row may be:
                - a dataclass instance (or class),
                - a mapping (`Mapping[str, Any]`),
                - or any other iterable of field values.
        keys:
            Optional list of column names defining the row identity.
            When provided:
                - for dataclasses, they are matched against field names;
                - for mappings, against mapping keys.
            When `None` or empty, the full value sequence (`get_row_values(row)`) defines the identity.
            Rows that expose no values for the selected `keys` are skipped.

    Returns:
        The deduplicated rows with the preserved order.
    """
    out: List[Row] = []
    seen: Set[Tuple[Any, ...]] = set()

    for row in rows:
        identity: Tuple[Any, ...] = get_row_values(row, keys)
        if keys and not identity:
            continue
        try:
            is_already_seen = identity in seen
        except TypeError as e:
            raise TypeError(f"Row identity {identity!r} derived from row {row!r} is not hashable") from e
        if not is_already_seen:
            out.append(row)
            seen.add(identity)
    return out
