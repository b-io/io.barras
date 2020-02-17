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
import java.util.Iterator;
import java.util.NoSuchElementException;

import jupiter.common.exception.IllegalClassException;
import jupiter.common.map.ObjectToStringMapper;

public class Collections {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default delimiting {@link String}.
	 */
	public static final String DELIMITER = ",";

	/**
	 * The default initial capacity.
	 */
	public static final int DEFAULT_CAPACITY = 10;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Collections}.
	 */
	protected Collections() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element {@link Class} of the specified {@link Collection}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection}
	 * @param collection the {@link Collection} of element type {@code E} to convert
	 * <p>
	 * @return the element {@link Class} of the specified {@link Collection}
	 */
	@SuppressWarnings("unchecked")
	public static <E> Class<E> getElementClass(final Collection<E> collection) {
		if (isNotEmpty(collection)) {
			final Iterator<?> iterator = collection.iterator();
			while (iterator.hasNext()) {
				final Object element = iterator.next();
				if (element != null) {
					return (Class<E>) element.getClass();
				}
			}
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array containing all of the elements in the specified {@link Collection} in proper
	 * sequence (from first to last element), or an empty array if the specified {@link Collection}
	 * is {@code null} or empty.
	 * <p>
	 * @param collection the {@link Collection} to convert (may be {@code null})
	 * <p>
	 * @return an array containing all of the elements in the specified {@link Collection} in proper
	 *         sequence (from first to last element), or an empty array if the specified
	 *         {@link Collection} is {@code null} or empty
	 *
	 * @see Collection#toArray
	 */
	public static Object[] toArray(final Collection<?> collection) {
		final Class<?> c = getElementClass(collection);
		if (c == null) {
			return Objects.EMPTY_ARRAY;
		}
		return collection.toArray(Arrays.create(c, collection.size()));
	}

	/**
	 * Returns a primitive array converted from the specified {@link Collection}, or {@code null} if
	 * the specified {@link Collection} is {@code null} or empty.
	 * <p>
	 * @param collection the {@link Collection} to convert (may be {@code null})
	 * <p>
	 * @return a primitive array converted from the specified {@link Collection}, or {@code null} if
	 *         the specified {@link Collection} is {@code null} or empty
	 */
	public static Object toPrimitiveArray(final Collection<?> collection) {
		return toPrimitiveArray(getElementClass(collection), collection);
	}

	/**
	 * Returns a primitive array converted from the specified {@link Collection} of the specified
	 * element {@link Class}, or {@code null} if the specified {@link Collection} or element
	 * {@link Class} is {@code null}.
	 * <p>
	 * @param c          the element {@link Class} of the {@link Collection} to convert (may be
	 *                   {@code null})
	 * @param collection the {@link Collection} to convert (may be {@code null})
	 * <p>
	 * @return a primitive array converted from the specified {@link Collection} of the specified
	 *         element {@link Class}, or {@code null} if the specified {@link Collection} or element
	 *         {@link Class} is {@code null}
	 */
	public static Object toPrimitiveArray(final Class<?> c, final Collection<?> collection) {
		if (c == null || collection == null) {
			return null;
		}
		if (Booleans.is(c)) {
			return Booleans.collectionToPrimitiveArray(collection);
		} else if (Characters.is(c)) {
			return Characters.collectionToPrimitiveArray(collection);
		} else if (Bytes.is(c)) {
			return Bytes.collectionToPrimitiveArray(collection);
		} else if (Shorts.is(c)) {
			return Shorts.collectionToPrimitiveArray(collection);
		} else if (Integers.is(c)) {
			return Integers.collectionToPrimitiveArray(collection);
		} else if (Longs.is(c)) {
			return Longs.collectionToPrimitiveArray(collection);
		} else if (Floats.is(c)) {
			return Floats.collectionToPrimitiveArray(collection);
		} else if (Doubles.is(c)) {
			return Doubles.collectionToPrimitiveArray(collection);
		}
		throw new IllegalClassException(c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Collection}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection}
	 */
	public static String join(final Collection<?> collection) {
		return Strings.joinWith(collection, DELIMITER);
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
	 * @param <C>        the {@link Collection} type
	 * @param <E>        the element type of the {@link Collection}
	 * @param collection the {@link Collection} of element type {@code E} to convert
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
	 * @param <C>        the {@link Collection} type
	 * @param <E>        the element type of the {@link Collection}
	 * @param collection the {@link Collection} of element type {@code E} to convert
	 * @param object     the {@link Object} to remove
	 * <p>
	 * @return the number of removed elements
	 */
	public static <C extends Collection<E>, E> int removeFirst(final C collection,
			final Object object) {
		final Iterator<E> iterator = collection.iterator();
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
	 * @param <C>        the {@link Collection} type
	 * @param <E>        the element type of the {@link Collection}
	 * @param collection the {@link Collection} of element type {@code E} to convert
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

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Collection} is {@code null} or empty.
	 * <p>
	 * @param collection the {@link Collection} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Collection} is {@code null} or empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * Tests whether the specified {@link Collection} is not {@code null} and empty.
	 * <p>
	 * @param collection the {@link Collection} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Collection} is not {@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final Collection<?> collection) {
		return collection != null && collection.isEmpty();
	}

	/**
	 * Tests whether the specified {@link Collection} is not {@code null} and not empty.
	 * <p>
	 * @param collection the {@link Collection} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Collection} is not {@code null} and not empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNotEmpty(final Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Collection}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection}
	 */
	public static String toString(final Collection<?> collection) {
		return Strings.bracketize(Strings.joinWith(collection, DELIMITER));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param delimiter  the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by
	 *         {@code delimiter}
	 */
	public static String toStringWith(final Collection<?> collection, final String delimiter) {
		return Strings.bracketize(Strings.joinWith(collection, delimiter));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} wrapped by
	 * {@code wrapper}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} wrapped by
	 *         {@code wrapper}
	 */
	public static String toStringWith(final Collection<?> collection,
			final ObjectToStringMapper wrapper) {
		return Strings.bracketize(Strings.joinWith(collection, wrapper));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param delimiter  the delimiting {@link String}
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String toStringWith(final Collection<?> collection, final String delimiter,
			final ObjectToStringMapper wrapper) {
		return Strings.bracketize(Strings.joinWith(collection, delimiter, wrapper));
	}
}
