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
#ifndef _DATES_H
#define _DATES_H


////////////////////////////////////////////////////////////////////////////////////////////////////
// INCLUDES
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <iostream>
#include <signal.h>
#include <string>
#include <time.h>

#include "pluto/util/Arguments.h"
#include "pluto/util/Formats.h"
#include "pluto/util/Strings.h"


////////////////////////////////////////////////////////////////////////////////////////////////////
// USING
////////////////////////////////////////////////////////////////////////////////////////////////////

using namespace std;


////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS
////////////////////////////////////////////////////////////////////////////////////////////////////

class Dates
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

private:

	Dates();
	virtual ~Dates();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMMON
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	static string getCurrentDate()
	{
		return getCurrentDateTime(Formats::DEFAULT_DATE_FORMAT, Formats::DEFAULT_MAX_DATE_SIZE);
	}

	static string getCurrentDateTime()
	{
		return getCurrentDateTime(Formats::DEFAULT_DATE_TIME_FORMAT, Formats::DEFAULT_MAX_DATE_TIME_SIZE);
	}

	static string getCurrentDateTime(const char* format, const int maxSize)
	{
		time_t rawDateTime = time(0);
		struct tm* localDateTime = localtime(&rawDateTime);
		char formattedDateTime[Formats::DEFAULT_MAX_DATE_TIME_SIZE];
		strftime(formattedDateTime, maxSize, format, localDateTime);
		return Strings::toString(formattedDateTime);
	}
};


#else
class Dates;
#endif // _DATES_H
