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
	 * Test of getElementClass method, of class Arrays.
	 */
	public void testGetElementClass() {
		IO.test(BULLET, " getElementClass");

		assertEquals(Object.class, Arrays.getElementClass());
		assertEquals(Number.class, Arrays.getElementClass(null, 1, 1.));
		assertEquals(String.class, Arrays.getElementClass(ARRAY));
		assertEquals(String[].class, Arrays.getElementClass(null, ARRAY));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of toArray method, of class Arrays.
	 */
	public void testToArray() {
		IO.test(BULLET, " toArray");

		assertEquals(Objects.hashCode(new String[] {}),
				Objects.hashCode(Arrays.toArray(new String[] {})));
		assertEquals(Objects.hashCode(new String[] {}),
				Objects.hashCode(Arrays.toArray(new String[][] {})));
		assertEquals(Objects.hashCode(new String[] {}),
				Objects.hashCode(Arrays.toArray(new String[][][] {})));

		assertEquals(Objects.hashCode(new String[] {"a", "b", "c"}),
				Objects.hashCode(Arrays.toArray(new String[] {"a", "b", "c"})));
		assertEquals(Objects.hashCode(new String[] {"a", "b", "c"}),
				Objects.hashCode(Arrays.toArray(new String[][] {{"a"}, {"b"}, {"c"}})));
		assertEquals(Objects.hashCode(new String[] {"a", "b", "c"}),
				Objects.hashCode(Arrays.toArray(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}})));
	}

	/**
	 * Test of toArray2D method, of class Arrays.
	 */
	public void testToArray2D() {
		IO.test(BULLET, " toArray2D");

		assertEquals(Objects.hashCode(new String[][] {}),
				Objects.hashCode(Arrays.toArray2D(new String[] {}, 0)));
		assertEquals(Objects.hashCode(new String[][] {}),
				Objects.hashCode(Arrays.toArray2D(new String[][] {})));
		assertEquals(Objects.hashCode(new String[][] {}),
				Objects.hashCode(Arrays.toArray2D(new String[][][] {})));

		assertEquals(Objects.hashCode(new String[][] {{"a"}, {"b"}, {"c"}}),
				Objects.hashCode(Arrays.toArray2D(new String[] {"a", "b", "c"}, 3)));
		assertEquals(Objects.hashCode(new String[][] {{"a"}, {"b"}, {"c"}}),
				Objects.hashCode(Arrays.toArray2D(new String[][] {{"a"}, {"b"}, {"c"}})));
		assertEquals(Objects.hashCode(new String[][] {{"a"}, {"b"}, {"c"}}),
				Objects.hashCode(Arrays.toArray2D(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}})));
	}

	/**
	 * Test of toArray3D method, of class Arrays.
	 */
	public void testToArray3D() {
		IO.test(BULLET, " toArray3D");

		assertEquals(Objects.hashCode(new String[][][] {}),
				Objects.hashCode(Arrays.toArray3D(new String[] {}, 0, 0)));
		assertEquals(Objects.hashCode(new String[][][] {}),
				Objects.hashCode(Arrays.toArray3D(new String[][] {}, 0)));
		assertEquals(Objects.hashCode(new String[][][] {}),
				Objects.hashCode(Arrays.toArray3D(new String[][][] {})));

		assertEquals(Objects.hashCode(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}}),
				Objects.hashCode(Arrays.toArray3D(new String[] {"a", "b", "c"}, 3, 1)));
		assertEquals(Objects.hashCode(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}}),
				Objects.hashCode(Arrays.toArray3D(new String[][] {{"a"}, {"b"}, {"c"}}, 1)));
		assertEquals(Objects.hashCode(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}}),
				Objects.hashCode(Arrays.toArray3D(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}})));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of count method, of class Arrays.
	 */
	public void testCount() {
		IO.test(BULLET, " count");

		assertEquals(FLAT_ARRAY_2D.length, Arrays.count(ARRAY_2D));
		assertEquals(FLAT_ARRAY_3D.length, Arrays.count(ARRAY_3D));
	}

	/**
	 * Test of filterAll method, of class Arrays.
	 */
	public void testFilterAll() {
		IO.test(BULLET, " filterAll");

		assertEquals(Objects.hashCode(
				new String[][] {new String[] {"a", "a", "c", "d", "d", "f"}}),
				Objects.hashCode(Arrays.<String>filterAll(ARRAY,
						new int[][] {new int[] {0, 0, 2, 3, 3, 5}})));
	}

	/**
	 * Test of merge method, of class Arrays.
	 */
	public void testMerge() {
		IO.test(BULLET, " merge");

		assertEquals(Objects.hashCode(new String[] {"a", "a", "b", "c", "d", "e", "f"}),
				Objects.hashCode(Arrays.<String>merge(new String[] {"a"}, ARRAY)));
		assertEquals(Objects.hashCode(FLAT_ARRAY_2D),
				Objects.hashCode(Arrays.<String>merge(ARRAY, ARRAY)));
		assertEquals(Objects.hashCode(FLAT_ARRAY_2D),
				Objects.hashCode(Arrays.<String>merge(ARRAY_2D)));
	}

	/**
	 * Test of take method, of class Arrays.
	 */
	public void testTake() {
		IO.test(BULLET, " take");

		// • 1D
		assertEquals(Objects.hashCode(ARRAY), Objects.hashCode(Arrays.<String>take(ARRAY)));
		assertEquals(Objects.hashCode(new String[] {"e", "f"}),
				Objects.hashCode(Arrays.<String>take(ARRAY, 4)));
		assertEquals(Objects.hashCode(new String[] {"e", "f"}),
				Objects.hashCode(Arrays.<String>take(ARRAY, 4, 2)));
		assertEquals(Objects.hashCode(new String[] {"e", "f"}),
				Objects.hashCode(Arrays.<String>take(ARRAY, 4, 10)));
		assertEquals(Objects.hashCode(Strings.EMPTY_ARRAY),
				Objects.hashCode(Arrays.<String>take(ARRAY, 4, 0)));

		// • 2D
		assertEquals(Objects.hashCode(FLAT_ARRAY_2D),
				Objects.hashCode(Arrays.<String>take(ARRAY_2D)));
		assertEquals(Objects.hashCode(ARRAY),
				Objects.hashCode(Arrays.<String>take(ARRAY_2D, 1)));
		assertEquals(Objects.hashCode(ARRAY),
				Objects.hashCode(Arrays.<String>take(ARRAY_2D, 1, 1)));
		assertEquals(Objects.hashCode(ARRAY),
				Objects.hashCode(Arrays.<String>take(ARRAY_2D, 1, 10)));
		assertEquals(Objects.hashCode(Strings.EMPTY_ARRAY),
				Objects.hashCode(Arrays.<String>take(ARRAY_2D, 1, 0)));

		// • 3D
		assertEquals(Objects.hashCode(FLAT_ARRAY_3D),
				Objects.hashCode(Arrays.<String>take(ARRAY_3D)));
		assertEquals(Objects.hashCode(FLAT_ARRAY_2D),
				Objects.hashCode(Arrays.<String>take(ARRAY_3D, 1)));
		assertEquals(Objects.hashCode(FLAT_ARRAY_2D),
				Objects.hashCode(Arrays.<String>take(ARRAY_3D, 1, 1)));
		assertEquals(Objects.hashCode(FLAT_ARRAY_2D),
				Objects.hashCode(Arrays.<String>take(ARRAY_3D, 1, 10)));
		assertEquals(Objects.hashCode(Strings.EMPTY_ARRAY),
				Objects.hashCode(Arrays.<String>take(ARRAY_3D, 1, 0)));
	}

	/**
	 * Test of unique method, of class Arrays.
	 */
	public void testUnique() {
		IO.test(BULLET, " unique");

		assertEquals(Objects.hashCode(ARRAY),
				Objects.hashCode(Arrays.<String>unique(FLAT_ARRAY_2D)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of isBetween method, of class Arrays.
	 */
	public void testIsBetween() {
		IO.test(BULLET, " isBetween");

		assertTrue(Arrays.isBetween(ARRAY, ARRAY, ARRAY, true, true));

		assertTrue(!Arrays.isBetween(ARRAY, ARRAY, ARRAY));
		assertTrue(!Arrays.isBetween(ARRAY, ARRAY, ARRAY, false, true));
		assertTrue(!Arrays.isBetween(ARRAY, ARRAY, ARRAY, false, false));

		assertTrue(Arrays.isBetween(ARRAY, ARRAY, new String[] {"a", "b", "c", "d", "g"}));
		assertTrue(Arrays.isBetween(ARRAY, new String[] {"a", "b", "c", "d", "e"}, ARRAY, false,
				true));
		assertTrue(Arrays.isBetween(ARRAY, new String[] {"a", "b", "c", "d", "e"},
				new String[] {"a", "b", "c", "d", "g"}, false, false));

	}
}
