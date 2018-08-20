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
package jupiter.math.linear.decomposition;

import java.io.Serializable;

import jupiter.common.exception.IllegalOperationException;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.test.MatrixArguments;

/**
 * LU Decomposition.
 * <p>
 * For a (m x n) matrix A with m {@literal >}= n, the LU decomposition is a (m x n) unit lower
 * triangular matrix L, a (n x n) upper triangular matrix U and a permutation vector piv of length m
 * so that A(piv, :) = L * U. If m {@literal <} n, then L is (m x m) and U is (m x n).
 * <p>
 * The LU decomposition with pivoting always exists, even if the matrix is singular, so the
 * constructor never fails. The primary use of the LU decomposition is in the solution of square
 * systems of simultaneous linear equations. This fails if isNonsingular() returns false.
 * <p>
 * @author JAMA, http://math.nist.gov/javanumerics/jama
 * @version 1.0.3
 */
public class LUDecomposition
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 4573517221084040490L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The row dimension.
	 */
	protected final int m;
	/**
	 * The column dimension.
	 */
	protected final int n;
	/**
	 * The decomposition.
	 */
	protected final double[][] LU;
	/**
	 * The pivot vector.
	 */
	protected final int[] pivot;
	/**
	 * The pivot sign.
	 */
	protected int pivotSign;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs the LU Decomposition. Performs the "left-looking", dot-product, Crout/Doolittle
	 * algorithm and sets {@code L}, {@code U} and {@code pivot}.
	 * <p>
	 * @param A a rectangular {@link Matrix}
	 */
	public LUDecomposition(final Matrix A) {
		// Initialize
		LU = A.getAll();
		m = A.getRowDimension();
		n = A.getColumnDimension();
		pivotSign = 1;
		pivot = new int[m];
		for (int i = 0; i < m; ++i) {
			pivot[i] = i;
		}
		double[] row;
		final double[] column = new double[m];

		// Decompose
		for (int j = 0; j < n; ++j) {
			// Copy the j-th column to localize references
			for (int i = 0; i < m; ++i) {
				column[i] = LU[i][j];
			}
			// Apply the previous transformations
			for (int i = 0; i < m; ++i) {
				row = LU[i];
				// @note most of the time is spent in the following dot product
				final int maxK = Math.min(i, j);
				double s = 0.;
				for (int k = 0; k < maxK; ++k) {
					s += row[k] * column[k];
				}
				row[j] = column[i] -= s;
			}
			// Find the pivot and exchange if necessary
			int p = j;
			for (int i = j + 1; i < m; ++i) {
				if (Math.abs(column[i]) > Math.abs(column[p])) {
					p = i;
				}
			}
			if (p != j) {
				for (int k = 0; k < n; ++k) {
					final double t = LU[p][k];
					LU[p][k] = LU[j][k];
					LU[j][k] = t;
				}
				final int k = pivot[p];
				pivot[p] = pivot[j];
				pivot[j] = k;
				pivotSign = -pivotSign;
			}
			// Compute multipliers
			if (j < m & LU[j][j] != 0.) {
				for (int i = j + 1; i < m; ++i) {
					LU[i][j] /= LU[j][j];
				}
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code A} is nonsingular.
	 * <p>
	 * @return {@code true} if U (and hence {@code A}) is nonsingular, {@code false} otherwise
	 */
	public boolean isNonsingular() {
		for (int j = 0; j < n; ++j) {
			if (LU[j][j] == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the lower triangular factor {@code L}.
	 * <p>
	 * @return the lower triangular factor {@code L}
	 */
	public Matrix getL() {
		final Matrix X = new Matrix(m, n);
		final double[][] L = X.getElements();
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (i > j) {
					L[i][j] = LU[i][j];
				} else if (i == j) {
					L[i][j] = 1.;
				} else {
					L[i][j] = 0.;
				}
			}
		}
		return X;
	}

	/**
	 * Returns the unpivoted lower triangular factor.
	 * <p>
	 * @return the unpivoted lower triangular factor
	 */
	public Matrix getUnpivotedL() {
		return getL().unpivot(getPivot());
	}

	/**
	 * Returns the upper triangular factor {@code U}.
	 * <p>
	 * @return the upper triangular factor {@code U}
	 */
	public Matrix getU() {
		final Matrix X = new Matrix(n, n);
		final double[][] U = X.getElements();
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				if (i <= j) {
					U[i][j] = LU[i][j];
				} else {
					U[i][j] = 0.;
				}
			}
		}
		return X;
	}

	/**
	 * Returns the pivot permutation vector {@code pivot}.
	 * <p>
	 * @return the pivot permutation vector {@code pivot}
	 */
	public int[] getPivot() {
		final int[] p = new int[m];
		System.arraycopy(pivot, 0, p, 0, m);
		return p;
	}

	/**
	 * Returns the pivot permutation vector as a one-dimensional double array.
	 * <p>
	 * @return the pivot permutation vector as a one-dimensional double array
	 */
	public double[] getDoublePivot() {
		final double[] vals = new double[m];
		for (int i = 0; i < m; ++i) {
			vals[i] = pivot[i];
		}
		return vals;
	}

	/**
	 * Returns the determinant of {@code A}.
	 * <p>
	 * @return the determinant of {@code A}
	 * <p>
	 * @throws IllegalOperationException if {@code A} is not square
	 */
	public double det() {
		// Verify the feasibility
		if (m != n) {
			throw new IllegalOperationException("The matrix is not square");
		}

		// Compute the determinant
		double d = pivotSign;
		for (int j = 0; j < n; ++j) {
			d *= LU[j][j];
		}
		return d;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SOLVER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the solution of {@code A * X = B}.
	 * <p>
	 * @param B a {@link Matrix} with as many rows as {@code A} and any number of columns
	 * <p>
	 * @return {@code X} so that {@code L * U * X = B(pivot, :)}
	 * <p>
	 * @throws IllegalArgumentException  if the matrix row dimensions do not agree
	 * @throws IllegalOperationException if {@code A} is singular
	 */
	public Matrix solve(final Matrix B) {
		// Check the arguments
		MatrixArguments.requireRowDimension(B.getRowDimension(), m);

		// Verify the feasibility
		if (!isNonsingular()) {
			throw new IllegalOperationException("The matrix is singular");
		}

		// Initialize
		final int nx = B.getColumnDimension();
		final Matrix X = B.getSubmatrix(pivot, 0, nx);
		final double[][] xElements = X.getElements();

		// Solve L * Y = B(pivot, :)
		for (int k = 0; k < n; ++k) {
			for (int i = k + 1; i < n; ++i) {
				for (int j = 0; j < nx; ++j) {
					xElements[i][j] -= xElements[k][j] * LU[i][k];
				}
			}
		}

		// Solve U * X = Y
		for (int k = n - 1; k >= 0; --k) {
			for (int j = 0; j < nx; ++j) {
				xElements[k][j] /= LU[k][k];
			}
			for (int i = 0; i < k; ++i) {
				for (int j = 0; j < nx; ++j) {
					xElements[i][j] -= xElements[k][j] * LU[i][k];
				}
			}
		}

		return X;
	}
}
