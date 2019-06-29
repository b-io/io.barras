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

import jupiter.math.analysis.function.Functions;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Scalar;

public class ActivationHyperbolicTangent
		extends ActivationFunction {

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

	protected ActivationHyperbolicTangent() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the hyperbolic tangent function to the specified {@code double} value and returns the
	 * resulting {@code double} value.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return the resulting {@code double} value
	 */
	@Override
	public double apply(final double x) {
		return Functions.TANH.apply(x);
	}

	/**
	 * Applies the hyperbolic tangent function to the specified {@link Entity} and returns the
	 * resulting {@link Entity}.
	 * <p>
	 * @param E an {@link Entity}
	 * <p>
	 * @return the resulting {@link Entity}
	 */
	@Override
	public Entity apply(final Entity E) {
		return E.apply(Functions.TANH);
	}

	/**
	 * Applies the derivative of the hyperbolic tangent function to the specified {@link Entity} and
	 * returns the resulting {@link Entity}.
	 * <p>
	 * @param E an array of {@link Entity}
	 * <p>
	 * @return the resulting {@link Entity}
	 */
	@Override
	public Entity derive(final Entity E) {
		return Scalar.ONE.minus(E.apply(Functions.SQUARE));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public ActivationHyperbolicTangent clone() {
		return (ActivationHyperbolicTangent) super.clone();
	}
}
