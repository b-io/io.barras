/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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

import java.util.concurrent.Callable;

import jupiter.common.model.ICloneable;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.util.Strings;

public abstract class Worker<I, O>
		extends Thread
		implements Callable<O>, ICloneable<Worker<I, O>> {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static volatile long currentId = 0L;

	protected final long id;
	protected volatile I input;
	protected volatile IWorkQueue<I, O> workQueue;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Worker() {
		this.id = currentId;
		++currentId;
	}

	public Worker(final I input) {
		this.id = currentId;
		this.input = input;
		++currentId;
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
	 * @param workQueue an {@link IWorkQueue} of type {@code I} and {@code O}
	 */
	public void setWorkQueue(final IWorkQueue<I, O> workQueue) {
		this.workQueue = workQueue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public O call() {
		return call(input);
	}

	public abstract O call(final I input);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// THREAD
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void run() {
		IO.debug("The working thread ", id, " has started");
		Pair<Long, I> task;
		while (true) {
			task = workQueue.getNextTask();
			if (workQueue.isRunning()) {
				O output;
				try {
					output = call(task.getSecond());
				} catch (final RuntimeException ex) {
					IO.error(ex);
					output = null;
				}
				workQueue.addResult(task.getFirst(), output);
			} else {
				break;
			}
		}
		IO.debug("The working thread ", id, " is finished");
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public abstract Worker<I, O> clone();

	@Override
	public String toString() {
		return getClass().getSimpleName() + Strings.parenthesize(id);
	}
}
