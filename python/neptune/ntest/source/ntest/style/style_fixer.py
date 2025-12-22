#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Fix coding-style issues in-place based on the `"STYLE.yml"` rules.
#   • Pads or trims hash banners to the target lengths 30/60/90/120 based on the leading `#`:
#     - 1 leading `#`  → 120 (section),
#     - 2 leading `#`  → 90  (subsection),
#     - 3 leading `#`  → 60  (subsubsection),
#     - 4+ leading `#` → 30  (subsubsubsection).
#   • Treats a line as a hash banner only if it starts with `#` (optionally spaced) and contains `##` somewhere.
#   • Trims only when the overflow past the target consists solely of spaces or `#`.
#   • Normalizes underscore banners to one of the allowed lengths 35/65/95/125 (keeps the current allowed length when
#     possible, otherwise uses the nearest allowed length).
#   • Removes a single trailing period from a one-line Python comment.
#   • Capitalizes the first alphabetic letter in a one-line Python comment.
#   • Optionally lowercases the first letter of an inline end-of-line comment (when a matching rule exists).
#
# Behavior
#   • Reads the rule regexes and file globs from the YAML file and applies a fixer only when the rule `pattern` matches.
#   • Merges repository excludes with defaults and prunes the directories derived from excludes ending in `"/**"`.
#   • Processes only files selected by the repo-level and rule-level include/exclude globs.
#
# CLI
#   • `"--root" <path>`   (optional; defaults to `"."`)
#   • `"--config" <path>` (optional; defaults to `"STYLE.yml"`)
#   • `"--dry-run"`       (optional; previews the changes without writing)
#
# Examples
#   • `python fix_style.py --config STYLE.yml --root .`
#   • `python fix_style.py --dry-run`
########################################################################################################################

from __future__ import annotations

import argparse
import logging

from ntest.style.common import *
from nutil.io.file import *
from nutil.io.logging import configure_logging
from nutil.scalar.string import ALPHANUMERIC_CHARS, LOWERCASE_LETTERS, UPPERCASE_LETTERS

__STYLE_FIXER_CONSTANTS___________________________________________________________________ = ""


HASH_BANNER_TARGET_LENGTH_BY_LEADING: Dict[int, int] = {
    1: 120,
    2: 90,
    3: 60,
    4: 30,
}

UNDERSCORE_BANNER_ALLOWED_LENGTHS: Tuple[int, ...] = (35, 65, 95, 125)
UNDERSCORE_BANNER_TAIL = ' = ""'


__STYLE_FIXER_PROCESSORS__________________________________________________________________ = ""


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

    active_rules: List[StyleRule] = [r for r in rules if r.id in FIXERS or r.id in TEXT_FIXERS]
    counts: Dict[str, int] = {}

    # Apply the text-level fixers first (multi-line patterns)
    text = orig
    for rule in active_rules:
        fixer = TEXT_FIXERS.get(rule.id)
        if not fixer:
            continue
        new_text, change_count = fixer(text, rule)
        if change_count > 0:
            counts[rule.id] = counts.get(rule.id, 0) + change_count
            text = new_text

    # Apply the line-level fixers
    for line in text.splitlines(keepends=True):
        current_line = line
        for rule in active_rules:
            fixer = FIXERS.get(rule.id)
            if not fixer:
                continue
            fixed_line, has_changed = fixer(current_line, rule)
            if has_changed:
                counts[rule.id] = counts.get(rule.id, 0) + 1
                current_line = fixed_line

    if not is_empty(counts):
        logging.info(
            "[DRY RUN] '%s' → %s",
            path,
            ", ".join("%s: %d" % (k, v) for k, v in sorted(counts.items())),
        )
    return counts


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
    active_rules: List[StyleRule] = [r for r in rules if r.id in FIXERS or r.id in TEXT_FIXERS]

    counts: Dict[str, int] = {}
    has_file_changed = False

    # Apply the text-level fixers first (multi-line patterns)
    text = orig
    for rule in active_rules:
        fixer = TEXT_FIXERS.get(rule.id)
        if not fixer:
            continue

        new_text, change_count = fixer(text, rule)
        if change_count > 0:
            counts[rule.id] = counts.get(rule.id, 0) + change_count
            text = new_text
            has_file_changed = True

    # Apply the line-level fixers
    out_lines: List[str] = []
    for line in text.splitlines(keepends=True):
        current_line = line

        for rule in active_rules:
            fixer = FIXERS.get(rule.id)
            if not fixer:
                continue

            fixed_line, has_line_changed = fixer(current_line, rule)
            if has_line_changed:
                counts[rule.id] = counts.get(rule.id, 0) + 1
                current_line = fixed_line
                has_file_changed = True

        out_lines.append(current_line)

    if has_file_changed:
        write_text(path, "".join(out_lines))

    return counts


