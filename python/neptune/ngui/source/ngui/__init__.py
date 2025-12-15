#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide graphical user interface (GUI) utilities.
########################################################################################################################

## GUI INIT ##############################################################################

__GUI_INIT__________________________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
__version__ = "1.0.1a1"


### GLOBALS ################################################

NAME = "ngui"
VERSION = __version__
DESCRIPTION = "Graphical user interface utility library"
