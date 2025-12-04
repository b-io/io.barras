#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# STYLE FIXER ##########################################################################################################
# Goal
#   Fix coding-style issues in-place based on the `"STYLE.yml"` rules.
#   • Pads or trims hash banners to the target widths **30**, **60**, **90**, or **120** based on the leading `#`.
#     - 1 leading `#` → **120** (section)
#     - 2 leading `#` → **90**  (subsection)
#     - 3 leading `#` → **60**  (subsubsection)
#     - 4+ leading `#` → **30** (subsubsubsection)
#   • Treats a line as a banner only if it starts with `#` (optionally spaced) and contains `##` somewhere.
#   • Trims only when the overflow past the target consists solely of spaces or `#`.
#   • Removes a single trailing period from a one-line Python comment.
#   • Capitalizes the first alphabetic letter in a one-line Python comment.
#   • Optionally lowercases the first letter of an inline end-of-line comment (when a matching rule exists).
#
# Behavior
#   • Reads rule regexes and file globs from the YAML file; applies a fixer only when the rule `pattern` matches.
#   • Merges repository excludes with defaults and prunes directories derived from excludes ending in `"/**"`.
#   • Processes only files selected by the repo-level and rule-level include/exclude globs.
#
# CLI
#   • `"--root" <path>`   (optional; defaults to `"."`)
#   • `"--config" <path"` (optional; defaults to `"STYLE.yml"`)
#   • `"--dry-run"`       (optional; previews changes without writing)
#
# Examples
#   • `python fix_style.py --config STYLE.yml --root .`
#   • `python fix_style.py --dry-run`
########################################################################################################################

from __future__ import annotations

import argparse
import logging
import os
import re
from pathlib import Path
from typing import Callable, Dict, List, Sequence, Tuple

from ntest.style.common import load_yaml_config, StyleConfig, StyleRule
from nutil.constants import DEFAULT_ENCODING
from nutil.io.file import (
    exclude_dir,
    exclude_file,
    join_posix_paths,
    resolve_path,
    to_relative_posix_path,
    write_text,
)
from nutil.io.logging import configure_logging

## FIXERS ################################################################################


def fix_hash_banner_length(line: str, rule: StyleRule) -> Tuple[str, bool]:
    """
    Pads or trims the banner line to the target width and normalizes the spacing.

    Normalization (applies only to the banner line that matches the `rule.pattern`):
        • Ensures exactly one space after the leading hashes, then the title, then one space,
          then the trailing `#` run.
        • Chooses the trailing hash count so that the total width equals the target (30/60/90/120).

    Additionally:
        • If the current line length is already one of 30, 60, 90, or 120, it corrects the leading
          `#` count for that width and rebalances the trailing `#` run to preserve the width.

    Trim is performed only when the overflow past the target consists solely of spaces or `#`.

    Args:
        line: The input line to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    if not rule.pattern.search(line):
        return line, False

    nl = "\n" if line.endswith("\n") else ""
    body = line[:-1] if nl else line

    # Match the lines that start with `#` and contain two consecutive `#`
    if not re.match(r"^\s*#", body) or "##" not in body:
        return line, False

    m = re.match(r"^(\s*)(#{1,})([^\n]*)$", body)
    if not m:
        return line, False

    indent, hashes, rest = m.groups()
    leading = len(hashes)
    cur_len = len(body)

    # The title is the content after the leading hashes, without the trailing hashes or spaces
    title = rest.lstrip()
    title = re.sub(r"[\s#]+$", "", title)

    # Choose the target width and the desired leading hash count
    exact_targets = {120: 1, 90: 2, 60: 3, 30: 4}
    if cur_len in exact_targets:
        target = cur_len
        desired_leading = exact_targets[cur_len]
    else:
        if leading == 1:
            target, desired_leading = 120, 1
        elif leading == 2:
            target, desired_leading = 90, 2
        elif leading == 3:
            target, desired_leading = 60, 3
        else:
            target, desired_leading = 30, 4

    # Build the normalized prefix with one space after the leading hashes and one before the trailing hashes
    base = f"{indent}{'#' * desired_leading}" + (f" {title} " if title else "")

    # Compute the trailing hash count to reach the target width
    hash_count = target - len(base)

    if hash_count < 0:
        # Fall back to the original width if trimming is safe (only spaces or `"#"`)
        if len(body) > target and set(body[target:]) <= {"#", " "}:
            return body[:target] + nl, True
        return line, False

    new_body = f"{base}{'#' * hash_count}"

    # If nothing changed, return the original line
    if new_body == body:
        return line, False

    return new_body + nl, True


def fix_line_comment_capitalized(line: str, rule: StyleRule) -> Tuple[str, bool]:
    """
    Capitalizes the first alphabetic character in a one-line Python comment.

    Triggered only when the `rule.pattern` matches.

    Args:
        line: The input line to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    if not rule.pattern.search(line):
        return line, False
    nl = "\n" if line.endswith("\n") else ""
    body = line[:-1] if nl else line
    m = re.match(r"^(\s*#\s*)([a-z])([^\n]*)$", body)
    if not m:
        return line, False
    prefix, first, rest = m.groups()
    return f"{prefix}{first.upper()}{rest}{nl}", True


