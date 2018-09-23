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
#ifndef _FILE_HANDLER_H
#define _FILE_HANDLER_H


////////////////////////////////////////////////////////////////////////////////////////////////////
// INCLUDES
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <fstream>
#include <string>

#include "pluto/Common.h"
#include "pluto/io/FileContent.h"
#include "pluto/util/Arguments.h"
#include "pluto/util/Dates.h"
#include "pluto/util/Files.h"
#include "pluto/util/Formats.h"
#include "pluto/util/Strings.h"


////////////////////////////////////////////////////////////////////////////////////////////////////
// DEFINES
////////////////////////////////////////////////////////////////////////////////////////////////////

#ifdef _WINDOWS
#define getcwd _getcwd
#endif


////////////////////////////////////////////////////////////////////////////////////////////////////
// USING
////////////////////////////////////////////////////////////////////////////////////////////////////

using namespace std;


////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS
////////////////////////////////////////////////////////////////////////////////////////////////////

class FileHandler
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

protected:

	FileHandler();
	virtual ~FileHandler();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PATHS
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Gets the current pathname.
	 * <p>
	 * @return the current pathname {@link string}
	 */
	static string getCurrentPath()
	{
		char buffer[Strings::DEFAULT_STRING_SIZE];
		getcwd(buffer, Strings::DEFAULT_STRING_SIZE);
		const string currentPath = Strings::toString(buffer);
		return currentPath;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CREATE
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Creates all the directories of the specified pathname.
	 * <p>
	 * @param pathname the pathname {@link string}
	 * <p>
	 * @return {@code true} if the directories are created, {@code false}
	 *         otherwise
	 */
	static bool createDirectories(const string& pathname)
	{
		bool success = false;
		return success;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READ
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Reads the file denoted by the specified pathname.
	 * <p>
	 * @param pathname the pathname of the file to be read from
	 * <p>
	 * @return the content of the file
	 */
	static FileContent* read(const string& pathname)
	{
		return NULL;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITE
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Writes the specified string in the file denoted by the specified
	 * pathname.
	 * <p>
	 * @param line   the {@link string} to be written
	 * @param pathname the pathname of the file to be written to
	 * <p>
	 * @return {@code true} if {@code string} is written in the file,
	 *         {@code false} otherwise
	 */
	static bool writeLine(const string& line, const string& pathname)
	{
		return writeLine(line, pathname, true);
	}

	/**
	 * Writes the specified string in the file denoted by the specified
	 * pathname.
	 * <p>
	 * @param line     the {@link string} to be written
	 * @param pathname the pathname of the file to be written to
	 * @param append   option specifying how the file is opened
	 * <p>
	 * @return {@code true} if {@code string} is written in the file,
	 *         {@code false} otherwise
	 */
	static bool writeLine(const string& line, const string& pathname, const bool append)
	{
		// Open the file
		ofstream out;
		if (append)
		{
			out.open(pathname, ios::out | ios::app);
		}
		else
		{
			out.open(pathname, ios::out | ios::trunc);
		}
		// Write the line
		if (out.is_open())
		{
			out << line << endl;
			out.close();
			return true;
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// EXISTS
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	/**
	 * Tests whether the file or directory denoted by the specified path exists.
	 * <p>
	 * @param path the path to the file (or directory) to be tested
	 * <p>
	 * @return {@code true} if and only if the file (or directory) denoted by
	 *         {@code path} exists, {@code false} otherwise
	 */
	static bool exists(const string& pathname)
	{
		if (FILE * file = fopen(pathname.c_str(), "r"))
		{
			fclose(file);
			return true;
		}
		else
		{
			return false;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// DELETE
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

};


#else
class FileHandler;
#endif // _FILE_HANDLER_H
