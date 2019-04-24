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

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.struct.tuple.Triple;
import jupiter.common.test.ArrayArguments;

public class Arrays {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final int DEFAULT_CAPACITY = 10;

	public static final String DEFAULT_DELIMITER = ",";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Arrays() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> T[] toArray(final Class<T> c, final T... array) {
		final T[] result = create(c, array.length);
		System.arraycopy(array, 0, result, 0, array.length);
		return result;
	}

	public static <T> T[][] toArray2D(final Class<T> c, final T[]... array2D) {
		final int rowCount = array2D.length;
		final int columnCount = array2D[0].length;
		final T[][] result = create(c, rowCount, columnCount);
		for (int i = 0; i < rowCount; ++i) {
			System.arraycopy(array2D[i], 0, result, i * columnCount, columnCount);
		}
		return result;
	}

	public static <T> T[][][] toArray3D(final Class<T> c, final T[][]... array3D) {
		final int rowCount = array3D.length;
		final int columnCount = array3D[0].length;
		final int depthCount = array3D[0][0].length;
		final T[][][] result = create(c, rowCount, columnCount, depthCount);
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < rowCount; ++j) {
				System.arraycopy(array3D[i][j], 0, result, (i * columnCount + j) * depthCount,
						depthCount);
			}
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> List<T> toList(final T... array) {
		return toExtendedList(array);
	}

