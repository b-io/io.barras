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

import jupiter.common.exception.IllegalTypeException;
import jupiter.common.io.InputOutput.SeverityLevel;
import jupiter.common.model.ICloneable;

public abstract class IOAppender
		extends IOHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link IOAppender}.
	 */
	protected IOAppender() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified {@link Message}.
	 * <p>
	 * @param message the {@link Message} to print
	 */
	@Override
	public void println(final Message message) {
		switch (message.getLevel()) {
			case TRACE:
				trace(message.getContent());
				break;
			case DEBUG:
				debug(message.getContent());
				break;
			case TEST:
				test(message.getContent());
				break;
			case INFO:
				info(message.getContent());
				break;
			case RESULT:
				result(message.getContent());
				break;
			case WARNING:
				warn(message.getContent());
				break;
			case ERROR:
				error(message.getContent());
				break;
			case FAILURE:
				fail(message.getContent());
				break;
			default:
				throw new IllegalTypeException(message.getLevel());
		}
	}

	//////////////////////////////////////////////

	/**
	 * Prints the specified content {@link Object} indicating the severity level
	 * {@link SeverityLevel#TRACE}.
	 * <p>
	 * @param content the content {@link Object} to print
	 */
	public abstract void trace(final Object content);

	/**
	 * Prints the specified content {@link Object} indicating the severity level
	 * {@link SeverityLevel#DEBUG}.
	 * <p>
	 * @param content the content {@link Object} to print
	 */
	public abstract void debug(final Object content);

	/**
	 * Prints the specified content {@link Object} indicating the severity level
	 * {@link SeverityLevel#TEST}.
	 * <p>
	 * @param content the content {@link Object} to print
	 */
	public abstract void test(final Object content);

	/**
	 * Prints the specified content {@link Object} indicating the severity level
	 * {@link SeverityLevel#INFO}.
	 * <p>
	 * @param content the content {@link Object} to print
	 */
	public abstract void info(final Object content);

	/**
	 * Prints the specified content {@link Object} indicating the severity level
	 * {@link SeverityLevel#RESULT}.
	 * <p>
	 * @param content the content {@link Object} to print
	 */
	public abstract void result(final Object content);

	//////////////////////////////////////////////

	/**
	 * Prints the specified content {@link Object} indicating the severity level
	 * {@link SeverityLevel#WARNING}.
	 * <p>
	 * @param content the content {@link Object} to print
	 */
	public abstract void warn(final Object content);

	/**
	 * Prints the specified {@link Exception} indicating the severity level
	 * {@link SeverityLevel#WARNING}.
	 * <p>
	 * @param exception an {@link Exception}
	 */
	public abstract void warn(final Exception exception);

	//////////////////////////////////////////////

	/**
	 * Prints the specified content {@link Object} indicating the severity level
	 * {@link SeverityLevel#ERROR}.
	 * <p>
	 * @param content the content {@link Object} to print
	 */
	public abstract void error(final Object content);

	/**
	 * Prints the specified {@link Exception} indicating the severity level
	 * {@link SeverityLevel#ERROR}.
	 * <p>
	 * @param exception an {@link Exception}
	 */
	public abstract void error(final Exception exception);

	//////////////////////////////////////////////

	/**
	 * Prints the specified content {@link Object} indicating the severity level
	 * {@link SeverityLevel#FAILURE}.
	 * <p>
	 * @param content the content {@link Object} to print
	 */
	public abstract void fail(final Object content);

	/**
	 * Prints the specified {@link Exception} indicating the severity level
	 * {@link SeverityLevel#FAILURE}.
	 * <p>
	 * @param exception an {@link Exception}
	 */
	public abstract void fail(final Exception exception);


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
	public abstract IOAppender clone();
}
