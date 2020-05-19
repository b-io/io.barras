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
package jupiter.math.linear.entity;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.LEFT_BRACKET;
import static jupiter.common.util.Characters.RIGHT_BRACKET;
import static jupiter.common.util.Characters.SPACE;
import static jupiter.common.Formats.MIN_NUMBER_LENGTH;
import static jupiter.common.Formats.NEWLINE;
import static jupiter.common.util.Strings.EMPTY;
import static jupiter.hardware.gpu.OpenCL.CL;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.io.InputOutput;
import jupiter.common.io.Resources;
import jupiter.common.io.file.FileHandler;
import jupiter.common.math.Interval;
import jupiter.common.math.Maths;
import jupiter.common.math.Statistics;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.table.DoubleTable;
import jupiter.common.struct.table.Table;
import jupiter.common.struct.tuple.Triple;
import jupiter.common.test.Arguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.thread.DivideAndConquer;
import jupiter.common.util.Arrays;
import jupiter.common.util.Doubles;
import jupiter.common.util.Longs;
import jupiter.common.util.Numbers;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.hardware.gpu.OpenCL;
import jupiter.math.analysis.function.bivariate.BivariateFunction;
import jupiter.math.analysis.function.univariate.UnivariateFunction;
import jupiter.math.linear.decomposition.CholeskyDecomposition;
import jupiter.math.linear.decomposition.EigenvalueDecomposition;
import jupiter.math.linear.decomposition.LUDecomposition;
import jupiter.math.linear.decomposition.Norms;
import jupiter.math.linear.decomposition.QRDecomposition;
import jupiter.math.linear.decomposition.SingularValueDecomposition;
import jupiter.math.linear.test.MatrixArguments;

/**
 * {@link Matrix} extends the Java Matrix Class (JAMA).
 * <p>
 * This extension provides especially the possibilities to multiply a {@link Matrix} with a
 * {@link Vector} or a {@link Scalar} and to parse a {@link Matrix} from an input {@link String}.
 * <p>
 * The Java Matrix Class provides the fundamental operations of numerical linear algebra. Various
 * constructors create a {@link Matrix} from 2D {@code double} arrays. Various "gets" and "sets"
 * provide access to elements and sub-{@link Matrix}. Several methods implement basic matrix
 * arithmetic, including matrix addition and multiplication, matrix norms and element-by-element
 * array operations. Methods for reading and printing matrices are also included. All the operations
 * in this version of the Java Matrix Class involve real matrices. Complex matrices may be handled
 * in a future version.
 * <p>
 * Five fundamental matrix decompositions, which consist of pairs or triples of matrices,
 * permutation vectors and the like, produce results in five decomposition classes. These
 * decompositions are accessed by the Java Matrix Class to compute solutions of simultaneous linear
 * equations, determinants, inverses and other matrix functions. The five decompositions are:
 * <ul>
 * <li>{@link CholeskyDecomposition} of symmetric, positive definite matrices.</li>
 * <li>{@link LUDecomposition} of rectangular matrices.</li>
 * <li>{@link QRDecomposition} of rectangular matrices.</li>
 * <li>{@link SingularValueDecomposition} of rectangular matrices.</li>
 * <li>{@link EigenvalueDecomposition} of both symmetric and non-symmetric square matrices.</li>
 * </ul>
 * <dl>
 * <dt><b>Example:</b></dt>
 * <dd>Solve a linear system A x = b and compute the residual norm, ||b - A x||.</dd>
 * </dl>
 * <p>
 * @author Florian Barras and JAMA (http://math.nist.gov/javanumerics/jama)
 * @version 1.0.3
 */
