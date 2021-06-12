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
package jupiter.common.math;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.math.Maths.DEGREE_TO_RADIAN;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Test;

public class MathsTest
		extends Test {

	public MathsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests the trigonometric functions of {@link Maths}.
	 */
	public void testTrigonometricFunctions() {
		IO.test(BULLET, " trigonometric functions");

		// Initialize
		final double alpha = 0.4; // [rad]
		final double beta = 0.8; // [rad]

		// Verify the trigonometric functions
		assertEquals(alpha, Maths.asin(Maths.sin(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.asin(Maths.sin(beta)), Maths.TOLERANCE);
		assertEquals(alpha, Maths.asec(Maths.sec(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.asec(Maths.sec(beta)), Maths.TOLERANCE);

		assertEquals(alpha, Maths.acos(Maths.cos(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.acos(Maths.cos(beta)), Maths.TOLERANCE);
		assertEquals(alpha, Maths.acosec(Maths.cosec(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.acosec(Maths.cosec(beta)), Maths.TOLERANCE);

		assertEquals(alpha, Maths.atan(Maths.tan(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.atan(Maths.tan(beta)), Maths.TOLERANCE);
		assertEquals(alpha, Maths.acot(Maths.cot(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.acot(Maths.cot(beta)), Maths.TOLERANCE);

		assertEquals(0., Maths.atan2(0., 0.), Maths.TOLERANCE);
		assertEquals(45. * DEGREE_TO_RADIAN, Maths.atan2(1., 1.), Maths.TOLERANCE);
		assertEquals(135. * DEGREE_TO_RADIAN, Maths.atan2(-1., 1.), Maths.TOLERANCE);
		assertEquals(-135. * DEGREE_TO_RADIAN, Maths.atan2(-1., -1.), Maths.TOLERANCE);
		assertEquals(-45. * DEGREE_TO_RADIAN, Maths.atan2(1., -1.), Maths.TOLERANCE);

		assertEquals(45. * DEGREE_TO_RADIAN, Maths.atan3(1., 1.), Maths.TOLERANCE);
		assertEquals(135. * DEGREE_TO_RADIAN, Maths.atan3(-1., 1.), Maths.TOLERANCE);
		assertEquals(225. * DEGREE_TO_RADIAN, Maths.atan3(-1., -1.), Maths.TOLERANCE);
		assertEquals(315. * DEGREE_TO_RADIAN, Maths.atan3(1., -1.), Maths.TOLERANCE);

		// Verify the trigonometric hyperbolic functions
		assertEquals(alpha, Maths.asinh(Maths.sinh(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.asinh(Maths.sinh(beta)), Maths.TOLERANCE);
		assertEquals(alpha, Maths.asech(Maths.sech(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.asech(Maths.sech(beta)), Maths.TOLERANCE);

		assertEquals(alpha, Maths.acosh(Maths.cosh(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.acosh(Maths.cosh(beta)), Maths.TOLERANCE);
		assertEquals(alpha, Maths.acosech(Maths.cosech(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.acosech(Maths.cosech(beta)), Maths.TOLERANCE);

		assertEquals(alpha, Maths.atanh(Maths.tanh(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.atanh(Maths.tanh(beta)), Maths.TOLERANCE);
		assertEquals(alpha, Maths.acoth(Maths.coth(alpha)), Maths.TOLERANCE);
		assertEquals(beta, Maths.acoth(Maths.coth(beta)), Maths.TOLERANCE);
	}

	/**
	 * Tests {@link Maths#greatCircleDistance}.
	 */
	public void testGreatCircleDistance() {
		IO.test(BULLET, " greatCircleDistance");

		// Initialize
		final double earthRadius = 6371.; // [km]

		// Verify the orthodromic distance between the Statue of Liberty and the Eiffel Tower
		assertEquals(5837.44, Maths.greatCircleDistance(earthRadius,
				40.689247 * DEGREE_TO_RADIAN, -74.044502 * DEGREE_TO_RADIAN,
				48.858093 * DEGREE_TO_RADIAN, 2.294694 * DEGREE_TO_RADIAN), 0.01);
	}
}
