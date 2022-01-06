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

import jupiter.common.math.Comparables;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

/**
 * {@link SynchronizedSortedUniqueList} is the sorted unique {@link SynchronizedLinkedList} of
 * {@code E} element type.
 * <p>
 * @param <E> the self element {@link Comparable} type of the {@link SynchronizedSortedUniqueList}
 */
public class SynchronizedSortedUniqueList<E extends Comparable<? super E>>
		extends SynchronizedLinkedList<E> {

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
	 * Constructs an empty {@link SynchronizedSortedUniqueList} of {@code E} element type.
	 */
	public SynchronizedSortedUniqueList() {
		super();
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link SynchronizedSortedUniqueList} of {@code E} element type with the
	 * specified elements.
	 * <p>
	 * @param elements an {@code E} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public SynchronizedSortedUniqueList(final E... elements) {
		super(elements);
	}

	/**
	 * Constructs a {@link SynchronizedSortedUniqueList} of {@code E} element type with the elements
	 * of the specified {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@code E} element subtype
	 */
	public SynchronizedSortedUniqueList(final Collection<? extends E> elements) {
		super(elements);
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
		int index = 0;
		for (final E e : this) {
			final int comparison = Comparables.compare(e, element);
			if (comparison == 0) {
				return false;
			} else if (comparison > 0) {
				add(index, element);
				return true;
			}
			++index;
		}
		super.add(element);
		return true;
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
	public SynchronizedSortedUniqueList<E> clone() {
		final SynchronizedSortedUniqueList<E> clone = new SynchronizedSortedUniqueList<E>();
		for (final E element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}
}
