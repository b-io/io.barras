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
package jupiter.common.io.string;

import static jupiter.common.util.Characters.COLON;
import static jupiter.common.util.Strings.INITIAL_CAPACITY;
import static jupiter.common.util.Strings.NULL;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import jupiter.common.util.Arrays;
import jupiter.common.util.Booleans;
import jupiter.common.util.Classes;
import jupiter.common.util.Collections;
import jupiter.common.util.Maps;
import jupiter.common.util.Numbers;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class JSON
		implements Stringifier {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@code char} delimiter.
	 */
	public static final char JSON_DELIMITER = ',';
	/**
	 * The {@link JSONGenerator}.
	 */
	public final JSONGenerator JSON_WRAPPER = new JSONGenerator();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link JSON}.
	 */
	protected JSON() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a JSON {@link String} of the fields of the specified content {@link Object}.
	 * <p>
	 * @param content the content {@link Object} containing the fields to represent as a JSON
	 *                {@link String} (may be {@code null})
	 * <p>
	 * @return a JSON {@link String} of the fields of the specified content {@link Object}
	 */
	public String stringify(final Object content) {
		// Check the arguments
		if (content == null) {
			return NULL;
		}

		// Convert the content to a JSON string
		final Field[] fields = Classes.get(content).getDeclaredFields();
		final StringBuilder builder = Strings.createBuilder(fields.length *
				(2 * INITIAL_CAPACITY + 6));
		int accessibleFieldCount = 0;
		for (final Field field : fields) {
			try {
				final String key = field.getName();
				final Object value = field.get(content);
				if (accessibleFieldCount > 0) {
					builder.append(JSON_DELIMITER);
				}
				builder.append(stringifyNode(key, value));
				++accessibleFieldCount;
			} catch (final IllegalAccessException ignored) {
			}
		}
		if (accessibleFieldCount == 0) {
			builder.append(stringifyNode(content));
		}
		return Strings.brace(builder.toString());
	}

	/**
	 * Returns a JSON {@link String} of the specified key-value mapping.
	 * <p>
	 * @param key   the key {@link String} of the key-value mapping to represent as a JSON
	 *              {@link String} (may be {@code null})
	 * @param value the value of the key-value mapping to represent as a JSON {@link String} (may be
	 *              {@code null})
	 * <p>
	 * @return a JSON {@link String} of the specified key-value mapping
	 */
	public String stringify(final String key, final Object value) {
		return Strings.brace(stringifyNode(key, value));
	}

	//////////////////////////////////////////////

	/**
	 * Returns a JSON entry {@link String} of the specified node value {@link Object}.
	 * <p>
	 * @param value the node value {@link Object} to represent as a JSON entry {@link String} (may
	 *              be {@code null})
	 * <p>
	 * @return a JSON entry {@link String} of the specified node value {@link Object}
	 */
	public String stringifyNode(final Object value) {
		return stringifyNode(null, value);
	}

	/**
	 * Returns a JSON entry {@link String} of the specified node key-value mapping.
	 * <p>
	 * @param key   the key {@link String} of the node key-value mapping to represent as a JSON
	 *              entry {@link String} (may be {@code null})
	 * @param value the value {@link Object} of the node key-value mapping to represent as a JSON
	 *              entry {@link String} (may be {@code null})
	 * <p>
	 * @return a JSON entry {@link String} of the specified node key-value mapping
	 */
	public String stringifyNode(final String key, final Object value) {
		final StringBuilder builder = Strings.createBuilder(2 * INITIAL_CAPACITY + 5);
		if (key != null) {
			builder.append(Strings.doubleQuote(key));
			builder.append(COLON);
		}
		if (value != null) {
			final Class<?> c = Classes.get(value);
			if (Arrays.is(value)) {
				final Object[] array = Arrays.toArray(value);
				if (Stringifiers.isLeaf(array)) {
					builder.append(Strings.bracketize(
							Strings.joinWith(array, JSON_DELIMITER, JSON_WRAPPER)));
				} else {
					builder.append(Strings.bracketize(
							Strings.joinWith(array, JSON_DELIMITER)));
				}
			} else if (Collections.is(value)) {
				final Collection<?> collection = (Collection<?>) value;
				if (Stringifiers.isLeaf(collection)) {
					builder.append(Strings.bracketize(
							Strings.joinWith(collection, JSON_DELIMITER, JSON_WRAPPER)));
				} else {
					builder.append(Strings.bracketize(
							Strings.joinWith(collection, JSON_DELIMITER)));
				}
			} else if (Maps.is(value)) {
				builder.append(Maps.toString((Map<?, ?>) value));
			} else if (Stringifiers.isLeaf(c) || Objects.hasToString(c)) {
				builder.append(stringifyLeaf(value));
			} else {
				builder.append(stringify(value));
			}
		} else {
			builder.append(stringifyLeaf(value));
		}
		return builder.toString();
	}

	/**
	 * Returns a JSON entry {@link String} of the specified leaf value {@link Object}.
	 * <p>
	 * @param value the leaf value {@link Object} to represent as a JSON entry {@link String} (may
	 *              be {@code null})
	 * <p>
	 * @return a JSON entry {@link String} of the specified leaf value {@link Object}
	 */
	public String stringifyLeaf(final Object value) {
		if (value == null || Booleans.is(value) || Numbers.is(value) ||
				value instanceof JSONValue) {
			return Objects.toString(value);
		}
		return Strings.doubleQuote(Strings.escape(value));
	}
}
