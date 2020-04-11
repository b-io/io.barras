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

import static jupiter.common.io.InputOutput.IO;
import static jupiter.math.analysis.function.univariate.UnivariateFunctions.COS;
import static jupiter.math.analysis.function.univariate.UnivariateFunctions.SIN;

import jupiter.common.math.DoubleInterval;
import jupiter.graphics.charts.panels.DynamicChartPanel;
import jupiter.math.analysis.differentiation.FiniteDifferentiator;
import jupiter.math.analysis.function.univariate.UnivariateFunction;
import jupiter.math.analysis.integration.FiniteIntegrator;

/**
 * {@link DynamicSeriesPanelDemo} demonstrates {@link DynamicChartPanel} with a
 * {@link SeriesGraphic}.
 */
public class DynamicSeriesPanelDemo {

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
		graph = new SeriesGraphic("Dynamic Series Graphic Demo", "X", "Trigonometry", "Custom");
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected void loadSeries() {
		// Initialize
		// • The interval
		final double from = -10., to = 10.;
		final double halfFrom = from / 2., halfTo = to / 2.;
		final DoubleInterval halfInterval = new DoubleInterval(halfFrom, halfTo);
		// • The sample
		final int sampleSize = 11;
		final double step = 0.01;
		final int chartSampleSize = 10 * (sampleSize - 1) + 1;
		// • The custom function with its derivative and antiderivative
		final UnivariateFunction y = new UnivariateFunction() {
			/**
			 * The generated serial version ID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected double a(final double x) {
				return x * x + 2. * x + 1.;
			}
		};
		final UnivariateFunction dy = new UnivariateFunction() {
			/**
			 * The generated serial version ID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected double a(final double x) {
				return 2. * x + 2.;
			}
		};
		final UnivariateFunction Y = new UnivariateFunction() {
			/**
			 * The generated serial version ID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected double a(final double x) {
				return x * x * x / 3. + x * x + x;
			}
		};

		// Show the sine, cosine and custom series
		graph.addSeries(0, Charts.createSeries("f(x) = sin(x)", SIN, from, to, chartSampleSize));
		graph.addSeries(0, Charts.createSeries("f(x) = cos(x)", COS, from, to, chartSampleSize));
		graph.addSeries(1, Charts.createSeries("y = 2x + 2", dy, from, to, chartSampleSize));
		graph.addSeries(1, Charts.createSeries("y = x^2 + 2x + 1", y, halfFrom, halfTo,
				chartSampleSize));
		graph.addSeries(1, Charts.createSeries("Y = x^3 / 3 + x^2 + x", Y, halfFrom, halfTo,
				chartSampleSize));

		// • Finite differentiation
		// 1. Differentiate twice successively
		final FiniteDifferentiator derivative = new FiniteDifferentiator(SIN, sampleSize, step);
		IO.test(derivative.getDomain());
		graph.addSeries(0, Charts.createSeries("(D1) f'(x) = cos(x)", derivative, from, to,
				chartSampleSize));
		final FiniteDifferentiator derivative2 = new FiniteDifferentiator(derivative, sampleSize,
				step);
		IO.test(derivative2.getDomain());
		graph.addSeries(0, Charts.createSeries("(D1) f''(x) = -sin(x)", derivative2, from, to,
				chartSampleSize));
		// 2. Differentiate twice directly
		final FiniteDifferentiator directDerivative2 = new FiniteDifferentiator(SIN, halfInterval,
				2, sampleSize, step);
		IO.test(directDerivative2.getDomain());
		graph.addSeries(0, Charts.createSeries("(D2) f''(x) = -sin(x)", directDerivative2, from, to,
				chartSampleSize));
		// 3. Differentiate the custom function
		final FiniteDifferentiator df = new FiniteDifferentiator(y, halfInterval, sampleSize, step);
		IO.test(df.getDomain());
		graph.addSeries(1, Charts.createSeries("(D3) f'(x) = 2x + 2", df, from, to,
				chartSampleSize));

		// • Finite integration
		// 1. Integrate twice successively
		final FiniteIntegrator integrator = new FiniteIntegrator(SIN,
				new DoubleInterval(from, to), sampleSize, step);
		IO.test(integrator.getDomain());
		graph.addSeries(0, Charts.createSeries("(I1) F(x) = -cos(x)", integrator, from, to,
				chartSampleSize));
		final FiniteIntegrator integrator2 = new FiniteIntegrator(integrator,
				new DoubleInterval(from, to), sampleSize, step);
		IO.test(integrator2.getDomain());
		graph.addSeries(0, Charts.createSeries("(I1) F(x) = -sin(x)", integrator2, from, to,
				chartSampleSize));
		// 2. Integrate twice directly
		final FiniteIntegrator directIntegrator2 = new FiniteIntegrator(SIN,
				halfInterval, 2, sampleSize, step);
		IO.test(directIntegrator2.getDomain());
		graph.addSeries(0, Charts.createSeries("(I2) F(x) = -sin(x)", directIntegrator2, from, to,
				chartSampleSize));
		// 3. Integrate the custom function
		final FiniteIntegrator F = new FiniteIntegrator(y, halfInterval, sampleSize, step);
		IO.test(F.getDomain());
		graph.addSeries(1, Charts.createSeries("(I3) F(x) = x^3 / 3 + x^2 + x + C", F, from, to,
				chartSampleSize));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void display() {
		graph.display();
	}
}
