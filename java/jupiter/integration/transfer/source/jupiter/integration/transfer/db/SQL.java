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
package jupiter.integration.transfer.db;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

import jupiter.common.math.Numbers;
import jupiter.common.struct.list.ExtendedList;

public class SQL {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected SQL() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Object convert(final Class<?> c, final String string) {
		if (isNull(string)) {
			return null;
		}
		if (Boolean.class.isAssignableFrom(c)) {
			return Integer.valueOf(string) == 1;
		} else if (Numbers.isNumber(c)) {
			return Numbers.toNumber(c, string);
		} else if (String.class.isAssignableFrom(c)) {
			return string;
		} else if (Timestamp.class.isAssignableFrom(c)) {
			return Timestamp.valueOf(string);
		}
		throw new IllegalArgumentException("Unknown class: " + c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static ExtendedList<DBRow> getRows(final CallableStatement statement)
			throws SQLException {
		// Get the result of the query
		final ResultSet resultSet = statement.executeQuery();
		// Get the header of the result
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int n = metaData.getColumnCount();
		final String[] header = new String[n];
		for (int i = 1; i <= n; ++i) {
			header[i - 1] = metaData.getColumnName(i);
		}
		// Store and return the rows
		final ExtendedList<DBRow> rows = new ExtendedList<DBRow>();
		while (resultSet.next()) {
			final Object[] values = new Object[n];
			for (int i = 0; i < n; ++i) {
				values[i] = resultSet.getObject(header[i]);
			}
			rows.add(new DBRow(header, values));
		}
		return rows;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean isNull(final Object object) {
		return object == null || object.toString().equals("NULL");
	}

	public static boolean isNull(final String string) {
		return string == null || string.equals("NULL");
	}
}