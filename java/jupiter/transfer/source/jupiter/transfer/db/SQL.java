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
package jupiter.transfer.db;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.LEFT_BRACE;
import static jupiter.common.util.Characters.RIGHT_BRACE;
import static jupiter.common.util.Strings.BRACKETER;
import static jupiter.common.util.Strings.EMPTY;

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
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.list.row.Row;
import jupiter.common.struct.list.row.RowList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.time.Dates;
import jupiter.common.util.Arrays;
import jupiter.common.util.Booleans;
import jupiter.common.util.Bytes;
import jupiter.common.util.Classes;
import jupiter.common.util.Doubles;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Numbers;
import jupiter.common.util.Objects;
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
	 * Prevents the construction of {@link SQL}.
	 */
	protected SQL() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the parameter at the specified index in the specified {@link PreparedStatement} to the
	 * specified value.
	 * <p>
	 * @param statement the {@link PreparedStatement} containing the parameter to set
	 * @param index     the index of the parameter to set
	 * @param value     an {@link Object}
	 * <p>
	 * @throws SQLException if {@code index} does not correspond to a parameter marker in
	 *                      {@code statement}, if a database access error occurs or if this method
	 *                      is called on a closed {@link PreparedStatement}
	 */
	public static void setParameter(final PreparedStatement statement, final int index,
			final Object value)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(statement, "statement");
		Arguments.requireNonNull(value, "value");

		// Set the parameter of the SQL statement at the index
		if (value instanceof Array) {
			statement.setArray(index, (Array) value);
		} else if (value instanceof BigDecimal) {
			statement.setBigDecimal(index, (BigDecimal) value);
		} else if (value instanceof Blob) {
			statement.setBlob(index, (Blob) value);
		} else if (Booleans.is(value)) {
			statement.setBoolean(index, (Boolean) value);
		} else if (Bytes.is(value)) {
			statement.setByte(index, (Byte) value);
		} else if (Bytes.isPrimitiveArray(value)) {
			statement.setBytes(index, (byte[]) value);
		} else if (value instanceof Clob) {
			statement.setClob(index, (Clob) value);
		} else if (Dates.is(value)) {
			final java.util.Date date = (java.util.Date) value;
			if (Dates.hasTime(date)) {
				statement.setTimestamp(index, toSQLTimestamp(date));
			} else {
				statement.setDate(index, toSQLDate(date));
			}
		} else if (Doubles.is(value)) {
			statement.setDouble(index, (Double) value);
		} else if (Floats.is(value)) {
			statement.setFloat(index, (Float) value);
		} else if (Integers.is(value)) {
			statement.setInt(index, (Integer) value);
		} else if (Longs.is(value)) {
			statement.setLong(index, (Long) value);
		} else if (Shorts.is(value)) {
			statement.setShort(index, (Short) value);
		} else if (Strings.is(value)) {
			statement.setString(index, (String) value);
		} else if (value instanceof Time) {
			statement.setTime(index, (Time) value);
		} else if (value instanceof Timestamp) {
			statement.setTimestamp(index, (Timestamp) value);
		} else if (value instanceof URL) {
			statement.setURL(index, (URL) value);
		} else {
			throw new IllegalClassException(Classes.get(value));
		}
	}

	/**
	 * Sets the parameters of the specified {@link PreparedStatement}.
	 * <p>
	 * @param statement the {@link PreparedStatement} containing the parameters to set
	 * @param values    an array of {@link Object} (may be {@code null})
	 * <p>
	 * @throws SQLException if the {@code parameters} length is greater than the number of parameter
	 *                      markers in {@code statement}, if a database access error occurs or if
	 *                      this method is called on a closed {@link PreparedStatement}
	 */
	public static void setParameters(final PreparedStatement statement, final Object... values)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(values, "values");

		// Set the parameters of the SQL statement
		int index = 1;
		for (final Object parameter : values) {
			setParameter(statement, index++, parameter);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link Object} of the specified SQL {@link Class} converted from the specified
	 * {@link String}.
	 * <p>
	 * @param c    the SQL {@link Class} of the {@link Object} to convert to
	 * @param text the {@link String} to convert
	 * <p>
	 * @return an {@link Object} of the specified SQL {@link Class} converted from the specified
	 *         {@link String}
	 */
	public static Object convert(final Class<?> c, final String text) {
		// Check the arguments
		if (isNull(text)) {
			return null;
		}

		// Convert the text to an object of the SQL class
		if (Booleans.isFrom(c)) {
			return Integers.convert(text) == 1;
		} else if (Bytes.isPrimitiveArrayFrom(c)) {
			return text.getBytes();
		} else if (Date.class.isAssignableFrom(c)) {
			return Date.valueOf(text);
		} else if (Numbers.isFrom(c)) {
			return Numbers.toNumber(c, text);
		} else if (Strings.isFrom(c)) {
			return text;
		} else if (Time.class.isAssignableFrom(c)) {
			return Time.valueOf(text);
		} else if (Timestamp.class.isAssignableFrom(c)) {
			return Timestamp.valueOf(text);
		} else if (URL.class.isAssignableFrom(c)) {
			try {
				return new URL(text);
			} catch (final MalformedURLException ex) {
				throw new IllegalStateException(Objects.toString(ex), ex);
			}
		}
		throw new IllegalClassException(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a SQL {@link Date} converted from the specified {@link java.util.Date}, or
	 * {@code null} if the {@link java.util.Date} is {@code null}.
	 * <p>
	 * @param date the {@link java.util.Date} to convert (may be {@code null})
	 * <p>
	 * @return a SQL {@link Date} converted from the specified {@link java.util.Date}, or
	 *         {@code null} if the {@link java.util.Date} is {@code null}
	 */
	public static Date toSQLDate(final java.util.Date date) {
		return date == null ? null : new Date(date.getTime());
	}

	/**
	 * Returns a SQL {@link Timestamp} converted from the specified {@link java.util.Date}, or
	 * {@code null} if the {@link java.util.Date} is {@code null}.
	 * <p>
	 * @param date the {@link java.util.Date} to convert (may be {@code null})
	 * <p>
	 * @return a SQL {@link Timestamp} converted from the specified {@link java.util.Date}, or
	 *         {@code null} if the {@link java.util.Date} is {@code null}
	 */
	public static Timestamp toSQLTimestamp(final java.util.Date date) {
		return date == null ? null : new Timestamp(date.getTime());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String createStoredProcedureQuery(final String storedProcedure,
			final int parameterCount) {
		// Check the arguments
		Arguments.requireNonNull(storedProcedure, "stored procedure");

		// Create the SQL query for executing the SQL stored procedure
		return Strings.join(LEFT_BRACE, "call ", storedProcedure,
				Arrays.toString(Arrays.repeat('?', parameterCount)), RIGHT_BRACE);
	}

	public static CallableStatement createStoredProcedureStatement(final Connection connection,
			final String storedProcedure, final Object... parameters)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(storedProcedure, "stored procedure");
		Arguments.requireNonNull(parameters, "parameters");

		// Create the SQL statement for executing the SQL stored procedure
		final CallableStatement statement = connection.prepareCall(
				createStoredProcedureQuery(storedProcedure, parameters.length));
		// Set the parameters of the SQL statement
		setParameters(statement, parameters);
		// Return the SQL statement
		return statement;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String createSelectQuery(final String table) {
		return createSelectQuery(table, null, null);
	}

	public static String createSelectQuery(final String table, final String... columns) {
		return createSelectQuery(table, columns, null);
	}

	public static String createSelectQuery(final String table, final String[] columns,
			final String... conditionalColumns) {
		// Check the arguments
		Arguments.requireNonNull(table, "table");
		if (Arrays.isNullOrEmpty(conditionalColumns)) {
			IO.warn("No conditional columns for selecting the table ", Strings.quote(table));
		}

		// Create the SQL query for selecting the table with the columns and conditional columns
		return Strings.join("SELECT ",
				Arrays.isNonEmpty(columns) ? Strings.joinWith(columns, ",", BRACKETER) : "*",
				" FROM ", Strings.bracketize(table),
				Arrays.isNonEmpty(conditionalColumns) ?
				Strings.join(" WHERE ",
						Strings.joinWith(conditionalColumns, "=? AND ", BRACKETER)
								.concat("=?")) :
				EMPTY);
	}

	public static PreparedStatement createSelectStatement(final Connection connection,
			final String table)
			throws SQLException {
		return createSelectStatement(connection, table, null, null);
	}

	public static PreparedStatement createSelectStatement(final Connection connection,
			final String table, final String... columns)
			throws SQLException {
		return createSelectStatement(connection, table, columns, null);
	}

	public static PreparedStatement createSelectStatement(final Connection connection,
			final String table, final String[] columns, final String... conditionalColumns)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");

		// Create the SELECT query for selecting the table with the columns and conditional columns
		final PreparedStatement statement = connection.prepareStatement(
				createSelectQuery(table, columns, conditionalColumns));
		// Return the SELECT statement
		return statement;
	}

	//////////////////////////////////////////////

	public static String createInsertQuery(final String table, final String... columns) {
		// Check the arguments
		Arguments.requireNonNull(table, "table");
		ArrayArguments.requireNonEmpty(columns, "columns");

		// Create the SQL query for inserting into the table with the columns
		return Strings.join("INSERT INTO ", Strings.bracketize(table),
				" ", Arrays.toStringWith(columns, BRACKETER),
				" VALUES ", Arrays.toString(Arrays.repeat('?', columns.length)));
	}

	public static PreparedStatement createInsertStatement(final Connection connection,
			final String table, final String... columns)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");

		// Create the INSERT query for inserting into the table with the columns
		final PreparedStatement statement = connection.prepareStatement(
				createInsertQuery(table, columns), Statement.RETURN_GENERATED_KEYS);
		// Return the INSERT statement
		return statement;
	}

	//////////////////////////////////////////////

	public static String createUpdateQuery(final String table, final String... columns) {
		return createUpdateQuery(table, columns, null);
	}

	public static String createUpdateQuery(final String table, final String[] columns,
			final String... conditionalColumns) {
		// Check the arguments
		Arguments.requireNonNull(table, "table");
		ArrayArguments.requireNonEmpty(columns, "columns");
		if (Arrays.isNullOrEmpty(conditionalColumns)) {
			IO.warn("No conditional columns for updating the table ", Strings.quote(table));
		}

		// Create the SQL query for updating the table with the columns and conditional columns
		return Strings.join("UPDATE ", Strings.bracketize(table),
				" SET ", Strings.joinWith(columns, "=?,", BRACKETER).concat("=?"),
				Arrays.isNonEmpty(conditionalColumns) ?
				Strings.join(" WHERE ",
						Strings.joinWith(conditionalColumns, "=? AND ", BRACKETER)
								.concat("=?")) :
				EMPTY);
	}

	public static PreparedStatement createUpdateStatement(final Connection connection,
			final String table, final String... columns)
			throws SQLException {
		return createUpdateStatement(connection, table, columns, null);
	}

	public static PreparedStatement createUpdateStatement(final Connection connection,
			final String table, final String[] columns, final String... conditionalColumns)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");

		// Create the UPDATE query for updating the table with the columns and conditional columns
		final PreparedStatement statement = connection.prepareStatement(
				createUpdateQuery(table, columns, conditionalColumns));
		// Return the UPDATE statement
		return statement;
	}

	//////////////////////////////////////////////

	public static String createDeleteQuery(final String table) {
		return createDeleteQuery(table, null);
	}

	public static String createDeleteQuery(final String table, final String... conditionalColumns) {
		// Check the arguments
		Arguments.requireNonNull(table, "table");
		if (Arrays.isNullOrEmpty(conditionalColumns)) {
			IO.warn("No conditional columns for deleting the table ", Strings.quote(table));
		}

		// Create the SQL query for deleting the table with the conditional columns
		return Strings.join("DELETE FROM ", Strings.bracketize(table),
				Arrays.isNonEmpty(conditionalColumns) ?
				Strings.join(" WHERE ",
						Strings.joinWith(conditionalColumns, "=? AND ", BRACKETER)
								.concat("=?")) :
				EMPTY);
	}

	public static PreparedStatement createDeleteStatement(final Connection connection,
			final String table)
			throws SQLException {
		return createDeleteStatement(connection, table, null);
	}

	public static PreparedStatement createDeleteStatement(final Connection connection,
			final String table, final String... conditionalColumns)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");

		// Create the DELETE query for deleting the table with the conditional columns
		final PreparedStatement statement = connection.prepareStatement(
				createInsertQuery(table, conditionalColumns), Statement.RETURN_GENERATED_KEYS);
		// Return the DELETE statement
		return statement;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT} query using
	 * the specified {@link Connection}, or {@code null} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code SELECT} query to execute
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT} query using
	 *         the specified {@link Connection}, or {@code null} if there is a problem
	 */
	public static RowList select(final Connection connection, final String query) {
		return selectWith(connection, query, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns the rows of the specified table in a {@link RowList} using the specified
	 * {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to update
	 * <p>
	 * @return the rows of the specified table in a {@link RowList} using the specified
	 *         {@link Connection}
	 */
	public static RowList selectWith(final Connection connection, final String table) {
		return selectWith(connection, table, null, null, null);
	}

	/**
	 * Returns the specified columns of the rows of the specified table in a {@link RowList} using
	 * the specified {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to update
	 * @param columns    the columns of the rows to select (may be {@code null})
	 * <p>
	 * @return the specified columns of the rows of the specified table in a {@link RowList} using
	 *         the specified {@link Connection}
	 */
	public static RowList selectWith(final Connection connection,
			final String table, final String... columns) {
		return selectWith(connection, table, columns, null, null);
	}

	/**
	 * Returns the specified columns of the rows of the specified table where the specified
	 * conditional columns are equal to the conditional values in a {@link RowList} using the
	 * specified {@link Connection}.
	 * <p>
	 * @param connection         a {@link Connection} (session) to a database
	 * @param table              the table containing the rows to update
	 * @param columns            the columns of the rows to select (may be {@code null})
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalValues  the conditional values to filter (may be {@code null})
	 * <p>
	 * @return the specified columns of the rows of the specified table where the specified
	 *         conditional columns are equal to the conditional values in a {@link RowList} using
	 *         the specified {@link Connection}
	 */
	public static RowList selectWith(final Connection connection,
			final String table, final String[] columns, final String[] conditionalColumns,
			final Object... conditionalValues) {
		// Check the arguments
		if (Arrays.isNonEmpty(conditionalColumns) || Arrays.isNonEmpty(conditionalValues)) {
			ArrayArguments.requireSameLength(
					ArrayArguments.requireNonEmpty(conditionalColumns, "conditional columns"),
					ArrayArguments.requireNonEmpty(conditionalValues, "conditional values"));
		}

		// Execute the SQL query and return the selected rows
		return selectWith(connection, createSelectQuery(table, columns, conditionalColumns),
				conditionalValues);
	}

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT} query with
	 * the specified parameters using the specified {@link Connection}, or {@code null} if there is
	 * a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code SELECT} query to execute
	 * @param parameters the array of parameters of the {@code SELECT} query to execute (may be
	 *                   {@code null})
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT} query with
	 *         the specified parameters using the specified {@link Connection}, or {@code null} if
	 *         there is a problem
	 */
	public static RowList selectWith(final Connection connection,
			final String query, final Object... parameters) {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(query, "query");

		// Create the SQL statement for executing the SQL query
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			// Execute the SQL query and return the selected rows
			return selectWith(statement, parameters);
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
		return new RowList();
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link RowList} constructed by executing the specified SQL Data Manipulation
	 * Language (DML) {@code SELECT} {@link PreparedStatement}, or {@code null} if there is a
	 * problem.
	 * <p>
	 * @param statement the SQL Data Manipulation Language (DML) {@code SELECT}
	 *                  {@link PreparedStatement} to execute
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified SQL Data Manipulation
	 *         Language (DML) {@code SELECT} {@link PreparedStatement}, or {@code null} if there is
	 *         a problem
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static RowList select(final PreparedStatement statement)
			throws SQLException {
		return selectWith(statement, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns a {@link RowList} constructed by executing the specified SQL Data Manipulation
	 * Language (DML) {@code SELECT} {@link PreparedStatement} with the specified parameters, or
	 * {@code null} if there is a problem.
	 * <p>
	 * @param statement  the SQL Data Manipulation Language (DML) {@code SELECT}
	 *                   {@link PreparedStatement} to execute
	 * @param parameters the array of parameters of the SQL Data Manipulation Language (DML)
	 *                   {@code SELECT} {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified SQL Data Manipulation
	 *         Language (DML) {@code SELECT} {@link PreparedStatement} with the specified
	 *         parameters, or {@code null} if there is a problem
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static RowList selectWith(final PreparedStatement statement,
			final Object... parameters)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(statement, "statement");

		// Set the parameters of the SQL statement
		if (Arrays.isNonEmpty(parameters)) {
			setParameters(statement, parameters);
		}
		// Execute the SQL query
		final ResultSet resultSet = statement.executeQuery();
		// Get the header of the result
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int n = metaData.getColumnCount();
		final String[] header = new String[n];
		for (int i = 1; i <= n; ++i) {
			header[i - 1] = metaData.getColumnName(i);
		}
		// Store the result
		final RowList rowList = new RowList(header);
		while (resultSet.next()) {
			final Object[] values = new Object[n];
			for (int i = 0; i < n; ++i) {
				values[i] = resultSet.getObject(header[i]);
			}
			rowList.add(new Row(header, values));
		}
		// Return the selected rows
		return rowList;
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 * the specified {@code SELECT} query using the specified {@link Connection}, or {@code null} if
	 * there is a problem.
	 * <p>
	 * @param <E>        the element type of the {@link ExtendedList} to return (subtype of
	 *                   {@link SQLRow})
	 * @param c          the row {@link Class} of {@code E} type
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code SELECT} query to execute
	 * <p>
	 * @return an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 *         the specified {@code SELECT} query using the specified {@link Connection}, or
	 *         {@code null} if there is a problem
	 */
	public static <E extends SQLRow> ExtendedList<E> select(final Class<E> c,
			final Connection connection, final String query) {
		return selectWith(c, connection, query, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns the rows of the specified table in an {@link ExtendedList} of the specified row
	 * {@link Class} type using the specified {@link Connection}.
	 * <p>
	 * @param <E>        the element type of the {@link ExtendedList} to return (subtype of
	 *                   {@link SQLRow})
	 * @param c          the row {@link Class} of {@code E} type
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to update
	 * <p>
	 * @return the rows of the specified table in an {@link ExtendedList} of the specified row
	 *         {@link Class} type using the specified {@link Connection}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final Connection connection, final String table) {
		return selectWith(c, connection, table, null, null, null);
	}

	/**
	 * Returns the specified columns of the rows of the specified table in an {@link ExtendedList}
	 * of the specified row {@link Class} type using the specified {@link Connection}.
	 * <p>
	 * @param <E>        the element type of the {@link ExtendedList} to return (subtype of
	 *                   {@link SQLRow})
	 * @param c          the row {@link Class} of {@code E} type
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to update
	 * @param columns    the columns of the rows to select (may be {@code null})
	 * <p>
	 * @return the specified columns of the rows of the specified table in an {@link ExtendedList}
	 *         of the specified row {@link Class} type using the specified {@link Connection}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final Connection connection, final String table, final String... columns) {
		return selectWith(c, connection, table, columns, null, null);
	}

	/**
	 * Returns the specified columns of the rows of the specified table where the specified
	 * conditional columns are equal to the conditional values in an {@link ExtendedList} of the
	 * specified row {@link Class} type using the specified {@link Connection}.
	 * <p>
	 * @param <E>                the element type of the {@link ExtendedList} to return (subtype of
	 *                           {@link SQLRow})
	 * @param c                  the row {@link Class} of {@code E} type
	 * @param connection         a {@link Connection} (session) to a database
	 * @param table              the table containing the rows to update
	 * @param columns            the columns of the rows to select (may be {@code null})
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalValues  the conditional values to filter (may be {@code null})
	 * <p>
	 * @return the specified columns of the rows of the specified table where the specified
	 *         conditional columns are equal to the conditional values in an {@link ExtendedList} of
	 *         the specified row {@link Class} type using the specified {@link Connection}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final Connection connection, final String table, final String[] columns,
			final String[] conditionalColumns, final Object... conditionalValues) {
		// Check the arguments
		if (Arrays.isNonEmpty(conditionalColumns) || Arrays.isNonEmpty(conditionalValues)) {
			ArrayArguments.requireSameLength(
					ArrayArguments.requireNonEmpty(conditionalColumns, "conditional columns"),
					ArrayArguments.requireNonEmpty(conditionalValues, "conditional values"));
		}

		// Execute the SQL query and return the selected rows
		return selectWith(c, connection, createSelectQuery(table, columns, conditionalColumns),
				conditionalValues);
	}

	/**
	 * Returns an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 * the specified {@code SELECT} with the specified parameters query using the specified
	 * {@link Connection}, or {@code null} if there is a problem.
	 * <p>
	 * @param <E>        the element type of the {@link ExtendedList} to return (subtype of
	 *                   {@link SQLRow})
	 * @param c          the row {@link Class} of {@code E} type
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code SELECT} query to execute
	 * @param parameters the array of parameters of the {@code SELECT} query to execute (may be
	 *                   {@code null})
	 * <p>
	 * @return an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 *         the specified {@code SELECT} with the specified parameters query using the specified
	 *         {@link Connection}, or {@code null} if there is a problem
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final Connection connection, final String query, final Object... parameters) {
		// Check the arguments
		Arguments.requireNonNull(c, "class");
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(query, "query");

		// Create the SQL statement for executing the SQL query
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			// Execute the SQL query and return the selected rows
			return selectWith(c, statement, parameters);
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
		return new ExtendedList<E>();
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 * the specified SQL Data Manipulation Language (DML) {@code SELECT} {@link PreparedStatement},
	 * or {@code null} if there is a problem.
	 * <p>
	 * @param <E>       the element type of the {@link ExtendedList} to return (subtype of
	 *                  {@link SQLRow})
	 * @param c         the row {@link Class} of {@code E} type
	 * @param statement the SQL Data Manipulation Language (DML) {@code SELECT}
	 *                  {@link PreparedStatement} to execute
	 * <p>
	 * @return an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 *         the specified SQL Data Manipulation Language (DML) {@code SELECT}
	 *         {@link PreparedStatement}, or {@code null} if there is a problem
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static <E extends SQLRow> ExtendedList<E> select(final Class<E> c,
			final PreparedStatement statement)
			throws SQLException {
		return selectWith(c, statement, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 * the specified SQL Data Manipulation Language (DML) {@code SELECT} {@link PreparedStatement}
	 * with the specified parameters, or {@code null} if there is a problem.
	 * <p>
	 * @param <E>        the element type of the {@link ExtendedList} to return (subtype of
	 *                   {@link SQLRow})
	 * @param c          the row {@link Class} of {@code E} type
	 * @param statement  the SQL Data Manipulation Language (DML) {@code SELECT}
	 *                   {@link PreparedStatement} to execute
	 * @param parameters the array of parameters of the SQL Data Manipulation Language (DML)
	 *                   {@code SELECT} {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 *         the specified SQL Data Manipulation Language (DML) {@code SELECT}
	 *         {@link PreparedStatement} with the specified parameters, or {@code null} if there is
	 *         a problem
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final PreparedStatement statement, final Object... parameters)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(c, "class");
		Arguments.requireNonNull(statement, "statement");

		// Set the parameters of the SQL statement
		if (Arrays.isNonEmpty(parameters)) {
			setParameters(statement, parameters);
		}
		// Execute the SQL query
		final ExtendedList<E> rows = new ExtendedList<E>();
		try {
			final Constructor<E> constructor = c.getConstructor(ResultSet.class);
			final ResultSet resultSet = statement.executeQuery();
			// Store the result
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
			IO.error(ex, "No constructor with ", Objects.getName(ResultSet.class),
					" in ", Objects.getName(c), " found");
		} catch (final SecurityException ex) {
			IO.error(ex);
		}
		// Return the selected rows
		return rows;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT} stored
	 * procedure with the specified parameters using the specified {@link Connection}, or
	 * {@code null} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param name       the name of the {@code SELECT} stored procedure to execute
	 * @param parameters the array of parameters of the {@code SELECT} query to execute (may be
	 *                   {@code null})
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT} stored
	 *         procedure with the specified parameters using the specified {@link Connection}, or
	 *         {@code null} if there is a problem
	 */
	public static RowList selectWithStoredProcedure(final Connection connection,
			final String name, final Object... parameters) {
		// Check the arguments
		Arguments.requireNonNull(parameters, "parameters");

		// Execute the SQL stored procedure and return the selected rows
		return selectWith(connection, createStoredProcedureQuery(name, parameters.length),
				parameters);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns any auto-generated keys created by executing the specified {@code INSERT} query using
	 * the specified {@link Connection}, or {@code null} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code INSERT} query to execute
	 * <p>
	 * @return any auto-generated keys created by executing the specified {@code INSERT} query using
	 *         the specified {@link Connection}, or {@code null} if there is a problem
	 */
	public static long[] insert(final Connection connection, final String query) {
		return insertWith(connection, query, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns any auto-generated keys created by inserting the row with the specified columns
	 * containing the specified values into the specified table using the specified
	 * {@link Connection}, or {@code null} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table to insert into
	 * @param columns    the columns of the row to insert
	 * @param values     the values of the row to insert
	 * <p>
	 * @return any auto-generated keys created by inserting the row with the specified columns
	 *         containing the specified values into the specified table using the specified
	 *         {@link Connection}, or {@code null} if there is a problem
	 */
	public static long[] insertWith(final Connection connection,
			final String table, final String[] columns, final Object... values) {
		// Check the arguments
		ArrayArguments.requireSameLength(ArrayArguments.requireNonEmpty(columns, "columns"),
				ArrayArguments.requireNonEmpty(values, "values"));

		// Execute the SQL query and return any auto-generated keys
		return insertWith(connection, createInsertQuery(table, columns), values);
	}

	/**
	 * Returns any auto-generated keys created by executing the specified {@code INSERT} query with
	 * the specified parameters using the specified {@link Connection}, or {@code null} if there is
	 * a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code INSERT} query to execute
	 * @param parameters the array of parameters of the {@code INSERT} query to execute (may be
	 *                   {@code null})
	 * <p>
	 * @return any auto-generated keys created by executing the specified {@code INSERT} query with
	 *         the specified parameters using the specified {@link Connection}, or {@code null} if
	 *         there is a problem
	 */
	public static long[] insertWith(final Connection connection, final String query,
			final Object... parameters) {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(query, "query");

		// Create the SQL statement for executing the SQL query
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			// Execute the SQL query and return any auto-generated keys
			return insertWith(statement, parameters);
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
		return null;
	}

	//////////////////////////////////////////////

	/**
	 * Returns any auto-generated keys created by executing the specified SQL Data Manipulation
	 * Language (DML) {@code INSERT} {@link PreparedStatement}, or {@code null} if there is a
	 * problem.
	 * <p>
	 * @param statement the SQL Data Manipulation Language (DML) {@code INSERT}
	 *                  {@link PreparedStatement} to execute
	 * <p>
	 * @return any auto-generated keys created by executing the specified SQL Data Manipulation
	 *         Language (DML) {@code INSERT} {@link PreparedStatement}, or {@code null} if there is
	 *         a problem
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static long[] insert(final PreparedStatement statement)
			throws SQLException {
		return insertWith(statement, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns any auto-generated keys created by executing the specified SQL Data Manipulation
	 * Language (DML) {@code INSERT} {@link PreparedStatement} with the specified parameters, or
	 * {@code null} if there is a problem.
	 * <p>
	 * @param statement  the SQL Data Manipulation Language (DML) {@code INSERT}
	 *                   {@link PreparedStatement} to execute
	 * @param parameters the array of parameters of the SQL Data Manipulation Language (DML)
	 *                   {@code INSERT} {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return any auto-generated keys created by executing the specified SQL Data Manipulation
	 *         Language (DML) {@code INSERT} {@link PreparedStatement} with the specified
	 *         parameters, or {@code null} if there is a problem
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static long[] insertWith(final PreparedStatement statement, final Object... parameters)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(statement, "statement");

		// Set the parameters of the SQL statement
		if (Arrays.isNonEmpty(parameters)) {
			setParameters(statement, parameters);
		}
		// Execute the SQL query
		statement.executeUpdate();
		// Return any auto-generated keys
		final ResultSet resultSet = statement.getGeneratedKeys();
		final ExtendedLinkedList<Long> result = new ExtendedLinkedList<Long>();
		while (resultSet.next()) {
			result.add(resultSet.getLong(1));
		}
		return result.isEmpty() ? Longs.EMPTY_PRIMITIVE_ARRAY : (long[]) result.toPrimitiveArray();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of rows updated by executing the specified SQL query using the specified
	 * {@link Connection}, {@code 0} if nothing is returned, or {@code -1} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the SQL query to execute, such as {@code INSERT}, {@code UPDATE} or
	 *                   {@code DELETE}; or a SQL statement that returns nothing, such as a
	 *                   {@code DDL} statement
	 * <p>
	 * @return the number of rows updated by executing the specified SQL query using the specified
	 *         {@link Connection}, {@code 0} if nothing is returned, or {@code -1} if there is a
	 *         problem
	 */
	public static int update(final Connection connection, final String query) {
		return updateWith(connection, query, Objects.EMPTY_ARRAY);
	}

	/**
	 * Updates the rows of specified table by setting the specified columns to the specified values
	 * using the specified {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to update
	 * @param columns    the columns of the rows to update
	 * @param values     the values of the rows to update to
	 * <p>
	 * @return the number of updated rows, {@code 0} if nothing is returned, or {@code -1} if there
	 *         is a problem
	 */
	public static int updateWith(final Connection connection,
			final String table, final String[] columns, final Object... values) {
		return updateWith(connection, table, columns, values, null, null);
	}

	/**
	 * Updates the rows of specified table by setting the specified columns to the specified values
	 * where the specified conditional columns are equal to the conditional values using the
	 * specified {@link Connection}.
	 * <p>
	 * @param connection         a {@link Connection} (session) to a database
	 * @param table              the table containing the rows to update
	 * @param columns            the columns of the rows to update
	 * @param values             the values of the rows to update to
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalValues  the conditional values to filter (may be {@code null})
	 * <p>
	 * @return the number of updated rows, {@code 0} if nothing is returned, or {@code -1} if there
	 *         is a problem
	 */
	public static int updateWith(final Connection connection,
			final String table, final String[] columns, final Object[] values,
			final String[] conditionalColumns, final Object... conditionalValues) {
		// Check the arguments
		if (Arrays.isNonEmpty(conditionalColumns) || Arrays.isNonEmpty(conditionalValues)) {
			ArrayArguments.requireSameLength(
					ArrayArguments.requireNonEmpty(conditionalColumns, "conditional columns"),
					ArrayArguments.requireNonEmpty(conditionalValues, "conditional values"));
		}

		// Execute the SQL query and return the number of updated rows
		return updateWith(connection, createUpdateQuery(table, columns, conditionalColumns),
				Arrays.merge(values, conditionalValues));
	}

	/**
	 * Returns the number of rows updated by executing the specified SQL query with the specified
	 * parameters using the specified {@link Connection}, {@code 0} if nothing is returned, or
	 * {@code -1} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the SQL query to execute, such as {@code INSERT}, {@code UPDATE} or
	 *                   {@code DELETE}; or a SQL statement that returns nothing, such as a
	 *                   {@code DDL} statement
	 * @param parameters the array of parameters of the SQL Data Manipulation Language (DML)
	 *                   {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return the number of rows updated by executing the specified SQL query with the specified
	 *         parameters using the specified {@link Connection}, {@code 0} if nothing is returned,
	 *         or {@code -1} if there is a problem
	 */
	public static int updateWith(final Connection connection, final String query,
			final Object... parameters) {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(query, "query");

		// Create the SQL statement for executing the SQL query
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			// Execute the SQL query and return the number of updated rows
			return updateWith(statement, parameters);
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
	 * Returns the number of rows updated by executing the specified SQL Data Manipulation Language
	 * (DML) {@link PreparedStatement}, {@code 0} if nothing is returned, or {@code -1} if there is
	 * a problem.
	 * <p>
	 * @param statement the SQL Data Manipulation Language (DML) {@link PreparedStatement} to
	 *                  execute, such as {@code INSERT}, {@code UPDATE} or {@code DELETE}; or a SQL
	 *                  statement that returns nothing, such as a {@code DDL} statement
	 * <p>
	 * @return the number of rows updated by executing the specified SQL Data Manipulation Language
	 *         (DML) {@link PreparedStatement}, {@code 0} if nothing is returned, or {@code -1} if
	 *         there is a problem
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static int update(final PreparedStatement statement)
			throws SQLException {
		return updateWith(statement, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns the number of rows updated by executing the specified SQL Data Manipulation Language
	 * (DML) {@link PreparedStatement} with the specified parameters, {@code 0} if nothing is
	 * returned, or {@code -1} if there is a problem.
	 * <p>
	 * @param statement  the SQL Data Manipulation Language (DML) {@link PreparedStatement} to
	 *                   execute, such as {@code INSERT}, {@code UPDATE} or {@code DELETE}; or a SQL
	 *                   statement that returns nothing, such as a {@code DDL} statement
	 * @param parameters the array of parameters of the SQL Data Manipulation Language (DML)
	 *                   {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return the number of rows updated by executing the specified SQL Data Manipulation Language
	 *         (DML) {@link PreparedStatement} with the specified parameters, {@code 0} if nothing
	 *         is returned, or {@code -1} if there is a problem
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static int updateWith(final PreparedStatement statement, final Object... parameters)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(statement, "statement");

		// Set the parameters of the SQL statement
		if (Arrays.isNonEmpty(parameters)) {
			setParameters(statement, parameters);
		}
		// Execute the SQL query and return the number of updated rows
		return statement.executeUpdate();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of rows updated by executing the specified SQL stored procedure with the
	 * specified parameters using the specified {@link Connection}, {@code 0} if nothing is
	 * returned, or {@code -1} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param name       the name of the SQL stored procedure to execute, such as {@code INSERT},
	 *                   {@code UPDATE} or {@code DELETE}; or a SQL statement that returns nothing,
	 *                   such as a {@code DDL} statement
	 * @param parameters the array of parameters of the SQL Data Manipulation Language (DML)
	 *                   {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return the number of rows updated by executing the specified SQL stored procedure with the
	 *         specified parameters using the specified {@link Connection}, {@code 0} if nothing is
	 *         returned, or {@code -1} if there is a problem
	 */
	public static int updateWithStoredProcedure(final Connection connection,
			final String name, final Object... parameters) {
		// Check the arguments
		Arguments.requireNonNull(parameters, "parameters");

		// Execute the SQL stored procedure and return the number of updated rows
		return updateWith(connection, createStoredProcedureQuery(name, parameters.length),
				parameters);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of rows deleted by executing the specified {@code DELETE} query using the
	 * specified {@link Connection}, {@code 0} if nothing is returned, or {@code -1} if there is a
	 * problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code DELETE} query to execute
	 * <p>
	 * @return the number of rows deleted by executing the specified {@code DELETE} query using the
	 *         specified {@link Connection}, {@code 0} if nothing is returned, or {@code -1} if
	 *         there is a problem
	 */
	public static int delete(final Connection connection, final String query) {
		return update(connection, query);
	}

	/**
	 * Deletes the rows from the specified table using the specified {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to delete
	 * <p>
	 * @return the number of deleted rows, {@code 0} if nothing is returned, or {@code -1} if there
	 *         is a problem
	 */
	public static int deleteWith(final Connection connection, final String table) {
		return deleteWith(connection, table, null, null);
	}

	/**
	 * Deletes the rows from the specified table where the specified conditional columns are equal
	 * to the conditional values using the specified {@link Connection}.
	 * <p>
	 * @param connection         a {@link Connection} (session) to a database
	 * @param table              the table containing the rows to delete
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalValues  the conditional values to filter (may be {@code null})
	 * <p>
	 * @return the number of deleted rows, {@code 0} if nothing is returned, or {@code -1} if there
	 *         is a problem
	 */
	public static int deleteWith(final Connection connection, final String table,
			final String[] conditionalColumns, final Object... conditionalValues) {
		// Check the arguments
		if (Arrays.isNonEmpty(conditionalColumns) || Arrays.isNonEmpty(conditionalValues)) {
			ArrayArguments.requireSameLength(
					ArrayArguments.requireNonEmpty(conditionalColumns, "conditional columns"),
					ArrayArguments.requireNonEmpty(conditionalValues, "conditional values"));
		}

		// Execute the SQL query and return the number of deleted rows
		return updateWith(connection, createDeleteQuery(table, conditionalColumns),
				conditionalValues);
	}

	/**
	 * Returns the number of rows deleted by executing the specified {@code DELETE} query with the
	 * specified parameters using the specified {@link Connection}, {@code 0} if nothing is
	 * returned, or {@code -1} if there is a problem.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code DELETE} query to execute
	 * @param parameters the array of parameters of the SQL Data Manipulation Language (DML)
	 *                   {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return the number of rows deleted by executing the specified {@code DELETE} query with the
	 *         specified parameters using the specified {@link Connection}, {@code 0} if nothing is
	 *         returned, or {@code -1} if there is a problem
	 */
	public static int deleteWith(final Connection connection, final String query,
			final Object... parameters) {
		return updateWith(connection, query, parameters);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of rows deleted by executing the specified SQL Data Manipulation Language
	 * (DML) {@link PreparedStatement}, {@code 0} if nothing is returned, or {@code -1} if there is
	 * a problem.
	 * <p>
	 * @param statement the SQL Data Manipulation Language (DML) {@code DELETE}
	 *                  {@link PreparedStatement} to execute
	 * <p>
	 * @return the number of rows deleted by executing the specified SQL Data Manipulation Language
	 *         (DML) {@link PreparedStatement}, {@code 0} if nothing is returned, or {@code -1} if
	 *         there is a problem
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static int delete(final PreparedStatement statement)
			throws SQLException {
		return update(statement);
	}

	/**
	 * Returns the number of rows deleted by executing the specified SQL Data Manipulation Language
	 * (DML) {@link PreparedStatement} with the specified parameters, {@code 0} if nothing is
	 * returned, or {@code -1} if there is a problem.
	 * <p>
	 * @param statement  the SQL Data Manipulation Language (DML) {@code DELETE}
	 *                   {@link PreparedStatement} to execute
	 * @param parameters the array of parameters of the SQL Data Manipulation Language (DML)
	 *                   {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return the number of rows deleted by executing the specified SQL Data Manipulation Language
	 *         (DML) {@link PreparedStatement} with the specified parameters, {@code 0} if nothing
	 *         is returned, or {@code -1} if there is a problem
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static int deleteWith(final PreparedStatement statement, final Object... parameters)
			throws SQLException {
		return updateWith(statement, parameters);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is {@code null} or {@code "NULL"}.
	 * <p>
	 * @param object the {@link Object} to test (may be {@code null})
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
	 * @param text the {@link String} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link String} is {@code null} or {@code "NULL"},
	 *         {@code false} otherwise
	 */
	public static boolean isNull(final String text) {
		return text == null || NULL.equals(text);
	}
}
