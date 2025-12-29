#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide scalar utilities for strings.
########################################################################################################################

from __future__ import annotations

import random
import re
import string

from nutil.common import *

__STRING_CONSTANTS________________________________________________________________________ = ""

# The basic Latin ranges (for regex character classes, e.g., `"[a-z]"`)
LOWERCASE_LATIN_RANGE: str = "a-z"
UPPERCASE_LATIN_RANGE: str = "A-Z"

# The language-specific letters (literal characters, not regex ranges)
LOWERCASE_DE_SPECIAL_LETTERS: str = "äöüß"
UPPERCASE_DE_SPECIAL_LETTERS: str = "ÄÖÜẞ"

LOWERCASE_FR_SPECIAL_LETTERS: str = "àâæçéèêëîïôœùûüÿ"
UPPERCASE_FR_SPECIAL_LETTERS: str = "ÀÂÆÇÉÈÊËÎÏÔŒÙÛÜŸ"

# The combined sets (intended for regex character classes, e.g., `f"[{LETTERS}]"`)
LOWERCASE_LETTERS: str = f"{LOWERCASE_LATIN_RANGE}{LOWERCASE_DE_SPECIAL_LETTERS}{LOWERCASE_FR_SPECIAL_LETTERS}"
UPPERCASE_LETTERS: str = f"{UPPERCASE_LATIN_RANGE}{UPPERCASE_DE_SPECIAL_LETTERS}{UPPERCASE_FR_SPECIAL_LETTERS}"
LETTERS: str = f"{LOWERCASE_LETTERS}{UPPERCASE_LETTERS}"

# The allowed digits (for regex character classes, e.g., `"[0-9]"`)
DIGIT_RANGE: str = "0-9"

# The combined set of alphanumeric characters (for regex character classes, e.g., `f"[{ALPHANUMERIC_CHARS}]"`)
ALPHANUMERIC_CHARS: str = f"{LETTERS}{DIGIT_RANGE}"


__STRING_CONVERTERS_______________________________________________________________________ = ""


def to_string(
    x: Any,
    *,
    default: str = "",
    delimiter: str = ",",
    strip: Optional[str] = None,
) -> Any:
    """
    Converts the specified object to a string-like representation.

    Behavior:
        • If `x` is null, returns `default`.
        • If `x` is a structure:
          - if it exposes `astype`, returns `x.astype(STRING_ELEMENT_TYPE)` (array/Series-like)
          - otherwise collapses the structure via `collapse(…)`.
        • Otherwise stringifies `x` via `stringify(…)`.

    Notes:
        The return type is intentionally polymorphic to preserve array/Series semantics when `astype` is available.

    Args:
        x: The object to convert.
        default: The default string returned when `x` is null.
        delimiter: The delimiter used for collapsing structures.
        strip: Optional strip argument forwarded to `collapse(…)` / `stringify(…)`.

    Returns:
        A string (scalar) or a string-typed structure (when `astype` is available).
    """
    if is_null(x):
        return default
    elif is_struct(x):
        if has_callable(x, "astype"):
            return x.astype(STRING_ELEMENT_TYPE)
        return collapse(x, default=default, delimiter=delimiter, strip=strip)
    return stringify(x, default=default, strip=strip)


### LETTERS ################################################


def to_lowercase(name: Optional[str]) -> Optional[str]:
    """Returns `name.lower()` when `name` is not null, otherwise null."""
    return name.lower() if not is_null(name) else None


def to_uppercase(name: Optional[str]) -> Optional[str]:
    """Returns `name.upper()` when `name` is not null, otherwise null."""
    return name.upper() if not is_null(name) else None


### LETTERS ################################################


def to_greek_letter(n: int) -> str:
    """
    Converts `1` → `"α"`, `2` → `"β"`, … using a 24-letter cycle (supports >24 as `"αα"`, `"αβ"`, …).

    Raises:
        ValueError: If `n` is non-positive.
    """
    if n <= 0:
        raise ValueError("'n' must be >= 1")

    # Greek lowercase letters (24 letters, excluding final sigma)
    greek_letters = "αβγδεζηθικλμνξοπρστυφχψω"

    letters: List[str] = []
    x = n
    while x > 0:
        x -= 1
        letters.append(greek_letters[x % 24])
        x //= 24
    return "".join(reversed(letters))


