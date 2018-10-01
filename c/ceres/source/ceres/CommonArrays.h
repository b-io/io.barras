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
#ifndef _COMMON_ARRAYS_H
#define _COMMON_ARRAYS_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/CommonConstants.h"
#include "ceres/CommonMacros.h"
#include "ceres/CommonTypes.h"
#include "ceres/CommonStructures.h"
#include "ceres/CommonFunctions.h"

#include "ceres/type/CommonType.h"

#include <stdarg.h>
#include <stdio.h>


	/***********************************************************************************************
	 * CONSTRUCT
	 **********************************************************************************************/

	/**
	 * Returns an array with the specified size, or {@code NULL} if the memory
	 * allocation failed.
	 * <p>
	 * @param size        the size of the array to be allocated
	 * @param elementSize the element size of the array to be allocated
	 * <p>
	 * @return an array with the specified size, or {@code NULL} if the memory
	 *         allocation failed
	 */
	void* array_new(const natural size, const natural elementSize);


	/***********************************************************************************************
	 * ELEMENT
	 **********************************************************************************************/

	/**
	 * Constructs an Element statically.
	 * <p>
	 * @param index   the index of the Element
	 * @param pointer the pointer of the Element
	 * <p>
	 * @return the statically constructed Element
	 */
	Element Element_create(const natural index, void* pointer);


	/***********************************************************************************************
	 * ARRAY
	 **********************************************************************************************/

	/**
	 * Replaces the specified element with the specified value.
	 * <p>
	 * @param elementType the element type
	 * @param elementSize the element size
	 * @param element     the element to be set
	 * @param value       the value to be stored
	 * <p>
	 * @return the next element to be set, or {@code NULL} if the type is not
	 *         elementary
	 */
	void* array_add(const type elementType, const natural elementSize, void* element, const void* value);

	/**
	 * Copies {@code source} into {@code target}.
	 * <p>
	 * @param source      the array to be copied
	 * @param sourceSize  the size of the array to be copied
	 * @param elementSize the element size
	 * @param target      the target array
	 * @param targetSize  the size of the target array
	 * <p>
	 * @return {@code _TRUE} if {@code source} has been fully copied into
	 *         {@code target}, {@code _FALSE} otherwise
	 */
	boolean array_copy(const void* source, const natural sourceSize, const natural elementSize, void* target, const natural targetSize);

	/**********************************************************************************************/

	/**
	 * Returns the first element with the specified value and its index in the
	 * specified array if present, {@code NULL} and the length of the array
	 * otherwise.
	 * <p>
	 * @param array       the array
	 * @param length      the length of the array
	 * @param elementType the element type of the array
	 * @param elementSize the element size of the array
	 * @param value       the value to be found
	 * <p>
	 * @return the first element with the specified value and its index in the
	 *         specified array if present, {@code NULL} and the length of the
	 *         array otherwise
	 */
	Element array_find(const void* array, const natural length, const type elementType, const natural elementSize, const void* value);

	/**
	 * Returns the first element with the specified value in the specified array
	 * if present, {@code NULL} otherwise.
	 * <p>
	 * @param array       the array
	 * @param length      the length of the array
	 * @param elementType the element type of the array
	 * @param elementSize the element size of the array
	 * @param value       the value to be found
	 * <p>
	 * @return the first element with the specified value in the specified array
	 *         if present, {@code NULL} otherwise
	 */
	void* array_find_element(const void* array, const natural length, const type elementType, const natural elementSize, const void* value);

	/**
	 * Returns the index of the first element with the specified value in the
	 * specified array if present, the length of the array otherwise.
	 * <p>
	 * @param array       the array
	 * @param length      the length of the array
	 * @param elementType the element type of the array
	 * @param elementSize the element size of the array
	 * @param value       the value to be found
	 * <p>
	 * @return the index of the first element with the specified value in the
	 *         specified array if present, the length of the array otherwise
	 */
	natural array_find_index(const void* array, const natural length, const type elementType, const natural elementSize, const void* value);

	/**********************************************************************************************/

	/**
	 * Returns the first element with the specified value and its index in the
	 * specified array if present, {@code NULL} and the length of the array
	 * otherwise.
	 * <p>
	 * @param array       the array
	 * @param length      the length of the array
	 * @param elementType the element type of the array
	 * @param elementSize the element size of the array
	 * @param value       the value to be found
	 * @param comparator  the comparator for element equality of the array
	 * <p>
	 * @return the first element with the specified value and its index in the
	 *         specified array if present, {@code NULL} and the length of the
	 *         array otherwise
	 */
	Element array_find_with_comparator(const void* array, const natural length, const type elementType, const natural elementSize, const void* value, const Comparable* comparator);

	/**
	 * Returns the first element with the specified value in the specified array
	 * if present, {@code NULL} otherwise.
	 * <p>
	 * @param array       the array
	 * @param length      the length of the array
	 * @param elementType the element type of the array
	 * @param elementSize the element size of the array
	 * @param value       the value to be found
	 * @param comparator  the comparator for element equality of the array
	 * <p>
	 * @return the first element with the specified value in the specified array
	 *         if present, {@code NULL} otherwise
	 */
	void* array_find_element_with_comparator(const void* array, const natural length, const type elementType, const natural elementSize, const void* value, const Comparable* comparator);

	/**
	 * Returns the index of the first element with the specified value in the
	 * specified array if present, the length of the array otherwise.
	 * <p>
	 * @param array       the array
	 * @param length      the length of the array
	 * @param elementType the element type of the array
	 * @param elementSize the element size of the array
	 * @param value       the value to be found
	 * @param comparator  the comparator for element equality of the array
	 * <p>
	 * @return the index of the first element with the specified value in the
	 *         specified array if present, the length of the array otherwise
	 */
	natural array_find_index_with_comparator(const void* array, const natural length, const type elementType, const natural elementSize, const void* value, const Comparable* comparator);

	/**********************************************************************************************/

	/**
	 * Returns the element at the specified position in the specified array.
	 * <p>
	 * @param array       the array
	 * @param elementType the element type of the array
	 * @param elementSize the element size of the array
	 * @param index       the index of the element to be returned
	 * <p>
	 * @return the element at the specified position in the specified array
	 */
	void* array_get(const void* array, const type elementType, const natural elementSize, const natural index);

	/**
	 * Inserts the specified value at the specified position into the array.
	 * <p>
	 * @param length      the length of the array
	 * @param elementSize the element size of the array
	 * @param index       the index at which the value is to be inserted
	 * @param element     the element at the index
	 * @param value       the value to be inserted
	 * <p>
	 * @return {@code _TRUE} if the specified value is inserted into the array,
	 *         {@code _FALSE} otherwise
	 */
	boolean array_insert(const natural length, const natural elementSize, const natural index, void* element, const void* value);

	/**
	 * Removes the specified element from the array.
	 * <p>
	 * @param length      the length of the array
	 * @param elementType the element type of the array
	 * @param elementSize the element size of the array
	 * @param index       the index of the element to be removed from the array
	 * @param element     the element to be removed from the array
	 * <p>
	 * @return {@code _TRUE} if the specified element is removed from the array,
	 *         {@code _FALSE} otherwise
	 */
	void* array_remove(const natural length, const type elementType, const natural elementSize, const natural index, void* element);

	/**
	 * Replaces the element at the specified position in the specified array
	 * with the specified value.
	 * <p>
	 * @param array       the array to be set
	 * @param elementType the element type of the array
	 * @param elementSize the element size of the array
	 * @param index       the index of the element to be replaced
	 * @param value       the value to be stored at the position
	 * <p>
	 * @return {@code _TRUE} if the replacement is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean array_set(void* array, const type elementType, const natural elementSize, const natural index, const void* value);


	/***********************************************************************************************
	 * ITERABLE
	 **********************************************************************************************/

	/**
	 * Returns an Iterator over the elements of the specified array.
	 * <p>
	 * @param array       the array
	 * @param length      the length of the array
	 * @param elementType the element type of the array
	 * @param elementSize the element size of the array
	 * <p>
	 * @return an Iterator over the elements of the specified array
	 */
	Iterator array_iterator(void* array, const natural length, const type elementType, const natural elementSize);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean array_to_string(const Structure* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean append_array_to_string(const Structure* source, string target);


#endif /* _COMMON_ARRAYS_H */
#ifdef __cplusplus
}
#endif
