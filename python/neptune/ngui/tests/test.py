#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - tests the graphical utility functions
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

import unittest

from ngui.charts import *
from ngui.image import *
from ntest.common import *
from ntest.unit.unittest import Test

## GUI TEST CONSTANTS ####################################################################

__GUI_TEST_CONSTANTS________________________________________ = ""

SIZE = 1000


## GUI TEST CLASSES ######################################################################

__GUI_TEST_CLASSES__________________________________________ = ""


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

    ##########################

    def test_image(self):
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

        self.assert_equals(
            format_rgb_color(map_to_color(100, normalize=True)), "rgba(0,104,55,1.0)"
        )

        rgb_buffer = generate_image(30, 20, 3)
        hsv_buffer = cv2.cvtColor(rgb_buffer, cv2.COLOR_RGB2HSV)

        self.assert_equals(
            hsv_to_rgb(hsv_buffer, scale=False)[0], to_rgb(rgb_buffer, scale=False)[0], precision=1
        )
        self.assert_equals(
            rgb_to_hsv(rgb_buffer, scale=False)[0], to_hsv(hsv_buffer, scale=False)[0], precision=1
        )

        self.assert_equals(
            image_to_buffer(buffer_to_image(rgb_buffer, FileType.BMP))[0], rgb_buffer[0]
        )

        self.assert_equals(
            buffer_to_html(rgb_buffer, FileType.BMP, rotate=True)[-42:-10],
            "KGD4EM9jXS31CxQJywbVdHZpDT0bEjPC",
        )
        self.assert_equals(
            buffer_to_html(rgb_buffer, FileType.JPEG, rotate=True)[-42:-10],
            "p1pQi7R9nOdltzyxVeMp2/mlFKLlu4pJ",
        )
        self.assert_equals(
            buffer_to_html(rgb_buffer, FileType.PNG, rotate=True)[-42:-10],
            "bFgQj/MR9VQH3x/7viwQAAAABJRU5Erk",
        )
        self.assert_equals(
            buffer_to_html(rgb_buffer, FileType.TIFF, rotate=True)[-42:-10],
            "MBAwADAAAAtgkAAAAAAAAIAAgACAABAA",
        )

        self.assert_equals(evaluate_colorfulness(rgb_buffer), 113.85, precision=2)
        self.assert_equals(evaluate_blurriness(rgb_buffer), 113665.04, precision=2)
        self.assert_equals(evaluate_brightness(rgb_buffer), 189.17, precision=2)

    ########################################################

    def evaluate_colorfulness(self, image):
        t = timed(stmt=lambda: evaluate_colorfulness(image), number=TEST_COUNT)
        logging.info(
            "Evaluate the colorfulness of an image", 10 * TEST_COUNT, "times in", round(t), "[s]"
        )

    def evaluate_brightness(self, image):
        t = timed(stmt=lambda: evaluate_brightness(image), number=TEST_COUNT)
        logging.info(
            "Evaluate the brightness of an image", 10 * TEST_COUNT, "times in", round(t), "[s]"
        )

    def evaluate_blurriness(self, image):
        t = timed(stmt=lambda: evaluate_blurriness(image), number=TEST_COUNT)
        logging.info(
            "Evaluate the blurriness of an image", 10 * TEST_COUNT, "times in", round(t), "[s]"
        )


## GUI TEST MAIN #########################################################################

__GUI_TEST_MAIN_____________________________________________ = ""


def main():
    """Tests the graphical user interface utility library."""
    unittest.main()


if __name__ == "__main__":
    main()
