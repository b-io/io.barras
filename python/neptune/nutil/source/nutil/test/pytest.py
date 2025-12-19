#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide test utilities for pytest.
########################################################################################################################

import random

import numpy as np
import pytest

from nutil.test.util import PRECISION, TEST_COUNT


__PYTEST_FIXTURES_________________________________________________________________________ = ""


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
