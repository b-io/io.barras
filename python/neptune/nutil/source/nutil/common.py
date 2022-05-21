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
#    Copyright © 2013-2022 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

import cProfile
import functools
import inspect
import itertools
import json
import multiprocessing as mp
import numbers
import os
import pdb
import pstats
import random
import re
import string
import struct
import sys
import warnings
from calendar import monthrange
from concurrent.futures import ThreadPoolExecutor
from datetime import *
from distutils.util import *
from enum import Enum
from io import StringIO
from pstats import SortKey
from typing import Iterable, MutableSet, OrderedDict, Sequence
from urllib.request import urlopen

import javaproperties as prop
import numpy as np
import pandas as pd
import validators
from dateutil import parser
from dateutil.relativedelta import relativedelta
from multiprocess.pool import Pool
from pandas.api.types import is_numeric_dtype

####################################################################################################
# COMMON SETTINGS
####################################################################################################

warnings.simplefilter(action='ignore', category=FutureWarning)

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
# COMMON CLASSES
####################################################################################################

__COMMON_CLASSES__________________________________ = ''


class Object(object):
	pass


##################################################

class OrderedSet(MutableSet, Sequence):

	def __init__(self, *args):
		super().__init__()
		self.elements = OrderedDict.fromkeys(to_collection(*args))

	##############################################
	# OPERATORS
	##############################################

	def __getitem__(self, index):
		return self.to_list()[index]

	def __iter__(self):
		return self.elements.__iter__()

	def __len__(self):
		return self.elements.__len__()

	##############################################

	difference = property(lambda self: self.__sub__)
	difference_update = property(lambda self: self.__isub__)
	intersection = property(lambda self: self.__and__)
	intersection_update = property(lambda self: self.__iand__)
	issubset = property(lambda self: self.__le__)
	issuperset = property(lambda self: self.__ge__)
	symmetric_difference = property(lambda self: self.__xor__)
	symmetric_difference_update = property(lambda self: self.__ixor__)
	union = property(lambda self: self.__or__)

	def __contains__(self, x):
		return self.elements.__contains__(x)

	def __le__(self, other):
		return all(e in other for e in self)

	def __lt__(self, other):
		return self <= other and self != other

	def __ge__(self, other):
		return all(e in self for e in other)

	def __gt__(self, other):
		return self >= other and self != other

	##############################################

	def __repr__(self):
		return 'OrderedSet([%s])' % (', '.join(map(repr, self.elements)))

	def __str__(self):
		return '{%s}' % (', '.join(map(repr, self.elements)))

	##############################################
	# CONVERTERS
	##############################################

	def to_list(self):
		return to_list(self.elements)

	##############################################
	# PROCESSORS
	##############################################

	def add(self, element):
		self.elements[element] = None

	def discard(self, element):
		self.elements.pop(element, None)


####################################################################################################
# COMMON CONSTANTS
####################################################################################################

__COMMON_CONSTANTS________________________________ = ''

OBJECT_TYPE = object

#########################

ITERABLE_TYPE = Iterable

SEQUENCE_TYPE = Sequence

TUPLE_TYPE = tuple

##################################################

BIT_COUNT = 8 * struct.calcsize('P')

CORE_COUNT = mp.cpu_count() or 1

NA_NAME = 'N/A'

# • ARRAY ##########################################################################################

__ARRAY_CONSTANTS_________________________________ = ''

ARRAY_TYPE = np.ndarray

# • DATAFRAME ######################################################################################

__DATAFRAME_CONSTANTS_____________________________ = ''

SERIES_TYPE = pd.Series
SERIES_GROUP_TYPE = pd.core.groupby.generic.SeriesGroupBy

FRAME_TYPE = pd.DataFrame
FRAME_GROUP_TYPE = pd.core.groupby.generic.DataFrameGroupBy

#########################

INDEX_TYPE = pd.Index
TIME_INDEX_TYPE = pd.DatetimeIndex

# • DATE ###########################################################################################

__DATE_CONSTANTS__________________________________ = ''

# The default date format
DEFAULT_DATE_FORMAT = '%Y-%m-%d'

# The default time format
DEFAULT_TIME_FORMAT = '%H:%M:%S'

# The default date-time format
DEFAULT_DATE_TIME_FORMAT = DEFAULT_DATE_FORMAT + ' ' + DEFAULT_TIME_FORMAT

# The default full date format
DEFAULT_FULL_DATE_FORMAT = '%B %e, %Y'

# The default month-year date format
DEFAULT_MONTH_YEAR_FORMAT = '%Y-%m'

# The default full month-year date format
DEFAULT_FULL_MONTH_YEAR_FORMAT = '%B %Y'

# The default month date format
DEFAULT_MONTH_FORMAT = '%b'

# The default full month date format
DEFAULT_FULL_MONTH_FORMAT = '%B'

#########################

# The default frequency
DEFAULT_FREQUENCY = Frequency.MONTHS

# The default group
DEFAULT_GROUP = Group.LAST

# The default period
DEFAULT_PERIOD = '1' + Frequency.YEARS.value

##################################################

DATE_TYPE = date

DATE_TIME_TYPE = datetime

TIMESTAMP_TYPE = pd.Timestamp

##################################################

# The time deltas
DAY = relativedelta(days=1)
WEEK = 7 * DAY
MONTH = relativedelta(months=1)
QUARTER = 3 * MONTH
SEMESTER = 6 * MONTH
YEAR = relativedelta(years=1)

FREQUENCY_DELTA = {
	Frequency.DAYS: DAY,
	Frequency.WEEKS: WEEK,
	Frequency.MONTHS: MONTH,
	Frequency.QUARTERS: QUARTER,
	Frequency.SEMESTERS: SEMESTER,
	Frequency.YEARS: YEAR
}

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

FREQUENCY_DAY_COUNT = {
	Frequency.DAYS: 1,
	Frequency.WEEKS: DAYS_PER_WEEK,
	Frequency.MONTHS: DAYS_PER_MONTH,
	Frequency.QUARTERS: DAYS_PER_QUARTER,
	Frequency.SEMESTERS: DAYS_PER_SEMESTER,
	Frequency.YEARS: DAYS_PER_YEAR
}

DAY_COUNT_FREQUENCY = {FREQUENCY_DAY_COUNT[k]: k for k in FREQUENCY_DAY_COUNT}

#########################

# The weekdays
MON, TUE, WED, THU, FRI, SAT, SUN = WEEKDAYS = tuple(i for i in range(7))
WEEKDAY_NAMES = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']

# • DICT ###########################################################################################

__DICT_CONSTANTS__________________________________ = ''

DICT_TYPE = dict

# • FILE ###########################################################################################

__FILE_CONSTANTS__________________________________ = ''

# The default root
DEFAULT_ROOT = None

# The default resources directory
DEFAULT_RES_DIR = 'resources'

# • LIST ###########################################################################################

__LIST_CONSTANTS__________________________________ = ''

LIST_TYPE = list

# • NUMBER #########################################################################################

__NUMBER_CONSTANTS________________________________ = ''

DEFAULT_MAX_DECIMALS = 8

##################################################

NUMBER_TYPE = numbers.Number

#########################

BOOL_TYPE = bool
BOOL_ELEMENT_TYPE = np.bool8

FLOAT_TYPE = float
FLOAT_ELEMENT_TYPE = np.float32 if BIT_COUNT == 32 else np.float64 if BIT_COUNT == 64 else None

INT_TYPE = int
INT_ELEMENT_TYPE = np.int32 if BIT_COUNT == 32 else np.int64 if BIT_COUNT == 64 else None

LONG_TYPE = int
LONG_ELEMENT_TYPE = np.uint32 if BIT_COUNT == 32 else np.uint64 if BIT_COUNT == 64 else None

SHORT_TYPE = int
SHORT_ELEMENT_TYPE = np.uint8

##################################################

EPS = np.finfo(FLOAT_TYPE).eps
INF = np.inf
NAN = np.nan

# • SET ############################################################################################

__SET_CONSTANTS___________________________________ = ''

SET_TYPE = set

MUTABLE_SET_TYPE = MutableSet

ORDERED_SET_TYPE = OrderedSet

# • STRING #########################################################################################

__STRING_CONSTANTS________________________________ = ''

STRING_TYPE = str
STRING_ELEMENT_TYPE = np.str_  # np.string_

##################################################

NEWLINE = '\n'

BULLET = '•'
COLON = ':'
SEMICOLON = ';'

####################################################################################################
# COMMON VERIFIERS
####################################################################################################

__COMMON_VERIFIERS________________________________ = ''


def is_iterable(x):
	return isinstance(x, ITERABLE_TYPE)


def is_sequence(x):
	return isinstance(x, SEQUENCE_TYPE)


def is_subscriptable(x):
	return hasattr(x, '__getitem__')


def is_tuple(x):
	return isinstance(x, TUPLE_TYPE)


#########################

def is_null(x):
	return x is None or is_nan(x)


def is_all_null(*args):
	return all([is_null(arg) for arg in to_collection(*args)])


def is_all_not_null(*args):
	return not is_any_null(*args)


def is_any_null(*args):
	return any([is_null(arg) for arg in to_collection(*args)])


def is_any_not_null(*args):
	return not is_all_null(*args)


#########################

def is_empty(x):
	return is_null(x) or \
	       (hasattr(x, '__len__') and len(x) == 0 or is_frame(x) and count_cols(x) == 0)


def is_all_empty(*args):
	return all([is_empty(arg) for arg in to_collection(*args)])


def is_all_not_empty(*args):
	return not is_any_empty(*args)


def is_any_empty(*args):
	return any([is_empty(arg) for arg in to_collection(*args)])


def is_any_not_empty(*args):
	return not is_all_empty(*args)


#########################

def is_all_value(value, *args):
	return all([value == arg for arg in to_collection(*args)])


def is_all_not_value(value, *args):
	return not is_any_value(value, *args)


def is_any_value(value, *args):
	return any([value == arg for arg in to_collection(*args)])


def is_any_not_value(value, *args):
	return not is_all_value(value, *args)


##################################################

def exists(x):
	return x in globals() or x in locals() or x in dir(__builtins__)


# • ARRAY ##########################################################################################

__ARRAY_VERIFIERS_________________________________ = ''


def is_array(x):
	return isinstance(x, ARRAY_TYPE)


# • COLLECTION (LIST/DICT/DATAFRAME) ###############################################################

__COLLECTION_VERIFIERS____________________________ = ''


def is_collection(x):
	return is_iterable(x) and not is_string(x) and not is_tuple(x)


#########################

def is_indexed_collection(x):
	return is_collection(x) and has_index(x)


def is_subscriptable_collection(x):
	return is_collection(x) and is_subscriptable(x)


#########################

def is_multidimensional_collection(x):
	return is_table(x) or is_array(x)


##################################################

def has_index(c):
	return is_array(c) or is_index(c) or is_sequence(c)


#########################

def has_filter(keys=None, inclusion=None, exclusion=None):
	return not is_null(keys) or not is_null(inclusion) or not is_empty(exclusion)


# • DATAFRAME ######################################################################################

__DATAFRAME_VERIFIERS_____________________________ = ''


def is_table(x):
	return is_series(x) or is_frame(x)


def is_series(x):
	return isinstance(x, SERIES_TYPE) or isinstance(x, SERIES_GROUP_TYPE)


def is_frame(x):
	return isinstance(x, FRAME_TYPE) or isinstance(x, FRAME_GROUP_TYPE)


def is_group(x):
	return isinstance(x, SERIES_GROUP_TYPE) or isinstance(x, FRAME_GROUP_TYPE)


#########################

def is_time_series(x):
	return is_table(x) and not is_group(x) and is_time_index(x.index)


#########################

def is_index(x):
	return isinstance(x, INDEX_TYPE)


def is_time_index(x):
	return isinstance(x, TIME_INDEX_TYPE)


# • DATE ###########################################################################################

__DATE_VERIFIERS__________________________________ = ''


def is_date(x):
	return isinstance(x, DATE_TYPE)


def is_datetime(x):
	return isinstance(x, DATE_TIME_TYPE)


def is_timestamp(x):
	return isinstance(x, TIMESTAMP_TYPE)


def is_stamp(x):
	return is_number(x)


#########################

def is_business_day(d):
	if is_string(d):
		d = parse_datetime(d)
	return date.weekday(d) < 5


# • DICT ###########################################################################################

__DICT_VERIFIERS__________________________________ = ''


def is_dict(x):
	return isinstance(x, DICT_TYPE)


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
	return isinstance(x, LIST_TYPE)


# • NUMBER #########################################################################################

__NUMBER_VERIFIERS________________________________ = ''


