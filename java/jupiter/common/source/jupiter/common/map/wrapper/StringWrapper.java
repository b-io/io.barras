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
package jupiter.common.map.wrapper;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * {@link StringWrapper} is the {@link ObjectToStringMapper} wrapping an input {@link String} with
 * the specified left and right {@code char} tokens.
 */
public class StringWrapper
		extends ObjectToStringMapper {

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

	/**
	 * The left wrapping {@link String}.
	 */
	protected final String left;
	/**
	 * The right wrapping {@link String}.
	 */
	protected final String right;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link StringWrapper} with the specified left and right wrapping {@code char}
	 * tokens.
	 * <p>
	 * @param left  the left wrapping {@code char} token
	 * @param right the right wrapping {@code char} token
	 */
	public StringWrapper(final char left, final char right) {
		super();
		this.left = Objects.toString(left);
		this.right = Objects.toString(right);
	}

	/**
	 * Constructs a {@link StringWrapper} with the specified left and right wrapping {@link String}.
	 * <p>
	 * @param left  the left wrapping {@link String}
	 * @param right the right wrapping {@link String}
	 */
	public StringWrapper(final String left, final String right) {
		super();
		this.left = left;
		this.right = right;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String call(final Object input) {
		return Strings.wrap(input, left, right);
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
	public StringWrapper clone() {
		return new StringWrapper(left, right);
	}
}
