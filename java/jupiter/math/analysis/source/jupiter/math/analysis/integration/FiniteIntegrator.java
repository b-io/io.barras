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
package jupiter.math.analysis.integration;

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
 * {@link FiniteIntegrator} is the finite {@link Integrator} computing
 * {@code Y = F(x) - F(x - step)} for {@code x} and {@code x - step} defined in {@code range} using
 * the trapezoidal rule.
 */
public class FiniteIntegrator
		extends Integrator {

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
	 * The {@link UnivariateFunction} to integrate.
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
	 * The integration {@link Range}.
	 */
	protected final Range range;
	/**
	 * The enlarged integration {@link Range}.
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
	 * Constructs a {@link FiniteIntegrator} with the specified {@link UnivariateFunction}, sample
	 * size and interval between the sampling points.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite integrator can lead to highly unstable and inaccurate
	 * results, especially for high integration orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param f          the {@link UnivariateFunction} to integrate
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2} or {@code step}
	 *                                  is negative
	 */
	public FiniteIntegrator(final UnivariateFunction f, final int sampleSize,
			final double step) {
		this(f, sampleSize, step,
				new Range(f.getDomain().getLowerBound(), f.getDomain().getUpperBound()));
	}

	/**
	 * Constructs a {@link FiniteIntegrator} with the specified {@link UnivariateFunction}, sample
	 * size, interval between the sampling points and integration {@link Range}.
	 * <p>
	 * When the independent variable is bounded ({@code lowerBound < t < upperBound}), the sampling
	 * points used for integration will be adapted to ensure the constraint holds even near the
	 * boundaries. This means the sample will not be centered anymore in these cases. At an extreme
	 * case, computing the finite integrals exactly at the lower bound will lead the sample to be
	 * entirely on the right side of the integration point.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite integrator can lead to highly unstable and inaccurate
	 * results, especially for high integration orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param f          the {@link UnivariateFunction} to integrate
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * @param range      the integration {@link Range}
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2}, {@code step}
	 *                                  is negative or {@code (sampleSize - 1) * step} is greater or
	 *                                  equal to
	 *                                  {@code range.getUpperBound() - range.getLowerBound()}
	 */
	public FiniteIntegrator(final UnivariateFunction f, final int sampleSize,
			final double step, final Range range) {
		this(1, f, sampleSize, step, range);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link FiniteIntegrator} with the specified integration order,
	 * {@link UnivariateFunction}, sample size and interval between the sampling points.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite integrator can lead to highly unstable and inaccurate
	 * results, especially for high integration orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param order      the integration order
	 * @param f          the {@link UnivariateFunction} to integrate
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2} or {@code step}
	 *                                  is negative
	 */
	public FiniteIntegrator(final int order, final UnivariateFunction f, final int sampleSize,
			final double step) {
		this(order, f, sampleSize, step,
				new Range(f.getDomain().getLowerBound(), f.getDomain().getUpperBound()));
	}

	/**
	 * Constructs a {@link FiniteIntegrator} with the specified integration order, sample size,
	 * interval between the sampling points and integration {@link Range}.
	 * <p>
	 * When the independent variable is bounded ({@code lowerBound < t < upperBound}), the sampling
	 * points used for integration will be adapted to ensure the constraint holds even near the
	 * boundaries. This means the sample will not be centered anymore in these cases. At an extreme
	 * case, computing the finite integrals exactly at the lower bound will lead the sample to be
	 * entirely on the right side of the integration point.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite integrator can lead to highly unstable and inaccurate
	 * results, especially for high integration orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param order      the integration order
	 * @param f          the {@link UnivariateFunction} to integrate
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * @param range      the integration {@link Range}
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2}, {@code step}
	 *                                  is negative or {@code (sampleSize - 1) * step} is greater or
	 *                                  equal to
	 *                                  {@code range.getUpperBound() - range.getLowerBound()}
	 */
	public FiniteIntegrator(final int order, final UnivariateFunction f, final int sampleSize,
			final double step, final Range range) {
		super(order, f.getDomain());

		// Check the arguments
		IntegerArguments.requireGreaterOrEqualTo(sampleSize, 2);
		DoubleArguments.requireNonNegative(step);
		SetArguments.requireValid(range, "integration range");
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
	 * Returns the {@link UnivariateFunction} to integrate.
	 * <p>
	 * @return the {@link UnivariateFunction} to integrate
	 */
	public UnivariateFunction getFunction() {
		return f;
	}

	//////////////////////////////////////////////

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

	//////////////////////////////////////////////

	/**
	 * Returns the integration {@link Range}.
	 * <p>
	 * @return the integration {@link Range}
	 */
	public Range getRange() {
		return range;
	}

	/**
	 * Returns the enlarged integration {@link Range}.
	 * <p>
	 * @return the enlarged integration {@link Range}
	 */
	public Range getEnlargedRange() {
		return enlargedRange;
	}

	/**
	 * Returns the initial value (on the abscissa).
	 * <p>
	 * @return the initial value (on the abscissa)
	 */
	public double getInitialValue() {
		return range.getLowerBound().getValue() - halfSampleSpan;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the integrated {@code double} value {@code Y = F(x) - F(x - step)} for {@code x} and
	 * {@code x - step} defined in {@code range}.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>The finite integral approximation is computed using the trapezoidal rule and interpolated
	 * by a {@link SplineInterpolator}.</dd>
	 * </dl>
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code Y = F(x) - F(x - step)} for {@code x} and {@code x - step} defined in
	 *         {@code range}
	 */
	@Override
	protected double integrate(final double x) {
		if (order > 1) {
			integrateAll();
		}
		if (interpolator != null) {
			// Evaluate the value Y = F(x) - F(x - step)
			return interpolator.apply(x);
		}

		// Bound the value x and center the integration range (if it is possible)
		final double t0 = enlargedRange.bound(x - halfSampleSpan);
		final int size = sampleSize - 1;

		// Sample the function f around the value x
		final double[] X = Doubles.createSequence(sampleSize, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the finite integral approximation Y using the trapezoidal rule
		final double[] DX = new double[size];
		System.arraycopy(X, 0, DX, 0, size);
		Maths.sum(DX, step);
		final double[] DY = new double[size];
		for (int i = 0; i < size; ++i) {
			DY[i] = step * (Y[i] + Y[i + 1]) / 2.;
		}

		// Interpolate the finite integral approximation Y
		final SplineInterpolator i = SplineInterpolator.create(DX, DY);

		// Evaluate the value Y = F(x) - F(x - step)
		return i.apply(x);
	}

	/**
	 * Returns the integrated {@code double} value {@code Y = F(x)} for {@code x} defined in
	 * {@code range} with the initial value (on the ordinate).
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>The finite integral approximation is computed using the trapezoidal rule and interpolated
	 * by a {@link SplineInterpolator}.</dd>
	 * </dl>
	 * <p>
	 * @param x  a {@code double} value (on the abscissa)
	 * @param y0 the initial {@code double} value (on the ordinate) for each integration order
	 * <p>
	 * @return {@code Y = F(x)} for {@code x} defined in {@code range}
	 */
	public double integrate(final double x, final double... y0) {
		if (order > 1) {
			integrateAll(y0);
		}
		if (interpolator != null) {
			// Evaluate the value Y = F(x)
			return interpolator.apply(x);
		}

		// Bound the value x and center the integration range (if it is possible)
		final double t0 = enlargedRange.bound(x - halfSampleSpan);
		final int size = sampleSize - 1;

		// Sample the function f around the value x
		final double[] X = Doubles.createSequence(sampleSize, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the finite integral approximation Y using the trapezoidal rule
		final double[] DX = new double[size];
		System.arraycopy(X, 0, DX, 0, size);
		Maths.sum(DX, step);
		final double[] DY = new double[size];
		for (int i = 0; i < size; ++i) {
			DY[i] = (i == 0 ? y0[0] : DY[i - 1]) + step * (Y[i] + Y[i + 1]) / 2.;
		}

		// Interpolate the finite integral approximation Y
		final SplineInterpolator i = SplineInterpolator.create(DX, DY);

		// Evaluate the value Y = F(x)
		return i.apply(x);
	}

	//////////////////////////////////////////////

	/**
	 * Integrates {@code y = f(x)} for all {@code x} and {@code x - step} defined in {@code range}
	 * and then use {@link #integrate} to retrieve {@code Y = F(x) - F(x - step)}.
	 * <p>
	 * @return {@code true} if the integration is done, {@code false} otherwise
	 *
	 * @see #integrate(double)
	 */
	@Override
	protected boolean integrateAll() {
		if (interpolator != null) {
			return true;
		}
		if (!enlargedRange.isFinite()) {
			IO.warn("The integration range is not finite");
			return false;
		}

		// Set the domain coordinates of the first and last sampling points
		final double t0 = enlargedRange.getLowerBoundValue(step);
		final double tn = enlargedRange.getUpperBoundValue(step);
		final int size = Integers.convert((tn - t0) / step);
		if (size < 2) {
			IO.warn("The integration range is too small");
			return false;
		}

		// Integrate for all the integration orders
		if (order > 1) {
			FiniteIntegrator df = new FiniteIntegrator(f, sampleSize, step, range);
			df.integrateAll();
			for (int o = 1; o < order; ++o) {
				df = new FiniteIntegrator(df, sampleSize, step, range);
				df.integrateAll();
			}
			interpolator = df.interpolator;
			return true;
		}

		// Sample the function f
		final double[] X = Doubles.createSequence(size + 1, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the finite integral approximation Y using the trapezoidal rule
		final double[] DX = new double[size];
		System.arraycopy(X, 0, DX, 0, size);
		Maths.sum(DX, step);
		final double[] DY = new double[size];
		for (int i = 0; i < size; ++i) {
			DY[i] = step * (Y[i] + Y[i + 1]) / 2.;
		}

		// Interpolate the finite integral approximation Y
		interpolator = SplineInterpolator.create(DX, DY);
		return true;
	}

	/**
	 * Integrates {@code y = f(x)} for all {@code x} defined in {@code range} and then use
	 * {@link #integrate} to retrieve {@code Y = F(x)} with the initial value (on the ordinate).
	 * <p>
	 * @param y0 the initial {@code double} value value (on the ordinate) for each integration order
	 * <p>
	 * @return {@code true} if the integration is done, {@code false} otherwise
	 *
	 * @see #integrate(double)
	 */
	public boolean integrateAll(final double... y0) {
		if (interpolator != null) {
			return true;
		}
		if (!enlargedRange.isFinite()) {
			IO.warn("The integration range is not finite");
			return false;
		}

		// Set the domain coordinates of the first and last sampling points
		final double t0 = enlargedRange.getLowerBoundValue(step);
		final double tn = enlargedRange.getUpperBoundValue(step);
		final int size = Integers.convert((tn - t0) / step);
		if (size < 2) {
			IO.warn("The integration range is too small");
			return false;
		}

		// Integrate for all the integration orders
		if (order > 1) {
			FiniteIntegrator df = new FiniteIntegrator(f, sampleSize, step, range);
			df.integrateAll(y0[0]);
			for (int o = 1; o < order; ++o) {
				df = new FiniteIntegrator(df, sampleSize, step, range);
				df.integrateAll(y0[o]);
			}
			interpolator = df.interpolator;
			return true;
		}

		// Sample the function f
		final double[] X = Doubles.createSequence(size + 1, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the finite integral approximation Y using the trapezoidal rule
		final double[] DX = new double[size];
		System.arraycopy(X, 0, DX, 0, size);
		Maths.sum(DX, step);
		final double[] DY = new double[size];
		for (int i = 0; i < size; ++i) {
			DY[i] = (i == 0 ? y0[0] : DY[i - 1]) + step * (Y[i] + Y[i + 1]) / 2.;
		}

		// Interpolate the finite integral approximation Y
		interpolator = SplineInterpolator.create(DX, DY);
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
	public FiniteIntegrator clone() {
		final FiniteIntegrator clone = (FiniteIntegrator) super.clone();
		clone.enlargedRange = Objects.clone(enlargedRange);
		clone.interpolator = Objects.clone(interpolator);
		return clone;
	}
}