public class Matrix
		extends Entity {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The column {@code char} delimiters.
	 */
	public static final char[] COLUMN_DELIMITERS = new char[] {' ', '\t', ','};
	/**
	 * The row {@code char} delimiter.
	 */
	public static final char ROW_DELIMITER = ';';

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link Multiplication} used for computing the multiplication.
	 */
	protected static volatile Multiplication MULTIPLICATION = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The row dimension.
	 */
	protected final int m;
	/**
	 * The column dimension.
	 */
	protected final int n;
	/**
	 * The {@link Dimensions}.
	 */
	protected final Dimensions dimensions;
	/**
	 * The elements.
	 */
	protected double[] elements;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a square {@link Matrix} of zeros with the specified number of rows and columns.
	 * <p>
	 * @param size the number of rows and columns
	 */
	public Matrix(final int size) {
		this(size, size);
	}

	/**
	 * Constructs a {@link Matrix} of zeros with the specified {@link Dimensions}.
	 * <p>
	 * @param dimensions the {@link Dimensions}
	 */
	public Matrix(final Dimensions dimensions) {
		this(Arguments.requireNonNull(dimensions, "dimensions").m, dimensions.n);
	}

	/**
	 * Constructs a {@link Matrix} of zeros with the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public Matrix(final int rowCount, final int columnCount) {
		super();

		// Check the arguments
		IntegerArguments.requirePositive(rowCount);
		IntegerArguments.requirePositive(columnCount);

		// Set the numbers of rows and columns
		m = rowCount;
		n = columnCount;
		dimensions = new Dimensions(m, n);
		// Set the elements
		elements = new double[m * n];
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a constant {@link Matrix} with the specified numbers of rows and columns and
	 * constant.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 * @param constant    the {@code double} constant
	 */
	public Matrix(final int rowCount, final int columnCount, final double constant) {
		super();

		// Check the arguments
		IntegerArguments.requirePositive(rowCount);
		IntegerArguments.requirePositive(columnCount);

		// Set the numbers of rows and columns
		m = rowCount;
		n = columnCount;
		dimensions = new Dimensions(m, n);
		// Set the elements
		elements = new double[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				elements[i * n + j] = constant;
			}
		}
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Matrix} with the specified values.
	 * <p>
	 * @param rowCount  the number of rows
	 * @param values    the {@code double} values of the elements
	 * @param transpose the flag specifying whether to transpose
	 * <p>
	 * @throws IllegalArgumentException if the {@code values} length is not a multiple of
	 *                                  {@code rowCount}
	 */
	public Matrix(final int rowCount, final double[] values, final boolean transpose) {
		super();

		// Check the arguments
		IntegerArguments.requirePositive(rowCount);
		Arguments.requireNonNull(values, "values");

		// Set the numbers of rows and columns
		if (transpose) {
			n = rowCount;
			m = rowCount > 0 ? values.length / rowCount : 0;
		} else {
			m = rowCount;
			n = rowCount > 0 ? values.length / rowCount : 0;
		}
		dimensions = new Dimensions(m, n);
		// Check the length of the array
		if (m * n != values.length) {
			throw new IllegalArgumentException("The length of the specified array " +
					values.length + " is not a multiple of " + rowCount);
		}
		// Set the elements
		if (transpose) {
			elements = Doubles.transpose(rowCount, values);
		} else {
			elements = Doubles.take(values);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Matrix} with the specified values.
	 * <p>
	 * @param values the values of the elements in a 2D {@code double} array
	 * <p>
	 * @throws IllegalArgumentException if {@code values} has different row lengths
	 */
	public Matrix(final double[][] values) {
		this(Arguments.requireNonNull(values, "values").length, values[0].length, values);
	}

	/**
	 * Constructs a {@link Matrix} with the specified numbers of rows and columns with the specified
	 * values.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 * @param values      the values of the elements in a 2D {@code double} array
	 * <p>
	 * @throws IllegalArgumentException if the {@code values} rows have not the same length
	 */
	public Matrix(final int rowCount, final int columnCount, final double[][] values) {
		super();

		// Check the arguments
		IntegerArguments.requirePositive(rowCount);
		IntegerArguments.requirePositive(columnCount);
		Arguments.requireNonNull(values, "values");

		// Set the numbers of rows and columns
		m = rowCount;
		n = columnCount;
		dimensions = new Dimensions(m, n);
		// Check the row and column lengths of the 2D array
		if (values.length < m) {
			throw new IllegalArgumentException(
					"The row length of the specified 2D array is less than " + m);
		}
		elements = new double[m * n];
		for (int i = 0; i < m; ++i) {
			if (values[i].length < n) {
				throw new IllegalArgumentException(
						"The specified 2D array has a column length less than " + n);
			}
			setRow(i, values[i], 0, n);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Matrix} loaded from the file denoted by the specified path.
	 * <p>
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public Matrix(final String path, final boolean hasHeader)
			throws IOException {
		this(new DoubleTable(path, hasHeader));
	}

	/**
	 * Constructs a {@link Matrix} with the specified {@link DoubleTable} containing the elements.
	 * <p>
	 * @param table the {@link DoubleTable} containing the elements
	 */
	public Matrix(final DoubleTable table) {
		this(Arguments.requireNonNull(table, "table").getRowCount(), table.toPrimitiveArray());
	}

	/**
	 * Constructs a {@link Matrix} with the specified number of rows and elements.
	 * <p>
	 * @param rowCount the number of rows
	 * @param elements a {@code double} array
	 * <p>
	 * @throws IllegalArgumentException if the {@code elements} length is not a multiple of
	 *                                  {@code rowCount}
	 */
	public Matrix(final int rowCount, final double[] elements) {
		super();

		// Check the arguments
		IntegerArguments.requirePositive(rowCount);
		Arguments.requireNonNull(elements, "elements");

		// Set the numbers of rows and columns
		m = rowCount;
		n = rowCount > 0 ? elements.length / rowCount : 0;
		dimensions = new Dimensions(m, n);
		// Check the length of the array
		if (m * n != elements.length) {
			throw new IllegalArgumentException("The length of the specified array " +
					elements.length + " is not a multiple of " + rowCount);
		}
		// Set the elements
		this.elements = elements;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the name.
	 * <p>
	 * @return the name
	 */
	@Override
	public String getName() {
		return Objects.getName(this) + " of dimensions " + dimensions;
	}

	/**
	 * Returns the row dimension.
	 * <p>
	 * @return the row dimension
	 */
	public int getRowDimension() {
		return m;
	}

	/**
	 * Returns the column dimension.
	 * <p>
	 * @return the column dimension
	 */
	public int getColumnDimension() {
		return n;
	}

	/**
	 * Returns the {@link Dimensions}.
	 * <p>
	 * @return the {@link Dimensions}
	 */
	@Override
	public Dimensions getDimensions() {
		return dimensions;
	}

	/**
	 * Returns the {@code double} elements.
	 * <p>
	 * @return the {@code double} elements
	 */
	public double[] getElements() {
		return elements;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the element at the specified row and column indices.
	 * <p>
	 * @param i the row index
	 * @param j the column index
	 * <p>
	 * @return the element at the specified row and column indices
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out of bounds
	 */
	public double get(final int i, final int j) {
		return elements[i * n + j];
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@code double} values of the elements of the specified row.
	 * <p>
	 * @param i the row index
	 * <p>
	 * @return the {@code double} values of the elements of the specified row
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public double[] getRow(final int i) {
		return getRow(i, 0, n);
	}

	/**
	 * Returns the elements of the specified row truncated from the specified column index in a
	 * {@code double} array.
	 * <p>
	 * @param i          the row index
	 * @param fromColumn the initial column index (inclusive)
	 * <p>
	 * @return the elements of the specified row truncated from the specified column index in a
	 *         {@code double} array
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code fromColumn} is out of bounds
	 */
	public double[] getRow(final int i, final int fromColumn) {
		return getRow(i, fromColumn, n - fromColumn);
	}

	/**
	 * Returns the {@code double} values of the elements of the specified row truncated from the
	 * specified column index to the specified length.
	 * <p>
	 * @param i          the row index
	 * @param fromColumn the initial column index (inclusive)
	 * @param length     the number of row elements to get
	 * <p>
	 * @return the {@code double} values of the elements of the specified row truncated from the
	 *         specified column index to the specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code fromColumn} is out of bounds
	 */
	public double[] getRow(final int i, final int fromColumn, final int length) {
		final double[] row = new double[Math.min(length, n - fromColumn)];
		System.arraycopy(elements, i * n + fromColumn, row, 0, row.length);
		return row;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@code double} values of the elements of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the {@code double} values of the elements of the specified column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public double[] getColumn(final int j) {
		return getColumn(j, 0, m);
	}

	/**
	 * Returns the elements of the specified column truncated from the specified row index in a
	 * {@code double} array.
	 * <p>
	 * @param j       the column index
	 * @param fromRow the initial row index (inclusive)
	 * <p>
	 * @return the elements of the specified column truncated from the specified row index in a
	 *         {@code double} array
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code fromRow} is out of bounds
	 */
	public double[] getColumn(final int j, final int fromRow) {
		return getColumn(j, fromRow, m - fromRow);
	}

	/**
	 * Returns the {@code double} values of the elements of the specified column truncated from the
	 * specified row index to the specified length.
	 * <p>
	 * @param j       the column index
	 * @param fromRow the initial row index (inclusive)
	 * @param length  the number of column elements to get
	 * <p>
	 * @return the {@code double} values of the elements of the specified column truncated from the
	 *         specified row index to the specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code fromRow} is out of bounds
	 */
	public double[] getColumn(final int j, final int fromRow, final int length) {
		final double[] column = new double[Math.min(length, m - fromRow)];
		for (int i = 0; i < column.length; ++i) {
			column[i] = elements[(fromRow + i) * n + j];
		}
		return column;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the sub-{@link Matrix} {@code this(rowStart:rowEnd, columnStart:columnEnd)}.
	 * <p>
	 * @param rowStart    the initial row index (inclusive)
	 * @param rowEnd      the final row index (exclusive)
	 * @param columnStart the initial column index (inclusive)
	 * @param columnEnd   the final column index (exclusive)
	 * <p>
	 * @return the sub-{@link Matrix} {@code this(rowStart:rowEnd, columnStart:columnEnd)}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the {@code submatrix} indices are out of bounds
	 */
	public Matrix getSubmatrix(final int rowStart, final int rowEnd, final int columnStart,
			final int columnEnd) {
		final int rowCount = rowEnd - rowStart;
		final int columnCount = columnEnd - columnStart;
		// Create the submatrix
		final Matrix submatrix = new Matrix(rowCount, columnCount);
		try {
			for (int i = 0; i < rowCount; ++i) {
				submatrix.setRow(i, getRow(rowStart + i), columnStart);
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indices are out of bounds: " + ex);
		}
		return submatrix;
	}

	/**
	 * Returns the sub-{@link Matrix} {@code this(rowIndices(:), columnStart:columnEnd)}.
	 * <p>
	 * @param rowIndices  an array of row indices
	 * @param columnStart the initial column index (inclusive)
	 * @param columnEnd   the final column index (exclusive)
	 * <p>
	 * @return the sub-{@link Matrix} {@code this(rowIndices(:), columnStart:columnEnd)}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the {@code submatrix} indices are out of bounds
	 */
	public Matrix getSubmatrix(final int[] rowIndices, final int columnStart, final int columnEnd) {
		final int rowCount = rowIndices.length;
		final int columnCount = columnEnd - columnStart;
		// Create the submatrix
		final Matrix submatrix = new Matrix(rowCount, columnCount);
		try {
			for (int i = 0; i < rowCount; ++i) {
				submatrix.setRow(i, getRow(rowIndices[i]), columnStart);
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indices are out of bounds: " + ex);
		}
		return submatrix;
	}

	/**
	 * Returns the sub-{@link Matrix} {@code this(rowStart:rowEnd, columnIndices(:))}.
	 * <p>
	 * @param rowStart      the initial row index (inclusive)
	 * @param rowEnd        the final row index (exclusive)
	 * @param columnIndices an array of column indices
	 * <p>
	 * @return the sub-{@link Matrix} {@code this(rowStart:rowEnd, columnIndices(:))}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the {@code submatrix} indices are out of bounds
	 */
	public Matrix getSubmatrix(final int rowStart, final int rowEnd, final int[] columnIndices) {
		final int rowCount = rowEnd - rowStart;
		final int columnCount = columnIndices.length;
		// Create the submatrix
		final Matrix submatrix = new Matrix(rowCount, columnCount);
		try {
			for (int i = 0; i < rowCount; ++i) {
				for (int j = 0; j < columnCount; ++j) {
					submatrix.elements[i * columnCount + j] = elements[(rowStart + i) * n + columnIndices[j]];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indices are out of bounds: " + ex);
		}
		return submatrix;
	}

	/**
	 * Returns the sub-{@link Matrix} {@code this(rowIndices(:), columnIndices(:))}.
	 * <p>
	 * @param rowIndices    an array of row indices
	 * @param columnIndices an array of column indices
	 * <p>
	 * @return the sub-{@link Matrix} {@code this(rowIndices(:), columnIndices(:))}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the {@code submatrix} indices are out of bounds
	 */
	public Matrix getSubmatrix(final int[] rowIndices, final int[] columnIndices) {
		final int rowCount = rowIndices.length;
		final int columnCount = columnIndices.length;
		// Create the submatrix
		final Matrix submatrix = new Matrix(rowCount, columnCount);
		try {
			for (int i = 0; i < rowCount; ++i) {
				for (int j = 0; j < columnCount; ++j) {
					submatrix.elements[i * columnCount + j] = elements[rowIndices[i] * n +
							columnIndices[j]];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indices are out of bounds: " + ex);
		}
		return submatrix;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the element at the specified row and column indices.
	 * <p>
	 * @param i     the row index
	 * @param j     the column index
	 * @param value a {@code double} value
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out of bounds
	 */
	public void set(final int i, final int j, final double value) {
		elements[i * n + j] = value;
	}

	/**
	 * Sets the element at the specified row and column indices.
	 * <p>
	 * @param i     the row index
	 * @param j     the column index
	 * @param value a value {@link Object}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out of bounds
	 */
	public void set(final int i, final int j, final Object value) {
		elements[i * n + j] = Doubles.convert(value);
	}

	//////////////////////////////////////////////

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values a {@code double} array
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void setRow(final int i, final double... values) {
		setRow(i, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index.
	 * <p>
	 * @param i          the row index
	 * @param values     a {@code double} array
	 * @param fromColumn the initial column index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code fromColumn} is out of bounds
	 */
	public void setRow(final int i, final double[] values, final int fromColumn) {
		setRow(i, values, fromColumn, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index to the specified
	 * length.
	 * <p>
	 * @param i          the row index
	 * @param values     a {@code double} array
	 * @param fromColumn the initial column index (inclusive)
	 * @param length     the number of row elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code fromColumn} is out of bounds
	 */
	public void setRow(final int i, final double[] values, final int fromColumn, final int length) {
		System.arraycopy(values, 0, elements, i * n + fromColumn, Math.min(length, n - fromColumn));
	}

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values a {@link Collection} of {@link Double}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void setRow(final int i, final Collection<Double> values) {
		setRow(i, Doubles.collectionToPrimitiveArray(values));
	}

	//////////////////////////////////////////////

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values a {@code double} array
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public void setColumn(final int j, final double... values) {
		setColumn(j, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index.
	 * <p>
	 * @param j       the column index
	 * @param values  a {@code double} array
	 * @param fromRow the initial row index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code fromRow} is out of bounds
	 */
	public void setColumn(final int j, final double[] values, final int fromRow) {
		setColumn(j, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index to the specified
	 * length.
	 * <p>
	 * @param j       the column index
	 * @param values  a {@code double} array
	 * @param fromRow the initial row index (inclusive)
	 * @param length  the number of column elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code fromRow} is out of bounds
	 */
	public void setColumn(final int j, final double[] values, final int fromRow, final int length) {
		final int limit = Math.min(length, m - fromRow);
		for (int i = 0; i < limit; ++i) {
			elements[i * n + j] = values[fromRow + i];
		}
	}

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values a {@link Collection} of {@link Double}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public void setColumn(final int j, final Collection<Double> values) {
		setColumn(j, Doubles.collectionToPrimitiveArray(values));
	}

	//////////////////////////////////////////////

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values a {@code double} array
	 * <p>
	 * @throws IndexOutOfBoundsException if {@code values} is not of the same length as {@code this}
	 */
	public void setAll(final double... values) {
		System.arraycopy(values, 0, elements, 0, m * n);
	}

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values a 2D {@code double} array
	 * <p>
	 * @throws IndexOutOfBoundsException if {@code values} is not of the same length as {@code this}
	 */
	public void setAll(final double[]... values) {
		for (int i = 0; i < m; ++i) {
			setRow(i, values[i]);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Sets the sub-{@link Matrix} {@code this(rowStart:rowEnd, columnStart:columnEnd)}.
	 * <p>
	 * @param rowStart    the initial row index (inclusive)
	 * @param rowEnd      the final row index (exclusive)
	 * @param columnStart the initial column index (inclusive)
	 * @param columnEnd   the final column index (exclusive)
	 * @param submatrix   a {@link Matrix}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the {@code submatrix} indices are out of bounds
	 */
	public void setSubmatrix(final int rowStart, final int rowEnd, final int columnStart,
			final int columnEnd, final Matrix submatrix) {
		final int rowCount = rowEnd - rowStart;
		final int columnCount = columnEnd - columnStart;
		try {
			for (int i = 0; i < rowCount; ++i) {
				setRow(rowStart + i, submatrix.getRow(i), columnStart, columnCount);
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indices are out of bounds: " + ex);
		}
	}

	/**
	 * Sets the sub-{@link Matrix} {@code this(rowIndices(:), columnStart:columnEnd)}.
	 * <p>
	 * @param rowIndices  an array of row indices
	 * @param columnStart the initial column index (inclusive)
	 * @param columnEnd   the final column index (exclusive)
	 * @param submatrix   a {@link Matrix}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the {@code submatrix} indices are out of bounds
	 */
	public void setSubmatrix(final int[] rowIndices, final int columnStart, final int columnEnd,
			final Matrix submatrix) {
		final int rowCount = rowIndices.length;
		final int columnCount = columnEnd - columnStart;
		try {
			for (int i = 0; i < rowCount; ++i) {
				setRow(rowIndices[i], submatrix.getRow(i), columnStart, columnCount);
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indices are out of bounds: " + ex);
		}
	}

	/**
	 * Sets the sub-{@link Matrix} {@code this(rowStart:rowEnd, columnIndices(:))}.
	 * <p>
	 * @param rowStart      the initial row index (inclusive)
	 * @param rowEnd        the final row index (exclusive)
	 * @param columnIndices an array of column indices
	 * @param submatrix     a {@link Matrix}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the {@code submatrix} indices are out of bounds
	 */
	public void setSubmatrix(final int rowStart, final int rowEnd, final int[] columnIndices,
			final Matrix submatrix) {
		final int rowCount = rowEnd - rowStart;
		final int columnCount = columnIndices.length;
		try {
			for (int i = 0; i < rowCount; ++i) {
				for (int j = 0; j < columnCount; ++j) {
					elements[(rowStart + i) * n + columnIndices[j]] = submatrix.elements[i * columnCount + j];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indices are out of bounds: " + ex);
		}
	}

	/**
	 * Sets the sub-{@link Matrix} {@code this(rowIndices(:), columnIndices(:))}.
	 * <p>
	 * @param rowIndices    an array of row indices
	 * @param columnIndices an array of column indices
	 * @param submatrix     a {@link Matrix}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the {@code submatrix} indices are out of bounds
	 */
	public void setSubmatrix(final int[] rowIndices, final int[] columnIndices,
			final Matrix submatrix) {
		final int rowCount = rowIndices.length;
		final int columnCount = columnIndices.length;
		try {
			for (int i = 0; i < rowCount; ++i) {
				for (int j = 0; j < columnCount; ++j) {
					elements[rowIndices[i] * n + columnIndices[j]] = submatrix.elements[i * columnCount + j];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indices are out of bounds: " + ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONTROLLERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parallelizes {@code this}.
	 */
	public static synchronized void parallelize() {
		IO.debug(EMPTY);

		// Initialize
		if (MULTIPLICATION == null) {
			MULTIPLICATION = new Multiplication();
		} else {
			IO.debug("The work queue ", MULTIPLICATION, " has already started");
		}
	}

	/**
	 * Unparallelizes {@code this}.
	 */
	public static synchronized void unparallelize() {
		IO.debug(EMPTY);

		// Shutdown
		if (MULTIPLICATION != null) {
			MULTIPLICATION.shutdown();
			MULTIPLICATION = null;
		}
	}

	/**
	 * Reparallelizes {@code this}.
	 */
	public static synchronized void reparallelize() {
		IO.debug(EMPTY);

		unparallelize();
		parallelize();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts {@code this} to a {@code double} array.
	 * <p>
	 * @return a {@code double} array
	 */
	@Override
	public double[] toPrimitiveArray() {
		return Doubles.toPrimitiveArray(elements);
	}

	/**
	 * Converts {@code this} to a 2D {@code double} array.
	 * <p>
	 * @return a 2D {@code double} array
	 */
	public double[][] toPrimitiveArray2D() {
		return Doubles.toPrimitiveArray2D(elements, m);
	}

	/**
	 * Converts {@code this} to a {@link Scalar}.
	 * <p>
	 * @return a {@link Scalar}
	 * <p>
	 * @throws IllegalOperationException if {@code this} cannot be converted to a {@link Scalar}
	 */
	@Override
	public Scalar toScalar() {
		if (m == 1 && n == 1) {
			return new Scalar(elements[0]);
		}
		throw new IllegalOperationException(
				"Cannot convert a " + getName() + " to a " + Objects.getName(Scalar.class));
	}

	/**
	 * Converts {@code this} to a {@link Vector}.
	 * <p>
	 * @return a {@link Vector}
	 * <p>
	 * @throws IllegalOperationException if {@code this} cannot be converted to a {@link Vector}
	 */
	@Override
	public Vector toVector() {
		if (m == 1 || n == 1) {
			return new Vector(toPrimitiveArray(), n != 1);
		}
		throw new IllegalOperationException(
				"Cannot convert a " + getName() + " to a " + Objects.getName(Vector.class));
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

	/**
	 * Converts {@code this} to a {@link Table}.
	 * <p>
	 * @return a {@link Table}
	 */
	public DoubleTable toTable() {
		return new DoubleTable(elements);
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
	public Vector mean() {
		return mean(false);
	}

	/**
	 * Returns the mean of {@code this}.
	 * <p>
	 * @param transpose the flag specifying whether to transpose
	 * <p>
	 * @return {@code mean(this)}
	 */
	public Vector mean(final boolean transpose) {
		final double[] mean;
		if (transpose) {
			mean = new double[m];
			for (int i = 0; i < m; ++i) {
				mean[i] = Statistics.mean(elements[i]);
			}
		} else {
			mean = new double[n];
			for (int j = 0; j < n; ++j) {
				mean[j] = Statistics.mean(getColumn(j));
			}
		}
		return new Vector(mean);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the size of {@code this}.
	 * <p>
	 * @return {@code size(this)}
	 */
	@Override
	public Vector size() {
		return new Vector(new double[] {m, n});
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the identity of {@code size(this)}.
	 * <p>
	 * @return {@code eye(size(this))}
	 */
	@Override
	public Matrix identity() {
		return identity(m, n);
	}

	/**
	 * Returns the identity {@link Matrix} of the specified number of rows and columns.
	 * <p>
	 * @param size the number of rows and columns
	 * <p>
	 * @return {@code eye(size)}
	 */
	public static Matrix identity(final int size) {
		return identity(size, size);
	}

	/**
	 * Returns the identity {@link Matrix} of the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 * <p>
	 * @return {@code eye(m, n)}
	 */
	public static Matrix identity(final int rowCount, final int columnCount) {
		final Matrix identity = new Matrix(rowCount, columnCount);
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				identity.elements[i * columnCount + j] = i == j ? 1. : 0.;
			}
		}
		return identity;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the magic square {@link Matrix} of the specified size.
	 * <p>
	 * @param size the number of rows and columns
	 * <p>
	 * @return the magic square {@link Matrix} of the specified size
	 */
	public static Matrix magic(final int size) {
		final double[][] values = new double[size][size];
		if (size % 2 == 1) {
			// • Create the magic square of the size (of odd order)
			final int a = (size + 1) / 2;
			final int b = size + 1;
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j) {
					values[i][j] = size * ((i + j + a) % size) + (i + 2 * j + b) % size + 1;
				}
			}
		} else if (size % 4 == 0) {
			// • Create the magic square of the size (of doubly even order)
			for (int i = 0; i < size; ++i) {
				for (int j = 0; j < size; ++j) {
					if ((i + 1) / 2 % 2 == (j + 1) / 2 % 2) {
						values[i][j] = size * size - size * i - j;
					} else {
						values[i][j] = size * i + j + 1;
					}
				}
			}
		} else {
			// • Create the magic square of the size (of singly even order)
			final int p = size / 2;
			final int k = (size - 2) / 4;
			final Matrix A = magic(p);
			for (int i = 0; i < p; ++i) {
				for (int j = 0; j < p; ++j) {
					final double aij = A.get(i, j);
					values[i][j] = aij;
					values[i][j + p] = aij + 2 * p * p;
					values[i + p][j] = aij + 3 * p * p;
					values[i + p][j + p] = aij + p * p;
				}
			}
			for (int i = 0; i < p; ++i) {
				for (int j = 0; j < k; ++j) {
					final double value = values[i][j];
					values[i][j] = values[i + p][j];
					values[i + p][j] = value;
				}
				for (int j = size - k + 1; j < size; ++j) {
					final double value = values[i][j];
					values[i][j] = values[i + p][j];
					values[i + p][j] = value;
				}
			}
			double value = values[k][0];
			values[k][0] = values[k + p][0];
			values[k + p][0] = value;
			value = values[k][k];
			values[k][k] = values[k + p][k];
			values[k + p][k] = value;
		}
		return new Matrix(values);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the randomization of {@code size(this)}.
	 * <p>
	 * @return {@code rand(size(this))}
	 */
	@Override
	public Matrix random() {
		return random(m, n);
	}

	/**
	 * Returns a random {@link Matrix} of the specified number of rows and columns.
	 * <p>
	 * @param size the number of rows and columns
	 * <p>
	 * @return {@code rand(size)}
	 */
	public static Matrix random(final int size) {
		return random(size, size);
	}

	/**
	 * Returns a random {@link Matrix} of the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 * <p>
	 * @return {@code rand(m, n)}
	 */
	public static Matrix random(final int rowCount, final int columnCount) {
		final Matrix random = new Matrix(rowCount, columnCount);
		for (int i = 0; i < rowCount; ++i) {
			for (int j = 0; j < columnCount; ++j) {
				random.elements[i * columnCount + j] = Doubles.random();
			}
		}
		return random;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the sequence of {@code size(this)}.
	 * <p>
	 * @return {@code reshape(1:prod(size(this)), size(this))'}
	 */
	@Override
	public Matrix sequence() {
		return sequence(m, n);
	}

	/**
	 * Returns a sequence {@link Matrix} of the specified number of rows and columns.
	 * <p>
	 * @param size the number of rows and columns
	 * <p>
	 * @return {@code reshape(1:(size * size), size, size)'}
	 */
	public static Matrix sequence(final int size) {
		return sequence(size, size);
	}

	/**
	 * Returns a sequence {@link Matrix} of the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 * <p>
	 * @return {@code reshape(1:(m * n), m, n)'}
	 */
	public static Matrix sequence(final int rowCount, final int columnCount) {
		final Matrix sequence = new Matrix(rowCount, columnCount);
		sequence.elements = Doubles.createSequence(rowCount * columnCount, 1);
		return sequence;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the pivot rows at the specified row indices.
	 * <p>
	 * @param rowIndices an array of row indices
	 * <p>
	 * @return the pivot rows at the specified row indices
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code rowIndices} are out of bounds
	 */
	public Matrix pivot(final int... rowIndices) {
		return getSubmatrix(rowIndices, 0, n);
	}

	/**
	 * Returns the unpivot rows at the specified row indices.
	 * <p>
	 * @param rowIndices an array of row indices
	 * <p>
	 * @return the unpivot rows at the specified row indices
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code rowIndices} are out of bounds
	 */
	public Matrix unpivot(final int... rowIndices) {
		final int rowCount = rowIndices.length;
		final int[] unpivotIndices = new int[rowCount];
		for (int i = 0; i < rowCount; ++i) {
			unpivotIndices[rowIndices[i]] = i;
		}
		return getSubmatrix(unpivotIndices, 0, n);
	}

	/**
	 * Returns the one norm.
	 * <p>
	 * @return the maximum absolute column sum
	 */
	public double norm1() {
		double result = 0.;
		for (int j = 0; j < n; ++j) {
			double sum = 0.;
			for (int i = 0; i < m; ++i) {
				sum += Maths.abs(elements[i * n + j]);
			}
			result = Math.max(result, sum);
		}
		return result;
	}

	/**
	 * Returns the two norm.
	 * <p>
	 * @return the maximum singular value
	 */
	public double norm2() {
		return new SingularValueDecomposition(this).norm2();
	}

	/**
	 * Returns the infinity norm.
	 * <p>
	 * @return the maximum absolute row sum
	 */
	public double normInf() {
		double result = 0.;
		for (int i = 0; i < m; ++i) {
			double sum = 0.;
			for (int j = 0; j < n; ++j) {
				sum += Maths.abs(elements[i * n + j]);
			}
			result = Math.max(result, sum);
		}
		return result;
	}

	/**
	 * Returns the Frobenius norm, i.e. the square root of the sum of the squares of all the values
	 * of the elements.
	 * <p>
	 * @return the square root of the sum of the squares of all the values of the elements
	 */
	public double normF() {
		double result = 0.;
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result = Norms.euclideanNorm(result, elements[i * n + j]);
			}
		}
		return result;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS / EXPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link Matrix} loaded from the file denoted by the specified path.
	 * <p>
	 * @param path the path to the file to load
	 * <p>
	 * @return a {@link Matrix} loaded from the file denoted by the specified path
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public static Matrix load(final String path)
			throws IOException {
		return load(path, false);
	}

	/**
	 * Creates a {@link Matrix} loaded from the file denoted by the specified path.
	 * <p>
	 * @param path      the path to the file to load
	 * @param transpose the flag specifying whether to transpose
	 * <p>
	 * @return a {@link Matrix} loaded from the file denoted by the specified path
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public static Matrix load(final String path, final boolean transpose)
			throws IOException {
		final FileHandler fileHandler = new FileHandler(path);
		try {
			return load(fileHandler.createReader(), fileHandler.countLines(true), transpose);
		} finally {
			Resources.close(fileHandler);
		}
	}

	/**
	 * Creates a {@link Matrix} loaded from the specified reader.
	 * <p>
	 * @param reader    the {@link BufferedReader} of the lines to load
	 * @param lineCount the number of lines to load
	 * @param transpose the flag specifying whether to transpose
	 * <p>
	 * @return a {@link Matrix} loaded from the specified reader
	 * <p>
	 * @throws IOException if there is a problem with reading with {@code reader}
	 */
	public static Matrix load(final BufferedReader reader, final int lineCount,
			final boolean transpose)
			throws IOException {
		final int m = lineCount;
		int n = 0;
		// Parse the file
		String line;
		if ((line = reader.readLine()) != null) {
			// Find the delimiter (take the first one in the list in case of different delimiters)
			Character delimiter = null;
			for (final char d : COLUMN_DELIMITERS) {
				final int occurrenceCount = Strings.getIndices(line, d).size();
				if (occurrenceCount > 0) {
					if (n == 0) {
						delimiter = d;
						n = occurrenceCount;
					} else {
						IO.warn("The file contains different delimiters; ",
								Strings.quote(delimiter), " is selected");
						break;
					}
				}
			}
			if (delimiter == null) {
				delimiter = COLUMN_DELIMITERS[0];
			}
			++n;
			IO.debug("The file contains ", n, " columns separated by ", Strings.quote(delimiter));
			// Create the matrix
			final Matrix matrix;
			if (transpose) {
				matrix = new Matrix(n, m);
			} else {
				matrix = new Matrix(m, n);
			}
			// Scan the file line by line
			int i = 0;
			String[] values = Strings.split(line, delimiter).toArray();
			if (transpose) {
				matrix.setColumn(i++, Doubles.toPrimitiveArray(values));
			} else {
				matrix.setRow(i++, Doubles.toPrimitiveArray(values));
			}
			while ((line = reader.readLine()) != null) {
				values = Strings.split(line, delimiter).toArray();
				if (Arrays.isNullOrEmpty(values) || Strings.isNullOrEmpty(values[0])) {
					IO.warn("There is no element at line ", i, SPACE,
							Arguments.expectedButFound(0, n));
				} else if (values.length < n) {
					IO.error("There are not enough elements at line ", i, SPACE,
							Arguments.expectedButFound(values.length, n));
				} else {
					if (values.length > n) {
						IO.warn("There are too many elements at line ", i, SPACE,
								Arguments.expectedButFound(values.length, n));
					}
					if (transpose) {
						matrix.setColumn(i++, Doubles.toPrimitiveArray(values));
					} else {
						matrix.setRow(i++, Doubles.toPrimitiveArray(values));
					}
				}
			}
			return matrix;
		}
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Saves {@code this} to the file denoted by the specified path.
	 * <p>
	 * @param path the path to the file to save to
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening the file denoted
	 *                               by {@code path}
	 * @throws IOException           if there is a problem with writing to the file denoted by
	 *                               {@code path}
	 */
	public void save(final String path)
			throws FileNotFoundException, IOException {
		toTable().save(path, false);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// UNARY OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Applies the specified {@link UnivariateFunction} to {@code this}.
	 * <p>
	 * @param f the {@link UnivariateFunction} to apply
	 * <p>
	 * @return {@code f(this)}
	 */
	@Override
	public Matrix apply(final UnivariateFunction f) {
		return new Matrix(m, f.applyToPrimitiveArray(elements));
	}

	/**
	 * Applies the specified {@link BivariateFunction} to the columns of {@code this}.
	 * <p>
	 * @param f the {@link BivariateFunction} to apply column-wise
	 * <p>
	 * @return {@code f(this)}
	 */
	@Override
	public Entity applyByColumn(final BivariateFunction f) {
		if (n == 1) {
			double result = f.getInitialValue();
			for (int i = 0; i < m; ++i) {
				for (int j = 0; j < n; ++j) {
					result = f.apply(result, elements[i * n + j]);
				}
			}
			return new Scalar(result);
		}
		final Vector result = new Vector(n, true);
		for (int j = 0; j < n; ++j) {
			result.elements[j] = f.getInitialValue();
		}
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[j] = f.apply(result.elements[j], elements[i * n + j]);
			}
		}
		return result;
	}

	/**
	 * Applies the specified {@link BivariateFunction} to the rows of {@code this}.
	 * <p>
	 * @param f the {@link BivariateFunction} to apply row-wise
	 * <p>
	 * @return {@code f(this')}
	 */
	@Override
	public Entity applyByRow(final BivariateFunction f) {
		if (m == 1) {
			double result = f.getInitialValue();
			for (int i = 0; i < m; ++i) {
				for (int j = 0; j < n; ++j) {
					result = f.apply(result, elements[i * n + j]);
				}
			}
			return new Scalar(result);
		}
		final Vector result = new Vector(m);
		for (int i = 0; i < m; ++i) {
			result.elements[i] = f.getInitialValue();
		}
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i] = f.apply(result.elements[i], elements[i * n + j]);
			}
		}
		return result;
	}

	/**
	 * Returns the negation of {@code this}.
	 * <p>
	 * @return {@code -this}
	 */
	@Override
	public Matrix minus() {
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] = -elements[i * n + j];
			}
		}
		return result;
	}

	/**
	 * Returns the sum of the elements.
	 * <p>
	 * @return {@code sum(sum(this))}
	 */
	@Override
	public double sum() {
		double sum = 0.;
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				sum += elements[i * n + j];
			}
		}
		return sum;
	}

	/**
	 * Returns the transpose of {@code this}.
	 * <p>
	 * @return {@code this'}
	 */
	@Override
	public Matrix transpose() {
		return new Matrix(n, Doubles.transpose(m, elements));
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
	public Matrix plus(final double scalar) {
		final Matrix result = clone();
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] += scalar;
			}
		}
		return result;
	}

	/**
	 * Returns the addition of the specified {@link Matrix} to {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this + matrix}
	 */
	@Override
	public Matrix plus(final Matrix matrix) {
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(m, n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireDimensions(broadcastedMatrix);

		// Compute
		final Matrix result = clone();
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] += broadcastedMatrix.elements[i * broadcastedMatrix.n + j];
			}
		}
		return result;
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
	public Matrix add(final double scalar) {
		if (scalar != 0.) {
			for (int i = 0; i < m; ++i) {
				for (int j = 0; j < n; ++j) {
					elements[i * n + j] += scalar;
				}
			}
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
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(m, n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireDimensions(broadcastedMatrix);

		// Compute
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				elements[i * n + j] += broadcastedMatrix.elements[i * broadcastedMatrix.n + j];
			}
		}
		return this;
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code this} from the specified offsets.
	 * <p>
	 * @param B       the {@code double} array to multiply
	 * @param c       the constant {@code c} to multiply
	 * @param offset  the offset of {@code this}
	 * @param bOffset the offset of {@code B}
	 * <p>
	 * @return {@code this += c * B}
	 */
	public Matrix arrayAdd(final double[] B, final double c, final int offset, final int bOffset) {
		Maths.arrayAdd(elements, B, c, offset, bOffset, 0, n);
		return this;
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code this} from the specified offsets
	 * between the specified indices.
	 * <p>
	 * @param B         the {@code double} array to multiply
	 * @param c         the constant {@code c} to multiply
	 * @param offset    the offset of {@code this}
	 * @param bOffset   the offset of {@code B}
	 * @param fromIndex the index to start summing from (inclusive)
	 * @param toIndex   the index to finish summing at (exclusive)
	 * <p>
	 * @return {@code this += c * B}
	 */
	public Matrix arrayAdd(final double[] B, final double c, final int offset, final int bOffset,
			final int fromIndex, final int toIndex) {
		Maths.arrayAdd(elements, B, c, offset, bOffset, fromIndex, toIndex);
		return this;
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
	public Matrix minus(final double scalar) {
		final Matrix result = clone();
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] -= scalar;
			}
		}
		return result;
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
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(m, n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireDimensions(broadcastedMatrix);

		// Compute
		final Matrix result = clone();
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] -= broadcastedMatrix.elements[i * broadcastedMatrix.n + j];
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
	public Matrix subtract(final double scalar) {
		if (scalar != 0.) {
			for (int i = 0; i < m; ++i) {
				for (int j = 0; j < n; ++j) {
					elements[i * n + j] -= scalar;
				}
			}
		}
		return this;
	}

	/**
	 * Subtracts the specified {@link Matrix} from {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this -= matrix}
	 */
	@Override
	public Matrix subtract(final Matrix matrix) {
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(m, n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireDimensions(broadcastedMatrix);

		// Compute
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				elements[i * n + j] -= broadcastedMatrix.elements[i * broadcastedMatrix.n + j];
			}
		}
		return this;
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
	public Matrix times(final double scalar) {
		final Matrix result = clone();
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] *= scalar;
			}
		}
		return result;
	}

	/**
	 * Returns the multiplication of {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this * matrix}
	 * <p>
	 * @throws IllegalArgumentException if the inner dimensions of {@code this} and {@code matrix}
	 *                                  do not agree
	 */
	@Override
	public Entity times(final Matrix matrix) {
		// Initialize
		final int innerDimension = n;

		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(innerDimension);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireInnerDimension(broadcastedMatrix);

		// Test whether the result is a scalar or a matrix
		if (m == 1 && broadcastedMatrix.n == 1) {
			// • Scalar
			double sum = 0.;
			for (int k = 0; k < innerDimension; ++k) {
				sum += elements[k] * broadcastedMatrix.elements[k];
			}
			return new Scalar(sum);
		}
		// • Matrix
		final Matrix result = new Matrix(m, broadcastedMatrix.n);
		if (MULTIPLICATION != null) {
			MULTIPLICATION.divideAndConquer(
					new Triple<Matrix, Matrix, Matrix>(result, this, broadcastedMatrix));
		} else {
			Multiplication.process(result, this, broadcastedMatrix);
		}
		return result;
	}

	/**
	 * Returns the diagonal of the multiplication of {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code diag(this * matrix)}
	 * <p>
	 * @throws IllegalArgumentException if the inner dimensions of {@code this} and {@code matrix}
	 *                                  do not agree
	 */
	@Override
	public Entity diagonalTimes(final Matrix matrix) {
		// Initialize
		final int innerDimension = n;

		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(innerDimension);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireInnerDimension(broadcastedMatrix);

		// Test whether the result is a scalar or a matrix
		if (m == 1 && broadcastedMatrix.n == 1) {
			// • Scalar
			double sum = 0.;
			for (int k = 0; k < innerDimension; ++k) {
				sum += elements[k] * broadcastedMatrix.elements[k];
			}
			return new Scalar(sum);
		}
		// • Matrix
		final Matrix result = new Matrix(m, broadcastedMatrix.n);
		final int limit = Math.min(m, broadcastedMatrix.n);
		for (int i = 0; i < limit; ++i) {
			double sum = 0.;
			for (int k = 0; k < innerDimension; ++k) {
				sum += elements[i * n + k] *
						broadcastedMatrix.elements[k * broadcastedMatrix.n + i];
			}
			result.elements[i * result.n + i] = sum;
		}
		return result;
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
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(m, n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireDimensions(broadcastedMatrix);

		// Compute
		final Matrix result = clone();
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] *= broadcastedMatrix.elements[i * broadcastedMatrix.n + j];
			}
		}
		return result;
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
	public Matrix multiply(final double scalar) {
		if (scalar != 1.) {
			for (int i = 0; i < m; ++i) {
				for (int j = 0; j < n; ++j) {
					elements[i * n + j] *= scalar;
				}
			}
		}
		return this;
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
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(m, n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireDimensions(broadcastedMatrix);

		// Compute
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				elements[i * n + j] *= broadcastedMatrix.elements[i * broadcastedMatrix.n + j];
			}
		}
		return this;
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
	public Matrix division(final double scalar) {
		final Matrix result = clone();
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] /= scalar + Maths.TINY_TOLERANCE;
			}
		}
		return result;
	}

	/**
	 * Returns the division of {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this / matrix}
	 */
	@Override
	public Entity division(final Matrix matrix) {
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(n, true);
		} else {
			broadcastedMatrix = matrix;
		}

		// Compute
		return times(broadcastedMatrix.inverse());
	}

	/**
	 * Returns the element-by-element division of {@code this} by the specified {@link Matrix}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @return {@code this ./ matrix}
	 */
	@Override
	public Matrix arrayDivision(final Matrix matrix) {
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(m, n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireDimensions(broadcastedMatrix);

		// Compute
		final Matrix result = clone();
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] /= broadcastedMatrix.elements[i * broadcastedMatrix.n + j] +
						Maths.TINY_TOLERANCE;
			}
		}
		return result;
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
	public Matrix divide(final double scalar) {
		if (scalar != 1.) {
			for (int i = 0; i < m; ++i) {
				for (int j = 0; j < n; ++j) {
					elements[i * n + j] /= scalar + Maths.TINY_TOLERANCE;
				}
			}
		}
		return this;
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
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(m, n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireDimensions(broadcastedMatrix);

		// Compute
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				elements[i * n + j] /= broadcastedMatrix.elements[i * broadcastedMatrix.n + j] +
						Maths.TINY_TOLERANCE;
			}
		}
		return this;
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
	public Matrix arrayPower(final double scalar) {
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] = Maths.pow(elements[i * n + j], scalar);
			}
		}
		return result;
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
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(m, n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireDimensions(broadcastedMatrix);

		// Compute
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i * result.n + j] = Maths.pow(elements[i * n + j],
						broadcastedMatrix.elements[i * broadcastedMatrix.n + j]);
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
	public Matrix arrayRaise(final double scalar) {
		if (scalar != 1.) {
			for (int i = 0; i < m; ++i) {
				for (int j = 0; j < n; ++j) {
					elements[i * n + j] = Maths.pow(elements[i * n + j], scalar);
				}
			}
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
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(m, n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireDimensions(broadcastedMatrix);

		// Compute
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				elements[i * n + j] = Maths.pow(elements[i * n + j],
						broadcastedMatrix.elements[i * broadcastedMatrix.n + j]);
			}
		}
		return this;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the multiplication of {@code this} by {@code A} followed by the addition of
	 * {@code B}.
	 * <p>
	 * @param A the {@link Entity} to multiply
	 * @param B the {@link Entity} to add
	 * <p>
	 * @return {@code this * A + B}
	 */
	public Entity forward(final Entity A, final Entity B) {
		if (OpenCL.IS_ACTIVE && !(A instanceof Scalar) && !(B instanceof Scalar)) {
			final Matrix a = A.toMatrix();
			final Matrix b = B.toMatrix();
			if (a.n == b.m && CL.test(n, a.n, b.n)) {
				return new Matrix(m, CL.forward(elements, a.elements, b.elements, n, a.n, b.n));
			}
		}
		return times(A).plus(B);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parses the {@link Matrix} encoded in the specified expression {@link String}.
	 * <p>
	 * @param expression the expression {@link String} to parse
	 * <p>
	 * @return the {@link Matrix} encoded in the specified expression {@link String}, or
	 *         {@code null} if there is a problem with parsing
	 */
	public static Matrix parse(final String expression) {
		try {
			final char[] delimiters = new char[] {LEFT_BRACKET, RIGHT_BRACKET};
			final List<Integer> indices = Strings.getIndices(expression, delimiters);
			if (indices.size() == 2) {
				final int fromIndex = indices.get(0);
				final int toIndex = indices.get(1);
				if (fromIndex < toIndex && expression.charAt(fromIndex) == delimiters[0] &&
						expression.charAt(toIndex) == delimiters[1]) {
					// Get the rows
					final String content = expression.substring(fromIndex + 1, toIndex).trim();
					final ExtendedLinkedList<String> rows = Strings.removeEmpty(
							Strings.split(content, ROW_DELIMITER));
					// Count the numbers of rows and columns
					final int m = rows.size();
					final int n = Strings.removeEmpty(
							Strings.split(rows.getFirst().trim(), COLUMN_DELIMITERS)).size();
					// Fill the matrix row by row
					final double[] elements = new double[m * n];
					final Iterator<String> rowIterator = rows.iterator();
					for (int i = 0; i < m; ++i) {
						final Iterator<String> elementIterator = Strings.removeEmpty(
								Strings.split(rowIterator.next().trim(), COLUMN_DELIMITERS))
								.iterator();
						for (int j = 0; j < n; ++j) {
							elements[i * n + j] = Doubles.convert(elementIterator.next());
						}
					}
					return new Matrix(m, elements);
				}
			} else {
				final int indexCount = indices.size();
				if (indexCount > 2) {
					throw new ParseException("There are too many square brackets " +
							Arguments.expectedButFound(indexCount, 2), indices.get(2));
				}
				throw new ParseException("There are not enough square brackets " +
						Arguments.expectedButFound(indexCount, 2), 0);
			}
		} catch (final NumberFormatException ex) {
			IO.error(ex);
		} catch (final ParseException ex) {
			IO.error(ex);
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fills {@code this} with the specified constant.
	 * <p>
	 * @param constant the {@code double} constant to fill with
	 */
	@Override
	public void fill(final double constant) {
		Doubles.fill(elements, constant);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is square.
	 * <p>
	 * @return {@code true} if {@code this} is square, {@code false} otherwise
	 */
	public boolean isSquare() {
		return m == n;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} is parsable to a {@link Matrix}.
	 * <p>
	 * @param text a {@link String}
	 * <p>
	 * @return {@code true} if the specified {@link String} is parsable to a {@link Matrix},
	 *         {@code false} otherwise
	 */
	public static boolean isParsableFrom(final String text) {
		final char[] delimiters = new char[] {LEFT_BRACKET, RIGHT_BRACKET};
		final List<Integer> indices = Strings.getIndices(text.trim(), delimiters);
		if (indices.size() == 2) {
			final int fromIndex = indices.get(0);
			final int toIndex = indices.get(1);
			if (fromIndex < toIndex && text.charAt(fromIndex) == delimiters[0] &&
					text.charAt(toIndex) == delimiters[1]) {
				return true;
			}
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Requires the specified {@link Matrix} to have the same dimensions as {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @throws IllegalArgumentException if the dimensions of {@code this} and {@code matrix} do not
	 *                                  agree
	 */
	public void requireDimensions(final Matrix matrix) {
		MatrixArguments.requireDimensions(matrix, m, n);
	}

	/**
	 * Requires the specified {@link Matrix} to have the row dimension equals to the column
	 * dimension of {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @throws IllegalArgumentException if the inner dimensions of {@code this} and {@code matrix}
	 *                                  do not agree
	 */
	public void requireInnerDimension(final Matrix matrix) {
		MatrixArguments.requireInnerDimension(matrix, n);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// DECOMPOSITIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the LU decomposition.
	 * <p>
	 * @return the LU decomposition
	 *
	 * @see LUDecomposition
	 */
	public LUDecomposition lu() {
		return new LUDecomposition(this);
	}

	/**
	 * Returns the QR decomposition.
	 * <p>
	 * @return the QR decomposition
	 *
	 * @see QRDecomposition
	 */
	public QRDecomposition qr() {
		return new QRDecomposition(this);
	}

	/**
	 * Returns the Cholesky decomposition.
	 * <p>
	 * @return the Cholesky decomposition
	 *
	 * @see CholeskyDecomposition
	 */
	public CholeskyDecomposition chol() {
		return new CholeskyDecomposition(this);
	}

	/**
	 * Returns the singular value decomposition.
	 * <p>
	 * @return the singular value decomposition
	 *
	 * @see SingularValueDecomposition
	 */
	public SingularValueDecomposition svd() {
		return new SingularValueDecomposition(this);
	}

	/**
	 * Returns the eigenvalue decomposition.
	 * <p>
	 * @return the eigenvalue decomposition
	 *
	 * @see EigenvalueDecomposition
	 */
	public EigenvalueDecomposition eig() {
		return new EigenvalueDecomposition(this);
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
	 * <p>
	 * @throws IllegalOperationException if {@code this} cannot be solved with {@code entity}
	 */
	@Override
	public Matrix solve(final Entity entity) {
		if (entity instanceof Matrix) {
			return solve((Matrix) entity);
		}
		throw new IllegalOperationException("Cannot find a solution if A is a " + getName() +
				" and B is a " + entity.getName());
	}

	/**
	 * Returns the solution X of {@code this * X = B}.
	 * <p>
	 * @param B the right hand side of the equation
	 * <p>
	 * @return the solution if {@code this} is square, the least squares solution otherwise
	 */
	public Matrix solve(final Matrix B) {
		return isSquare() ? new LUDecomposition(this).solve(B) : new QRDecomposition(this).solve(B);
	}

	/**
	 * Returns the solution X of {@code X * this = B}, which is also {@code this' * X' = B'}.
	 * <p>
	 * @param B the right hand side of the equation
	 * <p>
	 * @return the solution if {@code this} is square, the least squares solution otherwise
	 */
	public Matrix solveTranspose(final Matrix B) {
		return transpose().solve(B.transpose());
	}

	/**
	 * Returns the condition (2 norm) of {@code this}, i.e. the ratio of the largest singular value
	 * to the smallest singular value.
	 * <p>
	 * @return {@code cond(this)}
	 */
	public double cond() {
		return new SingularValueDecomposition(this).cond();
	}

	/**
	 * Returns the determinant of {@code this}.
	 * <p>
	 * @return {@code det(this)}
	 */
	public double det() {
		return new LUDecomposition(this).det();
	}

	/**
	 * Returns the inverse of {@code this}.
	 * <p>
	 * @return {@code inv(this)}
	 */
	@Override
	public Matrix inverse() {
		return solve(identity(m));
	}

	/**
	 * Returns the effective numerical rank of {@code this}, obtained from SVD.
	 * <p>
	 * @return {@code rank(this)}
	 */
	public int rank() {
		return new SingularValueDecomposition(this).rank();
	}

	/**
	 * Returns the trace of {@code this}.
	 * <p>
	 * @return {@code trace(this)}
	 */
	public double trace() {
		double trace = 0.;
		final int limit = Math.min(m, n);
		for (int i = 0; i < limit; ++i) {
			trace += elements[i * n + i];
		}
		return trace;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public Matrix clone() {
		final Matrix clone = (Matrix) super.clone();
		clone.elements = Doubles.clone(elements);
		return clone;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		return equals(other, Maths.TOLERANCE);
	}

	/**
	 * Tests whether {@code this} is equal to {@code other} within {@code tolerance}.
	 * <p>
	 * @param other     the other {@link Object} to compare against for equality (may be
	 *                  {@code null})
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other} within {@code tolerance},
	 *         {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other, final double tolerance) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Matrix)) {
			return false;
		}
		final Matrix otherMatrix = (Matrix) other;
		if (otherMatrix.m != m || otherMatrix.n != n) {
			return false;
		}
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (!Doubles.equals(elements[i * n + j],
						otherMatrix.elements[i * otherMatrix.n + j], tolerance)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		int hashCode = Longs.hashCode(serialVersionUID);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				hashCode = Objects.hashCode(i, j * hashCode, elements[i * n + j]);
			}
		}
		return hashCode;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return toString(MIN_NUMBER_LENGTH, false);
	}

	/**
	 * Returns a representative {@link String} of {@code this} with the specified column width and
	 * flag specifying whether to use multiple lines.
	 * <p>
	 * @param columnWidth      the width of the representative {@link String} to create
	 * @param useMultipleLines the flag specifying whether to use multiple lines
	 * <p>
	 * @return a representative {@link String} of {@code this} with the specified column width and
	 *         flag specifying whether to use multiple lines
	 */
	public String toString(final int columnWidth, final boolean useMultipleLines) {
		final StringBuilder builder = Strings.createBuilder(m * (n * columnWidth + 1));
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				builder.append(SPACE);
				builder.append(Strings.leftPad(Numbers.toString(elements[i * n + j]),
						columnWidth - 1));
			}
			if (i < m - 1) {
				if (useMultipleLines) {
					builder.append(NEWLINE);
				} else {
					builder.append(ROW_DELIMITER);
				}
			}
		}
		if (useMultipleLines) {
			return builder.toString();
		}
		return Strings.bracketize(builder.toString());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Multiplication
			extends DivideAndConquer<Triple<Matrix, Matrix, Matrix>> {

		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs a {@link Multiplication}.
		 */
		protected Multiplication() {
			super();
		}

		/**
		 * Divides the multiplication of the specified input {@link Triple} into execution slices
		 * and conquers them. Returns the exit code for all of them.
		 * <p>
		 * @param input the input {@link Triple} containing the result {@link Matrix}, the left-hand
		 *              side operand {@link Matrix} and the right-hand side operand {@link Matrix}
		 *              to process
		 * <p>
		 * @return {@link InputOutput#EXIT_SUCCESS} if the multiplication succeeds,
		 *         {@link InputOutput#EXIT_FAILURE} otherwise for each execution slice
		 */
		protected int[] divideAndConquer(final Triple<Matrix, Matrix, Matrix> input) {
			return divideAndConquer(input, 0, input.getFirst().m);
		}

		/**
		 * Conquers the execution slice with the specified input {@link Triple} and
		 * {@link Interval}.
		 * <p>
		 * @param input    the input {@link Triple} containing the result {@link Matrix}, the
		 *                 left-hand side operand {@link Matrix} and the right-hand side operand
		 *                 {@link Matrix} to process
		 * @param interval the {@link Interval} of {@link Integer} of the execution slice to conquer
		 * <p>
		 * @return {@link InputOutput#EXIT_SUCCESS} if conquering the execution slice succeeds,
		 *         {@link InputOutput#EXIT_FAILURE} otherwise
		 */
		@Override
		protected int conquer(final Triple<Matrix, Matrix, Matrix> input,
				final Interval<Integer> interval) {
			return process(input.getFirst(), input.getSecond(), input.getThird(),
					interval.getLowerBound().getValue(), interval.getUpperBound().getValue());
		}

		/**
		 * Processes the multiplication with the specified result {@link Matrix}, left-hand side
		 * operand {@link Matrix} and right-hand side operand {@link Matrix}.
		 * <p>
		 * @param result the result {@link Matrix}
		 * @param left   the left-hand side operand {@link Matrix}
		 * @param right  the right-hand side operand {@link Matrix}
		 * <p>
		 * @return {@link InputOutput#EXIT_SUCCESS} if the multiplication succeeds,
		 *         {@link InputOutput#EXIT_FAILURE} otherwise
		 */
		protected static int process(final Matrix result, final Matrix left, final Matrix right) {
			return process(result, left, right, 0, left.m);
		}

		/**
		 * Processes the multiplication with the specified result {@link Matrix}, left-hand side
		 * operand {@link Matrix} and right-hand side operand {@link Matrix} between the specified
		 * indices.
		 * <p>
		 * @param result    the result {@link Matrix}
		 * @param left      the left-hand side operand {@link Matrix}
		 * @param right     the right-hand side operand {@link Matrix}
		 * @param fromIndex the index to start multiplying from (inclusive)
		 * @param toIndex   the index to finish multiplying at (exclusive)
		 * <p>
		 * @return {@link InputOutput#EXIT_SUCCESS} if the multiplication succeeds,
		 *         {@link InputOutput#EXIT_FAILURE} otherwise
		 */
		protected static int process(final Matrix result, final Matrix left, final Matrix right,
				final int fromIndex, final int toIndex) {
			final int innerDimension = left.n; // or right.m
			for (int i = fromIndex; i < toIndex; ++i) {
				for (int k = 0; k < innerDimension; ++k) {
					result.arrayAdd(right.elements, left.elements[i * left.n + k],
							i * result.n, k * right.n);
				}
			}
			return InputOutput.EXIT_SUCCESS;
		}

		/**
		 * Clones {@code this}.
		 * <p>
		 * @return a clone of {@code this}
		 *
		 * @see ICloneable
		 */
		@Override
		public Multiplication clone() {
			return (Multiplication) super.clone();
		}
	}
}
