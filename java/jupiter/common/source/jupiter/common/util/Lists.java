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
import java.util.Iterator;
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
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code E} array containing all of the elements in the specified {@link List} in
	 * proper sequence (from first to last element).
	 * <p>
	 * @param <E>  the component type of the array
	 * @param list a {@link List} of type {@code E}
	 * <p>
	 * @return an {@code E} array containing all of the elements in the specified {@link List} in
	 *         proper sequence (from first to last element)
	 *
	 * @see List#toArray
	 */
	@SuppressWarnings("unchecked")
	public static <E> E[] toArray(final List<E> list) {
		return (E[]) list.toArray();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <E, T extends E> boolean addAll(final List<E> list, final T[] array) {
		return list.addAll(java.util.Arrays.asList(array));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <E extends Number> ExtendedList<E> getMinElements(final List<E> a,
			final List<E> b) {
		// Check the arguments
		CollectionArguments.<List<E>>requireSameSize(a, b);

		// For each index, get the minimum number
		final ExtendedList<E> minElements = new ExtendedList<E>(a.size());
		final Iterator<E> aIterator = a.iterator();
		final Iterator<E> bIterator = b.iterator();
		while (aIterator.hasNext() && bIterator.hasNext()) {
			minElements.add(Numbers.<E>getMin(aIterator.next(), bIterator.next()));
		}
		return minElements;
	}

	public static <E extends Number> ExtendedList<E> getMaxElements(final List<E> a,
			final List<E> b) {
		// Check the arguments
		CollectionArguments.<List<E>>requireSameSize(a, b);

		// For each index, get the maximum number
		final ExtendedList<E> maxElements = new ExtendedList<E>(a.size());
		final Iterator<E> aIterator = a.iterator();
		final Iterator<E> bIterator = b.iterator();
		while (aIterator.hasNext() && bIterator.hasNext()) {
			maxElements.add(Numbers.<E>getMax(aIterator.next(), bIterator.next()));
		}
		return maxElements;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the first occurrence of the specified {@link Object} from the specified {@link List}
	 * and returns the index of the removed element, or {@code -1} if it is not present.
	 * <p>
	 * @param <L>    the type extending {@link List}
	 * @param <E>    the type of the {@link List}
	 * @param list   a {@link List} of type {@code E}
	 * @param object the {@link Object} to remove
	 * <p>
	 * @return the index of the removed element, or {@code -1} if it is not present
	 */
	public static <L extends List<E>, E> int removeFirst(final L list, final Object object) {
		final Iterator<E> iterator = list.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			if (Objects.equals(iterator.next(), object)) {
				iterator.remove();
				return index;
			}
			++index;
		}
		return -1;
	}

	/**
	 * Removes all the occurrences of the specified {@link Object} from the specified {@link List}
	 * and returns the indexes of the removed elements.
	 * <p>
	 * @param <L>    the type extending {@link List}
	 * @param <E>    the type of the {@link List}
	 * @param list   a {@link List} of type {@code E}
	 * @param object the {@link Object} to remove
	 * <p>
	 * @return the number of removed elements
	 */
	public static <L extends List<E>, E> int[] removeAll(final L list,
			final Object object) {
		final ExtendedList<Integer> indexes = new ExtendedList<Integer>();
		final Iterator<E> iterator = list.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			if (Objects.equals(iterator.next(), object)) {
				iterator.remove();
				indexes.add(index);
				++index;
			}
		}
		return Integers.collectionToPrimitiveArray(indexes);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static <E> void sort(final List<E> list) {
		final E[] array = (E[]) list.toArray();
		Arrays.sort(array);
		final ListIterator<E> iterator = list.listIterator();
		for (final E element : array) {
			iterator.next();
			iterator.set(element);
		}
	}

	/**
	 * Sorts the specified {@link List} with the specified {@link Comparator}.
	 * <p>
	 * @param <E>        the type of the {@link List} to sort
	 * @param list       the {@link List} of type {@code E} to sort
	 * @param comparator the {@link Comparator} of super type {@code E} to determine the order (a
	 *                   {@code null} value indicates that {@linkplain Comparable natural ordering}
	 *                   of the elements should be used)
	 * <p>
	 * @throws ClassCastException       if the array contains elements that are not mutually
	 *                                  comparable using the specified comparator
	 * @throws IllegalArgumentException (optional) if the comparator is found to violate the
	 *                                  {@link Comparator} contract
	 */
	@SuppressWarnings("unchecked")
	public static <E> void sort(final List<E> list, final Comparator<? super E> comparator) {
		final E[] array = (E[]) list.toArray();
		Arrays.<E>sort(array, comparator);
		final ListIterator<E> iterator = list.listIterator();
		for (final E element : array) {
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
