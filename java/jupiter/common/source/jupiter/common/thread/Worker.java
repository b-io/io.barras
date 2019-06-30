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
import jupiter.common.util.Strings;

/**
 * {@link Worker} is a working {@link Thread} processing an {@code I} input and returning an
 * {@code O} output.
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

	/**
	 * The identifier.
	 */
	protected final long id;
	/**
	 * The {@code I} input.
	 */
	protected volatile I input;
	/**
	 * The {@link WorkQueue} of type {@code I} and {@code O}.
	 */
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
	 * Sets the {@code I} input.
	 * <p>
	 * @param input an {@code I} input
	 */
	public void setInput(final I input) {
		this.input = input;
	}

	/**
	 * Sets the {@link WorkQueue}.
	 * <p>
	 * @param workQueue a {@link WorkQueue} of type {@code I} and {@code O}
	 */
	public void setWorkQueue(final WorkQueue<I, O> workQueue) {
		this.workQueue = workQueue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Processes the {@code I} input and returns an {@code O} output.
	 * <p>
	 * @return an {@code O} output
	 */
	public O call() {
		return call(input);
	}

	/**
	 * Processes the specified {@code I} input and returns an {@code O} output.
	 * <p>
	 * @param input the {@code I} input to process
	 * <p>
	 * @return an {@code O} output
	 */
	public abstract O call(final I input);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// THREAD
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Runs {@code this}.
	 *
	 * @see #start()
	 * @see #stop()
	 */
	@Override
	public void run() {
		IO.debug("The worker ", this, " has started");
		while (true) {
			final Task<I> task = workQueue.getNextTask();
			if (workQueue.isRunning()) {
				O output = null;
				try {
					output = call(task.getInput());
				} catch (final RuntimeException ex) {
					IO.error(ex);
				}
				workQueue.addResult(task.getID(), output);
			} else {
				break;
			}
		}
		finalize();
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
	public abstract Worker<I, O> clone();

	/**
	 * Disposes of system resources and performs a cleanup. Note that this method is called by the
	 * garbage collector on an {@link Object} when the garbage collection determines that there are
	 * no more references to the {@link Object}.
	 *
	 * @see java.lang.ref.PhantomReference
	 * @see java.lang.ref.WeakReference
	 */
	@Override
	protected void finalize() {
		IO.debug(this, " is finalized");
		try {
			super.finalize();
		} catch (final Throwable ignored) {
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + Strings.parenthesize(id);
	}
}
