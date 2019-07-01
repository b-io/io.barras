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

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.CollectionArguments;


public class Lists
		extends Collections {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Lists}.
	 */
	protected Lists() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T extends Number> ExtendedList<T> getMinElements(final List<T> a,
			final List<T> b) {
		// Check the arguments
		CollectionArguments.<List<T>>requireSameSize(a, b);

		// Get the size
		final int size = a.size();
		// For each index, get the minimum number
		final ExtendedList<T> minElements = new ExtendedList<T>(size);
		for (int i = 0; i < size; ++i) {
			minElements.add(Numbers.<T>getMin(a.get(i), b.get(i)));
		}
		return minElements;
	}

	public static <T extends Number> ExtendedList<T> getMaxElements(final List<T> a,
			final List<T> b) {
		// Check the arguments
		CollectionArguments.<List<T>>requireSameSize(a, b);

		// Get the size
		final int size = a.size();
		// For each index, get the maximum number
		final ExtendedList<T> maxElements = new ExtendedList<T>(size);
		for (int i = 0; i < size; ++i) {
			maxElements.add(Numbers.<T>getMax(a.get(i), b.get(i)));
		}
		return maxElements;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static <T> void sort(final List<T> list) {
		final T[] array = (T[]) list.toArray();
		Arrays.sort(array);
		final ListIterator<T> iterator = list.listIterator();
		for (final T element : array) {
			iterator.next();
			iterator.set(element);
		}
	}

	/**
	 * Sorts the specified {@link List} with the specified {@link Comparator}.
	 * <p>
	 * @param <T>        the type of the {@link List} to sort
	 * @param list       the {@link List} of type {@code T} to sort
	 * @param comparator the {@link Comparator} of super type {@code T} to determine the order (a
	 *                   {@code null} value indicates that {@linkplain Comparable natural ordering}
	 *                   of the elements should be used)
	 * <p>
	 * @throws ClassCastException       if the array contains elements that are not mutually
	 *                                  comparable using the specified comparator
	 * @throws IllegalArgumentException (optional) if the comparator is found to violate the
	 *                                  {@link Comparator} contract
	 */
	@SuppressWarnings("unchecked")
	public static <T> void sort(final List<T> list, final Comparator<? super T> comparator) {
		final T[] array = (T[]) list.toArray();
		Arrays.<T>sort(array, comparator);
		final ListIterator<T> iterator = list.listIterator();
		for (final T element : array) {
			iterator.next();
			iterator.set(element);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link List}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link List},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return List.class.isAssignableFrom(c);
	}
}
