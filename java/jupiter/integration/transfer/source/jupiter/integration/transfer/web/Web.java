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
package jupiter.integration.transfer.web;

import java.lang.reflect.Field;
import java.util.Collection;

import jupiter.common.util.Collections;
import jupiter.common.util.Strings;

public class Web {

	public static final JSONWrapper JSON_WRAPPER = new JSONWrapper();

	/**
	 * Returns a JSON {@link String} representation of the specified content.
	 * <p>
	 * @param content the {@link Object} to represent as a JSON {@link String}
	 * <p>
	 * @return a JSON {@link String} representation of the specified content
	 */
	public static String jsonify(final Object content) {
		final StringBuilder builder = Strings.createBuilder();
		builder.append('{');
		if (content != null) {
			final Field[] fields = content.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; ++i) {
				final Field field = fields[i];
				builder.append(Strings.doubleQuote(field.getName()));
				builder.append(':');
				try {
					if (Collections.is(field.getType())) {
						builder.append(Collections.toString((Collection<?>) field.get(content),
								Collections.DEFAULT_DELIMITER, JSON_WRAPPER));
					} else {
						builder.append(JSON_WRAPPER.call(field.get(content)));
					}
				} catch (final IllegalAccessException ignored) {
				}
				if (i < fields.length - 1) {
					builder.append(',');
				}
			}
		}
		builder.append('}');
		return builder.toString();
	}
}
