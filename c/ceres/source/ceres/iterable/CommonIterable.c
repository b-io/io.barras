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

#include "ceres/iterable/CommonIterable.h"


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void Iterable_reset(void* iterable, const natural length, const type elementType, const natural elementSize)
{
	_PRINT_TEST(_S("<resetIterable>"));
	_IF (_CHECK(iterable, _ITERABLE_NAME))
	{
		/* Get the Iterable structure */
		Iterable* set = (Iterable*) iterable;

		/* FUNCTIONS */
		/* - Iterable */
		set->isEmpty = Iterable_isEmpty;
		set->iterator = NULL;
		/* - Comparable */
		set->compareTo = NULL;
		/* - Basic */
		set->release = NULL;
		set->clone = NULL;
		set->equals = NULL;
		set->hash = NULL;
		set->toString = NULL;

		/* ATTRIBUTES */
		/* - Iterable */
		set->length = length;
		set->element.core = Core_create(_FALSE, _TRUE, type_is_Basic(elementType), type_is_Comparable(elementType));
		set->element.type = elementType;
		set->element.size = elementSize;
		set->element.value = NULL;
	}
	_PRINT_TEST(_S("</resetIterable>"));
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

boolean Iterable_isEmpty(const void* iterable)
{
	_IF (_CHECK(iterable, _ITERABLE_NAME))
	{
		/* Get the Iterable structure */
		const Iterable* set = (Iterable*) iterable;

		return set->length == 0;
	}
	return _TRUE;
}
