#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the test utilities.
########################################################################################################################

from __future__ import annotations

import logging

from nutil.io.logging import configure_logging


__TESTING_TEST_RUNNERS____________________________________________________________________ = ""


### MAIN ###################################################


def main() -> None:
    """Tests the test utilities."""
    configure_logging(level=logging.DEBUG)


if __name__ == "__main__":
    main()
