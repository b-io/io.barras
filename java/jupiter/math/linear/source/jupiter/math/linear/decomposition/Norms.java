/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.math.linear.decomposition;

public class Norms {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Norms}.
	 */
	protected Norms() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the Euclidean norm of {@code a} and {@code b} without under/overflow.
	 * <p>
	 * @param a a {@code double} value
	 * @param b another {@code double} value
	 * <p>
	 * @return the Euclidean norm of {@code a} and {@code b}
	 */
	public static double getEuclideanNorm(final double a, final double b) {
		double euclideanNorm;
		final double absA = Math.abs(a);
		final double absB = Math.abs(b);
		if (absA > absB) {
			euclideanNorm = b / a;
			euclideanNorm = absA * Math.sqrt(1 + euclideanNorm * euclideanNorm);
		} else if (absB > 0.) {
			euclideanNorm = a / b;
			euclideanNorm = absB * Math.sqrt(1 + euclideanNorm * euclideanNorm);
		} else {
			euclideanNorm = 0.;
		}
		return euclideanNorm;
	}
}
