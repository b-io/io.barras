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
import static jupiter.common.util.Formats.NEWLINE;
import static jupiter.common.util.Formats.formatNumber;

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

	public static final String NULL = "null";

	public static final String SPACE = " ";

	public static final String STAR = "*";

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile int DEFAULT_INITIAL_CAPACITY = 255;

	public static volatile char DEFAULT_PROGRESS_CHARACTER = '-';

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final StringParser PARSER = new StringParser();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final StringWrapper SINGLE_QUOTER = new StringWrapper(SINGLE_QUOTE, SINGLE_QUOTE);
	public static final StringWrapper DOUBLE_QUOTER = new StringWrapper(DOUBLE_QUOTE, DOUBLE_QUOTE);
	public static final StringWrapper QUOTER = new StringWrapper(LEFT_QUOTE, RIGHT_QUOTE);
	public static final StringRemover UNQUOTER = new StringRemover(join(SINGLE_QUOTE, DOUBLE_QUOTE,
			LEFT_QUOTE, RIGHT_QUOTE));

	public static final StringWrapper PARENTHESER = new StringWrapper(LEFT_PARENTHESIS,
			RIGHT_PARENTHESIS);
	public static final StringWrapper BRACKETER = new StringWrapper(LEFT_BRACKET, RIGHT_BRACKET);


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
	 * Returns an array of {@link String} from the specified {@link Collection} of {@link Object}.
	 * <p>
	 * @param collection a {@link Collection} of {@link Object}
	 * <p>
	 * @return an array of {@link String} from the specified {@link Collection} of {@link Object}
	 */
	public static String[] collectionToArray(final Collection<?> collection) {
		return PARSER.callCollectionToArray(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link List} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link List} of {@link String} from the specified {@code T} array
	 */
	public static <T> List<String> toList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns a {@link List} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link List} of {@link String} from the specified {@code T} array
	 */
	public static <T> List<String> asList(final T... array) {
		return toList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link String} from the specified {@code T} array
	 */
	public static <T> ExtendedList<String> toExtendedList(final T[] array) {
		return PARSER.callToList(array);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return an {@link ExtendedList} of {@link String} from the specified {@code T} array
	 */
	public static <T> ExtendedList<String> asExtendedList(final T... array) {
		return toExtendedList(array);
	}

	/**
	 * Returns a {@link List} of {@link String} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link List} of {@link String} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> List<String> collectionToList(final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	/**
	 * Returns an {@link ExtendedList} of {@link String} from the specified {@link Collection} of
	 * type {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return an {@link ExtendedList} of {@link String} from the specified {@link Collection} of
	 *         type {@code T}
	 */
	public static <T> ExtendedList<String> collectionToExtendedList(
			final Collection<T> collection) {
		return PARSER.callCollectionToList(collection);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link Set} of {@link String} from the specified {@code T} array.
	 * <p>
	 * @param <T>   the component type of the array
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
	 * @param <T>   the component type of the array
	 * @param array a {@code T} array
	 * <p>
	 * @return a {@link Set} of {@link String} from the specified {@code T} array
	 */
	public static <T> Set<String> asSet(final T... array) {
		return toSet(array);
	}

	/**
	 * Returns a {@link Set} of {@link String} from the specified {@link Collection} of type
	 * {@code T}.
	 * <p>
	 * @param <T>        the type of the {@link Collection} to convert
	 * @param collection a {@link Collection} of type {@code T}
	 * <p>
	 * @return a {@link Set} of {@link String} from the specified {@link Collection} of type
	 *         {@code T}
	 */
	public static <T> Set<String> collectionToSet(final Collection<T> collection) {
		return PARSER.callCollectionToSet(collection);
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

	/**
	 * Returns a representative {@link String} of the specified array of {@link Object}.
	 * <p>
	 * @param array an array of {@link Object}
	 * <p>
	 * @return a representative {@link String} of the specified array of {@link Object}
	 */
	public static String join(final Object... array) {
		return joinWith(array, EMPTY);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} of
	 * {@link Object}.
	 * <p>
	 * @param collection a {@link Collection} of {@link Object}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} of {@link Object}
	 */
	public static String join(final Collection<?> collection) {
		return joinWith(collection, EMPTY);
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
	 * Returns a representative {@link String} of the specified {@link Collection} of {@link Object}
	 * joined by {@code delimiter}.
	 * <p>
	 * @param collection a {@link Collection} of {@link Object}
	 * @param delimiter  the delimiting {@code char} value
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} of {@link Object}
	 *         joined by {@code delimiter}
	 */
	public static String joinWith(final Collection<?> collection, final char delimiter) {
		return joinWith(collection, toString(delimiter));
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} of {@link Object}
	 * joined by {@code delimiter}.
	 * <p>
	 * @param collection a {@link Collection} of {@link Object}
	 * @param delimiter  the delimiting {@link String}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} of {@link Object}
	 *         joined by {@code delimiter}
	 */
	public static String joinWith(final Collection<?> collection, final String delimiter) {
		// Check the arguments
		Arguments.requireNonNull(collection);

		// Initialize
		final StringBuilder builder = createBuilder(collection.size() * delimiter.length());
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
	 * Returns a representative {@link String} of the specified {@link Collection} of {@link Object}
	 * joined by {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param collection a {@link Collection} of {@link Object}
	 * @param delimiter  the delimiting {@code char} value
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} of {@link Object}
	 *         joined by {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String joinWith(final Collection<?> collection, final char delimiter,
			final ObjectToStringMapper wrapper) {
		return joinWith(collection, toString(delimiter), wrapper);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Collection} of {@link Object}
	 * joined by {@code delimiter} and wrapped by {@code wrapper}.
	 * <p>
	 * @param collection a {@link Collection} of {@link Object}
	 * @param delimiter  the delimiting {@link String}
	 * @param wrapper    an {@link ObjectToStringMapper}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Collection} of {@link Object}
	 *         joined by {@code delimiter} and wrapped by {@code wrapper}
	 */
	public static String joinWith(final Collection<?> collection, final String delimiter,
			final ObjectToStringMapper wrapper) {
		// Check the arguments
		Arguments.requireNonNull(collection);

		// Initialize
		final StringBuilder builder = createBuilder(collection.size() * delimiter.length());
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

	public static String repeat(final String text, final int repeat) {
		// Check the arguments
		Arguments.requireNonNull(text);
		IntegerArguments.requireNonNegative(repeat);

		// Initialize
		final StringBuilder builder = createBuilder(repeat);

		// Repeat the text
		for (int i = 0; i < repeat; ++i) {
			builder.append(text);
		}
		return builder.toString();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by reversing the specified {@link String}.
	 * <p>
	 * @param text a {@link String}
	 * <p>
	 * @return the {@link String} constructed by reversing the specified {@link String}
	 */
	public static String reverse(final String text) {
		if (text == null) {
			return null;
		}
		return createBuilder(text.length()).append(text).reverse().toString();
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
		IntegerArguments.requireNonNegative(index);

		// Remove the character
		if (text == null) {
			return null;
		}
		return text.substring(0, index) + text.substring(index + 1);
	}

	/**
	 * Returns the {@link String} constructed by removing the characters at the specified indexes in
	 * the specified {@link String}.
	 * <p>
	 * @param text    a {@link String}
	 * @param indexes the indexes of the characters to remove
	 * <p>
	 * @return the {@link String} constructed by removing the characters at the specified indexes in
	 *         the specified {@link String}
	 */
	public static String remove(final String text, final int[] indexes) {
		// Check the arguments
		ArrayArguments.requireMaxLength(indexes.length, text.length());

		// Remove the characters
		if (text == null) {
			return null;
		}
		if (indexes.length == 0) {
			return text;
		}
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
	 * Returns the {@link String} constructed by removing all the specified tokens from the
	 * specified {@link String}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the tokens to remove
	 * <p>
	 * @return the {@link String} constructed by removing all the specified tokens from the
	 *         specified {@link String}
	 */
	public static String removeAll(final String text, final String tokens) {
		if (text == null) {
			return null;
		}
		return replaceAll(text, bracketize(tokens), EMPTY);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the specified {@link Collection} of {@link String} without the empty {@link String}.
	 * <p>
	 * @param <C>        the type extending {@link Collection}
	 * @param collection a {@link Collection} of {@link String}
	 * <p>
	 * @return the specified {@link Collection} of {@link String} without the empty {@link String}
	 */
	public static <C extends Collection<String>> C removeEmpty(final C collection) {
		return Collections.<C, String>removeAll(collection, EMPTY);
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
		IntegerArguments.requireNonNegative(index);

		// Replace the character
		if (text == null) {
			return null;
		}
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
		ArrayArguments.requireMaxLength(indexes.length, text.length());
		ArrayArguments.requireSameLength(indexes.length, replacements.length);

		// Replace the characters
		if (text == null) {
			return null;
		}
		if (indexes.length == 0) {
			return text;
		}
		final StringBuilder builder = createBuilder(text.length()).append(text);
		for (int i = 0; i < indexes.length; ++i) {
			builder.setCharAt(indexes[i], replacements[i]);
		}
		return builder.toString();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link String} constructed by replacing all matching subsequence in the specified
	 * {@link String} by the specified replacement {@link String}, substituting captured
	 * subsequences as needed where the characters identified by the specified regular expression.
	 * <p>
	 * @param text        a {@link String}
	 * @param regex       the regular expression {@link String} to identify and replace
	 * @param replacement the {@link String} to replace by
	 * <p>
	 * @return the {@link String} constructed by replacing all matching subsequence in the specified
	 *         {@link String} by the specified replacement {@link String}, substituting captured
	 *         subsequences as needed where the characters identified by the specified regular
	 *         expression
	 */
	public static String replaceAll(final String text, final String regex,
			final String replacement) {
		if (text == null) {
			return null;
		}
		return text.replaceAll(regex, replacement);
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
		return wrapper + toString(content) + wrapper;
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
		return left + toString(content) + right;
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
	 * Returns the index of the first token of {@code text} that is in {@code tokens}, or {@code -1}
	 * if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@code char} array to find
	 * <p>
	 * @return the index of the first token of {@code text} that is in {@code tokens}, or {@code -1}
	 *         if there is no such occurrence
	 */
	public static int findFirst(final String text, final char[] tokens) {
		return findFirst(text, tokens, 0);
	}

	/**
	 * Returns the index of the first token of {@code text} that is in {@code tokens}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@code char} array to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first token of {@code text} that is in {@code tokens}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirst(final String text, final char[] tokens, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the first token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int index = fromIndex; index < text.length(); ++index) {
				if (Characters.contains(tokens, text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the first token of {@code text} that is in {@code tokens}, or {@code -1}
	 * if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of {@link Character} to find
	 * <p>
	 * @return the index of the first token of {@code text} that is in {@code tokens}, or {@code -1}
	 *         if there is no such occurrence
	 */
	public static int findFirst(final String text, final Collection<Character> tokens) {
		return findFirst(text, tokens, 0);
	}

	/**
	 * Returns the index of the first token of {@code text} that is in {@code tokens}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of {@link Character} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first token of {@code text} that is in {@code tokens}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirst(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the first token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int index = fromIndex; index < text.length(); ++index) {
				if (tokens.contains(text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of {@link String} containing the first token of {@code text} that
	 * is in {@code tokens}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of {@link String} to find
	 * <p>
	 * @return the {@link Index} of {@link String} containing the first token of {@code text} that
	 *         is in {@code tokens}, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findFirstString(final String text, final String[] tokens) {
		return findFirstString(text, tokens, 0);
	}

	/**
	 * Returns the {@link Index} of {@link String} containing the first token of {@code text} that
	 * is in {@code tokens}, seeking forward from {@code fromIndex}, or {@code null} if there is no
	 * such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the array of {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the {@link Index} of {@link String} containing the first token of {@code text} that
	 *         is in {@code tokens}, seeking forward from {@code fromIndex}, or {@code null} if
	 *         there is no such occurrence
	 */
	public static Index<String> findFirstString(final String text, final String[] tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Initialize
		Index<String> indexAndToken = null;

		// Find the first token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (final String token : tokens) {
				final int index = text.indexOf(token, fromIndex);
				if (index >= 0 && (indexAndToken == null || index < indexAndToken.getIndex())) {
					indexAndToken = new Index<String>(index, token);
				}
			}
		}
		return indexAndToken;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of {@link String} containing the first token of {@code text} that
	 * is in {@code tokens}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of {@link String} to find
	 * <p>
	 * @return the {@link Index} of {@link String} containing the first token of {@code text} that
	 *         is in {@code tokens}, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findFirstString(final String text,
			final Collection<String> tokens) {
		return findFirstString(text, tokens, 0);
	}

	/**
	 * Returns the {@link Index} of {@link String} containing the first token of {@code text} that
	 * is in {@code tokens}, seeking forward from {@code fromIndex}, or {@code null} if there is no
	 * such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the {@link Index} of {@link String} containing the first token of {@code text} that
	 *         is in {@code tokens}, seeking forward from {@code fromIndex}, or {@code null} if
	 *         there is no such occurrence
	 */
	public static Index<String> findFirstString(final String text, final Collection<String> tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Initialize
		Index<String> indexAndToken = null;

		// Find the first token
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
	 * Returns the index of the last token of {@code text} that is in {@code tokens}, or {@code -1}
	 * if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@code char} array to find
	 * <p>
	 * @return the index of the last token of {@code text} that is in {@code tokens}, or {@code -1}
	 *         if there is no such occurrence
	 */
	public static int findLast(final String text, final char[] tokens) {
		return findLast(text, tokens, text.length() - 1);
	}

	/**
	 * Returns the index of the last token of {@code text} that is in {@code tokens}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@code char} array to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last token of {@code text} that is in {@code tokens}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String text, final char[] tokens, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the last token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int index = fromIndex; index >= 0; --index) {
				if (Characters.contains(tokens, text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the last token of {@code text} that is in {@code tokens}, or {@code -1}
	 * if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of {@link Character} to find
	 * <p>
	 * @return the index of the last token of {@code text} that is in {@code tokens}, or {@code -1}
	 *         if there is no such occurrence
	 */
	public static int findLast(final String text, final Collection<Character> tokens) {
		return findLast(text, tokens, text.length() - 1);
	}

	/**
	 * Returns the index of the last token of {@code text} that is in {@code tokens}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of {@link Character} to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last token of {@code text} that is in {@code tokens}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLast(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the last token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int index = fromIndex; index >= 0; --index) {
				if (tokens.contains(text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of {@link String} containing the last token of {@code text} that is
	 * in {@code tokens}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of {@link String} to find
	 * <p>
	 * @return the {@link Index} of {@link String} containing the last token of {@code text} that is
	 *         in {@code tokens}, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final String[] tokens) {
		return findLastString(text, tokens, text.length() - 1);
	}

	/**
	 * Returns the {@link Index} of {@link String} containing the last token of {@code text} that is
	 * in {@code tokens}, seeking backward from {@code fromIndex}, or {@code null} if there is no
	 * such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the array of {@link String} to find
	 * @param fromIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link Index} of {@link String} containing the last token of {@code text} that is
	 *         in {@code tokens}, seeking backward from {@code fromIndex}, or {@code null} if there
	 *         is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final String[] tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Initialize
		Index<String> indexAndToken = null;

		// Find the last token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (final String token : tokens) {
				final int index = text.lastIndexOf(token, fromIndex);
				if (index >= 0 && (indexAndToken == null || index > indexAndToken.getIndex())) {
					indexAndToken = new Index<String>(index, token);
				}
			}
		}
		return indexAndToken;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of {@link String} containing the last token of {@code text} that is
	 * in {@code tokens}, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of {@link String} to find
	 * <p>
	 * @return the {@link Index} of {@link String} containing the last token of {@code text} that is
	 *         in {@code tokens}, or {@code null} if there is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final Collection<String> tokens) {
		return findLastString(text, tokens, text.length() - 1);
	}

	/**
	 * Returns the {@link Index} of {@link String} containing the last token of {@code text} that is
	 * in {@code tokens}, seeking backward from {@code fromIndex}, or {@code null} if there is no
	 * such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of {@link String} to find
	 * @param fromIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link Index} of {@link String} containing the last token of {@code text} that is
	 *         in {@code tokens}, seeking backward from {@code fromIndex}, or {@code null} if there
	 *         is no such occurrence
	 */
	public static Index<String> findLastString(final String text, final Collection<String> tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Initialize
		Index<String> indexAndToken = null;

		// Find the last token
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
	 * Returns the index of the first token of {@code text} that is not equal to {@code token}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@code char} value to find
	 * <p>
	 * @return the index of the first token of {@code text} that is not equal to {@code token}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotEqualTo(final String text, final char token) {
		return findFirstNotEqualTo(text, token, 0);
	}

	/**
	 * Returns the index of the first token of {@code text} that is not equal to {@code token},
	 * seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@code char} value to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first token of {@code text} that is not equal to {@code token},
	 *         seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotEqualTo(final String text, final char token,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(token);

		// Find the first token
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
	 * Returns the index of the first token of {@code text} that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@code char} array to find
	 * <p>
	 * @return the index of the first token of {@code text} that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final char[] tokens) {
		return findFirstNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first token of {@code text} that is not in {@code tokens}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@code char} array to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first token of {@code text} that is not in {@code tokens}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final char[] tokens, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the first token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int index = fromIndex; index < text.length(); ++index) {
				if (!Characters.contains(tokens, text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the first token of {@code text} that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of {@link Character} to find
	 * <p>
	 * @return the index of the first token of {@code text} that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final Collection<Character> tokens) {
		return findFirstNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first token of {@code text} that is not in {@code tokens}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of {@link Character} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first token of {@code text} that is not in {@code tokens}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstNotIn(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the first token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int index = fromIndex; index < text.length(); ++index) {
				if (!tokens.contains(text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the first token of {@code text} that is not equal to {@code token}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@link String} to find
	 * <p>
	 * @return the index of the first token of {@code text} that is not equal to {@code token}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotEqualTo(final String text, final String token) {
		return findFirstStringNotEqualTo(text, token, 0);
	}

	/**
	 * Returns the index of the first token of {@code text} that is not equal to {@code token},
	 * seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first token of {@code text} that is not equal to {@code token},
	 *         seeking forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotEqualTo(final String text, final String token,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(token);

		// Find the first token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int index = fromIndex; index < text.length(); index += token.length()) {
				if (!isToken(text, index, token)) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the first token of {@code text} that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of {@link String} to find
	 * <p>
	 * @return the index of the first token of {@code text} that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final String[] tokens) {
		return findFirstStringNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first token of {@code text} that is not in {@code tokens}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the array of {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first token of {@code text} that is not in {@code tokens}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final String[] tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the first token
		if (fromIndex >= 0 && fromIndex < text.length()) {
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

	//////////////////////////////////////////////

	/**
	 * Returns the index of the first token of {@code text} that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link List} of {@link String} to find
	 * <p>
	 * @return the index of the first token of {@code text} that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final List<String> tokens) {
		return findFirstStringNotIn(text, tokens, 0);
	}

	/**
	 * Returns the index of the first token of {@code text} that is not in {@code tokens}, seeking
	 * forward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link List} of {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the index of the first token of {@code text} that is not in {@code tokens}, seeking
	 *         forward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findFirstStringNotIn(final String text, final List<String> tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the first token
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
	 * Returns the index of the last token of {@code text} that is not equal to {@code token}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@code char} value to find
	 * <p>
	 * @return the index of the last token of {@code text} that is not equal to {@code token}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastNotEqualTo(final String text, final char token) {
		return findLastNotEqualTo(text, token, text.length() - 1);
	}

	/**
	 * Returns the index of the last token of {@code text} that is not equal to {@code token},
	 * seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@code char} value to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last token of {@code text} that is not equal to {@code token},
	 *         seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotEqualTo(final String text, final char token, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(token);

		// Find the last token
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
	 * Returns the index of the last token of {@code text} that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@code char} array to find
	 * <p>
	 * @return the index of the last token of {@code text} that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final char[] tokens) {
		return findLastNotIn(text, tokens, text.length() - 1);
	}

	/**
	 * Returns the index of the last token of {@code text} that is not in {@code tokens}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@code char} array to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last token of {@code text} that is not in {@code tokens}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final char[] tokens, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the last token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int index = fromIndex; index >= 0; --index) {
				if (!Characters.contains(tokens, text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the last token of {@code text} that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of {@link Character} to find
	 * <p>
	 * @return the index of the last token of {@code text} that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final Collection<Character> tokens) {
		return findLastNotIn(text, tokens, text.length() - 1);
	}

	/**
	 * Returns the index of the last token of {@code text} that is not in {@code tokens}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of {@link Character} to find
	 * @param fromIndex the index to start seeking backward from (inclusive)
	 * <p>
	 * @return the index of the last token of {@code text} that is not in {@code tokens}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastNotIn(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the last token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int index = fromIndex; index >= 0; --index) {
				if (!tokens.contains(text.charAt(index))) {
					return index;
				}
			}
		}
		return -1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the last token of {@code text} that is not equal to {@code token}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@link String} to find
	 * <p>
	 * @return the index of the last token of {@code text} that is not equal to {@code token}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotEqualTo(final String text, final String token) {
		return findLastStringNotEqualTo(text, token, text.length() - 1);
	}

	/**
	 * Returns the index of the last token of {@code text} that is not equal to {@code token},
	 * seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@link String} to find
	 * @param fromIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the index of the last token of {@code text} that is not equal to {@code token},
	 *         seeking backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotEqualTo(final String text, final String token,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(token);

		// Find the last token
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int index = fromIndex; index >= 0; index -= token.length()) {
				if (!isTokenTo(text, index, token)) {
					return index;
				}
			}
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the index of the last token of {@code text} that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of {@link String} to find
	 * <p>
	 * @return the index of the last token of {@code text} that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final String[] tokens) {
		return findLastStringNotIn(text, tokens, text.length() - 1);
	}

	/**
	 * Returns the index of the last token of {@code text} that is not in {@code tokens}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the array of {@link String} to find
	 * @param fromIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the index of the last token of {@code text} that is not in {@code tokens}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final String[] tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the last token
		if (fromIndex >= 0 && fromIndex < text.length()) {
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

	//////////////////////////////////////////////

	/**
	 * Returns the index of the last token of {@code text} that is not in {@code tokens}, or
	 * {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link List} of {@link String} to find
	 * <p>
	 * @return the index of the last token of {@code text} that is not in {@code tokens}, or
	 *         {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final List<String> tokens) {
		return findLastStringNotIn(text, tokens, text.length() - 1);
	}

	/**
	 * Returns the index of the last token of {@code text} that is not in {@code tokens}, seeking
	 * backward from {@code fromIndex}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link List} of {@link String} to find
	 * @param fromIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the index of the last token of {@code text} that is not in {@code tokens}, seeking
	 *         backward from {@code fromIndex}, or {@code -1} if there is no such occurrence
	 */
	public static int findLastStringNotIn(final String text, final List<String> tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Find the last token
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
	 * @param token the {@code char} value to find
	 * <p>
	 * @return the indexes of {@code token} in {@code text}
	 */
	public static List<Integer> getIndexes(final String text, final char token) {
		return getIndexes(text, token, 0);
	}

	/**
	 * Returns the indexes of {@code token} in {@code text}, seeking forward from {@code fromIndex}.
	 * <p>
	 * @param text      a {@link String}
	 * @param token     the {@code char} value to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of {@code token} in {@code text}, seeking forward from {@code fromIndex}
	 */
	public static List<Integer> getIndexes(final String text, final char token,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(token);

		// Initialize
		final List<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
	 * @param token   the {@code char} value to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of {@code token} in {@code text}, seeking forward to {@code toIndex}
	 */
	public static List<Integer> getIndexesTo(final String text, final char token,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(token);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, text.length());

		// Initialize
		final List<Integer> indexes = new ExtendedLinkedList<Integer>();
		int index = text.indexOf(token);

		// Get the indexes
		while (index >= 0 && index < toIndex) {
			indexes.add(index);
			index = text.indexOf(token, index + 1);
		}
		return indexes;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the indexes of {@code tokens} in {@code text}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@code char} array to find
	 * <p>
	 * @return the indexes of {@code tokens} in {@code text}
	 */
	public static List<Integer> getIndexes(final String text, final char[] tokens) {
		return getIndexes(text, tokens, 0);
	}

	/**
	 * Returns the indexes of the tokens of {@code text} that are in {@code tokens}, seeking forward
	 * from {@code fromIndex}.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@code char} array to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of the tokens of {@code text} that are in {@code tokens}, seeking forward
	 *         from {@code fromIndex}
	 */
	public static List<Integer> getIndexes(final String text, final char[] tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Initialize
		final List<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
	 * Returns the indexes of the tokens of {@code text} that are in {@code tokens}, seeking forward
	 * to {@code toIndex}.
	 * <p>
	 * @param text    a {@link String}
	 * @param tokens  the {@code char} array to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of the tokens of {@code text} that are in {@code tokens}, seeking forward
	 *         to {@code toIndex}
	 */
	public static List<Integer> getIndexesTo(final String text, final char[] tokens,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, text.length());

		// Initialize
		final List<Integer> indexes = new ExtendedLinkedList<Integer>();
		final char[] array = text.toCharArray();

		// Get the indexes
		for (int index = 0; index < toIndex; ++index) {
			if (Characters.contains(tokens, array[index])) {
				indexes.add(index);
			}
		}
		return indexes;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the indexes of {@code tokens} in {@code text}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of {@link Character} to find
	 * <p>
	 * @return the indexes of {@code tokens} in {@code text}
	 */
	public static List<Integer> getIndexes(final String text, final Collection<Character> tokens) {
		return getIndexes(text, tokens, 0);
	}

	/**
	 * Returns the indexes of the tokens of {@code text} that are in {@code tokens}, seeking forward
	 * from {@code fromIndex}.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of {@link Character} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes of the tokens of {@code text} that are in {@code tokens}, seeking forward
	 *         from {@code fromIndex}
	 */
	public static List<Integer> getIndexes(final String text, final Collection<Character> tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Initialize
		final List<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
	 * Returns the indexes of the tokens of {@code text} that are in {@code tokens}, seeking forward
	 * to {@code toIndex}.
	 * <p>
	 * @param text    a {@link String}
	 * @param tokens  the {@link Collection} of {@link Character} to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes of the tokens of {@code text} that are in {@code tokens}, seeking forward
	 *         to {@code toIndex}
	 */
	public static List<Integer> getIndexesTo(final String text, final Collection<Character> tokens,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, text.length());

		// Initialize
		final List<Integer> indexes = new ExtendedLinkedList<Integer>();
		final char[] array = text.toCharArray();

		// Get the indexes
		for (int index = 0; index < toIndex; ++index) {
			if (tokens.contains(array[index])) {
				indexes.add(index);
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
	public static List<Integer> getStringIndexes(final String text, final String token) {
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
	public static List<Integer> getStringIndexes(final String text, final String token,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(token);

		// Initialize
		final List<Integer> indexes = new ExtendedLinkedList<Integer>();

		// Get the indexes
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
	public static List<Integer> getStringIndexesTo(final String text, final String token,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(token);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, text.length());

		// Initialize
		final List<Integer> indexes = new ExtendedLinkedList<Integer>();
		int index = text.indexOf(token);

		// Get the indexes
		while (index >= 0 && index < toIndex) {
			indexes.add(index);
			index = text.indexOf(token, index + 1);
		}
		return indexes;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the indexes and {@code tokens} in {@code text}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of {@link String} to find
	 * <p>
	 * @return the indexes and {@code tokens} in {@code text}
	 */
	public static List<Index<String>> getStringIndexes(final String text, final String[] tokens) {
		return getStringIndexes(text, tokens, 0);
	}

	/**
	 * Returns the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward
	 * from {@code fromIndex}.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the array of {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward
	 *         from {@code fromIndex}
	 */
	public static List<Index<String>> getStringIndexes(final String text, final String[] tokens,
			final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Initialize
		final List<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (final String token : tokens) {
				final List<Integer> tokenIndexes = getStringIndexes(text, token, fromIndex);
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
	 * @param tokens  the array of {@link String} to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward to
	 *         {@code toIndex}
	 */
	public static List<Index<String>> getStringIndexesTo(final String text, final String[] tokens,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, text.length());

		// Initialize
		final List<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		for (final String token : tokens) {
			final List<Integer> tokenIndexes = getStringIndexesTo(text, token, toIndex);
			for (final int tokenIndex : tokenIndexes) {
				indexes.add(new Index<String>(tokenIndex, token));
			}
		}
		return indexes;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the indexes and {@code tokens} in {@code text}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@link Collection} of {@link String} to find
	 * <p>
	 * @return the indexes and {@code tokens} in {@code text}
	 */
	public static List<Index<String>> getStringIndexes(final String text,
			final Collection<String> tokens) {
		return getStringIndexes(text, tokens, 0);
	}

	/**
	 * Returns the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward
	 * from {@code fromIndex}.
	 * <p>
	 * @param text      a {@link String}
	 * @param tokens    the {@link Collection} of {@link String} to find
	 * @param fromIndex the index to start seeking forward from (inclusive)
	 * <p>
	 * @return the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward
	 *         from {@code fromIndex}
	 */
	public static List<Index<String>> getStringIndexes(final String text,
			final Collection<String> tokens, final int fromIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);

		// Initialize
		final List<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (final String token : tokens) {
				final List<Integer> tokenIndexes = getStringIndexes(text, token, fromIndex);
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
	 * @param tokens  the {@link Collection} of {@link String} to find
	 * @param toIndex the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the indexes and tokens of {@code text} that are in {@code tokens}, seeking forward to
	 *         {@code toIndex}
	 */
	public static List<Index<String>> getStringIndexesTo(final String text,
			final Collection<String> tokens, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(tokens);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, text.length());

		// Initialize
		final List<Index<String>> indexes = new SortedList<Index<String>>();

		// Get the indexes
		for (final String token : tokens) {
			final List<Integer> tokenIndexes = getStringIndexesTo(text, token, toIndex);
			for (final int tokenIndex : tokenIndexes) {
				indexes.add(new Index<String>(tokenIndex, token));
			}
		}
		return indexes;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final Comparator<Index<String>> STRING_INDEX_COMPARATOR = new Comparator<Index<String>>() {
		public int compare(final Index<String> a, final Index<String> b) {
			return Integers.compare(a.getIndex(), b.getIndex());
		}
	};

	/**
	 * Sorts the specified {@link List} of {@link Index} of {@link String}.
	 * <p>
	 * @param indexes a {@link List} of {@link Index} of {@link String}
	 */
	public static void sortStringIndexes(final List<Index<String>> indexes) {
		Lists.sort(indexes, STRING_INDEX_COMPARATOR);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiterIndexes}.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes an {@code int} array
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiterIndexes}
	 */
	public static List<String> getTokens(final String text, final int[] delimiterIndexes) {
		return getTokensTo(text, delimiterIndexes, text.length());
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiterIndexes}.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes an {@code int} array
	 * @param toIndex          the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiterIndexes}
	 */
	public static List<String> getTokensTo(final String text, final int[] delimiterIndexes,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(delimiterIndexes);

		// Initialize
		final List<String> tokens = new ExtendedLinkedList<String>();
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

	//////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiterIndexes}.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes a {@link Collection} of {@link Integer}
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiterIndexes}
	 */
	public static List<String> getTokens(final String text,
			final Collection<Integer> delimiterIndexes) {
		return getTokensTo(text, delimiterIndexes, text.length());
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiterIndexes}.
	 * <p>
	 * @param text             a {@link String}
	 * @param delimiterIndexes a {@link Collection} of {@link Integer}
	 * @param toIndex          the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiterIndexes}
	 */
	public static List<String> getTokensTo(final String text,
			final Collection<Integer> delimiterIndexes, final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(delimiterIndexes);

		// Initialize
		final List<String> tokens = new ExtendedLinkedList<String>();
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
		if (fromIndex >= 0 && fromIndex < text.length()) {
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
		if (fromIndex >= 0 && fromIndex < text.length()) {
			for (int i = 0; i < tokens.size(); ++i) {
				if (isToken(text, fromIndex, tokens.get(i))) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int getTokenTo(final String text, final int toIndex, final List<String> tokens) {
		for (int i = 0; i < tokens.size(); ++i) {
			if (isToken(text, toIndex - tokens.get(i).length(), tokens.get(i))) {
				return i;
			}
		}
		return -1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SPLITTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiter}.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@code char} value
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiter}
	 */
	public static List<String> split(final String text, final char delimiter) {
		return splitTo(text, delimiter, text.length());
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiter}.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@code char} value
	 * @param toIndex   the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiter}
	 */
	public static List<String> splitTo(final String text, final char delimiter, final int toIndex) {
		return getTokensTo(text, getIndexesTo(text, delimiter, toIndex), toIndex);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiters}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the array of delimiting {@code char} values
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiters}
	 */
	public static List<String> split(final String text, final char[] delimiters) {
		return splitTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiters} to {@code toIndex}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the array of delimiting {@code char} values
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiters} to {@code toIndex}
	 */
	public static List<String> splitTo(final String text, final char[] delimiters,
			final int toIndex) {
		return getTokensTo(text, getIndexesTo(text, delimiters, toIndex), toIndex);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiters}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link Collection} of delimiting {@link Character}
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiters}
	 */
	public static List<String> split(final String text, final Collection<Character> delimiters) {
		return splitTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiters} to {@code toIndex}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link Collection} of delimiting {@link Character}
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiters} to {@code toIndex}
	 */
	public static List<String> splitTo(final String text, final Collection<Character> delimiters,
			final int toIndex) {
		return getTokensTo(text, getIndexesTo(text, delimiters, toIndex), toIndex);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiter}.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@link String}
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiter}
	 */
	public static List<String> splitString(final String text, final String delimiter) {
		return splitStringTo(text, delimiter, text.length());
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiter}.
	 * <p>
	 * @param text      a {@link String}
	 * @param delimiter the delimiting {@link String}
	 * @param toIndex   the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiter}
	 */
	public static List<String> splitStringTo(final String text, final String delimiter,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(delimiter);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, text.length());

		// Initialize
		final List<String> tokens = new ExtendedLinkedList<String>();
		final List<Integer> delimiterIndexes = getStringIndexesTo(text, delimiter, toIndex);
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
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiters}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the array of delimiting {@link String}
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiters}
	 */
	public static List<String> splitString(final String text, final String[] delimiters) {
		return splitStringTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiters} to {@code toIndex}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the array of delimiting {@link String}
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiters} to {@code toIndex}
	 */
	public static List<String> splitStringTo(final String text, final String[] delimiters,
			final int toIndex) {
		// Check the arguments
		Arguments.requireNonNull(text);
		Arguments.requireNonNull(delimiters);
		IntegerArguments.requireNonNegative(toIndex);
		IntegerArguments.requireLessOrEqualTo(toIndex, text.length());

		// Initialize
		final List<String> tokens = new ExtendedLinkedList<String>();
		final List<Index<String>> delimiterIndexes = getStringIndexesTo(text, delimiters, toIndex);
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
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiters}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link List} of delimiting {@link String}
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiters}
	 */
	public static List<String> splitString(final String text, final List<String> delimiters) {
		return splitStringTo(text, delimiters, text.length());
	}

	/**
	 * Returns the {@link List} of {@link String} computed by splitting {@code text} around
	 * {@code delimiters} to {@code toIndex}.
	 * <p>
	 * @param text       a {@link String}
	 * @param delimiters the {@link List} of delimiting {@link String}
	 * @param toIndex    the index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link List} of {@link String} computed by splitting {@code text} around
	 *         {@code delimiters} to {@code toIndex}
	 */
	public static List<String> splitStringTo(final String text, final List<String> delimiters,
			final int toIndex) {
		// Initialize
		final List<String> tokens = new ExtendedLinkedList<String>();
		final List<Index<String>> delimiterIndexes = getStringIndexesTo(text, delimiters, toIndex);
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

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code text} is {@code null} or empty.
	 * <p>
	 * @param text the {@link String} to test
	 * <p>
	 * @return {@code true} if {@code text} is {@code null} or empty, {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(final String text) {
		return text == null || text.length() == 0;
	}

	/**
	 * Tests whether {@code text} is not {@code null} and empty.
	 * <p>
	 * @param text the {@link String} to test
	 * <p>
	 * @return {@code true} if {@code text} is not {@code null} and empty, {@code false} otherwise
	 */
	public static boolean isEmpty(final String text) {
		return text != null && text.length() == 0;
	}

	/**
	 * Tests whether {@code text} is not {@code null} and not empty.
	 * <p>
	 * @param text the {@link String} to test
	 * <p>
	 * @return {@code true} if {@code text} is not {@code null} and not empty, {@code false}
	 *         otherwise
	 */
	public static boolean isNotEmpty(final String text) {
		return text != null && text.length() > 0;
	}

	/**
	 * Tests whether {@code text} is numeric.
	 * <p>
	 * @param text the {@link String} to test
	 * <p>
	 * @return {@code true} if {@code text} is numeric, {@code false} otherwise
	 */
	public static boolean isNumeric(final String text) {
		if (text == null) {
			return false;
		}
		final NumberFormat formatter = NumberFormat.getInstance();
		final ParsePosition position = new ParsePosition(0);
		formatter.parse(text, position);
		return text.length() == position.getIndex();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean isToken(final String text, final int index, final String token) {
		return index >= 0 && text.substring(index, index + token.length()).equals(token);
	}

	public static boolean isTokenTo(final String text, final int toIndex, final String token) {
		return isToken(text, toIndex - token.length(), token);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code text} contains {@code token}.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@code char} value to test for presence
	 * <p>
	 * @return {@code true} if {@code text} contains {@code token}, {@code false} otherwise
	 */
	public static boolean contains(final String text, final char token) {
		if (text == null) {
			return false;
		}
		return text.indexOf(token) >= 0;
	}

	/**
	 * Tests whether {@code text} contains any {@code tokens}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the {@code char} array to test for presence
	 * <p>
	 * @return {@code true} if {@code text} contains any {@code tokens}, {@code false} otherwise
	 */
	public static boolean containsAny(final String text, final char[] tokens) {
		if (text == null) {
			return false;
		}
		for (final char token : tokens) {
			if (text.indexOf(token) >= 0) {
				return true;
			}
		}
		return false;
	}

	//////////////////////////////////////////////

	/**
	 * Tests whether {@code text} contains {@code token}.
	 * <p>
	 * @param text  a {@link String}
	 * @param token the {@link String} to test for presence
	 * <p>
	 * @return {@code true} if {@code text} contains {@code token}, {@code false} otherwise
	 */
	public static boolean contains(final String text, final String token) {
		if (text == null) {
			return false;
		}
		return text.contains(token);
	}

	/**
	 * Tests whether {@code text} contains any {@code tokens}.
	 * <p>
	 * @param text   a {@link String}
	 * @param tokens the array of {@link String} to test for presence
	 * <p>
	 * @return {@code true} if {@code text} contains any {@code tokens}, {@code false} otherwise
	 */
	public static boolean containsAny(final String text, final String[] tokens) {
		if (text == null) {
			return false;
		}
		for (final String token : tokens) {
			if (text.contains(token)) {
				return true;
			}
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

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
		if (text == null) {
			return 0;
		}
		int lineCount;
		final Matcher matcher = Pattern.compile("\r\n|\r|\n").matcher(text);
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
	 * Tests whether {@code text} matches {@code expression}.
	 * <p>
	 * @param text       a {@link String}
	 * @param expression the expression {@link String} to test for presence
	 * <p>
	 * @return {@code true} if {@code text} matches {@code expression}, {@code false} otherwise
	 */
	public static boolean matches(final String text, final String expression) {
		if (text == null) {
			return false;
		}
		return text.matches(expression);
	}

	/**
	 * Tests whether {@code text} matches any {@code expressions}.
	 * <p>
	 * @param text        a {@link String}
	 * @param expressions the array of expression {@link String} to test for presence
	 * <p>
	 * @return {@code true} if {@code text} matches any {@code expressions}, {@code false} otherwise
	 */
	public static boolean matches(final String text, final String[] expressions) {
		if (text == null) {
			return false;
		}
		for (final String expression : expressions) {
			if (text.matches(expression)) {
				return true;
			}
		}
		return false;
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
	 * Returns the representative {@link String} of the specified {@link Object} if not
	 * {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return the representative {@link String} of the specified {@link Object} if not
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
			return formatNumber(object);
		}
		return String.valueOf(object);
	}

	/**
	 * Returns the representative {@link String} of the specified {@link Object} if not {@code null}
	 * or {@code "null"}, {@code null} otherwise.
	 * <p>
	 * @param object an {@link Object}
	 * <p>
	 * @return the representative {@link String} of the specified {@link Object} if not {@code null}
	 *         or {@code "null"}, {@code null} otherwise
	 */
	public static String toStringWithNull(final Object object) {
		final String value = toString(object);
		if (NULL.equals(value)) {
			return null;
		}
		return value;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the representative {@link String} of the specified {@link Object} if not
	 * {@code null}, {@code defaultString} otherwise.
	 * <p>
	 * @param object        the {@link Object}
	 * @param defaultString the {@link String} to return if {@code null}
	 * <p>
	 * @return the representative {@link String} of the specified {@link Object} if not
	 *         {@code null}, {@code defaultString} otherwise
	 */
	public static String toString(final Object object, final String defaultString) {
		return object != null ? toString(object) : defaultString;
	}

	/**
	 * Returns the representative {@link String} of the specified {@link Object} if not {@code null}
	 * or {@code "null"}, {@code defaultString} otherwise.
	 * <p>
	 * @param object        the {@link Object}
	 * @param defaultString the {@link String} to return if {@code null}
	 * <p>
	 * @return the representative {@link String} of the specified {@link Object} if not {@code null}
	 *         or {@code "null"}, {@code defaultString} otherwise
	 */
	public static String toStringWithNull(final Object object, final String defaultString) {
		final String value = toString(object);
		if (NULL.equals(value)) {
			return defaultString;
		}
		return value;
	}

	//////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of the specified {@link Exception} if not
	 * {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param exception an {@link Exception}
	 * <p>
	 * @return a representative {@link String} of the specified {@link Exception} if not
	 *         {@code null}, {@code "null"} otherwise
	 */
	public static String toString(final Exception exception) {
		return toString(exception, 0);
	}

	/**
	 * Returns a representative {@link String} of the specified {@link Exception} with the specified
	 * number of {@link StackTraceElement} if not {@code null}, {@code "null"} otherwise.
	 * <p>
	 * @param exception              an {@link Exception}
	 * @param stackTraceElementCount the number of {@link StackTraceElement} to add
	 * <p>
	 * @return a representative {@link String} of the specified {@link Exception} with the specified
	 *         number of {@link StackTraceElement} if not {@code null}, {@code "null"} otherwise
	 */
	public static String toString(final Exception exception, final int stackTraceElementCount) {
		if (exception != null) {
			if (stackTraceElementCount > 0) {
				final StackTraceElement[] stackTraces = Arrays.<StackTraceElement>take(
						exception.getStackTrace(), 0, stackTraceElementCount);
				return exception.getLocalizedMessage() + ":" + NEWLINE +
						joinWith(stackTraces, NEWLINE);
			}
			return exception.getLocalizedMessage();
		}
		return NULL;
	}
}
