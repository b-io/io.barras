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
package jupiter.common.struct.map.tree;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import java.util.Map;

import jupiter.common.test.Test;
import jupiter.common.util.Strings;

public class AvlTreeMapTest
		extends Test {

	public AvlTreeMapTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link AvlTreeMap#getHeight}.
	 */
	public void testGetHeight() {
		IO.test(BULLET, "getHeight");

		// • Fill with 7 elements
		AvlTreeMap<Integer, Integer> instance = new AvlTreeMap<Integer, Integer>(Integer.class);
		fill(instance, 7);
		String representation = instance.toString();
		IO.result(representation);
		assertEquals(3, instance.getHeight());
		assertTrue(Strings.countLines(representation) <= instance.getMaxHeight());

		// • Fill with 15 elements
		instance = new AvlTreeMap<Integer, Integer>(Integer.class);
		fill(instance, 15);
		representation = instance.toString();
		assertEquals(4, instance.getHeight());
		assertTrue(Strings.countLines(representation) <= instance.getMaxHeight());
	}

	/**
	 * Tests {@link AvlTreeMap#put}.
	 */
	public void testPut() {
		IO.test(BULLET, "put");

		final AvlTreeMap<Integer, Integer> instance = new AvlTreeMap<Integer, Integer>(
				Integer.class);
		fill(instance);
		assertEquals(0, (int) instance.getFirstEntry().key);
		assertEquals(6, (int) instance.getLastEntry().value);
	}

	/**
	 * Tests {@link AvlTreeMap#remove}.
	 */
	public void testRemove() {
		IO.test(BULLET, "removeNode");

		final AvlTreeMap<Integer, Integer> instance = new AvlTreeMap<Integer, Integer>(
				Integer.class);
		fill(instance);
		instance.remove(0);
		assertEquals(1, (int) instance.getFirstEntry().key);
		assertEquals(1, (int) instance.getFirstEntry().value);
	}

	/**
	 * Tests {@link AvlTreeMap#clone}.
	 */
	public void testClone() {
		IO.test(BULLET, "clone");

		final AvlTreeMap<Integer, Integer> instance = new AvlTreeMap<Integer, Integer>(
				Integer.class);
		fill(instance);
		assertEquals(instance, instance.clone());
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
