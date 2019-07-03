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
package jupiter.math.statistics;

import java.io.Serializable;

import jupiter.common.test.DoubleArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Doubles;

public class BayesianInference
		implements Inference, Serializable {

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
	 * The number of hypotheses.
	 */
	protected final int hypothesisCount;
	/**
	 * The array of probabilities P(E | H) for all hypothesis H (the likelihood).
	 */
	protected final double[] likelihoods;
	/**
	 * The array of probabilities P(H | E) for all hypothesis H (the posterior probability).
	 */
	protected final double[] hypothesesProbabilities;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public BayesianInference(final int hypothesisCount) {
		this.hypothesisCount = hypothesisCount;
		likelihoods = new double[hypothesisCount];
		hypothesesProbabilities = new double[hypothesisCount];
		reset();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// INFERENCE
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void reset() {
		Doubles.fill(hypothesesProbabilities, 1. / hypothesisCount);
	}

	public void updateHypothesesProbabilities(final double value) {
		// Get the marginal likelihood P(E)
		double marginalLikelihood = 0.;
		for (int i = 0; i < hypothesisCount; ++i) {
			// Compute P(E) = SUM[P(H) * P(E | H), H] (chain rule)
			marginalLikelihood += hypothesesProbabilities[i] * likelihoods[i];
		}
		// Update the probabilities of the hypotheses
		for (int i = 0; i < hypothesisCount; ++i) {
			// Using the previous posterior probability P(H | E) as the current prior P(H),
			// compute P(H | E) = P(H) * P(E | H) / P(E) (Bayes' rule)
			hypothesesProbabilities[i] *= likelihoods[i] / marginalLikelihood;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the probabilities of the hypotheses.
	 * <p>
	 * @return the probabilities of the hypotheses
	 */
	public double[] getHypothesesProbabilities() {
		return hypothesesProbabilities;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void setLikelihood(final int i, final double likelihood) {
		// Check the arguments
		IntegerArguments.requireNonNegative(i);
		DoubleArguments.requireLessThan(i, hypothesisCount);

		// Set the likelihood
		likelihoods[i] = likelihood;
	}
}
