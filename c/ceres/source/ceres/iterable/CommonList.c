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


/***************************************************************************************************
 * INCLUDES
 **************************************************************************************************/

#include "ceres/iterable/CommonList.h"


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void List_reset(void* list, const natural length, const type elementType, const natural elementSize, const natural initialSize)
{
	_PRINT_TEST(_S("<resetList>"));
	_IF (_CHECK(list, _LIST_NAME))
	{
		/* Get the List */
		List* l = (List*) list;

		/* INHERITANCE */
		Collection_reset(l, length, elementType, elementSize, initialSize);

		/* FUNCTIONS */
		/* - List */
		l->get = List_get;
		l->removeAt = NULL;
	}
	_PRINT_TEST(_S("</resetList>"));
}


/***************************************************************************************************
 * LIST
 **************************************************************************************************/

Structure List_get(const void* list, const natural index)
{
	_IF (_CHECK(list, _LIST_NAME))
	{
		/* Get the List */
		const List* l = (List*) list;
		/* Get the Structure of the elements of the List */
		Structure s = l->element;
		/* Declare the iteration variable(s) */
		Iterator it = l->iterator(l);
		Object* e;

		for (e = (Object*) it.next(&it);
			it.index < index;
			e = (Object*) it.next(&it));
		s.value = e;
		return s;
	}
	return _STRUCTURE_DEFAULT();
}
