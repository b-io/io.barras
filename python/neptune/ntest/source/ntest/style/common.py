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

import difflib
import logging
import re
from typing import (
    Callable,
    Match,
)

from nutil.common import *


## DIFFERENCES ###########################################################################


def get_text_window(
    text: str,
    from_index: int = 0,  # inclusive index into the `text`
    to_index: Optional[int] = None,  # exclusive index into the `text`
    max_length: Optional[int] = 200,
) -> str:
    """
    Slices a small, printable window from the `text`, adding the ellipses and visualizing the control characters.

    Behavior:
        • Converts `\n`, `\r`, `\t` to `⏎`, `␍`, `⇥` for display.
        • Prepends/appends `…` when the window is clipped.
        • Enforces the `max_length` if provided.

    Args:
        text: The source string.
        from_index: The inclusive start index within the `text` (defaults to `0`).
        to_index: The exclusive end index within the `text` (defaults to `len(text)`).
        max_length: The maximum window length (if set, limits the `to_index`).

    Returns:
        The printable snippet string.
    """
    if to_index is None:
        to_index = len(text)
    start = max(0, from_index)
    end = min(to_index, len(text))
    if max_length is not None:
        end = min(end, start + max_length)

    s = text[start:end].replace("\n", "⏎").replace("\r", "␍").replace("\t", "⇥")
    if start > 0:
        s = "…" + s
    if end < len(text):
        s += "…"
    return s


def get_diffs(old: str, new: str, context_length: int = 20, max_diffs: int = 100) -> List[str]:
    """
    Summarizes the differences between the `old` and the `new` as compact, context-rich snippets.

    Format:
        Each snippet shows a small window around the change and an inline mark:
        - `[-"old"]` for deletions.
        - `[+"new"]` for insertions.
        - `[-"old"][+"new"]` for replacements.

    Args:
        old: The original string.
        new: The revised string.
        context_length: The number of characters to include on each side of a change.
        max_diffs: The maximum number of snippets to emit before truncating.

    Returns:
        The list of snippet strings in change order.
    """
    out: List[str] = []
    sm = difflib.SequenceMatcher(a=old, b=new, autojunk=False)
    for tag, i1, i2, j1, j2 in sm.get_opcodes():
        if tag == "equal":
            continue

        # Choose a window around the `new`-string position for the user-facing context
        before = get_text_window(new[:j1], from_index=j1 - context_length)
        after = get_text_window(new[j2:], to_index=context_length)
        old_segment = get_text_window(old[i1:i2])
        new_segment = get_text_window(new[j1:j2])

        if tag == "replace":
            body = f'[-"{old_segment}"][+"{new_segment}"]'
        elif tag == "delete":
            body = f'[-"{old_segment}"]'
        elif tag == "insert":
            body = f'[+"{new_segment}"]'
        else:
            continue

        # Build the change snippet (show the character index in the `new` string)
        index = j1
        snippet = f"@char {index}  …{before}{body}{after}"
        out.append(snippet)

        if len(out) >= max_diffs:
            out.append("…(diff truncated)…")
            break
    return out


def get_diffs_with_pattern(
    text: str,
    pattern: re.Pattern[str],
    replacement: Union[str, Callable[[Match[str]], str]],
    *,
    context_length: int = 25,
    max_diffs: int = 50,
) -> List[str]:
    """
    Shows compact, context-rich previews for the replacements matched by the `pattern`.

    Behavior:
        • Finds the non-overlapping regex matches with `pattern.finditer(text)`.
        • Builds a window around each match using `get_text_window`.
        • Previews the replacement by calling `replacement(match)` if callable, else `match.expand(replacement)`.

    Args:
        text: The input text to scan.
        pattern: The compiled regular expression to search with.
        replacement: The replacement string (may use backreferences) or a callable.
        context_length: The number of characters to include on each side of the match.
        max_diffs: The maximum number of preview snippets to emit.

    Returns:
        The list of preview snippet strings in match order.
    """
    out: List[str] = []
    for m in pattern.finditer(text):
        # Choose a window around the match position for the user-facing context
        start, end = m.span()
        before = get_text_window(text[:start], from_index=start - context_length)
        after = get_text_window(text[end:], to_index=context_length)
        old_segment = get_text_window(text[start:end])

        # Preview the replacement without modifying the string
        try:
            new_segment_raw = replacement(m) if callable(replacement) else m.expand(replacement)
        except Exception:
            new_segment_raw = "<callable>"
        new_segment = get_text_window(new_segment_raw)

        # Build the change snippet
        snippet = f'…{before}[-"{old_segment}"][+"{new_segment}"]{after}'
        out.append(snippet)

        if len(out) >= max_diffs:
            logging.warning("…(diff truncated)…")
            break
    return out


