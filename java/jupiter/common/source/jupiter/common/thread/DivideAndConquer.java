/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

import jupiter.common.io.InputOutput;
import jupiter.common.math.Interval;
import jupiter.common.math.Maths;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Objects;

public abstract class DivideAndConquer<I>
		implements ICloneable<DivideAndConquer<I>>, Serializable {

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
	 * The {@link WorkQueue} used for dividing and conquering the execution.
	 */
	protected final WorkQueue<Pair<I, Interval<Integer>>, Integer> workQueue;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link DivideAndConquer} of {@code I} type by default.
	 */
	public DivideAndConquer() {
		workQueue = new SynchronizedWorkQueue<Pair<I, Interval<Integer>>, Integer>(new Conqueror());
	}

	/**
	 * Constructs a {@link DivideAndConquer} of {@code I} type with the specified minimum and
	 * maximum numbers of {@link Worker}.
	 * <p>
	 * @param minThreadCount the minimum number of {@link Worker} to handle
	 * @param maxThreadCount the maximum number of {@link Worker} to handle
	 */
	public DivideAndConquer(final int minThreadCount, final int maxThreadCount) {
		workQueue = new SynchronizedWorkQueue<Pair<I, Interval<Integer>>, Integer>(new Conqueror(),
				minThreadCount, maxThreadCount);
	}

	/**
	 * Constructs a {@link DivideAndConquer} of {@code I} type with the specified minimum and
	 * maximum numbers of {@link Worker} and fairness policy.
	 * <p>
	 * @param minThreadCount the minimum number of {@link Worker} to handle
	 * @param maxThreadCount the maximum number of {@link Worker} to handle
	 * @param isFair         the flag specifying whether to use a fair ordering policy
	 */
	public DivideAndConquer(final int minThreadCount, final int maxThreadCount,
			final boolean isFair) {
		workQueue = new LockedWorkQueue<Pair<I, Interval<Integer>>, Integer>(new Conqueror(),
				minThreadCount, maxThreadCount, isFair);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONTROLLERS
	////////////////////////////////////////////////////////////////////////////////////////////////

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
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Divides the execution between the specified indices into execution slices and conquers them.
	 * <p>
	 * @param input     the {@code I} input to process
	 * @param fromIndex the index to start dividing from (inclusive)
	 * @param toIndex   the index to finish dividing at (exclusive)
	 * <p>
	 * @return {@link InputOutput#EXIT_SUCCESS} if conquering the execution slice succeeds,
	 *         {@link InputOutput#EXIT_FAILURE} otherwise for each of them
	 */
	public int[] divideAndConquer(final I input, final int fromIndex, final int toIndex) {
		return divideAndConquer(input, fromIndex, toIndex, 1);
	}

	/**
	 * Divides the execution between the specified indices into execution slices and conquers them.
	 * <p>
	 * @param input        the {@code I} input to process
	 * @param fromIndex    the index to start dividing from (inclusive)
	 * @param toIndex      the index to finish dividing at (exclusive)
	 * @param minSliceSize the minimum execution slice size
	 * <p>
	 * @return {@link InputOutput#EXIT_SUCCESS} if conquering the execution slice succeeds,
	 *         {@link InputOutput#EXIT_FAILURE} otherwise for each of them
	 */
	public int[] divideAndConquer(final I input, final int fromIndex, final int toIndex,
			final int minSliceSize) {
		// Check the arguments
		IntegerArguments.requirePositive(minSliceSize);

		// Divide and conquer
		final int maxWorkerToReserveCount = Maths.ceilToInt((toIndex - fromIndex) / minSliceSize);
		if (maxWorkerToReserveCount == 1) {
			return new int[] {conquer(input, fromIndex, toIndex)};
		}
		final int sliceCount = workQueue.reserveMaxWorkers(maxWorkerToReserveCount);
		if (sliceCount == 0) {
			return new int[] {conquer(input, fromIndex, toIndex)};
		}
		try {
			return conquer(divide(input, fromIndex, toIndex, sliceCount));
		} finally {
			workQueue.freeWorkers(sliceCount);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Divides the execution between the specified indices into the specified number of execution
	 * slices.
	 * <p>
	 * @param input      the {@code I} input to process
	 * @param fromIndex  the index to start dividing from (inclusive)
	 * @param toIndex    the index to finish dividing at (exclusive)
	 * @param sliceCount the number of execution slices to create
	 * <p>
	 * @return the identifier of each execution slice
	 */
	protected long[] divide(final I input, final int fromIndex, final int toIndex,
			final int sliceCount) {
		final int count = toIndex - fromIndex;
		final int sliceSize = count / sliceCount;
		final long[] ids = new long[sliceCount];
		for (int si = 0; si < sliceCount - 1; ++si) {
			ids[si] = workQueue.submit(new Pair<I, Interval<Integer>>(input,
					new Interval<Integer>(si * sliceSize, (si + 1) * sliceSize)));
		}
		ids[sliceCount - 1] = workQueue.submit(new Pair<I, Interval<Integer>>(input,
				new Interval<Integer>((sliceCount - 1) * sliceSize, toIndex)));
		return ids;
	}

	//////////////////////////////////////////////

	/**
	 * Conquers the execution slices with the specified identifiers.
	 * <p>
	 * @param ids the identifiers of the execution slices to conquer
	 * <p>
	 * @return {@link InputOutput#EXIT_SUCCESS} if conquering the execution slice succeeds,
	 *         {@link InputOutput#EXIT_FAILURE} otherwise for each of them
	 */
	protected int[] conquer(final long... ids) {
		final int[] outputs = new int[ids.length];
		for (int i = 0; i < ids.length; ++i) {
			outputs[i] = workQueue.get(ids[i]);
		}
		return outputs;
	}

	/**
	 * Conquers the execution slice between the specified indices with the specified {@code I}
	 * input.
	 * <p>
	 * @param input     the {@code I} input to process
	 * @param fromIndex the index to start conquering from (inclusive)
	 * @param toIndex   the index to finish conquering at (exclusive)
	 * <p>
	 * @return {@link InputOutput#EXIT_SUCCESS} if conquering the execution slice succeeds,
	 *         {@link InputOutput#EXIT_FAILURE} otherwise
	 */
	protected int conquer(final I input, final int fromIndex, final int toIndex) {
		return conquer(input, new Interval<Integer>(fromIndex, toIndex));
	}

	/**
	 * Conquers the execution slice with the specified {@code I} input and {@link Interval}.
	 * <p>
	 * @param input    the {@code I} input to process
	 * @param interval the {@link Interval} of {@link Integer} of the execution slice to conquer
	 * <p>
	 * @return {@link InputOutput#EXIT_SUCCESS} if conquering the execution slice succeeds,
	 *         {@link InputOutput#EXIT_FAILURE} otherwise
	 */
	protected abstract int conquer(final I input, final Interval<Integer> interval);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the {@link WorkQueue} is running.
	 * <p>
	 * @return {@code true} if the {@link WorkQueue} is running, {@code false} otherwise
	 */
	public boolean isRunning() {
		return workQueue.isRunning();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	@SuppressWarnings({"cast", "unchecked"})
	public DivideAndConquer<I> clone() {
		try {
			return (DivideAndConquer<I>) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof DivideAndConquer)) {
			return false;
		}
		final DivideAndConquer<?> otherDivideAndConquer = (DivideAndConquer<?>) other;
		return Objects.equals(workQueue, otherDivideAndConquer.workQueue);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see #equals(Object)
	 * @see System#identityHashCode(Object)
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(serialVersionUID, workQueue);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Objects.getName(this);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected class Conqueror
			extends Worker<Pair<I, Interval<Integer>>, Integer> {

		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs a {@link Conqueror}.
		 */
		protected Conqueror() {
			super();
		}

		@Override
		public Integer call(final Pair<I, Interval<Integer>> input) {
			return conquer(input.getFirst(), input.getSecond());
		}

		/**
		 * Clones {@code this}.
		 * <p>
		 * @return a clone of {@code this}
		 *
		 * @see ICloneable
		 */
		@Override
		public Conqueror clone() {
			return new Conqueror();
		}
	}
}
