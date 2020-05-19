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
package jupiter.common.io;

import static jupiter.common.Formats.CHARSET;
import static jupiter.common.util.Strings.NULL;

import java.io.Serializable;
import java.nio.charset.Charset;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

public class Content
		implements ICloneable<Content>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The content {@link String}.
	 */
	protected final String content;
	/**
	 * The {@link Charset}.
	 */
	protected final Charset charset;
	/**
	 * The number of lines.
	 */
	protected final int lineCount;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Content} with the specified content {@link Object}.
	 * <p>
	 * @param content the content {@link Object}
	 */
	public Content(final Object content) {
		this(content, CHARSET);
	}

	/**
	 * Constructs a {@link Content} with the specified content {@link Object} and {@link Charset}.
	 * <p>
	 * @param content the content {@link Object}
	 * @param charset the {@link Charset} of the content {@link Object}
	 */
	public Content(final Object content, final Charset charset) {
		this(content, charset, -1);
	}

	/**
	 * Constructs a {@link Content} with the specified content {@link Object} and number of lines.
	 * <p>
	 * @param content   the content {@link Object}
	 * @param lineCount the number of lines of the content {@link Object}
	 */
	public Content(final Object content, final int lineCount) {
		this(content, CHARSET, lineCount);
	}

	/**
	 * Constructs a {@link Content} with the specified content {@link Object}, {@link Charset} and
	 * number of lines.
	 * <p>
	 * @param content   the content {@link Object}
	 * @param charset   the {@link Charset} of the content {@link Object}
	 * @param lineCount the number of lines of the content {@link Object}
	 */
	public Content(final Object content, final Charset charset, final int lineCount) {
		this.content = Objects.toString(content);
		this.charset = charset;
		this.lineCount = lineCount;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the content {@link String}.
	 * <p>
	 * @return the content {@link String}
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Returns the {@link Charset}.
	 * <p>
	 * @return the {@link Charset}
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * Returns the number of lines.
	 * <p>
	 * @return the number of lines
	 */
	public int getLineCount() {
		return lineCount;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is {@code "null"} or empty.
	 * <p>
	 * @return {@code true} if {@code this} is {@code "null"} or empty, {@code false} otherwise
	 */
	public boolean isNullOrEmpty() {
		return content.equals(NULL) || content.isEmpty();
	}

	/**
	 * Tests whether {@code this} is empty.
	 * <p>
	 * @return {@code true} if {@code this} is empty, {@code false} otherwise
	 */
	public boolean isEmpty() {
		return content.isEmpty();
	}

	/**
	 * Tests whether {@code this} is not {@code "null"} and non-empty.
	 * <p>
	 * @return {@code true} if {@code this} is not {@code "null"} and non-empty, {@code false}
	 *         otherwise
	 */
	public boolean isNonEmpty() {
		return !content.equals(NULL) && !content.isEmpty();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public Content clone() {
		try {
			return (Content) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Content)) {
			return false;
		}
		final Content otherContent = (Content) other;
		return Objects.equals(content, otherContent.content) &&
				Objects.equals(charset, otherContent.charset) &&
				Objects.equals(lineCount, otherContent.lineCount);
	}

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, content, charset, lineCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return content;
	}
}
