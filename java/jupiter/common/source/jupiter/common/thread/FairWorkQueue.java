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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.util.Arrays;

public class FairWorkQueue<I, O>
		implements IWorkQueue<I, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile int MIN_THREADS = Runtime.getRuntime().availableProcessors();
	public static volatile int MAX_THREADS = 2 * MIN_THREADS;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether {@code this} is running.
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
	/**
	 * The internal lock of the workers.
	 */
	protected final Lock workersLock = new ReentrantLock(true);
	protected volatile int workerCount = 0;
	protected volatile int reservedWorkerCount = 0;

	/**
	 * The tasks.
	 */
	protected final LinkedList<Pair<Long, I>> tasks = new LinkedList<Pair<Long, I>>();
	/**
	 * The internal lock of the tasks.
	 */
	protected final Lock tasksLock = new ReentrantLock(true);
	protected final Condition tasksLockCondition = tasksLock.newCondition();
	/**
	 * The current task identifier.
	 */
	protected volatile long currentTaskId = 0L;

	/**
	 * The results.
	 */
	protected final Map<Long, O> results = new HashMap<Long, O>(Arrays.DEFAULT_CAPACITY);
	/**
	 * The internal lock of the results.
	 */
	protected final Lock resultsLock = new ReentrantLock(true);
	protected final Condition resultsLockCondition = resultsLock.newCondition();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public FairWorkQueue(final Worker<I, O> model) {
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
		workersLock.lock();
		try {
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
			}
			IO.debug("No workers available (total reserved: ", reservedWorkerCount, ")");
		} finally {
			workersLock.unlock();
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
			IO.error(ex);
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
		workersLock.lock();
		try {
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
		} finally {
			workersLock.unlock();
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
		tasksLock.lock();
		try {
			++currentTaskId;
			IO.debug("Add the task ", currentTaskId);
			tasks.add(new Pair<Long, I>(currentTaskId, input));
			tasksLockCondition.signal();
			return currentTaskId;
		} finally {
			tasksLock.unlock();
		}
	}

	/**
	 * Returns the next task.
	 * <p>
	 * @return the next task
	 */
	public Pair<Long, I> getNextTask() {
		tasksLock.lock();
		try {
			IO.debug("Get the next task");
			while (isRunning && tasks.isEmpty()) {
				try {
					tasksLockCondition.await();
				} catch (final InterruptedException ignored) {
				}
			}
			if (isRunning) {
				return tasks.removeFirst();
			}
		} finally {
			tasksLock.unlock();
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
		resultsLock.lock();
		try {
			IO.debug("Add the result of the task ", id);
			results.put(id, result);
			resultsLockCondition.signalAll();
		} finally {
			resultsLock.unlock();
		}
		workersLock.lock();
		try {
			--reservedWorkerCount;
		} finally {
			workersLock.unlock();
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
		resultsLock.lock();
		try {
			IO.debug("Get the result of the task ", id);
			while (!results.containsKey(id)) {
				try {
					resultsLockCondition.await();
				} catch (final InterruptedException ignored) {
				}
			}
			return results.remove(id);
		} finally {
			resultsLock.unlock();
		}
	}

	/**
	 * Tests whether the result of the task with the specified identifier is ready.
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
	 * Tests whether {@code this} is running.
	 * <p>
	 * @return {@code true} if {@code this} is running, {@code false} otherwise
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Shutdowns {@code this}.
	 */
	public void shutdown() {
		tasksLock.lock();
		try {
			IO.debug("Shutdown the ", getClass().getSimpleName());
			isRunning = false;
			tasksLockCondition.signalAll();
		} finally {
			tasksLock.unlock();
		}
	}
}
