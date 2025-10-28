#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains common utility functions
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

from __future__ import annotations

from calendar import monthrange
from datetime import timezone
from math import ceil

from dateutil import parser
from dateutil.relativedelta import relativedelta

from nutil.config import *
from nutil.enums import *
from nutil.scalar.number import *
from nutil.scalar.string import *

## DATE CONSTANTS ########################################################################

__DATE_CONSTANTS____________________________________________ = ""

DEFAULT_TIME_ZONE = timezone.utc

##############################

# The default date format
DEFAULT_DATE_FORMAT = "%Y-%m-%d"

# The default time format
DEFAULT_TIME_FORMAT = "%H:%M:%S.%f"

# The default date-time format
DEFAULT_DATE_TIME_FORMAT = DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT

# The default full date format
DEFAULT_FULL_DATE_FORMAT = "%B %e, %Y"

# The default month-year date format
DEFAULT_MONTH_YEAR_FORMAT = "%Y-%m"

# The default full month-year date format
DEFAULT_FULL_MONTH_YEAR_FORMAT = "%B %Y"

# The default month date format
DEFAULT_MONTH_FORMAT = "%b"

# The default full month date format
DEFAULT_FULL_MONTH_FORMAT = "%B"

##############################

# The default aggregation
DEFAULT_AGGREGATION = Aggregation.IDENTITY

# The default frequency
DEFAULT_FREQUENCY = Frequency.MONTHS

# The default period
DEFAULT_PERIOD = "1" + Frequency.YEARS.value

# The default position
DEFAULT_POSITION = Position.END

############################################################

# The weekdays
MON, TUE, WED, THU, FRI, SAT, SUN = WEEKDAYS = tuple(i for i in range(7))
WEEKDAY_NAMES = ("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

##############################

# The time durations
DAY = np.timedelta64(1, "D")
WEEK = np.timedelta64(1, "W")
MONTH = np.timedelta64(1, "M")
QUARTER = 3 * MONTH
SEMESTER = 6 * MONTH
YEAR = np.timedelta64(1, "Y")

FREQUENCY_TO_DURATION = {
    Frequency.DAYS: DAY,
    Frequency.WEEKS: WEEK,
    Frequency.MONTHS: MONTH,
    Frequency.QUARTERS: QUARTER,
    Frequency.SEMESTERS: SEMESTER,
    Frequency.YEARS: YEAR,
}

# DURATION_TO_FREQUENCY = {v: k for k, v in FREQUENCY_TO_DURATION.items()}

##############################

# The relative time durations
RELATIVE_DAY = relativedelta(days=1)
RELATIVE_WEEK = relativedelta(weeks=1)
RELATIVE_MONTH = relativedelta(months=1)
RELATIVE_QUARTER = 3 * RELATIVE_MONTH
RELATIVE_SEMESTER = 6 * RELATIVE_MONTH
RELATIVE_YEAR = relativedelta(years=1)

FREQUENCY_TO_RELATIVE_DURATION = {
    Frequency.DAYS: RELATIVE_DAY,
    Frequency.WEEKS: RELATIVE_WEEK,
    Frequency.MONTHS: RELATIVE_MONTH,
    Frequency.QUARTERS: RELATIVE_QUARTER,
    Frequency.SEMESTERS: RELATIVE_SEMESTER,
    Frequency.YEARS: RELATIVE_YEAR,
}

RELATIVE_DURATION_TO_FREQUENCY = {v: k for k, v in FREQUENCY_TO_RELATIVE_DURATION.items()}

##############################

# The average number of days per year
DAYS_PER_YEAR = 365.25  # days

# The average number of trading days per year
TRADING_DAYS_PER_YEAR = 253  # days

# The average number of weeks per year
WEEKS_PER_YEAR = DAYS_PER_YEAR / 7  # weeks

# The number of months per year
MONTHS_PER_YEAR = 12  # months

# The number of quarters per year
QUARTERS_PER_YEAR = 4  # quarters

# The number of semesters per year
SEMESTERS_PER_YEAR = 2  # semesters

##############################

# The number of days per week
DAYS_PER_WEEK = 7  # days

# The average number of days per month
DAYS_PER_MONTH = DAYS_PER_YEAR / MONTHS_PER_YEAR  # days

# The average number of days per quarter
DAYS_PER_QUARTER = DAYS_PER_YEAR / QUARTERS_PER_YEAR  # days

# The average number of days per semester
DAYS_PER_SEMESTER = DAYS_PER_YEAR / SEMESTERS_PER_YEAR  # days

FREQUENCY_TO_DAY_COUNT = {
    Frequency.DAYS: 1,
    Frequency.WEEKS: DAYS_PER_WEEK,
    Frequency.MONTHS: DAYS_PER_MONTH,
    Frequency.QUARTERS: DAYS_PER_QUARTER,
    Frequency.SEMESTERS: DAYS_PER_SEMESTER,
    Frequency.YEARS: DAYS_PER_YEAR,
}

DAY_COUNT_TO_FREQUENCY = {v: k for k, v in FREQUENCY_TO_DAY_COUNT.items()}


## DATE ACCESSORS ########################################################################

__DATE_ACCESSORS____________________________________________ = ""

def get_date():
    return date.today()


def get_date_string():
    return format_date(get_date())


def get_datetime(tz=DEFAULT_TIME_ZONE):
    return datetime.now(tz=tz)


def get_datetime_string(format=DATE_TIME_FORMAT):
    return format_datetime(get_datetime(), format=format)


def get_time_string():
    return format_time(get_datetime())


def get_datestamp():
    return to_datestamp(get_date())


def get_timestamp():
    return to_timestamp(get_datetime())


def get_stamp():
    return to_stamp(get_datetime())


##############################


def get_microsecond(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        return d.microsecond
    return d


def get_microseconds(s, use_index=False):
    if use_index:
        from nutil.struct.util import get_index
        if is_table(s):
            return to_timestamp(get_index(s)).microsecond
        elif is_dict(s):
            return get_microseconds(get_index(s))
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_microsecond(d) for d in s], s)


