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
package jupiter.lang.r;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.SPACE;

import java.io.IOException;

import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.io.IOHandler;
import jupiter.common.io.Systems;
import jupiter.common.math.Comparables;
import jupiter.common.model.ICloneable;
import jupiter.common.test.ArrayArguments;
import jupiter.common.thread.Threads;
import jupiter.common.thread.WorkQueue;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class R {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String R_PATH = "R";
	public static final String R_SCRIPT_PATH = "RScript";
	public static final String[] ARGS = new String[] {};

	//////////////////////////////////////////////

	public static volatile String REPO = "https://cloud.r-project.org";

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link RPrinter}.
	 */
	public static final RPrinter DEFAULT_PRINTER = new RPrinter();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link R}.
	 */
	protected R() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int installPackage(final String name) {
		return execute(Strings.join(
				"if (!", Strings.singleQuote(name), " %in% installed.packages())",
				"install.packages(", Strings.singleQuote(name),
				", repos=", Strings.singleQuote(REPO),
				")"));
	}

	public static int[] installPackages(final String[] names) {
		final int[] status = new int[names.length];
		for (int i = 0; i < names.length; ++i) {
			status[i] = installPackage(names[i]);
		}
		return status;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean start() {
		return start(Comparables.isGreaterOrEqualTo(SeverityLevel.DEBUG, IO.getSeverityLevel()));
	}

	public static boolean start(final boolean debug) {
		// Test whether the server is already running
		if (R.isRunning()) {
			return true;
		}

		// Launch Rserve
		if (!(installPackage("Rserve") == IO.EXIT_SUCCESS && execute(Strings.join(
				"library(Rserve);",
				"Rserve(", debug ? "T" : "F",
				", args = ", Strings.singleQuote(Strings.joinWith(ARGS, SPACE)),
				")")) == IO.EXIT_SUCCESS)) {
			return false;
		}

		// Try up to 5 times before giving up; we can be conservative here, because at this point
		// the process execution itself was successful and the start up is usually asynchronous
		int attemptCount = 5;
		do {
			// Sleep in case the start up is delayed or asynchronous
			Threads.sleep();
			--attemptCount;
		} while (attemptCount > 0 && !isRunning());
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean stop() {
		try {
			new RConnection().shutdown();
		} catch (final RserveException ex) {
			IO.warn(ex, "Fail to stop Rserve");
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Executes the specified script on the R engine.
	 * <p>
	 * @param script the script to execute
	 * <p>
	 * @return the exit value of the specified script executed on the R engine
	 */
	public static int executeScript(final String... script) {
		return executeScript(DEFAULT_PRINTER, script);
	}

	/**
	 * Executes the specified script on the R engine, prints the output with the specified printer
	 * {@link IOHandler}.
	 * <p>
	 * @param printer the printer {@link IOHandler}
	 * @param script  the script to execute
	 * <p>
	 * @return the exit value of the specified script executed on the R engine
	 */
	public static int executeScript(final IOHandler printer, final String... script) {
		try {
			return Systems.execute(printer,
					Arrays.<String>merge(new String[] {R_SCRIPT_PATH}, script, ARGS));
		} catch (final InterruptedException ex) {
			IO.error(ex);
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return IO.EXIT_FAILURE;
	}

	//////////////////////////////////////////////

	/**
	 * Executes the specified command on the R engine.
	 * <p>
	 * @param command the command to execute
	 * <p>
	 * @return the exit value of the specified command executed on the R engine
	 */
	public static int execute(final String... command) {
		return execute(DEFAULT_PRINTER, command);
	}

	/**
	 * Executes the specified command on the R engine, prints the output with the specified printer
	 * {@link IOHandler}.
	 * <p>
	 * @param printer the printer {@link IOHandler}
	 * @param command the command to execute
	 * <p>
	 * @return the exit value of the specified command executed on the R engine
	 */
	public static int execute(final IOHandler printer, final String... command) {
		// Check the system
		Systems.requireOS();
		// Check the arguments
		ArrayArguments.requireMinLength(command, 1);

		// Execute the command and print the output with the printer
		try {
			return Systems.execute(printer,
					Arrays.<String>merge(new String[] {R_PATH, "--slave", "-e"}, command));
		} catch (final InterruptedException ex) {
			IO.error(ex);
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return IO.EXIT_FAILURE;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the local Rserve instance is running on the default port.
	 * <p>
	 * @return {@code true} if the local Rserve instance is running on the default port,
	 *         {@code false} otherwise
	 */
	public static boolean isRunning() {
		try {
			final RConnection connection = new RConnection();
			IO.info("Rserve is running");
			connection.close();
			return true;
		} catch (final RserveException ex) {
			IO.warn(ex, "Rserve is not running");
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class RPrinter
			extends IOHandler {

		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The message prefix {@link String}.
		 */
		protected static final String PREFIX = "R> ";

		/**
		 * The printer {@link IOHandler}.
		 */
		protected final IOHandler printer;

		/**
		 * The {@link WorkQueue} used for monitoring.
		 */
		protected WorkQueue<?, ?> workQueueToMonitor;

		/**
		 * Constructs a {@link RPrinter}.
		 */
		protected RPrinter() {
			this(IO.getPrinter());
		}

		/**
		 * Constructs a {@link RPrinter} with the specified printer {@link IOHandler}.
		 * <p>
		 * @param printer the printer {@link IOHandler}
		 */
		protected RPrinter(final IOHandler printer) {
			super();
			this.printer = printer;
		}

		public void setWorkQueueToMonitor(final WorkQueue<?, ?> workQueueToMonitor) {
			this.workQueueToMonitor = workQueueToMonitor;
		}

		@Override
		public void print(final Object content, final boolean isError) {
			final String text = Objects.toString(content);
			printer.print(PREFIX.concat(text), isError);
			if (isError) {
				check(text);
			}
		}

		@Override
		public void println(final Object content, final boolean isError) {
			final String text = Objects.toString(content);
			printer.println(PREFIX.concat(text), isError);
			if (isError) {
				check(text);
			}
		}

		protected void check(final String text) {
			final String lowerCaseText = text.toLowerCase();
			if (workQueueToMonitor != null && !lowerCaseText.contains("warning") &&
					(lowerCaseText.contains("error") || lowerCaseText.contains("invalid"))) {
				IO.error(text);
				workQueueToMonitor.restart(true);
			}
		}

		@Override
		public void clear() {
			printer.clear();
		}

		/**
		 * Clones {@code this}.
		 * <p>
		 * @return a clone of {@code this}
		 *
		 * @see ICloneable
		 */
		@Override
		public RPrinter clone() {
			return new RPrinter(printer);
		}
	}
}
