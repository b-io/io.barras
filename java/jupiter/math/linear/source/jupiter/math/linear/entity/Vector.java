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
import jupiter.common.util.Doubles;
import jupiter.math.analysis.function.Function;

public class Vector
		extends Matrix {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -3318967766451663359L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether {@code this} is transposed.
	 */
	protected volatile boolean isTransposed;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a zero {@link Vector} of the specified number of elements.
	 * <p>
	 * @param dimension the number of elements
	 */
	public Vector(final int dimension) {
		this(dimension, false);
	}

	/**
	 * Constructs a zero {@link Vector} of the specified number of elements.
	 * <p>
	 * @param dimension the number of elements
	 * @param transpose the flag specifying whether to transpose
	 */
	public Vector(final int dimension, final boolean transpose) {
		super(transpose ? 1 : dimension, transpose ? dimension : 1);
		isTransposed = transpose;
	}

	/**
	 * Constructs a constant {@link Vector} of the specified number of elements and value.
	 * <p>
	 * @param dimension the number of elements
	 * @param value     a {@code double} value
	 */
	public Vector(final int dimension, final double value) {
		this(dimension, value, false);
	}

	/**
	 * Constructs a constant {@link Vector} of the specified number of elements and value.
	 * <p>
	 * @param dimension the number of elements
	 * @param value     a {@code double} value
	 * @param transpose the flag specifying whether to transpose
	 */
	public Vector(final int dimension, final double value, final boolean transpose) {
		super(transpose ? 1 : dimension, transpose ? dimension : 1, value);
		isTransposed = transpose;
	}

	/**
	 * Constructs a {@link Vector} of the specified values.
	 * <p>
	 * @param values an array of {@code double} values
	 */
	public Vector(final double... values) {
		this(values, false);
	}

	/**
	 * Constructs a {@link Vector} of the specified values.
	 * <p>
	 * @param values    an array of {@code double} values
	 * @param transpose the flag specifying whether to transpose
	 */
	public Vector(final double[] values, final boolean transpose) {
		super(values.length, values, transpose);
		isTransposed = transpose;
	}

	/**
	 * Constructs a {@link Vector} of the specified elements.
	 * <p>
	 * @param elements a 2D array of {@code double} values
	 * <p>
	 * @throws IllegalArgumentException if {@code elements} is not one-dimensional
	 */
	public Vector(final double[]... elements) {
		super(elements);
		if (elements.length == 1) {
			isTransposed = true;
		} else if (elements[0].length == 1) {
			isTransposed = false;
		} else {
			throw new IllegalArgumentException("The specified 2D array of dimensions " +
					new Dimensions(elements.length, elements[0].length) +
					" is not one-dimensional");
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the dimension.
	 * <p>
	 * @return the dimension
	 */
	public int getDimension() {
		return m == 1 ? n : m;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element at the specified row index.
	 * <p>
	 * @param i the row index
	 * <p>
	 * @return the element at the specified row index
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public double get(final int i) {
		return get(i, 0);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the element at the specified row index.
	 * <p>
	 * @param i     the row index
	 * @param value a {@code double} value
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void set(final int i, final double value) {
		elements[i] = value;
	}

	/**
	 * Sets the element at the specified row index.
	 * <p>
	 * @param i     the row index
	 * @param value an {@link Object}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void set(final int i, final Object value) {
		elements[i] = Doubles.convert(value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts {@code this} to a {@link Vector}.
	 * <p>
	 * @return a {@link Vector}
	 */
	@Override
	public Vector toVector() {
		return this;
	}

	/**
	 * Converts {@code this} to a {@link Matrix}.
	 * <p>
	 * @return a {@link Matrix}
	 */
	@Override
	public Matrix toMatrix() {
		return this;
	}

	public Matrix toMatrix(final int m) {
		return toMatrix(m, false);
	}

	public Matrix toMatrix(final int dimension, final boolean transpose) {
		if (transpose) {
			if (dimension == n) {
				return this;
			}
			return toMatrix(m, dimension);
		}
		if (dimension == m) {
			return this;
		}
		return toMatrix(dimension, n);
	}

	public Matrix toMatrix(final int rowCount, final int columnCount) {
		if (m == rowCount && n == columnCount) {
			return this;
		}
		if (isTransposed) {
			if (n == columnCount) {
				final Matrix result = new Matrix(rowCount, columnCount);
				for (int i = 0; i < rowCount; ++i) {
					result.setRow(i, elements);
				}
				return result;
			}
			throw new IllegalOperationException("Cannot broadcast " + getName() + " to " +
					getDimensions() + " (wrong number of columns)");
		}
		if (m == rowCount) {
			final Matrix result = new Matrix(rowCount, columnCount);
			for (int j = 0; j < columnCount; ++j) {
				result.setColumn(j, elements);
			}
			return result;
		}
		throw new IllegalOperationException("Cannot broadcast " + getName() + " to " +
				getDimensions() + " (wrong number of rows)");
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a random {@link Matrix} of the specified number of elements and columns.
	 * <p>
	 * @param m the number of elements
	 * <p>
	 * @return {@code rand(size)}
	 */
	public static Vector random(final int m) {
		final Vector random = new Vector(m);
		for (int i = 0; i < m; ++i) {
			random.elements[i] = Doubles.random();
		}
		return random;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fills {@code this} with the specified value.
	 * <p>
	 * @param value the {@code double} value to fill with
	 */
	@Override
	public void fill(final double value) {
		Doubles.fill(elements, value);
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
	public Vector apply(final Function f) {
		return new Vector(f.applyToPrimitiveArray(elements));
	}

	/**
	 * Returns the negation of {@code this}.
	 * <p>
	 * @return {@code -this}
	 */
	@Override
	public Vector minus() {
		final Vector result = new Vector(getDimension());
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] = -elements[i * n + j];
			}
		}
		return result;
	}

	/**
	 * Returns the transpose of {@code this}.
	 * <p>
	 * @return {@code this'}
	 */
	@Override
	public Vector transpose() {
		return new Vector(Doubles.transpose(m, elements), !isTransposed);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see Cloneable
	 */
	@Override
	public Vector clone() {
		return new Vector(elements);
	}
}
