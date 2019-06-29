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
package jupiter.common.thread;

import static jupiter.common.util.Strings.SPACE;

import java.io.Serializable;
import jupiter.common.io.Message;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link Result} is a wrapper around an {@code O} output.
 * <p>
 * @param <O> the output type
 */
public class Result<O>
		implements ICloneable<Result<O>>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The stack index offset.
	 */
	protected static final int STACK_INDEX_OFFSET = 1;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@code O} output.
	 */
	protected O output;
	/**
	 * The {@link Message}.
	 */
	protected Message message;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Result} with the specified {@code O} output.
	 * <p>
	 * @param output the {@code O} output
	 */
	public Result(final O output) {
		this.output = output;
		message = null;
	}

	/**
	 * Constructs a {@link Result} with the specified {@link Message}.
	 * <p>
	 * @param message the {@link Message}
	 */
	public Result(final Message message) {
		output = null;
		this.message = message;
	}

	/**
	 * Constructs a {@link Result} with the specified {@code O} output and {@link Message}.
	 * <p>
	 * @param output  the {@code O} output
	 * @param message the {@link Message}
	 */
	public Result(final O output, final Message message) {
		this.output = output;
		this.message = message;
	}

	/**
	 * Constructs a {@link Result} with the specified {@link Exception}.
	 * <p>
	 * @param exception the {@link Exception}
	 */
	public Result(final Exception exception) {
		this.output = null;
		this.message = new Message(exception, Message.DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code O} output.
	 * <p>
	 * @return the {@code O} output
	 */
	public O getOutput() {
		return output;
	}

	/**
	 * Returns the {@link Message}.
	 * <p>
	 * @return the {@link Message}
	 */
	public Message getMessage() {
		return message;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@code O} output.
	 * <p>
	 * @param output an {@code O} output
	 */
	public void setOutput(final O output) {
		this.output = output;
	}

	/**
	 * Sets the {@link Message}.
	 * <p>
	 * @param message a {@link Message}
	 */
	public void setMessage(final Message message) {
		this.message = message;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public Result<O> clone() {
		try {
			final Result<O> clone = (Result<O>) super.clone();
			clone.output = Objects.clone(output);
			clone.message = Objects.clone(message);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new RuntimeException(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality
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
		if (other == null || !(other instanceof Result)) {
			return false;
		}
		final Result<?> otherResult = (Result<?>) other;
		return Objects.equals(output, otherResult.output) &&
				Objects.equals(message, otherResult.message);
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
		return Objects.hashCode(serialVersionUID, output, message);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		final StringBuilder builder = Strings.createBuilder();
		if (output != null) {
			builder.append(output);
			if (message != null) {
				builder.append(SPACE).append(Strings.parenthesize(message.getContent()));
			}
		} else if (message != null) {
			builder.append(message.getContent());
		}
		return builder.toString();
	}
}
