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

import static jupiter.math.analysis.function.Functions.LOG;
import static jupiter.math.analysis.function.Functions.SIGMOID;

import jupiter.learning.supervised.Classifier;
import jupiter.math.analysis.function.Sigmoid;
import jupiter.math.linear.entity.Entity;
import jupiter.math.linear.entity.Scalar;

/**
 * {@link ActivationSigmoid} is the logistic {@link OutputActivationFunction} with return values
 * monotonically increasing from 0 to 1.
 */
public class ActivationSigmoid
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
	 * Constructs an {@link ActivationSigmoid}.
	 */
	protected ActivationSigmoid() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the {@link Sigmoid} to the specified {@link Entity} and returns the resulting
	 * {@link Entity}.
	 * <p>
	 * @param E an {@link Entity}
	 * <p>
	 * @return {@code 1. / (1. + exp(E))}
	 */
	@Override
	public Entity apply(final Entity E) {
		return E.apply(SIGMOID);
	}

	/**
	 * Applies the derivative of the {@link Sigmoid} to the specified {@link Entity} and returns the
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
	 * @return {@code -(log(A) Y' + log(1. - A) (1. - Y')) / m}
	 */
	@Override
	public double computeCost(final Classifier classifier, final Entity A) {
		return -A.apply(LOG)
				.diagonalTimes(classifier.getTransposedClasses())
				.add(Scalar.ONE.minus(A)
						.apply(LOG)
						.diagonalTimes(Scalar.ONE.minus(classifier.getTransposedClasses())))
				.toScalar()
				.get() / classifier.getTrainingExampleCount();
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
	public ActivationSigmoid clone() {
		return (ActivationSigmoid) super.clone();
	}
}
