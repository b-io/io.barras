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


import static jupiter.common.util.Formats.DEFAULT_CHARSET;
import static jupiter.common.util.Formats.NEWLINE;
import static jupiter.common.util.Strings.EMPTY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import jupiter.common.exception.IllegalTypeException;
import jupiter.common.io.console.ConsoleHandler;
import jupiter.common.io.console.IConsole;
import jupiter.common.io.log.LogHandler;
import jupiter.common.util.Arrays;
import jupiter.common.util.Strings;

public class IO {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether to exit with success.
	 */
	public static final int EXIT_SUCCESS = 0;
	/**
	 * The flag specifying whether to exit with failure.
	 */
	public static final int EXIT_FAILURE = 1;

	/**
	 * The default severity level.
	 */
	public static final SeverityLevel DEFAULT_SEVERITY_LEVEL = SeverityLevel.INFO;

	/**
	 * The default console handler.
	 */
	public static final ConsoleHandler DEFAULT_CONSOLE_HANDLER = new ConsoleHandler();
	/**
	 * The default log handler.
	 */
	public static final LogHandler DEFAULT_LOG_HANDLER = new LogHandler();

	/**
	 * The default IO.
	 */
	public static final IO IO = new IO();

	/**
	 * The stack index offset.
	 */
	protected static final int STACK_INDEX_OFFSET = 1;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The stack index.
	 */
	protected final int stackIndex;
	/**
	 * The severity level.
	 */
	protected volatile SeverityLevel severityLevel;

	/**
	 * The IO handlers.
	 */
	protected List<IOHandler> handlers;
	/**
	 * The console handler.
	 */
	protected ConsoleHandler consoleHandler;
	/**
	 * The log handler.
	 */
	protected LogHandler logHandler;
	/**
	 * The printer.
	 */
	protected final IOHandler printer;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public IO() {
		this(Message.DEFAULT_STACK_INDEX);
	}

	public IO(final int stackIndex) {
		this(stackIndex, DEFAULT_SEVERITY_LEVEL);
	}

	public IO(final int stackIndex, final SeverityLevel severityLevel) {
		this(stackIndex, severityLevel, Arrays.<IOHandler>asList(DEFAULT_CONSOLE_HANDLER));
	}

	public IO(final int stackIndex, final SeverityLevel severityLevel,
			final ConsoleHandler consoleHandler) {
		this(stackIndex, severityLevel,
				Arrays.<IOHandler>asList(consoleHandler, DEFAULT_LOG_HANDLER));
	}

	public IO(final int stackIndex, final SeverityLevel severityLevel,
			final LogHandler logHandler) {
		this(stackIndex, severityLevel,
				Arrays.<IOHandler>asList(DEFAULT_CONSOLE_HANDLER, logHandler));
	}

	public IO(final int stackIndex, final SeverityLevel severityLevel,
			final ConsoleHandler consoleHandler, final LogHandler logHandler) {
		this(stackIndex, severityLevel, Arrays.<IOHandler>asList(consoleHandler, logHandler));
	}

