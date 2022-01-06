/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
import java.util.List;

import jupiter.common.io.IOHandler;
import jupiter.common.io.InputOutput;
import jupiter.common.io.InputOutput.SeverityLevel;
import jupiter.common.io.console.ConsoleHandler;
import jupiter.common.io.log.LogHandler;
import jupiter.common.model.ICloneable;
import jupiter.common.properties.Properties;
import jupiter.common.util.Objects;

public class AndroidInputOutput
		implements ICloneable<AndroidInputOutput>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link SeverityLevel}.
	 */
	public static final SeverityLevel DEFAULT_SEVERITY_LEVEL = SeverityLevel.TRACE;
	/**
	 * The default stack index.
	 */
	public static final int DEFAULT_STACK_INDEX = 0;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The offset of the stack index.
	 */
	public static volatile int STACK_INDEX_OFFSET = 1;


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
	 * Constructs an {@link AndroidInputOutput} by default.
	 */
	public AndroidInputOutput() {
		this(DEFAULT_SEVERITY_LEVEL);
	}

	/**
	 * Constructs an {@link AndroidInputOutput} with the specified {@link SeverityLevel}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 */
	public AndroidInputOutput(final SeverityLevel severityLevel) {
		this(severityLevel, DEFAULT_STACK_INDEX);
	}

	/**
	 * Constructs an {@link AndroidInputOutput} with the specified {@link SeverityLevel} and stack
	 * index.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 */
	public AndroidInputOutput(final SeverityLevel severityLevel, final int stackIndex) {
		super();

		// Set the attributes
		io = new InputOutput(severityLevel, STACK_INDEX_OFFSET + stackIndex);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs an {@link AndroidInputOutput} with the specified {@link SeverityLevel}, stack
	 * index and {@link ConsoleHandler}.
	 * <p>
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param stackIndex     the stack index
	 * @param consoleHandler the {@link ConsoleHandler}
	 */
	public AndroidInputOutput(final SeverityLevel severityLevel, final int stackIndex,
			final ConsoleHandler consoleHandler) {
		super();

		// Set the attributes
		io = new InputOutput(severityLevel, STACK_INDEX_OFFSET + stackIndex, consoleHandler);
	}

	/**
	 * Constructs an {@link AndroidInputOutput} with the specified {@link SeverityLevel}, stack
	 * index and {@link LogHandler}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param logHandler    the {@link LogHandler}
	 */
	public AndroidInputOutput(final SeverityLevel severityLevel, final int stackIndex,
			final LogHandler logHandler) {
		super();

		// Set the attributes
		io = new InputOutput(severityLevel, STACK_INDEX_OFFSET + stackIndex, logHandler);
	}

	/**
	 * Constructs an {@link AndroidInputOutput} with the specified {@link SeverityLevel}, stack
	 * index, {@link ConsoleHandler} and {@link LogHandler}.
	 * <p>
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param stackIndex     the stack index
	 * @param consoleHandler the {@link ConsoleHandler}
	 * @param logHandler     the {@link LogHandler}
	 */
	public AndroidInputOutput(final SeverityLevel severityLevel, final int stackIndex,
			final ConsoleHandler consoleHandler, final LogHandler logHandler) {
		super();

		// Set the attributes
		io = new InputOutput(severityLevel, STACK_INDEX_OFFSET + stackIndex, consoleHandler,
				logHandler);
	}

	/**
	 * Constructs an {@link AndroidInputOutput} with the specified {@link SeverityLevel}, stack
	 * index and {@link List} of {@link IOHandler}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param handlers      the {@link List} of {@link IOHandler}
	 */
	public AndroidInputOutput(final SeverityLevel severityLevel, final int stackIndex,
			final List<IOHandler> handlers) {
		super();

		// Set the attributes
		io = new InputOutput(severityLevel, STACK_INDEX_OFFSET + stackIndex, handlers);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link AndroidInputOutput} loaded from the specified {@link Properties}
	 * containing the specified {@link SeverityLevel}, stack index and {@link List} of
	 * {@link IOHandler}.
	 * <p>
	 * @param properties the {@link Properties} to load
	 */
	public AndroidInputOutput(final Properties properties) {
		super();

		// Set the attributes
		io = new InputOutput(properties);
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
	public synchronized void show(final Context context, final Object message) {
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
		if (io.getSeverityLevel().isTrace()) {
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
		if (io.getSeverityLevel().isDebug()) {
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
		if (io.getSeverityLevel().isTest()) {
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
		if (io.getSeverityLevel().isInfo()) {
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
		if (io.getSeverityLevel().isResult()) {
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
		if (io.getSeverityLevel().isWarning()) {
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
		if (io.getSeverityLevel().isError()) {
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
		if (io.getSeverityLevel().isFailure()) {
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
	public AndroidInputOutput clone() {
		try {
			final AndroidInputOutput clone = (AndroidInputOutput) super.clone();
			clone.io = Objects.clone(io);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}
}
