#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# COMMON UTILITIES #####################################################################################################
# Goal
#   Provide small, dependency-light helpers for path handling, glob matching, robust HTTP GET with retries,
#   atomic writes, Unicode/text sanitization (including BiDi/zero-width cleanup and dehyphenation), HTML minification,
#   and compact diff previews you can log during regex-driven edits.
#
# What This Module Offers
#   • Globs & Matching
#       - `get_dirnames_from_globs(globs, suffix="/**")`        → directory base names implied by directory globs
#       - `match_any_globs(rel_path, globs)`                → POSIX path vs. glob list (treats leading `"**/"` as optional)
#       - `merge_globs(primary, extra)`                     → stable merge (preserve order, drop dups)
#       - `should_exclude_dir(rel_dir, exclude)`            → prune directories by exclude patterns
#       - `should_exclude_file(rel_path, exclude, include)` → inclusion/exclusion gate for files
#
#   • Iterables
#       - `deduplicate(items)`  → remove duplicates while preserving order
#
#   • Networking (GET + retries with backoff)
#       - `build_session_with_retries(...)`         → `requests.Session` with Retry(429/5xx, backoff, headers)
#       - `request(session, url, ...)`              → polite GET with throttle; returns `Response | None`
#       - `request_json(session, url, ...)`         → `(status, obj|None)`; raises `RateLimitError` on 429/503
#       - `RateLimitError(api=None, message=None)`  → uniform, API-labelled rate-limit error
#       Defaults:
#         `DEFAULT_USER_AGENT`, `DEFAULT_ACCEPT`, `DEFAULT_TIMEOUT`, `DEFAULT_THROTTLE`
#
#   • IO
#       - `to_json(obj)`            → JSON-friendly projection (e.g., set → sorted list)
#       - `write_text(path, text)`  → atomic write with fsync best-effort
#
#   • Paths
#       - `join_posix_paths(a, b)`              → clean POSIX join
#       - `resolve_path(path, must_exist=True)` → search CWD & parents when `must_exist=True`
#       - `to_relative_posix_path(path, root)`  → POSIX-style relative ("" for root)
#
#   • Pattern Computation (German declensions)
#       - `get_declension(base, variant, allow_suffixes=...)`   → compact token: "-", "-e", "¨e", etc.
#
#   • Regex Helpers
#       - `build_regex_alternation(alternatives)`                       → longest-first non-capturing alternation
#       - `compile_regex_alternation_pattern(alternatives, flags=...)`  → whole-word compiled pattern
#
#   • String Sets (Latin + DE/FR letters)
#       - `LOWERCASE`, `UPPERCASE`, `LETTERS`   → handy for character-class ranges
#
#   • Text & HTML Sanitizing
#       - `DehyphenationMode` (`off` | `conservative` | `aggressive`)
#       - `SanitizeConfig(dehyphenation=..., collapse_blank_lines=True, normalize_quotes_and_dashes=True)`
#       - `clean_text(text, sanitize_config=None)`  → fix mojibake, remove zero-width/BiDi marks, normalize
#                                                          quotes/dashes/ellipsis, dehyphenate across line breaks,
#                                                          compact whitespace while preserving newlines
#       - `fold_to_ascii(text)`                     → best-effort diacritic folding (NFKD + strip combining)
#       - `minify_html(html_text, preserve_tags=("pre","code","textarea"))`
#                                                   → collapse inter-tag whitespace, keep preserved blocks intact,
#                                                     fix `<p>…<table>` nesting, micro-spacing around tags vs. letters
#       - `minify_text(s)`                          → single-line compaction for CSV cells
#       - `parse_json(s)`                           → safe `dict`-only parse (else `None`)
#       - `strip_html_tags(html_text)`              → plaintext with reasonable newlines for block elements
#       Regex constants clarify terminology:
#         • “BiDi marks” = bidirectional control chars; “NBSP-like” = `\u00A0`, `\u202F`, `\u2007`, `\u2009`, `\u200A`.
#
#   • Differences & Regex-Aware Previews
#       - `get_text_window(text, from_index=0, to_index=None, max_length=200)`  → compact printable slice (↵, ␍, ⇥)
#       - `get_diffs(old, new, context_length=20, max_diffs=100)`               → human-friendly change snippets
#       - `get_diffs_with_pattern(text, pattern, replacement, ...)`             → previews of regex replacements
#       - `sub(pattern, replacement, old, flags=0, label="")`                   → `re.sub` + logged previews
#
# Cache Helpers
#   • `CachePolicy` (Enum with string values) is provided for callers that persist caches.
#
# Key Behaviors & Guarantees
#   • Text cleaning never invents characters; it normalizes or removes control/formatting marks.
#   • Dehyphenation:
#       - `conservative`: join `lowercase-⏎lowercase` only (safer for headings/proper nouns).
#       - `aggressive`: join any `\w-⏎\w` pair.
#   • HTML minification never touches content within `preserve_tags`.
#   • Networking honors `Retry-After` and uses exponential backoff; callers can opt-in to raising on rate limits.
#
# Dependencies
#   • `ftfy`, `requests`, `urllib3` (via `Retry`), and Python stdlib.
#
# Importing
#   from text import (
#       clean_text, minify_html, fold_to_ascii,
#       build_session_with_retries, request_json,
#       join_posix_paths, resolve_path, sub,
#       SanitizeConfig, DehyphenationMode,
#   )
#
# Quick Examples
#   • Clean a blob (keep Unicode, conservative dehyphenation):
#       s = clean_text(raw_html_or_text)
#
#   • Clean with ASCII folding:
#       s = fold_to_ascii(clean_text(text, sanitize_config=SanitizeConfig(normalize_quotes_and_dashes=True)))
#
#   • Robust JSON GET:
#       sess = build_session_with_retries(headers={"Authorization": f"Bearer {token}"})
#       status, data = request_json(sess, "https://api.example.com/v1/thing", api_name="Example")
#
#   • Regex substitution with previews in logs (the `pattern` label appears in log lines):
#       new = sub(r"\s+\n", "\n", old, flags=0, label="trim_trailing_ws")
#
# Notes on Wording
#   • Docstrings sometimes use articles with identifiers for readability (e.g., “the `pattern`”), which is intentional.
########################################################################################################################


from __future__ import annotations

import re
from dataclasses import dataclass
from pathlib import Path

import yaml

from nutil.common import *  # existing import kept as-is
from nutil.io.file import get_dirnames_from_globs
from nutil.struct.collection.list import deduplicate
from nutil.struct.table.util import get_row_string

## STYLE CONSTANTS #######################################################################

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
    "IGNORECASE": re.I,
    "MULTILINE": re.MULTILINE,
    "VERBOSE": re.VERBOSE,
}


## STYLE CLASSES #########################################################################


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


## STYLE LOADING #########################################################################


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
