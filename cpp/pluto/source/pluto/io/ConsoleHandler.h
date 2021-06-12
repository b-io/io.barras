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
#ifndef _CONSOLE_HANDLER_H
#define _CONSOLE_HANDLER_H


////////////////////////////////////////////////////////////////////////////////////////////////////
// INCLUDES
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <iostream>

#include "pluto/io/IOHandler.h"
#include "pluto/io/Message.h"
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

class ConsoleHandler : public IOHandler
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

protected:

	ConsoleHandler();
	virtual ~ConsoleHandler();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// INPUT
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Returns the next input line of {@code console}.
	 * <p>
	 * @return the next input line of {@code console}
	 */
	static string getInputLine()
	{
		printInput();
		string inputLine;
		getline(cin, inputLine);
		return inputLine;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	template<typename T>
	static void print(const T& object, const bool error)
	{
		if (error)
		{
			cerr << object;
		}
		else
		{
			cout << object;
		}
	}

	template<typename T>
	static void printn(const T& object, const bool error)
	{
		if (error)
		{
			cerr << object << endl;
		}
		else
		{
			cout << object << endl;
		}
	}

	/**
	 * Prints the indication of an input line.
	 */
	static void printInput()
	{
		print(Message::createInputMessage(), false);
	}

	/**
	 * Prints the specified message in the console.
	 * <p>
	 * @param message the {@link Message} to be printed
	 */
	static void printn(const Message& message)
	{
		switch (message.getLevel())
		{
			case TRACE:
				if (!TRACE_MODE)
				{
					break;
				}
			case DEBUG:
				if (!DEBUG_MODE)
				{
					break;
				}
			case TEST:
			case INFO:
			case RESULT:
				printn(message, false);
				break;
			case WARNING:
			case ERROR:
			case FAILURE:
				printn(message, true);
				break;
		}
	}

	/**
	 * Prints {@code n} spaces.
	 * <p>
	 * @param n the number of spaces to be printed
	 */
	static void printSpaces(int n)
	{
		printStrings(" ", n);
	}

	/**
	 * Prints {@code n} strings.
	 * <p>
	 * @param n the number of strings to be printed
	 */
	static void printStrings(const string& string, int n)
	{
		if (n > 0)
		{
			print(Strings::repeat(string, n), false);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// BAR
	////////////////////////////////////////////////////////////////////////////////////////////////

protected:

	static const int MAX_BAR_POINTS = Formats::DEFAULT_LINE_LENGTH - 2;
	static int nBarPoints;

	/**
	 * Returns the number of points to be printed.
	 * <p>
	 * @param i the current number of iterations
	 * @param n the total number of iterations
	 * <p>
	 * @return the number of points to be printed
	 */
	static int getPointsNumber(double i, double n)
	{
		// Check the argument(s)
		Arguments::requireNonNegative(i);
		Arguments::requireLessOrEqualTo(i, n);
		Arguments::requireNonNegative(n);
		// Process
		return (int) ((i / n) * MAX_BAR_POINTS);
	}

	/**
	 * Prints {@code n} points.
	 * <p>
	 * @param n the number of points to be printed
	 */
	static void printPoints(int n)
	{
		if (n > 0)
		{
			printStrings(".", n);
			nBarPoints += n;
		}
	}

public:

	/**
	 * Prints a loading bar of {@code i/n} points.
	 * <p>
	 * @param i an {@code int} value
	 * @param n the upper bound of {@code i}
	 */
	static void printLoadingBar(double i, double n)
	{
		int nPoints = getPointsNumber(i, n);
		if (nPoints > nBarPoints)
		{
			startLoadingBar();
			printPoints(nPoints);
			printSpaces(MAX_BAR_POINTS - nPoints);
			stopLoadingBar();
		}
		if (i >= n)
		{
			nBarPoints = 0;
		}
	}

	/**
	 * Prints the beginning of a loading bar.
	 */
	static void startLoadingBar()
	{
		nBarPoints = 0;
		print("[", false);
	}

	/**
	 * Prints {@code i/n} points.
	 * <p>
	 * @param i an {@code double} value
	 * @param n the upper bound of {@code i}
	 */
	static void updateLoadingBar(double i, double n)
	{
		printPoints(getPointsNumber(i, n) - nBarPoints);
	}

	/**
	 * Prints the end of a loading bar.
	 */
	static void stopLoadingBar()
	{
		printn("]", false);
	}
};


#else
class ConsoleHandler;
#endif // _CONSOLE_HANDLER_H
