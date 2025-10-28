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
#   • Reads rule regexes and file globs from the YAML file; applies a fixer only when the rule’s `pattern` matches.
#   • Merges repository excludes with defaults and prunes directories derived from excludes ending in `"/**"`.
#   • Processes only files selected by the repo-level and rule-level include/exclude globs.
#
# Fix Order
#   1) `"hash-banner-length"`
#   2) `"line-comment-capitalized"`
#   3) `"line-comment-trailing-period"`
#   4) `"inline-comment-lowercase"` (optional rule)
#
# YAML Config (excerpt)
#   rules:
#     - id: "hash-banner-length"
#       description: "Pad/trim and normalize hash banners to 30/60/90/120."
# Pattern: "^\\s*#.*##.*$" #############################################################################################
#       include: ["**/*.py"]
#       exclude: []
#       flags: ["MULTILINE"]
#       params: {"allowed_lengths": [30, 60, 90, 120]}
#
# CLI
#   • `"--root" <path>`   (optional; defaults to `"."`)
#   • `"--config" <path>` (optional; defaults to `"STYLE.yml"`)
#   • `"--dry-run"`       (optional; previews changes without writing)
#
# Examples
#   • `python fix_style.py --config STYLE.yml --root .`
#   • `python fix_style.py --dry-run`
########################################################################################################################

from __future__ import annotations

import argparse
import logging
import re
from dataclasses import dataclass
from typing import Callable, Pattern

import yaml

from nutil.io.file import *

## CONFIG ################################################################################

# The default logging configuration
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%H:%M:%S",
)


## DATA CLASSES ##########################################################################


@dataclass
class RuleSpec:
    """
    A compiled fixer rule loaded from the YAML config.

    Args:
        id: The rule identifier.
        description: The short rule summary.
        severity: The severity string (`"error"` or `"warning"`).
        pattern: The compiled regex pattern that matches violations to be fixed.
        include: The list of file-glob patterns this rule applies to.
        exclude: The list of file-glob patterns this rule should ignore.
        params: Optional key–value pairs for rule-specific behavior (e.g., `"allowed_lengths"`).
    """

    id: str
    description: str
    severity: str
    pattern: Pattern[str]
    include: List[str]
    exclude: List[str]
    params: Dict[str, Any]


@dataclass
class ConfigSpec:
    """
    A compiled configuration for the fixer.

    Args:
        include: The repository-level include globs (coarse gate).
        exclude: The repository-level exclude globs (coarse gate), merged with the defaults.
        prune_names: The directory basenames to prune during traversal (derived from the excludes).
        rules: The list of compiled `RuleSpec` instances.
    """

    include: List[str]
    exclude: List[str]
    prune_names: Set[str]
    rules: List[RuleSpec]


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
    "DOTALL": re.DOTALL,
    "IGNORECASE": re.IGNORECASE,
    "MULTILINE": re.MULTILINE,
    "VERBOSE": re.VERBOSE,
}

# The fixer IDs in a sensible order (banner → caps → trailing dot → inline lowercase)
FIX_ORDER: Tuple[str, ...] = (
    "hash-banner-length",
    "line-comment-capitalized",
    "line-comment-trailing-period",
    "inline-comment-lowercase",
)


## YAML LOADING ##########################################################################


def load_yaml_config(path: Path) -> ConfigSpec:
    """
    Loads and compiles the YAML configuration.

    Behavior:
        - Always merges the YAML `exclude:` with `DEFAULT_EXCLUDES` (order-preserving and deduplicated).
        - Computes the `prune_names` set from the merged `exclude` list (patterns ending with `"/**"`).

    Args:
        path: The path to the YAML configuration file.

    Returns:
        The compiled `ConfigSpec`.

    Raises:
        SystemExit: If the YAML file cannot be read or parsed, or a rule regex is invalid.
    """
    try:
        text = path.read_text(encoding="utf-8")
    except Exception as e:
        sys.exit(f"Could not read YAML config '{path}': {e}")

    try:
        data = yaml.safe_load(text) or {}
    except Exception as e:
        sys.exit(f"Invalid YAML in '{path}': {e}")

    include = list(data.get("include") or ["**/*"])

    # Always merge the defaults with the user excludes (even when the user specifies an empty list)
    exclude_yaml = list(data.get("exclude") or [])
    exclude = merge_globs(DEFAULT_EXCLUDES, exclude_yaml)

    rules: List[RuleSpec] = []
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
            RuleSpec(
                id=rid,
                description=str(r.get("description") or ""),
                severity=str(r.get("severity") or "warning").lower(),
                pattern=pat,
                include=list(r.get("include") or ["**/*"]),
                exclude=list(r.get("exclude") or []),
                params=dict(r.get("params") or {}),
            )
        )

    prune_names = globs_to_dirs(exclude)
    return ConfigSpec(include=include, exclude=exclude, prune_names=prune_names, rules=rules)


