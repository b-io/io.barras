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
package jupiter.common.util;

import static jupiter.common.util.Characters.DOUBLE_QUOTE;
import static jupiter.common.util.Characters.LEFT_BRACKET;
import static jupiter.common.util.Characters.LEFT_PARENTHESIS;
import static jupiter.common.util.Characters.LEFT_QUOTE;
import static jupiter.common.util.Characters.RIGHT_BRACKET;
import static jupiter.common.util.Characters.RIGHT_PARENTHESIS;
import static jupiter.common.util.Characters.RIGHT_QUOTE;
import static jupiter.common.util.Characters.SINGLE_QUOTE;
import static jupiter.common.util.Formats.DEFAULT_LINE_LENGTH;
import static jupiter.common.util.Formats.DEFAULT_LOCALE;
import static jupiter.common.util.Formats.NEW_LINE;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jupiter.common.exception.IllegalClassException;
import jupiter.common.map.ObjectToStringMapper;
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

public class Strings {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String EMPTY = "";
	public static final String[] EMPTY_ARRAY = new String[] {};

	protected static final StringParser PARSER = new StringParser();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String NULL = "null";

	public static final String SPACE = " ";

	public static final String STAR = "*";

	public static final String UNICODE = "\\u";

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile int DEFAULT_INITIAL_CAPACITY = 15;

	public static volatile char DEFAULT_PROGRESS_CHARACTER = '-';

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final StringWrapper SINGLE_QUOTER = new StringWrapper(SINGLE_QUOTE, SINGLE_QUOTE);
	public static final StringWrapper DOUBLE_QUOTER = new StringWrapper(DOUBLE_QUOTE, DOUBLE_QUOTE);
	public static final StringWrapper QUOTER = new StringWrapper(LEFT_QUOTE, RIGHT_QUOTE);
	public static final StringRemover UNQUOTER = new StringRemover(join(
			SINGLE_QUOTE, DOUBLE_QUOTE, LEFT_QUOTE, RIGHT_QUOTE));

	public static final StringWrapper PARENTHESER = new StringWrapper(LEFT_PARENTHESIS,
			RIGHT_PARENTHESIS);
	public static final StringWrapper BRACKETER = new StringWrapper(LEFT_BRACKET, RIGHT_BRACKET);

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

	public static String toLowerCase(final String text) {
		return text.toLowerCase(DEFAULT_LOCALE);
	}

	public static String toUpperCase(final String text) {
		return text.toUpperCase(DEFAULT_LOCALE);
	}

	//////////////////////////////////////////////

	/**
	 * Returns an Unicode {@link String} converted from the specified {@code String}.
	 * <p>
	 * @param text the {@code String} to convert
	 * <p>
	 * @return an Unicode {@link String} converted from the specified {@code String}
	 */
	public static String toUnicode(final String text) {
		final StringBuilder builder = Strings.createBuilder(6 * text.length());
		for (int i = 0; i < text.length(); ++i) {
			builder.append(Characters.toUnicode(text.charAt(i)));
		}
		return builder.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@link String} from the specified array of {@link Object}.
	 * <p>
	 * @param array an array of {@link Object}
	 * <p>
	 * @return an array of {@link String} from the specified array of {@link Object}
	 */
	public static String[] toArray(final Object[] array) {
		return PARSER.callToArray(array);
	}

	/**
	 * Returns an array of {@link String} from the specified array of {@link Object}.
	 * <p>
	 * @param array an array of {@link Object}
	 * <p>
	 * @return an array of {@link String} from the specified array of {@link Object}
	 */
	public static String[] asArray(final Object... array) {
		return toArray(array);
	}

	/**
	 * Returns a 2D array of {@link String} from the specified 2D array of {@link Object}.
	 * <p>
	 * @param array2D a 2D array of {@link Object}
	 * <p>
	 * @return a 2D array of {@link String} from the specified 2D array of {@link Object}
	 */
	public static String[][] toArray2D(final Object[][] array2D) {
		return PARSER.callToArray2D(array2D);
	}

	/**
	 * Returns a 2D array of {@link String} from the specified 2D array of {@link Object}.
	 * <p>
	 * @param array2D a 2D array of {@link Object}
	 * <p>
	 * @return a 2D array of {@link String} from the specified 2D array of {@link Object}
	 */
	public static String[][] asArray2D(final Object[]... array2D) {
		return toArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@link String} from the specified 3D array of {@link Object}.
	 * <p>
	 * @param array3D a 3D array of {@link Object}
	 * <p>
	 * @return a 3D array of {@link String} from the specified 3D array of {@link Object}
	 */
	public static String[][][] toArray3D(final Object[][][] array3D) {
		return PARSER.callToArray3D(array3D);
	}

	/**
	 * Returns a 3D array of {@link String} from the specified 3D array of {@link Object}.
	 * <p>
	 * @param array3D a 3D array of {@link Object}
	 * <p>
	 * @return a 3D array of {@link String} from the specified 3D array of {@link Object}
	 */
	public static String[][][] asArray3D(final Object[][]... array3D) {
		return toArray3D(array3D);
	}

