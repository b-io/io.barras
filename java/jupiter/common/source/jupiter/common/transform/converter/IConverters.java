/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2025 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.transform.converter;

public interface IConverters {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final BooleanConverter BOOLEAN_CONVERTER = new BooleanConverter();
	public static final ByteConverter BYTE_CONVERTER = new ByteConverter();
	public static final CharacterConverter CHARACTER_CONVERTER = new CharacterConverter();
	public static final DoubleConverter DOUBLE_CONVERTER = new DoubleConverter();
	public static final FloatConverter FLOAT_CONVERTER = new FloatConverter();
	public static final IntegerConverter INTEGER_CONVERTER = new IntegerConverter();
	public static final LongConverter LONG_CONVERTER = new LongConverter();
	public static final ShortConverter SHORT_CONVERTER = new ShortConverter();
	public static final StringConverter STRING_CONVERTER = new StringConverter();
}
