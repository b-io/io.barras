/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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

import junit.framework.TestCase;

public class RedBlackTreeMapTest
		extends TestCase {

	public RedBlackTreeMapTest() {
	}

	/**
	 * Test of put method, of class RedBlackTreeMap.
	 */
	public void testPut() {
		IO.test("put");

		final int n = 100;

		final RedBlackTreeMap<Integer, Integer> instance = new RedBlackTreeMap<Integer, Integer>();
		fill(instance, n);
		assertEquals(0, (int) instance.getFirstEntry().key);
		assertEquals(0, (int) instance.getFirstEntry().value);
		assertEquals(n - 1, (int) instance.getLastEntry().key);
		assertEquals(n - 1, (int) instance.getLastEntry().value);

		final OriginalTree<Integer, Integer> original = new OriginalTree<Integer, Integer>();
		fill(original, n);
		assertEquals(original.getRootEntry(), instance.getRootEntry());
	}

	/**
	 * Test of remove method, of class RedBlackTreeMap.
	 */
	public void testRemove() {
		IO.test("remove");

		final int n = 100;

		final RedBlackTreeMap<Integer, Integer> instance = new RedBlackTreeMap<Integer, Integer>();
		fill(instance, n);
		remove(instance, 0, 25, 50, 75, 99);
		assertEquals(1, (int) instance.getFirstEntry().key);
		assertEquals(1, (int) instance.getFirstEntry().value);

		final OriginalTree<Integer, Integer> original = new OriginalTree<Integer, Integer>();
		fill(original, n);
		remove(original, 0, 25, 50, 75, 99);
		assertEquals(original.getRootEntry(), instance.getRootEntry());
	}

	/**
	 * Test of clone method, of class RedBlackTreeMap.
	 */
	public void testClone() {
		IO.test("clone");

		final RedBlackTreeMap<Integer, Integer> instance = new RedBlackTreeMap<Integer, Integer>();
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
