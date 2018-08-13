/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io> (florian@barras.io)
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
#ifndef _COMMON_FUNCTIONS_H
#define _COMMON_FUNCTIONS_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/CommonConstants.h"
#include "ceres/CommonMacros.h"
#include "ceres/CommonTypes.h"
#include "ceres/CommonStructures.h"

#include "ceres/type/CommonType.h"

#include <stdarg.h>
#include <stdio.h>


	/***********************************************************************************************
	 * FORMAT
	 **********************************************************************************************/

	boolean format_specifier_to_string(const character* source, va_list* args, string target);

	boolean format_to_chars(const character* source, va_list* args, character* target, const natural targetSize);
	boolean format_to_file(const character* source, va_list* args, FILE* target, const natural targetSize);


	/***********************************************************************************************
	 * POINTER
	 **********************************************************************************************/

	boolean pointer_to_string(const void* source, string target);


	/***********************************************************************************************
	 * TYPES
	 **********************************************************************************************/

	natural type_get_size(const type type);

	/**********************************************************************************************/

	boolean type_is_dynamic(const type type);

	boolean type_is_array(const type type);

	boolean type_is_numeric(const type type);
	boolean type_is_Basic(const type type);
	boolean type_is_Comparable(const type type);
	boolean type_is_Iterable(const type type);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean type_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean type_append_to_string(const void* source, string target);


	/***********************************************************************************************
	 * COMPARABLE
	 **********************************************************************************************/

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @param functionRelease   the release function to set
	 * @param functionClone     the clone function to set
	 * @param functionEquals    the equals function to set
	 * @param functionHash      the hash function to set
	 * @param functionToString  the to_string function to set
	 * @param functionCompareTo the compare_to function to set
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable Comparable_create(function_release functionRelease, function_clone functionClone, function_equals functionEquals, function_hash functionHash, function_to_string functionToString, function_compare_to functionCompareTo);

	integer compare_to(const type firstType, const void* firstValue, const type secondType, const void* secondValue);
	integer Structure_compare_to(const void* structure, const type type, const void* value);
	integer Comparable_compare_to(const void* structure, const type type, const void* value);
	function_compare_to get_function_compare_to(const type type);

	integer Structures_compare_to(const Structure* first, const Structure* second);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

	Basic Basic_create(function_release functionRelease, function_clone functionClone, function_equals functionEquals, function_hash functionHash, function_to_string functionToString);

	/**********************************************************************************************/

	/**
	 * Releases the specified Structure.
	 * <p>
	 * @param structure the Structure to be released
	 */
	void release(Structure* structure);

	/**********************************************************************************************/

	boolean equals(const type firstType, const void* firstValue, const type secondType, const void* secondValue);
	boolean Structure_equals(const void* structure, const type type, const void* value);
	boolean Basic_equals(const void* structure, const type type, const void* value);
	function_equals get_function_equals(const type type);

	boolean Structures_equals(const Structure* first, const Structure* second);

	/**********************************************************************************************/

	integer hash(const Structure* structure);
	integer Structure_hash(const void* structure);
	integer Basic_hash(const void* structure);
	function_hash get_function_hash(const type type);

	/**********************************************************************************************/

	boolean to_string(const void* source, const type type, string target);
	boolean Structure_to_string(const void* source, string target);
	boolean Basic_to_string(const void* source, string target);
	function_to_string get_function_to_string(const type type);

	boolean append_to_string(const void* source, const type type, string target);
	boolean Structure_append_to_string(const void* source, string target);
	boolean Basic_append_to_string(const void* source, string target);
	function_append_to_string get_function_append_to_string(const type type);


	/***********************************************************************************************
	 * STRUCTURE
	 **********************************************************************************************/

	/**
	 * Constructs a Structure statically.
	 * <p>
	 * @param type  the type to set
	 * @param value the value to set
	 * <p>
	 * @return the statically constructed Structure
	 */
	Structure Structure_create(const type type, void* value);


	/***********************************************************************************************
	 * CORE
	 **********************************************************************************************/

	/**
	 * Constructs a Core statically.
	 * <p>
	 * @param isBasic      the is Basic boolean to set
	 * @param isComparable the is Comparable boolean to set
	 * @param isDynamic    the is dynamic boolean to set
	 * @param isElement    the is element boolean to set
	 * <p>
	 * @return the statically constructed Core
	 */
	Core Core_create(const boolean isBasic, const boolean isComparable, const boolean isDynamic, const boolean isElement);


#endif /* _COMMON_FUNCTIONS_H */
#ifdef __cplusplus
}
#endif
