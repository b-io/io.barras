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
package jupiter.transfer.file;

import java.lang.reflect.Field;
import java.util.Collection;

import jupiter.common.util.Arrays;
import jupiter.common.util.Collections;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class JSON {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final char JSON_DELIMITER = ',';
	public static final JSONWrapper JSON_WRAPPER = new JSONWrapper();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected JSON() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a JSON representative {@link String} of the fields of the specified content.
	 * <p>
	 * @param content the content {@link Object} containing the fields to represent as a JSON
	 *                {@link String}
	 * <p>
	 * @return a JSON representative {@link String} of the fields of the specified content
	 */
	public static String jsonify(final Object content) {
		final StringBuilder builder = Strings.createBuilder();
		builder.append('{');
		if (content != null) {
			final Field[] fields = content.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; ++i) {
				final Field field = fields[i];
				try {
					builder.append(jsonifyNode(field.getName(), field.get(content)));
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
	 * Returns a JSON representative {@link String} of the specified key-value mapping.
	 * <p>
	 * @param key   the key of the key-value mapping to represent as a JSON {@link String}
	 * @param value the value of the key-value mapping to represent as a JSON {@link String}
	 * <p>
	 * @return a JSON representative {@link String} of the specified key-value mapping
	 */
	public static String jsonify(final String key, final Object value) {
		final StringBuilder builder = Strings.createBuilder();
		builder.append('{');
		builder.append(jsonifyNode(key, value));
		builder.append('}');
		return builder.toString();
	}

	/**
	 * Returns a JSON entry representative {@link String} of the specified value {@link Object}.
	 * <p>
	 * @param value the value {@link Object} to represent as a JSON entry {@link String}
	 * <p>
	 * @return a JSON entry representative {@link String} of the specified value {@link Object}
	 */
	public static String jsonifyNode(final Object value) {
		return jsonifyNode(null, value);
	}

	/**
	 * Returns a JSON entry representative {@link String} of the specified key-value mapping.
	 * <p>
	 * @param key   the key {@link String} of the key-value mapping to represent as a JSON entry
	 *              {@link String}
	 * @param value the value {@link Object} of the key-value mapping to represent as a JSON entry
	 *              {@link String}
	 * <p>
	 * @return a JSON entry representative {@link String} of the specified key-value mapping
	 */
	public static String jsonifyNode(final String key, final Object value) {
		final StringBuilder builder = Strings.createBuilder();
		if (key != null) {
			builder.append(Strings.doubleQuote(key));
			builder.append(':');
		}
		if (value != null) {
			final Class<?> c = value.getClass();
			if (Arrays.is(c)) {
				final Object[] array = Arrays.toArray(value);
				if (array.length == 0 || isLeaf(c.getComponentType())) {
					builder.append(Strings.bracketize(Strings.joinWith(array, JSON_DELIMITER)));
				} else {
					builder.append(Strings.bracketize(
							Strings.joinWith(array, JSON_DELIMITER, JSON_WRAPPER)));
				}
			} else if (Collections.is(c)) {
				final Collection<?> collection = (Collection<?>) value;
				if (collection.isEmpty() || isLeaf(Collections.get(collection, 0).getClass())) {
					builder.append(Strings.bracketize(
							Strings.joinWith(collection, JSON_DELIMITER)));
				} else {
					builder.append(Strings.bracketize(
							Strings.joinWith(collection, JSON_DELIMITER, JSON_WRAPPER)));
				}
			} else if (isLeaf(c)) {
				builder.append(jsonifyLeaf(value));
			} else {
				builder.append(jsonify(value));
			}
		} else {
			builder.append(jsonifyLeaf(value));
		}
		return builder.toString();
	}

	public static String jsonifyLeaf(final Object value) {
		if (value != null && Strings.is(value.getClass())) {
			return Strings.doubleQuote(Strings.escape(value));
		}
		return Strings.toString(value);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is a leaf.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is a leaf, {@code false} otherwise
	 */
	public static boolean isLeaf(final Class<?> c) {
		return c.isPrimitive() || Objects.hasToString(c);
	}
}
