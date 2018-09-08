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
package jupiter.common.math;

import jupiter.common.util.Doubles;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public abstract class ComparableNumber
		extends Number
		implements IComparable<ComparableNumber> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 430434450678720415L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected ComparableNumber() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARABLE NUMBER COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code comparableNumber} for order. Returns a negative integer,
	 * zero or a positive integer as {@code this} is less than, equal to or greater than
	 * {@code comparableNumber}.
	 * <p>
	 * @param comparableNumber the {@link ComparableNumber} to compare with for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code comparableNumber}
	 * <p>
	 * @throws NullPointerException if {@code comparableNumber} is {@code null}
	 */
	public int compareTo(final ComparableNumber comparableNumber) {
		return Doubles.compare(doubleValue(), comparableNumber.doubleValue());
	}

	/**
	 * Tests whether {@code this} is less than {@code comparableNumber}.
	 * <p>
	 * @param comparableNumber the {@link ComparableNumber} to compare with
	 * <p>
	 * @return {@code true} if {@code this} is less than {@code comparableNumber}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws NullPointerException if {@code comparableNumber} is {@code null}
	 */
	public boolean isLessThan(final ComparableNumber comparableNumber) {
		return compareTo(comparableNumber) < 0;
	}

	/**
	 * Tests whether {@code this} is less or equal to {@code comparableNumber}.
	 * <p>
	 * @param comparableNumber the {@link ComparableNumber} to compare with
	 * <p>
	 * @return {@code true} if {@code this} is less or equal to {@code comparableNumber},
	 *         {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code comparableNumber} is {@code null}
	 */
	public boolean isLessOrEqualTo(final ComparableNumber comparableNumber) {
		return compareTo(comparableNumber) <= 0;
	}

	/**
	 * Tests whether {@code this} is greater than {@code comparableNumber}.
	 * <p>
	 * @param comparableNumber the {@link ComparableNumber} to compare with
	 * <p>
	 * @return {@code true} if {@code this} is greater than {@code comparableNumber}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws NullPointerException if {@code comparableNumber} is {@code null}
	 */
	public boolean isGreaterThan(final ComparableNumber comparableNumber) {
		return compareTo(comparableNumber) > 0;
	}

	/**
	 * Tests whether {@code this} is greater or equal to {@code comparableNumber}.
	 * <p>
	 * @param comparableNumber the {@link ComparableNumber} to compare with
	 * <p>
	 * @return {@code true} if {@code this} is greater or equal to {@code comparableNumber},
	 *         {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code comparableNumber} is {@code null}
	 */
	public boolean isGreaterOrEqualTo(final ComparableNumber comparableNumber) {
		return compareTo(comparableNumber) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code this} and {@code comparableNumber}, or {@code this} if they are
	 * equal.
	 * <p>
	 * @param comparableNumber the {@link ComparableNumber} to compare with
	 * <p>
	 * @return the smaller of {@code this} and {@code comparableNumber}, or {@code this} if they are
	 *         equal
	 * <p>
	 * @throws NullPointerException if {@code comparableNumber} is {@code null}
	 */
	public ComparableNumber getMin(final ComparableNumber comparableNumber) {
		return isLessOrEqualTo(comparableNumber) ? this : comparableNumber;
	}

	/**
	 * Returns the larger of {@code this} and {@code comparableNumber}, or {@code this} if they are
	 * equal.
	 * <p>
	 * @param comparableNumber the {@link ComparableNumber} to compare with
	 * <p>
	 * @return the larger of {@code this} and {@code comparableNumber}, or {@code this} if they are
	 *         equal
	 * <p>
	 * @throws NullPointerException if {@code comparableNumber} is {@code null}
	 */
	public ComparableNumber getMax(final ComparableNumber comparableNumber) {
		return isGreaterOrEqualTo(comparableNumber) ? this : comparableNumber;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// NUMBER COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares {@code this} with {@code number} for order. Returns a negative integer, zero or a
	 * positive integer as {@code this} is less than, equal to or greater than {@code number}.
	 * <p>
	 * @param number the {@link Number} to compare with for order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code this} is less than, equal to
	 *         or greater than {@code number}
	 * <p>
	 * @throws NullPointerException if {@code number} is {@code null}
	 */
	public int compareTo(final Number number) {
		return Numbers.compare(this, number);
	}

	/**
	 * Tests whether {@code this} is less than {@code number}.
	 * <p>
	 * @param number the {@link Number} to compare with
	 * <p>
	 * @return {@code true} if {@code this} is less than {@code number}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code number} is {@code null}
	 */
	public boolean isLessThan(final Number number) {
		return compareTo(number) < 0;
	}

	/**
	 * Tests whether {@code this} is less or equal to {@code number}.
	 * <p>
	 * @param number the {@link Number} to compare with
	 * <p>
	 * @return {@code true} if {@code this} is less or equal to {@code number}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws NullPointerException if {@code number} is {@code null}
	 */
	public boolean isLessOrEqualTo(final Number number) {
		return compareTo(number) <= 0;
	}

	/**
	 * Tests whether {@code this} is greater than {@code number}.
	 * <p>
	 * @param number the {@link Number} to compare with
	 * <p>
	 * @return {@code true} if {@code this} is greater than {@code number}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code number} is {@code null}
	 */
	public boolean isGreaterThan(final Number number) {
		return compareTo(number) > 0;
	}

	/**
	 * Tests whether {@code this} is greater or equal to {@code number}.
	 * <p>
	 * @param number the {@link Number} to compare with
	 * <p>
	 * @return {@code true} if {@code this} is greater or equal to {@code number}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws NullPointerException if {@code number} is {@code null}
	 */
	public boolean isGreaterOrEqualTo(final Number number) {
		return compareTo(number) >= 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the smaller of {@code this} and {@code number}, or {@code this} if they are equal.
	 * <p>
	 * @param number the {@link Number} to compare with
	 * <p>
	 * @return the smaller of {@code this} and {@code number}, or {@code this} if they are equal
	 * <p>
	 * @throws NullPointerException if {@code number} is {@code null}
	 */
	public Number getMin(final Number number) {
		return isLessOrEqualTo(number) ? this : number;
	}

	/**
	 * Returns the larger of {@code this} and {@code number}, or {@code this} if they are equal.
	 * <p>
	 * @param number the {@link Number} to compare with
	 * <p>
	 * @return the larger of {@code this} and {@code number}, or {@code this} if they are equal
	 * <p>
	 * @throws NullPointerException if {@code number} is {@code null}
	 */
	public Number getMax(final Number number) {
		return isGreaterOrEqualTo(number) ? this : number;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the {@link Object} to compare with for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the class of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
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
	 * Tests whether {@code this} is equal to {@code number}.
	 * <p>
	 * @param number the {@link Number} to compare with for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code number}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code number} is {@code null}
	 */
	public boolean equals(final Number number) {
		return Numbers.equals(this, number);
	}

	/**
	 * Tests whether {@code this} is equal to {@code number} within {@code tolerance}.
	 * <p>
	 * @param number    the {@link Number} to compare with for equality
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code number} within {@code tolerance},
	 *         {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code number} is {@code null}
	 */
	public boolean equals(final Number number, final double tolerance) {
		return Numbers.equals(this, number, tolerance);
	}

	/**
	 * Tests whether {@code this} is equal to {@code comparableNumber}.
	 * <p>
	 * @param comparableNumber the {@link ComparableNumber} to compare with for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code comparableNumber}, {@code false}
	 *         otherwise
	 * <p>
	 * @throws NullPointerException if {@code comparableNumber} is {@code null}
	 */
	public boolean equals(final ComparableNumber comparableNumber) {
		return Numbers.equals(this, comparableNumber);
	}

	/**
	 * Tests whether {@code this} is equal to {@code comparableNumber} within {@code tolerance}.
	 * <p>
	 * @param comparableNumber the {@link ComparableNumber} to compare with for equality
	 * @param tolerance        the tolerance level
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code comparableNumber} within
	 *         {@code tolerance}, {@code false} otherwise
	 * <p>
	 * @throws NullPointerException if {@code comparableNumber} is {@code null}
	 */
	public boolean equals(final ComparableNumber comparableNumber, final double tolerance) {
		return Numbers.equals(this, comparableNumber, tolerance);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, doubleValue());
	}

	@Override
	public String toString() {
		return Strings.toString(doubleValue());
	}
}
