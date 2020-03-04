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

import static com.jogamp.opencl.CLMemory.Mem.READ_ONLY;
import static com.jogamp.opencl.CLMemory.Mem.READ_WRITE;
import static com.jogamp.opencl.CLMemory.Mem.WRITE_ONLY;
import static jupiter.common.io.IO.IO;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLContext;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLKernel;
import com.jogamp.opencl.CLProgram;

import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.Map;

import jupiter.common.math.Maths;
import jupiter.common.test.Arguments;
import jupiter.common.test.DoubleArguments;
import jupiter.common.util.Strings;

public class JogAmpl
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

	protected final CLContext context;
	protected final CLDevice device;
	protected final CLCommandQueue commandQueue;

	protected CLProgram program;
	protected final Map<String, CLKernel> kernels = new HashMap<String, CLKernel>();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link JogAmpl} with the specified source code {@link String}.
	 * <p>
	 * @param sourceCode the source code {@link String}
	 */
	public JogAmpl(final String sourceCode) {
		super(sourceCode);

		// Create the context for all the devices (use the default CLPlatform)
		context = CLContext.create();
		try {
			// Select the fastest device
			device = context.getMaxFlopsDevice();
			setDeviceInfo();

			// Create the command-queue for the selected device
			commandQueue = device.createCommandQueue();

			// Create the program from the source code
			program = context.createProgram(OpenCL.PROGRAM).build();

			// Build the kernels
			IO.debug("#Kernels: ", kernelNames.size());
			for (final String kernelName : kernelNames) {
				IO.debug("Build the kernel ", Strings.quote(kernelName));
				kernels.put(kernelName, program.createCLKernel(kernelName));
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
	public CLContext getContext() {
		return context;
	}

	/**
	 * Returns the command-queue.
	 * <p>
	 * @return the command-queue
	 */
	public CLCommandQueue getCommandQueue() {
		return commandQueue;
	}

	/**
	 * Returns the program.
	 * <p>
	 * @return the program
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
	public int getMaxWorkItemDimensions() {
		return device.getMaxWorkItemDimensions();
	}

	@Override
	public long getMaxWorkGroupSize() {
		return device.getMaxWorkGroupSize();
	}

	@Override
	public long getLocalMemorySize() {
		return device.getLocalMemSize();
	}

	@Override
	public long getGlobalMemorySize() {
		return device.getGlobalMemSize();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public CLBuffer<DoubleBuffer> createReadBuffer(final int length) {
		return context.createDoubleBuffer(length, READ_ONLY);
	}

	public CLBuffer<DoubleBuffer> createReadBuffer(final double[] array) {
		final CLBuffer<DoubleBuffer> buffer = context.createDoubleBuffer(array.length, READ_ONLY);
		fill(buffer.getBuffer(), array);
		return buffer;
	}

	public CLBuffer<DoubleBuffer> createWriteBuffer(final int length) {
		return context.createDoubleBuffer(length, WRITE_ONLY);
	}

	public CLBuffer<DoubleBuffer> createReadWriteBuffer(final double[] array) {
		final CLBuffer<DoubleBuffer> buffer = context.createDoubleBuffer(array.length, READ_WRITE);
		fill(buffer.getBuffer(), array);
		return buffer;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int count(final DoubleBuffer buffer) {
		int count = 0;
		while (buffer.remaining() > 0) {
			buffer.get();
			++count;
		}
		buffer.rewind();
		return count;
	}

	public static void fill(final DoubleBuffer buffer, final double[] array) {
		int i = 0;
		while (i < array.length && buffer.remaining() > 0) {
			buffer.put(array[i++]);
		}
		buffer.rewind();
	}

	/**
	 * Releases the memory.
	 */
	@Override
	public void release() {
		isActive = false;
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
		final double[] result = new double[A.length];
		final CLBuffer<DoubleBuffer> aBuffer = createReadBuffer(A);
		final CLBuffer<DoubleBuffer> bBuffer = createReadBuffer(B);
		final CLBuffer<DoubleBuffer> resultBuffer = createWriteBuffer(result.length);

		// Get the kernel
		final CLKernel kernel = getKernel(name);
		// Set the kernel arguments
		kernel.putArgs(aBuffer, bBuffer, resultBuffer);
		// Execute the kernel
		commandQueue.putWriteBuffer(aBuffer, false)
				.putWriteBuffer(bBuffer, false)
				.put1DRangeKernel(kernel, 0, Maths.roundUp(result.length, localWorkGroupSize),
						localWorkGroupSize)
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
		Arguments.requireNonNull(A, "array A");
		Arguments.requireNonNull(B, "array B");
		OpenCLArguments.requireSameInnerDimension(aColumnDimension, B.length / bColumnDimension);

		// Initialize
		final int aRowDimension = A.length / aColumnDimension;
		final double[] result = new double[aRowDimension * bColumnDimension];
		final CLBuffer<DoubleBuffer> aBuffer = createReadBuffer(A);
		final CLBuffer<DoubleBuffer> bBuffer = createReadBuffer(B);
		final CLBuffer<DoubleBuffer> resultBuffer = createWriteBuffer(result.length);

		// Get the kernel
		final CLKernel kernel = getKernel("times");
		// Set the kernel arguments
		kernel.putArgs(aBuffer, bBuffer, resultBuffer)
				.putArg(aColumnDimension)
				.putArg(bColumnDimension);
		// Execute the kernel
		commandQueue.putWriteBuffer(aBuffer, false)
				.putWriteBuffer(bBuffer, false)
				.put1DRangeKernel(kernel, 0, Maths.roundUp(result.length, localWorkGroupSize),
						localWorkGroupSize)
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
	public synchronized double[] forward(final double[] A, final double[] B, final double[] C,
			final int aColumnDimension, final int bColumnDimension, final int cColumnDimension) {
		// Check the arguments
		Arguments.requireNonNull(A, "array A");
		Arguments.requireNonNull(B, "array B");
		Arguments.requireNonNull(C, "array C");
		OpenCLArguments.requireSameInnerDimension(aColumnDimension, B.length / bColumnDimension);

		// Initialize
		final int aRowDimension = A.length / aColumnDimension;
		final double[] result = new double[aRowDimension * bColumnDimension];
		final CLBuffer<DoubleBuffer> aBuffer = createReadBuffer(A);
		final CLBuffer<DoubleBuffer> bBuffer = createReadBuffer(B);
		final CLBuffer<DoubleBuffer> cBuffer = createReadBuffer(C);
		final CLBuffer<DoubleBuffer> resultBuffer = createWriteBuffer(
				aRowDimension * bColumnDimension);

		// Get the kernel
		final CLKernel kernel = getKernel("forward");
		// Set the kernel arguments
		kernel.putArgs(aBuffer, bBuffer, cBuffer, resultBuffer)
				.putArg(aColumnDimension)
				.putArg(bColumnDimension)
				.putArg(cColumnDimension);
		// Execute the kernel
		commandQueue.putWriteBuffer(aBuffer, false)
				.putWriteBuffer(bBuffer, false)
				.putWriteBuffer(cBuffer, false)
				.put1DRangeKernel(kernel, 0, Maths.roundUp(result.length, localWorkGroupSize),
						localWorkGroupSize)
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
	public synchronized double[] arraySum(final double[] A, final double[] B, final double c,
			final int aOffset, final int bOffset, final int length) {
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
		final CLBuffer<DoubleBuffer> aBuffer = createReadWriteBuffer(aSlice);
		final CLBuffer<DoubleBuffer> bBuffer = createReadBuffer(bSlice);

		// Get the kernel
		final CLKernel kernel = getKernel("arraySum");
		// Set the kernel arguments
		kernel.putArgs(aBuffer, bBuffer).putArg(c);
		// Execute the kernel
		commandQueue.putWriteBuffer(aBuffer, false)
				.putWriteBuffer(bBuffer, false)
				.put1DRangeKernel(kernel, 0, Maths.roundUp(length, localWorkGroupSize),
						localWorkGroupSize)
				.putReadBuffer(aBuffer, true);
		// Read the result
		aBuffer.getBuffer().get(aSlice);
		// Release the memory
		aBuffer.release();
		bBuffer.release();
		// Copy the slice back into the array
		System.arraycopy(aSlice, 0, A, aOffset, length);
		return A;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public JogAmpl clone() {
		return new JogAmpl(sourceCode);
	}
}
