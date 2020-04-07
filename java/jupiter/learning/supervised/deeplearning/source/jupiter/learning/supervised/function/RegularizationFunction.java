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
package jupiter.learning.supervised.function;

import java.io.Serializable;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Matrix;

/**
 * {@link RegularizationFunction} is the process of adding information in order to solve an
 * ill-posed problem or to prevent overfitting.
 */
public abstract class RegularizationFunction
		implements ICloneable<RegularizationFunction>, Serializable {

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
	 * The hyper-parameter {@code λ}.
	 */
	protected final double lambda;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link RegularizationFunction} with the specified hyper-parameter {@code λ}.
	 * <p>
	 * @param lambda the hyper-parameter {@code λ}
	 */
	protected RegularizationFunction(final double lambda) {
		this.lambda = lambda;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Computes the regularization cost.
	 * <p>
	 * @param m       the number of training examples
	 * @param weights the array of weight {@link Matrix}
	 * <p>
	 * @return the regularization cost
	 */
	public abstract double computeCost(final int m, final Matrix... weights);

	/**
	 * Applies the derivative of the regularization function to the specified weight {@link Matrix}.
	 * <p>
	 * @param m the number of training examples
	 * @param W the weight {@link Matrix}
	 * <p>
	 * @return the resulting {@link Matrix}
	 */
	public abstract Entity derive(final int m, final Matrix W);


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
	public RegularizationFunction clone() {
		try {
			return (RegularizationFunction) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}
}
