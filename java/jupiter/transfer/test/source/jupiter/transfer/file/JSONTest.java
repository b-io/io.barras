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
package jupiter.transfer.file;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.BULLET;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jupiter.common.struct.map.tree.ComparableRedBlackTreeMap;
import jupiter.common.test.Test;
import jupiter.common.util.Arrays;
import jupiter.common.util.Integers;
import jupiter.common.util.Strings;

public class JSONTest
		extends Test {

	public JSONTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void testStringify() {
		IO.test(BULLET, " stringify");

		String result;
		// • Boolean
		final boolean primitiveBoolean = true;
		result = JSON.stringifyNode(primitiveBoolean);
		IO.test(result);
		assertEquals("true", result);
		// • Character
		final char primitiveCharacter = 'c';
		result = JSON.stringifyNode(primitiveCharacter);
		IO.test(result);
		assertEquals("c", result);
		// • Byte
		final byte primitiveByte = (byte) 42;
		result = JSON.stringifyNode(primitiveByte);
		IO.test(result);
		assertEquals("42", result);
		// • Short
		final short primitiveShort = (short) 42;
		result = JSON.stringifyNode(primitiveShort);
		IO.test(result);
		assertEquals("42", result);
		// • Integer
		final int primitiveInteger = 42;
		result = JSON.stringifyNode(primitiveInteger);
		IO.test(result);
		assertEquals("42", result);
		// • Long
		final long primitiveLong = 42L;
		result = JSON.stringifyNode(primitiveLong);
		IO.test(result);
		assertEquals("42", result);
		// • Float
		final float primitiveFloat = 42f;
		result = JSON.stringifyNode(primitiveFloat);
		IO.test(result);
		assertEquals("42", result);
		// • Double
		final double primitiveDouble = 42.;
		result = JSON.stringifyNode(primitiveDouble);
		IO.test(result);
		assertEquals("42", result);
		// • Primitive array
		final int[] primitiveSequence = Integers.createSequence(5);
		result = JSON.stringifyNode(primitiveSequence);
		IO.test(result);
		assertEquals("[0,1,2,3,4]", result);
		// • Array
		final Integer[] array = (Integer[]) Arrays.toArray(primitiveSequence);
		result = JSON.stringifyNode(array);
		IO.test(result);
		assertEquals("[0,1,2,3,4]", result);
		// • Collection
		final Collection<Integer> collection = Arrays.toList(array);
		result = JSON.stringifyNode(collection);
		IO.test(result);
		assertEquals("[0,1,2,3,4]", result);
		// • Map
		final Map<String, Integer> map = new ComparableRedBlackTreeMap<String, Integer>();
		for (int i = 0; i < 5; ++i) {
			map.put(Strings.toString(i), i);
		}
		result = JSON.stringifyNode(map);
		IO.test(result);
		assertEquals("[{\"0\":0},{\"1\":1},{\"2\":2},{\"3\":3},{\"4\":4}]", result);
		// • Object
		final Node object = new Node();
		result = JSON.stringifyNode(object);
		IO.test(result);
		assertEquals("{\"key\":\"value\",\"array\":[a,b,c],\"list\":[a,b,c]}", result);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Node {
		public String key = "value";
		public String[] array = new String[] {"a", "b", "c"};
		public List<String> list = Arrays.toList(array);
	}
}
