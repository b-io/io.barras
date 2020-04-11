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

import java.util.Collection;
import java.util.Random;
import java.util.Set;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.BooleanParser;
import jupiter.common.map.parser.IParsers;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;

public class Booleans {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final boolean[] EMPTY_PRIMITIVE_ARRAY = new boolean[] {};
	public static final boolean[][] EMPTY_PRIMITIVE_ARRAY_2D = new boolean[][] {};
	public static final boolean[][][] EMPTY_PRIMITIVE_ARRAY_3D = new boolean[][][] {};

	public static final Boolean[] EMPTY_ARRAY = new Boolean[] {};
	public static final Boolean[][] EMPTY_ARRAY_2D = new Boolean[][] {};
	public static final Boolean[][][] EMPTY_ARRAY_3D = new Boolean[][][] {};

	protected static final BooleanParser PARSER = IParsers.BOOLEAN_PARSER;

	//////////////////////////////////////////////

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
	 * @param object the {@link Object} to convert (may be {@code null})
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
	 * @param object the {@code T} object to convert
	 * <p>
	 * @return a {@code boolean} value from the specified {@code T} object
	 */
	public static <T> boolean toPrimitive(final T object) {
		return PARSER.callToPrimitive(object);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code boolean} array from the specified {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to convert
	 * <p>
	 * @return a {@code boolean} array from the specified {@code boolean} array
	 */
	public static boolean[] toPrimitiveArray(final boolean... array) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return EMPTY_PRIMITIVE_ARRAY;
		}

