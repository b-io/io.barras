#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# NAME
#   <NAME> - contains machine learning utilities
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

## LEARN INIT ############################################################################

__LEARN_INIT________________________________________________ = ""

__all__ = [s for s in dir() if not s.startswith("_")]
__version__ = "1.0.0.post137"

############################################################

NAME = "nlearn"
VERSION = __version__
DESCRIPTION = "Machine learning utility library"
