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

import static jupiter.common.util.Formats.DEFAULT_CHARSET;
import static jupiter.common.util.Formats.NEW_LINE;
import static jupiter.common.util.Strings.EMPTY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

import jupiter.common.exception.IllegalTypeException;
import jupiter.common.io.console.ConsoleHandler;
import jupiter.common.io.console.IConsole;
import jupiter.common.io.log.LogHandler;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class IO
		implements ICloneable<IO>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The end-of-file (or stream).
	 */
	public static final int EOF = -1;

	/**
	 * The flag specifying whether to exit with success.
	 */
	public static final int EXIT_SUCCESS = 0;
	/**
	 * The flag specifying whether to exit with failure.
	 */
	public static final int EXIT_FAILURE = 1;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link SeverityLevel}.
	 */
	public static final SeverityLevel DEFAULT_SEVERITY_LEVEL = SeverityLevel.INFO;

	/**
	 * The default {@link ConsoleHandler}.
	 */
	public static final ConsoleHandler DEFAULT_CONSOLE_HANDLER = new ConsoleHandler();
	/**
	 * The default {@link LogHandler}.
	 */
	public static final LogHandler DEFAULT_LOG_HANDLER = new LogHandler();

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link IO}.
	 */
	public static final IO IO = new IO();

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The buffer size used for copying.
	 */
	protected static volatile int BUFFER_SIZE = 4096; // [byte]

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
	 * The {@link SeverityLevel}.
	 */
	protected SeverityLevel severityLevel;

	/**
	 * The {@link IOPrinter} containing the {@link List} of {@link IOHandler} (containing the
	 * {@link ConsoleHandler} and {@link LogHandler} by default).
	 */
	protected IOPrinter printer;
	/**
	 * The {@link List} of {@link IOHandler} (containing the {@link ConsoleHandler} and
	 * {@link LogHandler} by default).
	 */
	protected List<IOHandler> handlers;
	/**
	 * The {@link ConsoleHandler}.
	 */
	protected ConsoleHandler consoleHandler;
	/**
	 * The {@link LogHandler}.
	 */
	protected LogHandler logHandler;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link IO} by default.
	 */
	public IO() {
		this(Message.DEFAULT_STACK_INDEX);
	}

	/**
	 * Constructs an {@link IO} with the specified stack index.
	 * <p>
	 * @param stackIndex a stack index
	 */
	public IO(final int stackIndex) {
		this(stackIndex, DEFAULT_SEVERITY_LEVEL);
	}

	/**
	 * Constructs an {@link IO} with the specified stack index and {@link SeverityLevel}.
	 * <p>
	 * @param stackIndex    the stack index
	 * @param severityLevel the {@link SeverityLevel}
	 */
	public IO(final int stackIndex, final SeverityLevel severityLevel) {
		this(stackIndex, severityLevel, DEFAULT_CONSOLE_HANDLER, DEFAULT_LOG_HANDLER);
	}

	/**
	 * Constructs an {@link IO} with the specified stack index, {@link SeverityLevel} and
	 * {@link ConsoleHandler}.
	 * <p>
	 * @param stackIndex     the stack index
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param consoleHandler the {@link ConsoleHandler}
	 */
	public IO(final int stackIndex, final SeverityLevel severityLevel,
			final ConsoleHandler consoleHandler) {
		this(stackIndex, severityLevel, consoleHandler, DEFAULT_LOG_HANDLER);
	}

	/**
	 * Constructs an {@link IO} with the specified stack index, {@link SeverityLevel} and
	 * {@link LogHandler}.
	 * <p>
	 * @param stackIndex    the stack index
	 * @param severityLevel the {@link SeverityLevel}
	 * @param logHandler    the {@link LogHandler}
	 */
	public IO(final int stackIndex, final SeverityLevel severityLevel,
			final LogHandler logHandler) {
		this(stackIndex, severityLevel, DEFAULT_CONSOLE_HANDLER, logHandler);
	}

	/**
	 * Constructs an {@link IO} with the specified stack index, {@link SeverityLevel},
	 * {@link ConsoleHandler} and {@link LogHandler}.
	 * <p>
	 * @param stackIndex     the stack index
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param consoleHandler the {@link ConsoleHandler}
	 * @param logHandler     the {@link LogHandler}
	 */
	public IO(final int stackIndex, final SeverityLevel severityLevel,
			final ConsoleHandler consoleHandler, final LogHandler logHandler) {
		this(stackIndex, severityLevel, Arrays.<IOHandler>asList(consoleHandler, logHandler));
	}

	/**
	 * Constructs an {@link IO} with the specified stack index, {@link SeverityLevel} and
	 * {@link List} of {@link IOHandler}.
	 * <p>
	 * @param stackIndex    the stack index
	 * @param severityLevel the {@link SeverityLevel}
	 * @param handlers      the {@link List} of {@link IOHandler}
	 */
	public IO(final int stackIndex, final SeverityLevel severityLevel,
			final List<IOHandler> handlers) {
		// Set the stack index and severity level
		this.stackIndex = stackIndex;
		this.severityLevel = severityLevel;
		// Set the IO handlers
		this.handlers = handlers;
		consoleHandler = DEFAULT_CONSOLE_HANDLER;
		logHandler = DEFAULT_LOG_HANDLER;
		for (final IOHandler handler : handlers) {
			if (handler instanceof ConsoleHandler) {
				consoleHandler = (ConsoleHandler) handler;
			} else if (handler instanceof LogHandler) {
				logHandler = (LogHandler) handler;
			}
		}
		// Set the printer
		printer = new IOPrinter(handlers);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link SeverityLevel}.
	 * <p>
	 * @return the {@link SeverityLevel}
	 */
	public SeverityLevel getSeverityLevel() {
		return severityLevel;
	}

	/**
	 * Returns the {@link IOPrinter} containing the {@link List} of {@link IOHandler} (the
	 * {@link ConsoleHandler} and {@link LogHandler} by default).
	 * <p>
	 * @return the {@link IOPrinter} containing the {@link List} of {@link IOHandler} (the
	 *         {@link ConsoleHandler} and {@link LogHandler} by default)
	 */
	public IOPrinter getPrinter() {
		return printer;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@link SeverityLevel}.
	 * <p>
	 * @param severityLevel a {@link SeverityLevel}
	 */
	public void setSeverityLevel(final SeverityLevel severityLevel) {
		this.severityLevel = severityLevel;
	}

	/**
	 * Sets the {@link IConsole} of the {@link ConsoleHandler}.
	 * <p>
	 * @param console an {@link IConsole}
	 */
	public void setConsole(final IConsole console) {
		consoleHandler.setConsole(console);
	}

	/**
	 * Sets the log directory {@link File} of the {@link LogHandler} with the specified directory
	 * path.
	 * <p>
	 * @param logDirPath a {@link String}
	 */
	public void setLogDir(final String logDirPath) {
		logHandler.setLogDir(logDirPath);
	}

	/**
	 * Sets the output log {@link File} of the {@link LogHandler} with the specified name.
	 * <p>
	 * @param outputLogName a {@link String}
	 */
	public void setOutputLog(final String outputLogName) {
		logHandler.setOutputLog(outputLogName);
	}

	/**
	 * Sets the error log {@link File} of the {@link LogHandler} with the specified name.
	 * <p>
	 * @param errorLogName a {@link String}
	 */
	public void setErrorLog(final String errorLogName) {
		logHandler.setErrorLog(errorLogName);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedReader} of the specified {@link InputStream}.
	 * <p>
	 * @param input the {@link InputStream} to read from
	 * <p>
	 * @return a {@link BufferedReader} of the specified {@link InputStream}
	 */
	public static BufferedReader createReader(final InputStream input) {
		return createReader(input, DEFAULT_CHARSET);
	}

	/**
	 * Creates a {@link BufferedReader} of the specified {@link InputStream} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param input   the {@link InputStream} to read from
	 * @param charset the {@link Charset} of the data to read
	 * <p>
	 * @return a {@link BufferedReader} of the specified {@link InputStream} with the specified
	 *         {@link Charset}
	 */
	public static BufferedReader createReader(final InputStream input, final Charset charset) {
		return new BufferedReader(new InputStreamReader(input, charset));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Content} of the specified {@link InputStream}.
	 * <p>
	 * @param input the {@link InputStream} to read from
	 * <p>
	 * @return the {@link Content} of the specified {@link InputStream}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code input}
	 */
	public static Content read(final InputStream input)
			throws IOException {
		return read(input, DEFAULT_CHARSET);
	}

	/**
	 * Returns the number of lines of the specified {@link InputStream} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param input   the {@link InputStream} to read from
	 * @param charset the {@link Charset} of the data to read
	 * <p>
	 * @return the number of lines of the specified {@link InputStream} with the specified
	 *         {@link Charset}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code input}
	 */
	public static Content read(final InputStream input, final Charset charset)
			throws IOException {
		final StringBuilder builder = Strings.createBuilder();
		int lineCount = 0;
		BufferedReader reader = null;
		try {
			// Create the input reader with the charset
			reader = createReader(input, charset);
			// Iterate over the lines
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append(NEW_LINE);
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
	 * @param input the {@link InputStream} to count the lines from
	 * <p>
	 * @return the number of lines of the specified {@link InputStream}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code input}
	 */
	public static int countLines(final InputStream input)
			throws IOException {
		return countLines(input, DEFAULT_CHARSET);
	}

	/**
	 * Returns the number of lines of the specified {@link InputStream} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param input   the {@link InputStream} to count the lines from
	 * @param charset the {@link Charset} of the lines to count
	 * <p>
	 * @return the number of lines of the specified {@link InputStream} with the specified
	 *         {@link Charset}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code input}
	 */
	public static int countLines(final InputStream input, final Charset charset)
			throws IOException {
		return countLines(input, charset, false);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 * {@link InputStream}.
	 * <p>
	 * @param input          the {@link InputStream} to count the lines from
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 *         {@link InputStream}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code input}
	 */
	public static int countLines(final InputStream input, final boolean skipEmptyLines)
			throws IOException {
		return countLines(input, DEFAULT_CHARSET, skipEmptyLines);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 * {@link InputStream} with the specified {@link Charset}.
	 * <p>
	 * @param input          the {@link InputStream} to count the lines from
	 * @param charset        the {@link Charset} of the lines to count
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 *         {@link InputStream} with the specified {@link Charset}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code input}
	 */
	public static int countLines(final InputStream input, final Charset charset,
			final boolean skipEmptyLines)
			throws IOException {
		int lineCount = 0;
		BufferedReader reader = null;
		try {
			// Create the input reader with the charset
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

	/**
	 * Creates a {@link BufferedWriter} of the specified {@link OutputStream}.
	 * <p>
	 * @param outputStream the {@link OutputStream} to write to
	 * <p>
	 * @return a {@link BufferedWriter} of the specified {@link OutputStream}
	 */
	public static BufferedWriter createWriter(final OutputStream outputStream) {
		return createWriter(outputStream, DEFAULT_CHARSET);
	}

	/**
	 * Creates a {@link BufferedWriter} of the specified {@link OutputStream} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param outputStream the {@link OutputStream} to write to
	 * @param charset      the {@link Charset} of the data to write
	 * <p>
	 * @return a {@link BufferedWriter} of the specified {@link OutputStream} with the specified
	 *         {@link Charset}
	 */
	public static BufferedWriter createWriter(final OutputStream outputStream,
			final Charset charset) {
		return new BufferedWriter(new OutputStreamWriter(outputStream, charset));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reads the data from the specified {@link InputStream}, writes it to the specified
	 * {@link OutputStream} and returns the number of copied {@code byte}.
	 * <p>
	 * @param input  the {@link InputStream} to read from
	 * @param output the {@link OutputStream} to write to
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code input} or writing to
	 *                     {@code output}
	 */
	public static long copy(final InputStream input, final OutputStream output)
			throws IOException {
		return copy(input, output, new byte[BUFFER_SIZE]);
	}

	/**
	 * Reads the data from the specified {@link InputStream}, writes it to the specified
	 * {@link OutputStream} with the specified buffer and returns the number of copied {@code byte}.
	 * <p>
	 * @param input  the {@link InputStream} to read from
	 * @param output the {@link OutputStream} to write to
	 * @param buffer the buffer {@code byte} array used for copying
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code input} or writing to
	 *                     {@code output}
	 */
	public static long copy(final InputStream input, final OutputStream output, final byte[] buffer)
			throws IOException {
		long writtenByteCount = 0L;
		int readByteCount;
		while ((readByteCount = input.read(buffer)) != EOF) {
			output.write(buffer, 0, readByteCount);
			writtenByteCount += readByteCount;
		}
		return writtenByteCount;
	}

	/**
	 * Copies the data of the specified {@link BufferedReader} with the specified
	 * {@link PrintWriter} from the specified line index.
	 * <p>
	 * @param reader   the {@link BufferedReader} to read with
	 * @param writer   the {@link PrintWriter} to write with
	 * @param fromLine the line index to start copying forward from
	 * <p>
	 * @throws IOException if there is a problem with reading with {@code reader} or writing with
	 *                     {@code writer}
	 */
	public static void copy(final BufferedReader reader, final PrintWriter writer,
			final int fromLine)
			throws IOException {
		int i = 0;
		String line;
		while ((line = reader.readLine()) != null) {
			if (i++ >= fromLine) {
				writer.println(line);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the input line of the {@link ConsoleHandler} and prints it with all the
	 * {@link IOHandler} (except the {@link ConsoleHandler}).
	 * <p>
	 * @return the input line of the {@link ConsoleHandler}
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
	 * Prints the specified content {@link Object} in the standard output (or in the standard error
	 * if {@code isError}) with the {@link IOPrinter}.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	public void print(final Object content, final boolean isError) {
		printer.print(content, isError);
	}

	/**
	 * Prints the specified number of times the specified content {@link Object} in the standard
	 * output (or in the standard error if {@code isError}) with the {@link IOPrinter}.
	 * <p>
	 * @param content the content {@link Object} to print
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
	 * Prints the indication of an input line with the {@link ConsoleHandler}.
	 */
	public void printInput() {
		consoleHandler.print(new Message(Type.INPUT, SeverityLevel.RESULT, EMPTY,
				stackIndex + STACK_INDEX_OFFSET), false);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints an empty line with the {@link IOPrinter}.
	 */
	public void println() {
		println(EMPTY, false);
	}

	/**
	 * Prints the specified content {@link Object} in the standard output (or in the standard error
	 * if {@code isError}) and terminates the line with the {@link IOPrinter}.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	public void println(final Object content, final boolean isError) {
		printer.println(content, isError);
	}

	/**
	 * Prints the specified number of times the specified content {@link Object} in the standard
	 * output (or in the standard error if {@code isError}) and terminates the line with the
	 * {@link IOPrinter}.
	 * <p>
	 * @param content the content {@link Object} to print
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
	 * Prints the specified {@link Message} and terminates the line with the {@link IOPrinter}.
	 * <p>
	 * @param message the {@link Message} to print
	 */
	public void println(final Message message) {
		printer.println(message);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints a bar line and terminates the line with the {@link IOPrinter}.
	 */
	public void bar() {
		println(Strings.createBar(), false);
	}

	/**
	 * Prints a bar line with the specified progress {@code char} symbol and terminates the line
	 * with the {@link IOPrinter}.
	 * <p>
	 * @param progressSymbol the progress {@code char} symbol of the bar to print
	 */
	public void bar(final char progressSymbol) {
		println(Strings.createBar(progressSymbol), false);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified content array with the {@link IOPrinter} indicating the severity level
	 * {@link SeverityLevel#TRACE}.
	 * <p>
	 * @param content the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content array
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
	 * Prints the specified content array with the {@link IOPrinter} indicating the severity level
	 * {@link SeverityLevel#DEBUG}.
	 * <p>
	 * @param content the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content array
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
	 * Prints the specified content array with the {@link IOPrinter} indicating the severity level
	 * {@link SeverityLevel#TEST}.
	 * <p>
	 * @param content the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content array
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
	 * Prints the specified content array with the {@link IOPrinter} indicating the severity level
	 * {@link SeverityLevel#INFO}.
	 * <p>
	 * @param content the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content array
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
	 * Prints the specified content array with the {@link IOPrinter} indicating the severity level
	 * {@link SeverityLevel#RESULT}.
	 * <p>
	 * @param content the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content array
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
	 * Prints the specified content array with the {@link IOPrinter} indicating the severity level
	 * {@link SeverityLevel#WARNING}.
	 * <p>
	 * @param content the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content array
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
	 * Prints the specified {@link Exception} with the {@link IOPrinter} indicating the severity
	 * level {@link SeverityLevel#WARNING}.
	 * <p>
	 * @param exception an {@link Exception}
	 * <p>
	 * @return a {@link Message} containing the specified {@link Exception}
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
	 * Prints the specified content {@link Object} and {@link Exception} with the {@link IOPrinter}
	 * indicating the severity level {@link SeverityLevel#WARNING}.
	 * <p>
	 * @param content   the content {@link Object} to print
	 * @param exception the {@link Exception} to print
	 * <p>
	 * @return a {@link Message} containing the specified content {@link Object} and
	 *         {@link Exception}
	 */
	public Message warn(final Object content, final Exception exception) {
		if (SeverityLevel.WARNING.toInt() >= severityLevel.toInt()) {
			final String text = Strings.join(content, ": ", exception);
			final Message message = new Message(Type.OUTPUT, SeverityLevel.WARNING, text,
					stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content array with the {@link IOPrinter} indicating the severity level
	 * {@link SeverityLevel#ERROR}.
	 * <p>
	 * @param content the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content array
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
	 * Prints the specified {@link Exception} with the {@link IOPrinter} indicating the severity
	 * level {@link SeverityLevel#ERROR}.
	 * <p>
	 * @param exception an {@link Exception}
	 * <p>
	 * @return a {@link Message} containing the specified {@link Exception}
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
	 * Prints the specified content {@link Object} and {@link Exception} with the {@link IOPrinter}
	 * indicating the severity level {@link SeverityLevel#ERROR}.
	 * <p>
	 * @param content   the content {@link Object} to print
	 * @param exception the {@link Exception} to print
	 * <p>
	 * @return a {@link Message} containing the specified content {@link Object} and
	 *         {@link Exception}
	 */
	public Message error(final Object content, final Exception exception) {
		if (SeverityLevel.ERROR.toInt() >= severityLevel.toInt()) {
			final String text = Strings.join(content, ": ", exception);
			final Message message = new Message(Type.OUTPUT, SeverityLevel.ERROR, text,
					stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content array with the {@link IOPrinter} indicating the severity level
	 * {@link SeverityLevel#FAILURE}.
	 * <p>
	 * @param content the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content array
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
	 * Prints the specified {@link Exception} with the {@link IOPrinter} indicating the severity
	 * level {@link SeverityLevel#FAILURE}.
	 * <p>
	 * @param exception an {@link Exception}
	 * <p>
	 * @return a {@link Message} containing the specified {@link Exception}
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
	 * Prints the specified content {@link Object} and {@link Exception} with the {@link IOPrinter}
	 * indicating the severity level {@link SeverityLevel#FAILURE}.
	 * <p>
	 * @param content   the content {@link Object} to print
	 * @param exception the {@link Exception} to print
	 * <p>
	 * @return a {@link Message} containing the specified content {@link Object} and
	 *         {@link Exception}
	 */
	public Message fail(final Object content, final Exception exception) {
		if (SeverityLevel.FAILURE.toInt() >= severityLevel.toInt()) {
			final String text = Strings.join(content, ": ", exception);
			final Message message = new Message(Type.OUTPUT, SeverityLevel.FAILURE, text,
					stackIndex + STACK_INDEX_OFFSET);
			println(message);
			return message;
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEANERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clears {@code this}.
	 */
	public void clear() {
		printer.clear();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public IO clone() {
		try {
			final IO clone = (IO) super.clone();
			clone.printer = Objects.clone(printer);
			clone.handlers = Objects.clone(handlers);
			clone.consoleHandler = Objects.clone(consoleHandler);
			clone.logHandler = Objects.clone(logHandler);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
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
		if (other == null || !(other instanceof IO)) {
			return false;
		}
		final IO otherIO = (IO) other;
		return Objects.equals(stackIndex, otherIO.stackIndex) &&
				Objects.equals(severityLevel, otherIO.severityLevel) &&
				Objects.equals(printer, otherIO.printer) &&
				Objects.equals(handlers, otherIO.handlers) &&
				Objects.equals(consoleHandler, otherIO.consoleHandler) &&
				Objects.equals(logHandler, otherIO.logHandler);
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
		return Objects.hashCode(serialVersionUID, stackIndex, severityLevel, printer, handlers,
				consoleHandler, logHandler);
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

		/**
		 * Tests whether {@code this} is at most {@link SeverityLevel#DEBUG}.
		 * <p>
		 * @return {@code true} if {@code this} is at most {@link SeverityLevel#DEBUG},
		 *         {@code false} otherwise
		 */
		public boolean isDebug() {
			return value <= DEBUG.toInt();
		}

		/**
		 * Tests whether {@code this} is at least {@link SeverityLevel#WARNING}.
		 * <p>
		 * @return {@code true} if {@code this} is at least {@link SeverityLevel#WARNING},
		 *         {@code false} otherwise
		 */
		public boolean isError() {
			return value >= WARNING.toInt();
		}

		public int toInt() {
			return value;
		}

		/**
		 * Returns a representative {@link String} of {@code this}.
		 * <p>
		 * @return a representative {@link String} of {@code this}
		 */
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
