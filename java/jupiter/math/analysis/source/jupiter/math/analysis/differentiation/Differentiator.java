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
package jupiter.math.analysis.differentiation;

import jupiter.common.math.Domain;
import jupiter.common.math.DoubleInterval;
import jupiter.common.model.ICloneable;
import jupiter.common.test.IntegerArguments;
import jupiter.common.test.IntervalArguments;
import jupiter.math.analysis.function.univariate.UnivariateFunction;

/**
 * {@link Differentiator} is the {@link UnivariateFunction} differentiating {@code y = f(x)} for
 * {@code x} defined in the differentiation {@link DoubleInterval}.
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
	 * The {@link UnivariateFunction} to differentiate.
	 */
	protected final UnivariateFunction f;

	/**
	 * The derivation order.
	 */
	protected final int order;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Differentiator} with the specified {@link UnivariateFunction}.
	 * <p>
	 * @param f the {@link UnivariateFunction} to differentiate
	 */
	public Differentiator(final UnivariateFunction f) {
		this(f, 1);
	}

	/**
	 * Constructs a {@link Differentiator} with the specified {@link UnivariateFunction} and
	 * differentiation {@link DoubleInterval}.
	 * <p>
	 * @param f        the {@link UnivariateFunction} to differentiate
	 * @param interval the differentiation {@link DoubleInterval}
	 */
	public Differentiator(final UnivariateFunction f, final DoubleInterval interval) {
		this(f, interval, 1);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Differentiator} with the specified {@link UnivariateFunction} and
	 * derivation order.
	 * <p>
	 * @param f     the {@link UnivariateFunction} to differentiate
	 * @param order the derivation order
	 */
	public Differentiator(final UnivariateFunction f, final int order) {
		this(f, f.getDomain().getFirst(), order);
	}

	/**
	 * Constructs a {@link Differentiator} with the specified {@link UnivariateFunction},
	 * differentiation {@link DoubleInterval} and derivation order.
	 * <p>
	 * @param f        the {@link UnivariateFunction} to differentiate
	 * @param interval the differentiation {@link DoubleInterval}
	 * @param order    the derivation order
	 */
	public Differentiator(final UnivariateFunction f, final DoubleInterval interval,
			final int order) {
		super(new Domain(interval));

		// Check the arguments
		IntervalArguments.requireValid(interval, "differentiation interval");
		IntervalArguments.requireInside(interval, "differentiation interval", f.getDomain(),
				"domain");
		IntegerArguments.requirePositive(order);

		// Set the attributes
		this.f = f;
		this.order = order;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link UnivariateFunction} to differentiate.
	 * <p>
	 * @return the {@link UnivariateFunction} to differentiate
	 */
	public UnivariateFunction getFunction() {
		return f;
	}

	/**
	 * Returns the derivation order.
	 * <p>
	 * @return the derivation order
	 */
	public int getDerivationOrder() {
		return order;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the differentiation function to the specified value.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code y' = f'(x)} for {@code x} defined in {@code domain}
	 *
	 * @see #differentiate(double)
	 */
	@Override
	protected double a(final double x) {
		return differentiate(x);
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

	//////////////////////////////////////////////

	/**
	 * Differentiates {@code y = f(x)} for all {@code x} defined in {@code domain} and then use
	 * {@link #differentiate(double)} to retrieve {@code y' = f'(x)}.
	 * <p>
	 * @return {@code true} if the differentiation is done, {@code false} otherwise
	 *
	 * @see #differentiate(double)
	 */
	public abstract boolean differentiateAll();

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public double[] applyToPrimitiveArray(final double... array) {
		differentiateAll();
		return super.applyToPrimitiveArray(array);
	}

	@Override
	public double[] applyToPrimitiveArray(final Number[] array) {
		differentiateAll();
		return super.applyToPrimitiveArray(array);
	}

	//////////////////////////////////////////////

	@Override
	public byte[] applyToPrimitiveByteArray(final double... array) {
		differentiateAll();
		return super.applyToPrimitiveByteArray(array);
	}

	@Override
	public short[] applyToPrimitiveShortArray(final double... array) {
		differentiateAll();
		return super.applyToPrimitiveShortArray(array);
	}

	@Override
	public int[] applyToPrimitiveIntArray(final double... array) {
		differentiateAll();
		return super.applyToPrimitiveIntArray(array);
	}

	@Override
	public long[] applyToPrimitiveLongArray(final double... array) {
		differentiateAll();
		return super.applyToPrimitiveLongArray(array);
	}

	@Override
	public float[] applyToPrimitiveFloatArray(final double... array) {
		differentiateAll();
		return super.applyToPrimitiveFloatArray(array);
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
