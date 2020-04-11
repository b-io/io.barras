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
package jupiter.math.calculator.model;

import java.io.Serializable;

import jupiter.common.util.Objects;
import jupiter.math.linear.entity.Entity;

public abstract class Element
		implements Serializable {

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
	 * The parent {@link Element}.
	 */
	protected Element parent;
	/**
	 * The expression {@link String}.
	 */
	protected String expression;
	/**
	 * The {@link Entity}.
	 */
	protected Entity entity;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link Element} with the specified parent {@link Element} and expression
	 * {@link String}.
	 * <p>
	 * @param parent     the parent {@link Element}
	 * @param expression the expression {@link String}
	 */
	protected Element(final Element parent, final String expression) {
		this.parent = parent;
		this.expression = expression;
	}

	/**
	 * Constructs an {@link Element} with the specified parent {@link Element}, expression
	 * {@link String} and {@link Entity}.
	 * <p>
	 * @param parent     the parent {@link Element}
	 * @param expression the expression {@link String}
	 * @param entity     the {@link Entity}
	 */
	protected Element(final Element parent, final String expression, final Entity entity) {
		this.parent = parent;
		this.expression = expression;
		this.entity = entity;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the parent {@link Element}.
	 * <p>
	 * @return the parent {@link Element}
	 */
	public Element getParent() {
		return parent;
	}

	/**
	 * Returns the expression {@link String}.
	 * <p>
	 * @return the expression {@link String}
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Returns the {@link Entity}.
	 * <p>
	 * @return the {@link Entity}
	 */
	public Entity getEntity() {
		return entity;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the parent {@link Element}.
	 * <p>
	 * @param parent an {@link Element}
	 */
	public void setParent(final Element parent) {
		this.parent = parent;
	}

	/**
	 * Sets the expression {@link String}.
	 * <p>
	 * @param expression a {@link String}
	 */
	public void setExpression(final String expression) {
		this.expression = expression;
	}

	/**
	 * Sets the {@link Entity}.
	 * <p>
	 * @param entity an {@link Entity}
	 */
	public void setEntity(final Entity entity) {
		this.entity = entity;
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
		return Objects.toString(entity);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum Type {
		ENTITY,

		ADDITION,
		SUBTRACTION,
		MULTIPLICATION,
		DIVISION,
		POWER,
		SOLUTION,

		FACTORIAL,
		INVERSE,
		TRANSPOSE,

		LEFT_PARENTHESIS,
		RIGHT_PARENTHESIS,
		LEFT_BRACKET,
		RIGHT_BRACKET
	}
}
