/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
import static jupiter.common.util.Characters.NUMERICAL_DIGITS;

import jupiter.common.test.CharacterArguments;
import jupiter.common.test.Test;

public class CharactersTest
		extends Test {

	public CharactersTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Characters#toArray}.
	 */
	public void testToArray() {
		IO.test(BULLET, "toArray");

		assertTrue(Arrays.equals(new Character[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'},
				Characters.toArray(NUMERICAL_DIGITS)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Characters#clone}.
	 */
	public void testClone() {
		IO.test(BULLET, "clone");

		final String text = "Hello world!";
		final char[] array = CharacterArguments.requireNonEmpty(text.toCharArray());
		final char[] clone = CharacterArguments.requireNonEmpty(array.clone());
		Characters.shuffle(array);
		assertNotSame(array, clone);
	}
}
