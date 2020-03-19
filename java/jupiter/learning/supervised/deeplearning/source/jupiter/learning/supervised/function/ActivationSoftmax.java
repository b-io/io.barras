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

import static jupiter.math.analysis.function.univariate.UnivariateFunctions.EXP;
import static jupiter.math.analysis.function.univariate.UnivariateFunctions.LOG;
import static jupiter.math.analysis.function.bivariate.BivariateFunctions.ADD;
import static jupiter.math.analysis.function.bivariate.BivariateFunctions.MAX;

import jupiter.common.model.ICloneable;
import jupiter.learning.supervised.Classifier;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Scalar;

/**
 * {@link ActivationSoftmax} is the softmax {@link OutputActivationFunction} with multiple
 * non-negative return values summing up to 0.
 */
public class ActivationSoftmax
		extends OutputActivationFunction {

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
	 * Constructs an {@link ActivationSoftmax}.
	 */
	protected ActivationSoftmax() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the softmax to the specified {@link Entity}.
	 * <p>
	 * @param E an {@link Entity}
	 * <p>
	 * @return {@code exp(E) / sum(exp(E))}
	 */
	@Override
	public Entity apply(final Entity E) {
		// Subtract the values by the maximum value (to avoid overflow) and apply the exponential
		final Entity result = E.minus(E.applyByColumn(MAX)).apply(EXP);
		// Divide by the sum of the values
		return result.arrayDivide(result.applyByColumn(ADD).toMatrix());
	}

	/**
	 * Applies the derivative of the softmax to the specified {@link Entity} and returns the
	 * resulting {@link Entity}.
	 * <p>
	 * @param E an {@link Entity}
	 * <p>
	 * @return {@code E (1. - E)}
	 */
	@Override
	public Entity derive(final Entity E) {
		return E.times(Scalar.ONE.minus(E));
	}

	//////////////////////////////////////////////

	/**
	 * Computes the cost of {@code A}.
	 * <p>
	 * @param classifier a {@link Classifier}
	 * @param A          an {@link Entity}
	 * <p>
	 * @return {@code log(A) Y'}
	 */
	@Override
	public double computeCost(final Classifier classifier, final Entity A) {
		return -A.apply(LOG).diagonalTimes(classifier.getTransposedClasses()).sum() /
				classifier.getTrainingExampleCount();
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
	public ActivationSoftmax clone() {
		return (ActivationSoftmax) super.clone();
	}
}
