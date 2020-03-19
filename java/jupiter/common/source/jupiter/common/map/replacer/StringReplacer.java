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
package jupiter.common.map.replacer;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.model.ICloneable;
import jupiter.common.util.Strings;

/**
 * {@link StringReplacer} is the {@link ObjectToStringMapper} replacing the specified {@code char}
 * tokens from an input {@link String} with the specified {@link String}.
 */
public class StringReplacer
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
	 * The {@code char} tokens.
	 */
	protected final char[] tokens;
	/**
	 * The replacement {@link String}.
	 */
	protected final String replacement;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link StringReplacer} with the specified {@code char} tokens and replacement
	 * {@link String}.
	 * <p>
	 * @param tokens      the {@code char} tokens to replace
	 * @param replacement the {@link String} to replace with
	 */
	public StringReplacer(final char[] tokens, final String replacement) {
		super();
		this.tokens = tokens;
		this.replacement = replacement;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CALLABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public String call(final Object input) {
		return Strings.replaceAll(Strings.toString(input), tokens, replacement);
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
	public StringReplacer clone() {
		return new StringReplacer(tokens, replacement);
	}
}
