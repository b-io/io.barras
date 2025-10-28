#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains formatting utility functions for colors
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

import cv2
import matplotlib.cm as mcm
import matplotlib.colors as mcolors

from ngui.common import DEFAULT_COLORS
from nmath.stats import normal
from nutil.math import *
from nutil.scalar.string import *

## COLOR CONSTANTS #######################################################################

__COLOR_CONSTANTS___________________________________________ = ""

TRANSPARENT = (0, 0, 0, 0)

############################################################

# The default colors `Iterator`
DEFAULT_COLORS_ITERATOR = get_iterator(DEFAULT_COLORS, cycle=True)

# The default background color
DEFAULT_BG_COLOR = TRANSPARENT

############################################################


def to_rgb(*args, r=0, g=0, b=0, alpha=None, scale=None):
    color = forward_element(*args)
    if is_collection(color) or is_tuple(color):
        if is_array(color):
            r, g, b = cv2.split(to_float(color))
        elif len(color) == 3:
            r, g, b = color
        elif len(color) == 4:
            r, g, b, alpha = color
    elif is_string(color):
        if "rgb" in color:
            color = to_float(extract(color, "[0-9\.]+"))
            if len(color) == 3:
                r, g, b = color
            elif len(color) == 4:
                r, g, b, alpha = color
        else:
            if not is_null(alpha):
                r, g, b, alpha = mcolors.to_rgba(color, alpha=alpha)
            else:
                r, g, b = mcolors.to_rgb(color)
    if not is_null(scale):
        r, g, b = unscale_color(r, g, b)
        if scale:
            r, g, b = scale_color(r, g, b)
    if not is_null(alpha):
        return r, g, b, alpha
    return r, g, b


##############################


def to_hsv(*args, h=0, s=0, v=0, alpha=None, scale=None):
    color = forward_element(*args)
    if is_collection(color) or is_tuple(color):
        if is_array(color):
            h, s, v = cv2.split(to_float(color))
        elif len(color) == 3:
            h, s, v = color
        elif len(color) == 4:
            h, s, v, alpha = color
    if not is_null(scale):
        h, s, v = unscale_color(h, s, v)
        if scale:
            h, s, v = scale_color(h, s, v)
    if not is_null(alpha):
        return h, s, v, alpha
    return h, s, v


##############################


def rgb_to_hsv(*args, r=0, g=0, b=0, alpha=None, scale=None):
    color = forward_element(*args)
    if is_array(color):
        image = cv2.cvtColor(color, cv2.COLOR_RGB2HSV)
        h, s, v = cv2.split(to_float(image))
    else:
        r, g, b = to_rgb(*args, r=r, g=g, b=b)
        h, s, v = mcolors.rgb_to_hsv((r, g, b))
    if not is_null(scale):
        h, s, v = unscale_color(h, s, v)
        if scale:
            h, s, v = scale_color(h, s, v)
    if not is_null(alpha):
        return h, s, v, alpha
    return h, s, v


def hsv_to_rgb(*args, h=0, s=0, v=0, alpha=None, scale=None):
    color = forward_element(*args)
    if is_array(color):
        image = cv2.cvtColor(color, cv2.COLOR_HSV2RGB)
        r, g, b = cv2.split(to_float(image))
    else:
        h, s, v = to_hsv(*args, h=h, s=s, v=v)
        r, g, b = mcolors.hsv_to_rgb((h, s, v))
    if not is_null(scale):
        r, g, b = unscale_color(r, g, b)
        if scale:
            r, g, b = scale_color(r, g, b)
    if not is_null(alpha):
        return r, g, b, alpha
    return r, g, b


##############################


def is_scaled_color(x, y, z) -> bool:
    """Returns whether the specified RGB color components are scaled (contain values greater than 1)."""
    return maximum(maximum(x)) > 1 or maximum(maximum(y)) > 1 or maximum(maximum(z)) > 1


def is_unscaled_color(x, y, z) -> bool:
    """Returns whether the specified RGB color components are unscaled (all values are less than or equal to 1)."""
    return maximum(maximum(x)) <= 1 and maximum(maximum(y)) <= 1 and maximum(maximum(z)) <= 1


##############################


def scale_color(x, y, z):
    if is_unscaled_color(x, y, z):
        x = round_to_int(x * 255)
        y = round_to_int(y * 255)
        z = round_to_int(z * 255)
    return x, y, z


def unscale_color(x, y, z):
    if is_scaled_color(x, y, z):
        x /= 255
        y /= 255
        z /= 255
    return x, y, z


##############################

BASE_COLOR_NAMES = [name for name, _ in mcolors.BASE_COLORS.items()]
BASE_COLOR_RGB_CODES = to_array([to_rgb(color) for _, color in mcolors.BASE_COLORS.items()])
BASE_COLOR_HSV_CODES = to_array([rgb_to_hsv(color) for _, color in mcolors.BASE_COLORS.items()])

