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

from typing import Any, Iterable, Sequence

from nutil.common.string import *
from nutil.common.tuple import *
from nutil.enums import Aggregation, Frequency, Position

####################################################################################################
# COMMON COLLECTION CONSTANTS
####################################################################################################

__COMMON_COLLECTION_CONSTANTS_____________________ = ""

ITERABLE_TYPE = Iterable

SEQUENCE_TYPE = Sequence


####################################################################################################
# COMMON COLLECTION VERIFIERS
####################################################################################################

__COMMON_COLLECTION_VERIFIERS_____________________ = ""


def is_iterable(x):
    return isinstance(x, ITERABLE_TYPE)


def is_sequence(x):
    return isinstance(x, SEQUENCE_TYPE)


def is_subscriptable(x):
    return hasattr(x, "__getitem__")


#########################


def is_collection(x: Any) -> bool:
    return is_iterable(x) and not is_string(x) and not is_byte(x) and not is_tuple(x)


#########################


def is_indexed_collection(x):
    return is_collection(x) and has_index(x)


def is_subscriptable_collection(x):
    return is_collection(x) and is_subscriptable(x)


#########################


def is_multidimensional_collection(x):
    return is_table(x) or is_array(x)


##################################################


def has_index(c):
    return is_array(c) or is_index(c) or is_sequence(c)


#########################


def has_filter(keys=None, inclusion=None, exclusion=None):
    return not is_null(keys) or not is_null(inclusion) or not is_empty(exclusion)


#########################


def _length_compare(x: Any, n: int, op) -> bool:
    """
    Compares the length of a collection to a specified number using the given operator.
    Returns False if x is not a valid collection or has no length.
    """
    if not is_collection(x):
        return False
    try:
        return op(len(x), n)
    except TypeError:
        return False


def has_length_ge(x: Any, n: int = 1) -> bool:
    """Returns True if len(x) >= n and x is a collection."""
    return _length_compare(x, n, operator.ge)


def has_length_gt(x: Any, n: int = 1) -> bool:
    """Returns True if len(x) > n and x is a collection."""
    return _length_compare(x, n, operator.gt)


def has_length_le(x: Any, n: int = 1) -> bool:
    """Returns True if len(x) <= n and x is a collection."""
    return _length_compare(x, n, operator.le)


def has_length_lt(x: Any, n: int = 1) -> bool:
    """Returns True if len(x) < n and x is a collection."""
    return _length_compare(x, n, operator.lt)


####################################################################################################
# COMMON COLLECTION ACCESSORS
####################################################################################################

__COMMON_COLLECTION_ACCESSORS_____________________ = ""


def get(c, index, axis=0):
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(axis):
        return simplify(flatten(c, axis=axis)[index])
    if is_multidimensional_collection(c):
        if axis == 0:
            return simplify(get_row(c, index))
        return simplify(get_col(c, index))
    elif is_dict(c):
        return simplify(c[get_keys(c)[index]])
    return simplify(c[index])


def get_first(c, axis=0):
    return get(c, 0, axis=axis)


def get_last(c, axis=0):
    return get(c, -1, axis=axis)


def get_iterator(c, cycle=False):
    if cycle:
        return itertools.cycle(c)
    return iter(c)


def get_next(c, cycle=False):
    if not is_collection(c):
        return c
    return next(get_iterator(c, cycle=cycle))


#########################


def get_shape(c, keys=None, inclusion=None, exclusion=None):
    c = filter(c, keys=keys, inclusion=inclusion, exclusion=exclusion)
    if is_multidimensional_collection(c):
        return c.shape
    elif is_tuple(c):
        return c
    return (len(c),)


#########################


def get_name(c, inclusion=None, exclusion=None):
    return simplify(get_names(c, inclusion=inclusion, exclusion=exclusion))


def get_names(c, inclusion=None, exclusion=None):
    """Returns the names of the specified collection."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_table(inclusion):
        inclusion = get_names(inclusion)
    if is_table(exclusion):
        exclusion = get_names(exclusion)
    if hasattr(c, "names"):
        c = c.names() if callable(c.names) else c.names
    elif hasattr(c, "name"):
        c = c.name() if callable(c.name) else c.name
    elif is_collection(c):
        if has_index(c):
            c = range(len(c))
        else:
            c = [get_name(e) for e in c]
    else:
        c = [to_string(c)]
    return filter_list(c, inclusion=inclusion, exclusion=exclusion)


def get_all_common_names(*args, inclusion=None, exclusion=None):
    return reduce(
        lambda c1, c2: get_common_names(c1, c2, inclusion=inclusion, exclusion=exclusion), *args
    )


def get_common_names(c1, c2, inclusion=None, exclusion=None):
    """Returns the common names of the specified collections that are in the specified inclusive
    list and are not in the specified exclusive list."""
    return get_names(c1, inclusion=include_list(get_names(c2), inclusion), exclusion=exclusion)


def get_all_uncommon_names(*args, inclusion=None, exclusion=None):
    return reduce(
        lambda c1, c2: get_uncommon_names(c1, c2, inclusion=inclusion, exclusion=exclusion), *args
    )


def get_uncommon_names(c1, c2, inclusion=None, exclusion=None):
    """Returns the uncommon names of the specified collections that are in the specified inclusive
    list and are not in the specified exclusive list."""
    return get_names(c1, inclusion=inclusion, exclusion=include_list(get_names(c2), exclusion))


#########################


def get_key(c, inclusion=None, exclusion=None):
    return simplify(get_keys(c, inclusion=inclusion, exclusion=exclusion))


def get_keys(c, inclusion=None, exclusion=None):
    """Returns the keys (indices/keys/names) of the specified collection that are in the specified
    inclusive list and are not in the specified exclusive list."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable_collection(c):
        return OrderedSet()
    if is_table(inclusion):
        inclusion = get_keys(inclusion)
    if is_table(exclusion):
        exclusion = get_keys(exclusion)
    if is_series(c):
        c = c.index
    elif has_index(c):
        c = range(len(c))
    return filter_ordered_set(c, inclusion=inclusion, exclusion=exclusion)


def get_all_common_keys(*args, inclusion=None, exclusion=None):
    return reduce(
        lambda c1, c2: get_common_keys(c1, c2, inclusion=inclusion, exclusion=exclusion), *args
    )


def get_common_keys(c1, c2, inclusion=None, exclusion=None):
    """Returns the common keys (indices/keys/names) of the specified collections that are in the
    specified inclusive list and are not in the specified exclusive list."""
    return get_keys(c1, inclusion=include_list(get_keys(c2), inclusion), exclusion=exclusion)


def get_all_uncommon_keys(*args, inclusion=None, exclusion=None):
    return reduce(
        lambda c1, c2: get_uncommon_keys(c1, c2, inclusion=inclusion, exclusion=exclusion), *args
    )


def get_uncommon_keys(c1, c2, inclusion=None, exclusion=None):
    """Returns the uncommon keys (indices/keys/names) of the specified collections that are in the
    specified inclusive list and are not in the specified exclusive list."""
    return get_keys(c1, inclusion=inclusion, exclusion=include_list(get_keys(c2), exclusion))


#########################


