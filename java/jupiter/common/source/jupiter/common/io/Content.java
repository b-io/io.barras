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
package jupiter.common.io;

import static jupiter.common.util.Formats.DEFAULT_CHARSET;

import java.io.Serializable;
import java.nio.charset.Charset;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

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

	protected final String content;
	protected final Charset charset;
	protected final int lineCount;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Content(final Object content) {
		this(content, DEFAULT_CHARSET, -1);
	}

	public Content(final Object content, final Charset charset) {
		this(content, charset, -1);
	}

	public Content(final Object content, final int lineCount) {
		this(content, DEFAULT_CHARSET, lineCount);
	}

	public Content(final Object content, final Charset charset, final int lineCount) {
		this.content = Strings.toString(content);
		this.charset = charset;
		this.lineCount = lineCount;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the content.
	 * <p>
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Returns the character set.
	 * <p>
	 * @return the character set
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

	public boolean isNullOrEmpty() {
		return Strings.isNullOrEmpty(content);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Content clone() {
		try {
			return (Content) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new AssertionError(ex.getMessage(), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

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

	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, content, charset, lineCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return content;
	}
}
