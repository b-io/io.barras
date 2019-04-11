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
package jupiter.common.thread;

import static jupiter.common.io.IO.IO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.util.Arrays;

public class WorkQueue<I, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile int DEFAULT_MIN_THREADS = Runtime.getRuntime().availableProcessors();
	public static volatile int DEFAULT_MAX_THREADS = 2 * DEFAULT_MIN_THREADS;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	public volatile int minThreads;
	public volatile int maxThreads;

	/**
	 * The flag specifying whether {@code this} is running.
	 */
	protected volatile boolean isRunning = true;

	/**
	 * The worker model.
	 */
	protected final Worker<I, O> model;
	/**
	 * The worker type.
	 */
	protected final Class<?> type;
	/**
	 * The workers.
	 */
	protected final Stack<Worker<I, O>> workers = new Stack<Worker<I, O>>();
	protected volatile int workerCount = 0;
	protected volatile int availableWorkerCount = 0;
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

	/**
	 * Constructs a {@code WorkQueue} with the specified model of the workers to handle.
	 * <p>
	 * @param model the model of the workers to handle
	 */
	protected WorkQueue(final Worker<I, O> model) {
		this(model, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
	}

	/**
	 * Constructs a {@code WorkQueue} with the specified model and the specified minimum and maximum
	 * number of workers to handle.
	 * <p>
	 * @param model      the model of the workers to handle
	 * @param minThreads the minimum number of the workers to handle
	 * @param maxThreads the maximum number of the workers to handle
	 */
	protected WorkQueue(final Worker<I, O> model, final int minThreads, final int maxThreads) {
		this.model = model;
		this.type = model.getClass();
		this.minThreads = minThreads;
		this.maxThreads = maxThreads;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WORKER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the type of the workers to handle.
	 * <p>
	 * @return the type of the workers to handle
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * Returns the number of workers.
	 * <p>
	 * @return the number of workers
	 */
	public int getWorkerCount() {
		return workerCount;
	}

	/**
	 * Returns the number of available workers.
	 * <p>
	 * @return the number of available workers
	 */
	public int getAvailableWorkerCount() {
		return availableWorkerCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Instantiates n workers according to the model.
	 * <p>
	 * @param n the number of workers to create
	 * <p>
	 * @return the number of created workers
	 */
	public int createWorkers(final int n) {
		int createdWorkerCount = 0;
		try {
			for (int i = 0; i < n; ++i) {
				createdWorkerCount += createWorker();
			}
		} catch (final IllegalOperationException ex) {
			IO.error(ex);
		}
		return createdWorkerCount;
	}

	/**
	 * Instantiates n workers according to the model if required.
	 * <p>
	 * @param n the number of workers to create if required
	 * <p>
	 * @return the number of created workers
	 */
	public int createAvailableWorkers(final int n) {
		final int workerToCreateCount = n - availableWorkerCount;
		if (workerToCreateCount <= 0) {
			return 0;
		}
		return createWorkers(Math.min(workerToCreateCount, maxThreads - workerCount));
	}

	/**
	 * Instantiates a worker according to the model.
	 * <p>
	 * @return the number of created workers
	 * <p>
	 * @throws IllegalOperationException if the maximum number of workers has been reached
	 */
	protected int createWorker()
			throws IllegalOperationException {
		int createdWorkerCount = 0;
		if (workerCount >= maxThreads) {
			throw new IllegalOperationException(
					"The maximum number of workers (" + maxThreads + ") has been reached");
		}
		final Worker<I, O> worker = model.clone();
		worker.setWorkQueue(this);
		workers.push(worker);
		++workerCount;
		++availableWorkerCount;
		++createdWorkerCount;
		IO.debug(workerCount, ") Start the worker ", worker);
		worker.start();
		return createdWorkerCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean reserveWorkers(final int n) {
		IO.debug("Try to reserve ", n, " workers of type ", type);
		if (maxThreads - reservedWorkerCount >= n) {
			// Reserve the workers
			reservedWorkerCount += n;
			// Create more workers if required
			final int workerToCreateCount = reservedWorkerCount - workerCount;
			if (workerToCreateCount > 0) {
				IO.debug("OK, create ", workerToCreateCount, " more workers of type ", type,
						" (total reserved: ", reservedWorkerCount, "/", maxThreads, ")");
				return createWorkers(workerToCreateCount) == workerToCreateCount;
			} else {
				IO.debug("OK, the workers of type ", type, " are already created (total reserved: ",
						reservedWorkerCount, "/", maxThreads, ")");
			}
			return true;
		}
		IO.debug("Cannot reserve ", n, " workers of type ", type, " (total reserved: ",
				reservedWorkerCount, "/", maxThreads, ")");
		return false;
	}

	/**
	 * Frees n workers.
	 * <p>
	 * @param n the number of workers to free
	 */
	public void freeWorkers(final int n) {
		reservedWorkerCount -= n;
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
		++currentTaskId;
		IO.debug("Add task ", currentTaskId);
		tasks.add(new Pair<Long, I>(currentTaskId, input));
		return currentTaskId;
	}

	/**
	 * Returns the next task if {@code this} is running, or {@code null} otherwise.
	 * <p>
	 * @return the next task if {@code this} is running, or {@code null} otherwise
	 */
	public Pair<Long, I> getNextTask() {
		if (isRunning) {
			IO.debug("Get the next task");
			--availableWorkerCount;
			return tasks.removeFirst();
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
		IO.debug("Add the result of task ", id);
		results.put(id, result);
		++availableWorkerCount;
	}

	/**
	 * Returns the result of the task with the specified identifier.
	 * <p>
	 * @param id the identifier of the task
	 * <p>
	 * @return the result of the task with the specified identifier
	 */
	public O get(final long id) {
		IO.debug("Get the result of task ", id);
		return results.remove(id);
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
		IO.debug("Shutdown the work queue ", this);
		isRunning = false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return getClass().getSimpleName() + " of type " + type.getSimpleName();
	}
}
