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
package jupiter.math.linear.decomposition;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.math.Maths;
import jupiter.common.test.Test;
import jupiter.math.linear.entity.Matrix;

public class LUDecompositionTest
		extends Test {

	public LUDecompositionTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link LUDecomposition#LUDecomposition}.
	 */
	public void testLUDecomposition() {
		IO.test(BULLET, " LUDecomposition");

		// Test the LU decomposition
		for (int size = 3; size <= 32; ++size) {
			// Initialize
			final Matrix matrix = Matrix.magic(size);

			// Decompose
			final LUDecomposition LU = new LUDecomposition(matrix);

			// Verify the decomposition
			final Matrix L = LU.getL();
			final Matrix U = LU.getU();
			final int[] pivot = LU.getPivot();
			final Matrix result = L.times(U).minus(matrix.getSubmatrix(pivot, 0, size));
			assertEquals(0., result.norm1() / (size * Maths.TOLERANCE), 0.01);
		}
	}
}
