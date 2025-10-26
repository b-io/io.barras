#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains financial functions for time series
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

from statistics import mode

from ngui.charts import *
from statsmodels.tsa.api import ExponentialSmoothing
from statsmodels.tsa.seasonal import STL

from nutil.math import *

## TIME SERIES ENUMS #####################################################################

__TIME_SERIES_ENUMS_________________________________________ = ""


class Transformation(StringEnum):
    """
    An enumeration of common series transformations.

    Members:
        • `LOG`: natural logarithm.
        • `DIFF`: first difference.
        • `RETURNS`: arithmetic returns (percentage change).
        • `LOG_RETURNS`: log returns (difference of logs).
    """

    LOG = "log"
    DIFF = "diff"
    RETURNS = "returns"
    LOG_RETURNS = "log_returns"


## TIME SERIES FUNCTIONS #################################################################

__TIME_SERIES_______________________________________________ = ""


### TIME SERIES IMPUTATION #################################

__TIME_SERIES_IMPUTATION____________________________________ = ""


def clean_series(series, pos=POSITION):
    return sort_index(unique(remove_null(series), pos=pos))


def prepare_series(
    series, date_from=None, date_to=None, fill=False, interpolate=True, freq=FREQUENCY, group=GROUP
):
    freq, group = get_freq_group(series, freq=freq, group=group)
    series = transform_series(series, freq=freq, group=group)
    if is_null(date_from):
        date_from = get_first(series.index)
    if is_null(date_to):
        date_to = get_last(series.index)
    series = fill_null_rows(
        series[
            (series.index >= date_from - FREQUENCY_TO_RELATIVE_DURATION[freq])
            & (series.index <= date_to)
        ],
        create_datetime_sequence(date_from, date_to, freq=freq, group=group),
    )
    set_freq(series, freq=freq, group=group)
    if fill:
        series = series.fillna(method="ffill")
    elif interpolate:
        series = series.interpolate()
    return series[series.index >= date_from]


### TIME SERIES FREQUENCY ADJUSTMENT #######################

__TIME_SERIES_FREQUENCY_ADJUSTMENT__________________________ = ""


def get_average_duration(series, per=DAY):
    return np.diff(series.index).mean() / per


def get_frequency_and_position(series, freq=FREQUENCY, pos=POSITION):
    if is_null(freq):
        freq = find_nearest_freq(series)
    if is_null(pos):
        pos = find_nearest_position(series, freq=freq)
    return freq, pos


############################################################


def set_freq(series, freq=FREQUENCY, pos=POSITION):
    freq, pos = get_frequency_and_position(series, freq=freq, pos=pos)
    series.index.freq = get_frequency(freq=freq, pos=pos)


############################################################


def find_nearest_freq(series):
    return find_nearest_frequency_from_days(get_average_duration(series, per=DAY))


def find_nearest_frequency_from_days(day_count):
    return DAY_COUNT_TO_FREQUENCY[nearest(FREQUENCY_TO_DAY_COUNT, day_count)]


def find_nearest_frequency_from_period(period=PERIOD):
    return find_nearest_frequency_from_days(get_period_days(None, period=period))


def find_nearest_position(series, freq=FREQUENCY):
    if is_null(series) or freq is Frequency.DAYS:
        return POSITION
    if freq is Frequency.WEEKS:
        if mode(get_weekdays(series, use_index=True)) < DAYS_PER_WEEK / 2:
            return Position.START
    elif freq is Frequency.MONTHS:
        if mode(get_days(series, use_index=True)) < DAYS_PER_MONTH / 2:
            return Position.START
    elif freq is Frequency.QUARTERS:
        if (
            mode(get_days(series, use_index=True, year=True)) % DAYS_PER_QUARTER
            < DAYS_PER_QUARTER / 2
        ):
            return Position.START
    elif freq is Frequency.SEMESTERS:
        if (
            mode(get_days(series, use_index=True, year=True)) % DAYS_PER_SEMESTER
            < DAYS_PER_SEMESTER / 2
        ):
            return Position.START
    elif freq is Frequency.YEARS:
        if mode(get_days(series, use_index=True, year=True)) < DAYS_PER_YEAR / 2:
            return Position.START
    return Position.END


def find_nearest_frequency_position(series):
    freq = find_nearest_freq(series)
    pos = find_nearest_position(series, freq=freq)
    return freq, pos


