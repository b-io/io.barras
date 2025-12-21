#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Maintain a generic sectioned JSON cache via `SectionedCache`. The typical operations:
#     • List the counts per section.
#     • Show the entries (regex/prefix filters).
#     • Delete the keys (exact) or by the prefix.
#     • Delete the empty values (`[]` / `""`).
#     • Clear a section.
#     • Rename a key within a section.
#     • Merge the list-valued keys (append unique, preserve order).
########################################################################################################################

from __future__ import annotations

import argparse
import json
import logging
import re
from pathlib import Path
from types import SimpleNamespace

from nutil.common import *
from nutil.io.caching.common import *
from nutil.io.caching.sectioned_cache import SectionedCache
from nutil.io.file import resolve_path
from nutil.io.logging import configure_logging

__SECTIONED_CACHE_TOOL_RUNNERS____________________________________________________________ = ""


def run(
    cache_path: Path,
    sections: Optional[Iterable[str]] = None,
    *,
    # Accessors
    list_entries: bool = False,
    show_entries: bool = False,
    regex_filter: Optional[str] = None,
    prefix_filter: Optional[str] = None,
    # Deletions
    delete_keys: Optional[List[str]] = None,
    delete_prefixes: Optional[List[str]] = None,
    delete_empty: bool = False,
    only_list_empty: bool = False,
    only_str_empty: bool = False,
    clear: bool = False,
    # Editions
    rename: Optional[Tuple[str, str]] = None,
    merge: Optional[List[str]] = None,
    # Load
    policy: Optional[CachePolicy] = None,
    persistence_frequency: int = 0,
    # Save
    dry_run: bool = False,
    compact: bool = False,
    backup: bool = False,
    backup_dir: Optional[str] = None,
) -> None:
    """
    Runs the unified cache tool with the same defaults.

    Args:
        cache_path: The path to the cache JSON.
        sections: The sections to operate on (declared names).

        list_entries: Tells to list the counts per section.
        show_entries: Tells to print entries (with optional `regex_filter` / `prefix_filter`).
        regex_filter: The regex applied to keys for `--show`.
        prefix_filter: The prefix applied to keys for `--show`.

        delete_keys: The exact keys to delete.
        delete_prefixes: The key prefixes to delete.
        delete_empty: Tells to delete empty values.
        only_list_empty: Tells to delete only `[]` with `--delete-empty`.
        only_str_empty: Tells to delete only `""` with `--delete-empty`.
        clear: Tells to clear selected sections.

        rename: The `(old, new)` key pair to rename (single section required).
        merge: The list `["TARGET", "SRC", …]` to merge (single section required).

        policy: Optional `CachePolicy` to use when loading/creating the cache. When the cache file does not
            yet contain a `policy`, this value is used as the initial policy.
        persistence_frequency: Periodic autosave cadence for in-memory changes (rows/operations).

        dry_run: Tells to skip writing the file; only logs the changes.
        compact: Tells to write compact JSON on save.
        backup: Tells to create a timestamped backup on save.
        backup_dir: The directory where backups are stored.
    """
    run_with_args(
        SimpleNamespace(
            cache=cache_path,
            sections=sections,
            # Accessors
            list=list_entries,
            show=show_entries,
            filter=regex_filter,
            prefix=prefix_filter,
            # Deletions
            delete_keys=delete_keys,
            delete_prefix=delete_prefixes,
            delete_empty=bool(delete_empty),
            only_list_empty=bool(only_list_empty),
            only_str_empty=bool(only_str_empty),
            clear=bool(clear),
            # Editions
            rename=rename,
            merge=merge,
            # Load
            policy=policy,
            persistence_frequency=persistence_frequency,
            # Save
            dry_run=bool(dry_run),
            compact=bool(compact),
            backup=bool(backup),
            backup_dir=backup_dir,
        )
    )


