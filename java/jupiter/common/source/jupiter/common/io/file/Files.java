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
package jupiter.common.io.file;

import static jupiter.common.io.IO.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import jupiter.common.exception.CopyFileException;
import jupiter.common.test.Arguments;
import jupiter.common.util.Formats;
import jupiter.common.util.Strings;

public class Files {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The buffer size.
	 */
	public static final int BUFFER_SIZE = 8192;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected Files() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the current pathname.
	 * <p>
	 * @return the current pathname
	 */
	public static String getPath() {
		return new File(".").getAbsolutePath();
	}

	/**
	 * Returns the filename from the specified pathname.
	 * <p>
	 * @param pathname a {@link String}
	 * <p>
	 * @return the filename from the specified pathname
	 */
	public static String getFilename(final String pathname) {
		return pathname.substring(pathname.lastIndexOf('\\') + 1);
	}

	/**
	 * Returns the extension from the specified filename {@link String}.
	 * <p>
	 * @param filename a {@link String}
	 * <p>
	 * @return the extension from the specified filename {@link String}
	 */
	public static String getExtension(final String filename) {
		return filename.substring(filename.lastIndexOf('.') + 1);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates all the directories of the specified pathname.
	 * <p>
	 * @param pathname a {@link String}
	 * <p>
	 * @return {@code true} if the directories are created, {@code false} otherwise
	 */
	public static boolean createDirectories(final String pathname) {
		final File file = new File(pathname);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				IO.error("Unable to create the directories ", Strings.quote(pathname));
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates all the directories of the specified file (or directory).
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @return {@code true} if the directories are created, {@code false} otherwise
	 */
	public static boolean createDirectories(final File file) {
		if (!file.exists()) {
			if (!file.mkdirs()) {
				IO.error("Unable to create the directories ",
						Strings.quote(file.getAbsolutePath()));
				return false;
			}
		}
		return true;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a buffered reader of the specified file.
	 * <p>
	 * @param pathname the pathname of the file to read
	 * <p>
	 * @return a buffered reader of the specified file
	 * <p>
	 * @throws FileNotFoundException {@inheritDoc}
	 */
	public static BufferedReader createReader(final String pathname)
			throws FileNotFoundException {
		return createReader(pathname, Formats.DEFAULT_CHARSET);
	}

	/**
	 * Creates a buffered reader of the specified file with the specified {@link Charset}.
	 * <p>
	 * @param pathname the pathname of the file to read
	 * @param charset  the {@link Charset} of the file to read
	 * <p>
	 * @return a buffered reader of the specified file with the specified {@link Charset}
	 * <p>
	 * @throws FileNotFoundException {@inheritDoc}
	 */
	public static BufferedReader createReader(final String pathname, final Charset charset)
			throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(pathname), charset));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the content of the specified file.
	 * <p>
	 * @param pathname the pathname of the file to read
	 * <p>
	 * @return the content of the specified file
	 */
	public static FileContent read(final String pathname) {
		return read(pathname, Formats.DEFAULT_CHARSET);
	}

	/**
	 * Returns the content of the specified file with the specified {@link Charset}.
	 * <p>
	 * @param pathname the pathname of the file to read
	 * @param charset  the {@link Charset} of the file to read
	 * <p>
	 * @return the content of the specified file with the specified {@link Charset}
	 */
	public static FileContent read(final String pathname, final Charset charset) {
		final StringBuilder builder = Strings.createBuilder();
		int nLines = 0;
		// File reader
		BufferedReader reader = null;
		try {
			// Create a new file reader
			reader = createReader(pathname, charset);
			// Iterate over the lines of the file
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
				++nLines;
			}
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(pathname),
					IO.appendException(ex));
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			// Close the file reader
			close(reader);
		}
		return new FileContent(builder.toString(), nLines);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of lines of the specified file.
	 * <p>
	 * @param pathname the pathname of the file to read
	 * @param charset  the character set of the file to read
	 * <p>
	 * @return the number of lines of the specified file
	 */
	public static int countLines(final String pathname, final Charset charset) {
		return countLines(pathname, charset, false);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 * file.
	 * <p>
	 * @param pathname       the pathname of the file to read
	 * @param charset        the character set of the file to read
	 * @param skipEmptyLines the option specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 *         file
	 */
	public static int countLines(final String pathname, final Charset charset,
			final boolean skipEmptyLines) {
		int nLines = 0;
		// Lines counter
		BufferedReader reader = null;
		try {
			// Create a new file reader
			reader = createReader(pathname, charset);
			// Iterate over the lines of the file
			String line;
			while ((line = reader.readLine()) != null) {
				if (Strings.isNotEmpty(line)) {
					++nLines;
				}
			}
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(pathname),
					IO.appendException(ex));
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			// Close the file reader
			close(reader);
		}
		return nLines;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a buffered writer.
	 * <p>
	 * @param pathname the pathname of the file to write
	 * @param isAppend the option specifying whether to append
	 * <p>
	 * @return a buffered writer
	 * <p>
	 * @throws FileNotFoundException {@inheritDoc}
	 */
	public static BufferedWriter createWriter(final String pathname, final boolean isAppend)
			throws FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathname, isAppend)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified string in the specified file.
	 * <p>
	 * @param content  the {@link String} to write
	 * @param pathname the pathname of the file to write
	 * <p>
	 * @return {@code true} if {@code string} is written in the specified file, {@code false}
	 *         otherwise
	 */
	public static boolean appendLine(final String content, final String pathname) {
		return writeLine(content, pathname, true, Formats.DEFAULT_CHARSET);
	}

