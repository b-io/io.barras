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
package jupiter.math.analysis.interpolation;

import jupiter.common.util.Arrays;
import jupiter.common.util.Characters;
import jupiter.common.util.Strings;

/**
 * Performs a spline interpolation from a specified set of control points.
 */
public class SplineInterpolator {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final double[] x;
	protected final double[] y;
	protected final double[] m;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected SplineInterpolator(final double[] x, final double[] y, final double[] m) {
		this.x = x;
		this.y = y;
		this.m = m;
	}

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
	 * @param x the X component of the control points, strictly increasing
	 * @param y the Y component of the control points
	 * <p>
	 * @return a monotone cubic spline from the specified set of control points
	 * <p>
	 * @throws IllegalArgumentException if the X or Y arrays are {@code null}, have different
	 *                                  lengths or have fewer than 2 values
	 */
	public static SplineInterpolator createMonotoneCubicSpline(final double[] x, final double[] y) {
		if (x == null || y == null || x.length != y.length || x.length < 2) {
			throw new IllegalArgumentException(
					"There must be at least two control points and the arrays must be of equal length");
		}

		final int n = x.length;
		final double[] d = new double[n - 1];
		final double[] m = new double[n];

		// Compute slopes of secant lines between successive points
		for (int i = 0; i < n - 1; ++i) {
			final double h = x[i + 1] - x[i];
			if (h <= 0.) {
				throw new IllegalArgumentException(
						"The control points must all have strictly increasing X values");
			}
			d[i] = (y[i + 1] - y[i]) / h;
		}

		// Initialize the tangents as the average of the secants
		m[0] = d[0];
		for (int i = 1; i < n - 1; ++i) {
			m[i] = (d[i - 1] + d[i]) * 0.5;
		}
		m[n - 1] = d[n - 2];

		// Update the tangents to preserve monotonicity
		for (int i = 0; i < n - 1; ++i) {
			if (d[i] == 0.) {
				// @note successive Y values are equal
				m[i] = 0.;
				m[i + 1] = 0.;
			} else {
				final double a = m[i] / d[i];
				final double b = m[i + 1] / d[i];
				final double h = Math.hypot(a, b);
				if (h > 9.) {
					final double t = 3. / h;
					m[i] = t * a * d[i];
					m[i + 1] = t * b * d[i];
				}
			}
		}
		return new SplineInterpolator(x, y, m);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// INTERPOLATE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Interpolates the value of Y = f(X) for the specified X. Clamps X to the domain of the spline.
	 * <p>
	 * @param value the value to interpolate
	 * <p>
	 * @return the interpolated Y = f(X) value
	 */
	public double interpolate(final double value) {
		// Handle the boundary cases
		final int n = x.length;
		if (Double.isNaN(value)) {
			return value;
		}
		if (value <= x[0]) {
			return y[0];
		}
		if (value >= x[n - 1]) {
			return y[n - 1];
		}

		// Find the index of the last point with smaller X
		int i = 0;
		while (value >= x[i + 1]) {
			i += 1;
			if (value == x[i]) {
				return y[i];
			}
		}

		// Apply the cubic Hermite spline interpolation
		final double h = x[i + 1] - x[i];
		final double t = (value - x[i]) / h;
		return (y[i] * (1 + 2 * t) + h * m[i] * t) * (1 - t) * (1 - t) +
				(y[i + 1] * (3 - 2 * t) + h * m[i + 1] * (t - 1)) * t * t;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		final StringBuilder builder = Strings.createBuilder(10 * x.length * y.length * m.length);
		final int n = x.length;
		for (int i = 0; i < n; ++i) {
			if (i > 0) {
				builder.append(Arrays.DEFAULT_DELIMITER);
			}
			builder.append(Characters.LEFT_PARENTHESIS)
					.append(x[i])
					.append(Arrays.DEFAULT_DELIMITER)
					.append(y[i])
					.append(": ")
					.append(m[i])
					.append(Characters.RIGHT_PARENTHESIS);
		}
		return Strings.bracketize(builder.toString());
	}
}
