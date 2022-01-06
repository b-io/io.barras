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
package jupiter.hardware.gpu;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;
import static jupiter.hardware.gpu.OpenCL.CL;

import jupiter.common.test.Test;
import jupiter.common.util.Doubles;

public class OpenCLTest
		extends Test {

	public OpenCLTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link OpenCL#arrayTimes}.
	 */
	public void testArrayTimes() {
		if (OpenCL.IS_ACTIVE) {
			IO.test(BULLET, "arrayTimes");

			// Create the input and output data
			final int dimension = 1000;
			final double[] A = new double[dimension];
			final double[] B = new double[dimension];
			for (int i = 0; i < dimension; ++i) {
				A[i] = i;
				B[i] = i;
			}

			// Execute the operation
			final double[] result = CL.arrayTimes(A, B);
			//IO.result(Doubles.toString(result));

			// Verify the result
			boolean isPassed = true;
			for (int i = 0; i < dimension; ++i) {
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
	}

	/**
	 * Tests {@link OpenCL#times}.
	 */
	public void testTimes() {
		if (OpenCL.IS_ACTIVE) {
			IO.test(BULLET, "times");

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
			final double[] result = CL.times(A, B, aColumnDimension, bColumnDimension);
			//IO.result(Doubles.toString(result));

			// Verify the result
			boolean isPassed = true;
			for (int ri = 0; ri < result.length; ++ri) {
				final double x = result[ri];

				double y = 0.;
				final int rowOffset = ri / bColumnDimension;
				final int columnOffset = ri % bColumnDimension;
				for (int k = 0; k < aColumnDimension; ++k) {
					y += A[rowOffset * aColumnDimension + k] *
							B[k * bColumnDimension + columnOffset];
				}

				if (!Doubles.equals(x, y)) {
					isPassed = false;
				}
			}
			IO.test(isPassed ? "PASSED" : "FAILED");
			assertEquals(true, isPassed);
		}
	}

	/**
	 * Tests {@link OpenCL#forward}.
	 */
	public void testForward() {
		if (OpenCL.IS_ACTIVE) {
			IO.test(BULLET, "forward");

			// Create the input and output data
			final int n = 100;
			final int aRowDimension = 2 * n, aColumnDimension = 3 * n;
			final int bRowDimension = 3 * n, bColumnDimension = 4 * n;
			final int cColumnDimension = bColumnDimension;
			final double[] A = new double[aRowDimension * aColumnDimension];
			final double[] B = new double[bRowDimension * bColumnDimension];
			final double[] C = new double[cColumnDimension];
			for (int i = 0; i < A.length; ++i) {
				A[i] = i;
			}
			for (int i = 0; i < B.length; ++i) {
				B[i] = i;
			}
			for (int i = 0; i < C.length; ++i) {
				C[i] = i;
			}

			// Execute the operation
			final double[] result = CL.forward(A, B, C, aColumnDimension, bColumnDimension,
					cColumnDimension);
			//IO.result(Doubles.toString(result));

			// Verify the result
			boolean isPassed = true;
			for (int ri = 0; ri < result.length; ++ri) {
				final double x = result[ri];

				double y = 0.;
				final int rowOffset = ri / bColumnDimension;
				final int columnOffset = ri % bColumnDimension;
				for (int k = 0; k < aColumnDimension; ++k) {
					y += A[rowOffset * aColumnDimension + k] *
							B[k * bColumnDimension + columnOffset];
				}
				y += C[ri % cColumnDimension];

				if (!Doubles.equals(x, y)) {
					isPassed = false;
				}
			}
			IO.test(isPassed ? "PASSED" : "FAILED");
			assertEquals(true, isPassed);
		}
	}

	/**
	 * Tests {@link OpenCL#arraySum}.
	 */
	public void testArraySum() {
		if (OpenCL.IS_ACTIVE) {
			IO.test(BULLET, "arraySum");

			// Create the input and output data
			final int dimension = 1000;
			final double[] A = new double[dimension];
			final double[] B = new double[dimension];
			final double c = 10;
			for (int i = 0; i < A.length; ++i) {
				A[i] = i - dimension / 2.;
			}
			for (int i = 0; i < B.length; ++i) {
				B[i] = i - dimension / 2.;
			}

			// Execute the operation
			final double[] result = CL.arraySum(A.clone(), B, c, 0, 0, dimension);
			//IO.result(Doubles.toString(A));

			// Verify the result
			boolean isPassed = true;
			for (int i = 0; i < dimension; ++i) {
				final double x = result[i];
				final double y = A[i] + c * B[i];
				if (!Doubles.equals(x, y)) {
					isPassed = false;
				}
			}
			IO.test(isPassed ? "PASSED" : "FAILED");
			assertEquals(true, isPassed);
		}
	}
}