### BANNERS ################################################


def fix_hash_banner_length(line: str, rule: StyleRule) -> Tuple[str, bool]:
    """
    Pads or trims the hash banner to the target length and normalizes the spacing.

    Normalization (applies only to a hash banner that matches the `rule.pattern`):
        • Ensures exactly one space after the leading hashes, then the title, then one space,
          then the trailing `#` run.
        • Chooses the trailing hash count so that the total length equals the target (30/60/90/120).

    Args:
        line: The input line to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    if not rule.pattern.search(line):
        return line, False

    newline = "\n" if line.endswith("\n") else ""
    body = line[:-1] if newline else line

    # Treat a line as a hash banner only if it starts with `#` and contains `##` somewhere
    if not re.match(r"^\s*#", body):
        return line, False
    if "##" not in body:
        return line, False

    m = re.match(r"^(\s*)(#{1,})([^\n]*)$", body)
    if is_null(m):
        return line, False

    indent, hashes, rest = m.groups()
    leading = len(hashes)

    # Extract the title without trailing hashes or spaces
    title = rest.lstrip()
    title = re.sub(r"[\s#]+$", "", title)

    # Select the target length based only on the number of leading hashes
    target = _get_target_hash_banner_length(leading)

    # Build the normalized prefix with one space after the leading hashes and one before the trailing hashes
    base = "%s%s" % (indent, "#" * leading)
    if title:
        base = "%s %s " % (base, title)

    # Compute the trailing hash length to reach the target length
    hash_count = target - len(base)

    if hash_count < 0:
        # Trim only when the overflow past the target consists solely of spaces or `#`
        if len(body) > target and set(body[target:]) <= {"#", " "}:
            trimmed = body[:target]
            if trimmed == body:
                return line, False
            return trimmed + newline, True
        return line, False

    new_body = "%s%s" % (base, "#" * hash_count)
    if new_body == body:
        return line, False

    return new_body + newline, True


def fix_underscore_banner_length(line: str, rule: StyleRule) -> Tuple[str, bool]:
    """
    Normalizes an underscore banner of the form `__NAME________________________ = ""`.

    Behavior:
        • Applies only when the `rule.pattern` matches (the YAML gate for underscore-banner length and format).
        • Preserves the leading indentation.
        • Ensures the symbolic name:
            - starts with `__`,
            - uses only uppercase letters/digits/underscores in the stored form,
            - collapses underscore runs to a single underscore between tokens,
            - strips stray leading/trailing underscores beyond the `__` prefix.
        • Reconstructs the tail as exactly ` = ""`.
        • Chooses the target length as:
            - the current length if it is one of 35/65/95/125,
            - otherwise the nearest allowed length (prefers the larger value on ties).
        • Ensures at least one underscore in the trailing bar.

    Args:
        line: The input line to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    if not rule.pattern.search(line):
        return line, False

    newline = "\n" if line.endswith("\n") else ""
    body = line[:-1] if newline else line

    # Indent + `__` + name + trailing bar + ` = ""`
    m = re.match(rf"^(\s*)(__)([{ALPHANUMERIC_CHARS}_]+?)(_+)\s*=\s*\"\"\s*$", body)
    if is_null(m):
        return line, False

    indent, prefix, name, trailing_bar = m.groups()

    # Normalize the symbolic part: uppercase letters, collapse underscore runs, strip stray underscores
    symbolic = re.sub(r"_+", "_", name.upper()).strip("_")
    if not symbolic:
        # Nothing meaningful to normalize; leave unchanged
        return line, False

    base_name = prefix + symbolic

    current_length = len(body)
    target = _get_nearest_allowed_length(current_length, UNDERSCORE_BANNER_ALLOWED_LENGTHS)

    # Compute the available length for the name and the trailing bar (after the indent and the tail)
    available = target - len(indent) - len(UNDERSCORE_BANNER_TAIL)
    if available <= 0:
        # No space left for the name; keep the original line
        return line, False

    # Ensure at least one underscore in the trailing bar
    name_max = max(0, available - 1)
    if name_max < len(prefix) + 1:
        return line, False

    normalized_name = base_name if len(base_name) <= name_max else base_name[:name_max]
    bar_len = available - len(normalized_name)
    if bar_len < 1:
        normalized_name = normalized_name[: max(0, available - 1)]
        bar_len = 1

    # Pad the trailing bar with underscores to reach the target length
    name_and_bar = "%s%s" % (normalized_name, "_" * bar_len)
    new_body = "%s%s%s" % (indent, name_and_bar, UNDERSCORE_BANNER_TAIL)

    if new_body == body:
        return line, False

    return new_body + newline, True


