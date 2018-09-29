/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.integration.jni;

import static jupiter.common.io.IO.IO;

import jupiter.common.io.file.Files;
import jupiter.common.math.Maths;
import jupiter.common.test.Arguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.util.Formats;
import jupiter.common.util.Strings;

public class JNI {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether to use JNI.
	 */
	public static volatile boolean USE = true;
	/**
	 * The flag specifying whether {@code this} is loaded.
	 */
	public static volatile boolean IS_LOADED = false;

	public static volatile String LIB_DIR = Files.getPath() + "/lib";
	public static final String LIB_NAME = "jni-" + Formats.VERSION + ".dll";

	static {
		load();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected JNI() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean load() {
		return load(LIB_DIR + "/" + LIB_NAME);
	}

	public static boolean load(final String libPath) {
		if (USE) {
			if (!IS_LOADED) {
				try {
					System.load(libPath);
					IS_LOADED = true;
					return true;
				} catch (final Exception ex) {
					IO.error(ex);
				}
			} else {
				IO.warn("The library " + Strings.quote(libPath) + " is already loaded");
			}
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public native void dot();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static double[] plus(final double[] A, final double[] B) {
		return compute("plus", A, B);
	}

	public static double[] minus(final double[] A, final double[] B) {
		return compute("minus", A, B);
	}

	public static double[] times(final double[] A, final double[] B, final int aColumnDimension,
			final int bColumnDimension) {
		// Check the arguments
		JNIArguments.requireSameInnerDimension(aColumnDimension,
				Arguments.requireNonNull(B).length / bColumnDimension);

		// Initialize
		final int aRowDimension = A.length / aColumnDimension;
		final double[] result = new double[aRowDimension * bColumnDimension];

		// Execute the JNI function
		// TODO
		new JNI().dot();
		return result;
	}

	public static double[] arrayTimes(final double[] A, final double[] B) {
		return compute("arrayTimes", A, B);
	}

	public static double[] arrayDivision(final double[] A, final double[] B) {
		return compute("arrayDivision", A, B);
	}

	public static double[] arrayLeftDivision(final double[] A, final double[] B) {
		return compute("arrayLeftDivision", A, B);
	}

	protected static double[] compute(final String name, final double[] A, final double B[]) {
		// Check the arguments
		DoubleArguments.requireSameLength(A, B);

		// Initialize
		final double[] result = new double[A.length];

		// Execute the JNI function
		// TODO
		return result;
	}

	/**
	 * Returns the multiplication of {@code A} by {@code B} followed by the addition of {@code C}.
	 * <p>
	 * @param A                the array of {@code double} values to multiply
	 * @param B                the array of {@code double} values to multiply
	 * @param C                the array of {@code double} values to add
	 * @param aColumnDimension the column dimension of {@code A}
	 * @param bColumnDimension the column dimension of {@code B}
	 * @param cColumnDimension the column dimension of {@code C}
	 * <p>
	 * @return {@code A * B + C}
	 */
	public static double[] forward(final double[] A, final double[] B, final double[] C,
			final int aColumnDimension, final int bColumnDimension, final int cColumnDimension) {
		// Check the arguments
		JNIArguments.requireSameInnerDimension(aColumnDimension,
				Arguments.requireNonNull(B).length / bColumnDimension);

		// Initialize
		final int aRowDimension = A.length / aColumnDimension;
		final double[] result = new double[aRowDimension * bColumnDimension];

		// Execute the JNI function
		// TODO
		return result;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean test(final int rowDimension, final int innerDimension,
			final int columnDimension) {
		return USE && Maths.maxToInt(rowDimension * innerDimension, rowDimension * columnDimension,
				innerDimension * columnDimension) > 1E5; // TODO
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void finalize() {
		IO.debug(getClass().getSimpleName(), " is finalized");
		try {
			//release(); // TODO
		} finally {
			try {
				super.finalize();
			} catch (final Throwable ignored) {
			}
		}
	}
}
