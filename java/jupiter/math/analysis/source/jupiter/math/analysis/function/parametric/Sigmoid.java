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

import jupiter.common.math.Maths;
import jupiter.common.model.ICloneable;
import jupiter.common.test.DoubleArguments;

/**
 * {@link Sigmoid} is the logistic {@link ParametricFunction} with return values monotonically
 * increasing from 0 to 1.
 */
public class Sigmoid
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
	 * Constructs a {@link Sigmoid} by default.
	 */
	protected Sigmoid() {
		this(1.);
	}

	/**
	 * Constructs a {@link Sigmoid} with the specified coefficient.
	 * <p>
	 * @param coefficient the {@code double} coefficient
	 */
	protected Sigmoid(final double coefficient) {
		super(coefficient);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the sigmoid function to the specified value with the specified parameters.
	 * <p>
	 * @param x          a {@code double} value
	 * @param parameters the {@code double} parameters
	 * <p>
	 * @return {@code f(x, coefficient)}
	 *
	 * @see #apply(double, double)
	 */
	@Override
	protected double a(final double x, final double... parameters) {
		// Check the arguments
		DoubleArguments.requireLength(parameters, 1);

		// Apply the sigmoid function to the value with the parameters
		return apply(x, parameters[0]);
	}

	/**
	 * Applies the sigmoid function to the specified value with the specified coefficient.
	 * <p>
	 * @param x           a {@code double} value
	 * @param coefficient the {@code double} coefficient
	 * <p>
	 * @return {@code 1. / (1. + exp(-coefficient * x))}
	 */
	public static double apply(final double x, final double coefficient) {
		return Maths.sigmoid(x, coefficient);
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
	public Sigmoid clone() {
		return (Sigmoid) super.clone();
	}
}
