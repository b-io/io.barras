#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide financial utilities for time series.
########################################################################################################################

from __future__ import annotations

from datetime import timedelta
from statistics import mode

from statsmodels.tsa.api import ExponentialSmoothing
from statsmodels.tsa.seasonal import STL

from nformat.common import *
from ngui import charts
from nutil.enums import StrEnum
from nutil.math import *
from nutil.scalar.date import *
from nutil.scalar.string import generate_string

__TIME_SERIES_ENUMS_______________________________________________________________________ = ""


class Transformation(StrEnum):
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


__TIME_SERIES_ACCESSORS___________________________________________________________________ = ""


def get_average_duration(series: pd.Series, per: Union[timedelta, np.timedelta64, pd.Timedelta] = DAY) -> float:
    """
    Computes the average spacing between consecutive index timestamps normalized by `per`.

    Args:
        series: The time-indexed `pd.Series` (index may be strings; it is coerced to datetimes).
        per: The normalization period (`np.timedelta64`, `datetime.timedelta`, or `pd.Timedelta`).

    Returns:
        The average spacing as a float multiple of `per` (0.0 if fewer than two valid timestamps).

    Raises:
        ValueError: If `per` is non-positive.
    """
    # Coerce the index to datetimes and drop invalids
    index: pd.DatetimeIndex = (
        series.index if is_time_index(series.index) else pd.to_datetime(series.index, errors="coerce")
    )
    index = index[~index.isna()]
    if len(index) < 2:
        return 0.0  # not enough points to form a difference

    # Compute the mean delta as a `pd.Timedelta`
    average_delta: pd.Timedelta = (index[1:] - index[:-1]).mean()

    # Normalize by `per`
    per_time_delta = per if isinstance(per, pd.Timedelta) else pd.to_timedelta(per)
    if per_time_delta <= pd.Timedelta(0):
        raise ValueError("'per' must be a positive duration")

    return float(average_delta / per_time_delta)


def get_frequency_and_position(series, freq=FREQUENCY, pos=POSITION):
    if is_null(freq):
        freq = find_nearest_freq(series)
    if is_null(pos) or pos is Position.AUTO:
        pos = find_nearest_position(series, freq=freq)
    return freq, pos


############################################################


def set_freq(series, freq=FREQUENCY, pos=POSITION):
    # Resolve the frequency and position from the data if needed
    freq, pos = get_frequency_and_position(series, freq=freq, pos=pos)

    series.index.freq = get_frequency(freq=freq, pos=pos)


__TIME_SERIES_CONVERTERS__________________________________________________________________ = ""


def to_time_index(series, format="%Y-%m-%d"):
    series.index = pd.to_datetime(series.index, format=format, errors="raise")


