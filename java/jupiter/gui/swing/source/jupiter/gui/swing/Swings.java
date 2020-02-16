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
package jupiter.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import jupiter.common.thread.Threads;

public class Swings {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The minimum {@link Dimension} of the windows.
	 */
	public static volatile Dimension MIN_SIZE = new Dimension(720, 480);
	/**
	 * The preferred {@link Dimension} of the windows.
	 */
	public static volatile Dimension PREFERRED_SIZE = new Dimension(1440, 960);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Swings}.
	 */
	protected Swings() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static JFrame createFrame(final String title) {
		final JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		return frame;
	}

	public static JProgressBar createProgressBar(final int min, final int max) {
		final JProgressBar progressBar = new JProgressBar(min, max);
		progressBar.setStringPainted(true);
		progressBar.setValue(min);
		progressBar.setMinimumSize(new Dimension(MIN_SIZE.width / 2, 50));
		progressBar.setPreferredSize(new Dimension(PREFERRED_SIZE.width / 2, 50));
		return progressBar;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GUI
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sizes the specified {@link Window} to fit the preferred size and layouts of its subcomponents
	 * and shows it in the center of the screen.
	 * <p>
	 * @param window the {@link Window} to show
	 */
	public static void show(final Window window) {
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	/**
	 * Hides the specified {@link Window}.
	 * <p>
	 * @param window the {@link Window} to hide
	 */
	public static void hide(final Window window) {
		window.setVisible(false);
	}

	/**
	 * Closes the specified {@link Window} and releases all of the native screen resources used by
	 * it, its subcomponents and all of its owned children.
	 * <p>
	 * @param window the {@link Window} to close
	 */
	public static void close(final Window window) {
		window.setVisible(false);
		window.dispose();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shows the specified message {@link String} in the specified {@link JPanel}.
	 * <p>
	 * @param panel   the {@link JPanel} in which the message {@link String} is showed
	 * @param message the message {@link String} to show
	 */
	public static void showMessage(final JPanel panel, final String message) {
		final JPanel p = new JPanel();
		p.add(new JLabel(message));
		JOptionPane.showMessageDialog(panel, p);
	}

	//////////////////////////////////////////////

	/**
	 * Shows the progress bar for the specified length of time (in milliseconds).
	 * <p>
	 * @param title the title of the progress bar
	 * @param time  the length of time to show the progress bar (in milliseconds)
	 */
	public static void showTimeProgressBar(final String title, final int time) {
		showTimeProgressBar(title, time, 500);
	}

	/**
	 * Shows the progress bar for the specified length of time (in milliseconds) with the specified
	 * time interval between each refresh (in milliseconds).
	 * <p>
	 * @param title the title of the progress bar
	 * @param time  the length of time to show the progress bar (in milliseconds)
	 * @param step  the time interval between each refresh (in milliseconds)
	 */
	public static void showTimeProgressBar(final String title, final int time, final int step) {
		final JFrame frame = createFrame(title);
		frame.setAlwaysOnTop(true);
		final JProgressBar progressBar = createProgressBar(0, time);
		frame.add(progressBar);
		show(frame);
		try {
			int value = 0; // [ms]
			while (value < time) {
				Threads.sleep(step);
				value = Math.min(value + step, time);
				progressBar.setValue(value);
			}
		} finally {
			close(frame);
		}
	}
}
