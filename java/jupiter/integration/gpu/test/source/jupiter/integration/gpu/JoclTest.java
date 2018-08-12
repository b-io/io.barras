/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clSetKernelArg;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;

import junit.framework.TestCase;
import jupiter.common.math.Maths;

public class JoclTest
		extends TestCase {

	public JoclTest() {
	}

	/**
	 * Test of class Jocl.
	 */
	public void testJocl() {
		IO.test("Jocl");

		// The source code of the OpenCL program to execute
		final String SOURCE_CODE = "__kernel void " + "test(__global const float *a," +
				"             __global const float *b," + "             __global float *c)" + "{" +
				"    int gid = get_global_id(0);" + "    c[gid] = a[gid] * b[gid];" + "}";

		// Initialize
		final Jocl jocl = new Jocl();

		// Create input and output data
		final int n = 10;
		final float sourceArrayA[] = new float[n];
		final float sourceArrayB[] = new float[n];
		final float targetArray[] = new float[n];
		for (int i = 0; i < n; i++) {
			sourceArrayA[i] = i;
			sourceArrayB[i] = i;
		}
		final Pointer sourceA = Pointer.to(sourceArrayA);
		final Pointer sourceB = Pointer.to(sourceArrayB);
		final Pointer target = Pointer.to(targetArray);

		// Allocate the memory objects for the input and output data
		final cl_mem buffers[] = new cl_mem[3];
		buffers[0] = clCreateBuffer(jocl.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
				Sizeof.cl_float * n, sourceA, null);
		buffers[1] = clCreateBuffer(jocl.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
				Sizeof.cl_float * n, sourceB, null);
		buffers[2] = clCreateBuffer(jocl.getContext(), CL_MEM_READ_WRITE, Sizeof.cl_float * n, null,
				null);

		// Create the kernel
		final cl_kernel kernel = jocl.build("test", SOURCE_CODE);

		// Set the arguments for the kernel
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(buffers[0]));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(buffers[1]));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(buffers[2]));

		// Set the work-item dimensions
		final long global_work_size[] = new long[] {
			n
		};
		final long local_work_size[] = new long[] {
			1
		};

		// Execute the kernel
		clEnqueueNDRangeKernel(jocl.getCommandQueue(), kernel, 1, null, global_work_size,
				local_work_size, 0, null, null);

		// Read the output data
		clEnqueueReadBuffer(jocl.getCommandQueue(), buffers[2], CL_TRUE, 0, n * Sizeof.cl_float,
				target, 0, null, null);

		// Clean
		clReleaseMemObject(buffers[0]);
		clReleaseMemObject(buffers[1]);
		clReleaseMemObject(buffers[2]);
		jocl.clean();

		// Verify the result
		boolean passed = true;
		final float epsilon = 1e-7f;
		for (int i = 0; i < n; i++) {
			final float x = targetArray[i];
			final float y = sourceArrayA[i] * sourceArrayB[i];
			final boolean epsilonEqual = Maths.delta(x, y) <= epsilon * Math.abs(x);
			if (!epsilonEqual) {
				passed = false;
				break;
			}
		}
		System.out.println("Test " + (passed ? "PASSED" : "FAILED"));
		if (n <= 10) {
			System.out.println("Result: " + java.util.Arrays.toString(targetArray));
		}
	}
}
