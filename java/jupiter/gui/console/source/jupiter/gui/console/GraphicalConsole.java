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
package jupiter.gui.console;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Formats.DEFAULT_CHARSET;
import static jupiter.common.util.Formats.VERSION;
import static jupiter.common.util.Strings.EMPTY;

import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jupiter.common.io.InputOutput.SeverityLevel;
import jupiter.common.io.InputOutput.Type;
import jupiter.common.io.Message;
import jupiter.common.io.Resources;
import jupiter.common.io.console.ConsoleHandler.Color;
import jupiter.common.util.Strings;
import jupiter.gui.swing.Swings;

public abstract class GraphicalConsole
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final String TITLE = "Jupiter v" + VERSION;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link JFrame}.
	 */
	protected final JFrame frame;
	/**
	 * The {@link JConsole}.
	 */
	protected final JConsole console;
	/**
	 * The {@link PrintStream}.
	 */
	protected PrintStream printStream;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link GraphicalConsole} by default.
	 */
	public GraphicalConsole() {
		// Create the frame
		frame = Swings.createFrame(TITLE);
		// Create the console
		console = new JConsole();
		frame.add(console);
		// Redirect the IO input to the console
		IO.setConsole(console);
		try {
			// Redirect the system output to the console
			printStream = new PrintStream(new OutputStreamCapturer(console, System.out), true,
					DEFAULT_CHARSET.name());
			System.setOut(printStream);
			System.setErr(printStream);
			// Show the frame
			Swings.show(frame);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final ClassNotFoundException ignored) {
		} catch (final IllegalAccessException ignored) {
		} catch (final InstantiationException ignored) {
		} catch (final UnsupportedLookAndFeelException ignored) {
		} catch (final UnsupportedEncodingException ex) {
			IO.fail(ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the prefix {@link String}.
	 * <p>
	 * @return the prefix {@link String}
	 */
	protected String getPrefix() {
		return new Message(Type.OUTPUT, SeverityLevel.INFO, EMPTY).toString();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clears the {@link JConsole}.
	 */
	public void clear() {
		console.clear();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified message {@link String} in the {@link JConsole}.
	 * <p>
	 * @param message the message {@link String} to print
	 */
	public void println(final String message) {
		console.println(message);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Executes the specified input expression {@link String}.
	 * <p>
	 * @param inputExpression the input expression {@link String} to execute
	 */
	protected abstract void execute(final String inputExpression);

	/**
	 * Interacts with the user.
	 */
	protected void run() {
		boolean isRunning = true;
		do {
			// Process the input expression
			IO.print(Color.BLUE.getStyledText(getPrefix()), false);
			final String inputExpression = IO.getInputLine().trim();
			if (Strings.toLowerCase(inputExpression).contains("clear")) {
				clear();
			} else if (Strings.toLowerCase(inputExpression).contains("exit")) {
				IO.info("Good bye!");
				isRunning = false;
			} else {
				// Execute the input expression
				execute(inputExpression);
			}
		} while (isRunning);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Exits {@code this}.
	 */
	public void exit() {
		exit(IO.EXIT_SUCCESS);
	}

	/**
	 * Exits {@code this} with the specified status code.
	 * <p>
	 * @param status the status code to exit with
	 */
	public void exit(final int status) {
		Swings.hide(frame);
		Resources.close(printStream);
		System.exit(status);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the input line of the {@link JConsole}.
	 * <p>
	 * @return the input line of the {@link JConsole}
	 */
	public String getInputLine() {
		return console.getInputLine();
	}

	/**
	 * Returns the last line of the {@link JConsole}.
	 * <p>
	 * @return the last line of the {@link JConsole}
	 */
	public String getLastLine() {
		return console.getLastLine();
	}
}
