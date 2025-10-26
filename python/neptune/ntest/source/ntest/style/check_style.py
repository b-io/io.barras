#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# STYLE CHECKER ########################################################################################################
# Goal
#   Run the repository-wide regex style checks defined in a YAML config.
#   • Prunes excluded directories during traversal (so `".venv"` is not scanned)
#   • Treats each rule's regex as a *violation* matcher
#   • Applies each rule only to files matching the rule's own `include`/`exclude`
#   • Exits `0` when no `"error"`-severity matches are found; `1` otherwise
#
# YAML Config (excerpt)
#   rules:
#     - id: "line-comment-trailing-period"
#       description: "Line comments must not end with a period."
#       pattern: "^(?=\\s*#)(?!.*https?://).*\\S\\.$"
#       include: ["**/*.py"]
#       exclude: []
#       flags: ["IGNORECASE"]
#       severity: "warning"
#
# CLI
#   • `"--config" <path>` (optional; defaults to `'STYLE.yml'`)
#   • `"--root" <path>`   (optional; defaults to `"."`)
#
# Examples
#   • `python check_style.py --config STYLE.yml --root .`
#   • `python -m check_style --root ..`
########################################################################################################################

from __future__ import annotations

import argparse
import logging
import os
import re
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Dict, List, Pattern, Set, Tuple

import yaml

from common import (
    dirnames_from_globs,
    join_posix_paths,
    merge_globs,
    resolve_path,
    should_exclude_dir,
    should_exclude_file,
    to_relative_posix_path,
)

## CONFIG ################################################################################

# The default logging configuration
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%H:%M:%S",
)


## DATA CLASSES ##########################################################################


@dataclass
class Rule:
    """
    A regex-based lint rule loaded from the YAML config.

    Args:
        id: The rule identifier.
        description: The short rule summary shown in reports.
        severity: The severity string (`"error"` or `"warning"`).
        pattern: The compiled `re.Pattern` that matches violations.
        include: The list of file-glob patterns this rule applies to.
        exclude: The list of file-glob patterns this rule should ignore.
    """

    id: str
    description: str
    severity: str  # choices: `"error"` or `"warning"`
    pattern: Pattern[str]
    include: List[str]
    exclude: List[str]


@dataclass
class Config:
    """
    A compiled linter configuration.

    Args:
        include: The repository-level include globs (coarse gate).
        exclude: The repository-level exclude globs (coarse gate).
        prune_names: The directory basenames to prune during traversal (e.g., `".venv"`).
        rules: The list of compiled `Rule` instances.
    """

    include: List[str]
    exclude: List[str]
    prune_names: Set[str]
    rules: List[Rule]


## CONSTANTS #############################################################################

DEFAULT_EXCLUDES: List[str] = [
    "**/__pycache__/**",
    "**/.git/**",
    "**/.venv/**",
    "**/build/**",
    "**/dist/**",
    "**/node_modules/**",
    "**/venv/**",
]

FLAG_MAP: Dict[str, int] = {
    "IGNORECASE": re.IGNORECASE,
    "MULTILINE": re.MULTILINE,
    "DOTALL": re.DOTALL,
    "VERBOSE": re.VERBOSE,
}


## CONFIG LOADING ########################################################################


def load_config(path: Path) -> Config:
    """
    Loads the YAML config and compiles the rule regexes.

    Args:
        path: The config file path.

    Returns:
        The parsed and compiled `Config`.

    Raises:
        SystemExit: If the YAML cannot be parsed or a rule regex is invalid.
    """
    try:
        data = yaml.safe_load(path.read_text(encoding="utf-8")) or {}
    except Exception as e:
        sys.exit(f"Could not read YAML config '{path}': {e}")

    include = list(data.get("include") or ["**/*"])

    # Merge defaults with user excludes, then deduplicate while preserving order
    exclude_yaml = list(data.get("exclude") or [])
    exclude = merge_globs(DEFAULT_EXCLUDES, exclude_yaml)

    rules: List[Rule] = []
    for r in data.get("rules") or []:
        rid = str(r.get("id") or "unnamed")
        flags_val = 0
        for f in r.get("flags") or []:
            flags_val |= FLAG_MAP.get(str(f).upper(), 0)
        try:
            pat = re.compile(str(r["pattern"]), flags_val)
        except Exception as e:
            sys.exit(f"Invalid regex for rule '{rid}': {e}")
        rules.append(
            Rule(
                id=rid,
                description=str(r.get("description") or ""),
                severity=str(r.get("severity") or "error").lower(),
                pattern=pat,
                include=list(r.get("include") or ["**/*"]),
                exclude=list(r.get("exclude") or []),
            )
        )

    prune_names = dirnames_from_globs(exclude)
    return Config(include=include, exclude=exclude, prune_names=prune_names, rules=rules)


