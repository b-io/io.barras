#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain common utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2021 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

import functools
import json
import multiprocessing as mp
import numbers
import os
import pdb
import random
import re
import string
import sys
from calendar import monthrange
from collections import Iterable, Sequence
from datetime import *
from distutils.util import *
from enum import Enum
from urllib.request import urlopen

import javaproperties as prop
import numpy as np
import pandas as pd
import validators
from dateutil import parser
from dateutil.relativedelta import relativedelta
from pandas.api.types import is_numeric_dtype

####################################################################################################
# COMMON ENUMS
####################################################################################################

__COMMON_ENUMS____________________________________ = ''


class Enum(Enum):

	def __str__(self):
		return str(self.value)


##################################################

class Environment(Enum):
	DEV = 'dev'
	TEST = 'test'
	MODEL = 'model'
	PROD = 'prod'


# • COLLECTION (LIST/DICT/DATAFRAME) ###############################################################

__COLLECTION_ENUMS________________________________ = ''


class Group(Enum):
	COUNT = 'count'
	FIRST = 'first'
	LAST = 'last'
	MIN = 'min'
	MAX = 'max'
	MEAN = 'mean'
	MEDIAN = 'median'
	STD = 'std'
	VAR = 'var'
	SUM = 'sum'


# • CONSOLE ########################################################################################

__CONSOLE_ENUMS___________________________________ = ''


class SeverityLevel(Enum):
	FAIL = 0
	ERROR = 1
	WARN = 2
	RESULT = 3
	INFO = 4
	TEST = 5
	DEBUG = 6
	TRACE = 7


# • DATE ###########################################################################################

__DATE_ENUMS______________________________________ = ''


class Frequency(Enum):
	DAYS = 'D'
	WEEKS = 'W'
	MONTHS = 'M'
	QUARTERS = 'Q'
	SEMESTERS = 'S'
	YEARS = 'Y'


####################################################################################################
# COMMON CONSTANTS
####################################################################################################

__COMMON_CONSTANTS________________________________ = ''

CORE_COUNT = mp.cpu_count()

NA_NAME = 'NA'

# • DATE ###########################################################################################

__DATE_CONSTANTS__________________________________ = ''

# The default date format
if 'DEFAULT_DATE_FORMAT' not in globals():
	DEFAULT_DATE_FORMAT = '%Y-%m-%d'

# The default full date format
if 'DEFAULT_FULL_DATE_FORMAT' not in globals():
	DEFAULT_FULL_DATE_FORMAT = '%B %e, %Y'

# The default month-year date format
if 'DEFAULT_MONTH_YEAR_FORMAT' not in globals():
	DEFAULT_MONTH_YEAR_FORMAT = '%Y-%m'

# The default full month-year date format
if 'DEFAULT_FULL_MONTH_YEAR_FORMAT' not in globals():
	DEFAULT_FULL_MONTH_YEAR_FORMAT = '%B %Y'

# The default month date format
if 'DEFAULT_MONTH_FORMAT' not in globals():
	DEFAULT_MONTH_FORMAT = '%b'

# The default full month date format
if 'DEFAULT_FULL_MONTH_FORMAT' not in globals():
	DEFAULT_FULL_MONTH_FORMAT = '%B'

# The default time format
if 'DEFAULT_TIME_FORMAT' not in globals():
	DEFAULT_TIME_FORMAT = '%H:%M:%S'

# The default date-time format
if 'DEFAULT_DATE_TIME_FORMAT' not in globals():
	DEFAULT_DATE_TIME_FORMAT = DEFAULT_DATE_FORMAT + ' ' + DEFAULT_TIME_FORMAT

#########################

# The default frequency
if 'DEFAULT_FREQUENCY' not in globals():
	DEFAULT_FREQUENCY = Frequency.MONTHS

# The default group
if 'DEFAULT_GROUP' not in globals():
	DEFAULT_GROUP = Group.LAST

# The default period
if 'DEFAULT_PERIOD' not in globals():
	DEFAULT_PERIOD = '1' + Frequency.YEARS.value

##################################################

# The time deltas
DAY = relativedelta(days=1)
WEEK = 7 * DAY
MONTH = relativedelta(months=1)
QUARTER = 3 * MONTH
SEMESTER = 6 * MONTH
YEAR = relativedelta(years=1)

#########################

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

#########################

# The number of days per week
DAYS_PER_WEEK = 7  # days

# The average number of days per month
DAYS_PER_MONTH = DAYS_PER_YEAR / MONTHS_PER_YEAR  # days

# The average number of days per quarter
DAYS_PER_QUARTER = DAYS_PER_YEAR / QUARTERS_PER_YEAR  # days

# The average number of days per semester
DAYS_PER_SEMESTER = DAYS_PER_YEAR / SEMESTERS_PER_YEAR  # days

#########################

# The weekdays
MO, TU, WE, TH, FR, SA, SU = WEEKDAYS = tuple(i for i in range(7))

# • FILE ###########################################################################################

__FILE_CONSTANTS__________________________________ = ''

# The default root
if 'DEFAULT_ROOT' not in globals():
	DEFAULT_ROOT = None

# The default resources directory
if 'DEFAULT_RES_DIR' not in globals():
	DEFAULT_RES_DIR = 'resources'

# • NUMBER #########################################################################################

__NUMBER_CONSTANTS________________________________ = ''

EPS = np.finfo(float).eps
INF = np.inf
NAN = np.nan

####################################################################################################
# COMMON VERIFIERS
####################################################################################################

__COMMON_VERIFIERS________________________________ = ''


def is_iterable(x):
	return isinstance(x, Iterable)


def is_sequence(x):
	return isinstance(x, Sequence)


def is_tuple(x):
	return isinstance(x, tuple)


#########################

def is_null(x):
	return x is None or is_nan(x)


def is_all_null(*args):
	return all([is_null(arg) for arg in to_list(*args)])


def is_all_not_null(*args):
	return not is_any_null(*args)


def is_any_null(*args):
	return any([is_null(arg) for arg in to_list(*args)])


def is_any_not_null(*args):
	return not is_all_null(*args)


#########################

def is_empty(x):
	return is_null(x) or \
	       (is_collection(x) and len(x) == 0 or is_frame(x) and count_cols(x) == 0) or \
	       str(x) == ''


def is_all_empty(*args):
	return all([is_empty(arg) for arg in to_list(*args)])


def is_all_not_empty(*args):
	return not is_any_empty(*args)


def is_any_empty(*args):
	return any([is_empty(arg) for arg in to_list(*args)])


def is_any_not_empty(*args):
	return not is_all_empty(*args)


##################################################

def exists(x):
	return x in globals() or x in locals() or x in dir(__builtins__)


# • ARRAY ##########################################################################################

__ARRAY_VERIFIERS_________________________________ = ''


def is_array(x):
	return isinstance(x, np.ndarray)


# • COLLECTION (LIST/DICT/DATAFRAME) ###############################################################

__COLLECTION_VERIFIERS____________________________ = ''


def is_collection(x):
	return is_iterable(x) and not is_string(x) and not is_tuple(x)


# • DATAFRAME ######################################################################################

__DATAFRAME_VERIFIERS_____________________________ = ''


def is_table(x):
	return is_series(x) or is_frame(x)


def is_series(x):
	return isinstance(x, pd.Series) or isinstance(x, pd.core.groupby.generic.SeriesGroupBy)


def is_frame(x):
	return isinstance(x, pd.DataFrame) or isinstance(x, pd.core.groupby.generic.DataFrameGroupBy)


def is_group(x):
	return isinstance(x, pd.core.groupby.generic.SeriesGroupBy) or \
	       isinstance(x, pd.core.groupby.generic.DataFrameGroupBy)


# • DATE ###########################################################################################

__DATE_VERIFIERS__________________________________ = ''


def is_date(x):
	return isinstance(x, date)


def is_datetime(x):
	return isinstance(x, datetime)


def is_timestamp(x):
	return isinstance(x, pd.Timestamp)


def is_stamp(x):
	return is_float(x)


#########################

def is_business_day(d):
	if is_string(d):
		d = parse_datetime(d)
	return date.weekday(d) < 5


# • DICT ###########################################################################################

__DICT_VERIFIERS__________________________________ = ''


def is_dict(x):
	return isinstance(x, dict)


# • FILE ###########################################################################################

__FILE_VERIFIERS__________________________________ = ''


def is_dir(path):
	return os.path.isdir(path)


def is_file(path):
	return os.path.isfile(path)


#########################

def is_root(path):
	return os.path.dirname(path) == path


# • LIST ###########################################################################################

__LIST_VERIFIERS__________________________________ = ''


def is_list(x):
	return isinstance(x, list)


# • NUMBER #########################################################################################

__NUMBER_VERIFIERS________________________________ = ''


def is_nan(x):
	return x is pd.NA or x is pd.NaT or (is_number(x) and str(x) == 'nan')


#########################

def is_number(x):
	return isinstance(x, numbers.Number)


def is_bool(x):
	return isinstance(x, bool)


def is_int(x):
	return isinstance(x, int)


def is_float(x):
	return isinstance(x, float)


# • STRING #########################################################################################

__STRING_VERIFIERS________________________________ = ''


def is_string(x):
	return isinstance(x, str)


####################################################################################################
# FILE FUNCTIONS
####################################################################################################

__FILE____________________________________________ = ''


def get_path(path=None):
	if is_null(path):
		path = '.'
	return os.path.abspath(path)


#########################

def get_dir(path=None, parent=None):
	path = get_path(path)
	if is_null(parent):
		parent = not is_dir(path)
	return os.path.dirname(get_path(path) + ('/' if not parent else ''))


def get_filename(path=None):
	path = get_path(path)
	return os.path.basename(path)


def get_extension(path=None):
	path = get_path(path)
	return os.path.splitext(path)[1][1:]


##################################################

def find_path(filename, dir=None, subdir=None):
	if is_null(dir):
		dir = get_dir(get_path())
		while not is_file(format_dir(dir) + format_dir(subdir) + filename) and not is_root(dir):
			dir = get_dir(dir, parent=True)
	elif is_file(dir):
		dir = get_dir(dir)
	return format_dir(dir) + format_dir(subdir) + filename


#########################

def format_dir(dir):
	if is_null(dir):
		return ''
	if dir[-1] == '/' or dir[-1] == '\\':
		dir = dir[:-1]
	return dir + '/'


#########################

def read(path, encoding=None):
	with open(path, mode='r', encoding=encoding) as f:
		return f.read()


def read_bytes(path, encoding=None):
	with open(path, mode='rb', encoding=encoding) as f:
		return f.read()


def read_json(path, encoding=None):
	if validators.url(path):
		with urlopen(path) as f:
			return json.load(f)
	with open(path, encoding=encoding) as f:
		return json.load(f)