#### HELPERS #################


def _get_nearest_allowed_length(length: int, allowed: Tuple[int, ...]) -> int:
    """
    Selects the nearest allowed length, preferring the larger value on ties.

    Args:
        length: The measured length to snap.
        allowed: The ordered allowed lengths.

    Returns:
        The selected allowed length.
    """
    if length in allowed:
        return length
    return min(allowed, key=lambda x: (abs(x - length), -x))


def _get_target_hash_banner_length(leading_hash_count: int) -> int:
    """
    Selects the target hash-banner length from the leading hash count.

    Args:
        leading_hash_count: The number of leading `#` characters.

    Returns:
        The target length (30/60/90/120).
    """
    if leading_hash_count >= 4:
        return 30
    return HASH_BANNER_TARGET_LENGTH_BY_LEADING.get(leading_hash_count, 30)


### COMMENTS ###############################################


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

    newline = "\n" if line.endswith("\n") else ""
    body = line[:-1] if newline else line

    m = re.match(rf"^(\s*#\s*)([{LOWERCASE_LETTERS}])([^\n]*)$", body)
    if is_null(m):
        return line, False

    prefix, first, rest = m.groups()
    return "%s%s%s%s" % (prefix, first.upper(), rest, newline), True


def fix_inline_comment_lowercase(line: str, rule: StyleRule) -> Tuple[str, bool]:
    """
    Lowercases the first letter of an inline (end-of-line) comment.

    Triggered only when the `rule.pattern` matches.

    Args:
        line: The input line to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    if not rule.pattern.search(line):
        return line, False

    newline = "\n" if line.endswith("\n") else ""
    body = line[:-1] if newline else line

    m = re.match(rf"^(?P<left>(?!\s*#).*?\S[ \t]{2,}#\s+)(?P<first>[{UPPERCASE_LETTERS}])(?P<rest>[^\n]*)$", body)
    if is_null(m):
        return line, False

    left, first, rest = m.group("left"), m.group("first"), m.group("rest")
    return "%s%s%s%s" % (left, first.casefold(), rest, newline), True


def fix_line_comment_trailing_period(line: str, rule: StyleRule) -> Tuple[str, bool]:
    """
    Removes a single trailing `.` in a one-line Python comment.

    Triggered only when the `rule.pattern` matches.

    Args:
        line: The input line to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    if not rule.pattern.search(line):
        return line, False

    newline = "\n" if line.endswith("\n") else ""
    body = line[:-1] if newline else line

    r = body.rstrip()
    if r.endswith(".") and not r.endswith(".."):
        return r[:-1] + body[len(r) :] + newline, True

    return line, False


