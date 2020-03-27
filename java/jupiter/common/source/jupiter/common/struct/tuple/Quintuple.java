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
package jupiter.common.struct.tuple;

import java.io.Serializable;

import jupiter.common.math.ITuple;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class Quintuple<T1, T2, T3, T4, T5>
		implements ICloneable<Quintuple<T1, T2, T3, T4, T5>>, ITuple, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@code T1} component.
	 */
	protected T1 first;
	/**
	 * The {@code T2} component.
	 */
	protected T2 second;
	/**
	 * The {@code T3} component.
	 */
	protected T3 third;
	/**
	 * The {@code T4} component.
	 */
	protected T4 fourth;
	/**
	 * The {@code T5} component.
	 */
	protected T5 fifth;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Quintuple}.
	 */
	public Quintuple() {
	}

	/**
	 * Constructs a {@link Quintuple} with the specified {@code T1}, {@code T2}, {@code T3},
	 * {@code T4} and {@code T5} components.
	 * <p>
	 * @param first  the {@code T1} component
	 * @param second the {@code T2} component
	 * @param third  the {@code T3} component
	 * @param fourth the {@code T4} component
	 * @param fifth  the {@code T5} component
	 */
	public Quintuple(final T1 first, final T2 second, final T3 third, final T4 fourth,
			final T5 fifth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code T1} component.
	 * <p>
	 * @return the {@code T1} component
	 */
	public T1 getFirst() {
		return first;
	}

	/**
	 * Returns the {@code T2} component.
	 * <p>
	 * @return the {@code T2} component
	 */
	public T2 getSecond() {
		return second;
	}

	/**
	 * Returns the {@code T3} component.
	 * <p>
	 * @return the {@code T3} component
	 */
	public T3 getThird() {
		return third;
	}

	/**
	 * Returns the {@code T4} component.
	 * <p>
	 * @return the {@code T4} component
	 */
	public T4 getFourth() {
		return fourth;
	}

	/**
	 * Returns the {@code T5} component.
	 * <p>
	 * @return the {@code T5} component
	 */
	public T5 getFifth() {
		return fifth;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@code T1} component.
	 * <p>
	 * @param first a {@code T1} object
	 */
	public void setFirst(final T1 first) {
		this.first = first;
	}

	/**
	 * Sets the {@code T2} component.
	 * <p>
	 * @param second a {@code T2} object
	 */
	public void setSecond(final T2 second) {
		this.second = second;
	}

	/**
	 * Sets the {@code T3} component.
	 * <p>
	 * @param third a {@code T3} object
	 */
	public void setThird(final T3 third) {
		this.third = third;
	}

	/**
	 * Sets the {@code T4} component.
	 * <p>
	 * @param fourth a {@code T4} object
	 */
	public void setFourth(final T4 fourth) {
		this.fourth = fourth;
	}

	/**
	 * Sets the {@code T5} component.
	 * <p>
	 * @param fifth a {@code T5} object
	 */
	public void setFifth(final T5 fifth) {
		this.fifth = fifth;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the size of {@code this}.
	 * <p>
	 * @return the size of {@code this}
	 */
	@Override
	public int size() {
		return 5;
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
	@SuppressWarnings({"cast", "unchecked"})
	public Quintuple<T1, T2, T3, T4, T5> clone() {
		try {
			final Quintuple<T1, T2, T3, T4, T5> clone = (Quintuple<T1, T2, T3, T4, T5>) super.clone();
			clone.first = Objects.clone(first);
			clone.second = Objects.clone(second);
			clone.third = Objects.clone(third);
			clone.fourth = Objects.clone(fourth);
			clone.fifth = Objects.clone(fifth);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Quintuple)) {
			return false;
		}
		final Quintuple<?, ?, ?, ?, ?> otherQuintuple = (Quintuple<?, ?, ?, ?, ?>) other;
		return Objects.equals(first, otherQuintuple.first) &&
				Objects.equals(second, otherQuintuple.second) &&
				Objects.equals(third, otherQuintuple.third) &&
				Objects.equals(fourth, otherQuintuple.fourth) &&
				Objects.equals(fifth, otherQuintuple.fifth);
	}

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, first, second, third, fourth, fifth);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Arrays.toString(first, second, third, fourth, fifth);
	}
}
