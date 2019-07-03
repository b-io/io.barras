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

import jupiter.common.test.IntegerArguments;

public class DynamicSample
		extends OnlineStatistic {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The minimum sample size (2 for the first sample standard deviation and 1 for the second one).
	 */
	public static final int DEFAULT_MIN_SAMPLE_SIZE = 3;
	/**
	 * The maximum sample size.
	 */
	public static final int DEFAULT_MAX_SAMPLE_SIZE = Integer.MAX_VALUE;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The minimum sample size.
	 */
	protected int minSampleSize;
	/**
	 * The maximum sample size.
	 */
	protected int maxSampleSize;
	/**
	 * The previous sample mean.
	 */
	protected double previousSampleMean;
	/**
	 * The previous confidence interval.
	 */
	protected double previousConfidenceInterval;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link DynamicSample}.
	 */
	public DynamicSample() {
		this(DEFAULT_MIN_SAMPLE_SIZE);
	}

	/**
	 * Constructs a {@link DynamicSample} with the specified minimum sample size.
	 * <p>
	 * @param minSampleSize the minimum sample size
	 */
	public DynamicSample(final int minSampleSize) {
		this(minSampleSize, DEFAULT_MAX_SAMPLE_SIZE);
	}

	/**
	 * Constructs a {@link DynamicSample} with the specified minimum and maximum sample sizes.
	 * <p>
	 * @param minSampleSize the minimum sample size
	 * @param maxSampleSize the maximum sample size
	 */
	public DynamicSample(final int minSampleSize, final int maxSampleSize) {
		// Check the arguments
		IntegerArguments.requireGreaterOrEqualTo(minSampleSize, DEFAULT_MIN_SAMPLE_SIZE);
		IntegerArguments.requireGreaterOrEqualTo(maxSampleSize, minSampleSize);

		// Set the minimum and maximum sample sizes
		this.minSampleSize = minSampleSize;
		this.maxSampleSize = maxSampleSize;
		// Reset the fields
		reset();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the sample mean.
	 * <p>
	 * @return the sample mean
	 */
	@Override
	public double getSampleMean() {
		if (sampleSize < minSampleSize && previousSampleMean != Double.NaN) {
			return previousSampleMean;
		}
		return sampleMean;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the minimum sample size.
	 * <p>
	 * @param minSampleSize an {@code int} value
	 */
	public void setMinSampleSize(final int minSampleSize) {
		this.minSampleSize = minSampleSize;
	}

	/**
	 * Sets the maximum sample size.
	 * <p>
	 * @param maxSampleSize an {@code int} value
	 */
	public void setMaxSampleSize(final int maxSampleSize) {
		this.maxSampleSize = maxSampleSize;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Updates the sample mean and variance with the specified {@code double} value.
	 * <p>
	 * @param x a {@code double} value
	 */
	public void sample(final double x) {
		// Update the previous sample standard deviation
		if (sampleSize % (minSampleSize - 1) == 0) {
			previousConfidenceInterval = getSampleMeanConfidenceInterval();
		}
		// Update the sample mean and variance
		update(x);
	}

	/**
	 * Resamples and returns {@code true} if the size of the sample is greater than
	 * {@code maxSampleSize} or the precision is decreasing, {@code false} otherwise.
	 * <p>
	 * @return {@code true} if the size of the sample is greater than {@code maxSampleSize} or the
	 *         precision is decreasing, {@code false} otherwise
	 */
	public boolean resample() {
		if (isResampling()) {
			previousSampleMean = sampleMean;
			reset();
			return true;
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the dynamic sample size is greater or equal to the lower bound and the
	 * precision is decreasing.
	 * <p>
	 * @return {@code true} if the dynamic sample size is greater or equal to the lower bound and
	 *         the precision is decreasing, {@code false} otherwise
	 */
	public boolean isPrecisionDecreasing() {
		final double sampleMeanConfidenceInterval = getSampleMeanConfidenceInterval();
		return sampleSize >= minSampleSize && sampleMeanConfidenceInterval != Double.NaN &&
				previousConfidenceInterval != Double.NaN &&
				sampleMeanConfidenceInterval >= previousConfidenceInterval;
	}

	/**
	 * Tests whether the size of the sample is greater than {@code maxSampleSize} or the precision
	 * is decreasing.
	 * <p>
	 * @return {@code true} if the size of the sample is greater than {@code maxSampleSize} or the
	 *         precision is decreasing, {@code false} otherwise
	 */
	public boolean isResampling() {
		return sampleSize == maxSampleSize || isPrecisionDecreasing();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ONLINE STATISTIC
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Resets the fields.
	 */
	@Override
	public void reset() {
		super.reset();
		previousConfidenceInterval = Double.NaN;
	}
}
