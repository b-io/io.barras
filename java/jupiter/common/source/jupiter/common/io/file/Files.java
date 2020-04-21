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

import static jupiter.common.io.InputOutput.BUFFER_SIZE;
import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.util.Formats.DEFAULT_CHARSET;
import static jupiter.common.util.Formats.NEW_LINE;
import static jupiter.common.util.Strings.EMPTY;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jupiter.common.exception.CopyFileException;
import jupiter.common.io.Content;
import jupiter.common.io.InputOutput;
import jupiter.common.io.Resources;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.list.Index;
import jupiter.common.struct.tuple.Triple;
import jupiter.common.test.Arguments;
import jupiter.common.test.FileArguments;
import jupiter.common.thread.LockedWorkQueue;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.time.Dates;
import jupiter.common.util.Strings;

public class Files {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@code char} name-separator.
	 */
	public static final char CHAR_SEPARATOR = '/';
	/**
	 * The name-separator {@link String}.
	 */
	public static final String SEPARATOR = "" + CHAR_SEPARATOR;

	/**
	 * All the {@code char} name-separators.
	 */
	public static final char[] ALL_CHAR_SEPARATORS = new char[] {CHAR_SEPARATOR,
		File.separatorChar};
	/**
	 * All the name-separator {@link String}.
	 */
	public static final String ALL_SEPARATORS = "" + CHAR_SEPARATOR + File.separatorChar;

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile String TEMP_DIR_PATH = System.getProperty("java.io.tmpdir");
	public static volatile int TEMP_FILE_NAME_LENGTH = 10;
	public static volatile String TEMP_FILE_EXTENSION = "tmp";

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link WorkQueue} used for copying the files (or directories).
	 */
	protected static volatile WorkQueue<Triple<File, File, Boolean>, Boolean> COPIER_QUEUE = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Files}.
	 */
	protected Files() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the current path.
	 * <p>
	 * @return the current path
	 */
	public static String getPath() {
		return getPath(new File("."));
	}

	/**
	 * Returns the path to the specified {@link File}.
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @return the path to the specified {@link File}
	 */
	public static String getPath(final File file) {
		return file.getAbsolutePath();
	}

	/**
	 * Returns the canonical path to the specified {@link File}.
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @return the canonical path to the specified {@link File}
	 * <p>
	 * @throws IOException       if there is a problem with querying the file system
	 * @throws SecurityException if there is a permission problem
	 */
	public static String getCanonicalPath(final File file)
			throws IOException {
		return file.getCanonicalPath();
	}

	/**
	 * Returns the relative path to the specified path {@link File} against the specified base
	 * {@link File}.
	 * <p>
	 * @param base the base {@link File} of the path to relativize
	 * @param path the path {@link File} to relativize
	 * <p>
	 * @return the relative path to the specified path {@link File} against the specified base
	 *         {@link File}
	 */
	public static String getRelativePath(final File base, final File path) {
		return base.toURI().relativize(path.toURI()).getPath();
	}

	//////////////////////////////////////////////

	/**
	 * Returns the path to the parent directory of the specified {@link File}, or {@code null} if it
	 * is the root.
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @return the path to the parent directory of the specified {@link File}, or {@code null} if it
	 *         is the root
	 */
	public static String getParentPath(final File file) {
		final String path = getPath(file);
		final int index = Strings.findLast(path, ALL_CHAR_SEPARATORS);
		if (index > 0) {
			return path.substring(0, index);
		}
		return null;
	}

	/**
	 * Returns the parent directory {@link File} of the specified {@link File}, or {@code null} if
	 * it is the root.
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @return the parent directory {@link File} of the specified {@link File}, or {@code null} if
	 *         it is the root
	 */
	public static File getParent(final File file) {
		final String path = getParentPath(file);
		if (path != null) {
			return new File(path);
		}
		return null;
	}

	/**
	 * Returns the canonical path to the parent directory of the specified {@link File}, or
	 * {@code null} if it is the root.
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @return the canonical path to the parent directory of the specified {@link File}, or
	 *         {@code null} if it is the root
	 * <p>
	 * @throws IOException       if there is a problem with querying the file system
	 * @throws SecurityException if there is a permission problem
	 */
	public static String getCanonicalParentPath(final File file)
			throws IOException {
		final String canonicalPath = getCanonicalPath(file);
		final int index = Strings.findLast(canonicalPath, ALL_CHAR_SEPARATORS);
		if (index > 0) {
			return canonicalPath.substring(0, index);
		}
		return null;
	}

	/**
	 * Returns the canonical parent directory {@link File} of the specified {@link File}, or
	 * {@code null} if it is the root.
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @return the canonical parent directory {@link File} of the specified {@link File}, or
	 *         {@code null} if it is the root
	 * <p>
	 * @throws IOException       if there is a problem with querying the file system
	 * @throws SecurityException if there is a permission problem
	 */
	public static File getCanonicalParent(final File file)
			throws IOException {
		final String canonicalPath = getCanonicalParentPath(file);
		if (canonicalPath != null) {
			return new File(canonicalPath);
		}
		return null;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the file name of the specified path.
	 * <p>
	 * @param path a {@link String}
	 * <p>
	 * @return the file name of the specified path
	 */
	public static String getName(final String path) {
		return path.substring(Strings.findLast(path, ALL_CHAR_SEPARATORS) + 1);
	}

	/**
	 * Returns the file name without the extension of the specified path.
	 * <p>
	 * @param path a {@link String}
	 * <p>
	 * @return the file name without the extension of the specified path
	 */
	public static String getNameWithoutExtension(final String path) {
		final String fileName = getName(path);
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	/**
	 * Returns the file extension of the specified path.
	 * <p>
	 * @param path a {@link String}
	 * <p>
	 * @return the file extension of the specified path
	 */
	public static String getExtension(final String path) {
		final String fileName = getName(path);
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the last-modified time of the specified {@link File}.
	 * <p>
	 * @param file      the {@link File} to set
	 * @param timestamp a length of time (in milliseconds) since the epoch (January 1, 1970,
	 *                  00:00:00 GMT)
	 */
	public static void setLastModified(final File file, final long timestamp) {
		if (exists(file)) {
			file.setLastModified(timestamp);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONTROLLERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parallelizes {@code this}.
	 */
	public static synchronized void parallelize() {
		IO.debug(EMPTY);

		// Initialize
		if (COPIER_QUEUE == null) {
			COPIER_QUEUE = new LockedWorkQueue<Triple<File, File, Boolean>, Boolean>(new Copier());
		} else {
			IO.debug("The copier queue ", COPIER_QUEUE, " has already started");
		}
	}

	/**
	 * Unparallelizes {@code this}.
	 */
	public static synchronized void unparallelize() {
		IO.debug(EMPTY);

		// Shutdown
		if (COPIER_QUEUE != null) {
			COPIER_QUEUE.shutdown();
			COPIER_QUEUE = null;
		}
	}

	/**
	 * Reparallelizes {@code this}.
	 */
	public static synchronized void reparallelize() {
		IO.debug(EMPTY);

		unparallelize();
		parallelize();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates all the directories of the specified {@link File}.
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @throws IOException       if there is a problem with creating the directories
	 * @throws SecurityException if there is a permission problem
	 */
	public static void createDirs(final File file)
			throws IOException {
		if (!exists(file) && !file.mkdirs()) {
			throw new IOException("Cannot create the directories " + Strings.quote(getPath(file)));
		}
	}

	/**
	 * Creates all the parent directories of the specified {@link File}.
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @throws IOException       if there is a problem with creating the directories
	 * @throws SecurityException if there is a permission problem
	 */
	public static void createParentDirs(final File file)
			throws IOException {
		createDirs(getParent(file));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a temporary {@link File}.
	 * <p>
	 * @return a temporary {@link File}
	 */
	public static File createTempFile() {
		return new File(Strings.join(TEMP_DIR_PATH, SEPARATOR,
				Strings.random(TEMP_FILE_NAME_LENGTH, 'A', '['), ".", TEMP_FILE_EXTENSION));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedInputStream} of the file denoted by the specified path.
	 * <p>
	 * @param path a {@link String}
	 * <p>
	 * @return a {@link BufferedInputStream} of the file denoted by the specified path
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening the file denoted by
	 *                               {@code path}
	 */
	public static BufferedInputStream createInputStream(final String path)
			throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream(path));
	}

	/**
	 * Creates a {@link BufferedInputStream} of the specified {@link File}.
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @return a {@link BufferedInputStream} of the specified {@link File}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening {@code file}
	 */
	public static BufferedInputStream createInputStream(final File file)
			throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream(file));
	}

	//////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedOutputStream} of the file denoted by the specified path.
	 * <p>
	 * @param path a {@link String}
	 * <p>
	 * @return a {@link BufferedOutputStream} of the file denoted by the specified path
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening the file denoted
	 *                               by {@code path}
	 */
	public static BufferedOutputStream createOutputStream(final String path)
			throws FileNotFoundException {
		return new BufferedOutputStream(new FileOutputStream(path));
	}

	/**
	 * Creates a {@link BufferedOutputStream} of the file denoted by the specified path.
	 * <p>
	 * @param path   a {@link String}
	 * @param append the flag specifying whether to append
	 * <p>
	 * @return a {@link BufferedOutputStream} of the file denoted by the specified path
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening the file denoted
	 *                               by {@code path}
	 */
	public static BufferedOutputStream createOutputStream(final String path, final boolean append)
			throws FileNotFoundException {
		return new BufferedOutputStream(new FileOutputStream(path, append));
	}

	/**
	 * Creates a {@link BufferedOutputStream} of the specified {@link File}.
	 * <p>
	 * @param file a {@link File}
	 * <p>
	 * @return a {@link BufferedOutputStream} of the specified {@link File}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 */
	public static BufferedOutputStream createOutputStream(final File file)
			throws FileNotFoundException {
		return new BufferedOutputStream(new FileOutputStream(file));
	}

	/**
	 * Creates a {@link BufferedOutputStream} of the specified {@link File}.
	 * <p>
	 * @param file   a {@link File}
	 * @param append the flag specifying whether to append
	 * <p>
	 * @return a {@link BufferedOutputStream} of the specified {@link File}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 */
	public static BufferedOutputStream createOutputStream(final File file, final boolean append)
			throws FileNotFoundException {
		return new BufferedOutputStream(new FileOutputStream(file, append));
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PROCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Copies the specified source {@link File} to the specified target {@link File} (preserving the
	 * file dates).
	 * <p>
	 * @param source the source {@link File} to copy from
	 * @param target the target {@link File} to copy to
	 * <p>
	 * @return {@code true} if the specified source {@link File} is copied to the specified target
	 *         {@link File} (preserving the file dates), {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code source} to {@code target}
	 */
	public static boolean copy(final File source, final File target)
			throws CopyFileException {
		return copy(source, target, false);
	}

	/**
	 * Copies the specified source {@link File} to the specified target {@link File} (preserving the
	 * file dates).
	 * <p>
	 * @param source the source {@link File} to copy from
	 * @param target the target {@link File} to copy to
	 * @param force  the flag specifying whether to delete the target {@link File} before copying
	 * <p>
	 * @return {@code true} if the specified source {@link File} is copied to the specified target
	 *         {@link File} (preserving the file dates), {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code source} to {@code target}
	 */
	public static boolean copy(final File source, final File target, final boolean force)
			throws CopyFileException {
		if (exists(target)) {
			if (force) {
				delete(target, true);
			} else {
				throw new CopyFileException(
						Strings.join("Target file ", Strings.quote(target), " already exists"));
			}
		}
		if (source.isDirectory()) {
			// Copy the directory
			try {
				boolean status = true;
				// Create the target directory
				final String targetDirPath = getPath(target);
				createDirs(target);
				// Copy the files to the directory
				final File[] files = source.listFiles();
				if (COPIER_QUEUE != null) {
					final long[] ids = new long[files.length];
					for (int fi = 0; fi < files.length; ++fi) {
						final File file = files[fi];
						ids[fi] = COPIER_QUEUE.submit(new Triple<File, File, Boolean>(file,
								new File(targetDirPath.concat(SEPARATOR)
										.concat(getRelativePath(source, file))),
								force));
					}
					for (final long id : ids) {
						status &= COPIER_QUEUE.get(id);
					}
				} else {
					for (final File file : files) {
						status &= copy(file, new File(targetDirPath.concat(SEPARATOR)
								.concat(getRelativePath(source, file))), force);
					}
				}
				return status;
			} catch (final IOException ex) {
				IO.error(ex);
			} finally {
				setLastModified(target, source.lastModified());
			}
		} else {
			// Copy the file
			FileChannel inputChannel = null;
			FileChannel outputChannel = null;
			try {
				createParentDirs(target);
				inputChannel = new FileInputStream(source).getChannel();
				outputChannel = new FileOutputStream(target).getChannel();
				final long size = inputChannel.size();
				long position = 0L;
				long byteCount;
				while (position < size) {
					final long remain = size - position;
					byteCount = outputChannel.transferFrom(inputChannel, position,
							remain > BUFFER_SIZE ? BUFFER_SIZE : remain);
					// Exit if there are no more bytes to transfer
					// (e.g. if the file is truncated after caching the size)
					if (byteCount == 0L) {
						break;
					}
					position += byteCount;
				}
				if (source.length() != target.length()) {
					throw new CopyFileException(
							Strings.join("Failed to copy the full content from ",
									Strings.quote(source), " to ", Strings.quote(target), " ",
									Arguments.expectedButFound(target.length(), source.length())));
				}
				return true;
			} catch (final IOException ex) {
				IO.error(ex);
			} finally {
				Resources.close(inputChannel);
				Resources.close(outputChannel);
				setLastModified(target, source.lastModified());
			}
		}
		return false;
	}

	/**
	 * Copies the specified source {@link File} to the specified target {@link File} from the
	 * specified line (without necessary preserving the file dates).
	 * <p>
	 * @param sourceFile    the source {@link File} to copy from
	 * @param targetFile    the target {@link File} to copy to
	 * @param force         the flag specifying whether to delete the target {@link File} before
	 *                      copying
	 * @param fromLineIndex the line index to start copying from (inclusive)
	 * <p>
	 * @return {@code true} if the specified source {@link File} is copied to the specified target
	 *         {@link File} from the specified line, {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code sourceFile} to
	 *                           {@code targetFile}
	 */
	public static boolean copy(final File sourceFile, final File targetFile, final boolean force,
			final int fromLineIndex)
			throws CopyFileException {
		return copy(sourceFile, targetFile, force, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Copies the specified source {@link File} to the specified target {@link File} between the
	 * specified lines (without necessary preserving the file dates).
	 * <p>
	 * @param sourceFile    the source {@link File} to copy from
	 * @param targetFile    the target {@link File} to copy to
	 * @param force         the flag specifying whether to delete the target {@link File} before
	 *                      copying
	 * @param fromLineIndex the line index to start copying from (inclusive)
	 * @param toLineIndex   the line index to finish copying at (exclusive)
	 * <p>
	 * @return {@code true} if the specified source {@link File} is copied to the specified target
	 *         {@link File} between the specified lines, {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code sourceFile} to
	 *                           {@code targetFile}
	 */
	public static boolean copy(final File sourceFile, final File targetFile, final boolean force,
			final int fromLineIndex, final int toLineIndex)
			throws CopyFileException {
		if (fromLineIndex == 0) {
			return copy(sourceFile, targetFile, force);
		}
		if (exists(targetFile)) {
			if (force) {
				delete(targetFile, true);
			} else {
				throw new CopyFileException(
						Strings.join("Target file ", Strings.quote(targetFile), " already exists"));
			}
		}
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			createParentDirs(targetFile);
			reader = new BufferedReader(new FileReader(sourceFile));
			writer = new PrintWriter(new FileWriter(targetFile));
			InputOutput.copy(reader, writer, fromLineIndex, toLineIndex);
			return true;
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			Resources.close(reader);
			Resources.close(writer);
		}
		return false;
	}

	//////////////////////////////////////////////

	/**
	 * Reads the data from the specified source {@link File} and writes it to the specified
	 * {@link OutputStream}.
	 * <p>
	 * @param sourceFile the source {@link File} to copy from
	 * @param output     the {@link OutputStream} to copy to
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code sourceFile} or writing to
	 *                     {@code output}
	 */
	public static long copy(final File sourceFile, final OutputStream output)
			throws IOException {
		return copy(sourceFile, output, new byte[BUFFER_SIZE]);
	}

	/**
	 * Reads the data from the specified source {@link File} and writes it to the specified
	 * {@link OutputStream} with the specified buffer.
	 * <p>
	 * @param sourceFile the source {@link File} to copy from
	 * @param output     the {@link OutputStream} to copy to
	 * @param buffer     the buffer {@code byte} array used for copying
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening {@code sourceFile}
	 * @throws IOException           if there is a problem with reading {@code sourceFile} or
	 *                               writing to {@code output}
	 */
	public static long copy(final File sourceFile, final OutputStream output, final byte[] buffer)
			throws FileNotFoundException, IOException {
		InputStream input = null;
		try {
			input = createInputStream(sourceFile);
			return InputOutput.copy(input, output, buffer);
		} finally {
			Resources.close(input);
		}
	}

	/**
	 * Reads the data from the specified source {@link File} and writes it to the specified
	 * {@link FileChannel}.
	 * <p>
	 * @param sourceFile the source {@link File} to copy from
	 * @param output     the {@link FileChannel} to copy to
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code sourceFile} or writing to
	 *                     {@code output}
	 */
	public static long copy(final File sourceFile, final FileChannel output)
			throws IOException {
		FileInputStream input = null;
		try {
			input = new FileInputStream(sourceFile);
			return output.transferFrom(input.getChannel(), 0, Long.MAX_VALUE);
		} finally {
			Resources.close(input);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Reads the data from the specified {@link InputStream} and writes it to the specified target
	 * {@link File}.
	 * <p>
	 * @param input      the {@link InputStream} to copy from
	 * @param targetFile the target {@link File} to copy to
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening
	 *                               {@code targetFile}
	 * @throws IOException           if there is a problem with reading {@code input} or writing to
	 *                               {@code targetFile}
	 */
	public static long copy(final InputStream input, final File targetFile)
			throws FileNotFoundException, IOException {
		return copy(input, targetFile, new byte[BUFFER_SIZE]);
	}

	/**
	 * Reads the data from the specified {@link InputStream} and writes it to the specified target
	 * {@link File} with the specified buffer.
	 * <p>
	 * @param input      the {@link InputStream} to copy from
	 * @param targetFile the target {@link File} to copy to
	 * @param buffer     the buffer {@code byte} array used for copying
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening
	 *                               {@code targetFile}
	 * @throws IOException           if there is a problem with reading {@code input} or writing to
	 *                               {@code targetFile}
	 */
	public static long copy(final InputStream input, final File targetFile, final byte[] buffer)
			throws FileNotFoundException, IOException {
		OutputStream output = null;
		try {
			output = createOutputStream(targetFile);
			return InputOutput.copy(input, output, buffer);
		} finally {
			Resources.close(output);
		}
	}

	/**
	 * Reads the data from the specified {@link ReadableByteChannel} and writes it to the specified
	 * target {@link File}.
	 * <p>
	 * @param input      the {@link ReadableByteChannel} to copy from
	 * @param targetFile the target {@link File} to copy to
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening
	 *                               {@code targetFile}
	 * @throws IOException           if there is a problem with reading {@code input} or writing to
	 *                               {@code targetFile}
	 */
	public static long copy(final ReadableByteChannel input, final File targetFile)
			throws FileNotFoundException, IOException {
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(targetFile);
			return output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
		} finally {
			Resources.close(output);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Deletes the specified {@link File}.
	 * <p>
	 * @param file the {@link File} to delete
	 * <p>
	 * @return {@code true} if the specified {@link File} is deleted, {@code false} otherwise
	 */
	public static boolean delete(final File file) {
		return delete(file, false);
	}

	/**
	 * Deletes the specified {@link File}.
	 * <p>
	 * @param file  the {@link File} to delete
	 * @param force the flag specifying whether to force deleting
	 * <p>
	 * @return {@code true} if the specified {@link File} is deleted, {@code false} otherwise
	 */
	public static boolean delete(final File file, final boolean force) {
		boolean isDeleted = false;
		// Test whether the file (or directory) exists
		if (exists(file)) {
			// Test whether it is not write protected
			if (file.canWrite()) {
				// Test whether it is a directory
				if (file.isDirectory()) {
					// • Directory
					final File[] fs = file.listFiles();
					// Test whether the directory contains files
					if (fs.length > 0) {
						// If force, delete the files of the directory recursively
						if (force) {
							for (final File f : fs) {
								delete(f, true);
							}
						} else {
							IO.warn("The directory ", Strings.quote(file), " is not empty");
						}
					}
				}
				// Attempt to delete the file (or directory)
				isDeleted = file.delete();
				if (!isDeleted) {
					IO.error("Cannot delete the file ", Strings.quote(file));
				}
			} else {
				IO.warn("The file ", Strings.quote(file), " is write protected");
			}
		} else if (!force) {
			IO.warn("The file ", Strings.quote(file), " does not exist");
		}
		return isDeleted;
	}

	//////////////////////////////////////////////

	/**
	 * Moves the specified source {@link File} to the specified target {@link File} (preserving the
	 * file dates).
	 * <p>
	 * @param source the source {@link File} to move from
	 * @param target the target {@link File} to move to
	 * <p>
	 * @return {@code true} if the specified source {@link File} is moved to the specified target
	 *         {@link File} (preserving the file dates), {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code source} to {@code target}
	 */
	public static boolean move(final File source, final File target)
			throws CopyFileException {
		return move(source, target, false);
	}

	/**
	 * Moves the specified source {@link File} to the specified target {@link File} (preserving the
	 * file dates).
	 * <p>
	 * @param source the source {@link File} to move from
	 * @param target the target {@link File} to move to
	 * @param force  the flag specifying whether to delete the target {@link File} before moving
	 * <p>
	 * @return {@code true} if the specified source {@link File} is moved to the specified target
	 *         {@link File} (preserving the file dates), {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code source} to {@code target}
	 */
	public static boolean move(final File source, final File target, final boolean force)
			throws CopyFileException {
		return copy(source, target, force) && delete(source, force);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all the {@link File} contained in the specified directory or subdirectories in an
	 * {@link ExtendedLinkedList}.
	 * <p>
	 * @param dir the directory {@link File} of the {@link File} to list
	 * <p>
	 * @return all the {@link File} contained in the specified directory or subdirectories in an
	 *         {@link ExtendedLinkedList}
	 */
	public static ExtendedLinkedList<File> listAll(final File dir) {
		// Check the arguments
		FileArguments.requireDir(dir);

		// List all the files contained in the directory or subdirectories
		final ExtendedLinkedList<File> allFiles = new ExtendedLinkedList<File>();
		final File[] files = dir.listFiles();
		for (final File file : files) {
			allFiles.add(file);
			if (file.isDirectory()) {
				allFiles.addAll(listAll(file));
			}
		}
		return allFiles;
	}

	/**
	 * Returns all the {@link File} contained in the specified directory or subdirectories and
	 * matching the specified name filter {@link String} in an {@link ExtendedLinkedList}.
	 * <p>
	 * @param dir        the directory {@link File} of the {@link File} to list
	 * @param nameFilter the name filter {@link String} of the {@link File} to list
	 * <p>
	 * @return all the {@link File} contained in the specified directory or subdirectories and
	 *         matching the specified name filter {@link String} in an {@link ExtendedLinkedList}
	 */
	public static ExtendedLinkedList<File> listAll(final File dir, final String nameFilter) {
		return listAll(dir, Pattern.compile(nameFilter));
	}

	/**
	 * Returns all the {@link File} contained in the specified directory or subdirectories and
	 * matching the specified name {@link Pattern} in an {@link ExtendedLinkedList}.
	 * <p>
	 * @param dir         the directory {@link File} of the {@link File} to list
	 * @param namePattern the name {@link Pattern} of the {@link File} to list
	 * <p>
	 * @return all the {@link File} contained in the specified directory or subdirectories and
	 *         matching the specified name {@link Pattern} in an {@link ExtendedLinkedList}
	 */
	public static ExtendedLinkedList<File> listAll(final File dir, final Pattern namePattern) {
		// Check the arguments
		FileArguments.requireDir(dir);

		// List all the files contained in the directory or subdirectories and matching the pattern
		final ExtendedLinkedList<File> allFiles = new ExtendedLinkedList<File>();
		final File[] files = dir.listFiles();
		for (final File file : files) {
			if (namePattern.matcher(file.getName()).matches()) {
				allFiles.add(file);
			}
			if (file.isDirectory()) {
				allFiles.addAll(listAll(file, namePattern));
			}
		}
		return allFiles;
	}

	/**
	 * Returns all the {@link File} contained in the specified directory or subdirectories and
	 * matching the specified name {@link Pattern} until the specified depth in an
	 * {@link ExtendedLinkedList}.
	 * <p>
	 * @param dir         the directory {@link File} of the {@link File} to list
	 * @param namePattern the name {@link Pattern} of the {@link File} to list
	 * @param depth       the number of subdirectories under the specified directory to search in
	 * <p>
	 * @return all the {@link File} contained in the specified directory or subdirectories and
	 *         matching the specified name {@link Pattern} until the specified depth in an
	 *         {@link ExtendedLinkedList}
	 */
	public static ExtendedLinkedList<File> listAll(final File dir, final Pattern namePattern,
			final int depth) {
		// Check the arguments
		FileArguments.requireDir(dir);

		// List all the files contained in the directory or subdirectories and matching the pattern
		// until the depth
		final ExtendedLinkedList<File> allFiles = new ExtendedLinkedList<File>();
		final File[] files = dir.listFiles();
		for (final File file : files) {
			if (namePattern.matcher(file.getName()).matches()) {
				allFiles.add(file);
			}
			if (depth > 0 && file.isDirectory()) {
				allFiles.addAll(listAll(file, namePattern, depth - 1));
			}
		}
		return allFiles;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes the specified line from the specified {@link File}.
	 * <p>
	 * @param file      a {@link File}
	 * @param lineIndex the index of the line to remove
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public static boolean remove(final File file, final int lineIndex) {
		return removeAll(file, lineIndex, lineIndex + 1);
	}

	//////////////////////////////////////////////

	/**
	 * Removes all the lines from the specified {@link File} from the specified line.
	 * <p>
	 * @param file          a {@link File}
	 * @param fromLineIndex the line index to start removing from (inclusive)
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public static boolean removeAll(final File file, final int fromLineIndex) {
		return removeAll(file, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Removes all the lines from the specified {@link File} between the specified lines.
	 * <p>
	 * @param file          a {@link File}
	 * @param fromLineIndex the line index to start removing from (inclusive)
	 * @param toLineIndex   the line index to finish removing at (exclusive)
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public static boolean removeAll(final File file, final int fromLineIndex,
			final int toLineIndex) {
		final FileHandler fileHandler = new FileHandler(file);
		try {
			return fileHandler.removeAll(fromLineIndex, toLineIndex);
		} finally {
			Resources.close(fileHandler);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Replaces all the substrings matching the specified regular expression {@link String} in the
	 * specified {@link File} by the specified {@link String}.
	 * <p>
	 * @param file        a {@link File}
	 * @param regex       the regular expression {@link String} to identify and replace (may be
	 *                    {@code null})
	 * @param replacement the {@link String} to replace by (may be {@code null})
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public static boolean replaceAll(final File file, final String regex, final String replacement) {
		return replaceAll(file, regex, replacement, 0, Integer.MAX_VALUE);
	}

	/**
	 * Replaces all the substrings matching the specified regular expression {@link String} in the
	 * specified {@link File} by the specified {@link String} from the specified line.
	 * <p>
	 * @param file          a {@link File}
	 * @param regex         the regular expression {@link String} to identify and replace (may be
	 *                      {@code null})
	 * @param replacement   the {@link String} to replace by (may be {@code null})
	 * @param fromLineIndex the line index to start replacing from (inclusive)
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public static boolean replaceAll(final File file, final String regex, final String replacement,
			final int fromLineIndex) {
		return replaceAll(file, regex, replacement, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Replaces all the substrings matching the specified regular expression {@link String} in the
	 * specified {@link File} by the specified {@link String} between the specified lines.
	 * <p>
	 * @param file          a {@link File}
	 * @param regex         the regular expression {@link String} to identify and replace (may be
	 *                      {@code null})
	 * @param replacement   the {@link String} to replace by (may be {@code null})
	 * @param fromLineIndex the line index to start replacing from (inclusive)
	 * @param toLineIndex   the line index to finish replacing at (exclusive)
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public static boolean replaceAll(final File file, final String regex, final String replacement,
			final int fromLineIndex, final int toLineIndex) {
		final FileHandler fileHandler = new FileHandler(file);
		try {
			return fileHandler.replaceAll(regex, replacement, fromLineIndex, toLineIndex);
		} finally {
			Resources.close(fileHandler);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Touches the specified {@link File}.
	 * <p>
	 * @param file the {@link File} to touch
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 */
	public static void touch(final File file)
			throws FileNotFoundException {
		if (!file.exists()) {
			Resources.close(new FileOutputStream(file));
		}
		file.setLastModified(Dates.createTimestamp());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Truncates the specified {@link File} from the specified line.
	 * <p>
	 * @param file          a {@link File}
	 * @param fromLineIndex the line index to start truncating from (inclusive)
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public static boolean truncate(final File file, final int fromLineIndex) {
		return truncate(file, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Truncates the specified {@link File} between the specified lines.
	 * <p>
	 * @param file          a {@link File}
	 * @param fromLineIndex the line index to start truncating from (inclusive)
	 * @param toLineIndex   the line index to finish truncating at (exclusive)
	 * <p>
	 * @return {@code true} if there is no {@link IOException}, {@code false} otherwise
	 */
	public static boolean truncate(final File file, final int fromLineIndex,
			final int toLineIndex) {
		final FileHandler fileHandler = new FileHandler(file);
		try {
			return fileHandler.truncate(fromLineIndex, toLineIndex);
		} finally {
			Resources.close(fileHandler);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Zips the specified directory (preserving the file dates).
	 * <p>
	 * @param sourceDir the source directory {@link File}
	 * <p>
	 * @return the number of zipped files
	 */
	public static int zipDir(final File sourceDir) {
		return zipDir(sourceDir, new File(getPath(sourceDir).concat(".zip")));
	}

	/**
	 * Zips the specified directory (preserving the file dates).
	 * <p>
	 * @param sourceDir  the source directory {@link File}
	 * @param targetFile the target ZIP {@link File}
	 * <p>
	 * @return the number of zipped files
	 */
	public static int zipDir(final File sourceDir, final File targetFile) {
		// Check the arguments
		FileArguments.requireDir(sourceDir);

		// Zip the directory
		int entryCount = 0;
		final byte[] buffer = new byte[1024];
		try {
			// Zip the files
			ZipOutputStream output = null;
			try {
				output = new ZipOutputStream(createOutputStream(targetFile));
				final List<File> sources = listAll(sourceDir);
				for (final File source : sources) {
					// Zip the file
					final ZipEntry entry = new ZipEntry(getRelativePath(sourceDir, source));
					entry.setTime(source.lastModified());
					output.putNextEntry(entry);
					IO.debug("Zip ", Strings.quote(entry.getName()),
							" to ", Strings.quote(targetFile));
					if (source.isDirectory()) {
						++entryCount;
					} else {
						try {
							copy(source, output, buffer);
							++entryCount;
						} catch (final FileNotFoundException ex) {
							IO.error(ex, "Cannot open the source file ", Strings.quote(source));
						} catch (final IOException ex) {
							IO.error(ex, "Cannot read the source file ", Strings.quote(source),
									" or write to the ZIP file ", Strings.quote(targetFile));
						}
					}
					output.closeEntry();
				}
			} catch (final FileNotFoundException ex) {
				IO.error(ex, "Cannot open or create the ZIP file ", Strings.quote(targetFile));
			} finally {
				Resources.close(output);
			}
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return entryCount;
	}

	//////////////////////////////////////////////

	/**
	 * Unzips the specified ZIP {@link File} (preserving the file dates).
	 * <p>
	 * @param sourceFile the source ZIP {@link File}
	 * <p>
	 * @return the number of unzipped files
	 */
	public static int unzipDir(final File sourceFile) {
		return unzipDir(sourceFile, new File(getNameWithoutExtension(sourceFile.getName())));
	}

	/**
	 * Unzips the specified ZIP {@link File} (preserving the file dates) to the specified directory.
	 * <p>
	 * @param sourceFile the source ZIP {@link File}
	 * @param targetDir  the target directory {@link File}
	 * <p>
	 * @return the number of unzipped files
	 */
	public static int unzipDir(final File sourceFile, final File targetDir) {
		// Unzip the directory
		int entryCount = 0;
		final byte[] buffer = new byte[1024];
		try {
			// Create the target directory
			final String targetDirPath = getPath(targetDir);
			createDirs(targetDir);
			// Unzip the files
			ZipInputStream input = null;
			try {
				input = new ZipInputStream(createInputStream(sourceFile));
				ZipEntry entry;
				while ((entry = input.getNextEntry()) != null) {
					// Unzip the file
					final File target = new File(targetDirPath.concat(SEPARATOR)
							.concat(entry.getName()));
					IO.debug("Unzip ", Strings.quote(entry.getName()),
							" to ", Strings.quote(target));
					if (entry.isDirectory()) {
						try {
							createDirs(target);
							++entryCount;
						} catch (final IOException ex) {
							IO.error(ex);
						}
					} else {
						final File parentDir = getParent(target);
						final long lastModified = parentDir.lastModified();
						try {
							copy(input, target, buffer);
							++entryCount;
						} catch (final FileNotFoundException ex) {
							IO.error(ex, "Cannot open or create the target file ",
									Strings.quote(target));
						} catch (final IOException ex) {
							IO.error(ex, "Cannot read the ZIP file ", Strings.quote(sourceFile),
									" or write to the target file ", Strings.quote(target));
						} finally {
							setLastModified(parentDir, lastModified);
						}
					}
					setLastModified(target, entry.getTime());
					input.closeEntry();
				}
			} catch (final FileNotFoundException ex) {
				IO.error(ex, "Cannot open the ZIP file ", Strings.quote(sourceFile));
			} finally {
				Resources.close(input);
			}
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return entryCount;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS / WRITERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedReader} of the specified {@link File}.
	 * <p>
	 * @param file the {@link File} to read
	 * <p>
	 * @return a {@link BufferedReader} of the specified {@link File}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening {@code file}
	 */
	public static BufferedReader createReader(final File file)
			throws FileNotFoundException {
		return createReader(file, DEFAULT_CHARSET);
	}

	/**
	 * Creates a {@link BufferedReader} of the specified {@link File} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param file    the {@link File} to read
	 * @param charset the {@link Charset} of the {@link File} to read
	 * <p>
	 * @return a {@link BufferedReader} of the specified {@link File} with the specified
	 *         {@link Charset}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening {@code file}
	 */
	public static BufferedReader createReader(final File file, final Charset charset)
			throws FileNotFoundException {
		return InputOutput.createReader(createInputStream(file), charset);
	}

	/**
	 * Creates a {@link ReversedFileReader} of the specified {@link File}.
	 * <p>
	 * @param file the {@link File} to read
	 * <p>
	 * @return a {@link ReversedFileReader} of the specified {@link File}
	 * <p>
	 * @throws FileNotFoundException        if there is a problem with opening {@code file}
	 * @throws IOException                  if there is a problem with reading {@code file}
	 * @throws UnsupportedEncodingException if the {@code charset} byte order cannot be determined
	 */
	public static ReversedFileReader createReversedReader(final File file)
			throws FileNotFoundException, IOException, UnsupportedEncodingException {
		return createReversedReader(file, DEFAULT_CHARSET);
	}

	/**
	 * Creates a {@link ReversedFileReader} of the specified {@link File} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param file    the {@link File} to read
	 * @param charset the {@link Charset} of the {@link File} to read
	 * <p>
	 * @return a {@link ReversedFileReader} of the specified {@link File} with the specified
	 *         {@link Charset}
	 * <p>
	 * @throws FileNotFoundException        if there is a problem with opening {@code file}
	 * @throws IOException                  if there is a problem with reading {@code file}
	 * @throws UnsupportedEncodingException if the {@code charset} byte order cannot be determined
	 */
	public static ReversedFileReader createReversedReader(final File file, final Charset charset)
			throws FileNotFoundException, IOException, UnsupportedEncodingException {
		return new ReversedFileReader(file, charset);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Content} of the specified {@link File}.
	 * <p>
	 * @param file the {@link File} to read
	 * <p>
	 * @return the {@link Content} of the specified {@link File}
	 */
	public static Content read(final File file) {
		return read(file, DEFAULT_CHARSET);
	}

	/**
	 * Returns the {@link Content} of the specified {@link File} with the specified {@link Charset}.
	 * <p>
	 * @param file    the {@link File} to read
	 * @param charset the {@link Charset} of the {@link File} to read
	 * <p>
	 * @return the {@link Content} of the specified {@link File} with the specified {@link Charset}
	 */
	public static Content read(final File file, final Charset charset) {
		try {
			return InputOutput.read(createInputStream(file), charset);
		} catch (final FileNotFoundException ex) {
			IO.error(ex, "Cannot open the file ", Strings.quote(file));
		} catch (final IOException ex) {
			IO.error(ex, "Cannot read the file ", Strings.quote(file));
		}
		return null;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the unzipped {@link Content} of the specified ZIP {@link File}.
	 * <p>
	 * @param file the {@link File} to unzip
	 * <p>
	 * @return the unzipped {@link Content} of the specified ZIP {@link File}
	 */
	public static Content unzip(final File file) {
		return unzip(file, DEFAULT_CHARSET);
	}

	/**
	 * Returns the unzipped {@link Content} of the specified ZIP {@link File} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param file    the {@link File} to unzip
	 * @param charset the {@link Charset} of the {@link File} to unzip
	 * <p>
	 * @return the unzipped {@link Content} of the specified ZIP {@link File} with the specified
	 *         {@link Charset}
	 */
	public static Content unzip(final File file, final Charset charset) {
		try {
			return InputOutput.read(new ZipInputStream(createInputStream(file)), charset);
		} catch (final FileNotFoundException ex) {
			IO.error(ex, "Cannot open the ZIP file ", Strings.quote(file));
		} catch (final IOException ex) {
			IO.error(ex, "Cannot read the ZIP file ", Strings.quote(file));
		}
		return null;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the ungzipped {@link Content} of the specified GZIP {@link File}.
	 * <p>
	 * @param file the {@link File} to ungzip
	 * <p>
	 * @return the ungzipped {@link Content} of the specified GZIP {@link File}
	 */
	public static Content ungzip(final File file) {
		return ungzip(file, DEFAULT_CHARSET);
	}

	/**
	 * Returns the ungzipped {@link Content} of the specified GZIP {@link File} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param file    the {@link File} to ungzip
	 * @param charset the {@link Charset} of the {@link File} to ungzip
	 * <p>
	 * @return the ungzipped {@link Content} of the specified GZIP {@link File} with the specified
	 *         {@link Charset}
	 */
	public static Content ungzip(final File file, final Charset charset) {
		try {
			return InputOutput.read(new GZIPInputStream(createInputStream(file)), charset);
		} catch (final FileNotFoundException ex) {
			IO.error(ex, "Cannot open the GZIP file ", Strings.quote(file));
		} catch (final IOException ex) {
			IO.error(ex, "Cannot read the GZIP file ", Strings.quote(file));
		}
		return null;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the number of lines of the specified {@link File}.
	 * <p>
	 * @param file the {@link File} to count the lines from
	 * <p>
	 * @return the number of lines of the specified {@link File}
	 */
	public static int countLines(final File file) {
		return countLines(file, DEFAULT_CHARSET, false);
	}

	/**
	 * Returns the number of lines of the specified {@link File} with the specified {@link Charset}.
	 * <p>
	 * @param file    the {@link File} to count the lines from
	 * @param charset the {@link Charset} of the {@link File} to count the lines from
	 * <p>
	 * @return the number of lines of the specified {@link File} with the specified {@link Charset}
	 */
	public static int countLines(final File file, final Charset charset) {
		return countLines(file, charset, false);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 * {@link File}.
	 * <p>
	 * @param file           the {@link File} to count the lines from
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 *         {@link File}
	 */
	public static int countLines(final File file, final boolean skipEmptyLines) {
		return countLines(file, DEFAULT_CHARSET, skipEmptyLines);
	}

	/**
	 * Returns the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 * {@link File} with the specified {@link Charset}.
	 * <p>
	 * @param file           the {@link File} to count the lines from
	 * @param charset        the {@link Charset} of the {@link File} to count the lines from
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 *         {@link File} with the specified {@link Charset}
	 */
	public static int countLines(final File file, final Charset charset,
			final boolean skipEmptyLines) {
		try {
			return InputOutput.countLines(createInputStream(file), charset);
		} catch (final FileNotFoundException ex) {
			IO.error(ex, "Cannot open the file ", Strings.quote(file));
		} catch (final IOException ex) {
			IO.error(ex, "Cannot read the file ", Strings.quote(file));
		}
		return -1;
	}

	//////////////////////////////////////////////

	/**
	 * Skips the specified number of lines of the specified {@link ReversedFileReader}.
	 * <p>
	 * @param reader    the {@link ReversedFileReader} to skip the lines from
	 * @param skipCount the number of lines to skip
	 * <p>
	 * @return the number of skipped lines
	 * <p>
	 * @throws IOException if there is a problem with reading with {@code reader}
	 */
	public static int skipLines(final ReversedFileReader reader, final int skipCount)
			throws IOException {
		int li = 0;
		while (li < skipCount && reader.readLine() != null) {
			++li;
		}
		return li;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedWriter} of the specified {@link File}.
	 * <p>
	 * @param file   the {@link File} to write to
	 * @param append the flag specifying whether to append
	 * <p>
	 * @return a {@link BufferedWriter} of the specified {@link File}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 */
	public static BufferedWriter createWriter(final File file, final boolean append)
			throws FileNotFoundException {
		return createWriter(file, DEFAULT_CHARSET, append);
	}

	/**
	 * Creates a {@link BufferedWriter} of the specified {@link File} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param file    the {@link File} to write to
	 * @param charset the {@link Charset} of the {@link File} to write to
	 * @param append  the flag specifying whether to append
	 * <p>
	 * @return a {@link BufferedWriter} of the specified {@link File} with the specified
	 *         {@link Charset}
	 * <p>
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code file}
	 */
	public static BufferedWriter createWriter(final File file, final Charset charset,
			final boolean append)
			throws FileNotFoundException {
		return InputOutput.createWriter(createOutputStream(file, append), charset);
	}

	//////////////////////////////////////////////

	/**
	 * Writes the specified content {@link String} to the specified {@link File}.
	 * <p>
	 * @param content the content {@link String} to write
	 * @param file    the {@link File} to write to
	 * <p>
	 * @return {@code true} if the specified content {@link String} is written to the specified
	 *         {@link File}, {@code false} otherwise
	 */
	public static boolean write(final String content, final File file) {
		return write(content, file, true, DEFAULT_CHARSET);
	}

	/**
	 * Writes or appends the specified content {@link String} to the specified {@link File} with the
	 * specified {@link Charset}.
	 * <p>
	 * @param content the content {@link String} to write
	 * @param file    the {@link File} to write to
	 * @param append  the flag specifying whether to append
	 * @param charset the {@link Charset} of the {@link File} to write to
	 * <p>
	 * @return {@code true} if the specified content {@link String} is written to the specified
	 *         {@link File} with the specified {@link Charset}, {@code false} otherwise
	 */
	public static boolean write(final String content, final File file, final boolean append,
			final Charset charset) {
		boolean isWritten = false;
		BufferedWriter writer = null;
		try {
			// Create the file writer with the charset
			writer = InputOutput.createWriter(createOutputStream(file, append), charset);
			// Write or append the content to the file
			writer.write(content);
			isWritten = true;
		} catch (final FileNotFoundException ex) {
			IO.error(ex, "Cannot open or create the file ", Strings.quote(file));
		} catch (final IOException ex) {
			IO.error(ex, "Cannot write to the file ", Strings.quote(file));
		} finally {
			Resources.close(writer);
		}
		return isWritten;
	}

	//////////////////////////////////////////////

	/**
	 * Writes the specified content {@link String} to the specified {@link File} and terminates the
	 * line.
	 * <p>
	 * @param content the content {@link String} to write
	 * @param file    the {@link File} to write to
	 * <p>
	 * @return {@code true} if the specified content {@link String} is written to the specified
	 *         {@link File}, {@code false} otherwise
	 */
	public static boolean writeLine(final String content, final File file) {
		return writeLine(content, file, true, DEFAULT_CHARSET);
	}

	/**
	 * Writes or appends the specified content {@link String} to the specified {@link File} with the
	 * specified {@link Charset} and terminates the line.
	 * <p>
	 * @param content the content {@link String} to write
	 * @param file    the {@link File} to write to
	 * @param append  the flag specifying whether to append
	 * @param charset the {@link Charset} of the {@link File} to write to
	 * <p>
	 * @return {@code true} if the specified content {@link String} is written to the specified
	 *         {@link File} with the specified {@link Charset}, {@code false} otherwise
	 */
	public static boolean writeLine(final String content, final File file, final boolean append,
			final Charset charset) {
		return write(content.concat(NEW_LINE), file, append, charset);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SEEKERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, or {@code null} if there is no
	 * such occurrence.
	 * <p>
	 * @param file   a {@link File}
	 * @param tokens the array of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, or {@code null} if
	 *         there is no such occurrence
	 */
	public static Index<Index<String>> findFirstLine(final File file, final String... tokens) {
		return findFirstLine(file, tokens, 0, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, seeking forward from the
	 * specified index, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param file          a {@link File}
	 * @param tokens        the array of token {@link String} to find (may be {@code null})
	 * @param fromLineIndex the line index to start seeking forward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, seeking forward from
	 *         the specified index, or {@code null} if there is no such occurrence
	 */
	public static Index<Index<String>> findFirstLine(final File file, final String[] tokens,
			final int fromLineIndex) {
		return findFirstLine(file, tokens, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, seeking forward from the
	 * specified index to the specified index, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param file          a {@link File}
	 * @param tokens        the array of token {@link String} to find (may be {@code null})
	 * @param fromLineIndex the line index to start seeking forward from (inclusive)
	 * @param toLineIndex   the line index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, seeking forward from
	 *         the specified index to the specified index, or {@code null} if there is no such
	 *         occurrence
	 */
	public static Index<Index<String>> findFirstLine(final File file, final String[] tokens,
			final int fromLineIndex, final int toLineIndex) {
		final FileHandler fileHandler = new FileHandler(file);
		try {
			return fileHandler.findFirstLine(tokens, fromLineIndex, toLineIndex);
		} finally {
			Resources.close(fileHandler);
		}
	}

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, or {@code null} if there is no
	 * such occurrence.
	 * <p>
	 * @param file   a {@link File}
	 * @param tokens the {@link Collection} of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, or {@code null} if
	 *         there is no such occurrence
	 */
	public static Index<Index<String>> findFirstLine(final File file,
			final Collection<String> tokens) {
		return findFirstLine(file, tokens, 0, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, seeking forward from the
	 * specified index, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param file          a {@link File}
	 * @param tokens        the {@link Collection} of token {@link String} to find (may be
	 *                      {@code null})
	 * @param fromLineIndex the line index to start seeking forward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, seeking forward from
	 *         the specified index, or {@code null} if there is no such occurrence
	 */
	public static Index<Index<String>> findFirstLine(final File file,
			final Collection<String> tokens, final int fromLineIndex) {
		return findFirstLine(file, tokens, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the first line containing the first occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, seeking forward from the
	 * specified index to the specified index, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param file          a {@link File}
	 * @param tokens        the {@link Collection} of token {@link String} to find (may be
	 *                      {@code null})
	 * @param fromLineIndex the line index to start seeking forward from (inclusive)
	 * @param toLineIndex   the line index to finish seeking forward at (exclusive)
	 * <p>
	 * @return the {@link Index} of the first line containing the first occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, seeking forward from
	 *         the specified index to the specified index, or {@code null} if there is no such
	 *         occurrence
	 */
	public static Index<Index<String>> findFirstLine(final File file,
			final Collection<String> tokens, final int fromLineIndex, final int toLineIndex) {
		final FileHandler fileHandler = new FileHandler(file);
		try {
			return fileHandler.findFirstLine(tokens, fromLineIndex, toLineIndex);
		} finally {
			Resources.close(fileHandler);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, or {@code null} if there is no
	 * such occurrence.
	 * <p>
	 * @param file   a {@link File}
	 * @param tokens the array of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, or {@code null} if
	 *         there is no such occurrence
	 */
	public static Index<Index<String>> findLastLine(final File file, final String... tokens) {
		return findLastLine(file, tokens, 0, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, seeking backward from the
	 * specified index, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param file          a {@link File}
	 * @param tokens        the array of token {@link String} to find (may be {@code null})
	 * @param fromLineIndex the line index to start seeking backward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, seeking backward from
	 *         the specified index, or {@code null} if there is no such occurrence
	 */
	public static Index<Index<String>> findLastLine(final File file, final String[] tokens,
			final int fromLineIndex) {
		return findLastLine(file, tokens, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, seeking backward from the
	 * specified index to the specified index, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param file          a {@link File}
	 * @param tokens        the array of token {@link String} to find (may be {@code null})
	 * @param fromLineIndex the line index to start seeking backward from (inclusive)
	 * @param toLineIndex   the line index to finish seeking backward at (exclusive)
	 * <p>
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, seeking backward from
	 *         the specified index to the specified index, or {@code null} if there is no such
	 *         occurrence
	 */
	public static Index<Index<String>> findLastLine(final File file, final String[] tokens,
			final int fromLineIndex, final int toLineIndex) {
		final FileHandler fileHandler = new FileHandler(file);
		try {
			return fileHandler.findLastLine(tokens, fromLineIndex, toLineIndex);
		} finally {
			Resources.close(fileHandler);
		}
	}

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, or {@code null} if there is no
	 * such occurrence.
	 * <p>
	 * @param file   a {@link File}
	 * @param tokens the {@link Collection} of token {@link String} to find (may be {@code null})
	 * <p>
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, or {@code null} if
	 *         there is no such occurrence
	 */
	public static Index<Index<String>> findLastLine(final File file,
			final Collection<String> tokens) {
		return findLastLine(file, tokens, 0, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, seeking backward from the
	 * specified index, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param file          a {@link File}
	 * @param tokens        the {@link Collection} of token {@link String} to find (may be
	 *                      {@code null})
	 * @param fromLineIndex the line index to start seeking backward from (inclusive)
	 * <p>
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, seeking backward from
	 *         the specified index, or {@code null} if there is no such occurrence
	 */
	public static Index<Index<String>> findLastLine(final File file,
			final Collection<String> tokens, final int fromLineIndex) {
		return findLastLine(file, tokens, fromLineIndex, Integer.MAX_VALUE);
	}

	/**
	 * Returns the {@link Index} of the last line containing the last occurrence of any of the
	 * specified token {@link String} in the specified {@link File}, seeking backward from the
	 * specified index to the specified index, or {@code null} if there is no such occurrence.
	 * <p>
	 * @param file          a {@link File}
	 * @param tokens        the {@link Collection} of token {@link String} to find (may be
	 *                      {@code null})
	 * @param fromLineIndex the line index to start seeking backward from (inclusive)
	 * @param toLineIndex   the line index to finish seeking backward at (exclusive)
	 * <p>
	 * @return the {@link Index} of the last line containing the last occurrence of any of the
	 *         specified token {@link String} in the specified {@link File}, seeking backward from
	 *         the specified index to the specified index, or {@code null} if there is no such
	 *         occurrence
	 */
	public static Index<Index<String>> findLastLine(final File file,
			final Collection<String> tokens, final int fromLineIndex, final int toLineIndex) {
		final FileHandler fileHandler = new FileHandler(file);
		try {
			return fileHandler.findLastLine(tokens, fromLineIndex, toLineIndex);
		} finally {
			Resources.close(fileHandler);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Object} is an instance of {@link File}.
	 * <p>
	 * @param object the {@link Object} to test
	 * <p>
	 * @return {@code true} if the specified {@link Object} is an instance of {@link File},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Object object) {
		return object instanceof File;
	}

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link File}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link File},
	 *         {@code false} otherwise
	 */
	public static boolean isFrom(final Class<?> c) {
		return File.class.isAssignableFrom(c);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link File} exists.
	 * <p>
	 * @param file the {@link File} to test for presence (may be {@code null})
	 * <p>
	 * @return {@code true} if the specified {@link File} exists, {@code false} otherwise
	 * <p>
	 * @throws SecurityException if there is a permission problem
	 */
	public static boolean exists(final File file)
			throws SecurityException {
		return file != null && file.exists();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Copier
			extends Worker<Triple<File, File, Boolean>, Boolean> {

		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs a {@link Copier}.
		 */
		protected Copier() {
			super();
		}

		@Override
		public Boolean call(final Triple<File, File, Boolean> input) {
			try {
				return copy(input.getFirst(), input.getSecond(), input.getThird());
			} catch (final CopyFileException ex) {
				IO.error(ex);
			}
			return true;
		}

		/**
		 * Clones {@code this}.
		 * <p>
		 * @return a clone of {@code this}
		 *
		 * @see ICloneable
		 */
		@Override
		public Copier clone() {
			return new Copier();
		}
	}
}
