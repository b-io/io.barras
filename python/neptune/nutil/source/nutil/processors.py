#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains utility processors
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

from concurrent.futures import ThreadPoolExecutor

from multiprocess.pool import Pool

## PROCESSORS ############################################################################

__PROCESSORS________________________________________________ = ""


### CONFIG #################################################

__COMMON_CONFIG_PROCESSORS__________________________________ = ""


def escape_property(property):
    return property.replace("%", "%%") if not is_null(property) else None


def merge_config_with_defaults(config: Dict[str, Any], defaults: Dict[str, Any]) -> Dict[str, Any]:
    """
    Merges the specified config with defaults, replacing None with defaults.

    Args:
        config: The base configuration (may contain None values).
        defaults: The submodule defaults.

    Returns:
        A new dictionary with defaults applied.
    """
    merged: Dict[str, Any] = dict(config)
    for key, default in defaults.items():
        value = merged.get(key)
        merged[key] = default if is_null(value) else value
    return merged


### THREAD #################################################

__THREAD_PROCESSORS_________________________________________ = ""


def multithread(c, f, *args, asynchronous=False, max_workers=CORE_COUNT, timeout=None, **kwargs):
    return multithread_map(
        c,
        lambda x: apply(x, f, *args, **kwargs),
        asynchronous=asynchronous,
        max_workers=max_workers,
        timeout=timeout,
    )


def multithread_map(c, f, asynchronous=False, max_workers=CORE_COUNT, timeout=None):
    if is_empty(c) or not is_collection(c):
        return []
    max_workers = min(max_workers, len(c))
    trace(
        "Apply the function",
        quote(f.__name__),
        "to the collection of size",
        len(c),
        "(multithreading)",
    )
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        # Submit the tasks and collect the results
        results = executor.map(f, c, timeout=timeout)
        if asynchronous:
            return results
        return to_list(results)


def multiprocess(
    c,
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
        c,
        lambda x: apply(x, f, *args, **kwargs),
        asynchronous=asynchronous,
        chunk_size=chunk_size,
        max_workers=max_workers,
        timeout=timeout,
        callback=callback,
        error_callback=error_callback,
    )


def multiprocess_map(
    c,
    f,
    asynchronous=False,
    chunk_size=None,
    max_workers=CORE_COUNT,
    timeout=None,
    callback=None,
    error_callback=None,
):
    if is_empty(c) or not is_collection(c):
        return []
    max_workers = min(max_workers, len(c))
    if is_null(chunk_size):
        chunk_size = ceil(len(c) / max_workers)
    trace(
        "Apply the function",
        quote(f.__name__),
        "to the collection of size",
        len(c),
        "(multiprocessing)",
    )
    with Pool(processes=max_workers) as executor:
        # Submit the tasks and collect the results
        results = executor.map_async(
            f, c, chunksize=chunk_size, callback=callback, error_callback=error_callback
        )
        if asynchronous:
            return results
        return results.get(timeout=timeout)
