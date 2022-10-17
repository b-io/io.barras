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

		np.random.seed(0)
		rgb_image = generate_image(300, 200, 3)
		hsv_image = cv2.cvtColor(rgb_image, cv2.COLOR_BGR2HSV)
		# show_image(image)

		self.assert_equals(hsv_to_rgb(hsv_image, scale=False)[0],
		                   to_rgb(rgb_image, scale=False)[0], precision=1)
		self.assert_equals(rgb_to_hsv(rgb_image, scale=False)[0],
		                   to_hsv(hsv_image, scale=False)[0], precision=1)

		self.assert_equals(evaluate_colorfulness(rgb_image), 112.47474098389532)
		self.assert_equals(evaluate_blurriness(rgb_image), 108648.5407215791)
		self.assert_equals(evaluate_brightness(rgb_image), 190.55361666666667)

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
