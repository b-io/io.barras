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
 * QR Decomposition.
 * <p>
 * For a (m x n) matrix A with m {@literal >}= n, the QR decomposition is a (m x n) orthogonal
 * matrix Q and a (n x n) upper triangular matrix R so that A = Q * R.
 * <p>
 * The QR decomposition always exists, even if the matrix does not have full rank, so the
 * constructor never fails. The primary use of the QR decomposition is in the least squares solution
 * of non-square systems of simultaneous linear equations. This fails if isFullRank() returns false.
 * <p>
 * @author JAMA, http://math.nist.gov/javanumerics/jama
 * @version 1.0.3
 */
public class QRDecomposition
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -5830957598032372042L;


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
	protected final double[][] QR;
	/**
	 * The diagonal of {@code R}.
	 */
	protected final double[] Rdiag;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * QR Decomposition, computed by Householder reflections. Sets {@code R} and the Householder
	 * vectors and computes {@code Q}.
	 * <p>
	 * @param A a rectangular {@link Matrix}
	 */
	public QRDecomposition(final Matrix A) {
		// Initialize
		QR = A.toPrimitiveArray2D();
		m = A.getRowDimension();
		n = A.getColumnDimension();
		Rdiag = new double[n];

		// Decompose
		for (int k = 0; k < n; ++k) {
			// Compute the 2-norm of the k-th column without under/overflow
			double nrm = 0;
			for (int i = k; i < m; ++i) {
				nrm = Norms.getEuclideanNorm(nrm, QR[i][k]);
			}
			if (nrm != 0.) {
				// Form the k-th Householder vector
				if (QR[k][k] < 0) {
					nrm = -nrm;
				}
				for (int i = k; i < m; ++i) {
					QR[i][k] /= nrm;
				}
				QR[k][k] += 1.;
				// Apply the transformation to the remaining columns
				for (int j = k + 1; j < n; ++j) {
					double s = 0.;
					for (int i = k; i < m; ++i) {
						s += QR[i][k] * QR[i][j];
					}
					s = -s / QR[k][k];
					for (int i = k; i < m; ++i) {
						QR[i][j] += s * QR[i][k];
					}
				}
			}
			Rdiag[k] = -nrm;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code A} is full rank.
	 * <p>
	 * @return {@code true} if {@code R} (and hence {@code A}) is full rank
	 */
	public boolean isFullRank() {
		for (int j = 0; j < n; ++j) {
			if (Rdiag[j] == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the Householder vectors.
	 * <p>
	 * @return the lower trapezoidal matrix whose columns define the reflections
	 */
	public Matrix getH() {
		final Matrix X = new Matrix(m, n);
		final double[][] H = X.getElements();
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (i >= j) {
					H[i][j] = QR[i][j];
				} else {
					H[i][j] = 0.;
				}
			}
		}
		return X;
	}

	/**
	 * Return the upper triangular factor {@code R}.
	 * <p>
	 * @return the upper triangular factor {@code R}
	 */
	public Matrix getR() {
		final Matrix X = new Matrix(n, n);
		final double[][] R = X.getElements();
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				if (i < j) {
					R[i][j] = QR[i][j];
				} else if (i == j) {
					R[i][j] = Rdiag[i];
				} else {
					R[i][j] = 0.;
				}
			}
		}
		return X;
	}

	/**
	 * Returns the (economy-sized) orthogonal factor {@code Q}.
	 * <p>
	 * @return the (economy-sized) orthogonal factor {@code Q}
	 */
	public Matrix getQ() {
		final Matrix X = new Matrix(m, n);
		final double[][] Q = X.getElements();
		for (int k = n - 1; k >= 0; --k) {
			for (int i = 0; i < m; ++i) {
				Q[i][k] = 0.;
			}
			Q[k][k] = 1.;
			for (int j = k; j < n; ++j) {
				if (QR[k][k] != 0.) {
					double s = 0.;
					for (int i = k; i < m; ++i) {
						s += QR[i][k] * Q[i][j];
					}
					s = -s / QR[k][k];
					for (int i = k; i < m; ++i) {
						Q[i][j] += s * QR[i][k];
					}
				}
			}
		}
		return X;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SOLVER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the least squares solution of {@code A * X = B}.
	 * <p>
	 * @param B a {@link Matrix} with as many rows as {@code A} and any number of columns
	 * <p>
	 * @return {@code X} that minimizes the two norm of {@code Q * R * X - B}
	 * <p>
	 * @throws IllegalArgumentException  if the matrix row dimensions do not agree
	 * @throws IllegalOperationException if {@code A} is rank deficient
	 */
	public Matrix solve(final Matrix B) {
		// Check the arguments
		MatrixArguments.requireRowDimension(B.getRowDimension(), m);

		// Verify the feasibility
		if (!isFullRank()) {
			throw new IllegalOperationException("The matrix is rank deficient");
		}

		// Initialize
		final int nx = B.getColumnDimension();
		final double[][] xElements = B.toPrimitiveArray2D();

		// Compute Y = Q' * B
		for (int k = 0; k < n; ++k) {
			for (int j = 0; j < nx; ++j) {
				double s = 0.;
				for (int i = k; i < m; ++i) {
					s += QR[i][k] * xElements[i][j];
				}
				s = -s / QR[k][k];
				for (int i = k; i < m; ++i) {
					xElements[i][j] += s * QR[i][k];
				}
			}
		}

		// Solve R * X = Y
		for (int k = n - 1; k >= 0; --k) {
			for (int j = 0; j < nx; ++j) {
				xElements[k][j] /= Rdiag[k];
			}
			for (int i = 0; i < k; ++i) {
				for (int j = 0; j < nx; ++j) {
					xElements[i][j] -= xElements[k][j] * QR[i][k];
				}
			}
		}

		return new Matrix(n, nx, xElements).getSubmatrix(0, n, 0, nx);
	}
}
