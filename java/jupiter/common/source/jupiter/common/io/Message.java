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
package jupiter.common.io;

import java.io.Serializable;

import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class Message
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 3940885528433393986L;

	/**
	 * The default stack index.
	 */
	public static final int DEFAULT_STACK_INDEX = 3;
	/**
	 * The stack index offset.
	 */
	protected static final int STACK_INDEX_OFFSET = 1;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final IO.Type type;
	protected final IO.SeverityLevel level;
	protected final String prefix;
	protected final String content;
	protected final Exception exception;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Message(final Object content) {
		this(IO.Type.OUTPUT, IO.SeverityLevel.INFO, content,
				DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}

	public Message(final Object content, final int stackIndex) {
		this(IO.Type.OUTPUT, IO.SeverityLevel.INFO, content, stackIndex + STACK_INDEX_OFFSET);
	}

	public Message(final IO.Type type, final IO.SeverityLevel level, final Object content) {
		this(type, level, content, DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}

	public Message(final IO.Type type, final IO.SeverityLevel level, final Object content,
			final int stackIndex) {
		this.type = type;
		this.level = level;
		prefix = Messages.getPrefix(type, level, stackIndex);
		this.content = Strings.toString(content);
		exception = null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public Message(final Exception exception) {
		this(IO.SeverityLevel.ERROR, exception, DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}

	public Message(final Exception exception, final int stackIndex) {
		this(IO.SeverityLevel.ERROR, exception, stackIndex + STACK_INDEX_OFFSET);
	}

	public Message(final IO.SeverityLevel level, final Exception exception) {
		this(level, exception, DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}

	public Message(final IO.SeverityLevel level, final Exception exception, final int stackIndex) {
		type = IO.Type.OUTPUT;
		this.level = level;
		prefix = Messages.getPrefix(type, level, stackIndex);
		content = Strings.toString(exception);
		this.exception = exception;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @return the type
	 */
	public IO.Type getType() {
		return type;
	}

	/**
	 * @return the level
	 */
	public IO.SeverityLevel getLevel() {
		return level;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Message)) {
			return false;
		}
		final Message otherMessage = (Message) other;
		return Objects.equals(type, otherMessage.getType()) &&
				Objects.equals(level, otherMessage.getLevel()) &&
				Objects.equals(prefix, otherMessage.getPrefix()) &&
				Objects.equals(content, otherMessage.getContent()) &&
				Objects.equals(exception, otherMessage.getException());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, type, level, prefix, content, exception);
	}

	@Override
	public String toString() {
		return (Strings.isNotEmpty(prefix) ? prefix + " " : Strings.EMPTY) + content;
	}
}
