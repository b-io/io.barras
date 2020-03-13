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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import jupiter.common.exception.IllegalClassException;
import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.struct.list.ExtendedList;

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
	 * @param collection a {@link Collection} (may be {@code null})
	 * <p>
	 * @return the element {@link Class} of the specified {@link Collection}
	 */
	public static Class<?> getElementClass(final Collection<?> collection) {
		// Check the arguments
		if (isNullOrEmpty(collection)) {
			return Object.class;
		}

		// Get the element class of the collection (common ancestor of the element classes)
		final Iterator<?> iterator = collection.iterator();
		Class<?> c = Classes.get(iterator.next());
		while (iterator.hasNext()) {
			c = Classes.getCommonAncestor(c, Classes.get(iterator.next()));
		}
		return c != null ? c : Object.class;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code E} array containing all the elements of the specified {@link Collection} in
	 * the same order, or an empty array if the specified {@link Collection} is {@code null} or
	 * empty.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection the {@link Collection} of {@code E} element type to convert (may be
	 *                   {@code null})
	 * <p>
	 * @return an {@code E} array containing all the elements of the specified {@link Collection} in
	 *         the same order, or an empty array if the specified {@link Collection} is {@code null}
	 *         or empty
	 *
	 * @see Collection#toArray(Object[])
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public static <E> E[] toArray(final Collection<E> collection) {
		return (E[]) collection.toArray(Arrays.create(getElementClass(collection),
				collection.size()));
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
	 * Returns a primitive array of the specified element {@link Class} converted from the specified
	 * {@link Collection}, or {@code null} if any of them is {@code null} or {@code c} is equal to
	 * {@code Object.class}.
	 * <p>
	 * @param c          the element {@link Class} of the {@link Collection} to convert (may be
	 *                   {@code null})
	 * @param collection the {@link Collection} to convert (may be {@code null})
	 * <p>
	 * @return a primitive array of the specified element {@link Class} converted from the specified
	 *         {@link Collection}, or {@code null} if any of them is {@code null} or {@code c} is
	 *         equal to {@code Object.class}
	 */
	public static Object toPrimitiveArray(final Class<?> c, final Collection<?> collection) {
		// Check the arguments
		if (c == null || c == Object.class || collection == null) {
			return null;
		}

		// Convert the collection to a primitive array of the element class
		if (Booleans.isFrom(c)) {
			return Booleans.collectionToPrimitiveArray(collection);
		} else if (Characters.isFrom(c)) {
			return Characters.collectionToPrimitiveArray(collection);
		} else if (Bytes.isFrom(c)) {
			return Bytes.collectionToPrimitiveArray(collection);
		} else if (Shorts.isFrom(c)) {
			return Shorts.collectionToPrimitiveArray(collection);
		} else if (Integers.isFrom(c)) {
			return Integers.collectionToPrimitiveArray(collection);
		} else if (Longs.isFrom(c)) {
			return Longs.collectionToPrimitiveArray(collection);
		} else if (Floats.isFrom(c)) {
			return Floats.collectionToPrimitiveArray(collection);
		} else if (Doubles.isFrom(c)) {
			return Doubles.collectionToPrimitiveArray(collection);
		}
		throw new IllegalClassException(c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Collection}, or
	 * {@code "null"} if it is {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection}, or
	 *         {@code "null"} if it is {@code null}
	 */
	public static String join(final Collection<?> collection) {
		return Strings.joinWith(collection, DELIMITER);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds all the specified elements to the specified {@link Collection}.
	 * <p>
	 * @param <T>        the type of the elements to add
	 * @param collection a {@link Collection} of {@code T} supertype
	 * @param elements   the {@code T} elements to add
	 * <p>
	 * @return {@code true} if the specified {@link Collection} changed as a result of the call
	 */
	public static <T> boolean addAll(final Collection<? super T> collection, final T[] elements) {
		boolean status = true;
		for (final T element : elements) {
			status &= collection.add(element);
		}
		return status;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element at the specified index of the elements returned by the iterator.
	 * <p>
	 * @param <E>        the element type of the {@link Collection}
	 * @param collection a {@link Collection} of {@code E} element type
	 * @param index      the index of the element to return
	 * <p>
	 * @return the element at the specified index of the elements returned by the iterator
	 * <p>
	 * @throws NoSuchElementException if the iteration has no more elements
	 */
	public static <E> E get(final Collection<E> collection, final int index)
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
	 * {@link Collection} and returns the index of the removed element, or {@code -1} if it is not
	 * present.
	 * <p>
	 * @param collection a {@link Collection} of {@code E} element type
	 * @param object     the {@link Object} to remove
	 * <p>
	 * @return the index of the removed element, or {@code -1} if it is not present
	 */
	public static int removeFirst(final Collection<?> collection, final Object object) {
		final Iterator<?> iterator = collection.iterator();
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
	 * Removes all the occurrences of the specified {@link Object} from the specified
	 * {@link Collection} and returns the indexes of the removed elements.
	 * <p>
	 * @param collection a {@link Collection} of {@code E} element type
	 * @param object     the {@link Object} to remove
	 * <p>
	 * @return the indexes of the removed elements
	 */
	public static int[] removeAll(final Collection<?> collection, final Object object) {
		final ExtendedList<Integer> indexes = new ExtendedList<Integer>();
		final Iterator<?> iterator = collection.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			if (Objects.equals(iterator.next(), object)) {
				iterator.remove();
				indexes.add(index);
			}
			++index;
		}
		return Integers.collectionToPrimitiveArray(indexes);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link Collection}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Collection},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Collection;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Collection}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Collection},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
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
	 * Tests whether the specified {@link Collection} is non-{@code null} and empty.
	 * <p>
	 * @param collection the {@link Collection} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Collection} is non-{@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final Collection<?> collection) {
		return collection != null && collection.isEmpty();
	}

	/**
	 * Tests whether the specified {@link Collection} is non-{@code null} and non-empty.
	 * <p>
	 * @param collection the {@link Collection} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link Collection} is non-{@code null} and non-empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNonEmpty(final Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Collection}, or
	 * {@code "null"} if it is {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection}, or
	 *         {@code "null"} if it is {@code null}
	 */
	public static String toString(final Collection<?> collection) {
		if (collection == null) {
			return NULL;
		}
		return Strings.bracketize(Strings.joinWith(collection, DELIMITER));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by the
	 * specified delimiting {@link String}, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param delimiter  the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by the
	 *         specified delimiting {@link String}, or {@code "null"} if it is {@code null}
	 */
	public static String toStringWith(final Collection<?> collection, final String delimiter) {
		if (collection == null) {
			return NULL;
		}
		return Strings.bracketize(Strings.joinWith(collection, delimiter));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} wrapped by
	 * {@code wrapper}, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} wrapped by
	 *         {@code wrapper}, or {@code "null"} if it is {@code null}
	 */
	public static String toStringWith(final Collection<?> collection,
			final ObjectToStringMapper wrapper) {
		if (collection == null) {
			return NULL;
		}
		return Strings.bracketize(Strings.joinWith(collection, wrapper));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by the
	 * specified delimiting {@link String} and wrapped by {@code wrapper}, or {@code "null"} if it
	 * is {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param delimiter  the delimiting {@link String}
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by the
	 *         specified delimiting {@link String} and wrapped by {@code wrapper}, or {@code "null"}
	 *         if it is {@code null}
	 */
	public static String toStringWith(final Collection<?> collection, final String delimiter,
			final ObjectToStringMapper wrapper) {
		if (collection == null) {
			return NULL;
		}
		return Strings.bracketize(Strings.joinWith(collection, delimiter, wrapper));
	}
}