def is_acceptable_diff(
    expected: str,
    actual: str,
    *,
    acceptable_differences: Set[str] = set(),
    acceptable_deletions: Set[str] = set(),
    acceptable_insertions: Set[str] = set(),
    acceptable_replacements: Set[Tuple[str, str]] = set(),
    case_sensitive: bool = False,
) -> bool:
    """
    Checks if the difference between two strings is acceptable based on configurable tolerance rules.

    Uses `difflib.SequenceMatcher` to analyze character-level differences and validates each
    difference against the provided acceptable patterns. Returns `True` only if ALL differences
    are within the acceptable tolerances.

    Args:
        expected: The expected/reference string to compare against.
        actual: The actual string to validate.
        acceptable_differences: Set of strings that are acceptable as both deletions and
            insertions. These will be automatically added to both `acceptable_deletions`
            and `acceptable_insertions`. Useful for bidirectional tolerances like hyphens.
        acceptable_deletions: Set of strings that are acceptable to be deleted from the
            expected string. For example, `{"u"}` would allow `"colour"` → `"color"`.
        acceptable_insertions: Set of strings that are acceptable to be inserted into the
            actual string. For example, `{"s"}` would allow `"math"` → `"maths"`.
        acceptable_replacements: Set of `(from_str, to_str)` tuples representing acceptable
            character replacements. For example, `{("en", "t"), ("t", "en")}` would allow
            bidirectional `"en"` <-> `"t"` substitutions.
        case_sensitive: If `False` (default), comparison is done using `casefold()` for
            Unicode-aware case-insensitive matching. If `True`, comparison is case-sensitive.

    Returns:
        `True` if all differences between expected and actual are within acceptable tolerances,
        `False` if any difference is not acceptable.

    Examples:
        >>> is_acceptable_diff("colour", "color", acceptable_deletions={"u"})
        >>> True

        >>> is_acceptable_diff("WiFi", "wi-fi", acceptable_differences={"-"})
        >>> True
    """

    if expected == actual:
        return True

    acceptable_deletions = acceptable_deletions.union(acceptable_differences)
    acceptable_insertions = acceptable_insertions.union(acceptable_differences)

    # Normalize both strings for caseless comparison
    if case_sensitive:
        a = expected
        b = actual
    else:
        a = expected.casefold()
        b = actual.casefold()

    # Check the differences
    sm = difflib.SequenceMatcher(a=a, b=b, autojunk=False)
    for tag, i1, i2, j1, j2 in sm.get_opcodes():
        if tag == "equal":
            # No difference, continue
            continue
        elif tag == "delete":
            # A text is deleted from `a` (expected)
            deleted_text = a[i1:i2]
            if deleted_text in acceptable_deletions:
                continue
            else:
                return False
        elif tag == "insert":
            # A text is inserted into `b` (actual)
            inserted_text = b[j1:j2]
            if inserted_text in acceptable_insertions:
                continue
            else:
                return False
        elif tag == "replace":
            # A text is replaced from `a` (expected) to `b` (actual)
            replaced_text = (a[i1:i2], b[j1:j2])
            if replaced_text in acceptable_replacements or (
                replaced_text[0] in acceptable_deletions
                and replaced_text[1] in acceptable_insertions
            ):
                continue
            else:
                return False
    return True


def sub(
    pattern: str,
    replacement: Union[str, Callable[[Match[str]], str]],
    old: str,
    flags: int = 0,
    label: str = "",
) -> str:
    """
    Substitutes the regex matches with the replacement while logging the compact previews of the changes.

    Strategy:
        1) Compiles the pattern with the flags.
        2) If there is at least one match, emits the preview snippets via `get_diffs_with_pattern`.
        3) Performs the actual substitution once with `re.sub`.

    Args:
        pattern: The regular-expression pattern.
        replacement: The replacement string (supports backreferences) or a callable.
        old: The original text to transform.
        flags: The regex flags to pass to `re.compile`.
        label: The optional label to include in the log messages.

    Returns:
        The transformed string with all substitutions applied.
    """
    pattern: re.Pattern[str] = re.compile(pattern, flags)

    # Peek first to decide whether to log
    if not pattern.search(old):
        return old

    # Show the change snippets
    for snippet in get_diffs_with_pattern(old, pattern, replacement):
        logging.warning("[clean:%s] %s", label or pattern, snippet)

    # Perform the actual substitution once
    new, n = pattern.subn(replacement, old)
    logging.debug("[clean:%s] number of replacements: %d", label or pattern, n)
    return new
