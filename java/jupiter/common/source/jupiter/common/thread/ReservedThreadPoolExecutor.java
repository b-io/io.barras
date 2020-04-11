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
	 * Constructs a {@link ReservedThreadPoolExecutor} by default.
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
		this(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
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

	//////////////////////////////////////////////

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
	// ACCESSORS
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
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Submits the specified {@link Runnable} task for execution if there is at least one thread
	 * that is not actively executing tasks.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>The {@link Future#get} method will return {@code null} upon successful completion.</dd>
	 * </dl>
	 * <p>
	 * @param task the {@link Runnable} task to submit
	 * <p>
	 * @return a {@link Future} representing the pending completion of the specified
	 *         {@link Runnable} task if there is at least one thread that is not actively executing
	 *         tasks, {@code null} otherwise
	 * <p>
	 * @throws RejectedExecutionException if {@code task} is rejected
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
	 * Submits the specified {@link Runnable} task for execution if there is at least one thread
	 * that is not actively executing tasks.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>The {@link Future#get} method will return the specified default {@code T} result upon
	 * successful completion.</dd>
	 * </dl>
	 * <p>
	 * @param <T>           the type of the {@link Future} to return
	 * @param task          the {@link Runnable} task to submit
	 * @param defaultResult the default {@code T} result (may be {@code null})
	 * <p>
	 * @return a {@link Future} representing the pending completion of the specified
	 *         {@link Runnable} task if there is at least one thread that is not actively executing
	 *         tasks, {@code null} otherwise
	 * <p>
	 * @throws RejectedExecutionException if {@code task} is rejected
	 */
	@Override
	public <T> Future<T> submit(final Runnable task, final T defaultResult) {
		submitLock.lock();
		try {
			if (getActiveCount() < maxPoolSize) {
				return super.submit(task, defaultResult);
			}
		} finally {
			submitLock.unlock();
		}
		return null;
	}

	/**
	 * Submits the specified {@link Callable} task for execution if there is at least one thread
	 * that is not actively executing tasks.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>The {@link Future#get} method will return {@code null} upon successful completion.</dd>
	 * </dl>
	 * <p>
	 * @param <T>  the type of the {@link Future} to return
	 * @param task the {@link Callable} task of {@code T} type to submit
	 * <p>
	 * @return a {@link Future} representing the pending completion of the specified
	 *         {@link Callable} task if there is at least one thread that is not actively executing
	 *         tasks, {@code null} otherwise
	 * <p>
	 * @throws RejectedExecutionException if {@code task} is rejected
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
