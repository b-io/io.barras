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
package jupiter.common.test;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Strings.EMPTY;

import jupiter.common.util.Strings;

/**
 * The {@code Arguments} class provides utility methods for generating descriptive messages
 * and validating arguments in a variety of contextual scenarios. This includes verifying
 * argument conditions, ensuring non-null references, and constructing descriptive error
 * or state-specific messages.
 */
public class Arguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String NAME = "object";
	public static final String NAMES = NAME + "s";

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Defines whether argument checking is enabled globally in the application.
	 * When set to {@code true}, methods in the {@link Arguments} class that validate
	 * arguments throw exceptions if their preconditions are not met.
	 * When set to {@code false}, argument checks are skipped, and methods will not
	 * enforce preconditions.
	 *
	 * This flag is useful for toggling strict validation behavior during development
	 * or runtime without modifying individual method implementations.
	 *
	 * Default value is {@code true}.
	 */
	public static volatile boolean CHECK_ARGS = true;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Arguments}.
	 */
	protected Arguments() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a message string indicating an expected value and a found value.
	 *
	 * @param found the object that was found
	 * @param expected the object that was expected
	 * @return a formatted string describing the expected and found values
	 */
	public static String expectedButFound(final Object found, final Object expected) {
		return Strings.parenthesize(Strings.paste(Strings.quote(expected), "expected but",
				Strings.quote(found), "found"));
	}

	/**
	 * Constructs a formatted message indicating that a minimum expected value was not met,
	 * showing both the found and expected values in the message.
	 *
	 * @param found the value that was found or encountered
	 * @param expected the minimum value that was expected
	 * @return a formatted string message indicating the expected minimum value and the actual found value
	 */
	public static String atLeastExpectedButFound(final Object found, final Object expected) {
		return Strings.parenthesize(Strings.paste("at least", Strings.quote(expected),
				"expected but", Strings.quote(found), "found"));
	}

	/**
	 * Constructs a string message indicating that at most an expected value was required but a different value was found.
	 *
	 * @param found the value that was actually found
	 * @param expected the value that was at most expected
	 * @return a formatted message string indicating the discrepancy between the expected and found values
	 */
	public static String atMostExpectedButFound(final Object found, final Object expected) {
		return Strings.parenthesize(Strings.paste("at most", Strings.quote(expected),
				"expected but", Strings.quote(found), "found"));
	}

	//////////////////////////////////////////////

	/**
	 * Generates a string describing a value found between an expected range.
	 *
	 * @param found        the object that was found
	 * @param expectedFrom the lower boundary of the expected range
	 * @param expectedTo   the upper boundary of the expected range
	 * @return a string describing the range and the found value
	 */
	public static String betweenExpectedButFound(final Object found, final Object expectedFrom,
			final Object expectedTo) {
		return betweenExpectedButFound(found, expectedFrom, expectedTo, true, false);
	}

	/**
	 * Evaluates if a given object lies between an expected range and generates a descriptive result.
	 *
	 * @param found the object that is being evaluated for its position within the range
	 * @param expectedFrom the lower bound of the expected range
	 * @param expectedTo the upper bound of the expected range
	 * @param isUpperInclusive a boolean indicating whether the upper bound of the range is inclusive
	 * @return a String describing the result of the evaluation
	 */
	public static String betweenExpectedButFound(final Object found, final Object expectedFrom,
			final Object expectedTo, final boolean isUpperInclusive) {
		return betweenExpectedButFound(found, expectedFrom, expectedTo, true, isUpperInclusive);
	}

	/**
	 * Generates a descriptive string indicating that a found value was outside the expected range.
	 *
	 * @param found          The value that was found.
	 * @param expectedFrom   The lower bound of the expected range.
	 * @param expectedTo     The upper bound of the expected range.
	 * @param isLowerInclusive Indicates whether the lower bound is inclusive.
	 * @param isUpperInclusive Indicates whether the upper bound is inclusive.
	 * @return A formatted string describing the value range expectations and the discrepancy.
	 */
	public static String betweenExpectedButFound(final Object found, final Object expectedFrom,
			final Object expectedTo, final boolean isLowerInclusive,
			final boolean isUpperInclusive) {
		return Strings.parenthesize(Strings.paste(
				"between", Strings.quote(expectedFrom),
				isLowerInclusive ? "(inclusive)" : "(exclusive)", "and",
				Strings.quote(expectedTo), isUpperInclusive ? "(inclusive)" : "(exclusive)",
				"expected but", Strings.quote(found), "found"));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares two objects and generates a formatted string indicating that the objects are not equal.
	 *
	 * @param a the first object to compare
	 * @param b the second object to compare
	 * @return a string representation stating that the first object is not equal to the second object
	 */
	public static String isNotEqualTo(final Object a, final Object b) {
		return Strings.parenthesize(Strings.paste(Strings.quote(a), "is not equal to",
				Strings.quote(b)));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks that the specified object reference is not {@code null} and returns it.
	 * This method is primarily used to enforce the requirement of non-null arguments
	 * in methods or constructors.
	 *
	 * @param <T> the type of the object reference
	 * @param object the object reference to check for nullity
	 * @return the non-{@code null} object reference that was validated
	 * @throws NullPointerException if the specified object reference is {@code null}
	 */
	public static <T> T requireNonNull(final T object) {
		if (CHECK_ARGS) {
			return requireNonNull(object, NAME, 1);
		}
		return object;
	}

	/**
	 * Ensures that the provided object is not null and throws a {@link NullPointerException} with the specified name if the object is null.
	 * This method helps validate that required parameters are non-null.
	 *
	 * @param <T> the type of the object to check for non-null
	 * @param object the object to be checked for nullity
	 * @param name the name of the object or parameter, used in the exception message if the object is null
	 * @return the non-null object provided as a parameter
	 * @throws NullPointerException if the object is null
	 */
	public static <T> T requireNonNull(final T object, final String name) {
		if (CHECK_ARGS) {
			return requireNonNull(object, name, 1);
		}
		return object;
	}

	/**
	 * Ensures that the specified object reference is not null. If the object is null,
	 * a NullPointerException is thrown with a message indicating the name of the argument
	 * and providing additional details optionally.
	 *
	 * @param <T> the type of the object reference
	 * @param object the object reference to check for null
	 * @param name the name of the argument being checked
	 * @param stackIndex the index of the stack trace to retrieve additional debugging information
	 * @return the validated object reference if it is not null
	 * @throws NullPointerException if the specified object is null
	 */
	public static <T> T requireNonNull(final T object, final String name, final int stackIndex) {
		if (CHECK_ARGS && object == null) {
			throw new NullPointerException(Strings.paste(
					"The specified argument", Strings.quote(name), "is null",
					IO.getSeverityLevel().isDebug() ?
							Tests.getStackTraceMessage(stackIndex + 1) : EMPTY));
		}
		return object;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Ensures that the provided object matches the expected object. If the objects do not match,
	 * throws an IllegalArgumentException.
	 *
	 * @param <T> the type of the objects being compared
	 * @param found the object that was found
	 * @param expected the object that is expected
	 * @return the found object if it matches the expected object
	 * @throws NullPointerException if the found object is null
	 * @throws IllegalArgumentException if the found object does not equal the expected object
	 */
	public static <T> T require(final T found, final T expected) {
		if (CHECK_ARGS && !requireNonNull(found).equals(expected)) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAME, "is wrong",
					expectedButFound(found, expected)));
		}
		return found;
	}

	//////////////////////////////////////////////

	/**
	 * Ensures that the two specified objects are equal. If argument checking is enabled
	 * and the objects are not equal, an IllegalArgumentException is thrown.
	 *
	 * @param <T> the type of the objects being compared
	 * @param a the first object to be compared; must not be null
	 * @param b the second object to be compared; must not be null
	 * @throws NullPointerException     if either {@code a} or {@code b} is null
	 * @throws IllegalArgumentException if argument checking is enabled and {@code a} is not equal to {@code b}
	 */
	public static <T> void requireEquals(final T a, final T b) {
		if (CHECK_ARGS && !requireNonNull(a).equals(requireNonNull(b))) {
			throw new IllegalArgumentException(Strings.paste("The specified", NAMES,
					"are not equal", isNotEqualTo(a, b)));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Ensures that the given condition is false. If the condition is true and argument checking
	 * is enabled, throws an IllegalArgumentException with the provided message.
	 *
	 * @param found   the condition to evaluate; must be false
	 * @param message the exception message to use if the condition is true
	 * @throws IllegalArgumentException if argument checking is enabled and the condition is true
	 */
	public static void requireFalse(final boolean found, final String message) {
		if (CHECK_ARGS && found) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Ensures that the specified condition is true. If the condition is false,
	 * an {@link IllegalArgumentException} is thrown with the provided message.
	 *
	 * @param found the condition to be checked; must be true to avoid an exception
	 * @param message the message to be included in the exception if the condition is false
	 */
	public static void requireTrue(final boolean found, final String message) {
		if (CHECK_ARGS && !found) {
			throw new IllegalArgumentException(message);
		}
	}
}