def read_csv(path, encoding=None,
             delimiter=',', dtype=None,
             na_values=None, keep_default_na=False, na_filter=True,
             parse_dates=True, date_parser=None, infer_datetime_format=True, keep_date_col=True,
             verbose=False):
	if is_null(na_values):
		na_values = ['']
	return pd.read_csv(path, encoding=encoding,
	                   delimiter=delimiter, dtype=dtype,
	                   na_values=na_values, keep_default_na=keep_default_na, na_filter=na_filter,
	                   parse_dates=parse_dates, date_parser=date_parser,
	                   infer_datetime_format=infer_datetime_format, keep_date_col=keep_date_col,
	                   verbose=verbose)


#########################

def write(path, content, encoding=None):
	with open(path, mode='w', encoding=encoding) as f:
		return f.write(content)


def write_bytes(path, content, encoding=None):
	with open(path, mode='wb', encoding=encoding) as f:
		return f.write(content)


####################################################################################################
# COMMON PROPERTIES
####################################################################################################

__COMMON_PROPERTIES_______________________________ = ''


def load_props(filename, dir=DEFAULT_ROOT, subdir=DEFAULT_RES_DIR):
	"""Returns the properties with the specified filename in the specified directory."""
	with open(find_path(filename + '.properties', dir=dir, subdir=subdir), 'r') as f:
		return prop.load(f)


#########################

# The properties
PROPS = load_props('common')


def get_prop(name, default=None):
	"""Returns the property with the specified name."""
	try:
		return PROPS[name]
	except Exception as ex:
		return default


def get_bool_prop(name, default=None):
	prop = get_prop(name, default=default)
	if is_null(prop):
		return prop
	elif is_bool(prop):
		return prop
	return strtobool(str(prop))


def get_float_prop(name, default=None):
	prop = get_prop(name, default=default)
	if is_null(prop):
		return prop
	elif is_float(prop):
		return prop
	return float(prop)


def get_int_prop(name, default=None):
	prop = get_prop(name, default=default)
	if is_null(prop):
		return prop
	elif is_int(prop):
		return prop
	return int(prop)


##################################################

# The environment
ENV = Environment(get_prop('env', 'prod'))

# The flag specifying whether to test
TEST = get_bool_prop('test', True)

# • CONSOLE ########################################################################################

__CONSOLE_PROPERTIES______________________________ = ''

# The severity level
SEVERITY_LEVEL = SeverityLevel(get_int_prop('severity', 4))

# The flag specifying whether to enable the verbose mode
VERBOSE = get_bool_prop('verbose', True)

# • DATE ###########################################################################################

__DATE_PROPERTIES_________________________________ = ''

# The frequency
FREQUENCY = Frequency(get_prop('frequency', DEFAULT_FREQUENCY.value))

# The group
GROUP = Group(get_prop('group', DEFAULT_GROUP.value))

# The period
PERIOD = get_prop('period', DEFAULT_PERIOD)

####################################################################################################
# COMMON ACCESSORS
####################################################################################################

__COMMON_ACCESSORS________________________________ = ''


def get_exec_info():
	return sys.exc_info()[0]


#########################

def get_attributes(obj):
	return [a for a in vars(obj) if not a.startswith('_')]


def get_class_name(obj):
	return obj.__class__.__name__


def get_module_name(obj):
	return obj.__class__.__module__


def get_full_class_name(obj):
	module_name = get_module_name(obj)
	if is_null(module_name) or module_name == get_module_name(str):
		return get_class_name(obj)
	return collapse(module_name, '.', get_class_name(obj))


# • COLLECTION (LIST/DICT/DATAFRAME) ###############################################################

__COLLECTION_ACCESSORS____________________________ = ''


def get(c, index=0, axis=0):
	if is_null(axis):
		return simplify(flatten(c, axis=axis)[index])
	if is_table(c) or is_array(c):
		if axis == 0:
			return simplify(get_row(c, index))
		return simplify(get_col(c, index))
	elif is_dict(c):
		return simplify(c[get_keys(c)[index]])
	return simplify(c[index])


def get_first(c, axis=0):
	return get(c, index=0, axis=axis)


def get_last(c, axis=0):
	return get(c, index=-1, axis=axis)


#########################

def get_name(c, inclusion=None, exclusion=None):
	return simplify(get_names(c, inclusion=inclusion, exclusion=exclusion))


def get_names(c, inclusion=None, exclusion=None):
	"""Returns the names of the specified collection."""
	if is_null(c):
		return None
	elif is_empty(c):
		return []
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_table(inclusion):
		inclusion = get_names(inclusion)
	if is_table(exclusion):
		exclusion = get_names(exclusion)
	if is_series(c):
		c = c.name
	elif not is_frame(c) and not is_dict(c):
		c = range(len(c))
	return filter_list(c, inclusion=inclusion, exclusion=exclusion)


def get_key(c, inclusion=None, exclusion=None):
	return simplify(get_keys(c, inclusion=inclusion, exclusion=exclusion))


def get_keys(c, inclusion=None, exclusion=None):
	"""Returns the keys (indices/keys/names) of the specified collection that are in the specified
	inclusive list and are not in the specified exclusive list."""
	if is_null(c):
		return None
	elif is_empty(c):
		return []
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_table(inclusion):
		inclusion = get_keys(inclusion)
	if is_table(exclusion):
		exclusion = get_keys(exclusion)
	if is_series(c):
		c = c.index
	elif not is_frame(c) and not is_dict(c):
		c = range(len(c))
	return unique(filter_list(c, inclusion=inclusion, exclusion=exclusion))


def get_all_common_keys(*args, inclusion=None, exclusion=None):
	return reduce(lambda c1, c2: get_common_keys(c1, c2, inclusion=inclusion, exclusion=exclusion),
	              *args)


def get_common_keys(c1, c2, inclusion=None, exclusion=None):
	"""Returns the common keys (indices/keys/names) of the specified collections that are in the
	specified inclusive list and are not in the specified exclusive list."""
	return get_keys(c1, inclusion=include_list(get_keys(c2), inclusion), exclusion=exclusion)


def get_index(c, inclusion=None, exclusion=None):
	"""Returns the index (indices/keys/index) of the specified collection that are in the
	specified inclusive list and are not in the specified exclusive list."""
	if is_null(c):
		return None
	elif is_empty(c):
		return []
	if is_group(c):
		c = c.groups if c.axis == 0 else c.obj
	if is_table(inclusion):
		inclusion = get_index(inclusion)
	if is_table(exclusion):
		exclusion = get_index(exclusion)
	if is_table(c):
		return filter_list(c.index, inclusion=inclusion, exclusion=exclusion)
	return get_keys(c, inclusion=inclusion, exclusion=exclusion)


def get_all_common_index(*args, inclusion=None, exclusion=None):
	return reduce(lambda c1, c2: get_common_index(c1, c2, inclusion=inclusion, exclusion=exclusion),
	              *args)


def get_common_index(c1, c2, inclusion=None, exclusion=None):
	"""Returns the common index (indices/keys/index) of the specified collections that are in the
	specified inclusive list and are not in the specified exclusive list."""
	return get_index(c1, inclusion=include_list(get_keys(c2), inclusion), exclusion=exclusion)


def get_item(c, inclusion=None, exclusion=None):
	return simplify(get_items(c, inclusion=inclusion, exclusion=exclusion))


def get_items(c, inclusion=None, exclusion=None):
	"""Returns the items (values/entries/columns) of the specified collection whose keys
	(indices/keys/names) are in the specified inclusive list and are not in the specified exclusive
	list."""
	if is_null(c):
		return None
	elif is_empty(c):
		return []
	keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		if c.axis == 0:
			return [(k, filter(v, inclusion=keys)) for k, v in c]
		return [(k, v) for k, v in c if k in keys]
	return [(k, c[k]) for k in keys]


def get_value(c, inclusion=None, exclusion=None):
	return simplify(get_values(c, inclusion=inclusion, exclusion=exclusion))


def get_values(c, inclusion=None, exclusion=None):
	"""Returns the values (values/values/columns) of the specified collection whose keys
	(indices/keys/names) are in the specified inclusive list and are not in the specified exclusive
	list."""
	if is_null(c):
		return None
	elif is_empty(c):
		return to_array()
	elif is_number(c):
		return to_array(c)
	keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		if c.axis == 0:
			return to_array([filter(v, inclusion=keys).values for k, v in c])
		return to_array([v.values for k, v in c if k in keys])
	elif is_table(c):
		return filter(c, inclusion=keys).values
	elif is_array(c):
		return c
	return to_array([c[k] for k in keys])


##################################################

def set_names(c, new_names):
	"""Sets the names of the specified collection."""
	if is_null(c):
		return None
	elif is_empty(c):
		return c
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_table(new_names):
		new_names = get_names(new_names)
	else:
		new_names = to_list(new_names)
	if is_frame(c):
		c.columns = new_names
	elif is_series(c):
		c.name = simplify(new_names)
	else:
		set_keys(c, new_names)
	return c


def set_keys(c, new_keys, inclusion=None, exclusion=None):
	"""Sets the keys (indices/keys/names) of the specified collection that are in the specified
	inclusive list and are not in the specified exclusive list."""
	if is_null(c):
		return None
	elif is_empty(c):
		return c
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_table(new_keys):
		new_keys = get_keys(new_keys)
	else:
		new_keys = to_list(new_keys)
	keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_frame(c):
		c.loc[:, keys].columns = new_keys
	elif is_series(c):
		set_index(c, new_keys, inclusion=inclusion, exclusion=exclusion)
	elif is_dict(c):
		d = c.copy()
		for key, new_key in zip(keys, new_keys):
			d[new_key] = c.pop(key)
		update(c, d, inclusion=new_keys)
	else:
		l = c.copy()
		for key, new_key in zip(keys, new_keys):
			l[new_key] = c[key]
		update(c, l, inclusion=new_keys)
	return c


def set_index(c, new_index, inclusion=None, exclusion=None):
	"""Sets the index (indices/keys/index) of the specified collection that are in the specified
	inclusive list and are not in the specified exclusive list."""
	if is_null(c):
		return None
	elif is_empty(c):
		return c
	if is_group(c):
		c = c.groups if c.axis == 0 else c.obj
	if is_table(new_index):
		new_index = get_index(new_index)
	else:
		new_index = to_list(new_index)
	if is_table(c):
		if not is_empty(new_index) and is_tuple(new_index[0]):
			c.index = pd.MultiIndex.from_tuples(new_index)
		else:
			index = get_index(c, inclusion=inclusion, exclusion=exclusion)
			rename(c, index=dict(zip(index, new_index)))
	else:
		set_keys(c, new_index, inclusion=inclusion, exclusion=exclusion)
	return c


# • DATAFRAME ######################################################################################

__DATAFRAME_ACCESSORS_____________________________ = ''


