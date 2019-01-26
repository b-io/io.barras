/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2019 Florian Barras <https://barras.io> (florian@barras.io)
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

#include "ceres/io/CommonIO.h"


/***************************************************************************************************
 * WRITE
 **************************************************************************************************/

void char_to_stream(const character source, FILE* target)
{
#ifdef _WIDE_STRING
	fputwc(source, target);
#else
	fputc(source, target);
#endif
}

void string_to_stream(const string source, FILE* target)
{
#ifdef _WIDE_STRING
	fputws(source, target);
#else
	fputs(source, target);
#endif
}


/***************************************************************************************************
 * PRINT
 **************************************************************************************************/

void print(const string format, ...)
{
	if (format != NULL)
	{
		va_list args;

		va_start(args, format);
		file_print(stdout, format, &args);
		va_end(args);
	}
}

void error_print(const string format, ...)
{
	if (format != NULL)
	{
		va_list args;

		va_start(args, format);
		file_print(stderr, format, &args);
		va_end(args);
	}
}

void file_print(FILE* file, const string format, va_list* args)
{
	if (file != NULL)
	{
		format_to_file(format, args, file, _STRING_SIZE);
	}
}

/**************************************************************************************************/

void printn(const string format, ...)
{
	if (format != NULL)
	{
		va_list args;

		va_start(args, format);
		file_printn(stdout, format, &args);
		va_end(args);
	}
}

void error_printn(const string format, ...)
{
	if (format != NULL)
	{
		va_list args;

		va_start(args, format);
		file_printn(stderr, format, &args);
		va_end(args);
	}
}

void file_printn(FILE* file, const string format, va_list* args)
{
	if (file != NULL)
	{
		format_to_file(format, args, file, _STRING_SIZE);
		_PRINT_LINE_FEED(file);
	}
}

/**************************************************************************************************/

void IOMessage_print(const IOMessage message)
{
	string buffer;

	message.toString(&message, buffer);
	switch (message.level)
	{
		case _TRACE:
		case _DEBUG:
		case _TEST:
		case _INFO:
		case _RESULT:
			string_to_stream(buffer, stdout);
			_PRINT_LINE_FEED(stdout);
			break;
		case _WARNING:
		case _ERROR:
		case _FAILURE:
			string_to_stream(buffer, stderr);
			_PRINT_LINE_FEED(stderr);
			break;
	}
}

/**************************************************************************************************/

IOMessage print_trace(const string filePath, const string functionName, const natural lineNumber, const string content)
{
	const IOMessage message = IOMessage_create(_OUT, _TRACE, filePath, functionName, lineNumber, content);

	IOMessage_print(message);
	return message;
}

IOMessage print_debug(const string filePath, const natural lineNumber, const string content)
{
	const IOMessage message = IOMessage_create(_OUT, _DEBUG, filePath, _STRING_EMPTY, lineNumber, content);

	IOMessage_print(message);
	return message;
}

IOMessage print_test(const string filePath, const string content)
{
	const IOMessage message = IOMessage_create(_OUT, _TEST, filePath, _STRING_EMPTY, 0, content);

	IOMessage_print(message);
	return message;
}

IOMessage print_info(const string content)
{
	const IOMessage message = IOMessage_create(_OUT, _INFO, _STRING_EMPTY, _STRING_EMPTY, 0, content);

	IOMessage_print(message);
	return message;
}

IOMessage print_result(const string content)
{
	const IOMessage message = IOMessage_create(_OUT, _RESULT, _STRING_EMPTY, _STRING_EMPTY, 0, content);

	IOMessage_print(message);
	return message;
}

/**************************************************************************************************/

IOMessage print_warning(const string filePath, const string content)
{
	const IOMessage message = IOMessage_create(_OUT, _WARNING, filePath, _STRING_EMPTY, 0, content);

	IOMessage_print(message);
	return message;
}

IOMessage print_error(const string filePath, const natural lineNumber, const string content)
{
	const IOMessage message = IOMessage_create(_OUT, _ERROR, filePath, _STRING_EMPTY, lineNumber, content);

	IOMessage_print(message);
	return message;
}

void print_failure(const string filePath, const string functionName, const natural lineNumber, const string content)
{
	const IOMessage message = IOMessage_create(_OUT, _FAILURE, filePath, functionName, lineNumber, content);

	IOMessage_print(message);
	exit(EXIT_FAILURE);
}
