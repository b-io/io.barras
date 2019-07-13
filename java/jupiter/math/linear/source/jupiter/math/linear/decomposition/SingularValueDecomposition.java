/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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

import jupiter.common.math.Maths;
import jupiter.math.linear.entity.Matrix;

/**
 * Singular Value Decomposition.
 * <p>
 * For a (m x n) matrix A with m {@literal >}= n, the singular value decomposition is a (m x n)
 * orthogonal matrix U, a (n x n) diagonal matrix S and a (n x n) orthogonal matrix V so that A = U
 * * S * V'.
 * <p>
 * The singular values, sigma[k] = S[k][k], are ordered so that sigma[0] {@literal >}= sigma[1]
 * {@literal >}= ... {@literal >}= sigma[n-1].
 * <p>
 * The singular value decomposition always exists, so the constructor never fails. The matrix
 * condition number and the effective numerical rank can be computed from this decomposition.
 * <p>
 * @author JAMA (http://math.nist.gov/javanumerics/jama)
 * @version 1.0.3
 */
public class SingularValueDecomposition
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
	 * The decomposition.
	 */
	protected final double[][] U, V;
	/**
	 * The singular values.
	 */
	protected final double[] s;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SingularValueDecomposition} of the specified rectangular {@link Matrix}.
	 * Sets the decomposition {@code U} and {@code V} and the singular values {@code s}.
	 * <p>
	 * See LINPACK code.
	 * <p>
	 * @param A the rectangular {@link Matrix} to decompose
	 */
	public SingularValueDecomposition(final Matrix A) {
		// Initialize
		final double[] elements = A.getElements();
		m = A.getRowDimension();
		n = A.getColumnDimension();
		/*
		 * @todo apparently the failing cases are only a proper subset of m < n, so let us not throw
		 * an exception. The fix may come later.
		 * if (m < n) { throw new UnsupportedOperationException("Work only for m >= n"); }
		 */
		final int nu = Math.min(m, n);
		s = new double[Math.min(m + 1, n)];
		U = new double[m][nu];
		V = new double[n][n];
		final double[] e = new double[n];
		final double[] work = new double[m];
		final boolean requireU = true;
		final boolean requireV = true;

		// Reduce A to the bidiagonal form,
		// storing the diagonal elements in s and the super-diagonal elements in e
		final int nct = Math.min(m - 1, n);
		final int nrt = Math.max(0, Math.min(m, n - 2));
		for (int k = 0; k < Math.max(nct, nrt); ++k) {
			if (k < nct) {
				// Apply the transformation for the k-th column and place the k-th diagonal in s[k]
				// Compute the 2-norm of the k-th column without under/overflow
				s[k] = 0;
				for (int i = k; i < m; ++i) {
					s[k] = Norms.getEuclideanNorm(s[k], elements[i * n + k]);
				}
				if (s[k] != 0.) {
					if (elements[k * n + k] < 0.) {
						s[k] = -s[k];
					}
					for (int i = k; i < m; ++i) {
						elements[i * n + k] /= s[k];
					}
					elements[k * n + k] += 1.;
				}
				s[k] = -s[k];
			}
			for (int j = k + 1; j < n; ++j) {
				if (k < nct && s[k] != 0.) {
					// Apply the transformation
					double t = 0.;
					for (int i = k; i < m; ++i) {
						t += elements[i * n + k] * elements[i * n + j];
					}
					t = -t / elements[k * n + k];
					for (int i = k; i < m; ++i) {
						elements[i * n + j] += t * elements[i * n + k];
					}
				}
				// Store the k-th row of A into e
				// for the subsequent calculation of the row transformation
				e[j] = elements[k * n + j];
			}
			if (requireU && k < nct) {
				// Store the transformation in U for the subsequent back multiplication
				for (int i = k; i < m; ++i) {
					U[i][k] = elements[i * n + k];
				}
			}
			if (k < nrt) {
				// Apply the k-th row transformation and place the k-th super-diagonal in e[k]
				// Compute the 2-norm without under/overflow
				e[k] = 0;
				for (int i = k + 1; i < n; ++i) {
					e[k] = Norms.getEuclideanNorm(e[k], e[i]);
				}
				if (e[k] != 0.) {
					if (e[k + 1] < 0.) {
						e[k] = -e[k];
					}
					for (int i = k + 1; i < n; ++i) {
						e[i] /= e[k];
					}
					e[k + 1] += 1.;
				}
				e[k] = -e[k];
				if (k + 1 < m && e[k] != 0.) {
					// Apply the transformation
					for (int i = k + 1; i < m; ++i) {
						work[i] = 0.;
					}
					for (int j = k + 1; j < n; ++j) {
						for (int i = k + 1; i < m; ++i) {
							work[i] += e[j] * elements[i * n + j];
						}
					}
					for (int j = k + 1; j < n; ++j) {
						final double t = -e[j] / e[k + 1];
						for (int i = k + 1; i < m; ++i) {
							elements[i * n + j] += t * work[i];
						}
					}
				}
				if (requireV) {
					// Store the transformation in V for the subsequent back multiplication
					for (int i = k + 1; i < n; ++i) {
						V[i][k] = e[i];
					}
				}
			}
		}

		// Set up the final bidiagonal matrix or order p
		int p = Math.min(m + 1, n);
		if (nct < n) {
			s[nct] = elements[nct * n + nct];
		}
		if (m < p) {
			s[p - 1] = 0.;
		}
		if (nrt + 1 < p) {
			e[nrt] = elements[nrt * n + p - 1];
		}
		e[p - 1] = 0.;

		// If required, create U
		if (requireU) {
			for (int j = nct; j < nu; ++j) {
				for (int i = 0; i < m; ++i) {
					U[i][j] = 0.;
				}
				U[j][j] = 1.;
			}
			for (int k = nct - 1; k >= 0; --k) {
				if (s[k] != 0.) {
					for (int j = k + 1; j < nu; ++j) {
						double t = 0.;
						for (int i = k; i < m; ++i) {
							t += U[i][k] * U[i][j];
						}
						t = -t / U[k][k];
						for (int i = k; i < m; ++i) {
							U[i][j] += t * U[i][k];
						}
					}
					for (int i = k; i < m; ++i) {
						U[i][k] = -U[i][k];
					}
					U[k][k] = 1. + U[k][k];
					for (int i = 0; i < k - 1; ++i) {
						U[i][k] = 0.;
					}
				} else {
					for (int i = 0; i < m; ++i) {
						U[i][k] = 0.;
					}
					U[k][k] = 1.;
				}
			}
		}

		// If required, create V
		if (requireV) {
			for (int k = n - 1; k >= 0; --k) {
				if (k < nrt && e[k] != 0.) {
					for (int j = k + 1; j < nu; ++j) {
						double t = 0.;
						for (int i = k + 1; i < n; ++i) {
							t += V[i][k] * V[i][j];
						}
						t = -t / V[k + 1][k];
						for (int i = k + 1; i < n; ++i) {
							V[i][j] += t * V[i][k];
						}
					}
				}
				for (int i = 0; i < n; ++i) {
					V[i][k] = 0.;
				}
				V[k][k] = 1.;
			}
		}

		// Compute the singular values
		final int pp = p - 1;
		// int iter = 0;
		final double eps = Maths.TOLERANCE;
		final double tiny = Maths.TINY_TOLERANCE;
		while (p > 0) {
			int k, kase;
			// @todo test to avoid too many iterations
			/*
			 * This section of the program inspects for negligible elements in the s and e arrays.
			 * On completion the variables kase and k are set as follows:
			 * - kase = 1 if s(p) and e[k-1] are negligible and k < p,
			 * - kase = 2 if s(k) is negligible and k < p,
			 * - kase = 3 if e[k-1] is negligible, k < p and s(k), ..., s(p) are not negligible (qr
			 *   step), or
			 * - kase = 4 if e(p-1) is negligible (convergence).
			 */
			for (k = p - 2; k >= -1; --k) {
				if (k == -1) {
					break;
				}
				if (Math.abs(e[k]) <= tiny + eps * (Math.abs(s[k]) + Math.abs(s[k + 1]))) {
					e[k] = 0.;
					break;
				}
			}
			if (k == p - 2) {
				kase = 4;
			} else {
				int ks;
				for (ks = p - 1; ks >= k; --ks) {
					if (ks == k) {
						break;
					}
					final double t = (ks != p ? Math.abs(e[ks]) : 0.) +
							(ks != k + 1 ? Math.abs(e[ks - 1]) : 0.);
					if (Math.abs(s[ks]) <= tiny + eps * t) {
						s[ks] = 0.;
						break;
					}
				}
				if (ks == k) {
					kase = 3;
				} else if (ks == p - 1) {
					kase = 1;
				} else {
					kase = 2;
					k = ks;
				}
			}
			++k;

			// Perform the task indicated by kase
			switch (kase) {
				// Deflate negligible s(p)
				case 1: {
					double f = e[p - 2];
					e[p - 2] = 0.;
					for (int j = p - 2; j >= k; --j) {
						double t = Norms.getEuclideanNorm(s[j], f);
						final double cs = s[j] / t;
						final double sn = f / t;
						s[j] = t;
						if (j != k) {
							f = -sn * e[j - 1];
							e[j - 1] = cs * e[j - 1];
						}
						if (requireV) {
							for (int i = 0; i < n; ++i) {
								t = cs * V[i][j] + sn * V[i][p - 1];
								V[i][p - 1] = -sn * V[i][j] + cs * V[i][p - 1];
								V[i][j] = t;
							}
						}
					}
					break;
				}

				// Split at negligible s(k)
				case 2: {
					double f = e[k - 1];
					e[k - 1] = 0.;
					for (int j = k; j < p; ++j) {
						double t = Norms.getEuclideanNorm(s[j], f);
						final double cs = s[j] / t;
						final double sn = f / t;
						s[j] = t;
						f = -sn * e[j];
						e[j] = cs * e[j];
						if (requireU) {
							for (int i = 0; i < m; ++i) {
								t = cs * U[i][j] + sn * U[i][k - 1];
								U[i][k - 1] = -sn * U[i][j] + cs * U[i][k - 1];
								U[i][j] = t;
							}
						}
					}
					break;
				}

				// Perform the single QR step
				case 3: {
					// Compute the shift
					final double scale = Math
							.max(Math.max(Math.max(Math.max(Math.abs(s[p - 1]), Math.abs(s[p - 2])),
									Math.abs(e[p - 2])), Math.abs(s[k])), Math.abs(e[k]));
					final double sp = s[p - 1] / scale;
					final double spm1 = s[p - 2] / scale;
					final double epm1 = e[p - 2] / scale;
					final double sk = s[k] / scale;
					final double ek = e[k] / scale;
					final double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.;
					final double c = sp * epm1 * (sp * epm1);
					double shift = 0.;
					if (b != 0. | c != 0.) {
						shift = Math.sqrt(b * b + c);
						if (b < 0.) {
							shift = -shift;
						}
						shift = c / (b + shift);
					}
					double f = (sk + sp) * (sk - sp) + shift;
					double g = sk * ek;

					// Chase the zeros
					for (int j = k; j < p - 1; ++j) {
						double t = Norms.getEuclideanNorm(f, g);
						double cs = f / t;
						double sn = g / t;
						if (j != k) {
							e[j - 1] = t;
						}
						f = cs * s[j] + sn * e[j];
						e[j] = cs * e[j] - sn * s[j];
						g = sn * s[j + 1];
						s[j + 1] = cs * s[j + 1];
						if (requireV) {
							for (int i = 0; i < n; ++i) {
								t = cs * V[i][j] + sn * V[i][j + 1];
								V[i][j + 1] = -sn * V[i][j] + cs * V[i][j + 1];
								V[i][j] = t;
							}
						}
						t = Norms.getEuclideanNorm(f, g);
						cs = f / t;
						sn = g / t;
						s[j] = t;
						f = cs * e[j] + sn * s[j + 1];
						s[j + 1] = -sn * e[j] + cs * s[j + 1];
						g = sn * e[j + 1];
						e[j + 1] = cs * e[j + 1];
						if (requireU && j < m - 1) {
							for (int i = 0; i < m; ++i) {
								t = cs * U[i][j] + sn * U[i][j + 1];
								U[i][j + 1] = -sn * U[i][j] + cs * U[i][j + 1];
								U[i][j] = t;
							}
						}
					}
					e[p - 2] = f;
					// iter += 1;
					break;
				}

				// Convergence
				case 4: {
					// Make the singular values positive
					if (s[k] <= 0.) {
						s[k] = s[k] < 0. ? -s[k] : 0.;
						if (requireV) {
							for (int i = 0; i <= pp; ++i) {
								V[i][k] = -V[i][k];
							}
						}
					}

					// Order the singular values
					while (k < pp) {
						if (s[k] >= s[k + 1]) {
							break;
						}
						double t = s[k];
						s[k] = s[k + 1];
						s[k + 1] = t;
						if (requireV && k < n - 1) {
							for (int i = 0; i < n; ++i) {
								t = V[i][k + 1];
								V[i][k + 1] = V[i][k];
								V[i][k] = t;
							}
						}
						if (requireU && k < m - 1) {
							for (int i = 0; i < m; ++i) {
								t = U[i][k + 1];
								U[i][k + 1] = U[i][k];
								U[i][k] = t;
							}
						}
						++k;
					}
					// iter = 0;
					--p;
					break;
				}
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Matrix} containing the left singular vectors {@code U}.
	 * <p>
	 * @return the {@link Matrix} containing the left singular vectors {@code U}
	 */
	public Matrix getU() {
		return new Matrix(m, Math.min(m + 1, n), U);
	}

	/**
	 * Returns the {@link Matrix} containing the right singular vectors {@code V}.
	 * <p>
	 * @return the {@link Matrix} containing the right singular vectors {@code V}
	 */
	public Matrix getV() {
		return new Matrix(n, n, V);
	}

	/**
	 * Returns the one-dimensional array of singular values {@code s}.
	 * <p>
	 * @return the one-dimensional array of singular values {@code s}
	 */
	public double[] getSingularValues() {
		return s;
	}

	/**
	 * Returns the diagonal {@link Matrix} of the singular values {@code s}.
	 * <p>
	 * @return the diagonal {@link Matrix} of the singular values {@code s}
	 */
	public Matrix getS() {
		final double[][] s = new double[n][n];
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				s[i][j] = 0.;
			}
			s[i][i] = this.s[i];
		}
		return new Matrix(s);
	}

	/**
	 * Returns the two norms.
	 * <p>
	 * @return {@code max(s)}
	 */
	public double norm2() {
		return s[0];
	}

	/**
	 * Returns the two norms condition number.
	 * <p>
	 * @return {@code max(s) / min(s)}
	 */
	public double cond() {
		return s[0] / s[Math.min(m, n) - 1];
	}

	/**
	 * Returns the effective numerical matrix rank.
	 * <p>
	 * @return the number of non-negligible singular values
	 */
	public int rank() {
		final double eps = Maths.TOLERANCE;
		final double tolerance = Math.max(m, n) * s[0] * eps;
		int r = 0;
		for (final double value : s) {
			if (value > tolerance) {
				++r;
			}
		}
		return r;
	}
}
