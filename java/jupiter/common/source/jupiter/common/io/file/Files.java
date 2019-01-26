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
package jupiter.common.io.file;

import static jupiter.common.io.IO.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import jupiter.common.io.Resources;
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
	 * Returns the current path name.
	 * <p>
	 * @return the current path name
	 */
	public static String getPath() {
		return new File(".").getAbsolutePath();
	}

	/**
	 * Returns the file name of the specified path name.
	 * <p>
	 * @param pathName a {@link String}
	 * <p>
	 * @return the file name of the specified path name
	 */
	public static String getFileName(final String pathName) {
		return pathName.substring(pathName.lastIndexOf('\\') + 1);
	}

	/**
	 * Returns the extension of the specified file name.
	 * <p>
	 * @param fileName a {@link String}
	 * <p>
	 * @return the extension of the specified file name
	 */
	public static String getExtension(final String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates all the directories of the specified path name.
	 * <p>
	 * @param pathName a {@link String}
	 * <p>
	 * @return {@code true} if the directories are created, {@code false} otherwise
	 */
	public static boolean createDirectories(final String pathName) {
		final File file = new File(pathName);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				IO.error("Unable to create the directories ", Strings.quote(pathName));
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
	 * Creates a {@link BufferedReader} of the specified file.
	 * <p>
	 * @param pathName the path name of the file to read
	 * <p>
	 * @return a {@link BufferedReader} of the specified file
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening the specified file
	 */
	public static BufferedReader createReader(final String pathName)
			throws FileNotFoundException {
		return createReader(pathName, Formats.DEFAULT_CHARSET);
	}

	/**
	 * Creates a {@link BufferedReader} of the specified file with the specified {@link Charset}.
	 * <p>
	 * @param pathName the path name of the file to read
	 * @param charset  the {@link Charset} of the file to read
	 * <p>
	 * @return a {@link BufferedReader} of the specified file with the specified {@link Charset}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening the specified file
	 */
	public static BufferedReader createReader(final String pathName, final Charset charset)
			throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(pathName), charset));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link FileContent} of the specified file.
	 * <p>
	 * @param pathName the path name of the file to read
	 * <p>
	 * @return the {@link FileContent} of the specified file
	 */
	public static FileContent read(final String pathName) {
		return read(pathName, Formats.DEFAULT_CHARSET);
	}

	/**
	 * Returns the content of the specified file with the specified {@link Charset}.
	 * <p>
	 * @param pathName the path name of the file to read
	 * @param charset  the {@link Charset} of the file to read
	 * <p>
	 * @return the content of the specified file with the specified {@link Charset}
	 */
	public static FileContent read(final String pathName, final Charset charset) {
		final StringBuilder builder = Strings.createBuilder();
		int lineCount = 0;
		// File reader
		BufferedReader reader = null;
		try {
			// Create a new file reader
			reader = createReader(pathName, charset);
			// Iterate over the lines of the file
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
				++lineCount;
			}
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(pathName),
					IO.appendException(ex));
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			// Close the file reader
			Resources.close(reader);
		}
		return new FileContent(builder.toString(), lineCount);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of lines of the specified file.
	 * <p>
	 * @param pathName the path name of the file to read
	 * @param charset  the {@link Charset} of the file to read
	 * <p>
	 * @return the number of lines of the specified file
	 */
	public static int countLines(final String pathName, final Charset charset) {
		return countLines(pathName, charset, false);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 * file.
	 * <p>
	 * @param pathName       the path name of the file to read
	 * @param charset        the {@link Charset} of the file to read
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 *         file
	 */
	public static int countLines(final String pathName, final Charset charset,
			final boolean skipEmptyLines) {
		int lineCount = 0;
		// Lines counter
		BufferedReader reader = null;
		try {
			// Create a new file reader
			reader = createReader(pathName, charset);
			// Iterate over the lines of the file
			String line;
			while ((line = reader.readLine()) != null) {
				if (Strings.isNotEmpty(line)) {
					++lineCount;
				}
			}
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(pathName),
					IO.appendException(ex));
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			// Close the file reader
			Resources.close(reader);
		}
		return lineCount;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedWriter} of the specified file.
	 * <p>
	 * @param pathName the path name of the file to write
	 * @param append   the flag specifying whether to append
	 * <p>
	 * @return a {@link BufferedWriter} of the specified file
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening the specified file
	 */
	public static BufferedWriter createWriter(final String pathName, final boolean append)
			throws FileNotFoundException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathName, append)));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Appends the specified string to the specified file.
	 * <p>
	 * @param content  the {@link String} to write
	 * @param pathName the path name of the file to write
	 * <p>
	 * @return {@code true} if {@code string} is written to the specified file, {@code false}
	 *         otherwise
	 */
	public static boolean appendLine(final String content, final String pathName) {
		return writeLine(content, pathName, true, Formats.DEFAULT_CHARSET);
	}

	/**
	 * Writes the specified string to the specified file.
	 * <p>
	 * @param content  the {@link String} to write
	 * @param pathName the path name of the file to write
	 * <p>
	 * @return {@code true} if {@code string} is written to the specified file, {@code false}
	 *         otherwise
	 */
	public static boolean writeLine(final String content, final String pathName) {
		return writeLine(content, pathName, true, Formats.DEFAULT_CHARSET);
	}

	/**
	 * Writes the specified string to the specified file.
	 * <p>
	 * @param content  the {@link String} to write
	 * @param pathName the path name of the file to write
	 * @param append   the flag specifying whether to append
	 * @param charset  the {@link Charset} of the file to write
	 * <p>
	 * @return {@code true} if {@code string} is written to the specified file, {@code false}
	 *         otherwise
	 */
	public static boolean writeLine(final String content, final String pathName,
			final boolean append, final Charset charset) {
		boolean isWritten = false;
		// File writer
		BufferedWriter writer = null;
		try {
			// Create a new file writer
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(pathName, append), charset));
			// Append the string to the file
			writer.write(content + "\n");
			isWritten = true;
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(pathName),
					IO.appendException(ex));
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			// Close the file writer
			Resources.close(writer);
		}
		return isWritten;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean copy(final File source, final File target, final boolean force)
			throws CopyFileException {
		if (exists(target)) {
			if (force) {
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
				long byteCount;
				while (position < size) {
					final long remain = size - position;
					byteCount = output.transferFrom(input, position,
							remain > BUFFER_SIZE ? BUFFER_SIZE : remain);
					// Exit if there are no more bytes to transfer
					// (e.g. if the file is truncated after caching the size)
					if (byteCount == 0) {
						break;
					}
					position += byteCount;
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
				Resources.close(input);
				Resources.close(output);
			}
		} else {
			createDirectories(target);
		}
		return false;
	}

	public static boolean copy(final File source, final File target, final boolean force,
			final int from)
			throws CopyFileException {
		if (from == 0) {
			return copy(source, target, force);
		}
		if (exists(target)) {
			if (force) {
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
				Resources.close(reader);
				Resources.close(writer);
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
	 * @param pathName the path name of the file (or directory) to delete
	 * <p>
	 * @return {@code true} if the specified file (or directory) is deleted, {@code false} otherwise
	 */
	public static boolean delete(final String pathName) {
		return delete(pathName, false);
	}

	/**
	 * Deletes the specified file (or directory).
	 * <p>
	 * @param file the {@link File} to delete
	 * <p>
	 * @return {@code true} if the file (or directory) is deleted, {@code false} otherwise
	 */
	public static boolean delete(final File file) {
		return delete(file, false);
	}

	/**
	 * Deletes the specified file (or directory).
	 * <p>
	 * @param pathName the path name of the file (or directory) to delete
	 * @param force    the flag specifying whether to force deleting
	 * <p>
	 * @return {@code true} if the specified file (or directory) is deleted, {@code false} otherwise
	 */
	public static boolean delete(final String pathName, final boolean force) {
		return delete(new File(pathName), force);
	}

	/**
	 * Deletes the specified file (or directory).
	 * <p>
	 * @param file  the {@link File} to delete
	 * @param force the flag specifying whether to force deleting
	 * <p>
	 * @return {@code true} if the specified file (or directory) is deleted, {@code false} otherwise
	 */
	public static boolean delete(final File file, final boolean force) {
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
						if (force) {
							for (final File f : files) {
								delete(f, force);
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
		} else if (!force) {
			IO.warn("The file ", Strings.quote(file), " does not exist");
		}
		return isDeleted;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified file (or directory) exists.
	 * <p>
	 * @param pathName the path name of the file (or directory) to test
	 * <p>
	 * @return {@code true} if and only if the specified file (or directory) exists, {@code false}
	 *         otherwise
	 * <p>
	 * @throws SecurityException If a security manager exists and its
	 *                           {@link java.lang.SecurityManager#checkRead(java.lang.String)}
	 *                           method denies read access to the specified file (or directory)
	 */
	public static boolean exists(final String pathName)
			throws SecurityException {
		return Strings.isNotEmpty(pathName) && new File(pathName).exists();
	}

	/**
	 * Tests whether the specified file (or directory) exists.
	 * <p>
	 * @param file the {@link File} to test
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
