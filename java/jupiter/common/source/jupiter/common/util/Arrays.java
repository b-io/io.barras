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
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
	 * The default delimiting {@link String}.
	 */
	public static final String DEFAULT_DELIMITER = ",";


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

	public static <T> Class<?> getComponentType(final T[] array) {
		return array.getClass().getComponentType();
	}

	public static <T> Class<?> getComponentType2D(final T[] array) {
		return array.getClass().getComponentType().getComponentType();
	}

	public static <T> Class<?> getComponentType3D(final T[] array) {
		return array.getClass().getComponentType().getComponentType().getComponentType();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Object[] toArray(final Object object) {
		// Check the arguments
		ArrayArguments.requireArray(object);

		// Convert
		final Class<?> c = object.getClass();
		if (Booleans.isPrimitiveArray(c)) {
			return Booleans.toArray((boolean[]) object);
		} else if (Characters.isPrimitiveArray(c)) {
			return Characters.toArray((char[]) object);
		} else if (Bytes.isPrimitiveArray(c)) {
			return Bytes.toArray((byte[]) object);
		} else if (Shorts.isPrimitiveArray(c)) {
			return Shorts.toArray((short[]) object);
		} else if (Integers.isPrimitiveArray(c)) {
			return Integers.toArray((int[]) object);
		} else if (Longs.isPrimitiveArray(c)) {
			return Longs.toArray((long[]) object);
		} else if (Floats.isPrimitiveArray(c)) {
			return Floats.toArray((float[]) object);
		} else if (Doubles.isPrimitiveArray(c)) {
			return Doubles.toArray((double[]) object);
		}
		return (Object[]) object;
	}

	//////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(final T[] array) {
		return toArray(getComponentType(array), array);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(final Class<?> c, final T[] array) {
		final T[] output = (T[]) create(c, array.length);
		System.arraycopy(array, 0, output, 0, array.length);
		return output;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[][] toArray2D(final T[][] array2D) {
		return toArray2D(getComponentType2D(array2D), array2D);
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

	@SuppressWarnings("unchecked")
	public static <T> T[][][] toArray3D(final T[][][] array3D) {
		return toArray3D(getComponentType3D(array3D), array3D);
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

	public static <T> ExtendedList<T> asList(final T... array) {
		return toList(array);
	}

	public static <T> ExtendedLinkedList<T> toLinkedList(final T[] array) {
		final ExtendedLinkedList<T> list = new ExtendedLinkedList<T>();
		for (final T element : array) {
			list.add(element);
		}
		return list;
	}

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
	 * Returns a representative {@link String} of the specified array of {@link Object}.
	 * <p>
	 * @param array an array of {@link Object}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Object}
	 */
	public static String join(final Object... array) {
		return Strings.joinWith(array, DEFAULT_DELIMITER);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code T} token in the specified {@code T}
	 * array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * @param token the {@code T} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code T} token in the specified {@code T}
	 *         array
	 */
	public static <T> int count(final T[] array, final T token) {
		int occurrenceCount = 0, index = 0;
		while ((index = findFirstIndex(array, token, index)) >= 0) {
			++occurrenceCount;
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified {@code T} tokens in the specified
	 * {@code T} array.
	 * <p>
	 * @param <T>    the component type of the array
	 * @param array  a {@code T} array
	 * @param tokens the {@code T} tokens to count
	 * <p>
	 * @return the number of occurrences of the specified {@code T} tokens in the specified
	 *         {@code T} array
	 */
	public static <T> int count(final T[] array, final T[] tokens) {
		int occurrenceCount = 0;
		for (final T token : tokens) {
			occurrenceCount += count(array, token);
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the sum of the lengths of the specified {@code T} arrays.
	 * <p>
	 * @param <T>    the component type of the arrays
	 * @param arrays a 2D {@code T} array
	 * <p>
	 * @return the sum of the lengths of the specified {@code T} arrays
	 */
	public static <T> int countLength(final T[][] arrays) {
		int countLength = 0;
		for (final T[] array : arrays) {
			countLength += array.length;
		}
		return countLength;
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

	/**
	 * Returns the filtered {@code T} array from the specified {@code T} array and indexes.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array   a {@code T} array
	 * @param indexes the indexes to filter
	 * <p>
	 * @return the filtered {@code T} array from the specified {@code T} array and indexes
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] filter(final T[] array, final int... indexes) {
		final T[] filteredArray = (T[]) create(getComponentType(array), indexes.length);
		for (int i = 0; i < indexes.length; ++i) {
			filteredArray[i] = array[indexes[i]];
		}
		return filteredArray;
	}

	/**
	 * Returns all the filtered {@code T} arrays from the specified {@code T} array and indexes.
	 * <p>
	 * @param <T>     the component type of the array
	 * @param array   a {@code T} array
	 * @param indexes the array of indexes to filter
	 * <p>
	 * @return all the filtered {@code T} arrays from the specified {@code T} array and indexes
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[][] filterAll(final T[] array, final int[]... indexes) {
		final T[][] filteredArrays = (T[][]) create(array.getClass(), indexes.length);
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
	 * @param a   a {@code T} array (may be {@code null})
	 * @param b   a {@code T} array (may be {@code null})
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
		final Class<?> c = getComponentType(a);
		final T[] mergedArray = (T[]) create(c, a.length + b.length);
		System.arraycopy(a, 0, mergedArray, 0, a.length);
		try {
			System.arraycopy(b, 0, mergedArray, a.length, b.length);
		} catch (final ArrayStoreException ex) {
			ArrayArguments.requireAssignableFrom(c, getComponentType(b));
			throw ex;
		}
		return mergedArray;
	}

	/**
	 * Returns a {@code T} array containing all the elements of the specified {@code T} arrays.
	 * <p>
	 * @param <T>    the component type of the arrays to merge
	 * @param arrays a 2D {@code T} array (may be {@code null})
	 * <p>
	 * @return a {@code T} array containing all the elements of the specified {@code T} arrays
	 * <p>
	 * @throws IllegalArgumentException if the type of {@code a} is neither the same as, nor is a
	 *                                  superclass or superinterface of, the type of {@code b}
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] merge(final T[]... arrays) {
		if (arrays == null) {
			return null;
		} else if (arrays.length == 1) {
			return toArray(getComponentType2D(arrays), arrays[0]);
		}
		final Class<?> c = getComponentType2D(arrays);
		final T[] mergedArray = (T[]) create(c, countLength(arrays));
		int offset = 0;
		for (final T[] array : arrays) {
			try {
				System.arraycopy(array, 0, mergedArray, offset, array.length);
			} catch (final ArrayStoreException ex) {
				ArrayArguments.requireAssignableFrom(c, getComponentType2D(array));
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
	 * Sorts the specified array of {@link Object} into ascending order, according to the
	 * {@linkplain Comparable natural ordering} of its elements. All elements in the array must
	 * implement the {@link Comparable} interface. Furthermore, all elements in the array must be
	 * <i>mutually comparable</i> (that is, {@code e1.compareTo(e2)} must not throw a
	 * {@link ClassCastException} for any elements {@code e1} and {@code e2} in the array).
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort.
	 * <p>
	 * Implementation note: This implementation is a stable, adaptive, iterative merge sort that
	 * requires far fewer than n lg(n) comparisons when the input array is partially sorted, while
	 * offering the performance of a traditional merge sort when the input array is randomly
	 * ordered. If the input array is nearly sorted, the implementation requires approximately n
	 * comparisons. Temporary storage requirements vary from a small constant for nearly sorted
	 * input arrays to n/2 object references for randomly ordered input arrays.
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
	 * @param array the array of {@link Object} to sort
	 * <p>
	 * @throws ClassCastException       if {@code array} contains elements that are not mutually
	 *                                  comparable (for example, a {@link String} and an
	 *                                  {@link Integer})
	 * @throws IllegalArgumentException (optional) if the natural ordering of the {@code array}
	 *                                  elements is found to violate the {@link Comparable} contract
	 */
	public static void sort(final Object... array) {
		ComparableSort.sort(array, 0, array.length, null, 0, 0);
	}

	/**
	 * Sorts the specified array of {@link Object} into ascending order, according to the
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
	 * requires far fewer than n lg(n) comparisons when the input array is partially sorted, while
	 * offering the performance of a traditional merge sort when the input array is randomly
	 * ordered. If the input array is nearly sorted, the implementation requires approximately n
	 * comparisons. Temporary storage requirements vary from a small constant for nearly sorted
	 * input arrays to n/2 object references for randomly ordered input arrays.
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
	 * @param array     the array of {@link Object} to sort
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

		ComparableSort.sort(array, fromIndex, array.length, null, 0, 0);
	}

	/**
	 * Sorts the specified range of the specified array of {@link Object} into ascending order,
	 * according to the {@linkplain Comparable natural ordering} of its elements. The range to sort
	 * extends from index {@code fromIndex}, inclusive, to index {@code toIndex}, exclusive. (If
	 * {@code fromIndex==toIndex}, the range to sort is empty.) All elements in this range must
	 * implement the {@link Comparable} interface. Furthermore, all elements in this range must be
	 * <i>mutually comparable</i> (that is, {@code e1.compareTo(e2)} must not throw a
	 * {@link ClassCastException} for any elements {@code e1} and {@code e2} in the array).
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort.
	 * <p>
	 * Implementation note: This implementation is a stable, adaptive, iterative merge sort that
	 * requires far fewer than n lg(n) comparisons when the input array is partially sorted, while
	 * offering the performance of a traditional merge sort when the input array is randomly
	 * ordered. If the input array is nearly sorted, the implementation requires approximately n
	 * comparisons. Temporary storage requirements vary from a small constant for nearly sorted
	 * input arrays to n/2 object references for randomly ordered input arrays.
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
	 * @param array     the array of {@link Object} to sort
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

		ComparableSort.sort(array, fromIndex, toIndex, null, 0, 0);
	}

	//////////////////////////////////////////////

	/**
	 * Sorts the specified array of {@link Object} according to the order induced by the specified
	 * {@link Comparator}. All elements in the array must be <i>mutually comparable</i> by the
	 * specified {@link Comparator} (that is, {@code c.compare(e1, e2)} must not throw a
	 * {@link ClassCastException} for any elements {@code e1} and {@code e2} in the array).
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort.
	 * <p>
	 * Implementation note: This implementation is a stable, adaptive, iterative merge sort that
	 * requires far fewer than n lg(n) comparisons when the input array is partially sorted, while
	 * offering the performance of a traditional merge sort when the input array is randomly
	 * ordered. If the input array is nearly sorted, the implementation requires approximately n
	 * comparisons. Temporary storage requirements vary from a small constant for nearly sorted
	 * input arrays to n/2 object references for randomly ordered input arrays.
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
	 * @param comparator the {@link Comparator} of supertype {@code T} to determine the order
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
	 * Sorts the specified range of the specified array of {@link Object} according to the order
	 * induced by the specified {@link Comparator}. The range to sort extends from index
	 * {@code fromIndex}, inclusive, to index {@code toIndex}, exclusive. (If
	 * {@code fromIndex==toIndex}, the range to be sorted is empty.) All elements in the range must
	 * be <i>mutually comparable</i> by the specified {@link Comparator} (that is,
	 * {@code c.compare(e1, e2)} must not throw a {@link ClassCastException} for any elements
	 * {@code e1} and {@code e2} in the range).
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
	 * of the sort.
	 * <p>
	 * Implementation note: This implementation is a stable, adaptive, iterative merge sort that
	 * requires far fewer than n lg(n) comparisons when the input array is partially sorted, while
	 * offering the performance of a traditional merge sort when the input array is randomly
	 * ordered. If the input array is nearly sorted, the implementation requires approximately n
	 * comparisons. Temporary storage requirements vary from a small constant for nearly sorted
	 * input arrays to n/2 object references for randomly ordered input arrays.
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
	 * @param comparator the {@link Comparator} of supertype {@code T} to determine the order
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
		final T[] subarray = (T[]) create(getComponentType(array), maxLength);
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
		final T[] subarray = (T[]) create(getComponentType2D(array2D),
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
		final T[] subarray = (T[]) create(getComponentType3D(array3D),
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
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T> int findFirstIndex(final T[] array, final T token) {
		return findFirstIndex(array, token, 0, array.length);
	}

	public static <T> int findFirstIndex(final T[] array, final T token, final int from) {
		return findFirstIndex(array, token, from, array.length);
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
		return findLastIndex(array, token, 0, array.length);
	}

	public static <T> int findLastIndex(final T[] array, final T token, final int from) {
		return findLastIndex(array, token, from, array.length);
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
	 * Tests whether the specified {@link Class} is assignable to an array.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to an array, {@code false}
	 *         otherwise
	 */
	public static boolean is(final Class<?> c) {
		return c.isArray();
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code T} array is {@code null} or empty.
	 * <p>
	 * @param <T>   the component type of the array to test
	 * @param array the {@code T} array to test
	 * <p>
	 * @return {@code true} if the specified {@code T} array is {@code null} or empty, {@code false}
	 *         otherwise
	 */
	public static <T> boolean isNullOrEmpty(final T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Tests whether the specified {@code T} array is not {@code null} and empty.
	 * <p>
	 * @param <T>   the component type of the array to test
	 * @param array the {@code T} array to test
	 * <p>
	 * @return {@code true} if the specified {@code T} array is not {@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static <T> boolean isEmpty(final T[] array) {
		return array != null && array.length == 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code T} array is between the specified lower and upper bound
	 * {@code T} arrays.
	 * <p>
	 * @param <T>   the component type of the arrays to test
	 * @param array the {@code T} array to test
	 * @param from  the lower bound {@code T} array to test against (inclusive)
	 * @param to    the upper bound {@code T} array to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@code T} array is between the specified lower and
	 *         upper bound {@code T} arrays, {@code false} otherwise
	 */
	public static <T> boolean isBetween(final T[] array, final T[] from, final T[] to) {
		return isBetween(array, from, to, Comparables.createCastComparator());
	}

	/**
	 * Tests whether the specified {@code T} array is between the specified lower and upper bound
	 * {@code T} arrays using the specified {@link Comparator}.
	 * <p>
	 * @param <T>        the component type of the arrays to test
	 * @param array      the {@code T} array to test
	 * @param from       the lower bound {@code T} array to test against (inclusive)
	 * @param to         the upper bound {@code T} array to test against (exclusive)
	 * @param comparator the {@link Comparator} of supertype {@code T} to determine the order
	 * <p>
	 * @return {@code true} if the specified {@code T} array is between the specified lower and
	 *         upper bound {@code T} arrays using the specified {@link Comparator}, {@code false}
	 *         otherwise
	 */
	public static <T> boolean isBetween(final T[] array, final T[] from, final T[] to,
			final Comparator<? super T> comparator) {
		return compare(array, from, comparator) >= 0 && compare(array, to, comparator) < 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@code T} array has any element different from {@code null}.
	 * <p>
	 * @param <T>   the component type of the array to test
	 * @param array the {@code T} array to test
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
	 * @param array2D the 2D {@code T} array to test
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
	 * @param array3D the 3D {@code T} array to test
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
	 * @param array the {@code T} array to test
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
	 * @param array  the {@code T} array to test
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
	 * zero or a positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param <T> the self {@link Comparable} type of the arrays to compare
	 * @param a   the array of {@link Comparable} of type {@code T} to compare
	 * @param b   the other array of {@link Comparable} of type {@code T} to compare against
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T extends Comparable<T>> int compare(final T[] a, final T[] b) {
		return compare(a, b, Comparables.<T>createComparator());
	}

	/**
	 * Compares the specified {@code T} arrays for order. Returns a negative integer, zero or a
	 * positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param <T> the type of the arrays to compare
	 * @param a   the {@code T} array to compare
	 * @param b   the other {@code T} array to compare against
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T> int compareCast(final T[] a, final T[] b) {
		return compare(a, b, Comparables.<T>createCastComparator());
	}

	/**
	 * Compares the specified arrays of {@link Comparable} for order. Returns a negative integer,
	 * zero or a positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param <T>        the component type of the arrays to compare for order
	 * @param a          the {@code T} array to compare for order
	 * @param b          the other {@code T} array to compare against for order
	 * @param comparator the {@link Comparator} of supertype {@code T} to determine the order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 * <p>
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 */
	public static <T> int compare(final T[] a, final T[] b,
			final Comparator<? super T> comparator) {
		if (a == b) {
			return 0;
		}
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
	 * @throws CloneNotSupportedException if the type {@code T} does not implement {@link Cloneable}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] clone(final T[] array)
			throws CloneNotSupportedException {
		if (array == null) {
			return null;
		}
		final T[] clone = (T[]) create(getComponentType(array), array.length);
		for (int i = 0; i < array.length; ++i) {
			clone[i] = Objects.clone(array[i]);
		}
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array of {@link Object}.
	 * <p>
	 * @param array an array of {@link Object}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Object}
	 */
	public static String toString(final Object... array) {
		return Strings.parenthesize(join(array));
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Object} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Object}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Object} joined by
	 *         {@code delimiter}
	 */
	public static String toString(final Object[] array, final String delimiter) {
		return Strings.parenthesize(Strings.joinWith(array, delimiter));
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Object} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Object}
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Object} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Object[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Strings.parenthesize(Strings.joinWith(array, delimiter, wrapper));
	}
}
