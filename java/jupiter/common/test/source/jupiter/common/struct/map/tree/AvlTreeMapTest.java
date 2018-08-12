/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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

public class AvlTreeMapTest
		extends TestCase {

	public AvlTreeMapTest() {
	}

	/**
	 * Test of getHeight method, of class AvlTreeMap.
	 */
	public void testGetHeight() {
		IO.test("getHeight");

		AvlTreeMap<Integer, Integer> instance = new AvlTreeMap<Integer, Integer>();
		fill(instance, 7);
		assertEquals(2, instance.getHeight());
		instance = new AvlTreeMap<Integer, Integer>();
		fill(instance, 8);
		assertEquals(3, instance.getHeight());
	}

	/**
	 * Test of put method, of class AvlTreeMap.
	 */
	public void testPut() {
		IO.test("put");

		final AvlTreeMap<Integer, Integer> instance = new AvlTreeMap<Integer, Integer>();
		fill(instance);
		assertEquals(0, (int) instance.getFirstEntry().key);
		assertEquals(6, (int) instance.getLastEntry().value);
	}

	/**
	 * Test of remove method, of class AvlTreeMap.
	 */
	public void testRemove() {
		IO.test("removeNode");

		final AvlTreeMap<Integer, Integer> instance = new AvlTreeMap<Integer, Integer>();
		fill(instance);
		instance.remove(0);
		assertEquals(1, (int) instance.getFirstEntry().key);
		assertEquals(1, (int) instance.getFirstEntry().value);
	}

	/**
	 * Test of clone method, of class AvlTreeMap.
	 */
	public void testClone() {
		IO.test("clone");

		final AvlTreeMap<Integer, Integer> instance = new AvlTreeMap<Integer, Integer>();
		fill(instance);
		final AvlTreeMap clone = instance.clone();
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
}
