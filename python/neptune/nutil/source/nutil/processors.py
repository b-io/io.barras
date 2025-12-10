#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide processor utilities.
########################################################################################################################

import logging
from concurrent.futures import ThreadPoolExecutor

from multiprocess.pool import Pool

from nutil.common import *
from nutil.scalar.number import ceil
from nutil.scalar.string import quote
from nutil.struct.util import apply

## PROCESSORS ############################################################################

__PROCESSORS________________________________________________ = ""

### THREAD #################################################

__THREAD_PROCESSORS_________________________________________ = ""


def multithread(s, f, *args, asynchronous=False, max_workers=CORE_COUNT, timeout=None, **kwargs):
    return multithread_map(
        s,
        lambda x: apply(x, f, *args, **kwargs),
        asynchronous=asynchronous,
        max_workers=max_workers,
        timeout=timeout,
    )


def multithread_map(s, f, asynchronous=False, max_workers=CORE_COUNT, timeout=None):
    if is_empty(s) or is_element(s):
        return []
    max_workers = min(max_workers, len(s))
    logging.trace(
        "Apply the function",
        quote(f.__name__),
        "to the collection of size",
        len(s),
        "(multithreading)",
    )
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        # Submit the tasks and collect the results
        results = executor.map(f, s, timeout=timeout)
        if asynchronous:
            return results
        return to_list(results)


def multiprocess(
    s,
    f,
    *args,
    asynchronous=False,
    chunk_size=None,
    max_workers=CORE_COUNT,
    timeout=None,
    callback=None,
    error_callback=None,
    **kwargs,
):
    return multiprocess_map(
        s,
        lambda x: apply(x, f, *args, **kwargs),
        asynchronous=asynchronous,
        chunk_size=chunk_size,
        max_workers=max_workers,
        timeout=timeout,
        callback=callback,
        error_callback=error_callback,
    )


def multiprocess_map(
    s,
    f,
    asynchronous=False,
    chunk_size=None,
    max_workers=CORE_COUNT,
    timeout=None,
    callback=None,
    error_callback=None,
):
    if is_empty(s) or is_element(s):
        return []
    max_workers = min(max_workers, len(s))
    if is_null(chunk_size):
        chunk_size = ceil(len(s) / max_workers)
    logging.trace(
        "Apply the function",
        quote(f.__name__),
        "to the collection of size",
        len(s),
        "(multiprocessing)",
    )
    with Pool(processes=max_workers) as executor:
        # Submit the tasks and collect the results
        results = executor.map_async(
            f, s, chunksize=chunk_size, callback=callback, error_callback=error_callback
        )
        if asynchronous:
            return results
        return results.get(timeout=timeout)