### IF / ELIF ##############################################


def fix_elif_subject_mismatch(text: str, rule: StyleRule) -> Tuple[str, int]:
    """
    Replaces `"elif"` with `"if"` when the `rule.pattern` detects a subject mismatch.

    Triggered only when the multi-line `rule.pattern` matches the file text.

    Args:
        text: The file text to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_text, change_count)`.
    """
    if not rule.pattern.search(text):
        return text, 0

    changed = 0

    def _repl(m: Any) -> str:
        nonlocal changed
        indent = m.group("indent")
        out, has_changed = _replace_last_keyword_in_match(m.group(0), indent, "elif", "if")
        if has_changed:
            changed += 1
        return out

    new_text = rule.pattern.sub(_repl, text)
    return new_text, changed


def fix_if_missing_elif(text: str, rule: StyleRule) -> Tuple[str, int]:
    """
    Replaces `"if"` with `"elif"` when the `rule.pattern` detects a missing `"elif"`.

    Triggered only when the multi-line `rule.pattern` matches the file text.

    Args:
        text: The file text to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_text, change_count)`.
    """
    if not rule.pattern.search(text):
        return text, 0

    changed = 0

    def _repl(m: Any) -> str:
        nonlocal changed
        indent = m.group("indent")
        out, has_changed = _replace_last_keyword_in_match(m.group(0), indent, "if", "elif")
        if has_changed:
            changed += 1
        return out

    new_text = rule.pattern.sub(_repl, text)
    return new_text, changed


#### HELPERS #################


def _replace_last_keyword_in_match(match_text: str, indent: str, from_kw: str, to_kw: str) -> Tuple[str, bool]:
    """
    Replaces the last occurrence of a line-leading keyword inside the matched block.

    Args:
        match_text: The full matched multi-line text.
        indent: The captured indentation to anchor the line-leading keyword.
        from_kw: The keyword to replace.
        to_kw: The keyword to insert.

    Returns:
        A tuple of `(possibly_modified_match_text, did_change)`.
    """
    needle = "\n%s%s" % (indent, from_kw)
    pos = match_text.rfind(needle)
    if pos >= 0:
        start = pos + 1 + len(indent)
    else:
        needle2 = "%s%s" % (indent, from_kw)
        pos2 = match_text.rfind(needle2)
        if pos2 < 0:
            return match_text, False
        start = pos2 + len(indent)

    end = start + len(from_kw)
    if match_text[start:end] != from_kw:
        return match_text, False
    if end < len(match_text) and match_text[end] not in {" ", "\t"}:
        return match_text, False

    return match_text[:start] + to_kw + match_text[end:], True


### MESSAGES ###############################################


