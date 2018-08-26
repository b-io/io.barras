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
package jupiter.common.io.console;

import jupiter.common.io.IOHandler;
import jupiter.common.io.Message;
import jupiter.common.test.Arguments;
import jupiter.common.util.Strings;

public class ConsoleHandler
		extends IOHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The option specifying whether to use colors.
	 */
	public static volatile boolean USE_COLORS = true;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The console to handle.
	 */
	protected IConsole console;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public ConsoleHandler() {
		console = new SystemConsole();
	}

	public ConsoleHandler(final IConsole console) {
		this.console = console;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the console.
	 * <p>
	 * @param console an {@link IConsole}
	 */
	public void setConsole(final IConsole console) {
		this.console = console;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public String input() {
		return console.input();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void print(final Object content, final boolean isError) {
		// Check the arguments
		Arguments.requireNonNull(content);

		// Print the content
		if (isError) {
			console.getErr().print(content);
		} else {
			console.getOut().print(content);
		}
	}

	/**
	 * Prints the specified object in the console and then terminates the line.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param isError the option specifying whether to print in {@code console.getErr()} or in
	 *                {@code console.getOut()}
	 */
	@Override
	public void println(final Object content, final boolean isError) {
		// Check the arguments
		Arguments.requireNonNull(content);

		// Print the content
		if (isError) {
			console.getErr().println(content);
		} else {
			console.getOut().println(content);
		}
	}

	/**
	 * Prints the specified message (in color if {@code USE_COLORS}) in the console.
	 * <p>
	 * @param message the {@link Message} to print
	 */
	@Override
	public void println(final Message message) {
		if (USE_COLORS) {
			final String color;
			switch (message.getLevel()) {
				case TRACE:
					color = Strings.toString(Color.MAGENTA);
					break;
				case DEBUG:
					color = Strings.toString(Color.GREEN);
					break;
				case TEST:
					color = Strings.toString(Color.CYAN);
					break;
				case INFO:
					color = Strings.toString(Color.BLUE);
					break;
				case RESULT:
					color = Strings.toString(Color.BLACK);
					break;
				case WARNING:
					color = Strings.toString(Color.YELLOW);
					break;
				case ERROR:
				case FAILURE:
				default:
					color = Strings.toString(Color.RED);
			}
			println(color + message + Color.RESET, message.getLevel().isError());
		} else {
			println(message, message.getLevel().isError());
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEANERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void clear() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum Color {
		RESET,
		BLACK,
		BLUE,
		CYAN,
		GREEN,
		MAGENTA,
		RED,
		WHITE,
		YELLOW;

		/**
		 * The color intensity.
		 * <p>
		 * 0: standard 1: light 2: dark
		 */
		public static volatile int INTENSITY = 0;

		@Override
		public String toString() {
			switch (this) {
				case BLACK:
					return "\u001B[" + INTENSITY + ";30m";
				case BLUE:
					return "\u001B[" + INTENSITY + ";34m";
				case CYAN:
					return "\u001B[" + INTENSITY + ";36m";
				case GREEN:
					return "\u001B[" + INTENSITY + ";32m";
				case MAGENTA:
					return "\u001B[" + INTENSITY + ";35m";
				case RED:
					return "\u001B[" + INTENSITY + ";31m";
				case WHITE:
					return "\u001B[" + INTENSITY + ";37m";
				case YELLOW:
					return "\u001B[" + INTENSITY + ";33m";
				default: // RESET
					return "\u001B[" + INTENSITY + ";0m";
			}
		}
	}
}
