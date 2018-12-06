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
package jupiter.common.io;

import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.io.IO.Type;
import jupiter.common.time.Dates;
import jupiter.common.util.Strings;

public class Messages {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Messages() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	// - ALL
	public static String getPrefix(final Type type, final SeverityLevel level,
			final int stackIndex) {
		switch (type) {
			case INPUT:
				return getInputPrefix();
			case OUTPUT:
				return getOutputPrefix(level, stackIndex);
			default:
				return Strings.EMPTY;
		}
	}

	// - INPUT
	public static String getInputPrefix() {
		return createInputPrefix();
	}

	// - OUTPUT
	public static String getOutputPrefix(final SeverityLevel level, final int stackIndex) {
		// Get information about the call (class name, method name and Message number)
		final StackTraceElement stackTraceElement = new Throwable().fillInStackTrace()
				.getStackTrace()[stackIndex];
		final String simpleClassName = getSimpleClassName(stackTraceElement);
		// Create the prefix
		switch (level) {
			case RESULT:
				return Strings.EMPTY;
			case INFO:
				return createOutputPrefix(level);
			case TEST:
			case WARNING:
				return createOutputPrefix(level, simpleClassName);
			case DEBUG:
			case ERROR:
				return createOutputPrefix(level, simpleClassName,
						stackTraceElement.getMethodName());
			case TRACE:
			case FAILURE:
				return createOutputPrefix(level, simpleClassName, stackTraceElement.getMethodName(),
						stackTraceElement.getLineNumber());
			default:
				return Strings.EMPTY;
		}
	}

	public static String getSimpleClassName(final StackTraceElement stackTraceElement) {
		final String className = stackTraceElement.getClassName();
		return className.substring(className.lastIndexOf('.') + 1);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	// - ALL
	protected static String createPrefix() {
		return createLabel(Dates.getDateTime());
	}

	protected static String createPrefix(final Type type) {
		return createPrefix() + createLabel(type);
	}

	// - INPUT
	protected static String createInputPrefix() {
		return createPrefix(Type.INPUT);
	}

	// - OUTPUT
	protected static String createOutputPrefix() {
		return createPrefix();
	}

	protected static String createOutputPrefix(final SeverityLevel level) {
		return createOutputPrefix() + createLabel(level);
	}

	protected static String createOutputPrefix(final SeverityLevel level,
			final String className) {
		return createOutputPrefix(level) + createLabel(className);
	}

	protected static String createOutputPrefix(final SeverityLevel level, final String className,
			final String methodName) {
		return createOutputPrefix(level, className) + createLabel(methodName);
	}

	protected static String createOutputPrefix(final SeverityLevel level, final String className,
			final String methodName, final int lineNumber) {
		return createOutputPrefix(level, className, methodName) +
				createLabel(Strings.toString(lineNumber));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	// - ALL
	protected static String createLabel(final Type type) {
		final String content;
		switch (type) {
			case INPUT:
				content = "INPUT";
				break;
			case OUTPUT:
				content = "OUTPUT";
				break;
			default:
				content = Strings.EMPTY;
		}
		return createLabel(content);
	}

	protected static String createLabel(final SeverityLevel level) {
		return createLabel(Strings.toString(level).substring(0, 4));
	}

	protected static String createLabel(final String string) {
		return Strings.isNotEmpty(string) ? Strings.bracketize(string) : Strings.EMPTY;
	}
}
