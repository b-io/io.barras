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
package jupiter.common.math;

import static jupiter.common.io.IO.IO;

import java.util.Collection;
import java.util.ListIterator;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.SortedUniqueList;
import jupiter.common.util.Objects;

/**
 * {@link GenericIntervalList} is the {@link SortedUniqueList} of {@code E} element type (subtype of
 * {@link Interval} of {@code T} type).
 * <p>
 * @param <E> the element type of the {@link SortedUniqueList} (subtype of {@link Interval} of
 *            {@code T} type)
 * @param <T> the self {@link Comparable} type of the {@link Interval}
 */
public class GenericIntervalList<E extends Interval<T>, T extends Comparable<? super T>>
		extends SortedUniqueList<E>
		implements ISet<T> {

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
	 * Constructs an empty {@link GenericIntervalList} of {@code E} and {@code T} types.
	 */
	public GenericIntervalList() {
		super();
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link GenericIntervalList} of {@code E} and {@code T} types with the specified
	 * elements.
	 * <p>
	 * @param elements an {@code E} array
	 */
	public GenericIntervalList(final E... elements) {
		super(elements);
	}

	/**
	 * Constructs a {@link GenericIntervalList} of {@code E} and {@code T} types with the elements
	 * of the specified {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@code E} element subtype
	 */
	public GenericIntervalList(final Collection<? extends E> elements) {
		super(elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link LowerBound} of {@code T} type.
	 * <p>
	 * @return the {@link LowerBound} of {@code T} type
	 */
	public LowerBound<T> getLowerBound() {
		return isNonEmpty() ? getFirst().lowerBound : null;
	}

	/**
	 * Returns the {@link UpperBound} of {@code T} type.
	 * <p>
	 * @return the {@link UpperBound} of {@code T} type
	 */
	public UpperBound<T> getUpperBound() {
		UpperBound<T> upperBound = null;
		for (final E interval : this) {
			if (Comparables.isGreaterThan(interval.upperBound, upperBound)) {
				upperBound = interval.upperBound;
			}
		}
		return upperBound;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code value} if {@code value} is inside {@code this}, {@code null} otherwise.
	 * <p>
	 * @param value the {@code T} value to constrain
	 * <p>
	 * @return {@code value} if {@code value} is inside {@code this}, {@code null} otherwise
	 */
	public T constrain(final T value) {
		if (value == null) {
			IO.warn("The specified value is null");
			return null;
		}
		if (isEmpty()) {
			IO.warn("The interval list is empty");
			return null;
		}
		if (!isInside(value)) {
			IO.warn("The specified value ", value, " is not inside the interval list ", this);
			return null;
		}
		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Merges the intervals.
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	public synchronized boolean merge() {
		boolean hasChanged = false;
		final ListIterator<E> iterator = listIterator();
		if (iterator.hasNext()) {
			E previousInterval = iterator.next();
			while (iterator.hasNext()) {
				final E interval = iterator.next();
				if (previousInterval.isInside(interval) || previousInterval.merge(interval) ||
						interval.merge(previousInterval)) {
					hasChanged = true;
					iterator.remove();
				} else {
					previousInterval = interval;
				}
			}
		}
		return hasChanged;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the empty {@link Interval}.
	 * <p>
	 * @return {@code true} if {@code this} has changed as a result of the call, {@code false}
	 *         otherwise
	 */
	public boolean removeEmpty() {
		boolean hasChanged = false;
		final ListIterator<E> iterator = listIterator();
		while (iterator.hasNext()) {
			if (iterator.next().isEmpty()) {
				hasChanged = true;
				iterator.remove();
			}
		}
		return hasChanged;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GROUP
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} contains the specified {@code T} object.
	 * <p>
	 * @param object the {@code T} object to test for membership (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} contains the specified {@code T} object, {@code false}
	 *         otherwise
	 */
	public boolean isInside(final T object) {
		for (final E interval : this) {
			if (Comparables.isLessThan(object, interval.lowerBound.value)) {
				return false;
			}
			if (interval.isInside(object)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether {@code this} contains the specified {@link Interval}.
	 * <p>
	 * @param interval the {@link Interval} of {@code T} type to test for membership (may be
	 *                 {@code null})
	 * <p>
	 * @return {@code true} if {@code this} contains the specified {@link Interval}, {@code false}
	 *         otherwise
	 */
	public boolean isInside(final Interval<T> interval) {
		for (final E i : this) {
			if (Comparables.isLessThan(interval.lowerBound.value, i.lowerBound.value)) {
				return false;
			}
			if (i.isInside(interval)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests whether {@code this} contains the specified {@link GenericIntervalList}.
	 * <p>
	 * @param intervalList the {@link GenericIntervalList} of {@code E} and {@code T} types to test
	 *                     for membership (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} contains the specified {@link GenericIntervalList},
	 *         {@code false} otherwise
	 */
	public boolean isInside(final GenericIntervalList<E, T> intervalList) {
		for (final E interval : intervalList) {
			if (!isInside(interval)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests whether {@code this} is valid.
	 * <p>
	 * @return {@code true} if {@code this} is valid, {@code false} otherwise
	 */
	public boolean isValid() {
		for (final E interval : this) {
			if (!interval.isValid()) {
				return false;
			}
		}
		return isNonEmpty();
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
	public GenericIntervalList<E, T> clone() {
		final GenericIntervalList<E, T> clone = new GenericIntervalList<E, T>();
		for (final E element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}
}
