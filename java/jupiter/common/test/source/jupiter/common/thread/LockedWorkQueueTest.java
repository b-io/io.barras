/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.io.IO.IO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jupiter.common.test.Test;
import jupiter.common.time.Chronometer;

public class LockedWorkQueueTest
		extends Test {

	public LockedWorkQueueTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of addTask method, of class FairWorkQueue.
	 */
	public void testAddTask() {
		IO.test("addTask");

		// Initialize
		final int taskCount = 100000;
		final Chronometer chrono = new Chronometer();

		// Create a work queue
		final IWorkQueue<Integer, Integer> workQueue = new FairWorkQueue<Integer, Integer>(
				new SimpleWorker());
		workQueue.reserveWorkers(FairWorkQueue.MAX_THREADS);
		IO.test("There are ", FairWorkQueue.MAX_THREADS, " working threads");

		// Process the tasks
		chrono.start();
		final List<Long> ids = new ArrayList<Long>(taskCount);
		for (int i = 0; i < taskCount; ++i) {
			ids.add(workQueue.submit(i));
		}
		final Set<Integer> results = new HashSet<Integer>(ids.size());
		for (final long id : ids) {
			results.add(workQueue.get(id));
		}
		chrono.stop();
		IO.test(chrono.getMilliseconds(), " [ms]");

		// Test
		for (int i = 0; i < taskCount; ++i) {
			assertTrue(results.contains(i));
		}
	}
}
