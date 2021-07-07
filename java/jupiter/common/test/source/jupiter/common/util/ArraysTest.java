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
package jupiter.common.util;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;
import static jupiter.common.util.Classes.OBJECT_CLASS;

import jupiter.common.test.Test;

public class ArraysTest
		extends Test {

	// • 1D
	protected static final String[] ARRAY = new String[] {"a", "b", "c", "d", "e", "f"};
	// • 2D
	protected static final String[][] ARRAY_2D = new String[][] {ARRAY, ARRAY};
	protected static final String[] FLAT_ARRAY_2D = Arrays.concat(ARRAY, ARRAY);
	// • 3D
	protected static final String[][][] ARRAY_3D = new String[][][] {ARRAY_2D, ARRAY_2D};
	protected static final String[] FLAT_ARRAY_3D = Arrays.concat(FLAT_ARRAY_2D, FLAT_ARRAY_2D);

	////////////////////////////////////////////////////////////////////////////////////////////////

	public ArraysTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Arrays#getElementClass}.
	 */
	public void testGetElementClass() {
		IO.test(BULLET, "getElementClass");

		assertEquals(OBJECT_CLASS, Arrays.getElementClass());
		assertEquals(OBJECT_CLASS, Arrays.getElementClass(Objects.EMPTY_ARRAY));
		assertEquals(Number.class, Arrays.getElementClass(null, 1, 1.));
		assertEquals(String.class, Arrays.getElementClass(ARRAY));
		assertEquals(String[].class, Arrays.getElementClass(null, ARRAY));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Arrays#toArray}.
	 */
	public void testToArray() {
		IO.test(BULLET, "toArray");

		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY,
				Arrays.toArray(Strings.EMPTY_ARRAY)));
		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY,
				Arrays.toArray(Strings.EMPTY_ARRAY_2D)));
		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY,
				Arrays.toArray(Strings.EMPTY_ARRAY_3D)));

		assertTrue(Arrays.equals(new String[] {"a", "b", "c"},
				Arrays.toArray(new String[] {"a", "b", "c"})));
		assertTrue(Arrays.equals(new String[] {"a", "b", "c"},
				Arrays.toArray(new String[][] {{"a"}, {"b"}, {"c"}})));
		assertTrue(Arrays.equals(new String[] {"a", "b", "c"},
				Arrays.toArray(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}})));
	}

	/**
	 * Tests {@link Arrays#toArray2D}.
	 */
	public void testToArray2D() {
		IO.test(BULLET, "toArray2D");

		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY_2D,
				Arrays.toArray2D(Strings.EMPTY_ARRAY, 0)));
		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY_2D,
				Arrays.toArray2D(Strings.EMPTY_ARRAY_2D)));
		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY_2D,
				Arrays.toArray2D(Strings.EMPTY_ARRAY_3D)));

		assertTrue(Arrays.equals(new String[][] {{"a"}, {"b"}, {"c"}},
				Arrays.toArray2D(new String[] {"a", "b", "c"}, 3)));
		assertTrue(Arrays.equals(new String[][] {{"a"}, {"b"}, {"c"}},
				Arrays.toArray2D(new String[][] {{"a"}, {"b"}, {"c"}})));
		assertTrue(Arrays.equals(new String[][] {{"a"}, {"b"}, {"c"}},
				Arrays.toArray2D(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}})));
	}

	/**
	 * Tests {@link Arrays#toArray3D}.
	 */
	public void testToArray3D() {
		IO.test(BULLET, "toArray3D");

		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY_3D,
				Arrays.toArray3D(Strings.EMPTY_ARRAY, 0, 0)));
		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY_3D,
				Arrays.toArray3D(Strings.EMPTY_ARRAY_2D, 0)));
		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY_3D,
				Arrays.toArray3D(Strings.EMPTY_ARRAY_3D)));

		assertTrue(Arrays.equals(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}},
				Arrays.toArray3D(new String[] {"a", "b", "c"}, 3, 1)));
		assertTrue(Arrays.equals(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}},
				Arrays.toArray3D(new String[][] {{"a"}, {"b"}, {"c"}}, 1)));
		assertTrue(Arrays.equals(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}},
				Arrays.toArray3D(new String[][][] {{{"a"}}, {{"b"}}, {{"c"}}})));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Arrays#concat}.
	 */
	public void testConcat() {
		IO.test(BULLET, "concat");

		assertTrue(Arrays.equals(new String[] {"a", "a", "b", "c", "d", "e", "f"},
				Arrays.concat(new String[] {"a"}, ARRAY)));
		assertTrue(Arrays.equals(FLAT_ARRAY_2D,
				Arrays.concat(ARRAY, ARRAY)));
		assertTrue(Arrays.equals(FLAT_ARRAY_2D,
				Arrays.concat(ARRAY_2D)));
	}

	/**
	 * Tests {@link Arrays#count}.
	 */
	public void testCount() {
		IO.test(BULLET, "count");

		assertEquals(FLAT_ARRAY_2D.length, Arrays.count(ARRAY_2D));
		assertEquals(FLAT_ARRAY_3D.length, Arrays.count(ARRAY_3D));
	}

	/**
	 * Tests {@link Arrays#filterAll}.
	 */
	public void testFilterAll() {
		IO.test(BULLET, "filterAll");

		assertTrue(Arrays.equals(new String[][] {new String[] {"a", "a", "c", "d", "d", "f"}},
				Arrays.filterAll(ARRAY, new int[][] {new int[] {0, 0, 2, 3, 3, 5}})));
	}

	/**
	 * Tests {@link Arrays#take}.
	 */
	public void testTake() {
		IO.test(BULLET, "take");

		// • 1D
		assertTrue(Arrays.equals(new String[] {"e", "f"},
				Arrays.take(ARRAY, 4, 2)));
		assertTrue(Arrays.equals(new String[] {"e", "f"},
				Arrays.take(ARRAY, 4, 10)));
		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY,
				Arrays.take(ARRAY, 4, 0)));

		// • 2D
		assertTrue(Arrays.equals(FLAT_ARRAY_2D,
				Arrays.take(ARRAY_2D)));
		assertTrue(Arrays.equals(ARRAY,
				Arrays.take(ARRAY_2D, 1, 1)));
		assertTrue(Arrays.equals(ARRAY,
				Arrays.take(ARRAY_2D, 1, 10)));
		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY,
				Arrays.take(ARRAY_2D, 1, 0)));

		// • 3D
		assertTrue(Arrays.equals(FLAT_ARRAY_3D,
				Arrays.take(ARRAY_3D)));
		assertTrue(Arrays.equals(FLAT_ARRAY_2D,
				Arrays.take(ARRAY_3D, 1, 1)));
		assertTrue(Arrays.equals(FLAT_ARRAY_2D,
				Arrays.take(ARRAY_3D, 1, 10)));
		assertTrue(Arrays.equals(Strings.EMPTY_ARRAY,
				Arrays.take(ARRAY_3D, 1, 0)));
	}

	/**
	 * Tests {@link Arrays#tally}.
	 */
	public void testTally() {
		IO.test(BULLET, "tally");

		assertTrue(Integers.equals(new int[] {1, 1, 2, 2, 3, 3},
				Arrays.tally(ARRAY, new String[] {"a", "c", "e"})));
		assertTrue(Integers.equals(new int[] {0, 1, 1, 2, 2, 3},
				Arrays.tally(ARRAY, new String[] {"b", "d", "f"})));
		assertTrue(Integers.equals(new int[] {0, 0, 0, 0, 0, 0},
				Arrays.tally(ARRAY, Strings.EMPTY_ARRAY)));
		assertTrue(Integers.equals(Integers.EMPTY_PRIMITIVE_ARRAY,
				Arrays.tally(Strings.EMPTY_ARRAY, Strings.EMPTY_ARRAY)));
	}

	/**
	 * Tests {@link Arrays#unique}.
	 */
	public void testUnique() {
		IO.test(BULLET, "unique");

		assertTrue(Arrays.equals(ARRAY, Arrays.unique(FLAT_ARRAY_2D)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Arrays#isBetween}.
	 */
	public void testIsBetween() {
		IO.test(BULLET, "isBetween");

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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Arrays#clone}.
	 */
	public void testClone() {
		IO.test(BULLET, "clone");

		// • 1D
		assertTrue(Arrays.equals(ARRAY, Arrays.clone(ARRAY)));

		// • 2D
		assertTrue(Arrays.equals(ARRAY_2D, Arrays.clone(ARRAY_2D)));

		// • 3D
		assertTrue(Arrays.equals(ARRAY_3D, Arrays.clone(ARRAY_3D)));
	}
}
