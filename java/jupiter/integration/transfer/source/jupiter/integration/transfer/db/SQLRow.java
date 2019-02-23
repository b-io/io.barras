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
package jupiter.integration.transfer.db;

import static jupiter.common.io.IO.IO;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import jupiter.common.util.Booleans;
import jupiter.common.util.Doubles;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Shorts;
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
			IO.error("No constructor with ", ResultSet.class.getSimpleName(), " in ",
					getClass().getSimpleName(), " found: ", ex.getMessage());
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
	protected String getColumnName(final String fieldName) {
		return fieldName;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void load(final ResultSet resultSet) {
		if (resultSet != null) {
			final Field[] fields = getClass().getDeclaredFields();
			for (final Field field : fields) {
				if (field.isAccessible()) {
					try {
						final String columnName = getColumnName(field.getName());
						final Class<?> c = field.getType();
						if (Array.class.isAssignableFrom(c)) {
							field.set(this, resultSet.getArray(columnName));
						} else if (BigDecimal.class.isAssignableFrom(c)) {
							field.set(this, resultSet.getBigDecimal(columnName));
						} else if (Blob.class.isAssignableFrom(c)) {
							field.set(this, resultSet.getBlob(columnName));
						} else if (Booleans.is(c)) {
							field.set(this, resultSet.getBoolean(columnName));
						} else if (byte.class.isAssignableFrom(c)) {
							field.set(this, resultSet.getByte(columnName));
						} else if (byte[].class.isAssignableFrom(c)) {
							field.set(this, resultSet.getBytes(columnName));
						} else if (Clob.class.isAssignableFrom(c)) {
							field.set(this, resultSet.getClob(columnName));
						} else if (Date.class.isAssignableFrom(c)) {
							field.set(this, resultSet.getDate(columnName));
						} else if (Doubles.is(c)) {
							field.set(this, resultSet.getDouble(columnName));
						} else if (Floats.is(c)) {
							field.set(this, resultSet.getFloat(columnName));
						} else if (Integers.is(c)) {
							field.set(this, resultSet.getInt(columnName));
						} else if (Longs.is(c)) {
							field.set(this, resultSet.getLong(columnName));
						} else if (Shorts.is(c)) {
							field.set(this, resultSet.getShort(columnName));
						} else if (Strings.is(c)) {
							field.set(this, resultSet.getString(columnName));
						} else if (Time.class.isAssignableFrom(c)) {
							field.set(this, resultSet.getTime(columnName));
						} else if (Timestamp.class.isAssignableFrom(c)) {
							field.set(this, resultSet.getTimestamp(columnName));
						} else if (URL.class.isAssignableFrom(c)) {
							field.set(this, resultSet.getURL(columnName));
						} else {
							IO.warn("Unhandled field class: ", Strings.quote(c));
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
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return Web.jsonify(this);
	}
}
