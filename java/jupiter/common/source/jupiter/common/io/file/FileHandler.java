/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2026 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.Formats.CHARSET;
import static jupiter.common.Formats.NEWLINE;
import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Strings.EMPTY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.PhantomReference;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.Collection;

import jupiter.common.exception.CopyFileException;
import jupiter.common.io.Content;
import jupiter.common.io.InputOutput;
import jupiter.common.io.Resources;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.collection.list.ExtendedLinkedList;
import jupiter.common.struct.collection.list.Index;
import jupiter.common.test.Arguments;
import jupiter.common.time.Dates;
import jupiter.common.util.Arrays;
import jupiter.common.util.Collections;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class FileHandler
		implements ICloneable<FileHandler>, Closeable, Serializable {

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
	 * The number of lines.
	 */
	protected int lineCount = -1;

	/**
	 * The {@link ExtendedLinkedList} of {@link Closeable} reader of the {@link File} to handle.
	 */
	protected final ExtendedLinkedList<Closeable> readers = new ExtendedLinkedList<Closeable>();
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
	 * Constructs a {@link FileHandler} of the file denoted by the specified path.
	 *
	 * @param path the path to the file to handle
	 */
	public FileHandler(final String... path) {
		this(Strings.join(path));
	}

	/**
	 * Constructs a {@link FileHandler} of the file denoted by the specified path.
	 *
	 * @param path the path to the file to handle
	 */
	public FileHandler(final String path) {
		this(path, CHARSET);
	}

	/**
	 * Constructs a {@link FileHandler} of the file denoted by the specified path with the specified
	 * {@link Charset}.
	 *
	 * @param path    the path to the file to handle
	 * @param charset the {@link Charset} of the file to handle
	 */
	public FileHandler(final String path, final Charset charset) {
		this(new File(Arguments.requireNonNull(path, "path")), charset);
	}

	//////////////////////////////////////////////

	/**
	 * Constructs a {@link FileHandler} of the specified {@link File}.
	 *
	 * @param file the {@link File} to handle
	 */
	public FileHandler(final File file) {
		this(file, CHARSET);
	}

	/**
	 * Constructs a {@link FileHandler} of the specified {@link File} with the specified
	 * {@link Charset}.
	 *
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
	 *
	 * @return the {@link File}
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the {@link Charset}.
	 *
	 * @return the {@link Charset}
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * Returns the number of lines.
	 *
	 * @return the number of lines
	 */
	public int getLineCount() {
		return lineCount >= 0 ? lineCount : countLines();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return Files.getPath(file);
	}

	/**
	 * Returns the canonical path.
	 *
	 * @return the canonical path
	 * @throws IOException       if there is a problem with querying the file system
	 * @throws SecurityException if there is a permission problem
	 */
	public String getCanonicalPath()
			throws IOException {
		return Files.getCanonicalPath(file);
	}

	/**
	 * Returns the file name.
	 *
	 * @return the file name
	 */
	public String getName() {
		return file.getName();
	}

	/**
	 * Returns the file name without the extension.
	 *
	 * @return the file name without the extension
	 */
	public String getNameWithoutExtension() {
		return Files.getNameWithoutExtension(file.getName());
	}

	/**
	 * Returns the file extension.
	 *
	 * @return the file extension
	 */
	public String getExtension() {
		return Files.getExtension(file.getName());
	}

	//////////////////////////////////////////////

	/**
	 * Returns the flag specifying whether to append.
	 *
	 * @return the flag specifying whether to append
	 */
	public boolean append() {
		return append;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates all the directories.
	 *
	 * @throws IOException       if there is a problem with creating the directories
	 * @throws SecurityException if there is a permission problem
	 */
	public void createDirs()
			throws IOException {
		Files.createDirs(file);
	}

	/**
	 * Creates all the parent directories.
	 *
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
	 *
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean empty() {
		try {
			write(EMPTY, false);
			return true;
		} catch (final FileNotFoundException ex) {
			IO.error(ex, "Cannot open or create the file", Strings.quote(file));
		} catch (final IOException ex) {
			IO.error(ex, "Cannot empty the file", Strings.quote(file));
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Copies the {@link File} to the specified target {@link File} (preserving the file dates).
	 *
	 * @param target the target {@link File} to copy to
	 * @return {@code true} if the {@link File} is copied to the specified target {@link File}
	 *         (preserving the file dates), {@code false} otherwise
	 * @throws CopyFileException if there is a problem with copying {@code file} to {@code target}
	 */
	public boolean copy(final File target)
			throws CopyFileException {
		return copy(target, false);
	}

	/**
	 * Copies the {@link File} to the specified target {@link File} (preserving the file dates).
	 *
	 * @param target the target {@link File} to copy to
	 * @param force  the flag specifying whether to delete the target {@link File} before copying
	 * @return {@code true} if the {@link File} is copied to the specified target {@link File}
	 *         (preserving the file dates), {@code false} otherwise
	 * @throws CopyFileException if there is a problem with copying {@code file} to {@code target}
	 */
	public boolean copy(final File target, final boolean force)
			throws CopyFileException {
		return Files.copy(file, target, force);
	}

	//////////////////////////////////////////////

	/**
	 * Deletes the {@link File}.
	 *
	 * @return {@code true} if the {@link File} is deleted, {@code false} otherwise
	 */
	public boolean delete() {
		return delete(false);
	}

	/**
	 * Deletes the {@link File}.
	 *
	 * @param force the flag specifying whether to force deleting
	 * @return {@code true} if the {@link File} is deleted, {@code false} otherwise
	 */
	public boolean delete(final boolean force) {
		close();
		return Files.delete(file, force);
	}

	//////////////////////////////////////////////

	/**
	 * Moves the {@link File} to the specified target {@link File} (preserving the file dates).
	 *
	 * @param target the target {@link File} to move to
	 * @return {@code true} if the {@link File} is moved to the specified target {@link File}
	 *         (preserving the file dates), {@code false} otherwise
	 * @throws CopyFileException if there is a problem with copying {@code file} to {@code target}
	 */
	public boolean move(final File target)
			throws CopyFileException {
		return move(target, false);
	}

	/**
	 * Moves the {@link File} to the specified target {@link File} (preserving the file dates).
	 *
	 * @param target the target {@link File} to move to
	 * @param force  the flag specifying whether to delete the target {@link File} before moving
	 * @return {@code true} if the {@link File} is moved to the specified target {@link File}
	 *         (preserving the file dates), {@code false} otherwise
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
	 * Removes the specified line from the {@link File}.
	 *
	 * @param lineIndex the index of the line to remove
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean remove(final int lineIndex) {
		return removeAll(lineIndex, lineIndex + 1);
	}

	//////////////////////////////////////////////

	/**
	 * Removes all the lines from the {@link File} from the specified line.
	 *
	 * @param fromLineIndex the line index to start removing from (inclusive)
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean removeAll(final int fromLineIndex) {
		return removeAll(fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Removes all the lines from the {@link File} between the specified lines.
	 *
	 * @param fromLineIndex the line index to start removing from (inclusive)
	 * @param toLineIndex   the line index to finish removing at (exclusive)
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean removeAll(final int fromLineIndex, final int toLineIndex) {
		BufferedReader reader = null;
		final FileHandler tempFileHandler = new FileHandler(Files.createTempFile());
		try {
			reader = createReader();
			tempFileHandler.empty();
			tempFileHandler.writeAllLines(reader, 0, fromLineIndex);
			InputOutput.skipLines(reader, toLineIndex - fromLineIndex);
			tempFileHandler.writeAllLines(reader);
			close();
			lineCount = tempFileHandler.lineCount;
			return tempFileHandler.move(file, true);
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			closeReader(reader, null);
			Resources.close(tempFileHandler);
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Replaces all the substrings matching the specified regular expression {@link String} in the
	 * {@link File} by the specified {@link String}.
	 *
	 * @param regex       the regular expression {@link String} to identify and replace (may be
	 *                    {@code null})
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean replaceAll(final String regex, final String replacement) {
		return replaceAll(regex, replacement, 0, Integer.MAX_VALUE);
	}

	/**
	 * Replaces all the substrings matching the specified regular expression {@link String} in the
	 * {@link File} by the specified {@link String} from the specified line.
	 *
	 * @param regex         the regular expression {@link String} to identify and replace (may be
	 *                      {@code null})
	 * @param replacement   the {@link String} to replace by (may be {@code null})
	 * @param fromLineIndex the line index to start replacing from (inclusive)
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean replaceAll(final String regex, final String replacement,
			final int fromLineIndex) {
		return replaceAll(regex, replacement, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Replaces all the substrings matching the specified regular expression {@link String} in the
	 * {@link File} by the specified {@link String} between the specified lines.
	 *
	 * @param regex         the regular expression {@link String} to identify and replace (may be
	 *                      {@code null})
	 * @param replacement   the {@link String} to replace by (may be {@code null})
	 * @param fromLineIndex the line index to start replacing from (inclusive)
	 * @param toLineIndex   the line index to finish replacing at (exclusive)
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
			tempFileHandler.writeAllLines(reader, 0, fromLineIndex);
			int li = fromLineIndex;
			String line;
			while (li < toLineIndex && (line = reader.readLine()) != null) {
				tempFileHandler.writeLine(Strings.replaceAll(line, regex, replacement));
				++li;
			}
			tempFileHandler.writeAllLines(reader);
			close();
			lineCount = tempFileHandler.lineCount;
			return tempFileHandler.move(file, true);
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			closeReader(reader, null);
			Resources.close(tempFileHandler);
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Touches the {@link File}.
	 *
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean touch() {
		try {
			if (!file.exists()) {
				Resources.close(new FileOutputStream(file));
			}
			file.setLastModified(Dates.getTimestamp());
			return true;
		} catch (final FileNotFoundException ex) {
			IO.error(ex, "Cannot open or create the file", Strings.quote(file));
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Truncates the {@link File} from the specified line.
	 *
	 * @param fromLineIndex the line index to start truncating from (inclusive)
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean truncate(final int fromLineIndex) {
		return truncate(fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Truncates the {@link File} between the specified lines.
	 *
	 * @param fromLineIndex the line index to start truncating from (inclusive)
	 * @param toLineIndex   the line index to finish truncating at (exclusive)
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public boolean truncate(final int fromLineIndex, final int toLineIndex) {
		BufferedReader reader = null;
		final FileHandler tempFileHandler = new FileHandler(Files.createTempFile());
		try {
			reader = createReader();
			tempFileHandler.empty();
			tempFileHandler.writeAllLines(reader, fromLineIndex, toLineIndex);
			close();
			lineCount = tempFileHandler.lineCount;
			return tempFileHandler.move(file, true);
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			closeReader(reader, null);
			Resources.close(tempFileHandler);
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS / WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedReader}.
	 *
	 * @return a {@link BufferedReader}
	 * @throws FileNotFoundException if there is a problem with opening {@code file}
	 */
	public BufferedReader createReader()
			throws FileNotFoundException {
		final BufferedReader reader = Files.createReader(file, charset);
		readers.add(reader);
		return reader;
	}

	/**
	 * Creates a {@link ReversedFileReader}.
	 *
	 * @return a {@link ReversedFileReader}
	 * @throws FileNotFoundException        if there is a problem with opening {@code file}
	 * @throws IOException                  if there is a problem with reading {@code file}
	 * @throws UnsupportedEncodingException if the {@code charset} byte order cannot be determined
	 */
	public ReversedFileReader createReversedReader()
			throws FileNotFoundException, IOException, UnsupportedEncodingException {
		final ReversedFileReader reader = Files.createReversedReader(file, charset);
		readers.add(reader);
		return reader;
	}

	//////////////////////////////////////////////

	/**
	 * Closes the specified {@link Closeable} reader.
	 *
	 * @param reader the {@link Closeable} reader to close
	 */
	public void closeReader(final Closeable reader) {
		closeReader(reader, Strings.paste("The reader of", Strings.quote(file),
				"has already been closed"));
	}

	/**
	 * Closes the specified {@link Closeable} reader.
	 *
	 * @param reader  the {@link Closeable} reader to close
	 * @param message the warning message {@link String} to print for each {@link Closeable} reader
	 *                already closed
	 */
	public void closeReader(final Closeable reader, final String message) {
		if (!readers.remove(reader)) {
			if (message != null) {
				IO.warn(message);
			}
		} else {
			Resources.close(reader);
		}
	}

	/**
	 * Closes all the {@link Closeable} readers.
	 */
	public void closeAllReaders() {
		closeAllReaders(Strings.paste("A reader of", Strings.quote(file),
				"has already been closed"));
	}

	/**
	 * Closes all the {@link Closeable} readers.
	 *
	 * @param message the warning message {@link String} to print for each {@link Closeable} reader
	 *                already closed
	 */
	public void closeAllReaders(final String message) {
		for (final Closeable reader : readers) {
			Resources.close(reader, message);
		}
		readers.clear();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Content}.
	 *
	 * @return the {@link Content}
	 */
	public Content read() {
		return Files.read(file, charset);
	}

	/**
	 * Returns the unzipped {@link Content}.
	 *
	 * @return the unzipped {@link Content}
	 */
	public Content unzip() {
		return Files.unzip(file, charset);
	}

	/**
	 * Returns the ungzipped {@link Content}.
	 *
	 * @return the ungzipped {@link Content}
	 */
	public Content ungzip() {
		return Files.ungzip(file, charset);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of lines.
	 *
	 * @return the number of lines
	 */
	public int countLines() {
		return countLines(false);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}).
	 *
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines})
	 */
	public int countLines(final boolean skipEmptyLines) {
		return Files.countLines(file, charset, skipEmptyLines);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates the {@link BufferedWriter}.
	 *
	 * @param append the flag specifying whether to append
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 */
	public synchronized void createWriter(final boolean append)
			throws FileNotFoundException {
		if (writer != null && this.append != append) {
			closeWriter();
		}
		if (writer == null) {
			writer = Files.createWriter(file, charset, append);
			this.append = append;
			if (!append) {
				lineCount = 0;
			}
		}
	}

	//////////////////////////////////////////////

	/**
	 * Closes the {@link BufferedWriter}.
	 */
	public synchronized void closeWriter() {
		closeWriter(Strings.paste("The writer of", Strings.quote(file),
				"has not been created or has already been closed"));
	}

	/**
	 * Closes the {@link BufferedWriter}.
	 *
	 * @param message the warning message {@link String} to print if the {@link BufferedWriter} is
	 *                already closed
	 */
	public synchronized void closeWriter(final String message) {
		Resources.close(writer, message);
		writer = null;
	}

	//////////////////////////////////////////////

	/**
	 * Writes the specified content {@link String}.
	 *
	 * @param content the content {@link String} to write
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 * @throws IOException           if there is a problem with writing to {@code file}
	 */
	public synchronized void write(final String content)
			throws FileNotFoundException, IOException {
		write(content, append);
	}

	/**
	 * Writes the specified content {@link String}.
	 *
	 * @param content the content {@link String} to write
	 * @param append  the flag specifying whether to append
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 * @throws IOException           if there is a problem with writing to {@code file}
	 */
	public synchronized void write(final String content, final boolean append)
			throws FileNotFoundException, IOException {
		// Initialize
		createWriter(append);

		// Append the content
		writer.write(content);
		writer.flush();
	}

	//////////////////////////////////////////////

	/**
	 * Writes the specified content {@link String} and terminates the line.
	 *
	 * @param content the content {@link String} to write
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 * @throws IOException           if there is a problem with writing to {@code file}
	 */
	public synchronized void writeLine(final String content)
			throws FileNotFoundException, IOException {
		writeLine(content, append);
	}

	/**
	 * Writes the specified content {@link String} and terminates the line.
	 *
	 * @param content the content {@link String} to write
	 * @param append  the flag specifying whether to append
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 * @throws IOException           if there is a problem with writing to {@code file}
	 */
	public synchronized void writeLine(final String content, final boolean append)
			throws FileNotFoundException, IOException {
		write(content.concat(NEWLINE), append);
		if (lineCount >= 0) {
			++lineCount;
		}
	}

	//////////////////////////////////////////////

	/**
	 * Writes all the lines of the specified {@link BufferedReader}.
	 *
	 * @param reader the {@link BufferedReader} to read
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 * @throws IOException           if there is a problem with writing to {@code file}
	 */
	public synchronized void writeAllLines(final BufferedReader reader)
			throws FileNotFoundException, IOException {
		writeAllLines(reader, 0, Integer.MAX_VALUE, append);
	}

	/**
	 * Writes all the lines of the specified {@link BufferedReader}.
	 *
	 * @param reader the {@link BufferedReader} to read
	 * @param append the flag specifying whether to append
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 * @throws IOException           if there is a problem with writing to {@code file}
	 */
	public synchronized void writeAllLines(final BufferedReader reader, final boolean append)
			throws FileNotFoundException, IOException {
		writeAllLines(reader, 0, Integer.MAX_VALUE, append);
	}

	/**
	 * Writes all the lines of the specified {@link BufferedReader} from the specified line.
	 *
	 * @param reader        the {@link BufferedReader} to read
	 * @param fromLineIndex the line index to start reading from (inclusive)
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 * @throws IOException           if there is a problem with writing to {@code file}
	 */
	public synchronized void writeAllLines(final BufferedReader reader, final int fromLineIndex)
			throws FileNotFoundException, IOException {
		writeAllLines(reader, fromLineIndex, Integer.MAX_VALUE, append);
	}

	/**
	 * Writes all the lines of the specified {@link BufferedReader} from the specified line.
	 *
	 * @param reader        the {@link BufferedReader} to read
	 * @param fromLineIndex the line index to start reading from (inclusive)
	 * @param append        the flag specifying whether to append
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 * @throws IOException           if there is a problem with writing to {@code file}
	 */
	public synchronized void writeAllLines(final BufferedReader reader, final int fromLineIndex,
			final boolean append)
			throws FileNotFoundException, IOException {
		writeAllLines(reader, fromLineIndex, Integer.MAX_VALUE, append);
	}

	/**
	 * Writes all the lines of the specified {@link BufferedReader} between the specified lines.
	 *
	 * @param reader        the {@link BufferedReader} to read
	 * @param fromLineIndex the line index to start reading from (inclusive)
	 * @param toLineIndex   the line index to finish replacing at (exclusive)
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 * @throws IOException           if there is a problem with writing to {@code file}
	 */
	public synchronized void writeAllLines(final BufferedReader reader, final int fromLineIndex,
			final int toLineIndex)
			throws FileNotFoundException, IOException {
		writeAllLines(reader, fromLineIndex, toLineIndex, append);
	}

	/**
	 * Writes all the lines of the specified {@link BufferedReader} between the specified lines.
	 *
	 * @param reader        the {@link BufferedReader} to read
	 * @param fromLineIndex the line index to start reading from (inclusive)
	 * @param toLineIndex   the line index to finish replacing at (exclusive)
	 * @param append        the flag specifying whether to append
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 * @throws IOException           if there is a problem with writing to {@code file}
	 */
	public synchronized void writeAllLines(final BufferedReader reader, final int fromLineIndex,
			final int toLineIndex, final boolean append)
			throws FileNotFoundException, IOException {
		int li = InputOutput.skipLines(reader, fromLineIndex);
		String line;
		while (li < toLineIndex && (line = reader.readLine()) != null) {
			writeLine(line, append);
			++li;
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the {@link File}, or {@code null} if there is no such
	 * occurrence.
	 *
	 * @param tokens the array of token {@link String} to find (may be {@code null})
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the {@link File}, or {@code null} if there is no
	 *         such occurrence
	 */
	public Index<Index<String>> findFirstLine(final String... tokens) {
		return findFirstLine(tokens, 0, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the {@link File}, seeking forward from the specified index,
	 * or {@code null} if there is no such occurrence.
	 *
	 * @param tokens        the array of token {@link String} to find (may be {@code null})
	 * @param fromLineIndex the line index to start seeking forward from (inclusive)
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the {@link File}, seeking forward from the
	 *         specified index, or {@code null} if there is no such occurrence
	 */
	public Index<Index<String>> findFirstLine(final String[] tokens, final int fromLineIndex) {
		return findFirstLine(tokens, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the {@link File}, seeking forward from the specified index
	 * to the specified index, or {@code null} if there is no such occurrence.
	 *
	 * @param tokens        the array of token {@link String} to find (may be {@code null})
	 * @param fromLineIndex the line index to start seeking forward from (inclusive)
	 * @param toLineIndex   the line index to finish seeking forward at (exclusive)
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the {@link File}, seeking forward from the
	 *         specified index to the specified index, or {@code null} if there is no such
	 *         occurrence
	 */
	public Index<Index<String>> findFirstLine(final String[] tokens, final int fromLineIndex,
			final int toLineIndex) {
		if (Arrays.isNonEmpty(tokens)) {
			BufferedReader reader = null;
			try {
				reader = createReader();
				int li = InputOutput.skipLines(reader, fromLineIndex);
				String line;
				while (li < toLineIndex && (line = reader.readLine()) != null) {
					final Index<String> index = Strings.findFirstString(line, tokens);
					if (index != null) {
						return new Index<Index<String>>(li, index);
					}
					++li;
				}
			} catch (final FileNotFoundException ex) {
				IO.error(ex, "Cannot open the file", Strings.quote(file));
			} catch (final IOException ex) {
				IO.error(ex, "Cannot read the file", Strings.quote(file));
			} finally {
				closeReader(reader, null);
			}
		}
		return null;
	}

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the {@link File}, or {@code null} if there is no such
	 * occurrence.
	 *
	 * @param tokens the {@link Collection} of token {@link String} to find (may be {@code null})
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the {@link File}, or {@code null} if there is no
	 *         such occurrence
	 */
	public Index<Index<String>> findFirstLine(final Collection<String> tokens) {
		return findFirstLine(tokens, 0, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the {@link File}, seeking forward from the specified index,
	 * or {@code null} if there is no such occurrence.
	 *
	 * @param tokens        the {@link Collection} of token {@link String} to find (may be
	 *                      {@code null})
	 * @param fromLineIndex the line index to start seeking forward from (inclusive)
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the {@link File}, seeking forward from the
	 *         specified index, or {@code null} if there is no such occurrence
	 */
	public Index<Index<String>> findFirstLine(final Collection<String> tokens,
			final int fromLineIndex) {
		return findFirstLine(tokens, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the {@link File}, seeking forward from the specified index
	 * to the specified index, or {@code null} if there is no such occurrence.
	 *
	 * @param tokens        the {@link Collection} of token {@link String} to find (may be
	 *                      {@code null})
	 * @param fromLineIndex the line index to start seeking forward from (inclusive)
	 * @param toLineIndex   the line index to finish seeking forward at (exclusive)
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the {@link File}, seeking forward from the
	 *         specified index to the specified index, or {@code null} if there is no such
	 *         occurrence
	 */
	public Index<Index<String>> findFirstLine(final Collection<String> tokens,
			final int fromLineIndex, final int toLineIndex) {
		if (Collections.isNonEmpty(tokens)) {
			BufferedReader reader = null;
			try {
				reader = createReader();
				int li = InputOutput.skipLines(reader, fromLineIndex);
				String line;
				while (li < toLineIndex && (line = reader.readLine()) != null) {
					final Index<String> index = Strings.findFirstString(line, tokens);
					if (index != null) {
						return new Index<Index<String>>(li, index);
					}
					++li;
				}
			} catch (final FileNotFoundException ex) {
				IO.error(ex, "Cannot open the file", Strings.quote(file));
			} catch (final IOException ex) {
				IO.error(ex, "Cannot read the file", Strings.quote(file));
			} finally {
				closeReader(reader, null);
			}
		}
		return null;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the {@link File}, or {@code null} if there is no such
	 * occurrence.
	 *
	 * @param tokens the array of token {@link String} to find (may be {@code null})
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the {@link File}, or {@code null} if there is no
	 *         such occurrence
	 */
	public Index<Index<String>> findLastLine(final String... tokens) {
		return findLastLine(tokens, 0, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the {@link File}, seeking backward from the specified
	 * index, or {@code null} if there is no such occurrence.
	 *
	 * @param tokens        the array of token {@link String} to find (may be {@code null})
	 * @param fromLineIndex the line index to start seeking backward from (inclusive)
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the {@link File}, seeking backward from the
	 *         specified index, or {@code null} if there is no such occurrence
	 */
	public Index<Index<String>> findLastLine(final String[] tokens, final int fromLineIndex) {
		return findLastLine(tokens, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the {@link File}, seeking backward from the specified index
	 * to the specified index, or {@code null} if there is no such occurrence.
	 *
	 * @param tokens        the array of token {@link String} to find (may be {@code null})
	 * @param fromLineIndex the line index to start seeking backward from (inclusive)
	 * @param toLineIndex   the line index to finish seeking backward at (exclusive)
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the {@link File}, seeking backward from the
	 *         specified index to the specified index, or {@code null} if there is no such
	 *         occurrence
	 */
	public Index<Index<String>> findLastLine(final String[] tokens, final int fromLineIndex,
			final int toLineIndex) {
		if (Arrays.isNonEmpty(tokens)) {
			ReversedFileReader reader = null;
			try {
				reader = createReversedReader();
				int li = Files.skipLines(reader, fromLineIndex);
				String line;
				while (li < toLineIndex && (line = reader.readLine()) != null) {
					final Index<String> index = Strings.findLastString(line, tokens);
					if (index != null) {
						return new Index<Index<String>>(getLineCount() - 1 - li, index);
					}
					++li;
				}
			} catch (final FileNotFoundException ex) {
				IO.error(ex, "Cannot open the file", Strings.quote(file));
			} catch (final IOException ex) {
				IO.error(ex, "Cannot read the file", Strings.quote(file));
			} finally {
				closeReader(reader, null);
			}
		}
		return null;
	}

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the {@link File}, or {@code null} if there is no such
	 * occurrence.
	 *
	 * @param tokens the {@link Collection} of token {@link String} to find (may be {@code null})
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the {@link File}, or {@code null} if there is no
	 *         such occurrence
	 */
	public Index<Index<String>> findLastLine(final Collection<String> tokens) {
		return findLastLine(tokens, 0, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the {@link File}, seeking backward from the specified
	 * index, or {@code null} if there is no such occurrence.
	 *
	 * @param tokens        the {@link Collection} of token {@link String} to find (may be
	 *                      {@code null})
	 * @param fromLineIndex the line index to start seeking backward from (inclusive)
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the {@link File}, seeking backward from the
	 *         specified index, or {@code null} if there is no such occurrence
	 */
	public Index<Index<String>> findLastLine(final Collection<String> tokens,
			final int fromLineIndex) {
		return findLastLine(tokens, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the {@link File}, seeking backward from the specified index
	 * to the specified index, or {@code null} if there is no such occurrence.
	 *
	 * @param tokens        the {@link Collection} of token {@link String} to find (may be
	 *                      {@code null})
	 * @param fromLineIndex the line index to start seeking backward from (inclusive)
	 * @param toLineIndex   the line index to finish seeking backward at (exclusive)
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the {@link File}, seeking backward from the
	 *         specified index to the specified index, or {@code null} if there is no such
	 *         occurrence
	 */
	public Index<Index<String>> findLastLine(final Collection<String> tokens,
			final int fromLineIndex, final int toLineIndex) {
		if (Collections.isNonEmpty(tokens)) {
			ReversedFileReader reader = null;
			try {
				reader = createReversedReader();
				int li = Files.skipLines(reader, fromLineIndex);
				String line;
				while (li < toLineIndex && (line = reader.readLine()) != null) {
					final Index<String> index = Strings.findLastString(line, tokens);
					if (index != null) {
						return new Index<Index<String>>(getLineCount() - 1 - li, index);
					}
					++li;
				}
			} catch (final FileNotFoundException ex) {
				IO.error(ex, "Cannot open the file", Strings.quote(file));
			} catch (final IOException ex) {
				IO.error(ex, "Cannot read the file", Strings.quote(file));
			} finally {
				closeReader(reader, null);
			}
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the {@link File} exists.
	 *
	 * @return {@code true} if the {@link File} exists, {@code false} otherwise
	 * @throws SecurityException if there is a permission problem
	 */
	public boolean exists()
			throws SecurityException {
		return Files.exists(file);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLOSEABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Closes {@code this}.
	 */
	public void close() {
		closeAllReaders(null);
		closeWriter(null);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Clones {@code this}.
	 *
	 * @return a clone of {@code this}
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

	/**
	 * Disposes of system resources and performs a cleanup.
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>This method is called by the garbage collector on an {@link Object} when the garbage
	 * collection determines that there are no more references to the {@link Object}.</dd>
	 * </dl>
	 *
	 * @see PhantomReference
	 * @see WeakReference
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void finalize() {
		IO.trace(this, "is finalized");
		try {
			close();
		} finally {
			try {
				super.finalize();
			} catch (final Throwable ignored) {
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representative {@link String} of {@code this}.
	 *
	 * @return a representative {@link String} of {@code this}
	 */
	@Override
	public String toString() {
		return String.valueOf(file);
	}
}
