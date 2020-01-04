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

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.LEFT_BRACKET;
import static jupiter.common.util.Characters.RIGHT_BRACKET;
import static jupiter.common.util.Formats.MIN_NUMBER_LENGTH;
import static jupiter.common.util.Formats.NEWLINE;
import static jupiter.common.util.Formats.NUMBER_LENGTH;
import static jupiter.common.util.Formats.formatNumber;
import static jupiter.common.util.Strings.EMPTY;
import static jupiter.common.util.Strings.SPACE;
import static jupiter.hardware.gpu.OpenCL.CL;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.io.file.FileHandler;
import jupiter.common.math.Interval;
import jupiter.common.math.Maths;
import jupiter.common.math.Statistics;
import jupiter.common.struct.table.DoubleTable;
import jupiter.common.struct.table.Table;
import jupiter.common.struct.tuple.Triple;
import jupiter.common.test.Arguments;
import jupiter.common.thread.DivideAndConquer;
import jupiter.common.thread.WorkQueue;
import jupiter.common.util.Doubles;
import jupiter.common.util.Longs;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.hardware.gpu.OpenCL;
import jupiter.math.analysis.function.Function;
import jupiter.math.analysis.function.ReducerFunction;
import jupiter.math.linear.decomposition.CholeskyDecomposition;
import jupiter.math.linear.decomposition.EigenvalueDecomposition;
import jupiter.math.linear.decomposition.LUDecomposition;
import jupiter.math.linear.decomposition.Norms;
import jupiter.math.linear.decomposition.QRDecomposition;
import jupiter.math.linear.decomposition.SingularValueDecomposition;
import jupiter.math.linear.test.MatrixArguments;

