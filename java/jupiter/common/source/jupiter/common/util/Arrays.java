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

import static jupiter.common.util.Strings.NULL;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import jupiter.common.exception.IllegalClassException;
import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.math.Comparables;
import jupiter.common.struct.list.ComparableSort;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.list.Sort;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.struct.tuple.Triple;
import jupiter.common.test.ArrayArguments;

public class Arrays {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@code char} delimiter.
	 */
	public static final char DELIMITER = ',';


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Arrays}.
	 */
	protected Arrays() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> Class<?> getComponentClass(final T[] array) {
		return Classes.get(array).getComponentType();
	}

	public static <T> Class<?> getComponentClass2D(final T[] array) {
		return getComponentClass(array).getComponentType();
	}

	public static <T> Class<?> getComponentClass3D(final T[] array) {
		return getComponentClass2D(array).getComponentType();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the element {@link Class} of the specified array, or {@code null} if it is empty or
	 * contains only {@code null} elements.
	 * <p>
	 * @param array an array of {@link Object} (may be {@code null})
	 * <p>
	 * @return the element {@link Class} of the specified array, or {@code null} if it is empty or
	 *         contains only {@code null} elements
	 */
	public static Class<?> getElementClass(final Object... array) {
		// Check the arguments
		if (isNullOrEmpty(array)) {
			return null;
		}

		// Get the element class of the array (common ancestor of the classes)
		Class<?> c = Classes.get(array[0]);
		for (int i = 1; i < array.length; ++i) {
			c = Classes.getCommonAncestor(c, Classes.get(array[i]));
		}
		return c;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array converted from the specified primitive array.
	 * <p>
	 * @param primitiveArray the primitive array to convert
	 * <p>
	 * @return an array converted from the specified primitive array
	 */
	public static Object[] toArray(final Object primitiveArray) {
		// Check the arguments
		ArrayArguments.requireArray(primitiveArray);

		// Convert the primitive array to an array
		if (Booleans.isPrimitiveArray(primitiveArray)) {
			return Booleans.toArray((boolean[]) primitiveArray);
		} else if (Characters.isPrimitiveArray(primitiveArray)) {
			return Characters.toArray((char[]) primitiveArray);
		} else if (Bytes.isPrimitiveArray(primitiveArray)) {
			return Bytes.toArray((byte[]) primitiveArray);
		} else if (Shorts.isPrimitiveArray(primitiveArray)) {
			return Shorts.toArray((short[]) primitiveArray);
		} else if (Integers.isPrimitiveArray(primitiveArray)) {
			return Integers.toArray((int[]) primitiveArray);
		} else if (Longs.isPrimitiveArray(primitiveArray)) {
			return Longs.toArray((long[]) primitiveArray);
		} else if (Floats.isPrimitiveArray(primitiveArray)) {
			return Floats.toArray((float[]) primitiveArray);
		} else if (Doubles.isPrimitiveArray(primitiveArray)) {
			return Doubles.toArray((double[]) primitiveArray);
		}
		return (Object[]) primitiveArray;
	}

	/**
	 * Returns a primitive array converted from the specified array, or {@code null} if the array is
	 * {@code null}.
	 * <p>
	 * @param array the array to convert (may be {@code null})
	 * <p>
	 * @return a primitive array converted from the specified array, or {@code null} if the array is
	 *         {@code null}
	 */
	public static Object toPrimitiveArray(final Object[] array) {
		if (array == null) {
			return null;
		}
		return toPrimitiveArray(getComponentClass(array), array);
	}

	/**
	 * Returns a primitive array of the specified element {@link Class} converted from the specified
	 * array, or {@code null} if the specified element {@link Class} or array is {@code null}.
	 * <p>
	 * @param c     the element {@link Class} of the array to convert (may be {@code null})
	 * @param array the array to convert (may be {@code null})
	 * <p>
	 * @return a primitive array of the specified element {@link Class} converted from the specified
	 *         array, or {@code null} if the specified element {@link Class} or array is
	 *         {@code null}
	 */
	public static Object toPrimitiveArray(final Class<?> c, final Object[] array) {
		// Check the arguments
		if (c == null || array == null) {
			return null;
		}

		// Convert the array to a primitive array
		if (Booleans.isFrom(c)) {
			return Booleans.toPrimitiveArray(array);
		} else if (Characters.isFrom(c)) {
			return Characters.toPrimitiveArray(array);
		} else if (Bytes.isFrom(c)) {
			return Bytes.toPrimitiveArray(array);
		} else if (Shorts.isFrom(c)) {
			return Shorts.toPrimitiveArray(array);
		} else if (Integers.isFrom(c)) {
			return Integers.toPrimitiveArray(array);
		} else if (Longs.isFrom(c)) {
			return Longs.toPrimitiveArray(array);
		} else if (Floats.isFrom(c)) {
			return Floats.toPrimitiveArray(array);
		} else if (Doubles.isFrom(c)) {
			return Doubles.toPrimitiveArray(array);
		}
		throw new IllegalClassException(c);
	}

	//////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(final T[] array) {
		return toArray(getComponentClass(array), array);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(final Class<?> c, final T[] array) {
		final T[] output = (T[]) create(c, array.length);
		System.arraycopy(array, 0, output, 0, array.length);
		return output;
	}

	//////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static <T> T[][] toArray2D(final T[][] array2D) {
		return toArray2D(getComponentClass2D(array2D), array2D);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[][] toArray2D(final Class<?> c, final T[][] array2D) {
		final int rowCount = array2D.length;
		final int columnCount = array2D[0].length;
		final T[][] output2D = (T[][]) create(c, rowCount, columnCount);
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, output2D, i * columnCount, columnCount);
		}
		return output2D;
	}

	//////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static <T> T[][][] toArray3D(final T[][][] array3D) {
		return toArray3D(getComponentClass3D(array3D), array3D);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[][][] toArray3D(final Class<?> c, final T[][][] array3D) {
		final int rowCount = array3D.length;
		final int columnCount = array3D[0].length;
		final int depthCount = array3D[0][0].length;
		final T[][][] output3D = (T[][][]) create(c, rowCount, columnCount, depthCount);
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < rowCount; ++j) {
				System.arraycopy(array3D[i][j], 0, output3D, (i * columnCount + j) * depthCount,
						depthCount);
			}
		}
		return output3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> ExtendedList<T> toList(final T[] array) {
		final ExtendedList<T> list = new ExtendedList<T>(array.length);
		list.addAll(array);
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> ExtendedList<T> asList(final T... array) {
		return toList(array);
	}

	public static <T> ExtendedLinkedList<T> toLinkedList(final T[] array) {
		final ExtendedLinkedList<T> linkedList = new ExtendedLinkedList<T>();
		for (final T element : array) {
			linkedList.add(element);
		}
		return linkedList;
	}

	@SuppressWarnings("unchecked")
	public static <T> ExtendedLinkedList<T> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> Set<T> toSet(final T[] array) {
		final Set<T> set = new HashSet<T>(array.length);
		for (final T element : array) {
			set.add(element);
		}
		return set;
	}

	@SuppressWarnings("unchecked")
	public static <T> Set<T> asSet(final T... array) {
		return toSet(array);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static <T> T[] create(final Class<T> c, final int length) {
		return (T[]) Array.newInstance(c, length);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[][] create(final Class<T> c, final int rowCount, final int columnCount) {
		final T[] array = create(c, columnCount);
		final T[][] array2D = (T[][]) Array.newInstance(Classes.get(array), rowCount);
		if (columnCount > 0) {
			array2D[0] = array;
			for (int i = 1; i < rowCount; ++i) {
				array2D[i] = create(c, columnCount);
			}
		}
		return array2D;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[][][] create(final Class<T> c, final int rowCount, final int columnCount,
			final int depthCount) {
		final T[][] array2D = create(c, columnCount, depthCount);
		final T[][][] array3D = (T[][][]) Array.newInstance(Classes.get(array2D), rowCount);
		if (depthCount > 0) {
			array3D[0] = array2D;
			for (int i = 1; i < depthCount; ++i) {
				array3D[i] = create(c, rowCount, columnCount);
			}
		}
		return array3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array, or {@code "null"} if it is
	 * {@code null}.
	 * <p>
	 * @param array the array of {@link Object} to join (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified array, or {@code "null"} if it is
	 *         {@code null}
	 */
	public static String join(final Object... array) {
		return Strings.joinWith(array, DELIMITER);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code T} array of the specified length with the specified {@code T} element.
	 * <p>
	 * @param <T>     the component type of the array to create
	 * @param element the {@code T} element of the {@code T} array to create
	 * @param length  the length of the {@code T} array to create
	 * <p>
	 * @return a {@code T} array of the specified length with the specified {@code T} element
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] repeat(final T element, final int length) {
		return fill((T[]) create(Classes.get(element), length), element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of elements in the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the 2D array to count from
	 * @param array2D the 2D {@code T} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 2D {@code T} array
	 */
	public static <T> int count(final T[][] array2D) {
		int count = 0;
		if (array2D != null) {
			for (final T[] array : array2D) {
				count += array.length;
			}
		}
		return count;
	}

	/**
	 * Returns the number of elements in the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the 3D array to count from
	 * @param array3D the 3D {@code T} array to count from (may be {@code null})
	 * <p>
	 * @return the number of elements in the specified 3D {@code T} array
	 */
	public static <T> int count(final T[][][] array3D) {
		int count = 0;
		if (array3D != null) {
			for (final T[][] array2D : array3D) {
				count += count(array2D);
			}
		}
		return count;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code T} token in the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to count from
	 * @param array the {@code T} array to count from (may be {@code null})
	 * @param token the {@code T} token to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code T} token in the specified {@code T}
	 *         array
	 */
	public static <T> int count(final T[] array, final T token) {
		int occurrenceCount = 0;
		if (array != null && token != null) {
			int index = -1;
			while ((index = findFirstIndex(array, token, index + 1)) >= 0) {
				++occurrenceCount;
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code T} token in the specified 2D
	 * {@code T} array.
	 * <p>
	 * @param <T>     the component type of the 2D array to count from
	 * @param array2D the 2D {@code T} array to count from (may be {@code null})
	 * @param token   the {@code T} token to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code T} token in the specified 2D
	 *         {@code T} array
	 */
	public static <T> int count(final T[][] array2D, final T token) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final T[] array : array2D) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code T} token in the specified 3D
	 * {@code T} array.
	 * <p>
	 * @param <T>     the component type of the 3D array to count from
	 * @param array3D the 3D {@code T} array to count from (may be {@code null})
	 * @param token   the {@code T} token to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code T} token in the specified 3D
	 *         {@code T} array
	 */
	public static <T> int count(final T[][][] array3D, final T token) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final T[][] array2D : array3D) {
				occurrenceCount += count(array2D, token);
			}
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code T} tokens in the specified
	 * {@code T} array.
	 * <p>
	 * @param <T>    the component type of the array to count from
	 * @param array  the {@code T} array to count from (may be {@code null})
	 * @param tokens the {@code T} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code T} tokens in the specified
	 *         {@code T} array
	 */
	public static <T> int count(final T[] array, final T[] tokens) {
		int occurrenceCount = 0;
		if (array != null && tokens != null) {
			for (final T token : tokens) {
				occurrenceCount += count(array, token);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code T} tokens in the specified 2D
	 * {@code T} array.
	 * <p>
	 * @param <T>     the component type of the 2D array to count from
	 * @param array2D the 2D {@code T} array to count from (may be {@code null})
	 * @param tokens  the {@code T} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code T} tokens in the specified 2D
	 *         {@code T} array
	 */
	public static <T> int count(final T[][] array2D, final T[] tokens) {
		int occurrenceCount = 0;
		if (array2D != null) {
			for (final T[] array : array2D) {
				occurrenceCount += count(array, tokens);
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code T} tokens in the specified 3D
	 * {@code T} array.
	 * <p>
	 * @param <T>     the component type of the 3D array to count from
	 * @param array3D the 3D {@code T} array to count from (may be {@code null})
	 * @param tokens  the {@code T} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code T} tokens in the specified 3D
	 *         {@code T} array
	 */
	public static <T> int count(final T[][][] array3D, final T[] tokens) {
		int occurrenceCount = 0;
		if (array3D != null) {
			for (final T[][] array2D : array3D) {
				occurrenceCount += count(array2D, tokens);
			}
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of {@code null} elements in the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to count from
	 * @param array the {@code T} array to count from (may be {@code null})
	 * <p>
	 * @return the number of {@code null} elements in the specified {@code T} array
	 */
	public static <T> int countNull(final T[] array) {
		return count(array, null);
	}

	/**
	 * Returns the number of {@code null} elements in the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the 2D array to count from
	 * @param array2D the 2D {@code T} array to count from (may be {@code null})
	 * <p>
	 * @return the number of {@code null} elements in the specified 2D {@code T} array
	 */
	public static <T> int countNull(final T[][] array2D) {
		return count(array2D, null);
	}

	/**
	 * Returns the number of {@code null} elements in the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the 3D array to count from
	 * @param array3D the 3D {@code T} array to count from (may be {@code null})
	 * <p>
	 * @return the number of {@code null} elements in the specified 3D {@code T} array.
	 */
	public static <T> int countNull(final T[][][] array3D) {
		return count(array3D, null);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of non-{@code null} elements in the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to count from
	 * @param array the {@code T} array to count from (may be {@code null})
	 * <p>
	 * @return the number of non-{@code null} elements in the specified {@code T} array
	 */
	public static <T> int countNonNull(final T[] array) {
		return array == null ? 0 : array.length - countNull(array);
	}

	/**
	 * Returns the number of non-{@code null} elements in the specified 2D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the 2D array to count from
	 * @param array2D the 2D {@code T} array to count from (may be {@code null})
	 * <p>
	 * @return the number of non-{@code null} elements in the specified 2D {@code T} array
	 */
	public static <T> int countNonNull(final T[][] array2D) {
		return array2D == null ? 0 : count(array2D) - countNull(array2D);
	}

	/**
	 * Returns the number of non-{@code null} elements in the specified 3D {@code T} array.
	 * <p>
	 * @param <T>     the component type of the 3D array to count from
	 * @param array3D the 3D {@code T} array to count from (may be {@code null})
	 * <p>
	 * @return the number of non-{@code null} elements in the specified 3D {@code T} array.
	 */
	public static <T> int countNonNull(final T[][][] array3D) {
		return array3D == null ? 0 : count(array3D) - countNull(array3D);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> T[] fill(final T[] array, final T object) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = object;
		}
		return array;
	}

	public static <T> T[][] fill(final T[][] array2D, final T object) {
		for (final T[] array : array2D) {
			fill(array, object);
		}
		return array2D;
	}

	public static <T> T[][][] fill(final T[][][] array3D, final T object) {
		for (final T[][] array2D : array3D) {
			fill(array2D, object);
		}
		return array3D;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code T} array containing all the elements of the specified {@code T} array at the
	 * specified indexes.
	 * <p>
	 * @param <T>     the component type of the array to filter from
	 * @param array   the {@code T} array to filter from
	 * @param indexes the indexes to filter
	 * <p>
	 * @return a {@code T} array containing all the elements of the specified {@code T} array at the
	 *         specified indexes
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] filter(final T[] array, final int... indexes) {
		final T[] filteredArray = (T[]) create(getComponentClass(array), indexes.length);
		for (int i = 0; i < indexes.length; ++i) {
			filteredArray[i] = array[indexes[i]];
		}
		return filteredArray;
	}

	/**
	 * Returns a 2D {@code T} array containing all the elements of the specified {@code T} array at
	 * all the specified indexes.
	 * <p>
	 * @param <T>     the component type of the array to filter from
	 * @param array   the {@code T} array to filter from
	 * @param indexes the array of indexes to filter
	 * <p>
	 * @return a 2D {@code T} array containing all the elements of the specified {@code T} array at
	 *         all the specified indexes
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[][] filterAll(final T[] array, final int[]... indexes) {
		final T[][] filteredArrays = (T[][]) create(Classes.get(array), indexes.length);
		for (int i = 0; i < indexes.length; ++i) {
			filteredArrays[i] = filter(array, indexes[i]);
		}
		return filteredArrays;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code T} array containing all the elements of the specified {@code T} arrays.
	 * <p>
	 * @param <T> the component type of the arrays to merge
	 * @param a   the {@code T} array to merge (may be {@code null})
	 * @param b   the other {@code T} array to merge with (may be {@code null})
	 * <p>
	 * @return a {@code T} array containing all the elements of the specified {@code T} arrays
	 * <p>
	 * @throws IllegalArgumentException if the type of {@code a} is neither the same as, nor is a
	 *                                  superclass or superinterface of, the type of {@code b}
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] merge(final T[] a, final T[] b) {
		if (a == null) {
			return b != null ? toArray(b) : null;
		} else if (b == null) {
			return toArray(a);
		}
		final Class<?> c = getComponentClass(a);
		final T[] mergedArray = (T[]) create(c, a.length + b.length);
		System.arraycopy(a, 0, mergedArray, 0, a.length);
		try {
			System.arraycopy(b, 0, mergedArray, a.length, b.length);
		} catch (final ArrayStoreException ex) {
			ArrayArguments.requireAssignableFrom(c, getComponentClass(b));
			throw ex;
		}
		return mergedArray;
	}

	/**
	 * Returns a {@code T} array containing all the elements of the specified {@code T} arrays.
	 * <p>
	 * @param <T>    the component type of the arrays to merge
	 * @param arrays the {@code T} arrays to merge (may be {@code null})
	 * <p>
	 * @return a {@code T} array containing all the elements of the specified {@code T} arrays
	 * <p>
	 * @throws IllegalArgumentException if the type of any {@code arrays} is neither the same as,
	 *                                  nor is a superclass or superinterface of, the type of any
	 *                                  other {@code arrays}
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] merge(final T[]... arrays) {
		// Check the arguments
		if (arrays == null) {
			return null;
		} else if (arrays.length == 1) {
			return toArray(getComponentClass2D(arrays), arrays[0]);
		}

		// Merge the arrays
		final Class<?> c = getComponentClass2D(arrays);
		final T[] mergedArray = (T[]) create(c, count(arrays));
		int offset = 0;
		for (final T[] array : arrays) {
			try {
				System.arraycopy(array, 0, mergedArray, offset, array.length);
			} catch (final ArrayStoreException ex) {
				ArrayArguments.requireAssignableFrom(c, getComponentClass2D(array));
				throw ex;
			}
			offset += array.length;
		}
		return mergedArray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shuffles the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to shuffle
	 * @param array the {@code T} array to shuffle
	 */
	public static <T> void shuffle(final T[] array) {
		shuffle(array, 0, array.length);
	}

	/**
	 * Shuffles the specified {@code T} array from the specified index.
	 * <p>
	 * @param <T>       the component type of the array to shuffle
	 * @param array     the {@code T} array to shuffle
	 * @param fromIndex the index to start shuffling from (inclusive)
	 */
	public static <T> void shuffle(final T[] array, final int fromIndex) {
		shuffle(array, fromIndex, array.length);
	}

	/**
	 * Shuffles the specified {@code T} array between the specified indexes.
	 * <p>
	 * @param <T>       the component type of the array to shuffle
	 * @param array     the {@code T} array to shuffle
	 * @param fromIndex the index to start shuffling from (inclusive)
	 * @param toIndex   the index to finish shuffling at (exclusive)
	 */
	public static <T> void shuffle(final T[] array, final int fromIndex, final int toIndex) {
		for (int i = fromIndex; i < toIndex; ++i) {
			swap(array, i, Integers.random(fromIndex, toIndex));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sorts the specified array into ascending order, according to the
	 * {@linkplain Comparable natural ordering} of its elements. All elements in the array must
	 * implement the {@link Comparable} interface. Furthermore, all elements in the array must be
	 * <i>mutually comparable</i> (that is, {@code e1.compareTo(e2)} must not throw a
	 * {@link ClassCastException} for any elements {@code e1} and {@code e2} in the array).
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort.
	 * <p>
	 * Implementation note: This implementation is a stable, adaptive, iterative merge sort that
	 * requires far fewer than {@code n log n} comparisons when the input array is partially sorted,
	 * while offering the performance of a traditional merge sort when the input array is randomly
	 * ordered. If the input array is nearly sorted, the implementation requires approximately
	 * {@code n} comparisons. Temporary storage requirements vary from a small constant for nearly
	 * sorted input arrays to {@code n / 2} object references for randomly ordered input arrays.
	 * <p>
	 * The implementation takes equal advantage of ascending and descending order in its input array
	 * and can take advantage of ascending and descending order in different parts of the same input
	 * array. It is well-suited to merging two or more sorted arrays: simply concatenate the arrays
	 * and sort the resulting array.
	 * <p>
	 * The implementation was adapted from Tim Peters's list sort for Python
	 * (<a href="http://svn.python.org/projects/python/trunk/Objects/listsort.txt">
	 * TimSort</a>). It uses techniques from Peter McIlroy's "Optimistic Sorting and Information
	 * Theoretic Complexity", in Proceedings of the Fourth Annual ACM-SIAM Symposium on Discrete
	 * Algorithms, pp 467-474, January 1993.
	 * <p>
	 * @param array the array to sort
	 * <p>
	 * @throws ClassCastException       if {@code array} contains elements that are not mutually
	 *                                  comparable (for example, a {@link String} and an
	 *                                  {@link Integer})
	 * @throws IllegalArgumentException (optional) if the natural ordering of the {@code array}
	 *                                  elements is found to violate the {@link Comparable} contract
	 */
	public static void sort(final Object[] array) {
		ComparableSort.sort(array, 0, array.length, null, 0, 0);
	}

	/**
	 * Sorts the specified array into ascending order, according to the
	 * {@linkplain Comparable natural ordering} of its elements from index {@code fromIndex},
	 * inclusive. All elements in the array must implement the {@link Comparable} interface.
	 * Furthermore, all elements in the array must be <i>mutually comparable</i> (that is,
	 * {@code e1.compareTo(e2)} must not throw a {@link ClassCastException} for any elements
	 * {@code e1} and {@code e2} in the array).
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort.
	 * <p>
	 * Implementation note: This implementation is a stable, adaptive, iterative merge sort that
	 * requires far fewer than {@code n log n} comparisons when the input array is partially sorted,
	 * while offering the performance of a traditional merge sort when the input array is randomly
	 * ordered. If the input array is nearly sorted, the implementation requires approximately
	 * {@code n} comparisons. Temporary storage requirements vary from a small constant for nearly
	 * sorted input arrays to {@code n / 2} object references for randomly ordered input arrays.
	 * <p>
	 * The implementation takes equal advantage of ascending and descending order in its input array
	 * and can take advantage of ascending and descending order in different parts of the same input
	 * array. It is well-suited to merging two or more sorted arrays: simply concatenate the arrays
	 * and sort the resulting array.
	 * <p>
	 * The implementation was adapted from Tim Peters's list sort for Python
	 * (<a href="http://svn.python.org/projects/python/trunk/Objects/listsort.txt">
	 * TimSort</a>). It uses techniques from Peter McIlroy's "Optimistic Sorting and Information
	 * Theoretic Complexity", in Proceedings of the Fourth Annual ACM-SIAM Symposium on Discrete
	 * Algorithms, pp 467-474, January 1993.
	 * <p>
	 * @param array     the array to sort
	 * @param fromIndex the index of the first element to sort (inclusive)
	 * <p>
	 * @throws ClassCastException       if {@code array} contains elements that are not mutually
	 *                                  comparable (for example, a {@link String} and an
	 *                                  {@link Integer})
	 * @throws IllegalArgumentException if {@code fromIndex} is out of bounds or (optional) if the
	 *                                  natural ordering of the {@code array} elements is found to
	 *                                  violate the {@link Comparable} contract
	 */
	public static void sort(final Object[] array, final int fromIndex) {
		// Check the arguments
		ArrayArguments.requireIndex(fromIndex, array.length);

		// Sort the array
		ComparableSort.sort(array, fromIndex, array.length, null, 0, 0);
	}

	/**
	 * Sorts the specified range of the specified array into ascending order, according to the
	 * {@linkplain Comparable natural ordering} of its elements. The range to sort extends from
	 * index {@code fromIndex}, inclusive, to index {@code toIndex}, exclusive. (If
	 * {@code fromIndex==toIndex}, the range to sort is empty.) All elements in this range must
	 * implement the {@link Comparable} interface. Furthermore, all elements in this range must be
	 * <i>mutually comparable</i> (that is, {@code e1.compareTo(e2)} must not throw a
	 * {@link ClassCastException} for any elements {@code e1} and {@code e2} in the array).
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort.
	 * <p>
	 * Implementation note: This implementation is a stable, adaptive, iterative merge sort that
	 * requires far fewer than {@code n log n} comparisons when the input array is partially sorted,
	 * while offering the performance of a traditional merge sort when the input array is randomly
	 * ordered. If the input array is nearly sorted, the implementation requires approximately
	 * {@code n} comparisons. Temporary storage requirements vary from a small constant for nearly
	 * sorted input arrays to {@code n / 2} object references for randomly ordered input arrays.
	 * <p>
	 * The implementation takes equal advantage of ascending and descending order in its input array
	 * and can take advantage of ascending and descending order in different parts of the same input
	 * array. It is well-suited to merging two or more sorted arrays: simply concatenate the arrays
	 * and sort the resulting array.
	 * <p>
	 * The implementation was adapted from Tim Peters's list sort for Python
	 * (<a href="http://svn.python.org/projects/python/trunk/Objects/listsort.txt">
	 * TimSort</a>). It uses techniques from Peter McIlroy's "Optimistic Sorting and Information
	 * Theoretic Complexity", in Proceedings of the Fourth Annual ACM-SIAM Symposium on Discrete
	 * Algorithms, pp 467-474, January 1993.
	 * <p>
	 * @param array     the array to sort
	 * @param fromIndex the index of the first element to sort (inclusive)
	 * @param toIndex   the index of the last element to sort (exclusive)
	 * <p>
	 * @throws ClassCastException       if {@code array} contains elements that are not mutually
	 *                                  comparable (for example, a {@link String} and an
	 *                                  {@link Integer})
	 * @throws IllegalArgumentException if {@code fromIndex} or {@code toIndex} is out of bounds or
	 *                                  (optional) if the natural ordering of the {@code array}
	 *                                  elements is found to violate the {@link Comparable} contract
	 */
	public static void sort(final Object[] array, final int fromIndex, final int toIndex) {
		// Check the arguments
		ArrayArguments.requireIndex(fromIndex, array.length);
		ArrayArguments.requireIndex(toIndex, array.length, true);

		// Sort the array
		ComparableSort.sort(array, fromIndex, toIndex, null, 0, 0);
	}

	//////////////////////////////////////////////

	/**
	 * Sorts the specified array according to the order induced by the specified {@link Comparator}.
	 * All elements in the array must be <i>mutually comparable</i> by the specified
	 * {@link Comparator} (that is, {@code c.compare(e1, e2)} must not throw a
	 * {@link ClassCastException} for any elements {@code e1} and {@code e2} in the array).
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort.
	 * <p>
	 * Implementation note: This implementation is a stable, adaptive, iterative merge sort that
	 * requires far fewer than {@code n log n} comparisons when the input array is partially sorted,
	 * while offering the performance of a traditional merge sort when the input array is randomly
	 * ordered. If the input array is nearly sorted, the implementation requires approximately
	 * {@code n} comparisons. Temporary storage requirements vary from a small constant for nearly
	 * sorted input arrays to {@code n / 2} object references for randomly ordered input arrays.
	 * <p>
	 * The implementation takes equal advantage of ascending and descending order in its input array
	 * and can take advantage of ascending and descending order in different parts of the same input
	 * array. It is well-suited to merging two or more sorted arrays: simply concatenate the arrays
	 * and sort the resulting array.
	 * <p>
	 * The implementation was adapted from Tim Peters's list sort for Python
	 * (<a href="http://svn.python.org/projects/python/trunk/Objects/listsort.txt">
	 * TimSort</a>). It uses techniques from Peter McIlroy's "Optimistic Sorting and Information
	 * Theoretic Complexity", in Proceedings of the Fourth Annual ACM-SIAM Symposium on Discrete
	 * Algorithms, pp 467-474, January 1993.
	 * <p>
	 * @param <T>        the component type of the array to sort
	 * @param array      the {@code T} array to sort
	 * @param comparator the {@link Comparator} of {@code T} supertype to determine the order
	 *                   ({@code null} indicates that {@linkplain Comparable natural ordering} of
	 *                   the elements should be used)
	 * <p>
	 * @throws ClassCastException       if {@code array} contains elements that are not mutually
	 *                                  comparable using {@code comparator}
	 * @throws IllegalArgumentException (optional) if {@code comparator} is found to violate the
	 *                                  {@link Comparator} contract
	 */
	public static <T> void sort(final T[] array, final Comparator<? super T> comparator) {
		if (comparator == null) {
			sort(array);
		} else {
			Sort.<T>sort(array, 0, array.length, comparator, null, 0, 0);
		}
	}

	/**
	 * Sorts the specified range of the specified array according to the order induced by the
	 * specified {@link Comparator}. The range to sort extends from index {@code fromIndex},
	 * inclusive, to index {@code toIndex}, exclusive. (If {@code fromIndex==toIndex}, the range to
	 * be sorted is empty.) All elements in the range must be <i>mutually comparable</i> by the
	 * specified {@link Comparator} (that is, {@code c.compare(e1, e2)} must not throw a
	 * {@link ClassCastException} for any elements {@code e1} and {@code e2} in the range).
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort.
	 * <p>
	 * Implementation note: This implementation is a stable, adaptive, iterative merge sort that
	 * requires far fewer than {@code n log n} comparisons when the input array is partially sorted,
	 * while offering the performance of a traditional merge sort when the input array is randomly
	 * ordered. If the input array is nearly sorted, the implementation requires approximately
	 * {@code n} comparisons. Temporary storage requirements vary from a small constant for nearly
	 * sorted input arrays to {@code n / 2} object references for randomly ordered input arrays.
	 * <p>
	 * The implementation takes equal advantage of ascending and descending order in its input array
	 * and can take advantage of ascending and descending order in different parts of the same input
	 * array. It is well-suited to merging two or more sorted arrays: simply concatenate the arrays
	 * and sort the resulting array.
	 * <p>
	 * The implementation was adapted from Tim Peters's list sort for Python
	 * (<a href="http://svn.python.org/projects/python/trunk/Objects/listsort.txt">
	 * TimSort</a>). It uses techniques from Peter McIlroy's "Optimistic Sorting and Information
	 * Theoretic Complexity", in Proceedings of the Fourth Annual ACM-SIAM Symposium on Discrete
	 * Algorithms, pp 467-474, January 1993.
	 * <p>
	 * @param <T>        the component type of the array to sort
	 * @param array      the {@code T} array to sort
	 * @param fromIndex  the index of the first element to sort (inclusive)
	 * @param toIndex    the index of the last element to sort (exclusive)
	 * @param comparator the {@link Comparator} of {@code T} supertype to determine the order
	 *                   ({@code null} indicates that {@linkplain Comparable natural ordering} of
	 *                   the elements should be used)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code fromIndex < 0} or {@code toIndex > a.length}
	 * @throws ClassCastException             if {@code array} contains elements that are not
	 *                                        mutually comparable using {@code comparator}
	 * @throws IllegalArgumentException       if {@code fromIndex > toIndex} or (optional) if
	 *                                        {@code comparator} is found to violate the
	 *                                        {@link Comparator} contract
	 */
	public static <T> void sort(final T[] array, final int fromIndex, final int toIndex,
			final Comparator<? super T> comparator) {
		if (comparator == null) {
			sort(array, fromIndex, toIndex);
		} else {
			Sort.<T>sort(array, fromIndex, toIndex, comparator, null, 0, 0);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> void reverse(final T[] array) {
		reverse(array, 0, array.length - 1);
	}

	public static <T> void reverse(final T[] array, final int fromIndex) {
		reverse(array, fromIndex, array.length - 1);
	}

	public static <T> void reverse(final T[] array, final int fromIndex, final int toIndex) {
		final int limit = Integers.middleUp(toIndex - fromIndex);
		for (int i = 0; i < limit; ++i) {
			swap(array, fromIndex + i, toIndex - i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> void swap(final T[] array, final int i, final int j) {
		final T element = array[i];
		array[i] = array[j];
		array[j] = element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> T[] take(final T[] array) {
		return take(array, 0);
	}

	public static <T> T[] take(final T[] array, final int fromIndex) {
		return take(array, fromIndex, array.length);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] take(final T[] array, final int fromIndex, final int length) {
		final int maxLength = Math.min(length, array.length - fromIndex);
		final T[] subarray = (T[]) create(getComponentClass(array), maxLength);
		System.arraycopy(array, fromIndex, subarray, 0, maxLength);
		return subarray;
	}

	//////////////////////////////////////////////

	public static <T> T[] take(final T[][] array2D) {
		return take(array2D, 0);
	}

	public static <T> T[] take(final T[][] array2D, final int fromRow) {
		return take(array2D, fromRow, array2D.length);
	}

	public static <T> T[] take(final T[][] array2D, final int fromRow, final int rowCount) {
		return take(array2D, fromRow, rowCount, 0);
	}

	public static <T> T[] take(final T[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn) {
		return take(array2D, fromRow, rowCount, fromColumn, array2D[0].length);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] take(final T[][] array2D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		final int maxRowCount = Math.min(rowCount, array2D.length - fromRow);
		final int maxColumnCount = Math.min(columnCount, array2D[0].length - fromColumn);
		final T[] subarray = (T[]) create(getComponentClass2D(array2D),
				maxRowCount * maxColumnCount);
		for (int i = 0; i < maxRowCount; ++i) {
			System.arraycopy(array2D[fromRow + i], fromColumn, subarray, i * maxColumnCount,
					maxColumnCount);
		}
		return subarray;
	}

	//////////////////////////////////////////////

	public static <T> T[] take(final T[][][] array3D) {
		return take(array3D, 0);
	}

	public static <T> T[] take(final T[][][] array3D, final int fromRow) {
		return take(array3D, fromRow, array3D.length);
	}

	public static <T> T[] take(final T[][][] array3D, final int fromRow, final int rowCount) {
		return take(array3D, fromRow, rowCount, 0);
	}

	public static <T> T[] take(final T[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn) {
		return take(array3D, fromRow, rowCount, fromColumn, array3D[0].length);
	}

	public static <T> T[] take(final T[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, 0);
	}

	public static <T> T[] take(final T[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth) {
		return take(array3D, fromRow, rowCount, fromColumn, columnCount, fromDepth,
				array3D[0][0].length);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] take(final T[][][] array3D, final int fromRow, final int rowCount,
			final int fromColumn, final int columnCount, final int fromDepth,
			final int depthCount) {
		final int maxRowCount = Math.min(rowCount, array3D.length - fromRow);
		final int maxColumnCount = Math.min(columnCount, array3D[0].length - fromColumn);
		final int maxDepthCount = Math.min(depthCount, array3D[0][0].length - fromDepth);
		final T[] subarray = (T[]) create(getComponentClass3D(array3D),
				maxRowCount * maxColumnCount * maxDepthCount);
		for (int i = 0; i < maxRowCount; ++i) {
			for (int j = 0; j < maxColumnCount; ++j) {
				System.arraycopy(array3D[fromRow + i][fromColumn + j], fromDepth, subarray,
						(i * maxColumnCount + j) * maxDepthCount, maxDepthCount);
			}
		}
		return subarray;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> T[][] transpose(final Class<T> c, final T[][] array2D) {
		final int m = array2D[0].length;
		final int n = array2D.length;
		final T[][] transpose = create(c, m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				transpose[i][j] = array2D[j][i];
			}
		}
		return transpose;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@code T} array containing all the distinct elements of the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array to filter from
	 * @param array the {@code T} array to filter from
	 * <p>
	 * @return a {@code T} array containing all the distinct elements of the specified {@code T}
	 *         array
	 */
	public static <T> T[] unique(final T[] array) {
		final HashSet<T> set = new HashSet<T>();
		Sets.addAll(set, array);
		return Sets.toArray(set);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> int findFirstIndex(final T[] array, final T token) {
		if (array != null) {
			return findFirstIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static <T> int findFirstIndex(final T[] array, final T token, final int from) {
		if (array != null) {
			return findFirstIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static <T> int findFirstIndex(final T[] array, final T token, final int from,
			final int to) {
		if (array != null) {
			for (int i = from; i < to; ++i) {
				if (Objects.equals(array[i], token)) {
					return i;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	public static <T> Pair<Integer, Integer> findFirstIndex(final T[][] array2D, final T token) {
		if (array2D != null) {
			for (int i = 0; i < array2D.length; ++i) {
				final int index = findFirstIndex(array2D[i], token);
				if (index > 0) {
					return new Pair<Integer, Integer>(i, index);
				}
			}
		}
		return null;
	}

	public static <T> Triple<Integer, Integer, Integer> findFirstIndex(final T[][][] array3D,
			final T token) {
		if (array3D != null) {
			for (int i = 0; i < array3D.length; ++i) {
				final Pair<Integer, Integer> index = findFirstIndex(array3D[i], token);
				if (index != null) {
					return new Triple<Integer, Integer, Integer>(i, index.getFirst(),
							index.getSecond());
				}
			}
		}
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> int findLastIndex(final T[] array, final T token) {
		if (array != null) {
			return findLastIndex(array, token, 0, array.length);
		}
		return -1;
	}

	public static <T> int findLastIndex(final T[] array, final T token, final int from) {
		if (array != null) {
			return findLastIndex(array, token, from, array.length);
		}
		return -1;
	}

	public static <T> int findLastIndex(final T[] array, final T token, final int from,
			final int to) {
		if (array != null) {
			for (int i = to - 1; i >= from; --i) {
				if (Objects.equals(array[i], token)) {
					return i;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	public static <T> Pair<Integer, Integer> findLastIndex(final T[][] array2D, final T token) {
		if (array2D != null) {
			for (int i = array2D.length - 1; i >= 0; --i) {
				final int index = findLastIndex(array2D[i], token);
				if (index > 0) {
					return new Pair<Integer, Integer>(i, index);
				}
			}
		}
		return null;
	}

	public static <T> Triple<Integer, Integer, Integer> findLastIndex(final T[][][] array3D,
			final T token) {
		if (array3D != null) {
			for (int i = array3D.length - 1; i >= 0; --i) {
				final Pair<Integer, Integer> index = findFirstIndex(array3D[i], token);
				if (index != null) {
					return new Triple<Integer, Integer, Integer>(i, index.getFirst(),
							index.getSecond());
				}
			}
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of array.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of array, {@code false}
	 *         otherwise
	 */
	public static boolean is(final Object object) {
		return isFrom(Classes.get(object));
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to an array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to an array, {@code false}
	 *         otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return c.isArray();
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code T} array is {@code null} or empty.
	 * <p>
	 * @param <T>   the component type of the array to test
	 * @param array the {@code T} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code T} array is {@code null} or empty, {@code false}
	 *         otherwise
	 */
	public static <T> boolean isNullOrEmpty(final T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code T} array is non-{@code null} and empty.
	 * <p>
	 * @param <T>   the component type of the array to test
	 * @param array the {@code T} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code T} array is non-{@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static <T> boolean isEmpty(final T[] array) {
		return array != null && array.length == 0;
	}

	/**
	 * Tests whether the specified {@code T} array is non-{@code null} and non-empty.
	 * <p>
	 * @param <T>   the component type of the array to test
	 * @param array the {@code T} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code T} array is non-{@code null} and non-empty,
	 *         {@code false} otherwise
	 */
	public static <T> boolean isNonEmpty(final T[] array) {
		return array != null && array.length > 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified index is in the bounds of the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to test against
	 * @param index the index to test
	 * @param array the {@code T} array to test against
	 * <p>
	 * @return {@code true} if the specified index is in the bounds of the specified {@code T}
	 *         array, {@code false} otherwise
	 */
	public static <T> boolean isBetween(final int index, final T[] array) {
		return isBetween(index, array.length);
	}

	/**
	 * Tests whether the specified index is in the bounds of the specified array length.
	 * <p>
	 * @param index  the index to test
	 * @param length the array length to test against
	 * <p>
	 * @return {@code true} if the specified index is in the bounds of the specified array length,
	 *         {@code false} otherwise
	 */
	public static boolean isBetween(final int index, final int length) {
		return Integers.isBetween(index, 0, length);
	}

	/**
	 * Tests whether the specified index is in the bounds of the specified {@code T} array.
	 * <p>
	 * @param <T>              the component type of the array to test against
	 * @param index            the index to test
	 * @param array            the {@code T} array to test against
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified index is in the bounds of the specified {@code T}
	 *         array, {@code false} otherwise
	 */
	public static <T> boolean isBetween(final int index, final T[] array,
			final boolean isUpperInclusive) {
		return isBetween(index, array.length, isUpperInclusive);
	}

	/**
	 * Tests whether the specified index is in the bounds of the specified array length.
	 * <p>
	 * @param index            the index to test
	 * @param length           the array length to test against
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified index is in the bounds of the specified array length,
	 *         {@code false} otherwise
	 */
	public static boolean isBetween(final int index, final int length,
			final boolean isUpperInclusive) {
		return Integers.isBetween(index, 0, length, isUpperInclusive);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code T} array is between the specified lower and upper bound
	 * {@code T} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param <T>   the component type of the arrays to test
	 * @param array the {@code T} array to test (may be {@code null})
	 * @param from  the lower bound {@code T} array to test against (inclusive) (may be
	 *              {@code null})
	 * @param to    the upper bound {@code T} array to test against (exclusive) (may be
	 *              {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code T} array is between the specified lower and
	 *         upper bound {@code T} arrays, {@code false} otherwise
	 */
	public static <T> boolean isBetween(final T[] array, final T[] from, final T[] to) {
		return isBetween(array, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@code T} array is between the specified lower and upper bound
	 * {@code T} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param <T>              the component type of the arrays to test
	 * @param array            the {@code T} array to test (may be {@code null})
	 * @param from             the lower bound {@code T} array to test against (inclusive) (may be
	 *                         {@code null})
	 * @param to               the upper bound {@code T} array to test against (may be {@code null})
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code T} array is between the specified lower and
	 *         upper bound {@code T} arrays, {@code false} otherwise
	 */
	public static <T> boolean isBetween(final T[] array, final T[] from, final T[] to,
			final boolean isUpperInclusive) {
		return isBetween(array, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code T} array is between the specified lower and upper bound
	 * {@code T} arrays (with {@code null} considered as the minimum value).
	 * <p>
	 * @param <T>              the component type of the arrays to test
	 * @param array            the {@code T} array to test (may be {@code null})
	 * @param from             the lower bound {@code T} array to test against (may be {@code null})
	 * @param to               the upper bound {@code T} array to test against (may be {@code null})
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code T} array is between the specified lower and
	 *         upper bound {@code T} arrays, {@code false} otherwise
	 */
	public static <T> boolean isBetween(final T[] array, final T[] from, final T[] to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return isBetween(array, from, to, Comparables.createCastComparator(), isLowerInclusive,
				isUpperInclusive);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code T} array is between the specified lower and upper bound
	 * {@code T} arrays using the specified {@link Comparator} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param <T>        the component type of the arrays to test
	 * @param array      the {@code T} array to test (may be {@code null})
	 * @param from       the lower bound {@code T} array to test against (inclusive) (may be
	 *                   {@code null})
	 * @param to         the upper bound {@code T} array to test against (exclusive) (may be
	 *                   {@code null})
	 * @param comparator the {@link Comparator} of {@code T} supertype to determine the order
	 * <p>
	 * @return {@code true} if the specified {@code T} array is between the specified lower and
	 *         upper bound {@code T} arrays using the specified {@link Comparator}, {@code false}
	 *         otherwise
	 */
	public static <T> boolean isBetween(final T[] array, final T[] from, final T[] to,
			final Comparator<? super T> comparator) {
		return isBetween(array, from, to, comparator, true, false);
	}

	/**
	 * Tests whether the specified {@code T} array is between the specified lower and upper bound
	 * {@code T} arrays using the specified {@link Comparator} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param <T>              the component type of the arrays to test
	 * @param array            the {@code T} array to test (may be {@code null})
	 * @param from             the lower bound {@code T} array to test against (inclusive) (may be
	 *                         {@code null})
	 * @param to               the upper bound {@code T} array to test against (may be {@code null})
	 * @param comparator       the {@link Comparator} of {@code T} supertype to determine the order
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code T} array is between the specified lower and
	 *         upper bound {@code T} arrays using the specified {@link Comparator}, {@code false}
	 *         otherwise
	 */
	public static <T> boolean isBetween(final T[] array, final T[] from, final T[] to,
			final Comparator<? super T> comparator, final boolean isUpperInclusive) {
		return isBetween(array, from, to, comparator, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@code T} array is between the specified lower and upper bound
	 * {@code T} arrays using the specified {@link Comparator} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param <T>              the component type of the arrays to test
	 * @param array            the {@code T} array to test (may be {@code null})
	 * @param from             the lower bound {@code T} array to test against (may be {@code null})
	 * @param to               the upper bound {@code T} array to test against (may be {@code null})
	 * @param comparator       the {@link Comparator} of {@code T} supertype to determine the order
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@code T} array is between the specified lower and
	 *         upper bound {@code T} arrays using the specified {@link Comparator}, {@code false}
	 *         otherwise
	 */
	public static <T> boolean isBetween(final T[] array, final T[] from, final T[] to,
			final Comparator<? super T> comparator, final boolean isLowerInclusive,
			final boolean isUpperInclusive) {
		return (isLowerInclusive ?
						compare(array, from, comparator) >= 0 :
						compare(array, from, comparator) > 0) &&
				(isUpperInclusive ?
						compare(array, to, comparator) <= 0 :
						compare(array, to, comparator) < 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code T} array has any element different from {@code null}.
	 * <p>
	 * @param <T>   the component type of the array to test
	 * @param array the {@code T} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@code T} array has any element different from
	 *         {@code null}, {@code false} otherwise
	 */
	public static <T> boolean hasAnyValue(final T[] array) {
		if (array != null) {
			for (final T element : array) {
				if (element != null) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tests whether the specified 2D {@code T} array has any element different from {@code null}.
	 * <p>
	 * @param <T>     the component type of the 2D array to test
	 * @param array2D the 2D {@code T} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified 2D {@code T} array has any element different from
	 *         {@code null}, {@code false} otherwise
	 */
	public static <T> boolean hasAnyValue(final T[][] array2D) {
		if (array2D != null) {
			for (final T[] array : array2D) {
				if (hasAnyValue(array)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tests whether the specified 3D {@code T} array has any element different from {@code null}.
	 * <p>
	 * @param <T>     the component type of the 3D array to test
	 * @param array3D the 3D {@code T} array to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified 3D {@code T} array has any element different from
	 *         {@code null}, {@code false} otherwise
	 */
	public static <T> boolean hasAnyValue(final T[][][] array3D) {
		if (array3D != null) {
			for (final T[][] array2D : array3D) {
				if (hasAnyValue(array2D)) {
					return true;
				}
			}
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code T} array contains the specified {@code T} token.
	 * <p>
	 * @param <T>   the component type of the array to test
	 * @param array the {@code T} array to test (may be {@code null})
	 * @param token the {@code T} token to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code T} array contains the specified {@code T} token,
	 *         {@code false} otherwise
	 */
	public static <T> boolean contains(final T[] array, final T token) {
		return findFirstIndex(array, token) >= 0;
	}

	/**
	 * Tests whether the specified {@code T} array contains any of the specified {@code T} tokens.
	 * <p>
	 * @param <T>    the component type of the array to test
	 * @param array  the {@code T} array to test (may be {@code null})
	 * @param tokens the {@code T} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@code T} array contains any of the specified {@code T}
	 *         tokens, {@code false} otherwise
	 */
	public static <T> boolean containsAny(final T[] array, final T[] tokens) {
		if (array != null) {
			for (final T token : tokens) {
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
	 * Compares the specified arrays of {@link Comparable} for order. Returns a negative integer,
	 * {@code 0} or a positive integer as {@code a} is less than, equal to or greater than {@code b}
	 * (with {@code null} considered as the minimum value).
	 * <p>
	 * @param <T> the self {@link Comparable} component type of the arrays to compare
	 * @param a   the array of {@link Comparable} of {@code T} type to compare (may be {@code null})
	 * @param b   the other array of {@link Comparable} of {@code T} type to compare against (may be
	 *            {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static <T extends Comparable<? super T>> int compare(final T[] a, final T[] b) {
		return compare(a, b, Comparables.<T>createComparator());
	}

	/**
	 * Compares the specified {@code T} arrays for order. Returns a negative integer, {@code 0} or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param <T> the component type of the arrays to compare
	 * @param a   the {@code T} array to compare (may be {@code null})
	 * @param b   the other {@code T} array to compare against (may be {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static <T> int compareCast(final T[] a, final T[] b) {
		return compare(a, b, Comparables.<T>createCastComparator());
	}

	/**
	 * Compares the specified arrays for order using the specified {@link Comparator}. Returns a
	 * negative integer, {@code 0} or a positive integer as {@code a} is less than, equal to or
	 * greater than {@code b} (with {@code null} considered as the minimum value).
	 * <p>
	 * @param <T>        the component type of the arrays to compare for order
	 * @param a          the {@code T} array to compare for order (may be {@code null})
	 * @param b          the other {@code T} array to compare against for order (may be
	 *                   {@code null})
	 * @param comparator the {@link Comparator} of {@code T} supertype to determine the order
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static <T> int compare(final T[] a, final T[] b,
			final Comparator<? super T> comparator) {
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

		// Compare the arrays for order using the comparator
		final int limit = Math.min(a.length, b.length);
		for (int i = 0; i < limit; ++i) {
			final int comparison = comparator.compare(a[i], b[i]);
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
	 * Creates a copy of the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to clone
	 * @param array the {@code T} array to clone (may be {@code null})
	 * <p>
	 * @return a copy of the specified {@code T} array
	 * <p>
	 * @throws CloneNotSupportedException if the {@code T} type does not implement {@link Cloneable}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] clone(final T[] array)
			throws CloneNotSupportedException {
		// Check the arguments
		if (array == null) {
			return null;
		}

		// Clone the array
		final T[] clone = (T[]) create(getComponentClass(array), array.length);
		for (int i = 0; i < array.length; ++i) {
			clone[i] = Objects.clone(array[i]);
		}
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array, or {@code "null"} if it is
	 * {@code null}.
	 * <p>
	 * @param array an array of {@link Object} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified array, or {@code "null"} if it is
	 *         {@code null}
	 */
	public static String toString(final Object... array) {
		if (array == null) {
			return NULL;
		}
		return Strings.parenthesize(join(array));
	}

	/**
	 * Returns a representative {@link String} of the specified array joined by the specified
	 * delimiting {@link String}, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param array     an array of {@link Object} (may be {@code null})
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array joined by the specified
	 *         delimiting {@link String}, or {@code "null"} if it is {@code null}
	 */
	public static String toStringWith(final Object[] array, final String delimiter) {
		if (array == null) {
			return NULL;
		}
		return Strings.parenthesize(Strings.joinWith(array, delimiter));
	}

	/**
	 * Returns a representative {@link String} of the specified array wrapped by {@code wrapper}, or
	 * {@code "null"} if it is {@code null}.
	 * <p>
	 * @param array   an array of {@link Object} (may be {@code null})
	 * @param wrapper an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array wrapped by {@code wrapper}, or
	 *         {@code "null"} if it is {@code null}
	 */
	public static String toStringWith(final Object[] array, final ObjectToStringMapper wrapper) {
		if (array == null) {
			return NULL;
		}
		return Strings.parenthesize(Strings.joinWith(array, wrapper));
	}

	/**
	 * Returns a representative {@link String} of the specified array joined by the specified
	 * delimiting {@link String} and wrapped by {@code wrapper}, or {@code "null"} if it is
	 * {@code null}.
	 * <p>
	 * @param array     an array of {@link Object} (may be {@code null})
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array joined by the specified
	 *         delimiting {@link String} and wrapped by {@code wrapper}, or {@code "null"} if it is
	 *         {@code null}
	 */
	public static String toStringWith(final Object[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		if (array == null) {
			return NULL;
		}
		return Strings.parenthesize(Strings.joinWith(array, delimiter, wrapper));
	}
}