##############################


def group_series(series, clean=False, freq=FREQUENCY, sort=True):
    if clean:
        series = remove_null(series)
    if is_empty(series):
        return series
    if freq is Frequency.WEEKS:
        index = get_year_weeks(series, use_index=True)
    elif freq is Frequency.MONTHS:
        index = [get_years(series, use_index=True), get_months(series, use_index=True)]
    elif freq is Frequency.QUARTERS:
        index = [get_years(series, use_index=True), get_quarters(series, use_index=True)]
    elif freq is Frequency.SEMESTERS:
        index = [get_years(series, use_index=True), get_semesters(series, use_index=True)]
    elif freq is Frequency.YEARS:
        index = get_years(series, use_index=True)
    else:
        index = [
            get_years(series, use_index=True),
            get_months(series, use_index=True),
            get_days(series, use_index=True),
        ]
    return series.groupby(index, sort=sort)


def ungroup_series(series, clean=False, freq=FREQUENCY, end=True):
    if clean:
        series = remove_null(series)
    if is_empty(series):
        return series
    if freq is Frequency.WEEKS:
        series.index = (
            [get_end_period(y, w=w) for y, w in series.index]
            if end
            else [get_start_period(y, w=w) for y, w in series.index]
        )
    elif freq is Frequency.MONTHS:
        series.index = (
            [get_end_period(y, m=m) for y, m in series.index]
            if end
            else [get_start_period(y, m=m) for y, m in series.index]
        )
    elif freq is Frequency.QUARTERS:
        series.index = (
            [get_end_period(y, q=q) for y, q in series.index]
            if end
            else [get_start_period(y, q=q) for y, q in series.index]
        )
    elif freq is Frequency.SEMESTERS:
        series.index = (
            [get_end_period(y, s=s) for y, s in series.index]
            if end
            else [get_start_period(y, s=s) for y, s in series.index]
        )
    elif freq is Frequency.YEARS:
        series.index = (
            [get_end_period(y) for y in series.index]
            if end
            else [get_start_period(y) for y in series.index]
        )
    else:
        series.index = (
            [get_end_period(y, m=m, d=d) for y, m, d in series.index]
            if end
            else [get_start_period(y, m=m, d=d) for y, m, d in series.index]
        )
    return series


### TIME SERIES TRANSFORMATION

__TIME_SERIES_TRANSFORMATION________________________________ = ""


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


##############################


def get_moving_average(series, window):
    return remove_null(series.rolling(window).mean())


##############################


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


##############################


def transform_series(
    series,
    clean=True,
    method=None,
    how: Optional[str] = None,
    normalize: bool = False,
    fill_value=None,
    sort=True,
    freq=FREQUENCY,
    pos=POSITION,
    transf=None,
):
    """
    Transforms a time series by grouping, aggregating, resampling, and applying optional statistical
    transformations.

    Parameters
    ----------
    series : pd.Series
        The input time series with a datetime index.
    clean : bool, default True
        Whether to remove null values from the input series before processing.
    method : {'pad', 'ffill', 'backfill', 'bfill'}, optional
        Method used for filling holes during frequency conversion:
        • 'pad' / 'ffill': propagate last valid observation forward.
        • 'backfill' / 'bfill': use the next valid observation to fill.
        Does not fill pre-existing NaNs.
    how : {'start', 'end'}, optional
        Only applicable for `PeriodIndex`. Determines whether to use the start or end of the period
        during conversion.
    normalize : bool, default False
        If True, resets timestamps to midnight during frequency conversion.
    fill_value : scalar, optional
        Value to insert in missing positions during upsampling. Has no effect on existing NaNs.
    sort : bool, default True
        Whether to sort the series by its datetime index before processing.
    freq : str or pandas.DateOffset, default FREQUENCY
        The target frequency used to group and aggregate the data.
        Example values: 'D', 'W', 'M', 'Q', 'Y'.
    transf : Transformation (Enum), optional
        Optional transformation to apply after aggregation:
        • Transformation.LOG
        • Transformation.DIFF
        • Transformation.RETURNS
        • Transformation.LOG_RETURNS

    Returns
    -------
    pd.Series
        The transformed time series after optional cleaning, grouping, reindexing, and transformation.
    """
    if clean:
        series = remove_null(series)
    if sort:
        series = sort_index(series)
    if is_empty(series):
        return series
    freq, group = get_frequency_and_position(series, freq=freq, pos=pos)
    series = group_series(series, freq=freq)
    if group is Aggregation.COUNT:
        series = series.count()
    elif group is Aggregation.FIRST:
        series = series.first()
    elif group is Aggregation.LAST:
        series = series.last()
    elif group is Aggregation.MIN:
        series = series.min()
    elif group is Aggregation.MAX:
        series = series.max()
    elif group is Aggregation.MEAN:
        series = series.mean()
    elif group is Aggregation.MEDIAN:
        series = series.median()
    elif group is Aggregation.STD:
        series = series.std()
    elif group is Aggregation.VAR:
        series = series.var()
    elif group is Aggregation.SUM:
        series = series.sum()
    series = ungroup_series(series, clean=clean, freq=freq, end=group != Aggregation.FIRST)
    if not is_null(method):
        series = series.asfreq(
            get_frequency(freq, group),
            method=method,
            how=how,
            normalize=normalize,
            fill_value=fill_value,
        )
    if transformation is Transformation.LOG:
        series = log(series)
    elif transformation is Transformation.DIFF:
        series = get_diff(series)
    elif transformation is Transformation.RETURNS:
        series = get_returns(series)
    elif transformation is Transformation.LOG_RETURNS:
        series = get_log_returns(series)
    return series