/**
 * Extension of the Java Matrix class (JAMA).
 * <p>
 * This extension provides the possibilities to multiply Matrices with Scalars and to parse Matrices
 * from an input String.
 * <p>
 * The Java Matrix Class provides the fundamental operations of numerical linear algebra. Various
 * constructors create Matrices from two dimensional arrays of double precision floating-point
 * numbers. Various "gets" and "sets" provide access to submatrices and matrix elements. Several
 * methods implement basic matrix arithmetic, including matrix addition and multiplication, matrix
 * norms and element-by-element array operations. Methods for reading and printing matrices are also
 * included. All the operations in this version of the Matrix Class involve real matrices. Complex
 * matrices may be handled in a future version.
 * <p>
 * Five fundamental matrix decompositions, which consist of pairs or triples of matrices,
 * permutation vectors, and the like, produce results in five decomposition classes. These
 * decompositions are accessed by the Matrix class to compute solutions of simultaneous linear
 * equations, determinants, inverses and other matrix functions. The five decompositions are:
 * <UL>
 * <LI>Cholesky Decomposition of symmetric, positive definite matrices.
 * <LI>LU Decomposition of rectangular matrices.
 * <LI>QR Decomposition of rectangular matrices.
 * <LI>Singular Value Decomposition of rectangular matrices.
 * <LI>Eigenvalue Decomposition of both symmetric and non-symmetric square matrices.
 * </UL>
 * <DL>
 * <DT><B>Example of use:</B></DT>
 * <DD>Solve a linear system A x = b and compute the residual norm, ||b - A x||.
 * <p>
 * </DD>
 * </DL>
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

	/**
	 * The column {@code char} delimiters.
	 */
	public static final char[] COLUMN_DELIMITERS = new char[] {' ', '\t', ','};
	/**
	 * The row {@code char} delimiter.
	 */
	public static final char ROW_DELIMITER = ';';

	/**
	 * The flag specifying whether to parallelize using a {@link WorkQueue}.
	 */
	public static volatile boolean PARALLELIZE = false;
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
	protected Dimensions size;
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
		this(dimensions.m, dimensions.n);
	}

	/**
	 * Constructs a {@link Matrix} of zeros with the specified numbers of rows and columns.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public Matrix(final int rowCount, final int columnCount) {
		// Set the numbers of rows and columns
		m = rowCount;
		n = columnCount;
		size = new Dimensions(m, n);
		// Set the elements
		elements = new double[m * n];
	}

	/**
	 * Constructs a constant {@link Matrix} with the specified numbers of rows and columns with the
	 * specified {@code double} value.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 * @param value       the {@code double} value of the elements
	 */
	public Matrix(final int rowCount, final int columnCount, final double value) {
		// Set the numbers of rows and columns
		m = rowCount;
		n = columnCount;
		size = new Dimensions(m, n);
		// Set the elements
		elements = new double[m * n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				elements[i * n + j] = value;
			}
		}
	}

	/**
	 * Constructs a {@link Matrix} with the specified elements.
	 * <p>
	 * @param rowCount the number of rows of the array
	 * @param elements a {@code double} array
	 * <p>
	 * @throws IllegalArgumentException if the length of {@code elements} is not a multiple of
	 *                                  {@code rowCount}
	 */
	public Matrix(final int rowCount, final double[] elements) {
		// Set the numbers of rows and columns
		m = rowCount;
		n = rowCount > 0 ? elements.length / rowCount : 0;
		size = new Dimensions(m, n);
		// Check the length of the specified array
		if (m * n != elements.length) {
			throw new IllegalArgumentException("The length of the specified array " +
					elements.length + " is not a multiple of " + rowCount);
		}
		// Set the elements
		this.elements = elements;
	}

	/**
	 * Constructs a {@link Matrix} with the specified {@code double} values.
	 * <p>
	 * @param rowCount  the number of rows of the array
	 * @param values    a {@code double} array
	 * @param transpose the flag specifying whether to transpose
	 * <p>
	 * @throws IllegalArgumentException if the length of {@code values} is not a multiple of
	 *                                  {@code rowCount}
	 */
	public Matrix(final int rowCount, final double[] values, final boolean transpose) {
		// Set the numbers of rows and columns
		if (transpose) {
			n = rowCount;
			m = rowCount > 0 ? values.length / rowCount : 0;
		} else {
			m = rowCount;
			n = rowCount > 0 ? values.length / rowCount : 0;
		}
		size = new Dimensions(m, n);
		// Check the length of the specified array
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

	/**
	 * Constructs a {@link Matrix} with the specified {@code double} values.
	 * <p>
	 * @param values a 2D {@code double} array
	 * <p>
	 * @throws IllegalArgumentException if {@code values} has different row lengths
	 */
	public Matrix(final double[][] values) {
		this(values.length, values[0].length, values);
	}

	/**
	 * Constructs a {@link Matrix} with the specified numbers of rows and columns with the specified
	 * values.
	 * <p>
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 * @param values      a 2D {@code double} array
	 * <p>
	 * @throws IllegalArgumentException if the rows of {@code values} have not the same length
	 */
	public Matrix(final int rowCount, final int columnCount, final double[][] values) {
		// Set the numbers of rows and columns
		m = rowCount;
		n = columnCount;
		size = new Dimensions(m, n);
		// Check the row and column lengths of the specified array
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

	/**
	 * Constructs a {@link Matrix} with the specified table.
	 * <p>
	 * @param table a {@link DoubleTable}
	 */
	public Matrix(final DoubleTable table) {
		this(table.getRowCount(), table.toPrimitiveArray());
	}

	/**
	 * Constructs a {@link Matrix} loaded from the specified file.
	 * <p>
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the specified file
	 */
	public Matrix(final String path, final boolean hasHeader)
			throws IOException {
		this(new DoubleTable(path, hasHeader));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the name.
	 * <p>
	 * @return the name
	 */
	@Override
	public String getName() {
		return getClass().getSimpleName() + " of dimensions " + size;
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
	 * Returns the dimensions.
	 * <p>
	 * @return the dimensions
	 */
	@Override
	public Dimensions getDimensions() {
		return size;
	}

	/**
	 * Returns the elements.
	 * <p>
	 * @return the elements
	 */
	public double[] getElements() {
		return elements;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element at the specified row and column indexes.
	 * <p>
	 * @param i the row index
	 * @param j the column index
	 * <p>
	 * @return the element at the specified row and column indexes
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out of bounds
	 */
	public double get(final int i, final int j) {
		return elements[i * n + j];
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the elements of the specified row.
	 * <p>
	 * @param i the row index
	 * <p>
	 * @return the elements of the specified row
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public double[] getRow(final int i) {
		return getRow(i, 0, n);
	}

	/**
	 * Returns the elements of the specified row truncated from the specified column index
	 * (inclusive).
	 * <p>
	 * @param i    the row index
	 * @param from the initial column index (inclusive)
	 * <p>
	 * @return the elements of the specified row truncated from the specified column index
	 *         (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code from} is out of bounds
	 */
	public double[] getRow(final int i, final int from) {
		return getRow(i, from, n - from);
	}

	/**
	 * Returns the elements of the specified row truncated from the specified column index
	 * (inclusive) to the specified length.
	 * <p>
	 * @param i      the row index
	 * @param from   the initial column index (inclusive)
	 * @param length the number of row elements to get
	 * <p>
	 * @return the elements of the specified row truncated from the specified column index
	 *         (inclusive) to the specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code from} is out of bounds
	 */
	public double[] getRow(final int i, final int from, final int length) {
		final double[] row = new double[Math.min(length, n - from)];
		System.arraycopy(elements, i * n + from, row, 0, row.length);
		return row;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the elements of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the elements of the specified column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public double[] getColumn(final int j) {
		return getColumn(j, 0, m);
	}

	/**
	 * Returns the elements of the specified column truncated from the specified row index
	 * (inclusive).
	 * <p>
	 * @param j    the column index
	 * @param from the initial row index (inclusive)
	 * <p>
	 * @return the elements of the specified column truncated from the specified row index
	 *         (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code from} is out of bounds
	 */
	public double[] getColumn(final int j, final int from) {
		return getColumn(j, from, m - from);
	}

	/**
	 * Returns the elements of the specified column truncated from the specified row index
	 * (inclusive) to the specified length.
	 * <p>
	 * @param j      the column index
	 * @param from   the initial row index (inclusive)
	 * @param length the number of column elements to get
	 * <p>
	 * @return the elements of the specified column truncated from the specified row index
	 *         (inclusive) to the specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code from} is out of bounds
	 */
	public double[] getColumn(final int j, final int from, final int length) {
		final double[] column = new double[Math.min(length, m - from)];
		for (int i = 0; i < column.length; ++i) {
			column[i] = elements[(from + i) * n + j];
		}
		return column;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the submatrix {@code this(rowStart:rowEnd, columnStart:columnEnd)}.
	 * <p>
	 * @param rowStart    the initial row index (inclusive)
	 * @param rowEnd      the final row index (exclusive)
	 * @param columnStart the initial column index (inclusive)
	 * @param columnEnd   the final column index (exclusive)
	 * <p>
	 * @return the submatrix {@code this(rowStart:rowEnd, columnStart:columnEnd)}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified submatrix indexes are out of bounds
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
					"The specified submatrix indexes are out of bounds: " + ex);
		}
		return submatrix;
	}

	/**
	 * Returns the submatrix {@code this(rowIndexes(:), columnStart:columnEnd)}.
	 * <p>
	 * @param rowIndexes  an array of row indexes
	 * @param columnStart the initial column index (inclusive)
	 * @param columnEnd   the final column index (exclusive)
	 * <p>
	 * @return the submatrix {@code this(rowIndexes(:), columnStart:columnEnd)}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified submatrix indexes are out of bounds
	 */
	public Matrix getSubmatrix(final int[] rowIndexes, final int columnStart, final int columnEnd) {
		final int rowCount = rowIndexes.length;
		final int columnCount = columnEnd - columnStart;
		// Create the submatrix
		final Matrix submatrix = new Matrix(rowCount, columnCount);
		try {
			for (int i = 0; i < rowCount; ++i) {
				submatrix.setRow(i, getRow(rowIndexes[i]), columnStart);
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds: " + ex);
		}
		return submatrix;
	}

	/**
	 * Returns the submatrix {@code this(rowStart:rowEnd, columnIndexes(:))}.
	 * <p>
	 * @param rowStart      the initial row index (inclusive)
	 * @param rowEnd        the final row index (exclusive)
	 * @param columnIndexes an array of column indexes
	 * <p>
	 * @return the submatrix {@code this(rowStart:rowEnd, columnIndexes(:))}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified submatrix indexes are out of bounds
	 */
	public Matrix getSubmatrix(final int rowStart, final int rowEnd, final int[] columnIndexes) {
		final int rowCount = rowEnd - rowStart;
		final int columnCount = columnIndexes.length;
		// Create the submatrix
		final Matrix submatrix = new Matrix(rowCount, columnCount);
		try {
			for (int i = 0; i < rowCount; ++i) {
				for (int j = 0; j < columnCount; ++j) {
					submatrix.elements[i * columnCount + j] = elements[(rowStart + i) * n +
							columnIndexes[j]];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds: " + ex);
		}
		return submatrix;
	}

	/**
	 * Returns the submatrix {@code this(rowIndexes(:), columnIndexes(:))}.
	 * <p>
	 * @param rowIndexes    an array of row indexes
	 * @param columnIndexes an array of column indexes
	 * <p>
	 * @return the submatrix {@code this(rowIndexes(:), columnIndexes(:))}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified submatrix indexes are out of bounds
	 */
	public Matrix getSubmatrix(final int[] rowIndexes, final int[] columnIndexes) {
		final int rowCount = rowIndexes.length;
		final int columnCount = columnIndexes.length;
		// Create the submatrix
		final Matrix submatrix = new Matrix(rowCount, columnCount);
		try {
			for (int i = 0; i < rowCount; ++i) {
				for (int j = 0; j < columnCount; ++j) {
					submatrix.elements[i * columnCount + j] = elements[rowIndexes[i] * n +
							columnIndexes[j]];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds: " + ex);
		}
		return submatrix;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the element at the specified row and column indexes to the specified {@code double}
	 * value.
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
	 * Sets the element at the specified row and column indexes to the specified {@link Object}.
	 * <p>
	 * @param i     the row index
	 * @param j     the column index
	 * @param value an {@link Object}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out of bounds
	 */
	public void set(final int i, final int j, final Object value) {
		elements[i * n + j] = Doubles.convert(value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values a {@code double} array
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void setRow(final int i, final double[] values) {
		setRow(i, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index (inclusive).
	 * <p>
	 * @param i      the row index
	 * @param values a {@code double} array
	 * @param from   the initial column index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code from} is out of bounds
	 */
	public void setRow(final int i, final double[] values, final int from) {
		setRow(i, values, from, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index (inclusive) to the
	 * specified length.
	 * <p>
	 * @param i      the row index
	 * @param values a {@code double} array
	 * @param from   the initial column index (inclusive)
	 * @param length the number of row elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code from} is out of bounds
	 */
	public void setRow(final int i, final double[] values, final int from, final int length) {
		System.arraycopy(values, 0, elements, i * n + from, Math.min(length, n - from));
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values a {@code double} array
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public void setColumn(final int j, final double[] values) {
		setColumn(j, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index (inclusive).
	 * <p>
	 * @param j      the column index
	 * @param values a {@code double} array
	 * @param from   the initial row index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code from} is out of bounds
	 */
	public void setColumn(final int j, final double[] values, final int from) {
		setColumn(j, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index (inclusive) to the
	 * specified length.
	 * <p>
	 * @param j      the column index
	 * @param values a {@code double} array
	 * @param from   the initial row index (inclusive)
	 * @param length the number of column elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code from} is out of bounds
	 */
	public void setColumn(final int j, final double[] values, final int from, final int length) {
		final int limit = Math.min(length, m - from);
		for (int i = 0; i < limit; ++i) {
			elements[i * n + j] = values[from + i];
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values a {@code double} array
	 * <p>
	 * @throws IndexOutOfBoundsException if the specified array is not of the same length
	 */
	public void setAll(final double... values) {
		System.arraycopy(values, 0, elements, 0, m * n);
	}

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values a 2D {@code double} array
	 * <p>
	 * @throws IndexOutOfBoundsException if the specified array is not of the same length
	 */
	public void setAll(final double[]... values) {
		for (int i = 0; i < m; ++i) {
			setRow(i, values[i]);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets {@code this(rowStart:rowEnd, columnStart:columnEnd)} to the specified submatrix.
	 * <p>
	 * @param rowStart    the initial row index (inclusive)
	 * @param rowEnd      the final row index (exclusive)
	 * @param columnStart the initial column index (inclusive)
	 * @param columnEnd   the final column index (exclusive)
	 * @param submatrix   a {@link Matrix}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified submatrix indexes are out of bounds
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
					"The specified submatrix indexes are out of bounds: " + ex);
		}
	}

	/**
	 * Sets {@code this(rowIndexes(:), columnStart:columnEnd)} to the specified submatrix.
	 * <p>
	 * @param rowIndexes  an array of row indexes
	 * @param columnStart the initial column index (inclusive)
	 * @param columnEnd   the final column index (exclusive)
	 * @param submatrix   a {@link Matrix}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified submatrix indexes are out of bounds
	 */
	public void setSubmatrix(final int[] rowIndexes, final int columnStart, final int columnEnd,
			final Matrix submatrix) {
		final int rowCount = rowIndexes.length;
		final int columnCount = columnEnd - columnStart;
		try {
			for (int i = 0; i < rowCount; ++i) {
				setRow(rowIndexes[i], submatrix.getRow(i), columnStart, columnCount);
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds: " + ex);
		}
	}

	/**
	 * Sets {@code this(rowStart:rowEnd, columnIndexes(:))} to the specified submatrix.
	 * <p>
	 * @param rowStart      the initial row index (inclusive)
	 * @param rowEnd        the final row index (exclusive)
	 * @param columnIndexes an array of column indexes
	 * @param submatrix     a {@link Matrix}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified submatrix indexes are out of bounds
	 */
	public void setSubmatrix(final int rowStart, final int rowEnd, final int[] columnIndexes,
			final Matrix submatrix) {
		final int rowCount = rowEnd - rowStart;
		final int columnCount = columnIndexes.length;
		try {
			for (int i = 0; i < rowCount; ++i) {
				for (int j = 0; j < columnCount; ++j) {
					elements[(rowStart + i) * n +
							columnIndexes[j]] = submatrix.elements[i * columnCount + j];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds: " + ex);
		}
	}

	/**
	 * Sets {@code this(rowIndexes(:), columnIndexes(:))} to the specified submatrix.
	 * <p>
	 * @param rowIndexes    an array of row indexes
	 * @param columnIndexes an array of column indexes
	 * @param submatrix     a {@link Matrix}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified submatrix indexes are out of bounds
	 */
	public void setSubmatrix(final int[] rowIndexes, final int[] columnIndexes,
			final Matrix submatrix) {
		final int rowCount = rowIndexes.length;
		final int columnCount = columnIndexes.length;
		try {
			for (int i = 0; i < rowCount; ++i) {
				for (int j = 0; j < columnCount; ++j) {
					elements[rowIndexes[i] * n +
							columnIndexes[j]] = submatrix.elements[i * columnCount + j];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds: " + ex);
		}
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
		return Doubles.take(elements);
	}

	/**
	 * Converts {@code this} to a 2D {@code double} array.
	 * <p>
	 * @return a 2D {@code double} array
	 */
	public double[][] toPrimitiveArray2D() {
		final double[][] values = new double[m][n];
		for (int i = 0; i < m; ++i) {
			System.arraycopy(elements, i * n, values[i], 0, n);
		}
		return values;
	}

	/**
	 * Converts {@code this} to a {@link Scalar}.
	 * <p>
	 * @return a {@link Scalar}
	 */
	@Override
	public Scalar toScalar() {
		if (m == 1 && n == 1) {
			return new Scalar(elements[0]);
		}
		throw new IllegalOperationException(
				"Cannot convert a " + getName() + " to a " + Scalar.class.getSimpleName());
	}

	/**
	 * Converts {@code this} to a {@link Vector}.
	 * <p>
	 * @return a {@link Vector}
	 */
	@Override
	public Vector toVector() {
		if (m == 1 || n == 1) {
			return new Vector(toPrimitiveArray(), n != 1);
		}
		throw new IllegalOperationException(
				"Cannot convert a " + getName() + " to a " + Vector.class.getSimpleName());
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
	 * Returns the pivot rows at the specified row indexes.
	 * <p>
	 * @param rowIndexes an array of row indexes
	 * <p>
	 * @return the pivot rows at the specified row indexes
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code rowIndexes} are out of bounds
	 */
	public Matrix pivot(final int[] rowIndexes) {
		return getSubmatrix(rowIndexes, 0, n);
	}

	/**
	 * Returns the unpivot rows at the specified row indexes.
	 * <p>
	 * @param rowIndexes an array of row indexes
	 * <p>
	 * @return the unpivot rows at the specified row indexes
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code rowIndexes} are out of bounds
	 */
	public Matrix unpivot(final int[] rowIndexes) {
		final int rowCount = rowIndexes.length;
		final int[] unpivotIndexes = new int[rowCount];
		for (int i = 0; i < rowCount; ++i) {
			unpivotIndexes[rowIndexes[i]] = i;
		}
		return getSubmatrix(unpivotIndexes, 0, n);
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
				sum += Math.abs(elements[i * n + j]);
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
				sum += Math.abs(elements[i * n + j]);
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
				result = Norms.getEuclideanNorm(result, elements[i * n + j]);
			}
		}
		return result;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parallelizes {@code this}.
	 */
	public static synchronized void parallelize() {
		IO.debug(EMPTY);

		// Initialize
		if (MULTIPLICATION == null) {
			MULTIPLICATION = new Multiplication();
			PARALLELIZE = true;
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
			PARALLELIZE = false;
			MULTIPLICATION.shutdown();
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

	/**
	 * Fills {@code this} with the specified {@code double} value.
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
	 * Applies the specified {@link Function} to {@code this}.
	 * <p>
	 * @param f the {@link Function} to apply
	 * <p>
	 * @return {@code f(this)}
	 */
	@Override
	public Matrix apply(final Function f) {
		return new Matrix(m, f.applyToPrimitiveArray(elements));
	}

	/**
	 * Applies the specified {@link ReducerFunction} to the columns of {@code this}.
	 * <p>
	 * @param f the {@link ReducerFunction} to apply column-wise
	 * <p>
	 * @return {@code f(this)}
	 */
	@Override
	public Entity applyByColumn(final ReducerFunction f) {
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
	 * Applies the specified {@link ReducerFunction} to the rows of {@code this}.
	 * <p>
	 * @param f the {@link ReducerFunction} to apply row-wise
	 * <p>
	 * @return {@code f(this')}
	 */
	@Override
	public Entity applyByRow(final ReducerFunction f) {
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
				result.elements[i * result.n + j] += broadcastedMatrix.elements[i *
						broadcastedMatrix.n + j];
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
				result.elements[i * result.n + j] -= broadcastedMatrix.elements[i *
						broadcastedMatrix.n + j];
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
	 * @throws IllegalArgumentException if the inner dimensions of the matrices do not agree
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
		if (PARALLELIZE) {
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
	 * @throws IllegalArgumentException if the inner dimensions of the matrices do not agree
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
				result.elements[i * result.n + j] *= broadcastedMatrix.elements[i *
						broadcastedMatrix.n + j];
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
				result.elements[i * result.n + j] /= scalar + Maths.TOLERANCE;
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
				result.elements[i * result.n + j] /= broadcastedMatrix.elements[i *
						broadcastedMatrix.n + j] +
						Maths.TOLERANCE;
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
					elements[i * n + j] /= scalar + Maths.TOLERANCE;
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
						Maths.TOLERANCE;
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
				result.elements[i * result.n + j] = Math.pow(elements[i * n + j], scalar);
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
				result.elements[i * result.n + j] = Math.pow(elements[i * n + j],
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
					elements[i * n + j] = Math.pow(elements[i * n + j], scalar);
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
				elements[i * n + j] = Math.pow(elements[i * n + j],
						broadcastedMatrix.elements[i * broadcastedMatrix.n + j]);
			}
		}
		return this;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code this} from the specified offsets.
	 * <p>
	 * @param B       the {@code double} array to multiply
	 * @param c       the constant {@code c} to multiply
	 * @param offset  the offset of {@code this}
	 * @param bOffset the offset of {@code B}
	 */
	public void arraySum(final double[] B, final double c, final int offset, final int bOffset) {
		Maths.arraySum(elements, B, c, offset, bOffset, 0, n);
	}

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code this} from the specified offsets
	 * to the specified length.
	 * <p>
	 * @param B       the {@code double} array to multiply
	 * @param c       the constant {@code c} to multiply
	 * @param offset  the offset of {@code this}
	 * @param bOffset the offset of {@code B}
	 * @param from    the index to start from (inclusive)
	 * @param to      the index to finish at (exclusive)
	 */
	public void arraySum(final double[] B, final double c, final int offset, final int bOffset,
			final int from, final int to) {
		Maths.arraySum(elements, B, c, offset, bOffset, from, to);
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
		double t = 0.;
		final int limit = Math.min(m, n);
		for (int i = 0; i < limit; ++i) {
			t += elements[i * n + i];
		}
		return t;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link Matrix} loaded from the specified file.
	 * <p>
	 * @param path the path to the file to load
	 * <p>
	 * @return a {@link Matrix} loaded from the specified file
	 * <p>
	 * @throws IOException if there is a problem with reading the specified file
	 */
	public static Matrix create(final String path)
			throws IOException {
		final FileHandler fileHandler = new FileHandler(path);
		return create(fileHandler.getReader(), fileHandler.countLines(true), false);
	}

	/**
	 * Creates a {@link Matrix} loaded from the specified file.
	 * <p>
	 * @param path      the path to the file to load
	 * @param transpose the flag specifying whether to transpose
	 * <p>
	 * @return a {@link Matrix} loaded from the specified file
	 * <p>
	 * @throws IOException if there is a problem with reading the specified file
	 */
	public static Matrix create(final String path, final boolean transpose)
			throws IOException {
		final FileHandler fileHandler = new FileHandler(path);
		return create(fileHandler.getReader(), fileHandler.countLines(true), transpose);
	}

	/**
	 * Creates a {@link Matrix} loaded from the specified reader.
	 * <p>
	 * @param reader    a {@link BufferedReader}
	 * @param length    the number of lines to load
	 * @param transpose the flag specifying whether to transpose
	 * <p>
	 * @return a {@link Matrix} loaded from the specified reader
	 * <p>
	 * @throws IOException if there is a problem with reading
	 */
	public static Matrix create(final BufferedReader reader, final int length,
			final boolean transpose)
			throws IOException {
		final int m = length;
		int n = 0;
		// Parse the file
		String line;
		if ((line = reader.readLine()) != null) {
			// Find the delimiter (take the first one in the list in case of different delimiters)
			String delimiter = null;
			for (final char d : COLUMN_DELIMITERS) {
				final int occurrenceCount = Strings.getIndexes(line, d).size();
				if (occurrenceCount > 0) {
					if (n == 0) {
						delimiter = Strings.toString(d);
						n = occurrenceCount;
					} else {
						IO.warn("The file contains different delimiters; ",
								Strings.quote(delimiter), " is selected");
						break;
					}
				}
			}
			++n;
			// Create the matrix
			final Matrix matrix;
			if (transpose) {
				matrix = new Matrix(n, m);
			} else {
				matrix = new Matrix(m, n);
			}
			// Scan the file line by line
			int i = 0;
			String[] values = line.split(delimiter);
			if (transpose) {
				matrix.setColumn(i, Doubles.toPrimitiveArray(values));
			} else {
				matrix.setRow(i, Doubles.toPrimitiveArray(values));
			}
			++i;
			while ((line = reader.readLine()) != null) {
				values = line.split(delimiter);
				if (values == null || values.length == 0 || values[0] == null ||
						EMPTY.equals(values[0])) {
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
						matrix.setColumn(i, Doubles.toPrimitiveArray(values));
					} else {
						matrix.setRow(i, Doubles.toPrimitiveArray(values));
					}
					++i;
				}
			}
			return matrix;
		}
		return null;
	}

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
			final List<Integer> indexes = Strings.getIndexes(expression, delimiters);
			if (indexes.size() == 2) {
				final int fromIndex = indexes.get(0);
				final int toIndex = indexes.get(1);
				if (fromIndex < toIndex && expression.charAt(fromIndex) == delimiters[0] &&
						expression.charAt(toIndex) == delimiters[1]) {
					// Get the content
					final String content = expression.substring(fromIndex + 1, toIndex).trim();
					// Get the rows
					final List<String> rows = Strings.removeEmpty(Strings.split(content,
							ROW_DELIMITER));
					// Count the number of rows
					final int m = rows.size();
					// Count the number of columns
					String row = rows.get(0).trim();
					final int n = Strings.removeEmpty(Strings.split(row, COLUMN_DELIMITERS)).size();
					// Create the elements of the matrix
					final double[] elements = new double[m * n];
					List<String> rowElements;
					// Fill the matrix
					for (int i = 0; i < m; ++i) {
						// Get the current row
						row = rows.get(i).trim();
						// Get the elements of the row
						rowElements = Strings.removeEmpty(Strings.split(row, COLUMN_DELIMITERS));
						// Store the elements
						for (int j = 0; j < n; ++j) {
							elements[i * n + j] = Doubles.convert(rowElements.get(j));
						}
					}
					return new Matrix(m, elements);
				}
			} else {
				final int size = indexes.size();
				if (size > 2) {
					throw new ParseException("There are too many square brackets " +
							Arguments.expectedButFound(size, 2), indexes.get(2));
				}
				throw new ParseException("There are not enough square brackets " +
						Arguments.expectedButFound(size, 2), 0);
			}
		} catch (final NumberFormatException ex) {
			IO.error(ex);
		} catch (final ParseException ex) {
			IO.error(ex);
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// EXPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Saves {@code this} to the specified file.
	 * <p>
	 * @param path the path to the file to save to
	 * <p>
	 * @return {@code true} if {@code this} is saved to the specified file, {@code false} otherwise
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code path}
	 */
	public boolean save(final String path)
			throws FileNotFoundException {
		return toTable().save(path, false);
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

	/**
	 * Requires {@code this} to have the row dimension equals to the column dimension.
	 * <p>
	 * @throws IllegalArgumentException if {@code this} is not square
	 */
	public void requireSquare() {
		if (!isSquare()) {
			throw new IllegalOperationException("The matrix is not square");
		}
	}

	/**
	 * Requires the specified {@link Matrix} to have the same dimensions as {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @throws IllegalArgumentException if the dimensions of the matrices do not agree
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
	 * @throws IllegalArgumentException if the inner dimensions of the matrices do not agree
	 */
	public void requireInnerDimension(final Matrix matrix) {
		MatrixArguments.requireInnerDimension(matrix, n);
	}

	/**
	 * Tests whether the specified {@link String} is a parsable {@link Matrix}.
	 * <p>
	 * @param text a {@link String}
	 * <p>
	 * @return {@code true} if the specified {@link String} is a parsable {@link Matrix},
	 *         {@code false} otherwise
	 */
	public static boolean is(final String text) {
		final char[] delimiters = new char[] {LEFT_BRACKET, RIGHT_BRACKET};
		final List<Integer> indexes = Strings.getIndexes(text.trim(), delimiters);
		if (indexes.size() == 2) {
			final int fromIndex = indexes.get(0);
			final int toIndex = indexes.get(1);
			if (fromIndex < toIndex && text.charAt(fromIndex) == delimiters[0] &&
					text.charAt(toIndex) == delimiters[1]) {
				return true;
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public Matrix clone() {
		try {
			final Matrix clone = (Matrix) super.clone();
			clone.size = Objects.clone(size);
			clone.elements = Objects.clone(elements);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
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
	 * @param other     the other {@link Object} to compare against for equality
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other} within {@code tolerance},
	 *         {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
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
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	@SuppressWarnings("unchecked")
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
		final StringBuilder builder;
		if (useMultipleLines) {
			builder = Strings.createBuilder(m + m * n * (NUMBER_LENGTH + 1));
		} else {
			builder = Strings.createBuilder(2 + m + m * n * (NUMBER_LENGTH + 1));
		}
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				final String formattedElement = formatNumber(elements[i * n + j]);
				final int padding = Math.max(1, columnWidth - formattedElement.length());
				for (int k = 0; k < padding; ++k) {
					builder.append(' ');
				}
				builder.append(formattedElement);
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
		 * and conquers them. Returns the exit code for each of them.
		 * <p>
		 * @param input the input {@link Triple} of result {@link Matrix}, left-hand side operand
		 *              {@link Matrix} and right-hand side operand {@link Matrix} to process
		 * <p>
		 * @return {@code IO.EXIT_SUCCESS} if the multiplication succeeds, {@code IO.EXIT_FAILURE}
		 *         otherwise for each execution slice
		 */
		protected int[] divideAndConquer(final Triple<Matrix, Matrix, Matrix> input) {
			return divideAndConquer(input, 0, input.getFirst().m);
		}

		/**
		 * Conquers the execution slice with the specified input {@link Triple} and {@link Interval}
		 * and returns the exit code.
		 * <p>
		 * @param input    the input {@link Triple} of result {@link Matrix}, left-hand side operand
		 *                 {@link Matrix} and right-hand side operand {@link Matrix} to process
		 * @param interval the {@link Interval} of {@link Integer} of the execution slice to conquer
		 * <p>
		 * @return {@code IO.EXIT_SUCCESS} if conquering the execution slice succeeds,
		 *         {@code IO.EXIT_FAILURE} otherwise
		 */
		@Override
		protected int conquer(final Triple<Matrix, Matrix, Matrix> input,
				final Interval<Integer> interval) {
			return process(input.getFirst(), input.getSecond(), input.getThird(),
					interval.getLowerBound(), interval.getUpperBound());
		}

		/**
		 * Processes the multiplication with the specified result {@link Matrix}, left-hand side
		 * operand {@link Matrix} and right-hand side operand {@link Matrix} and returns the exit
		 * code.
		 * <p>
		 * @param result the result {@link Matrix}
		 * @param left   the left-hand side operand {@link Matrix}
		 * @param right  the right-hand side operand {@link Matrix}
		 * <p>
		 * @return {@code IO.EXIT_SUCCESS} if the multiplication succeeds, {@code IO.EXIT_FAILURE}
		 *         otherwise
		 */
		protected static int process(final Matrix result, final Matrix left, final Matrix right) {
			return process(result, left, right, 0, left.m);
		}

		/**
		 * Processes the multiplication with the specified result {@link Matrix}, left-hand side
		 * operand {@link Matrix} and right-hand side operand {@link Matrix} from the specified
		 * index to the specified index and returns the exit code.
		 * <p>
		 * @param result the result {@link Matrix}
		 * @param left   the left-hand side operand {@link Matrix}
		 * @param right  the right-hand side operand {@link Matrix}
		 * @param from   the index to start multiplying from (inclusive)
		 * @param to     the index to finish multiplying at (exclusive)
		 * <p>
		 * @return {@code IO.EXIT_SUCCESS} if the multiplication succeeds, {@code IO.EXIT_FAILURE}
		 *         otherwise
		 */
		protected static int process(final Matrix result, final Matrix left, final Matrix right,
				final int from, final int to) {
			final int innerDimension = left.n; // or right.m
			for (int i = from; i < to; ++i) {
				for (int k = 0; k < innerDimension; ++k) {
					result.arraySum(right.elements, left.elements[i * left.n + k],
							i * result.n, k * right.n);
				}
			}
			return IO.EXIT_SUCCESS;
		}

		/**
		 * Creates a copy of {@code this}.
		 * <p>
		 * @return a copy of {@code this}
		 *
		 * @see jupiter.common.model.ICloneable
		 */
		@Override
		public Multiplication clone() {
			return (Multiplication) super.clone();
		}
	}
}
