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
import jupiter.common.math.DoubleInterval;
import jupiter.common.model.ICloneable;
import jupiter.common.test.DoubleArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Doubles;
import jupiter.common.util.Integers;
import jupiter.common.util.Objects;
import jupiter.math.analysis.function.univariate.UnivariateFunction;
import jupiter.math.analysis.interpolation.SplineInterpolator;

/**
 * {@link FiniteIntegrator} is the finite {@link Integrator} computing {@code Y = F(b) - F(a)} for
 * {@code a} and {@code b} defined in the integration {@link DoubleInterval} using the midpoint
 * rule.
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
	 * The sampling points.
	 */
	protected double[] DX, DY;
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
		this(f, 1, sampleSize, step);
	}

	/**
	 * Constructs a {@link FiniteIntegrator} with the specified {@link UnivariateFunction},
	 * integration {@link DoubleInterval}, sample size and interval between the sampling points.
	 * <p>
	 * When the independent variable is bounded ({@code lowerBound < t < upperBound}), the sampling
	 * points used for integration will be adapted to ensure the constraint holds even near the
	 * boundaries. This means the sample will not be centered anymore in these cases. At an extreme
	 * case, computing the integrals exactly at the lower bound will lead the sample to be entirely
	 * on the right side of the integration point.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite integrator can lead to highly unstable and inaccurate
	 * results, especially for high integration orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param f          the {@link UnivariateFunction} to integrate
	 * @param interval   the integration {@link DoubleInterval}
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2}, {@code step}
	 *                                  is negative or {@code (sampleSize - 1) * step} is greater or
	 *                                  equal to {@code upperBound - lowerBound}
	 */
	public FiniteIntegrator(final UnivariateFunction f, final DoubleInterval interval,
			final int sampleSize, final double step) {
		this(f, interval, 1, sampleSize, step);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link FiniteIntegrator} with the specified {@link UnivariateFunction},
	 * integration order, sample size and interval between the sampling points.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite integrator can lead to highly unstable and inaccurate
	 * results, especially for high integration orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param f          the {@link UnivariateFunction} to integrate
	 * @param order      the integration order
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2} or {@code step}
	 *                                  is negative
	 */
	public FiniteIntegrator(final UnivariateFunction f, final int order, final int sampleSize,
			final double step) {
		this(f, f.getDomain().getFirst(), order, sampleSize, step);
	}

	/**
	 * Constructs a {@link FiniteIntegrator} with the specified {@link UnivariateFunction},
	 * integration {@link DoubleInterval}, integration order, sample size and interval between the
	 * sampling points.
	 * <p>
	 * When the independent variable is bounded ({@code lowerBound < t < upperBound}), the sampling
	 * points used for integration will be adapted to ensure the constraint holds even near the
	 * boundaries. This means the sample will not be centered anymore in these cases. At an extreme
	 * case, computing the integrals exactly at the lower bound will lead the sample to be entirely
	 * on the right side of the integration point.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite integrator can lead to highly unstable and inaccurate
	 * results, especially for high integration orders. Using a very small interval between the
	 * sampling points is often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param f          the {@link UnivariateFunction} to integrate
	 * @param interval   the integration {@link DoubleInterval}
	 * @param order      the integration order
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less than {@code 2}, {@code step}
	 *                                  is negative or {@code (sampleSize - 1) * step} is greater or
	 *                                  equal to {@code upperBound - lowerBound}
	 */
	public FiniteIntegrator(final UnivariateFunction f, final DoubleInterval interval,
			final int order, final int sampleSize, final double step) {
		super(f, interval, order);

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
	// GETTERS
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

	//////////////////////////////////////////////

	/**
	 * Returns the initial value (on the abscissa).
	 * <p>
	 * @return the initial value (on the abscissa)
	 */
	public double getInitialValue() {
		return initialValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected int boundIndex(final int index) {
		return Maths.bound(index, 0, DX.length - 1);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the integrated {@code double} value {@code Y = F(x) - F(x - step)} for {@code x} and
	 * {@code x - step} defined in {@code domain}.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>The finite integral approximation is computed using the midpoint rule and interpolated by
	 * a {@link SplineInterpolator}.</dd>
	 * </dl>
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code Y = F(x) - F(x - step)} for {@code x} and {@code x - step} defined in
	 *         {@code domain}
	 */
	@Override
	protected double integrate(final double x) {
		// Check the arguments
		if (order > 1) {
			integrateAll();
		}
		if (interpolator != null) {
			// Evaluate the value Y = F(x) - F(x - step)
			return interpolator.apply(x);
		}

		// Bound the value x and center the integration interval (if it is possible)
		final double t0 = domain.bound(x - halfSampleSpan);
		final int size = sampleSize - 1;

		// Sample the function f around the value x
		final double[] X = Doubles.createSequence(sampleSize, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the finite integral approximation Y using the midpoint rule
		DX = new double[size];
		System.arraycopy(X, 0, DX, 0, size);
		Maths.sum(DX, step);
		DY = new double[size];
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
	 * {@code domain} with the initial value (on the ordinate).
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>The finite integral approximation is computed using the midpoint rule and interpolated by
	 * a {@link SplineInterpolator}.</dd>
	 * </dl>
	 * <p>
	 * @param x  a {@code double} value (on the abscissa)
	 * @param y0 the initial {@code double} value (on the ordinate) for each integration order
	 * <p>
	 * @return {@code Y = F(x)} for {@code x} defined in {@code domain}
	 */
	public double integrate(final double x, final double... y0) {
		// Check the arguments
		if (order > 1) {
			integrateAll(y0);
		}
		if (interpolator != null) {
			// Evaluate the value Y = F(x)
			return interpolator.apply(x);
		}

		// Bound the value x and center the integration interval (if it is possible)
		final double t0 = domain.bound(x - halfSampleSpan);
		final int size = sampleSize - 1;

		// Sample the function f around the value x
		final double[] X = Doubles.createSequence(sampleSize, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the finite integral approximation Y using the midpoint rule
		DX = new double[size];
		System.arraycopy(X, 0, DX, 0, size);
		Maths.sum(DX, step);
		DY = new double[size];
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
	 * Returns the integrated {@code double} value {@code Y = F(b) - F(a)} for {@code a} and
	 * {@code b} defined in {@code domain}.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>The finite integral approximation is computed using the midpoint rule and interpolated by
	 * a {@link SplineInterpolator}.</dd>
	 * </dl>
	 * <p>
	 * @param a the {@code double} lower bound (on the abscissa) of the integration interval
	 * @param b the {@code double} upper bound (on the abscissa) of the integration interval
	 * <p>
	 * @return {@code Y = F(b) - F(a)} for {@code a} and {@code b} defined in {@code domain}
	 */
	@Override
	public double integrate(final double a, final double b) {
		// Check the arguments
		integrateAll();
		if (a >= b) {
			return 0.;
		}

		// Find the corresponding sampling interval
		final int fromIndex = boundIndex(Maths.countMinSteps(initialValue, a, step));
		final int toIndex = boundIndex(Maths.countMaxSteps(initialValue, b, step));

		// Compute the corrections between the sampling interval and the interval
		final double fromCorrection = Maths.remainderMinSteps(initialValue, a, step, fromIndex) *
				(DY[fromIndex] + DY[boundIndex(fromIndex + 1)]) / 2.;
		final double toCorrection = Maths.remainderMaxSteps(initialValue, b, step, toIndex) *
				(DY[boundIndex(toIndex - 1)] + DY[toIndex]) / 2.;

		return Maths.sumInterval(fromIndex, toIndex + 1, DY) - fromCorrection - toCorrection;
	}

	//////////////////////////////////////////////

	/**
	 * Integrates {@code y = f(x)} for all {@code x} and {@code x - step} defined in {@code domain}
	 * and then use {@link #integrate} to retrieve {@code Y = F(x) - F(x - step)}.
	 * <p>
	 * @return {@code true} if the integration is done, {@code false} otherwise
	 *
	 * @see #integrate(double)
	 */
	@Override
	public boolean integrateAll() {
		// Check the arguments
		if (interpolator != null) {
			return true;
		}
		if (!domain.isFinite()) {
			IO.warn("The integration interval is not finite");
			return false;
		}

		// Set the domain coordinates of the first and last sampling points
		final double t0 = domain.getLowerBoundValue(step);
		final double tn = domain.getUpperBoundValue(step);
		final int size = Integers.convert((tn - t0) / step);
		if (size < 2) {
			IO.warn("The integration interval is too small");
			return false;
		}

		// Integrate for all the integration orders
		if (order > 1) {
			FiniteIntegrator df = new FiniteIntegrator(f, domain.getFirst(), sampleSize, step);
			df.integrateAll();
			for (int o = 1; o < order; ++o) {
				df = new FiniteIntegrator(df, domain.getFirst(), sampleSize, step);
				df.integrateAll();
			}
			DX = df.DX;
			DY = df.DY;
			interpolator = df.interpolator;
			return true;
		}

		// Sample the function f
		final double[] X = Doubles.createSequence(size + 1, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the finite integral approximation Y using the midpoint rule
		DX = new double[size];
		System.arraycopy(X, 0, DX, 0, size);
		Maths.sum(DX, step);
		DY = new double[size];
		for (int i = 0; i < size; ++i) {
			DY[i] = step * (Y[i] + Y[i + 1]) / 2.;
		}

		// Interpolate the finite integral approximation Y
		interpolator = SplineInterpolator.create(DX, DY);
		return true;
	}

	/**
	 * Integrates {@code y = f(x)} for all {@code x} defined in {@code domain} and then use
	 * {@link #integrate} to retrieve {@code Y = F(x)} with the initial value (on the ordinate).
	 * <p>
	 * @param y0 the initial {@code double} value value (on the ordinate) for each integration order
	 * <p>
	 * @return {@code true} if the integration is done, {@code false} otherwise
	 *
	 * @see #integrate(double)
	 */
	public boolean integrateAll(final double... y0) {
		// Check the arguments
		if (interpolator != null) {
			return true;
		}
		if (!domain.isFinite()) {
			IO.warn("The integration interval is not finite");
			return false;
		}
		DoubleArguments.requireSameLength(y0, order);

		// Set the domain coordinates of the first and last sampling points
		final double t0 = domain.getLowerBoundValue(step);
		final double tn = domain.getUpperBoundValue(step);
		final int size = Integers.convert((tn - t0) / step);
		if (size < 2) {
			IO.warn("The integration interval is too small");
			return false;
		}

		// Integrate for all the integration orders
		if (order > 1) {
			FiniteIntegrator df = new FiniteIntegrator(f, domain.getFirst(), sampleSize, step);
			df.integrateAll(y0[0]);
			for (int o = 1; o < order; ++o) {
				df = new FiniteIntegrator(df, domain.getFirst(), sampleSize, step);
				df.integrateAll(y0[o]);
			}
			DX = df.DX;
			DY = df.DY;
			interpolator = df.interpolator;
			return true;
		}

		// Sample the function f
		final double[] X = Doubles.createSequence(size + 1, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the finite integral approximation Y using the midpoint rule
		DX = new double[size];
		System.arraycopy(X, 0, DX, 0, size);
		Maths.sum(DX, step);
		DY = new double[size];
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
		clone.DX = Doubles.clone(DX);
		clone.DY = Doubles.clone(DY);
		clone.interpolator = Objects.clone(interpolator);
		return clone;
	}
}
