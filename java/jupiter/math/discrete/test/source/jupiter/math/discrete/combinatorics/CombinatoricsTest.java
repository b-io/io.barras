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
package jupiter.math.discrete.combinatorics;

import static jupiter.common.io.IO.IO;

import jupiter.common.test.Test;

public class CombinatoricsTest
		extends Test {

	public CombinatoricsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of P method, of class Combinatorics.
	 */
	public void testP() {
		IO.test("P");

		// Count the number of permutations of an 10-element set
		assertEquals(1L,     Combinatorics.P(10L, 0L));
		assertEquals(10L,    Combinatorics.P(10L, 1L));
		assertEquals(30240L, Combinatorics.P(10L, 5L));
		assertEquals(1L,     Combinatorics.P(10L, 10L));

		// Count the number of anagrams of the word "MISSISSIPPI"
		assertEquals(34650L, Combinatorics.PR(1L, 4L, 4L, 2L));
	}

	/**
	 * Test of C method, of class Combinatorics.
	 */
	public void testC() {
		IO.test("C");

		// Count the number of combinations of an 10-element set
		assertEquals(1L,   Combinatorics.C(10L, 0L));
		assertEquals(10L,  Combinatorics.C(10L, 1L));
		assertEquals(252L, Combinatorics.C(10L, 5L));
		assertEquals(1L,   Combinatorics.C(10L, 10L));

		// Count the number of ways to choose 3 donuts from 4 distinct types with repetition
		assertEquals(20L, Combinatorics.CR(4L, 3L));

		// Count the number of ways to choose 3 donuts from 4 distinct types with repetition having
		// at least 1 of them from the first type
		assertEquals(10L, Combinatorics.CR(4L, 3L, 1L));
	}
}