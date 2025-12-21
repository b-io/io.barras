#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide structure utilities.
#
# Description
#   Provides a uniform API to access, convert, filter, transform, and combine heterogeneous
#   containers such as lists, tuples, dicts, NumPy arrays, Pandas Series/DataFrames, and Pandas
#   GroupBy objects. Functions are designed to handle empty inputs, scalar fallbacks, and mixed
#   shapes gracefully. Most APIs accept optional `keys`/`inclusion`/`exclusion` filters and many
#   are `axis`-aware.
#
#   Key ideas:
#       • Accessors (`get_*`): read names, keys, index, items, values, types, and shapes.
#       • Converters (`to_*`): coerce between containers and enforce element dtypes.
#       • Generators: build masks and shaped arrays aligned to a template.
#       • Processors: vectorized apply/calculate/filter/sort/slice/reduce utilities.
#       • Joins/Merges/Pivots: thin convenience wrappers over Pandas operations.
#
#   Conventions:
#       • “names” = column labels
#       • “index” = row index
#       • “keys” = indices or mapping keys (for non-tabular containers)
#       • `axis=None` → element-wise; `axis=0` → rows; `axis=1` → columns
########################################################################################################################

from __future__ import annotations

import functools
from numbers import Number

from pandas.core.dtypes.common import is_numeric_dtype

from nutil.config import *
from nutil.enums import Aggregation, Position
from nutil.scalar.util import *
from nutil.struct.collection.array import *
from nutil.struct.collection.list import *
from nutil.struct.collection.registry.ordered_set import *

__STRUCT_ACCESSORS________________________________________________________________________ = ""


def get(s: Struct, index: int, axis: Optional[int] = 0) -> Value:
    """
    Returns the entry at the specified `index` along `axis`.

    Notes:
        • `axis=None` → element-wise flattening before indexing.
        • `axis=0/1`  → delegates to `get_row` / `get_col`.

    Complexity:
        O(1) for direct indexing; O(n) if flattening.
    """
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s
    elif is_null(axis):
        return flatten(s, axis=axis)[index]

    if is_multidimensional(s):
        if axis == 0:
            return get_row(s, index)
        return get_col(s, index)
    elif is_dict(s):
        return s[get_keys(s)[index]]
    return s[index]


def get_first(s: Struct, axis: Optional[int] = 0) -> Value:
    """Returns the first entry along `axis` (equivalent to `get(s, 0, axis=axis)`)."""
    return get(s, 0, axis=axis)


