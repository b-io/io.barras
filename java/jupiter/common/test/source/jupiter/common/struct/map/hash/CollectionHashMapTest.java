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

import jupiter.common.struct.set.ExtendedHashSet;
import jupiter.common.test.Test;
import jupiter.common.util.Doubles;

public class CollectionHashMapTest
		extends Test {

	public CollectionHashMapTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link CollectionHashMap#getOrCreate}.
	 */
	public void testGetOrCreate() {
		IO.test(BULLET, "getOrCreate");

		final CollectionHashMap<Integer, Double> map = new CollectionHashMap<Integer, Double>();
		for (int i = 0; i < 100; ++i) {
			map.getOrCreate(i);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Tests {@link CollectionHashMap#addAt}.
	 */
	public void testAddAt() {
		IO.test(BULLET, "addAt");

		final CollectionHashMap<Integer, Double> map = new CollectionHashMap<Integer, Double>();
		fill(map);
	}

	/**
	 * Tests {@link CollectionHashMap#addAllAt}.
	 */
	public void testAddAllAt() {
		IO.test(BULLET, "addAllAt");

		final CollectionHashMap<Integer, Double> map = new CollectionHashMap<Integer, Double>();
		map.addAllAt(42, Doubles.toArray(Doubles.createSequence(100, 0., 1.)));
	}

	/**
	 * Tests {@link CollectionHashMap#removeAt}.
	 */
	public void testRemoveAt() {
		IO.test(BULLET, "removeAt");

		final ExtendedHashSet<Double> model = new ExtendedHashSet<Double>();
		CollectionHashMap<Integer, Double> map = new CollectionHashMap<Integer, Double>(
				model);
		fill(map);
		for (int i = 0; i < 100; i += 2) {
			map.removeAt(i, Double.valueOf(i));
			assertEquals(null, map.get(i));
		}
		map = new CollectionHashMap<Integer, Double>(model, false);
		fill(map);
		for (int i = 0; i < 100; i += 2) {
			map.removeAt(i, Double.valueOf(i));
			assertEquals(0, map.get(i).size());
		}
	}

	/**
	 * Tests {@link CollectionHashMap#removeAllAt}.
	 */
	public void testRemoveAllAt() {
		IO.test(BULLET, "removeAllAt");

		final CollectionHashMap<Integer, Double> map = new CollectionHashMap<Integer, Double>();
		map.addAllAt(42, Doubles.toArray(Doubles.createSequence(100, 0., 1.)));
		map.removeAllAt(42, Doubles.toLinkedList(Doubles.createSequence(30, 0., 1.)));
		assertEquals(70, map.get(42).size());
	}

	/**
	 * Tests {@link CollectionHashMap#retainAllAt}.
	 */
	public void testRetainAllAt() {
		IO.test(BULLET, "retainAllAt");

		final CollectionHashMap<Integer, Double> map = new CollectionHashMap<Integer, Double>();
		map.addAllAt(42, Doubles.toArray(Doubles.createSequence(100, 0., 1.)));
		map.retainAllAt(42, Doubles.toLinkedList(Doubles.createSequence(30, 0., 1.)));
		assertEquals(30, map.get(42).size());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void fill(final CollectionHashMap<Integer, Double> map) {
		fill(map, 100);
	}

	protected void fill(final CollectionHashMap<Integer, Double> map, final int n) {
		for (int i = 0; i < n; ++i) {
			map.addAt(i, Double.valueOf(i));
		}
	}
}
