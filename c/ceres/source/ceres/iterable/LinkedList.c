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

#include "ceres/iterable/LinkedList.h"


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

LinkedList* LinkedList_new(void)
{
	LinkedList* ll = _NEW(LinkedList);

	_PRINT_DEBUG(_S("<newLinkedList"));
	if (ll != NULL)
	{
		ll->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		ll->length = 0;
		ll->size = 0;
		ll->first = NULL;
		ll->last = NULL;
		LinkedList_reset(ll);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_LINKED_LIST_NAME);
	}
	_PRINT_DEBUG(_S("</newLinkedList"));
	return ll;
}

/**************************************************************************************************/

LinkedNode* LinkedNode_new_from_Structure(const Structure* structure)
{
	LinkedNode* node = _NEW(LinkedNode);

	if (node != NULL)
	{
		node->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		LinkedNode_reset(node, structure);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_LINKED_NODE_NAME);
	}
	return node;
}

LinkedNode* LinkedNode_new_from_Object(const Object* value)
{
	LinkedNode* node = _NEW(LinkedNode);

	if (node != NULL)
	{
		node->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		/* - Linked Node */
		node->previous = NULL;
		node->next = NULL;
		/* - Object */
		*((Object*) node) = *value;
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_LINKED_NODE_NAME);
	}
	return node;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void LinkedList_reset(void* linkedList)
{
	_PRINT_DEBUG(_S("<resetLinkedList>"));
	_IF (_CHECK(linkedList, _LINKED_LIST_NAME))
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) linkedList;

		/* INHERITANCE */
		List_reset(ll, ll->length, _OBJECT_TYPE, OBJECT_SIZE, ll->size);

		/* FUNCTIONS */
		/* - List */
		ll->get = LinkedList_get;
		ll->removeAt = LinkedList_remove_at;
		/* - Collection */
		ll->add = LinkedList_add;
		ll->addValue = LinkedList_add_value;
		ll->addStructure = LinkedList_add_Structure;
		ll->clear = LinkedList_clear;
		ll->contains = LinkedList_contains;
		ll->containsStructure = LinkedList_contains_Structure;
		ll->count = LinkedList_count;
		ll->countStructure = LinkedList_count_Structure;
		ll->remove = LinkedList_remove;
		ll->removeStructure = LinkedList_remove_Structure;
		ll->removeAll = LinkedList_remove_all;
		ll->resize = LinkedList_resize;
		/* - Iterable */
		ll->iterator = LinkedList_iterator;
		/* - Comparable */
		ll->compareTo = LinkedList_compare_to;
		/* - Basic */
		ll->release = LinkedList_release;
		ll->clone = LinkedList_clone;
		ll->equals = LinkedList_equals;
		ll->toString = LinkedList_to_string;

		/* ATTRIBUTES */
		/* Release the Linked Nodes */
		ll->clear(ll);
	}
	_PRINT_DEBUG(_S("</resetLinkedList>"));
}

/**************************************************************************************************/

void LinkedNode_reset(void* linkedNode, const Structure* structure)
{
	_PRINT_DEBUG(_S("<resetLinkedNode>"));
	_IF (_CHECK(linkedNode, _LINKED_NODE_NAME))
	{
		/* Get the Linked Node */
		LinkedNode* node = (LinkedNode*) linkedNode;

		/* INHERITANCE */
		Object_reset(node, structure);

		/* ATTRIBUTES */
		/* - LinkedNode */
		node->previous = NULL;
		node->next = NULL;
	}
	_PRINT_DEBUG(_S("</resetLinkedNode>"));
}


/***************************************************************************************************
 * LINKED LIST
 **************************************************************************************************/

boolean LinkedList_insert_between(LinkedList* linkedList, LinkedNode* node, LinkedNode* previousNode, LinkedNode* nextNode)
{
	_IF (_CHECK(linkedList, _LINKED_LIST_NAME)
		&& _CHECK(node, _LINKED_NODE_NAME))
	{
		if (previousNode == NULL && nextNode == NULL)
		{
			linkedList->first = node;
			linkedList->last = node;
		}
		else if (previousNode == NULL)
		{
			linkedList->first = node;
			/* NULL o->o ... */
			node->next = nextNode;
			/* NULL o<-o ... */
			nextNode->previous = node;
		}
		else if (nextNode == NULL)
		{
			linkedList->last = node;
			/* ... o->o NULL */
			previousNode->next = node;
			/* ... o<-o NULL */
			node->previous = previousNode;
		}
		else
		{
			/* ... o->o o ... */
			previousNode->next = node;
			/* ... o<-o o ... */
			node->previous = previousNode;
			/* ... o o->o ... */
			node->next = nextNode;
			/* ... o o<-o ... */
			nextNode->previous = node;
		}
		return _TRUE;
	}
	return _FALSE;
}

