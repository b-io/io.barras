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
package jupiter.common;

import static jupiter.common.util.Strings.EMPTY;

import java.util.Locale;

import jupiter.common.util.Strings;

public class Locales {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Locales}.
	 */
	protected Locales() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the {@link Locale} with the specified name.
	 * <pre>
	 * Locales.get("")          = new Locale("", "")
	 * Locales.get("en")        = new Locale("en", "")
	 * Locales.get("en_GB")     = new Locale("en", "GB")
	 * Locales.get("en_001")    = new Locale("en", "001")
	 * Locales.get("en_GB_xxx") = new Locale("en", "GB", "xxx") (#)
	 * </pre>
	 * <dl>
	 * <dt><b>Note:</b></dt>
	 * <dd>This method validates the input strictly. The language code must be lower case. The
	 * country code must be upper case. The separator must be an underscore. The length must be
	 * correct.
	 * <p>
	 * (#) The behavior of the JDK variant constructor changed between JDK1.3 and JDK1.4. In JDK1.3,
	 * the constructor upper cases the variant, in JDK1.4, it does not. Thus, the result from
	 * {@link Locale#getVariant} may vary depending on your JDK.</dd>
	 * </dl>
	 * <p>
	 * @param name the name of the {@link Locale}
	 * <p>
	 * @return the {@link Locale} with the specified name
	 * <p>
	 * @throws IllegalArgumentException if {@code name} is an invalid format
	 */
	public static Locale get(final String name) {
		// Check the arguments
		if (name == null) {
			return null;
		}
		if (name.isEmpty()) {
			return new Locale(EMPTY, EMPTY);
		}
		if (name.contains("#")) {
			throw new IllegalArgumentException("Invalid locale format: " + name);
		}
		final int length = name.length();
		if (length < 2) {
			throw new IllegalArgumentException("Invalid locale format: " + name);
		}

		// Parse the locale
		final char ch0 = name.charAt(0);
		if (ch0 == '_') {
			if (length < 3) {
				throw new IllegalArgumentException("Invalid locale format: " + name);
			}
			final char ch1 = name.charAt(1);
			final char ch2 = name.charAt(2);
			if (!Character.isUpperCase(ch1) || !Character.isUpperCase(ch2)) {
				throw new IllegalArgumentException("Invalid locale format: " + name);
			}
			if (length == 3) {
				return new Locale(EMPTY, name.substring(1, 3));
			}
			if (length < 5) {
				throw new IllegalArgumentException("Invalid locale format: " + name);
			}
			if (name.charAt(3) != '_') {
				throw new IllegalArgumentException("Invalid locale format: " + name);
			}
			return new Locale(EMPTY, name.substring(1, 3), name.substring(4));
		}
		return parseLocale(name);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// PARSERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parses the {@link Locale} encoded in the specified name.
	 *
	 * @param name the name of the {@link Locale}
	 *
	 * @return the {@link Locale} encoded in the specified name
	 *
	 * @throws IllegalArgumentException if the given String can not be parsed.
	 */
	protected static Locale parseLocale(final String name) {
		// Check the arguments
		if (isLanguageCode(name)) {
			return new Locale(name);
		}

		// Parse the locale
		final String[] segments = name.split("_", -1);
		final String language = segments[0];
		if (segments.length == 2) {
			final String country = segments[1];
			if (isLanguageCode(language) && isCountryCode(country) ||
					isNumericAreaCode(country)) {
				return new Locale(language, country);
			}
		} else if (segments.length == 3) {
			final String country = segments[1];
			final String variant = segments[2];
			if (isLanguageCode(language) &&
					(country.isEmpty() ||
							isCountryCode(country) ||
							isNumericAreaCode(country)) &&
					!variant.isEmpty()) {
				return new Locale(language, country, variant);
			}
		}
		throw new IllegalArgumentException("Invalid locale format: " + name);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the specified {@link String} is an ISO 639 compliant language code.
	 * <p>
	 * @param language the {@link String} to test
	 * <p>
	 * @return {@code true} if the specified {@link String} is an ISO 639 compliant language code,
	 *         {@code false} otherwise
	 */
	public static boolean isLanguageCode(final String language) {
		return Strings.isLowerCase(language) && (language.length() == 2 || language.length() == 3);
	}

	/**
	 * Tests whether the specified {@link String} is an ISO 3166 alpha-2 compliant country code.
	 * <p>
	 * @param country the {@link String} to test
	 * <p>
	 * @return {@code true} if the specified {@link String} is an ISO 3166 alpha-2 compliant country
	 *         code, {@code false} otherwise
	 */
	public static boolean isCountryCode(final String country) {
		return Strings.isUpperCase(country) && country.length() == 2;
	}

	/**
	 * Tests whether the specified {@link String} is an UN M.49 numeric area code.
	 * <p>
	 * @param area the {@link String} to test
	 * <p>
	 * @return {@code true} if the specified {@link String} is an UN M.49 numeric area code,
	 *         {@code false} otherwise
	 */
	public static boolean isNumericAreaCode(final String area) {
		return Strings.isNumeric(area) && area.length() == 3;
	}
}
