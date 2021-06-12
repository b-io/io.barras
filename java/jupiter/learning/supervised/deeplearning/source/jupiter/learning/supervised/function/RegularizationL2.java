/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.math.analysis.function.parametric.ParametricFunctions.SQUARE;

import jupiter.common.model.ICloneable;
import jupiter.math.linear.entity.Matrix;

/**
 * {@link RegularizationL2} is the L2 {@link RegularizationFunction} adding a L2 penalty equal to
 * the sum of the squares of all the weights.
 */
public class RegularizationL2
		extends RegularizationFunction {

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
	 * Constructs a {@link RegularizationL2}.
	 */
	protected RegularizationL2() {
		this(0.1);
	}

	/**
	 * Constructs a {@link RegularizationL2} with the specified hyper-parameter {@code λ}.
	 * <p>
	 * @param lambda the hyper-parameter {@code λ}
	 */
	public RegularizationL2(final double lambda) {
		super(lambda);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Computes the regularization cost.
	 * <p>
	 * @param m       the number of training examples
	 * @param weights the array of weight {@link Matrix}
	 * <p>
	 * @return {@code λ sum(sum(W .* W)) / (2. * m)}
	 */
	@Override
	public double computeCost(final int m, final Matrix... weights) {
		double sum = 0.;
		for (final Matrix W : weights) {
			sum += W.apply(SQUARE).sum();
		}
		return lambda * sum / (2. * m);
	}

	/**
	 * Applies the derivative of the regularization function to the specified weight {@link Matrix}.
	 * <p>
	 * @param m the number of training examples
	 * @param W the weight {@link Matrix}
	 * <p>
	 * @return {@code λ W / m}
	 */
	@Override
	public Matrix derive(final int m, final Matrix W) {
		return W.times(lambda / m);
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
	public RegularizationL2 clone() {
		return (RegularizationL2) super.clone();
	}
}
