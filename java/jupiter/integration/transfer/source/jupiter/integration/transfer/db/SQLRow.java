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
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import jupiter.common.io.Resources;
import jupiter.common.util.Booleans;
import jupiter.common.util.Doubles;
import jupiter.common.util.Integers;
import jupiter.common.util.Strings;
import jupiter.integration.transfer.web.Web;

public abstract class SQLRow {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Constructor<? extends SQLRow> constructor;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SQLRow} with the specified {@link ResultSet}.
	 * <p>
	 * @param resultSet the {@link ResultSet} whose cursor is pointing to the row of data to load
	 */
	protected SQLRow(final ResultSet resultSet) {
		constructor = null;
		try {
			constructor = getClass().getConstructor(ResultSet.class);
		} catch (final NoSuchMethodException ex) {
			IO.error("No constructor with ", ResultSet.class.getSimpleName(), " in ", getClass()
					.getSimpleName(), " found: ", ex.getMessage());
		}
		load(resultSet);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the database column name from the specified field name.
	 * <p>
	 * @param fieldName the field name
	 * <p>
	 * @return the database column name from the specified field name
	 */
	protected abstract String getColumnName(final String fieldName);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public List<SQLRow> request(final Connection connection, final String query) {
		final List<SQLRow> result = new LinkedList<SQLRow>();
		if (constructor != null) {
			CallableStatement statement = null;
			try {
				statement = connection.prepareCall(query);
				final ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					result.add(constructor.newInstance(resultSet));
				}
			} catch (final Exception ex) {
				IO.error(ex);
			} finally {
				Resources.autoClose(statement);
			}
		} else {
			IO.error("No constructor with ", ResultSet.class.getSimpleName(), " in ", getClass()
					.getSimpleName(), " found");
		}
		return result;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void load(final ResultSet resultSet) {
		if (resultSet != null) {
			final Field[] fields = getClass().getDeclaredFields();
			for (final Field field : fields) {
				try {
					final String columnName = getColumnName(field.getName());
					final Class<?> c = field.getType();
					if (BigDecimal.class.isAssignableFrom(c)) {
						field.set(this, resultSet.getBigDecimal(columnName));
					} else if (Booleans.is(c)) {
						field.set(this, resultSet.getBoolean(columnName));
					} else if (Doubles.is(c)) {
						field.set(this, resultSet.getDouble(columnName));
					} else if (Integers.is(c)) {
						field.set(this, resultSet.getInt(columnName));
					} else if (Strings.is(c)) {
						field.set(this, resultSet.getString(columnName));
					} else if (Time.class.isAssignableFrom(c)) {
						field.set(this, resultSet.getTime(columnName));
					} else if (Timestamp.class.isAssignableFrom(c)) {
						field.set(this, resultSet.getTimestamp(columnName));
					} else {
						IO.error("Unhandled field class: ", Strings.quote(c));
					}
				} catch (final IllegalAccessException ignored) {
				} catch (final IllegalArgumentException ex) {
					IO.error(ex);
				} catch (final SQLException ex) {
					IO.error(ex);
				}
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return Web.jsonify(this);
	}
}
