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
package jupiter.math.statistics;

import jupiter.common.math.Maths;

/**
 * {@link Statistics} is a collection of statistical functions.
 */
public class Statistics {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Statistics}.
	 */
	protected Statistics() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// STATISTICAL FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the conditional probability {@code P(H|E)} of the hypothesis {@code H} given the
	 * event {@code E} using Baye's theorem.
	 * <p>
	 * @param peh the conditional probability {@code P(E|H)} of the event {@code E} given the
	 *            hypothesis {@code H}
	 * @param ph  the probability {@code P(H)} of the hypothesis {@code H}
	 * @param pe  the probability {@code P(E)} of the event {@code E}
	 * <p>
	 * @return the conditional probability {@code P(H|E)} of the hypothesis {@code H} given the
	 *         event {@code E} using Baye's theorem
	 */
	public static double conditionalProbability(final double peh, final double ph,
			final double pe) {
		return peh * ph / pe;
	}

	/**
	 * Returns the conditional probability {@code P(H|E)} of the hypothesis {@code H} given the
	 * event {@code E} using Baye's theorem and the law of total probability where
	 * {@code P(E) = Σ[P(H) * P(E|H)]} over {@code H}.
	 * <p>
	 * @param peh  the conditional probability {@code P(E|H)} of the event {@code E} given the
	 *             hypothesis {@code H}
	 * @param ph   the probability {@code P(H)} of the hypothesis {@code H}
	 * @param pehs the array of conditional probability {@code P(H|E)} of the hypothesis {@code H}
	 *             given all event {@code E}
	 * @param phs  the array of probability {@code P(H)} for all hypothesis {@code H}
	 * <p>
	 * @return the conditional probability {@code P(H|E)} of the hypothesis {@code H} given the
	 *         event {@code E} using Baye's theorem and the law of total probability where
	 *         {@code P(E) = Σ[P(H) * P(E|H)]} over {@code H}
	 */
	public static double conditionalProbability(final double peh, final double ph,
			final double[] pehs, final double[] phs) {
		return conditionalProbability(peh, ph, Maths.weightedSum(pehs, phs));
	}
}
