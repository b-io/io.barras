/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2025 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.struct.collection.list;

import java.util.ArrayList;
import java.util.Collection;

import jupiter.common.model.ICloneable;
import jupiter.common.test.Arguments;
import jupiter.common.test.CollectionArguments;
import jupiter.common.util.Integers;
import jupiter.common.util.Lists;
import jupiter.common.util.Objects;

/**
 * {@link ExtendedList} is the extended {@link ArrayList} of {@code E} element type.
 *
 * @param <E> the element type of the {@link ExtendedList}
 */
public class ExtendedList<E>
		extends ArrayList<E>
		implements ICloneable<ExtendedList<E>> {

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
	 * Constructs an empty {@link ExtendedList} of {@code E} element type by default.
	 */
	public ExtendedList() {
		super(Lists.DEFAULT_CAPACITY);
	}

	/**
	 * Constructs an empty {@link ExtendedList} of {@code E} element type with the specified initial
	 * capacity.
	 *
	 * @param initialCapacity the initial capacity
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public ExtendedList(final int initialCapacity) {
		super(initialCapacity);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs an {@link ExtendedList} of {@code E} element type with the specified elements.
	 *
	 * @param elements an {@code E} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public ExtendedList(final E... elements) {
		super(Arguments.requireNonNull(elements, "elements").length);
		addAll(elements);
	}

	/**
	 * Constructs an {@link ExtendedList} of {@code E} element type with the elements of the
	 * specified {@link Collection}.
	 *
	 * @param elements a {@link Collection} of {@code E} element subtype
	 */
	public ExtendedList(final Collection<? extends E> elements) {
		super(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element {@link Class}.
	 *
	 * @return the element {@link Class}
	 */
	public Class<?> getElementClass() {
		return Lists.getElementClass(this);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the first element.
	 *
	 * @return the first element
	 */
	public E getFirst() {
		// Check the arguments
		CollectionArguments.requireNonEmpty(this);

		// Return the first element
		return get(0);
	}

	/**
	 * Returns the middle element.
	 *
	 * @return the middle element
	 */
	public E getMiddle() {
		// Check the arguments
		CollectionArguments.requireNonEmpty(this);

		// Return the middle element
		return get(Integers.middle(size()));
	}

	/**
	 * Returns the last element.
	 *
	 * @return the last element
	 */
	public E getLast() {
		// Check the arguments
		CollectionArguments.requireNonEmpty(this);

		// Return the last element
		return get(size() - 1);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Replaces the element at the specified position in {@code this} with the specified element.
	 *
	 * @param index   the index of the element to replace
	 * @param element the element to store at the index
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException if {@code index} is out of bounds
	 */
	@Override
	public E set(final int index, final E element) {
		return super.set(index, element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the elements from {@code this}.
	 */
	@Override
	public void clear() {
		super.clear();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code E} array containing all the elements of {@code this} in the same order, or
	 * an empty array if {@code this} is empty.
	 *
	 * @return an {@code E} array containing all the elements of {@code this} in the same order, or
	 *         an empty array if {@code this} is empty
	 *
	 * @see Lists#toArray(Collection)
	 */
	@Override
	public E[] toArray() {
		return Lists.<E>toArray(this);
	}

	/**
	 * Returns a primitive array containing all the elements of {@code this} in the same order, or
	 * {@code null} if {@code this} is empty.
	 *
	 * @return a primitive array containing all the elements of {@code this} in the same order, or
	 *         {@code null} if {@code this} is empty
	 *
	 * @see Lists#toPrimitiveArray(Collection)
	 */
	public Object toPrimitiveArray() {
		return Lists.toPrimitiveArray(this);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified element to the end of {@code this}.
	 *
	 * @param element the {@code E} element to append
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public boolean add(final E element) {
		return super.add(element);
	}

	/**
	 * Inserts the specified element at the specified index into {@code this}. Shifts the element
	 * currently at that position (if any) and any subsequent elements to the right (adds one to
	 * their indices).
	 *
	 * @param index   the index to insert at
	 * @param element the {@code E} element to insert
	 */
	@Override
	public void add(final int index, final E element) {
		super.add(index, element);
	}

	/**
	 * Appends all the specified elements to the end of {@code this}.
	 *
	 * @param <T>      the type of the elements to append ({@code E} subtype)
	 * @param elements the {@code T} elements to append
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	public <T extends E> boolean addAll(final T[] elements) {
		return Lists.<T>addAll(this, elements);
	}

	/**
	 * Appends all the elements of the specified {@link Collection} to the end of {@code this}.
	 *
	 * @param elements the {@link Collection} containing the {@code E} elements to append
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public boolean addAll(final Collection<? extends E> elements) {
		return super.addAll(elements);
	}

	/**
	 * Inserts all the elements of the specified {@link Collection} into {@code this}, starting from
	 * the specified index. Shifts the element currently at that position (if any) and any
	 * subsequent elements to the right (increases their indices).
	 *
	 * @param index    the index to insert at
	 * @param elements the {@link Collection} containing the {@code E} elements to insert
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public boolean addAll(final int index, final Collection<? extends E> elements) {
		return super.addAll(index, elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the element at the specified index from {@code this}.
	 *
	 * @param index the index of the element to remove (may be {@code null})
	 * @return the removed element
	 * @throws IndexOutOfBoundsException if {@code index} is out of bounds
	 */
	@Override
	public E remove(final int index) {
		return super.remove(index);
	}

	/**
	 * Removes the first occurrence of the specified {@link Object} from {@code this}.
	 *
	 * @param object the {@link Object} to remove (may be {@code null})
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public boolean remove(final Object object) {
		return super.remove(object);
	}

	/**
	 * Removes all the elements that are contained in the specified {@link Collection} from
	 * {@code this}.
	 *
	 * @param collection the {@link Collection} to remove (may be {@code null})
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public boolean removeAll(final Collection<?> collection) {
		return super.removeAll(collection);
	}

	//////////////////////////////////////////////

	/**
	 * Removes the first occurrence of the specified {@link Object} from {@code this}.
	 *
	 * @param object the {@link Object} to remove (may be {@code null})
	 * @return the index of the removed element, or {@code -1} if it is not present
	 */
	public int removeFirst(final Object object) {
		return Lists.removeFirst(this, object);
	}

	/**
	 * Removes the last occurrence of the specified {@link Object} from {@code this}.
	 *
	 * @param object the {@link Object} to remove (may be {@code null})
	 * @return the index of the removed element, or {@code -1} if it is not present
	 */
	public int removeLast(final Object object) {
		return Lists.removeLast(this, object);
	}

	/**
	 * Removes all the occurrences of the specified {@link Object} from {@code this}.
	 *
	 * @param object the {@link Object} to remove (may be {@code null})
	 * @return the indices of the removed elements
	 */
	public int[] removeAll(final Object object) {
		return Lists.removeAll(this, object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the elements that are not contained in the specified {@link Collection} from
	 * {@code this}.
	 *
	 * @param collection the {@link Collection} to retain (may be {@code null})
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public boolean retainAll(final Collection<?> collection) {
		return super.retainAll(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is non-empty.
	 *
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
	 *
	 * @return a clone of {@code this}
	 * @see ICloneable
	 */
	@Override
	public ExtendedList<E> clone() {
		final ExtendedList<E> clone = new ExtendedList<E>(size());
		for (final E element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 *
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Lists.toString(this);
	}
}
