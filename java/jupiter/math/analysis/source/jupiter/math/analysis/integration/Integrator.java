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

import jupiter.common.math.Domain;
import jupiter.common.math.DoubleInterval;
import jupiter.common.model.ICloneable;
import jupiter.common.test.IntegerArguments;
import jupiter.common.test.IntervalArguments;
import jupiter.math.analysis.function.bivariate.BivariateFunction;
import jupiter.math.analysis.function.univariate.UnivariateFunction;

/**
 * {@link Integrator} is the {@link BivariateFunction} integrating {@code y = f(x)} for {@code x}
 * defined in the integration {@link DoubleInterval}.
 */
public abstract class Integrator
		extends BivariateFunction {

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
	 * The integration order.
	 */
	protected final int order;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Integrator} with the specified {@link UnivariateFunction}.
	 * <p>
	 * @param f the {@link UnivariateFunction} to integrate
	 */
	public Integrator(final UnivariateFunction f) {
		this(f, 1);
	}

	/**
	 * Constructs a {@link Integrator} with the specified {@link UnivariateFunction} and integration
	 * {@link DoubleInterval}.
	 * <p>
	 * @param f        the {@link UnivariateFunction} to integrate
	 * @param interval the integration {@link DoubleInterval}
	 */
	public Integrator(final UnivariateFunction f, final DoubleInterval interval) {
		this(f, interval, 1);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Integrator} with the specified {@link UnivariateFunction} and integration
	 * order.
	 * <p>
	 * @param f     the {@link UnivariateFunction} to integrate
	 * @param order the integration order
	 */
	public Integrator(final UnivariateFunction f, final int order) {
		this(f, new DoubleInterval(f.getDomain().getLowerBound(), f.getDomain().getUpperBound()),
				order);
	}

	/**
	 * Constructs a {@link Integrator} with the specified {@link UnivariateFunction}, integration
	 * {@link DoubleInterval} and integration order.
	 * <p>
	 * @param f        the {@link UnivariateFunction} to integrate
	 * @param interval the integration {@link DoubleInterval}
	 * @param order    the integration order
	 */
	public Integrator(final UnivariateFunction f, final DoubleInterval interval,
			final int order) {
		super(new Domain(interval), interval.getLowerBoundValue());

		// Check the arguments
		IntervalArguments.requireValid(interval, "integration interval");
		IntervalArguments.requireInside(interval, "integration interval", f.getDomain(), "domain");
		IntegerArguments.requirePositive(order);

		// Set the attributes
		this.f = f;
		this.order = order;
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

	/**
	 * Returns the integration order.
	 * <p>
	 * @return the integration order
	 */
	public int getIntegrationOrder() {
		return order;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the integration function to the specified value.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code Y = F(x)} for {@code x} defined in {@code domain}
	 *
	 * @see #integrate(double)
	 */
	@Override
	protected double a(final double x) {
		return integrate(x);
	}

	/**
	 * Returns the integrated {@code double} value {@code Y = F(x)} for {@code x} defined in
	 * {@code domain}.
	 * <p>
	 * @param x a {@code double} value (on the abscissa)
	 * <p>
	 * @return {@code Y = F(x)} for {@code x} defined in {@code domain}
	 */
	protected abstract double integrate(final double x);

	//////////////////////////////////////////////

	/**
	 * Applies the integration function to the specified integration interval.
	 * <p>
	 * @param a the {@code double} lower bound (on the abscissa) of the integration interval
	 * @param b the {@code double} upper bound (on the abscissa) of the integration interval
	 * <p>
	 * @return {@code Y = F(b) - F(a)} for {@code a} and {@code b} defined in {@code domain}
	 *
	 * @see #integrate(double, double)
	 */
	@Override
	protected double a(final double a, final double b) {
		return integrate(a, b);
	}

	/**
	 * Returns the integrated {@code double} value {@code Y = F(b) - F(a)} for {@code a} and
	 * {@code b} defined in {@code domain}.
	 * <p>
	 * @param a the {@code double} lower bound (on the abscissa) of the integration interval
	 * @param b the {@code double} upper bound (on the abscissa) of the integration interval
	 * <p>
	 * @return {@code Y = F(b) - F(a)} for {@code a} and {@code b} defined in {@code domain}
	 */
	protected abstract double integrate(final double a, final double b);

	//////////////////////////////////////////////

	/**
	 * Integrates {@code y = f(x)} for all {@code x} defined in {@code domain} and then use
	 * {@link #integrate(double, double)} to retrieve {@code Y = F(b) - F(a)} for {@code a} and
	 * {@code b} defined in {@code domain}.
	 * <p>
	 * @return {@code true} if the integration is done, {@code false} otherwise
	 *
	 * @see #integrate(double, double)
	 */
	public abstract boolean integrateAll();


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
	public Integrator clone() {
		return (Integrator) super.clone();
	}
}
