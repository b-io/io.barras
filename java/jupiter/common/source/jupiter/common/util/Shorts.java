/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2020 Florian Barras <https://barras.io> (florian@barras.io)
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

import static java.lang.Short.MAX_VALUE;
import static java.lang.Short.MIN_VALUE;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.IParsers;
import jupiter.common.map.parser.ShortParser;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;

public class Shorts {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final short[] EMPTY_PRIMITIVE_ARRAY = new short[] {};
	public static final short[][] EMPTY_PRIMITIVE_ARRAY_2D = new short[][] {};
	public static final short[][][] EMPTY_PRIMITIVE_ARRAY_3D = new short[][][] {};

	public static final Short[] EMPTY_ARRAY = new Short[] {};
	public static final Short[][] EMPTY_ARRAY_2D = new Short[][] {};
	public static final Short[][][] EMPTY_ARRAY_3D = new Short[][][] {};

	protected static final ShortParser PARSER = IParsers.SHORT_PARSER;

	//////////////////////////////////////////////

	public static volatile Random RANDOM = new Random();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<Short> COMPARATOR = new Comparator<Short>() {
		@Override
		public int compare(final Short a, final Short b) {
			return Shorts.compare(a, b);
		}
	};

	public static final Comparator<short[]> ARRAY_COMPARATOR = new Comparator<short[]>() {
		@Override
		public int compare(final short[] a, final short[] b) {
			return Shorts.compare(a, b);
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Shorts}.
	 */
	protected Shorts() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code short} value converted from the specified {@code int} value.
	 * <p>
	 * @param value the {@code int} value to convert
	 * <p>
	 * @return a {@code short} value converted from the specified {@code int} value
	 */
	public static short convert(final int value) {
		if (value < MIN_VALUE || value > MAX_VALUE) {
			throw new ArithmeticException("Short under/overflow");
		}
		return (short) value;
	}

	/**
	 * Returns a {@code short} value converted from the specified {@code long} value.
	 * <p>
	 * @param value the {@code long} value to convert
	 * <p>
	 * @return a {@code short} value converted from the specified {@code long} value
	 */
	public static short convert(final long value) {
		if (value < MIN_VALUE || value > MAX_VALUE) {
			throw new ArithmeticException("Short under/overflow");
		}
		return (short) value;
	}

	/**
	 * Returns a {@code short} value converted from the specified {@code float} value.
	 * <p>
	 * @param value the {@code float} value to convert
	 * <p>
	 * @return a {@code short} value converted from the specified {@code float} value
	 */
	public static short convert(final float value) {
		if (value < MIN_VALUE || value > MAX_VALUE) {
			throw new ArithmeticException("Short under/overflow");
		}
		return (short) value;
	}

	/**
	 * Returns a {@code short} value converted from the specified {@code double} value.
	 * <p>
	 * @param value the {@code double} value to convert
	 * <p>
	 * @return a {@code short} value converted from the specified {@code double} value
	 */
	public static short convert(final double value) {
		if (value < MIN_VALUE || value > MAX_VALUE) {
			throw new ArithmeticException("Short under/overflow");
		}
		return (short) value;
	}

