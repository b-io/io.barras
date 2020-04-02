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
import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_GLOBAL_MEM_SIZE;
import static org.jocl.CL.CL_DEVICE_LOCAL_MEM_SIZE;
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

import java.util.Collection;

import jupiter.common.math.Maths;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.map.hash.ExtendedHashMap;
import jupiter.common.test.Arguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.util.Strings;

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

public class JOCL
		extends OpenCL {

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

	protected cl_device_id device;
	protected final cl_context context;
	protected final cl_command_queue commandQueue;

	protected final cl_program program;
	/**
	 * The {@link cl_kernel} associated to their names.
	 */
	protected final ExtendedHashMap<String, cl_kernel> kernels = new ExtendedHashMap<String, cl_kernel>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link JOCL} with the specified source code {@link String}.
	 * <p>
	 * @param sourceCode the source code {@link String}
	 */
	@SuppressWarnings("deprecation")
	public JOCL(final String sourceCode) {
		super(sourceCode);

		// Set the platform, the device type and number
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
			IS_ACTIVE = false;
			throw new IllegalStateException("There is no compatible OpenCL platform");
		}

		// Obtain the platform identifier
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
			IS_ACTIVE = false;
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
		setDeviceInfo();

		try {
			// Create the context for the selected device
			context = clCreateContext(contextProperties, 1, new cl_device_id[] {device}, null, null,
					null);

			// Create the command-queue for the selected device
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

			isActive = true;
		} catch (final Exception ex) {
			IS_ACTIVE = false;
			release();
			throw new IllegalStateException("There is a problem with the OpenCL program", ex);
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
	 * Returns the program.
	 * <p>
	 * @return the program
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
	public int getMaxWorkItemDimensions() {
		return getDeviceInfoHelperInt(device, CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
	}

	@Override
	public long getMaxWorkGroupSize() {
		return getDeviceInfoHelperLong(device, CL_DEVICE_MAX_WORK_GROUP_SIZE);
	}

	@Override
	public long getLocalMemorySize() {
		return getDeviceInfoHelperLong(device, CL_DEVICE_LOCAL_MEM_SIZE);
	}

	@Override
	public long getGlobalMemorySize() {
		return getDeviceInfoHelperLong(device, CL_DEVICE_GLOBAL_MEM_SIZE);
	}

	protected int getDeviceInfoHelperInt(final cl_device_id device_id, final int parameterName) {
		final int valueCount = 1;
		final int[] values = new int[valueCount];
		clGetDeviceInfo(device_id, parameterName, Sizeof.cl_int * valueCount, Pointer.to(values),
				null);
		return values[0];
	}

	protected long getDeviceInfoHelperLong(final cl_device_id device_id, final int parameterName) {
		final int valueCount = 1;
		final long[] values = new long[valueCount];
		clGetDeviceInfo(device_id, parameterName, Sizeof.cl_long * valueCount, Pointer.to(values),
				null);
		return values[0];
	}

	protected String getDeviceInfoHelperString(final cl_device_id device_id,
			final int parameterName) {
		// Obtain the length of the info text to query
		final long[] size = new long[1];
		clGetDeviceInfo(device_id, parameterName, 0, null, size);

		// Create the buffer of the appropriate size and fill it with the info text
		final byte[] buffer = new byte[(int) size[0]];
		clGetDeviceInfo(device_id, parameterName, buffer.length, Pointer.to(buffer), null);

		// Return the info text (create a string from the buffer excluding the trailing \0 byte)
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

	public cl_mem createReadWriteBuffer(final double[] array) {
		return clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_USE_HOST_PTR,
				array.length * Sizeof.cl_double, Pointer.to(array), null);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public cl_kernel build(final String name) {
		return clCreateKernel(program, name, null);
	}

	public int execute(final cl_kernel kernel, final long globalWorkSize,
			final long localWorkSize) {
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
	 */
	public void release(final cl_mem[] buffers) {
		for (final cl_mem buffer : buffers) {
			clReleaseMemObject(buffer);
		}
	}

	/**
	 * Releases the memory.
	 */
	@Override
	public void release() {
		isActive = false;
		final Collection<cl_kernel> ks = kernels.values();
		for (final cl_kernel k : ks) {
			clReleaseKernel(k);
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
		execute(kernel, Maths.roundUp(result.length, localWorkGroupSize), localWorkGroupSize);
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
		Arguments.requireNonNull(A, "array A");
		Arguments.requireNonNull(B, "array B");
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
		execute(kernel, Maths.roundUp(result.length, localWorkGroupSize), localWorkGroupSize);
		// Read the result
		read(buffers[2], result);
		// Release the memory
		release(buffers);
		return result;
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
	@Override
	public double[] forward(final double[] A, final double[] B, final double[] C,
			final int aColumnDimension, final int bColumnDimension, final int cColumnDimension) {
		// Check the arguments
		Arguments.requireNonNull(A, "array A");
		Arguments.requireNonNull(B, "array B");
		Arguments.requireNonNull(C, "array C");
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
		execute(kernel, Maths.roundUp(result.length, localWorkGroupSize), localWorkGroupSize);
		// Read the result
		read(buffers[3], result);
		// Release the memory
		release(buffers);
		return result;
	}

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
	@Override
	public double[] arraySum(final double[] A, final double[] B, final double c, final int aOffset,
			final int bOffset, final int length) {
		if (c == 0.) {
			return A;
		}

		// Check the arguments
		Arguments.requireNonNull(A, "array A");
		Arguments.requireNonNull(B, "array B");
		DoubleArguments.requireMinLength(A.length - aOffset, length);
		DoubleArguments.requireMinLength(B.length - bOffset, length);

		// Initialize
		final double[] aSlice = new double[length];
		System.arraycopy(A, aOffset, aSlice, 0, length);
		final double[] bSlice = new double[length];
		System.arraycopy(B, bOffset, bSlice, 0, length);
		final cl_mem[] buffers = new cl_mem[2];
		buffers[0] = createReadWriteBuffer(aSlice);
		buffers[1] = createReadBuffer(bSlice);

		// Get the kernel
		final cl_kernel kernel = getKernel("arraySum");
		// Set the kernel arguments
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(buffers[0]));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(buffers[1]));
		clSetKernelArg(kernel, 2, Sizeof.cl_double, Pointer.to(new double[] {c}));
		// Execute the kernel
		execute(kernel, Maths.roundUp(length, localWorkGroupSize), localWorkGroupSize);
		// Read the result
		read(buffers[0], aSlice);
		// Release the memory
		release(buffers);
		// Copy the slice back into the array
		System.arraycopy(aSlice, 0, A, aOffset, length);
		return A;
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
	public JOCL clone() {
		return new JOCL(sourceCode);
	}
}
