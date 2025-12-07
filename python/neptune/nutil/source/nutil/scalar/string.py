#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common utilities for strings.
########################################################################################################################

import random
import re
import string

from nutil.common import *

## STRING CONSTANTS ######################################################################

__STRING_CONSTANTS__________________________________________ = ""

NEWLINE = "\n"

BULLET = "•"
COLON = ":"
SEMICOLON = ";"

# The basic Latin ranges
LOWERCASE_LATIN: str = "a-z"
UPPERCASE_LATIN: str = "A-Z"

# The language-specific letters
LOWERCASE_DE: str = "äöüß"
UPPERCASE_DE: str = "ÄÖÜẞ"

LOWERCASE_FR: str = "àâæçéèêëîïôœùûüÿ"
UPPERCASE_FR: str = "ÀÂÆÇÉÈÊËÎÏÔŒÙÛÜŸ"

# The combined sets
LOWERCASE: str = f"{LOWERCASE_LATIN}{LOWERCASE_DE}{LOWERCASE_FR}"
UPPERCASE: str = f"{UPPERCASE_LATIN}{UPPERCASE_DE}{UPPERCASE_FR}"
LETTERS: str = f"{LOWERCASE}{UPPERCASE}"

# The allowed digits (e.g., `"MP3"`)
DIGITS: str = "0-9"
ALPHA_NUMERIC_CHARS: str = f"{LETTERS}{DIGITS}"


## STRING CONVERTERS #####################################################################

__STRING_CONVERTERS_________________________________________ = ""


def to_string(x: Any, *, default: str = "", delimiter=",", strip: Optional[str] = None):
    if is_null(x):
        return default
    elif is_collection(x):
        if has_callable(x, "astype"):
            return x.astype(STRING_ELEMENT_TYPE)
        return collapse(x, default=default, delimiter=delimiter, strip=strip)
    return stringify(x, default=default, strip=strip)


### LETTERS ################################################


def to_greek_letter(n: int) -> str:
    """Converts `1` → `"α"`, `2` → `"β"`, … using a 24-letter cycle (supports >24 as `"αα"`, `"αβ"`, …)."""
    if n <= 0:
        return ""

    # Greek lowercase letters (24 letters, excluding final sigma)
    greek_letters = "αβγδεζηθικλμνξοπρστυφχψω"

    letters = []
    x = n
    while x > 0:
        x -= 1
        letters.append(greek_letters[x % 24])
        x //= 24
    return "".join(reversed(letters))


def to_latin_letter(n: int) -> str:
    """Converts `1` → `"a"`, `2` → `"b"`, … using a 26-letter cycle (supports >26 as `"aa"`, `"ab"`, …)."""
    if n <= 0:
        return ""
    letters = []
    x = n
    while x > 0:
        x -= 1
        letters.append(chr(ord("a") + (x % 26)))
        x //= 26
    return "".join(reversed(letters))


def to_roman(n: int) -> str:
    """Converts an integer `n` (>= 1) to a Roman numeral."""
    vals = [
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
    out = []
    x = n
    for v, s in vals:
        if x == 0:
            break
        q, x = divmod(x, v)
        out.append(s * q)
    return "".join(out)


## STRING GENERATORS #####################################################################

__STRING_GENERATORS_________________________________________ = ""


def generate_string(length, case_sensitive=False, digits=True):
    """Generates a pseudorandom, uniformly distributed string of the specified length."""
    choices = string.ascii_uppercase
    if case_sensitive:
        choices += string.ascii_lowercase
    if digits:
        choices += string.digits
    return collapse(random.choices(choices, k=length))


## STRING PROCESSORS #####################################################################

__STRING_PROCESSORS_________________________________________ = ""


def extract(s, pattern):
    """Returns all the occurrences of the specified pattern from the specified string."""
    return re.findall(pattern, s)


##############################


def replace(s, pattern, replacement):
    """
    Returns the string constructed by replacing the specified pattern by the specified replacement
    string in the specified string recursively (only if the length is decreasing).
    """
    count = INF
    while len(s) < count:
        count = len(s)
        s = re.sub(pattern, replacement, s)
    return s


def replace_word(s, word, replacement):
    """
    Returns the string constructed by replacing the specified word by the specified replacement
    string in the specified string recursively (only if the length is decreasing).
    """
    return replace(s, "\\b" + word + "\\b", replacement)


##############################


def split(s, delimiter=",", empty_filter=True):
    """
    Returns all the tokens computed by splitting the specified string around the specified delimiter
    (regular expression).
    """
    if empty_filter:
        from nutil.struct.util import remove_empty

        return remove_empty(re.split(delimiter, s))
    return re.split(delimiter, s)


##############################


def trim(s, replace_space=True, replace_special=True):
    """
    Returns the string constructed by stripping the specified string (and replacing recursively the
    adjacent spaces if `replace_space` is `True` and/or special characters if `replace_special` is
    `True` to a single space).
    """
    if replace_special:
        s = replace(s, "\b|\f|\r\n|\r|\n|\t", " ")
    if replace_space:
        s = replace(s, " +", " ")
    return s.strip()


##############################


def wrap(content, left, right=None):
    """Returns the wrapped representative string of the specified content."""
    if is_null(left):
        return content
    elif is_null(right):
        right = left
    if is_collection(content):
        from nutil.struct.util import apply

        return apply(content, wrap, left, right=right)
    return collapse(left, content, right)


def quote(content):
    """Returns the single-quoted representative string of the specified content."""
    return wrap(content, "'")


def dquote(content):
    """Returns the double-quoted representative string of the specified content."""
    return wrap(content, '"')


def par(content):
    """Returns the parenthesized representative string of the specified content."""
    return wrap(content, "(", ")")


def sbra(content):
    """Returns the bracketized representative string of the specified content."""
    return wrap(content, "[", "]")  # square brackets


def cbra(content):
    """Returns the braced representative string of the specified content."""
    return wrap(content, "{", "}")  # curly brackets
