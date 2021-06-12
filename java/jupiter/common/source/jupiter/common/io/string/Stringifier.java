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
package jupiter.common.io.string;

public interface Stringifier {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} of the fields of the specified content {@link Object}.
	 * <p>
	 * @param content the content {@link Object} containing the fields to represent as a
	 *                {@link String} (may be {@code null})
	 * <p>
	 * @return a {@link String} of the fields of the specified content {@link Object}
	 */
	public String stringify(final Object content);

	/**
	 * Returns a {@link String} of the specified key-value mapping.
	 * <p>
	 * @param key   the key {@link String} of the key-value mapping to represent as a {@link String}
	 *              (may be {@code null})
	 * @param value the value of the key-value mapping to represent as a {@link String} (may be
	 *              {@code null})
	 * <p>
	 * @return a {@link String} of the specified key-value mapping
	 */
	public String stringify(final String key, final Object value);

	//////////////////////////////////////////////

	/**
	 * Returns an entry {@link String} of the specified node value {@link Object}.
	 * <p>
	 * @param value the node value {@link Object} to represent as an entry {@link String} (may be
	 *              {@code null})
	 * <p>
	 * @return an entry {@link String} of the specified node value {@link Object}
	 */
	public String stringifyNode(final Object value);

	/**
	 * Returns an entry {@link String} of the specified node key-value mapping.
	 * <p>
	 * @param key   the key {@link String} of the node key-value mapping to represent as an entry
	 *              {@link String} (may be {@code null})
	 * @param value the value {@link Object} of the node key-value mapping to represent as an entry
	 *              {@link String} (may be {@code null})
	 * <p>
	 * @return an entry {@link String} of the specified node key-value mapping
	 */
	public String stringifyNode(final String key, final Object value);

	//////////////////////////////////////////////

	/**
	 * Returns an entry {@link String} of the specified leaf value {@link Object}.
	 * <p>
	 * @param value the leaf value {@link Object} to represent as an entry {@link String} (may be
	 *              {@code null})
	 * <p>
	 * @return an entry {@link String} of the specified leaf value {@link Object}
	 */
	public String stringifyLeaf(final Object value);
}
