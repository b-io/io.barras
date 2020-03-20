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
package jupiter.math.analysis.differentiation;

import jupiter.common.math.Domain;
import jupiter.common.model.ICloneable;
import jupiter.math.analysis.function.univariate.UnivariateFunction;

/**
 * {@link Differentiator} is the {@link UnivariateFunction} differentiating {@code y = f(x)} in the
 * {@link Domain}.
 */
public abstract class Differentiator
		extends UnivariateFunction {

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
	 * Constructs a {@link Differentiator} by default.
	 */
	protected Differentiator() {
		super();
	}

	/**
	 * Constructs a {@link Differentiator} with the specified {@link Domain}.
	 * <p>
	 * @param domain the {@link Domain}
	 */
	protected Differentiator(final Domain domain) {
		super(domain);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the differentiation function to the specified value.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return {@code f(x)}
	 *
	 * @see #differentiate(double)
	 */
	@Override
	public double apply(final double x) {
		return differentiate(bound(x));
	}

	/**
	 * Returns the differentiated {@code double} value {@code y' = f'(x)} for {@code x}. Evaluates
	 * the derivative at {@code x} defined in {@code domain}.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code y = f(x)} for {@code x} defined in {@code domain}
	 */
	protected abstract double differentiate(final double x);


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
	public Differentiator clone() {
		return (Differentiator) super.clone();
	}
}
