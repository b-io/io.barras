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

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Strings.SINGLE_QUOTER;
import static jupiter.common.util.Strings.SPACE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import jupiter.common.thread.LockedWorkQueue;
import jupiter.common.thread.WorkQueue;
import jupiter.common.util.Strings;

public class Systems {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The OS name.
	 */
	public static final String OS_NAME = System.getProperty("os.name", "generic")
			.toLowerCase(Locale.ENGLISH);
	/**
	 * The OS type.
	 */
	public static final OS OS = getOS();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Systems() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static OS getOS() {
		if (OS_NAME.contains("nux")) {
			return OS.LINUX;
		} else if (OS_NAME.contains("darwin") || OS_NAME.contains("mac")) {
			return OS.MACOS;
		} else if (OS_NAME.contains("win")) {
			return OS.WINDOWS;
		}
		return OS.OTHER;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int execute(final String... command)
			throws InterruptedException, IOException {
		return execute(IO.getPrinter(), command);
	}

	public static int execute(final IOHandler printer, final String... command)
			throws InterruptedException, IOException {
		IO.info(Strings.joinWith(command, SPACE, SINGLE_QUOTER));
		final Process process = Runtime.getRuntime().exec(command);
		// Read the input stream from the process and print it
		final WorkQueue<InputStream, Integer> printerQueue = new LockedWorkQueue<InputStream, Integer>(
				new IOStreamWriter(printer, false), 1, 1);
		printerQueue.submit(process.getInputStream());
		// Read the error stream from the process and print it
		final WorkQueue<InputStream, Integer> errorPrinterQueue = new LockedWorkQueue<InputStream, Integer>(
				new IOStreamWriter(printer, true), 1, 1);
		errorPrinterQueue.submit(process.getErrorStream());
		// Wait until the process has terminated
		process.waitFor();
		// Clear
		printerQueue.shutdown();
		errorPrinterQueue.shutdown();
		return process.exitValue();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the system is {@link OS.LINUX} or {@link OS.MACOS}.
	 * <p>
	 * @return {@code true} if the system is {@link OS.LINUX} or {@link OS.MACOS}, {@code false}
	 *         otherwise
	 */
	public static boolean isUnix() {
		return OS == OS.LINUX || OS == OS.MACOS;
	}

	/**
	 * Tests whether the system is {@link OS.WINDOWS}.
	 * <p>
	 * @return {@code true} if the system is {@link OS.WINDOWS}, {@code false} otherwise
	 */
	public static boolean isWindows() {
		return OS == OS.WINDOWS;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void requireOS() {
		if (!isUnix() && !isWindows()) {
			throw new IllegalStateException(
					"The OS " + Strings.quote(Systems.OS) + " is not yet supported");
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum OS {
		LINUX,
		MACOS,
		OTHER,
		WINDOWS
	}
}
