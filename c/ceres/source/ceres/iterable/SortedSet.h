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
#ifndef _SORTED_SET_H
#define _SORTED_SET_H


	/***********************************************************************************************
	 * INCLUDES
	 **********************************************************************************************/

#include "ceres/Common.h"

#include "ceres/iterable/Set.h"


	/***********************************************************************************************
	 * CONSTANTS
	 **********************************************************************************************/

	/**
	 * Defines the name of the Sorted Sets.
	 */
#define _SORTED_SET_NAME			_S("Sorted Set")
	/**
	 * Defines the type of the Sorted Sets.
	 */
#define _SORTED_SET_TYPE			_SET_TYPE


	/***********************************************************************************************
	 * TYPES
	 **********************************************************************************************/

	typedef Set SortedSet;


	/***********************************************************************************************
	 * CONSTRUCT
	 **********************************************************************************************/

	/**
	 * Constructs a Sorted Set dynamically.
	 * <p>
	 * @param elementType the element type of the Sorted Set to be constructed
	 * @param elementSize the element size of the Sorted Set to be constructed
	 * @param initialSize the initial size of the Sorted Set to be constructed
	 * @param comparator  the element comparator of the Sorted Set to be constructed
	 * <p>
	 * @return the dynamically constructed Sorted Set
	 */
	SortedSet* SortedSet_new(const type elementType, const natural elementSize, const natural initialSize, const Comparable comparator);


	/***********************************************************************************************
	 * RESET
	 **********************************************************************************************/

	/**
	 * Resets the specified Sorted Set.
	 * <p>
	 * @param sortedSet   the Sorted Set to be reset
	 * @param elementType the element type to set
	 * @param elementSize the element size to set
	 * @param initialSize the initial size to set
	 * @param comparator  the element comparator to set
	 */
	void SortedSet_reset(void* sortedSet, const type elementType, const natural elementSize, const natural initialSize, const Comparable comparator);


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
	boolean SortedSet_add(void* collection, const type type, void* value);

	/**
	 * Adds the specified value to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param value      the value to be added
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean SortedSet_add_value(void* collection, void* value);

	/**
	 * Adds the specified Structure to the specified Collection.
	 * <p>
	 * @param collection the Collection
	 * @param structure  the Structure to be added
	 * <p>
	 * @return {@code _TRUE} if the insert is performed, {@code _FALSE}
	 *         otherwise
	 */
	boolean SortedSet_add_Structure(void* collection, const Structure* structure);


	/***********************************************************************************************
	 * BASIC
	 **********************************************************************************************/

	/**
	 * Constructs a dynamic copy of the specified structure.
	 * <p>
	 * @param structure the structure to be cloned
	 * <p>
	 * @return a dynamic copy of the specified structure
	 */
	void* SortedSet_clone(const void* structure);


#endif /* _SORTED_SET_H */
#ifdef __cplusplus
}
#endif
