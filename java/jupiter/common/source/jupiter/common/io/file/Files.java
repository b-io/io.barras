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

import static jupiter.common.io.IO.IO;
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
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jupiter.common.exception.CopyFileException;
import jupiter.common.io.Content;
import jupiter.common.io.Resources;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.struct.tuple.Triple;
import jupiter.common.test.Arguments;
import jupiter.common.test.FileArguments;
import jupiter.common.thread.LockedWorkQueue;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.util.Strings;

public class Files {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The buffer size.
	 */
	public static final int BUFFER_SIZE = 8192;

	/**
	 * The flag specifying whether to parallelize using a {@link WorkQueue}.
	 */
	public static volatile boolean PARALLELIZE = false;
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
	// GETTERS
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
	 * Returns the file name of the specified path.
	 * <p>
	 * @param path a {@link String}
	 * <p>
	 * @return the file name of the specified path
	 */
	public static String getName(final String path) {
		return path.substring(path.lastIndexOf(File.separator) + 1);
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
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static void setLastModified(final File file, final long time) {
		if (exists(file)) {
			file.setLastModified(time);
		}
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
			throw new IOException(Strings.join("Unable to create the directories ",
					Strings.quote(getPath(file))));
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
		createDirs(file.getParentFile());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@link BufferedInputStream} of the file denoted by the specified path.
	 * <p>
	 * @param path a {@link String}
	 * <p>
	 * @return a {@link BufferedInputStream} of the file denoted by the specified path
	 * <p>
	 * @throws FileNotFoundException if there is a problem with opening {@code path}
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
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code path}
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
	 * @throws FileNotFoundException if there is a problem with creating or opening {@code path}
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
	// READERS
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
		return IO.createReader(createInputStream(file), charset);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

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
	 * Returns the {@link Content} of the specified {@link File} using the specified
	 * {@link Charset}.
	 * <p>
	 * @param file    the {@link File} to read
	 * @param charset the {@link Charset} of the {@link File} to read
	 * <p>
	 * @return the {@link Content} of the specified {@link File} using the specified {@link Charset}
	 */
	public static Content read(final File file, final Charset charset) {
		try {
			return IO.read(createInputStream(file), charset);
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(file), ": ", ex);
		} catch (final IOException ex) {
			IO.error(ex);
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
			return IO.read(new ZipInputStream(createInputStream(file)), charset);
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(file), ": ", ex);
		} catch (final IOException ex) {
			IO.error(ex);
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
			return IO.read(new GZIPInputStream(createInputStream(file)), charset);
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(file), ": ", ex);
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of lines of the specified {@link File}.
	 * <p>
	 * @param file the {@link File} to count the lines from
	 * <p>
	 * @return the number of lines of the specified {@link File}
	 */
	public static int countLines(final File file) {
		return countLines(file, DEFAULT_CHARSET);
	}

	/**
	 * Returns the number of lines of the specified {@link File} using the specified
	 * {@link Charset}.
	 * <p>
	 * @param file    the {@link File} to count the lines from
	 * @param charset the {@link Charset} of the {@link File} to count the lines from
	 * <p>
	 * @return the number of lines of the specified {@link File} using the specified {@link Charset}
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
	 * {@link File} using the specified {@link Charset}.
	 * <p>
	 * @param file           the {@link File} to count the lines from
	 * @param charset        the {@link Charset} of the {@link File} to count the lines from
	 * @param skipEmptyLines the flag specifying whether to skip empty lines
	 * <p>
	 * @return the number of lines (or non-empty lines if {@code skipEmptyLines}) of the specified
	 *         {@link File} using the specified {@link Charset}
	 */
	public static int countLines(final File file, final Charset charset,
			final boolean skipEmptyLines) {
		try {
			return IO.countLines(createInputStream(file), charset);
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(file), ": ", ex);
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return -1;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WRITERS
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
		return IO.createWriter(createOutputStream(file, append), charset);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes the specified {@link String} to the specified {@link File}.
	 * <p>
	 * @param content the content {@link String} to write
	 * @param file    the {@link File} to write to
	 * <p>
	 * @return {@code true} if the specified {@link String} is written to the specified
	 *         {@link File}, {@code false} otherwise
	 */
	public static boolean writeLine(final String content, final File file) {
		return writeLine(content, file, true, DEFAULT_CHARSET);
	}

	/**
	 * Writes or appends the specified {@link String} to the specified {@link File}.
	 * <p>
	 * @param content the content {@link String} to write
	 * @param file    the {@link File} to write to
	 * @param append  the flag specifying whether to append
	 * @param charset the {@link Charset} of the {@link File} to write to
	 * <p>
	 * @return {@code true} if the specified {@link String} is written to the specified
	 *         {@link File}, {@code false} otherwise
	 */
	public static boolean writeLine(final String content, final File file, final boolean append,
			final Charset charset) {
		boolean isWritten = false;
		BufferedWriter writer = null;
		try {
			// Create the file writer with the charset
			writer = IO.createWriter(createOutputStream(file, append), charset);
			// Write or append the content to the file
			writer.write(content + NEW_LINE);
			isWritten = true;
		} catch (final FileNotFoundException ex) {
			IO.error("Unable to find the specified file ", Strings.quote(file), ": ", ex);
		} catch (final IOException ex) {
			IO.error(ex);
		} finally {
			Resources.close(writer);
		}
		return isWritten;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parallelizes {@code this}.
	 */
	public static synchronized void parallelize() {
		IO.debug(EMPTY);

		// Initialize
		if (COPIER_QUEUE == null) {
			COPIER_QUEUE = new LockedWorkQueue<Triple<File, File, Boolean>, Boolean>(new Copier());
			PARALLELIZE = true;
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
			PARALLELIZE = false;
			COPIER_QUEUE.shutdown();
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

	/**
	 * Copies the specified source {@link File} to the specified target {@link File} (preserving the
	 * file dates).
	 * <p>
	 * @param source the source {@link File} to read from
	 * @param target the target {@link File} to write to
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
				if (PARALLELIZE) {
					final long[] ids = new long[files.length];
					for (int i = 0; i < files.length; ++i) {
						final File file = files[i];
						ids[i] = COPIER_QUEUE.submit(new Triple<File, File, Boolean>(file, new File(
								targetDirPath + File.separator + getRelativePath(source, file)),
								force));
					}
					for (final long id : ids) {
						status &= COPIER_QUEUE.get(id);
					}
				} else {
					for (final File file : files) {
						status &= copy(file, new File(
								targetDirPath + File.separator + getRelativePath(source, file)),
								force);
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
	 * specified line index (without necessary preserving the file dates).
	 * <p>
	 * @param source   the source {@link File} to read from
	 * @param target   the target {@link File} to write to
	 * @param force    the flag specifying whether to delete the target {@link File} before copying
	 * @param fromLine the line index to start copying forward from
	 * <p>
	 * @return {@code true} if the specified source {@link File} is copied to the specified target
	 *         {@link File} from the specified line index (without necessary preserving the file
	 *         dates), {@code false} otherwise
	 * <p>
	 * @throws CopyFileException if there is a problem with copying {@code source} to {@code target}
	 */
	public static boolean copy(final File source, final File target, final boolean force,
			final int fromLine)
			throws CopyFileException {
		if (fromLine == 0) {
			return copy(source, target, force);
		}
		if (exists(target)) {
			if (force) {
				delete(target, true);
			} else {
				throw new CopyFileException(
						Strings.join("Target file ", Strings.quote(target), " already exists"));
			}
		}
		if (source.isDirectory()) {
			return copy(source, target, force);
		}
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			createParentDirs(target);
			reader = new BufferedReader(new FileReader(source));
			writer = new PrintWriter(new FileWriter(target));
			IO.copy(reader, writer, fromLine);
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
	 * Reads the data from the specified source {@link File}, writes it to the specified
	 * {@link OutputStream} and returns the number of copied {@code byte}.
	 * <p>
	 * @param source the source {@link File} to read from
	 * @param output the {@link OutputStream} to write to
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code source} or writing to
	 *                     {@code output}
	 */
	public static long copy(final File source, final OutputStream output)
			throws IOException {
		return copy(source, output, new byte[BUFFER_SIZE]);
	}

	/**
	 * Reads the data from the specified source {@link File}, writes it to the specified
	 * {@link OutputStream} with the specified buffer and returns the number of copied {@code byte}.
	 * <p>
	 * @param source the source {@link File} to read from
	 * @param output the {@link OutputStream} to write to
	 * @param buffer the buffer {@code byte} array used for copying
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code source} or writing to
	 *                     {@code output}
	 */
	public static long copy(final File source, final OutputStream output, final byte[] buffer)
			throws IOException {
		InputStream input = null;
		try {
			input = createInputStream(source);
			return IO.copy(input, output, buffer);
		} finally {
			Resources.close(input);
		}
	}

	//////////////////////////////////////////////

	/**
	 * Reads the data from the specified {@link InputStream}, writes it to the specified target
	 * {@link File} and returns the number of copied {@code byte}.
	 * <p>
	 * @param input  the {@link InputStream} to read from
	 * @param target the target {@link File} to write to
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code input} or writing to
	 *                     {@code target}
	 */
	public static long copy(final InputStream input, final File target)
			throws IOException {
		return copy(input, target, new byte[BUFFER_SIZE]);
	}

	/**
	 * Reads the data from the specified {@link InputStream}, writes it to the specified target
	 * {@link File} with the specified buffer and returns the number of copied {@code byte}.
	 * <p>
	 * @param input  the {@link InputStream} to read from
	 * @param target the target {@link File} to write to
	 * @param buffer the buffer {@code byte} array used for copying
	 * <p>
	 * @return the number of copied {@code byte}
	 * <p>
	 * @throws IOException if there is a problem with reading {@code input} or writing to
	 *                     {@code target}
	 */
	public static long copy(final InputStream input, final File target, final byte[] buffer)
			throws IOException {
		OutputStream output = null;
		try {
			output = createOutputStream(target);
			return IO.copy(input, output, buffer);
		} finally {
			Resources.close(output);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Deletes the specified {@link File}.
	 * <p>
	 * @param file the {@link File} to delete
	 * <p>
	 * @return {@code true} if {@code file} is deleted, {@code false} otherwise
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
					final File[] files = file.listFiles();
					// Test whether the directory contains files
					if (files.length > 0) {
						// If force, delete the files of the directory recursively
						if (force) {
							for (final File f : files) {
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

	/**
	 * Returns the {@link ExtendedLinkedList} of all the {@link File} contained in the specified
	 * directory or subdirectories.
	 * <p>
	 * @param dir a {@link File}
	 * <p>
	 * @return the {@link ExtendedLinkedList} of all the {@link File} contained in the specified
	 *         directory or subdirectories
	 */
	public static ExtendedLinkedList<File> listAll(final File dir) {
		final ExtendedLinkedList<File> list = new ExtendedLinkedList<File>();
		for (final File file : dir.listFiles()) {
			list.add(file);
			if (file.isDirectory()) {
				list.addAll(listAll(file));
			}
		}
		return list;
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of all the {@link File} contained in the specified
	 * directory or subdirectories and matching the specified pattern {@link String}.
	 * <p>
	 * @param dir     the directory {@link File} of the {@link File} to list
	 * @param pattern the pattern {@link String} of the {@link File} to list
	 * <p>
	 * @return the {@link ExtendedLinkedList} of all the {@link File} contained in the specified
	 *         directory or subdirectories and matching the specified pattern {@link String}
	 */
	public static ExtendedLinkedList<File> listAll(final File dir, final String pattern) {
		return listAll(dir, Pattern.compile(pattern));
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of all the {@link File} contained in the specified
	 * directory or subdirectories and matching the specified {@link Pattern}.
	 * <p>
	 * @param dir     the directory {@link File} of the {@link File} to list
	 * @param pattern the name {@link Pattern} of the {@link File} to list
	 * <p>
	 * @return the {@link ExtendedLinkedList} of all the {@link File} contained in the specified
	 *         directory or subdirectories and matching the specified {@link Pattern}
	 */
	public static ExtendedLinkedList<File> listAll(final File dir, final Pattern pattern) {
		final ExtendedLinkedList<File> list = new ExtendedLinkedList<File>();
		for (final File file : dir.listFiles()) {
			if (pattern.matcher(file.getName()).matches()) {
				list.add(file);
			}
			if (file.isDirectory()) {
				list.addAll(listAll(file, pattern));
			}
		}
		return list;
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of all the {@link File} contained in the specified
	 * directory or subdirectories and matching the specified {@link Pattern} until the specified
	 * depth.
	 * <p>
	 * @param dir     the directory {@link File} of the {@link File} to list
	 * @param pattern the name {@link Pattern} of the {@link File} to list
	 * @param depth   the number of subdirectories under the specified directory to search in
	 * <p>
	 * @return the {@link ExtendedLinkedList} of all the {@link File} contained in the specified
	 *         directory or subdirectories and matching the specified {@link Pattern} until the
	 *         specified depth
	 */
	public static ExtendedLinkedList<File> listAll(final File dir, final Pattern pattern,
			final int depth) {
		final ExtendedLinkedList<File> list = new ExtendedLinkedList<File>();
		for (final File file : dir.listFiles()) {
			if (pattern.matcher(file.getName()).matches()) {
				list.add(file);
			}
			if (depth > 0 && file.isDirectory()) {
				list.addAll(listAll(file, pattern, depth - 1));
			}
		}
		return list;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Zips the specified directory (preserving the file dates) and returns the number of zipped
	 * files.
	 * <p>
	 * @param sourceDir the source directory {@link File}
	 * <p>
	 * @return the number of zipped files
	 */
	public static int zipDir(final File sourceDir) {
		return zipDir(sourceDir, new File(getPath(sourceDir) + ".zip"));
	}

	/**
	 * Zips the specified directory (preserving the file dates) and returns the number of zipped
	 * files.
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
				for (final File source : listAll(sourceDir)) {
					// Zip the file
					final ZipEntry entry = new ZipEntry(getRelativePath(sourceDir, source));
					entry.setTime(source.lastModified());
					output.putNextEntry(entry);
					IO.debug("Zip ", Strings.quote(entry.getName()), " to ",
							Strings.quote(targetFile));
					if (source.isDirectory()) {
						++entryCount;
					} else {
						copy(source, output, buffer);
						++entryCount;
					}
					output.closeEntry();
				}
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
	 * Unzips the specified ZIP {@link File} (preserving the file dates) and returns the number of
	 * unzipped files.
	 * <p>
	 * @param sourceFile the source ZIP {@link File}
	 * <p>
	 * @return the number of unzipped files
	 */
	public static int unzipDir(final File sourceFile) {
		return unzipDir(sourceFile, new File(getNameWithoutExtension(sourceFile.getName())));
	}

	/**
	 * Unzips the specified ZIP {@link File} (preserving the file dates) to the specified directory
	 * and returns the number of unzipped files.
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
					final File target = new File(targetDirPath + File.separator + entry.getName());
					IO.debug("Unzip ", Strings.quote(entry.getName()), " to ",
							Strings.quote(target));
					if (entry.isDirectory()) {
						try {
							createDirs(target);
							++entryCount;
						} catch (final IOException ex) {
							IO.error(ex);
						}
					} else {
						final File parentDir = target.getParentFile();
						final long lastModified = parentDir.lastModified();
						try {
							Files.copy(input, target, buffer);
							++entryCount;
						} catch (final IOException ex) {
							IO.error(ex);
						} finally {
							setLastModified(parentDir, lastModified);
						}
					}
					setLastModified(target, entry.getTime());
					input.closeEntry();
				}
			} finally {
				Resources.close(input);
			}
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return entryCount;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link Class} is assignable to a {@link File}.
	 * <p>
	 * @param c the {@link Class} to test
	 * <p>
	 * @return {@code true} if the specified {@link Class} is assignable to a {@link File},
	 *         {@code false} otherwise
	 */
	public static boolean is(final Class<?> c) {
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
			} catch (final Exception ex) {
				IO.error(ex);
			}
			return true;
		}

		/**
		 * Creates a copy of {@code this}.
		 * <p>
		 * @return a copy of {@code this}
		 *
		 * @see jupiter.common.model.ICloneable
		 */
		@Override
		public Copier clone() {
			return new Copier();
		}
	}
}
