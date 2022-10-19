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
# IMAGE CONSTANTS
####################################################################################################

__IMAGE_CONSTANTS_________________________________ = ''

DEFAULT_IMAGE_MODE = cv2.IMREAD_UNCHANGED

##################################################

BMP_FORMAT = 'bmp'  # bitmap
JPEG_FORMAT = 'jpg'  # Joint Photographic Experts Group
PNG_FORMAT = 'png'  # Portable Network Graphics
SVG_FORMAT = 'svg'  # Scalable Vector Graphics
TIFF_FORMAT = 'tiff'  # Tag Image File Format
WEBP_FORMAT = 'webp'  # WebP

####################################################################################################
# IMAGE FUNCTIONS
####################################################################################################

__IMAGE___________________________________________ = ''


def buffer_to_image(buffer, format, rotate=False):
	'''Converts the specified image buffer to an image of the specified format.'''
	if format == SVG_FORMAT:
		return buffer
	if rotate:
		buffer = rotate_anti_90(buffer)
	return cv2.imencode('.' + format, buffer)[1]


def buffer_to_html(buffer, format, encoding=DEFAULT_ENCODING, style=None,
                   rotate=False,
                   width=DEFAULT_WIDTH, height=DEFAULT_HEIGHT):
	'''Encodes the specified image buffer to Base64 and returns its HTML code.'''
	if is_empty(buffer):
		return ''
	if format == SVG_FORMAT:
		return buffer.decode(encoding)
	if rotate:
		width, height = height, width
	image = base64.b64encode(buffer_to_image(buffer, format, rotate=rotate)).decode(encoding)
	template = paste('<img width="{width}" height="{height}" src="data:image/{format};base64,{image}"',
	                 collapse('style="', style, '"') if not is_null(style) else '', '/>')
	return template.format(image=image, format=format, width=width, height=height)


#########################

def image_to_buffer(image, mode=DEFAULT_IMAGE_MODE):
	'''Converts the specified image to an image buffer.'''
	if image[1:4] == SVG_FORMAT.encode(DEFAULT_ENCODING):
		return image
	if is_string(image):
		image = read_bytes(image)
	return cv2.imdecode(np.frombuffer(image, dtype=SHORT_ELEMENT_TYPE), flags=mode)


def image_to_html(image, format, encoding=DEFAULT_ENCODING, mode=DEFAULT_IMAGE_MODE, style=None,
                  rotate=False,
                  width=DEFAULT_WIDTH, height=DEFAULT_HEIGHT):
	'''Converts the specified image to HTML.'''
	if format == SVG_FORMAT:
		return image.decode(encoding)
	return buffer_to_html(image_to_buffer(image, mode=mode), format, encoding=encoding, style=style,
	                      rotate=rotate,
	                      width=width, height=height)


##################################################

def generate_image(*shape):
	return create_random_short_array(255, *shape)


##################################################

def evaluate_colorfulness(buffer):
	'''Evaluates the colorfulness with the combination of the means and standard deviations of the
	color components of the specified RGB image buffer.'''
	# Split the color components of the RGB image buffer
	r, g, b = cv2.split(to_float(buffer))
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


def evaluate_blurriness(buffer):
	'''Evaluates the blurriness with the variance of the Laplacian of the specified image buffer.'''
	return cv2.Laplacian(buffer, cv2.CV_64F).var()


def evaluate_brightness(buffer):
	'''Evaluates the blurriness with the mean of the value (brightness) of the HSV representation of
	the specified image buffer.'''
	_, _, v = rgb_to_hsv(buffer)
	return mean(mean(v))


#########################

def resize_image(buffer,
                 width=DEFAULT_WIDTH, height=DEFAULT_HEIGHT):
	return cv2.resize(buffer, (width, height))


#########################

def rotate_anti_90(buffer):
	buffer = cv2.transpose(buffer)
	buffer = cv2.flip(buffer, 0)
	return buffer


def rotate_anti_270(buffer):
	buffer = cv2.transpose(buffer)
	buffer = cv2.flip(buffer, 1)
	return buffer


def rotate_by(buffer, angle, center=None, scale=1):
	h, w = buffer.shape[:2]
	if is_null(center):
		center = (w / 2, h / 2)
	m = cv2.getRotationMatrix2D(center, angle, scale)
	return cv2.warpAffine(buffer, m, (w, h))


#########################

def show_image(buffer, title='Image'):
	cv2.imshow(title, buffer)
	cv2.waitKey(0)
	cv2.destroyAllWindows()


##################################################

def write_image(path, buffer):
	return cv2.imwrite(path, buffer)
