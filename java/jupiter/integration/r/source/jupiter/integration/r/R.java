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
package jupiter.integration.r;

import static jupiter.common.io.IO.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.io.Systems;
import jupiter.common.util.Arrays;
import jupiter.common.util.Strings;

public class R {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final String PATH = System.getenv("R") + "/bin/R.exe";
	protected static final String ARGS = "--no-save --slave";
	protected static final String REPO = "https://cloud.r-project.org";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected R() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// R MONITOR
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean installPackage(final String name) {
		return execute("if (!'" + name + "' %in% installed.packages())" + "install.packages('" +
				name + "', repos='" + REPO + "')");
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
		if (!(installPackage("Rserve") && execute("library(Rserve); Rserve(" +
				(debug ? "TRUE" : "FALSE") + ", args='" + ARGS + "')"))) {
			return false;
		}

		// Try up to 5 times before giving up; we can be conservative here, because at this point
		// the process execution itself was successful and the start up is usually asynchronous
		int attempts = 5;
		do {
			// Sleep in case the start up is delayed or asynchronous
			try {
				Thread.sleep(2000);
			} catch (final InterruptedException ignored) {
			}
			;
			--attempts;
		} while (attempts > 0 && !isRunning());
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean stop() {
		try {
			final RConnection c = new RConnection();
			c.shutdown();
		} catch (final RserveException ex) {
			IO.warn("Fail to stop Rserve", ex);
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether Rserve is currently running (on local machine and default port).
	 *
	 * @return {@code true} if local Rserve instance is running, {@code false} otherwise
	 */
	public static boolean isRunning() {
		try {
			final RConnection c = new RConnection();
			IO.info("Rserve is running");
			c.close();
			return true;
		} catch (final RserveException ex) {
			IO.warn("Rserve is not running", ex);
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
		return execute(script, ARGS);
	}

	public static boolean execute(final String script, final String args) {
		try {
			IO.debug(">> Run ", Strings.quote(script));
			final Process process;
			if (Systems.isWindows()) {
				// Windows
				process = Systems.execute(Strings.doubleQuote(PATH) + " -e " +
						Strings.doubleQuote(script) + " " + args);
			} else {
				// Unix
				process = Systems.execute(
						Arrays.toArray("/bin/sh", "-c", "echo " + Strings.doubleQuote(script) +
								" | " + Strings.doubleQuote(PATH) + " " + args));
			}
			new RStream(process.getInputStream());
			new RStream(process.getErrorStream());
			if (!Systems.isWindows()) {
				process.waitFor();
			}
			IO.debug("<< Done");
			return true;
		} catch (final InterruptedException ex) {
			IO.error("<< Fail to run the script", ex);
		} catch (final IOException ex) {
			IO.error("<< Fail to run the script", ex);
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WORKER
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class RStream
			extends Thread {

		protected final InputStream input;

		RStream(final InputStream input) {
			this.input = input;
			start();
		}

		@Override
		public void run() {
			try {
				final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;
				while ((line = reader.readLine()) != null) {
					IO.info("R> ", line);
				}
			} catch (final IOException ex) {
				IO.error(ex);
			}
		}
	}
}
