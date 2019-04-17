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
package jupiter.common.io.console;

import jupiter.common.exception.IllegalTypeException;
import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.io.IOHandler;
import jupiter.common.io.Message;
import jupiter.common.test.Arguments;
import jupiter.common.thread.Worker;
import jupiter.common.util.Strings;

public class ConsoleHandler
		extends IOHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether to use colors.
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
		super();
		console = new SystemConsole();
	}

	public ConsoleHandler(final IConsole console) {
		super();
		this.console = console;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Color getColor(final SeverityLevel severityLevel) {
		switch (severityLevel) {
			case TRACE:
				return Color.MAGENTA;
			case DEBUG:
				return Color.GREEN;
			case TEST:
				return Color.CYAN;
			case INFO:
				return Color.BLUE;
			case RESULT:
				return Color.BLACK;
			case WARNING:
				return Color.YELLOW;
			case ERROR:
			case FAILURE:
				return Color.RED;
			default:
				return Color.BLACK;
		}
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

	/**
	 * Prints the specified content in the console.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param isError the flag specifying whether to print in {@code console.getErr()} or in
	 *                {@code console.getOut()}
	 */
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
	 * Prints the specified content in the console and then terminates the line.
	 * <p>
	 * @param content the {@link Object} to print
	 * @param isError the flag specifying whether to print in {@code console.getErr()} or in
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
	 * Prints the specified message (whether in the standard output or in the standard error and in
	 * color if {@code USE_COLORS}) in the console and terminates the line.
	 * <p>
	 * @param message the {@link Message} to print
	 */
	@Override
	public void println(final Message message) {
		if (USE_COLORS) {
			final String color = Strings.toString(getColor(message.getLevel()));
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
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public Worker<Message, Integer> clone() {
		return new ConsoleHandler(console);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENUMS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public enum Color {
		RESET,

		BLACK,
		WHITE,

		BLUE,
		CYAN,
		GREEN,
		MAGENTA,
		RED,
		YELLOW;

		/**
		 * The color prefix.
		 */
		public static volatile String PREFIX = "\u001B[";
		/**
		 * The color saturation.
		 */
		public static volatile int SATURATION = 100;
		/**
		 * The color intensity.
		 * <p>
		 * - 0: standard
		 * - 1: light
		 * - 2: dark
		 */
		public static volatile int INTENSITY = 0;

		public int getHue() {
			switch (this) {
				case RESET:
				case BLACK:
					return 0;
				case WHITE:
					return 100;
				case BLUE:
					return 240;
				case CYAN:
					return 180;
				case GREEN:
					return 120;
				case MAGENTA:
					return 300;
				case RED:
					return 0;
				case YELLOW:
					return 60;
				default:
					throw new IllegalTypeException(this);
			}
		}

		public int getSaturation() {
			switch (this) {
				case RESET:
				case BLACK:
					return 0;
				case WHITE:
					return 100;
				case BLUE:
				case CYAN:
				case GREEN:
				case MAGENTA:
				case RED:
				case YELLOW:
					return SATURATION;
				default:
					throw new IllegalTypeException(this);
			}
		}

		public int getBrightness() {
			switch (this) {
				case RESET:
				case BLACK:
					return 0;
				case WHITE:
					return 100;
				default:
					switch (INTENSITY) {
						case 0:
							return 67;
						case 1:
							return 100;
						case 2:
							return 33;
						default:
							throw new IllegalTypeException(INTENSITY);
					}
			}
		}

		public String getText(final String text) {
			return text.replace(toString(), Strings.EMPTY).replace(RESET.toString(), Strings.EMPTY);
		}

		public static Color parse(final String string) {
			if (string.startsWith(RESET.toString())) {
				return RESET;
			} else if (string.startsWith(BLACK.toString())) {
				return BLACK;
			} else if (string.startsWith(WHITE.toString())) {
				return WHITE;
			} else if (string.startsWith(BLUE.toString())) {
				return BLUE;
			} else if (string.startsWith(CYAN.toString())) {
				return CYAN;
			} else if (string.startsWith(GREEN.toString())) {
				return GREEN;
			} else if (string.startsWith(MAGENTA.toString())) {
				return MAGENTA;
			} else if (string.startsWith(RED.toString())) {
				return RED;
			} else if (string.startsWith(YELLOW.toString())) {
				return YELLOW;
			}
			return null;
		}

		public java.awt.Color toAWT() {
			switch (this) {
				case RESET:
				case BLACK:
				case WHITE:
				case BLUE:
				case CYAN:
				case GREEN:
				case MAGENTA:
				case RED:
				case YELLOW:
					return java.awt.Color.getHSBColor(getHue(), getSaturation(), getBrightness());
				default:
					throw new IllegalTypeException(this);
			}
		}

		@Override
		public String toString() {
			switch (this) {
				case RESET:
					return PREFIX + INTENSITY + ";0m";
				case BLACK:
					return PREFIX + INTENSITY + ";30m";
				case WHITE:
					return PREFIX + INTENSITY + ";37m";
				case BLUE:
					return PREFIX + INTENSITY + ";34m";
				case CYAN:
					return PREFIX + INTENSITY + ";36m";
				case GREEN:
					return PREFIX + INTENSITY + ";32m";
				case MAGENTA:
					return PREFIX + INTENSITY + ";35m";
				case RED:
					return PREFIX + INTENSITY + ";31m";
				case YELLOW:
					return PREFIX + INTENSITY + ";33m";
				default:
					throw new IllegalTypeException(this);
			}
		}
	}
}
