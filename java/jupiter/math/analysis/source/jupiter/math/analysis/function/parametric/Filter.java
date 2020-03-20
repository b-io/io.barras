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
package jupiter.math.analysis.function.parametric;

import jupiter.common.model.ICloneable;
import jupiter.common.test.DoubleArguments;

public class Filter
		extends ParametricFunction {

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
	 * Constructs a {@link Filter} by default.
	 */
	protected Filter() {
		this(0., 0., 1.);
	}

	/**
	 * Constructs a {@link Filter} with the specified threshold and possible resulting
	 * {@code double} values.
	 * <p>
	 * @param threshold the {@code double} threshold
	 * @param a         the resulting {@code double} value if {@code x <= threshold}
	 * @param b         the other resulting {@code double} value if {@code x > threshold}
	 */
	public Filter(final double threshold, final double a, final double b) {
		super(threshold, a, b);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the filter function to the specified value with the specified parameters.
	 * <p>
	 * @param x          a {@code double} value
	 * @param parameters the {@code double} parameters
	 * <p>
	 * @return {@code f(x, threshold, a, b)}
	 *
	 * @see #apply(double, double, double, double)
	 */
	@Override
	public double apply(final double x, final double... parameters) {
		// Check the arguments
		DoubleArguments.requireLength(parameters, 3);

		// Apply the filter function to the value with the parameters
		return apply(x, parameters[0], parameters[1], parameters[2]);
	}

	/**
	 * Applies the filter function to the specified value with the specified threshold and possible
	 * resulting {@code double} values.
	 * <p>
	 * @param x         a {@code double} value
	 * @param threshold the threshold
	 * @param a         the resulting {@code double} value if {@code x <= threshold}
	 * @param b         the other resulting {@code double} value if {@code x > threshold}
	 * <p>
	 * @return {@code a} if {@code x <= threshold}, {@code b} otherwise
	 */
	public static double apply(final double x, final double threshold, final double a,
			final double b) {
		return x <= threshold ? a : b;
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
	public Filter clone() {
		return (Filter) super.clone();
	}
}
