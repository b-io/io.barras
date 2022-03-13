/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

/**
 * {@link SynchronizedLinkedList} is the synchronized {@link ExtendedLinkedList} of {@code E}
 * element type.
 * <p>
 * @param <E> the element type of the {@link SynchronizedLinkedList}
 */
public class SynchronizedLinkedList<E>
		extends ExtendedLinkedList<E> {

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
	 * Constructs an empty {@link SynchronizedLinkedList} of {@code E} element type.
	 */
	public SynchronizedLinkedList() {
		super();
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link SynchronizedLinkedList} of {@code E} element type with the specified
	 * elements.
	 * <p>
	 * @param elements an {@code E} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public SynchronizedLinkedList(final E... elements) {
		super(elements);
	}

	/**
	 * Constructs a {@link SynchronizedLinkedList} of {@code E} element type with the elements of
	 * the specified {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@code E} element subtype
	 */
	public SynchronizedLinkedList(final Collection<? extends E> elements) {
		super(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
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
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified element to the end of {@code this}.
	 * <p>
	 * @param element the {@code E} element to append
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public synchronized boolean add(final E element) {
		return super.add(element);
	}

	/**
	 * Inserts the specified element at the specified index into {@code this}. Shifts the element
	 * currently at that position (if any) and any subsequent elements to the right (adds one to
	 * their indices).
	 * <p>
	 * @param index   the index to insert at
	 * @param element the {@code E} element to insert
	 */
	@Override
	public synchronized void add(final int index, final E element) {
		super.add(index, element);
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
	@Override
	public synchronized <T extends E> boolean addAll(final T[] elements) {
		return super.<T>addAll(elements);
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

	/**
	 * Inserts all the elements of the specified {@link Collection} into {@code this}, starting from
	 * the specified index. Shifts the element currently at that position (if any) and any
	 * subsequent elements to the right (increases their indices).
	 * <p>
	 * @param index    the index to insert at
	 * @param elements the {@link Collection} containing the {@code E} elements to insert
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public synchronized boolean addAll(final int index, final Collection<? extends E> elements) {
		return super.addAll(index, elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the element at the specified index from {@code this}.
	 * <p>
	 * @param index the index of the element to remove (may be {@code null})
	 * <p>
	 * @return the removed element
	 * <p>
	 * @throws IndexOutOfBoundsException if {@code index} is out of bounds
	 */
	@Override
	public synchronized E remove(final int index) {
		return super.remove(index);
	}

	/**
	 * Removes the first occurrence of the specified {@link Object} from {@code this}.
	 * <p>
	 * @param object the {@link Object} to remove (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public synchronized boolean remove(final Object object) {
		return super.remove(object);
	}

	/**
	 * Removes all the elements that are contained in the specified {@link Collection} from
	 * {@code this}.
	 * <p>
	 * @param collection the {@link Collection} to remove (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
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
	@Override
	public synchronized int removeFirst(final Object object) {
		return super.removeFirst(object);
	}

	/**
	 * Removes the last occurrence of the specified {@link Object} from {@code this}.
	 * <p>
	 * @param object the {@link Object} to remove (may be {@code null})
	 * <p>
	 * @return the index of the removed element, or {@code -1} if it is not present
	 */
	@Override
	public synchronized int removeLast(final Object object) {
		return super.removeLast(object);
	}

	/**
	 * Removes all the occurrences of the specified {@link Object} from {@code this}.
	 * <p>
	 * @param object the {@link Object} to remove (may be {@code null})
	 * <p>
	 * @return the indices of the removed elements
	 */
	@Override
	public synchronized int[] removeAll(final Object object) {
		return super.removeAll(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the elements that are not contained in the specified {@link Collection} from
	 * {@code this}.
	 * <p>
	 * @param collection the {@link Collection} to retain (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public synchronized boolean retainAll(final Collection<?> collection) {
		return super.retainAll(collection);
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
	public SynchronizedLinkedList<E> clone() {
		final SynchronizedLinkedList<E> clone = new SynchronizedLinkedList<E>();
		for (final E element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}
}
