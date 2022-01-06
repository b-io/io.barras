/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2022 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.Charsets.UTF_16;
import static jupiter.common.io.InputOutput.BUFFER_SIZE;
import static jupiter.common.util.Strings.CR;
import static jupiter.common.util.Strings.CRLF;
import static jupiter.common.util.Strings.EMPTY;
import static jupiter.common.util.Strings.LF;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import jupiter.common.Charsets;
import jupiter.common.io.Resources;
import jupiter.common.util.Strings;

/**
 * {@link ReversedFileReader} reads the lines of a {@link File} reversely.
 */
public class ReversedFileReader
		implements Closeable {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The buffer size.
	 */
	protected final int bufferSize;

	/**
	 * The {@link Charset}.
	 */
	protected final Charset charset;
	/**
	 * The number of bytes to encode a {@code char} value with the {@link Charset}.
	 */
	protected final int charSize;

	/**
	 * The {@link RandomAccessFile}.
	 */
	protected final RandomAccessFile randomAccessFile;
	protected final long fileSize;
	/**
	 * The current {@link FilePart}.
	 */
	protected FilePart currentFilePart;
	protected final long filePartCount;

	/**
	 * The {@code byte} sequence of each newline.
	 */
	protected final byte[][] newlineSequences;
	/**
	 * The maximum number of bytes of a newline.
	 */
	protected final int maxNewLineSize;
	/**
	 * The flag specifying whether to skip the trailing newline.
	 */
	protected boolean skipTrailingNewline = false;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link ReversedFileReader} of the specified {@link File}.
	 * <p>
	 * @param file the {@link File} to read
	 * <p>
	 * @throws FileNotFoundException        if there is a problem with opening {@code file}
	 * @throws IOException                  if there is a problem with reading {@code file}
	 * @throws UnsupportedEncodingException if the {@code charset} byte order cannot be determined
	 */
	public ReversedFileReader(final File file)
			throws FileNotFoundException, IOException, UnsupportedEncodingException {
		this(file, BUFFER_SIZE, Charset.defaultCharset());
	}

	/**
	 * Constructs a {@link ReversedFileReader} of the specified {@link File} with the specified
	 * {@link Charset}.
	 * <p>
	 * @param file    the file to read
	 * @param charset the {@link Charset} to use
	 * <p>
	 * @throws FileNotFoundException        if there is a problem with opening {@code file}
	 * @throws IOException                  if there is a problem with reading {@code file}
	 * @throws UnsupportedEncodingException if the {@code charset} byte order cannot be determined
	 */
	public ReversedFileReader(final File file, final Charset charset)
			throws FileNotFoundException, IOException, UnsupportedEncodingException {
		this(file, BUFFER_SIZE, charset);
	}

	/**
	 * Constructs a {@link ReversedFileReader} with the given block size and {@link Charset}.
	 * <p>
	 * @param file       the file to read
	 * @param bufferSize the size of the internal buffer (ideally this should match with the block
	 *                   size of the file system)
	 * @param charset    the {@link Charset} of the file
	 * <p>
	 * @throws FileNotFoundException        if there is a problem with opening {@code file}
	 * @throws IOException                  if there is a problem with reading {@code file}
	 * @throws UnsupportedEncodingException if the {@code charset} byte order cannot be determined
	 */
	public ReversedFileReader(final File file, final int bufferSize, final Charset charset)
			throws FileNotFoundException, IOException, UnsupportedEncodingException {
		// Set the attributes
		this.bufferSize = bufferSize;
		this.charset = charset;

		// Prepare the encoding information
		if (charset == UTF_16) {
			throw new UnsupportedEncodingException(
					"Specify the byte order for UTF-16 (either UTF-16BE or UTF-16LE)");
		}
		charSize = Charsets.getCharSize(charset);
		newlineSequences = new byte[][] {CRLF.getBytes(charset), CR.getBytes(charset),
			LF.getBytes(charset)};
		maxNewLineSize = newlineSequences[0].length;

		// Open the file
		randomAccessFile = new RandomAccessFile(file, "r");
		fileSize = randomAccessFile.length();
		int lastPartSize = (int) (fileSize % bufferSize);
		if (lastPartSize > 0) {
			filePartCount = fileSize / bufferSize + 1;
		} else {
			filePartCount = fileSize / bufferSize;
			if (fileSize > 0) {
				lastPartSize = bufferSize;
			}
		}
		currentFilePart = new FilePart(filePartCount, lastPartSize, null);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLOSEABLE
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Closes {@code this}.
	 * <p>
	 * @throws IOException if there is a problem with closing {@code randomAccessFile}
	 */
	public void close()
			throws IOException {
		Resources.close(randomAccessFile);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// READERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the next line of the {@link File} from bottom to top, or {@code null} if the start of
	 * the {@link File} is reached.
	 * <p>
	 * @return the next line of the {@link File} from bottom to top, or {@code null} if the start of
	 *         the {@link File} is reached
	 * <p>
	 * @throws IOException if there is a problem with reading {@code randomAccessFile}
	 */
	public String readLine()
			throws IOException {
		// Read the next line of the file from bottom to top
		String line = currentFilePart.readLine();
		while (line == null && (currentFilePart = currentFilePart.rollOver()) != null) {
			line = currentFilePart.readLine();
		}
		// Skip any last empty line
		if (EMPTY.equals(line) && !skipTrailingNewline) {
			skipTrailingNewline = true;
			line = readLine();
		}
		return line;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected class FilePart {

		/**
		 * The part number.
		 */
		protected final long partNumber;

		/**
		 * The buffer {@code byte} array.
		 */
		protected final byte[] buffer;
		/**
		 * The buffer {@code byte} array remaining from the previous part.
		 */
		protected byte[] remainingBuffer;

		/**
		 * The current index of the last byte.
		 */
		protected int currentLastByteIndex;

		/**
		 * Constructs a {@link FilePart} with the specified part number, length and buffer
		 * {@code byte} array remaining from the previous part.
		 * <p>
		 * @param partNumber      the part number
		 * @param length          the length
		 * @param remainingBuffer the buffer {@code byte} array remaining from the previous part
		 * <p>
		 * @throws IOException if there is a problem with reading {@code randomAccessFile}
		 */
		protected FilePart(final long partNumber, final int length, final byte[] remainingBuffer)
				throws IOException {
			// Set the attributes
			this.partNumber = partNumber;
			buffer = new byte[length + (remainingBuffer != null ? remainingBuffer.length : 0)];

			// Read the part of the file
			final long offset = (partNumber - 1) * bufferSize;
			if (partNumber > 0) {
				randomAccessFile.seek(offset);
				if (randomAccessFile.read(buffer, 0, length) != length) {
					throw new IllegalStateException(
							"The number of read bytes is not equal to the number of requested bytes");
				}
			}

			// Copy the remaining buffer from the previous part to the buffer
			if (remainingBuffer != null) {
				System.arraycopy(remainingBuffer, 0, buffer, length, remainingBuffer.length);
			}

			// Set the remaining attributes
			currentLastByteIndex = buffer.length - 1;
			this.remainingBuffer = null;
		}

		/**
		 * Returns the next {@link FilePart}, or {@code null} if there is none.
		 * <p>
		 * @return the next {@link FilePart}, or {@code null} if there is none
		 * <p>
		 * @throws IOException if there was a problem with reading {@code randomAccessFile}
		 */
		protected FilePart rollOver()
				throws IOException {
			if (currentLastByteIndex >= 0) {
				throw new IllegalStateException(
						"The current last byte index is unexpectedly non-negative: " +
								currentLastByteIndex +
								"; the previous read line should be non-empty");
			}
			if (partNumber > 1) {
				return new FilePart(partNumber - 1, bufferSize, remainingBuffer);
			}
			if (remainingBuffer != null) {
				throw new IllegalStateException(
						"The remaining buffer of the last part is non-null: " +
								Strings.quote(new String(remainingBuffer, charset)));
			}
			return null;
		}

		/**
		 * Returns the next line of the {@link File} from bottom to top, or {@code null} if the
		 * start of the part is reached and the part is not last.
		 * <p>
		 * @return the next line of the {@link File} from bottom to top, or {@code null} if the
		 *         start of the part is reached and the part is not last
		 * <p>
		 * @throws IOException if there is a problem with reading {@code randomAccessFile}
		 */
		protected String readLine()
				throws IOException {
			// Initialize
			final boolean isLastPart = partNumber == 1;

			// Find the newline
			int bi = currentLastByteIndex;
			String line = null;
			while (bi >= 0) {
				if (!isLastPart && bi < maxNewLineSize) {
					// Skip the last few bytes and leave it to the next part
					createRemainingBuffer();
					break;
				}
				final int newlineSize = getNewlineSize(buffer, bi);
				if (newlineSize > 0) {
					final int lineStart = bi + 1;
					final int lineSize = currentLastByteIndex - lineStart + 1;
					if (lineSize < 0) {
						throw new IllegalStateException("Unexpected negative line size: " +
								lineSize);
					}
					final byte[] lineBuffer = new byte[lineSize];
					System.arraycopy(buffer, lineStart, lineBuffer, 0, lineSize);
					line = new String(lineBuffer, charset);
					currentLastByteIndex = bi - newlineSize;
					break; // found
				}
				bi -= charSize;
				if (bi < 0) {
					// Create the remaining buffer for the next part
					createRemainingBuffer();
					break; // not found
				}
			}
			// Handle the last part
			if (isLastPart && remainingBuffer != null) {
				// Read the last line of the file
				line = new String(remainingBuffer, charset);
				remainingBuffer = null;
			}
			// Return the next line, or null if the part is not last and the end is reached
			return line;
		}

		/**
		 * Creates the buffer containing any remaining bytes for the next part.
		 */
		protected void createRemainingBuffer() {
			final int lineLengthBytes = currentLastByteIndex + 1;
			if (lineLengthBytes > 0) {
				remainingBuffer = new byte[lineLengthBytes];
				System.arraycopy(buffer, 0, remainingBuffer, 0, lineLengthBytes);
			} else {
				remainingBuffer = null;
			}
			currentLastByteIndex = -1;
		}

		/**
		 * Returns the number of bytes of the first newline in the specified buffer {@code byte}
		 * array, or {@code 0} if there is no such occurrence.
		 * <p>
		 * @param buffer    the buffer {@code byte} array to read
		 * @param fromIndex the index to start reading the buffer {@code byte} array from
		 *                  (inclusive)
		 * <p>
		 * @return the number of bytes of the first newline in the specified buffer {@code byte}
		 *         array, or {@code 0} if there is no such occurrence
		 */
		protected int getNewlineSize(final byte[] buffer, final int fromIndex) {
			for (final byte[] newlineSequence : newlineSequences) {
				boolean match = true;
				for (int bi = newlineSequence.length - 1; bi >= 0; --bi) {
					final int byteIndex = fromIndex + bi - (newlineSequence.length - 1);
					match &= byteIndex >= 0 && buffer[byteIndex] == newlineSequence[bi];
				}
				if (match) {
					return newlineSequence.length;
				}
			}
			return 0;
		}
	}
}
