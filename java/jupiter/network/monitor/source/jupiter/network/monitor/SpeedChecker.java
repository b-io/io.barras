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
package jupiter.network.monitor;

import static jupiter.common.io.InputOutput.IO;
import static jupiter.common.io.file.Files.ALL_SEPARATORS;
import static jupiter.common.io.file.Files.SEPARATOR;
import static jupiter.common.io.file.Files.TEMP_DIR_PATH;
import static jupiter.common.util.Strings.EMPTY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.List;

import jupiter.common.Formats;
import jupiter.common.io.Resources;
import jupiter.common.io.file.FileHandler;
import jupiter.common.io.file.Files;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.map.hash.ExtendedHashMap;
import jupiter.common.struct.table.StringTable;
import jupiter.common.thread.Result;
import jupiter.common.thread.SynchronizedWorkQueue;
import jupiter.common.thread.Threads;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.time.Chronometer;
import jupiter.common.time.Dates;
import jupiter.common.util.Arrays;
import jupiter.common.util.Collections;
import jupiter.common.util.Strings;
import jupiter.graphics.charts.Charts;
import jupiter.graphics.charts.TimeSeriesGraphic;

public class SpeedChecker {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile int RUNS_COUNT = 2880;
	public static volatile int TIME_INTERVAL = 30000; // [ms]
	public static volatile int TIME_OUT = 15000; // [ms]

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link ExtendedList} of names of the URLs pointing to the files to download.
	 */
	protected static final ExtendedList<String> URL_NAMES = Strings.asList(
			"http://cachefly.cachefly.net/1mb.test", "http://cachefly.cachefly.net/10mb.test");

	/**
	 * The {@link FileHandler} of the data files storing the downloading speeds (in Mbits/s) of the
	 * URLs associated to their URL names.
	 */
	protected static final ExtendedHashMap<String, FileHandler> DATA_FILES = new ExtendedHashMap<String, FileHandler>(
			URL_NAMES.size());

	/**
	 * The {@link TimeSeriesGraphic} showing the downloading speeds (in Mbits/s) of the URLs.
	 */
	protected static final TimeSeriesGraphic GRAPH = new TimeSeriesGraphic("Network Speed", "Time",
			"Downloading Speed [Mbits/s]");

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The {@link WorkQueue} used for checking the downloading speeds (in Mbits/s).
	 */
	protected static volatile WorkQueue<String, Result<Double>> WORK_QUEUE = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts the {@link SpeedChecker}.
	 * <p>
	 * @param args the array of command line arguments
	 */
	public static void main(final String[] args) {
		parallelize();
		try {
			start();
			clear();
			show();
			for (int ri = 0; ri < RUNS_COUNT; ++ri) {
				SpeedChecker.downloadAll();
				Threads.sleep(TIME_INTERVAL);
			}
			stop();
		} finally {
			unparallelize();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SpeedChecker}.
	 */
	protected SpeedChecker() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// ACCESSORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the name of the file pointed by the specified {@link URL}.
	 * <p>
	 * @param url an {@link URL}
	 * <p>
	 * @return the name of the file pointed by the specified {@link URL}
	 */
	public static String getURLFileName(final URL url) {
		return Strings.removeAll(url.getFile(), ALL_SEPARATORS);
	}

	/**
	 * Returns the path to the data file storing the downloading speeds (in Mbits/s) of the
	 * specified {@link URL}.
	 * <p>
	 * @param url an {@link URL}
	 * <p>
	 * @return the path to the data file storing the downloading speeds (in Mbits/s) of the
	 *         specified {@link URL}
	 */
	public static String getDataFilePath(final URL url) {
		return Strings.join(TEMP_DIR_PATH, SEPARATOR,
				"downloading_speeds_of_", getURLFileName(url), ".csv");
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLEARERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Empties the data files.
	 */
	public static void clear() {
		final FileHandler[] dataFileHandlers = Collections.<FileHandler>toArray(
				DATA_FILES.values());
		for (final FileHandler dataFileHandler : dataFileHandlers) {
			dataFileHandler.empty();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONTROLLERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts {@code this}.
	 */
	protected static synchronized void start() {
		IO.trace(EMPTY);

		// Initialize
		for (final String urlName : URL_NAMES) {
			try {
				// Get the URL denoted by the name
				final URL url = new URL(urlName);
				// Create the handler of the data file storing the downloading speeds of the URL
				DATA_FILES.put(urlName, new FileHandler(getDataFilePath(url)));
			} catch (final MalformedURLException ex) {
				IO.error(ex, "The URL ", Strings.quote(urlName), " is malformed");
			}
		}
	}

	/**
	 * Stops {@code this}.
	 */
	protected static synchronized void stop() {
		IO.trace(EMPTY);

		// Shutdown
		final Collection<FileHandler> dataFileHandlers = DATA_FILES.values();
		for (final FileHandler dataFileHandler : dataFileHandlers) {
			dataFileHandler.close();
		}
		DATA_FILES.clear();
	}

	/**
	 * Restarts {@code this}.
	 */
	public static synchronized void restart() {
		IO.trace(EMPTY);

		stop();
		start();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parallelizes {@code this}.
	 */
	protected static synchronized void parallelize() {
		IO.trace(EMPTY);

		// Initialize
		if (WORK_QUEUE == null) {
			WORK_QUEUE = new SynchronizedWorkQueue<String, Result<Double>>(new Checker());
		} else {
			IO.trace("The work queue ", WORK_QUEUE, " has already started");
		}
	}

	/**
	 * Unparallelizes {@code this}.
	 */
	public static synchronized void unparallelize() {
		IO.trace(EMPTY);

		// Shutdown
		if (WORK_QUEUE != null) {
			WORK_QUEUE.shutdown();
			WORK_QUEUE = null;
		}
	}

	/**
	 * Reparallelizes {@code this}.
	 */
	public static synchronized void reparallelize() {
		IO.trace(EMPTY);

		unparallelize();
		parallelize();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// DOWNLOADERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Downloads all the files pointed by the URLs.
	 */
	protected static void downloadAll() {
		if (WORK_QUEUE != null) {
			// Distribute the tasks
			final List<Long> ids = new ExtendedList<Long>(URL_NAMES.size());
			for (final String urlName : URL_NAMES) {
				ids.add(WORK_QUEUE.submit(urlName));
			}

			// Collect the results
			int i = 0;
			for (final long id : ids) {
				final Result<Double> result = WORK_QUEUE.get(id);
				final double downloadingSpeed = result.getOutput();
				IO.info(downloadingSpeed, " [Mbits/s]");
				final FileHandler dataFileHandler = DATA_FILES.get(URL_NAMES.get(i));
				try {
					dataFileHandler.writeLine(Strings.join(Dates.getDateTime(), Arrays.DELIMITER,
							downloadingSpeed));
				} catch (final FileNotFoundException ex) {
					IO.error(ex, "Cannot open or create the data file ",
							Strings.quote(dataFileHandler));
				} catch (final IOException ex) {
					IO.error(ex, "Cannot write to the data file ", Strings.quote(dataFileHandler));
				}
				GRAPH.addPoint(0, i, downloadingSpeed);
				++i;
			}
		} else {
			// Collect the results
			int i = 0;
			for (final String urlName : URL_NAMES) {
				final Result<Double> result = download(urlName);
				final double downloadingSpeed = result.getOutput();
				IO.info(downloadingSpeed, " [Mbits/s]");
				final FileHandler dataFileHandler = DATA_FILES.get(urlName);
				try {
					dataFileHandler.writeLine(Strings.join(Dates.getDateTime(), Arrays.DELIMITER,
							downloadingSpeed));
				} catch (final FileNotFoundException ex) {
					IO.error(ex, "Cannot open or create the data file ",
							Strings.quote(dataFileHandler));
				} catch (final IOException ex) {
					IO.error(ex, "Cannot write to the data file ", Strings.quote(dataFileHandler));
				}
				GRAPH.addPoint(0, i, downloadingSpeed);
				++i;
			}
		}
	}

	/**
	 * Downloads the file pointed by the URL denoted by the specified name.
	 * <p>
	 * @param urlName the name of the URL pointing to the file to download
	 * <p>
	 * @return the downloading speed (in Mbits/s)
	 */
	protected static Result<Double> download(final String urlName) {
		IO.debug("Download the file pointed by the URL ", Strings.quote(urlName));

		// Initialize
		final Chronometer chrono = new Chronometer();

		// Check if the URL is well-formed
		try {
			final URL url = new URL(urlName);

			// Check if the host of the URL is reachable
			try {
				final String hostName = url.getHost();
				NetworkMonitors.ping(hostName);
				IO.debug("The host ", Strings.quote(hostName), " is reachable");

				// Download the file pointed by the URL
				final String urlFileName = getURLFileName(url);
				final File urlFile = new File(TEMP_DIR_PATH.concat(SEPARATOR).concat(urlFileName));
				IO.debug("Download the file ", Strings.quote(urlFileName));
				ReadableByteChannel inputChannel = null;
				try {
					inputChannel = Channels.newChannel(url.openStream());
					chrono.start();
					Files.copy(inputChannel, urlFile);
					chrono.stop();
					final double length = urlFile.length() / 1048576. * 8.;
					final double time = chrono.getSeconds();
					final double speed = length / time;
					return new Result<Double>(speed,
							IO.info("Downloaded ", length, " [Mbits] in ", time, " [s] => ",
									speed, " [Mbits/s]"));
				} catch (final FileNotFoundException ex) {
					IO.error(ex, "Cannot open or create the URL file ", Strings.quote(urlFile));
					return new Result<Double>(0., IO.error(ex));
				} catch (final IOException ex) {
					return new Result<Double>(0., IO.error(ex,
							"Cannot download the URL file ", Strings.quote(urlName),
							" to ", Strings.quote(urlFile)));
				} finally {
					Resources.close(inputChannel);
				}
			} catch (final IOException ex) {
				IO.error(ex, "The URL ", Strings.quote(urlName), " is not reachable");
				return new Result<Double>(0., IO.error(ex));
			}
		} catch (final MalformedURLException ex) {
			IO.error(ex, "The URL ", Strings.quote(urlName), " is malformed");
			return new Result<Double>(0., IO.error(ex));
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static void loadSeries(final TimeSeriesGraphic graph, final int axisDatasetIndex,
			final String path) {
		try {
			final StringTable coordinates = new StringTable(path, false);
			coordinates.setHeader(new String[] {"Time", Files.getNameWithoutExtension(path)});
			graph.load(axisDatasetIndex, coordinates, 0, 1);
		} catch (final IOException ex) {
			IO.error(ex);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GUI
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Shows the downloading speeds (in Mbits/s) contained in the data files in the
	 * {@link TimeSeriesGraphic}.
	 */
	public static void show() {
		final FileHandler[] dataFileHandlers = Collections.<FileHandler>toArray(
				DATA_FILES.values());
		Charts.DATE_FORMAT = Formats.DATE_TIME_FORMAT;
		for (final FileHandler dataFileHandler : dataFileHandlers) {
			loadSeries(GRAPH, 0, dataFileHandler.getPath());
		}
		GRAPH.display();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Checker
			extends Worker<String, Result<Double>> {

		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs a {@link Checker}.
		 */
		protected Checker() {
			super();
		}

		@Override
		public Result<Double> call(final String input) {
			return download(input);
		}

		/**
		 * Clones {@code this}.
		 * <p>
		 * @return a clone of {@code this}
		 *
		 * @see ICloneable
		 */
		@Override
		public Checker clone() {
			return new Checker();
		}
	}
}
