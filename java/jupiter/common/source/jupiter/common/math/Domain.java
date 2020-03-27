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
import jupiter.common.util.Doubles;
import jupiter.common.util.Objects;

/**
 * {@link Domain} is the {@link GenericIntervalList} of {@link Range} and {@link Double}.
 */
public class Domain
		extends GenericIntervalList<Range, Double>
		implements IRange {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Domain ALL = new Domain(new Range(
			new LowerBound<Double>(Double.NEGATIVE_INFINITY, false),
			new UpperBound<Double>(Double.POSITIVE_INFINITY, false)));

	public static final Domain NEGATIVE = new Domain(new Range(
			new LowerBound<Double>(Double.NEGATIVE_INFINITY, false),
			new UpperBound<Double>(0., false)));
	public static final Domain NON_POSITIVE = new Domain(new Range(
			new LowerBound<Double>(Double.NEGATIVE_INFINITY, false),
			new UpperBound<Double>(0., true)));

	public static final Domain POSITIVE = new Domain(new Range(
			new LowerBound<Double>(0., false),
			new UpperBound<Double>(Double.POSITIVE_INFINITY, false)));
	public static final Domain NON_NEGATIVE = new Domain(new Range(
			new LowerBound<Double>(0., true),
			new UpperBound<Double>(Double.POSITIVE_INFINITY, false)));


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
	 * @param elements an array of {@link Range}
	 */
	public Domain(final Range... elements) {
		super(elements);
	}

	/**
	 * Constructs a {@link Domain} with the elements of the specified {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@link Range}
	 */
	public Domain(final Collection<? extends Range> elements) {
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
		Bound<Double> bound = null;
		double distance = Double.POSITIVE_INFINITY;
		for (final Range range : this) {
			final double d = range.getDistance(value);
			if (distance > d) {
				bound = range.getClosestBound(value);
				distance = d;
			}
		}
		return bound;
	}

	/**
	 * Returns the closest {@link Range} to the specified value, or {@code null} if {@code this}
	 * does not contain any finite {@link Range}.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the closest {@link Range} to the specified value, or {@code null} if {@code this}
	 *         does not contain any finite {@link Range}
	 */
	public Range getClosestRange(final Double value) {
		Range range = null;
		double distance = Double.POSITIVE_INFINITY;
		for (final Range r : this) {
			final double d = r.getDistance(value);
			if (distance > d) {
				range = r;
				distance = d;
			}
		}
		return range;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the distance to the specified value.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the distance to the specified value
	 */
	public double getDistance(final Double value) {
		final Range range = getClosestRange(value);
		return range != null ? range.getDistance(value) : Double.POSITIVE_INFINITY;
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
		return Ranges.bound(this, value);
	}

	/**
	 * Returns {@code value} if {@code value} is inside {@code this}, {@code NaN} otherwise.
	 * <p>
	 * @param value the {@link Double} to constrain (may be {@code null})
	 * <p>
	 * @return {@code value} if {@code value} is inside {@code this}, {@code NaN} otherwise
	 */
	public Double constrain(final Double value) {
		return Ranges.constrain(this, value);
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
		for (final Range range : this) {
			if (!range.isFinite()) {
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
		for (final Range element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}
}
