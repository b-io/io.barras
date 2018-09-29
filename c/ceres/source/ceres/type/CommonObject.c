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

#include "ceres/type/CommonObject.h"


/***************************************************************************************************
 * CONSTANTS
 **************************************************************************************************/

const size OBJECT_SIZE = sizeof (Object);


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

Object Object_create(const Structure* structure)
{
	Object o;

	o.core = Core_create(_FALSE, _FALSE, _TRUE, _TRUE);
	Object_reset(&o, structure);
	return o;
}

Object* Object_new(const Structure* structure)
{
	Object* o = _NEW(Object);

	_PRINT_DEBUG(_S("<newObject>"));
	if (o != NULL)
	{
		o->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		Object_reset(o, structure);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_OBJECT_NAME);
	}
	_PRINT_DEBUG(_S("</newObject>"));
	return o;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void Object_reset(void* object, const Structure* structure)
{
	_PRINT_DEBUG(_S("<resetObject>"));
	_IF (_CHECK(object, _OBJECT_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Object */
		Object* o = (Object*) object;

		if (structure->type == _OBJECT_TYPE)
		{
			/* Get the encapsulated Object */
			const Object* object = (Object*) structure->value;

			/* FUNCTIONS */
			/* - Comparable */
			o->compareTo = object->compareTo;
			/* - Basic */
			o->release = object->release;
			o->clone = object->clone;
			o->hash = object->hash;
			o->equals = object->equals;
			o->toString = object->toString;

			/* ATTRIBUTES */
			/* - Object */
			o->structure = object->structure;
		}
		else
		{
			/* FUNCTIONS */
			/* - Comparable */
			o->compareTo = Object_compare_to;
			/* - Basic */
			o->release = Object_release;
			o->clone = Object_clone;
			o->hash = Object_hash;
			o->equals = Object_equals;
			o->toString = Object_to_string;

			/* ATTRIBUTES */
			/* - Object */
			o->structure = *structure;
		}
	}
	_PRINT_DEBUG(_S("</resetObject>"));
}

Object* Object_leaf(const void* object)
{
	_IF (_CHECK(object, _OBJECT_NAME))
	{
		/* Get the Object */
		Object* o = (Object*) object;

		while (o->structure.type == _OBJECT_TYPE)
		{
			o = (Object*) o->structure.value;
		}
		return o;
	}
	return NULL;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* Object_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			Object* node = (Object*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable Object_create_Comparable(void)
{
	return Comparable_create(Object_release, Object_clone, Object_equals, Object_hash, Object_to_string, Object_compare_to);
}

integer Object_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_OBJECT_TYPE, structure, type, value))
	{
		/* Get the Object */
		const Object* o1 = Object_leaf(structure);

		if (type == _OBJECT_TYPE)
		{
			/* Get the other Object */
			const Object* o2 = Object_leaf(value);

			if (o1 == o2)
			{
				return 0;
			}
			else
			{
				return Structures_compare_to(&o1->structure, &o2->structure);
			}
		}
		else
		{
			return compare_to(o1->structure.type, o1->structure.value, type, value);
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void Object_release(void* structure)
{
	_PRINT_DEBUG(_S("<releaseObject>"));
	if (structure != NULL)
	{
		/* Get the Object */
		Object* o = Object_leaf(structure);

		/* Release the value if it is Basic, or free it if it is an array */
		release(&o->structure);
		/* Free the Object */
		if (o->core.isDynamic)
		{
			_FREE(o);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_OBJECT_NAME);
	}
	_PRINT_DEBUG(_S("</releaseObject>"));
}

/**************************************************************************************************/

void* Object_clone(const void* structure)
{
	_IF (_CHECK(structure, _OBJECT_NAME))
	{
		/* Get the Object */
		const Object* o = Object_leaf(structure);

		/* Construct the clone dynamically */
		return Object_new(&o->structure);
	}
	return NULL;
}

/**************************************************************************************************/

boolean Object_equals(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_OBJECT_TYPE, structure, type, value))
	{
		/* Get the Object */
		const Object* o1 = Object_leaf(structure);

		if (type == _OBJECT_TYPE)
		{
			/* Get the other Object */
			const Object* o2 = Object_leaf(value);

			if (o1 == o2)
			{
				return _TRUE;
			}
			else
			{
				return Structures_equals(&o1->structure, &o2->structure);
			}
		}
		else
		{
			return equals(o1->structure.type, o1->structure.value, type, value);
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

integer Object_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the Object */
		const Object* o = Object_leaf(structure);

		return bits_hash(2, _OBJECT_TYPE, hash(&o->structure));
	}
	return integer_random();
}

/**************************************************************************************************/

boolean Object_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the Object */
		const Object* o = Object_leaf(source);

		if (type_is_array(o->structure.type))
		{
			return array_to_string(&o->structure, target);
		}
		else
		{
			return to_string(o->structure.value, o->structure.type, target);
		}
	}
	return _FALSE;
}

boolean Object_append_to_string(const void* source, string target)
{
	string buffer;

	Object_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
