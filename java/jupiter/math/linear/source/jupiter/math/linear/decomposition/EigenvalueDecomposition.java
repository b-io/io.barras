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
 * Eigenvalues and eigenvectors of a real matrix.
 * <p>
 * If A is symmetric, then A = V D V' where the eigenvalue matrix D is diagonal and the eigenvector
 * matrix V is orthogonal. I.e. A = V.times(D.times(V.transpose())) and V.times(V.transpose())
 * equals the identity matrix.
 * <p>
 * If A is not symmetric, then the eigenvalue matrix D is block diagonal with the real eigenvalues
 * in (1 x 1) blocks and any complex eigenvalues, lambda + i * mu, in (2 x 2) blocks, [lambda, mu;
 * -mu, lambda]. The columns of V represent the eigenvectors in the sense that A V = V D, i.e.
 * A.times(V) equals V.times(D). The matrix V may be badly conditioned or even singular, so the
 * validity of the equation A = V D inv(V) depends upon V.cond().
 * <p>
 * @author JAMA (http://math.nist.gov/javanumerics/jama)
 * @version 1.0.3
 */
public class EigenvalueDecomposition
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -5766123633823171989L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The row and column dimension (square matrix).
	 */
	protected final int dimension;
	/**
	 * The flag specifying whether {@code A} is symmetric.
	 */
	protected boolean isSymmetric;
	/**
	 * The eigenvalues.
	 */
	protected final double[] d, e;
	/**
	 * The eigenvectors.
	 */
	protected final double[][] V;
	/**
	 * The non-symmetric Hessenberg form.
	 */
	protected double[][] H;
	/**
	 * The internal storage of the non-symmetric algorithm.
	 */
	protected double[] ort;
	/**
	 * The internal storage of the complex scalar division.
	 */
	protected double cdivr, cdivi;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests for symmetry, then constructs the eigenvalue decomposition. Sets {@code D} and
	 * {@code V}.
	 * <p>
	 * @param A a square {@link Matrix}
	 */
	public EigenvalueDecomposition(final Matrix A) {
		// Initialize
		final double[] elements = A.getElements();
		dimension = A.getColumnDimension();
		isSymmetric = true;
		for (int j = 0; j < dimension & isSymmetric; ++j) {
			for (int i = 0; i < dimension & isSymmetric; ++i) {
				isSymmetric = elements[i * dimension + j] == elements[j * dimension + i];
			}
		}
		d = new double[dimension];
		e = new double[dimension];
		V = new double[dimension][dimension];

		// Decompose
		if (isSymmetric) {
			for (int i = 0; i < dimension; ++i) {
				System.arraycopy(elements, i * dimension, V[i], 0, dimension);
			}
			// Tridiagonalize
			tred2();
			// Diagonalize
			tql2();
		} else {
			H = new double[dimension][dimension];
			ort = new double[dimension];
			for (int j = 0; j < dimension; ++j) {
				for (int i = 0; i < dimension; ++i) {
					H[i][j] = elements[i * dimension + j];
				}
			}
			// Reduce to the Hessenberg form
			orthes();
			// Reduce the Hessenberg form to the real Schur form
			hqr2();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the eigenvector matrix {@code V}.
	 * <p>
	 * @return the eigenvector matrix {@code V}
	 */
	public Matrix getV() {
		return new Matrix(dimension, dimension, V);
	}

	/**
	 * Returns the real parts of the eigenvalues.
	 * <p>
	 * @return real(diag({@code D}))
	 */
	public double[] getRealEigenvalues() {
		return d;
	}

	/**
	 * Returns the imaginary parts of the eigenvalues.
	 * <p>
	 * @return imag(diag({@code D}))
	 */
	public double[] getImagEigenvalues() {
		return e;
	}

	/**
	 * Returns the block diagonal eigenvalue matrix {@code D}.
	 * <p>
	 * @return the block diagonal eigenvalue matrix {@code D}
	 */
	public Matrix getD() {
		final double[][] D = new double[dimension][dimension];
		for (int i = 0; i < dimension; ++i) {
			for (int j = 0; j < dimension; ++j) {
				D[i][j] = 0.;
			}
			D[i][i] = d[i];
			if (e[i] > 0) {
				D[i][i + 1] = e[i];
			} else if (e[i] < 0) {
				D[i][i - 1] = e[i];
			}
		}
		return new Matrix(D);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SOLVER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Performs the symmetric Householder reduction to the tridiagonal form.
	 * <p>
	 * See the Algol procedures tred2 by Bowdler, Martin, Reinsch and Wilkinson, Handbook for Auto.
	 * Comp., Vol.ii-Linear Algebra, and the corresponding Fortran subroutine in EISPACK.
	 */
	protected void tred2() {
		// Initialize
		System.arraycopy(V[dimension - 1], 0, d, 0, dimension);

		// Iterate over the row and column index
		for (int i = dimension - 1; i > 0; --i) {
			// Scale to avoid under/overflow
			double scale = 0.;
			double h = 0.;
			for (int k = 0; k < i; ++k) {
				scale += Math.abs(d[k]);
			}
			if (scale == 0.) {
				e[i] = d[i - 1];
				for (int j = 0; j < i; ++j) {
					d[j] = V[i - 1][j];
					V[i][j] = 0.;
					V[j][i] = 0.;
				}
			} else {
				// Create the Householder vector
				for (int k = 0; k < i; ++k) {
					d[k] /= scale;
					h += d[k] * d[k];
				}
				double f = d[i - 1];
				double g = Math.sqrt(h);
				if (f > 0) {
					g = -g;
				}
				e[i] = scale * g;
				h -= f * g;
				d[i - 1] = f - g;
				for (int j = 0; j < i; ++j) {
					e[j] = 0.;
				}
				// Apply the similarity transformation to the remaining columns
				for (int j = 0; j < i; ++j) {
					f = d[j];
					V[j][i] = f;
					g = e[j] + V[j][j] * f;
					for (int k = j + 1; k <= i - 1; ++k) {
						g += V[k][j] * d[k];
						e[k] += V[k][j] * f;
					}
					e[j] = g;
				}
				f = 0.;
				for (int j = 0; j < i; ++j) {
					e[j] /= h;
					f += e[j] * d[j];
				}
				final double hh = f / (h + h);
				for (int j = 0; j < i; ++j) {
					e[j] -= hh * d[j];
				}
				for (int j = 0; j < i; ++j) {
					f = d[j];
					g = e[j];
					for (int k = j; k <= i - 1; ++k) {
						V[k][j] -= f * e[k] + g * d[k];
					}
					d[j] = V[i - 1][j];
					V[i][j] = 0.;
				}
			}
			d[i] = h;
		}

		// Accumulate the transformations
		for (int i = 0; i < dimension - 1; ++i) {
			V[dimension - 1][i] = V[i][i];
			V[i][i] = 1.;
			final double h = d[i + 1];
			if (h != 0.) {
				for (int k = 0; k <= i; ++k) {
					d[k] = V[k][i + 1] / h;
				}
				for (int j = 0; j <= i; ++j) {
					double g = 0.;
					for (int k = 0; k <= i; ++k) {
						g += V[k][i + 1] * V[k][j];
					}
					for (int k = 0; k <= i; ++k) {
						V[k][j] -= g * d[k];
					}
				}
			}
			for (int k = 0; k <= i; ++k) {
				V[k][i + 1] = 0.;
			}
		}
		for (int j = 0; j < dimension; ++j) {
			d[j] = V[dimension - 1][j];
			V[dimension - 1][j] = 0.;
		}
		V[dimension - 1][dimension - 1] = 1.;
		e[0] = 0.;
	}

	/**
	 * Performs the symmetric tridiagonal QL algorithm.
	 * <p>
	 * See the Algol procedures tql2, by Bowdler, Martin, Reinsch and Wilkinson, Handbook for Auto.
	 * Comp., Vol.ii-Linear Algebra, and the corresponding Fortran subroutine in EISPACK.
	 */
	protected void tql2() {
		// Initialize
		for (int i = 1; i < dimension; ++i) {
			e[i - 1] = e[i];
		}
		e[dimension - 1] = 0.;
		double f = 0.;
		double tst1 = 0.;
		final double eps = Maths.DEFAULT_TOLERANCE;

		// Iterate over the row and column index
		for (int l = 0; l < dimension; ++l) {
			// Find a small subdiagonal element
			tst1 = Math.max(tst1, Math.abs(d[l]) + Math.abs(e[l]));
			int m = l;
			while (m < dimension && Math.abs(e[m]) > eps * tst1) {
				++m;
			}
			// If m == l, d[l] is an eigenvalue, iterate otherwise
			if (m > l) {
				// int iter = 0;
				do {
					// iter += 1; // @todo test to avoid too many iterations
					// Apply the implicit shift
					double g = d[l];
					double p = (d[l + 1] - g) / (2. * e[l]);
					double r = Norms.getEuclideanNorm(p, 1.);
					if (p < 0) {
						r = -r;
					}
					d[l] = e[l] / (p + r);
					d[l + 1] = e[l] * (p + r);
					final double dl1 = d[l + 1];
					double h = g - d[l];
					for (int i = l + 2; i < dimension; ++i) {
						d[i] -= h;
					}
					f += h;
					// Apply the implicit QL transformation
					p = d[m];
					double c = 1.;
					double c2 = c;
					double c3 = c;
					final double el1 = e[l + 1];
					double s = 0.;
					double s2 = 0.;
					for (int i = m - 1; i >= l; --i) {
						c3 = c2;
						c2 = c;
						s2 = s;
						g = c * e[i];
						h = c * p;
						r = Norms.getEuclideanNorm(p, e[i]);
						e[i + 1] = s * r;
						s = e[i] / r;
						c = p / r;
						p = c * d[i] - s * g;
						d[i + 1] = h + s * (c * g + s * d[i]);
						// Accumulate the transformations
						for (int k = 0; k < dimension; ++k) {
							h = V[k][i + 1];
							V[k][i + 1] = s * V[k][i] + c * h;
							V[k][i] = c * V[k][i] - s * h;
						}
					}
					p = -s * s2 * c3 * el1 * e[l] / dl1;
					e[l] = s * p;
					d[l] = c * p;
				} while (Math.abs(e[l]) > eps * tst1); // test the convergence
			}
			d[l] += f;
			e[l] = 0.;
		}

		// Sort the eigenvalues and the corresponding vectors
		for (int i = 0; i < dimension - 1; ++i) {
			int k = i;
			double p = d[i];
			for (int j = i + 1; j < dimension; ++j) {
				if (d[j] < p) {
					k = j;
					p = d[j];
				}
			}
			if (k != i) {
				d[k] = d[i];
				d[i] = p;
				for (int j = 0; j < dimension; ++j) {
					p = V[j][i];
					V[j][i] = V[j][k];
					V[j][k] = p;
				}
			}
		}
	}

	/**
	 * Performs the non-symmetric reduction to the Hessenberg form.
	 * <p>
	 * See the Algol procedures orthes and ortran, by Martin and Wilkinson, Handbook for Auto.
	 * Comp., Vol.ii-Linear Algebra, and the corresponding Fortran subroutines in EISPACK.
	 */
	protected void orthes() {
		// Initialize
		final int low = 0;
		final int high = dimension - 1;

		// Iterate over the row and column index
		for (int m = low + 1; m <= high - 1; ++m) {
			// Scale the column
			double scale = 0.;
			for (int i = m; i <= high; ++i) {
				scale += Math.abs(H[i][m - 1]);
			}
			if (scale != 0.) {
				// Apply the Householder transformation
				double h = 0.;
				for (int i = high; i >= m; --i) {
					ort[i] = H[i][m - 1] / scale;
					h += ort[i] * ort[i];
				}
				double g = Math.sqrt(h);
				if (ort[m] > 0) {
					g = -g;
				}
				h -= ort[m] * g;
				ort[m] -= g;
				// Apply the Householder similarity transformation
				// H := (I - u * u' / h) * H * (I - u * u' * / h)
				for (int j = m; j < dimension; ++j) {
					double f = 0.;
					for (int i = high; i >= m; --i) {
						f += ort[i] * H[i][j];
					}
					f /= h;
					for (int i = m; i <= high; ++i) {
						H[i][j] -= f * ort[i];
					}
				}
				for (int i = 0; i <= high; ++i) {
					double f = 0.;
					for (int j = high; j >= m; --j) {
						f += ort[j] * H[i][j];
					}
					f /= h;
					for (int j = m; j <= high; ++j) {
						H[i][j] -= f * ort[j];
					}
				}
				ort[m] = scale * ort[m];
				H[m][m - 1] = scale * g;
			}
		}

		// Accumulate the transformations (Algol's ortran)
		for (int i = 0; i < dimension; ++i) {
			for (int j = 0; j < dimension; ++j) {
				V[i][j] = i == j ? 1. : 0.;
			}
		}
		for (int m = high - 1; m >= low + 1; --m) {
			if (H[m][m - 1] != 0.) {
				for (int i = m + 1; i <= high; ++i) {
					ort[i] = H[i][m - 1];
				}
				for (int j = m; j <= high; ++j) {
					double g = 0.;
					for (int i = m; i <= high; ++i) {
						g += ort[i] * V[i][j];
					}
					// @note use a double division to avoid possible underflows
					g = g / ort[m] / H[m][m - 1];
					for (int i = m; i <= high; ++i) {
						V[i][j] += g * ort[i];
					}
				}
			}
		}
	}

	/**
	 * Complex scalar division.
	 * <p>
	 * @param xr the real part of the dividend
	 * @param xi the imaginary part of the dividend
	 * @param yr the real part of the divisor
	 * @param yi the imaginary part of the divisor
	 */
	protected void cdiv(final double xr, final double xi, final double yr, final double yi) {
		final double r, d;
		if (Math.abs(yr) > Math.abs(yi)) {
			r = yi / yr;
			d = yr + r * yi;
			cdivr = (xr + r * xi) / d;
			cdivi = (xi - r * xr) / d;
		} else {
			r = yr / yi;
			d = yi + r * yr;
			cdivr = (r * xr + xi) / d;
			cdivi = (r * xi - xr) / d;
		}
	}

	/**
	 * Performs the non-symmetric reduction from the Hessenberg form to the real Schur form.
	 * <p>
	 * See the Algol procedure hqr2, by Martin and Wilkinson, Handbook for Auto. Comp.,
	 * Vol.ii-Linear Algebra, and the corresponding Fortran subroutine in EISPACK.
	 */
	protected void hqr2() {
		// Initialize
		final int nn = dimension;
		int n = nn - 1;
		final int low = 0;
		final int high = nn - 1;
		final double eps = Maths.DEFAULT_TOLERANCE; // the relative accuracy
		double exshift = 0.;
		double p = 0, q = 0, r = 0, s = 0, z = 0, t, w, x, y;

		// Store the roots isolated by balance and compute the matrix norm
		double norm = 0.;
		for (int i = 0; i < nn; ++i) {
			if (i < low | i > high) {
				d[i] = H[i][i];
				e[i] = 0.;
			}
			for (int j = Math.max(0, i - 1); j < nn; ++j) {
				norm += Math.abs(H[i][j]);
			}
		}

		// Iterate over the eigenvalue index
		int iter = 0;
		while (n >= low) {
			// Find a single small sub-diagonal element
			int l = n;
			while (l > low) {
				s = Math.abs(H[l - 1][l - 1]) + Math.abs(H[l][l]);
				if (s == 0.) {
					s = norm;
				}
				if (Math.abs(H[l][l - 1]) < eps * s) {
					break;
				}
				--l;
			}

			// Test the convergence
			if (l == n) {
				// - 1 root found
				H[n][n] += exshift;
				d[n] = H[n][n];
				e[n] = 0.;
				--n;
				iter = 0;
			} else if (l == n - 1) {
				// - 2 roots found
				w = H[n][n - 1] * H[n - 1][n];
				p = (H[n - 1][n - 1] - H[n][n]) / 2.;
				q = p * p + w;
				z = Math.sqrt(Math.abs(q));
				H[n][n] += exshift;
				H[n - 1][n - 1] += exshift;
				x = H[n][n];
				// Test whether the pair is real or complex
				if (q >= 0) {
					// - Real pair
					if (p >= 0) {
						z = p + z;
					} else {
						z = p - z;
					}
					d[n - 1] = x + z;
					d[n] = d[n - 1];
					if (z != 0.) {
						d[n] = x - w / z;
					}
					e[n - 1] = 0.;
					e[n] = 0.;
					x = H[n][n - 1];
					s = Math.abs(x) + Math.abs(z);
					p = x / s;
					q = z / s;
					r = Math.sqrt(p * p + q * q);
					p /= r;
					q /= r;
					// Modify the rows
					for (int j = n - 1; j < nn; ++j) {
						z = H[n - 1][j];
						H[n - 1][j] = q * z + p * H[n][j];
						H[n][j] = q * H[n][j] - p * z;
					}
					// Modify the columns
					for (int i = 0; i <= n; ++i) {
						z = H[i][n - 1];
						H[i][n - 1] = q * z + p * H[i][n];
						H[i][n] = q * H[i][n] - p * z;
					}
					// Accumulate the transformations
					for (int i = low; i <= high; ++i) {
						z = V[i][n - 1];
						V[i][n - 1] = q * z + p * V[i][n];
						V[i][n] = q * V[i][n] - p * z;
					}
				} else {
					// - Complex pair
					d[n - 1] = x + p;
					d[n] = x + p;
					e[n - 1] = z;
					e[n] = -z;
				}
				n -= 2;
				iter = 0;
			} else {
				// - No convergence yet
				// Apply form shift
				x = H[n][n];
				y = 0.;
				w = 0.;
				if (l < n) {
					y = H[n - 1][n - 1];
					w = H[n][n - 1] * H[n - 1][n];
				}
				// Apply Wilkinson's original ad hoc shift
				if (iter == 10) {
					exshift += x;
					for (int i = low; i <= n; ++i) {
						H[i][i] -= x;
					}
					s = Math.abs(H[n][n - 1]) + Math.abs(H[n - 1][n - 2]);
					x = y = 0.75 * s;
					w = -0.4375 * s * s;
				}
				// Apply MATLAB's new ad hoc shift
				if (iter == 30) {
					s = (y - x) / 2.;
					s = s * s + w;
					if (s > 0) {
						s = Math.sqrt(s);
						if (y < x) {
							s = -s;
						}
						s = x - w / ((y - x) / 2. + s);
						for (int i = low; i <= n; ++i) {
							H[i][i] -= s;
						}
						exshift += s;
						x = y = w = 0.964;
					}
				}
				iter += 1; // @todo test to avoid too many iterations
				// Find two consecutive small sub-diagonal elements
				int m = n - 2;
				while (m >= l) {
					z = H[m][m];
					r = x - z;
					s = y - z;
					p = (r * s - w) / H[m + 1][m] + H[m][m + 1];
					q = H[m + 1][m + 1] - z - r - s;
					r = H[m + 2][m + 1];
					s = Math.abs(p) + Math.abs(q) + Math.abs(r);
					p /= s;
					q /= s;
					r /= s;
					if (m == l) {
						break;
					}
					if (Math.abs(H[m][m - 1]) * (Math.abs(q) + Math.abs(r)) < eps *
							(Math.abs(p) * (Math.abs(H[m - 1][m - 1]) + Math.abs(z) +
									Math.abs(H[m + 1][m + 1])))) {
						break;
					}
					--m;
				}
				for (int i = m + 2; i <= n; ++i) {
					H[i][i - 2] = 0.;
					if (i > m + 2) {
						H[i][i - 3] = 0.;
					}
				}
				// Perform the double QR step involving the rows l:dimension and the columns m:dimension
				for (int k = m; k <= n - 1; ++k) {
					final boolean isNotLast = k != n - 1;
					if (k != m) {
						p = H[k][k - 1];
						q = H[k + 1][k - 1];
						r = isNotLast ? H[k + 2][k - 1] : 0.;
						x = Math.abs(p) + Math.abs(q) + Math.abs(r);
						if (x == 0.) {
							continue;
						}
						p /= x;
						q /= x;
						r /= x;
					}
					s = Math.sqrt(p * p + q * q + r * r);
					if (p < 0) {
						s = -s;
					}
					if (s != 0.) {
						if (k != m) {
							H[k][k - 1] = -s * x;
						} else if (l != m) {
							H[k][k - 1] = -H[k][k - 1];
						}
						p += s;
						x = p / s;
						y = q / s;
						z = r / s;
						q /= p;
						r /= p;
						// Modify the rows
						for (int j = k; j < nn; ++j) {
							p = H[k][j] + q * H[k + 1][j];
							if (isNotLast) {
								p += r * H[k + 2][j];
								H[k + 2][j] -= p * z;
							}
							H[k][j] -= p * x;
							H[k + 1][j] -= p * y;
						}
						// Modify the columns
						for (int i = 0; i <= Math.min(k + 3, n); ++i) {
							p = x * H[i][k] + y * H[i][k + 1];
							if (isNotLast) {
								p += z * H[i][k + 2];
								H[i][k + 2] -= p * r;
							}
							H[i][k] -= p;
							H[i][k + 1] -= p * q;
						}
						// Accumulate the transformations
						for (int i = low; i <= high; ++i) {
							p = x * V[i][k] + y * V[i][k + 1];
							if (isNotLast) {
								p += z * V[i][k + 2];
								V[i][k + 2] -= p * r;
							}
							V[i][k] -= p;
							V[i][k + 1] -= p * q;
						}
					} // (s != 0.)
				} // k loop
			} // test the convergence
		} // while (dimension >= low)

		// Backsubstitute to find the vectors of the upper triangular form
		if (norm == 0.) {
			return;
		}
		for (n = nn - 1; n >= 0; --n) {
			p = d[n];
			q = e[n];
			// Test whether the vector is real or complex
			if (q == 0) {
				// - Real vector
				int l = n;
				H[n][n] = 1.;
				for (int i = n - 1; i >= 0; --i) {
					w = H[i][i] - p;
					r = 0.;
					for (int j = l; j <= n; ++j) {
						r += H[i][j] * H[j][n];
					}
					if (e[i] < 0.) {
						z = w;
						s = r;
					} else {
						l = i;
						if (e[i] == 0.) {
							if (w != 0.) {
								H[i][n] = -r / w;
							} else {
								H[i][n] = -r / (eps * norm);
							}
						} else {
							// Solve real equations
							x = H[i][i + 1];
							y = H[i + 1][i];
							q = (d[i] - p) * (d[i] - p) + e[i] * e[i];
							t = (x * s - z * r) / q;
							H[i][n] = t;
							if (Math.abs(x) > Math.abs(z)) {
								H[i + 1][n] = (-r - w * t) / x;
							} else {
								H[i + 1][n] = (-s - y * t) / z;
							}
						}
						// Control under/overflow
						t = Math.abs(H[i][n]);
						if (eps * t * t > 1) {
							for (int j = i; j <= n; ++j) {
								H[j][n] /= t;
							}
						}
					}
				}
			} else if (q < 0) {
				// - Complex vector
				int l = n - 1;
				// @note the last vector element is imaginary so the matrix is triangular
				if (Math.abs(H[n][n - 1]) > Math.abs(H[n - 1][n])) {
					H[n - 1][n - 1] = q / H[n][n - 1];
					H[n - 1][n] = -(H[n][n] - p) / H[n][n - 1];
				} else {
					cdiv(0., -H[n - 1][n], H[n - 1][n - 1] - p, q);
					H[n - 1][n - 1] = cdivr;
					H[n - 1][n] = cdivi;
				}
				H[n][n - 1] = 0.;
				H[n][n] = 1.;
				for (int i = n - 2; i >= 0; --i) {
					double ra, sa, vr, vi;
					ra = 0.;
					sa = 0.;
					for (int j = l; j <= n; ++j) {
						ra += H[i][j] * H[j][n - 1];
						sa += H[i][j] * H[j][n];
					}
					w = H[i][i] - p;
					if (e[i] < 0.) {
						z = w;
						r = ra;
						s = sa;
					} else {
						l = i;
						if (e[i] == 0) {
							cdiv(-ra, -sa, w, q);
							H[i][n - 1] = cdivr;
							H[i][n] = cdivi;
						} else {
							// Solve complex equations
							x = H[i][i + 1];
							y = H[i + 1][i];
							vr = (d[i] - p) * (d[i] - p) + e[i] * e[i] - q * q;
							vi = (d[i] - p) * 2. * q;
							if (vr == 0. & vi == 0.) {
								vr = eps * norm * (Math.abs(w) + Math.abs(q) + Math.abs(x) +
										Math.abs(y) + Math.abs(z));
							}
							cdiv(x * r - z * ra + q * sa, x * s - z * sa - q * ra, vr, vi);
							H[i][n - 1] = cdivr;
							H[i][n] = cdivi;
							if (Math.abs(x) > Math.abs(z) + Math.abs(q)) {
								H[i + 1][n - 1] = (-ra - w * H[i][n - 1] + q * H[i][n]) / x;
								H[i + 1][n] = (-sa - w * H[i][n] - q * H[i][n - 1]) / x;
							} else {
								cdiv(-r - y * H[i][n - 1], -s - y * H[i][n], z, q);
								H[i + 1][n - 1] = cdivr;
								H[i + 1][n] = cdivi;
							}
						}
						// Control under/overflow
						t = Math.max(Math.abs(H[i][n - 1]), Math.abs(H[i][n]));
						if (eps * t * t > 1) {
							for (int j = i; j <= n; ++j) {
								H[j][n - 1] /= t;
								H[j][n] /= t;
							}
						}
					}
				}
			}
		}

		// Store the vectors of the isolated roots
		for (int i = 0; i < nn; ++i) {
			if (i < low | i > high) {
				System.arraycopy(H[i], i, V[i], i, nn - i);
			}
		}

		// Apply the back transformation to get the eigenvectors of the original matrix
		for (int j = nn - 1; j >= low; --j) {
			for (int i = low; i <= high; ++i) {
				z = 0.;
				for (int k = low; k <= Math.min(j, high); ++k) {
					z += V[i][k] * H[k][j];
				}
				V[i][j] = z;
			}
		}
	}
}
