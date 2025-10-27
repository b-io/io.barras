#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains utility configuration
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

import configparser

from nutil.common import *
from nutil.enums import *

## CONFIG CLASSES ########################################################################

__CONFIG_CLASSES____________________________________________ = ""

class EnvInterpolation(configparser.BasicInterpolation):
    """Extends the basic property parser to handle environment variables."""

    def before_get(self, parser, section, option, value, defaults):
        value = super().before_get(parser, section, option, value, defaults)
        return os.path.expandvars(value)


## CONFIG CONSTANTS ######################################################################

__CONFIG_CONSTANTS__________________________________________ = ""

CONFIG = configparser.ConfigParser(interpolation=EnvInterpolation())

##############################

# The default configuration
DEFAULT_CONFIG = {
    "common": {
        # Assert
        "assert": None,
        # Environment (local, dev, test, model, prod)
        "env": None,
    },
    "console": {
        # Severity level (0: FAIL, 1: ERROR, 2: WARN, 3: RESULT, 4: INFO, 5: TEST, 6: DEBUG, 7: TRACE)
        "severityLevel": None,
        # Verbose
        "verbose": None,
    },
    "date": {
        # Date format
        "dateFormat": None,
        # Time format
        "timeFormat": None,
    },
    "series": {
        # Aggregation (`count`, `min`, `max`, `mean`, `median`, `std`, `var`, `sum`)
        "aggregation": None,
        # Frequency (`D`, `W`, `M`, `Q`, `S`, `Y`)
        "frequency": None,
        # Period
        "period": None,
        # Position (`auto`, `start`, `middle`, `end`)
        "position": None,
    },
}
CONFIG.read_dict(DEFAULT_CONFIG)

############################################################

# The flag specifying whether to assert
ASSERT = CONFIG.getboolean("common", "assert")

# The environment
ENV = Environment(CONFIG.get("common", "env"))


### DATE ###################################################

__DATE_PROPERTIES___________________________________________ = ""

# The date format
DATE_FORMAT = CONFIG.get("date", "dateFormat")

# The time format
TIME_FORMAT = CONFIG.get("date", "timeFormat")

# The date-time format
DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT

##############################

# The aggregation (`count`, `min`, `max`, `mean`, `median`, `std`, `var`, `sum`)
AGGREGATION = Aggregation(CONFIG.get("series", "aggregation"))

# The frequency (`D`, `W`, `M`, `Q`, `S`, `Y`)
FREQUENCY = Frequency(CONFIG.get("series", "frequency"))

# The period
PERIOD = CONFIG.get("series", "period")

# The position (`auto`, `start`, `middle`, `end`)
POSITION = Position(CONFIG.get("series", "position"))

## CONFIG ACCESSORS ######################################################################

__CONFIG_ACCESSORS__________________________________________ = ""

def get_config_path(filename, dir=DEFAULT_ROOT, subdir=DEFAULT_RES_DIR):
    """Returns the path to the properties with the specified filename in the specified directory."""
    return find_path(filename + ".properties", dir=dir, subdir=subdir)


## CONFIG PROCESSORS #####################################################################

__CONFIG_PROCESSORS_________________________________________ = ""

def escape_property(property):
    return property.replace("%", "%%") if not is_null(property) else None

def load_config(filename, dir=DEFAULT_ROOT, subdir=DEFAULT_RES_DIR):
    """Loads the properties with the specified filename in the specified directory."""
    return CONFIG.read(get_config_path(filename, dir=dir, subdir=subdir))


## CONFIG EXECUTION ######################################################################

__CONFIG_EXECUTION__________________________________________ = ""

load_config("")
print(CONFIG)
