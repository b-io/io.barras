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
package jupiter.network.monitor;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Strings.EMPTY;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import jupiter.common.io.Resources;
import jupiter.common.io.file.FileHandler;
import jupiter.common.io.file.Files;
import jupiter.common.model.ICloneable;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.struct.map.hash.ExtendedHashMap;
import jupiter.common.struct.table.StringTable;
import jupiter.common.thread.LockedWorkQueue;
import jupiter.common.thread.Result;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.time.Chronometer;
import jupiter.common.time.Dates;
import jupiter.common.util.Arrays;
import jupiter.common.util.Collections;
import jupiter.common.util.Formats;
import jupiter.common.util.Objects;
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
	 * The {@link FileHandler} of the data files storing the downloading speeds of the URLs
	 * associated to their URL names.
	 */
	protected static final ExtendedHashMap<String, FileHandler> DATA_FILES = new ExtendedHashMap<String, FileHandler>(
			URL_NAMES.size());

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The flag specifying whether to parallelize using a {@link WorkQueue}.
	 */
	protected static volatile boolean PARALLELIZE = false;
	/**
	 * The {@link WorkQueue} used for checking the downloading speeds.
	 */
	protected static volatile WorkQueue<String, Result<Double>> WORK_QUEUE = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs a {@link SpeedChecker}.
	 */
	protected SpeedChecker() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts the {@link SpeedChecker}.
	 * <p>
	 * @param args the array of command line arguments
	 */
	public static void main(final String[] args) {
		start();
		for (int i = 0; i < RUNS_COUNT; ++i) {
			SpeedChecker.downloadAll();
			Threads.sleep(TIME_INTERVAL);
		}
		show();
		stop();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GETTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the name of the file pointed by the specified {@link URL}.
	 * <p>
	 * @param url an {@link URL}
	 * <p>
	 * @return the name of the file pointed by the specified {@link URL}
	 */
	public static String getURLFileName(final URL url) {
		return url.getFile()
				.replace(File.pathSeparator, EMPTY)
				.replace(Files.SEPARATOR, EMPTY);
	}

	/**
	 * Returns the path to the data file storing the downloading speeds of the specified
	 * {@link URL}.
	 * <p>
	 * @param url an {@link URL}
	 * <p>
	 * @return the path to the data file storing the downloading speeds of the specified {@link URL}
	 */
	public static String getDataFilePath(final URL url) {
		return Strings.join(Files.TEMP_DIR_PATH, Files.SEPARATOR,
				"downloading_speeds_of_", getURLFileName(url), ".csv");
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts {@code this}.
	 */
	protected static synchronized void start() {
		IO.debug(EMPTY);

		// Initialize
		// • The file handlers of the data files storing the downloading speeds of the URLs
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
		// • The work queue
		if (PARALLELIZE) {
			if (WORK_QUEUE == null) {
				WORK_QUEUE = new LockedWorkQueue<String, Result<Double>>(new Checker());
			} else {
				IO.debug("The work queue ", WORK_QUEUE, " has already started");
			}
		}
	}

	/**
	 * Stops {@code this}.
	 */
	protected static synchronized void stop() {
		IO.debug(EMPTY);

		// Shutdown
		// • The work queue
		if (WORK_QUEUE != null) {
			WORK_QUEUE.shutdown();
		}
		// • The file handlers of the data files storing the downloading speeds
		final Collection<FileHandler> dataFiles = DATA_FILES.values();
		for (final FileHandler dataFile : dataFiles) {
			dataFile.closeWriter();
		}
		DATA_FILES.clear();
	}

	/**
	 * Restarts {@code this}.
	 */
	public static synchronized void restart() {
		IO.debug(EMPTY);

		stop();
		start();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Downloads all the files pointed by the URLs.
	 */
	protected static void downloadAll() {
		if (PARALLELIZE) {
			// Distribute the tasks
			final List<Long> ids = new ExtendedList<Long>(URL_NAMES.size());
			for (final String urlName : URL_NAMES) {
				ids.add(WORK_QUEUE.submit(urlName));
			}

			// Collect the results
			int i = 0;
			for (final long id : ids) {
				final Result<Double> result = WORK_QUEUE.get(id);
				final String output = Objects.toString(result.getOutput());
				IO.info(output, " [Mbits/s]");
				DATA_FILES.get(URL_NAMES.get(i++)).writeLine(Strings.join(Dates.getDateTime(),
						Arrays.DELIMITER, output));
			}
		} else {
			for (final String urlName : URL_NAMES) {
				final Result<Double> result = download(urlName);
				final String output = Objects.toString(result.getOutput());
				IO.info(output, " [Mbits/s]");
				DATA_FILES.get(urlName).writeLine(Strings.join(Dates.getDateTime(),
						Arrays.DELIMITER, output));
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
				final File urlFile = new File(Files.TEMP_DIR_PATH.concat(Files.SEPARATOR)
						.concat(urlFileName));
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
				} catch (final IOException ex) {
					return new Result<Double>(0., IO.error(ex,
							"Cannot transfer the file ", Strings.quote(urlName),
							" to ", Strings.quote(Files.getPath(urlFile))));
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

	/**
	 * Shows the downloading speeds (in Mbits/s) in a {@link TimeSeriesGraphic}.
	 */
	public static void show() {
		show(Collections.toArray(DATA_FILES.values()));
	}

	/**
	 * Shows the downloading speeds (in Mbits/s) contained in the specified array of data
	 * {@link FileHandler} in a {@link TimeSeriesGraphic}.
	 * <p>
	 * @param dataFileHandlers the array of data {@link FileHandler} containing the downloading
	 *                         speeds (in Mbits/s) to show
	 */
	public static void show(final FileHandler... dataFileHandlers) {
		Charts.DATE_FORMAT = Formats.DATE_TIME_FORMAT;
		final TimeSeriesGraphic graph = new TimeSeriesGraphic("Network Speed", "Time",
				"Downloading Speed [Mbits/s]");
		for (int i = 0; i < dataFileHandlers.length; ++i) {
			loadSeries(graph, 0, dataFileHandlers[i].getPath());
		}
		graph.display();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// IMPORTERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static void loadSeries(final TimeSeriesGraphic graph, final int axisDatasetIndex,
			final String path) {
		try {
			final StringTable coordinates = new StringTable(new String[] {"Time",
				Files.getNameWithoutExtension(path)}, path, false);
			graph.load(axisDatasetIndex, coordinates, 0, 1, true);
		} catch (final IOException ex) {
			IO.error(ex);
		} catch (final ParseException ex) {
			IO.error(ex);
		}
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
