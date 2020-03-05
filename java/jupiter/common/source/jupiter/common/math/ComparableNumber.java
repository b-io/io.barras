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

import jupiter.common.model.ICloneable;
import jupiter.common.util.Doubles;
import jupiter.common.util.Numbers;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public abstract class ComparableNumber
		extends Number
		implements ICloneable<ComparableNumber>, IComparable<ComparableNumber> {

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
	 * Constructs a {@link ComparableNumber}.
	 */
	protected ComparableNumber() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARABLE NUMBER COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, {@code 0} or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code other} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link ComparableNumber} to compare against for order (may be
	 *              {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code this} is less than,
	 *         equal to or greater than {@code other}
	 */
	public int compareTo(final ComparableNumber other) {
		return this == other ? 0 : other == null ? 1 :
				Doubles.compare(doubleValue(), other.doubleValue());
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is less than {@code other} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param other the other {@link ComparableNumber} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is less than {@code other}, {@code false} otherwise
	 */
	public boolean isLessThan(final ComparableNumber other) {
		return compareTo(other) < 0;
	}

	/**
	 * Tests whether {@code this} is less or equal to {@code other} (with {@code null} considered as
	 * the minimum value).
	 * <p>
	 * @param other the other {@link ComparableNumber} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is less or equal to {@code other}, {@code false}
	 *         otherwise
	 */
	public boolean isLessOrEqualTo(final ComparableNumber other) {
		return compareTo(other) <= 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is greater than {@code other} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param other the other {@link ComparableNumber} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is greater than {@code other}, {@code false} otherwise
	 */
	public boolean isGreaterThan(final ComparableNumber other) {
		return compareTo(other) > 0;
	}

	/**
	 * Tests whether {@code this} is greater or equal to {@code other} (with {@code null} considered
	 * as the minimum value).
	 * <p>
	 * @param other the other {@link ComparableNumber} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is greater or equal to {@code other}, {@code false}
	 *         otherwise
	 */
	public boolean isGreaterOrEqualTo(final ComparableNumber other) {
		return compareTo(other) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code this} and {@code other}, or {@code this} if they are equal
	 * (with {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link ComparableNumber} to compare against (may be {@code null})
	 * <p>
	 * @return the smaller of {@code this} and {@code other}, or {@code this} if they are equal
	 */
	public ComparableNumber getMin(final ComparableNumber other) {
		return isLessOrEqualTo(other) ? this : other;
	}

	/**
	 * Returns the larger of {@code this} and {@code other}, or {@code this} if they are equal (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link ComparableNumber} to compare against (may be {@code null})
	 * <p>
	 * @return the larger of {@code this} and {@code other}, or {@code this} if they are equal
	 */
	public ComparableNumber getMax(final ComparableNumber other) {
		return isGreaterOrEqualTo(other) ? this : other;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// NUMBER COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code other} for order. Returns a negative integer, {@code 0} or
	 * a positive integer as {@code this} is less than, equal to or greater than {@code other} (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link Number} to compare against for order (may be {@code null})
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code other}
	 */
	public int compareTo(final Number other) {
		return Numbers.compare(this, other);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is less than {@code other} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param other the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is less than {@code other}, {@code false} otherwise
	 */
	public boolean isLessThan(final Number other) {
		return compareTo(other) < 0;
	}

	/**
	 * Tests whether {@code this} is less or equal to {@code other} (with {@code null} considered as
	 * the minimum value).
	 * <p>
	 * @param other the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is less or equal to {@code other}, {@code false}
	 *         otherwise
	 */
	public boolean isLessOrEqualTo(final Number other) {
		return compareTo(other) <= 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is greater than {@code other} (with {@code null} considered as the
	 * minimum value).
	 * <p>
	 * @param other the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is greater than {@code other}, {@code false} otherwise
	 */
	public boolean isGreaterThan(final Number other) {
		return compareTo(other) > 0;
	}

	/**
	 * Tests whether {@code this} is greater or equal to {@code other} (with {@code null} considered
	 * as the minimum value).
	 * <p>
	 * @param other the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is greater or equal to {@code other}, {@code false}
	 *         otherwise
	 */
	public boolean isGreaterOrEqualTo(final Number other) {
		return compareTo(other) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code this} and {@code other}, or {@code this} if they are equal
	 * (with {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return the smaller of {@code this} and {@code other}, or {@code this} if they are equal
	 */
	public Number getMin(final Number other) {
		return isLessOrEqualTo(other) ? this : other;
	}

	/**
	 * Returns the larger of {@code this} and {@code other}, or {@code this} if they are equal (with
	 * {@code null} considered as the minimum value).
	 * <p>
	 * @param other the other {@link Number} to compare against (may be {@code null})
	 * <p>
	 * @return the larger of {@code this} and {@code other}, or {@code this} if they are equal
	 */
	public Number getMax(final Number other) {
		return isGreaterOrEqualTo(other) ? this : other;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public ComparableNumber clone() {
		try {
			return (ComparableNumber) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException if the {@code other} type prevents it from being compared to
	 *                            {@code this}
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof ComparableNumber)) {
			return false;
		}
		return equals((ComparableNumber) other);
	}

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Number} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	public boolean equals(final Number other) {
		return Numbers.equals(this, other);
	}

	/**
	 * Tests whether {@code this} is equal to {@code other} within {@code tolerance}.
	 * <p>
	 * @param other     the other {@link Number} to compare against for equality (may be
	 *                  {@code null})
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other} within {@code tolerance},
	 *         {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	public boolean equals(final Number other, final double tolerance) {
		return Numbers.equals(this, other, tolerance);
	}

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link ComparableNumber} to compare against for equality (may be
	 *              {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	public boolean equals(final ComparableNumber other) {
		return Numbers.equals(this, other);
	}

	/**
	 * Tests whether {@code this} is equal to {@code other} within {@code tolerance}.
	 * <p>
	 * @param other     the other {@link ComparableNumber} to compare against for equality (may be
	 *                  {@code null})
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other} within {@code tolerance},
	 *         {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	public boolean equals(final ComparableNumber other, final double tolerance) {
		return Numbers.equals(this, other, tolerance);
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
		return Objects.hashCode(serialVersionUID, doubleValue());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Strings.toString(doubleValue());
	}
}
