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
package jupiter.common.map.remover;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Strings;

/**
 * {@link StringRemover} is the {@link ObjectToStringMapper} removing the specified {@code char}
 * tokens from an input {@link String}.
 */
public class StringRemover
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
	 * The {@link String} containing the {@code char} tokens to remove.
	 */
	protected final String tokens;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link StringRemover} with the specified {@code char} tokens.
	 * <p>
	 * @param tokens the {@code char} tokens to remove
	 */
	public StringRemover(final char... tokens) {
		super();
		this.tokens = Strings.join(tokens);
	}

	/**
	 * Constructs a {@link StringRemover} with the specified {@link String} containing the
	 * {@code char} tokens.
	 * <p>
	 * @param tokens the {@link String} containing the {@code char} tokens to remove
	 */
	public StringRemover(final String tokens) {
		super();
		this.tokens = tokens;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String call(final Object input) {
		if (input == null) {
			return null;
		}
		return Strings.removeAll(Strings.toString(input), tokens);
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
	public StringRemover clone() {
		return new StringRemover();
	}
}
