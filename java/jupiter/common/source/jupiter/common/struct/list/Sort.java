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
package jupiter.common.struct.list;

import java.io.Serializable;
import java.util.Comparator;

import jupiter.common.util.Arrays;

/**
 * This is a duplicate of {@code TimSort} from Oracle Java 8.
 * <p>
 * @param <T> the component type of the array to sort
 */
public class Sort<T>
		implements Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * This is the minimum sized sequence that will be merged. Shorter sequences will be lengthened
	 * by calling the static method {@link #binarySort}. If the entire array is less than this
	 * length, no merges will be performed.
	 * <p>
	 * This constant should be a power of two. It was 64 in Tim Peter's C implementation, but 32 was
	 * empirically determined to work better in this implementation. In the unlikely event that you
	 * set this constant to be a number that is not a power of two, you will need to change the
	 * computation of the static method {@link #minRunLength}.
	 * <p>
	 * If you decrease this constant, you must change the {@code stackLength} computation in the
	 * {@link Sort} constructor, or you risk an {@link ArrayIndexOutOfBoundsException}. See
	 * listsort.txt for a discussion of the minimum stack length required as a function of the
	 * length of the array being sorted and the minimum merge sequence length.
	 */
	protected static final int MIN_MERGE = 32;

	/**
	 * When we get into galloping mode, we stay there until both runs win less often than
	 * {@code MIN_GALLOP} consecutive times.
	 */
	protected static final int MIN_GALLOP = 7;

	/**
	 * Maximum initial size of {@code tempArray} array used for merging. The array can grow to
	 * accommodate demand.
	 * <p>
	 * Unlike Tim's original C version, we do not allocate this much storage when sorting smaller
	 * arrays. This change was required for performance.
	 */
	protected static final int INITIAL_TEMP_STORAGE_LENGTH = 256;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The array being sorted.
	 */
	protected final T[] array;

	/**
	 * The {@link Comparator} of supertype {@code T} to determine the order.
	 */
	protected final Comparator<? super T> comparator;

	/**
	 * This controls when we get *into* galloping mode. It is initialized to {@code MIN_GALLOP}. The
	 * methods {@link #mergeLo} and {@link #mergeHi} nudge it higher for random data and lower for
	 * highly structured data.
	 */
	protected int minGallop = MIN_GALLOP;

	/**
	 * Temporary storage for merges. A workspace array may optionally be provided in constructor and
	 * if so will be used as long as it is big enough.
	 */
	protected T[] tempArray;
	protected int tempArrayBase; // base of temp array slice
	protected int tempArrayLength; // length of temp array slice

	/**
	 * A stack of pending runs yet to be merged. Run {@code i} starts at address {@code base[i]} and
	 * extends for {@code length[i]} elements. It is always true (so long as the indexes are in
	 * bounds) that:
	 * <p>
	 * {@code runBase[i] + runLength[i] == runBase[i + 1]}
	 * <p>
	 * so we could cut the storage for this, but it is a minor amount and keeping all the info
	 * explicit simplifies the code.
	 */
	protected int stackSize = 0; // number of pending runs on stack
	protected final int[] runBase;
	protected final int[] runLength;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link Sort} instance to maintain the state of an ongoing sort.
	 * <p>
	 * @param array      the {@code T} array to sort
	 * @param comparator the {@link Comparator} of supertype {@code T} to determine the order
	 * @param work       a workspace array (slice) (may be {@code null})
	 * @param workBase   the origin of the usable space in the work array
	 * @param workLength the usable size of the work array
	 */
	protected Sort(final T[] array, final Comparator<? super T> comparator, final T[] work,
			final int workBase, final int workLength) {
		this.array = array;
		this.comparator = comparator;

		// Allocate temporary storage (which may be increased later if necessary)
		final int length = array.length;
		final int tempLength = length < 2 * INITIAL_TEMP_STORAGE_LENGTH ? length >>> 1 :
				INITIAL_TEMP_STORAGE_LENGTH;
		if (work == null || workLength < tempLength || workBase + tempLength > work.length) {
			@SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
			final T[] newArray = (T[]) Arrays.create(Arrays.getComponentType(array), tempLength);
			tempArray = newArray;
			tempArrayBase = 0;
			tempArrayLength = tempLength;
		} else {
			tempArray = work;
			tempArrayBase = workBase;
			tempArrayLength = workLength;
		}

		/*
		 * Allocate runs-to-be-merged stack (which cannot be expanded). The stack length
		 * requirements are described in listsort.txt. The C version always uses the same stack
		 * length (85), but this was measured to be too expensive when sorting "mid-sized" arrays
		 * (e.g., 100 elements) in Java. Therefore, we use smaller (but sufficiently large) stack
		 * lengths for smaller arrays. The "magic numbers" in the computation below must be changed
		 * if {@code MIN_MERGE} is decreased. See the {@code MIN_MERGE} declaration above for more
		 * information. The maximum value of 49 allows for an array up to length {@code
		 * Integer.MAX_VALUE - 4}, if array is filled by the worst case stack size increasing
		 * scenario. More explanations are specified in section 4 of:
		 * http://envisage-project.eu/wp-content/uploads/2015/02/sorting.pdf
		 */
		final int stackLength = length < 120 ? 5 : length < 1542 ? 10 : length < 119151 ? 24 : 49;
		runBase = new int[stackLength];
		runLength = new int[stackLength];
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sorts the specified range, using the specified workspace array slice for temporary storage
	 * when possible. This method is designed to be invoked from public methods (in class
	 * {@link Arrays}) after performing any necessary array bounds checks and expanding parameters
	 * into the required forms.
	 * <p>
	 * @param <T>        the component type of the array to sort
	 * @param array      the {@code T} array to sort
	 * @param fromIndex  the index of the first element to sort (inclusive)
	 * @param toIndex    the index of the last element to sort (exclusive)
	 * @param comparator the {@link Comparator} of supertype {@code T} to determine the order
	 * @param work       a workspace array (slice)
	 * @param workBase   the origin of the usable space in the work array
	 * @param workLength the usable size of the work array
	 */
	public static <T> void sort(final T[] array, int fromIndex, final int toIndex,
			final Comparator<? super T> comparator, final T[] work, final int workBase,
			final int workLength) {
		assert comparator != null && array != null && fromIndex >= 0 && fromIndex <= toIndex &&
				toIndex <= array.length;

		int nRemaining = toIndex - fromIndex;
		if (nRemaining < 2) {
			return; // arrays of size 0 and 1 are always sorted
		}
		// If array is small, do a "mini-TimSort" with no merges
		if (nRemaining < MIN_MERGE) {
			final int initRunLength = countRunAndMakeAscending(array, fromIndex, toIndex,
					comparator);
			binarySort(array, fromIndex, toIndex, fromIndex + initRunLength, comparator);
			return;
		}

		/**
		 * March over the array once, left to right, finding natural runs, extending short natural
		 * runs to minRun elements and merging runs to maintain stack invariant.
		 */
		final Sort<T> ts = new Sort<T>(array, comparator, work, workBase, workLength);
		final int minRun = minRunLength(nRemaining);
		do {
			// Identify next run
			int runLength = countRunAndMakeAscending(array, fromIndex, toIndex, comparator);

			// If run is short, extend to min(minRun, nRemaining)
			if (runLength < minRun) {
				final int force = nRemaining <= minRun ? nRemaining : minRun;
				binarySort(array, fromIndex, fromIndex + force, fromIndex + runLength, comparator);
				runLength = force;
			}

			// Push run onto pending-run stack and maybe merge
			ts.pushRun(fromIndex, runLength);
			ts.mergeCollapse();

			// Advance to find next run
			fromIndex += runLength;
			nRemaining -= runLength;
		} while (nRemaining > 0);

		// Merge all remaining runs to complete sort
		assert fromIndex == toIndex;
		ts.mergeForceCollapse();
		assert ts.stackSize == 1;
	}

	/**
	 * Sorts the specified portion of the specified array using a binary insertion sort. This is the
	 * best method for sorting small numbers of elements. It requires O(n log n) compares, but
	 * O(n^2) data movement (worst case).
	 * <p>
	 * If the initial part of the specified range is already sorted, this method can take advantage
	 * of it: the method assumes that the elements from index {@code lo}, inclusive, to
	 * {@code start}, exclusive are already sorted.
	 * <p>
	 * @param <T>        the component type of the array to sort
	 * @param array      the {@code T} array in which a range is to be sorted
	 * @param lo         the index of the first element in the range to sort
	 * @param hi         the index after the last element in the range to sort
	 * @param start      the index of the first element in the range that is not already known to be
	 *                   sorted ({@code lo <= start <= hi})
	 * @param comparator the {@link Comparator} of supertype {@code T} to determine the order
	 */
	@SuppressWarnings("fallthrough")
	protected static <T> void binarySort(final T[] array, final int lo, final int hi, int start,
			final Comparator<? super T> comparator) {
		assert lo <= start && start <= hi;
		if (start == lo) {
			++start;
		}
		for (; start < hi; ++start) {
			final T pivot = array[start];

			// Set left (and right) to the index where a[start] (pivot) belongs
			int left = lo, right = start;
			assert left <= right;
			// Invariants:
			// • pivot >= all in [lo, left) and
			// • pivot < all in [right, start).
			while (left < right) {
				final int mid = left + right >>> 1;
				if (comparator.compare(pivot, array[mid]) < 0) {
					right = mid;
				} else {
					left = mid + 1;
				}
			}
			assert left == right;

			// The invariants still hold:
			// • pivot >= all in [lo, left) and
			// • pivot < all in [left, start), so pivot belongs at left.
			// Note that if there are elements equal to pivot, left points to the first slot after
			// them - that is why this sort is stable. Slide elements over to make room for pivot.
			final int n = start - left; // the number of elements to move
			// Switch is just an optimization for arraycopy in default case
			switch (n) {
				case 2:
					array[left + 2] = array[left + 1];
				case 1:
					array[left + 1] = array[left];
					break;
				default:
					System.arraycopy(array, left, array, left + 1, n);
			}
			array[left] = pivot;
		}
	}

	/**
	 * Returns the length of the run beginning at the specified position in the specified array and
	 * reverses the run if it is descending (ensuring that the run will always be ascending when the
	 * method returns).
	 * <p>
	 * A run is the longest ascending sequence with {@code a[lo] <= a[lo + 1] <= a[lo + 2] <= ...}
	 * or the longest descending sequence with {@code a[lo] > a[lo + 1] > a[lo + 2] > ...}.
	 * <p>
	 * For its intended use in a stable merge sort, the strictness of the definition of "descending"
	 * is needed so that the call can safely reverse a descending sequence without violating
	 * stability.
	 * <p>
	 * @param <T>        the component type of the array to sort
	 * @param array      the {@code T} array in which a run is to be counted and possibly reversed
	 * @param lo         the index of the first element in the run
	 * @param hi         the index after the last element that may be contained in the run. It is
	 *                   required that {@code lo < hi}.
	 * @param comparator the {@link Comparator} of supertype {@code T} to determine the order
	 * <p>
	 * @return the length of the run beginning at the specified position in the specified array
	 */
	protected static <T> int countRunAndMakeAscending(final T[] array, final int lo, final int hi,
			final Comparator<? super T> comparator) {
		assert lo < hi;
		int runHi = lo + 1;
		if (runHi == hi) {
			return 1;
		}

		// Find end of run and reverse range if descending
		if (comparator.compare(array[runHi++], array[lo]) < 0) {
			// Descending
			while (runHi < hi && comparator.compare(array[runHi], array[runHi - 1]) < 0) {
				++runHi;
			}
			reverseRange(array, lo, runHi);
		} else {
			// Ascending
			while (runHi < hi && comparator.compare(array[runHi], array[runHi - 1]) >= 0) {
				++runHi;
			}
		}

		return runHi - lo;
	}

	/**
	 * Reverse the specified range of the specified array.
	 * <p>
	 * @param array the array of {@link Object} in which a range is to be reversed
	 * @param lo    the index of the first element in the range to reverse
	 * @param hi    the index after the last element in the range to reverse
	 */
	protected static void reverseRange(final Object[] array, int lo, int hi) {
		--hi;
		while (lo < hi) {
			final Object t = array[lo];
			array[lo++] = array[hi];
			array[hi--] = t;
		}
	}

	/**
	 * Returns the minimum acceptable run length for an array of the specified length. Natural runs
	 * shorter than this will be extended with the static method {@link #binarySort}.
	 * <p>
	 * Roughly speaking, the computation is:
	 * <p>
	 * If {@code n < MIN_MERGE}, return {@code n} (it is too small to bother with fancy stuff); else
	 * if {@code n} is an exact power of 2, return {@code MIN_MERGE / 2}; else return an int
	 * {@code k}, {@code MIN_MERGE / 2 <= k <= MIN_MERGE}, such that {@code n / k} is close to, but
	 * strictly less than, an exact power of 2.
	 * <p>
	 * For the rationale, see listsort.txt.
	 * <p>
	 * @param n the length of the array to sort
	 * <p>
	 * @return the length of the minimum run to merge
	 */
	protected static int minRunLength(int n) {
		assert n >= 0;
		int r = 0; // becomes 1 if any 1 bits are shifted off
		while (n >= MIN_MERGE) {
			r |= n & 1;
			n >>= 1;
		}
		return n + r;
	}

	/**
	 * Pushes the specified run onto the pending-run stack.
	 * <p>
	 * @param runBase   the index of the first element in the run
	 * @param runLength the number of elements in the run
	 */
	protected void pushRun(final int runBase, final int runLength) {
		this.runBase[stackSize] = runBase;
		this.runLength[stackSize] = runLength;
		++stackSize;
	}

	/**
	 * Examines the stack of runs waiting to be merged and merges adjacent runs until the stack
	 * invariants are reestablished:
	 * <p>
	 * 1. {@code runLength[i - 3] > runLength[i - 2] + runLength[i - 1]}
	 * <p>
	 * 2. {@code runLength[i - 2] > runLength[i - 1]}
	 * <p>
	 * This method is called each time a new run is pushed onto the stack, so the invariants are
	 * guaranteed to hold for {@code i < stackSize} upon entry to the method.
	 */
	protected void mergeCollapse() {
		while (stackSize > 1) {
			int n = stackSize - 2;
			if (n > 0 && runLength[n - 1] <= runLength[n] + runLength[n + 1]) {
				if (runLength[n - 1] < runLength[n + 1]) {
					--n;
				}
				mergeAt(n);
			} else if (runLength[n] <= runLength[n + 1]) {
				mergeAt(n);
			} else {
				break; // invariant is established
			}
		}
	}

	/**
	 * Merges all runs on the stack until only one remains. This method is called once, to complete
	 * the sort.
	 */
	protected void mergeForceCollapse() {
		while (stackSize > 1) {
			int n = stackSize - 2;
			if (n > 0 && runLength[n - 1] < runLength[n + 1]) {
				--n;
			}
			mergeAt(n);
		}
	}

	/**
	 * Merges the two runs at stack indexes {@code i} and {@code i + 1}. Run {@code i} must be the
	 * penultimate or antepenultimate run on the stack. In other words, {@code i} must be equal to
	 * {@code stackSize - 2} or {@code stackSize - 3}.
	 * <p>
	 * @param i the stack index of the first of the two runs to merge
	 */
	protected void mergeAt(final int i) {
		assert stackSize >= 2;
		assert i >= 0;
		assert i == stackSize - 2 || i == stackSize - 3;

		int base1 = runBase[i], length1 = runLength[i];
		int base2 = runBase[i + 1], length2 = runLength[i + 1];
		assert length1 > 0 && length2 > 0;
		assert base1 + length1 == base2;

		/*
		 * Record the length of the combined runs; if i is the 3rd-last run now, also slide over the
		 * last run (which isn't involved in this merge). The current run ({@code i + 1}) goes away
		 * in any case.
		 */
		runLength[i] = length1 + length2;
		if (i == stackSize - 3) {
			runBase[i + 1] = runBase[i + 2];
			runLength[i + 1] = runLength[i + 2];
		}
		--stackSize;

		/*
		 * Find where the first element of run2 goes in run1. Prior elements in run1 can be ignored
		 * (because they're already in place).
		 */
		final int k = gallopRight(array[base2], array, base1, length1, 0, comparator);
		assert k >= 0;
		base1 += k;
		length1 -= k;
		if (length1 == 0) {
			return;
		}

		/*
		 * Find where the last element of run1 goes in run2. Subsequent elements in run2 can be
		 * ignored (because they're already in place).
		 */
		length2 = gallopLeft(array[base1 + length1 - 1], array, base2, length2, length2 - 1,
				comparator);
		assert length2 >= 0;
		if (length2 == 0) {
			return;
		}

		/*
		 * Merge remaining runs, using {@code tempArray} array with {@code min(length1, length2)}
		 * elements.
		 */
		if (length1 <= length2) {
			mergeLo(base1, length1, base2, length2);
		} else {
			mergeHi(base1, length1, base2, length2);
		}
	}

	/**
	 * Locates the position at which to insert the specified {@code T} key into the specified sorted
	 * range; if the range contains an element equal to {@code key}, returns the index of the
	 * leftmost equal element.
	 * <p>
	 * @param <T>        the component type of the array to sort
	 * @param key        the {@code T} key whose insertion point to search for
	 * @param array      the {@code T} array in which to search
	 * @param base       the index of the first element in the range
	 * @param length     the length of the range (must be greater than 0)
	 * @param hint       the index at which to begin the search, {@code 0 <= hint < n} (the closer
	 *                   hint is to the result, the faster this method will run)
	 * @param comparator the {@link Comparator} of supertype {@code T} to order the range and to
	 *                   search
	 * <p>
	 * @return the integer {@code k}, {@code 0 <= k <= n} such that
	 *         {@code a[b + k - 1] < key <= a[b + k]}, pretending that {@code a[b - 1]} is minus
	 *         infinity and {@code a[b + n]} is infinity; in other words, {@code key} belongs at
	 *         index {@code b + k}; or in other words, the first {@code k} elements of {@code a}
	 *         should precede {@code key} and the last {@code n - k} should follow it
	 */
	protected static <T> int gallopLeft(final T key, final T[] array, final int base,
			final int length, final int hint, final Comparator<? super T> comparator) {
		assert length > 0 && hint >= 0 && hint < length;

		int lastOfs = 0, ofs = 1;
		if (comparator.compare(key, array[base + hint]) > 0) {
			/*
			 * Gallop right until {@code a[base + hint + lastOfs] < key <= a[base + hint + ofs]}.
			 */
			final int maxOfs = length - hint;
			while (ofs < maxOfs && comparator.compare(key, array[base + hint + ofs]) > 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) // int overflow
				{
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to base
			lastOfs += hint;
			ofs += hint;
		} else {
			// key <= a[base + hint]
			/*
			 * Gallop left until {@code a[base + hint - ofs] < key <= a[base + hint - lastOfs]}.
			 */
			final int maxOfs = hint + 1;
			while (ofs < maxOfs && comparator.compare(key, array[base + hint - ofs]) <= 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) // int overflow
				{
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to base
			final int tempArray = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tempArray;
		}
		assert -1 <= lastOfs && lastOfs < ofs && ofs <= length;

		/*
		 * Now {@code a[base + lastOfs] < key <= a[base + ofs]}, so key belongs somewhere to the
		 * right of {@code lastOfs} but no farther right than {@code ofs}. Do a binary search, with
		 * invariant {@code a[base + lastOfs - 1] < key <= a[base + ofs]}.
		 */
		++lastOfs;
		while (lastOfs < ofs) {
			final int m = lastOfs + (ofs - lastOfs >>> 1);

			if (comparator.compare(key, array[base + m]) > 0) {
				lastOfs = m + 1; // a[base + m] < key
			} else {
				ofs = m; // key <= a[base + m]
			}
		}
		assert lastOfs == ofs; // so a[base + ofs - 1] < key <= a[base + ofs]
		return ofs;
	}

	/**
	 * Like the static method {@link #gallopLeft}, except that if the range contains an element
	 * equal to {@code key}, the static method {@link #gallopRight} returns the index after the
	 * rightmost equal element.
	 * <p>
	 * @param <T>        the component type of the array to sort
	 * @param key        the {@code T} key whose insertion point to search for
	 * @param array      the {@code T} array in which to search
	 * @param base       the index of the first element in the range
	 * @param length     the length of the range (must be greater than 0)
	 * @param hint       the index at which to begin the search, {@code 0 <= hint < n} (the closer
	 *                   hint is to the result, the faster this method will run)
	 * @param comparator the {@link Comparator} of supertype {@code T} to order the range and to
	 *                   search
	 * <p>
	 * @return the integer {@code k}, {@code 0 <= k <= n} such that
	 *         {@code a[b + k - 1] <= key < a[b + k]}
	 */
	protected static <T> int gallopRight(final T key, final T[] array, final int base,
			final int length, final int hint, final Comparator<? super T> comparator) {
		assert length > 0 && hint >= 0 && hint < length;

		int ofs = 1, lastOfs = 0;
		if (comparator.compare(key, array[base + hint]) < 0) {
			/*
			 * Gallop left until {@code a[b + hint - ofs] <= key < a[b + hint - lastOfs]}.
			 */
			final int maxOfs = hint + 1;
			while (ofs < maxOfs && comparator.compare(key, array[base + hint - ofs]) < 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) // int overflow
				{
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to b
			final int tempArray = lastOfs;
			lastOfs = hint - ofs;
			ofs = hint - tempArray;
		} else {
			// a[b + hint] <= key
			/*
			 * Gallop right until {@code a[b + hint + lastOfs] <= key < a[b + hint + ofs]}.
			 */
			final int maxOfs = length - hint;
			while (ofs < maxOfs && comparator.compare(key, array[base + hint + ofs]) >= 0) {
				lastOfs = ofs;
				ofs = (ofs << 1) + 1;
				if (ofs <= 0) // int overflow
				{
					ofs = maxOfs;
				}
			}
			if (ofs > maxOfs) {
				ofs = maxOfs;
			}

			// Make offsets relative to b
			lastOfs += hint;
			ofs += hint;
		}
		assert -1 <= lastOfs && lastOfs < ofs && ofs <= length;

		/*
		 * Now {@code a[b + lastOfs] <= key < a[b + ofs]}, so key belongs somewhere to the right of
		 * {@code lastOfs} but no farther right than {@code ofs}. Do a binary search, with invariant
		 * {@code a[b + lastOfs - 1] <= key < a[b + ofs]}.
		 */
		++lastOfs;
		while (lastOfs < ofs) {
			final int m = lastOfs + (ofs - lastOfs >>> 1);

			if (comparator.compare(key, array[base + m]) < 0) {
				ofs = m; // key < a[b + m]
			} else {
				lastOfs = m + 1; // a[b + m] <= key
			}
		}
		assert lastOfs == ofs; // so a[b + ofs - 1] <= key < a[b + ofs]
		return ofs;
	}

	/**
	 * Merges two adjacent runs in place, in a stable fashion. The first element of the first run
	 * must be greater than the first element of the second run ({@code a[base1] > a[base2]}) and
	 * the last element of the first run ({@code a[base1 + length1 - 1]}) must be greater than all
	 * elements of the second run.
	 * <p>
	 * For performance, this method should be called only when {@code length1 <= length2}; its twin
	 * method {@link #mergeHi} should be called if {@code length1 >= length2}. (Either method may be
	 * called if {@code length1 == length2}.)
	 * <p>
	 * @param base1   index of first element in first run to be merged
	 * @param length1 length of first run to be merged (must be greater than 0)
	 * @param base2   index of first element in second run to be merged (must be
	 *                {@code aBase + aLength})
	 * @param length2 length of second run to be merged (must be greater than 0)
	 */
	protected void mergeLo(final int base1, int length1, final int base2, int length2) {
		assert length1 > 0 && length2 > 0 && base1 + length1 == base2;

		// Copy first run into temporary array
		final T[] array = this.array; // for performance
		final T[] tempArray = ensureCapacity(length1);
		int cursor1 = tempArrayBase; // indexes into temp array
		int cursor2 = base2; // indexes into a
		int dest = base1; // indexes into a
		System.arraycopy(array, base1, tempArray, cursor1, length1);

		// Move first element of second run and deal with degenerate cases
		array[dest++] = array[cursor2++];
		if (--length2 == 0) {
			System.arraycopy(tempArray, cursor1, array, dest, length1);
			return;
		}
		if (length1 == 1) {
			System.arraycopy(array, cursor2, array, dest, length2);
			array[dest + length2] = tempArray[cursor1]; // last elt of run 1 to end of merge
			return;
		}

		// Use local variable for performance
		final Comparator<? super T> comparator = this.comparator;
		int minGallop = this.minGallop;
outer:  while (true) {
			int count1 = 0; // number of times in a row that first run won
			int count2 = 0; // number of times in a row that second run won

			/*
			 * Do the straightforward thing until (if ever) one run starts winning consistently.
			 */
			do {
				assert length1 > 1 && length2 > 0;
				if (comparator.compare(array[cursor2], tempArray[cursor1]) < 0) {
					array[dest++] = array[cursor2++];
					++count2;
					count1 = 0;
					if (--length2 == 0) {
						break outer;
					}
				} else {
					array[dest++] = tempArray[cursor1++];
					++count1;
					count2 = 0;
					if (--length1 == 1) {
						break outer;
					}
				}
			} while ((count1 | count2) < minGallop);

			/*
			 * One run is winning so consistently that galloping may be a huge win. So try that and
			 * continue galloping until (if ever) neither run appears to be winning consistently
			 * anymore.
			 */
			do {
				assert length1 > 1 && length2 > 0;
				count1 = gallopRight(array[cursor2], tempArray, cursor1, length1, 0, comparator);
				if (count1 != 0) {
					System.arraycopy(tempArray, cursor1, array, dest, count1);
					dest += count1;
					cursor1 += count1;
					length1 -= count1;
					if (length1 <= 1) // length1 == 1 || length1 == 0
					{
						break outer;
					}
				}
				array[dest++] = array[cursor2++];
				if (--length2 == 0) {
					break outer;
				}

				count2 = gallopLeft(tempArray[cursor1], array, cursor2, length2, 0, comparator);
				if (count2 != 0) {
					System.arraycopy(array, cursor2, array, dest, count2);
					dest += count2;
					cursor2 += count2;
					length2 -= count2;
					if (length2 == 0) {
						break outer;
					}
				}
				array[dest++] = tempArray[cursor1++];
				if (--length1 == 1) {
					break outer;
				}
				--minGallop;
			} while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
			if (minGallop < 0) {
				minGallop = 0;
			}
			minGallop += 2; // penalize for leaving gallop mode
		} // end of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop; // write back to field

		switch (length1) {
			case 1:
				assert length2 > 0;
				System.arraycopy(array, cursor2, array, dest, length2);
				array[dest + length2] = tempArray[cursor1]; // last elt of run 1 to end of merge
				break;
			case 0:
				throw new IllegalArgumentException(
						"Comparison method violates its general contract");
			default:
				assert length2 == 0;
				assert length1 > 1;
				System.arraycopy(tempArray, cursor1, array, dest, length1);
		}
	}

	/**
	 * Like the method {@link #mergeLo}, except that this method should be called only if
	 * {@code length1 >= length2}; the method {@link #mergeLo} should be called if
	 * {@code length1 <= length2}. (Either method may be called if {@code length1 == length2}.)
	 * <p>
	 * @param base1   index of first element in first run to be merged
	 * @param length1 length of first run to be merged (must be greater than 0)
	 * @param base2   index of first element in second run to be merged (must be
	 *                {@code aBase + aLength})
	 * @param length2 length of second run to be merged (must be greater than 0)
	 */
	protected void mergeHi(final int base1, int length1, final int base2, int length2) {
		assert length1 > 0 && length2 > 0 && base1 + length1 == base2;

		// Copy second run into temporary array
		final T[] array = this.array; // for performance
		final T[] tempArray = ensureCapacity(length2);
		final int tempArrayBase = this.tempArrayBase;
		System.arraycopy(array, base2, tempArray, tempArrayBase, length2);

		int cursor1 = base1 + length1 - 1; // indexes into a
		int cursor2 = tempArrayBase + length2 - 1; // indexes into temp array
		int dest = base2 + length2 - 1; // indexes into a

		// Move last element of first run and deal with degenerate cases
		array[dest--] = array[cursor1--];
		if (--length1 == 0) {
			System.arraycopy(tempArray, tempArrayBase, array, dest - (length2 - 1), length2);
			return;
		}
		if (length2 == 1) {
			dest -= length1;
			cursor1 -= length1;
			System.arraycopy(array, cursor1 + 1, array, dest + 1, length1);
			array[dest] = tempArray[cursor2];
			return;
		}

		// Use local variable for performance
		final Comparator<? super T> comparator = this.comparator;
		int minGallop = this.minGallop;
outer:  while (true) {
			int count1 = 0; // number of times in a row that first run won
			int count2 = 0; // number of times in a row that second run won

			/*
			 * Do the straightforward thing until (if ever) one run appears to win consistently.
			 */
			do {
				assert length1 > 0 && length2 > 1;
				if (comparator.compare(tempArray[cursor2], array[cursor1]) < 0) {
					array[dest--] = array[cursor1--];
					++count1;
					count2 = 0;
					if (--length1 == 0) {
						break outer;
					}
				} else {
					array[dest--] = tempArray[cursor2--];
					++count2;
					count1 = 0;
					if (--length2 == 1) {
						break outer;
					}
				}
			} while ((count1 | count2) < minGallop);

			/*
			 * One run is winning so consistently that galloping may be a huge win. So try that and
			 * continue galloping until (if ever) neither run appears to be winning consistently
			 * anymore.
			 */
			do {
				assert length1 > 0 && length2 > 1;
				count1 = length1 - gallopRight(tempArray[cursor2], array, base1, length1,
						length1 - 1, comparator);
				if (count1 != 0) {
					dest -= count1;
					cursor1 -= count1;
					length1 -= count1;
					System.arraycopy(array, cursor1 + 1, array, dest + 1, count1);
					if (length1 == 0) {
						break outer;
					}
				}
				array[dest--] = tempArray[cursor2--];
				if (--length2 == 1) {
					break outer;
				}

				count2 = length2 - gallopLeft(array[cursor1], tempArray, tempArrayBase, length2,
						length2 - 1, comparator);
				if (count2 != 0) {
					dest -= count2;
					cursor2 -= count2;
					length2 -= count2;
					System.arraycopy(tempArray, cursor2 + 1, array, dest + 1, count2);
					if (length2 <= 1) {
						break outer; // length2 == 1 || length2 == 0
					}
				}
				array[dest--] = array[cursor1--];
				if (--length1 == 0) {
					break outer;
				}
				--minGallop;
			} while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
			if (minGallop < 0) {
				minGallop = 0;
			}
			minGallop += 2; // penalize for leaving gallop mode
		} // end of "outer" loop
		this.minGallop = minGallop < 1 ? 1 : minGallop; // write back to field

		switch (length2) {
			case 1:
				assert length1 > 0;
				dest -= length1;
				cursor1 -= length1;
				System.arraycopy(array, cursor1 + 1, array, dest + 1, length1);
				array[dest] = tempArray[cursor2]; // move first elt of run2 to front of merge
				break;
			case 0:
				throw new IllegalArgumentException(
						"Comparison method violates its general contract");
			default:
				assert length1 == 0;
				assert length2 > 0;
				System.arraycopy(tempArray, tempArrayBase, array, dest - (length2 - 1), length2);
		}
	}

	/**
	 * Ensures that the external array {@code tempArray} has at least the specified number of
	 * elements, increasing its size if necessary. The size increases exponentially to ensure
	 * amortized linear time complexity.
	 * <p>
	 * @param minCapacity the minimum required capacity of {@code tempArray}
	 * <p>
	 * @return {@code tempArray}, whether or not it grew
	 */
	protected T[] ensureCapacity(final int minCapacity) {
		if (tempArrayLength < minCapacity) {
			// Compute the smallest power of 2 > minCapacity
			int newSize = minCapacity;
			newSize |= newSize >> 1;
			newSize |= newSize >> 2;
			newSize |= newSize >> 4;
			newSize |= newSize >> 8;
			newSize |= newSize >> 16;
			++newSize;

			if (newSize < 0) // not bloody likely!
			{
				newSize = minCapacity;
			} else {
				newSize = Math.min(newSize, array.length >>> 1);
			}

			@SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
			final T[] newArray = (T[]) Arrays.create(Arrays.getComponentType(array), newSize);
			tempArray = newArray;
			tempArrayLength = newSize;
			tempArrayBase = 0;
		}
		return tempArray;
	}
}
