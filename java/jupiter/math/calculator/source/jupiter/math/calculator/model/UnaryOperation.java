/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.util.Characters.SPACE;

import jupiter.common.util.Strings;
import jupiter.math.calculator.model.Element.Type;

public class UnaryOperation
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
	 * The {@link Element}.
	 */
	protected Element element;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link UnaryOperation} with the specified parent {@link Element}, expression
	 * {@link String}, operation {@link Type} and {@link Element}.
	 * <p>
	 * @param parent     the parent {@link Element}
	 * @param expression the expression {@link String}
	 * @param type       the operation {@link Type}
	 * @param element    the {@link Element}
	 */
	public UnaryOperation(final Element parent, final String expression, final Type type,
			final Element element) {
		super(parent, expression);
		this.type = type;
		this.element = element;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
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
	 * Returns the {@link Element}.
	 * <p>
	 * @return the {@link Element}
	 */
	public Element getElement() {
		return element;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the operation {@link Type}.
	 * <p>
	 * @param type a {@link Type}
	 */
	public void setType(final Type type) {
		this.type = type;
	}

	/**
	 * Sets the {@link Element}.
	 * <p>
	 * @param element an {@link Element}
	 */
	public void setElement(final Element element) {
		this.element = element;
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
		return Strings.join(type, SPACE, element);
	}
}
