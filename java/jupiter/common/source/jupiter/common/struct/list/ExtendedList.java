/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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

import jupiter.common.math.WholeNumbers;
import jupiter.common.model.ICloneable;
import jupiter.common.test.CollectionArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Collections;

public class ExtendedList<T>
		extends ArrayList<T>
		implements ICloneable<ExtendedList<T>> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -5163759803630476779L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link ExtendedList} of type {@code T} with the default initial capacity.
	 */
	public ExtendedList() {
		super(Arrays.DEFAULT_CAPACITY);
	}

	/**
	 * Constructs an empty {@link ExtendedList} of type {@code T} with the specified initial
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
	 * Constructs an {@link ExtendedList} of type {@code T} from the specified {@link Collection} of
	 * super-type {@code T}.
	 * <p>
	 * @param collection a {@link Collection} of super-type {@code T}
	 * <p>
	 * @throws NullPointerException if {@code collection} is {@code null}
	 */
	public ExtendedList(final Collection<? extends T> collection) {
		super(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public T getMiddle() {
		// Check the arguments
		CollectionArguments.requireNonEmpty(this);

		// Get the middle
		return get(WholeNumbers.middle(size()));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized T set(final int index, final T element) {
		return super.set(index, element);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized boolean add(final T element) {
		return super.add(element);
	}

	@Override
	public synchronized void add(final int index, final T element) {
		super.add(index, element);
	}

	@Override
	public synchronized boolean addAll(final Collection<? extends T> collection) {
		return super.addAll(collection);
	}

	@Override
	public synchronized boolean addAll(final int index, final Collection<? extends T> collection) {
		return super.addAll(index, collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public synchronized T remove(final int index) {
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

	@Override
	public ExtendedList<T> clone() {
		return new ExtendedList<T>(this);
	}

	@Override
	public String toString() {
		return Collections.<T>toString(this);
	}
}
