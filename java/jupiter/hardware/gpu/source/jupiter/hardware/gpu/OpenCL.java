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
package jupiter.hardware.gpu;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Formats.NEWLINE;

import java.util.LinkedList;
import java.util.List;

import jupiter.common.math.Maths;
import jupiter.common.test.StringArguments;
import jupiter.common.util.Characters;

public abstract class OpenCL {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether OpenCL is active.
	 */
	public static volatile boolean ACTIVE = false;

	protected static final String KERNEL_PREFIX = "__kernel void";
	protected static final String PROGRAM = "#pragma OPENCL EXTENSION cl_khr_fp64: enable" +
			NEWLINE +
			"__kernel void plus(__global const double* A, __global const double* B," +
			"		__global double* C) {" +
			"	const int index = get_global_id(0);" +
			"	C[index] = A[index] + B[index];" +
			"}" +
			"__kernel void minus(__global const double* A, __global const double* B," +
			"		__global double* C) {" +
			"	const int index = get_global_id(0);" +
			"	C[index] = A[index] - B[index];" +
			"}" +
			"__kernel void times(__global const double* A, __global const double* B," +
			"		__global double* C, const int aColumnDimension, const int bColumnDimension) {" +
			"	const int index = get_global_id(0);" +
			"	const int aRowOffset = (index / bColumnDimension) * aColumnDimension;" +
			"	const int bColumnOffset = index % bColumnDimension;" +
			"	double sum = 0.;" +
			"	for (int k = 0; k < aColumnDimension; ++k) {" +
			"		sum += A[aRowOffset + k] * B[k * bColumnDimension + bColumnOffset];" +
			"	}" +
			"	C[index] = sum;" +
			"}" +
			"__kernel void arrayTimes(__global const double* A, __global const double* B," +
			"		__global double* C) {" +
			"	const int index = get_global_id(0);" +
			"	C[index] = A[index] * B[index];" +
			"}" +
			"__kernel void arrayDivision(__global const double* A, __global const double* B," +
			"		__global double* C) {" +
			"	const int index = get_global_id(0);" +
			"	C[index] = A[index] / B[index];" +
			"}" +
			"__kernel void arrayLeftDivision(__global const double* A, __global const double* B," +
			"		__global double* C) {" +
			"	const int index = get_global_id(0);" +
			"	C[index] = B[index] / A[index];" +
			"}" +
			"__kernel void forward(__global const double* A, __global const double* B," +
			"		__global double* C, __global double* D, const int aColumnDimension," +
			"		const int bColumnDimension, const int cColumnDimension) {" +
			"	const int index = get_global_id(0);" +
			"	const int aRowOffset = (index / bColumnDimension) * aColumnDimension;" +
			"	const int bColumnOffset = index % bColumnDimension;" +
			"	double sum = 0.;" +
			"	for (int k = 0; k < aColumnDimension; ++k) {" +
			"		sum += A[aRowOffset + k] * B[k * bColumnDimension + bColumnOffset];" +
			"	}" +
			"	D[index] = sum + C[index % cColumnDimension];" +
			"}";
	public static volatile OpenCL CL = null;

	static {
		if (ACTIVE) {
			try {
				CL = new JOCL(PROGRAM);
			} catch (final IllegalStateException ex) {
				ACTIVE = false;
				IO.error(ex);
			}
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether {@code this} is active.
	 */
	protected volatile boolean active;

	protected final String sourceCode;
	protected final List<String> kernelNames = new LinkedList<String>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected OpenCL(final String sourceCode) {
		if (!ACTIVE) {
			throw new IllegalStateException("OpenCL is not active");
		}

		// Check the arguments
		StringArguments.requireNonEmpty(sourceCode);

		// Set the attributes
		this.sourceCode = sourceCode;
		int index = -1;
		while ((index = sourceCode.indexOf(KERNEL_PREFIX, index + 1)) >= 0) {
			final int fromIndex = index + KERNEL_PREFIX.length() + 1;
			final int toIndex = sourceCode.indexOf(Characters.LEFT_PARENTHESIS, index);
			kernelNames.add(sourceCode.substring(fromIndex, toIndex).trim());
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean isActive() {
		return active;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public List<String> getKernels() {
		return kernelNames;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract String getDeviceName();

	public abstract int getMaxComputeUnits();

	public abstract long getMaxWorkItemDimensions();

	public abstract long getMaxWorkGroupSize();

	public abstract long getGlobalMemorySize();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public void setActive(final boolean active) {
		this.active = active;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract void release();

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected abstract double[] compute(final String name, final double[] A, final double B[]);

	public double[] plus(final double[] A, final double[] B) {
		return compute("plus", A, B);
	}

	public double[] minus(final double[] A, final double[] B) {
		return compute("minus", A, B);
	}

	public abstract double[] times(final double[] A, final double[] B, final int aColumnDimension,
			final int bColumnDimension);

	public double[] arrayTimes(final double[] A, final double[] B) {
		return compute("arrayTimes", A, B);
	}

	public double[] arrayDivision(final double[] A, final double[] B) {
		return compute("arrayDivision", A, B);
	}

	public double[] arrayLeftDivision(final double[] A, final double[] B) {
		return compute("arrayLeftDivision", A, B);
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
	 * @return {@code A . B + C}
	 */
	public abstract double[] forward(final double[] A, final double[] B, final double[] C,
			final int aColumnDimension, final int bColumnDimension, final int cColumnDimension);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean test(final int rowDimension, final int innerDimension,
			final int columnDimension) {
		return active && Maths.maxToInt(rowDimension * innerDimension,
				rowDimension * columnDimension, innerDimension * columnDimension) > 1E5;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void finalize() {
		IO.debug(this, " is finalized");
		try {
			release();
		} finally {
			try {
				super.finalize();
			} catch (final Throwable ignored) {
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
