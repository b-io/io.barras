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
import jupiter.common.test.DoubleArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.math.analysis.function.univariate.UnivariateFunction;

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
	 * The number of points to use.
	 */
	protected final int pointCount;
	/**
	 * The step size.
	 */
	protected final double stepSize;
	/**
	 * The half sample span.
	 */
	protected final double halfSampleSpan;

	/**
	 * The safe {@link Range}.
	 */
	protected final Range safeRange;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified number of points and step size.
	 * <p>
	 * <b>Note:</b> Wrong settings for the finite differences differentiator can lead to highly
	 * unstable and inaccurate results, especially for high derivation orders. Using a very small
	 * step sizes is often a <em>bad</em> idea.
	 * <p>
	 * @param f          the {@link UnivariateFunction} to differentiate
	 * @param pointCount the number of points to use
	 * @param stepSize   the step size (the gap between each point)
	 * <p>
	 * @throws IllegalArgumentException if {@code pointCount} is less or equal to {@code 1} or
	 *                                  {@code stepsize} is negative
	 */
	public FiniteDifferentiator(final UnivariateFunction f, final int pointCount,
			final double stepSize) {
		this(f, pointCount, stepSize,
				new Range(f.getDomain().getLowerBound(), f.getDomain().getUpperBound()));
	}

	/**
	 * Constructs a {@link FiniteDifferentiator} with the specified number of points, step size and
	 * {@link Domain}.
	 * <p>
	 * When the independent variable is bounded ({@code lowerBound < t < upperBound}), the sampling
	 * points used for differentiation will be adapted to ensure the constraint holds even near the
	 * boundaries. This means the sample will not be centered anymore in these cases. At an extreme
	 * case, computing the derivatives exactly at the lower bound will lead the sample to be
	 * entirely on the right side of the derivation point.
	 * <p>
	 * <b>Note:</b> Wrong settings for the finite differences differentiator can lead to highly
	 * unstable and inaccurate results, especially for high derivation orders. Using a very small
	 * step sizes is often a <em>bad</em> idea.
	 * <p>
	 * @param f          the {@link UnivariateFunction} to differentiate
	 * @param pointCount the number of points to use
	 * @param stepSize   the step size (the gap between each point)
	 * @param range      the {@link Range}
	 * <p>
	 * @throws IllegalArgumentException if {@code pointCount} is less or equal to {@code 1},
	 *                                  {@code stepsize} is negative or
	 *                                  {@code stepSize * (pointCount - 1)} is greater or equal to
	 *                                  {@code domain.getUpperBound() - domain.getLowerBound()}
	 */
	public FiniteDifferentiator(final UnivariateFunction f, final int pointCount,
			final double stepSize, final Range range) {
		super(f.getDomain());

		// Check the arguments
		IntegerArguments.requireGreaterThan(pointCount, 1);
		DoubleArguments.requireNonNegative(stepSize);
		DoubleArguments.requireLessThan(stepSize * (pointCount - 1),
				domain.getUpperBound().getValue() - domain.getLowerBound().getValue());

		// Set the attributes
		this.pointCount = pointCount;
		this.stepSize = stepSize;
		this.f = f;
		halfSampleSpan = 0.5 * stepSize * (pointCount - 1);
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
	 * Returns the number of points to use.
	 *
	 * @return the number of points to use
	 */
	public int getPointCount() {
		return pointCount;
	}

	/**
	 * Returns the step size.
	 *
	 * @return the step size
	 */
	public double getStepSize() {
		return stepSize;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the differentiated {@code double} value {@code y' = f'(x)} for {@code x}. Evaluates
	 * the derivative at {@code x} defined in {@code domain}.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code y = f(x)} for {@code x} defined in {@code domain}
	 */
	protected double differentiate(final double x) {
		//TODO
		return Double.NaN;
	}
}
