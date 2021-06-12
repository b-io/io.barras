/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2021 Florian Barras <https://barras.io> (florian@barras.io)
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

import static jupiter.common.util.Strings.EMPTY;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import jupiter.common.struct.list.ExtendedLinkedList;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

public class Audio {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The basic {@link AudioFormat}.
	 */
	public static final AudioFormat BASIC_AUDIO_FORMAT = new AudioFormat(11025, 16, 2, true, true);
	/**
	 * The radio {@link AudioFormat}.
	 */
	public static final AudioFormat RADIO_AUDIO_FORMAT = new AudioFormat(16000, 16, 2, true, true);
	/**
	 * The MP3 {@link AudioFormat}.
	 */
	public static final AudioFormat MP3_AUDIO_FORMAT = new AudioFormat(22050, 16, 2, true, true);
	/**
	 * The speech {@link AudioFormat}.
	 */
	public static final AudioFormat SPEECH_AUDIO_FORMAT = new AudioFormat(32000, 16, 2, true, true);
	/**
	 * The CD {@link AudioFormat}.
	 */
	public static final AudioFormat CD_AUDIO_FORMAT = new AudioFormat(44100, 16, 2, true, true);
	/**
	 * The DVD {@link AudioFormat}.
	 */
	public static final AudioFormat DVD_AUDIO_FORMAT = new AudioFormat(48000, 16, 2, true, true);

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link AudioFormat}.
	 */
	public static volatile AudioFormat AUDIO_FORMAT = SPEECH_AUDIO_FORMAT;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Audio}.
	 */
	protected Audio() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link AudioSourceInterface}.
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link AudioSourceInterface}
	 */
	public static ExtendedLinkedList<AudioSourceInterface> getSourceInterfaces() {
		return getSourceInterfaces(EMPTY);
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link AudioSourceInterface} with the specified
	 * mixer name.
	 * <p>
	 * @param mixerName a {@link String}
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link AudioSourceInterface} with the specified
	 *         mixer name
	 */
	public static ExtendedLinkedList<AudioSourceInterface> getSourceInterfaces(
			final String mixerName) {
		final ExtendedLinkedList<AudioSourceInterface> interfaces = new ExtendedLinkedList<AudioSourceInterface>();
		final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (final Mixer.Info mixerInfo : mixerInfos) {
			if (Strings.contains(mixerInfo.getName(), mixerName)) {
				final Mixer mixer = AudioSystem.getMixer(mixerInfo);
				final Line.Info[] sourceInfos = mixer.getSourceLineInfo();
				for (final Line.Info sourceInfo : sourceInfos) {
					try {
						final Line line = mixer.getLine(sourceInfo);
						if (line instanceof SourceDataLine) {
							interfaces.add(new AudioSourceInterface(mixerInfo.getName(), mixer,
									Objects.toString(sourceInfo), (SourceDataLine) line));
						}
					} catch (final LineUnavailableException ignored) {
					}
				}
			}
		}
		return interfaces;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link AudioTargetInterface}.
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link AudioTargetInterface}
	 */
	public static ExtendedLinkedList<AudioTargetInterface> getTargetInterfaces() {
		return getTargetInterfaces(EMPTY);
	}

	/**
	 * Returns the {@link ExtendedLinkedList} of {@link AudioTargetInterface} with the specified
	 * mixer name.
	 * <p>
	 * @param mixerName a {@link String}
	 * <p>
	 * @return the {@link ExtendedLinkedList} of {@link AudioTargetInterface} with the specified
	 *         mixer name
	 */
	public static ExtendedLinkedList<AudioTargetInterface> getTargetInterfaces(
			final String mixerName) {
		final ExtendedLinkedList<AudioTargetInterface> interfaces = new ExtendedLinkedList<AudioTargetInterface>();
		final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (final Mixer.Info mixerInfo : mixerInfos) {
			if (Strings.contains(mixerInfo.getName(), mixerName)) {
				final Mixer mixer = AudioSystem.getMixer(mixerInfo);
				final Line.Info[] targetInfos = mixer.getTargetLineInfo();
				for (final Line.Info targetInfo : targetInfos) {
					try {
						final Line line = mixer.getLine(targetInfo);
						if (line instanceof TargetDataLine) {
							interfaces.add(new AudioTargetInterface(mixerInfo.getName(), mixer,
									Objects.toString(targetInfo), (TargetDataLine) line));
						}
					} catch (final LineUnavailableException ignored) {
					}
				}
			}
		}
		return interfaces;
	}
}