def is_nan(x):
	return x is pd.NA or x is pd.NaT or (is_number(x) and str(x) == 'nan')


#########################

def is_number(x):
	return isinstance(x, NUMBER_TYPE)


def is_bool(x):
	return isinstance(x, BOOL_TYPE) or isinstance(x, BOOL_ELEMENT_TYPE)


def is_float(x):
	return isinstance(x, FLOAT_TYPE) or isinstance(x, FLOAT_ELEMENT_TYPE)


def is_int(x):
	return isinstance(x, INT_TYPE) or isinstance(x, INT_ELEMENT_TYPE)


def is_long(x):
	return isinstance(x, LONG_TYPE) or isinstance(x, LONG_ELEMENT_TYPE)


def is_short(x):
	return isinstance(x, SHORT_TYPE) or isinstance(x, SHORT_ELEMENT_TYPE)


##################################################

def equals(x, y):
	return is_null(x) and is_null(y) or x == y


# • SET ############################################################################################

__SET_VERIFIERS___________________________________ = ''


def is_set(x):
	return isinstance(x, SET_TYPE)


def is_mutable_set(x):
	return isinstance(x, MUTABLE_SET_TYPE)


def is_ordered_set(x):
	return isinstance(x, ORDERED_SET_TYPE)


# • STRING #########################################################################################

__STRING_VERIFIERS________________________________ = ''


def is_string(x):
	return isinstance(x, STRING_TYPE) or isinstance(x, STRING_ELEMENT_TYPE)


####################################################################################################
# FILE FUNCTIONS
####################################################################################################

__FILE____________________________________________ = ''


def get_path(path='.'):
	return os.path.abspath(path)


#########################

def get_dir(path='.', parent=None):
	path = get_path(path)
	if is_null(parent):
		parent = not is_dir(path)
	return os.path.dirname(get_path(path) + ('/' if not parent else ''))


def get_filename(path='.'):
	path = get_path(path)
	return os.path.basename(path)


def get_extension(path='.'):
	path = get_path(path)
	return os.path.splitext(path)[1][1:]


##################################################

def create_dir(path):
	if not os.path.exists(path):
		os.makedirs(path)


##################################################

