#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the graphical utilities.
########################################################################################################################

from __future__ import annotations

import logging
import unittest

import plotly.express as px

from nformat.image import *
from ngui import charts
from nutil.io.logging import configure_logging
from nutil.struct.util import sum
from nutil.test.unittest import Test

__GUI_TEST_CONSTANTS______________________________________________________________________ = ""


SIZE: int = 1000


__GUI_TEST_CASES__________________________________________________________________________ = ""


class TestGui(Test):

    def __init__(self, methodName="runTest"):
        super().__init__(methodName=methodName)

        charts.disable_default_rendering()

    def test_chart(self):
        width, height, channels = 300, 200, 3
        rgb_buffer = rotate_anti_90(generate_image(width, height, channels))
        #  show_image(rgb_buffer)

        fig = px.imshow(rgb_buffer)
        margin = 0.1
        # Check the JPG conversion
        jpg_image = image_to_buffer(charts.fig_to_jpg(fig, width=width, height=height, margin=margin))
        #  show_image(jpg_image)
        self.assert_equals(sum(sum(sum(jpg_image))), 32352698)
        # Check the PNG conversion
        png_image = image_to_buffer(charts.fig_to_png(fig, width=width, height=height, margin=margin))
        #  show_image(png_image)
        self.assert_equals(sum(sum(sum(png_image))), 47687972)
        # Check the WebP conversion
        webp_image = image_to_buffer(charts.fig_to_webp(fig, width=width, height=height, margin=margin))
        #  show_image(webp_image)
        self.assert_equals(sum(sum(sum(webp_image))), 32368558)

        self.assert_equals(charts.fig_to_jpg_html(fig)[-42:-10], "oAKKKKACiiigAooooAKKKKACiiigAooo")
        self.assert_equals(charts.fig_to_png_html(fig)[-42:-10], "4QAAAAEOn/AKmlt7PIJQ5AAAAAAElFTk")
        self.assert_equals(charts.fig_to_svg_html(fig)[-42:-10], 'g-xtitle"/><g class="g-ytitle"/>')
        self.assert_equals(charts.fig_to_webp_html(fig)[-42:-10], "/Uf+o/9Z/6T/2n/lP/qf/Uf+o/9Z/6T/")


__GUI_TEST_RUNNERS________________________________________________________________________ = ""


### MAIN ###################################################


def main() -> None:
    """Tests the graphical utilities."""
    configure_logging(level=logging.DEBUG)
    unittest.main()


if __name__ == "__main__":
    main()
