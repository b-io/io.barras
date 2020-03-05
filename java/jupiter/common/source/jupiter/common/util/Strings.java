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
package jupiter.common.util;

import static jupiter.common.util.Arrays.DELIMITER;
import static jupiter.common.util.Characters.COLON;
import static jupiter.common.util.Characters.DOUBLE_QUOTE;
import static jupiter.common.util.Characters.HYPHEN;
import static jupiter.common.util.Characters.LEFT_BRACE;
import static jupiter.common.util.Characters.LEFT_BRACKET;
import static jupiter.common.util.Characters.LEFT_PARENTHESIS;
import static jupiter.common.util.Characters.LEFT_QUOTE;
import static jupiter.common.util.Characters.RIGHT_BRACE;
import static jupiter.common.util.Characters.RIGHT_BRACKET;
import static jupiter.common.util.Characters.RIGHT_PARENTHESIS;
import static jupiter.common.util.Characters.RIGHT_QUOTE;
import static jupiter.common.util.Characters.SINGLE_QUOTE;
import static jupiter.common.util.Characters.SPACE;
import static jupiter.common.util.Formats.DEFAULT_LINE_LENGTH;
import static jupiter.common.util.Formats.DEFAULT_LOCALE;
import static jupiter.common.util.Formats.NEW_LINE;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.IParsers;
import jupiter.common.map.parser.StringParser;
import jupiter.common.map.remover.StringRemover;
import jupiter.common.map.wrapper.StringWrapper;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.list.Index;
import jupiter.common.struct.list.SortedList;
import jupiter.common.test.Arguments;
import jupiter.common.test.ArrayArguments;
import jupiter.common.test.IntegerArguments;
import jupiter.common.time.Dates;

public class Strings {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String EMPTY = "";
	public static final String[] EMPTY_ARRAY = new String[] {};

	protected static final StringParser PARSER = IParsers.STRING_PARSER;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String NULL = "null";

	public static final String STAR = "*";

	public static final String UNICODE = "\\u";

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final char DEFAULT_PROGRESS_SYMBOL = HYPHEN;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile int INITIAL_CAPACITY = 15;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final StringWrapper SINGLE_QUOTER = new StringWrapper(SINGLE_QUOTE, SINGLE_QUOTE);
	public static final StringWrapper DOUBLE_QUOTER = new StringWrapper(DOUBLE_QUOTE, DOUBLE_QUOTE);
	public static final StringWrapper QUOTER = new StringWrapper(LEFT_QUOTE, RIGHT_QUOTE);
	public static final StringRemover UNQUOTER = new StringRemover(
			join(SINGLE_QUOTE, DOUBLE_QUOTE, LEFT_QUOTE, RIGHT_QUOTE));

	public static final StringWrapper PARENTHESER = new StringWrapper(LEFT_PARENTHESIS,
			RIGHT_PARENTHESIS);
	public static final StringWrapper BRACKETER = new StringWrapper(LEFT_BRACKET, RIGHT_BRACKET);
	public static final StringWrapper BRACER = new StringWrapper(LEFT_BRACE, RIGHT_BRACE);

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final Comparator<String> COMPARATOR = new Comparator<String>() {
		@Override
		public int compare(final String a, final String b) {
			return Strings.compare(a, b);
		}
	};

