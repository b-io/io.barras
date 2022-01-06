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
package jupiter.math.linear.test;

import static jupiter.common.test.Arguments.CHECK_ARGS;

import jupiter.common.test.Arguments;
import jupiter.math.linear.entity.Matrix;

public class MatrixArguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link MatrixArguments}.
	 */
	protected MatrixArguments() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void requireDimensions(final Matrix matrix, final int expectedRowDimension,
			final int expectedColumnDimension) {
		if (CHECK_ARGS) {
			Arguments.requireNonNull(matrix, "matrix");
			requireRowDimension(matrix.getRowDimension(), expectedRowDimension);
			requireColumnDimension(matrix.getColumnDimension(), expectedColumnDimension);
		}
	}

	public static void requireRowDimension(final int foundRowDimension,
			final int expectedRowDimension) {
		if (CHECK_ARGS && foundRowDimension != expectedRowDimension) {
			throw new IllegalArgumentException("The specified matrix has wrong row dimension " +
					Arguments.expectedButFound(foundRowDimension, expectedRowDimension));
		}
	}

	public static void requireColumnDimension(final int foundColumnDimension,
			final int expectedColumnDimension) {
		if (CHECK_ARGS && foundColumnDimension != expectedColumnDimension) {
			throw new IllegalArgumentException("The specified matrix has wrong column dimension " +
					Arguments.expectedButFound(foundColumnDimension, expectedColumnDimension));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void requireInnerDimension(final Matrix matrix,
			final int expectedInnerDimension) {
		if (CHECK_ARGS) {
			requireInnerDimension(Arguments.requireNonNull(matrix, "matrix").getRowDimension(),
					expectedInnerDimension);
		}
	}

	public static void requireInnerDimension(final int foundInnerDimension,
			final int expectedInnerDimension) {
		if (CHECK_ARGS && foundInnerDimension != expectedInnerDimension) {
			throw new IllegalArgumentException(
					"The specified matrix has wrong (inner) row dimension " +
					Arguments.expectedButFound(foundInnerDimension, expectedInnerDimension));
		}
	}
}
