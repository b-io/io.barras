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

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jupiter.common.map.ObjectToStringMapper;
import jupiter.common.map.parser.StringParser;
import jupiter.common.map.remover.StringRemover;
import jupiter.common.map.wrapper.StringWrapper;
import jupiter.common.test.Arguments;
import jupiter.common.test.IntegerArguments;

public class Strings {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String EMPTY = "";
	public static final String[] EMPTY_ARRAY = new String[] {};

	public static final String NULL = "null";

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final char DEFAULT_BAR_CHARACTER = '-';
	public static final int DEFAULT_INITIAL_CAPACITY = 256;

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final StringParser PARSER = new StringParser();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final StringWrapper SINGLE_QUOTER = new StringWrapper(Characters.SINGLE_QUOTE,
			Characters.SINGLE_QUOTE);
	public static final StringWrapper DOUBLE_QUOTER = new StringWrapper(Characters.DOUBLE_QUOTE,
			Characters.DOUBLE_QUOTE);
	public static final StringWrapper QUOTER = new StringWrapper(Characters.LEFT_QUOTE,
			Characters.RIGHT_QUOTE);
	public static final StringRemover UNQUOTER = new StringRemover(join(Characters.SINGLE_QUOTE,
			Characters.DOUBLE_QUOTE, Characters.LEFT_QUOTE, Characters.RIGHT_QUOTE));

