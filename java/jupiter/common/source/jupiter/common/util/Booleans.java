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
import jupiter.common.map.parser.BooleanParser;
import jupiter.common.map.parser.Parsers;
import jupiter.common.struct.list.ExtendedList;

public class Booleans {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final boolean[] EMPTY_PRIMITIVE_ARRAY = new boolean[] {};
	public static final Boolean[] EMPTY_ARRAY = new Boolean[] {};

	protected static final BooleanParser PARSER = Parsers.BOOLEAN_PARSER;

	public static volatile Random RANDOM = new Random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Booleans() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Boolean} from the specified {@link Object}.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return a {@link Boolean} from the specified {@link Object}
	 */
	public static Boolean convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code boolean} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object a {@code T} object
	 * <p>
	 * @return a {@code boolean} value from the specified {@code T} object
	 */
	public static <T> boolean toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	/**
	 * Returns an array of {@code boolean} values from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an array of {@code boolean} values from the specified array of type {@code T}
	 */
	public static <T> boolean[] toPrimitiveArray(final T... array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a 2D array of {@code boolean} values from the specified 2D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array2D a 2D array of type {@code T}
	 * <p>
	 * @return a 2D array of {@code boolean} values from the specified 2D array of type {@code T}
	 */
	public static <T> boolean[][] toPrimitiveArray2D(final T[]... array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@code boolean} values from the specified 3D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array to convert
	 * @param array3D a 3D array of type {@code T}
	 * <p>
	 * @return a 3D array of {@code boolean} values from the specified 3D array of type {@code T}
	 */
	public static <T> boolean[][][] toPrimitiveArray3D(final T[][]... array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns an array of {@code boolean} values from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@code boolean} values from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> boolean[] collectionToPrimitiveArray(final Collection<T> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Boolean} from the specified array of {@code boolean} values.
	 * <p>
	 * @param array an array of {@code boolean} values
	 * <p>
	 * @return an array of {@link Boolean} from the specified array of {@code boolean} values
	 */
	public static Boolean[] toArray(final boolean... array) {
		final Boolean[] result = new Boolean[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Returns a 2D array of {@link Boolean} from the specified 2D array of {@code boolean} values.
	 * <p>
	 * @param array2D a 2D array of {@code boolean} values
	 * <p>
	 * @return a 2D array of {@link Boolean} from the specified 2D array of {@code boolean} values
	 */
	public static Boolean[][] toArray2D(final boolean[]... array2D) {
		final Boolean[][] result = new Boolean[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = toArray(array2D[i]);
		}
		return result;
	}

	/**
	 * Returns a 3D array of {@link Boolean} from the specified 3D array of {@code boolean} values.
	 * <p>
	 * @param array3D a 3D array of {@code boolean} values
	 * <p>
	 * @return a 3D array of {@link Boolean} from the specified 3D array of {@code boolean} values
	 */
	public static Boolean[][][] toArray3D(final boolean[][]... array3D) {
		final Boolean[][][] result = new Boolean[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = toArray2D(array3D[i]);
		}
		return result;
	}

	/**
	 * Returns an array of {@link Boolean} from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@link Boolean} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> Boolean[] collectionToArray(final Collection<T> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link List} of {@link Boolean} from the specified array of {@code boolean} values.
	 * <p>
	 * @param array an array of {@code boolean} values
	 * <p>
	 * @return a {@link List} of {@link Boolean} from the specified array of {@code boolean} values
	 */
	public static List<Boolean> toList(final boolean... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified array of
	 * {@code boolean} values.
	 * <p>
	 * @param array an array of {@code boolean} values
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified array of
	 *         {@code boolean} values
	 */
	public static ExtendedList<Boolean> toExtendedList(final boolean... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Boolean} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Boolean} from the specified array of type {@code T}
	 */
	public static <T> List<Boolean> toList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified array of type
	 * {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified array of type {@code T}
	 */
	public static <T> ExtendedList<Boolean> toExtendedList(final T... array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Boolean} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link Boolean} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> List<Boolean> collectionToList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified {@link Collection} of
	 * type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified {@link Collection} of
	 *         type {@code T}
	 */
	public static <T> ExtendedList<Boolean> collectionToExtendedList(
			final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified array of {@code boolean} values.
	 * <p>
	 * @param array an array of {@code boolean} values
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified array of {@code boolean} values
	 */
	public static Set<Boolean> toSet(final boolean... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array to convert
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified array of type {@code T}
	 */
	public static <T> Set<Boolean> toSet(final T... array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> Set<Boolean> collectionToSet(final Collection<T> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code boolean} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code boolean} value
	 */
	public static boolean random() {
		return RANDOM.nextBoolean();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of random {@code boolean} values of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return an array of random {@code boolean} values of the specified length
	 */
	public static boolean[] createRandomSequence(final int length) {
		final boolean[] array = new boolean[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void fill(final boolean[] array, final boolean value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static void fill(final boolean[][] array2D, final boolean value) {
		for (final boolean[] array : array2D) {
			fill(array, value);
		}
	}

	public static void fill(final boolean[][][] array3D, final boolean value) {
		for (final boolean[][] array2D : array3D) {
			fill(array2D, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean[] take(final boolean[] array) {
		return take(array, 0, array.length);
	}

	public static boolean[] take(final boolean[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length);
		final boolean[] result = new boolean[maxLength];
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
	}

	public static boolean[] take(final boolean[][] array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static boolean[] take(final boolean[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static boolean[] take(final boolean[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length);
		final boolean[] result = new boolean[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, result, i * columnCount,
					columnCount);
		}
		return result;
	}

	public static boolean[] take(final boolean[][][] array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static boolean[] take(final boolean[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static boolean[] take(final boolean[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static boolean[] take(final boolean[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length);
		final int length = columnCount * depthCount;
		final boolean[] result = new boolean[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					result, i * length, length);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified 2D array of {@code boolean} values.
	 * <p>
	 * @param array2D a 2D array of {@code boolean} values
	 * <p>
	 * @return the transpose of the specified 2D array of {@code boolean} values
	 */
	public static boolean[][] transpose(final boolean[]... array2D) {
		final int n = array2D.length;
		final int m = array2D[0].length;
		final boolean[][] transpose = new boolean[m][n];
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
	 * Tests whether the specified {@link Class} is assignable to a {@code boolean} value or a
	 * {@link Boolean}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code boolean} value
	 *         or a {@link Boolean}, {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return boolean.class.isAssignableFrom(c) || Boolean.class.isAssignableFrom(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code bools} contains {@code bool}.
	 * <p>
	 * @param bools an array of {@code boolean} values
	 * @param bool  the {@code boolean} value to test for presence
	 * <p>
	 * @return {@code true} if {@code bools} contains {@code bool}, {@code false} otherwise
	 */
	public static boolean contains(final boolean[] bools, final boolean bool) {
		if (bools == null) {
			return false;
		}
		for (final boolean c : bools) {
			if (bool == c) {
				return true;
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of {@code boolean} values.
	 * <p>
	 * @param array an array of {@code boolean} values
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code boolean} values
	 */
	public static String toString(final boolean... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code boolean} values
	 * joined by {@code delimiter}.
	 * <p>
	 * @param array     an array of {@code boolean} values
	 * @param delimiter a {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code boolean} values
	 *         joined by {@code delimiter}
	 */
	public static String toString(final boolean[] array, final String delimiter) {
		return Arrays.toString(toArray(array), delimiter);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@code boolean} values
	 * joined by {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@code boolean} values
	 * @param delimiter a {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@code boolean} values
	 *         joined by {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final boolean[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(toArray(array), delimiter, wrapper);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of {@link Boolean}.
	 * <p>
	 * @param array an array of {@link Boolean}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Boolean}
	 */
	public static String toString(final Boolean... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Boolean} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Boolean}
	 * @param delimiter a {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Boolean} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Boolean[] array, final String delimiter) {
		return Arrays.toString(array, delimiter);
	}

	/**
	 * Returns a {@link String} representation of the specified array of {@link Boolean} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Boolean}
	 * @param delimiter a {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of {@link Boolean} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Boolean[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(array, delimiter, wrapper);
	}
}