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
package jupiter.common.io;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class Charsets {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The US-ASCII {@link Charset}.
	 */
	public static final Charset US_ASCII = getCharset("US-ASCII");

	/**
	 * The Latin-1 Western European {@link Charset}.
	 */
	public static final Charset ISO_8859_1 = getCharset("ISO-8859-1");
	/**
	 * The Latin-2 Central European {@link Charset}.
	 */
	public static final Charset ISO_8859_2 = getCharset("ISO-8859-2");
	/**
	 * The Latin-3 South European {@link Charset}.
	 */
	public static final Charset ISO_8859_3 = getCharset("ISO-8859-3");
	/**
	 * The Latin-4 North European {@link Charset}.
	 */
	public static final Charset ISO_8859_4 = getCharset("ISO-8859-4");
	/**
	 * The Latin/Cyrillic {@link Charset}.
	 */
	public static final Charset ISO_8859_5 = getCharset("ISO-8859-5");
	/**
	 * The Latin/Arabic {@link Charset}.
	 */
	public static final Charset ISO_8859_6 = getCharset("ISO-8859-6");
	/**
	 * The Latin/Greek {@link Charset}.
	 */
	public static final Charset ISO_8859_7 = getCharset("ISO-8859-7");
	/**
	 * The Latin/Hebrew {@link Charset}.
	 */
	public static final Charset ISO_8859_8 = getCharset("ISO-8859-8");
	/**
	 * The Latin-5 Turkish {@link Charset}.
	 */
	public static final Charset ISO_8859_9 = getCharset("ISO-8859-9");
	/**
	 * The Latin-6 Nordic {@link Charset}.
	 */
	public static final Charset ISO_8859_10 = getCharset("ISO-8859-10");
	/**
	 * The Latin/Thai {@link Charset}.
	 */
	public static final Charset ISO_8859_11 = getCharset("ISO-8859-11");
	/**
	 * The Latin/Devanagari {@link Charset}.
	 */
	@Deprecated
	public static final Charset ISO_8859_12 = getCharset("ISO-8859-12");
	/**
	 * The Latin-7 Baltic Rim {@link Charset}.
	 */
	public static final Charset ISO_8859_13 = getCharset("ISO-8859-13");
	/**
	 * The Latin-8 Celtic {@link Charset}.
	 */
	public static final Charset ISO_8859_14 = getCharset("ISO-8859-14");
	/**
	 * The Latin-9 {@link Charset}.
	 */
	public static final Charset ISO_8859_15 = getCharset("ISO-8859-15");
	/**
	 * The Latin-10 South-Eastern European {@link Charset}.
	 */
	public static final Charset ISO_8859_16 = getCharset("ISO-8859-16");

	/**
	 * The Shift-JIS (Japanese) {@link Charset}.
	 */
	public static final Charset SHIFT_JIS = getCharset("Shift_JIS");
	/**
	 * The Windows code page 932 (Japanese) {@link Charset}.
	 */
	public static final Charset JAPANESE = getCharset("windows-31j");
	/**
	 * The Windows code page 949 (Korean) {@link Charset}.
	 */
	public static final Charset KOREAN = getCharset("x-windows-949");
	/**
	 * The Windows code page 936 (Simplified Chinese) {@link Charset}.
	 */
	public static final Charset SIMPLIFIED_CHINESE = getCharset("gbk");
	/**
	 * The Windows code page 950 (Traditional Chinese) {@link Charset}.
	 */
	public static final Charset TRADITIONAL_CHINESE = getCharset("x-windows-950");

	/**
	 * The UTF-8 {@link Charset}.
	 */
	public static final Charset UTF_8 = getCharset("UTF-8");
	/**
	 * The UTF-16 {@link Charset}.
	 */
	public static final Charset UTF_16 = getCharset("UTF-16");
	/**
	 * The UTF-16BE (big-endian) {@link Charset}.
	 */
	public static final Charset UTF_16BE = getCharset("UTF-16BE");
	/**
	 * The UTF-16LE (little-endian) {@link Charset}.
	 */
	public static final Charset UTF_16LE = getCharset("UTF-16LE");


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Charsets}.
	 */
	protected Charsets() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static Charset getCharset(final String name) {
		try {
			return Charset.forName(name);
		} catch (final UnsupportedCharsetException ignored) {
		}
		return null;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of bytes to encode a character with the specified {@link Charset}.
	 * <p>
	 * @param charset a {@link Charset}
	 * <p>
	 * @return the number of bytes to encode a character with the specified {@link Charset}
	 */
	public static int getCharSize(final Charset charset) {
		if (charset == Charsets.UTF_16 ||
				charset == Charsets.UTF_16BE || charset == Charsets.UTF_16LE) {
			return 2;
		}
		return 1;
	}
}
