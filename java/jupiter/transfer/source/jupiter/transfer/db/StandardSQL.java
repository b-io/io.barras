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
import static jupiter.common.util.Characters.LEFT_BRACE;
import static jupiter.common.util.Characters.RIGHT_BRACE;
import static jupiter.common.util.Strings.DOUBLE_QUOTER;
import static jupiter.common.util.Strings.EMPTY;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import jupiter.common.io.Resources;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Strings;

public class StandardSQL
		extends SQL {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link StandardSQL}.
	 */
	protected StandardSQL() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

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
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(table, "table");

		// Create the SQL query
		final String query = Strings.join("SELECT ",
				Arrays.isNonEmpty(columns) ? Strings.joinWith(columns, ",", DOUBLE_QUOTER) : "*",
				" FROM ", Strings.doubleQuote(table),
				" LIMIT 1");
		// Create the SQL statement for executing the SQL query
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			// Execute the SQL query
			final ResultSet resultSet = statement.executeQuery();
			// Return the SQL types of the columns of the table
			final ResultSetMetaData metadata = resultSet.getMetaData();
			final int[] types = new int[metadata.getColumnCount()];
			for (int i = 0; i < types.length; ++i) {
				types[i] = resultSet.getMetaData().getColumnType(i + 1);
			}
			return types;
		} finally {
			Resources.closeAuto(statement);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Returns the SQL types of all the parameters of the specified SQL stored procedure using the
	 * specified {@link Connection}.
	 * <p>
	 * @param connection      a {@link Connection} (session) to a database
	 * @param storedProcedure the SQL stored procedure containing the parameters to get the SQL
	 *                        types from
	 * <p>
	 * @return the SQL types of all the parameters of the specified SQL stored procedure using the
	 *         specified {@link Connection}
	 * <p>
	 * @throws SQLException if a database access error occurs or if this method is called on a
	 *                      closed {@link Connection}
	 */
	public static int[] getStoredProcedureParameterTypes(final Connection connection,
			final String storedProcedure)
			throws SQLException {
		// Check the arguments
		Arguments.requireNonNull(connection, "connection");
		Arguments.requireNonNull(storedProcedure, "stored procedure");

		// Get the database metadata
		final DatabaseMetaData metadata = connection.getMetaData();
		final ResultSet resultSet = metadata.getProcedureColumns(connection.getCatalog(), null,
				storedProcedure, null);
		// Return the SQL types of all the parameters of the stored procedure
		final ExtendedLinkedList<Integer> types = new ExtendedLinkedList<Integer>();
		resultSet.next();
		while (resultSet.next()) {
			types.add(resultSet.getInt("DATA_TYPE"));
		}
		return (int[]) types.toPrimitiveArray();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String createSelectQuery(final String table, final String[] columns,
			final String... conditionalColumns) {
		// Check the arguments
		Arguments.requireNonNull(table, "table");
		if (Arrays.isNullOrEmpty(conditionalColumns)) {
			IO.warn("No conditional columns for selecting the table ", Strings.quote(table));
		}

		// Create the SQL query for selecting the table with the columns and conditional columns
		return Strings.join("SELECT ",
				Arrays.isNonEmpty(columns) ? Strings.joinWith(columns, ",", DOUBLE_QUOTER) : "*",
				" FROM ", Strings.doubleQuote(table),
				Arrays.isNonEmpty(conditionalColumns) ?
				Strings.join(" WHERE ",
						Strings.joinWith(conditionalColumns, "=? AND ", DOUBLE_QUOTER)
								.concat("=?")) :
				EMPTY);
	}

	//////////////////////////////////////////////

	public static String createInsertQuery(final String table, final String... columns) {
		// Check the arguments
		Arguments.requireNonNull(table, "table");
		ArrayArguments.requireNonEmpty(columns, "columns");

		// Create the SQL query for inserting into the table with the columns
		return Strings.join("INSERT INTO ", Strings.doubleQuote(table),
				" ", Arrays.toStringWith(columns, DOUBLE_QUOTER),
				" VALUES ", Arrays.toString(Arrays.repeat('?', columns.length)));
	}

	//////////////////////////////////////////////

	public static String createUpdateQuery(final String table, final String[] columns,
			final String... conditionalColumns) {
		// Check the arguments
		Arguments.requireNonNull(table, "table");
		ArrayArguments.requireNonEmpty(columns, "columns");
		if (Arrays.isNullOrEmpty(conditionalColumns)) {
			IO.warn("No conditional columns for updating the table ", Strings.quote(table));
		}

		// Create the SQL query for updating the table with the columns and conditional columns
		return Strings.join("UPDATE ", Strings.doubleQuote(table),
				" SET ", Strings.joinWith(columns, "=?,", DOUBLE_QUOTER).concat("=?"),
				Arrays.isNonEmpty(conditionalColumns) ?
				Strings.join(" WHERE ",
						Strings.joinWith(conditionalColumns, "=? AND ", DOUBLE_QUOTER)
								.concat("=?")) :
				EMPTY);
	}

	//////////////////////////////////////////////

	public static String createDeleteQuery(final String table, final String... conditionalColumns) {
		// Check the arguments
		Arguments.requireNonNull(table, "table");
		if (Arrays.isNullOrEmpty(conditionalColumns)) {
			IO.warn("No conditional columns for deleting the table ", Strings.quote(table));
		}

		// Create the SQL query for deleting the table with the conditional columns
		return Strings.join("DELETE FROM ", Strings.doubleQuote(table),
				Arrays.isNonEmpty(conditionalColumns) ?
				Strings.join(" WHERE ",
						Strings.joinWith(conditionalColumns, "=? AND ", DOUBLE_QUOTER)
								.concat("=?")) :
				EMPTY);
	}

	//////////////////////////////////////////////

	public static String createStoredProcedureQuery(final String storedProcedure,
			final int parameterCount) {
		// Check the arguments
		Arguments.requireNonNull(storedProcedure, "stored procedure");

		// Create the SQL query for executing the SQL stored procedure
		return Strings.join(LEFT_BRACE, "call ", storedProcedure,
				Arrays.toString(Arrays.repeat('?', parameterCount)), RIGHT_BRACE);
	}
}
