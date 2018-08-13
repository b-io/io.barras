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

import java.math.BigInteger;
import java.util.Collection;

import jupiter.common.util.Integers;
import jupiter.common.util.Longs;

public class Maths {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default tolerance level (or termination criterion) ε for float.
	 */
	public static final float DEFAULT_FLOAT_TOLERANCE = 1E-7f;
	/**
	 * The default tolerance level (or termination criterion) ε for double.
	 */
	public static final double DEFAULT_TOLERANCE = 1E-14;
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

	protected Maths() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int floorToInt(final double number) {
		return Integers.convert(Math.floor(number));
	}

	public static long floorToLong(final double number) {
		return Longs.convert(Math.floor(number));
	}

	public static int ceilToInt(final double number) {
		return Integers.convert(Math.ceil(number));
	}

	public static long ceilToLong(final double number) {
		return Longs.convert(Math.ceil(number));
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

	public static long gcd(final long a, final long b) {
		long i = a, j = b, t;
		while (j > 0) {
			t = j;
			j = i % t;
			i = t;
		}
		return i;
	}

	public static long lcm(final long a, final long b) {
		return a * b / gcd(a, b);
	}

	public static double square(final double x) {
		return x * x;
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

	public static double sum(final double... array) {
		double sum = 0.;
		for (final double element : array) {
			sum += element;
		}
		return sum;
	}

	public static <T extends Number> double sum(final T... array) {
		double sum = 0.;
		for (final T element : array) {
			if (element != null) {
				sum += element.doubleValue();
			}
		}
		return sum;
	}

	public static <T extends Number> double sum(final Collection<T> collection) {
		double sum = 0.;
		for (final T element : collection) {
			if (element != null) {
				sum += element.doubleValue();
			}
		}
		return sum;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sumWithoutNaN(final double... array) {
		double sum = 0.;
		for (final double element : array) {
			if (!Double.isNaN(element)) {
				sum += element;
			}
		}
		return sum;
	}

	public static <T extends Number> double sumWithoutNaN(final T... array) {
		double sum = 0.;
		for (final T element : array) {
			if (element != null) {
				final double elementValue = element.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += elementValue;
				}
			}
		}
		return sum;
	}

	public static <T extends Number> double sumWithoutNaN(final Collection<T> collection) {
		double sum = 0.;
		for (final T element : collection) {
			if (element != null) {
				final double elementValue = element.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += elementValue;
				}
			}
		}
		return sum;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sumOfSquares(final double[] array, final double mean) {
		double sum = 0.;
		for (final double element : array) {
			sum += square(element - mean);
		}
		return sum;
	}

	public static <T extends Number> double sumOfSquares(final T[] array, final double mean) {
		double sum = 0.;
		for (final T element : array) {
			if (element != null) {
				sum += square(element.doubleValue() - mean);
			}
		}
		return sum;
	}

	public static <T extends Number> double sumOfSquares(final Collection<T> collection,
			final double mean) {
		double sum = 0.;
		for (final T element : collection) {
			if (element != null) {
				sum += square(element.doubleValue() - mean);
			}
		}
		return sum;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double sumOfSquaresWithoutNaN(final double[] array, final double mean) {
		double sum = 0.;
		for (final double element : array) {
			if (!Double.isNaN(element)) {
				sum += square(element - mean);
			}
		}
		return sum;
	}

	public static <T extends Number> double sumOfSquaresWithoutNaN(final T[] array,
			final double mean) {
		double sum = 0.;
		for (final T element : array) {
			if (element != null) {
				final double elementValue = element.doubleValue();
				if (!Double.isNaN(elementValue)) {
					sum += square(elementValue - mean);
				}
			}
		}
		return sum;
	}

	public static <T extends Number> double sumOfSquaresWithoutNaN(final Collection<T> collection,
			final double mean) {
		double sum = 0.;
		for (final T element : collection) {
			if (element != null) {
				final double elementValue = element.doubleValue();
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
