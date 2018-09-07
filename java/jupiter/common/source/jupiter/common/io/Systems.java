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
package jupiter.common.io;

import java.io.IOException;
import java.util.Locale;

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
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static OS getOS() {
		if (OS_NAME.contains("nux")) {
			return jupiter.common.io.Systems.OS.LINUX;
		} else if (OS_NAME.contains("darwin") || OS_NAME.contains("mac")) {
			return jupiter.common.io.Systems.OS.MACOS;
		} else if (OS_NAME.contains("win")) {
			return jupiter.common.io.Systems.OS.WINDOWS;
		}
		return jupiter.common.io.Systems.OS.OTHER;
	}

	public static boolean isUnix() {
		return OS == jupiter.common.io.Systems.OS.LINUX || OS == jupiter.common.io.Systems.OS.MACOS;
	}

	public static boolean isWindows() {
		return OS == jupiter.common.io.Systems.OS.WINDOWS;
	}

	public static Process execute(final String command)
			throws IOException {
		return Runtime.getRuntime().exec(command);
	}

	public static Process execute(final String[] commands)
			throws IOException {
		return Runtime.getRuntime().exec(commands);
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
