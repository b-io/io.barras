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
package jupiter.common.struct.set;

import java.util.Collection;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

/**
 * {@link SynchronizedHashSet} is the synchronized {@link ExtendedHashSet} of {@code E} element
 * type.
 * <p>
 * @param <E> the element type of the {@link SynchronizedHashSet}
 */
public class SynchronizedHashSet<E>
		extends ExtendedHashSet<E> {

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
	 * Constructs an empty {@link SynchronizedHashSet} of {@code E} element type by default.
	 */
	public SynchronizedHashSet() {
		super();
	}

	/**
	 * Constructs an empty {@link SynchronizedHashSet} of {@code E} element type with the specified
	 * initial capacity.
	 * <p>
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public SynchronizedHashSet(final int initialCapacity) {
		super(initialCapacity);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link SynchronizedHashSet} of {@code E} element type with the specified
	 * elements.
	 * <p>
	 * @param elements an {@code E} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public SynchronizedHashSet(final E... elements) {
		super(elements);
	}

	/**
	 * Constructs a {@link SynchronizedHashSet} of {@code E} element type with the elements of the
	 * specified {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@code E} element subtype
	 */
	public SynchronizedHashSet(final Collection<? extends E> elements) {
		super(elements);
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
	 * Adds the specified element to {@code this}.
	 * <p>
	 * @param element the {@code E} element to add
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public synchronized boolean add(final E element) {
		return super.add(element);
	}

	/**
	 * Adds all the specified elements to {@code this}.
	 * <p>
	 * @param <T>      the type of the elements to add ({@code E} subtype)
	 * @param elements the {@code T} elements to add
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public synchronized <T extends E> boolean addAll(final T[] elements) {
		return super.<T>addAll(elements);
	}

	/**
	 * Adds all the elements of the specified {@link Collection} to {@code this}.
	 * <p>
	 * @param elements the {@link Collection} containing the {@code E} elements to add
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	@Override
	public synchronized boolean addAll(final Collection<? extends E> elements) {
		return super.addAll(elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the specified {@link Object} from {@code this}.
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
	public SynchronizedHashSet<E> clone() {
		final SynchronizedHashSet<E> clone = new SynchronizedHashSet<E>(size());
		for (final E element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}
}
