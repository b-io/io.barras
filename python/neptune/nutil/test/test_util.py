#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the utilities.
########################################################################################################################

from nutil.io.logging import configure_logging
from tests import *

## UTIL TEST MAIN ########################################################################

__UTIL_TEST_MAIN____________________________________________ = ""


def main() -> None:
    """Tests the utilities."""
    configure_logging(level=logging.DEBUG)
    test_metaclasses.main()


if __name__ == "__main__":
    main()
