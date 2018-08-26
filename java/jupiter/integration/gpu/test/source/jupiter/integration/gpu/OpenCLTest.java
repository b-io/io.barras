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
package jupiter.integration.gpu;

import static jupiter.common.io.IO.IO;
import static jupiter.integration.gpu.OpenCL.CL;

import java.io.IOException;

import junit.framework.TestCase;
import jupiter.common.util.Doubles;

public class OpenCLTest
		extends TestCase {

	public OpenCLTest() {
	}

	/**
	 * Test of class OpenCL (array times).
	 */
	public void testArrayTimes()
			throws IOException {
		IO.test("OpenCL (array times)");

		// Create the input and output data
		final int n = 10;
		final double[] A = new double[n];
		final double[] B = new double[n];
		for (int i = 0; i < n; i++) {
			A[i] = i;
			B[i] = i;
		}

		// Execute the operation
		final double[] result = CL.arrayTimes(A, B);

		// Verify the result
		IO.test("Result: ", Doubles.toString(result));
		boolean isPassed = true;
		for (int i = 0; i < n; i++) {
			final double x = result[i];
			final double y = A[i] * B[i];
			if (!Doubles.equals(x, y)) {
				isPassed = false;
				break;
			}
		}
		IO.test(isPassed ? "PASSED" : "FAILED");
		assertEquals(true, isPassed);
	}

	/**
	 * Test of class OpenCL (times / dot product).
	 */
	public void testTimes()
			throws IOException {
		IO.test("OpenCL (times / dot product)");

		// Create the input and output data
		final int aRowDimension = 2, aColumnDimension = 3;
		final int bRowDimension = 3, bColumnDimension = 4;
		final double[] A = new double[aRowDimension * aColumnDimension];
		final double[] B = new double[bRowDimension * bColumnDimension];
		for (int i = 0; i < A.length; ++i) {
			A[i] = i;
		}
		for (int i = 0; i < B.length; ++i) {
			B[i] = i;
		}

		// Execute the operation
		final double[] result = CL.times(A, B, aColumnDimension, bColumnDimension);

		// Verify the result
		IO.test("Result: ", Doubles.toString(result));
		boolean isPassed = true;
		for (int e = 0; e < result.length; ++e) {
			final double x = result[e];

			double y = 0.0;
			final int rowOffset = e / bColumnDimension;
			final int columnOffset = e % bColumnDimension;
			for (int i = 0; i < aColumnDimension; ++i) {
				y += A[rowOffset * aColumnDimension + i] * B[i * bColumnDimension + columnOffset];
			}

			if (!Doubles.equals(x, y)) {
				isPassed = false;
			}
		}
		IO.test(isPassed ? "PASSED" : "FAILED");
		assertEquals(true, isPassed);
	}
}
