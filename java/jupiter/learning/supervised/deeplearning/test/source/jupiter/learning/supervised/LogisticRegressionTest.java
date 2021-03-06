/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

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
	 * Tests {@link LogisticRegression#classify}.
	 */
	public void testClassify() {
		IO.test(BULLET, "classify");

		try {
			// Initialize
			final LogisticRegression model = new LogisticRegression("test/resources/A/X.csv",
					"test/resources/A/Y.csv");

			// Train the model
			final int iterationCount = model.train();

			// Report the statistics
			final double accuracy = model.computeAccuracy();
			final double f1Score = model.computeF1Score();
			final double cost = model.computeCost();
			IO.test(Doubles.formatPercent(accuracy), "accuracy,",
					Doubles.formatPercent(f1Score), "F1 score and",
					cost, "cost in",
					iterationCount, "iterations");

			// Verify the model
			assertEquals(0.5, accuracy, 0.05);
			assertEquals(0.5, f1Score, 0.05);
			assertEquals(0.67, cost, 0.05);
		} catch (final IOException ex) {
			IO.error(ex);
		}
	}
}
