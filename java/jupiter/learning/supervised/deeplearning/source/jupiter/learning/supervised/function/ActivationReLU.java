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
package jupiter.learning.supervised.function;

import static jupiter.math.analysis.function.parametric.ParametricFunctions.FILTER;

import jupiter.common.model.ICloneable;
import jupiter.common.test.DoubleArguments;
import jupiter.math.analysis.function.bivariate.Max;
import jupiter.math.analysis.function.parametric.Filter;
import jupiter.math.linear.entity.Entity;

/**
 * {@link ActivationReLU} is the rectified linear unit (ReLU) {@link ActivationFunction}.
 */
public class ActivationReLU
		extends ActivationFunction {

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

	protected final Max max;
	protected final Filter filter;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link ActivationReLU}.
	 */
	protected ActivationReLU() {
		super();
		max = new Max(0.);
		filter = FILTER;
	}

	/**
	 * Constructs a leaky {@link ActivationReLU} with the specified positive gradient.
	 * <p>
	 * @param gradient a {@code double} value
	 */
	public ActivationReLU(final double gradient) {
		super();

		// Check the arguments
		DoubleArguments.requirePositive(gradient);

		// Set the attributes
		max = new Max(gradient);
		filter = new Filter(0., gradient, 1.);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the ReLU function to the specified {@link Entity}.
	 * <p>
	 * @param E an {@link Entity}
	 * <p>
	 * @return {@code max(E)}
	 */
	@Override
	public Entity apply(final Entity E) {
		return E.apply(max);
	}

	/**
	 * Applies the derivative of the ReLU function to the specified {@link Entity}.
	 * <p>
	 * @param E an {@link Entity}
	 * <p>
	 * @return {@code filter(E)}
	 */
	@Override
	public Entity derive(final Entity E) {
		return E.apply(filter);
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
	public ActivationReLU clone() {
		return (ActivationReLU) super.clone();
	}
}
