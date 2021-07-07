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
package jupiter.math.linear.decomposition;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.math.Maths;
import jupiter.common.test.Test;
import jupiter.math.linear.entity.Matrix;

public class EigenvalueDecompositionTest
		extends Test {

	public EigenvalueDecompositionTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link EigenvalueDecomposition}.
	 */
	public void test() {
		IO.test(BULLET, "EigenvalueDecomposition");

		// Initialize
		final double[] expectedValues = new double[] {
			-5.196152, -1.118034E1, -2.160457E1, -3.462958E1, -5.669557E1, -8.248636E1,
			-1.187198E2, -1.605923E2, -2.150621E2, -2.692601E2, -3.534747E2, -4.360778E2,
			-5.415145E2, -6.26929E2, -7.868758E2, -9.143987E2, -1.097155E3, -1.210919E3,
			-1.480025E3, -1.652825E3, -1.943098E3, -2.076656E3, -2.494034E3, -2.704367E3,
			-3.140456E3, -3.279565E3, -3.890017E3, -4.126534E3, -4.750344E3, -4.875073E3
		};

		// Test the eigenvalue decomposition
		for (int size = 3; size <= 32; ++size) {
			// Initialize
			final Matrix matrix = Matrix.magic(size);

			// Decompose
			final EigenvalueDecomposition decomposition = new EigenvalueDecomposition(
					matrix.plus(matrix.transpose()).divide(2.));

			// Verify the decomposition
			final double[] eigenvalues = decomposition.getRealEigenvalues();
			assertEquals(expectedValues[size - 3], eigenvalues[0], 0.01);
			final double condition = matrix.cond();
			IO.test(condition < 1. / Maths.FLOAT_TOLERANCE ? condition : "Infinity");
		}
	}
}
