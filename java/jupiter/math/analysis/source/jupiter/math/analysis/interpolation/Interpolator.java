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

import jupiter.common.math.Domain;
import jupiter.common.math.DoubleInterval;
import jupiter.common.model.ICloneable;
import jupiter.math.analysis.function.univariate.UnivariateFunction;
import jupiter.math.analysis.struct.XY;

/**
 * {@link Interpolator} is the {@link UnivariateFunction} interpolating {@code y = f(x)} between two
 * endpoints.
 */
public abstract class Interpolator
		extends UnivariateFunction {

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
	 * The leading endpoint {@link XY} of {@link Double} of the interpolant (inclusive).
	 */
	protected final XY<Double> fromPoint;
	/**
	 * The tailing endpoint {@link XY} of {@link Double} of the interpolant (inclusive).
	 */
	protected final XY<Double> toPoint;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link Interpolator} with the interpolant of {@code y = f(x)} between the
	 * specified endpoints.
	 * <p>
	 * @param fromPoint the leading endpoint {@link XY} of {@link Double} of the interpolant
	 *                  (inclusive)
	 * @param toPoint   the tailing endpoint {@link XY} of {@link Double} of the interpolant
	 *                  (inclusive)
	 */
	protected Interpolator(final XY<Double> fromPoint, final XY<Double> toPoint) {
		super(new Domain(new DoubleInterval(fromPoint.getX(), toPoint.getX())));
		this.fromPoint = fromPoint;
		this.toPoint = toPoint;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the interpolation function to the specified value.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code y = f(x)} for {@code x} between {@code fromPoint} and {@code toPoint}
	 *
	 * @see #interpolate(double)
	 */
	@Override
	protected double a(final double x) {
		return interpolate(x);
	}

	/**
	 * Returns the interpolated {@code double} value of {@code y = f(x)} for {@code x} between
	 * {@code fromPoint} and {@code toPoint}.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code y = f(x)} for {@code x} between {@code fromPoint} and {@code toPoint}
	 */
	protected abstract double interpolate(final double x);


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
	public Interpolator clone() {
		return (Interpolator) super.clone();
	}
}
