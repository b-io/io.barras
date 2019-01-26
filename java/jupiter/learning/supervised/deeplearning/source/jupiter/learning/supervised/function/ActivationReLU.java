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
package jupiter.learning.supervised.function;

import jupiter.common.test.DoubleArguments;
import jupiter.math.analysis.function.Filter;
import jupiter.math.analysis.function.Functions;
import jupiter.math.analysis.function.Max;
import jupiter.math.linear.entity.Entity;

/**
 * The rectified linear unit (ReLU) function.
 */
public class ActivationReLU
		extends ActivationFunction {

	protected final Max max;
	protected final Filter filter;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a ReLU function.
	 */
	protected ActivationReLU() {
		super();
		max = Functions.MAX;
		filter = Functions.FILTER;
	}

	/**
	 * Constructs a leaky ReLU function with the specified positive gradient.
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

	/**
	 * Applies the ReLU function to the specified value and returns the result.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return the result
	 */
	@Override
	public double apply(final double x) {
		return max.apply(x);
	}

	/**
	 * Applies the ReLU function to the specified {@link Entity} and returns the result.
	 * <p>
	 * @param E an {@link Entity}
	 * <p>
	 * @return the result
	 */
	@Override
	public Entity apply(final Entity E) {
		return E.apply(max);
	}

	/**
	 * Applies the derivative of the ReLU function to the specified {@link Entity} and returns the
	 * result.
	 * <p>
	 * @param E an {@link Entity}
	 * <p>
	 * @return the result
	 */
	@Override
	public Entity derive(final Entity E) {
		return E.apply(filter);
	}
}
