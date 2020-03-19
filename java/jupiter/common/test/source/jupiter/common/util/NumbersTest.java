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
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Test;

public class NumbersTest
		extends Test {

	public NumbersTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests toString method, of class Numbers.
	 */
	public void testToString() {
		IO.test(BULLET, " toString");

		IO.test(Numbers.toString(0.0000001));
		assertEquals("1E-9", Numbers.toString(0.000000001));
		assertEquals("0.00000001", Numbers.toString(0.00000001));
		assertEquals("0.0000001", Numbers.toString(0.0000001));
		assertEquals("0.000001", Numbers.toString(0.000001));
		assertEquals("0.00001", Numbers.toString(0.00001));
		assertEquals("0.0001", Numbers.toString(0.0001));
		assertEquals("0.001", Numbers.toString(0.001));
		assertEquals("0.01", Numbers.toString(0.01));
		assertEquals("0.1", Numbers.toString(0.1));
		assertEquals("0", Numbers.toString(0.));

		assertEquals("1", Numbers.toString(1));
		assertEquals("12", Numbers.toString(12));
		assertEquals("123", Numbers.toString(123));
		assertEquals("1234", Numbers.toString(1234));
		assertEquals("12345", Numbers.toString(12345));
		assertEquals("123456", Numbers.toString(123456));
		assertEquals("1234567", Numbers.toString(1234567));
		assertEquals("12345678", Numbers.toString(12345678));
		assertEquals("1.23456789E8", Numbers.toString(123456789));
	}
}
