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
import jupiter.common.struct.map.tree.RedBlackTreeMap;
import jupiter.common.test.Arguments;
import jupiter.common.test.FloatArguments;
import jupiter.common.test.StringArguments;
import jupiter.common.util.Arrays;
import jupiter.common.util.Characters;
import jupiter.common.util.Strings;

public class OpenCL {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final String KERNEL_PREFIX = "__kernel void";

	protected static final String PROGRAM = "" +
			"__kernel void plus(__global const float* A, __global const float* B, __global float* C) {" +
			"	const int index = get_global_id(0);" +
			"	C[index] = A[index] + B[index];" +
			"}" +
			"__kernel void minus(__global const float* A, __global const float* B, __global float* C) {" +
			"	const int index = get_global_id(0);" +
			"	C[index] = A[index] - B[index];" +
			"}" +
			"__kernel void times(__global const float* A, __global const float* B, __global float* C," +
			"		const int aColumnDimension, const int bColumnDimension) {" +
			"	const int index = get_global_id(0);" +
			"	const int rowOffset = index / bColumnDimension;" +
			"	const int columnOffset = index % bColumnDimension;" +
			"	float sum = 0.0;" +
			"	for (int i = 0; i < aColumnDimension; ++i) {" +
			"		sum += A[rowOffset * aColumnDimension + i] * B[i * bColumnDimension + columnOffset];" +
			"	}" +
			"	C[index] = sum;" +
			"}" +
			"__kernel void arrayTimes(__global const float* A, __global const float* B, __global float* C) {" +
			"	const int index = get_global_id(0);" +
			"	C[index] = A[index] * B[index];" +
			"}" +
			"__kernel void arrayDivision(__global const float* A, __global const float* B, __global float* C) {" +
			"	const int index = get_global_id(0);" +
			"	C[index] = A[index] / B[index];" +
			"}" +
			"__kernel void arrayLeftDivision(__global const float* A, __global const float* B, __global float* C) {" +
			"	const int index = get_global_id(0);" +
			"	C[index] = B[index] / A[index];" +
			"}";

	public static final OpenCL CL = new OpenCL(PROGRAM);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final cl_context context;
	protected final cl_command_queue commandQueue;

	protected final cl_program program;
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
		final int[] nPlatformsArray = new int[1];
		clGetPlatformIDs(0, null, nPlatformsArray);
		final int nPlatforms = nPlatformsArray[0];

		// Obtain a platform identifier
		final cl_platform_id[] platforms = new cl_platform_id[nPlatforms];
		clGetPlatformIDs(platforms.length, platforms, null);
		final cl_platform_id platform = platforms[platformIndex];

		// Initialize the context properties
		final cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

		// Obtain the number of devices for the platform
		final int[] nDevicesArray = new int[1];
		clGetDeviceIDs(platform, deviceType, 0, null, nDevicesArray);
		final int nDevices = nDevicesArray[0];

		// Obtain a device identifier
		final cl_device_id[] devices = new cl_device_id[nDevices];
		clGetDeviceIDs(platform, deviceType, nDevices, devices, null);
		final cl_device_id device = devices[deviceIndex];

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

	public cl_mem createReadBuffer(final float[] array) {
		return clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
				array.length * Sizeof.cl_float, Pointer.to(array), null);
	}

	public cl_mem createWriteBuffer(final float[] array) {
		return clCreateBuffer(context, CL_MEM_READ_WRITE, array.length * Sizeof.cl_float, null,
				null);
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

	public int read(final cl_mem buffer, final float[] array) {
		return clEnqueueReadBuffer(commandQueue, buffer, CL_TRUE, 0,
				array.length * Sizeof.cl_float, Pointer.to(array), 0, null, null);
	}

	public void release(final cl_mem[] buffers) {
		for (final cl_mem buffer : buffers) {
			clReleaseMemObject(buffer);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public float[] plus(final float[] A, final float[] B) {
		return arrayOperation("plus", A, B);
	}

	public float[] minus(final float[] A, final float[] B) {
		return arrayOperation("minus", A, B);
	}

	public float[] times(final float[] A, final float[] B, final int aColumnDimension,
			final int bColumnDimension) {
		// Check the arguments
		OpenCLArguments.requireSameInnerDimension(aColumnDimension,
				Arguments.requireNonNull(B).length / bColumnDimension);

		// Initialize
		final int aRowDimension = A.length / aColumnDimension;
		final float[] result = new float[aRowDimension * bColumnDimension];
		final cl_mem[] buffers = new cl_mem[3];
		buffers[0] = CL.createReadBuffer(A);
		buffers[1] = CL.createReadBuffer(B);
		buffers[2] = CL.createWriteBuffer(result);

		// Get the kernel
		final cl_kernel kernel = CL.getKernel("times");
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
		CL.execute(kernel, result.length, 1);
		// Read the result
		CL.read(buffers[2], result);
		// Release the memory
		CL.release(buffers);
		return result;
	}

	public float[] arrayTimes(final float[] A, final float[] B) {
		return arrayOperation("arrayTimes", A, B);
	}

	public float[] arrayDivision(final float[] A, final float[] B) {
		return arrayOperation("arrayDivision", A, B);
	}

	public float[] arrayLeftDivision(final float[] A, final float[] B) {
		return arrayOperation("arrayLeftDivision", A, B);
	}

	protected float[] arrayOperation(final String name, final float[] A, final float B[]) {
		// Check the arguments
		FloatArguments.requireSameLength(A, B);

		// Initialize
		final float[] result = new float[A.length];
		final cl_mem[] buffers = new cl_mem[3];
		buffers[0] = CL.createReadBuffer(A);
		buffers[1] = CL.createReadBuffer(B);
		buffers[2] = CL.createWriteBuffer(result);

		// Get the kernel
		final cl_kernel kernel = CL.getKernel(name);
		// Set the kernel arguments
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(buffers[0]));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(buffers[1]));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(buffers[2]));
		// Execute the kernel
		CL.execute(kernel, result.length, 1);
		// Read the result
		CL.read(buffers[2], result);
		// Release the memory
		CL.release(buffers);
		return result;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void finalize() {
		IO.debug(getClass().getSimpleName(), " is finalized");
		for (final cl_kernel kernel : kernels.values()) {
			clReleaseKernel(kernel);
		}
		clReleaseProgram(program);
		clReleaseCommandQueue(commandQueue);
		clReleaseContext(context);
	}
}
