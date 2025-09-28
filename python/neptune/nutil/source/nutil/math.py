#!/usr/bin/env python
####################################################################################################
# NAME
#   <NAME> - contains utility functions for mathematics
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from math import cos, sin

from nutil.scalar.number import *
from nutil.struct.util import *

####################################################################################################
# MATH CONSTANTS
####################################################################################################

__MATH_CONSTANTS__________________________________ = ""

E = np.e
PI = np.pi

#########################

DEG_TO_RAD = PI / 180
RAD_TO_DEG = 180 / PI


####################################################################################################
# MATH FUNCTIONS
####################################################################################################

__MATH____________________________________________ = ""


def is_negative(x: Any) -> bool:
    """Returns whether `x` is negative."""
    return x < 0


def is_non_negative(x: Any) -> bool:
    """Returns whether `x` is non-negative."""
    return x >= 0


def is_positive(x: Any) -> bool:
    """Returns whether `x` is positive."""
    return x > 0


def is_non_positive(x: Any) -> bool:
    """Returns whether `x` is non-positive."""
    return x <= 0


##################################################


def abs(x: Any):
    return np.abs(x)


def exp(x: Any):
    return np.exp(x)


def log(x: Any):
    return np.log(x)


def sqrt(x: Any):
    return np.sqrt(x)


#########################


def scale(x: Any, base=10, eps=EPS):
    """Returns `x` scaled so its magnitude is stable across orders of magnitude."""
    if is_collection(x):
        return apply(x, scale, axis=1, base=base, eps=eps)
    return x / base ** floor(log(maximum(abs(x)) + eps) / log(base))


#########################


def expand_dims(x, y, axis: Axis = 0):
    """Returns `x` with a dimension inserted at the specified axis to match the dimension of `y`."""
    if is_null(axis):
        return x  # for scalar broadcasting
    return x if np.ndim(x) == np.ndim(y) else np.expand_dims(x, axis=axis)


def sum_along(x, axis: Axis = 0):
    """Returns the sum along the specified axis with the dimension preserved."""
    if is_null(axis):
        return np.sum(x)  # for scalar broadcasting
    return expand_dims(np.sum(x, axis=axis), x, axis=axis)


def normalize(x, axis: Axis=0, eps=EPS):
    """Returns `x` divided by its sum along the specified axis.

    Adds a small epsilon to the denominator to improve numerical stability.
    """
    return x / (sum_along(x, axis=axis) + eps)


def softmax(x, axis: Axis=0, eps=EPS):
    m = np.max(x, axis=axis) if axis is not None else np.max(x)
    return normalize(exp(x - expand_dims(m, x, axis=axis)), axis=axis, eps=eps)


# • MATH ARITHMETIC ################################################################################

__MATH_ARITHMETIC_________________________________ = ""


def add_all(*args, numeric_default=None, object_default=None, rename=False):
    return reduce(
        args, add, numeric_default=numeric_default, object_default=object_default, rename=rename,
    )


def add(c1, c2, numeric_default=None, object_default=None, rename=False):
    """Returns the addition of the specified collections."""
    if is_list(c1):
        return [
            add(
                c, c2, numeric_default=numeric_default, object_default=object_default, rename=rename
            )
            for c in c1
        ]
    elif is_list(c2):
        return [
            add(
                c1, c, numeric_default=numeric_default, object_default=object_default, rename=rename
            )
            for c in c2
        ]
    elif is_table(c1) and is_table(c2):
        if is_frame(c1) and not is_frame(c2):
            return concat_cols(
                [
                    add(
                        set_names(c1[k], k),
                        c2,
                        numeric_default=numeric_default,
                        object_default=object_default,
                        rename=rename,
                    )
                    for k in get_keys(c1)
                ]
            )
        elif not is_frame(c1) and is_frame(c2):
            return concat_cols(
                [
                    add(
                        c1,
                        set_names(c2[k], k),
                        numeric_default=numeric_default,
                        object_default=object_default,
                        rename=rename,
                    )
                    for k in get_keys(c2)
                ]
            )
        if rename:
            names = get_names(c2)
            rename_all(c1, c2, names=get_names(c1))
        result = fill_null_all(
            c1, c2, numeric_default=numeric_default, object_default=object_default
        ) + fill_null_all(c2, c1, numeric_default=numeric_default, object_default=object_default)
        if rename:
            set_names(c2, names)
        return result
    elif (
        (is_table(c1) or is_number(c1))
        and (is_table(c2) or is_number(c2))
        or (is_array(c1) or is_number(c1))
        and (is_array(c2) or is_number(c2))
    ):
        return c1 + c2
    elif is_array(c1):
        return [collection_to_type(a, c2) for a in np.vstack(c1) + get_values(c2)]
    elif is_array(c2):
        return [collection_to_type(a, c1) for a in get_values(c1) + np.vstack(c2)]
    elif is_table(c1):
        return sum_cols(join(c1, get_values(c2)))
    elif is_table(c2):
        return sum_cols(join(c2, get_values(c1)))
    keys = get_common_keys(c1, c2)
    v1 = fill_null(
        get_values(c1, keys=keys), numeric_default=numeric_default, object_default=object_default
    )
    v2 = fill_null(
        get_values(c2, keys=keys), numeric_default=numeric_default, object_default=object_default
    )
    return collection_to_type(np.add(v1, v2), c1)


