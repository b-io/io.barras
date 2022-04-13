#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain utility functions for mathematics
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

from nutil.common import *

####################################################################################################
# MATH CONSTANTS
####################################################################################################

__MATH_CONSTANTS__________________________________ = ''

E = np.e
PI = np.pi

#########################

DEG_TO_RAD = PI / 180
RAD_TO_DEG = 180 / PI

####################################################################################################
# MATH FUNCTIONS
####################################################################################################

__MATH____________________________________________ = ''


def is_negative(x):
	return x < 0


def is_non_negative(x):
	return x >= 0


def is_positive(x):
	return x > 0


def is_non_positive(x):
	return x <= 0


##################################################

def abs(x):
	return np.abs(x)


def exp(x):
	return np.exp(x)


def log(x):
	return np.log(x)


def sqrt(x):
	return np.sqrt(x)


#########################

def scale(x, base=10):
	if is_collection(x):
		return apply(x, scale, axis=1, base=base)
	return x / base ** floor(log(maximum(abs(x)) + EPS) / log(base))


# • MATH ARITHMETIC ################################################################################

__MATH_ARITHMETIC_________________________________ = ''


def add_all(*args, numeric_default=None, object_default=None, rename=False):
	return reduce(add, *args, numeric_default=numeric_default, object_default=object_default,
	              rename=rename)


def add(c1, c2, numeric_default=None, object_default=None, rename=False):
	'''Returns the addition of the specified collections.'''
	if is_list(c1):
		return [add(c, c2, numeric_default=numeric_default, object_default=object_default,
		            rename=rename) for c in c1]
	elif is_list(c2):
		return [add(c1, c, numeric_default=numeric_default, object_default=object_default,
		            rename=rename) for c in c2]
	elif is_table(c1) and is_table(c2):
		if is_frame(c1) and not is_frame(c2):
			return concat_cols(
				[add(set_names(c1[k], k), c2, numeric_default=numeric_default,
				     object_default=object_default, rename=rename) for k in get_keys(c1)])
		elif not is_frame(c1) and is_frame(c2):
			return concat_cols(
				[add(c1, set_names(c2[k], k), numeric_default=numeric_default,
				     object_default=object_default, rename=rename) for k in get_keys(c2)])
		if rename:
			names = get_names(c2)
			rename_all(c1, c2, names=get_names(c1))
		result = fill_null_all(c1, c2, numeric_default=numeric_default, object_default=object_default) + \
		         fill_null_all(c2, c1, numeric_default=numeric_default, object_default=object_default)
		if rename:
			set_names(c2, names)
		return result
	elif (is_table(c1) or is_number(c1)) and (is_table(c2) or is_number(c2)) or \
			(is_array(c1) or is_number(c1)) and (is_array(c2) or is_number(c2)):
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
	v1 = fill_null(get_values(c1, keys=keys), numeric_default=numeric_default, object_default=object_default)
	v2 = fill_null(get_values(c2, keys=keys), numeric_default=numeric_default, object_default=object_default)
	return collection_to_type(np.add(v1, v2), c1)


def subtract_all(*args, numeric_default=None, object_default=None, rename=False):
	return reduce(subtract, *args, numeric_default=numeric_default, object_default=object_default,
	              rename=rename)


def subtract(c1, c2, numeric_default=None, object_default=None, rename=False):
	'''Returns the subtraction of the specified collections.'''
	if is_list(c1):
		return [subtract(c, c2, numeric_default=numeric_default, object_default=object_default,
		                 rename=rename) for c in c1]
	elif is_list(c2):
		return [subtract(c1, c, numeric_default=numeric_default, object_default=object_default,
		                 rename=rename) for c in c2]
	elif is_table(c1) and is_table(c2):
		if is_frame(c1) and not is_frame(c2):
			return concat_cols(
				[subtract(set_names(c1[k], k), c2, numeric_default=numeric_default,
				          object_default=object_default, rename=rename) for k in get_keys(c1)])
		elif not is_frame(c1) and is_frame(c2):
			return concat_cols(
				[subtract(c1, set_names(c2[k], k), numeric_default=numeric_default,
				          object_default=object_default, rename=rename) for k in get_keys(c2)])
		if rename:
			names = get_names(c2)
			rename_all(c1, c2, names=get_names(c1))
		result = fill_null_all(c1, c2, numeric_default=numeric_default, object_default=object_default) - \
		         fill_null_all(c2, c1, numeric_default=numeric_default, object_default=object_default)
		if rename:
			set_names(c2, names)
		return result
	elif (is_table(c1) or is_number(c1)) and (is_table(c2) or is_number(c2)) or \
			(is_array(c1) or is_number(c1)) and (is_array(c2) or is_number(c2)):
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
	v1 = fill_null(get_values(c1, keys=keys), numeric_default=numeric_default, object_default=object_default)
	v2 = fill_null(get_values(c2, keys=keys), numeric_default=numeric_default, object_default=object_default)
	return collection_to_type(np.subtract(v1, v2), c1)


