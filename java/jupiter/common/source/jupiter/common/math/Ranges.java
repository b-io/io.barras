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

import static jupiter.common.io.IO.IO;

public class Ranges {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Ranges}.
	 */
	protected Ranges() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code value} if {@code value} is inside the specified {@link IRange}, the value of
	 * the closest {@link Bound} if {@code value} is non-{@code null} and not {@code NaN},
	 * {@code NaN} otherwise.
	 * <p>
	 * @param range an {@link IRange}
	 * @param value the {@link Double} to bound (may be {@code null})
	 * <p>
	 * @return {@code value} if {@code value} is inside the specified {@link IRange}, the value of
	 *         the closest {@link Bound} if {@code value} is non-{@code null} and not {@code NaN},
	 *         {@code NaN} otherwise
	 */
	public static double bound(final IRange range, final Double value) {
		// Check the arguments
		if (value == null) {
			IO.warn("The specified double number is null");
			return Double.NaN;
		}
		if (Double.isNaN(value)) {
			IO.warn("The specified double number is NaN");
			return Double.NaN;
		}
		if (range.isEmpty()) {
			IO.warn("The range is empty");
			return Double.NaN;
		}

		// Bound the value
		if (range.isInside(value)) {
			return value;
		}
		final Bound<Double> bound = range.getClosestBound(value);
		if (bound != null) {
			return bound.isInclusive ? bound.value :
					bound instanceof LowerBound ?
							bound.value + Maths.TINY_TOLERANCE :
							bound.value - Maths.TINY_TOLERANCE;
		}
		return Double.NaN;
	}

	/**
	 * Returns {@code value} if {@code value} is inside the specified {@link IRange}, {@code NaN}
	 * otherwise.
	 * <p>
	 * @param range an {@link IRange}
	 * @param value the {@link Double} to constrain (may be {@code null})
	 * <p>
	 * @return {@code value} if {@code value} is inside the specified {@link IRange}, {@code NaN}
	 *         otherwise
	 */
	public static Double constrain(final IRange range, final Double value) {
		// Check the arguments
		if (value == null) {
			IO.warn("The specified double number is null");
			return Double.NaN;
		}
		if (Double.isNaN(value)) {
			IO.warn("The specified double number is NaN");
			return Double.NaN;
		}
		if (range.isEmpty()) {
			IO.warn("The range is empty");
			return Double.NaN;
		}

		// Constrain the value
		if (!range.isInside(value)) {
			IO.warn("The specified double number ", value, " is not inside the range ", range);
			return Double.NaN;
		}
		return value;
	}
}
