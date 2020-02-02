/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2020 Florian Barras <https://barras.io> (florian@barras.io)
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

#include "ceres/CommonVerifier.h"


/***************************************************************************************************
 * ARGUMENT
 **************************************************************************************************/

boolean check(const void* argument, const string name)
{
	if (argument == NULL)
	{
		_PRINT_ERROR_NULL(name);
		return _FALSE;
	}
	return _TRUE;
}

boolean checks(const type firstType, const void* firstValue, const type secondType, const void* secondValue)
{
	if (firstValue == NULL)
	{
		string name;

		if (firstType == secondType)
		{
			string_format(name, _S("first %t"), firstType);
		}
		else
		{
			string_format(name, _S("%t"), firstType);
		}
		_PRINT_ERROR_NULL(name);
		return _FALSE;
	}
	if (secondValue == NULL)
	{
		string name;

		if (secondType == firstType)
		{
			string_format(name, _S("second %t"), secondType);
		}
		else
		{
			string_format(name, _S("%t"), secondType);
		}
		_PRINT_ERROR_NULL(name);
		return _FALSE;
	}
	return _TRUE;
}


/***************************************************************************************************
 * TYPE
 **************************************************************************************************/

boolean type_check(const type givenType, const type expectedType)
{
	if (givenType != expectedType)
	{
		_PRINT_ERROR_TYPE(givenType, expectedType);
		return _FALSE;
	}
	return _TRUE;
}


/***************************************************************************************************
 * STRUCTURE
 **************************************************************************************************/

boolean Structure_check(const Structure* structure)
{
	if (structure == NULL)
	{
		_PRINT_ERROR_NULL(_STRUCTURE_NAME);
		return _FALSE;
	}
	if (structure->value == NULL)
	{
		string name;

		type_to_string(&structure->type, name);
		_PRINT_ERROR_NULL(name);
		return _FALSE;
	}
	return _TRUE;
}

boolean Structure_checks(const Structure* first, const Structure* second)
{
	if (first == NULL || second == NULL)
	{
		_PRINT_ERROR_NULL(_STRUCTURE_NAME);
		return _FALSE;
	}
	if (first->value == NULL)
	{
		string name;

		if (first->type == second->type)
		{
			string_format(name, _S("first %t"), first->type);
		}
		else
		{
			string_format(name, _S("%t"), first->type);
		}
		_PRINT_ERROR_NULL(name);
		return _FALSE;
	}
	if (second->value == NULL)
	{
		string name;

		if (second->type == first->type)
		{
			string_format(name, _S("second %t"), second->type);
		}
		else
		{
			string_format(name, _S("%t"), second->type);
		}
		_PRINT_ERROR_NULL(name);
		return _FALSE;
	}
	return _TRUE;
}


/***************************************************************************************************
 * ARRAY
 **************************************************************************************************/

boolean array_check(const void* array, const natural size, const string name)
{
	if (_CHECK(array, name))
	{
		if (size > 0)
		{
			return _TRUE;
		}
		else
		{
			_PRINT_ERROR_SIZE(name);
		}
	}
	return _FALSE;
}
