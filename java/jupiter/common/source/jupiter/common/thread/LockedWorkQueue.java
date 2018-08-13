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

public class LockedWorkQueue<I, O>
		implements IWorkQueue<I, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile int MIN_THREADS = 2;
	public static volatile int MAX_THREADS = Runtime.getRuntime().availableProcessors();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	// Threads
	protected volatile boolean isRunning = true;
	// Workers
	protected final Worker<I, O> model;
	protected final Stack<Worker<I, O>> workers = new Stack<Worker<I, O>>();
	protected final Lock workersLock = new ReentrantLock();
	protected volatile int nWorkers = 0;
	protected volatile int nReservedWorkers = 0;
	// Tasks
	protected final LinkedList<Pair<Long, I>> tasks = new LinkedList<Pair<Long, I>>();
	protected final Lock tasksLock = new ReentrantLock();
	protected final Condition tasksLockCondition = tasksLock.newCondition();
	protected volatile long currentId = 0L;
	// Results
	protected final Map<Long, O> results = new HashMap<Long, O>(Arrays.DEFAULT_CAPACITY);
	protected final Lock resultsLock = new ReentrantLock();
	protected final Condition resultsLockCondition = resultsLock.newCondition();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public LockedWorkQueue(final Worker<I, O> model) {
		this.model = model;
		initWorkers();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WORKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the working threads.
	 */
	public void initWorkers() {
		createWorkers(MIN_THREADS);
	}

	/**
	 * Instantiates n working threads according to the model.
	 * <p>
	 * @param n the number of working threads to create
	 * <p>
	 * @return the number of created working threads
	 */
	protected int createWorkers(final int n) {
		int nCreatedWorkers = 0;
		try {
			for (int i = 0; i < n; ++i) {
				nCreatedWorkers += createWorker();
			}
		} catch (final Exception ex) {
			IO.error(ex);
		}
		return nCreatedWorkers;
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
		int nCreatedWorkers = 0;
		workersLock.lock();
		try {
			IO.debug("Create the thread ", nWorkers + 1);
			if (nWorkers >= MAX_THREADS) {
				throw new IllegalOperationException(
						"The maximum number of threads (" + MAX_THREADS + ") has been reached");
			}
			final Worker<I, O> worker = model.clone();
			worker.setWorkQueue(this);
			workers.push(worker);
			++nWorkers;
			++nCreatedWorkers;
			worker.start();
		} finally {
			workersLock.unlock();
		}
		return nCreatedWorkers;
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
			IO.debug("Try to reserve ", n, " threads");
			if (MAX_THREADS - nReservedWorkers >= n) {
				nReservedWorkers += n;
				// Create more workers if required
				final int nWorkersToCreate = nReservedWorkers - nWorkers;
				if (nWorkersToCreate > 0) {
					IO.debug("OK, create ", nWorkersToCreate, " more workers (total reserved: ",
							nReservedWorkers, ")");
					return createWorkers(nWorkersToCreate) == nWorkersToCreate;
				}
				IO.debug("OK, the workers are already created (total reserved: ", nReservedWorkers,
						")");
				return true;
			}
			IO.debug("No workers available (total reserved: ", nReservedWorkers, ")");
		} finally {
			workersLock.unlock();
		}
		return false;
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
			++currentId;
			IO.debug("Add the task ", currentId);
			tasks.add(new Pair<Long, I>(currentId, input));
			tasksLockCondition.signal();
			return currentId;
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
				tasksLockCondition.await();
			}
			if (isRunning) {
				return tasks.removeFirst();
			}
		} catch (final InterruptedException ignored) {
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
			--nReservedWorkers;
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
		tasksLock.lock();
		try {
			IO.debug("Shutdown the thread pool");
			isRunning = false;
			tasksLockCondition.signalAll();
		} finally {
			tasksLock.unlock();
		}
	}
}
