#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide scalar utilities for bytes.
########################################################################################################################

from __future__ import annotations

from nutil.common import *

__BYTES_CONVERTERS________________________________________________________________________ = ""


def to_bytes(x: Any, encoding: str = DEFAULT_ENCODING, errors: str = "strict") -> Any:
    """
    Converts `x` to bytes (recursively for collections).

    Dispatch:
        • Returns `None` if `x` is null (per `is_null`).
        • Returns `bytes(x)` for byte-like (`bytes`, `bytearray`, `memoryview`).
        • Converts the numbers via `str(x).encode(...)` (avoids `bytes(int)` zero-fill trap).
        • Encodes `str` via `encoding`/`errors`.
        • For NumPy arrays:
            – If `dtype` is `uint8`, returns `x.tobytes()`.
            – Otherwise maps element-wise via `apply(..., to_bytes)`.
        • For other collections, maps element-wise via `apply`.
        • If `__bytes__` is defined, uses `bytes(x)` (with safe fallback).
        • Otherwise encodes `str(x)`.

    Returns:
        `bytes` for scalars/byte-like, or the collection with elements converted to `bytes`.
        Returns `None` if `x` is null.
    """
    if is_null(x):
        return None
    elif is_byte_like(x):
        return bytes(x)
    elif is_number(x):
        return str(x).encode(encoding, errors)
    elif is_string(x):
        return x.encode(encoding, errors)
    elif is_array(x):
        if x.dtype == np.uint8:
            return x.tobytes()
        from nutil.struct.util import apply

        return apply(x, lambda e: to_bytes(e, encoding, errors))
    elif is_struct(x):
        from nutil.struct.util import apply

        return apply(x, lambda e: to_bytes(e, encoding, errors))
    elif has_callable(x, "__bytes__"):
        try:
            return bytes(x)
        except Exception:
            pass
    return str(x).encode(encoding, errors)
