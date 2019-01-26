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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jupiter.common.exception.IllegalOperationException;
import jupiter.common.struct.tuple.Pair;

public class FairWorkQueue<I, O>
		extends WorkQueue<I, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The internal lock of the workers.
	 */
	protected final Lock workersLock = new ReentrantLock(true);

	/**
	 * The internal lock of the tasks.
	 */
	protected final Lock tasksLock = new ReentrantLock(true);
	protected final Condition tasksLockCondition = tasksLock.newCondition();

	/**
	 * The internal lock of the results.
	 */
	protected final Lock resultsLock = new ReentrantLock(true);
	protected final Condition resultsLockCondition = resultsLock.newCondition();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public FairWorkQueue(final Worker<I, O> model) {
		super(model);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WORKER
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reserves n workers.
	 * <p>
	 * @param n the number of workers to reserve
	 * <p>
	 * @return {@code true} if the workers are reserved, {@code false} otherwise
	 */
	@Override
	public boolean reserveWorkers(final int n) {
		workersLock.lock();
		try {
			return super.reserveWorkers(n);
		} finally {
			workersLock.unlock();
		}
	}

	/**
	 * Instantiates a worker according to the model.
	 * <p>
	 * @return the number of created workers
	 * <p>
	 * @throws IllegalOperationException if the maximum number of workers has been reached
	 */
	@Override
	protected int createWorker()
			throws IllegalOperationException {
		workersLock.lock();
		try {
			return super.createWorker();
		} finally {
			workersLock.unlock();
		}
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
	@Override
	public long submit(final I input) {
		// Create a worker if required
		createAvailableWorkers(1);
		// Add the task
		tasksLock.lock();
		try {
			final long taskId = super.submit(input);
			tasksLockCondition.signal();
			return taskId;
		} finally {
			tasksLock.unlock();
		}
	}

	/**
	 * Returns the next task.
	 * <p>
	 * @return the next task
	 */
	@Override
	public Pair<Long, I> getNextTask() {
		tasksLock.lock();
		try {
			while (isRunning && tasks.isEmpty()) {
				try {
					tasksLockCondition.await();
				} catch (final InterruptedException ignored) {
				}
			}
			return super.getNextTask();
		} finally {
			tasksLock.unlock();
		}
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
	@Override
	public void addResult(final long id, final O result) {
		resultsLock.lock();
		try {
			super.addResult(id, result);
			resultsLockCondition.signalAll();
		} finally {
			resultsLock.unlock();
		}
	}

	/**
	 * Returns the result of the task with the specified identifier.
	 * <p>
	 * @param id the identifier of the task
	 * <p>
	 * @return the result of the task with the specified identifier
	 */
	@Override
	public O get(final long id) {
		resultsLock.lock();
		try {
			while (!results.containsKey(id)) {
				try {
					resultsLockCondition.await();
				} catch (final InterruptedException ignored) {
				}
			}
			return super.get(id);
		} finally {
			resultsLock.unlock();
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
		tasksLock.lock();
		try {
			super.shutdown();
			tasksLockCondition.signalAll();
		} finally {
			tasksLock.unlock();
		}
	}
}
