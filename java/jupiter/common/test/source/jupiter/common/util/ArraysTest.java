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

public class ArraysTest
		extends Test {

	// • 1D
	protected static final String[] ARRAY = new String[] {"a", "b", "c", "d", "e", "f"};
	// • 2D
	protected static final String[][] ARRAY_2D = new String[][] {ARRAY, ARRAY};
	protected static final String[] FLAT_ARRAY_2D = Arrays.<String>merge(ARRAY, ARRAY);
	// • 3D
	protected static final String[][][] ARRAY_3D = new String[][][] {ARRAY_2D, ARRAY_2D};
	protected static final String[] FLAT_ARRAY_3D = Arrays.<String>merge(FLAT_ARRAY_2D,
			FLAT_ARRAY_2D);

	////////////////////////////////////////////////////////////////////////////////////////////////

	public ArraysTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of filterAll method, of class Arrays.
	 */
	public void testFilterAll() {
		IO.test(BULLET, " filterAll");

		assertEquals(
				Objects.hashCode(Arrays.<String>filterAll(ARRAY,
						new int[][] {new int[] {0, 0, 1, 2, 2, 3, 4, 5}})),
				Objects.hashCode(
						new String[][] {new String[] {"a", "a", "b", "c", "c", "d", "e", "f"}}));
	}

	/**
	 * Test of merge method, of class Arrays.
	 */
	public void testMerge() {
		IO.test(BULLET, " merge");

		assertEquals(Objects.hashCode(Arrays.<String>merge(new String[] {"a"}, ARRAY)),
				Objects.hashCode(new String[] {"a", "a", "b", "c", "d", "e", "f"}));
		assertEquals(Objects.hashCode(Arrays.<String>merge(ARRAY, ARRAY)),
				Objects.hashCode(FLAT_ARRAY_2D));
		assertEquals(Objects.hashCode(Arrays.<String>merge(ARRAY_2D)),
				Objects.hashCode(FLAT_ARRAY_2D));
	}

	/**
	 * Test of take method, of class Arrays.
	 */
	public void testTake() {
		IO.test(BULLET, " take");

		// • 1D
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY)), Objects.hashCode(ARRAY));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY, 4)),
				Objects.hashCode(new String[] {"e", "f"}));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY, 4, 2)),
				Objects.hashCode(new String[] {"e", "f"}));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY, 4, 10)),
				Objects.hashCode(new String[] {"e", "f"}));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY, 4, 0)),
				Objects.hashCode(Strings.EMPTY_ARRAY));

		// • 2D
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY_2D)),
				Objects.hashCode(FLAT_ARRAY_2D));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY_2D, 1)), Objects.hashCode(ARRAY));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY_2D, 1, 1)),
				Objects.hashCode(ARRAY));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY_2D, 1, 10)),
				Objects.hashCode(ARRAY));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY_2D, 1, 0)),
				Objects.hashCode(Strings.EMPTY_ARRAY));

		// • 3D
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY_3D)),
				Objects.hashCode(FLAT_ARRAY_3D));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY_3D, 1)),
				Objects.hashCode(FLAT_ARRAY_2D));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY_3D, 1, 1)),
				Objects.hashCode(FLAT_ARRAY_2D));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY_3D, 1, 10)),
				Objects.hashCode(FLAT_ARRAY_2D));
		assertEquals(Objects.hashCode(Arrays.<String>take(ARRAY_3D, 1, 0)),
				Objects.hashCode(Strings.EMPTY_ARRAY));
	}
}
