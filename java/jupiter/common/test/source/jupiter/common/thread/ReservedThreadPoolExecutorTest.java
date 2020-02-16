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
package jupiter.common.thread;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.BULLET;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jupiter.common.struct.list.ExtendedList;
import jupiter.common.test.Test;
import jupiter.common.time.Chronometer;

public class ReservedThreadPoolExecutorTest
		extends Test {

	public ReservedThreadPoolExecutorTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of addTask method, of class ReservedThreadPoolExecutor.
	 */
	public void testSubmit() {
		IO.test(BULLET, " submit");

		// Initialize
		final int taskCount = 100000;
		final Chronometer chrono = new Chronometer();
		final ReservedThreadPoolExecutor queue = new ReservedThreadPoolExecutor();
		IO.test("There are ", queue.getMaxPoolSize(), " workers");

		// Submit the tasks
		chrono.start();
		final List<Future<Integer>> futures = new ExtendedList<Future<Integer>>(taskCount);
		for (int i = 0; i < taskCount; ++i) {
			Future<Integer> future;
			//do {
			future = queue.submit((Callable<Integer>) new SimpleWorker(i));
			//} while (future == null);
			futures.add(future);
		}

		// Collect the results
		final Set<Integer> results = new HashSet<Integer>(futures.size());
		int skippedTaskCount = 0;
		for (final Future<Integer> future : futures) {
			try {
				if (future != null) {
					results.add(future.get());
				} else {
					++skippedTaskCount;
				}
			} catch (final ExecutionException ex) {
				IO.error(ex);
			} catch (final InterruptedException ex) {
				IO.error(ex);
			}
		}
		chrono.stop();
		IO.test(chrono.getMilliseconds(), " [ms]");
		IO.test(skippedTaskCount, " skipped tasks");

		// Report the number of completed tasks
		int completedTaskCount = 0;
		for (int i = 0; i < taskCount; ++i) {
			if (results.contains(i)) {
				++completedTaskCount;
			}
		}
		IO.test(completedTaskCount, "/", taskCount, " completed");
	}
}
