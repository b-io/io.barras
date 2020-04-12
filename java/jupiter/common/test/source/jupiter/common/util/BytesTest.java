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

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Test;

public class BytesTest
		extends Test {

	public BytesTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Bytes#toBinary}.
	 */
	public void testToBinary() {
		IO.test(BULLET, " toBinary");

		final String text = "Hello world!";
		assertEquals(
				"010010000110010101101100011011000110111100100000011101110110111101110010011011000110010000100001",
				Bytes.toBinaryString(text.getBytes()));
	}

	/**
	 * Tests {@link Bytes#toOctal}.
	 */
	public void testToOctal() {
		IO.test(BULLET, " toOctal");

		final String text = "Hello world!";
		assertEquals("011001450154015401570040016701570162015401440041",
				Bytes.toOctalString(text.getBytes()));
	}

	/**
	 * Tests {@link Bytes#toHex}.
	 */
	public void testToHex() {
		IO.test(BULLET, " toHex");

		final String text = "Hello world!";
		assertEquals("48656C6C6F20776F726C6421", Bytes.toHexString(text.getBytes()));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Bytes#parseBinaryString}.
	 */
	public void testParseBinaryString() {
		IO.test(BULLET, " parseBinaryString");

		final String text = "Hello world!";
		assertEquals(text,
				new String(Bytes.parseBinaryString(Bytes.toBinaryString(text.getBytes()))));
	}

	/**
	 * Tests {@link Bytes#parseOctalString}.
	 */
	public void testParseOctalString() {
		IO.test(BULLET, " parseOctalString");

		final String text = "Hello world!";
		assertEquals(text,
				new String(Bytes.parseOctalString(Bytes.toOctalString(text.getBytes()))));
	}

	/**
	 * Tests {@link Bytes#parseHexString}.
	 */
	public void testParseHexString() {
		IO.test(BULLET, " parseHexString");

		final String text = "Hello world!";
		assertEquals(text, new String(Bytes.parseHexString(Bytes.toHexString(text.getBytes()))));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Bytes#clone}.
	 */
	public void testClone() {
		IO.test(BULLET, " clone");

		final String text = "Hello world!";
		final byte[] array = text.getBytes();
		final byte[] clone = array.clone();
		Bytes.shuffle(array);
		assertEquals("48656C6C6F20776F726C6421", Bytes.toHexString(clone));
	}
}
