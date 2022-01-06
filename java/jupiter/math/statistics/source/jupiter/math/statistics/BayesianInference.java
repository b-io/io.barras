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

/**
 * {@link BayesianInference} is the method of {@link StatisticalInference} in which Bayes' theorem
 * is used to update the probability {@code P(H)} for a hypothesis {@code H} as more evidence
 * {@code E} becomes available.
 */
public abstract class BayesianInference
		extends StatisticalInference {

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
	 * The array of evidence conditional probability {@code P(E|H)} (called likelihood) for all
	 * hypothesis {@code H}.
	 */
	protected double[] likelihoods;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link BayesianInference} with the specified number of hypotheses.
	 * <p>
	 * @param hypothesisCount the number of hypotheses
	 */
	public BayesianInference(final int hypothesisCount) {
		super(hypothesisCount);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the array of hypothesis probability {@code P(H)} for all hypothesis {@code H}.
	 * <p>
	 * @return the array of hypothesis probability {@code P(H)} for all hypothesis {@code H}
	 */
	public double[] getHypothesesProbabilities() {
		return hypothesisProbabilities;
	}

	/**
	 * Returns the evidence conditional probability {@code P(E|H)} (called likelihood) of observing
	 * the specified evidence {@code E} given the specified hypothesis {@code H}.
	 * <p>
	 * @param hypothesisIndex the index of the hypothesis {@code H}
	 * @param evidence        a {@code double} value
	 * <p>
	 * @return the evidence conditional probability {@code P(E|H)} (called likelihood) of observing
	 *         the specified evidence {@code E} given the specified hypothesis {@code H}
	 */
	protected abstract double getLikelihood(final int hypothesisIndex, final double evidence);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Updates the likelihood {@code P(E|H)} for all hypothesis {@code H} with the specified
	 * evidence {@code E}, computes the marginal likelihood {@code P(E)} (also called model
	 * evidence) and updates the hypothesis probability {@code P(H)} for all hypothesis {@code H}.
	 * <p>
	 * @param evidence the evidence {@code E} that is not yet used in the inference
	 */
	@Override
	public void infer(final double evidence) {
		// Update the likelihood P(E|H) for all hypothesis H with the evidence E
		updateLikelihoods(evidence);
		// Compute the marginal likelihood P(E) (also called model evidence) and update the
		// hypothesis probability P(H) for all hypothesis H
		updateHypothesisProbabilities(evidence);
	}

	//////////////////////////////////////////////

	/**
	 * Updates the likelihood {@code P(E|H)} for all hypothesis {@code H} with the specified
	 * evidence {@code E}.
	 * <p>
	 * @param evidence the evidence {@code E} that is not yet used for updating the likelihood
	 *                 {@code P(E|H)} for all hypothesis {@code H}
	 */
	protected void updateLikelihoods(final double evidence) {
		for (int hi = 0; hi < hypothesisCount; ++hi) {
			likelihoods[hi] += getLikelihood(hi, evidence);
		}
	}

	/**
	 * Computes the marginal likelihood {@code P(E)} (also called model evidence) and updates the
	 * hypothesis probability {@code P(H)} for all hypothesis {@code H}.
	 * <p>
	 * @param evidence the evidence {@code E} that is not yet used for updating the hypothesis
	 *                 probability {@code P(H)} for all hypothesis {@code H}
	 */
	public void updateHypothesisProbabilities(final double evidence) {
		// Compute the marginal likelihood P(E) = Σ[P(H) * P(E|H)] over H (law of total probability)
		double marginalLikelihood = 0.;
		for (int hi = 0; hi < hypothesisCount; ++hi) {
			marginalLikelihood += hypothesisProbabilities[hi] * likelihoods[hi];
		}
		// Update the hypothesis probability P(H) for all hypothesis H
		for (int hi = 0; hi < hypothesisCount; ++hi) {
			// Compute the posterior probability P(H|E) = P(H) * P(E|H) / P(E) (Bayes' rule) and
			// replace the prior probability P(H) by it
			hypothesisProbabilities[hi] *= likelihoods[hi] / marginalLikelihood;
		}
	}
}
