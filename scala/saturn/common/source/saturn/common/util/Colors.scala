/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package saturn.common.util

object Colors {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link RGB} color from the specified hexadecimal representative {@link String}.
	 * <p>
	 * @param hex the hexadecimal representative {@link String} of the {@link RGB} color to create
	 * <p>
	 * @return a {@link RGB} color from the specified hexadecimal representative {@link String}
	 */
	def toRGB(hex: String): RGB = hex.length match {
		case 3 => new RGB(Numbers.hexToDec(hex.charAt(0).toString() + hex.charAt(0)),
			Numbers.hexToDec(hex.charAt(1).toString() + hex.charAt(1)),
			Numbers.hexToDec(hex.charAt(2).toString() + hex.charAt(2)))
		case 6 => new RGB(Numbers.hexToDec(hex.substring(0, 2)),
			Numbers.hexToDec(hex.substring(2, 4)),
			Numbers.hexToDec(hex.substring(4, 6)))
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link RGB} color.
	 * <p>
	 * @param rc the red intensity
	 * @param gc the green intensity
	 * @param bc the blue intensity
	 */
	class RGB(rc: String, gc: String, bc: String) {

		/**
		 * The red intensity.
		 */
		var r: String = rc
		/**
		 * The green intensity.
		 */
		var g: String = gc
		/**
		 * The blue intensity.
		 */
		var b: String = bc

		/**
		 * Returns the representative {@link String} of the color.
		 * <p>
		 * @return the representative {@link String} of the color
		 */
		override def toString(): String = hex()

		/**
		 * Returns the decimal representative {@link String} of the color.
		 * <p>
		 * @return the decimal representative {@link String} of the color
		 */
		def dec(): String = "(" + r + "," + g + "," + b + ")"

		/**
		 * Returns the hexadecimal representative {@link String} of the color.
		 * <p>
		 * @return the hexadecimal representative {@link String} of the color
		 */
		def hex(): String = "#" + Numbers.decToHex(r) + Numbers.decToHex(g) + Numbers.decToHex(g)
	}
}