def get_row(df, i=0):
	"""Returns the row of the specified dataframe at the specified index."""
	if is_group(df):
		df = get_values(df)
	elif is_table(df):
		return df.iloc[i:i + 1] if i != -1 else df.iloc[i:]
	return df[i]


def get_first_row(df):
	"""Returns the first row of the specified dataframe."""
	return get_row(df, 0)


def get_last_row(df):
	"""Returns the last row of the specified dataframe."""
	return get_row(df, -1)


#########################

def get_col(df, j=0):
	"""Returns the column of the specified dataframe at the specified index."""
	if is_group(df):
		df = get_values(df)
	elif is_frame(df):
		return df.iloc[:, j]
	elif is_series(df):
		return df.iloc[:]
	return df[:, j]


def get_first_col(df):
	"""Returns the first column of the specified dataframe."""
	return get_col(df, 0)


def get_last_col(df):
	"""Returns the last column of the specified dataframe."""
	return get_col(df, -1)


# • DATE ###########################################################################################

__DATE_ACCESSORS__________________________________ = ''


def get_date():
	return date.today()


def get_date_string():
	return format_date(get_date())


def get_datetime():
	return datetime.now()


def get_datetime_string(fmt=DEFAULT_DATE_TIME_FORMAT):
	return format_datetime(get_datetime(), fmt=fmt)


def get_time_string():
	return format_time(get_datetime())


def get_datestamp():
	return to_datestamp(get_date())


def get_timestamp():
	return to_timestamp(get_datetime())


def get_stamp():
	return to_stamp(get_datetime())


#########################

def get_day(d=get_datetime(), week=False, year=False):
	if is_string(d):
		d = parse_datetime(d)
	if is_date(d):
		return d.weekday() if week else d.timetuple().tm_yday if year else d.day
	return d


def get_days(c, week=False, year=False):
	if is_table(c):
		index = to_timestamp(get_index(c))
		return index.weekday if week else index.dayofyear if year else index.day
	elif is_dict(c):
		return get_days(get_index(c), week=week, year=year)
	return list_to_type([get_day(d, week=week, year=year) for d in c], c)


def get_week(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	if is_date(d):
		return d.isocalendar()[1]
	return d


def get_weeks(c):
	if is_table(c):
		return pd.Int64Index(to_timestamp(get_index(c)).isocalendar().week)
	elif is_dict(c):
		return get_weeks(get_index(c))
	return list_to_type([get_week(d) for d in c], c)


def get_year_week(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	if is_date(d):
		iso_cal = d.isocalendar()
		return iso_cal[0], iso_cal[1]
	return d


def get_year_weeks(c):
	if is_table(c):
		year_week = ['year', 'week']
		iso_cal = to_timestamp(get_index(c)).isocalendar()
		return pd.MultiIndex.from_frame(filter(iso_cal, inclusion=year_week), names=year_week)
	elif is_dict(c):
		return get_year_weeks(get_index(c))
	return list_to_type([get_year_week(d) for d in c], c)


def get_month(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	if is_date(d):
		return d.month
	return d


def get_months(c):
	if is_table(c):
		return to_timestamp(get_index(c)).month
	elif is_dict(c):
		return get_months(get_index(c))
	return list_to_type([get_month(d) for d in c], c)


def get_quarter(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	if is_date(d):
		return ceil(d.month / 3)
	return d


def get_quarters(c):
	if is_table(c):
		return to_timestamp(get_index(c)).quarter
	elif is_dict(c):
		return get_quarters(get_index(c))
	return list_to_type([get_quarter(d) for d in c], c)


def get_semester(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	if is_date(d):
		return ceil(d.month / 6)
	return d


def get_semesters(c):
	if is_table(c):
		return ceil(get_months(c) / 6)
	elif is_dict(c):
		return get_semesters(get_index(c))
	return list_to_type([get_semester(d) for d in c], c)


def get_year(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	if is_date(d):
		return d.year
	return d


def get_years(c):
	if is_table(c):
		return to_timestamp(get_index(c)).year
	elif is_dict(c):
		return get_years(get_index(c))
	return list_to_type([get_year(d) for d in c], c)


#########################

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
	if day is MO:  # Monday
		return d - 3 * DAY
	elif day is SU:  # Sunday
		return d - 2 * DAY
	return d - DAY


def get_next_business_day(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	day = date.weekday(d)
	if day is FR:  # Friday
		return d + 3 * DAY
	elif day is SA:  # Saturday
		return d + 2 * DAY
	return d + DAY


#########################

def get_month_range(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	return monthrange(d.year, d.month)


def get_month_weekday(year, month):
	return monthrange(year, month)[0]


def get_month_days(year, month):
	return monthrange(year, month)[1]


#########################

def get_month_start(d=get_datetime()):
	if is_collection(d):
		return apply(get_month_start, d)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(day=1))


def get_month_end(d=get_datetime()):
	if is_collection(d):
		return apply(get_month_end, d)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(day=get_month_days(d.year, d.month)))


def get_prev_month_start(d=get_datetime()):
	if is_collection(d):
		return apply(get_prev_month_start, d)
	elif is_string(d):
		d = parse_datetime(d)
	if d.month == 1:
		year = d.year - 1
		month = 12
	else:
		year = d.year
		month = d.month - 1
	return reset_time(d.replace(year=year, month=month, day=1))


def get_prev_month_end(d=get_datetime()):
	if is_collection(d):
		return apply(get_prev_month_end, d)
	elif is_string(d):
		d = parse_datetime(d)
	if d.month == 1:
		year = d.year - 1
		month = 12
	else:
		year = d.year
		month = d.month - 1
	return reset_time(d.replace(year=year, month=month, day=get_month_days(year, month)))


def get_next_month_start(d=get_datetime()):
	if is_collection(d):
		return apply(get_next_month_start, d)
	elif is_string(d):
		d = parse_datetime(d)
	if d.month == 12:
		year = d.year + 1
		month = 1
	else:
		year = d.year
		month = d.month + 1
	return reset_time(d.replace(year=year, month=month, day=1))


def get_next_month_end(d=get_datetime()):
	if is_collection(d):
		return apply(get_next_month_end, d)
	elif is_string(d):
		d = parse_datetime(d)
	if d.month == 12:
		year = d.year + 1
		month = 1
	else:
		year = d.year
		month = d.month + 1
	return reset_time(d.replace(year=year, month=month, day=get_month_days(year, month)))


#########################

def get_quarter_start(d=get_datetime()):
	if is_collection(d):
		return apply(get_quarter_start, d)
	elif is_string(d):
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
	if is_collection(d):
		return apply(get_quarter_end, d)
	elif is_string(d):
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
	if is_collection(d):
		return apply(get_prev_quarter_start, d)
	elif is_string(d):
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
	if is_collection(d):
		return apply(get_prev_quarter_end, d)
	elif is_string(d):
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
	if is_collection(d):
		return apply(get_next_quarter_start, d)
	elif is_string(d):
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
	if is_collection(d):
		return apply(get_next_quarter_end, d)
	elif is_string(d):
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


#########################

def get_semester_start(d=get_datetime()):
	if is_collection(d):
		return apply(get_semester_start, d)
	elif is_string(d):
		d = parse_datetime(d)
	if 1 <= d.month <= 6:
		month = 1
	else:
		month = 7
	return reset_time(d.replace(month=month, day=1))


def get_semester_end(d=get_datetime()):
	if is_collection(d):
		return apply(get_semester_end, d)
	elif is_string(d):
		d = parse_datetime(d)
	if 1 <= d.month <= 6:
		month = 6
	else:
		month = 12
	return reset_time(d.replace(month=month, day=get_month_days(d.year, month)))


def get_prev_semester_start(d=get_datetime()):
	if is_collection(d):
		return apply(get_prev_semester_start, d)
	elif is_string(d):
		d = parse_datetime(d)
	if 1 <= d.month <= 6:
		year = d.year - 1
		month = 7
	else:
		year = d.year
		month = 1
	return reset_time(d.replace(year=year, month=month, day=1))


def get_prev_semester_end(d=get_datetime()):
	if is_collection(d):
		return apply(get_prev_semester_end, d)
	elif is_string(d):
		d = parse_datetime(d)
	if 1 <= d.month <= 6:
		year = d.year - 1
		month = 12
	else:
		year = d.year
		month = 6
	return reset_time(d.replace(year=year, month=month, day=get_month_days(year, month)))


def get_next_semester_start(d=get_datetime()):
	if is_collection(d):
		return apply(get_next_semester_start, d)
	elif is_string(d):
		d = parse_datetime(d)
	if 1 <= d.month <= 6:
		year = d.year
		month = 7
	else:
		year = d.year + 1
		month = 1
	return reset_time(d.replace(year=year, month=month, day=1))


def get_next_semester_end(d=get_datetime()):
	if is_collection(d):
		return apply(get_next_semester_end, d)
	elif is_string(d):
		d = parse_datetime(d)
	if 1 <= d.month <= 6:
		year = d.year
		month = 12
	else:
		year = d.year + 1
		month = 6
	return reset_time(d.replace(year=year, month=month, day=get_month_days(year, month)))


#########################

def get_year_start(d=get_datetime()):
	if is_collection(d):
		return apply(get_year_start, d)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(month=1, day=1))


def get_year_end(d=get_datetime()):
	if is_collection(d):
		return apply(get_year_end, d)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(month=12, day=31))


def get_prev_year_start(d=get_datetime()):
	if is_collection(d):
		return apply(get_prev_year_start, d)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(year=d.year - 1, month=1, day=1))


def get_prev_year_end(d=get_datetime()):
	if is_collection(d):
		return apply(get_prev_year_end, d)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(year=d.year - 1, month=12, day=31))


def get_next_year_start(d=get_datetime()):
	if is_collection(d):
		return apply(get_next_year_start, d)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(year=d.year + 1, month=1, day=1))


def get_next_year_end(d=get_datetime()):
	if is_collection(d):
		return apply(get_next_year_end, d)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(year=d.year + 1, month=12, day=31))


#########################

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


#########################

def get_start_date(d, freq=FREQUENCY):
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


def get_end_date(d, freq=FREQUENCY):
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


def get_start_datetime(d, freq=FREQUENCY):
	return to_datetime(get_start_date(d, freq=freq))


def get_end_datetime(d, freq=FREQUENCY):
	return to_datetime(get_end_date(d, freq=freq))


def get_start_timestamp(d, freq=Frequency.DAYS):
	return to_timestamp(get_start_date(d, freq=freq))


def get_end_timestamp(d, freq=Frequency.DAYS):
	return to_timestamp(get_end_date(d, freq=freq))


#########################

def get_period_index(period=PERIOD):
	period_length = int(period[0:-1])
	period_freq = Frequency(period[-1].upper())
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


