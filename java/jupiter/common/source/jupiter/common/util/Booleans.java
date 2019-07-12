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
import jupiter.common.map.parser.BooleanParser;
import jupiter.common.map.parser.IParsers;
import jupiter.common.struct.list.ExtendedList;

public class Booleans {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final boolean[] EMPTY_PRIMITIVE_ARRAY = new boolean[] {};
	public static final Boolean[] EMPTY_ARRAY = new Boolean[] {};

	protected static final BooleanParser PARSER = IParsers.BOOLEAN_PARSER;

	public static volatile Random RANDOM = new Random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Booleans}.
	 */
	protected Booleans() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Boolean} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert
	 * <p>
	 * @return a {@link Boolean} converted from the specified {@link Object}
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

	//////////////////////////////////////////////

	/**
	 * Returns a {@code boolean} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@code boolean} array from the specified {@code T} array
	 */
	public static <T> boolean[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code boolean} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@code boolean} array from the specified {@code T} array
	 */
	public static <T> boolean[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code boolean} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a {@code boolean} array from the specified 2D {@code T} array
	 */
	public static <T> boolean[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code boolean} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a {@code boolean} array from the specified 2D {@code T} array
	 */
	public static <T> boolean[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code boolean} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a {@code boolean} array from the specified 3D {@code T} array
	 */
	public static <T> boolean[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code boolean} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a {@code boolean} array from the specified 3D {@code T} array
	 */
	public static <T> boolean[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code boolean} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code boolean} array from the specified 2D {@code T} array
	 */
	public static <T> boolean[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code boolean} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array2D a 2D {@code T} array
	 * <p>
	 * @return a 2D {@code boolean} array from the specified 2D {@code T} array
	 */
	public static <T> boolean[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 3D {@code boolean} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code boolean} array from the specified 3D {@code T} array
	 */
	public static <T> boolean[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code boolean} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array3D a 3D {@code T} array
	 * <p>
	 * @return a 3D {@code boolean} array from the specified 3D {@code T} array
	 */
	public static <T> boolean[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a {@code boolean} array from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@code boolean} array from the specified {@link Collection} of type {@code T}
	 */
	public static <T> boolean[] collectionToPrimitiveArray(final Collection<T> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array a {@code boolean} array
	 * <p>
	 * @return an array of {@link Boolean} from the specified {@code boolean} array
	 */
	public static Boolean[] toArray(final boolean[] array) {
		final Boolean[] result = new Boolean[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[i];
		}
		return result;
	}

	/**
	 * Returns an array of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array a {@code boolean} array
	 * <p>
	 * @return an array of {@link Boolean} from the specified {@code boolean} array
	 */
	public static Boolean[] asArray(final boolean... array) {
		return toArray(array);
	}

	/**
	 * Returns a 2D array of {@link Boolean} from the specified 2D {@code boolean} array.
	 * <p>
	 * @param array2D a 2D {@code boolean} array
	 * <p>
	 * @return a 2D array of {@link Boolean} from the specified 2D {@code boolean} array
	 */
	public static Boolean[][] toArray2D(final boolean[][] array2D) {
		final Boolean[][] result = new Boolean[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			result[i] = toArray(array2D[i]);
		}
		return result;
	}

	/**
	 * Returns a 2D array of {@link Boolean} from the specified 2D {@code boolean} array.
	 * <p>
	 * @param array2D a 2D {@code boolean} array
	 * <p>
	 * @return a 2D array of {@link Boolean} from the specified 2D {@code boolean} array
	 */
	public static Boolean[][] asArray2D(final boolean[]... array2D) {
		return toArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@link Boolean} from the specified 3D {@code boolean} array.
	 * <p>
	 * @param array3D a 3D {@code boolean} array
	 * <p>
	 * @return a 3D array of {@link Boolean} from the specified 3D {@code boolean} array
	 */
	public static Boolean[][][] toArray3D(final boolean[][][] array3D) {
		final Boolean[][][] result = new Boolean[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			result[i] = toArray2D(array3D[i]);
		}
		return result;
	}

	/**
	 * Returns a 3D array of {@link Boolean} from the specified 3D {@code boolean} array.
	 * <p>
	 * @param array3D a 3D {@code boolean} array
	 * <p>
	 * @return a 3D array of {@link Boolean} from the specified 3D {@code boolean} array
	 */
	public static Boolean[][][] asArray3D(final boolean[][]... array3D) {
		return toArray3D(array3D);
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
	 * Returns a {@link List} of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array a {@code boolean} array
	 * <p>
	 * @return a {@link List} of {@link Boolean} from the specified {@code boolean} array
	 */
	public static List<Boolean> toList(final boolean[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns a {@link List} of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array a {@code boolean} array
	 * <p>
	 * @return a {@link List} of {@link Boolean} from the specified {@code boolean} array
	 */
	public static List<Boolean> asList(final boolean... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array a {@code boolean} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified {@code boolean} array
	 */
	public static ExtendedList<Boolean> toExtendedList(final boolean[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array a {@code boolean} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified {@code boolean} array
	 */
	public static ExtendedList<Boolean> asExtendedList(final boolean... array) {
		return toExtendedList(array);
	}

	/**
	 * Returns a {@link List} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link List} of {@link Boolean} from the specified {@code T} array
	 */
	public static <T> List<Boolean> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link List} of {@link Boolean} from the specified {@code T} array
	 */
	public static <T> List<Boolean> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Boolean> toExtendedList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Boolean> asExtendedList(final T... array) {
		return toExtendedList(array);
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
	 * Returns a {@link Set} of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array a {@code boolean} array
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified {@code boolean} array
	 */
	public static Set<Boolean> toSet(final boolean[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array a {@code boolean} array
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified {@code boolean} array
	 */
	public static Set<Boolean> asSet(final boolean... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified {@code T} array
	 */
	public static <T> Set<Boolean> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified {@code T} array
	 */
	public static <T> Set<Boolean> asSet(final T... array) {
		return toSet(array);
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
	 * Creates a random {@code boolean} array of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return a random {@code boolean} array of the specified length
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

	/**
	 * Returns a {@code boolean} array containing the specified {@code boolean} value and all the
	 * elements of the specified {@code boolean} array.
	 * <p>
	 * @param a a {@code boolean} value (may be {@code null})
	 * @param b a {@code boolean} array (may be {@code null})
	 * <p>
	 * @return a {@code boolean} array containing the specified {@code boolean} value and all the
	 *         elements of the specified {@code boolean} array
	 */
	public static boolean[] merge(final boolean a, final boolean... b) {
		return merge(asPrimitiveArray(a), b);
	}

	/**
	 * Returns a {@code boolean} array containing all the elements of the specified {@code boolean}
	 * arrays.
	 * <p>
	 * @param a a {@code boolean} array (may be {@code null})
	 * @param b a {@code boolean} array (may be {@code null})
	 * <p>
	 * @return a {@code boolean} array containing all the elements of the specified {@code boolean}
	 *         arrays
	 */
	public static boolean[] merge(final boolean[] a, final boolean... b) {
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}
		final boolean[] result = new boolean[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean[] take(final boolean... array) {
		return take(array, 0, array.length);
	}

	public static boolean[] take(final boolean[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length - from);
		final boolean[] result = new boolean[maxLength];
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
	}

	public static boolean[] take(final boolean[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static boolean[] take(final boolean[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static boolean[] take(final boolean[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final boolean[] result = new boolean[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, result, i * columnCount,
					columnCount);
		}
		return result;
	}

	public static boolean[] take(final boolean[][]... array3D) {
		return take(array3D, 0, array3D.length, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static boolean[] take(final boolean[][][] array3D, final int fromRow,
			final int rowCount) {
		return take(array3D, fromRow, rowCount, 0, array3D[0].length, 0, array3D[0][0].length);
	}

	public static boolean[] take(final boolean[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0, array3D[0][0].length);
	}

	public static boolean[] take(final boolean[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length - fromRow);
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
	 * Returns the transpose of the specified {@code boolean} array.
	 * <p>
	 * @param rowCount the number of rows of the array
	 * @param array    a {@code boolean} array
	 * <p>
	 * @return the transpose of the specified {@code boolean} array
	 */
	public static boolean[] transpose(final int rowCount, final boolean[] array) {
		final int n = rowCount;
		final int m = array.length / rowCount;
		final boolean[] transpose = new boolean[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i * n + j] = array[j * m + i];
			}
		}
		return transpose;
	}

	/**
	 * Returns the transpose of the specified 2D {@code boolean} array.
	 * <p>
	 * @param array2D a 2D {@code boolean} array
	 * <p>
	 * @return the transpose of the specified 2D {@code boolean} array
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

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code boolean} value.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code boolean} value,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitive(final Class<?> c) {
		return boolean.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code boolean} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code boolean} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Class<?> c) {
		return boolean[].class.isAssignableFrom(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code array} contains {@code token}.
	 * <p>
	 * @param array a {@code boolean} array
	 * @param token the {@code boolean} value to test for presence
	 * <p>
	 * @return {@code true} if {@code array} contains {@code token}, {@code false} otherwise
	 */
	public static boolean contains(final boolean[] array, final boolean token) {
		if (array == null) {
			return false;
		}
		for (final boolean element : array) {
			if (element == token) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether {@code array} contains any {@code tokens}.
	 * <p>
	 * @param array  a {@code boolean} array
	 * @param tokens the {@code boolean} array to test for presence
	 * <p>
	 * @return {@code true} if {@code array} contains any {@code tokens}, {@code false} otherwise
	 */
	public static boolean containsAny(final boolean[] array, final boolean[] tokens) {
		if (array == null) {
			return false;
		}
		for (final boolean token : tokens) {
			if (contains(array, token)) {
				return true;
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified {@code boolean} array, or {@code null} if {@code array} is
	 * {@code null}.
	 * <p>
	 * @param array a {@code boolean} array
	 * <p>
	 * @return a clone of the specified {@code boolean} array, or {@code null} if {@code array} is
	 *         {@code null}
	 */
	public static boolean[] clone(final boolean... array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@code boolean} array.
	 * <p>
	 * @param array a {@code boolean} array
	 * <p>
	 * @return a representative {@link String} of the specified {@code boolean} array
	 */
	public static String toString(final boolean... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code boolean} array joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     a {@code boolean} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code boolean} array joined by
	 *         {@code delimiter}
	 */
	public static String toString(final boolean[] array, final String delimiter) {
		return Arrays.toString(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code boolean} array joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code boolean} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code boolean} array joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final boolean[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(toArray(array), delimiter, wrapper);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array of {@link Boolean}.
	 * <p>
	 * @param array an array of {@link Boolean}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Boolean}
	 */
	public static String toString(final Boolean... array) {
		return Arrays.toString(array);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Boolean} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Boolean}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Boolean} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Boolean[] array, final String delimiter) {
		return Arrays.toString(array, delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Boolean} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Boolean}
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Boolean} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Boolean[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toString(array, delimiter, wrapper);
	}
}
