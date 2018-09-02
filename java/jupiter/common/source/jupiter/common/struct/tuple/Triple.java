/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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

public class Triple<T1, T2, T3>
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 7006401742978894167L;


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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Triple() {
	}

	public Triple(final T1 first, final T2 second, final T3 third) {
		this.first = first;
		this.second = second;
		this.third = third;
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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Triple)) {
			return false;
		}
		final Triple<?, ?, ?> otherTriple = (Triple) other;
		return Objects.equals(first, otherTriple.first) &&
				Objects.equals(second, otherTriple.second) &&
				Objects.equals(third, otherTriple.third);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, first, second, third);
	}

	@Override
	public String toString() {
		return Arrays.toString(first, second, third);
	}
}
