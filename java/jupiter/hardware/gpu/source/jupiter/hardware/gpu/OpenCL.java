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
package jupiter.hardware.gpu;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.LEFT_PARENTHESIS;
import static jupiter.common.util.Formats.NEW_LINE;

import java.io.Serializable;
import java.lang.ref.PhantomReference;
import java.lang.ref.WeakReference;
import java.util.List;

import jupiter.common.math.Maths;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.test.StringArguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public abstract class OpenCL
		implements ICloneable<OpenCL>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether OpenCL is active.
	 */
	public static volatile boolean IS_ACTIVE = false;

	/**
	 * The kernel prefix {@link String}.
	 */
	protected static final String KERNEL_PREFIX = "__kernel void";
	/**
	 * The OpenCL program {@link String}.
	 */
	protected static final String PROGRAM = Strings.join(
			"#pragma OPENCL EXTENSION cl_khr_fp64: enable", NEW_LINE,
			KERNEL_PREFIX, " plus(__global const double* A, __global const double* B,",
			"		__global double* C) {",
			"	const int index = get_global_id(0);",
			"	C[index] = A[index] + B[index];",
			"}",
			KERNEL_PREFIX, " minus(__global const double* A, __global const double* B,",
			"		__global double* C) {",
			"	const int index = get_global_id(0);",
			"	C[index] = A[index] - B[index];",
			"}",
			KERNEL_PREFIX, " times(__global const double* A, __global const double* B,",
			"		__global double* C, const int aColumnDimension, const int bColumnDimension) {",
			"	const int index = get_global_id(0);",
			"	const int aRowOffset = (index / bColumnDimension) * aColumnDimension;",
			"	const int bColumnOffset = index % bColumnDimension;",
			"	double sum = 0.;",
			"	for (int k = 0; k < aColumnDimension; ++k) {",
			"		sum += A[aRowOffset + k] * B[k * bColumnDimension + bColumnOffset];",
			"	}",
			"	C[index] = sum;",
			"}",
			KERNEL_PREFIX, " arrayTimes(__global const double* A, __global const double* B,",
			"		__global double* C) {",
			"	const int index = get_global_id(0);",
			"	C[index] = A[index] * B[index];",
			"}",
			KERNEL_PREFIX, " arrayDivision(__global const double* A, __global const double* B,",
			"		__global double* C) {",
			"	const int index = get_global_id(0);",
			"	C[index] = A[index] / B[index];",
			"}",
			KERNEL_PREFIX, " arrayLeftDivision(__global const double* A, __global const double* B,",
			"		__global double* C) {",
			"	const int index = get_global_id(0);",
			"	C[index] = B[index] / A[index];",
			"}",
			KERNEL_PREFIX, " forward(__global const double* A, __global const double* B,",
			"		__global const double* C, __global double* D, const int aColumnDimension,",
			"		const int bColumnDimension, const int cColumnDimension) {",
			"	const int index = get_global_id(0);",
			"	const int aRowOffset = (index / bColumnDimension) * aColumnDimension;",
			"	const int bColumnOffset = index % bColumnDimension;",
			"	double sum = 0.;",
			"	for (int k = 0; k < aColumnDimension; ++k) {",
			"		sum += A[aRowOffset + k] * B[k * bColumnDimension + bColumnOffset];",
			"	}",
			"	D[index] = sum + C[index % cColumnDimension];",
			"}",
			KERNEL_PREFIX, " arraySum(__global double* A, __global const double* B,",
			"		const double c) {",
			"	const int index = get_global_id(0);",
			"	A[index] += c * B[index];",
			"}");
	public static volatile OpenCL CL = null;

	static {
		if (IS_ACTIVE) {
			try {
				CL = new JOCL(PROGRAM);
			} catch (final IllegalStateException ex) {
				IS_ACTIVE = false;
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
	protected boolean isActive;

	/**
	 * The source code {@link String}.
	 */
	protected final String sourceCode;
	/**
	 * The {@link List} of kernel names.
	 */
	protected List<String> kernelNames = new ExtendedLinkedList<String>();

	/**
	 * The device local work group size.
	 */
	protected long localWorkGroupSize;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link OpenCL} with the specified source code {@link String}.
	 * <p>
	 * @param sourceCode the source code {@link String}
	 */
	protected OpenCL(final String sourceCode) {
		if (!IS_ACTIVE) {
			throw new IllegalStateException("OpenCL is not active");
		}

		// Check the arguments
		StringArguments.requireNonEmpty(sourceCode);

		// Set the attributes
		this.sourceCode = sourceCode;
		int index = -1;
		while ((index = sourceCode.indexOf(KERNEL_PREFIX, index + 1)) >= 0) {
			final int fromIndex = index + KERNEL_PREFIX.length() + 1;
			final int toIndex = sourceCode.indexOf(LEFT_PARENTHESIS, index);
			kernelNames.add(sourceCode.substring(fromIndex, toIndex).trim());
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the flag specifying whether {@code this} is active.
	 * <p>
	 * @return the flag specifying whether {@code this} is active
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Returns the source code {@link String}.
	 * <p>
	 * @return the source code {@link String}
	 */
	public String getSourceCode() {
		return sourceCode;
	}

	/**
	 * Returns the {@link List} of kernel names.
	 * <p>
	 * @return the {@link List} of kernel names
	 */
	public List<String> getKernelNames() {
		return kernelNames;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract String getDeviceName();

	public abstract int getMaxComputeUnits();

	public abstract int getMaxWorkItemDimensions();

	public abstract long getMaxWorkGroupSize();

	public abstract long getLocalMemorySize();

	public abstract long getGlobalMemorySize();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the flag specifying whether {@code this} is active.
	 * <p>
	 * @param isActive a {@code boolean} value
	 */
	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}

	public void setDeviceInfo() {
		IO.debug("Device name: ", getDeviceName());
		IO.debug("Max compute units: ", getMaxComputeUnits());
		IO.debug("Max work item dimensions: ", getMaxWorkItemDimensions());
		IO.debug("Max work group size: ", getMaxWorkGroupSize());
		IO.debug("Local memory size: ", getLocalMemorySize());
		IO.debug("Global memory size: ", getGlobalMemorySize());

		localWorkGroupSize = Math.min(getMaxWorkGroupSize(), 256L);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
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
	 * @param A                the {@code double} array to multiply
	 * @param B                the {@code double} array to multiply
	 * @param C                the {@code double} array to add
	 * @param aColumnDimension the column dimension of {@code A}
	 * @param bColumnDimension the column dimension of {@code B}
	 * @param cColumnDimension the column dimension of {@code C}
	 * <p>
	 * @return {@code A . B + C}
	 */
	// @todo slice the matrices so that they fit into the local memory of the GPU
	public abstract double[] forward(final double[] A, final double[] B, final double[] C,
			final int aColumnDimension, final int bColumnDimension, final int cColumnDimension);

	/**
	 * Adds the multiplication of {@code B} by {@code c} to {@code A}.
	 * <p>
	 * @param A       the {@code double} array to add
	 * @param B       the {@code double} array to multiply
	 * @param c       the constant {@code c} to multiply
	 * @param aOffset the offset of {@code A}
	 * @param bOffset the offset of {@code B}
	 * @param length  the length of the iteration
	 * <p>
	 * @return {@code A += c * B}
	 */
	public abstract double[] arraySum(final double[] A, final double[] B, final double c,
			final int aOffset, final int bOffset, final int length);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean test(final int aDimension, final int bDimension) {
		return isActive && aDimension * bDimension > 1E6;
	}

	public boolean test(final int rowDimension, final int innerDimension,
			final int columnDimension) {
		return isActive && Maths.maxToInt(rowDimension * innerDimension,
				rowDimension * columnDimension, innerDimension * columnDimension) > 1E6;
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
	public abstract OpenCL clone();

	/**
	 * Disposes of system resources and performs a cleanup.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>This method is called by the garbage collector on an {@link Object} when the garbage
	 * collection determines that there are no more references to the {@link Object}.</dd>
	 * </dl>
	 *
	 * @see PhantomReference
	 * @see WeakReference
	 */
	@Override
	@SuppressWarnings("deprecation")
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
}