## SCANNER ###############################################################################


def scan_file(abs_path: Path, rules: List[Rule]) -> List[Tuple[Rule, int, str]]:
    """
    Scans a text file with applicable rules and returns the violations.

    Args:
        abs_path: The absolute file path.
        rules: The list of compiled regex rules to apply.

    Returns:
        The list of `(rule, line_number, line_text)` tuples for each match.
    """
    violations: List[Tuple[Rule, int, str]] = []
    try:
        with abs_path.open("r", encoding="utf-8", errors="ignore") as fh:
            for ln, line in enumerate(fh, 1):
                for rule in rules:
                    if rule.pattern.search(line):
                        violations.append((rule, ln, line.rstrip("\n")))
    except Exception as e:
        # Treat unreadable files as warnings (report but do not fail the run)
        violations.append(
            (
                Rule(
                    id="read-error",
                    description=f"Could not read file '{abs_path}': {e}",
                    severity="warning",
                    pattern=re.compile("$^"),
                    include=["**/*"],
                    exclude=[],
                ),
                0,
                "",
            )
        )
    return violations


## RUNNER ################################################################################


def run(config_path: Path, root: Path) -> int:
    """
    Executes the regex lint across the repository.

    Args:
        config_path: The path to the YAML config file.
        root: The repository root directory to scan.

    Returns:
        Exit code `0` on success (no `"error"`-severity violations), `1` otherwise.
    """
    cfg = load_config(config_path)
    any_error = False
    total_violations = 0

    for dirpath, dirnames, filenames in os.walk(root):
        dir_path = Path(dirpath)
        rel_dir = to_relative_posix_path(dir_path, root)

        # Prune excluded directories in place (avoid entering `".venv"`, etc)
        kept: List[str] = []
        for d in dirnames:
            if d in cfg.prune_names:
                continue
            rel_child = join_posix_paths(rel_dir, d)
            if should_exclude_dir(rel_child, cfg.exclude):
                continue
            kept.append(d)
        dirnames[:] = kept

        for name in filenames:
            rel_file = join_posix_paths(rel_dir, name)

            # Apply the coarse gate: global file-level include/exclude
            if should_exclude_file(rel_file, cfg.exclude, cfg.include):
                continue

            # Select only rules that apply to this file by the rule-level include/exclude
            active_rules: List[Rule] = [
                r for r in cfg.rules if not should_exclude_file(rel_file, r.exclude, r.include)
            ]
            if not active_rules:
                # Skip the file if no rule targets it (avoid PDFs and binaries)
                continue

            abs_file = dir_path / name
            violations = scan_file(abs_file, active_rules)
            if violations:
                for rule, ln, text in violations:
                    loc = f"{rel_file}:{ln}" if ln else rel_file
                    level = logging.ERROR if rule.severity == "error" else logging.WARNING
                    logging.log(level, "[%s] [%s] %s", rule.id, loc, rule.description)
                    if text:
                        logging.log(level, "%s", text)
                    if rule.severity == "error":
                        any_error = True
                total_violations += len(violations)

    if total_violations == 0:
        logging.info("CODING STYLE: no violations found ✅")
    else:
        logging.warning("CODING STYLE: %d violation(s) found", total_violations)

    return 1 if any_error else 0


## CLI ###################################################################################


def parse_args() -> argparse.Namespace:
    """
    Parses the CLI arguments for the style checker.

    Returns:
        An `argparse.Namespace` with resolved paths and options.

    Raises:
        FileNotFoundError: If the input paths are invalid.
    """
    args = _build_arg_parser().parse_args()

    # Resolve the input paths
    args.config = resolve_path(args.config)
    args.root = resolve_path(args.root)

    return args


def _build_arg_parser() -> argparse.ArgumentParser:
    """Builds the CLI argument parser."""
    ap = argparse.ArgumentParser(description="Regex-based style checks based on 'STYLE.yml' rules.")
    # The paths
    ap.add_argument("--config", default="STYLE.yml", help="Path to YAML config.")
    ap.add_argument("--root", default=".", help="Root directory to scan.")
    return ap


## MAIN ##################################################################################

if __name__ == "__main__":
    args = parse_args()
    logging.info("Run '%s' with args: %s", Path(__file__).name, args)
    run(args.config, args.root)