def subtract_all(*args, numeric_default=None, object_default=None, rename=False):
    return reduce(
        args,
        subtract,
        numeric_default=numeric_default,
        object_default=object_default,
        rename=rename,
    )


def subtract(c1, c2, numeric_default=None, object_default=None, rename=False):
    """Returns the subtraction of the specified collections."""
    if is_list(c1):
        return [
            subtract(
                c, c2, numeric_default=numeric_default, object_default=object_default, rename=rename
            )
            for c in c1
        ]
    elif is_list(c2):
        return [
            subtract(
                c1, c, numeric_default=numeric_default, object_default=object_default, rename=rename
            )
            for c in c2
        ]
    elif is_table(c1) and is_table(c2):
        if is_frame(c1) and not is_frame(c2):
            return concat_cols(
                [
                    subtract(
                        set_names(c1[k], k),
                        c2,
                        numeric_default=numeric_default,
                        object_default=object_default,
                        rename=rename,
                    )
                    for k in get_keys(c1)
                ]
            )
        elif not is_frame(c1) and is_frame(c2):
            return concat_cols(
                [
                    subtract(
                        c1,
                        set_names(c2[k], k),
                        numeric_default=numeric_default,
                        object_default=object_default,
                        rename=rename,
                    )
                    for k in get_keys(c2)
                ]
            )
        if rename:
            names = get_names(c2)
            rename_all(c1, c2, names=get_names(c1))
        result = fill_null_all(
            c1, c2, numeric_default=numeric_default, object_default=object_default
        ) - fill_null_all(c2, c1, numeric_default=numeric_default, object_default=object_default)
        if rename:
            set_names(c2, names)
        return result
    elif (
        (is_table(c1) or is_number(c1))
        and (is_table(c2) or is_number(c2))
        or (is_array(c1) or is_number(c1))
        and (is_array(c2) or is_number(c2))
    ):
        return c1 - c2
    elif is_array(c1):
        return [collection_to_type(a, c2) for a in np.vstack(c1) - get_values(c2)]
    elif is_array(c2):
        return [collection_to_type(a, c1) for a in get_values(c1) - np.vstack(c2)]
    elif is_table(c1):
        return sum_cols(join(c1, -get_values(c2)))
    elif is_table(c2):
        return sum_cols(join(-c2, get_values(c1)))
    keys = get_common_keys(c1, c2)
    v1 = fill_null(
        get_values(c1, keys=keys), numeric_default=numeric_default, object_default=object_default
    )
    v2 = fill_null(
        get_values(c2, keys=keys), numeric_default=numeric_default, object_default=object_default
    )
    return collection_to_type(np.subtract(v1, v2), c1)


def multiply_all(*args, numeric_default=None, object_default=None, rename=False):
    return reduce(
        args,
        multiply,
        numeric_default=numeric_default,
        object_default=object_default,
        rename=rename,
    )


def multiply(c1, c2, numeric_default=None, object_default=None, rename=False):
    """Returns the multiplication of the specified collections."""
    if is_list(c1):
        return [
            multiply(
                c, c2, numeric_default=numeric_default, object_default=object_default, rename=rename
            )
            for c in c1
        ]
    elif is_list(c2):
        return [
            multiply(
                c1, c, numeric_default=numeric_default, object_default=object_default, rename=rename
            )
            for c in c2
        ]
    elif is_table(c1) and is_table(c2):
        if is_frame(c1) and not is_frame(c2):
            return concat_cols(
                [
                    multiply(
                        set_names(c1[k], k),
                        c2,
                        numeric_default=numeric_default,
                        object_default=object_default,
                        rename=rename,
                    )
                    for k in get_keys(c1)
                ]
            )
        elif not is_frame(c1) and is_frame(c2):
            return concat_cols(
                [
                    multiply(
                        c1,
                        set_names(c2[k], k),
                        numeric_default=numeric_default,
                        object_default=object_default,
                        rename=rename,
                    )
                    for k in get_keys(c2)
                ]
            )
        if rename:
            names = get_names(c2)
            rename_all(c1, c2, names=get_names(c1))
        result = fill_null_all(
            c1, c2, numeric_default=numeric_default, object_default=object_default
        ) * fill_null_all(c2, c1, numeric_default=numeric_default, object_default=object_default)
        if rename:
            set_names(c2, names)
        return result
    elif (
        (is_table(c1) or is_number(c1))
        and (is_table(c2) or is_number(c2))
        or (is_array(c1) or is_number(c1))
        and (is_array(c2) or is_number(c2))
    ):
        return c1 * c2
    elif is_array(c1):
        return [collection_to_type(a, c2) for a in np.vstack(c1) * get_values(c2)]
    elif is_array(c2):
        return [collection_to_type(a, c1) for a in get_values(c1) * np.vstack(c2)]
    elif is_table(c1):
        return product_cols(join(c1, get_values(c2)))
    elif is_table(c2):
        return product_cols(join(c2, get_values(c1)))
    keys = get_common_keys(c1, c2)
    v1 = fill_null(
        get_values(c1, keys=keys), numeric_default=numeric_default, object_default=object_default
    )
    v2 = fill_null(
        get_values(c2, keys=keys), numeric_default=numeric_default, object_default=object_default
    )
    return collection_to_type(np.multiply(v1, v2), c1)


