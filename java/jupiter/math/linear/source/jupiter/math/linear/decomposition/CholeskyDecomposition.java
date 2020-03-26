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

import static jupiter.common.io.IO.IO;

import java.io.Serializable;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.math.Maths;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.test.MatrixArguments;

/**
 * {@link CholeskyDecomposition} performs a Cholesky decomposition on a {@link Matrix}.
 * <p>
 * For a symmetric, positive definite matrix {@code A}, the Cholesky decomposition is a lower
 * triangular matrix {@code L} so that {@code A = L L'}.
 * <p>
 * If the matrix is not symmetric or positive definite, the constructor returns a partial
 * decomposition and sets an internal flag that may be queried by the method {@link #isSPD}.
 * <p>
 * @author JAMA (http://math.nist.gov/javanumerics/jama)
 * @version 1.0.3
 */
public class CholeskyDecomposition
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The row and column dimension (square matrix).
	 */
	protected final int dimension;
	/**
	 * The flag specifying whether {@code A} is symmetric and positive definite.
	 */
	protected boolean isSymmetricPositiveDefinite;
	/**
	 * The decomposition {@code L}.
	 */
	protected final double[][] L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link CholeskyDecomposition} of the specified symmetric and positive definite
	 * {@link Matrix}. Sets {@code isSymmetricPositiveDefinite} and the decomposition {@code L}.
	 * <p>
	 * @param A the symmetric and positive definite {@link Matrix} to decompose
	 * <p>
	 * @throws IllegalOperationException if {@code A} is not square
	 */
	public CholeskyDecomposition(final Matrix A) {
		// Verify the feasibility
		if (!A.isSquare()) {
			throw new IllegalOperationException("The matrix is not square");
		}

		// Initialize
		final double[] elements = A.getElements();
		dimension = A.getRowDimension();
		isSymmetricPositiveDefinite = A.getColumnDimension() == dimension;
		L = new double[dimension][dimension];

		// Decompose
		for (int j = 0; j < dimension; ++j) {
			final double[] Lrowj = L[j];
			double d = 0.;
			for (int k = 0; k < j; ++k) {
				final double[] Lrowk = L[k];
				double s = 0.;
				for (int i = 0; i < k; ++i) {
					s += Lrowk[i] * Lrowj[i];
				}
				Lrowj[k] = s = (elements[j * dimension + k] - s) / L[k][k];
				d += s * s;
				isSymmetricPositiveDefinite &= elements[k * dimension + j] ==
						elements[j * dimension + k];
			}
			d = elements[j * dimension + j] - d;
			isSymmetricPositiveDefinite &= d > 0.;
			L[j][j] = Maths.sqrt(Math.max(0., d));
			for (int k = j + 1; k < dimension; ++k) {
				L[j][k] = 0.;
			}
		}

		// Verify the feasibility
		if (!isSymmetricPositiveDefinite) {
			IO.warn("The matrix is not symmetric positive definite");
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code A} is symmetric and positive definite.
	 * <p>
	 * @return {@code true} if {@code A} is symmetric and positive definite, {@code false} otherwise
	 */
	public boolean isSPD() {
		return isSymmetricPositiveDefinite;
	}

	/**
	 * Returns the triangular factor {@code L}.
	 * <p>
	 * @return the triangular factor {@code L}
	 */
	public Matrix getL() {
		return new Matrix(dimension, dimension, L);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SOLVER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Solves {@code A X = B}.
	 * <p>
	 * @param B a {@link Matrix} with as many rows as {@code A} and any number of columns
	 * <p>
	 * @return {@code X} so that {@code L L' X = B}
	 * <p>
	 * @throws IllegalArgumentException if the inner dimensions of {@code A} and {@code B} do not
	 *                                  agree
	 */
	public Matrix solve(final Matrix B) {
		// Check the arguments
		MatrixArguments.requireInnerDimension(B.getRowDimension(), dimension);

		// Initialize
		final Matrix X = B.clone();
		final int xColumnDimension = X.getColumnDimension();
		final double[] xElements = X.getElements();

		// Solve L * Y = B
		for (int k = 0; k < dimension; ++k) {
			for (int j = 0; j < xColumnDimension; ++j) {
				for (int i = 0; i < k; ++i) {
					xElements[k * xColumnDimension + j] -= xElements[i * xColumnDimension + j] *
							L[k][i];
				}
				xElements[k * xColumnDimension + j] /= L[k][k];
			}
		}

		// Solve L' * X = Y
		for (int k = dimension - 1; k >= 0; --k) {
			for (int j = 0; j < xColumnDimension; ++j) {
				for (int i = k + 1; i < dimension; ++i) {
					xElements[k * xColumnDimension + j] -= xElements[i * xColumnDimension + j] *
							L[i][k];
				}
				xElements[k * xColumnDimension + j] /= L[k][k];
			}
		}

		return X;
	}
}
