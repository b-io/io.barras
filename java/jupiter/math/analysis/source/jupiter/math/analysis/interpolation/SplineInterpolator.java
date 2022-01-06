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
package jupiter.math.analysis.interpolation;

import static jupiter.common.util.Characters.COLON;
import static jupiter.common.util.Characters.LEFT_PARENTHESIS;
import static jupiter.common.util.Characters.RIGHT_PARENTHESIS;
import static jupiter.common.util.Strings.INITIAL_CAPACITY;

import jupiter.common.math.Maths;
import jupiter.common.model.ICloneable;
import jupiter.common.test.DoubleArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Doubles;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.math.analysis.struct.XY;

/**
 * {@link SplineInterpolator} is the monotone cubic spline {@link Interpolator} interpolating
 * {@code y = f(x)} between two endpoints with a set of control points and tangents.
 */
public class SplineInterpolator
		extends Interpolator {

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
	 * The {@code double} x-coordinates of the control points, strictly increasing.
	 */
	protected double[] X;
	/**
	 * The {@code double} y-coordinates of the control points.
	 */
	protected double[] Y;
	/**
	 * The {@code double} tangents.
	 */
	protected double[] M;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SplineInterpolator} with the specified {@code XY}-coordinates of the
	 * control points and tangents.
	 * <p>
	 * @param X the {@code double} x-coordinates of the control points, strictly increasing
	 * @param Y the {@code double} y-coordinates of the control points
	 * @param M the {@code double} tangents
	 */
	protected SplineInterpolator(final double[] X, final double[] Y, final double[] M) {
		super(new XY<Double>(X[0], Y[0]), new XY<Double>(X[X.length - 1], Y[Y.length - 1]));
		this.X = X;
		this.Y = Y;
		this.M = M;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link SplineInterpolator} from the specified set of control points.
	 * <p>
	 * The spline is guaranteed to pass through each control point exactly. Moreover, assuming the
	 * control points are monotonic ({@code Y} is non-decreasing or non-increasing) then the
	 * interpolated values are also monotonic.
	 * <p>
	 * This function uses the Fritsch-Carlson method for computing the spline parameters.
	 * http://en.wikipedia.org/wiki/Monotone_cubic_interpolation
	 * <p>
	 * @param X the {@code double} x-coordinates of the control points, strictly increasing
	 * @param Y the {@code double} y-coordinates of the control points
	 * <p>
	 * @return a monotone cubic spline from the specified set of control points
	 * <p>
	 * @throws IllegalArgumentException if {@code X} or {@code Y} is {@code null}, have different
	 *                                  lengths or have less than two values
	 */
	public static SplineInterpolator create(final double[] X, final double[] Y) {
		// Check the arguments
		DoubleArguments.requireSameLength(X, Y);
		DoubleArguments.requireMinLength(X, 2);

		// Initialize
		final int n = X.length;
		final double[] D = new double[n - 1];
		final double[] M = new double[n];

		// Compute the slopes of the secant lines between the successive points
		for (int i = 0; i < n - 1; ++i) {
			final double h = X[i + 1] - X[i];
			if (h <= 0.) {
				throw new IllegalArgumentException(
						"The control points must all have strictly increasing x-coordinates");
			}
			D[i] = (Y[i + 1] - Y[i]) / h;
		}
		// Compute the tangents as the average of the secant lines
		M[0] = D[0];
		for (int i = 1; i < n - 1; ++i) {
			M[i] = Maths.mean(D[i - 1], D[i]);
		}
		M[n - 1] = D[n - 2];
		// Update the tangents to preserve monotonicity
		for (int i = 0; i < n - 1; ++i) {
			if (D[i] == 0.) {
				// @note successive y-coordinates are equal
				M[i] = 0.;
				M[i + 1] = 0.;
			} else {
				final double a = M[i] / D[i];
				final double b = M[i + 1] / D[i];
				final double h = Maths.hypot(a, b);
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
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the interpolated {@code double} value of {@code y = f(x)} for {@code x} between
	 * {@code fromPoint} and {@code toPoint}.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code y = f(x)} for {@code x} between {@code fromPoint} and {@code toPoint}
	 */
	@Override
	protected double interpolate(final double x) {
		// Find the index of the last point with smaller X
		int i = 0;
		if (Doubles.equals(x, X[i])) {
			return Y[i];
		}
		while (x > X[i + 1] + Maths.TOLERANCE) {
			++i;
		}
		if (Doubles.equals(x, X[i + 1])) {
			return Y[i + 1];
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
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public SplineInterpolator clone() {
		final SplineInterpolator clone = (SplineInterpolator) super.clone();
		clone.X = Objects.clone(X);
		clone.Y = Objects.clone(Y);
		clone.M = Objects.clone(M);
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = Strings.createBuilder(X.length * (3 * INITIAL_CAPACITY + 5));
		for (int i = 0; i < X.length; ++i) {
			if (i > 0) {
				builder.append(Arrays.DELIMITER);
			}
			builder.append(LEFT_PARENTHESIS)
					.append(X[i])
					.append(Arrays.DELIMITER)
					.append(Y[i])
					.append(COLON)
					.append(M[i])
					.append(RIGHT_PARENTHESIS);
		}
		return Strings.bracketize(builder.toString());
	}
}