boolean LinkedList_insert_last(LinkedList* linkedList, LinkedNode* node)
{
	_IF (_CHECK(linkedList, _LINKED_LIST_NAME)
		&& _CHECK(node, _LINKED_NODE_NAME))
	{
		/* Get the last Linked Node */
		LinkedNode* lastNode = linkedList->last;

		if (lastNode == NULL)
		{
			linkedList->first = node;
		}
		else
		{
			lastNode->next = node;
			node->previous = lastNode;
		}
		linkedList->last = node;
		++linkedList->length;
		linkedList->size = linkedList->length;
		return _TRUE;
	}
	return _FALSE;
}

void LinkedList_extract(LinkedList* linkedList, LinkedNode* node)
{
	_IF (_CHECK(linkedList, _LINKED_LIST_NAME)
		&& _CHECK(node, _LINKED_NODE_NAME))
	{
		if (node->previous == NULL && node->next == NULL)
		{
			linkedList->first = NULL;
			linkedList->last = NULL;
		}
		else if (node->previous == NULL)
		{
			linkedList->first = node->next;
			/* NULL<---o ... */
			node->next->previous = NULL;
		}
		else if (node->next == NULL)
		{
			linkedList->last = node->previous;
			/* ... o--->NULL */
			node->previous->next = NULL;
		}
		else
		{
			/* ... o--->o ... */
			node->previous->next = node->next;
			/* ... o<---o ... */
			node->next->previous = node->previous;
		}
		--linkedList->length;
		linkedList->size = linkedList->length;
	}
}

LinkedNode* LinkedList_get_LinkedNode(LinkedList* linkedList, const natural index)
{
	_IF (_CHECK(linkedList, _LINKED_LIST_NAME))
	{
		if (index < linkedList->length)
		{
			/* Declare the iteration variable(s) */
			natural i;
			LinkedNode* node;

			for (i = 0, node = linkedList->first; i < index; ++i, node = node->next);
			return node;
		}
		else
		{
			_PRINT_ERROR_INDEX(index, linkedList->length);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * LIST
 **************************************************************************************************/

Structure LinkedList_get(const void* list, const natural index)
{
	_IF (_CHECK(list, _LINKED_LIST_NAME))
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) list;
		/* Get the Linked Node */
		LinkedNode* node = LinkedList_get_LinkedNode(ll, index);

		if (node != NULL)
		{
			return node->structure;
		}
	}
	return _STRUCTURE_DEFAULT();
}

boolean LinkedList_remove_at(void* list, const natural index)
{
	_IF (_CHECK(list, _LINKED_LIST_NAME))
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) list;
		/* Get the Linked Node */
		LinkedNode* node = LinkedList_get_LinkedNode(ll, index);

		if (node != NULL)
		{
			LinkedList_extract(ll, node);
			LinkedNode_release(node);
			return _TRUE;
		}
	}
	return _FALSE;
}


/***************************************************************************************************
 * COLLECTION
 **************************************************************************************************/

boolean LinkedList_add(void* collection, const type type, void* value)
{
	_IF (_CHECK(collection, _LINKED_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) collection;
		/* Declare the Linked Node */
		LinkedNode* node;

		/* Create the Linked Node to be inserted */
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
		return LinkedList_insert_last(ll, node);
	}
	return _FALSE;
}

boolean LinkedList_add_value(void* collection, void* value)
{
	_IF (_CHECK(collection, _LINKED_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) collection;
		/* Create the Linked Node to be inserted */
		LinkedNode* node = LinkedNode_new_from_Object((Object*) value);

		return LinkedList_insert_last(ll, node);
	}
	return _FALSE;
}

boolean LinkedList_add_Structure(void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _LINKED_LIST_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) collection;
		/* Create the Linked Node to be inserted */
		LinkedNode* node = LinkedNode_new_from_Structure(structure);

		return LinkedList_insert_last(ll, node);
	}
	return _FALSE;
}

/**************************************************************************************************/

