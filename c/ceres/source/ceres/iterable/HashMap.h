/*
 * The MIT License
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
#ifndef _HASH_MAP_H
#define _HASH_MAP_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/Common.h"

#include "ceres/iterable/Set.h"


	/***********************************************************************************************
	 * CONSTANTS
	 **********************************************************************************************/

	/**
	 * Defines the name of the Hash Maps.
	 */
#define _HASH_MAP_NAME    _S("Hash Map")

	/**
	 * Defines the type of the Hash Maps.
	 */
#if defined(_32_BITS)
#define _HASH_MAP_TYPE    294707382L /* 32 bits */
#elif defined(_64_BITS)
#define _HASH_MAP_TYPE    6989640109620831344L /* 64 bits */
#endif /* TODO */


	/***********************************************************************************************
	 * STRUCTURES
	 **********************************************************************************************/

	/**
	 * Defines the Hash Map.
	 */
	typedef struct HashMap
	{
		/*******************************************************************************************
		 * BASIC
		 ******************************************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/*******************************************************************************************
		 * COMPARABLE
		 ******************************************************************************************/

		integer(*compareTo)(const void* structure, const type type, const void* value);

		/*******************************************************************************************
		 * ITERABLE
		 ******************************************************************************************/

		natural length;
		Structure element;

		boolean(*isEmpty)(const void* iterable);
		Iterator(*iterator)(const void* iterable);

		/*******************************************************************************************
		 * COLLECTION
		 ******************************************************************************************/

		natural size;

		boolean(*add)(void* collection, const type type, void* value);
		boolean(*addValue)(void* collection, void* value);
		boolean(*addStructure)(void* collection, const Structure* structure);
		boolean(*addAll)(void* collection, const void* values);

		void (*clear)(void* collection);

		boolean(*contains)(const void* collection, const type type, const void* value);
		boolean(*containsValue)(const void* collection, const void* value);
		boolean(*containsStructure)(const void* collection, const Structure* structure);
		boolean(*containsAll)(const void* collection, const void* values);

		natural(*count)(const void* collection, const type type, const void* value);
		natural(*countValue)(const void* collection, const void* value);
		natural(*countStructure)(const void* collection, const Structure* structure);
		natural(*countAll)(const void* collection, const void* values);

		boolean(*remove)(void* collection, const type type, const void* value);
		boolean(*removeValue)(void* collection, const void* value);
		boolean(*removeStructure)(void* collection, const Structure* structure);
		boolean(*removeAll)(void* collection, const void* values);

		boolean(*resize)(void* collection, const natural size);

		/*******************************************************************************************
		 * HASH MAP
		 ******************************************************************************************/

		Comparable comparator;
		Array** table;
	} HashMap;


	/***********************************************************************************************
	 * CONSTRUCT
	 **********************************************************************************************/

	/**
	 * Constructs an Hash Map dynamically.
	 * <p>
	 * @param elementType the element type of the Hash Map to be constructed
	 * @param elementSize the element size of the Hash Map to be constructed
	 * @param initialSize the initial size of the Hash Map to be constructed
	 * <p>
	 * @return the dynamically constructed Hash Map
	 */
	HashMap* HashMap_new(const type elementType, const natural elementSize, const natural initialSize);


	/***********************************************************************************************
	 * RESET
	 **********************************************************************************************/

	/**
	 * Resets the specified Hash Map.
	 * <p>
	 * @param set         the Hash Map to be reset
	 * @param elementType the element type to set
	 * @param elementSize the element size to set
	 * @param initialSize the initial size to set
	 */
	void HashMap_reset(void* set, const type elementType, const natural elementSize, const natural initialSize);


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
	boolean HashMap_add(void* collection, const type type, void* value);

	/**********************************************************************************************/

	/**
	 * Removes all of the elements from the specified Collection. The specified
	 * Collection will be empty after this method returns.
	 * <p>
	 * @param collection the Collection to be cleared
	 */
	void HashMap_clear(void* collection);

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
	boolean HashMap_contains(const void* collection, const type type, const void* value);

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
	natural HashMap_count(const void* collection, const type type, const void* value);

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
	boolean HashMap_remove(void* collection, const type type, const void* value);

	/**
	 * Removes all of the specified values from the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param values     the Collection of values to be removed
	 * <p>
	 * @return {@code _TRUE} if the Collection changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean HashMap_remove_all(void* collection, const void* values);

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
	boolean HashMap_resize(void* collection, const natural size);


	/***********************************************************************************************
	 * ITERABLE
	 **********************************************************************************************/

	/**
	 * Returns an Iterator over the elements in the specified Iterable structure.
	 * <p>
	 * @param iterable the Iterable structure to be iterated
	 * <p>
	 * @return an Iterator over the elements in the specified Iterable structure
	 */
	Iterator HashMap_iterator(const void* iterable);

	/**********************************************************************************************/

	/**
	 * Returns the next element in the iteration.
	 * <p>
	 * @param iterator the Iterator of the iteration
	 * <p>
	 * @return the next element in the iteration
	 */
	void* HashMap_Iterator_next(Iterator* iterator);


	/***********************************************************************************************
	 * COMPARABLE
	 **********************************************************************************************/

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable HashMap_create_Comparable(void);

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
	integer HashMap_compare_to(const void* structure, const type type, const void* value);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

	/**
	 * Releases the specified structure and frees it if requested.
	 * <p>
	 * @param structure the structure to be released
	 */
	void HashMap_release(void* structure);

	/**********************************************************************************************/

	/**
	 * Constructs a dynamic copy of the specified structure.
	 * <p>
	 * @param structure the structure to be cloned
	 * <p>
	 * @return a dynamic copy of the specified structure
	 */
	void* HashMap_clone(const void* structure);

	/**********************************************************************************************/

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
	boolean HashMap_equals(const void* structure, const type type, const void* value);

	/**********************************************************************************************/

	/**
	 * Returns the hash code of the specified structure.
	 * <p>
	 * @param structure the structure to be hashed
	 * <p>
	 * @return the hash code of the specified structure
	 */
	integer HashMap_hash(const void* structure);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean HashMap_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean HashMap_append_to_string(const void* source, string target);


#endif /* _HASH_MAP_H */
#ifdef __cplusplus
}
#endif
