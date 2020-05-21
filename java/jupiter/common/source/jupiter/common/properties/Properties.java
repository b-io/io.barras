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
package jupiter.common.properties;

import static jupiter.common.io.InputOutput.IO;

import java.io.IOException;

import jupiter.common.model.ICloneable;
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
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default value {@link String}.
	 */
	public final String defaultValue;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Properties} by default.
	 */
	public Properties() {
		this((java.util.Properties) null);
	}

	/**
	 * Constructs a {@link Properties} with the specified default {@link Properties}.
	 * <p>
	 * @param defaultProperties the default {@link Properties} (may be {@code null})
	 */
	public Properties(final java.util.Properties defaultProperties) {
		super(defaultProperties);
		defaultValue = null;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link Properties} loaded from the file denoted by the specified name.
	 * <p>
	 * @param fileName the name of the file to load
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code fileName}
	 */
	public Properties(final String fileName)
			throws IOException {
		this(fileName, null);
	}

	/**
	 * Constructs a {@link Properties} loaded from the file denoted by the specified name with the
	 * specified default value {@link String}.
	 * <p>
	 * @param fileName     the name of the file to load
	 * @param defaultValue the default value {@link String} (may be {@code null})
	 */
	public Properties(final String fileName, final String defaultValue) {
		this.defaultValue = defaultValue;
		try {
			load(fileName);
		} catch (final IOException ex) {
			IO.warn(ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the property {@link String} (or default property {@link String} if it is not present)
	 * associated to the specified key {@link String}, the default value {@link String} otherwise.
	 * <p>
	 * @param key the key {@link String}
	 * <p>
	 * @return the property {@link String} (or default property {@link String} if it is not present)
	 *         associated to the specified key {@link String}, the default value {@link String}
	 *         otherwise
	 *
	 * @see #defaults
	 * @see #defaultValue
	 */
	@Override
	public String getProperty(final String key) {
		final String value = super.getProperty(key);
		return value != null ? value : defaultValue;
	}

	public boolean getBoolean(final String key, final boolean defaultValue) {
		final String value = super.getProperty(key);
		return value != null ? Boolean.valueOf(value) : defaultValue;
	}

	public char getChar(final String key, final char defaultValue) {
		final String value = super.getProperty(key);
		return value != null ? value.charAt(0) : defaultValue;
	}

	public byte getByte(final String key, final byte defaultValue) {
		final String value = super.getProperty(key);
		return value != null ? Byte.valueOf(value) : defaultValue;
	}

	public short getShort(final String key, final short defaultValue) {
		final String value = super.getProperty(key);
		return value != null ? Short.valueOf(value) : defaultValue;
	}

	public int getInt(final String key, final int defaultValue) {
		final String value = super.getProperty(key);
		return value != null ? Integer.valueOf(value) : defaultValue;
	}

	public long getLong(final String key, final long defaultValue) {
		final String value = super.getProperty(key);
		return value != null ? Long.valueOf(value) : defaultValue;
	}

	public float getFloat(final String key, final float defaultValue) {
		final String value = super.getProperty(key);
		return value != null ? Float.valueOf(value) : defaultValue;
	}

	public double getDouble(final String key, final double defaultValue) {
		final String value = super.getProperty(key);
		return value != null ? Double.valueOf(value) : defaultValue;
	}

	public static String get(final String fileName, final String key) {
		return new Properties(fileName, null).getProperty(key);
	}

	public static String get(final String fileName, final String key, final String defaultValue) {
		return new Properties(fileName, defaultValue).getProperty(key);
	}

	//////////////////////////////////////////////

	/**
	 * Returns the property array of {@link String} (or default property array of {@link String} if
	 * it is not present) associated to the specified key {@link String}, the property array of
	 * {@link String} of the default value {@link String} otherwise.
	 * <p>
	 * @param key the key {@link String}
	 * <p>
	 * @return the property array of {@link String} (or default property array of {@link String} if
	 *         it is not present) associated to the specified key {@link String}, the property array
	 *         of {@link String} of the default value {@link String} otherwise
	 *
	 * @see #defaults
	 * @see #defaultValue
	 */
	public String[] getPropertyArray(final String key) {
		return getPropertyArray(key, defaultValue);
	}

	/**
	 * Returns the property array of {@link String} (or default property array of {@link String} if
	 * it is not present) associated to the specified key {@link String}, the property array of
	 * {@link String} of the specified default value {@link String} otherwise.
	 * <p>
	 * @param key          the key {@link String}
	 * @param defaultValue the default value {@link String} (may be {@code null})
	 * <p>
	 * @return the property array of {@link String} (or default property array of {@link String} if
	 *         it is not present) associated to the specified key {@link String}, the property array
	 *         of {@link String} of the specified default value {@link String} otherwise
	 *
	 * @see #defaults
	 */
	public String[] getPropertyArray(final String key, final String defaultValue) {
		final String propertyList = getProperty(key, defaultValue);
		if (propertyList == null) {
			return null;
		}
		return Strings.split(propertyList).toArray();
	}

	public static String[] getArray(final String fileName, final String key) {
		return new Properties(fileName, null).getPropertyArray(key);
	}

	public static String[] getArray(final String fileName, final String key,
			final String defaultValue) {
		return new Properties(fileName, defaultValue).getPropertyArray(key, defaultValue);
	}

	//////////////////////////////////////////////

	/**
	 * Returns all the property {@link String} (or default property {@link String} if it is not
	 * present) associated to the specified key {@link String} or the default value {@link String}
	 * if it is not present in an array of {@link String}.
	 * <p>
	 * @param keys the array of key {@link String}
	 * <p>
	 * @return all the property {@link String} (or default property {@link String} if it is not
	 *         present) associated to the specified key {@link String} or the default value
	 *         {@link String} if it is not present in an array of {@link String}
	 *
	 * @see #defaults
	 * @see #defaultValue
	 */
	public String[] getAllProperties(final String... keys) {
		final String[] properties = new String[keys.length];
		for (int i = 0; i < keys.length; ++i) {
			properties[i] = getProperty(keys[i], defaultValue);
		}
		return properties;
	}

	public static String[] getAll(final String fileName, final String... keys) {
		return new Properties(fileName, null).getAllProperties(keys);
	}

	/**
	 * Returns all the property {@link String} (or default property {@link String} if it is not
	 * present) associated to the specified key {@link String} or the corresponding specified
	 * default value {@link String} if it is not present in an array of {@link String}.
	 * <p>
	 * @param keys          the array of key {@link String}
	 * @param defaultValues the array of default value {@link String}
	 * <p>
	 * @return all the property {@link String} (or default property {@link String} if it is not
	 *         present) associated to the specified key {@link String} or the corresponding
	 *         specified default value {@link String} if it is not present in an array of
	 *         {@link String}
	 *
	 * @see #defaults
	 */
	public String[] getAllProperties(final String[] keys, final String... defaultValues) {
		final String[] properties = new String[keys.length];
		for (int i = 0; i < keys.length; ++i) {
			properties[i] = getProperty(keys[i], defaultValues[i]);
		}
		return properties;
	}

	public static String[] getAll(final String fileName, final String[] keys,
			final String... defaultValues) {
		return new Properties(fileName, null).getAllProperties(keys, defaultValues);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Loads {@code this} from the file denoted by the specified name.
	 * <p>
	 * @param fileName the name of the file to load
	 * <p>
	 * @throws IOException if there is a problem with reading the file denoted by {@code fileName}
	 */
	public synchronized void load(final String fileName)
			throws IOException {
		load(Threads.getClassLoader().getResourceAsStream(fileName));
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
	public Properties clone() {
		return (Properties) super.clone();
	}
}
