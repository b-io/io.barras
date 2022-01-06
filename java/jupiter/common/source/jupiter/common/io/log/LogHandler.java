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
package jupiter.common.io.log;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.io.file.Files.SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jupiter.common.io.IOHandler;
import jupiter.common.io.Resources;
import jupiter.common.io.file.FileHandler;
import jupiter.common.io.file.Files;
import jupiter.common.model.ICloneable;
import jupiter.common.test.Arguments;
import jupiter.common.util.Objects;

public class LogHandler
		extends IOHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link String} log directory.
	 */
	public static final String DEFAULT_LOG_DIR_PATH = Files.getPath()
			.concat(SEPARATOR)
			.concat("logs");

	/**
	 * The default {@link String} output log name.
	 */
	public static final String DEFAULT_OUTPUT_LOG_NAME = "jupiter.out.log";
	/**
	 * The default {@link String} error log name.
	 */
	public static final String DEFAULT_ERROR_LOG_NAME = "jupiter.err.log";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The log directory {@link File}.
	 */
	protected File logDir;

	/**
	 * The output log {@link FileHandler}.
	 */
	protected FileHandler outputLogHandler;
	/**
	 * The internal {@link Lock} of the output log {@link File} to handle.
	 */
	protected final Lock outputLogLock = new ReentrantLock(true);

	/**
	 * The error log {@link FileHandler}.
	 */
	protected FileHandler errorLogHandler;
	/**
	 * The internal {@link Lock} of the error log {@link File} to handle.
	 */
	protected final Lock errorLogLock = new ReentrantLock(true);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link LogHandler} by default.
	 */
	public LogHandler() {
		this(DEFAULT_LOG_DIR_PATH);
	}

	/**
	 * Constructs a {@link LogHandler} with the specified path to the log directory.
	 * <p>
	 * @param logDirPath the path to the log directory
	 */
	public LogHandler(final String logDirPath) {
		this(logDirPath, DEFAULT_OUTPUT_LOG_NAME, DEFAULT_ERROR_LOG_NAME);
	}

	/**
	 * Constructs a {@link LogHandler} with the specified path to the log directory, output log and
	 * error log.
	 * <p>
	 * @param logDirPath    the path to the log directory
	 * @param outputLogName the name to the output log to handle
	 * @param errorLogName  the name to the error log to handle
	 */
	public LogHandler(final String logDirPath, final String outputLogName,
			final String errorLogName) {
		super();
		setLogDir(logDirPath);
		setOutputLog(outputLogName);
		setErrorLog(errorLogName);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the path to the specified log.
	 * <p>
	 * @param logName the name of the log
	 * <p>
	 * @return the path to the specified log
	 */
	protected String getPath(final String logName) {
		return Files.getPath(logDir).concat(SEPARATOR).concat(logName);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the log directory {@link File} with the specified directory path.
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
	 * Sets the output log {@link File} with the specified name.
	 * <p>
	 * @param outputLogName a {@link String}
	 */
	public void setOutputLog(final String outputLogName) {
		outputLogLock.lock();
		try {
			outputLogHandler = new FileHandler(getPath(outputLogName));
		} finally {
			outputLogLock.unlock();
		}
	}

	/**
	 * Sets the error log {@link File} with the specified name.
	 * <p>
	 * @param errorLogName a {@link String}
	 */
	public void setErrorLog(final String errorLogName) {
		errorLogLock.lock();
		try {
			errorLogHandler = new FileHandler(getPath(errorLogName));
		} finally {
			errorLogLock.unlock();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
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
			if (outputLogHandler.exists()) {
				outputLogHandler.delete();
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
			if (errorLogHandler.exists()) {
				errorLogHandler.delete();
			}
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
	 * @throws IOException       if there is a problem with creating the directories
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
	 * Prints the specified content {@link Object} to the output log (error log if {@code isError}).
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the error log or in the output log
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	@Override
	public boolean print(final Object content, final boolean isError) {
		// Check the arguments
		Arguments.requireNonNull(content, "content");

		// Print the content
		if (!isError) {
			outputLogLock.lock();
			try {
				createDirs();
				outputLogHandler.write(Objects.toString(content));
				return true;
			} catch (final IOException ex) {
				IO.error(ex);
			} finally {
				outputLogLock.unlock();
			}
		} else {
			errorLogLock.lock();
			try {
				createDirs();
				errorLogHandler.write(Objects.toString(content));
				return true;
			} catch (final IOException ex) {
				IO.error(ex);
			} finally {
				errorLogLock.unlock();
			}
		}
		return false;
	}

	//////////////////////////////////////////////

	/**
	 * Prints the specified content {@link Object} to the output log (error log if {@code isError})
	 * and terminates the line.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the error log or in the output log
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	@Override
	public boolean println(final Object content, final boolean isError) {
		// Check the arguments
		Arguments.requireNonNull(content, "content");

		// Print the content and terminate the line
		if (!isError) {
			outputLogLock.lock();
			try {
				createDirs();
				outputLogHandler.writeLine(Objects.toString(content));
				return true;
			} catch (final IOException ex) {
				IO.error(ex);
			} finally {
				outputLogLock.unlock();
			}
		} else {
			errorLogLock.lock();
			try {
				createDirs();
				errorLogHandler.writeLine(Objects.toString(content));
				return true;
			} catch (final IOException ex) {
				IO.error(ex);
			} finally {
				errorLogLock.unlock();
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLOSEABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Closes {@code this}.
	 */
	@Override
	public void close() {
		Resources.close(outputLogHandler);
		Resources.close(errorLogHandler);
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
	public LogHandler clone() {
		return new LogHandler(Files.getPath(logDir), outputLogHandler.getName(),
				errorLogHandler.getName());
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
		if (other == null || !(other instanceof LogHandler)) {
			return false;
		}
		final LogHandler otherLogHandler = (LogHandler) other;
		return Objects.equals(logDir, otherLogHandler.logDir) &&
				Objects.equals(outputLogHandler, otherLogHandler.outputLogHandler) &&
				Objects.equals(errorLogHandler, otherLogHandler.errorLogHandler);
	}

	//////////////////////////////////////////////

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
		return Objects.hashCode(serialVersionUID, logDir, outputLogHandler, errorLogHandler);
	}
}
