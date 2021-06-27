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
package jupiter.common.struct.map.hash;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Test;
import jupiter.common.util.Doubles;

public class CollectionExtendedHashMapTest
		extends Test {

	public CollectionExtendedHashMapTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link ExtendedHashMapCollection#getOrCreate}.
	 */
	public void testGetOrCreate() {
		IO.test(BULLET, " getOrCreate");

		final CollectionExtendedHashMap<Integer, Double> map = new CollectionExtendedHashMap<Integer, Double>();
		for (int i = 0; i < 100; ++i) {
			map.getOrCreate(i);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link ExtendedHashMapCollection#addAt}.
	 */
	public void testAddAt() {
		IO.test(BULLET, " addAt");

		final CollectionExtendedHashMap<Integer, Double> map = new CollectionExtendedHashMap<Integer, Double>();
		fill(map);
	}

	/**
	 * Tests {@link ExtendedHashMapCollection#addAllAt}.
	 */
	public void testAddAllAt() {
		IO.test(BULLET, " addAllAt");

		final CollectionExtendedHashMap<Integer, Double> map = new CollectionExtendedHashMap<Integer, Double>();
		map.addAllAt(42, Doubles.toArray(Doubles.createSequence(100, 0., 1.)));
	}

	/**
	 * Tests {@link ExtendedHashMapCollection#removeAt}.
	 */
	public void testRemoveAt() {
		IO.test(BULLET, " removeAt");

		final CollectionExtendedHashMap<Integer, Double> map = new CollectionExtendedHashMap<Integer, Double>();
		fill(map);
		for (int i = 0; i < 100; i += 2) {
			map.removeAt(i, Double.valueOf(i));
			assertEquals(0, map.get(i).size());
		}
	}

	/**
	 * Tests {@link ExtendedHashMapCollection#removeAllAt}.
	 */
	public void testRemoveAllAt() {
		IO.test(BULLET, " removeAllAt");

		final CollectionExtendedHashMap<Integer, Double> map = new CollectionExtendedHashMap<Integer, Double>();
		map.addAllAt(42, Doubles.toArray(Doubles.createSequence(100, 0., 1.)));
		map.removeAllAt(42, Doubles.toLinkedList(Doubles.createSequence(30, 0., 1.)));
		assertEquals(70, map.get(42).size());
	}

	/**
	 * Tests {@link ExtendedHashMapCollection#retainAllAt}.
	 */
	public void testRetainAllAt() {
		IO.test(BULLET, " retainAllAt");

		final CollectionExtendedHashMap<Integer, Double> map = new CollectionExtendedHashMap<Integer, Double>();
		map.addAllAt(42, Doubles.toArray(Doubles.createSequence(100, 0., 1.)));
		map.retainAllAt(42, Doubles.toLinkedList(Doubles.createSequence(30, 0., 1.)));
		assertEquals(30, map.get(42).size());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void fill(final CollectionExtendedHashMap<Integer, Double> map) {
		fill(map, 100);
	}

	protected void fill(final CollectionExtendedHashMap<Integer, Double> map, final int n) {
		for (int i = 0; i < n; ++i) {
			map.addAt(i, Double.valueOf(i));
		}
	}
}