	public static <T> ExtendedList<T> toExtendedList(final T... array) {
		final ExtendedList<T> result = new ExtendedList<T>(array.length);
		for (final T element : array) {
			result.add(element);
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> Set<T> toSet(final T... array) {
		final Set<T> result = new HashSet<T>(array.length);
		for (final T element : array) {
			result.add(element);
		}
		return result;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a clone of the specified array of type {@code T}, or {@code null} if {@code array} is
	 * {@code null}.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array an array of type {@code T} (may be {@code null})
	 * <p>
	 * @return a clone of the specified array of type {@code T}, or {@code null} if {@code array} is
	 *         {@code null}
	 */
	public static <T> T[] clone(final T... array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static <T> T[] create(final Class<T> c, final int length) {
		return (T[]) Array.newInstance(c, length);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[][] create(final Class<T> c, final int rowCount, final int columnCount) {
		final T[] array = create(c, columnCount);
		final T[][] array2D = (T[][]) Array.newInstance(array.getClass(), rowCount);
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
		final T[][][] array3D = (T[][][]) Array.newInstance(array2D.getClass(), rowCount);
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
	 * Returns a {@link String} representation of the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link String} representation of the specified array of type {@code T}
	 */
	public static <T> String join(final T... array) {
		return Strings.joinWith(array, DEFAULT_DELIMITER);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> int count(final T[]... arrays) {
		int result = 0;
		for (int i = 0; i < arrays.length; ++i) {
			result += arrays[i].length;
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> void fill(final T[] array, final T value) {
		for (int i = 0; i < array.length; ++i) {
			array[i] = value;
		}
	}

	public static <T> void fill(final T[][] array2D, final T value) {
		for (final T[] array : array2D) {
			fill(array, value);
		}
	}

	public static <T> void fill(final T[][][] array3D, final T value) {
		for (final T[][] array2D : array3D) {
			fill(array2D, value);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> int indexOf(final T[] array, final T element) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i].equals(element)) {
				return i;
			}
		}
		return -1;
	}

	public static <T> Pair<Integer, Integer> indexOf(final T[][] array2D, final T element) {
		for (int i = 0; i < array2D.length; ++i) {
			final int index = indexOf(array2D[i], element);
			if (index > 0) {
				return new Pair<Integer, Integer>(i, index);
			}
		}
		return null;
	}

	public static <T> Triple<Integer, Integer, Integer> indexOf(final T[][][] array3D,
			final T element) {
		for (int i = 0; i < array3D.length; ++i) {
			final Pair<Integer, Integer> index = indexOf(array3D[i], element);
			if (index != null) {
				return new Triple<Integer, Integer, Integer>(i, index.getFirst(),
						index.getSecond());
			}
		}
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of type {@code T} containing all the elements of the specified arrays of
	 * type {@code T}.
	 * <p>
	 * @param <T> the component type of the arrays
	 * @param a   an array of type {@code T} (may be {@code null})
	 * @param b   an array of type {@code T} (may be {@code null})
	 * <p>
	 * @return an array of type {@code T} containing all the elements of the specified arrays of
	 *         type {@code T}
	 * <p>
	 * @throws IllegalArgumentException if the type of {@code a} is neither the same as, nor is a
	 *                                  superclass or superinterface of, the type of {@code b}
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] merge(final T[] a, final T... b) {
		if (a == null) {
			return clone(b);
		} else if (b == null) {
			return clone(a);
		}
		final Class<?> type = a.getClass().getComponentType();
		final T[] result = (T[]) create(type, a.length + b.length);
		System.arraycopy(a, 0, result, 0, a.length);
		try {
			System.arraycopy(b, 0, result, a.length, b.length);
		} catch (final ArrayStoreException ex) {
			ArrayArguments.requireAssignableFrom(type, b.getClass().getComponentType());
			throw ex;
		}
		return result;
	}

	/**
	 * Returns an array of type {@code T} containing all the elements of the specified arrays of
	 * type {@code T}.
	 * <p>
	 * @param <T>    the component type of the arrays
	 * @param arrays a 2D array of type {@code T} (may be {@code null})
	 * <p>
	 * @return an array of type {@code T} containing all the elements of the specified arrays of
	 *         type {@code T}
	 * <p>
	 * @throws IllegalArgumentException if the type of {@code a} is neither the same as, nor is a
	 *                                  superclass or superinterface of, the type of {@code b}
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] merge(final T[]... arrays) {
		if (arrays == null) {
			return null;
		} else if (arrays.length == 1) {
			return clone(arrays[0]);
		}
		final Class<?> type = arrays.getClass().getComponentType().getComponentType();
		final T[] result = (T[]) create(type, count(arrays));
		int offset = 0;
		for (int i = 0; i < arrays.length; ++i) {
			final T[] array = arrays[i];
			try {
				System.arraycopy(array, 0, result, offset, array.length);
			} catch (final ArrayStoreException ex) {
				ArrayArguments.requireAssignableFrom(type, array.getClass().getComponentType()
						.getComponentType());
				throw ex;
			}
			offset += array.length;
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> T[] take(final T[] array) {
		return take(array, 0);
	}

	public static <T> T[] take(final T[] array, final int from) {
		return take(array, from, array.length);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] take(final T[] array, final int from, final int length) {
		final int maxLength = Math.min(length, array.length - from);
		final T[] result = (T[]) create(array.getClass().getComponentType(), maxLength);
		System.arraycopy(array, from, result, 0, maxLength);
		return result;
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
		final T[] result = (T[]) create(array2D.getClass().getComponentType().getComponentType(),
				maxRowCount * maxColumnCount);
		for (int i = 0; i < maxRowCount; ++i) {
			System.arraycopy(array2D[fromRow + i], fromColumn, result, i * maxColumnCount,
					maxColumnCount);
		}
		return result;
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
		final T[] result = (T[]) create(array3D.getClass().getComponentType().getComponentType()
				.getComponentType(), maxRowCount * maxColumnCount * maxDepthCount);
		for (int i = 0; i < maxRowCount; ++i) {
			for (int j = 0; j < maxColumnCount; ++j) {
				System.arraycopy(array3D[fromRow + i][fromColumn + j], fromDepth, result,
						(i * maxColumnCount + j) * maxDepthCount, maxDepthCount);
			}
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> T[][] transpose(final Class<T> c, final T[]... array2D) {
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
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified array of type {@code T} is empty.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array an array of type {@code T}
	 * <p>
	 * @return {@code true} if the specified array of type {@code T} is empty, {@code false}
	 *         otherwise
	 */
	public static <T> boolean isEmpty(final T... array) {
		for (final T element : array) {
			if (element != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests whether the specified 2D array of type {@code T} is empty.
	 * <p>
	 * @param <T>     the component type of the 2D array to test
	 * @param array2D a 2D array of type {@code T}
	 * <p>
	 * @return {@code true} if the specified 2D array of type {@code T} is empty, {@code false}
	 *         otherwise
	 */
	public static <T> boolean isEmpty(final T[]... array2D) {
		for (final T[] array : array2D) {
			if (!isEmpty(array)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests whether the specified 3D array of type {@code T} is empty.
	 * <p>
	 * @param <T>     the component type of the 3D array to test
	 * @param array3D an 3D array of type {@code T}
	 * <p>
	 * @return {@code true} if the specified 3D array of type {@code T} is empty, {@code false}
	 *         otherwise
	 */
	public static <T> boolean isEmpty(final T[][]... array3D) {
		for (final T[][] array2D : array3D) {
			if (!isEmpty(array2D)) {
				return false;
			}
		}
		return true;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link String} representation of the specified array of type {@code T}
	 */
	public static <T> String toString(final T... array) {
		return Strings.parenthesize(join(array));
	}

	/**
	 * Returns a {@link String} representation of the specified array of type {@code T} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param <T>       the component type of the array
	 * @param array     an array of type {@code T}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified array of type {@code T} joined by
	 *         {@code delimiter}
	 */
	public static <T> String toString(final T[] array, final String delimiter) {
		return Strings.parenthesize(Strings.joinWith(array, delimiter));
	}

	/**
	 * Returns a {@link String} representation of the specified array of type {@code T} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param <T>       the component type of the array
	 * @param array     an array of type {@code T}
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of type {@code T} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static <T> String toString(final T[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Strings.parenthesize(Strings.joinWith(array, delimiter, wrapper));
	}
}
