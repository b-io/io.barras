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
package jupiter.lang.r;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Strings.SPACE;

import java.io.IOException;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.io.IOHandler;
import jupiter.common.io.Message;
import jupiter.common.io.Systems;
import jupiter.common.test.ArrayArguments;
import jupiter.common.thread.Threads;
import jupiter.common.thread.Worker;
import jupiter.common.util.Arrays;
import jupiter.common.util.Strings;

public class R {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String PATH = "Rscript";
	public static final String[] ARGS = new String[] {};
	public static volatile String REPO = "https://cloud.r-project.org";

	public static final IOHandler PRINTER = new RPrinter();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected R() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean installPackage(final String name) {
		return execute("if (!" + Strings.singleQuote(name) + " %in% installed.packages())" +
				"install.packages(" + Strings.singleQuote(name) +
					", repos=" + Strings.singleQuote(REPO) +
				")");
	}

	public static boolean installPackages(final String[] names) {
		boolean isSuccess = true;
		for (final String name : names) {
			isSuccess &= installPackage(name);
		}
		return isSuccess;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean start() {
		return start(SeverityLevel.DEBUG.compareTo(IO.getSeverityLevel()) >= 0);
	}

	public static boolean start(final boolean debug) {
		// Test whether the server is already running
		if (R.isRunning()) {
			return true;
		}

		// Launch Rserve
		if (!(installPackage("Rserve") && execute("library(Rserve);" +
				"Rserve(" + (debug ? "T" : "F") +
					", args = " + Strings.singleQuote(Strings.joinWith(ARGS, SPACE)) +
				")"))) {
			return false;
		}

		// Try up to 5 times before giving up; we can be conservative here, because at this point
		// the process execution itself was successful and the start up is usually asynchronous
		int attempts = 5;
		do {
			// Sleep in case the start up is delayed or asynchronous
			Threads.sleep();
			--attempts;
		} while (attempts > 0 && !isRunning());
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean stop() {
		try {
			new RConnection().shutdown();
		} catch (final RserveException ex) {
			IO.warn("Fail to stop Rserve", ex);
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Executes the specified script on the R engine and returns {@code true} if the R engine
	 * processed it, {@code false} otherwise.
	 * <p>
	 * @param script the script to execute
	 * <p>
	 * @return {@code true} if the R engine processed the specified script, {@code false} otherwise
	 */
	public static boolean execute(final String script) {
		return execute(Arrays.<String>merge(Strings.asArray(script), ARGS));
	}

	public static boolean execute(final String... command) {
		// Check the arguments
		ArrayArguments.requireMinLength(command, 1);

		// Process
		try {
			// Test whether the OS is Windows or Unix
			if (Systems.isWindows()) {
				// - Execute the script on Windows
				Systems.execute(PRINTER, Arrays.<String>merge(new String[] {PATH, "-e"}, command));
			} else {
				// - Execute the script on Unix
				Systems.execute(PRINTER,
						Arrays.<String>merge(
								new String[] {"/bin/sh", "-c", "echo", command[0], "|", PATH},
								Arrays.<String>take(command, 1, command.length)));
			}
			return true;
		} catch (final InterruptedException ex) {
			IO.error("Fail to execute the script", Strings.quote(command[0]), ex);
		} catch (final IOException ex) {
			IO.error("Fail to execute the script", Strings.quote(command[0]), ex);
		}
		return false;
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
			IO.warn("Rserve is not running", ex);
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class RPrinter
			extends IOHandler {

		protected static final String PREFIX = "R> ";

		/**
		 * The printer.
		 */
		protected final IOHandler printer;

		protected RPrinter() {
			this(IO.getPrinter());
		}

		protected RPrinter(final IOHandler printer) {
			super();
			this.printer = printer;
		}

		@Override
		public void print(final Object content, final boolean isError) {
			printer.print(PREFIX + content, isError);
		}

		@Override
		public void println(final Object content, final boolean isError) {
			printer.println(PREFIX + content, isError);
		}

		@Override
		public void clear() {
			printer.clear();
		}

		@Override
		public Worker<Message, Integer> clone() {
			return new RPrinter(printer);
		}
	}
}
