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
package jupiter.math.discrete.combinatorics;

import static jupiter.common.io.IO.IO;

import edu.emory.mathcs.backport.java.util.Arrays;
import jupiter.common.test.Test;

public class CombinatoricsTest
		extends Test {

	public CombinatoricsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of getPermutations method, of class Combinatorics.
	 */
	public void testGetPermutations() {
		IO.test("• getPermutations");

		// Generate the permutations in lexicographical order
		int[][] permutations;
		IO.test("5-permutations of a 5-element set");
		permutations = Combinatorics.getPermutations(5);
		for (final int[] permutation : permutations) {
			IO.test(Arrays.toString(permutation));
		}
	}

	/**
	 * Test of getKPermutations method, of class Combinatorics.
	 */
	public void testGetKPermutations() {
		IO.test("• getKPermutations");

		// Generate the permutations in lexicographical order
		int[][] permutations;
		IO.test("3-permutations of a 5-element set");
		permutations = Combinatorics.getKPermutations(5, 3);
		for (final int[] permutation : permutations) {
			IO.test(Arrays.toString(permutation));
		}
	}

	//////////////////////////////////////////////

	/**
	 * Test of getAllCombinations method, of class Combinatorics.
	 */
	public void testGetAllCombinations() {
		IO.test("• getAllCombinations");

		// Generate all the combinations in lexicographical order
		int[][] combinations;
		IO.test("All combinations of a 3-element set");
		combinations = Combinatorics.getAllCombinations(3);
		for (final int[] combination : combinations) {
			IO.test(Arrays.toString(combination));
		}
	}

	/**
	 * Test of getKCombinations method, of class Combinatorics.
	 */
	public void testGetKCombinations() {
		IO.test("• getKCombinations");

		// Generate the k-combinations in lexicographical order
		int[][] combinations;
		IO.test("3-combinations of a 5-element set");
		combinations = Combinatorics.getKCombinations(5, 3);
		for (final int[] combination : combinations) {
			IO.test(Arrays.toString(combination));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Test of P method, of class Combinatorics.
	 */
	public void testP() {
		IO.test("• P");

		// Count the number of permutations of an 10-element set
		assertEquals(1L, Combinatorics.P(10L, 0L));
		assertEquals(10L, Combinatorics.P(10L, 1L));
		assertEquals(30240L, Combinatorics.P(10L, 5L));
		assertEquals(3628800L, Combinatorics.P(10L, 10L));
	}

	/**
	 * Test of PR method, of class Combinatorics.
	 */
	public void testPR() {
		IO.test("• PR");

		// Count the number of anagrams of the word "MISSISSIPPI"
		assertEquals(34650L, Combinatorics.PR(1L, 4L, 4L, 2L));
	}

	//////////////////////////////////////////////

	/**
	 * Test of C method, of class Combinatorics.
	 */
	public void testC() {
		IO.test("• C");

		// Count the number of combinations of an 10-element set
		assertEquals(1L, Combinatorics.C(10L, 0L));
		assertEquals(10L, Combinatorics.C(10L, 1L));
		assertEquals(252L, Combinatorics.C(10L, 5L));
		assertEquals(1L, Combinatorics.C(10L, 10L));
	}

	/**
	 * Test of CR method, of class Combinatorics.
	 */
	public void testCR() {
		IO.test("• CR");

		// Count the number of ways to choose 3 donuts from 4 distinct types with repetition
		assertEquals(20L, Combinatorics.CR(4L, 3L));

		// Count the number of ways to choose 3 donuts from 4 distinct types with repetition having
		// at least 1 of them from the first type
		assertEquals(10L, Combinatorics.CR(4L, 3L, 1L));
	}
}
