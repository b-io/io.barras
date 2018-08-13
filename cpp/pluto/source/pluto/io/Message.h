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
#ifndef _MESSAGE_H
#define _MESSAGE_H


////////////////////////////////////////////////////////////////////////////////////////////////////
// INCLUDES
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <iostream>

#include "pluto/io/Messages.h"
#include "pluto/model/Entity.h"


////////////////////////////////////////////////////////////////////////////////////////////////////
// USING
////////////////////////////////////////////////////////////////////////////////////////////////////

using namespace std;


////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS
////////////////////////////////////////////////////////////////////////////////////////////////////

class Message : public Entity
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

private:

	IOType type;
	SeverityLevel level;
	string prefix;
	string content;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	Message(const string& content);
	Message(const SeverityLevel level, const string& content);
	Message(const SeverityLevel level, const string& filePath, const string& content);
	Message(const SeverityLevel level, const string& filePath, const string& functionName, const string& content);
	Message(const SeverityLevel level, const string& filePath, const string& functionName, int lineNumber, const string& content);
	virtual ~Message();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Returns the type.
	 * <p>
	 * @return the type
	 */
	IOType getType() const
	{
		return type;
	}

	/**
	 * Returns the level.
	 * <p>
	 * @return the level
	 */
	SeverityLevel getLevel() const
	{
		return level;
	}

	/**
	 * Returns the content.
	 * <p>
	 * @return the content
	 */
	string getContent() const
	{
		return content;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ENTITY
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Returns a {@code string} representation of {@code this}.
	 *
	 * @return a {@code string} representation of {@code this}
	 */
	virtual string toString() const
	{
		return prefix + " " + content;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CREATION
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	// - INPUT

	static Message createInputMessage()
	{
		return createInputMessage(_STRING_EMPTY);
	}

	static Message createInputMessage(const string& content)
	{
		return Message(content);
	}

	// - OUTPUT

	static Message createOutputMessage(const SeverityLevel level, const string& content)
	{
		return Message(level, content);
	}

	static Message createOutputMessage(const SeverityLevel level, const string& filePath, const string& content)
	{
		return Message(level, filePath, content);
	}

	static Message createOutputMessage(const SeverityLevel level, const string& filePath, const string& functionName, const string& content)
	{
		return Message(level, filePath, functionName, content);
	}

	static Message createOutputMessage(const SeverityLevel level, const string& filePath, const string& functionName, int lineNumber, const string& content)
	{
		return Message(level, filePath, functionName, lineNumber, content);
	}
};


#else
class Message;
#endif // _MESSAGE_H