void LinkedList_clear(void* collection)
{
	_PRINT_DEBUG(_S("<clearLinkedList>"));
	_IF (_CHECK(collection, _LINKED_LIST_NAME))
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) collection;
		/* Declare the iteration variable(s) */
		LinkedNode* node;
		LinkedNode* nextNode;

		/* Release the Linked Nodes */
		for (node = ll->first; node != NULL; node = nextNode)
		{
			nextNode = node->next;
			LinkedNode_release(node);
		}
		ll->length = 0;
		ll->size = 0;
		ll->first = NULL;
		ll->last = NULL;
	}
	_PRINT_DEBUG(_S("</clearLinkedList>"));
}

/**************************************************************************************************/

boolean LinkedList_contains(const void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _LINKED_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Linked List */
		const LinkedList* ll = (LinkedList*) collection;
		/* Declare the iteration variable(s) */
		const LinkedNode* node;

		for (node = ll->first; node != NULL; node = node->next)
		{
			if (node->equals(node, type, value))
			{
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean LinkedList_contains_Structure(const void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _LINKED_LIST_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Linked List */
		const LinkedList* ll = (LinkedList*) collection;
		/* Declare the iteration variable(s) */
		const LinkedNode* node;

		for (node = ll->first; node != NULL; node = node->next)
		{
			if (Structures_equals(structure, &node->structure))
			{
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

natural LinkedList_count(const void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _LINKED_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Linked List */
		const LinkedList* ll = (LinkedList*) collection;
		/* Declare the variable(s) */
		natural counter = 0;
		/* Declare the iteration variable(s) */
		const LinkedNode* node;

		for (node = ll->first; node != NULL; node = node->next)
		{
			if (node->equals(node, type, value))
			{
				++counter;
			}
		}
		return counter;
	}
	return 0;
}

natural LinkedList_count_Structure(const void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _LINKED_LIST_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Linked List */
		const LinkedList* ll = (LinkedList*) collection;
		/* Declare the variable(s) */
		natural counter = 0;
		/* Declare the iteration variable(s) */
		const LinkedNode* node;

		for (node = ll->first; node != NULL; node = node->next)
		{
			if (Structures_equals(structure, &node->structure))
			{
				++counter;
			}
		}
		return counter;
	}
	return 0;
}

/**************************************************************************************************/

boolean LinkedList_remove(void* collection, const type type, const void* value)
{
	_IF (_CHECK(collection, _LINKED_LIST_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) collection;
		/* Declare the iteration variable(s) */
		LinkedNode* node;

		for (node = ll->first; node != NULL; node = node->next)
		{
			if (node->equals(node, type, value))
			{
				LinkedList_extract(ll, node);
				_RELEASE(node);
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean LinkedList_remove_Structure(void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _LINKED_LIST_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) collection;
		/* Declare the iteration variable(s) */
		LinkedNode* node;

		for (node = ll->first; node != NULL; node = node->next)
		{
			if (Structures_equals(structure, &node->structure))
			{
				LinkedList_extract(ll, node);
				LinkedNode_release(node);
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

boolean LinkedList_remove_all(void* collection, const void* values)
{
	/* Declare the variable(s) */
	boolean isModified = _FALSE;

	_IF (_CHECK(collection, _LINKED_LIST_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Linked List and the Collection of values */
		LinkedList* ll = (LinkedList*) collection;
		const Collection* vs = (Collection*) values;
		/* Declare the iteration variable(s) */
		LinkedNode* node;
		LinkedNode* nextNode;

		for (node = ll->first; node != NULL; node = nextNode)
		{
			nextNode = node->next;
			if (vs->containsStructure(vs, &node->structure))
			{
				isModified = _TRUE;
				LinkedList_extract(ll, node);
				LinkedNode_release(node);
			}
		}
	}
	return isModified;
}

/**************************************************************************************************/

boolean LinkedList_resize(void* collection, const natural size)
{
	_PRINT_DEBUG(_S("<resizeLinkedList>"));
	_IF (_CHECK(collection, _LINKED_LIST_NAME))
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) collection;

		/* Release the surplus Linked Nodes */
		if (ll->length > size)
		{
			if (size == 0)
			{
				ll->clear(ll);
			}
			else
			{
				/* Declare the iteration variable(s) */
				LinkedNode* lastNode;

				lastNode = LinkedList_get_LinkedNode(ll, size - 1);
				ll->last = lastNode;
				if (size == 1)
				{
					ll->first = lastNode;
				}
				if (lastNode != NULL)
				{
					/* Declare the iteration variable(s) */
					LinkedNode* node;
					LinkedNode* nextNode;

					for (node = lastNode->next; node != NULL; node = nextNode)
					{
						nextNode = node->next;
						LinkedNode_release(node);
					}
					lastNode->next = NULL;
				}
				ll->length = size;
				ll->size = size;
			}
		}
		_PRINT_DEBUG(_S("</resizeLinkedList>"));
		return _TRUE;
	}
	_PRINT_DEBUG(_S("</resizeLinkedList>"));
	return _FALSE;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

Iterator LinkedList_iterator(const void* iterable)
{
	_IF (_CHECK(iterable, _LINKED_LIST_NAME))
	{
		/* Get the Linked List */
		const LinkedList* ll = (LinkedList*) iterable;

		return Iterator_create(ll->length, ll->element.type, ll->element.size, ll->first, LinkedNode_Iterator_next);
	}
	return _ITERATOR_DEFAULT;
}

/**************************************************************************************************/

void* LinkedList_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			LinkedList* node = (LinkedList*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}

void* LinkedNode_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			LinkedNode* node = (LinkedNode*) iterator->node;

			iterator->element.value = node;
			++iterator->index;
			iterator->node = node->next;
			return iterator->element.value;
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable LinkedList_create_Comparable(void)
{
	return Comparable_create(LinkedList_release, LinkedList_clone, LinkedList_equals, Collection_hash, LinkedList_to_string, LinkedList_compare_to);
}

integer LinkedList_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_LINKED_LIST_TYPE, structure, type, value)
		&& type_is_Iterable(type))
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the Linked List and the Iterable structure */
			const LinkedList* ll = (LinkedList*) structure;
			const Iterable* vs = (Iterable*) value;
			/* Declare the iteration variable(s) */
			natural i;
			const LinkedNode* node;
			Iterator it = vs->iterator(vs);
			integer comparison;
			/* Get the minimum length */
			const natural length = _MIN(ll->length, it.length);

			for (i = 0, node = ll->first, it.next(&it);
				i < length;
				++i, node = node->next, it.next(&it))
			{
				comparison = node->compareTo(node, it.element.type, it.element.value);
				if (comparison != 0)
				{
					return comparison;
				}
			}
			return _COMPARE_TO(ll->length, it.length);
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void LinkedList_release(void* structure)
{
	_PRINT_DEBUG(_S("<releaseLinkedList>"));
	if (structure != NULL)
	{
		/* Get the Linked List */
		LinkedList* ll = (LinkedList*) structure;

		/* Release the Linked Nodes */
		ll->clear(ll);
		/* Free the Linked List */
		if (ll->core.isDynamic)
		{
			_FREE(ll);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_LINKED_LIST_NAME);
	}
	_PRINT_DEBUG(_S("</releaseLinkedList>"));
}

void LinkedNode_release(void* structure)
{
	_PRINT_DEBUG(_S("<releaseLinkedNode>"));
	if (structure != NULL)
	{
		/* Get the Linked Node */
		LinkedNode* node = (LinkedNode*) structure;

		/* Release the Linked Node */
		_RELEASE(node);
	}
	else
	{
		_PRINT_WARNING_NULL(_LINKED_NODE_NAME);
	}
	_PRINT_DEBUG(_S("</releaseLinkedNode>"));
}

/**************************************************************************************************/

void* LinkedList_clone(const void* structure)
{
	_IF (_CHECK(structure, _LINKED_LIST_NAME))
	{
		/* Get the Linked List */
		const LinkedList* ll = (LinkedList*) structure;
		/* Construct the clone dynamically */
		LinkedList* clone = LinkedList_new();

		clone->addAll(clone, ll);
		return clone;
	}
	return NULL;
}

/**************************************************************************************************/

boolean LinkedList_equals(const void* structure, const type type, const void* value)
{
	return LinkedList_compare_to(structure, type, value) == 0;
}

/**************************************************************************************************/

boolean LinkedList_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		boolean isNotFull;
		/* Get the Linked List */
		const LinkedList* ll = (LinkedList*) source;
		/* Declare the iteration variable(s) */
		LinkedNode* node;

		isNotFull = string_to_string(_S("("), target);
		if (isNotFull)
		{
			if (node = ll->first)
			{
				isNotFull = Object_append_to_string(node, target);
				while (isNotFull && (node = node->next))
				{
					isNotFull = string_append_to_string(_S(", "), target)
						&& Object_append_to_string(node, target);
				}
			}
			isNotFull = isNotFull && string_append_to_string(_S(")"), target);
		}
		return isNotFull;
	}
	return _FALSE;
}

boolean LinkedList_append_to_string(const void* source, string target)
{
	string buffer;

	LinkedList_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