def get_index(c, inclusion=None, exclusion=None):
    """Returns the index (indices/keys/index) of the specified collection that are in the
    specified inclusive list and are not in the specified exclusive list."""
    if is_group(c):
        c = c.obj if c.axis == 1 else c.groups
    if is_table(inclusion):
        inclusion = get_index(inclusion)
    if is_table(exclusion):
        exclusion = get_index(exclusion)
    if is_table(c):
        return filter_list(c.index, inclusion=inclusion, exclusion=exclusion)
    elif is_array(c):
        return filter_list(range(count_cols(c)), inclusion=inclusion, exclusion=exclusion)
    return get_keys(c, inclusion=inclusion, exclusion=exclusion)


def get_index_name(c):
    if is_table(c):
        if isinstance(c.index, pd.MultiIndex):
            return c.index.names
        return c.index.name
    return None


def get_all_common_index(*args, inclusion=None, exclusion=None):
    return reduce(
        lambda c1, c2: get_common_index(c1, c2, inclusion=inclusion, exclusion=exclusion), *args
    )


def get_common_index(c1, c2, inclusion=None, exclusion=None):
    """Returns the common index (indices/keys/index) of the specified collections that are in the
    specified inclusive list and are not in the specified exclusive list."""
    return get_index(c1, inclusion=include_list(get_index(c2), inclusion), exclusion=exclusion)


def get_all_uncommon_index(*args, inclusion=None, exclusion=None):
    return reduce(
        lambda c1, c2: get_uncommon_index(c1, c2, inclusion=inclusion, exclusion=exclusion), *args
    )


def get_uncommon_index(c1, c2, inclusion=None, exclusion=None):
    """Returns the uncommon index (indices/keys/index) of the specified collections that are in the
    specified inclusive list and are not in the specified exclusive list."""
    return get_index(c1, inclusion=inclusion, exclusion=include_list(get_index(c2), exclusion))


#########################


def get_keys_or_index(c, axis=0, inclusion=None, exclusion=None):
    return (
        get_keys(c, inclusion=inclusion, exclusion=exclusion)
        if axis == 0
        else get_index(c, inclusion=inclusion, exclusion=exclusion)
    )


def get_index_or_keys(c, axis=0, inclusion=None, exclusion=None):
    return (
        get_index(c, inclusion=inclusion, exclusion=exclusion)
        if axis == 0
        else get_keys(c, inclusion=inclusion, exclusion=exclusion)
    )


#########################


def get_item(c, keys=None, inclusion=None, exclusion=None):
    return simplify(get_items(c, keys=keys, inclusion=inclusion, exclusion=exclusion))


def get_items(c, keys=None, inclusion=None, exclusion=None):
    """Returns the items (values/entries/columns) of the specified collection whose keys
    (indices/keys/names) are in the specified inclusive list and are not in the specified exclusive
    list."""
    if is_empty(c):
        return []
    elif not is_subscriptable_collection(c):
        return to_list(c)
    if not has_filter(keys=keys, inclusion=inclusion, exclusion=exclusion):
        if is_table(c) and not is_group(c) or is_dict(c):
            return to_list(c.items())
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return []
    if is_group(c):
        if c.axis == 0:
            return [(k, filter(v, keys=keys)) for k, v in c]
        return [(k, v) for k, v in c if k in keys]
    return [(k, c[k]) for k in keys]


#########################


def get_value(c, type=None, keys=None, inclusion=None, exclusion=None):
    return simplify(get_values(c, type=type, keys=keys, inclusion=inclusion, exclusion=exclusion))


def get_values(c, type=None, keys=None, inclusion=None, exclusion=None):
    """Returns the values (values/values/columns) of the specified collection whose keys
    (indices/keys/names) are in the specified inclusive list and are not in the specified exclusive
    list."""
    if is_empty(c):
        return to_array(type=type)
    elif not is_subscriptable_collection(c):
        return to_array(c, type=type)
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return to_array(type=type)
    if is_group(c):
        if c.axis == 0:
            return to_array([filter(v, keys=keys).values for k, v in c], type=type)
        return to_array([v.values for k, v in c if k in keys], type=type)
    elif is_table(c):
        return filter(c, keys=keys).values
    elif is_array(c):
        return c[keys]
    return to_array([c[k] for k in keys], type=type)


#########################


def get_element_type(c, keys=None, inclusion=None, exclusion=None):
    return simplify(get_element_types(c, keys=keys, inclusion=inclusion, exclusion=exclusion))


def get_element_types(c, keys=None, inclusion=None, exclusion=None):
    """Returns the element types of the specified collection whose keys (indices/keys/names) are in
    the specified inclusive list and are not in the specified exclusive list."""
    if is_empty(c):
        return {}
    elif not is_subscriptable_collection(c):
        return {i: type(e) for i, e in enumerate(c)}
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return {}
    elif is_frame(c):
        return to_dict(filter(c, keys=keys).dtypes)
    elif is_series(c) or is_array(c):
        return c.dtype
    elif hasattr(c, "dtypes"):
        return to_dict(c.dtypes)
    elif hasattr(c, "dtype"):
        return {get_name(c): c.dtype}
    return {k: type(c[k]) for k in keys}


##################################################


def set_names(c, new_names):
    """Sets the names of the specified collection."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_table(new_names):
        new_names = get_names(new_names)
    else:
        new_names = to_list(new_names)
    if is_empty(new_names):
        return c
    if is_frame(c):
        c.columns = new_names
    elif is_series(c):
        c.name = simplify(new_names)
    else:
        set_keys(c, new_names)
    return c


def set_keys(c, new_keys, keys=None, inclusion=None, exclusion=None):
    """Sets the keys (indices/keys/names) of the specified collection that are in the specified
    inclusive list and are not in the specified exclusive list."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return c
    if is_table(new_keys):
        new_keys = get_keys(new_keys)
    else:
        new_keys = to_ordered_set(new_keys)
    if is_empty(new_keys):
        return c
    if is_frame(c):
        c.loc[:, keys].columns = new_keys
    elif is_series(c):
        set_index(c, new_keys)
    elif is_dict(c):
        upsert(c, {new_key: c.pop(key) for key, new_key in zip(keys, new_keys)})
    else:
        update(c, {new_key: c[key] for key, new_key in zip(keys, new_keys)}, keys=keys)
    return c


def set_index(c, new_index, index_name="index"):
    """Sets the index (indices/keys/index) of the specified collection that are in the specified
    inclusive list and are not in the specified exclusive list."""
    if is_group(c):
        c = c.obj if c.axis == 1 else c.groups
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_table(new_index):
        new_index_names = get_names(new_index.index)
        new_index = new_index.index
    else:
        new_index_names = get_names(new_index)
        new_index = to_list(new_index)
    if is_empty(new_index):
        return c
    if is_table(c):
        if not is_empty(new_index) and is_tuple(new_index[0]):
            new_index_names = resize_list(new_index_names, len(new_index[0]))
            c.index = pd.MultiIndex.from_tuples(new_index, names=new_index_names)
        else:
            rename(c, index=dict(zip(c.index, new_index)))
    else:
        set_keys(c, new_index)
    set_index_name(c, index_name)
    return c


