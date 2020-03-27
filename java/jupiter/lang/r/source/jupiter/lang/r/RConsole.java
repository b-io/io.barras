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

import jupiter.common.util.Strings;
import jupiter.gui.console.GraphicalConsole;

public class RConsole {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
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
	 * Constructs a {@link RConsole}.
	 */
	protected RConsole() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// MAIN
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Starts the {@link RConsole}.
	 * <p>
	 * @param args the array of command line arguments
	 */
	public static void main(final String[] args) {
		int status = IO.EXIT_SUCCESS;
		final GraphicalConsole console = new GraphicalConsole();
		R.start();
		try {
			R.installPackages(LIGHT_PACKAGES);
			interactions();
		} catch (final Exception ignored) {
			status = IO.EXIT_FAILURE;
		} finally {
			R.stop();
			console.exit(status);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Interacts with the user.
	 */
	protected static void interactions() {
		boolean isRunning = true;
		do {
			// Process the input expression
			final String inputExpression = IO.input().trim();
			if (Strings.toLowerCase(inputExpression).contains("exit")) {
				IO.info("Good bye!");
				isRunning = false;
			} else {
				R.execute(inputExpression);
			}
		} while (isRunning);
	}
}
