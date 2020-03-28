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
package jupiter.lang.r;

import static jupiter.common.io.IO.IO;
import static jupiter.common.util.Characters.SPACE;

import java.io.IOException;

import jupiter.common.io.IO.SeverityLevel;
import jupiter.common.io.IOHandler;
import jupiter.common.io.Systems;
import jupiter.common.math.Comparables;
import jupiter.common.model.ICloneable;
import jupiter.common.test.ArrayArguments;
import jupiter.common.thread.Threads;
import jupiter.common.thread.WorkQueue;
import jupiter.common.util.Arrays;
import jupiter.common.util.Objects;
import jupiter.common.util.Strings;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class R {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String R_PATH = "R";
	public static final String R_SCRIPT_PATH = "RScript";
	public static final String[] ARGS = new String[] {};

	//////////////////////////////////////////////

	public static volatile String REPO = "https://cloud.r-project.org";

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The default {@link RPrinter}.
	 */
	public static final RPrinter DEFAULT_PRINTER = new RPrinter();

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile String[] LIGHT_PACKAGES = new String[] {
		// • APIs
		// - System
		"processx", // execute and control system processes

		// • Data Science
		"DescTools", // provide a collection of miscellaneous basic statistic functions and convenience wrappers for efficiently describing data

		// • DB
		"dbplyr",
		"RODBC",

		// • Files
		"filesstrings", // manipulate files and strings
		"tbl2xts",
		"xlsx",

		// • Quant
		"fDMA", // estimate dynamic model averaging, dynamic model selection and median probability model
		"PerformanceAnalytics", // provide econometric functions for performance and risk analysis
		"quantmod", // specify, build, trade and analyse quantitative financial trading strategies
		"tidyquant", // provide a convenient wrapper to various 'xts', 'zoo', 'quantmod', 'TTR' and 'PerformanceAnalytics' package functions and returns the objects in the tidy 'tibble' format
		"TTR", // construct technical trading rules

		// • Test
		"testit",

		// • Time Series
		"imputeTS", // imputate (replace) missing values in univariate time series
		"timeDate",
		"TSA", // provide functions and datasets from the book 'Time Series Analysis with Applications in R' by Jonathan Cryer and Kung-Sik Chan
		"TSclust", // provide a set of measures of dissimilarity between time series to perform time series clustering
		"TSdist", // provide distance measures and some additional functions which can be used to measure the dissimilarity between time series
		"xts",
		"zoo",

		// • UI
		// - Charts
		"ggplot2",
		"ggthemes",
		// - Layout
		"gridExtra",

		// • Util
		"foreach",
		"Jmisc",
		"jsonlite",
		"lubridate", // parse date-time data, extract/update components of a date-time (years, months, days, hours, minutes and seconds) and manipulate algebraically date-time and time-span objects
		"magrittr", // provide a mechanism to chain commands with a new forward-pipe operator: '%>%'
		"parallel",
		"random",
		"reshape2",
		"scales", // map data to aesthetics and provide methods for automatically determining breaks and labels for axes and legends
		"stringi",
		"stringr",
		"zeallot", // provide an operator to perform multiple, unpacking and destructuring assignment: '%<-%'
		// - Tidyverse
		"plyr",
		"dplyr",
		"purrr",
		"readr",
		"tidyr",

		// • Web
		"httr"
	};

	public static volatile String[] QUANT_PACKAGES = new String[] {
		// • APIs
		// - System
		"processx", // execute and control system processes
		"rJava", // access the Java VM to create objects, call methods and access fields
		"rstudioapi", // access safely the RStudio API (if available) to retrieve the configuration
		// - Web
		"fredr", // access the 'FRED' API to retrieve economic time series and other data
		"Rblpapi", // access the Bloomberg API to retrieve economic time series and other data
		"rgdal", // access the 'Geospatial' Data Abstraction Library

		// • Data Science
		"BHH2", // provide functions and data sets reproducing some examples in 'Box, Hunter and Hunter II' (use for statistical design of experiments)
		"bsts", // regress time series using dynamic linear models fit using MCMC (Bayesian Structural Time Series)
		"depmixS4", // fit latent (hidden) Markov models on mixed categorical and continuous (time series) data, otherwise known as dependent mixture models
		"DescTools", // provide a collection of miscellaneous basic statistic functions and convenience wrappers for efficiently describing data
		"forecast", // display and analyse univariate time series forecasts including exponential smoothing via state space models and automatic ARIMA modelling
		"gamlss", // fit the Generalized Additive Models for Location Scale and Shape
		"lars",
		"ldhmm", // provide Hidden Markov Model (HMM) based on symmetric lambda distribution framework for the study of return time-series in the financial market
		"MASS", // provide functions and datasets from the book 'Modern Applied Statistics with S' by Venables and Ripley
		"mgcv", // regress time series using generalized additive (mixed) models (GAMs)
		"nFactors", // provide indices, heuristics and strategies to help determine the number of factors/components to retain
		"openintro", // provide functions and datasets from the 'OpenIntro' textbooks
		"pls", // provide multivariate regression methods: 'Partial Least Squares Regression' (PLSR), 'Principal Component Regression' (PCR) and 'Canonical Powered Partial Least Squares' (CPPLS)

		// • DB
		"dbplyr",
		"RODBC",

		// • Files
		"filesstrings", // manipulate files and strings
		"tbl2xts",
		"xlsx",

		// • Media
		"png",
		"raster",

		// • Quant
		"fDMA", // estimate dynamic model averaging, dynamic model selection and median probability model
		"PerformanceAnalytics", // provide econometric functions for performance and risk analysis
		"quantmod", // specify, build, trade and analyse quantitative financial trading strategies
		"tidyquant", // provide a convenient wrapper to various 'xts', 'zoo', 'quantmod', 'TTR' and 'PerformanceAnalytics' package functions and returns the objects in the tidy 'tibble' format
		"TTR", // construct technical trading rules

		// • Test
		"testit",

		// • Time Series
		"imputeTS", // imputate (replace) missing values in univariate time series
		"timeDate",
		"TSA", // provide functions and datasets from the book 'Time Series Analysis with Applications in R' by Jonathan Cryer and Kung-Sik Chan
		"TSclust", // provide a set of measures of dissimilarity between time series to perform time series clustering
		"TSdist", // provide distance measures and some additional functions which can be used to measure the dissimilarity between time series
		"xts",
		"zoo",

		// • UI
		// - Charts
		"d3heatmap", // create 'D3' JavaScript interactive heatmaps
		"dendextend", // create dendrogram graphs
		"ggplot2",
		"ggthemes",
		"highcharter",
		"plotly",
		// - Layout
		"gridExtra",
		"RColorBrewer",
		// - Shiny
		"shiny",
		"shinyalert",
		"shinycssloaders",
		"shinydashboard",
		"shinyHeatmaply",
		"shinyjs",
		"shinyTree", // provide 'jsTree' JavaScript interactive trees
		// - Tables
		"DT", // provide 'DataTables' JavaScript tables
		// - Trees
		"data.tree", // provide tree structures from hierarchical data and traverse the tree in various orders
		"igraph", // provide routines for simple graphs and network analysis
		"jsTree", // provide 'jsTree' JavaScript interactive trees
		"networkD3", // provide 'D3' JavaScript interactive network, tree, dendrogram and Sankey graphs

		// • Util
		"foreach",
		"Jmisc",
		"jsonlite",
		"lubridate", // parse date-time data, extract/update components of a date-time (years, months, days, hours, minutes and seconds) and manipulate algebraically date-time and time-span objects
		"magrittr", // provide a mechanism to chain commands with a new forward-pipe operator: '%>%'
		"parallel",
		"random",
		"reshape2",
		"scales", // map data to aesthetics and provide methods for automatically determining breaks and labels for axes and legends
		"stringi",
		"stringr",
		"zeallot", // provide an operator to perform multiple, unpacking and destructuring assignment: '%<-%'
		// - Tidyverse
		"plyr",
		"dplyr",
		"purrr",
		"readr",
		"tidyr",

		// • Web
		"httr"
	};


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link R}.
	 */
	protected R() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// FUNCTIONS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static int installPackage(final String name) {
		return execute(Strings.join(
				"if (!", Strings.singleQuote(name), " %in% installed.packages())",
				"install.packages(", Strings.singleQuote(name),
				", repos=", Strings.singleQuote(REPO),
				")"));
	}

	public static int[] installPackages(final String[] names) {
		final int[] status = new int[names.length];
		for (int i = 0; i < names.length; ++i) {
			status[i] = installPackage(names[i]);
		}
		return status;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean start() {
		return start(Comparables.isGreaterOrEqualTo(SeverityLevel.DEBUG, IO.getSeverityLevel()));
	}

	public static boolean start(final boolean debug) {
		// Test whether the server is already running
		if (R.isRunning()) {
			return true;
		}

		// Launch Rserve
		if (execute(Strings.join(
				"library(Rserve);",
				"Rserve(", debug ? "T" : "F",
				", args = ", Strings.singleQuote(Strings.joinWith(ARGS, SPACE)),
				")")) != IO.EXIT_SUCCESS) {
			return false;
		}

		// Try up to 5 times before giving up; we can be conservative here, because at this point
		// the process execution itself was successful and the start up is usually asynchronous
		int attemptCount = 5;
		do {
			// Sleep in case the start up is delayed or asynchronous
			Threads.sleep();
			--attemptCount;
		} while (attemptCount > 0 && !isRunning());
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean stop() {
		try {
			new RConnection().shutdown();
		} catch (final RserveException ex) {
			IO.warn(ex, "Fail to stop Rserve");
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Executes the specified script on the R engine.
	 * <p>
	 * @param script the script to execute
	 * <p>
	 * @return the exit value of the specified script executed on the R engine
	 */
	public static int executeScript(final String... script) {
		return executeScript(DEFAULT_PRINTER, script);
	}

	/**
	 * Executes the specified script on the R engine, prints the output with the specified printer
	 * {@link IOHandler}.
	 * <p>
	 * @param printer the printer {@link IOHandler}
	 * @param script  the script to execute
	 * <p>
	 * @return the exit value of the specified script executed on the R engine
	 */
	public static int executeScript(final IOHandler printer, final String... script) {
		try {
			return Systems.execute(printer,
					Arrays.<String>merge(new String[] {R_SCRIPT_PATH}, script, ARGS));
		} catch (final InterruptedException ex) {
			IO.error(ex);
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return IO.EXIT_FAILURE;
	}

	//////////////////////////////////////////////

	/**
	 * Executes the specified command on the R engine.
	 * <p>
	 * @param command the command to execute
	 * <p>
	 * @return the exit value of the specified command executed on the R engine
	 */
	public static int execute(final String... command) {
		return execute(DEFAULT_PRINTER, command);
	}

	/**
	 * Executes the specified command on the R engine, prints the output with the specified printer
	 * {@link IOHandler}.
	 * <p>
	 * @param printer the printer {@link IOHandler}
	 * @param command the command to execute
	 * <p>
	 * @return the exit value of the specified command executed on the R engine
	 */
	public static int execute(final IOHandler printer, final String... command) {
		// Check the system
		Systems.requireOS();
		// Check the arguments
		ArrayArguments.requireMinLength(command, 1);

		// Execute the command and print the output with the printer
		try {
			return Systems.execute(printer,
					Arrays.<String>merge(new String[] {R_PATH, "--slave", "-e"}, command));
		} catch (final InterruptedException ex) {
			IO.error(ex);
		} catch (final IOException ex) {
			IO.error(ex);
		}
		return IO.EXIT_FAILURE;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// VERIFIERS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tests whether the local Rserve instance is running on the default port.
	 * <p>
	 * @return {@code true} if the local Rserve instance is running on the default port,
	 *         {@code false} otherwise
	 */
	public static boolean isRunning() {
		try {
			final RConnection connection = new RConnection();
			IO.info("Rserve is running");
			connection.close();
			return true;
		} catch (final RserveException ex) {
			IO.warn(ex, "Rserve is not running");
		}
		return false;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CLASSES
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected static class RPrinter
			extends IOHandler {

		/**
		 * The generated serial version ID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The message prefix {@link String}.
		 */
		protected static final String PREFIX = "R> ";

		/**
		 * The printer {@link IOHandler}.
		 */
		protected final IOHandler printer;

		/**
		 * The {@link WorkQueue} used for monitoring.
		 */
		protected WorkQueue<?, ?> workQueueToMonitor;

		/**
		 * Constructs a {@link RPrinter}.
		 */
		protected RPrinter() {
			this(IO.getPrinter());
		}

		/**
		 * Constructs a {@link RPrinter} with the specified printer {@link IOHandler}.
		 * <p>
		 * @param printer the printer {@link IOHandler}
		 */
		protected RPrinter(final IOHandler printer) {
			super();
			this.printer = printer;
		}

		public void setWorkQueueToMonitor(final WorkQueue<?, ?> workQueueToMonitor) {
			this.workQueueToMonitor = workQueueToMonitor;
		}

		@Override
		public void print(final Object content, final boolean isError) {
			final String text = Objects.toString(content);
			printer.print(PREFIX.concat(text), isError);
			if (isError) {
				check(text);
			}
		}

		@Override
		public void println(final Object content, final boolean isError) {
			final String text = Objects.toString(content);
			printer.println(PREFIX.concat(text), isError);
			if (isError) {
				check(text);
			}
		}

		protected void check(final String text) {
			final String lowerCaseText = text.toLowerCase();
			if (workQueueToMonitor != null && !lowerCaseText.contains("warning") &&
					(lowerCaseText.contains("error") || lowerCaseText.contains("invalid"))) {
				IO.error(text);
				workQueueToMonitor.restart(true);
			}
		}

		@Override
		public void clear() {
			printer.clear();
		}

		/**
		 * Clones {@code this}.
		 * <p>
		 * @return a clone of {@code this}
		 *
		 * @see ICloneable
		 */
		@Override
		public RPrinter clone() {
			return new RPrinter(printer);
		}
	}
}
