/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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
package jupiter.graphics.charts.panels;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;

public class JPanels {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected JPanels() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// JPANELS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Displays the specified message in the specified panel.
	 * <p>
	 * @param panel   the panel to modify
	 * @param message the message to display
	 */
	public static void displayMessage(final JPanel panel, final String message) {
		final JPanel p = new JPanel();
		p.add(new JLabel(message));
		JOptionPane.showMessageDialog(panel, p);
	}

	/**
	 * Adds the scroll zoom to the specified panel.
	 * <p>
	 * @param panel the panel to modify
	 */
	public static void addScrollZoom(final JPanel panel) {
		panel.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(final MouseWheelEvent event) {
				if (event.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
					if (event.getWheelRotation() < 0) {
						increaseZoom((JComponent) event.getComponent(), true);
					} else {
						decreaseZoom((JComponent) event.getComponent(), true);
					}
				}
			}

			protected synchronized void increaseZoom(final JComponent chart,
					final boolean saveAction) {
				final ChartPanel chartPanel = (ChartPanel) chart;
				zoomChartAxis(chartPanel, true);
			}

			protected synchronized void decreaseZoom(final JComponent chart,
					final boolean saveAction) {
				final ChartPanel chartPanel = (ChartPanel) chart;
				zoomChartAxis(chartPanel, false);
			}

			protected void zoomChartAxis(final ChartPanel chartPanel, final boolean increase) {
				final int width = chartPanel.getMaximumDrawWidth() -
						chartPanel.getMinimumDrawWidth();
				final int height = chartPanel.getMaximumDrawHeight() -
						chartPanel.getMinimumDrawWidth();
				if (increase) {
					chartPanel.zoomInBoth(width / 2., height / 2.);
				} else {
					chartPanel.zoomOutBoth(width / 2., height / 2.);
				}
			}
		});
	}
}
