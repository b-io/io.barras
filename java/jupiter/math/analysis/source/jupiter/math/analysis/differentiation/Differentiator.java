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
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The derivation order.
	 */
	protected final int order;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Differentiator} by default.
	 */
	protected Differentiator() {
		this(1);
	}

	/**
	 * Constructs a {@link Differentiator} with the specified derivation order.
	 * <p>
	 * @param order the derivation order
	 */
	protected Differentiator(final int order) {
		super();
		this.order = order;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Differentiator} with the specified derivation order and {@link Domain}.
	 * <p>
	 * @param order  the derivation order
	 * @param domain the {@link Domain}
	 */
	protected Differentiator(final int order, final Domain domain) {
		super(domain);
		this.order = order;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the differentiation function to the specified value.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return {@code y' = f'(x)} for {@code x} defined in {@code domain}
	 *
	 * @see #differentiate(double)
	 */
	@Override
	public double apply(final double x) {
		return differentiate(bound(x));
	}

	/**
	 * Returns the differentiated {@code double} value {@code y' = f'(x)} for {@code x} defined in
	 * {@code domain}.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code y' = f'(x)} for {@code x} defined in {@code domain}
	 */
	protected abstract double differentiate(final double x);

	/**
	 * Differentiates {@code y = f(x)} for all {@code x} defined in {@code domain} and then use
	 * {@link #differentiate} to retrieve {@code y' = f'(x)}.
	 * <p>
	 * @return {@code true} if the differentiation is done, {@code false} otherwise
	 *
	 * @see #differentiate(double)
	 */
	protected abstract boolean differentiateAll();

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public double[] applyToPrimitiveArray(final double... array) {
		differentiateAll();
		return super.applyToPrimitiveArray(array);
	}

	@Override
	public double[] applyToPrimitiveArray(final Number... array) {
		differentiateAll();
		return super.applyToPrimitiveArray(array);
	}

	//////////////////////////////////////////////

	@Override
	public int[] applyToIntPrimitiveArray(final double... array) {
		differentiateAll();
		return super.applyToIntPrimitiveArray(array);
	}

	@Override
	public long[] applyToLongPrimitiveArray(final double... array) {
		differentiateAll();
		return super.applyToLongPrimitiveArray(array);
	}

	@Override
	public float[] applyToFloatPrimitiveArray(final double... array) {
		differentiateAll();
		return super.applyToFloatPrimitiveArray(array);
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
	public Differentiator clone() {
		return (Differentiator) super.clone();
	}
}
