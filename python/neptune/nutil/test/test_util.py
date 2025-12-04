#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the utilities.
########################################################################################################################

import logging

from tests import *
from nutil.io.logging import configure_logging


## UTIL TEST MAIN #################################################################

__UTIL_TEST_MAIN_____________________________________ = ""


def main() -> None:
    """Tests the utilities."""
    configure_logging(level=logging.DEBUG)
    test_metaclasses.main()


if __name__ == "__main__":
    main()
