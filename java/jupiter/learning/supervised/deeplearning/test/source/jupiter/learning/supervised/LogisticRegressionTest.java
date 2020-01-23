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
package jupiter.learning.supervised;

import static jupiter.common.io.IO.IO;

import java.io.IOException;

import jupiter.common.test.Test;
import jupiter.common.util.Doubles;

public class LogisticRegressionTest
		extends Test {

	public LogisticRegressionTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of classify method, of class LogisticRegression.
	 */
	public void testClassify() {
		IO.test("• classify");

		try {
			// Initialize
			final LogisticRegression model = new LogisticRegression("test/resources/A/X.csv",
					"test/resources/A/Y.csv");

			// Train
			final int iterationCount = model.train();

			// Test
			// • The accuracy
			final double accuracy = model.computeAccuracy();
			IO.test(Doubles.formatPercent(accuracy), " accuracy in ", iterationCount, " iterations");
			assertEquals(0.5, accuracy, 0.05);
			// • The F1 score
			final double f1Score = model.computeF1Score();
			IO.test(Doubles.formatPercent(f1Score), " F1 score in ", iterationCount, " iterations");
			assertEquals(0.5, f1Score, 0.05);
			// • The cost
			final double cost = model.computeCost();
			assertEquals(0.67, cost, 0.05);
		} catch (final IOException ex) {
			IO.error(ex);
		}
	}
}
