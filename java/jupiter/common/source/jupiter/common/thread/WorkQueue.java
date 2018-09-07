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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.util.Arrays;

public class WorkQueue<I, O>
		implements IWorkQueue<I, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile int MIN_THREADS = Runtime.getRuntime().availableProcessors();
	public static volatile int MAX_THREADS = 2 * Runtime.getRuntime().availableProcessors();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The running flag.
	 */
	protected volatile boolean isRunning = true;

	/**
	 * The worker model.
	 */
	protected final Worker<I, O> model;
	/**
	 * The workers.
	 */
	protected final Stack<Worker<I, O>> workers = new Stack<Worker<I, O>>();
	protected volatile int workerCount = 0;
	protected volatile int reservedWorkerCount = 0;

	/**
	 * The tasks.
	 */
	protected final LinkedList<Pair<Long, I>> tasks = new LinkedList<Pair<Long, I>>();
	/**
	 * The current task identifier.
	 */
	protected volatile long currentTaskId = 0L;

	/**
	 * The results.
	 */
	protected final Map<Long, O> results = new HashMap<Long, O>(Arrays.DEFAULT_CAPACITY);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public WorkQueue(final Worker<I, O> model) {
		this.model = model;
		initWorkers();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WORKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of working threads.
	 * <p>
	 * @return the number of working threads
	 */
	public int getWorkerCount() {
		return workerCount;
	}

	/**
	 * Initializes the working threads.
	 */
	public void initWorkers() {
		createWorkers(MIN_THREADS);
	}

	/**
	 * Reserves n working threads.
	 * <p>
	 * @param n the number of working threads to reserve
	 * <p>
	 * @return {@code true} if the working threads are reserved, {@code false} otherwise
	 */
	public boolean reserveWorkers(final int n) {
		synchronized (workers) {
			IO.debug("Try to reserve ", n, " working threads");
			if (MAX_THREADS - reservedWorkerCount >= n) {
				reservedWorkerCount += n;
				// Create more workers if required
				final int workerToCreateCount = reservedWorkerCount - workerCount;
				if (workerToCreateCount > 0) {
					IO.debug("OK, create ", workerToCreateCount, " more workers (total reserved: ",
							reservedWorkerCount, ")");
					return createWorkers(workerToCreateCount) == workerToCreateCount;
				}
				IO.debug("OK, the workers are already created (total reserved: ",
						reservedWorkerCount, ")");
				return true;
			} else {
				IO.debug("No workers available (total reserved: ", reservedWorkerCount, ")");
			}
		}
		return false;
	}

	/**
	 * Instantiates n working threads according to the model.
	 * <p>
	 * @param n the number of working threads to create
	 * <p>
	 * @return the number of created working threads
	 */
	protected int createWorkers(final int n) {
		int createdWorkerCount = 0;
		try {
			for (int i = 0; i < n; ++i) {
				createdWorkerCount += createWorker();
			}
		} catch (final Exception ex) {
			IO.fail(ex);
		}
		return createdWorkerCount;
	}

	/**
	 * Instantiates a working thread according to the model.
	 * <p>
	 * @return the number of created working threads
	 * <p>
	 * @throws Exception if the maximum number of working threads has been reached
	 */
	protected int createWorker()
			throws Exception {
		int createdWorkerCount = 0;
		synchronized (workers) {
			IO.debug("Create the working thread ", workerCount + 1);
			if (workerCount >= MAX_THREADS) {
				throw new IllegalOperationException("The maximum number of working threads (" +
						MAX_THREADS + ") has been reached");
			}
			final Worker<I, O> worker = model.clone();
			worker.setWorkQueue(this);
			workers.push(worker);
			++workerCount;
			++createdWorkerCount;
			worker.start();
		}
		return createdWorkerCount;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// TASK
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds a task with the specified input and returns its identifier.
	 * <p>
	 * @param input the input of the task to add
	 * <p>
	 * @return the identifier of the added task
	 */
	public long submit(final I input) {
		synchronized (tasks) {
			++currentTaskId;
			IO.debug("Add the task ", currentTaskId);
			tasks.addLast(new Pair<Long, I>(currentTaskId, input));
			tasks.notifyAll();
			return currentTaskId;
		}
	}

	/**
	 * Returns the next task.
	 * <p>
	 * @return the next task
	 */
	public Pair<Long, I> getNextTask() {
		synchronized (tasks) {
			IO.debug("Get the next task");
			while (isRunning && tasks.isEmpty()) {
				try {
					tasks.wait();
				} catch (final InterruptedException ignored) {
				}
			}
			if (isRunning) {
				return tasks.removeFirst();
			}
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// RESULT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds the result of the task with the specified identifier.
	 * <p>
	 * @param id     the identifier of the task
	 * @param result the result of the task
	 */
	public void addResult(final long id, final O result) {
		synchronized (results) {
			IO.debug("Add the result of the task ", id);
			results.put(id, result);
			results.notifyAll();
		}
		synchronized (workers) {
			--reservedWorkerCount;
		}
	}

	/**
	 * Returns the result of the task with the specified identifier.
	 * <p>
	 * @param id the identifier of the task
	 * <p>
	 * @return the result of the task with the specified identifier
	 */
	public O get(final long id) {
		synchronized (results) {
			IO.debug("Get the result of the task ", id);
			while (!results.containsKey(id)) {
				try {
					results.wait();
				} catch (final InterruptedException ignored) {
				}
			}
			return results.remove(id);
		}
	}

	/**
	 * Returns {@code true} if the result of the task with the specified identifier is ready,
	 * {@code false} otherwise.
	 * <p>
	 * @param id the identifier of the task
	 * <p>
	 * @return {@code true} if the result of the task with the specified identifier is ready,
	 *         {@code false} otherwise
	 */
	public boolean isReady(final long id) {
		return results.containsKey(id);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// POOL
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if {@code this} is isRunning, {@code false} otherwise.
	 * <p>
	 * @return {@code true} if {@code this} is isRunning, {@code false} otherwise
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Shutdowns {@code this}.
	 */
	public void shutdown() {
		synchronized (tasks) {
			IO.debug("Shutdown the ", getClass().getSimpleName());
			isRunning = false;
			tasks.notifyAll();
		}
	}
}
