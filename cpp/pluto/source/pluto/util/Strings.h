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
#ifndef _STRINGS_H
#define _STRINGS_H


////////////////////////////////////////////////////////////////////////////////////////////////////
// INCLUDES
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <iostream>
#include <sstream>
#include <string>

#include "pluto/util/Arguments.h"
#include "pluto/util/Formats.h"


////////////////////////////////////////////////////////////////////////////////////////////////////
// DEFINES
////////////////////////////////////////////////////////////////////////////////////////////////////

// Defines the empty string.
#define _STRING_EMPTY				""


////////////////////////////////////////////////////////////////////////////////////////////////////
// USING
////////////////////////////////////////////////////////////////////////////////////////////////////

using namespace std;


////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS
////////////////////////////////////////////////////////////////////////////////////////////////////

class Strings
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	// The default size of a string
	static const size_t DEFAULT_STRING_SIZE = 256;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

private:

	Strings();
	virtual ~Strings();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMMON
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	static string toString(const char characters[])
	{
		return string(characters);
	}

	template<typename T>
	static string toString(const T& object)
	{
		// Initialize
		stringstream buffer;
		// Process
		buffer << object;
		return buffer.str();
	}

	template<typename T>
	static string concatenate(const T& a, const T& b)
	{
		// Initialize
		stringstream buffer;
		// Process
		buffer << a << b;
		return buffer.str();
	}

	template<typename T>
	static string repeat(const T& object, int repeat)
	{
		// Check the argument(s)
		Arguments::requireNonNegative(repeat);
		// Initialize
		stringstream buffer;
		// Process
		for (long i = 0; i < repeat; i++)
		{
			buffer << object;
		}
		return buffer.str();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Returns a {@link string} line with the default length.
	 * <p>
	 * @return a {@link string} line with the default length
	 */
	static string generateLine()
	{
		return generateLine(Formats::DEFAULT_LINE_LENGTH);
	}

	/**
	 * Returns a {@link string} line with the specified length.
	 * <p>
	 * @param length the length of the line to be generated
	 * <p>
	 * @return a {@link string} line with the specified length
	 */
	static string generateLine(int length)
	{
		return repeat("-", length);
	}
};


#else
class Strings;
#endif // _STRINGS_H
