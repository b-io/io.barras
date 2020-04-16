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
package jupiter.common.io.file;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Formats.DEFAULT_CHARSET;
import static jupiter.common.util.Formats.NEW_LINE;
import static jupiter.common.util.Strings.EMPTY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

import jupiter.common.exception.CopyFileException;
import jupiter.common.io.Content;
import jupiter.common.io.Resources;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.test.Arguments;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class FileHandler
		implements ICloneable<FileHandler>, Serializable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link File} to handle.
	 */
	protected File file;
	/**
	 * The {@link Charset} of the {@link File} to handle.
	 */
	protected final Charset charset;

	/**
	 * The {@link ExtendedLinkedList} of {@link BufferedReader} of the {@link File} to handle.
	 */
	protected final ExtendedLinkedList<BufferedReader> readers = new ExtendedLinkedList<BufferedReader>();
	/**
	 * The {@link BufferedWriter} of the {@link File} to handle.
	 */
	protected BufferedWriter writer = null;
	/**
	 * The flag specifying whether to append.
	 */
	protected boolean append = true;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link FileHandler} with the specified path.
	 * <p>
	 * @param path the path to the file to handle
	 */
	public FileHandler(final String... path) {
		this(Strings.join(path));
	}

	/**
	 * Constructs a {@link FileHandler} with the specified path.
	 * <p>
	 * @param path the path to the file to handle
	 */
	public FileHandler(final String path) {
		this(path, DEFAULT_CHARSET);
	}

	/**
	 * Constructs a {@link FileHandler} with the specified path and {@link Charset}.
	 * <p>
	 * @param path    the path to the file to handle
	 * @param charset the {@link Charset} of the file to handle
	 */
	public FileHandler(final String path, final Charset charset) {
		this(new File(Arguments.requireNonNull(path, "path")), charset);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link FileHandler} with the specified {@link File}.
	 * <p>
	 * @param file the {@link File} to handle
	 */
	public FileHandler(final File file) {
		this(file, DEFAULT_CHARSET);
	}

	/**
	 * Constructs a {@link FileHandler} with the specified {@link File} and {@link Charset}.
	 * <p>
	 * @param file    the {@link File} to handle
	 * @param charset the {@link Charset} of the {@link File} to handle
	 */
	public FileHandler(final File file, final Charset charset) {
		// Check the arguments
		Arguments.requireNonNull(file, "file");
		Arguments.requireNonNull(charset, "charset");

		// Set the attributes
		this.file = file;
		this.charset = charset;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link File}.
	 * <p>
	 * @return the {@link File}
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the {@link Charset}.
	 * <p>
	 * @return the {@link Charset}
	 */
	public Charset getCharset() {
		return charset;
	}

	//////////////////////////////////////////////

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
	 * @throws IOException       if there is a problem with querying the file system
	 * @throws SecurityException if there is a permission problem
	 */
	public String getCanonicalPath()
			throws IOException {
		return Files.getCanonicalPath(file);
	}

	/**
	 * Returns the file name.
	 * <p>
	 * @return the file name
	 */
	public String getName() {
		return file.getName();
	}

	/**
	 * Returns the file name without the extension.
	 * <p>
	 * @return the file name without the extension
	 */
	public String getNameWithoutExtension() {
		return Files.getNameWithoutExtension(file.getName());
	}

	/**
	 * Returns the file extension.
	 * <p>
	 * @return the file extension
	 */
	public String getExtension() {
		return Files.getExtension(file.getName());
	}

	//////////////////////////////////////////////

	/**
	 * Returns the flag specifying whether to append.
	 * <p>
	 * @return the flag specifying whether to append
	 */
	public boolean append() {
		return append;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Closes all the {@link BufferedReader} and the {@link BufferedWriter}.
	 */
	public void clear() {
		closeAllReaders(null);
		closeWriter(null);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates all the directories.
	 * <p>
	 * @throws IOException       if there is a problem with creating the directories
	 * @throws SecurityException if there is a permission problem
	 */
	public void createDirs()
			throws IOException {
		Files.createDirs(file);
	}

	/**
	 * Creates all the parent directories.
	 * <p>
	 * @throws IOException       if there is a problem with creating the directories
	 * @throws SecurityException if there is a permission problem
	 */
	public void createParentDirs()
			throws IOException {
		Files.createParentDirs(file);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes all the lines from the {@link File}.
	 * <p>
	 * @return {@code true} if all the lines are removed from the {@link File}, {@code false}
	 *         otherwise
	 */
	public boolean empty() {
		return write(EMPTY, false);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Copies the {@link File} to the specified target {@link File} (preserving the file dates).
	 * <p>
	 * @param target the target {@link File} to copy to
	 * <p>
	 * @return {@code true} if the {@link File} is copied to the specified target {@link File}
	 *         (preserving the file dates), {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code file} to {@code target}
	 */
	public boolean copy(final File target)
			throws CopyFileException {
		return Files.copy(file, target);
	}

	/**
	 * Copies the {@link File} to the specified target {@link File} (preserving the file dates).
	 * <p>
	 * @param target the target {@link File} to copy to
	 * @param force  the flag specifying whether to delete the target {@link File} before copying
	 * <p>
	 * @return {@code true} if the {@link File} is copied to the specified target {@link File}
	 *         (preserving the file dates), {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code file} to {@code target}
	 */
	public boolean copy(final File target, final boolean force)
			throws CopyFileException {
		return Files.copy(file, target, force);
	}

	//////////////////////////////////////////////

	/**
	 * Deletes the {@link File}.
	 * <p>
	 * @return {@code true} if the {@link File} is deleted, {@code false} otherwise
	 */
	public boolean delete() {
		clear();
		return Files.delete(file);
	}

	/**
	 * Deletes the {@link File}.
	 * <p>
	 * @param force the flag specifying whether to force deleting
	 * <p>
	 * @return {@code true} if the {@link File} is deleted, {@code false} otherwise
	 */
	public boolean delete(final boolean force) {
		clear();
		return Files.delete(file, force);
	}

	//////////////////////////////////////////////

	/**
	 * Moves the {@link File} to the specified target {@link File} (preserving the file dates).
	 * <p>
	 * @param target the target {@link File} to move to
	 * <p>
	 * @return {@code true} if the {@link File} is moved to the specified target {@link File}
	 *         (preserving the file dates), {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code file} to {@code target}
	 */
	public boolean move(final File target)
			throws CopyFileException {
		return move(target, false);
	}

	/**
	 * Moves the {@link File} to the specified target {@link File} (preserving the file dates).
	 * <p>
	 * @param target the target {@link File} to move to
	 * @param force  the flag specifying whether to delete the target {@link File} before moving
	 * <p>
	 * @return {@code true} if the {@link File} is moved to the specified target {@link File}
	 *         (preserving the file dates), {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code file} to {@code target}
	 */
	public boolean move(final File target, final boolean force)
			throws CopyFileException {
		final boolean status = copy(target, force) && delete(true);
		if (status) {
			file = target;
		}
		return status;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Replaces all the substrings matching the specified regular expression {@link String} in the
	 * {@link File} by the specified {@link String}.
	 * <p>
	 * @param regex       the regular expression {@link String} to identify and replace (may be
	 *                    {@code null})
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean replaceAll(final String regex, final String replacement) {
		return replaceAll(regex, replacement, 0, Integer.MAX_VALUE);
	}

	/**
	 * Replaces all the substrings matching the specified regular expression {@link String} in the
	 * {@link File} by the specified {@link String} between the specified lines.
	 * <p>
	 * @param regex         the regular expression {@link String} to identify and replace (may be
	 *                      {@code null})
	 * @param replacement   the {@link String} to replace by (may be {@code null})
	 * @param fromLineIndex the line index to start replacing from (inclusive)
	 * @param toLineIndex   the line index to finish replacing at (exclusive)
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean replaceAll(final String regex, final String replacement, final int fromLineIndex,
			final int toLineIndex) {
		// Check the arguments
		if (regex == null || replacement == null) {
			return true;
		}

		// Replace all the substrings matching the regex in the file by the replacement string
		// between the lines
		BufferedReader reader = null;
		final FileHandler tempFileHandler = new FileHandler(Files.createTempFile());
		try {
			reader = createReader();
			tempFileHandler.empty();
			int i = 0;
			String line;
			while (i >= fromLineIndex && i < toLineIndex && (line = reader.readLine()) != null) {
				tempFileHandler.writeLine(Strings.replaceAll(line, regex, replacement));
				++i;
			}
			clear();
			return tempFileHandler.move(file, true);
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			closeReader(reader, null);
			tempFileHandler.clear();
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS / WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedReader}.
	 * <p>
	 * @return a {@link BufferedReader}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening {@code file}
	 */
	public BufferedReader createReader()
			throws FileNotFoundException {
		final BufferedReader reader = Files.createReader(file, charset);
		readers.add(reader);
		return reader;
	}

	//////////////////////////////////////////////

	/**
	 * Closes the specified {@link BufferedReader}.
	 * <p>
	 * @param reader the {@link BufferedReader} to close
	 */
	public void closeReader(final BufferedReader reader) {
		closeReader(reader, Strings.join("The reader of ", Strings.quote(file),
				" has already been closed"));
	}

	/**
	 * Closes the specified {@link BufferedReader}.
	 * <p>
	 * @param reader  the {@link BufferedReader} to close
	 * @param message the warning message {@link String} to print for each {@link BufferedReader}
	 *                already closed
	 */
	public void closeReader(final BufferedReader reader, final String message) {
		if (!readers.remove(reader)) {
			if (message != null) {
				IO.warn(message);
			}
		} else {
			Resources.close(reader);
		}
	}

	/**
	 * Closes all the {@link BufferedReader}.
	 */
	public void closeAllReaders() {
		closeAllReaders(Strings.join("A reader of ", Strings.quote(file),
				" has already been closed"));
	}

	/**
	 * Closes all the {@link BufferedReader}.
	 * <p>
	 * @param message the warning message {@link String} to print for each {@link BufferedReader}
	 *                already closed
	 */
	public void closeAllReaders(final String message) {
		for (final BufferedReader reader : readers) {
			Resources.close(reader, message);
		}
		readers.clear();
	}

	//////////////////////////////////////////////

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

	//////////////////////////////////////////////

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

	/**
	 * Creates the {@link BufferedWriter}.
	 * <p>
	 * @param append the flag specifying whether to append
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 */
	public void createWriter(final boolean append)
			throws FileNotFoundException {
		if (writer != null && this.append != append) {
			closeWriter();
		}
		if (writer == null) {
			writer = Files.createWriter(file, charset, append);
			this.append = append;
		}
	}

	//////////////////////////////////////////////

	/**
	 * Closes the {@link BufferedWriter}.
	 */
	public void closeWriter() {
		closeWriter(Strings.join("The writer of ", Strings.quote(file),
				" has not been created or has already been closed"));
	}

	/**
	 * Closes the {@link BufferedWriter}.
	 * <p>
	 * @param message the warning message {@link String} to print if the {@link BufferedWriter} is
	 *                already closed
	 */
	public void closeWriter(final String message) {
		Resources.close(writer, message);
		writer = null;
	}

	//////////////////////////////////////////////

	/**
	 * Writes the specified content {@link String}.
	 * <p>
	 * @param content the content {@link String} to write
	 * <p>
	 * @return {@code true} if the specified content {@link String} is written, {@code false}
	 *         otherwise
	 */
	public boolean write(final String content) {
		return write(content, append);
	}

	/**
	 * Writes the specified content {@link String}.
	 * <p>
	 * @param content the content {@link String} to write
	 * @param append  the flag specifying whether to append
	 * <p>
	 * @return {@code true} if the specified content {@link String} is written, {@code false}
	 *         otherwise
	 */
	public boolean write(final String content, final boolean append) {
		try {
			// Initialize
			createWriter(append);

			// Append the content
			writer.write(content);
			writer.flush();
			return true;
		} catch (final FileNotFoundException ex) {
			IO.error(ex, "Cannot find the file ", Strings.quote(file));
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return false;
	}

	//////////////////////////////////////////////

	/**
	 * Writes the specified content {@link String} and terminates the line.
	 * <p>
	 * @param content the content {@link String} to write
	 * <p>
	 * @return {@code true} if the specified content {@link String} is written, {@code false}
	 *         otherwise
	 */
	public boolean writeLine(final String content) {
		return writeLine(content, append);
	}

	/**
	 * Writes the specified content {@link String} and terminates the line.
	 * <p>
	 * @param content the content {@link String} to write
	 * @param append  the flag specifying whether to append
	 * <p>
	 * @return {@code true} if the specified content {@link String} is written, {@code false}
	 *         otherwise
	 */
	public boolean writeLine(final String content, final boolean append) {
		return write(content.concat(NEW_LINE), append);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the {@link File} exists.
	 * <p>
	 * @return {@code true} if the {@link File} exists, {@code false} otherwise
	 * <p>
	 * @throws SecurityException if there is a permission problem
	 */
	public boolean exists()
			throws SecurityException {
		return Files.exists(file);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 * <p>
	 * @return a clone of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public FileHandler clone() {
		try {
			return (FileHandler) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Objects.toString(ex), ex);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 * <p>
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return Objects.toString(file);
	}
}
