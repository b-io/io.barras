/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.io.string.Stringifiers.JSON;
import static jupiter.common.util.Characters.BULLET;
import static jupiter.common.util.Strings.EMPTY;

import java.util.List;
import java.util.Map;

import jupiter.common.struct.map.hash.ExtendedHashMap;
import jupiter.common.struct.map.tree.ComparableRedBlackTreeMap;
import jupiter.common.test.Test;
import jupiter.common.util.Arrays;
import jupiter.common.util.Integers;
import jupiter.common.util.Objects;

public class JSONTest
		extends Test {

	public JSONTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public void testStringify() {
		IO.test(BULLET, "stringify");

		String result;
		// • Boolean
		// - False
		result = JSON.stringifyNode(false);
		IO.test(result);
		assertEquals("false", result);
		// - True
		result = JSON.stringifyNode(true);
		IO.test(result);
		assertEquals("true", result);
		// • Character
		result = JSON.stringifyNode('c');
		IO.test(result);
		assertEquals("\"c\"", result);
		// • Byte
		result = JSON.stringifyNode((byte) 42);
		IO.test(result);
		assertEquals("42", result);
		// • Short
		result = JSON.stringifyNode((short) 42);
		IO.test(result);
		assertEquals("42", result);
		// • Integer
		result = JSON.stringifyNode(42);
		IO.test(result);
		assertEquals("42", result);
		// • Long
		result = JSON.stringifyNode(42L);
		IO.test(result);
		assertEquals("42", result);
		// • Float
		result = JSON.stringifyNode(42f);
		IO.test(result);
		assertEquals("42", result);
		// • Double
		result = JSON.stringifyNode(42.);
		IO.test(result);
		assertEquals("42", result);
		// • String
		result = JSON.stringifyNode(EMPTY);
		IO.test(result);
		assertEquals("\"\"", result);
		// • Primitive array
		final int[] primitiveSequence = Integers.createSequence(5);
		result = JSON.stringifyNode(primitiveSequence);
		IO.test(result);
		assertEquals("[0,1,2,3,4]", result);
		// • Array
		final Integer[] array = Integers.toArray(primitiveSequence);
		result = JSON.stringifyNode(array);
		IO.test(result);
		assertEquals("[0,1,2,3,4]", result);
		// • Collection
		result = JSON.stringifyNode(Arrays.toList(array));
		IO.test(result);
		assertEquals("[0,1,2,3,4]", result);
		// • Map
		final Map<String, Integer> map = new ComparableRedBlackTreeMap<String, Integer>();
		for (int i = 0; i < 5; ++i) {
			map.put(Objects.toString(i), i);
		}
		result = JSON.stringifyNode(map);
		IO.test(result);
		assertEquals("{\"0\":0,\"1\":1,\"2\":2,\"3\":3,\"4\":4}", result);
		// • Object
		// - Null
		result = JSON.stringifyNode(null);
		IO.test(result);
		assertEquals("null", result);
		// - Fields
		result = JSON.stringifyNode(new Node());
		IO.test(result);
		assertEquals("{\"key\":\"value\"," +
				"\"array\":[null,true,\"a\",0,\"a\"]," +
				"\"list\":[null,true,\"a\",0,\"a\"]," +
				"\"map\":{\"key\":\"value\"}}", result);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Node {
		public String key = "value";
		public Object[] array = new Object[] {null, true, 'a', 0, "a"};
		public List<Object> list = Arrays.toList(array);
		public Map<String, Object> map = new ExtendedHashMap<String, Object>(new String[] {"key"},
				new Object[] {"value"});
	}
}
