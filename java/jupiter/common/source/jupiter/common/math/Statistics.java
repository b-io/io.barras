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
package jupiter.common.math;

import java.util.Collection;

import jupiter.common.test.ArrayArguments;
import jupiter.common.test.CollectionArguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.util.Doubles;

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

	public static double mean(final double... values) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(values);

		// Get the mean
		return Maths.sumWithoutNaN(values) / values.length;
	}

	public static <T extends Number> double getMean(final T... numbers) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(numbers);

		// Get the mean
		return Maths.sumWithoutNaN(numbers) / numbers.length;
	}

	public static <T extends Number> double getMean(final Collection<T> numbers) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(numbers);

		// Get the mean
		return Maths.sumWithoutNaN(numbers) / numbers.size();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double variance(final double... values) {
		return varianceWith(values, mean(values));
	}

	public static <T extends Number> double getVariance(final T... numbers) {
		return Statistics.getVarianceWith(numbers, Statistics.getMean(numbers));
	}

	public static <T extends Number> double getVariance(final Collection<T> numbers) {
		return getVarianceWith(numbers, getMean(numbers));
	}

	public static double varianceWith(final double[] values, final double mean) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(values);

		// Get the variance
		return Maths.sumOfSquaresWithoutNaN(values, mean) / values.length;
	}

	public static <T extends Number> double getVarianceWith(final T[] numbers, final double mean) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(numbers);

		// Get the variance
		return Maths.sumOfSquaresWithoutNaN(numbers, mean) / numbers.length;
	}

	public static <T extends Number> double getVarianceWith(final Collection<T> numbers,
			final double mean) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(numbers);

		// Get the variance
		return Maths.sumOfSquaresWithoutNaN(numbers, mean) / numbers.size();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sampleVariance(final double... values) {
		return sampleVarianceWith(values, mean(values));
	}

	public static <T extends Number> double getSampleVariance(final T... numbers) {
		return Statistics.getSampleVarianceWith(numbers, Statistics.getMean(numbers));
	}

	public static <T extends Number> double getSampleVariance(final Collection<T> numbers) {
		return getSampleVarianceWith(numbers, getMean(numbers));
	}

	public static double sampleVarianceWith(final double[] values, final double mean) {
		// Check the arguments
		DoubleArguments.requireMinLength(values, 2);

		// Get the sample variance
		return Maths.sumOfSquaresWithoutNaN(values, mean) / (values.length - 1);
	}

	public static <T extends Number> double getSampleVarianceWith(final T[] numbers,
			final double mean) {
		// Check the arguments
		ArrayArguments.requireMinLength(numbers, 2);

		// Get the sample variance
		return Maths.sumOfSquaresWithoutNaN(numbers, mean) / (numbers.length - 1);
	}

	public static <T extends Number> double getSampleVarianceWith(final Collection<T> numbers,
			final double mean) {
		// Check the arguments
		CollectionArguments.requireMinSize(numbers, 2);

		// Get the sample variance
		return Maths.sumOfSquaresWithoutNaN(numbers, mean) / (numbers.size() - 1);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double standardDeviation(final double... values) {
		return Math.sqrt(variance(values));
	}

	public static <T extends Number> double getStandardDeviation(final T... numbers) {
		return Math.sqrt(Statistics.getVariance(numbers));
	}

	public static <T extends Number> double getStandardDeviation(final Collection<T> numbers) {
		return Math.sqrt(getVariance(numbers));
	}

	public static double standardDeviationWith(final double[] values, final double mean) {
		return Math.sqrt(varianceWith(values, mean));
	}

	public static <T extends Number> double getStandardDeviationWith(final T[] numbers,
			final double mean) {
		return Math.sqrt(Statistics.getVarianceWith(numbers, mean));
	}

	public static <T extends Number> double getStandardDeviationWith(final Collection<T> numbers,
			final double mean) {
		return Math.sqrt(getVarianceWith(numbers, mean));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sampleStandardDeviation(final double... values) {
		return Math.sqrt(sampleVariance(values));
	}

	public static <T extends Number> double getSampleStandardDeviation(final T... numbers) {
		return Math.sqrt(Statistics.getSampleVariance(numbers));
	}

	public static <T extends Number> double getSampleStandardDeviation(
			final Collection<T> collection) {
		return Math.sqrt(getSampleVariance(collection));
	}

	public static double sampleStandardDeviationWith(final double[] values, final double mean) {
		return Math.sqrt(sampleVarianceWith(values, mean));
	}

	public static <T extends Number> double getSampleStandardDeviationWith(final T[] numbers,
			final double mean) {
		return Math.sqrt(Statistics.getSampleVarianceWith(numbers, mean));
	}

	public static <T extends Number> double getSampleStandardDeviationWith(
			final Collection<T> numbers, final double mean) {
		return Math.sqrt(getSampleVarianceWith(numbers, mean));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double variation(final long sampleSize, final double standardDeviation) {
		return Maths.DEFAULT_Z * standardDeviation / Math.sqrt(sampleSize);
	}

	public static double variation(final long sampleSize, final double standardDeviation,
			final double confidence) {
		return normalCdfInverse(confidence) * standardDeviation / Math.sqrt(sampleSize);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Interval<Double> confidenceInterval(final long sampleSize, final double mean,
			final double standardDeviation) {
		final double variation = variation(sampleSize, standardDeviation);
		return new Interval<Double>(mean - variation, mean + variation, true, true);
	}

	public static Interval<Double> confidenceInterval(final long sampleSize, final double mean,
			final double standardDeviation, final double alpha) {
		final double variation = variation(sampleSize, standardDeviation, alpha);
		return new Interval<Double>(mean - variation, mean + variation, true, true);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns phi(x) = standard Gaussian PDF.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return phi(x) = standard Gaussian PDF
	 */
	public static double normalPdf(final double x) {
		return Math.exp(-Maths.square(x) / 2.) / Maths.SQUARE_ROOT_OF_2_PI;
	}

	/**
	 * Returns phi(x, mu, signma) = Gaussian PDF with mean {@code mu} and standard deviation
	 * {@code sigma}.
	 * <p>
	 * @param x     a {@code double} value
	 * @param mu    the mean of the distribution
	 * @param sigma the standard deviation of the distribution
	 * <p>
	 * @return phi(x, mu, signma) = Gaussian PDF with mean {@code mu} and standard deviation
	 *         {@code sigma}
	 */
	public static double normalPdf(final double x, final double mu, final double sigma) {
		return normalPdf((x - mu) / sigma) / sigma;
	}

	/**
	 * Returns Phi(z) = standard Gaussian CDF using Taylor approximation.
	 * <p>
	 * @param z a {@code double} value
	 * <p>
	 * @return Phi(z) = standard Gaussian CDF using Taylor approximation
	 */
	public static double normalCdf(final double z) {
		return normalCdf(z, Maths.TOLERANCE);
	}

	/**
	 * Returns Phi(z) = standard Gaussian CDF using Taylor approximation within the specified
	 * tolerance level.
	 * <p>
	 * @param z         a {@code double} value
	 * @param tolerance the tolerance level
	 * <p>
	 * @return Phi(z) = standard Gaussian CDF using Taylor approximation within the specified
	 *         tolerance level
	 */
	public static double normalCdf(final double z, final double tolerance) {
		if (z < -10.) {
			return 0.;
		}
		if (z == 0.) {
			return 0.5;
		}
		if (z > 10.) {
			return 1.;
		}
		double sum = 0., term = z;
		for (long i = 3L; term > tolerance; i += 2L) {
			sum += term;
			term *= Maths.square(z) / i;
		}
		return 0.5 + sum * normalPdf(z);
	}

	/**
	 * Returns Phi(z, mu, sigma) = Gaussian CDF with mean {@code mu} and standard deviation
	 * {@code sigma}.
	 * <p>
	 * @param z     a {@code double} value
	 * @param mu    the mean of the distribution
	 * @param sigma the standard deviation of the distribution
	 * <p>
	 * @return Phi(z, mu, sigma) = Gaussian CDF with mean {@code mu} and standard deviation
	 *         {@code sigma}
	 */
	public static double normalCdf(final double z, final double mu, final double sigma) {
		return normalCdf((z - mu) / sigma);
	}

	/**
	 * Returns {@code z} such that {@code Phi(z) = alpha} via bisection search.
	 * <p>
	 * @param alpha a {@code double} value
	 * <p>
	 * @return {@code z} such that {@code Phi(z) = alpha} via bisection search
	 */
	public static double normalCdfInverse(final double alpha) {
		return normalCdfInverse(alpha, Maths.TOLERANCE, -10., 10.);
	}

	/**
	 * Returns {@code z} such that {@code Phi(z) = alpha} via bisection search within the specified
	 * tolerance level.
	 * <p>
	 * @param alpha      a {@code double} value
	 * @param tolerance  the tolerance level
	 * @param lowerBound the lower bound of the search
	 * @param upperBound the upper bound of the search
	 * <p>
	 * @return {@code z} such that {@code Phi(z) = alpha} via bisection search within the specified
	 *         tolerance level
	 */
	protected static double normalCdfInverse(final double alpha, final double tolerance,
			final double lowerBound, final double upperBound) {
		final double middle = Doubles.middle(lowerBound, upperBound);
		// Test the convergence
		if (upperBound - lowerBound <= tolerance) {
			return middle;
		}
		if (normalCdf(middle) > alpha) {
			return normalCdfInverse(alpha, tolerance, lowerBound, middle);
		}
		return normalCdfInverse(alpha, tolerance, middle, upperBound);
	}
}
