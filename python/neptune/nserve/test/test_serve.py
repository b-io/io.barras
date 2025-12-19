#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the Web-serving utilities.
########################################################################################################################

from __future__ import annotations

import logging

from nutil.io.logging import configure_logging


__WEB_SERVING_TEST_RUNNERS________________________________________________________________ = ""


### MAIN ###################################################


def main() -> None:
    """Tests the Web-serving utilities."""
    configure_logging(level=logging.DEBUG)


if __name__ == "__main__":
    main()
