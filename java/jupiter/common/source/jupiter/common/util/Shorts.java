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
package jupiter.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.ShortParser;
import jupiter.common.struct.list.ExtendedList;

public class Shorts {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final short[] EMPTY_PRIMITIVE_ARRAY = new short[] {};
	public static final Short[] EMPTY_ARRAY = new Short[] {};

	protected static final ShortParser PARSER = new ShortParser();

	public static volatile Random RANDOM = new Random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Shorts() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code short} value converted from the specified value.
	 * <p>
	 * @param value an {@code int} value
	 * <p>
	 * @return a {@code short} value converted from the specified value
	 */
	public static short convert(final int value) {
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
			throw new ArithmeticException("Short under/overflow");
		}
		return (short) value;
	}

	/**
	 * Returns a {@code short} value converted from the specified value.
	 * <p>
	 * @param value a {@code long} value
	 * <p>
	 * @return a {@code short} value converted from the specified value
	 */
	public static short convert(final long value) {
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
			throw new ArithmeticException("Short under/overflow");
		}
		return (short) value;
	}

	/**
	 * Returns a {@code short} value converted from the specified value.
	 * <p>
	 * @param value a {@code float} value
	 * <p>
	 * @return a {@code short} value converted from the specified value
	 */
	public static short convert(final float value) {
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
			throw new ArithmeticException("Short under/overflow");
		}
		return (short) value;
	}

	/**
	 * Returns a {@code short} value converted from the specified value.
	 * <p>
	 * @param value a {@code double} value
	 * <p>
	 * @return a {@code short} value converted from the specified value
	 */
	public static short convert(final double value) {
		if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
			throw new ArithmeticException("Short under/overflow");
		}
		return (short) value;
	}

	/**
	 * Returns a {@link Short} from the specified {@link Object}.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return a {@link Short} from the specified {@link Object}
	 */
	public static Short convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code short} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object a {@code T} object
	 * <p>
	 * @return a {@code short} value from the specified {@code T} object
	 */
	public static <T> short toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an array of {@code short} values from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an array of {@code short} values from the specified array of type {@code T}
	 */
	public static <T> short[] toPrimitiveArray(final T... array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns an array of {@code short} values from the specified 2D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array
	 * @param array2D a 2D array of type {@code T}
	 * <p>
	 * @return an array of {@code short} values from the specified 2D array of type {@code T}
	 */
	public static <T> short[] toPrimitiveArray(final T[]... array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns an array of {@code short} values from the specified 3D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array
	 * @param array3D a 3D array of type {@code T}
	 * <p>
	 * @return an array of {@code short} values from the specified 3D array of type {@code T}
	 */
	public static <T> short[] toPrimitiveArray(final T[][]... array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D array of {@code short} values from the specified 2D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array
	 * @param array2D a 2D array of type {@code T}
	 * <p>
	 * @return a 2D array of {@code short} values from the specified 2D array of type {@code T}
	 */
	public static <T> short[][] toPrimitiveArray2D(final T[]... array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@code short} values from the specified 3D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array
	 * @param array3D a 3D array of type {@code T}
	 * <p>
	 * @return a 3D array of {@code short} values from the specified 3D array of type {@code T}
	 */
	public static <T> short[][][] toPrimitiveArray3D(final T[][]... array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns an array of {@code short} values from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@code short} values from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> short[] collectionToPrimitiveArray(final Collection<T> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Short} from the specified array of {@code short} values.
	 * <p>
	 * @param array an array of {@code short} values
	 * <p>
	 * @return an array of {@link Short} from the specified array of {@code short} values
	 */
	public static Short[] toArray(final short... array) {
		final Short[] result = new Short[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Returns a 2D array of {@link Short} from the specified 2D array of {@code short} values.
	 * <p>
	 * @param array2D a 2D array of {@code short} values
	 * <p>
	 * @return a 2D array of {@link Short} from the specified 2D array of {@code short} values
	 */
	public static Short[][] toArray2D(final short[]... array2D) {
		final Short[][] result = new Short[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = toArray(array2D[i]);
		}
		return result;
	}

	/**
	 * Returns a 3D array of {@link Short} from the specified 3D array of {@code short} values.
	 * <p>
	 * @param array3D a 3D array of {@code short} values
	 * <p>
	 * @return a 3D array of {@link Short} from the specified 3D array of {@code short} values
	 */
	public static Short[][][] toArray3D(final short[][]... array3D) {
		final Short[][][] result = new Short[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = toArray2D(array3D[i]);
		}
		return result;
	}

	/**
	 * Returns an array of {@link Short} from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@link Short} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Short[] collectionToArray(final Collection<T> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link List} of {@link Short} from the specified array of {@code short} values.
	 * <p>
	 * @param array an array of {@code short} values
	 * <p>
	 * @return a {@link List} of {@link Short} from the specified array of {@code short} values
	 */
	public static List<Short> toList(final short... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Short} from the specified array of {@code short}
	 * values.
	 * <p>
	 * @param array an array of {@code short} values
	 * <p>
	 * @return an {@link ExtendedList} of {@link Short} from the specified array of {@code short}
	 *         values
	 */
	public static ExtendedList<Short> toExtendedList(final short... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Short} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Short} from the specified array of type {@code T}
	 */
	public static <T> List<Short> toList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Short} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Short} from the specified array of type {@code T}
	 */
	public static <T> ExtendedList<Short> toExtendedList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Short} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Short} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> List<Short> collectionToList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Short} from the specified {@link Collection} of
	 * type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Short} from the specified {@link Collection} of
	 *         type {@code T}
	 */
	public static <T> ExtendedList<Short> collectionToExtendedList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Short} from the specified array of {@code short} values.
	 * <p>
	 * @param array an array of {@code short} values
	 * <p>
	 * @return a {@link Set} of {@link Short} from the specified array of {@code short} values
	 */
	public static Set<Short> toSet(final short... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Short} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Short} from the specified array of type {@code T}
	 */
	public static <T> Set<Short> toSet(final T... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Short} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Short} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> Set<Short> collectionToSet(final Collection<T> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code short} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code short} value
	 */
	public static short random() {
		return random(Short.MIN_VALUE, Short.MAX_VALUE);
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code short} value between {@code lowerBound}
	 * (inclusive) and {@code upperBound} (exclusive).
	 * <p>
	 * @param lowerBound the lower bound of the {@code short} value to create
	 * @param upperBound the upper bound of the {@code short} value to create
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code short} value between {@code lowerBound}
	 *         (inclusive) and {@code upperBound} (exclusive)
	 */
	public static short random(final short lowerBound, final short upperBound) {
		return convert(lowerBound + RANDOM.nextDouble() * (upperBound - lowerBound));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of {@code short} values of the specified length containing the sequence of
	 * numbers starting with zero and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return an array of {@code short} values of the specified length containing the sequence of
	 *         numbers starting with zero and spaced by one
	 */
	public static short[] createSequence(final int length) {
		return createSequence(length, (short) 0, (short) 1);
	}

	/**
	 * Creates an array of {@code short} values of the specified length containing the sequence of
	 * numbers starting with {@code from} and spaced by one.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return an array of {@code short} values of the specified length containing the sequence of
	 *         numbers starting with {@code from} and spaced by one
	 */
	public static short[] createSequence(final int length, final short from) {
		return createSequence(length, from, (short) 1);
	}

	/**
	 * Creates an array of {@code short} values of the specified length containing the sequence of
	 * numbers starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return an array of {@code short} values of the specified length containing the sequence of
	 *         numbers starting with {@code from} and spaced by {@code step}
	 */
	public static short[] createSequence(final int length, final short from, final short step) {
		final short[] array = new short[length];
		short value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of random {@code short} values of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return an array of random {@code short} values of the specified length
	 */
	public static short[] createRandomSequence(final int length) {
		final short[] array = new short[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates an array of {@code short} values of the specified length containing pseudorandom,
	 * uniformly distributed {@code short} values between {@code lowerBound} (inclusive) and
	 * {@code upperBound} (exclusive).
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the lower bound of the random sequence to create
	 * @param upperBound the upper bound of the random sequence to create
	 * <p>
	 * @return an array of {@code short} values of the specified length containing pseudorandom,
	 *         uniformly distributed {@code short} values between {@code lowerBound} (inclusive) and
	 *         {@code upperBound} (exclusive)
	 */
	public static short[] createRandomSequence(final int length, final short lowerBound,
			final short upperBound) {
		final short[] array = new short[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(lowerBound, upperBound);
		}
		return array;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final short[] array, final short value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final short[][] array2D, final short value) {
		for (final short[] array : array2D) {
			fill(array, value);
		}
	}

	public static void fill(final short[][][] array3D, final short value) {
		for (final short[][] array2D : array3D) {
			fill(array2D, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code short} value.
	 * <p>
	 * @param value a {@code short} value
	 * <p>
	 * @return the middle of the specified {@code short} value
	 */
	public static short middle(final short value) {
		return middle((short) 0, value);
	}

	/**
	 * Returns the middle of the specified lower and upper bounds rounded to the lower {@code short}
	 * value.
	 * <p>
	 * @param lowerBound a {@code short} value
	 * @param upperBound another {@code short} value
	 * <p>
	 * @return the middle of the specified lower and upper bounds rounded to the lower {@code short}
	 *         value
	 */
	public static short middle(final short lowerBound, final short upperBound) {
		return convert(lowerBound + (upperBound - lowerBound) / 2);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static short[] take(final short... array) {
		return take(array, 0, array.length);
	}

	public static short[] take(final short[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length);
		final short[] result = new short[maxLength];
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
	}

	public static short[] take(final short[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static short[] take(final short[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static short[] take(final short[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length);
		final short[] result = new short[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, result, i * columnCount,
					columnCount);
		}
		return result;
	}

	public static short[] take(final short[][]... array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static short[] take(final short[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static short[] take(final short[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static short[] take(final short[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length);
		final int length = columnCount * depthCount;
		final short[] result = new short[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					result, i * length, length);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified array of {@code short} values.
	 * <p>
	 * @param rowCount the number of rows of the array
	 * @param array    an array of {@code short} values
	 * <p>
	 * @return the transpose of the specified array of {@code short} values
	 */
	public static short[] transpose(final int rowCount, final short[] array) {
		final int n = rowCount;
		final int m = array.length / rowCount;
		final short[] transpose = new short[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i * n + j] = array[j * m + i];
			}
		}
		return transpose;
	}

	/**
	 * Returns the transpose of the specified 2D array of {@code short} values.
	 * <p>
	 * @param array2D a 2D array of {@code short} values
	 * <p>
	 * @return the transpose of the specified 2D array of {@code short} values
	 */
	public static short[][] transpose(final short[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final short[][] transpose = new short[m][n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i][j] = array2D[j][i];
			}
		}
		return transpose;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code short} value or a
	 * {@link Short}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code short} value or
	 *         a {@link Short}, {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return short.class.isAssignableFrom(c) || Short.class.isAssignableFrom(c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified values for order. Returns a negative integer, zero or a positive
	 * integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a a {@code short} value
	 * @param b another {@code short} value to compare with for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final short a, final short b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a hash code value for the specified value.
	 * <p>
	 * @param value a {@code short} value
	 * <p>
	 * @return a hash code value for the specified value
	 */
	public static int hashCode(final short value) {
		return Objects.hashCode((int) value, (int) (value >>> Bits.HALF_LONG_BITS_COUNT));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of {@code short} values.
	 * <p>
	 * @param array an array of {@code short} values
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code short} values
	 */
	public static String toString(final short... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code short} values joined
	 * by {@code delimiter}.
	 * <p>
	 * @param array     an array of {@code short} values
	 * @param delimiter a {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code short} values joined
	 *         by {@code delimiter}
	 */
	public static String toString(final short[] array, final String delimiter) {
		return Arrays.toString(toArray(array), delimiter);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code short} values joined
	 * by {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@code short} values
	 * @param delimiter a {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code short} values joined
	 *         by {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final short[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(toArray(array), delimiter, wrapper);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of {@link Short}.
	 * <p>
	 * @param array an array of {@link Short}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Short}
	 */
	public static String toString(final Short... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Short} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Short}
	 * @param delimiter a {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Short} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Short[] array, final String delimiter) {
		return Arrays.toString(array, delimiter);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Short} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Short}
	 * @param delimiter a {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Short} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Short[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(array, delimiter, wrapper);
	}
}
