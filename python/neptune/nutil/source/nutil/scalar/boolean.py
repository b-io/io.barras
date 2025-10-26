#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains boolean utility functions
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

from distutils.util import strtobool

from nutil.scalar.number import *
from nutil.struct.util.util import apply

## BOOLEAN CONVERTERS ####################################################################

__BOOLEAN_CONVERTERS________________________________________ = ""

def to_boolean(x: Any):
    if is_null(x):
        return NAN
    elif is_struct(x):
        if hasattr(x, "astype"):
            return x.astype(BOOLEAN_ELEMENT_TYPE)
        return apply(x, to_boolean)
    elif is_string(x):
        return bool(strtobool(x))
    return bool(x)