	/**
	 * Returns an array of {@link String} from the specified {@link Collection}.
	 * <p>
	 * @param collection a {@link Collection}
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
	 * @param array a {@code T} array
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
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link String} from the specified {@code T} array
	 */
	public static <T> ExtendedList<String> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array to convert
	 * @param array a {@code T} array
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
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link String} from the specified {@code T} array
	 */
	public static <T> ExtendedLinkedList<String> asLinkedList(final T... array) {
		return toLinkedList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link String} from the specified {@link Collection} of
	 * element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@link ExtendedList} of {@link String} from the specified {@link Collection} of
	 *         element type {@code E}
	 */
	public static <E> ExtendedList<String> collectionToList(final Collection<E> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedLinkedList} of {@link String} from the specified {@link Collection}
	 * of element type {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return an {@link ExtendedLinkedList} of {@link String} from the specified {@link Collection}
	 *         of element type {@code E}
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
	 * @param array a {@code T} array
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
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link String} from the specified {@code T} array
	 */
	public static <T> Set<String> asSet(final T... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link String} from the specified {@link Collection} of element type
	 * {@code E}.
	 * <p>
	 * @param <E>        the element type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of element type {@code E}
	 * <p>
	 * @return a {@link Set} of {@link String} from the specified {@link Collection} of element type
	 *         {@code E}
	 */
	public static <E> Set<String> collectionToSet(final Collection<E> collection) {
		return PARSER.callCollectionToSet(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
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

	//////////////////////////////////////////////

	public static String repeat(final char character, final int length) {
		return repeat(toString(character), length);
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

	public static StringBuilder createBuilder() {
		return createBuilder(DEFAULT_INITIAL_CAPACITY);
	}

	public static StringBuilder createBuilder(final int capacity) {
		return new StringBuilder(capacity);
	}

	//////////////////////////////////////////////

	/**
	 * Creates a {@link String} bar of the default length with the default progress {@code char}
	 * symbol.
	 * <p>
	 * @return a {@link String} bar of the default length with the default progress {@code char}
	 *         symbol
	 */
	public static String createBar() {
		return createBar(DEFAULT_LINE_LENGTH, DEFAULT_PROGRESS_CHARACTER);
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
		return createBar(length, DEFAULT_PROGRESS_CHARACTER);
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
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified {@code char} token in the specified
	 * {@link String}.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@code char} token to count
	 * <p>
	 * @return the number of occurrences of the specified {@code char} token in the specified
	 *         {@link String}
	 */
	public static int count(final String text, final char token) {
		return Characters.count(text.toCharArray(), token);
	}

	/**
	 * Returns the number of occurrences of the specified {@code char} tokens in the specified
	 * {@link String}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@code char} tokens to count
	 * <p>
	 * @return the number of occurrences of the specified {@code char} tokens in the specified
	 *         {@link String}
	 */
	public static int count(final String text, final char[] tokens) {
		return Characters.count(text.toCharArray(), tokens);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of occurrences of the specified token {@link String} in the specified
	 * {@link String}.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the token {@link String} to count
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
	 * Returns the number of occurrences of the specified token {@link String} in the specified
	 * {@link String}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of token {@link String} to count
	 * <p>
	 * @return the number of occurrences of the specified token {@link String} in the specified
	 *         {@link String}
	 */
	public static int countString(final String text, final String[] tokens) {
		int occurrenceCount = 0;
		for (final String token : tokens) {
			occurrenceCount += countString(text, token);
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
	 * @param text           a {@link String}
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

		// Count
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the escaped representative {@link String} of the specified unescaped content (i.e.
	 * without traces of offending characters that can prevent parsing).
	 * <p>
	 * @param content the content {@link Object}
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
	 * @param content the content {@link Object}
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array of {@link String}.
	 * <p>
	 * @param array an array of {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link String}
	 */
	public static String join(final String... array) {
		// Check the arguments
		if (array == null) {
			return NULL;
		}

		// Initialize
		final StringBuilder builder = createBuilder(array.length * DEFAULT_INITIAL_CAPACITY);

		// Join the array
		for (final String element : array) {
			builder.append(element);
		}
		return builder.toString();
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Object}.
	 * <p>
	 * @param array an array of {@link Object}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Object}
	 */
	public static String join(final Object... array) {
		// Check the arguments
		if (array == null) {
			return NULL;
		}

		// Initialize
		final StringBuilder builder = createBuilder(array.length * DEFAULT_INITIAL_CAPACITY);

		// Join the array
		for (final Object object : array) {
			builder.append(toString(object));
		}
		return builder.toString();
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection}.
	 * <p>
	 * @param collection a {@link Collection}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection}
	 */
	public static String join(final Collection<?> collection) {
		// Check the arguments
		if (collection == null) {
			return NULL;
		}

		// Initialize
		final StringBuilder builder = createBuilder(collection.size() * DEFAULT_INITIAL_CAPACITY);

		// Join the array
		for (final Object object : collection) {
			builder.append(toString(object));
		}
		return builder.toString();
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified array of {@link Object} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Object}
	 * @param delimiter the delimiting {@code char} value
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Object} joined by
	 *         {@code delimiter}
	 */
	public static String joinWith(final Object[] array, final char delimiter) {
		return joinWith(array, toString(delimiter));
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Object} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param array     an array of {@link Object}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Object} joined by
	 *         {@code delimiter}
	 */
	public static String joinWith(final Object[] array, final String delimiter) {
		// Check the arguments
		if (array == null) {
			return NULL;
		}
		Arguments.requireNonNull(delimiter);

		// Initialize
		final StringBuilder builder = createBuilder(
				array.length * (DEFAULT_INITIAL_CAPACITY + delimiter.length()));

		// Join the array
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
	 * Returns a representative {@link String} of the specified array of {@link Object} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Object}
	 * @param delimiter the delimiting {@code char} value
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Object} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String joinWith(final Object[] array, final char delimiter,
			final ObjectToStringMapper wrapper) {
		return joinWith(array, toString(delimiter), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified array of {@link Object} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param array     an array of {@link Object}
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Object} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String joinWith(final Object[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		// Check the arguments
		if (array == null) {
			return NULL;
		}
		Arguments.requireNonNull(delimiter);
		Arguments.requireNonNull(wrapper);

		// Initialize
		final StringBuilder builder = createBuilder(
				array.length * (DEFAULT_INITIAL_CAPACITY + delimiter.length()));

		// Join the array
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
	 * Returns a representative {@link String} of the specified {@link Collection} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param collection a {@link Collection}
	 * @param delimiter  the delimiting {@code char} value
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by
	 *         {@code delimiter}
	 */
	public static String joinWith(final Collection<?> collection, final char delimiter) {
		return joinWith(collection, toString(delimiter));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param collection a {@link Collection}
	 * @param delimiter  the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by
	 *         {@code delimiter}
	 */
	public static String joinWith(final Collection<?> collection, final String delimiter) {
		// Check the arguments
		if (collection == null) {
			return NULL;
		}
		Arguments.requireNonNull(delimiter);

		// Initialize
		final StringBuilder builder = createBuilder(
				collection.size() * (DEFAULT_INITIAL_CAPACITY + delimiter.length()));
		final Iterator<?> iterator = collection.iterator();

		// Join the collection
		if (iterator.hasNext()) {
			builder.append(iterator.next());
			while (iterator.hasNext()) {
				builder.append(delimiter).append(toString(iterator.next()));
			}
		}
		return builder.toString();
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param collection a {@link Collection}
	 * @param delimiter  the delimiting {@code char} value
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String joinWith(final Collection<?> collection, final char delimiter,
			final ObjectToStringMapper wrapper) {
		return joinWith(collection, toString(delimiter), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param collection a {@link Collection}
	 * @param delimiter  the delimiting {@link String}
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String joinWith(final Collection<?> collection, final String delimiter,
			final ObjectToStringMapper wrapper) {
		// Check the arguments
		if (collection == null) {
			return NULL;
		}
		Arguments.requireNonNull(delimiter);
		Arguments.requireNonNull(wrapper);

		// Initialize
		final StringBuilder builder = createBuilder(
				collection.size() * (DEFAULT_INITIAL_CAPACITY + delimiter.length()));
		final Iterator<?> iterator = collection.iterator();

		// Join the collection
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
		return leftPad(text, length, Characters.SPACE);
	}

	/**
	 * Returns the {@link String} constructed by left-padding the specified {@link String} to the
	 * specified length with the specified {@code char} value.
	 * <p>
	 * @param text      a {@link String}
	 * @param length    the length to pad to
	 * @param character the {@code char} value to pad with
	 * <p>
	 * @return the {@link String} constructed by left-padding the specified {@link String} to the
	 *         specified length with the specified {@code char} value
	 */
	public static String leftPad(final String text, final int length, final char character) {
		if (isNullOrEmpty(text) || length <= text.length()) {
			return text;
		}
		return repeat(character, length - text.length()).concat(text);
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
		return rightPad(text, length, Characters.SPACE);
	}

	/**
	 * Returns the {@link String} constructed by right-padding the specified {@link String} to the
	 * specified length with the specified {@code char} value.
	 * <p>
	 * @param text      a {@link String}
	 * @param length    the length to pad to
	 * @param character the {@code char} value to pad with
	 * <p>
	 * @return the {@link String} constructed by right-padding the specified {@link String} to the
	 *         specified length with the specified {@code char} value
	 */
	public static String rightPad(final String text, final int length, final char character) {
		if (isNullOrEmpty(text) || length <= text.length()) {
			return text;
		}
		return text.concat(repeat(character, length - text.length()));
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
		return centerPad(text, length, Characters.SPACE);
	}

	/**
	 * Returns the {@link String} constructed by center-padding the specified {@link String} to the
	 * specified length with the specified {@code char} value.
	 * <p>
	 * @param text      a {@link String}
	 * @param length    the length to pad to
	 * @param character the {@code char} value to pad with
	 * <p>
	 * @return the {@link String} constructed by center-padding the specified {@link String} to the
	 *         specified length with the specified {@code char} value
	 */
	public static String centerPad(final String text, final int length, final char character) {
		if (isNullOrEmpty(text) || length <= text.length()) {
			return text;
		}
		return rightPad(repeat(character, (length - text.length()) / 2).concat(text), length,
				character);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by removing the character at the specified index in
	 * the specified {@link String}.
	 * <p>
	 * @param text  a {@link String}
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

		// Remove the character
		return text.substring(0, index).concat(text.substring(index + 1));
	}

	/**
	 * Returns the {@link String} constructed by removing the characters at the specified distinct
	 * sorted indexes in the specified {@link String}.
	 * <p>
	 * @param text    a {@link String}
	 * @param indexes the distinct sorted indexes of the characters to remove
	 * <p>
	 * @return the {@link String} constructed by removing the characters at the specified distinct
	 *         sorted indexes in the specified {@link String}
	 */
	public static String remove(final String text, final int[] indexes) {
		// Check the arguments
		if (isNullOrEmpty(text) || Integers.isNullOrEmpty(indexes)) {
			return text;
		}

		// Remove the characters
		final StringBuilder builder = createBuilder(text.length() - indexes.length);
		int previousIndex = -1;
		for (int i = 0; i < indexes.length; ++i) {
			builder.append(text.substring(previousIndex + 1, indexes[i]));
			previousIndex = indexes[i];
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
	 * @param text   a {@link String}
	 * @param tokens the {@link String} containing the {@code char} tokens to remove
	 * <p>
	 * @return the {@link String} constructed by removing the first occurrence of any of the
	 *         {@code char} tokens contained in the specified {@link String} from the specified
	 *         {@link String}
	 */
	public static String removeFirst(final String text, final String tokens) {
		if (isNullOrEmpty(text) || isNullOrEmpty(tokens)) {
			return text;
		}
		return replaceFirst(text, bracketize(tokens), EMPTY);
	}

	/**
	 * Returns the {@link String} constructed by removing the last occurrence of any of the
	 * {@code char} tokens contained in the specified {@link String} from the specified
	 * {@link String}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link String} containing the {@code char} tokens to remove
	 * <p>
	 * @return the {@link String} constructed by removing the last occurrence of any of the
	 *         {@code char} tokens contained in the specified {@link String} from the specified
	 *         {@link String}
	 */
	public static String removeLast(final String text, final String tokens) {
		if (isNullOrEmpty(text) || isNullOrEmpty(tokens)) {
			return text;
		}
		return replaceLast(text, bracketize(tokens), EMPTY);
	}

	/**
	 * Returns the {@link String} constructed by removing all the occurrences of the {@code char}
	 * tokens contained in the specified {@link String} from the specified {@link String}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link String} containing the {@code char} tokens to remove
	 * <p>
	 * @return the {@link String} constructed by removing all the occurrences of the {@code char}
	 *         tokens contained in the specified {@link String} from the specified {@link String}
	 */
	public static String removeAll(final String text, final String tokens) {
		if (isNullOrEmpty(text) || isNullOrEmpty(tokens)) {
			return text;
		}
		return replaceAll(text, bracketize(tokens), EMPTY);
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
		Collections.<C, String>removeAll(collection, EMPTY);
		return collection;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by replacing the character at the specified index in
	 * the specified {@link String} by the specified replacement {@code char} value.
	 * <p>
	 * @param text        a {@link String}
	 * @param index       the index of the character to replace
	 * @param replacement the {@code char} value to replace by
	 * <p>
	 * @return the {@link String} constructed by replacing the character at the specified index in
	 *         the specified {@link String} by the specified replacement {@code char} value
	 */
	public static String replace(final String text, final int index, final char replacement) {
		// Check the arguments
		if (isNullOrEmpty(text)) {
			return text;
		}
		ArrayArguments.requireIndex(index, text.length());

		// Replace the character
		final StringBuilder builder = createBuilder(text.length()).append(text);
		builder.setCharAt(index, replacement);
		return builder.toString();
	}

	/**
	 * Returns the {@link String} constructed by replacing the characters at the specified indexes
	 * in the specified {@link String} respectively by the specified replacement {@code char}
	 * values.
	 * <p>
	 * @param text         a {@link String}
	 * @param indexes      the indexes of the characters to replace
	 * @param replacements the {@code char} values to replace by
	 * <p>
	 * @return the {@link String} constructed by replacing the characters at the specified indexes
	 *         in the specified {@link String} respectively by the specified replacement
	 *         {@code char} values
	 */
	public static String replace(final String text, final int[] indexes,
			final char[] replacements) {
		// Check the arguments
		if (isNullOrEmpty(text) || Integers.isNullOrEmpty(indexes)) {
			return text;
		}
		ArrayArguments.requireSameLength(indexes.length, replacements.length);

		// Replace the characters
		final StringBuilder builder = createBuilder(text.length()).append(text);
		for (int i = 0; i < indexes.length; ++i) {
			builder.setCharAt(indexes[i], replacements[i]);
		}
		return builder.toString();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by replacing the substring at the specified index in
	 * the specified {@link String} by the specified replacement {@link String} of the same length.
	 * <p>
	 * @param text        a {@link String}
	 * @param fromIndex   the index to start replacing from (inclusive)
	 * @param replacement the {@link String} to replace by
	 * <p>
	 * @return the {@link String} constructed by replacing the substring at the specified index in
	 *         the specified {@link String} by the specified replacement {@link String} of the same
	 *         length
	 */
	public static String replace(final String text, final int fromIndex, final String replacement) {
		if (isNullOrEmpty(text) || isNullOrEmpty(replacement)) {
			return text;
		}
		return replace(text, fromIndex, fromIndex + replacement.length(), replacement);
	}

	/**
	 * Returns the {@link String} constructed by replacing the substring between the specified
	 * indexes in the specified {@link String} by the specified replacement {@link String}.
	 * <p>
	 * @param text        a {@link String}
	 * @param fromIndex   the index to start replacing from (inclusive)
	 * @param toIndex     the index to finish replacing at (exclusive)
	 * @param replacement the {@link String} to replace by
	 * <p>
	 * @return the {@link String} constructed by replacing the substring between the specified
	 *         indexes in the specified {@link String} by the specified replacement {@link String}
	 */
	public static String replace(final String text, final int fromIndex, final int toIndex,
			final String replacement) {
		// Check the arguments
		if (isNullOrEmpty(text) || replacement == null) {
			return text;
		}
		ArrayArguments.requireIndex(fromIndex, text.length());
		ArrayArguments.requireIndex(toIndex, text.length(), false);

		// Replace the character
		final String string = text.substring(0, fromIndex).concat(replacement);
		if (toIndex < text.length()) {
			return string.concat(text.substring(toIndex));
		}
		return string;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by replacing the first matching subsequence in the
	 * specified regular expression {@link String} by the specified replacement {@link String},
	 * substituting the captured subsequence as needed.
	 * <p>
	 * @param text        a {@link String}
	 * @param regex       the regular expression {@link String} to identify and replace
	 * @param replacement the replacement {@link String}
	 * <p>
	 * @return the {@link String} constructed by replacing the first matching subsequence in the
	 *         specified regular expression {@link String} by the specified replacement
	 *         {@link String}, substituting the captured subsequence as needed
	 */
	public static String replaceFirst(final String text, final String regex,
			final String replacement) {
		if (text == null || regex == null || replacement == null) {
			return text;
		}
		return text.replaceFirst(regex, replacement);
	}

	/**
	 * Returns the {@link String} constructed by replacing the last matching subsequence in the
	 * specified regular expression {@link String} by the specified replacement {@link String},
	 * substituting the captured subsequence as needed.
	 * <p>
	 * @param text        a {@link String}
	 * @param regex       the regular expression {@link String} to identify and replace
	 * @param replacement the replacement {@link String}
	 * <p>
	 * @return the {@link String} constructed by replacing the last matching subsequence in the
	 *         specified regular expression {@link String} by the specified replacement
	 *         {@link String}, substituting the captured subsequence as needed
	 */
	public static String replaceLast(final String text, final String regex,
			final String replacement) {
		if (text == null || regex == null || replacement == null) {
			return text;
		}
		final Matcher matcher = Pattern.compile(regex).matcher(text);
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
	 * Returns the {@link String} constructed by replacing all the matching subsequences in the
	 * specified regular expression {@link String} by the specified replacement {@link String},
	 * substituting the captured subsequences as needed.
	 * <p>
	 * @param text        a {@link String}
	 * @param regex       the regular expression {@link String} to identify and replace
	 * @param replacement the replacement {@link String}
	 * <p>
	 * @return the {@link String} constructed by replacing all the matching subsequences in the
	 *         specified regular expression {@link String} by the specified replacement
	 *         {@link String}, substituting the captured subsequences as needed
	 */
	public static String replaceAll(final String text, final String regex,
			final String replacement) {
		if (text == null || regex == null || replacement == null) {
			return text;
		}
		return text.replaceAll(regex, replacement);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by reversing the specified {@link String}.
	 * <p>
	 * @param text a {@link String}
	 * <p>
	 * @return the {@link String} constructed by reversing the specified {@link String}
	 */
	public static String reverse(final String text) {
		if (isNullOrEmpty(text)) {
			return text;
		}
		return createBuilder(text.length()).append(text).reverse().toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by truncating the specified {@link String} to the
	 * specified length.
	 * <p>
	 * @param text   a {@link String}
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

		return text.substring(0, length);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the representative {@link String} of the specified content wrapped with
	 * {@code wrapper}.
	 * <p>
	 * @param content the content {@link Object}
	 * @param wrapper the {@link String} to wrap with
	 * <p>
	 * @return the representative {@link String} of the specified content wrapped with
	 *         {@code wrapper}
	 */
	public static String wrap(final Object content, final String wrapper) {
		if (content == null) {
			return null;
		}
		final String string = toString(content);
		if (isNullOrEmpty(wrapper)) {
			return string;
		}
		return wrapper.concat(string).concat(wrapper);
	}

	/**
	 * Returns the representative {@link String} of the specified content wrapped with {@code left}
	 * and {@code right}.
	 * <p>
	 * @param content the content {@link Object}
	 * @param left    the {@link String} to wrap with on the left
	 * @param right   right {@link String} to wrap with on the right
	 * <p>
	 * @return the representative {@link String} of the specified content wrapped with {@code left}
	 *         and {@code right}
	 */
	public static String wrap(final Object content, final String left, final String right) {
		if (content == null) {
			return null;
		}
		String string = toString(content);
		if (isNotEmpty(left)) {
			string = left.concat(string);
		}
		if (isNotEmpty(right)) {
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

	//////////////////////////////////////////////

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


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the first occurrence of any of the specified {@code char} tokens in the
	 * specified {@link String}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@code char} tokens to find
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
	 * @param text      a {@link String}
	 * @param tokens    the {@code char} tokens to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first occurrence of any of the specified {@code char} tokens in the
	 *         specified {@link String}, seeking forward from the specified index, or {@code -1} if
	 *         there is no such occurrence
	 */
	public static int findFirst(final String text, final char[] tokens, final int fromIndex) {
		if (isNotEmpty(text) && Characters.isNotEmpty(tokens) &&
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
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of token {@link Character} to find
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
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of token {@link Character} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first occurrence of any of the specified token {@link Character} in
	 *         the specified {@link String}, seeking forward from the specified index, or {@code -1}
	 *         if there is no such occurrence
	 */
	public static int findFirst(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
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
	 * Returns the {@link Index} of the first occurrence of any of the specified token
	 * {@link String} in the specified {@link String}, or {@code null} if there is no such
	 * occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of token {@link String} to find
	 * <p>
	 * @return the {@link Index} of the first occurrence of any of the specified token
	 *         {@link String} in the specified {@link String}, or {@code null} if there is no such
	 *         occurrence
	 */
	public static Index<String> findFirstString(final String text, final String... tokens) {
		return findFirstString(text, tokens, 0);
	}

	/**
	 * Returns the {@link Index} of the first occurrence of any of the specified token
	 * {@link String} in the specified {@link String}, seeking forward from the specified index, or
	 * {@code null} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the array of token {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the first occurrence of any of the specified token
	 *         {@link String} in the specified {@link String}, seeking forward from the specified
	 *         index, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findFirstString(final String text, final String[] tokens,
			final int fromIndex) {
		Index<String> indexAndToken = null;
		if (isNotEmpty(text) && Arrays.isNotEmpty(tokens) &&
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
	 * {@link String} in the specified {@link String}, or {@code null} if there is no such
	 * occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of token {@link String} to find
	 * <p>
	 * @return the {@link Index} of the first occurrence of any of the specified token
	 *         {@link String} in the specified {@link String}, or {@code null} if there is no such
	 *         occurrence
	 */
	public static Index<String> findFirstString(final String text,
			final Collection<String> tokens) {
		return findFirstString(text, tokens, 0);
	}

	/**
	 * Returns the {@link Index} of the first occurrence of any of the specified token
	 * {@link String} in the specified {@link String}, seeking forward from the specified index, or
	 * {@code null} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of token {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the first occurrence of any of the specified token
	 *         {@link String} in the specified {@link String}, seeking forward from the specified
	 *         index, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findFirstString(final String text, final Collection<String> tokens,
			final int fromIndex) {
		Index<String> indexAndToken = null;
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
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
	 * @param text   a {@link String}
	 * @param tokens the {@code char} tokens to find
	 * <p>
	 * @return the index of the last occurrence of any of the specified {@code char} tokens in the
	 *         specified {@link String}, or {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String text, final char... tokens) {
		if (isNotEmpty(text)) {
			return findLast(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of any of the specified {@code char} tokens in the
	 * specified {@link String}, seeking backward from the specified index, or {@code -1} if there
	 * is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@code char} tokens to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last occurrence of any of the specified {@code char} tokens in the
	 *         specified {@link String}, seeking backward from the specified index, or {@code -1} if
	 *         there is no such occurrence
	 */
	public static int findLast(final String text, final char[] tokens, final int fromIndex) {
		if (isNotEmpty(text) && Characters.isNotEmpty(tokens) &&
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
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of token {@link Character} to find
	 * <p>
	 * @return the index of the last occurrence of any of the specified token {@link Character} in
	 *         the specified {@link String}, or {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String text, final Collection<Character> tokens) {
		if (isNotEmpty(text)) {
			return findLast(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of any of the specified token {@link Character} in
	 * the specified {@link String}, seeking backward from the specified index, or {@code -1} if
	 * there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of token {@link Character} to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last occurrence of any of the specified token {@link Character} in
	 *         the specified {@link String}, seeking backward from the specified index, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
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
	 * Returns the {@link Index} of the last occurrence of any of the specified token {@link String}
	 * in the specified {@link String}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of token {@link String} to find
	 * <p>
	 * @return the {@link Index} of the last occurrence of any of the specified token {@link String}
	 *         in the specified {@link String}, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final String... tokens) {
		if (isNotEmpty(text)) {
			return findLastString(text, tokens, text.length() - 1);
		}
		return null;
	}

	/**
	 * Returns the {@link Index} of the last occurrence of any of the specified token {@link String}
	 * in the specified {@link String}, seeking backward from the specified index, or {@code null}
	 * if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the array of token {@link String} to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the last occurrence of any of the specified token {@link String}
	 *         in the specified {@link String}, seeking backward from the specified index, or
	 *         {@code null} if there is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final String[] tokens,
			final int fromIndex) {
		Index<String> indexAndToken = null;
		if (isNotEmpty(text) && Arrays.isNotEmpty(tokens) &&
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
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of token {@link String} to find
	 * <p>
	 * @return the {@link Index} of the last occurrence of any of the specified token {@link String}
	 *         in the specified {@link String}, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final Collection<String> tokens) {
		if (isNotEmpty(text)) {
			return findLastString(text, tokens, text.length() - 1);
		}
		return null;
	}

	/**
	 * Returns the {@link Index} of the last occurrence of any of the specified token {@link String}
	 * in the specified {@link String}, seeking backward from the specified index, or {@code null}
	 * if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of token {@link String} to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the last occurrence of any of the specified token {@link String}
	 *         in the specified {@link String}, seeking backward from the specified index, or
	 *         {@code null} if there is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final Collection<String> tokens,
			final int fromIndex) {
		Index<String> indexAndToken = null;
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
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
	 * Returns the index of the first {@code text} token that is not equal to {@code token}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@code char} token to find
	 * <p>
	 * @return the index of the first {@code text} token that is not equal to {@code token}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotEqualTo(final String text, final char token) {
		return findFirstNotEqualTo(text, token, 0);
	}

	/**
	 * Returns the index of the first {@code text} token that is not equal to {@code token}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@code char} token to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first {@code text} token that is not equal to {@code token}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotEqualTo(final String text, final char token,
			final int fromIndex) {
		if (isNotEmpty(text) && Arrays.isBetween(fromIndex, text.length())) {
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
	 * Returns the index of the first {@code text} token that is not equal to {@code token}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@link String} to find
	 * <p>
	 * @return the index of the first {@code text} token that is not equal to {@code token}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotEqualTo(final String text, final String token) {
		return findFirstStringNotEqualTo(text, token, 0);
	}

	/**
	 * Returns the index of the first {@code text} token that is not equal to {@code token}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first {@code text} token that is not equal to {@code token}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotEqualTo(final String text, final String token,
			final int fromIndex) {
		if (isNotEmpty(text) && isNotEmpty(token) &&
				Arrays.isBetween(fromIndex, text.length())) {
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
	 * Returns the index of the last {@code text} token that is not equal to {@code token}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@code char} token to find
	 * <p>
	 * @return the index of the last {@code text} token that is not equal to {@code token}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastNotEqualTo(final String text, final char token) {
		if (isNotEmpty(text)) {
			return findLastNotEqualTo(text, token, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last {@code text} token that is not equal to {@code token}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@code char} token to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last {@code text} token that is not equal to {@code token}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotEqualTo(final String text, final char token, final int fromIndex) {
		if (isNotEmpty(text) && Arrays.isBetween(fromIndex, text.length())) {
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
	 * Returns the index of the last {@code text} token that is not equal to {@code token}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@link String} to find
	 * <p>
	 * @return the index of the last {@code text} token that is not equal to {@code token}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotEqualTo(final String text, final String token) {
		if (isNotEmpty(text)) {
			return findLastStringNotEqualTo(text, token, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last {@code text} token that is not equal to {@code token}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@link String} to find
	 * @param fromIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the index of the last {@code text} token that is not equal to {@code token}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotEqualTo(final String text, final String token,
			final int fromIndex) {
		if (isNotEmpty(text) && isNotEmpty(token) &&
				Arrays.isBetween(fromIndex, text.length())) {
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
	 * Returns the index of the first {@code text} token that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@code char} tokens to find
	 * <p>
	 * @return the index of the first {@code text} token that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final char... tokens) {
		return findFirstNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first {@code text} token that is not in {@code tokens}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@code char} tokens to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first {@code text} token that is not in {@code tokens}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final char[] tokens, final int fromIndex) {
		if (isNotEmpty(text) && Characters.isNotEmpty(tokens) &&
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
	 * Returns the index of the first {@code text} token that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of token {@link Character} to find
	 * <p>
	 * @return the index of the first {@code text} token that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final Collection<Character> tokens) {
		return findFirstNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first {@code text} token that is not in {@code tokens}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of token {@link Character} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first {@code text} token that is not in {@code tokens}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
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
	 * Returns the index of the first {@code text} token that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of token {@link String} to find
	 * <p>
	 * @return the index of the first {@code text} token that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final String... tokens) {
		return findFirstStringNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first {@code text} token that is not in {@code tokens}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the array of token {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first {@code text} token that is not in {@code tokens}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final String[] tokens,
			final int fromIndex) {
		if (isNotEmpty(text) && Arrays.isNotEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex;
			do {
				final int i = getToken(text, index, tokens);
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
	 * Returns the index of the first {@code text} token that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link List} of {@link String} to find
	 * <p>
	 * @return the index of the first {@code text} token that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final List<String> tokens) {
		return findFirstStringNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first {@code text} token that is not in {@code tokens}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link List} of {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first {@code text} token that is not in {@code tokens}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final List<String> tokens,
			final int fromIndex) {
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex;
			do {
				final int i = getToken(text, index, tokens);
				if (i >= 0) {
					index += tokens.get(i).length();
				} else {
					return index;
				}
			} while (index < text.length());
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the last {@code text} token that is not in {@code tokens}, or {@code -1}
	 * if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@code char} tokens to find
	 * <p>
	 * @return the index of the last {@code text} token that is not in {@code tokens}, or {@code -1}
	 *         if there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final char... tokens) {
		if (isNotEmpty(text)) {
			return findLastNotIn(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last {@code text} token that is not in {@code tokens}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@code char} tokens to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last {@code text} token that is not in {@code tokens}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final char[] tokens, final int fromIndex) {
		if (isNotEmpty(text) && Characters.isNotEmpty(tokens) &&
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
	 * Returns the index of the last {@code text} token that is not in {@code tokens}, or {@code -1}
	 * if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of token {@link Character} to find
	 * <p>
	 * @return the index of the last {@code text} token that is not in {@code tokens}, or {@code -1}
	 *         if there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final Collection<Character> tokens) {
		if (isNotEmpty(text)) {
			return findLastNotIn(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last {@code text} token that is not in {@code tokens}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of token {@link Character} to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last {@code text} token that is not in {@code tokens}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
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
	 * Returns the index of the last {@code text} token that is not in {@code tokens}, or {@code -1}
	 * if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of token {@link String} to find
	 * <p>
	 * @return the index of the last {@code text} token that is not in {@code tokens}, or {@code -1}
	 *         if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final String... tokens) {
		if (isNotEmpty(text)) {
			return findLastStringNotIn(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last {@code text} token that is not in {@code tokens}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the array of token {@link String} to find
	 * @param fromIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the index of the last {@code text} token that is not in {@code tokens}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final String[] tokens,
			final int fromIndex) {
		if (isNotEmpty(text) && Arrays.isNotEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex;
			do {
				final int i = getTokenTo(text, index + 1, tokens);
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
	 * Returns the index of the last {@code text} token that is not in {@code tokens}, or {@code -1}
	 * if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link List} of {@link String} to find
	 * <p>
	 * @return the index of the last {@code text} token that is not in {@code tokens}, or {@code -1}
	 *         if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final List<String> tokens) {
		if (isNotEmpty(text)) {
			return findLastStringNotIn(text, tokens, text.length() - 1);
		}
		return -1;
	}

	/**
	 * Returns the index of the last {@code text} token that is not in {@code tokens}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link List} of {@link String} to find
	 * @param fromIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the index of the last {@code text} token that is not in {@code tokens}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final List<String> tokens,
			final int fromIndex) {
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
				Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex;
			do {
				final int i = getTokenTo(text, index + 1, tokens);
				if (i >= 0) {
					index -= tokens.get(i).length();
				} else {
					return index;
				}
			} while (index >= 0);
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the indexes of {@code token} in {@code text}.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@code char} token to find
	 * <p>
	 * @return the indexes of {@code token} in {@code text}
	 */
	public static ExtendedLinkedList<Integer> getIndexes(final String text, final char token) {
		return getIndexes(text, token, 0);
	}

	/**
	 * Returns the indexes of {@code token} in {@code text}, seeking forward from {@code fromIndex}.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@code char} token to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of {@code token} in {@code text}, seeking forward from {@code fromIndex}
	 */
	public static ExtendedLinkedList<Integer> getIndexes(final String text, final char token,
			final int fromIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNotEmpty(text) && Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex - 1;
			while ((index = text.indexOf(token, index + 1)) >= 0) {
				indexes.add(index);
			}
		}
		return indexes;
	}

	/**
	 * Returns the indexes of {@code token} in {@code text}, seeking forward to {@code toIndex}.
	 * <p>
	 * @param text    a {@link String}
	 * @param token   the {@code char} token to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of {@code token} in {@code text}, seeking forward to {@code toIndex}
	 */
	public static ExtendedLinkedList<Integer> getIndexesTo(final String text, final char token,
			final int toIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNotEmpty(text) && Arrays.isBetween(toIndex, text.length(), false)) {
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
	 * @param text   a {@link String}
	 * @param tokens the {@code char} tokens to find
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
	 * @param text      a {@link String}
	 * @param tokens    the {@code char} tokens to find
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
		if (isNotEmpty(text) && Characters.isNotEmpty(tokens) &&
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
	 * @param text    a {@link String}
	 * @param tokens  the {@code char} tokens to find
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
		if (isNotEmpty(text) && Characters.isNotEmpty(tokens) &&
				Arrays.isBetween(toIndex, text.length(), false)) {
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
	 * Returns the indexes of the specified {@link Collection} of token {@code Character} in the
	 * specified {@link String}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of token {@code Character} to find
	 * <p>
	 * @return the indexes of the specified {@link Collection} of token {@code Character} in the
	 *         specified {@link String}
	 */
	public static ExtendedLinkedList<Integer> getIndexes(final String text,
			final Collection<Character> tokens) {
		return getIndexes(text, tokens, 0);
	}

	/**
	 * Returns the indexes of the specified {@link Collection} of token {@code Character} in the
	 * specified {@link String}, seeking forward from the specified index.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of token {@link Character} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of the specified {@link Collection} of token {@code Character} in the
	 *         specified {@link String}, seeking forward from the specified index
	 */
	public static ExtendedLinkedList<Integer> getIndexes(final String text,
			final Collection<Character> tokens, final int fromIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
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
	 * Returns the indexes of the specified {@link Collection} of token {@code Character} in the
	 * specified {@link String}, seeking forward to the specified index.
	 * <p>
	 * @param text    a {@link String}
	 * @param tokens  the {@link Collection} of token {@link Character} to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of the specified {@link Collection} of token {@code Character} in the
	 *         specified {@link String}, seeking forward to the specified index
	 */
	public static ExtendedLinkedList<Integer> getIndexesTo(final String text,
			final Collection<Character> tokens, final int toIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
				Arrays.isBetween(toIndex, text.length(), false)) {
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
	 * Returns the indexes of {@code token} in {@code text}.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@link String} to find
	 * <p>
	 * @return the indexes of {@code token} in {@code text}
	 */
	public static ExtendedLinkedList<Integer> getStringIndexes(final String text,
			final String token) {
		return getStringIndexes(text, token, 0);
	}

	/**
	 * Returns the indexes of {@code token} in {@code text}, seeking forward from {@code fromIndex}.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of {@code token} in {@code text}, seeking forward from {@code fromIndex}
	 */
	public static ExtendedLinkedList<Integer> getStringIndexes(final String text,
			final String token, final int fromIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNotEmpty(text) && isNotEmpty(token) &&
				Arrays.isBetween(fromIndex, text.length())) {
			int index = fromIndex - 1;
			while ((index = text.indexOf(token, index + 1)) >= 0) {
				indexes.add(index);
			}
		}
		return indexes;
	}

	/**
	 * Returns the indexes of {@code token} in {@code text}, seeking forward to {@code toIndex}.
	 * <p>
	 * @param text    a {@link String}
	 * @param token   the {@link String} to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of {@code token} in {@code text}, seeking forward to {@code toIndex}
	 */
	public static ExtendedLinkedList<Integer> getStringIndexesTo(final String text,
			final String token, final int toIndex) {
		// Initialize
		final ExtendedLinkedList<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (isNotEmpty(text) && isNotEmpty(token) &&
				Arrays.isBetween(toIndex, text.length(), false)) {
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
	 * Returns the indexes and {@code tokens} in {@code text}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of token {@link String} to find
	 * <p>
	 * @return the indexes and {@code tokens} in {@code text}
	 */
	public static SortedList<Index<String>> getStringIndexes(final String text,
			final String[] tokens) {
		return getStringIndexes(text, tokens, 0);
	}

	/**
	 * Returns the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward
	 * from {@code fromIndex}.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the array of token {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward
	 *         from {@code fromIndex}
	 */
	public static SortedList<Index<String>> getStringIndexes(final String text,
			final String[] tokens, final int fromIndex) {
		// Initialize
		final SortedList<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		if (isNotEmpty(text) && Arrays.isNotEmpty(tokens) &&
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
	 * Returns the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward to
	 * {@code toIndex}.
	 * <p>
	 * @param text    a {@link String}
	 * @param tokens  the array of token {@link String} to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward to
	 *         {@code toIndex}
	 */
	public static SortedList<Index<String>> getStringIndexesTo(final String text,
			final String[] tokens, final int toIndex) {
		// Initialize
		final SortedList<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		if (isNotEmpty(text) && Arrays.isNotEmpty(tokens) &&
				Arrays.isBetween(toIndex, text.length(), false)) {
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
	 * Returns the indexes and {@code tokens} in {@code text}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of token {@link String} to find
	 * <p>
	 * @return the indexes and {@code tokens} in {@code text}
	 */
	public static SortedList<Index<String>> getStringIndexes(final String text,
			final Collection<String> tokens) {
		return getStringIndexes(text, tokens, 0);
	}

	/**
	 * Returns the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward
	 * from {@code fromIndex}.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of token {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward
	 *         from {@code fromIndex}
	 */
	public static SortedList<Index<String>> getStringIndexes(final String text,
			final Collection<String> tokens, final int fromIndex) {
		// Initialize
		final SortedList<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
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
	 * Returns the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward to
	 * {@code toIndex}.
	 * <p>
	 * @param text    a {@link String}
	 * @param tokens  the {@link Collection} of token {@link String} to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward to
	 *         {@code toIndex}
	 */
	public static SortedList<Index<String>> getStringIndexesTo(final String text,
			final Collection<String> tokens, final int toIndex) {
		// Initialize
		final SortedList<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		if (isNotEmpty(text) && Collections.isNotEmpty(tokens) &&
				Arrays.isBetween(toIndex, text.length(), false)) {
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
	 * Sorts the specified {@link List} of {@link Index}.
	 * <p>
	 * @param indexes a {@link List} of {@link Index}
	 */
	public static void sortStringIndexes(final List<Index<String>> indexes) {
		Lists.sort(indexes, Index.COMPARATOR);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiterIndexes}.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes an {@code int} array
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiterIndexes}
	 */
	public static ExtendedLinkedList<String> getTokens(final String text,
			final int[] delimiterIndexes) {
		return getTokensTo(text, delimiterIndexes, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiterIndexes}.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes an {@code int} array
	 * @param toIndex          the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiterIndexes}
	 */
	public static ExtendedLinkedList<String> getTokensTo(final String text,
			final int[] delimiterIndexes, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(delimiterIndexes);

		// Initialize
		final ExtendedLinkedList<String> tokens = new ExtendedLinkedList<String>();

		// Get the tokens
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
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiterIndexes}.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes a {@link Collection} of {@link Integer}
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiterIndexes}
	 */
	public static ExtendedLinkedList<String> getTokens(final String text,
			final Collection<Integer> delimiterIndexes) {
		return getTokensTo(text, delimiterIndexes, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiterIndexes}.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes a {@link Collection} of {@link Integer}
	 * @param toIndex          the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiterIndexes}
	 */
	public static ExtendedLinkedList<String> getTokensTo(final String text,
			final Collection<Integer> delimiterIndexes, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(delimiterIndexes);

		// Initialize
		final ExtendedLinkedList<String> tokens = new ExtendedLinkedList<String>();
		int index = 0;

		// Get the tokens
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

	public static int getToken(final String text, final int fromIndex, final String[] tokens) {
		if (Arrays.isBetween(fromIndex, text.length())) {
			for (int i = 0; i < tokens.length; ++i) {
				if (isToken(text, fromIndex, tokens[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int getTokenTo(final String text, final int toIndex, final String[] tokens) {
		for (int i = 0; i < tokens.length; ++i) {
			if (isToken(text, toIndex - tokens[i].length(), tokens[i])) {
				return i;
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	public static int getToken(final String text, final int fromIndex, final List<String> tokens) {
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

	public static int getTokenTo(final String text, final int toIndex, final List<String> tokens) {
		if (Arrays.isBetween(toIndex, text.length(), false)) {
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
	// SPLITTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiter}.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@code char} value
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiter}
	 */
	public static ExtendedLinkedList<String> split(final String text, final char delimiter) {
		return splitTo(text, delimiter, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiter}.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@code char} value
	 * @param toIndex   the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiter}
	 */
	public static ExtendedLinkedList<String> splitTo(final String text, final char delimiter,
			final int toIndex) {
		return getTokensTo(text, getIndexesTo(text, delimiter, toIndex), toIndex);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiters}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the array of delimiting {@code char} values
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiters}
	 */
	public static ExtendedLinkedList<String> split(final String text, final char[] delimiters) {
		return splitTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiters} to {@code toIndex}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the array of delimiting {@code char} values
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiters} to {@code toIndex}
	 */
	public static ExtendedLinkedList<String> splitTo(final String text, final char[] delimiters,
			final int toIndex) {
		return getTokensTo(text, getIndexesTo(text, delimiters, toIndex), toIndex);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiters}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link Collection} of delimiting {@link Character}
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiters}
	 */
	public static ExtendedLinkedList<String> split(final String text,
			final Collection<Character> delimiters) {
		return splitTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiters} to {@code toIndex}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link Collection} of delimiting {@link Character}
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiters} to {@code toIndex}
	 */
	public static ExtendedLinkedList<String> splitTo(final String text,
			final Collection<Character> delimiters, final int toIndex) {
		return getTokensTo(text, getIndexesTo(text, delimiters, toIndex), toIndex);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiter}.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiter}
	 */
	public static ExtendedLinkedList<String> splitString(final String text,
			final String delimiter) {
		return splitStringTo(text, delimiter, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiter}.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@link String}
	 * @param toIndex   the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiter}
	 */
	public static ExtendedLinkedList<String> splitStringTo(final String text,
			final String delimiter, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(delimiter);
		ArrayArguments.requireIndex(toIndex, text.length(), false);

		// Initialize
		final ExtendedLinkedList<String> tokens = new ExtendedLinkedList<String>();
		final ExtendedLinkedList<Integer> delimiterIndexes = getStringIndexesTo(text, delimiter,
				toIndex);
		int index = 0;

		// Get the tokens
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
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiters}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the array of delimiting {@link String}
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiters}
	 */
	public static ExtendedLinkedList<String> splitString(final String text,
			final String[] delimiters) {
		return splitStringTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiters} to {@code toIndex}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the array of delimiting {@link String}
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiters} to {@code toIndex}
	 */
	public static ExtendedLinkedList<String> splitStringTo(final String text,
			final String[] delimiters, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(delimiters);
		ArrayArguments.requireIndex(toIndex, text.length(), false);

		// Initialize
		final ExtendedLinkedList<String> tokens = new ExtendedLinkedList<String>();
		final SortedList<Index<String>> delimiterIndexes = getStringIndexesTo(text, delimiters,
				toIndex);
		int index = 0;

		// Get the tokens
		sortStringIndexes(delimiterIndexes);
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
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiters}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link List} of delimiting {@link String}
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiters}
	 */
	public static ExtendedLinkedList<String> splitString(final String text,
			final List<String> delimiters) {
		return splitStringTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 * around {@code delimiters} to {@code toIndex}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link List} of delimiting {@link String}
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link String} computed by splitting {@code text}
	 *         around {@code delimiters} to {@code toIndex}
	 */
	public static ExtendedLinkedList<String> splitStringTo(final String text,
			final List<String> delimiters, final int toIndex) {
		// Initialize
		final ExtendedLinkedList<String> tokens = new ExtendedLinkedList<String>();
		final SortedList<Index<String>> delimiterIndexes = getStringIndexesTo(text, delimiters,
				toIndex);
		int index = 0;

		// Get the tokens
		sortStringIndexes(delimiterIndexes);
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
	 * Tests whether the specified {@link Class} is assignable to a {@link String}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link String},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
		return String.class.isAssignableFrom(c);
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} is {@code null} or empty.
	 * <p>
	 * @param text the {@link String} to test
	 * <p>
	 * @return {@code true} if the specified {@link String} is {@code null} or empty, {@code false}
	 *         otherwise
	 */
	public static boolean isNullOrEmpty(final String text) {
		return text == null || text.isEmpty();
	}

	/**
	 * Tests whether the specified {@link String} is not {@code null} and empty.
	 * <p>
	 * @param text the {@link String} to test
	 * <p>
	 * @return {@code true} if the specified {@link String} is not {@code null} and empty,
	 *         {@code false} otherwise
	 */
	public static boolean isEmpty(final String text) {
		return text != null && text.isEmpty();
	}

	/**
	 * Tests whether the specified {@link String} is not {@code null} and not empty.
	 * <p>
	 * @param text the {@link String} to test
	 * <p>
	 * @return {@code true} if the specified {@link String} is not {@code null} and not empty,
	 *         {@code false} otherwise
	 */
	public static boolean isNotEmpty(final String text) {
		return text != null && !text.isEmpty();
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} is between the specified lower and upper bound
	 * {@link String}.
	 * <p>
	 * @param text the {@link String} to test
	 * @param from the lower bound {@link String} to test against (inclusive)
	 * @param to   the upper bound {@link String} to test against (exclusive)
	 * <p>
	 * @return {@code true} if the specified {@link String} is between the specified lower and upper
	 *         bound {@link String}, {@code false} otherwise
	 */
	public static boolean isBetween(final String text, final String from, final String to) {
		return compare(text, from) >= 0 && compare(text, to) < 0;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code text} is numeric.
	 * <p>
	 * @param text the {@link String} to test
	 * <p>
	 * @return {@code true} if {@code text} is numeric, {@code false} otherwise
	 */
	public static boolean isNumeric(final String text) {
		if (isNullOrEmpty(text)) {
			return false;
		}
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
	 * Tests whether the specified {@link String} contains the specified {@code char} token.
	 * <p>
	 * @param text  the {@link String} to test
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
	 * @param text   the {@link String} to test
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
	 * @param text  the {@link String} to test
	 * @param token the token {@link String} to test for presence
	 * <p>
	 * @return {@code true} if the specified {@link String} contains the specified token
	 *         {@link String}, {@code false} otherwise
	 */
	public static boolean contains(final String text, final String token) {
		return text != null && text.contains(token);
	}

	/**
	 * Tests whether the specified {@link String} contains any of the the specified token
	 * {@link String}.
	 * <p>
	 * @param text   the {@link String} to test
	 * @param tokens the array of token {@link String} to test for presence
	 * <p>
	 * @return {@code true} if the specified {@link String} contains any of the the specified token
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
	 * Tests whether {@code text} matches {@code expression}.
	 * <p>
	 * @param text       the {@link String} to test
	 * @param expression the expression {@link String} to test for presence
	 * <p>
	 * @return {@code true} if {@code text} matches {@code expression}, {@code false} otherwise
	 */
	public static boolean matches(final String text, final String expression) {
		return text != null && expression != null && text.matches(expression);
	}

	/**
	 * Tests whether {@code text} matches any {@code expressions}.
	 * <p>
	 * @param text        the {@link String} to test
	 * @param expressions the array of expression {@link String} to test for presence
	 * <p>
	 * @return {@code true} if {@code text} matches any {@code expressions}, {@code false} otherwise
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
	 * zero or a positive integer as {@code a} is less than, equal to or greater than {@code b}.
	 * <p>
	 * @param a the {@link String} to compare for lexicographic order
	 * @param b the other {@link String} to compare against for lexicographic order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compare(final String a, final String b) {
		return a.compareTo(b);
	}

	/**
	 * Compares the specified {@link String} for lexicographic order, ignoring case differences.
	 * Returns a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 * greater than {@code b}.
	 * <p>
	 * @param a the {@link String} to compare for lexicographic order
	 * @param b the other {@link String} to compare against for lexicographic order
	 * <p>
	 * @return a negative integer, zero or a positive integer as {@code a} is less than, equal to or
	 *         greater than {@code b}
	 */
	public static int compareIgnoreCase(final String a, final String b) {
		return a.compareToIgnoreCase(b);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the representative {@link String} of {@code a} and {@code b} are equal to each
	 * other.
	 * <p>
	 * @param a the {@link Object} to compare for equality
	 * @param b the other {@link Object} to compare against for equality
	 * <p>
	 * @return {@code true} if the representative {@link String} of {@code a} and {@code b} are
	 *         equal to each other, {@code false} otherwise
	 */
	public static boolean equals(final Object a, final Object b) {
		return toString(a).equals(toString(b));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the representative {@link String} of the specified {@link Object} if it is not
	 * {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param object an {@link Object}
	 * @param length the length of the representative {@link String}
	 * <p>
	 * @return the representative {@link String} of the specified {@link Object} if it is not
	 *         {@code null}, {@code "null"} otherwise
	 */
	public static String toString(final Object object, final int length) {
		return truncate(toString(object), length);
	}

	/**
	 * Returns the representative {@link String} of the specified {@link Object} if it is not
	 * {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return the representative {@link String} of the specified {@link Object} if it is not
	 *         {@code null}, {@code "null"} otherwise
	 */
	public static String toString(final Object object) {
		if (object == null) {
			return NULL;
		}
		final Class<?> c = object.getClass();
		if (c.isArray()) {
			if (c.getComponentType().isPrimitive()) {
				if (Booleans.isPrimitiveArray(c)) {
					return Booleans.toString((boolean[]) object);
				} else if (Characters.isPrimitiveArray(c)) {
					return Characters.toString((char[]) object);
				} else if (Bytes.isPrimitiveArray(c)) {
					return Bytes.toString((byte[]) object);
				} else if (Shorts.isPrimitiveArray(c)) {
					return Shorts.toString((short[]) object);
				} else if (Integers.isPrimitiveArray(c)) {
					return Integers.toString((int[]) object);
				} else if (Longs.isPrimitiveArray(c)) {
					return Longs.toString((long[]) object);
				} else if (Floats.isPrimitiveArray(c)) {
					return Floats.toString((float[]) object);
				} else if (Doubles.isPrimitiveArray(c)) {
					return Doubles.toString((double[]) object);
				}
				throw new IllegalClassException(c);
			}
			return Arrays.toString((Object[]) object);
		} else if (Collections.is(c)) {
			Collections.toString((Collection<?>) object);
		} else if (Numbers.is(c)) {
			return Formats.formatNumber(object);
		}
		return String.valueOf(object);
	}

	/**
	 * Returns the representative {@link String} of the specified {@link Object} if it is not
	 * {@code null} or {@code "null"}, {@code null} otherwise.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return the representative {@link String} of the specified {@link Object} if it is not
	 *         {@code null} or {@code "null"}, {@code null} otherwise
	 */
	public static String toStringWithNull(final Object object) {
		final String string = toString(object);
		if (NULL.equals(string)) {
			return null;
		}
		return string;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the representative {@link String} of the specified {@link Object} if it is not
	 * {@code null}, {@code defaultString} otherwise.
	 * <p>
	 * @param object        the {@link Object}
	 * @param defaultString the {@link String} to return if {@code null}
	 * <p>
	 * @return the representative {@link String} of the specified {@link Object} if it is not
	 *         {@code null}, {@code defaultString} otherwise
	 */
	public static String toString(final Object object, final String defaultString) {
		return object != null ? toString(object) : defaultString;
	}

	/**
	 * Returns the representative {@link String} of the specified {@link Object} if it is not
	 * {@code null} or {@code "null"}, {@code defaultString} otherwise.
	 * <p>
	 * @param object        the {@link Object}
	 * @param defaultString the {@link String} to return if {@code null}
	 * <p>
	 * @return the representative {@link String} of the specified {@link Object} if it is not
	 *         {@code null} or {@code "null"}, {@code defaultString} otherwise
	 */
	public static String toStringWithNull(final Object object, final String defaultString) {
		final String string = toString(object);
		if (NULL.equals(string)) {
			return defaultString;
		}
		return string;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Exception} if it is not
	 * {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param exception an {@link Exception}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Exception} if it is not
	 *         {@code null}, {@code "null"} otherwise
	 */
	public static String toString(final Exception exception) {
		return toString(exception, 0);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Exception} with the specified
	 * number of {@link StackTraceElement} if it is not {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param exception              an {@link Exception}
	 * @param stackTraceElementCount the number of {@link StackTraceElement} to add
	 * <p>
	 * @return a representative {@link String} of the specified {@link Exception} with the specified
	 *         number of {@link StackTraceElement} if it is not {@code null}, {@code "null"}
	 *         otherwise
	 */
	public static String toString(final Exception exception, final int stackTraceElementCount) {
		if (exception != null) {
			if (stackTraceElementCount > 0) {
				final StackTraceElement[] stackTraces = Arrays.<StackTraceElement>take(
						exception.getStackTrace(), 0, stackTraceElementCount);
				return exception.getLocalizedMessage().concat(":").concat(NEW_LINE)
						.concat(joinWith(stackTraces, NEW_LINE));
			}
			return exception.getLocalizedMessage();
		}
		return NULL;
	}
}
