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
package jupiter.math.analysis.interpolation;

import static jupiter.common.util.Characters.LEFT_PARENTHESIS;
import static jupiter.common.util.Characters.RIGHT_PARENTHESIS;

import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link SplineInterpolator} performs a spline interpolation from a specified set of control
 * points.
 */
public class SplineInterpolator
		implements ICloneable<SplineInterpolator>, Serializable {

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
	 * The {@code X}-component of the control points, strictly increasing.
	 */
	protected double[] X;
	/**
	 * The {@code Y}-component of the control points.
	 */
	protected double[] Y;
	/**
	 * The tangents {@code M}.
	 */
	protected double[] M;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SplineInterpolator} with the specified {@code X}- and
	 * {@code Y}-components of the control points and tangents.
	 * <p>
	 * @param X the {@code X}-component of the control points, strictly increasing
	 * @param Y the {@code Y}-component of the control points
	 * @param M the tangents
	 */
	protected SplineInterpolator(final double[] X, final double[] Y, final double[] M) {
		this.X = X;
		this.Y = Y;
		this.M = M;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a monotone cubic spline from the specified set of control points.
	 * <p>
	 * The spline is guaranteed to pass through each control point exactly. Moreover, assuming the
	 * control points are monotonic (Y is non-decreasing or non-increasing) then the interpolated
	 * values are also monotonic.
	 * <p>
	 * This function uses the Fritsch-Carlson method for computing the spline parameters.
	 * http://en.wikipedia.org/wiki/Monotone_cubic_interpolation
	 * <p>
	 * @param X the {@code X}-component of the control points, strictly increasing
	 * @param Y the {@code Y}-component of the control points
	 * <p>
	 * @return a monotone cubic spline from the specified set of control points
	 * <p>
	 * @throws IllegalArgumentException if {@code X} or {@code Y} is {@code null}, have different
	 *                                  lengths or have fewer than 2 values
	 */
	public static SplineInterpolator createMonotoneCubicSpline(final double[] X, final double[] Y) {
		if (X == null || Y == null || X.length != Y.length || X.length < 2) {
			throw new IllegalArgumentException(
					"The arrays must be of equal length and there must be at least two control points");
		}

		final int n = X.length;
		final double[] D = new double[n - 1];
		final double[] M = new double[n];

		// Compute the slopes of the secant lines between the successive points
		for (int i = 0; i < n - 1; ++i) {
			final double h = X[i + 1] - X[i];
			if (h <= 0.) {
				throw new IllegalArgumentException(
						"The control points must all have strictly increasing X values");
			}
			D[i] = (Y[i + 1] - Y[i]) / h;
		}

		// Initialize the tangents as the average of the secant lines
		M[0] = D[0];
		for (int i = 1; i < n - 1; ++i) {
			M[i] = 0.5 * (D[i - 1] + D[i]);
		}
		M[n - 1] = D[n - 2];

		// Update the tangents to preserve monotonicity
		for (int i = 0; i < n - 1; ++i) {
			if (D[i] == 0.) {
				// @note successive Y values are equal
				M[i] = 0.;
				M[i + 1] = 0.;
			} else {
				final double a = M[i] / D[i];
				final double b = M[i + 1] / D[i];
				final double h = Math.hypot(a, b);
				if (h > 9.) {
					final double t = 3. / h;
					M[i] = t * a * D[i];
					M[i + 1] = t * b * D[i];
				}
			}
		}
		return new SplineInterpolator(X, Y, M);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the interpolated {@code double} value of {@code Y = f(X)} for the specified {@code x}
	 * value. Clamps {@code x} to the domain of the spline.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return the interpolated {@code double} value of {@code Y = f(X)} for the specified {@code x}
	 *         value
	 */
	public double interpolate(final double x) {
		// Handle the boundary cases
		final int n = X.length;
		if (Double.isNaN(x)) {
			return x;
		}
		if (x <= X[0]) {
			return Y[0];
		}
		if (x >= X[n - 1]) {
			return Y[n - 1];
		}

		// Find the index of the last point with smaller X
		int i = 0;
		while (x >= X[i + 1]) {
			++i;
			if (x == X[i]) {
				return Y[i];
			}
		}

		// Apply the cubic Hermite spline interpolation
		final double h = X[i + 1] - X[i];
		final double t = (x - X[i]) / h;
		return (Y[i] * (1. + 2. * t) + h * M[i] * t) * (1. - t) * (1. - t) +
				(Y[i + 1] * (3. - 2. * t) + h * M[i + 1] * (t - 1)) * t * t;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public SplineInterpolator clone() {
		try {
			final SplineInterpolator clone = (SplineInterpolator) super.clone();
			clone.X = Objects.clone(X);
			clone.Y = Objects.clone(Y);
			clone.M = Objects.clone(M);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = Strings.createBuilder(10 * X.length * Y.length * M.length);
		final int n = X.length;
		for (int i = 0; i < n; ++i) {
			if (i > 0) {
				builder.append(Arrays.DELIMITER);
			}
			builder.append(LEFT_PARENTHESIS)
					.append(X[i])
					.append(Arrays.DELIMITER)
					.append(Y[i])
					.append(": ")
					.append(M[i])
					.append(RIGHT_PARENTHESIS);
		}
		return Strings.bracketize(builder.toString());
	}
}
