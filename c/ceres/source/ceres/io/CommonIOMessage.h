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

#ifdef __cplusplus
extern "C"
{
#endif
#ifndef _COMMON_IO_MESSAGE_H
#define _COMMON_IO_MESSAGE_H


	/***************************************************************************
	 * INCLUDES
	 **************************************************************************/

#include "ceres/CommonConstants.h"
#include "ceres/CommonMacros.h"
#include "ceres/CommonTypes.h"
#include "ceres/CommonStructures.h"
#include "ceres/CommonFunctions.h"
#include "ceres/CommonArrays.h"

#include "ceres/type/CommonType.h"

#include "ceres/math/CommonMath.h"

#include "ceres/time/CommonTime.h"

#include <stdio.h>


	/***************************************************************************
	 * SIZES
	 **************************************************************************/

	/**
	 * Defines the size of the I/O Messages.
	 */
	extern const natural IO_MESSAGE_SIZE;


	/***************************************************************************
	 * CONSTRUCT
	 **************************************************************************/

	/**
	 * Constructs an I/O Message statically.
	 * <p>
	 * @param type         the I/O type to set
	 * @param level        the Severity Level to set
	 * @param filePath     the file path to set
	 * @param functionName the function name to set
	 * @param lineNumber   the line number to set
	 * @param content      the content to set
	 * <p>
	 * @return the statically constructed I/O Message
	 */
	IOMessage IOMessage_create(const IOType type, const SeverityLevel level, const string filePath, const string functionName, const natural lineNumber, const string content);

	/**
	 * Constructs an I/O Message dynamically.
	 * <p>
	 * @param type         the I/O type to set
	 * @param level        the Severity Level to set
	 * @param filePath     the file path to set
	 * @param functionName the function name to set
	 * @param lineNumber   the line number to set
	 * @param content      the content to set
	 * <p>
	 * @return the dynamically constructed I/O Message
	 */
	IOMessage* IOMessage_new(const IOType type, const SeverityLevel level, const string filePath, const string functionName, const natural lineNumber, const string content);


	/***************************************************************************
	 * RESET
	 **************************************************************************/

	/**
	 * Resets the specified I/O Message.
	 * <p>
	 * @param message the I/O Message to be reset
	 */
	void IOMessage_reset(void* message);


	/***************************************************************************
	 * I/O MESSAGE
	 **************************************************************************/

	/**
	 * Sets the attributes of the specified I/O Message.
	 * <p>
	 * @param message      the I/O Message to be set
	 * @param type         the I/O type to set
	 * @param level        the Severity Level to set
	 * @param filePath     the file path to set
	 * @param functionName the function name to set
	 * @param lineNumber   the line number to set
	 * @param content      the content to set
	 */
	void IOMessage_set(IOMessage* message, const IOType type, const SeverityLevel level, const string filePath, const string functionName, const natural lineNumber, const string content);


	/***************************************************************************
	 * PREFIX
	 **************************************************************************/

	void prefix_create(const SeverityLevel level, const string filePath, const string functionName, const natural lineNumber, string output);


	/***************************************************************************
	 * FILE
	 **************************************************************************/

	void file_name(const string filePath, string output);


	/***************************************************************************
	 * BASIC
	 **************************************************************************/

	/**
	 * Releases the specified structure and frees it if requested.
	 * <p>
	 * @param structure the structure to be released
	 */
	void IOMessage_release(void* structure);

	/**************************************************************************/

	/**
	 * Constructs a dynamic copy of the specified structure.
	 * <p>
	 * @param structure the structure to be cloned
	 * <p>
	 * @return a dynamic copy of the specified structure
	 */
	void* IOMessage_clone(const void* structure);

	/**************************************************************************/

	/**
	 * Returns {@code _TRUE} if the specified structures are equal,
	 * {@code _FALSE} otherwise.
	 * <p>
	 * @param structure the structure to be compared for equality
	 * @param type      the type of the structure with which to compare
	 * @param value     the value of the structure with which to compare
	 * <p>
	 * @return {@code _TRUE} if the specified structures are equal,
	 *         {@code _FALSE} otherwise
	 */
	boolean IOMessage_equals(const void* structure, const type type, const void* value);

	/**************************************************************************/

	/**
	 * Returns the hash code of the specified structure.
	 * <p>
	 * @param structure the structure to be hashed
	 * <p>
	 * @return the hash code of the specified structure
	 */
	integer IOMessage_hash(const void* structure);

	/**************************************************************************/

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean IOMessage_to_string(const void* source, string target);

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean IOtype_to_string(const void* source, string target);

	/**
	 * Copies {@code source} into {@code target} (of size
	 * {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of {@code source} is
	 *   greater than {@code _STRING_LENGTH_MAX}.
	 */
	boolean SeverityLevel_to_string(const void* source, string target);

	/**
	 * Creates the label with {@code source} and appends it to the end of
	 * {@code target} (of size {@code _STRING_SIZE}).
	 * <p>
	 * Warning(s):
	 * - The {@code target} is truncated if the length of the label is
	 *   greater than {@code _STRING_LENGTH_MAX-strlen(target)}.
	 */
	boolean label_append_to_string(const void* source, string target);


#endif /* _COMMON_IO_MESSAGE_H */
#ifdef __cplusplus
}
#endif