def set_index_name(c, index_name):
    if is_empty(index_name):
        return c
    if is_table(c):
        if isinstance(c.index, pd.MultiIndex):
            c.index.names = (
                index_name
                if is_collection(index_name)
                else [index_name + str(i + 1) for i in range(len(c.index.names))]
            )
        else:
            c.index.name = index_name
    return c


def set_values(c, new_values, mask=None, keys=None, inclusion=None, exclusion=None):
    """Sets the values (values/values/columns) of the specified collection whose keys
    (indices/keys/names) are in the specified inclusive list and are not in the specified exclusive
    list."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return c
    if is_collection(new_values):
        new_values = get_values(new_values)
    else:
        if not is_multidimensional_collection(c) or is_null(mask):
            new_values = create_array(get_shape(c, keys=keys), fill=new_values)
    if not is_null(mask):
        if is_multidimensional_collection(c):
            c[mask] = new_values
        else:
            for i, k in enumerate(keys):
                if mask[i]:
                    c[k] = new_values[i]
    else:
        if is_empty(new_values):
            return c
        if is_frame(c):
            chained_assignment = pd.options.mode.chained_assignment
            pd.options.mode.chained_assignment = None
            c.loc[:, keys] = new_values.reshape(count_rows(c), len(keys))
            pd.options.mode.chained_assignment = chained_assignment
        elif is_series(c):
            chained_assignment = pd.options.mode.chained_assignment
            pd.options.mode.chained_assignment = None
            c.loc[keys] = new_values
            pd.options.mode.chained_assignment = chained_assignment
        elif is_array(c):
            c[keys] = new_values
        else:
            for i, k in enumerate(keys):
                c[k] = new_values[i]
    return c


def set_element_types(c, new_types, keys=None, inclusion=None, exclusion=None):
    """Sets the values (values/values/columns) of the specified collection whose keys
    (indices/keys/names) are in the specified inclusive list and are not in the specified exclusive
    list."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return c
    if not is_dict(new_types):
        if is_collection(new_types):
            new_types = get_element_types(new_types, keys=keys)
        elif not is_series(c) and not is_array(c):
            new_types = {k: new_types for k in keys}
    if is_empty(new_types):
        return c
    if is_frame(c):
        c = c.astype(
            {
                k: t
                for k, t in new_types.items()
                if t is not DATE_TYPE and t is not DATETIME_TYPE and t is not TIMESTAMP_TYPE
            },
            copy=False,
        )
        date_cols = [
            k
            for k, t in new_types.items()
            if t is DATE_TYPE or t is DATETIME_TYPE or t is TIMESTAMP_TYPE
        ]
        set_values(c, c[date_cols].apply(pd.to_datetime), keys=date_cols)
    elif is_series(c) or is_array(c):
        if is_dict(new_types):
            new_types = get_value(new_types)
        c = c.astype(new_types, copy=False)
    elif is_dict(c):
        upsert(
            c,
            {
                k: to_element_type(c.pop(k), new_element_type)
                for k, new_element_type in new_types.items()
            },
        )
    else:
        update(
            c,
            {
                k: to_element_type(c[k], new_element_type)
                for k, new_element_type in new_types.items()
            },
            keys=keys,
        )
    return c


####################################################################################################
# COMMON COLLECTION CONVERTERS
####################################################################################################

__COMMON_COLLECTION_CONVERTERS____________________ = ""


def to_collection(*args):
    if len(args) == 1:
        arg = args[0]
        if is_collection(arg):
            return arg
        return [arg]
    return to_list(*args)


def to_indexed_collection(*args):
    if len(args) == 1:
        arg = args[0]
        if is_indexed_collection(arg):
            return arg
        return [arg]
    return to_list(*args)


def to_subscriptable_collection(*args):
    if len(args) == 1:
        arg = args[0]
        if is_subscriptable_collection(arg):
            return arg
        return [arg]
    return to_list(*args)


def uncollect(c):
    if is_collection(c):
        if len(c) == 1:
            return get_next(c)
        return tuple(c)
    return c


#########################


def collection_to_type(c, x):
    if is_frame(x):
        return to_frame(c, names=x, index=x)
    elif is_series(x):
        return to_series(c, name=x, index=x)
    elif is_dict(x):
        return dict(zip(get_keys(x), c))
    elif is_ordered_set(x):
        return to_ordered_set(c)
    elif is_set(x):
        return to_set(c)
    elif is_array(x):
        return to_array(c)
    elif is_list(x):
        return to_list(c)
    return c


def collection_to_common_type(c, x):
    if is_frame(x):
        return to_frame(c)
    elif is_series(x):
        return to_series(c)
    elif is_dict(x):
        return to_dict(c)
    elif is_ordered_set(x):
        return to_ordered_set(c)
    elif is_set(x):
        return to_set(c)
    elif is_array(x):
        return to_array(c)
    elif is_list(x):
        return to_list(c)
    return c


# • DATAFRAME ######################################################################################

__DATAFRAME_CONVERTERS____________________________ = ""


def to_series(data, name=None, index=None, type=None):
    """Converts the specified collection to a series."""
    if is_empty(data) and not is_table(data):
        data = []
        type = OBJECT_TYPE
    elif is_group(data):
        data = data.obj
    elif not is_collection(data):
        data = create_array(len(get_index(index)), fill=data, type=type)
    if is_frame(data):
        if count_cols(data) > 1:
            return get_cols(data)
        series = get_col(data) if not is_empty(data) else pd.Series(data=data, dtype=type)
    elif is_series(data):
        series = data.copy()
    else:
        series = pd.Series(data=data, dtype=type)
    if not is_null(name):
        set_names(series, name)
    if not is_null(index):
        set_index(series, index)
    return series


def to_time_series(data, name=None, index=None, type=FLOAT_ELEMENT_TYPE):
    """Converts the specified collection to a time series."""
    if not is_null(index):
        index = to_timestamp(to_array(index))
    return to_series(data, name=name, index=index, type=type)


#########################


def to_frame(data, names=None, index=None, index_name="index", type=None):
    """Converts the specified collection to a dataframe."""
    if is_empty(data) and not is_table(data):
        data = []
        type = OBJECT_TYPE
    elif is_group(data):
        data = data.obj
    elif not is_collection(data):
        data = create_array(len(get_index(index)), len(get_names(names)), fill=data, type=type)
    if is_frame(data):
        frame = data.copy()
    elif is_series(data):
        frame = data.to_frame()
    elif is_dict(data):
        frame = pd.DataFrame.from_dict(data, dtype=type, orient="index")
    else:
        frame = pd.DataFrame(data=data, dtype=type)
    if not is_null(names):
        set_names(frame, names)
    if not is_null(index):
        set_index(frame, index)
    set_index_name(frame, index_name)
    return frame


def to_time_frame(data, names=None, index=None, index_name="index", type=FLOAT_ELEMENT_TYPE):
    """Converts the specified collection to a time frame."""
    if not is_null(index):
        index = to_timestamp(to_array(index))
    return to_frame(data, names=names, index=index, index_name=index_name, type=type)


# • DATE ###########################################################################################

__DATE_CONVERTERS_________________________________ = ""


