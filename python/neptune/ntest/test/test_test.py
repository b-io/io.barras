#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Test the testing utilities.
########################################################################################################################

import logging

from nutil.io.logging import configure_logging

## TESTING TEST MAIN #####################################################################

__TESTING_TEST_MAIN_________________________________________ = ""


def main() -> None:
    """Tests the testing utilities."""
    configure_logging(level=logging.DEBUG)


if __name__ == "__main__":
    main()
