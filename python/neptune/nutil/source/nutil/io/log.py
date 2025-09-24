#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains common console utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2025 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from tabulate import tabulate

from nutil.config import CONFIG
from nutil.enums import StrEnum
from nutil.scalar.date import *
from nutil.struct.table import *

####################################################################################################
# LOG CLASSES
####################################################################################################

__LOG_CLASSES_____________________________________ = ""


class LogLevel(StrEnum):
    CRITICAL = "CRITICAL"
    DEBUG = "DEBUG"
    ERROR = "ERROR"
    INFO = "INFO"
    WARNING = "WARNING"


class SeverityLevel(StrEnum):
    FAIL = 0
    ERROR = 1
    WARN = 2
    RESULT = 3
    INFO = 4
    TEST = 5
    DEBUG = 6
    TRACE = 7


####################################################################################################
# LOG PROPERTIES
####################################################################################################

__LOG_PROPERTIES__________________________________ = ""

# The severity level
SEVERITY_LEVEL = SeverityLevel(CONFIG.getint("console", "severityLevel"))

# The flag specifying whether to enable the verbose mode
VERBOSE = CONFIG.getboolean("console", "verbose")


####################################################################################################
# LOG PROCESSORS
####################################################################################################

__LOG_PROCESSORS__________________________________ = ""


def trace(*args, level=0):
    if SEVERITY_LEVEL.value >= 7:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][TRAC]",
                "[",
                get_script_name(level + 1),
                "]",
                "[",
                get_function_name(level + 1),
                "]",
                "[",
                get_line_number(level + 1),
                "] ",
                paste(*args),
            )
        )


def debug(*args, level=0):
    if SEVERITY_LEVEL.value >= 6:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][DEBU]",
                "[",
                get_script_name(level + 1),
                "]",
                "[",
                get_function_name(level + 1),
                "] ",
                paste(*args),
            )
        )


def test(*args, level=0):
    if SEVERITY_LEVEL.value >= 5:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][TEST]",
                "[",
                get_script_name(level + 1),
                "] ",
                paste(*args),
            )
        )


def info(*args):
    if SEVERITY_LEVEL.value >= 4:
        print(collapse("[", get_datetime_string(), "][INFO] ", paste(*args)))


def result(*args):
    if SEVERITY_LEVEL.value >= 3:
        print(paste(*args))


def warn(*args, level=0):
    if SEVERITY_LEVEL.value >= 2:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][WARN]",
                "[",
                get_script_name(level + 1),
                "] ",
                paste(*args),
            ),
            file=sys.stderr,
        )


def error(*args, level=0):
    if SEVERITY_LEVEL.value >= 1:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][ERRO]",
                "[",
                get_script_name(level + 1),
                "]",
                "[",
                get_function_name(level + 1),
                "] ",
                paste(*args),
            ),
            file=sys.stderr,
        )


def fail(*args, level=0):
    if SEVERITY_LEVEL.value >= 0:
        print(
            collapse(
                "[",
                get_datetime_string(),
                "][FAIL]",
                "[",
                get_script_name(level + 1),
                "]",
                "[",
                get_function_name(level + 1),
                "]",
                "[",
                get_line_number(level + 1),
                "] ",
                paste(*args),
            ),
            file=sys.stderr,
        )


##################################################


def print_table(table, format="grid", headers=None, show_index="default"):
    print(
        tabulate(
            table,
            tablefmt=format,
            headers=(
                headers if not is_null(headers) else get_names(table) if is_table(table) else EMPTY
            ),
            showindex=show_index,
        )
    )
