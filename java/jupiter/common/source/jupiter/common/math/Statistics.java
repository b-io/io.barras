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

		// Return the mean
		return Maths.sumWithoutNaN(values) / values.length;
	}

	public static double mean(final Number[] numbers) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(numbers, "numbers");

		// Return the mean
		return Maths.sumWithoutNaN(numbers) / numbers.length;
	}

	public static double mean(final Collection<? extends Number> numbers) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(numbers, "numbers");

		// Return the mean
		return Maths.sumWithoutNaN(numbers) / numbers.size();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double variance(final double... values) {
		return varianceWith(values, mean(values));
	}

	public static double variance(final Number[] numbers) {
		return Statistics.varianceWith(numbers, Statistics.mean(numbers));
	}

	public static double variance(final Collection<? extends Number> numbers) {
		return varianceWith(numbers, mean(numbers));
	}

	public static double varianceWith(final double[] values, final double mean) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(values);

		// Return the variance
		return Maths.sumOfSquaresWithoutNaN(values, mean) / values.length;
	}

	public static double varianceWith(final Number[] numbers, final double mean) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(numbers, "numbers");

		// Return the variance
		return Maths.sumOfSquaresWithoutNaN(numbers, mean) / numbers.length;
	}

	public static double varianceWith(final Collection<? extends Number> numbers,
			final double mean) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(numbers, "numbers");

		// Return the variance
		return Maths.sumOfSquaresWithoutNaN(numbers, mean) / numbers.size();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sampleVariance(final double... values) {
		return sampleVarianceWith(values, mean(values));
	}

	public static double sampleVariance(final Number[] numbers) {
		return Statistics.sampleVarianceWith(numbers, Statistics.mean(numbers));
	}

	public static double sampleVariance(final Collection<? extends Number> numbers) {
		return sampleVarianceWith(numbers, mean(numbers));
	}

	public static double sampleVarianceWith(final double[] values, final double mean) {
		// Check the arguments
		DoubleArguments.requireMinLength(values, 2);

		// Return the sample variance
		return Maths.sumOfSquaresWithoutNaN(values, mean) / (values.length - 1);
	}

	public static double sampleVarianceWith(final Number[] numbers, final double mean) {
		// Check the arguments
		ArrayArguments.requireMinLength(numbers, 2);

		// Return the sample variance
		return Maths.sumOfSquaresWithoutNaN(numbers, mean) / (numbers.length - 1);
	}

	public static double sampleVarianceWith(final Collection<? extends Number> numbers,
			final double mean) {
		// Check the arguments
		CollectionArguments.requireMinSize(numbers, 2);

		// Return the sample variance
		return Maths.sumOfSquaresWithoutNaN(numbers, mean) / (numbers.size() - 1);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double standardDeviation(final double... values) {
		return Maths.sqrt(variance(values));
	}

	public static double standardDeviation(final Number[] numbers) {
		return Maths.sqrt(Statistics.variance(numbers));
	}

	public static double standardDeviation(final Collection<? extends Number> numbers) {
		return Maths.sqrt(variance(numbers));
	}

	public static double standardDeviationWith(final double[] values, final double mean) {
		return Maths.sqrt(varianceWith(values, mean));
	}

	public static double standardDeviationWith(final Number[] numbers, final double mean) {
		return Maths.sqrt(Statistics.varianceWith(numbers, mean));
	}

	public static double standardDeviationWith(final Collection<? extends Number> numbers,
			final double mean) {
		return Maths.sqrt(varianceWith(numbers, mean));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sampleStandardDeviation(final double... values) {
		return Maths.sqrt(sampleVariance(values));
	}

	public static double sampleStandardDeviation(final Number[] numbers) {
		return Maths.sqrt(Statistics.sampleVariance(numbers));
	}

	public static double sampleStandardDeviation(final Collection<? extends Number> collection) {
		return Maths.sqrt(sampleVariance(collection));
	}

	public static double sampleStandardDeviationWith(final double[] values, final double mean) {
		return Maths.sqrt(sampleVarianceWith(values, mean));
	}

	public static double sampleStandardDeviationWith(final Number[] numbers, final double mean) {
		return Maths.sqrt(Statistics.sampleVarianceWith(numbers, mean));
	}

	public static double sampleStandardDeviationWith(final Collection<? extends Number> numbers,
			final double mean) {
		return Maths.sqrt(sampleVarianceWith(numbers, mean));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double variation(final long sampleSize, final double standardDeviation) {
		return Maths.DEFAULT_Z * standardDeviation / Maths.sqrt(sampleSize);
	}

	public static double variation(final long sampleSize, final double standardDeviation,
			final double confidence) {
		return normalCDFInverse(confidence) * standardDeviation / Maths.sqrt(sampleSize);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static DoubleInterval confidenceInterval(final long sampleSize, final double mean,
			final double standardDeviation) {
		final double variation = variation(sampleSize, standardDeviation);
		return new DoubleInterval(mean - variation, mean + variation);
	}

	public static DoubleInterval confidenceInterval(final long sampleSize, final double mean,
			final double standardDeviation, final double alpha) {
		final double variation = variation(sampleSize, standardDeviation, alpha);
		return new DoubleInterval(mean - variation, mean + variation);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns phi(x) = standard Gaussian PDF.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return phi(x) = standard Gaussian PDF
	 */
	public static double normalPDF(final double x) {
		return Maths.exp(-Maths.square(x) / 2.) / Maths.SQUARE_ROOT_OF_2_PI;
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
	public static double normalPDF(final double x, final double mu, final double sigma) {
		return normalPDF((x - mu) / sigma) / sigma;
	}

	/**
	 * Returns Phi(z) = standard Gaussian CDF using Taylor approximation.
	 * <p>
	 * @param z a {@code double} value
	 * <p>
	 * @return Phi(z) = standard Gaussian CDF using Taylor approximation
	 */
	public static double normalCDF(final double z) {
		return normalCDF(z, Maths.TOLERANCE);
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
	public static double normalCDF(final double z, final double tolerance) {
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
		return 0.5 + sum * normalPDF(z);
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
	public static double normalCDF(final double z, final double mu, final double sigma) {
		return normalCDF((z - mu) / sigma);
	}

	/**
	 * Returns {@code z} such that {@code Phi(z) = alpha} via bisection search.
	 * <p>
	 * @param alpha a {@code double} value
	 * <p>
	 * @return {@code z} such that {@code Phi(z) = alpha} via bisection search
	 */
	public static double normalCDFInverse(final double alpha) {
		return normalCDFInverse(alpha, Maths.TOLERANCE, -10., 10.);
	}

	/**
	 * Returns {@code z} such that {@code Phi(z) = alpha} via bisection search within the specified
	 * tolerance level.
	 * <p>
	 * @param alpha     a {@code double} value
	 * @param tolerance the tolerance level
	 * @param from      the {@code double} lower bound of the bisection search
	 * @param to        the {@code double} upper bound of the bisection search
	 * <p>
	 * @return {@code z} such that {@code Phi(z) = alpha} via bisection search within the specified
	 *         tolerance level
	 */
	protected static double normalCDFInverse(final double alpha, final double tolerance,
			final double from, final double to) {
		final double middle = Doubles.middle(from, to);
		// Test the convergence
		if (to - from <= tolerance) {
			return middle;
		}
		// Find the normal CDF inverse via bisection search
		if (normalCDF(middle) > alpha) {
			return normalCDFInverse(alpha, tolerance, from, middle);
		}
		return normalCDFInverse(alpha, tolerance, middle, to);
	}
}
