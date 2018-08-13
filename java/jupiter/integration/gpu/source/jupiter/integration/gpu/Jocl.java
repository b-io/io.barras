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

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.setExceptionsEnabled;

import java.util.LinkedList;
import java.util.List;

import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

import jupiter.common.util.Arrays;

/*
 * JOCL - Java bindings for OpenCL
 *
 * Copyright 2009 Marco Hutter - http://www.jocl.org/
 */
public class Jocl {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final cl_context context;
	protected final cl_command_queue commandQueue;
	public final List<cl_kernel> kernels = new LinkedList<cl_kernel>();
	public final List<cl_program> programs = new LinkedList<cl_program>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public Jocl() {
		// The platform, device type and device number to use
		final int platformIndex = 0;
		final long deviceType = CL_DEVICE_TYPE_ALL;
		final int deviceIndex = 0;

		// Enable exceptions and subsequently omit error checks
		setExceptionsEnabled(true);

		// Obtain the number of platforms
		final int nPlatformsArray[] = new int[1];
		clGetPlatformIDs(0, null, nPlatformsArray);
		final int nPlatforms = nPlatformsArray[0];

		// Obtain a platform identifier
		final cl_platform_id platforms[] = new cl_platform_id[nPlatforms];
		clGetPlatformIDs(platforms.length, platforms, null);
		final cl_platform_id platform = platforms[platformIndex];

		// Initialize the context properties
		final cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

		// Obtain the number of devices for the platform
		final int nDevicesArray[] = new int[1];
		clGetDeviceIDs(platform, deviceType, 0, null, nDevicesArray);
		final int nDevices = nDevicesArray[0];

		// Obtain a device identifier
		final cl_device_id devices[] = new cl_device_id[nDevices];
		clGetDeviceIDs(platform, deviceType, nDevices, devices, null);
		final cl_device_id device = devices[deviceIndex];

		// Create a context for the selected device
		context = clCreateContext(contextProperties, 1, new cl_device_id[] {
			device
		}, null, null, null);

		// Create a command-queue for the selected device
		commandQueue = clCreateCommandQueue(context, device, 0, null);
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
	 * Returns the command queue.
	 * <p>
	 * @return the command queue
	 */
	public cl_command_queue getCommandQueue() {
		return commandQueue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Builds the specified source code and returns the kernel.
	 * <p>
	 * @param name       the name of the kernel
	 * @param sourceCode the source code to build
	 * <p>
	 * @return the kernel
	 */
	public cl_kernel build(final String name, final String sourceCode) {
		// Create the program from the source code
		final cl_program program = clCreateProgramWithSource(context, 1, Arrays.toArray(sourceCode),
				null, null);
		programs.add(program);

		// Build the program
		clBuildProgram(program, 0, null, null, null, null);

		// Create the kernel
		final cl_kernel kernel = clCreateKernel(program, name, null);
		kernels.add(kernel);

		return kernel;
	}

	public void clean() {
		for (final cl_kernel kernel : kernels) {
			clReleaseKernel(kernel);
		}
		for (final cl_program program : programs) {
			clReleaseProgram(program);
		}
		clReleaseCommandQueue(commandQueue);
		clReleaseContext(context);
	}
}
