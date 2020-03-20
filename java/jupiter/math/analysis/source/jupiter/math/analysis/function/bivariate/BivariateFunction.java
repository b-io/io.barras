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
package jupiter.math.analysis.function.bivariate;

import jupiter.common.math.Domain;
import jupiter.common.model.ICloneable;
import jupiter.math.analysis.function.univariate.UnivariateFunction;

/**
 * {@link BivariateFunction} is a bivariate function defined in the {@link Domain} of each variable.
 */
public abstract class BivariateFunction
		extends UnivariateFunction
		implements IBivariateFunction {

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
	 * The {@link Domain} of the second variable.
	 */
	protected final Domain secondDomain;

	/**
	 * The initial {@code double} value.
	 */
	protected final double initialValue;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link BivariateFunction} by default.
	 */
	protected BivariateFunction() {
		this(0.);
	}

	/**
	 * Constructs a {@link BivariateFunction} with the specified initial value.
	 * <p>
	 * @param initialValue the initial {@code double} value
	 */
	public BivariateFunction(final double initialValue) {
		super();
		secondDomain = domain;
		this.initialValue = initialValue;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link BivariateFunction} with the specified {@link Domain} for both variables.
	 * <p>
	 * @param domain the {@link Domain} for both variables
	 */
	protected BivariateFunction(final Domain domain) {
		this(domain, 0.);
	}

	/**
	 * Constructs a {@link BivariateFunction} with the specified {@link Domain} for both variables
	 * and initial value.
	 * <p>
	 * @param domain       the {@link Domain} for both variables
	 * @param initialValue the initial {@code double} value
	 */
	public BivariateFunction(final Domain domain, final double initialValue) {
		super(domain);
		secondDomain = domain;
		this.initialValue = initialValue;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link BivariateFunction} with the specified {@link Domain} of the first
	 * variable and {@link Domain} of the second variable.
	 * <p>
	 * @param firstDomain  the {@link Domain} of the first variable
	 * @param secondDomain the {@link Domain} of the second variable
	 */
	protected BivariateFunction(final Domain firstDomain, final Domain secondDomain) {
		this(firstDomain, secondDomain, 0.);
	}

	/**
	 * Constructs a {@link BivariateFunction} with the specified {@link Domain} of the first
	 * variable, {@link Domain} of the second variable and initial value.
	 * <p>
	 * @param firstDomain  the {@link Domain} of the first variable
	 * @param secondDomain the {@link Domain} of the second variable
	 * @param initialValue the initial {@code double} value
	 */
	public BivariateFunction(final Domain firstDomain, final Domain secondDomain,
			final double initialValue) {
		super(firstDomain);
		this.secondDomain = secondDomain;
		this.initialValue = initialValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Domain} of the first variable.
	 * <p>
	 * @return the {@link Domain} of the first variable
	 */
	public Domain getFirstDomain() {
		return domain;
	}

	/**
	 * Returns the {@link Domain} of the second variable.
	 * <p>
	 * @return the {@link Domain} of the second variable
	 */
	public Domain getSecondDomain() {
		return secondDomain;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the initial {@code double} value.
	 * <p>
	 * @return the initial {@code double} value
	 */
	public double getInitialValue() {
		return initialValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code x2} if {@code x2} is inside {@code secondDomain}, {@code secondLowerBound} if
	 * {@code x2 < secondLowerBound}, {@code secondUpperBound} if {@code x2 > secondUpperBound},
	 * {@code NaN} otherwise.
	 * <p>
	 * @param x2 a {@code double} value
	 * <p>
	 * @return {@code x2} if {@code x2} is inside {@code secondDomain}, {@code secondLowerBound} if
	 *         {@code x2 < secondLowerBound}, {@code secondUpperBound} if
	 *         {@code x2 > secondUpperBound}, {@code NaN} otherwise
	 */
	public double boundSecond(final double x2) {
		return secondDomain.bound(x2);
	}

	/**
	 * Returns {@code x2} if {@code x2} is inside {@code secondDomain}, {@code NaN} otherwise.
	 * <p>
	 * @param x2 a {@code double} value
	 * <p>
	 * @return {@code x2} if {@code x2} is inside {@code secondDomain}, {@code NaN} otherwise
	 */
	public double constrainSecond(final double x2) {
		return secondDomain.constrain(x2);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the bivariate function to the initial value and the specified value.
	 * <p>
	 * @param x2 a {@code double} value
	 * <p>
	 * @return {@code f(initialValue, x2)}
	 */
	@Override
	public double apply(final double x2) {
		return apply(initialValue, constrainSecond(x2));
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
	public BivariateFunction clone() {
		return (BivariateFunction) super.clone();
	}
}
