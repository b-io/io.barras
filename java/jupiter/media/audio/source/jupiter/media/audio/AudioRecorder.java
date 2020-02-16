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
package jupiter.media.audio;

import static jupiter.common.io.IO.IO;
import static jupiter.media.audio.Audio.DEFAULT_AUDIO_FORMAT;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JOptionPane;

import jupiter.common.io.file.Files;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.thread.Threads;
import jupiter.common.time.Dates;
import jupiter.common.util.Strings;

public class AudioRecorder
		implements ICloneable<AudioRecorder>, Serializable {

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
	 * The {@link AudioFormat}.
	 */
	protected final AudioFormat format;

	/**
	 * The {@link AudioTargetInterface}.
	 */
	protected AudioTargetInterface targetInterface = null;
	/**
	 * The internal {@link Lock}.
	 */
	protected final Lock lock = new ReentrantLock(true);
	protected final Condition lockCondition = lock.newCondition();

	/**
	 * The recording time in seconds.
	 */
	protected Integer recordingTime = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link AudioRecorder} by default.
	 */
	public AudioRecorder() {
		this(DEFAULT_AUDIO_FORMAT);
	}

	/**
	 * Constructs an {@link AudioRecorder} with the specified {@link AudioFormat}.
	 * <p>
	 * @param format the {@link AudioFormat}
	 */
	public AudioRecorder(final AudioFormat format) {
		this.format = format;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// SETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the {@link AudioTargetInterface}.
	 * <p>
	 * @param targetInterface an {@link AudioTargetInterface}
	 */
	public void setTargetInterface(final AudioTargetInterface targetInterface) {
		this.targetInterface = targetInterface;
	}

	/**
	 * Sets the {@link AudioTargetInterface} with the specified mixer name.
	 * <p>
	 * @param mixerName a {@link String}
	 */
	public void setTargetInterface(final String mixerName) {
		final ExtendedLinkedList<AudioTargetInterface> targetInterfaces = Audio.getTargetInterfaces(
				mixerName);
		final int targetInterfaceCount = targetInterfaces.size();
		if (targetInterfaceCount > 0) {
			if (targetInterfaceCount > 1) {
				IO.warn("Multiple microphones with the mixer name ", Strings.quote(mixerName), ": ",
						targetInterfaces);
			}
			targetInterface = targetInterfaces.getFirst();
			IO.info("Select the microphone: ", targetInterface);
		} else {
			IO.warn("No microphone with the mixer name ", Strings.quote(mixerName));
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Captures the audio and returns the WAV {@link File} containing the recording.
	 * <p>
	 * @return the WAV {@link File} containing the recording
	 */
	public File capture() {
		new Thread() {
			@Override
			public void run() {
				try {
					lock.lock();
					try {
						// Prompt to select the microphone
						while (targetInterface == null || targetInterface.line == null) {
							final ExtendedLinkedList<AudioTargetInterface> targetInterfaces = Audio.getTargetInterfaces();
							if (targetInterfaces.isEmpty()) {
								throw new IllegalStateException("No available microphone");
							}
							targetInterface = (AudioTargetInterface) JOptionPane.showInputDialog(
									null, "Select the microphone:", "Microphone Selection",
									JOptionPane.QUESTION_MESSAGE, null, targetInterfaces.toArray(),
									targetInterfaces.getFirst());
						}

						// Prompt to enter the recording time
						while (recordingTime == null) {
							recordingTime = Integer.parseInt(JOptionPane.showInputDialog(null,
									"How many seconds do you want to record?", "Recording Time",
									JOptionPane.QUESTION_MESSAGE)); // [s]
						}

						// Start to record
						lockCondition.signal();
					} finally {
						lock.unlock();
					}
					Threads.sleep(recordingTime * 1000L);
				} catch (final HeadlessException ex) {
					IO.error("The environment does not support display", ex);
				} catch (final NumberFormatException ex) {
					IO.error("Cannot parse the recording time", ex);
				} finally {
					if (targetInterface != null && targetInterface.line != null) {
						targetInterface.line.stop();
					}
				}
			}
		}.start();
		return record();
	}

	/**
	 * Records the audio and returns the WAV {@link File} containing the recording.
	 * <p>
	 * @return the WAV {@link File} containing the recording
	 */
	protected File record() {
		File targetFile = null;
		try {
			lock.lock();
			try {
				while (targetInterface == null || targetInterface.line == null ||
						recordingTime == null) {
					lockCondition.await();
				}
			} catch (final InterruptedException ignored) {
			} finally {
				lock.unlock();
			}
			targetFile = record(targetInterface, format);
		} finally {
			if (targetInterface != null && targetInterface.line != null) {
				targetInterface.line.close();
			}
			if (targetFile != null) {
				JOptionPane.showMessageDialog(null, "Finish recording to " +
						Strings.quote(Files.getPath(targetFile)) + ".");
			} else {
				JOptionPane.showMessageDialog(null, "Fail to record.");
			}
		}
		return targetFile;
	}

	/**
	 * Records the audio with the specified {@link AudioTargetInterface} and returns the WAV
	 * {@link File} containing the recording.
	 * <p>
	 * @param targetInterface the {@link AudioTargetInterface} to record with
	 * <p>
	 * @return the WAV {@link File} containing the recording
	 */
	public static File record(final AudioTargetInterface targetInterface) {
		final File targetFile = new File(Dates.createTimestamp() + ".wav");
		record(targetInterface, targetFile, DEFAULT_AUDIO_FORMAT);
		return targetFile;
	}

	/**
	 * Records the audio with the specified {@link AudioTargetInterface} to the specified
	 * {@link AudioFormat} and returns the WAV {@link File} containing the recording.
	 * <p>
	 * @param targetInterface the {@link AudioTargetInterface} to record with
	 * @param format          the {@link AudioFormat} to record to
	 * <p>
	 * @return the WAV {@link File} containing the recording
	 */
	public static File record(final AudioTargetInterface targetInterface,
			final AudioFormat format) {
		final File targetFile = new File(Dates.createTimestamp() + ".wav");
		record(targetInterface, targetFile, format);
		return targetFile;
	}

	/**
	 * Records the audio with the specified {@link AudioTargetInterface} to the specified WAV
	 * {@link File}.
	 * <p>
	 * @param targetInterface the {@link AudioTargetInterface} to record with
	 * @param targetFile      the WAV {@link File} to record to
	 */
	public static void record(final AudioTargetInterface targetInterface, final File targetFile) {
		record(targetInterface, targetFile, DEFAULT_AUDIO_FORMAT);
	}

	/**
	 * Records the audio with the specified {@link AudioTargetInterface} to the specified WAV
	 * {@link File} and {@link AudioFormat}.
	 * <p>
	 * @param targetInterface the {@link AudioTargetInterface} to record with
	 * @param targetFile      the WAV {@link File} to record to
	 * @param format          the {@link AudioFormat} to record to
	 */
	public static void record(final AudioTargetInterface targetInterface, final File targetFile,
			final AudioFormat format) {
		try {
			IO.info("Start recording to ", Strings.quote(Files.getPath(targetFile)));
			targetInterface.line.open(format);
			targetInterface.line.start();
			final AudioInputStream input = new AudioInputStream(targetInterface.line);
			AudioSystem.write(input, Type.WAVE, targetFile);
			IO.info("Finish recording to ", Strings.quote(Files.getPath(targetFile)));
		} catch (final LineUnavailableException ex) {
			IO.error("Cannot open the microphone due to resource restrictions", ex);
		} catch (final IOException ex) {
			IO.error(ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see jupiter.common.model.ICloneable
	 */
	@Override
	public AudioRecorder clone() {
		try {
			return (AudioRecorder) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
		}
	}
}
