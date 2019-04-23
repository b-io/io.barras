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
package jupiter.network.monitor;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Formats.DECIMAL_FORMAT;
import static jupiter.common.util.Strings.EMPTY;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jupiter.common.io.Resources;
import jupiter.common.io.file.FileHandler;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.thread.LockedWorkQueue;
import jupiter.common.thread.Report;
import jupiter.common.thread.Threads;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.time.Chronometer;
import jupiter.common.time.Dates;
import jupiter.common.util.Strings;

public class SpeedChecker {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final int RUNS_COUNT = 1000;
	protected static final int TIME_INTERVAL = 30000; // [ms]
	protected static final int TIME_OUT = 10000; // [ms]
	protected static final String TEMP_DIR = "C:/Temp";
	protected static final String DEFAULT_DELIMITER = ",";

	/**
	 * The {@link List} of URLs to download.
	 */
	protected static final List<String> URLS = Strings.toList(
			"http://cachefly.cachefly.net/1mb.test", "http://cachefly.cachefly.net/10mb.test");

	/**
	 * The file handlers of the data files storing the downloading speeds.
	 */
	protected static final Map<String, FileHandler> DATA_FILES = new HashMap<String, FileHandler>(
			URLS.size());

	/**
	 * The flag specifying whether to parallelize using a work queue.
	 */
	protected static volatile boolean PARALLELIZE = true;
	/**
	 * The work queue for checking the downloading speeds.
	 */
	protected static volatile WorkQueue<String, Report<Double>> WORK_QUEUE = null;


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts the speed checker.
	 * <p>
	 * @param args the array of command line arguments
	 */
	public static void main(final String[] args) {
		start();
		for (int i = 0; i < RUNS_COUNT; ++i) {
			check();
			Threads.sleep();
		}
		stop();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// OPERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts {@code this}.
	 */
	protected static synchronized void start() {
		IO.debug(EMPTY);

		// Initialize
		// - The file handlers of the data files storing the downloading speeds
		for (final String urlName : URLS) {
			try {
				// Get the URL
				final URL url = new URL(urlName);
				// Get the name of the file pointed by the URL
				final String fileName = url.getFile().replace(File.separator, EMPTY);
				// Create a file handler of the data file storing the downloading speeds
				DATA_FILES.put(urlName,
						new FileHandler(TEMP_DIR + "/downloading_speeds_of_" + fileName + ".csv"));
			} catch (final MalformedURLException ex) {
				IO.error("The URL ", Strings.quote(urlName), " is malformed",
						IO.appendException(ex));
			}
		}
		// - The work queue
		if (PARALLELIZE) {
			if (WORK_QUEUE == null) {
				WORK_QUEUE = new LockedWorkQueue<String, Report<Double>>(new Checker());
			} else {
				IO.warn("The work queue ", WORK_QUEUE, " has already started");
			}
		}
	}

	/**
	 * Stops {@code this}.
	 */
	protected static synchronized void stop() {
		IO.debug(EMPTY);

		// Shutdown
		// - The work queue
		if (WORK_QUEUE != null) {
			WORK_QUEUE.shutdown();
		}
		// - The file handlers of the data files storing the downloading speeds
		for (final FileHandler fileHandler : DATA_FILES.values()) {
			fileHandler.closeWriter();
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

	protected static void check() {
		// Download the files pointed by the URLs
		if (PARALLELIZE) {
			// Distribute the tasks
			final List<Long> ids = new ExtendedList<Long>(URLS.size());
			for (final String urlName : URLS) {
				ids.add(WORK_QUEUE.submit(urlName));
			}

			// Collect the results
			int i = 0;
			for (final long id : ids) {
				final Report<Double> report = WORK_QUEUE.get(id);
				final String result = DECIMAL_FORMAT.format(report.getOutput());
				IO.info(result, " [Mbits/s]");
				DATA_FILES.get(URLS.get(i)).writeLine(Dates.getTime() + DEFAULT_DELIMITER + result);
				++i;
			}
		} else {
			for (final String urlName : URLS) {
				final Report<Double> report = checkURL(urlName);
				final String result = DECIMAL_FORMAT.format(report.getOutput());
				IO.info(result, " [Mbits/s]");
				DATA_FILES.get(urlName).writeLine(Dates.getTime() + DEFAULT_DELIMITER + result);
			}
		}
	}

	protected static Report<Double> checkURL(final String urlName) {
		IO.debug("Check URL ", Strings.quote(urlName));

		// Initialize
		final Chronometer chrono = new Chronometer();

		// Check if the URL is well-formed
		try {
			final URL url = new URL(urlName);

			// Check if the host of the URL is reachable
			try {
				final String hostName = url.getHost();
				ping(hostName);
				IO.debug("The host ", Strings.quote(hostName), " is reachable");

				// Download the file pointed by the URL
				final String fileName = url.getFile().replace(File.separator, EMPTY);
				final File targetFilePath = new File(TEMP_DIR + File.separator + fileName);
				IO.debug("Download the file ", Strings.quote(fileName));
				ReadableByteChannel channel = null;
				FileOutputStream tempFile = null;
				try {
					channel = Channels.newChannel(url.openStream());
					tempFile = new FileOutputStream(targetFilePath);
					chrono.start();
					tempFile.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
					chrono.stop();
					final double length = targetFilePath.length() / 1048576. * 8.;
					final double time = chrono.getSeconds();
					final double speed = length / time;
					return new Report<Double>(speed,
							IO.info("Downloaded ", DECIMAL_FORMAT.format(length), " [Mbits] in ",
									DECIMAL_FORMAT.format(time), " [s] => ",
									DECIMAL_FORMAT.format(speed), " [Mbits/s]"));
				} catch (final IOException ex) {
					return new Report<Double>(0.,
							IO.error("Unable to transfer the file ", Strings.quote(urlName), " to ",
									Strings.quote(targetFilePath.getCanonicalPath()),
									IO.appendException(ex)));
				} finally {
					Resources.close(channel);
					Resources.close(tempFile);
				}
			} catch (final IOException ex) {
				IO.error("The URL ", Strings.quote(urlName), " is not reachable",
						IO.appendException(ex));
				return new Report<Double>(0., IO.error(ex));
			}
		} catch (final MalformedURLException ex) {
			IO.error("The URL ", Strings.quote(urlName), " is malformed", IO.appendException(ex));
			return new Report<Double>(0., IO.error(ex));
		}
	}

	protected static boolean ping(final String hostName)
			throws IOException {
		final InetAddress host = InetAddress.getByName(hostName);
		return host.isReachable(TIME_OUT);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class Checker
			extends Worker<String, Report<Double>> {

		protected Checker() {
			super();
		}

		@Override
		public Report<Double> call(final String input) {
			return checkURL(input);
		}

		@Override
		public Checker clone() {
			return new Checker();
		}
	}
}
