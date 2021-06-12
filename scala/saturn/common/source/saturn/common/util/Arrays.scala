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

object Arrays {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the representative {@link String} of the specified {@link Array} joined with the
	 * specified delimiter and mapped with the specified element converter.
	 * <p>
	 * @tparam T        the type of the {@link Array}
	 * @param a         the {@link Array} of type {@code T} to print
	 * @param delimiter the delimiting {@link String}
	 * @param f         the element converter (from type {@code T} to {@link String})
	 * <p>
	 * @return the representative {@link String} of the specified {@link Array} joined with the
	 *         specified delimiter and mapped with the specified element converter
	 */
	def toString[T](a: Array[T], delimiter: String, f: T => String): String = {
		if (a == null) return Strings.NULL
		return a.map(f).mkString(delimiter)
	}
}
