#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain financial functions for time series
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2022 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from statistics import mode

from statsmodels.tsa.api import ExponentialSmoothing
from statsmodels.tsa.seasonal import STL

from ngui.chart import *
from nutil.math import *

####################################################################################################
# TIME SERIES ENUMS
####################################################################################################

__TIME_SERIES_ENUMS_______________________________ = ''


class Transformation(Enum):
	LOG = 'log'
	DIFF = 'diff'
	RETURNS = 'returns'
	LOG_RETURNS = 'log returns'


####################################################################################################
# TIME SERIES FUNCTIONS
####################################################################################################

__TIME_SERIES_____________________________________ = ''


def get_freq_group(series, freq=FREQUENCY, group=GROUP):
	if is_null(freq):
		freq = find_nearest_freq(series)
	if is_null(group):
		group = find_nearest_group(series, freq=freq)
	return freq, group


def get_average_freq_days(series):
	return np.diff(series.index).mean() / np.timedelta64(1, 'D')


##################################################

def set_freq(series, freq=FREQUENCY, group=GROUP):
	freq, group = get_freq_group(series, freq=freq, group=group)
	series.index.freq = get_freq(freq=freq, group=group)


##################################################

def find_nearest_freq(series):
	return find_nearest_freq_from_days(get_average_freq_days(series))


def find_nearest_freq_from_days(n):
	return DAY_COUNT_FREQUENCY[nearest(DAY_COUNT_FREQUENCY, n)]


def find_nearest_freq_from_period(period=PERIOD):
	return find_nearest_freq_from_days(get_period_days(None, period=period))


def find_nearest_group(series, freq=FREQUENCY):
	if is_null(series) or freq is Frequency.DAYS:
		return GROUP
	if freq is Frequency.WEEKS:
		if mode(get_weekdays(series.index)) < DAYS_PER_WEEK / 2:
			return Group.FIRST
	elif freq is Frequency.MONTHS:
		if mode(get_days(series.index)) < DAYS_PER_MONTH / 2:
			return Group.FIRST
	elif freq is Frequency.QUARTERS:
		if mode(get_days(series.index, year=True)) % DAYS_PER_QUARTER < DAYS_PER_QUARTER / 2:
			return Group.FIRST
	elif freq is Frequency.SEMESTERS:
		if mode(get_days(series.index, year=True)) % DAYS_PER_SEMESTER < DAYS_PER_SEMESTER / 2:
			return Group.FIRST
	elif freq is Frequency.YEARS:
		if mode(get_days(series.index, year=True)) < DAYS_PER_YEAR / 2:
			return Group.FIRST
	return Group.LAST


def find_nearest_freq_group(series):
	freq = find_nearest_freq(series)
	group = find_nearest_group(series, freq=freq)
	return freq, group


#########################

def group_series(series, clean=False, freq=FREQUENCY, sort=True):
	if clean:
		series = remove_null(series)
	if is_empty(series):
		return series
	if freq is Frequency.WEEKS:
		index = get_year_weeks(series)
	elif freq is Frequency.MONTHS:
		index = [get_years(series), get_months(series)]
	elif freq is Frequency.QUARTERS:
		index = [get_years(series), get_quarters(series)]
	elif freq is Frequency.SEMESTERS:
		index = [get_years(series), get_semesters(series)]
	elif freq is Frequency.YEARS:
		index = get_years(series)
	else:
		index = [get_years(series), get_months(series), get_days(series)]
	return series.groupby(index, sort=sort)


