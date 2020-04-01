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

import jupiter.common.model.ICloneable;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.util.Doubles;

/**
 * {@link DoubleInterval} is the {@link Interval} of {@link Double}.
 */
public class DoubleInterval
		extends Interval<Double>
		implements IDoubleInterval {

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
	 * Constructs an empty {@link DoubleInterval} by default.
	 */
	public DoubleInterval() {
		super();
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link DoubleInterval} with the specified values of the {@link LowerBound} and
	 * {@link UpperBound}.
	 * <p>
	 * @param lowerBoundValue the {@link Double} of the {@link LowerBound} (inclusive) (may be
	 *                        {@code null})
	 * @param upperBoundValue the {@link Double} of the {@link UpperBound} (inclusive) (may be
	 *                        {@code null})
	 */
	public DoubleInterval(final Double lowerBoundValue, final Double upperBoundValue) {
		super(lowerBoundValue, upperBoundValue);
	}

	/**
	 * Constructs a {@link DoubleInterval} with the specified {@link LowerBound} and
	 * {@link UpperBound}.
	 * <p>
	 * @param lowerBound the {@link LowerBound} of {@link Double}
	 * @param upperBound the {@link UpperBound} of {@link Double}
	 */
	public DoubleInterval(final LowerBound<Double> lowerBound,
			final UpperBound<Double> upperBound) {
		super(lowerBound, upperBound);
	}

	/**
	 * Constructs a {@link DoubleInterval} with the specified {@link Pair} of {@link LowerBound} and
	 * {@link UpperBound}.
	 * <p>
	 * @param pair the {@link Pair} of {@link LowerBound} and {@link UpperBound} of {@link Double}
	 */
	public DoubleInterval(final Pair<LowerBound<Double>, UpperBound<Double>> pair) {
		super(pair);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the closest {@link LowerBound} or {@link UpperBound} to the specified value, or
	 * {@code null} if {@code this} does not contain any finite {@link Bound}.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the closest {@link LowerBound} or {@link UpperBound} to the specified value, or
	 *         {@code null} if {@code this} does not contain any finite {@link Bound}
	 */
	public Bound<Double> getClosestBound(final Double value) {
		Bound<Double> bound = null;
		// • The lower bound
		if (lowerBound.value != null) {
			bound = lowerBound;
		}
		// • The upper bound
		if (upperBound.value != null && getUpperDistance(value) < getLowerDistance(value)) {
			bound = upperBound;
		}
		return bound;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the distance to the specified value.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the distance to the specified value
	 */
	public double getDistance(final Double value) {
		if (isInside(value)) {
			return 0.;
		}
		double distance = Double.POSITIVE_INFINITY;
		// • The lower bound
		if (lowerBound.value != null) {
			distance = getLowerDistance(value);
		}
		// • The upper bound
		if (upperBound.value != null) {
			final double d = getUpperDistance(value);
			if (d < distance) {
				distance = d;
			}
		}
		return distance;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the distance between the {@link LowerBound} and the specified value.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the distance between the {@link LowerBound} and the specified value
	 */
	public double getLowerDistance(final Double value) {
		return value != null ? Maths.delta(value, getLowerBoundValue()) : Double.POSITIVE_INFINITY;
	}

	/**
	 * Returns the distance between the {@link UpperBound} and the specified value.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the distance between the {@link UpperBound} and the specified value
	 */
	public double getUpperDistance(final Double value) {
		return value != null ? Maths.delta(value, getUpperBoundValue()) : Double.POSITIVE_INFINITY;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code double} value of the {@link LowerBound}.
	 * <p>
	 * @return the {@code double} value of the {@link LowerBound}
	 */
	public double getLowerBoundValue() {
		return getLowerBoundValue(Maths.TINY_TOLERANCE);
	}

	/**
	 * Returns the {@code double} value of the {@link LowerBound} using the specified minimal
	 * interval.
	 * <p>
	 * @param step the {@code double} minimal interval
	 * <p>
	 * @return the {@code double} value of the {@link LowerBound} using the specified minimal
	 *         interval
	 */
	public double getLowerBoundValue(final double step) {
		return lowerBound.value != null ?
				lowerBound.isInclusive ?
						lowerBound.value :
						lowerBound.value + step :
				Double.NEGATIVE_INFINITY;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@code double} value of the {@link UpperBound}.
	 * <p>
	 * @return the {@code double} value of the {@link UpperBound}
	 */
	public double getUpperBoundValue() {
		return getUpperBoundValue(Maths.TINY_TOLERANCE);
	}

	/**
	 * Returns the {@code double} value of the {@link UpperBound} using the specified minimal
	 * interval.
	 * <p>
	 * @param step the {@code double} minimal interval
	 * <p>
	 * @return the {@code double} value of the {@link UpperBound} using the specified minimal
	 *         interval
	 */
	public double getUpperBoundValue(final double step) {
		return upperBound.value != null ?
				upperBound.isInclusive ?
						upperBound.value :
						upperBound.value - step :
				Double.POSITIVE_INFINITY;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code value} if {@code value} is inside {@code this}, the value of the closest
	 * {@link Bound} if {@code value} is non-{@code null} and not {@code NaN}, {@code NaN}
	 * otherwise.
	 * <p>
	 * @param value the {@link Double} to bound (may be {@code null})
	 * <p>
	 * @return {@code value} if {@code value} is inside {@code this}, the value of the closest
	 *         {@link Bound} if {@code value} is non-{@code null} and not {@code NaN}, {@code NaN}
	 *         otherwise
	 */
	public double bound(final Double value) {
		return Intervals.bound(this, value);
	}

	/**
	 * Returns {@code value} if {@code value} is inside {@code this}, {@code NaN} otherwise.
	 * <p>
	 * @param value the {@link Double} to constrain (may be {@code null})
	 * <p>
	 * @return {@code value} if {@code value} is inside {@code this}, {@code NaN} otherwise
	 */
	@Override
	public Double constrain(final Double value) {
		return Intervals.constrain(this, value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is finite.
	 * <p>
	 * @return {@code true} if {@code this} is finite, {@code false} otherwise
	 */
	public boolean isFinite() {
		return Doubles.isFinite(lowerBound.getValue()) &&
				Doubles.isFinite(upperBound.getValue());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public DoubleInterval clone() {
		return (DoubleInterval) super.clone();
	}
}
