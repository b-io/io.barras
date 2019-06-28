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
import jupiter.common.struct.tuple.Pair;

public class SynchronizedWorkQueue<I, O>
		extends WorkQueue<I, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SynchronizedWorkQueue} with the specified model {@link Worker}.
	 * <p>
	 * @param model the model {@link Worker} of type {@code I} and {@code O}
	 */
	public SynchronizedWorkQueue(final Worker<I, O> model) {
		this(model, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
	}

	/**
	 * Constructs a {@link SynchronizedWorkQueue} with the specified model {@link Worker} and
	 * minimum and maximum number of {@link Worker} to handle.
	 * <p>
	 * @param model      the model {@link Worker} of type {@code I} and {@code O} to handle
	 * @param minThreads the minimum number of {@link Worker} to handle
	 * @param maxThreads the maximum number of {@link Worker} to handle
	 */
	public SynchronizedWorkQueue(final Worker<I, O> model, final int minThreads,
			final int maxThreads) {
		super(model, minThreads, maxThreads);
		createWorkers(minThreads);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WORKER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reserves the specified number of {@link Worker}.
	 * <p>
	 * @param n the number of {@link Worker} to reserve
	 * <p>
	 * @return {@code true} if the {@link Worker} are reserved, {@code false} otherwise
	 */
	@Override
	public boolean reserveWorkers(final int n) {
		synchronized (workers) {
			return super.reserveWorkers(n);
		}
	}

	/**
	 * Instantiates a {@link Worker} according to the model.
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
	// TASK
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds a task with the specified input and returns its identifier.
	 * <p>
	 * @param input the input {@code I} of the task to add
	 * <p>
	 * @return the identifier of the added task
	 */
	@Override
	public long submit(final I input) {
		// Create a worker if required
		createAvailableWorkers(1);
		// Add the task
		synchronized (tasks) {
			final long taskId = super.submit(input);
			tasks.notifyAll();
			return taskId;
		}
	}

	/**
	 * Returns the next task.
	 * <p>
	 * @return the next task
	 */
	@Override
	public Pair<Long, I> getNextTask() {
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
	 * Adds the result {@code O} of the task with the specified identifier.
	 * <p>
	 * @param id     the identifier of the task
	 * @param result the result {@code O} of the task
	 */
	@Override
	public void addResult(final long id, final O result) {
		synchronized (results) {
			super.addResult(id, result);
			results.notifyAll();
		}
	}

	/**
	 * Returns the result {@code O} of the task with the specified identifier.
	 * <p>
	 * @param id the identifier of the task
	 * <p>
	 * @return the result {@code O} of the task with the specified identifier
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
	 */
	@Override
	public void shutdown() {
		synchronized (tasks) {
			super.shutdown();
			tasks.notifyAll();
		}
	}
}
