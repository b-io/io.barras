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

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReservedThreadPoolExecutor
		extends ThreadPoolExecutor
		implements Serializable {

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
	 * The maximum pool size.
	 */
	protected final int maxPoolSize;

	/**
	 * The internal {@link Lock} for submission.
	 */
	protected final Lock submitLock = new ReentrantLock(true);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ReservedThreadPoolExecutor}.
	 */
	public ReservedThreadPoolExecutor() {
		this(Runtime.getRuntime().availableProcessors());
	}

	/**
	 * Constructs a {@link ReservedThreadPoolExecutor} with the specified pool size.
	 * <p>
	 * @param poolSize the pool size
	 */
	public ReservedThreadPoolExecutor(final int poolSize) {
		super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		maxPoolSize = poolSize;
	}

	/**
	 * Constructs a {@link ReservedThreadPoolExecutor} with the specified pool size.
	 * <p>
	 * @param poolSize      the pool size
	 * @param maxPoolSize   the maximum pool size
	 * @param keepAliveTime the keep alive time
	 * @param unit          the {@link TimeUnit}
	 * @param workQueue     the {@link BlockingQueue} of {@link Runnable}
	 */
	public ReservedThreadPoolExecutor(final int poolSize, final int maxPoolSize,
			final long keepAliveTime, final TimeUnit unit,
			final BlockingQueue<Runnable> workQueue) {
		super(poolSize, maxPoolSize, keepAliveTime, unit, workQueue);
		this.maxPoolSize = maxPoolSize;
	}

	/**
	 * Constructs a {@link ReservedThreadPoolExecutor} with the specified pool size.
	 * <p>
	 * @param poolSize      the pool size
	 * @param maxPoolSize   the maximum pool size
	 * @param keepAliveTime the keep alive time
	 * @param unit          the {@link TimeUnit}
	 * @param workQueue     the {@link BlockingQueue} of {@link Runnable}
	 * @param threadFactory the {@link ThreadFactory}
	 */
	public ReservedThreadPoolExecutor(final int poolSize, final int maxPoolSize,
			final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue,
			final ThreadFactory threadFactory) {
		super(poolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		this.maxPoolSize = maxPoolSize;
	}

	/**
	 * Constructs a {@link ReservedThreadPoolExecutor} with the specified pool size.
	 * <p>
	 * @param poolSize      the pool size
	 * @param maxPoolSize   the maximum pool size
	 * @param keepAliveTime the keep alive time
	 * @param unit          the {@link TimeUnit}
	 * @param workQueue     the {@link BlockingQueue} of {@link Runnable}
	 * @param handler       the {@link RejectedExecutionHandler}
	 */
	public ReservedThreadPoolExecutor(final int poolSize, final int maxPoolSize,
			final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue,
			final RejectedExecutionHandler handler) {
		super(poolSize, maxPoolSize, keepAliveTime, unit, workQueue, handler);
		this.maxPoolSize = maxPoolSize;
	}

	/**
	 * Constructs a {@link ReservedThreadPoolExecutor} with the specified pool size.
	 * <p>
	 * @param poolSize      the pool size
	 * @param maxPoolSize   the maximum pool size
	 * @param keepAliveTime the keep alive time
	 * @param unit          the {@link TimeUnit}
	 * @param workQueue     the {@link BlockingQueue} of {@link Runnable}
	 * @param threadFactory the {@link ThreadFactory}
	 * @param handler       the {@link RejectedExecutionHandler}
	 */
	public ReservedThreadPoolExecutor(final int poolSize, final int maxPoolSize,
			final long keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue,
			final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
		super(poolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		this.maxPoolSize = maxPoolSize;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the maximum pool size.
	 * <p>
	 * @return the maximum pool size
	 */
	public int getMaxPoolSize() {
		return maxPoolSize;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONDITIONAL SUBMIT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Submits the specified task to the {@link ThreadPoolExecutor} if there is at least one thread
	 * that is not actively executing tasks. Returns a {@link Future} representing the pending
	 * results if the task is submitted, {@code null} otherwise.
	 * <p>
	 * @param task the task {@link Runnable} to submit
	 * <p>
	 * @return a {@link Future} representing the pending results if the task is submitted,
	 *         {@code null} otherwise
	 * <p>
	 * @throws RejectedExecutionException if {@code task} is rejected
	 * @throws NullPointerException       if {@code task} is {@code null}
	 */
	@Override
	public Future<?> submit(final Runnable task) {
		submitLock.lock();
		try {
			if (getActiveCount() < maxPoolSize) {
				return super.submit(task);
			}
		} finally {
			submitLock.unlock();
		}
		return null;
	}

	/**
	 * Submits the specified task to the {@link ThreadPoolExecutor} if there is at least one thread
	 * that is not actively executing tasks. Returns a {@link Future} representing the pending
	 * results if the task is submitted, {@code null} otherwise.
	 * <p>
	 * @param <T>    the type of the task {@link Runnable} to submit
	 * @param task   the task {@link Runnable} to submit
	 * @param result the default value for the returned future
	 * <p>
	 * @return a {@link Future} representing the pending results if the task is submitted,
	 *         {@code null} otherwise
	 * <p>
	 * @throws RejectedExecutionException if {@code task} is rejected
	 * @throws NullPointerException       if {@code task} is {@code null}
	 */
	@Override
	public <T> Future<T> submit(final Runnable task, final T result) {
		submitLock.lock();
		try {
			if (getActiveCount() < maxPoolSize) {
				return super.submit(task, result);
			}
		} finally {
			submitLock.unlock();
		}
		return null;
	}

	/**
	 * Submits the specified task to the {@link ThreadPoolExecutor} if there is at least one thread
	 * that is not actively executing tasks. Returns a {@link Future} representing the pending
	 * results if the task is submitted, {@code null} otherwise.
	 * <p>
	 * @param <T>  the type of the task {@link Callable} to submit
	 * @param task the task {@link Callable} of type {@code T} to submit
	 * <p>
	 * @return a {@link Future} representing the pending results if the task is submitted,
	 *         {@code null} otherwise
	 * <p>
	 * @throws RejectedExecutionException if {@code task} is rejected
	 * @throws NullPointerException       if {@code task} is {@code null}
	 */
	@Override
	public <T> Future<T> submit(final Callable<T> task) {
		submitLock.lock();
		try {
			if (getActiveCount() < maxPoolSize) {
				return super.submit(task);
			}
		} finally {
			submitLock.unlock();
		}
		return null;
	}
}
