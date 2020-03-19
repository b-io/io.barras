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
package jupiter.math.discrete.combinatorics;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.BULLET;

import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.test.Test;
import jupiter.common.util.Doubles;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class CombinatoricsTest
		extends Test {

	public CombinatoricsTest(final String name) {
		super(name);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests getPermutationIndex method, of class Combinatorics.
	 */
	public void testGetPermutationIndex() {
		IO.test(BULLET, " getPermutationIndex");

		// Initialize
		final int n = 4;
		final ExtendedLinkedList<Integer> sequence = Integers.toLinkedList(
				Integers.createSequence(n));

		IO.test("- List the permutations of a ", n, "-element set in lexicographic order:");
		final int[][] permutations = Combinatorics.createPermutations(n, true);
		for (final int[] permutation : permutations) {
			IO.test(Combinatorics.getPermutationIndex(permutation, sequence), ": ",
					Integers.toString(permutation));
		}
	}

	/**
	 * Tests getKPermutationIndex method, of class Combinatorics.
	 */
	public void testGetKPermutationIndex() {
		IO.test(BULLET, " getKPermutationIndex");

		// Initialize
		final int n = 4;
		final int[] ks = new int[] {2, 3};
		final ExtendedLinkedList<Integer> sequence = Integers.toLinkedList(
				Integers.createSequence(n));

		for (final int k : ks) {
			IO.test("- List the ", k, "-permutations of a ", n,
					"-element set in lexicographic order:");
			final int[][] permutations = Combinatorics.createKPermutations(n, k, true);
			for (final int[] permutation : permutations) {
				IO.test(Combinatorics.getKPermutationIndex(permutation, sequence), ": ",
						Integers.toString(permutation));
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests toFactoradic method, of class Combinatorics.
	 */
	public void testToFactoradic() {
		IO.test(BULLET, " toFactoradic");

		IO.test("- Get the factoradic representation of 463");
		ExtendedLinkedList<Integer> factoradicValue = Combinatorics.toFactoradic(463);
		IO.test(factoradicValue);
		assertEquals(Objects.hashCode(new int[] {3, 4, 1, 0, 1, 0}),
				Objects.hashCode(Integers.collectionToPrimitiveArray(factoradicValue)));

		IO.test("- Get the factoradic representation of 1234");
		factoradicValue = Combinatorics.toFactoradic(1234);
		IO.test(factoradicValue);
		assertEquals(Objects.hashCode(new int[] {1, 4, 1, 1, 2, 0, 0}),
				Objects.hashCode(Integers.collectionToPrimitiveArray(factoradicValue)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests createAllPermutations method, of class Combinatorics.
	 */
	public void testCreateAllPermutations() {
		IO.test(BULLET, " createAllPermutations");

		// Initialize
		final int n = 4;

		IO.test("- List all the permutations of a ", n, "-element set:");
		int[][] permutations = Combinatorics.createAllPermutations(n);
		for (final int[] permutation : permutations) {
			IO.test(Integers.toString(permutation));
		}

		IO.test("- List all the permutations of a ", n, "-element set in lexicographic order:");
		permutations = Combinatorics.createAllPermutations(n, true);
		for (final int[] permutation : permutations) {
			IO.test(Integers.toString(permutation));
		}
	}

	/**
	 * Tests createPermutations method, of class Combinatorics.
	 */
	public void testCreatePermutations() {
		IO.test(BULLET, " createPermutations");

		// Initialize
		final int n = 4;

		IO.test("- List the permutations of a ", n, "-element set:");
		int[][] permutations = Combinatorics.createPermutations(n);
		for (final int[] permutation : permutations) {
			IO.test(Integers.toString(permutation));
		}

		IO.test("- List the permutations of a ", n, "-element set in lexicographic order:");
		permutations = Combinatorics.createPermutations(n, true);
		for (final int[] permutation : permutations) {
			IO.test(Integers.toString(permutation));
		}
	}

	/**
	 * Tests createKPermutations method, of class Combinatorics.
	 */
	public void testCreateKPermutations() {
		IO.test(BULLET, " createKPermutations");

		// Initialize
		final int n = 4;
		final int[] ks = new int[] {2, 3};
		final int[] ms = new int[] {3, 2, 1};

		for (final int k : ks) {
			IO.test("- List the ", k, "-permutations of a ", n, "-element set:");
			final int[][] permutations = Combinatorics.createKPermutations(n, k);
			for (final int[] permutation : permutations) {
				IO.test(Integers.toString(permutation));
			}
		}

		for (final int k : ks) {
			IO.test("- List the ", k, "-permutations of a ", n,
					"-element multiset with multiplicities ", Integers.toString(ms),
					" in lexicographic order:");
			final int[][] permutations = Combinatorics.createKPermutations(k, ms);
			for (final int[] permutation : permutations) {
				IO.test(Integers.toString(permutation));
			}
		}
	}

	//////////////////////////////////////////////

	/**
	 * Tests createAllCombinations method, of class Combinatorics.
	 */
	public void testCreateAllCombinations() {
		IO.test(BULLET, " createAllCombinations");

		// Initialize
		final int n = 4;

		IO.test("- List all the combinations of a ", n, "-element set");
		int[][] combinations = Combinatorics.createAllCombinations(n);
		for (final int[] combination : combinations) {
			IO.test(Integers.toString(combination));
		}

		IO.test("- List all the combinations of a ", n, "-element set in lexicographic order:");
		combinations = Combinatorics.createAllCombinations(n, true);
		for (final int[] combination : combinations) {
			IO.test(Integers.toString(combination));
		}
	}

	/**
	 * Tests createKCombinations method, of class Combinatorics.
	 */
	public void testCreateKCombinations() {
		IO.test(BULLET, " createKCombinations");

		// Initialize
		final int n = 4;
		final int[] ks = new int[] {2, 3};
		final int[] ms = new int[] {3, 2, 1};

		for (final int k : ks) {
			IO.test("- List the ", k, "-combinations of a ", n,
					"-element set in lexicographic order:");
			final int[][] combinations = Combinatorics.createKCombinations(n, k);
			for (final int[] combination : combinations) {
				IO.test(Integers.toString(combination));
			}
		}

		for (final int k : ks) {
			IO.test("- List the ", k, "-combinations of a ", n,
					"-element multiset with multiplicities ", Integers.toString(ms),
					" in lexicographic order:");
			final int[][] combinations = Combinatorics.createKCombinations(k, ms);
			for (final int[] combination : combinations) {
				IO.test(Integers.toString(combination));
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests P method, of class Combinatorics.
	 */
	public void testP() {
		IO.test(BULLET, " P");

		{
			final int n = 10;
			IO.test("- Count the number of k-permutations of a ", n, "-element set");
			assertEquals(1, Combinatorics.P(n, 0));
			assertEquals(n, Combinatorics.P(n, 1));
			assertEquals(30240, Combinatorics.P(n, 5));
			assertEquals(3628800, Combinatorics.P(n, n));
		}

		{
			final long n = 20L;
			IO.test("- Count the number of k-permutations of a ", n, "-element set");
			assertEquals(1L, Combinatorics.P(n, 0L));
			assertEquals(n, Combinatorics.P(n, 1L));
			assertEquals(1860480L, Combinatorics.P(n, 5L));
			assertEquals(670442572800L, Combinatorics.P(n, 10L));
			assertEquals(2432902008176640000L, Combinatorics.P(n, n));
		}

		{
			final double n = 30.;
			IO.test("- Count the number of k-permutations of a ", n, "-element set");
			assertEquals(1., Combinatorics.P(n, 0.));
			assertEquals(n, Combinatorics.P(n, 1.));
			assertEquals(1.710072E7, Combinatorics.P(n, 5.));
			assertEquals(1.09027350432E14, Combinatorics.P(n, 10.));
			assertEquals(2.6525285981219103E32, Combinatorics.P(n, n));
		}
	}

	/**
	 * Tests PFR method, of class Combinatorics.
	 */
	public void testPRF() {
		IO.test(BULLET, " PFR");

		IO.test("- Count the number of anagrams of the word ", Strings.quote("MISSISSIPI"));
		assertEquals(34650, Combinatorics.PFR(new int[] {1, 4, 4, 2}));
		assertEquals(34650L, Combinatorics.PFR(Longs.asPrimitiveArray(1L, 4L, 4L, 2L)));
		assertEquals(34650., Combinatorics.PFR(Doubles.asPrimitiveArray(1., 4., 4., 2.)));

		final int[] ks = new int[] {0, 1, 4, 8, 11};
		final int[] ms = new int[] {1, 4, 4, 2};
		IO.test("- Count the number of words of length ", ks,
				" that can be formed with the letters of the word", Strings.quote("MISSISSIPI"));
		assertEquals(1, Combinatorics.PFR(ks[0], ms));
		assertEquals(4, Combinatorics.PFR(ks[1], ms));
		assertEquals(176, Combinatorics.PFR(ks[2], ms));
		assertEquals(10430, Combinatorics.PFR(ks[3], ms));
		assertEquals(34650, Combinatorics.PFR(ks[4], ms));
	}

	//////////////////////////////////////////////

	/**
	 * Tests C method, of class Combinatorics.
	 */
	public void testC() {
		IO.test(BULLET, " C");

		{
			final int n = 10;
			IO.test("- Count the number of k-combinations of a ", n, "-element set");
			assertEquals(1, Combinatorics.C(n, 0));
			assertEquals(n, Combinatorics.C(n, 1));
			assertEquals(252, Combinatorics.C(n, n / 2));
			assertEquals(1, Combinatorics.C(n, n));
		}

		{
			final long n = 20L;
			IO.test("- Count the number of k-combinations of a ", n, "-element set");
			assertEquals(1L, Combinatorics.C(n, 0L));
			assertEquals(n, Combinatorics.C(n, 1L));
			assertEquals(184756L, Combinatorics.C(n, n / 2L));
			assertEquals(1L, Combinatorics.C(n, n));
		}

		{
			final double n = 30.;
			IO.test("- Count the number of k-combinations of a ", n, "-element set");
			assertEquals(1., Combinatorics.C(n, 0.));
			assertEquals(n, Combinatorics.C(n, 1.));
			assertEquals(1.5511752E8, Combinatorics.C(n, n / 2.));
			assertEquals(1., Combinatorics.C(n, n));
		}
	}

	/**
	 * Tests CR method, of class Combinatorics.
	 */
	public void testCR() {
		IO.test(BULLET, " CR");

		// Count the number of ways to choose 3 donuts from 4 distinct categories with repetition
		assertEquals(20L, Combinatorics.CR(4L, 3L));
	}

	/**
	 * Tests CFR method, of class Combinatorics.
	 */
	public void testCFR() {
		IO.test(BULLET, " CFR");

		int k = 3;
		int[] ms = new int[] {2, 2};
		IO.test("- Count the number of ways to choose ", k, " balls from ", ms[0], " white and ",
				ms[1], " black balls");
		IO.test(Combinatorics.createKCombinations(k, ms));
		assertEquals(2, Combinatorics.CFR(k, ms));

		k = 9;
		ms = new int[] {6, 6};
		IO.test("- Count the number of ways to choose ", k, " balls from ", ms[0], " white and ",
				ms[1], " black balls");
		IO.test(Combinatorics.createKCombinations(k, ms));
		assertEquals(4, Combinatorics.CFR(k, ms));
	}
}