	/**
	 * Writes the specified string in the specified file.
	 * <p>
	 * @param content  the {@link String} to write
	 * @param pathname the pathname of the file to write
	 * <p>
	 * @return {@code true} if {@code string} is written in the specified file, {@code false}
	 *         otherwise
	 */
	public static boolean writeLine(final String content, final String pathname) {
		return writeLine(content, pathname, true, Formats.DEFAULT_CHARSET);
	}

	/**
	 * Writes the specified string in the specified file.
	 * <p>
	 * @param content  the {@link String} to write
	 * @param pathname the pathname of the file to write
	 * @param isAppend the option specifying whether to append
	 * @param charset  the character set of the file to write
	 * <p>
	 * @return {@code true} if {@code string} is written in the specified file, {@code false}
	 *         otherwise
	 */
	public static boolean writeLine(final String content, final String pathname,
			final boolean isAppend, final Charset charset) {
		boolean isWritten = false;
		// File writer
		BufferedWriter writer = null;
		try {
			// Create a new file writer
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(pathname, isAppend), charset));
			// Append the string to the file
			writer.write(content + "\n");
			isWritten = true;
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(pathname),
					IO.appendException(ex));
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			// Close the file writer
			close(writer);
		}
		return isWritten;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean close(final Closeable closable) {
		return close(closable, null);
	}

	public static boolean close(final Closeable closable, final String message) {
		if (closable != null) {
			try {
				closable.close();
				return true;
			} catch (final IOException ex) {
				IO.error(ex);
			}
		} else {
			if (message != null) {
				IO.warn(message);
			}
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean copy(final File source, final File target, final boolean isForce)
			throws CopyFileException {
		if (exists(target)) {
			if (isForce) {
				delete(target);
			} else {
				throw new CopyFileException(
						"Target file " + Strings.quote(target) + " already exists");
			}
		}
		if (source.isFile()) {
			createDirectories(target.getParentFile());
			FileChannel input = null;
			FileChannel output = null;
			try {
				input = new FileInputStream(source).getChannel();
				output = new FileOutputStream(target).getChannel();
				final long size = input.size();
				long position = 0;
				long nBytes;
				while (position < size) {
					final long remain = size - position;
					nBytes = output.transferFrom(input, position,
							remain > BUFFER_SIZE ? BUFFER_SIZE : remain);
					// Exit if there are no more bytes to transfer
					// (e.g. if the file is truncated after caching the size)
					if (nBytes == 0) {
						break;
					}
					position += nBytes;
				}
				if (source.length() != target.length()) {
					throw new CopyFileException("Failed to copy the full contents from " +
							Strings.quote(source) + " to " + Strings.quote(target) + " " +
							Arguments.expectedButFound(target.length(), source.length()));
				}
				target.setLastModified(source.lastModified());
				return true;
			} catch (final IOException ex) {
				IO.error(ex);
			} finally {
				close(input);
				close(output);
			}
		} else {
			createDirectories(target);
		}
		return false;
	}

	public static boolean copy(final File source, final File target, final boolean isForce,
			final int from)
			throws CopyFileException {
		if (from == 0) {
			return copy(source, target, isForce);
		}
		if (exists(target)) {
			if (isForce) {
				delete(target);
			} else {
				throw new CopyFileException(
						"Target file " + Strings.quote(target) + " already exists");
			}
		}
		if (source.isFile()) {
			createDirectories(target.getParentFile());
			BufferedReader reader = null;
			PrintWriter writer = null;
			try {
				reader = new BufferedReader(new FileReader(source));
				writer = new PrintWriter(new FileWriter(target));
				int i = 0;
				String line;
				while ((line = reader.readLine()) != null) {
					if (i >= from) {
						writer.println(line);
					}
					++i;
				}
				return true;
			} catch (final IOException ex) {
				IO.error(ex);
			} finally {
				close(reader);
				close(writer);
			}
		} else {
			createDirectories(target);
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Deletes the specified file (or directory).
	 * <p>
	 * @param pathname the pathname of the file (or directory) to delete
	 * <p>
	 * @return {@code true} if the specified file (or directory) is deleted, {@code false} otherwise
	 */
	public static boolean delete(final String pathname) {
		return delete(pathname, false);
	}

	/**
	 * Deletes the specified file (or directory).
	 * <p>
	 * @param file the file (or directory) to delete
	 * <p>
	 * @return {@code true} if the file (or directory) is deleted, {@code false} otherwise
	 */
	public static boolean delete(final File file) {
		return delete(file, false);
	}

	/**
	 * Deletes the specified file (or directory).
	 * <p>
	 * @param pathname the pathname of the file (or directory) to delete
	 * @param isForce  the option specifying whether to force deleting
	 * <p>
	 * @return {@code true} if the specified file (or directory) is deleted, {@code false} otherwise
	 */
	public static boolean delete(final String pathname, final boolean isForce) {
		return delete(new File(pathname), isForce);
	}

	/**
	 * Deletes the specified file (or directory).
	 * <p>
	 * @param file    the file (or directory) to delete
	 * @param isForce the option specifying whether to force deleting
	 * <p>
	 * @return {@code true} if the specified file (or directory) is deleted, {@code false} otherwise
	 */
	public static boolean delete(final File file, final boolean isForce) {
		boolean isDeleted = false;
		// Test whether the file (or directory) exists
		if (file.exists()) {
			// Test whether it is not write protected
			if (file.canWrite()) {
				// Test whether it is a directory
				if (file.isDirectory()) {
					// - Directory
					final File[] files = file.listFiles();
					// Test whether the directory contains files
					if (files.length > 0) {
						// If force, delete the files of the directory recursively
						if (isForce) {
							for (final File f : files) {
								delete(f, isForce);
							}
						} else {
							IO.warn("The directory ", Strings.quote(file), " is not empty");
						}
					}
				}
				// Attempt to delete the file (or directory)
				isDeleted = file.delete();
				if (!isDeleted) {
					IO.error("Unable to delete the file ", Strings.quote(file));
				}
			} else {
				IO.warn("The file ", Strings.quote(file), " is write protected");
			}
		} else {
			if (!isForce) {
				IO.warn("The file ", Strings.quote(file), " does not exist");
			}
		}
		return isDeleted;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified file (or directory) exists.
	 * <p>
	 * @param pathname the pathname of the file (or directory) to test
	 * <p>
	 * @return {@code true} if and only if the specified file (or directory) exists, {@code false}
	 *         otherwise
	 * <p>
	 * @throws SecurityException If a security manager exists and its
	 *                           {@link java.lang.SecurityManager#checkRead(java.lang.String)}
	 *                           method denies read access to the specified file (or directory)
	 */
	public static boolean exists(final String pathname)
			throws SecurityException {
		return Strings.isNotEmpty(pathname) && new File(pathname).exists();
	}

	/**
	 * Tests whether the specified file (or directory) exists.
	 * <p>
	 * @param file the file (or directory) to test
	 * <p>
	 * @return {@code true} if and only if the specified file (or directory) exists, {@code false}
	 *         otherwise
	 * <p>
	 * @throws SecurityException If a security manager exists and its
	 *                           {@link java.lang.SecurityManager#checkRead(java.lang.String)}
	 *                           method denies read access to the file (or directory)
	 */
	public static boolean exists(final File file)
			throws SecurityException {
		return file.exists();
	}
}
