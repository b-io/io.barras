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
#ifndef _COMMON_STRUCTURES_H
#define _COMMON_STRUCTURES_H


	/***************************************************************************
	 * INCLUDES
	 **************************************************************************/

#include "ceres/CommonConstants.h"
#include "ceres/CommonMacros.h"
#include "ceres/CommonTypes.h"


	/***************************************************************************
	 * CORE
	 **************************************************************************/

	/**
	 * Defines the Core.
	 */
	typedef struct Core
	{
		boolean isDynamic;
		boolean isElement;
		boolean isBasic;
		boolean isComparable;
		status status;
	} Core;


	/***************************************************************************
	 * STRUCTURE
	 **************************************************************************/

	/**
	 * Defines the Structure.
	 */
	typedef struct Structure
	{
		Core core;
		type type;
		natural size;
		void* value;
	} Structure;


	/***************************************************************************
	 * BASIC
	 **************************************************************************/

	/**
	 * Defines the abstract layer Releasable.
	 */
	typedef struct Releasable
	{
		/***********************************************************************
		 * CORE
		 **********************************************************************/

		Core core;

		/***********************************************************************
		 * RELEASABLE
		 **********************************************************************/

		void (*release)(void* structure);
	} Releasable;

	/**************************************************************************/

	/**
	 * Defines the abstract layer Cloneable.
	 */
	typedef struct Cloneable
	{
		/***********************************************************************
		 * CORE
		 **********************************************************************/

		Core core;

		/***********************************************************************
		 * RELEASABLE
		 **********************************************************************/

		void (*release)(void* structure);

		/***********************************************************************
		 * CLONEABLE
		 **********************************************************************/

		void* (*clone)(const void* structure);
	} Cloneable;

	/**************************************************************************/

	/**
	 * Defines the abstract layer Equalable.
	 */
	typedef struct Equalable
	{
		/***********************************************************************
		 * CORE
		 **********************************************************************/

		Core core;

		/***********************************************************************
		 * RELEASABLE
		 **********************************************************************/

		void (*release)(void* structure);

		/***********************************************************************
		 * CLONEABLE
		 **********************************************************************/

		void* (*clone)(const void* structure);

		/***********************************************************************
		 * EQUALABLE
		 **********************************************************************/

		boolean(*equals)(const void* structure, const type type, const void* value);
	} Equalable;

	/**************************************************************************/

	/**
	 * Defines the abstract layer Hashable.
	 */
	typedef struct Hashable
	{
		/***********************************************************************
		 * CORE
		 **********************************************************************/

		Core core;

		/***********************************************************************
		 * RELEASABLE
		 **********************************************************************/

		void (*release)(void* structure);

		/***********************************************************************
		 * CLONEABLE
		 **********************************************************************/

		void* (*clone)(const void* structure);

		/***********************************************************************
		 * EQUALABLE
		 **********************************************************************/

		boolean(*equals)(const void* structure, const type type, const void* value);

		/***********************************************************************
		 * HASHABLE
		 **********************************************************************/

		integer(*hash)(const void* structure);
	} Hashable;

	/**************************************************************************/

	/**
	 * Defines the abstract layer Stringable.
	 */
	typedef struct Stringable
	{
		/***********************************************************************
		 * CORE
		 **********************************************************************/

		Core core;

		/***********************************************************************
		 * RELEASABLE
		 **********************************************************************/

		void (*release)(void* structure);

		/***********************************************************************
		 * CLONEABLE
		 **********************************************************************/

		void* (*clone)(const void* structure);

		/***********************************************************************
		 * EQUALABLE
		 **********************************************************************/

		boolean(*equals)(const void* structure, const type type, const void* value);

		/***********************************************************************
		 * HASHABLE
		 **********************************************************************/

		integer(*hash)(const void* structure);

		/***********************************************************************
		 * STRINGABLE
		 **********************************************************************/

		boolean(*toString)(const void* source, string target);
	} Stringable;

	/**************************************************************************/

	/**
	 * Defines the abstract layer Basic.
	 */
	typedef struct Basic
	{
		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);
	} Basic;


	/***************************************************************************
	 * COMPARABLE
	 **************************************************************************/

	/**
	 * Defines the abstract layer Comparable.
	 */
	typedef struct Comparable
	{
		/***********************************************************************
		 * BASIC
		 **********************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/***********************************************************************
		 * COMPARABLE
		 **********************************************************************/

		integer(*compareTo)(const void* structure, const type type, const void* value);
	} Comparable;


	/***************************************************************************
	 * NUMBER
	 **************************************************************************/

	/**
	 * Defines a Number.
	 */
	typedef struct Number
	{
		/***********************************************************************
		 * BASIC
		 **********************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/***********************************************************************
		 * COMPARABLE
		 **********************************************************************/

		integer(*compareTo)(const void* structure, const type type, const void* value);

		/***********************************************************************
		 * NUMBER
		 **********************************************************************/

		/**
		 * The array of digits.
		 */
		digit digits[_NUMBER_LENGTH_MAX];
		/**
		 * The number of digits (<= _NUMBER_LENGTH_MAX).
		 */
		natural length;
		/**
		 * The numerical base (<= _NUMBER_BASE_MAX).
		 */
		natural base;

		void (*changeBase)(void* number, const natural toBase);
		natural(*toDecimal)(const void* number);
		natural(*toNatural)(const void* number);
		void (*toZero)(void* number);
	} Number;


	/***************************************************************************
	 * OBJECT
	 **************************************************************************/

	/**
	 * Defines the Object.
	 */
	typedef struct Object
	{
		/***********************************************************************
		 * BASIC
		 **********************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/***********************************************************************
		 * COMPARABLE
		 **********************************************************************/

		integer(*compareTo)(const void* structure, const type type, const void* value);

		/***********************************************************************
		 * OBJECT
		 **********************************************************************/

		Structure structure;
	} Object;


	/***************************************************************************
	 * ITERATOR
	 **************************************************************************/

	/**
	 * Defines the Iterator (over an Iterable structure).
	 */
	typedef struct Iterator
	{
		/***********************************************************************
		 * BASIC
		 **********************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/***********************************************************************
		 * ITERATOR
		 **********************************************************************/

		natural length;
		Structure element;

		natural index;
		void* node;

		void* (*next)(struct Iterator* iterator);
	} Iterator;


	/***************************************************************************
	 * ITERABLE
	 **************************************************************************/

	/**
	 * Defines the abstract layer Iterable.
	 */
	typedef struct Iterable
	{
		/***********************************************************************
		 * BASIC
		 **********************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/***********************************************************************
		 * COMPARABLE
		 **********************************************************************/

		integer(*compareTo)(const void* structure, const type type, const void* value);

		/***********************************************************************
		 * ITERABLE
		 **********************************************************************/

		natural length;
		Structure element;

		boolean(*isEmpty)(const void* iterable);
		Iterator(*iterator)(const void* iterable);
	} Iterable;


	/***************************************************************************
	 * COLLECTION
	 **************************************************************************/

	/**
	 * Defines the abstract layer Collection.
	 */
	typedef struct Collection
	{
		/***********************************************************************
		 * BASIC
		 **********************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/***********************************************************************
		 * COMPARABLE
		 **********************************************************************/

		integer(*compareTo)(const void* structure, const type type, const void* value);

		/***********************************************************************
		 * ITERABLE
		 **********************************************************************/

		natural length;
		Structure element;

		boolean(*isEmpty)(const void* iterable);
		Iterator(*iterator)(const void* iterable);

		/***********************************************************************
		 * COLLECTION
		 **********************************************************************/

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
	} Collection;


	/***************************************************************************
	 * LIST
	 **************************************************************************/

	/**
	 * Defines the abstract layer List.
	 */
	typedef struct List
	{
		/***********************************************************************
		 * BASIC
		 **********************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/***********************************************************************
		 * COMPARABLE
		 **********************************************************************/

		integer(*compareTo)(const void* structure, const type type, const void* value);

		/***********************************************************************
		 * ITERABLE
		 **********************************************************************/

		natural length;
		Structure element;

		boolean(*isEmpty)(const void* iterable);
		Iterator(*iterator)(const void* iterable);

		/***********************************************************************
		 * COLLECTION
		 **********************************************************************/

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

		/***********************************************************************
		 * LIST
		 **********************************************************************/

		Structure(*get)(const void* list, const natural index);

		boolean(*removeAt)(void* list, const natural index);
	} List;


	/***************************************************************************
	 * ARRAY
	 **************************************************************************/

	/**
	 * Defines the Array.
	 */
	typedef struct Array
	{
		/***********************************************************************
		 * BASIC
		 **********************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/***********************************************************************
		 * COMPARABLE
		 **********************************************************************/

		integer(*compareTo)(const void* structure, const type type, const void* value);

		/***********************************************************************
		 * ITERABLE
		 **********************************************************************/

		natural length;
		Structure element;

		boolean(*isEmpty)(const void* iterable);
		Iterator(*iterator)(const void* iterable);

		/***********************************************************************
		 * COLLECTION
		 **********************************************************************/

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

		/***********************************************************************
		 * LIST
		 **********************************************************************/

		Structure(*get)(const void* list, const natural index);

		boolean(*removeAt)(void* list, const natural index);

		/***********************************************************************
		 * ARRAY
		 **********************************************************************/

		void* elements;
	} Array;


	/***************************************************************************
	 * ELEMENT
	 **************************************************************************/

	/**
	 * Defines the Element.
	 */
	typedef struct Element
	{
		natural index;
		void* pointer;
	} Element;


	/***************************************************************************
	 * I/O
	 **************************************************************************/

	/**
	 * Defines the I/O Message.
	 */
	typedef struct IOMessage
	{
		/***********************************************************************
		 * BASIC
		 **********************************************************************/

		Core core;

		void (*release)(void* structure);
		void* (*clone)(const void* structure);
		boolean(*equals)(const void* structure, const type type, const void* value);
		integer(*hash)(const void* structure);
		boolean(*toString)(const void* source, string target);

		/***********************************************************************
		 * I/O MESSAGE
		 **********************************************************************/

		IOType type;
		SeverityLevel level;
		string prefix;
		string content;

		void (*set)(struct IOMessage* thisIOMessage, const IOType type, const SeverityLevel level, const string filePath, const string functionName, const natural lineNumber, const string content);
	} IOMessage;


	/***************************************************************************
	 * POINTER
	 **************************************************************************/

	/**
	 * Defines the Pointer.
	 */
	typedef struct Pointer
	{
		void* value;
	} Pointer;


	/***************************************************************************
	 * PAIR
	 **************************************************************************/

	/**
	 * Defines the pair of pointers.
	 */
	typedef struct Pair
	{
		void* a;
		void* b;
	} Pair;

	/**
	 * Defines the pair of characters.
	 */
	typedef struct CPair
	{
		character a, b;
	} CPair;

	/**
	 * Defines the pair of digits.
	 */
	typedef struct DPair
	{
		digit a, b;
	} DPair;

	/**
	 * Defines the pair of integers.
	 */
	typedef struct IPair
	{
		integer a, b;
	} IPair;

	/**
	 * Defines the pair of natural numbers.
	 */
	typedef struct NPair
	{
		natural a, b;
	} NPair;

	/**
	 * Defines the pair of real numbers.
	 */
	typedef struct RPair
	{
		real a, b;
	} RPair;

	/**
	 * Defines the pair of Objects.
	 */
	typedef struct OPair
	{
		Object a, b;
	} OPair;


	/***************************************************************************
	 * TRIPLET
	 **************************************************************************/

	/**
	 * Defines the triplet of pointers.
	 */
	typedef struct Triplet
	{
		void* a;
		void* b;
		void* c;
	} Triplet;

	/**
	 * Defines the triplet of characters.
	 */
	typedef struct CTriplet
	{
		character a, b, c;
	} CTriplet;

	/**
	 * Defines the triplet of digits.
	 */
	typedef struct DTriplet
	{
		digit a, b, c;
	} DTriplet;

	/**
	 * Defines the triplet of integers.
	 */
	typedef struct ITriplet
	{
		integer a, b, c;
	} ITriplet;

	/**
	 * Defines the triplet of natural numbers.
	 */
	typedef struct NTriplet
	{
		natural a, b, c;
	} NTriplet;

	/**
	 * Defines the triplet of real numbers.
	 */
	typedef struct RTriplet
	{
		real a, b, c;
	} RTriplet;

	/**
	 * Defines the triplet of Objects.
	 */
	typedef struct OTriplet
	{
		Object a, b, c;
	} OTriplet;


	/***************************************************************************
	 * SIZES
	 **************************************************************************/

	/**
	 * Defines the size of the Cores.
	 */
	extern const natural CORE_SIZE;

	/**
	 * Defines the size of the Structures.
	 */
	extern const natural STRUCTURE_SIZE;

	/**
	 * Defines the size of the Basic structures.
	 */
	extern const natural BASIC_SIZE;

	/**
	 * Defines the size of the Comparable structures.
	 */
	extern const natural COMPARABLE_SIZE;


#endif /* _COMMON_STRUCTURES_H */
#ifdef __cplusplus
}
#endif
