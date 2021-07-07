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
package jupiter.common.thread;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Characters.BULLET;

import java.util.List;
import java.util.Set;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.set.ExtendedHashSet;
import jupiter.common.test.Test;
import jupiter.common.time.Chronometer;

public class WorkQueueTest
		extends Test {

	public WorkQueueTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests the constants of {@link WorkQueue}.
	 */
	public void testConstants() {
		IO.test(BULLET, "constants");

		// Initialize
		final WorkQueue<Integer, Integer> workQueue = new WorkQueue<Integer, Integer>(
				new SimpleWorker());

		// Verify the constants of the work queue
		assertTrue(workQueue.minThreadCount <= workQueue.maxThreadCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected double test(final WorkQueue<Integer, Integer> workQueue) {
		// Initialize
		final int taskCount = 10000;
		final Chronometer chrono = new Chronometer();

		// Process the tasks
		chrono.start();
		final List<Long> ids = new ExtendedList<Long>(taskCount);
		for (int ti = 0; ti < taskCount; ++ti) {
			ids.add(workQueue.submit(ti));
		}
		final Set<Integer> results = new ExtendedHashSet<Integer>(ids.size());
		for (final long id : ids) {
			results.add(workQueue.get(id));
		}
		chrono.stop();

		// Verify the results
		for (int ti = 0; ti < taskCount; ++ti) {
			assertTrue(results.contains(ti));
		}
		return chrono.getMilliseconds();
	}
}
