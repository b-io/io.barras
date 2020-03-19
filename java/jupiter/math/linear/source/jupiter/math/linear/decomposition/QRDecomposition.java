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

import java.io.Serializable;

import jupiter.common.exception.IllegalOperationException;
import jupiter.math.linear.entity.Matrix;
import jupiter.math.linear.test.MatrixArguments;

/**
 * {@link QRDecomposition} performs a QR decomposition on a {@link Matrix}.
 * <p>
 * For a {@code m x n} matrix {@code A} with {@code m {@literal >}= n}, the QR decomposition is a
 * {@code m x n} orthogonal matrix {@code Q} and a {@code n x n} upper triangular matrix {@code R}
 * so that {@code A = Q R}.
 * <p>
 * The QR decomposition always exists, even if the matrix does not have full rank, so the
 * constructor never fails. The primary use of the QR decomposition is in the least squares solution
 * of non-square systems of simultaneous linear equations. This fails if the method
 * {@link #isFullRank} returns {@code false}.
 * <p>
 * @author JAMA (http://math.nist.gov/javanumerics/jama)
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
	private static final long serialVersionUID = 1L;


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
	 * The decomposition containing {@code Q} and {@code R}.
	 */
	protected final double[][] QR;
	/**
	 * The {@code R} diagonal.
	 */
	protected final double[] Rdiag;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link QRDecomposition} of the specified rectangular {@link Matrix}, computed by
	 * Householder reflections. Sets {@code R} and the Householder vectors and computes {@code Q}.
	 * <p>
	 * @param A the rectangular {@link Matrix} to decompose
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
			double norm = 0.;
			for (int i = k; i < m; ++i) {
				norm = Norms.getEuclideanNorm(norm, QR[i][k]);
			}
			if (norm != 0.) {
				// Form the k-th Householder vector
				if (QR[k][k] < 0) {
					norm = -norm;
				}
				for (int i = k; i < m; ++i) {
					QR[i][k] /= norm;
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
			Rdiag[k] = -norm;
		}

		// Verify the feasibility
		if (!isFullRank()) {
			throw new IllegalOperationException("The matrix is rank deficient");
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code R} (and hence {@code A}) is full rank.
	 * <p>
	 * @return {@code true} if {@code R} (and hence {@code A}) is full rank, {@code false} otherwise
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
		final double[][] H = new double[m][n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (i >= j) {
					H[i][j] = QR[i][j];
				} else {
					H[i][j] = 0.;
				}
			}
		}
		return new Matrix(H);
	}

	/**
	 * Returns the upper triangular factor {@code R}.
	 * <p>
	 * @return the upper triangular factor {@code R}
	 */
	public Matrix getR() {
		final double[][] R = new double[n][n];
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
		return new Matrix(R);
	}

	/**
	 * Returns the (economy-sized) orthogonal factor {@code Q}.
	 * <p>
	 * @return the (economy-sized) orthogonal factor {@code Q}
	 */
	public Matrix getQ() {
		final double[][] Q = new double[m][n];
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
		return new Matrix(Q);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SOLVER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the least squares solution of {@code A X = B}.
	 * <p>
	 * @param B a {@link Matrix} with as many rows as {@code A} and any number of columns
	 * <p>
	 * @return {@code X} that minimizes the two norm of {@code Q R X - B}
	 * <p>
	 * @throws IllegalArgumentException  if the matrix row dimensions do not agree
	 * @throws IllegalOperationException if {@code A} is rank deficient
	 */
	public Matrix solve(final Matrix B) {
		// Check the arguments
		MatrixArguments.requireRowDimension(B.getRowDimension(), m);

		// Initialize
		final Matrix X = B.clone();
		final int xColumnDimension = X.getColumnDimension();
		final double[] xElements = X.getElements();

		// Compute Y = Q' * B
		for (int k = 0; k < n; ++k) {
			for (int j = 0; j < xColumnDimension; ++j) {
				double s = 0.;
				for (int i = k; i < m; ++i) {
					s += QR[i][k] * xElements[i * xColumnDimension + j];
				}
				s = -s / QR[k][k];
				for (int i = k; i < m; ++i) {
					xElements[i * xColumnDimension + j] += s * QR[i][k];
				}
			}
		}

		// Solve R * X = Y
		for (int k = n - 1; k >= 0; --k) {
			for (int j = 0; j < xColumnDimension; ++j) {
				xElements[k * xColumnDimension + j] /= Rdiag[k];
			}
			for (int i = 0; i < k; ++i) {
				for (int j = 0; j < xColumnDimension; ++j) {
					xElements[i * xColumnDimension + j] -= xElements[k * xColumnDimension + j] *
							QR[i][k];
				}
			}
		}

		return X;
	}
}