__TIME_SERIES_FIGURES_____________________________________________________________________ = ""


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
        fig = charts.create_figure(
            title=title, title_x=title_x, title_y=title_y, width=width, height=height, margin=margin
        )
    if is_null(stackgroup):
        stackgroup = generate_string(10)
    fig.add_trace(
        charts.draw(
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
        charts.draw(
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
        charts.draw(
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
        charts.draw(
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


__TIME_SERIES_PROCESSORS__________________________________________________________________ = ""


### TIME SERIES IMPUTATION #################################


def clean_series(series, pos=POSITION):
    return sort_index(unique(remove_null(series), pos=pos))


def prepare_series(series, date_from=None, date_to=None, fill=False, interpolate=True, freq=FREQUENCY, pos=POSITION):
    # Resolve the frequency and position from the data if needed
    freq, pos = get_frequency_and_position(series, freq=freq, pos=pos)

    series = transform_series(series, freq=freq, pos=pos)
    if is_null(date_from):
        date_from = get_first(series.index)
    if is_null(date_to):
        date_to = get_last(series.index)
    series = fill_null_rows(
        series[(series.index >= date_from - FREQUENCY_TO_RELATIVE_DURATION[freq]) & (series.index <= date_to)],
        create_datetime_sequence(date_from, date_to, freq=freq, pos=pos),
    )
    set_freq(series, freq=freq, pos=pos)
    if fill:
        series = series.fillna(method="ffill")
    if interpolate:
        series = series.interpolate()
    return series[series.index >= date_from]


### TIME SERIES FREQUENCY ADJUSTMENT #######################


def find_nearest_freq(series):
    return find_nearest_frequency_from_days(get_average_duration(series, per=DAY))


def find_nearest_frequency_from_days(day_count):
    return DAY_COUNT_TO_FREQUENCY[nearest(FREQUENCY_TO_DAY_COUNT, day_count)]


def find_nearest_frequency_from_period(period=PERIOD):
    return find_nearest_frequency_from_days(get_period_days(None, period=period))


def find_nearest_position(series, freq=FREQUENCY):
    """
    Finds a positional anchor (`START`|`MIDDLE`|`END`) that best matches the series timestamps for `freq`.

    Args:
        series: The time series (index is used to infer positions).
        freq: The frequency under consideration.

    Returns:
        A `Position` value. Returns the project default `POSITION` when `series` is null or `freq` is `Frequency.DAYS`.
    """
    if is_null(series) or freq is Frequency.DAYS:
        return POSITION

    def _is_middle(offset: int, period_length: int) -> bool:
        """Checks if `offset` lies near the period midpoint with a small tolerance."""
        # 5% of the period length (min 1) to accommodate calendar irregularities and data jitter
        tolerance = max(1, int(round(period_length * 0.05)))
        return abs(offset - (period_length / 2)) <= tolerance

    if freq is Frequency.WEEKS:
        w = mode(get_weekdays(series, use_index=True))  # 0..6 (Mon … Sun)
        if _is_middle(w, DAYS_PER_WEEK):
            return Position.MIDDLE
        return Position.START if w < DAYS_PER_WEEK / 2 else Position.END

    elif freq is Frequency.MONTHS:
        d = mode(get_days(series, use_index=True))  # 1..31
        if _is_middle(d, DAYS_PER_MONTH):
            return Position.MIDDLE
        return Position.START if d < DAYS_PER_MONTH / 2 else Position.END

    elif freq is Frequency.QUARTERS:
        doy = mode(get_days(series, use_index=True, year=True))  # day of the year
        off = doy % DAYS_PER_QUARTER  # 0 … (length-1) within the quarter
        if _is_middle(off, DAYS_PER_QUARTER):
            return Position.MIDDLE
        return Position.START if off < DAYS_PER_QUARTER / 2 else Position.END

    elif freq is Frequency.SEMESTERS:
        doy = mode(get_days(series, use_index=True, year=True))  # day of the year
        off = doy % DAYS_PER_SEMESTER  # 0 … (length-1) within the semester
        if _is_middle(off, DAYS_PER_SEMESTER):
            return Position.MIDDLE
        return Position.START if off < DAYS_PER_SEMESTER / 2 else Position.END

    elif freq is Frequency.YEARS:
        doy = mode(get_days(series, use_index=True, year=True))  # day of the year
        if _is_middle(doy, DAYS_PER_YEAR):
            return Position.MIDDLE
        return Position.START if doy < DAYS_PER_YEAR / 2 else Position.END

    return Position.AUTO


def find_nearest_frequency_position(series):
    freq = find_nearest_freq(series)
    pos = find_nearest_position(series, freq=freq)
    return freq, pos


##############################


def group_series(series, clean=False, sort=False, freq=FREQUENCY):
    if clean:
        series = remove_null(series)
    if is_empty(series):
        return series
    return series.groupby(split_index(series.index, freq=freq), sort=sort)


def split_index(series, freq=FREQUENCY):
    if isinstance(series, pd.MultiIndex):
        return series.index
    if freq is Frequency.WEEKS:
        names = ("year", "week")
        index = (get_years(series, use_index=True), get_weeks(series, use_index=True))
    elif freq is Frequency.MONTHS:
        names = ("year", "month")
        index = (get_years(series, use_index=True), get_months(series, use_index=True))
    elif freq is Frequency.QUARTERS:
        names = ("year", "quarter")
        index = (get_years(series, use_index=True), get_quarters(series, use_index=True))
    elif freq is Frequency.SEMESTERS:
        names = ("year", "semester")
        index = (get_years(series, use_index=True), get_semesters(series, use_index=True))
    elif freq is Frequency.YEARS:
        names = ("year",)
        index = (get_years(series, use_index=True),)
    else:
        names = ("year", "month", "day")
        index = [
            get_years(series, use_index=True),
            get_months(series, use_index=True),
            get_days(series, use_index=True),
        ]
    return pd.MultiIndex.from_tuples(zip(*index), names=names)


##############################


def ungroup_series(
    series: pd.Series,
    clean: bool = False,
    freq: Frequency = FREQUENCY,
    pos: Position = POSITION,
) -> pd.Series:
    """
    Ungroups a reduced series by anchoring grouped period keys to start/middle/end timestamps.

    Args:
        series: The reduced `pd.Series` indexed by grouped period keys (tuples/ints).
        clean: Whether to remove null values before processing.
        freq: The grouping frequency that produced the index.
        pos: The positional anchor (`START`|`MIDDLE`|`END`; `AUTO` defaults to `END`).

    Returns:
        The `pd.Series` with a flat `DatetimeIndex` anchored per `pos`.
    """
    if clean:
        series = remove_null(series)
    if is_empty(series):
        return series

    if pos is Position.AUTO:
        pos = Position.END

    # Select the anchor
    series.index = [get_anchor_for_key(key, freq=freq, pos=pos) for key in series.index]
    return series


### TIME SERIES PRUNING ####################################


def prune_series(
    series: pd.Series,
    freq: Frequency = FREQUENCY,
    pos: Position = POSITION,
    clean: bool = False,
    sort: bool = False,
) -> pd.Series:
    """
    Prunes a time series to one observation per period at `freq`, anchored to `pos` (`Position.START|MIDDLE|END`).

    • START/END → uses vectorized `groupby().first()` / `groupby().last()` and anchors via `ungroup_series`.
    • MIDDLE    → picks, within each period, the observation nearest to the midpoint between start and end.

    Args:
        series: The input `pd.Series` with a datetime-like index.
        freq: The grouping frequency (e.g., `Frequency.WEEKS`, `MONTHS`, `QUARTERS`, …).
        pos: The positional anchor (`Position.START|MIDDLE|END`; `AUTO` is inferred from data).
        clean: Whether to remove null values before processing.
        sort: Whether to sort ascending by index within each group.

    Returns:
        A `pd.Series` with exactly one row per period and a `DatetimeIndex` anchored to `pos`.
    """
    # Normalize the inputs
    if clean:
        series = remove_null(series)
    if is_empty(series):
        return series
    if sort:
        series = sort_index(series)

    # Resolve the frequency and position from the data if needed
    freq, pos = get_frequency_and_position(series, freq=freq, pos=pos)

    # Handle fast pruning for `Position.START|END`
    if pos in (Position.START, Position.END):
        g = group_series(series, freq=freq)
        reduced = g.first() if pos is Position.START else g.last()
        return ungroup_series(reduced, freq=freq, pos=pos)

    # Handle pruning for `Position.MIDDLE`
    grouped = group_series(series, freq=freq)
    anchors: List[pd.Timestamp] = []
    values: List[float] = []
    for key, s in grouped:
        key_tuple = key if isinstance(key, tuple) else (key,)
        anchor = get_anchor_for_key(key_tuple, freq=freq, pos=pos)
        anchors.append(anchor)
        values.append(s.iloc[find_nearest_index(s.index, anchor)])

    pruned = pd.Series(values, index=pd.DatetimeIndex(anchors))
    pruned.name = series.name
    return pruned.sort_index()


def get_anchor_for_key(key: Tuple[int, ...], freq: Frequency = FREQUENCY, pos: Position = POSITION) -> pd.Timestamp:
    """
    Selects an anchor timestamp (start/middle/end) for a grouped index `key` at `freq`.

    Args:
        freq: The grouping frequency.
        pos: The positional anchor (`START`|`MIDDLE`|`END`).
        key: The period key tuple (e.g., `(y, m)`, `(y, q)`, `(y, m, d)` …).

    Returns:
        The anchored timestamp for that period.
    """
    if freq is Frequency.WEEKS:
        y, w = key
        start = get_start_period(y, w=w)
        end = get_end_period(y, w=w)
    elif freq is Frequency.MONTHS:
        y, m = key
        start = get_start_period(y, m=m)
        end = get_end_period(y, m=m)
    elif freq is Frequency.QUARTERS:
        y, q = key
        start = get_start_period(y, q=q)
        end = get_end_period(y, q=q)
    elif freq is Frequency.SEMESTERS:
        y, s = key
        start = get_start_period(y, s=s)
        end = get_end_period(y, s=s)
    elif freq is Frequency.YEARS:
        (y,) = key if isinstance(key, tuple) else (key,)
        start = get_start_period(y)
        end = get_end_period(y)
    else:
        y, m, d = key
        start = get_start_period(y, m=m, d=d)
        end = get_end_period(y, m=m, d=d)

    if pos is Position.START:
        return start
    elif pos is Position.END:
        return end

    # Return the middle anchor timestamp
    return pd.Timestamp(start + (end - start) / 2).normalize()


def find_nearest_index(index: pd.DatetimeIndex, anchor: pd.Timestamp) -> int:
    """Finds the closest index to the anchor."""
    if not isinstance(index, pd.DatetimeIndex):
        index = pd.to_datetime(index, errors="coerce")
    valid = ~index.isna()
    index = index[valid]
    if len(index) == 0:
        return 0
    deltas = (index - anchor).to_numpy()
    abs_ns = np.abs(deltas.astype("timedelta64[ns]").astype(np.int64))
    min_abs = abs_ns.min()
    candidates = np.flatnonzero(abs_ns == min_abs)
    return int(candidates[0])


### TIME SERIES TRANSFORMATION #############################


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


### TIME SERIES AGGREGATION ################################


def aggregate_series(
    series: pd.Series,
    clean: bool = False,
    sort: bool = False,
    agg: Optional[Aggregation] = AGGREGATION,
    freq: Union[str, pd.DateOffset] = FREQUENCY,
    pos: Position = POSITION,
) -> pd.Series:
    """
    Aggregates a time series by grouping at `freq` and applying `agg`, then restores a flat index.

    Args:
        series: The input `pd.Series` with a datetime-like index.
        clean: Whether to remove null values before processing.
        sort: Whether to sort the index before grouping.
        agg: The aggregation to apply (e.g., `Aggregation.MEAN`, `Aggregation.SUM`).
             Use `Aggregation.IDENTITY` to skip the aggregation.
        freq: The grouping frequency (e.g., `"D"`, `"W"`, `"M"`), or a `pd.DateOffset`.
        pos: The anchoring policy when ungrouping (e.g., `Position.START` or `Position.END`).

    Returns:
        The aggregated `pd.Series`. For `IDENTITY`, returns the (optionally cleaned/sorted) input.

    Raises:
        ValueError: If `agg` is null or unsupported.
    """
    if is_null(agg):
        raise ValueError("Require a concrete 'Aggregation' (or 'Aggregation.IDENTITY')")

    # Normalize the inputs
    if clean:
        series = remove_null(series)
    if is_empty(series):
        return series
    if sort:
        series = sort_index(series)

    # Resolve the frequency and position from the data if needed
    freq, pos = get_frequency_and_position(series, freq=freq, pos=pos)

    if agg is Aggregation.IDENTITY:
        return prune_series(series, freq=freq, pos=pos)

    # Group by frequency
    grouped = group_series(series, freq=freq)

    # Aggregate
    aggregate: Dict[Aggregation, Callable[[], pd.Series]] = {
        Aggregation.COUNT: grouped.count,
        Aggregation.MIN: grouped.min,
        Aggregation.MAX: grouped.max,
        Aggregation.MEAN: grouped.mean,
        Aggregation.MEDIAN: grouped.median,
        Aggregation.STD: grouped.std,
        Aggregation.VAR: grouped.var,
        Aggregation.SUM: grouped.sum,
    }
    try:
        series = aggregate[agg]()
    except KeyError as e:
        raise ValueError(f"Unsupported aggregation: '{agg}'") from e

    # Restore a flat index at the requested anchor
    return ungroup_series(series, freq=freq, pos=pos)


### TIME SERIES TRANSFORMATION #############################


def transform_series(
    series: pd.Series,
    method: Optional[str] = None,
    how: Optional[str] = None,
    normalize: bool = False,
    fill_value: Optional[Any] = None,
    clean: bool = False,
    sort: bool = False,
    agg: Optional["Aggregation"] = AGGREGATION,
    freq: Optional[Union[str, pd.DateOffset]] = FREQUENCY,
    pos: Position = POSITION,
    transformation: Optional["Transformation"] = None,
) -> pd.Series:
    """
    Transforms a time series by optional aggregation, frequency conversion, and statistical transforms.

    Args:
        series: The input `pd.Series` with a datetime-like index.
        method: The frequency-conversion fill method (`"pad"|"ffill"|"backfill"|"bfill"`); `None` to skip.
        how: The `PeriodIndex` anchor (`"start"|"end"`) during conversion; ignored for `DatetimeIndex`.
        normalize: If `True`, resets timestamps to midnight during conversion.
        fill_value: The value inserted in missing positions during upsampling (existing NaNs are preserved).
        clean: Whether to remove null values before processing.
        sort: Whether to sort by the datetime index before processing.
        agg: The aggregation to apply; `Aggregation.IDENTITY` or `None` skips aggregation.
        freq: The target frequency used to position and aggregate the data.
        pos: The position policy when mapping periods to timestamps.
        transformation: The post-aggregation transform (`LOG|DIFF|RETURNS|LOG_RETURNS`).

    Returns:
        The transformed `pd.Series`.

    Raises:
        ValueError: If `method`/`how`/`transformation` is unsupported.
    """
    # Check the inputs
    if not is_null(method) and method not in {"pad", "ffill", "backfill", "bfill"}:
        raise ValueError(f"Unsupported 'method': '{method}'")
    if not is_null(how) and how not in {"start", "end"}:
        raise ValueError(f"Unsupported 'how': '{how}'")

    # Resolve the frequency and position from the data if needed
    freq, pos = get_frequency_and_position(series, freq=freq, pos=pos)

    # Aggregate
    series = aggregate_series(series, clean=clean, sort=sort, agg=agg, freq=freq, pos=pos)
    if is_empty(series):
        return series

    # Normalize
    if not is_null(method):
        series = series.asfreq(
            get_frequency(freq, pos),
            method=method,
            how=how,
            normalize=normalize,
            fill_value=fill_value,
        )

    # Transform
    if not is_null(transformation):
        transform: Dict["Transformation", Callable[[pd.Series], pd.Series]] = {
            Transformation.LOG: log,
            Transformation.DIFF: get_diff,
            Transformation.RETURNS: get_returns,
            Transformation.LOG_RETURNS: get_log_returns,
        }
        try:
            series = transform[transformation](series)
        except KeyError as e:
            raise ValueError(f"Unsupported transformation: '{transformation}'") from e

    return series


def untransform_series(series, offset=0, clean=False, transformation=None):
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


### TIME SERIES DECOMPOSITION ##############################


def decompose_series(series, seasonal_period=1, freq=FREQUENCY, pos=POSITION):
    """
    Decomposes the specified time series into trend and seasonality using the seasonal-trend decomposition procedure STL
    based on LOESS of R. B. Cleveland, W. S. Cleveland, J.E. McRae, and I. Terpenning (1990).
    """
    # Resolve the frequency and position from the data if needed
    freq, pos = get_frequency_and_position(series, freq=freq, pos=pos)

    series = prepare_series(series, freq=freq, pos=pos)
    seasonal_period_length = seasonal_period * get_period_length(get_date(), freq=freq)
    return STL(series, period=seasonal_period_length).fit()


### TIME SERIES FORECASTING ################################


def forecast_series(
    series,
    horizon=1,
    initialization_method="estimated",
    trend="add",
    seasonal="add",
    seasonal_period=1,
    freq=FREQUENCY,
    period=PERIOD,
    pos=POSITION,
):
    """Forecasts the specified time series using Holt Winter's Exponential Smoothing (2014)."""
    # Resolve the frequency and position from the data if needed
    freq, pos = get_frequency_and_position(series, freq=freq, pos=pos)
    if pos is Position.MIDDLE:
        # Statsmodels forecasting needs a real frequency
        pos = Position.END

    series = prepare_series(series, freq=freq, pos=pos)
    seasonal_period_length = seasonal_period * get_period_length(get_date(), period=period, freq=freq)
    predictions = set_index(to_frame([]), series.index)
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
