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
package jupiter.math.linear.entity;

import static jupiter.common.io.IO.IO;

import org.junit.Test;

import junit.framework.TestCase;
import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.test.Tests;
import jupiter.common.time.Chronometer;
import jupiter.common.util.Doubles;

public class MatrixTest
		extends TestCase {

	public MatrixTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of dot method, of class Matrix.
	 */
	@Test
	public void testDot() {
		IO.test("dot");

		// Set up
		IO.setSeverityLevel(SeverityLevel.TEST);
		final int testCount = 2;
		final int[] rowCount = {
			10, 50, 100, 250, 500
		};
		final int[] columnCount = {
			10, 100, 500, 1000
		};

		// Test
		for (final int m : rowCount) {
			for (final int n : columnCount) {
				IO.test(m, " rows and ", n, " columns");

				final Matrix instance = new Matrix(m, Doubles.createSequence(m * n));
				final Chronometer chrono = new Chronometer();
				final double[] times = new double[testCount];

				// Test normal version
				IO.test("Normal:");
				Matrix expected = null;
				for (int i = 0; i < testCount; ++i) {
					chrono.start();
					expected = instance.dot(instance.transpose()).toMatrix();
					times[i] = chrono.stop();
				}
				Tests.printTimes(times);
				//expected.toTable().export("EXPECTED.csv");

				// Test parallel version
				IO.test("Parallelized:");
				Matrix.start();
				Matrix.USE_GPUS = false;
				try {
					Matrix found = null;
					for (int i = 0; i < testCount; ++i) {
						chrono.start();
						found = instance.dot(instance.transpose()).toMatrix();
						times[i] = chrono.stop();
					}
					Tests.printTimes(times);
					//found.toTable().export("PARALLEL.csv");
					assertEquals(expected, found);
				} finally {
					Matrix.stop();
				}

				// Test GPU version
				IO.test("With GPU:");
				Matrix.USE_GPUS = true;
				try {
					Matrix found = null;
					for (int i = 0; i < testCount; ++i) {
						chrono.start();
						found = instance.dot(instance.transpose()).toMatrix();
						times[i] = chrono.stop();
					}
					Tests.printTimes(times);
					//found.toTable().export("GPU.csv");
					assertEquals(expected, found);
				} finally {
					Matrix.USE_GPUS = false;
				}

				// Test hybrid version
				IO.test("Hybrid:");
				Matrix.start();
				try {
					Matrix found = null;
					for (int i = 0; i < testCount; ++i) {
						chrono.start();
						found = instance.dot(instance.transpose()).toMatrix();
						times[i] = chrono.stop();
					}
					Tests.printTimes(times);
					//found.toTable().export("HYBRID.csv");
					assertEquals(expected, found);
				} finally {
					Matrix.stop();
				}
			}
		}
	}
}
