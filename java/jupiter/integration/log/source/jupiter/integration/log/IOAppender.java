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
package jupiter.integration.log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import jupiter.common.io.IO;
import jupiter.common.io.Message;
import jupiter.common.io.console.ConsoleHandler;
import jupiter.common.io.log.LogHandler;

public class IOAppender
		extends AppenderSkeleton {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The stack index offset.
	 */
	protected static final int STACK_INDEX_OFFSET = 7;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final IO io;
	/**
	 * The internal lock of the IO.
	 */
	protected final Lock ioLock = new ReentrantLock();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public IOAppender() {
		super();
		io = new IO(Message.DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET);
	}

	public IOAppender(final IO.SeverityLevel severityLevel) {
		super();
		io = new IO(Message.DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET, severityLevel);
	}

	public IOAppender(final IO.SeverityLevel severityLevel, final ConsoleHandler consoleHandler) {
		super();
		io = new IO(Message.DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET, severityLevel,
				consoleHandler);
	}

	public IOAppender(final IO.SeverityLevel severityLevel, final ConsoleHandler consoleHandler,
			final LogHandler logHandler) {
		super();
		io = new IO(Message.DEFAULT_STACK_INDEX + STACK_INDEX_OFFSET, severityLevel, consoleHandler,
				logHandler);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// APPENDER
	////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void append(final LoggingEvent event) {
		ioLock.lock();
		try {
			final Level level = event.getLevel();
			final Object message = event.getMessage();
			if (level == Level.TRACE) {
				io.trace(message);
			} else if (level == Level.DEBUG) {
				io.debug(message);
			} else if (level == Level.INFO) {
				io.info(message);
			} else if (level == Level.WARN) {
				io.warn(message);
			} else if (level == Level.ERROR) {
				io.error(message);
			} else if (level == Level.FATAL) {
				io.fail(message);
			} else {
				io.result(message);
			}
		} catch (final Exception ignored) {
		} finally {
			ioLock.unlock();
		}
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}
}