def get_second(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        return d.second
    return d


def get_seconds(s, use_index=False):
    if use_index:
        from nutil.struct.util import get_index
        if is_table(s):
            return to_timestamp(get_index(s)).second
        elif is_dict(s):
            return get_seconds(get_index(s))
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_second(d) for d in s], s)


def get_minute(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        return d.minute
    return d


def get_minutes(s, use_index=False):
    if use_index:
        from nutil.struct.util import get_index
        if is_table(s):
            return to_timestamp(get_index(s)).minute
        elif is_dict(s):
            return get_minutes(get_index(s))
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_minute(d) for d in s], s)


def get_hour(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        return d.hour
    return d


def get_hours(s, use_index=False):
    if use_index:
        from nutil.struct.util import get_index
        if is_table(s):
            return to_timestamp(get_index(s)).hour
        elif is_dict(s):
            return get_hours(get_index(s))
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_hour(d) for d in s], s)


def get_day(d=get_datetime(), week=False, year=False):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        return d.weekday() if week else d.timetuple().tm_yday if year else d.day
    return d


def get_days(s, use_index=False, week=False, year=False):
    if use_index:
        from nutil.struct.util import get_index
        if is_table(s):
            index = to_timestamp(get_index(s))
            return index.weekday if week else index.dayofyear if year else index.day
        elif is_dict(s):
            return get_days(get_index(s), week=week, year=year)
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_day(d, week=week, year=year) for d in s], s)


def get_weekday(d=get_datetime()):
    return get_day(d, week=True)


def get_weekdays(d=get_datetime(), use_index=False):
    return get_days(d, use_index=use_index, week=True)


def get_weekday_name(d=get_datetime()):
    return WEEKDAY_NAMES[get_weekday(d)]


def get_weekday_names(d=get_datetime(), use_index=False):
    from nutil.struct.util import apply
    return apply(get_weekdays(d, use_index=use_index), lambda d: WEEKDAY_NAMES[d])


def get_week(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        return d.isocalendar()[1]
    return d


def get_weeks(s, use_index=False):
    if use_index:
        from nutil.struct.util import get_index
        if is_table(s):
            return to_timestamp(get_index(s)).week
        elif is_dict(s):
            return get_weeks(get_index(s))
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_week(d) for d in s], s)


