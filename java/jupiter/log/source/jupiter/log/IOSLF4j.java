/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.log;

import java.util.List;

import jupiter.common.io.IOHandler;
import jupiter.common.io.InputOutput.SeverityLevel;
import jupiter.common.io.console.ConsoleHandler;
import jupiter.common.io.log.LogHandler;
import jupiter.common.model.ICloneable;

public class IOSLF4j
		extends IOLog4j {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The offset of the stack index.
	 */
	public static volatile int STACK_INDEX_OFFSET = 1;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link IOSLF4j} by default.
	 */
	public IOSLF4j() {
		this(DEFAULT_SEVERITY_LEVEL);
	}

	/**
	 * Constructs an {@link IOSLF4j} with the specified {@link SeverityLevel}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 */
	public IOSLF4j(final SeverityLevel severityLevel) {
		this(severityLevel, DEFAULT_STACK_INDEX);
	}

	/**
	 * Constructs an {@link IOSLF4j} with the specified {@link SeverityLevel} and stack index.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 */
	public IOSLF4j(final SeverityLevel severityLevel, final int stackIndex) {
		super(severityLevel, STACK_INDEX_OFFSET + stackIndex);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs an {@link IOSLF4j} with the specified {@link SeverityLevel}, stack index and
	 * {@link ConsoleHandler}.
	 * <p>
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param stackIndex     the stack index
	 * @param consoleHandler the {@link ConsoleHandler}
	 */
	public IOSLF4j(final SeverityLevel severityLevel, final int stackIndex,
			final ConsoleHandler consoleHandler) {
		super(severityLevel, STACK_INDEX_OFFSET + stackIndex, consoleHandler);
	}

	/**
	 * Constructs an {@link IOSLF4j} with the specified {@link SeverityLevel}, stack index and
	 * {@link LogHandler}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param logHandler    the {@link LogHandler}
	 */
	public IOSLF4j(final SeverityLevel severityLevel, final int stackIndex,
			final LogHandler logHandler) {
		super(severityLevel, STACK_INDEX_OFFSET + stackIndex, logHandler);
	}

	/**
	 * Constructs an {@link IOSLF4j} with the specified {@link SeverityLevel}, stack index,
	 * {@link ConsoleHandler} and {@link LogHandler}.
	 * <p>
	 * @param severityLevel  the {@link SeverityLevel}
	 * @param stackIndex     the stack index
	 * @param consoleHandler the {@link ConsoleHandler}
	 * @param logHandler     the {@link LogHandler}
	 */
	public IOSLF4j(final SeverityLevel severityLevel, final int stackIndex,
			final ConsoleHandler consoleHandler, final LogHandler logHandler) {
		super(severityLevel, STACK_INDEX_OFFSET + stackIndex, consoleHandler, logHandler);
	}

	/**
	 * Constructs an {@link IOSLF4j} with the specified {@link SeverityLevel}, stack index and
	 * {@link List} of {@link IOHandler}.
	 * <p>
	 * @param severityLevel the {@link SeverityLevel}
	 * @param stackIndex    the stack index
	 * @param handlers      the {@link List} of {@link IOHandler}
	 */
	public IOSLF4j(final SeverityLevel severityLevel, final int stackIndex,
			final List<IOHandler> handlers) {
		super(severityLevel, STACK_INDEX_OFFSET + stackIndex, handlers);
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
	public IOSLF4j clone() {
		return (IOSLF4j) super.clone();
	}
}
