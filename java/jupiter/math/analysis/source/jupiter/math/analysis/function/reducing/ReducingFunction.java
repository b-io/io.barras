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
package jupiter.math.analysis.function.reducing;

import jupiter.common.model.ICloneable;
import jupiter.math.analysis.function.Function;

/**
 * {@link ReducingFunction} is an analytical array-reducing {@link Function}.
 */
public abstract class ReducingFunction
		extends Function
		implements IReducingFunction {

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
	 * The initial {@code double} value.
	 */
	protected final double initialValue;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ReducingFunction} by default.
	 */
	protected ReducingFunction() {
		this(0.);
	}

	/**
	 * Constructs a {@link ReducingFunction} with the specified initial {@code double} value.
	 * <p>
	 * @param initialValue the initial {@code double} value
	 */
	public ReducingFunction(final double initialValue) {
		super();
		this.initialValue = initialValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the initial {@code double} value.
	 * <p>
	 * @return the initial {@code double} value
	 */
	public double getInitialValue() {
		return initialValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the reducing function to the specified {@code double} value with the initial
	 * {@code double} value and returns the resulting {@code double} value.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return {@code f(initialValue, x)}
	 */
	@Override
	public double apply(final double x) {
		return apply(initialValue, x);
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
	public ReducingFunction clone() {
		return (ReducingFunction) super.clone();
	}
}
