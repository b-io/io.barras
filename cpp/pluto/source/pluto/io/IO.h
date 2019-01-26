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
#ifndef _IO_H
#define _IO_H


////////////////////////////////////////////////////////////////////////////////////////////////////
// INCLUDES
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <stdlib.h>
#include <string.h>

#include "pluto/io/ConsoleHandler.h"


////////////////////////////////////////////////////////////////////////////////////////////////////
// DEFINES
////////////////////////////////////////////////////////////////////////////////////////////////////

#define _TRACE(content)						IO::trace(__FILE__, __FUNCTION__, __LINE__, content)
#define _DEBUG(content)						IO::debug(__FILE__, __FUNCTION__, content)
#define _TEST(content)						IO::test(__FILE__, content)
#define _INFO(content)						IO::info(content)
#define _RESULT(content)					IO::result(content)
#define _WARN(content)						IO::warn(__FILE__, content)
#define _ERROR(content)						IO::error(__FILE__, __FUNCTION__, content)
#define _FAIL(content)						IO::fail(__FILE__, __FUNCTION__, __LINE__, content)


////////////////////////////////////////////////////////////////////////////////////////////////////
// USING
////////////////////////////////////////////////////////////////////////////////////////////////////

using namespace std;


////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS
////////////////////////////////////////////////////////////////////////////////////////////////////

class IO
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	static bool USE_LOGS;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

protected:

	IO();
	virtual ~IO();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Gets the input line using the console handler and writes it in the log
	 * indicating the I/O type {@code IOType.IN}.
	 * <p>
	 * @return the input {@link string}
	 */
	static string input()
	{
		const Message message = Message::createInputMessage(ConsoleHandler::getInputLine());
		if (USE_LOGS)
		{
			//LogHandler.writeLine(message);
		}
		return message.getContent();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PRINTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Prints the specified object.
	 * <p>
	 * @param message the message to be printed
	 * @param error if {@code true}, use stderr instead of stdout
	 */
	template<typename T>
	static void print(const T& message, const bool error)
	{
		ConsoleHandler::print(message, error);
	}

	/**
	 * Prints the specified object and terminates the current line by writing
	 * the line separator string.
	 * <p>
	 * @param message the message to be printed
	 * @param error if {@code true}, use stderr instead of stdout
	 */
	template<typename T>
	static void printn(const T& message, const bool error)
	{
		ConsoleHandler::printn(message, error);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prints the specified string in the console and writes it in the log
	 * indicating the severity level {@code SeverityLevel.TRACE}.
	 * <p>
	 * @param filePath     the file path
	 * @param functionName the function name
	 * @param lineNumber   the line number
	 * @param content      the content of the message to be printed
	 * <p>
	 * @return a {@link Message} containing the specified string
	 */
	static Message trace(const string& filePath, const string& functionName, int lineNumber, const string& content)
	{
		const Message message = Message::createOutputMessage(TRACE, filePath, functionName, lineNumber, content);
		ConsoleHandler::printn(message);
		if (USE_LOGS)
		{
			//LogHandler.writeLine(message);
		}
		return message;
	}

	/**
	 * Prints the specified string in the console and writes it in the log
	 * indicating the severity level {@code SeverityLevel.DEBUG}.
	 * <p>
	 * @param filePath     the file path
	 * @param functionName the function name
	 * @param content      the content of the message to be printed
	 * <p>
	 * @return a {@link Message} containing the specified string
	 */
	static Message debug(const string& filePath, const string& functionName, const string& content)
	{
		const Message message = Message::createOutputMessage(DEBUG, filePath, functionName, content);
		ConsoleHandler::printn(message);
		if (USE_LOGS)
		{
			//LogHandler.writeLine(message);
		}
		return message;
	}

	/**
	 * Prints the specified string in the console and writes it in the log
	 * indicating the severity level {@code SeverityLevel.TEST}.
	 * <p>
	 * @param filePath the file path
	 * @param content  the content of the message to be printed
	 * <p>
	 * @return a {@link Message} containing the specified string
	 */
	static Message test(const string& filePath, const string& content)
	{
		const Message message = Message::createOutputMessage(TEST, filePath, content);
		ConsoleHandler::printn(message);
		if (USE_LOGS)
		{
			//LogHandler.writeLine(message);
		}
		return message;
	}

	/**
	 * Prints the specified string in the console and writes it in the log
	 * indicating the severity level {@code SeverityLevel.INFO}.
	 * <p>
	 * @param string the {@link string} to be printed
	 * <p>
	 * @return a {@link Message} containing the specified string
	 */
	static Message info(const string& content)
	{
		const Message message = Message::createOutputMessage(INFO, content);
		ConsoleHandler::printn(message);
		if (USE_LOGS)
		{
			// LogHandler.writeLine(message);
		}
		return message;
	}

	/**
	 * Prints the specified string in the console and writes it in the log
	 * indicating the severity level {@code SeverityLevel.RESULT}.
	 * <p>
	 * @param string the {@link string} to be printed
	 * <p>
	 * @return a {@link Message} containing the specified string
	 */
	static Message result(const string& content)
	{
		const Message message = Message::createOutputMessage(RESULT, content);
		ConsoleHandler::printn(message);
		if (USE_LOGS)
		{
			// LogHandler.writeLine(message);
		}
		return message;
	}

	/**
	 * Prints the specified string in the console and writes it in the error log
	 * indicating the severity level {@code SeverityLevel.WARNING}.
	 * <p>
	 * @param filePath the file path
	 * @param content  the content of the message to be printed
	 * <p>
	 * @return a {@link Message} containing the specified string
	 */
	static Message warn(const string& filePath, const string& content)
	{
		const Message message = Message::createOutputMessage(WARNING, filePath, content);
		ConsoleHandler::printn(message);
		if (USE_LOGS)
		{
			//LogHandler.writeLine(message);
		}
		return message;
	}

	/**
	 * Prints the specified string in the console and writes it in the error log
	 * indicating the severity level {@code SeverityLevel.ERROR}.
	 * <p>
	 * @param filePath     the file path
	 * @param functionName the function name
	 * @param content      the content of the message to be printed
	 * <p>
	 * @return a {@link Message} containing the specified string
	 */
	static Message error(const string& filePath, const string& functionName, const string& content)
	{
		const Message message = Message::createOutputMessage(ERROR, filePath, functionName, content);
		ConsoleHandler::printn(message);
		if (USE_LOGS)
		{
			//LogHandler.writeLine(message);
		}
		return message;
	}

	/**
	 * Prints the specified string in the console and writes it in the error log
	 * indicating the severity level {@code SeverityLevel.FAILURE}.
	 * <p>
	 * @param filePath     the file path
	 * @param functionName the function name
	 * @param lineNumber   the line number
	 * @param content      the content of the message to be printed
	 * <p>
	 * @return a {@link Message} containing the specified string
	 */
	static Message fail(const string& filePath, const string& functionName, int lineNumber, const string& content)
	{
		const Message message = Message::createOutputMessage(FAILURE, filePath, functionName, lineNumber, content);
		ConsoleHandler::printn(message);
		if (USE_LOGS)
		{
			//LogHandler.writeLine(message);
		}
		exit(EXIT_FAILURE);
		return message;
	}
};


#else
class IO;
#endif // _IO_H
