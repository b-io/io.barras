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

import static jupiter.common.io.IO.IO;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import jupiter.common.io.Resources;
import jupiter.common.math.Numbers;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.util.Booleans;
import jupiter.common.util.Integers;
import jupiter.common.util.Strings;

public class SQL {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String NULL = "NULL";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected SQL() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Object convert(final Class<?> c, final String value) {
		if (isNull(value)) {
			return null;
		}
		if (Booleans.is(c)) {
			return Integers.convert(value) == 1;
		} else if (Numbers.is(c)) {
			return Numbers.toNumber(c, value);
		} else if (Strings.is(c)) {
			return value;
		} else if (Time.class.isAssignableFrom(c)) {
			return Time.valueOf(value);
		} else if (Timestamp.class.isAssignableFrom(c)) {
			return Timestamp.valueOf(value);
		}
		throw new IllegalArgumentException("Unknown class: " + c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static ExtendedList<SQLGenericRow> execute(final Connection connection,
			final String query) {
		CallableStatement statement = null;
		try {
			statement = connection.prepareCall(query);
			return execute(statement);
		} catch (final SQLException ex) {
			IO.error(ex);
		} finally {
			Resources.autoClose(statement);
		}
		return new ExtendedList<SQLGenericRow>();
	}

	public static ExtendedList<SQLGenericRow> execute(final CallableStatement statement)
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
		final ExtendedList<SQLGenericRow> rows = new ExtendedList<SQLGenericRow>();
		while (resultSet.next()) {
			final Object[] values = new Object[n];
			for (int i = 0; i < n; ++i) {
				values[i] = resultSet.getObject(header[i]);
			}
			rows.add(new SQLGenericRow(header, values));
		}
		return rows;
	}

	//////////////////////////////////////////////

	public static <T extends SQLRow> ExtendedList<T> execute(final Connection connection,
			final String query, final Class<T> c) {
		CallableStatement statement = null;
		try {
			statement = connection.prepareCall(query);
			return execute(statement, c);
		} catch (final SQLException ex) {
			IO.error(ex);
		} finally {
			Resources.autoClose(statement);
		}
		return new ExtendedList<T>();
	}

	public static <T extends SQLRow> ExtendedList<T> execute(final CallableStatement statement,
			final Class<T> c)
			throws SQLException {
		final ExtendedList<T> rows = new ExtendedList<T>();
		try {
			final Constructor<T> constructor = c.getConstructor(ResultSet.class);
			final ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				rows.add(constructor.newInstance(resultSet));
			}
		} catch (final IllegalAccessException ex) {
			IO.error(ex);
		} catch (final IllegalArgumentException ex) {
			IO.error(ex);
		} catch (final InstantiationException ex) {
			IO.error(ex);
		} catch (final InvocationTargetException ex) {
			IO.error(ex);
		} catch (final NoSuchMethodException ex) {
			IO.error("No constructor with ", ResultSet.class.getSimpleName(), " in ",
					c.getSimpleName(), " found: ", ex.getMessage());
		} catch (final SecurityException ex) {
			IO.error(ex);
		}
		return rows;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns either any auto-generated keys created as a result of executing the specified SQL
	 * Data Manipulation Language (DML) statement, or {@code -1} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) with a specific database
	 * @param query      a SQL Data Manipulation Language (DML) {@code INSERT} statement
	 * <p>
	 * @return either any auto-generated keys created as a result of executing the specified SQL
	 *         Data Manipulation Language (DML) statement, or {@code -1} if there is a problem
	 */
	public static long insert(final Connection connection, final String query) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			statement.executeUpdate();
			final ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (final SQLException ex) {
			IO.error(ex);
		} finally {
			Resources.autoClose(statement);
		}
		return -1;
	}

	/**
	 * Returns either the row count for SQL Data Manipulation Language (DML) statements, {@code 0}
	 * for SQL statements that return nothing, or {@code -1} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) with a specific database
	 * @param query      a SQL Data Manipulation Language (DML) statement, such as {@code INSERT},
	 *                   {@code UPDATE} or {@code DELETE}; or an SQL statement that returns nothing,
	 *                   such as a DDL statement
	 * <p>
	 * @return either the row count for SQL Data Manipulation Language (DML) statements, {@code 0}
	 *         for SQL statements that return nothing, or {@code -1} if there is a problem
	 */
	public static int update(final Connection connection, final String query) {
		CallableStatement statement = null;
		try {
			statement = connection.prepareCall(query);
			return statement.executeUpdate();
		} catch (final SQLException ex) {
			IO.error(ex);
		} finally {
			Resources.autoClose(statement);
		}
		return -1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean isNull(final Object object) {
		return object == null || NULL.equals(object.toString());
	}

	public static boolean isNull(final String string) {
		return string == null || NULL.equals(string);
	}
}
