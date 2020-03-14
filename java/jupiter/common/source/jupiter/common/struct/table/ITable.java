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

import jupiter.common.exception.IllegalOperationException;

public interface ITable {

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
	public Class<?> getColumnClass(final int j);

	/**
	 * Returns the number of rows.
	 * <p>
	 * @return the number of rows
	 */
	public int getRowCount();

	/**
	 * Returns the number of columns.
	 * <p>
	 * @return the number of columns
	 */
	public int getColumnCount();

	/**
	 * Returns the header.
	 * <p>
	 * @return the header
	 */
	public String[] getHeader();

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
	public int getColumnIndex(final String name);

	/**
	 * Returns the name of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the name of the specified column
	 */
	public String getColumnName(final int j);

	////////////////////////////////////////////////////////////////////////////////////////////////

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
	public Object get(final int i, final String name);

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
	public Object get(final int i, final int j);

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
	public Object[] getRow(final int i);

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
	public Object[] getRow(final int i, final int fromColumn);

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
	public Object[] getRow(final int i, final int fromColumn, final int length);

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
	public Object[] getColumn(final String name);

	/**
	 * Returns the elements of the specified column.
	 * <p>
	 * @param j the column index
	 * <p>
	 * @return the elements of the specified column
	 * <p>
	 * @throws ArrayIndexOutOfBoundsException if {@code j} is out of bounds
	 */
	public Object[] getColumn(final int j);

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
	public Object[] getColumn(final String name, final int fromRow);

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
	public Object[] getColumn(final int j, final int fromRow);

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
	public Object[] getColumn(final String name, final int fromRow, final int length);

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
	public Object[] getColumn(final int j, final int fromRow, final int length);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is empty.
	 * <p>
	 * @return {@code true} if {@code this} is empty, {@code false} otherwise
	 */
	public boolean isEmpty();

	/**
	 * Tests whether {@code this} is non-empty.
	 * <p>
	 * @return {@code true} if {@code this} is non-empty, {@code false} otherwise
	 */
	public boolean isNonEmpty();
}
