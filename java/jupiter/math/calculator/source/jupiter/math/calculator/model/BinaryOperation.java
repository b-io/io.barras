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
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Type type;
	protected Element left;
	protected Element right;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public BinaryOperation(final Element parent, final String expression, final Type type,
			final Element left, final Element right) {
		super(parent, expression);
		this.type = type;
		this.left = left;
		this.right = right;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the type.
	 * <p>
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the left part.
	 * <p>
	 * @return the left part
	 */
	public Element getLeft() {
		return left;
	}

	/**
	 * Returns the right part.
	 * <p>
	 * @return the right part
	 */
	public Element getRight() {
		return right;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the type.
	 * <p>
	 * @param type a {@link jupiter.math.calculator.model.Element.Type}
	 */
	public void setType(final Type type) {
		this.type = type;
	}

	/**
	 * Sets the left part.
	 * <p>
	 * @param left an {@link Element}
	 */
	public void setLeft(final Element left) {
		this.left = left;
	}

	/**
	 * Sets the right part.
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