TABLEAU_COLOR_NAMES = [name for name, _ in mcolors.TABLEAU_COLORS.items()]
TABLEAU_COLOR_RGB_CODES = to_array([to_rgb(color) for _, color in mcolors.TABLEAU_COLORS.items()])
TABLEAU_COLOR_HSV_CODES = to_array(
    [rgb_to_hsv(color) for _, color in mcolors.TABLEAU_COLORS.items()]
)

CSS4_COLOR_NAMES = [name for name, _ in mcolors.CSS4_COLORS.items()]
CSS4_COLOR_RGB_CODES = to_array([to_rgb(color) for _, color in mcolors.CSS4_COLORS.items()])
CSS4_COLOR_HSV_CODES = to_array([rgb_to_hsv(color) for _, color in mcolors.CSS4_COLORS.items()])

XKCD_COLOR_NAMES = [name for name, _ in mcolors.XKCD_COLORS.items()]
XKCD_COLOR_RGB_CODES = to_array([to_rgb(color) for _, color in mcolors.XKCD_COLORS.items()])
XKCD_COLOR_HSV_CODES = to_array([rgb_to_hsv(color) for _, color in mcolors.XKCD_COLORS.items()])

RAINBOW_SCALE = mcm.get_cmap(name="rainbow")
RYG_SCALE = mcm.get_cmap(name="RdYlGn")


## COLOR FUNCTIONS #######################################################################

__COLOR_____________________________________________________ = ""


def get_alternate_colors(n, row_odd_color="white", row_even_color="lightgray"):
    return ceil(n / 2) * [row_odd_color, row_even_color]


def get_complementary_color(*args, r=0, g=0, b=0, scale=None):
    r, g, b = to_rgb(*args, r=r, g=g, b=b, scale=scale)
    return tuple(minimum(r, g, b) + maximum(r, g, b) - c for c in (r, g, b))


def get_RYG(brightness="8"):
    colors = ("#E.0.0.", "#E..00.", "#E.E.0.", "#.0E.00", "#0...0.")
    return [color.replace(".", brightness) for color in colors]


##############################


def get_random_base_color_name():
    return random.choice(BASE_COLOR_NAMES)


def get_random_tableau_color_name():
    return random.choice(TABLEAU_COLOR_NAMES)


def get_random_css4_color_name():
    return random.choice(CSS4_COLOR_NAMES)


def get_random_xkcd_color_name():
    return random.choice(XKCD_COLOR_NAMES)


##############################


def get_color_name(code, names, codes):
    return names[min_distance_index(code, codes)]


def get_base_color_name(code, is_hsv=False):
    return get_color_name(
        unscale_color(*code),
        BASE_COLOR_NAMES,
        BASE_COLOR_HSV_CODES if is_hsv else BASE_COLOR_RGB_CODES,
    )


def get_tableau_color_name(code, is_hsv=False):
    return get_color_name(
        unscale_color(*code),
        TABLEAU_COLOR_NAMES,
        TABLEAU_COLOR_HSV_CODES if is_hsv else TABLEAU_COLOR_RGB_CODES,
    )


def get_css4_color_name(code, is_hsv=False):
    return get_color_name(
        unscale_color(*code),
        CSS4_COLOR_NAMES,
        CSS4_COLOR_HSV_CODES if is_hsv else CSS4_COLOR_RGB_CODES,
    )


def get_xkcd_color_name(code, is_hsv=False):
    return get_color_name(
        unscale_color(*code),
        XKCD_COLOR_NAMES,
        XKCD_COLOR_HSV_CODES if is_hsv else XKCD_COLOR_RGB_CODES,
    )


############################################################


def format_rgb_color(*args, r=0, g=0, b=0, alpha=1):
    """Formats the specified RGB color (with alpha if it is not null)."""
    prefix = "rgba" if not is_null(alpha) else "rgb"
    return prefix + par(collist(*to_rgb(*args, r=r, g=g, b=b, alpha=alpha, scale=True)))


def format_hsv_color(*args, h=0, s=0, v=0, alpha=1):
    """Formats the specified HSV color (with alpha if it is not null)."""
    prefix = "hsva" if not is_null(alpha) else "hsv"
    h, s, v = to_hsv(*args, h=h, s=s, v=v, scale=False)
    hsv = (round_to_int(h * 360), format_percent(s, 0), format_percent(v, 0))
    return prefix + par(collist(*hsv, alpha) if not is_null(alpha) else collist(*hsv))


##############################


def map_to_color(value, color_scale=RYG_SCALE, normalize=False):
    """Formats the specified value to an RGBA color using the specified alpha and color scale."""
    if normalize:
        value = float(normal.cdf(value))
    return color_scale(value)
