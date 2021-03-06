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
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Test;

public class StatisticsTest
		extends Test {

	public StatisticsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Statistics#normalCDF}.
	 */
	public void testNormalCDF() {
		IO.test(BULLET, "normalCDF");

		final double normalCDF = Statistics.normalCDF(0.);
		assertEquals(0.5, normalCDF, Maths.TOLERANCE);
	}

	/**
	 * Tests {@link Statistics#normalCDFInverse}.
	 */
	public void testNormalCDFInverse() {
		IO.test(BULLET, "normalCDFInverse");

		final double normalCDFInverse = Statistics.normalCDFInverse(Maths.DEFAULT_CONFIDENCE);
		IO.test("Accuracy:", Maths.distance(normalCDFInverse, Maths.DEFAULT_Z));
		assertEquals(Maths.DEFAULT_Z, normalCDFInverse, Maths.TOLERANCE);
	}
}
