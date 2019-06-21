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
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.util.Strings;

/**
 * Working thread.
 * <p>
 * @param <I> the input type
 * @param <O> the output type
 */
public abstract class Worker<I, O>
		extends Thread
		implements Callable<O>, ICloneable<Worker<I, O>>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	protected static volatile long CURRENT_ID = 0L;
	protected static final Lock CURRENT_ID_LOCK = new ReentrantLock(true);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final long id;
	protected volatile I input;
	protected volatile WorkQueue<I, O> workQueue;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Worker() {
		this(null);
	}

	protected Worker(final I input) {
		super();
		CURRENT_ID_LOCK.lock();
		try {
			this.id = CURRENT_ID;
			this.input = input;
			++CURRENT_ID;
		} finally {
			CURRENT_ID_LOCK.unlock();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the input.
	 * <p>
	 * @param input an {@code I} object
	 */
	public void setInput(final I input) {
		this.input = input;
	}

	/**
	 * Sets the work queue.
	 * <p>
	 * @param workQueue an {@link WorkQueue} of type {@code I} and {@code O}
	 */
	public void setWorkQueue(final WorkQueue<I, O> workQueue) {
		this.workQueue = workQueue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	public O call() {
		return call(input);
	}

	public abstract O call(final I input);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// THREAD
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void run() {
		IO.debug("The worker ", this, " has started");
		while (true) {
			final Pair<Long, I> task = workQueue.getNextTask();
			if (workQueue.isRunning()) {
				O output = null;
				try {
					output = call(task.getSecond());
				} catch (final RuntimeException ex) {
					IO.error(ex);
				}
				workQueue.addResult(task.getFirst(), output);
			} else {
				break;
			}
		}
		finalize();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public abstract Worker<I, O> clone();

	@Override
	protected void finalize() {
		IO.debug(this, " is finalized");
		try {
			super.finalize();
		} catch (final Throwable ignored) {
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + Strings.parenthesize(id);
	}
}
