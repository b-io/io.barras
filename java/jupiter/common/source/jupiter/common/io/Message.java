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

import static jupiter.common.util.Characters.SPACE;
import static jupiter.common.util.Strings.EMPTY;

import java.io.Serializable;

import jupiter.common.io.InputOutput.SeverityLevel;
import jupiter.common.io.InputOutput.Type;
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
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default IO {@link Type}.
	 */
	public static final Type DEFAULT_TYPE = Type.OUTPUT;
	/**
	 * The default standard {@link SeverityLevel}.
	 */
	public static final SeverityLevel DEFAULT_STANDARD_LEVEL = SeverityLevel.INFO;
	/**
	 * The default error {@link SeverityLevel}.
	 */
	public static final SeverityLevel DEFAULT_ERROR_LEVEL = SeverityLevel.ERROR;
	/**
	 * The default stack index.
	 */
	public static final int DEFAULT_STACK_INDEX = 1;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The IO {@link Type}.
	 */
	protected final Type type;
	/**
	 * The {@link SeverityLevel}.
	 */
	protected final SeverityLevel severityLevel;
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

	/**
	 * Constructs a {@link Message} with the specified content {@link Object}.
	 * <p>
	 * @param content the content {@link Object}
	 */
	public Message(final Object content) {
		this(DEFAULT_TYPE, DEFAULT_STANDARD_LEVEL, DEFAULT_STACK_INDEX, content);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Message} with the specified IO {@link Type} and content {@link Object}.
	 * <p>
	 * @param type    the IO {@link Type}
	 * @param content the content {@link Object}
	 */
	public Message(final Type type, final Object content) {
		this(type, DEFAULT_STANDARD_LEVEL, DEFAULT_STACK_INDEX, content);
	}

	/**
	 * Constructs a {@link Message} with the specified {@link SeverityLevel} and content
	 * {@link Object}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param content       the content {@link Object}
	 */
	public Message(final SeverityLevel severityLevel, final Object content) {
		this(DEFAULT_TYPE, severityLevel, DEFAULT_STACK_INDEX, content);
	}

	/**
	 * Constructs a {@link Message} with the specified stack index and content {@link Object}.
	 * <p>
	 * @param stackIndex the stack index
	 * @param content    the content {@link Object}
	 */
	public Message(final int stackIndex, final Object content) {
		this(DEFAULT_TYPE, DEFAULT_STANDARD_LEVEL, stackIndex, content);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Message} with the specified {@link SeverityLevel}, stack index and
	 * content {@link Object}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param content       the content {@link Object}
	 */
	public Message(final SeverityLevel severityLevel, final int stackIndex, final Object content) {
		this(DEFAULT_TYPE, severityLevel, stackIndex, content);
	}

	/**
	 * Constructs a {@link Message} with the specified IO {@link Type}, stack index and content
	 * {@link Object}.
	 * <p>
	 * @param type       the IO {@link Type}
	 * @param stackIndex the stack index
	 * @param content    the content {@link Object}
	 */
	public Message(final Type type, final int stackIndex, final Object content) {
		this(type, DEFAULT_STANDARD_LEVEL, stackIndex, content);
	}

	/**
	 * Constructs a {@link Message} with the specified IO {@link Type}, {@link SeverityLevel} and
	 * content {@link Object}.
	 * <p>
	 * @param type          the IO {@link Type}
	 * @param severityLevel the {@link SeverityLevel}
	 * @param content       the content {@link Object}
	 */
	public Message(final Type type, final SeverityLevel severityLevel, final Object content) {
		this(type, severityLevel, DEFAULT_STACK_INDEX, content);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Message} with the specified IO {@link Type}, {@link SeverityLevel}, stack
	 * index and content {@link Object}.
	 * <p>
	 * @param type          the IO {@link Type}
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param content       the content {@link Object}
	 */
	public Message(final Type type, final SeverityLevel severityLevel, final int stackIndex,
			final Object content) {
		this.type = type;
		this.severityLevel = severityLevel;
		prefix = Messages.getPrefix(type, severityLevel, stackIndex + 1);
		this.content = Objects.toString(content);
		exception = null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Message} with the specified {@link Exception}.
	 * <p>
	 * @param exception the {@link Exception}
	 */
	public Message(final Exception exception) {
		this(DEFAULT_ERROR_LEVEL, DEFAULT_STACK_INDEX, exception);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Message} with the specified {@link SeverityLevel} and {@link Exception}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param exception     the {@link Exception}
	 */
	public Message(final SeverityLevel severityLevel, final Exception exception) {
		this(severityLevel, DEFAULT_STACK_INDEX, exception);
	}

	/**
	 * Constructs a {@link Message} with the specified stack index and {@link Exception}.
	 * <p>
	 * @param stackIndex the stack index
	 * @param exception  the {@link Exception}
	 */
	public Message(final int stackIndex, final Exception exception) {
		this(DEFAULT_ERROR_LEVEL, stackIndex, exception);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link Message} with the specified {@link SeverityLevel}, stack index and
	 * {@link Exception}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param exception     the {@link Exception}
	 */
	public Message(final SeverityLevel severityLevel, final int stackIndex,
			final Exception exception) {
		type = DEFAULT_TYPE;
		this.severityLevel = severityLevel;
		prefix = Messages.getPrefix(type, severityLevel, stackIndex + 1);
		content = Objects.toString(exception);
		this.exception = exception;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the IO {@link Type}.
	 * <p>
	 * @return the IO {@link Type}
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the {@link SeverityLevel}.
	 * <p>
	 * @return the {@link SeverityLevel}
	 */
	public SeverityLevel getSeverityLevel() {
		return severityLevel;
	}

	/**
	 * Returns the prefix {@link String}.
	 * <p>
	 * @return the prefix {@link String}
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Returns the content {@link String}.
	 * <p>
	 * @return the content {@link String}
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Returns the {@link Exception}.
	 * <p>
	 * @return the {@link Exception}
	 */
	public Exception getException() {
		return exception;
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
	public Message clone() {
		try {
			return (Message) super.clone();
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
		if (other == null || !(other instanceof Message)) {
			return false;
		}
		final Message otherMessage = (Message) other;
		return Objects.equals(type, otherMessage.type) &&
				Objects.equals(severityLevel, otherMessage.severityLevel) &&
				Objects.equals(prefix, otherMessage.prefix) &&
				Objects.equals(content, otherMessage.content) &&
				Objects.equals(exception, otherMessage.exception);
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
		return Objects.hashCode(serialVersionUID, type, severityLevel, prefix, content, exception);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return (Strings.isNonEmpty(prefix) ? prefix + SPACE : EMPTY).concat(content);
	}
}
