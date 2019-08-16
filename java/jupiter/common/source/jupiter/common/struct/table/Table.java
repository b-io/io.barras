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
package jupiter.common.struct.table;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Formats.NEWLINE;
import static jupiter.common.util.Strings.EMPTY;
import static jupiter.common.util.Strings.SPACE;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.io.file.FileHandler;
import jupiter.common.map.parser.IParser;
import jupiter.common.model.ICloneable;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link Table} is a wrapper around a 2D {@code T} array.
 * <p>
 * @param <T> the component type of the 2D {@code T} array
 */
public class Table<T>
		implements ICloneable<Table<T>>, Iterable<T[]>, Serializable {

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
	public static final char[] COLUMN_DELIMITERS = new char[] {'\t', ',', ';'};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The class of the elements.
	 */
	protected Class<T> c;
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
	 * Constructs a {@link Table} of type {@code T} with the specified numbers of rows and columns.
	 * <p>
	 * @param c           the {@link Class} of type {@code T}
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public Table(final Class<T> c, final int rowCount, final int columnCount) {
		// Check the arguments
		IntegerArguments.requirePositive(rowCount);
		IntegerArguments.requirePositive(columnCount);

		// Set the attributes
		this.c = c;
		m = rowCount;
		n = columnCount;
		createHeader(columnCount);
		elements = createArray2D(rowCount, columnCount);
	}

	/**
	 * Constructs a {@link Table} of type {@code T} with the specified header and numbers of rows
	 * and columns.
	 * <p>
	 * @param c           the {@link Class} of type {@code T}
	 * @param header      an array of {@link String}
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public Table(final Class<T> c, final String[] header, final int rowCount,
			final int columnCount) {
		// Check the arguments
		IntegerArguments.requirePositive(rowCount);
		IntegerArguments.requirePositive(columnCount);

		// Set the attributes
		this.c = c;
		m = rowCount;
		n = columnCount;
		this.header = header;
		elements = createArray2D(rowCount, columnCount);
	}

	/**
	 * Constructs a {@link Table} of type {@code T} with the specified elements.
	 * <p>
	 * @param c        the {@link Class} of type {@code T}
	 * @param elements a 2D {@code T} array
	 */
	public Table(final Class<T> c, final T[][] elements) {
		// Set the attributes
		this.c = c;
		m = elements.length;
		if (m > 0) {
			n = elements[0].length;
		} else {
			n = 0;
		}
		createHeader(n);
		this.elements = elements;
	}

	/**
	 * Constructs a {@link Table} of type {@code T} with the specified header and elements.
	 * <p>
	 * @param c        the {@link Class} of type {@code T}
	 * @param header   an array of {@link String}
	 * @param elements a 2D {@code T} array
	 */
	public Table(final Class<T> c, final String[] header, final T[][] elements) {
		// Check the arguments
		if (elements.length > 0) {
			ArrayArguments.requireSameLength(header, elements[0]);
		}

		// Set the attributes
		this.c = c;
		m = elements.length;
		n = header.length;
		this.header = header;
		this.elements = elements;
	}

	/**
	 * Constructs a {@link Table} of type {@code T} loaded from the specified file.
	 * <p>
	 * @param parser    an {@link IParser} of type {@code T}
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the specified file
	 */
	public Table(final IParser<T> parser, final String path, final boolean hasHeader)
			throws IOException {
		// Set the attributes
		this.c = parser.getOutputClass();
		// Load the file
		load(parser, path, hasHeader);
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
	 * Returns the index of the specified column, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param name the column name
	 * <p>
	 * @return the index of the specified column, or {@code -1} if there is no such occurrence
	 * <p>
	 * @throws IllegalArgumentException  if {@code name} is not present
	 * @throws IllegalOperationException if there is no header
	 */
	public int getColumnIndex(final String name) {
		// Verify the feasibility
		if (header == null) {
			throw new IllegalOperationException("There is no header");
		}

		// Get the column index
		final int index = Arrays.<String>indexOf(header, name);
		if (index < 0) {
			throw new IllegalArgumentException("There is no column " + Strings.quote(name));
		}
		return index;
	}

	/**
	 * Returns the name of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the name of the specified column
	 */
	public String getColumnName(final int j) {
		// Verify the feasibility
		if (header == null) {
			throw new IllegalOperationException("There is no header");
		}

		// Check the arguments
		IntegerArguments.requireNonNegative(j);
		IntegerArguments.requireLessThan(j, n);

		// Get the column name
		return header[j];
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element at the specified row and column indexes.
	 * <p>
	 * @param i    the row index
	 * @param name the column name
	 * <p>
	 * @return the element at the specified row and column indexes
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 * @throws IllegalArgumentException       if {@code name} is not present
	 * @throws IllegalOperationException      if there is no header
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
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code from} is out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code from} is out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if {@code from} is out of bounds
	 * @throws IllegalArgumentException       if {@code name} is not present
	 * @throws IllegalOperationException      if there is no header
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
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code from} is out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if {@code from} is out of bounds
	 * @throws IllegalArgumentException       if {@code name} is not present
	 * @throws IllegalOperationException      if there is no header
	 */
	public T[] getColumn(final String name, final int from, final int length) {
		return getColumn(getColumnIndex(name), from, length);
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
	 * Returns all the elements in a 2D {@code T} array.
	 * <p>
	 * @return all the elements in a 2D {@code T} array
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
	 * @param value a value {@code T}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out of bounds
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
	 * @param values a {@code T} array
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void setRow(final int i, final T[] values) {
		setRow(i, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index (inclusive).
	 * <p>
	 * @param i      the row index
	 * @param values a {@code T} array
	 * @param from   the initial column index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code from} is out of bounds
	 */
	public void setRow(final int i, final T[] values, final int from) {
		setRow(i, values, from, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index (inclusive) to the
	 * specified length.
	 * <p>
	 * @param i      the row index
	 * @param values a {@code T} array
	 * @param from   the initial column index (inclusive)
	 * @param length the number of row elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code from} is out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void setRow(final int i, final Collection<T> values) {
		setRow(i, values.toArray(createArray(n)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values a {@code T} array
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public void setColumn(final int j, final T[] values) {
		setColumn(j, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index (inclusive).
	 * <p>
	 * @param j      the column index
	 * @param values a {@code T} array
	 * @param from   the initial row index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code from} is out of bounds
	 */
	public void setColumn(final int j, final T[] values, final int from) {
		setColumn(j, values, from, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index (inclusive) to the
	 * specified length.
	 * <p>
	 * @param j      the column index
	 * @param values a {@code T} array
	 * @param from   the initial row index (inclusive)
	 * @param length the number of column elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code from} is out of bounds
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
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public void setColumn(final int j, final Collection<T> values) {
		setColumn(j, values.toArray(createArray(m)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values a {@code T} array
	 * <p>
	 * @throws IndexOutOfBoundsException if the specified array is not of the same length
	 */
	public void setAll(final T[] values) {
		for (int i = 0; i < m; ++i) {
			System.arraycopy(values, i * n, elements[i], 0, n);
		}
	}

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values a 2D {@code T} array
	 * <p>
	 * @throws IndexOutOfBoundsException if the specified array is not of the same length
	 */
	public void setAll(final T[][] values) {
		for (int i = 0; i < m; ++i) {
			setRow(i, values[i]);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts {@code this} to a {@code T} array.
	 * <p>
	 * @return a {@code T} array
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
	 * Creates a header of the specified length.
	 * <p>
	 * @param length the length of the header
	 */
	protected void createHeader(final int length) {
		header = new String[length];
		for (int i = 1; i <= length; ++i) {
			header[i - 1] = Strings.toString(i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@code T} array of the specified length.
	 * <p>
	 * @param length the length of the array to create
	 * <p>
	 * @return a {@code T} array of the specified length
	 */
	protected T[] createArray(final int length) {
		return Arrays.<T>create(c, length);
	}

	/**
	 * Creates a 2D {@code T} array of the specified row and column lengths.
	 * <p>
	 * @param rowCount    the number of rows of the array to create
	 * @param columnCount the number of columns of the array to create
	 * <p>
	 * @return a 2D {@code T} array of the specified row and column lengths
	 */
	protected T[][] createArray2D(final int rowCount, final int columnCount) {
		return Arrays.<T>create(c, rowCount, columnCount);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fills {@code this} with the specified value {@code T}.
	 * <p>
	 * @param value the value {@code T} to fill with
	 */
	public void fill(final T value) {
		Arrays.<T>fill(elements, value);
	}

	/**
	 * Merges with the specified {@link Table} of type {@code T}.
	 * <p>
	 * @param table the {@link Table} of type {@code T} to merge with
	 */
	public void merge(final Table<T> table) {
		merge(table, true);
	}

	/**
	 * Merges with the specified {@link Table} of type {@code T}.
	 * <p>
	 * @param table     the {@link Table} of type {@code T} to merge with
	 * @param mergeRows the flag specifying whether to merge the rows or the columns
	 */
	public void merge(final Table<T> table, final boolean mergeRows) {
		if (mergeRows) {
			// Initialize
			final int rowOffset = m;

			// Resize the table
			resize(rowOffset + table.m, n);

			// Merge the rows
			for (int i = 0; i < table.m; ++i) {
				setRow(rowOffset + i, table.getRow(i));
			}
		} else {
			// Initialize
			final int columnOffset = n;

			// Resize the table
			resize(m, columnOffset + table.n);

			// Merge the header
			System.arraycopy(table.header, 0, header, columnOffset, table.n);

			// Merge the columns
			resize(m, columnOffset + table.n);
			for (int j = 0; j < table.n; ++j) {
				setColumn(columnOffset + j, table.getColumn(j));
			}
		}
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

			// Verify the feasibility
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
		createHeader(m);
		elements = Arrays.<T>transpose(c, elements);
		n = m;
		m = elements.length;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads {@code this} from the specified file.
	 * <p>
	 * @param parser    the {@link IParser} of type {@code T} of the file to load
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the specified file
	 */
	public void load(final IParser<T> parser, final String path, final boolean hasHeader)
			throws IOException {
		final FileHandler fileHandler = new FileHandler(path);
		load(parser, fileHandler.getReader(), fileHandler.countLines(true) - (hasHeader ? 1 : 0),
				hasHeader);
	}

	/**
	 * Loads {@code this} from the specified {@link BufferedReader}.
	 * <p>
	 * @param parser    the {@link IParser} of type {@code T} of the lines to load
	 * @param reader    the {@link BufferedReader} of the lines to load
	 * @param rowCount  the number of lines to load
	 * @param hasHeader the flag specifying whether the first line is a header
	 * <p>
	 * @throws IOException if there is a problem with reading
	 */
	public void load(final IParser<T> parser, final BufferedReader reader, final int rowCount,
			final boolean hasHeader)
			throws ClassCastException, IOException {
		m = rowCount;
		n = 0;
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
			// Reset the table
			reset();
			// Scan the file line by line
			int i = 0;
			String[] values = delimiter == null ? new String[] {line} : line.split(delimiter);
			if (hasHeader) {
				header = values;
			} else {
				setRow(i, parser.parseToArray(values));
				++i;
			}
			while ((line = reader.readLine()) != null) {
				values = delimiter == null ? new String[] {line} : line.split(delimiter);
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
					setRow(i, parser.parseToArray(values));
					++i;
				}
			}
			// Resize if there are any empty rows or columns
			resize();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// EXPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Saves {@code this} to the specified file.
	 * <p>
	 * @param path       the path to the file to save to
	 * @param saveHeader the flag specifying whether to save the header
	 * <p>
	 * @return {@code true} if {@code this} is saved to the specified file, {@code false} otherwise
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code path}
	 */
	public boolean save(final String path, final boolean saveHeader)
			throws FileNotFoundException {
		final FileHandler fileHandler = new FileHandler(path);
		fileHandler.initWriter(false);
		// Export the header
		if (saveHeader &&
				!fileHandler.writeLine(Strings.joinWith(getHeader(), COLUMN_DELIMITERS[0]))) {
			fileHandler.closeWriter();
			return false;
		}
		// Export the elements
		for (int i = 0; i < m; ++i) {
			if (!fileHandler.writeLine(Strings.joinWith(getRow(i), COLUMN_DELIMITERS[0]))) {
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

		/**
		 * The index of the next row.
		 */
		protected int cursor = 0;

		/**
		 * Tests whether {@code this} has next.
		 * <p>
		 * @return {@code true} if {@code this} has next, {@code false} otherwise
		 */
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

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Table<T> clone() {
		try {
			final Table<T> clone = (Table<T>) super.clone();
			clone.c = Objects.clone(c);
			clone.header = Objects.clone(header);
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
	@SuppressWarnings("unchecked")
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Table)) {
			return false;
		}
		final Table<?> otherTable = (Table<?>) other;
		if (!Objects.equals(c, otherTable.c) || m != otherTable.m || n != otherTable.n) {
			return false;
		}
		for (int j = 0; j < n; ++j) {
			if (!Objects.equals(header[j], otherTable.header[j])) {
				return false;
			}
		}
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (!Objects.equals(elements[i][j], otherTable.elements[i][j])) {
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
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, c, m, n, header, elements);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = Strings.createBuilder(10 * m * n);
		for (int i = 0; i < m; ++i) {
			builder.append(Strings.joinWith(getRow(i), COLUMN_DELIMITERS[0])).append(NEWLINE);
		}
		return builder.toString();
	}
}
