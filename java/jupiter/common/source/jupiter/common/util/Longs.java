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

import jupiter.common.map.parser.LongParser;
import jupiter.common.struct.list.ExtendedList;

public class Longs {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final long[] EMPTY_PRIMITIVE_ARRAY = new long[] {};
	public static final Long[] EMPTY_ARRAY = new Long[] {};

	protected static final LongParser PARSER = new LongParser();

	public static volatile Random RANDOM = new Random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Longs() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code long} value converted from the specified value.
	 * <p>
	 * @param value a {@code float} value
	 * <p>
	 * @return a {@code long} value converted from the specified value
	 */
	public static long convert(final float value) {
		if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
			throw new ArithmeticException("Long under/overflow");
		}
		return (long) value;
	}

	/**
	 * Returns a {@code long} value converted from the specified value.
	 * <p>
	 * @param value a {@code double} value
	 * <p>
	 * @return a {@code long} value converted from the specified value
	 */
	public static long convert(final double value) {
		if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
			throw new ArithmeticException("Long under/overflow");
		}
		return (long) value;
	}

	/**
	 * Returns a {@link Long} from the specified {@link Object}.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return a {@link Long} from the specified {@link Object}
	 */
	public static Long convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code long} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object a {@code T} object
	 * <p>
	 * @return a {@code long} value from the specified {@code T} object
	 */
	public static <T> long toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	/**
	 * Returns an array of {@code long} values from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an array of {@code long} values from the specified array of type {@code T}
	 */
	public static <T> long[] toPrimitiveArray(final T... array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a 2D array of {@code long} values from the specified 2D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array2D a 2D array of type {@code T}
	 * <p>
	 * @return a 2D array of {@code long} values from the specified 2D array of type {@code T}
	 */
	public static <T> long[][] toPrimitiveArray2D(final T[]... array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@code long} values from the specified 3D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array3D a 3D array of type {@code T}
	 * <p>
	 * @return a 3D array of {@code long} values from the specified 3D array of type {@code T}
	 */
	public static <T> long[][][] toPrimitiveArray3D(final T[][]... array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns an array of {@code long} values from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@code long} values from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> long[] collectionToPrimitiveArray(final Collection<T> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Long} from the specified array of {@code long} values.
	 * <p>
	 * @param array an array of {@code long} values
	 * <p>
	 * @return an array of {@link Long} from the specified array of {@code long} values
	 */
	public static Long[] toArray(final long... array) {
		final Long[] result = new Long[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Returns a 2D array of {@link Long} from the specified 2D array of {@code long} values.
	 * <p>
	 * @param array2D a 2D array of {@code long} values
	 * <p>
	 * @return a 2D array of {@link Long} from the specified 2D array of {@code long} values
	 */
	public static Long[][] toArray2D(final long[]... array2D) {
		final Long[][] result = new Long[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = toArray(array2D[i]);
		}
		return result;
	}

	/**
	 * Returns a 3D array of {@link Long} from the specified 3D array of {@code long} values.
	 * <p>
	 * @param array3D a 3D array of {@code long} values
	 * <p>
	 * @return a 3D array of {@link Long} from the specified 3D array of {@code long} values
	 */
	public static Long[][][] toArray3D(final long[][]... array3D) {
		final Long[][][] result = new Long[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = toArray2D(array3D[i]);
		}
		return result;
	}

	/**
	 * Returns an array of {@link Long} from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@link Long} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Long[] collectionToArray(final Collection<T> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link List} of {@link Long} from the specified array of {@code long} values.
	 * <p>
	 * @param array an array of {@code long} values
	 * <p>
	 * @return a {@link List} of {@link Long} from the specified array of {@code long} values
	 */
	public static List<Long> toList(final long... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Long} from the specified array of {@code long}
	 * values.
	 * <p>
	 * @param array an array of {@code long} values
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} from the specified array of {@code long}
	 *         values
	 */
	public static ExtendedList<Long> toExtendedList(final long... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Long} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Long} from the specified array of type {@code T}
	 */
	public static <T> List<Long> toList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Long} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} from the specified array of type {@code T}
	 */
	public static <T> ExtendedList<Long> toExtendedList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Long} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Long} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> List<Long> collectionToList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Long} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> ExtendedList<Long> collectionToExtendedList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Long} from the specified array of {@code long} values.
	 * <p>
	 * @param array an array of {@code long} values
	 * <p>
	 * @return a {@link Set} of {@link Long} from the specified array of {@code long} values
	 */
	public static Set<Long> toSet(final long... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Long} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Long} from the specified array of type {@code T}
	 */
	public static <T> Set<Long> toSet(final T... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Long} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Long} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Set<Long> collectionToSet(final Collection<T> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code long} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code long} value
	 */
	public static long random() {
		return random(Long.MIN_VALUE, Long.MAX_VALUE);
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code long} value between {@code lowerBound}
	 * (inclusive) and {@code upperBound} (exclusive).
	 * <p>
	 * @param lowerBound the lower bound of the {@code long} value to create
	 * @param upperBound the upper bound of the {@code long} value to create
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code long} value between {@code lowerBound}
	 *         (inclusive) and {@code upperBound} (exclusive)
	 */
	public static long random(final long lowerBound, final long upperBound) {
		return lowerBound + convert(RANDOM.nextDouble() * (upperBound - lowerBound));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of {@code long} values of the specified length containing the sequence of
	 * numbers starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return an array of {@code long} values of the specified length containing the sequence of
	 *         numbers starting with zero and spaced by one
	 */
	public static long[] createSequence(final int length) {
		return createSequence(length, 0L, 1L);
	}

	/**
	 * Creates an array of {@code long} values of the specified length containing the sequence of
	 * numbers starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return an array of {@code long} values of the specified length containing the sequence of
	 *         numbers starting with {@code from} and spaced by one
	 */
	public static long[] createSequence(final int length, final long from) {
		return createSequence(length, from, 1L);
	}

	/**
	 * Creates an array of {@code long} values of the specified length containing the sequence of
	 * numbers starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return an array of {@code long} values of the specified length containing the sequence of
	 *         numbers starting with {@code from} and spaced by {@code step}
	 */
	public static long[] createSequence(final int length, final long from, final long step) {
		final long[] array = new long[length];
		long value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of random {@code long} values of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return an array of random {@code long} values of the specified length
	 */
	public static long[] createRandomSequence(final int length) {
		final long[] array = new long[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates an array of {@code long} values of the specified length containing pseudorandom,
	 * uniformly distributed {@code long} values between {@code lowerBound} (inclusive) and
	 * {@code upperBound} (exclusive).
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create
	 * @param upperBound the upper bound of the random sequence to create
	 * <p>
	 * @return an array of {@code long} values of the specified length containing pseudorandom,
	 *         uniformly distributed {@code long} values between {@code lowerBound} (inclusive) and
	 *         {@code upperBound} (exclusive)
	 */
	public static long[] createRandomSequence(final int length, final long lowerBound,
			final long upperBound) {
		final long[] array = new long[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final long[] array, final long value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final long[][] array2D, final long value) {
		for (final long[] array : array2D) {
			fill(array, value);
		}
	}

	public static void fill(final long[][][] array3D, final long value) {
		for (final long[][] array2D : array3D) {
			fill(array2D, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code long} value.
	 * <p>
	 * @param value a {@code long} value
	 * <p>
	 * @return the middle of the specified {@code long} value
	 */
	public static long middle(final long value) {
		return middle(0L, value);
	}

	/**
	 * Returns the middle of the specified lower and upper bounds rounded to the lower {@code long}
	 * value.
	 * <p>
	 * @param lowerBound a {@code long} value
	 * @param upperBound another {@code long} value
	 * <p>
	 * @return the middle of the specified lower and upper bounds rounded to the lower {@code long}
	 *         value
	 */
	public static long middle(final long lowerBound, final long upperBound) {
		return lowerBound + (upperBound - lowerBound) / 2L;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static long[] take(final long[] array) {
		return take(array, 0, array.length);
	}

	public static long[] take(final long[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length);
		final long[] result = new long[maxLength];
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
	}

	public static long[] take(final long[][] array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static long[] take(final long[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static long[] take(final long[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length);
		final long[] result = new long[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, result, i * columnCount,
					columnCount);
		}
		return result;
	}

	public static long[] take(final long[][][] array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static long[] take(final long[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static long[] take(final long[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static long[] take(final long[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length);
		final int length = columnCount * depthCount;
		final long[] result = new long[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					result, i * length, length);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified 2D array of {@code long} values.
	 * <p>
	 * @param array2D a 2D array of {@code long} values
	 * <p>
	 * @return the transpose of the specified 2D array of {@code long} values
	 */
	public static long[][] transpose(final long[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final long[][] transpose = new long[m][n];
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
	 * @param a a {@code long} value
	 * @param b another {@code long} value to compare with for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final long a, final long b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a hash code value for the specified value.
	 * <p>
	 * @param value a {@code long} value
	 * <p>
	 * @return a hash code value for the specified value
	 */
	public static int hashCode(final long value) {
		return Objects.hashCode((int) value, (int) (value >>> Bits.N_HALF_LONG_BITS));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of {@code long} values.
	 * <p>
	 * @param array an array of {@code long} values
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code long} values
	 */
	public static String toString(final long... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code long} values joined
	 * by {@code delimiter} (and wrapped with parentheses if {@code useParentheses}).
	 * <p>
	 * @param array          an array of {@code long} values
	 * @param delimiter      a {@link String}
	 * @param useParentheses the flag specifying whether to use parentheses
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code long} values joined
	 *         by {@code delimiter} (and wrapped with parentheses if {@code useParentheses})
	 */
	public static String toString(final long[] array, final String delimiter,
			final boolean useParentheses) {
		return Arrays.toString(toArray(array), delimiter, useParentheses);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Long}.
	 * <p>
	 * @param array an array of {@link Long}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Long}
	 */
	public static String toString(final Long... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Long} joined by
	 * {@code delimiter} (and wrapped with parentheses if {@code useParentheses}).
	 * <p>
	 * @param array          an array of {@link Long}
	 * @param delimiter      a {@link String}
	 * @param useParentheses the flag specifying whether to use parentheses
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Long} joined by
	 *         {@code delimiter} (and wrapped with parentheses if {@code useParentheses})
	 */
	public static String toString(final Long[] array, final String delimiter,
			final boolean useParentheses) {
		return Arrays.toString(array, delimiter, useParentheses);
	}
}
