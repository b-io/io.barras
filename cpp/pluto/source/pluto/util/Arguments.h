/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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
#ifndef _ARGUMENTS_H
#define _ARGUMENTS_H


////////////////////////////////////////////////////////////////////////////////////////////////////
// INCLUDES
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <iostream>
#include <sstream>
#include <string>

#include "pluto/exception/Exceptions.h"


////////////////////////////////////////////////////////////////////////////////////////////////////
// USING
////////////////////////////////////////////////////////////////////////////////////////////////////

using namespace std;


////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS
////////////////////////////////////////////////////////////////////////////////////////////////////

class Arguments
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

protected:

	Arguments();
	virtual ~Arguments();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMMON
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	template<typename T>
	static string expectedButFound(const T& expected, const T& found)
	{
		stringstream message;
		message << "('" << expected << "' expected but '" << found << "' found)";
		return message.str();
	}

	template<typename T>
	static string atLeastExpectedButFound(const T& expected, const T& found)
	{
		stringstream message;
		message << "(at least '" << expected << "' expected but '" << found << "' found)";
		return message.str();
	}

	template<typename T>
	static string atMostExpectedButFound(const T& expected, const T& found)
	{
		stringstream message;
		message << "(at most '" << expected << "' expected but '" << found << "' found)";
		return message.str();
	}

	template<typename T>
	static string isNotEqualTo(const T& a, const T& b)
	{
		stringstream message;
		message << "('" << a << "' is not equal to '" << b << "')";
		return message.str();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CHECK
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	template<typename T>
	static T requireNonNull(const T* object, const string& string)
	{
		if (object == NULL)
		{
			Exceptions::throwException(string);
		}
		return object;
	}

	template<typename T>
	static T requireNonNull(const T* object)
	{
		return requireNonNull(object, "Specified object is null");
	}

	template<typename T>
	static void requireEquals(const T& a, const T& b)
	{
		if (a != b)
		{
			stringstream message;
			message << a << " is not equal to " << b;
			Exceptions::throwException(message.str());
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CHECK NUMBER
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	template<typename T>
	static T requireGreaterThan(const T& number, const T& lowerBound)
	{
		if (number <= lowerBound)
		{
			stringstream message;
			message << "Specified number is lower or equal to " << lowerBound;
			Exceptions::throwException(message.str());
		}
		return number;
	}

	template<typename T>
	static T requireGreaterOrEqualTo(const T& number, const T& lowerBound)
	{
		if (number < lowerBound)
		{
			stringstream message;
			message << "Specified number is lower than " << lowerBound;
			Exceptions::throwException(message.str());
		}
		return number;
	}

	template<typename T>
	static T requireLessThan(const T& number, const T& upperBound)
	{
		if (number >= upperBound)
		{
			stringstream message;
			message << "Specified number " << number << " is greater or equal to " << upperBound;
			Exceptions::throwException(message.str());
		}
		return number;
	}

	template<typename T>
	static T requireLessOrEqualTo(const T& number, const T& lowerBound)
	{
		if (number > lowerBound)
		{
			stringstream message;
			message << "Specified number is greater than " << lowerBound;
			Exceptions::throwException(message.str());
		}
		return number;
	}

	template<typename T>
	static T requireNegative(const T& number)
	{
		if (number >= 0.)
		{
			Exceptions::throwException("Specified number is zero or positive");
		}
		return number;
	}

	template<typename T>
	static T requireNonNegative(const T& number)
	{
		if (number < 0.)
		{
			Exceptions::throwException("Specified number is negative");
		}
		return number;
	}

	template<typename T>
	static T requireNonZero(const T& number)
	{
		if (number == 0.)
		{
			Exceptions::throwException("Specified number is zero");
		}
		return number;
	}

	template<typename T>
	static T requirePositive(const T& number)
	{
		if (number <= 0.)
		{
			Exceptions::throwException("Specified number is zero or negative");
		}
		return number;
	}

	template<typename T>
	static T requireNonPositive(const T& number)
	{
		if (number > 0.)
		{
			Exceptions::throwException("Specified number is positive");
		}
		return number;
	}
};


#else
class Arguments;
#endif // _ARGUMENTS_H