def get_year_week(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        iso_cal = d.isocalendar()
        return iso_cal[0], iso_cal[1]
    return d


def get_year_weeks(s, use_index=False):
    if use_index:
        from nutil.struct.util import get_index
        if is_table(s):
            return pd.MultiIndex.from_tuples(get_year_weeks(get_index(s)), names=["year", "week"])
        elif is_dict(s):
            return get_year_weeks(get_index(s))
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_year_week(d) for d in s], s)


def get_month(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        return d.month
    return d


def get_months(s, use_index=False):
    if use_index:
        from nutil.struct.util import get_index
        if is_table(s):
            return to_timestamp(get_index(s)).month
        elif is_dict(s):
            return get_months(get_index(s))
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_month(d) for d in s], s)


def get_quarter(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        return ceil(d.month / 3)
    return d


def get_quarters(s, use_index=False):
    if use_index:
        from nutil.struct.util import get_index
        if is_table(s):
            return to_timestamp(get_index(s)).quarter
        elif is_dict(s):
            return get_quarters(get_index(s))
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_quarter(d) for d in s], s)


def get_semester(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        return ceil(d.month / 6)
    return d


def get_semesters(s, use_index=False):
    if use_index:
        if is_table(s):
            return ceil(get_months(s, use_index=use_index) / 6)
        elif is_dict(s):
            from nutil.struct.util import get_index
            return get_semesters(get_index(s))
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_semester(d) for d in s], s)


