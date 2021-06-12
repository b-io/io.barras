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

object Bytes {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	val SIZE: Int = 8


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Array} of {@link Byte} of the specified hexadecimal representative
	 * {@link String}.
	 * <p>
	 * @return the {@link Array} of {@link Byte} of the specified hexadecimal representative
	 *         {@link String}
	 */
	def valueOf(hex: String): Array[Byte] = {
		val length: Int = hex.length()
		val bytes = new Array[Byte](length >> 1)
		for (i <- 0 until length by 2) {
			bytes(i >> 1) = ((Character.digit(hex.charAt(i), 16) << 4)
					+ Character.digit(hex.charAt(i + 1), 16)).toByte
		}
		return bytes
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the hexadecimal representative {@link String} of the specified {@link Byte}.
	 * <p>
	 * @return the hexadecimal representative {@link String} of the specified {@link Byte}
	 */
	def toString(byte: Byte): String = "%02X".format(byte & 0xff)

	/**
	 * Returns the hexadecimal representative {@link String} of the specified {@link Array} of
	 * {@link Byte}.
	 * <p>
	 * @return the hexadecimal representative {@link String} of the specified {@link Array} of
	 *         {@link Byte}
	 */
	def toString(bytes: Array[Byte]): String = Arrays.toString[Byte](bytes, "", toString)
}
