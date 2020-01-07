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
import java.util.Iterator;
import java.util.NoSuchElementException;

import jupiter.common.map.ObjectToStringMapper;

public class Collections {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default delimiting {@link String}.
	 */
	public static final String DEFAULT_DELIMITER = ",";

	/**
	 * The default initial capacity.
	 */
	public static volatile int DEFAULT_CAPACITY = 10;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Collections}.
	 */
	protected Collections() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} of
	 * {@link Object}.
	 * <p>
	 * @param collection a {@link Collection} of {@link Object}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} of {@link Object}
	 */
	public static String join(final Collection<?> collection) {
		return Strings.joinWith(collection, DEFAULT_DELIMITER);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <E, T extends E> boolean addAll(final Collection<E> collection, final T[] array) {
		boolean status = true;
		for (final E element : array) {
			status &= collection.add(element);
		}
		return status;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element at the specified index of the elements returned by the iterator.
	 * <p>
	 * @param <C>        the type extending {@link Collection}
	 * @param <E>        the type of the {@link Collection}
	 * @param collection a {@link Collection} of type {@code E}
	 * @param index      the index of the element to return
	 * <p>
	 * @return the element at the specified index of the elements returned by the iterator
	 * <p>
	 * @throws NoSuchElementException if the iteration has no more elements
	 */
	public static <C extends Collection<E>, E> E get(final C collection, final int index)
			throws NoSuchElementException {
		final Iterator<E> iterator = collection.iterator();
		for (int i = 0; i < index; ++i) {
			iterator.next();
		}
		return iterator.next();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the first occurrence of the specified {@link Object} from the specified
	 * {@link Collection} and returns the number of removed elements.
	 * <p>
	 * @param <C>    the type extending {@link Collection}
	 * @param <E>    the type of the {@link Collection}
	 * @param list   a {@link Collection} of type {@code E}
	 * @param object the {@link Object} to remove
	 * <p>
	 * @return the number of removed elements
	 */
	public static <C extends Collection<E>, E> int removeFirst(final C list, final Object object) {
		final Iterator<E> iterator = list.iterator();
		while (iterator.hasNext()) {
			if (Objects.equals(iterator.next(), object)) {
				iterator.remove();
				return 1;
			}
		}
		return 0;
	}

	/**
	 * Removes all the occurrences of the specified {@link Object} from the specified
	 * {@link Collection} and returns the number of removed elements.
	 * <p>
	 * @param <C>        the type extending {@link Collection}
	 * @param <E>        the type of the {@link Collection}
	 * @param collection a {@link Collection} of type {@code E}
	 * @param object     the {@link Object} to remove
	 * <p>
	 * @return the number of removed elements
	 */
	public static <C extends Collection<E>, E> int removeAll(final C collection,
			final Object object) {
		final Iterator<E> iterator = collection.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			if (Objects.equals(iterator.next(), object)) {
				iterator.remove();
				++count;
			}
		}
		return count;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Collection}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Collection},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return Collection.class.isAssignableFrom(c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} of
	 * {@link Object}.
	 * <p>
	 * @param collection a {@link Collection} of {@link Object}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} of {@link Object}
	 */
	public static String toString(final Collection<?> collection) {
		return Strings.bracketize(Strings.joinWith(collection, DEFAULT_DELIMITER));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} of {@link Object}
	 * joined by {@code delimiter}.
	 * <p>
	 * @param collection a {@link Collection} of {@link Object}
	 * @param delimiter  the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} of {@link Object}
	 *         joined by {@code delimiter}
	 */
	public static String toString(final Collection<?> collection, final String delimiter) {
		return Strings.bracketize(Strings.joinWith(collection, delimiter));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} of {@link Object}
	 * joined by {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param collection a {@link Collection} of {@link Object}
	 * @param delimiter  the delimiting {@link String}
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} of {@link Object}
	 *         joined by {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toString(final Collection<?> collection, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Strings.bracketize(Strings.joinWith(collection, delimiter, wrapper));
	}
}
