#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains collection converters
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

from __future__ import annotations

from nutil.struct.collection.registry.ordered_set import *

####################################################################################################
# COLLECTION ACCESSORS
####################################################################################################

__COLLECTION_ACCESSORS____________________________ = ""


def get(c, index, axis=0):
    if is_empty(c) or not is_subscriptable(c):
        return c
    if is_null(axis):
        return simplify(flatten(c, axis=axis)[index])
    if is_multidimensional(c):
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
    if is_multidimensional(c):
        return c.shape
    elif is_tuple(c):
        return c
    return (len(c),)


#########################


def get_name(c, inclusion=None, exclusion=None):
    return simplify(get_names(c, inclusion=inclusion, exclusion=exclusion))


def get_names(c, inclusion=None, exclusion=None):
    """Returns the names of the specified collection."""
    if is_group_by(c):
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
    if is_group_by(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable(c):
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
    if is_group_by(c):
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
    elif not is_subscriptable(c):
        return to_list(c)
    if not has_filter(keys=keys, inclusion=inclusion, exclusion=exclusion):
        if is_table(c) and not is_group_by(c) or is_dict(c):
            return to_list(c.items())
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return []
    if is_group_by(c):
        if c.axis == 0:
            return [(k, filter(v, keys=keys)) for k, v in c]
        return [(k, v) for k, v in c if k in keys]
    return [(k, c[k]) for k in keys]


#########################


def get_value(c, element_type=None, keys=None, inclusion=None, exclusion=None):
    return simplify(
        get_values(
            c, element_type=element_type, keys=keys, inclusion=inclusion, exclusion=exclusion
        )
    )


def get_values(c, element_type=None, keys=None, inclusion=None, exclusion=None):
    """Returns the values (values/values/columns) of the specified collection whose keys
    (indices/keys/names) are in the specified inclusive list and are not in the specified exclusive
    list."""
    if is_empty(c):
        return to_array(element_type=element_type)
    elif not is_subscriptable(c):
        return to_array(c, element_type=element_type)
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return to_array(element_type=element_type)
    if is_group_by(c):
        if c.axis == 0:
            return to_array([filter(v, keys=keys).values for k, v in c], element_type=element_type)
        return to_array([v.values for k, v in c if k in keys], element_type=element_type)
    elif is_table(c):
        return filter(c, keys=keys).values
    elif is_array(c):
        return c[keys]
    return to_array([c[k] for k in keys], element_type=element_type)


#########################


def get_element_type(c, keys=None, inclusion=None, exclusion=None):
    return simplify(get_element_types(c, keys=keys, inclusion=inclusion, exclusion=exclusion))


def get_element_types(c, keys=None, inclusion=None, exclusion=None):
    """Returns the element types of the specified collection whose keys (indices/keys/names) are in
    the specified inclusive list and are not in the specified exclusive list."""
    if is_empty(c):
        return {}
    elif not is_subscriptable(c):
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


def get_min_element_type(c1, c2, min_element_type=FLOAT_ELEMENT_TYPE):
    """Returns an element type that can safely represent c1, c2, and min_element_type.

    Uses NumPy's type promotion rules:
    - Computes the combined element type of c1 and c2.
    - Promotes it with the specified min_element_type.
    This is generic and works for bools, integers, unsigned integers, floats, and complex.
    """
    return np.promote_types(np.result_type(c1, c2), np.dtype(min_element_type))


##################################################


def set_names(c, new_names):
    """Sets the names of the specified collection."""
    if is_group_by(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable(c):
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
    if is_group_by(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable(c):
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
    if is_group_by(c):
        c = c.obj if c.axis == 1 else c.groups
    if is_empty(c) or not is_subscriptable(c):
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
    if is_group_by(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c) or not is_subscriptable(c):
        return c
    if is_null(keys):
        keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
    if is_empty(keys):
        return c
    if is_collection(new_values):
        new_values = get_values(new_values)
    else:
        if not is_multidimensional(c) or is_null(mask):
            new_values = create_array(get_shape(c, keys=keys), fill=new_values)
    if not is_null(mask):
        if is_multidimensional(c):
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


####################################################################################################
# COLLECTION CONVERTERS
####################################################################################################

__COLLECTION_CONVERTERS___________________________ = ""


def to_collection(*args):
    if len(args) == 1:
        arg = args[0]
        if is_collection(arg):
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


def collection_to_type(c, template):
    if is_frame(template):
        return to_frame(c, names=template, index=template)
    elif is_series(template):
        return to_series(c, name=template, index=template)
    elif is_dict(template):
        return dict(zip(get_keys(template), c))
    elif is_ordered_set(template):
        return to_ordered_set(c)
    elif is_set(template):
        return to_set(c)
    elif is_array(template):
        return to_array(c)
    elif is_list(template):
        return to_list(c)
    return c


def collection_to_common_type(c, template):
    if is_frame(template):
        return to_frame(c)
    elif is_series(template):
        return to_series(c)
    elif is_dict(template):
        return to_dict(c)
    elif is_ordered_set(template):
        return to_ordered_set(c)
    elif is_set(template):
        return to_set(c)
    elif is_array(template):
        return to_array(c)
    elif is_list(template):
        return to_list(c)
    return c


# • ARRAY ##########################################################################################

__COMMON_ARRAY_CONVERTERS_________________________ = ""


def to_array(*args, element_type=None):
    if len(args) == 1:
        arg = args[0]
        if is_array(arg):
            return arg
        elif is_subscriptable(arg):
            return np.array(arg, dtype=element_type)
    return np.array(to_list(*args), dtype=element_type)


def unarray(a):
    if is_array(a):
        if len(a) == 1:
            return a[0]
        return tuple(a)
    return a


# • DICT ###########################################################################################

__COMMON_DICT_CONVERTERS__________________________ = ""


def to_dict(c):
    """Converts the specified collection to a dictionary."""
    if is_group_by(c):
        c = c.obj if c.axis == 0 else c.groups
    if is_empty(c):
        return {}
    elif is_table(c):
        return c.to_dict()
    elif is_dict(c):
        return c
    return {i: v for i, v in enumerate(c)}


# • LIST ###########################################################################################

__COMMON_LIST_CONVERTERS__________________________ = ""


def unlist(l):
    if is_list(l):
        if len(l) == 1:
            return l[0]
        return tuple(l)
    return l


# • SET ############################################################################################

__COMMON_SET_CONVERTERS___________________________ = ""


def unset(s):
    if is_set(s):
        if len(s) == 1:
            return get_next(s)
        return tuple(s)
    return s