def to_latin_letter(n: int) -> str:
    """
    Converts `1` → `"a"`, `2` → `"b"`, … using a 26-letter cycle (supports >26 as `"aa"`, `"ab"`, …).

    Raises:
        ValueError: If `n` is non-positive.
    """
    if n <= 0:
        raise ValueError("'n' must be >= 1")
    letters: List[str] = []
    x = n
    while x > 0:
        x -= 1
        letters.append(chr(ord("a") + (x % 26)))
        x //= 26
    return "".join(reversed(letters))


def to_roman(n: int) -> str:
    """
    Converts an integer `n` (>= 1) to a Roman numeral.

    Raises:
        ValueError: If `n` is non-positive.
    """
    if n <= 0:
        raise ValueError("'n' must be >= 1")

    vals: List[Tuple[int, str]] = [
        (1000, "M"),
        (900, "CM"),
        (500, "D"),
        (400, "CD"),
        (100, "C"),
        (90, "XC"),
        (50, "L"),
        (40, "XL"),
        (10, "X"),
        (9, "IX"),
        (5, "V"),
        (4, "IV"),
        (1, "I"),
    ]
    out: List[str] = []
    x = n
    for v, s in vals:
        if x == 0:
            break
        q, x = divmod(x, v)
        out.append(s * q)
    return "".join(out)


__STRING_GENERATORS_______________________________________________________________________ = ""


def generate_string(
    length: int,
    case_sensitive: bool = False,
    include_digits: bool = True,
) -> str:
    """
    Generates a pseudorandom, uniformly distributed string of the specified length.

    Args:
        length: The output length (must be non-negative).
        case_sensitive: When `True`, includes lowercase letters.
        include_digits: When `True`, includes digits.

    Returns:
        A randomly generated string.

    Raises:
        ValueError: If `length` is negative.
    """
    if length < 0:
        raise ValueError("'length' must be >= 0")

    choices = string.ascii_uppercase
    if case_sensitive:
        choices += string.ascii_lowercase
    if include_digits:
        choices += string.digits
    return collapse(random.choices(choices, k=length))


__STRING_PROCESSORS_______________________________________________________________________ = ""


def extract(s: Optional[str], pattern: str) -> List[str]:
    """
    Returns all the occurrences of the specified pattern from the specified string.

    Notes:
        • Returns an empty list when `s` is null.

    Args:
        s: The input string.
        pattern: The regex pattern.

    Returns:
        The list of matches (possibly empty).
    """
    if is_null(s):
        return []

    return re.findall(pattern, s)


##############################


def replace(s: Optional[str], pattern: str, replacement: str) -> Optional[str]:
    """
    Returns the string constructed by replacing the specified pattern by the specified replacement
    string in the specified string recursively (only if the length is decreasing).

    Notes:
        • Returns `None` when `s` is null.
        • Recursion is bounded by the invariant that the string length must strictly decrease each iteration.

    Args:
        s: The input string.
        pattern: The regex pattern to replace.
        replacement: The replacement string.

    Returns:
        The replaced string, or `None` if `s` is null.
    """
    if is_null(s):
        return None

    count = INF
    while len(s) < count:
        count = len(s)
        s = re.sub(pattern, replacement, s)
    return s


def replace_word(s: Optional[str], word: str, replacement: str) -> Optional[str]:
    """
    Returns the string constructed by replacing the specified word by the specified replacement
    string in the specified string recursively (only if the length is decreasing).

    Notes:
        • Returns `None` when `s` is null.
        • Uses `re.escape(word)` so `word` is treated literally (e.g., `"C++"` is safe).

    Args:
        s: The input string.
        word: The literal word to replace.
        replacement: The replacement string.

    Returns:
        The replaced string, or `None` if `s` is null.
    """
    return replace(s, r"\b" + re.escape(word) + r"\b", replacement)