def fix_inline_comment_lowercase(line: str, rule: StyleRule) -> Tuple[str, bool]:
    """
    Lowercases the first letter of an inline (end-of-line) comment.

    Triggered only when the `rule.pattern` matches.
    A typical pattern is `^(?!\\s*#).*?\\S[ \\t]{2,}#\\s+[A-Z]`.

    Args:
        line: The input line to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    if not rule.pattern.search(line):
        return line, False
    nl = "\n" if line.endswith("\n") else ""
    body = line[:-1] if nl else line

    m = re.match(r"^(?P<left>(?!\s*#).*?\S[ \t]{2,}#\s+)(?P<first>[A-Z])(?P<rest>[^\n]*)$", body)
    if not m:
        return line, False
    left, first, rest = m.group("left"), m.group("first"), m.group("rest")
    return f"{left}{first.casefold()}{rest}{nl}", True


def fix_line_comment_trailing_period(line: str, rule: StyleRule) -> Tuple[str, bool]:
    """
    Removes a single trailing `.` in a one-line Python comment.

    Triggered only when the `rule.pattern` matches (e.g., URL exclusions handled in YAML).

    Args:
        line: The input line to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    if not rule.pattern.search(line):
        return line, False
    nl = "\n" if line.endswith("\n") else ""
    body = line[:-1] if nl else line
    r = body.rstrip()
    if r.endswith(".") and not r.endswith(".."):
        return r[:-1] + body[len(r) :] + nl, True
    return line, False


## FIXER REGISTRY ########################################################################

FixerFn = Callable[[str, StyleRule], Tuple[str, bool]]

FIXERS: Dict[str, FixerFn] = {
    "hash-banner-length": fix_hash_banner_length,
    "line-comment-capitalized": fix_line_comment_capitalized,
    "line-comment-trailing-period": fix_line_comment_trailing_period,
    "inline-comment-lowercase": fix_inline_comment_lowercase,  # optional rule
}


## FILE PROCESSOR ########################################################################


def process_file(path: Path, rules: Sequence[StyleRule]) -> Dict[str, int]:
    """
    Applies all active rule fixes to a file.

    Args:
        path: The file path to process.
        rules: The ordered sequence of active `StyleRule` instances.

    Returns:
        The dictionary of `{rule_id: count_of_line_changes}`.
    """
    try:
        orig = path.read_text(encoding=DEFAULT_ENCODING, errors="ignore")
    except Exception as e:
        logging.warning("Could not read '%s': %s", path, e)
        return {}

    # Preserve the rule order as defined in the YAML configuration
    ordered: List[StyleRule] = [r for r in rules if r.id in FIXERS]

    counts: Dict[str, int] = {}
    changed = False
    out_lines: List[str] = []

    for line in orig.splitlines(keepends=True):
        current = line
        for rule in ordered:
            fixer = FIXERS.get(rule.id)
            if not fixer:
                continue
            fixed, did = fixer(current, rule)
            if did:
                counts[rule.id] = counts.get(rule.id, 0) + 1
                current = fixed
        changed = changed or (current != line)
        out_lines.append(current)

    if changed:
        write_text(path, "".join(out_lines))

    return counts


## RUNNER ################################################################################