def get_period_length(d, period=PERIOD, freq=FREQUENCY):
	return diff_date(subtract_period(d, period), d, freq=freq)


def get_period_days(d, period=PERIOD):
	return diff_days(subtract_period(d, period), d)


def get_period_weeks(d, period=PERIOD):
	return diff_weeks(subtract_period(d, period), d)


def get_period_months(d, period=PERIOD):
	return diff_months(subtract_period(d, period), d)


def get_period_quarters(d, period=PERIOD):
	return diff_quarters(subtract_period(d, period), d)


def get_period_semesters(d, period=PERIOD):
	return diff_semesters(subtract_period(d, period), d)


def get_period_years(d, period=PERIOD):
	return diff_years(subtract_period(d, period), d)


####################################################################################################
# COMMON CONVERTERS
####################################################################################################

__COMMON_CONVERTERS_______________________________ = ''

# • ARRAY ##########################################################################################

__ARRAY_CONVERTERS________________________________ = ''


def to_array(*args):
	if len(args) == 1:
		arg = args[0]
		if is_array(arg):
			return arg
		elif is_collection(arg):
			return np.array(arg)
	return np.array(to_list(*args))


def unarray(a):
	if is_array(a):
		if len(a) == 1:
			return a[0]
		return tuple(a)
	return a


#########################

def array_to_type(a, x):
	"""Converts the specified array to the type of the specified variable."""
	if is_frame(x):
		return to_frame(a, names=get_names(x), index=get_index(x))
	elif is_series(x):
		return to_series(a, name=get_names(x), index=get_index(x))
	elif is_dict(x):
		return dict(zip(get_keys(x), a))
	elif is_array(x):
		return a
	return to_list(a)


# • DATAFRAME ######################################################################################

__DATAFRAME_CONVERTERS____________________________ = ''


def to_series(data, name=None, index=None):
	"""Converts the specified collection to a series."""
	if is_null(data):
		data = []
	elif is_group(data):
		data = data.obj
	if is_frame(data):
		if count_cols(data) > 1:
			return [to_series(data[k], name=name, index=index) for k in get_keys(data)]
		series = get_col(data) if not is_empty(data) else pd.Series()
	elif is_series(data):
		series = data
	else:
		series = pd.Series(data=data, dtype=float)
	if not is_null(index):
		set_index(series, index)
	if not is_null(name):
		set_names(series, name)
	return series


def to_time_series(data, name=None, index=None):
	"""Converts the specified collection to a time series."""
	if not is_null(index):
		index = to_timestamp(index)
	return to_series(data, name=name, index=index)


def to_frame(data, names=None, index=None):
	"""Converts the specified collection to a dataframe."""
	if is_null(data):
		data = []
	elif is_group(data):
		data = data.obj
	if is_frame(data):
		frame = data
	elif is_series(data):
		frame = data.to_frame()
	elif is_dict(data):
		frame = pd.DataFrame.from_dict(data)
	else:
		frame = pd.DataFrame(data=data)
	if not is_null(index):
		set_index(frame, index)
	if not is_null(names):
		set_names(frame, names)
	return frame


# • DATE ###########################################################################################

__DATE_CONVERTERS_________________________________ = ''


def to_date(x, fmt=DEFAULT_DATE_FORMAT):
	if is_null(x):
		return None
	elif is_collection(x):
		return apply(to_date, x, fmt=fmt)
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
	return datetime.strptime(x, fmt)


def to_datetime(x, fmt=DEFAULT_DATE_TIME_FORMAT):
	if is_null(x):
		return None
	elif is_collection(x):
		return apply(to_datetime, x, fmt=fmt)
	elif is_stamp(x):
		return parse_stamp(x)
	elif is_timestamp(x):
		return x.to_pydatetime()
	elif is_datetime(x):
		return x
	elif is_date(x):
		return create_datetime(x.year, x.month, x.day)
	return datetime.strptime(x, fmt)


def to_time(x, fmt=DEFAULT_TIME_FORMAT):
	if is_null(x):
		return None
	return to_datetime(x, fmt=fmt)


def to_datestamp(d):
	if is_null(d):
		return None
	elif is_stamp(d):
		d = parse_stamp(d)
	return pd.to_datetime(d).floor('D')


def to_timestamp(d):
	if is_null(d):
		return None
	elif is_stamp(d):
		d = parse_stamp(d)
	return pd.to_datetime(d)


def to_stamp(x):
	if is_null(x):
		return None
	elif is_collection(x):
		return apply(to_stamp, x)
	elif is_stamp(x):
		return x
	return to_datetime(x).timestamp()


#########################

def timestamp_to_type(t, x):
	"""Converts the specified timestamp to the type of the specified variable."""
	if is_collection(t):
		return apply(timestamp_to_type, t, x)
	elif is_stamp(x):
		return to_stamp(t)
	elif is_timestamp(x):
		return t
	elif is_datetime(x):
		return to_datetime(t)
	elif is_date(x):
		return to_date(t)
	return t


# • DICT ###########################################################################################

__DICT_CONVERTERS_________________________________ = ''


def to_dict(c):
	"""Converts the specified collection to a dictionary."""
	if is_null(c):
		return {}
	elif is_table(c):
		return c.to_dict()
	elif is_dict(c):
		return c
	return {k: v for k, v in enumerate(c)}


# • LIST ###########################################################################################

__LIST_CONVERTERS_________________________________ = ''


def to_list(*args):
	if len(args) == 1:
		arg = args[0]
		if is_list(arg):
			return arg
		elif is_collection(arg):
			return list(arg)
		return [arg]
	return list(args)


def unlist(l):
	if is_list(l):
		if len(l) == 1:
			return l[0]
		return tuple(l)
	return l


#########################

def list_to_type(l, x):
	if is_frame(x):
		return to_frame(l, names=get_names(x), index=get_index(x))
	elif is_series(x):
		return to_series(l, name=get_names(x), index=get_index(x))
	elif is_dict(x):
		return dict(zip(get_keys(x), l))
	elif is_array(x):
		return to_array(l)
	return l


# • NUMBER #########################################################################################

__NUMBER_CONVERTERS_______________________________ = ''


def to_bool(x):
	if is_null(x):
		return NAN
	elif is_collection(x):
		return apply(to_bool, x)
	return strtobool(str(x))


def to_int(x):
	if is_null(x):
		return NAN
	elif is_collection(x):
		return apply(to_int, x)
	return int(x)


def to_float(x):
	if is_null(x):
		return NAN
	elif is_collection(x):
		return apply(to_float, x)
	return float(x)


# • STRING #########################################################################################

__STRING_CONVERTERS_______________________________ = ''


def to_string(x, delimiter=','):
	if is_null(x):
		return None
	elif is_collection(x):
		return collapse(x, delimiter=delimiter)
	return str(x)


####################################################################################################
# COMMON GENERATORS
####################################################################################################

__COMMON_GENERATORS_______________________________ = ''

# • DATE ###########################################################################################

__DATE_GENERATORS_________________________________ = ''


def create_date(y, m, d):
	return date(int(y), int(m), int(d))


def create_datetime(y, m, d):
	return datetime(int(y), int(m), int(d))


def create_timestamp(y, m, d):
	return pd.Timestamp(int(y), int(m), int(d))


def create_stamp(y, m, d):
	return to_stamp(create_datetime(y, m, d))


#########################

def create_date_sequence(date_from, date_to, periods=None, freq=FREQUENCY):
	return to_date(pd.date_range(date_from, date_to, periods=periods, freq=freq.value))


def create_datetime_sequence(date_from, date_to, periods=None, freq=FREQUENCY):
	return to_datetime(pd.date_range(date_from, date_to, periods=periods, freq=freq.value))


def create_timestamp_sequence(date_from, date_to, periods=None, freq=FREQUENCY):
	return to_timestamp(pd.date_range(date_from, date_to, periods=periods, freq=freq.value))


def create_stamp_sequence(date_from, date_to, periods=None, freq=FREQUENCY):
	return to_stamp(pd.date_range(date_from, date_to, periods=periods, freq=freq.value))


# • NUMBER #########################################################################################

__NUMBER_GENERATORS_______________________________ = ''


def create_sequence(start=0, stop=0, step=1, include=False, n=None):
	if start == stop:
		if include:
			return start
		return to_array()
	elif start > stop:
		start, stop = stop, start
	if not is_null(n):
		if n <= 1:
			return start
		step = (stop - start) / (n if not include else n - 1)
	sequence = np.arange(start, stop, step)
	if include:
		sequence = np.append(sequence, stop)
	return sequence


# • STRING #########################################################################################

__STRING_GENERATORS_______________________________ = ''


def generate_string(length, case_sensitive=False, digits=True):
	"""Generates a pseudorandom, uniformly distributed string of the specified length."""
	choices = string.ascii_uppercase
	if case_sensitive:
		choices += string.ascii_lowercase
	if digits:
		choices += string.digits
	return collapse(random.choices(choices, k=length))


####################################################################################################
# COMMON PROCESSORS
####################################################################################################

__COMMON_PROCESSORS_______________________________ = ''


def apply(f, x, *args, axis=None, inplace=False, inclusion=None, exclusion=None, **kwargs):
	"""Applies the specified function iteratively over the specified value along the specified axis
	(over the rows, columns or elements if the specified axis is respectively zero, one or null)
	with the specified arguments."""
	if is_collection(x):
		keys = get_keys(x, inclusion=inclusion, exclusion=exclusion)
		if inplace:
			if is_frame(x):
				chained_assignment = pd.options.mode.chained_assignment
				pd.options.mode.chained_assignment = None
				for k in keys:
					apply(f, x.loc[:, k], *args, axis=axis, inplace=inplace, **kwargs)
				pd.options.mode.chained_assignment = chained_assignment
			elif is_series(x):
				chained_assignment = pd.options.mode.chained_assignment
				pd.options.mode.chained_assignment = None
				x.loc[keys] = x.loc[keys].apply(f, args=args, **kwargs)
				pd.options.mode.chained_assignment = chained_assignment
			else:
				for k in keys:
					x[k] = f(x[k], *args, **kwargs)
			return x
		if is_group(x):
			axis = x.axis
			if axis == 0:
				names = get_names(x, inclusion=keys)
				return concat_rows([to_frame([f(get_values(v, inclusion=keys), *args, **kwargs)],
				                             names=names, index=i) for i, v in x])
			index = get_index(x)
			return concat_cols([to_series(f(get_values(v, inclusion=keys), *args, **kwargs),
			                              name=k, index=index) for k, v in x if k in keys])
		elif is_frame(x):
			if is_null(axis):
				return concat_cols([x[k].apply(f, args=args, **kwargs) for k in keys])
			index = get_names(x, inclusion=keys) if axis == 0 else get_index(x)
			data = f(get_values(x, inclusion=keys), *args, **kwargs)
			if count_cols(data) > 1:
				names = get_index(x) if axis == 0 else get_names(x, inclusion=keys)
				return to_frame(data, names=names, index=index)
			return to_series(data, name=f.__name__, index=index)
		elif is_series(x):
			return x.loc[keys].apply(f, args=args, **kwargs)
		elif is_dict(x):
			return {k: f(x[k], *args, **kwargs) for k in keys}
		return list_to_type([f(x[k], *args, **kwargs) for k in keys], x)
	elif is_string(x):
		return collapse([f(c, *args, **kwargs) for c in x])
	return f(x, *args, **kwargs)


