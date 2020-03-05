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
package jupiter.media.audio;

import static jupiter.common.util.Characters.COLON;
import static jupiter.common.util.Characters.SPACE;

import java.io.Serializable;

import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

import jupiter.common.model.ICloneable;
import jupiter.common.util.Strings;

public abstract class AudioInterface
		implements ICloneable<AudioInterface>, Serializable {

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
	 * The name of the {@link Mixer}.
	 */
	protected final String mixerName;
	/**
	 * The {@link Mixer}.
	 */
	protected final Mixer mixer;

	/**
	 * The name of the {@link Line}.
	 */
	protected final String lineName;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an {@link AudioInterface} with the specified name of the {@link Mixer},
	 * {@link Mixer}, name of the {@link Line} and {@link Line}.
	 * <p>
	 * @param mixerName the name of the {@link Mixer}
	 * @param mixer     the {@link Mixer}
	 * @param lineName  the name of the {@link Line}
	 */
	public AudioInterface(final String mixerName, final Mixer mixer, final String lineName) {
		this.mixerName = mixerName;
		this.mixer = mixer;
		this.lineName = lineName;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the name of the {@link Mixer}.
	 * <p>
	 * @return the name of the {@link Mixer}
	 */
	public String getMixerName() {
		return mixerName;
	}

	/**
	 * Returns the {@link Mixer}.
	 * <p>
	 * @return the {@link Mixer}
	 */
	public Mixer getMixer() {
		return mixer;
	}

	//////////////////////////////////////////////

	/**
	 * Returns the name of the {@link Line}.
	 * <p>
	 * @return the name of the {@link Line}
	 */
	public String getLineName() {
		return lineName;
	}

	/**
	 * Returns the {@link Line}.
	 * <p>
	 * @return the {@link Line}
	 */
	public abstract Line getLine();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OBJECT
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a copy of {@code this}.
	 * <p>
	 * @return a copy of {@code this}
	 *
	 * @see ICloneable
	 */
	@Override
	public AudioInterface clone() {
		try {
			return (AudioInterface) super.clone();
		} catch (final CloneNotSupportedException ex) {
			throw new IllegalStateException(Strings.toString(ex), ex);
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
		return Strings.join(mixerName, COLON, SPACE, lineName);
	}
}
