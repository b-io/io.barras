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
package jupiter.common.math;

import static jupiter.common.io.IO.IO;
import static jupiter.common.math.IntervalTest.A1;
import static jupiter.common.math.IntervalTest.A2;
import static jupiter.common.math.IntervalTest.A3;
import static jupiter.common.math.IntervalTest.ALPHA;
import static jupiter.common.math.IntervalTest.B1;
import static jupiter.common.math.IntervalTest.B2;
import static jupiter.common.math.IntervalTest.B3;
import static jupiter.common.math.IntervalTest.BETA;
import static jupiter.common.math.IntervalTest.C1;
import static jupiter.common.math.IntervalTest.C2;
import static jupiter.common.math.IntervalTest.C3;
import static jupiter.common.math.IntervalTest.GAMMA;
import static jupiter.common.math.IntervalTest.N1;
import static jupiter.common.math.IntervalTest.N2;
import static jupiter.common.math.IntervalTest.N3;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Test;

public class IntervalListTest
		extends Test {

	protected static final IntervalList<Double> A = new IntervalList<Double>(N1, A1, N2, A2, A3);
	protected static final IntervalList<Double> B = new IntervalList<Double>(B1, N2, B2, N3, B3);
	protected static final IntervalList<Double> C = new IntervalList<Double>(C1, C2, N3, C3, N1);

	////////////////////////////////////////////////////////////////////////////////////////////////

	public IntervalListTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests isInside method, of class IntervalList.
	 */
	public void testIsInside() {
		IO.test(BULLET, " isInside");

		assertTrue(A.isInside(ALPHA));
		assertTrue(A.isInside(BETA));
		assertTrue(!A.isInside(GAMMA));

		assertTrue(!B.isInside(ALPHA));
		assertTrue(B.isInside(BETA));
		assertTrue(B.isInside(GAMMA));

		assertTrue(C.isInside(ALPHA));
		assertTrue(C.isInside(BETA));
		assertTrue(C.isInside(GAMMA));
	}

	/**
	 * Tests merge method, of class IntervalList.
	 */
	public void testMerge() {
		IO.test(BULLET, " merge");

		// Initialize
		final IntervalList<Double> a = A.clone();
		final IntervalList<Double> b = B.clone();
		final IntervalList<Double> c = C.clone();

		// Verify the method
		assertTrue(a.merge());
		a.removeEmpty();
		assertEquals(1, a.size());
		assertTrue(a.isInside(ALPHA));
		assertTrue(a.isInside(BETA));
		assertTrue(!a.isInside(GAMMA));

		assertTrue(b.merge());
		b.removeEmpty();
		assertEquals(1, b.size());
		assertTrue(!b.isInside(ALPHA));
		assertTrue(b.isInside(BETA));
		assertTrue(b.isInside(GAMMA));

		assertTrue(c.merge());
		c.removeEmpty();
		assertEquals(1, c.size());
		assertTrue(c.isInside(ALPHA));
		assertTrue(c.isInside(BETA));
		assertTrue(c.isInside(GAMMA));
	}
}
