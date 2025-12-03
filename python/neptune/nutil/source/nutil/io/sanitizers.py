#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

##########################################################################################
# NAME
#   <NAME> - contains io utility sanitizers
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

import html
from dataclasses import dataclass
from enum import Enum

import ftfy
import unicodedata

from nutil.scalar.string import *


## SANITIZERS ############################################################################

### CONFIG #################################################


class DehyphenationMode(Enum):
    """An enum for the dehyphenation strategies."""

    OFF = "off"
    CONSERVATIVE = "conservative"  # only join lowercase-to-lowercase across a line break
    AGGRESSIVE = "aggressive"  # join any letter-to-letter across a line break

    @classmethod
    def from_value(cls, value: str) -> "DehyphenationMode":
        """Parses a member by value, raising a uniform error on failure."""
        try:
            return cls(value)
        except ValueError as e:
            raise ValueError(f"'{value}' is not a valid value for '{cls.__name__}'") from e

    @classmethod
    def from_name(cls, name: str) -> "DehyphenationMode":
        """Parses a member by name, raising a uniform error on failure."""
        try:
            return cls[name]
        except KeyError as e:
            raise ValueError(f"'{name}' is not a valid name for '{cls.__name__}'") from e

    @classmethod
    def names(cls) -> List[str]:
        """Returns the list of member names."""
        return list(cls.__members__.keys())

    @classmethod
    def values(cls) -> List[str]:
        """Returns the list of member values."""
        return [m.value for m in cls]


@dataclass(frozen=True)
class SanitizeConfig:
    """
    A configuration container that holds the options for `clean_text`.

    Args:
        dehyphenation: The dehyphenation mode (`"off"`, `"conservative"`, `"aggressive"`).
        collapse_blank_lines: If `True`, collapses `"\n\n+"` to a single `"\n"`.
        normalize_quotes_and_dashes: If `True`, maps curly quotes/dashes/minus via a small table.
    """

    dehyphenation: DehyphenationMode = DehyphenationMode.CONSERVATIVE
    collapse_blank_lines: bool = True
    normalize_quotes_and_dashes: bool = True


### NORMALIZATION ##########################################

PUNCTUATION_NORMALIZATION: Dict[int, Union[str, int]] = str.maketrans(
    {
        # The dashes
        "–": "-",  # en dash
        "—": "-",  # em dash
        "\u2011": "-",  # non-breaking hyphen
        "\u2010": "-",  # hyphen
        "\u2212": "-",  # minus sign
        "\u25B6": "-",  # ▶ black right-pointing triangle
        "\u25BA": "-",  # ► black right-pointing pointer
        "\u25B8": "-",  # ▸ small right-pointing triangle
        # The quotes
        "“": '"',
        "”": '"',
        "„": '"',
        "’": "'",
        "‘": "'",
        "‚": "'",
        # The ellipsis
        "\u2026": "…",
    }
)

# The zero-width and bidirectional (BiDi) control characters (to be removed)
ZERO_WIDTH_AND_BIDI_MARKS_PATTERN: re.Pattern[str] = re.compile(
    r"[\u200B\u200C\u200D\u2060\uFEFF\u200E\u200F\u202A-\u202E\u2066-\u2069]"
)

# The non-breaking and thin-ish spaces (to be mapped to a regular space)
NO_BREAK_OR_THIN_SPACE_PATTERN: re.Pattern[str] = re.compile(
    r"[\u00A0\u202F\u2007\u2009\u200A]"
)  # nbsp, narrow no-break, figure, thin/hair

# The discretionary soft hyphen (to be removed)
SOFT_HYPHEN_PATTERN: re.Pattern[str] = re.compile("\u00AD")

# The form-feed controls (to be normalized to a newline)
FORM_FEED_CHARS_PATTERN: re.Pattern[str] = re.compile(r"[\f]+")

# The horizontal whitespace except newline (to be collapsed to a single space)
HORIZONTAL_WHITESPACE_EXCEPT_NEWLINE_PATTERN: re.Pattern[str] = re.compile(r"[^\S\n]+")

# The multiple consecutive newlines (to be collapsed to a single newline)
MULTIPLE_NEWLINES_PATTERN: re.Pattern[str] = re.compile(r"\n{2,}")

