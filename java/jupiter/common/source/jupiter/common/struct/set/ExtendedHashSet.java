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
package jupiter.common.struct.set;

import java.util.HashSet;
import java.util.Collection;

import jupiter.common.model.ICloneable;
import jupiter.common.test.Arguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Sets;

/**
 * {@link ExtendedHashSet} is the extended synchronized {@link HashSet} of {@code E} element type.
 * <p>
 * @param <E> the element type of the {@link ExtendedHashSet}
 */
public class ExtendedHashSet<E>
		extends HashSet<E>
		implements ICloneable<ExtendedHashSet<E>> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link ExtendedHashSet} of {@code E} element type by default.
	 */
	public ExtendedHashSet() {
		super(Sets.DEFAULT_CAPACITY);
	}

	/**
	 * Constructs an empty {@link ExtendedHashSet} of {@code E} element type with the specified
	 * initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public ExtendedHashSet(final int initialCapacity) {
		super(initialCapacity);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs an {@link ExtendedHashSet} of {@code E} element type with the specified elements.
	 * <p>
	 * @param elements an {@code E} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public ExtendedHashSet(final E... elements) {
		super(Arguments.requireNonNull(elements, "elements").length);
		addAll(elements);
	}

	/**
	 * Constructs an {@link ExtendedHashSet} of {@code E} element type with the elements of the
	 * specified {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@code E} element subtype
	 */
	public ExtendedHashSet(final Collection<? extends E> elements) {
		super(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element {@link Class}.
	 * <p>
	 * @return the element {@link Class}
	 */
	public Class<?> getElementClass() {
		return Sets.getElementClass(this);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the elements from {@code this}.
	 */
	@Override
	public synchronized void clear() {
		super.clear();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code E} array containing all the elements of {@code this} in the same order, or
	 * an empty array if {@code this} is empty.
	 * <p>
	 * @return an {@code E} array containing all the elements of {@code this} in the same order, or
	 *         an empty array if {@code this} is empty
	 *
	 * @see HashSet#toArray(Object[])
	 */
	@Override
	public E[] toArray() {
		return Sets.<E>toArray(this);
	}

	/**
	 * Returns a primitive array containing all the elements of {@code this} in the same order, or
	 * {@code null} if {@code this} is empty.
	 * <p>
	 * @return a primitive array containing all the elements of {@code this} in the same order, or
	 *         {@code null} if {@code this} is empty
	 *
	 * @see HashSet#toArray(Object[])
	 */
	public Object toPrimitiveArray() {
		return Sets.toPrimitiveArray(this);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified element to the end of {@code this}.
	 * <p>
	 * @param element the {@code E} element to append
	 * <p>
	 * @return {@code true} (as specified by {@link Collection#add})
	 */
	@Override
	public synchronized boolean add(final E element) {
		return super.add(element);
	}

	/**
	 * Appends all the specified elements to the end of {@code this}.
	 * <p>
	 * @param <T>      the type of the elements to append ({@code E} subtype)
	 * @param elements the {@code T} elements to append
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	public synchronized <T extends E> boolean addAll(final T[] elements) {
		return Sets.<T>addAll(this, elements);
	}

	/**
	 * Appends all the elements of the specified {@link Collection} to the end of {@code this}.
	 * <p>
	 * @param elements the {@link Collection} containing the {@code E} elements to append
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public synchronized boolean addAll(final Collection<? extends E> elements) {
		return super.addAll(elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized boolean remove(final Object object) {
		return super.remove(object);
	}

	@Override
	public synchronized boolean removeAll(final Collection<?> collection) {
		return super.removeAll(collection);
	}

	//////////////////////////////////////////////

	/**
	 * Removes the first occurrence of the specified {@link Object} from {@code this}.
	 * <p>
	 * @param object the {@link Object} to remove (may be {@code null})
	 * <p>
	 * @return the index of the removed element, or {@code -1} if it is not present
	 */
	public synchronized int removeFirst(final Object object) {
		return Sets.removeFirst(this, object);
	}

	/**
	 * Removes all the occurrences of the specified {@link Object} from {@code this}.
	 * <p>
	 * @param object the {@link Object} to remove (may be {@code null})
	 * <p>
	 * @return the indices of the removed elements
	 */
	public synchronized int[] removeAll(final Object object) {
		return Sets.removeAll(this, object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized boolean retainAll(final Collection<?> collection) {
		return super.retainAll(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is non-empty.
	 * <p>
	 * @return {@code true} if {@code this} is non-empty, {@code false} otherwise
	 */
	public boolean isNonEmpty() {
		return !isEmpty();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public ExtendedHashSet<E> clone() {
		final ExtendedHashSet<E> clone = new ExtendedHashSet<E>(size());
		for (final E element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Sets.toString(this);
	}
}
