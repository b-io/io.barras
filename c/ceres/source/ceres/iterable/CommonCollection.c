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

#include "ceres/iterable/CommonCollection.h"


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void Collection_reset(void* collection, const natural length, const type elementType, const natural elementSize, const natural initialSize)
{
	_PRINT_DEBUG(_S("<resetCollection>"));
	_IF (_CHECK(collection, _COLLECTION_NAME))
	{
		/* Get the Collection */
		Collection* c = (Collection*) collection;

		/* INHERITANCE */
		Iterable_reset(c, length, elementType, elementSize);

		/* FUNCTIONS */
		/* - Collection */
		c->add = NULL;
		c->addValue = Collection_add_value;
		c->addStructure = Collection_add_Structure;
		c->addAll = Collection_add_all;
		c->clear = NULL;
		c->contains = NULL;
		c->containsValue = Collection_contains_value;
		c->containsStructure = Collection_contains_Structure;
		c->containsAll = Collection_contains_all;
		c->count = NULL;
		c->countValue = Collection_count_value;
		c->countStructure = Collection_count_Structure;
		c->countAll = Collection_count_all;
		c->remove = NULL;
		c->removeValue = Collection_remove_value;
		c->removeStructure = Collection_remove_Structure;
		c->removeAll = Collection_remove_all;
		c->resize = NULL;
		/* - Comparable */
		c->compareTo = Collection_compare_to;
		/* - Basic */
		c->equals = Collection_equals;
		c->hash = Collection_hash;
		c->toString = Collection_to_string;

		/* ATTRIBUTES */
		/* - Collection */
		c->size = initialSize;
	}
	_PRINT_DEBUG(_S("</resetCollection>"));
}


/***************************************************************************************************
 * COLLECTION
 **************************************************************************************************/

boolean Collection_add_value(void* collection, void* value)
{
	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Collection */
		Collection* c = (Collection*) collection;

		return c->add(c, c->element.type, value);
	}
	return _FALSE;
}

boolean Collection_add_Structure(void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Collection */
		Collection* c = (Collection*) collection;

		return c->add(c, structure->type, structure->value);
	}
	return _FALSE;
}

boolean Collection_add_all(void* collection, const void* values)
{
	/* Declare the variable(s) */
	boolean isModified = _FALSE;

	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Collection and the Collection of values */
		Collection* c = (Collection*) collection;
		const Collection* vs = (Collection*) values;

		/* Resize the Collection if necessary */
		if (c->size >= c->length + vs->length
			|| c->resize(c, c->length + vs->length))
		{
			/* Declare the iteration variable(s) */
			Iterator it = vs->iterator(vs);

			/* Add the values to the Collection */
			while (it.next(&it))
			{
				if (c->addStructure(c, &it.element))
				{
					isModified = _TRUE;
				}
			}
		}
	}
	return isModified;
}

boolean Collection_add_all_and_resize(void* collection, const void* values)
{
	if (Collection_add_all(collection, values))
	{
		/* Get the Collection */
		Collection* c = (Collection*) collection;

		/* Resize the Collection */
		c->resize(c, c->length);
		return _TRUE;
	}
	return _FALSE;
}

/**************************************************************************************************/

boolean Collection_contains_value(const void* collection, const void* value)
{
	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Collection */
		Collection* c = (Collection*) collection;

		return c->contains(c, c->element.type, value);
	}
	return _FALSE;
}

boolean Collection_contains_Structure(const void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Collection */
		const Collection* c = (Collection*) collection;

		return c->contains(c, structure->type, structure->value);
	}
	return _FALSE;
}

boolean Collection_contains_all(const void* collection, const void* values)
{
	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Collection */
		const Collection* c = (Collection*) collection;
		/* Clone the values to be checked for containment */
		Collection* vs = (Collection*) ((Collection*) values)->clone(values);
		/* Declare the iteration variable(s) */
		Iterator it = c->iterator(c);

		while (it.next(&it))
		{
			if (vs->removeStructure(vs, &it.element)
				&& vs->isEmpty(vs))
			{
				_RELEASE(vs);
				return _TRUE;
			}
		}
		_RELEASE(vs);
	}
	return _FALSE;
}

/**************************************************************************************************/

natural Collection_count_value(const void* collection, const void* value)
{
	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Collection */
		const Collection* c = (Collection*) collection;

		return c->count(c, c->element.type, value);
	}
	return 0;
}

natural Collection_count_Structure(const void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Collection */
		const Collection* c = (Collection*) collection;

		return c->contains(c, structure->type, structure->value);
	}
	return 0;
}

