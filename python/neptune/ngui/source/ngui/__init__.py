#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# NAME
#   <NAME> - contains graphical user interface utilities
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

## GUI INIT ##############################################################################

__GUI_INIT__________________________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
__version__ = "1.0.0.post137"

############################################################

NAME = "ngui"
VERSION = __version__
DESCRIPTION = "Graphical user interface utility library"
