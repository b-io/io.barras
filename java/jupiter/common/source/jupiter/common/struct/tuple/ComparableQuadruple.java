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

public class ComparableQuadruple<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
		implements Cloneable, Comparable<ComparableQuadruple<T1, T2, T3, T4>>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 3768409760250647332L;


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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public ComparableQuadruple() {
	}

	public ComparableQuadruple(final T1 first, final T2 second, final T3 third, final T4 fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public int compareTo(final ComparableQuadruple<T1, T2, T3, T4> quadruple) {
		int comparison = first.compareTo(quadruple.first);
		if (comparison != 0) {
			return comparison;
		}
		comparison = second.compareTo(quadruple.second);
		if (comparison != 0) {
			return comparison;
		}
		comparison = third.compareTo(quadruple.third);
		if (comparison != 0) {
			return comparison;
		}
		return fourth.compareTo(quadruple.fourth);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@SuppressWarnings("unchecked")
	public ComparableQuadruple<T1, T2, T3, T4> clone()
			throws CloneNotSupportedException {
		final ComparableQuadruple<T1, T2, T3, T4> clone = (ComparableQuadruple<T1, T2, T3, T4>) super.clone();
		clone.first = Objects.clone(first);
		clone.second = Objects.clone(second);
		clone.third = Objects.clone(third);
		clone.fourth = Objects.clone(fourth);
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof ComparableQuadruple)) {
			return false;
		}
		final ComparableQuadruple<?, ?, ?, ?> otherQuadruple = (ComparableQuadruple) other;
		return Objects.equals(first, otherQuadruple.first) &&
				Objects.equals(second, otherQuadruple.second) &&
				Objects.equals(third, otherQuadruple.third) &&
				Objects.equals(fourth, otherQuadruple.fourth);
	}

	@Override
	@SuppressWarnings("unchecked")
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, first, second, third, fourth);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return Arrays.toString(first, second, third, fourth);
	}
}