natural Collection_count_all(const void* collection, const void* values)
{
	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Collection and the Collection of values */
		const Collection* c = (Collection*) collection;
		const Collection* vs = (Collection*) values;
		/* Declare the variable(s) */
		natural counter = 0;
		/* Declare the iteration variable(s) */
		Iterator it = c->iterator(c);

		while (it.next(&it))
		{
			if (vs->containsStructure(vs, &it.element))
			{
				++counter;
			}
		}
		return counter;
	}
	return 0;
}

/**************************************************************************************************/

boolean Collection_remove_value(void* collection, const void* value)
{
	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _CHECK(value, _S("value")))
	{
		/* Get the Collection */
		Collection* c = (Collection*) collection;

		return c->remove(c, c->element.type, value);
	}
	return _FALSE;
}

boolean Collection_remove_Structure(void* collection, const Structure* structure)
{
	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _STRUCTURE_CHECK(structure))
	{
		/* Get the Collection */
		Collection* c = (Collection*) collection;

		return c->remove(c, structure->type, structure->value);
	}
	return _FALSE;
}

boolean Collection_remove_all(void* collection, const void* values)
{
	/* Declare the variable(s) */
	boolean isModified = _FALSE;

	_IF (_CHECK(collection, _COLLECTION_NAME)
		&& _CHECK(values, _COLLECTION_NAME _S(" of values")))
	{
		/* Get the Collection and the Collection of values */
		Collection* c = (Collection*) collection;
		const Collection* vs = (Collection*) values;
		/* Declare the iteration variable(s) */
		Iterator it = c->iterator(c);

		while (it.next(&it))
		{
			if (vs->containsStructure(vs, &it.element)
				&& c->removeStructure(c, &it.element))
			{
				isModified = _TRUE;
			}
		}
	}
	return isModified;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable Collection_create_Comparable(void)
{
	return Comparable_create(NULL, NULL, Collection_equals, Collection_hash, Collection_to_string, Collection_compare_to);
}

integer Collection_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_COLLECTION_TYPE, structure, type, value)
		&& type_is_Iterable(type))
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the Collection and the Iterable structure */
			const Collection* c = (Collection*) structure;
			const Iterable* vs = (Iterable*) value;
			/* Declare the iteration variable(s) */
			natural i;
			Iterator it1 = c->iterator(c);
			const Object* e;
			Iterator it2 = vs->iterator(vs);
			integer comparison;
			/* Get the minimum length */
			const natural length = _MIN(it1.length, it2.length);

			for (i = 0, e = (Object*) it1.next(&it1), it2.next(&it2);
				i < length;
				++i, e = (Object*) it1.next(&it1), it2.next(&it2))
			{
				comparison = e->compareTo(e, it2.element.type, it2.element.value);
				if (comparison != 0)
				{
					return comparison;
				}
			}
			return _COMPARE_TO(it1.length, it2.length);
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

boolean Collection_equals(const void* structure, const type type, const void* value)
{
	return Collection_compare_to(structure, type, value) == 0;
}

/**************************************************************************************************/

integer Collection_hash(const void* structure)
{
	integer code;

	if (structure != NULL)
	{
		/* Get the Collection */
		const Collection* c = (Collection*) structure;
		/* Declare the iteration variable(s) */
		Iterator it = c->iterator(c);
		const Object* e;
		boolean isLeft = _TRUE;

		code = (integer) _COLLECTION_TYPE;
		while (e = (Object*) it.next(&it))
		{
			if (isLeft)
			{
				code = bits_rotate_left((natural) code, _THIRD_BITS_NUMBER);
			}
			else
			{
				code = bits_rotate_right((natural) code, _EIGHTH_BITS_NUMBER);
			}
			code ^= e->hash(e);
			isLeft = !isLeft;
		}
	}
	else
	{
		code = integer_random();
	}
	return code;
}

/**************************************************************************************************/

boolean Collection_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		boolean isNotFull;
		/* Get the Collection */
		const Collection* c = (Collection*) source;
		/* Declare the iteration variable(s) */
		Iterator it = c->iterator(c);
		Object* e;

		isNotFull = string_to_string(_S("("), target);
		if (isNotFull)
		{
			if (e = (Object*) it.next(&it))
			{
				isNotFull = Object_append_to_string(e, target);
				while (isNotFull && (e = (Object*) it.next(&it)))
				{
					isNotFull = string_append_to_string(_S(", "), target)
						&& Object_append_to_string(e, target);
				}
			}
			isNotFull = isNotFull && string_append_to_string(_S(")"), target);
		}
		return isNotFull;
	}
	return _FALSE;
}

boolean Collection_append_to_string(const void* source, string target)
{
	string buffer;

	Collection_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
