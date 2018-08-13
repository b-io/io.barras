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

#include "ceres/time/FormattedTime.h"


/***************************************************************************************************
 * CONSTANTS
 **************************************************************************************************/

const natural FORMATTED_TIME_SIZE = sizeof (FormattedTime);


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

FormattedTime FormattedTime_create(const timestamp* stamp, const string format)
{
	FormattedTime ft;

	ft.core = Core_create(_FALSE, _FALSE, _TRUE, _TRUE);
	FormattedTime_reset(&ft, stamp, format);
	return ft;
}

FormattedTime* FormattedTime_new(const timestamp* stamp, const string format)
{
	FormattedTime* ft = _NEW(FormattedTime);

	_PRINT_TEST(_S("<newFormattedTime>"));
	if (ft != NULL)
	{
		ft->core = Core_create(_TRUE, _FALSE, _TRUE, _TRUE);
		FormattedTime_reset(ft, stamp, format);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_FORMATTED_TIME_NAME);
	}
	_PRINT_TEST(_S("</newFormattedTime>"));
	return ft;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void FormattedTime_reset(void* formattedTime, const timestamp* stamp, const string format)
{
	_PRINT_TEST(_S("<resetFormattedTime>"));
	_IF (_CHECK(formattedTime, _FORMATTED_TIME_NAME))
	{
		/* Get the Formatted Time */
		FormattedTime* ft = (FormattedTime*) formattedTime;

		/* FUNCTIONS */
		/* - Comparable */
		ft->compareTo = FormattedTime_compare_to;
		/* - Basic */
		ft->release = FormattedTime_release;
		ft->clone = FormattedTime_clone;
		ft->equals = FormattedTime_equals;
		ft->hash = FormattedTime_hash;
		ft->toString = FormattedTime_to_string;

		/* ATTRIBUTES */
		/* - FormattedTime */
		ft->time = (stamp == NULL) ? timestamp_get() : *stamp;
		ft->utc = Time_utc(&ft->time);
		ft->local = Time_local(&ft->time);
		string_to_string((format == NULL) ? _TIME_FORMAT : format, ft->format);
	}
	_PRINT_TEST(_S("</resetFormattedTime>"));
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* FormattedTime_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			FormattedTime* node = (FormattedTime*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable FormattedTime_create_Comparable(void)
{
	return Comparable_create(FormattedTime_release, FormattedTime_clone, FormattedTime_equals, FormattedTime_hash, FormattedTime_to_string, FormattedTime_compare_to);
}

integer FormattedTime_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_FORMATTED_TIME_TYPE, structure, type, value)
		&& type == _FORMATTED_TIME_TYPE)
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the Formatted Times */
			const FormattedTime* ft1 = (FormattedTime*) structure;
			const FormattedTime* ft2 = (FormattedTime*) value;

			return Time_compare_to(&ft1->time, _TIME_TYPE, &ft2->time);
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void FormattedTime_release(void* structure)
{
	_PRINT_TEST(_S("<releaseFormattedTime>"));
	if (structure != NULL)
	{
		/* Get the Formatted Time */
		FormattedTime* ft = (FormattedTime*) structure;

		/* Free the Formatted Time */
		if (ft->core.isDynamic)
		{
			_FREE(ft);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_FORMATTED_TIME_NAME);
	}
	_PRINT_TEST(_S("</releaseFormattedTime>"));
}

/**************************************************************************************************/

void* FormattedTime_clone(const void* structure)
{
	_IF (_CHECK(structure, _FORMATTED_TIME_NAME))
	{
		/* Get the Formatted Time */
		const FormattedTime* ft = (FormattedTime*) structure;

		/* Construct the clone dynamically */
		return FormattedTime_new(&ft->time, ft->format);
	}
	return NULL;
}

/**************************************************************************************************/

integer FormattedTime_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the Formatted Time */
		const FormattedTime* ft = (FormattedTime*) structure;
		/* Get the string */
		string s;

		ft->toString(ft, s);
		return bits_hash(2, _FORMATTED_TIME_TYPE, string_hash(s));
	}
	return integer_random();
}

/**************************************************************************************************/

boolean FormattedTime_equals(const void* structure, const type type, const void* value)
{
	return FormattedTime_compare_to(structure, type, value) == 0;
}

/**************************************************************************************************/

boolean FormattedTime_to_string(const void* source, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the Formatted Time */
		const FormattedTime* formattedTime = (FormattedTime*) source;

		return Time_format(&formattedTime->local, formattedTime->format, target);
	}
	return _FALSE;
}

boolean FormattedTime_append_to_string(const void* source, string target)
{
	string buffer;

	FormattedTime_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
