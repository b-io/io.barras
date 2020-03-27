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
package jupiter.math.analysis.differentiation;

import static jupiter.common.io.IO.IO;

import jupiter.common.math.Maths;
import jupiter.common.math.Range;
import jupiter.common.model.ICloneable;
import jupiter.common.test.DoubleArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.test.SetArguments;
import jupiter.common.util.Doubles;
import jupiter.common.util.Integers;
import jupiter.common.util.Objects;
import jupiter.math.analysis.function.univariate.UnivariateFunction;
import jupiter.math.analysis.interpolation.SplineInterpolator;

/**
 * {@link FiniteDifferentiator} is the {@link Differentiator} using the Crank–Nicolson method.
 */
public class FiniteDifferentiator
		extends Differentiator {

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
	 * The {@link UnivariateFunction} to differentiate.
	 */
	protected final UnivariateFunction f;

	/**
	 * The sample size.
	 */
	protected final int sampleSize;
	/**
	 * The interval between the sampling points.
	 */
	protected final double step;
	/**
	 * The half sample span.
	 */
	protected final double halfSampleSpan;

	/**
	 * The differentiation {@link Range}.
	 */
	protected final Range range;
	/**
	 * The enlarged differentiation {@link Range}.
	 */
	protected Range enlargedRange;

	/**
	 * The {@link SplineInterpolator}.
	 */
	protected SplineInterpolator interpolator = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified {@link UnivariateFunction},
	 * sample size and interval between the sampling points.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite differentiator can lead to highly unstable and inaccurate
	 * results, especially for high derivation orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param f          the {@link UnivariateFunction} to differentiate
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2} or {@code step}
	 *                                  is negative
	 */
	public FiniteDifferentiator(final UnivariateFunction f, final int sampleSize,
			final double step) {
		this(f, sampleSize, step,
				new Range(f.getDomain().getLowerBound(), f.getDomain().getUpperBound()));
	}

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified {@link UnivariateFunction},
	 * sample size, interval between the sampling points and differentiation {@link Range}.
	 * <p>
	 * When the independent variable is bounded ({@code lowerBound < t < upperBound}), the sampling
	 * points used for differentiation will be adapted to ensure the constraint holds even near the
	 * boundaries. This means the sample will not be centered anymore in these cases. At an extreme
	 * case, computing the derivatives exactly at the lower bound will lead the sample to be
	 * entirely on the right side of the derivation point.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite differentiator can lead to highly unstable and inaccurate
	 * results, especially for high derivation orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param f          the {@link UnivariateFunction} to differentiate
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * @param range      the differentiation {@link Range}
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2}, {@code step}
	 *                                  is negative or {@code (sampleSize - 1) * step} is greater or
	 *                                  equal to
	 *                                  {@code range.getUpperBound() - range.getLowerBound()}
	 */
	public FiniteDifferentiator(final UnivariateFunction f, final int sampleSize,
			final double step, final Range range) {
		this(1, f, sampleSize, step, range);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified derivation order,
	 * {@link UnivariateFunction}, sample size and interval between the sampling points.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite differentiator can lead to highly unstable and inaccurate
	 * results, especially for high derivation orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param order      the derivation order
	 * @param f          the {@link UnivariateFunction} to differentiate
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2} or {@code step}
	 *                                  is negative
	 */
	public FiniteDifferentiator(final int order, final UnivariateFunction f, final int sampleSize,
			final double step) {
		this(order, f, sampleSize, step,
				new Range(f.getDomain().getLowerBound(), f.getDomain().getUpperBound()));
	}

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified derivation order, sample size,
	 * interval between the sampling points and differentiation {@link Range}.
	 * <p>
	 * When the independent variable is bounded ({@code lowerBound < t < upperBound}), the sampling
	 * points used for differentiation will be adapted to ensure the constraint holds even near the
	 * boundaries. This means the sample will not be centered anymore in these cases. At an extreme
	 * case, computing the derivatives exactly at the lower bound will lead the sample to be
	 * entirely on the right side of the derivation point.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite differentiator can lead to highly unstable and inaccurate
	 * results, especially for high derivation orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param order      the derivation order
	 * @param f          the {@link UnivariateFunction} to differentiate
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * @param range      the differentiation {@link Range}
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2}, {@code step}
	 *                                  is negative or {@code (sampleSize - 1) * step} is greater or
	 *                                  equal to
	 *                                  {@code range.getUpperBound() - range.getLowerBound()}
	 */
	public FiniteDifferentiator(final int order, final UnivariateFunction f, final int sampleSize,
			final double step, final Range range) {
		super(order, f.getDomain());

		// Check the arguments
		IntegerArguments.requireGreaterOrEqualTo(sampleSize, 2);
		DoubleArguments.requireNonNegative(step);
		SetArguments.requireValid(range, "differentiation range");
		DoubleArguments.requireLessOrEqualTo((sampleSize - 1) * step,
				range.getUpperBound().getValue() - range.getLowerBound().getValue());

		// Set the attributes
		this.f = f;
		this.sampleSize = sampleSize;
		this.step = step;
		this.range = range;
		halfSampleSpan = (sampleSize - 1) * step / 2.;
		enlargedRange = new Range(range.getLowerBound().getValue() - order * halfSampleSpan,
				range.getUpperBound().getValue() + order * halfSampleSpan);
		if (!domain.isInside(enlargedRange)) {
			enlargedRange = range;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link UnivariateFunction} to differentiate.
	 *
	 * @return the {@link UnivariateFunction} to differentiate
	 */
	public UnivariateFunction getFunction() {
		return f;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the sample size.
	 *
	 * @return the sample size
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * Returns the interval between the sampling points.
	 *
	 * @return the interval between the sampling points
	 */
	public double getStep() {
		return step;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the differentiation {@link Range}.
	 *
	 * @return the differentiation {@link Range}
	 */
	public Range getRange() {
		return range;
	}

	/**
	 * Returns the enlarged differentiation {@link Range}.
	 *
	 * @return the enlarged differentiation {@link Range}
	 */
	public Range getEnlargedRange() {
		return enlargedRange;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the differentiated {@code double} value {@code y' = f'(x)} for {@code x} defined in
	 * {@code range}.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>The derivative approximation is computed using the Crank–Nicolson method and interpolated
	 * by a {@link SplineInterpolator}.</dd>
	 * </dl>
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code y' = f'(x)} for {@code x} defined in {@code range}
	 */
	@Override
	protected double differentiate(final double x) {
		if (order > 1) {
			differentiateAll();
		}
		if (interpolator != null) {
			// Evaluate the value y' = f'(x)
			return interpolator.apply(x);
		}

		// Bound the value x and center the differentiation range (if it is possible)
		final double t0 = enlargedRange.bound(x - halfSampleSpan);
		final int size = sampleSize - 1;

		// Sample the function f around the value x
		final double[] X = Doubles.createSequence(sampleSize, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the derivative approximation y' using the Crank–Nicolson method
		final double[] dX = new double[size];
		System.arraycopy(X, 0, dX, 0, size);
		Maths.sum(dX, step / 2.);
		final double[] dY = new double[size];
		for (int i = 0; i < size; ++i) {
			dY[i] = (Y[i + 1] - Y[i]) / step;
		}

		// Interpolate the derivative approximation y'
		final SplineInterpolator i = SplineInterpolator.create(dX, dY);

		// Evaluate the value y' = f'(x)
		return i.apply(x);
	}

	//////////////////////////////////////////////

	/**
	 * Differentiates {@code y = f(x)} for all {@code x} defined in {@code range} and then use
	 * {@link #differentiate} to retrieve {@code y' = f'(x)}.
	 * <p>
	 * @return {@code true} if the differentiation is done, {@code false} otherwise
	 *
	 * @see #differentiate(double)
	 */
	@Override
	protected boolean differentiateAll() {
		if (interpolator != null) {
			return true;
		}
		if (!enlargedRange.isFinite()) {
			IO.warn("The differentiation range is not finite");
			return false;
		}

		// Set the domain coordinates of the first and last sampling points
		final double t0 = enlargedRange.getLowerBoundValue(step);
		final double tn = enlargedRange.getUpperBoundValue(step);
		final int size = Integers.convert((tn - t0) / step);
		if (size < 2) {
			IO.warn("The differentiation range is too small");
			return false;
		}

		// Differentiate for all the orders
		if (order > 1) {
			FiniteDifferentiator df = new FiniteDifferentiator(f, sampleSize, step, range);
			df.differentiateAll();
			for (int o = 1; o < order; ++o) {
				df = new FiniteDifferentiator(df, sampleSize, step, range);
				df.differentiateAll();
			}
			interpolator = df.interpolator;
			return true;
		}

		// Sample the function f
		final double[] X = Doubles.createSequence(size + 1, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the derivative approximation y' using the Crank–Nicolson method
		final double[] dX = new double[size];
		System.arraycopy(X, 0, dX, 0, size);
		Maths.sum(dX, step / 2.);
		final double[] dY = new double[size];
		for (int i = 0; i < size; ++i) {
			dY[i] = (Y[i + 1] - Y[i]) / step;
		}

		// Interpolate the derivative approximation y'
		interpolator = SplineInterpolator.create(dX, dY);
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
	public FiniteDifferentiator clone() {
		final FiniteDifferentiator clone = (FiniteDifferentiator) super.clone();
		clone.enlargedRange = Objects.clone(enlargedRange);
		clone.interpolator = Objects.clone(interpolator);
		return clone;
	}
}
