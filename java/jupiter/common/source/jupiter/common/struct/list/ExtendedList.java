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
package jupiter.common.struct.list;

import java.util.ArrayList;
import java.util.Collection;

import jupiter.common.model.ICloneable;
import jupiter.common.test.CollectionArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Collections;
import jupiter.common.util.Integers;
import jupiter.common.util.Lists;
import jupiter.common.util.Objects;

/**
 * {@link ExtendedList} extends {@link ArrayList} of element type {@code E}.
 * <p>
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
	 * Constructs an empty {@link ExtendedList} of element type {@code E} with the default initial
	 * capacity.
	 */
	public ExtendedList() {
		super(Collections.DEFAULT_CAPACITY);
	}

	/**
	 * Constructs an empty {@link ExtendedList} of element type {@code E} with the specified initial
	 * capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public ExtendedList(final int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs an {@link ExtendedList} of element type {@code E} with the specified elements.
	 * <p>
	 * @param elements an {@code E} array
	 * <p>
	 * @throws NullPointerException if {@code collection} is {@code null}
	 */
	public ExtendedList(final E... elements) {
		super(elements.length);
		addAll(elements);
	}

	/**
	 * Constructs an {@link ExtendedList} of element type {@code E} with the specified
	 * {@link Collection} of element subtype {@code E}.
	 * <p>
	 * @param collection a {@link Collection} of element subtype {@code E}
	 * <p>
	 * @throws NullPointerException if {@code collection} is {@code null}
	 */
	public ExtendedList(final Collection<? extends E> collection) {
		super(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public Class<E> getElementClass() {
		if (isEmpty()) {
			return null;
		}
		return (Class<E>) get(0).getClass();
	}

	/**
	 * Returns the middle.
	 * <p>
	 * @return the middle
	 */
	public E getMiddle() {
		// Check the arguments
		CollectionArguments.requireNonEmpty(this);

		// Get the middle
		return get(Integers.middle(size()));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Replaces the element at the specified position in {@code this} with the specified element.
	 * <p>
	 * @param index   the index of the element to replace
	 * @param element the element to store at the index
	 * <p>
	 * @return the element previously at the specified position
	 * <p>
	 * @throws IndexOutOfBoundsException if {@code index} is out of bounds
	 */
	@Override
	public synchronized E set(final int index, final E element) {
		return super.set(index, element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@code E} array containing all of the elements in {@code this} in proper sequence
	 * (from first to last element).
	 * <p>
	 * @return an {@code E} array containing all of the elements in {@code this} in proper sequence
	 *         (from first to last element)
	 *
	 * @see ArrayList#toArray
	 */
	@Override
	public Object[] toArray() {
		if (isEmpty()) {
			return Objects.EMPTY_ARRAY;
		}
		return super.toArray(Arrays.create(getElementClass(), size()));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized boolean add(final E element) {
		return super.add(element);
	}

	@Override
	public synchronized void add(final int index, final E element) {
		super.add(index, element);
	}

	public synchronized <T extends E> boolean addAll(final T[] array) {
		return Lists.addAll(this, array);
	}

	@Override
	public synchronized boolean addAll(final Collection<? extends E> collection) {
		return super.addAll(collection);
	}

	@Override
	public synchronized boolean addAll(final int index, final Collection<? extends E> collection) {
		return super.addAll(index, collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized E remove(final int index) {
		return super.remove(index);
	}

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
	 * Removes the first occurrence of the specified {@link Object} from {@code this} and returns
	 * the index of the removed element, or {@code -1} if it is not present.
	 * <p>
	 * @param object the {@link Object} to remove
	 * <p>
	 * @return the index of the removed element, or {@code -1} if it is not present
	 */
	public synchronized int removeFirst(final Object object) {
		return Lists.removeFirst(this, object);
	}

	/**
	 * Removes all the occurrences of the specified {@link Object} from {@code this} and returns the
	 * number of removed elements.
	 * <p>
	 * @param object the {@link Object} to remove
	 * <p>
	 * @return the number of removed elements
	 */
	public synchronized int removeAll(final Object object) {
		return Collections.removeAll(this, object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized boolean retainAll(final Collection<?> collection) {
		return super.retainAll(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized void clear() {
		super.clear();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ExtendedList<E> clone() {
		return (ExtendedList<E>) super.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Collections.toString(this);
	}
}
