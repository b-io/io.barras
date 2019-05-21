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
	private static final long serialVersionUID = 4962308765948283486L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected long value;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public WholeNumber(final long value) {
		this.value = value;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
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

	/**
	 * Sets the value.
	 * <p>
	 * @param value a {@code long} value
	 */
	public void set(final long value) {
		this.value = value;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the middle of the {@code long} value rounded to the lower {@code long} value.
	 * <p>
	 * @return the middle of the {@code long} value rounded to the lower {@code long} value
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
	@Override
	public int compareTo(final ComparableNumber comparableNumber) {
		if (comparableNumber instanceof WholeNumber) {
			return Longs.compare(value, ((WholeNumber) comparableNumber).value);
		}
		return Numbers.compare(value, comparableNumber);
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
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, value);
	}

	@Override
	public String toString() {
		return Strings.toString(value);
	}
}
