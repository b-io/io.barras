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

import static jupiter.common.util.Characters.ESCAPE;
import static jupiter.common.util.Strings.EMPTY;

import java.io.PrintStream;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.exception.IllegalTypeException;
import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.io.IOHandler;
import jupiter.common.io.Message;
import jupiter.common.test.Arguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class ConsoleHandler
		extends IOHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The flag specifying whether to use colors.
	 */
	public static volatile boolean USE_COLORS = true;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link IConsole} to handle.
	 */
	protected IConsole console;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ConsoleHandler}.
	 */
	public ConsoleHandler() {
		super();
		console = new SystemConsole();
	}

	/**
	 * Constructs a {@link ConsoleHandler} with the specified {@link IConsole}.
	 * <p>
	 * @param console the {@link IConsole} to handle
	 */
	public ConsoleHandler(final IConsole console) {
		super();
		this.console = console;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Color} of the specified {@link SeverityLevel}.
	 * <p>
	 * @param severityLevel a {@link SeverityLevel}
	 * <p>
	 * @return the {@link Color} of the specified {@link SeverityLevel}
	 */
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
				throw new IllegalTypeException(severityLevel);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@link IConsole}.
	 * <p>
	 * @param console an {@link IConsole}
	 */
	public void setConsole(final IConsole console) {
		this.console = console;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public String input() {
		return console.input();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified content {@link Object} in the console with the specified standard type.
	 * <p>
	 * @param content the content {@link Object} to print
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
	 * Prints the specified content {@link Object} in the console with the specified standard type
	 * and terminates the line.
	 * <p>
	 * @param content the content {@link Object} to print
	 * @param isError the flag specifying whether to print in {@code console.getErr()} or in
	 *                {@code console.getOut()}
	 */
	@Override
	public void println(final Object content, final boolean isError) {
		// Check the arguments
		Arguments.requireNonNull(content);

		// Print the content
		final PrintStream printStream = isError ? console.getErr() : console.getOut();
		String text = Strings.toString(content);
		if (USE_COLORS && isError && Color.parse(text) == null) {
			text = Color.RED.getStyledText(text);
		}
		printStream.println(text);
	}

	/**
	 * Prints the specified {@link Message} (in color if {@code USE_COLORS}) and terminates the
	 * line.
	 * <p>
	 * @param message the {@link Message} to print
	 */
	@Override
	public void println(final Message message) {
		if (USE_COLORS) {
			println(getColor(message.getLevel()).getStyledText(Strings.toString(message)),
					message.getLevel().isError());
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

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public ConsoleHandler clone() {
		return new ConsoleHandler(console);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the {@code other} type prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof ConsoleHandler)) {
			return false;
		}
		final ConsoleHandler otherConsoleHandler = (ConsoleHandler) other;
		return Objects.equals(console, otherConsoleHandler.console);
	}

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	@SuppressWarnings("unchecked")
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, console);
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
		public static final String PREFIX = ESCAPE + "[";
		/**
		 * The color saturation.
		 */
		public static volatile float SATURATION = 1f;
		/**
		 * The color intensity.
		 * • 0: standard
		 * • 1: light
		 * • 2: dark
		 */
		public static volatile int INTENSITY = 0;

		public float getHue() {
			switch (this) {
				case RESET:
					throw new IllegalOperationException(Strings.join(
							"Cannot get the hue of ", Strings.quote("RESET")));
				case BLACK:
					return 0f;
				case WHITE:
					return 1f;
				case BLUE:
					return 4f / 6f;
				case CYAN:
					return 3f / 6f;
				case GREEN:
					return 2f / 6f;
				case MAGENTA:
					return 5f / 6f;
				case RED:
					return 0f;
				case YELLOW:
					return 1f / 6f;
				default:
					throw new IllegalTypeException(this);
			}
		}

		public float getSaturation() {
			switch (this) {
				case RESET:
					throw new IllegalOperationException(Strings.join(
							"Cannot get the saturation of ", Strings.quote("RESET")));
				case BLACK:
					return 0f;
				case WHITE:
					return 1f;
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

		public float getBrightness() {
			switch (this) {
				case RESET:
					throw new IllegalOperationException(Strings.join(
							"Cannot get the brightness of ", Strings.quote("RESET")));
				case BLACK:
					return 0f;
				case WHITE:
					return 1f;
				case BLUE:
				case CYAN:
				case GREEN:
				case MAGENTA:
				case RED:
				case YELLOW:
					switch (INTENSITY) {
						case 0:
							return 2f / 3f;
						case 1:
							return 1f;
						case 2:
							return 1f / 3f;
						default:
							throw new IllegalTypeException(INTENSITY);
					}
				default:
					throw new IllegalTypeException(this);
			}
		}

		public String getText(final String text) {
			return text.replace(toString(), EMPTY).replace(RESET.toString(), EMPTY);
		}

		public String getStyledText(final String text) {
			return toString() + text + RESET.toString();
		}

		/**
		 * Parses the {@link Color} encoded in the specified {@link String}.
		 * <p>
		 * @param text the {@link String} to parse
		 * <p>
		 * @return the {@link Color} encoded in the specified {@link String}, or {@code null} if
		 *         there is a problem with parsing
		 */
		public static Color parse(final String text) {
			if (text.startsWith(RESET.toString())) {
				return RESET;
			} else if (text.startsWith(BLACK.toString())) {
				return BLACK;
			} else if (text.startsWith(WHITE.toString())) {
				return WHITE;
			} else if (text.startsWith(BLUE.toString())) {
				return BLUE;
			} else if (text.startsWith(CYAN.toString())) {
				return CYAN;
			} else if (text.startsWith(GREEN.toString())) {
				return GREEN;
			} else if (text.startsWith(MAGENTA.toString())) {
				return MAGENTA;
			} else if (text.startsWith(RED.toString())) {
				return RED;
			} else if (text.startsWith(YELLOW.toString())) {
				return YELLOW;
			}
			return null;
		}

		public java.awt.Color toAWT() {
			switch (this) {
				case RESET:
					return null;
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

		/**
		 * Returns a representative {@link String} of {@code this}.
		 * <p>
		 * @return a representative {@link String} of {@code this}
		 */
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
