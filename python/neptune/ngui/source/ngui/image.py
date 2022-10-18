#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain graphical utility functions for images
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

import base64

from ngui.common import *
from nutil.math import *

####################################################################################################
# IMAGE FUNCTIONS
####################################################################################################

__IMAGE___________________________________________ = ''


def buffer_to_image(buffer, format):
	'''Converts the specified image buffer to an image of the specified format.'''
	return cv2.imencode('.' + format, buffer)[1]


def buffer_to_html(buffer, format, width=DEFAULT_WIDTH, height=DEFAULT_HEIGHT,
                   mode=cv2.IMREAD_UNCHANGED, rotate=False, style=None):
	'''Encodes the specified image to Base64 and returns its HTML code.'''
	return image_to_html(buffer_to_image(buffer, format), format, width=width, height=height,
	                     mode=mode, rotate=rotate, style=style)


#########################

def image_to_buffer(image, mode=cv2.IMREAD_UNCHANGED):
	'''Converts the specified image to an image buffer.'''
	if is_string(image):
		image = read_bytes(image)
	return cv2.imdecode(np.frombuffer(image, dtype=SHORT_ELEMENT_TYPE), flags=mode)


def image_to_html(image, format, width=DEFAULT_WIDTH, height=DEFAULT_HEIGHT,
                  mode=cv2.IMREAD_UNCHANGED, rotate=False, style=None):
	'''Converts the specified image to HTML.'''
	if is_null(image):
		return ''
	buffer = image_to_buffer(image, mode=mode)
	if rotate:
		buffer = rotate_anti_90(buffer)
		width, height = height, width
	image = buffer_to_image(buffer, format)
	image = base64.b64encode(image).decode('utf-8')
	template = paste('<img width="{width}" height="{height}" src="data:image/{format};base64,{image}"',
	                 collapse('style="', style, '"') if not is_null(style) else '', '/>')
	return template.format(image=image, format=format, width=width, height=height)


##################################################

def generate_image(*shape):
	return create_random_short_array(255, *shape)


##################################################

def evaluate_colorfulness(image):
	'''Evaluates the colorfulness with the combination of the means and standard deviations of the
	color components of the specified RGB image.'''
	# Split the color components of the image
	r, g, b = cv2.split(to_float(image))
	# Compute rg = R - G
	rg = cv2.absdiff(r, g)
	# Compute yb = 0.5 * (R + G) - B
	yb = cv2.absdiff(cv2.addWeighted(r, 0.5, g, 0.5, 0), b)
	# Compute the mean and standard deviation of rg and yb
	rg_mean, rg_std = cv2.meanStdDev(rg)
	yb_mean, yb_std = cv2.meanStdDev(yb)
	# Combine the means and standard deviations
	mean_root = np.sqrt(rg_mean ** 2 + yb_mean ** 2)
	std_root = np.sqrt(rg_std ** 2 + yb_std ** 2)
	# Derive the colorfulness metric
	return simplify(0.3 * mean_root + std_root)


def evaluate_blurriness(image):
	'''Evaluates the blurriness with the variance of the Laplacian of the specified image.'''
	return cv2.Laplacian(image, cv2.CV_64F).var()


def evaluate_brightness(image):
	'''Evaluates the blurriness with the mean of the value (brightness) of the HSV representation of
	the specified image.'''
	_, _, v = rgb_to_hsv(image)
	return mean(mean(v))


#########################

def load_image(path, mode=cv2.IMREAD_UNCHANGED):
	return buffer_to_image(read_bytes(path), mode=mode)


#########################

def rotate_anti_90(image):
	image = cv2.transpose(image)
	image = cv2.flip(image, 0)
	return image


def rotate_anti_270(image):
	image = cv2.transpose(image)
	image = cv2.flip(image, 1)
	return image


def rotate_by(image, angle, center=None, scale=1):
	h, w = image.shape[:2]
	if is_null(center):
		center = (w / 2, h / 2)
	m = cv2.getRotationMatrix2D(center, angle, scale)
	return cv2.warpAffine(image, m, (w, h))


#########################

def show_image(image):
	cv2.imshow('image', image)
	cv2.waitKey(0)
	cv2.destroyAllWindows()


##################################################

def write_buffer(path, buffer):
	return cv2.imwrite(path, buffer)
