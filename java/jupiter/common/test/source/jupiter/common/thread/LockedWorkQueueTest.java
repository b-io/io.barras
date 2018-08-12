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
package jupiter.common.thread;

import static jupiter.common.io.IO.IO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.time.Chronometer;

public class LockedWorkQueueTest
		extends TestCase {

	public LockedWorkQueueTest(final String testName) {
		super(testName);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of addTask method, of class LockedWorkQueue.
	 */
	public void testAddTask() {
		IO.test("addTask");
		IO.setSeverityLevel(SeverityLevel.TEST);

		// Create a thread pool
		final IWorkQueue<Integer, Integer> queue = new LockedWorkQueue<Integer, Integer>(
				new JobTest());
		queue.reserveWorkers(LockedWorkQueue.MAX_THREADS);
		IO.test("There are ", LockedWorkQueue.MAX_THREADS, " threads");

		// Process the tasks
		final Chronometer chrono = new Chronometer();
		chrono.start();
		final int nTasks = 1000000;
		final List<Long> ids = new ExtendedList<Long>(nTasks);
		for (int i = 0; i < nTasks; ++i) {
			ids.add(queue.submit(i));
		}
		final Set<Integer> results = new HashSet<Integer>(ids.size());
		for (final long id : ids) {
			results.add(queue.get(id));
		}
		chrono.stop();
		IO.test(chrono.getMilliseconds(), " [ms]");

		// Test
		for (int i = 0; i < nTasks; ++i) {
			assertTrue(results.contains(i));
		}
	}
}
