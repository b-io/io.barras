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

#include "ceres/time/CommonTime.h"


/***************************************************************************************************
 * CONSTANTS
 **************************************************************************************************/

const size TIME_SIZE = sizeof (Time);


/***************************************************************************************************
 * TIME
 **************************************************************************************************/

timestamp timestamp_get(void)
{
	timestamp ts;

	time(&ts);
	return ts;
}

/**************************************************************************************************/

Time Time_utc(const timestamp* stamp)
{
	Time t;

	if (stamp == NULL)
	{
		timestamp ts = timestamp_get();

		_GM_TIME(&t, &ts);
	}
	else
	{
		_GM_TIME(&t, stamp);
	}
	return t;
}

Time Time_local(const timestamp* stamp)
{
	Time t;

	if (stamp == NULL)
	{
		timestamp ts = timestamp_get();

		_LOCAL_TIME(&t, &ts);
	}
	else
	{
		_LOCAL_TIME(&t, stamp);
	}
	return t;
}

/**************************************************************************************************/

boolean Time_format(const void* source, const string format, string target)
{
	_IF (_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the Time */
		Time* t = (Time*) source;

#ifdef _WIDE_STRING
		wcsftime(target, _STRING_SIZE, format, t);
#else
		strftime(target, _STRING_SIZE, format, t);
#endif
		return _TRUE;
	}
	return _FALSE;
}


/***************************************************************************************************
 * ITERABLE
 **************************************************************************************************/

void* Time_Iterator_next(Iterator* iterator)
{
	_IF (_CHECK(iterator, _ITERATOR_NAME))
	{
		if (iterator->node != NULL && iterator->index < iterator->length)
		{
			/* Get the current node */
			Time* node = (Time*) iterator->node;

			_ITERATOR_NEXT(iterator, node);
		}
	}
	return NULL;
}


/***************************************************************************************************
 * COMPARABLE
 **************************************************************************************************/

Comparable timestamp_create_Comparable(void)
{
	return Comparable_create(NULL, NULL, timestamp_equals, NULL, NULL, timestamp_compare_to);
}

integer timestamp_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_TIME_TYPE, structure, type, value)
		&& type == _TIME_TYPE)
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the timestamps */
			const timestamp* ts1 = (timestamp*) structure;
			const timestamp* ts2 = (timestamp*) value;

			return _COMPARE_TO(*ts1, *ts2);
		}
	}
	return _NOT_COMPARABLE;
}

Comparable Time_create_Comparable(void)
{
	return Comparable_create(NULL, NULL, Time_equals, Time_hash, Time_to_string, Time_compare_to);
}

integer Time_compare_to(const void* structure, const type type, const void* value)
{
	_IF (_CHECKS(_TIME_TYPE, structure, type, value)
		&& type == _TIME_TYPE)
	{
		if (structure == value)
		{
			return 0;
		}
		else
		{
			/* Get the Times */
			Time t1 = *((Time*) structure);
			Time t2 = *((Time*) value);
			/* Get the timestamps */
			const timestamp ts1 = mktime(&t1);
			const timestamp ts2 = mktime(&t2);

			return timestamp_compare_to(&ts1, _TIME_TYPE, &ts2);
		}
	}
	return _NOT_COMPARABLE;
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

boolean timestamp_equals(const void* structure, const type type, const void* value)
{
	return timestamp_compare_to(structure, type, value) == 0;
}

boolean Time_equals(const void* structure, const type type, const void* value)
{
	return Time_compare_to(structure, type, value) == 0;
}

/**************************************************************************************************/

integer Time_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the Time */
		const Time* t = (Time*) structure;
		/* Get the string */
		string s;

		Time_to_string(t, s);
		return bits_hash(2, _TIME_TYPE, string_hash(s));
	}
	return integer_random();
}

/**************************************************************************************************/

boolean Time_to_string(const void* source, string target)
{
	return Time_format(source, _DATE_TIME_FORMAT, target);
}

boolean Time_append_to_string(const void* source, string target)
{
	string buffer;

	Time_to_string(source, buffer);
	return string_append_to_string(buffer, target);
}
