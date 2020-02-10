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
package jupiter.math.statistics;

import java.io.Serializable;

import jupiter.common.util.Doubles;

/**
 * {@link StatisticalInference} is the process of using data analysis to deduce properties of an
 * underlying probability distribution.
 */
public abstract class StatisticalInference
		implements Serializable {

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
	 * The array of hypothesis probability {@code P(H)} for all hypothesis {@code H}.
	 */
	protected final double[] hypothesisProbabilities;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link StatisticalInference} with the specified number of hypotheses.
	 * <p>
	 * @param hypothesisCount the number of hypotheses
	 */
	public StatisticalInference(final int hypothesisCount) {
		this.hypothesisCount = hypothesisCount;
		hypothesisProbabilities = new double[hypothesisCount];
		reset();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Resets {@code this}.
	 */
	public void reset() {
		// Reset the hypothesis probability P(H) for all hypothesis H to equal probabilities
		Doubles.fill(hypothesisProbabilities, 1. / hypothesisCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Infers the hypothesis probability {@code P(H)} for all hypothesis {@code H} with the
	 * specified evidence {@code E}.
	 * <p>
	 * @param evidence the evidence {@code E} that is not yet used in the inference
	 */
	public abstract void infer(final double evidence);
}