	/**
	 * Returns a {@link Short} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert (may be {@code null})
	 * <p>
	 * @return a {@link Short} converted from the specified {@link Object}
	 */
	public static Short convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code short} value from the specified {@code T} object.
	 * <p>
	 * @param <T>    the type of the object to convert
	 * @param object the {@code T} object to convert
	 * <p>
	 * @return a {@code short} value from the specified {@code T} object
	 */
	public static <T> short toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code short} array from the specified {@code short} array.
	 * <p>
	 * @param array the {@code short} array to convert
	 * <p>
	 * @return a {@code short} array from the specified {@code short} array
	 */
	public static short[] toPrimitiveArray(final short... array) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return EMPTY_PRIMITIVE_ARRAY;
		}

		// Copy the array to an array
		final short[] output = new short[array.length];
		System.arraycopy(array, 0, output, 0, array.length);
		return output;
	}

	/**
	 * Returns a {@code short} array from the specified 2D {@code short} array.
	 * <p>
	 * @param array2D the 2D {@code short} array to convert
	 * <p>
	 * @return a {@code short} array from the specified 2D {@code short} array
	 */
	public static short[] toPrimitiveArray(final short[]... array2D) {
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
		final short[] output = new short[rowCount * columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output, i * columnCount, columnCount);
		}
		return output;
	}

	/**
	 * Returns a {@code short} array from the specified 3D {@code short} array.
	 * <p>
	 * @param array3D the 3D {@code short} array to convert
	 * <p>
	 * @return a {@code short} array from the specified 3D {@code short} array
	 */
	public static short[] toPrimitiveArray(final short[][]... array3D) {
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
		final short[] output = new short[rowCount * columnCount * depthCount];
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
	 * Returns a {@code short} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code short} array from the specified {@code T} array
	 */
	public static <T> short[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code short} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code short} array from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> short[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code short} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code short} array from the specified 2D {@code T} array
	 */
	public static <T> short[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code short} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code short} array from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> short[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code short} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code short} array from the specified 3D {@code T} array
	 */
	public static <T> short[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code short} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code short} array from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> short[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	public static short[][] toPrimitiveArray2D(final short[] array, final int rowCount) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0 || rowCount == 0) {
			return EMPTY_PRIMITIVE_ARRAY_2D;
		}

		// Copy the array to a 2D array
		final int columnCount = array.length / rowCount;
		final short[][] output2D = new short[rowCount][columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array, i * columnCount, output2D[i], 0, columnCount);
		}
		return output2D;
	}

	public static short[][] toPrimitiveArray2D(final short[][] array2D) {
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
		final short[][] output2D = new short[rowCount][columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output2D[i], 0, columnCount);
		}
		return output2D;
	}

	public static short[][] toPrimitiveArray2D(final short[][][] array3D) {
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
		final short[][] output2D = new short[rowCount][columnCount * depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output2D[i], j * depthCount, depthCount);
			}
		}
		return output2D;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code short} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code short} array from the specified 2D {@code T} array
	 */
	public static <T> short[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code short} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code short} array from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> short[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	//////////////////////////////////////////////

	public static short[][][] toPrimitiveArray3D(final short[] array, final int rowCount,
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
		final short[][][] output3D = new short[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array, (i * columnCount + j) * depthCount, output3D[i][j], 0,
						depthCount);
			}
		}
		return output3D;
	}

	public static short[][][] toPrimitiveArray3D(final short[][] array2D,
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
		final short[][][] output3D = new short[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array2D[i], j * depthCount, output3D[i][j], 0, depthCount);
			}
		}
		return output3D;
	}

	public static short[][][] toPrimitiveArray3D(final short[][][] array3D) {
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
		final short[][][] output3D = new short[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output3D[i][j], 0, depthCount);
			}
		}
		return output3D;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D {@code short} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code short} array from the specified 3D {@code T} array
	 */
	public static <T> short[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code short} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code short} array from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> short[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code short} array from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return a {@code short} array from the specified {@link Collection}
	 */
	public static short[] collectionToPrimitiveArray(final Collection<?> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Short} from the specified {@code short} array.
	 * <p>
	 * @param array the {@code short} array to convert
	 * <p>
	 * @return an array of {@link Short} from the specified {@code short} array
	 */
	public static Short[] toArray(final short[] array) {
		final Short[] convertedArray = new Short[array.length];
		for (int i = 0; i < array.length; ++i) {
			convertedArray[i] = array[i];
		}
		return convertedArray;
	}

	/**
	 * Returns an array of {@link Short} from the specified {@code short} array.
	 * <p>
	 * @param array the {@code short} array to convert
	 * <p>
	 * @return an array of {@link Short} from the specified {@code short} array
	 */
	public static Short[] asArray(final short... array) {
		return toArray(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D array of {@link Short} from the specified 2D {@code short} array.
	 * <p>
	 * @param array2D the 2D {@code short} array to convert
	 * <p>
	 * @return a 2D array of {@link Short} from the specified 2D {@code short} array
	 */
	public static Short[][] toArray2D(final short[][] array2D) {
		final Short[][] convertedArray2D = new Short[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			convertedArray2D[i] = toArray(array2D[i]);
		}
		return convertedArray2D;
	}

	/**
	 * Returns a 2D array of {@link Short} from the specified 2D {@code short} array.
	 * <p>
	 * @param array2D the 2D {@code short} array to convert
	 * <p>
	 * @return a 2D array of {@link Short} from the specified 2D {@code short} array
	 */
	public static Short[][] asArray2D(final short[]... array2D) {
		return toArray2D(array2D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D array of {@link Short} from the specified 3D {@code short} array.
	 * <p>
	 * @param array3D the 3D {@code short} array to convert
	 * <p>
	 * @return a 3D array of {@link Short} from the specified 3D {@code short} array
	 */
	public static Short[][][] toArray3D(final short[][][] array3D) {
		final Short[][][] convertedArray3D = new Short[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			convertedArray3D[i] = toArray2D(array3D[i]);
		}
		return convertedArray3D;
	}

	/**
	 * Returns a 3D array of {@link Short} from the specified 3D {@code short} array.
	 * <p>
	 * @param array3D the 3D {@code short} array to convert
	 * <p>
	 * @return a 3D array of {@link Short} from the specified 3D {@code short} array
	 */
	public static Short[][][] asArray3D(final short[][]... array3D) {
		return toArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an array of {@link Short} from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an array of {@link Short} from the specified {@link Collection}
	 */
	public static Short[] collectionToArray(final Collection<?> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Short} from the specified {@code short} array.
	 * <p>
	 * @param array the {@code short} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Short} from the specified {@code short} array
	 */
	public static ExtendedList<Short> toList(final short[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Short} from the specified {@code short} array.
	 * <p>
	 * @param array the {@code short} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Short} from the specified {@code short} array
	 */
	public static ExtendedList<Short> asList(final short... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Short} from the specified {@code short}
	 * array.
	 * <p>
	 * @param array the {@code short} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Short} from the specified {@code short} array
	 */
	public static ExtendedLinkedList<Short> toLinkedList(final short[] array) {
		return PARSER.callToLinkedList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Short} from the specified {@code short}
	 * array.
	 * <p>
	 * @param array the {@code short} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Short} from the specified {@code short} array
	 */
	public static ExtendedLinkedList<Short> asLinkedList(final short... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Short} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Short} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Short> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Short} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Short} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedList<Short> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Short} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Short} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<Short> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Short} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Short} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedLinkedList<Short> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Short} from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Short} from the specified {@link Collection}
	 */
	public static ExtendedList<Short> collectionToList(final Collection<?> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Short} from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Short} from the specified {@link Collection}
	 */
	public static ExtendedLinkedList<Short> collectionToLinkedList(
			final Collection<?> collection) {
		return PARSER.callCollectionToLinkedList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Short} from the specified {@code short} array.
	 * <p>
	 * @param array the {@code short} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Short} from the specified {@code short} array
	 */
	public static Set<Short> toSet(final short[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns a {@link Set} of {@link Short} from the specified {@code short} array.
	 * <p>
	 * @param array the {@code short} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Short} from the specified {@code short} array
	 */
	public static Set<Short> asSet(final short... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Short} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Short} from the specified {@code T} array
	 */
	public static <T> Set<Short> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Short} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Short} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> Set<Short> asSet(final T... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Short} from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return a {@link Set} of {@link Short} from the specified {@link Collection}
	 */
	public static Set<Short> collectionToSet(final Collection<?> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code short} array of the specified length containing the sequence of numbers
	 * starting with {@code 0} and spaced by {@code 1}.
	 * <p>
	 * @param length the length of the sequence to create
	 * <p>
	 * @return a {@code short} array of the specified length containing the sequence of numbers
	 *         starting with {@code 0} and spaced by {@code 1}
	 */
	public static short[] createSequence(final int length) {
		return createSequence(length, (short) 0, (short) 1);
	}

	/**
	 * Creates a {@code short} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code 1}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * <p>
	 * @return a {@code short} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code 1}
	 */
	public static short[] createSequence(final int length, final short from) {
		return createSequence(length, from, (short) 1);
	}

	/**
	 * Creates a {@code short} array of the specified length containing the sequence of numbers
	 * starting with {@code from} and spaced by {@code step}.
	 * <p>
	 * @param length the length of the sequence to create
	 * @param from   the first value of the sequence to create
	 * @param step   the interval between the values of the sequence to create
	 * <p>
	 * @return a {@code short} array of the specified length containing the sequence of numbers
	 *         starting with {@code from} and spaced by {@code step}
	 */
	public static short[] createSequence(final int length, final short from, final short step) {
		final short[] array = new short[length];
		short value = from;
		for (int i = 0; i < length; ++i, value += step) {
			array[i] = value;
		}
		return array;
	}

	//////////////////////////////////////////////

	/**
	 * Creates a random {@code short} array of the specified length.
	 * <p>
	 * @param length the length of the random sequence to create
	 * <p>
	 * @return a random {@code short} array of the specified length
	 */
	public static short[] createRandomSequence(final int length) {
		final short[] array = new short[length];
		for (int i = 0; i < length; ++i) {
			array[i] = random();
		}
		return array;
	}

	/**
	 * Creates a {@code short} array of the specified length containing pseudorandom, uniformly
	 * distributed {@code short} values between the specified bounds.
	 * <p>
	 * @param length     the length of the random sequence to create
	 * @param lowerBound the {@code short} lower bound of the random sequence to create (inclusive)
	 * @param upperBound the {@code short} upper bound of the random sequence to create (exclusive)
	 * <p>
	 * @return a {@code short} array of the specified length containing pseudorandom, uniformly
	 *         distributed {@code short} values between the specified bounds
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

	/**
	 * Returns a pseudorandom, uniformly distributed {@code short} value.
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code short} value
	 */
	public static short random() {
		return random(MIN_VALUE, MAX_VALUE);
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@code short} value between the specified
	 * bounds.
	 * <p>
	 * @param lowerBound the {@code short} lower bound of the value to generate (inclusive)
	 * @param upperBound the {@code short} upper bound of the value to generate (exclusive)
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@code short} value between the specified
	 *         bounds
	 */
	public static short random(final short lowerBound, final short upperBound) {
		return convert(lowerBound + RANDOM.nextInt(upperBound - lowerBound));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code short} array of the specified length with the specified {@code short}
	 * element.
	 * <p>
	 * @param element the {@code short} element of the {@code short} array to create
	 * @param length  the length of the {@code short} array to create
	 * <p>
	 * @return a {@code short} array of the specified length with the specified {@code short}
	 *         element
	 */
	public static short[] repeat(final short element, final int length) {
		return fill(new short[length], element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of elements in the specified 2D {@code short} array.
	 * <p>
	 * @param array2D the 2D {@code short} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 2D {@code short} array
	 */
	public static int count(final short[][] array2D) {
		int count = 0;
		if (array2D != null) {
			for (final short[] array : array2D) {
				count += array.length;
			}
		}
		return count;
	}

	/**
	 * Returns the number of elements in the specified 3D {@code short} array.
	 * <p>
	 * @param array3D the 3D {@code short} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 3D {@code short} array
	 */
	public static int count(final short[][][] array3D) {
		int count = 0;
		if (array3D != null) {
			for (final short[][] array2D : array3D) {
				count += count(array2D);
			}
		}
		return count;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code short} token in the specified
	 * {@code short} array.
	 * <p>
	 * @param array the {@code short} array to count from (may be {@code null})
	 * @param token the {@code short} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code short} token in the specified
	 *         {@code short} array
	 */
	public static int count(final short[] array, final short token) {
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
	 * Returns the number of occurrences of the specified {@code short} token in the specified 2D
	 * {@code short} array.
	 * <p>
	 * @param array2D the 2D {@code short} array to count from (may be {@code null})
	 * @param token   the {@code short} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code short} token in the specified 2D
	 *         {@code short} array
	 */
	public static int count(final short[][] array2D, final short token) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final short[] array : array2D) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code short} token in the specified 3D
	 * {@code short} array.
	 * <p>
	 * @param array3D the 3D {@code short} array to count from (may be {@code null})
	 * @param token   the {@code short} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code short} token in the specified 3D
	 *         {@code short} array
	 */
	public static int count(final short[][][] array3D, final short token) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final short[][] array2D : array3D) {
				occurrenceCount += count(array2D, token);
			}
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code short} tokens in the specified
	 * {@code short} array.
	 * <p>
	 * @param array  the {@code short} array to count from (may be {@code null})
	 * @param tokens the {@code short} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code short} tokens in the specified
	 *         {@code short} array
	 */
	public static int count(final short[] array, final short... tokens) {
		int occurrenceCount = 0;
		if (array != null && tokens != null) {
			for (final short token : tokens) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code short} tokens in the specified 2D
	 * {@code short} array.
	 * <p>
	 * @param array2D the 2D {@code short} array to count from (may be {@code null})
	 * @param tokens  the {@code short} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code short} tokens in the specified 2D
	 *         {@code short} array
	 */
	public static int count(final short[][] array2D, final short... tokens) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final short[] array : array2D) {
				occurrenceCount += count(array, tokens);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code short} tokens in the specified 3D
	 * {@code short} array.
	 * <p>
	 * @param array3D the 3D {@code short} array to count from (may be {@code null})
	 * @param tokens  the {@code short} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code short} tokens in the specified 3D
	 *         {@code short} array
	 */
	public static int count(final short[][][] array3D, final short... tokens) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final short[][] array2D : array3D) {
				occurrenceCount += count(array2D, tokens);
			}
		}
		return occurrenceCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static short[] fill(final short[] array, final short value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
		return array;
	}

	public static short[][] fill(final short[][] array2D, final short value) {
		for (final short[] array : array2D) {
			fill(array, value);
		}
		return array2D;
	}

	public static short[][][] fill(final short[][][] array3D, final short value) {
		for (final short[][] array2D : array3D) {
			fill(array2D, value);
		}
		return array3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code short} array containing all the elements of the specified {@code short}
	 * array at the specified indices.
	 * <p>
	 * @param array   the {@code short} array to filter from
	 * @param indices the indices to filter
	 * <p>
	 * @return a {@code short} array containing all the elements of the specified {@code short}
	 *         array at the specified indices
	 */
	public static short[] filter(final short[] array, final int... indices) {
		final short[] filteredArray = new short[indices.length];
		for (int i = 0; i < indices.length; ++i) {
			filteredArray[i] = array[indices[i]];
		}
		return filteredArray;
	}

	/**
	 * Returns a 2D {@code short} array containing all the elements of the specified {@code short}
	 * array at all the specified indices.
	 * <p>
	 * @param array   the {@code short} array to filter from
	 * @param indices the array of indices to filter
	 * <p>
	 * @return a 2D {@code short} array containing all the elements of the specified {@code short}
	 *         array at all the specified indices
	 */
	public static short[][] filterAll(final short[] array, final int[]... indices) {
		final short[][] filteredArrays = new short[indices.length][];
		for (int i = 0; i < indices.length; ++i) {
			filteredArrays[i] = filter(array, indices[i]);
		}
		return filteredArrays;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code short} array containing the specified {@code short} value and all the
	 * elements of the specified {@code short} array.
	 * <p>
	 * @param a a {@code short} value
	 * @param b another {@code short} array (may be {@code null})
	 * <p>
	 * @return a {@code short} array containing the specified {@code short} value and all the
	 *         elements of the specified {@code short} array
	 */
	public static short[] merge(final short a, final short... b) {
		return merge(asPrimitiveArray(a), b);
	}

	/**
	 * Returns a {@code short} array containing all the elements of the specified {@code short}
	 * arrays.
	 * <p>
	 * @param a a {@code short} array (may be {@code null})
	 * @param b another {@code short} array (may be {@code null})
	 * <p>
	 * @return a {@code short} array containing all the elements of the specified {@code short}
	 *         arrays
	 */
	public static short[] merge(final short[] a, final short... b) {
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}
		final short[] mergedArray = new short[a.length + b.length];
		System.arraycopy(a, 0, mergedArray, 0, a.length);
		System.arraycopy(b, 0, mergedArray, a.length, b.length);
		return mergedArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code short} value rounded down.
	 * <p>
	 * @param value a {@code short} value
	 * <p>
	 * @return the middle of the specified {@code short} value rounded down
	 */
	public static short middle(final short value) {
		return convert(value / 2);
	}

	/**
	 * Returns the middle of the specified {@code short} lower and upper bounds rounded down.
	 * <p>
	 * @param lowerBound a {@code short} value
	 * @param upperBound another {@code short} value
	 * <p>
	 * @return the middle of the specified {@code short} lower and upper bounds rounded down
	 */
	public static short middle(final short lowerBound, final short upperBound) {
		return convert(lowerBound + (upperBound - lowerBound) / 2);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the middle of the specified {@code short} value rounded up.
	 * <p>
	 * @param value a {@code short} value
	 * <p>
	 * @return the middle of the specified {@code short} value rounded up
	 */
	public static short middleUp(final short value) {
		return convert((value + 1) / 2);
	}

	/**
	 * Returns the middle of the specified {@code short} lower and upper bounds rounded up.
	 * <p>
	 * @param lowerBound a {@code short} value
	 * @param upperBound another {@code short} value
	 * <p>
	 * @return the middle of the specified {@code short} lower and upper bounds rounded up
	 */
	public static short middleUp(final short lowerBound, final short upperBound) {
		return convert(lowerBound + (upperBound - lowerBound + 1) / 2);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void reverse(final short... array) {
		reverse(array, 0, array.length);
	}

	public static void reverse(final short[] array, final int fromIndex, final int toIndex) {
		final int limit = Integers.middle(toIndex - fromIndex);
		for (int i = 0; i < limit; ++i) {
			swap(array, fromIndex + i, toIndex - 1 - i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shuffles the specified {@code short} array.
	 * <p>
	 * @param array the {@code short} array to shuffle
	 */
	public static void shuffle(final short... array) {
		shuffle(array, 0, array.length);
	}

	/**
	 * Shuffles the specified {@code short} array between the specified indices.
	 * <p>
	 * @param array     the {@code short} array to shuffle
	 * @param fromIndex the index to start shuffling from (inclusive)
	 * @param toIndex   the index to finish shuffling at (exclusive)
	 */
	public static void shuffle(final short[] array, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			swap(array, i, Integers.random(fromIndex, toIndex));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final short[] array, final int i, final int j) {
		final short element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static short[] take(final short[] array, final int fromIndex, final int length) {
		final int maxLength = Math.min(length, array.length - fromIndex);
		final short[] subarray = new short[maxLength];
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	//////////////////////////////////////////////

	public static short[] take(final short[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static short[] take(final short[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static short[] take(final short[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final short[] subarray = new short[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, subarray,
					i * columnCount, columnCount);
		}
		return subarray;
	}

	//////////////////////////////////////////////

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
		final int maxRowCount = Math.min(rowCount, array3D.length - fromRow);
		final int length = columnCount * depthCount;
		final short[] subarray = new short[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					subarray, i * length, length);
		}
		return subarray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code short} array.
	 * <p>
	 * @param rowCount the number of rows of the {@code short} array
	 * @param array    a {@code short} array
	 * <p>
	 * @return the transpose of the specified {@code short} array
	 */
	public static short[] transpose(final int rowCount, final short... array) {
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
	 * Returns the transpose of the specified 2D {@code short} array.
	 * <p>
	 * @param array2D the 2D {@code short} array to convert
	 * <p>
	 * @return the transpose of the specified 2D {@code short} array
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
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int findFirstIndex(final short[] array, final short token) {
		if (array != null) {
			return findFirstIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final short[] array, final short token, final int from) {
		if (array != null) {
			return findFirstIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final short[] array, final short token, final int from,
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

	public static int findLastIndex(final short[] array, final short token) {
		if (array != null) {
			return findLastIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final short[] array, final short token, final int from) {
		if (array != null) {
			return findLastIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final short[] array, final short token, final int from,
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
	 * Tests whether the specified {@link Object} is an instance of {@link Short}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Short},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Short;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code short} value or a
	 * {@link Short}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code short} value or
	 *         a {@link Short}, {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return short.class.isAssignableFrom(c) || Short.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code short} value.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code short} value,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveFrom(final Class<?> c) {
		return short.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Object} is an instance of {@code short} array.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@code short} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Object object) {
		return object instanceof short[];
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code short} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code short} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArrayFrom(final Class<?> c) {
		return short[].class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code short} array is {@code null} or empty.
	 * <p>
	 * @param array the {@code short} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code short} array is {@code null} or empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final short[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code short} array is non-{@code null} and empty.
	 * <p>
	 * @param array the {@code short} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code short} array is non-{@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final short[] array) {
		return array != null && array.length == 0;
	}

	/**
	 * Tests whether the specified {@code short} array is non-{@code null} and non-empty.
	 * <p>
	 * @param array the {@code short} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code short} array is non-{@code null} and non-empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNonEmpty(final short[] array) {
		return array != null && array.length > 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code short} value is between the specified {@code short} lower
	 * and upper bounds.
	 * <p>
	 * @param value the {@code short} value to test
	 * @param from  the {@code short} lower bound to test against (inclusive)
	 * @param to    the {@code short} upper bound to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code short} value is between the specified
	 *         {@code short} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final short value, final short from, final short to) {
		return isBetween(value, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code short} value is between the specified {@code short} lower
	 * and upper bounds.
	 * <p>
	 * @param value            the {@code short} value to test
	 * @param from             the {@code short} lower bound to test against (inclusive)
	 * @param to               the {@code short} upper bound to test against
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code short} value is between the specified
	 *         {@code short} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final short value, final short from, final short to,
			final boolean isUpperInclusive) {
		return isBetween(value, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code short} value is between the specified {@code short} lower
	 * and upper bounds.
	 * <p>
	 * @param value            the {@code short} value to test
	 * @param from             the {@code short} lower bound to test against
	 * @param to               the {@code short} upper bound to test against
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code short} value is between the specified
	 *         {@code short} lower and upper bounds, {@code false} otherwise
	 */
	public static boolean isBetween(final short value, final short from, final short to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? value >= from : value > from) &&
				(isUpperInclusive ? value <= to : value < to);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code short} array is between the specified lower and upper
	 * bound {@code short} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array the {@code short} array to test (may be {@code null})
	 * @param from  the lower bound {@code short} array to test against (inclusive) (may be
	 *              {@code null})
	 * @param to    the upper bound {@code short} array to test against (exclusive) (may be
	 *              {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code short} array is between the specified lower and
	 *         upper bound {@code short} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final short[] array, final short[] from, final short[] to) {
		return isBetween(array, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code short} array is between the specified lower and upper
	 * bound {@code short} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array            the {@code short} array to test (may be {@code null})
	 * @param from             the lower bound {@code short} array to test against (inclusive) (may
	 *                         be {@code null})
	 * @param to               the upper bound {@code short} array to test against (may be
	 *                         {@code null})
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code short} array is between the specified lower and
	 *         upper bound {@code short} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final short[] array, final short[] from, final short[] to,
			final boolean isUpperInclusive) {
		return isBetween(array, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code short} array is between the specified lower and upper
	 * bound {@code short} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param array            the {@code short} array to test (may be {@code null})
	 * @param from             the lower bound {@code short} array to test against (may be
	 *                         {@code null})
	 * @param to               the upper bound {@code short} array to test against (may be
	 *                         {@code null})
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code short} array is between the specified lower and
	 *         upper bound {@code short} arrays, {@code false} otherwise
	 */
	public static boolean isBetween(final short[] array, final short[] from, final short[] to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? compare(array, from) >= 0 : compare(array, from) > 0) &&
				(isUpperInclusive ? compare(array, to) <= 0 : compare(array, to) < 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code short} array contains the specified {@code short} token.
	 * <p>
	 * @param array the {@code short} array to test (may be {@code null})
	 * @param token the {@code short} token to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code short} array contains the specified
	 *         {@code short} token, {@code false} otherwise
	 */
	public static boolean contains(final short[] array, final short token) {
		return findFirstIndex(array, token) >= 0;
	}

	/**
	 * Tests whether the specified {@code short} array contains any of the specified {@code short}
	 * tokens.
	 * <p>
	 * @param array  the {@code short} array to test (may be {@code null})
	 * @param tokens the {@code short} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code short} array contains any of the specified
	 *         {@code short} tokens, {@code false} otherwise
	 */
	public static boolean containsAny(final short[] array, final short... tokens) {
		if (array != null) {
			for (final short token : tokens) {
				if (contains(array, token)) {
					return true;
				}
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@code short} values for order. Returns a negative integer, {@code 0}
	 * or a positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@code short} value to compare for order
	 * @param b the other {@code short} value to compare against for order
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final short a, final short b) {
		return a < b ? -1 : a == b ? 0 : 1;
	}

	//////////////////////////////////////////////

	/**
	 * Compares the specified {@code short} arrays for order. Returns a negative integer, {@code 0}
	 * or a positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@code short} array to compare for order (may be {@code null})
	 * @param b the other {@code short} array to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final short[] a, final short[] b) {
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
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified {@code short} array, or {@code null} if it is {@code null}.
	 * <p>
	 * @param array the {@code short} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified {@code short} array, or {@code null} if it is {@code null}
	 */
	public static short[] clone(final short... array) {
		// Check the arguments
		if (array == null) {
			return null;
		}

		// Clone the array
		return array.clone();
	}

	/**
	 * Clones the specified 2D {@code short} array.
	 * <p>
	 * @param array2D the 2D {@code short} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified 2D {@code short} array, or {@code null} if it is
	 *         {@code null}
	 */
	public static short[][] clone(final short[]... array2D) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}

		// Clone the 2D array
		final short[][] clone = new short[array2D.length]
				[array2D.length > 0 ? array2D[0].length : 0];
		for (int i = 0; i < array2D.length; ++i) {
			clone[i] = clone(array2D[i]);
		}
		return clone;
	}

	/**
	 * Clones the specified 3D {@code short} array.
	 * <p>
	 * @param array3D the 3D {@code short} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified 3D {@code short} array, or {@code null} if it is
	 *         {@code null}
	 */
	public static short[][][] clone(final short[][][] array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}

		// Clone the 3D array
		final short[][][] clone = new short[array3D.length]
				[array3D.length > 0 ? array3D[0].length : 0]
				[array3D[0].length > 0 ? array3D[0][0].length : 0];
		for (int i = 0; i < array3D.length; ++i) {
			clone[i] = clone(array3D[i]);
		}
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the hash code value for the specified {@code short} array.
	 * <p>
	 * @param array the {@code short} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code short} array
	 */
	public static int hashCode(final short... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code short} array at the specified depth.
	 * <p>
	 * @param depth the depth to hash at
	 * @param array the {@code short} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code short} array at the specified depth
	 */
	public static int hashCodeWith(final int depth, final short... array) {
		if (array == null) {
			return 0;
		}
		switch (array.length) {
			case 0:
				return Bits.SEEDS[depth % Bits.SEEDS.length];
			case 1:
				return array[0];
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
	 * Returns a representative {@link String} of the specified {@code short} array.
	 * <p>
	 * @param array the {@code short} array to convert
	 * <p>
	 * @return a representative {@link String} of the specified {@code short} array
	 */
	public static String toString(final short... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code short} array joined by the
	 * specified {@code char} delimiter.
	 * <p>
	 * @param array     a {@code short} array
	 * @param delimiter the {@code char} delimiter
	 * <p>
	 * @return a representative {@link String} of the specified {@code short} array joined by the
	 *         specified {@code char} delimiter
	 */
	public static String toStringWith(final short[] array, final char delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code short} array joined by the
	 * specified delimiting {@link String}.
	 * <p>
	 * @param array     a {@code short} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code short} array joined by the
	 *         specified delimiting {@link String}
	 */
	public static String toStringWith(final short[] array, final String delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code short} array wrapped by
	 * {@code wrapper}.
	 * <p>
	 * @param array   a {@code short} array
	 * @param wrapper an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code short} array wrapped by
	 *         {@code wrapper}
	 */
	public static String toStringWith(final short[] array, final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code short} array joined by the
	 * specified delimiting {@link String} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code short} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code short} array joined by the
	 *         specified delimiting {@link String} and wrapped by {@code wrapper}
	 */
	public static String toStringWith(final short[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), delimiter, wrapper);
	}
}
