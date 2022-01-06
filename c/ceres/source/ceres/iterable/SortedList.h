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
#ifndef _SORTED_LIST_H
#define _SORTED_LIST_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/Common.h"

#include "ceres/iterable/LinkedList.h"


	/***********************************************************************************************
	 * CONSTANTS
	 **********************************************************************************************/

	/**
	 * Defines the name of the Sorted Lists.
	 */
#define _SORTED_LIST_NAME			_S("Sorted List")
	/**
	 * Defines the type of the Sorted Lists.
	 */
#define _SORTED_LIST_TYPE			_LIST_TYPE


	/**
	 * Defines the name of the Sorted Nodes.
	 */
#define _SORTED_NODE_NAME			_S("Sorted Node")


	/***********************************************************************************************
	 * TYPES
	 **********************************************************************************************/

	typedef LinkedList SortedList;
	typedef LinkedNode SortedNode;


	/***********************************************************************************************
	 * CONSTRUCT
	 **********************************************************************************************/

	/**
	 * Constructs a Sorted List dynamically.
	 * <p>
	 * @return the dynamically constructed Sorted List
	 */
	SortedList* SortedList_new(void);


	/***********************************************************************************************
	 * RESET
	 **********************************************************************************************/

	/**
	 * Resets the specified Sorted List.
	 * <p>
	 * @param sortedList the Sorted List to be reset
	 */
	void SortedList_reset(void* sortedList);


	/***********************************************************************************************
	 * SORTED LIST
	 **********************************************************************************************/

	/**
	 * Inserts {@code node} into {@code sortedList}.
	 * <p>
	 * @param sortedList the Sorted List
	 * @param node       the Sorted Node to be inserted
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean SortedList_insert(SortedList* sortedList, SortedNode* node);


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
	boolean SortedList_add(void* collection, const type type, void* value);

	/**
	 * Adds the specified value to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param value      the value to be added
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean SortedList_add_value(void* collection, void* value);

	/**
	 * Adds the specified Structure to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param structure  the Structure to be added
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean SortedList_add_Structure(void* collection, const Structure* structure);


#endif /* _SORTED_LIST_H */
#ifdef __cplusplus
}
#endif
