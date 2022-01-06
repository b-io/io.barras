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
#ifdef __cplusplus
extern "C"
{
#endif
#ifndef _COMMON_STRING_H
#define _COMMON_STRING_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/CommonConstants.h"
#include "ceres/CommonMacros.h"
#include "ceres/CommonTypes.h"
#include "ceres/CommonStructures.h"
#include "ceres/CommonFunctions.h"
#include "ceres/CommonArrays.h"
#include "ceres/CommonVerifier.h"

#include "ceres/io/CommonIO.h"

#include "ceres/type/CommonType.h"

#include "ceres/iterable/CommonArray.h"

#include <string.h>


	/***********************************************************************************************
	 * CONSTRUCT
	 **********************************************************************************************/

	/**
	 * Constructs an array of characters dynamically.
	 * <p>
	 * @param size the size of the array of characters to be constructed
	 * <p>
	 * @return the constructed array of characters
	 */
	character* chars_new(const natural size);

	/**
	 * Constructs a string dynamically.
	 * <p>
	 * @return the constructed string
	 */
	character* string_new(void);


	/***********************************************************************************************
	 * RESET
	 **********************************************************************************************/

	/**
	 * Sets the first character of {@code string} to {@code _STRING_END}.
	 * <p>
	 * @param string the array of characters to be reset
	 * @param size   the size of the array of characters to be reset
	 */
	void chars_reset(character* string, const natural size);

	/**
	 * Sets the first character of {@code string} to {@code _STRING_END}.
	 * <p>
	 * @param string the string to be reset
	 */
	void string_reset(string string);

	/**********************************************************************************************/

	/**
	 * Sets all the characters of {@code string} to {@code _STRING_END}.
	 * <p>
	 * @param string the array of characters to be fully reset
	 * @param size   the size of the array of characters to be fully reset
	 */
	void chars_reset_all(character* string, const natural size);

	/**
	 * Sets all the characters of {@code string} to {@code _STRING_END}.
	 * <p>
	 * @param string the string to be fully reset
	 */
	void string_reset_all(string string);


	/***********************************************************************************************
	 * COMMON
	 **********************************************************************************************/

	/**
	 * Returns the length of {@code string}, or {@code 0} if {@code string} is
	 * {@code NULL}.
	 * <p>
	 * @param string the input array of characters
	 * @param size   the size of the input array of characters
	 * <p>
	 * @return the length of {@code string}, or {@code 0} if {@code string} is
	 *         {@code NULL}
	 */
	natural chars_length(const character* string, const natural size);

	/**
	 * Returns the length of {@code string}, or {@code 0} if {@code string} is
	 * {@code NULL}.
	 * <p>
	 * @param string the input string
	 * <p>
	 * @return the length of {@code string}, or {@code 0} if {@code string} is
	 *         {@code NULL}
	 */
	natural string_length(const string string);

	/**********************************************************************************************/

	/**
	 * Returns {@code _TRUE} if {@code string} is empty or {@code NULL},
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param string the input array of characters
	 * @param size   the size of the input array of characters
	 * <p>
	 * @return {@code _TRUE} if {@code string} is empty or {@code NULL},
	 *         {@code _FALSE} otherwise
	 */
	boolean chars_is_empty(const character* string, const natural size);

	/**
	 * Returns {@code _TRUE} if {@code string} is empty or {@code NULL},
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param string the input string
	 * <p>
	 * @return {@code _TRUE} if {@code string} is empty or {@code NULL},
	 *         {@code _FALSE} otherwise
	 */
	boolean string_is_empty(const string string);

	/**********************************************************************************************/

	/**
	 * Sets all the characters of {@code string} of size {@code size} to
	 * {@code character}.
	 * <p>
	 * Warning(s):
	 * - If the size of {@code string} is smaller than {@code size}, there is a
	 *   buffer overflow.
	 * <p>
	 * @param string the array of characters to be filled
	 * @param size   the size of the array of characters to be filled
	 * @param c      the character to fill with
	 */
	void chars_fill(character* string, const natural size, const character c);

	/**
	 * Sets all the characters of {@code string} of size
	 * {@code _STRING_SIZE} to {@code character}.
	 * <p>
	 * Warning(s):
	 * - If the size of {@code string} is smaller than {@code size}, there is a
	 *   buffer overflow.
	 * <p>
	 * @param string the string to be filled
	 * @param c      the character to fill with
	 */
	void string_fill(string string, const character c);

	/**********************************************************************************************/

	/**
	 * Sets all the characters of {@code string} of size {@code size} to
	 * {@code _STRING_END}.
	 * <p>
	 * Warning(s):
	 * - If the size of {@code string} is smaller than {@code size}, there is a
	 *   buffer overflow.
	 * <p>
	 * @param string the array of characters to be cleared
	 * @param size   the size of the array of characters to be cleared
	 */
	void chars_clear(character* string, const natural size);

	/**
	 * Sets all the characters of {@code string} (of size
	 * {@code _STRING_SIZE}) to {@code _STRING_END}.
	 * <p>
	 * @param string the string to be cleared
	 */
	void string_clear(string string);


	/***********************************************************************************************
	 * COPY
	 **********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target}.
	 * <p>
	 * @param source     the input array of characters
	 * @param sourceSize the size of the input array of characters
	 * @param length     the length of the copy
	 * @param target     the output array of characters
	 * @param targetSize the size of the output array
	 * <p>
	 * @return {@code _TRUE} if {@code source} has been fully copied into
	 *         {@code target}, {@code _FALSE} otherwise
	 */
	boolean chars_copy(const character* source, const natural sourceSize, const natural length, character* target, const natural targetSize);

	/**
	 * Copies {@code source} into {@code target}.
	 * <p>
	 * @param source     the input array of characters
	 * @param sourceSize the size of the input array of characters
	 * @param length     the length of the copy
	 * @param target     the output array of characters
	 * <p>
	 * @return {@code _TRUE} if {@code source} has been fully copied into
	 *         {@code target}, {@code _FALSE} otherwise
	 */
	boolean chars_copy_to_string(const character* source, const natural sourceSize, const natural length, character* target);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target}.
	 * <p>
	 * @param source the input array of characters
	 * @param length the length of the copy
	 * @param target the output array of characters
	 * <p>
	 * @return {@code _TRUE} if {@code source} has been fully copied into
	 *         {@code target}, {@code _FALSE} otherwise
	 */
	boolean string_copy(const character* source, const natural length, character* target);

	/**
	 * Copies {@code source} into {@code target}.
	 * <p>
	 * @param source     the input array of characters
	 * @param length     the length of the copy
	 * @param target     the output array of characters
	 * @param targetSize the size of the output array
	 * <p>
	 * @return {@code _TRUE} if {@code source} has been fully copied into
	 *         {@code target}, {@code _FALSE} otherwise
	 */
	boolean string_copy_to_chars(const character* source, const natural length, character* target, const natural targetSize);

	/**********************************************************************************************/

	/**
	 * Returns a new array of characters that is a substring of {@code string}.
	 * The substring begins at {@code startIndex}.
	 * <p>
	 * @param source     the input array of characters
	 * @param sourceSize the size of the input array of characters
	 * @param startIndex the starting index (inclusive)
	 * @param target     the output array of characters
	 * @param targetSize the size of the output array
	 */
	void chars_from(const character* source, const natural sourceSize, const natural startIndex, character* target, const natural targetSize);

	/**
	 * Returns a new string that is a substring of {@code string}. The substring
	 * begins at {@code startIndex}.
	 * <p>
	 * @param source     the input string
	 * @param startIndex the starting index (inclusive)
	 * @param target     the output string
	 */
	void string_from(const string source, const natural startIndex, string target);

	/**********************************************************************************************/

	/**
	 * Returns a new array of characters that is a substring of {@code string}.
	 * The substring begins at {@code startIndex} and extends to the character
	 * at index {@code min(endIndex - 1, size - 1)}.
	 * <p>
	 * @param source     the input array of characters
	 * @param sourceSize the size of the input array of characters
	 * @param startIndex the starting index (inclusive)
	 * @param endIndex   the ending index (exclusive)
	 * @param target     the output array of characters
	 * @param targetSize the size of the output array
	 */
	void chars_sub(const character* source, const natural sourceSize, const natural startIndex, const natural endIndex, character* target, const natural targetSize);

	/**
	 * Returns a new string that is a substring of {@code string}. The substring
	 * begins at {@code startIndex} and extends to the character at index
	 * {@code min(endIndex - 1, _STRING_SIZE - 1)}.
	 * <p>
	 * @param source     the input string
	 * @param startIndex the starting index (inclusive)
	 * @param endIndex   the ending index (exclusive)
	 * @param target     the output string
	 */
	void string_sub(const string source, const natural startIndex, const natural endIndex, string target);


	/***********************************************************************************************
	 * FIND
	 **********************************************************************************************/

	/**
	 * Returns {@code _TRUE} if {@code source} contains {@code c}, {@code _FALSE}
	 * otherwise.
	 * <p>
	 * @param source the input array of characters
	 * @param size   the size of the input array of characters
	 * @param c      the character to be searched
	 * <p>
	 * @return {@code _TRUE} if {@code source} contains {@code c}, {@code _FALSE}
	 *         otherwise
	 */
	boolean chars_contain(const character* source, const natural size, const character c);

	/**
	 * Returns {@code _TRUE} if {@code source} contains {@code c}, {@code _FALSE}
	 * otherwise.
	 * <p>
	 * @param source the input string
	 * @param c      the character to be searched
	 * <p>
	 * @return {@code _TRUE} if {@code source} contains {@code c}, {@code _FALSE}
	 *         otherwise
	 */
	boolean string_contain(const string source, const character c);

	/**********************************************************************************************/

	/**
	 * Returns the first character of {@code source} that is in {@code set}, or
	 * {@code NULL} if there is no such occurrence.
	 * <p>
	 * @param source the input array of characters
	 * @param size   the size of the input array of characters
	 * @param set    the set of characters to be searched
	 * <p>
	 * @return the first character of {@code source} that is in {@code set}, or
	 *         {@code NULL} if there is no such occurrence
	 */
	character* chars_find(character* source, const natural size, const string set);

	/**
	 * Returns the first character of {@code source} that is in {@code set}, or
	 * {@code NULL} if there is no such occurrence.
	 * <p>
	 * @param source the input string
	 * @param set    the set of characters to be searched
	 * <p>
	 * @return the first character of {@code source} that is in {@code set}, or
	 *         {@code NULL} if there is no such occurrence
	 */
	character* string_find(string source, const string set);

	/**********************************************************************************************/

	/**
	 * Returns the index of the first character of {@code source} that is in
	 * {@code set}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param source the input array of characters
	 * @param size   the size of the input array of characters
	 * @param set    the set of characters to be searched
	 * <p>
	 * @return the index of the first character of {@code source} that is in
	 *         {@code set}, or {@code -1} if there is no such occurrence
	 */
	natural chars_find_index(character* source, const natural size, const string set);

	/**
	 * Returns the index of the first character of {@code source} that is in
	 * {@code set}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param source the input string
	 * @param set    the set of characters to be searched
	 * <p>
	 * @return the index of the first character of {@code source} that is in
	 *         {@code set}, or {@code -1} if there is no such occurrence
	 */
	natural string_find_index(string source, const string set);

	/**********************************************************************************************/

	/**
	 * Returns the last character of {@code source} that is in {@code set}, or
	 * {@code NULL} if there is no such occurrence.
	 * <p>
	 * @param source the input array of characters
	 * @param size   the size of the input array of characters
	 * @param set    the set of characters to be searched
	 * <p>
	 * @return the last character of {@code source} that is in {@code set}, or
	 *         {@code NULL} if there is no such occurrence
	 */
	character* chars_find_last(character* source, const natural size, const string set);

	/**
	 * Returns the last character of {@code source} that is in {@code set}, or
	 * {@code NULL} if there is no such occurrence.
	 * <p>
	 * @param source the input string
	 * @param set    the set of characters to be searched
	 * <p>
	 * @return the last character of {@code source} that is in {@code set}, or
	 *         {@code NULL} if there is no such occurrence
	 */
	character* string_find_last(string source, const string set);

	/**********************************************************************************************/

	/**
	 * Returns the index of the last character of {@code source} that is in
	 * {@code set}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param source the input array of characters
	 * @param size   the size of the input array of characters
	 * @param set    the set of characters to be searched
	 * <p>
	 * @return the index of the last character of {@code source} that is in
	 *         {@code set}, or {@code -1} if there is no such occurrence
	 */
	natural chars_find_last_index(character* source, const natural size, const string set);

	/**
	 * Returns the index of the last character of {@code source} that is in
	 * {@code set}, or {@code -1} if there is no such occurrence.
	 * <p>
	 * @param source the input array of characters
	 * @param size   the size of the input array of characters
	 * @param set    the set of characters to be searched
	 * <p>
	 * @return the index of the last character of {@code source} that is in
	 *         {@code set}, or {@code -1} if there is no such occurrence
	 */
	natural string_find_last_index(string source, const string set);

	/**********************************************************************************************/

	/**
	 * Returns an Array containing all the characters of {@code source} that are
	 * in {@code set}.
	 * <p>
	 * @param source the input array of characters
	 * @param size   the size of the input array of characters
	 * @param set    the set of characters to be searched
	 * <p>
	 * @return an Array containing all the characters of {@code source} that are
	 *         in {@code set}
	 */
	Array* chars_find_all(character* source, const natural size, const string set);

	/**
	 * Returns an Array containing all the characters of {@code source} that are
	 * in {@code set}.
	 * <p>
	 * @param source the input string
	 * @param set    the set of characters to be searched
	 * <p>
	 * @return an Array containing all the characters of {@code source} that are
	 *         in {@code set}
	 */
	Array* string_find_all(string source, const string set);

	/**********************************************************************************************/

	/**
	 * Returns the first occurrence of {@code text} within {@code source}, or
	 * {@code NULL} if there is no such occurrence.
	 * <p>
	 * @param source     the input array of characters
	 * @param sourceSize the size of the input array of characters
	 * @param text       the array of characters to be searched
	 * @param textSize   the size of the array of characters to be searched
	 * <p>
	 * @return the first occurrence of {@code text} within {@code source}, or
	 *         {@code NULL} if there is no such occurrence
	 */
	character* chars_find_chars(character* source, const natural sourceSize, const character* text, const natural textSize);

	/**
	 * Returns the first occurrence of {@code text} within {@code source}, or
	 * {@code NULL} if there is no such occurrence.
	 * <p>
	 * @param source the input string
	 * @param text   the string to be searched
	 * <p>
	 * @return the first occurrence of {@code text} within {@code source}, or
	 *         {@code NULL} if there is no such occurrence
	 */
	character* string_find_string(string source, const string text);

	/**********************************************************************************************/

	boolean chars_replace(character* source, const natural sourceSize, const character* oldText, const natural oldTextSize, const character* newText, const natural newTextSize);

	boolean string_replace(string source, const string oldText, const string newText);

	/**********************************************************************************************/

	boolean chars_replace_all(character* source, const natural sourceSize, const character* oldText, const natural oldTextSize, const character* newText, const natural newTextSize);

	boolean string_replace_all(string source, const string oldText, const string newText);


	/***********************************************************************************************
	 * SPLIT
	 **********************************************************************************************/

	/**
	 * Returns an Array of arrays of characters computed by splitting
	 * {@code source} around {@code delimiters}.
	 * <p>
	 * @param source     the array of characters to be split
	 * @param size       the size of the array of characters to be split
	 * @param delimiters the delimiting characters
	 * <p>
	 * @return an Array of arrays of characters computed by splitting
	 *         {@code source} around {@code delimiters}
	 */
	Array* chars_split(character* source, const natural size, const string delimiters);

	/**
	 * Returns an Array of strings computed by splitting {@code source} around
	 * {@code delimiters}.
	 * <p>
	 * @param source     the string to be split
	 * @param delimiters the delimiting characters
	 * <p>
	 * @return an Array of strings computed by splitting {@code source} around
	 *         {@code delimiters}
	 */
	Array* string_split(string source, const string delimiters);


	/***********************************************************************************************
	 * CONCATENATE
	 **********************************************************************************************/

	/**
	 * Concatenate {@code source} to {@code target}.
	 * <p>
	 * @param source     the input array of characters
	 * @param sourceSize the size of the input array of characters
	 * @param length     the length of the concatenation
	 * @param target     the output array of characters
	 * @param targetSize the size of the output array
	 * <p>
	 * @return {@code _TRUE} if {@code source} has been fully concatenated into
	 *         {@code target}, {@code _FALSE} otherwise
	 */
	boolean chars_cat(const character* source, const natural sourceSize, const natural length, character* target, const natural targetSize);


	/***********************************************************************************************
	 * FORMAT
	 **********************************************************************************************/

	/**
	 * Writes the C string pointed by {@code source} to {@code target}. If
	 * {@code source} includes format specifiers (subsequences beginning with
	 * {@code _FORMAT_SPECIFIER}), the additional arguments are formatted and
	 * inserted in the resulting string replacing their respective specifiers.
	 * <p>
	 * @param target     the target array of characters
	 * @param targetSize the size of the target array of characters
	 * @param format     the C string that contains the text to be written and
	 *                   optionally embedded format specifiers
	 * @param ...        the additional arguments to be formatted as requested
	 *                   and that replace the format specifiers
	 * <p>
	 * @return {@code _TRUE} if the replacement of the format specifiers is
	 *         performed, {@code _FALSE} otherwise
	 */
	boolean chars_format(character* target, const natural targetSize, const character* format, ...);

	/**
	 * Writes the C string pointed by {@code source} to {@code target}. If
	 * {@code source} includes format specifiers (subsequences beginning with
	 * {@code _FORMAT_SPECIFIER}), the additional arguments are formatted and
	 * inserted in the resulting string replacing their respective specifiers.
	 * <p>
	 * @param target the target array of characters
	 * @param format the C string that contains the text to be written and
	 *               optionally embedded format specifiers
	 * @param ...    the additional arguments to be formatted as requested
	 *               and that replace the format specifiers
	 * <p>
	 * @return {@code _TRUE} if the replacement of the format specifiers is
	 *         performed, {@code _FALSE} otherwise
	 */
	boolean string_format(character* target, const character* format, ...);

	/**
	 * Returns the C string pointed by {@code source}. If {@code source}
	 * includes format specifiers (subsequences beginning with
	 * {@code _FORMAT_SPECIFIER}), the additional arguments are formatted and
	 * inserted in the resulting string replacing their respective specifiers.
	 * <p>
	 * @param format the C string that contains the text to be written and
	 *               optionally embedded format specifiers
	 * @param ...    the additional arguments to be formatted as requested and
	 *               that replace the format specifiers
	 * <p>
	 * @return the C string pointed by {@code source}
	 */
	character* string_format_new(const character* format, ...);


	/***********************************************************************************************
	 * ITERABLE
	 **********************************************************************************************/

	/**
	 * Returns the next element in the iteration.
	 * <p>
	 * @param iterator the Iterator of the iteration
	 * <p>
	 * @return the next element in the iteration
	 */
	void* string_Iterator_next(Iterator* iterator);


	/***********************************************************************************************
	 * COMPARABLE
	 **********************************************************************************************/

	/**
	 * Compares the specified array of characters for order. Returns a negative
	 * integer, zero, or a positive integer as the first argument is less than,
	 * equal to, or greater than the second.
	 * <p>
	 * @param first      the array of characters to be compared for order
	 * @param firstSize  the size of the array of characters to be compared for order
	 * @param second     the array of characters with which to compare
	 * @param secondSize the size of the array of characters with which to compare
	 * <p>
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second
	 */
	integer chars_compare_to(const character* first, const natural firstSize, const character* second, const natural secondSize);

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable string_create_Comparable(void);

	/**
	 * Compares the specified structures for order. Returns a negative integer,
	 * zero, or a positive integer as the first argument is less than, equal to,
	 * or greater than the second.
	 * <p>
	 * @param structure the structure to be compared for order
	 * @param type      the type of the structure with which to compare
	 * @param value     the value of the structure with which to compare
	 * <p>
	 * @return a negative integer, zero, or a positive integer as the first
	 *         argument is less than, equal to, or greater than the second
	 */
	integer string_compare_to(const void* structure, const type type, const void* value);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

	/**
	 * Releases the specified array of characters.
	 * <p>
	 * @param string the array of characters to be released
	 */
	void chars_release(character* string);

	/**
	 * Releases the specified string.
	 * <p>
	 * @param string the string to be released
	 */
	void string_release(string string);

	/**********************************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified arrays of characters are equal,
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param first      the array of characters to be compared for equality
	 * @param firstSize  the size of the array of characters to be compared for equality
	 * @param second     the array of characters with which to compare
	 * @param secondSize the size of the array of characters with which to compare
	 * <p>
	 * @return {@code _TRUE} if the specified arrays of characters are equal,
	 *         {@code _FALSE} otherwise
	 */
	boolean chars_equals(const character* first, const natural firstSize, const character* second, const natural secondSize);

	/**
	 * Returns {@code _TRUE} if the specified structures are equal,
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param structure the structure to be compared for equality
	 * @param type      the type of the structure with which to compare
	 * @param value     the value of the structure with which to compare
	 * <p>
	 * @return {@code _TRUE} if the specified structures are equal,
	 *         {@code _FALSE} otherwise
	 */
	boolean string_equals(const void* structure, const type type, const void* value);

	/**********************************************************************************************/

	/**
	 * Returns the hash code of the specified array.
	 * <p>
	 * @param structure the array to be hashed
	 * @param size      the size of the array to be hashed
	 * <p>
	 * @return the hash code of the specified array
	 */
	integer chars_hash(const character* structure, const natural size);

	/**
	 * Returns the hash code of the specified structure.
	 * <p>
	 * @param structure the structure to be hashed
	 * <p>
	 * @return the hash code of the specified structure
	 */
	integer string_hash(const void* structure);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} of size {@code size}.
	 * <p>
	 * Warning(s):
	 * - If the size of {@code target} is smaller than {@code size}, there
	 *   is a buffer overflow.
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code size}.
	 */
	boolean chars_to_chars(const character* source, const natural sourceSize, character* target, const natural targetSize);

	/**
	 * Appends {@code source} to the end of {@code target} of size
	 * {@code size}.
	 * <p>
	 * Warning(s):
	 * - If the size of {@code target} is smaller than {@code size}, there
	 *   is a buffer overflow.
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code size-strlen(target)}.
	 */
	boolean chars_append_to_chars(const character* source, const natural sourceSize, character* target, const natural targetSize);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean chars_to_string(const character* source, const natural sourceSize, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean chars_append_to_string(const character* source, const natural sourceSize, string target);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean string_to_chars(const string source, string target, const natural targetSize);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean string_append_to_chars(const string source, string target, const natural targetSize);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean string_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean string_append_to_string(const void* source, string target);


#endif /* _COMMON_STRING_H */
#ifdef __cplusplus
}
#endif
