#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide regular expression utilities.
########################################################################################################################

from __future__ import annotations

import re
from typing import Iterable, Optional


__REGEX_PROCESSORS________________________________________________________________________ = ""


def build_alternation_regex(
    alternatives: Iterable[str],
    *,
    group: Optional[str] = None,
    is_whole: bool = False,
) -> str:
    """Builds a non-capturing alternation sorted longest-first to prevent the partial matches."""
    alternatives = sorted(alternatives, key=len, reverse=True)
    if not alternatives:
        raise ValueError("'alternatives' must be a non-empty iterable of strings")
    alternation: str = "|".join(map(re.escape, alternatives))
    alternation: str = rf"(?P<{group}>{alternation})" if group else f"(?:{alternation})"
    return rf"\b{alternation}\b" if is_whole else alternation


def compile_alternation_regex(
    words: Iterable[str],
    *,
    group: Optional[str] = None,
    flags: int = 0,
    is_whole: bool = False,
) -> re.Pattern[str]:
    """Compiles a regex that matches any of the `words` (whole-word when `is_whole=True`)."""
    return re.compile(build_alternation_regex(words, group=group, is_whole=is_whole), flags=flags)
