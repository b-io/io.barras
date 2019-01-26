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
#ifdef __cplusplus
extern "C"
{
#endif
#ifndef _ARRAY_LIST_H
#define _ARRAY_LIST_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/Common.h"

#include "ceres/iterable/CommonList.h"


	/***********************************************************************************************
	 * CONSTANTS
	 **********************************************************************************************/

	/**
	 * Defines the name of the Array Lists.
	 */
#define _ARRAY_LIST_NAME			_S("Array List")
	/**
	 * Defines the type of the Array Lists.
	 */
#define _ARRAY_LIST_TYPE			_LIST_TYPE


	/***********************************************************************************************
	 * STRUCTURES
	 **********************************************************************************************/

	/**
	 * Defines the Array List.
	 */
	typedef struct ArrayList
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
		 * LIST
		 ******************************************************************************************/

		Structure(*get)(const void* list, const natural index);

		boolean(*removeAt)(void* list, const natural index);

		/*******************************************************************************************
		 * ARRAY LIST
		 ******************************************************************************************/

		Object* elements;
		Object* next;
	} ArrayList;


	/***********************************************************************************************
	 * CONSTRUCT
	 **********************************************************************************************/

	/**
	 * Constructs an Array List dynamically.
	 * <p>
	 * @param initialSize the initial size of the Array List to be constructed
	 * <p>
	 * @return the dynamically constructed Array List
	 */
	ArrayList* ArrayList_new(const natural initialSize);


	/***********************************************************************************************
	 * RESET
	 **********************************************************************************************/

	/**
	 * Resets the specified Array List.
	 * <p>
	 * @param arrayList   the Array List to be reset
	 * @param initialSize the initial size to set
	 */
	void ArrayList_reset(void* arrayList, const natural initialSize);


	/***********************************************************************************************
	 * LIST
	 **********************************************************************************************/

	/**
	 * Returns the element at the specified position in the specified List.
	 * <p>
	 * @param list  the List
	 * @param index the index of the element to be returned
	 * <p>
	 * @return the element at the specified position in the specified List
	 */
	Structure ArrayList_get(const void* list, const natural index);

	/**
	 * Returns the element that was removed from the specified List.
	 * <p>
	 * @param list  the List
	 * @param index the index of the element to be removed
	 * <p>
	 * @return {@code _TRUE} if the List changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean ArrayList_remove_at(void* list, const natural index);


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
	boolean ArrayList_add(void* collection, const type type, void* value);

	/**
	 * Adds the specified Structure to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param structure  the Structure to be added
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean ArrayList_add_Structure(void* collection, const Structure* structure);

	/**********************************************************************************************/

	/**
	 * Removes all of the elements from the specified Collection. The specified
	 * Collection will be empty after this function returns.
	 * <p>
	 * @param collection the Collection to be cleared
	 */
	void ArrayList_clear(void* collection);

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
	boolean ArrayList_contains(const void* collection, const type type, const void* value);

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
	boolean ArrayList_contains_Structure(const void* collection, const Structure* structure);

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
	natural ArrayList_count(const void* collection, const type type, const void* value);

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
	natural ArrayList_count_Structure(const void* collection, const Structure* structure);

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
	boolean ArrayList_remove(void* collection, const type type, const void* value);

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
	boolean ArrayList_remove_Structure(void* collection, const Structure* structure);

	/**
	 * Removes all of the specified values from the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param values     the Collection of values to be removed
	 * <p>
	 * @return {@code _TRUE} if the Collection changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean ArrayList_remove_all(void* collection, const void* values);

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
	boolean ArrayList_resize(void* collection, const natural size);


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
	Iterator ArrayList_iterator(const void* iterable);

	/**********************************************************************************************/

	/**
	 * Returns the next element in the iteration.
	 * <p>
	 * @param iterator the Iterator of the iteration
	 * <p>
	 * @return the next element in the iteration
	 */
	void* ArrayList_Iterator_next(Iterator* iterator);


	/***********************************************************************************************
	 * COMPARABLE
	 **********************************************************************************************/

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable ArrayList_create_Comparable(void);

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
	integer ArrayList_compare_to(const void* structure, const type type, const void* value);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

	/**
	 * Releases the specified structure and frees it if requested.
	 * <p>
	 * @param structure the structure to be released
	 */
	void ArrayList_release(void* structure);

	/**********************************************************************************************/

	/**
	 * Constructs a dynamic copy of the specified structure.
	 * <p>
	 * @param structure the structure to be cloned
	 * <p>
	 * @return a dynamic copy of the specified structure
	 */
	void* ArrayList_clone(const void* structure);

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
	boolean ArrayList_equals(const void* structure, const type type, const void* value);

	/**********************************************************************************************/

	/**
	 * Returns the hash code of the specified structure.
	 * <p>
	 * @param structure the structure to be hashed
	 * <p>
	 * @return the hash code of the specified structure
	 */
	integer ArrayList_hash(const void* structure);


#endif /* _ARRAY_LIST_H */
#ifdef __cplusplus
}
#endif
