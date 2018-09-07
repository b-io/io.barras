/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.math.linear.entity;

import java.io.Serializable;

import jupiter.common.exception.IllegalTypeException;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Strings;
import jupiter.math.analysis.function.Function;

public interface Entity
		extends ICloneable<Entity>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the name.
	 * <p>
	 * @return the name
	 */
	public String getName();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts {@code this} to a {@link Scalar}.
	 * <p>
	 * @return a {@link Scalar}
	 */
	public Scalar toScalar();

	/**
	 * Converts {@code this} to a {@link Vector}.
	 * <p>
	 * @return a {@link Vector}
	 */
	public Vector toVector();

	/**
	 * Converts {@code this} to a {@link Matrix}.
	 * <p>
	 * @return a {@link Matrix}
	 */
	public Matrix toMatrix();

	/**
	 * Converts {@code this} to an array of {@code double} values.
	 * <p>
	 * @return an array of {@code double} values
	 */
	public double[] toPrimitiveArray();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the mean of {@code this}.
	 * <p>
	 * @return {@code mean(this)}
	 */
	public Entity mean();

	/**
	 * Returns the size of {@code this}.
	 * <p>
	 * @return {@code size(this)}
	 */
	public Entity size();

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the identity of {@code size(this)}.
	 * <p>
	 * @return {@code eye(size(this))}
	 */
	public Entity identity();

	/**
	 * Returns the randomization of {@code size(this)}.
	 * <p>
	 * @return {@code rand(size(this))}
	 */
	public Entity random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fills {@code this} with the specified value.
	 * <p>
	 * @param value the value to fill with
	 */
	public void fill(final double value);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// UNARY OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the specified function to {@code this}.
	 * <p>
	 * @param f the {@link Function} to apply
	 * <p>
	 * @return {@code f(this)}
	 */
	public Entity apply(final Function f);

	/**
	 * Returns the negation of {@code this}.
	 * <p>
	 * @return {@code -this}
	 */
	public Entity minus();

	/**
	 * Returns the transpose of {@code this}.
	 * <p>
	 * @return {@code this'}
	 */
	public Entity transpose();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// BINARY OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the addition of the specified {@link Entity} to {@code this}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this + entity}
	 */
	public Entity plus(Entity entity);


	/**
	 * Returns the subtraction of the specified {@link Entity} from {@code this}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this - entity}
	 */
	public Entity minus(Entity entity);

	/**
	 * Returns the multiplication of {@code this} by the specified {@link Entity}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this * entity}
	 */
	public Entity times(Entity entity);

	/**
	 * Returns the division of {@code this} by the specified {@link Entity}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this / entity}
	 */
	public Entity division(Entity entity);

	/**
	 * Returns the value of {@code this} raised to the power of the specified {@link Entity}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this ^ entity}
	 */
	public Entity power(Entity entity);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SOLVER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the solution X of {@code this * X = entity}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return the solution X of {@code this * X = entity}
	 */
	public Entity solve(Entity entity);

	/**
	 * Returns the inverse of {@code this}.
	 * <p>
	 * @return {@code inv(this)}
	 */
	public Entity inverse();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Entity clone();

	public boolean equals(final Object other);

	public boolean equals(final Object other, final double tolerance);

	public String toString();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum BinaryOperation {
		ADDITION("ADDITION"),
		SUBTRACTION("SUBTRACTION"),
		MULTIPLICATION("MULTIPLICATION"),
		DIVISION("DIVISION");

		public final String value;

		private BinaryOperation(final String value) {
			this.value = value;
		}

		public static BinaryOperation get(final String name) {
			final String value = Strings.toUpperCase(name);
			if (ADDITION.value.equals(value)) {
				return ADDITION;
			} else if (SUBTRACTION.value.equals(value)) {
				return SUBTRACTION;
			} else if (MULTIPLICATION.value.equals(value)) {
				return MULTIPLICATION;
			} else if (DIVISION.value.equals(value)) {
				return DIVISION;
			}
			throw new IllegalTypeException(name);
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
