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

import jupiter.common.math.Domain;
import jupiter.common.math.Maths;
import jupiter.common.math.Range;
import jupiter.common.model.ICloneable;
import jupiter.common.test.DoubleArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Doubles;
import jupiter.math.analysis.function.univariate.UnivariateFunction;
import jupiter.math.analysis.interpolation.SplineInterpolator;

/**
 * {@link FiniteDifferentiator} is the {@link Differentiator} using finite differences.
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
	 * The safe {@link Range} of the domain.
	 */
	protected final Range safeRange;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified sample size and interval between
	 * the sampling points.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite differences differentiator can lead to highly unstable and
	 * inaccurate results, especially for high derivation orders. Using a very small step size is
	 * often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param f          the {@link UnivariateFunction} to differentiate
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less or equal to {@code 1} or
	 *                                  {@code step} is negative
	 */
	public FiniteDifferentiator(final UnivariateFunction f, final int sampleSize,
			final double step) {
		this(f, sampleSize, step,
				new Range(f.getDomain().getLowerBound(), f.getDomain().getUpperBound()));
	}

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified sample size, step size and
	 * {@link Domain}.
	 * <p>
	 * When the independent variable is bounded ({@code lowerBound < t < upperBound}), the sampling
	 * points used for differentiation will be adapted to ensure the constraint holds even near the
	 * boundaries. This means the sample will not be centered anymore in these cases. At an extreme
	 * case, computing the derivatives exactly at the lower bound will lead the sample to be
	 * entirely on the right side of the derivation point.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>Wrong settings for the finite differences differentiator can lead to highly unstable and
	 * inaccurate results, especially for high derivation orders. Using a very small step size is
	 * often a <em>bad</em> idea.</dd>
	 * </dl>
	 * <p>
	 * @param f          the {@link UnivariateFunction} to differentiate
	 * @param sampleSize the sample size
	 * @param step       the interval between the sampling points
	 * @param range      the {@link Range} of the domain
	 * <p>
	 * @throws IllegalArgumentException if {@code sampleSize} is less or equal to
	 *                                  {@code 1}, {@code step} is negative or
	 *                                  {@code step * (sampleSize - 1)} is greater or equal to
	 *                                  {@code domain.getUpperBound() - domain.getLowerBound()}
	 */
	public FiniteDifferentiator(final UnivariateFunction f, final int sampleSize,
			final double step, final Range range) {
		super(f.getDomain());

		// Check the arguments
		IntegerArguments.requireGreaterThan(sampleSize, 1);
		DoubleArguments.requireNonNegative(step);
		DoubleArguments.requireLessThan(step * (sampleSize - 1),
				domain.getUpperBound().getValue() - domain.getLowerBound().getValue());

		// Set the attributes
		this.sampleSize = sampleSize;
		this.step = step;
		this.f = f;
		halfSampleSpan = step * (sampleSize - 1) / 2.;
		final double safety = Maths.ulp(halfSampleSpan);
		safeRange = new Range(range.getLowerBound().getValue() + halfSampleSpan + safety,
				range.getUpperBound().getValue() - halfSampleSpan - safety);
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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
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
		// Bound the value x and center the differentiation range (if it is possible)
		final double t0 = safeRange.bound(x) - halfSampleSpan;

		// Sample the function f around the value x
		final double[] X = Doubles.createSequence(sampleSize, t0, step);
		final double[] Y = f.applyToPrimitiveArray(X);

		// Compute the derivative approximation y' using the Crank–Nicolson method
		final double[] derivative = new double[sampleSize];
		for (int i = 0; i < sampleSize - 1; ++i) {
			derivative[i] = (Y[i + 1] - Y[i]) / step;
		}

		// Interpolate the derivative approximation y'
		final SplineInterpolator interpolator = SplineInterpolator.create(X, derivative);

		// Evaluate the value y' = f'(x)
		return interpolator.apply(safeRange.bound(x - step / 2.));
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
		return (FiniteDifferentiator) super.clone();
	}
}
