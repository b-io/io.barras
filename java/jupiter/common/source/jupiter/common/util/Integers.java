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

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.IntegerParser;
import jupiter.common.struct.list.ExtendedList;

public class Integers {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final int[] EMPTY_PRIMITIVE_ARRAY = new int[] {};
	public static final Integer[] EMPTY_ARRAY = new Integer[] {};

	protected static final IntegerParser PARSER = new IntegerParser();

	public static volatile Random RANDOM = new Random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Integers() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code int} value converted from the specified value.
	 * <p>
	 * @param value a {@code long} value
	 * <p>
	 * @return an {@code int} value converted from the specified value
	 */
	public static int convert(final long value) {
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			throw new ArithmeticException("Integer under/overflow");
		}
		return (int) value;
	}

	/**
	 * Returns an {@code int} value converted from the specified value.
	 * <p>
	 * @param value a {@code float} value
	 * <p>
	 * @return an {@code int} value converted from the specified value
	 */
	public static int convert(final float value) {
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			throw new ArithmeticException("Integer under/overflow");
		}
		return (int) value;
	}

	/**
	 * Returns an {@code int} value converted from the specified value.
	 * <p>
	 * @param value a {@code double} value
	 * <p>
	 * @return an {@code int} value converted from the specified value
	 */
	public static int convert(final double value) {
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			throw new ArithmeticException("Integer under/overflow");
		}
		return (int) value;
	}

	/**
	 * Returns an {@link Integer} from the specified {@link Object}.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return an {@link Integer} from the specified {@link Object}
	 */
	public static Integer convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code int} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object a {@code T} object
	 * <p>
	 * @return an {@code int} value from the specified {@code T} object
	 */
	public static <T> int toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	/**
	 * Returns an array of {@code int} values from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an array of {@code int} values from the specified array of type {@code T}
	 */
	public static <T> int[] toPrimitiveArray(final T... array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a 2D array of {@code int} values from the specified 2D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array2D a 2D array of type {@code T}
	 * <p>
	 * @return a 2D array of {@code int} values from the specified 2D array of type {@code T}
	 */
	public static <T> int[][] toPrimitiveArray2D(final T[]... array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@code int} values from the specified 3D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array3D a 3D array of type {@code T}
	 * <p>
	 * @return a 3D array of {@code int} values from the specified 3D array of type {@code T}
	 */
	public static <T> int[][][] toPrimitiveArray3D(final T[][]... array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns an array of {@code int} values from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@code int} values from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> int[] collectionToPrimitiveArray(final Collection<T> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Integer} from the specified array of {@code int} values.
	 * <p>
	 * @param array an array of {@code int} values
	 * <p>
	 * @return an array of {@link Integer} from the specified array of {@code int} values
	 */
	public static Integer[] toArray(final int... array) {
		final Integer[] result = new Integer[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Returns a 2D array of {@link Integer} from the specified 2D array of {@code int} values.
	 * <p>
	 * @param array2D a 2D array of {@code int} values
	 * <p>
	 * @return a 2D array of {@link Integer} from the specified 2D array of {@code int} values
	 */
	public static Integer[][] toArray2D(final int[]... array2D) {
		final Integer[][] result = new Integer[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = toArray(array2D[i]);
		}
		return result;
	}

	/**
	 * Returns a 3D array of {@link Integer} from the specified 3D array of {@code int} values.
	 * <p>
	 * @param array3D a 3D array of {@code int} values
	 * <p>
	 * @return a 3D array of {@link Integer} from the specified 3D array of {@code int} values
	 */
	public static Integer[][][] toArray3D(final int[][]... array3D) {
		final Integer[][][] result = new Integer[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = toArray2D(array3D[i]);
		}
		return result;
	}

	/**
	 * Returns an array of {@link Integer} from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@link Integer} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Integer[] collectionToArray(final Collection<T> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link List} of {@link Integer} from the specified array of {@code int} values.
	 * <p>
	 * @param array an array of {@code int} values
	 * <p>
	 * @return a {@link List} of {@link Integer} from the specified array of {@code int} values
	 */
	public static List<Integer> toList(final int... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified array of {@code int}
	 * values.
	 * <p>
	 * @param array an array of {@code int} values
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified array of {@code int}
	 *         values
	 */
	public static ExtendedList<Integer> toExtendedList(final int... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Integer} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Integer} from the specified array of type {@code T}
	 */
	public static <T> List<Integer> toList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified array of type
	 * {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified array of type {@code T}
	 */
	public static <T> ExtendedList<Integer> toExtendedList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Integer} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Integer} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> List<Integer> collectionToList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Integer} from the specified {@link Collection} of
	 * type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Integer} from the specified {@link Collection} of
	 *         type {@code T}
	 */
	public static <T> ExtendedList<Integer> collectionToExtendedList(
			final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Integer} from the specified array of {@code int} values.
	 * <p>
	 * @param array an array of {@code int} values
	 * <p>
	 * @return a {@link Set} of {@link Integer} from the specified array of {@code int} values
	 */
	public static Set<Integer> toSet(final int... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Integer} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Integer} from the specified array of type {@code T}
	 */
	public static <T> Set<Integer> toSet(final T... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Integer} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Integer} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> Set<Integer> collectionToSet(final Collection<T> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code int} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code int} value
	 */
	public static int random() {
		return RANDOM.nextInt();
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code int} value between {@code lowerBound}
	 * (inclusive) and {@code upperBound} (exclusive).
	 * <p>
	 * @param lowerBound the lower bound of the {@code int} value to create
	 * @param upperBound the upper bound of the {@code int} value to create
	 * <p>
	 * @return the next pseudorandom, uniformly distributed {@code int} value between
	 *         {@code lowerBound} (inclusive) and {@code upperBound} (exclusive)
	 */
	public static int random(final int lowerBound, final int upperBound) {
		return lowerBound + RANDOM.nextInt(upperBound - lowerBound);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of {@code int} values of the specified length containing the sequence of
	 * numbers starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return an array of {@code int} values of the specified length containing the sequence of
	 *         numbers starting with zero and spaced by one
	 */
	public static int[] createSequence(final int length) {
		return createSequence(length, 0, 1);
	}

	/**
	 * Creates an array of {@code int} values of the specified length containing the sequence of
	 * numbers starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return an array of {@code int} values of the specified length containing the sequence of
	 *         numbers starting with {@code from} and spaced by one
	 */
	public static int[] createSequence(final int length, final int from) {
		return createSequence(length, from, 1);
	}

	/**
	 * Creates an array of {@code int} values of the specified length containing the sequence of
	 * numbers starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return an array of {@code int} values of the specified length containing the sequence of
	 *         numbers starting with {@code from} and spaced by {@code step}
	 */
	public static int[] createSequence(final int length, final int from, final int step) {
		final int[] array = new int[length];
		int value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of random {@code int} values of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return an array of random {@code int} values of the specified length
	 */
	public static int[] createRandomSequence(final int length) {
		final int[] array = new int[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates an array of {@code int} values of the specified length containing pseudorandom,
	 * uniformly distributed {@code int} values between {@code lowerBound} (inclusive) and
	 * {@code upperBound} (exclusive).
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create
	 * @param upperBound the upper bound of the random sequence to create
	 * <p>
	 * @return an array of {@code int} values of the specified length containing pseudorandom,
	 *         uniformly distributed {@code int} values between {@code lowerBound} (inclusive) and
	 *         {@code upperBound} (exclusive)
	 */
	public static int[] createRandomSequence(final int length, final int lowerBound,
			final int upperBound) {
		final int[] array = new int[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final int[] array, final int value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final int[][] array2D, final int value) {
		for (final int[] array : array2D) {
			fill(array, value);
		}
	}

	public static void fill(final int[][][] array3D, final int value) {
		for (final int[][] array2D : array3D) {
			fill(array2D, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code int} value.
	 * <p>
	 * @param value an {@code int} value
	 * <p>
	 * @return the middle of the specified {@code int} value
	 */
	public static int middle(final int value) {
		return middle(0, value);
	}

	/**
	 * Returns the middle of the specified lower and upper bounds rounded to the lower {@code int}
	 * value.
	 * <p>
	 * @param lowerBound an {@code int} value
	 * @param upperBound another {@code int} value
	 * <p>
	 * @return the middle of the specified lower and upper bounds rounded to the lower {@code int}
	 *         value
	 */
	public static int middle(final int lowerBound, final int upperBound) {
		return lowerBound + Integers.convert((upperBound - lowerBound) / 2.);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int[] take(final int[] array) {
		return take(array, 0, array.length);
	}

	public static int[] take(final int[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length);
		final int[] result = new int[maxLength];
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
	}

	public static int[] take(final int[][] array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static int[] take(final int[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static int[] take(final int[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length);
		final int[] result = new int[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, result, i * columnCount,
					columnCount);
		}
		return result;
	}

	public static int[] take(final int[][][] array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static int[] take(final int[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static int[] take(final int[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static int[] take(final int[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length);
		final int length = columnCount * depthCount;
		final int[] result = new int[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					result, i * length, length);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified 2D array of {@code int} values.
	 * <p>
	 * @param array2D a 2D array of {@code int} values
	 * <p>
	 * @return the transpose of the specified 2D array of {@code int} values
	 */
	public static int[][] transpose(final int[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final int[][] transpose = new int[m][n];
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
	 * @param a an {@code int} value
	 * @param b another {@code int} value to compare with for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final int a, final int b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of {@code int} values.
	 * <p>
	 * @param array an array of {@code int} values
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code int} values
	 */
	public static String toString(final int... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code int} values joined
	 * by {@code delimiter}.
	 * <p>
	 * @param array     an array of {@code int} values
	 * @param delimiter a {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code int} values joined
	 *         by {@code delimiter}
	 */
	public static String toString(final int[] array, final String delimiter) {
		return Arrays.toString(toArray(array), delimiter);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code int} values joined
	 * by {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@code int} values
	 * @param delimiter a {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code int} values joined
	 *         by {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final int[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(toArray(array), delimiter, wrapper);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of {@link Integer}.
	 * <p>
	 * @param array an array of {@link Integer}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Integer}
	 */
	public static String toString(final Integer... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Integer} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Integer}
	 * @param delimiter a {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Integer} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Integer[] array, final String delimiter) {
		return Arrays.toString(array, delimiter);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Integer} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Integer}
	 * @param delimiter a {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Integer} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Integer[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(array, delimiter, wrapper);
	}
}
