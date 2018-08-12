/*
 * The MIT License
 *
 * Copyright © 2013-2018 Florian Barras <https://barras.io>
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import jupiter.common.util.Formats;
import jupiter.common.util.Strings;

public class FileHandler {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected final String pathname;
	protected final String filename;
	protected final Charset charset;
	protected BufferedWriter writer;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public FileHandler(final String pathname) {
		this.pathname = pathname;
		filename = Files.getFilename(pathname);
		charset = Formats.DEFAULT_CHARSET;
		writer = null;
	}


	public FileHandler(final String pathname, final Charset charset) {
		this.pathname = pathname;
		filename = Files.getFilename(pathname);
		this.charset = charset;
		writer = null;
	}

	public FileHandler(final File file)
			throws IOException {
		pathname = file.getAbsolutePath();
		filename = file.getName();
		charset = Formats.DEFAULT_CHARSET;
		writer = null;
	}

	public FileHandler(final File file, final Charset charset)
			throws IOException {
		pathname = file.getAbsolutePath();
		filename = file.getName();
		this.charset = charset;
		writer = null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the pathname.
	 * <p>
	 * @return the pathname
	 */
	public String getPath() {
		return pathname;
	}

	/**
	 * Returns the filename.
	 * <p>
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Returns the extension.
	 * <p>
	 * @return the extension
	 */
	public String getExtension() {
		return Files.getExtension(filename);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates all the directories of the pathname.
	 * <p>
	 * @return {@code true} if the directories are created, {@code false} otherwise
	 */
	public boolean createDirectories() {
		return Files.createDirectories(pathname);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a buffered reader.
	 * <p>
	 * @return a buffered reader
	 * <p>
	 * @throws FileNotFoundException {@inheritDoc}
	 */
	public BufferedReader getReader()
			throws FileNotFoundException {
		return Files.createReader(pathname, charset);
	}

	/**
	 * Returns the content.
	 * <p>
	 * @return the content
	 */
	public FileContent read() {
		return Files.read(pathname, charset);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of lines.
	 * <p>
	 * @return the number of lines
	 */
	public int countLines() {
		return Files.countLines(pathname, charset, false);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}).
	 * <p>
	 * @param skipEmptyLines the option specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines})
	 */
	public int countLines(final boolean skipEmptyLines) {
		return Files.countLines(pathname, charset, skipEmptyLines);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the writer.
	 * <p>
	 * @param isAppend the option specifying whether to append
	 * <p>
	 * @throws FileNotFoundException {@inheritDoc}
	 */
	public void initWriter(final boolean isAppend)
			throws FileNotFoundException {
		if (writer == null) {
			writer = Files.createWriter(pathname, isAppend);
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
	 * @param string   the {@link String} to write
	 * @param isAppend the option specifying whether to append
	 * <p>
	 * @return {@code true} if {@code string} is written, {@code false} otherwise
	 */
	public boolean writeLine(final String string, final boolean isAppend) {
		try {
			// Initialize the writer
			initWriter(isAppend);
			// Append the string
			writer.write(string + "\n");
			return true;
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(pathname),
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
		Files.close(writer,
				"The writer of " + Strings.quote(pathname) + " has already been closed");
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
		closeWriter();
		return Files.delete(pathname, false);
	}

	/**
	 * Deletes the file (or directory).
	 * <p>
	 * @param isForce the option specifying whether to force deleting
	 * <p>
	 * @return {@code true} if the file (or directory) is deleted, {@code false} otherwise
	 */
	public boolean delete(final boolean isForce) {
		closeWriter();
		return Files.delete(new File(pathname), isForce);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the file (or directory) exists.
	 * <p>
	 * @return {@code true} if and only if the file (or directory) exists, {@code false} otherwise
	 * <p>
	 * @throws SecurityException If a security manager exists and its
	 *                           {@link java.lang.SecurityManager#checkRead(java.lang.String)}
	 *                           method denies read access to the file (or directory)
	 */
	public boolean exists()
			throws SecurityException {
		return Files.exists(pathname);
	}
}
