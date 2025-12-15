#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the graphical user interface (GUI) utilities.
########################################################################################################################

import logging
import unittest

from ngui.charts import *
from nutil.io.logging import configure_logging
from nutil.test.unittest import Test

## GUI TEST CONSTANTS ####################################################################

__GUI_TEST_CONSTANTS________________________________________ = ""

SIZE = 1000


## GUI TEST CASES ########################################################################

__GUI_TEST_CASES____________________________________________ = ""


class TestGui(Test):

    def test_chart(self):
        width, height, channels = 300, 200, 3
        rgb_buffer = rotate_anti_90(generate_image(width, height, channels))
        #  show_image(rgb_buffer)

        fig = px.imshow(rgb_buffer)
        margin = 0.1
        # Check the JPG conversion
        jpg_image = image_to_buffer(fig_to_jpg(fig, width=width, height=height, margin=margin))
        #  show_image(jpg_image)
        self.assert_equals(sum(sum(sum(jpg_image))), 32352698)
        # Check the PNG conversion
        png_image = image_to_buffer(fig_to_png(fig, width=width, height=height, margin=margin))
        #  show_image(png_image)
        self.assert_equals(sum(sum(sum(png_image))), 47687972)
        # Check the WebP conversion
        webp_image = image_to_buffer(fig_to_webp(fig, width=width, height=height, margin=margin))
        #  show_image(webp_image)
        self.assert_equals(sum(sum(sum(webp_image))), 32368558)

        self.assert_equals(fig_to_jpg_html(fig)[-42:-10], "oAKKKKACiiigAooooAKKKKACiiigAooo")
        self.assert_equals(fig_to_png_html(fig)[-42:-10], "4QAAAAEOn/AKmlt7PIJQ5AAAAAAElFTk")
        self.assert_equals(fig_to_svg_html(fig)[-42:-10], 'g-xtitle"/><g class="g-ytitle"/>')
        self.assert_equals(fig_to_webp_html(fig)[-42:-10], "/Uf+o/9Z/6T/2n/lP/qf/Uf+o/9Z/6T/")


## GUI TEST MAIN #########################################################################

__GUI_TEST_MAIN_____________________________________________ = ""


def main() -> None:
    """Tests the graphical user interface (GUI) utilities."""
    configure_logging(level=logging.DEBUG)
    unittest.main()


if __name__ == "__main__":
    main()
