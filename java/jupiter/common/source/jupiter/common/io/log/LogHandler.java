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
package jupiter.common.io.log;

import static jupiter.common.io.IO.IO;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jupiter.common.io.IOHandler;
import jupiter.common.io.file.Files;
import jupiter.common.test.Arguments;
import jupiter.common.util.Strings;

public class LogHandler
		extends IOHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The default {@link String} log directory.
	 */
	protected static final String DEFAULT_LOG_DIR = Files.getPath() + "\\" + "logs";

	/**
	 * The default {@link String} output log name.
	 */
	protected static final String DEFAULT_OUTPUT_LOG_NAME = "jupiter.out.log";
	/**
	 * The default {@link String} error log name.
	 */
	protected static final String DEFAULT_ERROR_LOG_NAME = "jupiter.err.log";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The log directory {@link File}.
	 */
	protected volatile File logDir;

	/**
	 * The output log {@link File}.
	 */
	protected volatile File outputLog;
	/**
	 * The internal {@link Lock} of the output log {@link File}.
	 */
	protected final Lock outputLogLock = new ReentrantLock(true);
	protected final StringBuilder outputLineBuilder = Strings.createBuilder();

	/**
	 * The error log {@link File}.
	 */
	protected volatile File errorLog;
	/**
	 * The internal {@link Lock} of the error log {@link File}.
	 */
	protected final Lock errorLogLock = new ReentrantLock(true);
	protected final StringBuilder errorLineBuilder = Strings.createBuilder();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public LogHandler() {
		this(DEFAULT_LOG_DIR);
	}

	public LogHandler(final String logDirPath) {
		this(logDirPath, DEFAULT_OUTPUT_LOG_NAME, DEFAULT_ERROR_LOG_NAME);
	}

	public LogHandler(final String logDirPath, final String outputLogName,
			final String errorLogName) {
		super();
		setLogDir(logDirPath);
		setOutputLog(outputLogName);
		setErrorLog(errorLogName);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} path to the specified log.
	 * <p>
	 * @param logName the name of the log
	 * <p>
	 * @return the {@link String} path to the specified log
	 */
	protected String getPath(final String logName) {
		return Files.getPath(logDir) + File.separator + logName;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the log directory.
	 * <p>
	 * @param logDirPath a {@link String}
	 */
	public void setLogDir(final String logDirPath) {
		outputLogLock.lock();
		try {
			errorLogLock.lock();
			try {
				logDir = new File(logDirPath);
			} finally {
				errorLogLock.unlock();
			}
		} finally {
			outputLogLock.unlock();
		}
	}

	/**
	 * Sets the output log.
	 * <p>
	 * @param outputLogName a {@link String}
	 */
	public void setOutputLog(final String outputLogName) {
		outputLogLock.lock();
		try {
			outputLog = new File(getPath(outputLogName));
		} finally {
			outputLogLock.unlock();
		}
	}

	/**
	 * Sets the error log.
	 * <p>
	 * @param errorLogName a {@link String}
	 */
	public void setErrorLog(final String errorLogName) {
		errorLogLock.lock();
		try {
			errorLog = new File(getPath(errorLogName));
		} finally {
			errorLogLock.unlock();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates all the directories of the log directory.
	 * <p>
	 * @throws IOException       if there is a problem creating the directories
	 * @throws SecurityException if there is a permission problem
	 */
	protected void createDirs()
			throws IOException {
		Files.createDirs(logDir);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified content to the line buffer with the specified type. Note that the line
	 * buffer is written to the log when either {@code println} or {@code flush} is called.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the error log or in the output log
	 */
	@Override
	public void print(final Object content, final boolean isError) {
		// Check the arguments
		Arguments.requireNonNull(content);

		// Update the log line
		updateLogLine(content, isError);
	}

	/**
	 * Writes the specified content to the log with the specified type.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the error log or in the output log
	 */
	@Override
	public void println(final Object content, final boolean isError) {
		// Check the arguments
		Arguments.requireNonNull(content);

		// Print the content
		if (isError) {
			errorLogLock.lock();
			try {
				createDirs();
				updateLogLine(content, isError);
				flush(isError);
			} catch (final IOException ex) {
				IO.error(ex);
			} finally {
				errorLogLock.unlock();
			}
		} else {
			outputLogLock.lock();
			try {
				createDirs();
				updateLogLine(content, isError);
				flush(isError);
			} catch (final IOException ex) {
				IO.error(ex);
			} finally {
				outputLogLock.unlock();
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void updateLogLine(final Object content, final boolean isError) {
		if (isError) {
			errorLineBuilder.append(content);
		} else {
			outputLineBuilder.append(content);
		}
	}

	protected String getLogLine(final boolean isError) {
		return isError ? errorLineBuilder.toString() : outputLineBuilder.toString();
	}

	protected void clearLogLine(final boolean isError) {
		if (isError) {
			errorLineBuilder.setLength(0);
		} else {
			outputLineBuilder.setLength(0);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void flush() {
		flush(false);
		flush(true);
	}

	public void flush(final boolean isError) {
		Files.writeLine(getLogLine(isError), isError ? errorLog : outputLog);
		clearLogLine(isError);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEANERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Deletes the logs.
	 */
	@Override
	public void clear() {
		if (Files.exists(logDir)) {
			outputLogLock.lock();
			deleteOutputLog();
			try {
				errorLogLock.lock();
				deleteErrorLog();
				try {
					Files.delete(logDir);
				} finally {
					errorLogLock.unlock();
				}
			} finally {
				outputLogLock.unlock();
			}
		}
	}

	/**
	 * Deletes the output log.
	 */
	public void deleteOutputLog() {
		outputLogLock.lock();
		try {
			if (Files.exists(outputLog)) {
				Files.delete(outputLog);
			}
		} finally {
			outputLogLock.unlock();
		}
	}

	/**
	 * Deletes the error log.
	 */
	public void deleteErrorLog() {
		errorLogLock.lock();
		try {
			if (Files.exists(errorLog)) {
				Files.delete(errorLog);
			}
		} finally {
			errorLogLock.unlock();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see Cloneable
	 */
	@Override
	public LogHandler clone() {
		return new LogHandler(Files.getPath(logDir), outputLog.getName(), errorLog.getName());
	}
}
