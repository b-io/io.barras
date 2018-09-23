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
	 * Test of arrayTimes method, of class OpenCL.
	 */
	public void testArrayTimes()
			throws IOException {
		IO.test("arrayTimes");

		// Create the input and output data
		final int n = 10;
		final double[] A = new double[n];
		final double[] B = new double[n];
		for (int i = 0; i < n; i++) {
			A[i] = i;
			B[i] = i;
		}

		// Execute the operation
		final double[] result = JNI.arrayTimes(A, B);

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
	 * Test of times method, of class OpenCL.
	 */
	public void testTimes()
			throws IOException {
		IO.test("times");

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
				y += A[rowOffset * aColumnDimension + i] * B[i * bColumnDimension + columnOffset];
			}

			if (!Doubles.equals(x, y)) {
				isPassed = false;
			}
		}
		IO.test(isPassed ? "PASSED" : "FAILED");
		assertEquals(true, isPassed);
	}

	/**
	 * Test of forward method, of class OpenCL.
	 */
	public void testForward()
			throws IOException {
		IO.test("forward");

		// Create the input and output data
		final int aRowDimension = 2, aColumnDimension = 3;
		final int bRowDimension = 3, bColumnDimension = 4;
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
		final double[] result = JNI.forward(A, B, C, aColumnDimension, bColumnDimension,
				cColumnDimension);

		// Verify the result
		IO.test("Result: ", Doubles.toString(result));
		boolean isPassed = true;
		for (int e = 0; e < result.length; ++e) {
			final double x = result[e];

			double y = 0f;
			final int rowOffset = e / bColumnDimension;
			final int columnOffset = e % bColumnDimension;
			for (int i = 0; i < aColumnDimension; ++i) {
				y += A[rowOffset * aColumnDimension + i] * B[i * bColumnDimension + columnOffset];
			}
			y += C[e % cColumnDimension];

			if (!Doubles.equals(x, y)) {
				isPassed = false;
			}
		}
		IO.test(isPassed ? "PASSED" : "FAILED");
		assertEquals(true, isPassed);
	}
}
