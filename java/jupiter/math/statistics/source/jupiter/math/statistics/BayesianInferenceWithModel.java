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
package jupiter.math.statistics;

import jupiter.common.test.Arguments;
import jupiter.common.test.IntegerArguments;

public class BayesianInferenceWithModel
		extends BayesianInference {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final StatisticalModel[] models;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public BayesianInferenceWithModel(final int nModels) {
		super(nModels);
		models = new StatisticalModel[nModels];
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// BAYESIAN INFERENCE
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void updateLikelihood(final int i, final double value) {
		likelihoods[i] = models[i].getLikelihood(value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the statistical model of the specified hypothesis.
	 * <p>
	 * @param i     the hypothesis index
	 * @param model a {@link StatisticalModel}
	 */
	public void setStatisticalModel(final int i, final StatisticalModel model) {
		// Check the arguments
		IntegerArguments.requireNonNegative(i);
		IntegerArguments.requireLessThan(i, nHypotheses);
		Arguments.requireNonNull(model);

		// Set the statistical model
		models[i] = model;
	}
}