	public static final StringWrapper PARENTHESER = new StringWrapper(Characters.LEFT_PARENTHESIS,
			Characters.RIGHT_PARENTHESIS);
	public static final StringWrapper BRACKETER = new StringWrapper(Characters.LEFT_BRACKET,
			Characters.RIGHT_BRACKET);


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Strings() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONVERTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} from the specified {@link Object}.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return a {@link String} from the specified {@link Object}
	 */
	public static String convert(final Object object) {
		return PARSER.call(object);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String toLowerCase(final String string) {
		return string.toLowerCase(Formats.DEFAULT_LOCALE);
	}

	public static String toUpperCase(final String string) {
		return string.toUpperCase(Formats.DEFAULT_LOCALE);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an array of {@code String} values from the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array
	 * @param array an array of type {@code T}
	 * <p>
	 * @return an array of {@code String} values from the specified array of type {@code T}
	 */
	public static <T> String[] toArray(final T... array) {
		return PARSER.callToArray(array);
	}

	/**
	 * Returns a 2D array of {@code String} values from the specified 2D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array
	 * @param array2D a 2D array of type {@code T}
	 * <p>
	 * @return a 2D array of {@code String} values from the specified 2D array of type {@code T}
	 */
	public static <T> String[][] toArray2D(final T[]... array2D) {
		return PARSER.callToArray2D(array2D);
	}

	/**
	 * Returns a 3D array of {@code String} values from the specified 3D array of type {@code T}.
	 * <p>
	 * @param <T>     the type of the array
	 * @param array3D a 3D array of type {@code T}
	 * <p>
	 * @return a 3D array of {@code String} values from the specified 3D array of type {@code T}
	 */
	public static <T> String[][][] toArray3D(final T[][]... array3D) {
		return PARSER.callToArray3D(array3D);
	}

	/**
	 * Returns an array of {@link String} from the specified {@link Collection} of type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an array of {@link String} from the specified {@link Collection} of type {@code T}
	 */
	public static <T> String[] collectionToArray(final Collection<T> collection) {
		return PARSER.callCollectionToArray(collection);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static StringBuilder createBuilder() {
		return createBuilder(DEFAULT_INITIAL_CAPACITY);
	}

	public static StringBuilder createBuilder(final int capacity) {
		return new StringBuilder(capacity);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link String} bar of the default length.
	 * <p>
	 * @return a {@link String} bar of the default length
	 */
	public static String createBar() {
		return Strings.createBar(Formats.DEFAULT_LINE_LENGTH);
	}

	/**
	 * Creates a {@link String} bar with the specified {@code char} value.
	 * <p>
	 * @param character the character of the bar to create
	 * <p>
	 * @return a {@link String} bar of the specified length
	 */
	public static String createBar(final char character) {
		return repeat(character, Formats.DEFAULT_LINE_LENGTH);
	}

	/**
	 * Creates a {@link String} bar of the specified length.
	 * <p>
	 * @param length the length of the bar to create
	 * <p>
	 * @return a {@link String} bar of the specified length
	 */
	public static String createBar(final int length) {
		return repeat(DEFAULT_BAR_CHARACTER, length);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of type {@code T}.
	 * <p>
	 * @param <T>   the type of the array
	 * @param array an array of type {@code T}
	 * <p>
	 * @return a {@link String} representation of the specified array of type {@code T}
	 */
	public static <T> String join(final T... array) {
		return joinWith(array, EMPTY);
	}

	/**
	 * Returns a {@link String} representation of the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to join
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link String} representation of the specified {@link Collection} of type {@code T}
	 */
	public static <T> String join(final Collection<T> collection) {
		return joinWith(collection, EMPTY);
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified array of type {@code T} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param <T>       the type of the array
	 * @param array     an array of type {@code T}
	 * @param delimiter the delimiting {@code char} value
	 * <p>
	 * @return a {@link String} representation of the specified array of type {@code T} joined by
	 *         {@code delimiter}
	 */
	public static <T> String joinWith(final T[] array, final char delimiter) {
		return joinWith(array, toString(delimiter));
	}

	/**
	 * Returns a {@link String} representation of the specified array of type {@code T} joined by
	 * {@code delimiter}.
	 * <p>
	 * @param <T>       the type of the array
	 * @param array     an array of type {@code T}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified array of type {@code T} joined by
	 *         {@code delimiter}
	 */
	public static <T> String joinWith(final T[] array, final String delimiter) {
		// Check the arguments
		Arguments.requireNonNull(array);

		// Initialize
		int i = 0;
		final StringBuilder builder = createBuilder(array.length * delimiter.length());

		// Join the array
		if (array.length > 0) {
			builder.append(toString(array[i]));
			++i;
			while (i < array.length) {
				builder.append(delimiter).append(toString(array[i]));
				++i;
			}
		}
		return builder.toString();
	}

	/**
	 * Returns a {@link String} representation of the specified array of type {@code T} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param <T>       the type of the array
	 * @param array     an array of type {@code T}
	 * @param delimiter the delimiting {@code char} value
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of type {@code T} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static <T> String joinWith(final T[] array, final char delimiter,
			final ObjectToStringMapper wrapper) {
		return joinWith(array, toString(delimiter), wrapper);
	}

	/**
	 * Returns a {@link String} representation of the specified array of type {@code T} joined by
	 * {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param <T>       the type of the array
	 * @param array     an array of type {@code T}
	 * @param delimiter the delimiting {@link String}
	 * @param wrapper   an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified array of type {@code T} joined by
	 *         {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static <T> String joinWith(final T[] array, final String delimiter,
			final ObjectToStringMapper wrapper) {
		// Check the arguments
		Arguments.requireNonNull(array);

		// Initialize
		int i = 0;
		final StringBuilder builder = createBuilder(array.length * delimiter.length());

		// Join the array
		if (array.length > 0) {
			builder.append(wrapper.call(array[i]));
			++i;
			while (i < array.length) {
				builder.append(delimiter).append(wrapper.call(array[i]));
				++i;
			}
		}
		return builder.toString();
	}

	//////////////////////////////////////////////

	/**
	 * Returns a {@link String} representation of the specified {@link Collection} of type {@code T}
	 * joined by {@code delimiter}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to join
	 * @param collection a {@link Collection} of type {@code T}
	 * @param delimiter  the delimiting {@code char} value
	 * <p>
	 * @return a {@link String} representation of the specified {@link Collection} of type {@code T}
	 *         joined by {@code delimiter}
	 */
	public static <T> String joinWith(final Collection<T> collection, final char delimiter) {
		return joinWith(collection, toString(delimiter));
	}

	/**
	 * Returns a {@link String} representation of the specified {@link Collection} of type {@code T}
	 * joined by {@code delimiter}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to join
	 * @param collection a {@link Collection} of type {@code T}
	 * @param delimiter  the delimiting {@link String}
	 * <p>
	 * @return a {@link String} representation of the specified {@link Collection} of type {@code T}
	 *         joined by {@code delimiter}
	 */
	public static <T> String joinWith(final Collection<T> collection, final String delimiter) {
		// Check the arguments
		Arguments.requireNonNull(collection);

		// Initialize
		final StringBuilder builder = createBuilder(collection.size() * delimiter.length());
		final Iterator<T> iterator = collection.iterator();

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
	 * Returns a {@link String} representation of the specified {@link Collection} of type {@code T}
	 * joined by {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to join
	 * @param collection a {@link Collection} of type {@code T}
	 * @param delimiter  the delimiting {@code char} value
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified {@link Collection} of type {@code T}
	 *         joined by {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static <T> String joinWith(final Collection<T> collection, final char delimiter,
			final ObjectToStringMapper wrapper) {
		return joinWith(collection, toString(delimiter), wrapper);
	}

	/**
	 * Returns a {@link String} representation of the specified {@link Collection} of type {@code T}
	 * joined by {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to join
	 * @param collection a {@link Collection} of type {@code T}
	 * @param delimiter  the delimiting {@link String}
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a {@link String} representation of the specified {@link Collection} of type {@code T}
	 *         joined by {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static <T> String joinWith(final Collection<T> collection, final String delimiter,
			final ObjectToStringMapper wrapper) {
		// Check the arguments
		Arguments.requireNonNull(collection);

		// Initialize
		final StringBuilder builder = createBuilder(collection.size() * delimiter.length());
		final Iterator<T> iterator = collection.iterator();

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
	 * Returns a pseudorandom, uniformly distributed {@link String} of the specified length.
	 * <p>
	 * @param length the length of the {@link String} to create
	 * <p>
	 * @return a pseudorandom, uniformly distributed {@link String} of the specified length
	 */
	public static String random(final int length) {
		final StringBuilder builder = createBuilder(length);
		for (int i = 0; i < length; ++i) {
			builder.append(Characters.random());
		}
		return builder.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static String repeat(final char character, final int repeat) {
		return repeat(toString(character), repeat);
	}

	public static String repeat(final String string, final int repeat) {
		// Check the arguments
		Arguments.requireNonNull(string);
		IntegerArguments.requireNonNegative(repeat);

		// Initialize
		final StringBuilder builder = createBuilder(repeat);

		// Repeat the string
		for (int i = 0; i < repeat; ++i) {
			builder.append(string);
		}
		return builder.toString();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the escaped {@link String} representation of the specified unescaped content (i.e.
	 * without traces of offending characters that can prevent parsing).
	 * <p>
	 * @param content an {@link Object}
	 * <p>
	 * @return the escaped {@link String} representation of the specified unescaped content (i.e.
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
	 * Returns the unescaped {@link String} representation of the specified escaped content (i.e.
	 * with traces of offending characters that can prevent parsing).
	 * <p>
	 * @param content an {@link Object}
	 * <p>
	 * @return the unescaped {@link String} representation of the specified escaped content (i.e.
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
	 * Returns a {@link String} wrapped with {@code left} and {@code right}.
	 * <p>
	 * @param content an {@link Object}
	 * @param wrapper the {@link String} to wrap with
	 * <p>
	 * @return a {@link String} wrapped with {@code left} and {@code right}
	 */
	public static String wrap(final Object content, final String wrapper) {
		if (content == null) {
			return null;
		}
		return wrapper + toString(content) + wrapper;
	}

	/**
	 * Returns a {@link String} wrapped with {@code left} and {@code right}.
	 * <p>
	 * @param content an {@link Object}
	 * @param left    the {@link String} to wrap with on the left
	 * @param right   right {@link String} to wrap with on the right
	 * <p>
	 * @return a {@link String} wrapped with {@code left} and {@code right}
	 */
	public static String wrap(final Object content, final String left, final String right) {
		if (content == null) {
			return null;
		}
		return left + toString(content) + right;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link String} without the specified characters.
	 * <p>
	 * @param content    an {@link Object}
	 * @param characters the characters to remove
	 * <p>
	 * @return a {@link String} without the specified characters
	 */
	public static String remove(final Object content, final String characters) {
		if (content == null) {
			return null;
		}
		return toString(content).replaceAll(Strings.bracketize(characters), EMPTY);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a single quoted {@link String}.
	 * <p>
	 * @param content an {@link Object}
	 * <p>
	 * @return a single quoted {@link String}
	 */
	public static String singleQuote(final Object content) {
		return SINGLE_QUOTER.call(content);
	}

	/**
	 * Returns a double quoted {@link String}.
	 * <p>
	 * @param content an {@link Object}
	 * <p>
	 * @return a double quoted {@link String}
	 */
	public static String doubleQuote(final Object content) {
		return DOUBLE_QUOTER.call(content);
	}

	/**
	 * Returns a left and right quoted {@link String}.
	 * <p>
	 * @param content an {@link Object}
	 * <p>
	 * @return a left and right quoted {@link String}
	 */
	public static String quote(final Object content) {
		return QUOTER.call(content);
	}

	/**
	 * Returns a {@link String} without quotes.
	 * <p>
	 * @param content an {@link Object}
	 * <p>
	 * @return a {@link String} without quotes
	 */
	public static String unquote(final Object content) {
		return UNQUOTER.call(content);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a left and right parenthesized {@link String}.
	 * <p>
	 * @param content an {@link Object}
	 * <p>
	 * @return a left and right parenthesized {@link String}
	 */
	public static String parenthesize(final Object content) {
		return PARENTHESER.call(content);
	}

	/**
	 * Returns a left and right bracketized {@link String}.
	 * <p>
	 * @param content an {@link Object}
	 * <p>
	 * @return a left and right bracketized {@link String}
	 */
	public static String bracketize(final Object content) {
		return BRACKETER.call(content);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the first character of {@code string} that is in {@code characters}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * <p>
	 * @return the index of the first character of {@code string} that is in {@code characters}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirst(final String string, final char[] characters) {
		return findFirst(string, characters, 0);
	}

	/**
	 * Returns the index of the first character of {@code string} that is in {@code characters}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * <p>
	 * @return the index of the first character of {@code string} that is in {@code characters}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirst(final String string, final Collection<Character> characters) {
		return findFirst(string, characters, 0);
	}

	/**
	 * Returns the index of the first character of {@code string} that is in {@code characters},
	 * seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * @param fromIndex  the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first character of {@code string} that is in {@code characters},
	 *         seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirst(final String string, final char[] characters, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Find the first character
		for (int index = fromIndex; index < string.length(); ++index) {
			if (Characters.contains(characters, string.charAt(index))) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the first character of {@code string} that is in {@code characters},
	 * seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * @param fromIndex  the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first character of {@code string} that is in {@code characters},
	 *         seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirst(final String string, final Collection<Character> characters, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Find the first character
		for (int index = fromIndex; index < string.length(); ++index) {
			if (characters.contains(string.charAt(index))) {
				return index;
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the last character of {@code string} that is in {@code characters}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * <p>
	 * @return the index of the last character of {@code string} that is in {@code characters}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String string, final char[] characters) {
		return findLast(string, characters, string.length());
	}

	/**
	 * Returns the index of the last character of {@code string} that is in {@code characters}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * <p>
	 * @return the index of the last character of {@code string} that is in {@code characters}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String string, final Collection<Character> characters) {
		return findLast(string, characters, string.length());
	}

	/**
	 * Returns the index of the last character of {@code string} that is in {@code characters},
	 * seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * @param fromIndex  the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last character of {@code string} that is in {@code characters},
	 *         seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String string, final char[] characters, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Find the last character
		for (int index = fromIndex; index >= 0; --index) {
			if (Characters.contains(characters, string.charAt(index))) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last character of {@code string} that is in {@code characters},
	 * seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * @param fromIndex  the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last character of {@code string} that is in {@code characters},
	 *         seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String string, final Collection<Character> characters,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Find the last character
		for (int index = fromIndex; index >= 0; --index) {
			if (characters.contains(string.charAt(index))) {
				return index;
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the first character of {@code string} that is not equal to
	 * {@code character}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string    a {@link String}
	 * @param character the {@code char} value to find
	 * <p>
	 * @return the index of the first character of {@code string} that is not equal to
	 *         {@code character}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotEqualTo(final String string, final char character) {
		return findFirstNotEqualTo(string, character, 0);
	}

	/**
	 * Returns the index of the first character of {@code string} that is not equal to
	 * {@code character}, seeking forward from {@code fromIndex}, or {@code -1} if there is no such
	 * occurrence.
	 * <p>
	 * @param string    a {@link String}
	 * @param character the {@code char} value to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first character of {@code string} that is not equal to
	 *         {@code character}, seeking forward from {@code fromIndex}, or {@code -1} if there is
	 *         no such occurrence
	 */
	public static int findFirstNotEqualTo(final String string, final char character, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(character);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Find the first character
		for (int index = fromIndex; index < string.length(); ++index) {
			if (string.charAt(index) != character) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the first character of {@code string} that is not in {@code characters},
	 * or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * <p>
	 * @return the index of the first character of {@code string} that is not in {@code characters},
	 *         or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String string, final char[] characters) {
		return findFirstNotIn(string, characters, 0);
	}

	/**
	 * Returns the index of the first character of {@code string} that is not in {@code characters},
	 * or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * <p>
	 * @return the index of the first character of {@code string} that is not in {@code characters},
	 *         or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String string, final Collection<Character> characters) {
		return findFirstNotIn(string, characters, 0);
	}

	/**
	 * Returns the index of the first character of {@code string} that is not in {@code characters},
	 * seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * @param fromIndex  the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first character of {@code string} that is not in {@code characters},
	 *         seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String string, final char[] characters,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Find the first character
		for (int index = fromIndex; index < string.length(); ++index) {
			if (!Characters.contains(characters, string.charAt(index))) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the first character of {@code string} that is not in {@code characters},
	 * seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * @param fromIndex  the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first character of {@code string} that is not in {@code characters},
	 *         seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String string,
			final Collection<Character> characters, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Find the first character
		for (int index = fromIndex; index < string.length(); ++index) {
			if (!characters.contains(string.charAt(index))) {
				return index;
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the last character of {@code string} that is not equal to
	 * {@code character}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string    a {@link String}
	 * @param character the {@code char} value to find
	 * <p>
	 * @return the index of the last character of {@code string} that is not equal to
	 *         {@code character}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotEqualTo(final String string, final char character) {
		return findLastNotEqualTo(string, character, string.length() - 1);
	}

	/**
	 * Returns the index of the last character of {@code string} that is not equal to
	 * {@code character}, seeking backward from {@code fromIndex}, or {@code -1} if there is no such
	 * occurrence.
	 * <p>
	 * @param string    a {@link String}
	 * @param character the {@code char} value to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last character of {@code string} that is not equal to
	 *         {@code character}, seeking backward from {@code fromIndex}, or {@code -1} if there is
	 *         no such occurrence
	 */
	public static int findLastNotEqualTo(final String string, final char character, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(character);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Find the last character
		for (int index = fromIndex; index >= 0; --index) {
			if (string.charAt(index) != character) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last character of {@code string} that is not in {@code characters},
	 * or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * <p>
	 * @return the index of the last character of {@code string} that is not in {@code characters},
	 *         or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String string, final char[] characters) {
		return findLastNotIn(string, characters, string.length());
	}

	/**
	 * Returns the index of the last character of {@code string} that is not in {@code characters},
	 * or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * <p>
	 * @return the index of the last character of {@code string} that is not in {@code characters},
	 *         or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String string, final Collection<Character> characters) {
		return findLastNotIn(string, characters, string.length());
	}

	/**
	 * Returns the index of the last character of {@code string} that is not in {@code characters},
	 * seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * @param fromIndex  the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last character of {@code string} that is not in {@code characters},
	 *         seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String string, final char[] characters, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Find the last character
		for (int index = fromIndex; index >= 0; --index) {
			if (!Characters.contains(characters, string.charAt(index))) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last character of {@code string} that is not in {@code characters},
	 * seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * @param fromIndex  the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last character of {@code string} that is not in {@code characters},
	 *         seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String string,
			final Collection<Character> characters, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Find the last character
		for (int index = fromIndex; index >= 0; --index) {
			if (!characters.contains(string.charAt(index))) {
				return index;
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the indexes of {@code character} in {@code string}.
	 * <p>
	 * @param string    a {@link String}
	 * @param character the {@code char} value to find
	 * <p>
	 * @return all the indexes of {@code character} in {@code string}
	 */
	public static List<Integer> getAllIndexes(final String string, final char character) {
		return getIndexes(string, character, 0);
	}

	/**
	 * Returns all the indexes of {@code characters} in {@code string}.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * <p>
	 * @return all the indexes of {@code characters} in {@code string}
	 */
	public static List<Integer> getAllIndexes(final String string, final char[] characters) {
		return getIndexes(string, characters, 0);
	}

	/**
	 * Returns all the indexes of {@code characters} in {@code string}.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * <p>
	 * @return all the indexes of {@code characters} in {@code string}
	 */
	public static List<Integer> getAllIndexes(final String string,
			final Collection<Character> characters) {
		return getIndexes(string, characters, 0);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the indexes of {@code character} in {@code string}, seeking forward from
	 * {@code fromIndex}.
	 * <p>
	 * @param string    a {@link String}
	 * @param character the {@code char} value to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of {@code character} in {@code string}, seeking forward from
	 *         {@code fromIndex}
	 */
	public static List<Integer> getIndexes(final String string, final char character,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(character);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Initialize
		final List<Integer> indexes = new LinkedList<Integer>();
		int index = string.indexOf(character, fromIndex);

		// Get the indexes
		while (index >= 0) {
			indexes.add(index);
			index = string.indexOf(character, index + 1);
		}
		return indexes;
	}

	/**
	 * Returns the indexes of the characters of {@code string} that are in {@code characters},
	 * seeking forward from {@code fromIndex}.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * @param fromIndex  the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of the characters of {@code string} that are in {@code characters},
	 *         seeking forward from {@code fromIndex}
	 */
	public static List<Integer> getIndexes(final String string, final char[] characters,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Initialize
		final List<Integer> indexes = new LinkedList<Integer>();
		final char[] array = string.toCharArray();

		// Get the indexes
		for (int index = fromIndex; index < string.length(); ++index) {
			if (Characters.contains(characters, array[index])) {
				indexes.add(index);
			}
		}
		return indexes;
	}

	/**
	 * Returns the indexes of the characters of {@code string} that are in {@code characters},
	 * seeking forward from {@code fromIndex}.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * @param fromIndex  the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of the characters of {@code string} that are in {@code characters},
	 *         seeking forward from {@code fromIndex}
	 */
	public static List<Integer> getIndexes(final String string,
			final Collection<Character> characters, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(fromIndex);
		IntegerArguments.requireLessThan(fromIndex, string.length());

		// Initialize
		final List<Integer> indexes = new LinkedList<Integer>();
		final char[] array = string.toCharArray();

		// Get the indexes
		for (int index = fromIndex; index < string.length(); ++index) {
			if (characters.contains(array[index])) {
				indexes.add(index);
			}
		}
		return indexes;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the indexes of {@code character} in {@code string}, seeking forward to
	 * {@code toIndex}.
	 * <p>
	 * @param string    a {@link String}
	 * @param character the {@code char} value to find
	 * @param toIndex   the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of {@code character} in {@code string}, seeking forward to
	 *         {@code toIndex}
	 */
	public static List<Integer> getIndexesTo(final String string, final char character,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(character);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, string.length());

		// Initialize
		final List<Integer> indexes = new LinkedList<Integer>();
		int index = string.indexOf(character);

		// Get the indexes
		while (index >= 0 && index < toIndex) {
			indexes.add(index);
			index = string.indexOf(character, index + 1);
		}
		return indexes;
	}

	/**
	 * Returns the indexes of the characters of {@code string} that are in {@code characters},
	 * seeking forward to {@code toIndex}.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the array of {@code char} values to find
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of the characters of {@code string} that are in {@code characters},
	 *         seeking forward to {@code toIndex}
	 */
	public static List<Integer> getIndexesTo(final String string, final char[] characters,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, string.length());

		// Initialize
		final List<Integer> indexes = new LinkedList<Integer>();
		final char[] array = string.toCharArray();

		// Get the indexes
		for (int index = 0; index < toIndex; ++index) {
			if (Characters.contains(characters, array[index])) {
				indexes.add(index);
			}
		}
		return indexes;
	}

	/**
	 * Returns the indexes of the characters of {@code string} that are in {@code characters},
	 * seeking forward to {@code toIndex}.
	 * <p>
	 * @param string     a {@link String}
	 * @param characters the {@link Collection} of {@link Character} to find
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of the characters of {@code string} that are in {@code characters},
	 *         seeking forward to {@code toIndex}
	 */
	public static List<Integer> getIndexesTo(final String string,
			final Collection<Character> characters, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(characters);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, string.length());

		// Initialize
		final List<Integer> indexes = new LinkedList<Integer>();
		final char[] array = string.toCharArray();

		// Get the indexes
		for (int index = 0; index < toIndex; ++index) {
			if (characters.contains(array[index])) {
				indexes.add(index);
			}
		}
		return indexes;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiterIndexes}.
	 * <p>
	 * @param string           a {@link String}
	 * @param delimiterIndexes an array of {@code int} values
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiterIndexes}
	 */
	public static List<String> getTokens(final String string, final int[] delimiterIndexes) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(delimiterIndexes);

		// Initialize
		final List<String> tokens = new LinkedList<String>();
		int index = 0;

		// Get the tokens
		for (final int delimiterIndex : delimiterIndexes) {
			if (delimiterIndex > index) {
				tokens.add(string.substring(index, delimiterIndex));
			}
			index = delimiterIndex + 1;
		}
		tokens.add(string.substring(index));
		return tokens;
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiterIndexes}.
	 * <p>
	 * @param string           a {@link String}
	 * @param delimiterIndexes a {@link Collection} of {@link Integer}
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiterIndexes}
	 */
	public static List<String> getTokens(final String string, final Collection<Integer> delimiterIndexes) {
		// Check the arguments
		Arguments.requireNonNull(string);
		Arguments.requireNonNull(delimiterIndexes);

		// Initialize
		final List<String> tokens = new LinkedList<String>();
		int index = 0;

		// Get the tokens
		for (final int delimiterIndex : delimiterIndexes) {
			if (delimiterIndex > index) {
				tokens.add(string.substring(index, delimiterIndex));
			}
			index = delimiterIndex + 1;
		}
		tokens.add(string.substring(index));
		return tokens;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SPLITTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiter}.
	 * <p>
	 * @param string    a {@link String}
	 * @param delimiter the delimiting {@code char} value
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiter}
	 */
	public static List<String> split(final String string, final char delimiter) {
		return getTokens(string, getAllIndexes(string, delimiter));
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiter} (inside).
	 * <p>
	 * @param string    a {@link String}
	 * @param delimiter the delimiting {@code char} value
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiter} (inside)
	 */
	public static List<String> splitInside(final String string, final char delimiter) {
		return splitTo(string, delimiter, findLastNotEqualTo(string, delimiter));
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiter}.
	 * <p>
	 * @param string    a {@link String}
	 * @param delimiter the delimiting {@code char} value
	 * @param toIndex   the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiter}
	 */
	public static List<String> splitTo(final String string, final char delimiter, final int toIndex) {
		return getTokens(string, getIndexesTo(string, delimiter, toIndex));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiters}.
	 * <p>
	 * @param string     a {@link String}
	 * @param delimiters the array of delimiting {@code char} values
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiters}
	 */
	public static List<String> split(final String string, final char[] delimiters) {
		return getTokens(string, getAllIndexes(string, delimiters));
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiters} (inside).
	 * <p>
	 * @param string     a {@link String}
	 * @param delimiters the array of delimiting {@code char} values
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiters} (inside)
	 */
	public static List<String> splitInside(final String string, final char[] delimiters) {
		return splitTo(string, delimiters, findLastNotIn(string, delimiters));
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiters} to {@code toIndex}.
	 * <p>
	 * @param string     a {@link String}
	 * @param delimiters the array of delimiting {@code char} values
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiters} to {@code toIndex}
	 */
	public static List<String> splitTo(final String string, final char[] delimiters,
			final int toIndex) {
		return getTokens(string, getIndexesTo(string, delimiters, toIndex));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiters}.
	 * <p>
	 * @param string     a {@link String}
	 * @param delimiters the {@link Collection} of delimiting {@link Character}
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiters}
	 */
	public static List<String> split(final String string,
			final Collection<Character> delimiters) {
		return getTokens(string, getAllIndexes(string, delimiters));
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiters} (inside).
	 * <p>
	 * @param string     a {@link String}
	 * @param delimiters the {@link Collection} of delimiting {@link Character}
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiters} (inside)
	 */
	public static List<String> splitInside(final String string, final Collection<Character> delimiters) {
		return splitTo(string, delimiters, findLastNotIn(string, delimiters));
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code string} around
	 * {@code delimiters} to {@code toIndex}.
	 * <p>
	 * @param string     a {@link String}
	 * @param delimiters the {@link Collection} of delimiting {@link Character}
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code string} around
	 *         {@code delimiters} to {@code toIndex}
	 */
	public static List<String> splitTo(final String string,
			final Collection<Character> delimiters, final int toIndex) {
		return getTokens(string, getIndexesTo(string, delimiters, toIndex));
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code string} is {@code null} or empty.
	 * <p>
	 * @param string the {@link String} to test
	 * <p>
	 * @return {@code true} if {@code string} is {@code null} or empty, {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final String string) {
		return string == null || string.length() == 0;
	}

	/**
	 * Tests whether {@code string} is not {@code null} and empty.
	 * <p>
	 * @param string the {@link String} to test
	 * <p>
	 * @return {@code true} if {@code string} is not {@code null} and empty, {@code false} otherwise
	 */
	public static boolean isEmpty(final String string) {
		return string != null && string.length() == 0;
	}

	/**
	 * Tests whether {@code string} is not {@code null} and not empty.
	 * <p>
	 * @param string the {@link String} to test
	 * <p>
	 * @return {@code true} if {@code string} is not {@code null} and not empty, {@code false}
	 *         otherwise
	 */
	public static boolean isNotEmpty(final String string) {
		return string != null && string.length() > 0;
	}

	/**
	 * Tests whether {@code string} is numeric.
	 * <p>
	 * @param string the {@link String} to test
	 * <p>
	 * @return {@code true} if {@code string} is numeric, {@code false} otherwise
	 */
	public static boolean isNumeric(final String string) {
		if (string == null) {
			return false;
		}
		final NumberFormat formatter = NumberFormat.getInstance();
		final ParsePosition position = new ParsePosition(0);
		formatter.parse(string, position);
		return string.length() == position.getIndex();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code string} contains {@code character}.
	 * <p>
	 * @param string    a {@link String}
	 * @param character the {@code char} value to test for presence
	 * <p>
	 * @return {@code true} if {@code string} contains {@code character}, {@code false} otherwise
	 */
	public static boolean contains(final String string, final char character) {
		if (string == null) {
			return false;
		}
		return string.indexOf(character) >= 0;
	}

	/**
	 * Tests whether {@code string} contains {@code word}.
	 * <p>
	 * @param string a {@link String}
	 * @param word   the {@link String} to test for presence
	 * <p>
	 * @return {@code true} if {@code string} contains {@code word}, {@code false} otherwise
	 */
	public static boolean contains(final String string, final String word) {
		if (string == null) {
			return false;
		}
		return string.contains(word);
	}

	/**
	 * Tests whether {@code string} matches {@code expression}.
	 * <p>
	 * @param string     a {@link String}
	 * @param expression the regular expression to test for presence
	 * <p>
	 * @return {@code true} if {@code string} matches {@code expression}, {@code false} otherwise
	 */
	public static boolean matches(final String string, final String expression) {
		if (string == null) {
			return false;
		}
		return string.matches(expression);
	}

	/**
	 * Tests whether {@code string} matches any {@code expressions}.
	 * <p>
	 * @param string      a {@link String}
	 * @param expressions the array of regular expressions to test for presence
	 * <p>
	 * @return {@code true} if {@code string} matches any {@code expressions}, {@code false}
	 *         otherwise
	 */
	public static boolean matches(final String string, final String[] expressions) {
		if (string == null) {
			return false;
		}
		for (final String expression : expressions) {
			if (string.matches(expression)) {
				return true;
			}
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the {@link String} representations of the arguments are equal to each other.
	 * <p>
	 * @param a an {@link Object}
	 * @param b another {@link Object} to compare with {@code a} for equality
	 * <p>
	 * @return {@code true} if the {@link String} representations of the arguments are equal to each
	 *         other, {@code false} otherwise
	 */
	public static boolean equals(final Object a, final Object b) {
		return toString(a).equals(toString(b));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@code String} representation of the specified content if not {@code null},
	 * {@code "null"} otherwise.
	 * <p>
	 * @param content an {@link Object}
	 * <p>
	 * @return the {@code String} representation of the specified content if not {@code null},
	 *         {@code "null"} otherwise
	 */
	public static String toString(final Object content) {
		return String.valueOf(content);
	}

	/**
	 * Returns the {@code String} representation of the specified content if not {@code null} or
	 * {@code "null"}, {@code null} otherwise.
	 * <p>
	 * @param content an {@link Object}
	 * <p>
	 * @return the {@code String} representation of the specified content if not {@code null} or
	 *         {@code "null"}, {@code null} otherwise
	 */
	public static String toStringWithNull(final Object content) {
		final String value = String.valueOf(content);
		if (NULL.equals(value)) {
			return null;
		}
		return value;
	}

	/**
	 * Returns the {@code String} representation of the specified content if not {@code null},
	 * {@code defaultString} otherwise.
	 * <p>
	 * @param content       an {@link Object}
	 * @param defaultString the {@link String} to return if {@code null}
	 * <p>
	 * @return the {@code String} representation of the specified content if not {@code null},
	 *         {@code defaultString} otherwise
	 */
	public static String toString(final Object content, final String defaultString) {
		return content != null ? String.valueOf(content) : defaultString;
	}

	/**
	 * Returns the {@code String} representation of the specified content if not {@code null} or
	 * {@code "null"}, {@code defaultString} otherwise.
	 * <p>
	 * @param content       an {@link Object}
	 * @param defaultString the {@link String} to return if {@code null}
	 * <p>
	 * @return the {@code String} representation of the specified content if not {@code null} or
	 *         {@code "null"}, {@code defaultString} otherwise
	 */
	public static String toStringWithNull(final Object content, final String defaultString) {
		final String value = String.valueOf(content);
		if (NULL.equals(value)) {
			return defaultString;
		}
		return value;
	}

	/**
	 * Returns a {@link String} representation of the specified {@link Exception} if not
	 * {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param exception an {@link Exception}
	 * <p>
	 * @return a {@link String} representation of the specified {@link Exception} if not
	 *         {@code null}, {@code "null"} otherwise
	 */
	public static String toString(final Exception exception) {
		return toString(exception, 0);
	}

	/**
	 * Returns a {@link String} representation of the specified {@link Exception} with the specified
	 * number of {@link StackTraceElement} if not {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param exception              an {@link Exception}
	 * @param stackTraceElementCount the number of {@link StackTraceElement} to add
	 * <p>
	 * @return a {@link String} representation of the specified {@link Exception} with the specified
	 *         number of {@link StackTraceElement} if not {@code null}, {@code "null"} otherwise
	 */
	public static String toString(final Exception exception, final int stackTraceElementCount) {
		if (exception != null) {
			if (stackTraceElementCount > 0) {
				final List<StackTraceElement> stackTraces = Arrays.take(exception.getStackTrace(),
						0, stackTraceElementCount);
				return exception.getMessage() + ":\n" + joinWith(stackTraces, "\n");
			}
			return exception.getMessage();
		}
		return "null";
	}
}
