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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.util.Collections;
import jupiter.common.util.Objects;

public class WorkQueue<I, O>
		implements ICloneable<WorkQueue<I, O>>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The default minimum number of {@link Worker} to handle.
	 */
	public static volatile int DEFAULT_MIN_THREADS = Runtime.getRuntime().availableProcessors();
	/**
	 * The default maximum number of {@link Worker} to handle.
	 */
	public static volatile int DEFAULT_MAX_THREADS = 2 * DEFAULT_MIN_THREADS;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The minimum number of {@link Worker} to handle.
	 */
	public volatile int minThreads;
	/**
	 * The maximum number of {@link Worker} to handle.
	 */
	public volatile int maxThreads;

	/**
	 * The flag specifying whether {@code this} is running.
	 */
	protected volatile boolean isRunning = true;

	/**
	 * The model {@link Worker} of type {@code I} and {@code O}.
	 */
	protected final Worker<I, O> model;
	/**
	 * The {@link Class} of the model {@link Worker}.
	 */
	protected final Class<?> c;
	/**
	 * The {@link Stack} of {@link Worker}.
	 */
	protected final Stack<Worker<I, O>> workers = new Stack<Worker<I, O>>();
	/**
	 * The number of {@link Worker}.
	 */
	protected volatile int workerCount = 0;
	/**
	 * The number of available {@link Worker}.
	 */
	protected volatile int availableWorkerCount = 0;
	/**
	 * The number of reserved {@link Worker}.
	 */
	protected volatile int reservedWorkerCount = 0;

	/**
	 * The {@link ExtendedLinkedList} of {@link Task} of type {@code I}.
	 */
	protected final ExtendedLinkedList<Task<I>> tasks = new ExtendedLinkedList<Task<I>>();
	/**
	 * The current {@link Task} identifier.
	 */
	protected volatile long currentTaskId = 0L;

	/**
	 * The {@link Map} containing the {@code O} results.
	 */
	protected final Map<Long, O> results = new HashMap<Long, O>(Collections.DEFAULT_CAPACITY);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link WorkQueue} with the specified model {@link Worker}.
	 * <p>
	 * @param model the model {@link Worker} of type {@code I} and {@code O}
	 */
	protected WorkQueue(final Worker<I, O> model) {
		this(model, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
	}

	/**
	 * Constructs a {@link WorkQueue} with the specified model {@link Worker} and minimum and
	 * maximum numbers of {@link Worker}.
	 * <p>
	 * @param model      the model {@link Worker} of type {@code I} and {@code O}
	 * @param minThreads the minimum number of {@link Worker} to handle
	 * @param maxThreads the maximum number of {@link Worker} to handle
	 */
	protected WorkQueue(final Worker<I, O> model, final int minThreads, final int maxThreads) {
		this.model = model;
		this.c = model.getClass();
		this.minThreads = minThreads;
		this.maxThreads = maxThreads;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WORKER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Class} of the model {@link Worker}.
	 * <p>
	 * @return the {@link Class} of the model {@link Worker}
	 */
	public Class<?> getType() {
		return c;
	}

	/**
	 * Returns the number of {@link Worker}.
	 * <p>
	 * @return the number of {@link Worker}
	 */
	public int getWorkerCount() {
		return workerCount;
	}

	/**
	 * Returns the number of available {@link Worker}.
	 * <p>
	 * @return the number of available {@link Worker}
	 */
	public int getAvailableWorkerCount() {
		return availableWorkerCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates the specified number of {@link Worker} according to the model.
	 * <p>
	 * @param n the number of {@link Worker} to create
	 * <p>
	 * @return the number of created {@link Worker}
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
	 * Creates the specified number of {@link Worker} according to the model if required.
	 * <p>
	 * @param n the number of {@link Worker} to create if required
	 * <p>
	 * @return the number of created {@link Worker}
	 */
	public int createAvailableWorkers(final int n) {
		final int workerToCreateCount = n - (availableWorkerCount - tasks.size());
		if (workerToCreateCount <= 0) {
			return 0;
		}
		return createWorkers(Math.min(workerToCreateCount, maxThreads - workerCount));
	}

	/**
	 * Creates a {@link Worker} according to the model.
	 * <p>
	 * @return {@code 1} if the {@link Worker} is created, {@code 0} otherwise
	 * <p>
	 * @throws IllegalOperationException if the maximum number of {@link Worker} has been reached
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

	//////////////////////////////////////////////

	/**
	 * Removes the specified {@link Worker}.
	 * <p>
	 * @param worker the {@link Worker} of type {@code I} and {@code O} to remove
	 */
	public void removeWorker(final Worker<I, O> worker) {
		workers.remove(worker);
		--workerCount;
		--availableWorkerCount;
	}

	/**
	 * Removes all the specified {@link Worker}.
	 */
	public void removeAllWorkers() {
		workers.clear();
		workerCount = 0;
		availableWorkerCount = 0;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reserves the specified number of {@link Worker}.
	 * <p>
	 * @param n the number of {@link Worker} to reserve
	 * <p>
	 * @return {@code true} if the {@link Worker} are reserved, {@code false} otherwise
	 */
	public boolean reserveWorkers(final int n) {
		IO.debug("Try to reserve ", n, " workers of type ", c);
		if (maxThreads - reservedWorkerCount >= n) {
			// Reserve the workers
			reservedWorkerCount += n;
			// Create more workers if required
			final int workerToCreateCount = reservedWorkerCount - workerCount;
			if (workerToCreateCount > 0) {
				IO.debug("OK, create ", workerToCreateCount, " more workers of type ", c,
						" (total reserved: ", reservedWorkerCount, "/", maxThreads, ")");
				return createWorkers(workerToCreateCount) == workerToCreateCount;
			} else {
				IO.debug("OK, the workers of type ", c, " are already created (total reserved: ",
						reservedWorkerCount, "/", maxThreads, ")");
			}
			return true;
		}
		IO.debug("Cannot reserve ", n, " workers of type ", c, " (total reserved: ",
				reservedWorkerCount, "/", maxThreads, ")");
		return false;
	}

	//////////////////////////////////////////////

	/**
	 * Frees the specified number of {@link Worker}.
	 * <p>
	 * @param n the number of {@link Worker} to free
	 */
	public void freeWorkers(final int n) {
		reservedWorkerCount -= n;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Kills the specified {@link Worker}.
	 * <p>
	 * @param worker the {@link Worker} of type {@code I} and {@code O} to kill
	 */
	public void killWorker(final Worker<I, O> worker) {
		worker.stop();
		removeWorker(worker);
	}

	/**
	 * Kills all the {@link Worker}.
	 */
	public void killAllWorkers() {
		Threads.sleep();
		for (final Worker<I, O> worker : workers) {
			worker.stop();
		}
		removeAllWorkers();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// TASK
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds a {@link Task} with the specified {@code I} input and returns its identifier.
	 * <p>
	 * @param input the {@code I} input of the {@link Task} to add
	 * <p>
	 * @return the identifier of the added {@link Task}
	 */
	public long submit(final I input) {
		++currentTaskId;
		IO.debug("Add task ", currentTaskId);
		tasks.add(new Task<I>(currentTaskId, input));
		return currentTaskId;
	}

	/**
	 * Returns the next {@link Task} of type {@code I} if {@code this} is running, {@code null}
	 * otherwise.
	 * <p>
	 * @return the next {@link Task} of type {@code I} if {@code this} is running, {@code null}
	 *         otherwise
	 */
	public Task<I> getNextTask() {
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
	 * Adds the {@code O} result of the {@link Task} with the specified identifier.
	 * <p>
	 * @param id     the identifier of the {@link Task}
	 * @param result the {@code O} result of the {@link Task}
	 */
	public void addResult(final long id, final O result) {
		IO.debug("Add the result of task ", id);
		results.put(id, result);
		++availableWorkerCount;
	}

	/**
	 * Returns the {@code O} result of the {@link Task} with the specified identifier.
	 * <p>
	 * @param id the identifier of the {@link Task}
	 * <p>
	 * @return the {@code O} result of the {@link Task} with the specified identifier
	 */
	public O get(final long id) {
		IO.debug("Get the result of task ", id);
		return results.remove(id);
	}

	/**
	 * Tests whether the {@code O} result of the {@link Task} with the specified identifier is
	 * ready.
	 * <p>
	 * @param id the identifier of the {@link Task}
	 * <p>
	 * @return {@code true} if the {@code O} result of the {@link Task} with the specified
	 *         identifier is ready, {@code false} otherwise
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

	//////////////////////////////////////////////

	/**
	 * Shutdowns {@code this}.
	 */
	public void shutdown() {
		shutdown(false);
	}

	/**
	 * Shutdowns {@code this}.
	 * <p>
	 * @param force the flag specifying whether to force shutdowning
	 */
	public void shutdown(final boolean force) {
		IO.debug("Shutdown the work queue ", this);
		isRunning = false;
	}

	//////////////////////////////////////////////

	/**
	 * Restarts {@code this}.
	 */
	public void restart() {
		restart(false);
	}

	/**
	 * Restarts {@code this}.
	 * <p>
	 * @param force the flag specifying whether to force restarting
	 */
	public void restart(final boolean force) {
		shutdown(force);
		IO.debug("Restart the work queue ", this);
		isRunning = true;
		createWorkers(minThreads);
		synchronized (this) {
			notifyAll();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	@SuppressWarnings("unchecked")
	public WorkQueue<I, O> clone() {
		return new WorkQueue<I, O>(model, minThreads, maxThreads);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the type of {@code other} prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof WorkQueue)) {
			return false;
		}
		final WorkQueue<?, ?> otherWorkQueue = (WorkQueue<?, ?>) other;
		return Objects.equals(minThreads, otherWorkQueue.minThreads) &&
				Objects.equals(maxThreads, otherWorkQueue.maxThreads) &&
				Objects.equals(isRunning, otherWorkQueue.isRunning) &&
				Objects.equals(model, otherWorkQueue.model) &&
				Objects.equals(c, otherWorkQueue.c) &&
				Objects.equals(workers, otherWorkQueue.workers) &&
				Objects.equals(workerCount, otherWorkQueue.workerCount) &&
				Objects.equals(availableWorkerCount, otherWorkQueue.availableWorkerCount) &&
				Objects.equals(reservedWorkerCount, otherWorkQueue.reservedWorkerCount) &&
				Objects.equals(tasks, otherWorkQueue.tasks) &&
				Objects.equals(currentTaskId, otherWorkQueue.currentTaskId) &&
				Objects.equals(results, otherWorkQueue.results);
	}

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, minThreads, maxThreads, isRunning, model, c,
				workers, workerCount, availableWorkerCount, reservedWorkerCount, tasks,
				currentTaskId, results);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " of type " + c.getSimpleName();
	}
}
