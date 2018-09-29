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
#ifndef _COMMON_COLLECTION_H
#define _COMMON_COLLECTION_H


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

#include "ceres/iterable/CommonIterable.h"


	/***********************************************************************************************
	 * RESET
	 **********************************************************************************************/

	/**
	 * Resets the specified Collection.
	 * <p>
	 * @param collection  the Collection to be reset
	 * @param length      the length to set
	 * @param elementType the element type to set
	 * @param elementSize the element size to set
	 * @param initialSize the initial size to set
	 */
	void Collection_reset(void* collection, const natural length, const type elementType, const natural elementSize, const natural initialSize);


	/***********************************************************************************************
	 * COLLECTION
	 **********************************************************************************************/

	/**
	 * Adds the specified value to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param type       the type of the value to be added
	 * @param value      the value to be added
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean Collection_add(void* collection, const type type, void* value);

	/**
	 * Adds the specified value to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param value      the value to be added
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean Collection_add_value(void* collection, void* value);

	/**
	 * Adds the specified Structure to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param structure  the Structure to be added
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean Collection_add_Structure(void* collection, const Structure* structure);

	/**
	 * Adds all of the specified values to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param values     the Collection of values to be added
	 * <p>
	 * @return {@code _TRUE} if the Collection changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean Collection_add_all(void* collection, const void* values);

	/**
	 * Adds all of the specified values to the specified Collection and resizes.
	 * <p>
	 * @param collection the Collection
	 * @param values     the Collection of values to be added
	 * <p>
	 * @return {@code _TRUE} if the Collection changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean Collection_add_all_and_resize(void* collection, const void* values);

	/**********************************************************************************************/

	/**
	 * Removes all of the elements from the specified Collection. The specified
	 * Collection will be empty after this method returns.
	 * <p>
	 * @param collection the Collection to be cleared
	 */
	void Collection_clear(void* collection);

	/**********************************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified Collection contains the specified
	 * value, {@code _FALSE} otherwise.
	 * <p>
	 * @param collection the Collection
	 * @param type       the type of the value to be checked for containment
	 * @param value      the value to be checked for containment
	 * <p>
	 * @return {@code _TRUE} if the specified Collection contains the specified
	 *         value, {@code _FALSE} otherwise
	 */
	boolean Collection_contains(const void* collection, const type type, const void* value);

	/**
	 * Returns {@code _TRUE} if the specified Collection contains the specified
	 * value, {@code _FALSE} otherwise.
	 * <p>
	 * @param collection the Collection
	 * @param value      the value to be checked for containment
	 * <p>
	 * @return {@code _TRUE} if the specified Collection contains the specified
	 *         value, {@code _FALSE} otherwise
	 */
	boolean Collection_contains_value(const void* collection, const void* value);

	/**
	 * Returns {@code _TRUE} if the specified Collection contains the specified
	 * Structure, {@code _FALSE} otherwise.
	 * <p>
	 * @param collection the Collection
	 * @param structure  the Structure to be checked for containment
	 * <p>
	 * @return {@code _TRUE} if the specified Collection contains the specified
	 *         Structure, {@code _FALSE} otherwise
	 */
	boolean Collection_contains_Structure(const void* collection, const Structure* structure);

	/**
	 * Returns {@code _TRUE} if the specified Collection contains all of the
	 * specified values, {@code _FALSE} otherwise.
	 * <p>
	 * @param collection the Collection
	 * @param values     the Collection of values to be checked for containment
	 * <p>
	 * @return {@code _TRUE} if the specified Collection contains all of the
	 *         specified values, {@code _FALSE} otherwise
	 */
	boolean Collection_contains_all(const void* collection, const void* values);

	/**********************************************************************************************/

	/**
	 * Returns the number of occurrences of the specified value in the specified
	 * Collection.
	 * <p>
	 * @param collection the Collection
	 * @param type       the type of the value to be counted
	 * @param value      the value to be counted
	 * <p>
	 * @return the number of occurrences of the specified value in the specified
	 *         Collection
	 */
	natural Collection_count(const void* collection, const type type, const void* value);

	/**
	 * Returns the number of occurrences of the specified value in the specified
	 * Collection.
	 * <p>
	 * @param collection the Collection
	 * @param value      the value to be counted
	 * <p>
	 * @return the number of occurrences of the specified value in the specified
	 *         Collection
	 */
	natural Collection_count_value(const void* collection, const void* value);

	/**
	 * Returns the number of occurrences of the specified Structure in the
	 * specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param structure  the Structure to be counted
	 * <p>
	 * @return the number of occurrences of the specified Structure in the
	 *         specified Collection
	 */
	natural Collection_count_Structure(const void* collection, const Structure* structure);

	/**
	 * Returns the number of occurrences of the specified values in the
	 * specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param values     the Collection of values to be counted
	 * <p>
	 * @return the number of occurrences of the specified values in the
	 *         specified Collection
	 */
	natural Collection_count_all(const void* collection, const void* values);

	/**********************************************************************************************/

	/**
	 * Removes a single instance of the specified value from the specified
	 * Collection.
	 * <p>
	 * @param collection the Collection
	 * @param type       the type of the value to be removed
	 * @param value      the value to be removed
	 * <p>
	 * @return {@code _TRUE} if the Collection changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean Collection_remove(void* collection, const type type, const void* value);

	/**
	 * Removes a single instance of the specified value from the specified
	 * Collection.
	 * <p>
	 * @param collection the Collection
	 * @param value      the value to be removed
	 * <p>
	 * @return {@code _TRUE} if the Collection changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean Collection_remove_value(void* collection, const void* value);

	/**
	 * Removes a single instance of the specified Structure from the specified
	 * Collection.
	 * <p>
	 * @param collection the Collection
	 * @param structure  the Structure to be removed
	 * <p>
	 * @return {@code _TRUE} if the Collection changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean Collection_remove_Structure(void* collection, const Structure* structure);

	/**
	 * Removes all of the specified values from the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param values     the Collection of values to be removed
	 * <p>
	 * @return {@code _TRUE} if the Collection changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean Collection_remove_all(void* collection, const void* values);

	/**********************************************************************************************/

	/**
	 * Resizes the specified Collection to the specified size.
	 * <p>
	 * @param collection the Collection to be resized
	 * @param size       the size to set
	 * <p>
	 * @return {@code _TRUE} if the resizing is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean Collection_resize(void* collection, const natural size);


	/***********************************************************************************************
	 * COMPARABLE
	 **********************************************************************************************/

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable Collection_create_Comparable(void);

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
	integer Collection_compare_to(const void* structure, const type type, const void* value);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

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
	boolean Collection_equals(const void* structure, const type type, const void* value);

	/**********************************************************************************************/

	/**
	 * Returns the hash code of the specified structure.
	 * <p>
	 * @param structure the structure to be hashed
	 * <p>
	 * @return the hash code of the specified structure
	 */
	integer Collection_hash(const void* structure);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean Collection_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean Collection_append_to_string(const void* source, string target);


#endif /* _COMMON_COLLECTION_H */
#ifdef __cplusplus
}
#endif
