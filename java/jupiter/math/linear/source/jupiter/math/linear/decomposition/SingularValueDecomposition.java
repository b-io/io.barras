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

import jupiter.common.math.Maths;
import jupiter.math.linear.entity.Matrix;

/**
 * {@link SingularValueDecomposition} performs a singular value decomposition on a {@link Matrix}.
 * <p>
 * For a {@code m x n} matrix {@code A} with {@code m {@literal >}= n}, the singular value
 * decomposition is a {@code m x n} orthogonal matrix {@code U}, a {@code n x n} diagonal matrix
 * {@code S} and a {@code n x n} orthogonal matrix {@code V} so that {@code A = U S V'}.
 * <p>
 * The singular values, {@code sigma[k] = S[k][k]}, are ordered so that
 * {@code sigma[0] {@literal >}= sigma[1] {@literal >}= ... {@literal >}= sigma[n-1]}.
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
	 * The decomposition {@code U} and {@code V}.
	 */
	protected final double[][] U, V;
	/**
	 * The singular values {@code sigma}.
	 */
	protected final double[] sigma;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SingularValueDecomposition} of the specified rectangular {@link Matrix}.
	 * Sets the decomposition {@code U} and {@code V} and the singular values {@code sigma}.
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
		 * {@code if (m < n) { throw new UnsupportedOperationException("Work only for m >= n"); }}
		 */
		final int nu = Math.min(m, n);
		sigma = new double[Math.min(m + 1, n)];
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
		final int limit = Math.max(nct, nrt);
		for (int k = 0; k < limit; ++k) {
			if (k < nct) {
				// Apply the transformation for the k-th column and place the k-th diagonal in s[k]
				// Compute the 2-norm of the k-th column without under/overflow
				sigma[k] = 0;
				for (int i = k; i < m; ++i) {
					sigma[k] = Norms.euclideanNorm(sigma[k], elements[i * n + k]);
				}
				if (sigma[k] != 0.) {
					if (elements[k * n + k] < 0.) {
						sigma[k] = -sigma[k];
					}
					for (int i = k; i < m; ++i) {
						elements[i * n + k] /= sigma[k];
					}
					elements[k * n + k] += 1.;
				}
				sigma[k] = -sigma[k];
			}
			for (int j = k + 1; j < n; ++j) {
				if (k < nct && sigma[k] != 0.) {
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
				// for the subsequent computation of the row transformation
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
					e[k] = Norms.euclideanNorm(e[k], e[i]);
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

		// Set up the final bidiagonal matrix of order p
		int p = Math.min(m + 1, n);
		if (nct < n) {
			sigma[nct] = elements[nct * n + nct];
		}
		if (m < p) {
			sigma[p - 1] = 0.;
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
				if (sigma[k] != 0.) {
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
		while (p > 0) {
			int k, kase;
			// @todo test to avoid too many iterations
			/*
			 * This section of the program inspects for negligible elements in the s and e arrays.
			 * On completion the variables kase and k are set as follows:
			 * • kase = 1 if s(p) and e[k-1] are negligible and k < p,
			 * • kase = 2 if s(k) is negligible and k < p,
			 * • kase = 3 if e[k-1] is negligible, k < p and s(k), ..., s(p) are not negligible (qr step), or
			 * • kase = 4 if e(p-1) is negligible (convergence).
			 */
			for (k = p - 2; k >= -1; --k) {
				if (k == -1) {
					break;
				}
				if (Maths.abs(e[k]) <= Maths.TOLERANCE *
						(Maths.abs(sigma[k]) + Maths.abs(sigma[k + 1])) + Maths.TINY_TOLERANCE) {
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
					final double t = (ks != p ? Maths.abs(e[ks]) : 0.) +
							(ks != k + 1 ? Maths.abs(e[ks - 1]) : 0.);
					if (Maths.abs(sigma[ks]) <= Maths.TOLERANCE * t + Maths.TINY_TOLERANCE) {
						sigma[ks] = 0.;
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
						double t = Norms.euclideanNorm(sigma[j], f);
						final double cs = sigma[j] / t;
						final double sn = f / t;
						sigma[j] = t;
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
						double t = Norms.euclideanNorm(sigma[j], f);
						final double cs = sigma[j] / t;
						final double sn = f / t;
						sigma[j] = t;
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
					final double scale = Maths.max(Maths.abs(sigma[p - 1]),
							Maths.abs(sigma[p - 2]), Maths.abs(e[p - 2]),
							Maths.abs(sigma[k]), Maths.abs(e[k]));
					final double sp = sigma[p - 1] / scale;
					final double spm1 = sigma[p - 2] / scale;
					final double epm1 = e[p - 2] / scale;
					final double sk = sigma[k] / scale;
					final double ek = e[k] / scale;
					final double b = Maths.mean((spm1 + sp) * (spm1 - sp), epm1 * epm1);
					final double c = sp * epm1 * (sp * epm1);
					double shift = 0.;
					if (b != 0. | c != 0.) {
						shift = Maths.sqrt(b * b + c);
						if (b < 0.) {
							shift = -shift;
						}
						shift = c / (b + shift);
					}
					double f = (sk + sp) * (sk - sp) + shift;
					double g = sk * ek;

					// Chase the zeros
					for (int j = k; j < p - 1; ++j) {
						double t = Norms.euclideanNorm(f, g);
						double cs = f / t;
						double sn = g / t;
						if (j != k) {
							e[j - 1] = t;
						}
						f = cs * sigma[j] + sn * e[j];
						e[j] = cs * e[j] - sn * sigma[j];
						g = sn * sigma[j + 1];
						sigma[j + 1] = cs * sigma[j + 1];
						if (requireV) {
							for (int i = 0; i < n; ++i) {
								t = cs * V[i][j] + sn * V[i][j + 1];
								V[i][j + 1] = -sn * V[i][j] + cs * V[i][j + 1];
								V[i][j] = t;
							}
						}
						t = Norms.euclideanNorm(f, g);
						cs = f / t;
						sn = g / t;
						sigma[j] = t;
						f = cs * e[j] + sn * sigma[j + 1];
						sigma[j + 1] = -sn * e[j] + cs * sigma[j + 1];
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
					//++iter;
					break;
				}

				// Convergence
				case 4: {
					// Make the singular values positive
					if (sigma[k] <= 0.) {
						sigma[k] = sigma[k] < 0. ? -sigma[k] : 0.;
						if (requireV) {
							for (int i = 0; i <= pp; ++i) {
								V[i][k] = -V[i][k];
							}
						}
					}

					// Order the singular values
					while (k < pp) {
						if (sigma[k] >= sigma[k + 1]) {
							break;
						}
						double value = sigma[k];
						sigma[k] = sigma[k + 1];
						sigma[k + 1] = value;
						if (requireV && k < n - 1) {
							for (int i = 0; i < n; ++i) {
								value = V[i][k + 1];
								V[i][k + 1] = V[i][k];
								V[i][k] = value;
							}
						}
						if (requireU && k < m - 1) {
							for (int i = 0; i < m; ++i) {
								value = U[i][k + 1];
								U[i][k + 1] = U[i][k];
								U[i][k] = value;
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
	// ACCESSORS
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
	 * Returns the array of singular values {@code sigma}.
	 * <p>
	 * @return the array of singular values {@code sigma}
	 */
	public double[] getSingularValues() {
		return sigma;
	}

	/**
	 * Returns the diagonal {@link Matrix} of the singular values {@code sigma}.
	 * <p>
	 * @return the diagonal {@link Matrix} of the singular values {@code sigma}
	 */
	public Matrix getS() {
		final double[][] S = new double[n][n];
		for (int i = 0; i < n; ++i) {
			for (int j = 0; j < n; ++j) {
				S[i][j] = 0.;
			}
			S[i][i] = sigma[i];
		}
		return new Matrix(S);
	}

	/**
	 * Returns the two norms.
	 * <p>
	 * @return {@code max(s)}
	 */
	public double norm2() {
		return sigma[0];
	}

	/**
	 * Returns the two norms condition number.
	 * <p>
	 * @return {@code max(s) / min(s)}
	 */
	public double cond() {
		return sigma[0] / sigma[Math.min(m, n) - 1];
	}

	/**
	 * Returns the effective numerical matrix rank.
	 * <p>
	 * @return the number of non-negligible singular values
	 */
	public int rank() {
		final double eps = Maths.TOLERANCE;
		final double tolerance = Math.max(m, n) * sigma[0] * eps;
		int r = 0;
		for (final double value : sigma) {
			if (value > tolerance) {
				++r;
			}
		}
		return r;
	}
}
