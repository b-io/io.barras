#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain mathematical utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2021 Florian Barras <https://barras.io>.
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
		return apply(scale, x, axis=1, base=base)
	return x / base ** floor(log(max(abs(x)) + EPS) / log(base))


# • MATH ARITHMETIC ################################################################################

__MATH_ARITHMETIC_________________________________ = ''


def add_all(*args):
	return reduce(add, *args)


def add(c1, c2):
	"""Returns the addition of the specified collections."""
	if is_list(c1):
		return [add(c, c2) for c in c1]
	elif is_list(c2):
		return [add(c1, c) for c in c2]
	elif is_table(c1) and is_table(c2):
		if is_frame(c1) and not is_frame(c2):
			return concat_cols([add(c1[k], c2) for k in get_keys(c1)])
		elif not is_frame(c1) and is_frame(c2):
			return concat_cols([add(c1, c2[k]) for k in get_keys(c2)])
		return set_names(sum_cols(join(c1, c2)), c1)
	elif (is_table(c1) or is_number(c1)) and (is_table(c2) or is_number(c2)) or \
			(is_array(c1) or is_number(c1)) and (is_array(c2) or is_number(c2)):
		return c1 + c2
	elif is_array(c1):
		return [array_to_type(a, c2) for a in np.vstack(c1) + get_values(c2)]
	elif is_array(c2):
		return [array_to_type(a, c1) for a in get_values(c1) + np.vstack(c2)]
	elif is_table(c1):
		return sum_cols(join(c1, get_values(c2)))
	elif is_table(c2):
		return sum_cols(join(c2, get_values(c1)))
	return array_to_type(np.add(get_values(c1), get_values(c2)), c1)


def subtract_all(*args):
	return reduce(subtract, *args)


def subtract(c1, c2):
	"""Returns the subtraction of the specified collections."""
	if is_list(c1):
		return [subtract(c, c2) for c in c1]
	elif is_list(c2):
		return [subtract(c1, c) for c in c2]
	elif is_table(c1) and is_table(c2):
		if is_frame(c1) and not is_frame(c2):
			return concat_cols([subtract(c1[k], c2) for k in get_keys(c1)])
		elif not is_frame(c1) and is_frame(c2):
			return concat_cols([subtract(c1, c2[k]) for k in get_keys(c2)])
		return set_names(sum_cols(join(c1, -c2)), c1)
	elif (is_table(c1) or is_number(c1)) and (is_table(c2) or is_number(c2)) or \
			(is_array(c1) or is_number(c1)) and (is_array(c2) or is_number(c2)):
		return c1 - c2
	elif is_array(c1):
		return [array_to_type(a, c2) for a in np.vstack(c1) - get_values(c2)]
	elif is_array(c2):
		return [array_to_type(a, c1) for a in get_values(c1) - np.vstack(c2)]
	elif is_table(c1):
		return sum_cols(join(c1, -get_values(c2)))
	elif is_table(c2):
		return sum_cols(join(-c2, get_values(c1)))
	return array_to_type(np.subtract(get_values(c1), get_values(c2)), c1)


def multiply_all(*args):
	return reduce(multiply, *args)


def multiply(c1, c2):
	"""Returns the multiplication of the specified collections."""
	if is_list(c1):
		return [multiply(c, c2) for c in c1]
	elif is_list(c2):
		return [multiply(c1, c) for c in c2]
	elif is_table(c1) and is_table(c2):
		if is_frame(c1) and not is_frame(c2):
			return concat_cols([multiply(c1[k], c2) for k in get_keys(c1)])
		elif not is_frame(c1) and is_frame(c2):
			return concat_cols([multiply(c1, c2[k]) for k in get_keys(c2)])
		return set_names(product_cols(join(c1, c2)), c1)
	elif (is_table(c1) or is_number(c1)) and (is_table(c2) or is_number(c2)) or \
			(is_array(c1) or is_number(c1)) and (is_array(c2) or is_number(c2)):
		return c1 * c2
	elif is_array(c1):
		return [array_to_type(a, c2) for a in np.vstack(c1) * get_values(c2)]
	elif is_array(c2):
		return [array_to_type(a, c1) for a in get_values(c1) * np.vstack(c2)]
	elif is_table(c1):
		return product_cols(join(c1, get_values(c2)))
	elif is_table(c2):
		return product_cols(join(c2, get_values(c1)))
	return array_to_type(np.multiply(get_values(c1), get_values(c2)), c1)


def divide_all(*args):
	return reduce(divide, *args)


def divide(c1, c2):
	"""Returns the division of the specified collections."""
	if is_list(c1):
		return [divide(c, c2) for c in c1]
	elif is_list(c2):
		return [divide(c1, c) for c in c2]
	elif is_table(c1) and is_table(c2):
		if is_frame(c1) and not is_frame(c2):
			return concat_cols([divide(c1[k], c2) for k in get_keys(c1)])
		elif not is_frame(c1) and is_frame(c2):
			return concat_cols([divide(c1, c2[k]) for k in get_keys(c2)])
		return set_names(product_cols(join(c1, 1 / c2)), c1)
	elif (is_table(c1) or is_number(c1)) and (is_table(c2) or is_number(c2)) or \
			(is_array(c1) or is_number(c1)) and (is_array(c2) or is_number(c2)):
		return c1 / c2
	elif is_array(c1):
		return [array_to_type(a, c2) for a in np.vstack(c1) / get_values(c2)]
	elif is_array(c2):
		return [array_to_type(a, c1) for a in get_values(c1) / np.vstack(c2)]
	elif is_table(c1):
		return product_cols(join(c1, 1 / get_values(c2)))
	elif is_table(c2):
		return product_cols(join(1 / c2, get_values(c1)))
	return array_to_type(np.divide(get_values(c1), get_values(c2)), c1)


# • MATH GEOMETRY ##################################################################################

__MATH_GEOMETRY___________________________________ = ''


def create_ellipse(center, a, b, angle=0, precision=100):
	X = []
	Y = []
	cx, cy = center
	for theta in create_sequence(0, 2 * PI, include=True, n=precision):
		# Calculate the coordinates of the ellipse point at the angle theta
		px = a * cos(theta)
		py = b * sin(theta)

		# Rotate the ellipse point by the angle and translate it to the center
		x, y = rotate(px, py, angle)
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

def rotate(x, y, angle=0):
	return cos(angle) * x - sin(angle) * y, \
	       sin(angle) * x + cos(angle) * y
