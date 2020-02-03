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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jupiter.common.exception.IllegalOperationException;

public class LockedWorkQueue<I, O>
		extends WorkQueue<I, O> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether {@code this} is fair.
	 */
	protected final boolean isFair;

	/**
	 * The internal {@link Lock} of the workers.
	 */
	protected final Lock workersLock;
	protected final Condition workersLockCondition;

	/**
	 * The internal {@link Lock} of the tasks.
	 */
	protected final Lock tasksLock;
	protected final Condition tasksLockCondition;

	/**
	 * The internal {@link Lock} of the results.
	 */
	protected final Lock resultsLock;
	protected final Condition resultsLockCondition;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link LockedWorkQueue} with the specified model {@link Worker}.
	 * <p>
	 * @param model the model {@link Worker} of types {@code I} and {@code O}
	 */
	public LockedWorkQueue(final Worker<I, O> model) {
		this(model, DEFAULT_MIN_THREADS, DEFAULT_MAX_THREADS);
	}

	/**
	 * Constructs a {@link LockedWorkQueue} with the specified model {@link Worker} and minimum and
	 * maximum numbers of {@link Worker}.
	 * <p>
	 * @param model          the model {@link Worker} of types {@code I} and {@code O}
	 * @param minThreadCount the minimum number of {@link Worker} to handle
	 * @param maxThreadCount the maximum number of {@link Worker} to handle
	 */
	public LockedWorkQueue(final Worker<I, O> model, final int minThreadCount,
			final int maxThreadCount) {
		this(model, minThreadCount, maxThreadCount, false);
	}

	/**
	 * Constructs a {@link LockedWorkQueue} with the specified model {@link Worker}, minimum and
	 * maximum numbers of {@link Worker} and fairness policy.
	 * <p>
	 * @param model          the model {@link Worker} of types {@code I} and {@code O}
	 * @param minThreadCount the minimum number of {@link Worker} to handle
	 * @param maxThreadCount the maximum number of {@link Worker} to handle
	 * @param isFair         the flag specifying whether to use a fair ordering policy
	 */
	public LockedWorkQueue(final Worker<I, O> model, final int minThreadCount,
			final int maxThreadCount, final boolean isFair) {
		super(model, minThreadCount, maxThreadCount);
		this.isFair = isFair;
		workersLock = new ReentrantLock(isFair);
		workersLockCondition = workersLock.newCondition();
		tasksLock = new ReentrantLock(isFair);
		tasksLockCondition = tasksLock.newCondition();
		resultsLock = new ReentrantLock(isFair);
		resultsLockCondition = resultsLock.newCondition();
		createWorkers(minThreadCount);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the flag specifying whether {@code this} is fair.
	 * <p>
	 * @return the flag specifying whether {@code this} is fair
	 */
	public boolean isFair() {
		return isFair;
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
		workersLock.lock();
		try {
			return super.createWorker();
		} finally {
			workersLock.unlock();
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
		workersLock.lock();
		try {
			return super.reserveWorkers(workerToReserveCount);
		} finally {
			workersLock.unlock();
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
		workersLock.lock();
		try {
			return super.reserveMaxWorkers(maxWorkerToReserveCount);
		} finally {
			workersLock.unlock();
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
		workersLock.lock();
		try {
			super.removeWorker(worker);
			workersLockCondition.signal();
		} finally {
			workersLock.unlock();
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
	 * Returns the next {@link Task} of type {@code I} if {@code this} is running, {@code null}
	 * otherwise.
	 * <p>
	 * @return the next {@link Task} of type {@code I} if {@code this} is running, {@code null}
	 *         otherwise
	 */
	@Override
	public Task<I> getNextTask() {
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
	 * Adds the {@code O} result of the {@link Task} with the specified identifier.
	 * <p>
	 * @param id     the identifier of the {@link Task}
	 * @param result the {@code O} result of the {@link Task}
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
	 * Returns the {@code O} result of the {@link Task} with the specified identifier.
	 * <p>
	 * @param id the identifier of the {@link Task}
	 * <p>
	 * @return the {@code O} result of the {@link Task} with the specified identifier
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
	 * <p>
	 * @param force the flag specifying whether to force shutdowning
	 */
	@Override
	public void shutdown(final boolean force) {
		tasksLock.lock();
		try {
			super.shutdown(force);
			tasksLockCondition.signalAll();
		} finally {
			tasksLock.unlock();
		}

		workersLock.lock();
		try {
			if (force) {
				killAllWorkers();
			}

			while (workerCount > 0) {
				try {
					workersLockCondition.await();
				} catch (final InterruptedException ignored) {
				}
			}
		} finally {
			workersLock.unlock();
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
	public LockedWorkQueue<I, O> clone() {
		return new LockedWorkQueue<I, O>(model, minThreadCount, maxThreadCount, isFair);
	}
}