	public static final Comparator<String> IGNORE_CASE_COMPARATOR = new Comparator<String>() {
		@Override
		public int compare(final String a, final String b) {
			return Strings.compareIgnoreCase(a, b);
		}
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Strings}.
	 */
	protected Strings() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} converted from the specified {@link Object}.
	 * <p>
	 * @param object the {@link Object} to convert
	 * <p>
	 * @return a {@link String} converted from the specified {@link Object}
	 */
	public static String convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an Unicode {@link String} converted from the specified {@link String}.
	 * <p>
	 * @param text the {@link String} to convert
	 * <p>
	 * @return an Unicode {@link String} converted from the specified {@link String}
	 */
	public static String toUnicode(final String text) {
		final StringBuilder builder = createBuilder(6 * text.length());
		for (int i = 0; i < text.length(); ++i) {
			builder.append(Characters.toUnicode(text.charAt(i)));
		}
		return builder.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String toLowerCase(final String text) {
		return text.toLowerCase(DEFAULT_LOCALE);
	}

	public static String toUpperCase(final String text) {
		return text.toUpperCase(DEFAULT_LOCALE);
	}

	//////////////////////////////////////////////

	public static String toCase(final String text) {
		final StringBuilder builder = createBuilder(text.length() + countUpperCase(text) +
				countTitleCase(text));
		for (final char token : text.toCharArray()) {
			if (Character.isUpperCase(token) || Character.isTitleCase(token)) {
				builder.append(SPACE).append(Character.toLowerCase(token));
			} else {
				builder.append(token);
			}
		}
		return builder.toString();
	}

	public static String toCamelCase(final String text) {
		final StringBuilder builder = createBuilder(text.length() - count(text, SPACE));
		boolean isSpace = false;
		for (final char token : text.toCharArray()) {
			if (token == SPACE) {
				isSpace = true;
			} else if (isSpace) {
				builder.append(Character.toUpperCase(token));
				isSpace = false;
			} else {
				builder.append(Character.toLowerCase(token));
			}
		}
		return builder.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link String} from the specified array.
	 * <p>
	 * @param array the array to convert
	 * <p>
	 * @return an array of {@link String} from the specified array
	 */
	public static String[] toArray(final Object[] array) {
		return PARSER.callToArray(array);
	}

	/**
	 * Returns an array of {@link String} from the specified array.
	 * <p>
	 * @param array the array to convert
	 * <p>
	 * @return an array of {@link String} from the specified array
	 */
	public static String[] asArray(final Object... array) {
		return toArray(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 2D array of {@link String} from the specified 2D array.
	 * <p>
	 * @param array2D the 2D array to convert
	 * <p>
	 * @return a 2D array of {@link String} from the specified 2D array
	 */
	public static String[][] toArray2D(final Object[][] array2D) {
		return PARSER.callToArray2D(array2D);
	}

	/**
	 * Returns a 2D array of {@link String} from the specified 2D array.
	 * <p>
	 * @param array2D the 2D array to convert
	 * <p>
	 * @return a 2D array of {@link String} from the specified 2D array
	 */
	public static String[][] asArray2D(final Object[]... array2D) {
		return toArray2D(array2D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a 3D array of {@link String} from the specified 3D array.
	 * <p>
	 * @param array3D the 3D array to convert
	 * <p>
	 * @return a 3D array of {@link String} from the specified 3D array
	 */
	public static String[][][] toArray3D(final Object[][][] array3D) {
		return PARSER.callToArray3D(array3D);
	}

	/**
	 * Returns a 3D array of {@link String} from the specified 3D array.
	 * <p>
	 * @param array3D the 3D array to convert
	 * <p>
	 * @return a 3D array of {@link String} from the specified 3D array
	 */
	public static String[][][] asArray3D(final Object[][]... array3D) {
		return toArray3D(array3D);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an array of {@link String} from the specified {@link Collection}.
	 * <p>
	 * @param collection the {@link Collection} to convert
	 * <p>
	 * @return an array of {@link String} from the specified {@link Collection}
	 */
	public static String[] collectionToArray(final Collection<?> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link String} from the specified {@code T} array
	 */
	public static <T> ExtendedList<String> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link String} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedList<String> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link String} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<String> toLinkedList(final T[] array) {
		return PARSER.callToLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link String} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> ExtendedLinkedList<String> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an {@link ExtendedList} of {@link String} from the specified {@link Collection}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection the {@link Collection} of {@code E} element type to convert
	 * <p>
	 * @return an {@link ExtendedList} of {@link String} from the specified {@link Collection}
	 */
	public static <E> ExtendedList<String> collectionToList(final Collection<E> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link String} from the specified
	 * {@link Collection}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection the {@link Collection} of {@code E} element type to convert
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link String} from the specified {@link Collection}
	 */
	public static <E> ExtendedLinkedList<String> collectionToLinkedList(
			final Collection<E> collection) {
		return PARSER.callCollectionToLinkedList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert to convert
	 * <p>
	 * @return a {@link Set} of {@link String} from the specified {@code T} array
	 */
	public static <T> Set<String> toSet(final T[] array) {
		return PARSER.callToSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array the {@code T} array to convert to convert
	 * <p>
	 * @return a {@link Set} of {@link String} from the specified {@code T} array
	 */
	@SuppressWarnings({"unchecked", "varargs"})
	public static <T> Set<String> asSet(final T... array) {
		return toSet(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link String} from the specified {@link Collection}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection the {@link Collection} of {@code E} element type to convert
	 * <p>
	 * @return a {@link Set} of {@link String} from the specified {@link Collection}
	 */
	public static <E> Set<String> collectionToSet(final Collection<E> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link String} bar by default.
	 * <p>
	 * @return a {@link String} bar by default
	 */
	public static String createBar() {
		return createBar(DEFAULT_LINE_LENGTH, DEFAULT_PROGRESS_SYMBOL);
	}

	/**
	 * Creates a {@link String} bar of the specified length with the default progress {@code char}
	 * symbol.
	 * <p>
	 * @param length the length of the bar to create
	 * <p>
	 * @return a {@link String} bar of the specified length with the default progress {@code char}
	 *         symbol
	 */
	public static String createBar(final int length) {
		return createBar(length, DEFAULT_PROGRESS_SYMBOL);
	}

	/**
	 * Creates a {@link String} bar of the default length with the specified progress {@code char}
	 * symbol.
	 * <p>
	 * @param progressSymbol the progress {@code char} symbol of the bar to create
	 * <p>
	 * @return a {@link String} bar of the default length with the specified progress {@code char}
	 *         symbol
	 */
	public static String createBar(final char progressSymbol) {
		return createBar(DEFAULT_LINE_LENGTH, progressSymbol);
	}

	/**
	 * Creates a {@link String} bar of the specified length with the specified progress {@code char}
	 * symbol.
	 * <p>
	 * @param length         the length of the bar to create
	 * @param progressSymbol the progress {@code char} symbol of the bar to create
	 * <p>
	 * @return a {@link String} bar of the specified length with the specified progress {@code char}
	 *         symbol
	 */
	public static String createBar(final int length, final char progressSymbol) {
		return repeat(progressSymbol, length);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static StringBuilder createBuilder() {
		return createBuilder(INITIAL_CAPACITY);
	}

	public static StringBuilder createBuilder(final int capacity) {
		return new StringBuilder(capacity);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a pseudorandom, uniformly distributed {@link String} of the specified length.
	 * <p>
	 * @param length the length of the random {@link String} to generate
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@link String} of the specified length
	 */
	public static String random(final int length) {
		// Check the arguments
		IntegerArguments.requireNonNegative(length);

		// Generate
		final StringBuilder builder = createBuilder(length);
		for (int i = 0; i < length; ++i) {
			builder.append(Characters.random());
		}
		return builder.toString();
	}

	/**
	 * Returns a pseudorandom, uniformly distributed {@link String} of the specified length
	 * generated with {@code char} values between the specified bounds.
	 * <p>
	 * @param length     the length of the random {@link String} to generate
	 * @param lowerBound the lower bound of the {@code char} value to generate (inclusive)
	 * @param upperBound the upper bound of the {@code char} value to generate (exclusive)
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@link String} of the specified length
	 *         generated with {@code char} values between the specified bounds
	 */
	public static String random(final int length, final char lowerBound, final char upperBound) {
		// Check the arguments
		IntegerArguments.requireNonNegative(length);

		// Generate
		final StringBuilder builder = createBuilder(length);
		for (int i = 0; i < length; ++i) {
			builder.append(Characters.random(lowerBound, upperBound));
		}
		return builder.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String repeat(final char token, final int length) {
		return repeat(toString(token), length);
	}

	public static String repeat(final String text, final int length) {
		// Check the arguments
		if (isNullOrEmpty(text)) {
			return text;
		}
		IntegerArguments.requireNonNegative(length);

		// Initialize
		final StringBuilder builder = createBuilder(length * text.length());

		// Repeat the text
		for (int i = 0; i < length; ++i) {
			builder.append(text);
		}
		return builder.toString();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by capitalizing the first character of the specified
	 * {@link String}.
	 * <p>
	 * @param text the @link String} to capitalize (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by capitalizing the first character of the specified
	 *         {@link String}
	 */
	public static String capitalizeFirst(final String text) {
		// Check the arguments
		if (isNullOrEmpty(text)) {
			return text;
		}

		// Capitalize the first character of the text
		return Character.toTitleCase(text.charAt(0)) + text.substring(1);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by capitalizing all the whitespace-separated words of
	 * the specified {@link String}.
	 * <b>Note:</b> To also convert the remaining characters to lowercase, use
	 * {@link #capitalizeStrictly(String)}.
	 * <p>
	 * @param text the @link String} to capitalize (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by capitalizing all the whitespace-separated words of
	 *         the specified {@link String}
	 *
	 * @see #capitalizeStrictly(String)
	 * @see #uncapitalize(String)
	 */
	public static String capitalize(final String text) {
		return capitalize(text, SPACE);
	}

	/**
	 * Returns the {@link String} constructed by capitalizing all the words of the specified
	 * {@link String} separated by the specified {@code char} delimiters.
	 * <b>Note:</b> To also convert the remaining characters to lowercase, use
	 * {@link #capitalizeStrictly(String, char[])}.
	 * <p>
	 * @param text       the @link String} to capitalize (may be {@code null})
	 * @param delimiters the {@code char} delimiters (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by capitalizing all the words of the specified
	 *         {@link String} separated by the specified {@code char} delimiters
	 *
	 * @see #capitalizeStrictly(String, char[])
	 * @see #uncapitalize(String, char[])
	 */
	public static String capitalize(final String text, final char... delimiters) {
		// Check the arguments
		if (isNullOrEmpty(text) || Characters.isNullOrEmpty(delimiters)) {
			return text;
		}

		// Capitalize all the words of the text separated by the delimiters
		final char[] array = text.toCharArray();
		boolean capitalize = true;
		for (int i = 0; i < array.length; i++) {
			final char token = array[i];
			if (Characters.contains(delimiters, token)) {
				capitalize = true;
			} else if (capitalize) {
				array[i] = Character.toTitleCase(token);
				capitalize = false;
			}
		}
		return new String(array);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by capitalizing all the whitespace-separated words of
	 * the specified {@link String} and converting the remaining characters to lowercase.
	 * <p>
	 * @param text the @link String} to capitalize (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by capitalizing all the whitespace-separated words of
	 *         the specified {@link String} and converting the remaining characters to lowercase
	 *
	 * @see #uncapitalize(String)
	 */
	public static String capitalizeStrictly(final String text) {
		return capitalizeStrictly(text, SPACE);
	}

	/**
	 * Returns the {@link String} constructed by capitalizing all the words of the specified
	 * {@link String} separated by the specified {@code char} delimiters and converting the
	 * remaining characters to lowercase.
	 * <p>
	 * @param text       the @link String} to capitalize (may be {@code null})
	 * @param delimiters the {@code char} delimiters (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by capitalizing all the words of the specified
	 *         {@link String} separated by the specified {@code char} delimiters and converting the
	 *         remaining characters to lowercase
	 *
	 * @see #uncapitalize(String, char[])
	 */
	public static String capitalizeStrictly(final String text, final char... delimiters) {
		// Check the arguments
		if (isNullOrEmpty(text) || Characters.isNullOrEmpty(delimiters)) {
			return text;
		}

		// Convert the text to lowercase and
		// capitalize all the words of the text separated by the delimiters
		return capitalize(text.toLowerCase(), delimiters);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by uncapitalizing all the whitespace-separated words
	 * of the specified {@link String}.
	 * <p>
	 * @param text the @link String} to uncapitalize (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by uncapitalizing all the whitespace-separated words
	 *         of the specified {@link String}
	 *
	 * @see #capitalize(String)
	 */
	public static String uncapitalize(final String text) {
		return uncapitalize(text, SPACE);
	}

	/**
	 * Returns the {@link String} constructed by uncapitalizing all the words of the specified
	 * {@link String} separated by the specified {@code char} delimiters.
	 * <p>
	 * @param text       the @link String} to uncapitalize (may be {@code null})
	 * @param delimiters the {@code char} delimiters (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by uncapitalizing all the words of the specified
	 *         {@link String} separated by the specified {@code char} delimiters
	 *
	 * @see #capitalize(String, char[])
	 */
	public static String uncapitalize(final String text, final char... delimiters) {
		// Check the arguments
		if (isNullOrEmpty(text) || Characters.isNullOrEmpty(delimiters)) {
			return text;
		}

		// Uncapitalize all the words of the text separated by the delimiters
		final char[] array = text.toCharArray();
		boolean uncapitalize = true;
		for (int i = 0; i < array.length; i++) {
			final char token = array[i];
			if (Characters.contains(delimiters, token)) {
				uncapitalize = true;
			} else if (uncapitalize) {
				array[i] = Character.toLowerCase(token);
				uncapitalize = false;
			}
		}
		return new String(array);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code char} token in the specified
	 * {@link String}.
	 * <p>
	 * @param text  the {@link String} to count from (may be {@code null})
	 * @param token the {@code char} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code char} token in the specified
	 *         {@link String}
	 */
	public static int count(final String text, final char token) {
		// Check the arguments
		if (text == null) {
			return 0;
		}

		// Count the token in the text
		return Characters.count(text.toCharArray(), token);
	}

	/**
	 * Returns the number of occurrences of the specified {@code char} tokens in the specified
	 * {@link String}.
	 * <p>
	 * @param text   the {@link String} to count from (may be {@code null})
	 * @param tokens the {@code char} tokens to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified {@code char} tokens in the specified
	 *         {@link String}
	 */
	public static int count(final String text, final char[] tokens) {
		// Check the arguments
		if (text == null || tokens == null) {
			return 0;
		}

		// Count the tokens in the text
		return Characters.count(text.toCharArray(), tokens);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified token {@link String} in the specified
	 * {@link String}.
	 * <p>
	 * @param text  the {@link String} to count from (may be {@code null})
	 * @param token the token {@link String} to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified token {@link String} in the specified
	 *         {@link String}
	 */
	public static int countString(final String text, final String token) {
		int occurrenceCount = 0;
		if (text != null && token != null) {
			int index = -1;
			while ((index = text.indexOf(token, index + 1)) >= 0) {
				++occurrenceCount;
			}
		}
		return occurrenceCount;
	}

	/**
	 * Returns the number of occurrences of the specified array of token {@link String} in the
	 * specified {@link String}.
	 * <p>
	 * @param text   the {@link String} to count from (may be {@code null})
	 * @param tokens the array of token {@link String} to count (may be {@code null})
	 * <p>
	 * @return the number of occurrences of the specified array of token {@link String} in the
	 *         specified {@link String}
	 */
	public static int countString(final String text, final String[] tokens) {
		int occurrenceCount = 0;
		if (text != null && tokens != null) {
			for (final String token : tokens) {
				occurrenceCount += countString(text, token);
			}
		}
		return occurrenceCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of lines of the specified {@link String}.
	 * <p>
	 * @param text a {@link String}
	 * <p>
	 * @return the number of lines of the specified {@link String}
	 */
	public static int countLines(final String text) {
		return countLines(text, false);
	}

	/**
	 * Returns the number of lines of the specified {@link String}.
	 * <p>
	 * @param text           a {@link String} (may be {@code null})
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines of the specified {@link String}
	 */
	public static int countLines(final String text, final boolean skipEmptyLines) {
		// Check the arguments
		if (text == null) {
			return 0;
		}

		// Initialize
		final Matcher matcher = Pattern.compile("\r\n|\r|\n").matcher(text);

		// Count the lines
		int lineCount;
		if (skipEmptyLines) {
			lineCount = 0;
			int previousIndex = matcher.regionStart();
			while (matcher.find()) {
				if (previousIndex != matcher.start()) {
					++lineCount;
				}
				previousIndex = matcher.end();
			}
			if (previousIndex != matcher.regionEnd()) {
				++lineCount;
			}
		} else {
			lineCount = 1;
			while (matcher.find()) {
				++lineCount;
			}
		}
		return lineCount;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of lower case characters in the specified {@link String}.
	 * <p>
	 * @param text the {@link String} to count from (may be {@code null})
	 * <p>
	 * @return the number of lower case characters in the specified {@link String}
	 */
	public static int countLowerCase(final String text) {
		// Check the arguments
		if (text == null) {
			return 0;
		}

		// Count the lower case characters in the text
		return Characters.countLowerCase(text.toCharArray());
	}

	/**
	 * Returns the number of upper case characters in the specified {@link String}.
	 * <p>
	 * @param text the {@link String} to count from (may be {@code null})
	 * <p>
	 * @return the number of upper case characters in the specified {@link String}
	 */
	public static int countUpperCase(final String text) {
		// Check the arguments
		if (text == null) {
			return 0;
		}

		// Count the upper case characters in the text
		return Characters.countUpperCase(text.toCharArray());
	}

	/**
	 * Returns the number of title case characters in the specified {@link String}.
	 * <p>
	 * @param text the {@link String} to count from (may be {@code null})
	 * <p>
	 * @return the number of title case characters in the specified {@link String}
	 */
	public static int countTitleCase(final String text) {
		// Check the arguments
		if (text == null) {
			return 0;
		}

		// Count the title case characters in the text
		return Characters.countTitleCase(text.toCharArray());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the escaped representative {@link String} of the specified unescaped content (i.e.
	 * without traces of offending characters that can prevent parsing).
	 * <p>
	 * @param content the content {@link Object} to escape
	 * <p>
	 * @return the escaped representative {@link String} of the specified unescaped content (i.e.
	 *         without traces of offending characters that can prevent parsing)
	 */
	public static String escape(final Object content) {
		return toString(content).replaceAll("\\\\", "\\\\\\\\")
				.replaceAll("\"", "\\\\\"")
				.replaceAll("\b", "\\\\b")
				.replaceAll("\f", "\\\\f")
				.replaceAll("\n", "\\\\n")
				.replaceAll("\r", "\\\\r")
				.replaceAll("\t", "\\\\t");
	}

	/**
	 * Returns the unescaped representative {@link String} of the specified escaped content (i.e.
	 * with traces of offending characters that can prevent parsing).
	 * <p>
	 * @param content the content {@link Object} to unescape
	 * <p>
	 * @return the unescaped representative {@link String} of the specified escaped content (i.e.
	 *         with traces of offending characters that can prevent parsing)
	 */
	public static String unescape(final Object content) {
		return toString(content).replaceAll("\\\\t", "\t")
				.replaceAll("\\\\r", "\r")
				.replaceAll("\\\\n", "\n")
				.replaceAll("\\\\f", "\f")
				.replaceAll("\\\\b", "\b")
				.replaceAll("\\\\\"", "\"")
				.replaceAll("\\\\\\\\", "\\\\");
	}

	//////////////////////////////////////////////

	/**
	 * Returns the escaped representative {@link String} of the specified unescaped content for
	 * regular expressions (i.e. without traces of offending characters that can prevent parsing a
	 * regular expression).
	 * <p>
	 * @param content the content {@link Object} to escape
	 * <p>
	 * @return the escaped representative {@link String} of the specified unescaped content for
	 *         regular expressions (i.e. without traces of offending characters that can prevent
	 *         parsing a regular expression)
	 */
	public static String escapeRegex(final Object content) {
		return Pattern.quote(toString(content));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array of {@link String}, or
	 * {@code "null"} if it is {@code null}.
	 * <p>
	 * @param array an array of {@link String} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link String}, or
	 *         {@code "null"} if it is {@code null}
	 */
	public static String join(final String... array) {
		// Check the arguments
		if (array == null) {
			return NULL;
		}

		// Initialize
		final StringBuilder builder = createBuilder(array.length * INITIAL_CAPACITY);

		// Join the elements
		for (final String element : array) {
			builder.append(element);
		}
		return builder.toString();
	}

	/**
	 * Returns a representative {@link String} of the specified array, or {@code "null"} if it is
	 * {@code null}.
	 * <p>
	 * @param array an array of {@link Object} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified array, or {@code "null"} if it is
	 *         {@code null}
	 */
	public static String join(final Object... array) {
		// Check the arguments
		if (array == null) {
			return NULL;
		}

		// Initialize
		final StringBuilder builder = createBuilder(array.length * INITIAL_CAPACITY);

		// Join the elements
		for (final Object element : array) {
			builder.append(toString(element));
		}
		return builder.toString();
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection}, or
	 * {@code "null"} if it is {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection}, or
	 *         {@code "null"} if it is {@code null}
	 */
	public static String join(final Collection<?> collection) {
		// Check the arguments
		if (collection == null) {
			return NULL;
		}

		// Initialize
		final StringBuilder builder = createBuilder(collection.size() * INITIAL_CAPACITY);

		// Join the elements
		for (final Object element : collection) {
			builder.append(toString(element));
		}
		return builder.toString();
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array joined by
	 * {@link Arrays#DELIMITER}, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param array an array of {@link Object} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified array joined by
	 *         {@link Arrays#DELIMITER}, or {@code "null"} if it is {@code null}
	 */
	public static String joinWith(final Object[] array) {
		return joinWith(array, toString(DELIMITER));
	}

	/**
	 * Returns a representative {@link String} of the specified array joined by the specified
	 * {@code char} delimiter, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param array     an array of {@link Object} (may be {@code null})
	 * @param delimiter the {@code char} delimiter
	 * <p>
	 * @return a representative {@link String} of the specified array joined by the specified
	 *         {@code char} delimiter, or {@code "null"} if it is {@code null}
	 */
	public static String joinWith(final Object[] array, final char delimiter) {
		return joinWith(array, toString(delimiter));
	}

	/**
	 * Returns a representative {@link String} of the specified array joined by the specified
	 * delimiting {@link String}, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param array     an array of {@link Object} (may be {@code null})
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array joined by the specified
	 *         delimiting {@link String}, or {@code "null"} if it is {@code null}
	 */
	public static String joinWith(final Object[] array, final String delimiter) {
		// Check the arguments
		if (array == null) {
			return NULL;
		}
		Arguments.requireNonNull(delimiter, "delimiter");

		// Initialize
		final StringBuilder builder = createBuilder(
				array.length * (INITIAL_CAPACITY + delimiter.length()));

		// Join the elements
		int i = 0;
		if (array.length > 0) {
			builder.append(toString(array[i++]));
			while (i < array.length) {
				builder.append(delimiter).append(toString(array[i++]));
			}
		}
		return builder.toString();
	}

	/**
	 * Returns a representative {@link String} of the specified array joined by
	 * {@link Arrays#DELIMITER} and wrapped by {@code wrapper}, or {@code "null"} if it is
	 * {@code null}.
	 * <p>
	 * @param array   an array of {@link Object} (may be {@code null})
	 * @param wrapper an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array joined by
	 *         {@link Arrays#DELIMITER} and wrapped by {@code wrapper}, or {@code "null"} if it is
	 *         {@code null}
	 */
	public static String joinWith(final Object[] array, final ObjectToStringMapper wrapper) {
		return joinWith(array, toString(DELIMITER), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified array joined by the specified
	 * {@code char} delimiter and wrapped by {@code wrapper}, or {@code "null"} if it is
	 * {@code null}.
	 * <p>
	 * @param array     an array of {@link Object} (may be {@code null})
	 * @param delimiter the {@code char} delimiter
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array joined by the specified
	 *         {@code char} delimiter and wrapped by {@code wrapper}, or {@code "null"} if it is
	 *         {@code null}
	 */
	public static String joinWith(final Object[] array, final char delimiter,
			final ObjectToStringMapper wrapper) {
		return joinWith(array, toString(delimiter), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified array joined by the specified
	 * delimiting {@link String} and wrapped by {@code wrapper}, or {@code "null"} if it is
	 * {@code null}.
	 * <p>
	 * @param array     an array of {@link Object} (may be {@code null})
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array joined by the specified
	 *         delimiting {@link String} and wrapped by {@code wrapper}, or {@code "null"} if it is
	 *         {@code null}
	 */
	public static String joinWith(final Object[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		// Check the arguments
		if (array == null) {
			return NULL;
		}
		Arguments.requireNonNull(delimiter, "delimiter");
		Arguments.requireNonNull(wrapper, "wrapper");

		// Initialize
		final StringBuilder builder = createBuilder(
				array.length * (INITIAL_CAPACITY + delimiter.length()));

		// Wrap the elements and join them
		int i = 0;
		if (array.length > 0) {
			builder.append(wrapper.call(array[i++]));
			while (i < array.length) {
				builder.append(delimiter).append(wrapper.call(array[i++]));
			}
		}
		return builder.toString();
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by the
	 * specified {@code char} delimiter, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param delimiter  the {@code char} delimiter
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by the
	 *         specified {@code char} delimiter, or {@code "null"} if it is {@code null}
	 */
	public static String joinWith(final Collection<?> collection, final char delimiter) {
		return joinWith(collection, toString(delimiter));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by the
	 * specified delimiting {@link String}, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param delimiter  the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by the
	 *         specified delimiting {@link String}, or {@code "null"} if it is {@code null}
	 */
	public static String joinWith(final Collection<?> collection, final String delimiter) {
		// Check the arguments
		if (collection == null) {
			return NULL;
		}
		Arguments.requireNonNull(delimiter, "delimiter");

		// Initialize
		final StringBuilder builder = createBuilder(
				collection.size() * (INITIAL_CAPACITY + delimiter.length()));
		final Iterator<?> iterator = collection.iterator();

		// Join the elements
		if (iterator.hasNext()) {
			builder.append(iterator.next());
			while (iterator.hasNext()) {
				builder.append(delimiter).append(toString(iterator.next()));
			}
		}
		return builder.toString();
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by the
	 * specified {@code char} delimiter and wrapped by {@code wrapper}, or {@code "null"} if it is
	 * {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param delimiter  the {@code char} delimiter
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by the
	 *         specified {@code char} delimiter and wrapped by {@code wrapper}, or {@code "null"} if
	 *         it is {@code null}
	 */
	public static String joinWith(final Collection<?> collection, final char delimiter,
			final ObjectToStringMapper wrapper) {
		return joinWith(collection, toString(delimiter), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} wrapped by
	 * {@code wrapper}, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} wrapped by
	 *         {@code wrapper}, or {@code "null"} if it is {@code null}
	 */
	public static String joinWith(final Collection<?> collection,
			final ObjectToStringMapper wrapper) {
		return joinWith(collection, toString(DELIMITER), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by the
	 * specified delimiting {@link String} and wrapped by {@code wrapper}, or {@code "null"} if it
	 * is {@code null}.
	 * <p>
	 * @param collection a {@link Collection} (may be {@code null})
	 * @param delimiter  the delimiting {@link String}
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by the
	 *         specified delimiting {@link String} and wrapped by {@code wrapper}, or {@code "null"}
	 *         if it is {@code null}
	 */
	public static String joinWith(final Collection<?> collection, final String delimiter,
			final ObjectToStringMapper wrapper) {
		// Check the arguments
		if (collection == null) {
			return NULL;
		}
		Arguments.requireNonNull(delimiter, "delimiter");
		Arguments.requireNonNull(wrapper, "wrapper");

		// Initialize
		final StringBuilder builder = createBuilder(
				collection.size() * (INITIAL_CAPACITY + delimiter.length()));
		final Iterator<?> iterator = collection.iterator();

		// Wrap the elements and join them
		if (iterator.hasNext()) {
			builder.append(wrapper.call(iterator.next()));
			while (iterator.hasNext()) {
				builder.append(delimiter).append(wrapper.call(iterator.next()));
			}
		}
		return builder.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by left-padding the specified {@link String} to the
	 * specified length.
	 * <p>
	 * @param text   a {@link String}
	 * @param length the length to pad to
	 * <p>
	 * @return the {@link String} constructed by left-padding the specified {@link String} to the
	 *         specified length
	 */
	public static String leftPad(final String text, final int length) {
		return leftPad(text, length, SPACE);
	}

	/**
	 * Returns the {@link String} constructed by left-padding the specified {@link String} to the
	 * specified length with the specified {@code char} token.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param length the length to pad to
	 * @param token  the {@code char} token to pad with
	 * <p>
	 * @return the {@link String} constructed by left-padding the specified {@link String} to the
	 *         specified length with the specified {@code char} token
	 */
	public static String leftPad(final String text, final int length, final char token) {
		// Check the arguments
		if (isNullOrEmpty(text) || length <= text.length()) {
			return text;
		}

		// Left-pad the text
		return repeat(token, length - text.length()).concat(text);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by right-padding the specified {@link String} to the
	 * specified length.
	 * <p>
	 * @param text   a {@link String}
	 * @param length the length to pad to
	 * <p>
	 * @return the {@link String} constructed by right-padding the specified {@link String} to the
	 *         specified length
	 */
	public static String rightPad(final String text, final int length) {
		return rightPad(text, length, SPACE);
	}

	/**
	 * Returns the {@link String} constructed by right-padding the specified {@link String} to the
	 * specified length with the specified {@code char} token.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param length the length to pad to
	 * @param token  the {@code char} token to pad with
	 * <p>
	 * @return the {@link String} constructed by right-padding the specified {@link String} to the
	 *         specified length with the specified {@code char} token
	 */
	public static String rightPad(final String text, final int length, final char token) {
		// Check the arguments
		if (isNullOrEmpty(text) || length <= text.length()) {
			return text;
		}

		// Right-pad the text
		return text.concat(repeat(token, length - text.length()));
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by center-padding the specified {@link String} to the
	 * specified length.
	 * <p>
	 * @param text   a {@link String}
	 * @param length the length to pad to
	 * <p>
	 * @return the {@link String} constructed by center-padding the specified {@link String} to the
	 *         specified length
	 */
	public static String centerPad(final String text, final int length) {
		return centerPad(text, length, SPACE);
	}

	/**
	 * Returns the {@link String} constructed by center-padding the specified {@link String} to the
	 * specified length with the specified {@code char} token.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param length the length to pad to
	 * @param token  the {@code char} token to pad with
	 * <p>
	 * @return the {@link String} constructed by center-padding the specified {@link String} to the
	 *         specified length with the specified {@code char} token
	 */
	public static String centerPad(final String text, final int length, final char token) {
		// Check the arguments
		if (isNullOrEmpty(text) || length <= text.length()) {
			return text;
		}

		// Center-pad the text
		return rightPad(repeat(token, (length - text.length()) / 2).concat(text), length, token);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by removing the character at the specified index in
	 * the specified {@link String}.
	 * <p>
	 * @param text  a {@link String} (may be {@code null})
	 * @param index the index of the character to remove
	 * <p>
	 * @return the {@link String} constructed by removing the character at the specified index in
	 *         the specified {@link String}
	 */
	public static String remove(final String text, final int index) {
		// Check the arguments
		if (isNullOrEmpty(text)) {
			return text;
		}
		ArrayArguments.requireIndex(index, text.length());

		// Remove the character at the index from the text
		return text.substring(0, index).concat(text.substring(index + 1));
	}

	/**
	 * Returns the {@link String} constructed by removing the characters at the specified distinct
	 * sorted indexes in the specified {@link String}.
	 * <p>
	 * @param text    a {@link String} (may be {@code null})
	 * @param indexes the distinct sorted indexes of the characters to remove (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by removing the characters at the specified distinct
	 *         sorted indexes in the specified {@link String}
	 */
	public static String remove(final String text, final int[] indexes) {
		// Check the arguments
		if (isNullOrEmpty(text) || Integers.isNullOrEmpty(indexes)) {
			return text;
		}

		// Remove the characters at the indexes from the text
		final StringBuilder builder = createBuilder(text.length() - indexes.length);
		int previousIndex = -1;
		for (final int index : indexes) {
			builder.append(text.substring(previousIndex + 1, index));
			previousIndex = index;
		}
		if (previousIndex + 1 < indexes.length) {
			builder.append(text.substring(previousIndex + 1));
		}
		return builder.toString();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by removing the first occurrence of any of the
	 * {@code char} tokens contained in the specified {@link String} from the specified
	 * {@link String}.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link String} containing the {@code char} tokens to remove (may be
	 *               {@code null})
	 * <p>
	 * @return the {@link String} constructed by removing the first occurrence of any of the
	 *         {@code char} tokens contained in the specified {@link String} from the specified
	 *         {@link String}
	 */
	public static String removeFirst(final String text, final String tokens) {
		// Check the arguments
		if (isNullOrEmpty(text) || isNullOrEmpty(tokens)) {
			return text;
		}

		// Remove the first occurrence of any of the tokens from the text
		return replaceFirst(text, bracketize(escapeRegex(tokens)), EMPTY);
	}

	/**
	 * Returns the {@link String} constructed by removing the last occurrence of any of the
	 * {@code char} tokens contained in the specified {@link String} from the specified
	 * {@link String}.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link String} containing the {@code char} tokens to remove (may be
	 *               {@code null})
	 * <p>
	 * @return the {@link String} constructed by removing the last occurrence of any of the
	 *         {@code char} tokens contained in the specified {@link String} from the specified
	 *         {@link String}
	 */
	public static String removeLast(final String text, final String tokens) {
		// Check the arguments
		if (isNullOrEmpty(text) || isNullOrEmpty(tokens)) {
			return text;
		}

		// Remove the last occurrence of any of the tokens from the text
		return replaceLast(text, bracketize(escapeRegex(tokens)), EMPTY);
	}

	/**
	 * Returns the {@link String} constructed by removing all the occurrences of the {@code char}
	 * tokens contained in the specified {@link String} from the specified {@link String}.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link String} containing the {@code char} tokens to remove (may be
	 *               {@code null})
	 * <p>
	 * @return the {@link String} constructed by removing all the occurrences of the {@code char}
	 *         tokens contained in the specified {@link String} from the specified {@link String}
	 */
	public static String removeAll(final String text, final String tokens) {
		// Check the arguments
		if (isNullOrEmpty(text) || isNullOrEmpty(tokens)) {
			return text;
		}

		// Remove all the occurrences of the tokens from the text
		return replaceAll(text, bracketize(escapeRegex(tokens)), EMPTY);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the specified {@link Collection} of {@link String} without the empty {@link String}.
	 * <p>
	 * @param <C>        the {@link Collection} type
	 * @param collection a {@link Collection} of {@link String}
	 * <p>
	 * @return the specified {@link Collection} of {@link String} without the empty {@link String}
	 */
	public static <C extends Collection<String>> C removeEmpty(final C collection) {
		Collections.removeAll(collection, EMPTY);
		return collection;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by replacing the character at the specified index in
	 * the specified {@link String} by the specified {@code char} token.
	 * <p>
	 * @param text        a {@link String} (may be {@code null})
	 * @param index       the index of the character to replace
	 * @param replacement the {@code char} token to replace by
	 * <p>
	 * @return the {@link String} constructed by replacing the character at the specified index in
	 *         the specified {@link String} by the specified {@code char} token
	 */
	public static String replace(final String text, final int index, final char replacement) {
		// Check the arguments
		if (isNullOrEmpty(text)) {
			return text;
		}
		ArrayArguments.requireIndex(index, text.length());

		// Replace the character at the index in the text by the replacement character
		final StringBuilder builder = createBuilder(text.length()).append(text);
		builder.setCharAt(index, replacement);
		return builder.toString();
	}

	/**
	 * Returns the {@link String} constructed by replacing the characters at the specified indexes
	 * in the specified {@link String} respectively by the specified {@code char} tokens.
	 * <p>
	 * @param text         a {@link String} (may be {@code null})
	 * @param indexes      the indexes of the characters to replace (may be {@code null})
	 * @param replacements the {@code char} tokens to replace by (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by replacing the characters at the specified indexes
	 *         in the specified {@link String} respectively by the specified {@code char} tokens
	 */
	public static String replace(final String text, final int[] indexes,
			final char[] replacements) {
		// Check the arguments
		if (isNullOrEmpty(text) || Integers.isNullOrEmpty(indexes) ||
				Characters.isNullOrEmpty(replacements)) {
			return text;
		}
		ArrayArguments.requireSameLength(indexes.length, replacements.length);

		// Replace the characters at the indexes in the text by the replacement characters
		final StringBuilder builder = createBuilder(text.length()).append(text);
		for (int i = 0; i < indexes.length; ++i) {
			builder.setCharAt(indexes[i], replacements[i]);
		}
		return builder.toString();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by replacing the substring at the specified index in
	 * the specified {@link String} by the specified {@link String} of the same length.
	 * <p>
	 * @param text        a {@link String} (may be {@code null})
	 * @param fromIndex   the index to start replacing from (inclusive)
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by replacing the substring at the specified index in
	 *         the specified {@link String} by the specified {@link String} of the same length
	 */
	public static String replace(final String text, final int fromIndex, final String replacement) {
		// Check the arguments
		if (isNullOrEmpty(text) || isNullOrEmpty(replacement)) {
			return text;
		}

		// Replace the substring at the index in the text by the replacement string
		return replace(text, fromIndex, fromIndex + replacement.length(), replacement);
	}

	/**
	 * Returns the {@link String} constructed by replacing the substring between the specified
	 * indexes in the specified {@link String} by the specified {@link String}.
	 * <p>
	 * @param text        a {@link String} (may be {@code null})
	 * @param fromIndex   the index to start replacing from (inclusive)
	 * @param toIndex     the index to finish replacing at (exclusive)
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by replacing the substring between the specified
	 *         indexes in the specified {@link String} by the specified {@link String}
	 */
	public static String replace(final String text, final int fromIndex, final int toIndex,
			final String replacement) {
		// Check the arguments
		if (text == null || replacement == null) {
			return text;
		}
		ArrayArguments.requireIndex(fromIndex, text.length() + 1); // handle the empty string
		ArrayArguments.requireIndex(toIndex, text.length() + 1, true); // handle the empty string

		// Replace the substring between the indexes in the text by the replacement string
		final String string = text.substring(0, fromIndex).concat(replacement);
		if (toIndex < text.length()) {
			return string.concat(text.substring(toIndex));
		}
		return string;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by replacing all the characters matching the specified
	 * {@code char} tokens in the specified {@link String} by the specified {@link String}.
	 * <p>
	 * @param text        a {@link String} (may be {@code null})
	 * @param tokens      the {@code char} tokens to replace (may be {@code null})
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by replacing all the characters matching the specified
	 *         {@code char} tokens in the specified {@link String} by the specified {@link String}
	 */
	public static String replaceAll(final String text, final char[] tokens,
			final String replacement) {
		// Check the arguments
		if (text == null || tokens == null) {
			return text;
		}

		// Replace all the characters matching the tokens in the text by the replacement string
		final StringBuilder builder = createBuilder(text.length());
		for (int i = 0; i < text.length(); ++i) {
			final char character = text.charAt(i);
			if (Characters.contains(tokens, character)) {
				builder.append(replacement);
			} else {
				builder.append(character);
			}
		}
		return builder.toString();
	}

	/**
	 * Returns the {@link String} constructed by replacing all the characters matching the specified
	 * {@code char} tokens inside the specified {@code char} delimiters in the specified
	 * {@link String} by the specified {@link String}.
	 * <p>
	 * @param text        a {@link String} (may be {@code null})
	 * @param delimiters  the {@code char} delimiters (may be {@code null})
	 * @param tokens      the {@code char} tokens to replace (may be {@code null})
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by replacing all the characters matching the specified
	 *         {@code char} tokens inside the specified {@code char} delimiters in the specified
	 *         {@link String} by the specified {@link String}
	 */
	public static String replaceInside(final String text, final char[] delimiters,
			final char[] tokens, final String replacement) {
		// Check the arguments
		if (text == null || delimiters == null || tokens == null) {
			return text;
		}

		// Replace all the characters matching the tokens inside the delimiters in the text by the
		// replacement character
		final StringBuilder builder = createBuilder(text.length());
		boolean isInside = false;
		for (int i = 0; i < text.length(); ++i) {
			final char character = text.charAt(i);
			if (Characters.contains(delimiters, character)) {
				isInside = !isInside;
			} else if (isInside && Characters.contains(tokens, character)) {
				builder.append(replacement);
			} else {
				builder.append(character);
			}
		}
		return builder.toString();
	}

	/**
	 * Returns the {@link String} constructed by replacing all the characters matching the specified
	 * {@code char} tokens outside the specified {@code char} delimiters in the specified
	 * {@link String} by the specified {@link String}.
	 * <p>
	 * @param text        a {@link String} (may be {@code null})
	 * @param delimiters  the {@code char} delimiters (may be {@code null})
	 * @param tokens      the {@code char} tokens to replace (may be {@code null})
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by replacing all the characters matching the specified
	 *         {@code char} tokens outside the specified {@code char} delimiters in the specified
	 *         {@link String} by the specified {@link String}
	 */
	public static String replaceOutside(final String text, final char[] delimiters,
			final char[] tokens, final String replacement) {
		// Check the arguments
		if (text == null || delimiters == null || tokens == null) {
			return text;
		}

		// Replace all the characters matching the tokens outside the delimiters in the text by the
		// replacement character
		final StringBuilder builder = createBuilder(text.length());
		boolean isOutside = false;
		for (int i = 0; i < text.length(); ++i) {
			final char character = text.charAt(i);
			if (Characters.contains(delimiters, character)) {
				isOutside = !isOutside;
			} else if (isOutside && Characters.contains(tokens, character)) {
				builder.append(replacement);
			} else {
				builder.append(character);
			}
		}
		return builder.toString();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by replacing the first substring matching the
	 * specified regular expression {@link String} by the specified {@link String}.
	 * <p>
	 * @param text        a {@link String} (may be {@code null})
	 * @param regex       the regular expression {@link String} to identify and replace (may be
	 *                    {@code null})
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by replacing the first substring matching the
	 *         specified regular expression {@link String} by the specified {@link String}
	 */
	public static String replaceFirst(final String text, final String regex,
			final String replacement) {
		// Check the arguments
		if (text == null || regex == null || replacement == null) {
			return text;
		}

		// Replace the first substring matching the regex in the text by the replacement string
		return text.replaceFirst(regex, replacement);
	}

	/**
	 * Returns the {@link String} constructed by replacing the last substring matching the specified
	 * regular expression {@link String} by the specified {@link String}.
	 * <p>
	 * @param text        a {@link String} (may be {@code null})
	 * @param regex       the regular expression {@link String} to identify and replace (may be
	 *                    {@code null})
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by replacing the last substring matching the specified
	 *         regular expression {@link String} by the specified {@link String}
	 */
	public static String replaceLast(final String text, final String regex,
			final String replacement) {
		// Check the arguments
		if (text == null || regex == null || replacement == null) {
			return text;
		}

		// Initialize
		final Matcher matcher = Pattern.compile(regex).matcher(text);

		// Replace the last substring matching the regex in the text by the replacement string
		int fromIndex = -1, toIndex = -1;
		while (matcher.find()) {
			fromIndex = matcher.start();
			toIndex = matcher.end();
		}
		if (fromIndex >= 0) {
			return replace(text, fromIndex, toIndex, replacement);
		}
		return text;
	}

	/**
	 * Returns the {@link String} constructed by replacing all the substrings matching the specified
	 * regular expression {@link String} by the specified {@link String}.
	 * <p>
	 * @param text        a {@link String} (may be {@code null})
	 * @param regex       the regular expression {@link String} to identify and replace (may be
	 *                    {@code null})
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by replacing all the substrings matching the specified
	 *         regular expression {@link String} by the specified {@link String}
	 */
	public static String replaceAll(final String text, final String regex,
			final String replacement) {
		// Check the arguments
		if (text == null || regex == null || replacement == null) {
			return text;
		}

		// Replace all the substrings matching the regex in the text by the replacement string
		return text.replaceAll(regex, replacement);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by reversing the specified {@link String}.
	 * <p>
	 * @param text a {@link String} (may be {@code null})
	 * <p>
	 * @return the {@link String} constructed by reversing the specified {@link String}
	 */
	public static String reverse(final String text) {
		// Check the arguments
		if (isNullOrEmpty(text)) {
			return text;
		}

		// Reverse the text
		return createBuilder(text.length()).append(text).reverse().toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sorts the specified {@link List} of {@link Index}.
	 * <p>
	 * @param indexes a {@link List} of {@link Index}
	 */
	public static void sortStringIndexes(final List<Index<String>> indexes) {
		Lists.sort(indexes, Index.COMPARATOR);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by truncating the specified {@link String} to the
	 * specified length.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param length the length to truncate to
	 * <p>
	 * @return the {@link String} constructed by truncating the specified {@link String} to the
	 *         specified length
	 */
	public static String truncate(final String text, final int length) {
		// Check the arguments
		if (isNullOrEmpty(text) || length >= text.length()) {
			return text;
		}
		IntegerArguments.requireNonNegative(length);

		// Truncate the text to the length
		return text.substring(0, length);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified content wrapped with
	 * {@code wrapper}.
	 * <p>
	 * @param content the content {@link Object} (may be {@code null})
	 * @param wrapper the {@link String} to wrap with (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified content wrapped with {@code wrapper}
	 */
	public static String wrap(final Object content, final String wrapper) {
		// Check the arguments
		if (content == null) {
			return null;
		}

		// Wrap the content
		final String string = toString(content);
		if (isNullOrEmpty(wrapper)) {
			return string;
		}
		return wrapper.concat(string).concat(wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified content wrapped with {@code left}
	 * and {@code right}.
	 * <p>
	 * @param content the content {@link Object} (may be {@code null})
	 * @param left    the {@link String} to wrap with on the left
	 * @param right   right {@link String} to wrap with on the right
	 * <p>
	 * @return a representative {@link String} of the specified content wrapped with {@code left}
	 *         and {@code right}
	 */
	public static String wrap(final Object content, final String left, final String right) {
		// Check the arguments
		if (content == null) {
			return null;
		}

		// Wrap the content
		String string = toString(content);
		if (isNonEmpty(left)) {
			string = left.concat(string);
		}
		if (isNonEmpty(right)) {
			string = string.concat(right);
		}
		return string;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the quoted representative {@link String} of the specified content.
	 * <p>
	 * @param content the content {@link Object}
	 * <p>
	 * @return the quoted representative {@link String} of the specified content
	 */
	public static String quote(final Object content) {
		return QUOTER.call(content);
	}

	/**
	 * Returns the unquoted representative {@link String} of the specified content.
	 * <p>
	 * @param content the content {@link Object}
	 * <p>
	 * @return the unquoted representative {@link String} of the specified content
	 */
	public static String unquote(final Object content) {
		return UNQUOTER.call(content);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the single-quoted representative {@link String} of the specified content.
	 * <p>
	 * @param content the content {@link Object}
	 * <p>
	 * @return the single-quoted representative {@link String} of the specified content
	 */
	public static String singleQuote(final Object content) {
		return SINGLE_QUOTER.call(content);
	}

	/**
	 * Returns the double-quoted representative {@link String} of the specified content.
	 * <p>
	 * @param content the content {@link Object}
	 * <p>
	 * @return the double-quoted representative {@link String} of the specified content
	 */
	public static String doubleQuote(final Object content) {
		return DOUBLE_QUOTER.call(content);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the parenthesized representative {@link String} of the specified content.
	 * <p>
	 * @param content the content {@link Object}
	 * <p>
	 * @return the parenthesized representative {@link String} of the specified content
	 */
	public static String parenthesize(final Object content) {
		return PARENTHESER.call(content);
	}

	/**
	 * Returns the bracketized representative {@link String} of the specified content.
	 * <p>
	 * @param content the content {@link Object}
	 * <p>
	 * @return the bracketized representative {@link String} of the specified content
	 */
	public static String bracketize(final Object content) {
		return BRACKETER.call(content);
	}

	/**
	 * Returns the braced representative {@link String} of the specified content.
	 * <p>
	 * @param content the content {@link Object}
	 * <p>
	 * @return the braced representative {@link String} of the specified content
	 */
	public static String brace(final Object content) {
		return BRACER.call(content);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the first occurrence of any of the specified {@code char} tokens in the
	 * specified {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@code char} tokens to find (may be {@code null})
	 * <p>
	 * @return the index of the first occurrence of any of the specified {@code char} tokens in the
	 *         specified {@link String}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirst(final String text, final char... tokens) {
		return findFirst(text, tokens, 0);
	}

	/**
	 * Returns the index of the first occurrence of any of the specified {@code char} tokens in the
	 * specified {@link String}, seeking forward from the specified index, or {@code -1} if there is
	 * no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@code char} tokens to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first occurrence of any of the specified {@code char} tokens in the
	 *         specified {@link String}, seeking forward from the specified index, or {@code -1} if
	 *         there is no such occurrence
	 */
	public static int findFirst(final String text, final char[] tokens, final int fromIndex) {
		if (isNonEmpty(text) && Characters.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index < text.length(); ++index) {
				if (Characters.contains(tokens, text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the first occurrence of any of the specified token {@link Character} in
	 * the specified {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link Collection} of token {@link Character} to find (may be {@code null})
	 * <p>
	 * @return the index of the first occurrence of any of the specified token {@link Character} in
	 *         the specified {@link String}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirst(final String text, final Collection<Character> tokens) {
		return findFirst(text, tokens, 0);
	}

	/**
	 * Returns the index of the first occurrence of any of the specified token {@link Character} in
	 * the specified {@link String}, seeking forward from the specified index, or {@code -1} if
	 * there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@link Collection} of token {@link Character} to find (may be
	 *                  {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first occurrence of any of the specified token {@link Character} in
	 *         the specified {@link String}, seeking forward from the specified index, or {@code -1}
	 *         if there is no such occurrence
	 */
	public static int findFirst(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		if (isNonEmpty(text) && Collections.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index < text.length(); ++index) {
				if (tokens.contains(text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of the first occurrence of any of the specified array of token
	 * {@link String}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the array of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the {@link Index} of the first occurrence of any of the specified array of token
	 *         {@link String}, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findFirstString(final String text, final String... tokens) {
		return findFirstString(text, tokens, 0);
	}

	/**
	 * Returns the {@link Index} of the first occurrence of any of the specified array of token
	 * {@link String}, seeking forward from the specified index, or {@code null} if there is no such
	 * occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the array of token {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the first occurrence of any of the specified array of token
	 *         {@link String}, seeking forward from the specified index, or {@code null} if there is
	 *         no such occurrence
	 */
	public static Index<String> findFirstString(final String text, final String[] tokens,
			final int fromIndex) {
		Index<String> indexAndToken = null;
		if (isNonEmpty(text) && Arrays.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (final String token : tokens) {
				final int index = text.indexOf(token, fromIndex);
				if (index >= 0 && (indexAndToken == null || index < indexAndToken.getIndex())) {
					indexAndToken = new Index<String>(index, token);
				}
			}
		}
		return indexAndToken;
	}

	/**
	 * Returns the {@link Index} of the first occurrence of any of the specified token
	 * {@link String}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link Collection} of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the {@link Index} of the first occurrence of any of the specified token
	 *         {@link String}, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findFirstString(final String text,
			final Collection<String> tokens) {
		return findFirstString(text, tokens, 0);
	}

	/**
	 * Returns the {@link Index} of the first occurrence of any of the specified token
	 * {@link String}, seeking forward from the specified index, or {@code null} if there is no such
	 * occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@link Collection} of token {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the first occurrence of any of the specified token
	 *         {@link String}, seeking forward from the specified index, or {@code null} if there is
	 *         no such occurrence
	 */
	public static Index<String> findFirstString(final String text, final Collection<String> tokens,
			final int fromIndex) {
		Index<String> indexAndToken = null;
		if (isNonEmpty(text) && Collections.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (final String token : tokens) {
				final int index = text.indexOf(token, fromIndex);
				if (index >= 0 && (indexAndToken == null || index < indexAndToken.getIndex())) {
					indexAndToken = new Index<String>(index, token);
				}
			}
		}
		return indexAndToken;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the last occurrence of any of the specified {@code char} tokens in the
	 * specified {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@code char} tokens to find (may be {@code null})
	 * <p>
	 * @return the index of the last occurrence of any of the specified {@code char} tokens in the
	 *         specified {@link String}, or {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String text, final char... tokens) {
		if (isNonEmpty(text)) {
			return findLast(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of any of the specified {@code char} tokens in the
	 * specified {@link String}, seeking backward from the specified index, or {@code -1} if there
	 * is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@code char} tokens to find (may be {@code null})
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last occurrence of any of the specified {@code char} tokens in the
	 *         specified {@link String}, seeking backward from the specified index, or {@code -1} if
	 *         there is no such occurrence
	 */
	public static int findLast(final String text, final char[] tokens, final int fromIndex) {
		if (isNonEmpty(text) && Characters.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index >= 0; --index) {
				if (Characters.contains(tokens, text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of any of the specified token {@link Character} in
	 * the specified {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link Collection} of token {@link Character} to find (may be {@code null})
	 * <p>
	 * @return the index of the last occurrence of any of the specified token {@link Character} in
	 *         the specified {@link String}, or {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String text, final Collection<Character> tokens) {
		if (isNonEmpty(text)) {
			return findLast(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of any of the specified token {@link Character} in
	 * the specified {@link String}, seeking backward from the specified index, or {@code -1} if
	 * there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@link Collection} of token {@link Character} to find (may be
	 *                  {@code null})
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last occurrence of any of the specified token {@link Character} in
	 *         the specified {@link String}, seeking backward from the specified index, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		if (isNonEmpty(text) && Collections.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index >= 0; --index) {
				if (tokens.contains(text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of the last occurrence of any of the specified array of token
	 * {@link String} in the specified {@link String}, or {@code null} if there is no such
	 * occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the array of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the {@link Index} of the last occurrence of any of the specified array of token
	 *         {@link String} in the specified {@link String}, or {@code null} if there is no such
	 *         occurrence
	 */
	public static Index<String> findLastString(final String text, final String... tokens) {
		if (isNonEmpty(text)) {
			return findLastString(text, tokens, text.length() - 1);
		}
		return null;
	}

	/**
	 * Returns the {@link Index} of the last occurrence of any of the specified array of token
	 * {@link String} in the specified {@link String}, seeking backward from the specified index, or
	 * {@code null} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the array of token {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the last occurrence of any of the specified array of token
	 *         {@link String} in the specified {@link String}, seeking backward from the specified
	 *         index, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final String[] tokens,
			final int fromIndex) {
		Index<String> indexAndToken = null;
		if (isNonEmpty(text) && Arrays.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (final String token : tokens) {
				final int index = text.lastIndexOf(token, fromIndex);
				if (index >= 0 && (indexAndToken == null || index > indexAndToken.getIndex())) {
					indexAndToken = new Index<String>(index, token);
				}
			}
		}
		return indexAndToken;
	}

	/**
	 * Returns the {@link Index} of the last occurrence of any of the specified token {@link String}
	 * in the specified {@link String}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link Collection} of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the {@link Index} of the last occurrence of any of the specified token {@link String}
	 *         in the specified {@link String}, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final Collection<String> tokens) {
		if (isNonEmpty(text)) {
			return findLastString(text, tokens, text.length() - 1);
		}
		return null;
	}

	/**
	 * Returns the {@link Index} of the last occurrence of any of the specified token {@link String}
	 * in the specified {@link String}, seeking backward from the specified index, or {@code null}
	 * if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@link Collection} of token {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the last occurrence of any of the specified token {@link String}
	 *         in the specified {@link String}, seeking backward from the specified index, or
	 *         {@code null} if there is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final Collection<String> tokens,
			final int fromIndex) {
		Index<String> indexAndToken = null;
		if (isNonEmpty(text) && Collections.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (final String token : tokens) {
				final int index = text.lastIndexOf(token, fromIndex);
				if (index >= 0 && (indexAndToken == null || index > indexAndToken.getIndex())) {
					indexAndToken = new Index<String>(index, token);
				}
			}
		}
		return indexAndToken;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the first character in the specified {@link String} that is not equal to
	 * the specified {@code char} token, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String} (may be {@code null})
	 * @param token the {@code char} token to find
	 * <p>
	 * @return the index of the first character in the specified {@link String} that is not equal to
	 *         the specified {@code char} token, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotEqualTo(final String text, final char token) {
		return findFirstNotEqualTo(text, token, 0);
	}

	/**
	 * Returns the index of the first character in the specified {@link String} that is not equal to
	 * the specified {@code char} token, seeking forward from the specified index, or {@code -1} if
	 * there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param token     the {@code char} token to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first character in the specified {@link String} that is not equal to
	 *         the specified {@code char} token, seeking forward from the specified index, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotEqualTo(final String text, final char token,
			final int fromIndex) {
		if (isNonEmpty(text) && Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index < text.length(); ++index) {
				if (text.charAt(index) != token) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the first substring in the specified {@link String} that is not equal to
	 * the specified token {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String} (may be {@code null})
	 * @param token the {@link String} to find (may be {@code null})
	 * <p>
	 * @return the index of the first substring in the specified {@link String} that is not equal to
	 *         the specified token {@link String}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotEqualTo(final String text, final String token) {
		return findFirstStringNotEqualTo(text, token, 0);
	}

	/**
	 * Returns the index of the first substring in the specified {@link String} that is not equal to
	 * the specified token {@link String}, seeking forward from the specified index, or {@code -1}
	 * if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param token     the {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first substring in the specified {@link String} that is not equal to
	 *         the specified token {@link String}, seeking forward from the specified index, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotEqualTo(final String text, final String token,
			final int fromIndex) {
		if (isNonEmpty(text) && isNonEmpty(token) && Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index < text.length(); index += token.length()) {
				if (!isToken(text, index, token)) {
					return index;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the last character in the specified {@link String} that is not equal to
	 * the specified {@code char} token, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String} (may be {@code null})
	 * @param token the {@code char} token to find
	 * <p>
	 * @return the index of the last character in the specified {@link String} that is not equal to
	 *         the specified {@code char} token, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotEqualTo(final String text, final char token) {
		if (isNonEmpty(text)) {
			return findLastNotEqualTo(text, token, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last character in the specified {@link String} that is not equal to
	 * the specified {@code char} token, seeking backward from the specified index, or {@code -1} if
	 * there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param token     the {@code char} token to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last character in the specified {@link String} that is not equal to
	 *         the specified {@code char} token, seeking backward from the specified index, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastNotEqualTo(final String text, final char token, final int fromIndex) {
		if (isNonEmpty(text) && Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index >= 0; --index) {
				if (text.charAt(index) != token) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the last substring in the specified {@link String} that is not equal to
	 * the specified token {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String} (may be {@code null})
	 * @param token the {@link String} to find (may be {@code null})
	 * <p>
	 * @return the index of the last substring in the specified {@link String} that is not equal to
	 *         the specified token {@link String}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotEqualTo(final String text, final String token) {
		if (isNonEmpty(text)) {
			return findLastStringNotEqualTo(text, token, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last substring in the specified {@link String} that is not equal to
	 * the specified token {@link String}, seeking backward from the specified index, or {@code -1}
	 * if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param token     the {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last substring in the specified {@link String} that is not equal to
	 *         the specified token {@link String}, seeking backward from the specified index, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotEqualTo(final String text, final String token,
			final int fromIndex) {
		if (isNonEmpty(text) && isNonEmpty(token) && Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index >= 0; index -= token.length()) {
				if (!isTokenTo(text, index, token)) {
					return index;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the first character in the specified {@link String} that is not in the
	 * {@code char} tokens, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@code char} tokens to find (may be {@code null})
	 * <p>
	 * @return the index of the first character in the specified {@link String} that is not in the
	 *         {@code char} tokens, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final char... tokens) {
		return findFirstNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first character in the specified {@link String} that is not in the
	 * {@code char} tokens, seeking forward from the specified index, or {@code -1} if there is no
	 * such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@code char} tokens to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first character in the specified {@link String} that is not in the
	 *         {@code char} tokens, seeking forward from the specified index, or {@code -1} if there
	 *         is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final char[] tokens, final int fromIndex) {
		if (isNonEmpty(text) && Characters.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index < text.length(); ++index) {
				if (!Characters.contains(tokens, text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the first character in the specified {@link String} that is not in the
	 * {@link Collection} of token {@link Character}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link Collection} of token {@link Character} to find (may be {@code null})
	 * <p>
	 * @return the index of the first character in the specified {@link String} that is not in the
	 *         {@link Collection} of token {@link Character}, or {@code -1} if there is no such
	 *         occurrence
	 */
	public static int findFirstNotIn(final String text, final Collection<Character> tokens) {
		return findFirstNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first character in the specified {@link String} that is not in the
	 * {@link Collection} of token {@link Character}, seeking forward from the specified index, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@link Collection} of token {@link Character} to find (may be
	 *                  {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first character in the specified {@link String} that is not in the
	 *         {@link Collection} of token {@link Character}, seeking forward from the specified
	 *         index, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		if (isNonEmpty(text) && Collections.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index < text.length(); ++index) {
				if (!tokens.contains(text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the first substring in the specified {@link String} that is not in the
	 * array of token {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the array of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the index of the first substring in the specified {@link String} that is not in the
	 *         array of token {@link String}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final String... tokens) {
		return findFirstStringNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first substring in the specified {@link String} that is not in the
	 * array of token {@link String}, seeking forward from the specified index, or {@code -1} if
	 * there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the array of token {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first substring in the specified {@link String} that is not in the
	 *         array of token {@link String}, seeking forward from the specified index, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final String[] tokens,
			final int fromIndex) {
		if (isNonEmpty(text) && Arrays.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex;
			do {
				final int i = getTokenIndex(text, tokens, index);
				if (i >= 0) {
					index += tokens[i].length();
				} else {
					return index;
				}
			} while (index < text.length());
		}
		return -1;
	}

	/**
	 * Returns the index of the first substring in the specified {@link String} that is not in the
	 * specified {@link List} of token {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link List} of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the index of the first substring in the specified {@link String} that is not in the
	 *         specified {@link List} of token {@link String}, or {@code -1} if there is no such
	 *         occurrence
	 */
	public static int findFirstStringNotIn(final String text, final List<String> tokens) {
		return findFirstStringNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first substring in the specified {@link String} that is not in the
	 * specified {@link List} of token {@link String}, seeking forward from the specified index, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@link List} of token {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first substring in the specified {@link String} that is not in the
	 *         specified {@link List} of token {@link String}, seeking forward from the specified
	 *         index, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final List<String> tokens,
			final int fromIndex) {
		if (isNonEmpty(text) && Lists.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex;
			do {
				final int i = getTokenIndex(text, tokens, index);
				if (i >= 0) {
					index += tokens.get(i).length();
				} else {
					return index;
				}
			} while (index < text.length());
		}
		return -1;
	}

	protected static int getTokenIndex(final String text, final String[] tokens,
			final int fromIndex) {
		if (Arrays.isBetween(fromIndex, text.length())) {
			for (int i = 0; i < tokens.length; ++i) {
				if (isToken(text, fromIndex, tokens[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	protected static int getTokenIndex(final String text, final List<String> tokens,
			final int fromIndex) {
		if (Arrays.isBetween(fromIndex, text.length())) {
			final Iterator<String> tokenIterator = tokens.iterator();
			int index = 0;
			while (tokenIterator.hasNext()) {
				if (isToken(text, fromIndex, tokenIterator.next())) {
					return index;
				}
				++index;
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the last character in the specified {@link String} that is not in the
	 * {@code char} tokens, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@code char} tokens to find (may be {@code null})
	 * <p>
	 * @return the index of the last character in the specified {@link String} that is not in the
	 *         {@code char} tokens, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final char... tokens) {
		if (isNonEmpty(text)) {
			return findLastNotIn(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last character in the specified {@link String} that is not in the
	 * {@code char} tokens, seeking backward from the specified index, or {@code -1} if there is no
	 * such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@code char} tokens to find (may be {@code null})
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last character in the specified {@link String} that is not in the
	 *         {@code char} tokens, seeking backward from the specified index, or {@code -1} if
	 *         there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final char[] tokens, final int fromIndex) {
		if (isNonEmpty(text) && Characters.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index >= 0; --index) {
				if (!Characters.contains(tokens, text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last character in the specified {@link String} that is not in the
	 * {@link Collection} of token {@link Character}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link Collection} of token {@link Character} to find (may be {@code null})
	 * <p>
	 * @return the index of the last character in the specified {@link String} that is not in the
	 *         {@link Collection} of token {@link Character}, or {@code -1} if there is no such
	 *         occurrence
	 */
	public static int findLastNotIn(final String text, final Collection<Character> tokens) {
		if (isNonEmpty(text)) {
			return findLastNotIn(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last character in the specified {@link String} that is not in the
	 * {@link Collection} of token {@link Character}, seeking backward from the specified index, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@link Collection} of token {@link Character} to find (may be
	 *                  {@code null})
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last character in the specified {@link String} that is not in the
	 *         {@link Collection} of token {@link Character}, seeking backward from the specified
	 *         index, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		if (isNonEmpty(text) && Collections.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (int index = fromIndex; index >= 0; --index) {
				if (!tokens.contains(text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the last substring in the specified {@link String} that is not in the
	 * array of token {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the array of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the index of the last substring in the specified {@link String} that is not in the
	 *         array of token {@link String}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final String... tokens) {
		if (isNonEmpty(text)) {
			return findLastStringNotIn(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last substring in the specified {@link String} that is not in the
	 * array of token {@link String}, seeking backward from the specified index, or {@code -1} if
	 * there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the array of token {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last substring in the specified {@link String} that is not in the
	 *         array of token {@link String}, seeking backward from the specified index, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final String[] tokens,
			final int fromIndex) {
		if (isNonEmpty(text) && Arrays.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex;
			do {
				final int i = getLastTokenIndex(text, tokens, index + 1);
				if (i >= 0) {
					index -= tokens[i].length();
				} else {
					return index;
				}
			} while (index >= 0);
		}
		return -1;
	}

	/**
	 * Returns the index of the last substring in the specified {@link String} that is not in the
	 * specified {@link List} of token {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link List} of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the index of the last substring in the specified {@link String} that is not in the
	 *         specified {@link List} of token {@link String}, or {@code -1} if there is no such
	 *         occurrence
	 */
	public static int findLastStringNotIn(final String text, final List<String> tokens) {
		if (isNonEmpty(text)) {
			return findLastStringNotIn(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last substring in the specified {@link String} that is not in the
	 * specified {@link List} of token {@link String}, seeking backward from the specified index, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@link List} of token {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last substring in the specified {@link String} that is not in the
	 *         specified {@link List} of token {@link String}, seeking backward from the specified
	 *         index, or {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final List<String> tokens,
			final int fromIndex) {
		if (isNonEmpty(text) && Lists.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex;
			do {
				final int i = getLastTokenIndex(text, tokens, index + 1);
				if (i >= 0) {
					index -= tokens.get(i).length();
				} else {
					return index;
				}
			} while (index >= 0);
		}
		return -1;
	}

	protected static int getLastTokenIndex(final String text, final String[] tokens,
			final int toIndex) {
		if (Arrays.isBetween(toIndex, text.length(), true)) {
			for (int i = 0; i < tokens.length; ++i) {
				if (isToken(text, toIndex - tokens[i].length(), tokens[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	protected static int getLastTokenIndex(final String text, final List<String> tokens,
			final int toIndex) {
		if (Arrays.isBetween(toIndex, text.length(), true)) {
			final Iterator<String> tokenIterator = tokens.iterator();
			int index = 0;
			while (tokenIterator.hasNext()) {
				final String token = tokenIterator.next();
				if (isToken(text, toIndex - token.length(), token)) {
					return index;
				}
				++index;
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the indexes of the specified {@code char} token in the specified {@link String}.
	 * <p>
	 * @param text  a {@link String} (may be {@code null})
	 * @param token the {@code char} token to find
	 * <p>
	 * @return the indexes of the specified {@code char} token in the specified {@link String}
	 */
	public static ExtendedLinkedList<Integer> getIndexes(final String text, final char token) {
		return getIndexes(text, token, 0);
	}

	/**
	 * Returns the indexes of the specified {@code char} token in the specified {@link String},
	 * seeking forward from the specified index.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param token     the {@code char} token to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of the specified {@code char} token in the specified {@link String},
	 *         seeking forward from the specified index
	 */
	public static ExtendedLinkedList<Integer> getIndexes(final String text, final char token,
			final int fromIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNonEmpty(text) && Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex - 1;
			while ((index = text.indexOf(token, index + 1)) >= 0) {
				indexes.add(index);
			}
		}
		return indexes;
	}

	/**
	 * Returns the indexes of the specified {@code char} token in the specified {@link String},
	 * seeking forward to the specified index.
	 * <p>
	 * @param text    a {@link String} (may be {@code null})
	 * @param token   the {@code char} token to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of the specified {@code char} token in the specified {@link String},
	 *         seeking forward to the specified index
	 */
	public static ExtendedLinkedList<Integer> getIndexesTo(final String text, final char token,
			final int toIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNonEmpty(text) && Arrays.isBetween(toIndex, text.length(), true)) {
			int index = text.indexOf(token);
			while (index >= 0 && index < toIndex) {
				indexes.add(index);
				index = text.indexOf(token, index + 1);
			}
		}
		return indexes;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the indexes of the specified {@code char} tokens in the specified {@link String}.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@code char} tokens to find (may be {@code null})
	 * <p>
	 * @return the indexes of the specified {@code char} tokens in the specified {@link String}
	 */
	public static ExtendedLinkedList<Integer> getIndexes(final String text, final char[] tokens) {
		return getIndexes(text, tokens, 0);
	}

	/**
	 * Returns the indexes of the specified {@code char} tokens in the specified {@link String},
	 * seeking forward from the specified index.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@code char} tokens to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of the specified {@code char} tokens in the specified {@link String},
	 *         seeking forward from the specified index
	 */
	public static ExtendedLinkedList<Integer> getIndexes(final String text, final char[] tokens,
			final int fromIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNonEmpty(text) && Characters.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			final char[] array = text.toCharArray();
			for (int index = fromIndex; index < text.length(); ++index) {
				if (Characters.contains(tokens, array[index])) {
					indexes.add(index);
				}
			}
		}
		return indexes;
	}

	/**
	 * Returns the indexes of the specified {@code char} tokens in the specified {@link String},
	 * seeking forward to the specified index.
	 * <p>
	 * @param text    a {@link String} (may be {@code null})
	 * @param tokens  the {@code char} tokens to find (may be {@code null})
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of the specified {@code char} tokens in the specified {@link String},
	 *         seeking forward to the specified index
	 */
	public static ExtendedLinkedList<Integer> getIndexesTo(final String text, final char[] tokens,
			final int toIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNonEmpty(text) && Characters.isNonEmpty(tokens) &&
				Arrays.isBetween(toIndex, text.length(), true)) {
			final char[] array = text.toCharArray();
			for (int index = 0; index < toIndex; ++index) {
				if (Characters.contains(tokens, array[index])) {
					indexes.add(index);
				}
			}
		}
		return indexes;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the indexes of the specified {@link Collection} of token {@link Character} in the
	 * specified {@link String}.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link Collection} of token {@link Character} to find (may be {@code null})
	 * <p>
	 * @return the indexes of the specified {@link Collection} of token {@link Character} in the
	 *         specified {@link String}
	 */
	public static ExtendedLinkedList<Integer> getIndexes(final String text,
			final Collection<Character> tokens) {
		return getIndexes(text, tokens, 0);
	}

	/**
	 * Returns the indexes of the specified {@link Collection} of token {@link Character} in the
	 * specified {@link String}, seeking forward from the specified index.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@link Collection} of token {@link Character} to find (may be
	 *                  {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of the specified {@link Collection} of token {@link Character} in the
	 *         specified {@link String}, seeking forward from the specified index
	 */
	public static ExtendedLinkedList<Integer> getIndexes(final String text,
			final Collection<Character> tokens, final int fromIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNonEmpty(text) && Collections.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			final char[] array = text.toCharArray();
			for (int index = fromIndex; index < text.length(); ++index) {
				if (tokens.contains(array[index])) {
					indexes.add(index);
				}
			}
		}
		return indexes;
	}

	/**
	 * Returns the indexes of the specified {@link Collection} of token {@link Character} in the
	 * specified {@link String}, seeking forward to the specified index.
	 * <p>
	 * @param text    a {@link String} (may be {@code null})
	 * @param tokens  the {@link Collection} of token {@link Character} to find (may be
	 *                {@code null})
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of the specified {@link Collection} of token {@link Character} in the
	 *         specified {@link String}, seeking forward to the specified index
	 */
	public static ExtendedLinkedList<Integer> getIndexesTo(final String text,
			final Collection<Character> tokens, final int toIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNonEmpty(text) && Collections.isNonEmpty(tokens) &&
				Arrays.isBetween(toIndex, text.length(), true)) {
			final char[] array = text.toCharArray();
			for (int index = 0; index < toIndex; ++index) {
				if (tokens.contains(array[index])) {
					indexes.add(index);
				}
			}
		}
		return indexes;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the indexes of the specified token {@link String} in the specified {@link String}.
	 * <p>
	 * @param text  a {@link String} (may be {@code null})
	 * @param token the {@link String} to find (may be {@code null})
	 * <p>
	 * @return the indexes of the specified token {@link String} in the specified {@link String}
	 */
	public static ExtendedLinkedList<Integer> getStringIndexes(final String text,
			final String token) {
		return getStringIndexes(text, token, 0);
	}

	/**
	 * Returns the indexes of the specified token {@link String} in the specified {@link String},
	 * seeking forward from the specified index.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param token     the {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of the specified token {@link String} in the specified {@link String},
	 *         seeking forward from the specified index
	 */
	public static ExtendedLinkedList<Integer> getStringIndexes(final String text,
			final String token, final int fromIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNonEmpty(text) && isNonEmpty(token) && Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex - 1;
			while ((index = text.indexOf(token, index + 1)) >= 0) {
				indexes.add(index);
			}
		}
		return indexes;
	}

	/**
	 * Returns the indexes of the specified token {@link String} in the specified {@link String},
	 * seeking forward to the specified index.
	 * <p>
	 * @param text    a {@link String} (may be {@code null})
	 * @param token   the {@link String} to find (may be {@code null})
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of the specified token {@link String} in the specified {@link String},
	 *         seeking forward to the specified index
	 */
	public static ExtendedLinkedList<Integer> getStringIndexesTo(final String text,
			final String token, final int toIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNonEmpty(text) && isNonEmpty(token) &&
				Arrays.isBetween(toIndex, text.length(), true)) {
			int index = text.indexOf(token);
			while (index >= 0 && index < toIndex) {
				indexes.add(index);
				index = text.indexOf(token, index + 1);
			}
		}
		return indexes;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link SortedList} of {@link Index} of the specified array of token
	 * {@link String} in the specified {@link String}.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the array of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the {@link SortedList} of {@link Index} of the specified array of token
	 *         {@link String} in the specified {@link String}
	 */
	public static SortedList<Index<String>> getStringIndexes(final String text,
			final String[] tokens) {
		return getStringIndexes(text, tokens, 0);
	}

	/**
	 * Returns the {@link SortedList} of {@link Index} of the specified array of token
	 * {@link String}, seeking forward from the specified index.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the array of token {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the {@link SortedList} of {@link Index} of the specified array of token
	 *         {@link String}, seeking forward from the specified index
	 */
	public static SortedList<Index<String>> getStringIndexes(final String text,
			final String[] tokens, final int fromIndex) {
		// Initialize
		final SortedList<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		if (isNonEmpty(text) && Arrays.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (final String token : tokens) {
				final ExtendedLinkedList<Integer> tokenIndexes = getStringIndexes(text, token,
						fromIndex);
				for (final int tokenIndex : tokenIndexes) {
					indexes.add(new Index<String>(tokenIndex, token));
				}
			}
		}
		return indexes;
	}

	/**
	 * Returns the {@link SortedList} of {@link Index} of the specified array of token
	 * {@link String}, seeking forward to the specified index.
	 * <p>
	 * @param text    a {@link String} (may be {@code null})
	 * @param tokens  the array of token {@link String} to find (may be {@code null})
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link SortedList} of {@link Index} of the specified array of token
	 *         {@link String}, seeking forward to the specified index
	 */
	public static SortedList<Index<String>> getStringIndexesTo(final String text,
			final String[] tokens, final int toIndex) {
		// Initialize
		final SortedList<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		if (isNonEmpty(text) && Arrays.isNonEmpty(tokens) &&
				Arrays.isBetween(toIndex, text.length(), true)) {
			for (final String token : tokens) {
				final ExtendedLinkedList<Integer> tokenIndexes = getStringIndexesTo(text, token,
						toIndex);
				for (final int tokenIndex : tokenIndexes) {
					indexes.add(new Index<String>(tokenIndex, token));
				}
			}
		}
		return indexes;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link SortedList} of {@link Index} of the specified {@link Collection} of token
	 * {@link String} in the specified {@link String}.
	 * <p>
	 * @param text   a {@link String} (may be {@code null})
	 * @param tokens the {@link Collection} of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the {@link SortedList} of {@link Index} of the specified {@link Collection} of token
	 *         {@link String} in the specified {@link String}
	 */
	public static SortedList<Index<String>> getStringIndexes(final String text,
			final Collection<String> tokens) {
		return getStringIndexes(text, tokens, 0);
	}

	/**
	 * Returns the {@link SortedList} of {@link Index} of the specified {@link Collection} of token
	 * {@link String}, seeking forward from the specified index.
	 * <p>
	 * @param text      a {@link String} (may be {@code null})
	 * @param tokens    the {@link Collection} of token {@link String} to find (may be {@code null})
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the {@link SortedList} of {@link Index} of the specified {@link Collection} of token
	 *         {@link String}, seeking forward from the specified index
	 */
	public static SortedList<Index<String>> getStringIndexes(final String text,
			final Collection<String> tokens, final int fromIndex) {
		// Initialize
		final SortedList<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		if (isNonEmpty(text) && Collections.isNonEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			for (final String token : tokens) {
				final ExtendedLinkedList<Integer> tokenIndexes = getStringIndexes(text, token,
						fromIndex);
				for (final int tokenIndex : tokenIndexes) {
					indexes.add(new Index<String>(tokenIndex, token));
				}
			}
		}
		return indexes;
	}

	/**
	 * Returns the {@link SortedList} of {@link Index} of the specified {@link Collection} of token
	 * {@link String}, seeking forward to the specified index.
	 * <p>
	 * @param text    a {@link String} (may be {@code null})
	 * @param tokens  the {@link Collection} of token {@link String} to find (may be {@code null})
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link SortedList} of {@link Index} of the specified {@link Collection} of token
	 *         {@link String}, seeking forward to the specified index
	 */
	public static SortedList<Index<String>> getStringIndexesTo(final String text,
			final Collection<String> tokens, final int toIndex) {
		// Initialize
		final SortedList<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		if (isNonEmpty(text) && Collections.isNonEmpty(tokens) &&
				Arrays.isBetween(toIndex, text.length(), true)) {
			for (final String token : tokens) {
				final ExtendedLinkedList<Integer> tokenIndexes = getStringIndexesTo(text, token,
						toIndex);
				for (final int tokenIndex : tokenIndexes) {
					indexes.add(new Index<String>(tokenIndex, token));
				}
			}
		}
		return indexes;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting indexes.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes an {@code int} array
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting indexes
	 */
	public static ExtendedLinkedList<String> getTokens(final String text,
			final int[] delimiterIndexes) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");

		// Get the tokens computed by splitting the text around the delimiters
		return getTokensTo(text, delimiterIndexes, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting indexes until the specified index.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes an {@code int} array
	 * @param toIndex          the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting indexes until the specified
	 *         index
	 */
	public static ExtendedLinkedList<String> getTokensTo(final String text,
			final int[] delimiterIndexes, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");
		Arguments.requireNonNull(delimiterIndexes, "delimiting indexes");

		// Initialize
		final ExtendedLinkedList<String> tokens = new ExtendedLinkedList<String>();

		// Get the tokens computed by splitting the text around the delimiters
		int index = 0;
		for (final int delimiterIndex : delimiterIndexes) {
			if (index <= delimiterIndex && delimiterIndex < toIndex) {
				tokens.add(text.substring(index, delimiterIndex));
			}
			index = delimiterIndex + 1;
		}
		if (index <= toIndex) {
			tokens.add(text.substring(index, toIndex));
		}
		return tokens;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting indexes.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes a {@link Collection} of {@link Integer}
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting indexes
	 */
	public static ExtendedLinkedList<String> getTokens(final String text,
			final Collection<Integer> delimiterIndexes) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");

		// Get the tokens computed by splitting the text around the delimiters
		return getTokensTo(text, delimiterIndexes, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting indexes until the specified index.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes a {@link Collection} of {@link Integer}
	 * @param toIndex          the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting indexes until the specified
	 *         index
	 */
	public static ExtendedLinkedList<String> getTokensTo(final String text,
			final Collection<Integer> delimiterIndexes, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");
		Arguments.requireNonNull(delimiterIndexes, "delimiting indexes");

		// Initialize
		final ExtendedLinkedList<String> tokens = new ExtendedLinkedList<String>();

		// Get the tokens computed by splitting the text around the delimiters
		int index = 0;
		for (final int delimiterIndex : delimiterIndexes) {
			if (index <= delimiterIndex && delimiterIndex < toIndex) {
				tokens.add(text.substring(index, delimiterIndex));
			}
			index = delimiterIndex + 1;
		}
		if (index <= toIndex) {
			tokens.add(text.substring(index, toIndex));
		}
		return tokens;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SPLITTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around {@link Arrays#DELIMITER}.
	 * <p>
	 * @param text a {@link String}
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around {@link Arrays#DELIMITER}
	 */
	public static ExtendedLinkedList<String> split(final String text) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");

		// Split the text around the delimiter
		return splitTo(text, DELIMITER, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified {@code char} delimiter.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the {@code char} delimiter
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified {@code char} delimiter
	 */
	public static ExtendedLinkedList<String> split(final String text, final char delimiter) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");

		// Split the text around the delimiter
		return splitTo(text, delimiter, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified {@code char} delimiter until the specified
	 * index.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the {@code char} delimiter
	 * @param toIndex   the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified {@code char} delimiter until the
	 *         specified index
	 */
	public static ExtendedLinkedList<String> splitTo(final String text, final char delimiter,
			final int toIndex) {
		return getTokensTo(text, getIndexesTo(text, delimiter, toIndex), toIndex);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting {@link Character}.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@link Character} (may be {@code null})
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting {@link Character}
	 */
	public static ExtendedLinkedList<String> split(final String text, final Character delimiter) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");

		// Split the text around the delimiter
		return splitTo(text, delimiter, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting {@link Character} until the
	 * specified index.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@link Character} (may be {@code null})
	 * @param toIndex   the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting {@link Character} until the
	 *         specified index
	 */
	public static ExtendedLinkedList<String> splitTo(final String text, final Character delimiter,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");
		ArrayArguments.requireIndex(toIndex, text.length(), true);
		if (delimiter == null) {
			return new ExtendedLinkedList<String>(text.substring(0, toIndex));
		}

		// Split the text around the delimiter
		return getTokensTo(text, getIndexesTo(text, delimiter, toIndex), toIndex);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified {@code char} delimiters.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@code char} delimiters (may be {@code null})
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified {@code char} delimiters
	 */
	public static ExtendedLinkedList<String> split(final String text, final char[] delimiters) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");

		// Split the text around the delimiters
		return splitTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified {@code char} delimiters until the specified
	 * index.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@code char} delimiters (may be {@code null})
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified {@code char} delimiters until the
	 *         specified index
	 */
	public static ExtendedLinkedList<String> splitTo(final String text, final char[] delimiters,
			final int toIndex) {
		return getTokensTo(text, getIndexesTo(text, delimiters, toIndex), toIndex);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting {@link Character}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link Collection} of delimiting {@link Character} (may be
	 *                   {@code null})
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting {@link Character}
	 */
	public static ExtendedLinkedList<String> split(final String text,
			final Collection<Character> delimiters) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");

		// Split the text around the delimiters
		return splitTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting {@link Character} until the
	 * specified index.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link Collection} of delimiting {@link Character} (may be
	 *                   {@code null})
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting {@link Character} until the
	 *         specified index
	 */
	public static ExtendedLinkedList<String> splitTo(final String text,
			final Collection<Character> delimiters, final int toIndex) {
		return getTokensTo(text, getIndexesTo(text, delimiters, toIndex), toIndex);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting {@link String}.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@link String} (may be {@code null})
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting {@link String}
	 */
	public static ExtendedLinkedList<String> splitString(final String text,
			final String delimiter) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");

		// Split the text around the delimiter
		return splitStringTo(text, delimiter, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting {@link String} until the specified
	 * index.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@link String} (may be {@code null})
	 * @param toIndex   the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting {@link String} until the
	 *         specified index
	 */
	public static ExtendedLinkedList<String> splitStringTo(final String text,
			final String delimiter, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");
		ArrayArguments.requireIndex(toIndex, text.length(), true);
		if (delimiter == null) {
			return new ExtendedLinkedList<String>(text.substring(0, toIndex));
		}

		// Initialize
		final ExtendedLinkedList<String> tokens = new ExtendedLinkedList<String>();
		final ExtendedLinkedList<Integer> delimiterIndexes = getStringIndexesTo(text, delimiter,
				toIndex);

		// Split the text around the delimiter
		int index = 0;
		for (final int delimiterIndex : delimiterIndexes) {
			if (index <= delimiterIndex && delimiterIndex < toIndex) {
				tokens.add(text.substring(index, delimiterIndex));
			}
			index = delimiterIndex + delimiter.length();
		}
		if (index <= toIndex) {
			tokens.add(text.substring(index, toIndex));
		}
		return tokens;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting {@link String}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the array of delimiting {@link String} (may be {@code null})
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting {@link String}
	 */
	public static ExtendedLinkedList<String> splitString(final String text,
			final String[] delimiters) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");

		// Split the text around the delimiters
		return splitStringTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting {@link String} until the specified
	 * index.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the array of delimiting {@link String} (may be {@code null})
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting {@link String} until the
	 *         specified index
	 */
	public static ExtendedLinkedList<String> splitStringTo(final String text,
			final String[] delimiters, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");
		Arguments.requireNonNull(delimiters, "delimiters");
		ArrayArguments.requireIndex(toIndex, text.length(), true);

		// Initialize
		final ExtendedLinkedList<String> tokens = new ExtendedLinkedList<String>();
		final SortedList<Index<String>> delimiterIndexes = getStringIndexesTo(text, delimiters,
				toIndex);

		// Split the text around the delimiters
		sortStringIndexes(delimiterIndexes);
		int index = 0;
		for (final Index<String> delimiterIndexAndToken : delimiterIndexes) {
			final int delimiterIndex = delimiterIndexAndToken.getIndex();
			if (index <= delimiterIndex && delimiterIndex < toIndex) {
				tokens.add(text.substring(index, delimiterIndex));
			}
			index = delimiterIndex + delimiterIndexAndToken.getToken().length();
		}
		if (index <= toIndex) {
			tokens.add(text.substring(index, toIndex));
		}
		return tokens;
	}

	///////////////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting {@link String}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link List} of delimiting {@link String} (may be {@code null})
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting {@link String}
	 */
	public static ExtendedLinkedList<String> splitString(final String text,
			final List<String> delimiters) {
		// Check the arguments
		Arguments.requireNonNull(text, "text");

		// Split the text around the delimiters
		return splitStringTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 * specified {@link String} around the specified delimiting {@link String} until the specified
	 * index.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link List} of delimiting {@link String} (may be {@code null})
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of token {@link String} computed by splitting the
	 *         specified {@link String} around the specified delimiting {@link String} until the
	 *         specified index
	 */
	public static ExtendedLinkedList<String> splitStringTo(final String text,
			final List<String> delimiters, final int toIndex) {
		// Initialize
		final ExtendedLinkedList<String> tokens = new ExtendedLinkedList<String>();
		final SortedList<Index<String>> delimiterIndexes = getStringIndexesTo(text, delimiters,
				toIndex);

		// Split the text around the delimiters
		sortStringIndexes(delimiterIndexes);
		int index = 0;
		for (final Index<String> delimiterIndexAndToken : delimiterIndexes) {
			final int delimiterIndex = delimiterIndexAndToken.getIndex();
			if (index <= delimiterIndex && delimiterIndex < toIndex) {
				tokens.add(text.substring(index, delimiterIndex));
			}
			index = delimiterIndex + delimiterIndexAndToken.getToken().length();
		}
		if (index <= toIndex) {
			tokens.add(text.substring(index, toIndex));
		}
		return tokens;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link String}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link String},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof String;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link String}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link String},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return String.class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} is {@code null} or empty.
	 * <p>
	 * @param text the {@link String} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link String} is {@code null} or empty, {@code false}
	 *         otherwise
	 */
	public static boolean isNullOrEmpty(final String text) {
		return text == null || text.isEmpty();
	}

	/**
	 * Tests whether the specified {@link String} is non-{@code null} and empty.
	 * <p>
	 * @param text the {@link String} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link String} is non-{@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final String text) {
		return text != null && text.isEmpty();
	}

	/**
	 * Tests whether the specified {@link String} is non-{@code null} and non-empty.
	 * <p>
	 * @param text the {@link String} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link String} is non-{@code null} and non-empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNonEmpty(final String text) {
		return text != null && !text.isEmpty();
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} is numeric.
	 * <p>
	 * @param text the {@link String} to test (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link String} is numeric, {@code false} otherwise
	 */
	public static boolean isNumeric(final String text) {
		// Check the arguments
		if (isNullOrEmpty(text)) {
			return false;
		}

		// Test whether the text is numeric
		final NumberFormat formatter = NumberFormat.getInstance();
		final ParsePosition position = new ParsePosition(0);
		formatter.parse(text, position);
		return text.length() == position.getIndex();
	}

	//////////////////////////////////////////////

	public static boolean isToken(final String text, final int index, final String token) {
		return index >= 0 && text.substring(index, index + token.length()).equals(token);
	}

	public static boolean isTokenTo(final String text, final int toIndex, final String token) {
		return isToken(text, toIndex - token.length(), token);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} is between the specified lower and upper bound
	 * {@link String} (with {@code null} considered as the minimum value).
	 * <p>
	 * @param text the {@link String} to test (may be {@code null})
	 * @param from the lower bound {@link String} to test against (inclusive) (may be {@code null})
	 * @param to   the upper bound {@link String} to test against (exclusive) (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link String} is between the specified lower and upper
	 *         bound {@link String}, {@code false} otherwise
	 */
	public static boolean isBetween(final String text, final String from, final String to) {
		return isBetween(text, from, to, true, false);
	}

	/**
	 * Tests whether the specified {@link String} is between the specified lower and upper bound
	 * {@link String} (with {@code null} considered as the minimum value).
	 * <p>
	 * @param text             the {@link String} to test (may be {@code null})
	 * @param from             the lower bound {@link String} to test against (inclusive) (may be
	 *                         {@code null})
	 * @param to               the upper bound {@link String} to test against (exclusive) (may be
	 *                         {@code null})
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@link String} is between the specified lower and upper
	 *         bound {@link String}, {@code false} otherwise
	 */
	public static boolean isBetween(final String text, final String from, final String to,
			final boolean isUpperInclusive) {
		return isBetween(text, from, to, true, isUpperInclusive);
	}

	/**
	 * Tests whether the specified {@link String} is between the specified lower and upper bound
	 * {@link String} (with {@code null} considered as the minimum value).
	 * <p>
	 * @param text             the {@link String} to test (may be {@code null})
	 * @param from             the lower bound {@link String} to test against (inclusive) (may be
	 *                         {@code null})
	 * @param to               the upper bound {@link String} to test against (exclusive) (may be
	 *                         {@code null})
	 * @param isLowerInclusive the flag specifying whether the lower bound is inclusive
	 * @param isUpperInclusive the flag specifying whether the upper bound is inclusive
	 * <p>
	 * @return {@code true} if the specified {@link String} is between the specified lower and upper
	 *         bound {@link String}, {@code false} otherwise
	 */
	public static boolean isBetween(final String text, final String from, final String to,
			final boolean isLowerInclusive, final boolean isUpperInclusive) {
		return (isLowerInclusive ? compare(text, from) >= 0 : compare(text, from) > 0) &&
				(isUpperInclusive ? compare(text, to) <= 0 : compare(text, to) < 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} contains the specified {@code char} token.
	 * <p>
	 * @param text  the {@link String} to test (may be {@code null})
	 * @param token the {@code char} token to test for presence
	 * <p>
	 * @return {@code true} if the specified {@link String} contains the specified {@code char}
	 *         token, {@code false} otherwise
	 */
	public static boolean contains(final String text, final char token) {
		return text != null && text.indexOf(token) >= 0;
	}

	/**
	 * Tests whether the specified {@link String} contains any of the specified {@code char} tokens.
	 * <p>
	 * @param text   the {@link String} to test (may be {@code null})
	 * @param tokens the {@code char} tokens to test for presence
	 * <p>
	 * @return {@code true} if the specified {@link String} contains any of the specified
	 *         {@code char} tokens, {@code false} otherwise
	 */
	public static boolean containsAny(final String text, final char[] tokens) {
		if (text != null) {
			for (final char token : tokens) {
				if (text.indexOf(token) >= 0) {
					return true;
				}
			}
		}
		return false;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} contains the specified token {@link String}.
	 * <p>
	 * @param text  the {@link String} to test (may be {@code null})
	 * @param token the token {@link String} to test for presence
	 * <p>
	 * @return {@code true} if the specified {@link String} contains the specified token
	 *         {@link String}, {@code false} otherwise
	 */
	public static boolean contains(final String text, final String token) {
		return text != null && text.contains(token);
	}

	/**
	 * Tests whether the specified {@link String} contains any of the specified token
	 * {@link String}.
	 * <p>
	 * @param text   the {@link String} to test (may be {@code null})
	 * @param tokens the array of token {@link String} to test for presence
	 * <p>
	 * @return {@code true} if the specified {@link String} contains any of the specified token
	 *         {@link String}, {@code false} otherwise
	 */
	public static boolean containsAny(final String text, final String[] tokens) {
		if (text != null) {
			for (final String token : tokens) {
				if (text.contains(token)) {
					return true;
				}
			}
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} matches {@code expression}.
	 * <p>
	 * @param text       the {@link String} to test (may be {@code null})
	 * @param expression the expression {@link String} to test for presence
	 * <p>
	 * @return {@code true} if the specified {@link String} matches {@code expression},
	 *         {@code false} otherwise
	 */
	public static boolean matches(final String text, final String expression) {
		return text != null && expression != null && text.matches(expression);
	}

	/**
	 * Tests whether the specified {@link String} matches any {@code expressions}.
	 * <p>
	 * @param text        the {@link String} to test (may be {@code null})
	 * @param expressions the array of expression {@link String} to test for presence
	 * <p>
	 * @return {@code true} if the specified {@link String} matches any {@code expressions},
	 *         {@code false} otherwise
	 */
	public static boolean matches(final String text, final String[] expressions) {
		if (text != null) {
			for (final String expression : expressions) {
				if (expression != null && text.matches(expression)) {
					return true;
				}
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMPARATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares the specified {@link String} for lexicographic order. Returns a negative integer,
	 * {@code 0} or a positive integer as {@code a} is less than, equal to or greater than {@code b}
	 * (with {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@link String} to compare for lexicographic order (may be {@code null})
	 * @param b the other {@link String} to compare against for lexicographic order (may be
	 *          {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compare(final String a, final String b) {
		// Check the arguments
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}

		// Compare the strings for lexicographic order
		return a.compareTo(b);
	}

	/**
	 * Compares the specified {@link String} for lexicographic order, ignoring case differences.
	 * Returns a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 * to or greater than {@code b} (with {@code null} considered as the minimum value).
	 * <p>
	 * @param a the {@link String} to compare for lexicographic order (may be {@code null})
	 * @param b the other {@link String} to compare against for lexicographic order (may be
	 *          {@code null})
	 * <p>
	 * @return a negative integer, {@code 0} or a positive integer as {@code a} is less than, equal
	 *         to or greater than {@code b}
	 */
	public static int compareIgnoreCase(final String a, final String b) {
		// Check the arguments
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}

		// Compare the strings for lexicographic order, ignoring case differences
		return a.compareToIgnoreCase(b);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the representative {@link String} of {@code a} and {@code b} are equal to each
	 * other.
	 * <p>
	 * @param a the {@link Object} to compare for equality (may be {@code null})
	 * @param b the other {@link Object} to compare against for equality (may be {@code null})
	 * <p>
	 * @return {@code true} if the representative {@link String} of {@code a} and {@code b} are
	 *         equal to each other, {@code false} otherwise
	 */
	public static boolean equals(final Object a, final Object b) {
		return toString(a).equals(toString(b));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or {@code "null"} if
	 * it is {@code null}.
	 * <p>
	 * @param object an {@link Object} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or {@code "null"} if
	 *         it is {@code null}
	 */
	public static String toString(final Object object) {
		// Check the arguments
		if (object == null) {
			return NULL;
		}

		// Convert the object to a representative string
		if (Arrays.is(object)) {
			if (Booleans.isPrimitiveArray(object)) {
				return Booleans.toString((boolean[]) object);
			} else if (Characters.isPrimitiveArray(object)) {
				return Characters.toString((char[]) object);
			} else if (Bytes.isPrimitiveArray(object)) {
				return Bytes.toString((byte[]) object);
			} else if (Shorts.isPrimitiveArray(object)) {
				return Shorts.toString((short[]) object);
			} else if (Integers.isPrimitiveArray(object)) {
				return Integers.toString((int[]) object);
			} else if (Longs.isPrimitiveArray(object)) {
				return Longs.toString((long[]) object);
			} else if (Floats.isPrimitiveArray(object)) {
				return Floats.toString((float[]) object);
			} else if (Doubles.isPrimitiveArray(object)) {
				return Doubles.toString((double[]) object);
			}
			return Arrays.toString((Object[]) object);
		} else if (Collections.is(object)) {
			Collections.toString((Collection<?>) object);
		} else if (Dates.is(object)) {
			return Dates.toString((Date) object);
		} else if (Maps.is(object)) {
			Maps.toString((Map<?, ?>) object);
		} else if (Numbers.is(object)) {
			return Numbers.toString((Number) object);
		}
		return String.valueOf(object);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or {@code "null"} if
	 * it is {@code null}, truncated to the specified length.
	 * <p>
	 * @param object an {@link Object} (may be {@code null})
	 * @param length the length of the representative {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or {@code "null"} if
	 *         it is {@code null}, truncated to the specified length
	 */
	public static String toString(final Object object, final int length) {
		return truncate(toString(object), length);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or {@code null} if
	 * it is {@code null} or {@code "null"}.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or {@code null} if
	 *         it is {@code null} or {@code "null"}
	 */
	public static String toStringWithNull(final Object object) {
		final String string = toString(object);
		return !NULL.equals(string) ? string : null;
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or {@code null} if
	 * it is {@code null} or {@code "null"}, truncated to the specified length.
	 * <p>
	 * @param object an {@link Object}
	 * @param length the length of the representative {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or {@code null} if
	 *         it is {@code null} or {@code "null"}, truncated to the specified length
	 */
	public static String toStringWithNull(final Object object, final int length) {
		return truncate(toStringWithNull(object), length);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or
	 * {@code defaultString} if it is {@code null}.
	 * <p>
	 * @param object        the {@link Object} (may be {@code null})
	 * @param defaultString the default {@link String} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or
	 *         {@code defaultString} if it is {@code null}
	 */
	public static String toString(final Object object, final String defaultString) {
		return object != null ? toString(object) : defaultString;
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or
	 * {@code defaultString} if it is {@code null}, truncated to the specified length.
	 * <p>
	 * @param object        an {@link Object} (may be {@code null})
	 * @param defaultString the default {@link String} (may be {@code null})
	 * @param length        the length of the representative {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or
	 *         {@code defaultString} if it is {@code null}, truncated to the specified length
	 */
	public static String toString(final Object object, final String defaultString,
			final int length) {
		return truncate(toString(object, defaultString), length);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or
	 * {@code defaultString} if it is {@code null} or {@code "null"}.
	 * <p>
	 * @param object        the {@link Object}
	 * @param defaultString the default {@link String} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or
	 *         {@code defaultString} if it is {@code null} or {@code "null"}
	 */
	public static String toStringWithNull(final Object object, final String defaultString) {
		final String string = toString(object);
		return !NULL.equals(string) ? string : defaultString;
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Object}, or
	 * {@code defaultString} if it is {@code null} or {@code "null"}, truncated to the specified
	 * length.
	 * <p>
	 * @param object        the {@link Object}
	 * @param defaultString the default {@link String} (may be {@code null})
	 * @param length        the length of the representative {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Object}, or
	 *         {@code defaultString} if it is {@code null} or {@code "null"}, truncated to the
	 *         specified length
	 */
	public static String toStringWithNull(final Object object, final String defaultString,
			final int length) {
		return truncate(toStringWithNull(object, defaultString), length);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Exception}, or {@code "null"}
	 * if it is {@code null}.
	 * <p>
	 * @param exception an {@link Exception} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified {@link Exception}, or {@code "null"}
	 *         if it is {@code null}
	 */
	public static String toString(final Exception exception) {
		return toString(exception, 0);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Exception} with the specified
	 * number of {@link StackTraceElement}, or {@code "null"} if it is {@code null}.
	 * <p>
	 * @param exception              an {@link Exception} (may be {@code null})
	 * @param stackTraceElementCount the number of {@link StackTraceElement} to add
	 * <p>
	 * @return a representative {@link String} of the specified {@link Exception} with the specified
	 *         number of {@link StackTraceElement}, or {@code "null"} if it is {@code null}
	 */
	public static String toString(final Exception exception, final int stackTraceElementCount) {
		if (exception == null) {
			return NULL;
		}
		if (stackTraceElementCount > 0) {
			final StackTraceElement[] stackTraces = Arrays.<StackTraceElement>take(
					exception.getStackTrace(), 0, stackTraceElementCount);
			return join(exception.getLocalizedMessage(), COLON, NEW_LINE,
					joinWith(stackTraces, NEW_LINE));
		}
		return exception.getLocalizedMessage();
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified value {@link Object}, or
	 * {@code "null"} if it is {@code null}.
	 * <p>
	 * @param value a value {@link Object} (may be {@code null})
	 * <p>
	 * @return a representative {@link String} of the specified value {@link Object}, or
	 *         {@code "null"} if it is {@code null}
	 */
	public static String valueToString(final Object value) {
		if (value == null || Booleans.is(value) || Numbers.is(value)) {
			return toString(value);
		}
		return doubleQuote(escape(value));
	}
}
