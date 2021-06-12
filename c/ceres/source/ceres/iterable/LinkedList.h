/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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
#ifndef _LINKED_LIST_H
#define _LINKED_LIST_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/Common.h"

#include "ceres/iterable/CommonList.h"


	/***********************************************************************************************
	 * CONSTANTS
	 **********************************************************************************************/

	/**
	 * Defines the name of the Linked Lists.
	 */
#define _LINKED_LIST_NAME			_S("Linked List")

	/**
	 * Defines the type of the Linked Lists.
	 */
#define _LINKED_LIST_TYPE			_LIST_TYPE

	/**********************************************************************************************/

	/**
	 * Defines the name of the Linked Nodes.
	 */
#define _LINKED_NODE_NAME			_S("Linked Node")

	/**
	 * Defines the type of the Linked Nodes.
	 */
#if _32_BITS
#define _LINKED_NODE_TYPE			-958821868L /* 32 bits */
#else
#define _LINKED_NODE_TYPE			-5273095345972366597LL /* 64 bits */
#endif


	/***********************************************************************************************
	 * STRUCTURES
	 **********************************************************************************************/

	/**
	 * Defines a Linked Node.
	 */
	typedef struct LinkedNode
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
		 * OBJECT
		 ******************************************************************************************/

		Structure structure;

		/*******************************************************************************************
		 * LINKED NODE
		 ******************************************************************************************/

		struct LinkedNode* previous;
		struct LinkedNode* next;
	} LinkedNode;

	/**********************************************************************************************/

	/**
	 * Defines a Linked List.
	 */
	typedef struct LinkedList
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
		 * LINKED LIST
		 ******************************************************************************************/

		LinkedNode* first;
		LinkedNode* last;
	} LinkedList;


	/***********************************************************************************************
	 * CONSTRUCT
	 **********************************************************************************************/

	/**
	 * Constructs a Linked List dynamically.
	 * <p>
	 * @return the dynamically constructed Linked List
	 */
	LinkedList* LinkedList_new(void);

	/**********************************************************************************************/

	/**
	 * Constructs a Linked Node dynamically.
	 * <p>
	 * @param structure the Structure to set
	 * <p>
	 * @return the dynamically constructed Linked Node
	 */
	LinkedNode* LinkedNode_new_from_Structure(const Structure* structure);

	/**
	 * Constructs a Linked Node dynamically.
	 * <p>
	 * @param object the Object to set
	 * <p>
	 * @return the dynamically constructed Linked Node
	 */
	LinkedNode* LinkedNode_new_from_Object(const Object* object);


	/***********************************************************************************************
	 * RESET
	 **********************************************************************************************/

	/**
	 * Resets the specified Linked List.
	 * <p>
	 * @param linkedList the Linked List to be reset
	 */
	void LinkedList_reset(void* linkedList);

	/**********************************************************************************************/

	/**
	 * Resets the specified Linked Node.
	 * <p>
	 * @param linkedNode the Linked Node to be reset
	 * @param structure  the Structure to set
	 */
	void LinkedNode_reset(void* linkedNode, const Structure* structure);


	/***********************************************************************************************
	 * LINKED LIST
	 **********************************************************************************************/

	/**
	 * Inserts {@code node} into {@code linkedList} between {@code previousNode}
	 * and {@code nextNode}).
	 * <p>
	 * @param linkedList   the Linked List
	 * @param node         the Linked Node to be inserted
	 * @param previousNode the previous Linked Node of the Linked Node to be inserted
	 * @param nextNode     the next Linked Node of the Linked Node to be inserted
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean LinkedList_insert_between(LinkedList* linkedList, LinkedNode* node, LinkedNode* previousNode, LinkedNode* nextNode);

	/**
	 * Inserts {@code node} at the end of {@code linkedList}.
	 * <p>
	 * @param linkedList   the Linked List
	 * @param node         the Linked Node to be inserted
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean LinkedList_insert_last(LinkedList* linkedList, LinkedNode* node);

	/**
	 * Extracts {@code node} from {@code linkedList}.
	 * <p>
	 * @param linkedList the Linked List
	 * @param node       the Linked Node to be removed
	 */
	void LinkedList_extract(LinkedList* linkedList, LinkedNode* node);

	/**
	 * Returns the Linked Node at the specified position in the specified Linked
	 * List.
	 * <p>
	 * @param linkedList the Linked List
	 * @param index      the index of the Linked Node to be returned
	 * <p>
	 * @return the Linked Node at the specified position in the specified Linked
	 *         List
	 */
	LinkedNode* LinkedList_get_LinkedNode(LinkedList* linkedList, const natural index);


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
	Structure LinkedList_get(const void* list, const natural index);

	/**
	 * Returns the element that was removed from the specified List.
	 * <p>
	 * @param list  the List
	 * @param index the index of the element to be removed
	 * <p>
	 * @return {@code _TRUE} if the List changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean LinkedList_remove_at(void* list, const natural index);


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
	boolean LinkedList_add(void* collection, const type type, void* value);

	/**
	 * Adds the specified value to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param value      the value to be added
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean LinkedList_add_value(void* collection, void* value);

	/**
	 * Adds the specified Structure to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param structure  the Structure to be added
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean LinkedList_add_Structure(void* collection, const Structure* structure);

	/**********************************************************************************************/

	/**
	 * Removes all of the elements from the specified Collection. The specified
	 * Collection will be empty after this function returns.
	 * <p>
	 * @param collection the Collection to be cleared
	 */
	void LinkedList_clear(void* collection);

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
	boolean LinkedList_contains(const void* collection, const type type, const void* value);

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
	boolean LinkedList_contains_Structure(const void* collection, const Structure* structure);

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
	natural LinkedList_count(const void* collection, const type type, const void* value);

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
	natural LinkedList_count_Structure(const void* collection, const Structure* structure);

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
	boolean LinkedList_remove(void* collection, const type type, const void* value);

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
	boolean LinkedList_remove_Structure(void* collection, const Structure* structure);

	/**
	 * Removes all of the specified values from the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param values     the Collection of values to be removed
	 * <p>
	 * @return {@code _TRUE} if the Collection changed as a result of this call,
	 *         {@code _FALSE} otherwise
	 */
	boolean LinkedList_remove_all(void* collection, const void* values);

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
	boolean LinkedList_resize(void* collection, const natural size);


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
	Iterator LinkedList_iterator(const void* iterable);

	/**********************************************************************************************/

	/**
	 * Returns the next element in the iteration.
	 * <p>
	 * @param iterator the Iterator of the iteration
	 * <p>
	 * @return the next element in the iteration
	 */
	void* LinkedList_Iterator_next(Iterator* iterator);

	/**
	 * Returns the next element in the iteration.
	 * <p>
	 * @param iterator the Iterator of the iteration
	 * <p>
	 * @return the next element in the iteration
	 */
	void* LinkedNode_Iterator_next(Iterator* iterator);


	/***********************************************************************************************
	 * COMPARABLE
	 **********************************************************************************************/

	/**
	 * Constructs a Comparable.
	 * <p>
	 * @return the statically constructed Comparable
	 */
	Comparable LinkedList_create_Comparable(void);

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
	integer LinkedList_compare_to(const void* structure, const type type, const void* value);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

	/**
	 * Releases the specified structure and frees it if requested.
	 * <p>
	 * @param structure the structure to be released
	 */
	void LinkedList_release(void* structure);

	/**
	 * Releases the specified structure and frees it if requested.
	 * <p>
	 * @param structure the structure to be released
	 */
	void LinkedNode_release(void* structure);

	/**********************************************************************************************/

	/**
	 * Constructs a dynamic copy of the specified structure.
	 * <p>
	 * @param structure the structure to be cloned
	 * <p>
	 * @return a dynamic copy of the specified structure
	 */
	void* LinkedList_clone(const void* structure);

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
	boolean LinkedList_equals(const void* structure, const type type, const void* value);

	/**********************************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean LinkedList_to_string(const void* source, string target);

	/**
	 * Appends {@code source} to the end of {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean LinkedList_append_to_string(const void* source, string target);


#endif /* _LINKED_LIST_H */
#ifdef __cplusplus
}
#endif
