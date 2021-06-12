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
package jupiter.log;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.io.file.Files.SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import jupiter.common.io.IOHandler;
import jupiter.common.io.InputOutput;
import jupiter.common.io.InputOutput.SeverityLevel;
import jupiter.common.io.Resources;
import jupiter.common.io.console.ConsoleHandler;
import jupiter.common.io.file.Files;
import jupiter.common.io.log.LogHandler;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class IOLog4j
		extends AppenderSkeleton
		implements ICloneable<IOLog4j>, Serializable {

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
	public static volatile int STACK_INDEX_OFFSET = 6;


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
	 * Constructs an {@link IOLog4j} by default.
	 */
	public IOLog4j() {
		this(DEFAULT_SEVERITY_LEVEL);
	}

	/**
	 * Constructs an {@link IOLog4j} with the specified {@link SeverityLevel}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 */
	public IOLog4j(final SeverityLevel severityLevel) {
		this(severityLevel, DEFAULT_STACK_INDEX);
	}

	/**
	 * Constructs an {@link IOLog4j} with the specified {@link SeverityLevel} and stack index.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 */
	public IOLog4j(final SeverityLevel severityLevel, final int stackIndex) {
		super();

		// Set the attributes
		io = new InputOutput(severityLevel, STACK_INDEX_OFFSET + stackIndex);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs an {@link IOLog4j} with the specified {@link SeverityLevel}, stack index and
	 * {@link ConsoleHandler}.
	 * <p>
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param stackIndex     the stack index
	 * @param consoleHandler the {@link ConsoleHandler}
	 */
	public IOLog4j(final SeverityLevel severityLevel, final int stackIndex,
			final ConsoleHandler consoleHandler) {
		super();

		// Set the attributes
		io = new InputOutput(severityLevel, STACK_INDEX_OFFSET + stackIndex, consoleHandler);
	}

	/**
	 * Constructs an {@link IOLog4j} with the specified {@link SeverityLevel}, stack index and
	 * {@link LogHandler}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param logHandler    the {@link LogHandler}
	 */
	public IOLog4j(final SeverityLevel severityLevel, final int stackIndex,
			final LogHandler logHandler) {
		super();

		// Set the attributes
		io = new InputOutput(severityLevel, STACK_INDEX_OFFSET + stackIndex, logHandler);
	}

	/**
	 * Constructs an {@link IOLog4j} with the specified {@link SeverityLevel}, stack index,
	 * {@link ConsoleHandler} and {@link LogHandler}.
	 * <p>
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param stackIndex     the stack index
	 * @param consoleHandler the {@link ConsoleHandler}
	 * @param logHandler     the {@link LogHandler}
	 */
	public IOLog4j(final SeverityLevel severityLevel, final int stackIndex,
			final ConsoleHandler consoleHandler, final LogHandler logHandler) {
		super();

		// Set the attributes
		io = new InputOutput(severityLevel, STACK_INDEX_OFFSET + stackIndex, consoleHandler,
				logHandler);
	}

	/**
	 * Constructs an {@link IOLog4j} with the specified {@link SeverityLevel}, stack index and
	 * {@link List} of {@link IOHandler}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param handlers      the {@link List} of {@link IOHandler}
	 */
	public IOLog4j(final SeverityLevel severityLevel, final int stackIndex,
			final List<IOHandler> handlers) {
		super();

		// Set the attributes
		io = new InputOutput(severityLevel, STACK_INDEX_OFFSET + stackIndex, handlers);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the path to the configuration file by default.
	 */
	public static void setConfigurationPath() {
		setConfigurationPath("log4j.properties");
	}

	/**
	 * Sets the path to the configuration file.
	 * <p>
	 * @param fileName a {@link String}
	 */
	public static void setConfigurationPath(final String fileName) {
		try {
			final File log4j = new File(Files.getPath().concat(SEPARATOR).concat(fileName));
			System.setProperty("log4j.configuration", "file:///" + Files.getCanonicalPath(log4j));
		} catch (final IOException ex) {
			IO.error(ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void append(final LoggingEvent event) {
		try {
			final Level level = event.getLevel();
			final Object message = event.getMessage();
			if (level == Level.TRACE) {
				io.trace(message);
			} else if (level == Level.DEBUG) {
				io.debug(message);
			} else if (level == Level.INFO) {
				io.info(message);
			} else if (level == Level.WARN) {
				io.warn(message);
			} else if (level == Level.ERROR) {
				io.error(message);
			} else if (level == Level.FATAL) {
				io.fail(message);
			} else {
				io.result(message);
			}
		} catch (final Exception ignored) {
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean requiresLayout() {
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLOSEABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Closes {@code this}.
	 */
	public void close() {
		Resources.close(io);
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
	public IOLog4j clone() {
		try {
			final IOLog4j clone = (IOLog4j) super.clone();
			clone.io = Objects.clone(io);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}
}