	public IO(final int stackIndex, final SeverityLevel severityLevel,
			final List<IOHandler> handlers) {
		// Set the stack index and the severity level
		this.stackIndex = stackIndex;
		this.severityLevel = severityLevel;
		// Set the IO handlers
		this.handlers = handlers;
		consoleHandler = DEFAULT_CONSOLE_HANDLER;
		logHandler = DEFAULT_LOG_HANDLER;
		for (final IOHandler handler : handlers) {
			if (ConsoleHandler.class.isAssignableFrom(handler.getClass())) {
				consoleHandler = (ConsoleHandler) handler;
			} else if (LogHandler.class.isAssignableFrom(handler.getClass())) {
				logHandler = (LogHandler) handler;
			}
		}
		// Set the printer
		printer = new IOPrinter(handlers);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS & SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the severity level.
	 * <p>
	 * @return the severity level
	 */
	public SeverityLevel getSeverityLevel() {
		return severityLevel;
	}

	/**
	 * Returns the printer.
	 * <p>
	 * @return the printer
	 */
	public IOHandler getPrinter() {
		return printer;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the severity level.
	 * <p>
	 * @param severityLevel a {@link SeverityLevel}
	 */
	public void setSeverityLevel(final SeverityLevel severityLevel) {
		this.severityLevel = severityLevel;
	}

	/**
	 * Sets the console.
	 * <p>
	 * @param console an {@link IConsole}
	 */
	public void setConsole(final IConsole console) {
		consoleHandler.setConsole(console);
	}

	/**
	 * Sets the log directory.
	 * <p>
	 * @param logDir a {@link String}
	 */
	public void setLogDir(final String logDir) {
		logHandler.setLogDir(logDir);
	}

	/**
	 * Sets the name of the output log.
	 * <p>
	 * @param outputLogName a {@link String}
	 */
	public void setOutputLog(final String outputLogName) {
		logHandler.setOutputLog(outputLogName);
	}

	/**
	 * Sets the name of the error log.
	 * <p>
	 * @param errorLogName a {@link String}
	 */
	public void setErrorLog(final String errorLogName) {
		logHandler.setErrorLog(errorLogName);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static BufferedReader createReader(final InputStream input) {
		return createReader(input, DEFAULT_CHARSET);
	}

	public static BufferedReader createReader(final InputStream input, final Charset charset) {
		return new BufferedReader(new InputStreamReader(input, charset));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Content} of the specified {@link InputStream}.
	 * <p>
	 * @param input the {@link InputStream} of the data to read
	 * <p>
	 * @return the {@link Content} of the specified {@link InputStream}
	 * <p>
	 * @throws IOException if there is a problem with reading
	 */
	public static Content read(final InputStream input)
			throws IOException {
		return read(input, DEFAULT_CHARSET);
	}

	/**
	 * Returns the number of lines of the specified {@link InputStream} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param input   the {@link InputStream} of the data to read
	 * @param charset the {@link Charset} of the data to read
	 * <p>
	 * @return the number of lines of the specified {@link InputStream} with the specified
	 *         {@link Charset}
	 * <p>
	 * @throws IOException if there is a problem with reading
	 */
	public static Content read(final InputStream input, final Charset charset)
			throws IOException {
		final StringBuilder builder = Strings.createBuilder();
		int lineCount = 0;
		BufferedReader reader = null;
		try {
			// Create a new reader
			reader = createReader(input, charset);
			// Iterate over the lines
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append(NEWLINE);
				++lineCount;
			}
		} finally {
			Resources.close(reader);
		}
		return new Content(builder.toString(), charset, lineCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of lines of the specified {@link InputStream}.
	 * <p>
	 * @param input the {@link InputStream} of the lines to count
	 * <p>
	 * @return the number of lines of the specified {@link InputStream}
	 * <p>
	 * @throws IOException if there is a problem with reading
	 */
	public static int countLines(final InputStream input)
			throws IOException {
		return countLines(input, DEFAULT_CHARSET);
	}

	/**
	 * Returns the number of lines of the specified {@link InputStream} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param input   the {@link InputStream} of the lines to count
	 * @param charset the {@link Charset} of the lines to count
	 * <p>
	 * @return the number of lines of the specified {@link InputStream} with the specified
	 *         {@link Charset}
	 * <p>
	 * @throws IOException if there is a problem with reading
	 */
	public static int countLines(final InputStream input, final Charset charset)
			throws IOException {
		return countLines(input, charset, false);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 * {@link InputStream}.
	 * <p>
	 * @param input          the {@link InputStream} of the lines to count
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 *         {@link InputStream}
	 * <p>
	 * @throws IOException if there is a problem with reading
	 */
	public static int countLines(final InputStream input, final boolean skipEmptyLines)
			throws IOException {
		return countLines(input, DEFAULT_CHARSET, skipEmptyLines);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 * {@link InputStream} with the specified {@link Charset}.
	 * <p>
	 * @param input          the {@link InputStream} of the lines to count
	 * @param charset        the {@link Charset} of the lines to count
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 *         {@link InputStream} with the specified {@link Charset}
	 * <p>
	 * @throws IOException if there is a problem with reading
	 */
	public static int countLines(final InputStream input, final Charset charset,
			final boolean skipEmptyLines)
			throws IOException {
		int lineCount = 0;
		BufferedReader reader = null;
		try {
			// Create a new reader
			reader = createReader(input, charset);
			// Iterate over the lines
			while (reader.readLine() != null) {
				++lineCount;
			}
		} finally {
			Resources.close(reader);
		}
		return lineCount;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static BufferedWriter createWriter(final OutputStream outputStream) {
		return createWriter(outputStream, DEFAULT_CHARSET);
	}

	public static BufferedWriter createWriter(final OutputStream outputStream,
			final Charset charset) {
		return new BufferedWriter(new OutputStreamWriter(outputStream, charset));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void copy(final BufferedReader reader, final PrintWriter writer, final int from)
			throws IOException {
		int i = 0;
		String line;
		while ((line = reader.readLine()) != null) {
			if (i >= from) {
				writer.println(line);
			}
			++i;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the input line of the console and prints it with the IO handlers (except to the
	 * console).
	 * <p>
	 * @return the input line of the console
	 */
	public String input() {
		final Message message = new Message(Type.INPUT, SeverityLevel.RESULT,
				consoleHandler.input(), stackIndex + STACK_INDEX_OFFSET);
		for (final IOHandler handler : handlers) {
			if (handler != consoleHandler) {
				handler.println(message);
			}
		}
		return message.getContent();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified content with the IO handlers.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	public void print(final Object content, final boolean isError) {
		printer.print(content, isError);
	}

	/**
	 * Prints {@code n} times the specified content with the IO handlers.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param n       the number of times to print the content
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	public void print(final Object content, final int n, final boolean isError) {
		if (n > 0) {
			print(Strings.repeat(Strings.toString(content), n), isError);
		}
	}

	/**
	 * Prints the indication of an input line in the console.
	 */
	public void printInput() {
		consoleHandler.print(new Message(Type.INPUT, SeverityLevel.RESULT, EMPTY,
				stackIndex + STACK_INDEX_OFFSET), false);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints an empty line with the IO handlers.
	 */
	public void println() {
		println(EMPTY, false);
	}

	/**
	 * Prints the specified content and terminates the line with the IO handlers.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	public void println(final Object content, final boolean isError) {
		printer.println(content, isError);
	}

	/**
	 * Prints {@code n} times the specified content and terminates the line with the IO handlers.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param n       the number of times to print the content
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	public void println(final Object content, final int n, final boolean isError) {
		for (int i = 0; i < n; ++i) {
			println(content, false);
		}
	}

	/**
	 * Prints the specified message and terminates the line with the IO handlers.
	 * <p>
	 * @param message the {@link Message} to print
	 */
	public void println(final Message message) {
		printer.println(message);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints a bar line and terminates the line with the IO handlers.
	 */
	public void bar() {
		println(Strings.createBar(), false);
	}

	/**
	 * Prints a bar line with the specified progress character and terminates the line with the IO
	 * handlers.
	 * <p>
	 * @param progressCharacter the {@code char} value of the bar to print
	 */
	public void bar(final char progressCharacter) {
		println(Strings.createBar(progressCharacter), false);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified content with the IO handlers indicating the severity level
	 * {@link SeverityLevel#TRACE}.
	 * <p>
	 * @param content the array of {@link Object} to print
	 * <p>
	 * @return a {@link Message} containing {@code content}
	 */
	public Message trace(final Object... content) {
		if (SeverityLevel.TRACE.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.TRACE,
					Strings.join(content), stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content with the IO handlers indicating the severity level
	 * {@link SeverityLevel#DEBUG}.
	 * <p>
	 * @param content the array of {@link Object} to print
	 * <p>
	 * @return a {@link Message} containing {@code content}
	 */
	public Message debug(final Object... content) {
		if (SeverityLevel.DEBUG.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.DEBUG,
					Strings.join(content), stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content with the IO handlers indicating the severity level
	 * {@link SeverityLevel#TEST}.
	 * <p>
	 * @param content the array of {@link Object} to print
	 * <p>
	 * @return a {@link Message} containing {@code content}
	 */
	public Message test(final Object... content) {
		if (SeverityLevel.TEST.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.TEST,
					Strings.join(content), stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content with the IO handlers indicating the severity level
	 * {@link SeverityLevel#INFO}.
	 * <p>
	 * @param content the array of {@link Object} to print
	 * <p>
	 * @return a {@link Message} containing {@code content}
	 */
	public Message info(final Object... content) {
		if (SeverityLevel.INFO.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.INFO,
					Strings.join(content), stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content with the IO handlers indicating the severity level
	 * {@link SeverityLevel#RESULT}.
	 * <p>
	 * @param content the array of {@link Object} to print
	 * <p>
	 * @return a {@link Message} containing {@code content}
	 */
	public Message result(final Object... content) {
		if (SeverityLevel.RESULT.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.RESULT,
					Strings.join(content), stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content with the IO handlers indicating the severity level
	 * {@link SeverityLevel#WARNING}.
	 * <p>
	 * @param content the array of {@link Object} to print
	 * <p>
	 * @return a {@link Message} containing {@code content}
	 */
	public Message warn(final Object... content) {
		if (SeverityLevel.WARNING.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.WARNING,
					Strings.join(content), stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified {@link Exception} with the IO handlers indicating the severity level
	 * {@link SeverityLevel#WARNING}.
	 * <p>
	 * @param exception the {@link Exception} to print
	 * <p>
	 * @return a {@link Message} containing {@code exception}
	 */
	public Message warn(final Exception exception) {
		if (SeverityLevel.WARNING.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.WARNING, exception,
					stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content and {@link Exception} with the IO handlers indicating the
	 * severity level {@link SeverityLevel#WARNING}.
	 * <p>
	 * @param content   the {@link Object} to print
	 * @param exception the {@link Exception} to print
	 * <p>
	 * @return a {@link Message} containing {@code content} and {@code exception}
	 */
	public Message warn(final Object content, final Exception exception) {
		if (SeverityLevel.WARNING.toInt() >= severityLevel.toInt()) {
			final String text = Strings.toString(content) + appendException(exception);
			final Message message = new Message(Type.OUTPUT, SeverityLevel.WARNING, text,
					stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content with the IO handlers indicating the severity level
	 * {@link SeverityLevel#ERROR}.
	 * <p>
	 * @param content the array of {@link Object} to print
	 * <p>
	 * @return a {@link Message} containing {@code content}
	 */
	public Message error(final Object... content) {
		if (SeverityLevel.ERROR.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.ERROR,
					Strings.join(content), stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified {@link Exception} with the IO handlers indicating the severity level
	 * {@link SeverityLevel#ERROR}.
	 * <p>
	 * @param exception the {@link Exception} to print
	 * <p>
	 * @return a {@link Message} containing {@code exception}
	 */
	public Message error(final Exception exception) {
		if (SeverityLevel.ERROR.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.ERROR, exception,
					stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content and {@link Exception} with the IO handlers indicating the
	 * severity level {@link SeverityLevel#ERROR}.
	 * <p>
	 * @param content   the {@link Object} to print
	 * @param exception the {@link Exception} to print
	 * <p>
	 * @return a {@link Message} containing {@code content} and {@code exception}
	 */
	public Message error(final Object content, final Exception exception) {
		if (SeverityLevel.ERROR.toInt() >= severityLevel.toInt()) {
			final String text = Strings.toString(content) + appendException(exception);
			final Message message = new Message(Type.OUTPUT, SeverityLevel.ERROR, text,
					stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content with the IO handlers indicating the severity level
	 * {@link SeverityLevel#FAILURE}.
	 * <p>
	 * @param content the array of {@link Object} to print
	 * <p>
	 * @return a {@link Message} containing {@code content}
	 */
	public Message fail(final Object... content) {
		if (SeverityLevel.FAILURE.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.FAILURE,
					Strings.join(content), stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		Runtime.getRuntime().halt(EXIT_FAILURE);
		return null;
	}

	/**
	 * Prints the specified {@link Exception} with the IO handlers indicating the severity level
	 * {@link SeverityLevel#FAILURE}.
	 * <p>
	 * @param exception the {@link Exception} to print
	 * <p>
	 * @return a {@link Message} containing {@code exception}
	 */
	public Message fail(final Exception exception) {
		if (SeverityLevel.FAILURE.toInt() >= severityLevel.toInt()) {
			final Message message = new Message(Type.OUTPUT, SeverityLevel.FAILURE, exception,
					stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		Runtime.getRuntime().halt(EXIT_FAILURE);
		return null;
	}

	/**
	 * Prints the specified content and {@link Exception} with the IO handlers indicating the
	 * severity level {@link SeverityLevel#FAILURE}.
	 * <p>
	 * @param content   the {@link Object} to print
	 * @param exception the {@link Exception} to print
	 * <p>
	 * @return a {@link Message} containing {@code content} and {@code exception}
	 */
	public Message fail(final Object content, final Exception exception) {
		if (SeverityLevel.FAILURE.toInt() >= severityLevel.toInt()) {
			final String text = Strings.toString(content) + appendException(exception);
			final Message message = new Message(Type.OUTPUT, SeverityLevel.FAILURE, text,
					stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String appendException(final Exception ex) {
		return ": " + ex;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEANERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clears the IO handlers.
	 */
	public void clear() {
		printer.clear();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum Type {
		INPUT,
		OUTPUT
	}

	public enum SeverityLevel
			implements Comparable<SeverityLevel> {
		TRACE(0),
		DEBUG(1),
		TEST(2),
		INFO(3),
		RESULT(4),
		WARNING(5),
		ERROR(6),
		FAILURE(7);

		public final int value;

		private SeverityLevel(final int value) {
			this.value = value;
		}

		public boolean isDebug() {
			return value <= DEBUG.toInt();
		}

		public boolean isError() {
			return value >= WARNING.toInt();
		}

		public int toInt() {
			return value;
		}

		@Override
		public String toString() {
			switch (value) {
				case 0:
					return "TRACE";
				case 1:
					return "DEBUG";
				case 2:
					return "TEST";
				case 3:
					return "INFO";
				case 4:
					return "RESULT";
				case 5:
					return "WARNING";
				case 6:
					return "ERROR";
				case 7:
					return "FAILURE";
				default:
					throw new IllegalTypeException(value);
			}
		}
	}
}
