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

import jupiter.common.math.Interval;
import jupiter.common.math.Maths;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.tuple.Pair;
import jupiter.common.test.IntegerArguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

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
	 * The {@link LockedWorkQueue} used for dividing and conquering the execution.
	 */
	protected final LockedWorkQueue<Pair<I, Interval<Integer>>, Integer> workQueue;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link DivideAndConquer} of type {@code I}.
	 */
	public DivideAndConquer() {
		workQueue = new LockedWorkQueue<Pair<I, Interval<Integer>>, Integer>(new Conqueror());
	}

	/**
	 * Constructs a {@link DivideAndConquer} of type {@code I} with the specified minimum and
	 * maximum numbers of {@link Worker}.
	 * <p>
	 * @param minThreadCount the minimum number of {@link Worker} to handle
	 * @param maxThreadCount the maximum number of {@link Worker} to handle
	 */
	public DivideAndConquer(final int minThreadCount, final int maxThreadCount) {
		workQueue = new LockedWorkQueue<Pair<I, Interval<Integer>>, Integer>(new Conqueror(),
				minThreadCount, maxThreadCount);
	}

	/**
	 * Constructs a {@link DivideAndConquer} of type {@code I} with the specified minimum and
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
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Divides the execution between the specified indexes into execution slices and conquers them.
	 * Returns the exit code for each of them.
	 * <p>
	 * @param input     the {@code I} input to process
	 * @param fromIndex the index to start dividing from (inclusive)
	 * @param toIndex   the index to finish dividing at (exclusive)
	 * <p>
	 * @return {@code IO.EXIT_SUCCESS} if conquering the execution slice succeeds,
	 *         {@code IO.EXIT_FAILURE} otherwise for each of them
	 */
	public int[] divideAndConquer(final I input, final int fromIndex, final int toIndex) {
		return divideAndConquer(input, fromIndex, toIndex, 1);
	}

	/**
	 * Divides the execution between the specified indexes into execution slices and conquers them.
	 * Returns the exit code for each of them.
	 * <p>
	 * @param input        the {@code I} input to process
	 * @param fromIndex    the index to start dividing from (inclusive)
	 * @param toIndex      the index to finish dividing at (exclusive)
	 * @param minSliceSize the minimum execution slice size
	 * <p>
	 * @return {@code IO.EXIT_SUCCESS} if conquering the execution slice succeeds,
	 *         {@code IO.EXIT_FAILURE} otherwise for each of them
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
	 * Divides the execution between the specified indexes into the specified number of execution
	 * slices and returns the identifier of each of them.
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
		for (int i = 0; i < sliceCount - 1; ++i) {
			ids[i] = workQueue.submit(new Pair<I, Interval<Integer>>(input,
					new Interval<Integer>(i * sliceSize, (i + 1) * sliceSize)));
		}
		ids[sliceCount - 1] = workQueue.submit(new Pair<I, Interval<Integer>>(input,
				new Interval<Integer>((sliceCount - 1) * sliceSize, toIndex)));
		return ids;
	}

	//////////////////////////////////////////////

	/**
	 * Conquers the execution slices with the specified identifiers and returns the exit code for
	 * each of them.
	 * <p>
	 * @param ids the identifiers of the execution slices to conquer
	 * <p>
	 * @return {@code IO.EXIT_SUCCESS} if conquering the execution slice succeeds,
	 *         {@code IO.EXIT_FAILURE} otherwise for each of them
	 */
	protected int[] conquer(final long[] ids) {
		final int[] outputs = new int[ids.length];
		for (int i = 0; i < ids.length; ++i) {
			outputs[i] = workQueue.get(ids[i]);
		}
		return outputs;
	}

	/**
	 * Conquers the execution slice between the specified indexes with the specified {@code I} input
	 * and returns the exit code.
	 * <p>
	 * @param input     the {@code I} input to process
	 * @param fromIndex the index to start conquering from (inclusive)
	 * @param toIndex   the index to finish conquering at (exclusive)
	 * <p>
	 * @return {@code IO.EXIT_SUCCESS} if conquering the execution slice succeeds,
	 *         {@code IO.EXIT_FAILURE} otherwise
	 */
	protected int conquer(final I input, final int fromIndex, final int toIndex) {
		return conquer(input, new Interval<Integer>(fromIndex, toIndex));
	}

	/**
	 * Conquers the execution slice with the specified {@code I} input and {@link Interval} and
	 * returns the exit code.
	 * <p>
	 * @param input    the {@code I} input to process
	 * @param interval the {@link Interval} of {@link Integer} of the execution slice to conquer
	 * <p>
	 * @return {@code IO.EXIT_SUCCESS} if conquering the execution slice succeeds,
	 *         {@code IO.EXIT_FAILURE} otherwise
	 */
	protected abstract int conquer(final I input, final Interval<Integer> interval);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// POOL
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the {@link LockedWorkQueue} is running.
	 * <p>
	 * @return {@code true} if the {@link LockedWorkQueue} is running, {@code false} otherwise
	 */
	public boolean isRunning() {
		return workQueue.isRunning();
	}

	//////////////////////////////////////////////

	/**
	 * Shutdowns the {@link LockedWorkQueue}.
	 */
	public void shutdown() {
		workQueue.shutdown();
	}

	/**
	 * Shutdowns the {@link LockedWorkQueue}.
	 * <p>
	 * @param force the flag specifying whether to force shutdowning
	 */
	public void shutdown(final boolean force) {
		workQueue.shutdown(force);
	}

	//////////////////////////////////////////////

	/**
	 * Restarts the {@link LockedWorkQueue}.
	 */
	public void restart() {
		workQueue.restart();
	}

	/**
	 * Restarts the {@link LockedWorkQueue}.
	 * <p>
	 * @param force the flag specifying whether to force restarting
	 */
	public void restart(final boolean force) {
		workQueue.restart(force);
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
	public DivideAndConquer<I> clone() {
		try {
			return (DivideAndConquer<I>) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} is equal to {@code other}.
	 * <p>
	 * @param other the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
	 * <p>
	 * @throws ClassCastException   if the {@code other} type prevents it from being compared to
	 *                              {@code this}
	 * @throws NullPointerException if {@code other} is {@code null}
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

	/**
	 * Returns the hash code of {@code this}.
	 * <p>
	 * @return the hash code of {@code this}
	 *
	 * @see Object#equals(Object)
	 * @see System#identityHashCode
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
		return getClass().getSimpleName();
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
