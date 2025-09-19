#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains common utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2025 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

from nutil.scalar import *
from nutil.scalar.number import INF

####################################################################################################
# STRING CONSTANTS
####################################################################################################

__STRING_CONSTANTS________________________________ = ""

NEWLINE = "\n"

BULLET = "•"
COLON = ":"
SEMICOLON = ";"


####################################################################################################
# STRING CONVERTERS
####################################################################################################

__STRING_CONVERTERS_______________________________ = ""


def to_string(x, delimiter=","):
    if is_null(x):
        return None
    elif is_collection(x):
        if hasattr(x, "astype"):
            return x.astype(STRING_ELEMENT_TYPE)
        return collapse(x, delimiter=delimiter)
    return str(x)


####################################################################################################
# STRING GENERATORS
####################################################################################################

__STRING_GENERATORS_______________________________ = ""


def generate_string(length, case_sensitive=False, digits=True):
    """Generates a pseudorandom, uniformly distributed string of the specified length."""
    choices = string.ascii_uppercase
    if case_sensitive:
        choices += string.ascii_lowercase
    if digits:
        choices += string.digits
    return collapse(random.choices(choices, k=length))


####################################################################################################
# STRING PROCESSORS
####################################################################################################

__STRING_PROCESSORS_______________________________ = ""


def extract(s, pattern):
    """Returns all the occurrences of the specified pattern from the specified string."""
    return re.findall(pattern, s)


#########################


def replace(s, pattern, replacement):
    """Returns the string constructed by replacing the specified pattern by the specified
    replacement string in the specified string recursively (only if the length is decreasing)."""
    count = INF
    while len(s) < count:
        count = len(s)
        s = re.sub(pattern, replacement, s)
    return s


def replace_word(s, word, replacement):
    """Returns the string constructed by replacing the specified word by the specified replacement
    string in the specified string recursively (only if the length is decreasing)."""
    return replace(s, "\\b" + word + "\\b", replacement)


#########################


def split(s, delimiter=",", empty_filter=True):
    """Returns all the tokens computed by splitting the specified string around the specified
    delimiter (regular expression)."""
    if empty_filter:
        return remove_empty(re.split(delimiter, s))
    return re.split(delimiter, s)


#########################


def trim(s, replace_space=True, replace_special=True):
    """Returns the string constructed by stripping the specified string (and replacing recursively
    the adjacent spaces if replace_space is True and/or special characters if replace_special is
    True to a single space)."""
    if replace_special:
        s = replace(s, "\b|\f|\r\n|\r|\n|\t", " ")
    if replace_space:
        s = replace(s, " +", " ")
    return s.strip()


#########################


def wrap(content, left, right=None):
    """Returns the wrapped representative string of the specified content."""
    if is_null(left):
        return content
    elif is_null(right):
        right = left
    if is_collection(content):
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
