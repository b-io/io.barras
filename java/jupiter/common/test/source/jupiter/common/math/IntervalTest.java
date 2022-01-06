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
package jupiter.common.math;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Test;

public class IntervalTest
		extends Test {

	protected static final double ALPHA = 0.;
	protected static final double BETA = 1.;
	protected static final double GAMMA = 2.;

	protected static final Interval<Double> N1 = new Interval<Double>(
			new LowerBound<Double>(null, false), new UpperBound<Double>(null, false));
	protected static final Interval<Double> N2 = new Interval<Double>(
			new LowerBound<Double>(null, true), new UpperBound<Double>(ALPHA, false));
	protected static final Interval<Double> N3 = new Interval<Double>(
			new LowerBound<Double>(ALPHA, false), new UpperBound<Double>(null, true));
	protected static final Interval<Double> N4 = new Interval<Double>(
			new LowerBound<Double>(ALPHA, true), new UpperBound<Double>(ALPHA, true));

	protected static final Interval<Double> A1 = new Interval<Double>(
			new LowerBound<Double>(ALPHA, false), new UpperBound<Double>(BETA, false));
	protected static final Interval<Double> A2 = new Interval<Double>(
			new LowerBound<Double>(ALPHA, true), new UpperBound<Double>(BETA, false));
	protected static final Interval<Double> A3 = new Interval<Double>(
			new LowerBound<Double>(ALPHA, false), new UpperBound<Double>(BETA, true));
	protected static final Interval<Double> A4 = new Interval<Double>(
			new LowerBound<Double>(ALPHA, true), new UpperBound<Double>(BETA, true));

	protected static final Interval<Double> B1 = new Interval<Double>(
			new LowerBound<Double>(BETA, false), new UpperBound<Double>(GAMMA, false));
	protected static final Interval<Double> B2 = new Interval<Double>(
			new LowerBound<Double>(BETA, true), new UpperBound<Double>(GAMMA, false));
	protected static final Interval<Double> B3 = new Interval<Double>(
			new LowerBound<Double>(BETA, false), new UpperBound<Double>(GAMMA, true));
	protected static final Interval<Double> B4 = new Interval<Double>(
			new LowerBound<Double>(BETA, true), new UpperBound<Double>(GAMMA, true));

	protected static final Interval<Double> C1 = new Interval<Double>(
			new LowerBound<Double>(ALPHA, false), new UpperBound<Double>(GAMMA, false));
	protected static final Interval<Double> C2 = new Interval<Double>(
			new LowerBound<Double>(ALPHA, true), new UpperBound<Double>(GAMMA, false));
	protected static final Interval<Double> C3 = new Interval<Double>(
			new LowerBound<Double>(ALPHA, false), new UpperBound<Double>(GAMMA, true));
	protected static final Interval<Double> C4 = new Interval<Double>(
			new LowerBound<Double>(ALPHA, true), new UpperBound<Double>(GAMMA, true));

	////////////////////////////////////////////////////////////////////////////////////////////////

	public IntervalTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Interval#merge}.
	 */
	public void testMerge() {
		IO.test(BULLET, "merge");

		// Initialize
		final Interval<Double> n1 = N1.clone();
		final Interval<Double> n2 = N2.clone();
		final Interval<Double> n3 = N3.clone();
		final Interval<Double> n4 = N4.clone();

		final Interval<Double> a1 = A1.clone();
		final Interval<Double> a2 = A2.clone();
		final Interval<Double> a3 = A3.clone();
		final Interval<Double> a4 = A4.clone();

		final Interval<Double> b1 = B1.clone();
		final Interval<Double> b2 = B2.clone();
		final Interval<Double> b3 = B3.clone();
		final Interval<Double> b4 = B4.clone();

		// Verify the method
		// • N
		assertTrue(n1.merge(A1));
		assertEquals(A1, n1);
		assertTrue(n2.merge(A2));
		assertEquals(A2, n2);
		assertTrue(n3.merge(A3));
		assertEquals(A3, n3);
		assertTrue(n4.merge(A4));
		assertEquals(A4, n4);
		// • A
		assertTrue(!a1.merge(N1));
		assertTrue(!a2.merge(N2));
		assertTrue(!a3.merge(N3));
		assertTrue(!a4.merge(N4));
		assertTrue(a1.merge(A2));
		assertEquals(A2, a1);
		assertTrue(a1.merge(A3));
		assertEquals(A4, a1);
		assertTrue(!a2.merge(A1));
		assertEquals(A2, a2);
		assertTrue(a2.merge(A3));
		assertEquals(A4, a2);
		assertTrue(!a3.merge(A1));
		assertEquals(A3, a3);
		assertTrue(a3.merge(A2));
		assertEquals(A4, a3);
		// • B + A = C
		assertTrue(!b1.merge(A1));
		assertEquals(B1, b1);
		assertTrue(b2.merge(A2));
		assertEquals(C2, b2);
		assertTrue(b3.merge(A3));
		assertEquals(C3, b3);
		assertTrue(b4.merge(A4));
		assertEquals(C4, b4);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Interval#isInside}.
	 */
	public void testIsInside() {
		IO.test(BULLET, "isInside");

		assertTrue(!A1.isInside(ALPHA));
		assertTrue(!A1.isInside(BETA));

		assertTrue(A2.isInside(ALPHA));
		assertTrue(!A2.isInside(BETA));

		assertTrue(!A3.isInside(ALPHA));
		assertTrue(A3.isInside(BETA));

		assertTrue(A4.isInside(ALPHA));
		assertTrue(A4.isInside(BETA));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Interval#compareTo}.
	 */
	public void testCompareTo() {
		IO.test(BULLET, "compareTo");

		assertTrue(Comparables.isLessThan(N2, N4));
		assertTrue(Comparables.isLessThan(N4, N3));
		assertTrue(Comparables.isLessThan(N1, N3));

		assertTrue(Comparables.isLessThan(A2, A4));
		assertTrue(Comparables.isLessThan(A4, A3));
		assertTrue(Comparables.isLessThan(A1, A3));

		assertTrue(Comparables.isLessThan(B2, B4));
		assertTrue(Comparables.isLessThan(B4, B3));
		assertTrue(Comparables.isLessThan(B1, B3));

		assertTrue(Comparables.isLessThan(C2, C4));
		assertTrue(Comparables.isLessThan(C4, C3));
		assertTrue(Comparables.isLessThan(C1, C3));
	}
}