def untransform_series(series, clean=True, offset=0, transformation=None):
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


### TIME SERIES AGGREGATION ################################

__TIME_SERIES_AGGREGATION___________________________________ = ""


def aggregate_series(
    series,
    clean=True,
    sort=True,
    agg=None,
    freq=FREQUENCY,
    pos=POSITION,
):
    """
    Aggregates a time series based on the specified frequency and aggregation method.

    Parameters
    ----------
    series : pd.Series
        The input time series with a datetime index.
    clean : bool, default True
        Whether to remove null values before processing.
    sort : bool, default True
        Whether to sort the index before grouping.
    freq : str or pandas.DateOffset, default FREQUENCY
        Frequency for grouping (e.g., 'D', 'W', 'M').
    group : Group (Enum), default GROUP
        Aggregation method (e.g., Aggregation.MEAN, Aggregation.SUM, Aggregation.LAST).

    Returns
    -------
    pd.Series
        The grouped and aggregated time series with index restored.
    """
    if clean:
        series = remove_null(series)
    if sort:
        series = sort_index(series)
    if is_empty(series):
        return series

    freq, pos = get_frequency_and_position(series, freq=freq, pos=pos)

    series = group_series(series, freq=freq)

    if group is Aggregation.COUNT:
        series = series.count()
    elif group is Aggregation.FIRST:
        series = series.first()
    elif group is Aggregation.LAST:
        series = series.last()
    elif group is Aggregation.MIN:
        series = series.min()
    elif group is Aggregation.MAX:
        series = series.max()
    elif group is Aggregation.MEAN:
        series = series.mean()
    elif group is Aggregation.MEDIAN:
        series = series.median()
    elif group is Aggregation.STD:
        series = series.std()
    elif group is Aggregation.VAR:
        series = series.var()
    elif group is Aggregation.SUM:
        series = series.sum()

    series = ungroup_series(series, clean=clean, freq=freq, end=group != Aggregation.FIRST)

    return series


##############################


def aggregate_series(
    series,
    date_from=None,
    date_to=None,
    fill=False,
    interpolate=True,
    agg=None,
    freq=FREQUENCY,
    pos=POSITION,
):
    freq, group = get_frequency_and_position(series, agg=agg, freq=freq, pos=pos)
    series = transform_series(series, freq=freq)
    if is_null(date_from):
        date_from = get_first(series.index)
    if is_null(date_to):
        date_to = get_last(series.index)
    series = fill_null_rows(
        series[
            (series.index >= date_from - FREQUENCY_TO_RELATIVE_DURATION[freq])
            & (series.index <= date_to)
        ],
        create_datetime_sequence(date_from, date_to, agg=agg, freq=freq, pos=pos),
    )
    set_freq(series, agg=agg, freq=freq, pos=pos)
    if fill:
        series = series.fillna(method="ffill")
    elif interpolate:
        series = series.interpolate()
    return series[series.index >= date_from]


