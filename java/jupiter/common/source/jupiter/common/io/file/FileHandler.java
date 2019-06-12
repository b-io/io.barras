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
import static jupiter.common.util.Formats.DEFAULT_CHARSET;
import static jupiter.common.util.Formats.NEWLINE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import jupiter.common.io.Content;
import jupiter.common.io.Resources;
import jupiter.common.test.Arguments;
import jupiter.common.util.Strings;

public class FileHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final String pathName;
	protected final String fileName;
	protected final Charset charset;
	protected BufferedWriter writer;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public FileHandler(final String pathName) {
		this(pathName, DEFAULT_CHARSET);
	}

	public FileHandler(final String pathName, final Charset charset) {
		// Check the arguments
		Arguments.requireNonNull(pathName);
		Arguments.requireNonNull(charset);

		// Set the attributes
		this.pathName = pathName;
		fileName = Files.getFileName(pathName);
		this.charset = charset;
		writer = null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the path name.
	 * <p>
	 * @return the path name
	 */
	public String getPath() {
		return pathName;
	}

	/**
	 * Returns the file name.
	 * <p>
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns the extension.
	 * <p>
	 * @return the extension
	 */
	public String getExtension() {
		return Files.getExtension(fileName);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates all the directories of the path name.
	 * <p>
	 * @return {@code true} if the directories are created, {@code false} otherwise
	 */
	public boolean createDirectories() {
		return Files.createDirectories(pathName);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link BufferedReader}.
	 * <p>
	 * @return a {@link BufferedReader}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening the file
	 */
	public BufferedReader getReader()
			throws FileNotFoundException {
		return Files.createReader(pathName, charset);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Content}.
	 * <p>
	 * @return the {@link Content}
	 */
	public Content read() {
		return Files.read(pathName, charset);
	}

	/**
	 * Returns the unzipped {@link Content}.
	 * <p>
	 * @return the unzipped {@link Content}
	 */
	public Content unzip() {
		return Files.unzip(pathName, charset);
	}

	/**
	 * Returns the ungzipped {@link Content}.
	 * <p>
	 * @return the ungzipped {@link Content}
	 */
	public Content ungzip() {
		return Files.ungzip(pathName, charset);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of lines.
	 * <p>
	 * @return the number of lines
	 */
	public int countLines() {
		return Files.countLines(pathName, charset);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}).
	 * <p>
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines})
	 */
	public int countLines(final boolean skipEmptyLines) {
		return Files.countLines(pathName, charset, skipEmptyLines);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the writer.
	 * <p>
	 * @param append the flag specifying whether to append
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening the file
	 */
	public void initWriter(final boolean append)
			throws FileNotFoundException {
		if (writer == null) {
			writer = Files.createWriter(pathName, charset, append);
		}
	}

	/**
	 * Appends the specified string.
	 * <p>
	 * @param string the {@link String} to write
	 * <p>
	 * @return {@code true} if {@code string} is written, {@code false} otherwise
	 */
	public boolean appendLine(final String string) {
		return writeLine(string, true);
	}

	/**
	 * Writes the specified string.
	 * <p>
	 * @param string the {@link String} to write
	 * <p>
	 * @return {@code true} if {@code string} is written, {@code false} otherwise
	 */
	public boolean writeLine(final String string) {
		return writeLine(string, true);
	}

	/**
	 * Writes the specified string.
	 * <p>
	 * @param string the {@link String} to write
	 * @param append the flag specifying whether to append
	 * <p>
	 * @return {@code true} if {@code string} is written, {@code false} otherwise
	 */
	public boolean writeLine(final String string, final boolean append) {
		try {
			// Initialize
			initWriter(append);

			// Append the string
			writer.write(string + NEWLINE);
			writer.flush();
			return true;
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(pathName),
					IO.appendException(ex));
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return false;
	}

	/**
	 * Closes the writer.
	 */
	public void closeWriter() {
		closeWriter("The writer of " + Strings.quote(pathName) + " has already been closed");
	}

	/**
	 * Closes the writer.
	 * <p>
	 * @param message the warning message to print if closed
	 */
	public void closeWriter(final String message) {
		Resources.close(writer, message);
		writer = null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Deletes the file (or directory).
	 * <p>
	 * @return {@code true} if the file (or directory) is deleted, {@code false} otherwise
	 */
	public boolean delete() {
		return delete(true);
	}

	/**
	 * Deletes the file (or directory).
	 * <p>
	 * @param force the flag specifying whether to force deleting
	 * <p>
	 * @return {@code true} if the file (or directory) is deleted, {@code false} otherwise
	 */
	public boolean delete(final boolean force) {
		closeWriter(null);
		return Files.delete(new File(pathName), force);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the file (or directory) exists.
	 * <p>
	 * @return {@code true} if and only if the file (or directory) exists, {@code false} otherwise
	 * <p>
	 * @throws SecurityException if a security manager exists and its
	 *                           {@link java.lang.SecurityManager#checkRead(java.lang.String)}
	 *                           method denies read access to the file (or directory)
	 */
	public boolean exists()
			throws SecurityException {
		return Files.exists(pathName);
	}
}
