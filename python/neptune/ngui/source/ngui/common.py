#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain common graphical utility functions
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

import matplotlib.cm as mcm
import matplotlib.colors as mcolors

from nmath import normal
from nutil.math import *

####################################################################################################
# GUI COMMON CONSTANTS
####################################################################################################

__GUI_COMMON_CONSTANTS____________________________ = ''

# The default scale
DEFAULT_SCALE = 1  # the higher, the better quality

# The default width
DEFAULT_WIDTH = 660

# The default height
DEFAULT_HEIGHT = 933

# The default margin (left, right, bottom and top) defined by the ratio to the width or height
DEFAULT_MARGIN = dict(l=0, r=0, b=0, t=0)
DEFAULT_MARGIN_WITH_TITLE = dict(l=0, r=0, b=0, t=0.05)

# The default marker size
DEFAULT_MARKER_SIZE = 4

# • GUI COLOR ######################################################################################

__GUI_COLOR_CONSTANTS_____________________________ = ''

TRANSPARENT = (0, 0, 0, 0)

##################################################

# The default colors
DEFAULT_COLORS = [
	'#1F77B4',  # muted blue
	'#FF7F0E',  # safety orange
	'#2CA02C',  # cooked asparagus green
	'#D62728',  # brick red
	'#9467BD',  # muted purple
	'#8C564B',  # chestnut brown
	'#E377C2',  # raspberry yogurt pink
	'#7F7F7F',  # middle gray
	'#BCBD22',  # curry yellow-green
	'#17BECF'  # blue-teal
]

# The default colors iterator
DEFAULT_COLORS_ITERATOR = get_iterator(DEFAULT_COLORS, cycle=True)

# The default background color
DEFAULT_BG_COLOR = TRANSPARENT

# The default font size
DEFAULT_FONT_SIZE = 12

##################################################

NAMED_COLORS = [
	'aliceblue', 'antiquewhite', 'aqua', 'aquamarine', 'azure',
	'beige', 'bisque', 'black', 'blanchedalmond', 'blue',
	'blueviolet', 'brown', 'burlywood', 'cadetblue',
	'chartreuse', 'chocolate', 'coral', 'cornflowerblue',
	'cornsilk', 'crimson', 'cyan', 'darkblue', 'darkcyan',
	'darkgoldenrod', 'darkgray', 'darkgrey', 'darkgreen',
	'darkkhaki', 'darkmagenta', 'darkolivegreen', 'darkorange',
	'darkorchid', 'darkred', 'darksalmon', 'darkseagreen',
	'darkslateblue', 'darkslategray', 'darkslategrey',
	'darkturquoise', 'darkviolet', 'deeppink', 'deepskyblue',
	'dimgray', 'dimgrey', 'dodgerblue', 'firebrick',
	'floralwhite', 'forestgreen', 'fuchsia', 'gainsboro',
	'ghostwhite', 'gold', 'goldenrod', 'gray', 'grey', 'green',
	'greenyellow', 'honeydew', 'hotpink', 'indianred', 'indigo',
	'ivory', 'khaki', 'lavender', 'lavenderblush', 'lawngreen',
	'lemonchiffon', 'lightblue', 'lightcoral', 'lightcyan',
	'lightgoldenrodyellow', 'lightgray', 'lightgrey',
	'lightgreen', 'lightpink', 'lightsalmon', 'lightseagreen',
	'lightskyblue', 'lightslategray', 'lightslategrey',
	'lightsteelblue', 'lightyellow', 'lime', 'limegreen',
	'linen', 'magenta', 'maroon', 'mediumaquamarine',
	'mediumblue', 'mediumorchid', 'mediumpurple',
	'mediumseagreen', 'mediumslateblue', 'mediumspringgreen',
	'mediumturquoise', 'mediumvioletred', 'midnightblue',
	'mintcream', 'mistyrose', 'moccasin', 'navajowhite', 'navy',
	'oldlace', 'olive', 'olivedrab', 'orange', 'orangered',
	'orchid', 'palegoldenrod', 'palegreen', 'paleturquoise',
	'palevioletred', 'papayawhip', 'peachpuff', 'peru', 'pink',
	'plum', 'powderblue', 'purple', 'red', 'rosybrown',
	'royalblue', 'rebeccapurple', 'saddlebrown', 'salmon',
	'sandybrown', 'seagreen', 'seashell', 'sienna', 'silver',
	'skyblue', 'slateblue', 'slategray', 'slategrey', 'snow',
	'springgreen', 'steelblue', 'tan', 'teal', 'thistle', 'tomato',
	'turquoise', 'violet', 'wheat', 'white', 'whitesmoke',
	'yellow', 'yellowgreen'
]

RAINBOW_SCALE = mcm.get_cmap(name='rainbow')
RYG_SCALE = mcm.get_cmap(name='RdYlGn')

####################################################################################################
# GUI COMMON FUNCTIONS
####################################################################################################

__GUI_COMMON______________________________________ = ''

# • GUI COLOR ######################################################################################

__GUI_COLOR_______________________________________ = ''


def get_alternate_colors(n, row_odd_color='white', row_even_color='lightgray'):
	return ceil(n / 2) * [row_odd_color, row_even_color]


def get_complementary_color(*args, r=0, g=0, b=0, alpha=1, scale=True):
	r, g, b, alpha = to_rgba(*args, r=r, g=g, b=b, alpha=alpha, scale=scale)
	return to_rgba_color([minimum(r, g, b) + maximum(r, g, b) - c for c in (r, g, b)], alpha=alpha,
	                     scale=False)


def get_random_named_color():
	return random.choice(NAMED_COLORS)


def get_RYG(brightness='8'):
	colors = ['#E.0.0.', '#E..00.', '#E.E.0.', '#.0E.00', '#0...0.']
	return [color.replace('.', brightness) for color in colors]


##################################################

def to_color(value, alpha=1, color_scale=RYG_SCALE, normalize=False, scale=True):
	'''Converts the specified value to a RGBA color using the specified alpha and color scale.'''
	if normalize:
		value = float(normal.cdf(value))
	c = color_scale(value)
	return to_rgba_color(c, alpha=alpha, scale=scale)


def to_rgba(*args, r=0, g=0, b=0, alpha=1, scale=True):
	if len(args) == 1:
		arg = args[0]
		if is_string(arg):
			if 'rgba' in arg:
				arg = to_float(extract(arg, '[0-9\.]+'))
				if len(arg) == 3:
					r, g, b = arg
				elif len(arg) == 4:
					r, g, b, alpha = arg
			else:
				r, g, b, alpha = mcolors.to_rgba(arg, alpha=alpha)
		elif is_collection(arg) or is_tuple(arg):
			if len(arg) == 3:
				r, g, b = arg
			elif len(arg) == 4:
				r, g, b, alpha = arg
	if scale and r <= 1 and g <= 1 and b <= 1:
		r = round_to_int(r * 255)
		g = round_to_int(g * 255)
		b = round_to_int(b * 255)
	return r, g, b, alpha


def to_rgba_color(*args, r=0, g=0, b=0, alpha=1, scale=True):
	r, g, b, alpha = to_rgba(*args, r=r, g=g, b=b, alpha=alpha, scale=scale)
	return 'rgba' + par(collist(r, g, b, alpha))
