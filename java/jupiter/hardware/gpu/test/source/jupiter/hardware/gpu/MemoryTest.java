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
package jupiter.hardware.gpu;

import static jupiter.common.io.InputOutput.IO;
import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_FALSE;
import static org.jocl.CL.CL_MAP_READ;
import static org.jocl.CL.CL_MAP_WRITE;
import static org.jocl.CL.CL_MEM_ALLOC_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clEnqueueMapBuffer;
import static org.jocl.CL.clEnqueueUnmapMemObject;
import static org.jocl.CL.clEnqueueWriteBuffer;
import static org.jocl.CL.clFinish;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseMemObject;

import java.nio.ByteBuffer;
import java.util.Locale;

import jupiter.common.io.ProgressBar;
import jupiter.common.math.Maths;
import jupiter.common.test.Test;
import jupiter.common.time.Chronometer;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;

/**
 * A test for the bandwidth of the data transfer from the host to the device.
 */
public class MemoryTest
		extends Test {

	/**
	 * The number of memcopy operations to perform for each test.
	 */
	protected static final int MEMCOPY_ITERATION_COUNT = 10;

	/**
	 * The index of the OpenCL platform to use.
	 */
	protected static final int PLATFORM_INDEX = 0;

	/**
	 * The OpenCL device type to use.
	 */
	protected static final long DEVICE_TYPE = CL_DEVICE_TYPE_ALL;

	/**
	 * The index of the OpenCL device to use.
	 */
	protected static final int DEVICE_INDEX = 0;

	/**
	 * The OpenCL context.
	 */
	protected static volatile cl_context context;

	/**
	 * The OpenCL command queue.
	 */
	protected static volatile cl_command_queue commandQueue;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public MemoryTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The entry point of the memory transfer test.
	 */
	public static void test() {
		if (OpenCL.IS_ACTIVE) {
			initialize();
			final MemoryMode[] memoryModes = MemoryMode.values();
			final AccessMode[] accessModes = AccessMode.values();
			for (final MemoryMode memoryMode : memoryModes) {
				for (final AccessMode accessMode : accessModes) {
					runTest(memoryMode, accessMode);
				}
			}
			release();
		}
	}

	/**
	 * Runs a bandwidth test with the specified memory and access modes.
	 * <p>
	 * @param memoryMode The memory mode
	 * @param accessMode The access mode
	 */
	protected static void runTest(final MemoryMode memoryMode, final AccessMode accessMode) {
		final int minExponent = 10;
		final int maxExponent = 26;
		final int memorySizeCount = maxExponent - minExponent;
		final int[] memorySizes = new int[memorySizeCount];
		final double[] bandwidths = new double[memorySizeCount];

		final ProgressBar progressBar = new ProgressBar(5 * memorySizeCount);
		progressBar.start();
		for (int msi = 0; msi < memorySizeCount; ++msi) {
			progressBar.printSymbols(5);
			memorySizes[msi] = Maths.pow2(minExponent) + msi;
			final double bandwidth = computeBandwidth(memorySizes[msi], memoryMode, accessMode);
			bandwidths[msi] = bandwidth;
		}
		progressBar.end();

		IO.test("Bandwidths for", memoryMode, "and", accessMode);
		for (int msi = 0; msi < memorySizeCount; ++msi) {
			final String memorySize = String.format("%10d", memorySizes[msi]);
			final String bandwidth = String.format(Locale.ENGLISH, "%5.3f", bandwidths[msi]);
			IO.test(memorySize, "bytes :", bandwidth, "[MB/s]");
		}
	}

	/**
	 * Computes the bandwidth for copying a chunk of memory of the specified size from the host to
	 * the device with the specified memory and access modes.
	 * <p>
	 * @param memorySize The memory size, in bytes
	 * @param memoryMode The memory mode
	 * @param accessMode The access mode
	 * <p>
	 * @return the bandwidth
	 */
	protected static double computeBandwidth(final int memorySize, final MemoryMode memoryMode,
			final AccessMode accessMode) {
		ByteBuffer hostBuffer;
		cl_mem pinnedHostBuffer = null;
		cl_mem deviceBuffer;

		if (memoryMode == MemoryMode.PINNED) {
			// Allocate the pinned host memory
			pinnedHostBuffer = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_ALLOC_HOST_PTR,
					memorySize, null, null);

			// Map the host memory to the host address space
			hostBuffer = clEnqueueMapBuffer(commandQueue, pinnedHostBuffer, CL_TRUE, CL_MAP_WRITE,
					0, memorySize, 0, null, null, null);

			// Write data to the host memory
			for (int msi = 0; msi < memorySize; ++msi) {
				hostBuffer.put(msi, (byte) msi);
			}

			// Unmap the host memory, writing the data back to the pinned host memory
			clEnqueueUnmapMemObject(commandQueue, pinnedHostBuffer, hostBuffer, 0, null, null);
		} else {
			// Standard (pageable, non-pinned) allocation
			hostBuffer = ByteBuffer.allocateDirect(memorySize);

			// Write data to the host memory
			for (int msi = 0; msi < memorySize; ++msi) {
				hostBuffer.put(msi, (byte) msi);
			}
		}

		// Allocate the device memory
		deviceBuffer = clCreateBuffer(context, CL_MEM_READ_WRITE, memorySize, null, null);

		// Wait for all the queued commands to complete
		clFinish(commandQueue);

		// Start the chronometer
		final Chronometer chrono = new Chronometer();
		chrono.start();

		if (accessMode == AccessMode.DIRECT) {
			if (memoryMode == MemoryMode.PINNED) {
				// Map the host memory to the host address space
				hostBuffer = clEnqueueMapBuffer(commandQueue, pinnedHostBuffer, CL_TRUE,
						CL_MAP_READ, 0, memorySize, 0, null, null, null);
			}

			// Copy the data from the host memory to the device memory a few times
			for (int i = 0; i < MEMCOPY_ITERATION_COUNT; ++i) {
				clEnqueueWriteBuffer(commandQueue, deviceBuffer, CL_FALSE, 0, memorySize,
						Pointer.to(hostBuffer), 0, null, null);
			}

			// Wait for all the queued commands to complete
			clFinish(commandQueue);
		} else {
			// Map the device memory to the host addess space
			final ByteBuffer mappedDeviceBuffer = clEnqueueMapBuffer(commandQueue, deviceBuffer,
					CL_TRUE, CL_MAP_WRITE, 0, memorySize, 0, null, null, null);

			if (memoryMode == MemoryMode.PINNED) {
				// Map the host memory to the host address space
				hostBuffer = clEnqueueMapBuffer(commandQueue, pinnedHostBuffer, CL_TRUE,
						CL_MAP_READ, 0, memorySize, 0, null, null, null);
			}

			// Copy the data from the host memory to the device memory a few times
			for (int i = 0; i < MEMCOPY_ITERATION_COUNT; ++i) {
				mappedDeviceBuffer.put(hostBuffer);
				hostBuffer.position(0);
				mappedDeviceBuffer.position(0);
			}

			// Unmap the device memory, writing the data back to the pinned host memory
			clEnqueueUnmapMemObject(commandQueue, deviceBuffer, mappedDeviceBuffer, 0, null, null);
		}

		// Compute the bandwidth
		chrono.stop();
		final double duration = chrono.getSeconds();
		final double bandwidth = memorySize * MEMCOPY_ITERATION_COUNT / (duration * Maths.pow2(20)); // [MB/s]

		// Release the memory and return the bandwidth
		if (deviceBuffer != null) {
			clReleaseMemObject(deviceBuffer);
		}
		if (pinnedHostBuffer != null) {
			clEnqueueUnmapMemObject(commandQueue, pinnedHostBuffer, hostBuffer, 0, null, null);
			clReleaseMemObject(pinnedHostBuffer);
		}
		return bandwidth;
	}

	/**
	 * Performs a default initialization by creating a context and a command queue.
	 */
	@SuppressWarnings("deprecation")
	protected static void initialize() {
		// Enable exceptions and subsequently omit error checks in this sample
		CL.setExceptionsEnabled(true);

		// Obtain the number of platforms
		final int[] platformCountArray = new int[1];
		clGetPlatformIDs(0, null, platformCountArray);
		final int platformCount = platformCountArray[0];

		// Obtain the platform identifier
		final cl_platform_id[] platforms = new cl_platform_id[platformCount];
		clGetPlatformIDs(platforms.length, platforms, null);
		final cl_platform_id platform = platforms[PLATFORM_INDEX];

		// Initialize the context properties
		final cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

		// Obtain the number of devices for the platform
		final int[] deviceCountArray = new int[1];
		clGetDeviceIDs(platform, DEVICE_TYPE, 0, null, deviceCountArray);
		final int deviceCount = deviceCountArray[0];

		// Obtain the device identifier
		final cl_device_id[] devices = new cl_device_id[deviceCount];
		clGetDeviceIDs(platform, DEVICE_TYPE, deviceCount, devices, null);
		final cl_device_id device = devices[DEVICE_INDEX];

		// Create the context for the selected device
		context = clCreateContext(contextProperties, 1, new cl_device_id[] {device}, null, null,
				null);

		// Create the command-queue for the selected device
		commandQueue = clCreateCommandQueue(context, device, 0, null);
	}

	/**
	 * Releases the memory.
	 */
	protected static void release() {
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

	/**
	 * The memory access modes to test.
	 */
	public enum AccessMode {
		DIRECT,
		MAPPED
	}

	/**
	 * The host memory modes to test.
	 */
	public enum MemoryMode {
		PAGEABLE,
		PINNED
	}
}
