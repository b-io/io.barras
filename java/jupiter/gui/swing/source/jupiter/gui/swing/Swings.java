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
package jupiter.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import jupiter.common.math.Maths;
import jupiter.common.thread.Threads;
import jupiter.common.time.Chronometer;
import jupiter.common.util.Strings;

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

	/**
	 * The minimum {@link Dimension} of the progress bars.
	 */
	public static volatile Dimension PROGRESS_BAR_MIN_SIZE = new Dimension(360, 60);
	/**
	 * The preferred {@link Dimension} of the progress bars.
	 */
	public static volatile Dimension PROGRESS_BAR_PREFERRED_SIZE = new Dimension(720, 120);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Swings}.
	 */
	protected Swings() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the parameters of the specified {@link Component} by default.
	 * <p>
	 * @param component the {@link Component} to set
	 */
	public static void setDefaultParameters(final Component component) {
		setSizes(component);
	}

	/**
	 * Sets the minimum and preferred sizes of the specified {@link Component} by default.
	 * <p>
	 * @param component the {@link Component} to set
	 */
	public static void setSizes(final Component component) {
		setSizes(component, MIN_SIZE, PREFERRED_SIZE);
	}

	/**
	 * Sets the minimum and preferred sizes of the specified {@link Component}.
	 * <p>
	 * @param component     the {@link Component} to set
	 * @param minSize       a minimum {@link Dimension}
	 * @param preferredSize a preferred {@link Dimension}
	 */
	public static void setSizes(final Component component, final Dimension minSize,
			final Dimension preferredSize) {
		component.setMinimumSize(minSize);
		component.setPreferredSize(preferredSize);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static JFrame createFrame(final String title) {
		return createFrame(title, MIN_SIZE, PREFERRED_SIZE);
	}

	public static JFrame createFrame(final String title, final Dimension minSize,
			final Dimension preferredSize) {
		final JFrame frame = new JFrame(title);
		setSizes(frame, minSize, preferredSize);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		return frame;
	}

	//////////////////////////////////////////////

	public static JButton createButton(final String actionCommand,
			final ActionListener actionListener) {
		final JButton button = new JButton(Strings.capitalize(Strings.toCase(actionCommand)));
		button.setActionCommand(actionCommand);
		button.addActionListener(actionListener);
		return button;
	}

	//////////////////////////////////////////////

	public static JProgressBar createProgressBar(final int min, final int max) {
		return createProgressBar(min, max, PROGRESS_BAR_MIN_SIZE, PROGRESS_BAR_PREFERRED_SIZE);
	}

	public static JProgressBar createProgressBar(final int min, final int max,
			final Dimension minSize, final Dimension preferredSize) {
		final JProgressBar progressBar = new JProgressBar(min, max);
		setSizes(progressBar, minSize, preferredSize);
		progressBar.setStringPainted(true);
		progressBar.setValue(min);
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
		showTimeProgressBar(title, time, 100);
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
		final JFrame frame = createFrame(title, PROGRESS_BAR_MIN_SIZE, PROGRESS_BAR_PREFERRED_SIZE);
		frame.setAlwaysOnTop(true);
		final JProgressBar progressBar = createProgressBar(0, time);
		frame.add(progressBar);
		show(frame);
		final Chronometer chrono = new Chronometer();
		chrono.start();
		try {
			for (int t = step; t <= time; t += step) {
				chrono.stop();
				final int difference = t - Maths.roundToInt(chrono.getMilliseconds());
				if (difference > 0) {
					Threads.sleep(difference);
				}
				progressBar.setValue(t);
			}
		} finally {
			close(frame);
		}
	}
}
