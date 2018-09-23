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
package jupiter.integration.gpu;

import static jupiter.common.io.IO.IO;
import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_ALLOC_HOST_PTR;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;
import static org.jocl.CL.setExceptionsEnabled;

import java.util.Map;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

import jupiter.common.io.file.FileHandler;
import jupiter.common.math.Maths;
import jupiter.common.struct.map.tree.RedBlackTreeMap;
import jupiter.common.test.Arguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.test.StringArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Characters;
import jupiter.common.util.Strings;

public class OpenCL {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether to use OpenCL.
	 */
	public static volatile boolean USE = false;

	protected static final String KERNEL_PREFIX = "__kernel void";
	protected static final String PROGRAM = "#pragma OPENCL EXTENSION cl_khr_fp64: enable\n" +
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
		try {
			CL = new OpenCL(PROGRAM);
		} catch (final IllegalStateException ex) {
			IO.error(ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	public volatile boolean use;

	protected cl_context context;
	protected cl_command_queue commandQueue;

	protected cl_program program;
	protected final Map<String, cl_kernel> kernels = new RedBlackTreeMap<String, cl_kernel>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public OpenCL(final FileHandler fileHandler) {
		this(fileHandler.read().getContent());
	}

	public OpenCL(final String sourceCode) {
		// Check the arguments
		StringArguments.requireNonEmpty(sourceCode);

		// Set the platform, the device type and the device number
		final int platformIndex = 0;
		final long deviceType = CL_DEVICE_TYPE_ALL;
		final int deviceIndex = 0;

		// Enable the exceptions (and subsequently omit the error checks)
		setExceptionsEnabled(true);

		// Obtain the number of platforms
		final int[] platformCountArray = new int[1];
		clGetPlatformIDs(0, null, platformCountArray);
		final int platformCount = platformCountArray[0];
		IO.debug("#Platforms: ", platformCount);
		if (platformCount == 0) {
			use = false;
			throw new IllegalStateException("There is no compatible OpenCL platform");
		}

		// Obtain a platform identifier
		final cl_platform_id[] platforms = new cl_platform_id[platformCount];
		clGetPlatformIDs(platforms.length, platforms, null);
		final cl_platform_id platform = platforms[platformIndex];

		// Initialize the context properties
		final cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

		// Obtain the number of devices for the platform
		final int[] deviceCountArray = new int[1];
		clGetDeviceIDs(platform, deviceType, 0, null, deviceCountArray);
		final int deviceCount = deviceCountArray[0];
		IO.debug("#Devices: ", deviceCount);
		if (platformCount == 0) {
			use = false;
			throw new IllegalStateException("There is no compatible OpenCL device");
		}

		// Obtain a device identifier
		final cl_device_id[] devices = new cl_device_id[deviceCount];
		clGetDeviceIDs(platform, deviceType, deviceCount, devices, null);
		final cl_device_id device = devices[deviceIndex];

		try {
			// Create a context for the selected device
			context = clCreateContext(contextProperties, 1, new cl_device_id[] {
				device
			}, null, null, null);

			// Create a command-queue for the selected device
			commandQueue = clCreateCommandQueue(context, device, 0, null);

			// Create the program from the source code
			program = clCreateProgramWithSource(context, 1, Arrays.toArray(sourceCode), null, null);

			// Build the program
			clBuildProgram(program, 0, null, null, null, null);

			// Build the kernels
			int index = -1;
			while ((index = sourceCode.indexOf(KERNEL_PREFIX, index + 1)) != -1) {
				final int fromIndex = index + KERNEL_PREFIX.length() + 1;
				final int toIndex = sourceCode.indexOf(Characters.LEFT_PARENTHESIS, index);
				final String name = sourceCode.substring(fromIndex, toIndex).trim();
				IO.debug("Build the kernel ", Strings.quote(name));
				kernels.put(name, build(name));
			}
			use = USE;
		} catch (final Exception ex) {
			use = false;
			release();
			throw new IllegalStateException(
					"There is a problem with the program: " + ex.getMessage());
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the context.
	 * <p>
	 * @return the context
	 */
	public cl_context getContext() {
		return context;
	}

	/**
	 * Returns the command-queue.
	 * <p>
	 * @return the command-queue
	 */
	public cl_command_queue getCommandQueue() {
		return commandQueue;
	}

	/**
	 * Returns the specified kernel.
	 * <p>
	 * @param name the kernel name
	 * <p>
	 * @return the specified kernel
	 */
	public cl_kernel getKernel(final String name) {
		return kernels.get(name);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public cl_mem createReadBuffer(final double[] array) {
		return clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
				array.length * Sizeof.cl_double, Pointer.to(array), null);
	}

	public cl_mem createWriteBuffer(final double[] array) {
		return clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_ALLOC_HOST_PTR,
				array.length * Sizeof.cl_double, Pointer.to(array), null);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public cl_kernel build(final String name) {
		return clCreateKernel(program, name, null);
	}

	public int execute(final cl_kernel kernel, final int globalWorkSize, final int localWorkSize) {
		return clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, new long[] {
			globalWorkSize
		}, new long[] {
			localWorkSize
		}, 0, null, null);
	}

	public int read(final cl_mem buffer, final double[] array) {
		return clEnqueueReadBuffer(commandQueue, buffer, CL_TRUE, 0,
				array.length * Sizeof.cl_double, Pointer.to(array), 0, null, null);
	}

	/**
	 * Releases the specified memory buffers.
	 * <p>
	 * @param buffers the array of {@link cl_mem} to release
	 */
	public void release(final cl_mem[] buffers) {
		for (final cl_mem buffer : buffers) {
			clReleaseMemObject(buffer);
		}
	}

	/**
	 * Releases the memory.
	 */
	public void release() {
		for (final cl_kernel kernel : kernels.values()) {
			clReleaseKernel(kernel);
		}
		kernels.clear();
		if (program != null) {
			clReleaseProgram(program);
			program = null;
		}
		if (commandQueue != null) {
			clReleaseCommandQueue(commandQueue);
			commandQueue = null;
		}
		if (context != null) {
			clReleaseContext(context);
			context = null;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public double[] plus(final double[] A, final double[] B) {
		return compute("plus", A, B);
	}

	public double[] minus(final double[] A, final double[] B) {
		return compute("minus", A, B);
	}

	public double[] times(final double[] A, final double[] B, final int aColumnDimension,
			final int bColumnDimension) {
		// Check the arguments
		OpenCLArguments.requireSameInnerDimension(aColumnDimension,
				Arguments.requireNonNull(B).length / bColumnDimension);

		// Initialize
		final int aRowDimension = A.length / aColumnDimension;
		final double[] result = new double[aRowDimension * bColumnDimension];
		final cl_mem[] buffers = new cl_mem[3];
		buffers[0] = createReadBuffer(A);
		buffers[1] = createReadBuffer(B);
		buffers[2] = createWriteBuffer(result);

		// Get the kernel
		final cl_kernel kernel = getKernel("times");
		// Set the kernel arguments
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(buffers[0]));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(buffers[1]));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(buffers[2]));
		clSetKernelArg(kernel, 3, Sizeof.cl_int, Pointer.to(new int[] {
			aColumnDimension
		}));
		clSetKernelArg(kernel, 4, Sizeof.cl_int, Pointer.to(new int[] {
			bColumnDimension
		}));
		// Execute the kernel
		execute(kernel, result.length, 1);
		// Read the result
		read(buffers[2], result);
		// Release the memory
		release(buffers);
		return result;
	}

	public double[] arrayTimes(final double[] A, final double[] B) {
		return compute("arrayTimes", A, B);
	}

	public double[] arrayDivision(final double[] A, final double[] B) {
		return compute("arrayDivision", A, B);
	}

	public double[] arrayLeftDivision(final double[] A, final double[] B) {
		return compute("arrayLeftDivision", A, B);
	}

	protected double[] compute(final String name, final double[] A, final double B[]) {
		// Check the arguments
		DoubleArguments.requireSameLength(A, B);

		// Initialize
		final double[] result = new double[A.length];
		final cl_mem[] buffers = new cl_mem[3];
		buffers[0] = createReadBuffer(A);
		buffers[1] = createReadBuffer(B);
		buffers[2] = createWriteBuffer(result);

		// Get the kernel
		final cl_kernel kernel = getKernel(name);
		// Set the kernel arguments
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(buffers[0]));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(buffers[1]));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(buffers[2]));
		// Execute the kernel
		execute(kernel, result.length, 1);
		// Read the result
		read(buffers[2], result);
		// Release the memory
		release(buffers);
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
	public double[] forward(final double[] A, final double[] B, final double[] C,
			final int aColumnDimension, final int bColumnDimension, final int cColumnDimension) {
		// Check the arguments
		OpenCLArguments.requireSameInnerDimension(aColumnDimension,
				Arguments.requireNonNull(B).length / bColumnDimension);

		// Initialize
		final int aRowDimension = A.length / aColumnDimension;
		final double[] result = new double[aRowDimension * bColumnDimension];
		final cl_mem[] buffers = new cl_mem[4];
		buffers[0] = createReadBuffer(A);
		buffers[1] = createReadBuffer(B);
		buffers[2] = createReadBuffer(C);
		buffers[3] = createWriteBuffer(result);

		// Get the kernel
		final cl_kernel kernel = getKernel("forward");
		// Set the kernel arguments
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(buffers[0]));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(buffers[1]));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(buffers[2]));
		clSetKernelArg(kernel, 3, Sizeof.cl_mem, Pointer.to(buffers[3]));
		clSetKernelArg(kernel, 4, Sizeof.cl_int, Pointer.to(new int[] {
			aColumnDimension
		}));
		clSetKernelArg(kernel, 5, Sizeof.cl_int, Pointer.to(new int[] {
			bColumnDimension
		}));
		clSetKernelArg(kernel, 6, Sizeof.cl_int, Pointer.to(new int[] {
			cColumnDimension
		}));
		// Execute the kernel
		execute(kernel, result.length, 1);
		// Read the result
		read(buffers[3], result);
		// Release the memory
		release(buffers);
		return result;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean test(final int rowDimension, final int innerDimension,
			final int columnDimension) {
		return use && Maths.maxToInt(rowDimension * innerDimension, rowDimension * columnDimension,
				innerDimension * columnDimension) > 1E5;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void finalize() {
		IO.debug(getClass().getSimpleName(), " is finalized");
		try {
			release();
		} finally {
			try {
				super.finalize();
			} catch (final Throwable ignored) {
			}
		}
	}
}
