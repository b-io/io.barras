#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide a reusable, sectioned, on-disk JSON cache with atomic writes via `write_json`, optional periodic persistence,
#   and write policies controlled by `CachePolicy`. The keys are strings; the values must be JSON-serializable.
#
# Terminology
#   • A `section` is a top-level JSON key that groups related entries (e.g., `"en"`, `"fr"`, `"de_def"`).
#   • A `key` is the domain identifier inside a section (e.g., a lemma string).
#   • A `value` is the JSON-serializable payload stored for a `key` (e.g., translations or a definition).
#
# Behavior
#   • The namespacing by sections avoids the collisions for the same `key` across heterogeneous datasets.
#   • The API selects the entries by `(section, key)`; the missing entries return `None`.
#   • The negative caching is a caller-level choice: empty lists/strings may be stored intentionally.
#   • The persistence is atomic (via `write_json`); the failures to `fsync` are treated as non-fatal by that helper.
########################################################################################################################

from __future__ import annotations

import json
import logging
import re
from dataclasses import dataclass, field
from pathlib import Path
from typing import Pattern

from typing_extensions import TypeAlias

from nutil.common import *
from nutil.io.caching.common import *
from nutil.io.file import write_json

__SECTIONED_CACHE_CLASSES_________________________________________________________________ = ""


V = TypeVar("V")

Section: TypeAlias = str
Sections: TypeAlias = Tuple[Section, ...]

Bucket: TypeAlias = Dict[str, V]
Store: TypeAlias = Dict[Section, Bucket]


