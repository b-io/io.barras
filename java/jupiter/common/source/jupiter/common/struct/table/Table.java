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
package jupiter.common.struct.table;

import static jupiter.common.io.IO.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.io.file.FileHandler;
import jupiter.common.map.parser.Parser;
import jupiter.common.model.ICloneable;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Characters;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link Table} is a 2D array structure of type {@code T}.
 * <p>
 * @param <T> the type of the elements
 */
public class Table<T>
		implements ICloneable<Table<T>>, Iterable<T[]>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1555648344572603585L;

	/**
	 * The delimiters.
	 */
	public static final char[] DELIMITERS = Characters.toPrimitiveArray('\t', ',', ';');


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The class of the elements.
	 */
	protected final Class<T> c;
	/**
	 * The number of rows.
	 */
	protected int m;
	/**
	 * The number of columns.
	 */
	protected int n;
	/**
	 * The header.
	 */
	protected String[] header;
	/**
	 * The elements.
	 */
	protected T[][] elements;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Table} of type {@code T} of the specified numbers of rows and columns.
	 * <p>
	 * @param c the {@link Class} of type {@code T}
	 * @param m the number of rows
	 * @param n the number of columns
	 */
	public Table(final Class<T> c, final int m, final int n) {
		// Check the arguments
		IntegerArguments.requirePositive(m);
		IntegerArguments.requirePositive(n);

		// Set the attributes
		this.c = c;
		this.m = m;
		this.n = n;
		createHeader(n);
		elements = createArray2D(m, n);
	}

	/**
	 * Constructs a {@link Table} of type {@code T} from the specified elements.
	 * <p>
	 * @param c        the {@link Class} of type {@code T}
	 * @param elements a 2D array of type {@code T}
	 */
	public Table(final Class<T> c, final T[]... elements) {
		// Set the attributes
		this.c = c;
		this.m = elements.length;
		if (this.m > 0) {
			this.n = elements[0].length;
		} else {
			this.n = 0;
		}
		createHeader(n);
		this.elements = elements;
	}

	/**
	 * Constructs a {@link Table} of type {@code T} from the specified header and elements.
	 * <p>
	 * @param c        the {@link Class} of type {@code T}
	 * @param header   an array of {@link String}
	 * @param elements a 2D array of type {@code T}
	 */
	public Table(final Class<T> c, final String[] header, final T[]... elements) {
		// Check the arguments
		if (elements.length > 0) {
			ArrayArguments.requireSameLength(header, elements[0]);
		}

		// Set the attributes
		this.c = c;
		this.m = elements.length;
		this.n = header.length;
		this.header = header;
		this.elements = elements;
	}

	/**
	 * Constructs a {@link Table} of type {@code T} imported from the specified file.
	 * <p>
	 * @param parser    a {@link Parser} of type {@code T}
	 * @param pathname  the pathname of the file to import
	 * @param hasHeader the option specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public Table(final Parser<T> parser, final String pathname, final boolean hasHeader)
			throws IOException {
		// Set the attributes
		this.c = parser.getOutputClass();
		// Load the file
		load(parser, pathname, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Class} of the elements.
	 * <p>
	 * @return the {@link Class} of the elements
	 */
	public Class<T> getElementClass() {
		return c;
	}

	/**
	 * Returns the number of rows.
	 * <p>
	 * @return the number of rows
	 */
	public int getRowCount() {
		return m;
	}

	/**
	 * Returns the number of columns.
	 * <p>
	 * @return the number of columns
	 */
	public int getColumnCount() {
		return n;
	}

	/**
	 * Returns the header.
	 * <p>
	 * @return the header
	 */
	public String[] getHeader() {
		return header;
	}

	/**
	 * Returns the elements.
	 * <p>
	 * @return the elements
	 */
	public T[][] getElements() {
		return elements;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the column with the specified name, or {@code -1} if there is no such
	 * occurrence.
	 * <p>
	 * @param name the column name
	 * <p>
	 * @return the index of the column with the specified name, or {@code -1} if there is no such
	 *         occurrence
	 */
	public int getColumnIndex(final String name) {
		if (header == null) {
			throw new IllegalOperationException("There is no header");
		}
		final int index = Arrays.indexOf(header, name);
		if (index < 0) {
			throw new IllegalArgumentException("There is no column " + Strings.quote(name));
		}
		return index;
	}

	/**
	 * Returns the element at the specified row and column indexes.
	 * <p>
	 * @param i    the row index
	 * @param name the column name
	 * <p>
	 * @return the element at the specified row and column indexes
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T get(final int i, final String name) {
		return get(i, getColumnIndex(name));
	}

	/**
	 * Returns the element at the specified row and column indexes.
	 * <p>
	 * @param i the row index
	 * @param j the column index
	 * <p>
	 * @return the element at the specified row and column indexes
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T get(final int i, final int j) {
		// Check the arguments
		// - i
		IntegerArguments.requireNonNegative(i);
		IntegerArguments.requireLessThan(i, m);
		// - j
		IntegerArguments.requireNonNegative(j);
		IntegerArguments.requireLessThan(j, n);

		// Get the corresponding element
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
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T[] getRow(final int i) {
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
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T[] getRow(final int i, final int from) {
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
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T[] getRow(final int i, final int from, final int length) {
		// Check the arguments
		// - i
		IntegerArguments.requireNonNegative(i);
		IntegerArguments.requireLessThan(i, m);
		// - from
		IntegerArguments.requireNonNegative(from);
		IntegerArguments.requireLessThan(from, n);
		// - length
		IntegerArguments.requirePositive(length);
		IntegerArguments.requireLessOrEqualTo(length, n - from);

		// Get the corresponding row
		final T[] row = createArray(length);
		System.arraycopy(elements[i], from, row, 0, length);
		return row;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the elements of the specified column.
	 * <p>
	 * @param name the column name
	 * <p>
	 * @return the elements of the specified column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T[] getColumn(final String name) {
		return getColumn(name, 0, m);
	}

	/**
	 * Returns the elements of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the elements of the specified column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T[] getColumn(final int j) {
		return getColumn(j, 0, m);
	}

	/**
	 * Returns the elements of the specified column truncated from the specified row index
	 * (inclusive).
	 * <p>
	 * @param name the column name
	 * @param from the initial row index (inclusive)
	 * <p>
	 * @return the elements of the specified column truncated from the specified row index
	 *         (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T[] getColumn(final String name, final int from) {
		return getColumn(name, from, m - from);
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
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T[] getColumn(final int j, final int from) {
		return getColumn(j, from, m - from);
	}

	/**
	 * Returns the elements of the specified column truncated from the specified row index
	 * (inclusive) to the specified length.
	 * <p>
	 * @param name   the column name
	 * @param from   the initial row index (inclusive)
	 * @param length the number of column elements to get
	 * <p>
	 * @return the elements of the specified column truncated from the specified row index
	 *         (inclusive) to the specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T[] getColumn(final String name, final int from, final int length) {
		return getColumn(getColumnIndex(name), from, m - from);
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
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public T[] getColumn(final int j, final int from, final int length) {
		// Check the arguments
		// - j
		IntegerArguments.requireNonNegative(j);
		IntegerArguments.requireLessThan(j, n);
		// - from
		IntegerArguments.requireNonNegative(from);
		IntegerArguments.requireLessThan(from, m);
		// - length
		IntegerArguments.requirePositive(length);
		IntegerArguments.requireLessOrEqualTo(length, m - from);

		// Get the corresponding column
		final T[] column = createArray(length);
		for (int i = 0; i < column.length; ++i) {
			column[i] = elements[from + i][j];
		}
		return column;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the elements in a 2D array.
	 * <p>
	 * @return all the elements in a 2D array
	 */
	public T[][] getAll() {
		final T[][] values = createArray2D(m, n);
		for (int i = 0; i < m; ++i) {
			System.arraycopy(elements[i], 0, values[i], 0, n);
		}
		return values;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the header.
	 * <p>
	 * @param header an array of {@link String}
	 */
	public void setHeader(final String[] header) {
		// Check the arguments
		ArrayArguments.requireLength(header, n);

		// Set the header
		this.header = header;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the element at the specified row and column indexes.
	 * <p>
	 * @param i     the row index
	 * @param j     the column index
	 * @param value a {@code T} object
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void set(final int i, final int j, final T value) {
		// Check the arguments
		// - i
		IntegerArguments.requireNonNegative(i);
		IntegerArguments.requireLessThan(i, m);
		// - j
		IntegerArguments.requireNonNegative(j);
		IntegerArguments.requireLessThan(j, n);

		// Set the corresponding element
		elements[i][j] = value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values an array of type {@code T}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void setRow(final int i, final T[] values) {
		setRow(i, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index (inclusive).
	 * <p>
	 * @param i      the row index
	 * @param values an array of type {@code T}
	 * @param from   the initial column index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void setRow(final int i, final T[] values, final int from) {
		setRow(i, values, from, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index (inclusive) to the
	 * specified length.
	 * <p>
	 * @param i      the row index
	 * @param values an array of type {@code T}
	 * @param from   the initial column index (inclusive)
	 * @param length the number of row elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void setRow(final int i, final T[] values, final int from, final int length) {
		// Check the arguments
		// - i
		IntegerArguments.requireNonNegative(i);
		IntegerArguments.requireLessThan(i, m);
		// - values
		ArrayArguments.requireNonEmpty(values);
		ArrayArguments.requireMinLength(values, length);
		// - from
		IntegerArguments.requireNonNegative(from);
		IntegerArguments.requireLessThan(from, n);
		// - length
		IntegerArguments.requirePositive(length);
		IntegerArguments.requireLessOrEqualTo(length, n - from);

		// Set the corresponding row
		System.arraycopy(values, 0, elements[i], from, length);
	}

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values a {@link Collection} of type {@code T}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void setRow(final int i, final Collection<T> values) {
		setRow(i, values.toArray(createArray(n)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values an array of type {@code T}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void setColumn(final int j, final T[] values) {
		setColumn(j, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index (inclusive).
	 * <p>
	 * @param j      the column index
	 * @param values an array of type {@code T}
	 * @param from   the initial row index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void setColumn(final int j, final T[] values, final int from) {
		setColumn(j, values, from, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index (inclusive) to the
	 * specified length.
	 * <p>
	 * @param j      the column index
	 * @param values an array of type {@code T}
	 * @param from   the initial row index (inclusive)
	 * @param length the number of column elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void setColumn(final int j, final T[] values, final int from, final int length) {
		// Check the arguments
		// - j
		IntegerArguments.requireNonNegative(j);
		IntegerArguments.requireLessThan(j, n);
		// - values
		ArrayArguments.requireNonEmpty(values);
		ArrayArguments.requireMinLength(values, length);
		// - from
		IntegerArguments.requireNonNegative(from);
		IntegerArguments.requireLessThan(from, m);
		// - length
		IntegerArguments.requirePositive(length);
		IntegerArguments.requireLessOrEqualTo(length, m - from);

		// Set the corresponding column
		for (int i = 0; i < length; ++i) {
			elements[i][j] = values[from + i];
		}
	}

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values a {@link Collection} of type {@code T}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void setColumn(final int j, final Collection<T> values) {
		setColumn(j, values.toArray(createArray(m)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values an array of type {@code T}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void setAll(final T... values) {
		for (int i = 0; i < m; ++i) {
			System.arraycopy(values, i * n, elements[i], 0, n);
		}
	}

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values a 2D array of type {@code T}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException {@inheritDoc}
	 */
	public void setAll(final T[]... values) {
		for (int i = 0; i < m; ++i) {
			setRow(i, values[i]);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts {@code this} to an array of type {@code T}.
	 * <p>
	 * @return an array of type {@code T}
	 */
	public T[] toArray() {
		final T[] array = createArray(m * n);
		for (int i = 0; i < m; ++i) {
			System.arraycopy(elements[i], 0, array, i * n, n);
		}
		return array;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates the header.
	 * <p>
	 * @param n the number of header values
	 */
	protected void createHeader(final int n) {
		header = new String[n];
		for (int i = 1; i <= n; ++i) {
			header[i - 1] = Strings.toString(i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of type {@code T} of the specified length.
	 * <p>
	 * @param length the length of the array to create
	 * <p>
	 * @return an array of type {@code T} of the specified length
	 */
	protected T[] createArray(final int length) {
		return Arrays.<T>create(c, length);
	}

	/**
	 * Creates a 2D array of type {@code T} of the specified row and column lengths.
	 * <p>
	 * @param rowCount    the number of rows of the array to create
	 * @param columnCount the number of columns of the array to create
	 * <p>
	 * @return a 2D array of type {@code T} of the specified row and column lengths
	 */
	protected T[][] createArray2D(final int rowCount, final int columnCount) {
		return Arrays.<T>create(c, rowCount, columnCount);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fills {@code this} with the specified value.
	 * <p>
	 * @param value the value to fill with
	 */
	public void fill(final T value) {
		Arrays.<T>fill(elements, value);
	}

	/**
	 * Resets {@code this}.
	 */
	public void reset() {
		elements = createArray2D(m, n);
	}

	/**
	 * Resizes the rows and columns.
	 */
	public void resize() {
		resizeRows();
		resizeColumns();
	}

	/**
	 * Removes the empty leading rows.
	 */
	public void resizeRows() {
		int i = m - 1;
		while (i > 0 && Arrays.<T>isEmpty(elements[i])) {
			--i;
		}
		resize(i + 1, n);
	}

	/**
	 * Removes the empty leading columns.
	 */
	public void resizeColumns() {
		final int j = n - 1;
		boolean isEmpty = true;
		while (j > 0 && isEmpty) {
			for (int i = 0; i < m && isEmpty; ++i) {
				if (elements[i][j] != null) {
					isEmpty = false;
				}
			}
		}
		resize(m, j + 1);
	}

	/**
	 * Resizes the rows and columns to the specified lengths.
	 * <p>
	 * @param rowCount    the number of rows to resize to
	 * @param columnCount the number of columns to resize to
	 */
	public void resize(final int rowCount, final int columnCount) {
		// Check the arguments
		IntegerArguments.requirePositive(rowCount);
		IntegerArguments.requirePositive(columnCount);

		// Test whether the row or column length is different
		if (m != rowCount || n != columnCount) {
			// Initialize
			final int minRowCount = Math.min(rowCount, m);
			final int minColumnCount = Math.min(columnCount, n);

			// Resize the header
			if (n != columnCount) {
				final String[] resizedHeader = new String[columnCount];
				System.arraycopy(header, 0, resizedHeader, 0, minColumnCount);
				header = resizedHeader;
			}

			// Resize the table
			final T[][] resizedTable = createArray2D(rowCount, columnCount);
			for (int i = 0; i < minRowCount; ++i) {
				System.arraycopy(elements[i], 0, resizedTable[i], 0, minColumnCount);
			}
			elements = resizedTable;
			m = rowCount;
			n = columnCount;
		}
	}

	/**
	 * Shifts the rows by the specified offset.
	 * <p>
	 * @param offset the offset to shift the rows by
	 */
	public void shiftRows(final int offset) {
		shift(offset, 0);
	}

	/**
	 * Shifts the columns by the specified offset.
	 * <p>
	 * @param offset the offset to shift the columns by
	 */
	public void shiftColumns(final int offset) {
		shift(0, offset);
	}

	/**
	 * Shifts the rows and columns by the specified offsets.
	 * <p>
	 * @param rowOffset    the offset to shift the rows by
	 * @param columnOffset the offset to shift the columns by
	 */
	public void shift(final int rowOffset, final int columnOffset) {
		// Test whether the row or column offset is non-zero
		if (rowOffset != 0 || columnOffset != 0) {
			// Initialize
			final int rowCount = rowOffset + m;
			final int columnCount = columnOffset + n;
			if (rowCount <= 0 || columnCount <= 0) {
				throw new IllegalOperationException("The table cannot be empty");
			}

			// Shift the header
			if (n != columnCount) {
				final String[] shiftedHeader = new String[columnCount];
				if (columnOffset < 0) {
					System.arraycopy(header, -columnOffset, shiftedHeader, 0, columnCount);
				} else {
					System.arraycopy(header, 0, shiftedHeader, columnOffset, n);
				}
				header = shiftedHeader;
			}

			// Shift the table
			final T[][] shiftedTable = createArray2D(rowCount, columnCount);
			for (int i = 0; i < rowCount; ++i) {
				if (i < rowOffset) {
					Arrays.<T>fill(elements[i], null);
				} else {
					final T[] source;
					if (rowOffset < 0) {
						source = elements[i - rowOffset];
					} else {
						source = elements[i];
					}
					if (columnOffset < 0) {
						System.arraycopy(source, -columnOffset, shiftedTable[i], 0, columnCount);
					} else {
						System.arraycopy(source, 0, shiftedTable[i], columnOffset, n);
					}
				}
			}
			elements = shiftedTable;
			m = rowCount;
			n = columnCount;
		}
	}

	/**
	 * Transposes {@code this}.
	 */
	public void transpose() {
		elements = Arrays.<T>transpose(c, elements);
		n = m;
		m = elements.length;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS & EXPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads the values from the specified file.
	 * <p>
	 * @param parser    a {@link Parser} of type {@code T}
	 * @param pathname  the pathname of the file to load
	 * @param hasHeader the option specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public void load(final Parser<T> parser, final String pathname, final boolean hasHeader)
			throws IOException {
		final FileHandler fileHandler = new FileHandler(pathname);
		load(parser, fileHandler.getReader(), fileHandler.countLines(true) - (hasHeader ? 1 : 0),
				hasHeader);
	}

	/**
	 * Loads the values of the specified row length from the specified reader.
	 * <p>
	 * @param parser    a {@link Parser} of type {@code T}
	 * @param reader    a {@link BufferedReader}
	 * @param rowCount  the number of lines to load
	 * @param hasHeader the option specifying whether the reader has a header
	 * <p>
	 * @throws IOException if there is a problem with reading
	 */
	public void load(final Parser<T> parser, final BufferedReader reader, final int rowCount,
			final boolean hasHeader)
			throws ClassCastException, IOException {
		m = rowCount;
		n = 0;
		// Parse the file
		String line;
		if ((line = reader.readLine()) != null) {
			// Find the delimiter (take the first one in the list in case of different delimiters)
			String delimiter = null;
			for (final char d : DELIMITERS) {
				final int nOccurrences = Strings.getAllIndexes(line, d).size();
				if (nOccurrences > 0) {
					if (n == 0) {
						delimiter = Strings.toString(d);
						n = nOccurrences;
					} else {
						IO.warn("The file contains different delimiters; ",
								Strings.quote(delimiter), " is selected");
						break;
					}
				}
			}
			++n;
			// Reset the table
			reset();
			// Scan the file line by line
			int i = 0;
			String[] values = line.split(delimiter);
			if (hasHeader) {
				header = values;
			} else {
				setRow(i, parser.parseToArray(values));
				++i;
			}
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
					setRow(i, parser.parseToArray(values));
					++i;
				}
			}
			// Resize if there are any empty rows or columns
			resize();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean export(final String pathname) {
		final FileHandler fileHandler = new FileHandler(pathname);
		fileHandler.delete();
		for (int i = 0; i < m; ++i) {
			if (!fileHandler.appendLine(Strings.joinWith(getRow(i), DELIMITERS[0]))) {
				fileHandler.closeWriter();
				return false;
			}
		}
		fileHandler.closeWriter();
		return true;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ITERABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link Iterator} over the rows of {@code this}.
	 * <p>
	 * @return an {@link Iterator} over the rows of {@code this}
	 */
	public Iterator<T[]> iterator() {
		return new TableIterator();
	}

	/**
	 * An {@link Iterator} over the rows of {@code this}.
	 */
	protected class TableIterator
			implements Iterator<T[]> {

		// The index of the next row
		protected int cursor = 0;

		public boolean hasNext() {
			return cursor != m;
		}

		public T[] next() {
			final int i = cursor;
			if (i >= m) {
				throw new NoSuchElementException();
			}
			cursor = i + 1;
			return elements[i];
		}

		@Override
		public void remove() {
			if (cursor > 0) {
				// Verify the feasibility
				if (m == 1) {
					throw new IllegalOperationException("The table cannot be empty");
				}

				// Remove the element pointed by the cursor
				final T[][] resizedTable = createArray2D(m - 1, n);
				final int rowIndex = cursor - 1;
				for (int i = 0; i < m; ++i) {
					if (i < rowIndex) {
						for (int j = 0; j < n; ++j) {
							resizedTable[i][j] = elements[i][j];
						}
					} else if (i > rowIndex) {
						for (int j = 0; j < n; ++j) {
							resizedTable[i - 1][j] = elements[i][j];
						}
					}
				}
				elements = resizedTable;
				--m;
				--cursor;
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Table<T> clone() {
		return new Table<T>(c, elements);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Table)) {
			return false;
		}
		final Table<?> otherTable = (Table<?>) other;
		if (!Objects.equals(c, otherTable.getElementClass()) || m != otherTable.getRowCount() ||
				n != otherTable.getColumnCount()) {
			return false;
		}
		final String[] otherHeader = otherTable.getHeader();
		for (int j = 0; j < n; ++j) {
			if (!Objects.equals(header[j], otherHeader[j])) {
				return false;
			}
		}
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (!Objects.equals(elements[i][j], otherTable.get(i, j))) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, elements);
	}

	@Override
	public String toString() {
		final StringBuilder builder = Strings.createBuilder(10 * m * n);
		for (int i = 0; i < m; ++i) {
			builder.append(Strings.joinWith(getRow(i), DELIMITERS[0])).append("\n");
		}
		return builder.toString();
	}
}
