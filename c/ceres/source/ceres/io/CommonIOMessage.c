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

#include "ceres/io/CommonIOMessage.h"


/***************************************************************************************************
 * CONSTANTS
 **************************************************************************************************/

const natural IO_MESSAGE_SIZE = sizeof (IOMessage);


/***************************************************************************************************
 * CONSTRUCT
 **************************************************************************************************/

IOMessage IOMessage_create(const IOType type, const SeverityLevel level, const string filePath, const string functionName, const natural lineNumber, const string content)
{
	IOMessage m;

	m.core = Core_create(_FALSE, _FALSE, _TRUE, _FALSE);
	IOMessage_reset(&m);
	m.set(&m, type, level, filePath, functionName, lineNumber, content);
	return m;
}

IOMessage* IOMessage_new(const IOType type, const SeverityLevel level, const string filePath, const string functionName, const natural lineNumber, const string content)
{
	IOMessage* message = _NEW(IOMessage);

	_PRINT_TEST(_S("<newIOMessage>"));
	if (message != NULL)
	{
		message->core = Core_create(_TRUE, _FALSE, _TRUE, _FALSE);
		IOMessage_reset(message);
		message->set(message, type, level, filePath, functionName, lineNumber, content);
	}
	else
	{
		_PRINT_ERROR_MEMORY_ALLOCATION(_IO_MESSAGE_NAME);
	}
	_PRINT_TEST(_S("</newIOMessage>"));
	return message;
}


/***************************************************************************************************
 * RESET
 **************************************************************************************************/

void IOMessage_reset(void* message)
{
	_PRINT_TEST(_S("<resetIOMessage>"));
	if (message != NULL)
	{
		/* Get the I/O Message */
		IOMessage* m = (IOMessage*) message;

		/* FUNCTIONS */
		/* - IOMessage */
		m->set = IOMessage_set;
		/* - Basic */
		m->release = IOMessage_release;
		m->clone = IOMessage_clone;
		m->equals = IOMessage_equals;
		m->hash = IOMessage_hash;
		m->toString = IOMessage_to_string;

		/* ATTRIBUTES */
		/* - IOMessage */
		m->type = _OUT;
		m->level = _RESULT;
		string_reset(m->prefix);
		string_reset(m->content);
	}
	_PRINT_TEST(_S("</resetIOMessage>"));
}


/***************************************************************************************************
 * I/O MESSAGE
 **************************************************************************************************/

void IOMessage_set(IOMessage* message, const IOType type, const SeverityLevel level, const string filePath, const string functionName, const natural lineNumber, const string content)
{
	if (message != NULL)
	{
		message->type = type;
		message->level = level;
		prefix_create(level, filePath, functionName, lineNumber, message->prefix);
		string_to_string(content, message->content);
	}
}


/***************************************************************************************************
 * PREFIX
 **************************************************************************************************/

void prefix_create(const SeverityLevel level, const string filePath, const string functionName, const natural lineNumber, string output)
{
	string buffer;
	/* Get the current Time */
	const Time time = Time_local(NULL);

	/* Append the local Time */
	Time_to_string(&time, buffer);
	label_append_to_string(buffer, output);

	/* Append the Severity Level */
	SeverityLevel_to_string(&level, buffer);
	label_append_to_string(buffer, output);

	/* Append the file name */
	file_name(filePath, buffer);
	label_append_to_string(buffer, output);

	/* Append the function name */
	label_append_to_string(functionName, output);

	/* Append the line number */
	if (lineNumber > 0)
	{
		natural_to_string(&lineNumber, buffer);
		label_append_to_string(buffer, output);
	}
}


/***************************************************************************************************
 * FILE
 **************************************************************************************************/

void file_name(const string filePath, string output)
{
	if (output != NULL)
	{
		if (filePath != NULL)
		{
			string input;
			character* delimiter;

			string_to_string(filePath, input);
			delimiter = string_find_last(input, _S("/\\"));

			if (delimiter != NULL)
			{
				chars_to_string(delimiter + 1, _STRING_SIZE - (natural) (delimiter + 1 - input), output);
			}
			else
			{
				string_to_string(input, output);
			}
		}
		else
		{
			string_reset(output);
		}
	}
}


/***************************************************************************************************
 * BASIC
 **************************************************************************************************/