@dataclass
class SectionedCache(Generic[V]):
    """
    A sectioned on-disk JSON cache, where a section is a top-level JSON key. Each section maps the `str` keys to the `V`
    values (must be JSON-serializable).

    Notes:
        • The `policy` is fixed for the lifetime of the instance. If the file has the top-level `POLICY_KEY` string,
          it overrides the constructor default during `load(…)`. The methods do not accept per-call policy overrides.

    Args:
        path: The cache file path.
        sections: The exact set of section names to maintain (the declared contract).
        policy: The write policy (`CachePolicy`).
        persistence_frequency: The periodic persistence cadence (in rows).
        coerce_value: An optional converter applied to the raw JSON values during `load(…)`.

    Raises:
        OSError: When reading the existing file fails at the OS level.
        ValueError: When the cache file exists but the JSON is malformed, empty, the top level is not an object,
                    or a section value is not an object.
    """

    POLICY_KEY: ClassVar[str] = "policy"

    path: Path
    sections: Optional[Sections] = None

    policy: CachePolicy = field(default_factory=lambda: CachePolicy.WRITE_MISS_ONLY)
    persistence_frequency: int = 0
    coerce_value: Optional[Callable[[Any], V]] = None

    _store: Store = field(default_factory=dict, init=False, repr=False)
    _since_save: int = field(default=0, init=False, repr=False)

    ### QUERIES ############################################

    def get_sections(self) -> Sections:
        """Returns the declared section names (the contract order)."""
        return self.sections or tuple(self._store.keys())

    def resolve_sections(self, sections: Optional[Sections] = None) -> Sections:
        """Returns the `sections` if provided; otherwise the declared section names."""
        return sections if not is_null(sections) else self.get_sections()

    def has(self, section: Section, key: str) -> bool:
        """Reports whether the `section/key` exists (including the negative-cached empties)."""
        bucket = self._store.get(section)
        return not is_null(bucket) and key in bucket

    def get(self, section: Section, key: str) -> Optional[V]:
        """Selects the cached value for the `section/key`, returning `None` on a cache miss."""
        bucket = self._store.get(section)
        if is_null(bucket):
            return None
        return bucket.get(key)

    def get_list(self, section: Section, key: str, *, allow_scalar: bool = False) -> List[Any]:
        """
        Returns the value as a list copy when stored as a `list`; otherwise:
        • `[]` if the key is missing or the stored value is `None`;
        • `[value]` when `allow_scalar=True` and a non-list scalar is stored (useful for the schema drift).
        """
        v = self.get(section, key)
        if is_null(v):
            return []
        if isinstance(v, list):
            return list(v)  # make the shallow copy to avoid the accidental in-place mutation
        return [v] if allow_scalar else []

    def map(self, section: Section) -> Bucket:
        """
        Returns the mutable map for the `section`, creating it if missing.

        Raises:
            KeyError: When the `section` is not part of the declared `sections`.
        """
        return self._require_section(section)

    def counts(self, sections: Optional[Sections] = None) -> Dict[Section, int]:
        """Returns the entry counts per section for the selected sections."""
        out: Dict[Section, int] = {}
        for sec in self.resolve_sections(sections):
            out[sec] = len(self.map(sec))
        return out

    def items(
        self,
        section: Section,
        *,
        prefix: Optional[str] = None,
        pattern: Optional[Union[str, Pattern[str]]] = None,
    ) -> List[Tuple[str, V]]:
        """
        Returns the `(key, value)` pairs in the `section`, filtered by the `prefix` and/or the regex `pattern`.
        """
        bucket = self.map(section)
        prefix = stringify(prefix)
        if isinstance(pattern, str) and pattern:
            pattern = re.compile(pattern, flags=re.I)

        out: List[Tuple[str, V]] = []
        for k, v in bucket.items():
            if prefix and not k.startswith(prefix):
                continue
            if pattern and not pattern.search(k):
                continue
            out.append((k, v))
        return out

    ### EDITIONS ###########################################

    def set(self, section: Section, key: str, value: V) -> bool:
        """
        Sets the `section/key` to the `value` in memory while respecting the instance write policy.

        Returns:
            `True` if the in-memory value changes, otherwise `False`.

        Raises:
            KeyError: When the `section` is not part of the declared `sections`.
        """
        if self.policy == CachePolicy.READ_ONLY:
            return False

        bucket = self._require_section(section)
        if self.policy == CachePolicy.WRITE_MISS_ONLY and key in bucket:
            return False

        changed = (key not in bucket) or (bucket[key] != value)
        if not changed and self.policy != CachePolicy.OVERWRITE:
            return False

        bucket[key] = value
        self._since_save += 1
        if self.persistence_frequency > 0 and self._since_save >= self.persistence_frequency:
            self.save()
        return True

    def set_list(self, section: Section, key: str, values: Iterable[Any]) -> bool:
        """Sets the value as a list (shallow-copies the iterable; no type coercion)."""
        return self.set(section, key, list(values))

    def rename_key(self, section: Section, old: str, new: str) -> bool:
        """
        Renames a key within the `section`. If the `new` already exists, keeps the `new` and removes the `old`.
        Returns `True` if changed.
        """
        if not old or not new or old == new:
            return False
        m = self.map(section)
        if old not in m:
            return False
        if new in m:
            # Do nothing to write; just drop the old key if the policy allows deletions
            return self.delete(section, old)
        # Create the new key via `set` (respect the policy), then delete the old one on success
        if self.set(section, new, m[old]):
            return self.delete(section, old) or True
        return False

    def merge_lists(self, section: Section, target: str, sources: Iterable[str]) -> int:
        """
        Merges the list-valued entries from the `sources` into the `target` within the `section`.

        Behavior:
            • Creates the `target` as an empty list if absent (via `set` so the autosave/policy are respected).
            • Appends the unique items preserving the order (non-destructive: builds a new list and calls `set`).
            • Deletes the merged source keys after a successful merge.
        """
        if not target:
            return 0
        m = self.map(section)

        # Ensure a list-valued target exists (respect the policy)
        if target not in m:
            if not self.set(section, target, []):  # may be blocked by READ_ONLY
                return 0

        target_list = m.get(target)
        if not isinstance(target_list, list):
            logging.warning("Skip merge in '%s': target '%s' is not a list", section, target)
            return 0

        merged = 0
        # Work on a new list to trigger the `set` equality detection (avoid the in-place mutation)
        new_list: List[Any] = target_list.copy()

        for source in sources or ():
            key = stringify(source)
            if key and key in m and key != target:
                src_val = m[key]
                if isinstance(src_val, list):
                    for item in src_val:
                        if item not in new_list:
                            new_list.append(item)
                    # Remove the source key (even if there are no new items; mirrors the CLI semantics)
                    self.delete(section, key)
                    merged += 1
                else:
                    logging.warning("Skip source '%s' in '%s': value is not a list", key, section)

        # Write back only if the content changed
        if new_list != target_list:
            self.set(section, target, new_list)
        return merged

    ### DELETIONS ##########################################

    def delete(self, section: Section, key: str) -> bool:
        """
        Deletes a `section/key` entry when present.

        Returns:
            `True` if the entry was removed, otherwise `False`.

        Raises:
            KeyError: When the `section` is not part of the declared `sections`.
        """
        if self.policy == CachePolicy.READ_ONLY:
            return False
        bucket = self._require_section(section)
        if key in bucket:
            del bucket[key]
            self._since_save += 1
            if self.persistence_frequency > 0 and self._since_save >= self.persistence_frequency:
                self.save()
            return True
        return False

    def delete_keys(self, section: Section, keys: Iterable[str]) -> int:
        """Deletes the exact `keys` from the `section` and returns the number removed."""
        removed = 0
        for key in keys or ():
            k = stringify(key)
            if k and self.delete(section, k):
                removed += 1
        return removed

    def delete_prefixes(self, section: Section, prefixes: Iterable[str]) -> int:
        """Deletes the keys that start with any of the `prefixes` in the `section` and returns the number removed."""
        prefs = [p for p in (s.strip() for s in (prefixes or ())) if p]
        if not prefs:
            return 0
        m = self.map(section)
        to_drop = [k for k in list(m.keys()) if any(k.startswith(p) for p in prefs)]
        removed = 0
        for k in to_drop:
            removed += 1 if self.delete(section, k) else 0
        return removed

    def delete_empty(
        self,
        section: Section,
        *,
        delete_list_empty: bool = True,
        delete_str_empty: bool = True,
    ) -> int:
        """
        Deletes the keys with empty values from the `section` and returns the number removed.

        Behavior:
            • When `delete_list_empty` is `True`, removes the entries with `[]`.
            • When `delete_str_empty`  is `True`, removes the entries with `""`.
        """
        m = self.map(section)
        to_drop: List[str] = []
        for k, v in m.items():
            if delete_list_empty and v == []:
                to_drop.append(k)
            elif delete_str_empty and isinstance(v, str) and v == "":
                to_drop.append(k)
        removed = 0
        for k in to_drop:
            removed += 1 if self.delete(section, k) else 0
        return removed

    def clear_section(self, section: str) -> None:
        """
        Clears all the entries in a declared `section`.

        Raises:
            KeyError: When the `section` is not part of the declared `sections`.
        """
        if self.policy == CachePolicy.READ_ONLY:
            return False
        bucket = self._require_section(section)
        bucket.clear()
        self._since_save += 1
        if self.persistence_frequency > 0 and self._since_save >= self.persistence_frequency:
            self.save()

    ### I/O ################################################

    @classmethod
    def load(
        cls,
        path: Path,
        sections: Optional[Sections] = None,
        *,
        policy: CachePolicy = CachePolicy.WRITE_MISS_ONLY,
        persistence_frequency: int = 0,
        coerce_value: Optional[Callable[[Any], V]] = None,
    ) -> "SectionedCache[V]":
        """
        Loads the cache from the `path`, returning a ready-to-use instance.

        Strictness:
            • If the `path` does not exist, a new empty cache is created and saved.
            • If the `path` exists but is empty, not JSON, the top level is not an object, or a section value is not an object,
              a `ValueError` is raised (no implicit fresh cache is created).
        """
        # Initialize the in-memory store for the file contents
        store: Dict[str, Any] = {}

        if path.exists():
            # Reject the empty file to prevent the silent creation of a fresh cache
            if path.stat().st_size == 0:
                raise ValueError(f"Cache file exists but is empty: '{path}'")

            # Read the file text with the default encoding
            try:
                text = path.read_text(encoding=DEFAULT_ENCODING)
            except OSError:
                raise

            # Parse the on-disk JSON and validate the top level
            try:
                data = json.loads(text)
            except Exception as e:
                raise ValueError(f"Malformed cache JSON at '{path}': {e}") from e
            if not isinstance(data, dict):
                raise ValueError(f"Invalid cache JSON at '{path}': top level must be an object")

            # Keep the parsed store for the subsequent coercion and validation
            store = data
        else:
            # Allow the first run when the cache file is missing
            logging.debug("Cache file does not exist; will create a new one at '%s'", path)

        # Derive the declared sections from the user input and the file (preserve the order, drop the duplicates)
        file_sections = [k for k in store.keys() if k != cls.POLICY_KEY]
        declared_sections: Sections = tuple(dict.fromkeys([*(sections or ()), *file_sections]))

        # Coerce the policy from the file when present (accept the value or the name; fall back to the default)
        detected_policy: Optional[CachePolicy] = None
        detected_policy_value_or_name = store.get(cls.POLICY_KEY)
        if isinstance(detected_policy_value_or_name, str):
            try:
                detected_policy = CachePolicy.from_value_or_name(detected_policy_value_or_name)
            except ValueError:
                logging.warning(
                    "Unknown policy '%s' in '%s'; using default '%s'",
                    detected_policy_value_or_name,
                    path,
                    policy.value,
                )

        # Build the cache instance with the detected policy and the declared sections
        cache = cls(
            path=path,
            sections=declared_sections,
            policy=detected_policy or policy,
            persistence_frequency=persistence_frequency,
            coerce_value=coerce_value,
        )

        # Validate and load the internal store from the parsed JSON
        cache._store = cache._coerce_and_validate(store)

        # Eagerly create the file on the first run to establish the sections and the policy
        if not path.exists():
            cache.save()

        return cache

    def save(self, *, compact: bool = False, backup: bool = False, backup_dir: Optional[Path] = None) -> None:
        """
        Saves the cache atomically to the disk (includes the active `self.POLICY_KEY` at the top level).

        Args:
            compact: Writes the compact JSON when `True`.
            backup: Creates the timestamped backup of the previous file when `True`.
            backup_dir: Stores the backups in this directory (defaults to next to the cache file).
        """
        logging.debug("Saving cache to '%s'", self.path)
        payload: Dict[str, Any] = {self.POLICY_KEY: self.policy.value}
        payload.update({section: dict(bucket) for section, bucket in self._store.items()})
        write_json(self.path, payload, compact=compact, backup=backup, backup_dir=backup_dir)
        self._since_save = 0

    ### INTERNALS ##########################################

    def _coerce_and_validate(self, store: Dict[str, Any]) -> Store:
        """Coerces and validates the parsed JSON object into the internal store structure."""
        out: Store = {name: {} for name in (self.sections or ())}
        if not store:
            return out

        # Validate and preserve the sections (unknown sections are allowed if they are objects)
        for section, entries in store.items():
            if section == self.POLICY_KEY:
                continue
            if not isinstance(entries, dict):
                raise ValueError(
                    f"Invalid section '{section}' in '{self.path}': expected an object map, got {type(entries).__name__}"
                )
            bucket: Bucket = {}
            for k, v in entries.items():
                try:
                    key = str(k)
                except Exception:
                    raise ValueError(f"Non-string-coercible key in section '{section}': {k!r}")
                bucket[key] = self.coerce_value(v) if self.coerce_value else v
            out[section] = bucket

        # Ensure all the declared sections exist
        for name in self.sections or ():
            out.setdefault(name, {})
        return out

    def _require_section(self, section: Section) -> Bucket:
        """Selects the mutable bucket for the `section`, creating it if missing, and validating the contract."""
        if self.sections and (section not in self.sections):
            raise KeyError(f"'{section}' is not a valid name for '{self.__class__.__name__}.sections'")
        if section not in self._store:
            self._store[section] = {}
        return self._store[section]
