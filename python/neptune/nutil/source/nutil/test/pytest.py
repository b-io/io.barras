#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide pytest utilities.
########################################################################################################################

import random

from ntest.common import *
from nutil.struct.util import *

## FIXTURES ##############################################################################


@pytest.fixture(autouse=True)
def seed_rng():
    """Seeds the random number generators for deterministic tests."""
    random.seed(0)
    np.random.seed(0)


@pytest.fixture
def precision():
    """Provides the default numeric precision."""
    return PRECISION


@pytest.fixture
def test_count():
    """Provides the default iteration count for timing tests."""
    return TEST_COUNT