void IOMessage_release(void* structure)
{
	_PRINT_TEST(_S("<releaseIOMessage>"));
	if (structure != NULL)
	{
		/* Get the I/O Message */
		IOMessage* m = (IOMessage*) structure;

		/* Free the I/O Message */
		if (m->core.isDynamic)
		{
			_FREE(m);
		}
	}
	else
	{
		_PRINT_WARNING_NULL(_IO_MESSAGE_NAME);
	}
	_PRINT_TEST(_S("</releaseIOMessage>"));
}

/**************************************************************************************************/

boolean IOMessage_equals(const void* structure, const type type, const void* value)
{
	_IF(_CHECKS(_IO_MESSAGE_TYPE, structure, type, value)
		&& type == _IO_MESSAGE_TYPE)
	{
		if (structure == value)
		{
			return _TRUE;
		}
		else
		{
			/* Get the I/O Messages */
			const IOMessage* m1 = (IOMessage*) structure;
			const IOMessage* m2 = (IOMessage*) value;

			if (m1->type == m2->type
				&& m1->level == m2->level
				&& string_equals(&m1->prefix, _STRING_TYPE, &m2->prefix)
				&& string_equals(&m1->content, _STRING_TYPE, &m2->content))
			{
				return _TRUE;
			}
		}
	}
	return _FALSE;
}

/**************************************************************************************************/

void* IOMessage_clone(const void* structure)
{
	_IF(_CHECK(structure, _IO_MESSAGE_NAME))
	{
		/* Get the I/O Message */
		const IOMessage* m = (IOMessage*) structure;
		/* Construct the clone dynamically */
		IOMessage* clone = IOMessage_new(m->type, m->level, _STRING_EMPTY, _STRING_EMPTY, 0, m->content);

		string_to_string(m->prefix, clone->prefix);
		return clone;
	}
	return NULL;
}

/**************************************************************************************************/

integer IOMessage_hash(const void* structure)
{
	if (structure != NULL)
	{
		/* Get the I/O Message */
		const IOMessage* m = (IOMessage*) structure;

		return bits_hash(5,
			_IO_MESSAGE_TYPE,
			m->type,
			m->level,
			string_hash(m->prefix),
			string_hash(m->content));
	}
	return integer_random();
}

/**************************************************************************************************/

boolean IOMessage_to_string(const void* source, string target)
{
	_IF(_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the I/O Message */
		const IOMessage* message = (IOMessage*) source;

		return string_to_string(message->prefix, target)
			&& string_append_to_string(_S(" "), target)
			&& string_append_to_string(message->content, target);
	}
	return _FALSE;
}

boolean IOtype_to_string(const void* source, string target)
{
	_IF(_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the I/O type */
		const IOType* t = (IOType*) source;

		switch (*t)
		{
			case _IN:
				return string_to_string(_S("IN"), target);
			case _OUT:
				return string_to_string(_S("OUT"), target);
			default:
				_UNKNOWN_TO_STRING(target);
		}
	}
	return _FALSE;
}

boolean SeverityLevel_to_string(const void* source, string target)
{
	_IF(_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the Severity Level */
		const SeverityLevel* sl = (SeverityLevel*) source;

		switch (*sl)
		{
			case _TRACE:
				return string_to_string(_S("TRAC"), target);
			case _DEBUG:
				return string_to_string(_S("DEBU"), target);
			case _TEST:
				return string_to_string(_S("TEST"), target);
			case _INFO:
				return string_to_string(_S("INFO"), target);
			case _RESULT:
				return string_to_string(_STRING_EMPTY, target);
			case _WARNING:
				return string_to_string(_S("WARN"), target);
			case _ERROR:
				return string_to_string(_S("ERRO"), target);
			case _FAILURE:
				return string_to_string(_S("FAIL"), target);
			default:
				_UNKNOWN_TO_STRING(target);
		}
	}
	return _FALSE;
}

boolean label_append_to_string(const void* source, string target)
{
	_IF(_SOURCE_TARGET_CHECK(source, target))
	{
		/* Get the label */
		const character* s = (character*) source;

		if (string_is_empty(s))
		{
			return _TRUE;
		}
		else
		{
			return string_append_to_string(_S("["), target)
				&& string_append_to_string(s, target)
				&& string_append_to_string(_S("]"), target);
		}
	}
	return _FALSE;
}
