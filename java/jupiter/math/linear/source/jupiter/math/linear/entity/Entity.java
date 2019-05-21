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
package jupiter.math.linear.entity;

import java.io.Serializable;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.exception.IllegalTypeException;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Strings;
import jupiter.math.analysis.function.Function;

public abstract class Entity
		implements ICloneable<Entity>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 2805059315080715833L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the name.
	 * <p>
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Returns the dimensions of {@code this}.
	 * <p>
	 * @return the dimensions of {@code this}
	 */
	public abstract Dimensions getDimensions();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts {@code this} to a {@link Scalar}.
	 * <p>
	 * @return a {@link Scalar}
	 */
	public abstract Scalar toScalar();

	/**
	 * Converts {@code this} to a {@link Vector}.
	 * <p>
	 * @return a {@link Vector}
	 */
	public abstract Vector toVector();

	/**
	 * Converts {@code this} to a {@link Matrix}.
	 * <p>
	 * @return a {@link Matrix}
	 */
	public abstract Matrix toMatrix();

	/**
	 * Converts {@code this} to an array of {@code double} values.
	 * <p>
	 * @return an array of {@code double} values
	 */
	public abstract double[] toPrimitiveArray();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the mean of {@code this}.
	 * <p>
	 * @return {@code mean(this)}
	 */
	public abstract Entity mean();

	/**
	 * Returns the size of {@code this}.
	 * <p>
	 * @return {@code size(this)}
	 */
	public abstract Entity size();

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the identity of {@code size(this)}.
	 * <p>
	 * @return {@code eye(size(this))}
	 */
	public abstract Entity identity();

	/**
	 * Returns the randomization of {@code size(this)}.
	 * <p>
	 * @return {@code rand(size(this))}
	 */
	public abstract Entity random();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fills {@code this} with the specified value.
	 * <p>
	 * @param value the value to fill with
	 */
	public abstract void fill(final double value);


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
	public abstract Entity apply(final Function f);

	/**
	 * Returns the negation of {@code this}.
	 * <p>
	 * @return {@code -this}
	 */
	public abstract Entity minus();

	/**
	 * Returns the sum of the elements.
	 * <p>
	 * @return the sum of the elements
	 */
	public abstract double sum();

	/**
	 * Returns the transpose of {@code this}.
	 * <p>
	 * @return {@code this'}
	 */
	public abstract Entity transpose();


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
	public Entity plus(final Entity entity) {
		if (entity instanceof Scalar) {
			return plus(((Scalar) entity).value);
		} else if (entity instanceof Matrix) {
			return plus((Matrix) entity);
		}
		throw new IllegalOperationException(
				"Cannot add a " + entity.getName() + " to a " + getName());
	}

	/**
	 * Returns the addition of the specified scalar to {@code this}.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this + scalar}
	 */
	public abstract Entity plus(final double scalar);

	/**
	 * Returns the addition of the specified {@link Matrix} to {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this + matrix}
	 */
	public abstract Matrix plus(final Matrix matrix);

	//////////////////////////////////////////////

	/**
	 * Adds the specified {@link Entity} to {@code this}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this += entity}
	 */
	public Entity add(final Entity entity) {
		if (entity instanceof Scalar) {
			return add(((Scalar) entity).value);
		} else if (entity instanceof Matrix) {
			return add((Matrix) entity);
		}
		throw new IllegalOperationException(
				"Cannot add a " + entity.getName() + " to a " + getName());
	}

	/**
	 * Adds the specified scalar to {@code this}.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this += scalar}
	 */
	public abstract Entity add(final double scalar);

	/**
	 * Adds the specified {@link Matrix} to {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this += matrix}
	 */
	public abstract Matrix add(final Matrix matrix);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the subtraction of the specified {@link Entity} from {@code this}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this - entity}
	 */
	public Entity minus(final Entity entity) {
		if (entity instanceof Scalar) {
			return minus(((Scalar) entity).value);
		} else if (entity instanceof Matrix) {
			return minus((Matrix) entity);
		}
		throw new IllegalOperationException(
				"Cannot subtract a " + entity.getName() + " from a " + getName());
	}

	/**
	 * Returns the subtraction of the specified scalar from {@code this}.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this - scalar}
	 */
	public abstract Entity minus(final double scalar);

	/**
	 * Returns the subtraction of the specified {@link Matrix} from {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this - matrix}
	 */
	public abstract Matrix minus(final Matrix matrix);

	//////////////////////////////////////////////

	/**
	 * Subtracts the specified {@link Entity} from {@code this}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this -= entity}
	 */
	public Entity subtract(final Entity entity) {
		if (entity instanceof Scalar) {
			return subtract(((Scalar) entity).value);
		} else if (entity instanceof Matrix) {
			return subtract((Matrix) entity);
		}
		throw new IllegalOperationException(
				"Cannot subtract a " + entity.getName() + " from a " + getName());
	}

	/**
	 * Subtracts the specified scalar from {@code this}.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this -= scalar}
	 */
	public abstract Entity subtract(final double scalar);

	/**
	 * Subtracts the specified {@link Matrix} from {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this -= matrix}
	 */
	public abstract Matrix subtract(final Matrix matrix);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the multiplication of {@code this} by the specified {@link Entity}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this * entity}
	 */
	public Entity times(final Entity entity) {
		if (entity instanceof Scalar) {
			return times(((Scalar) entity).value);
		} else if (entity instanceof Matrix) {
			return times((Matrix) entity);
		}
		throw new IllegalOperationException(
				"Cannot multiply a " + getName() + " by a " + entity.getName());
	}

	/**
	 * Returns the multiplication of {@code this} by the specified scalar.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this * scalar}
	 */
	public abstract Entity times(final double scalar);

	/**
	 * Returns the multiplication of {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this * matrix}
	 * <p>
	 * @throws IllegalArgumentException if the inner dimensions of the matrices do not agree
	 */
	public abstract Entity times(final Matrix matrix);

	/**
	 * Returns the element-by-element multiplication of {@code this} by the specified
	 * {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this .* matrix}
	 */
	public abstract Matrix arrayTimes(final Matrix matrix);

	//////////////////////////////////////////////

	/**
	 * Multiplies {@code this} by the specified scalar.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this *= scalar}
	 */
	public abstract Entity multiply(final double scalar);

	/**
	 * Multiplies {@code this} by the specified {@link Matrix} element-by-element.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this .*= matrix}
	 */
	public abstract Matrix arrayMultiply(final Matrix matrix);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the division of {@code this} by the specified {@link Entity}.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this / entity}
	 */
	public Entity division(final Entity entity) {
		if (entity instanceof Scalar) {
			return division(((Scalar) entity).value);
		} else if (entity instanceof Matrix) {
			return division((Matrix) entity);
		}
		throw new IllegalOperationException(
				"Cannot divide a " + getName() + " by a " + entity.getName());
	}

	/**
	 * Returns the division of {@code this} by the specified scalar.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this / scalar}
	 */
	public abstract Entity division(final double scalar);

	/**
	 * Returns the division of {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this / matrix}
	 */
	public abstract Entity division(final Matrix matrix);

	/**
	 * Returns the element-by-element division of {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this ./ matrix}
	 */
	public abstract Matrix arrayDivision(final Matrix matrix);

	//////////////////////////////////////////////

	/**
	 * Divides {@code this} by the specified scalar.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this /= scalar}
	 */
	public abstract Entity divide(final double scalar);

	/**
	 * Divides {@code this} by the specified {@link Matrix} element-by-element.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this ./= matrix}
	 */
	public abstract Matrix arrayDivide(final Matrix matrix);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value of {@code this} raised to the power of the specified scalar.
	 * <p>
	 * @param scalar an {@code int} value
	 * <p>
	 * @return {@code this ^ scalar}
	 */
	public Entity power(final int scalar) {
		Entity result = this;
		for (int i = 1; i < scalar; ++i) {
			result = times(result);
		}
		return result;
	}

	/**
	 * Returns the value of {@code this} raised to the power of the specified {@link Entity}
	 * element-by-element.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this .^ entity}
	 */
	public Entity arrayPower(final Entity entity) {
		if (entity instanceof Scalar) {
			return arrayPower(((Scalar) entity).value);
		} else if (entity instanceof Matrix) {
			return arrayPower((Matrix) entity);
		}
		throw new IllegalOperationException(
				"Cannot raise " + getName() + " to the power of " + entity.getName());
	}

	/**
	 * Returns the value of {@code this} raised to the power of the specified scalar
	 * element-by-element.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this .^ scalar}
	 */
	public abstract Entity arrayPower(final double scalar);

	/**
	 * Returns the value of {@code this} raised to the power of the specified {@link Matrix}
	 * element-by-element.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this .^ matrix}
	 */
	public abstract Matrix arrayPower(final Matrix matrix);

	//////////////////////////////////////////////

	/**
	 * Raises {@code this} to the power of the specified {@link Entity} element-by-element.
	 * <p>
	 * @param entity an {@link Entity}
	 * <p>
	 * @return {@code this .^= entity}
	 */
	public Entity arrayRaise(final Entity entity) {
		if (entity instanceof Scalar) {
			return arrayRaise(((Scalar) entity).value);
		} else if (entity instanceof Matrix) {
			return arrayRaise((Matrix) entity);
		}
		throw new IllegalOperationException(
				"Cannot raise " + getName() + " to the power of " + entity.getName());
	}

	/**
	 * Raises {@code this} to the power of the specified scalar element-by-element.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this .^= scalar}
	 */
	public abstract Entity arrayRaise(final double scalar);

	/**
	 * Raises {@code this} to the power of the specified {@link Matrix} element-by-element.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this .^= matrix}
	 */
	public abstract Matrix arrayRaise(final Matrix matrix);


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
	public abstract Entity solve(final Entity entity);

	/**
	 * Returns the inverse of {@code this}.
	 * <p>
	 * @return {@code inv(this)}
	 */
	public abstract Entity inverse();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public abstract Entity clone();

	@Override
	public abstract boolean equals(final Object other);

	public abstract boolean equals(final Object other, final double tolerance);

	@Override
	public abstract String toString();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum BinaryOperation {
		ADDITION("ADDITION"),
		SUBTRACTION("SUBTRACTION"),
		MULTIPLICATION("MULTIPLICATION"),
		DIVISION("DIVISION"),
		POWER("POWER");

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
			} else if (POWER.value.equals(value)) {
				return POWER;
			}
			throw new IllegalTypeException(name);
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
