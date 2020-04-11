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
package jupiter.common.struct.table;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BAR;
import static jupiter.common.util.Characters.DOUBLE_QUOTE;
import static jupiter.common.util.Characters.SINGLE_QUOTE;
import static jupiter.common.util.Characters.SPACE;
import static jupiter.common.util.Formats.NEW_LINE;
import static jupiter.common.util.Strings.INITIAL_CAPACITY;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.io.Resources;
import jupiter.common.io.file.FileHandler;
import jupiter.common.map.parser.IParser;
import jupiter.common.map.replacer.StringReplacer;
import jupiter.common.model.ICloneable;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Classes;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link Table} is a wrapper around a 2D {@code E} array.
 * <p>
 * @param <E> the element type of the {@link Table}
 */
public class Table<E>
		implements ICloneable<Table<E>>, Iterable<E[]>, ITable, Serializable {

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
	public static final char[] COLUMN_DELIMITERS = new char[] {'\t', ',', ';'};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link Class} of {@code E} element type.
	 */
	protected Class<E> c;
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
	protected E[][] elements;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link Table} by default.
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public Table() {
		this((Class<E>) Object.class);
	}

	/**
	 * Constructs an empty {@link Table} of {@code E} element type.
	 * <p>
	 * @param c the {@link Class} of {@code E} element type
	 */
	public Table(final Class<E> c) {
		this(c, 0, 0);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Table} of {@code E} element type with the specified numbers of rows and
	 * columns.
	 * <p>
	 * @param c           the {@link Class} of {@code E} element type
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public Table(final Class<E> c, final int rowCount, final int columnCount) {
		this(c, createHeader(columnCount), rowCount, columnCount);
	}

	/**
	 * Constructs a {@link Table} of {@code E} element type with the specified header and numbers of
	 * rows and columns.
	 * <p>
	 * @param c           the {@link Class} of {@code E} element type
	 * @param header      an array of {@link String} (may be {@code null})
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public Table(final Class<E> c, final String[] header, final int rowCount,
			final int columnCount) {
		// Check the arguments
		Arguments.requireNonNull(c, "class");
		if (header != null) {
			ArrayArguments.requireLength(header, columnCount);
		}
		IntegerArguments.requireNonNegative(rowCount);
		IntegerArguments.requireNonNegative(columnCount);

		// Set the attributes
		this.c = c;
		m = rowCount;
		n = columnCount;
		this.header = header;
		elements = createArray2D(rowCount, columnCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Table} of {@code E} element type with the specified elements.
	 * <p>
	 * @param c        the {@link Class} of {@code E} element type
	 * @param elements a 2D {@code E} array
	 */
	public Table(final Class<E> c, final E[][] elements) {
		// Check the arguments
		Arguments.requireNonNull(c, "class");
		ArrayArguments.requireNonEmpty(elements, "2D array of elements");

		// Set the attributes
		this.c = c;
		m = elements.length;
		if (m > 0) {
			n = elements[0].length;
		} else {
			n = 0;
		}
		header = createHeader(n);
		this.elements = elements;
	}

	/**
	 * Constructs a {@link Table} of {@code E} element type with the specified header and elements.
	 * <p>
	 * @param c        the {@link Class} of {@code E} element type
	 * @param header   an array of {@link String}
	 * @param elements a 2D {@code E} array
	 */
	public Table(final Class<E> c, final String[] header, final E[][] elements) {
		// Check the arguments
		Arguments.requireNonNull(c, "class");
		ArrayArguments.requireSameLength(ArrayArguments.requireNonEmpty(header, "header"),
				ArrayArguments.requireNonEmpty(elements, "elements"));

		// Set the attributes
		this.c = c;
		m = elements.length;
		n = header.length;
		this.header = header;
		this.elements = elements;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Table} of {@code E} element type loaded from the file denoted by the
	 * specified path.
	 * <p>
	 * @param parser    an {@link IParser} of {@code E} element type
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public Table(final IParser<E> parser, final String path, final boolean hasHeader)
			throws IOException {
		// Check the arguments
		Arguments.requireNonNull(parser, "parser");

		// Set the attributes
		c = parser.getOutputClass();

		// Load the file
		load(parser, path, hasHeader);
	}

	/**
	 * Constructs a {@link Table} of {@code E} element type with the specified header loaded from
	 * the file denoted by the specified path.
	 * <p>
	 * @param header    an array of {@link String}
	 * @param parser    an {@link IParser} of {@code E} element type
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public Table(final String[] header, final IParser<E> parser, final String path,
			final boolean hasHeader)
			throws IOException {
		// Check the arguments
		ArrayArguments.requireNonEmpty(header, "header");
		Arguments.requireNonNull(parser, "parser");

		// Set the attributes
		c = parser.getOutputClass();

		// Load the file
		load(parser, path, hasHeader);
		this.header = header;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element {@link Class}.
	 * <p>
	 * @return the element {@link Class}
	 */
	public Class<E> getElementClass() {
		return c;
	}

	/**
	 * Returns the element {@link Class} of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the element {@link Class} of the specified column
	 */
	public Class<?> getColumnClass(final int j) {
		// Check the arguments
		ArrayArguments.requireIndex(j, n);

		// Get the corresponding column class (common ancestor of the column element classes)
		Class<?> columnClass = c;
		for (int i = 0; i < m; ++i) {
			columnClass = Classes.getCommonAncestor(columnClass, Classes.get(elements[i][j]));
		}
		return columnClass;
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
	public E[][] getElements() {
		return elements;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the specified column, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param name the column name (may be {@code null})
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
		final int j = Arrays.<String>findFirstIndex(header, name);
		if (j < 0) {
			throw new IllegalArgumentException(
					Strings.join("There is no column ", Strings.quote(name)));
		}
		return j;
	}

	/**
	 * Returns the name of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the name of the specified column
	 * <p>
	 * @throws IllegalOperationException if there is no header
	 */
	public String getColumnName(final int j) {
		// Verify the feasibility
		if (header == null) {
			throw new IllegalOperationException("There is no header");
		}

		// Check the arguments
		ArrayArguments.requireIndex(j, n);

		// Get the column name
		return header[j];
	}

	//////////////////////////////////////////////

	/**
	 * Returns the element at the specified row and column indices.
	 * <p>
	 * @param i    the row index
	 * @param name the column name
	 * <p>
	 * @return the element at the specified row and column indices
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 * @throws IllegalArgumentException       if {@code name} is not present
	 * @throws IllegalOperationException      if there is no header
	 */
	public E get(final int i, final String name) {
		return get(i, getColumnIndex(name));
	}

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
	public E get(final int i, final int j) {
		// Check the arguments
		// • i
		ArrayArguments.requireIndex(i, m);
		// • j
		ArrayArguments.requireIndex(j, n);

		// Get the corresponding element
		return elements[i][j];
	}

	//////////////////////////////////////////////

	/**
	 * Returns the elements of the specified row.
	 * <p>
	 * @param i the row index
	 * <p>
	 * @return the elements of the specified row
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public E[] getRow(final int i) {
		return getRow(i, 0, n);
	}

	/**
	 * Returns the elements of the specified row truncated from the specified column index.
	 * <p>
	 * @param i          the row index
	 * @param fromColumn the initial column index (inclusive)
	 * <p>
	 * @return the elements of the specified row truncated from the specified column index
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code fromColumn} is out of bounds
	 */
	public E[] getRow(final int i, final int fromColumn) {
		return getRow(i, fromColumn, n - fromColumn);
	}

	/**
	 * Returns the elements of the specified row truncated from the specified column index to the
	 * specified length.
	 * <p>
	 * @param i          the row index
	 * @param fromColumn the initial column index (inclusive)
	 * @param length     the number of row elements to get
	 * <p>
	 * @return the elements of the specified row truncated from the specified column index to the
	 *         specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code fromColumn} is out of bounds
	 */
	public E[] getRow(final int i, final int fromColumn, final int length) {
		// Check the arguments
		// • i
		ArrayArguments.requireIndex(i, m);
		// • from
		ArrayArguments.requireIndex(fromColumn, n);
		// • length
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final int l = Math.min(length, n - fromColumn);

		// Get the corresponding row
		final E[] row = createArray(l);
		System.arraycopy(elements[i], fromColumn, row, 0, l);
		return row;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the elements of the specified column.
	 * <p>
	 * @param name the column name
	 * <p>
	 * @return the elements of the specified column
	 * <p>
	 * @throws IllegalOperationException if there is no header
	 */
	public E[] getColumn(final String name) {
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
	public E[] getColumn(final int j) {
		return getColumn(j, 0, m);
	}

	/**
	 * Returns the elements of the specified column truncated from the specified row index.
	 * <p>
	 * @param name    the column name
	 * @param fromRow the initial row index (inclusive)
	 * <p>
	 * @return the elements of the specified column truncated from the specified row index
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code fromRow} is out of bounds
	 * @throws IllegalArgumentException       if {@code name} is not present
	 * @throws IllegalOperationException      if there is no header
	 */
	public E[] getColumn(final String name, final int fromRow) {
		return getColumn(name, fromRow, m - fromRow);
	}

	/**
	 * Returns the elements of the specified column truncated from the specified row index.
	 * <p>
	 * @param j       the column index
	 * @param fromRow the initial row index (inclusive)
	 * <p>
	 * @return the elements of the specified column truncated from the specified row index
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code fromRow} is out of bounds
	 */
	public E[] getColumn(final int j, final int fromRow) {
		return getColumn(j, fromRow, m - fromRow);
	}

	/**
	 * Returns the elements of the specified column truncated from the specified row index to the
	 * specified length.
	 * <p>
	 * @param name    the column name
	 * @param fromRow the initial row index (inclusive)
	 * @param length  the number of column elements to get
	 * <p>
	 * @return the elements of the specified column truncated from the specified row index to the
	 *         specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code fromRow} is out of bounds
	 * @throws IllegalArgumentException       if {@code name} is not present
	 * @throws IllegalOperationException      if there is no header
	 */
	public E[] getColumn(final String name, final int fromRow, final int length) {
		return getColumn(getColumnIndex(name), fromRow, length);
	}

	/**
	 * Returns the elements of the specified column truncated from the specified row index to the
	 * specified length.
	 * <p>
	 * @param j       the column index
	 * @param fromRow the initial row index (inclusive)
	 * @param length  the number of column elements to get
	 * <p>
	 * @return the elements of the specified column truncated from the specified row index to the
	 *         specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code fromRow} is out of bounds
	 */
	public E[] getColumn(final int j, final int fromRow, final int length) {
		// Check the arguments
		// • j
		ArrayArguments.requireIndex(j, n);
		// • from
		ArrayArguments.requireIndex(fromRow, m);
		// • length
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final int l = Math.min(length, m - fromRow);

		// Get the corresponding column
		final E[] column = createArray(j, l);
		for (int i = 0; i < l; ++i) {
			column[i] = elements[fromRow + i][j];
		}
		return column;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the header.
	 * <p>
	 * @param header an array of {@link String}
	 */
	public void setHeader(final String... header) {
		// Check the arguments
		ArrayArguments.requireLength(header, n);

		// Set the header
		this.header = header;
	}

	//////////////////////////////////////////////

	/**
	 * Sets the element at the specified row and column indices.
	 * <p>
	 * @param i     the row index
	 * @param j     the column index
	 * @param value an {@code E} value (may be {@code null})
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out of bounds
	 */
	public void set(final int i, final int j, final E value) {
		// Check the arguments
		// • i
		ArrayArguments.requireIndex(i, m);
		// • j
		ArrayArguments.requireIndex(j, n);

		// Set the corresponding element
		elements[i][j] = value;
	}

	//////////////////////////////////////////////

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values an {@code E} array
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void setRow(final int i, final E[] values) {
		setRow(i, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index.
	 * <p>
	 * @param i          the row index
	 * @param values     an {@code E} array
	 * @param fromColumn the initial column index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code fromColumn} is out of bounds
	 */
	public void setRow(final int i, final E[] values, final int fromColumn) {
		setRow(i, values, fromColumn, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index to the specified
	 * length.
	 * <p>
	 * @param i          the row index
	 * @param values     an {@code E} array
	 * @param fromColumn the initial column index (inclusive)
	 * @param length     the number of row elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code fromColumn} is out of bounds
	 */
	public void setRow(final int i, final E[] values, final int fromColumn, final int length) {
		// Check the arguments
		// • i
		ArrayArguments.requireIndex(i, m);
		// • values
		ArrayArguments.requireNonEmpty(values, "values");
		ArrayArguments.requireMinLength(values, length);
		// • from
		ArrayArguments.requireIndex(fromColumn, n);
		// • length
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final int l = Math.min(length, n - fromColumn);

		// Set the corresponding row
		System.arraycopy(values, 0, elements[i], fromColumn, l);
	}

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values a {@link Collection} of {@code E} element subtype
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void setRow(final int i, final Collection<? extends E> values) {
		setRow(i, values.toArray(createArray(n)));
	}

	//////////////////////////////////////////////

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values an {@code E} array
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public void setColumn(final int j, final E[] values) {
		setColumn(j, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index.
	 * <p>
	 * @param j       the column index
	 * @param values  an {@code E} array
	 * @param fromRow the initial row index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code fromRow} is out of bounds
	 */
	public void setColumn(final int j, final E[] values, final int fromRow) {
		setColumn(j, values, fromRow, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index to the specified
	 * length.
	 * <p>
	 * @param j       the column index
	 * @param values  an {@code E} array
	 * @param fromRow the initial row index (inclusive)
	 * @param length  the number of column elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code fromRow} is out of bounds
	 */
	public void setColumn(final int j, final E[] values, final int fromRow, final int length) {
		// Check the arguments
		// • j
		ArrayArguments.requireIndex(j, n);
		// • values
		ArrayArguments.requireNonEmpty(values, "values");
		ArrayArguments.requireMinLength(values, length);
		// • from
		ArrayArguments.requireIndex(fromRow, m);
		// • length
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final int l = Math.min(length, m - fromRow);

		// Set the corresponding column
		for (int i = 0; i < l; ++i) {
			elements[i][j] = values[fromRow + i];
		}
	}

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values a {@link Collection} of {@code E} element subtype
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public void setColumn(final int j, final Collection<? extends E> values) {
		setColumn(j, values.toArray(createArray(m)));
	}

	//////////////////////////////////////////////

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values an {@code E} array
	 * <p>
	 * @throws IndexOutOfBoundsException if {@code values} is not of the same length as {@code this}
	 */
	public void setAll(final E[] values) {
		for (int i = 0; i < m; ++i) {
			System.arraycopy(values, i * n, elements[i], 0, n);
		}
	}

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values a 2D {@code E} array
	 * <p>
	 * @throws IndexOutOfBoundsException if {@code values} is not of the same length as {@code this}
	 */
	public void setAll(final E[][] values) {
		for (int i = 0; i < m; ++i) {
			setRow(i, values[i]);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clears {@code this}.
	 */
	public void clear() {
		elements = createArray2D(m, n);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts {@code this} to an {@code E} array.
	 * <p>
	 * @return an {@code E} array
	 */
	public E[] toArray() {
		return Arrays.<E>toArray(c, elements);
	}

	/**
	 * Converts {@code this} to a 2D {@code E} array.
	 * <p>
	 * @return a 2D {@code E} array
	 */
	public E[][] toArray2D() {
		return Arrays.<E>toArray2D(c, elements);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an {@code E} array of the specified length.
	 * <p>
	 * @param length the length of the array to create
	 * <p>
	 * @return an {@code E} array of the specified length
	 */
	protected E[] createArray(final int length) {
		return Arrays.<E>create(c, length);
	}

	/**
	 * Creates an array of the element {@link Class} of the specified column of the specified
	 * length.
	 * <p>
	 * @param j      the column index
	 * @param length the length of the array to create
	 * <p>
	 * @return an array of the element {@link Class} of the specified column of the specified length
	 */
	@SuppressWarnings({"cast", "unchecked"})
	protected E[] createArray(final int j, final int length) {
		return (E[]) Arrays.create(getColumnClass(j), length);
	}

	//////////////////////////////////////////////

	/**
	 * Creates a 2D {@code E} array of the specified row and column lengths.
	 * <p>
	 * @param rowCount    the number of rows of the array to create
	 * @param columnCount the number of columns of the array to create
	 * <p>
	 * @return a 2D {@code E} array of the specified row and column lengths
	 */
	protected E[][] createArray2D(final int rowCount, final int columnCount) {
		return Arrays.<E>create(c, rowCount, columnCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a header of the specified length.
	 * <p>
	 * @param length the length of the header
	 * <p>
	 * @return a header of the specified length
	 */
	protected static String[] createHeader(final int length) {
		final String[] header = new String[length];
		for (int i = 1; i <= length; ++i) {
			header[i - 1] = Objects.toString(i);
		}
		return header;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS / EXPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads {@code this} from the file denoted by the specified path.
	 * <p>
	 * @param parser    the {@link IParser} of {@code E} element type of the file to load
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public void load(final IParser<E> parser, final String path, final boolean hasHeader)
			throws IOException {
		final FileHandler fileHandler = new FileHandler(path);
		BufferedReader reader = null;
		try {
			reader = fileHandler.createReader();
			load(parser, reader, fileHandler.countLines(true) - (hasHeader ? 1 : 0), hasHeader);
		} finally {
			Resources.close(reader);
		}
	}

	/**
	 * Loads {@code this} from the specified {@link BufferedReader}.
	 * <p>
	 * @param parser    the {@link IParser} of {@code E} element type of the lines to load
	 * @param reader    the {@link BufferedReader} of the lines to load
	 * @param rowCount  the number of lines to load
	 * @param hasHeader the flag specifying whether the first line is a header
	 * <p>
	 * @throws IOException if there is a problem with reading with {@code reader}
	 */
	public void load(final IParser<E> parser, final BufferedReader reader, final int rowCount,
			final boolean hasHeader)
			throws ClassCastException, IOException {
		m = rowCount;
		n = 0;
		// Parse the file
		String line;
		if ((line = reader.readLine()) != null) {
			// Find the delimiter (take the first one in the array in case of different delimiters)
			Character delimiter = null;
			for (final char d : COLUMN_DELIMITERS) {
				final int occurrenceCount = Strings.count(line, d);
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
			// Create the replacer for the delimiter
			final StringReplacer replacer = new StringReplacer(new char[] {BAR},
					Objects.toString(delimiter));
			// Clear the table
			clear();
			// Scan the file line by line
			int i = 0;
			String[] values = loadLine(line, delimiter, replacer);
			if (hasHeader) {
				header = values;
			} else {
				setRow(i++, parser.parseToArray(values));
			}
			while ((line = reader.readLine()) != null) {
				values = loadLine(line, delimiter, replacer);
				if (Arrays.isNullOrEmpty(values)) {
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
					setRow(i++, parser.parseToArray(values));
				}
			}
			// Resize if there are any empty rows or columns
			resize();
		}
	}

	protected String[] loadLine(final String line, final char delimiter,
			final StringReplacer replacer) {
		final String l = Strings.replaceInside(line, new char[] {SINGLE_QUOTE, DOUBLE_QUOTE},
				new char[] {delimiter}, String.valueOf(BAR));
		final String[] values = Strings.split(l, delimiter).toArray();
		return replacer != null ? replacer.callToArray(values) : values;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Saves {@code this} to the file denoted by the specified path.
	 * <p>
	 * @param path       the path to the file to save to
	 * @param saveHeader the flag specifying whether to save the header
	 * <p>
	 * @return {@code true} if {@code this} is saved to the file denoted by the specified path,
	 *         {@code false} otherwise
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening the file denoted
	 *                               by {@code path}
	 */
	public boolean save(final String path, final boolean saveHeader)
			throws FileNotFoundException {
		final FileHandler fileHandler = new FileHandler(path);
		fileHandler.createWriter(false);
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
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fills {@code this} with the specified {@code E} value.
	 * <p>
	 * @param value the {@code E} value to fill with (may be {@code null})
	 */
	public void fill(final E value) {
		Arrays.<E>fill(elements, value);
	}

	/**
	 * Merges with the specified {@link Table}.
	 * <p>
	 * @param table the {@link Table} of {@code E} element type to merge with
	 */
	public void merge(final Table<E> table) {
		merge(table, true);
	}

	/**
	 * Merges with the specified {@link Table}.
	 * <p>
	 * @param table     the {@link Table} of {@code E} element type to merge with
	 * @param mergeRows the flag specifying whether to merge the rows or the columns
	 */
	public void merge(final Table<E> table, final boolean mergeRows) {
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
		while (i > 0 && !Arrays.<E>hasAnyValue(elements[i])) {
			--i;
		}
		resize(i + 1, n);
	}

	/**
	 * Removes the empty leading columns.
	 */
	public void resizeColumns() {
		int j = n - 1;
		boolean isEmpty = true;
		while (j > 0 && isEmpty) {
			for (int i = 0; i < m && isEmpty; ++i) {
				if (elements[i][j] != null) {
					isEmpty = false;
				}
			}
			if (isEmpty) {
				--j;
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
		IntegerArguments.requireNonNegative(rowCount);
		IntegerArguments.requireNonNegative(columnCount);

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
			final E[][] resizedTable = createArray2D(rowCount, columnCount);
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
			final int rowCount = Math.max(0, rowOffset + m);
			final int columnCount = Math.max(0, columnOffset + n);

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
			final E[][] shiftedTable = createArray2D(rowCount, columnCount);
			for (int i = 0; i < rowCount; ++i) {
				if (i < rowOffset) {
					Arrays.<E>fill(elements[i], null);
				} else {
					final E[] source;
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
		header = createHeader(m);
		elements = Arrays.<E>transpose(c, elements);
		n = m;
		m = elements.length;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is empty.
	 * <p>
	 * @return {@code true} if {@code this} is empty, {@code false} otherwise
	 */
	public boolean isEmpty() {
		return m == 0 || n == 0;
	}

	/**
	 * Tests whether {@code this} is non-empty.
	 * <p>
	 * @return {@code true} if {@code this} is non-empty, {@code false} otherwise
	 */
	public boolean isNonEmpty() {
		return m > 0 && n > 0;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ITERABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link Iterator} over the rows of {@code this}.
	 * <p>
	 * @return an {@link Iterator} over the rows of {@code this}
	 */
	public Iterator<E[]> iterator() {
		return new TableIterator();
	}

	/**
	 * {@link TableIterator} is the {@link Iterator} over the rows of {@code this}.
	 */
	protected class TableIterator
			implements Iterator<E[]> {

		/**
		 * The index of the next row.
		 */
		protected int cursor = 0;

		/**
		 * Constructs a {@link TableIterator}.
		 */
		protected TableIterator() {
		}

		/**
		 * Tests whether {@code this} has next.
		 * <p>
		 * @return {@code true} if {@code this} has next, {@code false} otherwise
		 */
		public boolean hasNext() {
			return cursor != m;
		}

		public E[] next() {
			final int i = cursor;
			if (i >= m) {
				throw new NoSuchElementException();
			}
			cursor = i + 1;
			return elements[i];
		}

		public void remove() {
			if (cursor > 0) {
				// Remove the element pointed by the cursor
				final E[][] resizedTable = createArray2D(m - 1, n);
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
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	@SuppressWarnings({"cast", "unchecked"})
	public Table<E> clone() {
		try {
			final Table<E> clone = (Table<E>) super.clone();
			clone.c = Objects.clone(c);
			clone.header = Arrays.clone(header);
			clone.elements = Arrays.clone(elements);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
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
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
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
		final StringBuilder builder = Strings.createBuilder(m * n * (INITIAL_CAPACITY + 1));
		for (int i = 0; i < m; ++i) {
			builder.append(Strings.joinWith(getRow(i), COLUMN_DELIMITERS[0])).append(NEW_LINE);
		}
		return builder.toString();
	}
}
