/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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

	protected ActivationHyperbolicTangent() {
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the hyperbolic tangent function to the specified value and returns the result.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return the result
	 */
	@Override
	public double apply(final double x) {
		return Functions.TANH.apply(x);
	}

	/**
	 * Applies the hyperbolic tangent function to the specified {@link Entity} and returns the
	 * result.
	 * <p>
	 * @param A an {@link Entity}
	 * <p>
	 * @return the result
	 */
	@Override
	public Entity apply(final Entity A) {
		return A.apply(Functions.TANH);
	}

	/**
	 * Applies the derivative of the hyperbolic tangent function to the specified {@link Entity} and
	 * returns the result.
	 * <p>
	 * @param A an array of {@link Entity}
	 * <p>
	 * @return the result
	 */
	@Override
	public Entity derive(final Entity A) {
		return Scalar.ONE.minus(A.apply(Functions.SQUARE));
	}
}
