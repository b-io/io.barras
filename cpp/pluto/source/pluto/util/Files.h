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
#ifndef _FILES_H
#define _FILES_H


////////////////////////////////////////////////////////////////////////////////////////////////////
// INCLUDES
////////////////////////////////////////////////////////////////////////////////////////////////////

#include <string>


////////////////////////////////////////////////////////////////////////////////////////////////////
// USING
////////////////////////////////////////////////////////////////////////////////////////////////////

using namespace std;


////////////////////////////////////////////////////////////////////////////////////////////////////
// CLASS
////////////////////////////////////////////////////////////////////////////////////////////////////

class Files
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

private:

	Files();
	virtual ~Files();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// COMMON
	////////////////////////////////////////////////////////////////////////////////////////////////

public:

	static string getFileName(const string& filePath)
	{
		string fileName = filePath;
		const size_t last_slash_index = fileName.find_last_of("\\/");
		if (last_slash_index != string::npos)
		{
			fileName.erase(0, last_slash_index + 1);
		}
		// Remove extension if present.
		const size_t period_index = fileName.rfind('.');
		if (period_index != string::npos)
		{
			fileName.erase(period_index);
		}
		return fileName;
	}
};


#else
class Files;
#endif // _FILES_H
