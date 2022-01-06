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

import jupiter.common.test.Test;

public class IntegersTest
		extends Test {

	public IntegersTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Integers#remove}.
	 */
	public void testRemove() {
		IO.test(BULLET, "remove");

		final int[] array = Integers.createSequence(4);
		assertTrue(Integers.equals(new int[] {1, 2, 3},
				Integers.remove(array, 0)));
		assertTrue(Integers.equals(new int[] {0, 2, 3},
				Integers.remove(array, 1)));
		assertTrue(Integers.equals(new int[] {0, 1, 3},
				Integers.remove(array, 2)));
		assertTrue(Integers.equals(new int[] {0, 1, 2},
				Integers.remove(array, 3)));
	}

	/**
	 * Tests {@link Integers#removeAll}.
	 */
	public void testRemoveAll() {
		IO.test(BULLET, "removeAll");

		final int[] array = new int[] {1, 2, 2, 3, 3, 3};
		assertTrue(Integers.equals(new int[] {2, 2, 3, 3, 3},
				Integers.removeAll(array, 1)));
		assertTrue(Integers.equals(new int[] {1, 3, 3, 3},
				Integers.removeAll(array, 2)));
		assertTrue(Integers.equals(new int[] {1, 2, 2},
				Integers.removeAll(array, 3)));
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link Integers#reverse}.
	 */
	public void testReverse() {
		IO.test(BULLET, "reverse");

		int[] array = Integers.EMPTY_PRIMITIVE_ARRAY;
		Integers.reverse(array);
		assertTrue(Integers.equals(Integers.EMPTY_PRIMITIVE_ARRAY, array));
		array = Integers.createSequence(4);
		Integers.reverse(array);
		assertTrue(Integers.equals(new int[] {3, 2, 1, 0}, array));
		array = Integers.createSequence(5);
		Integers.reverse(array);
		assertTrue(Integers.equals(new int[] {4, 3, 2, 1, 0}, array));
	}
}
