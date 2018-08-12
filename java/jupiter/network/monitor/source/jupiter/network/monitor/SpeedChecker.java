/*
 * The MIT License
 *
 * Copyright © 2013-2015 Florian Barras <https://barras.io>
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jupiter.common.io.file.FileHandler;
import jupiter.common.struct.list.ExtendedList;
import jupiter.common.thread.Report;
import jupiter.common.thread.WorkQueue;
import jupiter.common.thread.Worker;
import jupiter.common.time.Chronometer;
import jupiter.common.time.Dates;
import jupiter.common.util.Strings;

public class SpeedChecker {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final List<String> URLS = new ExtendedList<String>(Arrays.<String>asList(
			"http://cachefly.cachefly.net/1mb.test", "http://cachefly.cachefly.net/10mb.test"));
	protected static final int N_RUNS = 1000;
	protected static final int TIME_INTERVAL = 30000; // [ms]
	protected static final int TIME_OUT = 10000;
	protected static final String TEMP_DIR = "C:/Temp";
	// The file handlers of the data files storing the downloading times
	protected static final Map<String, FileHandler> DATA_FILES = new HashMap<String, FileHandler>(
			URLS.size());
	// The thread pool
	protected static final WorkQueue<String, Report<Double>> THREAD_POOL = new WorkQueue<String, Report<Double>>(
			new Checker());
	protected static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts the speed checker.
	 * <p>
	 * @param args ignored
	 */
	public static void main(final String[] args) {
		init();

		for (int i = 0; i < N_RUNS; ++i) {
			check();
			try {
				Thread.sleep(TIME_INTERVAL);
			} catch (final InterruptedException ex) {
				IO.error(ex);
			}
		}

		clear();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static void init() {
		// Initialize the handlers of the files storing the download statistics
		for (final String urlName : URLS) {
			try {
				final URL url = new URL(urlName);

				// Get the name of the file pointed by the URL
				final String filename = url.getFile().replace("/", Strings.EMPTY);

				// Create a file handler of the data file storing the downloading times
				DATA_FILES.put(urlName,
						new FileHandler(TEMP_DIR + "/downloading_times_of_" + filename + ".csv"));
			} catch (final MalformedURLException ex) {
				IO.error("The URL ", Strings.quote(urlName), " is malformed",
						IO.appendException(ex));
			}
		}
	}

	protected static void check() {
		// Download the files pointed by the URLs (one by one)
		for (final String urlName : URLS) {
			final long taskId = THREAD_POOL.submit(urlName);
			final Report<Double> report = THREAD_POOL.get(taskId);
			final String result = DECIMAL_FORMAT.format(report.getOutput());
			IO.info(result);
			DATA_FILES.get(urlName).writeLine(Dates.getTime() + ": " + result);
		}
	}

	protected static void clear() {
		// Stop the thread pool
		THREAD_POOL.shutdown();

		// Close the file handlers of the data files storing the downloading times
		for (final String urlName : URLS) {
			DATA_FILES.get(urlName).closeWriter();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// WORKER
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static class Checker
			extends Worker<String, Report<Double>> {

		protected final Chronometer chrono = new Chronometer();

		public Checker() {
		}

		@Override
		public Report<Double> call(final String input) {
			IO.debug("Process URL ", Strings.quote(input));

			// Check if the URL is well-formed
			try {
				final URL url = new URL(input);

				// Check if the host of the URL is reachable
				try {
					final String hostname = url.getHost();
					ping(hostname);
					IO.debug("The host ", hostname, " is reachable");

					// Download the file pointed by the URL
					final String filename = url.getFile().replace("/", Strings.EMPTY);
					final File targetFilePath = new File("C:/Temp/" + filename);
					IO.debug("Download the file ", filename);
					ReadableByteChannel rbc = null;
					FileOutputStream tempFile = null;
					try {
						rbc = Channels.newChannel(url.openStream());
						tempFile = new FileOutputStream(targetFilePath);
						chrono.start();
						tempFile.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
						chrono.stop();
						final double length = targetFilePath.length() / 1048576. * 8.;
						final double time = chrono.getMilliseconds() / 1000.;
						final double speed = length / time;
						return new Report<Double>(speed,
								IO.info("Downloaded ", DECIMAL_FORMAT.format(length),
										" [Mbits] in ", DECIMAL_FORMAT.format(time), " [s] => ",
										DECIMAL_FORMAT.format(speed), " [Mbits/s]"));
					} catch (final IOException ex) {
						return new Report<Double>(0.,
								IO.error("Unable to transfer the file ", Strings.quote(input),
										" to ", targetFilePath.getCanonicalPath(),
										IO.appendException(ex)));
					} finally {
						if (rbc != null) {
							rbc.close();
						}
						if (tempFile != null) {
							tempFile.close();
						}
					}
				} catch (final IOException ex) {
					IO.error("The URL ", Strings.quote(input), " is not reachable",
							IO.appendException(ex));
					return new Report<Double>(0., IO.error(ex));
				}
			} catch (final MalformedURLException ex) {
				IO.error("The URL ", Strings.quote(input), " is malformed", IO.appendException(ex));
				return new Report<Double>(0., IO.error(ex));
			}
		}

		protected static boolean ping(final String hostname)
				throws IOException {
			final InetAddress host = InetAddress.getByName(hostname);
			return host.isReachable(TIME_OUT);
		}

		@Override
		public Checker clone() {
			return new Checker();
		}
	}
}
