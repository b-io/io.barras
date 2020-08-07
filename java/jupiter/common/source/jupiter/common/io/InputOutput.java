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

import static jupiter.common.Formats.CHARSET;
import static jupiter.common.Formats.NEWLINE;
import static jupiter.common.util.Strings.EMPTY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.ref.PhantomReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;

import jupiter.common.exception.IllegalTypeException;
import jupiter.common.io.console.ConsoleHandler;
import jupiter.common.io.console.IConsole;
import jupiter.common.io.log.LogHandler;
import jupiter.common.model.ICloneable;
import jupiter.common.properties.Jupiter;
import jupiter.common.properties.Properties;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Arguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class InputOutput
		implements ICloneable<InputOutput>, Closeable, Serializable {

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
	public static final SeverityLevel DEFAULT_SEVERITY_LEVEL = Message.DEFAULT_STANDARD_LEVEL;
	/**
	 * The default stack index.
	 */
	public static final int DEFAULT_STACK_INDEX = 0;

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
	 * The default {@link InputOutput}.
	 */
	public static final InputOutput IO = new InputOutput(Jupiter.PROPERTIES);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The buffer size used for copying (it should ideally match the block size of the file system).
	 */
	public static volatile int BUFFER_SIZE = 4096; // [byte]


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link SeverityLevel}.
	 */
	protected SeverityLevel severityLevel;
	/**
	 * The stack index.
	 */
	protected int stackIndex;

	/**
	 * The {@link ConsoleHandler}.
	 */
	protected ConsoleHandler consoleHandler;
	/**
	 * The {@link LogHandler}.
	 */
	protected LogHandler logHandler;
	/**
	 * The {@link List} of {@link IOHandler} (containing the {@link ConsoleHandler} and
	 * {@link LogHandler} by default).
	 */
	protected List<IOHandler> handlers;
	/**
	 * The {@link IOPrinter} containing the {@link List} of {@link IOHandler} (containing the
	 * {@link ConsoleHandler} and {@link LogHandler} by default).
	 */
	protected IOPrinter printer;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link InputOutput} by default.
	 */
	public InputOutput() {
		this(DEFAULT_SEVERITY_LEVEL);
	}

	/**
	 * Constructs an {@link InputOutput} with the specified {@link SeverityLevel}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 */
	public InputOutput(final SeverityLevel severityLevel) {
		this(severityLevel, DEFAULT_STACK_INDEX);
	}

	/**
	 * Constructs an {@link InputOutput} with the specified {@link SeverityLevel} and stack index.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 */
	public InputOutput(final SeverityLevel severityLevel, final int stackIndex) {
		this(severityLevel, stackIndex, DEFAULT_CONSOLE_HANDLER, DEFAULT_LOG_HANDLER);
	}

	/**
	 * Constructs an {@link InputOutput} with the specified {@link SeverityLevel}, stack index and
	 * {@link ConsoleHandler}.
	 * <p>
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param stackIndex     the stack index
	 * @param consoleHandler the {@link ConsoleHandler}
	 */
	public InputOutput(final SeverityLevel severityLevel, final int stackIndex,
			final ConsoleHandler consoleHandler) {
		this(severityLevel, stackIndex, consoleHandler, DEFAULT_LOG_HANDLER);
	}

	/**
	 * Constructs an {@link InputOutput} with the specified {@link SeverityLevel}, stack index and
	 * {@link LogHandler}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param logHandler    the {@link LogHandler}
	 */
	public InputOutput(final SeverityLevel severityLevel, final int stackIndex,
			final LogHandler logHandler) {
		this(severityLevel, stackIndex, DEFAULT_CONSOLE_HANDLER, logHandler);
	}

	/**
	 * Constructs an {@link InputOutput} with the specified {@link SeverityLevel}, stack index,
	 * {@link ConsoleHandler} and {@link LogHandler}.
	 * <p>
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param stackIndex     the stack index
	 * @param consoleHandler the {@link ConsoleHandler}
	 * @param logHandler     the {@link LogHandler}
	 */
	public InputOutput(final SeverityLevel severityLevel, final int stackIndex,
			final ConsoleHandler consoleHandler, final LogHandler logHandler) {
		this(severityLevel, stackIndex, new ExtendedList<IOHandler>(consoleHandler, logHandler));
	}

	/**
	 * Constructs an {@link InputOutput} with the specified {@link SeverityLevel}, stack index and
	 * {@link List} of {@link IOHandler}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param handlers      the {@link List} of {@link IOHandler}
	 */
	public InputOutput(final SeverityLevel severityLevel, final int stackIndex,
			final List<IOHandler> handlers) {
		// Set the severity level and stack index
		this.severityLevel = severityLevel;
		this.stackIndex = stackIndex;
		// Set the IO handlers and IO printer
		setHandlers(handlers);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link InputOutput} loaded from the specified {@link Properties} containing the
	 * specified {@link SeverityLevel}, stack index and {@link List} of {@link IOHandler}.
	 * <p>
	 * @param properties the {@link Properties} to load
	 */
	public InputOutput(final Properties properties) {
		// Set the IO handlers and IO printer by default
		setDefaultHandlers();

		// Load the severity level, stack index, IO handlers and IO printer
		load(properties);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
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
	 * Returns the stack index.
	 * <p>
	 * @return the stack index
	 */
	public int getStackIndex() {
		return stackIndex;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link ConsoleHandler}.
	 * <p>
	 * @return the {@link ConsoleHandler}
	 */
	public ConsoleHandler getConsoleHandler() {
		return consoleHandler;
	}

	/**
	 * Returns the {@link LogHandler}.
	 * <p>
	 * @return the {@link LogHandler}
	 */
	public LogHandler getLogHandler() {
		return logHandler;
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

	/**
	 * Sets the {@link SeverityLevel}.
	 * <p>
	 * @param severityLevel a {@link SeverityLevel}
	 */
	public void setSeverityLevel(final SeverityLevel severityLevel) {
		this.severityLevel = severityLevel;
	}

	/**
	 * Sets the stack index.
	 * <p>
	 * @param stackIndex an {@code int} value
	 */
	public void setStackIndex(final int stackIndex) {
		this.stackIndex = stackIndex;
	}

	//////////////////////////////////////////////

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

	/**
	 * Sets the {@link ConsoleHandler}, {@link LogHandler}, {@link List} of {@link IOHandler} and
	 * {@link IOPrinter} by default.
	 */
	public void setDefaultHandlers() {
		consoleHandler = DEFAULT_CONSOLE_HANDLER;
		logHandler = DEFAULT_LOG_HANDLER;
		setHandlers(new ExtendedList<IOHandler>(consoleHandler, logHandler));
	}

	/**
	 * Sets the {@link ConsoleHandler}, {@link LogHandler}, {@link List} of {@link IOHandler} and
	 * {@link IOPrinter}.
	 * <p>
	 * @param handlers a {@link List} of {@link IOHandler}
	 */
	public void setHandlers(final List<IOHandler> handlers) {
		consoleHandler = DEFAULT_CONSOLE_HANDLER;
		logHandler = DEFAULT_LOG_HANDLER;
		for (final IOHandler handler : handlers) {
			if (handler instanceof ConsoleHandler) {
				consoleHandler = (ConsoleHandler) handler;
			} else if (handler instanceof LogHandler) {
				logHandler = (LogHandler) handler;
			}
		}
		this.handlers = handlers;
		printer = new IOPrinter(handlers);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clears {@code this}.
	 */
	public void clear() {
		for (final IOHandler handler : handlers) {
			handler.clear();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads {@code this} from the specified {@link Properties}.
	 * <p>
	 * @param properties the {@link Properties} to load
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public void load(final Properties properties) {
		// Check the arguments
		Arguments.requireNonNull(properties, "properties");

		// Set the severity level and stack index
		severityLevel = SeverityLevel.valueOf(properties.getProperty("io.severityLevel",
				DEFAULT_SEVERITY_LEVEL.toString()));
		stackIndex = properties.getInt("io.stackIndex", DEFAULT_STACK_INDEX);
		// Set the IO handlers and IO printer
		final String[] handlerClassNames = properties.getPropertyArray("io.handlers",
				Strings.join(DEFAULT_CONSOLE_HANDLER.getClass().getCanonicalName(), ",",
						DEFAULT_LOG_HANDLER.getClass().getCanonicalName()));
		final ExtendedList<IOHandler> handlers = new ExtendedList<IOHandler>(
				handlerClassNames.length);
		for (final String handlerClassName : handlerClassNames) {
			try {
				final Class<? extends IOHandler> handlerClass = (Class<? extends IOHandler>)
						Class.forName(handlerClassName);
				if (handlerClass == consoleHandler.getClass()) {
					handlers.add(consoleHandler);
				} else if (handlerClass == logHandler.getClass()) {
					handlers.add(logHandler);
				} else {
					handlers.add(handlerClass.getConstructor().newInstance());
				}
			} catch (final ClassNotFoundException ex) {
				IO.error(ex);
			} catch (final IllegalAccessException ex) {
				IO.error(ex);
			} catch (final IllegalArgumentException ex) {
				IO.error(ex);
			} catch (final InstantiationException ex) {
				IO.error(ex);
			} catch (final InvocationTargetException ex) {
				IO.error(ex);
			} catch (final NoSuchMethodException ex) {
				IO.error(ex, "No default constructor in ", handlerClassName, " found");
			} catch (final SecurityException ex) {
				IO.error(ex);
			}
		}
		setHandlers(handlers);
		if (properties.getBoolean("io.clear", false)) {
			clear();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified content {@link Object} in the standard output (or standard error if
	 * {@code isError}) with the {@link IOPrinter}.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean print(final Object content, final boolean isError) {
		return printer.print(content, isError);
	}

	/**
	 * Prints the specified number of times the specified content {@link Object} in the standard
	 * output (or standard error if {@code isError}) with the {@link IOPrinter}.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param n       the number of times to print the content
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean print(final Object content, final int n, final boolean isError) {
		if (n > 0) {
			return print(Strings.repeat(Objects.toString(content), n), isError);
		}
		return true;
	}

	//////////////////////////////////////////////

	/**
	 * Prints an empty line with the {@link IOPrinter}.
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean println() {
		return println(EMPTY, false);
	}

	/**
	 * Prints the specified {@link Message} and terminates the line with the {@link IOPrinter}.
	 * <p>
	 * @param message the {@link Message} to print
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean println(final Message message) {
		return printer.println(message);
	}

	/**
	 * Prints the specified content {@link Object} in the standard output (or standard error if
	 * {@code isError}) and terminates the line with the {@link IOPrinter}.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean println(final Object content, final boolean isError) {
		return printer.println(content, isError);
	}

	/**
	 * Prints the specified number of times the specified content {@link Object} in the standard
	 * output (or standard error if {@code isError}) and terminates the line with the
	 * {@link IOPrinter}.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param n       the number of times to print the content
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean println(final Object content, final int n, final boolean isError) {
		boolean status = true;
		for (int i = 0; i < n; ++i) {
			status &= println(content, false);
		}
		return status;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints a bar line with the {@link IOPrinter}.
	 */
	public void bar() {
		println(Strings.createBar(), false);
	}

	/**
	 * Prints a bar line with the specified progress {@code char} symbol with the {@link IOPrinter}.
	 * <p>
	 * @param progressSymbol the progress {@code char} symbol of the bar to print
	 */
	public void bar(final char progressSymbol) {
		println(Strings.createBar(progressSymbol), false);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the indication of an input line with the {@link ConsoleHandler}.
	 */
	public void input() {
		consoleHandler.print(new Message(Type.INPUT, SeverityLevel.RESULT, stackIndex + 1, EMPTY),
				false);
	}

	//////////////////////////////////////////////

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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.TRACE, stackIndex + 1,
					Strings.join(content));
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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.DEBUG, stackIndex + 1,
					Strings.join(content));
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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.TEST, stackIndex + 1,
					Strings.join(content));
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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.INFO, stackIndex + 1,
					Strings.join(content));
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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.RESULT, stackIndex + 1,
					Strings.join(content));
			println(message);
			return message;
		}
		return null;
	}

	//////////////////////////////////////////////

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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.WARNING, stackIndex + 1,
					Strings.join(content));
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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.WARNING, stackIndex + 1,
					exception);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content {@link Object} and {@link Exception} with the {@link IOPrinter}
	 * indicating the severity level {@link SeverityLevel#WARNING}.
	 * <p>
	 * @param exception the {@link Exception} to print
	 * @param content   the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content {@link Object} and
	 *         {@link Exception}
	 */
	public Message warn(final Exception exception, final Object... content) {
		if (SeverityLevel.WARNING.toInt() >= severityLevel.toInt()) {
			final String text = Strings.join(Strings.join(content), ": ", exception);
			final Message message = new Message(Type.OUTPUT, SeverityLevel.WARNING, stackIndex + 1,
					text);
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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.ERROR, stackIndex + 1,
					Strings.join(content));
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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.ERROR, stackIndex + 1,
					exception);
			println(message);
			return message;
		}
		return null;
	}

	/**
	 * Prints the specified content {@link Object} and {@link Exception} with the {@link IOPrinter}
	 * indicating the severity level {@link SeverityLevel#ERROR}.
	 * <p>
	 * @param exception the {@link Exception} to print
	 * @param content   the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content {@link Object} and
	 *         {@link Exception}
	 */
	public Message error(final Exception exception, final Object... content) {
		if (SeverityLevel.ERROR.toInt() >= severityLevel.toInt()) {
			final String text = Strings.join(Strings.join(content), ": ", exception);
			final Message message = new Message(Type.OUTPUT, SeverityLevel.ERROR, stackIndex + 1,
					text);
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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.FAILURE, stackIndex + 1,
					Strings.join(content));
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
			final Message message = new Message(Type.OUTPUT, SeverityLevel.FAILURE, stackIndex + 1,
					exception);
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
	 * @param exception the {@link Exception} to print
	 * @param content   the content array to print
	 * <p>
	 * @return a {@link Message} containing the specified content {@link Object} and
	 *         {@link Exception}
	 */
	public Message fail(final Exception exception, final Object... content) {
		if (SeverityLevel.FAILURE.toInt() >= severityLevel.toInt()) {
			final String text = Strings.join(Strings.join(content), ": ", exception);
			final Message message = new Message(Type.OUTPUT, SeverityLevel.FAILURE, stackIndex + 1,
					text);
			println(message);
			return message;
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reads the data from the specified {@link InputStream} and writes it to the specified
	 * {@link OutputStream}.
	 * <p>
	 * @param input  the {@link InputStream} to copy from
	 * @param output the {@link OutputStream} to copy to
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
	 * Reads the data from the specified {@link InputStream} and writes it to the specified
	 * {@link OutputStream} with the specified buffer.
	 * <p>
	 * @param input  the {@link InputStream} to copy from
	 * @param output the {@link OutputStream} to copy to
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
	 * {@link PrintWriter} from the specified line.
	 * <p>
	 * @param reader        the {@link BufferedReader} to read with
	 * @param writer        the {@link PrintWriter} to write with
	 * @param fromLineIndex the line index to start copying from (inclusive)
	 * <p>
	 * @throws IOException if there is a problem with reading with {@code reader} or writing with
	 *                     {@code writer}
	 */
	public static void copy(final BufferedReader reader, final PrintWriter writer,
			final int fromLineIndex)
			throws IOException {
		copy(reader, writer, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Copies the data of the specified {@link BufferedReader} with the specified
	 * {@link PrintWriter} between the specified lines.
	 * <p>
	 * @param reader        the {@link BufferedReader} to read with
	 * @param writer        the {@link PrintWriter} to write with
	 * @param fromLineIndex the line index to start copying from (inclusive)
	 * @param toLineIndex   the line index to finish copying at (exclusive)
	 * <p>
	 * @throws IOException if there is a problem with reading with {@code reader} or writing with
	 *                     {@code writer}
	 */
	public static void copy(final BufferedReader reader, final PrintWriter writer,
			final int fromLineIndex, final int toLineIndex)
			throws IOException {
		int li = skipLines(reader, fromLineIndex);
		String line;
		while (li < toLineIndex && (line = reader.readLine()) != null) {
			writer.println(line);
			++li;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS / WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the input line of the {@link ConsoleHandler} and prints it with all the
	 * {@link IOHandler} (except the {@link ConsoleHandler}).
	 * <p>
	 * @return the input line of the {@link ConsoleHandler}
	 */
	public String getInputLine() {
		final Message message = new Message(Type.INPUT, SeverityLevel.RESULT, stackIndex + 1,
				consoleHandler.getInputLine());
		for (final IOHandler handler : handlers) {
			if (handler != consoleHandler) {
				handler.println(message);
			}
		}
		return message.getContent();
	}

	//////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedReader} of the specified {@link InputStream}.
	 * <p>
	 * @param input the {@link InputStream} to read from
	 * <p>
	 * @return a {@link BufferedReader} of the specified {@link InputStream}
	 */
	public static BufferedReader createReader(final InputStream input) {
		return createReader(input, CHARSET);
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

	//////////////////////////////////////////////

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
		return read(input, CHARSET);
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
				builder.append(line).append(NEWLINE);
				++lineCount;
			}
		} finally {
			Resources.close(reader);
		}
		return new Content(builder.toString(), charset, lineCount);
	}

	//////////////////////////////////////////////

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
		return countLines(input, CHARSET, false);
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
		return countLines(input, CHARSET, skipEmptyLines);
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

	//////////////////////////////////////////////

	/**
	 * Skips the specified number of lines of the specified {@link BufferedReader}.
	 * <p>
	 * @param reader    the {@link BufferedReader} to skip the lines from
	 * @param skipCount the number of lines to skip
	 * <p>
	 * @return the number of skipped lines
	 * <p>
	 * @throws IOException if there is a problem with reading with {@code reader}
	 */
	public static int skipLines(final BufferedReader reader, final int skipCount)
			throws IOException {
		int li = 0;
		while (li < skipCount && reader.readLine() != null) {
			++li;
		}
		return li;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedWriter} of the specified {@link OutputStream}.
	 * <p>
	 * @param outputStream the {@link OutputStream} to write to
	 * <p>
	 * @return a {@link BufferedWriter} of the specified {@link OutputStream}
	 */
	public static BufferedWriter createWriter(final OutputStream outputStream) {
		return createWriter(outputStream, CHARSET);
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
	// CLOSEABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Closes {@code this}.
	 */
	public void close() {
		Resources.close(printer);
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
	public InputOutput clone() {
		try {
			final InputOutput clone = (InputOutput) super.clone();
			clone.consoleHandler = Objects.clone(consoleHandler);
			clone.logHandler = Objects.clone(logHandler);
			clone.handlers = Objects.clone(handlers);
			clone.printer = Objects.clone(printer);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	/**
	 * Disposes of system resources and performs a cleanup.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>This method is called by the garbage collector on an {@link Object} when the garbage
	 * collection determines that there are no more references to the {@link Object}.</dd>
	 * </dl>
	 *
	 * @see PhantomReference
	 * @see WeakReference
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void finalize() {
		IO.trace(this, " is finalized");
		try {
			close();
		} finally {
			try {
				super.finalize();
			} catch (final Throwable ignored) {
			}
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
		if (other == null || !(other instanceof InputOutput)) {
			return false;
		}
		final InputOutput otherIO = (InputOutput) other;
		return Objects.equals(severityLevel, otherIO.severityLevel) &&
				Objects.equals(stackIndex, otherIO.stackIndex) &&
				Objects.equals(consoleHandler, otherIO.consoleHandler) &&
				Objects.equals(logHandler, otherIO.logHandler) &&
				Objects.equals(handlers, otherIO.handlers) &&
				Objects.equals(printer, otherIO.printer);
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
		return Objects.hashCode(serialVersionUID, severityLevel, stackIndex, consoleHandler,
				logHandler, handlers, printer);
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
		 * Tests whether {@code this} is at least {@link SeverityLevel#ERROR}.
		 * <p>
		 * @return {@code true} if {@code this} is at least {@link SeverityLevel#ERROR},
		 *         {@code false} otherwise
		 */
		public boolean isError() {
			return value >= ERROR.toInt();
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
