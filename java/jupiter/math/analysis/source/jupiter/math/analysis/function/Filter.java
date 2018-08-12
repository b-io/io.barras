/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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
package jupiter.math.analysis.function;

public class Filter
		extends Function {

	protected final double limit;
	protected final double a, b;

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Filter() {
		limit = 0;
		a = 0;
		b = 1;
	}

	public Filter(final double limit, final double a, final double b) {
		this.limit = limit;
		this.a = a;
		this.b = b;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the filter function to the specified value and returns the result.
	 * <p>
	 * @param x a {@code double} value
	 * <p>
	 * @return the result
	 */
	@Override
	public double apply(final double x) {
		return x < limit ? a : b;
	}
}
