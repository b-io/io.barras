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
import static jupiter.common.util.Formats.DECIMAL_FORMAT;
import static jupiter.common.util.Strings.EMPTY;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jupiter.common.io.Resources;
import jupiter.common.io.file.FileHandler;
import jupiter.common.io.file.Files;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.thread.LockedWorkQueue;
import jupiter.common.thread.Result;
import jupiter.common.thread.Threads;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.time.Chronometer;
import jupiter.common.time.Dates;
import jupiter.common.util.Arrays;
import jupiter.common.util.Strings;

public class SpeedChecker {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static volatile int RUNS_COUNT = 1000;
	protected static volatile int TIME_INTERVAL = 30000; // [ms]
	protected static volatile int TIME_OUT = 10000; // [ms]
	protected static volatile String TEMP_DIR = "C:/Temp";
	/**
	 * The delimiting {@link String}.
	 */
	protected static volatile String DELIMITER = ",";

	/**
	 * The {@link List} of URLs to download.
	 */
	protected static final List<String> URLS = Strings.asList(
			"http://cachefly.cachefly.net/1mb.test", "http://cachefly.cachefly.net/10mb.test");

	/**
	 * The file handlers of the data files storing the downloading speeds.
	 */
	protected static final Map<String, FileHandler> DATA_FILES = new HashMap<String, FileHandler>(
			URLS.size());

	/**
	 * The flag specifying whether to parallelize using a {@link WorkQueue}.
	 */
	protected static volatile boolean PARALLELIZE = true;
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
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts {@code this}.
	 */
	protected static synchronized void start() {
		IO.debug(EMPTY);

		// Initialize
		// • The file handlers of the data files storing the downloading speeds
		for (final String urlName : URLS) {
			try {
				// Get the URL
				final URL url = new URL(urlName);
				// Get the name of the file pointed by the URL
				final String fileName = url.getFile().replace(File.separator, EMPTY);
				// Create the handler of the data file storing the downloading speeds
				DATA_FILES.put(urlName,
						new FileHandler(TEMP_DIR + "/downloading_speeds_of_" + fileName + ".csv"));
			} catch (final MalformedURLException ex) {
				IO.error("The URL ", Strings.quote(urlName), " is malformed: ", ex);
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
				final Result<Double> result = WORK_QUEUE.get(id);
				final String output = DECIMAL_FORMAT.format(result.getOutput());
				IO.info(output, " [Mbits/s]");
				DATA_FILES.get(URLS.get(i++))
						.writeLine(Dates.createTimestamp() + Arrays.DELIMITER + output);
			}
		} else {
			for (final String urlName : URLS) {
				final Result<Double> result = checkURL(urlName);
				final String output = DECIMAL_FORMAT.format(result.getOutput());
				IO.info(output, " [Mbits/s]");
				DATA_FILES.get(urlName)
						.writeLine(Dates.createTimestamp() + Arrays.DELIMITER + output);
			}
		}
	}

	protected static Result<Double> checkURL(final String urlName) {
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
				final File targetFile = new File(TEMP_DIR + File.separator + fileName);
				IO.debug("Download the file ", Strings.quote(fileName));
				ReadableByteChannel inputChannel = null;
				FileChannel outputChannel = null;
				try {
					inputChannel = Channels.newChannel(url.openStream());
					outputChannel = new FileOutputStream(targetFile).getChannel();
					chrono.start();
					outputChannel.transferFrom(inputChannel, 0, Long.MAX_VALUE);
					chrono.stop();
					final double length = targetFile.length() / 1048576. * 8.;
					final double time = chrono.getSeconds();
					final double speed = length / time;
					return new Result<Double>(speed,
							IO.info("Downloaded ", DECIMAL_FORMAT.format(length), " [Mbits] in ",
									DECIMAL_FORMAT.format(time), " [s] => ",
									DECIMAL_FORMAT.format(speed), " [Mbits/s]"));
				} catch (final IOException ex) {
					return new Result<Double>(0.,
							IO.error("Unable to transfer the file ", Strings.quote(urlName), " to ",
									Strings.quote(Files.getCanonicalPath(targetFile)), ": ", ex));
				} finally {
					Resources.close(inputChannel);
					Resources.close(outputChannel);
				}
			} catch (final IOException ex) {
				IO.error("The URL ", Strings.quote(urlName), " is not reachable: ", ex);
				return new Result<Double>(0., IO.error(ex));
			}
		} catch (final MalformedURLException ex) {
			IO.error("The URL ", Strings.quote(urlName), " is malformed: ", ex);
			return new Result<Double>(0., IO.error(ex));
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
			return checkURL(input);
		}

		/**
		 * Creates a copy of {@code this}.
		 * <p>
		 * @return a copy of {@code this}
		 *
		 * @see jupiter.common.model.ICloneable
		 */
		@Override
		public Checker clone() {
			return new Checker();
		}
	}
}
