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

import static jupiter.common.util.Strings.SPACE;

import jupiter.common.util.Strings;

public class BinaryOperation
		extends Element {

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
	 * The operation {@link Type}.
	 */
	protected Type type;
	/**
	 * The left {@link Element}.
	 */
	protected Element left;
	/**
	 * The right {@link Element}.
	 */
	protected Element right;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link BinaryOperation} with the specified parent {@link Element}, expression
	 * {@link String}, operation {@link Type}, left {@link Element} and right {@link Element}.
	 * <p>
	 * @param parent     the parent {@link Element}
	 * @param expression the expression {@link String}
	 * @param type       the operation {@link Type}
	 * @param left       the left {@link Element}
	 * @param right      the right {@link Element}
	 */
	public BinaryOperation(final Element parent, final String expression, final Type type,
			final Element left, final Element right) {
		super(parent, expression);
		this.type = type;
		this.left = left;
		this.right = right;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the operation {@link Type}.
	 * <p>
	 * @return the operation {@link Type}
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the left {@link Element}.
	 * <p>
	 * @return the left {@link Element}
	 */
	public Element getLeft() {
		return left;
	}

	/**
	 * Returns the right {@link Element}.
	 * <p>
	 * @return the right {@link Element}
	 */
	public Element getRight() {
		return right;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the operation type.
	 * <p>
	 * @param type a {@link jupiter.math.calculator.model.Element.Type}
	 */
	public void setType(final Type type) {
		this.type = type;
	}

	/**
	 * Sets the left {@link Element}.
	 * <p>
	 * @param left an {@link Element}
	 */
	public void setLeft(final Element left) {
		this.left = left;
	}

	/**
	 * Sets the right {@link Element}.
	 * <p>
	 * @param right an {@link Element}
	 */
	public void setRight(final Element right) {
		this.right = right;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Strings.toString(left) + SPACE + type + SPACE + right;
	}
}
