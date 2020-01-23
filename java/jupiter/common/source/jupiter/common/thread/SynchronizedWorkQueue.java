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

import jupiter.common.exception.IllegalOperationException;

public class SynchronizedWorkQueue<I, O>
		extends WorkQueue<I, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SynchronizedWorkQueue} with the specified model {@link Worker}.
	 * <p>
	 * @param model the model {@link Worker} of types {@code I} and {@code O}
	 */
	public SynchronizedWorkQueue(final Worker<I, O> model) {
		this(model, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
	}

	/**
	 * Constructs a {@link SynchronizedWorkQueue} with the specified model {@link Worker} and
	 * minimum and maximum numbers of {@link Worker}.
	 * <p>
	 * @param model          the model {@link Worker} of types {@code I} and {@code O}
	 * @param minThreadCount the minimum number of {@link Worker} to handle
	 * @param maxThreadCount the maximum number of {@link Worker} to handle
	 */
	public SynchronizedWorkQueue(final Worker<I, O> model, final int minThreadCount,
			final int maxThreadCount) {
		super(model, minThreadCount, maxThreadCount);
		createWorkers(minThreadCount);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WORKER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link Worker} according to the model.
	 * <p>
	 * @return {@code 1} if the {@link Worker} is created, {@code 0} otherwise
	 * <p>
	 * @throws IllegalOperationException if the maximum number of {@link Worker} has been reached
	 */
	@Override
	protected int createWorker()
			throws IllegalOperationException {
		synchronized (workers) {
			return super.createWorker();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reserves the specified number of {@link Worker}.
	 * <p>
	 * @param workerToReserveCount the number of {@link Worker} to reserve
	 * <p>
	 * @return {@code true} if the {@link Worker} are reserved, {@code false} otherwise
	 */
	@Override
	public boolean reserveWorkers(final int workerToReserveCount) {
		synchronized (workers) {
			return super.reserveWorkers(workerToReserveCount);
		}
	}

	/**
	 * Reserves the specified maximum number of {@link Worker}.
	 * <p>
	 * @param maxWorkerToReserveCount the maximum number of {@link Worker} to reserve
	 * <p>
	 * @return the number of reserved {@link Worker}
	 */
	@Override
	public int reserveMaxWorkers(final int maxWorkerToReserveCount) {
		synchronized (workers) {
			return super.reserveMaxWorkers(maxWorkerToReserveCount);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the specified {@link Worker}.
	 * <p>
	 * @param worker the {@link Worker} of types {@code I} and {@code O} to remove
	 */
	@Override
	public void removeWorker(final Worker<I, O> worker) {
		synchronized (workers) {
			super.removeWorker(worker);
			workers.notify();
		}
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
	@Override
	public long submit(final I input) {
		// Create a worker if required
		createAvailableWorkers(1);
		// Submit the task
		synchronized (tasks) {
			final long taskId = super.submit(input);
			tasks.notify();
			return taskId;
		}
	}

	/**
	 * Returns the next {@link Task} of type {@code I} if {@code this} is running, {@code null}
	 * otherwise.
	 * <p>
	 * @return the next {@link Task} of type {@code I} if {@code this} is running, {@code null}
	 *         otherwise
	 */
	@Override
	public Task<I> getNextTask() {
		synchronized (tasks) {
			while (isRunning && tasks.isEmpty()) {
				try {
					tasks.wait();
				} catch (final InterruptedException ignored) {
				}
			}
			return super.getNextTask();
		}
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
	@Override
	public void addResult(final long id, final O result) {
		synchronized (results) {
			super.addResult(id, result);
			results.notifyAll();
		}
	}

	/**
	 * Returns the {@code O} result of the {@link Task} with the specified identifier.
	 * <p>
	 * @param id the identifier of the {@link Task}
	 * <p>
	 * @return the {@code O} result of the {@link Task} with the specified identifier
	 */
	@Override
	public O get(final long id) {
		synchronized (results) {
			while (!results.containsKey(id)) {
				try {
					results.wait();
				} catch (final InterruptedException ignored) {
				}
			}
			return super.get(id);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// POOL
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shutdowns {@code this}.
	 * <p>
	 * @param force the flag specifying whether to force shutdowning
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void shutdown(final boolean force) {
		synchronized (tasks) {
			super.shutdown(force);
			tasks.notifyAll();
		}

		synchronized (workers) {
			if (force) {
				killAllWorkers();
			}

			while (workerCount != 0) {
				try {
					workers.wait();
				} catch (final InterruptedException ignored) {
				}
			}
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
	public SynchronizedWorkQueue<I, O> clone() {
		return new SynchronizedWorkQueue<I, O>(model, minThreadCount, maxThreadCount);
	}
}
