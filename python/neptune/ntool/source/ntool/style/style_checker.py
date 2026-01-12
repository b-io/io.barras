#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Run the repository-wide regex style checks defined in a YAML config.
#   • Prunes excluded directories during traversal (so `".venv"` is not scanned)
#   • Treats each rule's regex as a *violation* matcher
#   • Applies each rule only to files matching the rule's own `include`/`exclude`
#   • Exits `0` when no `"error"`-severity matches are found; otherwise `1`
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

from ntool.style.common import *
from nutil.io.file import *
from nutil.io.logging import configure_logging
from nutil.scalar.string import LETTERS

__STYLE_CHECKER_CONSTANTS_________________________________________________________________ = ""


### GLOBALS ################################################

MULTILINE_MAX_SPAN_CHARS: int = 1000


__STYLE_CHECKER_PROCESSORS________________________________________________________________ = ""


def scan_file(
    abs_path: Path,
    rules: List[StyleRule],
    *,
    # Read
    encoding: str = DEFAULT_ENCODING,
) -> List[Tuple[StyleRule, int, str]]:
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
        text = abs_path.read_text(encoding=encoding, errors="ignore")
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

    line_rules: List[StyleRule] = []
    multiline_rules: List[StyleRule] = []
    for rule in rules:
        if _is_multiline_rule(rule):
            multiline_rules.append(rule)
        else:
            line_rules.append(rule)

    # Apply the line-scoped rules line-by-line
    for line_number, line in enumerate(text.splitlines(), 1):
        for rule in line_rules:
            if rule.pattern.search(line):
                violations.append((rule, line_number, line))

    # Apply the multi-line rules to the whole text
    line_starts = list(_iter_line_start_offsets(text))
    for rule in multiline_rules:
        for m in _iter_multiline_matches(text, line_starts, rule):
            line_number, line_text = _get_match_last_line(text, m)
            violations.append((rule, line_number, line_text))

    return violations


### HELPERS ################################################


def _get_match_last_line(text: str, match: Any) -> Tuple[int, str]:
    """
    Selects the last line of a multi-line match, which is typically the offending header line.

    Args:
        text: The full file text.
        match: The regex match object.

    Returns:
        A tuple of `(line_number, line_text)` for the last line in the match.
    """
    block = match.group(0)
    rel_last_nl = block.rfind(NEWLINE)
    target_offset = match.start() if rel_last_nl < 0 else match.start() + rel_last_nl + 1

    line_number = text.count(NEWLINE, 0, target_offset) + 1

    line_start = text.rfind(NEWLINE, 0, target_offset)
    line_start = 0 if line_start < 0 else line_start + 1

    line_end = text.find(NEWLINE, target_offset)
    line_end = len(text) if line_end < 0 else line_end

    line_text = text[line_start:line_end].rstrip(CARRIAGE_RETURN)
    return line_number, line_text


def _is_line_anchored_pattern(pattern: str) -> bool:
    """
    Checks whether a regex pattern is line-anchored.

    Args:
        pattern: The regex source string.

    Returns:
        `True` if the pattern begins with optional inline flags followed by `^`, otherwise `False`.
    """
    return re.match(rf"^(?:\(\?[{LETTERS}]+\))*\^", pattern) is not None


def _is_multiline_rule(rule: StyleRule) -> bool:
    """
    Selects whether a rule should be applied to the whole file text.

    YAML regexes typically encode line breaks as `\\n` (two characters). Therefore, multi-line rules must be detected
    via escaped newline tokens or DOTALL usage.

    Args:
        rule: The style rule to classify.

    Returns:
        `True` if the rule must be applied to the whole file text, otherwise `False`.
    """
    pattern = rule.pattern.pattern
    if "\\r" in pattern or "\\n" in pattern:
        return True
    if (rule.pattern.flags & re.DOTALL) != 0:
        return True
    if "(?s)" in pattern:
        return True
    return False


def _iter_line_start_offsets(text: str) -> Iterable[int]:
    """
    Yields the offsets of each line start in `text`.

    Args:
        text: The full file text.

    Returns:
        The iterable of line-start offsets.
    """
    yield 0
    for i, ch in enumerate(text):
        if ch == NEWLINE:
            yield i + 1


def _iter_multiline_matches(text: str, line_starts: List[int], rule: StyleRule) -> Iterable[Any]:
    """
    Iterates multi-line regex matches in a safer way than `finditer(text)`.

    Strategy:
        • If the pattern is line-anchored (`^`), tries `match()` at each line start.
        • Otherwise, runs `search()` in bounded windows starting at each line start and deduplicates spans.

    Args:
        text: The full file text.
        line_starts: The precomputed line-start offsets in `text`.
        rule: The multi-line style rule to apply.

    Returns:
        The iterable of regex match objects.
    """
    n = len(text)

    if _is_line_anchored_pattern(rule.pattern.pattern):
        for pos in line_starts:
            if pos >= n:
                break
            endpos = min(n, pos + MULTILINE_MAX_SPAN_CHARS)
            m = rule.pattern.match(text, pos, endpos)
            if m:
                yield m
        return

    seen: Set[Tuple[int, int]] = set()
    for pos in line_starts:
        if pos >= n:
            break
        endpos = min(n, pos + MULTILINE_MAX_SPAN_CHARS)

        m = rule.pattern.search(text, pos, endpos)
        while m:
            span = (m.start(), m.end())
            if span not in seen:
                seen.add(span)
                yield m

            next_pos = m.end()
            if next_pos <= m.start():
                next_pos = m.start() + 1
            if next_pos >= endpos:
                break

            m = rule.pattern.search(text, next_pos, endpos)


__STYLE_CHECKER_RUNNERS___________________________________________________________________ = ""


def run(root: Path, config: StyleConfig) -> int:
    """
    Executes the regex lint across the repository.

    Args:
        root: The repository root directory to scan.
        config: The compiled style configuration to use.

    Returns:
        Exit code `0` on success (no `"error"`-severity violations), otherwise `1`.
    """
    any_error = False
    total_files = 0
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
            if not is_empty(violations):
                for rule, line_number, line_text in violations:
                    location = f"{rel_file}:{line_number}" if line_number else rel_file
                    level = logging.ERROR if rule.severity == "error" else logging.WARNING
                    logging.log(level, "[%s] [%s] %s", location, rule.id, rule.description)
                    if line_text:
                        logging.log(level, "[%s] [%s] %s", location, rule.id, line_text)
                    if rule.severity == "error":
                        any_error = True
                total_violations += len(violations)
            total_files += 1

    if total_violations == 0:
        logging.info("✅ No coding-style violations found in %d file(s)", total_files)
    else:
        logging.error("❌ %d coding-style violation(s) found", total_violations)

    return 1 if any_error else 0


### ARGUMENTS ##############################################


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


def _build_arg_parser() -> argparse.ArgumentParser:
    """Builds the CLI argument parser."""
    ap = argparse.ArgumentParser(description="Regex-based style checks based on 'STYLE.yml' rules.")
    # Add the path(s)
    ap.add_argument("--config", help="Path to YAML config.", default="STYLE.yml")
    ap.add_argument("--root", help="Root directory to scan recursively.", default=".")
    return ap


### MAIN ###################################################


def main() -> None:
    configure_logging()
    args = parse_args()
    logging.info("Run '%s' with args: %s", Path(__file__).name, args)
    run(args.root, args.config)


if __name__ == "__main__":
    main()
