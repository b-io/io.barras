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
package jupiter.math.analysis.function.bivariate;

import jupiter.common.model.ICloneable;

public class Modulo
		extends BivariateFunction {

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
	 * Constructs a {@link Modulo} by default.
	 */
	protected Modulo() {
		this(2.);
	}

	/**
	 * Constructs a {@link Modulo} with the specified initial value.
	 * <p>
	 * @param initialValue the initial {@code double} value
	 */
	public Modulo(final double initialValue) {
		super(initialValue);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the bivariate function to the initial value and the specified value.
	 * <p>
	 * @param x1 a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code f(x1, initialValue)}
	 *
	 * @see #a(double, double)
	 */
	@Override
	protected double a(final double x1) {
		return a(bound(x1), initialValue);
	}

	/**
	 * Applies the modulo function to the specified values.
	 * <p>
	 * @param x1 a {@code double} value (on the abscissa)
	 * @param x2 another {@code double} value
	 * <p>
	 * @return {@code x1 % x2}
	 */
	@Override
	protected double a(final double x1, final double x2) {
		return x1 % x2;
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
	public Modulo clone() {
		return (Modulo) super.clone();
	}
}
