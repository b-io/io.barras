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

import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Numbers;
import jupiter.common.util.Objects;
import jupiter.common.util.Shorts;
import jupiter.common.util.Strings;

public class WholeNumber
		extends ComparableNumber {

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
	 * The {@code long} value.
	 */
	protected long value;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link WholeNumber} with the specified value.
	 * <p>
	 * @param value the {@code long} value
	 */
	public WholeNumber(final long value) {
		super();
		this.value = value;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code long} value.
	 * <p>
	 * @return the {@code long} value
	 */
	public long get() {
		return value;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@code long} value.
	 * <p>
	 * @param value a {@code long} value
	 */
	public void set(final long value) {
		this.value = value;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of {@code this} rounded down.
	 * <p>
	 * @return the middle of {@code this} rounded down
	 */
	public long middle() {
		return Longs.middle(value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the greatest common divisor (GCD) of {@code this} and the specified {@link Number}.
	 * <p>
	 * @param number a {@link Number}
	 * <p>
	 * @return the greatest common divisor (GCD) of {@code this} and the specified {@link Number}
	 */
	public long gcd(final Number number) {
		return Maths.gcd(value, number.longValue());
	}

	/**
	 * Returns the least common multiple (LCM) of {@code this} and the specified {@link Number}.
	 * <p>
	 * @param number a {@link Number}
	 * <p>
	 * @return the least common multiple (LCM) of {@code this} and the specified {@link Number}
	 */
	public long lcm(final Number number) {
		return Maths.lcm(value, number.longValue());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// NUMBER
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public short shortValue() {
		return Shorts.convert(value);
	}

	@Override
	public int intValue() {
		return Integers.convert(value);
	}

	@Override
	public long longValue() {
		return value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public double doubleValue() {
		return value;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
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
	 *         equal to or greater than {@code other} (with {@code null} considered as the minimum
	 *         value)
	 */
	@Override
	public int compareTo(final ComparableNumber other) {
		if (other instanceof WholeNumber) {
			return Longs.compare(value, ((WholeNumber) other).value);
		}
		return Numbers.compare(value, other);
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
	public WholeNumber clone() {
		return (WholeNumber) super.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the {@code other} type prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		return super.equals(other);
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
		return Objects.hashCode(serialVersionUID, value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Strings.toString(value);
	}
}
