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

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Formats.DEFAULT_CHARSET;
import static jupiter.common.util.Formats.VERSION;

import java.awt.BorderLayout;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jupiter.common.io.Resources;

public class GraphicalConsole
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

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
	 * Constructs a {@link GraphicalConsole}.
	 */
	public GraphicalConsole() {
		// Create the frame
		frame = new JFrame(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setSize(600, 400);
		// Create the console
		console = new JConsole();
		frame.add(console);
		// Redirect the input of IO to this input
		IO.setConsole(console);
		try {
			// Redirect the system output to the console
			printStream = new PrintStream(new OutputStreamCapturer(console, System.out), true,
					DEFAULT_CHARSET.name());
			System.setOut(printStream);
			System.setErr(printStream);
			// Display the frame
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
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
	// GRAPHICAL CONSOLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the input line of the {@link JConsole}.
	 * <p>
	 * @return the input line of the {@link JConsole}
	 */
	public String input() {
		return console.input();
	}

	/**
	 * Returns the last line of the {@link JConsole}.
	 * <p>
	 * @return the last line of the {@link JConsole}
	 */
	public String getLastLine() {
		return console.getLastLine();
	}

	/**
	 * Prints the specified message {@link String} in the {@link JConsole}.
	 * <p>
	 * @param message the message {@link String} to print
	 */
	public void println(final String message) {
		console.println(message);
	}

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
		frame.setVisible(false);
		frame.dispose();
		Resources.close(printStream);
		System.exit(status);
	}
}
