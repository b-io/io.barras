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

public class OnlineStatistic {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected int size;
	protected double mean, variance;
	protected double sum;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public OnlineStatistic() {
		reset();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @return the sample size
	 */
	public int getSampleSize() {
		return size;
	}

	/**
	 * @return the sample mean
	 */
	public double getSampleMean() {
		return mean;
	}

	/**
	 * @return the sample variance
	 */
	public double getSampleVariance() {
		return variance;
	}

	/**
	 * @return the sample standard deviation
	 */
	public double getSampleStandardDeviation() {
		return Math.sqrt(variance);
	}

	/**
	 * @return the confidence interval of the sample mean
	 */
	public double getSampleMeanConfidenceInterval() {
		return getSampleStandardDeviation() / Math.sqrt(size);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Updates the sample mean and variance with the specified {@code double} value.
	 * <p>
	 * @param x a {@code double} value
	 */
	public void update(final double x) {
		++size;
		final double delta = x - mean;
		mean += delta / size;
		sum += delta * (x - mean);
		variance = sum / (size - 1);
	}

	/**
	 * Resets {@code this}.
	 */
	public void reset() {
		size = 0;
		mean = 0.;
		variance = 0.;
		sum = 0.;
	}
}
