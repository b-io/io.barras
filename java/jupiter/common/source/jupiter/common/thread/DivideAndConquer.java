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

import jupiter.common.math.Interval;
import jupiter.common.math.Maths;
import jupiter.common.test.IntegerArguments;

public abstract class DivideAndConquer
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
	 * The {@link WorkQueue} of {@link Interval} of {@link Integer} and {@link Integer}.
	 */
	protected final WorkQueue<Interval<Integer>, Integer> workQueue;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link DivideAndConquer}.
	 */
	public DivideAndConquer() {
		this.workQueue = new LockedWorkQueue<Interval<Integer>, Integer>(new Conqueror());
	}

	/**
	 * Constructs a {@link DivideAndConquer} with the specified minimum and maximum numbers of
	 * {@link Worker}.
	 * <p>
	 * @param minThreadCount the minimum number of {@link Worker} to handle
	 * @param maxThreadCount the maximum number of {@link Worker} to handle
	 */
	public DivideAndConquer(final int minThreadCount, final int maxThreadCount) {
		this.workQueue = new LockedWorkQueue<Interval<Integer>, Integer>(new Conqueror(),
				minThreadCount, maxThreadCount);
	}

	/**
	 * Constructs a {@link DivideAndConquer} with the specified minimum and maximum numbers of
	 * {@link Worker} and fairness policy.
	 * <p>
	 * @param minThreadCount the minimum number of {@link Worker} to handle
	 * @param maxThreadCount the maximum number of {@link Worker} to handle
	 * @param isFair         the flag specifying whether to use a fair ordering policy
	 */
	public DivideAndConquer(final int minThreadCount, final int maxThreadCount, final boolean isFair) {
		this.workQueue = new LockedWorkQueue<Interval<Integer>, Integer>(new Conqueror(),
				minThreadCount, maxThreadCount, isFair);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Divides the execution from the specified index to the specified index into execution slices
	 * and conquers them. Returns the result of each execution slice.
	 * <p>
	 * @param from         the index to start dividing from (inclusive)
	 * @param to           the index to finish dividing at (exclusive)
	 * @param minSliceSize the minimum execution slice size
	 * <p>
	 * @return the result of each execution slice
	 */
	public int[] divideAndConquer(final int from, final int to, final int minSliceSize) {
		// Check the arguments
		IntegerArguments.requirePositive(minSliceSize);

		// Divide and conquer
		final int sliceCount = workQueue.reserveMaxWorkers(
				Maths.ceilToInt(Maths.division(to - from, minSliceSize)));
		if (sliceCount == 0) {
			return new int[] {conquer(new Interval<Integer>(from, to))};
		}
		try {
			return conquer(divide(from, to, sliceCount));
		} finally {
			workQueue.freeWorkers(sliceCount);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Divides the execution from the specified index to the specified index into the specified
	 * number of execution slices and returns the identifier of each of them.
	 * <p>
	 * @param from       the index to start dividing from (inclusive)
	 * @param to         the index to finish dividing at (exclusive)
	 * @param sliceCount the number of execution slices to create
	 * <p>
	 * @return the identifier of each execution slice
	 */
	protected long[] divide(final int from, final int to, final int sliceCount) {
		final int count = to - from;
		final int sliceSize = count / sliceCount;
		final long[] ids = new long[sliceCount];
		for (int i = 0; i < sliceCount - 1; ++i) {
			ids[i] = workQueue.submit(
					new Interval<Integer>(i * sliceSize, (i + 1) * sliceSize - 1));
		}
		ids[sliceCount - 1] = workQueue.submit(
				new Interval<Integer>((sliceCount - 1) * sliceSize, to - 1));
		return ids;
	}

	//////////////////////////////////////////////

	/**
	 * Conquers the execution slices with the specified identifiers and returns the result of each
	 * of them.
	 * <p>
	 * @param ids the identifiers of the execution slices to conquer
	 * <p>
	 * @return the result of each execution slice
	 */
	protected int[] conquer(final long[] ids) {
		final int[] results = new int[ids.length];
		for (int i = 0; i < ids.length; ++i) {
			results[i] = workQueue.get(ids[i]);
		}
		return results;
	}

	/**
	 * Conquers the execution slice with the specified {@link Interval} and returns its result.
	 * <p>
	 * @param interval the {@link Interval} of {@link Integer} of the execution slice to conquer
	 * <p>
	 * @return the result of the execution slice
	 */
	protected abstract int conquer(final Interval<Integer> interval);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// POOL
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the {@link WorkQueue} is running.
	 * <p>
	 * @return {@code true} if the {@link WorkQueue} is running, {@code false} otherwise
	 */
	public boolean isRunning() {
		return workQueue.isRunning();
	}

	//////////////////////////////////////////////

	/**
	 * Shutdowns the {@link WorkQueue}.
	 */
	public void shutdown() {
		workQueue.shutdown();
	}

	/**
	 * Shutdowns the {@link WorkQueue}.
	 * <p>
	 * @param force the flag specifying whether to force shutdowning
	 */
	public void shutdown(final boolean force) {
		workQueue.shutdown(force);
	}

	//////////////////////////////////////////////

	/**
	 * Restarts the {@link WorkQueue}.
	 */
	public void restart() {
		workQueue.restart();
	}

	/**
	 * Restarts the {@link WorkQueue}.
	 * <p>
	 * @param force the flag specifying whether to force restarting
	 */
	public void restart(final boolean force) {
		workQueue.restart(force);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected class Conqueror
			extends Worker<Interval<Integer>, Integer> {

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
		 * Constructs a {@link Conqueror}.
		 */
		protected Conqueror() {
			super();
		}

		/**
		 * Constructs a {@link Conqueror} with the specified input {@link Interval}.
		 * <p>
		 * @param interval the input {@link Interval} of {@link Integer}
		 */
		protected Conqueror(final Interval<Integer> interval) {
			super(interval);
		}


		////////////////////////////////////////////////////////////////////////////////////////////////
		// WORKER
		////////////////////////////////////////////////////////////////////////////////////////////////

		@Override
		public Integer call(final Interval<Integer> interval) {
			return conquer(interval);
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
		public Conqueror clone() {
			return new Conqueror();
		}
	}
}
