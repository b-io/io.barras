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
package jupiter.common.exception;

import java.io.IOException;

public class CopyFileException
		extends IOException {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link CopyFileException}.
	 */
	public CopyFileException() {
		super();
	}

	/**
	 * Constructs a {@link CopyFileException} with the specified message {@link String} (which is
	 * saved for later retrieval by the method {@link #getMessage}).
	 * <p>
	 * @param message the message {@link String} (which is saved for later retrieval by the method
	 *                {@link #getMessage})
	 */
	public CopyFileException(final String message) {
		super(message);
	}

	/**
	 * Constructs a {@link CopyFileException} with the specified message {@link String} (which is
	 * saved for later retrieval by the method {@link #getMessage}) and cause {@link Throwable}
	 * (which is saved for later retrieval by the method {@link #getCause}).
	 * <p>
	 * @param message the message {@link String} (which is saved for later retrieval by the method
	 *                {@link #getMessage})
	 * @param cause   the cause {@link Throwable} (which is saved for later retrieval by the method
	 *                {@link #getCause})
	 */
	public CopyFileException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
