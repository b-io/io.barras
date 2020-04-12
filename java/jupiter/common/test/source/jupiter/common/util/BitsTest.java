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

public class BitsTest
		extends Test {

	public BitsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Bits#rotateLeft}.
	 */
	public void testRotateLeft_long() {
		IO.test(BULLET, " rotateLeft");

		assertEquals(0L, Bits.rotateLeft(0L));
	}

	/**
	 * Tests {@link Bits#rotateLeft}.
	 */
	public void testRotateLeft_long_long() {
		IO.test(BULLET, " rotateLeft");

		assertEquals(0L, Bits.rotateLeft(0L, 0L));
	}

	/**
	 * Tests {@link Bits#rotateLeft}.
	 */
	public void testRotateLeft_int() {
		IO.test(BULLET, " rotateLeft");

		assertEquals(0, Bits.rotateLeft(0));
	}

	/**
	 * Tests {@link Bits#rotateLeft}.
	 */
	public void testRotateLeft_int_int() {
		IO.test(BULLET, " rotateLeft");

		assertEquals(0, Bits.rotateLeft(0, 0));
	}

	/**
	 * Tests {@link Bits#rotateRight}.
	 */
	public void testRotateRight_long() {
		IO.test(BULLET, " rotateRight");

		assertEquals(0L, Bits.rotateRight(0L));
	}

	/**
	 * Tests {@link Bits#rotateRight}.
	 */
	public void testRotateRight_long_long() {
		IO.test(BULLET, " rotateRight");

		assertEquals(0L, Bits.rotateRight(0L, 0L));
	}

	/**
	 * Tests {@link Bits#rotateRight}.
	 */
	public void testRotateRight_int() {
		IO.test(BULLET, " rotateRight");

		assertEquals(0, Bits.rotateRight(0));
	}

	/**
	 * Tests {@link Bits#rotateRight}.
	 */
	public void testRotateRight_int_int() {
		IO.test(BULLET, " rotateRight");

		assertEquals(0, Bits.rotateRight(0, 0));
	}
}
