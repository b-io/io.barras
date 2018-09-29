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
/***************************************************************************************************
 * INCLUDES
 **************************************************************************************************/

#include "ceres/iterable/SortedList.h"


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

SortedList* SortedList_new(void)
{
	SortedList* sl = _NEW(SortedList);

	_PRINT_DEBUG(_S("<newSortedList>"));
	if (sl != NULL)
	{
		sl->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		sl->length = 0;
		sl->size = 0;
		sl->first = NULL;
		sl->last = NULL;
		SortedList_reset(sl);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_SORTED_LIST_NAME);
	}
	_PRINT_DEBUG(_S("</newSortedList>"));
	return sl;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void SortedList_reset(void* sortedList)
{
	_PRINT_DEBUG(_S("<resetSortedList>"));
	_IF (_CHECK(sortedList, _SORTED_LIST_NAME))
	{
		/* Get the Sorted List */
		SortedList* sl = (SortedList*) sortedList;

		/* INHERITANCE */
		LinkedList_reset(sl);

		/* FUNCTIONS */
		/* - Collection */
		sl->add = SortedList_add;
		sl->addValue = SortedList_add_value;
		sl->addStructure = SortedList_add_Structure;
	}
	_PRINT_DEBUG(_S("</resetSortedList>"));
}


/***************************************************************************************************
 * SORTED LIST
 **************************************************************************************************/

boolean SortedList_insert(SortedList* sortedList, SortedNode* node)
{
	_IF (_CHECK(sortedList, _SORTED_LIST_NAME)
		&& _CHECK(node, _SORTED_NODE_NAME))
	{
		if (sortedList->first == NULL)
		{
			_PRINT_DEBUG(_S("first and last"));
			sortedList->first = node;
			sortedList->last = node;
		}
		else if (node->compareTo(node, _OBJECT_TYPE, sortedList->last) >= 0)
		{
			_PRINT_DEBUG(_S("last"));
			/* ... o->o NULL */
			sortedList->last->next = node;
			/* ... o<-o NULL */
			node->previous = sortedList->last;
			/* Set the last node */
			sortedList->last = node;
		}
		else
		{
			/* Get the first Sorted Node */
			SortedNode* current = sortedList->first;

			do
			{
				if (node->compareTo(node, _OBJECT_TYPE, current) <= 0)
				{
					SortedNode* previous;

					previous = current->previous;
					if (previous == NULL)
					{
						_PRINT_DEBUG(_S("first"));
						/* NULL o->o ... */
						node->next = current;
						/* NULL o<-o ... */
						current->previous = node;
						/* Set the first node */
						sortedList->first = node;
					}
					else
					{
						_PRINT_DEBUG(_S("inside"));
						/* ... o->o o ... */
						previous->next = node;
						/* ... o<-o o ... */
						node->previous = previous;
						/* ... o o->o ... */
						node->next = current;
						/* ... o o<-o ... */
						current->previous = node;
					}
					break;
				}
				current = current->next;
			}
			while (_TRUE);
		}
		++sortedList->length;
		sortedList->size = sortedList->length;
		return _TRUE;
	}
	return _FALSE;
}


/***************************************************************************************************
 * COLLECTION
 **************************************************************************************************/

boolean SortedList_add(void* collection, const type type, void* value)
{
	_IF (_CHECK(collection, _SORTED_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Sorted List */
		SortedList* sl = (SortedList*) collection;
		/* Declare the Sorted Node */
		SortedNode* node;

		/* Create the Sorted Node to be inserted */
		if (type == _OBJECT_TYPE)
		{
			node = LinkedNode_new_from_Object((Object*) value);
		}
		else if (type == _STRUCTURE_TYPE)
		{
			node = LinkedNode_new_from_Structure((Structure*) value);
		}
		else
		{
			const Structure s = Structure_create(type, value);

			node = LinkedNode_new_from_Structure(&s);
		}
		return SortedList_insert(sl, node);
	}
	return _FALSE;
}

boolean SortedList_add_value(void* collection, void* value)
{
	_IF (_CHECK(collection, _SORTED_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Sorted List */
		SortedList* sl = (SortedList*) collection;
		/* Create the Sorted Node to be inserted */
		SortedNode* node = LinkedNode_new_from_Object(value);

		return SortedList_insert(sl, node);
	}
	return _FALSE;
}

boolean SortedList_add_Structure(void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _SORTED_LIST_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Sorted List */
		SortedList* sl = (SortedList*) collection;
		/* Create the Sorted Node to be inserted */
		SortedNode* node = LinkedNode_new_from_Structure(structure);

		return SortedList_insert(sl, node);
	}
	return _FALSE;
}
