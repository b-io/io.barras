/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.io.InputOutput.IO;

import jupiter.common.math.DoubleInterval;
import jupiter.common.math.Intervals;
import jupiter.common.math.Maths;
import jupiter.common.model.ICloneable;
import jupiter.common.test.DoubleArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Doubles;
import jupiter.common.util.Integers;
import jupiter.common.util.Objects;
import jupiter.math.analysis.function.univariate.UnivariateFunction;
import jupiter.math.analysis.interpolation.SplineInterpolator;

/**
 * {@link FiniteDifferentiator} is the finite {@link Differentiator} computing {@code y' = f'(x)}
 * for {@code x} defined in the differentiation {@link DoubleInterval} using the Crank–Nicolson
 * method.
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
		this(f, 1, sampleSize, step);
	}

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified {@link UnivariateFunction},
	 * differentiation {@link DoubleInterval}, sample size and interval between the sampling points.
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
	 * @param interval   the differentiation {@link DoubleInterval}
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2}, {@code step}
	 *                                  is negative or {@code (sampleSize - 1) * step} is greater or
	 *                                  equal to {@code upperBound - lowerBound}
	 */
	public FiniteDifferentiator(final UnivariateFunction f, final DoubleInterval interval,
			final int sampleSize, final double step) {
		this(f, interval, 1, sampleSize, step);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified {@link UnivariateFunction},
	 * derivation order, sample size and interval between the sampling points.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite differentiator can lead to highly unstable and inaccurate
	 * results, especially for high derivation orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param f          the {@link UnivariateFunction} to differentiate
	 * @param order      the derivation order
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2} or {@code step}
	 *                                  is negative
	 */
	public FiniteDifferentiator(final UnivariateFunction f, final int order, final int sampleSize,
			final double step) {
		this(f, f.getDomain().getFirst(), order, sampleSize, step);
	}

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified {@link UnivariateFunction},
	 * differentiation {@link DoubleInterval}, derivation order, sample size and interval between
	 * the sampling points.
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
	 * @param interval   the differentiation {@link DoubleInterval}
	 * @param order      the derivation order
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2}, {@code step}
	 *                                  is negative or {@code (sampleSize - 1) * step} is greater or
	 *                                  equal to {@code upperBound - lowerBound}
	 */
	public FiniteDifferentiator(final UnivariateFunction f, final DoubleInterval interval,
			final int order, final int sampleSize, final double step) {
		super(f, Intervals.createEnlargedInterval(interval, f.getDomain(), order, sampleSize, step),
				order);

		// Check the arguments
		IntegerArguments.requireGreaterOrEqualTo(sampleSize, 2);
		DoubleArguments.requireNonNegative(step);
		DoubleArguments.requireLessOrEqualTo((sampleSize - 1) * step,
				interval.getUpperBoundValue() - interval.getLowerBoundValue());

		// Set the attributes
		this.sampleSize = sampleSize;
		this.step = step;
		halfSampleSpan = (sampleSize - 1) * step / 2.;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the sample size.
	 * <p>
	 * @return the sample size
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * Returns the interval between the sampling points.
	 * <p>
	 * @return the interval between the sampling points
	 */
	public double getStep() {
		return step;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the differentiated {@code double} value {@code y' = f'(x)} for {@code x} defined in
	 * {@code domain}.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>The derivative approximation is computed using the Crank–Nicolson method and interpolated
	 * by a {@link SplineInterpolator}.</dd>
	 * </dl>
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code y' = f'(x)} for {@code x} defined in {@code domain}
	 */
	@Override
	protected double differentiate(final double x) {
		// Check the arguments
		if (order > 1) {
			differentiateAll();
		}
		if (interpolator != null) {
			// Evaluate the value y' = f'(x)
			return interpolator.apply(x);
		}

		// Bound the value x and center the differentiation interval (if possible)
		final double t0 = domain.bound(x - halfSampleSpan);
		final int size = sampleSize - 1;

		// Sample the function f around the value x
		final double[] X = Doubles.createSequence(sampleSize, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the derivative approximation y' using the Crank–Nicolson method
		final double[] dX = new double[size];
		System.arraycopy(X, 0, dX, 0, size);
		Maths.add(dX, step / 2.);
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
	 * Differentiates {@code y = f(x)} for all {@code x} defined in {@code domain} and then use
	 * {@link #differentiate(double)} to retrieve {@code y' = f'(x)}.
	 * <p>
	 * @return {@code true} if the differentiation is done, {@code false} otherwise
	 *
	 * @see #differentiate(double)
	 */
	@Override
	public boolean differentiateAll() {
		// Check the arguments
		if (interpolator != null) {
			return true;
		}
		if (!domain.isFinite()) {
			IO.warn("The differentiation interval is not finite");
			return false;
		}

		// Set the domain coordinates of the first and last sampling points
		final double t0 = domain.getLowerBoundValue(step);
		final double tn = domain.getUpperBoundValue(step);
		final int size = Integers.convert((tn - t0) / step);
		if (size < 2) {
			IO.warn("The differentiation interval is too small");
			return false;
		}

		// Differentiate for all the orders
		if (order > 1) {
			FiniteDifferentiator df = new FiniteDifferentiator(f, domain.getFirst(), sampleSize,
					step);
			df.differentiateAll();
			for (int o = 1; o < order; ++o) {
				df = new FiniteDifferentiator(df, domain.getFirst(), sampleSize, step);
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
		Maths.add(dX, step / 2.);
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
		clone.interpolator = Objects.clone(interpolator);
		return clone;
	}
}