def fill_with(x, value, *args, condition=lambda x: True, inplace=False, **kwargs):
	return apply(lambda x: value if condition(x, *args, **kwargs) else x, x, inplace=inplace)


def fill_null_with(x, value, inplace=False):
	return fill_with(x, value, condition=is_null, inplace=inplace)


#########################

def browser():
	pdb.set_trace()


#########################

def clear():
	sys.modules[__name__].__dict__.clear()


#########################

def forward(*args):
	if len(args) == 1:
		return args[0]
	return list(args)


#########################

def invert(x):
	return np.logical_not(x)


def reduce_and(x, axis=0):
	"""Reduces the dimension of the specified arguments by applying the logical AND function
	cumulatively along the specified axis (over the rows or columns if the specified axis is
	respectively zero or one)."""
	if axis == 1 and count_cols(x) == 0:
		return to_array(x)
	return np.logical_and.reduce(x, axis=axis)


def reduce_or(x, axis=0):
	"""Reduces the dimension of the specified arguments by applying the logical OR function
	cumulatively along the specified axis (over the rows or columns if the specified axis is
	respectively zero or one)."""
	if axis == 1 and count_cols(x) == 0:
		return to_array(x)
	return np.logical_or.reduce(x, axis=axis)


#########################

def reduce(f, *args, initial=None, **kwargs):
	"""Reduces the specified arguments to a single one by applying the specified function
	cumulatively (from left to right)."""
	args = remove_empty(to_list(*args))
	if is_empty(args):
		return initial
	if not is_null(initial):
		return functools.reduce(lambda x, y: f(x, y, **kwargs), args, initial=initial)
	return functools.reduce(lambda x, y: f(x, y, **kwargs), args)


# • COLLECTION (LIST/DICT/DATAFRAME) ###############################################################

__COLLECTION_PROCESSORS___________________________ = ''


def all_values(c):
	return np.all(get_values(c))


def all_not_values(c):
	return np.all(invert(get_values(c)))


def any_values(c):
	return np.any(get_values(c))


def any_not_values(c):
	return np.any(invert(get_values(c)))


#########################

def calculate(c, f, *args, axis=0, **kwargs):
	if is_group(c):
		axis = c.axis
		if axis == 0:
			names = get_names(c)
			return concat_rows([to_frame([f(v.values, *args, axis=axis, **kwargs)],
			                             names=names, index=i) for i, v in c])
		index = get_index(c)
		return concat_cols([to_series(f(v.values, *args, axis=axis, **kwargs),
		                              name=k, index=index) for k, v in c])
	elif is_frame(c):
		index = get_names(c) if axis == 0 else get_index(c)
		return to_series(f(c.values, *args, axis=axis, **kwargs), index=index)
	return f(get_values(c), *args, axis=axis, **kwargs)


#########################

def concat_all(*args):
	return reduce(concat, *args)


def concat(c1, c2):
	"""Concatenates the specified collections."""
	if is_table(c1) or is_table(c2):
		return concat_rows(c1, c2)
	elif is_dict(c1) or is_dict(c2):
		return dict(get_items(c1) + get_items(c2))
	return to_list(c1) + to_list(c2)


#########################

def fill_null(c, numeric_default=None, object_default=None, inclusion=None, exclusion=None):
	keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	for k in keys:
		if is_frame(c):
			col = c.loc[:, k]
			if is_numeric_dtype(col.dtypes):
				fill_null_with(col, numeric_default, inplace=True)
			else:
				fill_null_with(col, object_default, inplace=True)
		else:
			if is_null(c[k]):
				if is_number(c[k]):
					c[k] = numeric_default
				else:
					c[k] = object_default
	return c


#########################

def filter(c, inclusion=None, exclusion=None):
	"""Filters the specified collection by excluding the keys that are not in the specified
	inclusive collection and are in the specified exclusive collection."""
	if is_null(c):
		return None
	elif is_empty(c):
		return c
	keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		keys = get_index(c, inclusion=inclusion, exclusion=exclusion) if c.axis == 0 else keys
		return c.filter(lambda x: x.name in keys)
	elif is_frame(c):
		return c.loc[:, keys]
	elif is_series(c):
		return c[c.index.isin(keys)]
	elif is_dict(c):
		return {k: c[k] for k in keys}
	return list_to_type([c[k] for k in keys], c)


def include(c, inclusion):
	"""Filters the specified collection by excluding the keys that are not in the specified
	inclusive collection."""
	return filter(c, inclusion=inclusion)


def exclude(c, exclusion):
	"""Filters the specified collection by excluding the keys that are in the specified exclusive
	collection."""
	return filter(c, exclusion=exclusion)


#########################

