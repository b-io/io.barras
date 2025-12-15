#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# COMMON UTILITIES #####################################################################################################
# Goal
#   Provide common coding style utilities.
########################################################################################################################

from __future__ import annotations

import re
from dataclasses import dataclass
from pathlib import Path

import yaml

from nutil.common import *
from nutil.io.file import get_dirnames_from_globs
from nutil.struct.collection.list import deduplicate
from nutil.struct.table.util import get_row_string

## COMMON STYLE CONSTANTS ################################################################

__COMMON_STYLE_CONSTANTS____________________________________ = ""


### DEFAULTS ###############################################

DEFAULT_EXCLUDES: List[str] = [
    "**/__pycache__/**",
    "**/.git/**",
    "**/.venv/**",
    "**/build/**",
    "**/dist/**",
    "**/node_modules/**",
    "**/venv/**",
]


### GLOBALS ################################################

FLAG_MAP: Dict[str, int] = {
    "DOTALL": re.DOTALL,
    "IGNORECASE": re.I,
    "MULTILINE": re.MULTILINE,
    "VERBOSE": re.VERBOSE,
}


## COMMON STYLE CLASSES ##################################################################

__COMMON_STYLE_CLASSES______________________________________ = ""


@dataclass
class StyleRule:
    """
    A regex-based lint rule loaded from the YAML config.

    The layout mirrors the STYLE configuration:
        • `id`
        • `description`
        • `pattern` (compiled)
        • `include`
        • `exclude`
        • `flags` (string names from the YAML: `"IGNORECASE"`, `"MULTILINE"`, ...)
        • `severity` (`"error"` or `"warning"`)

    Args:
        id: The rule identifier.
        description: The short rule summary shown in reports.
        pattern: The compiled `re.Pattern` that matches violations.
        include: The list of file-glob patterns this rule applies to.
        exclude: The list of file-glob patterns this rule should ignore.
        flags: The list of textual flag names as specified in the YAML.
        severity: The severity string (`"error"` or `"warning"`).
    """

    id: str
    description: str
    pattern: re.Pattern[str]
    include: List[str]
    exclude: List[str]
    flags: List[str]
    severity: str  # choices: `"error"` or `"warning"`


@dataclass
class StyleConfig:
    """
    A compiled linter configuration.

    Args:
        include: The repository-level include globs (coarse gate).
        exclude: The repository-level exclude globs (coarse gate).
        prune_names: The directory basenames to prune during traversal (e.g., `".venv"`).
        rules: The list of compiled `StyleRule` instances.
    """

    include: List[str]
    exclude: List[str]
    prune_names: Set[str]
    rules: List[StyleRule]


## COMMON STYLE LOADING ##################################################################

__COMMON_STYLE_LOADING______________________________________ = ""


def load_yaml_config(path: Path) -> StyleConfig:
    """
    Loads and compiles the YAML configuration.

    Behavior:
        • Always merges the YAML `exclude:` with `DEFAULT_EXCLUDES` (order-preserving and deduplicated).
        • Computes the `prune_names` set from the merged `exclude` list (patterns ending with `"/**"`).
        • Compiles the `pattern` for each rule using the OR-ed `flags`, while preserving the textual
          `flags` list as specified in the YAML.

    Args:
        path: The path to the YAML configuration file.

    Returns:
        The compiled `StyleConfig`.

    Raises:
        SystemExit: When the YAML file cannot be read or parsed, or a rule regex is invalid.
    """
    try:
        text = path.read_text(encoding=DEFAULT_ENCODING)
    except Exception as e:
        sys.exit(f"Could not read YAML config '{path}': {e}")

    try:
        data = yaml.safe_load(text) or {}
    except Exception as e:
        sys.exit(f"Invalid YAML in '{path}': {e}")

    include = list(data.get("include") or ["**/*"])

    # Always merge the defaults with the user excludes (even when the user specifies an empty list)
    exclude_yaml = list(data.get("exclude") or [])
    exclude = deduplicate(DEFAULT_EXCLUDES + exclude_yaml)

    rules: List[StyleRule] = []
    for rule in data.get("rules") or []:
        rule_id = str(rule.get("id") or "unnamed")

        raw_flags: List[str] = list(rule.get("flags") or [])
        flags_val = 0
        for f in raw_flags:
            flags_val |= FLAG_MAP.get(str(f).upper(), 0)

        try:
            pattern: re.Pattern[str] = re.compile(str(rule["pattern"]), flags_val)
        except Exception as e:
            sys.exit(f"Invalid regex for rule '{rule_id}': {e}")

        rules.append(
            StyleRule(
                id=rule_id,
                description=get_row_string(rule, "description"),
                pattern=pattern,
                include=list(rule.get("include") or ["**/*"]),
                exclude=list(rule.get("exclude") or []),
                flags=[str(f).upper() for f in raw_flags],
                severity=str(rule.get("severity") or "warning").casefold(),
            )
        )

    prune_names = get_dirnames_from_globs(exclude)
    return StyleConfig(include=include, exclude=exclude, prune_names=prune_names, rules=rules)
