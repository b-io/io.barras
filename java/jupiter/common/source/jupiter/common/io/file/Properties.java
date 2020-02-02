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

import java.io.IOException;

import jupiter.common.thread.Threads;
import jupiter.common.util.Strings;

public class Properties
		extends java.util.Properties {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The generated serial version ID.
	 */
	private static final long serialVersionUID = 1L;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Properties}.
	 */
	public Properties() {
		super();
	}

	/**
	 * Constructs a {@link Properties} with the specified default {@link Properties}.
	 * <p>
	 * @param defaultProperties the default {@link Properties} (may be {@code null})
	 */
	public Properties(final java.util.Properties defaultProperties) {
		super(defaultProperties);
	}

	/**
	 * Constructs a {@link Properties} loaded from the specified file.
	 * <p>
	 * @param fileName the name of the file to load
	 * <p>
	 * @throws IOException if there is a problem with reading {@code fileName}
	 */
	public Properties(final String fileName)
			throws IOException {
		load(fileName);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public String[] getPropertyList(final String key) {
		final String propertyList = getProperty(key);
		if (propertyList == null) {
			return null;
		}
		return (String[]) Strings.split(propertyList).toArray();
	}

	public String[] getPropertyList(final String key, final String defaultValue) {
		final String propertyList = getProperty(key, defaultValue);
		if (propertyList == null) {
			return null;
		}
		return (String[]) Strings.split(propertyList).toArray();
	}

	//////////////////////////////////////////////

	public String[] getProperties(final String... keys) {
		final String[] properties = new String[keys.length];
		for (int i = 0; i < keys.length; ++i) {
			properties[i] = getProperty(keys[i]);
		}
		return properties;
	}

	public String[] getProperties(final String[] keys, final String[] defaultValues) {
		final String[] properties = new String[keys.length];
		for (int i = 0; i < keys.length; ++i) {
			properties[i] = getProperty(keys[i], defaultValues[i]);
		}
		return properties;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads {@code this} from the specified file.
	 * <p>
	 * @param fileName the name of the file to load
	 * <p>
	 * @throws IOException if there is a problem with reading {@code fileName}
	 */
	public synchronized void load(final String fileName)
			throws IOException {
		load(Threads.getClassLoader().getResourceAsStream(fileName));
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
	public Properties clone() {
		return (Properties) super.clone();
	}
}
