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

import java.math.BigInteger;
import java.util.Collection;

import jupiter.common.test.ArrayArguments;
import jupiter.common.test.CollectionArguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.test.FloatArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.test.LongArguments;
import jupiter.common.test.ShortArguments;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Shorts;

public class Maths {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default tolerance level (or termination criterion) ε for float.
	 */
	public static final float DEFAULT_FLOAT_TOLERANCE = 1E-6f;
	/**
	 * The default tolerance level (or termination criterion) ε for double.
	 */
	public static final double DEFAULT_TOLERANCE = 1E-12;
	/**
	 * The default tiny tolerance level for double.
	 */
	public static final double DEFAULT_TINY_TOLERANCE = 1E-300;

	public static final double DEFAULT_CONFIDENCE = 0.975;
	public static final double DEFAULT_Z = 1.9599639845400536; // 97.5%

	public static final double DEGREE_TO_RADIAN = Math.PI / 180.;

	public static final double SQUARE_ROOT_OF_TWO = Math.sqrt(2.);
	public static final double SQUARE_ROOT_OF_PI = Math.sqrt(Math.PI);
	public static final double SQUARE_ROOT_OF_TWO_PI = Math.sqrt(2. * Math.PI);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Maths}.
	 */
	protected Maths() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static short floorToShort(final double number) {
		return Shorts.convert(Math.floor(number));
	}

	public static int floorToInt(final double number) {
		return Integers.convert(Math.floor(number));
	}

	public static long floorToLong(final double number) {
		return Longs.convert(Math.floor(number));
	}

	//////////////////////////////////////////////

	public static short ceilToShort(final double number) {
		return Shorts.convert(Math.ceil(number));
	}

	public static int ceilToInt(final double number) {
		return Integers.convert(Math.ceil(number));
	}

	public static long ceilToLong(final double number) {
		return Longs.convert(Math.ceil(number));
	}

	//////////////////////////////////////////////

	public static short roundToShort(final double number) {
		return Shorts.convert(Math.round(number));
	}

	public static int roundToInt(final double number) {
		return Integers.convert(Math.round(number));
	}

	public static long roundToLong(final double number) {
		return Math.round(number);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double delta(final double a, final double b) {
		return Math.abs(a - b);
	}

	public static double factorial(final double n) {
		return roundToLong(n * gamma(n));
	}

	public static BigInteger factorial(final long n) {
		if (n == 0) {
			return BigInteger.ONE;
		}
		return BigInteger.valueOf(n).multiply(factorial(n - 1));
	}

	public static double gamma(final double z) {
		final double a = Math.sqrt(2 * Math.PI / z);
		double b = z + 1. / (12. * z - 1. / (10. * z));
		b = Math.pow(b / Math.E, z);
		return a * b;
	}

	/**
	 * Returns the greatest common divisor (GCD) of {@code a} and {@code b}.
	 * <p>
	 * @param a a {@code long} value
	 * @param b another {@code long} value
	 * <p>
	 * @return the greatest common divisor (GCD) of {@code a} and {@code b}
	 */
	public static long gcd(final long a, final long b) {
		long i = a, j = b, t;
		while (j > 0) {
			t = j;
			j = i % t;
			i = t;
		}
		return i;
	}

	/**
	 * Returns the least common multiple (LCM) of {@code a} and {@code b}.
	 * <p>
	 * @param a a {@code long} value
	 * @param b another {@code long} value
	 * <p>
	 * @return the least common multiple (LCM) of {@code a} and {@code b}
	 */
	public static long lcm(final long a, final long b) {
		return a * b / gcd(a, b);
	}

	/**
	 * Returns {@code number} rounded up to the nearest multiple of {@code unit}.
	 * <p>
	 * @param number the {@code int} value to round up
	 * @param unit   an {@code int} value
	 * <p>
	 * @return {@code number} rounded up to the nearest multiple of {@code unit}
	 */
	public static int roundUp(final int number, final int unit) {
		final int remainder = number % unit;
		if (remainder == 0) {
			return number;
		}
		return number + unit - remainder;
	}

	/**
	 * Returns {@code number} rounded up to the nearest multiple of {@code unit}.
	 * <p>
	 * @param number the {@code long} value to round up
	 * @param unit   a {@code long} value
	 * <p>
	 * @return {@code number} rounded up to the nearest multiple of {@code unit}
	 */
	public static long roundUp(final long number, final long unit) {
		final long remainder = number % unit;
		if (remainder == 0) {
			return number;
		}
		return number + unit - remainder;
	}

	public static double square(final double x) {
		return x * x;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static short maxToShort(final short... values) {
		// Check the arguments
		ShortArguments.requireNonEmpty(values);

		// Get the maximum
		short max = Short.MIN_VALUE;
		for (final short value : values) {
			max = value > max ? value : max;
		}
		return max;
	}

	public static int maxToInt(final int... values) {
		// Check the arguments
		IntegerArguments.requireNonEmpty(values);

		// Get the maximum
		int max = Integer.MIN_VALUE;
		for (final int value : values) {
			max = Math.max(max, value);
		}
		return max;
	}

	public static long maxToLong(final long... values) {
		// Check the arguments
		LongArguments.requireNonEmpty(values);

		// Get the maximum
		long max = Long.MIN_VALUE;
		for (final long value : values) {
			max = Math.max(max, value);
		}
		return max;
	}

	public static float maxToFloat(final float... values) {
		// Check the arguments
		FloatArguments.requireNonEmpty(values);

		// Get the maximum
		float max = Float.MIN_VALUE;
		for (final float value : values) {
			max = Math.max(max, value);
		}
		return max;
	}

	public static double maxToDouble(final double... values) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(values);

		// Get the maximum
		double max = Double.MIN_VALUE;
		for (final double value : values) {
			max = Math.max(max, value);
		}
		return max;
	}

	//////////////////////////////////////////////

	public static <T extends Number> double getMax(final T... numbers) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(numbers);

		// Get the maximum
		double max = Double.MIN_VALUE;
		for (final T number : numbers) {
			max = Math.max(max, number.doubleValue());
		}
		return max;
	}

	public static <T extends Number> double getMax(final Collection<T> numbers) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(numbers);

		// Get the maximum
		double max = Double.MIN_VALUE;
		for (final T number : numbers) {
			max = Math.max(max, number.doubleValue());
		}
		return max;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static short minToShort(final short... values) {
		// Check the arguments
		ShortArguments.requireNonEmpty(values);

		// Get the minimum
		short min = Short.MAX_VALUE;
		for (final short value : values) {
			min = value < min ? value : min;
		}
		return min;
	}

	public static int minToInt(final int... values) {
		// Check the arguments
		IntegerArguments.requireNonEmpty(values);

		// Get the minimum
		int min = Integer.MAX_VALUE;
		for (final int value : values) {
			min = Math.min(value, min);
		}
		return min;
	}

	public static long minToLong(final long... values) {
		// Check the arguments
		LongArguments.requireNonEmpty(values);

		// Get the minimum
		long min = Long.MAX_VALUE;
		for (final long value : values) {
			min = Math.min(value, min);
		}
		return min;
	}

	public static float minToFloat(final float... values) {
		// Check the arguments
		FloatArguments.requireNonEmpty(values);

		// Get the minimum
		float min = Float.MAX_VALUE;
		for (final float value : values) {
			min = Math.min(value, min);
		}
		return min;
	}

	public static double minToDouble(final double... values) {
		// Check the arguments
		DoubleArguments.requireNonEmpty(values);

		// Get the minimum
		double min = Double.MAX_VALUE;
		for (final double value : values) {
			min = Math.min(value, min);
		}
		return min;
	}

	//////////////////////////////////////////////

	public static <T extends Number> double getMin(final T... numbers) {
		// Check the arguments
		ArrayArguments.requireNonEmpty(numbers);

		// Get the minimum
		double min = Double.MAX_VALUE;
		for (final T number : numbers) {
			min = Math.min(number.doubleValue(), min);
		}
		return min;
	}

	public static <T extends Number> double getMin(final Collection<T> numbers) {
		// Check the arguments
		CollectionArguments.requireNonEmpty(numbers);

		// Get the minimum
		double min = Double.MAX_VALUE;
		for (final T number : numbers) {
			min = Math.min(number.doubleValue(), min);
		}
		return min;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ALGORITHMIC
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static long countPoints(final double min, final double max, final double increment) {
		return min <= max ? 1L + floorToLong((max - min) / increment) : 0L;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GEOMETRY
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the approximate edge length of the circumscribed square of the circle of the
	 * specified radius.
	 * <p>
	 * @param radius the radius of the circle
	 * <p>
	 * @return the approximate edge length of the circumscribed square of the circle of the
	 *         specified radius
	 */
	public static double getCircumscribedSquare(final double radius) {
		return 2. * radius;
	}

	/**
	 * Returns the approximate edge length of the inscribed square of the circle of the specified
	 * radius.
	 * <p>
	 * @param radius the radius of the circle
	 * <p>
	 * @return the approximate edge length of the inscribed square of the circle of the specified
	 *         radius
	 */
	public static double getInscribedSquare(final double radius) {
		return SQUARE_ROOT_OF_TWO * radius;
	}

	/**
	 * Returns the approximate edge length of the square that has the same area of the circle of the
	 * specified radius.
	 * <p>
	 * @param radius the radius of the circle
	 * <p>
	 * @return the approximate edge length of the square that has the same area of the circle of the
	 *         specified radius
	 */
	public static double getSquareWithSameAreaAsCircle(final double radius) {
		return SQUARE_ROOT_OF_PI * radius;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SUM
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sum(final double... values) {
		double sum = 0.;
		for (final double value : values) {
			sum += value;
		}
		return sum;
	}

	public static <T extends Number> double sum(final T... numbers) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				sum += number.doubleValue();
			}
		}
		return sum;
	}

	public static <T extends Number> double sum(final Collection<T> numbers) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				sum += number.doubleValue();
			}
		}
		return sum;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sumWithoutNaN(final double... values) {
		double sum = 0.;
		for (final double value : values) {
			if (!Double.isNaN(value)) {
				sum += value;
			}
		}
		return sum;
	}

	public static <T extends Number> double sumWithoutNaN(final T... numbers) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				final double elementValue = number.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += elementValue;
				}
			}
		}
		return sum;
	}

	public static <T extends Number> double sumWithoutNaN(final Collection<T> numbers) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				final double elementValue = number.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += elementValue;
				}
			}
		}
		return sum;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sumOfSquares(final double[] values, final double mean) {
		double sum = 0.;
		for (final double value : values) {
			sum += square(value - mean);
		}
		return sum;
	}

	public static <T extends Number> double sumOfSquares(final T[] values, final double mean) {
		double sum = 0.;
		for (final T value : values) {
			if (value != null) {
				sum += square(value.doubleValue() - mean);
			}
		}
		return sum;
	}

	public static <T extends Number> double sumOfSquares(final Collection<T> numbers,
			final double mean) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				sum += square(number.doubleValue() - mean);
			}
		}
		return sum;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sumOfSquaresWithoutNaN(final double[] values, final double mean) {
		double sum = 0.;
		for (final double value : values) {
			if (!Double.isNaN(value)) {
				sum += square(value - mean);
			}
		}
		return sum;
	}

	public static <T extends Number> double sumOfSquaresWithoutNaN(final T[] numbers,
			final double mean) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				final double elementValue = number.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += square(elementValue - mean);
				}
			}
		}
		return sum;
	}

	public static <T extends Number> double sumOfSquaresWithoutNaN(final Collection<T> numbers,
			final double mean) {
		double sum = 0.;
		for (final T number : numbers) {
			if (number != null) {
				final double elementValue = number.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += square(elementValue - mean);
				}
			}
		}
		return sum;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// TRIGONOMETRY
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sin(final double angle) {
		return Math.sin(angle * DEGREE_TO_RADIAN);
	}

	public static double cos(final double angle) {
		return Math.cos(angle * DEGREE_TO_RADIAN);
	}

	public static double tan(final double angle) {
		return Math.tan(angle * DEGREE_TO_RADIAN);
	}

	public static double cot(final double angle) {
		return 1. / Math.tan(angle * DEGREE_TO_RADIAN);
	}

	/**
	 * Returns the haversine of the specified angle.
	 * <p>
	 * @param angle a {@code double} value
	 * <p>
	 * @return the haversine of the specified angle
	 */
	public static double hav(final double angle) {
		return (1. - cos(angle)) / 2.;
	}
}