# The dehyphenation across line breaks
ANY_LETTER_HYPHEN_LINEBREAK_ANY_LETTER_PATTERN: re.Pattern[str] = re.compile(r"(?<=\w)-\n(?=\w)")
LOWERCASE_HYPHEN_LINEBREAK_LOWERCASE_PATTERN: re.Pattern[str] = re.compile(
    rf"(?<=[{LOWERCASE}])-\n(?=[{LOWERCASE}])"
)

### TEXT ###################################################


def clean_text(
    text: str,
    *,
    sanitize_config: Optional[SanitizeConfig] = None,
) -> str:
    """
    Cleans the raw text while preserving its meaningful structure.

    What this does (in order):
      1) Fixes mojibake via `ftfy` and normalizes Unicode (`"NFC"`).
      2) Removes the zero-width and directional marks; maps the NBSP-like spaces to a regular `" "`.
      3) Normalizes the dashes, hyphens, and minus; removes the discretionary soft hyphen.
      4) Dehyphenates across line breaks as per the configured mode.
      5) Normalizes the whitespace (keeps newlines) and collapses control chars (form feed → `"\n"`).
      6) Normalizes a few punctuation marks (the quotes and the ellipsis).

    Args:
        text: The raw input string to clean. If `text` is falsy, returns an empty string.
        sanitize_config: The `SanitizeConfig` controlling dehyphenation and punctuation normalization.

    Returns:
        The cleaned text with normalized Unicode, spacing, and punctuation.
    """
    if not text:
        return ""

    # Derive the config controlling the dehyphenation and the punctuation normalization
    sanitize_config = sanitize_config or SanitizeConfig()

    # 1) Unicode repair + normalization
    s = ftfy.fix_text(text)
    s = unicodedata.normalize("NFC", s)

    # 2) Remove the zero-width & BiDi markers; normalize the NBSP-like spaces
    s = ZERO_WIDTH_AND_BIDI_MARKS_PATTERN.sub("", s)
    s = s.replace("&nbsp;", "\u00A0")
    s = NO_BREAK_OR_THIN_SPACE_PATTERN.sub(" ", s)

    # 3) Remove the soft hyphen; normalize the newlines
    s = SOFT_HYPHEN_PATTERN.sub("", s)
    s = s.replace("\r\n", "\n").replace("\r", "\n")

    # 4) Normalize the quotes/dashes if enabled
    if sanitize_config.normalize_quotes_and_dashes:
        s = s.translate(PUNCTUATION_NORMALIZATION)
        # Optionally collapse exactly three ASCII dots into a single ellipsis (but not 4+ dots)
        s = re.sub(r"(?<!\.)\.\.\.(?!\.)", "…", s)

    # 5) Dehyphenate across line breaks
    if sanitize_config.dehyphenation is DehyphenationMode.AGGRESSIVE:
        s = ANY_LETTER_HYPHEN_LINEBREAK_ANY_LETTER_PATTERN.sub("", s)
    elif sanitize_config.dehyphenation is DehyphenationMode.CONSERVATIVE:
        s = LOWERCASE_HYPHEN_LINEBREAK_LOWERCASE_PATTERN.sub("", s)

    # 6) Whitespace normalization while preserving the line structure
    s = FORM_FEED_CHARS_PATTERN.sub("\n", s)  # form feed → newline
    s = HORIZONTAL_WHITESPACE_EXCEPT_NEWLINE_PATTERN.sub(
        " ", s
    )  # collapse horizontal whitespace runs
    if sanitize_config.collapse_blank_lines:
        s = MULTIPLE_NEWLINES_PATTERN.sub("\n", s)  # normalize multiple newlines

    return s.strip()


def minify_text(s: Optional[str]) -> Optional[str]:
    """
    Returns a single-line, whitespace-compacted string suitable for CSV cells.

    It uses the project cleaner (no dehyphenation surprises here), then collapses all
    whitespace (including newlines) to single spaces.
    """
    if not s:
        return None
    s = clean_text(s)  # keeps spelling as-is; normalize punctuation/spacing
    s = re.sub(r"\s+", " ", s)
    return s.strip()


