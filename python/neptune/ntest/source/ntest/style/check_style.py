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
# CLI
#   • `"--config" <path>` (optional; defaults to `"STYLE.yml"`)
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
from pathlib import Path
from typing import List, Tuple

from ntest.style.common import StyleConfig, StyleRule, load_yaml_config
from nutil.constants import DEFAULT_ENCODING
from nutil.io.file import (
    exclude_dir,
    exclude_file,
    join_posix_paths,
    resolve_path,
    to_relative_posix_path,
)
from nutil.io.logging import configure_logging


## CONFIG ################################################################################

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(module)s] [%(levelname)s] %(message)s",
    datefmt="%H:%M:%S",
)


## RUNNER ################################################################################


def run(root: Path, config: StyleConfig) -> int:
    """
    Executes the regex lint across the repository.

    Args:
        root: The repository root directory to scan.
        config: The compiled style configuration to use.

    Returns:
        Exit code `0` on success (no `"error"`-severity violations), `1` otherwise.
    """
    any_error = False
    total_violations = 0

    for dirpath, dirnames, filenames in os.walk(root):
        dir_path = Path(dirpath)
        rel_dir = to_relative_posix_path(dir_path, root)

        # Prune excluded directories in place (avoid entering `".venv"`, etc)
        kept: List[str] = []
        for d in dirnames:
            if d in config.prune_names:
                continue
            rel_child = join_posix_paths(rel_dir, d)
            if exclude_dir(rel_child, config.exclude):
                continue
            kept.append(d)
        dirnames[:] = kept

        for name in filenames:
            rel_file = join_posix_paths(rel_dir, name)

            # Apply the coarse gate: global file-level include/exclude
            if exclude_file(rel_file, config.exclude, config.include):
                continue

            # Select only rules that apply to this file by the rule-level include/exclude
            active_rules: List[StyleRule] = [
                r for r in config.rules if not exclude_file(rel_file, r.exclude, r.include)
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
                    logging.log(level, "[%s] [%s] %s", loc, rule.id, rule.description)
                    if text:
                        logging.log(level, "[%s] [%s] %s", loc, rule.id, text)
                    if rule.severity == "error":
                        any_error = True
                total_violations += len(violations)

    if total_violations == 0:
        logging.info("CODING STYLE: no violations found ✅")
    else:
        logging.warning("CODING STYLE: %d violation(s) found", total_violations)

    return 1 if any_error else 0


## SCANNER ###############################################################################


def scan_file(abs_path: Path, rules: List[StyleRule]) -> List[Tuple[StyleRule, int, str]]:
    """
    Scans a text file with applicable rules and returns the violations.

    Args:
        abs_path: The absolute file path.
        rules: The list of compiled regex rules to apply.

    Returns:
        The list of `(rule, line_number, line_text)` tuples for each match.
    """
    violations: List[Tuple[StyleRule, int, str]] = []
    try:
        with abs_path.open("r", encoding=DEFAULT_ENCODING, errors="ignore") as fh:
            for line_number, line in enumerate(fh, 1):
                for rule in rules:
                    if rule.pattern.search(line):
                        violations.append((rule, line_number, line.rstrip("\n\r")))
    except Exception as e:
        # Treat unreadable files as warnings (report but do not fail the run)
        violations.append(
            (
                StyleRule(
                    id="read-error",
                    description=f"Could not read file '{abs_path}': {e}",
                    pattern=re.compile("$^"),
                    include=["**/*"],
                    exclude=[],
                    flags=[],
                    severity="warning",
                ),
                0,
                "",
            )
        )
    return violations


## CLI ###################################################################################


def parse_args() -> argparse.Namespace:
    """
    Parses the CLI arguments for the style checker.

    Returns:
        An `argparse.Namespace` with resolved paths and options.

    Raises:
        FileNotFoundError: When the input paths are invalid.
    """
    ap: argparse.ArgumentParser = _build_arg_parser()
    args: argparse.Namespace = ap.parse_args()

    # Resolve the paths and load the configuration
    config_path = resolve_path(args.config)
    args.config = load_yaml_config(config_path)
    args.root = resolve_path(args.root)

    return args


### HELPERS ################################################


def _build_arg_parser() -> argparse.ArgumentParser:
    """Builds the CLI argument parser."""
    ap = argparse.ArgumentParser(description="Regex-based style checks based on 'STYLE.yml' rules.")
    # Add the path(s)
    ap.add_argument("--config", help="Path to YAML config.", default="STYLE.yml")
    ap.add_argument("--root", help="Root directory to scan.", default=".")
    return ap


## MAIN ##################################################################################


def main():
    configure_logging()
    args = parse_args()
    logging.info("Run '%s' with args: %s", Path(__file__).name, args)
    run(args.root, args.config)


if __name__ == "__main__":
    main()
