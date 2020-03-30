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

import static jupiter.common.io.IO.IO;
import static jupiter.math.analysis.function.univariate.UnivariateFunctions.COS;
import static jupiter.math.analysis.function.univariate.UnivariateFunctions.SIN;

import jupiter.common.math.Maths;
import jupiter.common.math.DoubleInterval;
import jupiter.graphics.charts.panels.DynamicChartPanel;
import jupiter.math.analysis.differentiation.FiniteDifferentiator;
import jupiter.math.analysis.integration.FiniteIntegrator;

/**
 * {@link DynamicSeriesPanelDemo} demonstrates {@link DynamicChartPanel} with a
 * {@link SeriesGraphic}.
 */
public class DynamicSeriesPanelDemo {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link SeriesGraphic}.
	 */
	protected final SeriesGraphic graph;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link DynamicSeriesPanelDemo}.
	 */
	public DynamicSeriesPanelDemo() {
		graph = new SeriesGraphic("Dynamic Series Graphic Demo", "X", "Y");
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Demonstrates {@link DynamicChartPanel} with a {@link SeriesGraphic}.
	 * <p>
	 * @param args ignored
	 */
	public static void main(final String[] args) {
		final DynamicSeriesPanelDemo demo = new DynamicSeriesPanelDemo();
		demo.loadSeries();
		demo.display();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void display() {
		graph.display();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void loadSeries() {
		// Initialize
		final double from = -10., to = 10.;
		final int sampleSize = 11;
		final double step = 0.1;
		final int chartSampleSize = 10 * (sampleSize - 1) + 1;

		// • Show sine and cosine series
		graph.addSeries(0, Charts.createSeries("y = sin(x)", SIN, from, to, chartSampleSize));
		graph.addSeries(0, Charts.createSeries("y = cos(x)", COS, from, to, chartSampleSize));

		// • Finite differentiation
		// 1. Differentiate twice
		final FiniteDifferentiator derivative = new FiniteDifferentiator(SIN, sampleSize, step);
		IO.result(derivative.getDomain());
		graph.addSeries(0, Charts.createSeries("(1) y' = cos(x)", derivative, from, to,
				chartSampleSize));
		final FiniteDifferentiator derivative2 = new FiniteDifferentiator(derivative, sampleSize,
				step);
		IO.result(derivative2.getDomain());
		graph.addSeries(0, Charts.createSeries("(1) y'' = -sin(x)", derivative2, from, to,
				chartSampleSize));
		// 2. Differentiate twice directly
		final FiniteDifferentiator fastDerivative2 = new FiniteDifferentiator(SIN,
				new DoubleInterval(from / 2., to / 2.), 2, sampleSize, step);
		IO.result(fastDerivative2.getDomain());
		graph.addSeries(0, Charts.createSeries("(2) y'' = -sin(x)", fastDerivative2, from, to,
				chartSampleSize));

		// • Finite integration
		// 3. Integrate twice directly (note that the antiderivative requires initial values)
		final FiniteIntegrator fastIntegrator = new FiniteIntegrator(SIN,
				new DoubleInterval(from / 2., to / 2.), 1, sampleSize, step);
		IO.result(fastIntegrator.getDomain());
		graph.addSeries(0, Charts.createSeries("(3) Y = -cos(x)", fastIntegrator, from, to,
				chartSampleSize));

		final FiniteIntegrator fastIntegrator2 = new FiniteIntegrator(SIN,
				new DoubleInterval(from / 2., to / 2.), 2, sampleSize, step);
		graph.addSeries(0, Charts.createSeries("(3) Y = -sin(x)", fastIntegrator2, from, to,
				chartSampleSize));
	}
}