def fix_error_message_trailing_period(line: str, rule: StyleRule) -> Tuple[str, bool]:
    """
    Removes a single trailing `.` from an exception message in a `raise …Error(…)` call.

    Triggered only when the `rule.pattern` matches.

    Args:
        line: The input line to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    m = rule.pattern.search(line)
    if is_null(m):
        return line, False

    newline = "\n" if line.endswith("\n") else ""
    body = line[:-1] if newline else line

    end = m.end()
    quote = m.group(1)

    if end >= 2 and body[end - 1] == quote and body[end - 2] == ".":
        new_body = body[: end - 2] + body[end - 1 :]
        return new_body + newline, True

    return line, False


def fix_logging_message_trailing_period(line: str, rule: StyleRule) -> Tuple[str, bool]:
    """
    Removes a single trailing `.` from a logging message in a `logging.<level>(…)` call.

    Triggered only when the `rule.pattern` matches.

    Args:
        line: The input line to check and possibly modify.
        rule: The `StyleRule` whose `pattern` gates execution.

    Returns:
        A tuple of `(possibly_modified_line, did_change)`.
    """
    m = rule.pattern.search(line)
    if is_null(m):
        return line, False

    newline = "\n" if line.endswith("\n") else ""
    body = line[:-1] if newline else line

    end = m.end()
    quote = m.group(1)

    if end >= 2 and body[end - 1] == quote and body[end - 2] == ".":
        new_body = body[: end - 2] + body[end - 1 :]
        return new_body + newline, True

    return line, False


__STYLE_FIXER_REGISTRIES__________________________________________________________________ = ""


Fixer = Callable[[str, StyleRule], Tuple[str, bool]]
TextFixer = Callable[[str, StyleRule], Tuple[str, int]]

FIXERS: Dict[str, Fixer] = {
    # Banners
    "hash-banner-length": fix_hash_banner_length,
    "underscore-banner-length": fix_underscore_banner_length,
    # Comments
    "inline-comment-lowercase": fix_inline_comment_lowercase,
    "line-comment-capitalized": fix_line_comment_capitalized,
    "line-comment-trailing-period": fix_line_comment_trailing_period,
    # Messages
    "error-message-trailing-period": fix_error_message_trailing_period,
    "logging-message-trailing-period": fix_logging_message_trailing_period,
}

TEXT_FIXERS: Dict[str, TextFixer] = {
    # IF / ELIF
    "elif-subject-mismatch-standalone": fix_elif_subject_mismatch,
    "elif-subject-mismatch-first-arg": fix_elif_subject_mismatch,
    "if-missing-elif-standalone": fix_if_missing_elif,
    "if-missing-elif-first-arg": fix_if_missing_elif,
}


__STYLE_FIXER_RUNNERS_____________________________________________________________________ = ""


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
    fixable_rules = [r for r in config.rules if r.id in FIXERS or r.id in TEXT_FIXERS]
    if not fixable_rules:
        logging.info("No known fixer rules found in config; nothing to do")
        return 0

    # The union of the rule-level include/exclude (a coarse filter to avoid opening irrelevant files)
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
            active_rules = [r for r in fixable_rules if not exclude_file(rel_file, r.exclude, r.include)]
            if not active_rules:
                continue

            total_files += 1
            counts = preview_file(abs_file, active_rules) if dry_run else process_file(abs_file, active_rules)

            for k, v in counts.items():
                total_counts[k] = total_counts.get(k, 0) + v

    parts = ["%s: %d" % (k, v) for k, v in sorted(total_counts.items())]
    logging.info(
        "✅ %s %d file(s)%s",
        ("[DRY RUN] " if dry_run else "") + "Fixed the coding style of",
        total_files,
        (" | " + ", ".join(parts)) if parts else "",
    )
    return 0


### ARGUMENTS ##############################################


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


def _build_arg_parser() -> argparse.ArgumentParser:
    """Builds the CLI argument parser."""
    ap = argparse.ArgumentParser(description="Fix simple coding-style issues based on 'STYLE.yml' rules.")
    # Add the path(s)
    ap.add_argument("--config", help="Path to the YAML config.", default="STYLE.yml")
    ap.add_argument("--root", help="Root directory to scan recursively.", default=".")
    # Add the save parameter(s)
    ap.add_argument(
        "--dry-run",
        help="Report changes without writing files.",
        action="store_true",
    )
    return ap


### MAIN ###################################################


def main() -> None:
    """Runs the fix style tool."""
    configure_logging()
    args = parse_args()
    logging.info("Run '%s' with args: %s", Path(__file__).name, args)
    run(args.root, args.config, dry_run=args.dry_run)


if __name__ == "__main__":
    main()
