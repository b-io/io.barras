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
package jupiter.integration.hardware.gpu;

import static jupiter.common.io.IO.IO;
import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_GLOBAL_MEM_SIZE;
import static org.jocl.CL.CL_DEVICE_MAX_COMPUTE_UNITS;
import static org.jocl.CL.CL_DEVICE_MAX_WORK_GROUP_SIZE;
import static org.jocl.CL.CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS;
import static org.jocl.CL.CL_DEVICE_NAME;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_ALLOC_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_MEM_USE_HOST_PTR;
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
import static org.jocl.CL.clGetDeviceInfo;
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

import jupiter.common.struct.map.tree.RedBlackTreeMap;
import jupiter.common.test.Arguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.util.Strings;

public class JOCL
		extends OpenCL {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected cl_device_id device;
	protected final cl_context context;
	protected final cl_command_queue commandQueue;

	protected final cl_program program;
	protected final Map<String, cl_kernel> kernels = new RedBlackTreeMap<String, cl_kernel>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public JOCL(final String sourceCode) {
		super(sourceCode);

		// Set the platform, the device type and the device number
		final int platformIndex = 0;
		final long deviceType = CL_DEVICE_TYPE_ALL;

		// Enable the exceptions (and subsequently omit the error checks)
		setExceptionsEnabled(true);

		// Obtain the number of platforms
		final int[] platformCountArray = new int[1];
		clGetPlatformIDs(0, null, platformCountArray);
		final int platformCount = platformCountArray[0];
		IO.debug("#Platforms: ", platformCount);
		if (platformCount == 0) {
			ACTIVE = false;
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
			ACTIVE = false;
			throw new IllegalStateException("There is no compatible OpenCL device");
		}

		// Select the fastest device
		final cl_device_id[] devices = new cl_device_id[deviceCount];
		clGetDeviceIDs(platform, deviceType, deviceCount, devices, null);
		int maxComputeUnitCount = 0;
		for (final cl_device_id d : devices) {
			final int computeUnitCount = getDeviceInfoHelperInt(d, CL_DEVICE_MAX_COMPUTE_UNITS);
			if (computeUnitCount > maxComputeUnitCount) {
				maxComputeUnitCount = computeUnitCount;
				device = d;
			}
		}
		IO.debug("Device name: ", getDeviceName());
		IO.debug("Max compute units: ", getMaxComputeUnits());
		IO.debug("Max work item dimensions: ", getMaxWorkItemDimensions());
		IO.debug("Max work group size: ", getMaxWorkGroupSize());
		IO.debug("Global memory size: ", getGlobalMemorySize());

		try {
			// Create a context for the selected device
			context = clCreateContext(contextProperties, 1, new cl_device_id[] {device}, null, null,
					null);

			// Create a command-queue for the selected device
			commandQueue = clCreateCommandQueue(context, device, 0, null);

			// Create the program from the source code
			program = clCreateProgramWithSource(context, 1, new String[] {sourceCode}, null, null);

			// Build the program
			clBuildProgram(program, 0, null, null, null, null);

			// Build the kernels
			IO.debug("#Kernels: ", kernelNames.size());
			for (final String kernelName : kernelNames) {
				IO.debug("Build the kernel ", Strings.quote(kernelName));
				kernels.put(kernelName, build(kernelName));
			}

			active = true;
		} catch (final Exception ex) {
			ACTIVE = false;
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
	 * <p>
	 * @since 1.6
	 */
	public cl_context getContext() {
		return context;
	}

	/**
	 * Returns the command-queue.
	 * <p>
	 * @return the command-queue
	 * <p>
	 * @since 1.6
	 */
	public cl_command_queue getCommandQueue() {
		return commandQueue;
	}

	/**
	 * Returns the program.
	 * <p>
	 * @return the program
	 * <p>
	 * @since 1.6
	 */
	public cl_program getProgram() {
		return program;
	}

	/**
	 * Returns the specified kernel.
	 * <p>
	 * @param name the kernel name
	 * <p>
	 * @return the specified kernel
	 * <p>
	 * @since 1.6
	 */
	public cl_kernel getKernel(final String name) {
		return kernels.get(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getDeviceName() {
		return getDeviceInfoHelperString(device, CL_DEVICE_NAME);
	}

	@Override
	public int getMaxComputeUnits() {
		return getDeviceInfoHelperInt(device, CL_DEVICE_MAX_COMPUTE_UNITS);
	}

	@Override
	public long getMaxWorkItemDimensions() {
		return getDeviceInfoHelperLong(device, CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
	}

	@Override
	public long getMaxWorkGroupSize() {
		return getDeviceInfoHelperLong(device, CL_DEVICE_MAX_WORK_GROUP_SIZE);
	}

	@Override
	public long getGlobalMemorySize() {
		return getDeviceInfoHelperLong(device, CL_DEVICE_GLOBAL_MEM_SIZE);
	}

	protected int getDeviceInfoHelperInt(final cl_device_id device_id, final int parameterName) {
		final int valueCount = 1;
		final int values[] = new int[valueCount];
		clGetDeviceInfo(device_id, parameterName, Sizeof.cl_int * valueCount, Pointer.to(values),
				null);
		return values[0];
	}

	protected long getDeviceInfoHelperLong(final cl_device_id device_id, final int parameterName) {
		final int valueCount = 1;
		final long values[] = new long[valueCount];
		clGetDeviceInfo(device_id, parameterName, Sizeof.cl_long * valueCount, Pointer.to(values),
				null);
		return values[0];
	}

	protected String getDeviceInfoHelperString(final cl_device_id device_id,
			final int parameterName) {
		// Obtain the length of the string to query
		final long size[] = new long[1];
		clGetDeviceInfo(device_id, parameterName, 0, null, size);

		// Create a buffer of the appropriate size and fill it with the info
		final byte buffer[] = new byte[(int) size[0]];
		clGetDeviceInfo(device_id, parameterName, buffer.length, Pointer.to(buffer), null);

		// Create a string from the buffer (excluding the trailing \0 byte)
		return new String(buffer, 0, buffer.length - 1);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public cl_mem createReadBuffer(final double[] array) {
		return clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR,
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
		return clEnqueueNDRangeKernel(commandQueue, kernel, 1, null, new long[] {globalWorkSize},
				new long[] {localWorkSize}, 0, null, null);
	}

	public int read(final cl_mem buffer, final double[] array) {
		return clEnqueueReadBuffer(commandQueue, buffer, CL_TRUE, 0,
				array.length * Sizeof.cl_double, Pointer.to(array), 0, null, null);
	}

	/**
	 * Releases the specified memory buffers.
	 * <p>
	 * @param buffers the array of {@link cl_mem} to release
	 * <p>
	 * @since 1.6
	 */
	public void release(final cl_mem[] buffers) {
		for (final cl_mem buffer : buffers) {
			clReleaseMemObject(buffer);
		}
	}

	/**
	 * Releases the memory.
	 * <p>
	 * @since 1.6
	 */
	@Override
	public void release() {
		active = false;
		for (final cl_kernel kernel : kernels.values()) {
			clReleaseKernel(kernel);
		}
		kernels.clear();
		clReleaseProgram(program);
		clReleaseCommandQueue(commandQueue);
		clReleaseContext(context);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
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

	@Override
	public double[] times(final double[] A, final double[] B, final int aColumnDimension,
			final int bColumnDimension) {
		// Check the arguments
		Arguments.requireNonNull(A);
		Arguments.requireNonNull(B);
		OpenCLArguments.requireSameInnerDimension(aColumnDimension, B.length / bColumnDimension);

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
		clSetKernelArg(kernel, 3, Sizeof.cl_int, Pointer.to(new int[] {aColumnDimension}));
		clSetKernelArg(kernel, 4, Sizeof.cl_int, Pointer.to(new int[] {bColumnDimension}));
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
	 * @return {@code A . B + C}
	 * <p>
	 * @since 1.6
	 */
	@Override
	public double[] forward(final double[] A, final double[] B, final double[] C,
			final int aColumnDimension, final int bColumnDimension, final int cColumnDimension) {
		// Check the arguments
		Arguments.requireNonNull(A);
		Arguments.requireNonNull(B);
		Arguments.requireNonNull(C);
		OpenCLArguments.requireSameInnerDimension(aColumnDimension, B.length / bColumnDimension);

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
		clSetKernelArg(kernel, 4, Sizeof.cl_int, Pointer.to(new int[] {aColumnDimension}));
		clSetKernelArg(kernel, 5, Sizeof.cl_int, Pointer.to(new int[] {bColumnDimension}));
		clSetKernelArg(kernel, 6, Sizeof.cl_int, Pointer.to(new int[] {cColumnDimension}));
		// Execute the kernel
		execute(kernel, result.length, 1);
		// Read the result
		read(buffers[3], result);
		// Release the memory
		release(buffers);
		return result;
	}
}