		// Copy the array to an array
		final boolean[] output = new boolean[array.length];
		System.arraycopy(array, 0, output, 0, array.length);
		return output;
	}

	/**
	 * Returns a {@code boolean} array from the specified 2D {@code boolean} array.
	 * <p>
	 * @param array2D the 2D {@code boolean} array to convert
	 * <p>
	 * @return a {@code boolean} array from the specified 2D {@code boolean} array
	 */
	public static boolean[] toPrimitiveArray(final boolean[]... array2D) {
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
		final boolean[] output = new boolean[rowCount * columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output, i * columnCount, columnCount);
		}
		return output;
	}

	/**
	 * Returns a {@code boolean} array from the specified 3D {@code boolean} array.
	 * <p>
	 * @param array3D the 3D {@code boolean} array to convert
	 * <p>
	 * @return a {@code boolean} array from the specified 3D {@code boolean} array
	 */
	public static boolean[] toPrimitiveArray(final boolean[][]... array3D) {
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
		final boolean[] output = new boolean[rowCount * columnCount * depthCount];
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
	 * Returns a {@code boolean} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code boolean} array from the specified {@code T} array
	 */
	public static <T> boolean[] toPrimitiveArray(final T[] array) {
		return PARSER.callToPrimitiveArray(array);
	}

	/**
	 * Returns a {@code boolean} array from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@code boolean} array from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> boolean[] asPrimitiveArray(final T... array) {
		return toPrimitiveArray(array);
	}

	/**
	 * Returns a {@code boolean} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code boolean} array from the specified 2D {@code T} array
	 */
	public static <T> boolean[] toPrimitiveArray(final T[][] array2D) {
		return PARSER.callToPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code boolean} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a {@code boolean} array from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> boolean[] asPrimitiveArray(final T[]... array2D) {
		return toPrimitiveArray(array2D);
	}

	/**
	 * Returns a {@code boolean} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code boolean} array from the specified 3D {@code T} array
	 */
	public static <T> boolean[] toPrimitiveArray(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray(array3D);
	}

	/**
	 * Returns a {@code boolean} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a {@code boolean} array from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> boolean[] asPrimitiveArray(final T[][]... array3D) {
		return toPrimitiveArray(array3D);
	}

	//////////////////////////////////////////////

	public static boolean[][] toPrimitiveArray2D(final boolean[] array, final int rowCount) {
		// Check the arguments
		if (array == null) {
			return null;
		}
		if (array.length == 0 || rowCount == 0) {
			return EMPTY_PRIMITIVE_ARRAY_2D;
		}

		// Copy the array to a 2D array
		final int columnCount = array.length / rowCount;
		final boolean[][] output2D = new boolean[rowCount][columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array, i * columnCount, output2D[i], 0, columnCount);
		}
		return output2D;
	}

	public static boolean[][] toPrimitiveArray2D(final boolean[][] array2D) {
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
		final boolean[][] output2D = new boolean[rowCount][columnCount];
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output2D[i], 0, columnCount);
		}
		return output2D;
	}

	public static boolean[][] toPrimitiveArray2D(final boolean[][][] array3D) {
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
		final boolean[][] output2D = new boolean[rowCount][columnCount * depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output2D[i], j * depthCount, depthCount);
			}
		}
		return output2D;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D {@code boolean} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code boolean} array from the specified 2D {@code T} array
	 */
	public static <T> boolean[][] toPrimitiveArray2D(final T[][] array2D) {
		return PARSER.callToPrimitiveArray2D(array2D);
	}

	/**
	 * Returns a 2D {@code boolean} array from the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array2D the 2D {@code T} array to convert
	 * <p>
	 * @return a 2D {@code boolean} array from the specified 2D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> boolean[][] asPrimitiveArray2D(final T[]... array2D) {
		return toPrimitiveArray2D(array2D);
	}

	//////////////////////////////////////////////

	public static boolean[][][] toPrimitiveArray3D(final boolean[] array, final int rowCount,
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
		final boolean[][][] output3D = new boolean[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array, (i * columnCount + j) * depthCount, output3D[i][j], 0,
						depthCount);
			}
		}
		return output3D;
	}

	public static boolean[][][] toPrimitiveArray3D(final boolean[][] array2D,
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
		final boolean[][][] output3D = new boolean[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array2D[i], j * depthCount, output3D[i][j], 0, depthCount);
			}
		}
		return output3D;
	}

	public static boolean[][][] toPrimitiveArray3D(final boolean[][][] array3D) {
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
		final boolean[][][] output3D = new boolean[rowCount][columnCount][depthCount];
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output3D[i][j], 0, depthCount);
			}
		}
		return output3D;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D {@code boolean} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code boolean} array from the specified 3D {@code T} array
	 */
	public static <T> boolean[][][] toPrimitiveArray3D(final T[][][] array3D) {
		return PARSER.callToPrimitiveArray3D(array3D);
	}

	/**
	 * Returns a 3D {@code boolean} array from the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the array to convert
	 * @param array3D the 3D {@code T} array to convert
	 * <p>
	 * @return a 3D {@code boolean} array from the specified 3D {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> boolean[][][] asPrimitiveArray3D(final T[][]... array3D) {
		return toPrimitiveArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@code boolean} array from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return a {@code boolean} array from the specified {@link Collection}
	 */
	public static boolean[] collectionToPrimitiveArray(final Collection<?> collection) {
		return PARSER.callCollectionToPrimitiveArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to convert
	 * <p>
	 * @return an array of {@link Boolean} from the specified {@code boolean} array
	 */
	public static Boolean[] toArray(final boolean[] array) {
		final Boolean[] convertedArray = new Boolean[array.length];
		for (int i = 0; i < array.length; ++i) {
			convertedArray[i] = array[i];
		}
		return convertedArray;
	}

	/**
	 * Returns an array of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to convert
	 * <p>
	 * @return an array of {@link Boolean} from the specified {@code boolean} array
	 */
	public static Boolean[] asArray(final boolean... array) {
		return toArray(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D array of {@link Boolean} from the specified 2D {@code boolean} array.
	 * <p>
	 * @param array2D the 2D {@code boolean} array to convert
	 * <p>
	 * @return a 2D array of {@link Boolean} from the specified 2D {@code boolean} array
	 */
	public static Boolean[][] toArray2D(final boolean[][] array2D) {
		final Boolean[][] convertedArray2D = new Boolean[array2D.length][];
		for (int i = 0; i < array2D.length; ++i) {
			convertedArray2D[i] = toArray(array2D[i]);
		}
		return convertedArray2D;
	}

	/**
	 * Returns a 2D array of {@link Boolean} from the specified 2D {@code boolean} array.
	 * <p>
	 * @param array2D the 2D {@code boolean} array to convert
	 * <p>
	 * @return a 2D array of {@link Boolean} from the specified 2D {@code boolean} array
	 */
	public static Boolean[][] asArray2D(final boolean[]... array2D) {
		return toArray2D(array2D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D array of {@link Boolean} from the specified 3D {@code boolean} array.
	 * <p>
	 * @param array3D the 3D {@code boolean} array to convert
	 * <p>
	 * @return a 3D array of {@link Boolean} from the specified 3D {@code boolean} array
	 */
	public static Boolean[][][] toArray3D(final boolean[][][] array3D) {
		final Boolean[][][] convertedArray3D = new Boolean[array3D.length][][];
		for (int i = 0; i < array3D.length; ++i) {
			convertedArray3D[i] = toArray2D(array3D[i]);
		}
		return convertedArray3D;
	}

	/**
	 * Returns a 3D array of {@link Boolean} from the specified 3D {@code boolean} array.
	 * <p>
	 * @param array3D the 3D {@code boolean} array to convert
	 * <p>
	 * @return a 3D array of {@link Boolean} from the specified 3D {@code boolean} array
	 */
	public static Boolean[][][] asArray3D(final boolean[][]... array3D) {
		return toArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an array of {@link Boolean} from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an array of {@link Boolean} from the specified {@link Collection}
	 */
	public static Boolean[] collectionToArray(final Collection<?> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified {@code boolean} array
	 */
	public static ExtendedList<Boolean> toList(final boolean[] array) {
		return PARSER.callToList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified {@code boolean} array
	 */
	public static ExtendedList<Boolean> asList(final boolean... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Boolean} from the specified {@code boolean}
	 * array.
	 * <p>
	 * @param array the {@code boolean} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Boolean} from the specified {@code boolean}
	 *         array
	 */
	public static ExtendedLinkedList<Boolean> toLinkedList(final boolean[] array) {
		return PARSER.callToLinkedList(toArray(array));
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Boolean} from the specified {@code boolean}
	 * array.
	 * <p>
	 * @param array the {@code boolean} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Boolean} from the specified {@code boolean}
	 *         array
	 */
	public static ExtendedLinkedList<Boolean> asLinkedList(final boolean... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified {@code T} array
	 */
	public static <T> ExtendedList<Boolean> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedList<Boolean> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Boolean} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<Boolean> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Boolean} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedLinkedList<Boolean> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link Boolean} from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link Boolean} from the specified {@link Collection}
	 */
	public static ExtendedList<Boolean> collectionToList(final Collection<?> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link Boolean} from the specified
	 * {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link Boolean} from the specified
	 *         {@link Collection}
	 */
	public static ExtendedLinkedList<Boolean> collectionToLinkedList(
			final Collection<?> collection) {
		return PARSER.callCollectionToLinkedList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified {@code boolean} array
	 */
	public static Set<Boolean> toSet(final boolean[] array) {
		return PARSER.callToSet(toArray(array));
	}

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified {@code boolean} array
	 */
	public static Set<Boolean> asSet(final boolean... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified {@code T} array
	 */
	public static <T> Set<Boolean> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> Set<Boolean> asSet(final T... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link Boolean} from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return a {@link Set} of {@link Boolean} from the specified {@link Collection}
	 */
	public static Set<Boolean> collectionToSet(final Collection<?> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
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
	 * Creates a {@code boolean} array of the specified length with the specified {@code boolean}
	 * element.
	 * <p>
	 * @param element the {@code boolean} element of the {@code boolean} array to create
	 * @param length  the length of the {@code boolean} array to create
	 * <p>
	 * @return a {@code boolean} array of the specified length with the specified {@code boolean}
	 *         element
	 */
	public static boolean[] repeat(final boolean element, final int length) {
		return fill(new boolean[length], element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of elements in the specified 2D {@code boolean} array.
	 * <p>
	 * @param array2D the 2D {@code boolean} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 2D {@code boolean} array
	 */
	public static int count(final boolean[][] array2D) {
		int count = 0;
		if (array2D != null) {
			for (final boolean[] array : array2D) {
				count += array.length;
			}
		}
		return count;
	}

	/**
	 * Returns the number of elements in the specified 3D {@code boolean} array.
	 * <p>
	 * @param array3D the 3D {@code boolean} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 3D {@code boolean} array
	 */
	public static int count(final boolean[][][] array3D) {
		int count = 0;
		if (array3D != null) {
			for (final boolean[][] array2D : array3D) {
				count += count(array2D);
			}
		}
		return count;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code boolean} token in the specified
	 * {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to count from (may be {@code null})
	 * @param token the {@code boolean} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code boolean} token in the specified
	 *         {@code boolean} array
	 */
	public static int count(final boolean[] array, final boolean token) {
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
	 * Returns the number of occurrences of the specified {@code boolean} token in the specified 2D
	 * {@code boolean} array.
	 * <p>
	 * @param array2D the 2D {@code boolean} array to count from (may be {@code null})
	 * @param token   the {@code boolean} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code boolean} token in the specified 2D
	 *         {@code boolean} array
	 */
	public static int count(final boolean[][] array2D, final boolean token) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final boolean[] array : array2D) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code boolean} token in the specified 3D
	 * {@code boolean} array.
	 * <p>
	 * @param array3D the 3D {@code boolean} array to count from (may be {@code null})
	 * @param token   the {@code boolean} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code boolean} token in the specified 3D
	 *         {@code boolean} array
	 */
	public static int count(final boolean[][][] array3D, final boolean token) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final boolean[][] array2D : array3D) {
				occurrenceCount += count(array2D, token);
			}
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code boolean} tokens in the specified
	 * {@code boolean} array.
	 * <p>
	 * @param array  the {@code boolean} array to count from (may be {@code null})
	 * @param tokens the {@code boolean} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code boolean} tokens in the specified
	 *         {@code boolean} array
	 */
	public static int count(final boolean[] array, final boolean... tokens) {
		int occurrenceCount = 0;
		if (array != null && tokens != null) {
			for (final boolean token : tokens) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code boolean} tokens in the specified 2D
	 * {@code boolean} array.
	 * <p>
	 * @param array2D the 2D {@code boolean} array to count from (may be {@code null})
	 * @param tokens  the {@code boolean} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code boolean} tokens in the specified 2D
	 *         {@code boolean} array
	 */
	public static int count(final boolean[][] array2D, final boolean... tokens) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final boolean[] array : array2D) {
				occurrenceCount += count(array, tokens);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code boolean} tokens in the specified 3D
	 * {@code boolean} array.
	 * <p>
	 * @param array3D the 3D {@code boolean} array to count from (may be {@code null})
	 * @param tokens  the {@code boolean} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code boolean} tokens in the specified 3D
	 *         {@code boolean} array
	 */
	public static int count(final boolean[][][] array3D, final boolean... tokens) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final boolean[][] array2D : array3D) {
				occurrenceCount += count(array2D, tokens);
			}
		}
		return occurrenceCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean[] fill(final boolean[] array, final boolean value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
		return array;
	}

	public static boolean[][] fill(final boolean[][] array2D, final boolean value) {
		for (final boolean[] array : array2D) {
			fill(array, value);
		}
		return array2D;
	}

	public static boolean[][][] fill(final boolean[][][] array3D, final boolean value) {
		for (final boolean[][] array2D : array3D) {
			fill(array2D, value);
		}
		return array3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code boolean} array containing all the elements of the specified {@code boolean}
	 * array at the specified indices.
	 * <p>
	 * @param array   the {@code boolean} array to filter from
	 * @param indices the indices to filter
	 * <p>
	 * @return a {@code boolean} array containing all the elements of the specified {@code boolean}
	 *         array at the specified indices
	 */
	public static boolean[] filter(final boolean[] array, final int... indices) {
		final boolean[] filteredArray = new boolean[indices.length];
		for (int i = 0; i < indices.length; ++i) {
			filteredArray[i] = array[indices[i]];
		}
		return filteredArray;
	}

	/**
	 * Returns a 2D {@code boolean} array containing all the elements of the specified
	 * {@code boolean} array at all the specified indices.
	 * <p>
	 * @param array   the {@code boolean} array to filter from
	 * @param indices the array of indices to filter
	 * <p>
	 * @return a 2D {@code boolean} array containing all the elements of the specified
	 *         {@code boolean} array at all the specified indices
	 */
	public static boolean[][] filterAll(final boolean[] array, final int[]... indices) {
		final boolean[][] filteredArrays = new boolean[indices.length][];
		for (int i = 0; i < indices.length; ++i) {
			filteredArrays[i] = filter(array, indices[i]);
		}
		return filteredArrays;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code boolean} array containing the specified {@code boolean} value and all the
	 * elements of the specified {@code boolean} array.
	 * <p>
	 * @param a a {@code boolean} value
	 * @param b another {@code boolean} array (may be {@code null})
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
	 * @param b another {@code boolean} array (may be {@code null})
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
		final boolean[] mergedArray = new boolean[a.length + b.length];
		System.arraycopy(a, 0, mergedArray, 0, a.length);
		System.arraycopy(b, 0, mergedArray, a.length, b.length);
		return mergedArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void reverse(final boolean... array) {
		reverse(array, 0, array.length);
	}

	public static void reverse(final boolean[] array, final int fromIndex, final int toIndex) {
		final int limit = Integers.middle(toIndex - fromIndex);
		for (int i = 0; i < limit; ++i) {
			swap(array, fromIndex + i, toIndex - 1 - i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shuffles the specified {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to shuffle
	 */
	public static void shuffle(final boolean... array) {
		shuffle(array, 0, array.length);
	}

	/**
	 * Shuffles the specified {@code boolean} array between the specified indices.
	 * <p>
	 * @param array     the {@code boolean} array to shuffle
	 * @param fromIndex the index to start shuffling from (inclusive)
	 * @param toIndex   the index to finish shuffling at (exclusive)
	 */
	public static void shuffle(final boolean[] array, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			swap(array, i, Integers.random(fromIndex, toIndex));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void swap(final boolean[] array, final int i, final int j) {
		final boolean element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean[] take(final boolean[] array, final int fromIndex, final int length) {
		final int maxLength = Math.min(length, array.length - fromIndex);
		final boolean[] subarray = new boolean[maxLength];
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	//////////////////////////////////////////////

	public static boolean[] take(final boolean[]... array2D) {
		return take(array2D, 0, array2D.length, 0, array2D[0].length);
	}

	public static boolean[] take(final boolean[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0, array2D[0].length);
	}

	public static boolean[] take(final boolean[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final boolean[] subarray = new boolean[maxRowCount * columnCount];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array2D[i], fromColumn, columnCount), 0, subarray,
					i * columnCount, columnCount);
		}
		return subarray;
	}

	//////////////////////////////////////////////

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
		final boolean[] subarray = new boolean[maxRowCount * length];
		for (int i = fromRow; i < maxRowCount; ++i) {
			System.arraycopy(take(array3D[i], fromColumn, columnCount, fromDepth, depthCount), 0,
					subarray, i * length, length);
		}
		return subarray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the transpose of the specified {@code boolean} array.
	 * <p>
	 * @param rowCount the number of rows of the {@code boolean} array
	 * @param array    a {@code boolean} array
	 * <p>
	 * @return the transpose of the specified {@code boolean} array
	 */
	public static boolean[] transpose(final int rowCount, final boolean... array) {
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
	 * @param array2D the 2D {@code boolean} array to convert
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
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int findFirstIndex(final boolean[] array, final boolean token) {
		if (array != null) {
			return findFirstIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final boolean[] array, final boolean token, final int from) {
		if (array != null) {
			return findFirstIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findFirstIndex(final boolean[] array, final boolean token, final int from,
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

	public static int findLastIndex(final boolean[] array, final boolean token) {
		if (array != null) {
			return findLastIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final boolean[] array, final boolean token, final int from) {
		if (array != null) {
			return findLastIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static int findLastIndex(final boolean[] array, final boolean token, final int from,
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
	 * Tests whether the specified {@link Object} is an instance of {@link Boolean}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Boolean},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Boolean;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code boolean} value or a
	 * {@link Boolean}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code boolean} value
	 *         or a {@link Boolean}, {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
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
	public static boolean isPrimitiveFrom(final Class<?> c) {
		return boolean.class.isAssignableFrom(c);
	}

	/**
	 * Tests whether the specified {@link Object} is an instance of {@code boolean} array.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@code boolean} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArray(final Object object) {
		return object instanceof boolean[];
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@code boolean} array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@code boolean} array,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitiveArrayFrom(final Class<?> c) {
		return boolean[].class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code boolean} array is {@code null} or empty.
	 * <p>
	 * @param array the {@code boolean} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code boolean} array is {@code null} or empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final boolean[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code boolean} array is non-{@code null} and empty.
	 * <p>
	 * @param array the {@code boolean} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code boolean} array is non-{@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final boolean[] array) {
		return array != null && array.length == 0;
	}

	/**
	 * Tests whether the specified {@code boolean} array is non-{@code null} and non-empty.
	 * <p>
	 * @param array the {@code boolean} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code boolean} array is non-{@code null} and
	 *         non-empty, {@code false} otherwise
	 */
	public static boolean isNonEmpty(final boolean[] array) {
		return array != null && array.length > 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code boolean} array contains the specified {@code boolean}
	 * token.
	 * <p>
	 * @param array the {@code boolean} array to test (may be {@code null})
	 * @param token the {@code boolean} token to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code boolean} array contains the specified
	 *         {@code boolean} token, {@code false} otherwise
	 */
	public static boolean contains(final boolean[] array, final boolean token) {
		return findFirstIndex(array, token) >= 0;
	}

	/**
	 * Tests whether the specified {@code boolean} array contains any of the specified
	 * {@code boolean} tokens.
	 * <p>
	 * @param array  the {@code boolean} array to test (may be {@code null})
	 * @param tokens the {@code boolean} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code boolean} array contains any of the specified
	 *         {@code boolean} tokens, {@code false} otherwise
	 */
	public static boolean containsAny(final boolean[] array, final boolean... tokens) {
		if (array != null) {
			for (final boolean token : tokens) {
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
	 * Returns a clone of the specified {@code boolean} array, or {@code null} if it is
	 * {@code null}.
	 * <p>
	 * @param array the {@code boolean} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified {@code boolean} array, or {@code null} if it is {@code null}
	 */
	public static boolean[] clone(final boolean... array) {
		// Check the arguments
		if (array == null) {
			return null;
		}

		// Clone the array
		return array.clone();
	}

	/**
	 * Clones the specified 2D {@code boolean} array.
	 * <p>
	 * @param array2D the 2D {@code boolean} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified 2D {@code boolean} array, or {@code null} if it is
	 *         {@code null}
	 */
	public static boolean[][] clone(final boolean[]... array2D) {
		// Check the arguments
		if (array2D == null) {
			return null;
		}

		// Clone the 2D array
		final boolean[][] clone = new boolean[array2D.length]
				[array2D.length > 0 ? array2D[0].length : 0];
		for (int i = 0; i < array2D.length; ++i) {
			clone[i] = clone(array2D[i]);
		}
		return clone;
	}

	/**
	 * Clones the specified 3D {@code boolean} array.
	 * <p>
	 * @param array3D the 3D {@code boolean} array to clone (may be {@code null})
	 * <p>
	 * @return a clone of the specified 3D {@code boolean} array, or {@code null} if it is
	 *         {@code null}
	 */
	public static boolean[][][] clone(final boolean[][][] array3D) {
		// Check the arguments
		if (array3D == null) {
			return null;
		}

		// Clone the 3D array
		final boolean[][][] clone = new boolean[array3D.length]
				[array3D.length > 0 ? array3D[0].length : 0]
				[array3D[0].length > 0 ? array3D[0][0].length : 0];
		for (int i = 0; i < array3D.length; ++i) {
			clone[i] = clone(array3D[i]);
		}
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the hash code value for the specified {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code boolean} array
	 */
	public static int hashCode(final boolean... array) {
		return hashCodeWith(0, array);
	}

	/**
	 * Returns the hash code value for the specified {@code boolean} array at the specified depth.
	 * <p>
	 * @param depth the depth to hash at
	 * @param array the {@code boolean} array to hash (may be {@code null})
	 * <p>
	 * @return the hash code value for the specified {@code boolean} array at the specified depth
	 */
	public static int hashCodeWith(final int depth, final boolean... array) {
		if (array == null) {
			return 0;
		}
		switch (array.length) {
			case 0:
				return Bits.SEEDS[depth % Bits.SEEDS.length];
			case 1:
				return array[0] ? 1231 : 1237;
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
	 * Returns a representative {@link String} of the specified {@code boolean} array.
	 * <p>
	 * @param array the {@code boolean} array to convert
	 * <p>
	 * @return a representative {@link String} of the specified {@code boolean} array
	 */
	public static String toString(final boolean... array) {
		return Arrays.toString(toArray(array));
	}

	/**
	 * Returns a representative {@link String} of the specified {@code boolean} array joined by the
	 * specified {@code char} delimiter.
	 * <p>
	 * @param array     a {@code boolean} array
	 * @param delimiter the {@code char} delimiter
	 * <p>
	 * @return a representative {@link String} of the specified {@code boolean} array joined by the
	 *         specified {@code char} delimiter
	 */
	public static String toStringWith(final boolean[] array, final char delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code boolean} array joined by the
	 * specified delimiting {@link String}.
	 * <p>
	 * @param array     a {@code boolean} array
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@code boolean} array joined by the
	 *         specified delimiting {@link String}
	 */
	public static String toStringWith(final boolean[] array, final String delimiter) {
		return Arrays.toStringWith(toArray(array), delimiter);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code boolean} array wrapped by
	 * {@code wrapper}.
	 * <p>
	 * @param array   a {@code boolean} array
	 * @param wrapper an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code boolean} array wrapped by
	 *         {@code wrapper}
	 */
	public static String toStringWith(final boolean[] array, final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified {@code boolean} array joined by the
	 * specified delimiting {@link String} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     a {@code boolean} array
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@code boolean} array joined by the
	 *         specified delimiting {@link String} and wrapped by {@code wrapper}
	 */
	public static String toStringWith(final boolean[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Arrays.toStringWith(toArray(array), delimiter, wrapper);
	}
}