##############################


def split(
    s: Optional[str],
    delimiter: str = ",",
    empty_filter: bool = True,
) -> List[str]:
    """
    Returns all the tokens computed by splitting the specified string around the specified delimiter
    (regular expression).

    Notes:
        • Returns an empty list when `s` is null.

    Args:
        s: The input string.
        delimiter: The regex delimiter.
        empty_filter: When `True`, removes empty tokens.

    Returns:
        The list of tokens (possibly empty).
    """
    if is_null(s):
        return []

    if empty_filter:
        from nutil.struct.util import remove_empty

        return remove_empty(re.split(delimiter, s))
    return re.split(delimiter, s)


def split_line(line: str) -> Tuple[str, str]:
    """
    Splits `line` into `(line_without_eol, eol)` while preserving the exact line terminator.

    Args:
        line: The line to split.

    Returns:
        A tuple `(line_without_eol, eol)` where `eol` is one of `""`, `"\r\n"`, `"\r"`, or `"\n"`.
    """
    if line.endswith(f"{CARRIAGE_RETURN}{NEWLINE}"):
        return line[:-2], f"{CARRIAGE_RETURN}{NEWLINE}"
    elif line.endswith(CARRIAGE_RETURN):
        return line[:-1], CARRIAGE_RETURN
    elif line.endswith(NEWLINE):
        return line[:-1], NEWLINE
    return line, ""


##############################


def strip_line(line: str) -> str:
    """
    Strips the line terminator from the specified line.

    Behavior:
        • Removes exactly one trailing line terminator:
          - `"{CARRIAGE_RETURN}{NEWLINE}"` (CRLF) → strips 2 chars
          - `"{CARRIAGE_RETURN}"` (CR) → strips 1 char
          - `"{NEWLINE}"` (LF) → strips 1 char
        • Does NOT strip other trailing whitespace (unlike `rstrip()`).

    Args:
        line: The input line.

    Returns:
        The line without a trailing line terminator.
    """
    if line.endswith(f"{CARRIAGE_RETURN}{NEWLINE}"):
        return line[:-2]
    if line.endswith(CARRIAGE_RETURN) or line.endswith(NEWLINE):
        return line[:-1]
    return line


def strip_pair(
    s: Optional[str],
    left: str = "(",
    right: str = ")",
    *,
    recursive: bool = True,
    strip_space: bool = True,
) -> Optional[str]:
    """
    Recursively strips matching wrapping pairs from the string.

    Examples:
        • `strip_pair("((a))")` → `"a"`
        • `strip_pair("  ( a )  ")` → `"a"` (when `strip_space=True`)
        • `strip_pair('""x""', left='"', right='"')` → `"x"`
        • `strip_pair("{[x]}", left="{", right="}")` → `"[x]"`

    Notes:
        • Returns `None` when `s` is null.
        • This function strips only when the string starts with `left` AND ends with `right`.
        • When `left == right` (e.g., quotes), it strips symmetric wrappers.

    Args:
        s: The input string.

        left: The left wrapper string.
        right: The right wrapper string.
        recursive: When `True`, strips repeatedly until the wrapper no longer matches.
        strip_space: When `True`, applies `strip()` before each wrapper check and after each stripping step.

    Returns:
        The stripped string, or `None` if `s` is null.

    Raises:
        ValueError: If `left` or `right` is empty.
    """
    if is_null(s):
        return None
    if is_empty(left):
        raise ValueError("The left wrapper is empty")
    if is_empty(right):
        raise ValueError("The right wrapper is empty")

    out = str(s)
    if strip_space:
        out = out.strip()

    left_len = len(left)
    right_len = len(right)

    while True:
        if len(out) < left_len + right_len:
            return out
        if not out.startswith(left) or not out.endswith(right):
            return out

        inner = out[left_len : len(out) - right_len]
        if strip_space:
            inner = inner.strip()

        # Prevent infinite loops
        if inner == out:
            return out

        out = inner
        if not recursive:
            return out


