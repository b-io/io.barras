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
package jupiter.integration.jni;

import static jupiter.common.io.IO.IO;

import java.io.IOException;

import jupiter.common.test.Test;
import jupiter.common.util.Doubles;

public class JNITest
		extends Test {

	public JNITest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of times method, of class JNI.
	 */
	public void testTimes()
			throws IOException {
		if (JNI.USE) {
			IO.test("times");

			// Create the input and output data
			final int n = 100;
			final int aRowDimension = 2 * n, aColumnDimension = 3 * n;
			final int bRowDimension = 3 * n, bColumnDimension = 4 * n;
			final double[] A = new double[aRowDimension * aColumnDimension];
			final double[] B = new double[bRowDimension * bColumnDimension];
			for (int i = 0; i < A.length; ++i) {
				A[i] = i;
			}
			for (int i = 0; i < B.length; ++i) {
				B[i] = i;
			}

			// Execute the operation
			final double[] result = JNI.times(A, B, aColumnDimension, bColumnDimension);

			// Verify the result
			IO.test("Result: ", Doubles.toString(result));
			boolean isPassed = true;
			for (int e = 0; e < result.length; ++e) {
				final double x = result[e];

				double y = 0f;
				final int rowOffset = e / bColumnDimension;
				final int columnOffset = e % bColumnDimension;
				for (int i = 0; i < aColumnDimension; ++i) {
					y += A[rowOffset * aColumnDimension + i] *
							B[i * bColumnDimension + columnOffset];
				}

				if (!Doubles.equals(x, y)) {
					isPassed = false;
				}
			}
			IO.test(isPassed ? "PASSED" : "FAILED");
			assertEquals(true, isPassed);
		}
	}
}
