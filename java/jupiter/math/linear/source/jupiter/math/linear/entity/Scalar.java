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

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.math.Maths;
import jupiter.common.util.Doubles;
import jupiter.common.util.Formats;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.math.analysis.function.Function;

public class Scalar
		extends Entity {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 4598899924191933856L;

	public static final Dimensions DIMENSIONS = new Dimensions(1, 1);

	public static final Scalar ZERO = new Scalar(0., true);
	public static final Scalar ONE = new Scalar(1., true);
	public static final Scalar TEN = new Scalar(10., true);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether {@code this} is read-only.
	 */
	protected final boolean readOnly;

	/**
	 * The value.
	 */
	protected double value;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Scalar() {
		this(0.);
	}

	public Scalar(final double value) {
		this.value = value;
		readOnly = false;
	}

	//////////////////////////////////////////////

	public Scalar(final boolean readOnly) {
		this(0., readOnly);
	}

	public Scalar(final double value, final boolean readOnly) {
		this.value = value;
		this.readOnly = readOnly;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the name.
	 * <p>
	 * @return the name
	 */
	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	/**
	 * Returns the dimensions.
	 * <p>
	 * @return the dimensions
	 */
	@Override
	public Dimensions getDimensions() {
		return DIMENSIONS;
	}

	/**
	 * Returns the value.
	 * <p>
	 * @return the value
	 */
	public double get() {
		return value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the value.
	 * <p>
	 * @param value a {@code double} value
	 */
	public void set(final double value) {
		if (!readOnly) {
			this.value = value;
		} else {
			throw new IllegalOperationException(
					"Cannot change the value of a read-only " + getName());
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts {@code this} to a {@link Scalar}.
	 * <p>
	 * @return a {@link Scalar}
	 */
	@Override
	public Scalar toScalar() {
		return this;
	}

	/**
	 * Converts {@code this} to a {@link Vector}.
	 * <p>
	 * @return a {@link Vector}
	 */
	@Override
	public Vector toVector() {
		return new Vector(new double[][] {
			new double[] {
				value
			}
		});
	}

	/**
	 * Converts {@code this} to a {@link Matrix}.
	 * <p>
	 * @return a {@link Matrix}
	 */
	@Override
	public Matrix toMatrix() {
		return new Matrix(new double[][] {
			new double[] {
				value
			}
		});
	}

	/**
	 * Converts {@code this} to an array of {@code double} values.
	 * <p>
	 * @return an array of {@code double} values
	 */
	@Override
	public double[] toPrimitiveArray() {
		return new double[] {
			value
		};
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the mean of {@code this}.
	 * <p>
	 * @return {@code mean(this)}
	 */
	@Override
	public Scalar mean() {
		return clone();
	}

	/**
	 * Returns the size of {@code this}.
	 * <p>
	 * @return {@code size(this)}
	 */
	@Override
	public Scalar size() {
		return new Scalar(1.);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the identity of {@code size(this)}.
	 * <p>
	 * @return {@code eye(size(this))}
	 */
	@Override
	public Scalar identity() {
		return new Scalar(1.);
	}

	/**
	 * Returns the randomization of {@code size(this)}.
	 * <p>
	 * @return {@code rand(size(this))}
	 */
	@Override
	public Scalar random() {
		return new Scalar(Doubles.random());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fills {@code this} with the specified value.
	 * <p>
	 * @param value the value to fill with
	 */
	@Override
	public void fill(final double value) {
		if (!readOnly) {
			this.value = value;
		} else {
			throw new IllegalOperationException(
					"Cannot change the value of a read-only " + getName());
		}
	}


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
	@Override
	public Scalar apply(final Function f) {
		return new Scalar(f.apply(value));
	}

	/**
	 * Returns the negation of {@code this}.
	 * <p>
	 * @return {@code -this}
	 */
	@Override
	public Scalar minus() {
		return new Scalar(-value);
	}

	/**
	 * Returns the sum of the elements.
	 * <p>
	 * @return the sum of the elements
	 */
	@Override
	public double sum() {
		return value;
	}

	/**
	 * Returns the transpose of {@code this}.
	 * <p>
	 * @return {@code this'}
	 */
	@Override
	public Scalar transpose() {
		return this;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// BINARY OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the addition of the specified scalar to {@code this}.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this + scalar}
	 */
	@Override
	public Scalar plus(final double scalar) {
		return new Scalar(value + scalar);
	}

	/**
	 * Returns the addition of the specified {@link Matrix} to {@code this}.
	 * <p>
	 * @param matrix a {@code Matrix}
	 * <p>
	 * @return {@code this + matrix}
	 */
	@Override
	public Matrix plus(final Matrix matrix) {
		return matrix.plus(value);
	}

	//////////////////////////////////////////////

	/**
	 * Adds the specified scalar to {@code this}.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this += scalar}
	 */
	@Override
	public Scalar add(final double scalar) {
		if (!readOnly) {
			value += scalar;
		} else {
			throw new IllegalOperationException(
					"Cannot change the value of a read-only " + getName());
		}
		return this;
	}

	/**
	 * Adds the specified {@link Matrix} to {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this += matrix}
	 */
	@Override
	public Matrix add(final Matrix matrix) {
		for (int i = 0; i < matrix.m; ++i) {
			for (int j = 0; j < matrix.n; ++j) {
				matrix.elements[i][j] += value;
			}
		}
		return matrix;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the subtraction of the specified scalar from {@code this}.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this - scalar}
	 */
	@Override
	public Scalar minus(final double scalar) {
		return new Scalar(value - scalar);
	}

	/**
	 * Returns the subtraction of the specified {@link Matrix} from {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this - matrix}
	 */
	@Override
	public Matrix minus(final Matrix matrix) {
		final Matrix result = new Matrix(matrix.m, matrix.n);
		for (int i = 0; i < matrix.m; ++i) {
			for (int j = 0; j < matrix.n; ++j) {
				result.elements[i][j] = value - matrix.elements[i][j];
			}
		}
		return result;
	}

	//////////////////////////////////////////////

	/**
	 * Subtracts the specified scalar from {@code this}.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this -= scalar}
	 */
	@Override
	public Scalar subtract(final double scalar) {
		if (!readOnly) {
			value -= scalar;
		} else {
			throw new IllegalOperationException(
					"Cannot change the value of a read-only " + getName());
		}
		return this;
	}

	/**
	 * Subtracts the specified {@link Matrix} from {@code this}.
	 * <p>
	 * @param matrix a {@code Matrix}
	 * <p>
	 * @return {@code this -= matrix}
	 */
	@Override
	public Matrix subtract(final Matrix matrix) {
		for (int i = 0; i < matrix.m; ++i) {
			for (int j = 0; j < matrix.n; ++j) {
				matrix.elements[i][j] = value - matrix.elements[i][j];
			}
		}
		return matrix;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the multiplication of {@code this} by the specified scalar.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this * scalar}
	 */
	@Override
	public Scalar times(final double scalar) {
		return new Scalar(value * scalar);
	}

	/**
	 * Returns the multiplication of {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this * matrix}
	 * <p>
	 * @throws IllegalArgumentException if the inner dimensions of the matrices do not agree
	 */
	@Override
	public Matrix times(final Matrix matrix) {
		return matrix.times(value);
	}

	/**
	 * Returns the element-by-element multiplication of {@code this} by the specified
	 * {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this .* matrix}
	 */
	@Override
	public Matrix arrayTimes(final Matrix matrix) {
		return times(matrix);
	}

	//////////////////////////////////////////////

	/**
	 * Multiplies {@code this} by the specified scalar.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this *= scalar}
	 */
	@Override
	public Scalar multiply(final double scalar) {
		if (!readOnly) {
			value *= scalar;
		} else {
			throw new IllegalOperationException(
					"Cannot change the value of a read-only " + getName());
		}
		return this;
	}

	/**
	 * Multiplies {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this *= matrix}
	 */
	@Override
	public Matrix multiply(final Matrix matrix) {
		return matrix.multiply(value);
	}

	/**
	 * Multiplies {@code this} by the specified {@link Matrix} element-by-element.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this .*= matrix}
	 */
	@Override
	public Matrix arrayMultiply(final Matrix matrix) {
		return multiply(matrix);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the division of {@code this} by the specified scalar.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this / scalar}
	 */
	@Override
	public Scalar division(final double scalar) {
		return new Scalar(value / scalar);
	}

	/**
	 * Returns the division of {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this / matrix}
	 */
	@Override
	public Matrix division(final Matrix matrix) {
		final Matrix result = new Matrix(matrix.m, matrix.n);
		for (int i = 0; i < matrix.m; ++i) {
			for (int j = 0; j < matrix.n; ++j) {
				result.elements[i][j] = value / matrix.elements[i][j];
			}
		}
		return result;
	}

	/**
	 * Returns the element-by-element division of the specified {@link Matrix} by {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this ./ matrix}
	 */
	@Override
	public Matrix arrayDivision(final Matrix matrix) {
		return division(matrix);
	}

	//////////////////////////////////////////////

	/**
	 * Divides {@code this} by the specified scalar.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this /= scalar}
	 */
	@Override
	public Scalar divide(final double scalar) {
		if (!readOnly) {
			value /= scalar;
		} else {
			throw new IllegalOperationException(
					"Cannot change the value of a read-only " + getName());
		}
		return this;
	}

	/**
	 * Divides {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this /= matrix}
	 */
	@Override
	public Matrix divide(final Matrix matrix) {
		for (int i = 0; i < matrix.m; ++i) {
			for (int j = 0; j < matrix.n; ++j) {
				matrix.elements[i][j] = value / matrix.elements[i][j];
			}
		}
		return matrix;
	}

	/**
	 * Divides {@code this} by the specified {@link Matrix} element-by-element.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this ./= matrix}
	 */
	@Override
	public Matrix arrayDivide(final Matrix matrix) {
		return divide(matrix);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value of {@code this} raised to the power of the specified scalar
	 * element-by-element.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this .^ scalar}
	 */
	@Override
	public Scalar arrayPower(final double scalar) {
		return new Scalar(Math.pow(value, scalar));
	}

	/**
	 * Returns the value of {@code this} raised to the power of the specified {@link Matrix}
	 * element-by-element.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this .^ matrix}
	 */
	@Override
	public Matrix arrayPower(final Matrix matrix) {
		final Matrix result = new Matrix(matrix.m, matrix.n);
		for (int i = 0; i < matrix.m; ++i) {
			for (int j = 0; j < matrix.n; ++j) {
				result.elements[i][j] = Math.pow(value, matrix.elements[i][j]);
			}
		}
		return result;
	}

	//////////////////////////////////////////////

	/**
	 * Raises {@code this} to the power of the specified scalar element-by-element.
	 * <p>
	 * @param scalar a {@code double} value
	 * <p>
	 * @return {@code this .^= scalar}
	 */
	@Override
	public Scalar arrayRaise(final double scalar) {
		if (!readOnly) {
			value = Math.pow(value, scalar);
		} else {
			throw new IllegalOperationException(
					"Cannot change the value of a read-only " + getName());
		}
		return this;
	}

	/**
	 * Raises {@code this} to the power of the specified {@link Matrix} element-by-element.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this .^= matrix}
	 */
	@Override
	public Matrix arrayRaise(final Matrix matrix) {
		for (int i = 0; i < matrix.m; ++i) {
			for (int j = 0; j < matrix.n; ++j) {
				matrix.elements[i][j] = Math.pow(value, matrix.elements[i][j]);
			}
		}
		return matrix;
	}


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
	@Override
	public Scalar solve(final Entity entity) {
		if (entity instanceof Scalar) {
			final Scalar scalar = (Scalar) entity;
			if (!equals(0.)) {
				return new Scalar(scalar.value / value);
			}
			if (!scalar.equals(0.)) {
				throw new ArithmeticException(
						"Cannot find a solution if A is equal to zero and B is not equal to zero");
			}
			throw new ArithmeticException(
					"Cannot find a unique solution if A and B are equal to zero");
		}
		throw new IllegalOperationException("Cannot find a solution if A is a " + getName() +
				" and B is a " + entity.getName());
	}

	/**
	 * Returns the inverse of {@code this}.
	 * <p>
	 * @return {@code inv(this)}
	 */
	@Override
	public Scalar inverse() {
		return new Scalar(1. / value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Scalar clone() {
		return new Scalar(value);
	}

	@Override
	public boolean equals(final Object other) {
		return equals(other, Maths.DEFAULT_TOLERANCE);
	}

	@Override
	public boolean equals(final Object other, final double tolerance) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Scalar)) {
			return false;
		}
		return Doubles.equals(value, ((Scalar) other).value, tolerance);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, value);
	}

	@Override
	public String toString() {
		return toString(Formats.MIN_NUMBER_LENGTH);
	}

	public String toString(final int width) {
		final StringBuilder builder = Strings.createBuilder(Formats.DEFAULT_NUMBER_LENGTH);
		final String formattedValue = Formats.format(value);
		final int padding = Math.max(0, width - formattedValue.length());
		for (int k = 0; k < padding; ++k) {
			builder.append(' ');
		}
		builder.append(formattedValue);
		return builder.toString();
	}
}
