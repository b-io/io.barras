/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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

import jupiter.common.map.parser.Parser;
import jupiter.common.math.Maths;
import jupiter.common.math.Numbers;
import jupiter.common.math.Statistics;
import jupiter.common.util.Objects;

/**
 * {@link NumberTable} is a {@link Table} of type {@code T} extending {@link Number}.
 * <p>
 * @param <T> the type of the elements extending {@link Number}
 */
public class NumberTable<T extends Number>
		extends Table<T> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = -1593077617163232511L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link NumberTable} of type {@code T} of the specified numbers of rows and
	 * columns.
	 * <p>
	 * @param c the {@link Class} of type {@code T}
	 * @param m the number of rows
	 * @param n the number of columns
	 */
	public NumberTable(final Class<T> c, final int m, final int n) {
		super(c, m, n);
	}

	/**
	 * Constructs a {@link NumberTable} of type {@code T} from the specified elements.
	 * <p>
	 * @param c        the {@link Class} of type {@code T}
	 * @param elements a 2D array of type {@code T}
	 */
	public NumberTable(final Class<T> c, final T[]... elements) {
		super(c, elements);
	}

	/**
	 * Constructs a {@link NumberTable} of type {@code T} from the specified header and elements.
	 * <p>
	 * @param c        the {@link Class} of type {@code T}
	 * @param header   an array of type {@code T}
	 * @param elements a 2D array of type {@code T}
	 */
	public NumberTable(final Class<T> c, final String[] header, final T[]... elements) {
		super(c, header, elements);
	}

	/**
	 * Constructs a {@link NumberTable} of type {@code T} imported from the specified file.
	 * <p>
	 * @param parser    a {@link Parser} of type {@code T}
	 * @param pathname  the pathname of the file to import
	 * @param hasHeader the option specifying whether the file has a header
	 * <p>
	 * @throws IOException if there is a problem with reading the file
	 */
	public NumberTable(final Parser<T> parser, final String pathname, final boolean hasHeader)
			throws IOException {
		super(parser, pathname, hasHeader);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the array of means that are computed for each row.
	 * <p>
	 * @return the array of means that are computed for each row
	 */
	public double[] getRowMeans() {
		final double[] means = new double[m];
		for (int i = 0; i < m; ++i) {
			means[i] = Statistics.<T>getMean(getRow(i));
		}
		return means;
	}

	/**
	 * Returns the array of means that are computed for each column.
	 * <p>
	 * @return the array of means that are computed for each column
	 */
	public double[] getColumnMeans() {
		final double[] means = new double[n];
		for (int i = 0; i < n; ++i) {
			means[i] = Statistics.<T>getMean(getColumn(i));
		}
		return means;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(final Object other) {
		return equals(other, Maths.DEFAULT_TOLERANCE);
	}

	public boolean equals(final Object other, final double tolerance) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof NumberTable)) {
			return false;
		}
		final NumberTable<?> otherNumberTable = (NumberTable<?>) other;
		if (!Objects.equals(c, otherNumberTable.getElementClass()) ||
				m != otherNumberTable.getRowCount() || n != otherNumberTable.getColumnCount()) {
			return false;
		}
		final String[] otherHeader = otherNumberTable.getHeader();
		for (int j = 0; j < n; ++j) {
			if (!Objects.equals(header[j], otherHeader[j])) {
				return false;
			}
		}
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				if (!Numbers.equals(elements[i][j], otherNumberTable.get(i, j), tolerance)) {
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
}