def filter_index(c, inclusion=None, exclusion=None):
	"""Filters the specified collection by excluding the index that are not in the specified
	inclusive collection and are in the specified exclusive collection."""
	if is_null(c):
		return None
	elif is_empty(c):
		return c
	index = get_index(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		index = index if c.axis == 0 else get_keys(c, inclusion=inclusion, exclusion=exclusion)
		return c.filter(lambda x: x.name in index)
	elif is_table(c):
		return c[c.index.isin(index)]
	return filter(c, inclusion=inclusion, exclusion=exclusion)


def include_index(c, inclusion):
	"""Filters the specified collection by excluding the index that are not in the specified
	inclusive collection."""
	return filter_index(c, inclusion=inclusion)


def exclude_index(c, exclusion):
	"""Filters the specified collection by excluding the index that are in the specified exclusive
	collection."""
	return filter_index(c, exclusion=exclusion)


#########################

def filter_with(c, f, *args, inclusion=None, exclusion=None, **kwargs):
	"""Returns the entries of the specified collection whose values return True with the specified
	function for all the specified keys."""
	if is_null(c):
		return None
	elif is_empty(c):
		return c
	keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		keys = get_index(c, inclusion=inclusion, exclusion=exclusion) if c.axis == 0 else keys
		return c.filter(lambda x: x.name in keys and all_values(apply(f, x, *args, **kwargs)))
	elif is_table(c):
		mask = get_values(apply(f, c, *args, inclusion=keys, **kwargs))
		if is_series(c):
			return c[mask_list(keys, mask)]
		return c[reduce_and(mask, axis=1)]
	elif is_dict(c):
		return {k: c[k] for k in keys if f(c[k], *args, **kwargs)}
	return list_to_type([c[k] for k in keys if f(c[k], *args, **kwargs)], c)


def filter_not_with(c, f, *args, inclusion=None, exclusion=None, **kwargs):
	"""Returns the entries of the specified collection whose values return False with the specified
	function for all the specified keys."""
	if is_null(c):
		return None
	elif is_empty(c):
		return c
	keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		keys = get_index(c, inclusion=inclusion, exclusion=exclusion) if c.axis == 0 else keys
		return c.filter(lambda x: x.name in keys and all_not_values(apply(f, x, *args, **kwargs)))
	elif is_table(c):
		mask = invert(get_values(apply(f, c, *args, inclusion=keys, **kwargs)))
		if is_series(c):
			return c[mask_list(keys, mask)]
		return c[reduce_and(mask, axis=1)]
	elif is_dict(c):
		return {k: c[k] for k in keys if not f(c[k], *args, **kwargs)}
	return list_to_type([c[k] for k in keys if not f(c[k], *args, **kwargs)], c)


def filter_any_with(c, f, *args, inclusion=None, exclusion=None, **kwargs):
	"""Returns the entries of the specified collection whose values return True with the specified
	function for at least one specified key."""
	if is_null(c):
		return None
	elif is_empty(c):
		return c
	keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		keys = get_index(c, inclusion=inclusion, exclusion=exclusion) if c.axis == 0 else keys
		return c.filter(lambda x: x.name in keys and any_values(apply(f, x, *args, **kwargs)))
	elif is_table(c):
		mask = get_values(apply(f, c, *args, inclusion=keys, **kwargs))
		if is_series(c):
			return c[mask_list(keys, mask)]
		return c[reduce_or(mask, axis=1)]
	elif is_dict(c):
		return {k: c[k] for k in keys if f(c[k], *args, **kwargs)}
	return list_to_type([c[k] for k in keys if f(c[k], *args, **kwargs)], c)


def filter_any_not_with(c, f, *args, inclusion=None, exclusion=None, **kwargs):
	"""Returns the entries of the specified collection whose values return False with the specified
	function for at least one specified key."""
	if is_null(c):
		return None
	elif is_empty(c):
		return c
	keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		keys = get_index(c, inclusion=inclusion, exclusion=exclusion) if c.axis == 0 else keys
		return c.filter(lambda x: x.name in keys and any_not_values(apply(f, x, *args, **kwargs)))
	elif is_table(c):
		mask = invert(get_values(apply(f, c, *args, inclusion=keys, **kwargs)))
		if is_series(c):
			return c[mask_list(keys, mask)]
		return c[reduce_or(mask, axis=1)]
	elif is_dict(c):
		return {k: c[k] for k in keys if not f(c[k], *args, **kwargs)}
	return list_to_type([c[k] for k in keys if not f(c[k], *args, **kwargs)], c)


#########################

def filter_null(c, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are null for all the specified
	keys."""
	return filter_with(c, is_null, inclusion=inclusion, exclusion=exclusion)


def filter_not_null(c, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are not null for all the
	specified keys."""
	return filter_not_with(c, is_null, inclusion=inclusion, exclusion=exclusion)


def filter_any_null(c, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are null for at least one
	specified key."""
	return filter_any_with(c, is_null, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_null(c, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are not null for at least one
	specified key."""
	return filter_any_not_with(c, is_null, inclusion=inclusion, exclusion=exclusion)


#########################

def filter_empty(c, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are empty for all the specified
	keys."""
	return filter_with(c, is_empty, inclusion=inclusion, exclusion=exclusion)


def filter_not_empty(c, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are not empty for all the
	specified keys."""
	return filter_not_with(c, is_empty, inclusion=inclusion, exclusion=exclusion)


def filter_any_empty(c, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are empty for at least one
	specified key."""
	return filter_any_with(c, is_empty, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_empty(c, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are not empty for at least one
	specified key."""
	return filter_any_not_with(c, is_empty, inclusion=inclusion, exclusion=exclusion)


#########################

def filter_value(c, value, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are equal to the specified value
	 for all the specified keys."""
	return filter_with(c, lambda v: v == value, inclusion=inclusion, exclusion=exclusion)


def filter_not_value(c, value, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are not equal to the specified
	value for all the specified keys."""
	return filter_not_with(c, lambda v: v == value, inclusion=inclusion, exclusion=exclusion)


def filter_any_value(c, value, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are equal to the specified value
	for at least one specified key."""
	return filter_any_with(c, lambda v: v == value, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_value(c, value, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are not equal to the specified
	value for at least one specified key."""
	return filter_any_not_with(c, lambda v: v == value, inclusion=inclusion, exclusion=exclusion)


#########################

def filter_in(c, values, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are in the specified values for
	all the specified keys."""
	return filter_with(c, lambda v: v in to_list(values), inclusion=inclusion, exclusion=exclusion)


def filter_not_in(c, values, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are not in the specified values
	for all the specified keys."""
	return filter_not_with(c, lambda v: v in to_list(values), inclusion=inclusion, exclusion=exclusion)


def filter_any_in(c, values, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are in the specified values for
	at least one specified key."""
	return filter_any_with(c, lambda v: v in to_list(values), inclusion=inclusion, exclusion=exclusion)


def filter_any_not_in(c, values, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are not in the specified values
	for at least one specified key."""
	return filter_any_not_with(c, lambda v: v in to_list(values), inclusion=inclusion, exclusion=exclusion)


#########################

def filter_between(c, lower=None, upper=None, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are lying between the lower
	(inclusive) and upper (exclusive) bounds for all the specified keys."""
	if is_all_null(lower, upper):
		return c
	elif is_null(lower):
		return filter_with(c, lambda v: v < upper, inclusion=inclusion, exclusion=exclusion)
	elif is_null(upper):
		return filter_with(c, lambda v: v >= lower, inclusion=inclusion, exclusion=exclusion)
	return filter_with(c, lambda v: lower <= v < upper, inclusion=inclusion, exclusion=exclusion)


def filter_not_between(c, lower=None, upper=None, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are not lying between the lower
	(inclusive) and upper (exclusive) bounds for all the specified keys."""
	if is_all_null(lower, upper):
		return c
	elif is_null(lower):
		return filter_not_with(c, lambda v: v < upper, inclusion=inclusion, exclusion=exclusion)
	elif is_null(upper):
		return filter_not_with(c, lambda v: v >= lower, inclusion=inclusion, exclusion=exclusion)
	return filter_not_with(c, lambda v: lower <= v < upper, inclusion=inclusion, exclusion=exclusion)


def filter_any_between(c, lower=None, upper=None, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are lying between the lower
	(inclusive) and upper (exclusive) bounds for at least one specified key."""
	if is_all_null(lower, upper):
		return c
	elif is_null(lower):
		return filter_any_with(c, lambda v: v < upper, inclusion=inclusion, exclusion=exclusion)
	elif is_null(upper):
		return filter_any_with(c, lambda v: v >= lower, inclusion=inclusion, exclusion=exclusion)
	return filter_any_with(c, lambda v: lower <= v < upper, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_between(c, lower=None, upper=None, inclusion=None, exclusion=None):
	"""Returns the entries of the specified collection whose values are not lying between the lower
	(inclusive) and upper (exclusive) bounds for at least one specified key."""
	if is_all_null(lower, upper):
		return c
	elif is_null(lower):
		return filter_any_not_with(c, lambda v: v < upper, inclusion=inclusion, exclusion=exclusion)
	elif is_null(upper):
		return filter_any_not_with(c, lambda v: v >= lower, inclusion=inclusion, exclusion=exclusion)
	return filter_any_not_with(c, lambda v: lower <= v < upper, inclusion=inclusion, exclusion=exclusion)


#########################

def filter_days(c, days, week=False, year=False):
	"""Filters the collection by matching its date-time index with the specified days (week days
	if week is True, days of the year if year is True, days of the month otherwise)."""
	indices = find_all_in(get_days(c, week=week, year=year), get_days(days, week=week, year=year))
	return take_at(c, indices)


def filter_weeks(c, weeks):
	"""Filters the collection by matching its date-time index with the specified weeks."""
	indices = find_all_in(get_weeks(c), get_weeks(weeks))
	return take_at(c, indices)


def filter_year_weeks(c, year_weeks):
	"""Filters the collection by matching its date-time index with the specified year-weeks."""
	indices = find_all_in(get_year_weeks(c), get_year_weeks(year_weeks))
	return take_at(c, indices)


def filter_months(c, months):
	"""Filters the collection by matching its date-time index with the specified months."""
	indices = find_all_in(get_months(c), get_months(months))
	return take_at(c, indices)


def filter_quarters(c, quarters):
	"""Filters the collection by matching its date-time index with the specified quarters."""
	indices = find_all_in(get_quarters(c), get_quarters(quarters))
	return take_at(c, indices)


def filter_semesters(c, semesters):
	"""Filters the collection by matching its date-time index with the specified semesters."""
	indices = find_all_in(get_semesters(c), get_semesters(semesters))
	return take_at(c, indices)


def filter_years(c, years):
	"""Filters the collection by matching its date-time index with the specified years."""
	indices = find_all_in(get_years(c), get_years(years))
	return take_at(c, indices)


#########################

def flatten(c, axis=0):
	return get_values(c).flatten(order='C' if axis == 0 else 'F' if axis == 1 else 'A')


#########################

def groupby(c, group=GROUP, dof=1, axis=0):
	if group is Group.COUNT:
		return count(c, axis=axis)
	elif group is Group.FIRST:
		return get_first(c, axis=axis)
	elif group is Group.LAST:
		return get_last(c, axis=axis)
	elif group is Group.MIN:
		return min(c, axis=axis)
	elif group is Group.MAX:
		return max(c, axis=axis)
	elif group is Group.MEAN:
		return mean(c, axis=axis)
	elif group is Group.MEDIAN:
		return median(c, axis=axis)
	elif group is Group.STD:
		return std(c, axis=axis, dof=dof)
	elif group is Group.VAR:
		return var(c, axis=axis, dof=dof)
	elif group is Group.SUM:
		return sum(c, axis=axis)


def count(*args, axis=0):
	c = forward(*args)
	if is_group(c):
		return c.count()
	if is_null(axis):
		return np.size(get_values(c))
	if is_frame(c):
		return c.count(axis=axis)
	elif is_array(c):
		return np.apply_along_axis(len, axis, c)
	return len(c)


def min(*args, axis=0):
	c = forward(*args)
	if is_group(c):
		return c.min()
	if is_null(axis) or is_dict(c):
		c = get_values(c)
	return np.min(c, axis=axis)


def max(*args, axis=0):
	c = forward(*args)
	if is_group(c):
		return c.max()
	if is_null(axis) or is_dict(c):
		c = get_values(c)
	return np.max(c, axis=axis)


def mean(*args, axis=0):
	c = forward(*args)
	if is_group(c):
		return c.mean()
	if is_null(axis) or is_dict(c):
		c = get_values(c)
	return np.mean(c, axis=axis)


def median(*args, axis=0):
	c = forward(*args)
	if is_group(c):
		return c.median()
	if is_null(axis) or is_dict(c):
		c = get_values(c)
	return np.median(c, axis=axis)


def std(*args, dof=1, axis=0):
	c = forward(*args)
	if is_group(c):
		return c.std(ddof=dof)
	if is_null(axis) or is_dict(c):
		c = get_values(c)
	return np.std(c, axis=axis, ddof=dof)


def var(*args, dof=1, axis=0):
	c = forward(*args)
	if is_group(c):
		return c.var(ddof=dof)
	if is_null(axis) or is_dict(c):
		c = get_values(c)
	return np.var(c, axis=axis, ddof=dof)


def sum(*args, axis=0):
	c = forward(*args)
	if is_group(c):
		return c.sum()
	if is_null(axis) or is_dict(c):
		c = get_values(c)
	return np.sum(c, axis=axis)


#########################

def reverse(c, axis=0):
	if is_frame(c):
		if axis == 0:
			return c.loc[:, ::-1]
		return c.loc[::-1]
	elif is_series(c):
		return c.loc[::-1]
	elif is_dict(c):
		return {v: k for k, v in get_items(c)}
	return c[::-1]


#########################

def keep_min(c, n, group=GROUP, axis=0):
	g = groupby(c, group=group, axis=axis) if not is_group(c) and not is_null(group) else c
	if is_number(g):
		return g
	keys = [t[1] for t in sorted(zip(g, get_keys(g) if axis == 0 else get_index(g)))[:n]]
	return take(c, keys, axis=1 if axis == 0 else 0)


def keep_max(c, n, group=GROUP, axis=0):
	g = groupby(c, group=group, axis=axis) if not is_group(c) and not is_null(group) else c
	if is_number(g):
		return g
	keys = [t[1] for t in sorted(zip(g, get_keys(g) if axis == 0 else get_index(g)))[-n:]]
	return take(c, keys, axis=1 if axis == 0 else 0)


#########################

def remove(c, value, conservative=True, inclusion=None, exclusion=None):
	if conservative:
		return filter_any_not_value(c, value, inclusion=inclusion, exclusion=exclusion)
	return filter_not_value(c, value, inclusion=inclusion, exclusion=exclusion)


def remove_null(c, axis=0, conservative=True, inclusion=None, exclusion=None):
	if axis == 0:
		if conservative:
			return filter_any_not_null(c, inclusion=inclusion, exclusion=exclusion)
		return filter_not_null(c, inclusion=inclusion, exclusion=exclusion)
	keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	for k in keys:
		if is_all_null(c[k]) if conservative else is_any_null(c[k]):
			c = remove_col(c, names=k)
	return c


def remove_empty(c, conservative=True, inclusion=None, exclusion=None):
	if conservative:
		return filter_any_not_empty(c, inclusion=inclusion, exclusion=exclusion)
	return filter_not_empty(c, inclusion=inclusion, exclusion=exclusion)


#########################

def shift_dates(c, years=0, months=0, weeks=0, days=0, hours=0, minutes=0, seconds=0,
                microseconds=0):
	"""Shifts the date-time index of the specified collection."""
	if is_table(c):
		c = c.copy()
		c.index += pd.DateOffset(years=years, months=months, weeks=weeks, days=days, hours=hours,
		                         minutes=minutes, seconds=seconds, microseconds=microseconds)
		return c
	elif is_dict(c):
		c = {}
		for k, v in get_items(c):
			c[shift_date(k, years=years, months=months, weeks=weeks, days=days, hours=hours,
			             minutes=minutes, seconds=seconds, microseconds=microseconds)] = v
		return c
	return list_to_type([shift_date(d, years=years, months=months, weeks=weeks, days=days,
	                                hours=hours, minutes=minutes, seconds=seconds,
	                                microseconds=microseconds) for d in c], c)


#########################

def simplify(c):
	if is_collection(c):
		keys = get_keys(c)
		if len(keys) == 1:
			return simplify(c[keys[0]])
	return c


#########################

def sort(c, ascending=True, by=None, axis=0):
	"""Sorts the values of the specified collection."""
	if is_frame(c):
		return c.sort_values(get_keys(c, inclusion=by), ascending=ascending, axis=axis)
	elif is_series(c):
		return c.sort_values(ascending=ascending)
	elif is_dict(c):
		return c
	c.sort()
	return c


def sort_index(c):
	"""Sorts the index of the specified collection."""
	if is_table(c):
		return c.sort_index()
	return c


#########################

def take(c, keys, axis=0):
	"""Returns the entries of the specified collection for all the specified keys."""
	keys = to_list(keys)
	if is_table(c):
		if axis == 0:
			return c.loc[keys]
		return c.loc[:, keys]
	elif is_dict(c):
		return {k: c[k] for k in keys}
	return list_to_type([c[k] for k in keys], c)


def take_not(c, keys, axis=0):
	"""Returns the entries of the specified collection except for all the specified keys."""
	indices = find_all_not_in(get_index(c) if axis == 0 else get_keys(c), keys)
	return take_at(c, indices, axis=axis)


def take_at(c, indices, axis=0):
	"""Returns the entries of the specified collection that are at the specified indices."""
	indices = to_list(indices)
	if is_table(c):
		if axis == 0:
			return c.iloc[indices]
		return c.iloc[:, indices]
	elif is_dict(c):
		return {k: c[k] for i, k in enumerate(c) if i in indices or i - len(c) in indices}
	return list_to_type([c[i] for i in indices], c)


def take_not_at(c, indices, axis=0):
	"""Returns the entries of the specified collection that are not at the specified indices."""
	indices = find_all_not_in(range(count_rows(c) if axis == 0 else count_cols(c)), indices)
	return take_at(c, indices, axis=axis)


#########################

def unique(c, keep='first'):
	if is_table(c):
		return c[invert(c.index.duplicated(keep=keep))]
	elif is_dict(c):
		return c
	return to_list(dict.fromkeys(c))


#########################

def update_all(*args, inclusion=None, exclusion=None):
	return reduce(lambda left, right: update(left, right, inclusion=inclusion, exclusion=exclusion),
	              *args)


def update(left, right, inclusion=None, exclusion=None):
	"""Updates the specified left collection with the specified right collection by overriding the
	left values with the right values on the common keys that are in the specified inclusive list
	and are not in the specified exclusive list."""
	keys = get_keys(right, inclusion=inclusion, exclusion=exclusion)
	if is_frame(left):
		left.update(filter(to_frame(right), keys))
	elif is_series(left):
		left.update(filter(to_series(right), keys))
	elif is_dict(left):
		left.update(filter(to_dict(right), keys))
	else:
		for k in keys:
			left[k] = right[k]
	return left


# • CONSOLE ########################################################################################

__CONSOLE_PROCESSORS______________________________ = ''


def trace(*args):
	if SEVERITY_LEVEL.value >= 7:
		print(collapse('[', get_datetime_string(), '][TRAC] ', paste(*args)))


def debug(*args):
	if SEVERITY_LEVEL.value >= 6:
		print(collapse('[', get_datetime_string(), '][DEBU] ', paste(*args)))


def test(*args):
	if SEVERITY_LEVEL.value >= 5:
		print(collapse('[', get_datetime_string(), '][TEST] ', paste(*args)))


def info(*args):
	if SEVERITY_LEVEL.value >= 4:
		print(collapse('[', get_datetime_string(), '][INFO] ', paste(*args)))


def result(*args):
	if SEVERITY_LEVEL.value >= 3:
		print(paste(*args))


def warn(*args):
	if SEVERITY_LEVEL.value >= 2:
		print(collapse('[', get_datetime_string(), '][WARN] ', paste(*args)), file=sys.stderr)


def error(*args):
	if SEVERITY_LEVEL.value >= 1:
		print(collapse('[', get_datetime_string(), '][ERRO] ', paste(*args)), file=sys.stderr)


def fail(*args):
	if SEVERITY_LEVEL.value >= 0:
		print(collapse('[', get_datetime_string(), '][FAIL] ', paste(*args)), file=sys.stderr)


# • DATAFRAME ######################################################################################

__DATAFRAME_PROCESSORS____________________________ = ''


def combine_all(*args, f):
	return reduce(lambda left, right: combine(left, right, f), *args)


def combine(left, right, f):
	"""Combines the specified left dataframe with the specified right dataframe with the specified
	function on the common columns (or on the specified columns if they are not null)."""
	return to_frame(left).combine(to_frame(right), f)


#########################

def concat_rows(*rows):
	"""Concatenates the specified rows to a dataframe."""
	rows = remove_empty(to_list(*rows))
	if is_empty(rows):
		return to_series(rows)
	df = pd.concat([to_frame(row) for row in rows], axis=0)
	if count_cols(df) == 1:
		return to_series(df)
	return df


def concat_cols(*cols):
	"""Concatenates the specified columns to a dataframe."""
	cols = remove_empty(to_list(*cols))
	if is_empty(cols):
		return to_series(cols)
	df = pd.concat(cols, axis=1)
	if count_cols(df) == 1:
		return to_series(df)
	return df


#########################

def count_rows(df):
	"""Counts the rows of the specified dataframe."""
	if is_group(df):
		if df.axis == 0:
			return len(df.groups)
		df = df.obj
	if is_series(df):
		return len(df)
	shape = np.shape(df)
	return shape[0] if len(shape) >= 1 else 0


def count_cols(df):
	"""Counts the columns of the specified dataframe."""
	if is_group(df):
		if df.axis == 1:
			return len(df.groups)
		df = df.obj
	if is_series(df):
		return 1
	shape = np.shape(df)
	return shape[1] if len(shape) >= 2 else 0


#########################

def fill_null_all(df, model, numeric_default=None, object_default=None):
	if is_series(df):
		return fill_null_rows(df, get_index(model), numeric_default=numeric_default,
		                      object_default=object_default)
	return fill_null(df.reindex(columns=unique(get_names(df) + get_names(model)),
	                            index=unique(get_index(df) + get_index(model))),
	                 numeric_default=numeric_default, object_default=object_default)


def fill_null_rows(df, index, numeric_default=None, object_default=None):
	if is_table(index):
		index = get_index(index)
	return fill_null(df.reindex(index=unique(get_index(df) + to_list(index))),
	                 numeric_default=numeric_default, object_default=object_default)


def fill_null_cols(df, names, numeric_default=None, object_default=None):
	if is_table(names):
		names = get_names(names)
	return fill_null(df.reindex(columns=unique(get_names(df) + to_list(names))),
	                 numeric_default=numeric_default, object_default=object_default)


#########################

def filter_rows(df, row):
	"""Returns the rows of the specified dataframe that match the specified row for all the common
	columns."""
	if is_null(row):
		return df
	return df[reduce_and([df[k] == v for k, v in get_items(row) if k in df])]


def filter_rows_not(df, row):
	"""Returns the rows of the specified dataframe that do not match the specified row for all the
	common columns."""
	if is_null(row):
		return df
	return df[reduce_and([df[k] != v for k, v in get_items(row) if k in df])]


def filter_any_rows(df, row):
	"""Returns the rows of the specified dataframe that match the specified row for at least one
	common column."""
	if is_null(row):
		return df
	return df[reduce_or([df[k] == v for k, v in get_items(row) if k in df])]


def filter_any_rows_not(df, row):
	"""Returns the rows of the specified dataframe that do not match the specified row for at least
	one common column."""
	if is_null(row):
		return df
	return df[reduce_or([df[k] != v for k, v in get_items(row) if k in df])]


#########################

def filter_rows_in(df, rows):
	"""Returns the rows of the specified dataframe that match the specified rows for all the common
	columns."""
	if is_null(rows):
		return df
	return df[reduce_and([df[k].isin(values) for k, values in get_items(rows) if k in df])]


def filter_rows_not_in(df, rows):
	"""Returns the rows of the specified dataframe that do not match the specified rows for all the
	common columns."""
	if is_null(rows):
		return df
	return df[reduce_and([invert(df[k].isin(values)) for k, values in get_items(rows) if k in df])]


def filter_any_rows_in(df, rows):
	"""Returns the rows of the specified dataframe that match the specified rows for at least one
	common column."""
	if is_null(rows):
		return df
	return df[reduce_or([df[k].isin(values) for k, values in get_items(rows) if k in df])]


def filter_any_rows_not_in(df, rows):
	"""Returns the rows of the specified dataframe that do not match the specified rows for at least
	one common column."""
	if is_null(rows):
		return df
	return df[reduce_or([invert(df[k].isin(values)) for k, values in get_items(rows) if k in df])]


#########################

def join_all(*args, how='inner', on=None, suffix=' 2'):
	return reduce(lambda left, right: join(left, right, how=how, on=on, suffix=suffix), *args)


def join(left, right, how='inner', on=None, suffix=' 2'):
	"""Joins the specified left dataframe with the specified right dataframe on the common columns
	(or on the specified columns if they are not null)."""
	return to_frame(left).join(to_frame(right), how=how, on=on, rsuffix=suffix)


#########################

def merge_all(*args, how='inner', on=None):
	return reduce(lambda left, right: merge(left, right, how=how, on=on), *args)


def merge(left, right, how='inner', on=None):
	"""Merges the specified left dataframe with the specified right dataframe on the common columns
	(or on the specified columns if they are not null)."""
	return to_frame(left).merge(to_frame(right), how=how, on=on)


#########################

def pivot(df, names, index, values):
	return df.pivot(columns=names, index=index, values=values)


def unpivot(df):
	return df.reset_index(level=0)


#########################

def remove_row(df, index=None, level=None, inplace=False):
	return df.drop(index=index, level=level, inplace=inplace)


def remove_row_at(df, i):
	if i < 0:
		i = count_rows(df) + i
	return df.iloc[to_list(range(0, i)) + to_list(range(i + 1, count_rows(df))), :]


def remove_col(df, names=None, level=None, inplace=False):
	return df.drop(columns=names, level=level, inplace=inplace)


def remove_col_at(df, j):
	if j < 0:
		j = count_cols(df) + j
	return df.iloc[:, to_list(range(0, j)) + to_list(range(j + 1, count_cols(df)))]


#########################

def rename(df, names=None, index=None, level=None):
	if is_all_null(names, index):
		set_names(df, range(count_cols(df)))
	else:
		if not is_null(index):
			df.rename(index=index, level=level, copy=False, inplace=True)
		if not is_null(names):
			set_names(df, names)
	return df


def rename_all(*args, names=None, index=None, level=None):
	for arg in args:
		rename(arg, names=names, index=index, level=level)


#########################

def rotate_rows(df, drop=True, prepend=False):
	"""Rotates the rows of the specified dataframe."""
	if is_empty(df):
		return df
	if prepend:
		df = concat_rows(get_last_row(df), df)
		if drop:
			df = remove_row_at(df, -1)
	else:
		df = concat_rows(df, get_first_row(df))
		if drop:
			df = remove_row_at(df, 0)
	return df


def rotate_cols(df, drop=True, prepend=False):
	"""Rotates the columns of the specified dataframe."""
	if is_empty(df):
		return df
	if prepend:
		df = concat_cols(get_last_col(df), df)
		if drop:
			df = remove_col_at(df, -1)
	else:
		df = concat_cols(df, get_first_col(df))
		if drop:
			df = remove_col_at(df, 0)
	return df


#########################

def sum_rows(df):
	"""Returns the sum of the rows of the specified dataframe."""
	return df.sum(axis=0)


def sum_cols(df):
	"""Returns the sum of the columns of the specified dataframe."""
	return df.sum(axis=1)


def product_rows(df):
	"""Returns the product of the rows of the specified dataframe."""
	return df.product(axis=0)


def product_cols(df):
	"""Returns the product of the columns of the specified dataframe."""
	return df.product(axis=1)


# • DATE ###########################################################################################

__DATE_PROCESSORS_________________________________ = ''


def add_period(d, period=PERIOD):
	period_length = int(period[0:-1])
	period_freq = Frequency(period[-1].upper())
	if period_freq is Frequency.DAYS:
		return d + period_length * DAY
	elif period_freq is Frequency.WEEKS:
		return d + period_length * WEEK
	elif period_freq is Frequency.MONTHS:
		return d + period_length * MONTH
	elif period_freq is Frequency.QUARTERS:
		return d + period_length * QUARTER
	elif period_freq is Frequency.SEMESTERS:
		return d + period_length * SEMESTER
	elif period_freq is Frequency.YEARS:
		return d + period_length * YEAR


def subtract_period(d, period=PERIOD):
	period_length = int(period[0:-1])
	period_freq = Frequency(period[-1].upper())
	if period_freq is Frequency.DAYS:
		return d - period_length * DAY
	elif period_freq is Frequency.WEEKS:
		return d - period_length * WEEK
	elif period_freq is Frequency.MONTHS:
		return d - period_length * MONTH
	elif period_freq is Frequency.QUARTERS:
		return d - period_length * QUARTER
	elif period_freq is Frequency.SEMESTERS:
		return d - period_length * SEMESTER
	elif period_freq is Frequency.YEARS:
		return d - period_length * YEAR


#########################

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


#########################

def format_date(d):
	return trim(format_datetime(d, fmt=DEFAULT_DATE_FORMAT))


def format_full_date(d):
	return trim(format_datetime(d, fmt=DEFAULT_FULL_DATE_FORMAT))


def format_month_year(d):
	return trim(format_datetime(d, fmt=DEFAULT_MONTH_YEAR_FORMAT))


def format_full_month_year(d):
	return trim(format_datetime(d, fmt=DEFAULT_FULL_MONTH_YEAR_FORMAT))


def format_month(d):
	return trim(format_datetime(d, fmt=DEFAULT_MONTH_FORMAT))


def format_full_month(d):
	return trim(format_datetime(d, fmt=DEFAULT_FULL_MONTH_FORMAT))


def format_datetime(d, fmt=DEFAULT_DATE_TIME_FORMAT):
	if is_string(d):
		d = parse_datetime(d)
	return trim(d.strftime(fmt)) if not is_null(d) else None


def format_time(d):
	return trim(format_datetime(d, fmt=DEFAULT_TIME_FORMAT))


#########################

def parse_date(s):
	return parser.parse(s).date()


def parse_datetime(s):
	return parser.parse(s)


def parse_time(s):
	return parser.parse(s)


def parse_stamp(s):
	return datetime.fromtimestamp(s)


#########################

def reset_time(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	elif not is_datetime(d):
		return d
	return d.replace(hour=0, minute=0, second=0, microsecond=0)


#########################

def shift_date(d, years=0, months=0, weeks=0, days=0, hours=0, minutes=0, seconds=0,
               microseconds=0):
	return timestamp_to_type(d + pd.DateOffset(years=years, months=months, weeks=weeks, days=days,
	                                           hours=hours, minutes=minutes, seconds=seconds,
	                                           microseconds=microseconds), d)


# • LIST ###########################################################################################

__LIST_PROCESSORS_________________________________ = ''


def filter_list(l, inclusion=None, exclusion=None):
	"""Returns the values of the specified list that are in the specified inclusive list and are not
	in the specified exclusive list."""
	if is_null(inclusion) and is_empty(exclusion):
		return to_list(l)
	elif is_null(inclusion):
		return [v for v in l if v not in to_list(exclusion)]
	elif is_empty(exclusion):
		return [v for v in l if v in to_list(inclusion)]
	return [v for v in l if v in to_list(inclusion) and v not in to_list(exclusion)]


def include_list(l, inclusion):
	"""Returns the values of the specified list that are in the specified inclusive list."""
	return filter_list(l, inclusion=inclusion)


def exclude_list(l, exclusion):
	"""Returns the values of the specified list that are not in the specified exclusive list."""
	return filter_list(l, exclusion=exclusion)


def mask_list(l, mask):
	"""Returns the values of the specified list that are True in the specified mask."""
	return [v for i, v in enumerate(l) if mask[i]]


#########################

def find_all(l, value):
	return find_all_with(l, lambda v: v == value)


def find_all_not(l, value):
	return find_all_not_with(l, lambda v: v == value)


def find_all_in(l, values):
	return find_all_with(l, lambda v: v in to_list(values))


def find_all_not_in(l, values):
	return find_all_not_with(l, lambda v: v in to_list(values))


def find_all_with(l, f, *args, **kwargs):
	return [i for i in range(len(l)) if f(l[i], *args, **kwargs)]


def find_all_not_with(l, f, *args, **kwargs):
	return [i for i in range(len(l)) if not f(l[i], *args, **kwargs)]


#########################

def find(l, value):
	return find_with(l, lambda v: v == value)


def find_not(l, value):
	return find_not_with(l, lambda v: v == value)


def find_in(l, values):
	return find_with(l, lambda v: v in to_list(values))


def find_not_in(l, values):
	return find_not_with(l, lambda v: v in to_list(values))


def find_with(l, f, *args, **kwargs):
	return next((i for i in range(len(l)) if f(l[i], *args, **kwargs)), None)


def find_not_with(l, f, *args, **kwargs):
	return next((i for i in range(len(l)) if not f(l[i], *args, **kwargs)), None)


#########################

def find_last(l, value):
	return find_last_with(l, lambda v: v == value)


def find_last_not(l, value):
	return find_last_not_with(l, lambda v: v == value)


def find_last_in(l, values):
	return find_last_with(l, lambda v: v in to_list(values))


def find_last_not_in(l, values):
	return find_last_not_with(l, lambda v: v in to_list(values))


def find_last_with(l, f, *args, **kwargs):
	return len(l) - find_with(l[::-1], f, *args, **kwargs) - 1


def find_last_not_with(l, f, *args, **kwargs):
	return len(l) - find_not_with(l[::-1], f, *args, **kwargs) - 1


#########################

def repeat(value, n):
	return n * [value]


#########################

def rotate(l, n=1):
	return l[-n:] + l[:-n]


#########################

def tally(l, boundaries):
	"""Tallies the values of the specified list into the intervals delimited by the specified
	boundaries."""
	if is_empty(l):
		return l
	if is_empty(boundaries):
		return repeat(0, len(l))
	index = 0
	lower = min(l)
	for boundary in boundaries:
		upper = boundary
		l = fill_with(l, index, condition=lambda v: lower <= v < upper)
		index += 1
		lower = upper
	l = fill_with(l, index, condition=lambda v: v >= upper)
	return l


# • NUMBER #########################################################################################

__NUMBER_PROCESSORS_______________________________ = ''


def ceil(x):
	return to_int(np.ceil(x))


def floor(x):
	return to_int(np.floor(x))


def round(x, decimals=0):
	if decimals == 0:
		return to_int(np.round(x))
	return np.round(x, decimals=decimals)


#########################

def mod(x, y):
	m = x % y
	return m if m != 0 else y


# • STRING #########################################################################################

__STRING_PROCESSORS_______________________________ = ''


def collapse(*args, delimiter='', append=False):
	"""Returns the string computed by joining the specified arguments with the specified
	delimiter."""
	return delimiter.join([str(v) for v in to_list(*args)]) + (delimiter if append else '')


def collist(*args):
	"""Returns the string computed by joining the specified arguments with a comma."""
	return collapse(to_list(*args), delimiter=',')


def paste(*args):
	"""Returns the string computed by joining the specified arguments with a space."""
	return collapse(remove_empty(args), delimiter=' ')


#########################

def extract(s, pattern):
	"""Returns all the occurrences of the specified pattern from the specified string."""
	return re.findall(pattern, s)


#########################

def replace(s, pattern, replacement):
	"""Returns the string constructed by replacing the specified pattern by the specified
	replacement string in the specified string recursively (only if the length is decreasing)."""
	count = INF
	while len(s) < count:
		count = len(s)
		s = re.sub(pattern, replacement, s)
	return s


#########################

def split(s, delimiter=',', empty_filter=True):
	"""Returns all the tokens computed by splitting the specified string around the specified
	delimiter (regular expression)."""
	if empty_filter:
		return remove_empty(re.split(delimiter, s))
	return re.split(delimiter, s)


#########################

def trim(s, replace_space=True, replace_special=True):
	"""Returns the string constructed by stripping the specified string (and replacing recursively
	the adjacent spaces if replace_space is True and/or special characters if replace_special is
	True to a single space)."""
	if replace_special:
		s = replace(s, '\b|\f|\r\n|\r|\n|\t', ' ')
	if replace_space:
		s = replace(s, ' +', ' ')
	return s.strip()


#########################

def wrap(content, left, right=None):
	"""Returns the wrapped representative string of the specified content."""
	if is_null(left):
		return content
	elif is_null(right):
		right = left
	if is_collection(content):
		return apply(wrap, content, left, right=right)
	return collapse(left, content, right)


def quote(content):
	"""Returns the single-quoted representative string of the specified content."""
	return wrap(content, '\'')


def dquote(content):
	"""Returns the double-quoted representative string of the specified content."""
	return wrap(content, '"')


def par(content):
	"""Returns the parenthesized representative string of the specified content."""
	return wrap(content, '(', ')')


def bra(content):
	"""Returns the bracketized representative string of the specified content."""
	return wrap(content, '[', ']')
