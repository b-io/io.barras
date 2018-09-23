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
package jupiter.math.analysis;

import static jupiter.common.io.IO.IO;

import jupiter.common.test.Test;
import jupiter.common.util.Doubles;
import jupiter.math.analysis.function.Function;
import jupiter.math.analysis.function.Functions;
import jupiter.math.analysis.interpolation.SplineInterpolator;

public class SplineInterpolatorTest
		extends Test {

	public SplineInterpolatorTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of interpolate method, of class SplineInterpolator.
	 */
	public void testInterpolate() {
		IO.test("interpolate");

		final Function f = Functions.SIN;
		final double[] x = Doubles.toPrimitiveArray(0., 1., 2., 3., 4., 5., 6., 7., 8., 9.);
		final double[] y = f.applyToPrimitiveArray(x);
		final SplineInterpolator instance = SplineInterpolator.createMonotoneCubicSpline(x, y);
		for (int i = 0; i < x.length - 1; ++i) {
			final double result = instance.interpolate(x[i] + 0.5);
			IO.test(result, " ~ ", f.apply(x[i] + 0.5), " ?");
			assertEquals(f.apply(x[i] + 0.5), result, 0.05);
		}
	}
}
