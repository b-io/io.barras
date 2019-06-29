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
package jupiter.common.util;

public class Bits {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final int INTEGER_BITS_COUNT = 32;
	public static final int HALF_INTEGER_BITS_COUNT = INTEGER_BITS_COUNT / 2;
	public static final long LONG_BITS_COUNT = 64L;
	public static final long HALF_LONG_BITS_COUNT = LONG_BITS_COUNT / 2L;

	public static volatile int[] SEEDS = Integers.createRandomSequence(128);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Bits}.
	 */
	protected Bits() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static long rotateLeft(final long bits) {
		return rotateLeft(bits, HALF_LONG_BITS_COUNT);
	}

	public static long rotateLeft(final long bits, final long k) {
		return bits >>> k | bits << LONG_BITS_COUNT - k;
	}

	public static int rotateLeft(final int bits) {
		return rotateLeft(bits, HALF_INTEGER_BITS_COUNT);
	}

	public static int rotateLeft(final int bits, final int k) {
		return bits >>> k | bits << INTEGER_BITS_COUNT - k;
	}

	public static long rotateRight(final long bits) {
		return rotateRight(bits, HALF_LONG_BITS_COUNT);
	}

	public static long rotateRight(final long bits, final long k) {
		return bits << k | bits >>> LONG_BITS_COUNT - k;
	}

	public static int rotateRight(final int bits) {
		return rotateRight(bits, HALF_INTEGER_BITS_COUNT);
	}

	public static int rotateRight(final int bits, final int k) {
		return bits << k | bits >>> INTEGER_BITS_COUNT - k;
	}
}
