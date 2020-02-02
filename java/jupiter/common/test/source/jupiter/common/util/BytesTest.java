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

public class BytesTest
		extends Test {

	public BytesTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of toBinary method, of class Bytes.
	 */
	public void testToBinary() {
		IO.test("• toBinary");

		final String text = "Hello world!";
		assertEquals(
				"010010000110010101101100011011000110111100100000011101110110111101110010011011000110010000100001",
				Bytes.toBinaryString(text.getBytes()));
	}

	/**
	 * Test of toOctal method, of class Bytes.
	 */
	public void testToOctal() {
		IO.test("• toOctal");

		final String text = "Hello world!";
		assertEquals("011001450154015401570040016701570162015401440041",
				Bytes.toOctalString(text.getBytes()));
	}

	/**
	 * Test of toHex method, of class Bytes.
	 */
	public void testToHex() {
		IO.test("• toHex");

		final String text = "Hello world!";
		assertEquals("48656C6C6F20776F726C6421", Bytes.toHexString(text.getBytes()));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of parseBinaryString method, of class Bytes.
	 */
	public void testParseBinaryString() {
		IO.test("• parseBinaryString");

		final String text = "Hello world!";
		assertEquals(text,
				new String(Bytes.parseBinaryString(Bytes.toBinaryString(text.getBytes()))));
	}

	/**
	 * Test of parseOctalString method, of class Bytes.
	 */
	public void testParseOctalString() {
		IO.test("• parseOctalString");

		final String text = "Hello world!";
		assertEquals(text,
				new String(Bytes.parseOctalString(Bytes.toOctalString(text.getBytes()))));
	}

	/**
	 * Test of parseHexString method, of class Bytes.
	 */
	public void testParseHexString() {
		IO.test("• parseHexString");

		final String text = "Hello world!";
		assertEquals(text, new String(Bytes.parseHexString(Bytes.toHexString(text.getBytes()))));
	}
}
