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

import java.util.Collection;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

/**
 * {@link IntervalList} is the {@link GenericIntervalList} of {@link Interval} of {@code T} type.
 * <p>
 * @param <T> the self {@link Comparable} type of the {@link Interval}
 */
public class IntervalList<T extends Comparable<? super T>>
		extends GenericIntervalList<Interval<T>, T> {

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
	 * Constructs an empty {@link IntervalList} of {@code T} type.
	 */
	public IntervalList() {
		super();
	}

	//////////////////////////////////////////////

	/**
	 * Constructs an {@link IntervalList} of {@code T} type with the specified elements.
	 * <p>
	 * @param elements an array of {@link Interval} of {@code T} type
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public IntervalList(final Interval<T>... elements) {
		super(elements);
	}

	/**
	 * Constructs an {@link IntervalList} of {@code T} type with the elements of the specified
	 * {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@link Interval} of {@code T} type
	 */
	public IntervalList(final Collection<? extends Interval<T>> elements) {
		super(elements);
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
	public IntervalList<T> clone() {
		final IntervalList<T> clone = new IntervalList<T>();
		for (final Interval<T> element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}
}