def ungroup_series(series, clean=False, freq=FREQUENCY, end=True):
	if clean:
		series = remove_null(series)
	if is_empty(series):
		return series
	if freq is Frequency.WEEKS:
		series.index = [get_end_period(y, w=w) for y, w in series.index] if end \
			else [get_start_period(y, w=w) for y, w in series.index]
	elif freq is Frequency.MONTHS:
		series.index = [get_end_period(y, m=m) for y, m in series.index] if end \
			else [get_start_period(y, m=m) for y, m in series.index]
	elif freq is Frequency.QUARTERS:
		series.index = [get_end_period(y, q=q) for y, q in series.index] if end \
			else [get_start_period(y, q=q) for y, q in series.index]
	elif freq is Frequency.SEMESTERS:
		series.index = [get_end_period(y, s=s) for y, s in series.index] if end \
			else [get_start_period(y, s=s) for y, s in series.index]
	elif freq is Frequency.YEARS:
		series.index = [get_end_period(y) for y in series.index] if end \
			else [get_start_period(y) for y in series.index]
	else:
		series.index = [get_end_period(y, m=m, d=d) for y, m, d in series.index] if end \
			else [get_start_period(y, m=m, d=d) for y, m, d in series.index]
	return series


# • TIME SERIES TRANSFORMATION #####################################################################

__TIME_SERIES_TRANSFORMATION______________________ = ''


def get_diff(series, periods=1):
	if is_table(series):
		return remove_null(series.diff(periods=periods))
	return to_array(series[periods:]) - to_array(series[:-periods])


def cum_diff(series, offset):
	series = concat(offset, series)
	if is_table(series):
		return series.cumsum()
	return np.cumsum(series)


def get_returns(series, periods=1):
	if is_table(series):
		return remove_null(series.pct_change(periods=periods))
	return to_array(series[periods:]) / to_array(series[:-periods]) - 1


def cum_returns(series, offset):
	series = concat(offset, series + 1)
	if is_table(series):
		return series.cumprod()
	return np.cumprod(series)


def get_log_returns(series, periods=1):
	return log(get_returns(series, periods=periods) + 1)


def cum_log_returns(series, offset):
	return cum_returns(exp(series) - 1, offset)


#########################

def get_moving_average(series, window):
	return remove_null(series.rolling(window).mean())


#########################

def get_period_over_period(series, freq=FREQUENCY):
	if is_null(freq):
		return series
	s = series.copy()
	if freq is Frequency.DAYS:
		s = shift_dates(s, days=1)
	elif freq is Frequency.WEEKS:
		s = shift_dates(s, days=7)
	elif freq is Frequency.MONTHS:
		s = shift_dates(s, months=1)
	elif freq is Frequency.QUARTERS:
		s = shift_dates(s, months=3)
	elif freq is Frequency.SEMESTERS:
		s = shift_dates(s, months=6)
	elif freq is Frequency.YEARS:
		s = shift_dates(s, years=1)
	return remove_null(subtract(series, s))


##################################################

def clean_series(series, group=GROUP):
	return sort_index(unique(remove_null(series), group=group))


def prepare_series(series, date_from=None, date_to=None, fill=False, interpolate=True,
                   freq=FREQUENCY, group=GROUP):
	freq, group = get_freq_group(series, freq=freq, group=group)
	series = transform_series(series, freq=freq, group=group)
	if is_null(date_from):
		date_from = get_first(series.index)
	if is_null(date_to):
		date_to = get_last(series.index)
	series = fill_null_rows(series[(series.index >= date_from - FREQUENCY_DELTA[freq]) &
	                               (series.index <= date_to)],
	                        create_datetime_sequence(date_from, date_to, freq=freq, group=group))
	set_freq(series, freq=freq, group=group)
	if fill:
		series = series.fillna(method='ffill')
	elif interpolate:
		series = series.interpolate()
	return series[series.index >= date_from]


#########################

def decompose_series(series, seasonal_period=1, freq=FREQUENCY, group=GROUP):
	'''Decomposes the specified time series into trend and seasonality using the seasonal-trend
	decomposition procedure STL based on LOESS of R. B. Cleveland, W. S. Cleveland, J.E. McRae, and
	I. Terpenning (1990).'''
	freq, group = get_freq_group(series, freq=freq, group=group)
	series = prepare_series(series, freq=freq, group=group)
	seasonal_period_length = seasonal_period * get_period_length(get_date(), freq=freq)
	return STL(series, period=seasonal_period_length).fit()


#########################

