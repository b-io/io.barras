#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide io utility sanitizers.
########################################################################################################################

import html
from dataclasses import dataclass

import ftfy
import unicodedata

from nutil.enums import StrEnum
from nutil.scalar.string import *


## SANITIZERS ############################################################################

### CONFIG #################################################


class DehyphenationMode(StrEnum):
    """An enum for the dehyphenation strategies."""

    OFF: str = "off"
    CONSERVATIVE: str = "conservative"  # only joins lowercase-to-lowercase across a line break
    AGGRESSIVE: str = "aggressive"  # joins any letter-to-letter across a line break


@dataclass(frozen=True)
class SanitizeConfig:
    """
    A configuration container that holds the options for `clean_text`.

    Args:
        dehyphenation: The dehyphenation mode (`"off"`, `"conservative"`, `"aggressive"`).
        collapse_blank_lines: When `True`, collapses `"\n\n+"` to a single `"\n"`.
        normalize_quotes_and_dashes: When `True`, maps the curly quotes/dashes/minus via a small table.
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
        "\u25b6": "-",  # ▶ black right-pointing triangle
        "\u25ba": "-",  # ► black right-pointing pointer
        "\u25b8": "-",  # ▸ small right-pointing triangle
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

# The regex pattern to recognize zero-width and bidirectional (BiDi) control characters (to be removed)
ZERO_WIDTH_AND_BIDI_MARKS_PATTERN: re.Pattern[str] = re.compile(
    r"[\u200B\u200C\u200D\u2060\uFEFF\u200E\u200F\u202A-\u202E\u2066-\u2069]"
)

# The regex pattern to recognize non-breaking and thin-ish spaces (to be mapped to a regular space)
NO_BREAK_OR_THIN_SPACE_PATTERN: re.Pattern[str] = re.compile(
    r"[\u00A0\u202F\u2007\u2009\u200A]"
)  # nbsp, narrow no-break, figure, thin/hair

# The regex pattern to recognize discretionary soft hyphens (to be removed)
SOFT_HYPHEN_PATTERN: re.Pattern[str] = re.compile("\u00ad")

# The regex pattern to recognize form-feed controls (to be normalized to a newline)
FORM_FEED_CHARS_PATTERN: re.Pattern[str] = re.compile(r"[\f]+")

# The regex pattern to recognize horizontal whitespace except newline (to be collapsed to a single space)
HORIZONTAL_WHITESPACE_EXCEPT_NEWLINE_PATTERN: re.Pattern[str] = re.compile(r"[^\S\n]+")

# The regex pattern to recognize multiple consecutive newlines (to be collapsed to a single newline)
MULTIPLE_NEWLINES_PATTERN: re.Pattern[str] = re.compile(r"\n{2,}")

# The regex patterns to recognize hyphens across lines (to be collapsed to a single line)
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
      1) Fixes the mojibake via `ftfy` and normalizes the Unicode (`"NFC"`).
      2) Removes the zero-width and directional marks; maps the NBSP-like spaces to a regular `" "`.
      3) Normalizes the dashes, hyphens, and minus; removes the discretionary soft hyphen.
      4) Dehyphenates across the line breaks as per the configured mode.
      5) Normalizes the whitespace (keeps the newlines) and collapses the control chars (form feed → `"\n"`).
      6) Normalizes a few punctuation marks (the quotes and the ellipsis).

    Args:
        text: The raw input string to clean. If `text` is falsy, returns an empty string.
        sanitize_config: The `SanitizeConfig` controlling the dehyphenation and the punctuation normalization.

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
    s = s.replace("&nbsp;", "\u00a0")
    s = NO_BREAK_OR_THIN_SPACE_PATTERN.sub(" ", s)

    # 3) Remove the soft hyphen; normalize the newlines
    s = SOFT_HYPHEN_PATTERN.sub("", s)
    s = s.replace("\r\n", "\n").replace("\r", "\n")

    # 4) Normalize the quotes/dashes if enabled
    if sanitize_config.normalize_quotes_and_dashes:
        s = s.translate(PUNCTUATION_NORMALIZATION)
        # Optionally collapse exactly three ASCII dots into a single ellipsis (but not 4+ dots)
        s = re.sub(r"(?<!\.)\.\.\.(?!\.)", "…", s)

    # 5) Dehyphenate across the line breaks
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


def minify_text(text: Optional[str], *, new_line: str = " ") -> Optional[str]:
    """
    Returns a compact string by normalizing whitespace without changing the semantic spaces between text fragments.

    Behavior:
        • Collapses runs of whitespace (including newlines) to a single space.

    Args:
        text: The string to minify. If falsy, returns it unchanged.
        new_line: The replacement used for newline characters before whitespace collapsing.

    Returns:
        The minified string (`""` if it becomes empty). If `text` is falsy, it is returned unchanged.
    """
    if not text:
        return text

    # Clean the text
    s = clean_text(text)

    # Normalize the newlines explicitly, then collapse all the whitespace runs
    s = s.replace("\n", new_line)
    s = re.sub(r"\s{2,}", " ", s)

    return s.strip()


def normalize_text(text: str) -> str:
    """Normalizes a string with Unicode NFC and casefold for robust equality."""
    return clean_text(text).casefold()


def to_ascii(text: str) -> str:
    """
    Folds the diacritics to ASCII by removing the combining marks (best-effort).

    Example:
        `"Straße"` → `"Strasse"`, `"Curaçao"` → `"Curacao"`.

    Args:
        text: The input string.

    Returns:
        An ASCII-ish representation useful for the search keys or filenames.
    """
    if not text:
        return ""
    s = unicodedata.normalize("NFKD", text)
    s = "".join(ch for ch in s if not unicodedata.combining(ch))
    return s.encode("ascii", "ignore").decode("ascii")


### HTML ###################################################


def minify_html(html_text: Optional[str], *, new_line: str = " ") -> Optional[str]:
    """
    Returns a compact HTML string by normalizing whitespace without changing the semantic spaces between text fragments.

    Behavior:
        • Collapses runs of whitespace (including newlines) to a single space.
        • Normalizes `"&nbsp;"` to a regular space.
        • Keeps spaces that separate text across tags (e.g. `"</b> <i>"`) so that stripping tags does not join words.
        • Leaves the attribute/element order intact.

    Args:
        html_text: The HTML string to minify. If falsy, returns it unchanged.
        new_line: The replacement used for newline characters before whitespace collapsing.

    Returns:
        The minified HTML string (`""` if it becomes empty). Returns `None` only when the input is falsy.
    """
    if not html_text:
        return html_text

    # 1) Normalize the entities and the whitespace
    s = minify_text(html_text, new_line=new_line)
    s = s.replace("&nbsp;", " ")

    # 2) Fix the invalid `"<p>…<table>"` nesting: close `"<p>"` before a table
    #   (HTML5 implicitly closes <p> before block-level elements; no reopen needed.)
    s = re.sub(r"(?is)<p>(.*?)\s*(?=<table\b)", r"<p>\1</p>", s)

    # 3) Tighten the spaces around the quotes and inline tags
    s = re.sub(r"\"\s+(<)", r'"\1', s)  # `" <tag>"` → `"<tag>"`
    s = re.sub(r"(>)\s+\"", r'\1"', s)  # `"</tag> "` → `"</tag>"`

    return s.strip()


def strip_html_tags(s: Optional[str]) -> Optional[str]:
    """
    Strips the HTML tags, preserving reasonable line breaks for the block-level elements.

    It converts the common block boundaries (`"<p>"`, `"<div>"`, the headings, `"<br>"`, `"<li>"`, `"<tr>"`) to `"\n"`,
    unescapes the HTML entities, and collapses the leftover whitespace while keeping the newlines.

    Args:
        s: The HTML snippet to strip.

    Returns:
        The plaintext with the approximate structure preserved.
    """
    if not s:
        return None

    # Normalize the common block boundaries to the newlines before stripping the tags
    s = re.sub(r"(?i)<\s*br\s*/?\s*>", "\n", s)
    s = re.sub(r"(?i)</\s*(p|div|h[1-6]|li|tr|table|ul|ol)\s*>", "\n", s)

    # Drop all the remaining tags
    s = re.sub(r"(?s)<[^>]+>", "", s)

    # Unescape the entities after dropping the tags
    s = html.unescape(s)

    # Reuse the whitespace compactor semantics
    s = HORIZONTAL_WHITESPACE_EXCEPT_NEWLINE_PATTERN.sub(" ", s)
    s = MULTIPLE_NEWLINES_PATTERN.sub("\n", s)
    return s.strip()
