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

/**
 * {@link IDoubleInterval} is the {@link ISet} of {@link Double} lying between lower and upper
 * {@link Bound}.
 */
public interface IDoubleInterval
		extends ISet<Double> {

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
	public Bound<Double> getClosestBound(final Double value);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the distance to the specified value.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the distance to the specified value
	 */
	public double getDistance(final Double value);

	//////////////////////////////////////////////

	/**
	 * Returns the distance between the {@link LowerBound} and the specified value.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the distance between the {@link LowerBound} and the specified value
	 */
	public double getLowerDistance(final Double value);

	/**
	 * Returns the distance between the {@link UpperBound} and the specified value.
	 * <p>
	 * @param value a {@link Double} (may be {@code null})
	 * <p>
	 * @return the distance between the {@link UpperBound} and the specified value
	 */
	public double getUpperDistance(final Double value);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code double} value of the {@link LowerBound}.
	 * <p>
	 * @return the {@code double} value of the {@link LowerBound}
	 */
	public double getLowerBoundValue();

	/**
	 * Returns the {@code double} value of the {@link LowerBound} using the specified minimal
	 * interval.
	 * <p>
	 * @param step the {@code double} minimal interval
	 * <p>
	 * @return the {@code double} value of the {@link LowerBound} using the specified minimal
	 *         interval
	 */
	public double getLowerBoundValue(final double step);

	//////////////////////////////////////////////

	/**
	 * Returns the {@code double} value of the {@link UpperBound}.
	 * <p>
	 * @return the {@code double} value of the {@link UpperBound}
	 */
	public double getUpperBoundValue();

	/**
	 * Returns the {@code double} value of the {@link UpperBound} using the specified minimal
	 * interval.
	 * <p>
	 * @param step the {@code double} minimal interval
	 * <p>
	 * @return the {@code double} value of the {@link UpperBound} using the specified minimal
	 *         interval
	 */
	public double getUpperBoundValue(final double step);


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
	public double bound(final Double value);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is finite.
	 * <p>
	 * @return {@code true} if {@code this} is finite, {@code false} otherwise
	 */
	public boolean isFinite();
}
