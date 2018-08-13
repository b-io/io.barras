/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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

public class Statistics {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Statistics() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double getMin(final double... array) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(array);

		// Get the minimum
		double min = Double.POSITIVE_INFINITY;
		for (final double element : array) {
			if (element < min) {
				min = element;
			}
		}
		return min;
	}

	public static <T extends Number> double getMin(final T... array) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(array);

		// Get the minimum
		double min = Double.POSITIVE_INFINITY;
		for (final T element : array) {
			if (element != null && element.doubleValue() < min) {
				min = element.doubleValue();
			}
		}
		return min;
	}

	public static <T extends Number> double getMin(final Collection<T> collection) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(collection);

		// Get the minimum
		double min = Double.POSITIVE_INFINITY;
		for (final T element : collection) {
			if (element != null && element.doubleValue() < min) {
				min = element.doubleValue();
			}
		}
		return min;
	}

	public static double getMax(final double... array) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(array);

		// Get the maximum
		double max = Double.NEGATIVE_INFINITY;
		for (final double element : array) {
			if (element > max) {
				max = element;
			}
		}
		return max;
	}

	public static <T extends Number> double getMax(final T... array) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(array);

		// Get the maximum
		double max = Double.NEGATIVE_INFINITY;
		for (final T element : array) {
			if (element != null && element.doubleValue() > max) {
				max = element.doubleValue();
			}
		}
		return max;
	}

	public static <T extends Number> double getMax(final Collection<T> collection) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(collection);

		// Get the maximum
		double max = Double.NEGATIVE_INFINITY;
		for (final T element : collection) {
			if (element != null && element.doubleValue() > max) {
				max = element.doubleValue();
			}
		}
		return max;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double getMean(final double... array) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(array);

		// Get the mean
		return Maths.sumWithoutNaN(array) / array.length;
	}

	public static <T extends Number> double getMean(final T... array) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(array);

		// Get the mean
		return Maths.sumWithoutNaN(array) / array.length;
	}

	public static <T extends Number> double getMean(final Collection<T> collection) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(collection);

		// Get the mean
		return Maths.sumWithoutNaN(collection) / collection.size();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double getVariance(final double... array) {
		return getVarianceWith(array, getMean(array));
	}

	public static <T extends Number> double getVariance(final T... array) {
		return getVarianceWith(array, getMean(array));
	}

	public static <T extends Number> double getVariance(final Collection<T> collection) {
		return getVarianceWith(collection, getMean(collection));
	}

	public static double getVarianceWith(final double[] array, final double mean) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(array);

		// Get the variance
		return Maths.sumOfSquaresWithoutNaN(array, mean) / array.length;
	}

	public static <T extends Number> double getVarianceWith(final T[] array, final double mean) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(array);

		// Get the variance
		return Maths.sumOfSquaresWithoutNaN(array, mean) / array.length;
	}

	public static <T extends Number> double getVarianceWith(final Collection<T> collection,
			final double mean) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(collection);

		// Get the variance
		return Maths.sumOfSquaresWithoutNaN(collection, mean) / collection.size();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double getSampleVariance(final double... array) {
		return getSampleVarianceWith(array, getMean(array));
	}

	public static <T extends Number> double getSampleVariance(final T... array) {
		return getSampleVarianceWith(array, getMean(array));
	}

	public static <T extends Number> double getSampleVariance(final Collection<T> collection) {
		return getSampleVarianceWith(collection, getMean(collection));
	}

	public static double getSampleVarianceWith(final double[] array, final double mean) {
		// Check the arguments
		DoubleArguments.requireMinLength(array, 2);

		// Get the sample variance
		return Maths.sumOfSquaresWithoutNaN(array, mean) / (array.length - 1);
	}

	public static <T extends Number> double getSampleVarianceWith(final T[] array,
			final double mean) {
		// Check the arguments
		ArrayArguments.requireMinLength(array, 2);

		// Get the sample variance
		return Maths.sumOfSquaresWithoutNaN(array, mean) / (array.length - 1);
	}

	public static <T extends Number> double getSampleVarianceWith(final Collection<T> collection,
			final double mean) {
		// Check the arguments
		CollectionArguments.requireMinSize(collection, 2);

		// Get the sample variance
		return Maths.sumOfSquaresWithoutNaN(collection, mean) / (collection.size() - 1);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double getStandardDeviation(final double... array) {
		return Math.sqrt(getVariance(array));
	}

	public static <T extends Number> double getStandardDeviation(final T... array) {
		return Math.sqrt(getVariance(array));
	}

	public static <T extends Number> double getStandardDeviation(final Collection<T> collection) {
		return Math.sqrt(getVariance(collection));
	}

	public static double getStandardDeviationWith(final double[] array, final double mean) {
		return Math.sqrt(getVarianceWith(array, mean));
	}

	public static <T extends Number> double getStandardDeviationWith(final T[] array,
			final double mean) {
		return Math.sqrt(getVarianceWith(array, mean));
	}

	public static <T extends Number> double getStandardDeviationWith(final Collection<T> collection,
			final double mean) {
		return Math.sqrt(getVarianceWith(collection, mean));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double getSampleStandardDeviation(final double... array) {
		return Math.sqrt(getSampleVariance(array));
	}

	public static <T extends Number> double getSampleStandardDeviation(final T... array) {
		return Math.sqrt(getSampleVariance(array));
	}

	public static <T extends Number> double getSampleStandardDeviation(
			final Collection<T> collection) {
		return Math.sqrt(getSampleVariance(collection));
	}

	public static double getSampleStandardDeviationWith(final double[] array, final double mean) {
		return Math.sqrt(getSampleVarianceWith(array, mean));
	}

	public static <T extends Number> double getSampleStandardDeviationWith(final T[] array,
			final double mean) {
		return Math.sqrt(getSampleVarianceWith(array, mean));
	}

	public static <T extends Number> double getSampleStandardDeviationWith(
			final Collection<T> collection, final double mean) {
		return Math.sqrt(getSampleVarianceWith(collection, mean));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double getVariation(final long sampleSize, final double standardDeviation) {
		return Maths.DEFAULT_Z * standardDeviation / Math.sqrt(sampleSize);
	}

	public static double getVariation(final long sampleSize, final double standardDeviation,
			final double confidence) {
		return getNormalCdfInverse(confidence) * standardDeviation / Math.sqrt(sampleSize);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Interval<Double> getConfidenceInterval(final long sampleSize, final double mean,
			final double standardDeviation) {
		final double variation = getVariation(sampleSize, standardDeviation);
		return new Interval<Double>(mean - variation, mean + variation);
	}

	public static Interval<Double> getConfidenceInterval(final long sampleSize, final double mean,
			final double standardDeviation, final double alpha) {
		final double variation = getVariation(sampleSize, standardDeviation, alpha);
		return new Interval<Double>(mean - variation, mean + variation);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns phi(x) = standard Gaussian PDF.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return phi(x) = standard Gaussian PDF
	 */
	public static double getNormalPdf(final double x) {
		return Math.exp(-Maths.square(x) / 2.) / Maths.SQUARE_ROOT_OF_TWO_PI;
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
	public static double getNormalPdf(final double x, final double mu, final double sigma) {
		return getNormalPdf((x - mu) / sigma) / sigma;
	}

	/**
	 * Returns Phi(z) = standard Gaussian CDF using Taylor approximation.
	 * <p>
	 * @param z a {@code double} value
	 * <p>
	 * @return Phi(z) = standard Gaussian CDF using Taylor approximation
	 */
	public static double getNormalCdf(final double z) {
		return getNormalCdf(z, Maths.DEFAULT_TOLERANCE);
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
	public static double getNormalCdf(final double z, final double tolerance) {
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
		return 0.5 + sum * getNormalPdf(z);
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
	public static double getNormalCdf(final double z, final double mu, final double sigma) {
		return getNormalCdf((z - mu) / sigma);
	}

	/**
	 * Returns {@code z} such that {@code Phi(z) = alpha} via bisection search.
	 * <p>
	 * @param alpha a {@code double} value
	 * <p>
	 * @return {@code z} such that {@code Phi(z) = alpha} via bisection search
	 */
	public static double getNormalCdfInverse(final double alpha) {
		return getNormalCdfInverse(alpha, Maths.DEFAULT_TOLERANCE, -10., 10.);
	}

	/**
	 * Returns {@code z} such that {@code Phi(z) = alpha} via bisection search.
	 * <p>
	 * @param alpha      a {@code double} value
	 * @param tolerance  the tolerance level
	 * @param lowerBound the lower bound of the search
	 * @param upperBound the upper bound of the search
	 * <p>
	 * @return {@code z} such that {@code Phi(z) = alpha} via bisection search
	 */
	protected static double getNormalCdfInverse(final double alpha, final double tolerance,
			final double lowerBound, final double upperBound) {
		final double middle = lowerBound + (upperBound - lowerBound) / 2.;
		// Test the convergence
		if (upperBound - lowerBound <= tolerance) {
			return middle;
		}
		if (getNormalCdf(middle) > alpha) {
			return getNormalCdfInverse(alpha, tolerance, lowerBound, middle);
		}
		return getNormalCdfInverse(alpha, tolerance, middle, upperBound);
	}
}
