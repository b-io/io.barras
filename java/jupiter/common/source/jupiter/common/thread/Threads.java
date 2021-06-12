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

import static jupiter.common.io.InputOutput.IO;

public class Threads {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default waiting time.
	 */
	public static final long DEFAULT_WAITING_TIME = 1000L; // [ms]


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Threads}.
	 */
	protected Threads() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void sleep() {
		sleep(DEFAULT_WAITING_TIME);
	}

	/**
	 * Causes the currently executing thread to sleep (temporarily cease execution) for the
	 * specified length of time (in milliseconds), subject to the precision and accuracy of system
	 * timers and schedulers. The thread does not lose ownership of any monitors.
	 * <p>
	 * @param time the length of time to sleep (in milliseconds)
	 * <p>
	 * @throws IllegalArgumentException if {@code time} is negative
	 */
	public static void sleep(final long time) {
		try {
			Thread.sleep(time);
		} catch (final InterruptedException ex) {
			IO.warn(ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link Thread}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link Thread},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof Thread;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link Thread}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link Thread},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return Thread.class.isAssignableFrom(c);
	}
}
