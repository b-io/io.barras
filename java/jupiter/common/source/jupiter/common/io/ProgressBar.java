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
import static jupiter.common.util.Formats.DEFAULT_LINE_LENGTH;

import java.io.Serializable;

import jupiter.common.util.Characters;
import jupiter.common.util.Integers;

public class ProgressBar
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The start symbol.
	 */
	public static volatile char START_SYMBOL = Characters.LEFT_BRACKET;
	/**
	 * The end symbol.
	 */
	public static volatile char END_SYMBOL = Characters.RIGHT_BRACKET;

	/**
	 * The symbol filling the progress bar.
	 */
	public static volatile String SYMBOL = "#";
	/**
	 * The space filling the progress bar.
	 */
	public static volatile String SPACE = "-";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The maximum number of symbols in the progress bar (excluding the wrapping symbols).
	 */
	protected final int length;
	/**
	 * The flag specifying whether to print in the standard error or in the standard output.
	 */
	protected final boolean isError;

	/**
	 * The current number of symbols in the progress bar (excluding the wrapping symbols).
	 */
	protected int progress = 0;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ProgressBar}.
	 */
	public ProgressBar() {
		this(DEFAULT_LINE_LENGTH - 2);
	}

	/**
	 * Constructs a {@link ProgressBar} with the specified length.
	 * <p>
	 * @param length the maximum number of symbols (excluding the wrapping symbols)
	 */
	public ProgressBar(final int length) {
		this(length, false);
	}

	/**
	 * Constructs a {@link ProgressBar} with the specified length and flag specifying whether to
	 * print in the standard error or in the standard output.
	 * <p>
	 * @param length  the maximum number of symbols (excluding the wrapping symbols)
	 * @param isError the flag specifying whether to print in the standard error or in the standard
	 *                output
	 */
	public ProgressBar(final int length, final boolean isError) {
		this.length = length;
		this.isError = isError;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints a progress bar until {@code i / n * length} symbols.
	 * <p>
	 * @param i a {@code double} value
	 * @param n the upper bound of {@code i}
	 */
	public void print(final double i, final double n) {
		if (i == 0) {
			start();
		}
		final int symbolCount = countSymbols(i, n) - progress;
		if (symbolCount > 0) {
			printSymbols(symbolCount);
			if (progress == length) {
				end();
			}
		}
	}

	/**
	 * Prints a progress bar of {@code i / n * length} symbols.
	 * <p>
	 * @param i a {@code double} value
	 * @param n the upper bound of {@code i}
	 */
	public void println(final double i, final double n) {
		progress = countSymbols(i, n);
		start();
		printSymbols();
		printSpaces();
		end();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the start of the progress bar.
	 */
	public synchronized void start() {
		progress = 0;
		IO.print(START_SYMBOL, isError);
	}

	/**
	 * Prints the end of the progress bar.
	 */
	public synchronized void end() {
		IO.println(END_SYMBOL, isError);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of symbols to print.
	 * <p>
	 * @param i a {@code double} value
	 * @param n the upper bound of {@code i}
	 * <p>
	 * @return the number of symbols to print
	 */
	protected int countSymbols(final double i, final double n) {
		return Integers.convert(i / n * length);
	}

	/**
	 * Prints the {@code progress} symbols.
	 */
	protected void printSymbols() {
		printSymbols(progress);
	}

	/**
	 * Prints the specified number of symbols.
	 * <p>
	 * @param n the number of symbols to print
	 */
	public synchronized void printSymbols(final int n) {
		if (n > 0) {
			IO.print(SYMBOL, n, isError);
			progress += n;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints {@code length - progress} spaces.
	 */
	protected void printSpaces() {
		printSpaces(length - progress);
	}

	/**
	 * Prints the specified number of spaces.
	 * <p>
	 * @param n the number of spaces to print
	 */
	protected synchronized void printSpaces(final int n) {
		IO.print(SPACE, n, isError);
	}
}