def divide_all(*args, numeric_default=None, object_default=None, rename=False):
    return reduce(
        args,
        divide,
        numeric_default=numeric_default,
        object_default=object_default,
        rename=rename,
    )


def divide(c1, c2, numeric_default=None, object_default=None, rename=False):
    """Returns the division of the specified collections."""
    if is_list(c1):
        return [
            divide(
                c, c2, numeric_default=numeric_default, object_default=object_default, rename=rename
            )
            for c in c1
        ]
    elif is_list(c2):
        return [
            divide(
                c1, c, numeric_default=numeric_default, object_default=object_default, rename=rename
            )
            for c in c2
        ]
    elif is_table(c1) and is_table(c2):
        if is_frame(c1) and not is_frame(c2):
            return concat_cols(
                [
                    divide(
                        set_names(c1[k], k),
                        c2,
                        numeric_default=numeric_default,
                        object_default=object_default,
                        rename=rename,
                    )
                    for k in get_keys(c1)
                ]
            )
        elif not is_frame(c1) and is_frame(c2):
            return concat_cols(
                [
                    divide(
                        c1,
                        set_names(c2[k], k),
                        numeric_default=numeric_default,
                        object_default=object_default,
                        rename=rename,
                    )
                    for k in get_keys(c2)
                ]
            )
        if rename:
            names = get_names(c2)
            rename_all(c1, c2, names=get_names(c1))
        result = fill_null_all(
            c1, c2, numeric_default=numeric_default, object_default=object_default
        ) / fill_null_all(c2, c1, numeric_default=numeric_default, object_default=object_default)
        if rename:
            set_names(c2, names)
        return result
    elif (
        (is_table(c1) or is_number(c1))
        and (is_table(c2) or is_number(c2))
        or (is_array(c1) or is_number(c1))
        and (is_array(c2) or is_number(c2))
    ):
        return safe_divide(c1, c2, invalid_default=numeric_default)
    elif is_array(c1):
        # Align shapes then safe divide per chunk
        v1 = np.vstack(c1)
        v2 = get_values(c2)
        return [
            collection_to_type(a, c2) for a in safe_divide(v1, v2, invalid_default=numeric_default)
        ]
    elif is_array(c2):
        # Align shapes then safe divide per chunk
        v1 = get_values(c1)
        v2 = np.vstack(c2)
        return [
            collection_to_type(a, c1) for a in safe_divide(v1, v2, invalid_default=numeric_default)
        ]
    elif is_table(c1):
        # Avoid 1 / 0 when forming reciprocals
        return product_cols(
            join(c1, safe_reciprocal(get_values(c2), invalid_default=numeric_default))
        )
    elif is_table(c2):
        # Avoid 1 / 0 when forming reciprocals
        return product_cols(
            join(c2, safe_reciprocal(get_values(c1), invalid_default=numeric_default))
        )
    # Dict/series-like fallthrough: compute on aligned numeric arrays
    keys = get_common_keys(c1, c2)
    v1 = fill_null(
        get_values(c1, keys=keys), numeric_default=numeric_default, object_default=object_default
    )
    v2 = fill_null(
        get_values(c2, keys=keys), numeric_default=numeric_default, object_default=object_default
    )
    return safe_divide(v1, v2, invalid_default=numeric_default, template=c1)


