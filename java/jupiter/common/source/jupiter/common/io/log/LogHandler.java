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
package jupiter.common.io.log;

import java.io.File;
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
	 * The default log directory.
	 */
	protected static final String DEFAULT_LOG_DIR = Files.getPath() + "\\" + "logs";

	/**
	 * The default output log name.
	 */
	protected static final String DEFAULT_OUTPUT_LOG_NAME = "jupiter.out.log";
	/**
	 * The default error log name.
	 */
	protected static final String DEFAULT_ERROR_LOG_NAME = "jupiter.err.log";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The log directory.
	 */
	protected volatile String logDir;

	/**
	 * The output log path.
	 */
	protected volatile String outputLogPath;
	/**
	 * The internal lock of the output log.
	 */
	protected final Lock outputLogLock = new ReentrantLock(true);
	protected final StringBuilder outputLineBuilder = Strings.createBuilder();

	/**
	 * The error log path.
	 */
	protected volatile String errorLogPath;
	/**
	 * The internal lock of the error log.
	 */
	protected final Lock errorLogLock = new ReentrantLock(true);
	protected final StringBuilder errorLineBuilder = Strings.createBuilder();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public LogHandler() {
		this(DEFAULT_LOG_DIR);
	}

	public LogHandler(final String logDir) {
		this(logDir, DEFAULT_OUTPUT_LOG_NAME, DEFAULT_ERROR_LOG_NAME);
	}

	public LogHandler(final String logDir, final String outputLogName, final String errorLogName) {
		super();
		this.logDir = logDir;
		outputLogPath = getPath(outputLogName);
		errorLogPath = getPath(errorLogName);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the path name of the specified log.
	 * <p>
	 * @param logName the name of the log
	 * <p>
	 * @return the path name of the specified log
	 */
	protected String getPath(final String logName) {
		return logDir + File.separator + logName;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the log directory.
	 * <p>
	 * @param logDir a {@link String}
	 */
	public void setLogDir(final String logDir) {
		outputLogLock.lock();
		try {
			errorLogLock.lock();
			try {
				this.logDir = logDir;
			} finally {
				errorLogLock.unlock();
			}
		} finally {
			outputLogLock.unlock();
		}
	}

	/**
	 * Sets the name of the output log.
	 * <p>
	 * @param outputLogName a {@link String}
	 */
	public void setOutputLog(final String outputLogName) {
		outputLogLock.lock();
		try {
			outputLogPath = getPath(outputLogName);
		} finally {
			outputLogLock.unlock();
		}
	}

	/**
	 * Sets the name of the error log.
	 * <p>
	 * @param errorLogName a {@link String}
	 */
	public void setErrorLog(final String errorLogName) {
		errorLogLock.lock();
		try {
			errorLogPath = getPath(errorLogName);
		} finally {
			errorLogLock.unlock();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates all the directories of the logs path name.
	 * <p>
	 * @return {@code true} if the directories are created, {@code false} otherwise
	 */
	protected boolean createDirectories() {
		return Files.createDirectories(logDir);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified content to the line buffer with the specified type. Note that the line
	 * buffer is written to the log when either {@code println} or {@code flush} is called.
	 * <p>
	 * @param content the {@link Object} to write
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
	 * @param content the {@link Object} to write
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
				if (createDirectories()) {
					updateLogLine(content, isError);
					flush(isError);
				}
			} finally {
				errorLogLock.unlock();
			}
		} else {
			outputLogLock.lock();
			try {
				if (createDirectories()) {
					updateLogLine(content, isError);
					flush(isError);
				}
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
		Files.writeLine(getLogLine(isError), isError ? errorLogPath : outputLogPath);
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
			if (Files.exists(outputLogPath)) {
				Files.delete(outputLogPath);
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
			if (Files.exists(errorLogPath)) {
				Files.delete(errorLogPath);
			}
		} finally {
			errorLogLock.unlock();
		}
	}
}