def to_ascii(text: str) -> str:
    """
    Folds diacritics to ASCII by removing combining marks (best-effort).

    Example:
        `"Straße"` → `"Strasse"`, `"Curaçao"` → `"Curacao"`.

    Args:
        text: The input string.

    Returns:
        An ASCII-ish representation useful for search keys or filenames.
    """
    if not text:
        return ""
    s = unicodedata.normalize("NFKD", text)
    s = "".join(ch for ch in s if not unicodedata.combining(ch))
    return s.encode("ascii", "ignore").decode("ascii")


### HTML ###################################################


def minify_html(
    html_text: Optional[str], *, preserve_tags: Iterable[str] = ("pre", "code", "textarea")
) -> Optional[str]:
    """
    Returns a compact HTML string by collapsing whitespace and trimming spaces *between* tags.

    Guarantees:
      - Preserve the content inside the `preserve_tags` (no whitespace/spacing fixes there).
      - Collapse runs of whitespace to a single space elsewhere.
      - Remove whitespace between closing and opening tags (`"> <"` → `"><"`).
      - Leave the attribute/element order intact.

    Args:
        html_text: The HTML string to minify. If falsy, returns it unchanged.
        preserve_tags: The tag names whose inner HTML must be preserved verbatim.

    Returns:
        The minified HTML, or `None` if the result is empty after trimming.
    """
    if not html_text:
        return html_text

    s = clean_text(html_text)
    placeholders: Dict[str, str] = {}

    # 1) Stash the preserved blocks so nothing touches their contents
    for tag in preserve_tags:
        pattern = re.compile(rf"<{tag}\b[^>]*>.*?</{tag}>", re.IGNORECASE | re.DOTALL)

        def _stash(m: re.Match[str]) -> str:
            key = f"__PRESERVE_BLOCK_{len(placeholders)}__"
            placeholders[key] = m.group(0)
            return key

        s = pattern.sub(_stash, s)

    # 2) Normalize the entities and the whitespace (outside the preserved blocks)
    s = s.replace("&nbsp;", " ")
    s = re.sub(r">\s+<", "><", s)  # tighten inter-tag gaps

    # 3) Fix the invalid `<p>…<table>` nesting: close <p> before a table
    #    (HTML5 implicitly closes <p> before block-level elements; no reopen needed.)
    s = re.sub(r"(?is)<p>(.*?)\s*(?=<table\b)", r"<p>\1</p>", s)

    # 4) Adjust the micro-spacing around the tags vs. the letters (outside the preserved blocks)
    #    - Ensure a space after a closing tag when a letter follows
    #    - Ensure a space before an opening tag when preceded by a letter
    s = re.sub(rf"(</[^>]+>)(?=[{LETTERS}])", r"\1 ", s)
    s = re.sub(rf"(?<=[{LETTERS}])(<[^/!][^>]*>)", r" \1", s)

    # 5) Tighten the spaces around the quotes and the inline tags
    s = re.sub(r"\"\s+(<)", r'"\1', s)  # `" <span>"` → `"<span>"`
    s = re.sub(r"(>)\s+\"", r'\1"', s)  # `"</span> "` → `"</span>"`

    # 6) Restore the preserved blocks verbatim
    for key, block in placeholders.items():
        s = s.replace(key, block)

    return s.strip()


def strip_html_tags(html_text: str) -> str:
    """
    Strips HTML tags, preserving reasonable line breaks for block-level elements.

    It converts common block boundaries (`<p>`, `<div>`, headings, `<br>`, `<li>`, `<tr>`) to `"\n"`,
    unescapes HTML entities, and collapses leftover whitespace while keeping newlines.

    Args:
        html_text: The HTML snippet to strip.

    Returns:
        The plaintext with approximate structure preserved.
    """
    if not html_text:
        return ""

    s = html_text
    # Normalize the common block boundaries to newlines before stripping the tags
    s = re.sub(r"(?i)<\s*br\s*/?\s*>", "\n", s)
    s = re.sub(r"(?i)</\s*(p|div|h[1-6]|li|tr|table|ul|ol)\s*>", "\n", s)

    # Drop all remaining tags
    s = re.sub(r"(?s)<[^>]+>", "", s)

    # Unescape the entities after dropping the tags
    s = html.unescape(s)

    # Reuse the whitespace compactor semantics
    s = HORIZONTAL_WHITESPACE_EXCEPT_NEWLINE_PATTERN.sub(" ", s)
    s = MULTIPLE_NEWLINES_PATTERN.sub("\n", s)
    return s.strip()
