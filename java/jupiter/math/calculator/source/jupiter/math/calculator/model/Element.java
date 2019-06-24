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

import jupiter.common.util.Strings;
import jupiter.math.linear.entity.Entity;

public abstract class Element {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Element parent;
	protected String expression;
	protected Entity entity;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Element(final Element parent, final String expression) {
		this.parent = parent;
		this.expression = expression;
	}

	protected Element(final Element parent, final String expression, final Entity entity) {
		this.parent = parent;
		this.expression = expression;
		this.entity = entity;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the parent.
	 * <p>
	 * @return the parent
	 */
	public Element getParent() {
		return parent;
	}

	/**
	 * Returns the expression.
	 * <p>
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Returns the entity.
	 * <p>
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the parent.
	 * <p>
	 * @param parent an {@link Element}
	 */
	public void setParent(final Element parent) {
		this.parent = parent;
	}

	/**
	 * Sets the expression.
	 * <p>
	 * @param expression a {@link String}
	 */
	public void setExpression(final String expression) {
		this.expression = expression;
	}

	/**
	 * Sets the entity.
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
		return Strings.toString(entity);
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
