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

import java.io.Serializable;
import java.util.Map;
import java.util.Stack;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.map.hash.ExtendedHashMap;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Classes;
import jupiter.common.util.Maps;
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default minimum number of {@link Worker} to handle.
	 */
	public static final int DEFAULT_MIN_THREADS = Runtime.getRuntime().availableProcessors();
	/**
	 * The default maximum number of {@link Worker} to handle.
	 */
	public static final int DEFAULT_MAX_THREADS = 2 * DEFAULT_MIN_THREADS;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The minimum number of {@link Worker} to handle.
	 */
	public volatile int minThreadCount;
	/**
	 * The maximum number of {@link Worker} to handle.
	 */
	public volatile int maxThreadCount;

	/**
	 * The flag specifying whether {@code this} is running.
	 */
	protected volatile boolean isRunning = true;

	/**
	 * The model {@link Worker} of {@code I} and {@code O} types.
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
	 * The {@link ExtendedLinkedList} of {@link Task} of {@code I} type.
	 */
	protected final ExtendedLinkedList<Task<I>> tasks = new ExtendedLinkedList<Task<I>>();
	/**
	 * The current {@link Task} identifier.
	 */
	protected volatile long currentTaskId = 0L;

	/**
	 * The {@link Map} containing the {@code O} results.
	 */
	protected final Map<Long, O> results = new ExtendedHashMap<Long, O>(Maps.DEFAULT_CAPACITY);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link WorkQueue} with the specified model {@link Worker} by default.
	 * <p>
	 * @param model the model {@link Worker} of {@code I} and {@code O} types
	 */
	protected WorkQueue(final Worker<I, O> model) {
		this(model, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
	}

	/**
	 * Constructs a {@link WorkQueue} with the specified model {@link Worker} and minimum and
	 * maximum numbers of {@link Worker}.
	 * <p>
	 * @param model          the model {@link Worker} of {@code I} and {@code O} types
	 * @param minThreadCount the minimum number of {@link Worker} to handle
	 * @param maxThreadCount the maximum number of {@link Worker} to handle
	 */
	protected WorkQueue(final Worker<I, O> model, final int minThreadCount,
			final int maxThreadCount) {
		this.model = model;
		c = Classes.get(model);
		this.minThreadCount = minThreadCount;
		this.maxThreadCount = maxThreadCount;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WORKER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Class} of the model {@link Worker}.
	 * <p>
	 * @return the {@link Class} of the model {@link Worker}
	 */
	public Class<?> getModelClass() {
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
	 * @param workerToCreateCount the number of {@link Worker} to create
	 * <p>
	 * @return the number of created {@link Worker}
	 */
	public int createWorkers(final int workerToCreateCount) {
		int createdWorkerCount = 0;
		try {
			for (int i = 0; i < workerToCreateCount; ++i) {
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
	 * @param availableWorkerToCreateCount the number of {@link Worker} to create if required
	 * <p>
	 * @return the number of created {@link Worker}
	 */
	public int createAvailableWorkers(final int availableWorkerToCreateCount) {
		final int workerToCreateCount = availableWorkerToCreateCount -
				(availableWorkerCount - tasks.size());
		if (workerToCreateCount <= 0) {
			return 0;
		}
		return createWorkers(Math.min(workerToCreateCount, maxThreadCount - workerCount));
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
		if (workerCount >= maxThreadCount) {
			throw new IllegalOperationException(
					"The maximum number of workers (" + maxThreadCount + ") has been reached");
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
	 * @param worker the {@link Worker} of {@code I} and {@code O} types to remove
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
	 * @param workerToReserveCount the number of {@link Worker} to reserve
	 * <p>
	 * @return {@code true} if the {@link Worker} are reserved, {@code false} otherwise
	 */
	public boolean reserveWorkers(final int workerToReserveCount) {
		// Check the arguments
		IntegerArguments.requirePositive(workerToReserveCount);

		// Reserve the workers
		IO.debug("Try to reserve ", workerToReserveCount, " workers of ", c);
		if (maxThreadCount - reservedWorkerCount < workerToReserveCount) {
			IO.debug("Cannot reserve ", workerToReserveCount, " workers of ", c, " (",
					reservedWorkerCount, "/", maxThreadCount, " reserved workers)");
			return false;
		}
		reservedWorkerCount += workerToReserveCount;
		// Create more workers if required
		final int workerToCreateCount = reservedWorkerCount - workerCount;
		if (workerToCreateCount <= 0) {
			IO.debug("OK, the workers of ", c, " are already created (",
					reservedWorkerCount, "/", maxThreadCount, " reserved workers)");
			return true;
		}
		if (createWorkers(workerToCreateCount) != workerToCreateCount) {
			reservedWorkerCount -= workerToReserveCount;
			IO.debug("Cannot reserve ", workerToReserveCount, " workers of ", c, " (",
					reservedWorkerCount, "/", maxThreadCount, " reserved workers)");
			return false;
		}
		IO.debug("OK, create ", workerToCreateCount, " more workers of ", c, " (",
				reservedWorkerCount, "/", maxThreadCount, " reserved workers)");
		return true;
	}

	/**
	 * Reserves the specified maximum number of {@link Worker}.
	 * <p>
	 * @param maxWorkerToReserveCount the maximum number of {@link Worker} to reserve
	 * <p>
	 * @return the number of reserved {@link Worker}
	 */
	public int reserveMaxWorkers(final int maxWorkerToReserveCount) {
		// Check the arguments
		IntegerArguments.requirePositive(maxWorkerToReserveCount);

		// Reserve the workers
		IO.debug("Reserve maximum ", maxWorkerToReserveCount, " workers of ", c);
		final int workerToReserveCount = Math.min(maxThreadCount - reservedWorkerCount,
				maxWorkerToReserveCount);
		if (workerToReserveCount <= 0) {
			IO.debug("Cannot reserve ", workerToReserveCount, " workers of ", c, " (",
					reservedWorkerCount, "/", maxThreadCount, " reserved workers)");
			return 0;
		}
		reservedWorkerCount += workerToReserveCount;
		// Create more workers if required
		final int workerToCreateCount = reservedWorkerCount - workerCount;
		if (workerToCreateCount <= 0) {
			IO.debug("OK, create ", workerToCreateCount, " more workers of ", c, " (",
					reservedWorkerCount, "/", maxThreadCount, " reserved workers)");
			return workerToReserveCount;
		}
		IO.debug("OK, create ", workerToCreateCount, " more workers of ", c,
				" (total reserved: ", reservedWorkerCount, "/", maxThreadCount, ")");
		final int missingWorkerCount = workerToCreateCount - createWorkers(workerToCreateCount);
		reservedWorkerCount -= missingWorkerCount;
		return workerToReserveCount - missingWorkerCount;
	}

	//////////////////////////////////////////////

	/**
	 * Frees the specified number of {@link Worker}.
	 * <p>
	 * @param workerToFreeCount the number of {@link Worker} to free
	 */
	public void freeWorkers(final int workerToFreeCount) {
		reservedWorkerCount -= workerToFreeCount;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Kills the specified {@link Worker}.
	 * <p>
	 * @param worker the {@link Worker} of {@code I} and {@code O} types to kill
	 */
	@SuppressWarnings("deprecation")
	public void killWorker(final Worker<I, O> worker) {
		worker.stop();
		removeWorker(worker);
	}

	/**
	 * Kills all the {@link Worker}.
	 */
	@SuppressWarnings("deprecation")
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
	 * Submits a {@link Task} with the specified {@code I} input for execution and returns its
	 * identifier.
	 * <p>
	 * @param input the {@code I} input of the {@link Task} to submit
	 * <p>
	 * @return the identifier of the submitted {@link Task}
	 */
	public long submit(final I input) {
		++currentTaskId;
		IO.debug("Submit task ", currentTaskId);
		tasks.add(new Task<I>(currentTaskId, input));
		return currentTaskId;
	}

	/**
	 * Returns the next {@link Task} of {@code I} type if {@code this} is running, {@code null}
	 * otherwise.
	 * <p>
	 * @return the next {@link Task} of {@code I} type if {@code this} is running, {@code null}
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
	 * Adds the specified {@code O} result of the {@link Task} with the specified identifier.
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
		createWorkers(minThreadCount);
		synchronized (this) {
			notifyAll();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public WorkQueue<I, O> clone() {
		return new WorkQueue<I, O>(model, minThreadCount, maxThreadCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
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
		return Objects.equals(minThreadCount, otherWorkQueue.minThreadCount) &&
				Objects.equals(maxThreadCount, otherWorkQueue.maxThreadCount) &&
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
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, minThreadCount, maxThreadCount, isRunning, model,
				c, workers, workerCount, availableWorkerCount, reservedWorkerCount, tasks,
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
		return Objects.getName(this).concat(" of type ").concat(Objects.getName(c));
	}
}
