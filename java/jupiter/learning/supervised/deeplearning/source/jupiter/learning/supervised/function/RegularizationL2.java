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

import jupiter.common.math.Maths;
import jupiter.math.analysis.function.Functions;
import jupiter.math.linear.entity.Matrix;

/**
 * The L2 regularization function.
 */
public class RegularizationL2
		extends RegularizationFunction {

	/**
	 * Constructs a L2 regularization function.
	 */
	protected RegularizationL2() {
		this(0.1);
	}

	/**
	 * Constructs a L2 regularization function.
	 * <p>
	 * @param lambda the hyper-parameter
	 */
	public RegularizationL2(final double lambda) {
		super(lambda);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public double apply(final double x) {
		return Maths.square(x);
	}

	/**
	 * Computes the regularization cost.
	 * <p>
	 * @param m       the number of training examples
	 * @param weights the array of weight {@link Matrix}
	 * <p>
	 * @return the regularization cost
	 */
	@Override
	public double computeCost(final int m, final Matrix[] weights) {
		double sum = 0.;
		for (final Matrix W : weights) {
			sum += W.apply(Functions.SQUARE).sum();
		}
		return lambda * sum / (2. * m);
	}

	/**
	 * Applies the derivative of the regularization function to the specified weight {@link Matrix}
	 * and returns the result.
	 * <p>
	 * @param m the number of training examples
	 * @param W the weight {@link Matrix}
	 * <p>
	 * @return the result
	 */
	@Override
	public Matrix derive(final int m, final Matrix W) {
		return W.times(lambda / m);
	}
}
