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
package jupiter.graphics.charts;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.BULLET;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jupiter.common.io.ProgressBar;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.map.tree.AvlTreeMap;
import jupiter.common.struct.map.tree.ComparableAvlTreeMap;
import jupiter.common.struct.map.tree.ComparableRedBlackTreeMap;
import jupiter.common.struct.map.tree.RedBlackTreeMap;
import jupiter.common.test.Tests;
import jupiter.common.time.Chronometer;
import jupiter.common.util.Objects;

public class ScatterPlotGraphicDemo {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final boolean TEST_INSERTION = true;
	protected static final boolean TEST_SEARCH = true;
	protected static final boolean TEST_DELETION = true;

	protected static final int TEST_COUNT = 100;
	protected static final int ELEMENT_COUNT = 100000;

	protected final static ProgressBar PROGRESS_BAR = new ProgressBar();


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Demonstrates {@link ScatterPlotGraphic}.
	 * <p>
	 * @param args ignored
	 */
	public static void main(final String[] args) {
		// Initialize
		final List<Map<Long, Long>> maps = new ExtendedList<Map<Long, Long>>();
		maps.add(new AvlTreeMap<Long, Long>(Long.class));
		maps.add(new ComparableAvlTreeMap<Long, Long>());
		maps.add(new RedBlackTreeMap<Long, Long>(Long.class));
		maps.add(new ComparableRedBlackTreeMap<Long, Long>());
		maps.add(new TreeMap<Long, Long>());
		maps.add(new HashMap<Long, Long>());

		// • Insertion tests
		if (TEST_INSERTION) {
			IO.test(BULLET, " Insertion tests");
			final ScatterPlotGraphic graphic = new ScatterPlotGraphic("Insertion", "Step", "Time");

			int m = 0;
			for (final Map<Long, Long> map : maps) {
				// Initialize
				final double[] times = new double[TEST_COUNT];
				graphic.addSeries(Objects.getName(map));

				// Test insertion
				IO.test("Test insertion for ", Objects.getName(map));
				for (int i = 0; i < TEST_COUNT; ++i) {
					times[i] = testInsertion(map, ELEMENT_COUNT);
					graphic.addPoint(m, i, times[i]);
					map.clear();
					PROGRESS_BAR.print(i, TEST_COUNT - 1);
				}

				// Report the statistics
				Tests.printTimes(times);
				++m;
			}
			graphic.display();
		}

		// • Search tests
		if (TEST_SEARCH) {
			IO.test(BULLET, " Search tests");
			final ScatterPlotGraphic graphic = new ScatterPlotGraphic("Search", "Step", "Time");

			int m = 0;
			for (final Map<Long, Long> map : maps) {
				// Initialize
				final double[] times = new double[TEST_COUNT];
				graphic.addSeries(Objects.getName(map));
				testInsertion(map, ELEMENT_COUNT);

				// Test search
				IO.test("Test search for ", Objects.getName(map));
				for (int i = 0; i < TEST_COUNT; ++i) {
					times[i] = testSearch(map, ELEMENT_COUNT);
					graphic.addPoint(m, i, times[i]);
					PROGRESS_BAR.print(i, TEST_COUNT - 1);
				}

				// Report the statistics
				Tests.printTimes(times);
				++m;
			}
			graphic.display();
		}

		// • Deletion tests
		if (TEST_DELETION) {
			IO.test(BULLET, " Deletion tests");
			final ScatterPlotGraphic graphic = new ScatterPlotGraphic("Deletion", "Step", "Time");

			int m = 0;
			for (final Map<Long, Long> map : maps) {
				// Initialize
				final double[] times = new double[TEST_COUNT];
				graphic.addSeries(Objects.getName(map));
				testInsertion(map, ELEMENT_COUNT);

				// Test deletion
				IO.test(BULLET, " Test deletion for ", Objects.getName(map));
				for (int i = 0; i < TEST_COUNT; ++i) {
					times[i] = testDeletion(map, ELEMENT_COUNT);
					graphic.addPoint(m, i, times[i]);
					PROGRESS_BAR.print(i, TEST_COUNT - 1);
				}

				// Report the statistics
				Tests.printTimes(times);
				++m;
			}
			graphic.display();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static double testInsertion(final Map<Long, Long> map, final long elementCount) {
		final Chronometer chrono = new Chronometer();
		chrono.start();
		for (long i = 0; i < elementCount; ++i) {
			map.put(i, i);
		}
		chrono.stop();
		return chrono.getMilliseconds();
	}

	protected static double testSearch(final Map<Long, Long> map, final long elementCount) {
		final Chronometer chrono = new Chronometer();
		chrono.start();
		for (long i = 0; i < elementCount; ++i) {
			final Long value = map.get(i);
			assert value == i;
		}
		chrono.stop();
		return chrono.getMilliseconds();
	}

	protected static double testDeletion(final Map<Long, Long> map, final long elementCount) {
		final Chronometer chrono = new Chronometer();
		chrono.start();
		for (long i = 0; i < elementCount; ++i) {
			final Long value = map.remove(i);
			assert value == i && map.get(i) == null;
		}
		chrono.stop();
		return chrono.getMilliseconds();
	}
}