def format_dir(dir):
	if is_null(dir):
		return ''
	if dir[-1] == '/' or dir[-1] == '\\':
		dir = dir[:-1]
	return dir + '/'


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
             delimiter=',', type=None,
             na_values=None, keep_default_na=False, na_filter=True,
             parse_dates=True, date_parser=None, infer_datetime_format=True, keep_date_col=True,
             verbose=False):
	if is_null(na_values):
		na_values = ['']
	return pd.read_csv(path, encoding=encoding,
	                   delimiter=delimiter, dtype=type,
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
	'''Returns the properties with the specified filename in the specified directory.'''
	with open(find_path(filename + '.properties', dir=dir, subdir=subdir), 'r') as f:
		return prop.load(f)


#########################

# The properties
DEFAULT_PROPS = {
	# • COMMON
	# Assert
	'assert': True,
	# Environment (dev, test, model, prod)
	'env': 'prod',

	# • CONSOLE
	# Severity level (0: FAIL, 1: ERROR, 2: WARN, 3: RESULT, 4: INFO, 5: TEST, 6: DEBUG, 7: TRACE)
	'severityLevel': 4,
	# Verbose
	'verbose': True,

	# • DATE
	# Date format
	'dateFormat': '%Y-%m-%d',
	# Time format
	'timeFormat': '%H:%M:%S',
	# Frequency (D, W, M, Q, S, Y)
	'frequency': 'D',
	# Group (count, first, last, min, max, mean, median, std, var, sum)
	'group': 'last',
	# Period
	'period': '1Y'
}
try:
	PROPS = load_props('common')
except FileNotFoundError as ex:
	PROPS = DEFAULT_PROPS


def get_prop(name, default=None):
	'''Returns the property with the specified name.'''
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

# The flag specifying whether to assert
ASSERT = get_bool_prop('assert', True)

# The environment
ENV = Environment(get_prop('env', 'prod'))

# • CONSOLE ########################################################################################

__CONSOLE_PROPERTIES______________________________ = ''

# The severity level
SEVERITY_LEVEL = SeverityLevel(get_int_prop('severityLevel', 4))

# The flag specifying whether to enable the verbose mode
VERBOSE = get_bool_prop('verbose', True)

# • DATE ###########################################################################################

__DATE_PROPERTIES_________________________________ = ''

# The date format
DATE_FORMAT = get_prop('dateFormat', DEFAULT_DATE_FORMAT)

# The time format
TIME_FORMAT = get_prop('timeFormat', DEFAULT_TIME_FORMAT)

# The date-time format
DATE_TIME_FORMAT = DATE_FORMAT + ' ' + TIME_FORMAT

#########################

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

def get_stack(level=0):
	return inspect.stack()[level + 1]


def get_script_name(level=0):
	return get_filename(get_stack(level + 1)[1])


def get_function_name(level=0):
	return get_stack(level + 1)[3]


def get_line_number(level=0):
	return get_stack(level + 1)[2]


#########################

def get_module_name(obj):
	return obj.__class__.__module__


def get_class_name(obj):
	return obj.__class__.__name__


def get_full_class_name(obj):
	module_name = get_module_name(obj)
	if is_null(module_name) or module_name == get_module_name(str):
		return get_class_name(obj)
	return collapse(module_name, '.', get_class_name(obj))


def get_attributes(obj):
	return [a for a in vars(obj) if not a.startswith('_')]


def get_all_attributes(obj):
	return [a for a in dir(obj) if not a.startswith('_')]


# • COLLECTION (LIST/DICT/DATAFRAME) ###############################################################

__COLLECTION_ACCESSORS____________________________ = ''


def get(c, index=0,
        axis=0):
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(axis):
		return simplify(flatten(c, axis=axis)[index])
	if is_multidimensional_collection(c):
		if axis == 0:
			return simplify(get_row(c, index))
		return simplify(get_col(c, index))
	elif is_dict(c):
		return simplify(c[get_keys(c)[index]])
	return simplify(c[index])


def get_first(c,
              axis=0):
	return get(c, index=0, axis=axis)


def get_last(c,
             axis=0):
	return get(c, index=-1, axis=axis)


def get_iterator(c, cycle=False):
	if cycle:
		return itertools.cycle(c)
	return iter(c)


def get_next(c):
	if not is_collection(c):
		return c
	return next(get_iterator(c))


#########################

def get_shape(c,
              keys=None, inclusion=None, exclusion=None):
	c = filter(c, keys=keys, inclusion=inclusion, exclusion=exclusion)
	if is_multidimensional_collection(c):
		return c.shape
	elif is_tuple(c):
		return c
	return (len(c),)


#########################

def get_name(c,
             inclusion=None, exclusion=None):
	return simplify(get_names(c, inclusion=inclusion, exclusion=exclusion))


def get_names(c,
              inclusion=None, exclusion=None):
	'''Returns the names of the specified collection.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_table(inclusion):
		inclusion = get_names(inclusion)
	if is_table(exclusion):
		exclusion = get_names(exclusion)
	if hasattr(c, 'names'):
		c = c.names() if callable(c.names) else c.names
	elif hasattr(c, 'name'):
		c = c.name() if callable(c.name) else c.name
	elif is_collection(c):
		if has_index(c):
			c = range(len(c))
		else:
			c = [get_name(e) for e in c]
	else:
		c = [to_string(c)]
	return filter_list(c, inclusion=inclusion, exclusion=exclusion)


def get_all_common_names(*args,
                         inclusion=None, exclusion=None):
	return reduce(lambda c1, c2: get_common_names(c1, c2,
	                                              inclusion=inclusion, exclusion=exclusion),
	              *args)


def get_common_names(c1, c2,
                     inclusion=None, exclusion=None):
	'''Returns the common names of the specified collections that are in the specified inclusive
	list and are not in the specified exclusive list.'''
	return get_names(c1, inclusion=include_list(get_names(c2), inclusion), exclusion=exclusion)


def get_all_uncommon_names(*args,
                           inclusion=None, exclusion=None):
	return reduce(lambda c1, c2: get_uncommon_names(c1, c2,
	                                                inclusion=inclusion, exclusion=exclusion),
	              *args)


def get_uncommon_names(c1, c2,
                       inclusion=None, exclusion=None):
	'''Returns the uncommon names of the specified collections that are in the specified inclusive
	list and are not in the specified exclusive list.'''
	return get_names(c1, inclusion=inclusion, exclusion=include_list(get_names(c2), exclusion))


#########################

def get_key(c,
            inclusion=None, exclusion=None):
	return simplify(get_keys(c, inclusion=inclusion, exclusion=exclusion))


def get_keys(c,
             inclusion=None, exclusion=None):
	'''Returns the keys (indices/keys/names) of the specified collection that are in the specified
	inclusive list and are not in the specified exclusive list.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_empty(c) or not is_subscriptable_collection(c):
		return OrderedSet()
	if is_table(inclusion):
		inclusion = get_keys(inclusion)
	if is_table(exclusion):
		exclusion = get_keys(exclusion)
	if is_series(c):
		c = c.index
	elif has_index(c):
		c = range(len(c))
	return filter_ordered_set(c, inclusion=inclusion, exclusion=exclusion)


def get_all_common_keys(*args,
                        inclusion=None, exclusion=None):
	return reduce(lambda c1, c2: get_common_keys(c1, c2,
	                                             inclusion=inclusion, exclusion=exclusion),
	              *args)


def get_common_keys(c1, c2,
                    inclusion=None, exclusion=None):
	'''Returns the common keys (indices/keys/names) of the specified collections that are in the
	specified inclusive list and are not in the specified exclusive list.'''
	return get_keys(c1, inclusion=include_list(get_keys(c2), inclusion), exclusion=exclusion)


def get_all_uncommon_keys(*args,
                          inclusion=None, exclusion=None):
	return reduce(lambda c1, c2: get_uncommon_keys(c1, c2,
	                                               inclusion=inclusion, exclusion=exclusion),
	              *args)


def get_uncommon_keys(c1, c2,
                      inclusion=None, exclusion=None):
	'''Returns the uncommon keys (indices/keys/names) of the specified collections that are in the
	specified inclusive list and are not in the specified exclusive list.'''
	return get_keys(c1, inclusion=inclusion, exclusion=include_list(get_keys(c2), exclusion))


#########################

def get_index(c,
              inclusion=None, exclusion=None):
	'''Returns the index (indices/keys/index) of the specified collection that are in the
	specified inclusive list and are not in the specified exclusive list.'''
	if is_group(c):
		c = c.obj if c.axis == 1 else c.groups
	if is_table(inclusion):
		inclusion = get_index(inclusion)
	if is_table(exclusion):
		exclusion = get_index(exclusion)
	if is_table(c):
		return filter_list(c.index, inclusion=inclusion, exclusion=exclusion)
	return get_keys(c, inclusion=inclusion, exclusion=exclusion)


def get_all_common_index(*args,
                         inclusion=None, exclusion=None):
	return reduce(lambda c1, c2: get_common_index(c1, c2,
	                                              inclusion=inclusion, exclusion=exclusion),
	              *args)


def get_common_index(c1, c2,
                     inclusion=None, exclusion=None):
	'''Returns the common index (indices/keys/index) of the specified collections that are in the
	specified inclusive list and are not in the specified exclusive list.'''
	return get_index(c1, inclusion=include_list(get_index(c2), inclusion), exclusion=exclusion)


def get_all_uncommon_index(*args,
                           inclusion=None, exclusion=None):
	return reduce(lambda c1, c2: get_uncommon_index(c1, c2,
	                                                inclusion=inclusion, exclusion=exclusion),
	              *args)


def get_uncommon_index(c1, c2,
                       inclusion=None, exclusion=None):
	'''Returns the uncommon index (indices/keys/index) of the specified collections that are in the
	specified inclusive list and are not in the specified exclusive list.'''
	return get_index(c1, inclusion=inclusion, exclusion=include_list(get_index(c2), exclusion))


#########################

def get_keys_or_index(c,
                      axis=0,
                      inclusion=None, exclusion=None):
	return get_keys(c, inclusion=inclusion, exclusion=exclusion) if axis == 0 else \
		get_index(c, inclusion=inclusion, exclusion=exclusion)


def get_index_or_keys(c,
                      axis=0,
                      inclusion=None, exclusion=None):
	return get_index(c, inclusion=inclusion, exclusion=exclusion) if axis == 0 else \
		get_keys(c, inclusion=inclusion, exclusion=exclusion)


#########################

def get_item(c,
             keys=None, inclusion=None, exclusion=None):
	return simplify(get_items(c, keys=keys, inclusion=inclusion, exclusion=exclusion))


def get_items(c,
              keys=None, inclusion=None, exclusion=None):
	'''Returns the items (values/entries/columns) of the specified collection whose keys
	(indices/keys/names) are in the specified inclusive list and are not in the specified exclusive
	list.'''
	if is_empty(c):
		return []
	elif not is_subscriptable_collection(c):
		return to_list(c)
	if not has_filter(keys=keys, inclusion=inclusion, exclusion=exclusion):
		if is_table(c) and not is_group(c) or is_dict(c):
			return c.items()
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_empty(keys):
		return []
	if is_group(c):
		if c.axis == 0:
			return [(k, filter(v, keys=keys)) for k, v in c]
		return [(k, v) for k, v in c if k in keys]
	return [(k, c[k]) for k in keys]


#########################

def get_value(c, type=None,
              keys=None, inclusion=None, exclusion=None):
	return simplify(get_values(c, type=type, keys=keys, inclusion=inclusion, exclusion=exclusion))


def get_values(c, type=None,
               keys=None, inclusion=None, exclusion=None):
	'''Returns the values (values/values/columns) of the specified collection whose keys
	(indices/keys/names) are in the specified inclusive list and are not in the specified exclusive
	list.'''
	if is_empty(c):
		return to_array(type=type)
	elif not is_subscriptable_collection(c):
		return to_array(c, type=type)
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_empty(keys):
		return to_array(type=type)
	if is_group(c):
		if c.axis == 0:
			return to_array([filter(v, keys=keys).values for k, v in c], type=type)
		return to_array([v.values for k, v in c if k in keys], type=type)
	elif is_table(c):
		return filter(c, keys=keys).values
	elif is_array(c):
		return c[keys]
	return to_array([c[k] for k in keys], type=type)


##################################################

def set_names(c, new_names):
	'''Sets the names of the specified collection.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_table(new_names):
		new_names = get_names(new_names)
	else:
		new_names = to_list(new_names)
	if is_empty(new_names):
		return c
	if is_frame(c):
		c.columns = new_names
	elif is_series(c):
		c.name = simplify(new_names)
	else:
		set_keys(c, new_names)
	return c


def set_keys(c, new_keys,
             keys=None, inclusion=None, exclusion=None):
	'''Sets the keys (indices/keys/names) of the specified collection that are in the specified
	inclusive list and are not in the specified exclusive list.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_empty(keys):
		return c
	if is_table(new_keys):
		new_keys = get_keys(new_keys)
	else:
		new_keys = to_ordered_set(new_keys)
	if is_empty(new_keys):
		return c
	if is_frame(c):
		c.loc[:, keys].columns = new_keys
	elif is_series(c):
		set_index(c, new_keys)
	elif is_dict(c):
		upsert(c, {new_key: c.pop(key) for key, new_key in zip(keys, new_keys)})
	else:
		update(c, {new_key: c[key] for key, new_key in zip(keys, new_keys)}, inclusion=new_keys)
	return c


def set_index(c, new_index):
	'''Sets the index (indices/keys/index) of the specified collection that are in the specified
	inclusive list and are not in the specified exclusive list.'''
	if is_group(c):
		c = c.obj if c.axis == 1 else c.groups
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_table(new_index):
		new_index_names = get_names(new_index.index)
		new_index = new_index.index
	else:
		new_index_names = get_names(new_index)
		new_index = to_list(new_index)
	if is_empty(new_index):
		return c
	if is_table(c):
		if not is_empty(new_index) and is_tuple(new_index[0]):
			new_index_names = resize_list(new_index_names, len(new_index[0]))
			c.index = pd.MultiIndex.from_tuples(new_index, names=new_index_names)
		else:
			rename(c, index=dict(zip(c.index, new_index)))
	else:
		set_keys(c, new_index)
	return c


def set_values(c, new_values, mask=None,
               keys=None, inclusion=None, exclusion=None):
	'''Sets the values (values/values/columns) of the specified collection whose keys
	(indices/keys/names) are in the specified inclusive list and are not in the specified exclusive
	list.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_empty(keys):
		return c
	if is_collection(new_values):
		new_values = get_values(new_values)
	else:
		if not is_multidimensional_collection(c) or is_null(mask):
			new_values = create_array(get_shape(c, keys=keys), fill=new_values)
	if not is_null(mask):
		if is_multidimensional_collection(c):
			c[mask] = new_values
		else:
			for i, k in enumerate(keys):
				if mask[i]:
					c[k] = new_values[i]
	else:
		if is_empty(new_values):
			return c
		if is_frame(c):
			chained_assignment = pd.options.mode.chained_assignment
			pd.options.mode.chained_assignment = None
			c.loc[:, keys] = new_values.reshape(count_rows(c), len(keys))
			pd.options.mode.chained_assignment = chained_assignment
		elif is_series(c):
			chained_assignment = pd.options.mode.chained_assignment
			pd.options.mode.chained_assignment = None
			c.loc[keys] = new_values
			pd.options.mode.chained_assignment = chained_assignment
		elif is_array(c):
			c[keys] = new_values
		else:
			for i, k in enumerate(keys):
				c[k] = new_values[i]
	return c


# • DATAFRAME ######################################################################################

__DATAFRAME_ACCESSORS_____________________________ = ''


def get_row(df, i=0):
	'''Returns the row of the specified dataframe at the specified index.'''
	if is_group(df):
		df = get_values(df)
	elif is_table(df):
		return df.iloc[i:] if i == -1 else df.iloc[i:i + 1]
	return df[i]


def get_first_row(df):
	'''Returns the first row of the specified dataframe.'''
	return get_row(df, 0)


def get_last_row(df):
	'''Returns the last row of the specified dataframe.'''
	return get_row(df, -1)


def get_rows(df):
	'''Returns the rows of the specified dataframe.'''
	if is_frame(df):
		return [row for _, row in df.iterrows()]
	elif is_series(df):
		return [row for _, row in df.items()]
	return [get_row(df, i) for i in range(count_rows(df))]


#########################

def get_col(df, j=0):
	'''Returns the column of the specified dataframe at the specified index.'''
	if is_group(df):
		df = get_values(df)
	elif is_frame(df):
		return df.iloc[:, j]
	elif is_series(df):
		return df.iloc[:]
	return df[:, j]


def get_first_col(df):
	'''Returns the first column of the specified dataframe.'''
	return get_col(df, 0)


def get_last_col(df):
	'''Returns the last column of the specified dataframe.'''
	return get_col(df, -1)


def get_cols(df):
	'''Returns the columns of the specified dataframe.'''
	if is_frame(df):
		return [col for _, col in df.items()]
	elif is_series(df):
		return [df]
	return [get_col(df, j) for j in range(count_cols(df))]


# • DATE ###########################################################################################

__DATE_ACCESSORS__________________________________ = ''


def get_date():
	return date.today()


def get_date_string():
	return format_date(get_date())


def get_datetime():
	return datetime.now()


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
	return collection_to_type([get_day(d, week=week, year=year) for d in c], c)


def get_weekday(d=get_datetime()):
	return get_day(d, week=True)


def get_weekdays(d=get_datetime()):
	return get_days(d, week=True)


def get_weekday_name(d=get_datetime()):
	return WEEKDAY_NAMES[get_weekday(d)]


def get_weekday_names(d=get_datetime()):
	return apply(get_weekdays(d), lambda d: WEEKDAY_NAMES[d])


def get_week(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	if is_date(d):
		return d.isocalendar()[1]
	return d


def get_weeks(c):
	if is_table(c):
		return to_timestamp(get_index(c)).week
	elif is_dict(c):
		return get_weeks(get_index(c))
	return collection_to_type([get_week(d) for d in c], c)


def get_year_week(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	if is_date(d):
		iso_cal = d.isocalendar()
		return iso_cal[0], iso_cal[1]
	return d


def get_year_weeks(c):
	if is_table(c):
		return pd.MultiIndex.from_tuples(get_year_weeks(get_index(c)), names=['year', 'week'])
	elif is_dict(c):
		return get_year_weeks(get_index(c))
	return collection_to_type([get_year_week(d) for d in c], c)


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
	return collection_to_type([get_month(d) for d in c], c)


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
	return collection_to_type([get_quarter(d) for d in c], c)


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
	return collection_to_type([get_semester(d) for d in c], c)


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
	return collection_to_type([get_year(d) for d in c], c)


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
	if day is MON:  # Monday
		return d - 3 * DAY
	elif day is SUN:  # Sunday
		return d - 2 * DAY
	return d - DAY


def get_next_business_day(d=get_datetime()):
	if is_string(d):
		d = parse_datetime(d)
	day = date.weekday(d)
	if day is FRI:  # Friday
		return d + 3 * DAY
	elif day is SAT:  # Saturday
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
		return apply(d, get_month_start)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(day=1))


def get_month_end(d=get_datetime()):
	if is_collection(d):
		return apply(d, get_month_end)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(day=get_month_days(d.year, d.month)))


def get_prev_month_start(d=get_datetime()):
	if is_collection(d):
		return apply(d, get_prev_month_start)
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
		return apply(d, get_prev_month_end)
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
		return apply(d, get_next_month_start)
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
		return apply(d, get_next_month_end)
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
		return apply(d, get_quarter_start)
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
		return apply(d, get_quarter_end)
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
		return apply(d, get_prev_quarter_start)
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
		return apply(d, get_prev_quarter_end)
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
		return apply(d, get_next_quarter_start)
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
		return apply(d, get_next_quarter_end)
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
		return apply(d, get_semester_start)
	elif is_string(d):
		d = parse_datetime(d)
	if 1 <= d.month <= 6:
		month = 1
	else:
		month = 7
	return reset_time(d.replace(month=month, day=1))


def get_semester_end(d=get_datetime()):
	if is_collection(d):
		return apply(d, get_semester_end)
	elif is_string(d):
		d = parse_datetime(d)
	if 1 <= d.month <= 6:
		month = 6
	else:
		month = 12
	return reset_time(d.replace(month=month, day=get_month_days(d.year, month)))


def get_prev_semester_start(d=get_datetime()):
	if is_collection(d):
		return apply(d, get_prev_semester_start)
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
		return apply(d, get_prev_semester_end)
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
		return apply(d, get_next_semester_start)
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
		return apply(d, get_next_semester_end)
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
		return apply(d, get_year_start)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(month=1, day=1))


def get_year_end(d=get_datetime()):
	if is_collection(d):
		return apply(d, get_year_end)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(month=12, day=31))


def get_prev_year_start(d=get_datetime()):
	if is_collection(d):
		return apply(d, get_prev_year_start)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(year=d.year - 1, month=1, day=1))


def get_prev_year_end(d=get_datetime()):
	if is_collection(d):
		return apply(d, get_prev_year_end)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(year=d.year - 1, month=12, day=31))


def get_next_year_start(d=get_datetime()):
	if is_collection(d):
		return apply(d, get_next_year_start)
	elif is_string(d):
		d = parse_datetime(d)
	return reset_time(d.replace(year=d.year + 1, month=1, day=1))


def get_next_year_end(d=get_datetime()):
	if is_collection(d):
		return apply(d, get_next_year_end)
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


#########################

def get_freq(freq=FREQUENCY, group=GROUP):
	f = freq.value
	if group is Group.FIRST:
		if freq is Frequency.DAYS:
			pass
		elif freq is Frequency.WEEKS:
			f += '-' + WEEKDAY_NAMES[MON]
		else:
			f += 'S'
	return f


#########################

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
		return period_length * FREQUENCY_DAY_COUNT[period_freq]
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


####################################################################################################
# COMMON CONVERTERS
####################################################################################################

__COMMON_CONVERTERS_______________________________ = ''

# • ARRAY ##########################################################################################

__ARRAY_CONVERTERS________________________________ = ''


def to_array(*args, type=None):
	if len(args) == 1:
		arg = args[0]
		if is_array(arg):
			return arg
		elif is_subscriptable_collection(arg):
			return np.array(arg, dtype=type)
	return np.array(list(args), dtype=type)


def unarray(a):
	if is_array(a):
		if len(a) == 1:
			return a[0]
		return tuple(a)
	return a


# • COLLECTION (LIST/DICT/DATAFRAME) ###############################################################

__COLLECTION_CONVERTERS___________________________ = ''


def to_collection(*args):
	if len(args) == 1:
		arg = args[0]
		if is_collection(arg):
			return arg
		return [arg]
	return list(args)


def to_indexed_collection(*args):
	if len(args) == 1:
		arg = args[0]
		if is_indexed_collection(arg):
			return arg
		return [arg]
	return list(args)


def to_subscriptable_collection(*args):
	if len(args) == 1:
		arg = args[0]
		if is_subscriptable_collection(arg):
			return arg
		return [arg]
	return list(args)


def uncollect(c):
	if is_collection(c):
		if len(c) == 1:
			return get_next(c)
		return tuple(c)
	return c


#########################

def collection_to_type(c, x):
	if is_frame(x):
		return to_frame(c, names=x, index=x)
	elif is_series(x):
		return to_series(c, name=x, index=x)
	elif is_dict(x):
		return dict(zip(get_keys(x), c))
	elif is_ordered_set(x):
		return to_ordered_set(c)
	elif is_set(x):
		return to_set(c)
	elif is_array(x):
		return to_array(c)
	elif is_list(x):
		return to_list(c)
	return c


def collection_to_common_type(c, x):
	if is_frame(x):
		return to_frame(c)
	elif is_series(x):
		return to_series(c)
	elif is_dict(x):
		return to_dict(c)
	elif is_ordered_set(x):
		return to_ordered_set(c)
	elif is_set(x):
		return to_set(c)
	elif is_array(x):
		return to_array(c)
	elif is_list(x):
		return to_list(c)
	return c


# • DATAFRAME ######################################################################################

__DATAFRAME_CONVERTERS____________________________ = ''


def to_series(data, name=None, index=None, type=None):
	'''Converts the specified collection to a series.'''
	if is_empty(data):
		data = []
		type = OBJECT_TYPE
	elif is_group(data):
		data = data.obj
	elif not is_collection(data):
		data = create_array(len(get_index(index)), fill=data, type=type)
	if is_frame(data):
		if count_cols(data) > 1:
			return get_cols(data)
		series = get_col(data) if not is_empty(data) else pd.Series(data=data, dtype=type)
	elif is_series(data):
		series = data.copy()
	else:
		series = pd.Series(data=data, dtype=type)
	if not is_null(name):
		set_names(series, name)
	if not is_null(index):
		set_index(series, index)
	return series


def to_time_series(data, name=None, index=None, type=FLOAT_ELEMENT_TYPE):
	'''Converts the specified collection to a time series.'''
	if not is_null(index):
		index = to_timestamp(to_array(index))
	return to_series(data, name=name, index=index, type=type)


#########################

def to_frame(data, names=None, index=None, type=None):
	'''Converts the specified collection to a dataframe.'''
	if is_empty(data):
		data = []
		type = OBJECT_TYPE
	elif is_group(data):
		data = data.obj
	elif not is_collection(data):
		data = create_array((len(get_index(index)), len(get_names(names))), fill=data, type=type)
	if is_frame(data):
		frame = data.copy()
	elif is_series(data):
		frame = data.to_frame()
	elif is_dict(data):
		frame = pd.DataFrame.from_dict(data, dtype=type, orient='index')
	else:
		frame = pd.DataFrame(data=data, dtype=type)
	if not is_null(names):
		set_names(frame, names)
	if not is_null(index):
		set_index(frame, index)
	return frame


def to_time_frame(data, names=None, index=None, type=FLOAT_ELEMENT_TYPE):
	'''Converts the specified collection to a time frame.'''
	if not is_null(index):
		index = to_timestamp(to_array(index))
	return to_frame(data, names=names, index=index, type=type)


# • DATE ###########################################################################################

__DATE_CONVERTERS_________________________________ = ''


def to_date(x, format=DATE_FORMAT):
	if is_null(x):
		return None
	elif is_collection(x):
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
		return apply(x, to_stamp)
	elif is_stamp(x):
		return x
	return to_datetime(x).timestamp()


#########################

def timestamp_to_type(t, x):
	'''Converts the specified timestamp to the type of the specified variable.'''
	if is_collection(t):
		return apply(t, timestamp_to_type, x)
	elif is_stamp(x):
		return to_stamp(t)
	elif is_timestamp(x):
		return t
	elif is_datetime(x):
		return to_datetime(t)
	elif is_date(x):
		return to_date(t)
	return t


#########################

def to_period(length, freq=FREQUENCY):
	return str(length) + freq.value


def to_period_length(period):
	return int(period[0:-1])


def to_period_freq(period):
	return Frequency(period[-1].upper())


# • DICT ###########################################################################################

__DICT_CONVERTERS_________________________________ = ''


def to_dict(c):
	'''Converts the specified collection to a dictionary.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_empty(c):
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


# • NUMBER #########################################################################################

__NUMBER_CONVERTERS_______________________________ = ''


def to_bool(x):
	if is_null(x):
		return NAN
	elif is_collection(x):
		return apply(x, to_bool)
	elif is_string(x):
		return bool(strtobool(x))
	return bool(x)


def to_int(x):
	if is_null(x):
		return NAN
	elif is_collection(x):
		return apply(x, to_int)
	return int(x)


def to_float(x):
	if is_null(x):
		return NAN
	elif is_collection(x):
		return apply(x, to_float)
	return float(x)


# • SET ############################################################################################

__SET_CONVERTERS__________________________________ = ''


def to_set(*args):
	if len(args) == 1:
		arg = args[0]
		if is_set(arg):
			return arg
		elif is_collection(arg):
			return set(arg)
		return {arg}
	return set(args)


def unset(s):
	if is_set(s):
		if len(s) == 1:
			return get_next(s)
		return tuple(s)
	return s


#########################

def to_ordered_set(*args):
	if len(args) == 1:
		arg = args[0]
		if is_ordered_set(arg):
			return arg
	return OrderedSet(*args)


# • STRING #########################################################################################

__STRING_CONVERTERS_______________________________ = ''


def to_string(x, delimiter=','):
	if is_null(x):
		return None
	elif is_collection(x):
		return collapse(x, delimiter=delimiter)
	return str(x)


####################################################################################################
# COMMON FORMATTERS
####################################################################################################

__COMMON_FORMATTERS_______________________________ = ''


def format_bulleted_value(value):
	return collapse(NEWLINE, BULLET, ' ', round(value) if is_number(value) else value)


def format_bulleted_list(l, f=format_bulleted_value):
	return collapse([f(v) for v in l])


#########################

def format_bulleted_item(key, value):
	return format_bulleted_value(collapse(key, COLON, ' ',
	                                      round(value) if is_number(value) else value))


def format_bulleted_dict(d, f=format_bulleted_item):
	return collapse([f(k, v) for k, v in d.items()])


# • DATE ###########################################################################################

__DATE_FORMATTERS_________________________________ = ''


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


# • NUMBER #########################################################################################

__NUMBER_FORMATTERS_______________________________ = ''


def format_number(x, decimals=DEFAULT_MAX_DECIMALS):
	return str(round(x, decimals=decimals))


#########################

def format_nth(x):
	s = str(x)
	if s[-1] == '1':
		return s + 'st'
	elif s[-1] == '2':
		return s + 'nd'
	elif s[-1] == '3':
		return s + 'rd'
	return s + 'th'


def format_percent(x):
	return format_number(x * 100) + '%'


####################################################################################################
# COMMON GENERATORS
####################################################################################################

__COMMON_GENERATORS_______________________________ = ''

# • ARRAY ##########################################################################################

__ARRAY_GENERATORS________________________________ = ''


def create_array(shape, fill=0, order='C', type=None):
	return np.full(get_shape(shape), fill, dtype=type, order=order)


# • COLLECTION (LIST/DICT/DATAFRAME) ###############################################################

__COLLECTION_GENERATORS___________________________ = ''


def create_mask(c, *args, condition=lambda x, *args, **kwargs: True, fill=True,
                keys=None, inclusion=None, exclusion=None,
                **kwargs):
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	mask = collection_to_type(create_array(c, fill=fill, type=BOOL_ELEMENT_TYPE), c)
	values = apply(c, condition, *args, keys=keys, **kwargs)
	set_values(mask, values, keys=keys)
	return mask


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

def create_date_range(date_from, date_to, periods=None, freq=FREQUENCY, group=GROUP):
	if not is_null(periods):
		return to_date(pd.date_range(date_from, date_to, periods=periods))
	if freq is Frequency.SEMESTERS:
		months = [1, 7] if group is Group.FIRST else [6, 12]
		return filter_with(create_date_sequence(date_from, date_to, freq=Frequency.QUARTERS,
		                                        group=group),
		                   f=lambda d: get_month(d) in months)
	f = get_freq(freq=freq, group=group)
	return pd.date_range(date_from, date_to, freq=f)


def create_date_sequence(date_from, date_to, periods=None, freq=FREQUENCY, group=GROUP):
	date_range = create_date_range(date_from, date_to, periods=periods, freq=freq, group=group)
	return to_date(date_range)


def create_datetime_sequence(date_from, date_to, periods=None, freq=FREQUENCY, group=GROUP):
	date_range = create_date_range(date_from, date_to, periods=periods, freq=freq, group=group)
	return to_datetime(date_range)


def create_timestamp_sequence(date_from, date_to, periods=None, freq=FREQUENCY, group=GROUP):
	date_range = create_date_range(date_from, date_to, periods=periods, freq=freq, group=group)
	return to_timestamp(date_range)


def create_stamp_sequence(date_from, date_to, periods=None, freq=FREQUENCY, group=GROUP):
	date_range = create_date_range(date_from, date_to, periods=periods, freq=freq, group=group)
	return to_stamp(date_range)


# • NUMBER #########################################################################################

__NUMBER_GENERATORS_______________________________ = ''


def create_sequence(start=0, stop=0, step=1, include=False, size=None):
	if start == stop:
		if include:
			return start
		return to_array(type=INT_ELEMENT_TYPE)
	elif start > stop:
		start, stop = stop, start
	if not is_null(size):
		if size <= 1:
			return start
		step = (stop - start) / (size if not include else size - 1)
	sequence = np.arange(start, stop, step)
	if include:
		sequence = np.append(sequence, stop)
	return sequence


# • STRING #########################################################################################

__STRING_GENERATORS_______________________________ = ''


def generate_string(length, case_sensitive=False, digits=True):
	'''Generates a pseudorandom, uniformly distributed string of the specified length.'''
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


def apply(x, f, *args, inplace=False,
          axis=None,
          keys=None, inclusion=None, exclusion=None,
          **kwargs):
	'''Applies the specified function iteratively over the specified value along the specified axis
	(over the rows, columns or elements if the specified axis is respectively zero, one or null)
	with the specified arguments.'''
	if is_subscriptable_collection(x):
		if is_empty(x):
			return x
		if is_null(keys):
			keys = get_keys(x, inclusion=inclusion, exclusion=exclusion)
		if inplace:
			return set_values(x, apply(x, f, *args, axis=axis, keys=keys, **kwargs), keys=keys)
		if is_group(x):
			axis = x.axis
			if axis == 0:
				return concat_rows([to_frame([to_array(f(get_values(v, keys=keys)), *args, **kwargs)],
				                             index=to_list(i)) for i, v in x])
			return concat_cols([to_series(to_array(f(to_array(v), *args, **kwargs)),
			                              name=k) for k, v in x if k in keys])
		elif is_frame(x):
			if is_null(axis):
				return concat_cols([x.loc[:, k].apply(f, args=args, **kwargs) for k in keys])
			return x.loc[:, keys].apply(f, args=args, axis=axis, **kwargs)
		elif is_series(x):
			return x.loc[keys].apply(f, args=args, **kwargs)
		elif is_dict(x):
			return {k: f(x[k], *args, **kwargs) for k in keys}
		elif is_array(x):
			if is_null(axis):
				return np.vectorize(lambda x: f(x, *args, **kwargs))(x[keys])
			return np.apply_along_axis(f, axis, x[keys], *args, **kwargs)
		return collection_to_type([f(x[k], *args, **kwargs) for k in keys], x)
	elif is_string(x):
		if is_null(keys):
			keys = get_keys(x, inclusion=inclusion, exclusion=exclusion)
		return collapse([f(c, *args, **kwargs) if i in keys else c for i, c in enumerate(x)])
	return f(x, *args, **kwargs)


def fill_with(x, value, *args, condition=lambda x, *args, **kwargs: True, inplace=False, **kwargs):
	return apply(x, lambda x: value if condition(x, *args, **kwargs) else x, inplace=inplace)


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


def reduce_and(x,
               axis=0):
	'''Reduces the dimension of the specified arguments by applying the logical AND function
	cumulatively along the specified axis (over the rows or columns if the specified axis is
	respectively zero or one).'''
	if axis == 1 and count_cols(x) == 0:
		return to_array(x, type=BOOL_ELEMENT_TYPE)
	return np.logical_and.reduce(x, axis=axis)


def reduce_or(x,
              axis=0):
	'''Reduces the dimension of the specified arguments by applying the logical OR function
	cumulatively along the specified axis (over the rows or columns if the specified axis is
	respectively zero or one).'''
	if axis == 1 and count_cols(x) == 0:
		return to_array(x, type=BOOL_ELEMENT_TYPE)
	return np.logical_or.reduce(x, axis=axis)


#########################

def reduce(f, *args, initial=None, **kwargs):
	'''Reduces the specified arguments to a single one by applying the specified function
	cumulatively (from left to right).'''
	args = remove_empty(to_collection(*args))
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

def calculate(c, f, *args,
              axis=0,
              **kwargs):
	if is_group(c):
		axis = c.axis
		if axis == 0:
			names = get_names(c)
			return concat_rows([to_frame([f(v.values, *args, axis=axis, **kwargs)],
			                             names=names, index=to_list(i)) for i, v in c])
		index = get_index(c)
		return concat_cols([to_series(f(v.values, *args, axis=axis, **kwargs),
		                              name=k, index=index) for k, v in c])
	elif is_frame(c):
		index = get_keys_or_index(c, axis=axis)
		return to_series(f(c.values, *args, axis=axis, **kwargs), index=index)
	return f(get_values(c), *args, axis=axis, **kwargs)


#########################

def concat_all(*args):
	return reduce(concat, *args)


def concat(c1, c2):
	'''Concatenates the specified collections.'''
	if is_table(c1) or is_table(c2):
		return concat_rows(c1, c2)
	elif is_dict(c1) or is_dict(c2):
		return dict(to_list(get_items(c1)) + to_list(get_items(c2)))
	elif is_ordered_set(c1) or is_ordered_set(c2):
		return to_ordered_set(c1).union(to_ordered_set(c2))
	elif is_set(c1) or is_set(c2):
		return to_set(c1).union(to_set(c2))
	elif is_array(c1) or is_array(c2):
		return np.append(to_array(c1), to_array(c2))
	return to_list(c1) + to_list(c2)


#########################

def fill_null(c, numeric_default=None, object_default=None,
              keys=None, inclusion=None, exclusion=None):
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	for k in keys:
		if is_frame(c):
			col = c.loc[:, k]
			if is_numeric_dtype(col.dtypes):
				fill_null_with(col, numeric_default, inplace=True)
			else:
				fill_null_with(col, object_default, inplace=True)
		elif is_series(c):
			if is_null(c.loc[k]):
				if is_number(c.loc[k]):
					c.loc[k] = numeric_default
				else:
					c.loc[k] = object_default
		else:
			if is_null(c[k]):
				if is_number(c[k]):
					c[k] = numeric_default
				else:
					c[k] = object_default
	return c


#########################

def filter(c,
           keys=None, inclusion=None, exclusion=None):
	'''Filters the specified collection by excluding the keys that are not in the specified
	inclusive collection and are in the specified exclusive collection.'''
	if is_empty(c) or not is_subscriptable_collection(c) or \
			not has_filter(keys=keys, inclusion=inclusion, exclusion=exclusion):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		if c.axis == 0:
			keys = get_index(c, inclusion=inclusion, exclusion=exclusion)
		return c.filter(lambda x: x.name in keys)
	elif is_frame(c):
		return c.loc[:, keys]
	elif is_series(c):
		return c.loc[keys]
	elif is_dict(c):
		return {k: c[k] for k in keys}
	elif is_array(c):
		return c[keys]
	return collection_to_type([c[k] for k in keys], c)


def include(c, inclusion):
	'''Filters the specified collection by excluding the keys that are not in the specified
	inclusive collection.'''
	return filter(c, inclusion=inclusion)


def exclude(c, exclusion):
	'''Filters the specified collection by excluding the keys that are in the specified exclusive
	collection.'''
	return filter(c, exclusion=exclusion)


#########################

def filter_index(c,
                 inclusion=None, exclusion=None):
	'''Filters the specified collection by excluding the index that are not in the specified
	inclusive collection and are in the specified exclusive collection.'''
	if is_empty(c) or not is_subscriptable_collection(c) or \
			not has_filter(inclusion=inclusion, exclusion=exclusion):
		return c
	index = get_index(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		if c.axis == 1:
			index = get_keys(c, inclusion=inclusion, exclusion=exclusion)
		return c.filter(lambda x: x.name in index)
	elif is_table(c):
		return c.loc[c.index.isin(index)]
	return filter(c, keys=index)


def include_index(c, inclusion):
	'''Filters the specified collection by excluding the index that are not in the specified
	inclusive collection.'''
	return filter_index(c, inclusion=inclusion)


def exclude_index(c, exclusion):
	'''Filters the specified collection by excluding the index that are in the specified exclusive
	collection.'''
	return filter_index(c, exclusion=exclusion)


#########################

def filter_with(c, f, *args,
                keys=None, inclusion=None, exclusion=None,
                **kwargs):
	'''Returns the entries of the specified collection whose values return True with the specified
	function for all the specified keys.'''
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		if c.axis == 0:
			keys = get_index(c, inclusion=inclusion, exclusion=exclusion)
		return c.filter(lambda x: x.name in keys and all_values(apply(x, f, *args, **kwargs)))
	elif is_table(c):
		mask = create_mask(c, *args, condition=f, keys=keys, **kwargs)
		if is_frame(c):
			return c.loc[reduce_and(mask, axis=1)]
		return c.loc[mask]
	elif is_dict(c):
		return {k: c[k] for k in keys if f(c[k], *args, **kwargs)}
	return collection_to_type([c[k] for k in keys if f(c[k], *args, **kwargs)], c)


def filter_not_with(c, f, *args,
                    keys=None, inclusion=None, exclusion=None,
                    **kwargs):
	'''Returns the entries of the specified collection whose values return False with the specified
	function for all the specified keys.'''
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		if c.axis == 0:
			keys = get_index(c, inclusion=inclusion, exclusion=exclusion)
		return c.filter(lambda x: x.name in keys and all_not_values(apply(x, f, *args, **kwargs)))
	elif is_table(c):
		mask = create_mask(c, condition=lambda x: not f(x, *args, **kwargs), keys=keys)
		if is_frame(c):
			return c.loc[reduce_and(mask, axis=1)]
		return c.loc[mask]
	elif is_dict(c):
		return {k: c[k] for k in keys if not f(c[k], *args, **kwargs)}
	return collection_to_type([c[k] for k in keys if not f(c[k], *args, **kwargs)], c)


def filter_any_with(c, f, *args,
                    keys=None, inclusion=None, exclusion=None,
                    **kwargs):
	'''Returns the entries of the specified collection whose values return True with the specified
	function for at least one specified key.'''
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		if c.axis == 0:
			keys = get_index(c, inclusion=inclusion, exclusion=exclusion)
		return c.filter(lambda x: x.name in keys and any_values(apply(x, f, *args, **kwargs)))
	elif is_table(c):
		mask = create_mask(c, *args, condition=f, keys=keys, **kwargs)
		if is_frame(c):
			return c.loc[reduce_or(mask, axis=1)]
		return c.loc[mask]
	elif is_dict(c):
		return {k: c[k] for k in keys if f(c[k], *args, **kwargs)}
	return collection_to_type([c[k] for k in keys if f(c[k], *args, **kwargs)], c)


def filter_any_not_with(c, f, *args,
                        keys=None, inclusion=None, exclusion=None,
                        **kwargs):
	'''Returns the entries of the specified collection whose values return False with the specified
	function for at least one specified key.'''
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if is_group(c):
		if c.axis == 0:
			keys = get_index(c, inclusion=inclusion, exclusion=exclusion)
		return c.filter(lambda x: x.name in keys and any_not_values(apply(x, f, *args, **kwargs)))
	elif is_table(c):
		mask = create_mask(c, condition=lambda x: not f(x, *args, **kwargs), keys=keys)
		if is_frame(c):
			return c.loc[reduce_or(mask, axis=1)]
		return c.loc[mask]
	elif is_dict(c):
		return {k: c[k] for k in keys if not f(c[k], *args, **kwargs)}
	return collection_to_type([c[k] for k in keys if not f(c[k], *args, **kwargs)], c)


#########################

def filter_null(c,
                keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are null for all the specified
	keys.'''
	return filter_with(c, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_null(c,
                    keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are not null for all the
	specified keys.'''
	return filter_not_with(c, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_null(c,
                    keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are null for at least one
	specified key.'''
	return filter_any_with(c, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_null(c,
                        keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are not null for at least one
	specified key.'''
	return filter_any_not_with(c, is_null, keys=keys, inclusion=inclusion, exclusion=exclusion)


#########################

def filter_empty(c,
                 keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are empty for all the specified
	keys.'''
	return filter_with(c, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_empty(c,
                     keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are not empty for all the
	specified keys.'''
	return filter_not_with(c, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_empty(c,
                     keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are empty for at least one
	specified key.'''
	return filter_any_with(c, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_empty(c,
                         keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are not empty for at least one
	specified key.'''
	return filter_any_not_with(c, is_empty, keys=keys, inclusion=inclusion, exclusion=exclusion)


#########################

def filter_value(c, value,
                 keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are equal to the specified value
	 for all the specified keys.'''
	return filter_with(c, lambda v: v == value,
	                   keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_value(c, value,
                     keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are not equal to the specified
	value for all the specified keys.'''
	return filter_not_with(c, lambda v: v == value,
	                       keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_value(c, value,
                     keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are equal to the specified value
	for at least one specified key.'''
	return filter_any_with(c, lambda v: v == value,
	                       keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_value(c, value,
                         keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are not equal to the specified
	value for at least one specified key.'''
	return filter_any_not_with(c, lambda v: v == value,
	                           keys=keys, inclusion=inclusion, exclusion=exclusion)


#########################

def filter_in(c, values,
              keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are in the specified values for
	all the specified keys.'''
	values = to_set(values)
	return filter_with(c, lambda v: v in values,
	                   keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_in(c, values,
                  keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are not in the specified values
	for all the specified keys.'''
	values = to_set(values)
	return filter_not_with(c, lambda v: v in values,
	                       keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_in(c, values,
                  keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are in the specified values for
	at least one specified key.'''
	values = to_set(values)
	return filter_any_with(c, lambda v: v in values,
	                       keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_in(c, values,
                      keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are not in the specified values
	for at least one specified key.'''
	values = to_set(values)
	return filter_any_not_with(c, lambda v: v in values,
	                           keys=keys, inclusion=inclusion, exclusion=exclusion)


#########################

def filter_between(c, lower=None, upper=None,
                   keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are lying between the lower
	(inclusive) and upper (exclusive) bounds for all the specified keys.'''
	if is_all_null(lower, upper):
		return c
	elif is_null(lower):
		return filter_with(c, lambda v: v < upper,
		                   keys=keys, inclusion=inclusion, exclusion=exclusion)
	elif is_null(upper):
		return filter_with(c, lambda v: v >= lower,
		                   keys=keys, inclusion=inclusion, exclusion=exclusion)
	return filter_with(c, lambda v: lower <= v < upper,
	                   keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_not_between(c, lower=None, upper=None,
                       keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are not lying between the lower
	(inclusive) and upper (exclusive) bounds for all the specified keys.'''
	if is_all_null(lower, upper):
		return c
	elif is_null(lower):
		return filter_not_with(c, lambda v: v < upper,
		                       keys=keys, inclusion=inclusion, exclusion=exclusion)
	elif is_null(upper):
		return filter_not_with(c, lambda v: v >= lower,
		                       keys=keys, inclusion=inclusion, exclusion=exclusion)
	return filter_not_with(c, lambda v: lower <= v < upper,
	                       keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_between(c, lower=None, upper=None,
                       keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are lying between the lower
	(inclusive) and upper (exclusive) bounds for at least one specified key.'''
	if is_all_null(lower, upper):
		return c
	elif is_null(lower):
		return filter_any_with(c, lambda v: v < upper,
		                       keys=keys, inclusion=inclusion, exclusion=exclusion)
	elif is_null(upper):
		return filter_any_with(c, lambda v: v >= lower,
		                       keys=keys, inclusion=inclusion, exclusion=exclusion)
	return filter_any_with(c, lambda v: lower <= v < upper,
	                       keys=keys, inclusion=inclusion, exclusion=exclusion)


def filter_any_not_between(c, lower=None, upper=None,
                           keys=None, inclusion=None, exclusion=None):
	'''Returns the entries of the specified collection whose values are not lying between the lower
	(inclusive) and upper (exclusive) bounds for at least one specified key.'''
	if is_all_null(lower, upper):
		return c
	elif is_null(lower):
		return filter_any_not_with(c, lambda v: v < upper,
		                           keys=keys, inclusion=inclusion, exclusion=exclusion)
	elif is_null(upper):
		return filter_any_not_with(c, lambda v: v >= lower,
		                           keys=keys, inclusion=inclusion, exclusion=exclusion)
	return filter_any_not_with(c, lambda v: lower <= v < upper,
	                           keys=keys, inclusion=inclusion, exclusion=exclusion)


#########################

def filter_days(c, days, week=False, year=False):
	'''Filters the collection by matching its date-time index with the specified days (week days
	if week is True, days of the year if year is True, days of the month otherwise).'''
	indices = find_all_in(get_days(c, week=week, year=year), get_days(days, week=week, year=year))
	return take_at(c, indices)


def filter_weeks(c, weeks):
	'''Filters the collection by matching its date-time index with the specified weeks.'''
	indices = find_all_in(get_weeks(c), get_weeks(weeks))
	return take_at(c, indices)


def filter_year_weeks(c, year_weeks):
	'''Filters the collection by matching its date-time index with the specified year-weeks.'''
	indices = find_all_in(get_year_weeks(c), get_year_weeks(year_weeks))
	return take_at(c, indices)


def filter_months(c, months):
	'''Filters the collection by matching its date-time index with the specified months.'''
	indices = find_all_in(get_months(c), get_months(months))
	return take_at(c, indices)


def filter_quarters(c, quarters):
	'''Filters the collection by matching its date-time index with the specified quarters.'''
	indices = find_all_in(get_quarters(c), get_quarters(quarters))
	return take_at(c, indices)


def filter_semesters(c, semesters):
	'''Filters the collection by matching its date-time index with the specified semesters.'''
	indices = find_all_in(get_semesters(c), get_semesters(semesters))
	return take_at(c, indices)


def filter_years(c, years):
	'''Filters the collection by matching its date-time index with the specified years.'''
	indices = find_all_in(get_years(c), get_years(years))
	return take_at(c, indices)


#########################

def flatten(c, type=None,
            axis=0):
	if is_empty(c):
		return to_array(type=type)
	if type == OBJECT_TYPE:
		return to_array(flatten_list(c), type=type)
	return get_values(c, type=type).flatten(order='C' if axis == 0 else 'F' if axis == 1 else 'A')


#########################

def groupby(c, group=GROUP, dof=1,
            axis=0):
	if group is Group.COUNT:
		return count(c, axis=axis)
	elif group is Group.FIRST:
		return get_first(c, axis=axis)
	elif group is Group.LAST:
		return get_last(c, axis=axis)
	elif group is Group.MIN:
		return minimum(c, axis=axis)
	elif group is Group.MAX:
		return maximum(c, axis=axis)
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


def count(*args,
          axis=0):
	c = forward(*args)
	if not is_collection(c):
		return 1
	if is_null(axis):
		return np.size(get_values(c))
	if is_group(c):
		return c.count()
	elif is_frame(c):
		return c.count(axis=axis)
	elif is_array(c):
		return np.apply_along_axis(len, axis, c)
	return len(c)


def minimum(*args,
            axis=0):
	c = forward(*args)
	if is_null(axis):
		return np.min(get_values(c))
	if is_group(c):
		return c.min()
	elif is_dict(c):
		c = get_values(c)
	return np.min(c, axis=axis)


def maximum(*args,
            axis=0):
	c = forward(*args)
	if is_null(axis):
		return np.max(get_values(c))
	if is_group(c):
		return c.max()
	elif is_dict(c):
		c = get_values(c)
	return np.max(c, axis=axis)


def mean(*args,
         axis=0):
	c = forward(*args)
	if is_null(axis):
		return np.mean(get_values(c))
	if is_group(c):
		return c.mean()
	elif is_dict(c):
		c = get_values(c)
	return np.mean(c, axis=axis)


def median(*args,
           axis=0):
	c = forward(*args)
	if is_null(axis):
		return np.median(get_values(c))
	if is_group(c):
		return c.median()
	elif is_dict(c):
		c = get_values(c)
	return np.median(c, axis=axis)


def std(*args, dof=1,
        axis=0):
	c = forward(*args)
	if is_null(axis):
		return np.std(get_values(c), ddof=dof)
	if is_group(c):
		return c.std(ddof=dof)
	elif is_dict(c):
		c = get_values(c)
	return np.std(c, axis=axis, ddof=dof)


def var(*args, dof=1,
        axis=0):
	c = forward(*args)
	if is_null(axis):
		return np.var(get_values(c), ddof=dof)
	if is_group(c):
		return c.var(ddof=dof)
	elif is_dict(c):
		c = get_values(c)
	return np.var(c, axis=axis, ddof=dof)


def sum(*args,
        axis=0):
	c = forward(*args)
	if is_null(axis):
		return np.sum(get_values(c))
	if is_group(c):
		return c.sum()
	elif is_dict(c):
		c = get_values(c)
	return np.sum(c, axis=axis)


#########################

def insert_all(*args):
	return reduce(insert, *args)


def insert(c1, c2,
           keys=None, inclusion=None, exclusion=None):
	'''Inserts the specified second collection into the first collection by inserting the values
	whose keys, which are in the specified inclusive list and are not in the specified exclusive
	list, or indices are different.'''
	# - Insert the rows
	insert_rows(c1, c2, keys=keys, inclusion=inclusion, exclusion=exclusion)
	# - Insert the columns
	return insert_cols(c1, c2, keys=keys, inclusion=inclusion, exclusion=exclusion)


def insert_rows(c1, c2,
                keys=None, inclusion=None, exclusion=None):
	'''Inserts the specified second collection into the first collection by inserting the values
	whose keys, which are in the specified inclusive list and are not in the specified exclusive
	list, are identical and indices are different.'''
	if is_table(c2) or is_dict(c2):
		c2 = exclude_index(c2, c1)
	return upsert_rows(c1, c2, keys=keys, inclusion=inclusion, exclusion=exclusion)


def insert_cols(c1, c2,
                keys=None, inclusion=None, exclusion=None):
	'''Inserts the specified second collection into the first collection by inserting the values
	whose keys, which are in the specified inclusive list and are not in the specified exclusive
	list, are different.'''
	if not is_table(c1) and not is_dict(c1):
		return c1
	if is_null(keys):
		keys = get_uncommon_keys(c2, c1, inclusion=inclusion, exclusion=exclusion)
	c2 = collection_to_common_type(filter(c2, keys=keys), c1)
	if is_table(c1):
		c1.update(c2.fillna(NA_NAME))
		c1.replace(NA_NAME, NAN, inplace=True)
	else:
		c1.update(c2)
	return c1


#########################

def keep_min(c, n, group=None,
             axis=0):
	g = groupby(c, group=group, axis=axis) if not is_group(c) and not is_null(group) else c
	if is_number(g):
		return g
	keys = [t[1] for t in sorted(zip(g, get_keys_or_index(g, axis=axis)))[:n]]
	return take(c, keys, axis=1 if (is_frame(c) or is_array(c)) and axis == 0 else 0)


def keep_min_with(c, n, f,
                  axis=0):
	return keep_min(apply(c, f, axis=axis), n)


def keep_max(c, n, group=None,
             axis=0):
	g = groupby(c, group=group, axis=axis) if not is_group(c) and not is_null(group) else c
	if is_number(g):
		return g
	keys = [t[1] for t in sorted(zip(g, get_keys_or_index(g, axis=axis)), reverse=True)[:n]]
	return take(c, keys, axis=1 if (is_frame(c) or is_array(c)) and axis == 0 else 0)


def keep_max_with(c, n, f,
                  axis=0):
	return keep_max(apply(c, f, axis=axis), n)


#########################

def remove_null(c, conservative=True,
                axis=0,
                keys=None, inclusion=None, exclusion=None):
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if axis == 0:
		if conservative:
			return filter_any_not_null(c, keys=keys)
		return filter_not_null(c, keys=keys)
	for k in keys:
		if is_all_null(c[k]) if conservative else is_any_null(c[k]):
			c = remove_col(c, names=k)
	return c


def remove_empty(c, conservative=True,
                 axis=0,
                 keys=None, inclusion=None, exclusion=None):
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if axis == 0:
		if conservative:
			return filter_any_not_empty(c, keys=keys)
		return filter_not_empty(c, keys=keys)
	for k in keys:
		if is_all_empty(c[k]) if conservative else is_any_empty(c[k]):
			c = remove_col(c, names=k)
	return c


def remove_value(c, value, conservative=True,
                 axis=0,
                 keys=None, inclusion=None, exclusion=None):
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	if axis == 0:
		if conservative:
			return filter_any_not_value(c, value, keys=keys)
		return filter_not_value(c, value, keys=keys)
	for k in keys:
		if is_all_value(value, c[k]) if conservative else is_any_value(value, c[k]):
			c = remove_col(c, names=k)
	return c


#########################

def reverse(c,
            axis=0):
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_table(c):
		if axis == 0:
			return c.loc[::-1]
		return c.loc[:, ::-1]
	elif is_dict(c):
		return {c[k]: k for k in c}
	return c[::-1]


#########################

def shift_dates(c, years=0, months=0, weeks=0, days=0, hours=0, minutes=0, seconds=0,
                microseconds=0):
	'''Shifts the date-time index of the specified collection.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_table(c):
		t = c.copy()
		t.index += pd.DateOffset(years=years, months=months, weeks=weeks, days=days, hours=hours,
		                         minutes=minutes, seconds=seconds, microseconds=microseconds)
		return t
	elif is_dict(c):
		return {shift_date(d, years=years, months=months, weeks=weeks, days=days, hours=hours,
		                   minutes=minutes, seconds=seconds, microseconds=microseconds):
			        c[d] for d in c}
	return collection_to_type([shift_date(d, years=years, months=months, weeks=weeks, days=days,
	                                      hours=hours, minutes=minutes, seconds=seconds,
	                                      microseconds=microseconds) for d in c], c)


#########################

def simplify(c):
	if is_collection(c):
		if len(c) == 1:
			return simplify(get_next(c))
	return c


#########################

def slice(c, index_from=None, index_to=None,
          axis=0):
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_null(index_from):
		index_from = 0
	if is_null(index_to):
		index_to = len(c)
	keys = get_index_or_keys(c, axis=axis)
	return take(c, keys[index_from:index_to], axis=axis)


#########################

def sort(c, ascending=True, by=None, inplace=False,
         axis=0):
	'''Sorts the values of the specified collection.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_frame(c):
		return c.sort_values(get_keys(c, inclusion=by), ascending=ascending, inplace=inplace,
		                     axis=axis)
	elif is_series(c):
		return c.sort_values(ascending=ascending, inplace=inplace)
	elif is_dict(c):
		return c
	if inplace:
		return c.sort()
	return sorted(c)


def sort_index(c):
	'''Sorts the index of the specified collection.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_table(c):
		return c.sort_index()
	return c


#########################

def take(c, keys,
         axis=0):
	'''Returns the entries of the specified collection for all the specified keys.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	keys = to_ordered_set(keys)
	if is_table(c):
		if axis == 0:
			return c.loc[keys]
		return c.loc[:, keys]
	return filter(c, keys=keys)


def take_not(c, keys,
             axis=0):
	'''Returns the entries of the specified collection except for all the specified keys.'''
	indices = find_all_not_in(get_index_or_keys(c, axis=axis), keys)
	return take_at(c, indices, axis=axis)


def take_at(c, indices,
            axis=0):
	'''Returns the entries of the specified collection that are at the specified indices.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	indices = to_list(indices)
	if is_table(c):
		if axis == 0:
			return c.iloc[indices]
		return c.iloc[:, indices]
	elif is_dict(c):
		return {k: c[k] for i, k in enumerate(c) if i in indices or i - len(c) in indices}
	return collection_to_type([c[i] for i in indices], c)


def take_not_at(c, indices,
                axis=0):
	'''Returns the entries of the specified collection that are not at the specified indices.'''
	indices = find_all_not_in(range(count_rows(c) if axis == 0 else count_cols(c)), indices)
	return take_at(c, indices, axis=axis)


#########################

def tally(c, boundaries):
	'''Tallies the values of the specified collection into the intervals delimited by the specified
	boundaries.'''
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_empty(boundaries):
		return repeat(0, len(c))
	tc = c.copy()
	lower = minimum(tc, axis=None)
	for i, upper in enumerate(boundaries):
		set_values(tc, i, mask=create_mask(c, condition=lambda v: lower <= v < upper))
		lower = upper
	set_values(tc, i + 1, mask=create_mask(c, condition=lambda v: v >= upper))
	return tc


#########################

def unique(c, group=GROUP):
	if is_group(c):
		c = c.obj if c.axis == 0 else c.groups
	if is_table(c):
		return c.loc[invert(c.index.duplicated(keep='first' if group is Group.FIRST else 'last'))]
	elif is_dict(c):
		return c
	return to_list(dict.fromkeys(c))


#########################

def update_all(*args,
               keys=None, inclusion=None, exclusion=None):
	return reduce(lambda c1, c2: update(c1, c2,
	                                    keys=keys, inclusion=inclusion, exclusion=exclusion),
	              *args)


def update(c1, c2,
           keys=None, inclusion=None, exclusion=None):
	'''Updates the specified first collection with the specified second collection by updating the
	values whose keys, which are in the specified inclusive list and are not in the specified
	exclusive list, and indices are identical.'''
	# - Update the rows
	if is_table(c2) or is_dict(c2):
		c2 = include_index(c2, c1)
	return upsert_rows(c1, c2, keys=keys, inclusion=inclusion, exclusion=exclusion)


#########################

def upsert_all(*args,
               keys=None, inclusion=None, exclusion=None):
	return reduce(lambda c1, c2: upsert(c1, c2,
	                                    keys=keys, inclusion=inclusion, exclusion=exclusion),
	              *args)


def upsert(c1, c2,
           keys=None, inclusion=None, exclusion=None):
	'''Upserts the specified first collection with the specified second collection by updating or
	inserting the values whose keys are in the specified inclusive list and are not in the specified
	exclusive list.'''
	if is_null(keys):
		keys = get_keys(c2, inclusion=inclusion, exclusion=exclusion)
	c2 = collection_to_common_type(filter(c2, keys=keys), c1)
	return insert(update(c1, c2), c2)


def upsert_rows(c1, c2,
                keys=None, inclusion=None, exclusion=None):
	'''Upserts the specified first collection with the specified second collection by updating or
	inserting the values whose keys, which are in the specified inclusive list and are not in the
	specified exclusive list, are identical.'''
	if is_null(keys):
		keys = get_common_keys(c2, c1, inclusion=inclusion, exclusion=exclusion)
	c2 = collection_to_common_type(filter(c2, keys=keys), c1)
	if is_table(c1):
		c1.update(c2.fillna(NA_NAME))
		c1.replace(NA_NAME, NAN, inplace=True)
	elif is_dict(c1):
		c1.update(c2)
	else:
		for k in keys:
			c1[k] = c2[k]
	return c1


#########################

def where(c, *args, condition=lambda x, *args, **kwargs: True,
          keys=None, inclusion=None, exclusion=None,
          **kwargs):
	if is_empty(c) or not is_subscriptable_collection(c):
		return c
	if is_null(keys):
		keys = get_keys(c, inclusion=inclusion, exclusion=exclusion)
	return [k for k in keys if condition(c[k], *args, **kwargs)]


# • CONSOLE ########################################################################################

__CONSOLE_PROCESSORS______________________________ = ''


def trace(*args, level=0):
	if SEVERITY_LEVEL.value >= 7:
		print(collapse('[', get_datetime_string(), '][TRAC]',
		               '[', get_script_name(level + 1), ']',
		               '[', get_function_name(level + 1), ']',
		               '[', get_line_number(level + 1), '] ',
		               paste(*args)))


def debug(*args, level=0):
	if SEVERITY_LEVEL.value >= 6:
		print(collapse('[', get_datetime_string(), '][DEBU]',
		               '[', get_script_name(level + 1), ']',
		               '[', get_function_name(level + 1), '] ',
		               paste(*args)))


def test(*args, level=0):
	if SEVERITY_LEVEL.value >= 5:
		print(collapse('[', get_datetime_string(), '][TEST]',
		               '[', get_script_name(level + 1), '] ',
		               paste(*args)))


def info(*args):
	if SEVERITY_LEVEL.value >= 4:
		print(collapse('[', get_datetime_string(), '][INFO] ',
		               paste(*args)))


def result(*args):
	if SEVERITY_LEVEL.value >= 3:
		print(paste(*args))


def warn(*args, level=0):
	if SEVERITY_LEVEL.value >= 2:
		print(collapse('[', get_datetime_string(), '][WARN]',
		               '[', get_script_name(level + 1), '] ',
		               paste(*args)), file=sys.stderr)


def error(*args, level=0):
	if SEVERITY_LEVEL.value >= 1:
		print(collapse('[', get_datetime_string(), '][ERRO]',
		               '[', get_script_name(level + 1), ']',
		               '[', get_function_name(level + 1), '] ',
		               paste(*args)), file=sys.stderr)


def fail(*args, level=0):
	if SEVERITY_LEVEL.value >= 0:
		print(collapse('[', get_datetime_string(), '][FAIL]',
		               '[', get_script_name(level + 1), ']',
		               '[', get_function_name(level + 1), ']',
		               '[', get_line_number(level + 1), '] ',
		               paste(*args)), file=sys.stderr)


# • DATAFRAME ######################################################################################

__DATAFRAME_PROCESSORS____________________________ = ''


def combine_all(*args, f):
	return reduce(lambda left, right: combine(left, right, f), *args)


def combine(left, right, f):
	'''Combines the specified left dataframe with the specified right dataframe with the specified
	function on the common columns (or on the specified columns if they are not null).'''
	return to_frame(left).combine(to_frame(right), f)


#########################

def concat_rows(*rows):
	'''Concatenates the specified rows to a dataframe.'''
	rows = remove_empty(to_collection(*rows))
	if is_empty(rows):
		return to_series(rows)
	df = pd.concat([to_frame(row) for row in rows], axis=0)
	if count_cols(df) == 1:
		return to_series(df)
	return df


def concat_cols(*cols):
	'''Concatenates the specified columns to a dataframe.'''
	cols = remove_empty(to_collection(*cols))
	if is_empty(cols):
		return to_series(cols)
	df = pd.concat(cols, axis=1)
	if count_cols(df) == 1:
		return to_series(df)
	return df


#########################

def count_rows(df):
	'''Counts the rows of the specified dataframe.'''
	if is_group(df):
		if df.axis == 0:
			return len(df.groups)
		df = df.obj
	if is_series(df):
		return len(df)
	shape = np.shape(df)
	return shape[0] if len(shape) >= 1 else 0


def count_cols(df):
	'''Counts the columns of the specified dataframe.'''
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
	return fill_null(sort_index(df.reindex(columns=unique(get_names(df) + get_names(model)),
	                                       index=unique(get_index(df) + get_index(model)))),
	                 numeric_default=numeric_default, object_default=object_default)


def fill_null_rows(df, index, numeric_default=None, object_default=None):
	if is_table(index):
		index = get_index(index)
	return fill_null(sort_index(df.reindex(index=unique(get_index(df) + to_list(index)))),
	                 numeric_default=numeric_default, object_default=object_default)


def fill_null_cols(df, names, numeric_default=None, object_default=None):
	if is_table(names):
		names = get_names(names)
	return fill_null(sort_index(df.reindex(columns=unique(get_names(df) + to_list(names)))),
	                 numeric_default=numeric_default, object_default=object_default)


#########################

def filter_rows_with(df, row, f, *args, **kwargs):
	'''Returns the rows of the specified dataframe whose values return True with the specified
	row and function for all the specified keys.'''
	if is_empty(df):
		return df
	return df.loc[reduce_and([apply(df[k], f, v, *args, **kwargs)
	                          for k, v in row.items() if k in df])]


def filter_rows_not_with(df, row, f, *args, **kwargs):
	'''Returns the rows of the specified dataframe whose values return False with the specified
	row and function for all the specified keys.'''
	if is_empty(df):
		return df
	return df.loc[reduce_and([invert(apply(df[k], f, v, *args, **kwargs))
	                          for k, v in row.items() if k in df])]


def filter_any_rows_with(df, row, f, *args, **kwargs):
	'''Returns the rows of the specified dataframe whose values return True with the specified
	row and function for at least one specified key.'''
	if is_empty(df):
		return df
	return df.loc[reduce_or([apply(df[k], f, v, *args, **kwargs)
	                         for k, v in row.items() if k in df])]


def filter_any_rows_not_with(df, row, f, *args, **kwargs):
	'''Returns the rows of the specified dataframe whose values return False with the specified
	row and function for at least one specified key.'''
	if is_empty(df):
		return df
	return df.loc[reduce_or([invert(apply(df[k], f, v, *args, **kwargs))
	                         for k, v in row.items() if k in df])]


#########################

def filter_rows(df, row):
	'''Returns the rows of the specified dataframe that match the specified row for all the common
	columns.'''
	if is_empty(df) or is_null(row):
		return df
	return df.loc[reduce_and([df[k] == v for k, v in row.items() if k in df])]


def filter_rows_not(df, row):
	'''Returns the rows of the specified dataframe that do not match the specified row for all the
	common columns.'''
	if is_empty(df) or is_null(row):
		return df
	return df.loc[reduce_and([df[k] != v for k, v in row.items() if k in df])]


def filter_any_rows(df, row):
	'''Returns the rows of the specified dataframe that match the specified row for at least one
	common column.'''
	if is_empty(df) or is_null(row):
		return df
	return df.loc[reduce_or([df[k] == v for k, v in row.items() if k in df])]


def filter_any_rows_not(df, row):
	'''Returns the rows of the specified dataframe that do not match the specified row for at least
	one common column.'''
	if is_empty(df) or is_null(row):
		return df
	return df.loc[reduce_or([df[k] != v for k, v in row.items() if k in df])]


#########################

def filter_rows_in(df, rows):
	'''Returns the rows of the specified dataframe that match the specified rows for all the common
	columns.'''
	if is_empty(df) or is_null(rows):
		return df
	return df.loc[reduce_and([df[k].isin(to_set(values))
	                          for k, values in rows.items() if k in df])]


def filter_rows_not_in(df, rows):
	'''Returns the rows of the specified dataframe that do not match the specified rows for all the
	common columns.'''
	if is_empty(df) or is_null(rows):
		return df
	return df.loc[reduce_and([invert(df[k].isin(to_set(values)))
	                          for k, values in rows.items() if k in df])]


def filter_any_rows_in(df, rows):
	'''Returns the rows of the specified dataframe that match the specified rows for at least one
	common column.'''
	if is_empty(df) or is_null(rows):
		return df
	return df.loc[reduce_or([df[k].isin(to_set(values))
	                         for k, values in rows.items() if k in df])]


def filter_any_rows_not_in(df, rows):
	'''Returns the rows of the specified dataframe that do not match the specified rows for at least
	one common column.'''
	if is_empty(df) or is_null(rows):
		return df
	return df.loc[reduce_or([invert(df[k].isin(to_set(values)))
	                         for k, values in rows.items() if k in df])]


#########################

def join_all(*args, how='inner', on=None, suffix=' 2'):
	return reduce(lambda left, right: join(left, right, how=how, on=on, suffix=suffix), *args)


def join(left, right, how='inner', on=None, suffix=' 2'):
	'''Joins the specified left dataframe with the specified right dataframe on the common columns
	(or on the specified columns if they are not null).'''
	return to_frame(left).join(to_frame(right), how=how, on=on, rsuffix=suffix)


#########################

def merge_all(*args, how='inner', on=None):
	return reduce(lambda left, right: merge(left, right, how=how, on=on), *args)


def merge(left, right, how='inner', on=None):
	'''Merges the specified left dataframe with the specified right dataframe on the common columns
	(or on the specified columns if they are not null).'''
	return to_frame(left).merge(to_frame(right), how=how, on=on)


#########################

def pivot(df, names, index, values):
	return df.pivot(columns=names, index=index, values=values)


def unpivot(df, names, value):
	df = df.unstack().reset_index(name=value)
	df.rename(columns={'level_' + str(i): name for i, name in enumerate(to_list(names))},
	          inplace=True)
	return df


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
	if is_all_empty(names, index):
		set_names(df, range(count_cols(df)))
	else:
		if not is_null(names):
			set_names(df, names)
		if not is_null(index):
			df.rename(index=index, level=level, copy=False, inplace=True)
	return df


def rename_all(*args, names=None, index=None, level=None):
	for arg in args:
		rename(arg, names=names, index=index, level=level)


#########################

def reset_index(df):
	return df.reset_index(level=0)


#########################

def rotate_rows(df, drop=True, prepend=False):
	'''Rotates the rows of the specified dataframe.'''
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
	'''Rotates the columns of the specified dataframe.'''
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
	'''Returns the sum of the rows of the specified dataframe.'''
	return df.sum(axis=0)


def sum_cols(df):
	'''Returns the sum of the columns of the specified dataframe.'''
	return df.sum(axis=1)


def product_rows(df):
	'''Returns the product of the rows of the specified dataframe.'''
	return df.product(axis=0)


def product_cols(df):
	'''Returns the product of the columns of the specified dataframe.'''
	return df.product(axis=1)


# • DATE ###########################################################################################

__DATE_PROCESSORS_________________________________ = ''


def add_period(d=get_datetime(), period=PERIOD):
	period_length = to_period_length(period)
	period_freq = to_period_freq(period)
	return d + period_length * FREQUENCY_DELTA[period_freq]


def subtract_period(d=get_datetime(), period=PERIOD):
	period_length = to_period_length(period)
	period_freq = to_period_freq(period)
	return d - period_length * FREQUENCY_DELTA[period_freq]


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

def find_nearest_period(length, freq=FREQUENCY):
	day_count = get_period_days(None, period=to_period(length, freq=freq))
	period_freq = DAY_COUNT_FREQUENCY[nearest(DAY_COUNT_FREQUENCY, day_count)]
	period_length = round_to_int(day_count / FREQUENCY_DAY_COUNT[period_freq])
	return to_period(period_length, period_freq)


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

def shift_date(d=get_datetime(), years=0, months=0, weeks=0, days=0, hours=0, minutes=0, seconds=0,
               microseconds=0):
	return timestamp_to_type(d + pd.DateOffset(years=years, months=months, weeks=weeks, days=days,
	                                           hours=hours, minutes=minutes, seconds=seconds,
	                                           microseconds=microseconds), d)


# • LIST ###########################################################################################

__LIST_PROCESSORS_________________________________ = ''


def filter_list(l,
                inclusion=None, exclusion=None):
	'''Returns the values of the specified list that are in the specified inclusive list and are not
	in the specified exclusive list.'''
	if is_empty(l):
		return []
	if not has_filter(inclusion=inclusion, exclusion=exclusion):
		return to_list(l)
	elif is_null(inclusion):
		return [v for v in l if v not in to_list(exclusion)]
	elif is_empty(exclusion):
		return [v for v in l if v in to_list(inclusion)]
	return [v for v in l if v in to_list(inclusion) and v not in to_list(exclusion)]


def include_list(l, inclusion):
	'''Returns the values of the specified list that are in the specified inclusive list.'''
	return filter_list(l, inclusion=inclusion)


def exclude_list(l, exclusion):
	'''Returns the values of the specified list that are not in the specified exclusive list.'''
	return filter_list(l, exclusion=exclusion)


#########################

def flatten_list(l, depth=-1):
	if is_empty(l):
		return []
	if not is_collection(l) or depth == 0:
		return to_list(l)
	elif depth == 1:
		return [v for sl in l for v in sl]
	fl = []
	for sl in l:
		fl += flatten_list(sl, depth=depth - 1)
	return fl


#########################

def find_all(l, value):
	return find_all_with(l, lambda v: v == value)


def find_all_not(l, value):
	return find_all_not_with(l, lambda v: v == value)


def find_all_in(l, values):
	values = to_set(values)
	return find_all_with(l, lambda v: v in values)


def find_all_not_in(l, values):
	values = to_set(values)
	return find_all_not_with(l, lambda v: v in values)


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
	values = to_set(values)
	return find_with(l, lambda v: v in values)


def find_not_in(l, values):
	values = to_set(values)
	return find_not_with(l, lambda v: v in values)


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
	values = to_set(values)
	return find_last_with(l, lambda v: v in values)


def find_last_not_in(l, values):
	values = to_set(values)
	return find_last_not_with(l, lambda v: v in values)


def find_last_with(l, f, *args, **kwargs):
	return len(l) - find_with(l[::-1], f, *args, **kwargs) - 1


def find_last_not_with(l, f, *args, **kwargs):
	return len(l) - find_not_with(l[::-1], f, *args, **kwargs) - 1


#########################

def mask_list(l, mask):
	'''Returns the values of the specified list that are True in the specified mask.'''
	return [v for i, v in enumerate(l) if mask[i]]


#########################

def resize_list(l, size, left=False, value=None):
	if len(l) < size:
		values = repeat(value, size - len(l))
		if left:
			return values + l
		return l + values
	return l[0:size]


#########################

def repeat(value, n):
	return n * [value]


#########################

def rotate_list(l, n=1):
	return l[-n:] + l[:-n]


# • NUMBER #########################################################################################

__NUMBER_PROCESSORS_______________________________ = ''


def ceil(x):
	return to_int(np.ceil(x))


def floor(x):
	return to_int(np.floor(x))


def round(x, decimals=DEFAULT_MAX_DECIMALS):
	if decimals == 0:
		return round_to_int(x)
	return np.round(x, decimals=decimals)


def round_to_int(x):
	return to_int(np.round(x))


#########################

def mod(x, y):
	m = x % y
	return y if m == 0 else m


#########################

def nearest(c, value):
	if is_empty(c):
		return None
	elif is_series(c) or is_array(c):
		return get(c, abs(c - value).argmin())
	return min(to_list(c), key=lambda x: abs(x - value))


def farthest(c, value):
	if is_empty(c):
		return None
	elif is_series(c) or is_array(c):
		return get(c, abs(c - value).argmax())
	return max(to_list(c), key=lambda x: abs(x - value))


# • PROFILE ########################################################################################

__PROFILE_PROCESSORS______________________________ = ''


def start_profile():
	'''Starts profiling.'''
	profile = cProfile.Profile()
	profile.enable()
	return profile


def end_profile(profile, stream=StringIO()):
	'''Ends profiling and returns the stream.'''
	profile.disable()
	pstats.Stats(profile, stream=stream).sort_stats(SortKey.CUMULATIVE).print_stats()
	return stream


# • SET ############################################################################################

__SET_PROCESSORS__________________________________ = ''


def filter_set(s,
               inclusion=None, exclusion=None):
	'''Returns the values of the specified set that are in the specified inclusive set and are not
	in the specified exclusive set.'''
	if is_empty(s):
		return set()
	if not has_filter(inclusion=inclusion, exclusion=exclusion):
		return to_set(s)
	elif is_null(inclusion):
		return to_set(s) - to_set(exclusion)
	elif is_empty(exclusion):
		return to_set(s) & to_set(inclusion)
	return to_set(s) & to_set(inclusion) - to_set(exclusion)


def include_set(s, inclusion):
	'''Returns the values of the specified set that are in the specified inclusive set.'''
	return filter_set(s, inclusion=inclusion)


def exclude_set(s, exclusion):
	'''Returns the values of the specified set that are not in the specified exclusive set.'''
	return filter_set(s, exclusion=exclusion)


#########################

def filter_ordered_set(s,
                       inclusion=None, exclusion=None):
	'''Returns the values of the specified ordered set that are in the specified inclusive set and
	are not in the specified exclusive set.'''
	if is_empty(s):
		return OrderedSet()
	if not has_filter(inclusion=inclusion, exclusion=exclusion):
		return to_ordered_set(s)
	elif is_null(inclusion):
		return to_ordered_set(s) - to_set(exclusion)
	elif is_empty(exclusion):
		return to_ordered_set(s) & to_set(inclusion)
	return to_ordered_set(s) & to_set(inclusion) - to_set(exclusion)


def include_ordered_set(s, inclusion):
	'''Returns the values of the specified ordered set that are in the specified inclusive set.'''
	return filter_ordered_set(s, inclusion=inclusion)


def exclude_ordered_set(s, exclusion):
	'''Returns the values of the specified ordered set that are not in the specified exclusive
	set.'''
	return filter_ordered_set(s, exclusion=exclusion)


# • STRING #########################################################################################

__STRING_PROCESSORS_______________________________ = ''


def collapse(*args, delimiter='', append=False):
	'''Returns the string computed by joining the specified arguments with the specified
	delimiter.'''
	return delimiter.join([str(v) for v in to_collection(*args)]) + (delimiter if append else '')


def collist(*args):
	'''Returns the string computed by joining the specified arguments with a comma.'''
	return collapse(*args, delimiter=',')


def paste(*args):
	'''Returns the string computed by joining the specified arguments with a space.'''
	return collapse(remove_empty(to_collection(*args)), delimiter=' ')


#########################

def extract(s, pattern):
	'''Returns all the occurrences of the specified pattern from the specified string.'''
	return re.findall(pattern, s)


#########################

def replace(s, pattern, replacement):
	'''Returns the string constructed by replacing the specified pattern by the specified
	replacement string in the specified string recursively (only if the length is decreasing).'''
	count = INF
	while len(s) < count:
		count = len(s)
		s = re.sub(pattern, replacement, s)
	return s


def replace_word(s, word, replacement):
	'''Returns the string constructed by replacing the specified word by the specified replacement
	string in the specified string recursively (only if the length is decreasing).'''
	count = INF
	while len(s) < count:
		count = len(s)
		s = re.sub('\\b' + word + '\\b', replacement, s)
	return s


#########################

def split(s, delimiter=',', empty_filter=True):
	'''Returns all the tokens computed by splitting the specified string around the specified
	delimiter (regular expression).'''
	if empty_filter:
		return remove_empty(re.split(delimiter, s))
	return re.split(delimiter, s)


#########################

def trim(s, replace_space=True, replace_special=True):
	'''Returns the string constructed by stripping the specified string (and replacing recursively
	the adjacent spaces if replace_space is True and/or special characters if replace_special is
	True to a single space).'''
	if replace_special:
		s = replace(s, '\b|\f|\r\n|\r|\n|\t', ' ')
	if replace_space:
		s = replace(s, ' +', ' ')
	return s.strip()


#########################

def wrap(content, left, right=None):
	'''Returns the wrapped representative string of the specified content.'''
	if is_null(left):
		return content
	elif is_null(right):
		right = left
	if is_collection(content):
		return apply(content, wrap, left, right=right)
	return collapse(left, content, right)


def quote(content):
	'''Returns the single-quoted representative string of the specified content.'''
	return wrap(content, '\'')


def dquote(content):
	'''Returns the double-quoted representative string of the specified content.'''
	return wrap(content, '"')


def par(content):
	'''Returns the parenthesized representative string of the specified content.'''
	return wrap(content, '(', ')')


def sbra(content):
	'''Returns the bracketized representative string of the specified content.'''
	return wrap(content, '[', ']')  # square brackets


def cbra(content):
	'''Returns the braced representative string of the specified content.'''
	return wrap(content, '{', '}')  # curly brackets


# • THREAD #########################################################################################

__THREAD_PROCESSORS_______________________________ = ''


def multithread(c, f, *args, asynchronous=False, max_workers=CORE_COUNT, timeout=None, **kwargs):
	return multithread_map(c, lambda x: apply(x, f, *args, **kwargs), asynchronous=asynchronous,
	                       max_workers=max_workers, timeout=timeout)


def multithread_map(c, f, asynchronous=False, max_workers=CORE_COUNT, timeout=None):
	if is_empty(c) or not is_collection(c):
		return []
	max_workers = min(max_workers, len(c))
	trace('Apply the function', quote(f.__name__), 'to the collection of size', len(c),
	      '(multithreading)')
	with ThreadPoolExecutor(max_workers=max_workers) as executor:
		# Submit the tasks and collect the results
		results = executor.map(f, c, timeout=timeout)
		if asynchronous:
			return results
		return to_list(results)


def multiprocess(c, f, *args, asynchronous=False, chunk_size=None, max_workers=CORE_COUNT,
                 timeout=None,
                 callback=None, error_callback=None,
                 **kwargs):
	return multiprocess_map(c, lambda x: apply(x, f, *args, **kwargs), asynchronous=asynchronous,
	                        chunk_size=chunk_size, max_workers=max_workers, timeout=timeout,
	                        callback=callback, error_callback=error_callback)


def multiprocess_map(c, f, asynchronous=False, chunk_size=None, max_workers=CORE_COUNT,
                     timeout=None,
                     callback=None, error_callback=None):
	if is_empty(c) or not is_collection(c):
		return []
	max_workers = min(max_workers, len(c))
	if is_null(chunk_size):
		chunk_size = ceil(len(c) / max_workers)
	trace('Apply the function', quote(f.__name__), 'to the collection of size', len(c),
	      '(multiprocessing)')
	with Pool(processes=max_workers) as executor:
		# Submit the tasks and collect the results
		results = executor.map_async(f, c, chunksize=chunk_size,
		                             callback=callback, error_callback=error_callback)
		if asynchronous:
			return results
		return results.get(timeout=timeout)
