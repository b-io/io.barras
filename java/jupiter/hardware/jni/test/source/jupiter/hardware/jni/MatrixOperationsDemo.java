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
package jupiter.hardware.jni;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.hardware.jni.MatrixOperations.multiply;
import static jupiter.hardware.jni.MatrixOperations.test;

import jupiter.common.util.Doubles;

/**
 * {@link MatrixOperationsDemo} demonstrates {@link MatrixOperations}.
 */
public class MatrixOperationsDemo {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Demonstrates {@link MatrixOperations}.
	 * <p>
	 * @param args ignored
	 */
	public static void main(final String[] args) {
		test();

		final double[] A = new double[] {
			1., 2., 3., 4., 5., 6.
		};
		final double[] B = new double[] {
			1., 2., 3., 4.
		};
		for (int i = 0; i < 100; ++i) {
			IO.result(Doubles.toString(multiply(A, B, 2, 2)));
		}
	}
}
