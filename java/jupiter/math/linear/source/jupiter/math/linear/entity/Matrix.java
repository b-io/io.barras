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
import static jupiter.integration.gpu.OpenCL.CL;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.io.file.FileHandler;
import jupiter.common.math.Interval;
import jupiter.common.math.Maths;
import jupiter.common.math.Statistics;
import jupiter.common.struct.table.DoubleTable;
import jupiter.common.struct.table.Table;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.struct.tuple.Triple;
import jupiter.common.test.Arguments;
import jupiter.common.thread.SynchronizedWorkQueue;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.util.Characters;
import jupiter.common.util.Doubles;
import jupiter.common.util.Formats;
import jupiter.common.util.Longs;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.integration.gpu.OpenCL;
import jupiter.math.analysis.function.Function;
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
	private static final long serialVersionUID = 2562031458053913335L;

	/**
	 * The column delimiters.
	 */
	public static final char[] COLUMN_DELIMITERS = Characters.toPrimitiveArray(' ', '\t', ',');
	/**
	 * The row delimiter.
	 */
	public static final char ROW_DELIMITER = ';';

	/**
	 * The flag specifying whether to parallelize using a work queue.
	 */
	public static volatile boolean PARALLELIZE = false;
	/**
	 * The work queue for computing the dot product.
	 */
	protected static volatile WorkQueue<Triple<Matrix, Matrix, Interval<Integer>>, Pair<Matrix, Interval<Integer>>> WORK_QUEUE = null;


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
	 * The dimensions.
	 */
	protected final Dimensions size;
	/**
	 * The elements.
	 */
	protected final double[][] elements;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a square {@link Matrix} of zeros of the specified number of rows and columns.
	 * <p>
	 * @param size the number of rows and columns
	 */
	public Matrix(final int size) {
		this(size, size);
	}

	/**
	 * Constructs a {@link Matrix} of zeros of the specified numbers of rows and columns.
	 * <p>
	 * @param m the number of rows
	 * @param n the number of columns
	 */
	public Matrix(final int m, final int n) {
		// Set the numbers of rows and columns
		this.m = m;
		this.n = n;
		size = new Dimensions(m, n);
		// Set the elements
		elements = new double[m][n];
	}

	/**
	 * Constructs a constant {@link Matrix} of the specified numbers of rows and columns from the
	 * specified value.
	 * <p>
	 * @param m     the number of rows
	 * @param n     the number of columns
	 * @param value a {@code double} value
	 */
	public Matrix(final int m, final int n, final double value) {
		// Set the numbers of rows and columns
		this.m = m;
		this.n = n;
		size = new Dimensions(m, n);
		// Set the elements
		elements = new double[m][n];
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				elements[i][j] = value;
			}
		}
	}

	/**
	 * Constructs a {@link Matrix} of the specified number of rows from the specified values.
	 * <p>
	 * @param m      the number of rows
	 * @param values an array of {@code double} values
	 * <p>
	 * @throws IllegalArgumentException if the length of {@code values} is not a multiple of
	 *                                  {@code m}
	 */
	public Matrix(final int m, final double[] values) {
		this(m, values, false);
	}

	/**
	 * Constructs a {@link Matrix} of the specified number of rows (or columns if {@code transpose})
	 * from the specified values.
	 * <p>
	 * @param dimension the number of rows (or columns if {@code transpose})
	 * @param values    an array of {@code double} values
	 * @param transpose the flag specifying whether to transpose
	 * <p>
	 * @throws IllegalArgumentException if the length of {@code values} is not a multiple of
	 *                                  {@code dimension}
	 */
	public Matrix(final int dimension, final double[] values, final boolean transpose) {
		final int length = values.length;
		// Set the numbers of rows and columns
		if (transpose) {
			n = dimension;
			m = dimension > 0 ? length / dimension : 0;
		} else {
			m = dimension;
			n = dimension > 0 ? length / dimension : 0;
		}
		size = new Dimensions(m, n);
		// Check the length of the specified array
		if (m * n != length) {
			throw new IllegalArgumentException("The length of the specified array " + length +
					" is not a multiple of " + dimension);
		}
		// Set the elements
		elements = new double[m][n];
		if (transpose) {
			for (int j = 0; j < n; ++j) {
				setColumn(j, Doubles.take(values, j * m, m));
			}
		} else {
			for (int i = 0; i < m; ++i) {
				setRow(i, Doubles.take(values, i * n, n));
			}
		}
	}

	/**
	 * Constructs a {@link Matrix} from the specified elements.
	 * <p>
	 * @param elements a 2D array of {@code double} values
	 * <p>
	 * @throws IllegalArgumentException if {@code elements} has different row lengths
	 */
	public Matrix(final double[]... elements) {
		// Set the numbers of rows and columns
		m = elements.length;
		n = elements[0].length;
		size = new Dimensions(m, n);
		// Check the row lengths of the specified array
		for (int i = 0; i < m; ++i) {
			if (elements[i].length != n) {
				throw new IllegalArgumentException(
						"The specified 2D array has different row lengths");
			}
		}
		// Set the elements
		this.elements = elements;
	}

	/**
	 * Constructs a {@link Matrix} of the specified numbers of rows and columns from the specified
	 * values.
	 * <p>
	 * @param m      the number of rows
	 * @param n      the number of columns
	 * @param values a 2D array of {@code double} values
	 * <p>
	 * @throws IllegalArgumentException if the rows of {@code elements} have not the same length
	 */
	public Matrix(final int m, final int n, final double[]... values) {
		// Set the numbers of rows and columns
		this.m = m;
		this.n = n;
		size = new Dimensions(m, n);
		// Check the row and column lengths of the specified array
		if (values.length < m) {
			throw new IllegalArgumentException(
					"The row length of the specified 2D array is less than " + m);
		}
		elements = new double[m][n];
		for (int i = 0; i < m; ++i) {
			if (values[i].length < n) {
				throw new IllegalArgumentException(
						"The specified 2D array has a column length less than " + n);
			}
			setRow(i, values[i], 0, n);
		}
	}

	/**
	 * Constructs a {@link Matrix} from the specified table.
	 * <p>
	 * @param table a {@link DoubleTable}
	 */
	public Matrix(final DoubleTable table) {
		// Set the numbers of rows and columns
		m = table.getRowCount();
		n = table.getColumnCount();
		size = new Dimensions(m, n);
		// Set the elements
		elements = table.toPrimitiveArray2D();
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
	public double[][] getElements() {
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
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
	 */
	public double get(final int i, final int j) {
		return elements[i][j];
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the elements of the specified row.
	 * <p>
	 * @param i the row index
	 * <p>
	 * @return the elements of the specified row
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified index is out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
	 */
	public double[] getRow(final int i, final int from, final int length) {
		final double[] row = new double[Math.min(length, n - from)];
		System.arraycopy(elements[i], from, row, 0, row.length);
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
	 * @throws ArrayIndexOutOfBoundsException if the specified index is out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
	 */
	public double[] getColumn(final int j, final int from, final int length) {
		final double[] column = new double[Math.min(length, m - from)];
		for (int i = 0; i < column.length; ++i) {
			column[i] = elements[from + i][j];
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
				submatrix.setRow(i, elements[rowStart + i], columnStart);
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds" + IO.appendException(ex));
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
				submatrix.setRow(i, elements[rowIndexes[i]], columnStart);
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds" + IO.appendException(ex));
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
					submatrix.elements[i][j] = elements[rowStart + i][columnIndexes[j]];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds" + IO.appendException(ex));
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
					submatrix.elements[i][j] = elements[rowIndexes[i]][columnIndexes[j]];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds" + IO.appendException(ex));
		}
		return submatrix;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the element at the specified row and column indexes.
	 * <p>
	 * @param i     the row index
	 * @param j     the column index
	 * @param value a {@code double} value
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
	 */
	public void set(final int i, final int j, final double value) {
		elements[i][j] = value;
	}

	/**
	 * Sets the element at the specified row and column indexes.
	 * <p>
	 * @param i     the row index
	 * @param j     the column index
	 * @param value an {@link Object}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
	 */
	public void set(final int i, final int j, final Object value) {
		elements[i][j] = Doubles.convert(value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values an array of {@code double} values
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified index is out of bounds
	 */
	public void setRow(final int i, final double[] values) {
		setRow(i, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index (inclusive).
	 * <p>
	 * @param i      the row index
	 * @param values an array of {@code double} values
	 * @param from   the initial column index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
	 */
	public void setRow(final int i, final double[] values, final int from) {
		setRow(i, values, from, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index (inclusive) to the
	 * specified length.
	 * <p>
	 * @param i      the row index
	 * @param values an array of {@code double} values
	 * @param from   the initial column index (inclusive)
	 * @param length the number of row elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
	 */
	public void setRow(final int i, final double[] values, final int from, final int length) {
		System.arraycopy(values, 0, elements[i], from, Math.min(length, n));
	}

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values a {@link Collection} of {@link Double}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified index is out of bounds
	 */
	public void setRow(final int i, final Collection<Double> values) {
		setRow(i, Doubles.collectionToPrimitiveArray(values));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values an array of {@code double} values
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified index is out of bounds
	 */
	public void setColumn(final int j, final double[] values) {
		setColumn(j, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index (inclusive).
	 * <p>
	 * @param j      the column index
	 * @param values an array of {@code double} values
	 * @param from   the initial row index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
	 */
	public void setColumn(final int j, final double[] values, final int from) {
		setColumn(j, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index (inclusive) to the
	 * specified length.
	 * <p>
	 * @param j      the column index
	 * @param values an array of {@code double} values
	 * @param from   the initial row index (inclusive)
	 * @param length the number of column elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified indexes are out of bounds
	 */
	public void setColumn(final int j, final double[] values, final int from, final int length) {
		for (int i = 0; i < Math.min(length, m); ++i) {
			elements[i][j] = values[from + i];
		}
	}

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values a {@link Collection} of {@link Double}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified index is out of bounds
	 */
	public void setColumn(final int j, final Collection<Double> values) {
		setColumn(j, Doubles.collectionToPrimitiveArray(values));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values an array of {@code double} values
	 * <p>
	 * @throws IndexOutOfBoundsException if the specified array is not of the same length
	 */
	public void setAll(final double... values) {
		for (int i = 0; i < m; ++i) {
			System.arraycopy(values, i * n, elements[i], 0, n);
		}
	}

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values a 2D array of {@code double} values
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
				setRow(rowStart + i, submatrix.elements[i], columnStart, columnCount);
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds" + IO.appendException(ex));
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
				setRow(rowIndexes[i], submatrix.elements[i], columnStart, columnCount);
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds" + IO.appendException(ex));
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
					elements[rowStart + i][columnIndexes[j]] = submatrix.elements[i][j];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds" + IO.appendException(ex));
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
					elements[rowIndexes[i]][columnIndexes[j]] = submatrix.elements[i][j];
				}
			}
		} catch (final ArrayIndexOutOfBoundsException ex) {
			throw new ArrayIndexOutOfBoundsException(
					"The specified submatrix indexes are out of bounds" + IO.appendException(ex));
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts {@code this} to an array of {@code double} values.
	 * <p>
	 * @return an array of {@code double} values
	 */
	@Override
	public double[] toPrimitiveArray() {
		final double[] array = new double[m * n];
		for (int i = 0; i < m; ++i) {
			System.arraycopy(elements[i], 0, array, i * n, n);
		}
		return array;
	}

	/**
	 * Converts {@code this} to a 2D array of {@code double} values.
	 * <p>
	 * @return a 2D array of {@code double} values
	 */
	public double[][] toPrimitiveArray2D() {
		final double[][] values = new double[m][n];
		for (int i = 0; i < m; ++i) {
			System.arraycopy(elements[i], 0, values[i], 0, n);
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
			return new Scalar(elements[0][0]);
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

	/**
	 * Returns the size of {@code this}.
	 * <p>
	 * @return {@code size(this)}
	 */
	@Override
	public Vector size() {
		return new Vector(new double[] {
			m, n
		});
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
		final Matrix identity = new Matrix(size, size);
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				identity.elements[i][j] = i == j ? 1. : 0.;
			}
		}
		return identity;
	}

	/**
	 * Returns the identity {@link Matrix} of the specified numbers of rows and columns.
	 * <p>
	 * @param m the number of rows
	 * @param n the number of columns
	 * <p>
	 * @return {@code eye(m, n)}
	 */
	public static Matrix identity(final int m, final int n) {
		final Matrix identity = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				identity.elements[i][j] = i == j ? 1. : 0.;
			}
		}
		return identity;
	}

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
	 * @param m the number of rows
	 * @param n the number of columns
	 * <p>
	 * @return {@code rand(m, n)}
	 */
	public static Matrix random(final int m, final int n) {
		final Matrix random = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				random.elements[i][j] = Doubles.random();
			}
		}
		return random;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the pivot rows at the specified row indexes.
	 * <p>
	 * @param rowIndexes an array of row indexes
	 * <p>
	 * @return the pivot rows at the specified row indexes
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if the specified row indexes are out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if the specified row indexes are out of bounds
	 */
	public Matrix unpivot(final int[] rowIndexes) {
		final int length = rowIndexes.length;
		final int[] unpivotIndexes = new int[length];
		for (int i = 0; i < length; ++i) {
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
		double f = 0.;
		for (int j = 0; j < n; ++j) {
			double s = 0.;
			for (int i = 0; i < m; ++i) {
				s += Math.abs(elements[i][j]);
			}
			f = Math.max(f, s);
		}
		return f;
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
		double f = 0.;
		for (int i = 0; i < m; ++i) {
			double s = 0.;
			for (int j = 0; j < n; ++j) {
				s += Math.abs(elements[i][j]);
			}
			f = Math.max(f, s);
		}
		return f;
	}

	/**
	 * Returns the Frobenius norm, i.e. the square root of the sum of the squares of all the values
	 * of the elements.
	 * <p>
	 * @return the square root of the sum of the squares of all the values of the elements
	 */
	public double normF() {
		double f = 0.;
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				f = Norms.getEuclideanNorm(f, elements[i][j]);
			}
		}
		return f;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts {@code this}.
	 */
	public static synchronized void start() {
		IO.debug("");

		// Initialize
		if (WORK_QUEUE == null) {
			WORK_QUEUE = new SynchronizedWorkQueue<Triple<Matrix, Matrix, Interval<Integer>>, Pair<Matrix, Interval<Integer>>>(
					new DotProduct());
			PARALLELIZE = true;
		} else {
			IO.warn("The work queue ", WORK_QUEUE, " has already started");
		}
	}

	/**
	 * Stops {@code this}.
	 */
	public static synchronized void stop() {
		IO.debug("");

		// Shutdown
		if (WORK_QUEUE != null) {
			PARALLELIZE = false;
			WORK_QUEUE.shutdown();
		}
	}

	/**
	 * Restarts {@code this}.
	 */
	public static synchronized void restart() {
		IO.debug("");

		stop();
		start();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fills {@code this} with the specified value.
	 * <p>
	 * @param value the value to fill with
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
	public Matrix apply(final Function f) {
		return new Matrix(f.applyToPrimitiveArray2D(elements));
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
				result.elements[i][j] = -elements[i][j];
			}
		}
		return result;
	}

	/**
	 * Returns the sum of the elements.
	 * <p>
	 * @return the sum of the elements
	 */
	@Override
	public double sum() {
		double sum = 0.;
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				sum += elements[i][j];
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
		return new Matrix(Doubles.transpose(elements));
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
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i][j] = elements[i][j] + scalar;
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
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i][j] = elements[i][j] + broadcastedMatrix.elements[i][j];
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
					elements[i][j] += scalar;
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
				elements[i][j] += broadcastedMatrix.elements[i][j];
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
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i][j] = elements[i][j] - scalar;
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
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i][j] = elements[i][j] - broadcastedMatrix.elements[i][j];
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
					elements[i][j] -= scalar;
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
				elements[i][j] -= broadcastedMatrix.elements[i][j];
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
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i][j] = elements[i][j] * scalar;
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
		// Broadcast
		final Matrix broadcastedMatrix;
		if (matrix instanceof Vector) {
			broadcastedMatrix = ((Vector) matrix).toMatrix(n);
		} else {
			broadcastedMatrix = matrix;
		}

		// Check the arguments
		requireInnerDimension(broadcastedMatrix);

		// Test whether the result is a scalar or a matrix
		if (m == 1 && broadcastedMatrix.n == 1) {
			// - Scalar
			final double[] row = elements[0];
			double sum = 0.;
			for (int k = 0; k < n; ++k) {
				sum += row[k] * broadcastedMatrix.elements[k][0];
			}
			return new Scalar(sum);
		}
		// - Matrix
		final Matrix result = new Matrix(m, broadcastedMatrix.n);
		if (PARALLELIZE) {
			// Initialize
			final int intervalCount = Math.min(m, WorkQueue.MAX_THREADS);
			final int rowCountPerInterval = m / intervalCount;
			final int remainingRowCount = m - intervalCount * rowCountPerInterval;
			final List<Long> ids = new ArrayList<Long>(intervalCount);

			// Distribute the tasks
			for (int i = 0; i < intervalCount; ++i) {
				final Interval<Integer> interval = new Interval<Integer>(i * rowCountPerInterval,
						(i + 1) * rowCountPerInterval);
				ids.add(WORK_QUEUE.submit(new Triple<Matrix, Matrix, Interval<Integer>>(this,
						broadcastedMatrix, interval)));
			}
			if (remainingRowCount > 0) {
				final Interval<Integer> interval = new Interval<Integer>(
						intervalCount * rowCountPerInterval, m);
				final Matrix submatrix = DotProduct.apply(this, broadcastedMatrix, interval);
				result.setSubmatrix(interval.getLowerBound(), result.m, 0, submatrix.n, submatrix);
			}

			// Collect the results
			for (final long id : ids) {
				final Pair<Matrix, Interval<Integer>> pair = WORK_QUEUE.get(id);
				final Matrix submatrix = pair.getFirst();
				final Interval<Integer> interval = pair.getSecond();
				result.setSubmatrix(interval.getLowerBound(),
						interval.getLowerBound() + submatrix.m, 0, submatrix.n, submatrix);
			}
		} else {
			for (int i = 0; i < m; ++i) {
				final double[] row = elements[i];
				for (int j = 0; j < broadcastedMatrix.n; ++j) {
					double sum = 0.;
					for (int k = 0; k < n; ++k) {
						sum += row[k] * broadcastedMatrix.elements[k][j];
					}
					result.elements[i][j] = sum;
				}
			}
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
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i][j] = elements[i][j] * broadcastedMatrix.elements[i][j];
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
					elements[i][j] *= scalar;
				}
			}
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
	public Entity multiply(final Matrix matrix) {
		return times(matrix);
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
				elements[i][j] *= broadcastedMatrix.elements[i][j];
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
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i][j] = elements[i][j] / scalar;
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
		final Matrix result = new Matrix(m, n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				result.elements[i][j] = elements[i][j] / broadcastedMatrix.elements[i][j];
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
					elements[i][j] /= scalar;
				}
			}
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
	public Entity divide(final Matrix matrix) {
		return multiply(matrix.inverse());
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
				elements[i][j] /= broadcastedMatrix.elements[i][j];
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
				result.elements[i][j] = Math.pow(elements[i][j], scalar);
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
				result.elements[i][j] = Math.pow(elements[i][j], broadcastedMatrix.elements[i][j]);
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
					elements[i][j] = Math.pow(elements[i][j], scalar);
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
				elements[i][j] = Math.pow(elements[i][j], broadcastedMatrix.elements[i][j]);
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
	 * <p>
	 * @since 1.6
	 */
	public Entity forward(final Entity A, final Entity B) {
		if (OpenCL.USE && !(A instanceof Scalar) && !(B instanceof Scalar)) {
			final Matrix a = A.toMatrix();
			final Matrix b = B.toMatrix();
			if (CL.test(n, a.n, b.n)) {
				return new Matrix(m, CL.forward(toPrimitiveArray(), a.toPrimitiveArray(),
						b.toPrimitiveArray(), n, a.n, b.n));
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
		return m == n ? new LUDecomposition(this).solve(B) : new QRDecomposition(this).solve(B);
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
		return solve(identity(m, m));
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
		for (int i = 0; i < Math.min(m, n); ++i) {
			t += elements[i][i];
		}
		return t;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Matrix} encoded in the specified {@link String}, or {@code null} if the
	 * specified {@link String} does not contain any {@link Matrix}.
	 * <p>
	 * @param expression the {@link String} to parse
	 * <p>
	 * @return a {@link Matrix} encoded in the specified {@link String}, or {@code null} if the
	 *         specified {@link String} does not contain any {@link Matrix}
	 */
	public static Matrix parse(final String expression) {
		try {
			final char[] delimiters = Characters.toPrimitiveArray(Characters.LEFT_BRACKET,
					Characters.RIGHT_BRACKET);
			final List<Integer> indexes = Strings.getAllIndexes(expression.trim(), delimiters);
			if (indexes.size() == 2) {
				final int from = indexes.get(0);
				final int to = indexes.get(1);
				if (from < to && expression.charAt(from) == delimiters[0] &&
						expression.charAt(to) == delimiters[1]) {
					final String content = expression.substring(from + 1, to);
					// Get the rows
					final List<String> rows = Strings.split(content, ROW_DELIMITER);
					// Count the number of rows
					final int m = rows.size();
					// Count the number of columns
					String row = rows.get(0);
					final int n = Strings.splitInside(row, COLUMN_DELIMITERS).size();
					// Create the elements of the matrix
					final double[][] elements = new double[m][n];
					List<String> rowElements;
					// Fill the matrix
					for (int i = 0; i < m; ++i) {
						// Get the current row
						row = rows.get(i);
						// Get the elements of the row
						rowElements = Strings.split(row, COLUMN_DELIMITERS);
						// Store the elements
						for (int j = 0; j < n; ++j) {
							elements[i][j] = Doubles.convert(rowElements.get(j));
						}
					}
					return new Matrix(m, n, elements);
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

	/**
	 * Returns a {@link Matrix} loaded from the specified file.
	 * <p>
	 * @param pathName the path name of the file to load
	 * <p>
	 * @return a {@link Matrix} loaded from the specified file
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public static Matrix load(final String pathName)
			throws IOException {
		final FileHandler fileHandler = new FileHandler(pathName);
		return load(fileHandler.getReader(), fileHandler.countLines(true), false);
	}

	/**
	 * Returns a {@link Matrix} loaded from the specified file.
	 * <p>
	 * @param pathName  the path name of the file to load
	 * @param transpose the flag specifying whether to transpose
	 * <p>
	 * @return a {@link Matrix} loaded from the specified file
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public static Matrix load(final String pathName, final boolean transpose)
			throws IOException {
		final FileHandler fileHandler = new FileHandler(pathName);
		return load(fileHandler.getReader(), fileHandler.countLines(true), transpose);
	}

	/**
	 * Returns a {@link Matrix} loaded from the specified reader.
	 * <p>
	 * @param reader    a {@link BufferedReader}
	 * @param length    the number of lines to load
	 * @param transpose the flag specifying whether to transpose
	 * <p>
	 * @return a {@link Matrix} loaded from the specified reader
	 * <p>
	 * @throws IOException if there is a problem with reading
	 */
	public static Matrix load(final BufferedReader reader, final int length,
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
				final int occurrenceCount = Strings.getAllIndexes(line, d).size();
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
						Strings.EMPTY.equals(values[0])) {
					IO.warn("There is no element at line ", i, " ",
							Arguments.expectedButFound(0, n));
				} else if (values.length < n) {
					IO.error("There are not enough elements at line ", i, " ",
							Arguments.expectedButFound(values.length, n));
				} else {
					if (values.length > n) {
						IO.warn("There are too many elements at line ", i, " ",
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
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Requires the specified {@link Matrix} to have the same dimensions as {@code this}.
	 * <p>
	 * @param matrix a {@link Matrix}
	 * <p>
	 * @throws IllegalArgumentException if the dimensions of the matrices do not agree
	 */
	protected void requireDimensions(final Matrix matrix) {
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
	protected void requireInnerDimension(final Matrix matrix) {
		MatrixArguments.requireInnerDimension(matrix, n);
	}

	/**
	 * Tests whether {@code string} is a parsable {@link Matrix}.
	 * <p>
	 * @param string a {@link String}
	 * <p>
	 * @return {@code true} if {@code string} is a parsable {@link Matrix}, {@code false} otherwise
	 */
	public static boolean isMatrix(final String string) {
		final char[] delimiters = Characters.toPrimitiveArray('[', ']');
		final List<Integer> indexes = Strings.getAllIndexes(string.trim(), delimiters);
		if (indexes.size() == 2) {
			final int from = indexes.get(0);
			final int to = indexes.get(1);
			if (from < to && string.charAt(from) == delimiters[0] &&
					string.charAt(to) == delimiters[1]) {
				return true;
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Matrix clone() {
		return new Matrix(m, n, elements);
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
		if (other == null || !(other instanceof Matrix)) {
			return false;
		}
		final Matrix otherMatrix = (Matrix) other;
		if (otherMatrix.m != m || otherMatrix.n != n) {
			return false;
		}
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (!Doubles.equals(elements[i][j], otherMatrix.elements[i][j], tolerance)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = Longs.hashCode(serialVersionUID);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				hashCode = Objects.hashCode(i, j * hashCode, elements[i][j]);
			}
		}
		return hashCode;
	}

	@Override
	public String toString() {
		return toString(Formats.MIN_NUMBER_LENGTH, false);
	}

	public String toString(final int columnWidth, final boolean multiLines) {
		final StringBuilder builder;
		if (multiLines) {
			builder = Strings.createBuilder(m + m * n * (Formats.DEFAULT_NUMBER_LENGTH + 1));
		} else {
			builder = Strings.createBuilder(2 + m + m * n * (Formats.DEFAULT_NUMBER_LENGTH + 1));
		}
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				final String formattedElement = Formats.format(elements[i][j]);
				final int padding = Math.max(1, columnWidth - formattedElement.length());
				for (int k = 0; k < padding; ++k) {
					builder.append(' ');
				}
				builder.append(formattedElement);
			}
			if (i < m - 1) {
				if (multiLines) {
					builder.append("\n");
				} else {
					builder.append(ROW_DELIMITER);
				}
			}
		}
		if (multiLines) {
			return builder.toString();
		}
		return Strings.bracketize(builder.toString());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class DotProduct
			extends
			Worker<Triple<Matrix, Matrix, Interval<Integer>>, Pair<Matrix, Interval<Integer>>> {

		protected DotProduct() {
			super();
		}

		public static Matrix apply(final Matrix left, final Matrix right,
				final Interval<Integer> interval) {
			// Initialize
			final int m = interval.getUpperBound() - interval.getLowerBound();
			final int innerDimension = left.n; // or right.m
			final int n = right.n;
			final Matrix result = new Matrix(m, n);

			// Compute
			for (int i = 0; i < m; ++i) {
				final double[] leftRow = left.elements[interval.getLowerBound() + i];
				for (int j = 0; j < n; ++j) {
					double sum = 0.;
					for (int k = 0; k < innerDimension; ++k) {
						sum += leftRow[k] * right.elements[k][j];
					}
					result.elements[i][j] = sum;
				}
			}
			return result;
		}

		@Override
		public Pair<Matrix, Interval<Integer>> call(
				final Triple<Matrix, Matrix, Interval<Integer>> input) {
			return new Pair<Matrix, Interval<Integer>>(
					apply(input.getFirst(), input.getSecond(), input.getThird()), input.getThird());
		}

		@Override
		public DotProduct clone() {
			return new DotProduct();
		}
	}
}
