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

import static jupiter.common.util.Strings.EMPTY;
import static jupiter.common.util.Strings.SPACE;

import java.io.Serializable;

import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.io.IO.Type;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class Message
		implements ICloneable<Message>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 3246094630264042616L;

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

	/**
	 * The {@link Type}.
	 */
	protected final Type type;
	/**
	 * The {@link SeverityLevel}.
	 */
	protected final SeverityLevel level;
	/**
	 * The prefix {@link String}.
	 */
	protected final String prefix;
	/**
	 * The content {@link String}.
	 */
	protected final String content;
	/**
	 * The {@link Exception}.
	 */
	protected final Exception exception;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Message(final Object content) {
		this(Type.OUTPUT, SeverityLevel.INFO, content, DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}

	public Message(final Object content, final int stackIndex) {
		this(Type.OUTPUT, SeverityLevel.INFO, content, stackIndex + STACK_INDEX_OFFSET);
	}

	public Message(final Type type, final SeverityLevel level, final Object content) {
		this(type, level, content, DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}

	public Message(final Type type, final SeverityLevel level, final Object content,
			final int stackIndex) {
		this.type = type;
		this.level = level;
		prefix = Messages.getPrefix(type, level, stackIndex);
		this.content = Strings.toString(content);
		exception = null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public Message(final Exception exception) {
		this(SeverityLevel.ERROR, exception, DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}

	public Message(final Exception exception, final int stackIndex) {
		this(SeverityLevel.ERROR, exception, stackIndex + STACK_INDEX_OFFSET);
	}

	public Message(final SeverityLevel level, final Exception exception) {
		this(level, exception, DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}

	public Message(final SeverityLevel level, final Exception exception, final int stackIndex) {
		type = Type.OUTPUT;
		this.level = level;
		prefix = Messages.getPrefix(type, level, stackIndex);
		content = Strings.toString(exception);
		this.exception = exception;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the type.
	 * <p>
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the level.
	 * <p>
	 * @return the level
	 */
	public SeverityLevel getLevel() {
		return level;
	}

	/**
	 * Returns the prefix.
	 * <p>
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Returns the content.
	 * <p>
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Returns the exception.
	 * <p>
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public Message clone() {
		try {
			return (Message) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new AssertionError(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the {@link Object} to compare against for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof Message)) {
			return false;
		}
		final Message otherMessage = (Message) other;
		return Objects.equals(type, otherMessage.type) &&
				Objects.equals(level, otherMessage.level) &&
				Objects.equals(prefix, otherMessage.prefix) &&
				Objects.equals(content, otherMessage.content) &&
				Objects.equals(exception, otherMessage.exception);
	}

	/**
	 * Returns the hash code for {@code this}.
	 * <p>
	 * @return the hash code for {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, type, level, prefix, content, exception);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return (Strings.isNotEmpty(prefix) ? prefix + SPACE : EMPTY) + content;
	}
}