def safe_divide(c1, c2, eps=EPS, invalid_default=0, template=None):
    """Returns c1 / c2 with invalid_default where the denominator is zero or invalid.

    Broadcasts c1 and c2, promotes an element type using get_min_element_type, then ensures a
    floating/complex element type for true division. Maps the result back to the specified template
    (or to c1 if template is None).
    """
    # Broadcast first (so shapes match)
    b1, b2 = np.broadcast_arrays(c1, c2)

    # Pick an element type that can represent c1, c2 and the minimum requirement
    element_type = get_min_element_type(b1, b2, min_element_type=FLOAT_ELEMENT_TYPE)

    # Cast to the working element type
    b1 = np.asarray(b1, dtype=element_type)
    b2 = np.asarray(b2, dtype=element_type)

    # Build the output buffer and the valid-denominator mask
    out = np.full(b1.shape, np.asarray(invalid_default, dtype=element_type), dtype=element_type)
    mask = np.isfinite(b2) & (np.abs(b2) > np.asarray(eps, dtype=element_type))

    return collection_to_type(
        np.divide(b1, b2, out=out, where=mask), template if template is not None else c1
    )


def safe_reciprocal(c, element_type=FLOAT_ELEMENT_TYPE, invalid_default=0, template=None):
    """Returns 1 / c with invalid default values where c == 0 to avoid NaN/Inf."""
    return safe_divide(
        np.ones_like(c),
        c,
        element_type=element_type,
        invalid_default=invalid_default,
        template=template,
    )


#########################


def nearest_inferior(c, value):
    if not is_series(c) and not is_array(c):
        c = to_list(c)
    return nearest(add(filter_with(subtract(c, value), is_non_positive), value), value)


def nearest_superior(c, value):
    if not is_series(c) and not is_array(c):
        c = to_list(c)
    return nearest(add(filter_with(subtract(c, value), is_non_negative), value), value)


def farthest_inferior(c, value):
    if not is_series(c) and not is_array(c):
        c = to_list(c)
    return farthest(add(filter_with(subtract(c, value), is_non_positive), value), value)


def farthest_superior(c, value):
    if not is_series(c) and not is_array(c):
        c = to_list(c)
    return farthest(add(filter_with(subtract(c, value), is_non_negative), value), value)


# • MATH GEOMETRY ##################################################################################

__MATH_GEOMETRY___________________________________ = ""


def create_ellipse(center, a, b, angle=0, precision=100):
    X = []
    Y = []
    cx, cy = center
    for theta in create_sequence(0, 2 * PI, include=True, size=precision):
        # Compute the coordinates of the ellipse point at the angle theta
        px = a * cos(theta)
        py = b * sin(theta)

        # Rotate the ellipse point by the angle and translate it to the center
        x, y = rotate_point(px, py, angle=angle)
        x += cx
        y += cy

        X.append(x)
        Y.append(y)
    return X, Y


##################################################


def distance(v1, v2):
    return norm2(np.subtract(v1, v2), axis=0)


def distances(v1, v2):
    return norm2(np.subtract(v1, v2), axis=1)


def min_distance(v1, v2):
    return np.min(distances(v1, v2))


def min_distance_index(v1, v2):
    return np.argmin(distances(v1, v2))


def max_distance(v1, v2):
    return np.max(distances(v1, v2))


def max_distance_index(v1, v2):
    return np.argmax(distances(v1, v2))


#########################


def eigh(a, use_lower_part=True):
    return np.linalg.eigh(a, UPLO="L" if use_lower_part else "U")


#########################


def norm1(vector, axis: Axis=0):
    """Returns the L1 norm (Manhattan norm) of the specified vector along the axis."""
    return np.linalg.norm(vector, ord=1, axis=axis)


def norm2(vector, axis: Axis=0):
    """Returns the L2 norm (Euclidean norm) of the specified vector along the axis."""
    return np.linalg.norm(vector, ord=2, axis=axis)


def normalize1(vector, axis: Axis=0, eps=EPS):
    """
    Returns the L1-normalized vector along the specified axis (sum of absolute values = 1).

    Divides the vector by its L1 norm. Adds eps to avoid division by zero.
    """
    return vector / (norm1(vector, axis=axis) + eps)


def normalize2(vector, axis: Axis=0, eps=EPS):
    """
    Returns the L2-normalized vector along the specified axis (Euclidean length = 1).

    Divides the vector by its L2 norm. Adds eps to avoid division by zero.
    """
    return vector / (norm2(vector, axis=axis) + eps)


##################################################


def cos(x: Any):
    return np.cos(x)


def acos(x: Any):
    return np.arccos(x)


def sin(x: Any):
    return np.sin(x)


def asin(x: Any):
    return np.arcsin(x)


def tan(x: Any):
    return np.tan(x)


def atan(x: Any):
    return np.arctan(x)


def atan2(y, x):
    return np.arctan2(y, x)


#########################


def rotate_point(x, y, angle=0):
    return (cos(angle) * x - sin(angle) * y, sin(angle) * x + cos(angle) * y)