def get_middle(s: Struct, axis: Optional[int] = 0) -> Value:
    """
    Returns the middle entry along `axis`.

    Equivalent to:
        • axis=None → get(s, (N-1)//2, axis=None) where N is the flattened size
        • axis=0    → get(s, (R-1)//2, axis=0)    where R is row count
        • axis=1    → get(s, (C-1)//2, axis=1)    where C is column count

    Notes:
        Uses floor toward the lower-middle for even lengths.
    """
    if is_null(axis):
        n = int(np.size(get_values(s)))
    else:
        n = count_rows(s) if axis == 0 else count_cols(s)
    return get(s, (n - 1) // 2, axis=axis)


def get_last(s: Struct, axis: Optional[int] = 0) -> Value:
    """Returns the last entry along `axis` (equivalent to `get(s, -1, axis=axis)`)."""
    return get(s, -1, axis=axis)


def get_iterator(s: Struct, cycle: bool = False) -> Iterator[Value]:
    """Returns an iterator over `s`, cycling if `cycle` is `True`."""
    if is_element(s):
        s = (s,)
    return itertools.cycle(s) if cycle else create_iterator(s)


def get_next(s: Struct, cycle: bool = False) -> Value:
    """Returns the next element of the iterator, or `s` itself if it is an element."""
    if is_element(s):
        return s
    return next(get_iterator(s, cycle=cycle))


##############################


def get_shape(
    s: Struct,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Tuple[int, ...]:
    """
    Returns the shape after filtering.

    Dispatch:
        • `pd.DataFrame` → `(rows, cols)`
        • `tuple`        → the `tuple` itself
        • Other `Struct` → `(len(s),)`

    Complexity:
        O(n) to filter; O(1) to read shape thereafter.
    """
    s = filter(s, keys=keys, inclusion=inclusion, exclusion=exclusion)
    if is_multidimensional(s):
        return s.shape
    elif is_tuple(s):
        return s
    return (len(s),)


##############################


def get_name(
    s: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Value:
    """Returns the simplified single name (equivalent to `simplify(get_names(…))`)."""
    return simplify(get_names(s, inclusion=inclusion, exclusion=exclusion))


def get_names(
    s: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Value]:
    """
    Returns the names (column labels) under filters.

    Dispatch:
        • `pd.DataFrame` / `pd.Series` → `.columns` / `.name`
        • Collections with index         → `range(len(s))`
        • Generic collection             → `[get_name(x) for x in s]`
        • Scalar                         → `[to_string(s)]`

    Complexity:
        O(n) over container elements.
    """
    s = ungroup(s)
    if is_table(inclusion):
        inclusion = get_names(inclusion)
    if is_table(exclusion):
        exclusion = get_names(exclusion)
    if has_callable(s, "names"):
        s = s.names()
    elif has_callable(s, "name"):
        s = s.name()
    elif is_struct(s):
        if is_series(s):
            s = s.name
        elif has_index(s):
            s = range(len(s))
        else:
            s = [get_name(x) for x in s]
    else:
        s = [to_string(s)]
    return filter_list(s, inclusion=inclusion, exclusion=exclusion)


def get_all_common_names(
    *args: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Value]:
    """Returns the common names across all inputs (left fold)."""
    return reduce(
        args,
        lambda s1, s2: get_common_names(s1, s2, inclusion=inclusion, exclusion=exclusion),
    )


def get_common_names(
    s1: Struct,
    s2: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Value]:
    """Returns the names common to `s1` and `s2` under filters."""
    return get_names(s1, inclusion=include_list(get_names(s2), inclusion), exclusion=exclusion)


def get_all_uncommon_names(
    *args: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Value]:
    """Returns the names in the first input not present in subsequent ones (left fold)."""
    return reduce(
        args,
        lambda s1, s2: get_uncommon_names(s1, s2, inclusion=inclusion, exclusion=exclusion),
    )


def get_uncommon_names(
    s1: Struct,
    s2: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Value]:
    """Returns the names of `s1` that are not in `s2` under filters."""
    return get_names(s1, inclusion=inclusion, exclusion=include_list(get_names(s2), exclusion))


##############################


def get_key(
    s: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Key:
    """Returns the simplified single key (index/key/name)."""
    return simplify(get_keys(s, inclusion=inclusion, exclusion=exclusion))


def get_keys(
    s: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> OrderedSet:
    """
    Returns keys (indices/keys/names) under filters.

    Dispatch:
        • `pd.Series`           → `.index`
        • Indexed collections   → `range(len(s))`
        • Dict/other            → as filtered

    Complexity:
        O(n) to derive/filter keys.
    """
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return OrderedSet()

    # Resolve the inclusion and the exclusion
    if is_table(inclusion):
        inclusion = get_keys(inclusion)
    if is_table(exclusion):
        exclusion = get_keys(exclusion)

    if is_series(s):
        s = s.index
    elif has_index(s):
        s = range(len(s))
    return filter_ordered_set(s, inclusion=inclusion, exclusion=exclusion)


def get_all_common_keys(
    *args: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> OrderedSet:
    """Returns the common keys across all inputs (left fold)."""
    return reduce(
        args,
        lambda s1, s2: get_common_keys(s1, s2, inclusion=inclusion, exclusion=exclusion),
    )


def get_common_keys(
    s1: Struct,
    s2: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> OrderedSet:
    """Returns keys common to `s1` and `s2` under filters."""
    return get_keys(s1, inclusion=include_list(get_keys(s2), inclusion), exclusion=exclusion)


def get_all_uncommon_keys(
    *args: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> OrderedSet:
    """Returns keys in the first input not present in subsequent ones (left fold)."""
    return reduce(
        args,
        lambda s1, s2: get_uncommon_keys(s1, s2, inclusion=inclusion, exclusion=exclusion),
    )


def get_uncommon_keys(
    s1: Struct,
    s2: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> OrderedSet:
    """Returns keys of `s1` that are not in `s2` under filters."""
    return get_keys(s1, inclusion=inclusion, exclusion=include_list(get_keys(s2), exclusion))


##############################


def get_index(
    s: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Key]:
    """
    Returns the index under filters.

    Dispatch:
        • `GroupBy(axis=0)` → unwraps to `.groups` first.
        • `GroupBy(axis=1)` → unwraps to `.obj` first.
        • `pd.DataFrame`    → returns filtered `s.index`.
        • 2D `np.ndarray`   → returns filtered `range(count_cols(s))`.
        • Fallback          → returns filtered `get_keys(s)`.

    Complexity:
        O(n) to filter; O(1) to read from Pandas thereafter.
    """
    s = ungroup(s, axis=1)
    if is_table(inclusion):
        inclusion = get_index(inclusion)
    if is_table(exclusion):
        exclusion = get_index(exclusion)
    if is_table(s):
        return filter_list(s.index, inclusion=inclusion, exclusion=exclusion)
    elif is_array(s):
        return filter_list(range(count_cols(s)), inclusion=inclusion, exclusion=exclusion)
    return list(get_keys(s, inclusion=inclusion, exclusion=exclusion))


def get_index_name(s: Struct) -> Optional[Value]:
    """Returns the index name(s) for a `pd.DataFrame`; otherwise returns `None`."""
    if is_table(s):
        if isinstance(s.index, pd.MultiIndex):
            return s.index.names
        return s.index.name
    return None


def get_all_common_index(
    *args: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Key]:
    """Returns the common indices across all inputs (left fold)."""
    return reduce(
        args,
        lambda s1, s2: get_common_index(s1, s2, inclusion=inclusion, exclusion=exclusion),
    )


def get_common_index(
    s1: Struct,
    s2: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Key]:
    """Returns the indices common to `s1` and `s2` under filters."""
    return get_index(s1, inclusion=include_list(get_index(s2), inclusion), exclusion=exclusion)


def get_all_uncommon_index(
    *args: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Key]:
    """Returns the indices in the first input not present in subsequent ones (left fold)."""
    return reduce(
        args,
        lambda s1, s2: get_uncommon_index(s1, s2, inclusion=inclusion, exclusion=exclusion),
    )


def get_uncommon_index(
    s1: Struct,
    s2: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Key]:
    """Returns the indices of `s1` that are not in `s2` under filters."""
    return get_index(s1, inclusion=inclusion, exclusion=include_list(get_index(s2), exclusion))


##############################


def get_keys_or_index(
    s: Struct,
    axis: int = 0,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
):
    """Returns the keys if `axis=0`, otherwise the index, under filters."""
    return (
        get_keys(s, inclusion=inclusion, exclusion=exclusion)
        if axis == 0
        else get_index(s, inclusion=inclusion, exclusion=exclusion)
    )


def get_index_or_keys(
    s: Struct,
    axis: int = 0,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
):
    """Returns the index if `axis=0`, otherwise the keys, under filters."""
    return (
        get_index(s, inclusion=inclusion, exclusion=exclusion)
        if axis == 0
        else get_keys(s, inclusion=inclusion, exclusion=exclusion)
    )


##############################


def get_item(
    s: Struct,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Tuple[Key, Value]:
    """Returns the simplified single `(key, value)` under filters."""
    return simplify(get_items(s, keys=keys, inclusion=inclusion, exclusion=exclusion))


def get_items(
    s: Struct,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> List[Tuple[Key, Value]]:
    """
    Returns `(key, value)` items under filters.

    Dispatch:
        • `pd.DataFrame` (non-GroupBy) / `dict` with no filters → `list(s.items())`
        • Otherwise, → materializes via derived `keys`.

    Complexity:
        O(n) to derive/filter; O(n) to materialize items.
    """
    if is_empty(s):
        return []
    elif not is_subscriptable(s):
        return to_list(s)

    if not has_filter(keys=keys, inclusion=inclusion, exclusion=exclusion):
        if (is_table(s) and not is_group_by(s)) or is_dict(s):
            return to_list(s.items())

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return []

    if is_group_by(s):
        if s.axis == 0:
            return [(k, filter(v, keys=keys)) for k, v in s]
        return [(k, v) for k, v in s if k in keys]
    return [(k, s[k]) for k in keys]


##############################


def get_value(
    s: Struct,
    element_type: Optional[ElementType] = None,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Value:
    """Returns a simplified single value (or structure of values) under filters."""
    return simplify(get_values(s, element_type=element_type, keys=keys, inclusion=inclusion, exclusion=exclusion))


def get_values(
    s: Struct,
    element_type: Optional[ElementType] = None,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
):
    """
    Returns the values under filters.

    Dispatch:
        • `GroupBy(axis=0)` → array of values filtered by row keys
        • `GroupBy(axis=1)` → array of values filtered by column keys
        • `pd.DataFrame`    → array of values filtered by keys
        • `np.ndarray`      → `s[keys]`
        • Fallback          → array from `[s[k] for k in keys]`

    Complexity:
        O(n) to filter/gather; shape depends on `keys` and container.
    """
    if is_empty(s):
        return to_array(element_type=element_type)
    elif not is_subscriptable(s):
        return to_array(s, element_type=element_type)

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return to_array(element_type=element_type)

    if is_group_by(s):
        if s.axis == 0:
            return to_array([filter(v, keys=keys).values for k, v in s], element_type=element_type)
        return to_array([v.values for k, v in s if k in keys], element_type=element_type)
    elif is_table(s):
        return filter(s, keys=keys).to_numpy(dtype=element_type)
    elif is_array(s):
        return s[keys]
    return to_array([s[k] for k in keys], element_type=element_type)


##############################


def get_element_type(
    s: Struct,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Value:
    """Returns the simplified element type(s) under filters."""
    return get_value(get_element_types(s, keys=keys, inclusion=inclusion, exclusion=exclusion))


def get_element_types(
    s: Struct,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Dict[Key, ElementType]:
    """
    Returns element type(s) under filters.

    Dispatch:
        • `pd.DataFrame`            → `dict` of dtypes (filtered columns)
        • `pd.Series` / `np.ndarray`→ scalar dtype
        • Objects with `.dtypes`    → `dict(s.dtypes)`
        • Objects with `.dtype`     → `{get_name(s): s.dtype}`
        • Fallback                  → `{k: type(s[k]) for k in keys}`

    Complexity:
        O(n) over selected keys/columns.
    """
    if is_empty(s):
        return {}
    elif not is_subscriptable(s):
        return {i: type(x) for i, x in enumerate(to_list(s))}

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return {}

    if is_frame(s):
        return to_dict(filter(s, keys=keys).dtypes)
    elif is_series(s) or is_array(s):
        return to_dict(s.dtype)
    elif hasattr(s, "dtypes"):
        return to_dict(s.dtypes)
    elif hasattr(s, "dtype"):
        return to_dict(s.dtype)
    return {k: type(s[k]) for k in keys}


def get_min_element_type(
    s1: Any,
    s2: Any,
    min_element_type: Any = FLOAT_ELEMENT_TYPE,
) -> np.dtype:
    """
    Returns an element type that can safely represent `s1`, `s2`, and `min_element_type`.

    Notes:
        • Uses NumPy type promotion:
            – Computes the combined element type of `s1` and `s2` via `np.result_type`.
            – Promotes it with `min_element_type` via `np.promote_types`.
        • Works for any numeric or comparable element type (`bool`, complex, `float`, integer,
          string, and compatible NumPy dtypes).

    Complexity:
        O(1).
    """
    return np.promote_types(np.result_type(s1, s2), np.dtype(min_element_type))


### SETTERS ################################################


def set_names(s: Struct, new_names: Any) -> Any:
    """
    Sets the names of the specified `Struct`.

    Dispatch:
        • `pd.DataFrame` → sets `s.columns`.
        • `pd.Series`    → sets `s.name` (simplified if a sequence is provided).
        • Fallback       → delegates to `set_keys`.

    Notes:
        • If `new_names` is a table, its names are extracted via `get_names(new_names)`.
        • No-op for empty or non-subscriptable inputs.
    """
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Normalize the names
    if is_table(new_names):
        new_names = get_names(new_names)
    else:
        new_names = to_list(new_names)
    if is_empty(new_names):
        return s

    if is_frame(s):
        s.columns = new_names
    elif is_series(s):
        s.name = simplify(new_names)
    else:
        set_keys(s, new_names)
    return s


def set_keys(
    s: Struct,
    new_keys: Any,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """
    Sets keys (indices/keys/names) on the specified `Struct` under filters.

    Dispatch:
        • `pd.DataFrame` → renames selected columns via `.loc[:, keys].columns = new_keys`.
        • `pd.Series`    → delegates to `set_index`.
        • `dict`         → rebuilds mapping with renamed keys.
        • Fallback       → updates positional container via `update(…)`.

    Notes:
        • `new_keys` can be a table; in that case, its keys are derived via `get_keys(new_keys)`.
        • If `keys` is `None`, uses `get_keys(s, …)`.
        • No-op for empty/invalid combinations.

    Complexity:
        O(k) where `k = len(keys)`; dictionary remapping is O(k).
    """
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return s

    # Normalize the keys
    if is_table(new_keys):
        new_keys = get_keys(new_keys)
    else:
        new_keys = to_ordered_set(new_keys)
    if is_empty(new_keys):
        return s

    if is_frame(s):
        s.loc[:, keys].columns = new_keys
    elif is_series(s):
        set_index(s, new_keys)
    elif is_dict(s):
        upsert(s, {new_key: s.pop(k) for k, new_key in zip(keys, new_keys)})
    else:
        update(s, {new_key: s[k] for k, new_key in zip(keys, new_keys)}, keys=keys)
    return s


def set_index(s: Struct, new_index: Any, index_name: Optional[str] = None) -> Any:
    """
    Sets the index (indices/keys/index) on the specified `Struct`.

    Dispatch:
        • `GroupBy(axis=0)` → unwraps to `.groups` first.
        • `GroupBy(axis=1)` → unwraps to `.obj` first.
        • `pd.DataFrame`    → sets `s.index` (supports `pd.MultiIndex` via tuples).
        • Fallback          → delegates to `set_keys`.

    Notes:
        • If `new_index` is a table, the new index values come from `new_index.index` and the
          index names from `get_names(new_index.index)`. Otherwise, both are derived from `new_index`.
        • Multi-index input (sequence of tuples) triggers construction of a `pd.MultiIndex` with
          names resized appropriately.
        • After assignment, sets the index name(s) via `set_index_name(s, index_name)`.

    Complexity:
        O(n) over length of `new_index`.
    """
    s = ungroup(s, axis=1)
    if not is_subscriptable(s):
        return s

    # Normalize the index
    if is_table(new_index):
        new_index_names = get_names(new_index.index)
        new_index = new_index.index
    else:
        new_index_names = get_names(new_index)
        new_index = to_list(new_index)
    if is_empty(new_index):
        return s

    if is_table(s):
        if not is_empty(new_index) and is_tuple(new_index[0]):
            new_index_names = resize_list(new_index_names, len(new_index[0]))
            s.index = pd.MultiIndex.from_tuples(new_index, names=new_index_names)
        else:
            s.index = new_index
    else:
        set_keys(s, new_index)
    if not is_null(index_name):
        set_index_name(s, index_name)
    return s


def set_index_name(s: Struct, index_name: Any) -> Any:
    """
    Sets the index name(s) on a `pd.DataFrame` in place; returns the structure.

    Notes:
        • If `s.index` is a `pd.MultiIndex`, accepts either a sequence of names or a single base
          name which is expanded as `name1`, `name2`, … to match the number of levels.
        • No-op for empty `index_name` or non-tabular inputs.
    """
    if is_empty(index_name):
        return s
    if is_table(s):
        if isinstance(s.index, pd.MultiIndex):
            s.index.names = (
                index_name if is_struct(index_name) else [index_name + str(i + 1) for i in range(len(s.index.names))]
            )
        else:
            s.index.name = index_name
    return s


def set_values(
    s: Struct,
    new_values: Any,
    mask: Optional[Any] = None,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """
    Sets values on the specified `Struct` under filters (optionally using a mask).

    Dispatch:
        • `pd.DataFrame` → assigns via `.loc[:, keys]` with shape `(rows, len(keys))`.
        • `pd.Series`    → assigns via `.loc[keys]`.
        • `np.ndarray`   → assigns via advanced indexing `s[keys]`.
        • Fallback       → assigns per key in a loop.

    Notes:
        • If `new_values` is a collection, it is normalized via `get_values(new_values)`.
          Otherwise, when no mask or non-multidimensional `s`, broadcasts a scalar to an array
          shaped as `get_shape(s, keys=keys)`.
        • When `mask` is provided:
            – Multidimensional `s`: uses native masked assignment `s[mask] = new_values`.
            – Other `Struct`: loops over `keys` and applies element-wise where `mask[i]` is truthy.
        • Pandas chained assignment warnings are temporarily disabled during `.loc` assignments.

    Complexity:
        O(k) to materialize/broadcast plus O(assignments) where `k = len(keys)`.
    """
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return s

    # Normalize the values
    if is_struct(new_values):
        new_values = get_values(new_values)
    else:
        if not is_multidimensional(s) or is_null(mask):
            new_values = create_array(get_shape(s, keys=keys), fill=new_values)

    if not is_null(mask):
        if is_multidimensional(s):
            s[mask] = new_values
        else:
            for i, k in enumerate(keys):
                if mask[i]:
                    s[k] = new_values[i]
    else:
        if is_empty(new_values):
            return s
        if is_frame(s):
            set_element_types(s, get_element_type(new_values), keys=keys)
            chained_assignment = pd.options.mode.chained_assignment
            try:
                pd.options.mode.chained_assignment = None
                s.loc[:, keys] = new_values.reshape(count_rows(s), len(keys))
            finally:
                pd.options.mode.chained_assignment = chained_assignment
        elif is_series(s):
            set_element_types(s, get_element_type(new_values), keys=keys)
            chained_assignment = pd.options.mode.chained_assignment
            try:
                pd.options.mode.chained_assignment = None
                s.loc[keys] = new_values
            finally:
                pd.options.mode.chained_assignment = chained_assignment
        elif is_array(s):
            s[keys] = new_values
        else:
            for i, k in enumerate(keys):
                s[k] = new_values[i]
    return s


def set_element_types(
    s: Struct,
    new_element_types: Any,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """
    Sets element type(s) on the specified `Struct` under filters, converting values in place.

    Dispatch:
        • `pd.DataFrame`             → vectorized `.astype(…)` per column (dates handled via `pd.to_datetime`).
        • `pd.Series` / `np.ndarray` → `.astype(…)`.
        • `dict`                     → rebuilds the values using `to_element_type`.
        • Fallback                   → updates per key using `to_element_type`.

    Notes:
        • `new_element_types` can be:
            – A mapping `{key: dtype}`.
            – A collection whose types are inferred via `get_element_types(…, keys=keys)`.
            – A single dtype applied across all selected keys (except for `Series`/`ndarray`, where
              a mapping or scalar dtype is expected).
        • `DATE_TYPE`/`DATETIME_TYPE`/`TIMESTAMP_TYPE` are converted with `pd.to_datetime`.
        • No-op for empty inputs or if `keys` resolve to an empty set.

    Complexity:
        O(k) to coerce `k = len(keys)` columns/items; Pandas/Numpy casting is vectorized.
    """
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return s

    # Normalize the element types and align the keys
    if not is_dict(new_element_types):
        if is_iterable(new_element_types):
            # Intersect the keys with the element type keys
            keys = get_keys(new_element_types) & keys
            new_element_types = get_element_types(new_element_types, keys=keys)
        elif not is_series(s) and not is_array(s):
            new_element_types = {k: new_element_types for k in keys}
    if is_empty(new_element_types):
        return s

    # Filter the new element types
    if is_frame(s):
        s[to_list(keys)] = s.loc[:, keys].astype(
            {k: t for k, t in new_element_types.items() if not is_date_type(t)},
            copy=False,
        )
        date_cols = [k for k, t in new_element_types.items() if is_date_type(t)]
        if not is_empty(date_cols):
            s[date_cols] = s.loc[:, date_cols].apply(pd.to_datetime)
    elif is_series(s) or is_array(s):
        if is_dict(new_element_types):
            new_element_types = get_value(new_element_types)
        s = s.astype(new_element_types, copy=False)
    elif is_dict(s):
        upsert(
            s,
            {k: to_element_type(s.pop(k), new_element_type) for k, new_element_type in new_element_types.items()},
        )
    else:
        update(
            s,
            {k: to_element_type(s[k], new_element_type) for k, new_element_type in new_element_types.items()},
            keys=keys,
        )
    return s


__TABLE_ACCESSORS___________________________________________ = ""


def get_row(df: Struct, i: int = 0) -> Value:
    """
    Returns the row at the specified integer position `i`.

    Dispatch:
        • `pd.core.groupby.GroupBy` → extracts the values with `get_values(df)` then proceeds.
        • `pd.DataFrame`            → returns a single-row `DataFrame` using `.iloc`;
                                       if `i == -1`, returns the last row as `df.iloc[i:]`.

    Complexity:
        O(1) for indexed access on array-like/Pandas.

    Notes:
        • Only the `DataFrame` branch special-cases `i == -1` to keep a one-row `DataFrame`.
        • Negative indices follow the native semantics of the underlying container.

    Returns:
        A one-row `pd.DataFrame` for `DataFrame` input; otherwise the container native row object.
    """
    if is_group_by(df):
        df = get_values(df)
    elif is_table(df):
        return df.iloc[i:] if i == -1 else df.iloc[i : i + 1]
    return df[i]


def get_first_row(df: Struct) -> Value:
    """Returns the first row (equivalent to `get_row(df, 0)`)."""
    return get_row(df, 0)


def get_last_row(df: Struct) -> Value:
    """Returns the last row (equivalent to `get_row(df, -1)`)."""
    return get_row(df, -1)


def get_rows(df: Struct) -> List[Value]:
    """
    Returns all rows as an ordered sequence.

    Dispatch:
        • `pd.DataFrame` → `[row for _, row in df.iterrows()]` (each row is a `Series`).
        • `pd.Series`    → `[row for _, row in df.items()]`.

    Complexity:
        O(n_rows).

    Notes:
        • Row order follows the input index order.
        • For `DataFrame`, each element is a copy-like `Series` per Pandas semantics.

    Returns:
        `list` of row objects.
    """
    if is_frame(df):
        return [row for _, row in df.iterrows()]
    elif is_series(df):
        return [row for _, row in df.items()]
    return [get_row(df, i) for i in range(count_rows(df))]


##############################


def get_col(df: Struct, j: int = 0) -> Value:
    """
    Returns the column at the specified integer position `j`.

    Dispatch:
        • `pd.core.groupby.GroupBy` → extracts the values with `get_values(df)` then proceeds.
        • `pd.DataFrame`            → `df.iloc[:, j]` (a `Series`).
        • `pd.Series`               → `df.iloc[:]` (the series itself).

    Complexity:
        O(1) for indexed access on array-like/Pandas.

    Notes:
        • Negative `j` is supported per the underlying container semantics.

    Returns:
        The selected column as the container native column object (e.g., `pd.Series`).
    """
    if is_group_by(df):
        df = get_values(df)
    elif is_frame(df):
        return df.iloc[:, j]
    elif is_series(df):
        return df.iloc[:]
    return df[:, j]


def get_first_col(df: Struct) -> Value:
    """Returns the first column (equivalent to `get_col(df, 0)`)."""
    return get_col(df, 0)


def get_last_col(df: Struct) -> Value:
    """Returns the last column (equivalent to `get_col(df, -1)`)."""
    return get_col(df, -1)


def get_cols(df: Struct) -> List[Value]:
    """
    Returns all columns as an ordered sequence.

    Dispatch:
        • `pd.DataFrame` → `[col for _, col in df.items()]` (each column is a `Series`).
        • `pd.Series`    → `[df]`.

    Complexity:
        O(n_cols).

    Notes:
        • Column order follows the input column order.
        • For `DataFrame`, each element is a `Series` that may share data with `df`.

    Returns:
        `list` of column objects.
    """
    if is_frame(df):
        return [col for _, col in df.items()]
    elif is_series(df):
        return [df]
    return [get_col(df, j) for j in range(count_cols(df))]


__STRUCT_CONVERTERS_______________________________________________________________________ = ""


def to_struct(*args: Any) -> Struct:
    """
    Returns a `Struct` from the specified arguments.

    Behavior:
        • One argument:
            – If it is a `Struct`, returns it unchanged.
            – Otherwise, wraps it into a one-element `list`.
        • Multiple arguments: returns a `list` via `to_list(*args)`.

    Complexity:
        O(1) for single argument; O(n) for materializing `list` of n args.
    """
    if len(args) == 1:
        arg = args[0]
        if is_struct(arg):
            return arg
        return [arg]
    return to_list(*args)


def unstruct(s: Any) -> Any:
    """
    Returns a simplified value from a `Struct`.

    Behavior:
        • If `s` is a `Struct`:
            – If it has length 1, returns its single element (`get_next(s)`).
            – Otherwise, returns `tuple(s)`.
        • Otherwise, returns `s` unchanged.

    Complexity:
        O(1) for length check; O(n) to build a `tuple` of length n.
    """
    if is_struct(s):
        if len(s) == 1:
            return get_next(s)
        return tuple(s)
    return s


##############################


def to_element_type(x: Any, t: Type[Any]) -> Any:
    """
    Converts `x` to the specified element type `t`.

    Behavior:
        • If `type(x) is t`, returns `x` unchanged.
        • If `t` is a `tuple` (shape-like/type-spec), converts to `tuple` via `to_tuple(x)`.
        • Else if `t` is a scalar *type* or scalar-like spec, converts via `to_scalar(x, t)`.
        • Otherwise, returns `x`.

    Notes:
        • This is a light, defensive adapter around higher-level coercers.
        • `t` may be a Python type (e.g., `int`, `float`) or a NumPy dtype; this function does
          not enforce NumPy promotion—use higher-level converters if needed.

    Complexity:
        O(1).
    """
    if type(x) is t:
        return x
    if is_tuple_type(t):
        return to_tuple(x)
    elif is_scalar_type(t):
        return to_scalar(x, t)
    return x


def struct_to_type(
    s: Struct,
    template: Any,
    element_type: Optional[ElementType] = None,
) -> Any:
    """
    Converts collection `s` to match the *container type* of `template`.

    Dispatch:
        • `pd.DataFrame` → `to_frame(s, names=template, index=template)`
        • `pd.Series`    → `to_series(s, name=template, index=template)`
        • `dict`         → `dict(zip(get_keys(template), s))`
        • `OrderedSet`   → `to_ordered_set(s)`
        • `set`          → `to_set(s)`
        • `np.ndarray`   → `to_array(s, element_type=element_type)`
        • `list`         → `to_list(s)`
        • Fallback       → `s` unchanged

    Complexity:
        O(n) to materialize the target container.
    """
    if is_frame(template):
        return to_frame(s, names=template, index=template)
    elif is_series(template):
        return to_series(s, name=template, index=template)
    elif is_dict(template):
        return dict(zip(get_keys(template), s))
    elif is_ordered_set(template):
        return to_ordered_set(s)
    elif is_set(template):
        return to_set(s)
    elif is_array(template):
        return to_array(s, element_type=element_type)
    elif is_list(template):
        return to_list(s)
    return s


def struct_to_common_type(s: Struct, template: Any) -> Any:
    """
    Converts collection `s` to a *common* container type compatible with `template`.

    Dispatch:
        • `pd.DataFrame` → `to_frame(s)`
        • `pd.Series`    → `to_series(s)`
        • `dict`         → `to_dict(s)`
        • `OrderedSet`   → `to_ordered_set(s)`
        • `set`          → `to_set(s)`
        • `np.ndarray`   → `to_array(s)`
        • `list`         → `to_list(s)`
        • Fallback       → `s` unchanged

    Complexity:
        O(n) to materialize the target container.
    """
    if is_frame(template):
        return to_frame(s)
    elif is_series(template):
        return to_series(s)
    elif is_dict(template):
        return to_dict(s)
    elif is_ordered_set(template):
        return to_ordered_set(s)
    elif is_set(template):
        return to_set(s)
    elif is_array(template):
        return to_array(s)
    elif is_list(template):
        return to_list(s)
    return s


__COLLECTION_CONVERTERS_____________________________________ = ""


def to_collection(*args: Any) -> Collection:
    """
    Returns a collection from the specified arguments.

    Behavior:
        • One argument:
            – If it is a collection, returns it unchanged.
            – Otherwise, wraps it into a one-element `list`.
        • Multiple arguments: returns a `list` via `to_list(*args)`.

    Complexity:
        O(1) for single argument; O(n) to build a `list` of n args.
    """
    if len(args) == 1:
        arg = args[0]
        if is_collection(arg):
            return arg
        return [arg]
    return to_list(*args)


def to_indexed_collection(*args: Any) -> Collection:
    """
    Returns an indexed collection from the specified arguments (or wraps single value).

    Behavior:
        • One argument:
            – If it is a collection *and* `has_index(arg)`, returns it unchanged.
            – Otherwise, wraps it into a one-element `list`.
        • Multiple arguments: returns a `list` via `to_list(*args)`.

    Complexity:
        O(1) for single argument; O(n) to build a `list` of n args.
    """
    if len(args) == 1:
        arg = args[0]
        if is_collection(arg) and has_index(arg):
            return arg
        return [arg]
    return to_list(*args)


def to_subscriptable_collection(*args: Any) -> Any:
    """
    Returns a subscriptable collection from the specified arguments (or wraps single value).

    Behavior:
        • One argument:
            – If it is subscriptable (`__getitem__`), returns it unchanged.
            – Otherwise, wraps it into a one-element `list`.
        • Multiple arguments: returns a `list` via `to_list(*args)`.

    Complexity:
        O(1) for single argument; O(n) to build a `list` of n args.
    """
    if len(args) == 1:
        arg = args[0]
        if is_subscriptable(arg):
            return arg
        return [arg]
    return to_list(*args)


def uncollect(c: Any) -> Any:
    """
    Returns a simplified value from a collection.

    Behavior:
        • If `c` is a collection:
            – If it has length 1, returns its single element (`get_next(c)`).
            – Otherwise, returns `tuple(c)`.
        • Otherwise, returns `c` unchanged.

    Complexity:
        O(1) for length check; O(n) to build a `tuple` of length n.
    """
    if is_collection(c):
        if len(c) == 1:
            return get_next(c)
        return tuple(c)
    return c


__TABLE_CONVERTERS__________________________________________ = ""


def to_series(
    data: Any,
    name: Optional[Any] = None,
    index: Optional[Any] = None,
    element_type: Optional[ElementType] = None,
) -> Union["pd.Series", List["pd.Series"]]:
    """
    Converts the specified collection to a `pd.Series` or a `list` of `Series` when `data` is a multi-column `DataFrame`.

    Dispatch:
        • Empty non-table input → creates empty series with `dtype=OBJECT_TYPE`.
        • `GroupBy`             → unwraps to `.obj`.
        • Scalar-like input     → broadcasts to length of `index` (if provided).
        • `DataFrame`:
            – If `count_cols(data) > 1`, returns `get_cols(data)` (a `list` of series).
            – Else returns the single column as a series (or empty series with dtype).
        • `Series`              → returns a copy.
        • Other collections     → constructs `pd.Series(data=data, dtype=element_type)`.

    Notes:
        • If `name` is provided, sets it via `set_names(series, name)`.
        • If `index` is provided, sets it via `set_index(series, index)`.

    Complexity:
        O(n) to materialize the series or a `list` of series.
    """
    if is_empty(data) and not is_table(data):
        data = []
        element_type = OBJECT_TYPE
    elif is_group_by(data):
        data = data.obj
    elif is_element(data) and not is_null(index):
        data = create_array(len(get_index(index)), fill=data, element_type=element_type)
    if is_frame(data):
        if count_cols(data) > 1:
            return get_cols(data)
        series = get_col(data) if not is_empty(data) else pd.Series(data=data, dtype=element_type)
    elif is_series(data):
        series = data.copy()
    else:
        series = pd.Series(data=data, dtype=element_type)
    if not is_null(name):
        set_names(series, name)
    if not is_null(index):
        set_index(series, index)
    return series


def to_time_series(
    data: Any,
    name: Optional[Any] = None,
    index: Optional[Any] = None,
    element_type: Any = FLOAT_ELEMENT_TYPE,
) -> "pd.Series":
    """
    Converts the specified collection to a time `pd.Series`.

    Notes:
        • If `index` is provided, converts it to timestamps via `to_timestamp(to_array(index))`.
        • Delegates to `to_series` for construction and naming.

    Complexity:
        O(n) to convert the index and materialize the series.
    """
    if not is_null(index):
        index = to_timestamp(to_array(index))
    return to_series(data, name=name, index=index, element_type=element_type)


##############################


def to_frame(
    data: Any,
    names: Optional[Any] = None,
    index: Optional[Any] = None,
    index_name: Optional[str] = None,
    element_type: Optional[ElementType] = None,
) -> "pd.DataFrame":
    """
    Converts the specified collection to a `pd.DataFrame`.

    Dispatch:
        • Empty non-table input → creates empty frame with `dtype=OBJECT_TYPE`.
        • `GroupBy`            → unwraps to `.obj`.
        • Scalar-like input    → broadcasts to shape `(len(index), len(names))` if provided.
        • `DataFrame`          → returns a copy.
        • `Series`             → converts via `.to_frame()`.
        • `dict`               → `pd.DataFrame.from_dict(…, orient="index")`.
        • Other collections    → `pd.DataFrame(data=data, dtype=element_type)`.

    Notes:
        • Applies `set_names(frame, names)` and `set_index(frame, index)` if provided.
        • Sets the index name via `set_index_name(frame, index_name)`.

    Complexity:
        O(n) to materialize the frame and assign names/index.
    """
    # Normalize the data
    if is_empty(data) and not is_table(data):
        data = []
        element_type = OBJECT_TYPE
    elif is_group_by(data):
        data = data.obj
    elif is_element(data) and not is_null(index) and not is_null(names):
        data = create_array(
            (len(get_index(index)), len(get_names(names))),
            fill=data,
            element_type=element_type,
        )

    if is_frame(data):
        df = data.copy()
    elif is_series(data):
        df = data.to_frame()
    elif is_dict(data):
        df = pd.DataFrame.from_dict(data, dtype=element_type, orient="index")
    else:
        df = pd.DataFrame(data=data, dtype=element_type)

    if not is_null(names):
        set_names(df, names)
    if not is_null(index):
        set_index(df, index)
    if not is_null(index_name):
        set_index_name(df, index_name)
    return df


def to_time_frame(
    data: Any,
    names: Optional[Any] = None,
    index: Optional[Any] = None,
    index_name: Optional[str] = None,
    element_type: Any = FLOAT_ELEMENT_TYPE,
) -> "pd.DataFrame":
    """
    Converts the specified collection to a time `pd.DataFrame`.

    Notes:
        • If `index` is provided, converts it to timestamps via `to_timestamp(to_array(index))`.
        • Delegates to `to_frame` for construction, naming, and index name assignment.

    Complexity:
        O(n) to convert the index and materialize the frame.
    """
    if not is_null(index):
        index = to_timestamp(to_array(index))
    return to_frame(data, names=names, index=index, index_name=index_name, element_type=element_type)


__STRUCT_FILTERS__________________________________________________________________________ = ""


def filter(
    s: Struct,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """
    Returns the entries of `s` restricted to `keys` (or derived via `inclusion`/`exclusion`).

    Notes:
        • For `GroupBy(axis=0)`, filters by index; for `GroupBy(axis=1)`, by column names.
        • For `DataFrame`: uses `s.loc[:, keys]`; for `Series`: `s.loc[keys]`.
    """
    if is_empty(s) or not is_subscriptable(s) or not has_filter(keys=keys, inclusion=inclusion, exclusion=exclusion):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    if is_group_by(s):
        if s.axis == 0:
            keys = get_index(s, inclusion=inclusion, exclusion=exclusion)
        return s.filter(lambda x: x.name in keys)
    elif is_frame(s):
        return s.loc[:, keys]
    elif is_series(s):
        return s.loc[keys]
    elif is_dict(s):
        return {k: s[k] for k in keys}
    elif is_array(s):
        return s[keys]
    return struct_to_type([s[k] for k in keys], s)


def include(s: Struct, inclusion: Iterable[Key]) -> Any:
    """Returns the entries of `s` restricted to `inclusion`."""
    return filter(s, inclusion=inclusion)


def exclude(s: Struct, exclusion: Iterable[Key]) -> Any:
    """Returns the entries of `s` excluding `exclusion`."""
    return filter(s, exclusion=exclusion)


##############################


def filter_index(
    s: Struct,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Returns the entries of `s` restricted by index (`inclusion`/`exclusion` against the index)."""
    if is_empty(s) or not is_subscriptable(s) or not has_filter(inclusion=inclusion, exclusion=exclusion):
        return s
    index = get_index(s, inclusion=inclusion, exclusion=exclusion)
    if is_group_by(s):
        if s.axis == 1:
            index = get_keys(s, inclusion=inclusion, exclusion=exclusion)
        return s.filter(lambda x: x.name in index)
    elif is_table(s):
        return s.loc[s.index.isin(index)]
    return filter(s, keys=index)


def include_index(s: Struct, inclusion: Iterable[Key]) -> Any:
    """Includes the entries by index membership."""
    return filter_index(s, inclusion=inclusion)


def exclude_index(s: Struct, exclusion: Iterable[Key]) -> Any:
    """Excludes the entries by index membership."""
    return filter_index(s, exclusion=exclusion)


##############################


def filter_with(
    s: Struct,
    f: Callable[..., bool],
    *args: Any,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
    **kwargs: Any,
) -> Any:
    """Returns the entries whose values return `True` with function `f` for all selected keys."""
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    if is_group_by(s):
        if s.axis == 0:
            keys = get_index(s, inclusion=inclusion, exclusion=exclusion)
        return s.filter(lambda x: x.name in keys and all_values(apply(x, f, *args, **kwargs)))
    elif is_table(s):
        mask = create_mask(s, *args, condition=f, keys=keys, **kwargs)
        if is_frame(s):
            return s.loc[reduce_and(mask, axis=1)]
        return s.loc[mask]
    elif is_dict(s):
        return {k: s[k] for k in keys if f(s[k], *args, **kwargs)}
    return struct_to_type([s[k] for k in keys if f(s[k], *args, **kwargs)], s)


def filter_not_with(
    s: Struct,
    f: Callable[..., bool],
    *args: Any,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
    **kwargs: Any,
) -> Any:
    """Returns the entries whose values return `False` with function `f` for all selected keys."""
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    if is_group_by(s):
        if s.axis == 0:
            keys = get_index(s, inclusion=inclusion, exclusion=exclusion)
        return s.filter(lambda x: x.name in keys and all_not_values(apply(x, f, *args, **kwargs)))
    elif is_table(s):
        mask = create_mask(s, condition=lambda x: not f(x, *args, **kwargs), keys=keys)
        if is_frame(s):
            return s.loc[reduce_and(mask, axis=1)]
        return s.loc[mask]
    elif is_dict(s):
        return {k: s[k] for k in keys if not f(s[k], *args, **kwargs)}
    return struct_to_type([s[k] for k in keys if not f(s[k], *args, **kwargs)], s)


def filter_any_with(
    s: Struct,
    f: Callable[..., bool],
    *args: Any,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
    **kwargs: Any,
) -> Any:
    """Returns the entries whose values return `True` with function `f` for at least one selected key."""
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    if is_group_by(s):
        if s.axis == 0:
            keys = get_index(s, inclusion=inclusion, exclusion=exclusion)
        return s.filter(lambda x: x.name in keys and any_values(apply(x, f, *args, **kwargs)))
    elif is_table(s):
        mask = create_mask(s, *args, condition=f, fill=False, keys=keys, **kwargs)
        if is_frame(s):
            return s.loc[reduce_or(mask, axis=1)]
        return s.loc[mask]
    elif is_dict(s):
        return {k: s[k] for k in keys if f(s[k], *args, **kwargs)}
    return struct_to_type([s[k] for k in keys if f(s[k], *args, **kwargs)], s)


def filter_any_not_with(
    s: Struct,
    f: Callable[..., bool],
    *args: Any,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
    **kwargs: Any,
) -> Any:
    """Returns the entries whose values return `False` with function `f` for at least one selected key."""
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    if is_group_by(s):
        if s.axis == 0:
            keys = get_index(s, inclusion=inclusion, exclusion=exclusion)
        return s.filter(lambda x: x.name in keys and any_not_values(apply(x, f, *args, **kwargs)))
    elif is_table(s):
        mask = create_mask(s, condition=lambda x: not f(x, *args, **kwargs), fill=False, keys=keys)
        if is_frame(s):
            return s.loc[reduce_or(mask, axis=1)]
        return s.loc[mask]
    elif is_dict(s):
        return {k: s[k] for k in keys if not f(s[k], *args, **kwargs)}
    return struct_to_type([s[k] for k in keys if not f(s[k], *args, **kwargs)], s)


##############################


def filter_null(s: Struct, keys: Optional[Iterable[Key]] = None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are null for all selected keys."""
    return filter_with(s, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_null(s: Struct, keys: Optional[Iterable[Key]] = None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are not null for all selected keys."""
    return filter_not_with(s, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_null(s: Struct, keys: Optional[Iterable[Key]] = None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are null for at least one selected key."""
    return filter_any_with(s, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_null(s: Struct, keys: Optional[Iterable[Key]] = None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are not null for at least one selected key."""
    return filter_any_not_with(s, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


##############################


def filter_empty(s: Struct, keys: Optional[Iterable[Key]] = None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are empty for all selected keys."""
    return filter_with(s, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_empty(s: Struct, keys: Optional[Iterable[Key]] = None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are not empty for all selected keys."""
    return filter_not_with(s, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_empty(s: Struct, keys: Optional[Iterable[Key]] = None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are empty for at least one selected key."""
    return filter_any_with(s, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_empty(s: Struct, keys: Optional[Iterable[Key]] = None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are not empty for at least one selected key."""
    return filter_any_not_with(s, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


##############################


def filter_value(s: Struct, value: Any, keys=None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values equal `value` for all selected keys."""
    return filter_with(s, lambda v: v == value, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_value(s: Struct, value: Any, keys=None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values do not equal `value` for all selected keys."""
    return filter_not_with(s, lambda v: v == value, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_value(s: Struct, value: Any, keys=None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values equal `value` for at least one selected key."""
    return filter_any_with(s, lambda v: v == value, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_value(s: Struct, value: Any, keys=None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values do not equal `value` for at least one selected key."""
    return filter_any_not_with(s, lambda v: v == value, keys=keys, inclusion=inclusion, exclusion=exclusion)


##############################


def filter_in(s: Struct, values: Iterable[Any], keys=None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are in `values` for all selected keys."""
    values = to_set(values)
    return filter_with(s, lambda v: v in values, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_in(s: Struct, values: Iterable[Any], keys=None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are not in `values` for all selected keys."""
    values = to_set(values)
    return filter_not_with(s, lambda v: v in values, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_in(s: Struct, values: Iterable[Any], keys=None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are in `values` for at least one selected key."""
    values = to_set(values)
    return filter_any_with(s, lambda v: v in values, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_in(s: Struct, values: Iterable[Any], keys=None, inclusion=None, exclusion=None) -> Any:
    """Returns the entries whose values are not in `values` for at least one selected key."""
    values = to_set(values)
    return filter_any_not_with(s, lambda v: v in values, keys=keys, inclusion=inclusion, exclusion=exclusion)


##############################


def filter_between(
    s: Struct,
    lower: Any = None,
    upper: Any = None,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Returns the entries where values lie in `[lower, upper)` for all selected keys."""
    if is_all_null(lower, upper):
        return s
    elif is_null(lower):
        return filter_with(s, lambda v: v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion)
    elif is_null(upper):
        return filter_with(s, lambda v: v >= lower, keys=keys, inclusion=inclusion, exclusion=exclusion)
    return filter_with(s, lambda v: lower <= v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_between(
    s: Struct,
    lower: Any = None,
    upper: Any = None,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Returns the entries where values are NOT in `[lower, upper)` for all selected keys."""
    if is_all_null(lower, upper):
        return s
    elif is_null(lower):
        return filter_not_with(s, lambda v: v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion)
    elif is_null(upper):
        return filter_not_with(s, lambda v: v >= lower, keys=keys, inclusion=inclusion, exclusion=exclusion)
    return filter_not_with(s, lambda v: lower <= v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_between(
    s: Struct,
    lower: Any = None,
    upper: Any = None,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Returns the entries where values lie in `[lower, upper)` for at least one key."""
    if is_all_null(lower, upper):
        return s
    elif is_null(lower):
        return filter_any_with(s, lambda v: v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion)
    elif is_null(upper):
        return filter_any_with(s, lambda v: v >= lower, keys=keys, inclusion=inclusion, exclusion=exclusion)
    return filter_any_with(s, lambda v: lower <= v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_between(
    s: Struct,
    lower: Any = None,
    upper: Any = None,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Returns the entries where values are NOT in `[lower, upper)` for at least one key."""
    if is_all_null(lower, upper):
        return s
    elif is_null(lower):
        return filter_any_not_with(s, lambda v: v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion)
    elif is_null(upper):
        return filter_any_not_with(s, lambda v: v >= lower, keys=keys, inclusion=inclusion, exclusion=exclusion)
    return filter_any_not_with(s, lambda v: lower <= v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion)


__TABLE_FILTERS_____________________________________________ = ""


def filter_rows_with(
    df: pd.DataFrame, row: Mapping[Key, Value], f: Callable[..., Any], *args: Any, **kwargs: Any
) -> pd.DataFrame:
    """
    Returns rows where each key in `row` satisfies `f(df[col], row[col], *args, **kwargs)`.

    Notes:
        • Combines per-column boolean Series with `reduce_and(…)`.
    """
    if is_empty(df):
        return df
    return df.loc[reduce_and([apply(df[k], f, v, *args, **kwargs) for k, v in row.items() if k in df])]


def filter_rows_not_with(
    df: pd.DataFrame, row: Mapping[Key, Value], f: Callable[..., Any], *args: Any, **kwargs: Any
) -> pd.DataFrame:
    """Returns rows where all `k` in `row` fail `f(…)`."""
    if is_empty(df):
        return df
    return df.loc[reduce_and([invert(apply(df[k], f, v, *args, **kwargs)) for k, v in row.items() if k in df])]


def filter_any_rows_with(
    df: pd.DataFrame, row: Mapping[Key, Value], f: Callable[..., Any], *args: Any, **kwargs: Any
) -> pd.DataFrame:
    """Returns rows where at least one `k` in `row` satisfies `f(…)`."""
    if is_empty(df):
        return df
    return df.loc[reduce_or([apply(df[k], f, v, *args, **kwargs) for k, v in row.items() if k in df])]


def filter_any_rows_not_with(
    df: pd.DataFrame, row: Mapping[Key, Value], f: Callable[..., Any], *args: Any, **kwargs: Any
) -> pd.DataFrame:
    """Returns rows where at least one `k` in `row` fails `f(…)`."""
    if is_empty(df):
        return df
    return df.loc[reduce_or([invert(apply(df[k], f, v, *args, **kwargs)) for k, v in row.items() if k in df])]


##############################


def filter_rows(df: pd.DataFrame, row: Mapping[Key, Value]) -> pd.DataFrame:
    """Returns rows matching `row` on all common columns."""
    if is_empty(df) or is_null(row):
        return df
    return df.loc[reduce_and([df[k] == v for k, v in row.items() if k in df])]


def filter_rows_not(df: pd.DataFrame, row: Mapping[Key, Value]) -> pd.DataFrame:
    """Returns rows not matching `row` on all common columns."""
    if is_empty(df) or is_null(row):
        return df
    return df.loc[reduce_and([df[k] != v for k, v in row.items() if k in df])]


def filter_any_rows(df: pd.DataFrame, row: Mapping[Key, Value]) -> pd.DataFrame:
    """Returns rows matching `row` on at least one common column."""
    if is_empty(df) or is_null(row):
        return df
    return df.loc[reduce_or([df[k] == v for k, v in row.items() if k in df])]


def filter_any_rows_not(df: pd.DataFrame, row: Mapping[Key, Value]) -> pd.DataFrame:
    """Returns rows not matching `row` on at least one common column."""
    if is_empty(df) or is_null(row):
        return df
    return df.loc[reduce_or([df[k] != v for k, v in row.items() if k in df])]


##############################


def filter_rows_in(df: pd.DataFrame, rows: Mapping[Key, Iterable[Value]]) -> pd.DataFrame:
    """Returns rows where each `k` satisfies `df[k].isin(rows[k])`."""
    if is_empty(df) or is_null(rows):
        return df
    return df.loc[reduce_and([df[k].isin(to_set(values)) for k, values in rows.items() if k in df])]


def filter_rows_not_in(df: pd.DataFrame, rows: Mapping[Key, Iterable[Value]]) -> pd.DataFrame:
    """Returns rows where each `k` satisfies `~df[k].isin(rows[k])`."""
    if is_empty(df) or is_null(rows):
        return df
    return df.loc[reduce_and([invert(df[k].isin(to_set(values))) for k, values in rows.items() if k in df])]


def filter_any_rows_in(df: pd.DataFrame, rows: Mapping[Key, Iterable[Value]]) -> pd.DataFrame:
    """Returns rows where at least one `k` satisfies `df[k].isin(rows[k])`."""
    if is_empty(df) or is_null(rows):
        return df
    return df.loc[reduce_or([df[k].isin(to_set(values)) for k, values in rows.items() if k in df])]


def filter_any_rows_not_in(df: pd.DataFrame, rows: Mapping[Key, Iterable[Value]]) -> pd.DataFrame:
    """Returns rows where at least one `k` satisfies `~df[k].isin(rows[k])`."""
    if is_empty(df) or is_null(rows):
        return df
    return df.loc[reduce_or([invert(df[k].isin(to_set(values))) for k, values in rows.items() if k in df])]


__STRUCT_FINDERS__________________________________________________________________________ = ""


__COLLECTION_FINDERS________________________________________ = ""


### LIST ###################################################


def find_all(l: List[Value], value: Value) -> List[int]:
    """Returns all indices where entries equal `value`."""
    return find_all_with(l, lambda v: v == value)


def find_all_not(l: List[Value], value: Value) -> List[int]:
    """Returns all indices where entries do not equal `value`."""
    return find_all_not_with(l, lambda v: v == value)


def find_all_in(l: List[Value], values: Iterable[Value]) -> List[int]:
    """Returns all indices where entries are contained in `values` (set membership)."""
    values = to_set(values)
    return find_all_with(l, lambda v: v in values)


def find_all_not_in(l: List[Value], values: Iterable[Value]) -> List[int]:
    """Returns all indices where entries are not contained in `values` (set membership)."""
    values = to_set(values)
    return find_all_not_with(l, lambda v: v in values)


def find_all_with(
    l: List[Value],
    f: Callable[..., bool],
    *args: Any,
    **kwargs: Any,
) -> List[int]:
    """Returns all indices where predicate `f(entry, *args, **kwargs)` is `True`."""
    return [i for i in range(len(l)) if f(l[i], *args, **kwargs)]


def find_all_not_with(
    l: List[Value],
    f: Callable[..., bool],
    *args: Any,
    **kwargs: Any,
) -> List[int]:
    """Returns all indices where predicate `f(entry, *args, **kwargs)` is `False`."""
    return [i for i in range(len(l)) if not f(l[i], *args, **kwargs)]


##############################


def find(l: List[Value], value: Value) -> Optional[int]:
    """Returns the first index where entry equals `value`, or `None` if not found."""
    return find_with(l, lambda v: v == value)


def find_not(l: List[Value], value: Value) -> Optional[int]:
    """Returns the first index where entry does not equal `value`, or `None` if not found."""
    return find_not_with(l, lambda v: v == value)


def find_in(l: List[Value], values: Iterable[Value]) -> Optional[int]:
    """Returns the first index where entry is contained in `values`, or `None` if not found."""
    values = to_set(values)
    return find_with(l, lambda v: v in values)


def find_not_in(l: List[Value], values: Iterable[Value]) -> Optional[int]:
    """Returns the first index where entry is not contained in `values`, or `None` if not found."""
    values = to_set(values)
    return find_not_with(l, lambda v: v in values)


def find_with(
    l: List[Value],
    f: Callable[..., bool],
    *args: Any,
    **kwargs: Any,
) -> Optional[int]:
    """Returns the first index where predicate `f(entry, *args, **kwargs)` is `True`, else `None`."""
    return next((i for i in range(len(l)) if f(l[i], *args, **kwargs)), None)


def find_not_with(
    l: List[Value],
    f: Callable[..., bool],
    *args: Any,
    **kwargs: Any,
) -> Optional[int]:
    """Returns the first index where predicate `f(entry, *args, **kwargs)` is `False`, else `None`."""
    return next((i for i in range(len(l)) if not f(l[i], *args, **kwargs)), None)


##############################


def find_last(l: List[Value], value: Value) -> Optional[int]:
    """Returns the last index where entry equals `value`, or `None` if not found."""
    return find_last_with(l, lambda v: v == value)


def find_last_not(l: List[Value], value: Value) -> Optional[int]:
    """Returns the last index where entry does not equal `value`, or `None` if not found."""
    return find_last_not_with(l, lambda v: v == value)


def find_last_in(l: List[Value], values: Iterable[Value]) -> Optional[int]:
    """Returns the last index where entry is contained in `values`, or `None` if not found."""
    values = to_set(values)
    return find_last_with(l, lambda v: v in values)


def find_last_not_in(l: List[Value], values: Iterable[Value]) -> Optional[int]:
    """Returns the last index where entry is not contained in `values`, or `None` if not found."""
    values = to_set(values)
    return find_last_not_with(l, lambda v: v in values)


def find_last_with(
    l: List[Value],
    f: Callable[..., bool],
    *args: Any,
    **kwargs: Any,
) -> Optional[int]:
    """Returns the last index where predicate `f(entry, *args, **kwargs)` is `True`, else `None`."""
    i = find_with(l[::-1], f, *args, **kwargs)
    return len(l) - i - 1 if not is_null(i) else None


def find_last_not_with(
    l: List[Value],
    f: Callable[..., bool],
    *args: Any,
    **kwargs: Any,
) -> Optional[int]:
    """Returns the last index where predicate `f(entry, *args, **kwargs)` is `False`, else `None`."""
    i = find_not_with(l[::-1], f, *args, **kwargs)
    return len(l) - i - 1 if not is_null(i) else None


__STRUCT_GENERATORS_______________________________________________________________________ = ""


def create_empty(
    t: Union[Any, Type[Any]],
    *,
    element_type: Optional[ElementType] = None,
    registry: Optional[CollectionRegistry] = None,
) -> Any:
    """
    Returns an empty structure compatible with the specified container type.

    Behavior:
        • If `t` is an instance, derives its type via `type(t)`.
        • If `registry` is `None`, uses the global `CollectionRegistry` singleton.
        • If an adapter is registered for `t`, tries to construct an empty instance of
          the resolved type:
            – If the type defines `from_iterable`, calls it with an empty `list`.
            – Otherwise, tries the no-arg constructor.
        • If no adapter is found or construction fails, falls back to the structure rules:
            – `Mapping` / `MutableMapping` → `dict()`
            – `MutableSequence` or non-string `Sequence` → `list()`
            – `MutableSet` / `Set` → `set()`
            – `np.ndarray` → `to_array(element_type=element_type)`
            – `pd.DataFrame` → `pd.DataFrame()`
            – `pd.Series` → `pd.Series(dtype=element_type or OBJECT_TYPE)`
            – Generic `Iterable` → `tuple()`.

    Args:
        t: The container type or instance whose empty counterpart is required.
        element_type: Optional NumPy dtype or element type used for array-like
            outputs (`np.ndarray`, `pd.Series`). When `None`, uses a default for
            the target type.
        registry: Optional collection registry. When `None`, uses the global
            `CollectionRegistry` singleton.

    Returns:
        An empty structure compatible with `t`.

    Raises:
        ValueError: If `t` cannot be mapped to an empty structure.
    """
    # Normalize to a type so both types and instances are accepted
    t: Type[Any] = t if isinstance(t, type) else type(t)

    # 1) Resolve registry and ask it for an adapter
    if is_null(registry):
        registry = CollectionRegistry()

    adapter = registry.get(t) if not is_null(registry) else None

    if not is_null(adapter):
        # Prefer constructing the specified type, not the adapter base type, so subclasses can override `from_iterable`
        if has_callable(t, "from_iterable"):
            return t.from_iterable([])
        try:
            return t()
        except TypeError:
            # Continue to the structure fallbacks
            pass

    # 2) Explicit known structures
    if is_frame_type(t):
        return pd.DataFrame()
    elif is_series_type(t):
        return pd.Series(dtype=element_type if not is_null(element_type) else OBJECT_TYPE)
    elif is_array_type(t):
        return to_array(element_type=element_type)

    # 3) Structural fallbacks via ABCs
    if is_mapping_type(t):
        return dict()
    elif is_sequence_type(t):
        return list()
    elif is_set_type(t):
        return set()
    elif is_iterable_type(t):
        return tuple()

    # 4) Last resort: try a bare no-arg constructor before failing
    try:
        return t()
    except TypeError as e:
        raise ValueError(f"Unexpected structure type '{t}'") from e


def create_mask(
    s: Struct,
    *args: Any,
    condition: Callable[..., bool] = lambda x, *a, **k: True,
    fill: bool = True,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
    **kwargs: Any,
) -> Any:
    """
    Returns a boolean mask shaped like `s` where each selected entry satisfies `condition`.

    Dispatch:
        • Vectorized attempt via `apply(s, condition, axis=None, keys=keys, …)`.
        • Fallback element-wise loop when vectorization fails.

    Complexity:
        • Vectorized: ~O(n) on the selected entries.
        • Fallback:   O(n) Python loop over selected entries.
    """
    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    # 1) Allocate the mask container aligned to `s`, pre-filled (e.g., `True`)
    mask = struct_to_type(create_array(get_shape(s), fill=fill, element_type=BOOLEAN_ELEMENT_TYPE), s)

    # 2) Try a vectorized evaluation on the selected slice
    try:
        m = apply(s, condition, *args, axis=None, keys=keys, **kwargs)

        # Normalize to a boolean array
        values = to_array(
            get_values(m) if is_subscriptable(m) else to_array(m),
            element_type=BOOLEAN_ELEMENT_TYPE,
        )

        # Broadcast a scalar to the exact filtered shape
        if values.size == 1:
            values = create_array(
                get_shape(mask, keys=keys),
                fill=bool(values.ravel()[0]),
                element_type=BOOLEAN_ELEMENT_TYPE,
            )

        # Write the values into the preallocated mask container
        set_values(mask, values, keys=keys)
        return mask
    except Exception:
        pass

    # 3) Fallback: evaluate element-wise and assign
    selected = get_values(s, keys=keys)
    set_values(mask, [bool(condition(v, *args, **kwargs)) for v in selected], keys=keys)
    return mask


__STRUCT_PROCESSORS_______________________________________________________________________ = ""


def all_values(s: Struct) -> bool:
    """Returns whether all selected values in `s` are truthy (`np.all`)."""
    return np.all(get_values(s))


def all_not_values(s: Struct) -> bool:
    """Returns whether all selected values in `s` are falsy (logical NOT then `np.all`)."""
    return np.all(invert(get_values(s)))


def any_values(s: Struct) -> bool:
    """Returns whether any selected value in `s` is truthy (`np.any`)."""
    return np.any(get_values(s))


def any_not_values(s: Struct) -> bool:
    """Returns whether any selected value in `s` is falsy (logical NOT then `np.any`)."""
    return np.any(invert(get_values(s)))


##############################


def apply(
    s: Struct,
    f: Callable[..., Any],
    *args: Any,
    inplace: bool = False,
    axis: Optional[int] = None,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
    **kwargs: Any,
) -> Any:
    """
    Applies function `f` over `s` along `axis` (`None` → element-wise; `0` → rows; `1` → columns).
    Tries a vectorized call first; falls back to `.apply`/Python loops when needed.

    Dispatch:
        • `GroupBy(axis=0)` → concatenates row-wise results built per group.
        • `GroupBy(axis=1)` → concatenates column-wise results built per group.
        • `pd.DataFrame`    → vectorizes on selected block/series; fallback to `.apply`.
        • `pd.Series`       → vectorizes on series; fallback to `.apply`.
        • `np.ndarray`      → vectorizes; fallback to `np.apply_along_axis` / element-wise.
        • `dict`            → loops over `keys`.
        • Generic           → loops and `struct_to_type(…)`.

    Complexity:
        • Vectorized: ~O(n) with C-level ops on the selected slice.
        • Fallback:   O(n) Python-level, slower than vectorized.
    """
    if is_empty(s):
        return s
    elif not is_subscriptable(s):
        return f(s, *args, **kwargs)

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    if inplace:
        return set_values(s, apply(s, f, *args, axis=axis, keys=keys, **kwargs), keys=keys)
    if is_group_by(s):
        axis = s.axis
        if axis == 0:
            return concat_rows(
                [
                    to_frame(
                        [to_array(f(get_values(v, keys=keys), *args, **kwargs))],
                        index=to_list(i),
                    )
                    for i, v in s
                ]
            )
        return concat_cols(
            [pd.Series(data=to_array(f(to_array(v), *args, **kwargs)), name=k) for k, v in s if k in keys]
        )
    elif is_frame(s):
        if is_null(axis):
            cols = []
            for k in keys:
                col = s.loc[:, k]
                cols.append(col.apply(f, args=args, **kwargs))
            return concat_cols(cols)
        cols = s.loc[:, keys]
        return cols.apply(f, args=args, axis=axis, **kwargs)
    elif is_series(s):
        rows = s.loc[keys]
        return rows.apply(f, args=args, **kwargs)
    elif is_dict(s):
        return {k: f(s[k], *args, **kwargs) for k in keys}
    elif is_array(s):
        a = s[keys]
        if is_null(axis):
            return np.vectorize(lambda z: f(z, *args, **kwargs))(a)
        return np.apply_along_axis(f, axis, a, *args, **kwargs)
    return struct_to_type([f(s[k], *args, **kwargs) for k in keys], s)


##############################


def calculate(s: Struct, f: Callable[..., Any], *args: Any, axis: Optional[Axis] = None, **kwargs: Any) -> Any:
    """
    Calculates `f(values, *args, axis=axis, **kwargs)` aligned to `s`, preserving labels where
    applicable (e.g., returns a `Series` for DataFrames with index/keys).
    """
    if is_group_by(s):
        axis = s.axis
        if axis == 0:
            names = get_names(s)
            return concat_rows(
                [to_frame([f(v.values, *args, axis=axis, **kwargs)], names=names, index=to_list(i)) for i, v in s]
            )
        index = get_index(s)
        return concat_cols([to_series(f(v.values, *args, axis=axis, **kwargs), name=k, index=index) for k, v in s])
    elif is_frame(s):
        index = get_keys_or_index(s, axis=axis)
        return to_series(f(s.values, *args, axis=axis, **kwargs), index=index)
    return f(get_values(s), *args, axis=axis, **kwargs)


##############################


def concat_all(*args: Any) -> Any:
    """Concatenates all arguments left-to-right using `concat`."""
    return reduce(args, concat)


def concat(c1: Any, c2: Any) -> Any:
    """Concatenates the specified collections."""
    if is_table(c1) or is_table(c2):
        return concat_rows(c1, c2)
    elif is_dict(c1) or is_dict(c2):
        return dict(to_list(get_items(c1)) + to_list(get_items(c2)))
    elif is_ordered_set(c1) or is_ordered_set(c2):
        return to_ordered_set(c1).union(to_ordered_set(c2))
    elif is_set(c1) or is_set(c2):
        return to_set(c1).union(to_set(c2))
    elif is_array(c1) or is_array(c2):
        return np.append(to_array(c1), to_array(c2))
    return to_list(c1) + to_list(c2)


##############################


def fill_null(
    s: Struct,
    numeric_default: Any = None,
    object_default: Any = None,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Fills `null` entries with type-aware defaults (`numeric_default` vs `object_default`)."""
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    for k in keys:
        if is_frame(s):
            col = s.loc[:, k]
            if is_numeric_dtype(col.dtypes):
                fill_null_with(col, numeric_default, inplace=True)
            else:
                fill_null_with(col, object_default, inplace=True)
        elif is_series(s):
            if is_null(s.loc[k]):
                s.loc[k] = numeric_default if is_number(s.loc[k]) else object_default
        else:
            if is_null(s[k]):
                s[k] = numeric_default if is_number(s[k]) else object_default
    return s


def fill_with(
    s: Struct,
    value: Any,
    *args: Any,
    condition: Callable[..., bool] = lambda x, *a, **k: True,
    inplace: bool = False,
    **kwargs: Any,
) -> Any:
    """Fills entries with `value` where `condition(entry, *args, **kwargs)` is `True`."""
    return apply(s, lambda x: value if condition(x, *args, **kwargs) else x, inplace=inplace)


def fill_null_with(s: Struct, value: Any, inplace: bool = False) -> Any:
    """Fills `null` entries with `value`."""
    return fill_with(s, value, condition=is_null, inplace=inplace)


##############################


def flatten(s: Struct, element_type: Optional[ElementType] = None, axis: int = 0) -> np.ndarray:
    """Returns a flattened `array` view of `s` respecting the specified `axis` order."""
    if is_empty(s):
        return to_array(element_type=element_type)

    if element_type is OBJECT_TYPE:
        return to_array(flatten_list(s), element_type=element_type)
    return get_values(s, element_type=element_type).flatten(order="C" if axis == 0 else "F" if axis == 1 else "A")


##############################


def groupby(s: Struct, agg: Aggregation = AGGREGATION, pos: Position = POSITION, dof: int = 1, axis: int = 0) -> Any:
    """Aggregates or selects the positions along `axis` according to `agg`/`pos`."""
    if pos is Position.START:
        return get_first(s, axis=axis)
    elif pos is Position.MIDDLE:
        return get_middle(s, axis=axis)
    elif pos is Position.END:
        return get_last(s, axis=axis)
    elif agg is Aggregation.COUNT:
        return count(s, axis=axis)
    elif agg is Aggregation.MIN:
        return minimum(s, axis=axis)
    elif agg is Aggregation.MAX:
        return maximum(s, axis=axis)
    elif agg is Aggregation.MEAN:
        return mean(s, axis=axis)
    elif agg is Aggregation.MEDIAN:
        return median(s, axis=axis)
    elif agg is Aggregation.STD:
        return std(s, axis=axis, dof=dof)
    elif agg is Aggregation.VAR:
        return var(s, axis=axis, dof=dof)
    elif agg is Aggregation.SUM:
        return sum(s, axis=axis)


def count(*args: Any, axis: Optional[int] = 0) -> Any:
    """Counts the elements along `axis` (or total when `axis is None`)."""
    s = forward(*args)
    if is_element(s):
        return 1
    if is_null(axis):
        return np.size(get_values(s))
    if is_group_by(s):
        return s.count()
    elif is_frame(s):
        return s.count(axis=axis)
    elif is_array(s):
        return np.apply_along_axis(len, axis, s)
    return len(s)


def minimum(*args: Any, axis: Optional[int] = 0) -> Any:
    """Returns the minimum along `axis` (or global when `axis is None`)."""
    s = forward(*args)
    if is_null(axis):
        return np.min(get_values(s))
    if is_group_by(s):
        return s.min()
    elif is_dict(s):
        s = get_values(s)
    return np.min(s, axis=axis)


def maximum(*args: Any, axis: Optional[int] = 0) -> Any:
    """Returns the maximum along `axis` (or global when `axis is None`)."""
    s = forward(*args)
    if is_null(axis):
        return np.max(get_values(s))
    if is_group_by(s):
        return s.max()
    elif is_dict(s):
        s = get_values(s)
    return np.max(s, axis=axis)


def mean(*args: Any, axis: Optional[int] = 0) -> Any:
    """Returns the mean along `axis` (or global when `axis is None`)."""
    s = forward(*args)
    if is_null(axis):
        return np.mean(get_values(s))
    if is_group_by(s):
        return s.mean()
    elif is_dict(s):
        s = get_values(s)
    return np.mean(s, axis=axis)


def median(*args: Any, axis: Optional[int] = 0) -> Any:
    """Returns the median along `axis` (or global when `axis is None`)."""
    s = forward(*args)
    if is_null(axis):
        return np.median(get_values(s))
    if is_group_by(s):
        return s.median()
    elif is_dict(s):
        s = get_values(s)
    return np.median(s, axis=axis)


def std(*args: Any, dof: int = 1, axis: Optional[int] = 0) -> Any:
    """Returns the standard deviation along `axis` (or global when `axis is None`)."""
    s = forward(*args)
    if is_null(axis):
        return np.std(get_values(s), ddof=dof)
    if is_group_by(s):
        return s.std(ddof=dof)
    elif is_dict(s):
        s = get_values(s)
    return np.std(s, axis=axis, ddof=dof)


def var(*args: Any, dof: int = 1, axis: Optional[int] = 0) -> Any:
    """Returns the variance along `axis` (or global when `axis is None`)."""
    s = forward(*args)
    if is_null(axis):
        return np.var(get_values(s), ddof=dof)
    if is_group_by(s):
        return s.var(ddof=dof)
    elif is_dict(s):
        s = get_values(s)
    return np.var(s, axis=axis, ddof=dof)


def sum(*args: Any, axis: Optional[int] = 0) -> Any:
    """Returns the sum along `axis` (or global when `axis is None`)."""
    s = forward(*args)
    if is_null(axis):
        return np.sum(get_values(s))
    if is_group_by(s):
        return s.sum()
    elif is_dict(s):
        s = get_values(s)
    return np.sum(s, axis=axis)


##############################


def insert_all(
    *args: Any,
    copy: bool = False,
    ignore_index: bool = False,
    sort: bool = False,
    verify_integrity: bool = False,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Inserts all subsequent collections into the first via left fold of `insert`."""
    return reduce(
        args,
        insert,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
        keys=keys,
        inclusion=inclusion,
        exclusion=exclusion,
    )


def insert(
    c1: Any,
    c2: Any,
    copy: bool = False,
    ignore_index: bool = False,
    sort: bool = False,
    verify_integrity: bool = False,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """
    Inserts `c2` into `c1`:
        • Rows: append rows whose indices are not in `c1`.
        • Cols: append columns whose keys are not in `c1`.
    """
    # Insert the rows
    c1 = insert_rows(
        c1,
        c2,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
        keys=keys,
        inclusion=inclusion,
        exclusion=exclusion,
    )
    # Insert the columns
    return insert_cols(c1, c2, keys=keys, inclusion=inclusion, exclusion=exclusion)


def insert_rows(
    c1: Any,
    c2: Any,
    copy: bool = False,
    ignore_index: bool = False,
    sort: bool = False,
    verify_integrity: bool = False,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Inserts rows from `c2` into `c1` for the selected keys where indices differ."""
    if not is_table(c1) and not is_dict(c1):
        return c1
    if is_table(c2):
        c2 = exclude_index(c2, c1)

    # Resolve the keys
    if is_null(keys):
        keys = get_common_keys(c2, c1, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return c1

    c2 = struct_to_common_type(filter(c2, keys=keys), c1)
    if is_table(c1):
        c1 = concat_rows(
            c1,
            c2,
            copy=copy,
            ignore_index=ignore_index,
            sort=sort,
            verify_integrity=verify_integrity,
        )
    elif is_dict(c1):
        c1.update(c2)
    return c1


def insert_cols(
    c1: Any,
    c2: Any,
    copy: bool = False,
    ignore_index: bool = False,
    sort: bool = False,
    verify_integrity: bool = False,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Inserts the columns from `c2` into `c1` for keys not present in `c1`."""
    if not is_table(c1) and not is_dict(c1):
        return c1
    if is_table(c2):
        c2 = include_index(c2, c2)

    # Resolve the keys
    if is_null(keys):
        keys = get_uncommon_keys(c2, c1, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return c1

    c2 = struct_to_common_type(filter(c2, keys=keys), c1)
    if is_table(c1):
        c1 = concat_cols(
            c1,
            c2,
            copy=copy,
            ignore_index=ignore_index,
            sort=sort,
            verify_integrity=verify_integrity,
        )
    elif is_dict(c1):
        c1.update(c2)
    return c1


##############################


def keep_min(s: Struct, n: int, agg: Aggregation = AGGREGATION, pos: Position = POSITION, axis: int = 0) -> Any:
    """Keeps the `n` smallest entries according to `groupby(s, agg, pos, axis)` semantics."""
    g = groupby(s, agg=agg, pos=pos, axis=axis) if not is_group_by(s) else s
    if is_number(g):
        return g
    keys = [t[1] for t in sorted(zip(g, get_keys_or_index(g, axis=axis)))[:n]]
    return take(s, keys, axis=1 if (is_frame(s) or is_array(s)) and axis == 0 else 0)


def keep_min_with(s: Struct, n: int, f: Callable[..., Any], axis: int = 0) -> Any:
    """Keeps the `n` smallest entries according to `f(s, axis=axis)`."""
    return keep_min(apply(s, f, axis=axis), n)


def keep_max(s: Struct, n: int, agg: Aggregation = AGGREGATION, pos: Position = POSITION, axis: int = 0) -> Any:
    """Keeps the `n` largest entries according to `groupby(s, agg, pos, axis)` semantics."""
    g = groupby(s, agg=agg, pos=pos, axis=axis) if not is_group_by(s) else s
    if is_number(g):
        return g
    keys = [t[1] for t in sorted(zip(g, get_keys_or_index(g, axis=axis)), reverse=True)[:n]]
    return take(s, keys, axis=1 if (is_frame(s) or is_array(s)) and axis == 0 else 0)


def keep_max_with(s: Struct, n: int, f: Callable[..., Any], axis: int = 0) -> Any:
    """Keeps the `n` largest entries according to `f(s, axis=axis)`."""
    return keep_max(apply(s, f, axis=axis), n)


##############################


def reduce(s: Iterable[Any], f: Callable[..., Any], *args: Any, initializer: Any = None, **kwargs: Any) -> Any:
    """Reduces the specified iterable to a single value by left-folding function `f`."""
    if is_empty(s):
        return initializer

    if not is_null(initializer):
        return functools.reduce(lambda x, y: f(x, y, *args, **kwargs), s, initializer)
    return functools.reduce(lambda x, y: f(x, y, *args, **kwargs), s)


def reduce_and(x: Any, axis: int = 0) -> np.ndarray:
    """Reduces by logical AND along `axis` with empty-axis identity handling."""
    # `axis=0` and no rows → one `True` per column
    if axis == 0 and count_rows(x) == 0:
        return np.ones(count_cols(x), dtype=BOOLEAN_ELEMENT_TYPE)
    # `axis=1` and no columns → one `True` per row
    elif axis == 1 and count_cols(x) == 0:
        return np.ones(count_rows(x), dtype=BOOLEAN_ELEMENT_TYPE)
    return np.logical_and.reduce(x, axis=axis)


def reduce_or(x: Any, axis: int = 0) -> np.ndarray:
    """Reduces by logical OR along `axis` with empty-axis identity handling."""
    # `axis=0` and no rows → one `False` per column
    if axis == 0 and count_rows(x) == 0:
        return np.zeros(count_cols(x), dtype=BOOLEAN_ELEMENT_TYPE)
    # `axis=1` and no columns → one `False` per row
    elif axis == 1 and count_cols(x) == 0:
        return np.zeros(count_rows(x), dtype=BOOLEAN_ELEMENT_TYPE)
    return np.logical_or.reduce(x, axis=axis)


##############################


def remove_null(
    s: Struct,
    conservative: bool = True,
    axis: int = 0,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Removes rows/cols with all-null (conservative) or any-null (non-conservative) values."""
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    if axis == 0:
        if conservative:
            return filter_any_not_null(s, keys=keys)
        return filter_not_null(s, keys=keys)
    for k in keys:
        if is_all_null(s[k]) if conservative else is_any_null(s[k]):
            s = remove_col(s, names=k)
    return s


def remove_empty(
    s: Struct,
    conservative: bool = True,
    axis: int = 0,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Removes rows/cols with all-empty (conservative) or any-empty (non-conservative) values."""
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    if axis == 0:
        if conservative:
            return filter_any_not_empty(s, keys=keys)
        return filter_not_empty(s, keys=keys)
    for k in keys:
        if is_all_empty(s[k]) if conservative else is_any_empty(s[k]):
            s = remove_col(s, names=k)
    return s


def remove_value(
    s: Struct,
    value: Any,
    conservative: bool = True,
    axis: int = 0,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Removes rows/cols with all-`value` (conservative) or any-`value` (non-conservative) values."""
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    if axis == 0:
        if conservative:
            return filter_any_not_value(s, value, keys=keys)
        return filter_not_value(s, value, keys=keys)
    for k in keys:
        if is_all_value(value, s[k]) if conservative else is_any_value(value, s[k]):
            s = remove_col(s, names=k)
    return s


##############################


def reverse(s: Struct, axis: int = 0) -> Any:
    """Reverses rows (`axis=0`) or columns (`axis=1`) for tables; reverses order for others."""
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s
    if is_table(s):
        if axis == 0:
            return s.loc[::-1]
        return s.loc[:, ::-1]
    elif is_dict(s):
        return dict(reversed(list(s.items())))
    return s[::-1]


##############################


def simplify(s: Any) -> Any:
    """Simplifies a one-element collection by returning its single element recursively."""
    if is_struct(s):
        if len(s) == 1:
            return simplify(get_next(s))
    return s


##############################


def slice(
    s: Struct,
    index_from: Optional[int] = None,
    index_to: Optional[int] = None,
    axis: int = 0,
) -> Any:
    """Slices `s` between `index_from` and `index_to` along `axis`."""
    s = ungroup(s)
    if is_null(index_from):
        index_from = 0
    if is_null(index_to):
        index_to = len(s)
    keys = get_index_or_keys(s, axis=axis)
    return take(s, keys[index_from:index_to], axis=axis)


##############################


def sort(s: Struct, ascending: bool = True, by: Any = None, inplace: bool = False, axis: int = 0) -> Any:
    """Sorts values of `s` (by column(s) for DataFrames, values for Series/others)."""
    s = ungroup(s)
    if is_frame(s):
        return s.sort_values(by, ascending=ascending, inplace=inplace, axis=axis)
    elif is_series(s):
        return s.sort_values(ascending=ascending, inplace=inplace)
    elif is_dict(s):
        return s
    if inplace:
        return s.sort()
    return sorted(s)


def sort_index(s: Struct) -> Any:
    """Sorts the index of `s` when tabular."""
    s = ungroup(s)
    if is_table(s):
        return s.sort_index()
    return s


##############################


def take(s: Struct, keys: Iterable[Key], axis: int = 0) -> Any:
    """Returns the entries of `s` for all `keys` along `axis`."""
    s = ungroup(s)
    keys = to_ordered_set(keys)
    if is_table(s):
        if axis == 0:
            return s.loc[keys]
        return s.loc[:, keys]
    return filter(s, keys=keys)


def take_not(s: Struct, keys: Iterable[Key], axis: int = 0) -> Any:
    """Returns the entries of `s` except those in `keys` along `axis`."""
    indices = find_all_not_in(get_index_or_keys(s, axis=axis), keys)
    return take_at(s, indices, axis=axis)


def take_at(s: Struct, indices: Iterable[int], axis: int = 0) -> Any:
    """Returns the entries of `s` located at `indices` along `axis`."""
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s
    indices = to_list(indices)
    if is_table(s):
        if axis == 0:
            return s.iloc[indices]
        return s.iloc[:, indices]
    elif is_dict(s):
        return {k: v for i, (k, v) in enumerate(s.items()) if i in indices or i - len(s) in indices}
    return struct_to_type([s[i] for i in indices], s)


def take_not_at(s: Struct, indices: Iterable[int], axis: int = 0) -> Any:
    """Returns the entries of `s` that are not at `indices` along `axis`."""
    indices = find_all_not_in(range(count_rows(s) if axis == 0 else count_cols(s)), indices)
    return take_at(s, indices, axis=axis)


##############################


def tally(s: Struct, boundaries: Iterable[Any]) -> Any:
    """Tallies the values of `s` into half-open intervals defined by `boundaries`."""
    s = ungroup(s)
    if is_empty(s) or not is_subscriptable(s):
        return s
    if is_empty(boundaries):
        return repeat(0, len(s))
    ts = s.copy()
    lower = minimum(ts, axis=None)
    for i, upper in enumerate(boundaries):
        set_values(ts, i, mask=create_mask(s, condition=lambda v: lower <= v < upper))
        lower = upper
    set_values(ts, i + 1, mask=create_mask(s, condition=lambda v: v >= upper))
    return ts


##############################


def unique(s: Struct, pos: Optional[Position] = POSITION) -> Any:
    """
    Extracts unique elements from a container, with optional positional bias.

    Dispatch:
        • `pd.DataFrame`    → deduplicates by index (not by row content).
        • Other containers  → deduplicates the values, preserving order.

    Positional bias:
        • Position.START        → keep first occurrence (default for DataFrames).
        • Position.END          → keep last occurrence.
        • Position.MIDDLE       → keep middle occurrence (rounded down).
        • None/Position.AUTO    → fastest order-preserving deduplication.

    Notes:
        • For dicts, returns unchanged (keys are already unique).
        • For DataFrames, row *content* may still repeat if index differs.
    """
    s = ungroup(s)

    # Fastest path: order-preserving unique values
    if is_null(pos) or pos is Position.AUTO:
        if is_table(s):
            return s.loc[~s.index.duplicated(keep="first")]
        elif is_dict(s):
            return s
        return list(dict.fromkeys(s))

    # Structured handling for known positions
    if is_table(s):
        if pos is Position.START:
            return s.loc[~s.index.duplicated(keep="first")]
        elif pos is Position.MIDDLE:

            def get_middle(group):
                return group.iloc[len(group) // 2 : len(group) // 2 + 1]

            return s.groupby(s.index, sort=False).apply(get_middle).reset_index(level=0, drop=True)
        elif pos is Position.END:
            return s.loc[~s.index.duplicated(keep="last")]
    elif is_dict(s):
        return s
    seen: Dict[Any, List[int]] = {}
    for i, v in enumerate(s):
        if v not in seen:
            seen[v] = []
        seen[v].append(i)
    if pos is Position.START:
        return [s[seen[k][0]] for k in seen]
    elif pos is Position.MIDDLE:
        return [s[seen[k][len(seen[k]) // 2]] for k in seen]
    elif pos is Position.END:
        return [s[seen[k][-1]] for k in seen]
    return list(dict.fromkeys(s))


##############################


def update_all(*args: Any, keys: Optional[Iterable[Key]] = None, inclusion=None, exclusion=None) -> Any:
    """Left-fold `update` across all arguments."""
    return reduce(
        args,
        lambda c1, c2: update(c1, c2, keys=keys, inclusion=inclusion, exclusion=exclusion),
    )


def update(
    c1: Any,
    c2: Any,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """
    Updates `c1` with `c2` for keys in `keys` (or filters), matching rows by index for tables.
    """
    if is_table(c2):
        c2 = include_index(c2, c1)

    # Resolve the keys
    if is_null(keys):
        keys = get_common_keys(c2, c1, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return c1

    c2 = struct_to_common_type(filter(c2, keys=keys), c1)
    if is_table(c1):
        element_types = get_element_types(c2)
        c1.update(c2.fillna(NA_NAME))
        c1.replace(NA_NAME, NAN, inplace=True)
        c1 = set_element_types(c1, element_types)
    elif is_dict(c1):
        c1.update(c2)
    else:
        for k in keys:
            c1[k] = c2[k]
    return c1


##############################


def upsert_all(
    *args: Any,
    copy: bool = False,
    ignore_index: bool = False,
    sort: bool = False,
    verify_integrity: bool = False,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Left-fold `upsert` across all arguments."""
    return reduce(
        args,
        lambda c1, c2: upsert(
            c1,
            c2,
            copy=copy,
            ignore_index=ignore_index,
            sort=sort,
            verify_integrity=verify_integrity,
            keys=keys,
            inclusion=inclusion,
            exclusion=exclusion,
        ),
    )


def upsert(
    c1: Any,
    c2: Any,
    copy: bool = False,
    ignore_index: bool = False,
    sort: bool = False,
    verify_integrity: bool = False,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Upserts `c1` with `c2` by updating then inserting for `keys`."""
    # Resolve the keys
    if is_null(keys):
        keys = get_keys(c2, inclusion=inclusion, exclusion=exclusion)

    c2 = struct_to_common_type(filter(c2, keys=keys), c1)
    return insert(
        update(c1, c2),
        c2,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
    )


def upsert_rows(
    c1: Any,
    c2: Any,
    copy: bool = False,
    ignore_index: bool = False,
    sort: bool = False,
    verify_integrity: bool = False,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
) -> Any:
    """Upserts rows of `c1` with rows of `c2` for `keys`."""
    # Resolve the keys
    if is_null(keys):
        keys = get_keys(c2, inclusion=inclusion, exclusion=exclusion)

    c2 = struct_to_common_type(filter(c2, keys=keys), c1)
    return insert_rows(
        update(c1, c2),
        c2,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
    )


##############################


def where(
    s: Struct,
    *args: Any,
    condition: Callable[..., bool] = lambda x, *a, **k: True,
    keys: Optional[Iterable[Key]] = None,
    inclusion: Optional[Iterable[Key]] = None,
    exclusion: Optional[Iterable[Key]] = None,
    **kwargs: Any,
) -> List[Key]:
    """Returns the keys in `s` where `condition(value, *args, **kwargs)` is `True`."""
    if is_empty(s) or not is_subscriptable(s):
        return []

    # Resolve the keys
    if is_null(keys):
        keys = get_keys(s, inclusion=inclusion, exclusion=exclusion)

    return [k for k in keys if condition(s[k], *args, **kwargs)]


__TABLE_PROCESSORS__________________________________________ = ""


def combine_all(*args: Any, f: Callable[[pd.Series, pd.Series], pd.Series]) -> pd.DataFrame:
    """Combines all frames by folding with `combine(left, right, f)` (left fold)."""
    return reduce(args, lambda left, right: combine(left, right, f))


def combine(left: Any, right: Any, f: Callable[[pd.Series, pd.Series], pd.Series]) -> pd.DataFrame:
    """
    Combines the two frames column-wise using Pandas `DataFrame.combine` with function `f`
    applied to overlapping columns.

    Notes:
        • Inputs are coerced with `to_frame(…)` to ensure DataFrame semantics.
    """
    return to_frame(left).combine(to_frame(right), f)


##############################


def concat_rows(
    *rows: Any,
    copy: bool = False,
    ignore_index: bool = False,
    sort: bool = False,
    verify_integrity: bool = False,
) -> Table:
    """
    Concatenates rows (stack vertically) into a DataFrame.

    Notes:
        • Each input is coerced via `to_frame`.
        • Returns a `pd.Series` if the result has a single column (symmetry with `concat_cols`).
    """
    df = pd.concat(
        [to_frame(row) for row in to_struct(*rows)],
        axis=0,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
    )
    if count_cols(df) == 1:
        return to_series(df)
    return df


def concat_cols(
    *cols: Any,
    copy: bool = False,
    ignore_index: bool = False,
    sort: bool = False,
    verify_integrity: bool = False,
) -> Table:
    """
    Concatenates the columns (stack horizontally) into a DataFrame.

    Notes:
        • Inputs are passed as-is (already Series/Frames) via `to_struct`.
        • Returns a `pd.Series` if the result has a single column (symmetry with `concat_rows`).
    """
    df = pd.concat(
        to_struct(*cols),
        axis=1,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
    )
    if count_cols(df) == 1:
        return to_series(df)
    return df


##############################


def fill_null_all(
    df: Table,
    model: Table,
    numeric_default: Optional[Number] = None,
    object_default: Optional[Any] = None,
) -> Table:
    """
    Fills nulls after aligning `df` to the union of names/index with `model`.

    Dispatch:
        • `pd.Series` → delegates to `fill_null_rows(…)` with `model.index`.
        • `pd.DataFrame` → reindexes both rows and columns, then calls `fill_null(…)`.
    """
    if is_series(df):
        return fill_null_rows(df, get_index(model), numeric_default=numeric_default, object_default=object_default)
    return fill_null(
        sort_index(
            df.reindex(
                columns=unique(get_names(df) + get_names(model)),
                index=unique(get_index(df) + get_index(model)),
            )
        ),
        numeric_default=numeric_default,
        object_default=object_default,
    )


def fill_null_rows(
    df: Table,
    index: Any,
    numeric_default: Optional[Number] = None,
    object_default: Optional[Any] = None,
) -> Table:
    """Fills nulls after aligning `df` to `index` (rows only)."""
    if is_table(index):
        index = get_index(index)
    return fill_null(
        sort_index(df.reindex(index=unique(get_index(df) + to_list(index)))),
        numeric_default=numeric_default,
        object_default=object_default,
    )


def fill_null_cols(
    df: Table,
    names: Any,
    numeric_default: Optional[Number] = None,
    object_default: Optional[Any] = None,
) -> Table:
    """Fills nulls after aligning `df` to `names` (columns only)."""
    if is_table(names):
        names = get_names(names)
    return fill_null(
        sort_index(df.reindex(columns=unique(get_names(df) + to_list(names)))),
        numeric_default=numeric_default,
        object_default=object_default,
    )


##############################


def join_all(
    *args: Any,
    how: str = "inner",
    on: Optional[Union[Key, List[Key]]] = None,
    index_name: Optional[str] = None,
    suffix: str = "2",
    validate: Optional[str] = "m:m",
) -> pd.DataFrame:
    """Folds joins from left to right with `join(…)` using the provided parameters."""
    return reduce(
        args,
        lambda left, right: join(left, right, how=how, on=on, index_name=index_name, suffix=suffix, validate=validate),
    )


def join(
    left: Any,
    right: Any,
    how: str = "inner",
    on: Optional[Union[Key, List[Key]]] = None,
    index_name: Optional[str] = None,
    suffix: str = "2",
    validate: Optional[str] = "m:m",
) -> pd.DataFrame:
    """Joins `left` with `right` on index (or `on`), preserving `index_name`."""
    df = to_frame(left).join(to_frame(right), how=how, on=on, rsuffix=suffix, validate=validate)
    if not is_null(index_name):
        set_index_name(df, index_name)
    return df


##############################


def merge_all(
    *args: Any,
    how: str = "inner",
    on: Optional[Union[Key, List[Key]]] = None,
    index_name: Optional[str] = None,
    suffixes: Tuple[Optional[str], Optional[str]] = (None, "2"),
    indicator: Optional[Union[bool, str]] = None,
    validate: Optional[str] = "m:m",
) -> pd.DataFrame:
    """Folds merges from left to right with `merge(…)` using the provided parameters."""
    return reduce(
        args,
        lambda left, right: merge(
            left,
            right,
            how=how,
            on=on,
            index_name=index_name,
            suffixes=suffixes,
            indicator=indicator,
            validate=validate,
        ),
    )


def merge(
    left: Any,
    right: Any,
    how: str = "inner",
    on: Optional[Union[Key, List[Key]]] = None,
    index_name: Optional[str] = None,
    suffixes: Tuple[Optional[str], Optional[str]] = (None, "2"),
    indicator: Optional[Union[bool, str]] = None,
    validate: Optional[str] = "m:m",
) -> pd.DataFrame:
    """Merges `left` with `right` on columns `on` (or index names if `on` is `None`)."""
    df = to_frame(left).merge(
        to_frame(right),
        copy=False,
        how=how,
        on=on if not is_null(on) else get_names(left.index),
        suffixes=suffixes,
        indicator=indicator,
        validate=validate,
    )
    if not is_null(index_name):
        set_index_name(df, index_name)
    return df


##############################


def pivot(df: pd.DataFrame, names: Any, index: Any, values: Any) -> pd.DataFrame:
    """Pivots `df` to a wide layout (thin wrapper over `DataFrame.pivot`)."""
    return df.pivot(columns=names, index=index, values=values)


def unpivot(df: pd.DataFrame, value: Key, names: Optional[Iterable[str]] = None) -> pd.DataFrame:
    """
    Unpivots `df` (wide→long) via `unstack+reset_index`, removing rows where `value` is null.

    Notes:
        • When `names` is provided, renames the `level_i` columns accordingly.
    """
    df = filter_not_null(df.unstack().reset_index(name=value), keys=[value])
    if not is_null(names):
        df.rename(columns={"level_" + str(i): name for i, name in enumerate(to_list(names))}, inplace=True)
    return df


##############################


def remove_row(df: pd.DataFrame, index: Any = None, level: Any = None, inplace: bool = False) -> Optional[pd.DataFrame]:
    """Removes rows by index/level (thin wrapper over `DataFrame.drop`)."""
    return df.drop(index=index, level=level, inplace=inplace)


def remove_row_at(df: pd.DataFrame, i: int) -> pd.DataFrame:
    """Removes the row at position `i` (supports negative indexing)."""
    if i < 0:
        i = count_rows(df) + i
    return df.iloc[to_list(range(0, i)) + to_list(range(i + 1, count_rows(df))), :]


def remove_col(df: pd.DataFrame, names: Any = None, level: Any = None, inplace: bool = False) -> Optional[pd.DataFrame]:
    """Removes the columns by name/level (thin wrapper over `DataFrame.drop`)."""
    return df.drop(columns=names, level=level, inplace=inplace)


def remove_col_at(df: pd.DataFrame, j: int) -> pd.DataFrame:
    """Removes the column at position `j` (supports negative indexing)."""
    if j < 0:
        j = count_cols(df) + j
    return df.iloc[:, to_list(range(0, j)) + to_list(range(j + 1, count_cols(df)))]


##############################


def rename(df: pd.DataFrame, names: Any = None, index: Any = None, level: Any = None) -> pd.DataFrame:
    """
    Renames the columns or index.

    Notes:
        • When both `names` and `index` are empty, sets names to a default integer range.
        • Uses `set_names` for columns to propagate across containers consistently.
    """
    if is_all_empty(names, index):
        set_names(df, range(count_cols(df)))
    else:
        if not is_null(names):
            set_names(df, names)
        if not is_null(index):
            df.rename(index=index, level=level, copy=False, inplace=True)
    return df


def rename_all(*args: Any, names: Any = None, index: Any = None, level: Any = None) -> None:
    """Renames the columns/index for each frame in `args`."""
    for arg in args:
        rename(arg, names=names, index=index, level=level)


##############################


def rotate_rows(df: pd.DataFrame, drop: bool = True, prepend: bool = False) -> pd.DataFrame:
    """Rotates rows by moving last→first (`prepend=True`) or first→last (`prepend=False`)."""
    if is_empty(df):
        return df

    if prepend:
        df = concat_rows(get_last_row(df), df)
        if drop:
            df = remove_row_at(df, -1)
    else:
        df = concat_rows(df, get_first_row(df))
        if drop:
            df = remove_row_at(df, 0)
    return df


def rotate_cols(df: pd.DataFrame, drop: bool = True, prepend: bool = False) -> pd.DataFrame:
    """Rotates the columns by moving last→first (`prepend=True`) or first→last (`prepend=False`)."""
    if is_empty(df):
        return df

    if prepend:
        df = concat_cols(get_last_col(df), df)
        if drop:
            df = remove_col_at(df, -1)
    else:
        df = concat_cols(df, get_first_col(df))
        if drop:
            df = remove_col_at(df, 0)
    return df


##############################


def sum_rows(df: pd.DataFrame) -> pd.Series:
    """Returns the sum across rows (`axis=0`)."""
    return df.sum(axis=0)


def sum_cols(df: pd.DataFrame) -> pd.Series:
    """Returns the sum across columns (`axis=1`)."""
    return df.sum(axis=1)


def product_rows(df: pd.DataFrame) -> pd.Series:
    """Returns the product across rows (`axis=0`)."""
    return df.product(axis=0)


def product_cols(df: pd.DataFrame) -> pd.Series:
    """Returns the product across columns (`axis=1`)."""
    return df.product(axis=1)
