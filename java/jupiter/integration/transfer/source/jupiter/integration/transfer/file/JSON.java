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
package jupiter.integration.transfer.file;

import java.lang.reflect.Field;
import java.util.Collection;

import jupiter.common.util.Arrays;
import jupiter.common.util.Collections;
import jupiter.common.util.Strings;

public class JSON {

	public static final char JSON_DELIMITER = ',';
	public static final JSONWrapper JSON_WRAPPER = new JSONWrapper();

	/**
	 * Returns a JSON {@link String} representation of the fields of the specified content.
	 * <p>
	 * @param content the {@link Object} containing the fields to represent as a JSON {@link String}
	 * <p>
	 * @return a JSON {@link String} representation of the fields of the specified content
	 */
	public static String jsonify(final Object content) {
		final StringBuilder builder = Strings.createBuilder();
		builder.append('{');
		if (content != null) {
			final Field[] fields = content.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; ++i) {
				final Field field = fields[i];
				try {
					builder.append(jsonifyEntry(field.getName(), field.get(content)));
					if (i < fields.length - 1) {
						builder.append(',');
					}
				} catch (final IllegalAccessException ignored) {
				}
			}
		}
		builder.append('}');
		return builder.toString();
	}

	/**
	 * Returns a JSON {@link String} representation of the specified key-value mapping.
	 * <p>
	 * @param key   the key of the key-value mapping to represent as a JSON {@link String}
	 * @param value the value of the key-value mapping to represent as a JSON {@link String}
	 * <p>
	 * @return a JSON {@link String} representation of the specified key-value mapping
	 */
	public static String jsonify(final String key, final Object value) {
		final StringBuilder builder = Strings.createBuilder();
		builder.append('{');
		builder.append(jsonifyEntry(key, value));
		builder.append('}');
		return builder.toString();
	}

	/**
	 * Returns a JSON entry {@link String} representation of the specified value.
	 * <p>
	 * @param value the value to represent as a JSON entry {@link String}
	 * <p>
	 * @return a JSON entry {@link String} representation of the specified value
	 */
	public static String jsonifyEntry(final Object value) {
		return jsonifyEntry(null, value);
	}

	/**
	 * Returns a JSON entry {@link String} representation of the specified key-value mapping.
	 * <p>
	 * @param key   the key of the key-value mapping to represent as a JSON entry {@link String}
	 * @param value the value of the key-value mapping to represent as a JSON entry {@link String}
	 * <p>
	 * @return a JSON entry {@link String} representation of the specified key-value mapping
	 */
	public static String jsonifyEntry(final String key, final Object value) {
		final StringBuilder builder = Strings.createBuilder();
		if (key != null) {
			builder.append(Strings.doubleQuote(key));
			builder.append(':');
		}
		if (value != null) {
			final Class<?> c = value.getClass();
			if (c.isArray()) {
				builder.append(jsonifyArray(value));
			} else if (Collections.is(c)) {
				builder.append(jsonifyCollection((Collection<?>) value));
			} else {
				builder.append(JSON_WRAPPER.call(value));
			}
		} else {
			builder.append(JSON_WRAPPER.call(value));
		}
		return builder.toString();
	}

	public static String jsonifyArray(final Object array) {
		return jsonifyArray(Arrays.toArray(array));
	}

	public static <T> String jsonifyArray(final T[] array) {
		return Strings.<T>bracketize(Strings.joinWith(array, JSON_DELIMITER, JSON_WRAPPER));
	}

	public static <T> String jsonifyCollection(final Collection<T> collection) {
		return Strings.<T>bracketize(Strings.joinWith(collection, JSON_DELIMITER, JSON_WRAPPER));
	}
}
