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
package jupiter.transfer.db;

import static jupiter.common.io.IO.IO;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import jupiter.common.exception.IllegalClassException;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Arguments;
import jupiter.common.time.Dates;
import jupiter.common.util.Arrays;
import jupiter.common.util.Booleans;
import jupiter.common.util.Bytes;
import jupiter.common.util.Doubles;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Numbers;
import jupiter.common.util.Shorts;
import jupiter.common.util.Strings;

public class SQL {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String NULL = "NULL";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SQL}.
	 */
	protected SQL() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the specified parameter {@link Object} of the specified {@link PreparedStatement} at the
	 * specified index.
	 * <p>
	 * @param statement a {@link PreparedStatement}
	 * @param index     the index of the parameter {@link Object} to set
	 * @param parameter the parameter {@link Object} to set
	 * <p>
	 * @throws SQLException if {@code index} does not correspond to a parameter marker in
	 *                      {@code statement}, if a database access error occurs or if this method
	 *                      is called on a closed {@link PreparedStatement}
	 */
	public static void setParameter(final PreparedStatement statement, final int index,
			final Object parameter)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(parameter);

		// Set the parameter of the statement at the index
		final Class<?> c = parameter.getClass();
		if (Array.class.isAssignableFrom(c)) {
			statement.setArray(index, (Array) parameter);
		} else if (BigDecimal.class.isAssignableFrom(c)) {
			statement.setBigDecimal(index, (BigDecimal) parameter);
		} else if (Blob.class.isAssignableFrom(c)) {
			statement.setBlob(index, (Blob) parameter);
		} else if (Booleans.is(c)) {
			statement.setBoolean(index, (Boolean) parameter);
		} else if (Bytes.is(c)) {
			statement.setByte(index, (Byte) parameter);
		} else if (Bytes.isPrimitiveArray(c)) {
			statement.setBytes(index, (byte[]) parameter);
		} else if (Clob.class.isAssignableFrom(c)) {
			statement.setClob(index, (Clob) parameter);
		} else if (Dates.is(c)) {
			statement.setDate(index, (Date) parameter);
		} else if (Doubles.is(c)) {
			statement.setDouble(index, (Double) parameter);
		} else if (Floats.is(c)) {
			statement.setFloat(index, (Float) parameter);
		} else if (Integers.is(c)) {
			statement.setInt(index, (Integer) parameter);
		} else if (Longs.is(c)) {
			statement.setLong(index, (Long) parameter);
		} else if (Shorts.is(c)) {
			statement.setShort(index, (Short) parameter);
		} else if (Strings.is(c)) {
			statement.setString(index, (String) parameter);
		} else if (Time.class.isAssignableFrom(c)) {
			statement.setTime(index, (Time) parameter);
		} else if (Timestamp.class.isAssignableFrom(c)) {
			statement.setTimestamp(index, (Timestamp) parameter);
		} else if (URL.class.isAssignableFrom(c)) {
			statement.setURL(index, (URL) parameter);
		} else {
			throw new IllegalClassException(c);
		}
	}

	/**
	 * Sets the specified array of parameter {@link Object} of the specified
	 * {@link PreparedStatement}.
	 * <p>
	 * @param statement  a {@link PreparedStatement}
	 * @param parameters the array of parameter {@link Object} to set
	 * <p>
	 * @throws SQLException if the {@code parameters} length is greater than the number of parameter
	 *                      markers in {@code statement}, if a database access error occurs or if
	 *                      this method is called on a closed {@link PreparedStatement}
	 */
	public static void setParameters(final PreparedStatement statement, final Object... parameters)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(parameters);

		// Set the parameters of the statement
		int index = 1;
		for (final Object parameter : parameters) {
			setParameter(statement, index++, parameter);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link Object} of the specified {@link Class} converted from the specified
	 * {@link String}.
	 * <p>
	 * @param c    the {@link Class} of the {@link Object} to convert to
	 * @param text the {@link String} to convert
	 * <p>
	 * @return an {@link Object} of the specified {@link Class} converted from the specified
	 *         {@link String}
	 */
	public static Object convert(final Class<?> c, final String text) {
		if (isNull(text)) {
			return null;
		}
		if (Booleans.is(c)) {
			return Integers.convert(text) == 1;
		} else if (Numbers.is(c)) {
			return Numbers.toNumber(c, text);
		} else if (Strings.is(c)) {
			return text;
		} else if (Time.class.isAssignableFrom(c)) {
			return Time.valueOf(text);
		} else if (Timestamp.class.isAssignableFrom(c)) {
			return Timestamp.valueOf(text);
		} else if (URL.class.isAssignableFrom(c)) {
			try {
				return new URL(text);
			} catch (final MalformedURLException ex) {
				throw new IllegalStateException(Strings.toString(ex), ex);
			}
		}
		throw new IllegalClassException(c);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String createStoredProcedureQuery(final String name, final int parameterCount)
			throws SQLException {
		return "{call ".concat(name)
				.concat(Arrays.toString(Arrays.repeat("?", parameterCount)))
				.concat("}");
	}

	public static CallableStatement createStoredProcedureCall(final Connection connection,
			final String name, final Object... parameters)
			throws SQLException {
		// Create the statement for executing the stored procedure
		CallableStatement statement = connection.prepareCall(
				createStoredProcedureQuery(name, parameters.length));
		// Set the parameters of the statement
		if (parameters != null) {
			setParameters(statement, parameters);
		}
		// Return the statement
		return statement;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static ExtendedList<SQLGenericRow> execute(final Connection connection,
			final String query) {
		return executeWith(connection, query, null);
	}

	public static ExtendedList<SQLGenericRow> executeWith(final Connection connection,
			final String query, final Object... parameters) {
		// Create the statement for executing the query
		CallableStatement statement = null;
		try {
			statement = connection.prepareCall(query);
			// Set the parameters of the statement
			if (parameters != null) {
				setParameters(statement, parameters);
			}
			// Execute the query and return the result
			return execute(statement);
		} catch (final SQLException ex) {
			IO.error(ex);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (final SQLException ex) {
					IO.error(ex);
				}
			}
		}
		return new ExtendedList<SQLGenericRow>();
	}

	public static ExtendedList<SQLGenericRow> execute(final CallableStatement statement)
			throws SQLException {
		return executeWith(statement, null);
	}

	public static ExtendedList<SQLGenericRow> executeWith(final CallableStatement statement,
			final Object... parameters)
			throws SQLException {
		// Set the parameters of the statement
		if (parameters != null) {
			setParameters(statement, parameters);
		}
		// Execute the query
		final ResultSet resultSet = statement.executeQuery();
		// Get the header of the result
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int n = metaData.getColumnCount();
		final String[] header = new String[n];
		for (int i = 1; i <= n; ++i) {
			header[i - 1] = metaData.getColumnName(i);
		}
		// Store and return the result
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
		return executeWith(connection, query, c, null);
	}

	public static <T extends SQLRow> ExtendedList<T> executeWith(final Connection connection,
			final String query, final Class<T> c, final Object... parameters) {
		// Create the statement for executing the query
		CallableStatement statement = null;
		try {
			statement = connection.prepareCall(query);
			// Set the parameters of the statement
			if (parameters != null) {
				setParameters(statement, parameters);
			}
			// Execute the query and return the result
			return execute(statement, c);
		} catch (final SQLException ex) {
			IO.error(ex);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (final SQLException ex) {
					IO.error(ex);
				}
			}
		}
		return new ExtendedList<T>();
	}

	public static <T extends SQLRow> ExtendedList<T> execute(final CallableStatement statement,
			final Class<T> c)
			throws SQLException {
		return executeWith(statement, c, null);
	}

	public static <T extends SQLRow> ExtendedList<T> executeWith(final CallableStatement statement,
			final Class<T> c, final Object... parameters)
			throws SQLException {
		// Set the parameters of the statement
		if (parameters != null) {
			setParameters(statement, parameters);
		}
		// Execute the query
		final ExtendedList<T> rows = new ExtendedList<T>();
		try {
			final Constructor<T> constructor = c.getConstructor(ResultSet.class);
			final ResultSet resultSet = statement.executeQuery();
			// Store and return the result
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
					c.getSimpleName(), " found: ", ex);
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
	 * @param connection a {@link Connection} (session) with a database
	 * @param query      a SQL Data Manipulation Language (DML) {@code INSERT} statement
	 * <p>
	 * @return either any auto-generated keys created as a result of executing the specified SQL
	 *         Data Manipulation Language (DML) statement, or {@code -1} if there is a problem
	 */
	public static long insert(final Connection connection, final String query) {
		return insertWith(connection, query, null);
	}

	/**
	 * Returns either any auto-generated keys created as a result of executing the specified SQL
	 * Data Manipulation Language (DML) statement with the specified array of parameter
	 * {@link Object}, or {@code -1} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) with a database
	 * @param query      a SQL Data Manipulation Language (DML) {@code INSERT} statement
	 * @param parameters the array of parameter {@link Object} to set
	 * <p>
	 * @return either any auto-generated keys created as a result of executing the specified SQL
	 *         Data Manipulation Language (DML) statement with the specified array of parameter
	 *         {@link Object}, or {@code -1} if there is a problem
	 */
	public static long insertWith(final Connection connection, final String query,
			final Object... parameters) {
		// Create the statement for executing the query
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			// Set the parameters of the statement
			if (parameters != null) {
				setParameters(statement, parameters);
			}
			// Execute the query
			statement.executeUpdate();
			// Return any auto-generated keys
			final ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (final SQLException ex) {
			IO.error(ex);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (final SQLException ex) {
					IO.error(ex);
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns either the row count for the SQL Data Manipulation Language (DML) statement,
	 * {@code 0} if nothing is returned, or {@code -1} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) with a database
	 * @param query      a SQL Data Manipulation Language (DML) statement, such as {@code INSERT},
	 *                   {@code UPDATE} or {@code DELETE}; or an SQL statement that returns nothing,
	 *                   such as a DDL statement
	 * <p>
	 * @return either the row count for the SQL Data Manipulation Language (DML) statement,
	 *         {@code 0} if nothing is returned, or {@code -1} if there is a problem
	 */
	public static int update(final Connection connection, final String query) {
		return updateWith(connection, query, null);
	}

	/**
	 * Returns either the row count for the SQL Data Manipulation Language (DML) statement with the
	 * specified array of parameter {@link Object}, {@code 0} if nothing is returned, or {@code -1}
	 * if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) with a database
	 * @param query      a SQL Data Manipulation Language (DML) statement, such as {@code INSERT},
	 *                   {@code UPDATE} or {@code DELETE}; or an SQL statement that returns nothing,
	 *                   such as a DDL statement
	 * @param parameters the array of parameter {@link Object} to set
	 * <p>
	 * @return either the row count for the SQL Data Manipulation Language (DML) statement with the
	 *         specified array of parameter {@link Object}, {@code 0} if nothing is returned, or
	 *         {@code -1} if there is a problem
	 */
	public static int updateWith(final Connection connection, final String query,
			final Object... parameters) {
		// Create the statement for executing the query
		CallableStatement statement = null;
		try {
			statement = connection.prepareCall(query);
			// Set the parameters of the statement
			if (parameters != null) {
				setParameters(statement, parameters);
			}
			// Execute the query and return the row count
			return statement.executeUpdate();
		} catch (final SQLException ex) {
			IO.error(ex);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (final SQLException ex) {
					IO.error(ex);
				}
			}
		}
		return -1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is {@code null} or {@code "NULL"}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is {@code null} or {@code "NULL"},
	 *         {@code false} otherwise
	 */
	public static boolean isNull(final Object object) {
		return object == null || NULL.equals(object.toString());
	}

	/**
	 * Tests whether the specified {@link String} is {@code null} or {@code "NULL"}.
	 * <p>
	 * @param text the {@link String} to test
	 * <p>
	 * @return {@code true} if the specified {@link String} is {@code null} or {@code "NULL"},
	 *         {@code false} otherwise
	 */
	public static boolean isNull(final String text) {
		return text == null || NULL.equals(text);
	}
}