def get_year(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if is_date(d):
        return d.year
    return d


def get_years(s, use_index=False):
    if use_index:
        from nutil.struct.util import get_index
        if is_table(s):
            return to_timestamp(get_index(s)).year
        elif is_dict(s):
            return get_years(get_index(s))
    from nutil.struct.util import collection_to_type
    return collection_to_type([get_year(d) for d in s], s)


##############################


def get_business_day(d=get_datetime(), prev=True):
    if is_string(d):
        d = parse_datetime(d)
    if not is_business_day(d):
        return get_prev_business_day(d) if prev else get_next_business_day(d)
    return d


def get_prev_business_day(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    day = date.weekday(d)
    if day is MON:
        return d - 3 * RELATIVE_DAY
    elif day is SUN:
        return d - 2 * RELATIVE_DAY
    return d - RELATIVE_DAY


def get_next_business_day(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    day = date.weekday(d)
    if day is FRI:
        return d + 3 * RELATIVE_DAY
    elif day is SAT:
        return d + 2 * RELATIVE_DAY
    return d + RELATIVE_DAY


##############################


def get_month_range(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    return monthrange(d.year, d.month)


def get_month_weekday(year, month):
    return monthrange(year, month)[0]


def get_month_days(year, month):
    return monthrange(year, month)[1]


##############################


def get_month_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    return reset_time(d.replace(day=1))


def get_month_end(d=get_datetime()):
    if is_collection(d):
        from nutil.struct.util import apply
        return apply(d, get_month_end)
    elif is_string(d):
        d = parse_datetime(d)
    return reset_time(d.replace(day=get_month_days(d.year, d.month)))


def get_prev_month_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if d.month == 1:
        year = d.year - 1
        month = 12
    else:
        year = d.year
        month = d.month - 1
    return reset_time(d.replace(year=year, month=month, day=1))


def get_prev_month_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if d.month == 1:
        year = d.year - 1
        month = 12
    else:
        year = d.year
        month = d.month - 1
    return reset_time(d.replace(year=year, month=month, day=get_month_days(year, month)))


def get_next_month_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if d.month == 12:
        year = d.year + 1
        month = 1
    else:
        year = d.year
        month = d.month + 1
    return reset_time(d.replace(year=year, month=month, day=1))


def get_next_month_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if d.month == 12:
        year = d.year + 1
        month = 1
    else:
        year = d.year
        month = d.month + 1
    return reset_time(d.replace(year=year, month=month, day=get_month_days(year, month)))


##############################


def get_quarter_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 3:
        month = 1
    elif 4 <= d.month <= 6:
        month = 4
    elif 7 <= d.month <= 9:
        month = 7
    else:
        month = 10
    return reset_time(d.replace(month=month, day=1))


def get_quarter_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 3:
        month = 3
    elif 4 <= d.month <= 6:
        month = 6
    elif 7 <= d.month <= 9:
        month = 9
    else:
        month = 12
    return reset_time(d.replace(month=month, day=get_month_days(d.year, month)))


def get_prev_quarter_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 3:
        year = d.year - 1
        month = 10
    elif 4 <= d.month <= 6:
        year = d.year
        month = 1
    elif 7 <= d.month <= 9:
        year = d.year
        month = 4
    else:
        year = d.year
        month = 7
    return reset_time(d.replace(year=year, month=month, day=1))


def get_prev_quarter_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 3:
        year = d.year - 1
        month = 10
    elif 4 <= d.month <= 6:
        year = d.year
        month = 1
    elif 7 <= d.month <= 9:
        year = d.year
        month = 4
    else:
        year = d.year
        month = 7
    return reset_time(d.replace(year=year, month=month, day=get_month_days(year, month)))


def get_next_quarter_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 3:
        year = d.year
        month = 4
    elif 4 <= d.month <= 6:
        year = d.year
        month = 7
    elif 7 <= d.month <= 9:
        year = d.year
        month = 10
    else:
        year = d.year + 1
        month = 1
    return reset_time(d.replace(year=year, month=month, day=1))


def get_next_quarter_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 3:
        year = d.year
        month = 4
    elif 4 <= d.month <= 6:
        year = d.year
        month = 7
    elif 7 <= d.month <= 9:
        year = d.year
        month = 10
    else:
        year = d.year + 1
        month = 1
    return reset_time(d.replace(year=year, month=month, day=get_month_days(year, month)))


##############################


def get_semester_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 6:
        month = 1
    else:
        month = 7
    return reset_time(d.replace(month=month, day=1))


def get_semester_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 6:
        month = 6
    else:
        month = 12
    return reset_time(d.replace(month=month, day=get_month_days(d.year, month)))


def get_prev_semester_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 6:
        year = d.year - 1
        month = 7
    else:
        year = d.year
        month = 1
    return reset_time(d.replace(year=year, month=month, day=1))


def get_prev_semester_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 6:
        year = d.year - 1
        month = 12
    else:
        year = d.year
        month = 6
    return reset_time(d.replace(year=year, month=month, day=get_month_days(year, month)))


def get_next_semester_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 6:
        year = d.year
        month = 7
    else:
        year = d.year + 1
        month = 1
    return reset_time(d.replace(year=year, month=month, day=1))


def get_next_semester_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    if 1 <= d.month <= 6:
        year = d.year
        month = 12
    else:
        year = d.year + 1
        month = 6
    return reset_time(d.replace(year=year, month=month, day=get_month_days(year, month)))


##############################


def get_year_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    return reset_time(d.replace(month=1, day=1))


def get_year_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    return reset_time(d.replace(month=12, day=31))


def get_prev_year_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    return reset_time(d.replace(year=d.year - 1, month=1, day=1))


def get_prev_year_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    return reset_time(d.replace(year=d.year - 1, month=12, day=31))


def get_next_year_start(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    return reset_time(d.replace(year=d.year + 1, month=1, day=1))


def get_next_year_end(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    return reset_time(d.replace(year=d.year + 1, month=12, day=31))


##############################


def get_start_period(y, s=None, q=None, m=None, w=None, d=None):
    if is_all_not_null(y, m, d):
        return create_datetime(y, m, d)
    elif is_all_not_null(y, w):
        return datetime.fromisocalendar(y, w, 1)
    elif is_all_not_null(y, m):
        return create_datetime(y, m, 1)
    elif is_all_not_null(y, q):
        return create_datetime(y, 1 + 3 * (q - 1), 1)
    elif is_all_not_null(y, s):
        return create_datetime(y, 1 + 6 * (s - 1), 1)
    return create_datetime(y, 1, 1)


def get_end_period(y, s=None, q=None, m=None, w=None, d=None):
    if is_all_not_null(y, m, d):
        return create_datetime(y, m, d)
    elif is_all_not_null(y, w):
        return datetime.fromisocalendar(y, w, 7)
    elif is_all_not_null(y, m):
        return create_datetime(y, m, monthrange(y, m)[1])
    elif is_all_not_null(y, q):
        return create_datetime(y, 3 + 3 * (q - 1), monthrange(y, 3 + 3 * (q - 1))[1])
    elif is_all_not_null(y, s):
        return create_datetime(y, 6 + 6 * (s - 1), monthrange(y, 6 + 6 * (s - 1))[1])
    return create_datetime(y, 12, 31)


##############################


def get_start_date(d=get_datetime(), freq=FREQUENCY):
    if freq is Frequency.WEEKS:
        y, w = get_year_week(d)
        return get_start_period(y=y, w=w)
    elif freq is Frequency.MONTHS:
        return get_start_period(y=get_year(d), m=get_month(d))
    elif freq is Frequency.QUARTERS:
        return get_start_period(y=get_year(d), q=get_quarter(d))
    elif freq is Frequency.SEMESTERS:
        return get_start_period(y=get_year(d), s=get_semester(d))
    elif freq is Frequency.YEARS:
        return get_start_period(y=get_year(d))
    return to_datetime(d)


def get_end_date(d=get_datetime(), freq=FREQUENCY):
    if freq is Frequency.WEEKS:
        y, w = get_year_week(d)
        return get_end_period(y=y, w=w)
    elif freq is Frequency.MONTHS:
        return get_end_period(y=get_year(d), m=get_month(d))
    elif freq is Frequency.QUARTERS:
        return get_end_period(y=get_year(d), q=get_quarter(d))
    elif freq is Frequency.SEMESTERS:
        return get_end_period(y=get_year(d), s=get_semester(d))
    elif freq is Frequency.YEARS:
        return get_end_period(y=get_year(d))
    return to_datetime(d)


def get_start_datetime(d=get_datetime(), freq=FREQUENCY):
    return to_datetime(get_start_date(d, freq=freq))


def get_end_datetime(d=get_datetime(), freq=FREQUENCY):
    return to_datetime(get_end_date(d, freq=freq))


def get_start_timestamp(d=get_datetime(), freq=Frequency.DAYS):
    return to_timestamp(get_start_date(d, freq=freq))


def get_end_timestamp(d=get_datetime(), freq=Frequency.DAYS):
    return to_timestamp(get_end_date(d, freq=freq))


##############################


def get_frequency(freq=FREQUENCY, pos=POSITION):
    if is_null(freq):
        return None
    f = freq.value
    if pos is Position.START:
        if freq is Frequency.DAYS:
            pass
        elif freq is Frequency.WEEKS:
            f += "-" + WEEKDAY_NAMES[MON]
        else:
            f += "S"
    return f


##############################


def get_period_index(period=PERIOD):
    period_length = to_period_length(period)
    period_freq = to_period_freq(period)
    if period_freq is Frequency.DAYS:
        return period_length
    elif period_freq is Frequency.WEEKS:
        return period_length * DAYS_PER_WEEK
    elif period_freq is Frequency.MONTHS:
        return period_length * DAYS_PER_MONTH
    elif period_freq is Frequency.QUARTERS:
        return period_length * DAYS_PER_QUARTER
    elif period_freq is Frequency.SEMESTERS:
        return period_length * DAYS_PER_SEMESTER
    elif period_freq is Frequency.YEARS:
        return period_length * DAYS_PER_YEAR


def get_period_length(d=get_datetime(), period=PERIOD, freq=FREQUENCY):
    return diff_date(subtract_period(d, period), d, freq=freq)


def get_period_days(d=get_datetime(), period=PERIOD):
    if is_null(d):
        period_length = to_period_length(period)
        period_freq = to_period_freq(period)
        return period_length * FREQUENCY_TO_DAY_COUNT[period_freq]
    return diff_days(subtract_period(d, period), d)


def get_period_weeks(d=get_datetime(), period=PERIOD):
    if is_null(d):
        return get_period_days(d, period=period) / DAYS_PER_WEEK
    return diff_weeks(subtract_period(d, period), d)


def get_period_months(d=get_datetime(), period=PERIOD):
    if is_null(d):
        return get_period_days(d, period=period) / DAYS_PER_MONTH
    return diff_months(subtract_period(d, period), d)


def get_period_quarters(d=get_datetime(), period=PERIOD):
    if is_null(d):
        return get_period_days(d, period=period) / DAYS_PER_QUARTER
    return diff_quarters(subtract_period(d, period), d)


def get_period_semesters(d=get_datetime(), period=PERIOD):
    if is_null(d):
        return get_period_days(d, period=period) / DAYS_PER_SEMESTER
    return diff_semesters(subtract_period(d, period), d)


def get_period_years(d=get_datetime(), period=PERIOD):
    if is_null(d):
        return get_period_days(d, period=period) / DAYS_PER_YEAR
    return diff_years(subtract_period(d, period), d)


## DATE CONVERTERS #######################################################################

__DATE_CONVERTERS___________________________________________ = ""

def parse_date(s):
    return parser.parse(s).date()


def parse_datetime(s):
    return parser.parse(s)


def parse_time(s):
    return parser.parse(s)


def parse_stamp(s):
    return datetime.fromtimestamp(s)


############################################################

def to_date(x, format=DATE_FORMAT):
    if is_null(x):
        return None
    elif is_collection(x):
        from nutil.struct.util import apply
        return apply(x, to_date, format=format)
    elif is_stamp(x):
        x = parse_stamp(x)
        return create_date(x.year, x.month, x.day)
    elif is_timestamp(x):
        x = x.to_pydatetime()
        return create_date(x.year, x.month, x.day)
    elif is_datetime(x):
        return create_date(x.year, x.month, x.day)
    elif is_date(x):
        return x
    return datetime.strptime(x, format)


def to_datetime(x, format=DATE_TIME_FORMAT):
    if is_null(x):
        return None
    elif is_collection(x):
        from nutil.struct.util import apply
        return apply(x, to_datetime, format=format)
    elif is_stamp(x):
        return parse_stamp(x)
    elif is_timestamp(x):
        return x.to_pydatetime()
    elif is_datetime(x):
        return x
    elif is_date(x):
        return create_datetime(x.year, x.month, x.day)
    return datetime.strptime(x, format)


def to_time(x, format=TIME_FORMAT):
    if is_null(x):
        return None
    return to_datetime(x, format=format)


def to_datestamp(d):
    if is_null(d):
        return None
    elif is_stamp(d):
        d = parse_stamp(d)
    return pd.to_datetime(d).floor("D")


def to_timestamp(d):
    if is_null(d):
        return None
    elif is_stamp(d):
        d = parse_stamp(d)
    return pd.to_datetime(d)


def to_stamp(x: Any):
    if is_null(x):
        return None
    elif is_collection(x):
        from nutil.struct.util import apply
        return apply(x, to_stamp)
    elif is_stamp(x):
        return x
    return to_datetime(x).timestamp()


##############################


def timestamp_to_type(t, template):
    """Converts the specified timestamp to the type of the specified variable."""
    if is_collection(t):
        from nutil.struct.util import apply
        return apply(t, timestamp_to_type, template)
    elif is_stamp(template):
        return to_stamp(t)
    elif is_timestamp(template):
        return t
    elif is_datetime(template):
        return to_datetime(t)
    elif is_date(template):
        return to_date(t)
    return t


##############################


def to_period(length, freq=FREQUENCY):
    return str(length) + freq.value


def to_period_length(period):
    return int(period[0:-1])


def to_period_freq(period):
    return Frequency(period[-1].upper())


## DATE FORMATTERS #######################################################################

__DATE_FORMATTERS___________________________________________ = ""


def format_date(d=get_datetime()):
    return trim(format_datetime(d, format=DATE_FORMAT))


def format_full_date(d=get_datetime()):
    return trim(format_datetime(d, format=DEFAULT_FULL_DATE_FORMAT))


def format_month_year(d=get_datetime()):
    return trim(format_datetime(d, format=DEFAULT_MONTH_YEAR_FORMAT))


def format_full_month_year(d=get_datetime()):
    return trim(format_datetime(d, format=DEFAULT_FULL_MONTH_YEAR_FORMAT))


def format_month(d=get_datetime()):
    return trim(format_datetime(d, format=DEFAULT_MONTH_FORMAT))


def format_full_month(d=get_datetime()):
    return trim(format_datetime(d, format=DEFAULT_FULL_MONTH_FORMAT))


def format_datetime(d=get_datetime(), format=DATE_TIME_FORMAT):
    if is_string(d):
        d = parse_datetime(d)
    return trim(d.strftime(format)) if not is_null(d) else None


def format_time(d=get_datetime()):
    return trim(format_datetime(d, format=TIME_FORMAT))


## DATE GENERATORS #######################################################################

__DATE_GENERATORS___________________________________________ = ""

def create_date(y, m, d):
    return date(int(y), int(m), int(d))


def create_datetime(y, m, d):
    return datetime(int(y), int(m), int(d))


def create_timestamp(y, m, d):
    return pd.Timestamp(int(y), int(m), int(d))


def create_stamp(y, m, d):
    return to_stamp(create_datetime(y, m, d))


##############################


def create_date_range(
    date_from, date_to, periods=None, freq=FREQUENCY, pos=POSITION
):
    if not is_null(periods):
        return to_date(pd.date_range(date_from, date_to, periods=periods))
    if freq is Frequency.SEMESTERS:
        from nutil.struct.util import filter_with
        months = [1, 7] if pos is Position.START else [6, 12]
        return filter_with(
            create_date_sequence(date_from, date_to, freq=Frequency.QUARTERS, pos=pos),
            f=lambda d: get_month(d) in months,
        )
    f = get_frequency(freq=freq, pos=pos)
    return pd.date_range(date_from, date_to, freq=f)


def create_date_sequence(
    date_from, date_to, periods=None, freq=FREQUENCY, pos=POSITION
):
    date_range = create_date_range(date_from, date_to, periods=periods, freq=freq, pos=pos)
    return to_date(date_range)


def create_datetime_sequence(
    date_from, date_to, periods=None, freq=FREQUENCY, pos=POSITION
):
    date_range = create_date_range(date_from, date_to, periods=periods, freq=freq, pos=pos)
    return to_datetime(date_range)


def create_timestamp_sequence(
    date_from, date_to, periods=None, freq=FREQUENCY, pos=POSITION
):
    date_range = create_date_range(date_from, date_to, periods=periods, freq=freq, pos=pos)
    return to_timestamp(date_range)


def create_stamp_sequence(
    date_from, date_to, periods=None, freq=FREQUENCY, pos=POSITION
):
    date_range = create_date_range(date_from, date_to, periods=periods, freq=freq, pos=pos)
    return to_stamp(date_range)


## DATE PROCESSORS #######################################################################

__DATE_PROCESSORS___________________________________________ = ""


def add_period(d=get_datetime(), period=PERIOD):
    period_length = to_period_length(period)
    period_freq = to_period_freq(period)
    return d + period_length * FREQUENCY_TO_RELATIVE_DURATION[period_freq]


def subtract_period(d=get_datetime(), period=PERIOD):
    period_length = to_period_length(period)
    period_freq = to_period_freq(period)
    return d - period_length * FREQUENCY_TO_RELATIVE_DURATION[period_freq]


##############################


def diff_date(date_from, date_to, freq=FREQUENCY):
    if freq is Frequency.WEEKS:
        return diff_weeks(date_from, date_to)
    elif freq is Frequency.MONTHS:
        return diff_months(date_from, date_to)
    elif freq is Frequency.QUARTERS:
        return diff_quarters(date_from, date_to)
    elif freq is Frequency.SEMESTERS:
        return diff_semesters(date_from, date_to)
    elif freq is Frequency.YEARS:
        return diff_years(date_from, date_to)
    return diff_days(date_from, date_to)


def diff_days(date_from, date_to):
    return (date_to - date_from).days


def diff_weeks(date_from, date_to):
    return diff_days(date_from, date_to) / DAYS_PER_WEEK


def diff_months(date_from, date_to):
    return diff_years(date_from, date_to) * 12 + get_month(date_to) - get_month(date_from)


def diff_quarters(date_from, date_to):
    return diff_years(date_from, date_to) * 4 + get_quarter(date_to) - get_quarter(date_from)


def diff_semesters(date_from, date_to):
    return diff_years(date_from, date_to) * 2 + get_semester(date_to) - get_semester(date_from)


def diff_years(date_from, date_to):
    return date_to.year - date_from.year

##############################

def filter_days(s, days, week=False, year=False):
    """
    Filters the collection by matching its date-time index with the specified days (week days if
    week is True, days of the year if year is True, days of the month otherwise).
    """
    from nutil.struct.util import find_all_in, take_at
    indices = find_all_in(
        get_days(s, use_index=True, week=week, year=year),
        get_days(days, use_index=True, week=week, year=year),
    )
    return take_at(s, indices)


def filter_weeks(s, weeks):
    """Filters the collection by matching its date-time index with the specified weeks."""
    from nutil.struct.util import find_all_in, take_at
    indices = find_all_in(get_weeks(s, use_index=True), get_weeks(weeks, use_index=True))
    return take_at(s, indices)


def filter_year_weeks(s, year_weeks):
    """Filters the collection by matching its date-time index with the specified year-weeks."""
    from nutil.struct.util import find_all_in, take_at
    indices = find_all_in(
        get_year_weeks(s, use_index=True), get_year_weeks(year_weeks, use_index=True)
    )
    return take_at(s, indices)


def filter_months(s, months):
    """Filters the collection by matching its date-time index with the specified months."""
    from nutil.struct.util import find_all_in, take_at
    indices = find_all_in(get_months(s, use_index=True), get_months(months, use_index=True))
    return take_at(s, indices)


def filter_quarters(s, quarters):
    """Filters the collection by matching its date-time index with the specified quarters."""
    from nutil.struct.util import find_all_in, take_at
    indices = find_all_in(get_quarters(s, use_index=True), get_quarters(quarters, use_index=True))
    return take_at(s, indices)


def filter_semesters(s, semesters):
    """Filters the collection by matching its date-time index with the specified semesters."""
    from nutil.struct.util import find_all_in, take_at
    indices = find_all_in(
        get_semesters(s, use_index=True), get_semesters(semesters, use_index=True)
    )
    return take_at(s, indices)


def filter_years(s, years):
    """Filters the collection by matching its date-time index with the specified years."""
    from nutil.struct.util import find_all_in, take_at
    indices = find_all_in(get_years(s, use_index=True), get_years(years, use_index=True))
    return take_at(s, indices)


##############################

def find_nearest_period(length, freq=FREQUENCY):
    day_count = get_period_days(None, period=to_period(length, freq=freq))
    period_freq = DAY_COUNT_TO_FREQUENCY[nearest(FREQUENCY_TO_DAY_COUNT, day_count)]
    period_length = round_to_int(day_count / FREQUENCY_TO_DAY_COUNT[period_freq])
    return to_period(period_length, period_freq)


##############################


def reset_time(d=get_datetime()):
    if is_string(d):
        d = parse_datetime(d)
    elif not is_datetime(d):
        return d
    return d.replace(hour=0, minute=0, second=0, microsecond=0)


##############################

def shift_date(
    d=get_datetime(),
    years=0,
    months=0,
    weeks=0,
    days=0,
    hours=0,
    minutes=0,
    seconds=0,
    microseconds=0,
):
    return timestamp_to_type(
        d
        + pd.DateOffset(
            years=years,
            months=months,
            weeks=weeks,
            days=days,
            hours=hours,
            minutes=minutes,
            seconds=seconds,
            microseconds=microseconds,
        ),
        d,
    )

def shift_dates(
    s,
    years=0,
    months=0,
    weeks=0,
    days=0,
    hours=0,
    minutes=0,
    seconds=0,
    microseconds=0,
):
    """Shifts the date-time index of the specified collection."""
    s = ungroup(s)
    if is_table(s):
        t = s.copy()
        t.index += pd.DateOffset(
            years=years,
            months=months,
            weeks=weeks,
            days=days,
            hours=hours,
            minutes=minutes,
            seconds=seconds,
            microseconds=microseconds,
        )
        return t
    elif is_dict(s):
        return {
            shift_date(
                d,
                years=years,
                months=months,
                weeks=weeks,
                days=days,
                hours=hours,
                minutes=minutes,
                seconds=seconds,
                microseconds=microseconds,
            ): s[d]
            for d in s
        }
    from nutil.struct.util import collection_to_type
    return collection_to_type(
        [
            shift_date(
                d,
                years=years,
                months=months,
                weeks=weeks,
                days=days,
                hours=hours,
                minutes=minutes,
                seconds=seconds,
                microseconds=microseconds,
            )
            for d in s
        ],
        s,
    )

## DATE VERIFIERS ########################################################################

__DATE_VERIFIERS____________________________________________ = ""

def is_business_day(x: Any) -> bool:
    """Returns whether `x` is a business day (Monday–Friday)."""
    if is_string(x):
        x = parse_datetime(x)
    elif is_datetime(x):
        x = x.date()
    elif not is_date(x):
        raise TypeError(f"'{x}' is not a valid date or datetime")
    return x.weekday() < 5

