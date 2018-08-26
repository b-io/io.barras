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

import jupiter.common.map.parser.FloatParser;
import jupiter.common.map.parser.Parsers;
import jupiter.common.math.Maths;
import jupiter.common.struct.list.ExtendedList;

public class Floats {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final float[] EMPTY_PRIMITIVE_ARRAY = new float[] {};
	public static final Float[] EMPTY_ARRAY = new Float[] {};

	protected static final FloatParser PARSER = Parsers.FLOAT_PARSER;

	public static volatile Random RANDOM = new Random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Floats() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static float getDecimalPart(final float value) {
		return value - convert(Math.floor(value));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code float} value converted from the specified value.
	 * <p>
	 * @param value a {@code double} value
	 * <p>
	 * @return a {@code float} value converted from the specified value
	 */
	public static float convert(final double value) {
		if (value < Float.MIN_VALUE || value > Float.MAX_VALUE) {
			throw new ArithmeticException("Float under/overflow");
		}
		return (float) value;
	}

	/**
	 * Returns a {@link Float} from the specified {@link Object}.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return a {@link Float} from the specified {@link Object}
	 */
	public static Float convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code float} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object a {@code T} object
	 * <p>
	 * @return a {@code float} value from the specified {@code T} object
	 */
	public static <T> float toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	/**
	 * Returns an array of {@code float} values from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an array of {@code float} values from the specified array of type {@code T}
	 */
	public static <T> float[] toPrimitiveArray(final T... array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a 2D array of {@code float} values from the specified 2D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array2D a 2D array of type {@code T}
	 * <p>
	 * @return a 2D array of {@code float} values from the specified 2D array of type {@code T}
	 */
	public static <T> float[][] toPrimitiveArray2D(final T[]... array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@code float} values from the specified 3D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array3D a 3D array of type {@code T}
	 * <p>
	 * @return a 3D array of {@code float} values from the specified 3D array of type {@code T}
	 */
	public static <T> float[][][] toPrimitiveArray3D(final T[][]... array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns an array of {@code float} values from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@code float} values from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> float[] collectionToPrimitiveArray(final Collection<T> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Float} from the specified array of {@code float} values.
	 * <p>
	 * @param array an array of {@code float} values
	 * <p>
	 * @return an array of {@link Float} from the specified array of {@code float} values
	 */
	public static Float[] toArray(final float... array) {
		final Float[] result = new Float[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Returns a 2D array of {@link Float} from the specified 2D array of {@code float} values.
	 * <p>
	 * @param array2D a 2D array of {@code float} values
	 * <p>
	 * @return a 2D array of {@link Float} from the specified 2D array of {@code float} values
	 */
	public static Float[][] toArray2D(final float[]... array2D) {
		final Float[][] result = new Float[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = toArray(array2D[i]);
		}
		return result;
	}

	/**
	 * Returns a 3D array of {@link Float} from the specified 3D array of {@code float} values.
	 * <p>
	 * @param array3D a 3D array of {@code float} values
	 * <p>
	 * @return a 3D array of {@link Float} from the specified 3D array of {@code float} values
	 */
	public static Float[][][] toArray3D(final float[][]... array3D) {
		final Float[][][] result = new Float[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = toArray2D(array3D[i]);
		}
		return result;
	}

	/**
	 * Returns an array of {@link Float} from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@link Float} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Float[] collectionToArray(final Collection<T> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link List} of {@link Float} from the specified array of {@code float} values.
	 * <p>
	 * @param array an array of {@code float} values
	 * <p>
	 * @return a {@link List} of {@link Float} from the specified array of {@code float} values
	 */
	public static List<Float> toList(final float... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Float} from the specified array of {@code float}
	 * values.
	 * <p>
	 * @param array an array of {@code float} values
	 * <p>
	 * @return an {@link ExtendedList} of {@link Float} from the specified array of {@code float}
	 *         values
	 */
	public static ExtendedList<Float> toExtendedList(final float... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Float} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Float} from the specified array of type {@code T}
	 */
	public static <T> List<Float> toList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Float} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Float} from the specified array of type {@code T}
	 */
	public static <T> ExtendedList<Float> toExtendedList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Float} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Float} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> List<Float> collectionToList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Float} from the specified {@link Collection} of
	 * type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Float} from the specified {@link Collection} of
	 *         type {@code T}
	 */
	public static <T> ExtendedList<Float> collectionToExtendedList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Float} from the specified array of {@code float} values.
	 * <p>
	 * @param array an array of {@code float} values
	 * <p>
	 * @return a {@link Set} of {@link Float} from the specified array of {@code float} values
	 */
	public static Set<Float> toSet(final float... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Float} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Float} from the specified array of type {@code T}
	 */
	public static <T> Set<Float> toSet(final T... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Float} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Float} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> Set<Float> collectionToSet(final Collection<T> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code float} value between {@code 0.0} and
	 * {@code 1.0}.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code float} value between {@code 0.0} and
	 *         {@code 1.0}
	 */
	public static float random() {
		return RANDOM.nextFloat();
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code float} value between {@code lowerBound}
	 * (inclusive) and {@code upperBound} (exclusive).
	 * <p>
	 * @param lowerBound the lower bound of the {@code float} value to create
	 * @param upperBound the upper bound of the {@code float} value to create
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code float} value between {@code lowerBound}
	 *         (inclusive) and {@code upperBound} (exclusive)
	 */
	public static float random(final float lowerBound, final float upperBound) {
		return lowerBound + RANDOM.nextFloat() * (upperBound - lowerBound);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of {@code float} values of the specified length containing the sequence of
	 * numbers starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return an array of {@code float} values of the specified length containing the sequence of
	 *         numbers starting with zero and spaced by one
	 */
	public static float[] createSequence(final int length) {
		return createSequence(length, 0, 1);
	}

	/**
	 * Creates an array of {@code float} values of the specified length containing the sequence of
	 * numbers starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return an array of {@code float} values of the specified length containing the sequence of
	 *         numbers starting with {@code from} and spaced by one
	 */
	public static float[] createSequence(final int length, final float from) {
		return createSequence(length, from, 1);
	}

	/**
	 * Creates an array of {@code float} values of the specified length containing the sequence of
	 * numbers starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return an array of {@code float} values of the specified length containing the sequence of
	 *         numbers starting with {@code from} and spaced by {@code step}
	 */
	public static float[] createSequence(final int length, final float from, final float step) {
		final float[] array = new float[length];
		float value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of random {@code float} values of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return an array of random {@code float} values of the specified length
	 */
	public static float[] createRandomSequence(final int length) {
		final float[] array = new float[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates an array of {@code float} values of the specified length containing pseudorandom,
	 * uniformly distributed {@code float} values between {@code lowerBound} (inclusive) and
	 * {@code upperBound} (exclusive).
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create
	 * @param upperBound the upper bound of the random sequence to create
	 * <p>
	 * @return an array of {@code float} values of the specified length containing pseudorandom,
	 *         uniformly distributed {@code float} values between {@code lowerBound} (inclusive) and
	 *         {@code upperBound} (exclusive)
	 */
	public static float[] createRandomSequence(final int length, final float lowerBound,
			final float upperBound) {
		final float[] array = new float[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} percentage representation of the specified value.
	 * <p>
	 * @param value a {@code float} value
	 * <p>
	 * @return a {@link String} percentage representation of the specified value
	 */
	public static String toPercentage(final float value) {
		return value * 100. + "%";
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final float[] array, final float value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final float[][] array2D, final float value) {
		for (final float[] element : array2D) {
			fill(element, value);
		}
	}

	public static void fill(final float[][][] array3D, final float value) {
		for (final float[][] element : array3D) {
			fill(element, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static float[] take(final float[] array) {
		return take(array, 0, array.length);
	}

	public static float[] take(final float[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length);
		final float[] result = new float[maxLength];
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
	}

	public static float[] take(final float[][] array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static float[] take(final float[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static float[] take(final float[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length);
		final float[] result = new float[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, result, i * columnCount,
					columnCount);
		}
		return result;
	}

	public static float[] take(final float[][][] array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static float[] take(final float[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static float[] take(final float[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static float[] take(final float[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length);
		final int length = columnCount * depthCount;
		final float[] result = new float[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					result, i * length, length);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified 2D array of {@code float} values.
	 * <p>
	 * @param array2D a 2D array of {@code float} values
	 * <p>
	 * @return the transpose of the specified 2D array of {@code float} values
	 */
	public static float[][] transpose(final float[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final float[][] transpose = new float[m][n];
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
	 * @param a a {@code float} value
	 * @param b another {@code float} value to compare with for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final float a, final float b) {
		if (a < b) {
			return -1; // neither value is NaN
		}
		if (a > b) {
			return 1; // neither value is NaN
		}
		// @note cannot use floatToRawIntBits because of NaNs
		final int aBits = Float.floatToIntBits(a);
		final int bBits = Float.floatToIntBits(b);
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
	 * @param a a {@code float} value
	 * @param b another {@code float} value to compare with {@code a} for equality
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final float a, final float b) {
		return equals(a, b, Maths.DEFAULT_FLOAT_TOLERANCE);
	}

	/**
	 * Returns {@code true} if {@code a} is equal to {@code b} within the specified tolerance level,
	 * {@code false} otherwise.
	 * <p>
	 * @param a         a {@code float} value
	 * @param b         another {@code float} value to compare with {@code a} for equality
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b} within the specified tolerance level,
	 *         {@code false} otherwise
	 */
	public static boolean equals(final float a, final float b, final float tolerance) {
		return Maths.delta(a, b) <= tolerance;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of {@code float} values.
	 * <p>
	 * @param array an array of {@code float} values
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code float} values
	 */
	public static String toString(final float... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code float} values joined
	 * by {@code delimiter} (and wrapped with parentheses if {@code useParentheses}).
	 * <p>
	 * @param array          an array of {@code float} values
	 * @param delimiter      a {@link String}
	 * @param useParentheses the option specifying whether to use parentheses
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code float} values joined
	 *         by {@code delimiter} (and wrapped with parentheses if {@code useParentheses})
	 */
	public static String toString(final float[] array, final String delimiter,
			final boolean useParentheses) {
		return Arrays.toString(toArray(array), delimiter, useParentheses);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Float}.
	 * <p>
	 * @param array an array of {@link Float}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Float}
	 */
	public static String toString(final Float... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Float} joined by
	 * {@code delimiter} (and wrapped with parentheses if {@code useParentheses}).
	 * <p>
	 * @param array          an array of {@link Float}
	 * @param delimiter      a {@link String}
	 * @param useParentheses the option specifying whether to use parentheses
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Float} joined by
	 *         {@code delimiter} (and wrapped with parentheses if {@code useParentheses})
	 */
	public static String toString(final Float[] array, final String delimiter,
			final boolean useParentheses) {
		return Arrays.toString(array, delimiter, useParentheses);
	}
}