def strip_pairs(
    s: Optional[str],
    pairs: Iterable[Tuple[str, str]],
    *,
    recursive: bool = True,
    strip_space: bool = True,
) -> Optional[str]:
    """
    Recursively strips any of the provided wrapping pairs.

    Behavior:
        • Returns `None` when `s` is null.
        • Attempts to strip one of the `pairs` from the outside.
        • If a pair matches, strips it and repeats (when `recursive=True`).
        • If no pair matches, returns the current string.

    Args:
        s: The input string.
        pairs: Iterable of `(left, right)` wrapper pairs.
        recursive: When `True`, repeats until no pair matches.
        strip_space: When `True`, applies `strip()` before each wrapper check and after each stripping step.

    Returns:
        The stripped string, or `None` if `s` is null.
    """
    if is_null(s):
        return None

    out = str(s)
    if strip_space:
        out = out.strip()

    # Prefer longer wrappers first (e.g., `"{{"` before `"{"`)
    ordered_pairs = sorted(list(pairs), key=lambda p: (len(p[0]) + len(p[1])), reverse=True)

    while True:
        changed = False
        for left, right in ordered_pairs:
            if is_empty(left) or is_empty(right):
                continue
            if out.startswith(left) and out.endswith(right) and len(out) >= len(left) + len(right):
                out = out[len(left) : len(out) - len(right)]
                if strip_space:
                    out = out.strip()
                changed = True
                break
        if not recursive or not changed:
            return out


##############################


def trim(
    s: Optional[str],
    replace_space: bool = True,
    replace_special: bool = True,
) -> Optional[str]:
    """
    Returns the string constructed by stripping the specified string (and replacing recursively the
    adjacent spaces if `replace_space` is `True` and/or special characters if `replace_special` is
    `True` to a single space).

    Notes:
        • Returns `None` if `s` is null.
        • When `replace_special=True`, replaces (best-effort) the following with a space:
          `BACKSPACE`, `FORM_FEED`, `TABULATION`, `CARRIAGE_RETURN+NEWLINE`, `CARRIAGE_RETURN`, `NEWLINE`.
          The special-character pattern is regex-escaped to avoid ambiguity (e.g., `"\b"` in regex).

    Args:
        s: The input string.
        replace_space: When `True`, collapses adjacent spaces to a single space (regex `" +"`).
        replace_special: When `True`, replaces special characters with a single space.

    Returns:
        The trimmed string, or `None` if `s` is null.
    """
    if is_null(s):
        return None

    if replace_special:
        # Escape tokens for regex alternation, and prefer longer matches first (e.g., CRLF before CR/LF)
        tokens = [
            f"{CARRIAGE_RETURN}{NEWLINE}",
            CARRIAGE_RETURN,
            NEWLINE,
            TABULATION,
            FORM_FEED,
            BACKSPACE,
        ]
        tokens = sorted(tokens, key=len, reverse=True)
        pattern = "|".join(re.escape(t) for t in tokens)
        s = replace(s, pattern, " ")

    if replace_space:
        s = replace(s, " +", " ")

    return s.strip()


##############################


def wrap(content: Any, left: Any, *, right: Optional[Any] = None) -> Any:
    """Returns the wrapped representative string of the specified content."""
    if is_null(left):
        return content
    elif is_null(right):
        right = left

    if is_struct(content):
        from nutil.struct.util import apply

        return apply(content, wrap, left, right=right)
    return collapse(left, content, right)


def quote(content: Any) -> Any:
    """Returns the single-quoted representative string of the specified content."""
    return wrap(content, "'")


def dquote(content: Any) -> Any:
    """Returns the double-quoted representative string of the specified content."""
    return wrap(content, '"')


def par(content: Any) -> Any:
    """Returns the parenthesized representative string of the specified content."""
    return wrap(content, "(", ")")


def sbra(content: Any) -> Any:
    """Returns the bracketized representative string of the specified content."""
    return wrap(content, "[", "]")  # square brackets


def cbra(content: Any) -> Any:
    """Returns the braced representative string of the specified content."""
    return wrap(content, "{", "}")  # curly brackets