def run(root: Path, config: StyleConfig, dry_run: bool = False) -> int:
    """
    Walks the tree, prunes the excluded directories, and applies the fixes.

    Args:
        root: The repository root to scan.
        config: The compiled style configuration to use.
        dry_run: Whether to preview changes without writing.

    Returns:
        The exit status code `0` for success.
    """
    fixable_rules = [r for r in config.rules if r.id in FIXERS]
    if not fixable_rules:
        logging.info("No known fixer rules found in config. Nothing to do.")
        return 0

    # The union of the rule-level include/exclude (coarse filter to avoid opening irrelevant files)
    rule_union_includes: List[str] = sorted({g for r in fixable_rules for g in r.include})
    rule_union_excludes: List[str] = sorted({g for r in fixable_rules for g in r.exclude})

    total_counts: Dict[str, int] = {}
    total_files = 0

    for dirpath, dirnames, filenames in os.walk(root):
        dir_path = Path(dirpath)
        rel_dir = to_relative_posix_path(dir_path, root)

        # Prune the excluded directories in place based on the merged excludes
        kept: List[str] = []
        for dirname in dirnames:
            if dirname in config.prune_names:
                continue
            rel_child = join_posix_paths(rel_dir, dirname)
            if exclude_dir(rel_child, config.exclude):
                continue
            kept.append(dirname)
        dirnames[:] = kept

        for name in filenames:
            rel_file = join_posix_paths(rel_dir, name)
            abs_file = dir_path / name

            # Coarse gate: the repo-level include/exclude
            if exclude_file(rel_file, config.exclude, config.include):
                continue

            # Coarse gate: the union of the rule-level include/exclude
            if exclude_file(rel_file, rule_union_excludes, rule_union_includes):
                continue

            # Determine the active rules for this file (the rule-level include/exclude)
            active_rules = [
                r for r in fixable_rules if not exclude_file(rel_file, r.exclude, r.include)
            ]
            if not active_rules:
                continue

            total_files += 1
            if dry_run:
                counts = preview_file(abs_file, active_rules)
            else:
                counts = process_file(abs_file, active_rules)

            for k, v in counts.items():
                total_counts[k] = total_counts.get(k, 0) + v

    parts = [f"{k}: {v}" for k, v in sorted(total_counts.items())]
    logging.info(
        "%s %d file(s)%s",
        ("[DRY RUN] " if dry_run else "") + "Fixed the coding style of",
        total_files,
        (" | " + ", ".join(parts)) if parts else "",
    )
    return 0


## CLI ###################################################################################


def preview_file(path: Path, rules: Sequence[StyleRule]) -> Dict[str, int]:
    """
    Reports what would change for a file without writing.

    Args:
        path: The file path to preview.
        rules: The ordered sequence of active `StyleRule` instances.

    Returns:
        The dictionary of `{rule_id: count_of_line_changes}`.
    """
    try:
        orig = path.read_text(encoding=DEFAULT_ENCODING, errors="ignore")
    except Exception as e:
        logging.warning("Could not read '%s': %s", path, e)
        return {}

    ordered: List[StyleRule] = [r for r in rules if r.id in FIXERS]
    counts: Dict[str, int] = {}

    for line in orig.splitlines(keepends=True):
        current = line
        for rule in ordered:
            fixer = FIXERS.get(rule.id)
            if not fixer:
                continue
            fixed, did = fixer(current, rule)
            if did:
                counts[rule.id] = counts.get(rule.id, 0) + 1
                current = fixed

    if counts:
        logging.info(
            "[DRY RUN] '%s' → %s",
            path,
            ", ".join(f"{k}: {v}" for k, v in sorted(counts.items())),
        )
    return counts


def parse_args() -> argparse.Namespace:
    """
    Parses the CLI arguments for the style fixer.

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
    ap = argparse.ArgumentParser(
        description="Fix simple coding-style issues based on 'STYLE.yml' rules."
    )
    # Add the path(s)
    ap.add_argument("--config", help="Path to YAML config.", default="STYLE.yml")
    ap.add_argument("--root", help="Root directory to scan.", default=".")
    # Add the save parameter(s)
    ap.add_argument(
        "--dry-run",
        help="Report changes without writing files.",
        action="store_true",
    )
    return ap


## MAIN ##################################################################################


def main():
    """Runs the fix style tool."""
    configure_logging()
    args = parse_args()
    logging.info("Run '%s' with args: %s", Path(__file__).name, args)
    run(args.root, args.config, dry_run=args.dry_run)


if __name__ == "__main__":
    main()
