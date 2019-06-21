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
package jupiter.math.linear.entity;

import static jupiter.common.io.IO.IO;
import static jupiter.hardware.gpu.OpenCL.CL;

import jupiter.common.math.Statistics;
import jupiter.common.struct.table.DoubleTable;
import jupiter.common.test.Test;
import jupiter.common.test.Tests;
import jupiter.common.time.Chronometer;
import jupiter.common.util.Doubles;
import jupiter.common.util.Integers;
import jupiter.common.util.Strings;
import jupiter.hardware.gpu.OpenCL;

public class MatrixTest
		extends Test {

	public MatrixTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of dot method, of class Matrix.
	 */
	public void testDot() {
		IO.test("dot");

		// Initialize
		final int testCount = 10;
		final int[] rowCounts = {10, 50, 100, 200, 300};
		final int[] columnCounts = {10, 50, 100, 200, 300};
		final String[] header = Strings.toArray(Integers.toArray(columnCounts));
		final DoubleTable normalStats = new DoubleTable(header, rowCounts.length,
				columnCounts.length);
		final DoubleTable parallelStats = new DoubleTable(header, rowCounts.length,
				columnCounts.length);
		final DoubleTable gpuStats = new DoubleTable(header, rowCounts.length, columnCounts.length);
		final DoubleTable hybridStats = new DoubleTable(header, rowCounts.length,
				columnCounts.length);

		// Test
		int i = 0;
		for (final int m : rowCounts) {
			int j = 0;
			for (final int n : columnCounts) {
				IO.test(m, " rows and ", n, " columns");

				final Matrix A = new Matrix(m, Doubles.createSequence(m * n));
				final Matrix B = A.transpose();
				final Chronometer chrono = new Chronometer();
				final double[] times = new double[testCount];

				// Test the normal version
				IO.test("- Normal:");
				Matrix expected = null;
				for (int t = 0; t < testCount; ++t) {
					chrono.start();
					expected = A.times(B).toMatrix();
					chrono.stop();
					times[t] = chrono.getMilliseconds();
				}
				Tests.printTimes(times);
				normalStats.set(i, j, Statistics.mean(times));
				//expected.toTable().export("normal.values.csv");

				// Test the parallel version
				IO.test("- Parallel:");
				Matrix.parallelize();
				if (OpenCL.IS_ACTIVE) {
					CL.setActive(false);
				}
				try {
					Matrix found = null;
					for (int t = 0; t < testCount; ++t) {
						chrono.start();
						found = A.times(B).toMatrix();
						chrono.stop();
						times[t] = chrono.getMilliseconds();
					}
					Tests.printTimes(times);
					parallelStats.set(i, j, Statistics.mean(times));
					//found.toTable().export("parallel.values.csv");
					assertEquals(expected, found);
				} finally {
					Matrix.unparallelize();
				}

				// Test the GPU version
				if (OpenCL.IS_ACTIVE) {
					IO.test("- GPU:");
					CL.setActive(true);
					try {
						Matrix found = null;
						for (int t = 0; t < testCount; ++t) {
							chrono.start();
							found = A.times(B).toMatrix();
							chrono.stop();
							times[t] = chrono.getMilliseconds();
						}
						Tests.printTimes(times);
						//found.toTable().export("gpu.values.csv");
						gpuStats.set(i, j, Statistics.mean(times));
						assertEquals(expected, found);
					} finally {
						CL.setActive(false);
					}
				}

				// Test the hybrid version
				IO.test("- Hybrid:");
				Matrix.parallelize();
				if (OpenCL.IS_ACTIVE) {
					CL.setActive(true);
				}
				try {
					Matrix found = null;
					for (int t = 0; t < testCount; ++t) {
						chrono.start();
						found = A.times(B).toMatrix();
						chrono.stop();
						times[t] = chrono.getMilliseconds();
					}
					Tests.printTimes(times);
					hybridStats.set(i, j, Statistics.mean(times));
					//found.toTable().export("hybrid.values.csv");
					assertEquals(expected, found);
				} finally {
					Matrix.unparallelize();
					if (OpenCL.IS_ACTIVE) {
						CL.setActive(false);
					}
				}

				++j;
			}
			++i;
		}

		// Export the statistics
		final boolean export = false;
		if (export) {
			normalStats.export("normal.stats.csv");
			parallelStats.export("parallel.stats.csv");
			gpuStats.export("gpu.stats.csv");
			hybridStats.export("hybrid.stats.csv");
		}
	}

	/**
	 * Test of division method, of class Matrix.
	 */
	public void testDivision() {
		IO.test("division");

		// Initialize
		final int size = 10;
		final Matrix A = Matrix.random(size);

		// Test
		assertTrue(A.equals(A.times(A).division(A)));
	}

	/**
	 * Test of forward method, of class Matrix.
	 */
	public void testForward() {
		IO.test("forward");

		// Initialize
		final int testCount = 10;
		final int[] rowCounts = {10, 50, 100, 200};
		final int[] columnCounts = {10, 50, 100, 200};
		final String[] header = Strings.toArray(Integers.toArray(columnCounts));
		final DoubleTable normalStats = new DoubleTable(header, rowCounts.length,
				columnCounts.length);
		final DoubleTable parallelStats = new DoubleTable(header, rowCounts.length,
				columnCounts.length);
		final DoubleTable gpuStats = new DoubleTable(header, rowCounts.length, columnCounts.length);
		final DoubleTable hybridStats = new DoubleTable(header, rowCounts.length,
				columnCounts.length);

		// Test
		int i = 0;
		for (final int m : rowCounts) {
			int j = 0;
			for (final int n : columnCounts) {
				IO.test(m, " rows and ", n, " columns");

				final Matrix A = new Matrix(m, Doubles.createSequence(m * n));
				final Matrix B = A.transpose();
				final Matrix C = new Vector(Doubles.createSequence(m));
				final Chronometer chrono = new Chronometer();
				final double[] times = new double[testCount];

				// Test the normal version
				IO.test("- Normal:");
				Matrix expected = null;
				for (int t = 0; t < testCount; ++t) {
					chrono.start();
					expected = A.forward(B, C).toMatrix();
					chrono.stop();
					times[t] = chrono.getMilliseconds();
				}
				Tests.printTimes(times);
				normalStats.set(i, j, Statistics.mean(times));
				//expected.toTable().export("normal.values.csv");

				// Test the parallel version
				IO.test("- Parallel:");
				Matrix.parallelize();
				if (OpenCL.IS_ACTIVE) {
					CL.setActive(false);
				}
				try {
					Matrix found = null;
					for (int t = 0; t < testCount; ++t) {
						chrono.start();
						found = A.forward(B, C).toMatrix();
						chrono.stop();
						times[t] = chrono.getMilliseconds();
					}
					Tests.printTimes(times);
					parallelStats.set(i, j, Statistics.mean(times));
					//found.toTable().export("parallel.values.csv");
					assertEquals(expected, found);
				} finally {
					Matrix.unparallelize();
				}

				// Test the GPU version
				if (OpenCL.IS_ACTIVE) {
					IO.test("- GPU:");
					CL.setActive(true);
					try {
						Matrix found = null;
						for (int t = 0; t < testCount; ++t) {
							chrono.start();
							found = A.forward(B, C).toMatrix();
							chrono.stop();
							times[t] = chrono.getMilliseconds();
						}
						Tests.printTimes(times);
						//found.toTable().export("gpu.values.csv");
						gpuStats.set(i, j, Statistics.mean(times));
						assertEquals(expected, found);
					} finally {
						CL.setActive(false);
					}
				}

				// Test the hybrid version
				IO.test("- Hybrid:");
				Matrix.parallelize();
				if (OpenCL.IS_ACTIVE) {
					CL.setActive(true);
				}
				try {
					Matrix found = null;
					for (int t = 0; t < testCount; ++t) {
						chrono.start();
						found = A.forward(B, C).toMatrix();
						chrono.stop();
						times[t] = chrono.getMilliseconds();
					}
					Tests.printTimes(times);
					hybridStats.set(i, j, Statistics.mean(times));
					//found.toTable().export("hybrid.values.csv");
					assertEquals(expected, found);
				} finally {
					Matrix.unparallelize();
					if (OpenCL.IS_ACTIVE) {
						CL.setActive(false);
					}
				}

				++j;
			}
			++i;
		}

		// Export the statistics
		final boolean export = false;
		if (export) {
			normalStats.export("normal.stats.csv");
			parallelStats.export("parallel.stats.csv");
			gpuStats.export("gpu.stats.csv");
			hybridStats.export("hybrid.stats.csv");
		}
	}
}
