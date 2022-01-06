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
package jupiter.common.thread;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.test.Tests;

public class LockedWorkQueueTest
		extends WorkQueueTest {

	public LockedWorkQueueTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests {@link LockedWorkQueue#addTask}.
	 */
	public void testAddTask() {
		IO.test(BULLET, "addTask");

		// Initialize
		final int testCount = 100;
		final double[] testTimes = new double[testCount];
		final double[] threadCounts = new double[testCount];

		// Test the locked work queue
		final LockedWorkQueue<Integer, Integer> workQueue = new LockedWorkQueue<Integer, Integer>(
				new SimpleWorker());
		for (int ti = 0; ti < testCount; ++ti) {
			testTimes[ti] = test(workQueue);
			threadCounts[ti] = workQueue.getWorkerCount();
			workQueue.restart();
		}
		Tests.printTimes(testTimes);
		Tests.printValues("number of threads", threadCounts);
	}
}