def to_date(x, format=DATE_FORMAT):
    if is_null(x):
        return None
    elif is_collection(x):
        return apply(x, to_date, format=format)
    elif is_stamp(x):
        x = parse_stamp(x)
        return create_date(x.year, x.month, x.day)
    elif is_timestamp(x):
        x = x.to_pydatetime()
        return create_date(x.year, x.month, x.day)
    elif is_datetime(x):
        return create_date(x.year, x.month, x.day)
    elif is_date(x):
        return x
    return datetime.strptime(x, format)


def to_datetime(x, format=DATE_TIME_FORMAT):
    if is_null(x):
        return None
    elif is_collection(x):
        return apply(x, to_datetime, format=format)
    elif is_stamp(x):
        return parse_stamp(x)
    elif is_timestamp(x):
        return x.to_pydatetime()
    elif is_datetime(x):
        return x
    elif is_date(x):
        return create_datetime(x.year, x.month, x.day)
    return datetime.strptime(x, format)


def to_time(x, format=TIME_FORMAT):
    if is_null(x):
        return None
    return to_datetime(x, format=format)


def to_datestamp(d):
    if is_null(d):
        return None
    elif is_stamp(d):
        d = parse_stamp(d)
    return pd.to_datetime(d).floor("D")


def to_timestamp(d):
    if is_null(d):
        return None
    elif is_stamp(d):
        d = parse_stamp(d)
    return pd.to_datetime(d)


def to_stamp(x):
    if is_null(x):
        return None
    elif is_collection(x):
        return apply(x, to_stamp)
    elif is_stamp(x):
        return x
    return to_datetime(x).timestamp()


#########################


def timestamp_to_type(t, x):
    """Converts the specified timestamp to the type of the specified variable."""
    if is_collection(t):
        return apply(t, timestamp_to_type, x)
    elif is_stamp(x):
        return to_stamp(t)
    elif is_timestamp(x):
        return t
    elif is_datetime(x):
        return to_datetime(t)
    elif is_date(x):
        return to_date(t)
    return t


#########################


def to_period(length, freq=FREQUENCY):
    return str(length) + freq.value


def to_period_length(period):
    return int(period[0:-1])


def to_period_freq(period):
    return Frequency(period[-1].upper())


# • DICT ###########################################################################################

__DICT_CONVERTERS_________________________________ = ""


