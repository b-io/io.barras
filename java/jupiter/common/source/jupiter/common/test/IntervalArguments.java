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
package jupiter.common.test;

import jupiter.common.math.Domain;
import jupiter.common.math.DoubleInterval;
import jupiter.common.math.GenericIntervalList;
import jupiter.common.math.Interval;
import jupiter.common.math.IntervalList;
import jupiter.common.util.Strings;

public class IntervalArguments
		extends SetArguments {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// ATTRIBUTES
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String NAME = "interval";
	public static final String NAMES = NAME + "s";

	public static final String LIST_NAME = "interval list";
	public static final String LIST_NAMES = LIST_NAME + "s";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link IntervalArguments}.
	 */
	protected IntervalArguments() {
		super();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <I extends Interval<?>> I requireNonEmpty(final I interval) {
		if (CHECK_ARGS) {
			return requireNonEmpty(interval, NAME);
		}
		return interval;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <T extends Comparable<? super T>> T requireInside(final T object,
			final Interval<? super T> interval) {
		if (CHECK_ARGS) {
			return requireInside(object, "object", interval, NAME);
		}
		return object;
	}

	//////////////////////////////////////////////

	public static <I extends Interval<T>, T extends Comparable<? super T>> I requireInside(
			final I interval, final GenericIntervalList<I, T> intervalList) {
		if (CHECK_ARGS) {
			return requireInside(interval, NAME, intervalList, LIST_NAME);
		}
		return interval;
	}

	public static <I extends Interval<T>, T extends Comparable<? super T>> I requireInside(
			final I interval, final String intervalName,
			final GenericIntervalList<I, T> intervalList, final String intervalListName) {
		if (CHECK_ARGS && !requireNonNull(intervalList, intervalListName).isInside(
				requireNonNull(interval, intervalName))) {
			throw new IllegalArgumentException(Strings.paste(
					"The specified", Strings.quote(intervalName),
					"is not inside the", Strings.quote(intervalListName)));
		}
		return interval;
	}

	//////////////////////////////////////////////

	public static DoubleInterval requireInside(final DoubleInterval interval, final Domain domain) {
		if (CHECK_ARGS) {
			return requireInside(interval, NAME, domain, "domain");
		}
		return interval;
	}

	public static DoubleInterval requireInside(final DoubleInterval interval,
			final String intervalName, final Domain domain, final String domainName) {
		if (CHECK_ARGS && !requireNonNull(domain, domainName).isInside(
				requireNonNull(interval, intervalName))) {
			throw new IllegalArgumentException(Strings.paste(
					"The specified", Strings.quote(intervalName),
					"is not inside the", Strings.quote(domainName)));
		}
		return interval;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static <I extends Interval<?>> I requireValid(final I interval) {
		if (CHECK_ARGS) {
			return requireValid(interval, NAME);
		}
		return interval;
	}

	//////////////////////////////////////////////

	public static <L extends IntervalList<?>> L requireValid(final L intervalList) {
		if (CHECK_ARGS) {
			return requireValid(intervalList, LIST_NAME);
		}
		return intervalList;
	}

	public static <L extends IntervalList<?>> L requireValid(final L intervalList,
			final String name) {
		if (CHECK_ARGS && !requireNonNull(intervalList, name).isValid()) {
			throw new IllegalArgumentException(Strings.paste("The specified", Strings.quote(name),
					"is invalid"));
		}
		return intervalList;
	}
}
