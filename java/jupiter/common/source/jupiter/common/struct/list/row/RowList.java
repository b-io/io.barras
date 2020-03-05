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

import java.util.Collection;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.table.ITable;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Classes;
import jupiter.common.util.Lists;
import jupiter.common.util.Strings;

/**
 * {@link RowList} extends {@link ExtendedList} of {@link Row}.
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
	 * The header.
	 */
	public final String[] header;


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
	public RowList(final String[] header) {
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
		super(initialCapacity);

		// Check the arguments
		ArrayArguments.requireNonNull(header);

		// Set the header
		this.header = header;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link RowList} with the specified elements.
	 * <p>
	 * @param elements an array of {@link Row}
	 * <p>
	 * @throws NullPointerException if {@code elements} is {@code null}
	 */
	public RowList(final Row... elements) {
		this(createHeader(Arguments.requireNonNull(elements, "elements").length), elements);
	}

	/**
	 * Constructs a {@link RowList} with the specified header and elements.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements an array of {@link Row}
	 * <p>
	 * @throws NullPointerException if {@code elements} is {@code null}
	 */
	public RowList(final String[] header, final Row... elements) {
		super(elements);

		// Check the arguments
		ArrayArguments.requireNonNull(header);

		// Set the header
		this.header = header;
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link RowList} with the specified elements in a {@link Collection}.
	 * <p>
	 * @param elements a {@link Collection} of element subtype of {@link Row}
	 * <p>
	 * @throws NullPointerException if {@code elements} is {@code null}
	 */
	public RowList(final Collection<? extends Row> elements) {
		this(createHeader(Arguments.requireNonNull(elements, "elements").size()), elements);
	}

	/**
	 * Constructs a {@link RowList} with the specified header and elements in a {@link Collection}.
	 * <p>
	 * @param header   an array of {@link String}
	 * @param elements a {@link Collection} of element subtype of {@link Row}
	 * <p>
	 * @throws NullPointerException if {@code elements} is {@code null}
	 */
	public RowList(final String[] header,
			final Collection<? extends Row> elements) {
		super(elements);

		// Check the arguments
		ArrayArguments.requireNonNull(header);

		// Set the header
		this.header = header;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element {@link Class} of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the element {@link Class} of the specified column
	 */
	public Class<?> getColumnClass(final int j) {
		// Check the arguments
		if (isEmpty()) {
			return Object.class;
		}
		ArrayArguments.requireIndex(j, header.length);

		// Get the corresponding column class (common ancestor of the column element classes)
		Class<?> c = Classes.get(get(0).elements[j]);
		for (int i = 1; i < size(); ++i) {
			c = Classes.getCommonAncestor(c, Classes.get(get(i).elements[j]));
		}
		return c != null ? c : Object.class;
	}

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

	/**
	 * Returns the header.
	 * <p>
	 * @return the header
	 */
	public String[] getHeader() {
		return header;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

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
	 */
	public String getColumnName(final int j) {
		// Check the arguments
		ArrayArguments.requireIndex(j, header.length);

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
	public Object get(final int i, final String name) {
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
	public Object get(final int i, final int j) {
		// Check the arguments
		// • i
		ArrayArguments.requireIndex(i, size());
		// • j
		ArrayArguments.requireIndex(j, header.length);

		// Get the corresponding element
		return get(i).elements[j];
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
	public Object[] getRow(final int i) {
		return getRow(i, 0, header.length);
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
		return getRow(i, fromColumn, header.length - fromColumn);
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
		ArrayArguments.requireIndex(i, size());
		// • from
		ArrayArguments.requireIndex(fromColumn, header.length);
		// • length
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final int l = Math.min(length, header.length - fromColumn);

		// Get the corresponding row
		final Object[] row = new Object[l];
		System.arraycopy(get(i).elements, fromColumn, row, 0, l);
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
	 * @throws IllegalOperationException if there is no header
	 */
	public Object[] getColumn(final String name) {
		return getColumn(name, 0, size());
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
	public Object[] getColumn(final int j) {
		return getColumn(j, 0, size());
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
		return getColumn(name, fromRow, size() - fromRow);
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
		return getColumn(j, fromRow, size() - fromRow);
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
		ArrayArguments.requireIndex(j, header.length);
		// • from
		ArrayArguments.requireIndex(fromRow, size());
		// • length
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final int l = Math.min(length, size() - fromRow);

		// Get the corresponding column
		final Object[] column = createArray(j, l);
		for (int i = 0; i < l; ++i) {
			column[i] = get(fromRow + i).elements[j];
		}
		return column;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an array of the element {@link Class} of the specified column of the specified
	 * length.
	 * <p>
	 * @param j      the column index
	 * @param length the length of the array to create
	 * <p>
	 * @return an array of the element {@link Class} of the specified column of the specified length
	 */
	protected Object[] createArray(final int j, final int length) {
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
			header[i - 1] = Strings.toString(i);
		}
		return header;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is non-empty.
	 * <p>
	 * @return {@code true} if {@code this} is non-empty, {@code false} otherwise
	 */
	public boolean isNonEmpty() {
		return !isEmpty();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public RowList clone() {
		return (RowList) super.clone();
	}
}
