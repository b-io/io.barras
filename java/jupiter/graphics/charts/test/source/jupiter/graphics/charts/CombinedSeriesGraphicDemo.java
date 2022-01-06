/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.math.analysis.function.univariate.UnivariateFunctions.SIN;

import jupiter.math.analysis.differentiation.FiniteDifferentiator;
import jupiter.math.analysis.function.univariate.UnivariateFunction;

/**
 * {@link CombinedSeriesGraphicDemo} demonstrates {@link CombinedSeriesGraphic}.
 */
public class CombinedSeriesGraphicDemo {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Demonstrates {@link CombinedSeriesGraphic}.
	 * <p>
	 * @param args ignored
	 */
	public static void main(final String[] args) {
		final CombinedSeriesGraphicDemo demo = new CombinedSeriesGraphicDemo();
		demo.loadSeries();
		demo.display();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link CombinedSeriesGraphic}.
	 */
	protected final CombinedSeriesGraphic graph;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link CombinedSeriesGraphicDemo}.
	 */
	public CombinedSeriesGraphicDemo() {
		graph = new CombinedSeriesGraphic("Combined Series Graphic Demo", "X", "Y1", "y2");
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void loadSeries() {
		final int sampleSize = 100;
		final double step = 1.;
		final UnivariateFunction sin = SIN;
		graph.addSeries(0, Charts.createSeries("SIN", sin, 0., 10., sampleSize));
		final UnivariateFunction cos = new FiniteDifferentiator(sin, sampleSize, step);
		graph.addSeries(1, Charts.createSeries("COS", cos, 0., 10., sampleSize));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void display() {
		graph.display();
	}
}
