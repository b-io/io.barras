/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
import jupiter.common.test.ArrayArguments;

/**
 * {@link BayesianInferenceOverModel} is the {@link BayesianInference} over an array of
 * {@link StatisticalModel} (hypotheses).
 */
public class BayesianInferenceOverModel
		extends BayesianInference {

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
	 * The array of {@link StatisticalModel} (hypotheses).
	 */
	protected final StatisticalModel[] models;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link BayesianInferenceOverModel} with the specified number of
	 * {@link StatisticalModel} (hypotheses).
	 * <p>
	 * @param modelCount the number of {@link StatisticalModel} (hypotheses)
	 */
	public BayesianInferenceOverModel(final int modelCount) {
		super(modelCount);
		models = new StatisticalModel[modelCount];
	}

	/**
	 * Constructs a {@link BayesianInferenceOverModel} with the specified array of
	 * {@link StatisticalModel} (hypotheses).
	 * <p>
	 * @param models the array of {@link StatisticalModel} (hypotheses)
	 */
	public BayesianInferenceOverModel(final StatisticalModel[] models) {
		super(Arguments.requireNonNull(models, "models").length);
		this.models = models;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the evidence conditional probability {@code P(E|H)} (called likelihood) of observing
	 * the specified evidence {@code E} given the specified {@link StatisticalModel} (hypothesis
	 * {@code H}).
	 * <p>
	 * @param hypothesisIndex the index of the {@link StatisticalModel} (hypothesis {@code H})
	 * @param evidence        a {@code double} value
	 * <p>
	 * @return the evidence conditional probability {@code P(E|H)} (called likelihood) of observing
	 *         the specified evidence {@code E} given the specified {@link StatisticalModel}
	 *         (hypothesis {@code H})
	 */
	@Override
	protected double getLikelihood(final int hypothesisIndex, final double evidence) {
		// Check the arguments
		ArrayArguments.requireIndex(hypothesisIndex, hypothesisCount);

		// Return the likelihood of the evidence given the statistical model
		return models[hypothesisIndex].getLikelihood(evidence);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@link StatisticalModel} at the specified index.
	 * <p>
	 * @param index the index of the {@link StatisticalModel} to set
	 * @param model a {@link StatisticalModel}
	 */
	public void setStatisticalModel(final int index, final StatisticalModel model) {
		// Check the arguments
		ArrayArguments.requireIndex(index, hypothesisCount);
		Arguments.requireNonNull(model, "model");

		// Set the statistical model
		models[index] = model;
	}
}
