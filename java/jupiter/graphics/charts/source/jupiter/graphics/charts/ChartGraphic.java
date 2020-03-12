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
package jupiter.graphics.charts;

import java.awt.EventQueue;
import java.util.Map;
import java.util.Set;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.map.hash.ExtendedHashMap;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;
import jupiter.graphics.charts.panels.DynamicChartPanel;
import jupiter.graphics.charts.struct.SeriesStyle;
import jupiter.gui.swing.Swings;
import jupiter.math.analysis.struct.XY;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public abstract class ChartGraphic
		extends Graphic {

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

	protected XY<String> labels;
	protected Map<Integer, SeriesStyle> styles = new ExtendedHashMap<Integer, SeriesStyle>(10);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ChartGraphic} with the specified title, x-axis label and y-axis label.
	 * <p>
	 * @param title  the title
	 * @param xLabel the label of the x-axis
	 * @param yLabel the label of the y-axis
	 */
	protected ChartGraphic(final String title, final String xLabel, final String yLabel) {
		super(title);
		labels = new XY<String>(xLabel, yLabel);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract JFreeChart createChart();

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link XYItemRenderer}.
	 * <p>
	 * @return a {@link XYItemRenderer}
	 */
	public XYItemRenderer createItemRenderer() {
		final XYItemRenderer renderer = new XYLineAndShapeRenderer();
		final Set<Map.Entry<Integer, SeriesStyle>> styleMaps = styles.entrySet();
		for (final Map.Entry<Integer, SeriesStyle> styleMap : styleMaps) {
			renderer.setSeriesPaint(styleMap.getKey(), styleMap.getValue().getColor());
			renderer.setSeriesShape(styleMap.getKey(), styleMap.getValue().getShape());
			renderer.setSeriesStroke(styleMap.getKey(), styleMap.getValue().getStroke());
		}
		return renderer;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Displays {@code this}.
	 */
	public void display() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Create the chart panel
				final JFreeChart chart = createChart();
				final ChartPanel chartPanel = new DynamicChartPanel(chart);
				setContentPane(chartPanel);
				// Show the chart graphic
				Swings.show(ChartGraphic.this);
			}
		});
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public ChartGraphic clone() {
		try {
			final ChartGraphic clone = (ChartGraphic) super.clone();
			clone.labels = Objects.clone(labels);
			clone.styles = Objects.clone(styles);
			return clone;
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}
}