### TIME SERIES DECOMPOSITION ##############################

__TIME_SERIES_DECOMPOSITION_________________________________ = ""


def decompose_series(series, seasonal_period=1, agg=AGGREGATION, freq=FREQUENCY, pos=POSITION):
    """Decomposes the specified time series into trend and seasonality using the seasonal-trend
    decomposition procedure STL based on LOESS of R. B. Cleveland, W. S. Cleveland, J.E. McRae, and
    I. Terpenning (1990)."""
    freq, group = get_frequency_and_position(series, agg=agg, freq=freq, pos=pos)
    series = prepare_series(series, agg=agg, freq=freq, pos=pos)
    seasonal_period_length = seasonal_period * get_period_length(get_date(), freq=freq)
    return STL(series, period=seasonal_period_length).fit()


### TIME SERIES FORECASTING ################################

__TIME_SERIES_FORECASTING___________________________________ = ""


def forecast_series(
    series,
    horizon=1,
    initialization_method="estimated",
    trend="add",
    seasonal="add",
    seasonal_period=1,
    freq=FREQUENCY,
    group=GROUP,
):
    """Forecasts the specified time series using Holt Winter's Exponential Smoothing (2014)."""
    freq, group = get_frequency_and_position(series, agg=agg, freq=freq, pos=pos)
    series = prepare_series(series, agg=agg, freq=freq, pos=pos)
    seasonal_period_length = seasonal_period * get_period_length(get_date(), freq=freq)
    predictions = to_frame([])
    for s in to_series(series) if is_frame(series) else [series]:
        model = ExponentialSmoothing(
            s,
            initialization_method=initialization_method,
            trend=trend,
            seasonal=seasonal,
            seasonal_periods=seasonal_period_length,
        ).fit()
        prediction = set_names(model.forecast(steps=horizon * seasonal_period_length), s)
        predictions = concat_cols(predictions, concat_rows(s, prediction))
    return predictions


### TIME SERIES FIGURE #####################################

__TIME_SERIES_FIGURE________________________________________ = ""


def plot_decomposition(
    trend,
    seasonal,
    residual,
    # Figure
    fig=None,
    title="Seasonal-Trend Decomposition",
    title_x="Time",
    title_y=None,
    width=DEFAULT_WIDTH,
    height=DEFAULT_HEIGHT,
    margin=None,
    # Chart
    color="black",
    trend_color="red",
    seasonal_color="gray",
    residual_color="lightgray",
    line_width=DEFAULT_LINE_WIDTH,
    marker_size=DEFAULT_MARKER_SIZE,
    name=None,
    stackgroup=None,
    yaxis=0,
    # Flags
    show_legend=False,
):
    if is_null(fig):
        fig = create_figure(
            title=title, title_x=title_x, title_y=title_y, width=width, height=height, margin=margin
        )
    if is_null(stackgroup):
        stackgroup = generate_string(10)
    fig.add_trace(
        draw(
            x=trend.index,
            y=get_col(trend),
            # Chart
            color=trend_color,
            fill="none",
            line_width=line_width,
            marker_size=marker_size,
            name=paste(name, "(Trend Component)"),
            stackgroup=stackgroup,
            yaxis=yaxis,
            # Flags
            show_legend=show_legend,
        )
    )
    fig.add_trace(
        draw(
            x=seasonal.index,
            y=get_col(seasonal),
            # Chart
            color=seasonal_color,
            fill="tonexty",
            line_width=line_width,
            marker_size=marker_size,
            name=paste(name, "(Seasonal Component)"),
            stackgroup=stackgroup,
            yaxis=yaxis,
            # Flags
            show_legend=show_legend,
        )
    )
    fig.add_trace(
        draw(
            x=residual.index,
            y=get_col(residual),
            # Chart
            color=residual_color,
            fill="tonexty",
            line_width=line_width,
            marker_size=marker_size,
            name=paste(name, "(Residual Component)"),
            stackgroup=stackgroup,
            yaxis=yaxis,
            # Flags
            show_legend=show_legend,
        )
    )
    series = trend + seasonal + residual
    fig.add_trace(
        draw(
            x=series.index,
            y=get_col(series),
            # Chart
            color=color,
            line_width=line_width,
            marker_size=marker_size,
            name=name,
            yaxis=yaxis,
        )
    )
    return fig
