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
package jupiter.common.struct.tuple;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Test;

public class PairTest
		extends Test {

	public PairTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Pair#clone}.
	 */
	@SuppressWarnings("deprecation")
	public void testClone() {
		IO.test(BULLET, "clone");

		final String[] header = new String[] {"a"};
		final Integer[] values = new Integer[] {42};
		final Pair<String[], Integer[]> pair = new Pair<String[], Integer[]>(header, values);
		assertEquals("a", pair.getFirst()[0]);
		assertEquals(new Integer(42), pair.getSecond()[0]);

		final Pair<String[], Integer[]> clone = pair.clone();
		clone.getFirst()[0] = "b";
		assertEquals("a", pair.getFirst()[0]);
		assertEquals("b", clone.getFirst()[0]);
		assertEquals(new Integer(42), pair.getSecond()[0]);
		assertEquals(new Integer(42), clone.getSecond()[0]);
	}
}
