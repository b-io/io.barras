/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.transfer.file;

import static jupiter.common.io.IO.IO;

import jupiter.common.test.Test;
import jupiter.common.util.Arrays;
import jupiter.common.util.Integers;

public class JSONTest
		extends Test {

	public JSONTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void testJsonify() {
		IO.test("jsonify");

		String result;
		// - Boolean
		boolean primitiveBoolean = true;
		result = JSON.jsonifyNode(primitiveBoolean);
		IO.test(result);
		assertEquals("true", result);
		// - Character
		char primitiveCharacter = 'c';
		result = JSON.jsonifyNode(primitiveCharacter);
		IO.test(result);
		assertEquals("c", result);
		// - Byte
		byte primitiveByte = (byte) 42;
		result = JSON.jsonifyNode(primitiveByte);
		IO.test(result);
		assertEquals("42", result);
		// - Short
		short primitiveShort = (short) 42;
		result = JSON.jsonifyNode(primitiveShort);
		IO.test(result);
		assertEquals("42", result);
		// - Integer
		int primitiveInteger = 42;
		result = JSON.jsonifyNode(primitiveInteger);
		IO.test(result);
		assertEquals("42", result);
		// - Long
		long primitiveLong = 42L;
		result = JSON.jsonifyNode(primitiveLong);
		IO.test(result);
		assertEquals("42", result);
		// - Float
		float primitiveFloat = 42f;
		result = JSON.jsonifyNode(primitiveFloat);
		IO.test(result);
		assertEquals("42.0", result);
		// - Double
		double primitiveDouble = 42.;
		result = JSON.jsonifyNode(primitiveDouble);
		IO.test(result);
		assertEquals("42.0", result);
		// - Primitive array
		int[] primitiveSequence = Integers.createSequence(5);
		result = JSON.jsonifyNode(primitiveSequence);
		IO.test(result);
		assertEquals("[0,1,2,3,4]", result);
		// - Array
		Integer[] sequence = (Integer[]) Arrays.toArray(primitiveSequence);
		result = JSON.jsonifyNode(sequence);
		IO.test(result);
		assertEquals("[0,1,2,3,4]", result);
		// - Object
		Node object = new Node();
		result = JSON.jsonifyNode(object);
		IO.test(result);
		assertEquals("{\"key\":\"value\",\"this$0\":{}}", result);
	}

	protected class Node {
		public String key = "value";
	}
}
