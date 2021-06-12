/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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
#ifndef _MESSAGES_H
#define _MESSAGES_H


////////////////////////////////////////////////////////////////////////////////////////////////////
// INCLUDES
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <iostream>
#include <string>

#include "pluto/util/Dates.h"
#include "pluto/util/Files.h"
#include "pluto/util/Strings.h"


////////////////////////////////////////////////////////////////////////////////////////////////////
// USING
////////////////////////////////////////////////////////////////////////////////////////////////////

using namespace std;


////////////////////////////////////////////////////////////////////////////////////////////////////
// ENUMS
////////////////////////////////////////////////////////////////////////////////////////////////////

enum IOType
{
	IN,
	OUT
};

enum SeverityLevel
{
	TRACE,
	DEBUG,
	TEST,
	INFO,
	RESULT,
	WARNING,
	ERROR,
	FAILURE
};


////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS
////////////////////////////////////////////////////////////////////////////////////////////////////

class Messages
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

protected:

	Messages();
	virtual ~Messages();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PREFIX
	////////////////////////////////////////////////////////////////////////////////////////////////

protected:

	static string createPrefix(IOType type)
	{
		return createPrefix() + createLabel(type);
	}

public:

	// - ALL

	static string createPrefix()
	{
		return createLabel(Dates::getCurrentDateTime());
	}
	// - INPUT

	static string createInputPrefix()
	{
		return createPrefix(IN);
	}
	// - OUTPUT

	static string createOutputPrefix()
	{
		return createPrefix();
	}

	static string createOutputPrefix(SeverityLevel level)
	{
		string prefix;
		if (level == RESULT)
		{
			prefix = _STRING_EMPTY;
		}
		else
		{
			prefix = createOutputPrefix() + createLabel(level);
		}
		return prefix;
	}

	static string createOutputPrefix(SeverityLevel level, const string& filePath)
	{
		return createOutputPrefix(level) + createLabel(Files::getFileName(filePath));
	}

	static string createOutputPrefix(SeverityLevel level, const string& filePath, const string& functionName)
	{
		return createOutputPrefix(level, filePath) + createLabel(functionName);
	}

	static string createOutputPrefix(SeverityLevel level, const string& filePath, const string& functionName, int lineNumber)
	{
		return createOutputPrefix(level, filePath, functionName) + createLabel(Strings::toString(lineNumber));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// LABEL CREATION
	////////////////////////////////////////////////////////////////////////////////////////////////

protected:

	static string createLabel(IOType type)
	{
		string string;
		switch (type)
		{
			case IN:
				string = "IN";
				break;
			case OUT:
				string = "OUT";
				break;
			default:
				string = _STRING_EMPTY;
		}
		return createLabel(string);
	}

	static string createLabel(SeverityLevel level)
	{
		string string;
		switch (level)
		{
			case TRACE:
				string = "TRAC";
				break;
			case DEBUG:
				string = "DEBU";
				break;
			case TEST:
				string = "TEST";
				break;
			case INFO:
				string = "INFO";
				break;
			case RESULT:
				string = _STRING_EMPTY;
				break;
			case WARNING:
				string = "WARN";
				break;
			case ERROR:
				string = "ERRO";
				break;
			case FAILURE:
				string = "FAIL";
				break;
			default:
				string = _STRING_EMPTY;
		}
		return createLabel(string);
	}

	static string createLabel(const string& string)
	{
		return (string.empty()) ? _STRING_EMPTY : "[" + string + "]";
	}
};


#else
class Messages;
#endif // _MESSAGES_H
