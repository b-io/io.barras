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
package jupiter.common.struct.tuple;

import java.io.Serializable;

import jupiter.common.math.ITuple;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class Quintuple<E1, E2, E3, E4, E5>
		implements ICloneable<Quintuple<E1, E2, E3, E4, E5>>, ITuple, Serializable {

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
	 * The {@code E1} component.
	 */
	protected E1 first;
	/**
	 * The {@code E2} component.
	 */
	protected E2 second;
	/**
	 * The {@code E3} component.
	 */
	protected E3 third;
	/**
	 * The {@code E4} component.
	 */
	protected E4 fourth;
	/**
	 * The {@code E5} component.
	 */
	protected E5 fifth;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Quintuple}.
	 */
	public Quintuple() {
	}

	/**
	 * Constructs a {@link Quintuple} with the specified {@code E1}, {@code E2}, {@code E3},
	 * {@code E4} and {@code E5} components.
	 * <p>
	 * @param first  the {@code E1} component
	 * @param second the {@code E2} component
	 * @param third  the {@code E3} component
	 * @param fourth the {@code E4} component
	 * @param fifth  the {@code E5} component
	 */
	public Quintuple(final E1 first, final E2 second, final E3 third, final E4 fourth,
			final E5 fifth) {
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
	 * Returns the {@code E1} component.
	 * <p>
	 * @return the {@code E1} component
	 */
	public E1 getFirst() {
		return first;
	}

	/**
	 * Returns the {@code E2} component.
	 * <p>
	 * @return the {@code E2} component
	 */
	public E2 getSecond() {
		return second;
	}

	/**
	 * Returns the {@code E3} component.
	 * <p>
	 * @return the {@code E3} component
	 */
	public E3 getThird() {
		return third;
	}

	/**
	 * Returns the {@code E4} component.
	 * <p>
	 * @return the {@code E4} component
	 */
	public E4 getFourth() {
		return fourth;
	}

	/**
	 * Returns the {@code E5} component.
	 * <p>
	 * @return the {@code E5} component
	 */
	public E5 getFifth() {
		return fifth;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@code E1} component.
	 * <p>
	 * @param first an {@code E1} object
	 */
	public void setFirst(final E1 first) {
		this.first = first;
	}

	/**
	 * Sets the {@code E2} component.
	 * <p>
	 * @param second an {@code E2} object
	 */
	public void setSecond(final E2 second) {
		this.second = second;
	}

	/**
	 * Sets the {@code E3} component.
	 * <p>
	 * @param third an {@code E3} object
	 */
	public void setThird(final E3 third) {
		this.third = third;
	}

	/**
	 * Sets the {@code E4} component.
	 * <p>
	 * @param fourth an {@code E4} object
	 */
	public void setFourth(final E4 fourth) {
		this.fourth = fourth;
	}

	/**
	 * Sets the {@code E5} component.
	 * <p>
	 * @param fifth an {@code E5} object
	 */
	public void setFifth(final E5 fifth) {
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
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Quintuple<E1, E2, E3, E4, E5> clone() {
		try {
			final Quintuple<E1, E2, E3, E4, E5> clone = (Quintuple<E1, E2, E3, E4, E5>) super.clone();
			clone.first = Objects.clone(first);
			clone.second = Objects.clone(second);
			clone.third = Objects.clone(third);
			clone.fourth = Objects.clone(fourth);
			clone.fifth = Objects.clone(fifth);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
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
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
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
