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
package jupiter.gui.console;

import static jupiter.common.util.Formats.NEW_LINE;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import jupiter.common.util.Strings;

public class OutputStreamCapturer
		extends OutputStream
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link StringBuilder}.
	 */
	protected final StringBuilder builder;
	/**
	 * The consuming {@link JConsole}.
	 */
	protected final JConsole consumer;
	/**
	 * The previous {@link PrintStream}.
	 */
	protected final PrintStream previousPrintStream;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link OutputStreamCapturer} with the specified consuming {@link JConsole} and
	 * previous {@link PrintStream}.
	 * <p>
	 * @param consumer            the consuming {@link JConsole}
	 * @param previousPrintStream the previous {@link PrintStream}
	 */
	public OutputStreamCapturer(final JConsole consumer, final PrintStream previousPrintStream) {
		super();
		this.consumer = consumer;
		this.previousPrintStream = previousPrintStream;
		builder = Strings.createBuilder();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OUTPUT STREAM
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void write(final int b)
			throws IOException {
		final char character = (char) b;
		final String value = Character.toString(character);
		builder.append(value);
		if (value.equals(NEW_LINE)) {
			consumer.append(builder.toString());
			builder.delete(0, builder.length());
		}
		previousPrintStream.print(character);
	}
}