def forecast_series(series, horizon=1, initialization_method='estimated', trend='add',
                    seasonal='add', seasonal_period=1, freq=FREQUENCY, group=GROUP):
	'''Forecasts the specified time series using Holt Winter's Exponential Smoothing (2014).'''
	freq, group = get_freq_group(series, freq=freq, group=group)
	series = prepare_series(series, freq=freq, group=group)
	seasonal_period_length = seasonal_period * get_period_length(get_date(), freq=freq)
	predictions = to_frame([])
	for s in to_series(series) if is_frame(series) else [series]:
		model = ExponentialSmoothing(s, initialization_method=initialization_method, trend=trend,
		                             seasonal=seasonal, seasonal_periods=seasonal_period_length).fit()
		prediction = set_names(model.forecast(steps=horizon * seasonal_period_length), s)
		predictions = concat_cols(predictions, concat_rows(s, prediction))
	return predictions


#########################

def transform_series(series, clean=True, method=None, sort=True, freq=FREQUENCY, group=GROUP,
                     transformation=None):
	if clean:
		series = remove_null(series)
	if sort:
		series = sort_index(series)
	if is_empty(series):
		return series
	freq, group = get_freq_group(series, freq=freq, group=group)
	series = group_series(series, freq=freq)
	if group is Group.COUNT:
		series = series.count()
	elif group is Group.FIRST:
		series = series.first()
	elif group is Group.LAST:
		series = series.last()
	elif group is Group.MIN:
		series = series.min()
	elif group is Group.MAX:
		series = series.max()
	elif group is Group.MEAN:
		series = series.mean()
	elif group is Group.MEDIAN:
		series = series.median()
	elif group is Group.STD:
		series = series.std()
	elif group is Group.VAR:
		series = series.var()
	elif group is Group.SUM:
		series = series.sum()
	series = ungroup_series(series, clean=clean, freq=freq, end=group != Group.FIRST)
	if not is_null(method):
		series = series.asfreq(get_freq(freq, group), method=method)
	if transformation is Transformation.LOG:
		series = log(series)
	elif transformation is Transformation.DIFF:
		series = get_diff(series)
	elif transformation is Transformation.RETURNS:
		series = get_returns(series)
	elif transformation is Transformation.LOG_RETURNS:
		series = get_log_returns(series)
	return series


def untransform_series(series, offset, clean=True, transformation=None):
	if clean:
		series = remove_null(series)
	if is_empty(series):
		return series
	if transformation is Transformation.LOG:
		return exp(series)
	elif transformation is Transformation.DIFF:
		return cum_diff(series, offset)
	elif transformation is Transformation.RETURNS:
		return cum_returns(series, offset)
	elif transformation is Transformation.LOG_RETURNS:
		return cum_log_returns(series, offset)
	return series


# • TIME SERIES FIGURE #############################################################################

__TIME_SERIES_FIGURE______________________________ = ''


def plot_decomposition(trend, seasonal, residual,
                       fig=None, title='Seasonal-Trend Decomposition', title_x='Time', title_y=None,
                       name=None, color='black', trend_color='red', seasonal_color='gray',
                       residual_color='lightgray', show_legend=False, width=2, yaxis=0):
	if is_null(fig):
		fig = create_figure(title=title, title_x=title_x, title_y=title_y)
	fig.add_trace(draw(x=trend.index, y=get_col(trend), color=trend_color, fill='none',
	                   name=paste(name, '(Trend Component)'), show_legend=show_legend,
	                   stackgroup='one', width=width, yaxis=yaxis))
	fig.add_trace(draw(x=seasonal.index, y=get_col(seasonal), color=seasonal_color, fill='tonexty',
	                   name=paste(name, '(Seasonal Component)'), show_legend=show_legend,
	                   stackgroup='one', width=width, yaxis=yaxis))
	fig.add_trace(draw(x=residual.index, y=get_col(residual), color=residual_color, fill='tonexty',
	                   name=paste(name, '(Residual Component)'), show_legend=show_legend,
	                   stackgroup='one', width=width, yaxis=yaxis))
	series = trend + seasonal + residual
	fig.add_trace(draw(x=series.index, y=get_col(series),
	                   color=color, name=name, width=width, yaxis=yaxis))
	return fig
