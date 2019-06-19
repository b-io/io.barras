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

	protected final File file;
	protected final Charset charset;
	protected BufferedWriter writer;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public FileHandler(final String path) {
		this(path, DEFAULT_CHARSET);
	}

	public FileHandler(final String path, final Charset charset) {
		// Check the arguments
		Arguments.requireNonNull(path);
		Arguments.requireNonNull(charset);

		// Set the attributes
		file = new File(path);
		this.charset = charset;
		writer = null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the path.
	 * <p>
	 * @return the path
	 */
	public String getPath() {
		return Files.getPath(file);
	}

	/**
	 * Returns the canonical path.
	 * <p>
	 * @return the canonical path
	 * <p>
	 * @throws IOException       if there is a problem querying the file system
	 * @throws SecurityException if there is a permission problem
	 */
	public String getCanonicalPath()
			throws IOException {
		return Files.getCanonicalPath(file);
	}

	/**
	 * Returns the name.
	 * <p>
	 * @return the name
	 */
	public String getName() {
		return file.getName();
	}

	/**
	 * Returns the name without the extension.
	 * <p>
	 * @return the name without the extension
	 */
	public String getNameWithoutExtension() {
		return Files.getFileNameWithoutExtension(file.getName());
	}

	/**
	 * Returns the extension.
	 * <p>
	 * @return the extension
	 */
	public String getExtension() {
		return Files.getFileExtension(file.getName());
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates all the directories.
	 * <p>
	 * @throws IOException       if there is a problem creating the directories
	 * @throws SecurityException if there is a permission problem
	 */
	public void createDirs()
			throws IOException {
		Files.createDirs(file);
	}

	/**
	 * Creates all the parent directories.
	 * <p>
	 * @throws IOException       if there is a problem creating the directories
	 * @throws SecurityException if there is a permission problem
	 */
	public void createParentDirs()
			throws IOException {
		Files.createParentDirs(file);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@link BufferedReader}.
	 * <p>
	 * @return a {@link BufferedReader}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening {@code this}
	 */
	public BufferedReader getReader()
			throws FileNotFoundException {
		return Files.createReader(file, charset);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Content}.
	 * <p>
	 * @return the {@link Content}
	 */
	public Content read() {
		return Files.read(file, charset);
	}

	/**
	 * Returns the unzipped {@link Content}.
	 * <p>
	 * @return the unzipped {@link Content}
	 */
	public Content unzip() {
		return Files.unzip(file, charset);
	}

	/**
	 * Returns the ungzipped {@link Content}.
	 * <p>
	 * @return the ungzipped {@link Content}
	 */
	public Content ungzip() {
		return Files.ungzip(file, charset);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of lines.
	 * <p>
	 * @return the number of lines
	 */
	public int countLines() {
		return Files.countLines(file, charset);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}).
	 * <p>
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines})
	 */
	public int countLines(final boolean skipEmptyLines) {
		return Files.countLines(file, charset, skipEmptyLines);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Initializes the writer.
	 * <p>
	 * @param append the flag specifying whether to append
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening {@code this}
	 */
	public void initWriter(final boolean append)
			throws FileNotFoundException {
		if (writer == null) {
			writer = Files.createWriter(file, charset, append);
		}
	}

	/**
	 * Writes the specified {@link String}.
	 * <p>
	 * @param string the {@link String} to write
	 * <p>
	 * @return {@code true} if the specified {@link String} is written, {@code false} otherwise
	 */
	public boolean writeLine(final String string) {
		return writeLine(string, true);
	}

	/**
	 * Writes the specified {@link String}.
	 * <p>
	 * @param string the {@link String} to write
	 * @param append the flag specifying whether to append
	 * <p>
	 * @return {@code true} if the specified {@link String} is written, {@code false} otherwise
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
			IO.error("Unable to find the specified file ", Strings.quote(file),
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
		closeWriter("The writer of " + Strings.quote(file) + " has already been closed");
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
	 * Deletes {@code this}.
	 * <p>
	 * @return {@code true} if {@code this} is deleted, {@code false} otherwise
	 */
	public boolean delete() {
		return delete(true);
	}

	/**
	 * Deletes {@code this}.
	 * <p>
	 * @param force the flag specifying whether to force deleting
	 * <p>
	 * @return {@code true} if {@code this} is deleted, {@code false} otherwise
	 */
	public boolean delete(final boolean force) {
		closeWriter(null);
		return Files.delete(file, force);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether {@code this} exists.
	 * <p>
	 * @return {@code true} if and only if {@code this} exists, {@code false} otherwise
	 * <p>
	 * @throws SecurityException if there is a permission problem
	 */
	public boolean exists()
			throws SecurityException {
		return Files.exists(file);
	}
}
