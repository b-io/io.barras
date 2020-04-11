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

import jupiter.common.math.Maths;

public class OnlineStatistic
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
	 * The sample size.
	 */
	protected int sampleSize;
	/**
	 * The sample mean and variance.
	 */
	protected double sampleMean, sampleVariance;
	/**
	 * The sum.
	 */
	protected double sum;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link OnlineStatistic}.
	 */
	public OnlineStatistic() {
		clear();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the sample size.
	 * <p>
	 * @return the sample size
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * Returns the sample mean.
	 * <p>
	 * @return the sample mean
	 */
	public double getSampleMean() {
		return sampleMean;
	}

	/**
	 * Returns the sample variance.
	 * <p>
	 * @return the sample variance
	 */
	public double getSampleVariance() {
		return sampleVariance;
	}

	/**
	 * Returns the sample standard deviation.
	 * <p>
	 * @return the sample standard deviation
	 */
	public double getSampleStandardDeviation() {
		return Maths.sqrt(sampleVariance);
	}

	/**
	 * Returns the confidence interval of the sample mean.
	 * <p>
	 * @return the confidence interval of the sample mean
	 */
	public double getSampleMeanConfidenceInterval() {
		return getSampleStandardDeviation() / Maths.sqrt(sampleSize);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clears {@code this}.
	 */
	public void clear() {
		sampleSize = 0;
		sampleMean = 0.;
		sampleVariance = 0.;
		sum = 0.;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Updates the sample mean and variance with the specified value.
	 * <p>
	 * @param x a {@code double} value
	 */
	public void update(final double x) {
		++sampleSize;
		final double delta = x - sampleMean;
		sampleMean += delta / sampleSize;
		sum += delta * (x - sampleMean);
		sampleVariance = sum / (sampleSize - 1);
	}
}