def multiply_all(*args, numeric_default=None, object_default=None, rename=False):
	return reduce(multiply, *args, numeric_default=numeric_default, object_default=object_default,
	              rename=rename)


def multiply(c1, c2, numeric_default=None, object_default=None, rename=False):
	'''Returns the multiplication of the specified collections.'''
	if is_list(c1):
		return [multiply(c, c2, numeric_default=numeric_default, object_default=object_default,
		                 rename=rename) for c in c1]
	elif is_list(c2):
		return [multiply(c1, c, numeric_default=numeric_default, object_default=object_default,
		                 rename=rename) for c in c2]
	elif is_table(c1) and is_table(c2):
		if is_frame(c1) and not is_frame(c2):
			return concat_cols(
				[multiply(set_names(c1[k], k), c2, numeric_default=numeric_default,
				          object_default=object_default, rename=rename) for k in get_keys(c1)])
		elif not is_frame(c1) and is_frame(c2):
			return concat_cols(
				[multiply(c1, set_names(c2[k], k), numeric_default=numeric_default,
				          object_default=object_default, rename=rename) for k in get_keys(c2)])
		if rename:
			names = get_names(c2)
			rename_all(c1, c2, names=get_names(c1))
		result = fill_null_all(c1, c2, numeric_default=numeric_default, object_default=object_default) * \
		         fill_null_all(c2, c1, numeric_default=numeric_default, object_default=object_default)
		if rename:
			set_names(c2, names)
		return result
	elif (is_table(c1) or is_number(c1)) and (is_table(c2) or is_number(c2)) or \
			(is_array(c1) or is_number(c1)) and (is_array(c2) or is_number(c2)):
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
	v1 = fill_null(get_values(c1, keys=keys), numeric_default=numeric_default, object_default=object_default)
	v2 = fill_null(get_values(c2, keys=keys), numeric_default=numeric_default, object_default=object_default)
	return collection_to_type(np.multiply(v1, v2), c1)


def divide_all(*args, numeric_default=None, object_default=None, rename=False):
	return reduce(divide, *args, numeric_default=numeric_default, object_default=object_default,
	              rename=rename)


def divide(c1, c2, numeric_default=None, object_default=None, rename=False):
	'''Returns the division of the specified collections.'''
	if is_list(c1):
		return [divide(c, c2, numeric_default=numeric_default, object_default=object_default,
		               rename=rename) for c in c1]
	elif is_list(c2):
		return [divide(c1, c, numeric_default=numeric_default, object_default=object_default,
		               rename=rename) for c in c2]
	elif is_table(c1) and is_table(c2):
		if is_frame(c1) and not is_frame(c2):
			return concat_cols(
				[divide(set_names(c1[k], k), c2, numeric_default=numeric_default,
				        object_default=object_default, rename=rename) for k in get_keys(c1)])
		elif not is_frame(c1) and is_frame(c2):
			return concat_cols(
				[divide(c1, set_names(c2[k], k), numeric_default=numeric_default,
				        object_default=object_default, rename=rename) for k in get_keys(c2)])
		if rename:
			names = get_names(c2)
			rename_all(c1, c2, names=get_names(c1))
		result = fill_null_all(c1, c2, numeric_default=numeric_default, object_default=object_default) / \
		         fill_null_all(c2, c1, numeric_default=numeric_default, object_default=object_default)
		if rename:
			set_names(c2, names)
		return result
	elif (is_table(c1) or is_number(c1)) and (is_table(c2) or is_number(c2)) or \
			(is_array(c1) or is_number(c1)) and (is_array(c2) or is_number(c2)):
		return c1 / c2
	elif is_array(c1):
		return [collection_to_type(a, c2) for a in np.vstack(c1) / get_values(c2)]
	elif is_array(c2):
		return [collection_to_type(a, c1) for a in get_values(c1) / np.vstack(c2)]
	elif is_table(c1):
		return product_cols(join(c1, 1 / get_values(c2)))
	elif is_table(c2):
		return product_cols(join(1 / c2, get_values(c1)))
	keys = get_common_keys(c1, c2)
	v1 = fill_null(get_values(c1, keys=keys), numeric_default=numeric_default, object_default=object_default)
	v2 = fill_null(get_values(c2, keys=keys), numeric_default=numeric_default, object_default=object_default)
	return collection_to_type(np.divide(v1, v2), c1)


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

__MATH_GEOMETRY___________________________________ = ''


def create_ellipse(center, a, b, angle=0, precision=100):
	X = []
	Y = []
	cx, cy = center
	for theta in create_sequence(0, 2 * PI, include=True, size=precision):
		# Calculate the coordinates of the ellipse point at the angle theta
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

def cos(x):
	return np.cos(x)


def acos(x):
	return np.arccos(x)


def sin(x):
	return np.sin(x)


def asin(x):
	return np.arcsin(x)


def tan(x):
	return np.tan(x)


def atan(x):
	return np.arctan(x)


def atan2(y, x):
	return np.arctan2(y, x)


#########################

def rotate_point(x, y, angle=0):
	return cos(angle) * x - sin(angle) * y, \
	       sin(angle) * x + cos(angle) * y