def to_dict(c):
    """Converts the specified collection to a dictionary."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c):
        return {}
    elif is_table(c):
        return c.to_dict()
    elif is_dict(c):
        return c
    return {i: v for i, v in enumerate(c)}


# • LIST ###########################################################################################

__LIST_CONVERTERS_________________________________ = ""


def to_list(*args):
    if len(args) == 1:
        arg = args[0]
        if is_list(arg):
            return arg
        elif is_collection(arg):
            return list(arg if not is_dict(arg) else arg.values())
        return [arg]
    return list(args)


def unlist(l):
    if is_list(l):
        if len(l) == 1:
            return l[0]
        return tuple(l)
    return l


# • NUMBER #########################################################################################

__NUMBER_CONVERTERS_______________________________ = ""


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


# • SET ############################################################################################

__SET_CONVERTERS__________________________________ = ""


def to_set(*args):
    if len(args) == 1:
        arg = args[0]
        if is_set(arg):
            return arg
        elif is_collection(arg):
            return set(arg if not is_dict(arg) else arg.values())
        return {arg}
    return set(args)


def unset(s):
    if is_set(s):
        if len(s) == 1:
            return get_next(s)
        return tuple(s)
    return s


#########################


def to_ordered_set(*args):
    if len(args) == 1:
        arg = args[0]
        if is_ordered_set(arg):
            return arg
    return OrderedSet(*args)


# • STRING #########################################################################################

__STRING_CONVERTERS_______________________________ = ""


def to_string(x, delimiter=","):
    if is_null(x):
        return None
    elif is_collection(x):
        if hasattr(x, "astype"):
            return x.astype(STRING_ELEMENT_TYPE)
        return collapse(x, delimiter=delimiter)
    return str(x)


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


# • DATE ###########################################################################################

__DATE_FORMATTERS_________________________________ = ""


def format_date(d=get_datetime()):
    return trim(format_datetime(d, format=DATE_FORMAT))


def format_full_date(d=get_datetime()):
    return trim(format_datetime(d, format=DEFAULT_FULL_DATE_FORMAT))


def format_month_year(d=get_datetime()):
    return trim(format_datetime(d, format=DEFAULT_MONTH_YEAR_FORMAT))


def format_full_month_year(d=get_datetime()):
    return trim(format_datetime(d, format=DEFAULT_FULL_MONTH_YEAR_FORMAT))


def format_month(d=get_datetime()):
    return trim(format_datetime(d, format=DEFAULT_MONTH_FORMAT))


def format_full_month(d=get_datetime()):
    return trim(format_datetime(d, format=DEFAULT_FULL_MONTH_FORMAT))


def format_datetime(d=get_datetime(), format=DATE_TIME_FORMAT):
    if is_string(d):
        d = parse_datetime(d)
    return trim(d.strftime(format)) if not is_null(d) else None


def format_time(d=get_datetime()):
    return trim(format_datetime(d, format=TIME_FORMAT))


# • NUMBER #########################################################################################

__NUMBER_FORMATTERS_______________________________ = ""


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
# COMMON COLLECTION GENERATORS
####################################################################################################

__COMMON_COLLECTION_GENERATORS____________________ = ""


def create_mask(
    c,
    *args,
    condition=lambda x, *args, **kwargs: True,
    fill=True,
    keys=None,
    inclusion=None,
    exclusion=None,
    **kwargs,
):
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    mask = collection_to_type(create_array(get_shape(c), fill=fill, type=BOOL_ELEMENT_TYPE), c)
    values = apply(c, condition, *args, keys=keys, **kwargs)
    set_values(mask, values, keys=keys)
    return mask


####################################################################################################
# COMMON COLLECTION PROCESSORS
####################################################################################################

__COMMON_COLLECTION_PROCESSORS____________________ = ""


def all_values(c):
    return np.all(get_values(c))


def all_not_values(c):
    return np.all(invert(get_values(c)))


def any_values(c):
    return np.any(get_values(c))


def any_not_values(c):
    return np.any(invert(get_values(c)))


#########################


def calculate(c, f, *args, axis=0, **kwargs):
    if is_group(c):
        axis = c.axis
        if axis == 0:
            names = get_names(c)
            return concat_rows(
                [
                    to_frame(
                        [f(v.values, *args, axis=axis, **kwargs)], names=names, index=to_list(i)
                    )
                    for i, v in c
                ]
            )
        index = get_index(c)
        return concat_cols(
            [to_series(f(v.values, *args, axis=axis, **kwargs), name=k, index=index) for k, v in c]
        )
    elif is_frame(c):
        index = get_keys_or_index(c, axis=axis)
        return to_series(f(c.values, *args, axis=axis, **kwargs), index=index)
    return f(get_values(c), *args, axis=axis, **kwargs)


#########################


def concat_all(*args):
    return reduce(concat, *args)


def concat(c1, c2):
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


#########################


def fill_null(
    c, numeric_default=None, object_default=None, keys=None, inclusion=None, exclusion=None
):
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    for k in keys:
        if is_frame(c):
            col = c.loc[:, k]
            if is_numeric_dtype(col.dtypes):
                fill_null_with(col, numeric_default, inplace=True)
            else:
                fill_null_with(col, object_default, inplace=True)
        elif is_series(c):
            if is_null(c.loc[k]):
                if is_number(c.loc[k]):
                    c.loc[k] = numeric_default
                else:
                    c.loc[k] = object_default
        else:
            if is_null(c[k]):
                if is_number(c[k]):
                    c[k] = numeric_default
                else:
                    c[k] = object_default
    return c


#########################


def filter(c, keys=None, inclusion=None, exclusion=None):
    """Filters the specified collection by excluding the keys that are not in the specified
    inclusive collection and are in the specified exclusive collection."""
    if (
        is_empty(c)
        or not is_subscriptable_collection(c)
        or not has_filter(keys=keys, inclusion=inclusion, exclusion=exclusion)
    ):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_group(c):
        if c.axis == 0:
            keys = get_index(c, inclusion=inclusion, exclusion=exclusion)
        return c.filter(lambda x: x.name in keys)
    elif is_frame(c):
        return c.loc[:, keys]
    elif is_series(c):
        return c.loc[keys]
    elif is_dict(c):
        return {k: c[k] for k in keys}
    elif is_array(c):
        return c[keys]
    return collection_to_type([c[k] for k in keys], c)


def include(c, inclusion):
    """Filters the specified collection by excluding the keys that are not in the specified
    inclusive collection."""
    return filter(c, inclusion=inclusion)


def exclude(c, exclusion):
    """Filters the specified collection by excluding the keys that are in the specified exclusive
    collection."""
    return filter(c, exclusion=exclusion)


#########################


def filter_index(c, inclusion=None, exclusion=None):
    """Filters the specified collection by excluding the index that are not in the specified
    inclusive collection and are in the specified exclusive collection."""
    if (
        is_empty(c)
        or not is_subscriptable_collection(c)
        or not has_filter(inclusion=inclusion, exclusion=exclusion)
    ):
        return c
    index = get_index(c, inclusion=inclusion, exclusion=exclusion)
    if is_group(c):
        if c.axis == 1:
            index = get_keys(c, inclusion=inclusion, exclusion=exclusion)
        return c.filter(lambda x: x.name in index)
    elif is_table(c):
        return c.loc[c.index.isin(index)]
    return filter(c, keys=index)


def include_index(c, inclusion):
    """Filters the specified collection by excluding the index that are not in the specified
    inclusive collection."""
    return filter_index(c, inclusion=inclusion)


def exclude_index(c, exclusion):
    """Filters the specified collection by excluding the index that are in the specified exclusive
    collection."""
    return filter_index(c, exclusion=exclusion)


#########################


def filter_with(c, f, *args, keys=None, inclusion=None, exclusion=None, **kwargs):
    """Returns the entries of the specified collection whose values return True with the specified
    function for all the specified keys."""
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_group(c):
        if c.axis == 0:
            keys = get_index(c, inclusion=inclusion, exclusion=exclusion)
        return c.filter(lambda x: x.name in keys and all_values(apply(x, f, *args, **kwargs)))
    elif is_table(c):
        mask = create_mask(c, *args, condition=f, keys=keys, **kwargs)
        if is_frame(c):
            return c.loc[reduce_and(mask, axis=1)]
        return c.loc[mask]
    elif is_dict(c):
        return {k: c[k] for k in keys if f(c[k], *args, **kwargs)}
    return collection_to_type([c[k] for k in keys if f(c[k], *args, **kwargs)], c)


def filter_not_with(c, f, *args, keys=None, inclusion=None, exclusion=None, **kwargs):
    """Returns the entries of the specified collection whose values return False with the specified
    function for all the specified keys."""
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_group(c):
        if c.axis == 0:
            keys = get_index(c, inclusion=inclusion, exclusion=exclusion)
        return c.filter(lambda x: x.name in keys and all_not_values(apply(x, f, *args, **kwargs)))
    elif is_table(c):
        mask = create_mask(c, condition=lambda x: not f(x, *args, **kwargs), keys=keys)
        if is_frame(c):
            return c.loc[reduce_and(mask, axis=1)]
        return c.loc[mask]
    elif is_dict(c):
        return {k: c[k] for k in keys if not f(c[k], *args, **kwargs)}
    return collection_to_type([c[k] for k in keys if not f(c[k], *args, **kwargs)], c)


def filter_any_with(c, f, *args, keys=None, inclusion=None, exclusion=None, **kwargs):
    """Returns the entries of the specified collection whose values return True with the specified
    function for at least one specified key."""
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_group(c):
        if c.axis == 0:
            keys = get_index(c, inclusion=inclusion, exclusion=exclusion)
        return c.filter(lambda x: x.name in keys and any_values(apply(x, f, *args, **kwargs)))
    elif is_table(c):
        mask = create_mask(c, *args, condition=f, fill=False, keys=keys, **kwargs)
        if is_frame(c):
            return c.loc[reduce_or(mask, axis=1)]
        return c.loc[mask]
    elif is_dict(c):
        return {k: c[k] for k in keys if f(c[k], *args, **kwargs)}
    return collection_to_type([c[k] for k in keys if f(c[k], *args, **kwargs)], c)


def filter_any_not_with(c, f, *args, keys=None, inclusion=None, exclusion=None, **kwargs):
    """Returns the entries of the specified collection whose values return False with the specified
    function for at least one specified key."""
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_group(c):
        if c.axis == 0:
            keys = get_index(c, inclusion=inclusion, exclusion=exclusion)
        return c.filter(lambda x: x.name in keys and any_not_values(apply(x, f, *args, **kwargs)))
    elif is_table(c):
        mask = create_mask(c, condition=lambda x: not f(x, *args, **kwargs), fill=False, keys=keys)
        if is_frame(c):
            return c.loc[reduce_or(mask, axis=1)]
        return c.loc[mask]
    elif is_dict(c):
        return {k: c[k] for k in keys if not f(c[k], *args, **kwargs)}
    return collection_to_type([c[k] for k in keys if not f(c[k], *args, **kwargs)], c)


#########################


def filter_null(c, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are null for all the specified
    keys."""
    return filter_with(c, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_null(c, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are not null for all the
    specified keys."""
    return filter_not_with(c, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_null(c, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are null for at least one
    specified key."""
    return filter_any_with(c, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_null(c, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are not null for at least one
    specified key."""
    return filter_any_not_with(c, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


#########################


def filter_empty(c, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are empty for all the specified
    keys."""
    return filter_with(c, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_empty(c, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are not empty for all the
    specified keys."""
    return filter_not_with(c, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_empty(c, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are empty for at least one
    specified key."""
    return filter_any_with(c, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_empty(c, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are not empty for at least one
    specified key."""
    return filter_any_not_with(c, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


#########################


def filter_value(c, value, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are equal to the specified value
    for all the specified keys."""
    return filter_with(c, lambda v: v == value, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_value(c, value, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are not equal to the specified
    value for all the specified keys."""
    return filter_not_with(
        c, lambda v: v == value, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


def filter_any_value(c, value, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are equal to the specified value
    for at least one specified key."""
    return filter_any_with(
        c, lambda v: v == value, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


def filter_any_not_value(c, value, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are not equal to the specified
    value for at least one specified key."""
    return filter_any_not_with(
        c, lambda v: v == value, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


#########################


def filter_in(c, values, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are in the specified values for
    all the specified keys."""
    values = to_set(values)
    return filter_with(
        c, lambda v: v in values, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


def filter_not_in(c, values, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are not in the specified values
    for all the specified keys."""
    values = to_set(values)
    return filter_not_with(
        c, lambda v: v in values, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


def filter_any_in(c, values, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are in the specified values for
    at least one specified key."""
    values = to_set(values)
    return filter_any_with(
        c, lambda v: v in values, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


def filter_any_not_in(c, values, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are not in the specified values
    for at least one specified key."""
    values = to_set(values)
    return filter_any_not_with(
        c, lambda v: v in values, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


#########################


def filter_between(c, lower=None, upper=None, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are lying between the lower
    (inclusive) and upper (exclusive) bounds for all the specified keys."""
    if is_all_null(lower, upper):
        return c
    elif is_null(lower):
        return filter_with(
            c, lambda v: v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion
        )
    elif is_null(upper):
        return filter_with(
            c, lambda v: v >= lower, keys=keys, inclusion=inclusion, exclusion=exclusion
        )
    return filter_with(
        c, lambda v: lower <= v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


def filter_not_between(c, lower=None, upper=None, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are not lying between the lower
    (inclusive) and upper (exclusive) bounds for all the specified keys."""
    if is_all_null(lower, upper):
        return c
    elif is_null(lower):
        return filter_not_with(
            c, lambda v: v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion
        )
    elif is_null(upper):
        return filter_not_with(
            c, lambda v: v >= lower, keys=keys, inclusion=inclusion, exclusion=exclusion
        )
    return filter_not_with(
        c, lambda v: lower <= v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


def filter_any_between(c, lower=None, upper=None, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are lying between the lower
    (inclusive) and upper (exclusive) bounds for at least one specified key."""
    if is_all_null(lower, upper):
        return c
    elif is_null(lower):
        return filter_any_with(
            c, lambda v: v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion
        )
    elif is_null(upper):
        return filter_any_with(
            c, lambda v: v >= lower, keys=keys, inclusion=inclusion, exclusion=exclusion
        )
    return filter_any_with(
        c, lambda v: lower <= v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


def filter_any_not_between(c, lower=None, upper=None, keys=None, inclusion=None, exclusion=None):
    """Returns the entries of the specified collection whose values are not lying between the lower
    (inclusive) and upper (exclusive) bounds for at least one specified key."""
    if is_all_null(lower, upper):
        return c
    elif is_null(lower):
        return filter_any_not_with(
            c, lambda v: v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion
        )
    elif is_null(upper):
        return filter_any_not_with(
            c, lambda v: v >= lower, keys=keys, inclusion=inclusion, exclusion=exclusion
        )
    return filter_any_not_with(
        c, lambda v: lower <= v < upper, keys=keys, inclusion=inclusion, exclusion=exclusion
    )


#########################


def filter_days(c, days, week=False, year=False):
    """Filters the collection by matching its date-time index with the specified days (week days
    if week is True, days of the year if year is True, days of the month otherwise)."""
    indices = find_all_in(
        get_days(c, use_index=True, week=week, year=year),
        get_days(days, use_index=True, week=week, year=year),
    )
    return take_at(c, indices)


def filter_weeks(c, weeks):
    """Filters the collection by matching its date-time index with the specified weeks."""
    indices = find_all_in(get_weeks(c, use_index=True), get_weeks(weeks, use_index=True))
    return take_at(c, indices)


def filter_year_weeks(c, year_weeks):
    """Filters the collection by matching its date-time index with the specified year-weeks."""
    indices = find_all_in(
        get_year_weeks(c, use_index=True), get_year_weeks(year_weeks, use_index=True)
    )
    return take_at(c, indices)


def filter_months(c, months):
    """Filters the collection by matching its date-time index with the specified months."""
    indices = find_all_in(get_months(c, use_index=True), get_months(months, use_index=True))
    return take_at(c, indices)


def filter_quarters(c, quarters):
    """Filters the collection by matching its date-time index with the specified quarters."""
    indices = find_all_in(get_quarters(c, use_index=True), get_quarters(quarters, use_index=True))
    return take_at(c, indices)


def filter_semesters(c, semesters):
    """Filters the collection by matching its date-time index with the specified semesters."""
    indices = find_all_in(
        get_semesters(c, use_index=True), get_semesters(semesters, use_index=True)
    )
    return take_at(c, indices)


def filter_years(c, years):
    """Filters the collection by matching its date-time index with the specified years."""
    indices = find_all_in(get_years(c, use_index=True), get_years(years, use_index=True))
    return take_at(c, indices)


#########################


def flatten(c, type=None, axis=0):
    if is_empty(c):
        return to_array(type=type)
    if type is OBJECT_TYPE:
        return to_array(flatten_list(c), type=type)
    return get_values(c, type=type).flatten(order="C" if axis == 0 else "F" if axis == 1 else "A")


#########################


def groupby(c, agg=AGGREGATION, pos=POSITION, dof=1, axis=0):
    if pos is Position.START:
        return get_first(c, axis=axis)
    elif pos is Position.END:
        return get_last(c, axis=axis)
    elif agg is Aggregation.COUNT:
        return count(c, axis=axis)
    elif agg is Aggregation.MIN:
        return minimum(c, axis=axis)
    elif agg is Aggregation.MAX:
        return maximum(c, axis=axis)
    elif agg is Aggregation.MEAN:
        return mean(c, axis=axis)
    elif agg is Aggregation.MEDIAN:
        return median(c, axis=axis)
    elif agg is Aggregation.STD:
        return std(c, axis=axis, dof=dof)
    elif agg is Aggregation.VAR:
        return var(c, axis=axis, dof=dof)
    elif agg is Aggregation.SUM:
        return sum(c, axis=axis)


def count(*args, axis=0):
    c = forward(*args)
    if not is_collection(c):
        return 1
    if is_null(axis):
        return np.size(get_values(c))
    if is_group(c):
        return c.count()
    elif is_frame(c):
        return c.count(axis=axis)
    elif is_array(c):
        return np.apply_along_axis(len, axis, c)
    return len(c)


def minimum(*args, axis=0):
    c = forward(*args)
    if is_null(axis):
        return np.min(get_values(c))
    if is_group(c):
        return c.min()
    elif is_dict(c):
        c = get_values(c)
    return np.min(c, axis=axis)


def maximum(*args, axis=0):
    c = forward(*args)
    if is_null(axis):
        return np.max(get_values(c))
    if is_group(c):
        return c.max()
    elif is_dict(c):
        c = get_values(c)
    return np.max(c, axis=axis)


def mean(*args, axis=0):
    c = forward(*args)
    if is_null(axis):
        return np.mean(get_values(c))
    if is_group(c):
        return c.mean()
    elif is_dict(c):
        c = get_values(c)
    return np.mean(c, axis=axis)


def median(*args, axis=0):
    c = forward(*args)
    if is_null(axis):
        return np.median(get_values(c))
    if is_group(c):
        return c.median()
    elif is_dict(c):
        c = get_values(c)
    return np.median(c, axis=axis)


def std(*args, dof=1, axis=0):
    c = forward(*args)
    if is_null(axis):
        return np.std(get_values(c), ddof=dof)
    if is_group(c):
        return c.std(ddof=dof)
    elif is_dict(c):
        c = get_values(c)
    return np.std(c, axis=axis, ddof=dof)


def var(*args, dof=1, axis=0):
    c = forward(*args)
    if is_null(axis):
        return np.var(get_values(c), ddof=dof)
    if is_group(c):
        return c.var(ddof=dof)
    elif is_dict(c):
        c = get_values(c)
    return np.var(c, axis=axis, ddof=dof)


def sum(*args, axis=0):
    c = forward(*args)
    if is_null(axis):
        return np.sum(get_values(c))
    if is_group(c):
        return c.sum()
    elif is_dict(c):
        c = get_values(c)
    return np.sum(c, axis=axis)


#########################


def insert_all(
    *args,
    copy=False,
    ignore_index=False,
    sort=False,
    verify_integrity=False,
    keys=None,
    inclusion=None,
    exclusion=None,
):
    return reduce(
        insert,
        *args,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
        keys=keys,
        inclusion=inclusion,
        exclusion=exclusion,
    )


def insert(
    c1,
    c2,
    copy=False,
    ignore_index=False,
    sort=False,
    verify_integrity=False,
    keys=None,
    inclusion=None,
    exclusion=None,
):
    """Inserts the specified second collection into the first collection by inserting the values
    whose keys, which are in the specified inclusive list and are not in the specified exclusive
    list, or indexes are different."""
    # - Insert the rows
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
    # - Insert the columns
    return insert_cols(c1, c2, keys=keys, inclusion=inclusion, exclusion=exclusion)


def insert_rows(
    c1,
    c2,
    copy=False,
    ignore_index=False,
    sort=False,
    verify_integrity=False,
    keys=None,
    inclusion=None,
    exclusion=None,
):
    """Inserts the specified second collection into the first collection by inserting the rows
    whose keys, which are in the specified inclusive list and are not in the specified exclusive
    list, are identical and indexes are different."""
    if not is_table(c1) and not is_dict(c1):
        return c1
    if is_table(c2):
        c2 = exclude_index(c2, c1)
    if is_null(keys):
        keys = get_common_keys(c2, c1, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return c1
    c2 = collection_to_common_type(filter(c2, keys=keys), c1)
    if is_table(c1):
        c1 = concat_rows(
            c1,
            c2,
            copy=copy,
            ignore_index=ignore_index,
            sort=sort,
            verify_integrity=verify_integrity,
        )
    else:
        c1.update(c2)
    return c1


def insert_cols(
    c1,
    c2,
    copy=False,
    ignore_index=False,
    sort=False,
    verify_integrity=False,
    keys=None,
    inclusion=None,
    exclusion=None,
):
    """Inserts the specified second collection into the first collection by inserting the columns
    whose keys, which are in the specified inclusive list and are not in the specified exclusive
    list, are different."""
    if not is_table(c1) and not is_dict(c1):
        return c1
    if is_table(c2):
        c2 = include_index(c2, c2)
    if is_null(keys):
        keys = get_uncommon_keys(c2, c1, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return c1
    c2 = collection_to_common_type(filter(c2, keys=keys), c1)
    if is_table(c1):
        c1 = concat_cols(
            c1,
            c2,
            copy=copy,
            ignore_index=ignore_index,
            sort=sort,
            verify_integrity=verify_integrity,
        )
    else:
        c1.update(c2)
    return c1


#########################


def keep_min(c, n, agg=AGGREGATION, pos=POSITION, axis=0):
    g = groupby(c, agg=agg, pos=pos, axis=axis) if not is_group(c) and not is_null(group) else c
    if is_number(g):
        return g
    keys = [t[1] for t in sorted(zip(g, get_keys_or_index(g, axis=axis)))[:n]]
    return take(c, keys, axis=1 if (is_frame(c) or is_array(c)) and axis == 0 else 0)


def keep_min_with(c, n, f, axis=0):
    return keep_min(apply(c, f, axis=axis), n)


def keep_max(c, n, group=None, axis=0):
    g = groupby(c, group=group, axis=axis) if not is_group(c) and not is_null(group) else c
    if is_number(g):
        return g
    keys = [t[1] for t in sorted(zip(g, get_keys_or_index(g, axis=axis)), reverse=True)[:n]]
    return take(c, keys, axis=1 if (is_frame(c) or is_array(c)) and axis == 0 else 0)


def keep_max_with(c, n, f, axis=0):
    return keep_max(apply(c, f, axis=axis), n)


#########################


def remove_null(c, conservative=True, axis=0, keys=None, inclusion=None, exclusion=None):
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if axis == 0:
        if conservative:
            return filter_any_not_null(c, keys=keys)
        return filter_not_null(c, keys=keys)
    for k in keys:
        if is_all_null(c[k]) if conservative else is_any_null(c[k]):
            c = remove_col(c, names=k)
    return c


def remove_empty(c, conservative=True, axis=0, keys=None, inclusion=None, exclusion=None):
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if axis == 0:
        if conservative:
            return filter_any_not_empty(c, keys=keys)
        return filter_not_empty(c, keys=keys)
    for k in keys:
        if is_all_empty(c[k]) if conservative else is_any_empty(c[k]):
            c = remove_col(c, names=k)
    return c


def remove_value(c, value, conservative=True, axis=0, keys=None, inclusion=None, exclusion=None):
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if axis == 0:
        if conservative:
            return filter_any_not_value(c, value, keys=keys)
        return filter_not_value(c, value, keys=keys)
    for k in keys:
        if is_all_value(value, c[k]) if conservative else is_any_value(value, c[k]):
            c = remove_col(c, names=k)
    return c


#########################


def reverse(c, axis=0):
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_table(c):
        if axis == 0:
            return c.loc[::-1]
        return c.loc[:, ::-1]
    elif is_dict(c):
        return {v: k for k, v in c.items()}
    return c[::-1]


#########################


def shift_dates(
    c, years=0, months=0, weeks=0, days=0, hours=0, minutes=0, seconds=0, microseconds=0
):
    """Shifts the date-time index of the specified collection."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_table(c):
        t = c.copy()
        t.index += pd.DateOffset(
            years=years,
            months=months,
            weeks=weeks,
            days=days,
            hours=hours,
            minutes=minutes,
            seconds=seconds,
            microseconds=microseconds,
        )
        return t
    elif is_dict(c):
        return {
            shift_date(
                d,
                years=years,
                months=months,
                weeks=weeks,
                days=days,
                hours=hours,
                minutes=minutes,
                seconds=seconds,
                microseconds=microseconds,
            ): c[d]
            for d in c
        }
    return collection_to_type(
        [
            shift_date(
                d,
                years=years,
                months=months,
                weeks=weeks,
                days=days,
                hours=hours,
                minutes=minutes,
                seconds=seconds,
                microseconds=microseconds,
            )
            for d in c
        ],
        c,
    )


#########################


def simplify(c):
    if is_collection(c):
        if len(c) == 1:
            return simplify(get_next(c))
    return c


#########################


def slice(c, index_from=None, index_to=None, axis=0):
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_null(index_from):
        index_from = 0
    if is_null(index_to):
        index_to = len(c)
    keys = get_index_or_keys(c, axis=axis)
    return take(c, keys[index_from:index_to], axis=axis)


#########################


def sort(c, ascending=True, by=None, inplace=False, axis=0):
    """Sorts the values of the specified collection."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_frame(c):
        return c.sort_values(by, ascending=ascending, inplace=inplace, axis=axis)
    elif is_series(c):
        return c.sort_values(ascending=ascending, inplace=inplace)
    elif is_dict(c):
        return c
    if inplace:
        return c.sort()
    return sorted(c)


def sort_index(c):
    """Sorts the index of the specified collection."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_table(c):
        return c.sort_index()
    return c


#########################


def take(c, keys, axis=0):
    """Returns the entries of the specified collection for all the specified keys."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    keys = to_ordered_set(keys)
    if is_table(c):
        if axis == 0:
            return c.loc[keys]
        return c.loc[:, keys]
    return filter(c, keys=keys)


def take_not(c, keys, axis=0):
    """Returns the entries of the specified collection except for all the specified keys."""
    indices = find_all_not_in(get_index_or_keys(c, axis=axis), keys)
    return take_at(c, indices, axis=axis)


def take_at(c, indices, axis=0):
    """Returns the entries of the specified collection that are at the specified indices."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    indices = to_list(indices)
    if is_table(c):
        if axis == 0:
            return c.iloc[indices]
        return c.iloc[:, indices]
    elif is_dict(c):
        return {k: v for i, (k, v) in enumerate(c.items()) if i in indices or i - len(c) in indices}
    return collection_to_type([c[i] for i in indices], c)


def take_not_at(c, indices, axis=0):
    """Returns the entries of the specified collection that are not at the specified indices."""
    indices = find_all_not_in(range(count_rows(c) if axis == 0 else count_cols(c)), indices)
    return take_at(c, indices, axis=axis)


#########################


def tally(c, boundaries):
    """Tallies the values of the specified collection into the intervals delimited by the specified
    boundaries."""
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_empty(boundaries):
        return repeat(0, len(c))
    tc = c.copy()
    lower = minimum(tc, axis=None)
    for i, upper in enumerate(boundaries):
        set_values(tc, i, mask=create_mask(c, condition=lambda v: lower <= v < upper))
        lower = upper
    set_values(tc, i + 1, mask=create_mask(c, condition=lambda v: v >= upper))
    return tc


#########################


def unique(c, pos=POSITION):
    """
    Extracts unique elements or rows from a container, optionally preserving positional bias when
    duplicates are detected.

    Parameters
    ----------
    c : list, dict, pd.Series, pd.DataFrame, or Grouped container
        The input container to deduplicate.
    pos : Position (Enum) or None, optional
        Indicates which duplicate to retain:
        - Position.START: keep first occurrence (default for DataFrames)
        - Position.END: keep last occurrence
        - Position.MIDDLE: keep middle occurrence (rounded down if even number)
        - None or Position.AUTO: fastest deduplication (order-preserving)

    Returns
    -------
    object
        A deduplicated container of the same type, using the specified positional strategy.
    """
    if is_group(c):
        c = c.obj if c.axis == 0 else c.groups

    # Fastest path: order-preserving unique values
    if pos is None or pos is Position.AUTO:
        if is_table(c):
            return c.loc[~c.index.duplicated(keep="first")]
        elif is_dict(c):
            return c
        return list(dict.fromkeys(c))

    # Structured handling for known positions
    if is_table(c):
        if pos is Position.START:
            return c.loc[~c.index.duplicated(keep="first")]
        elif pos is Position.END:
            return c.loc[~c.index.duplicated(keep="last")]
        elif pos is Position.MIDDLE:

            def middle_idx(group):
                return group.iloc[len(group) // 2 : len(group) // 2 + 1]

            return c.groupby(c.index, sort=False).apply(middle_idx).reset_index(level=0, drop=True)
    elif is_dict(c):
        return c
    seen = {}
    for i, v in enumerate(c):
        if v not in seen:
            seen[v] = []
        seen[v].append(i)
    if pos is Position.START:
        return [c[seen[k][0]] for k in seen]
    elif pos is Position.END:
        return [c[seen[k][-1]] for k in seen]
    elif pos is Position.MIDDLE:
        return [c[seen[k][len(seen[k]) // 2]] for k in seen]
    return list(dict.fromkeys(c))  # fallback


#########################


def update_all(*args, keys=None, inclusion=None, exclusion=None):
    return reduce(
        lambda c1, c2: update(c1, c2, keys=keys, inclusion=inclusion, exclusion=exclusion), *args
    )


def update(c1, c2, keys=None, inclusion=None, exclusion=None):
    """Updates the specified first collection with the specified second collection by updating the
    values whose keys, which are in the specified inclusive list and are not in the specified
    exclusive list, and indexes are identical."""
    # - Update the rows
    if is_table(c2):
        c2 = include_index(c2, c1)
    if is_null(keys):
        keys = get_common_keys(c2, c1, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return c1
    c2 = collection_to_common_type(filter(c2, keys=keys), c1)
    if is_table(c1):
        types = get_element_types(c2)
        c1.update(c2.fillna(NA_NAME))
        c1.replace(NA_NAME, NAN, inplace=True)
        c1 = set_element_types(c1, types)
    elif is_dict(c1):
        c1.update(c2)
    else:
        for k in keys:
            c1[k] = c2[k]
    return c1


#########################


def upsert_all(
    *args,
    copy=False,
    ignore_index=False,
    sort=False,
    verify_integrity=False,
    keys=None,
    inclusion=None,
    exclusion=None,
):
    return reduce(
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
        *args,
    )


def upsert(
    c1,
    c2,
    copy=False,
    ignore_index=False,
    sort=False,
    verify_integrity=False,
    keys=None,
    inclusion=None,
    exclusion=None,
):
    """Upserts the specified first collection with the specified second collection by updating or
    inserting the values whose keys are in the specified inclusive list and are not in the specified
    exclusive list."""
    if is_null(keys):
        keys = get_keys(c2, inclusion=inclusion, exclusion=exclusion)
    c2 = collection_to_common_type(filter(c2, keys=keys), c1)
    return insert(
        update(c1, c2),
        c2,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
    )


def upsert_rows(
    c1,
    c2,
    copy=False,
    ignore_index=False,
    sort=False,
    verify_integrity=False,
    keys=None,
    inclusion=None,
    exclusion=None,
):
    """Upserts the specified first collection with the specified second collection by updating or
    inserting the rows whose keys, which are in the specified inclusive list and are not in the
    specified exclusive list, are identical."""
    if is_null(keys):
        keys = get_keys(c2, inclusion=inclusion, exclusion=exclusion)
    c2 = collection_to_common_type(filter(c2, keys=keys), c1)
    return insert_rows(
        update(c1, c2),
        c2,
        copy=copy,
        ignore_index=ignore_index,
        sort=sort,
        verify_integrity=verify_integrity,
    )


#########################


def where(
    c,
    *args,
    condition=lambda x, *args, **kwargs: True,
    keys=None,
    inclusion=None,
    exclusion=None,
    **kwargs,
):
    if is_empty(c) or not is_subscriptable_collection(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    return [k for k in keys if condition(c[k], *args, **kwargs)]
