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

import java.util.Collection;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

/**
 * {@link Domain} is the {@link GenericIntervalList} of {@link DoubleInterval} and {@link Double}.
 */
public class Domain
		extends GenericIntervalList<DoubleInterval, Double>
		implements IDoubleInterval {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Domain ALL = new Domain(new DoubleInterval(
			new LowerBound<Double>(Double.NEGATIVE_INFINITY, false),
			new UpperBound<Double>(Double.POSITIVE_INFINITY, false)));

	public static final Domain NEGATIVE = new Domain(new DoubleInterval(
			new LowerBound<Double>(Double.NEGATIVE_INFINITY, false),
			new UpperBound<Double>(0., false)));
	public static final Domain NON_POSITIVE = new Domain(new DoubleInterval(
			new LowerBound<Double>(Double.NEGATIVE_INFINITY, false),
			new UpperBound<Double>(0., true)));

	public static final Domain POSITIVE = new Domain(new DoubleInterval(
			new LowerBound<Double>(0., false),
			new UpperBound<Double>(Double.POSITIVE_INFINITY, false)));
	public static final Domain NON_NEGATIVE = new Domain(new DoubleInterval(
			new LowerBound<Double>(0., true),
			new UpperBound<Double>(Double.POSITIVE_INFINITY, false)));

	public static final Domain NON_ZERO = NEGATIVE.clone().addAll(POSITIVE.clone());


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link Domain}.
	 */
	public Domain() {
		super();
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Domain} with the specified elements.
	 * <p>
	 * @param elements an array of {@link DoubleInterval}
	 */
	public Domain(final DoubleInterval... elements) {
		super(elements);
	}

	/**
	 * Constructs a {@link Domain} with the elements of the specified {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@link DoubleInterval}
	 */
	public Domain(final Collection<? extends DoubleInterval> elements) {
		super(elements);
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
		final DoubleInterval interval = getClosestInterval(value);
		return interval != null ? interval.getClosestBound(value) : null;
	}

	/**
	 * Returns the closest {@link DoubleInterval} to the specified value, or {@code null} if
	 * {@code this} does not contain any finite {@link DoubleInterval}.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the closest {@link DoubleInterval} to the specified value, or {@code null} if
	 *         {@code this} does not contain any finite {@link DoubleInterval}
	 */
	public DoubleInterval getClosestInterval(final Double value) {
		DoubleInterval interval = null;
		double distance = Double.POSITIVE_INFINITY;
		for (final DoubleInterval i : this) {
			final double d = i.getDistance(value);
			if (distance > d) {
				interval = i;
				distance = d;
			}
		}
		return interval;
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
		final DoubleInterval interval = getClosestInterval(value);
		return interval != null ? interval.getDistance(value) : Double.POSITIVE_INFINITY;
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
		final DoubleInterval lowerInterval = getLowerInterval();
		return lowerInterval != null ? lowerInterval.getLowerDistance(value) :
				Double.POSITIVE_INFINITY;
	}

	/**
	 * Returns the distance between the {@link UpperBound} and the specified value.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the distance between the {@link UpperBound} and the specified value
	 */
	public double getUpperDistance(final Double value) {
		final DoubleInterval upperInterval = getUpperInterval();
		return upperInterval != null ? upperInterval.getUpperDistance(value) :
				Double.POSITIVE_INFINITY;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code double} value of the {@link LowerBound}.
	 * <p>
	 * @return the {@code double} value of the {@link LowerBound}
	 */
	public double getLowerBoundValue() {
		final DoubleInterval lowerInterval = getLowerInterval();
		return lowerInterval != null ? lowerInterval.getLowerBoundValue() :
				Double.NEGATIVE_INFINITY;
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
		final DoubleInterval lowerInterval = getLowerInterval();
		return lowerInterval != null ? lowerInterval.getLowerBoundValue(step) :
				Double.NEGATIVE_INFINITY;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@code double} value of the {@link UpperBound}.
	 * <p>
	 * @return the {@code double} value of the {@link UpperBound}
	 */
	public double getUpperBoundValue() {
		final DoubleInterval upperInterval = getUpperInterval();
		return upperInterval != null ? upperInterval.getUpperBoundValue() :
				Double.POSITIVE_INFINITY;
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
		final DoubleInterval upperInterval = getUpperInterval();
		return upperInterval != null ? upperInterval.getUpperBoundValue(step) :
				Double.POSITIVE_INFINITY;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Domain addAll(final Domain domain) {
		for (final DoubleInterval interval : domain) {
			add(interval);
		}
		return this;
	}

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
		for (final DoubleInterval interval : this) {
			if (!interval.isFinite()) {
				return false;
			}
		}
		return true;
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
	public Domain clone() {
		final Domain clone = new Domain();
		for (final DoubleInterval element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}
}
