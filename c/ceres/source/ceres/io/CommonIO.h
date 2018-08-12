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
#ifndef _COMMON_IO_H
#define _COMMON_IO_H


	/***************************************************************************
	 * INCLUDES
	 **************************************************************************/

#include "ceres/CommonConstants.h"
#include "ceres/CommonMacros.h"
#include "ceres/CommonTypes.h"
#include "ceres/CommonStructures.h"
#include "ceres/CommonFunctions.h"
#include "ceres/CommonArrays.h"
#include "ceres/CommonVerifier.h"

#include "ceres/type/CommonType.h"

#include "ceres/math/CommonMath.h"

#include "ceres/time/CommonTime.h"

#include "ceres/io/CommonIOMessage.h"

#include <stdarg.h>
#include <stdio.h>


	/***************************************************************************
	 * WRITE
	 **************************************************************************/

	void char_to_stream(const character source, FILE* target);
	void string_to_stream(const string source, FILE* target);


	/***************************************************************************
	 * PRINT
	 **************************************************************************/

	/**
	 * Prints the C string pointed by {@code format} to {@code stdout}. If
	 * {@code format} includes format specifiers (subsequences beginning with %),
	 * the additional arguments following format are formatted and inserted in
	 * the resulting string replacing their respective specifiers.
	 * <p>
	 * @param format the C string that contains the text to be printed and
	 *               optionally embedded format specifiers
	 * @param ...    the additional arguments to be formatted as requested
	 *               and that replace the format specifiers
	 */
	void print(const string format, ...);

	/**
	 * Prints the C string pointed by {@code format} to {@code stderr}. If
	 * {@code format} includes format specifiers (subsequences beginning with %),
	 * the additional arguments following format are formatted and inserted in
	 * the resulting string replacing their respective specifiers.
	 * <p>
	 * @param format the C string that contains the text to be printed and
	 *               optionally embedded format specifiers
	 * @param ...    the additional arguments to be formatted as requested
	 *               and that replace the format specifiers
	 */
	void error_print(const string format, ...);

	/**
	 * Prints the C string pointed by {@code format} to {@code file}. If
	 * {@code format} includes format specifiers (subsequences beginning with %),
	 * the additional arguments following format are formatted and inserted in
	 * the resulting string replacing their respective specifiers.
	 * <p>
	 * @param file   pointer to a file where the resulting C-string is stored
	 * @param format the C string that contains the text to be printed and
	 *               optionally embedded format specifiers
	 * @param args   the additional arguments to be formatted as requested
	 *               and that replace the format specifiers
	 */
	void file_print(FILE* file, const string format, va_list* args);

	/**************************************************************************/

	/**
	 * Prints the C string pointed by {@code format} to {@code stdout} and
	 * terminates the current line by printing the line separator string. If
	 * {@code format} includes format specifiers (subsequences beginning with %),
	 * the additional arguments following format are formatted and inserted in
	 * the resulting string replacing their respective specifiers.
	 * <p>
	 * @param format the C string that contains the text to be printed and
	 *               optionally embedded format specifiers
	 * @param ...    the additional arguments to be formatted as requested
	 *               and that replace the format specifiers
	 */
	void printn(const string format, ...);

	/**
	 * Prints the C string pointed by {@code format} to {@code stderr} and
	 * terminates the current line by printing the line separator string. If
	 * {@code format} includes format specifiers (subsequences beginning with %),
	 * the additional arguments following format are formatted and inserted in
	 * the resulting string replacing their respective specifiers.
	 * <p>
	 * @param format the C string that contains the text to be printed and
	 *               optionally embedded format specifiers
	 * @param ...    the additional arguments to be formatted as requested
	 *               and that replace the format specifiers
	 */
	void error_printn(const string format, ...);

	/**
	 * Prints the C string pointed by {@code format} to {@code file} and
	 * terminates the current line by printing the line separator string. If
	 * {@code format} includes format specifiers (subsequences beginning with %),
	 * the additional arguments following format are formatted and inserted in
	 * the resulting string replacing their respective specifiers.
	 * <p>
	 * @param file   pointer to a file where the resulting C-string is stored
	 * @param format the C string that contains the text to be printed and
	 *               optionally embedded format specifiers
	 * @param args   the additional arguments to be formatted as requested
	 *               and that replace the format specifiers
	 */
	void file_printn(FILE* file, const string format, va_list* args);

	/**************************************************************************/

	/**
	 * Prints the specified I/O Message to {@code stdout} and terminates the
	 * current line by printing the line separator string.
	 * <p>
	 * @param message the I/O Message to be printed
	 */
	void IOMessage_print(const IOMessage message);

	/**************************************************************************/

	/**
	 * Constructs an {@code IOMessage} from the specified parameter(s), prints
	 * it in the console and in the log file (indicating the Severity Level
	 * {@code _RESULT}).
	 * <p>
	 * @param content the content of the message to be printed
	 * <p>
	 * @return an {@code IOMessage} containing the specified parameter(s)
	 */
	IOMessage print_result(const string content);

	/**
	 * Constructs an {@code IOMessage} from the specified parameter(s), prints
	 * it in the console and in the log file (indicating the Severity Level
	 * {@code _INFO}).
	 * <p>
	 * @param filePath     the file path
	 * @param functionName the function name
	 * @param content      the content of the message to be printed
	 * <p>
	 * @return an {@code IOMessage} containing the specified parameter(s)
	 */
	IOMessage print_info(const string filePath, const string functionName, const string content);

	/**
	 * Constructs an {@code IOMessage} from the specified parameter(s), prints
	 * it in the console and in the log file (indicating the Severity Level
	 * {@code _TEST}).
	 * <p>
	 * @param filePath     the file path
	 * @param functionName the function name
	 * @param lineNumber   the line number
	 * @param content      the content of the message to be printed
	 * <p>
	 * @return an {@code IOMessage} containing the specified parameter(s)
	 */
	IOMessage print_test(const string filePath, const string functionName, const natural lineNumber, const string content);

	/**************************************************************************/

	/**
	 * Constructs an {@code IOMessage} from the specified parameter(s), prints
	 * it in the console and in the log file (indicating the Severity Level
	 * {@code _WARNING}).
	 * <p>
	 * @param content the content of the message to be printed
	 * <p>
	 * @return an {@code IOMessage} containing the specified parameter(s)
	 */
	IOMessage print_warning(const string content);

	/**
	 * Constructs an {@code IOMessage} from the specified parameter(s), prints
	 * it in the console and in the log file (indicating the Severity Level
	 * {@code _ERROR}).
	 * <p>
	 * @param filePath   the file path
	 * @param lineNumber the line number
	 * @param content    the content of the message to be printed
	 * <p>
	 * @return an {@code IOMessage} containing the specified parameter(s)
	 */
	IOMessage print_error(const string filePath, const natural lineNumber, const string content);

	/**
	 * Constructs an {@code IOMessage} from the specified parameter(s), prints
	 * it in the console and in the log file (indicating the Severity Level
	 * {@code _FAILURE}).
	 * <p>
	 * @param filePath     the file path
	 * @param functionName the function name
	 * @param lineNumber   the line number
	 * @param content      the content of the message to be printed
	 * <p>
	 * @return an {@code IOMessage} containing the specified parameter(s)
	 */
	void print_failure(const string filePath, const string functionName, const natural lineNumber, const string content);


#endif /* _COMMON_IO_H */
#ifdef __cplusplus
}
#endif
