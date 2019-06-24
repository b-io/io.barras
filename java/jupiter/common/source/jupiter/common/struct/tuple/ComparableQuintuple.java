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

import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;

public class ComparableQuintuple<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>>
		implements Cloneable, Comparable<ComparableQuintuple<T1, T2, T3, T4, T5>>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 980509932686150429L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The first component.
	 */
	protected T1 first;
	/**
	 * The second component.
	 */
	protected T2 second;
	/**
	 * The third component.
	 */
	protected T3 third;
	/**
	 * The fourth component.
	 */
	protected T4 fourth;
	/**
	 * The fifth component.
	 */
	protected T5 fifth;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public ComparableQuintuple() {
	}

	public ComparableQuintuple(final T1 first, final T2 second, final T3 third, final T4 fourth,
			final T5 fifth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the first component.
	 * <p>
	 * @return the first component
	 */
	public T1 getFirst() {
		return first;
	}

	/**
	 * Returns the second component.
	 * <p>
	 * @return the second component
	 */
	public T2 getSecond() {
		return second;
	}

	/**
	 * Returns the third component.
	 * <p>
	 * @return the third component
	 */
	public T3 getThird() {
		return third;
	}

	/**
	 * Returns the fourth component.
	 * <p>
	 * @return the fourth component
	 */
	public T4 getFourth() {
		return fourth;
	}

	/**
	 * Returns the fifth component.
	 * <p>
	 * @return the fifth component
	 */
	public T5 getFifth() {
		return fifth;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the first component.
	 * <p>
	 * @param first a {@code T1} object
	 */
	public void setFirst(final T1 first) {
		this.first = first;
	}

	/**
	 * Sets the second component.
	 * <p>
	 * @param second a {@code T2} object
	 */
	public void setSecond(final T2 second) {
		this.second = second;
	}

	/**
	 * Sets the third component.
	 * <p>
	 * @param third a {@code T3} object
	 */
	public void setThird(final T3 third) {
		this.third = third;
	}

	/**
	 * Sets the fourth component.
	 * <p>
	 * @param fourth a {@code T4} object
	 */
	public void setFourth(final T4 fourth) {
		this.fourth = fourth;
	}

	/**
	 * Sets the fifth component.
	 * <p>
	 * @param fifth a {@code T5} object
	 */
	public void setFifth(final T5 fifth) {
		this.fifth = fifth;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code quintuple} for order. Returns a negative integer, zero or a
	 * positive integer as {@code this} is less than, equal to or greater than {@code quintuple}.
	 * <p>
	 * @param quintuple the {@link ComparablePair} of type {@code T1}, {@code T2}, {@code T3},
	 *                  {@code T4} and {@code T5} to compare against for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code quintuple}
	 * <p>
	 * @throws NullPointerException if {@code quintuple} is {@code null}
	 */
	public int compareTo(final ComparableQuintuple<T1, T2, T3, T4, T5> quintuple) {
		int comparison = first.compareTo(quintuple.first);
		if (comparison != 0) {
			return comparison;
		}
		comparison = second.compareTo(quintuple.second);
		if (comparison != 0) {
			return comparison;
		}
		comparison = third.compareTo(quintuple.third);
		if (comparison != 0) {
			return comparison;
		}
		comparison = fourth.compareTo(quintuple.fourth);
		if (comparison != 0) {
			return comparison;
		}
		return fifth.compareTo(quintuple.fifth);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@SuppressWarnings("unchecked")
	public ComparableQuintuple<T1, T2, T3, T4, T5> clone()
			throws CloneNotSupportedException {
		final ComparableQuintuple<T1, T2, T3, T4, T5> clone = (ComparableQuintuple<T1, T2, T3, T4, T5>) super.clone();
		clone.first = Objects.clone(first);
		clone.second = Objects.clone(second);
		clone.third = Objects.clone(third);
		clone.fourth = Objects.clone(fourth);
		clone.fifth = Objects.clone(fifth);
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof ComparableQuintuple)) {
			return false;
		}
		final ComparableQuintuple<?, ?, ?, ?, ?> otherQuintuple = (ComparableQuintuple) other;
		return Objects.equals(first, otherQuintuple.first) &&
				Objects.equals(second, otherQuintuple.second) &&
				Objects.equals(third, otherQuintuple.third) &&
				Objects.equals(fourth, otherQuintuple.fourth) &&
				Objects.equals(fifth, otherQuintuple.fifth);
	}

	/**
	 * Returns the hash code {@code int} value for {@code this}.
	 * <p>
	 * @return the hash code {@code int} value for {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	@SuppressWarnings("unchecked")
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
