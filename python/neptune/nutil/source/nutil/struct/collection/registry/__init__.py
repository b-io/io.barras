#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide collection registry utilities.
########################################################################################################################

from __future__ import annotations

from .common import *

__COLLECTION_REGISTRY_CONSTANTS___________________________________________________________ = ""


__all__ = [s for s in dir() if not s.startswith("_")]
