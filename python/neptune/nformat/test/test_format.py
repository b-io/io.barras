#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the formatting utilities.
########################################################################################################################

import logging
import unittest

from nformat.color import *
from nformat.image import *
from nutil.io.logging import configure_logging
from nutil.test.unittest import Test
from nutil.test.util import TEST_COUNT, timed

## FORMAT TEST CASES #####################################################################

__FORMAT_TEST_CASES_________________________________________ = ""


class TestFormat(Test):

    def test_color(self):
        rgb = to_rgb(200, 100, 0, scale=False)
        hsv = rgb_to_hsv(rgb, scale=False)  # (0.083, 1.000, 0.784)

        self.assert_equals(hsv_to_rgb(hsv), rgb)

        self.assert_equals(get_complementary_color(rgb, scale=True), (0, 100, 200))

        self.assert_equals(get_base_color_name(rgb), "y")
        self.assert_equals(get_tableau_color_name(rgb), "tab:orange")
        self.assert_equals(get_css4_color_name(rgb), "chocolate")
        self.assert_equals(get_xkcd_color_name(rgb), "xkcd:browny orange")

        self.assert_equals(get_base_color_name(hsv, is_hsv=True), "y")
        self.assert_equals(get_tableau_color_name(hsv, is_hsv=True), "tab:olive")
        self.assert_equals(get_css4_color_name(hsv, is_hsv=True), "darkgoldenrod")
        self.assert_equals(get_xkcd_color_name(hsv, is_hsv=True), "xkcd:browny orange")

        self.assert_equals(format_rgb_color(rgb, alpha=None), "rgb(200,100,0)")
        self.assert_equals(format_rgb_color(rgb), "rgba(200,100,0,1)")

        self.assert_equals(format_hsv_color(hsv, alpha=None), "hsv(30,100%,78%)")
        self.assert_equals(format_hsv_color(hsv), "hsva(30,100%,78%,1)")

        self.assert_equals(format_rgb_color(to_color(100, normalize=True)), "rgba(0,104,55,1.0)")

        rgb_buffer = generate_image(30, 20, 3)
        hsv_buffer = cv2.cvtColor(rgb_buffer, cv2.COLOR_RGB2HSV)

        self.assert_equals(hsv_to_rgb(hsv_buffer, scale=False)[0], to_rgb(rgb_buffer, scale=False)[0], precision=1)
        self.assert_equals(rgb_to_hsv(rgb_buffer, scale=False)[0], to_hsv(hsv_buffer, scale=False)[0], precision=1)

    ########################################################

    def test_image(self):
        rgb_buffer = generate_image(30, 20, 3)
        hsv_buffer = cv2.cvtColor(rgb_buffer, cv2.COLOR_RGB2HSV)

        self.assert_equals(hsv_to_rgb(hsv_buffer, scale=False)[0], to_rgb(rgb_buffer, scale=False)[0], precision=1)
        self.assert_equals(rgb_to_hsv(rgb_buffer, scale=False)[0], to_hsv(hsv_buffer, scale=False)[0], precision=1)

        self.assert_equals(image_to_buffer(buffer_to_image(rgb_buffer, FileType.BMP))[0], rgb_buffer[0])

        self.assert_equals(
            buffer_to_html(rgb_buffer, FileType.BMP, rotate=True)[-42:-10],
            "xEJ4iiU0ZPCGSH/a1FzfuO2429RXH9Lv",
        )
        self.assert_equals(
            buffer_to_html(rgb_buffer, FileType.JPEG, rotate=True)[-42:-10],
            "uKnXTlZtcznhXUk5W3cqn7yTe8/ed5an",
        )
        self.assert_equals(
            buffer_to_html(rgb_buffer, FileType.PNG, rotate=True)[-42:-10],
            "IQmQtPzgfaQYm2KtKn/QAAAABJRU5Erk",
        )
        self.assert_equals(
            buffer_to_html(rgb_buffer, FileType.TIFF, rotate=True)[-42:-10],
            "EDAAMAAACyCQAAAAAAAAgACAAIAAEAAQ",
        )

        self.assert_equals(evaluate_colorfulness(rgb_buffer), 114.33, precision=2)
        self.assert_equals(evaluate_blurriness(rgb_buffer), 106115.40, precision=2)
        self.assert_equals(evaluate_brightness(rgb_buffer), 191.37, precision=2)

    def evaluate_colorfulness(self, image):
        t = timed(stmt=lambda: evaluate_colorfulness(image), number=TEST_COUNT)
        logging.info("Evaluate the colorfulness of an image", 10 * TEST_COUNT, "times in", round(t), "[s]")

    def evaluate_brightness(self, image):
        t = timed(stmt=lambda: evaluate_brightness(image), number=TEST_COUNT)
        logging.info("Evaluate the brightness of an image", 10 * TEST_COUNT, "times in", round(t), "[s]")

    def evaluate_blurriness(self, image):
        t = timed(stmt=lambda: evaluate_blurriness(image), number=TEST_COUNT)
        logging.info("Evaluate the blurriness of an image", 10 * TEST_COUNT, "times in", round(t), "[s]")


## FORMAT TEST MAIN ######################################################################

__FORMAT_TEST_MAIN__________________________________________ = ""


def main() -> None:
    """Tests the graphical user interface (FORMAT) utilities."""
    configure_logging(level=logging.DEBUG)
    unittest.main()


if __name__ == "__main__":
    main()
