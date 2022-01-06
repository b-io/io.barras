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
package jupiter.math.analysis.function.univariate;

/**
 * {@link UnivariateFunctions} is a collection of {@link UnivariateFunction}.
 */
public class UnivariateFunctions {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Absolute ABS = new Absolute();
	public static final Exponential EXP = new Exponential();
	public static final Factorial FACTORIAL = new Factorial();
	public static final Inverse INV = new Inverse();
	public static final Logarithm LOG = new Logarithm();
	public static final Root ROOT = new Root();

	public static final Floor FLOOR = new Floor();
	public static final Ceil CEIL = new Ceil();
	public static final Round ROUND = new Round();

	public static final Cosine COS = new Cosine();
	public static final HyperbolicCosine COSH = new HyperbolicCosine();
	public static final Sine SIN = new Sine();
	public static final HyperbolicSine SINH = new HyperbolicSine();
	public static final Tangent TAN = new Tangent();
	public static final HyperbolicTangent TANH = new HyperbolicTangent();
	public static final Haversine HAV = new Haversine();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link UnivariateFunctions}.
	 */
	protected UnivariateFunctions() {
	}
}
