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
package jupiter.common.struct.list.row;

import static jupiter.common.util.Classes.OBJECT_CLASS;

import java.util.Collection;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.table.ITable;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Collections;
import jupiter.common.util.Lists;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link RowList} is the {@link ExtendedList} of {@link Row}.
 */
public class RowList
		extends ExtendedList<Row>
		implements ITable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The index (row names).
	 */
	protected Object[] index;
	/**
	 * The header (column names).
	 */
	protected String[] header;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty {@link RowList} by default.
	 */
	public RowList() {
		this(Strings.EMPTY_ARRAY);
	}

	/**
	 * Constructs an empty {@link RowList} with the specified header.
	 * <p>
	 * @param header an array of {@link String}
	 */
	public RowList(final String... header) {
		this(header, Lists.DEFAULT_CAPACITY);
	}

	/**
	 * Constructs an empty {@link RowList} with the specified header and initial capacity.
	 * <p>
	 * @param header          an array of {@link String}
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public RowList(final String[] header, final int initialCapacity) {
		this(null, header, initialCapacity);
	}

	/**
	 * Constructs an empty {@link RowList} with the specified index, header and initial capacity.
	 * <p>
	 * @param index           an array of {@link Object} (may be {@code null})
	 * @param header          an array of {@link String}
	 * @param initialCapacity the initial capacity
	 * <p>
	 * @throws IllegalArgumentException if {@code initialCapacity} is negative
	 */
	public RowList(final Object[] index, final String[] header, final int initialCapacity) {
		super(initialCapacity);

		// Check the arguments
		Arguments.requireNonNull(header, "header");

		// Set the attributes
		this.index = index;
		this.header = header;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link RowList} with the specified elements.
	 * <p>
	 * @param elements an array of {@link Row}
	 */
	public RowList(final Row... elements) {
		this(createHeader(Arguments.requireNonNull(elements, "elements").length), elements);
	}

	/**
	 * Constructs a {@link RowList} with the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements an array of {@link Row}
	 */
	public RowList(final String[] header, final Row... elements) {
		this(null, header, elements);
	}

	/**
	 * Constructs a {@link RowList} with the specified index, header and elements.
	 * <p>
	 * @param index    an array of {@link Object} (may be {@code null})
	 * @param header   an array of {@link String}
	 * @param elements an array of {@link Row}
	 */
	public RowList(final Object[] index, final String[] header, final Row... elements) {
		super(elements);

		// Check the arguments
		Arguments.requireNonNull(header, "header");

		// Set the attributes
		this.index = index;
		this.header = header;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link RowList} with the elements of the specified {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of {@link Row}
	 */
	public RowList(final Collection<? extends Row> elements) {
		this(createHeader(Arguments.requireNonNull(elements, "elements").size()), elements);
	}

	/**
	 * Constructs a {@link RowList} with the specified header and elements of the specified
	 * {@link Collection}.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a {@link Collection} of {@link Row}
	 */
	public RowList(final String[] header, final Collection<? extends Row> elements) {
		this(null, header, elements);
	}

	/**
	 * Constructs a {@link RowList} with the specified index, header and elements of the specified
	 * {@link Collection}.
	 * <p>
	 * @param index    an array of {@link Object} (may be {@code null})
	 * @param header   an array of {@link String}
	 * @param elements a {@link Collection} of {@link Row}
	 */
	public RowList(final Object[] index, final String[] header,
			final Collection<? extends Row> elements) {
		super(elements);

		// Check the arguments
		Arguments.requireNonNull(header, "header");

		// Set the attributes
		this.index = index;
		this.header = header;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of rows.
	 * <p>
	 * @return the number of rows
	 */
	public int getRowCount() {
		return size();
	}

	/**
	 * Returns the number of columns.
	 * <p>
	 * @return the number of columns
	 */
	public int getColumnCount() {
		return header.length;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the row names.
	 * <p>
	 * @return the row names
	 */
	public Object[] getIndex() {
		return index;
	}

	/**
	 * Returns the name of the specified row.
	 * <p>
	 * @param i the row index
	 * <p>
	 * @return the name of the specified row
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 * @throws IllegalOperationException      if there is no index
	 */
	public Object getRowName(final int i) {
		// Verify the feasibility
		if (index == null) {
			throw new IllegalOperationException("There is no index");
		}
		// Check the arguments
		ArrayArguments.requireIndex(i, getRowCount());

		// Return the row name
		return index[i];
	}

	/**
	 * Returns the index of the specified row, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param name the row name
	 * <p>
	 * @return the index of the specified row, or {@code -1} if there is no such occurrence
	 * <p>
	 * @throws IllegalArgumentException  if {@code name} is not present
	 * @throws IllegalOperationException if there is no index
	 */
	public int getRowIndex(final Object name) {
		// Verify the feasibility
		if (index == null) {
			throw new IllegalOperationException("There is no index");
		}

		// Return the row index
		final int i = Arrays.findFirstIndex(index, name);
		if (i < 0) {
			throw new IllegalArgumentException(
					Strings.join("There is no row ", Strings.quote(name)));
		}
		return i;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the column names.
	 * <p>
	 * @return the column names
	 */
	public String[] getHeader() {
		return header;
	}

	/**
	 * Returns the name of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the name of the specified column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 * @throws IllegalOperationException      if there is no header
	 */
	public String getColumnName(final int j) {
		// Verify the feasibility
		if (header == null) {
			throw new IllegalOperationException("There is no header");
		}
		// Check the arguments
		ArrayArguments.requireIndex(j, getColumnCount());

		// Return the column name
		return header[j];
	}

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

		// Return the column index
		final int j = Strings.findFirstIndexIgnoreCase(header, name);
		if (j < 0) {
			throw new IllegalArgumentException(
					Strings.join("There is no column ", Strings.quote(name)));
		}
		return j;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the element {@link Class} of the specified row.
	 * <p>
	 * @param i the row index
	 * <p>
	 * @return the element {@link Class} of the specified row
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public Class<?> getRowClass(final int i) {
		// Check the arguments
		if (isEmpty()) {
			return OBJECT_CLASS;
		}
		ArrayArguments.requireIndex(i, getRowCount());

		// Return the corresponding row class (common ancestor of the row element classes)
		return Arrays.getElementClass(get(i));
	}

	/**
	 * Returns the element {@link Class} of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the element {@link Class} of the specified column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public Class<?> getColumnClass(final int j) {
		// Check the arguments
		if (isEmpty()) {
			return OBJECT_CLASS;
		}
		ArrayArguments.requireIndex(j, getColumnCount());

		// Return the corresponding column class (common ancestor of the column element classes)
		return Arrays.getElementClass(getColumn(j));
	}

	//////////////////////////////////////////////

	/**
	 * Returns the element at the specified row and column.
	 * <p>
	 * @param i the row index
	 * @param j the column index
	 * <p>
	 * @return the element at the specified row and column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out of bounds
	 */
	public Object get(final int i, final int j) {
		// Check the arguments
		// • i
		ArrayArguments.requireIndex(i, getRowCount());
		// • j
		ArrayArguments.requireIndex(j, getColumnCount());

		// Return the corresponding element
		return get(i).elements[j];
	}

	/**
	 * Returns the element at the specified row and column.
	 * <p>
	 * @param name the row name
	 * @param j    the column index
	 * <p>
	 * @return the element at the specified row and column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 * @throws IllegalArgumentException       if {@code name} is not present
	 * @throws IllegalOperationException      if there is no index
	 */
	public Object get(final Object name, final int j) {
		return get(getRowIndex(name), j);
	}

	/**
	 * Returns the element at the specified row and column.
	 * <p>
	 * @param i    the row index
	 * @param name the column name
	 * <p>
	 * @return the element at the specified row and column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 * @throws IllegalArgumentException       if {@code name} is not present
	 * @throws IllegalOperationException      if there is no header
	 */
	public Object get(final int i, final String name) {
		return get(i, getColumnIndex(name));
	}

	/**
	 * Returns the element at the specified row and column.
	 * <p>
	 * @param rowName    the row name
	 * @param columnName the column name
	 * <p>
	 * @return the element at the specified row and column
	 * <p>
	 * @throws IllegalArgumentException  if {@code rowName} or {@code columnName} is not present
	 * @throws IllegalOperationException if there is no index or header
	 */
	public Object get(final Object rowName, final String columnName) {
		return get(getRowIndex(rowName), getColumnIndex(columnName));
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
	public Object[] getRow(final int i) {
		return getRow(i, 0, getColumnCount());
	}

	/**
	 * Returns the elements of the specified row.
	 * <p>
	 * @param name the row name
	 * <p>
	 * @return the elements of the specified row
	 * <p>
	 * @throws IllegalArgumentException  if {@code name} is not present
	 * @throws IllegalOperationException if there is no index
	 */
	public Object[] getRow(final Object name) {
		return getRow(getRowIndex(name));
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
	public Object[] getRow(final int i, final int fromColumn) {
		return getRow(i, fromColumn, getColumnCount() - fromColumn);
	}

	/**
	 * Returns the elements of the specified row truncated from the specified column index.
	 * <p>
	 * @param name       the row name
	 * @param fromColumn the initial column index (inclusive)
	 * <p>
	 * @return the elements of the specified row truncated from the specified column index
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code fromColumn} is out of bounds
	 * @throws IllegalArgumentException       if {@code name} is not present
	 * @throws IllegalOperationException      if there is no index
	 */
	public Object[] getRow(final Object name, final int fromColumn) {
		return getRow(getRowIndex(name), fromColumn);
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
	public Object[] getRow(final int i, final int fromColumn, final int length) {
		// Check the arguments
		// • i
		ArrayArguments.requireIndex(i, getRowCount());
		// • from
		ArrayArguments.requireIndex(fromColumn, getColumnCount());
		// • length
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final int l = Math.min(length, getColumnCount() - fromColumn);

		// Return the corresponding row
		final Object[] row = createRowArray(i, l);
		System.arraycopy(get(i).elements, fromColumn, row, 0, l);
		return row;
	}

	/**
	 * Returns the elements of the specified row truncated from the specified column index to the
	 * specified length.
	 * <p>
	 * @param name       the row name
	 * @param fromColumn the initial column index (inclusive)
	 * @param length     the number of row elements to get
	 * <p>
	 * @return the elements of the specified row truncated from the specified column index to the
	 *         specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code fromColumn} is out of bounds
	 * @throws IllegalArgumentException       if {@code name} is not present
	 * @throws IllegalOperationException      if there is no index
	 */
	public Object[] getRow(final Object name, final int fromColumn, final int length) {
		return getRow(getRowIndex(name), fromColumn, length);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the elements of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the elements of the specified column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public Object[] getColumn(final int j) {
		return getColumn(j, 0, getRowCount());
	}

	/**
	 * Returns the elements of the specified column.
	 * <p>
	 * @param name the column name
	 * <p>
	 * @return the elements of the specified column
	 * <p>
	 * @throws IllegalArgumentException  if {@code name} is not present
	 * @throws IllegalOperationException if there is no header
	 */
	public Object[] getColumn(final String name) {
		return getColumn(getColumnIndex(name));
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
	public Object[] getColumn(final int j, final int fromRow) {
		return getColumn(j, fromRow, getRowCount() - fromRow);
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
	public Object[] getColumn(final String name, final int fromRow) {
		return getColumn(getColumnIndex(name), fromRow);
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
	public Object[] getColumn(final int j, final int fromRow, final int length) {
		// Check the arguments
		// • j
		ArrayArguments.requireIndex(j, getColumnCount());
		// • from
		ArrayArguments.requireIndex(fromRow, getRowCount());
		// • length
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final int l = Math.min(length, getRowCount() - fromRow);

		// Return the corresponding column
		final Object[] column = createColumnArray(j, l);
		for (int i = 0; i < l; ++i) {
			column[i] = get(fromRow + i).elements[j];
		}
		return column;
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
	public Object[] getColumn(final String name, final int fromRow, final int length) {
		return getColumn(getColumnIndex(name), fromRow, length);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the index.
	 * <p>
	 * @param index an array of {@link Object}
	 */
	public void setIndex(final Object... index) {
		// Check the arguments
		ArrayArguments.requireLength(index, getRowCount());

		// Set the index
		this.index = index;
	}

	/**
	 * Sets the header.
	 * <p>
	 * @param header an array of {@link String}
	 */
	public void setHeader(final String... header) {
		// Check the arguments
		ArrayArguments.requireLength(header, getColumnCount());

		// Set the header
		this.header = header;
	}

	//////////////////////////////////////////////

	/**
	 * Sets the element at the specified row and column.
	 * <p>
	 * @param i     the row index
	 * @param j     the column index
	 * @param value an {@link Object} (may be {@code null})
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code j} is out of bounds
	 */
	public void set(final int i, final int j, final Object value) {
		// Check the arguments
		// • i
		ArrayArguments.requireIndex(i, getRowCount());
		// • j
		ArrayArguments.requireIndex(j, getColumnCount());

		// Set the corresponding element
		get(i).elements[j] = value;
	}

	//////////////////////////////////////////////

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values an array of {@link Object}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void setRow(final int i, final Object[] values) {
		setRow(i, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index.
	 * <p>
	 * @param i          the row index
	 * @param values     an array of {@link Object}
	 * @param fromColumn the initial column index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code fromColumn} is out of bounds
	 */
	public void setRow(final int i, final Object[] values, final int fromColumn) {
		setRow(i, values, fromColumn, values.length);
	}

	/**
	 * Sets the elements of the specified row from the specified column index to the specified
	 * length.
	 * <p>
	 * @param i          the row index
	 * @param values     an array of {@link Object}
	 * @param fromColumn the initial column index (inclusive)
	 * @param length     the number of row elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} or {@code fromColumn} is out of bounds
	 */
	public void setRow(final int i, final Object[] values, final int fromColumn, final int length) {
		// Check the arguments
		// • i
		ArrayArguments.requireIndex(i, getRowCount());
		// • values
		ArrayArguments.requireNonEmpty(values, "values");
		ArrayArguments.requireMinLength(values, length);
		// • from
		ArrayArguments.requireIndex(fromColumn, getColumnCount());
		// • length
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final int l = Math.min(length, getColumnCount() - fromColumn);

		// Set the corresponding row
		System.arraycopy(values, 0, get(i).elements, fromColumn, l);
	}

	/**
	 * Sets the elements of the specified row.
	 * <p>
	 * @param i      the row index
	 * @param values a {@link Collection} of {@link Object}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	public void setRow(final int i, final Collection<? extends Object> values) {
		setRow(i, Collections.toArray(values));
	}

	//////////////////////////////////////////////

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values an array of {@link Object}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public void setColumn(final int j, final Object[] values) {
		setColumn(j, values, 0, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index.
	 * <p>
	 * @param j       the column index
	 * @param values  an array of {@link Object}
	 * @param fromRow the initial row index (inclusive)
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code fromRow} is out of bounds
	 */
	public void setColumn(final int j, final Object[] values, final int fromRow) {
		setColumn(j, values, fromRow, values.length);
	}

	/**
	 * Sets the elements of the specified column from the specified row index to the specified
	 * length.
	 * <p>
	 * @param j       the column index
	 * @param values  an array of {@link Object}
	 * @param fromRow the initial row index (inclusive)
	 * @param length  the number of column elements to set
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} or {@code fromRow} is out of bounds
	 */
	public void setColumn(final int j, final Object[] values, final int fromRow, final int length) {
		// Check the arguments
		// • j
		ArrayArguments.requireIndex(j, getColumnCount());
		// • values
		ArrayArguments.requireNonEmpty(values, "values");
		ArrayArguments.requireMinLength(values, length);
		// • from
		ArrayArguments.requireIndex(fromRow, getRowCount());
		// • length
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final int l = Math.min(length, getRowCount() - fromRow);

		// Set the corresponding column
		for (int i = 0; i < l; ++i) {
			get(i).elements[j] = values[fromRow + i];
		}
	}

	/**
	 * Sets the elements of the specified column.
	 * <p>
	 * @param j      the column index
	 * @param values a {@link Collection} of {@link Object}
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public void setColumn(final int j, final Collection<? extends Object> values) {
		setColumn(j, Collections.toArray(values));
	}

	//////////////////////////////////////////////

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values an array of {@link Object}
	 * <p>
	 * @throws IndexOutOfBoundsException if {@code values} is not of the same length as {@code this}
	 */
	public void setAll(final Object[] values) {
		for (int i = 0; i < getRowCount(); ++i) {
			System.arraycopy(values, i * getColumnCount(), get(i).elements, 0, getColumnCount());
		}
	}

	/**
	 * Sets all the elements.
	 * <p>
	 * @param values a 2D array of {@link Object}
	 * <p>
	 * @throws IndexOutOfBoundsException if {@code values} is not of the same length as {@code this}
	 */
	public void setAll(final Object[][] values) {
		for (int i = 0; i < getRowCount(); ++i) {
			setRow(i, values[i]);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of the element {@link Class} of the specified row of the specified length.
	 * <p>
	 * @param i      the row index
	 * @param length the length of the array to create
	 * <p>
	 * @return an array of the element {@link Class} of the specified row of the specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code i} is out of bounds
	 */
	protected Object[] createRowArray(final int i, final int length) {
		return Arrays.create(getRowClass(i), length);
	}

	/**
	 * Creates an array of the element {@link Class} of the specified column of the specified
	 * length.
	 * <p>
	 * @param j      the column index
	 * @param length the length of the array to create
	 * <p>
	 * @return an array of the element {@link Class} of the specified column of the specified length
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	protected Object[] createColumnArray(final int j, final int length) {
		return Arrays.create(getColumnClass(j), length);
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
	public RowList clone() {
		final RowList clone = new RowList(index, header, getRowCount());
		for (final Row element : this) {
			clone.add(Objects.clone(element));
		}
		return clone;
	}
}
