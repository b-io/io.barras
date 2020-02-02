/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2020 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.util;

import static jupiter.common.io.IO.IO;

import jupiter.common.test.Test;

public class FormatsTest
		extends Test {

	public FormatsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of formatNumber method, of class Formats.
	 */
	public void testFormatNumber() {
		IO.test("• formatNumber");

		IO.test(Formats.formatNumber(0.0000001));
		assertEquals("1E-7", Formats.formatNumber(0.0000001));
		assertEquals("0.000001", Formats.formatNumber(0.000001));
		assertEquals("0.00001", Formats.formatNumber(0.00001));
		assertEquals("0.0001", Formats.formatNumber(0.0001));
		assertEquals("0.001", Formats.formatNumber(0.001));
		assertEquals("0.01", Formats.formatNumber(0.01));
		assertEquals("0.1", Formats.formatNumber(0.1));
		assertEquals("0", Formats.formatNumber(0.));

		assertEquals("1", Formats.formatNumber(1));
		assertEquals("12", Formats.formatNumber(12));
		assertEquals("123", Formats.formatNumber(123));
		assertEquals("1234", Formats.formatNumber(1234));
		assertEquals("12345", Formats.formatNumber(12345));
		assertEquals("123456", Formats.formatNumber(123456));
		assertEquals("1.234567E6", Formats.formatNumber(1234567));
	}
}
