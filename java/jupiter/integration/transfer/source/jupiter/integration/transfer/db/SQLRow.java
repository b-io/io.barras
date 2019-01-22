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
import jupiter.common.util.Strings;
import jupiter.integration.transfer.web.Web;

public abstract class SQLRow {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected SQLRow(final ResultSet resultSet) {
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

	protected abstract SQLRow createInstance(final ResultSet resultSet);

	public List<SQLRow> request(final Connection connection, final String query) {
		final List<SQLRow> result = new LinkedList<SQLRow>();
		CallableStatement statement = null;
		try {
			statement = connection.prepareCall(query);
			final ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result.add(createInstance(resultSet));
			}
		} catch (final SQLException ex) {
			IO.error(ex);
		} finally {
			Resources.autoClose(statement);
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
					if (c.isAssignableFrom(BigDecimal.class)) {
						field.set(this, resultSet.getBigDecimal(columnName));
					} else if (c.isAssignableFrom(boolean.class) ||
							c.isAssignableFrom(Boolean.class)) {
						field.set(this, resultSet.getBoolean(columnName));
					} else if (c.isAssignableFrom(double.class) ||
							c.isAssignableFrom(Double.class)) {
						field.set(this, resultSet.getDouble(columnName));
					} else if (c.isAssignableFrom(int.class) ||
							c.isAssignableFrom(Integer.class)) {
						field.set(this, resultSet.getInt(columnName));
					} else if (c.isAssignableFrom(String.class)) {
						field.set(this, resultSet.getString(columnName));
					} else if (c.isAssignableFrom(Time.class)) {
						field.set(this, resultSet.getTime(columnName));
					} else if (c.isAssignableFrom(Timestamp.class)) {
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
