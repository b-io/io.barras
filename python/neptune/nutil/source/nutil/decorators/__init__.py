#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# Goal
#   Provide utility decorators.
##########################################################################################

from . import common
from .common import *

## DECORATORS INIT #######################################################################

__DECORATORS_INIT___________________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
