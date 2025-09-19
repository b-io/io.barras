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

from nutil.common import *
from nutil.io.file import DEFAULT_ENCODING

####################################################################################################
# BYTES CONVERTERS
####################################################################################################

__BYTES_CONVERTERS________________________________ = ""

def to_bytes(x, encoding: str = DEFAULT_ENCODING, errors: str = "strict"):
    """
    Converts the specified object to bytes.

    - Returns the project null sentinel (...) unchanged if x is null.
    - Returns bytes unchanged for bytes/bytearray/memoryview.
    - Encodes strings with the specified encoding.
    - Converts numbers to their string representation and encodes that.
    - Recursively applies to collections.
    - Uses `__bytes__` if the object defines it.
    - Falls back to encoding str(x).

    Parameters
    ----------
    x : Any
        The specified object to convert.
    encoding : str, optional
        The text encoding used for strings (default: 'utf-8').
    errors : str, optional
        The error handling scheme (default: 'strict').

    Returns
    -------
    bytes | Any
        A bytes object, or the project null sentinel (...) if x is null.
    """
    # Handle the project-level null sentinel
    if is_null(x):
        return None

    # Fast path for already byte-like objects
    if isinstance(x, (bytes, bytearray, memoryview)):
        return bytes(x)

    # Strings must be encoded explicitly
    if is_string(x):
        return x.encode(encoding, errors)

    # Avoid the surprising behavior where bytes(int) yields zero-filled bytes
    if is_number(x):
        return str(x).encode(encoding, errors)

    # Numpy / array-like: try dtype conversion, otherwise map recursively
    if is_collection(x):
        # If it is a NumPy-like array, try to cast to a byte-capable dtype
        if hasattr(x, "astype"):
            try:
                # Prefer your constant if it represents a byte-string or uint8
                return x.astype(BYTES_ELEMENT_TYPE)
            except Exception:
                # Fall back to elementwise conversion
                return apply(x, lambda e: to_bytes(e, encoding, errors))
        # Generic Python collections (list/tuple/set/dict/Series...)
        return apply(x, lambda e: to_bytes(e, encoding, errors))

    # Use custom `__bytes__` if available
    to_b = getattr(x, "__bytes__", None)
    if callable(to_b):
        try:
            return bytes(x)
        except Exception:
            pass  # Fall through to string fallback

    # Last resort: encode the string representation
    return str(x).encode(encoding, errors)
