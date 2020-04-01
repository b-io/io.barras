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

public class Intervals {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Intervals}.
	 */
	protected Intervals() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an enlarged {@link DoubleInterval} from the specified {@link DoubleInterval} in the
	 * specified {@link Domain} with the specified enlargement factor, sample size and interval
	 * between the sampling points.
	 * <p>
	 * @param interval   the {@link DoubleInterval} to enlarge
	 * @param domain     the {@link Domain}
	 * @param factor     the enlargement factor
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @return an enlarged {@link DoubleInterval} from the specified {@link DoubleInterval} in the
	 *         specified {@link Domain} with the specified enlargement factor, sample size and
	 *         interval between the sampling points
	 */
	public static DoubleInterval createEnlargedInterval(final DoubleInterval interval,
			final Domain domain, final int factor, final int sampleSize, final double step) {
		final double halfSampleSpan = (sampleSize - 1) * step / 2.;
		DoubleInterval enlargedInterval = new DoubleInterval(
				interval.getLowerBoundValue() - factor * halfSampleSpan,
				interval.getUpperBoundValue() + factor * halfSampleSpan);
		if (!domain.isInside(enlargedInterval)) {
			enlargedInterval = interval;
		}
		return enlargedInterval;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code value} if {@code value} is inside the specified {@link IDoubleInterval}, the
	 * value of the closest {@link Bound} if {@code value} is non-{@code null} and not {@code NaN},
	 * {@code NaN} otherwise.
	 * <p>
	 * @param interval an {@link IDoubleInterval}
	 * @param value    the {@link Double} to bound (may be {@code null})
	 * <p>
	 * @return {@code value} if {@code value} is inside the specified {@link IDoubleInterval}, the
	 *         value of the closest {@link Bound} if {@code value} is non-{@code null} and not
	 *         {@code NaN}, {@code NaN} otherwise
	 */
	public static double bound(final IDoubleInterval interval, final Double value) {
		// Check the arguments
		if (value == null) {
			IO.warn("The specified double number is null");
			return Double.NaN;
		}
		if (Double.isNaN(value)) {
			IO.warn("The specified double number is NaN");
			return Double.NaN;
		}
		if (interval.isEmpty()) {
			IO.warn("The interval is empty");
			return Double.NaN;
		}

		// Bound the value
		if (interval.isInside(value)) {
			return value;
		}
		final Bound<Double> bound = interval.getClosestBound(value);
		if (bound != null) {
			return bound.isInclusive ? bound.value :
					bound instanceof LowerBound ?
							bound.value + Maths.TINY_TOLERANCE :
							bound.value - Maths.TINY_TOLERANCE;
		}
		return Double.NaN;
	}

	/**
	 * Returns {@code value} if {@code value} is inside the specified
	 * {@link IDoubleInterval}, {@code NaN} otherwise.
	 * <p>
	 * @param interval an {@link IDoubleInterval}
	 * @param value    the {@link Double} to constrain (may be {@code null})
	 * <p>
	 * @return {@code value} if {@code value} is inside the specified
	 *         {@link IDoubleInterval}, {@code NaN} otherwise
	 */
	public static Double constrain(final IDoubleInterval interval, final Double value) {
		// Check the arguments
		if (value == null) {
			IO.warn("The specified double number is null");
			return Double.NaN;
		}
		if (Double.isNaN(value)) {
			IO.warn("The specified double number is NaN");
			return Double.NaN;
		}
		if (interval.isEmpty()) {
			IO.warn("The interval is empty");
			return Double.NaN;
		}

		// Constrain the value
		if (!interval.isInside(value)) {
			IO.warn("The specified double number ", value, " is not inside the interval ",
					interval);
			return Double.NaN;
		}
		return value;
	}
}
