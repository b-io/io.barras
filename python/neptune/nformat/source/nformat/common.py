#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common formatting utilities.
########################################################################################################################

from __future__ import annotations

from nformat.color import TRANSPARENT
from nutil.struct.util import get_iterator


__COMMON_FORMAT_CONSTANTS_________________________________________________________________ = ""


### DEFAULTS ###############################################

# The default scale
DEFAULT_SCALE = 1  # the higher, the better quality

# The default width
DEFAULT_WIDTH = 660

# The default height
DEFAULT_HEIGHT = 933

# The default margin (left, right, bottom and top) defined by the ratio to the width or height
DEFAULT_MARGIN = dict(l=0, r=0, b=0, t=0)
DEFAULT_MARGIN_WITH_TITLE = dict(l=0.05, r=0.05, b=0.05, t=0.05)

##############################

# The default font size
DEFAULT_FONT_SIZE = 12

# The default line width
DEFAULT_LINE_WIDTH = 2

# The default marker size
DEFAULT_MARKER_SIZE = 4

##############################

# The default colors
DEFAULT_COLORS = [
    "#1F77B4",  # muted blue
    "#FF7F0E",  # safety orange
    "#2CA02C",  # cooked asparagus green
    "#D62728",  # brick red
    "#9467BD",  # muted purple
    "#8C564B",  # chestnut brown
    "#E377C2",  # raspberry yogurt pink
    "#7F7F7F",  # middle gray
    "#BCBD22",  # curry yellow-green
    "#17BECF",  # blue-teal
]

# The default colors `Iterator`
DEFAULT_COLORS_ITERATOR = get_iterator(DEFAULT_COLORS, cycle=True)

# The default background color
DEFAULT_BG_COLOR = TRANSPARENT
