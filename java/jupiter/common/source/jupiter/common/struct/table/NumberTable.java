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

import java.io.IOException;

import jupiter.common.map.parser.IParser;
import jupiter.common.math.Maths;
import jupiter.common.math.Statistics;
import jupiter.common.util.Numbers;
import jupiter.common.util.Objects;

/**
 * {@link NumberTable} is the {@link Table} of element {@link Number} type {@code E}.
 * <p>
 * @param <E> the element {@link Number} type of the {@link Table}
 */
public class NumberTable<E extends Number>
		extends Table<E> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link NumberTable} of element {@link Number} type {@code E} with the specified
	 * numbers of rows and columns.
	 * <p>
	 * @param c           the {@link Class} of element {@link Number} type {@code E}
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public NumberTable(final Class<E> c, final int rowCount, final int columnCount) {
		super(c, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link NumberTable} of element {@link Number} type {@code E} with the specified
	 * header and numbers of rows and columns.
	 * <p>
	 * @param c           the {@link Class} of element {@link Number} type {@code E}
	 * @param header      an array of {@link String}
	 * @param rowCount    the number of rows
	 * @param columnCount the number of columns
	 */
	public NumberTable(final Class<E> c, final String[] header, final int rowCount,
			final int columnCount) {
		super(c, header, rowCount, columnCount);
	}

	/**
	 * Constructs a {@link NumberTable} of element {@link Number} type {@code E} with the specified
	 * elements.
	 * <p>
	 * @param c        the {@link Class} of element {@link Number} type {@code E}
	 * @param elements a 2D {@code E} array
	 */
	public NumberTable(final Class<E> c, final E[][] elements) {
		super(c, elements);
	}

	/**
	 * Constructs a {@link NumberTable} of element {@link Number} type {@code E} with the specified
	 * header and elements.
	 * <p>
	 * @param c        the {@link Class} of element {@link Number} type {@code E}
	 * @param header   an {@code E} array
	 * @param elements a 2D {@code E} array
	 */
	public NumberTable(final Class<E> c, final String[] header, final E[][] elements) {
		super(c, header, elements);
	}

	/**
	 * Constructs a {@link NumberTable} of element {@link Number} type {@code E} loaded from the
	 * file denoted by the specified path.
	 * <p>
	 * @param parser    an {@link IParser} of element {@link Number} type {@code E}
	 * @param path      the path to the file to load
	 * @param hasHeader the flag specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code path}
	 */
	public NumberTable(final IParser<E> parser, final String path, final boolean hasHeader)
			throws IOException {
		super(parser, path, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the means that are computed for each row.
	 * <p>
	 * @return the means that are computed for each row
	 */
	public double[] getRowMeans() {
		final double[] means = new double[m];
		for (int i = 0; i < m; ++i) {
			means[i] = Statistics.getMean(getRow(i));
		}
		return means;
	}

	/**
	 * Returns the means that are computed for each column.
	 * <p>
	 * @return the means that are computed for each column
	 */
	public double[] getColumnMeans() {
		final double[] means = new double[n];
		for (int i = 0; i < n; ++i) {
			means[i] = Statistics.getMean(getColumn(i));
		}
		return means;
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
	public NumberTable<E> clone() {
		return (NumberTable<E>) super.clone();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the {@code other} type prevents it from being compared to
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
	 * @param other     the other {@link Object} to compare against for equality (may be
	 *                  {@code null})
	 * @param tolerance the tolerance level
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other} within {@code tolerance},
	 *         {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the {@code other} type prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
	 */
	public boolean equals(final Object other, final double tolerance) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof NumberTable)) {
			return false;
		}
		final NumberTable<?> otherNumberTable = (NumberTable<?>) other;
		if (!Objects.equals(c, otherNumberTable.c) || m != otherNumberTable.m ||
				n != otherNumberTable.n) {
			return false;
		}
		for (int j = 0; j < n; ++j) {
			if (!Objects.equals(header[j], otherNumberTable.header[j])) {
				return false;
			}
		}
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (!Numbers.equals(elements[i][j], otherNumberTable.elements[i][j], tolerance)) {
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
		return Objects.hashCode(serialVersionUID, elements);
	}
}
