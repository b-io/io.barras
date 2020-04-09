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
package jupiter.mobile.android.io;

import android.content.Context;
import android.widget.Toast;

import java.io.Serializable;

import jupiter.common.io.InputOutput;
import jupiter.common.io.InputOutput.SeverityLevel;
import jupiter.common.io.Message;
import jupiter.common.io.console.ConsoleHandler;
import jupiter.common.io.log.LogHandler;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

public class AIO
		implements ICloneable<AIO>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link AIO}.
	 */
	public static final AIO AIO = new AIO();

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The stack index offset.
	 */
	protected static final int STACK_INDEX_OFFSET = 1;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link InputOutput}.
	 */
	protected InputOutput io;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link AIO} by default.
	 */
	public AIO() {
		io = new InputOutput(Message.DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}

	/**
	 * Constructs an {@link AIO} with the specified {@link SeverityLevel}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 */
	public AIO(final SeverityLevel severityLevel) {
		io = new InputOutput(Message.DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET, severityLevel);
	}

	/**
	 * Constructs an {@link AIO} with the specified {@link SeverityLevel} and
	 * {@link ConsoleHandler}.
	 * <p>
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param consoleHandler the {@link ConsoleHandler}
	 */
	public AIO(final SeverityLevel severityLevel, final ConsoleHandler consoleHandler) {
		io = new InputOutput(Message.DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET, severityLevel,
				consoleHandler);
	}

	/**
	 * Constructs an {@link AIO} with the specified {@link SeverityLevel}, {@link ConsoleHandler}
	 * and {@link LogHandler}.
	 * <p>
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param consoleHandler the {@link ConsoleHandler}
	 * @param logHandler     the {@link LogHandler}
	 */
	public AIO(final SeverityLevel severityLevel, final ConsoleHandler consoleHandler,
			final LogHandler logHandler) {
		io = new InputOutput(Message.DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET, severityLevel,
				consoleHandler, logHandler);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shows the specified message {@link Object} on the screen.
	 * <p>
	 * @param context the {@link Context} of Android
	 * @param message the message {@link Object} to show
	 */
	public void show(final Context context, final Object message) {
		Toast.makeText(context, Objects.toString(message), Toast.LENGTH_LONG).show();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shows the specified message {@link Object} on the screen indicating the severity level
	 * {@link SeverityLevel#TRACE}.
	 * <p>
	 * @param context the {@link Context} of Android
	 * @param message the message {@link Object} to show
	 */
	public void trace(final Context context, final Object message) {
		if (SeverityLevel.TRACE.toInt() >= io.getSeverityLevel().toInt()) {
			show(context, io.trace(message));
		}
	}

	/**
	 * Shows the specified message {@link Object} on the screen indicating the severity level
	 * {@link SeverityLevel#DEBUG}.
	 * <p>
	 * @param context the {@link Context} of Android
	 * @param message the message {@link Object} to show
	 */
	public void debug(final Context context, final Object message) {
		if (SeverityLevel.DEBUG.toInt() >= io.getSeverityLevel().toInt()) {
			show(context, io.debug(message));
		}
	}

	/**
	 * Shows the specified message {@link Object} on the screen indicating the severity level
	 * {@link SeverityLevel#TEST}.
	 * <p>
	 * @param context the {@link Context} of Android
	 * @param message the message {@link Object} to show
	 */
	public void test(final Context context, final Object message) {
		if (SeverityLevel.TEST.toInt() >= io.getSeverityLevel().toInt()) {
			show(context, io.test(message));
		}
	}

	/**
	 * Shows the specified message {@link Object} on the screen indicating the severity level
	 * {@link SeverityLevel#INFO}.
	 * <p>
	 * @param context the {@link Context} of Android
	 * @param message the message {@link Object} to show
	 */
	public void info(final Context context, final Object message) {
		if (SeverityLevel.INFO.toInt() >= io.getSeverityLevel().toInt()) {
			show(context, io.info(message));
		}
	}

	/**
	 * Shows the specified message {@link Object} on the screen indicating the severity level
	 * {@link SeverityLevel#RESULT}.
	 * <p>
	 * @param context the {@link Context} of Android
	 * @param message the message {@link Object} to show
	 */
	public void result(final Context context, final Object message) {
		if (SeverityLevel.RESULT.toInt() >= io.getSeverityLevel().toInt()) {
			show(context, io.result(message));
		}
	}

	/**
	 * Shows the specified message {@link Object} on the screen indicating the severity level
	 * {@link SeverityLevel#WARNING}.
	 * <p>
	 * @param context the {@link Context} of Android
	 * @param message the message {@link Object} to show
	 */
	public void warn(final Context context, final Object message) {
		if (SeverityLevel.WARNING.toInt() >= io.getSeverityLevel().toInt()) {
			show(context, io.warn(message));
		}
	}

	/**
	 * Shows the specified message {@link Object} on the screen indicating the severity level
	 * {@link SeverityLevel#ERROR}.
	 * <p>
	 * @param context the {@link Context} of Android
	 * @param message the message {@link Object} to show
	 */
	public void error(final Context context, final Object message) {
		if (SeverityLevel.ERROR.toInt() >= io.getSeverityLevel().toInt()) {
			show(context, io.error(message));
		}
	}

	/**
	 * Shows the specified message {@link Object} on the screen indicating the severity level
	 * {@link SeverityLevel#FAILURE}.
	 * <p>
	 * @param context the {@link Context} of Android
	 * @param message the message {@link Object} to show
	 */
	public void fail(final Context context, final Object message) {
		if (SeverityLevel.FAILURE.toInt() >= io.getSeverityLevel().toInt()) {
			show(context, io.fail(message));
		}
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
	public AIO clone() {
		try {
			final AIO clone = (AIO) super.clone();
			clone.io = Objects.clone(io);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}
}