def run_with_args(args: argparse.Namespace) -> None:
    """Runs the selected operations on the sectioned cache."""
    # Load the cache; compute the union of the file-discovered sections and any user-provided `"--sections"`
    cache = SectionedCache.load(
        args.cache,
        tuple(args.sections or ()),
        policy=args.policy or CachePolicy.WRITE_MISS_ONLY,
        persistence_frequency=args.persistence_frequency or 0,
    )

    # Resolve the working section set (all sections when not provided)
    def _get_selected_sections() -> Tuple[str, ...]:
        return tuple(args.sections) if args.sections else cache.get_sections()

    # Inspect entries
    if args.list:
        counts = cache.counts(_get_selected_sections())
        for section, n in counts.items():
            logging.info("'%s' entries: %d", section, n)

    if args.show:
        prefix = stringify(args.prefix) or None
        pattern = re.compile(args.filter, flags=re.I) if args.filter else None
        for section in _get_selected_sections():
            for k, v in cache.items(section, prefix=prefix, pattern=pattern):
                logging.info(
                    "[%s] %r → %s",
                    section,
                    k,
                    json.dumps(v, ensure_ascii=False),
                )

    # Apply mutations
    changed = False
    before_counts = cache.counts(cache.get_sections())

    for section in _get_selected_sections():
        # Delete exact keys
        if args.delete_keys:
            n = cache.delete_keys(section, args.delete_keys)
            changed |= n > 0
            if n > 0:
                logging.info(
                    "Removed %d key%s from '%s' (exact)",
                    n,
                    "" if n == 1 else "s",
                    section,
                )

        # Delete prefixes
        if args.delete_prefix:
            n = cache.delete_prefixes(section, args.delete_prefix)
            changed |= n > 0
            if n > 0:
                logging.info(
                    "Removed %d key%s from '%s' (prefix match)",
                    n,
                    "" if n == 1 else "s",
                    section,
                )

        # Delete empty values
        if args.delete_empty:
            n = cache.delete_empty(
                section,
                delete_list_empty=not args.only_str_empty,
                delete_str_empty=not args.only_list_empty,
            )
            changed |= n > 0
            if n > 0:
                logging.info(
                    "Removed %d empty entr%s from '%s'",
                    n,
                    "y" if n == 1 else "ies",
                    section,
                )

        # Clear sections
        if args.clear:
            before = before_counts.get(section, 0)
            cache.clear_section(section)
            after = 0
            changed |= before != after
            if before:
                logging.info(
                    "Cleared '%s' (%d entr%s)",
                    section,
                    before,
                    "y" if before == 1 else "ies",
                )

        # Rename a key (single section)
        if args.rename:
            old, new = args.rename
            ok = cache.rename_key(section, old, new)
            changed |= ok
            if ok:
                logging.info("Renamed '%s' → '%s' in '%s'", old, new, section)

        # Merge list-valued keys (single section; `TARGET SRC …`)
        if args.merge and len(args.merge) >= 2:
            target, *sources = args.merge
            n = cache.merge_lists(section, target, sources)
            changed |= n > 0
            if n:
                logging.info(
                    "[%s] Merged %d source key%s into '%s'",
                    section,
                    n,
                    "" if n == 1 else "s",
                    target,
                )

    after_counts = cache.counts(cache.get_sections())
    changed = changed or (before_counts != after_counts)

    # Save results
    if changed and not args.dry_run:
        cache.save(
            compact=args.compact,
            backup=args.backup,
            backup_dir=Path(args.backup_dir) if args.backup_dir else None,
        )
        summary = " | ".join(
            f"'{k}' {before_counts.get(k, 0)} → {after_counts.get(k, 0)}"
            for k in sorted(set(before_counts) | set(after_counts))
        )
        logging.info("Done: %s", summary)
    elif changed and args.dry_run:
        logging.info("[dry-run] Would save changes")
    else:
        logging.info("No changes")


### ARGUMENTS ##############################################


def parse_args() -> argparse.Namespace:
    """Parses the CLI flags for the sectioned cache tool."""
    ap: argparse.ArgumentParser = _build_arg_parser()
    args: argparse.Namespace = ap.parse_args()

    # Resolve the path(s)
    args.cache = resolve_path(args.cache, must_exist=False)

    # Validate the mutually exclusive toggles
    if args.only_list_empty and args.only_str_empty:
        ap.error("--only-list-empty and --only-str-empty are mutually exclusive")

    # Validate the single-section operations
    if (args.rename or args.merge) and (not args.sections or len(args.sections) != 1):
        ap.error("--rename/--merge require exactly one --section")

    return args


def _build_arg_parser() -> argparse.ArgumentParser:
    """Builds the CLI argument parser."""
    ap = argparse.ArgumentParser(description="Maintain a generic sectioned JSON cache.")
    # Add the path(s)
    ap.add_argument("--cache", help="Path to the JSON cache.", required=True)
    # Add the section parameters(s)
    ap.add_argument("--sections", help="Section to operate on (repeatable).", action="append", metavar="NAME")
    # Add the query parameters(s)
    ap.add_argument("--list", help="Print the entry counts per section.", action="store_true")
    ap.add_argument("--show", help="Show entries (with optional --filter/--prefix).", action="store_true")
    ap.add_argument("--filter", help="Regex filter for --show.")
    ap.add_argument("--prefix", help="Prefix filter for --show.")
    # Add the edition parameters(s)
    ap.add_argument("--rename", help="Rename a key within a section.", nargs=2, metavar=("OLD", "NEW"))
    ap.add_argument(
        "--merge",
        help="Merge list-valued sources into the target: TARGET SRC …",
        nargs="+",
        metavar="KEY",
    )
    # Add the deletion parameters(s)
    ap.add_argument(
        "--delete-keys",
        help="Delete exact keys (repeatable across sections).",
        nargs="+",
        metavar="KEY",
    )
    ap.add_argument("--delete-prefix", help="Delete keys starting with any prefix.", nargs="+", metavar="PFX")
    ap.add_argument("--delete-empty", help="Delete entries with [] or ''.", action="store_true")
    ap.add_argument("--only-list-empty", help="With --delete-empty, delete [] only.", action="store_true")
    ap.add_argument("--only-str-empty", help="With --delete-empty, delete '' only.", action="store_true")
    ap.add_argument("--clear", help="Clear selected sections.", action="store_true")
    # Add the load parameters(s)
    ap.add_argument(
        "--cache-policy",
        help="Select the cache write policy: 'read_only', 'write_miss_only', or 'overwrite'.",
        choices=[p.value for p in CachePolicy],
        default=CachePolicy.WRITE_MISS_ONLY.value,
    )
    ap.add_argument(
        "--persistence-frequency",
        help="Autosave after this many in-memory changes (0 disables periodic autosave).",
        type=int,
        default=0,
    )
    # Add the save parameter(s)
    ap.add_argument("--dry-run", help="Do not write changes; only log results.", action="store_true")
    ap.add_argument("--compact", help="Write compact JSON on save.", action="store_true")
    ap.add_argument(
        "--backup",
        help="Create a timestamped backup of the previous file on save.",
        action="store_true",
    )
    ap.add_argument("--backup-dir", help="Directory to store backups (defaults to the cache file's directory).")
    return ap


### MAIN ###################################################


def main() -> None:
    """Runs the sectioned cache tool."""
    configure_logging()
    args = parse_args()
    logging.info("Run '%s' with args: %s", Path(__file__).name, args)
    run_with_args(args)


if __name__ == "__main__":
    main()
