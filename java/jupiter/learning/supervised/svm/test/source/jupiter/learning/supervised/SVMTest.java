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
package jupiter.learning.supervised;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.BULLET;

import java.io.IOException;

import jupiter.common.struct.table.StringTable;
import jupiter.common.test.Test;

public class SVMTest
		extends Test {

	public SVMTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of classify method, of class SVM.
	 */
	public void testClassify() {
		IO.test(BULLET, " classify");

		try {
			// Initialize
			final StringTable trainingExamples = new StringTable("test/resources/test.csv", false);
			final SVM svm = new SVM(2);

			// Train the model
			svm.load(trainingExamples, 2);
			svm.train();

			// Verify the model
			assertEquals((int) svm.classify(new double[] {0., 0.}), 0);
			assertEquals(0.69, svm.getProbabilityEstimates().get(0), 0.01);
			assertEquals((int) svm.classify(new double[] {0., 1.}), 1);
			assertEquals(0.69, svm.getProbabilityEstimates().get(1), 0.01);
			assertEquals((int) svm.classify(new double[] {1., 0.}), 2);
			assertEquals(0.69, svm.getProbabilityEstimates().get(2), 0.01);
			assertEquals((int) svm.classify(new double[] {1., 1.}), 3);
			assertEquals(0.69, svm.getProbabilityEstimates().get(3), 0.01);
		} catch (final IOException ex) {
			IO.error(ex);
		}
	}
}
