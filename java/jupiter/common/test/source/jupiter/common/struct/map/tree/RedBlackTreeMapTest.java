/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.struct.map.tree;

import static jupiter.common.io.IO.IO;

import java.util.Map;

import jupiter.common.test.Test;
import jupiter.common.util.Strings;

public class RedBlackTreeMapTest
		extends Test {

	public RedBlackTreeMapTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of getHeight method, of class RedBlackTreeMap.
	 */
	public void testGetHeight() {
		IO.test("• getHeight");

		// • Fill with 7 elements
		RedBlackTreeMap<Integer, Integer> instance = new RedBlackTreeMap<Integer, Integer>(
				Integer.class);
		fill(instance, 7);
		String representation = instance.toString();
		IO.result(representation);
		assertEquals(4, Strings.countLines(representation, true));
		assertTrue(Strings.countLines(representation, true) <= instance.getMaxHeight());

		// • Fill with 15 elements
		instance = new RedBlackTreeMap<Integer, Integer>(Integer.class);
		fill(instance, 15);
		representation = instance.toString();
		assertEquals(6, instance.getHeight());
		assertTrue(Strings.countLines(representation, true) <= instance.getMaxHeight());
	}

	/**
	 * Test of put method, of class RedBlackTreeMap.
	 */
	public void testPut() {
		IO.test("• put");

		final int n = 100;

		final RedBlackTreeMap<Integer, Integer> instance = new RedBlackTreeMap<Integer, Integer>(
				Integer.class);
		fill(instance, n);
		assertEquals(0, (int) instance.getFirstEntry().key);
		assertEquals(0, (int) instance.getFirstEntry().value);
		assertEquals(n - 1, (int) instance.getLastEntry().key);
		assertEquals(n - 1, (int) instance.getLastEntry().value);
	}

	/**
	 * Test of remove method, of class RedBlackTreeMap.
	 */
	public void testRemove() {
		IO.test("• remove");

		final int n = 100;

		final RedBlackTreeMap<Integer, Integer> instance = new RedBlackTreeMap<Integer, Integer>(
				Integer.class);
		fill(instance, n);
		remove(instance, 0, 25, 50, 75, 99);
		assertEquals(1, (int) instance.getFirstEntry().key);
		assertEquals(1, (int) instance.getFirstEntry().value);
	}

	/**
	 * Test of clone method, of class RedBlackTreeMap.
	 */
	public void testClone() {
		IO.test("• clone");

		final RedBlackTreeMap<Integer, Integer> instance = new RedBlackTreeMap<Integer, Integer>(
				Integer.class);
		fill(instance);
		final RedBlackTreeMap<Integer, Integer> clone = instance.clone();
		assertEquals(instance, clone);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void fill(final Map<Integer, Integer> tree) {
		fill(tree, 7);
	}

	protected void fill(final Map<Integer, Integer> map, final int n) {
		for (int i = 0; i < n; ++i) {
			map.put(i, i);
		}
	}

	protected void remove(final Map<Integer, Integer> map, final Integer... keys) {
		for (final Integer key : keys) {
			map.remove(key);
		}
	}
}
