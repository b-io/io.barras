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
package jupiter.common.struct.list;

import java.util.Collection;
import java.util.LinkedList;

import jupiter.common.model.ICloneable;
import jupiter.common.test.CollectionArguments;
import jupiter.common.util.Collections;
import jupiter.common.util.Integers;

/**
 * {@link SortedList} extends {@link LinkedList} of element type {@code E}.
 * <p>
 * @param <E> the self element {@link Comparable} type of the {@link SortedList}
 */
public class SortedList<E extends Comparable<E>>
		extends LinkedList<E>
		implements ICloneable<SortedList<E>> {

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
	 * Constructs an empty {@link SortedList} of element type {@code E}.
	 */
	public SortedList() {
		super();
	}

	/**
	 * Constructs a {@link SortedList} of element type {@code E} with the specified
	 * {@link Collection} of element subtype {@code E}.
	 * <p>
	 * @param collection a {@link Collection} of element subtype {@code E}
	 * <p>
	 * @throws NullPointerException if {@code collection} is {@code null}
	 */
	public SortedList(final Collection<? extends E> collection) {
		super(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element {@link Class}.
	 * <p>
	 * @return the element {@link Class}
	 */
	public Class<E> getElementClass() {
		return Collections.getElementClass(this);
	}

	public E getMiddle() {
		// Check the arguments
		CollectionArguments.requireNotEmpty(this);

		// Get the middle
		return get(Integers.middle(size()));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized E set(final int index, final E element) {
		return super.set(index, element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array containing all of the elements in {@code this} in proper sequence (from
	 * first to last element), or an empty array if {@code this} is empty.
	 * <p>
	 * @return an array containing all of the elements in {@code this} in proper sequence (from
	 *         first to last element), or an empty array if {@code this} is empty
	 *
	 * @see LinkedList#toArray
	 */
	@Override
	public Object[] toArray() {
		return Collections.toArray(this);
	}

	/**
	 * Returns a primitive array containing all of the elements in {@code this} in proper sequence
	 * (from first to last element), or {@code null} if {@code this} is empty.
	 * <p>
	 * @return a primitive array containing all of the elements in {@code this} in proper sequence
	 *         (from first to last element), or {@code null} if {@code this} is empty
	 *
	 * @see LinkedList#toArray
	 */
	public Object toPrimitiveArray() {
		return Collections.toPrimitiveArray(this);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized boolean add(final E element) {
		int index = 0;
		for (final E e : this) {
			if (e.compareTo(element) > 0) {
				add(index, element);
				return true;
			}
			++index;
		}
		super.add(element);
		return true;
	}

	@Override
	public synchronized void add(final int index, final E element) {
		super.add(index, element);
	}

	public synchronized <T extends E> boolean addAll(final T[] array) {
		for (final E element : array) {
			add(element);
		}
		return true;
	}

	@Override
	public synchronized boolean addAll(final Collection<? extends E> collection) {
		for (final E element : collection) {
			add(element);
		}
		return true;
	}

	@Override
	public synchronized boolean addAll(final int index, final Collection<? extends E> collection) {
		return addAll(collection);
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
	public SortedList<E> clone() {
		return (SortedList<E>) super.clone();
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
