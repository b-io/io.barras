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

import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Strings;
import jupiter.math.analysis.struct.XY;

/**
 * {@link LinearInterpolator} performs a linear interpolation from a specified set of control
 * points.
 */
public class LinearInterpolator
		implements ICloneable<LinearInterpolator>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link LinearInterpolator}.
	 */
	protected LinearInterpolator() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the interpolated {@code double} value of Y = f(X) for the specified X. Clamps X to
	 * the domain of the line determined by the specified points.
	 * <p>
	 * @param from the starting point {@link XY} of {@link Double} of the interpolating line
	 * @param to   the end point {@link XY} of {@link Double} of the interpolating line
	 * @param x    the X {@code double} value
	 * <p>
	 * @return the interpolated {@code double} value of Y = f(X) for the specified X
	 */
	public static double interpolate(final XY<Double> from, final XY<Double> to, final double x) {
		// Handle the boundary cases
		if (Double.isNaN(x)) {
			return x;
		}
		if (x <= from.getX()) {
			return from.getY();
		}
		if (x >= to.getX()) {
			return to.getY();
		}

		// Compute the slope and intercept of the interpolating line
		final double slope = (to.getY() - from.getY()) / (to.getX() - from.getX());
		final double intercept = from.getY() - slope * from.getX();

		// Apply the linear interpolation
		return intercept + slope * x;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public LinearInterpolator clone() {
		try {
			return (LinearInterpolator) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}
}
