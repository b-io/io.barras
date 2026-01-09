#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Expose the `nserve` CLI as `python -m nserve`.
########################################################################################################################

from __future__ import annotations

from nserve.cli import main

__MAIN_RUNNERS____________________________________________________________________________ = ""


### MAIN ###################################################

if __name__ == "__main__":
    main()
