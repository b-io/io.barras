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
package jupiter.common.util;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.IParsers;
import jupiter.common.map.parser.LongParser;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.set.ExtendedHashSet;

public class Longs {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final long[] EMPTY_PRIMITIVE_ARRAY = new long[] {};
	public static final long[][] EMPTY_PRIMITIVE_ARRAY_2D = new long[][] {};
	public static final long[][][] EMPTY_PRIMITIVE_ARRAY_3D = new long[][][] {};

	public static final Long[] EMPTY_ARRAY = new Long[] {};
	public static final Long[][] EMPTY_ARRAY_2D = new Long[][] {};
	public static final Long[][][] EMPTY_ARRAY_3D = new Long[][][] {};

	protected static final LongParser PARSER = IParsers.LONG_PARSER;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<Long> COMPARATOR = new Comparator<Long>() {
		@Override
		public int compare(final Long a, final Long b) {
			return Longs.compare(a, b);
		}
	};

	public static final Comparator<long[]> ARRAY_COMPARATOR = new Comparator<long[]>() {
		@Override
		public int compare(final long[] a, final long[] b) {
			return Longs.compare(a, b);
		}
	};

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile Random RANDOM = new Random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Longs}.
	 */
	protected Longs() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@code long} values for order. Returns a negative integer, {@code 0}
	 * or a positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code long} value to compare for order
	 * @param b the other {@code long} value to compare against for order
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final long a, final long b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}

	//////////////////////////////////////////////

	/**
	 * Compares the specified {@code long} arrays for order. Returns a negative integer, {@code 0}
	 * or a positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@code long} array to compare for order (may be {@code null})
	 * @param b the other {@code long} array to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final long[] a, final long[] b) {
		// Check the arguments
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}

		// Compare the arrays for order
		final int limit = Math.min(a.length, b.length);
		for (int i = 0; i < limit; ++i) {
			final int comparison = compare(a[i], b[i]);
			if (comparison != 0) {
				return comparison;
			}
		}
		return Integers.compare(a.length, b.length);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code long} value converted from the specified {@code float} value.
	 * <p>
	 * @param value the {@code float} value to convert
	 * <p>
	 * @return a {@code long} value converted from the specified {@code float} value
	 */
	public static long convert(final float value) {
		if (value < MIN_VALUE || value > MAX_VALUE) {
			throw new ArithmeticException("Long under/overflow");
		}
		return (long) value;
	}

	/**
	 * Returns a {@code long} value converted from the specified {@code double} value.
	 * <p>
	 * @param value the {@code double} value to convert
	 * <p>
	 * @return a {@code long} value converted from the specified {@code double} value
	 */
	public static long convert(final double value) {
		if (value < MIN_VALUE || value > MAX_VALUE) {
			throw new ArithmeticException("Long under/overflow");
		}
		return (long) value;
	}

	/**
	 * Returns a {@link Long} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert (may be {@code null})
	 * <p>
	 * @return a {@link Long} converted from the specified {@link Object}
	 */
	public static Long convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code long} value converted from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object the {@code T} object to convert
	 * <p>
	 * @return a {@code long} value converted from the specified {@code T} object
	 */
	public static <T> long toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code long} array converted from the specified {@code long} array.
	 * <p>
	 * @param array the {@code long} array to convert
	 * <p>
	 * @return a {@code long} array converted from the specified {@code long} array
	 */
	public static long[] toPrimitiveArray(final long... array) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return EMPTY_PRIMITIVE_ARRAY;
		}

		// Copy the array to an array
		final long[] output = new long[array.length];
		System.arraycopy(array, 0, output, 0, array.length);
		return output;
	}

	/**
	 * Returns a {@code long} array converted from the specified 2D {@code long} array.
	 * <p>
	 * @param array2D the 2D {@code long} array to convert
	 * <p>
	 * @return a {@code long} array converted from the specified 2D {@code long} array
	 */
	public static long[] toPrimitiveArray(final long[]... array2D) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}
		if (array2D.length == 0 || array2D[0].length == 0) {
			return EMPTY_PRIMITIVE_ARRAY;
		}

		// Copy the 2D array to an array
		final int rowCount = array2D.length;
		final int columnCount = array2D[0].length;
		final long[] output = new long[rowCount * columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output, i * columnCount, columnCount);
		}
		return output;
	}

	/**
	 * Returns a {@code long} array converted from the specified 3D {@code long} array.
	 * <p>
	 * @param array3D the 3D {@code long} array to convert
	 * <p>
	 * @return a {@code long} array converted from the specified 3D {@code long} array
	 */
	public static long[] toPrimitiveArray(final long[][]... array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}
		if (array3D.length == 0 || array3D[0].length == 0 || array3D[0][0].length == 0) {
			return EMPTY_PRIMITIVE_ARRAY;
		}

		// Copy the 3D array to an array
		final int rowCount = array3D.length;
		final int columnCount = array3D[0].length;
		final int depthCount = array3D[0][0].length;
		final long[] output = new long[rowCount * columnCount * depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output, (i * columnCount + j) * depthCount,
						depthCount);
			}
		}
		return output;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code long} array converted from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code long} array converted from the specified {@code T} array
	 */
	public static <T> long[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code long} array converted from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code long} array converted from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> long[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code long} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code long} array converted from the specified 2D {@code T} array
	 */
	public static <T> long[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code long} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code long} array converted from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> long[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code long} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code long} array converted from the specified 3D {@code T} array
	 */
	public static <T> long[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code long} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code long} array converted from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> long[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	public static long[][] toPrimitiveArray2D(final long[] array, final int rowCount) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0 || rowCount == 0) {
			return EMPTY_PRIMITIVE_ARRAY_2D;
		}

		// Copy the array to a 2D array
		final int columnCount = array.length / rowCount;
		final long[][] output2D = new long[rowCount][columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array, i * columnCount, output2D[i], 0, columnCount);
		}
		return output2D;
	}

	public static long[][] toPrimitiveArray2D(final long[][] array2D) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}
		if (array2D.length == 0 || array2D[0].length == 0) {
			return EMPTY_PRIMITIVE_ARRAY_2D;
		}

		// Copy the 2D array to a 2D array
		final int rowCount = array2D.length;
		final int columnCount = array2D[0].length;
		final long[][] output2D = new long[rowCount][columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output2D[i], 0, columnCount);
		}
		return output2D;
	}

	public static long[][] toPrimitiveArray2D(final long[][][] array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}
		if (array3D.length == 0 || array3D[0].length == 0 || array3D[0][0].length == 0) {
			return EMPTY_PRIMITIVE_ARRAY_2D;
		}

		// Copy the 3D array to a 2D array
		final int rowCount = array3D.length;
		final int columnCount = array3D[0].length;
		final int depthCount = array3D[0][0].length;
		final long[][] output2D = new long[rowCount][columnCount * depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output2D[i], j * depthCount, depthCount);
			}
		}
		return output2D;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code long} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code long} array converted from the specified 2D {@code T} array
	 */
	public static <T> long[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code long} array converted from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code long} array converted from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> long[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	//////////////////////////////////////////////

	public static long[][][] toPrimitiveArray3D(final long[] array, final int rowCount,
			final int columnCount) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0 || rowCount == 0 || columnCount == 0) {
			return EMPTY_PRIMITIVE_ARRAY_3D;
		}

		// Copy the array to a 3D array
		final int depthCount = array.length / (rowCount * columnCount);
		final long[][][] output3D = new long[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array, (i * columnCount + j) * depthCount, output3D[i][j], 0,
						depthCount);
			}
		}
		return output3D;
	}

	public static long[][][] toPrimitiveArray3D(final long[][] array2D,
			final int columnCount) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}
		if (array2D.length == 0 || array2D[0].length == 0 || columnCount == 0) {
			return EMPTY_PRIMITIVE_ARRAY_3D;
		}

		// Copy the 2D array to a 3D array
		final int rowCount = array2D.length;
		final int depthCount = array2D[0].length / columnCount;
		final long[][][] output3D = new long[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array2D[i], j * depthCount, output3D[i][j], 0, depthCount);
			}
		}
		return output3D;
	}

	public static long[][][] toPrimitiveArray3D(final long[][][] array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}
		if (array3D.length == 0 || array3D[0].length == 0 || array3D[0][0].length == 0) {
			return EMPTY_PRIMITIVE_ARRAY_3D;
		}

		// Copy the 3D array to a 3D array
		final int rowCount = array3D.length;
		final int columnCount = array3D[0].length;
		final int depthCount = array3D[0][0].length;
		final long[][][] output3D = new long[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output3D[i][j], 0, depthCount);
			}
		}
		return output3D;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D {@code long} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code long} array converted from the specified 3D {@code T} array
	 */
	public static <T> long[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code long} array converted from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code long} array converted from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> long[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code long} array converted from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return a {@code long} array converted from the specified {@link Collection}
	 */
	public static long[] collectionToPrimitiveArray(final Collection<?> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Long} converted from the specified {@code long} array.
	 * <p>
	 * @param array the {@code long} array to convert
	 * <p>
	 * @return an array of {@link Long} converted from the specified {@code long} array
	 */
	public static Long[] toArray(final long[] array) {
		final Long[] newArray = new Long[array.length];
		for (int i = 0; i < array.length; ++i) {
			newArray[i] = array[i];
		}
		return newArray;
	}

	/**
	 * Returns an array of {@link Long} converted from the specified {@code long} array.
	 * <p>
	 * @param array the {@code long} array to convert
	 * <p>
	 * @return an array of {@link Long} converted from the specified {@code long} array
	 */
	public static Long[] asArray(final long... array) {
		return toArray(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D array of {@link Long} converted from the specified 2D {@code long} array.
	 * <p>
	 * @param array2D the 2D {@code long} array to convert
	 * <p>
	 * @return a 2D array of {@link Long} converted from the specified 2D {@code long} array
	 */
	public static Long[][] toArray2D(final long[][] array2D) {
		final Long[][] newArray2D = new Long[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			newArray2D[i] = toArray(array2D[i]);
		}
		return newArray2D;
	}

	/**
	 * Returns a 2D array of {@link Long} converted from the specified 2D {@code long} array.
	 * <p>
	 * @param array2D the 2D {@code long} array to convert
	 * <p>
	 * @return a 2D array of {@link Long} converted from the specified 2D {@code long} array
	 */
	public static Long[][] asArray2D(final long[]... array2D) {
		return toArray2D(array2D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D array of {@link Long} converted from the specified 3D {@code long} array.
	 * <p>
	 * @param array3D the 3D {@code long} array to convert
	 * <p>
	 * @return a 3D array of {@link Long} converted from the specified 3D {@code long} array
	 */
	public static Long[][][] toArray3D(final long[][][] array3D) {
		final Long[][][] newArray3D = new Long[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			newArray3D[i] = toArray2D(array3D[i]);
		}
		return newArray3D;
	}

	/**
	 * Returns a 3D array of {@link Long} converted from the specified 3D {@code long} array.
	 * <p>
	 * @param array3D the 3D {@code long} array to convert
	 * <p>
	 * @return a 3D array of {@link Long} converted from the specified 3D {@code long} array
	 */
	public static Long[][][] asArray3D(final long[][]... array3D) {
		return toArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an array of {@link Long} converted from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an array of {@link Long} converted from the specified {@link Collection}
	 */
	public static Long[] collectionToArray(final Collection<?> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Long} converted from the specified {@code long}
	 * array.
	 * <p>
	 * @param array the {@code long} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} converted from the specified {@code long}
	 *         array
	 */
	public static ExtendedList<Long> toList(final long[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Long} converted from the specified {@code long}
	 * array.
	 * <p>
	 * @param array the {@code long} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} converted from the specified {@code long}
	 *         array
	 */
	public static ExtendedList<Long> asList(final long... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Long} converted from the specified
	 * {@code long} array.
	 * <p>
	 * @param array the {@code long} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Long} converted from the specified
	 *         {@code long} array
	 */
	public static ExtendedLinkedList<Long> toLinkedList(final long[] array) {
		return PARSER.callToLinkedList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Long} converted from the specified
	 * {@code long} array.
	 * <p>
	 * @param array the {@code long} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Long} converted from the specified
	 *         {@code long} array
	 */
	public static ExtendedLinkedList<Long> asLinkedList(final long... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Long} converted from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} converted from the specified {@code T} array
	 */
	public static <T> ExtendedList<Long> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Long} converted from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} converted from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedList<Long> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Long} converted from the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Long} converted from the specified {@code T}
	 *         array
	 */
	public static <T> ExtendedLinkedList<Long> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Long} converted from the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Long} converted from the specified {@code T}
	 *         array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedLinkedList<Long> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Long} converted from the specified
	 * {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Long} converted from the specified
	 *         {@link Collection}
	 */
	public static ExtendedList<Long> collectionToList(final Collection<?> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Long} converted from the specified
	 * {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Long} converted from the specified
	 *         {@link Collection}
	 */
	public static ExtendedLinkedList<Long> collectionToLinkedList(
			final Collection<?> collection) {
		return PARSER.callCollectionToLinkedList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Long} converted from the specified {@code long}
	 * array.
	 * <p>
	 * @param array the {@code long} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Long} converted from the specified {@code long}
	 *         array
	 */
	public static ExtendedHashSet<Long> toSet(final long[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Long} converted from the specified {@code long}
	 * array.
	 * <p>
	 * @param array the {@code long} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Long} converted from the specified {@code long}
	 *         array
	 */
	public static ExtendedHashSet<Long> asSet(final long... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Long} converted from the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Long} converted from the specified {@code T}
	 *         array
	 */
	public static <T> ExtendedHashSet<Long> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Long} converted from the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Long} converted from the specified {@code T}
	 *         array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedHashSet<Long> asSet(final T... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedHashSet} of {@link Long} converted from the specified
	 * {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedHashSet} of {@link Long} converted from the specified
	 *         {@link Collection}
	 */
	public static ExtendedHashSet<Long> collectionToSet(final Collection<?> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code long} array of the specified length containing the sequence of numbers
	 * starting with {@code 0L} and spaced by {@code 1L}.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return a {@code long} array of the specified length containing the sequence of numbers
	 *         starting with {@code 0L} and spaced by {@code 1L}
	 */
	public static long[] createSequence(final int length) {
		return createSequence(length, 0L, 1L);
	}

	/**
	 * Creates a {@code long} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code 1L}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return a {@code long} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code 1L}
	 */
	public static long[] createSequence(final int length, final long from) {
		return createSequence(length, from, 1L);
	}

	/**
	 * Creates a {@code long} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return a {@code long} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code step}
	 */
	public static long[] createSequence(final int length, final long from, final long step) {
		final long[] array = new long[length];
		long value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	//////////////////////////////////////////////

	/**
	 * Creates a random {@code long} array of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return a random {@code long} array of the specified length
	 */
	public static long[] createRandomSequence(final int length) {
		final long[] array = new long[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates a {@code long} array of the specified length containing pseudorandom, uniformly
	 * distributed {@code long} values between the specified bounds.
	 * <p>
	 * @param length the length of the random sequence to create
	 * @param from   the {@code long} lower bound of the random sequence to create (inclusive)
	 * @param to     the {@code long} upper bound of the random sequence to create (exclusive)
	 * <p>
	 * @return a {@code long} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code long} values between the specified bounds
	 */
	public static long[] createRandomSequence(final int length, final long from, final long to) {
		final long[] array = new long[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random(from, to);
		}
		return array;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@code long} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code long} value
	 */
	public static long random() {
		return random(MIN_VALUE, MAX_VALUE);
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code long} value between the specified
	 * bounds.
	 * <p>
	 * @param from the {@code long} lower bound of the value to generate (inclusive)
	 * @param to   the {@code long} upper bound of the value to generate (exclusive)
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code long} value between the specified bounds
	 */
	public static long random(final long from, final long to) {
		return convert(from + RANDOM.nextFloat() * (to - from));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code long} array of the specified length with the specified {@code long} element.
	 * <p>
	 * @param element the {@code long} element of the {@code long} array to create
	 * @param length  the length of the {@code long} array to create
	 * <p>
	 * @return a {@code long} array of the specified length with the specified {@code long} element
	 */
	public static long[] repeat(final long element, final int length) {
		return fill(new long[length], element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code long} array containing the specified {@code long} value and all the elements
	 * of the specified {@code long} array.
	 * <p>
	 * @param a a {@code long} value
	 * @param b another {@code long} array (may be {@code null})
	 * <p>
	 * @return a {@code long} array containing the specified {@code long} value and all the elements
	 *         of the specified {@code long} array
	 */
	public static long[] concat(final long a, final long... b) {
		return concat(asPrimitiveArray(a), b);
	}

	/**
	 * Returns a {@code long} array containing all the elements of the specified {@code long}
	 * arrays.
	 * <p>
	 * @param a a {@code long} array (may be {@code null})
	 * @param b another {@code long} array (may be {@code null})
	 * <p>
	 * @return a {@code long} array containing all the elements of the specified {@code long} arrays
	 */
	public static long[] concat(final long[] a, final long... b) {
		// Check the arguments
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}

		// Concatenate the arrays
		final long[] newArray = new long[a.length + b.length];
		System.arraycopy(a, 0, newArray, 0, a.length);
		System.arraycopy(b, 0, newArray, a.length, b.length);
		return newArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of elements in the specified 2D {@code long} array.
	 * <p>
	 * @param array2D the 2D {@code long} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 2D {@code long} array
	 */
	public static int count(final long[][] array2D) {
		int count = 0;
		if (array2D != null) {
			for (final long[] array : array2D) {
				count += array.length;
			}
		}
		return count;
	}

	/**
	 * Returns the number of elements in the specified 3D {@code long} array.
	 * <p>
	 * @param array3D the 3D {@code long} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 3D {@code long} array
	 */
	public static int count(final long[][][] array3D) {
		int count = 0;
		if (array3D != null) {
			for (final long[][] array2D : array3D) {
				count += count(array2D);
			}
		}
		return count;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code long} token in the specified
	 * {@code long} array.
	 * <p>
	 * @param array the {@code long} array to count from (may be {@code null})
	 * @param token the {@code long} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code long} token in the specified
	 *         {@code long} array
	 */
	public static int count(final long[] array, final long token) {
		int occurrenceCount = 0;
		if (array != null) {
			int index = -1;
			while ((index = findFirstIndex(array, token, index + 1)) >= 0) {
				++occurrenceCount;
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code long} token in the specified 2D
	 * {@code long} array.
	 * <p>
	 * @param array2D the 2D {@code long} array to count from (may be {@code null})
	 * @param token   the {@code long} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code long} token in the specified 2D
	 *         {@code long} array
	 */
	public static int count(final long[][] array2D, final long token) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final long[] array : array2D) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code long} token in the specified 3D
	 * {@code long} array.
	 * <p>
	 * @param array3D the 3D {@code long} array to count from (may be {@code null})
	 * @param token   the {@code long} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code long} token in the specified 3D
	 *         {@code long} array
	 */
	public static int count(final long[][][] array3D, final long token) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final long[][] array2D : array3D) {
				occurrenceCount += count(array2D, token);
			}
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code long} tokens in the specified
	 * {@code long} array.
	 * <p>
	 * @param array  the {@code long} array to count from (may be {@code null})
	 * @param tokens the {@code long} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code long} tokens in the specified
	 *         {@code long} array
	 */
	public static int count(final long[] array, final long... tokens) {
		int occurrenceCount = 0;
		if (array != null && tokens != null) {
			for (final long token : tokens) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code long} tokens in the specified 2D
	 * {@code long} array.
	 * <p>
	 * @param array2D the 2D {@code long} array to count from (may be {@code null})
	 * @param tokens  the {@code long} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code long} tokens in the specified 2D
	 *         {@code long} array
	 */
	public static int count(final long[][] array2D, final long... tokens) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final long[] array : array2D) {
				occurrenceCount += count(array, tokens);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code long} tokens in the specified 3D
	 * {@code long} array.
	 * <p>
	 * @param array3D the 3D {@code long} array to count from (may be {@code null})
	 * @param tokens  the {@code long} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code long} tokens in the specified 3D
	 *         {@code long} array
	 */
	public static int count(final long[][][] array3D, final long... tokens) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final long[][] array2D : array3D) {
				occurrenceCount += count(array2D, tokens);
			}
		}
		return occurrenceCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static long[] fill(final long[] array, final long value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
		return array;
	}

	public static long[][] fill(final long[][] array2D, final long value) {
		for (final long[] array : array2D) {
			fill(array, value);
		}
		return array2D;
	}

	public static long[][][] fill(final long[][][] array3D, final long value) {
		for (final long[][] array2D : array3D) {
			fill(array2D, value);
		}
		return array3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code long} array containing all the elements of the specified {@code long} array
	 * at the specified indices.
	 * <p>
	 * @param array   the {@code long} array to filter from
	 * @param indices the indices to filter
	 * <p>
	 * @return a {@code long} array containing all the elements of the specified {@code long} array
	 *         at the specified indices
	 */
	public static long[] filter(final long[] array, final int... indices) {
		final long[] newArray = new long[indices.length];
		for (int i = 0; i < indices.length; ++i) {
			newArray[i] = array[indices[i]];
		}
		return newArray;
	}

	/**
	 * Returns a 2D {@code long} array containing all the elements of the specified {@code long}
	 * array at all the specified indices.
	 * <p>
	 * @param array   the {@code long} array to filter from
	 * @param indices the array of indices to filter
	 * <p>
	 * @return a 2D {@code long} array containing all the elements of the specified {@code long}
	 *         array at all the specified indices
	 */
	public static long[][] filterAll(final long[] array, final int[]... indices) {
		final long[][] newArray2D = new long[indices.length][];
		for (int i = 0; i < indices.length; ++i) {
			newArray2D[i] = filter(array, indices[i]);
		}
		return newArray2D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code long} value rounded down.
	 * <p>
	 * @param value a {@code long} value
	 * <p>
	 * @return the middle of the specified {@code long} value rounded down
	 */
	public static long middle(final long value) {
		return value / 2L;
	}

	/**
	 * Returns the middle of the specified {@code long} lower and upper bounds rounded down.
	 * <p>
	 * @param from a {@code long} value
	 * @param to   another {@code long} value
	 * <p>
	 * @return the middle of the specified {@code long} lower and upper bounds rounded down
	 */
	public static long middle(final long from, final long to) {
		return from + (to - from) / 2L;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code long} value rounded up.
	 * <p>
	 * @param value a {@code long} value
	 * <p>
	 * @return the middle of the specified {@code long} value rounded up
	 */
	public static long middleUp(final long value) {
		return (value + 1L) / 2L;
	}

	/**
	 * Returns the middle of the specified {@code long} lower and upper bounds rounded up.
	 * <p>
	 * @param from a {@code long} value
	 * @param to   another {@code long} value
	 * <p>
	 * @return the middle of the specified {@code long} lower and upper bounds rounded up
	 */
	public static long middleUp(final long from, final long to) {
		return from + (to - from + 1L) / 2L;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the element at the specified index from the specified {@code long} array.
	 * <p>
	 * @param array the {@code long} array to remove from
	 * @param index the index of the element to remove
	 * <p>
	 * @return the specified {@code long} array without the element at the specified index
	 */
	public static long[] remove(final long[] array, final int index) {
		final long[] newArray = new long[array.length - 1];
		System.arraycopy(array, 0, newArray, 0, index);
		System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);
		return newArray;
	}

	/**
	 * Removes all the occurrences of the specified {@code long} value from the specified
	 * {@code long} array.
	 * <p>
	 * @param array the {@code long} array to remove from
	 * @param value the {@code long} value to remove (may be {@code null})
	 * <p>
	 * @return the specified {@code long} array without the specified {@code long} value
	 */
	public static long[] removeAll(final long[] array, final long value) {
		final long[] newArray = new long[array.length - count(array, value)];
		int index = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] != value) {
				newArray[index++] = array[i];
			}
		}
		return newArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void reverse(final long... array) {
		reverse(array, 0, array.length);
	}

	public static void reverse(final long[] array, final int fromIndex, final int toIndex) {
		final int limit = Integers.middle(toIndex - fromIndex);
		for (int i = 0; i < limit; ++i) {
			swap(array, fromIndex + i, toIndex - 1 - i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shuffles the specified {@code long} array.
	 * <p>
	 * @param array the {@code long} array to shuffle
	 */
	public static void shuffle(final long... array) {
		shuffle(array, 0, array.length);
	}

	/**
	 * Shuffles the specified {@code long} array between the specified indices.
	 * <p>
	 * @param array     the {@code long} array to shuffle
	 * @param fromIndex the index to start shuffling from (inclusive)
	 * @param toIndex   the index to finish shuffling at (exclusive)
	 */
	public static void shuffle(final long[] array, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			swap(array, i, Integers.random(fromIndex, toIndex));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final long[] array, final int i, final int j) {
		final long element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static long[] take(final long[] array, final int fromIndex, final int length) {
		if (isNullOrEmpty(array)) {
			return array;
		}
		final int maxLength = Math.min(length, array.length - fromIndex);
		final long[] subarray = new long[maxLength];
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	//////////////////////////////////////////////

	public static long[] take(final long[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static long[] take(final long[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static long[] take(final long[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final long[] subarray = new long[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, subarray,
					i * columnCount, columnCount);
		}
		return subarray;
	}

	//////////////////////////////////////////////

	public static long[] take(final long[][]... array3D) {
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
		final int maxRowCount = Math.min(rowCount, array3D.length - fromRow);
		final int length = columnCount * depthCount;
		final long[] subarray = new long[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					subarray, i * length, length);
		}
		return subarray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int tally(final long value, final long[] boundaries) {
		for (int i = 0; i < boundaries.length; ++i) {
			if (value < boundaries[i]) {
				return i;
			}
		}
		return boundaries.length;
	}

	public static int[] tally(final long[] array, final long[] boundaries) {
		final int[] intervals = new int[array.length];
		for (int i = 0; i < array.length; ++i) {
			intervals[i] = tally(array[i], boundaries);
		}
		return intervals;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code long} array.
	 * <p>
	 * @param rowCount the number of rows of the {@code long} array
	 * @param array    a {@code long} array
	 * <p>
	 * @return the transpose of the specified {@code long} array
	 */
	public static long[] transpose(final int rowCount, final long... array) {
		final int n = rowCount;
		final int m = array.length / rowCount;
		final long[] transpose = new long[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i * n + j] = array[j * m + i];
			}
		}
		return transpose;
	}

	/**
	 * Returns the transpose of the specified 2D {@code long} array.
	 * <p>
	 * @param array2D the 2D {@code long} array to convert
	 * <p>
	 * @return the transpose of the specified 2D {@code long} array
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
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int findFirstIndex(final long[] array, final long token) {
		if (array != null) {
			return findFirstIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final long[] array, final long token, final int from) {
		if (array != null) {
			return findFirstIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final long[] array, final long token, final int from,
			final int to) {
		if (array != null) {
			for (int i = from; i < to; ++i) {
				if (array[i] == token) {
					return i;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int findLastIndex(final long[] array, final long token) {
		if (array != null) {
			return findLastIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final long[] array, final long token, final int from) {
		if (array != null) {
			return findLastIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final long[] array, final long token, final int from,
			final int to) {
		if (array != null) {
			for (int i = to - 1; i >= from; --i) {
				if (array[i] == token) {
					return i;
				}
			}
		}
		return -1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link Long}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Long},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Long;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code long} value or a
	 * {@link Long}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code long} value or
	 *         a {@link Long}, {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return long.class.isAssignableFrom(c) || Long.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code long} value.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code long} value,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveFrom(final Class<?> c) {
		return long.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Object} is an instance of {@code long} array.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@code long} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Object object) {
		return object instanceof long[];
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code long} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code long} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArrayFrom(final Class<?> c) {
		return long[].class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code long} array is {@code null} or empty.
	 * <p>
	 * @param array the {@code long} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code long} array is {@code null} or empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final long[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code long} array is non-{@code null} and empty.
	 * <p>
	 * @param array the {@code long} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code long} array is non-{@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final long[] array) {
		return array != null && array.length == 0;
	}

	/**
	 * Tests whether the specified {@code long} array is non-{@code null} and non-empty.
	 * <p>
	 * @param array the {@code long} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code long} array is non-{@code null} and non-empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNonEmpty(final long[] array) {
		return array != null && array.length > 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code long} value is between the specified {@code long} lower
	 * and upper bounds.
	 * <p>
	 * @param value the {@code long} value to test
	 * @param from  the {@code long} lower bound to test against (inclusive)
	 * @param to    the {@code long} upper bound to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code long} value is between the specified
	 *         {@code long} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final long value, final long from, final long to) {
		return isBetween(value, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code long} value is between the specified {@code long} lower
	 * and upper bounds.
	 * <p>
	 * @param value            the {@code long} value to test
	 * @param from             the {@code long} lower bound to test against (inclusive)
	 * @param to               the {@code long} upper bound to test against
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code long} value is between the specified
	 *         {@code long} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final long value, final long from, final long to,
			final boolean isUpperInclusive) {
		return isBetween(value, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code long} value is between the specified {@code long} lower
	 * and upper bounds.
	 * <p>
	 * @param value            the {@code long} value to test
	 * @param from             the {@code long} lower bound to test against
	 * @param to               the {@code long} upper bound to test against
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code long} value is between the specified
	 *         {@code long} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final long value, final long from, final long to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? value >= from : value > from) &&
				(isUpperInclusive ? value <= to : value < to);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code long} array is between the specified lower and upper bound
	 * {@code long} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array the {@code long} array to test (may be {@code null})
	 * @param from  the lower bound {@code long} array to test against (inclusive) (may be
	 *              {@code null})
	 * @param to    the upper bound {@code long} array to test against (exclusive) (may be
	 *              {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code long} array is between the specified lower and
	 *         upper bound {@code long} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final long[] array, final long[] from, final long[] to) {
		return isBetween(array, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code long} array is between the specified lower and upper bound
	 * {@code long} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array            the {@code long} array to test (may be {@code null})
	 * @param from             the lower bound {@code long} array to test against (inclusive) (may
	 *                         be {@code null})
	 * @param to               the upper bound {@code long} array to test against (may be
	 *                         {@code null})
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code long} array is between the specified lower and
	 *         upper bound {@code long} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final long[] array, final long[] from, final long[] to,
			final boolean isUpperInclusive) {
		return isBetween(array, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code long} array is between the specified lower and upper bound
	 * {@code long} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array            the {@code long} array to test (may be {@code null})
	 * @param from             the lower bound {@code long} array to test against (may be
	 *                         {@code null})
	 * @param to               the upper bound {@code long} array to test against (may be
	 *                         {@code null})
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code long} array is between the specified lower and
	 *         upper bound {@code long} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final long[] array, final long[] from, final long[] to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? compare(array, from) >= 0 : compare(array, from) > 0) &&
				(isUpperInclusive ? compare(array, to) <= 0 : compare(array, to) < 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code long} array contains the specified {@code long} token.
	 * <p>
	 * @param array the {@code long} array to test (may be {@code null})
	 * @param token the {@code long} token to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code long} array contains the specified {@code long}
	 *         token, {@code false} otherwise
	 */
	public static boolean contains(final long[] array, final long token) {
		return findFirstIndex(array, token) >= 0;
	}

	/**
	 * Tests whether the specified {@code long} array contains any of the specified {@code long}
	 * tokens.
	 * <p>
	 * @param array  the {@code long} array to test (may be {@code null})
	 * @param tokens the {@code long} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code long} array contains any of the specified
	 *         {@code long} tokens, {@code false} otherwise
	 */
	public static boolean containsAny(final long[] array, final long... tokens) {
		if (array != null) {
			for (final long token : tokens) {
				if (contains(array, token)) {
					return true;
				}
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified {@code long} array, or {@code null} if it is {@code null}.
	 * <p>
	 * @param array the {@code long} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified {@code long} array, or {@code null} if it is {@code null}
	 */
	public static long[] clone(final long... array) {
		// Check the arguments
		if (array == null) {
			return null;
		}

		// Clone the array
		return array.clone();
	}

	/**
	 * Clones the specified 2D {@code long} array.
	 * <p>
	 * @param array2D the 2D {@code long} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified 2D {@code long} array, or {@code null} if it is {@code null}
	 */
	public static long[][] clone(final long[]... array2D) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}

		// Clone the 2D array
		final long[][] clone = new long[array2D.length]
				[array2D.length > 0 ? array2D[0].length : 0];
		for (int i = 0; i < array2D.length; ++i) {
			clone[i] = clone(array2D[i]);
		}
		return clone;
	}

	/**
	 * Clones the specified 3D {@code long} array.
	 * <p>
	 * @param array3D the 3D {@code long} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified 3D {@code long} array, or {@code null} if it is {@code null}
	 */
	public static long[][][] clone(final long[][][] array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}

		// Clone the 3D array
		final long[][][] clone = new long[array3D.length]
				[array3D.length > 0 ? array3D[0].length : 0]
				[array3D[0].length > 0 ? array3D[0][0].length : 0];
		for (int i = 0; i < array3D.length; ++i) {
			clone[i] = clone(array3D[i]);
		}
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the {@code long} array to compare for equality (may be {@code null})
	 * @param b the other {@code long} array to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final long[] a, final long[] b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null || a.length != b.length) {
			return false;
		}
		final int length = a.length; // or b.length
		for (int i = 0; i < length; ++i) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the 2D {@code long} array to compare for equality (may be {@code null})
	 * @param b the other 2D {@code long} array to compare against for equality (may be
	 *          {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final long[][] a, final long[][] b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null || a.length != b.length) {
			return false;
		}
		final int length = a.length; // or b.length
		for (int i = 0; i < length; ++i) {
			if (!equals(a[i], b[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests whether {@code a} is equal to {@code b}.
	 * <p>
	 * @param a the 3D {@code long} array to compare for equality (may be {@code null})
	 * @param b the other 3D {@code long} array to compare against for equality (may be
	 *          {@code null})
	 * <p>
	 * @return {@code true} if {@code a} is equal to {@code b}, {@code false} otherwise
	 */
	public static boolean equals(final long[][][] a, final long[][][] b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null || a.length != b.length) {
			return false;
		}
		final int length = a.length; // or b.length
		for (int i = 0; i < length; ++i) {
			if (!equals(a[i], b[i])) {
				return false;
			}
		}
		return true;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the hash code value for the specified {@code long} array.
	 * <p>
	 * @param array the {@code long} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code long} array
	 */
	public static int hashCode(final long... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code long} array at the specified depth.
	 * <p>
	 * @param depth the depth to hash at
	 * @param array the {@code long} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code long} array at the specified depth
	 */
	public static int hashCodeWith(final int depth, final long... array) {
		if (array == null) {
			return 0;
		}
		switch (array.length) {
			case 0:
				return Bits.SEEDS[depth % Bits.SEEDS.length];
			case 1:
				return (int) (array[0] ^ array[0] >>> 32);
			default:
				int hashCode = Bits.SEEDS[depth % Bits.SEEDS.length];
				for (int i = 0; i < array.length; ++i) {
					if (i % 2 == 0) {
						hashCode = Bits.rotateLeft(hashCode) ^ hashCodeWith(depth, array[i]);
					} else {
						hashCode = Bits.rotateRight(hashCode) ^ hashCodeWith(depth, array[i]);
					}
				}
				return hashCode;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@code long} array.
	 * <p>
	 * @param array the {@code long} array to convert
	 * <p>
	 * @return a representative {@link String} of the specified {@code long} array
	 */
	public static String toString(final long... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code long} array joined with the
	 * specified {@code char} delimiter.
	 * <p>
	 * @param array     a {@code long} array
	 * @param delimiter the {@code char} delimiter
	 * <p>
	 * @return a representative {@link String} of the specified {@code long} array joined with the
	 *         specified {@code char} delimiter
	 */
	public static String toStringWith(final long[] array, final char delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code long} array joined with the
	 * specified delimiting {@link String}.
	 * <p>
	 * @param array     a {@code long} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code long} array joined with the
	 *         specified delimiting {@link String}
	 */
	public static String toStringWith(final long[] array, final String delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code long} array wrapped by
	 * {@code wrapper}.
	 * <p>
	 * @param array   a {@code long} array
	 * @param wrapper an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code long} array wrapped by
	 *         {@code wrapper}
	 */
	public static String toStringWith(final long[] array, final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code long} array joined with the
	 * specified delimiting {@link String} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code long} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code long} array joined with the
	 *         specified delimiting {@link String} and wrapped by {@code wrapper}
	 */
	public static String toStringWith(final long[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), delimiter, wrapper);
	}
}
