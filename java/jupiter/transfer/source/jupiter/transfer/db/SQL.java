/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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
import static jupiter.common.properties.Jupiter.PROPERTIES;

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
import java.sql.Types;

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

	/**
	 * The default {@link DB}.
	 */
	public static final DB DEFAULT_DB = SQL.DB.StandardSQL;

	/**
	 * The {@link DB}.
	 */
	public static DB DB = SQL.DB.valueOf(PROPERTIES.getProperty("transfer.db",
			DEFAULT_DB.toString()));


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
	 * Returns the SQL types of all the columns of the specified table using the specified
	 * {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the columns to get the SQL types from
	 * <p>
	 * @return the SQL types of all the columns of the specified table using the specified
	 *         {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int[] getColumnTypes(final Connection connection, final String table)
			throws SQLException {
		return getColumnTypes(connection, table, Strings.EMPTY_ARRAY);
	}

	/**
	 * Returns the SQL types of the specified columns of the specified table using the specified
	 * {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the columns to get the SQL types from
	 * @param columns    the columns of the table to get the SQL types from (may be {@code null})
	 * <p>
	 * @return the SQL types of the specified columns of the specified table using the specified
	 *         {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int[] getColumnTypes(final Connection connection, final String table,
			final String[] columns)
			throws SQLException {
		if (DB == DB.MSSQL) {
			return MSSQL.getColumnTypes(connection, table, columns);
		}
		return StandardSQL.getColumnTypes(connection, table, columns);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the SQL types of all the parameters of the specified SQL procedure using the
	 * specified {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param procedure  the SQL procedure containing the parameters to get the SQL types from
	 * <p>
	 * @return the SQL types of all the parameters of the specified SQL procedure using the
	 *         specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int[] getProcedureParameterTypes(final Connection connection,
			final String procedure)
			throws SQLException {
		if (DB == DB.MSSQL) {
			return MSSQL.getProcedureParameterTypes(connection, procedure);
		}
		return StandardSQL.getProcedureParameterTypes(connection, procedure);
	}

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
		setParameter(statement, index, Types.NULL, value);
	}

	/**
	 * Sets the parameter at the specified index in the specified {@link PreparedStatement} to the
	 * specified value with the specified SQL type.
	 * <p>
	 * @param statement the {@link PreparedStatement} containing the parameter to set
	 * @param index     the index of the parameter to set
	 * @param type      the SQL type of the parameter to set
	 * @param value     an {@link Object}
	 * <p>
	 * @throws SQLException if {@code index} does not correspond to a parameter marker in
	 *                      {@code statement}, if a database access error occurs or if this method
	 *                      is called on a closed {@link PreparedStatement}
	 */
	public static void setParameter(final PreparedStatement statement, final int index,
			final int type, final Object value)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(statement, "statement");
		if (type == Types.NULL) {
			Arguments.requireNonNull(value, "parameter " + index);
		}

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
		} else if (type != Types.NULL) {
			statement.setNull(index, type);
		} else {
			throw new IllegalClassException(Classes.get(value));
		}
	}

	/**
	 * Sets the parameters of the specified {@link PreparedStatement} to the specified values.
	 * <p>
	 * @param statement the {@link PreparedStatement} containing the parameters to set
	 * @param values    an array of {@link Object} (may be {@code null})
	 * <p>
	 * @throws SQLException if the {@code values} length is greater than the number of parameter
	 *                      markers in {@code statement}, if a database access error occurs or if
	 *                      this method is called on a closed {@link PreparedStatement}
	 */
	public static void setParameters(final PreparedStatement statement, final Object... values)
			throws SQLException {
		setParameters(statement, Integers.EMPTY_PRIMITIVE_ARRAY, values);
	}

	/**
	 * Sets the parameters of the specified {@link PreparedStatement} to the specified values with
	 * the specified SQL types.
	 * <p>
	 * @param statement the {@link PreparedStatement} containing the parameters to set
	 * @param types     the {@code int} array containing the SQL types of the parameters to set (may
	 *                  be {@code null})
	 * @param values    an array of {@link Object} (may be {@code null})
	 * <p>
	 * @throws SQLException if the {@code values} length is greater than the number of parameter
	 *                      markers in {@code statement}, if a database access error occurs or if
	 *                      this method is called on a closed {@link PreparedStatement}
	 */
	public static void setParameters(final PreparedStatement statement, final int[] types,
			final Object... values)
			throws SQLException {
		// Check the arguments
		if (Integers.isNonEmpty(types)) {
			ArrayArguments.requireSameLength(
					ArrayArguments.requireNonEmpty(values, "parameter values"), types.length);
		}
		Arguments.requireNonNull(values, "parameter values");

		// Set the parameters of the SQL statement
		for (int i = 0; i < values.length; ++i) {
			if (Integers.isNonEmpty(types)) {
				setParameter(statement, i + 1, types[i], values[i]);
			} else {
				setParameter(statement, i + 1, values[i]);
			}
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
			return Integer.valueOf(text) == 1;
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

	public static String createSelectQuery(final String table) {
		return createSelectQuery(table, Strings.EMPTY_ARRAY, Strings.EMPTY_ARRAY);
	}

	public static String createSelectQuery(final String table, final String... columns) {
		return createSelectQuery(table, columns, Strings.EMPTY_ARRAY);
	}

	public static String createSelectQuery(final String table, final String[] columns,
			final String... conditionalColumns) {
		if (DB == DB.MSSQL) {
			return MSSQL.createSelectQuery(table, columns, conditionalColumns);
		}
		return StandardSQL.createSelectQuery(table, columns, conditionalColumns);
	}

	public static PreparedStatement createSelectStatement(final Connection connection,
			final String table)
			throws SQLException {
		return createSelectStatement(connection, table, Strings.EMPTY_ARRAY, Strings.EMPTY_ARRAY);
	}

	public static PreparedStatement createSelectStatement(final Connection connection,
			final String table, final String... columns)
			throws SQLException {
		return createSelectStatement(connection, table, columns, Strings.EMPTY_ARRAY);
	}

	public static PreparedStatement createSelectStatement(final Connection connection,
			final String table, final String[] columns, final String... conditionalColumns)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");

		// Create the SQL statement for selecting the table with the columns and conditional columns
		return connection.prepareStatement(createSelectQuery(table, columns, conditionalColumns));
	}

	//////////////////////////////////////////////

	public static String createInsertQuery(final String table, final String... columns) {
		if (DB == DB.MSSQL) {
			return MSSQL.createInsertQuery(table, columns);
		}
		return StandardSQL.createInsertQuery(table, columns);
	}

	public static PreparedStatement createInsertStatement(final Connection connection,
			final String table, final String... columns)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");

		// Create the SQL statement for inserting into the table with the columns
		return connection.prepareStatement(createInsertQuery(table, columns),
				Statement.RETURN_GENERATED_KEYS);
	}

	//////////////////////////////////////////////

	public static String createUpdateQuery(final String table, final String... columns) {
		return createUpdateQuery(table, columns, Strings.EMPTY_ARRAY);
	}

	public static String createUpdateQuery(final String table, final String[] columns,
			final String... conditionalColumns) {
		if (DB == DB.MSSQL) {
			return MSSQL.createUpdateQuery(table, columns, conditionalColumns);
		}
		return StandardSQL.createUpdateQuery(table, columns, conditionalColumns);
	}

	public static PreparedStatement createUpdateStatement(final Connection connection,
			final String table, final String... columns)
			throws SQLException {
		return createUpdateStatement(connection, table, columns, Strings.EMPTY_ARRAY);
	}

	public static PreparedStatement createUpdateStatement(final Connection connection,
			final String table, final String[] columns, final String... conditionalColumns)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");

		// Create the SQL statement for updating the table with the columns and conditional columns
		return connection.prepareStatement(createUpdateQuery(table, columns, conditionalColumns));
	}

	//////////////////////////////////////////////

	public static String createDeleteQuery(final String table) {
		return createDeleteQuery(table, Strings.EMPTY_ARRAY);
	}

	public static String createDeleteQuery(final String table, final String... conditionalColumns) {
		if (DB == DB.MSSQL) {
			return MSSQL.createDeleteQuery(table, conditionalColumns);
		}
		return StandardSQL.createDeleteQuery(table, conditionalColumns);
	}

	public static PreparedStatement createDeleteStatement(final Connection connection,
			final String table)
			throws SQLException {
		return createDeleteStatement(connection, table, Strings.EMPTY_ARRAY);
	}

	public static PreparedStatement createDeleteStatement(final Connection connection,
			final String table, final String... conditionalColumns)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");

		// Create the SQL statement for deleting the table with the conditional columns
		return connection.prepareStatement(createDeleteQuery(table, conditionalColumns),
				Statement.RETURN_GENERATED_KEYS);
	}

	//////////////////////////////////////////////

	public static String createProcedureQuery(final String procedure, final int parameterCount) {
		if (DB == DB.MSSQL) {
			return MSSQL.createProcedureQuery(procedure, parameterCount);
		}
		return StandardSQL.createProcedureQuery(procedure, parameterCount);
	}

	public static CallableStatement createProcedureStatement(final Connection connection,
			final String procedure, final Object... parameterValues)
			throws SQLException {
		return createProcedureStatement(connection, procedure,
				Integers.EMPTY_PRIMITIVE_ARRAY, parameterValues);
	}

	public static CallableStatement createProcedureStatement(final Connection connection,
			final String procedure, final int[] parameterTypes,
			final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(procedure, "procedure");
		Arguments.requireNonNull(parameterValues, "parameter values");

		// Create the SQL statement for executing the SQL procedure
		final CallableStatement statement = connection.prepareCall(
				createProcedureQuery(procedure, parameterValues.length));
		// Set the parameters of the SQL statement
		setParameters(statement, parameterTypes, parameterValues);
		// Return the SQL statement
		return statement;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT} query using
	 * the specified {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code SELECT} query to execute
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT} query using
	 *         the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static RowList select(final Connection connection, final String query)
			throws SQLException {
		return selectWith(connection, query, Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns the rows of the specified table in a {@link RowList} using the specified
	 * {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to select
	 * <p>
	 * @return the rows of the specified table in a {@link RowList} using the specified
	 *         {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static RowList selectWith(final Connection connection, final String table)
			throws SQLException {
		return selectWith(connection, table, Strings.EMPTY_ARRAY, Strings.EMPTY_ARRAY,
				Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns the specified columns of the rows of the specified table in a {@link RowList} using
	 * the specified {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to select
	 * @param columns    the columns of the rows to select (may be {@code null})
	 * <p>
	 * @return the specified columns of the rows of the specified table in a {@link RowList} using
	 *         the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static RowList selectWith(final Connection connection,
			final String table, final String... columns)
			throws SQLException {
		return selectWith(connection, table, columns, Strings.EMPTY_ARRAY,
				Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns the specified columns of the rows of the specified table where the specified
	 * conditional columns are equal to the conditional values in a {@link RowList} using the
	 * specified {@link Connection}.
	 * <p>
	 * @param connection         a {@link Connection} (session) to a database
	 * @param table              the table containing the rows to select
	 * @param columns            the columns of the rows to select (may be {@code null})
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalValues  the values of the conditional columns to filter (may be
	 *                           {@code null})
	 * <p>
	 * @return the specified columns of the rows of the specified table where the specified
	 *         conditional columns are equal to the conditional values in a {@link RowList} using
	 *         the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static RowList selectWith(final Connection connection,
			final String table, final String[] columns, final String[] conditionalColumns,
			final Object... conditionalValues)
			throws SQLException {
		return selectWith(connection, table, columns, conditionalColumns,
				getColumnTypes(connection, table, conditionalColumns), conditionalValues);
	}

	/**
	 * Returns the specified columns of the rows of the specified table where the specified
	 * conditional columns are equal to the conditional values with the specified SQL types in a
	 * {@link RowList} using the specified {@link Connection}.
	 * <p>
	 * @param connection         a {@link Connection} (session) to a database
	 * @param table              the table containing the rows to select
	 * @param columns            the columns of the rows to select (may be {@code null})
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalTypes   the {@code int} array containing the SQL types of the conditional
	 *                           columns to filter (may be {@code null})
	 * @param conditionalValues  the values of the conditional columns to filter (may be
	 *                           {@code null})
	 * <p>
	 * @return the specified columns of the rows of the specified table where the specified
	 *         conditional columns are equal to the conditional values with the specified SQL types
	 *         in a {@link RowList} using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static RowList selectWith(final Connection connection,
			final String table, final String[] columns, final String[] conditionalColumns,
			final int[] conditionalTypes, final Object... conditionalValues)
			throws SQLException {
		// Check the arguments
		if (Arrays.isNonEmpty(conditionalColumns) || Arrays.isNonEmpty(conditionalValues)) {
			ArrayArguments.requireSameLength(
					ArrayArguments.requireNonEmpty(conditionalColumns, "conditional columns"),
					ArrayArguments.requireNonEmpty(conditionalValues, "conditional values"));
		}

		// Execute the SQL query and return the selected rows
		return selectWith(connection, createSelectQuery(table, columns, conditionalColumns),
				conditionalTypes, conditionalValues);
	}

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT} query with
	 * the specified parameter values using the specified {@link Connection}.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param query           the {@code SELECT} query to execute
	 * @param parameterValues the array of values of the parameters of the {@code SELECT} query to
	 *                        execute (may be {@code null})
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT} query with
	 *         the specified parameter values using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static RowList selectWith(final Connection connection, final String query,
			final Object... parameterValues)
			throws SQLException {
		return selectWith(connection, query, Integers.EMPTY_PRIMITIVE_ARRAY, parameterValues);
	}

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT} query with
	 * the specified parameter SQL types and values using the specified {@link Connection}.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param query           the {@code SELECT} query to execute
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters of
	 *                        the {@code SELECT} query to execute (may be {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code SELECT} query to
	 *                        execute (may be {@code null})
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT} query with
	 *         the specified parameter SQL types and values using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static RowList selectWith(final Connection connection,
			final String query, final int[] parameterTypes, final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(query, "query");

		// Create the SQL statement for executing the SQL query
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			// Execute the SQL query and return the selected rows
			return selectWith(statement, parameterTypes, parameterValues);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (final SQLException ex) {
					IO.error(ex);
				}
			}
		}
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT}
	 * {@link PreparedStatement}.
	 * <p>
	 * @param statement the SQL Data Manipulation Language (DML) {@code SELECT}
	 *                  {@link PreparedStatement} to execute
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT}
	 *         {@link PreparedStatement}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static RowList select(final PreparedStatement statement)
			throws SQLException {
		return selectWith(statement, Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT}
	 * {@link PreparedStatement} with the specified parameter values.
	 * <p>
	 * @param statement       the SQL Data Manipulation Language (DML) {@code SELECT}
	 *                        {@link PreparedStatement} to execute
	 * @param parameterValues the array of values of the parameters of the {@code SELECT}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT}
	 *         {@link PreparedStatement} with the specified parameter values
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static RowList selectWith(final PreparedStatement statement,
			final Object... parameterValues)
			throws SQLException {
		return selectWith(statement, Integers.EMPTY_PRIMITIVE_ARRAY, parameterValues);
	}

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT}
	 * {@link PreparedStatement} with the specified parameter SQL types and values.
	 * <p>
	 * @param statement       the SQL Data Manipulation Language (DML) {@code SELECT}
	 *                        {@link PreparedStatement} to execute
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters of
	 *                        the {@code SELECT} {@link PreparedStatement} to execute (may be
	 *                        {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code SELECT}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT}
	 *         {@link PreparedStatement} with the specified parameter SQL types and values
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static RowList selectWith(final PreparedStatement statement, final int[] parameterTypes,
			final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(statement, "statement");

		// Set the parameters of the SQL statement
		if (Arrays.isNonEmpty(parameterValues)) {
			setParameters(statement, parameterTypes, parameterValues);
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
			final Object[] row = new Object[n];
			for (int i = 0; i < n; ++i) {
				row[i] = resultSet.getObject(header[i]);
			}
			rowList.add(new Row(header, row));
		}
		// Return the selected rows
		return rowList;
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 * the specified {@code SELECT} query using the specified {@link Connection}.
	 * <p>
	 * @param <E>        the element type of the {@link ExtendedList} to return (subtype of
	 *                   {@link SQLRow})
	 * @param c          the row {@link Class} of {@code E} type
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code SELECT} query to execute
	 * <p>
	 * @return an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 *         the specified {@code SELECT} query using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static <E extends SQLRow> ExtendedList<E> select(final Class<E> c,
			final Connection connection, final String query)
			throws SQLException {
		return selectWith(c, connection, query, Integers.EMPTY_PRIMITIVE_ARRAY,
				Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns the rows of the specified table in an {@link ExtendedList} of the specified row
	 * {@link Class} type using the specified {@link Connection}.
	 * <p>
	 * @param <E>        the element type of the {@link ExtendedList} to return (subtype of
	 *                   {@link SQLRow})
	 * @param c          the row {@link Class} of {@code E} type
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to select
	 * <p>
	 * @return the rows of the specified table in an {@link ExtendedList} of the specified row
	 *         {@link Class} type using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final Connection connection, final String table)
			throws SQLException {
		return selectWith(c, connection, table, Strings.EMPTY_ARRAY, Strings.EMPTY_ARRAY,
				Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns the specified columns of the rows of the specified table in an {@link ExtendedList}
	 * of the specified row {@link Class} type using the specified {@link Connection}.
	 * <p>
	 * @param <E>        the element type of the {@link ExtendedList} to return (subtype of
	 *                   {@link SQLRow})
	 * @param c          the row {@link Class} of {@code E} type
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to select
	 * @param columns    the columns of the rows to select (may be {@code null})
	 * <p>
	 * @return the specified columns of the rows of the specified table in an {@link ExtendedList}
	 *         of the specified row {@link Class} type using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final Connection connection, final String table, final String... columns)
			throws SQLException {
		return selectWith(c, connection, table, columns, Strings.EMPTY_ARRAY,
				Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
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
	 * @param table              the table containing the rows to select
	 * @param columns            the columns of the rows to select (may be {@code null})
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalValues  the values of the conditional columns to filter (may be
	 *                           {@code null})
	 * <p>
	 * @return the specified columns of the rows of the specified table where the specified
	 *         conditional columns are equal to the conditional values in an {@link ExtendedList} of
	 *         the specified row {@link Class} type using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final Connection connection, final String table, final String[] columns,
			final String[] conditionalColumns, final Object... conditionalValues)
			throws SQLException {
		return selectWith(c, connection, table, columns, conditionalColumns,
				getColumnTypes(connection, table, conditionalColumns), conditionalValues);
	}

	/**
	 * Returns the specified columns of the rows of the specified table where the specified
	 * conditional columns are equal to the conditional values with the specified SQL types in an
	 * {@link ExtendedList} of the specified row {@link Class} type using the specified
	 * {@link Connection}.
	 * <p>
	 * @param <E>                the element type of the {@link ExtendedList} to return (subtype of
	 *                           {@link SQLRow})
	 * @param c                  the row {@link Class} of {@code E} type
	 * @param connection         a {@link Connection} (session) to a database
	 * @param table              the table containing the rows to select
	 * @param columns            the columns of the rows to select (may be {@code null})
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalTypes   the {@code int} array containing the SQL types of the conditional
	 *                           columns to filter (may be {@code null})
	 * @param conditionalValues  the values of the conditional columns to filter (may be
	 *                           {@code null})
	 * <p>
	 * @return the specified columns of the rows of the specified table where the specified
	 *         conditional columns are equal to the conditional values with the specified SQL types
	 *         in an {@link ExtendedList} of the specified row {@link Class} type using the
	 *         specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final Connection connection, final String table, final String[] columns,
			final String[] conditionalColumns, final int[] conditionalTypes,
			final Object... conditionalValues)
			throws SQLException {
		// Check the arguments
		if (Arrays.isNonEmpty(conditionalColumns) || Arrays.isNonEmpty(conditionalValues)) {
			ArrayArguments.requireSameLength(
					ArrayArguments.requireNonEmpty(conditionalColumns, "conditional columns"),
					ArrayArguments.requireNonEmpty(conditionalValues, "conditional values"));
		}

		// Execute the SQL query and return the selected rows
		return selectWith(c, connection, createSelectQuery(table, columns, conditionalColumns),
				conditionalTypes, conditionalValues);
	}

	/**
	 * Returns an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 * the specified {@code SELECT} query with the specified parameter values using the specified
	 * {@link Connection}.
	 * <p>
	 * @param <E>             the element type of the {@link ExtendedList} to return (subtype of
	 *                        {@link SQLRow})
	 * @param c               the row {@link Class} of {@code E} type
	 * @param connection      a {@link Connection} (session) to a database
	 * @param query           the {@code SELECT} query to execute
	 * @param parameterValues the array of values of the parameters of the {@code SELECT} query to
	 *                        execute (may be {@code null})
	 * <p>
	 * @return an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 *         the specified {@code SELECT} query with the specified parameter values using the
	 *         specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final Connection connection, final String query, final Object... parameterValues)
			throws SQLException {
		return selectWith(c, connection, query, Integers.EMPTY_PRIMITIVE_ARRAY, parameterValues);
	}

	/**
	 * Returns an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 * the specified {@code SELECT} query with the specified parameter SQL types and values using
	 * the specified {@link Connection}.
	 * <p>
	 * @param <E>             the element type of the {@link ExtendedList} to return (subtype of
	 *                        {@link SQLRow})
	 * @param c               the row {@link Class} of {@code E} type
	 * @param connection      a {@link Connection} (session) to a database
	 * @param query           the {@code SELECT} query to execute
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters of
	 *                        the {@code SELECT} query to execute (may be {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code SELECT} query to
	 *                        execute (may be {@code null})
	 * <p>
	 * @return an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 *         the specified {@code SELECT} query with the specified parameter SQL types and values
	 *         using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final Connection connection, final String query, final int[] parameterTypes,
			final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(c, "class");
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(query, "query");

		// Create the SQL statement for executing the SQL query
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			// Execute the SQL query and return the selected rows
			return selectWith(c, statement, parameterTypes, parameterValues);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (final SQLException ex) {
					IO.error(ex);
				}
			}
		}
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 * the specified {@code SELECT} {@link PreparedStatement}.
	 * <p>
	 * @param <E>       the element type of the {@link ExtendedList} to return (subtype of
	 *                  {@link SQLRow})
	 * @param c         the row {@link Class} of {@code E} type
	 * @param statement the SQL Data Manipulation Language (DML) {@code SELECT}
	 *                  {@link PreparedStatement} to execute
	 * <p>
	 * @return an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 *         the specified {@code SELECT} {@link PreparedStatement}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static <E extends SQLRow> ExtendedList<E> select(final Class<E> c,
			final PreparedStatement statement)
			throws SQLException {
		return selectWith(c, statement, Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 * the specified {@code SELECT} {@link PreparedStatement} with the specified parameter values.
	 * <p>
	 * @param <E>             the element type of the {@link ExtendedList} to return (subtype of
	 *                        {@link SQLRow})
	 * @param c               the row {@link Class} of {@code E} type
	 * @param statement       the SQL Data Manipulation Language (DML) {@code SELECT}
	 *                        {@link PreparedStatement} to execute
	 * @param parameterValues the array of values of the parameters of the {@code SELECT}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 *         the specified {@code SELECT} {@link PreparedStatement} with the specified parameter
	 *         values
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final PreparedStatement statement, final Object... parameterValues)
			throws SQLException {
		return selectWith(c, statement, Integers.EMPTY_PRIMITIVE_ARRAY, parameterValues);
	}

	/**
	 * Returns an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 * the specified {@code SELECT} {@link PreparedStatement} with the specified parameter SQL types
	 * and values.
	 * <p>
	 * @param <E>             the element type of the {@link ExtendedList} to return (subtype of
	 *                        {@link SQLRow})
	 * @param c               the row {@link Class} of {@code E} type
	 * @param statement       the SQL Data Manipulation Language (DML) {@code SELECT}
	 *                        {@link PreparedStatement} to execute
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters of
	 *                        the {@code SELECT} {@link PreparedStatement} to execute (may be
	 *                        {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code SELECT}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return an {@link ExtendedList} of the specified row {@link Class} constructed by executing
	 *         the specified {@code SELECT} {@link PreparedStatement} with the specified parameter
	 *         SQL types and values
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static <E extends SQLRow> ExtendedList<E> selectWith(final Class<E> c,
			final PreparedStatement statement, final int[] parameterTypes,
			final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(c, "class");
		Arguments.requireNonNull(statement, "statement");

		// Set the parameters of the SQL statement
		if (Arrays.isNonEmpty(parameterValues)) {
			setParameters(statement, parameterTypes, parameterValues);
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
			IO.error(ex, "No constructor with", Objects.getName(ResultSet.class),
					"in", Objects.getName(c), "found");
		} catch (final SecurityException ex) {
			IO.error(ex);
		}
		// Return the selected rows
		return rows;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT} procedure
	 * with the specified parameter values using the specified {@link Connection}.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param procedure       the {@code SELECT} procedure to execute
	 * @param parameterValues the array of values of the parameters of the {@code SELECT} procedure
	 *                        to execute (may be {@code null})
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT} procedure
	 *         with the specified parameter values using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static RowList selectWithProcedure(final Connection connection, final String procedure,
			final Object... parameterValues)
			throws SQLException {
		return selectWithProcedure(connection, procedure,
				getProcedureParameterTypes(connection, procedure), parameterValues);
	}

	/**
	 * Returns a {@link RowList} constructed by executing the specified {@code SELECT} procedure
	 * with the specified parameter SQL types and values using the specified {@link Connection}.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param procedure       the {@code SELECT} procedure to execute
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters of
	 *                        the {@code SELECT} procedure to execute (may be {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code SELECT} procedure
	 *                        to execute (may be {@code null})
	 * <p>
	 * @return a {@link RowList} constructed by executing the specified {@code SELECT} procedure
	 *         with the specified parameter SQL types and values using the specified
	 *         {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static RowList selectWithProcedure(final Connection connection, final String procedure,
			final int[] parameterTypes, final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(parameterValues, "parameter values");

		// Execute the SQL procedure and return the selected rows
		return selectWith(connection, createProcedureQuery(procedure, parameterValues.length),
				Integers.take(parameterTypes, 0, parameterValues.length), parameterValues);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns any auto-generated keys created by executing the specified {@code INSERT} query using
	 * the specified {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code INSERT} query to execute
	 * <p>
	 * @return any auto-generated keys created by executing the specified {@code INSERT} query using
	 *         the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static long[] insert(final Connection connection, final String query)
			throws SQLException {
		return insertWith(connection, query, Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns any auto-generated keys created by inserting the row with the specified columns
	 * containing the specified values into the specified table using the specified
	 * {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table to insert into
	 * @param columns    the columns of the row to insert
	 * @param values     the values of the row to insert
	 * <p>
	 * @return any auto-generated keys created by inserting the row with the specified columns
	 *         containing the specified values into the specified table using the specified
	 *         {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static long[] insertWith(final Connection connection,
			final String table, final String[] columns, final Object... values)
			throws SQLException {
		return insertWith(connection, table, columns, getColumnTypes(connection, table, columns),
				values);
	}

	/**
	 * Returns any auto-generated keys created by inserting the row with the specified columns
	 * containing the specified values with the specified SQL types into the specified table using
	 * the specified {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table to insert into
	 * @param columns    the columns of the row to insert
	 * @param types      the {@code int} array containing the SQL types of the row to insert (may be
	 *                   {@code null})
	 * @param values     the values of the row to insert
	 * <p>
	 * @return any auto-generated keys created by inserting the row with the specified columns
	 *         containing the specified values with the specified SQL types into the specified table
	 *         using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static long[] insertWith(final Connection connection, final String table,
			final String[] columns, final int[] types, final Object... values)
			throws SQLException {
		// Check the arguments
		ArrayArguments.requireSameLength(ArrayArguments.requireNonEmpty(columns, "columns"),
				ArrayArguments.requireNonEmpty(values, "values"));

		// Execute the SQL query and return any auto-generated keys
		return insertWith(connection, createInsertQuery(table, columns), types, values);
	}

	/**
	 * Returns any auto-generated keys created by executing the specified {@code INSERT} query with
	 * the specified parameter values using the specified {@link Connection}.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param query           the {@code INSERT} query to execute
	 * @param parameterValues the array of values of the parameters of the {@code INSERT} query to
	 *                        execute (may be {@code null})
	 * <p>
	 * @return any auto-generated keys created by executing the specified {@code INSERT} query with
	 *         the specified parameter values using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static long[] insertWith(final Connection connection, final String query,
			final Object... parameterValues)
			throws SQLException {
		return insertWith(connection, query, Integers.EMPTY_PRIMITIVE_ARRAY, parameterValues);
	}

	/**
	 * Returns any auto-generated keys created by executing the specified {@code INSERT} query with
	 * the specified parameter SQL types and values using the specified {@link Connection}.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param query           the {@code INSERT} query to execute
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters of
	 *                        the {@code INSERT} query to execute (may be {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code INSERT} query to
	 *                        execute (may be {@code null})
	 * <p>
	 * @return any auto-generated keys created by executing the specified {@code INSERT} query with
	 *         the specified parameter SQL types and values using the specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static long[] insertWith(final Connection connection, final String query,
			final int[] parameterTypes, final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(query, "query");

		// Create the SQL statement for executing the SQL query
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			// Execute the SQL query and return any auto-generated keys
			return insertWith(statement, parameterTypes, parameterValues);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (final SQLException ex) {
					IO.error(ex);
				}
			}
		}
	}

	//////////////////////////////////////////////

	/**
	 * Returns any auto-generated keys created by executing the specified {@code INSERT}
	 * {@link PreparedStatement}.
	 * <p>
	 * @param statement the SQL Data Manipulation Language (DML) {@code INSERT}
	 *                  {@link PreparedStatement} to execute
	 * <p>
	 * @return any auto-generated keys created by executing the specified {@code INSERT}
	 *         {@link PreparedStatement}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static long[] insert(final PreparedStatement statement)
			throws SQLException {
		return insertWith(statement, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns any auto-generated keys created by executing the specified {@code INSERT}
	 * {@link PreparedStatement} with the specified parameter values.
	 * <p>
	 * @param statement       the SQL Data Manipulation Language (DML) {@code INSERT}
	 *                        {@link PreparedStatement} to execute
	 * @param parameterValues the array of values of the parameters of the {@code INSERT}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return any auto-generated keys created by executing the specified {@code INSERT}
	 *         {@link PreparedStatement} with the specified parameter values
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static long[] insertWith(final PreparedStatement statement,
			final Object... parameterValues)
			throws SQLException {
		return insertWith(statement, Integers.EMPTY_PRIMITIVE_ARRAY, parameterValues);
	}

	/**
	 * Returns any auto-generated keys created by executing the specified {@code INSERT}
	 * {@link PreparedStatement} with the specified parameter SQL types and values.
	 * <p>
	 * @param statement       the SQL Data Manipulation Language (DML) {@code INSERT}
	 *                        {@link PreparedStatement} to execute
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters of
	 *                        the {@code INSERT} {@link PreparedStatement} to execute (may be
	 *                        {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code INSERT}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return any auto-generated keys created by executing the specified {@code INSERT}
	 *         {@link PreparedStatement} with the specified parameter SQL types and values
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static long[] insertWith(final PreparedStatement statement, final int[] parameterTypes,
			final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(statement, "statement");

		// Set the parameters of the SQL statement
		if (Arrays.isNonEmpty(parameterValues)) {
			setParameters(statement, parameterTypes, parameterValues);
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
	 * Returns the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE}
	 * or {@code DELETE} or {@code DDL} query using the specified {@link Connection}, or {@code 0}
	 * if nothing is returned.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the SQL query to execute, such as {@code INSERT}, {@code UPDATE} or
	 *                   {@code DELETE}; or a SQL statement that returns nothing, such as a
	 *                   {@code DDL} statement
	 * <p>
	 * @return the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE}
	 *         or {@code DELETE} or {@code DDL} query using the specified {@link Connection}, or
	 *         {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int update(final Connection connection, final String query)
			throws SQLException {
		return updateWith(connection, query, Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
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
	 * @return the number of updated rows, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int updateWith(final Connection connection,
			final String table, final String[] columns, final Object... values)
			throws SQLException {
		return updateWith(connection, table, columns, values, Strings.EMPTY_ARRAY,
				Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
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
	 * @param conditionalValues  the values of the conditional columns to filter (may be
	 *                           {@code null})
	 * <p>
	 * @return the number of updated rows, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int updateWith(final Connection connection,
			final String table, final String[] columns, final Object[] values,
			final String[] conditionalColumns, final Object... conditionalValues)
			throws SQLException {
		return updateWith(connection, table, columns, values, conditionalColumns,
				getColumnTypes(connection, table, Arrays.concat(columns, conditionalColumns)),
				conditionalValues);
	}

	/**
	 * Updates the rows of specified table by setting the specified columns to the specified values
	 * where the specified conditional columns are equal to the conditional values with the
	 * specified SQL types using the specified {@link Connection}.
	 * <p>
	 * @param connection         a {@link Connection} (session) to a database
	 * @param table              the table containing the rows to update
	 * @param columns            the columns of the rows to update
	 * @param values             the values of the rows to update to
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalTypes   the {@code int} array containing the SQL types of the conditional
	 *                           columns to filter (may be {@code null})
	 * @param conditionalValues  the values of the conditional columns to filter (may be
	 *                           {@code null})
	 * <p>
	 * @return the number of updated rows, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int updateWith(final Connection connection,
			final String table, final String[] columns, final Object[] values,
			final String[] conditionalColumns, final int[] conditionalTypes,
			final Object... conditionalValues)
			throws SQLException {
		// Check the arguments
		if (Arrays.isNonEmpty(conditionalColumns) || Arrays.isNonEmpty(conditionalValues)) {
			ArrayArguments.requireSameLength(
					ArrayArguments.requireNonEmpty(conditionalColumns, "conditional columns"),
					ArrayArguments.requireNonEmpty(conditionalValues, "conditional values"));
		}

		// Execute the SQL query and return the number of updated rows
		return updateWith(connection, createUpdateQuery(table, columns, conditionalColumns),
				conditionalTypes, Arrays.concat(values, conditionalValues));
	}

	/**
	 * Returns the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE}
	 * or {@code DELETE} or {@code DDL} query with the specified parameter values using the
	 * specified {@link Connection}, or {@code 0} if nothing is returned.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param query           the SQL query to execute, such as {@code INSERT}, {@code UPDATE} or
	 *                        {@code DELETE}; or a SQL statement that returns nothing, such as a
	 *                        {@code DDL} statement
	 * @param parameterValues the array of values of the parameters of the {@code INSERT},
	 *                        {@code UPDATE} or {@code DELETE} or {@code DDL} query to execute (may
	 *                        be {@code null})
	 * <p>
	 * @return the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE}
	 *         or {@code DELETE} or {@code DDL} query with the specified parameter values using the
	 *         specified {@link Connection}, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int updateWith(final Connection connection, final String query,
			final Object... parameterValues)
			throws SQLException {
		return updateWith(connection, query, Integers.EMPTY_PRIMITIVE_ARRAY, parameterValues);
	}

	/**
	 * Returns the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE}
	 * or {@code DELETE} or {@code DDL} query with the specified parameter SQL types and values
	 * using the specified {@link Connection}, or {@code 0} if nothing is returned.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param query           the SQL query to execute, such as {@code INSERT}, {@code UPDATE} or
	 *                        {@code DELETE}; or a SQL statement that returns nothing, such as a
	 *                        {@code DDL} statement
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters of
	 *                        the {@code INSERT}, {@code UPDATE} or {@code DELETE} or {@code DDL}
	 *                        query to execute (may be {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code INSERT},
	 *                        {@code UPDATE} or {@code DELETE} or {@code DDL} query to execute (may
	 *                        be {@code null})
	 * <p>
	 * @return the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE}
	 *         or {@code DELETE} or {@code DDL} query with the specified parameter SQL types and
	 *         values using the specified {@link Connection}, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int updateWith(final Connection connection, final String query,
			final int[] parameterTypes, final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(query, "query");

		// Create the SQL statement for executing the SQL query
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			// Execute the SQL query and return the number of updated rows
			return updateWith(statement, parameterTypes, parameterValues);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (final SQLException ex) {
					IO.error(ex);
				}
			}
		}
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE},
	 * {@code DELETE} or {@code DDL} {@link PreparedStatement}, or {@code 0} if nothing is returned.
	 * <p>
	 * @param statement the SQL Data Manipulation Language (DML) {@link PreparedStatement} to
	 *                  execute, such as {@code INSERT}, {@code UPDATE} or {@code DELETE}; or a SQL
	 *                  statement that returns nothing, such as a {@code DDL} statement
	 * <p>
	 * @return the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE},
	 *         {@code DELETE} or {@code DDL} {@link PreparedStatement}, or {@code 0} if nothing is
	 *         returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static int update(final PreparedStatement statement)
			throws SQLException {
		return updateWith(statement, Integers.EMPTY_PRIMITIVE_ARRAY, Objects.EMPTY_ARRAY);
	}

	/**
	 * Returns the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE},
	 * {@code DELETE} or {@code DDL} {@link PreparedStatement} with the specified parameter values,
	 * or {@code 0} if nothing is returned.
	 * <p>
	 * @param statement       the SQL Data Manipulation Language (DML) {@link PreparedStatement} to
	 *                        execute, such as {@code INSERT}, {@code UPDATE} or {@code DELETE}; or
	 *                        a SQL statement that returns nothing, such as a {@code DDL} statement
	 * @param parameterValues the array of values of the parameters of the {@code INSERT},
	 *                        {@code UPDATE}, {@code DELETE} or {@code DDL}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE},
	 *         {@code DELETE} or {@code DDL} {@link PreparedStatement} with the specified parameter
	 *         values, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static int updateWith(final PreparedStatement statement, final Object... parameterValues)
			throws SQLException {
		return updateWith(statement, Integers.EMPTY_PRIMITIVE_ARRAY, parameterValues);
	}

	/**
	 * Returns the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE},
	 * {@code DELETE} or {@code DDL} {@link PreparedStatement} with the specified parameter SQL
	 * types and values, or {@code 0} if nothing is returned.
	 * <p>
	 * @param statement       the SQL Data Manipulation Language (DML) {@link PreparedStatement} to
	 *                        execute, such as {@code INSERT}, {@code UPDATE} or {@code DELETE}; or
	 *                        a SQL statement that returns nothing, such as a {@code DDL} statement
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters of
	 *                        the {@code INSERT}, {@code UPDATE}, {@code DELETE} or {@code DDL}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code INSERT},
	 *                        {@code UPDATE}, {@code DELETE} or {@code DDL}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE},
	 *         {@code DELETE} or {@code DDL} {@link PreparedStatement} with the specified parameter
	 *         SQL types and values, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static int updateWith(final PreparedStatement statement, final int[] parameterTypes,
			final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(statement, "statement");

		// Set the parameters of the SQL statement
		if (Arrays.isNonEmpty(parameterValues)) {
			setParameters(statement, parameterTypes, parameterValues);
		}
		// Execute the SQL query and return the number of updated rows
		return statement.executeUpdate();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE},
	 * {@code DELETE} or {@code DDL} procedure with the specified parameter values using the
	 * specified {@link Connection}, or {@code 0} if nothing is returned.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param procedure       the SQL procedure to execute, such as {@code INSERT}, {@code UPDATE}
	 *                        or {@code DELETE}; or a SQL statement that returns nothing, such as a
	 *                        {@code DDL} statement
	 * @param parameterValues the array of values of the parameters of the {@code INSERT},
	 *                        {@code UPDATE}, {@code DELETE} or {@code DDL} procedure to execute
	 *                        (may be {@code null})
	 * <p>
	 * @return the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE},
	 *         {@code DELETE} or {@code DDL} procedure with the specified parameter values using the
	 *         specified {@link Connection}, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int updateWithProcedure(final Connection connection, final String procedure,
			final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(parameterValues, "parameter values");

		// Execute the SQL procedure and return the number of updated rows
		return updateWithProcedure(connection, procedure,
				getProcedureParameterTypes(connection, procedure), parameterValues);
	}

	/**
	 * Returns the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE},
	 * {@code DELETE} or {@code DDL} procedure with the specified parameter SQL types and values
	 * using the specified {@link Connection}, or {@code 0} if nothing is returned.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param procedure       the SQL procedure to execute, such as {@code INSERT}, {@code UPDATE}
	 *                        or {@code DELETE}; or a SQL statement that returns nothing, such as a
	 *                        {@code DDL} statement
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters of
	 *                        the {@code INSERT}, {@code UPDATE}, {@code DELETE} or {@code DDL}
	 *                        procedure to execute (may be {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code INSERT},
	 *                        {@code UPDATE}, {@code DELETE} or {@code DDL} procedure to execute
	 *                        (may be {@code null})
	 * <p>
	 * @return the number of rows updated by executing the specified {@code INSERT}, {@code UPDATE},
	 *         {@code DELETE} or {@code DDL} procedure with the specified parameter SQL types and
	 *         values using the specified {@link Connection}, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int updateWithProcedure(final Connection connection, final String procedure,
			final int[] parameterTypes, final Object... parameterValues)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(parameterValues, "parameter values");

		// Execute the SQL procedure and return the number of updated rows
		return updateWith(connection, createProcedureQuery(procedure, parameterValues.length),
				Integers.take(parameterTypes, 0, parameterValues.length), parameterValues);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of rows deleted by executing the specified {@code DELETE} query using the
	 * specified {@link Connection}, or {@code 0} if nothing is returned.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param query      the {@code DELETE} query to execute
	 * <p>
	 * @return the number of rows deleted by executing the specified {@code DELETE} query using the
	 *         specified {@link Connection}, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int delete(final Connection connection, final String query)
			throws SQLException {
		return update(connection, query);
	}

	/**
	 * Deletes the rows from the specified table using the specified {@link Connection}.
	 * <p>
	 * @param connection a {@link Connection} (session) to a database
	 * @param table      the table containing the rows to delete
	 * <p>
	 * @return the number of deleted rows, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int deleteWith(final Connection connection, final String table)
			throws SQLException {
		return deleteWith(connection, table, Strings.EMPTY_ARRAY, Integers.EMPTY_PRIMITIVE_ARRAY,
				Objects.EMPTY_ARRAY);
	}

	/**
	 * Deletes the rows from the specified table where the specified conditional columns are equal
	 * to the conditional values using the specified {@link Connection}.
	 * <p>
	 * @param connection         a {@link Connection} (session) to a database
	 * @param table              the table containing the rows to delete
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalValues  the values of the conditional columns to filter (may be
	 *                           {@code null})
	 * <p>
	 * @return the number of deleted rows, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int deleteWith(final Connection connection, final String table,
			final String[] conditionalColumns, final Object... conditionalValues)
			throws SQLException {
		return deleteWith(connection, table, conditionalColumns,
				getColumnTypes(connection, table, conditionalColumns), conditionalValues);
	}


	/**
	 * Deletes the rows from the specified table where the specified conditional columns are equal
	 * to the conditional values with the specified SQL types using the specified
	 * {@link Connection}.
	 * <p>
	 * @param connection         a {@link Connection} (session) to a database
	 * @param table              the table containing the rows to delete
	 * @param conditionalColumns the conditional columns to filter (may be {@code null})
	 * @param conditionalTypes   the {@code int} array containing the SQL types of the conditional
	 *                           columns to filter (may be {@code null})
	 * @param conditionalValues  the values of the conditional columns to filter (may be
	 *                           {@code null})
	 * <p>
	 * @return the number of deleted rows, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int deleteWith(final Connection connection, final String table,
			final String[] conditionalColumns, final int[] conditionalTypes,
			final Object... conditionalValues)
			throws SQLException {
		// Check the arguments
		if (Arrays.isNonEmpty(conditionalColumns) || Arrays.isNonEmpty(conditionalValues)) {
			ArrayArguments.requireSameLength(
					ArrayArguments.requireNonEmpty(conditionalColumns, "conditional columns"),
					ArrayArguments.requireNonEmpty(conditionalValues, "conditional values"));
		}

		// Execute the SQL query and return the number of deleted rows
		return updateWith(connection, createDeleteQuery(table, conditionalColumns),
				conditionalTypes, conditionalValues);
	}

	/**
	 * Returns the number of rows deleted by executing the specified {@code DELETE} query with the
	 * specified parameter values using the specified {@link Connection}, or {@code 0} if nothing is
	 * returned.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param query           the {@code DELETE} query to execute
	 * @param parameterValues the array of values of the parameters of the {@code DELETE} query to
	 *                        execute (may be {@code null})
	 * <p>
	 * @return the number of rows deleted by executing the specified {@code DELETE} query with the
	 *         specified parameter values using the specified {@link Connection}, or {@code 0} if
	 *         nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int deleteWith(final Connection connection, final String query,
			final Object... parameterValues)
			throws SQLException {
		return updateWith(connection, query, Integers.EMPTY_PRIMITIVE_ARRAY, parameterValues);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of rows deleted by executing the specified {@code DELETE}
	 * {@link PreparedStatement}, or {@code 0} if nothing is returned.
	 * <p>
	 * @param statement the SQL Data Manipulation Language (DML) {@code DELETE}
	 *                  {@link PreparedStatement} to execute
	 * <p>
	 * @return the number of rows deleted by executing the specified {@code DELETE}
	 *         {@link PreparedStatement}, or {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static int delete(final PreparedStatement statement)
			throws SQLException {
		return update(statement);
	}

	/**
	 * Returns the number of rows deleted by executing the specified {@code DELETE}
	 * {@link PreparedStatement} with the specified parameter values, or {@code 0} if nothing is
	 * returned.
	 * <p>
	 * @param statement       the SQL Data Manipulation Language (DML) {@code DELETE}
	 *                        {@link PreparedStatement} to execute
	 * @param parameterValues the array of values of the parameters of the {@code DELETE}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return the number of rows deleted by executing the specified {@code DELETE}
	 *         {@link PreparedStatement} with the specified parameter values, or {@code 0} if
	 *         nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static int deleteWith(final PreparedStatement statement, final Object... parameterValues)
			throws SQLException {
		return updateWith(statement, parameterValues);
	}

	/**
	 * Returns the number of rows deleted by executing the specified {@code DELETE}
	 * {@link PreparedStatement} with the specified parameter SQL types and values, or {@code 0} if
	 * nothing is returned.
	 * <p>
	 * @param statement       the SQL Data Manipulation Language (DML) {@code DELETE}
	 *                        {@link PreparedStatement} to execute
	 * @param parameterTypes  the {@code int} array containing the SQL types of the parameters (may
	 *                        be {@code null})
	 * @param parameterValues the array of values of the parameters of the {@code DELETE}
	 *                        {@link PreparedStatement} to execute (may be {@code null})
	 * <p>
	 * @return the number of rows deleted by executing the specified {@code DELETE}
	 *         {@link PreparedStatement} with the specified parameter SQL types and values, or
	 *         {@code 0} if nothing is returned
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link PreparedStatement}
	 */
	public static int deleteWith(final PreparedStatement statement, final int[] parameterTypes,
			final Object... parameterValues)
			throws SQLException {
		return updateWith(statement, parameterTypes, parameterValues);
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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum DB {
		MSSQL,
		StandardSQL
	}
}
