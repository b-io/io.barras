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

/**
 * {@link StatisticalModel} is a mathematical model that embodies a set of statistical assumptions
 * concerning the generation of sample data (and similar data from a larger population).
 */
public abstract class StatisticalModel
		implements Serializable {

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
	 * Constructs a {@link StatisticalModel}.
	 */
	protected StatisticalModel() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the likelihood of {@code this} given the specified evidence.
	 * <p>
	 * @param evidence a {@code double} value
	 * <p>
	 * @return the likelihood of {@code this} given the specified evidence
	 */
	public abstract double getLikelihood(final double evidence);

	/**
	 * Returns the likelihood of {@code this} given the specified evidences.
	 * <p>
	 * @param evidences a {@code double} array
	 * <p>
	 * @return the likelihood of {@code this} given the specified evidences
	 */
	public double getLikelihood(final double... evidences) {
		double likelihood = 1.;
		for (final double evidence : evidences) {
			likelihood *= getLikelihood(evidence);
		}
		return likelihood;
	}
}
