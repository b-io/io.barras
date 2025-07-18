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

import numpy as np

####################################################################################################
# COMMON BYTES CONSTANTS
####################################################################################################

__COMMON_BYTES_CONSTANTS__________________________ = ""

BYTES_TYPE = bytes
BYTES_ELEMENT_TYPE = np.bytes_

####################################################################################################
# COMMON BYTES VERIFIERS
####################################################################################################

__COMMON_BYTES_VERIFIERS__________________________ = ""


def is_bytes(x):
    return isinstance(x, BYTES_TYPE) or isinstance(x, BYTES_ELEMENT_TYPE)
