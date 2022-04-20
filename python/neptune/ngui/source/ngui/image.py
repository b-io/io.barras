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

import cv2

from ngui.common import *
from nutil.math import *

####################################################################################################
# IMAGE FUNCTIONS
####################################################################################################

__IMAGE___________________________________________ = ''


def buffer_to_html(buffer, format, width=DEFAULT_WIDTH, height=DEFAULT_HEIGHT, style=None):
	'''Converts the specified image buffer to HTML.'''
	if is_null(buffer):
		return ''
	image = base64.b64encode(buffer).decode('utf-8')
	template = paste('<img width="{width}" height="{height}" src="data:image/{format};base64,{image}"',
	                 collapse('style="', style, '"') if not is_null(style) else '', '/>')
	return template.format(image=image, format=format, width=width, height=height)


def buffer_to_image(buffer):
	'''Converts the specified image buffer to an image.'''
	image = np.frombuffer(buffer, dtype=SHORT_ELEMENT_TYPE)
	return cv2.imdecode(image, flags=cv2.IMREAD_UNCHANGED)


#########################

def image_to_html(path, width=DEFAULT_WIDTH, height=DEFAULT_HEIGHT, rotate=False, style=None):
	'''Encodes the specified image to Base64 and returns its HTML code.'''
	format = get_extension(path).lower()
	with open(path, mode='rb') as f:
		buffer = f.read()
		if rotate:
			image = rotate_anti_90(buffer_to_image(buffer))
			_, buffer = cv2.imencode('.' + format, image)
		return buffer_to_html(buffer, format, width=width, height=height, style=style)


##################################################

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
