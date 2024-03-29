/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.math.analysis.function.univariate.UnivariateFunctions.FACTORIAL;

import java.util.Iterator;
import java.util.List;

import jupiter.common.math.Maths;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.util.Arrays;
import jupiter.common.util.Booleans;
import jupiter.common.util.Bytes;
import jupiter.common.util.Characters;
import jupiter.common.util.Doubles;
import jupiter.common.util.Floats;
import jupiter.common.util.Integers;
import jupiter.common.util.Longs;
import jupiter.common.util.Shorts;

/**
 * {@link Combinatorics} is a collection of combinatorial functions.
 */
public class Combinatorics {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The left direction.
	 */
	protected static final boolean LEFT = false;
	/**
	 * The right direction.
	 */
	protected static final boolean RIGHT = true;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Combinatorics}.
	 */
	protected Combinatorics() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the factoradic representation of the specified permutation without repetition of the
	 * specified sequence.
	 * <p>
	 * @param permutationIndices an {@code int} array containing the permutation indices of the
	 *                           sequence
	 * @param sequence           an {@link ExtendedLinkedList} of {@link Integer}
	 * <p>
	 * @return the factoradic representation of the specified permutation without repetition of the
	 *         specified sequence
	 */
	public static ExtendedLinkedList<Integer> getPermutationFactoradic(
			final int[] permutationIndices, final ExtendedLinkedList<Integer> sequence) {
		// Initialize
		final int n = sequence.size();
		final int k = permutationIndices.length;
		final ExtendedLinkedList<Integer> s = sequence.clone();
		final ExtendedLinkedList<Integer> factoradicValue = new ExtendedLinkedList<Integer>();

		// Return the factoradic of the permutation
		for (final int permutationIndex : permutationIndices) {
			factoradicValue.add(s.removeFirst(permutationIndex));
		}
		for (int i = 0; i < n - k; ++i) {
			factoradicValue.add(0);
		}
		return factoradicValue;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the specified permutation without repetition of the specified sequence.
	 * <p>
	 * @param permutationIndices an {@code int} array containing the permutation indices of the
	 *                           sequence
	 * @param sequence           an {@link ExtendedLinkedList} of {@link Integer}
	 * <p>
	 * @return the index of the specified permutation without repetition of the specified sequence
	 */
	public static int getPermutationIndex(final int[] permutationIndices,
			final ExtendedLinkedList<Integer> sequence) {
		return toDecimal(getPermutationFactoradic(permutationIndices, sequence));
	}

	/**
	 * Returns the index of the specified {@code k}-permutation without repetition of the specified
	 * sequence.
	 * <p>
	 * @param permutationIndices an {@code int} array containing the {@code k}-permutation indices
	 *                           of the sequence
	 * @param sequence           an {@link ExtendedLinkedList} of {@link Integer}
	 * <p>
	 * @return the index of the specified {@code k}-permutation without repetition of the specified
	 *         sequence
	 */
	public static int getKPermutationIndex(final int[] permutationIndices,
			final ExtendedLinkedList<Integer> sequence) {
		// Initialize
		final int n = sequence.size();
		final int k = permutationIndices.length;
		final int divisor = getKPermutationDivisor(n, k);

		// Return the index of the k-permutation
		return getKPermutationIndex(permutationIndices, sequence, divisor);
	}

	/**
	 * Returns the index of the specified {@code k}-permutation without repetition of the specified
	 * sequence with the specified divisor.
	 * <p>
	 * @param permutationIndices an {@code int} array containing the {@code k}-permutation indices
	 *                           of the sequence
	 * @param sequence           an {@link ExtendedLinkedList} of {@link Integer}
	 * @param divisor            an {@code int} value
	 * <p>
	 * @return the index of the specified {@code k}-permutation without repetition of the specified
	 *         sequence with the specified divisor
	 */
	public static int getKPermutationIndex(final int[] permutationIndices,
			final ExtendedLinkedList<Integer> sequence, final int divisor) {
		return getPermutationIndex(permutationIndices, sequence) / divisor;
	}

	/**
	 * Returns the ratio between the numbers of {@code n}-permutations and {@code k}-permutations
	 * without repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the ordered subsets
	 * <p>
	 * @return the ratio between the numbers of {@code n}-permutations and {@code k}-permutations
	 *         without repetition
	 */
	public static int getKPermutationDivisor(final int n, final int k) {
		return P(n, n) / P(n, k);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the decimal representation of the specified factoradic representation.
	 * <p>
	 * @param factoradicValue the factoradic {@link List} of {@link Integer} to convert
	 * <p>
	 * @return the decimal representation of the specified factoradic representation
	 */
	public static int toDecimal(final List<Integer> factoradicValue) {
		// Initialize
		final int n = factoradicValue.size();
		final Iterator<Integer> factoradicValueIterator = factoradicValue.iterator();

		// Convert the factoradic value to the decimal representation
		int decimalValue = 0, i = 0;
		while (factoradicValueIterator.hasNext()) {
			decimalValue += factoradicValueIterator.next() * Maths.factorial(n - 1 - i++);
		}
		return decimalValue;
	}

	/**
	 * Returns the factoradic representation of the specified decimal representation.
	 * <p>
	 * @param decimalValue the decimal {@code int} value to convert
	 * <p>
	 * @return the factoradic representation of the specified decimal representation
	 */
	public static ExtendedLinkedList<Integer> toFactoradic(final int decimalValue) {
		// Initialize
		final ExtendedLinkedList<Integer> factoradicValue = new ExtendedLinkedList<Integer>();

		// Convert the decimal value to the factoradic representation
		int remainder, quotient = decimalValue, radix = 1;
		do {
			remainder = quotient % radix;
			quotient /= radix;
			factoradicValue.push(remainder);
			++radix;
		} while (quotient > 0);
		return factoradicValue;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the distinct ordered subsets of a {@code n}-element set, i.e. permutations
	 * without repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * <p>
	 * @return all the distinct ordered subsets of a {@code n}-element set, i.e. permutations
	 *         without repetition
	 */
	public static int[][] createAllPermutations(final int n) {
		return createAllPermutations(n, false);
	}

	/**
	 * Returns all the distinct ordered subsets of a {@code n}-element set (in lexicographic order
	 * if {@code sort}), i.e. permutations without repetition.
	 * <p>
	 * @param n    the number of elements in the set
	 * @param sort the flag specifying whether to sort in lexicographic order
	 * <p>
	 * @return all the distinct ordered subsets of a {@code n}-element set (in lexicographic order
	 *         if {@code sort}), i.e. permutations without repetition
	 */
	public static int[][] createAllPermutations(final int n, final boolean sort) {
		// Initialize
		int allPermutationCount = 0;
		for (int k = 0; k <= n; ++k) {
			allPermutationCount += P(n, k);
		}
		final int[][] allPermutations = new int[allPermutationCount][];

		// Generate all the permutations
		int api = 0;
		for (int k = 0; k <= n; ++k) {
			final int[][] permutations = createKPermutations(n, k, sort);
			for (final int[] permutation : permutations) {
				allPermutations[api++] = permutation;
			}
		}
		return allPermutations;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the distinct ordered {@code n}-element subsets of a {@code n}-element set, i.e.
	 * {@code n}-permutations without repetition.
	 * <p>
	 * @param n the number of elements in the set and in the ordered subsets
	 * <p>
	 * @return the distinct ordered {@code n}-element subsets of a {@code n}-element set, i.e.
	 *         {@code n}-permutations without repetition
	 */
	public static int[][] createPermutations(final int n) {
		return createPermutations(n, false);
	}

	/**
	 * Returns the distinct ordered {@code n}-element subsets of a {@code n}-element set (in
	 * lexicographic order if {@code sort}), i.e. {@code n}-permutations without repetition.
	 * <p>
	 * @param n    the number of elements in the set and in the ordered subsets
	 * @param sort the flag specifying whether to sort in lexicographic order
	 * <p>
	 * @return the distinct ordered {@code n}-element subsets of a {@code n}-element set (in
	 *         lexicographic order if {@code sort}), i.e. {@code n}-permutations without repetition
	 */
	public static int[][] createPermutations(final int n, final boolean sort) {
		// Initialize
		final int permutationCount = P(n, n);
		final int[][] permutations = new int[permutationCount][n];
		final int[] permutation = Integers.createSequence(n);
		final boolean[] directions = new boolean[n];

		// Generate the n-permutations (in lexicographic order if sort)
		if (sort) {
			Booleans.fill(directions, LEFT);
		}
		permutations[0] = permutation.clone();
		for (int pi = 1; pi < permutationCount; ++pi) {
			if (sort) {
				// Generate in lexicographic order
				int fromIndex = n - 1;
				while (permutation[fromIndex - 1] >= permutation[fromIndex]) {
					--fromIndex;
				}
				int toIndex = n - 1;
				final int pivot = permutation[fromIndex - 1];
				while (toIndex > fromIndex && permutation[toIndex] <= pivot) {
					--toIndex;
				}
				Integers.swap(permutation, fromIndex - 1, toIndex);
				Integers.reverse(permutation, fromIndex, permutation.length);
			} else {
				// Generate with minimal changes (use the Steinhaus-Johnson-Trotter algorithm)
				// • Find the largest mobile index
				int largestMobileIndex = -1, largestIndex = -1;
				for (int i = 0; i < n; ++i) {
					if (directions[i] == LEFT && i > 0 && permutation[i] > permutation[i - 1] ||
							directions[i] == RIGHT && i < n - 1 && permutation[i] > permutation[i + 1]) {
						if (permutation[i] > largestIndex) {
							largestMobileIndex = i;
							largestIndex = permutation[i];
						}
					}
				}
				// • Swap the largest mobile index and the adjacent index (on the left or right)
				final int offset = directions[largestMobileIndex] == LEFT ? -1 : 1;
				Integers.swap(permutation, largestMobileIndex, largestMobileIndex + offset);
				Booleans.swap(directions, largestMobileIndex, largestMobileIndex + offset);
				// • Reverse the direction of all the integers larger than the largest index
				for (int di = 0; di < directions.length; ++di) {
					if (permutation[di] > largestIndex) {
						directions[di] = !directions[di];
					}
				}
			}
			permutations[pi] = permutation.clone();
		}
		return permutations;
	}

	public static boolean[][] createPermutations(final boolean... array) {
		return Booleans.filterAll(array, createPermutations(array.length));
	}

	public static char[][] createPermutations(final char... array) {
		return Characters.filterAll(array, createPermutations(array.length));
	}

	public static byte[][] createPermutations(final byte... array) {
		return Bytes.filterAll(array, createPermutations(array.length));
	}

	public static short[][] createPermutations(final short... array) {
		return Shorts.filterAll(array, createPermutations(array.length));
	}

	public static int[][] createPermutations(final int... array) {
		return Integers.filterAll(array, createPermutations(array.length));
	}

	public static long[][] createPermutations(final long... array) {
		return Longs.filterAll(array, createPermutations(array.length));
	}

	public static float[][] createPermutations(final float... array) {
		return Floats.filterAll(array, createPermutations(array.length));
	}

	public static double[][] createPermutations(final double... array) {
		return Doubles.filterAll(array, createPermutations(array.length));
	}

	public static <T> T[][] createPermutations(final T[] array) {
		return Arrays.<T>filterAll(array, createPermutations(array.length));
	}

	//////////////////////////////////////////////

	/**
	 * Returns the distinct ordered {@code k}-element subsets of a {@code n}-element set, i.e.
	 * {@code k}-permutations without repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the ordered subsets
	 * <p>
	 * @return the distinct ordered {@code k}-element subsets of a {@code n}-element set, i.e.
	 *         {@code k}-permutations without repetition
	 */
	public static int[][] createKPermutations(final int n, final int k) {
		return createKPermutations(n, k, false);
	}

	/**
	 * Returns the distinct ordered {@code k}-element subsets of a {@code n}-element set (in
	 * lexicographic order if {@code sort}), i.e. {@code k}-permutations without repetition.
	 * <p>
	 * @param n    the number of elements in the set
	 * @param k    the number of elements in the ordered subsets
	 * @param sort the flag specifying whether to sort in lexicographic order
	 * <p>
	 * @return the distinct ordered {@code k}-element subsets of a {@code n}-element set (in
	 *         lexicographic order if {@code sort}), i.e. {@code k}-permutations without repetition
	 */
	public static int[][] createKPermutations(final int n, final int k, final boolean sort) {
		// Initialize
		final int permutationCount = P(n, k);
		final int[][] permutations = new int[permutationCount][k];
		final int[][] combinations = createKCombinations(n, k);

		// Generate the k-permutations (in lexicographic order if sort)
		if (sort) {
			final ExtendedLinkedList<Integer> sequence = Integers.toLinkedList(
					Integers.createSequence(n));
			final int divisor = getKPermutationDivisor(n, k);
			for (final int[] combination : combinations) {
				final int[][] subpermutations = createPermutations(combination);
				for (final int[] subpermutation : subpermutations) {
					permutations[getKPermutationIndex(subpermutation, sequence, divisor)] = subpermutation;
				}
			}
		} else {
			int pi = 0;
			for (final int[] combination : combinations) {
				final int[][] subpermutations = createPermutations(combination);
				for (final int[] subpermutation : subpermutations) {
					permutations[pi++] = subpermutation;
				}
			}
		}
		return permutations;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the distinct {@code k}-element tuples of a {@code n}-element multiset with the
	 * specified multiplicities {@code M} where {@code n = sum(M)} in lexicographic order, i.e.
	 * {@code k}-permutations with finite repetition.
	 * <p>
	 * {@code {}}
	 * {@code {0}            {1}            {2}}
	 * {@code {00} {01} {02} {10} {11} {12} {20} {21} {22}}
	 * {@code ...}
	 * <p>
	 * @param k the number of elements in the multisubsets
	 * @param M the multiplicities of the {@code n}-element multiset
	 * <p>
	 * @return the distinct {@code k}-element tuples of a {@code n}-element multiset with the
	 *         specified multiplicities {@code M} where {@code n = sum(M)} in lexicographic order,
	 *         i.e. {@code k}-permutations with finite repetition
	 */
	public static int[][] createKPermutations(final int k, final int[] M) {
		// Initialize
		final ExtendedLinkedList<int[]> permutations = new ExtendedLinkedList<int[]>(
				Integers.EMPTY_PRIMITIVE_ARRAY);

		// Create the k-permutations with the finite multiplicities
		while (permutations.getFirst().length < k) {
			final int[] permutation = permutations.remove();
			for (int i = 0; i < M.length; ++i) {
				if (Integers.count(permutation, i) < M[i]) {
					permutations.add(Integers.concat(permutation, i));
				}
			}
		}
		return permutations.toArray();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the distinct subsets of a {@code n}-element set, i.e. combinations without
	 * repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * <p>
	 * @return all the distinct subsets of a {@code n}-element set, i.e. combinations without
	 *         repetition
	 */
	public static int[][] createAllCombinations(final int n) {
		return createAllCombinations(n, false);
	}

	/**
	 * Returns all the distinct subsets of a {@code n}-element set (in lexicographic order if
	 * {@code sort}), i.e. combinations without repetition.
	 * <p>
	 * @param n    the number of elements in the set
	 * @param sort the flag specifying whether to sort in lexicographic order
	 * <p>
	 * @return all the distinct subsets of a {@code n}-element set (in lexicographic order if
	 *         {@code sort}), i.e. combinations without repetition
	 */
	public static int[][] createAllCombinations(final int n, final boolean sort) {
		// Initialize
		final int allCombinationCount = Maths.pow2(n);
		final int[][] allCombinations = new int[allCombinationCount][];

		// Generate all the combinations
		if (sort) {
			int aci = 0;
			for (int k = 0; k <= n; ++k) {
				final int[][] combinations = createKCombinations(n, k);
				for (final int[] combination : combinations) {
					allCombinations[aci++] = combination;
				}
			}
		} else {
			for (int aci = 0; aci < allCombinationCount; ++aci) {
				allCombinations[aci] = new int[Integer.bitCount(aci)];
				int ci = 0;
				for (int i = 0; i < n; ++i) {
					// Use the binary representation of c as a mask to select the elements
					if ((aci & Maths.pow2(i)) != 0) {
						allCombinations[aci][ci++] = i;
					}
				}
			}
		}
		return allCombinations;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the distinct {@code k}-element subsets of a {@code n}-element set in lexicographic
	 * order, i.e. {@code k}-combinations without repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the subsets
	 * <p>
	 * @return the distinct {@code k}-element subsets of a {@code n}-element set in lexicographic
	 *         order, i.e. {@code k}-combinations without repetition
	 */
	public static int[][] createKCombinations(final int n, final int k) {
		// Initialize
		final int combinationCount = C(n, k);
		final int[][] combinations = new int[combinationCount][k];
		final int[] combination = Integers.createSequence(k);

		// Generate the k-combinations in lexicographic order
		combinations[0] = combination.clone();
		for (int ci = 1; ci < combinationCount; ++ci) {
			int fromIndex = k - 1;
			while (fromIndex > 0 && combination[fromIndex] == n - k + fromIndex) {
				--fromIndex;
			}
			++combination[fromIndex];
			for (int i = fromIndex + 1; i < k; ++i) {
				combination[i] = combination[i - 1] + 1;
			}
			combinations[ci] = combination.clone();
		}
		return combinations;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the distinct {@code k}-element multisubsets of a {@code n}-element multiset with the
	 * specified multiplicities {@code M} where {@code n = sum(M)} in lexicographic order, i.e.
	 * {@code k}-combinations with finite repetition.
	 * <pre>
	 * {}
	 * {0}                                 {1}               {2}
	 * {00}              {01}        {02}  {11}        {12}  {22}
	 * {000} {001} {002} {011} {012} {022} {111} {112} {122} {222}
	 * ...
	 * </pre>
	 * <p>
	 * @param k the number of elements in the multisubsets
	 * @param M the multiplicities of the {@code n}-element multiset
	 * <p>
	 * @return the distinct {@code k}-element multisubsets of a {@code n}-element multiset with the
	 *         specified multiplicities {@code M} where {@code n = sum(M)} in lexicographic order,
	 *         i.e. {@code k}-combinations with finite repetition
	 */
	public static int[][] createKCombinations(final int k, final int[] M) {
		// Initialize
		final ExtendedLinkedList<int[]> combinations = new ExtendedLinkedList<int[]>(
				Integers.EMPTY_PRIMITIVE_ARRAY);

		// Create the k-combinations with the finite multiplicities
		while (combinations.getFirst().length < k) {
			final int[] combination = combinations.remove();
			final int from = combination.length == 0 ? 0 : combination[combination.length - 1];
			for (int i = from; i < M.length; ++i) {
				final int index = Integers.findFirstIndex(combination, i);
				if ((index < 0 ? 0 : combination.length - index) < M[i]) {
					combinations.add(Integers.concat(combination, i));
				}
			}
		}
		return combinations.toArray();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMBINATORIAL FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number {@code P(n, k)} of distinct ordered {@code k}-element subsets of a
	 * {@code n}-element set, i.e. {@code k}-permutations without repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the ordered subsets
	 * <p>
	 * @return the number {@code P(n, k)} of distinct ordered {@code k}-element subsets of a
	 *         {@code n}-element set, i.e. {@code k}-permutations without repetition
	 */
	public static int P(final int n, final int k) {
		if (k == 0 || n == 0) {
			return 1;
		}
		if (Maths.isFactorialInt(n)) {
			return Maths.factorial(n) / Maths.factorial(n - k);
		}
		return Maths.productSeries(n - k + 1, n);
	}

	/**
	 * Returns the number {@code P(n, k)} of distinct ordered {@code k}-element subsets of a
	 * {@code n}-element set, i.e. {@code k}-permutations without repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the ordered subsets
	 * <p>
	 * @return the number {@code P(n, k)} of distinct ordered {@code k}-element subsets of a
	 *         {@code n}-element set, i.e. {@code k}-permutations without repetition
	 */
	public static long P(final long n, final long k) {
		if (k == 0L || n == 0L) {
			return 1L;
		}
		if (Maths.isFactorialLong(n)) {
			return Maths.factorial(n) / Maths.factorial(n - k);
		}
		return Maths.productSeries(n - k + 1L, n);
	}

	/**
	 * Returns the number {@code P(n, k)} of distinct ordered {@code k}-element subsets of a
	 * {@code n}-element set, i.e. {@code k}-permutations without repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the ordered subsets
	 * <p>
	 * @return the number {@code P(n, k)} of distinct ordered {@code k}-element subsets of a
	 *         {@code n}-element set, i.e. {@code k}-permutations without repetition
	 */
	public static double P(final double n, final double k) {
		if (k == 0. || n == 0.) {
			return 1.;
		}
		if (Maths.isFactorialLong(n)) {
			return Maths.factorial(n) / Maths.factorial(n - k);
		}
		return Maths.productSeries(n - k + 1., n);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number {@code PR(n, k)} of distinct {@code k}-element tuples of objects from a
	 * {@code n}-element set, {@code k}-permutations with repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the tuples
	 * <p>
	 * @return the number {@code PR(n, k)} of distinct {@code k}-element tuples of objects from a
	 *         {@code n}-element set, {@code k}-permutations with repetition
	 */
	public static int PR(final int n, final int k) {
		return Maths.pow(n, k);
	}

	/**
	 * Returns the number {@code PR(n, k)} of distinct {@code k}-element tuples of objects from a
	 * {@code n}-element set, {@code k}-permutations with repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the tuples
	 * <p>
	 * @return the number {@code PR(n, k)} of distinct {@code k}-element tuples of objects from a
	 *         {@code n}-element set, {@code k}-permutations with repetition
	 */
	public static long PR(final long n, final long k) {
		return Maths.pow(n, k);
	}

	/**
	 * Returns the number {@code PR(n, k)} of distinct {@code k}-element tuples of objects from a
	 * {@code n}-element set, {@code k}-permutations with repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the tuples
	 * <p>
	 * @return the number {@code PR(n, k)} of distinct {@code k}-element tuples of objects from a
	 *         {@code n}-element set, {@code k}-permutations with repetition
	 */
	public static double PR(final double n, final double k) {
		return Maths.pow(n, k);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number {@code PFR(M)} of distinct {@code n}-element tuples of objects from a
	 * {@code n}-element multiset with the specified multiplicities {@code M} where
	 * {@code n = sum(M)}, i.e. {@code n}-permutations with finite repetition.
	 * <p>
	 * @param M the multiplicities of the {@code n}-element multiset
	 * <p>
	 * @return the number {@code PFR(M)} of distinct {@code n}-element tuples of objects from a
	 *         {@code n}-element multiset with the specified multiplicities {@code M} where
	 *         {@code n = sum(M)}, i.e. {@code n}-permutations with finite repetition
	 */
	public static int PFR(final int[] M) {
		final int n = Maths.sum(M);
		if (n == 0) {
			return 1;
		}
		int result = Maths.productSeries(M[0] + 1, n);
		for (final int m : M) {
			result /= Maths.factorial(m);
		}
		return result;
	}

	/**
	 * Returns the number {@code PFR(M)} of distinct {@code n}-element tuples of objects from a
	 * {@code n}-element multiset with the specified multiplicities {@code M} where
	 * {@code n = sum(M)}, i.e. {@code n}-permutations with finite repetition.
	 * <p>
	 * @param M the multiplicities of the {@code n}-element multiset
	 * <p>
	 * @return the number {@code PFR(M)} of distinct {@code n}-element tuples of objects from a
	 *         {@code n}-element multiset with the specified multiplicities {@code M} where
	 *         {@code n = sum(M)}, i.e. {@code n}-permutations with finite repetition
	 */
	public static long PFR(final long[] M) {
		final long n = Maths.sum(M);
		if (n == 0L) {
			return 1L;
		}
		long result = Maths.productSeries(M[0] + 1L, n);
		for (final long m : M) {
			result /= Maths.factorial(m);
		}
		return result;
	}

	/**
	 * Returns the number {@code PFR(M)} of distinct {@code n}-element tuples of objects from a
	 * {@code n}-element multiset with the specified multiplicities {@code M} where
	 * {@code n = sum(M)}, i.e. {@code n}-permutations with finite repetition.
	 * <p>
	 * @param M the multiplicities of the {@code n}-element multiset
	 * <p>
	 * @return the number {@code PFR(M)} of distinct {@code n}-element tuples of objects from a
	 *         {@code n}-element multiset with the specified multiplicities {@code M} where
	 *         {@code n = sum(M)}, i.e. {@code n}-permutations with finite repetition
	 */
	public static double PFR(final double[] M) {
		final double n = Maths.sum(M);
		if (n == 0.) {
			return 1.;
		}
		if (Maths.isFactorialLong(n)) {
			return Maths.factorial(n) / Maths.product(FACTORIAL.applyToPrimitiveArray(M));
		}
		double result = Maths.productSeries(M[0] + 1., n);
		for (int i = 1; i < M.length; ++i) {
			result /= Maths.factorial(M[i]);
		}
		return result;
	}

	/**
	 * Returns the number {@code PFR(k, M)} of distinct {@code k}-element tuples of objects from a
	 * {@code n}-element multiset with the specified multiplicities {@code M} where
	 * {@code n = sum(M)}, i.e. {@code k}-permutations with finite repetition.
	 * <p>
	 * @param k the number of elements in the tuples
	 * @param M the multiplicities of the {@code n}-element multiset
	 * <p>
	 * @return the number {@code PFR(k, M)} of distinct {@code k}-element tuples of objects from a
	 *         {@code n}-element multiset with the specified multiplicities {@code M} where
	 *         {@code n = sum(M)}, i.e. {@code k}-permutations with finite repetition
	 */
	public static int PFR(final int k, final int[] M) {
		// Check the arguments
		if (k == 0) {
			return 1;
		}

		// Initialize
		final ExtendedLinkedList<int[]> permutations = new ExtendedLinkedList<int[]>(
				Integers.EMPTY_PRIMITIVE_ARRAY);

		// Create the k-permutations with the finite multiplicities
		int result = 0;
		while (permutations.isNonEmpty()) {
			final int[] permutation = permutations.remove();
			for (int i = 0; i < M.length; ++i) {
				if (Integers.count(permutation, i) < M[i]) {
					if (permutation.length + 1 == k) {
						++result;
					} else {
						permutations.add(Integers.concat(permutation, i));
					}
				}
			}
		}
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number {@code C(n, k)} of distinct {@code k}-element subsets of a
	 * {@code n}-element set, i.e. {@code k}-combinations without repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the subsets
	 * <p>
	 * @return the number {@code C(n, k)} of distinct {@code k}-element subsets of a
	 *         {@code n}-element set, i.e. {@code k}-combinations without repetition
	 */
	public static int C(final int n, final int k) {
		if (k == 0 || n == 0 || k >= n) {
			return 1;
		}
		return P(n, k) / Maths.factorial(k);
	}

	/**
	 * Returns the number {@code C(n, k)} of distinct {@code k}-element subsets of a
	 * {@code n}-element set, i.e. {@code k}-combinations without repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the subsets
	 * <p>
	 * @return the number {@code C(n, k)} of distinct {@code k}-element subsets of a
	 *         {@code n}-element set, i.e. {@code k}-combinations without repetition
	 */
	public static long C(final long n, final long k) {
		if (k == 0L || n == 0L || k >= n) {
			return 1L;
		}
		return P(n, k) / Maths.factorial(k);
	}

	/**
	 * Returns the number {@code C(n, k)} of distinct {@code k}-element subsets of a
	 * {@code n}-element set, i.e. {@code k}-combinations without repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the subsets
	 * <p>
	 * @return the number {@code C(n, k)} of distinct {@code k}-element subsets of a
	 *         {@code n}-element set, i.e. {@code k}-combinations without repetition
	 */
	public static double C(final double n, final double k) {
		if (k == 0. || n == 0. || k >= n) {
			return 1.;
		}
		return P(n, k) / Maths.factorial(k);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number {@code CR(n, k)} of distinct {@code k}-element multisets of objects from a
	 * {@code n}-element set, i.e. {@code k}-combinations with repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the tuples
	 * <p>
	 * @return the number {@code CR(n, k)} of distinct {@code k}-element multisets of objects from a
	 *         {@code n}-element set, i.e. {@code k}-combinations with repetition
	 */
	public static int CR(final int n, final int k) {
		return C(n + k - 1, k);
	}

	/**
	 * Returns the number {@code CR(n, k)} of distinct {@code k}-element multisets of objects from a
	 * {@code n}-element set, i.e. {@code k}-combinations with repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the tuples
	 * <p>
	 * @return the number {@code CR(n, k)} of distinct {@code k}-element multisets of objects from a
	 *         {@code n}-element set, i.e. {@code k}-combinations with repetition
	 */
	public static long CR(final long n, final long k) {
		return C(n + k - 1L, k);
	}

	/**
	 * Returns the number {@code CR(n, k)} of distinct {@code k}-element multisets of objects from a
	 * {@code n}-element set, i.e. {@code k}-combinations with repetition.
	 * <p>
	 * @param n the number of elements in the set
	 * @param k the number of elements in the tuples
	 * <p>
	 * @return the number {@code CR(n, k)} of distinct {@code k}-element multisets of objects from a
	 *         {@code n}-element set, i.e. {@code k}-combinations with repetition
	 */
	public static double CR(final double n, final double k) {
		return C(n + k - 1., k);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number {@code CFR(k, M)} of distinct {@code k}-element multisubsets of a
	 * {@code n}-element multiset with the specified multiplicities {@code M} where
	 * {@code n = sum(M)}, i.e. {@code k}-combinations with finite repetition.
	 * <p>
	 * @param k the number of elements in the multisubsets
	 * @param M the multiplicities of the {@code n}-element multiset
	 * <p>
	 * @return the number {@code CFR(k, M)} of distinct {@code k}-element multisubsets of a
	 *         {@code n}-element multiset with the specified multiplicities {@code M} where
	 *         {@code n = sum(M)}, i.e. {@code k}-combinations with finite repetition
	 */
	public static int CFR(final int k, final int[] M) {
		// Check the arguments
		if (k == 0) {
			return 0;
		}

		// Initialize
		final ExtendedLinkedList<int[]> combinations = new ExtendedLinkedList<int[]>(
				Integers.EMPTY_PRIMITIVE_ARRAY);

		// Compute the number of k-combinations with the finite multiplicities
		int result = 0;
		while (combinations.isNonEmpty()) {
			final int[] combination = combinations.remove();
			final int from = combination.length == 0 ? 0 : combination[combination.length - 1];
			for (int i = from; i < M.length; ++i) {
				final int index = Integers.findFirstIndex(combination, i);
				if ((index < 0 ? 0 : combination.length - index) < M[i]) {
					if (combination.length + 1 == k) {
						++result;
					} else {
						combinations.add(Integers.concat(combination, i));
					}
				}
			}
		}
		return result;
	}
}