## FIXERS ################################################################################


def fix_hash_banner_length(line: str, rule: RuleSpec) -> Tuple[str, bool]:
    """
    Pads or trims the banner line to the target width and normalizes the spacing.

    Normalization (applies only to the banner line that matches the `rule.pattern`):
        - Ensures exactly one space after the leading hashes, then the title, then one space,
          then the trailing `#` run.
        - Chooses the trailing hash count so that the total width equals the target (30/60/90/120).

    Additionally:
        - If the `title` (text between leading and trailing `#`) is empty, selects the closest
          target width among {30, 60, 90, 120}; on ties, prefers the larger width.
        - If the current line length is already one of 30, 60, 90, or 120, it corrects the leading
          `#` count for that width and rebalances the trailing `#` run to preserve the width.

    Trim is performed only when the overflow past the target consists solely of spaces or `#`.

    Args:
        line: The input line to check and possibly modify.
        rule: The `RuleSpec` whose `pattern` gates execution.

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

    m = re.match(r"^(\s*)(#{1,})(.*)$", body)
    if not m:
        return line, False

    indent, hashes, rest = m.groups()
    leading = len(hashes)
    current_length = len(body)

    # Extract the title (content after the leading hashes, without trailing hashes/spaces)
    title = rest.lstrip()
    title = re.sub(r"[#\s]+$", "", title)

    # Mapping: exact target width → desired leading hash count
    exact_target_lengths: Dict[int, int] = {120: 1, 90: 2, 60: 3, 30: 4}

    # Choose the target width and desired leading hash count
    if not title:
        # No title: pick the closest target; break ties toward the larger width
        targets: Tuple[int, ...] = tuple(exact_target_lengths.keys())
        target_length = min(targets, key=lambda L: (abs(L - current_length), -L))
        desired_leading = exact_target_lengths[target_length]
    elif current_length in exact_target_lengths:
        target_length = current_length
        desired_leading = exact_target_lengths[current_length]
    else:
        # Title exists: use leading count to choose the canonical target
        if leading == 1:
            target_length, desired_leading = 120, 1
        elif leading == 2:
            target_length, desired_leading = 90, 2
        elif leading == 3:
            target_length, desired_leading = 60, 3
        else:
            target_length, desired_leading = 30, 4

    # Build the normalized prefix with one space after the leading hashes and one before trailing hashes
    base = f"{indent}{'#' * desired_leading}" + (f" {title} " if title else "")

    # Compute the trailing hash count to reach the target width
    hash_count = target_length - len(base)

    if hash_count < 0:
        # Fall back to safe trim at the target only if the overflow is purely spaces or '#'
        if len(body) > target_length and set(body[target_length:]) <= {"#", " "}:
            return body[:target_length] + nl, True
        return line, False

    new_body = f"{base}{'#' * hash_count}"

    if new_body == body:
        return line, False
    return new_body + nl, True


def fix_line_comment_capitalized(line: str, rule: RuleSpec) -> Tuple[str, bool]:
    """
    Capitalizes the first alphabetic character in a one-line Python comment.

    Triggered only when the `rule.pattern` matches.

    Args:
        line: The input line to check and possibly modify.
        rule: The `RuleSpec` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    if not rule.pattern.search(line):
        return line, False
    nl = "\n" if line.endswith("\n") else ""
    body = line[:-1] if nl else line
    m = re.match(r"^(\s*#\s*)([a-z])(.*)$", body)
    if not m:
        return line, False
    prefix, first, rest = m.groups()
    return f"{prefix}{first.upper()}{rest}{nl}", True


def fix_line_comment_trailing_period(line: str, rule: RuleSpec) -> Tuple[str, bool]:
    """
    Removes a single trailing `.` in a one-line Python comment.

    Triggered only when the `rule.pattern` matches (e.g., URL exclusions handled in YAML).

    Args:
        line: The input line to check and possibly modify.
        rule: The `RuleSpec` whose `pattern` gates execution.

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


def fix_inline_comment_lowercase(line: str, rule: RuleSpec) -> Tuple[str, bool]:
    """
    Lowercases the first letter of an inline (end-of-line) comment.

    Triggered only when the `rule.pattern` matches.
    A typical pattern is `^(?!\s*#).*?\S[ \t]{2,}#\s+[A-Z]`.

    Args:
        line: The input line to check and possibly modify.
        rule: The `RuleSpec` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    if not rule.pattern.search(line):
        return line, False
    nl = "\n" if line.endswith("\n") else ""
    body = line[:-1] if nl else line

    m = re.match(r"^(?P<left>(?!\s*#).*?\S[ \t]{2,}#\s+)(?P<first>[A-Z])(?P<rest>.*)$", body)
    if not m:
        return line, False
    left, first, rest = m.group("left"), m.group("first"), m.group("rest")
    return f"{left}{first.lower()}{rest}{nl}", True


