#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide utility decorators for caching.
########################################################################################################################

from __future__ import annotations

import functools
from typing import OrderedDict

from nutil.common import *
from nutil.decorators import F

__CACHING_DECORATORS______________________________________________________________________ = ""


def hash_cache(maxsize: int = 10) -> Callable[[F], F]:
    """Decorates a function with an LRU cache keyed by `deep_hash(args, kwargs)`.

    The cache:
        • Works with unhashable arguments by hashing their structure via `deep_hash`
        • Stores the results in an `OrderedDict` implementing a simple LRU policy
        • Keeps at most `maxsize` distinct argument structures

    Args:
        maxsize: The maximum number of cached entries.

    Returns:
        A decorator that wraps the function with a hash-based LRU cache.
    """

    def decorator(func: F) -> F:
        cache: OrderedDict[int, Any] = OrderedDict()

        @functools.wraps(func)
        def wrapper(*args: Any, **kwargs: Any) -> Any:
            # Structural key from arguments and keyword arguments
            key = deep_hash((args, kwargs))

            # LRU lookup: move to end when found
            try:
                result = cache.pop(key)
                cache[key] = result
                return result
            except KeyError:
                result = func(*args, **kwargs)
                cache[key] = result
                if len(cache) > maxsize:
                    # Remove the least recently used entry
                    cache.popitem(last=False)
                return result

        return cast(F, wrapper)

    return decorator
