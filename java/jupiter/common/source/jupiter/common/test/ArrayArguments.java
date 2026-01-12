/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2026 Florian Barras <https://barras.io> (florian@barras.io)
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
package jupiter.common.test;

import java.util.Collection;

import jupiter.common.util.Arrays;
import jupiter.common.util.Integers;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

/**
 * Utility class for performing various validation operations on arrays.
 * This class provides methods to enforce constraints such as null checks,
 * array type checks, length comparisons, and more. It is designed to
 * help validate array-related arguments in a consistent and reusable way.
 *
 * The {@code ArrayArguments} class cannot be instantiated.
 */
public class ArrayArguments
		extends Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String NAME = "array";
	public static final String NAMES = NAME + "s";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link ArrayArguments}.
	 */
	protected ArrayArguments() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Ensures that the provided array is not null. If the array is null, an exception is thrown.
	 *
	 * @param <T> the component type of the array
	 * @param array the array to check for nullity
	 * @return the provided array if it is not null
	 * @throws NullPointerException if the array is null
	 */
	public static <T> T[] requireNonNull(final T[] array) {
		return Arguments.requireNonNull(array, NAME);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Ensures that the specified object is an array. If the condition is not met
	 * and argument checking is enabled, an exception is thrown.
	 *
	 * @param object the object to be validated as an array
	 * @throws IllegalArgumentException if the object is not an array and argument checking is enabled
	 * @throws NullPointerException if the object is null and argument checking is enabled
	 */
	public static void requireArray(final Object object) {
		if (CHECK_ARGS) {
			requireArray(object, "object");
		}
	}

	/**
	 * Validates that the specified object is an array. If the object is not an array, an
	 * {@link IllegalArgumentException} is thrown.
	 *
	 * @param object the object to validate as an array, must not be null
	 * @param name the name of the object being validated, used for exception messages
	 * @throws NullPointerException if the object or name is null
	 * @throws IllegalArgumentException if the object is not an array
	 */
	public static void requireArray(final Object object, final String name) {
		if (CHECK_ARGS && !Arrays.is(requireNonNull(object, name))) {
			throw new IllegalArgumentException(Strings.paste("The specified", Strings.quote(name),
					"is not an", NAME));
		}
	}

	//////////////////////////////////////////////

	/**
	 * Ensures that the specified class {@code b} is assignable to the specified class {@code a}.
	 * If not, an {@link IllegalArgumentException} is thrown.
	 *
	 * @param a the class to verify type compatibility with
	 * @param b the class to check if it can be assigned to {@code a}
	 * @throws IllegalArgumentException if {@code b} is not assignable to {@code a}
	 */
	public static void requireAssignableFrom(final Class<?> a, final Class<?> b) {
		if (CHECK_ARGS && !a.isAssignableFrom(b)) {
			throw new IllegalArgumentException(Strings.paste("Cannot store", Objects.getName(b),
					"in an", NAME, "of", Objects.getName(a)));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Ensures that the specified found array matches the expected array. If the arrays do not match,
	 * an {@link IllegalArgumentException} is thrown.
	 *
	 * @param <T> the type of elements in the arrays
	 * @param found the array that has been provided at runtime
	 * @param expected the array that is expected for validation
	 * @return the found array if it matches the expected array
	 * @throws IllegalArgumentException if the found array does not equal the expected array
	 */
	public static <T> T[] require(final T[] found, final T[] expected) {
		if (CHECK_ARGS && !Arrays.equals(found, expected)) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, "is wrong",
					expectedButFound(found, expected)));
		}
		return found;
	}

	//////////////////////////////////////////////

	/**
	 * Validates that two arrays are equal. If the arrays are not equal and the validation flag is enabled,
	 * an {@link IllegalArgumentException} is thrown.
	 *
	 * @param a the first array to be compared
	 * @param b the second array to be compared
	 * @throws IllegalArgumentException if the arrays are not equal and the validation flag is enabled
	 */
	public static void requireEquals(final Object[] a, final Object[] b) {
		if (CHECK_ARGS && !Arrays.equals(a, b)) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAMES,
					"are not equal", isNotEqualTo(a, b)));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Ensures that the provided array is not empty and returns the array.
	 *
	 * @param <T> the type of the elements in the array
	 * @param array the array to be validated
	 * @return the validated array if it is not empty
	 * @throws IllegalArgumentException if the array is empty
	 */
	public static <T> T[] requireNonEmpty(final T[] array) {
		if (CHECK_ARGS) {
			return requireNonEmpty(array, NAME);
		}
		return array;
	}

	/**
	 * Ensures that the provided array is not empty. If the array is empty,
	 * an exception will be thrown.
	 *
	 * @param <T>   the type of the elements in the array
	 * @param array the array to be checked for non-emptiness
	 * @param name  the name of the array used for exception messages
	 * @return the same array if it is not empty
	 * @throws NullPointerException     if*/
	public static <T> T[] requireNonEmpty(final T[] array, final String name) {
		if (CHECK_ARGS) {
			requireNonEmpty(requireNonNull(array, name).length, name);
		}
		return array;
	}

	/**
	 * Validates that the specified length is not zero. If the length is zero, an {@link IllegalArgumentException}
	 * is thrown with a message that includes the provided name.
	 *
	 * @param length the length to validate
	 * @param name the name of the entity being checked, used in the exception message if validation fails
	 * @throws IllegalArgumentException if the specified length is zero and argument checking is enabled
	 */
	public static void requireNonEmpty(final int length, final String name) {
		if (CHECK_ARGS && length == 0) {
			throw new IllegalArgumentException(Strings.paste("The specified", Strings.quote(name),
					"is empty"));
		}
	}

	//////////////////////////////////////////////

	/**
	 * Ensures that the specified array has the expected length.
	 *
	 * @param <T> the type of elements in the array
	 * @param array the array to validate, must not be null
	 * @param expectedLength the expected length of the array
	 * @return the validated array if the length matches the expected length
	 * @throws NullPointerException if the array is null
	 * @throws IllegalArgumentException if the array length does not match the expected length
	 */
	public static <T> T[] requireLength(final T[] array, final int expectedLength) {
		if (CHECK_ARGS) {
			requireLength(requireNonNull(array).length, expectedLength);
		}
		return array;
	}

	/**
	 * Validates that the specified length matches the expected length.
	 * Throws an {@link IllegalArgumentException} if the found length differs from the expected length.
	 *
	 * @param foundLength the actual length found, which is to be verified
	 * @param expectedLength the expected length to match against
	 * @throws IllegalArgumentException if the found length does not match the expected length
	 */
	public static void requireLength(final int foundLength, final int expectedLength) {
		if (CHECK_ARGS && foundLength != expectedLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME,
					"has wrong length", expectedButFound(foundLength, expectedLength)));
		}
	}

	//////////////////////////////////////////////

	/**
	 * Ensures that the specified array meets the minimum expected length requirement.
	 *
	 * @param <T> the type of elements in the array
	 * @param array the array to be checked; must not be null
	 * @param minExpectedLength the minimum expected length of the array
	 * @return the original array if the length requirement is met
	 * @throws NullPointerException if the array is null
	 * @throws IllegalArgumentException if the length of the array is less than the minimum expected length
	 */
	public static <T> T[] requireMinLength(final T[] array, final int minExpectedLength) {
		if (CHECK_ARGS) {
			requireMinLength(requireNonNull(array).length, minExpectedLength);
		}
		return array;
	}

	/**
	 * Ensures that a given length meets a specified minimum requirement.
	 * Throws an IllegalArgumentException if the given length is less than the required minimum.
	 *
	 * @param foundLength the actual length to be checked
	 * @param minExpectedLength the minimum length that is required
	 */
	public static void requireMinLength(final int foundLength, final int minExpectedLength) {
		if (CHECK_ARGS && foundLength < minExpectedLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME,
					"has a length", foundLength,
					"inferior to", minExpectedLength));
		}
	}

	//////////////////////////////////////////////

	/**
	 * Ensures that the provided array does not exceed the specified maximum expected length.
	 * If the array's length exceeds the limit, an appropriate exception is thrown.
	 *
	 * @param <T> the type of elements in the array
	 * @param array the array to be checked for maximum length, must not be null
	 * @param maxExpectedLength the maximum allowable length for the array
	 * @return the original array if the length requirement is met
	 * @throws NullPointerException if the array is null
	 * @throws IllegalArgumentException if the array's length exceeds the maximum expected length
	 */
	public static <T> T[] requireMaxLength(final T[] array, final int maxExpectedLength) {
		if (CHECK_ARGS) {
			requireMaxLength(requireNonNull(array).length, maxExpectedLength);
		}
		return array;
	}

	/**
	 * Ensures that the given length does not exceed the specified maximum expected length.
	 * If the provided length is greater than the maximum allowed, an {@link IllegalArgumentException} is thrown.
	 *
	 * @param foundLength        the length that was found
	 * @param maxExpectedLength  the maximum length that is allowed
	 * @throws IllegalArgumentException if the found length exceeds the maximum expected length
	 */
	public static void requireMaxLength(final int foundLength, final int maxExpectedLength) {
		if (CHECK_ARGS && foundLength > maxExpectedLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME,
					"has a length", foundLength,
					"superior to", maxExpectedLength));
		}
	}

	//////////////////////////////////////////////

	/**
	 * Ensures that the lengths of the two specified arrays are the same.
	 * This method will throw a {@link NullPointerException} if either of the arrays is null.
	 *
	 * @param a the first array to be checked for length
	 * @param b the second array to be checked for length
	 * @throws NullPointerException if either {@code a} or {@code b} is null
	 * @throws IllegalArgumentException if the lengths of {@code a} and {@code b} are not the same
	 */
	public static void requireSameLength(final Object[] a, final Object[] b) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, requireNonNull(b).length);
		}
	}

	/**
	 * Ensures that the specified arrays have the same length. If the lengths of the arrays
	 * are different, this will throw an {@link IllegalArgumentException}.
	 *
	 * @param a the first array to be checked
	 * @param aName the name of the first array for identifying it in exception messages
	 * @param b the second array to be checked
	 * @param bName the name of the second array for identifying it in exception messages
	 * @throws IllegalArgumentException if the lengths of the arrays are not the same
	 * @throws NullPointerException if any of the arrays is null
	 */
	public static void requireSameLength(final Object[] a, final String aName, final Object[] b,
			final String bName) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, aName, requireNonNull(b).length, bName);
		}
	}

	/**
	 * Ensures that the given array and collection have the same length. If the lengths do not match,
	 * an {@link IllegalArgumentException} is thrown.
	 *
	 * @param a the array to compare
	 * @param b the collection to compare with the array
	 * @throws NullPointerException if the array or collection is {@code null}
	 * @throws IllegalArgumentException if the lengths of the array and collection are not the same
	 */
	public static void requireSameLength(final Object[] a, final Collection<?> b) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, requireNonNull(b).size());
		}
	}

	/**
	 * Ensures that the length of the specified array {@code a} is equal to the size of the specified collection {@code b}.
	 * If the lengths differ, an {@link IllegalArgumentException} is thrown.
	 *
	 * @param a the array whose length is to be checked
	 * @param aName the name of the array {@code a}, used in the exception message
	 * @param b the collection whose size is to be checked
	 * @param bName the name of the collection {@code b}, used in the exception message
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 * @throws IllegalArgumentException if the length of {@code a} and the size of {@code b} are not equal
	 */
	public static void requireSameLength(final Object[] a, final String aName,
			final Collection<?> b, final String bName) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, aName, requireNonNull(b).size(), bName);
		}
	}

	/**
	 * Ensures that the specified array has a length equal to the given expected length.
	 *
	 * @param a the array whose length is to be checked
	 * @param bLength the expected length of the array
	 * @throws NullPointerException if the specified array is {@code null}
	 * @throws IllegalArgumentException if the length of the array is not equal to the expected length
	 */
	public static void requireSameLength(final Object[] a, final int bLength) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, bLength);
		}
	}

	/**
	 * Validates that the length of the provided array is the same as the specified length.
	 * If the lengths do not match and argument checking is enabled, an exception will be thrown.
	 *
	 * @param a the array whose length is to be validated
	 * @param aName the name of the array to include in the exception message
	 * @param bLength the expected length to compare against
	 * @param bName the name of the second reference to include in the exception message
	 * @throws NullPointerException if the array is null
	 * @throws IllegalArgumentException if the lengths do not match and*/
	public static void requireSameLength(final Object[] a, final String aName, final int bLength,
			final String bName) {
		if (CHECK_ARGS) {
			requireSameLength(requireNonNull(a).length, aName, bLength, bName);
		}
	}

	/**
	 * Ensures that the two specified lengths are the same. Throws an {@link IllegalArgumentException}
	 * if the provided lengths are not equal.
	 *
	 * @param aLength the length of the first array or collection to compare
	 * @param bLength the length of the second array or collection to compare
	 * @throws IllegalArgumentException if {@code aLength} is not equal to {@code bLength}
	 */
	public static void requireSameLength(final int aLength, final int bLength) {
		if (CHECK_ARGS && aLength != bLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAMES,
					"do not have the same length", isNotEqualTo(aLength, bLength)));
		}
	}

	/**
	 * Validates that two specified lengths are the same. If the lengths are not equal
	 * and argument checking is enabled, an IllegalArgumentException is thrown.
	 *
	 * @param aLength the length of the first sequence
	 * @param aName the name or identifier of the first sequence
	 * @param bLength the length of the second sequence
	 * @param bName the name or identifier of the second sequence
	 * @throws IllegalArgumentException if CHECK_ARGS is true and the lengths are not equal
	 */
	public static void requireSameLength(final int aLength, final String aName, final int bLength,
			final String bName) {
		if (CHECK_ARGS && aLength != bLength) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAMES,
					Strings.quote(aName), "and", Strings.quote(bName),
					"do not have the same length", isNotEqualTo(aLength, bLength)));
		}
	}

	//////////////////////////////////////////////

	/**
	 * Validates that the specified index falls within the permissible range
	 * determined by the maximum expected length. If the validation fails and
	 * argument checks are enabled, an exception will be thrown.
	 *
	 * @param foundIndex the index to be validated
	 * @param maxExpectedLength the maximum length that defines the upper bound
	 *                          of the permissible range (exclusive)
	 */
	public static void requireIndex(final int foundIndex, final int maxExpectedLength) {
		if (CHECK_ARGS) {
			requireIndex(foundIndex, maxExpectedLength, true, false);
		}
	}

	/**
	 * Ensures that the provided index is within the expected range.
	 *
	 * @param foundIndex          The index value to be validated.
	 * @param maxExpectedLength   The maximum length expected, used to define the upper limit.
	 * @param isUpperInclusive    Indicates whether the upper bound is inclusive.
	 */
	public static void requireIndex(final int foundIndex, final int maxExpectedLength,
			final boolean isUpperInclusive) {
		if (CHECK_ARGS) {
			requireIndex(foundIndex, maxExpectedLength, true, isUpperInclusive);
		}
	}

	/**
	 * Validates whether a given index falls within the specified bounds.
	 * Throws an {@link IllegalArgumentException} if the index is not within the allowable range.
	 *
	 * @param foundIndex the index to be checked
	 * @param maxExpectedLength the upper bound (exclusive or inclusive depending on `isUpperInclusive`) for the index
	 * @param isLowerInclusive flag indicating whether the lower bound (0) is inclusive
	 **/
	public static void requireIndex(final int foundIndex, final int maxExpectedLength,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		if (CHECK_ARGS && !Integers.isBetween(foundIndex, 0, maxExpectedLength, isLowerInclusive,
				isUpperInclusive)) {
			throw new IllegalArgumentException(Strings.paste("The specified index is out of bounds",
					betweenExpectedButFound(foundIndex, 0, maxExpectedLength, isLowerInclusive,
							isUpperInclusive)));
		}
	}
}
