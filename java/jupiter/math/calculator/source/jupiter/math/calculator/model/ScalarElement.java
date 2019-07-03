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
package jupiter.math.calculator.model;

import jupiter.math.linear.entity.Scalar;

public class ScalarElement
		extends Element {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ScalarElement} with the specified parent {@link Element} and expression
	 * {@link String}.
	 * <p>
	 * @param parent     the parent {@link Element}
	 * @param expression the expression {@link String}
	 */
	public ScalarElement(final Element parent, final String expression) {
		super(parent, expression, new Scalar(Double.parseDouble(expression)));
	}

	/**
	 * Constructs a {@link ScalarElement} with the specified parent {@link Element}, expression
	 * {@link String} and {@link Scalar}.
	 * <p>
	 * @param parent     the parent {@link Element}
	 * @param expression the expression {@link String}
	 * @param scalar     the {@link Scalar}
	 */
	public ScalarElement(final Element parent, final String expression, final Scalar scalar) {
		super(parent, expression, scalar);
	}
}
