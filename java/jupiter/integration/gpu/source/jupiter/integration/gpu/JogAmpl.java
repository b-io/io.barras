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

import static com.jogamp.opencl.CLMemory.Mem.READ_ONLY;
import static com.jogamp.opencl.CLMemory.Mem.WRITE_ONLY;
import static jupiter.common.io.IO.IO;

import java.nio.DoubleBuffer;
import java.util.Map;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;

import jupiter.common.math.Maths;
import jupiter.common.struct.map.tree.RedBlackTreeMap;
import jupiter.common.test.Arguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.util.Strings;

public class JogAmpl
		extends OpenCL {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final CLContext context;
	protected final CLDevice device;
	protected final CLCommandQueue commandQueue;

	protected CLProgram program;
	protected final Map<String, CLKernel> kernels = new RedBlackTreeMap<String, CLKernel>();

	protected final int localWorkGroupSize;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public JogAmpl(final String sourceCode) {
		super(sourceCode);

		// Create a context for all the devices (use the default CLPlatform)
		context = CLContext.create();
		try {
			// Select the fastest device
			device = context.getMaxFlopsDevice();
			IO.debug("Device name: ", getDeviceName());
			IO.debug("Max compute units: ", getMaxComputeUnits());
			IO.debug("Max work item dimensions: ", getMaxWorkItemDimensions());
			IO.debug("Max work group size: ", getMaxWorkGroupSize());
			IO.debug("Global memory size: ", getGlobalMemorySize());

			// Create a command-queue for the selected device
			commandQueue = device.createCommandQueue();

			// Create the program from the source code
			program = context.createProgram(OpenCL.PROGRAM).build();

			// Build the kernels
			IO.debug("#Kernels: ", kernelNames.size());
			for (final String kernelName : kernelNames) {
				IO.debug("Build the kernel ", Strings.quote(kernelName));
				kernels.put(kernelName, program.createCLKernel(kernelName));
			}

			// Set the local work group size
			localWorkGroupSize = Math.min(device.getMaxWorkGroupSize(), 256);
			IO.debug("Local work group size: ", localWorkGroupSize);

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
	 * <p>
	 * @since 1.6
	 */
	public CLContext getContext() {
		return context;
	}

	/**
	 * Returns the command-queue.
	 * <p>
	 * @return the command-queue
	 * <p>
	 * @since 1.6
	 */
	public CLCommandQueue getCommandQueue() {
		return commandQueue;
	}

	/**
	 * Returns the program.
	 * <p>
	 * @return the program
	 * <p>
	 * @since 1.6
	 */
	public CLProgram getProgram() {
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
	public CLKernel getKernel(final String name) {
		return kernels.get(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String getDeviceName() {
		return device.getName();
	}

	@Override
	public int getMaxComputeUnits() {
		return device.getMaxComputeUnits();
	}

	@Override
	public long getMaxWorkItemDimensions() {
		return device.getMaxWorkItemDimensions();
	}

	@Override
	public long getMaxWorkGroupSize() {
		return device.getMaxWorkGroupSize();
	}

	@Override
	public long getGlobalMemorySize() {
		return device.getGlobalMemSize();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public CLBuffer<DoubleBuffer> createReadBuffer(final int length) {
		return context.createDoubleBuffer(Maths.roundUp(length, localWorkGroupSize), READ_ONLY);
	}

	public CLBuffer<DoubleBuffer> createReadBuffer(final double[] array) {
		final CLBuffer<DoubleBuffer> buffer = context
				.createDoubleBuffer(Maths.roundUp(array.length, localWorkGroupSize), READ_ONLY);
		fill(buffer.getBuffer(), array);
		return buffer;
	}

	public CLBuffer<DoubleBuffer> createWriteBuffer(final int length) {
		return context.createDoubleBuffer(Maths.roundUp(length, localWorkGroupSize), WRITE_ONLY);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int count(final DoubleBuffer buffer) {
		int i = 0;
		while (buffer.remaining() > 0) {
			buffer.get();
			++i;
		}
		buffer.rewind();
		return i;
	}

	public static void fill(final DoubleBuffer buffer, final double[] array) {
		int i = 0;
		while (i < array.length && buffer.remaining() > 0) {
			buffer.put(array[i]);
			++i;
		}
		buffer.rewind();
	}

	/**
	 * Releases the memory.
	 * <p>
	 * @since 1.6
	 */
	@Override
	public void release() {
		for (final CLKernel kernel : kernels.values()) {
			kernel.release();
		}
		kernels.clear();
		program.release();
		commandQueue.release();
		context.release();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected synchronized double[] compute(final String name, final double[] A, final double B[]) {
		// Check the arguments
		DoubleArguments.requireSameLength(A, B);

		// Initialize
		final int resultLength = A.length;
		final double[] result = new double[resultLength];
		final CLBuffer<DoubleBuffer> aBuffer = createReadBuffer(A);
		final CLBuffer<DoubleBuffer> bBuffer = createReadBuffer(B);
		final CLBuffer<DoubleBuffer> resultBuffer = createWriteBuffer(resultLength);

		// Get the kernel
		final CLKernel kernel = getKernel(name);
		// Set the kernel arguments
		kernel.putArgs(aBuffer, bBuffer, resultBuffer);
		// Execute the kernel
		final int globalWorkGroupSize = Maths.roundUp(resultLength, localWorkGroupSize);
		commandQueue.putWriteBuffer(aBuffer, false).putWriteBuffer(bBuffer, false)
				.put1DRangeKernel(kernel, 0, globalWorkGroupSize, localWorkGroupSize)
				.putReadBuffer(resultBuffer, true);
		// Read the result
		resultBuffer.getBuffer().get(result);
		// Release the memory
		aBuffer.release();
		bBuffer.release();
		resultBuffer.release();
		return result;
	}

	@Override
	public synchronized double[] times(final double[] A, final double[] B,
			final int aColumnDimension, final int bColumnDimension) {
		// Check the arguments
		Arguments.requireNonNull(A);
		Arguments.requireNonNull(B);
		OpenCLArguments.requireSameInnerDimension(aColumnDimension, B.length / bColumnDimension);

		// Initialize
		final int aRowDimension = A.length / aColumnDimension;
		final int resultLength = aRowDimension * bColumnDimension;
		final double[] result = new double[resultLength];
		final CLBuffer<DoubleBuffer> aBuffer = createReadBuffer(A);
		final CLBuffer<DoubleBuffer> bBuffer = createReadBuffer(B);
		final CLBuffer<DoubleBuffer> resultBuffer = createWriteBuffer(resultLength);

		// Get the kernel
		final CLKernel kernel = getKernel("times");
		// Set the kernel arguments
		kernel.putArgs(aBuffer, bBuffer, resultBuffer).putArg(aColumnDimension)
				.putArg(bColumnDimension);
		// Execute the kernel
		final int globalWorkGroupSize = Maths.roundUp(Maths.maxToInt(A.length, B.length),
				localWorkGroupSize);
		commandQueue.putWriteBuffer(aBuffer, false).putWriteBuffer(bBuffer, false)
				.put1DRangeKernel(kernel, 0, globalWorkGroupSize, localWorkGroupSize)
				.putReadBuffer(resultBuffer, true);
		// Read the result
		resultBuffer.getBuffer().get(result);
		// Release the memory
		aBuffer.release();
		bBuffer.release();
		resultBuffer.release();
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
	public synchronized double[] forward(final double[] A, final double[] B, final double[] C,
			final int aColumnDimension, final int bColumnDimension, final int cColumnDimension) {
		// Check the arguments
		Arguments.requireNonNull(A);
		Arguments.requireNonNull(B);
		Arguments.requireNonNull(C);
		OpenCLArguments.requireSameInnerDimension(aColumnDimension, B.length / bColumnDimension);

		// Initialize
		final int aRowDimension = A.length / aColumnDimension;
		final int resultLength = aRowDimension * bColumnDimension;
		final double[] result = new double[resultLength];
		final CLBuffer<DoubleBuffer> aBuffer = createReadBuffer(A);
		final CLBuffer<DoubleBuffer> bBuffer = createReadBuffer(B);
		final CLBuffer<DoubleBuffer> cBuffer = createReadBuffer(C);
		final CLBuffer<DoubleBuffer> resultBuffer = createWriteBuffer(
				aRowDimension * bColumnDimension);

		// Get the kernel
		final CLKernel kernel = getKernel("forward");
		// Set the kernel arguments
		kernel.putArgs(aBuffer, bBuffer, cBuffer, resultBuffer).putArg(aColumnDimension)
				.putArg(bColumnDimension).putArg(cColumnDimension);
		// Execute the kernel
		final int globalWorkGroupSize = Maths.roundUp(Maths.maxToInt(A.length, B.length),
				localWorkGroupSize);
		commandQueue.putWriteBuffer(aBuffer, false).putWriteBuffer(bBuffer, false)
				.putWriteBuffer(cBuffer, false)
				.put1DRangeKernel(kernel, 0, globalWorkGroupSize, localWorkGroupSize)
				.putReadBuffer(resultBuffer, true);
		// Read the result
		resultBuffer.getBuffer().get(result);
		// Release the memory
		aBuffer.release();
		bBuffer.release();
		cBuffer.release();
		resultBuffer.release();
		return result;
	}
}
