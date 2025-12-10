#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the connecting utilities.
########################################################################################################################

import logging

from nutil.io.logging import configure_logging
from tests import *

## CONNECTING TEST MAIN ##################################################################

__CONNECTING_TEST_MAIN______________________________________ = ""


def main() -> None:
    """Tests the connecting utilities."""
    configure_logging(level=logging.DEBUG)
    test_db.main()


if __name__ == "__main__":
    main()
