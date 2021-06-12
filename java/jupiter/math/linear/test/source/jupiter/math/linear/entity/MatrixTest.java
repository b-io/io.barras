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
package jupiter.math.linear.entity;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;
import static jupiter.hardware.gpu.OpenCL.CL;

import java.io.FileNotFoundException;
import java.io.IOException;

import jupiter.common.math.Statistics;
import jupiter.common.struct.list.ExtendedList;
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

	protected static final boolean SAVE = false;

	public MatrixTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Matrix#setColumn}.
	 */
	public void testSetColumn() {
		IO.test(BULLET, " setColumn");

		// Initialize
		final Matrix matrix = Matrix.magic(5);

		// Verify the method
		matrix.setColumn(0, new ExtendedList<Double>(0., 1., 2., 3., 4.));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link Matrix#multiply}.
	 */
	public void testMultiply() {
		IO.test(BULLET, " multiply");

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

		// Test the matrix multiplication
		int rowIndex = 0;
		for (final int m : rowCounts) {
			int columnIndex = 0;
			for (final int n : columnCounts) {
				IO.test(m, " rows and ", n, " columns");

				final Matrix A = new Matrix(m, Doubles.createSequence(m * n));
				final Matrix B = A.transpose();
				final Chronometer chrono = new Chronometer();
				final double[] times = new double[testCount];

				// • Test the normal version
				IO.test("- Normal:");
				Matrix expected = null;
				for (int ti = 0; ti < testCount; ++ti) {
					chrono.start();
					expected = A.times(B).toMatrix();
					chrono.stop();
					times[ti] = chrono.getMilliseconds();
				}
				Tests.printTimes(times);
				normalStats.set(rowIndex, columnIndex, Statistics.mean(times));
				//expected.toTable().save("normal.values.csv");

				// • Test the parallel version
				IO.test("- Parallel:");
				Matrix.parallelize();
				if (OpenCL.IS_ACTIVE) {
					CL.setActive(false);
				}
				try {
					Matrix found = null;
					for (int ti = 0; ti < testCount; ++ti) {
						chrono.start();
						found = A.times(B).toMatrix();
						chrono.stop();
						times[ti] = chrono.getMilliseconds();
					}
					//found.toTable().save("parallel.values.csv");

					// Report the statistics
					Tests.printTimes(times);
					parallelStats.set(rowIndex, columnIndex, Statistics.mean(times));

					// Verify the result
					assertEquals(expected, found);
				} finally {
					Matrix.unparallelize();
				}

				// • Test the GPU version
				if (OpenCL.IS_ACTIVE) {
					IO.test("- GPU:");
					CL.setActive(true);
					try {
						Matrix found = null;
						for (int ti = 0; ti < testCount; ++ti) {
							chrono.start();
							found = A.times(B).toMatrix();
							chrono.stop();
							times[ti] = chrono.getMilliseconds();
						}
						//found.toTable().save("gpu.values.csv");

						// Report the statistics
						Tests.printTimes(times);
						gpuStats.set(rowIndex, columnIndex, Statistics.mean(times));

						// Verify the result
						assertEquals(expected, found);
					} finally {
						CL.setActive(false);
					}
				}

				// • Test the hybrid version
				IO.test("- Hybrid:");
				Matrix.parallelize();
				if (OpenCL.IS_ACTIVE) {
					CL.setActive(true);
				}
				try {
					Matrix found = null;
					for (int ti = 0; ti < testCount; ++ti) {
						chrono.start();
						found = A.times(B).toMatrix();
						chrono.stop();
						times[ti] = chrono.getMilliseconds();
					}
					//found.toTable().save("hybrid.values.csv");

					// Report the statistics
					Tests.printTimes(times);
					hybridStats.set(rowIndex, columnIndex, Statistics.mean(times));

					// Verify the result
					assertEquals(expected, found);
				} finally {
					Matrix.unparallelize();
					if (OpenCL.IS_ACTIVE) {
						CL.setActive(false);
					}
				}

				++columnIndex;
			}
			++rowIndex;
		}

		// Export the statistics
		if (SAVE) {
			try {
				normalStats.save("normal.stats.csv", true);
				parallelStats.save("parallel.stats.csv", true);
				gpuStats.save("gpu.stats.csv", true);
				hybridStats.save("hybrid.stats.csv", true);
			} catch (final FileNotFoundException ex) {
				IO.error(ex, "Cannot open or create the CSV file");
			} catch (final IOException ex) {
				IO.error(ex, "Cannot write to the CSV file");
			}
		}
	}

	/**
	 * Tests {@link Matrix#division}.
	 */
	public void testDivision() {
		IO.test(BULLET, " division");

		// Initialize
		final Matrix A = Matrix.random(10);

		// Verify the matrix division
		assertEquals(A, A.times(A).division(A));
	}

	/**
	 * Tests {@link Matrix#forward}.
	 */
	public void testForward() {
		IO.test(BULLET, " forward");

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

		// Test the matrix forward computation
		int rowIndex = 0;
		for (final int m : rowCounts) {
			int columnIndex = 0;
			for (final int n : columnCounts) {
				IO.test(m, " rows and ", n, " columns");

				final Matrix A = new Matrix(m, Doubles.createSequence(m * n));
				final Matrix B = A.transpose();
				final Matrix C = new Vector(Doubles.createSequence(m));
				final Chronometer chrono = new Chronometer();
				final double[] times = new double[testCount];

				// • Test the normal version
				IO.test("- Normal:");
				Matrix expected = null;
				for (int ti = 0; ti < testCount; ++ti) {
					chrono.start();
					expected = A.forward(B, C).toMatrix();
					chrono.stop();
					times[ti] = chrono.getMilliseconds();
				}
				Tests.printTimes(times);
				normalStats.set(rowIndex, columnIndex, Statistics.mean(times));
				//expected.toTable().save("normal.values.csv");

				// • Test the parallel version
				IO.test("- Parallel:");
				Matrix.parallelize();
				if (OpenCL.IS_ACTIVE) {
					CL.setActive(false);
				}
				try {
					Matrix found = null;
					for (int ti = 0; ti < testCount; ++ti) {
						chrono.start();
						found = A.forward(B, C).toMatrix();
						chrono.stop();
						times[ti] = chrono.getMilliseconds();
					}
					Tests.printTimes(times);
					parallelStats.set(rowIndex, columnIndex, Statistics.mean(times));
					//found.toTable().save("parallel.values.csv");
					assertEquals(expected, found);
				} finally {
					Matrix.unparallelize();
				}

				// • Test the GPU version
				if (OpenCL.IS_ACTIVE) {
					IO.test("- GPU:");
					CL.setActive(true);
					try {
						Matrix found = null;
						for (int ti = 0; ti < testCount; ++ti) {
							chrono.start();
							found = A.forward(B, C).toMatrix();
							chrono.stop();
							times[ti] = chrono.getMilliseconds();
						}
						Tests.printTimes(times);
						//found.toTable().save("gpu.values.csv");
						gpuStats.set(rowIndex, columnIndex, Statistics.mean(times));
						assertEquals(expected, found);
					} finally {
						CL.setActive(false);
					}
				}

				// • Test the hybrid version
				IO.test("- Hybrid:");
				Matrix.parallelize();
				if (OpenCL.IS_ACTIVE) {
					CL.setActive(true);
				}
				try {
					Matrix found = null;
					for (int ti = 0; ti < testCount; ++ti) {
						chrono.start();
						found = A.forward(B, C).toMatrix();
						chrono.stop();
						times[ti] = chrono.getMilliseconds();
					}
					Tests.printTimes(times);
					hybridStats.set(rowIndex, columnIndex, Statistics.mean(times));
					//found.toTable().save("hybrid.values.csv");
					assertEquals(expected, found);
				} finally {
					Matrix.unparallelize();
					if (OpenCL.IS_ACTIVE) {
						CL.setActive(false);
					}
				}

				++columnIndex;
			}
			++rowIndex;
		}

		// Export the statistics
		if (SAVE) {
			try {
				normalStats.save("normal.stats.csv", true);
				parallelStats.save("parallel.stats.csv", true);
				gpuStats.save("gpu.stats.csv", true);
				hybridStats.save("hybrid.stats.csv", true);
			} catch (final FileNotFoundException ex) {
				IO.error(ex, "Cannot open or create the CSV file");
			} catch (final IOException ex) {
				IO.error(ex, "Cannot write to the CSV file");
			}
		}
	}
}
