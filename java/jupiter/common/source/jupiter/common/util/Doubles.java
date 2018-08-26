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
package jupiter.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jupiter.common.map.parser.DoubleParser;
import jupiter.common.math.Maths;
import jupiter.common.struct.list.ExtendedList;

public class Doubles {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final double[] EMPTY_PRIMITIVE_ARRAY = new double[] {};
	public static final Double[] EMPTY_ARRAY = new Double[] {};

	protected static final DoubleParser PARSER = new DoubleParser();

	public static volatile Random RANDOM = new Random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Doubles() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double getDecimalPart(final double value) {
		return value - Math.floor(value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Double} from the specified {@link Object}.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return a {@link Double} from the specified {@link Object}
	 */
	public static Double convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code double} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object a {@code T} object
	 * <p>
	 * @return a {@code double} value from the specified {@code T} object
	 */
	public static <T> double toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	/**
	 * Returns an array of {@code double} values from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an array of {@code double} values from the specified array of type {@code T}
	 */
	public static <T> double[] toPrimitiveArray(final T... array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a 2D array of {@code double} values from the specified 2D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array2D a 2D array of type {@code T}
	 * <p>
	 * @return a 2D array of {@code double} values from the specified 2D array of type {@code T}
	 */
	public static <T> double[][] toPrimitiveArray2D(final T[]... array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@code double} values from the specified 3D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array3D a 3D array of type {@code T}
	 * <p>
	 * @return a 3D array of {@code double} values from the specified 3D array of type {@code T}
	 */
	public static <T> double[][][] toPrimitiveArray3D(final T[][]... array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns an array of {@code double} values from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@code double} values from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> double[] collectionToPrimitiveArray(final Collection<T> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Double} from the specified array of {@code double} values.
	 * <p>
	 * @param array an array of {@code double} values
	 * <p>
	 * @return an array of {@link Double} from the specified array of {@code double} values
	 */
	public static Double[] toArray(final double... array) {
		final Double[] result = new Double[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Returns a 2D array of {@link Double} from the specified 2D array of {@code double} values.
	 * <p>
	 * @param array2D a 2D array of {@code double} values
	 * <p>
	 * @return a 2D array of {@link Double} from the specified 2D array of {@code double} values
	 */
	public static Double[][] toArray2D(final double[]... array2D) {
		final Double[][] result = new Double[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = toArray(array2D[i]);
		}
		return result;
	}

	/**
	 * Returns a 3D array of {@link Double} from the specified 3D array of {@code double} values.
	 * <p>
	 * @param array3D a 3D array of {@code double} values
	 * <p>
	 * @return a 3D array of {@link Double} from the specified 3D array of {@code double} values
	 */
	public static Double[][][] toArray3D(final double[][]... array3D) {
		final Double[][][] result = new Double[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = toArray2D(array3D[i]);
		}
		return result;
	}

	/**
	 * Returns an array of {@link Double} from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@link Double} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Double[] collectionToArray(final Collection<T> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link List} of {@link Double} from the specified array of {@code double} values.
	 * <p>
	 * @param array an array of {@code double} values
	 * <p>
	 * @return a {@link List} of {@link Double} from the specified array of {@code double} values
	 */
	public static List<Double> toList(final double... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Double} from the specified array of {@code double}
	 * values.
	 * <p>
	 * @param array an array of {@code double} values
	 * <p>
	 * @return an {@link ExtendedList} of {@link Double} from the specified array of {@code double}
	 *         values
	 */
	public static ExtendedList<Double> toExtendedList(final double... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Double} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Double} from the specified array of type {@code T}
	 */
	public static <T> List<Double> toList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Double} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Double} from the specified array of type {@code T}
	 */
	public static <T> ExtendedList<Double> toExtendedList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Double} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Double} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> List<Double> collectionToList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Double} from the specified {@link Collection} of
	 * type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Double} from the specified {@link Collection} of
	 *         type {@code T}
	 */
	public static <T> ExtendedList<Double> collectionToExtendedList(
			final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Double} from the specified array of {@code double} values.
	 * <p>
	 * @param array an array of {@code double} values
	 * <p>
	 * @return a {@link Set} of {@link Double} from the specified array of {@code double} values
	 */
	public static Set<Double> toSet(final double... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Double} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Double} from the specified array of type {@code T}
	 */
	public static <T> Set<Double> toSet(final T... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Double} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Double} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> Set<Double> collectionToSet(final Collection<T> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code double} value between {@code 0.0} and
	 * {@code 1.0}.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code double} value between {@code 0.0} and
	 *         {@code 1.0}
	 */
	public static double random() {
		return RANDOM.nextDouble();
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code double} value between {@code lowerBound}
	 * (inclusive) and {@code upperBound} (exclusive).
	 * <p>
	 * @param lowerBound the lower bound of the {@code double} value to create
	 * @param upperBound the upper bound of the {@code double} value to create
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code double} value between {@code lowerBound}
	 *         (inclusive) and {@code upperBound} (exclusive)
	 */
	public static double random(final double lowerBound, final double upperBound) {
		return lowerBound + RANDOM.nextDouble() * (upperBound - lowerBound);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of {@code double} values of the specified length containing the sequence of
	 * numbers starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return an array of {@code double} values of the specified length containing the sequence of
	 *         numbers starting with zero and spaced by one
	 */
	public static double[] createSequence(final int length) {
		return createSequence(length, 0, 1);
	}

	/**
	 * Creates an array of {@code double} values of the specified length containing the sequence of
	 * numbers starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return an array of {@code double} values of the specified length containing the sequence of
	 *         numbers starting with {@code from} and spaced by one
	 */
	public static double[] createSequence(final int length, final double from) {
		return createSequence(length, from, 1);
	}

	/**
	 * Creates an array of {@code double} values of the specified length containing the sequence of
	 * numbers starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return an array of {@code double} values of the specified length containing the sequence of
	 *         numbers starting with {@code from} and spaced by {@code step}
	 */
	public static double[] createSequence(final int length, final double from, final double step) {
		final double[] array = new double[length];
		double value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of random {@code double} values of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return an array of random {@code double} values of the specified length
	 */
	public static double[] createRandomSequence(final int length) {
		final double[] array = new double[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates an array of {@code double} values of the specified length containing pseudorandom,
	 * uniformly distributed {@code double} values between {@code lowerBound} (inclusive) and
	 * {@code upperBound} (exclusive).
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create
	 * @param upperBound the upper bound of the random sequence to create
	 * <p>
	 * @return an array of {@code double} values of the specified length containing pseudorandom,
	 *         uniformly distributed {@code double} values between {@code lowerBound} (inclusive)
	 *         and {@code upperBound} (exclusive)
	 */
	public static double[] createRandomSequence(final int length, final double lowerBound,
			final double upperBound) {
		final double[] array = new double[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} percentage representation of the specified value.
	 * <p>
	 * @param value a {@code double} value
	 * <p>
	 * @return a {@link String} percentage representation of the specified value
	 */
	public static String toPercentage(final double value) {
		return value * 100. + "%";
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final double[] array, final double value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final double[][] array2D, final double value) {
		for (final double[] element : array2D) {
			fill(element, value);
		}
	}

	public static void fill(final double[][][] array3D, final double value) {
		for (final double[][] element : array3D) {
			fill(element, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double[] take(final double[] array) {
		return take(array, 0, array.length);
	}

	public static double[] take(final double[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length);
		final double[] result = new double[maxLength];
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
	}

	public static double[] take(final double[][] array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static double[] take(final double[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static double[] take(final double[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length);
		final double[] result = new double[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, result, i * columnCount,
					columnCount);
		}
		return result;
	}

	public static double[] take(final double[][][] array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static double[] take(final double[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static double[] take(final double[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static double[] take(final double[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length);
		final int length = columnCount * depthCount;
		final double[] result = new double[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					result, i * length, length);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified 2D array of {@code double} values.
	 * <p>
	 * @param array2D a 2D array of {@code double} values
	 * <p>
	 * @return the transpose of the specified 2D array of {@code double} values
	 */
	public static double[][] transpose(final double[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final double[][] transpose = new double[m][n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i][j] = array2D[j][i];
			}
		}
		return transpose;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified values for order. Returns a negative integer, zero or a positive
	 * integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a a {@code double} value
	 * @param b another {@code double} value to compare with for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final double a, final double b) {
		if (a < b) {
			return -1; // neither value is NaN
		}
		if (a > b) {
			return 1; // neither value is NaN
		}
		// @note cannot use doubleToRawLongBits because of NaNs
		final long aBits = Double.doubleToLongBits(a);
		final long bBits = Double.doubleToLongBits(b);
		return aBits == bBits ? 0 : // the values are equal
				aBits < bBits ? -1 : // (-0., 0.) or (!NaN, NaN)
						1; // (0., -0.) or (NaN, !NaN)
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise.
	 * <p>
	 * @param a a {@code double} value
	 * @param b another {@code double} value to compare with {@code a} for equality
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final double a, final double b) {
		return equals(a, b, Maths.DEFAULT_TOLERANCE);
	}

	/**
	 * Returns {@code true} if {@code a} is equal to {@code b} within the specified tolerance level,
	 * {@code false} otherwise.
	 * <p>
	 * @param a         a {@code double} value
	 * @param b         another {@code double} value to compare with {@code a} for equality
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b} within the specified tolerance level,
	 *         {@code false} otherwise
	 */
	public static boolean equals(final double a, final double b, final double tolerance) {
		return Maths.delta(a, b) <= tolerance;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of {@code double} values.
	 * <p>
	 * @param array an array of {@code double} values
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code double} values
	 */
	public static String toString(final double... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code double} values
	 * joined by {@code delimiter} (and wrapped with parentheses if {@code useParentheses}).
	 * <p>
	 * @param array          an array of {@code double} values
	 * @param delimiter      a {@link String}
	 * @param useParentheses the option specifying whether to use parentheses
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code double} values
	 *         joined by {@code delimiter} (and wrapped with parentheses if {@code useParentheses})
	 */
	public static String toString(final double[] array, final String delimiter,
			final boolean useParentheses) {
		return Arrays.toString(toArray(array), delimiter, useParentheses);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Double}.
	 * <p>
	 * @param array an array of {@link Double}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Double}
	 */
	public static String toString(final Double... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Double} joined by
	 * {@code delimiter} (and wrapped with parentheses if {@code useParentheses}).
	 * <p>
	 * @param array          an array of {@link Double}
	 * @param delimiter      a {@link String}
	 * @param useParentheses the option specifying whether to use parentheses
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Double} joined by
	 *         {@code delimiter} (and wrapped with parentheses if {@code useParentheses})
	 */
	public static String toString(final Double[] array, final String delimiter,
			final boolean useParentheses) {
		return Arrays.toString(array, delimiter, useParentheses);
	}
}
