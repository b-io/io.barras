#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - test the graphical utility functions
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

from ngui.chart import *
from ngui.image import *
from nutil.test import *

####################################################################################################
# GUI TEST CONSTANTS
####################################################################################################

__GUI_TEST_CONSTANTS______________________________ = ''

SIZE = 1000

####################################################################################################
# GUI TEST CLASSES
####################################################################################################

__GUI_TEST_CLASSES________________________________ = ''


class TestGui(Test):

	def test_chart(self):
		rgb_buffer = rotate_anti_90(generate_image(300, 200, 3))
		fig = px.imshow(rgb_buffer)

		self.assert_equals(sum(sum(sum(image_to_buffer(fig_to_jpg(fig))))), 367126580)
		self.assert_equals(sum(sum(sum(image_to_buffer(fig_to_png(fig))))), 524221335)
		self.assert_equals(sum(sum(sum(image_to_buffer(fig_to_webp(fig))))), 367221029)

		self.assert_equals(fig_to_jpg_html(fig)[-42:-10], 'FABRRRQAUUUUAFFFFABRRRQAUUUUAFFF')
		self.assert_equals(fig_to_png_html(fig)[-42:-10], 'AAAAAg0v8FMImIFolHcksAAAAASUVORK')
		self.assert_equals(fig_to_svg_html(fig)[-42:-10], 'g-xtitle"/><g class="g-ytitle"/>')
		self.assert_equals(fig_to_webp_html(fig)[-42:-10], '5z/7n/3H/uP/ef+8/95/5z/7n/3H/uv8')

	#####################

	def test_image(self):
		rgb = to_rgb(200, 100, 0, scale=False)
		hsv = rgb_to_hsv(rgb, scale=False)  # (0.083, 1.000, 0.784)
		self.assert_equals(hsv_to_rgb(hsv), rgb)

		self.assert_equals(get_complementary_color(rgb, scale=True), (0, 100, 200))

		self.assert_equals(get_base_color_name(rgb), 'y')
		self.assert_equals(get_tableau_color_name(rgb), 'tab:orange')
		self.assert_equals(get_css4_color_name(rgb), 'chocolate')
		self.assert_equals(get_xkcd_color_name(rgb), 'xkcd:browny orange')

		self.assert_equals(get_base_color_name(hsv, is_hsv=True), 'y')
		self.assert_equals(get_tableau_color_name(hsv, is_hsv=True), 'tab:olive')
		self.assert_equals(get_css4_color_name(hsv, is_hsv=True), 'darkgoldenrod')
		self.assert_equals(get_xkcd_color_name(hsv, is_hsv=True), 'xkcd:browny orange')

		self.assert_equals(format_rgb_color(rgb, alpha=None), 'rgb(200,100,0)')
		self.assert_equals(format_rgb_color(rgb), 'rgba(200,100,0,1)')

		self.assert_equals(format_hsv_color(hsv, alpha=None), 'hsv(30,100%,78%)')
		self.assert_equals(format_hsv_color(hsv), 'hsva(30,100%,78%,1)')

		self.assert_equals(format_rgb_color(map_to_color(100, normalize=True)),
		                   'rgba(0,104,55,1.0)')

		rgb_buffer = generate_image(300, 200, 3)
		hsv_buffer = cv2.cvtColor(rgb_buffer, cv2.COLOR_RGB2HSV)

		self.assert_equals(hsv_to_rgb(hsv_buffer, scale=False)[0],
		                   to_rgb(rgb_buffer, scale=False)[0], precision=1)
		self.assert_equals(rgb_to_hsv(rgb_buffer, scale=False)[0],
		                   to_hsv(hsv_buffer, scale=False)[0], precision=1)

		self.assert_equals(image_to_buffer(buffer_to_image(rgb_buffer, BMP_FORMAT))[0],
		                   rgb_buffer[0])

		self.assert_equals(buffer_to_html(rgb_buffer, BMP_FORMAT, rotate=True)[-42:-10],
		                   'eGSbtiKk/I3KxKINurU+/j0p12l32DAO')
		self.assert_equals(buffer_to_html(rgb_buffer, JPEG_FORMAT, rotate=True)[-42:-10],
		                   'ucPaXnVi+ZyX8GapwfuShaXLJ8zja97W')
		self.assert_equals(buffer_to_html(rgb_buffer, PNG_FORMAT, rotate=True)[-42:-10],
		                   'q21NUEUdmYyUW1RCNWQjkhAAAAAElFTk')
		self.assert_equals(buffer_to_html(rgb_buffer, TIFF_FORMAT, rotate=True)[-42:-10],
		                   'B4CAMArTMDAO9eAwA8igMAc7UDAAEAAQ')

		self.assert_equals(evaluate_colorfulness(rgb_buffer), 112.40, precision=2)
		self.assert_equals(evaluate_blurriness(rgb_buffer), 108379.74, precision=2)
		self.assert_equals(evaluate_brightness(rgb_buffer), 190.56, precision=2)

		# show_image(rgb_buffer)

	##############################################

	def evaluate_colorfulness(self, image):
		t = timeit.timeit(stmt=lambda: evaluate_colorfulness(image), number=TEST_COUNT)
		test('Evaluate the colorfulness of an image', 10 * TEST_COUNT, 'times in', round(t), '[s]')

	def evaluate_brightness(self, image):
		t = timeit.timeit(stmt=lambda: evaluate_brightness(image), number=TEST_COUNT)
		test('Evaluate the brightness of an image', 10 * TEST_COUNT, 'times in', round(t), '[s]')

	def evaluate_blurriness(self, image):
		t = timeit.timeit(stmt=lambda: evaluate_blurriness(image), number=TEST_COUNT)
		test('Evaluate the blurriness of an image', 10 * TEST_COUNT, 'times in', round(t), '[s]')


####################################################################################################
# GUI TEST MAIN
####################################################################################################

__GUI_TEST_MAIN___________________________________ = ''


def main():
	'''Tests the graphical utility library.'''
	unittest.main()


if __name__ == '__main__':
	main()