## FIXER REGISTRY ########################################################################

FixerFn = Callable[[str, RuleSpec], Tuple[str, bool]]

FIXERS: Dict[str, FixerFn] = {
    "hash-banner-length": fix_hash_banner_length,
    "line-comment-capitalized": fix_line_comment_capitalized,
    "line-comment-trailing-period": fix_line_comment_trailing_period,
    "inline-comment-lowercase": fix_inline_comment_lowercase,  # optional rule
}


## FILE PROCESSOR ########################################################################


def process_file(path: Path, rules: Sequence[RuleSpec]) -> Dict[str, int]:
    """
    Applies all active rule fixes to a file.

    Args:
        path: The file path to process.
        rules: The ordered sequence of active `RuleSpec` instances.

    Returns:
        The dictionary of `{rule_id: count_of_line_changes}`.
    """
    try:
        orig = path.read_text(encoding="utf-8", errors="ignore")
    except Exception as e:
        logging.warning("Could not read '%s': %s", path, e)
        return {}

    ordered = sorted(
        [r for r in rules if r.id in FIXERS],
        key=lambda r: (FIX_ORDER.index(r.id) if r.id in FIX_ORDER else len(FIX_ORDER), r.id),
    )

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


## WALKER ################################################################################


def run(root: Path, config: ConfigSpec, dry_run: bool = False) -> int:
    """
    Walks the tree, prunes the excluded directories, and applies the fixes.

    Args:
        root: The repository root to scan.
        config: The compiled configuration to use.
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
            if should_exclude_dir(rel_child, config.exclude):
                continue
            kept.append(dirname)
        dirnames[:] = kept

        for name in filenames:
            rel_file = join_posix_paths(rel_dir, name)
            abs_file = dir_path / name

            # Coarse gate: the repo-level include/exclude
            if should_exclude_file(rel_file, config.exclude, config.include):
                continue

            # Coarse gate: the union of the rule-level include/exclude
            if should_exclude_file(rel_file, rule_union_excludes, rule_union_includes):
                continue

            # Determine the active rules for this file (the rule-level include/exclude)
            active_rules = [
                r for r in fixable_rules if not should_exclude_file(rel_file, r.exclude, r.include)
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


def preview_file(path: Path, rules: Sequence[RuleSpec]) -> Dict[str, int]:
    """
    Reports what would change for a file without writing.

    Args:
        path: The file path to preview.
        rules: The ordered sequence of active `RuleSpec` instances.

    Returns:
        The dictionary of `{rule_id: count_of_line_changes}`.
    """
    try:
        orig = path.read_text(encoding="utf-8", errors="ignore")
    except Exception as e:
        logging.warning("Could not read '%s': %s", path, e)
        return {}

    ordered = sorted(
        [r for r in rules if r.id in FIXERS],
        key=lambda r: (FIX_ORDER.index(r.id) if r.id in FIX_ORDER else len(FIX_ORDER), r.id),
    )

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
        FileNotFoundError: If the input paths are invalid.
    """
    args = _build_arg_parser().parse_args()
    # Resolve the paths
    args.root = resolve_path(args.root)
    args.config = load_yaml_config(resolve_path(args.config))
    return args


def _build_arg_parser() -> argparse.ArgumentParser:
    """Builds the CLI argument parser."""
    ap = argparse.ArgumentParser(
        description="Fix simple coding-style issues based on 'STYLE.yml' rules."
    )
    # The paths
    ap.add_argument("--root", default=".", help="Root directory to scan.")
    ap.add_argument("--config", default="STYLE.yml", help="Path to YAML config.")
    # The flags
    ap.add_argument(
        "--dry-run",
        action="store_true",
        help="Report changes without writing files.",
    )
    return ap


## MAIN ##################################################################################


if __name__ == "__main__":
    args = parse_args()
    logging.info("Run '%s' with args: %s", Path(__file__).name, args)
    run(args.root, args.config, dry_run=args.dry_run)
